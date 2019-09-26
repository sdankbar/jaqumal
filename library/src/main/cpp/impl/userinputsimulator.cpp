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
#include "userinputsimulator.h"
#include <QKeyEvent>
#include <QWindow>
#include <QCoreApplication>
#include <QGuiApplication>

UserInputSimulator::UserInputSimulator(QObject *parent) :
    QObject(parent)
{
    // Empty Implementation
}

QString UserInputSimulator::keyToString(Qt::Key keyName,
                                        const Qt::KeyboardModifiers& modifiers,
                                        const QString& keyText) const
{
    if (!keyText.isEmpty())
    {
        return keyText;
    }
    else if (Qt::Key_A <= keyName && keyName <= Qt::Key_Z)
    {
        QChar c(keyName);
        if (modifiers.testFlag(Qt::KeyboardModifier::ShiftModifier))
        {
            return QString(c.toUpper());
        }
        else
        {
            return QString(c.toLower());
        }
    }
    else
    {
        return QString(QChar(keyName));
    }
}

void UserInputSimulator::keyPress(Qt::Key keyName,
                                  const Qt::KeyboardModifiers& modifiers,
                                  const QString& keyText)
{
    QKeyEvent* event = new QKeyEvent(QEvent::KeyPress, keyName, modifiers,
                                     keyToString(keyName, modifiers, keyText));
    QWindow* window = QGuiApplication::focusWindow();
    QCoreApplication::postEvent(window, event);
}

void UserInputSimulator::keyRelease(Qt::Key keyName,
                                    const Qt::KeyboardModifiers& modifiers,
                                    const QString& keyText)
{
    QKeyEvent* event = new QKeyEvent(QEvent::KeyRelease, keyName, modifiers,
                                     keyToString(keyName, modifiers, keyText));
    QWindow* window = QGuiApplication::focusWindow();
    QCoreApplication::postEvent(window, event);
}

void UserInputSimulator::keyClick(Qt::Key keyName,
                                  const Qt::KeyboardModifiers& modifiers,
                                  const QString& keyText)
{
    keyPress(keyName, modifiers, keyText);
    keyRelease(keyName, modifiers, keyText);
}
