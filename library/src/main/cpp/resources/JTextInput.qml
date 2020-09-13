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
import com.github.sdankbar.jaqumal 0.4

TextInput {
    property var model

    // QPoint
    property var selectionRange: model ? model.SelectionStartStop : undefined
    property var modelFocus: model ? model.Focus : false
    property alias validatorRegex: validator.regExp

    EventBuilder {
        id: eventing
    }

    id: internalButton

    validator: RegExpValidator {
        id: validator
        regExp: model ? model.Regex : /.*/
    }

    onSelectionRangeChanged: {
        if (selectionRange) {
            select(selectionRange.x, selectionRange.y)
        }
    }

    onAccepted: {
        if (model) {
            eventing.textInputAcceptedEvent(model.ModelName)
        }
    }

    onEditingFinished: {
        if (model) {
            eventing.textInputEditingFinishedEvent(model.ModelName)
        }
    }

    width: model ? model.Width : undefined
    onWidthChanged: {
        if (model) {
            model.setData(width, "Width")
        }
    }

    height: model ? model.Height : undefined
    onHeightChanged: {
        if (model) {
            model.setData(height, "Height")
        }
    }

    enabled: model ? model.Enabled : true

    onActiveFocusChanged: {
        if (model) {
            model.setData(activeFocus, "ActiveFocus")
        }
    }
    onModelFocusChanged: {
        if (modelFocus) {
            forceActiveFocus()
        } else if (activeFocus) {
            parent.forceActiveFocus()
        }
    }

    onFocusChanged: {
        if (model) {
            model.setData(focus, "Focus")
        }
    }

    inputMask: model ? model.InputMask : ""
    onInputMaskChanged: {
        if (model) {
            model.setData(inputMask, "InputMask")
        }
    }

    maximumLength: model ? model.MaximumLength : undefined
    onMaximumLengthChanged: {
        if (model) {
            model.setData(maximumLength, "MaximumLength")
        }
    }

    readOnly: model ? model.ReadOnly : false
    onReadOnlyChanged: {
        if (model) {
            model.setData(readOnly, "ReadOnly")
        }
    }

    text: model ? model.Text : ""
    onTextChanged: {
        if (model) {
            model.setData(text, "Text")
        }
    }

    opacity: model ? model.Opacity : 1.0
    visible: model ? model.Visible : true
}
