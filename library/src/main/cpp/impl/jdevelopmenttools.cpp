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
#include "jdevelopmenttools.h"
#include "applicationfunctions.h"
#include "compareimage.h"

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
    m_generateJUnit(false),
    m_generateQTTest(false)
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

bool JDevelopmentTools::generateJUnit() const
{
    return m_generateJUnit;
}

bool JDevelopmentTools::generateQTTest() const
{
    return m_generateQTTest;
}

void JDevelopmentTools::setGenerateJUnit(bool gen)
{
    if (m_generateJUnit != gen)
    {
        m_generateJUnit = gen;
        emit generateJUnitChanged();
    }
}

void JDevelopmentTools::setGenerateQTTest(bool gen)
{
    if (m_generateQTTest != gen)
    {
        m_generateQTTest = gen;
        emit generateQTTestChanged();
    }
}

bool JDevelopmentTools::eventFilter(QObject* watched, QEvent* event)
{
    switch (event->type())
    {
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
        else if ((watched->parent() == nullptr) && m_isRecording)
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
            m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
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
            m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
            return true;
        }
        else if (key->key() == Qt::Key_F10 && m_isRecording)
        {

            QImage windowImage = takeFocusedWindowScreenShot();
            if (!windowImage.isNull())
            {
                QString now = QDateTime::currentDateTimeUtc().toString("hh_mm_ss_zzz");
                QString fileName = "screenshot_" + now + ".png";
                QString filePath = QString::fromStdString(m_recordingDirectory) + "/" + fileName;
                windowImage.save(filePath, "png");

                RecordedEvent rec;
                rec.m_event = nullptr;
                rec.m_eventTime = QDateTime::currentDateTimeUtc();
                rec.m_screenshotFile = fileName;
                m_recordedEvents.push_back(rec);
            }
            m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
            return true;
        }
        else if ((watched->parent() == nullptr) && m_isRecording)
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
        if ((watched->parent() == nullptr) && m_isRecording)
        {
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*static_cast<QMouseEvent*>(event));
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
        if ((watched->parent() == nullptr) && m_isRecording)
        {
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*static_cast<QMouseEvent*>(event));
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
        if ((watched->parent() == nullptr) && (milli > MOVE_SAMPLE_RATE) && m_isRecording)
        {
            RecordedEvent rec;
            rec.m_event = new QMouseEvent(*static_cast<QMouseEvent*>(event));
            rec.m_eventTime = now;
            m_lastMouseMoveTime = now;
            m_recordedEvents.push_back(rec);
        }
        break;
    }
    case QEvent::Wheel:
    {
        if ((watched->parent() == nullptr) && m_isRecording)
        {
            RecordedEvent rec;
            rec.m_event = new QWheelEvent(*static_cast<QWheelEvent*>(event));
            rec.m_eventTime = QDateTime::currentDateTimeUtc();
            m_recordedEvents.push_back(rec);
        }
        m_lastMouseMoveTime = QDateTime::fromMSecsSinceEpoch(0);
        break;
    }
    case QEvent::TouchBegin:
    {
        // TODO
        break;
    }
    default:
        // Ignore
        break;
    }

    return QObject::eventFilter(watched, event);
}

void JDevelopmentTools::saveRecording(const QDateTime& recordingEndTime)
{
    if (m_generateJUnit)
    {
        saveJUnitRecording(recordingEndTime);
    }

    if (m_generateQTTest)
    {
        saveQTTestRecording(recordingEndTime);
    }
}

void JDevelopmentTools::saveJUnitRecording(const QDateTime& recordingEndTime)
{
    QFile javaTestFile(QString::fromStdString(m_recordingDirectory) + "/IntegrationTest.java");
    if (javaTestFile.open(QFile::WriteOnly | QFile::Truncate)) {
        QTextStream out(&javaTestFile);

        out << "import java.io.File;\n";
        out << "import java.time.Duration;\n";
        out << "import org.junit.After;\n";
        out << "import org.junit.Test;\n";
        out << "import com.github.sdankbar.qml.JQMLApplication;\n";
        out << "import com.github.sdankbar.qml.JQMLDevelopmentTools;\n";
        out << "\n";
        out << "public class IntegrationTest {\n";
        out << "\n";
        out << "\tprivate JQMLApplication<?> app;\n";
        out << "\tprivate final String screenshotDir = \"TODO\";\n";
        out << "\n";
        out << "\t@Before\n";
        out << "\tpublic void setup() {\n";
        out << "\t\t//TODO run test setup\n";
        out << "\t}\n";
        out << "\n";
        out << "\t@After\n";
        out << "\tpublic void finish() {\n";
        out << "\t\tapp.getDevelopmentTools().endIntegrationTest();\n";
        out << "\t}\n";
        out << "\n";
        out << "\t@Test\n";
        out << "\tpublic void test_run() {\n";
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
                    out << "\t\ttools.pressKey(" <<
                           key->key() << ", " <<
                           key->modifiers() << ", " <<
                           "\"" << key->text() << "\", " <<
                           (key->isAutoRepeat() ? "true" : "false") << ", " <<
                           key->count() << ", " <<
                           "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                case QEvent::KeyRelease:
                {
                    QKeyEvent* key = static_cast<QKeyEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.releaseKey(" <<
                           key->key() << ", " <<
                           key->modifiers() << ", " <<
                           "\"" << key->text() << "\", " <<
                           (key->isAutoRepeat() ? "true" : "false") << ", " <<
                           key->count() << ", " <<
                           "Duration.ofMillis(" << milli << "));\n";
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
                case QEvent::Wheel:
                {
                    QWheelEvent* mouse = static_cast<QWheelEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\ttools.wheel(" << mouse->x() << ", "
                        << mouse->y() << ", "
                        << mouse->pixelDelta().x() << ", "
                        << mouse->pixelDelta().y() << ", "
                        << mouse->angleDelta().x() << ", "
                        << mouse->angleDelta().y() << ", "
                        << mouse->buttons() << ", "
                        << mouse->modifiers() << ", "
                        << mouse->phase() << ", "
                        << (mouse->inverted() ? "true" : "false") << ", "
                        << "Duration.ofMillis(" << milli << "));\n";
                    break;
                }
                case QEvent::TouchBegin:
                {
                    // TODO
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

        out << "\t}\n";
        out << "\n";
        out << "}\n";
        out.flush();
    }
}

void JDevelopmentTools::saveQTTestRecording(const QDateTime& recordingEndTime)
{
    QFile cppTestFile(QString::fromStdString(m_recordingDirectory) + "/tst_integrationTest.cpp");
    if (cppTestFile.open(QFile::WriteOnly | QFile::Truncate)) {
        QTextStream out(&cppTestFile);

        out << "#include <QTest>\n";
        out << "#include <QObject>\n";
        out << "#include <ImageTest.h>\n";
        out << "\n";
        out << "class IntegrationTest : public QObject {\n";
        out << "Q_OBJECT";
        out << "\n";
        out << "public:\n";
        out << "\n";
        out << "\tprivate slots:\n";
        out << "\tvoid init();";
        out << "\n";
        out << "\tvoid cleanup();\n";
        out << "\n";
        out << "\tvoid test();\n";
        out << "};\n";

        out << "void IntegrationTest::init() {\n";
        out << "\t\t//TODO\n";
        out << "}\n";

        out << "void IntegrationTest::cleanup() {\n";
        out << "\t\t//TODO\n";
        out << "}\n";

        out << "void IntegrationTest::test() {\n";
        out << "\t\tstd::string screenshotDir = \"TODO\";\n";

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
                    out << "\t\tqwait(" << milli << ");\n";
                    out << "\t\tkeyPress(" <<
                           QGuiApplication::focusWindow() << ", " <<
                           key->key() << ", " <<
                           key->modifiers() << ");\n";
                    break;
                }
                case QEvent::KeyRelease:
                {
                    QKeyEvent* key = static_cast<QKeyEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\tqwait(" << milli << ");\n";
                    out << "\t\tkeyRelease(" <<
                           QGuiApplication::focusWindow() << ", " <<
                           key->key() << ", " <<
                           key->modifiers() << ");\n";
                    break;
                }
                case QEvent::MouseButtonPress:
                {
                    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\tqwait(" << milli << ");\n";
                    out << "\t\tmousePress("
                        << QGuiApplication::focusWindow() << ", "
                        << mouse->button() << ", "
                        << mouse->modifiers() << ", QPoint("
                        << mouse->x() << ", "
                        << mouse->y() << "));\n";
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
                    out << "\t\tqwait(" << milli << ");\n";
                    out << "\t\tmouseRelease("
                        << QGuiApplication::focusWindow() << ", "
                        << mouse->button() << ", "
                        << mouse->modifiers() << ", QPoint("
                        << mouse->x() << ", "
                        << mouse->y() << "));\n";
                    break;
                }
                case QEvent::MouseMove: {
                    QMouseEvent* mouse = static_cast<QMouseEvent*>(e.m_event);
                    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                    out << "\t\tqwait(" << milli << ");\n";
                    out << "\t\tmouseMove("
                        << QGuiApplication::focusWindow() << ", QPoint("
                        << mouse->x() << ", "
                        << mouse->y() << "));\n";
                    break;
                }
                //case QEvent::Wheel:
                //{
                //    QWheelEvent* mouse = static_cast<QWheelEvent*>(e.m_event);
                //    int64_t milli = workingTime.msecsTo(e.m_eventTime);
                //    out << "\t\tqwait(" << milli << ");\n";
                //    out << "\t\ttools.wheel(" << mouse->x() << ", "
                //        << mouse->y() << ", "
                //        << mouse->pixelDelta().x() << ", "
                //        << mouse->pixelDelta().y() << ", "
                //        << mouse->angleDelta().x() << ", "
                //        << mouse->angleDelta().y() << ", "
                //        << mouse->buttons() << ", "
                //        << mouse->modifiers() << ", "
                //        << mouse->phase() << ", "
                //        << (mouse->inverted() ? "true" : "false") << ", "
                //        << "Duration.ofMillis(" << milli << "));\n";
                //    break;
                //}
                case QEvent::TouchBegin:
                {
                    // TODO
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
                out << "\t\tqwait(" << milli << ");\n";
                out << "\t\tQIMAGECOMPARE(screenshotDir + \"/"
                    << e.m_screenshotFile << "\");\n";
                workingTime = e.m_eventTime;
            }
        }

        int64_t milli = workingTime.msecsTo(recordingEndTime);
        out << "\t\tqwait(" << milli << ");\n";

        out << "\t}\n";
        out << "\n";
        out << "}\n";
        out.flush();
    }
}
