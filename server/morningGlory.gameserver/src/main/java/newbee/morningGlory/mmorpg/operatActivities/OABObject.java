package newbee.morningGlory.mmorpg.operatActivities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sophia.mmorpg.utils.Type;

/**
 * 
 * Copyright (c) 2012 by 游爱.
 * 
 */
public class OABObject {

	public static byte zero = 0;

	private final Map<Object, Object> data = new LinkedHashMap<Object, Object>();

	public Map<Object, Object> getData() {
		return data;
	}

	public Object get(Object key) {
		return getData().get(key);
	}

	public byte getByte(Object key) {
		return getByte(key, zero);
	}

	public byte getByte(Object key, byte dv) {
		return Type.getByte(get(key), dv);
	}

	public byte setByte(Object key, byte val) {
		return Type.getByte(data.put(key, val), zero);
	}

	public byte addByte(Object key, byte val) {
		return setByte(key, (byte) (getByte(key) + val));
	}

	public short getShort(Object key) {
		return getShort(key, (short) 0);
	}

	public short getShort(Object key, short dv) {
		return Type.getShort(get(key), dv);
	}

	public short setShort(Object key, short val) {
		return Type.getShort(data.put(key, val), zero);
	}

	public short addShort(Object key, short val) {
		return setShort(key, (short) (getShort(key) + val));
	}

	public int getInt(Object key) {
		return getInt(key, 0);
	}

	public int getInt(Object key, int dv) {
		return Type.getInt(get(key), dv);
	}

	public int setInt(Object key, int val) {
		return Type.getInt(data.put(key, val), zero);
	}

	public int addInt(Object key, int val) {
		return setInt(key, (getInt(key) + val));
	}

	public long getLong(Object key) {
		return getLong(key, 0);
	}

	public long getLong(Object key, long dv) {
		return Type.getLong(get(key), dv);
	}

	public long setLong(Object key, long val) {
		return Type.getLong(data.put(key, val), zero);
	}

	public long addLong(Object key, long val) {
		return setLong(key, getLong(key) + val);
	}

	public double getDouble(Object key) {
		return getDouble(key, 0);
	}

	public double getDouble(Object key, double dv) {
		return Type.getDouble(get(key), dv);
	}

	public double setDouble(Object key, double val) {
		return Type.getDouble(data.put(key, val), zero);
	}

	public double addDouble(Object key, double val) {
		return setDouble(key, getDouble(key) + val);
	}

	public String getString(Object key) {
		return getString(key, "");
	}

	public String getString(Object key, String dv) {
		return Type.getString(get(key), dv);
	}

	public String setString(Object key, String val) {
		return Type.getString(data.put(key, val), "");
	}

	public String addString(Object key, String val) {
		return setString(key, getString(key) + val);
	}

	public List<?> getList(Object key) {
		return getList(key, null);
	}

	public List<?> checkList(Object key, List<?> dv) {
		List<?> list = getList(key);
		if (list != null)
			return list;
		set(key, dv);
		return dv;
	}

	public List<?> getList(Object key, List<?> dv) {
		return Type.getList(get(key), dv);
	}

	public Map<?, ?> getMap(Object key) {
		return getMap(key, null);
	}

	public Map<?, ?> checkMap(Object key, Map<?, ?> dv) {
		Map<?, ?> map = getMap(key);
		if (map != null)
			return map;
		set(key, dv);
		return dv;
	}

	public Map<?, ?> getMap(Object key, Map<?, ?> dv) {
		return Type.getMap(get(key), dv);
	}

	public Object set(Object key, Object value) {
		return getData().put(key, value);
	}
}
