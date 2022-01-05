#pragma once
#include <QImage>
#include <QApplication>
#include <QQuickWindow>
#include <math.h>

QWindow* getEventInjectionWindow();
QImage takeFocusedWindowScreenShot();
bool fuzzyEquals(const QImage& source, const QImage& target);

void QIMAGECOMPARE(const std::string& fileName);

void mouseWheel(int32_t x, int32_t y, int32_t pixelX, int32_t pixelY, int32_t angleX, int32_t angleY,
                int32_t buttons, int32_t modifiers, int32_t phase, bool inverted);
