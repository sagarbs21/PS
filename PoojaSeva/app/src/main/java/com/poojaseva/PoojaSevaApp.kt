package com.poojaseva

import android.app.Application
import com.poojaseva.nativebridge.NativeLibs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PoojaSevaApp : Application() {
	override fun onCreate() {
		super.onCreate()
		NativeLibs.load()
	}
}
