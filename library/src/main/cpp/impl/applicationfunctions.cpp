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
#include "applicationfunctions.h"

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
#include <QFontMetrics>
#include <QScreen>
#include <QApplication>
#include <iostream>
#include <functional>
#include <genericflattreemodel.h>
#include <genericlistmodel.h>
#include <genericobjectmodel.h>

#include "qmlimageprovider.h"
#include <QQmlContext>
#include <QQuickWindow>
#include <iostream>
#include <csignal>
#include <qmltest.h>

JNICALL void createQApplication(JNIEnv* env, jclass, jobjectArray argv)
{
    const int32_t argc = env->GetArrayLength(argv);

    int* argcCopy = new int;
    *argcCopy = argc;
    char** argvCopy = new char*[argc];
    for (int32_t i = 0; i < argc; ++i)
    {
        jstring str = static_cast<jstring>(env->GetObjectArrayElement(argv, i));
        const char* array = env->GetStringUTFChars(str, NULL);
        argvCopy[i] = strdup(array);
        env->ReleaseStringUTFChars(str, array);
    }

    ApplicationFunctions::create(argcCopy, argvCopy);
}

JNICALL void deleteQApplication(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::deleteSingleton();
    }
}

JNICALL void execQApplication(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->exec();
    }
}

JNICALL jstring getCompileQtVersion(JNIEnv* env, jclass)
{
    return env->NewStringUTF(QT_VERSION_STR);
}

JNICALL jstring getRuntimeQtVersion(JNIEnv* env, jclass)
{
    return env->NewStringUTF(qVersion());
}

JNICALL void loadQMLFile(JNIEnv* env, jclass, jstring fileName)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->loadQMLFile(JNIUtilities::toQString(env, fileName));
    }
}

JNICALL void reloadQMLFile(JNIEnv* env, jclass, jstring fileName)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->reloadQMLFile(JNIUtilities::toQString(env, fileName));
    }
}

JNICALL void unloadQML(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->unloadQML();
    }
}

JNICALL void setLoggingCallback(JNIEnv* env, jclass, jobject c)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->setLoggingCallback(c);
    }
}

JNICALL void quitQApplication(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->quitApplication();
    }
}

JNICALL int runQMLTest(JNIEnv* env, jclass, jstring pathToQMLTestFile, jobjectArray importPaths)
{
    std::string path = JNIUtilities::toString(env, pathToQMLTestFile);
    const int32_t count = env->GetArrayLength(importPaths);
    std::vector<std::string> paths;
    for (int i = 0; i < count; ++i)
    {
        jstring str = static_cast<jstring>(env->GetObjectArrayElement(importPaths, i));
        const char* array = env->GetStringUTFChars(str, NULL);
        std::string temp(array);
        env->ReleaseStringUTFChars(str, array);
        paths.push_back(temp);
    }
    runQMLTest(path, paths);
}

JNICALL void addImageProvider(JNIEnv* env, jclass, jstring id, jobject c)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->addImageProvider(
            env, JNIUtilities::toQString(env, id), c);
    }
}

JNICALL jobjectArray getScreens(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        QList<QScreen*> screens = ApplicationFunctions::get()->getScreens();
        jobjectArray array = ApplicationFunctions::get()->createJScreenArray(env, screens.length());
        for (int32_t i= 0; i < screens.length(); ++i)
        {
            QScreen* s = screens[i];

            QRect geo = s->geometry();
            env->SetObjectArrayElement(
                        array, i,
                        ApplicationFunctions::get()->createJScreen(env, geo.x(), geo.y(), geo.width(), geo.height(),
                                                                   s->physicalDotsPerInch()));
        }

        return array;
    }
    else
    {
        return nullptr;
    }
}

JNICALL void invoke(JNIEnv* env, jclass, jobject callback)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->metaObject()->invokeMethod(
                    ApplicationFunctions::get(),
                    "invokeCallback",
                    Qt::QueuedConnection,
                    Q_ARG(jobject, callback));
    }
}

JNICALL void invokeWithDelay(JNIEnv* env, jclass, jobject callback, jint delayMilli)
{
    if (ApplicationFunctions::check(env))
    {
        std::function<void()> func = [=] {
            ApplicationFunctions::get()->invokeCallback(env, callback);
        };
        QTimer::singleShot(delayMilli, func);
    }
}

void ApplicationFunctions::create(int* argc, char** argv)
{
    qmlRegisterType<EventBuilder>("com.github.sdankbar.jaqumal", 0, 4, "EventBuilder");
    qmlRegisterType<EventDispatcher>("com.github.sdankbar.jaqumal", 0, 4, "EventDispatcher");
    qmlRegisterUncreatableType<GenericListModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericListModel", "Cannot create GenericListModel");
    qmlRegisterUncreatableType<GenericFlatTreeModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericFlatTreeModel", "Cannot create GenericFlatTreeModel");
    qmlRegisterUncreatableType<GenericObjectModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericObjectModel", "Cannot create GenericObjectModel");
    qmlRegisterType<JPolyline>("com.github.sdankbar.jaqumal", 0, 4, "JPolyline");

    SINGLETON = new ApplicationFunctions(*argc, argv);
}
void ApplicationFunctions::deleteSingleton()
{
    delete ApplicationFunctions::SINGLETON;
    ApplicationFunctions::SINGLETON = nullptr;
}
ApplicationFunctions* ApplicationFunctions::get()
{
    return SINGLETON;
}

bool ApplicationFunctions::check(JNIEnv* env)
{
    if (SINGLETON)
    {
        return true;
    }
    else
    {
        JNIUtilities::throwQMLException(env, "Attempted to use QApplication before QApplication was created");
        return false;
    }
}

void ApplicationFunctions::invokeLoggingCallback(jobject obj, int type, const std::string& msg)
{
    jstring javaStr = lastEnv->NewStringUTF(msg.c_str());
    lastEnv->CallVoidMethod(obj, loggingCallbackMethod, type, javaStr);
}

jclass ApplicationFunctions::loggingCallback;
jmethodID ApplicationFunctions::loggingCallbackMethod;
jclass ApplicationFunctions::jscreenClass;
jmethodID ApplicationFunctions::jscreenContructor;
jclass ApplicationFunctions::imageProviderClass;
jmethodID ApplicationFunctions::imageProviderInvoke;
jclass ApplicationFunctions::bufferedImageClass;
jmethodID ApplicationFunctions::bufferedImageGetWidth;
jmethodID ApplicationFunctions::bufferedImageGetHeight;
jmethodID ApplicationFunctions::bufferedImageGetRGB;
JNIEnv* ApplicationFunctions::lastEnv = nullptr;

void ApplicationFunctions::initialize(JNIEnv* env)
{
    lastEnv = env;
    loggingCallback = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/LoggingCallback");
    loggingCallbackMethod = env->GetMethodID(loggingCallback, "invoke", "(ILjava/lang/String;)V");
    jscreenClass= JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/JScreen");
    jscreenContructor= env->GetMethodID(jscreenClass, "<init>", "(IIIID)V");
    imageProviderClass= JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/ImageProviderCallback");
    jscreenContructor= env->GetMethodID(jscreenClass, "invoke", "(Ljava/lang/String;II)Ljava.awt.image.BufferedImage;");

    bufferedImageClass= JNIUtilities::findClassGlobalReference(env, "java/awt/image/BufferedImage");
    bufferedImageGetWidth= env->GetMethodID(bufferedImageClass, "getWidth", "()I");
    bufferedImageGetHeight= env->GetMethodID(bufferedImageClass, "getHeight", "()I");
    bufferedImageGetRGB= env->GetMethodID(bufferedImageClass, "getRGB", "(IIII[III)[I");

    // TODO
    static JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("createQApplication",    "([Ljava/lang/String;)V",    (void *)&createQApplication)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/ApplicationFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(methods[0]));
    env->DeleteLocalRef(javaClass);
}

void ApplicationFunctions::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(loggingCallback);
}

ApplicationFunctions* ApplicationFunctions::SINGLETON = nullptr;

void signal_handler(int)
{
  if (ApplicationFunctions::get())
  {
      ApplicationFunctions::get()->quitApplication();
  }
}

ApplicationFunctions::ApplicationFunctions(int32_t& argc, char** argv) :
    m_qapp(new QApplication(argc, argv)),
    m_logging(),
    m_qmlEngine(new QQmlApplicationEngine(m_qapp)),
    m_uiSim()
{
    m_qmlEngine->rootContext()->setContextProperty("log", QVariant::fromValue(&m_logging));
    m_qmlEngine->rootContext()->setContextProperty("userInputSim", QVariant::fromValue(&m_uiSim));

    // Install SIGTERM signal handler so application can shutdown cleanly
    std::signal(SIGTERM, signal_handler);
}

ApplicationFunctions::~ApplicationFunctions()
{
    m_qmlEngine->clearComponentCache();

    if (m_qapp)
    {
        m_qapp->closeAllWindows();

        QList<QObject*> roots = m_qmlEngine->rootObjects();
        for (QObject* obj: roots)
        {
            QQuickWindow* window = qobject_cast<QQuickWindow*>(obj);
            if (window)
            {
                delete window;
            }
        }

        delete m_qapp;
        m_qapp = nullptr;
        m_qmlEngine = nullptr;
    }

    // Cleanup signal handler.
    std::signal(SIGTERM, SIG_DFL);
}

void ApplicationFunctions::exec()
{
   m_qapp->exec();
}

void ApplicationFunctions::quitApplication()
{
   m_qapp->quit();
}

void ApplicationFunctions::invokeCallback(JNIEnv* env, jobject c)
{
    JNIUtilities::invokeCallback(env, c);
}

/*
GenericObjectModel* ApplicationFunctions::createGenericObjectModel(const QString& modelName, const std::vector<QString>& roles)
{
    GenericObjectModel* modelPtr = new GenericObjectModel(modelName, roles);
    m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
    return modelPtr;
}




GenericFlatTreeModel* ApplicationFunctions::createGenericFlatTreeModel(const QString& modelName, const QStringList& roleNames,
                                        const QVector<int>& indices)
{
    QHash<int, QByteArray> roleNameMap;
    for (int32_t i = 0; i < indices.size(); ++i)
    {
        roleNameMap[indices[i]] = roleNames[i].toStdString().c_str();
    }

    GenericFlatTreeModel* modelPtr = new GenericFlatTreeModel(modelName, roleNameMap, 0);
    m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
    return modelPtr;
}
*/

void ApplicationFunctions::loadQMLFile(const QString& filePath)
{
    m_qmlEngine->load(filePath);
}

void ApplicationFunctions::unloadQML()
{
    QList<QObject*> roots = m_qmlEngine->rootObjects();
    for (QObject* obj: roots)
    {
        QQuickWindow* window = qobject_cast<QQuickWindow*>(obj);
        if (window)
        {
            window->close();
            window->deleteLater();
        }
    }
    m_qmlEngine->clearComponentCache();
}

void ApplicationFunctions::reloadQMLFile(const QString& filePath)
{
    unloadQML();
    loadQMLFile(filePath);
}

void ApplicationFunctions::setLoggingCallback(jobject callbackObject)
{
    m_logging.setCallback(callbackObject);
}

void ApplicationFunctions::addImageProvider(JNIEnv* env, const QString& id, jobject javaImageProviderCallback)
{
    m_qmlEngine->addImageProvider(
                id, new QMLImageProvider(createImageProviderFunctionCallback(env, javaImageProviderCallback)));
}

QList<QScreen*> ApplicationFunctions::getScreens()
{
    return m_qapp->screens();
}

jobjectArray ApplicationFunctions::createJScreenArray(JNIEnv* env, int32_t length)
{
    return env->NewObjectArray(length, jscreenClass, nullptr);
}

jobject ApplicationFunctions::createJScreen(JNIEnv* env, int32_t x, int32_t y, int32_t w, int32_t h, double dpi)
{
    return env->NewObject(jscreenClass, jscreenContructor, x, y, w, h, dpi);
}

std::function<QImage(std::string,int32_t,int32_t)> ApplicationFunctions::createImageProviderFunctionCallback(JNIEnv* env, jobject obj)
{
    std::function<QImage(std::string,int32_t,int32_t)> func = [=] (std::string id, int32_t w, int32_t h) {
        jstring jStr = env->NewStringUTF(id.c_str());
        jobject bufferedImage = env->CallObjectMethod(obj, imageProviderInvoke, jStr, w, h);
        return toQImage(env, bufferedImage);
    };
    return func;
}

void cleanupMemory2(void* ptr)
{
    delete static_cast<unsigned char*>(ptr);
}

QImage ApplicationFunctions::toQImage(JNIEnv* env, jobject bufferedImage)
{
    jint w = env->CallIntMethod(bufferedImage, bufferedImageGetWidth);
    jint h = env->CallIntMethod(bufferedImage, bufferedImageGetHeight);
    jintArray pixelData = static_cast<jintArray>(env->CallObjectMethod(bufferedImage, bufferedImageGetRGB, 0, 0, w, h, nullptr, 0, w));
    const int32_t copyLength = 4 * w * h;
    unsigned char* copy = new unsigned char[copyLength];
    jint* jData = env->GetIntArrayElements(pixelData, nullptr);
    memcpy(copy, jData, copyLength);
    env->ReleaseIntArrayElements(pixelData, jData, JNI_ABORT);
    return QImage(copy, w, h, QImage::Format_ARGB32, &cleanupMemory2);
}
