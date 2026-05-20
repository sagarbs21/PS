package com.poojaseva.nativebridge

object NativeLibs {
    @Volatile private var loaded = false

    fun load() {
        if (loaded) return
        System.loadLibrary("seedloader")
        System.loadLibrary("pricing")
        System.loadLibrary("booking")
        System.loadLibrary("payment")
        loaded = true
    }
}
