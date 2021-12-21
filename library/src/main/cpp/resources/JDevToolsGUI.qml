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
import QtQuick 2.0
import QtCharts 2.3
import com.github.sdankbar.jaqumal 0.4

JDevTools {
    id: toolsObj
    title: "Development Tools"
    x: 100
    y: 100
    width: 500
    height: 300

    property var mainWindow

    Column {
        anchors.fill: parent
        spacing: 10

        PerformanceMonitor {
            window: mainWindow
            width: parent.width
            height: 20
            visible: true
        }

        Row {
            spacing: 15

            Text {
                text: "Recording"
            }

            Rectangle {
                height: 20
                width: height
                radius: width / 2

                color: toolsObj.isRecording ? "red" : "green"
            }
        }

        Text {
            text: "F12 Show/hide this window"
        }
        Text {
            text: "F11 Start/stop recording user inputs"
        }
        Text {
            text: "F10 Take screenshot while recording"
        }
    }
}
