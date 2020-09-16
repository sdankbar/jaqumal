
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
            text: model.text + model.R1 + model.R2 +model.R3 +model.R4 +model.R5 +model.R6 +model.R7 +model.R8 +model.R9 +
            	model.R10 + model.R11 + model.R12 +model.R13 +model.R14 +model.R15 + model.R16 + model.R17 + model.R18 + model.R19 +
            	model.R20 + model.R21 + model.R22 +model.R23 +model.R24 +model.R25 + model.R26 + model.R27 + model.R28 + model.R29 +
            	model.R30 + model.R31 + model.R32 +model.R33 +model.R34 +model.R35 + model.R36 + model.R37 + model.R38 + model.R39 +
            	model.R40 + model.R41 + model.R42 +model.R43 +model.R44 +model.R45 + model.R46 + model.R47 + model.R48 + model.R49 +
            	model.R50 + model.R51 + model.R52 +model.R53 +model.R54 +model.R55 + model.R56 + model.R57 + model.R58 + model.R59 +
            	model.R60 + model.R61 + model.R62 +model.R63 +model.R64 +model.R65 + model.R66 + model.R67 + model.R68 + model.R69 +
            	model.R70 + model.R71 + model.R72 +model.R73 +model.R74 +model.R75 + model.R76 + model.R77 + model.R78 + model.R79 +
            	model.R80 + model.R81 + model.R82 +model.R83 +model.R84 +model.R85 + model.R86 + model.R87 + model.R88 + model.R89 +
            	model.R90 + model.R91 + model.R92 +model.R93 +model.R94 +model.R95 + model.R96 + model.R97 + model.R98 + model.R99
            x: model.x
            y: model.y

            textFormat: Text.PlainText
        }
    }
}
