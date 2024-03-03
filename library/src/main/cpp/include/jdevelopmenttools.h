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
#ifndef JDEVELOPMENTTOOLS_H
#define JDEVELOPMENTTOOLS_H

#include <QObject>
#include <QQuickWindow>
#include <QDateTime>

class JDevelopmentTools : public QQuickWindow
{
    Q_OBJECT
    Q_PROPERTY(bool isRecording READ isRecording NOTIFY isRecordingChanged)
    Q_PROPERTY(bool generateJUnit READ generateJUnit WRITE setGenerateJUnit NOTIFY generateJUnitChanged)
    Q_PROPERTY(bool generateQTTest READ generateQTTest WRITE setGenerateQTTest NOTIFY generateQTTestChanged)
public:
    JDevelopmentTools(QWindow* parent = nullptr);
    ~JDevelopmentTools();

    bool eventFilter(QObject* watched, QEvent* event) override;

    // Property Getters
    bool isRecording() const;
    bool generateJUnit() const;
    bool generateQTTest() const;

    void setGenerateJUnit(bool gen);
    void setGenerateQTTest(bool gen);

signals:
    // Property Notify Signals
    void isRecordingChanged();
    void generateJUnitChanged();
    void generateQTTestChanged();
private:

    struct RecordedEvent {
        QEvent* m_event;
        QDateTime m_eventTime;
        QString m_screenshotFile;
    };

    static int32_t INSTANCE_COUNT;

    void saveRecording(const QDateTime& recordingEndTime);

    void saveJUnitRecording(const QDateTime& recordingEndTime);
    void saveQTTestRecording(const QDateTime& recordingEndTime);

    // Properties
    bool m_isRecording;

    // Recording variables
    QDateTime m_startTime;
    QDateTime m_lastMouseMoveTime;
    std::vector<RecordedEvent> m_recordedEvents;
    std::string m_recordingDirectory;

    bool m_generateJUnit;
    bool m_generateQTTest;
    bool m_mouseToTouch;
};

#endif // JDEVELOPMENTTOOLS_H
