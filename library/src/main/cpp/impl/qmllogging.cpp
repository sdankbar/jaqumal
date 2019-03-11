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
#include "qmllogging.h"
#include <sstream>
#include <iostream>

QMLLogging* loggingPtr = nullptr;

void staticQtMessages(QtMsgType type, const QMessageLogContext &context, const QString &msg)
{
    if (loggingPtr)
    {
        loggingPtr->qtMessages(type, context, msg);
    }
    else
    {
        std::stringstream sstr;

        if (msg == "QObject::~QObject: Timers cannot be stopped from another thread")
        {
            // Know error that can be safely ignored.  May one day determine how to unload libraries,
            // allowing static de-initialization to occur in the correct thread.
            return;
        }

        std::string fileStr;
        if (context.file)
        {
            fileStr = context.file;
        }
        std::string funcStr;
        if (context.function)
        {
            funcStr = context.function;
        }
        sstr << "[" << fileStr << ":" << context.line << ":" << funcStr << "] " << msg.toStdString();
        const std::string message = sstr.str();
        switch (type) {
        case QtDebugMsg:
            std::cerr << "DEBUG: " << message << std::endl;
            break;
        case QtInfoMsg:
            std::cerr << "INFO: " << message << std::endl;
            break;
        case QtWarningMsg:
            std::cerr << "WARN: " << message << std::endl;
            break;
        case QtCriticalMsg:
            std::cerr << "CRIT: " << message << std::endl;
            break;
        case QtFatalMsg:
            std::cerr << "FATAL: " << message << std::endl;
            break;
        }
    }
}

QMLLogging::QMLLogging(QObject *parent) : QObject(parent)
{
    loggingPtr = this;
    qInstallMessageHandler(&staticQtMessages);
}

QMLLogging::~QMLLogging()
{
    loggingPtr = nullptr;
}

void QMLLogging::qtMessages(QtMsgType type, const QMessageLogContext &context, const QString &msg)
{
    std::stringstream sstr;

    std::string fileStr;
    if (context.file)
    {
        fileStr = context.file;
    }
    std::string funcStr;
    if (context.function)
    {
        funcStr = context.function;
    }
    sstr << "[" << fileStr << ":" << context.line << ":" << funcStr << "] " << msg.toStdString();
    const std::string temp = sstr.str();
    const QString message = QString::fromStdString(temp);
    switch (type) {
    case QtDebugMsg:
        debug(message);
        break;
    case QtInfoMsg:
        info(message);
        break;
    case QtWarningMsg:
        warn(message);
        break;
    case QtCriticalMsg:
        error(message);
        break;
    case QtFatalMsg:
        error("FATAL: " + message);
        break;
    }
}

void QMLLogging::trace(const QString& message) const
{
    const std::string temp = message.toStdString();
    javaLoggingCallback(0, temp.c_str());
}
void QMLLogging::debug(const QString& message) const
{
    const std::string temp = message.toStdString();
    javaLoggingCallback(1, temp.c_str());
}
void QMLLogging::info(const QString& message) const
{
    const std::string temp = message.toStdString();
    javaLoggingCallback(2, temp.c_str());
}
void QMLLogging::warn(const QString& message) const
{
    const std::string temp = message.toStdString();
    javaLoggingCallback(3, temp.c_str());
}
void QMLLogging::error(const QString& message) const
{
    const std::string temp = message.toStdString();
    javaLoggingCallback(4, temp.c_str());
}

void QMLLogging::setCallback(void c(int, const char*))
{
    javaLoggingCallback = c;
}
