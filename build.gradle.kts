import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript{
    repositories {
        mavenCentral()
        maven(url= "https://mirrors.huaweicloud.com/repository/maven/" )
        maven(url= "https://maven.aliyun.com/repository/public/")
    }
}

sourceSets{
    main{
        resources {
            srcDir("lib_so")
            srcDir("apk")
        }
    }
}


plugins {
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"

}

group = "com.unknow"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11


repositories {
    mavenCentral()
    maven(url= "https://mirrors.huaweicloud.com/repository/maven/" )
    maven(url= "https://maven.aliyun.com/repository/public/")
}

//TODO:isSelfDefineGradle


val isLocalLib = if(project.hasProperty("IsLocalLib")){
        project.property("IsLocalLib").toString().toBoolean()}
else{false}
println("is Local lib ${isLocalLib?:false}")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("commons-io:commons-io:2.4")
    implementation("commons-codec:commons-codec:1.6")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("com.squareup.okhttp3:okhttp:4.9.1")
    api("com.github.zhkl0228:unicorn:1.0.12")
    api("com.github.zhkl0228:keystone:0.9.5")
    api("com.github.zhkl0228:capstone:3.0.11")
    api("com.github.zhkl0228:unidbg-dynarmic:0.9.4")
    if(isLocalLib){
        File(projectDir.absolutePath,"/lib").listFiles().filter{it.name.contains("jar")}.forEach{
            api(files("lib/${it.name}"))
        }
    }else{
        api("com.github.zhkl0228:unidbg-api:0.9.4")
        api("com.github.zhkl0228:unidbg-android:0.9.4")
    }

}


apply("sync.gradle.kts")
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


