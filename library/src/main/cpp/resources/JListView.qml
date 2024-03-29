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
import QtQuick 2.11
import QtQuick.Controls 6.5
import com.github.sdankbar.jaqumal 0.4

ListView {
    id: internalView
    property bool selectionFollowsHighlight: false

    EventBuilder {
        id: eventing
    }

    EventDispatcher {
        id: dispatch
        allowedEvents: ["RequestScrollListToPositionEvent"]

        onEventReceived: function(eventName, args) {
            if (model !== null && model.modelName === args.model_name) {
                switch (args.position_mode) {
                case 0:
                    internalView.positionViewAtIndex(args.index, ListView.Beginning)
                    break
                case 1:
                    internalView.positionViewAtIndex(args.index, ListView.Center)
                    break
                case 2:
                    internalView.positionViewAtIndex(args.index, ListView.End)
                    break
                case 3:
                    internalView.positionViewAtIndex(args.index, ListView.Visible)
                    break
                case 4:
                    internalView.positionViewAtIndex(args.index, ListView.Contain)
                    break
                case 5:
                    internalView.positionViewAtIndex(args.index, ListView.SnapPosition)
                    break
                }
            }
        }
    }

	QtObject {
		id: internal
		property bool blockEvent: false;
	}

    function getSelectedIndex() {
        for (var i = 0; i < model.size; ++i) {
            if (model.getData(i).is_selected === true) {
                return i;
            }
        }
        return -1;
    }
    
    function getSelected() {
        var index = getSelectedIndex()
        if (index !== -1) {
            return model.getData(index)
        } else {
            return null
        }
    }

    function selectIndex(index) {
    	if (!internal.blockEvent) {
    		eventing.addBoolean(true)
    		eventing.addInteger(index)
            eventing.addString(model.modelName)
    		eventing.fireEvent("ListSelectionChangedEvent")
    		
    		if (selectionFollowsHighlight) {
    			internal.blockEvent = true;
    			currentIndex = index;
    			internal.blockEvent = false;
    		}
    	}
    }
    
    function deselectIndex(index) {
    	if (!internal.blockEvent) {
    		eventing.addBoolean(false)
    		eventing.addInteger(index)
            eventing.addString(model.modelName)
    		eventing.fireEvent("ListSelectionChangedEvent")
    	}
    }

    function toggleSelectionIndex(index) {
    	if (!internal.blockEvent) {
    		var oldSelection = model.getData(index).is_selected;
    		eventing.addBoolean(!oldSelection)
    		eventing.addInteger(index)
            eventing.addString(model.modelName)
    		eventing.fireEvent("ListSelectionChangedEvent")
    		
    		if (!oldSelection && selectionFollowsHighlight) {
    			internal.blockEvent = true;
    			currentIndex = index;
    			internal.blockEvent = false;
    		}
    	}
    }
    
    onCurrentIndexChanged: {
    	if (selectionFollowsHighlight && (0 <= currentIndex) && (currentIndex < count)) {
    		selectIndex(currentIndex)
    	}
    }
    
    Keys.onSpacePressed: {
		if (keyNavigationEnabled && !selectionFollowsHighlight && (0 <= currentIndex) && (currentIndex < count)) {
			event.accepted = true
			toggleSelectionIndex(currentIndex)
		} else {
			event.accepted = false
		}
    }
}
