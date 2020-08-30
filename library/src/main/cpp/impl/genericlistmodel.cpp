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
#include "genericlistmodel.h"
#include "qmlinterface.h"
#include "applicationfunctions.h"

void* createGenericListModel(const char* modelName, char** roleNames, int32_t* roleIndices, int32_t length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        QString modelNameStr(modelName);

        QStringList roleNamesList;
        QVector<int> roleIndicesList;
        for (int i = 0; i < length; ++i)
        {
            roleNamesList.push_back(roleNames[i]);
            roleIndicesList.push_back(roleIndices[i]);
        }

        QHash<int, QByteArray> roleNameMap;
        for (int32_t i = 0; i < roleNamesList.size(); ++i)
        {
            roleNameMap[roleIndices[i]] = roleNamesList[i].toStdString().c_str();
        }

        GenericListModel* modelPtr = new GenericListModel(modelName, roleNameMap);
        //m_qmlEngine->rootContext()->setContextProperty(modelName, QVariant::fromValue(modelPtr));
        return modelPtr;
    }
    else
    {
        return nullptr;
    }
}

void setGenericListModelData(void* tempPointer, int32_t index, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        int32_t size;
        //modelPtr->setRowData(index, toQVariant(data, size), roleIndex);
    }
}

void setGenericListModelDataMulti(void* tempPointer, int32_t index, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        std::vector<QVariant> variants;// = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }

        modelPtr->setRowData(index, variants, roleIndicies);
    }
}

void insertGenericListModelData(void* tempPointer, int32_t index, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        int32_t size;
        //modelPtr->insertRowData(index, toQVariant(data, size), roleIndex);
    }
}

void insertGenericListModelDataMulti(void* tempPointer, int32_t index, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        std::vector<QVariant> variants;// = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }
        modelPtr->insertRowData(index, variants, roleIndicies);
    }
}

void clearGenericListModelData(void* tempPointer, int32_t index, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        modelPtr->clear(index, roleIndex);
    }
}
void clearAllGenericListModelData(void* tempPointer, int32_t index)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        modelPtr->clear(index);
    }
}
void eraseGenericListModelData(void* tempPointer, int32_t index)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        modelPtr->erase(index);
    }
}
int32_t appendGenericListModelData(void* tempPointer, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        int32_t size;
        //return modelPtr->appendRowData(toQVariant(data, size), roleIndex);
        return -1;
    }
    else
    {
        return -1;
    }
}

int32_t appendGenericListModelDataMulti(void* tempPointer, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        std::vector<QVariant> variants;// = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }

        return modelPtr->appendRowData(variants, roleIndicies);
    }
    else
    {
        return -1;
    }
}

void* getGenericListModelData(void* tempPointer, int32_t index, int32_t roleIndex, int32_t& length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        //return fromQVariant(modelPtr->getRowData(index, roleIndex), length, false);
        return nullptr;
    }
    else
    {
        return nullptr;
    }
}

void clearGenericListModelRole(void* tempPointer, int32_t index, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        modelPtr->clear(index, roleIndex);
    }
}
void clearGenericListModel(void* tempPointer, int32_t index)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        modelPtr->clear(index);
    }
}
bool isGenericListModelRolePresent(void* tempPointer, int32_t index, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        return modelPtr->containsRole(index, roleIndex);
    }
    else
    {
        return false;
    }
}
int32_t getGenericListModelSize(void* tempPointer)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        return modelPtr->rowCount();
    }
    else
    {
        return 0;
    }
}

void reorderGenericListModel(void* tempPointer, int32_t* ordering, int32_t length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        std::vector<int32_t> orderingVec;
        for (int32_t i = 0; i < length; ++i)
        {
            orderingVec.push_back(ordering[i]);
        }
        modelPtr->reorder(orderingVec);
    }
}

void putRootValueIntoListModel(void* tempPointer, const char* key, void* data)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        QString keyStr(key);
        int size;
        //QVariant dataVar = toQVariant(data, size);
        //modelPtr->putRootValue(keyStr, dataVar);
    }
}

void removeRootValueFromListModel(void* tempPointer, const char* key)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        QString keyStr(key);
        modelPtr->removeRootValue(keyStr);
    }
}

void* getRootValueFromListModel(void* tempPointer, const char* key, int32_t& length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericListModel*>(tempPointer);
        QString keyStr(key);
        //return fromQVariant(modelPtr->getRootValue(key), length, false);
        return nullptr;
    }
    else
    {
        return nullptr;
    }
}

GenericListModel::GenericListModel(const QString& modelName, const QHash<int, QByteArray>& roleMap)
    : QAbstractListModel(nullptr),
      m_modelName(modelName),
      m_roleNames(roleMap)
{
    auto iter = m_roleNames.constBegin();
    auto end = m_roleNames.constEnd();
    for (; iter != end; ++iter)
    {
        m_stringToIndexRoleMap[iter.value()] = iter.key();
    }
}

const QString& GenericListModel::modelName() const
{
    return m_modelName;
}

const QVariantMap& GenericListModel::root() const
{
    return m_root;
}

void GenericListModel::putRootValue(const QString& key, const QVariant& value)
{
    m_root[key] = value;
    emit rootChanged();
}

void GenericListModel::removeRootValue(const QString& key)
{
    m_root.remove(key);
    emit rootChanged();
}

const QVariant& GenericListModel::getRootValue(const QString& key)
{
    auto iter = m_root.find(key);
    if (iter != m_root.end())
    {
        return iter.value();
    }
    else
    {
        static const QVariant EMPTY;
        return EMPTY;
    }
}

int GenericListModel::rowCount(const QModelIndex &parent) const
{
    // For list models only the root node (an invalid parent) should return the list's size. For all
    // other (valid) parents, rowCount() should return 0 so that it does not become a tree model.
    if (parent.isValid())
    {
        return 0;
    }
    else
    {
        return m_rowData.size();
    }
}

QVariant GenericListModel::data(const QModelIndex &index, int role) const
{
    if (!index.isValid())
    {
        return QVariant();
    }

    if (index.row() >= 0 && index.row() < m_rowData.size())
    {
        const auto& data = m_rowData[index.row()];
        if (data.contains(role))
        {
            return *data.find(role);
        }
        else
        {
            return QVariant();
        }
    }
    else
    {
        return QVariant();
    }
}

bool GenericListModel::setData(const QModelIndex &i, const QVariant &value, int role)
{
    if (!i.isValid())
    {
        return false;
    }

    if (i.row() >= 0 && i.row() < m_rowData.size())
    {
        auto& data = m_rowData[i.row()];
        data.insert(role, value);
        emit dataChanged(index(i.row(), 0), index(i.row(), 0));
        return true;
    }
    else
    {
        return false;
    }
}

QVariantMap GenericListModel::getData(qint32 row) const
{
    QVariantMap map;
    if (0 <= row && row < m_rowData.size())
    {
        const QHash<int, QVariant>& data = m_rowData[row];
        for (const auto& k: m_roleNames.keys())
        {
            const QString roleName = m_roleNames.value(k);
            map.insert(roleName, data.value(k));
        }
    }

    return map;
}

QVariant GenericListModel::getData(qint32 row, const QString& propertyName) const
{
    if (0 <= row && row < m_rowData.size())
    {
        const QHash<int, QVariant>& data = m_rowData[row];
        const int key = m_stringToIndexRoleMap.value(propertyName);
        return data.value(key);
    }
    else
    {
        return QVariant();
    }
}

QVariant GenericListModel::getRowData(qint32 row, int32_t roleIndex) const
{
    if (row < m_rowData.size())
    {
        auto iter = m_rowData[row].find(roleIndex);
        if (iter != m_rowData[row].end())
        {
            return iter.value();
        }
        else
        {
            return QVariant();
        }
    }
    else
    {
        return QVariant();
    }
}

void GenericListModel::setRowData(qint32 row, const QVariant& data, int32_t roleIndex){
    if (m_rowData.size() <= row)
    {
        beginInsertRows(QModelIndex(), m_rowData.size(), row);
        while (m_rowData.size() <= row)
        {
            QHash<int32_t, QVariant> map;
            m_rowData.push_back(map);
        }

        m_rowData[row].insert(roleIndex, data);

        endInsertRows();
        emit sizeChanged();
    }
    else
    {
        QVector<int> rolesChanged;
        rolesChanged.append(roleIndex);
        m_rowData[row].insert(roleIndex, data);
        emit dataChanged(index(row, 0), index(row, 0), rolesChanged);
    }
}

void GenericListModel::setRowData(qint32 row, const std::vector<QVariant>& data, const QVector<int>& roleIndex)
{
    if (m_rowData.size() <= row)
    {
        beginInsertRows(QModelIndex(), m_rowData.size(), row);
        while (m_rowData.size() <= row)
        {
            QHash<int32_t, QVariant> map;
            m_rowData.push_back(map);
        }

        for (int32_t i = 0; i < roleIndex.size(); ++i)
        {
            m_rowData[row].insert(roleIndex[i], data[i]);
        }

        endInsertRows();
        emit sizeChanged();
    }
    else
    {
        for (int32_t i = 0; i < roleIndex.size(); ++i)
        {
            m_rowData[row].insert(roleIndex[i], data[i]);
        }
        emit dataChanged(index(row, 0), index(row, 0), roleIndex);
    }
}

int32_t GenericListModel::GenericListModel::appendRowData(const QVariant& data, int32_t roleIndex)
{
    int32_t newIndex = m_rowData.size();
    insertRowData(m_rowData.size(), data, roleIndex);
    return newIndex;
}

int32_t GenericListModel::appendRowData(const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex)
{
    int32_t newIndex = m_rowData.size();
    insertRowData(m_rowData.size(), data, roleIndex);
    return newIndex;
}

void GenericListModel::insertRowData(qint32 row, const QVariant& data, int32_t roleIndex)
{
    int32_t actualRow = std::min(row, m_rowData.size());
    beginInsertRows(QModelIndex(), actualRow, actualRow);
    QHash<int32_t, QVariant> map;
    map.insert(roleIndex, data);
    m_rowData.insert(actualRow, map);

    endInsertRows();
    emit sizeChanged();
}

void GenericListModel::insertRowData(qint32 row, const std::vector<QVariant>& data, QVector<int> roleIndex)
{
    int32_t actualRow = std::min(row, m_rowData.size());
    beginInsertRows(QModelIndex(), actualRow, actualRow);
    QHash<int32_t, QVariant> map;
    for (int32_t i = 0; i < roleIndex.size(); ++i)
    {
       map.insert(roleIndex[i], data[i]);
    }
    m_rowData.insert(actualRow, map);

    endInsertRows();
    emit sizeChanged();
}

void GenericListModel::erase(qint32 row)
{
    beginRemoveRows(QModelIndex(), row, row);
    m_rowData.removeAt(row);
    endRemoveRows();
    emit sizeChanged();
}

QHash<int, QByteArray> GenericListModel::roleNames() const
{
    return m_roleNames;
}

void GenericListModel::clear(qint32 row, int32_t roleIndex)
{
    if (row < m_rowData.size())
    {
        auto iter = m_rowData[row].find(roleIndex);
        if (iter != m_rowData[row].end())
        {
            m_rowData[row].remove(roleIndex);
            emit dataChanged(index(row, 0), index(row, 0));
        }
    }
}
void GenericListModel::clear(qint32 row)
{
    if (row < m_rowData.size())
    {
        if (!m_rowData[row].isEmpty())
        {
            m_rowData[row].clear();
            emit dataChanged(index(row, 0), index(row, 0));
        }
    }
}


bool GenericListModel::containsRole(qint32 row, int32_t roleIndex)
{
    if (row < m_rowData.size())
    {
        return m_rowData[row].contains(roleIndex);
    }
    else
    {
        return false;
    }
}

void GenericListModel::reorder(const std::vector<int32_t>& ordering)
{
    QList<QHash<int32_t, QVariant> > swapArea;
    for (int32_t i = 0; i < m_rowData.size(); ++i)
    {
        QHash<int32_t, QVariant> temp;
        temp.swap(m_rowData[ordering[i]]);
        swapArea.append(temp);
    }
    m_rowData.swap(swapArea);
}

qint32 GenericListModel::size() const
{
    return m_rowData.size();
}

