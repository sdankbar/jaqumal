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
#include "qmlinterface.h"
#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <jpolyline.h>
#include <QQmlContext>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontMetrics>
#include <QScreen>
#include <QImage>
#include <iostream>
#include <functional>
#include <applicationfunctions.h>

std::function<void(const char*)> exceptionHandler;

void addEventCallback(void* c(const char*, void*, int32_t))
{
    EventBuilder::addEventHandler(std::function<void*(const char*, void*, int32_t)>(c));
}

void addImageProvider(const char* id, void* c(const char*, int, int))
{
    if (ApplicationFunctions::check(nullptr))
    {
        //QMLLibrary::library->addImageProvider(
        //    QString(id),
        //    std::function<void* (const char*, int , int)>(c));
    }
}

void sendQMLEvent(const char* eventName, const char** keys, void* valuesPointer, int keyValuesCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        std::vector<QVariant> variants;// = toQVariantList(valuesPointer, static_cast<uint32_t>(keyValuesCount));
        QVariantMap map;
        for (uint32_t i = 0; i < static_cast<uint32_t>(keyValuesCount); ++i)
        {
            map.insert(QString(keys[i]), variants[i]);
        }

        EventDispatcher::sendToDispatchers(QString(eventName), map);
    }
}

void* getScreens()
{
    static char* retPtr = nullptr;
    if (ApplicationFunctions::check(nullptr))
    {
        /*QList<QScreen*> screens = QMLLibrary::library->getScreens();
        size_t bytesAllocated = 4u + static_cast<size_t>(screens.size()) * (8u + 4u * 4u);
        free(retPtr);
        retPtr = reinterpret_cast<char*>(malloc(bytesAllocated));
        memset(retPtr, 0, bytesAllocated);

        char* working = retPtr;
        putInt(working, screens.size());
        for (int i = 0; i < screens.size(); ++i)
        {
            putDouble(working, screens[i]->physicalDotsPerInch());

            QRect geo = screens[i]->geometry();
            putInt(working, geo.x());
            putInt(working, geo.y());
            putInt(working, geo.width());
            putInt(working, geo.height());
        }*/

        return retPtr;
    }
    else
    {
        return nullptr;
    }
}
