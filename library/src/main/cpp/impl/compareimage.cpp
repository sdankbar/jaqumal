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
        return quickWindow->grabWindow();
    }
    else
    {
        return QImage();
    }
}

bool fuzzyEquals(const QImage& source, const QImage& target)
{
    if (source.isNull() || target.isNull())
    {
        return false;
    }
    else if (source.width() != target.width())
    {
        return false;
    }
    else if (source.height() != target.height())
    {
        return false;
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
            return true;
        }
        else
        {
            double peakSignalToNoiseRatio = 10 * log10((255 * 255) / meanSquareError);
            return (peakSignalToNoiseRatio > 60);
        }
    }
}

void QIMAGECOMPARE(const std::string& fileName)
{
    QImage target(QString::fromStdString(fileName));
    if (target.isNull())
    {
        QFAIL(("Unable to read " + fileName).c_str());
    }

    bool matches = false;
    for (int i = 0; i < 10; ++i)
    {
        QImage source = takeFocusedWindowScreenShot();
        if (fuzzyEquals(source, target))
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
        if ("1" == qgetenv("RECAPTURE"))
        {
            takeFocusedWindowScreenShot().save(QString::fromStdString(fileName));
        }
        else
        {
            QFAIL(("Image does not match "+ fileName).c_str());
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
