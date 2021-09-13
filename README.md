#fork [unidbg-server](https://github.com/cxapython/unidbg-server)

1. Git删除apk文件和so文件(162M估计不知道怎么压缩了。除非删Commit)   
2. 提供线上同步脚本(尽量apk文件不要走git)     
3. maven转成gradle   
4. 打包的时候添加外层目录(apk/lib_so)到resources。并修改读取方式    
5. 可以编辑依赖。通过参数决定依赖本地还是线上so    

#前置
推荐用jdk11。可以用sdkman来管理   

# 同步线上文件到本地
在sync.gradle.kts下面设置文件地址.   
gradle syncFiles(自动同步配置文件)

# 打包
gradle build
优先导入lib下面的jar文件。

#  运行
springboot运行unidbg
java -jar -Dserver.port=8080 xxx.jar

# 问题
原版本的抖音没有跑起来。应该是原作者没有更新    
Java11版本以上要--add-exports java.base/sun.security.pkcs=ALL-UNNAMED --illegal-access   

#使用
`http://localhost:9090/unidbg/jdSign?functionId=asynInteface&body=%7B%22intefaceType%22%3A%22asynIntefaceType%22%2C%22skuId%22%3A%22100008667315%22%7D&uuid=99001184062989-f460e22c02fa&platform=android&version=9.2.2`
output demo:`st=1607417268979&sign=c57e8ffcd59827fbe36db57bb53440c3&sv=110`

### 特别感谢
https://github.com/friddle/unidbg-server  
http://91fans.com.cn/post/unidbgthr/   
https://github.com/zhkl0228/unidbg   
