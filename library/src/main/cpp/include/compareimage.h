#pragma once
#include <QImage>
#include <QApplication>
#include <QQuickWindow>
#include <math.h>

QWindow* getEventInjectionWindow();
QImage takeFocusedWindowScreenShot();
bool fuzzyEquals(const QImage& source, const QImage& target);
