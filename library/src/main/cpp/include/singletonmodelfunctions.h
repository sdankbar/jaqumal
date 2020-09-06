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
#include <QString>
#include <QQmlPropertyMap>

class SingletonModelFunctions
{
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);

    static void invokeMapChangedCallback(jobject callback, jstring key, jobject jvariant);

private:
    static jclass mapChangedClass;
    static jmethodID mapChangedMethod;
};

class GenericObjectModel : public QQmlPropertyMap
{
    Q_OBJECT
    Q_PROPERTY(const QString& modelName READ modelName)

public:
    explicit GenericObjectModel(const QString& modelName, const std::vector<QString>& roles);

    const QString& modelName() const;

    void setData(const std::vector<int32_t>& roleIndex);
    Q_INVOKABLE void setData(const QVariant& data, const QString& propertyName);

    Q_INVOKABLE QVariant getData(const QString& propertyName) const;
    QVariant getData(int32_t roleIndex) const;


    void clear(int32_t roleIndex);
    void clear();

    bool containsRole(int32_t roleIndex);

    void registerValueChangedCallback(jobject c);

private slots:

    void onValueChanged(const QString& key, const QVariant& value);

private:

    void setData(QVariant& data, size_t roleIndex);
    void callbackListeners(const QString& key, const QVariant& newValue);

    // Member variables
    QString m_modelName;

    std::vector<QString> m_roleMap;

    std::vector<jobject> callbacks;
};



