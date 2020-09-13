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
#include "jpolyline.h"
#include <QSGGeometryNode>
#include <QSGGeometry>
#include <QSGFlatColorMaterial>

JPolyline::JPolyline(QQuickItem* parent) :
    QQuickItem(parent),
    m_polyline(),
    m_strokeColor(0, 0, 0),
    m_strokeWidth(1)
{
    setFlag(ItemHasContents, true);
}

JPolyline::~JPolyline()
{
    // Empty Implementation
}

QSGNode* JPolyline::updatePaintNode(QSGNode* oldNode, UpdatePaintNodeData* data)
{
    Q_UNUSED(data);

    QSGGeometryNode* geoNode = nullptr;
    QSGGeometry* geo = nullptr;
    QSGFlatColorMaterial* material = nullptr;

    const qint32 pointCount = m_polyline.size();
    if (oldNode == nullptr)
    {
        geoNode = new QSGGeometryNode();

        geo = new QSGGeometry(QSGGeometry::defaultAttributes_Point2D(), pointCount);
        geo->setDrawingMode(QSGGeometry::DrawLineStrip);
        geoNode->setGeometry(geo);
        geoNode->setFlag(QSGNode::OwnsGeometry);

        material = new QSGFlatColorMaterial();
        geoNode->setMaterial(material);
        geoNode->setFlag(QSGNode::OwnsMaterial);
    }
    else
    {
        geoNode = static_cast<QSGGeometryNode*>(oldNode);
        geo = geoNode->geometry();
        geo->allocate(pointCount);
        material = static_cast<QSGFlatColorMaterial*>(geoNode->material());
    }

    geo->setLineWidth(m_strokeWidth);
    material->setColor(m_strokeColor);

    QSGGeometry::Point2D* vertexArray = geo->vertexDataAsPoint2D();
    for (const QPointF& p : m_polyline)
    {
        vertexArray->set(static_cast<float>(p.x()), static_cast<float>(p.y()));
        ++vertexArray;
    }

    geoNode->markDirty(QSGNode::DirtyGeometry);
    geoNode->markDirty(QSGNode::DirtyMaterial);

    return geoNode;
}

const QPolygonF& JPolyline::polyline() const
{
    return m_polyline;
}

void JPolyline::setPolyline(const QPolygonF& polyline)
{
    if (m_polyline != polyline)
    {
        m_polyline = polyline;
        emit polylineChanged(m_polyline);
        update();
    }
}

const QColor& JPolyline::strokeColor() const
{
    return m_strokeColor;
}

void JPolyline::setStrokeColor(const QColor& strokeColor)
{
    if (m_strokeColor != strokeColor)
    {
        m_strokeColor = strokeColor;
        emit strokeColorChanged(m_strokeColor);
        update();
    }
}

qint32 JPolyline::strokeWidth() const
{
    return m_strokeWidth;
}

void JPolyline::setStrokeWidth(qint32 strokeWidth)
{
    if (m_strokeWidth != strokeWidth)
    {
        m_strokeWidth = strokeWidth;
        emit strokeWidthChanged(m_strokeWidth);
        update();
    }
}
