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
import QtQuick.Window 2.10
import QtQuick.Controls 6.5
import QtQuick.Dialogs
import com.github.sdankbar.jaqumal 0.4

Window {
    visible: true
    width: 800
    height: 600
    x: 400
    y: 400
    title: qsTr("LazyTable")
    id: mainWindow

    Flickable {
        id: headerFlickable
        Row {
            Repeater {
                id: headerRepeater
                property int sortingIndex: -1
                model: headers
                delegate: ColumnHeader {
                    text: model.text
                    sortActive: model.index === headerRepeater.sortingIndex
                }
            }
        }
    }

    Flickable {
        id: flickable
        anchors.topMargin: 30
        anchors.fill: parent
        clip: true
        boundsBehavior: Flickable.StopAtBounds
        ScrollBar.vertical: ScrollBar {
        }
        contentHeight: lazy_model.root.totalSize
        contentWidth: 7 * 150
        onContentYChanged: {
            lazy_model_invoke.addInteger(contentY)
            lazy_model_invoke.invoke("setScrollPosition")
        }
        onContentXChanged: {
            headerFlickable.contentX = contentX
        }

        onHeightChanged: {
            lazy_model_invoke.addInteger(height)
            lazy_model_invoke.invoke("setWindowSize")
        }

        Repeater {
            id: listView
            anchors.fill: parent
            model: lazy_model

            delegate: Item {
                width: 700
                height: 40
                x: 0
                y: model.pos || model.pos === 0 ? model.pos : -1
                visible: y >= 0

                Row {
                    anchors.fill: parent
                    Cell {
                        text:  model.text ? model.text : ""
                    }
                    Cell {
                        text:  model.text1 ? model.text1 : ""
                    }
                    Cell {
                        text:  model.text2 ? model.text2 : ""
                    }
                    Cell {
                        text:  model.text3 ? model.text3 : ""
                    }
                    Cell {
                        text:  model.text4 ? model.text4 : ""
                    }
                    Cell {
                        text:  model.text5 ? model.text5 : ""
                    }
                    Cell {
                        text:  model.text6 ? model.text6 : ""
                    }
                }// end Row
            }
        }
    }




}
