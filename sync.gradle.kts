

apply<AsyncFilePlugin>()
configure<SyncFilesExtension> {
    apkFiles=arrayOf(
            "https://github.com/friddle/unidbg-server/releases/download/gradle_build/douyin10_6.apk",
            "https://github.com/friddle/unidbg-server/releases/download/gradle_build/mafengwo.apk",
             "http://apk.360buyimg.com/build-cms/V10.1.4-90060-350271430-lc023-32bit.apk"
    )
    libSoFiles=arrayOf(
            "https://github.com/friddle/unidbg-server/releases/download/gradle_build/libcms.so",
            "https://github.com/friddle/unidbg-server/releases/download/gradle_build/libmfw.so",
            "https://github.com/friddle/unidbg-server/releases/download/mall_apk/libjdbitmapkit.so"
    )
    libFiles=arrayOf(
            "https://github.com/friddle/unidbg-server/releases/download/gradle_build/unidbg-android-jd-crack.jar"
    )
}


