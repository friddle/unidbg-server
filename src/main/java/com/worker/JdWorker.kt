package com.worker

import com.crack.JingDong
import com.crack.MaFengWo
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.worker.Worker
import java.io.IOException

class JdWorker : Worker {
    val jingdong: JingDong<ByteArrayFileIO> by lazy{JingDong()}

    @Throws(IOException::class)
    override fun close() {
        jingdong.destroy()
    }

    fun worker(vararg args: String?): Map<String, String> {
//        System.out.println("MFWWorker worker: " + Thread.currentThread().getName() + Thread.currentThread().getId());
        val functionId = args[0].toString()
        val body=args[1].toString()
        val uuid=args[2].toString()
        val platform=args[3].toString()
        val version=args[4].toString()
        return hashMapOf<String,String>(Pair("sign",jingdong.crack(functionId,body,uuid,platform,version)))
    }

}