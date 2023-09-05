
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
import QtQuick.Dialogs 1.3
import QtCharts 2.3
import com.github.sdankbar.jaqumal 0.4

Window {
    visible: true
    width: 800
    height: 600
    x: 400
    y: 400
    title: qsTr("Line Chart")
    id: mainWindow

    EventBuilder {
        id: eventing
    }

    ChartView {
        id: chart
        title: "Line"
        anchors.fill: parent
        antialiasing: true

        Keys.onDigit1Pressed: {
            chart.zoomIn()
        }
        Keys.onDigit2Pressed: {
            chart.zoomOut()
        }
        Keys.onUpPressed: {
            chart.scrollUp(5)
        }
        Keys.onDownPressed: {
            chart.scrollDown(5)
        }
        Keys.onLeftPressed: {
            chart.scrollLeft(5)
        }
        Keys.onRightPressed: {
            chart.scrollRight(5)
        }

        JLineSeries {
            name: "LineSeries"
            model: lineSeries
        }

        Component.onCompleted: {
            chart.forceActiveFocus()
        }
    }
}
