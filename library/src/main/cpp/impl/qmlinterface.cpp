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
#include "qmllibobject.h"
#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <QQmlContext>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontMetrics>
#include <QScreen>
#include <iostream>
#include <functional>

std::function<void(const char*)> exceptionHandler;
static char* cppToJavaMem;
static int32_t cppToJavaMemLength;
namespace
{
bool checkQMLLibrary()
{
    if (QMLLibrary::library != nullptr)
    {
        return true;
    }
    else
    {
        exceptionHandler("Attempted to use QApplication before QApplication was created");
        return false;
    }
}

void putByte(char*& ptr, char byte)
{
    *ptr = byte;
    ptr = ptr + 1;
}

void putInt(char*& ptr, int32_t integer)
{
    *(reinterpret_cast<int*>(ptr)) = integer;
    ptr = ptr + 4;
}

void putDouble(char*& ptr, double real)
{
    *(reinterpret_cast<double*>(ptr)) = real;
    ptr = ptr + 8;
}
}

void createQApplication(int32_t argc, char** argv)
{
    int* argcCopy = new int;
    *argcCopy = argc;
    char** argvCopy = new char*[argc];
    for (int i = 0; i < argc; ++i)
    {
        argvCopy[i] = strdup(argv[i]);
    }

    qRegisterMetaType<Callback>();

    qmlRegisterType<EventBuilder>("com.github.sdankbar.jaqumal", 0, 4, "EventBuilder");
    qmlRegisterType<EventDispatcher>("com.github.sdankbar.jaqumal", 0, 4, "EventDispatcher");
    qmlRegisterUncreatableType<GenericListModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericListModel", "Cannot create GenericListModel");
    qmlRegisterUncreatableType<GenericFlatTreeModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericFlatTreeModel", "Cannot create GenericFlatTreeModel");
    qmlRegisterUncreatableType<GenericObjectModel>("com.github.sdankbar.jaqumal", 0, 4, "GenericObjectModel", "Cannot create GenericObjectModel");

    QMLLibrary::library = new QMLLibrary(*argcCopy, argvCopy);
}

void execQApplication() {
    if (checkQMLLibrary())
    {
        QMLLibrary::library->exec();
    }
}

void deleteQApplication()
{
    delete QMLLibrary::library;
    QMLLibrary::library = nullptr;
}

void quitQApplication()
{
    if (checkQMLLibrary())
    {
        QMLLibrary::library->quitApplication();
    }
}

void loadQMLFile(const char* fileName)
{
    if (checkQMLLibrary())
    {
        QString filePath(fileName);
        QMLLibrary::library->loadQMLFile(filePath);
    }
}

void unloadQML()
{
    if (checkQMLLibrary())
    {
        QMLLibrary::library->unloadQML();
    }
}

void reloadQMLFile(const char* fileName)
{
    if (checkQMLLibrary())
    {
        QString filePath(fileName);
        QMLLibrary::library->reloadQMLFile(filePath);
    }
}

void addEventCallback(void c(const char*, void*, int32_t))
{
    EventBuilder::addEventHandler(std::function<void(const char*, void*, int32_t)>(c));
}

void invoke(void c())
{
    if (checkQMLLibrary())
    {
        Callback callback(c);
        QMLLibrary::library->metaObject()->invokeMethod(QMLLibrary::library, "invoke", Qt::QueuedConnection,
                                                        Q_ARG(Callback, callback));
    }
}

void invokeWithDelay(void c(), int32_t milliseconds)
{
    QTimer::singleShot(milliseconds, c);
}

void setLoggingCallback(void c(int, const char*))
{
    if (checkQMLLibrary())
    {
        QMLLibrary::library->setLoggingCallback(c);
    }
}

void setExceptionCallback(void c(const char*))
{
    exceptionHandler = c;
}

void addImageProvider(const char* id, void* c(const char*, int, int))
{
    if (checkQMLLibrary())
    {
        QMLLibrary::library->addImageProvider(
            QString(id), 
            std::function<void* (const char*, int , int)>(c));
    }
}

void sendQMLEvent(const char* eventName, const char** keys, void* valuesPointer, int keyValuesCount)
{
    if (checkQMLLibrary())
    {
        std::vector<QVariant> variants = toQVariantList(valuesPointer, static_cast<uint32_t>(keyValuesCount));
        QVariantMap map;
        for (uint32_t i = 0; i < static_cast<uint32_t>(keyValuesCount); ++i)
        {
            map.insert(QString(keys[i]), variants[i]);
        }

        EventDispatcher::sendToDispatchers(QString(eventName), map);
    }
}

void cleanupMemory(void* ptr)
{
    delete static_cast<unsigned char*>(ptr);
}

void setSharedMemory(char* cppToJava, int32_t length)
{
    cppToJavaMem = cppToJava;
    cppToJavaMemLength = length;
}

const char* getQFontToString(const char* family, int pointSize, int pixelSize, bool bold, bool italic, bool overline,
                             bool strikeout, bool underline, bool fixedPitch, bool kerning, int fontWeight,
                             double wordSpacing, double letteringSpacing, int letterSpacingType, int capitalization,
                             int hintingPreference, int stretch, int style, const char* styleName, int styleHint, int styleStrategy)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f(family);
        if (pointSize > 0) {
            f.setPointSize(pointSize);
        } else if (pixelSize > 0) {
            f.setPixelSize(pixelSize);
        }

        f.setBold(bold);
        f.setFixedPitch(fixedPitch);
        f.setItalic(italic);
        f.setKerning(kerning);
        f.setOverline(overline);
        f.setStrikeOut(strikeout);
        f.setUnderline(underline);
        f.setWeight(fontWeight);

        f.setWordSpacing(wordSpacing);
        f.setLetterSpacing(static_cast<QFont::SpacingType>(letterSpacingType), letteringSpacing);

        f.setCapitalization(static_cast<QFont::Capitalization>(capitalization));
        f.setHintingPreference(static_cast<QFont::HintingPreference>(hintingPreference));
        f.setStretch(stretch);
        f.setStyle(static_cast<QFont::Style>(style));
        f.setStyleName(QString(styleName));
        f.setStyleHint(static_cast<QFont::StyleHint>(styleHint), static_cast<QFont::StyleStrategy>(styleStrategy));

        static std::string ret;
        ret = f.toString().toStdString();
        return ret.c_str();
    }
    else
    {
        return "";
    }
}

extern const char* getQFontInfo(const char* fontToString)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        static std::string ret;
        QFont f;
        f.fromString(QString(fontToString));
        const QFontInfo info(f);
        //  0         1         2         3        4          5           6      7        8     9             10       11
        // Family, pointSize, pixelSize, bold, exactMatch, fixedPitch, italic, rawMode, style, styleHint, styleName, weight

        const QString temp("%1,%2,%3,%4,%5,%6,%7,%8,%9,%10,%11,%12");
        ret = temp.arg(info.family()).arg(info.pointSize()).arg(info.pixelSize())
                .arg(info.bold())
                .arg(info.exactMatch())
                .arg(info.fixedPitch())
                .arg(info.italic())
                .arg(info.rawMode())
                .arg(info.style())
                .arg(info.styleHint())
                .arg(info.styleName())
                .arg(info.weight()).toStdString();

        return ret.c_str();
    }
    else
    {
        return "";
    }
}

const char* getQFontMetrics(const char* fontToString)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        static std::string ret;
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        //  0           1             2      3      4          5          6           7               8            9           10         11          12
        // ascent,averageCharWidth,descent,height,leading,lineSpacing,maxWidth,minLeftBearing,minRightBearing,overLinePos,strikeOutPos,underlinePos,xheight

        const QString temp("%1,%2,%3,%4,%5,%6,%7,%8,%9,%10,%11,%12,%13");
        ret = temp.arg(metrics.ascent())
                .arg(metrics.averageCharWidth())
                .arg(metrics.descent())
                .arg(metrics.height())
                .arg(metrics.leading())
                .arg(metrics.lineSpacing())
                .arg(metrics.maxWidth())
                .arg(metrics.minLeftBearing())
                .arg(metrics.minRightBearing())
                .arg(metrics.overlinePos())
                .arg(metrics.strikeOutPos())
                .arg(metrics.underlinePos())
                .arg(metrics.xHeight())
                .toStdString();
        return ret.c_str();
    }
    else
    {
        return "";
    }
}
void* getBoundingRect(const char* fontToString, const char* text)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        QRect rect = metrics.boundingRect(QString(text));

        static int ret[4];
        ret[0] = rect.x();
        ret[1] = rect.y();
        ret[2] = rect.width();
        ret[3] = rect.height();
        return ret;
    }
    else
    {
        return nullptr;
    }
}
void* getBoundingRect2(const char* fontToString, int x, int y, int w, int h, int alignFlags, int textFlags,
            const char* text)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        const QRect bounds(x, y, w, h);
        QRect rect = metrics.boundingRect(bounds, alignFlags | textFlags, QString(text));

        static int ret[4];
        ret[0] = rect.x();
        ret[1] = rect.y();
        ret[2] = rect.width();
        ret[3] = rect.height();
        return ret;
    }
    else
    {
        return nullptr;
    }
}
void* getTightBoundingRect(const char* fontToString, const char* text)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        QRect rect = metrics.tightBoundingRect(QString(text));

        static int ret[4];
        ret[0] = rect.x();
        ret[1] = rect.y();
        ret[2] = rect.width();
        ret[3] = rect.height();
        return ret;
    }
    else
    {
        return nullptr;
    }
}
int getStringWidth(const char* fontToString, const char* text)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        return metrics.width(QString(text));
    }
    else
    {
        return -1;
    }
}
bool inFont(const char* fontToString, const int character)
{
    if (checkQMLLibrary()) // QTBUG-27024
    {
        QFont f;
        f.fromString(QString(fontToString));
        const QFontMetrics metrics(f);
        return metrics.inFont(static_cast<QChar>(character));
    }
    else
    {
        return false;
    }
}

void* getScreens()
{
    static char* retPtr = nullptr;
    if (checkQMLLibrary())
    {
        QList<QScreen*> screens = QMLLibrary::library->getScreens();
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
        }

        return retPtr;
    }
    else
    {
        return nullptr;
    }
}

std::vector<QVariant> toQVariantList(void* data, uint32_t count)
{
    std::vector<QVariant> vec;
    vec.reserve(count);
    int32_t offset = 0;
    for (uint32_t i = 0; i < count; ++i)
    {
        int32_t size;
        vec.push_back(toQVariant((static_cast<char*>(data)) + offset, size));
        if (size > 0)
        {
            offset += size;
        }
        else
        {
            break;
        }
    }
    return vec;
}

QVariant toQVariant(void* data, int32_t& size)
{
    char* buffer = static_cast<char*>(data);
    Type t = (Type) *((char*)data);
    switch (t) {
    case BOOL: {
        size = 1 + 1;
        return QVariant(*(bool*)(buffer + 1));
    }
    case BYTE_ARRAY: {
        int32_t length = *((int32_t*)(buffer + 1));
        size = 1 + 4 + length;
        QByteArray array((char*)(buffer + 5), length);
        return QVariant(array);
    }
    case COLOR: {
        int32_t argb = *((int32_t*)(buffer + 1));
        size = 1 + 4;
        return QVariant(QColor::fromRgba(argb));
    }
    case DATE_TIME: {
        int64_t seconds = *((int64_t*)(buffer + 1));
        int32_t nanos = *((int32_t*)(buffer + 9));
        size = 1 + 8 + 4;
        return QVariant(QDateTime::fromMSecsSinceEpoch(seconds * 1000 + nanos / 1000000));
    }
    case DOUBLE: {
         double v = *((double*)(buffer + 1));
         size = 1 + 8;
         return QVariant(v);
    }
    case FLOAT: {
        float v = *((float*)(buffer + 1));
        size = 1 + 4;
        return QVariant(v);
    }
    case IMAGE: {
        int32_t width = *((int32_t*)(buffer + 1));
        int32_t height = *((int32_t*)(buffer + 5));
        int32_t copyLength =width * height * 4;
        unsigned char* copy = new unsigned char[copyLength];
        memcpy(copy, buffer + 9, copyLength);
        QImage image(copy, width, height, QImage::Format_ARGB32, &cleanupMemory);
        size = 1 + 4 + 4 + copyLength;
        return QVariant(image);
    }
    case INT: {
        int32_t v = *((int32_t*)(buffer + 1));
        size = 1 + 4;
        return QVariant(v);
    }
    case LINE: {
        double x1 = *((double*)(buffer + 1));
        double y1 = *((double*)(buffer + 9));
        double x2 = *((double*)(buffer + 17));
        double y2 = *((double*)(buffer + 25));
        size = 1 + 8 + 8 + 8 + 8;
        return QVariant(QLine((int32_t)x1, (int32_t)y1, (int32_t)x2, (int32_t)y2));
    }
    case LONG: {
        qint64 v = *((qint64*)(buffer + 1));
        size = 1 + 8;
        return QVariant(v);
    }
    case POINT: {
        double x1 = *((double*)(buffer + 1));
        double y1 = *((double*)(buffer + 9));
        size = 1 + 8 + 8;
        return QVariant(QPoint((int32_t)x1, (int32_t)y1));
    }
    case RECTANGLE: {
        double x1 = *((double*)(buffer + 1));
        double y1 = *((double*)(buffer + 9));
        double w = *((double*)(buffer + 17));
        double h = *((double*)(buffer + 25));
        size = 1 + 8 + 8 + 8 + 8;
        return QVariant(QRect((int32_t)x1, (int32_t)y1, (int32_t)w, (int32_t) h));
    }
    case REGULAR_EXPRESSION: {
        int32_t length = *((int32_t*)(buffer + 1));
        QString str= QString::fromUtf8(buffer + 5, length);
        size = 1 + 4 + length;
        return QVariant(QRegExp(str));
    }
    case SIZE: {
        double w = *((double*)(buffer + 1));
        double h = *((double*)(buffer + 9));
        size = 1 + 8 + 8;
        return QVariant(QSize((int32_t)w, (int32_t)h));
    }
    case STRING: {
        int32_t length = *((int32_t*)(buffer + 1));
        QString str = QString::fromUtf8(buffer + 5, length);
        size = 1 + 4 + length;
        return QVariant(str);
    }
    case URL: {
        int32_t length = *((int32_t*)(buffer + 1));
        QString str = QString::fromUtf8(buffer + 5, length);
        size = 1 + 4 + length;
        return QVariant(QUrl(str));
    }
    case UUID: {
        int32_t length = *((int32_t*)(buffer + 1));
        QString str = QString::fromUtf8(buffer + 5, length);
        size = 1 + 4 + length;
        return QVariant(QUuid(str));
    }
    case FONT: {
        if (checkQMLLibrary()) // QTBUG-27024
        {
            int32_t length = *((int32_t*)(buffer + 1));
            QString str = QString::fromUtf8(buffer + 5, length);
            size = 1 + 4 + length;
            QFont f;
            f.fromString(str);
            return QVariant(f);
        }
        else
        {
            size = 0;
            return QVariant();
        }
    }
    case POLYLINE: {
        int32_t length = *((int32_t*)(buffer + 1));
        QVariantList polygon;
        polygon.reserve(length);
        double* workingPtr = (double*)(buffer + 1 + 4);
        for (int32_t i = 0; i < length; ++i)
        {
            double x = *workingPtr;
            double y = *(workingPtr + 1);
            polygon.append(QPointF(x, y));
            workingPtr += 2;
        }
        size = 1 + 4 + length * (8 + 8);
        return QVariant::fromValue(polygon);
    }
    default:
        size = 0;
        return QVariant();
    }
}

char* alloc(int32_t size, bool allocateMem)
{
    if (!allocateMem)
    {
        // TODO throw exception if too big
        return cppToJavaMem;
    }
    else
    {
        return new char[size];
    }
}

char* fromQVariant(const QVariant& var, int32_t& length, bool allocateMem)
{
    switch (var.type()) {
    case QVariant::Bool: {
        length = 1 + 1;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = BOOL;
        ptr[1] = var.toBool();
        return ptr;
    }
    case QVariant::ByteArray: {
        QByteArray a = var.toByteArray();
        length = 1 + 4 + a.size();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = BYTE_ARRAY;
        *(int*)(ptr+1) = a.size();
        memcpy(ptr + 5, a.data(), a.size());
        return ptr;
    }
    case QVariant::Color: {
        QColor c = var.value<QColor>();
        length = 1 + 4;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = COLOR;
        *(int*)(ptr+1) = c.rgba();
        return ptr;
    }
    case QVariant::DateTime: {
        QDateTime d = var.toDateTime();
        length = 1 + 8 + 4;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = DATE_TIME;

        int64_t seconds = d.toSecsSinceEpoch();
        int32_t nanos = d.toMSecsSinceEpoch() * 1000000;

        *(int64_t*)(ptr+1) = seconds;
        *(int32_t*)(ptr+9) = nanos;
        return ptr;
    }
    case QVariant::Double: {
        length = 1 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = DOUBLE;
        *(double*)(ptr+1) = var.toDouble();
        return ptr;
    }
    case QVariant::Image: {
        QImage i = var.value<QImage>();
        length = 1 + 4 + 4 + i.sizeInBytes();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = IMAGE;
        *(int32_t*)(ptr+1) = i.width();
        *(int32_t*)(ptr+5) = i.height();
        memcpy(ptr + 9, i.constBits(), i.sizeInBytes());
        return ptr;
    }
    case QVariant::Int: {
        length = 1 + 4;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = INT;
        *(int32_t*)(ptr+1) = var.toInt();
        return ptr;
    }
    case QVariant::Line: {
        QLine l = var.toLine();
        length = 1 + 8 + 8 + 8 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = LINE;

        *(double*)(ptr+1) = l.x1();
        *(double*)(ptr+9) = l.y1();
        *(double*)(ptr+17) = l.x2();
        *(double*)(ptr+25) = l.y2();
        return ptr;
    }
    case QVariant::LongLong: {
        length = 1 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = LONG;
        *(int64_t*)(ptr+1) = var.toLongLong();
        return ptr;
    }
    case QVariant::Point: {
        QPoint p = var.toPoint();
        length = 1 + 8 + 8 + 8 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = POINT;

        *(double*)(ptr+1) = p.x();
        *(double*)(ptr+9) = p.y();
        return ptr;
    }
    case QVariant::Rect: {
        QRect r = var.toRect();
        length = 1 + 8 + 8 + 8 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = RECTANGLE;

        *(double*)(ptr+1) = r.x();
        *(double*)(ptr+9) = r.y();
        *(double*)(ptr+17) = r.width();
        *(double*)(ptr+25) = r.height();
        return ptr;
    }
    case QVariant::RegExp: {
        QString str = var.toRegExp().pattern();
        QByteArray array = str.toUtf8();
        length = 1 + 4 + array.length();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = REGULAR_EXPRESSION;
        *(int32_t*)(ptr+1) = array.size();
        memcpy(ptr + 5, array.data(), array.size());
        return ptr;
    }
    case QVariant::Size: {
        QSize s = var.toSize();
        length = 1 + 8 + 8 + 8 + 8;
        char* ptr = alloc(length, allocateMem);
        ptr[0] = SIZE;

        *(double*)(ptr+1) = s.width();
        *(double*)(ptr+9) = s.height();
        return ptr;
    }
    case QVariant::String: {
        QString str = var.toString();
        QByteArray array = str.toUtf8();
        length = 1 + 4 + array.length();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = STRING;
        *(int*)(ptr+1) = array.size();
        memcpy(ptr + 5, array.data(), array.size());
        return ptr;
    }
    case QVariant::Url: {
        QString str = var.toUrl().toString();
        QByteArray array = str.toUtf8();
        length = 1 + 4 + array.length();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = URL;
        *(int32_t*)(ptr+1) = array.size();
        memcpy(ptr + 5, array.data(), array.size());
        return ptr;
    }
    case QVariant::Uuid: {
        QString str = var.toUuid().toString();
        QByteArray array = str.toUtf8();
        length = 1 + 4 + array.length();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = URL;
        *(int32_t*)(ptr+1) = array.size();
        memcpy(ptr + 5, array.data(), array.size());
        return ptr;
    }
    case QVariant::Font: {
        QFont f = var.value<QFont>();
        QString str = f.toString();
        QByteArray array = str.toUtf8();
        length = 1 + 4 + array.length();
        char* ptr = alloc(length, allocateMem);
        ptr[0] = URL;
        *(int32_t*)(ptr+1) = array.size();
        memcpy(ptr + 5, array.data(), array.size());
        return ptr;
    }
    default:
        if (var.canConvert<QVariantList>())
        {
            QVariantList p = var.value<QVariantList>();
            length = 1 + 4 + p.size() * (8 + 8);
            char* ptr = alloc(length, allocateMem);
            ptr[0] = POLYLINE;
            *(int*)(ptr+1) = p.size();

            double* workingPtr = (double*) (ptr +  1 + 4);
            for (int32_t i = 0; i < p.size(); ++i)
            {
                const QVariant& var = p[i];
                QPointF point = var.toPointF();
                *workingPtr = point.x();
                *(workingPtr + 1) = point.y();
                workingPtr += 2;
            }
            return ptr;
        }
        else {
            return nullptr;
        }
    }

}
