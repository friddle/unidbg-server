package com.github.unidbg.linux

import com.github.unidbg.Emulator
import com.github.unidbg.arm.ARM
import com.github.unidbg.arm.backend.Backend
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.memory.SvcMemory
import com.github.unidbg.pointer.UnidbgPointer
import com.github.unidbg.unix.struct.TimeVal32
import com.sun.jna.Pointer
import okhttp3.internal.and
import unicorn.ArmConst
import java.util.*

internal class ARM32SyscallHandlerWithTime(svcMemory: SvcMemory): AndroidSyscallHandler() {
    val handler= ARM32SyscallHandler(svcMemory)
    override fun hook(backend: Backend, intno: Int, swi: Int, user: Any) {
        val NR: Int = backend.reg_read(ArmConst.UC_ARM_REG_R7).toInt()
        val emulator = user as Emulator<AndroidFileIO>
        if(NR==78){
            val pc = UnidbgPointer.register(emulator, ArmConst.UC_ARM_REG_PC)
            val bkpt: Int
            if (pc == null) {
                bkpt = swi
            } else {
                if (ARM.isThumb(backend)) {
                    bkpt = pc.getShort(0) and 0xff
                } else {
                    val instruction = pc.getInt(0)
                    bkpt = instruction and 0xf or (instruction shr 8 and 0xfff) shl 4
                }
            }
             backend.reg_write(ArmConst.UC_ARM_REG_R0, gettimeofday(emulator))
            return
            //handler.hook(backend,intno,swi,user)
            //return
        }
        else{
            handler.hook(backend,intno,swi,user)
            return
        }
    }

    fun gettimeofday(emulator:Emulator<*>):Int
    {
        val tv: Pointer? = UnidbgPointer.register(emulator, ArmConst.UC_ARM_REG_R0)
        val tz: Pointer? = UnidbgPointer.register(emulator, ArmConst.UC_ARM_REG_R1)
        return gettimeofday_(emulator, tv,tz)
    }



    fun gettimeofday_(emulator: Emulator<*>?, tv: Pointer?, tz: Pointer?): Int {
        val currentTimeMillis=1607417268979L
        val tv_sec = currentTimeMillis / 1000
        val tv_usec = currentTimeMillis % 1000 * 1000
        val timeVal = TimeVal32(tv)
        timeVal.tv_sec = tv_sec.toInt()
        timeVal.tv_usec = tv_usec.toInt()
        timeVal.pack()
        return 0
    }

    override fun createByteArrayFileIO(p0: String?, p1: Int, p2: ByteArray?): AndroidFileIO {
        return handler.createByteArrayFileIO(p0,p1,p2)
    }

    override fun createDriverFileIO(p0: Emulator<*>?, p1: Int, p2: String?): AndroidFileIO {
        return handler.createDriverFileIO(p0,p1,p2)
    }
}