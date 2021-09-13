package com.github.unidbg.linux

import com.github.unidbg.arm.backend.BackendFactory
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.linux.android.AndroidARMEmulator
import com.github.unidbg.memory.SvcMemory
import com.github.unidbg.unix.UnixSyscallHandler
import java.io.File

open class AndroidARM32EmulatorChooseTime(processName:String, rootDir: File, backendFactories:Collection<BackendFactory>):
    AndroidARMEmulator(processName, rootDir, backendFactories){

    override fun createSyscallHandler(svcMemory: SvcMemory): UnixSyscallHandler<AndroidFileIO?>? {
        return ARM32SyscallHandlerWithTime(svcMemory)
    }
}