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
#include "qmldatatransfer.h"
#include <QRegularExpression>
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
#include "painterinstructions.h"
#include <iostream>

namespace
{
const std::size_t MAX_SIZE = 1024;
}

Q_DECLARE_METATYPE(PainterInstructions);

void QMLDataTransfer_Store(const QVariant& var, int32_t role)
{
    QMLDataTransfer::store(var, role);
}

void QMLDataTransfer_SetJVariantConverter(
        std::function<jobject(JNIEnv*, jclass, jmethodID, const QVariant&)> func)
{
    QMLDataTransfer::setJVariantConverter(func);
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

JNICALL void setPointReal(JNIEnv*, jclass, jdouble x, jdouble y, jint roleIndex)
{
    QMLDataTransfer::storeRef(QPointF(x, y), roleIndex);
}

JNICALL void setLine(JNIEnv*, jclass, jint x1, jint y1, jint x2, jint y2, jint roleIndex)
{
    QMLDataTransfer::storeRef(QLine(x1, y1, x2, y2), roleIndex);
}

JNICALL void setRectangle(JNIEnv*, jclass, jint x, jint y, jint w, jint h, jint roleIndex)
{
    QMLDataTransfer::storeRef(QRect(x, y, w, h), roleIndex);
}

JNICALL void setRectangleReal(JNIEnv*, jclass, jdouble x, jdouble y, jdouble w, jdouble h, jint roleIndex)
{
    QMLDataTransfer::storeRef(QRectF(x, y, w, h), roleIndex);
}

JNICALL void setString(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    QString res = JNIUtilities::toQString(env, v);
    QMLDataTransfer::storeRef(res, roleIndex);
}

JNICALL void setRegularExpression(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    QString res = JNIUtilities::toQString(env, v);
    QMLDataTransfer::storeRef(QRegularExpression(res), roleIndex);
}

JNICALL void setURL(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    QString res = JNIUtilities::toQString(env, v);
    QMLDataTransfer::storeRef(QUrl(res), roleIndex);
}

JNICALL void setUUID(JNIEnv* env, jclass, jstring v, jint roleIndex)
{
    QString res = JNIUtilities::toQString(env, v);
    QMLDataTransfer::storeRef(QUuid(res), roleIndex);
}

JNICALL void setByteArray(JNIEnv* env, jclass, jbyteArray v, jint roleIndex)
{
    const jsize len = env->GetArrayLength(v);
    jbyte* array = env->GetByteArrayElements(v, NULL);
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
    QMLDataTransfer::storeRef(QDateTime::fromMSecsSinceEpoch(seconds * 1000L + nanos / 1000000L), roleIndex);
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

JNICALL void setFont(JNIEnv*, jclass, jint fontIndex, jint roleIndex)
{
    QMLDataTransfer::storeRef(JNIUtilities::getFont(fontIndex), roleIndex);
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

JNICALL void setPainterInstructions(JNIEnv* env, jclass, jint length, jbyteArray data, jint roleIndex)
{
    jbyte* array = env->GetByteArrayElements(data, NULL);

    unsigned char* copy = new unsigned char[length];
    memcpy(copy, array, length);
    PainterInstructions instr(length, copy);
    QMLDataTransfer::storeRef(instr, roleIndex);

    env->ReleaseByteArrayElements(data, array, JNI_ABORT);
}

std::function<jobject(JNIEnv*, jclass, jmethodID, const QVariant&)> QMLDataTransfer::toJVariantFunc;
std::vector<QVariant> QMLDataTransfer::variants;
QVector<int32_t> QMLDataTransfer::roleStack;


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
jmethodID QMLDataTransfer::fromPointFMethod;
jmethodID QMLDataTransfer::fromRectangleMethod;
jmethodID QMLDataTransfer::fromRectangleFMethod;
jmethodID QMLDataTransfer::fromURLMethod;
jmethodID QMLDataTransfer::fromUUIDMethod;
jmethodID QMLDataTransfer::fromPainterInstructions;
jmethodID QMLDataTransfer::fromStorableMethod;

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
    fromDimensionMethod = env->GetStaticMethodID(jvariantClass, "fromDimension", "(II)Lcom/github/sdankbar/qml/JVariant;");
    fromColorMethod = env->GetStaticMethodID(jvariantClass, "fromColor", "(I)Lcom/github/sdankbar/qml/JVariant;");
    fromPolygonMethod = env->GetStaticMethodID(jvariantClass, "fromPolygon", "([D[D)Lcom/github/sdankbar/qml/JVariant;");
    fromInstanteMethod = env->GetStaticMethodID(jvariantClass, "fromInstant", "(JI)Lcom/github/sdankbar/qml/JVariant;");
    fromJFontMethod = env->GetStaticMethodID(jvariantClass, "fromJFont", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromLineMethod = env->GetStaticMethodID(jvariantClass, "fromLine", "(IIII)Lcom/github/sdankbar/qml/JVariant;");
    fromPatternMethod = env->GetStaticMethodID(jvariantClass, "fromPattern", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromPointMethod = env->GetStaticMethodID(jvariantClass, "fromPoint", "(II)Lcom/github/sdankbar/qml/JVariant;");
    fromPointFMethod = env->GetStaticMethodID(jvariantClass, "fromPoint", "(DD)Lcom/github/sdankbar/qml/JVariant;");
    fromRectangleMethod = env->GetStaticMethodID(jvariantClass, "fromRectangle", "(IIII)Lcom/github/sdankbar/qml/JVariant;");
    fromRectangleFMethod = env->GetStaticMethodID(jvariantClass, "fromRectangle", "(DDDD)Lcom/github/sdankbar/qml/JVariant;");
    fromURLMethod = env->GetStaticMethodID(jvariantClass, "fromURL", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromUUIDMethod = env->GetStaticMethodID(jvariantClass, "fromUUID", "(Ljava/lang/String;)Lcom/github/sdankbar/qml/JVariant;");
    fromPainterInstructions = env->GetStaticMethodID(jvariantClass, "fromPainterInstructions", "([B)Lcom/github/sdankbar/qml/JVariant;");
    fromStorableMethod = env->GetStaticMethodID(jvariantClass, "fromStorable",
                                                "(Lcom/github/sdankbar/qml/JVariant$Storable;)Lcom/github/sdankbar/qml/JVariant;");

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
        JNIUtilities::createJNIMethod("setPointReal",    "(DDI)V",    (void *)&setPointReal),
        JNIUtilities::createJNIMethod("setLine",    "(IIIII)V",    (void *)&setLine),
        JNIUtilities::createJNIMethod("setRectangle",    "(IIIII)V",    (void *)&setRectangle),
        JNIUtilities::createJNIMethod("setRectangleReal",    "(DDDDI)V",    (void *)&setRectangleReal),
        JNIUtilities::createJNIMethod("setString",    "(Ljava/lang/String;I)V",    (void *)&setString),
        JNIUtilities::createJNIMethod("setRegularExpression",    "(Ljava/lang/String;I)V",    (void *)&setRegularExpression),
        JNIUtilities::createJNIMethod("setURL",    "(Ljava/lang/String;I)V",    (void *)&setURL),
        JNIUtilities::createJNIMethod("setUUID",    "(Ljava/lang/String;I)V",    (void *)&setUUID),
        JNIUtilities::createJNIMethod("setByteArray",    "([BI)V",    (void *)&setByteArray),
        JNIUtilities::createJNIMethod("setColor",    "(II)V",    (void *)&setColor),
        JNIUtilities::createJNIMethod("setDateTime",    "(JII)V",    (void *)&setDateTime),
        JNIUtilities::createJNIMethod("setImage",    "(II[BI)V",    (void *)&setImage),
        JNIUtilities::createJNIMethod("setFont",    "(II)V",    (void *)&setFont),
        JNIUtilities::createJNIMethod("setPolyline",    "(I[DI)V",    (void *)&setPolyline),
        JNIUtilities::createJNIMethod("setPainterInstructions",    "(I[BI)V",    (void *)&setPainterInstructions),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/data_transfer/QMLDataTransfer");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(methods[0]));
    env->DeleteLocalRef(javaClass);
}

void QMLDataTransfer::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(jvariantClass);
}

void QMLDataTransfer::setJVariantConverter(
        std::function<jobject(JNIEnv*, jclass, jmethodID, const QVariant&)> func)
{
    toJVariantFunc = func;
}

jobject QMLDataTransfer::toJVariant(JNIEnv* env, const QVariant& value)
{
    switch (value.typeId()) {
    case qMetaTypeId<bool>(): {
        return env->NewObject(jvariantClass, booleanConstructor, value.toBool());
    }
    case qMetaTypeId<QByteArray>(): {
        const QByteArray a = value.toByteArray();
        jbyteArray arrayObj = env->NewByteArray(a.length());
        jbyte* array = env->GetByteArrayElements(arrayObj, nullptr);
        memcpy(array, a.data(), a.size());
        env->ReleaseByteArrayElements(arrayObj, array, 0);// Commit and release
        return env->NewObject(jvariantClass, byteArrayConstructor, arrayObj);
    }
    case qMetaTypeId<QColor>(): {
        const QColor c = value.value<QColor>();
        return env->CallStaticObjectMethod(jvariantClass, fromColorMethod, c.rgba());
    }
    case qMetaTypeId<QDateTime>(): {
        const QDateTime d = value.toDateTime();
        const int64_t millis = d.toMSecsSinceEpoch();
        const int64_t seconds = millis / 1000L;
        const int64_t nanos = (millis - (seconds * 1000L)) * 1000000L;
        return env->CallStaticObjectMethod(jvariantClass, fromInstanteMethod, seconds, nanos);
    }
    case qMetaTypeId<double>(): {
        return env->NewObject(jvariantClass, doubleConstructor, value.toDouble());
    }
    case qMetaTypeId<QImage>(): {
        const QImage i = value.value<QImage>();
        jbyteArray arrayObj = env->NewByteArray(i.sizeInBytes());
        jbyte* array = env->GetByteArrayElements(arrayObj, nullptr);
        memcpy(array, i.constBits(), i.sizeInBytes());
        env->ReleaseByteArrayElements(arrayObj, array, 0);// Commit and release
        return env->CallStaticObjectMethod(jvariantClass, fromBufferedImageMethod, i.width(), i.height(), arrayObj);
    }
    case qMetaTypeId<int>(): {
        return env->NewObject(jvariantClass, integerConstructor, value.toInt());
    }
    case qMetaTypeId<QLine>(): {
        const QLine l = value.toLine();
        return env->CallStaticObjectMethod(jvariantClass, fromLineMethod, l.x1(), l.y1(), l.x2(), l.y2());
    }
    case qMetaTypeId<qlonglong>(): {
        return env->NewObject(jvariantClass, longConstructor, value.toLongLong());
    }
    case qMetaTypeId<QPoint>(): {
        const QPoint p = value.toPoint();
        return env->CallStaticObjectMethod(jvariantClass, fromPointMethod, p.x(), p.y());
    }
    case qMetaTypeId<QPointF>(): {
        const QPointF p = value.toPointF();
        return env->CallStaticObjectMethod(jvariantClass, fromPointFMethod, p.x(), p.y());
    }
    case qMetaTypeId<QRect>(): {
        const QRect r = value.toRect();
        return env->CallStaticObjectMethod(jvariantClass, fromRectangleMethod, r.x(), r.y(), r.width(), r.height());
    }
    case qMetaTypeId<QRectF>(): {
        const QRectF r = value.toRectF();
        return env->CallStaticObjectMethod(jvariantClass, fromRectangleFMethod, r.x(), r.y(), r.width(), r.height());
    }
    case qMetaTypeId<QRegularExpression>(): {
        const QString str = value.toRegularExpression().pattern();
        jstring jStr = env->NewString(
                    reinterpret_cast<const jchar*>(str.constData()),
                    str.length());
        return env->CallStaticObjectMethod(jvariantClass, fromPatternMethod, jStr);
    }
    case qMetaTypeId<QSize>(): {
        const QSize s = value.toSize();
        return env->CallStaticObjectMethod(jvariantClass, fromDimensionMethod, s.width(), s.height());
    }
    case qMetaTypeId<QString>(): {
        const QString str = value.toString();
        jstring jStr = env->NewString(
                    reinterpret_cast<const jchar*>(str.constData()),
                    str.length());
        return env->NewObject(jvariantClass, stringConstructor, jStr);
    }
    case qMetaTypeId<QUrl>(): {
        const QString str = value.toUrl().toString();
        jstring jStr = env->NewString(
                    reinterpret_cast<const jchar*>(str.constData()),
                    str.length());
        return env->CallStaticObjectMethod(jvariantClass, fromURLMethod, jStr);
    }
    case qMetaTypeId<QUuid>(): {
        const QString str = value.toUuid().toString(QUuid::WithoutBraces);
        jstring jStr = env->NewString(
                    reinterpret_cast<const jchar*>(str.constData()),
                    str.length());
        return env->CallStaticObjectMethod(jvariantClass, fromUUIDMethod, jStr);
    }
    case qMetaTypeId<QFont>(): {
        const QString str = value.value<QFont>().toString();
        jstring jStr = env->NewString(
                    reinterpret_cast<const jchar*>(str.constData()),
                    str.length());
        return env->CallStaticObjectMethod(jvariantClass, fromJFontMethod, jStr);
    }
    default:
        if (value.canConvert<PainterInstructions>())
        {
            const PainterInstructions p = value.value<PainterInstructions>();
            jbyteArray data = p.cloneIntoJavaArray(env);
            return env->CallStaticObjectMethod(jvariantClass, fromPainterInstructions, data);
        }
        else if (value.canConvert<QPolygonF>())
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
        else if (toJVariantFunc)
        {
            return toJVariantFunc(env, jvariantClass, fromStorableMethod, value);
        }
        else
        {
            return nullptr;
        }
    }
}

QMLDataTransfer::QMLDataTransfer()
{
    // Empty Implementation
}
