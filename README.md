#fork unidbg-server
1. Git删除apk文件和so文件
2. 提供线上同步脚本    
3. maven 转成Gradle With kts
4. 打包的时候添加外层目录(apk/lib_so)到resources。并修改读取方式  
5. 可以编辑依赖。

# 同步线上文件到本地
在sync.gradle.kts下面设置文件地址，apkFiles放到apk目录下。 其他类似  
gradle syncFiles(自动同步文件)

# 打包
正常打包: gradle build 
自定义依赖打包(即相应的依赖文件夹下的so文件) gradle build -DLocalBuild=true

#  运行
springboot运行unidbg
java -jar -Dserver.port=8080 xxx.jar






### 感谢
https://github.com/zhkl0228/unidbg
