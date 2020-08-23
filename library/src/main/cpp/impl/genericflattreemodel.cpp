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
#include "genericflattreemodel.h"
#include "qmlinterface.h"
#include <applicationfunctions.h>

namespace
{
const QString SUBMODEL_ROLE("SUBMODEL");
}

std::deque<int32_t> toTreePath(int32_t* index, int32_t count)
{
    std::deque<int32_t> indicies;
    indicies.resize(count);
    for (int i = 0; i < count; ++i)
    {
        indicies[i] = index[i];
    }
    return indicies;
}

void* createGenericFlatTreeModel(const char* modelName, char** roleNames, int32_t* roleIndices, int32_t length)
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

        //return QMLLibrary::library->createGenericFlatTreeModel(modelNameStr, roleNamesList, roleIndicesList);
        return nullptr;
    }
    else
    {
        return nullptr;
    }
}

void setGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        int32_t size;
        modelPtr->setRowData(indicies, toQVariant(data, size), roleIndex);

    }
}

void setGenericFlatTreeModelDataMulti(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);

        std::vector<QVariant> variants = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }

        modelPtr->setRowData(indicies, variants, roleIndicies);
    }
}

void insertGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        int32_t size;
        modelPtr->insertRowData(indicies, toQVariant(data, size), roleIndex);
    }
}

void insertGenericFlatTreeModelDataMulti(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);

        std::vector<QVariant> variants = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }

        modelPtr->insertRowData(indicies, variants, roleIndicies);
    }
}

void clearGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        modelPtr->clear(indicies, roleIndex);
    }
}
void clearAllGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        modelPtr->clear(indicies);
    }
}
void eraseGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        modelPtr->erase(indicies);
    }
}
int32_t appendGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        int32_t size;
        return modelPtr->appendRowData(toQVariant(data, size), roleIndex, indicies);
    }
    else
    {
        return -1;
    }
}

int32_t appendGenericFlatTreeModelDataMulti(void* tempPointer, int32_t* index, int32_t elementCount, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);

        std::vector<QVariant> variants = toQVariantList(data, valueCount);
        QVector<int> roleIndicies(valueCount);
        roleIndicies.reserve(valueCount);
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies[i] = roleIndex[i];
        }

        return modelPtr->appendRowData(variants, roleIndicies, indicies);
    }
    else
    {
        return -1;
    }
}

void* getGenericFlatTreeModelData(void* tempPointer, int32_t* index, int32_t elementCount, int32_t roleIndex, int32_t& length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        return fromQVariant(modelPtr->getRowData(indicies, roleIndex), length, true);
    }
    else
    {
        return nullptr;
    }
}

void clearGenericFlatTreeModelRole(void* tempPointer, int32_t* index, int32_t elementCount, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        modelPtr->clear(indicies, roleIndex);
    }
}
void clearGenericFlatTreeModel(void* tempPointer, int32_t* index, int32_t elementCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        modelPtr->clear(indicies);
    }
}
bool isGenericFlatTreeModelRolePresent(void* tempPointer, int32_t* index, int32_t elementCount, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        return modelPtr->containsRole(indicies, roleIndex);
    }
    else
    {
        return false;
    }
}
int32_t getGenericFlatTreeModelSize(void* tempPointer, int32_t* index, int32_t elementCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);
        return modelPtr->getSize(indicies);
    }
    else
    {
        return 0;
    }
}

void reorderGenericFlatTreeModel(void* tempPointer, int32_t* index, int32_t elementCount, int32_t* ordering, int32_t length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericFlatTreeModel*>(tempPointer);
        std::deque<int32_t> indicies = toTreePath(index, elementCount);

        std::vector<int32_t> orderingVec;
        orderingVec.resize(length);
        for (int32_t i = 0; i < length; ++i)
        {
            orderingVec[i] = ordering[i];
        }

        modelPtr->reorder(indicies, orderingVec);
    }
}

GenericFlatTreeModel::GenericFlatTreeModel(const QString& modelName, const QHash<int, QByteArray>& roleMap, int32_t depth)
    : QAbstractListModel(nullptr),
      m_modelName(modelName),
      m_roleNames(roleMap),
      m_depth(depth)
{
    auto iter = m_roleNames.constBegin();
    auto end = m_roleNames.constEnd();
    for (; iter != end; ++iter)
    {
        if (QString(iter.value()) == SUBMODEL_ROLE)
        {
            m_submodelIndex = iter.key();
        }
        else if (QString(iter.value()) == "PARENT_INDEX")
        {
            m_parentRoleIndex = iter.key();
        }
        else if (QString(iter.value()) == "FLAT_INDEX_INDEX")
        {
            m_flatIndexRoleIndex = iter.key();
        }

        m_indexToStringRoleMap[iter.key()] = iter.value();
        m_stringToIndexRoleMap[iter.value()] = iter.key();
    }
}

const QString& GenericFlatTreeModel::modelName() const
{
	return m_modelName;
}

QSharedPointer<GenericFlatTreeModel> GenericFlatTreeModel::getSubmodel(qint32 row, bool create)
{
    if (row < m_rowData.size())
    {
        auto iter = m_rowData[row].find(m_submodelIndex);
        if (iter != m_rowData[row].end())
        {
            return qvariant_cast<QSharedPointer<GenericFlatTreeModel> >(iter.value());
        }
        else if (create)
        {
            QSharedPointer<GenericFlatTreeModel> p =
                    QSharedPointer<GenericFlatTreeModel>::create(m_modelName, m_roleNames, m_depth + 1);
            m_rowData[row].insert(m_submodelIndex, QVariant::fromValue(p));
            return p;
        }
        else
        {
            return QSharedPointer<GenericFlatTreeModel>();
        }
    }
    else
    {
        return QSharedPointer<GenericFlatTreeModel>();
    }
}

int GenericFlatTreeModel::rowCount(const QModelIndex &parent) const
{
    // For list models only the root node (an invalid parent) should return the list's size. For all
    // other (valid) parents, rowCount() should return 0 so that it does not become a native tree model.
    if (parent.isValid())
    {
        return 0;
    }
    else
    {
        return m_flatData.size();
    }
}

QVariant GenericFlatTreeModel::data(const QModelIndex &index, int role) const
{
    if (!index.isValid())
    {
        return QVariant();
    }
    if (index.row() >= 0 && index.row() < m_flatData.size())
    {
        const auto& data = m_flatData[index.row()];
        if (m_indexToStringRoleMap.contains(role))
        {
            if (data->contains(role))
            {
                return *(data->find(role));
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
    else
    {
        return QVariant();
    }
}

bool GenericFlatTreeModel::setData(const QModelIndex &i, const QVariant &value, int role)
{
    if (!i.isValid())
    {
        return false;
    }

    if (i.row() >= 0 && i.row() < m_flatData.size())
    {
        auto& data = m_flatData[i.row()];
        data->insert(role, value);
        emit dataChanged(index(i.row(), 0), index(i.row(), 0));
        return true;
    }
    else
    {
        return false;
    }
}

QVariant GenericFlatTreeModel::getRowData(std::deque<int32_t>& indicies, int32_t roleIndex)
{
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (0 <= row && row < m_rowData.size())
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
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            return p->getRowData(indicies, roleIndex);
        }
        else
        {
            return QVariant();
        }
    }
}

const QHash<int32_t, QVariant>& GenericFlatTreeModel::getRowData(std::deque<int32_t>& indicies)
{
    static QHash<int32_t, QVariant> empty;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (0 <= row && row < m_rowData.size())
        {
            return m_rowData[row];
        }
        else
        {
            return empty;
        }
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0], false);
        if (p)
        {
            indicies.pop_front();
            return p->getRowData(indicies);
        }
        else
        {
            return empty;
        }
    }
}

QVariant GenericFlatTreeModel::getRowData(qint32 flatRow, qint32 roleIndex) const
{
    static QVariant empty;
    if (0 <= flatRow && flatRow < m_flatData.size())
    {
        return m_flatData[flatRow]->value(roleIndex);
    }
    else
    {
        return empty;
    }
}

QVariant GenericFlatTreeModel::getRowData(qint32 flatRow, const QString& roleName) const
{
    auto iter = m_stringToIndexRoleMap.find(roleName);
    if (iter != m_stringToIndexRoleMap.end())
    {
        return getRowData(flatRow, iter.value());
    }
    else
    {
        return QVariant();
    }
}

void GenericFlatTreeModel::setRowData(qint32 flatRow, const QVariant& value, qint32 roleIndex)
{
    if (flatRow >= 0 && flatRow < m_flatData.size())
    {
        m_flatData[flatRow]->insert(roleIndex, value);
        emit dataChanged(index(flatRow, 0), index(flatRow, 0));
    }
}

void GenericFlatTreeModel::setRowData(qint32 flatRow, const QVariant& value, const QString& roleName)
{
    auto iter = m_stringToIndexRoleMap.find(roleName);
    if (iter != m_stringToIndexRoleMap.end())
    {
        return setRowData(flatRow, value, iter.value());
    }
}

qint32 GenericFlatTreeModel::getSize() const
{
    return m_flatData.size();
}

void GenericFlatTreeModel::setRowData(std::deque<int32_t>& indicies, const QVariant& data, int32_t roleIndex){
    std::deque<int32_t> indiciesCopy = indicies;
    int32_t added = 0;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        while (m_rowData.size() <= row)
        {
            QHash<int32_t, QVariant> map;
            m_rowData.push_back(map);
            ++added;
        }

        m_rowData[row].insert(roleIndex, data);
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->setRowData(indicies, data, roleIndex);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, added);
    }
}

void GenericFlatTreeModel::setRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int>& roleIndex)
{
    std::deque<int32_t> indiciesCopy = indicies;
    int32_t added = 0;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        while (m_rowData.size() <= row)
        {
            QHash<int32_t, QVariant> map;
            m_rowData.push_back(map);
            ++added;
        }

        for (uint32_t i = 0; i < data.size(); ++i)
        {
            m_rowData[row].insert(roleIndex[i], data[i]);
        }
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->setRowData(indicies, data, roleIndex);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, added);
    }
}

int32_t GenericFlatTreeModel::appendRowData(const QVariant& data, int32_t roleIndex, std::deque<int32_t>& indicies)
{
    std::deque<int32_t> indiciesCopy = indicies;
    int32_t retVal = -1;
    if (indicies.size() == 0)
    {
        int32_t newIndex = m_rowData.size();
        std::deque<int32_t> newIndexList;
        newIndexList.push_back(newIndex);
        setRowData(newIndexList, data, roleIndex);
        retVal = newIndex;
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            retVal = p->appendRowData(data, roleIndex, indicies);
        }
        else
        {
            retVal = -1;
        }
    }

    if (m_depth == 0)
    {
        indiciesCopy.push_back(retVal);
        updateFlatData(indiciesCopy, 1);
    }
    return retVal;
}

int32_t GenericFlatTreeModel::appendRowData(const std::vector<QVariant>& data, const QVector<int>& roleIndex, std::deque<int32_t>& indicies)
{
    std::deque<int32_t> indiciesCopy = indicies;
    int32_t retVal = -1;
    if (indicies.size() == 0)
    {
        int32_t newIndex = m_rowData.size();
        std::deque<int32_t> newIndexList;
        newIndexList.push_back(newIndex);
        setRowData(newIndexList, data, roleIndex);
        retVal = newIndex;
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            retVal = p->appendRowData(data, roleIndex, indicies);
        }
        else
        {
            retVal = -1;
        }
    }

    if (m_depth == 0 && retVal != -1)
    {
        indiciesCopy.push_back(retVal);
        updateFlatData(indiciesCopy, 1);
    }
    return retVal;
}

void GenericFlatTreeModel::insertRowData(std::deque<int32_t>& indicies, const QVariant& data, int32_t roleIndex)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (row < m_rowData.size())
        {
            QHash<int32_t, QVariant> map;
            m_rowData.insert(row, map);
        }

        m_rowData[row].insert(roleIndex, data);
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->insertRowData(indicies, data, roleIndex);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, 1);
    }
}

void GenericFlatTreeModel::insertRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data,
                                         const QVector<int>& roleIndex)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        int32_t actualRow = std::min(row, m_rowData.size());
        QHash<int32_t, QVariant> map;
        for (uint32_t i = 0; i < data.size(); ++i)
        {
            map.insert(roleIndex[i], data[i]);
        }
        m_rowData.insert(actualRow, map);
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->insertRowData(indicies, data, roleIndex);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, 1);
    }
}

void GenericFlatTreeModel::erase(std::deque<int32_t>& indicies)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        m_rowData.removeAt(indicies[0]);
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->erase(indicies);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, 1);
    }
}

QHash<int, QByteArray> GenericFlatTreeModel::roleNames() const
{
    return m_roleNames;
}

void GenericFlatTreeModel::clear(std::deque<int32_t>& indicies, int32_t roleIndex)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (row < m_rowData.size())
        {
            auto iter = m_rowData[row].find(roleIndex);
            if (iter != m_rowData[row].end())
            {
                m_rowData[row].remove(roleIndex);
            }
        }
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->clear(indicies, roleIndex);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, 0);
    }
}
void GenericFlatTreeModel::clear(std::deque<int32_t>& indicies)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (row < m_rowData.size())
        {
            m_rowData[row].clear();
        }
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->clear(indicies);
        }
    }

    if (m_depth == 0)
    {
        updateFlatData(indiciesCopy, 0);
    }
}

int32_t GenericFlatTreeModel::getSize(std::deque<int32_t>& indicies)
{
    if (indicies.size() == 0)
    {
        return m_rowData.size();
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            return p->getSize(indicies);
        }
        else
        {
            return 0;
        }
    }
}

bool GenericFlatTreeModel::containsRole(std::deque<int32_t>& indicies, int32_t roleIndex)
{
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        if (row < m_rowData.size())
        {
            return m_rowData[row].contains(roleIndex);
        }
        else
        {
            return false;
        }
    }
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            return p->containsRole(indicies, roleIndex);
        }
        else
        {
            return false;
        }
    }
}

void GenericFlatTreeModel::updateFlatData(std::deque<int32_t>& indicies, int32_t rowsChanged)
{
    // TODO test and improve this function
    if (rowsChanged > 0)
    {
        QList<QHash<int32_t, QVariant>*> list;
        int32_t flatIndex = 0;
        updateFlatData(indicies, list, -1, flatIndex);

        if (rowsChanged > 1)
        {
            beginResetModel();
            m_flatData.swap(list);
            endResetModel();
        }
        else if (list.size() > m_flatData.size())
        {
            // Gained a row
            int32_t newIndex = treeIndexToFlatIndex(indicies);
            beginInsertRows(QModelIndex(), newIndex, newIndex);
            m_flatData.swap(list);
            endInsertRows();
        }
        else if (list.size() < m_flatData.size())
        {
            // Lost a row
            int32_t removedIndex = 0;
            for (; removedIndex < list.size(); ++removedIndex)
            {
                if (m_flatData[removedIndex] != list[removedIndex])
                {
                    break;
                }
            }
            beginRemoveRows(QModelIndex(), removedIndex, removedIndex);
            m_flatData.swap(list);
            endRemoveRows();
        }
    }


    const QHash<int32_t, QVariant>& data = getRowData(indicies);
    for (int32_t i = 0; i < m_flatData.size(); ++i)
    {
        if (&data == m_flatData[i])
        {
          emit dataChanged(index(i, 0), index(i, 0));
        }
    }
}

void GenericFlatTreeModel::updateFlatData(std::deque<int32_t>& indicies, QList<QHash<int32_t, QVariant>* >& list, int32_t parentIndex, int32_t& flatIndex)
{
    for (int32_t i = 0; i < m_rowData.size(); ++i)
    {
        list.append(&m_rowData[i]);
        m_rowData[i].insert(m_parentRoleIndex, parentIndex);
        m_rowData[i].insert(m_flatIndexRoleIndex, flatIndex++);

        QSharedPointer<GenericFlatTreeModel> submodel = getSubmodel(i, false);
        if (submodel)
        {
            submodel->updateFlatData(indicies, list, list.size() - 1, flatIndex);
        }
    }
}

int32_t GenericFlatTreeModel::treeIndexToFlatIndex(const std::deque<int32_t>& indicies)
{
    std::deque<int32_t> copy = indicies;
    QHash<int32_t, QVariant> map = getRowData(copy);
    auto iter = map.find(m_flatIndexRoleIndex);
    if (iter != map.end())
    {
        return iter.value().toInt();
    }
    else
    {
        return -1;
    }
}

void GenericFlatTreeModel::reorder(std::deque<int32_t>& indicies, const std::vector<int32_t>& ordering)
{
    if (indicies.size() == 0)
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
    else
    {
        QSharedPointer<GenericFlatTreeModel> p = getSubmodel(indicies[0]);
        if (p)
        {
            indicies.pop_front();
            p->reorder(indicies, ordering);
        }
    }
}
