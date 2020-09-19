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
#include "invoketarget.h"

QVector<InvokeTarget*> InvokeTarget::allTargets;

QVariant InvokeTarget::sendToTarget(const QString& name, const QVariantMap& args)
{
    QVariant ret;
    for (InvokeTarget* d: allTargets)
    {
        if (d->targetName() == name)
        {
            ret = d->invoke(args);
        }
    }
    return ret;
}

InvokeTarget::InvokeTarget(QObject* parent) :
    QObject(parent)
{
    allTargets.push_back(this);
}

InvokeTarget::~InvokeTarget()
{
    allTargets.removeAll(this);
}

const QString& InvokeTarget::targetName() const
{
    return m_name;
}

void InvokeTarget::setTargetName(const QString &newName)
{
    if (m_name != newName)
    {
        m_name = newName;
        emit targetNameChanged();
    }
}

QVariant InvokeTarget::invoke(const QVariantMap& args)
{
    emit invoked(args);
    // TODO
    return QVariant();
}

