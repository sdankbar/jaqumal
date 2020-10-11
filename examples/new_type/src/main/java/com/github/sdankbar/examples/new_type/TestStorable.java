package com.github.sdankbar.examples.new_type;

import com.github.sdankbar.qml.JVariant.Storable;

public class TestStorable implements Storable {

	private final String str;
	private final int x;
	private final int y;

	public TestStorable(final String str, final int x, final int y) {
		this.str = str;
		this.x = x;
		this.y = y;
	}

	@Override
	public void store(final int role) {
		Native.setTestStorable(str, x, y, role);
	}

}
