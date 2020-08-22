#pragma once

#include <jni.h>
#include <QVariant>

class QMLDataTransfer
{
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);

    template<typename T>
    static void store(T data, int32_t role)
    {
        variants[role].setValue(data);
        roleStack.push_back(role);
    }

    template<typename T>
    static void storeRef(const T& data, int32_t role)
    {
        variants[role].setValue(data);
        roleStack.push_back(role);
    }

private:
    QMLDataTransfer();

    static std::vector<QVariant> variants;
    static std::vector<int32_t> roleStack;
};

