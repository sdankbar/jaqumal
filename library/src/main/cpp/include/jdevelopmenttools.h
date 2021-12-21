#ifndef JDEVELOPMENTTOOLS_H
#define JDEVELOPMENTTOOLS_H

#include <QObject>
#include <QQuickWindow>

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

    static int32_t INSTANCE_COUNT;

    // Properties
    bool m_isRecording;
};

#endif // JDEVELOPMENTTOOLS_H
