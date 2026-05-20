#include <jni.h>
#include <string>
#include <chrono>
#include <random>
#include <sstream>
#include <iomanip>

static std::string RandomHex(size_t length) {
    static const char* kHex = "0123456789ABCDEF";
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<int> dist(0, 15);

    std::string out;
    out.reserve(length);
    for (size_t i = 0; i < length; ++i) {
        out.push_back(kHex[dist(gen)]);
    }
    return out;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_poojaseva_nativebridge_NativeBookingId_generateBookingId(
    JNIEnv* env,
    jobject /*thiz*/
) {
    const auto now = std::chrono::system_clock::now().time_since_epoch();
    const auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(now).count();

    std::ostringstream oss;
    oss << "BKG_" << ms << "_" << RandomHex(6);
    const std::string id = oss.str();
    return env->NewStringUTF(id.c_str());
}
