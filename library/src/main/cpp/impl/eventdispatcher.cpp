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
#include "eventdispatcher.h"

QSet<EventDispatcher*> EventDispatcher::allDispatchers;

void EventDispatcher::sendToDispatchers(const QString& eventName, const QVariantMap& args)
{
    for (EventDispatcher* d: allDispatchers)
    {
        d->sendTo(eventName, args);
    }
}

EventDispatcher::EventDispatcher(QObject* parent) :
    QObject(parent)
{
    allDispatchers.insert(this);
}

EventDispatcher::~EventDispatcher()
{
    allDispatchers.remove(this);
}

const QStringList& EventDispatcher::allowedEvents() const
{
    return m_allowedEvents;
}

void EventDispatcher::setAllowedEvents(const QStringList& newAllowedEvents)
{
    if (m_allowedEvents != newAllowedEvents)
    {
        m_allowedEvents = newAllowedEvents;
        m_fastAllowedEvents = QSet(newAllowedEvents.begin(), newAllowedEvents.end());
        emit allowedEventsChanged();
    }
}

void EventDispatcher::sendTo(const QString& eventName, const QVariantMap& args)
{
    if (m_fastAllowedEvents.contains(eventName))
    {
        emit eventReceived(eventName, args);
    }
}
