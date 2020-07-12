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
