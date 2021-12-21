#ifndef JDEVELOPMENTTOOLS_H
#define JDEVELOPMENTTOOLS_H

#include <QObject>
#include <QQuickItem>

class JDevelopmentTools : public QQuickItem
{
    Q_OBJECT
public:
    JDevelopmentTools(QQuickItem* parent = nullptr);

    bool eventFilter(QObject* watched, QEvent* event) override;

private:

    static int32_t INSTANCE_COUNT;
};

#endif // JDEVELOPMENTTOOLS_H
