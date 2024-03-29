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
#include <QImage>
#include <QApplication>
#include <QQuickWindow>
#include <math.h>

QWindow* getEventInjectionWindow();
QImage takeFocusedWindowScreenShot();
bool fuzzyEquals(const QImage& source, const QImage& target, double ratiodB = 50);
double getPeakSignalToNoiseRatio(const QImage& source, const QImage& target);

QImage generateDelta(const QImage& source, const QImage& target);

void QIMAGECOMPARE(const std::string& fileName, double ratiodB = 50);

void mouseWheel(int32_t x, int32_t y, int32_t pixelX, int32_t pixelY, int32_t angleX, int32_t angleY,
                int32_t buttons, int32_t modifiers, int32_t phase, bool inverted);
