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
package com.github.sdankbar.qml.eventing.builtin;

import java.util.Objects;
import java.util.Set;

import com.github.sdankbar.qml.eventing.Event;

/**
 * Event is generated when a mouse wheel is moved in a MouseArea.
 */
public class MouseWheelEvent extends Event<BuiltinEventProcessor> {

	private final String objectName;
	private final int angleDeltaX;
	private final int angleDeltaY;

	private final Set<MouseButton> buttons;

	private final Set<KeyModifier> modifiers;
	private final int x;
	private final int y;

	/**
	 * Creates a new event.
	 *
	 * @param objectName  Name of the object that generated this event.
	 * @param angleDeltaX The angle the wheel was moved horizontally.
	 * @param angleDeltaY The angle the wheel was moved vertically.
	 * @param buttonMask  Mask of buttons that are pressed when this MouseEvent was
	 *                    created.
	 * @param modifiers   Keyboard buttons that are pressed when this MouseEvent was
	 *                    created.
	 * @param x           X location of the mouse click.
	 * @param y           Y location of the mouse click.
	 */
	public MouseWheelEvent(final String objectName, final int angleDeltaX, final int angleDeltaY, final int buttonMask,
			final int modifiers, final int x, final int y) {
		this.objectName = Objects.requireNonNull(objectName, "obejctName is null");
		this.angleDeltaX = angleDeltaX;
		this.angleDeltaY = angleDeltaY;
		this.x = x;
		this.y = y;
		buttons = MouseButton.fromMask(buttonMask);
		this.modifiers = KeyModifier.fromMask(modifiers);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MouseWheelEvent other = (MouseWheelEvent) obj;
		if (angleDeltaX != other.angleDeltaX) {
			return false;
		}
		if (angleDeltaY != other.angleDeltaY) {
			return false;
		}
		if (buttons == null) {
			if (other.buttons != null) {
				return false;
			}
		} else if (!buttons.equals(other.buttons)) {
			return false;
		}
		if (modifiers == null) {
			if (other.modifiers != null) {
				return false;
			}
		} else if (!modifiers.equals(other.modifiers)) {
			return false;
		}
		if (objectName == null) {
			if (other.objectName != null) {
				return false;
			}
		} else if (!objectName.equals(other.objectName)) {
			return false;
		}
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}

	/**
	 * @return the angleDeltaX
	 */
	public int getAngleDeltaX() {
		return angleDeltaX;
	}

	/**
	 * @return the angleDeltaY
	 */
	public int getAngleDeltaY() {
		return angleDeltaY;
	}

	/**
	 * @return A Set of the MouseButtons that pressed when this MouseEvent was
	 *         generated.
	 */
	public Set<MouseButton> getButtons() {
		return buttons;
	}

	/**
	 * @return Set of KeyModifiers that were active when the wheel motion occurred.
	 */
	public Set<KeyModifier> getModifiers() {
		return modifiers;
	}

	/**
	 * @return The name of the object that generated this event.
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @return X coordinate of the click
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return Y coordinate of the click
	 */
	public int getY() {
		return y;
	}

	@Override
	public void handle(final BuiltinEventProcessor processor) {
		processor.handle(this);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + angleDeltaX;
		result = prime * result + angleDeltaY;
		result = prime * result + ((buttons == null) ? 0 : buttons.hashCode());
		result = prime * result + ((modifiers == null) ? 0 : modifiers.hashCode());
		result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MouseWheelEvent [objectName=" + objectName + ", angleDeltaX=" + angleDeltaX + ", angleDeltaY="
				+ angleDeltaY + ", buttons=" + buttons + ", modifiers=" + modifiers + ", x=" + x + ", y=" + y + "]";
	}

}
