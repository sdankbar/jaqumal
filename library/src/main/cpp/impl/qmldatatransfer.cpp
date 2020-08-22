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

namespace
{
const std::size_t MAX_ROLES = 256;
}

JNICALL void setInteger(JNIEnv*, jclass, jint i, jint roleIndex)
{
    QMLDataTransfer::store(i, roleIndex);
}

JNICALL void setLong(JNIEnv*, jclass, jlong v, jint roleIndex)
{
    QMLDataTransfer::store(v, roleIndex);
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
        double x = *array;
        double y = *(array + 1);
        polygon.append(QPointF(x, y));
        array += 2;
    }
    QMLDataTransfer::storeRef(polygon, roleIndex);

    env->ReleaseDoubleArrayElements(data, array, JNI_ABORT);
}

std::vector<QVariant> QMLDataTransfer::variants;
std::vector<int32_t> QMLDataTransfer::roleStack;

void QMLDataTransfer::initialize(JNIEnv* env)
{
    QMLDataTransfer::variants.resize(MAX_ROLES);
    QMLDataTransfer::roleStack.reserve(MAX_ROLES);

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
    env->RegisterNatives(javaClass, methods, sizeof(methods));
    env->DeleteLocalRef(javaClass);
}

void QMLDataTransfer::uninitialize(JNIEnv*)
{

}

QMLDataTransfer::QMLDataTransfer()
{
    // Empty Implementation
}
