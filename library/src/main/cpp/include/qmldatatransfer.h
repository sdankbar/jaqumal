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

    static const QVariant& retrieve(size_t i);
    static void retrieve(QVariant& var, size_t i);
    static void retrieve(QVariant& var, int32_t& role, size_t i);

    static jobject toJVariant(const QVariant& value);
    static const std::vector<int32_t>& getPendingRoleIndices();
    static void clearPendingData();

private:
    QMLDataTransfer();

    static std::vector<QVariant> variants;
    static std::vector<int32_t> roleStack;

    static jclass jvariantClass;
    static jmethodID fromBufferedImageMethod;
    static jmethodID fromDimensionMethod;
    static jmethodID fromColorMethod;
    static jmethodID fromPolygonMethod;
    static jmethodID fromInstanteMethod;
    static jmethodID fromJFontMethod;
    static jmethodID fromLineMethod;
    static jmethodID fromPatternMethod;
    static jmethodID fromPointMethod;
    static jmethodID fromRectangleMethod;
    static jmethodID fromURLMethod;
    static jmethodID fromUUIDMethod;

    static jmethodID booleanConstructor;
    static jmethodID byteArrayConstructor;
    static jmethodID doubleConstructor;
    static jmethodID floatConstructor;
    static jmethodID integerConstructor;
    static jmethodID longConstructor;
    static jmethodID stringConstructor;
};

