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
import QtQuick.Controls 1.4
import com.github.sdankbar.jaqumal 0.4

Button {
    id: internalButton
    property var model

    EventBuilder {
        id: eventing
    }

    onClicked: {
        if (model) {
            UtilFunc.fireButtonClick(model.ModelName, eventing)
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

    activeFocusOnPress: model ? model.ActiveFocusOnPressed : false
    onActiveFocusChanged: {
        if (model) {
            model.setData(activeFocus, "ActiveFocus")
        }
    }

    checkable: model ? model.Checkable : false
    onPressedChanged: {
        if (model) {
            model.setData(pressed, "Pressed")
        }
    }

    checked: model ? model.Checked : false
    onHoveredChanged: {
        if (model) {
            model.setData(hovered, "Hovered")
        }
    }

    iconName: model ? model.IconName : ""
    iconSource: model ? model.IconSource : ""
    isDefault: model ? model.IsDefault : false
    text: model ? model.Text : ""
    tooltip: model ? model.Tooltip : ""
    clip: model ? model.Clip : false
    enabled: model ? model.Enabled : true
    opacity: model ? model.Opacity : 1.0
    visible: model ? model.Visible : true
}
