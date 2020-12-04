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
#include "qmltest.h"
#include <QObject>
#include <QQmlEngine>
#include <QQmlContext>
#include <QtQuickTest>
#include <QStringList>
#include <iostream>

MockQMLLogging::MockQMLLogging(QObject* parent) :
    QObject(parent)
{
    // Empty Implementation
}

MockQMLLogging::~MockQMLLogging() {
    // Empty Implementation
}

void MockQMLLogging::trace(const QString& message) const
{
    std::cout << message.toStdString() << std::endl;
}
void MockQMLLogging::debug(const QString& message) const
{
    std::cout << message.toStdString() << std::endl;
}
void MockQMLLogging::MockQMLLogging::info(const QString& message) const
{
    std::cout << message.toStdString() << std::endl;
}
void MockQMLLogging::warn(const QString& message) const
{
    std::cerr << message.toStdString() << std::endl;
}
void MockQMLLogging::error(const QString& message) const
{
    std::cerr << message.toStdString() << std::endl;
}


MockUserInputSimulator::MockUserInputSimulator(QObject* parent) :
    QObject(parent)
{
    // Empty Implementation
}

MockUserInputSimulator::~MockUserInputSimulator() {
    // Empty Implementation
}

void MockUserInputSimulator::keyPress(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers, const QString& keyText)
{
    Q_UNUSED(keyName)
    Q_UNUSED(modifiers)
    Q_UNUSED(keyText)
}
void MockUserInputSimulator::keyRelease(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers, const QString& keyText)
{
    Q_UNUSED(keyName)
    Q_UNUSED(modifiers)
    Q_UNUSED(keyText)
}
void MockUserInputSimulator::keyClick(Qt::Key keyName, const Qt::KeyboardModifiers& modifiers, const QString& keyText)
{
    Q_UNUSED(keyName)
    Q_UNUSED(modifiers)
    Q_UNUSED(keyText)
}

MockEventBuilder::MockEventBuilder(QObject *parent)
{
    Q_UNUSED(parent)
}

QVariant MockEventBuilder::fireEvent(const QString& type)
{
    Q_UNUSED(type)
    return QVariant();
}
void MockEventBuilder::fireEvent(const QString& type, const QString& data)
{
    Q_UNUSED(type)
    Q_UNUSED(data)
}
void MockEventBuilder::addBoolean(bool data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addInteger(qint32 data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addLong(qint64 data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addFloat(float data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addDouble(double data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addString(const QString& data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addColor(const QColor& data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addRect(const QRect& data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addSize(const QSize& data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addDate(const QDateTime& data)
{
    Q_UNUSED(data)
}
void MockEventBuilder::addPoint(const QPoint& data)
{
    Q_UNUSED(data)
}


MockEventDispatcher::MockEventDispatcher(QObject *parent)
{
    Q_UNUSED(parent)
}
MockEventDispatcher::~MockEventDispatcher()
{
    // Empty Implementation
}

const QStringList& MockEventDispatcher::allowedEvents() const
{
    static const QStringList l;
    return l;
}

void MockEventDispatcher::setAllowedEvents(const QStringList& newAllowedEvents)
{
    Q_UNUSED(newAllowedEvents)
}


MockSetup::MockSetup() :
    m_logger(this),
    m_uiSim(this)
{
    static bool firstCall = true;
    if (firstCall)
    {
        qmlRegisterType<MockEventBuilder>("com.github.sdankbar.jaqumal", 0, 4, "EventBuilder");
        qmlRegisterType<MockEventDispatcher>("com.github.sdankbar.jaqumal", 0, 4, "EventDispatcher");
        firstCall = false;
    }
}

void MockSetup::qmlEngineAvailable(QQmlEngine *engine)
{
    engine->rootContext()->setContextProperty("log", QVariant::fromValue(&m_logger));
    engine->rootContext()->setContextProperty("userInputSim", QVariant::fromValue(&m_uiSim));
}

int runQMLTest(const QString& pathToQMLTestFile, std::vector<QString>& importPaths)
{
    QTEST_SET_MAIN_SOURCE_PATH;

    int argc = 3;
    if (!importPaths.empty())
    {
        argc = 4 + importPaths.size();
    }

    char** argv = new char*[argc];
    argv[0] = strdup("");
    argv[1] = strdup("-input");
    argv[2] = strdup(pathToQMLTestFile.toStdString().c_str());

    if (!importPaths.empty())
    {
        argv[3] = strdup("-import");
        for (size_t i = 0; i < importPaths.size(); ++i)
        {
            argv[4 + i] = strdup(importPaths[i].toStdString().c_str());
        }
    }

    MockSetup setup;
    int result = quick_test_main_with_setup(argc, argv, "QMLTests", QUICK_TEST_SOURCE_DIR, &setup);

    for (int i = 0; i < argc; ++i)
    {
        free(argv[i]);
    }
    delete[] argv;

    return result;
}
