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
#ifndef QMLLIBOBJECT_H
#define QMLLIBOBJECT_H

#include <map>
#include <memory>
#include <string>
#include <QApplication>
#include <QStandardItemModel>
#include <QQmlApplicationEngine>
#include <genericobjectmodel.h>
#include <genericlistmodel.h>
#include <genericflattreemodel.h>
#include <qmllogging.h>
#include <userinputsimulator.h>

typedef std::function<void()> Callback;
Q_DECLARE_METATYPE(Callback)

class QMLLibrary : public QObject
{
    Q_OBJECT
public:
    static QMLLibrary* library;

    QMLLibrary(int32_t& argc, char** argv);
    virtual ~QMLLibrary();

    void exec();

    Q_INVOKABLE void quitApplication();

    Q_INVOKABLE void invoke(Callback callback);

    Q_INVOKABLE void loadQMLFile(const QString& filePath);
    Q_INVOKABLE void unloadQML();
    Q_INVOKABLE void reloadQMLFile(const QString& filePath);

    // Create ObjectModel
    GenericObjectModel* createGenericObjectModel(const QString& modelName, const std::vector<QString>& roles);

    // Create ListModel
    GenericListModel* createGenericListModel(
            const QString& modelName, const QStringList& roleNames, const QVector<int>& indices);

    // Create FlatTreeModel
    GenericFlatTreeModel* createGenericFlatTreeModel(
            const QString& modelName, const QStringList& roleNames, const QVector<int>& indices);

    void setLoggingCallback(void c(int, const char*));

private:

    QApplication* m_qapp;
    QMLLogging m_logging;

    QQmlApplicationEngine* m_qmlEngine;

    UserInputSimulator m_uiSim;
};



#endif // QMLLIBOBJECT_H
