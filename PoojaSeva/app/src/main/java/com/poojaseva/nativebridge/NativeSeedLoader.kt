package com.poojaseva.nativebridge

import android.content.res.AssetManager

object NativeSeedLoader {
    external fun readServicesJson(assetManager: AssetManager): String
}
