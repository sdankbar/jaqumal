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
import QtQuick 2.10
import QtQuick.Window 2.10
import QtQuick.Controls 1.4
import QtQuick.Dialogs 1.3
import QtQuick.Controls.Styles 1.4
import QtGraphicalEffects 1.0
import com.github.sdankbar.jaqumal 0.4

Window {
    visible: true
    width: 800
    height: 600
    x: 400
    y: 400
    title: qsTr("ColorEditor")
    id: mainWindow

    readonly property int presetListWidth: 350

    EventBuilder {
        id: eventing
    }

    Row {
        Rectangle {
            width: presetListWidth
            height: 600
            border.width: 1
            border.color: "gray"

            ScrollView {
                anchors.fill: parent
                ListView {
                    anchors.fill: parent
                    spacing: 1

                    model: presetColors
                    delegate: presetColorsDelegate
                }
            }
        }

        Item {
            width: 70
            height: 600
            Column {
                property int spaceForPadding: (parent.height - 2 * 50)
                topPadding: spaceForPadding / 4
                bottomPadding: spaceForPadding / 4
                spacing: spaceForPadding / 4

                JButton {
                    model: addPresetColor
                    width: 60
                    height: 50

                }

                JButton {
                    model: saveColors
                    width: 60
                    height: 50
                }
            }
        }
    }

    Component {
        id: presetColorsDelegate
        Item {
            width: presetListWidth
            height: 28

            Row {
                leftPadding: 5
                spacing: 5

                Rectangle {
                    width: 200
                    height: parent.height
                    border.width: 2
                    border.color: "black"

                    TextInput {
                        text: colorName
                        width: 200
                        x: 5
                        y: 2

                        onEditingFinished: {
                            eventing.addInteger(index);
                            eventing.addString(text);
                            eventing.fireEvent("PresetColorNameEdited")
                        }

                        onAccepted: {
                            focus = false
                        }
                    }
                }
                Rectangle {
                    width:50
                    height: parent.height

                    color: colorRGB
                }

                Button {
                    width: 70
                    text: "Edit"

                    onClicked: {
                        presetColorPicker.editedColorIndex = index
                        presetColorPicker.open()
                    }
                }
            }
        }
    }

    ColorDialog {
        id: presetColorPicker
        title: "Select a color"

        property var editedColorIndex

        modality: Qt.ApplicationModal

        onAccepted: {
            eventing.addInteger(editedColorIndex)
            eventing.addColor(color);
            eventing.fireEvent("PresetColorEdited");
        }
    }
}
