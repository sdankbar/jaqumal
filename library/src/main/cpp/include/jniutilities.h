#pragma once
#include "jni.h"

class JNIUtilities
{
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);

    static JNINativeMethod createJNIMethod(const char* name, const char* sig, void* funcPtr);
    static jclass findClassGlobalReference(JNIEnv* env, const char* name);

    static void throwIllegalStateException(JNIEnv* env, const char* msg);

    static void invokeCallback(JNIEnv* env, jobject callbackObject);

private:
    JNIUtilities();

    static jclass illegalStateExceptionClass;
    static jclass callbackClass;
    static jmethodID callbackMethod;
};

