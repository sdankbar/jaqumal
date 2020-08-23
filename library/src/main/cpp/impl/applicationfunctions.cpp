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

#include "qmlimageprovider.h"
#include <QQmlContext>
#include <QQuickWindow>
#include <iostream>
#include <csignal>

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
    ApplicationFunctions::deleteSingleton();
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
    // TODO
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

}
JNICALL void addImageProvider(JNIEnv* env, jclass, jstring id, jobject c)
{

}
JNICALL jobjectArray getScreens(JNIEnv* env, jclass)
{

}
JNICALL void invoke(JNIEnv* env, jclass, jobject callback)
{

}
JNICALL void invokeWithDelay(JNIEnv* env, jclass, jobject callback, jint delayMilli)
{

}

void ApplicationFunctions::create(int* argc, char** argv)
{
    // TODO
    //qRegisterMetaType<Callback>();

    qmlRegisterType<EventBuilder>("com.github.sdankbar.jaqumal", 0, 4, "EventBuilder");
    qmlRegisterType<EventDispatcher>("com.github.sdankbar.jaqumal", 0, 4, "EventDispatcher");
    //qmlRegisterUncreatableType<GenericListModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericListModel", "Cannot create GenericListModel");
    //qmlRegisterUncreatableType<GenericFlatTreeModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericFlatTreeModel", "Cannot create GenericFlatTreeModel");
    //qmlRegisterUncreatableType<GenericObjectModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericObjectModel", "Cannot create GenericObjectModel");
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

void ApplicationFunctions::initialize(JNIEnv* env)
{
    // TODO
    JNINativeMethod methods[] = {
        /*JNIUtilities::createJNIMethod("setInteger",    "(II)V",    (void *)&setInteger),
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
        JNIUtilities::createJNIMethod("setPolyline",    "(I[DI)V",    (void *)&setPolyline),*/
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/");
    env->RegisterNatives(javaClass, methods, sizeof(methods));
    env->DeleteLocalRef(javaClass);
}

void ApplicationFunctions::uninitialize(JNIEnv*)
{

}

ApplicationFunctions* ApplicationFunctions::SINGLETON = nullptr;

void signal_handler(int)
{
  if (ApplicationFunctions::get())
  {
      ApplicationFunctions::get()->quitApplication();
  }
}

ApplicationFunctions::ApplicationFunctions(int32_t argc, char** argv) :
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

//void ApplicationFunctions::invoke(Callback callback)
//{
//  callback();
//}

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

void ApplicationFunctions::setLoggingCallback(void c(int, const char*))
{
    m_logging.setCallback(c);
}

void ApplicationFunctions::addImageProvider(const QString& id, std::function<void* (const char*, int, int)> javaImageProviderCallback)
{
    m_qmlEngine->addImageProvider(id, new QMLImageProvider(javaImageProviderCallback));
}

QList<QScreen*> ApplicationFunctions::getScreens()
{
    return m_qapp->screens();
}
