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
#include "keyeventpreprocessor.h"
#include <QEvent>
#include <QKeyEvent>
#include <QCoreApplication>

KeyEventPreProcessor::KeyEventPreProcessor(QObject* parent) :
    QObject(parent),
    m_target(nullptr),
    m_mode(DEFAULT)
{
    // Empty Implementation
}

QObject* KeyEventPreProcessor::target() const
{
    return m_target;
}

void KeyEventPreProcessor::setTarget(QObject* target)
{
    if (m_target != target)
    {
        if (m_target != nullptr)
        {
            m_target->removeEventFilter(this);
        }

        m_target = target;

        if (m_target != nullptr)
        {
            m_target->installEventFilter(this);
        }

        emit targetChanged();
    }
}

KeyEventPreProcessor::CaseMode KeyEventPreProcessor::mode() const
{
    return m_mode;
}

void KeyEventPreProcessor::setMode(CaseMode mode)
{
    if (m_mode != mode)
    {
        m_mode = mode;
        emit modeChanged();
    }
}

bool KeyEventPreProcessor::eventFilter(QObject* obj, QEvent* event)
{
    if (event->type() == QEvent::KeyPress || event->type() == QEvent::KeyRelease)
    {
        QKeyEvent* cast = dynamic_cast<QKeyEvent*>(event);
        if (m_mode == UPPER)
        {
            if (!cast->text().isUpper())
            {
                QKeyEvent* upperEvent = new QKeyEvent(cast->type(), cast->key(), cast->modifiers(),
                              cast->nativeScanCode(), cast->nativeVirtualKey(),
                              cast->nativeModifiers(), cast->text().toUpper(), cast->isAutoRepeat(),
                              cast->count());

                // Turn key event being sent into upper case letters
                QCoreApplication::postEvent(obj, upperEvent);
                return true;
            }
            else
            {
                // Is already upper case
                return false;
            }
        }
        else if (m_mode == LOWER)
        {
            if (!cast->text().isLower())
            {
                QKeyEvent* lowerEvent = new QKeyEvent(cast->type(), cast->key(), cast->modifiers(),
                                  cast->nativeScanCode(), cast->nativeVirtualKey(),
                                  cast->nativeModifiers(), cast->text().toLower(), cast->isAutoRepeat(),
                                  cast->count());

                // Turn key event being sent into lower case letters
                QCoreApplication::postEvent(obj, lowerEvent);
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    return QObject::eventFilter(obj, event);
}
