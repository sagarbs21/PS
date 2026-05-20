#include <jni.h>
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_poojaseva_nativebridge_NativeSeedLoader_readServicesJson(
    JNIEnv* env,
    jobject /*thiz*/,
    jobject assetManager
) {
    if (assetManager == nullptr) {
        return env->NewStringUTF("");
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if (mgr == nullptr) {
        return env->NewStringUTF("");
    }

    AAsset* asset = AAssetManager_open(mgr, "services.json", AASSET_MODE_BUFFER);
    if (asset == nullptr) {
        return env->NewStringUTF("");
    }

    const size_t length = AAsset_getLength(asset);
    std::string data;
    data.resize(length);
    const int read = AAsset_read(asset, data.data(), length);
    AAsset_close(asset);

    if (read <= 0) {
        return env->NewStringUTF("");
    }

    if (static_cast<size_t>(read) < data.size()) {
        data.resize(static_cast<size_t>(read));
    }

    return env->NewStringUTF(data.c_str());
}
