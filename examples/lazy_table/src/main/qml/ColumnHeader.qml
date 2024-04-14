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
import QtQuick 2.11
import QtQuick.Controls 6.5

Rectangle {
    id: root
    property string text
    property bool sortActive: false
    property bool ascending: true
    property string sortKey: ""

    border.color: "black"
    width: 150
    height: 30
    Text {
        anchors.horizontalCenter: parent.horizontalCenter
        text: root.text
    }
    Text {
        visible: sortActive
        text: ascending ? "▼" : "▲"
        anchors.right: parent.right
        anchors.rightMargin: 4
    }

    MouseArea {
        anchors.fill: parent

        onClicked: {
            if (!sortActive) {
                headerRepeater.sortingIndex = model.index
                ascending = true
            } else {
                ascending = !ascending
            }

            if (sortKey !== "") {
                lazy_model_invoke.addString(sortKey)
            } else {
               lazy_model_invoke.addString(root.text)
            }

            lazy_model_invoke.addBoolean(ascending)
            lazy_model_invoke.invoke("setSortingKey")
        }
    }
}
