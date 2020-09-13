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
#pragma once

#include <jni.h>
#include <QApplication>
#include <qmllogging.h>
#include <QQmlApplicationEngine>
#include <userinputsimulator.h>
//#include <QQmlContext>

class ApplicationFunctions : public QObject
{
    Q_OBJECT
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);

    static void create(int* argc, char** argv);
    static void deleteSingleton();
    static ApplicationFunctions* get();
    static bool check(JNIEnv* env);
    static void invokeLoggingCallback(jobject obj, int type, const std::string& msg);

    static JNIEnv* mainEnv;

    void exec();
    void quitApplication();
    void loadQML(const QString& filePath);
    void unloadQML();
    void reloadQML(const QString& filePath);
    void setLoggingObject(jobject callbackObject);
    void addImageProviderObject(const QString& id, jobject javaImageProviderCallback);
    QList<QScreen*> getScreensList();
    jobjectArray createJScreenArray(JNIEnv* env, int32_t length);
    jobject createJScreen(JNIEnv* env, int32_t x, int32_t y, int32_t w, int32_t h, double dpi);
    std::function<QImage(std::string,int32_t,int32_t)> createImageProviderFunctionCallback(JNIEnv* env, jobject obj);

    void addToContext(const QString& name, const QVariant& value);

    template<typename T>
    void addToContext(const QString& name, T* ptr)
    {
        addToContext(name, QVariant::fromValue(ptr));
    }
public slots:
    void invokeCallback(JNIEnv* env, jobject c);

private:
    ApplicationFunctions(int32_t& argc, char** argv);
    ~ApplicationFunctions();

    QImage toQImage(JNIEnv* env, jobject bufferedImage);

    static ApplicationFunctions* SINGLETON;
    static jclass loggingCallback;
    static jmethodID loggingCallbackMethod;

    static jclass jscreenClass;
    static jmethodID jscreenContructor;

    static jclass imageProviderClass;
    static jmethodID imageProviderInvoke;

    static jclass bufferedImageClass;
    static jmethodID bufferedImageGetWidth;
    static jmethodID bufferedImageGetHeight;
    static jmethodID bufferedImageGetRGB;

    QApplication* m_qapp;
    QMLLogging m_logging;
    QQmlApplicationEngine* m_qmlEngine;
    UserInputSimulator m_uiSim;
};



