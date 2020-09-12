
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
import QtQuick.Controls.Styles 1.4
import QtGraphicalEffects 1.0
import com.github.sdankbar.jaqumal 0.4

Window {
    visible: true
    width: 850
    height: 850
    x: 400
    y: 400
    title: qsTr("Throughput")

    Repeater {
        x: 20
        y: 20
        model: list_model

        delegate: Text {
            text: model.text + model.R1 + model.R2 +model.R3 +model.R4 +model.R5 +model.R6 +model.R7 +model.R8 +model.R9 +model.R10 +
                model.R11 + model.R12 +model.R13 +model.R14 +model.R15 +model.R16
            x: model.x
            y: model.y
        }
    }
}
