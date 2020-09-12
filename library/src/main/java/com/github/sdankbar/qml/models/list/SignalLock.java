package com.github.sdankbar.qml.models.list;

/**
 * Class for locking/unlocking a list model.
 */
public class SignalLock implements AutoCloseable {
	private final JQMLListModelImpl<?> model;

	/**
	 * Constructor
	 *
	 * @param model The model to lock/unlock. If null, takes no action on close().
	 */
	public SignalLock(final JQMLListModelImpl<?> model) {
		this.model = model;
	}

	@Override
	public void close() {
		if (model != null) {
			model.unlockSignals();
		}
	}

}
