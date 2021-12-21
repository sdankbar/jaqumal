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

    void saveRecording();

    // Properties
    bool m_isRecording;

    // Recording variables
    QDateTime m_startTime;
    QEvent* m_lastReceivedEvent;
    std::vector<RecordedEvent> m_recordedEvents;
    std::string m_recordingDirectory;
};

#endif // JDEVELOPMENTTOOLS_H
