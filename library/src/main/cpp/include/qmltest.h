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

#include <QObject>
#include <QString>
#include <QVariant>

extern "C"
{
extern int runQMLTest(const char* pathToQMLTestFile);
}

class QQmlEngine;

class MockQMLLogging : public QObject
{
    Q_OBJECT
public:

    MockQMLLogging(QObject* parent) ;

    virtual ~MockQMLLogging();

    Q_INVOKABLE void trace(const QString& message) const;
    Q_INVOKABLE void debug(const QString& message) const;
    Q_INVOKABLE void info(const QString& message) const;
    Q_INVOKABLE void warn(const QString& message) const;
    Q_INVOKABLE void error(const QString& message) const;
};

class MockUserInputSimulator : public QObject
{
    Q_OBJECT
public:

    MockUserInputSimulator(QObject* parent);

    virtual ~MockUserInputSimulator();

    Q_INVOKABLE void keyPress(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers = Qt::NoModifier, const QString& keyText = QString());
    Q_INVOKABLE void keyRelease(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers = Qt::NoModifier, const QString& keyText = QString());
    Q_INVOKABLE void keyClick(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers = Qt::NoModifier, const QString& keyText = QString());
};

class MockEventBuilder : public QObject
{
    Q_OBJECT
public:

    explicit MockEventBuilder(QObject *parent = nullptr);

    Q_INVOKABLE QVariant fireEvent(const QString& type);
    Q_INVOKABLE void fireEvent(const QString& type, const QString& data);

    Q_INVOKABLE void addBoolean(bool data);
    Q_INVOKABLE void addInteger(qint32 data);
    Q_INVOKABLE void addLong(qint64 data);
    Q_INVOKABLE void addFloat(float data);
    Q_INVOKABLE void addDouble(double data);
    Q_INVOKABLE void addString(const QString& data);
    Q_INVOKABLE void addColor(const QColor& data);
    Q_INVOKABLE void addRect(const QRect& data);
    Q_INVOKABLE void addSize(const QSize& data);
    Q_INVOKABLE void addDate(const QDateTime& data);
    Q_INVOKABLE void addPoint(const QPoint& data);
};

class MockEventDispatcher : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QStringList allowedEvents READ allowedEvents WRITE setAllowedEvents NOTIFY allowedEventsChanged)
public:

    explicit MockEventDispatcher(QObject *parent = nullptr);
    virtual ~MockEventDispatcher();

    const QStringList& allowedEvents() const;
    void setAllowedEvents(const QStringList& newAllowedEvents);

signals:
    void allowedEventsChanged();
    void eventReceived(const QString& name, const QVariantMap& args);
};

class MockSetup : public QObject
{
    Q_OBJECT

public:
    MockSetup() ;

public slots:
    void qmlEngineAvailable(QQmlEngine* engine);

private:
    MockQMLLogging m_logger;
    MockUserInputSimulator m_uiSim;
};
