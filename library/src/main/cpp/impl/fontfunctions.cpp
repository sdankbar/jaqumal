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
#include "fontfunctions.h"

#include "jniutilities.h"

#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <jpolyline.h>
#include <QQmlContext>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontDatabase>
#include <QFontMetrics>
#include <QScreen>
#include <QApplication>
#include <iostream>
#include <functional>
#include <applicationfunctions.h>

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getBoundingRect
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getBoundingRect(JNIEnv* env, jstring fontToString, jstring text)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        QRect rect = metrics.boundingRect(JNIUtilities::toQString(env, text));

        return FontFunctions::constructRectangle(env, rect.x(), rect.y(), rect.width(), rect.height());
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getBoundingRect2
 * Signature: (Ljava/lang/String;IIIIIILjava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getBoundingRect2(JNIEnv* env, jclass, jstring fontToString, jint x, jint y, jint w, jint h, jint alignFlags, jint textFlags, jstring text)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        const QRect bounds(x, y, w, h);
        QRect rect = metrics.boundingRect(bounds, alignFlags | textFlags, JNIUtilities::toQString(env, text));

        return FontFunctions::constructRectangle(env, rect.x(), rect.y(), rect.width(), rect.height());
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontInfo
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
jstring JNICALL getQFontInfo(JNIEnv* env, jclass, jstring fontToString)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontInfo info(f);
        //  0         1         2         3        4          5           6      7        8     9             10       11
        // Family, pointSize, pixelSize, bold, exactMatch, fixedPitch, italic, rawMode, style, styleHint, styleName, weight

        const QString temp("%1,%2,%3,%4,%5,%6,%7,%8,%9,%10,%11,%12");
        QString ret = temp.arg(info.family()).arg(info.pointSize()).arg(info.pixelSize())
                .arg(info.bold())
                .arg(info.exactMatch())
                .arg(info.fixedPitch())
                .arg(info.italic())
                .arg(info.rawMode())
                .arg(info.style())
                .arg(info.styleHint())
                .arg(info.styleName())
                .arg(info.weight());

        return env->NewStringUTF(qPrintable(ret));
    }
    else
    {
        return env->NewStringUTF("");
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontMetrics
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
jstring JNICALL getQFontMetrics(JNIEnv* env, jclass, jstring fontToString)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        //  0           1             2      3      4          5          6           7               8            9           10         11          12
        // ascent,averageCharWidth,descent,height,leading,lineSpacing,maxWidth,minLeftBearing,minRightBearing,overLinePos,strikeOutPos,underlinePos,xheight

        const QString temp("%1,%2,%3,%4,%5,%6,%7,%8,%9,%10,%11,%12,%13");
        QString ret = temp.arg(metrics.ascent())
                .arg(metrics.averageCharWidth())
                .arg(metrics.descent())
                .arg(metrics.height())
                .arg(metrics.leading())
                .arg(metrics.lineSpacing())
                .arg(metrics.maxWidth())
                .arg(metrics.minLeftBearing())
                .arg(metrics.minRightBearing())
                .arg(metrics.overlinePos())
                .arg(metrics.strikeOutPos())
                .arg(metrics.underlinePos())
                .arg(metrics.xHeight());
        return env->NewStringUTF(qPrintable(ret));
    }
    else
    {
        return env->NewStringUTF("");
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getQFontToString
 * Signature: (ILjava/lang/String;IIZZZZZZZIDDIIIIILjava/lang/String;II)Ljava/lang/String;
 */
jstring JNICALL getQFontToString(JNIEnv* env, jclass, jint fontIndex, jstring family, jint pointSize,
                                 jint pixelSize, jboolean bold, jboolean italic, jboolean overline,
                                 jboolean strikeout, jboolean underline, jboolean fixedPitch,
                                 jboolean kerning, jint fontWeight, jdouble wordSpacing,
                                 jdouble letteringSpacing, jint letterSpacingType,
                                 jint capitalization, jint hintingPreference, jint stretch, jint style,
                                 jstring styleName, jint styleHint, jint styleStrategy)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f(JNIUtilities::toQString(env, family));
        if (pointSize > 0) {
            f.setPointSize(pointSize);
        } else if (pixelSize > 0) {
            f.setPixelSize(pixelSize);
        }

        f.setBold(bold);
        f.setFixedPitch(fixedPitch);
        f.setItalic(italic);
        f.setKerning(kerning);
        f.setOverline(overline);
        f.setStrikeOut(strikeout);
        f.setUnderline(underline);
        f.setWeight(fontWeight);

        f.setWordSpacing(wordSpacing);
        f.setLetterSpacing(static_cast<QFont::SpacingType>(letterSpacingType), letteringSpacing);

        f.setCapitalization(static_cast<QFont::Capitalization>(capitalization));
        f.setHintingPreference(static_cast<QFont::HintingPreference>(hintingPreference));
        f.setStretch(stretch);
        f.setStyle(static_cast<QFont::Style>(style));
        f.setStyleName(JNIUtilities::toQString(env, (styleName)));
        f.setStyleHint(static_cast<QFont::StyleHint>(styleHint), static_cast<QFont::StyleStrategy>(styleStrategy));

        JNIUtilities::cacheFont(fontIndex, f);

        return JNIUtilities::toJString(env, f.toString());
    }
    else
    {
        return env->NewStringUTF("");
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getStringWidth
 * Signature: (Ljava/lang/String;Ljava/lang/String;)I
 */
jint JNICALL getStringWidth(JNIEnv* env, jclass, jstring fontToString, jstring text)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        return metrics.width(JNIUtilities::toQString(env, text));
    }
    else
    {
        return -1;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    getTightBoundingRect
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;
 */
jobject JNICALL getTightBoundingRect(JNIEnv* env, jclass, jstring fontToString, jstring text)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        QRect rect = metrics.tightBoundingRect(JNIUtilities::toQString(env, text));

        return FontFunctions::constructRectangle(env, rect.x(), rect.y(), rect.width(), rect.height());
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_FontFunctions
 * Method:    inFont
 * Signature: (Ljava/lang/String;I)Z
 */
jboolean JNICALL inFont(JNIEnv* env, jclass, jstring fontToString, jint character)
{
    if (ApplicationFunctions::check(env)) // QTBUG-27024
    {
        QFont f;
        f.fromString(JNIUtilities::toQString(env, fontToString));
        const QFontMetrics metrics(f);
        return metrics.inFont(QChar(static_cast<int32_t>(character)));
    }
    else
    {
        return false;
    }
}

inline int32_t getSize(const QFont& f)
{
    if (f.pointSize() > 0)
    {
        return f.pointSize();
    }
    else
    {
        return f.pixelSize();
    }
}

jint JNICALL scaleToFit(JNIEnv* env, jclass, jint w, jint h, jstring inputString,
                           jint fontIndex, jint minimumSize)
{
    QString qStr = JNIUtilities::toQString(env, inputString);

    const QRect constraint(0, 0, w, h);
    // TODO allow passing flags
    const int32_t startingSize = getSize(JNIUtilities::getFont(fontIndex));
    int32_t workingIndex = fontIndex;
    int32_t lastFittingIndex = workingIndex;
    const int32_t maxIndex = workingIndex + (startingSize - minimumSize);
    QRect bounds = JNIUtilities::getFontMetrics(workingIndex).boundingRect(constraint, 0, qStr);
    while (bounds.width() > w || bounds.height() > h)
    {
        lastFittingIndex = workingIndex;
        ++workingIndex;
        if (workingIndex > maxIndex)
        {
            break;
        }
        else
        {
            bounds = JNIUtilities::getFontMetrics(workingIndex).boundingRect(constraint, 0, qStr);
        }
    }

    return lastFittingIndex;
}

jint JNICALL loadFont(JNIEnv* env, jclass, jstring filePath)
{
   QString qStr = JNIUtilities::toQString(env, filePath);
   return QFontDatabase::addApplicationFont(qStr);
}


jclass FontFunctions::rectangleClass;
jmethodID FontFunctions::rectangleConstructor;

void FontFunctions::initialize(JNIEnv* env)
{
    rectangleClass = JNIUtilities::findClassGlobalReference(env, "java/awt/Rectangle");
    rectangleConstructor = env->GetMethodID(rectangleClass, "<init>", "(IIII)V");

    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("getBoundingRect",    "(Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;",    (void *)&getBoundingRect),
        JNIUtilities::createJNIMethod("getBoundingRect2",    "(Ljava/lang/String;IIIIIILjava/lang/String;)Ljava/awt/Rectangle;",    (void *)&getBoundingRect2),
        JNIUtilities::createJNIMethod("getQFontMetrics",    "(Ljava/lang/String;)Ljava/lang/String;",    (void *)&getQFontMetrics),
        JNIUtilities::createJNIMethod("getQFontToString",    "(ILjava/lang/String;IIZZZZZZZIDDIIIIILjava/lang/String;II)Ljava/lang/String;",    (void *)&getQFontToString),
        JNIUtilities::createJNIMethod("getQFontInfo",    "(Ljava/lang/String;)Ljava/lang/String;",    (void *)&getQFontInfo),
        JNIUtilities::createJNIMethod("getStringWidth",    "(Ljava/lang/String;Ljava/lang/String;)I",    (void *)&getStringWidth),
        JNIUtilities::createJNIMethod("getTightBoundingRect",    "(Ljava/lang/String;Ljava/lang/String;)Ljava/awt/Rectangle;",    (void *)&getTightBoundingRect),
        JNIUtilities::createJNIMethod("inFont",    "(Ljava/lang/String;I)Z",    (void *)&inFont),
        JNIUtilities::createJNIMethod("scaleToFit",    "(IILjava/lang/String;II)I",    (void *)&scaleToFit),
        JNIUtilities::createJNIMethod("loadFont",    "(Ljava/lang/String;)I",    (void *)&loadFont),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/FontFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void FontFunctions::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(rectangleClass);
}

jobject FontFunctions::constructRectangle(JNIEnv* env, int32_t x, int32_t y, int32_t w, int32_t h)
{
    return env->NewObject(rectangleClass, rectangleConstructor, x, y, w, h);
}
