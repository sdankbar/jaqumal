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
#pragma once

#include <jni.h>
#include <QAbstractListModel>

class ListModelFunctions
{
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);
};

class GenericListModel : public QAbstractListModel
{
    Q_OBJECT
    Q_PROPERTY(qint32 size READ size NOTIFY sizeChanged)
    Q_PROPERTY(const QString& modelName READ modelName)
    Q_PROPERTY(const QVariantMap& root READ root NOTIFY rootChanged)

public:
    explicit GenericListModel(const QString& modelName, const QHash<int, QByteArray>& roleMap);

    const QString& modelName() const;
    const QVariantMap& root() const;

    void putRootValue(const QString& key, const QVariant& value);
    void removeRootValue(const QString& key);
    const QVariant& getRootValue(const QString& key);

    // Basic functionality:
    int rowCount(const QModelIndex& parent = QModelIndex()) const override;

    QVariant data(const QModelIndex& index, int role = Qt::DisplayRole) const override;
    QHash<int, QByteArray> roleNames() const override;
    bool setData(const QModelIndex &index, const QVariant &value, int role) override;
    //

    Q_INVOKABLE QVariantMap getData(qint32 row) const;
    Q_INVOKABLE QVariant getData(qint32 row, const QString& propertyName) const;

    int32_t appendRowData(std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);
    void setRowData(qint32 row, std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);
    void assignRowData(qint32 row, std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);
    void insertRowData(qint32 row, std::vector<QVariant>& data, QVector<int32_t> roleIndex);

    const QVariant& getRowData(qint32 row, int32_t roleIndex) const;

    void clear(qint32 row, int32_t roleIndex);
    void clear(qint32 row);
    void erase(qint32 row);

    bool containsRole(qint32 row, int32_t roleIndex);

    void reorder(const std::vector<int32_t>& ordering);

    qint32 size() const;

    void lockDataChangedSignal();
    void unlockDataChangedSignal();

    void registerModelChangedListener(jobject c);

signals:
    void sizeChanged();
    void rootChanged();

private:

    void emitSignal(qint32 row);
    void callbackModelChangedListeners();

    // Member variables
    QString m_modelName;
    QVariantMap m_root;

    QVector<QVector<QVariant> > m_rowData;
    QHash<QString, int> m_stringToIndexRoleMap;
    QHash<int, QByteArray> m_roleNames;

    bool m_dataChangedLocked;

    std::vector<jobject> callbacks;
};



