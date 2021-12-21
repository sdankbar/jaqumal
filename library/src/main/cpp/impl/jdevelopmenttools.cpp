#include "jdevelopmenttools.h"
#include "applicationfunctions.h"

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
                // TODO
            } else {
                // TODO
            }
            m_isRecording = !m_isRecording;
            emit isRecordingChanged();
            return true;
        }
        break;
    }
    case QEvent::MouseButtonPress:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);

        break;
    }
    case QEvent::MouseButtonDblClick:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);

        break;
    }
    case QEvent::MouseButtonRelease:
    {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);

        break;
    }
    case QEvent::MouseMove: {
        QMouseEvent* mouse = static_cast<QMouseEvent*>(event);
        break;
    }
    default:
        // Ignore
        break;
    }

    return QObject::eventFilter(watched, event);
}
