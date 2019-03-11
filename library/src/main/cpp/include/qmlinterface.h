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
#ifndef JAQUMAL_H
#define JAQUMAL_H

#include <string>
#include <iostream>
#include <QVariant>

extern std::function<void(const char*)> exceptionHandler;
extern "C"
{
extern void createQApplication(int32_t argc, char** argv);
extern void execQApplication();
extern void deleteQApplication();
extern void quitQApplication();

extern void loadQMLFile(const char* fileName);
extern void reloadQMLFile(const char* fileName);
extern void addEventCallback(void c(const char*, void*, int32_t));
extern void setLoggingCallback(void c(int, const char*));
extern void setExceptionCallback(void c(const char*));

extern void invoke(void c());
extern void invokeWithDelay(void c(), int32_t milliseconds);
extern void cleanupMemory(void* ptr);

extern void setSharedMemory(char* cppToJava, int32_t length);
}

enum Type
{
    REGULAR_EXPRESSION,
    URL,
    UUID,
    INT,
    LONG,
    BOOL,
    DOUBLE,
    FLOAT,
    STRING,
    BYTE_ARRAY,
    DATE_TIME,
    SIZE,
    POINT,
    LINE,
    RECTANGLE,
    COLOR,
    IMAGE
};

QVariant toQVariant(void* data, int32_t& size);
std::vector<QVariant> toQVariantList(void* data, uint32_t count);
char* fromQVariant(const QVariant& var, int32_t& length, bool allocateMem);

#endif // JAQUMAL_H
