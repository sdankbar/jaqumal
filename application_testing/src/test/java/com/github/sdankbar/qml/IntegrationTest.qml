
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
import QtQuick 2.10
import QtQuick.Window 2.10
import QtQuick.Controls 6.5
import com.github.sdankbar.jaqumal 0.4

Window {
    id: root
    visible: true
    width: 400
    height: 200
    x: 400
    y: 400
    title: qsTr("IntegrationTest")

    JDevToolsGUI {
        mainWindow: root
        generateJUnit: true
        generateQTTest: true
    }

    Grid {
        rows: 4
        columns: 10
        spacing: 10
        anchors.fill: parent

        Repeater {
            model: 40

            delegate: CheckBox {
                text: model.index
            }
        }
    }

    Rectangle {
        anchors.bottom: parent.bottom

        height: 20
        width: 100
        border.color: "black"

        TextInput {
            anchors.fill: parent
            anchors.margins: 2
        }
    }
}
