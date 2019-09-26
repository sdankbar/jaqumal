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

QString UserInputSimulator::keyToString(Qt::Key keyName, Qt::KeyboardModifiers modifiers) const
{
    if (Qt::Key_0 <= keyName && keyName <= Qt::Key_9)
    {
        return QString(QChar(keyName));
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
        switch (keyName) {
        case Qt::Key_Apostrophe:
            return "`";
        case Qt::Key_AsciiTilde:
            return "~";
        case Qt::Key_Exclam:
            return "!";
        case Qt::Key_At:
            return "@";
        case Qt::Key_NumberSign:
            return "#";
        case Qt::Key_Dollar:
            return "$";
        case Qt::Key_Percent:
            return "%";
        case Qt::Key_AsciiCircum:
            return "^";
        case Qt::Key_Ampersand:
            return "&";
        case Qt::Key_Asterisk:
            return "*";
        case Qt::Key_ParenLeft:
            return "(";
        case Qt::Key_ParenRight:
            return ")";
        case Qt::Key_Minus:
            return "-";
        case Qt::Key_Underscore:
            return "_";
        case Qt::Key_Equal:
            return "=";
        case Qt::Key_Plus:
            return "+";
        case Qt::Key_BracketLeft:
            return "[";
        case Qt::Key_BraceLeft:
            return "{";
        case Qt::Key_BracketRight:
            return "]";
        case Qt::Key_BraceRight:
            return "}";
        case Qt::Key_Backslash:
            return "\\";
        case Qt::Key_Bar:
            return "|";
        case Qt::Key_Semicolon:
            return ";";
        case Qt::Key_Colon:
            return ":";
        case Qt::Key_QuoteLeft:
            return "'";
        case Qt::Key_QuoteDbl:
            return "\"";
        case Qt::Key_Comma:
            return ",";
        case Qt::Key_Less:
            return "<";
        case Qt::Key_Period:
            return ".";
        case Qt::Key_Greater:
            return ">";
        case Qt::Key_Slash:
            return "/";
        case Qt::Key_Question:
            return "?";
        case Qt::Key_Space:
            return " ";
        default:
            return "";
        }
    }
}

void UserInputSimulator::keyPress(Qt::Key keyName, Qt::KeyboardModifiers modifiers)
{
    QKeyEvent* event = new QKeyEvent(QEvent::KeyPress, keyName, modifiers,
                                     keyToString(keyName, modifiers));
    QWindow* window = QGuiApplication::focusWindow();
    QCoreApplication::postEvent(window, event);
}

void UserInputSimulator::keyRelease(Qt::Key keyName, Qt::KeyboardModifiers modifiers)
{
    QKeyEvent* event = new QKeyEvent(QEvent::KeyRelease, keyName, modifiers,
                                     keyToString(keyName, modifiers));
    QWindow* window = QGuiApplication::focusWindow();
    QCoreApplication::postEvent(window, event);
}

void UserInputSimulator::keyClick(Qt::Key keyName, Qt::KeyboardModifiers modifiers)
{
    keyPress(keyName, modifiers);
    keyRelease(keyName, modifiers);
}
