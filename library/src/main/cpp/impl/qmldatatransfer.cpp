/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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
#include "qmldatatransfer.h"
#include <QRegExp>
#include <QUrl>
#include <QUuid>
#include <QSize>
#include <QPoint>
#include <QLine>
#include <QRect>
#include <QColor>
#include <QDateTime>
#include <QImage>
#include <QFont>

#include "jniutilities.h"
#include <iostream>

namespace
{
const std::size_t MAX_SIZE = 256;
}

JNICALL void setInteger(JNIEnv*, jclass, jint i, jint roleIndex)
{
    QMLDataTransfer::store(static_cast<int32_t>(i), roleIndex);
}

JNICALL void setLong(JNIEnv*, jclass, jlong v, jint roleIndex)
{
    QMLDataTransfer::store(static_cast<int64_t>(v), roleIndex);
}

JNICALL void setBoolean(JNIEnv*, jclass, jboolean v, jint roleIndex)
{
    QMLDataTransfer::store(static_cast<bool>(v), roleIndex);
}

JNICALL void setFloat(JNIEnv*, jclass, jfloat v, jint roleIndex)
{
    QMLDataTransfer::store(v, roleIndex);
}

JNICALL void setDouble(JNIEnv*, jclass, jdouble v, jint roleIndex)
{
    QMLDataTransfer::store(v, roleIndex);
}

JNICALL void setSize(JNIEnv*, jclass, jint w, jint h, jint roleIndex)
{
    QMLDataTransfer::storeRef(QSize(w, h), roleIndex);
}

JNICALL void setPoint(JNIEnv*, jclass, jint x, jint y, jint roleIndex)
{
    QMLDataTransfer::storeRef(QPoint(x, y), roleIndex);
}

JNICALL void setLine(JNIEnv*, jclass, jint x1, jint y1, jint x2, jint y2, jint roleIndex)
{
    QMLDataTransfer::storeRef(QLine(x1, y1, x2, y2), roleIndex);
}

JNICALL void setRectangle(JNIEnv*, jclass, jint x, jint y, jint w, jint h, jint roleIndex)
{
    QMLDataTransfer::storeRef(QRect(x, y, w, h), roleIndex);
}

JNICALL void setString(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    const char* array = env->GetStringUTFChars(v, NULL);
    QMLDataTransfer::storeRef(QString::fromUtf8(array), roleIndex);
    env->ReleaseStringUTFChars(v, array);
}

JNICALL void setRegularExpression(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    const char* array = env->GetStringUTFChars(v, NULL);
    QMLDataTransfer::storeRef(QRegExp(QString::fromUtf8(array)), roleIndex);
    env->ReleaseStringUTFChars(v, array);
}

JNICALL void setURL(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    const char* array = env->GetStringUTFChars(v, NULL);
    QMLDataTransfer::storeRef(QUrl(QString::fromUtf8(array)), roleIndex);
    env->ReleaseStringUTFChars(v, array);
}

JNICALL void setUUID(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    const char* array = env->GetStringUTFChars(v, NULL);
    QMLDataTransfer::storeRef(QUuid(QString::fromUtf8(array)), roleIndex);
    env->ReleaseStringUTFChars(v, array);
}

JNICALL void setByteArray(JNIEnv* env, jclass, jbyteArray v, jint roleIndex)
{
    const jsize len = env->GetArrayLength(v);
    jbyte* array = env->GetByteArrayElements(v,NULL);
    const QByteArray byteArray(reinterpret_cast<const char*>(array), len);
    QMLDataTransfer::storeRef(byteArray, roleIndex);
    env->ReleaseByteArrayElements(v, array, JNI_ABORT);
}

JNICALL void setColor(JNIEnv*, jclass, jint v, jint roleIndex)
{
    QMLDataTransfer::storeRef(QColor::fromRgba(v), roleIndex);
}

JNICALL void setDateTime(JNIEnv*, jclass, jlong seconds, jint nanos, jint roleIndex)
{
    QMLDataTransfer::storeRef(QDateTime::fromMSecsSinceEpoch(seconds * 1000 + nanos / 1000000), roleIndex);
}

void cleanupMemory(void* ptr)
{
    delete static_cast<unsigned char*>(ptr);
}

JNICALL void setImage(JNIEnv* env, jclass, jint w, jint h, jbyteArray data, jint roleIndex)
{
    jbyte* array = env->GetByteArrayElements(data, NULL);

    const int32_t copyLength = 4 * w * h;
    unsigned char* copy = new unsigned char[copyLength];
    memcpy(copy, array, copyLength);
    QMLDataTransfer::storeRef(QImage(copy, w, h, QImage::Format_ARGB32, &cleanupMemory), roleIndex);

    env->ReleaseByteArrayElements(data, array, JNI_ABORT);
}

JNICALL void setFont(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    const char* array = env->GetStringUTFChars(v, NULL);

    QString str = QString::fromUtf8(array);
    QFont f;
    f.fromString(str);
    QMLDataTransfer::storeRef(f, roleIndex);

    env->ReleaseStringUTFChars(v, array);
}

JNICALL void setPolyline(JNIEnv* env, jclass, jint length, jdoubleArray data, jint roleIndex)
{
    jdouble* array = env->GetDoubleArrayElements(data, NULL);

    QPolygonF polygon;
    polygon.reserve(length);
    for (int32_t i = 0; i < length; ++i)
    {
        double x = array[2 * i];
        double y = array[2 * i + 1];
        polygon.append(QPointF(x, y));
    }
    QMLDataTransfer::storeRef(polygon, roleIndex);

    env->ReleaseDoubleArrayElements(data, array, JNI_ABORT);
}

int32_t QMLDataTransfer::variantsIndex = 0;
std::vector<QVariant> QMLDataTransfer::variants;
std::vector<int32_t> QMLDataTransfer::roleStack;


jclass QMLDataTransfer::jvariantClass;
jmethodID QMLDataTransfer::fromBufferedImageMethod;
jmethodID QMLDataTransfer::fromDimensionMethod;
jmethodID QMLDataTransfer::fromColorMethod;
jmethodID QMLDataTransfer::fromPolygonMethod;
jmethodID QMLDataTransfer::fromInstanteMethod;
jmethodID QMLDataTransfer::fromJFontMethod;
jmethodID QMLDataTransfer::fromLineMethod;
jmethodID QMLDataTransfer::fromPatternMethod;
jmethodID QMLDataTransfer::fromPointMethod;
jmethodID QMLDataTransfer::fromRectangleMethod;
jmethodID QMLDataTransfer::fromURLMethod;
jmethodID QMLDataTransfer::fromUUIDMethod;

jmethodID QMLDataTransfer::booleanConstructor;
jmethodID QMLDataTransfer::byteArrayConstructor;
jmethodID QMLDataTransfer::doubleConstructor;
jmethodID QMLDataTransfer::floatConstructor;
jmethodID QMLDataTransfer::integerConstructor;
jmethodID QMLDataTransfer::longConstructor;
jmethodID QMLDataTransfer::stringConstructor;

void QMLDataTransfer::initialize(JNIEnv* env)
{
    QMLDataTransfer::variants.resize(MAX_SIZE);
    QMLDataTransfer::roleStack.reserve(MAX_SIZE);


    jvariantClass = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/JVariant");
    fromBufferedImageMethod = env->GetStaticMethodID(jvariantClass, "fromBufferedImage", "(II[I)Lcom/github/sdankbar/qml/JVariant;");
    fromDimensionMethod = env->GetStaticMethodID(jvariantClass, "fromColor", "(I)Lcom/github/sdankbar/qml/JVariant;");
    fromColorMethod = env->GetStaticMethodID(jvariantClass, "fromDimension", "(II)Lcom/github/sdankbar/qml/JVariant;");
    fromPolygonMethod = env->GetStaticMethodID(jvariantClass, "fromPolygon", "([D[D)Lcom/github/sdankbar/qml/JVariant;");
    fromInstanteMethod = env->GetStaticMethodID(jvariantClass, "fromInstant", "(JI)Lcom/github/sdankbar/qml/JVariant;");
    fromJFontMethod = env->GetStaticMethodID(jvariantClass, "fromJFont", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromLineMethod = env->GetStaticMethodID(jvariantClass, "fromLine", "(IIII)Lcom/github/sdankbar/qml/JVariant;");
    fromPatternMethod = env->GetStaticMethodID(jvariantClass, "fromPattern", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromPointMethod = env->GetStaticMethodID(jvariantClass, "fromPoint", "(II)Lcom/github/sdankbar/qml/JVariant;");
    fromRectangleMethod = env->GetStaticMethodID(jvariantClass, "fromRectangle", "(IIII)Lcom/github/sdankbar/qml/JVariant;");
    fromURLMethod = env->GetStaticMethodID(jvariantClass, "fromURL", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromUUIDMethod = env->GetStaticMethodID(jvariantClass, "fromUUID", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");

    booleanConstructor = env->GetMethodID(jvariantClass, "<init>", "(Z)V");
    byteArrayConstructor= env->GetMethodID(jvariantClass, "<init>", "([B)V");
    doubleConstructor= env->GetMethodID(jvariantClass, "<init>", "(D)V");
    floatConstructor= env->GetMethodID(jvariantClass, "<init>", "(F)V");
    integerConstructor= env->GetMethodID(jvariantClass, "<init>", "(I)V");
    longConstructor= env->GetMethodID(jvariantClass, "<init>", "(J)V");
    stringConstructor= env->GetMethodID(jvariantClass, "<init>", "(Ljava/lang/String;)V");

    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("setInteger",    "(II)V",    (void *)&setInteger),
        JNIUtilities::createJNIMethod("setLong",    "(JI)V",    (void *)&setLong),
        JNIUtilities::createJNIMethod("setBoolean",    "(ZI)V",    (void *)&setBoolean),
        JNIUtilities::createJNIMethod("setFloat",    "(FI)V",    (void *)&setFloat),
        JNIUtilities::createJNIMethod("setDouble",    "(DI)V",    (void *)&setDouble),
        JNIUtilities::createJNIMethod("setSize",    "(III)V",    (void *)&setSize),
        JNIUtilities::createJNIMethod("setPoint",    "(III)V",    (void *)&setPoint),
        JNIUtilities::createJNIMethod("setLine",    "(IIIII)V",    (void *)&setLine),
        JNIUtilities::createJNIMethod("setRectangle",    "(IIIII)V",    (void *)&setRectangle),
        JNIUtilities::createJNIMethod("setString",    "(Ljava/lang/String;I)V",    (void *)&setString),
        JNIUtilities::createJNIMethod("setRegularExpression",    "(Ljava/lang/String;I)V",    (void *)&setRegularExpression),
        JNIUtilities::createJNIMethod("setURL",    "(Ljava/lang/String;I)V",    (void *)&setURL),
        JNIUtilities::createJNIMethod("setUUID",    "(Ljava/lang/String;I)V",    (void *)&setUUID),
        JNIUtilities::createJNIMethod("setByteArray",    "([BI)V",    (void *)&setByteArray),
        JNIUtilities::createJNIMethod("setColor",    "(II)V",    (void *)&setColor),
        JNIUtilities::createJNIMethod("setDateTime",    "(JII)V",    (void *)&setDateTime),
        JNIUtilities::createJNIMethod("setImage",    "(II[BI)V",    (void *)&setImage),
        JNIUtilities::createJNIMethod("setFont",    "(Ljava/lang/String;I)V",    (void *)&setFont),
        JNIUtilities::createJNIMethod("setPolyline",    "(I[DI)V",    (void *)&setPolyline),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/data_transfer/QMLDataTransfer");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(methods[0]));
    env->DeleteLocalRef(javaClass);
}

void QMLDataTransfer::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(jvariantClass);
}

QVariant& QMLDataTransfer::retrieve(size_t i)
{
    return variants[i];
}

jobject QMLDataTransfer::toJVariant(JNIEnv* env, const QVariant& value)
{
    switch (value.type()) {
    case QVariant::Bool: {
        return env->NewObject(jvariantClass, booleanConstructor, value.toBool());
    }
    case QVariant::ByteArray: {
        const QByteArray a = value.toByteArray();
        jbyteArray arrayObj = env->NewByteArray(a.length());
        jbyte* array = env->GetByteArrayElements(arrayObj, nullptr);
        memcpy(array, a.data(), a.size());
        env->ReleaseByteArrayElements(arrayObj, array, 0);// Commit and release
        return env->NewObject(jvariantClass, byteArrayConstructor, arrayObj);
    }
    case QVariant::Color: {
        const QColor c = value.value<QColor>();
        return env->CallStaticObjectMethod(jvariantClass, fromColorMethod, c.rgba());
    }
    case QVariant::DateTime: {
        const QDateTime d = value.toDateTime();
        const int64_t seconds = d.toSecsSinceEpoch();
        const int32_t nanos = d.toMSecsSinceEpoch() * 1000000;
        return env->CallStaticObjectMethod(jvariantClass, fromInstanteMethod, seconds, nanos);
    }
    case QVariant::Double: {
        return env->NewObject(jvariantClass, doubleConstructor, value.toDouble());
    }
    case QVariant::Image: {
        const QImage i = value.value<QImage>();
        jbyteArray arrayObj = env->NewByteArray(i.sizeInBytes());
        jbyte* array = env->GetByteArrayElements(arrayObj, nullptr);
        memcpy(array, i.constBits(), i.sizeInBytes());
        env->ReleaseByteArrayElements(arrayObj, array, 0);// Commit and release
        return env->CallStaticObjectMethod(jvariantClass, fromBufferedImageMethod, i.width(), i.height(), arrayObj);
    }
    case QVariant::Int: {
        return env->NewObject(jvariantClass, integerConstructor, value.toInt());
    }
    case QVariant::Line: {
        const QLine l = value.toLine();
        return env->CallStaticObjectMethod(jvariantClass, fromLineMethod, l.x1(), l.y1(), l.x2(), l.y2());
    }
    case QVariant::LongLong: {
        return env->NewObject(jvariantClass, longConstructor, value.toLongLong());
    }
    case QVariant::Point: {
        const QPoint p = value.toPoint();
        return env->CallStaticObjectMethod(jvariantClass, fromPointMethod, p.x(), p.y());
    }
    case QVariant::Rect: {
        const QRect r = value.toRect();
        return env->CallStaticObjectMethod(jvariantClass, fromRectangleMethod, r.x(), r.y(), r.width(), r.height());
    }
    case QVariant::RegExp: {
        const QString str = value.toRegExp().pattern();
        jstring jStr = env->NewStringUTF(qPrintable(str));
        return env->CallStaticObjectMethod(jvariantClass, fromPatternMethod, jStr);
    }
    case QVariant::Size: {
        const QSize s = value.toSize();
        return env->CallStaticObjectMethod(jvariantClass, fromDimensionMethod, s.width(), s.height());
    }
    case QVariant::String: {
        const QString str = value.toString();
        jstring jStr = env->NewStringUTF(qPrintable(str));
        return env->NewObject(jvariantClass, stringConstructor, jStr);
    }
    case QVariant::Url: {
        const QString str = value.toUrl().toString();
        jstring jStr = env->NewStringUTF(qPrintable(str));
        return env->CallStaticObjectMethod(jvariantClass, fromURLMethod, jStr);
    }
    case QVariant::Uuid: {
        const QString str = value.toUuid().toString();
        jstring jStr = env->NewStringUTF(qPrintable(str));
        return env->CallStaticObjectMethod(jvariantClass, fromUUIDMethod, jStr);
    }
    case QVariant::Font: {
        const QString str = value.value<QFont>().toString();
        jstring jStr = env->NewStringUTF(qPrintable(str));
        return env->CallStaticObjectMethod(jvariantClass, fromJFontMethod, jStr);
    }
    default:
        if (value.canConvert<QPolygonF>())
        {
            const QPolygonF p = value.value<QPolygonF>();
            jdoubleArray xArrayObj = env->NewDoubleArray(p.size());
            jdoubleArray yArrayObj = env->NewDoubleArray(p.size());
            jdouble* xArray = env->GetDoubleArrayElements(xArrayObj, nullptr);
            jdouble* yArray = env->GetDoubleArrayElements(yArrayObj, nullptr);

            for (int32_t i = 0; i < p.size(); ++i)
            {
                const QPointF& point = p[i];
                xArray[i] = point.x();
                yArray[i] = point.y();
            }

            env->ReleaseDoubleArrayElements(xArrayObj, xArray, 0);// Commit and release
            env->ReleaseDoubleArrayElements(yArrayObj, yArray, 0);// Commit and release
            return env->CallStaticObjectMethod(jvariantClass, fromPolygonMethod, xArrayObj, yArrayObj);
        }
        else {
            return nullptr;
        }
    }
}

const std::vector<int32_t>& QMLDataTransfer::getPendingRoleIndices()
{
    return roleStack;
}

std::vector<QVariant>& QMLDataTransfer::getPendingVariants()
{
    return variants;
}

void QMLDataTransfer::clearPendingData()
{
    variantsIndex = 0;
    roleStack.clear();
}

QMLDataTransfer::QMLDataTransfer()
{
    // Empty Implementation
}
