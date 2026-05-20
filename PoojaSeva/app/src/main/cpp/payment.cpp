#include <jni.h>
#include <string>
#include <random>

static std::string RandomToken(size_t length) {
    static const char* kChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<int> dist(0, 35);

    std::string out;
    out.reserve(length);
    for (size_t i = 0; i < length; ++i) {
        out.push_back(kChars[dist(gen)]);
    }
    return out;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_poojaseva_nativebridge_NativePayment_generateTransactionId(
    JNIEnv* env,
    jobject /*thiz*/,
    jstring /*bookingId*/,
    jint /*amountInr*/
) {
    const std::string token = RandomToken(10);
    const std::string txn = "TXN_" + token;
    return env->NewStringUTF(txn.c_str());
}
