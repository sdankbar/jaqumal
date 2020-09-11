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
#include <singletonmodelfunctions.h>
#include <listmodelfunctions.h>
#include <flattreemodelfunctions.h>

#include "qmlimageprovider.h"
#include <QQmlContext>
#include <QQuickWindow>
#include <csignal>
#include <qmltest.h>

JNICALL void createQApplication(JNIEnv* env, jclass, jobjectArray argv)
{
    ApplicationFunctions::mainEnv = env;
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
        ApplicationFunctions::get()->loadQML(JNIUtilities::toQString(env, fileName));
    }
}

JNICALL void reloadQMLFile(JNIEnv* env, jclass, jstring fileName)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->reloadQML(JNIUtilities::toQString(env, fileName));
    }
}

JNICALL void unloadQMLFile(JNIEnv* env, jclass)
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
        ApplicationFunctions::get()->setLoggingObject(env->NewGlobalRef(c));
    }
}

JNICALL void quitQApplication(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->quitApplication();
    }
}

JNICALL int runQMLTests(JNIEnv* env, jclass, jstring pathToQMLTestFile, jobjectArray importPaths)
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
    return runQMLTest(path, paths);
}

JNICALL void addImageProvider(JNIEnv* env, jclass, jstring id, jobject c)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->addImageProviderObject(
            JNIUtilities::toQString(env, id), env->NewGlobalRef(c));
    }
}

JNICALL jobjectArray getScreens(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        QList<QScreen*> screens = ApplicationFunctions::get()->getScreensList();
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
        jobject globalRef = env->NewGlobalRef(callback);
        QMetaObject::invokeMethod(ApplicationFunctions::get(), [=]{
            JNIEnv* qtEnv = ApplicationFunctions::mainEnv;
            ApplicationFunctions::get()->invokeCallback(qtEnv, globalRef);
            qtEnv->DeleteGlobalRef(globalRef);
        }, Qt::QueuedConnection);
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
    jstring javaStr = mainEnv->NewStringUTF(msg.c_str());
    mainEnv->CallVoidMethod(obj, loggingCallbackMethod, type, javaStr);
    if (mainEnv->ExceptionCheck()) {
        std::cerr << "Exception while logging" << std::endl;
        mainEnv->ExceptionClear();
    }
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
JNIEnv* ApplicationFunctions::mainEnv = nullptr;

void ApplicationFunctions::initialize(JNIEnv* env)
{
    loggingCallback = JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/LoggingCallback");
    loggingCallbackMethod = env->GetMethodID(loggingCallback, "invoke", "(ILjava/lang/String;)V");
    jscreenClass= JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/JScreen");
    jscreenContructor= env->GetMethodID(jscreenClass, "<init>", "(IIIID)V");
    imageProviderClass= JNIUtilities::findClassGlobalReference(env, "com/github/sdankbar/qml/cpp/jni/interfaces/ImageProviderCallback");
    imageProviderInvoke= env->GetMethodID(imageProviderClass, "invoke", "(Ljava/lang/String;II)Ljava/awt/image/BufferedImage;");

    bufferedImageClass= JNIUtilities::findClassGlobalReference(env, "java/awt/image/BufferedImage");
    bufferedImageGetWidth= env->GetMethodID(bufferedImageClass, "getWidth", "()I");
    bufferedImageGetHeight= env->GetMethodID(bufferedImageClass, "getHeight", "()I");
    bufferedImageGetRGB= env->GetMethodID(bufferedImageClass, "getRGB", "(IIII[III)[I");

    static JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("createQApplication",    "([Ljava/lang/String;)V",    (void *)&createQApplication),
        JNIUtilities::createJNIMethod("deleteQApplication",    "()V",    (void *)&deleteQApplication),
        JNIUtilities::createJNIMethod("execQApplication",    "()V",    (void *)&execQApplication),
        JNIUtilities::createJNIMethod("getCompileQtVersion",    "()Ljava/lang/String;",    (void *)&getCompileQtVersion),
        JNIUtilities::createJNIMethod("getRuntimeQtVersion",    "()Ljava/lang/String;",    (void *)&getRuntimeQtVersion),
        JNIUtilities::createJNIMethod("loadQMLFile",    "(Ljava/lang/String;)V",    (void *)&loadQMLFile),
        JNIUtilities::createJNIMethod("reloadQMLFile",    "(Ljava/lang/String;)V",    (void *)&reloadQMLFile),
        JNIUtilities::createJNIMethod("unloadQMLFile",    "()V",    (void *)&unloadQMLFile),
        JNIUtilities::createJNIMethod("setLoggingCallback",    "(Lcom/github/sdankbar/qml/cpp/jni/interfaces/LoggingCallback;)V",    (void *)&setLoggingCallback),
        JNIUtilities::createJNIMethod("quitQApplication",    "()V",    (void *)&quitQApplication),
        JNIUtilities::createJNIMethod("runQMLTests",    "(Ljava/lang/String;[Ljava/lang/String;)I",    (void *)&runQMLTests),
        JNIUtilities::createJNIMethod("addImageProvider",    "(Ljava/lang/String;Lcom/github/sdankbar/qml/cpp/jni/interfaces/ImageProviderCallback;)V",    (void *)&addImageProvider),
        JNIUtilities::createJNIMethod("getScreens",    "()[Lcom/github/sdankbar/qml/JScreen;",    (void *)&getScreens),
        JNIUtilities::createJNIMethod("invoke",    "(Lcom/github/sdankbar/qml/cpp/jni/interfaces/InvokeCallback;)V",    (void *)&invoke)
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/ApplicationFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods) / sizeof(methods[0]));
    env->DeleteLocalRef(javaClass);
}

void ApplicationFunctions::uninitialize(JNIEnv* env)
{
    env->DeleteGlobalRef(loggingCallback);
    env->DeleteGlobalRef(jscreenClass);
    env->DeleteGlobalRef(imageProviderClass);
    env->DeleteGlobalRef(bufferedImageClass);
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

void ApplicationFunctions::loadQML(const QString& filePath)
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

void ApplicationFunctions::reloadQML(const QString& filePath)
{
    unloadQML();
    loadQML(filePath);
}

void ApplicationFunctions::setLoggingObject(jobject callbackObject)
{
    m_logging.setCallback(callbackObject);
}

void ApplicationFunctions::addImageProviderObject(const QString& id, jobject javaImageProviderCallback)
{
    m_qmlEngine->addImageProvider(
                id, new QMLImageProvider(createImageProviderFunctionCallback(mainEnv, javaImageProviderCallback)));
}

QList<QScreen*> ApplicationFunctions::getScreensList()
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

        if (env->ExceptionCheck())
        {
            std::cerr << "Exception when calling image provider" << std::endl;
            env->ExceptionClear();
            return QImage();
        }
        else if (bufferedImage != nullptr)
        {
            return toQImage(env, bufferedImage);
        }
        else
        {
            return QImage();
        }
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
    if (env->ExceptionCheck())
    {
        std::cerr << "Exception when calling converting buffered image to QImage (getWidth)" << std::endl;
        env->ExceptionClear();
        return QImage();
    }

    jint h = env->CallIntMethod(bufferedImage, bufferedImageGetHeight);
    if (env->ExceptionCheck())
    {
        std::cerr << "Exception when calling converting buffered image to QImage (getHeight)" << std::endl;
        env->ExceptionClear();
        return QImage();
    }

    jintArray pixelData = static_cast<jintArray>(
                env->CallObjectMethod(bufferedImage, bufferedImageGetRGB, 0, 0, w, h, nullptr, 0, w));

    if (env->ExceptionCheck())
    {
        std::cerr << "Exception when calling converting buffered image to QImage (getRGB)" << std::endl;
        env->ExceptionClear();
        return QImage();
    }
    else
    {
        const int32_t copyLength = 4 * w * h;
        unsigned char* copy = new unsigned char[copyLength];
        jint* jData = env->GetIntArrayElements(pixelData, nullptr);
        memcpy(copy, jData, copyLength);
        env->ReleaseIntArrayElements(pixelData, jData, JNI_ABORT);
        return QImage(copy, w, h, QImage::Format_ARGB32, &cleanupMemory2);
    }
}

void ApplicationFunctions::addToContext(const QString& name, const QVariant& value)
{
    m_qmlEngine->rootContext()->setContextProperty(name, value);
}
