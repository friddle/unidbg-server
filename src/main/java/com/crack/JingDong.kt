@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")
package com.crack

import com.github.unidbg.AndroidEmulator
import com.github.unidbg.Emulator
import com.github.unidbg.Module
import com.github.unidbg.arm.HookStatus
import com.github.unidbg.file.FileResult
import com.github.unidbg.file.IOResolver
import com.github.unidbg.file.NewFileIO
import com.github.unidbg.hook.HookContext
import com.github.unidbg.hook.ReplaceCallback
import com.github.unidbg.linux.AndroidARM32EmulatorChooseTime
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.android.XHookImpl
import com.github.unidbg.linux.android.dvm.*
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.memory.Memory
import com.github.unidbg.pointer.UnidbgPointer
import com.github.unidbg.virtualmodule.android.AndroidModule
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import sun.security.pkcs.PKCS7
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption



class JingDong<T:NewFileIO>: AbstractJni, IOResolver<T> {

    //防止打成jar包的时候找不到文件
    var soPath = "libjdbitmapkit.so"
    var appPath = "jd.apk"
    var packageName="com.jingdong.app.mall"
    var packageNameZH="京东"
    var logger= LoggerFactory.getLogger(JingDong::class.java)

    lateinit var emulator: AndroidEmulator
    lateinit var module: Module
    lateinit var vm: VM
    lateinit var aBitmapkitUtils:DvmClass


    constructor(isDebug:Boolean=false)
    {
        var classPathResource = ClassPathResource(soPath)
        var appPathResource = ClassPathResource(appPath)
        if(Paths.get("./tmp/libjdbitmapkit.so").toFile().exists()&&Paths.get("./tmp/jd.apk").toFile().exists()){
            logger.info("临时目录已经有文件不更新")
        }else{
            try
            {
                val inputStream = classPathResource.inputStream
                Files.copy(inputStream, Paths.get("./tmp/libjdbitmapkit.so"), StandardCopyOption.REPLACE_EXISTING)
                val appinputStream = appPathResource.inputStream
                Files.copy(appinputStream, Paths.get("./tmp/jd.apk"), StandardCopyOption.REPLACE_EXISTING)
            }catch (  e: IOException)
            {
                e.printStackTrace()
            }
        }
        if(isDebug){
            initVmWithTime()
        }else{
            initVm()
        }
    }

    fun initVmWithTime()
    {
        emulator = AndroidARM32EmulatorChooseTime(processName = packageName,rootDir=Paths.get("./tmp/test/").toFile(),backendFactories = arrayListOf())
        val memory=emulator.getMemory().apply{this.setLibraryResolver(AndroidResolver(23))} // 设置系统类库解析
        vm = emulator.createDalvikVM(File("./tmp/${appPath}")) // 创建Android虚拟机
        AndroidModule(emulator, vm).register(memory)
        vm.setJni(this)
        vm.setVerbose(true) // 设置是否打印Jni调用细节
        val dm: DalvikModule = vm.loadLibrary(File("./tmp/${soPath}"), false) // 加载libcms.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator) // 手动执行JNI_OnLoad函数
        module = dm.module // 加载好的libcms.so对应为一个模块
        aBitmapkitUtils = vm.resolveClass("com/jingdong/common/utils/BitmapkitUtils");
    }

    fun initVm()
    {
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName(packageName).build()
        val memory: Memory = emulator.getMemory() // 模拟器的内存操作接口
        memory.setLibraryResolver(AndroidResolver(23)) // 设置系统类库解析
        vm = emulator.createDalvikVM(File("./tmp/${appPath}")) // 创建Android虚拟机
        AndroidModule(emulator, vm).register(memory)
        vm.setJni(this)
        vm.setVerbose(false) // 设置是否打印Jni调用细节
        val dm: DalvikModule = vm.loadLibrary(File("./tmp/${soPath}"), false) // 加载libcms.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator) // 手动执行JNI_OnLoad函数
        module = dm.module // 加载好的libcms.so对应为一个模块
        aBitmapkitUtils = vm.resolveClass("com/jingdong/common/utils/BitmapkitUtils");
    }


    override fun getStaticObjectField(vm: BaseVM, dvmClass: DvmClass?, signature: String?): DvmObject<*>? {
        logger.debug("----static--object--field:${signature}--")
        when (signature) {
            "com/jingdong/common/utils/BitmapkitUtils->a:Landroid/app/Application;" -> return vm.resolveClass(
                "android/app/Application",
                vm.resolveClass("android/content/ContextWrapper", vm.resolveClass("android/content/Context"))
            ).newObject(null)
        }
        return super.getStaticObjectField(vm, dvmClass, signature)
    }

    override fun newObject(vm: BaseVM, dvmClass: DvmClass, signature: String, vaList: VarArg): DvmObject<*> {
        return when(signature)
        {
            "sun/security/pkcs/PKCS7-><init>([B)V"->generatePkcs7(dvmClass,vaList)
            else-> super.newObject(vm, dvmClass, signature, vaList)
        }
    }


    fun generatePkcs7(dvmClass:DvmClass,varArg: VarArg):DvmObject<sun.security.pkcs.PKCS7?>{
        val array: com.github.unidbg.linux.android.dvm.array.ByteArray = varArg.getObject<com.github.unidbg.linux.android.dvm.array.ByteArray>(0)
        try{
            val pkcs7= PKCS7(array.value)
            return Pkcs7Object(dvmClass,pkcs7)
        }catch(e:Exception){
            logger.error("生成Pkcs7失败:${array.value}",e)
            return Pkcs7Object(dvmClass,PKCS7(array.value))
        }
    }

    fun crack(functionId:String,body:String,uuid:String,platform:String,version:String):String
    {
        val strRc = aBitmapkitUtils.callStaticJniMethodObject<DvmObject<String>>(
            emulator,
            "getSignFromJni()(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
            vm.addLocalObject(null),
            vm.addLocalObject(StringObject(vm, functionId)),
            vm.addLocalObject(StringObject(vm, body)),
            vm.addLocalObject(StringObject(vm, uuid)),
            vm.addLocalObject(StringObject(vm, platform)),
            vm.addLocalObject(StringObject(vm, version))
        )
        return strRc.value?:""
    }

    override fun resolve(emulator: Emulator<T>?, pathname: String?, oflags: Int): FileResult<T> {
        TODO("Not yet implemented")
    }

    fun destroy(){
        emulator.close();
    }

}

fun main() {
    val jingdong=JingDong<ByteArrayFileIO>()
    val functionId="asynInteface"
    val body="{\"intefaceType\":\"asynIntefaceType\",\"skuId\":\"100008667315\"}"
    val uuid="99001184062989-f460e22c02fa"
    val platform="android"
    val version="10.1.4"
    println(jingdong.crack(functionId,body,uuid,platform,version))

}