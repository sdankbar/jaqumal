package com.github.sdankbar.qml.testing.models;

import java.util.Set;

import com.github.sdankbar.qml.JQMLModelFactory;
import com.github.sdankbar.qml.JVariant;
import com.github.sdankbar.qml.models.JQMLMapPool;
import com.github.sdankbar.qml.models.flat_tree.JQMLFlatTreeModel;
import com.github.sdankbar.qml.models.list.JQMLListModel;
import com.github.sdankbar.qml.models.list.JQMLXYSeriesModel;
import com.github.sdankbar.qml.models.singleton.JQMLButtonModel;
import com.github.sdankbar.qml.models.singleton.JQMLSingletonModel;
import com.github.sdankbar.qml.models.singleton.JQMLTextInputModel;
import com.google.common.collect.ImmutableMap;

public class MockJQMLModelFactory implements JQMLModelFactory {

	@Override
	public JQMLButtonModel createButtonModel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLFlatTreeModel<K> createFlatTreeModel(String name, Class<K> enumClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLFlatTreeModel<K> createFlatTreeModel(String name, Set<K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLListModel<K> createListModel(String name, Class<K> enumClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLListModel<K> createListModel(String name, Set<K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLMapPool<K> createPool(String name, Class<K> enumClass,
			ImmutableMap<K, JVariant> initialValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLMapPool<K> createPool(String name, Set<K> keys, ImmutableMap<K, JVariant> initialValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K extends Enum<K>> JQMLSingletonModel<K> createSingletonModel(String name, Class<K> enumClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> JQMLSingletonModel<K> createSingletonModel(String name, Set<K> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JQMLTextInputModel createTextInputModel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JQMLXYSeriesModel createXYSeriesModel(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
