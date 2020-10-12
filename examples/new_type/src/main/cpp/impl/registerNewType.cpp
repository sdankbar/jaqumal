/**
 * The MIT License
 * Copyright © 2020 Stephen Dankbar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
#include "registerNewType.h"
#include <QPainter>
#include <QSharedPointer>
#include <iostream>

Q_DECL_IMPORT void QMLDataTransfer_Store(const QVariant& var, int32_t role);
Q_DECL_IMPORT void QMLDataTransfer_SetJVariantConverter(
        std::function<jobject(JNIEnv*, jclass, jmethodID, const QVariant&)> func);
Q_DECL_IMPORT JNINativeMethod JNIUtilities_createJNIMethod(
        const char* name, const char* sig, void* funcPtr);
Q_DECL_EXPORT jclass JNIUtilities_findClassGlobalReference(JNIEnv* env, const char* name);
Q_DECL_EXPORT QString JNIUtilities_toQString(JNIEnv* env, jstring str);
Q_DECL_EXPORT jstring JNIUtilities_toJString(JNIEnv* env, const QString& str);

jclass testStorableClass;
jmethodID testStorableConstructor;

jobject convert(JNIEnv* env, jclass jvariantClass, jmethodID method, const QVariant& var)
{
    if (var.canConvert<QSharedPointer<StringPosition>>()) {
        QSharedPointer<StringPosition> ptr = var.value<QSharedPointer<StringPosition>>();
        jstring str = JNIUtilities_toJString(env, ptr->getString());
        jobject jobj = env->NewObject(testStorableClass, testStorableConstructor, str, ptr->getX(), ptr->getY());
        return env->CallStaticObjectMethod(jvariantClass, method, jobj);
    } else {
        return nullptr;
    }
}

JNICALL void setTestStorable(JNIEnv* env, jclass, jstring str, jint x, jint y, jint roleIndex)
{
    QVariant var = QVariant(QSharedPointer<StringPosition>::create(JNIUtilities_toQString(env, str), x, y));
    QMLDataTransfer_Store(var, roleIndex);
}

Q_DECLARE_METATYPE(StringPosition);

jint JNI_OnLoad(JavaVM* vm, void*)
{
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_2) != JNI_OK) {
        std::cout << "  bad version" << std::endl;
        return JNI_ERR;
    }

    QMLDataTransfer_SetJVariantConverter(&convert);


    qmlRegisterType<NewType>("com.github.sdankbar.jaqumal", 0, 4, "NewType");

    JNINativeMethod methods[] = {
        JNIUtilities_createJNIMethod("setTestStorable",    "(Ljava/lang/String;III)V",    (void *)&setTestStorable)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/examples/new_type/Native");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);

    // Return the JNI Version as required by method
    return JNI_VERSION_1_2;
}

void JNI_OnUnload(JavaVM*, void*)
{
   // Empty Implementation
}

NewType::NewType(QQuickItem* parent) :
    QQuickPaintedItem(parent),
    m_data()
{
    std::cout << "Construct" << std::endl;
}

void NewType::paint(QPainter* painter)
{
    std::cout << "Paint" << std::endl;
    if (m_data)
    {
        painter->drawText(m_data->getX(), m_data->getY(), m_data->getString());
    }
    else
    {
        painter->drawText(50, 50, "Hello NewType");
    }
}

QVariant NewType::data() const
{
    QVariant var = QVariant(m_data);
    return var;
}
void NewType::setData(const QVariant& newData)
{
    if (newData.canConvert<QSharedPointer<StringPosition>>())
    {
        QSharedPointer<StringPosition> newPtr = newData.value<QSharedPointer<StringPosition>>();
        if (m_data != newPtr)
        {
            m_data = newPtr;
            update();
            emit dataChanged();
        }
    }
}

StringPosition::StringPosition():
    QObject(nullptr),
    str(""),
    x(0),
    y(0)
{
    // Empty Implementation
}

StringPosition::StringPosition(const StringPosition& arg):
    QObject(arg.parent()),
    str(arg.str),
    x(arg.x),
    y(arg.y)
{
    // Empty Implementation
}

StringPosition::StringPosition(const QString& str2, int32_t x2, int32_t y2) :
    str(str2),
    x(x2),
    y(y2)
{
    // Empty Implementation
}

const QString& StringPosition::getString() const
{
    return str;
}
int32_t StringPosition::getX() const
{
    return x;
}
int32_t StringPosition::getY() const
{
    return y;
}

