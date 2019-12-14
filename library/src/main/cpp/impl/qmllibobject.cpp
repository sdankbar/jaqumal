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
#include "qmllibobject.h"
#include <QQmlContext>
#include <QQuickWindow>
#include <iostream>
#include <csignal>

QMLLibrary* QMLLibrary::library = nullptr;

void signal_handler(int)
{
  if (QMLLibrary::library)
  {
      QMLLibrary::library->quitApplication();
  }
}

QMLLibrary::QMLLibrary(int32_t& argc, char** argv) :
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

QMLLibrary::~QMLLibrary()
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

void QMLLibrary::exec()
{
   m_qapp->exec();
}

void QMLLibrary::quitApplication()
{
   m_qapp->quit();
}

void QMLLibrary::invoke(Callback callback)
{
  callback();
}

GenericObjectModel* QMLLibrary::createGenericObjectModel(const QString& modelName, const std::vector<QString>& roles)
{
    GenericObjectModel* modelPtr = new GenericObjectModel(roles);
    m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
    return modelPtr;
}

GenericListModel* QMLLibrary::createGenericListModel(const QString& modelName, const QStringList& roleNames,
                                        const QVector<int>& indices)
{
    QHash<int, QByteArray> roleNameMap;
    for (int32_t i = 0; i < indices.size(); ++i)
    {
        roleNameMap[indices[i]] = roleNames[i].toStdString().c_str();
    }

    GenericListModel* modelPtr = new GenericListModel(roleNameMap);
    m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
    return modelPtr;
}


GenericFlatTreeModel* QMLLibrary::createGenericFlatTreeModel(const QString& modelName, const QStringList& roleNames,
                                        const QVector<int>& indices)
{
    QHash<int, QByteArray> roleNameMap;
    for (int32_t i = 0; i < indices.size(); ++i)
    {
        roleNameMap[indices[i]] = roleNames[i].toStdString().c_str();
    }

    GenericFlatTreeModel* modelPtr = new GenericFlatTreeModel(roleNameMap, 0);
    m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
    return modelPtr;
}

void QMLLibrary::loadQMLFile(const QString& filePath)
{
    m_qmlEngine->load(filePath);
}

void QMLLibrary::unloadQML()
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

void QMLLibrary::reloadQMLFile(const QString& filePath)
{
    unloadQML();
    loadQMLFile(filePath);
}

void QMLLibrary::setLoggingCallback(void c(int, const char*))
{
    m_logging.setCallback(c);
}
