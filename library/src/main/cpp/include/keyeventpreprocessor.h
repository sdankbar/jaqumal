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
#ifndef KEYEVENTPREPROCESSOR_H
#define KEYEVENTPREPROCESSOR_H

#include <QObject>

class QKeyEvent;

class KeyEventPreProcessor : public QObject
{
public:
    enum CaseMode {
        DEFAULT,
        UPPER,
        LOWER
    };
    Q_ENUM(CaseMode)

private:
    Q_OBJECT
    Q_PROPERTY(QObject* target READ target WRITE setTarget NOTIFY targetChanged)
    Q_PROPERTY(CaseMode mode READ mode WRITE setMode NOTIFY modeChanged)
public:
    KeyEventPreProcessor(QObject* parent = nullptr);

    QObject* target() const;
    void setTarget(QObject* target);

    CaseMode mode() const;
    void setMode(CaseMode mode);

    virtual bool eventFilter(QObject* obj, QEvent* event) override;
signals:
    void targetChanged();
    void modeChanged();
private:
    QObject* m_target;
    CaseMode m_mode;
};

#endif // KEYEVENTPREPROCESSOR_H
