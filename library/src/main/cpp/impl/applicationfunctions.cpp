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
#include "applicationfunctions.h"

#include "jniutilities.h"
#include <compareimage.h>

#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <eventlogger.h>
#include <invoketarget.h>
#include <jpolyline.h>
#include <dashedrectangle.h>
#include <painter.h>
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
#include <keyeventpreprocessor.h>
#include <jdevelopmenttools.h>

#include "qmlimageprovider.h"
#include <QQmlContext>
#include <QResource>
#include <QQuickWindow>
#include <csignal>
#include <qmltest.h>
#include <math.h>

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

JNICALL void pollQAplicationEvents(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->pollEvents();
    }
}

JNICALL void injectMousePressIntoApplication(JNIEnv* env, jclass, jint x, jint y,
                                             jint button, jint buttons,
                                             jint modifiers)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectMousePress(x, y, button, buttons, modifiers);
    }
}

JNICALL void injectMouseReleaseIntoApplication(JNIEnv* env, jclass, jint x, jint y,
                                               jint button, jint buttons,
                                               jint modifiers)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectMouseRelease(x, y, button, buttons, modifiers);
    }
}

JNICALL void injectMouseDoubleClickIntoApplication(JNIEnv* env, jclass, jint x, jint y,
                                               jint button, jint buttons,
                                               jint modifiers)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectMouseDoubleClick(x, y, button, buttons, modifiers);
    }
}

JNICALL void injectMouseMoveIntoApplication(JNIEnv* env, jclass, jint x, jint y,
                                            jint button, jint buttons,
                                            jint modifiers)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectMouseMove(x, y, button, buttons, modifiers);
    }
}

JNICALL void injectWheelIntoApplication(JNIEnv* env, jclass, jint x, jint y,
                                        jint pixelX, jint pixelY,
                                        jint angleX, jint angleY,
                                        jint buttons, jint modifiers,
                                        jint phase, jboolean inverted)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectWheel(x, y, pixelX, pixelY,
                                                 angleX, angleY, buttons, modifiers,
                                                 phase, inverted);
    }
}

JNICALL void injectKeyPressIntoApplication(JNIEnv* env, jclass,
                                           jint key,
                                           jint modifiers,
                                           jstring text,
                                           jboolean autoRep,
                                           jint count)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectKeyPress(key, modifiers, JNIUtilities::toQString(env, text), autoRep, count);
    }
}

JNICALL void injectKeyReleaseIntoApplication(JNIEnv* env, jclass,
                                             jint key,
                                             jint modifiers,
                                             jstring text,
                                             jboolean autoRep,
                                             jint count)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->injectKeyRelease(key, modifiers, JNIUtilities::toQString(env, text), autoRep, count);
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
    QString path = JNIUtilities::toQString(env, pathToQMLTestFile);
    const int32_t count = env->GetArrayLength(importPaths);
    std::vector<QString> paths;
    for (int i = 0; i < count; ++i)
    {
        jstring str = static_cast<jstring>(env->GetObjectArrayElement(importPaths, i));
        paths.push_back(JNIUtilities::toQString(env, str));
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

JNICALL void enableEventLogging(JNIEnv* env, jclass)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->createEventLogger();
    }
}

JNICALL void setWindowsIcon(JNIEnv* env, jclass, jobject jImage)
{
    if (ApplicationFunctions::check(env))
    {
        QImage i = ApplicationFunctions::get()->toQImage(env, jImage);
        ApplicationFunctions::get()->setWindowIcon(QIcon(QPixmap::fromImage(i)));
    }
}

JNICALL jboolean registerResourceFile(JNIEnv* env, jclass, jstring rccFile, jstring mapRoot)
{
    return QResource::registerResource(
                JNIUtilities::toQString(env, rccFile),
                JNIUtilities::toQString(env, mapRoot));
}

JNICALL jboolean registerResourceData(JNIEnv* env, jclass, jint length, jbyteArray rccData, jstring mapRoot)
{
    jbyte* array = env->GetByteArrayElements(rccData, NULL);
    uchar* copy = new uchar[length];
    memcpy(copy, array, length);
    return QResource::registerResource(copy, JNIUtilities::toQString(env, mapRoot));
}

JNICALL void addImportPath(JNIEnv* env, jclass, jstring importPath)
{
    if (ApplicationFunctions::check(env))
    {
        ApplicationFunctions::get()->addPathToImportPath(JNIUtilities::toQString(env, importPath));
    }
}

JNICALL jboolean compareImageToActiveWindow(JNIEnv* env, jclass, jobject jImage, jdouble ratiodB)
{
    if (ApplicationFunctions::check(env))
    {
        QImage target = ApplicationFunctions::get()->toQImage(env, jImage);
        QImage source = takeFocusedWindowScreenShot();

        return fuzzyEquals(source, target, ratiodB);
    }
    else
    {
        return false;
    }
}

JNICALL void generateDeltaBetweenImageAndActiveWindow(JNIEnv* env, jclass, jstring fileName, jobject jImage)
{
    if (ApplicationFunctions::check(env))
    {
        QImage target = ApplicationFunctions::get()->toQImage(env, jImage);
        QImage source = takeFocusedWindowScreenShot();

        QImage output = generateDelta(source, target);
        output.save(JNIUtilities::toQString(env, fileName), "png");
    }
}

JNICALL void saveScreenshot(JNIEnv* env, jclass, jstring path)
{
    if (ApplicationFunctions::check(env))
    {
        QString qpath = JNIUtilities::toQString(env, path);
        QImage source = takeFocusedWindowScreenShot();
        source.save(qpath);
    }
}

void ApplicationFunctions::create(int* argc, char** argv)
{
    qmlRegisterType<EventBuilder>("com.github.sdankbar.jaqumal", 0, 4, "EventBuilder");
    qmlRegisterType<EventDispatcher>("com.github.sdankbar.jaqumal", 0, 4, "EventDispatcher");
    qmlRegisterType<InvokeTarget>("com.github.sdankbar.jaqumal", 0, 4, "InvokeTarget");
    qmlRegisterType<KeyEventPreProcessor>("com.github.sdankbar.jaqumal", 0, 4, "KeyEventPreProcessor");
    qmlRegisterUncreatableType<GenericListModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericListModel", "Cannot create GenericListModel");
    qmlRegisterUncreatableType<GenericFlatTreeModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericFlatTreeModel", "Cannot create GenericFlatTreeModel");
    qmlRegisterUncreatableType<GenericObjectModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericObjectModel", "Cannot create GenericObjectModel");
    qmlRegisterType<JPolyline>("com.github.sdankbar.jaqumal", 0, 4, "JPolyline");
    qmlRegisterType<DashedRectangle>("com.github.sdankbar.jaqumal", 0, 4, "DashedRectangle");
    qmlRegisterType<Painter>("com.github.sdankbar.jaqumal", 0, 4, "Painter");
    qmlRegisterType<JDevelopmentTools>("com.github.sdankbar.jaqumal", 0, 4, "JDevTools");

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

void ApplicationFunctions::incrementAndCheckRecursionDepth(JNIEnv* env)
{
    ++recursionDepth;
    if (recursionDepth > 1)
    {
        JNIUtilities::throwQMLException(env, "Unallowed recursion detected");
    }
}

void ApplicationFunctions::decrementRecursionDepth()
{
    --recursionDepth;
}

void ApplicationFunctions::invokeLoggingCallback(jobject obj, int type, const std::string& msg)
{
    JNIEnv* threadEnv = JNIUtilities::attachThread();

    jstring javaStr = threadEnv->NewStringUTF(msg.c_str());
    threadEnv->CallVoidMethod(obj, loggingCallbackMethod, type, javaStr);
    if (threadEnv->ExceptionCheck()) {
        std::cerr << "Exception while logging: " << msg << std::endl;
        threadEnv->ExceptionClear();
    }
    threadEnv->DeleteLocalRef(javaStr);

    if (mainEnv != threadEnv)
    {
        // Only need to detach if not a normal Java thread.
        JNIUtilities::dettachThread();
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
        JNIUtilities::createJNIMethod("pollQAplicationEvents", "()V",    (void *)&pollQAplicationEvents),
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
        JNIUtilities::createJNIMethod("invoke",    "(Lcom/github/sdankbar/qml/cpp/jni/interfaces/InvokeCallback;)V",    (void *)&invoke),
        JNIUtilities::createJNIMethod("enableEventLogging", "()V", (void *)&enableEventLogging),
        JNIUtilities::createJNIMethod("setWindowsIcon", "(Ljava/awt/image/BufferedImage;)V", (void *)&setWindowsIcon),
        JNIUtilities::createJNIMethod("compareImageToActiveWindow", "(Ljava/awt/image/BufferedImage;D)Z", (void *)&compareImageToActiveWindow),
        JNIUtilities::createJNIMethod("generateDeltaBetweenImageAndActiveWindow", "(Ljava/lang/String;Ljava/awt/image/BufferedImage;)V", (void *)&generateDeltaBetweenImageAndActiveWindow),
        JNIUtilities::createJNIMethod("registerResource", "(Ljava/lang/String;Ljava/lang/String;)Z", (void *)&registerResourceFile),
        JNIUtilities::createJNIMethod("registerResource", "(I[BLjava/lang/String;)Z", (void *)&registerResourceData),
        JNIUtilities::createJNIMethod("addImportPath", "(Ljava/lang/String;)V", (void *)&addImportPath),
        JNIUtilities::createJNIMethod("injectMousePressIntoApplication", "(IIIII)V", (void *)&injectMousePressIntoApplication),
        JNIUtilities::createJNIMethod("injectMouseReleaseIntoApplication", "(IIIII)V", (void *)&injectMouseReleaseIntoApplication),
        JNIUtilities::createJNIMethod("injectMouseDoubleClickIntoApplication", "(IIIII)V", (void *)&injectMouseDoubleClickIntoApplication),
        JNIUtilities::createJNIMethod("injectMouseMoveIntoApplication", "(IIIII)V", (void *)&injectMouseMoveIntoApplication),
        JNIUtilities::createJNIMethod("injectKeyPressIntoApplication", "(IILjava/lang/String;ZI)V", (void *)&injectKeyPressIntoApplication),
        JNIUtilities::createJNIMethod("injectKeyReleaseIntoApplication", "(IILjava/lang/String;ZI)V", (void *)&injectKeyReleaseIntoApplication),
        JNIUtilities::createJNIMethod("saveScreenshot", "(Ljava/lang/String;)V", (void *)&saveScreenshot),
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

int32_t ApplicationFunctions::recursionDepth = 0;

void signal_handler(int)
{
    if (ApplicationFunctions::get())
    {
        ApplicationFunctions::get()->quitApplication();
    }
}

ApplicationFunctions::ApplicationFunctions(int32_t& argc, char** argv) :
    m_qapp(new QApplication(argc, argv)),
    m_qmlEngine(new QQmlApplicationEngine(m_qapp)),
    m_uiSim(),
    m_logging(),
    m_eventLogger(nullptr)
{
    m_qmlEngine->rootContext()->setContextProperty("log", QVariant::fromValue(&m_logging));
    m_qmlEngine->rootContext()->setContextProperty("userInputSim", QVariant::fromValue(&m_uiSim));
    m_qmlEngine->rootContext()->setContextProperty("Jaqumal", QVariant::fromValue(this));

    // Install SIGTERM signal handler so application can shutdown cleanly
    std::signal(SIGTERM, signal_handler);
}

ApplicationFunctions::~ApplicationFunctions()
{
    if (m_qapp)
    {
        m_qmlEngine->clearComponentCache();

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

void ApplicationFunctions::pollEvents()
{
    m_qapp->processEvents();
    m_qapp->sendPostedEvents();
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

void ApplicationFunctions::createEventLogger()
{
    if (m_eventLogger == nullptr)
    {
        m_eventLogger = new EventLogger(m_logging, m_qapp);
        m_qapp->installEventFilter(m_eventLogger);
    }
}

void ApplicationFunctions::addImageProviderObject(const QString& id, jobject javaImageProviderCallback)
{
    m_qmlEngine->addImageProvider(
                id, new QMLImageProvider(createImageProviderFunctionCallback(mainEnv, javaImageProviderCallback)));
}

void ApplicationFunctions::setWindowIcon(const QIcon& icon)
{
    m_qapp->setWindowIcon(icon);
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

std::function<QImage(const QString&,int32_t,int32_t)> ApplicationFunctions::createImageProviderFunctionCallback(JNIEnv* env, jobject obj)
{
    std::function<QImage(const QString&,int32_t,int32_t)> func = [=] (const QString& id, int32_t w, int32_t h) {
        jstring jStr = JNIUtilities::toJString(env, id);
        jobject bufferedImage = env->CallObjectMethod(obj, imageProviderInvoke, jStr, w, h);
        env->DeleteLocalRef(jStr);

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
        const int32_t pixels = w * h;
        const int32_t byteCount = 4 * pixels;
        unsigned char* copy = new unsigned char[byteCount];
        env->GetIntArrayRegion(pixelData, 0, pixels,  reinterpret_cast<jint*>(copy));
        return QImage(copy, w, h, QImage::Format_ARGB32, &cleanupMemory2);
    }
}

void ApplicationFunctions::addToContext(const QString& name, const QVariant& value)
{
    m_objectLookupMap.insert(name, value);
    emit modelMapChanged();
    m_qmlEngine->rootContext()->setContextProperty(name, value);
}

void ApplicationFunctions::addPathToImportPath(const QString& path)
{
    m_qmlEngine->addImportPath(path);
}

QVariant ApplicationFunctions::lookup(const QString& objectName) const
{
    return m_objectLookupMap.value(objectName);
}

const QVariantMap& ApplicationFunctions::modelMap() const
{
    return m_objectLookupMap;
}

void ApplicationFunctions::installEventFilterToApplication(QObject* obj)
{
    m_qapp->installEventFilter(obj);
}

void ApplicationFunctions::removeEventFilterFromApplication(QObject* obj)
{
    m_qapp->removeEventFilter(obj);
}

void ApplicationFunctions::injectMousePress(int32_t x, int32_t y,
                                            int32_t button, int32_t buttons,
                                            int32_t modifiers)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QMouseEvent* event = new QMouseEvent(
                    QEvent::MouseButtonPress,
                    QPointF(x, y),
                    QPointF(x, y),
                    QPointF(x + window->x(), y + window->y()),
                    static_cast<Qt::MouseButton>(button),
                    static_cast<Qt::MouseButtons>(buttons),
                    static_cast<Qt::KeyboardModifiers>(modifiers),
                    Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::postEvent(window, event);
    }
}

void ApplicationFunctions::injectMouseRelease(int32_t x, int32_t y,
                                              int32_t button, int32_t buttons,
                                              int32_t modifiers)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QMouseEvent* event = new QMouseEvent(
                    QEvent::MouseButtonRelease,
                    QPointF(x, y),
                    QPointF(x, y),
                    QPointF(x + window->x(), y + window->y()),
                    static_cast<Qt::MouseButton>(button),
                    static_cast<Qt::MouseButtons>(buttons),
                    static_cast<Qt::KeyboardModifiers>(modifiers),
                    Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::postEvent(window, event);
    }
}

void ApplicationFunctions::injectMouseDoubleClick(int32_t x, int32_t y, int32_t button, int32_t buttons, int32_t modifiers)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QMouseEvent* event = new QMouseEvent(
                    QEvent::MouseButtonDblClick,
                    QPointF(x, y),
                    QPointF(x, y),
                    QPointF(x + window->x(), y + window->y()),
                    static_cast<Qt::MouseButton>(button),
                    static_cast<Qt::MouseButtons>(buttons),
                    static_cast<Qt::KeyboardModifiers>(modifiers),
                    Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::postEvent(window, event);
    }
}

void ApplicationFunctions::injectMouseMove(int32_t x, int32_t y,
                                           int32_t button, int32_t buttons,
                                           int32_t modifiers)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QMouseEvent* event = new QMouseEvent(
                    QEvent::MouseMove,
                    QPointF(x, y),
                    QPointF(x, y),
                    QPointF(x + window->x(), y + window->y()),
                    static_cast<Qt::MouseButton>(button),
                    static_cast<Qt::MouseButtons>(buttons),
                    static_cast<Qt::KeyboardModifiers>(modifiers),
                    Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::postEvent(window, event);
    }
}

void ApplicationFunctions::injectKeyPress(int32_t key,
                                          int32_t modifiers,
                                          const QString& text,
                                          bool autoRep,
                                          int32_t count)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QWindow* window = QGuiApplication::focusWindow();
        QKeyEvent* event = new QKeyEvent(QEvent::KeyPress,
                                         key,
                                         static_cast<Qt::KeyboardModifiers>(modifiers),
                                         text,
                                         autoRep,
                                         count);
        QCoreApplication::postEvent(window, event);
    }
}
void ApplicationFunctions::injectKeyRelease(int32_t key,
                                            int32_t modifiers,
                                            const QString& text,
                                            bool autoRep,
                                            int32_t count)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QWindow* window = QGuiApplication::focusWindow();
        QKeyEvent* event = new QKeyEvent(QEvent::KeyRelease,
                                         key,
                                         static_cast<Qt::KeyboardModifiers>(modifiers),
                                         text,
                                         autoRep,
                                         count);
        QCoreApplication::postEvent(window, event);
    }
}

void ApplicationFunctions::injectWheel(int32_t x, int32_t y,
                                       int32_t pixelX, int32_t pixelY,
                                       int32_t angleX, int32_t angleY,
                                       int32_t buttons, int32_t modifiers,
                                       int32_t phase, bool inverted)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QWheelEvent* event = new QWheelEvent(
                    QPointF(x, y),
                    QPointF(x + window->x(), y + window->y()),
                    QPoint(pixelX, pixelY),
                    QPoint(angleX, angleY),
                    static_cast<Qt::MouseButtons>(buttons),
                    static_cast<Qt::KeyboardModifiers>(modifiers),
                    static_cast<Qt::ScrollPhase>(phase),
                    inverted,
                    Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::postEvent(window, event);
    }
}
