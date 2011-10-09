/*
 * InstanceProperty.java
 * Copyright (C) 2011 Meyer Kizner
 * All rights reserved.
 */

package com.subitarius.instance.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.google.common.collect.Maps;

public enum InstanceProperty {
	FETCH_TIMESTAMP;

	private static final File FILE = new File("instance.ser");

	public Object get() throws IOException {
		return get(null);
	}

	public Object get(Object defaultValue) throws IOException {
		Map<String, Object> map = getMap();
		if (map != null && map.containsKey(name())) {
			return map.get(name());
		} else {
			return defaultValue;
		}
	}

	public void set(Object value) throws IOException {
		Map<String, Object> map = getMap();
		if (map == null) {
			map = Maps.newHashMap();
		}
		map.put(name(), value);

		if (FILE.exists()) {
			FILE.delete();
		}
		FILE.createNewFile();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				FILE));
		oos.writeObject(map);
		oos.close();
	}

	private static Map<String, Object> getMap() throws IOException {
		if (FILE.exists() && FILE.canRead()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					FILE));
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) ois
						.readObject();
				return map;
			} catch (ClassNotFoundException cnfx) {
				throw new RuntimeException(cnfx);
			} finally {
				ois.close();
			}
		}
		return null;
	}
}
