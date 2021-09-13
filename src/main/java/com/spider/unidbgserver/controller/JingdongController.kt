package com.spider.unidbgserver.controller

import com.alibaba.fastjson.util.IOUtils.UTF8
import com.crack.DouyinSign
import com.crack.JingDong
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.spider.unidbgserver.service.DouyinSignService
import net.dongliu.apk.parser.ByteArrayApkFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import javax.annotation.Resource

@Controller
@RequestMapping("/unidbg")
class JingdongController {
    val jingdong: JingDong<ByteArrayFileIO> by lazy{JingDong()}
    @RequestMapping(value = ["jdSign"], method = [RequestMethod.GET, RequestMethod.POST])
    @ResponseBody
    fun jdSign(
        @RequestParam("functionId")functionId:String,
        @RequestParam("body")body:String,
        @RequestParam("uuid")uuid:String,
        @RequestParam("platform")platform:String,
        @RequestParam("version")version:String
    ): String {
        synchronized(this) {
            val result = jingdong!!.crack(functionId, URLDecoder.decode(body, UTF_8), uuid, platform, version)
            return result
        }
    }


}