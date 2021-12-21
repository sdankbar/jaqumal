#include "jdevelopmenttools.h"
#include "applicationfunctions.h"

#include <QDir>

int32_t JDevelopmentTools::INSTANCE_COUNT = 0;

JDevelopmentTools::JDevelopmentTools(QWindow* parent) :
    QQuickWindow(parent),
    m_isRecording(false)
{
    ++INSTANCE_COUNT;
    if (INSTANCE_COUNT == 1)
    {
        ApplicationFunctions::get()->installEventFilterToApplication(this);
    }
    else
    {
        // TODO log warning
    }
}

JDevelopmentTools::~JDevelopmentTools()
{
    ApplicationFunctions::get()->removeEventFilterFromApplication(this);
}

bool JDevelopmentTools::isRecording() const
{
    return m_isRecording;
}

bool JDevelopmentTools::eventFilter(QObject* watched, QEvent* event)
{
    switch (event->type()) {
    case QEvent::KeyPress:
    {
        QKeyEvent* key = static_cast<QKeyEvent*>(event);

        if (key->key() == Qt::Key_F12)
        {
            // Ignore
        }
        else if (key->key() == Qt::Key_F11)
        {
            // Ignore
        }
        else if (key->key() == Qt::Key_F10)
        {
            // Ignore
        }
        else
        {
            RecordedEvent rec;
            rec.m_event = new QKeyEvent(*key);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        break;
    }
    case QEvent::KeyRelease:
    {
        QKeyEvent* key = static_cast<QKeyEvent*>(event);
        if (key->key() == Qt::Key_F12)
        {
            setVisible(!isVisible());
            return true;
        }
        else if (key->key() == Qt::Key_F11)
        {
            if (m_isRecording) {
                saveRecording();
            } else {
                m_startTime = QDateTime::currentDateTimeUtc();
                m_recordedEvents.clear();

                m_recordingDirectory =
                        "recording_" + m_startTime.toString("hh:mm:ss.zzz").toStdString();
                QDir currentDirectory;
                currentDirectory.mkdir(QString::fromStdString(m_recordingDirectory));
            }
            m_isRecording = !m_isRecording;
            emit isRecordingChanged();
            return true;
        }
        else if (key->key() == Qt::Key_F10)
        {

            QImage windowImage = ApplicationFunctions::get()->takeFocusedWindowScreenShot();
            if (!windowImage.isNull())
            {
                QString now = QDateTime::currentDateTimeUtc().toString("hh:mm:ss.zzz");
                QString fileName = QString::fromStdString(m_recordingDirectory) + "/screenshot_" + now + ".png";
                windowImage.save(fileName, "png");

                RecordedEvent rec;
                rec.m_event = nullptr;
                rec.m_eventTime = QDateTime::currentDateTimeUtc();
                rec.m_screenshotFile = fileName;
                m_recordedEvents.push_back(rec);
            }
            return true;
        }
        else
        {
            RecordedEvent rec;
            rec.m_event = new QKeyEvent(*key);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        break;
    }
    case QEvent::MouseButtonPress:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        RecordedEvent rec;
        rec.m_event = new QMouseEvent(*mouse);
        rec.m_eventTime = QDateTime::currentDateTimeUtc();
        m_recordedEvents.push_back(rec);
        break;
    }
    case QEvent::MouseButtonDblClick:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        RecordedEvent rec;
        rec.m_event = new QMouseEvent(*mouse);
        rec.m_eventTime = QDateTime::currentDateTimeUtc();
        m_recordedEvents.push_back(rec);
        break;
    }
    case QEvent::MouseButtonRelease:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        RecordedEvent rec;
        rec.m_event = new QMouseEvent(*mouse);
        rec.m_eventTime = QDateTime::currentDateTimeUtc();
        m_recordedEvents.push_back(rec);
        break;
    }
    case QEvent::MouseMove: {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        RecordedEvent rec;
        rec.m_event = new QMouseEvent(*mouse);
        rec.m_eventTime = QDateTime::currentDateTimeUtc();
        m_recordedEvents.push_back(rec);
        break;
    }
    default:
        // Ignore
        break;
    }

    return QObject::eventFilter(watched, event);
}

void JDevelopmentTools::saveRecording()
{
    // TODO
}
