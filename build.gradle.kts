import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript{
    repositories {
        mavenCentral()
        maven(url= "https://mirrors.huaweicloud.com/repository/maven/" )
        maven(url= "https://maven.aliyun.com/repository/public/")
    }
}

val unidb_version="0.9.4"

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
java.sourceCompatibility = JavaVersion.VERSION_1_9

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

val unidbg_dependencies=arrayListOf("unidbg-dynarmic","unidbg-api","unidbg-android")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("commons-io:commons-io:2.4")
    implementation("commons-codec:commons-codec:1.6")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("com.squareup.okhttp3:okhttp:4.9.1")
    api("com.alibaba:fastjson:1.2.78")
    api("net.dongliu:apk-parser:2.6.10")
    api("com.github.zhkl0228:unicorn:1.0.12")
    api("com.github.zhkl0228:keystone:0.9.5")
    api("com.github.zhkl0228:capstone:3.0.11")
    val importJarDependNames= arrayListOf<String>()
    File(projectDir.absolutePath,"/lib").listFiles().filter{it.name.contains("jar")}.forEach{
            jarFile->
        api(files("lib/${jarFile.name}"))
        println("import jar lib/${jarFile.name}")
        val depend_name=unidbg_dependencies.filter{depend_name->jarFile.name.contains(depend_name)}.firstOrNull()?:""
        if(depend_name.isNotEmpty()){
            importJarDependNames.add(depend_name)
        }
    }
    unidbg_dependencies.filter { !importJarDependNames.contains(it) }.forEach {
        println("import dependencies:${it}")
        api("com.github.zhkl0228:${it}:${unidb_version}")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}



apply("sync.gradle.kts")
tasks.withType<KotlinCompile>{
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }

}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = listOf("--add-exports","java.base/sun.security.pkcs=ALL-UNNAMED")
}


tasks.withType<Test> {
    useJUnitPlatform()
}


