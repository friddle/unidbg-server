import com.crack.JingDong
import com.github.unidbg.linux.file.ByteArrayFileIO
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JingDongTest {
   @Test
    fun testJd()
    {
        val jingdong=JingDong<ByteArrayFileIO>()
        val functionId="asynInteface"
        val body="{\"intefaceType\":\"asynIntefaceType\",\"skuId\":\"100008667315\"}"
        val uuid="99001184062989-f460e22c02fa"
        val platform="android"
        val version="9.2.2"
        val st=jingdong.crack(functionId,body,uuid,platform,version)
        println("st:${st}")
        assert(st=="st=1607417268979&sign=15db6c5b8076570b5db2407c308d282f&sv=111")
    }
}