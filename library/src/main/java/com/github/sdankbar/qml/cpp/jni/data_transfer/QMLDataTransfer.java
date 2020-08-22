package com.github.sdankbar.qml.cpp.jni.data_transfer;

public class QMLDataTransfer {
	static native void setInteger(int i, int roleIndex);

	static native void setLong(long v, int roleIndex);

	static native void setBoolean(boolean v, int roleIndex);

	static native void setFloat(float v, int roleIndex);

	static native void setDouble(double v, int roleIndex);

	static native void setSize(int w, int h, int roleIndex);

	static native void setPoint(int x, int y, int roleIndex);

	static native void setLine(int x1, int y1, int x2, int y2, int roleIndex);

	static native void setRectangle(int x, int y, int w, int h, int roleIndex);

	static native void setString(String v, int roleIndex);

	static native void setRegularExpression(String v, int roleIndex);

	static native void setURL(String v, int roleIndex);

	static native void setUUID(String v, int roleIndex);

	static native void setByteArray(byte[] v, int roleIndex);

	static native void setColor(int v, int roleIndex);

	static native void setDateTime(long seconds, int nanos, int roleIndex);

	static native void setImage(int w, int h, byte[] data, int roleIndex);

	static native void setFont(String v, int roleIndex);

	static native void setPolyline(int length, double[] data, int roleIndex);
}
