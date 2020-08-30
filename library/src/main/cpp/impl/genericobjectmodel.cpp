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
#include "genericobjectmodel.h"
#include <iostream>
#include <applicationfunctions.h>
#include <memory>

void* createGenericObjectModel(const char* modelName, char** roleNames,
                              int32_t length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        QString modelNameStr(modelName);

        std::vector<QString> roles;
        for (int i = 0; i < length; ++i)
        {
            roles.push_back(roleNames[i]);
        }

        //return QMLLibrary::library->createGenericObjectModel(modelNameStr, roles);
        return nullptr;
    }
    else
    {
        return nullptr;
    }
}

void setGenericObjectModelData(void* tempPointer, void* data, int32_t roleIndex)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        int32_t size;
        //modelPtr->setData(toQVariant(data, size), roleIndex);
    }
}

void setGenericObjectModelDataMulti(void* tempPointer, void* data, int32_t* roleIndex, int32_t valueCount)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        std::vector<QVariant> variants;// = toQVariantList(data, static_cast<uint32_t>(valueCount));
        std::vector<int32_t> roleIndicies;
        roleIndicies.reserve(static_cast<uint32_t>(valueCount));
        for (int32_t i = 0; i < valueCount; ++i)
        {
            roleIndicies.push_back(roleIndex[i]);
        }

        modelPtr->setData(variants, roleIndicies);
    }
}

void* getGenericObjectModelData(void* tempPointer, int32_t roleIndex, int32_t& length)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        return modelPtr->getDataSerialized(roleIndex, length);
    }
    else
    {
        return nullptr;
    }
}

void clearGenericObjectModelRole(void* tempPointer, int32_t role)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        modelPtr->clear(role);
    }
}
void clearGenericObjectModel(void* tempPointer)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        modelPtr->clear();
    }
}
bool isGenericObjectModelRolePresent(void* tempPointer, int32_t role)
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        return modelPtr->containsRole(role);
    }
    else
    {
        return false;
    }
}
void registerValueChangedCallback(void* tempPointer, void c(const char*, const char*, int32_t))
{
    if (ApplicationFunctions::check(nullptr))
    {
        auto modelPtr = static_cast<GenericObjectModel*>(tempPointer);
        modelPtr->registerValueChangedCallback(c);
    }
}


GenericObjectModel::GenericObjectModel(const QString& modelName, const std::vector<QString>& roles)
    : QQmlPropertyMap(this, nullptr),
      m_modelName(modelName),
      m_roleMap(roles)
{
    const int32_t size = static_cast<int32_t>(roles.size());
    for (int32_t i = 0; i < size; ++i)
    {
        setData(QVariant(), i);
    }
    QObject::connect(this, &QQmlPropertyMap::valueChanged, this, &GenericObjectModel::onValueChanged);
}

const QString& GenericObjectModel::modelName() const
{
	return m_modelName;
}

QVariant GenericObjectModel::getData(int32_t roleIndex) const
{
    if (0 <= roleIndex && static_cast<uint32_t>(roleIndex) < m_roleMap.size())
    {
        return value(m_roleMap[static_cast<uint32_t>(roleIndex)]);
    }
    else
    {
        //exceptionHandler("Role does not exist");
        // TODO throw
        return QVariant();
    }
}

char* GenericObjectModel::getDataSerialized(int32_t roleIndex, int32_t& length) const
{
    return nullptr;//fromQVariant(getData(roleIndex), length, false);
}

void GenericObjectModel::setData(const QVariant& data, int32_t roleIndex)
{
    if (0 <= roleIndex && static_cast<uint32_t>(roleIndex) < m_roleMap.size())
    {
        const QString& role = m_roleMap[static_cast<uint32_t>(roleIndex)];
        if (value(role) != data)
        {
            insert(role, data);
            emit valueChanged(role, data);
        }
    }
    else
    {
        // TODO throw
        //exceptionHandler("Role does not exist");
    }
}

void GenericObjectModel::setData(const QVariant& data, const QString& propertyName)
{
    if (std::find(m_roleMap.begin(), m_roleMap.end(), propertyName) != m_roleMap.end())
    {
        if (value(propertyName) != data)
        {
            insert(propertyName, data);

            callbackListeners(propertyName, data);
        }
    }
}

void GenericObjectModel::setData(const std::vector<QVariant>& data, const std::vector<int32_t>& roleIndex)
{
    for (uint32_t i = 0; i < data.size(); ++i)
    {
        setData(data[i], roleIndex[i]);
    }
}

QVariant GenericObjectModel::getData(const QString& propertyName) const
{
    return value(propertyName);
}

void GenericObjectModel::clear(int32_t roleIndex)
{
    setData(QVariant(), roleIndex);
}

void GenericObjectModel::clear()
{
    for (uint32_t i = 0; i < m_roleMap.size(); ++i)
    {
        clear(static_cast<int32_t>(i));
    }
}

bool GenericObjectModel::containsRole(int32_t roleIndex)
{
    return getData(roleIndex) != QVariant();
}

void GenericObjectModel::onValueChanged(const QString& key, const QVariant& value)
{
    callbackListeners(key, value);
}

void GenericObjectModel::registerValueChangedCallback(std::function<void(const char*, const char*, int32_t)> c)
{
    callbacks.push_back(c);
}

void GenericObjectModel::callbackListeners(const QString& key, const QVariant& newValue)
{
    if (!callbacks.empty())
    {
        int32_t l;
        std::unique_ptr<char> data;//(fromQVariant(newValue, l, true));
        for (const auto& f: callbacks)
        {
            f(key.toStdString().c_str(), data.get(), l);
        }
    }
}
