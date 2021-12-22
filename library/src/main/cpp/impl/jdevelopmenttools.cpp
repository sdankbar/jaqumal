#include "jdevelopmenttools.h"
#include "applicationfunctions.h"

#include <QDir>
#include <QTextStream>

namespace
{
int32_t MOVE_SAMPLE_RATE = 250;
}

int32_t JDevelopmentTools::INSTANCE_COUNT = 0;

JDevelopmentTools::JDevelopmentTools(QWindow* parent) :
    QQuickWindow(parent),
    m_isRecording(false),
    m_lastMouseMoveTime(QDateTime::fromMSecsSinceEpoch(0)),
    m_lastReceivedEvent(nullptr)
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
        else if (event != m_lastReceivedEvent)
        {
            RecordedEvent rec;
            rec.m_event = new QKeyEvent(*key);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
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
                saveRecording(QDateTime::currentDateTimeUtc());
            } else {
                m_startTime = QDateTime::currentDateTimeUtc();
                m_recordedEvents.clear();

                m_recordingDirectory =
                        "recording_" + m_startTime.toString("hh_mm_ss_zzz").toStdString();
                QDir currentDirectory;
                currentDirectory.mkdir(QString::fromStdString(m_recordingDirectory));
            }
            m_isRecording = !m_isRecording;
            emit isRecordingChanged();
            return true;
        }
        else if (key->key() == Qt::Key_F10 && m_isRecording)
        {

            QImage windowImage = ApplicationFunctions::get()->takeFocusedWindowScreenShot();
            if (!windowImage.isNull())
            {
                QString now = QDateTime::currentDateTimeUtc().toString("hh_mm_ss_zzz");
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
        else if (event != m_lastReceivedEvent)
        {
            RecordedEvent rec;
            rec.m_event = new QKeyEvent(*key);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
        break;
    }
    case QEvent::MouseButtonPress:
    {
        if (event != m_lastReceivedEvent)
        {
            QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*mouse);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
        break;
    }
        //case QEvent::MouseButtonDblClick:
        //{
        //    QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        //    RecordedEvent rec;
        //    rec.m_event = new QMouseEvent(*mouse);
        //    rec.m_eventTime = QDateTime::currentDateTimeUtc();
        //    m_recordedEvents.push_back(rec);
        //    break;
        //}
    case QEvent::MouseButtonRelease:
    {
        if (event != m_lastReceivedEvent)
        {
            QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*mouse);
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
        break;
    }
    case QEvent::MouseMove:
    {
        QDateTime now = QDateTime::currentDateTimeUtc();
        int64_t milli = m_lastMouseMoveTime.msecsTo(now);
        if (event != m_lastReceivedEvent && (milli > MOVE_SAMPLE_RATE))
        {
            QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*mouse);
            rec.m_eventTime = now;
            m_lastMouseMoveTime = now;
            m_recordedEvents.push_back(rec);
        }
        break;
    }
    default:
        // Ignore
        break;
    }

    m_lastReceivedEvent = event;
    return QObject::eventFilter(watched, event);
}

void JDevelopmentTools::saveRecording(const QDateTime& recordingEndTime)
{
    QFile javaTestFile(QString::fromStdString(m_recordingDirectory) + "/IntegrationTest.java");
    if (javaTestFile.open(QFile::WriteOnly | QFile::Truncate)) {
        QTextStream out(&javaTestFile);

        out << "import java.time.Duration;\n";
        out << "import org.junit.Test;\n";
        out << "import com.github.sdankbar.qml.JQMLApplication\n";
        out << "import com.github.sdankbar.qml.JQMLDevelopmentTools\n";

        out << "class IntegrationTest {\n";
        out << "\n";
        out << "\t@Test\n";
        out << "\tpublic void test_run() {\n";
        out << "\t\t// TODO run test setup\n";
        out << "\t\tString screenshotDir = \"TODO\";\n";
        out << "\t\tJQMLDevelopmentTools tools = app.getDevelopmentTools();\n";
        out << "\t\ttools.startIntegrationTest();\n";

        QDateTime workingTime = m_startTime;
        for (const RecordedEvent& e: m_recordedEvents)
        {
            if (e.m_event != nullptr)
            {
                switch (e.m_event->type()) {
                case QEvent::KeyPress:
                {
                    QKeyEvent* key = static_cast<QKeyEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.pressKey(" << key->text() << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                case QEvent::KeyRelease:
                {
                    QKeyEvent* key = static_cast<QKeyEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.pressKey(" << key->text() << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                case QEvent::MouseButtonPress:
                {
                    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.mousePress(" << mouse->x() << ", "
                        << mouse->y() << ", "
                        << mouse->button() << ", "
                        << mouse->buttons() << ", "
                        << mouse->modifiers() << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                    //case QEvent::MouseButtonDblClick:
                    //{
                    //    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    //
                    //    break;
                    //}
                case QEvent::MouseButtonRelease:
                {
                    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.mouseRelease(" << mouse->x() << ", "
                        << mouse->y() << ", "
                        << mouse->button() << ", "
                        << mouse->buttons() << ", "
                        << mouse->modifiers() << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                case QEvent::MouseMove: {
                    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.mouseMove(" << mouse->x() << ", "
                        << mouse->y() << ", "
                        << mouse->button() << ", "
                        << mouse->buttons() << ", "
                        << mouse->modifiers() << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                default:
                    // Ignore
                    break;
                }

                workingTime = e.m_eventTime;
            }
            else
            {
                int64_t milli = workingTime.msecsTo(e.m_eventTime);
                out << "\t\ttools.compareWindowToImage(new File(screenshotDir, \""
                    << e.m_screenshotFile << "\"), Duration.ofMillis(" << milli << "));\n";
                workingTime = e.m_eventTime;
            }
        }

        int64_t milli = workingTime.msecsTo(recordingEndTime);
        out << "\t\ttools.pollEventQueue(Duration.ofMillis(" << milli << "));\n";

        out << "\t\ttools.endIntegrationTest();\n";
        out << "\t}\n";
        out << "\n";
        out << "}\n";
        out.flush();
    }
}
