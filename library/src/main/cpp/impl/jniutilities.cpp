#include "jniutilities.h"
#include "string.h"
#include "qmldatatransfer.h"

jint JNI_OnLoad(JavaVM* vm, void*)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2) != JNI_OK) {
        return JNI_ERR;
    }

    JNIUtilities::initialize(env);
    QMLDataTransfer::initialize(env);

    // Return the JNI Version as required by method
    return JNI_VERSION_1_2;
}

void JNI_OnUnload(JavaVM* vm, void*)
{
    JNIEnv* env;
    vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2);

    JNIUtilities::uninitialize(env);
    QMLDataTransfer::uninitialize(env);
}

jclass JNIUtilities::illegalStateExceptionClass;
jclass JNIUtilities::callbackClass;
jmethodID JNIUtilities::callbackMethod;

void JNIUtilities::initialize(JNIEnv* env)
{
    illegalStateExceptionClass = findClassGlobalReference(env, "java/lang/IllegalStateException");
    callbackClass = findClassGlobalReference(env, "com/github/sdankbar/jni/CallbackInterface");
    callbackMethod = env->GetMethodID(callbackClass, "callback", "(I)V");
}

void JNIUtilities::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(illegalStateExceptionClass);
    env->DeleteGlobalRef(callbackClass);
}

JNINativeMethod JNIUtilities::createJNIMethod(const char* name, const char* sig, void* funcPtr)
{
    JNINativeMethod method;
    method.name = strdup(name);
    method.signature = strdup(sig);
    method.fnPtr = funcPtr;
    return method;
}

jclass JNIUtilities::findClassGlobalReference(JNIEnv* env, const char* name)
{
    const jclass tempLocalRef = env->FindClass(name);
    const jclass tempGlobalRef = (jclass) env->NewGlobalRef(tempLocalRef);
    env->DeleteLocalRef(tempLocalRef);
    return tempGlobalRef;
}

void JNIUtilities::throwIllegalStateException(JNIEnv* env, const char* msg)
{
    env->ThrowNew(illegalStateExceptionClass, msg);
}

void JNIUtilities::invokeCallback(JNIEnv* env, jobject callbackObject)
{
    env->CallVoidMethod(callbackObject, callbackMethod, 0);
}

JNIUtilities::JNIUtilities()
{

}
