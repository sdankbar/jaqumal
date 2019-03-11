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
#include "eventbuilder.h"
#include "iostream"
#include <QColor>

std::vector<std::function<void(const char*, void*, int32_t)> > EventBuilder::EVENT_HANDLERS;

void EventBuilder::addEventHandler(std::function<void(const char*, void*, int32_t)> f)
{
    EVENT_HANDLERS.push_back(f);
}

EventBuilder::EventBuilder(QObject* parent) :
    QObject(parent)
{
    // Empty Implementation
}

void EventBuilder::fireEvent(const QString& type)
{
    if (!EVENT_HANDLERS.empty())
    {
        uint32_t size = m_queuedArguements.size();
        char* memory = new char[size];
        for (uint32_t i = 0; i < size; ++i)
        {
            memory[i] = m_queuedArguements[i];
        }
        for (auto& func: EVENT_HANDLERS)
        {
            func(type.toStdString().c_str(), memory, size);
        }
        delete memory;
    }
    else
    {
        std::cout << "No event handler registered" << std::endl;
    }
    m_queuedArguements.clear();
}

void EventBuilder::fireEvent(const QString& type, const QString& data)
{
    addString(data);
    fireEvent(type);
}

void EventBuilder::mouseClickEvent(const QString& objectName, int32_t x, int32_t y, int32_t button, int32_t buttons, int32_t modifiers, bool wasHeld)
{
    addString(objectName);
    addInteger(x);
    addInteger(y);
    addInteger(button);
    addInteger(buttons);
    addInteger(modifiers);
    addBoolean(wasHeld);
    fireEvent(QStringLiteral("Builtin-MouseClick"));
}

void EventBuilder::mouseWheelEvent(const QString& objectName, qint32 angleDeltaX, qint32 angleDeltaY,
                                   qint32 buttons, qint32 modifiers, qint32 x, qint32 y)
{
    addString(objectName);
    addInteger(angleDeltaX);
    addInteger(angleDeltaY);
    addInteger(buttons);
    addInteger(modifiers);
    addInteger(x);
    addInteger(y);
    fireEvent(QStringLiteral("Builtin-MouseWheel"));
}

void EventBuilder::textInputAcceptedEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent("Builtin-TextInputAccepted");
}
void EventBuilder::textInputEditingFinishedEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent("Builtin-TextInputEditingFinished");
}

void EventBuilder::perfEvent(PerfEventType t)
{
    addInteger(t);
    fireEvent("Builtin-PerformanceEvent");
}

void EventBuilder::buttonClickEvent(const QString& objectName)
{
    addString(objectName);
    fireEvent(QStringLiteral("Builtin-ButtonClick"));
}

void EventBuilder::addBoolean(bool data)
{
    m_queuedArguements.push_back(data);
}
void EventBuilder::addInteger(qint32 data)
{
    const uint32_t size = sizeof(data);
    char* ptr = (char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addLong(qint64 data)
{
    const uint32_t size = sizeof(data);
    char* ptr = (char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addFloat(float data)
{
    const uint32_t size = sizeof(data);
    char* ptr = (char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addDouble(double data)
{
    const uint32_t size = sizeof(data);
    char* ptr = (char*)(&data);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
void EventBuilder::addString(const QString& data)
{
    std::string str = data.toStdString();
    for (uint32_t i = 0; i < str.length(); ++i)
    {
        m_queuedArguements.push_back(str[i]);
    }
    m_queuedArguements.push_back(0);
}
void EventBuilder::addColor(const QColor& data)
{
    qint32 argb = data.rgba();
    const uint32_t size = sizeof(argb);
    char* ptr = (char*)(&argb);
    for (uint32_t i = 0; i < size; ++i)
    {
        char byte = ptr[i];
        m_queuedArguements.push_back(byte);
    }
}
