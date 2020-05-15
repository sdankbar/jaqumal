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
#ifndef GENERICLISTMODEL_H
#define GENERICLISTMODEL_H

#include <QAbstractListModel>

extern "C"
{
extern void* createGenericListModel(const char* modelName, char** roleNames, int32_t* roleIndices, int32_t length);

extern void setGenericListModelData(void* tempPointer, int32_t index, void* data, int32_t roleIndex);
extern void setGenericListModelDataMulti(void* tempPointer, int32_t index, void* data, int32_t* roleIndex, int32_t valueCount);

extern void insertGenericListModelData(void* tempPointer, int32_t index, void* data, int32_t roleIndex);
extern void insertGenericListModelDataMulti(void* tempPointer, int32_t index, void* data, int32_t* roleIndex, int32_t valueCount);

extern void clearGenericListModelData(void* tempPointer, int32_t index, int32_t roleIndex);
extern void clearAllGenericListModelData(void* tempPointer, int32_t index);
extern void eraseGenericListModelData(void* tempPointer, int32_t index);
extern int32_t appendGenericListModelData(void* tempPointer, void* data, int32_t roleIndex);
extern int32_t appendGenericListModelDataMulti(void* tempPointer, void* data, int32_t* roleIndex, int32_t valueCount);

extern void* getGenericListModelData(void* tempPointer, int32_t index, int32_t roleIndex, int32_t& length);

extern void reorderGenericListModel(void* tempPointer, int32_t* ordering, int32_t length);

extern bool isGenericListModelRolePresent(void* tempPointer, int32_t index, int32_t roleIndex);
extern int32_t getGenericListModelSize(void* tempPointer);
}

class GenericListModel : public QAbstractListModel
{
    Q_OBJECT
    Q_PROPERTY(qint32 size READ size NOTIFY sizeChanged)

public:
    explicit GenericListModel(const QHash<int, QByteArray>& roleMap);

    // Basic functionality:
    int rowCount(const QModelIndex& parent = QModelIndex()) const override;

    QVariant data(const QModelIndex& index, int role = Qt::DisplayRole) const override;
    QHash<int, QByteArray> roleNames() const override;
    bool setData(const QModelIndex &index, const QVariant &value, int role) override;
    //

    Q_INVOKABLE QVariantMap getData(qint32 row) const;
    Q_INVOKABLE QVariant getData(qint32 row, const QString& propertyName) const;

    int32_t appendRowData(const QVariant& data, int32_t roleIndex);
    int32_t appendRowData(const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex);

    void setRowData(qint32 row, const QVariant& data, int32_t roleIndex);
    void setRowData(qint32 row, const std::vector<QVariant>& data, const QVector<int>& roleIndex);

    void insertRowData(qint32 row, const QVariant& data, int32_t roleIndex);
    void insertRowData(qint32 row, const std::vector<QVariant>& data, QVector<int> roleIndex);

    QVariant getRowData(qint32 row, int32_t roleIndex) const;

    void clear(qint32 row, int32_t roleIndex);
    void clear(qint32 row);
    void erase(qint32 row);

    bool containsRole(qint32 row, int32_t roleIndex);

    void reorder(const std::vector<int32_t>& ordering);
    
    qint32 size() const;
    
signals:
    void sizeChanged();
private:

    void emitSignal(qint32 row);

    QList<QHash<int32_t, QVariant> > m_rowData;

    QHash<QString, int> m_stringToIndexRoleMap;

    QHash<int, QByteArray> m_roleNames;
};

#endif // GENERICLISTMODEL_H
