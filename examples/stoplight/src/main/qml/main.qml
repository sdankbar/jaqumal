
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
import QtQuick.Controls 6.0
import SharedComponents 1.0
import com.github.sdankbar.jaqumal 0.4

Window {
    id: root
    visible: true
    width: 110
    height: 440
    x: 400
    y: 400
    title: qsTr("StopLight")

    EventBuilder {
        id: eventing
    }

    EventDispatcher {
        id: dispatch
        allowedEvents: ["TestQMLEvent"]

        onEventReceived: function(eventName, args) {
            log.info("TEST EVENT: " + args.time)
        }
    }

    InvokeTarget {
        targetName: "test_invoke_target"

        onInvoked: {
            console.error("invoked target:"+args.data)
            returnValue = args.data + 9
        }

    }

    JDevToolsGUI {
        mainWindow: root
    }

    Item {
        anchors.fill: parent
        focus: true

        Rectangle {
            anchors.fill: parent
            color: "gray"

            Column {
                spacing: 10
                anchors.fill: parent
                anchors.margins: 5

                MouseArea {
                    width: 100
                    height: 100
                    Circle {
                        color: "red"
                        opacity: model.lightColor === "red" ? 1 : 0.2
                    }
                    onClicked: {
                        log.info("USER INPUT- clicked RED light")
                        test_invokable.addString("RED INVOKED")
                        test_invokable.invoke("function1")
                        test_invokable.addInteger(3)
                        console.error("ret="+test_invokable.invoke("function2"))
                    }
                }
                Rectangle {
                    width: 100
                    height: 100
                    color: "yellow"
                    opacity: model.lightColor === "yellow" ? 1 : 0.2
                    radius: 50
                }
                Rectangle {
                    width: 100
                    height: 100
                    color: "green"
                    opacity: model.lightColor === "green" ? 1 : 0.2
                    radius: 50
                }
                Button {
                    width: 100
                    height: 100
                    text: "Walk"
                }
            }
        }
    }
}
