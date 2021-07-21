package com.crack;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.crack.utils.tools.encodeUrl;

/*
xPreAuthencode目标native
RegisterNative(com/mfw/tnative/AuthorizeHelper, xPreAuthencode(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, RX@0x4002e301[libmfw.so]0x2e301)
目标方法地址为0x2e301

y https://mapi.mafengwo.cn/user/mine/get_info/v2
params:===> {"o_lat":"40.008224","device_type":"android","dev_ver":"D1907.0","oauth_version":"1.0","oauth_signature_method":"HMAC-SHA1","is_special":"1","screen_height":"1794","open_udid":"40:4E:36:B1:47:3E","app_version_code":"734","x_auth_mode":"client_auth","oauth_token":"0_0969044fd4edf59957f4a39bce9200c6","sys_ver":"8.1.0","o_lng":"116.350234","brand":"google","app_code":"com.mfw.roadbook","screen_scale":"2.88","screen_width":"1080","time_offset":"480","device_id":"40:4E:36:B1:47:3E","oauth_consumer_key":"5","oauth_timestamp":"1626418363","oauth_nonce":"ba9f536e-d7cb-47fc-b5d7-d8104e89b4cb","user_id":"30044554","mfwsdk_ver":"20140507","app_ver":"9.3.7","has_notch":"0","hardware_model":"Pixel","channel_id":"MFW"}
getNormalizedParams:===> app_code=com.mfw.roadbook&app_ver=9.3.7&app_version_code=734&brand=google&channel_id=MFW&dev_ver=D1907.0&device_id=40%3A4E%3A36%3AB1%3A47%3A3E&device_type=android&hardware_model=Pixel&has_notch=0&is_special=1&mfwsdk_ver=20140507&o_lat=40.008224&o_lng=116.350234&oauth_consumer_key=5&oauth_nonce=ba9f536e-d7cb-47fc-b5d7-d8104e89b4cb&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1626418363&oauth_token=0_0969044fd4edf59957f4a39bce9200c6&oauth_version=1.0&open_udid=40%3A4E%3A36%3AB1%3A47%3A3E&screen_height=1794&screen_scale=2.88&screen_width=1080&sys_ver=8.1.0&time_offset=480&user_id=30044554&x_auth_mode=client_auth
encodeUrl:===> app_code%3Dcom.mfw.roadbook%26app_ver%3D9.3.7%26app_version_code%3D734%26brand%3Dgoogle%26channel_id%3DMFW%26dev_ver%3DD1907.0%26device_id%3D40%253A4E%253A36%253AB1%253A47%253A3E%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26is_special%3D1%26mfwsdk_ver%3D20140507%26o_lat%3D40.008224%26o_lng%3D116.350234%26oauth_consumer_key%3D5%26oauth_nonce%3Dba9f536e-d7cb-47fc-b5d7-d8104e89b4cb%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626418363%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3D40%253A4E%253A36%253AB1%253A47%253A3E%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26user_id%3D30044554%26x_auth_mode%3Dclient_auth
fullEncodeUrl:===> GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fuser%2Fmine%2Fget_info%2Fv2&app_code%3Dcom.mfw.roadbook%26app_ver%3D9.3.7%26app_version_code%3D734%26brand%3Dgoogle%26channel_id%3DMFW%26dev_ver%3DD1907.0%26device_id%3D40%253A4E%253A36%253AB1%253A47%253A3E%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26is_special%3D1%26mfwsdk_ver%3D20140507%26o_lat%3D40.008224%26o_lng%3D116.350234%26oauth_consumer_key%3D5%26oauth_nonce%3Dba9f536e-d7cb-47fc-b5d7-d8104e89b4cb%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626418363%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3D40%253A4E%253A36%253AB1%253A47%253A3E%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26user_id%3D30044554%26x_auth_mode%3Dclient_auth
ghostSigh:===> 91c0ad24affec4bf69e9a553271db489207efa69
* **/
public class MaFengWo extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    public static Map<String,String> mfwObj(){
        Map<String,String> obj= new HashMap<>();
        return obj;
    }

    public MaFengWo() {
        emulator = new AndroidARMEmulator("com.mfw.roadbook"); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));// 设置系统类库解析
        vm = emulator.createDalvikVM(new File("./mafengwo.apk")); // 创建Android虚拟机
        vm.setJni(this);
        //vm.setVerbose(true);// 设置是否打印Jni调用细节
        // 自行修改文件路径,loadLibrary是java加载so的方法
        DalvikModule dm = vm.loadLibrary(new File("./libmfw.so"), true); // 加载libmfw.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator);// 手动执行JNI_OnLoad函数
        module = dm.getModule();// 加载好的libmfw.so对应为一个模块
    }

    public Map<String, String>  xPreAuthencode(String allParams){
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());//第一个参数是env
        list.add(0); //第二个参数，实例方法是object,静态方法jclazz，直接填0，一般用不到
        Object custom =null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom); //context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm,allParams)));
        list.add(vm.addLocalObject(new StringObject(vm,"com.mfw.roadbook")));

        Number number = module.callFunction(emulator,0x2e235,list.toArray())[0];
        String zzzghostsigh = vm.getObject(number.intValue()).getValue().toString();
        String lasturl = allParams+encodeUrl("&zzzghostsigh="+zzzghostsigh);
        String oauth_signature=xAuthencode(lasturl);
        Map<String,String> result=mfwObj();
        result.put("zzzghostsigh",zzzghostsigh);
        result.put("oauth_signature" ,oauth_signature);
        return result;

    }
   public String xAuthencode(String allParams){
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());//第一个参数是env
        list.add(0); //第二个参数，实例方法是object,静态方法jclazz，直接填0，一般用不到
        Object custom =null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom); //context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm,allParams)));
        list.add(vm.addLocalObject(new StringObject(vm,"")));
        list.add(vm.addLocalObject(new StringObject(vm,"com.mfw.roadbook")));
        list.add(vm.addLocalObject(null));
        Number number = module.callFunction(emulator,0x2e2fd,list.toArray())[0];
        String result = vm.getObject(number.intValue()).getValue().toString();
        return result;

    }

    public static void main(String[] args) {
        MaFengWo mfw = new MaFengWo();
        //String fullurl = "GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fuser%2Ffriend%2Fget_friends_list%2Fv1&app_code%3Dcom.mfw.roadbook%26app_ver%3D9.3.7%26app_version_code%3D734%26brand%3Dgoogle%26channel_id%3DMFW%26dev_ver%3DD1907.0%26device_id%3D40%253A4E%253A36%253AB1%253A47%253A3E%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26jsondata%3D%257B%2522user_id%2522%253A%252213096%2522%252C%2522page%2522%253A%257B%2522boundary%2522%253A%252240%2522%252C%2522num%2522%253A%252220%2522%257D%252C%2522type%2522%253A%2522follows%2522%257D%26mfwsdk_ver%3D20140507%26o_lat%3D40.008481%26o_lng%3D116.350205%26oauth_consumer_key%3D5%26oauth_nonce%3Dea6cad67-a5fe-4564-89ce-b2a78b0e05de%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626421913%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3D40%253A4E%253A36%253AB1%253A47%253A3E%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26x_auth_mode%3Dclient_auth";
        String fullurl ="GET&https%3A%2F%2Fmapi.mafengwo.cn%2Fuser%2Fprofile%2Fget_profile%2Fv2&app_code%3Dcom.mfw.roadbook%26app_ver%3D10.0.0%26app_version_code%3D837%26brand%3Dgoogle%26channel_id%3DMFW-WDJPPZS-1%26dev_ver%3DD2001.0%26device_id%3DA4%253A50%253A46%253A36%253AA8%253A43%26device_type%3Dandroid%26hardware_model%3DPixel%26has_notch%3D0%26mfwsdk_ver%3D20140507%26o_coord%3Dwgs%26o_lat%3D40.008221%26o_lng%3D116.350234%26oauth_consumer_key%3D5%26oauth_nonce%3D00b85005-ab89-401f-9037-aa9b3f37198e%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1626430325%26oauth_token%3D0_0969044fd4edf59957f4a39bce9200c6%26oauth_version%3D1.0%26open_udid%3DA4%253A50%253A46%253A36%253AA8%253A43%26patch_ver%3D1.6%26screen_height%3D1794%26screen_scale%3D2.88%26screen_width%3D1080%26sys_ver%3D8.1.0%26time_offset%3D480%26user_id%3D30044554%26x_auth_mode%3Dclient_auth";
        Map<String,String> result=mfw.xPreAuthencode(fullurl);

        System.out.println(result.toString());
    }



}
