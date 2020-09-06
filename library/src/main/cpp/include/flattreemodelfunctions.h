/**
 * The MIT License
 * Copyright © 2019 Stephen Dankbar
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
#include <QApplication>
#include <qmllogging.h>
#include <QQmlApplicationEngine>
#include <userinputsimulator.h>
#include <QQmlContext>
#include <QAbstractListModel>

#include <deque>

class FlatTreeModelFunctions
{
public:
    static void initialize(JNIEnv* env);
    static void uninitialize(JNIEnv* env);


private:

};

class GenericFlatTreeModel : public QAbstractListModel
{
    Q_OBJECT
    Q_PROPERTY(const QString& modelName READ modelName)

public:
    explicit GenericFlatTreeModel(
            const QString& modelName,
            const QHash<int, QByteArray>& roleMap,
            int32_t depth);

    const QString& modelName() const;

    // Basic functionality:
    int rowCount(const QModelIndex& parent = QModelIndex()) const override;

    QVariant data(const QModelIndex& index, int role = Qt::DisplayRole) const override;
    QHash<int, QByteArray> roleNames() const override;
    bool setData(const QModelIndex &index, const QVariant &value, int role) override;

    int32_t appendRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);

    void setRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);

    void insertRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);

    QVariant getRowData(std::deque<int32_t>& indicies, int32_t roleIndex);
    const QHash<int32_t, QVariant>& getRowData(std::deque<int32_t>& indicies);
    QVariant getRowData(qint32 flatRow, qint32 roleIndex) const;
    Q_INVOKABLE QVariant getRowData(qint32 flatRow, const QString& roleName) const;
    void setRowData(qint32 flatRow, const QVariant& value, qint32 roleIndex);
    Q_INVOKABLE void setRowData(qint32 flatRow, const QVariant& value, const QString& roleName);
    Q_INVOKABLE qint32 getSize() const;

    void clear(std::deque<int32_t>& indicies, int32_t roleIndex);
    void clear(std::deque<int32_t>& indicies);
    void erase(std::deque<int32_t>& indicies);

    void reorder(std::deque<int32_t>& indicies, const std::vector<int32_t>& ordering);

    int32_t getSize(std::deque<int32_t>& indicies);
    bool containsRole(std::deque<int32_t>& indicies, int32_t roleIndex);
private:

    void emitSignal(qint32 row);
    QSharedPointer<GenericFlatTreeModel> getSubmodel(qint32 row, bool create = true);

    void updateFlatData(std::deque<int32_t>& indicies, int32_t rowsChanged);
    void updateFlatData(std::deque<int32_t>& indicies, QList<QHash<int32_t, QVariant>*>& list, int32_t parentIndex, int32_t& flatIndex);

    int32_t treeIndexToFlatIndex(const std::deque<int32_t>& indicies);

    // Member Variables
    QString m_modelName;
    QList<QHash<int32_t, QVariant>* > m_flatData;

    QList<QHash<int32_t, QVariant> > m_rowData;

    QHash<int, QString> m_indexToStringRoleMap;
    QHash<QString, int> m_stringToIndexRoleMap;
    int32_t m_submodelIndex;
    int32_t m_parentRoleIndex;
    int32_t m_flatIndexRoleIndex;

    QHash<int, QByteArray> m_roleNames;

    int32_t m_depth;
};



