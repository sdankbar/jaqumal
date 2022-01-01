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
public:
    JDevelopmentTools(QWindow* parent = nullptr);
    ~JDevelopmentTools();

    bool eventFilter(QObject* watched, QEvent* event) override;

    // Property Getters
    bool isRecording() const;

signals:
    // Property Notify Signals
    void isRecordingChanged();
private:

    struct RecordedEvent {
        QEvent* m_event;
        QDateTime m_eventTime;
        QString m_screenshotFile;
    };

    static int32_t INSTANCE_COUNT;

    void saveRecording(const QDateTime& recordingEndTime);

    // Properties
    bool m_isRecording;

    // Recording variables
    QDateTime m_startTime;
    QDateTime m_lastMouseMoveTime;
    std::vector<RecordedEvent> m_recordedEvents;
    std::string m_recordingDirectory;
};

#endif // JDEVELOPMENTTOOLS_H
