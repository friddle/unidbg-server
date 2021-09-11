plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
    maven(url= "https://mirrors.huaweicloud.com/repository/maven/" )
    maven(url= "https://maven.aliyun.com/repository/public/")
}
dependencies {
    api("com.squareup.okhttp3:okhttp:4.9.1")
    api("commons-io:commons-io:2.4")
    api("commons-codec:commons-codec:1.6")
}
