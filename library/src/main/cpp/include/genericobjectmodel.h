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
#ifndef GENERICOBJECTMODEL_H
#define GENERICOBJECTMODEL_H

#include <QQmlPropertyMap>

extern "C"
{
extern void* createGenericObjectModel(const char* modelName, char** roleNames,
                                      int32_t length);
extern void registerValueChangedCallback(void* modelID, void c(const char*, const char*, int32_t));

extern void setGenericObjectModelData(void* modelID, void* data, int32_t roleIndex);
extern void setGenericObjectModelDataMulti(void* modelID, void* data, int32_t* roleIndex, int32_t valueCount);
extern void* getGenericObjectModelData(void* modelID, int32_t roleIndex, int32_t& length);

extern void clearGenericObjectModelRole(void* modelID, int32_t role);
extern void clearGenericObjectModel(void* modelID);
extern bool isGenericObjectModelRolePresent(void* modelID, int32_t role);
}

class GenericObjectModel : public QQmlPropertyMap
{
    Q_OBJECT

public:
    explicit GenericObjectModel(const std::vector<QString>& roles);

    void setData(const QVariant& data, int32_t roleIndex);
    void setData(const std::vector<QVariant>& data, const std::vector<int32_t>& roleIndex);
    Q_INVOKABLE void setData(const QVariant& data, const QString& propertyName);

    Q_INVOKABLE QVariant getData(const QString& propertyName) const;
    QVariant getData(int32_t roleIndex) const;
    char* getDataSerialized(int32_t roleIndex, int32_t& length) const;


    void clear(int32_t roleIndex);
    void clear();

    bool containsRole(int32_t roleIndex);

    void registerValueChangedCallback(std::function<void(const char*, const char*, int32_t)> c);

private slots:

    void onValueChanged(const QString& key, const QVariant& value);

private:

    void callbackListeners(const QString& key, const QVariant& newValue);

    std::vector<QString> m_roleMap;

    std::vector<std::function<void(const char*, const char*, int32_t)> > callbacks;
};

#endif // GENERICOBJECTMODEL_H
