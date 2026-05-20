#include <jni.h>
#include <string>
#include <cctype>

static std::string JStringToString(JNIEnv* env, jstring str) {
    if (str == nullptr) {
        return "";
    }
    const char* chars = env->GetStringUTFChars(str, nullptr);
    if (chars == nullptr) {
        return "";
    }
    std::string out(chars);
    env->ReleaseStringUTFChars(str, chars);
    return out;
}

static bool IsBlank(const std::string& s) {
    for (unsigned char c : s) {
        if (!std::isspace(c)) {
            return false;
        }
    }
    return true;
}

static bool IsDigits(const std::string& s) {
    if (s.empty()) {
        return false;
    }
    for (unsigned char c : s) {
        if (!std::isdigit(c)) {
            return false;
        }
    }
    return true;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_poojaseva_nativebridge_NativePricing_calculateTotalInr(
    JNIEnv* /*env*/,
    jobject /*thiz*/,
    jint basePrice,
    jfloat multiplier
) {
    const float value = static_cast<float>(basePrice) * multiplier;
    if (value <= 0.0f) {
        return 0;
    }
    return static_cast<jint>(value + 0.0001f);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_poojaseva_nativebridge_NativePricing_validateBooking(
    JNIEnv* env,
    jobject /*thiz*/,
    jlong dateEpochMillis,
    jstring addressLine,
    jstring city,
    jstring pincode,
    jstring contactName,
    jstring contactPhone
) {
    if (dateEpochMillis <= 0) {
        return JNI_FALSE;
    }

    const std::string address = JStringToString(env, addressLine);
    const std::string cityStr = JStringToString(env, city);
    const std::string pincodeStr = JStringToString(env, pincode);
    const std::string nameStr = JStringToString(env, contactName);
    const std::string phoneStr = JStringToString(env, contactPhone);

    if (IsBlank(address) || IsBlank(cityStr) || IsBlank(nameStr)) {
        return JNI_FALSE;
    }

    if (pincodeStr.size() != 6 || !IsDigits(pincodeStr)) {
        return JNI_FALSE;
    }

    if (phoneStr.size() != 10 || !IsDigits(phoneStr)) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}
