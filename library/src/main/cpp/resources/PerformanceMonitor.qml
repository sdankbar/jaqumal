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
import QtQuick.Controls 6.5
import com.github.sdankbar.jaqumal 0.4

Item {
    id: root
    visible: false

    property var window

    Rectangle {
        border.color: "black"
        anchors.fill: parent

        Text {
            text: Math.round(1000.0 / PerfModel.AVERAGE_TOTAL_TIME_MILLI) + " Average Sync/Render/Swap Time"
        }
    }

    EventBuilder {
        id: eventing
    }

    Connections {
        target: window

        onBeforeSynchronizing: {
            eventing.perfEvent(EventBuilder.BEFORE_SYNC)
        }

        onBeforeRendering: {
            eventing.perfEvent(EventBuilder.BEFORE_RENDER)
        }

        onAfterRendering: {
            eventing.perfEvent(EventBuilder.AFTER_RENDER)
        }

        onFrameSwapped: {
            eventing.perfEvent(EventBuilder.FRAME_SWAP)
        }
    }

}
