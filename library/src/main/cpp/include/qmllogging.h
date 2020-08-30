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

#include <jni.h>
#include <QObject>

class QMLLogging : public QObject
{
    Q_OBJECT
public:
    explicit QMLLogging(QObject *parent = nullptr);
    virtual ~QMLLogging();

    void qtMessages(QtMsgType type, const QMessageLogContext &context, const QString &msg);

    Q_INVOKABLE void trace(const std::string& message) const;
    Q_INVOKABLE void debug(const std::string& message) const;
    Q_INVOKABLE void info(const std::string& message) const;
    Q_INVOKABLE void warn(const std::string& message) const;
    Q_INVOKABLE void error(const std::string& message) const;

    void setCallback(jobject obj);

private:

    jobject javaLoggingCallback;
};
