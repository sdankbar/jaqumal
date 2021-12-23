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
#include "eventlogger.h"
#include <QEvent>
#include <QKeyEvent>
#include <QMouseEvent>
#include <QTouchEvent>
#include <sstream>

EventLogger::EventLogger(QMLLogging& log, QObject* parent) :
    QObject(parent),
    m_log(log)
{
    m_log.info(QStringLiteral("Enabled QEvent logging"));
}

bool EventLogger::eventFilter(QObject* obj, QEvent* event)
{
    switch (event->type()) {
    case QEvent::KeyPress:
    {
        QKeyEvent* key = static_cast<QKeyEvent*>(event);
        m_log.info(QString("KeyPress = 0x%1 %2").arg(key->key(), 0, 16).arg(key->modifiers().operator unsigned int(), 0, 2));
        break;
    }
    case QEvent::KeyRelease:
    {
        QKeyEvent* key = static_cast<QKeyEvent*>(event);
        m_log.info(QString("KeyRelease = 0x%1 %2").arg(key->key(), 0, 16).arg(key->modifiers().operator unsigned int(), 0, 2));
        break;
    }
    case QEvent::MouseButtonPress:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        m_log.info(QString("MouseButtonPress btn=%1 x=%2 y=%3").arg(mouse->button()).arg(mouse->x()).arg(mouse->y()));
        break;
    }
    case QEvent::MouseButtonDblClick:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        m_log.info(QString("MouseButtonDblClick btn=%1 x=%2 y=%3").arg(mouse->button()).arg(mouse->x()).arg(mouse->y()));
        break;
    }
    case QEvent::MouseButtonRelease:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        m_log.info(QString("MouseButtonRelease btn=%1 x=%2 y=%3").arg(mouse->button()).arg(mouse->x()).arg(mouse->y()));
        break;
    }
    case QEvent::Wheel:
    {
        QWheelEvent* mouse = static_cast<QWheelEvent*>(event);
        m_log.info(QString("Wheel x=%1 y=%2 angleX=%3 angleY=%4").
                   arg(mouse->x()).
                   arg(mouse->y()).
                   arg(mouse->angleDelta().x()).
                   arg(mouse->angleDelta().y()));
        break;
    }
    case QEvent::TouchBegin:
    {
        QTouchEvent* touch = static_cast<QTouchEvent*>(event);
        std::stringstream str;
        for (const QTouchEvent::TouchPoint& p : touch->touchPoints())
        {
            str << p.id() << "=(" << p.pos().x() << ", " << p.pos().y() << ") ";
        }
        m_log.info(QString("Touch begin points=%1").arg(QString::fromStdString(str.str())));
        break;
    }
    case QEvent::TouchEnd:
    {
        QTouchEvent* touch = static_cast<QTouchEvent*>(event);
        std::stringstream str;
        for (const QTouchEvent::TouchPoint& p : touch->touchPoints())
        {
            str << p.id() << "=(" << p.pos().x() << ", " << p.pos().y() << ") ";
        }
        m_log.info(QString("Touch end points=%1").arg(QString::fromStdString(str.str())));
        break;
    }
    case QEvent::TouchUpdate:
    {
        QTouchEvent* touch = static_cast<QTouchEvent*>(event);
        std::stringstream str;
        for (const QTouchEvent::TouchPoint& p : touch->touchPoints())
        {
            str << p.id() << "=(" << p.pos().x() << ", " << p.pos().y() << ") ";
        }
        m_log.info(QString("Touch update points=%1").arg(QString::fromStdString(str.str())));
        break;
    }
    case QEvent::TouchCancel:
    {
        m_log.info(QString("Touch cancel"));
        break;
    }
    //case QEvent::MouseMove: {
    // TODO enable after providing a way to control
    // QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
    // m_log.info(QString("MouseMove x=%2 y=%3").arg(mouse->x()).arg(mouse->y()));
    // break;
    //}
    default:
        // Ignore
        break;
    }

    return QObject::eventFilter(obj, event);
}
