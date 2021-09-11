import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileOutputStream


open class SyncFilesExtension {
    var apkFiles:Array<String> =arrayOf()
    var libSoFiles:Array<String> = arrayOf()
    var libFiles:Array<String> =arrayOf()
}

open class AsyncFilePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.run {
            val filesExtension = extensions.create("syncFiles", SyncFilesExtension::class.java)
            tasks.create("syncFiles"){
                    doLast {
                        val okHttpClient=OkHttpClient()
                        val projectPath=project.projectDir.absolutePath
                        preCreateDir(projectPath)
                        filesExtension.apkFiles.map {
                            downloadFile(location="$projectPath/apk/",url=it,client=okHttpClient)
                        }
                        filesExtension.libSoFiles.map{
                            downloadFile(location="$projectPath/lib_so/",url=it,client=okHttpClient)
                        }
                        filesExtension.libFiles.map{
                            downloadFile(location="$projectPath/lib/",url=it,client=okHttpClient)
                        }
                    }
                }
            }
        }

    fun downloadFile(location:String,url:String,client:OkHttpClient)
    {
        val fileName=FilenameUtils.getName(url)
        val request=Request.Builder().url(url).build()
        val file=File(location,fileName)
        println("async file from ${url} to ${file.absolutePath}")
        val fileStream= FileOutputStream(file)
        val rsp=client.newCall(request).execute()
        val bodyStream=rsp.body?.byteStream()
        bodyStream?.transferTo(fileStream)
    }


    fun preCreateDir(projectDir:String)
    {
        fun createIfNotExists(subFolder:String){
            val f = File(projectDir, subFolder)
            if(!f.exists()){
                f.mkdir()
            }
        }
        arrayListOf("lib_so","apk").map{
            createIfNotExists(it)
        }
    }



}