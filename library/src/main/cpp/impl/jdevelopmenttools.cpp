#include "jdevelopmenttools.h"

int32_t JDevelopmentTools::INSTANCE_COUNT = 0;

JDevelopmentTools::JDevelopmentTools(QQuickItem* parent) :
    QQuickItem(parent)
{
    ++INSTANCE_COUNT;
    if (INSTANCE_COUNT > 1)
    {
        // TODO log warning
    }
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
            // TODO open dev tools
        }
        else if (key->key() == Qt::Key_F11)
        {
            // TODO toggle recording
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
