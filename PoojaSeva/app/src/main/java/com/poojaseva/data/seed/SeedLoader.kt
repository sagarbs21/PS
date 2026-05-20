package com.poojaseva.data.seed

import android.content.Context
import com.poojaseva.nativebridge.NativeSeedLoader
import kotlinx.serialization.json.Json

object SeedLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(context: Context): SeedData {
        val text = try {
            NativeSeedLoader.readServicesJson(context.assets)
        } catch (_: Throwable) {
            context.assets.open("services.json").bufferedReader().use { it.readText() }
        }
        return json.decodeFromString(SeedData.serializer(), text)
    }
}
