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
#include "qmllogging.h"
#include <sstream>
#include <applicationfunctions.h>
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
            // Known error that can be safely ignored.  May one day determine how to unload libraries,
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
        switch (type) {
        case QtDebugMsg:
            std::cerr << "DEBUG: " << sstr.str() << std::endl;
            break;
        case QtInfoMsg:
            std::cerr << "INFO: " << sstr.str() << std::endl;
            break;
        case QtWarningMsg:
            std::cerr << "WARN: " << sstr.str() << std::endl;
            break;
        case QtCriticalMsg:
            std::cerr << "CRIT: " << sstr.str() << std::endl;
            break;
        case QtFatalMsg:
            std::cerr << "FATAL: " << sstr.str() << std::endl;
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
    switch (type) {
    case QtDebugMsg:
        debug(sstr.str());
        break;
    case QtInfoMsg:
        info(sstr.str());
        break;
    case QtWarningMsg:
        warn(sstr.str());
        break;
    case QtCriticalMsg:
        error(sstr.str());
        break;
    case QtFatalMsg:
        error("FATAL: " + sstr.str());
        break;
    }
}

void QMLLogging::trace(const QString& message) const
{
    trace(message.toStdString());
}
void QMLLogging::debug(const QString& message) const
{
    debug(message.toStdString());
}
void QMLLogging::info(const QString& message) const
{
    info(message.toStdString());
}
void QMLLogging::warn(const QString& message) const
{
    warn(message.toStdString());
}
void QMLLogging::error(const QString& message) const
{
    error(message.toStdString());
}

void QMLLogging::trace(const std::string& message) const
{
    ApplicationFunctions::invokeLoggingCallback(javaLoggingCallback, 0, message);
}
void QMLLogging::debug(const std::string& message) const
{
    ApplicationFunctions::invokeLoggingCallback(javaLoggingCallback, 1, message);
}
void QMLLogging::info(const std::string& message) const
{
    ApplicationFunctions::invokeLoggingCallback(javaLoggingCallback, 2, message);
}
void QMLLogging::warn(const std::string& message) const
{
    ApplicationFunctions::invokeLoggingCallback(javaLoggingCallback, 3, message);
}
void QMLLogging::error(const std::string& message) const
{
    ApplicationFunctions::invokeLoggingCallback(javaLoggingCallback, 4, message);
}

void QMLLogging::setCallback(jobject obj)
{
    javaLoggingCallback = obj;
}
