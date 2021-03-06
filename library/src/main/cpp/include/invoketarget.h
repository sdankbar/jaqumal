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
#pragma once

#include <QObject>
#include <QString>
#include <QVariant>
#include <QVector>

class InvokeTarget : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QString targetName READ targetName WRITE setTargetName NOTIFY targetNameChanged)
    Q_PROPERTY(QVariant returnValue READ returnValue WRITE setReturnValue)
public:

    static QVariant sendToTarget(const QString& name, const QVariantMap& args);

    explicit InvokeTarget(QObject *parent = nullptr);
    virtual ~InvokeTarget();

    const QString& targetName() const;
    void setTargetName(const QString& newName);

    const QVariant& returnValue() const;
    void setReturnValue(const QVariant& newReturnValue);

signals:
    void targetNameChanged();
    QVariant invoked(const QVariantMap& args);

private:

    static QVector<InvokeTarget*> allTargets;

    QVariant invoke(const QVariantMap& args);

    QString m_name;
    QVariant m_returnValue;
};
