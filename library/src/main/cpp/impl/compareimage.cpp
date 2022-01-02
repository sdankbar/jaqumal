#include "compareimage.h"

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
