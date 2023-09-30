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
#include "compareimage.h"
#include <QImage>
#include <QTest>

QWindow* getEventInjectionWindow()
{
    QWindow* w = QApplication::focusWindow();
    if (w != nullptr)
    {
        return w;
    }
    QWindowList topLevel = QApplication::topLevelWindows();
    if (!topLevel.isEmpty())
    {
        return topLevel[0];
    }
    else
    {
        return nullptr;
    }
}

QImage takeFocusedWindowScreenShot()
{
    QWindow* w = getEventInjectionWindow();
    QQuickWindow* quickWindow = dynamic_cast<QQuickWindow*>(w);
    if (quickWindow != nullptr)
    {
        QImage temp = quickWindow->grabWindow();
        temp.convertTo(QImage::Format_ARGB32);
        return temp;
    }
    else
    {
        return QImage();
    }
}

double getPeakSignalToNoiseRatio(const QImage& source, const QImage& target)
{
  if (source.isNull() || target.isNull())
  {
    return 0;
  }
  else if (source.width() != target.width())
  {
    return 0;
  }
  else if (source.height() != target.height())
  {
    return 0;
  }
  else
  {
    uint64_t sqSum = 0;
    const int32_t pixelCount = source.width() * source.height();
    const QRgb* sourcePixels = (const QRgb*)source.constBits();
    const QRgb* targetPixels = (const QRgb*)target.constBits();
    for (int i = 0; i < pixelCount; ++i)
    {
      const QRgb sColor = sourcePixels[i];
      const QRgb tColor = targetPixels[i];
      const int deltaR = qRed(sColor) - qRed(tColor);
      const int deltaG = qGreen(sColor) - qGreen(tColor);
      const int deltaB = qBlue(sColor)- qBlue(tColor);
      sqSum += (deltaR * deltaR) +
        (deltaG * deltaG) +
        (deltaB * deltaB);
    }

    double meanSquareError = sqSum / (3.0 * pixelCount);
    if (meanSquareError == 0)
    {
      // Avoid division by 0.
      return 1000;
    }
    else
    {
      double peakSignalToNoiseRatio = 10 * log10((255 * 255) / meanSquareError);
      return std::min(peakSignalToNoiseRatio, 1000.0);
    }
  }
}

bool fuzzyEquals(const QImage& source, const QImage& target, double ratiodB)
{
  double peakSignalToNoiseRatio = getPeakSignalToNoiseRatio(source, target);
  return (peakSignalToNoiseRatio > ratiodB);
}

QImage generateDelta(const QImage& source, const QImage& target)
{
    if (source.isNull() || target.isNull())
    {
        return QImage();
    }
    else if (source.width() != target.width())
    {
        return QImage();
    }
    else if (source.height() != target.height())
    {
        return QImage();
    }
    else
    {
        const bool whiteEquals = ("1" == qgetenv("WHITE_EQUALS"));
        QImage output(source.width(), source.height(), QImage::Format_ARGB32);
        const int32_t pixelCount = source.width() * source.height();
        const QRgb* sourcePixels = (const QRgb*)source.constBits();
        const QRgb* targetPixels = (const QRgb*)target.constBits();
        QRgb* outputPixels = (QRgb*)output.bits();
        for (int i = 0; i < pixelCount; ++i)
        {
            const QRgb sColor = sourcePixels[i];
            const QRgb tColor = targetPixels[i];
            const int deltaR = std::abs(qRed(sColor) - qRed(tColor));
            const int deltaG = std::abs(qGreen(sColor) - qGreen(tColor));
            const int deltaB = std::abs(qBlue(sColor)- qBlue(tColor));
            if (whiteEquals && deltaR == 0 && deltaG == 0 && deltaB == 0)
            {
              outputPixels[i] = qRgb(255, 255, 255);
            }
            else
            {
              outputPixels[i] = qRgb(deltaR, deltaG, deltaB);
            }
        }

        return output;
    }
}

void QIMAGECOMPARE(const std::string& fileName, double ratiodB)
{
    QImage target(QString::fromStdString(fileName));
    if (target.isNull())
    {
        QFAIL(("Unable to read " + fileName).c_str());
    }
    target.convertTo(QImage::Format_ARGB32);

    bool matches = false;
    QImage source;
    for (int i = 0; i < 10; ++i)
    {
        source = takeFocusedWindowScreenShot();
        if (fuzzyEquals(source, target, ratiodB))
        {
            matches = true;
            break;
        }
        else
        {
            QTest::qWait(100);
        }
    }

    if (!matches)
    {
        if ("1" == qgetenv("RECAPTURE_CONDITIONALLY") &&
            !qgetenv("RECAPTURE_LOWER_BOUND").isEmpty())
        {
          double ratio = getPeakSignalToNoiseRatio(source, target);
          double lowerBound = std::stold(qgetenv("RECAPTURE_LOWER_BOUND").toStdString());
          if (ratio >= lowerBound)
          {
            QImage delta = generateDelta(source, target);
            if (!delta.isNull())
            {
              QString diffFile = QString::fromStdString(fileName).replace(".png",".delta.png");
              delta.save(diffFile, "png");
            }
            source.save(QString::fromStdString(fileName));
          }
          else
          {
            QImage delta = generateDelta(source, target);
            if (!delta.isNull())
            {
              QString diffFile = QString::fromStdString(fileName).replace(".png",".delta.png");
              delta.save(diffFile, "png");
              QVERIFY2(false, ("Image does not match " + fileName + " and not eligible for recapture. See " + diffFile.toStdString()).c_str());
            }
            else
            {
              QVERIFY2(false, ("Image does not match "+ fileName + " and not eligible for recapture").c_str());
            }
          }
        }
        else if ("1" == qgetenv("RECAPTURE"))
        {
          QImage delta = generateDelta(source, target);
          if (!delta.isNull())
          {
            QString diffFile = QString::fromStdString(fileName).replace(".png",".delta.png");
            delta.save(diffFile, "png");
          }
          source.save(QString::fromStdString(fileName));
        }
        else
        {
            QImage delta = generateDelta(source, target);
            if (!delta.isNull())
            {
                QString diffFile = QString::fromStdString(fileName).replace(".png",".delta.png");
                delta.save(diffFile, "png");
                QVERIFY2(false, ("Image does not match " + fileName + ". See " + diffFile.toStdString()).c_str());
            }
            else
            {
                QVERIFY2(false, ("Image does not match "+ fileName).c_str());
            }
        }
    }
}

void mouseWheel(int32_t x, int32_t y, int32_t pixelX, int32_t pixelY, int32_t angleX, int32_t angleY,
                int32_t buttons, int32_t modifiers, int32_t phase, bool inverted)
{
    QWindow* window = getEventInjectionWindow();
    if (window != nullptr)
    {
        QWheelEvent* w = new QWheelEvent(QPointF(x, y), QPointF(x + window->x(), y + window->y()),
                                         QPoint(pixelX, pixelY),
                                         QPoint(angleX, angleY),
                                         static_cast<Qt::MouseButtons>(buttons),
                                         static_cast<Qt::KeyboardModifiers>(modifiers),
                                         static_cast<Qt::ScrollPhase>(phase),
                                         inverted,
                                         Qt::MouseEventSynthesizedByApplication);
        QCoreApplication::instance()->sendEvent(window, w);
    }
}
