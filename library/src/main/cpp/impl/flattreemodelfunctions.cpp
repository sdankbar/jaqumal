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
#include "flattreemodelfunctions.h"

#include "jniutilities.h"

#include <eventbuilder.h>
#include <eventdispatcher.h>
#include <jpolyline.h>
#include <QQmlContext>
#include <QTimer>
#include <QDateTime>
#include <QUuid>
#include <QFont>
#include <QFontInfo>
#include <QFontMetrics>
#include <QScreen>
#include <QApplication>
#include <functional>

#include <applicationfunctions.h>
#include <jniutilities.h>
#include <qmldatatransfer.h>

namespace
{
const QString SUBMODEL_ROLE("SUBMODEL");
}

std::deque<int32_t> toTreePath(jint* index, int32_t count)
{
    std::deque<int32_t> indicies;
    indicies.resize(count);
    for (int i = 0; i < count; ++i)
    {
        indicies[i] = index[i];
    }
    return indicies;
}

std::deque<int32_t> toTreePath(JNIEnv* env, jintArray array)
{
    jint* data = env->GetIntArrayElements(array, nullptr);
    std::deque<int32_t> ret = toTreePath(data, env->GetArrayLength(array));
    env->ReleaseIntArrayElements(array, data, JNI_ABORT);
    return ret;
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    createGenericFlatTreeModel
 * Signature: (Ljava/lang/String;[Ljava/lang/String;[I)J
 */
jlong JNICALL createGenericFlatTreeModel(JNIEnv* env, jclass, jstring modelName, jobjectArray roleNames, jintArray roleIndices)
{
    if (ApplicationFunctions::check(env))
    {
        QString modelNameStr = JNIUtilities::toQString(env, modelName);

        const size_t length = env->GetArrayLength(roleNames);
        jint* roleArray = env->GetIntArrayElements(roleIndices, nullptr);
        QHash<int, QByteArray> roleNameMap;
        for (size_t i = 0; i < length; ++i)
        {
            jstring jStr = static_cast<jstring>(env->GetObjectArrayElement(roleNames, i));
            QString n = JNIUtilities::toQString(env, jStr);
            int index = roleArray[i];
            roleNameMap[index] = n.toUtf8();
        }

        env->ReleaseIntArrayElements(roleIndices, roleArray, JNI_ABORT);


        GenericFlatTreeModel* modelPtr = new GenericFlatTreeModel(modelNameStr, roleNameMap, 0);
        ApplicationFunctions::get()->addToContext(modelNameStr, modelPtr);
        return reinterpret_cast<jlong>(modelPtr);
    }
    else
    {
        return 0;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    appendGenericFlatTreeModelData
 * Signature: (J[I)I
 */
jint JNICALL appendGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        jint ret = modelPtr->appendRowData(indicies,
                                           QMLDataTransfer::getPendingVariants(),
                                           QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
        return ret;
    }
    else
    {
        return -1;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    clearAllGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL clearAllGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->clear(indicies);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    clearGenericFlatTreeModelData
 * Signature: (J[II)V
 */
void JNICALL clearGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path, jint role)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->clear(indicies, role);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    eraseGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL eraseGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->erase(indicies);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    getGenericFlatTreeModelData
 * Signature: (J[II)Lcom/github/sdankbar/qml/JVariant;
 */
jobject JNICALL getGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path, jint role)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        return QMLDataTransfer::toJVariant(env, modelPtr->getRowData(indicies, role));
    }
    else
    {
        return nullptr;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    getGenericFlatTreeModelSize
 * Signature: (J[I)I
 */
jint JNICALL getGenericFlatTreeModelSize(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        return modelPtr->getSize(indicies);
    }
    else
    {
        return 0;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    insertGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL insertGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->insertRowData(indicies,
                                QMLDataTransfer::getPendingVariants(),
                                QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    isGenericFlatTreeModelRolePresent
 * Signature: (J[II)Z
 */
jboolean JNICALL isGenericFlatTreeModelRolePresent(JNIEnv* env, jclass, jlong pointer, jintArray path, jint role)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        return modelPtr->containsRole(indicies, role);
    }
    else
    {
        return false;
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    reorderGenericFlatTreeModel
 * Signature: (J[I[I)V
 */
void JNICALL reorderGenericFlatTreeModel(JNIEnv* env, jclass, jlong pointer, jintArray path, jintArray ordering)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);

        size_t length = env->GetArrayLength(ordering);
        jint* array = env->GetIntArrayElements(ordering, nullptr);
        std::vector<int32_t> orderingVec;
        orderingVec.resize(length);
        for (size_t i = 0; i < length; ++i)
        {
            orderingVec[i] = array[i];
        }

        modelPtr->reorder(indicies, orderingVec);
    }
}

/*
 * Class:     com_github_sdankbar_qml_cpp_jni_flat_tree_FlatTreeQMLAPIFast
 * Method:    setGenericFlatTreeModelData
 * Signature: (J[I)V
 */
void JNICALL setGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->setRowData(indicies,
        					 QMLDataTransfer::getPendingVariants(), 
        					 QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

void JNICALL assignGenericFlatTreeModelData(JNIEnv* env, jclass, jlong pointer, jintArray path)
{
    if (ApplicationFunctions::check(env))
    {
        auto modelPtr = reinterpret_cast<GenericFlatTreeModel*>(pointer);
        std::deque<int32_t> indicies = toTreePath(env, path);
        modelPtr->assignRowData(indicies,
                             QMLDataTransfer::getPendingVariants(),
                             QMLDataTransfer::getPendingRoleIndices());
        QMLDataTransfer::clearPendingData();
    }
}

void FlatTreeModelFunctions::initialize(JNIEnv* env)
{
    JNINativeMethod methods[] = {
        JNIUtilities::createJNIMethod("createGenericFlatTreeModel",    "(Ljava/lang/String;[Ljava/lang/String;[I)J",    (void *)&createGenericFlatTreeModel),
        JNIUtilities::createJNIMethod("appendGenericFlatTreeModelData",    "(J[I)I",    (void *)&appendGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("clearAllGenericFlatTreeModelData",    "(J[I)V",    (void *)&clearAllGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("clearGenericFlatTreeModelData",    "(J[II)V",    (void *)&clearGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("eraseGenericFlatTreeModelData",    "(J[I)V",    (void *)&eraseGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("getGenericFlatTreeModelData",    "(J[II)Lcom/github/sdankbar/qml/JVariant;",    (void *)&getGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("getGenericFlatTreeModelSize",    "(J[I)I",    (void *)&getGenericFlatTreeModelSize),
        JNIUtilities::createJNIMethod("insertGenericFlatTreeModelData",    "(J[I)V",    (void *)&insertGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("isGenericFlatTreeModelRolePresent",    "(J[II)Z",    (void *)&isGenericFlatTreeModelRolePresent),
        JNIUtilities::createJNIMethod("reorderGenericFlatTreeModel",    "(J[I[I)V",    (void *)&reorderGenericFlatTreeModel),
        JNIUtilities::createJNIMethod("setGenericFlatTreeModelData",    "(J[I)V",    (void *)&setGenericFlatTreeModelData),
        JNIUtilities::createJNIMethod("assignGenericFlatTreeModelData",    "(J[I)V",    (void *)&assignGenericFlatTreeModelData),
    };
    jclass javaClass = env->FindClass("com/github/sdankbar/qml/cpp/jni/flat_tree/FlatTreeModelFunctions");
    env->RegisterNatives(javaClass, methods, sizeof(methods)/sizeof(JNINativeMethod));
    env->DeleteLocalRef(javaClass);
}

void FlatTreeModelFunctions::uninitialize(JNIEnv*)
{

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

void GenericFlatTreeModel::setRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex)
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

        for (int32_t i = 0; i < roleIndex.size(); ++i)
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

void GenericFlatTreeModel::assignRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex)
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

        m_rowData[row].clear();
        for (int32_t i = 0; i < roleIndex.size(); ++i)
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

int32_t GenericFlatTreeModel::appendRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex)
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
            retVal = p->appendRowData(indicies, data, roleIndex);
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

void GenericFlatTreeModel::insertRowData(std::deque<int32_t>& indicies, const std::vector<QVariant>& data, const QVector<int32_t>& roleIndex)
{
    std::deque<int32_t> indiciesCopy = indicies;
    if (indicies.size() == 1)
    {
        int32_t row = indicies[0];
        int32_t actualRow = std::min(row, m_rowData.size());
        QHash<int32_t, QVariant> map;
        for (int32_t i = 0; i < roleIndex.size(); ++i)
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
