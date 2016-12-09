package newbee.morningGlory.http.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Type {
	public static boolean isSuper(Class clazz1, Class clazz2) {
		if ((clazz1 == null) || (clazz2 == null))
			return false;
		if ((!(clazz1.isInterface())) && (!(clazz2.isInterface()))) {
			while (clazz1 != null) {
				if (clazz1.equals(clazz2))
					return true;
				clazz1 = clazz1.getSuperclass();
			}
		} else if (clazz2.isInterface()) {
			if (clazz1.equals(clazz2))
				return true;
			Class[] cs = clazz1.getInterfaces();
			for (int i = 0; i < cs.length; ++i) {
				if (isSuper(cs[i], clazz2))
					return true;
			}
			if (!(clazz1.isInterface()))
				return isSuper(clazz1.getSuperclass(), clazz2);
		}
		return false;
	}

	public static boolean isMap(Object o) {
		return o instanceof Map;
	}

	public static boolean isArray(Object o) {
		return ((o != null) && (o.getClass().isArray()));
	}

	public static boolean isList(Object o) {
		return o instanceof List;
	}

	public static boolean isSet(Object o) {
		return o instanceof Set;
	}

	public static boolean isString(Object o) {
		return o instanceof String;
	}

	public static boolean isBoolean(Object o) {
		return o instanceof Boolean;
	}

	public static boolean isNumber(Object o) {
		return ((isByte(o)) || (isShort(o)) || (isInt(o)) || (isLong(o)) || (isFloat(o)) || (isDouble(o)));
	}

	public static boolean isIntegral(Object o) {
		return ((isByte(o)) || (isShort(o)) || (isInt(o)) || (isLong(o)));
	}

	public static boolean isFloating(Object o) {
		return ((isFloat(o)) || (isDouble(o)));
	}

	public static boolean isByte(Object o) {
		return o instanceof Byte;
	}

	public static boolean isShort(Object o) {
		return o instanceof Short;
	}

	public static boolean isChar(Object o) {
		return o instanceof Character;
	}

	public static boolean isInt(Object o) {
		return o instanceof Integer;
	}

	public static boolean isLong(Object o) {
		return o instanceof Long;
	}

	public static boolean isFloat(Object o) {
		return o instanceof Float;
	}

	public static boolean isDouble(Object o) {
		return o instanceof Double;
	}

	public static boolean isDate(Object o) {
		return o instanceof Date;
	}

	public static boolean isSQLTimestamp(Object o) {
		return o instanceof Timestamp;
	}

	public static boolean isSQLDate(Object o) {
		return o instanceof java.sql.Date;
	}

	public static boolean isSQLTime(Object o) {
		return o instanceof Time;
	}

	public static boolean isBytes(Object o) {
		return o instanceof byte[];
	}

	public static boolean isChars(Object o) {
		return o instanceof char[];
	}

	public static boolean isClass(Object o) {
		return o instanceof Class;
	}

	public static boolean isMethod(Object o) {
		return o instanceof Method;
	}

	public static boolean getBoolean(Object o, boolean dv) {
		if (o == null)
			return dv;
		if (isBoolean(o))
			return ((Boolean) o).booleanValue();
		if (isNumber(o))
			return (getDouble(o, 0.0D) != 0.0D);
		if (isString(o)) {
			if ("true".equalsIgnoreCase((String) o))
				return true;
			if ("false".equalsIgnoreCase((String) o))
				return false;
			try {
				return (Double.parseDouble((String) o) != 0.0D);
			} catch (Exception e) {
			}
		}
		return dv;
	}

	public static Boolean getBoolean(Object o, Boolean dv) {
		return new Boolean(getBoolean(o, (dv == null) ? false : dv.booleanValue()));
	}

	public static byte getByte(Object o, byte dv) {
		if (o == null)
			return dv;
		if (isBoolean(o)) {
			if (((Boolean) o).booleanValue())
				return 1;
			return 0;
		}
		if (isByte(o))
			return ((Byte) o).byteValue();
		if (isShort(o))
			return ((Short) o).byteValue();
		if (isInt(o))
			return ((Integer) o).byteValue();
		if (isLong(o))
			return ((Long) o).byteValue();
		if (isFloat(o))
			return ((Float) o).byteValue();
		if (isDouble(o))
			return ((Double) o).byteValue();
		if (isString(o)) {
			try {
				return Byte.parseByte((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Byte getByte(Object o, Byte dv) {
		return new Byte(getByte(o, (dv == null) ? 0 : dv.byteValue()));
	}

	public static short getShort(Object o, short dv) {
		if (o == null)
			return dv;
		if (isBoolean(o))
			return getByte(o, (byte) -128);
		if (isByte(o))
			return ((Byte) o).shortValue();
		if (isShort(o))
			return ((Short) o).shortValue();
		if (isInt(o))
			return ((Integer) o).shortValue();
		if (isLong(o))
			return ((Long) o).shortValue();
		if (isFloat(o))
			return ((Float) o).shortValue();
		if (isDouble(o))
			return ((Double) o).shortValue();
		if (isString(o)) {
			try {
				return Short.parseShort((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Short getShort(Object o, Short dv) {
		return new Short(getShort(o, (dv == null) ? 0 : dv.shortValue()));
	}

	public static char getChar(Object o, char dv) {
		if (o == null)
			return dv;
		if (isChar(o))
			return ((Character) o).charValue();
		if (isByte(o))
			return (char) ((Byte) o).shortValue();
		if (isShort(o))
			return (char) ((Short) o).shortValue();
		if (isInt(o))
			return (char) ((Integer) o).shortValue();
		if (isLong(o))
			return (char) ((Long) o).shortValue();
		if (isFloat(o))
			return (char) ((Float) o).shortValue();
		if (isDouble(o))
			return (char) ((Double) o).shortValue();
		String s = "";
		if ((isString(o)) && ((s = (String) o).length() == 1)) {
			return s.charAt(0);
		}
		return dv;
	}

	public static Character getChar(Object o, Character dv) {
		return new Character(getChar(o, (dv == null) ? null : Character.valueOf(dv.charValue())).charValue());
	}

	public static int getInt(Object o, int dv) {
		if (o == null)
			return dv;
		if (isBoolean(o))
			return getByte(o, (byte) -128);
		if (isByte(o))
			return ((Byte) o).intValue();
		if (isShort(o))
			return ((Short) o).intValue();
		if (isInt(o))
			return ((Integer) o).intValue();
		if (isLong(o))
			return ((Long) o).intValue();
		if (isFloat(o))
			return ((Float) o).intValue();
		if (isDouble(o))
			return ((Double) o).intValue();
		if (isString(o)) {
			try {
				return Integer.parseInt((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Integer getInt(Object o, Integer dv) {
		return new Integer(getInt(o, (dv == null) ? 0 : dv.intValue()));
	}

	public static long getLong(Object o, long dv) {
		if (o == null)
			return dv;
		if (isBoolean(o))
			return getByte(o, (byte) -128);
		if (isByte(o))
			return ((Byte) o).longValue();
		if (isShort(o))
			return ((Short) o).longValue();
		if (isInt(o))
			return ((Integer) o).longValue();
		if (isLong(o))
			return ((Long) o).longValue();
		if (isFloat(o))
			return ((Float) o).longValue();
		if (isDouble(o))
			return ((Double) o).longValue();
		if (isDate(o))
			return ((Date) o).getTime();
		if (isString(o)) {
			try {
				return Long.parseLong((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Long getLong(Object o, Long dv) {
		return new Long(getLong(o, (dv == null) ? 0L : dv.longValue()));
	}

	public static float getFloat(Object o, float dv) {
		if (o == null)
			return dv;
		if (isByte(o))
			return ((Byte) o).floatValue();
		if (isShort(o))
			return ((Short) o).floatValue();
		if (isInt(o))
			return ((Integer) o).floatValue();
		if (isLong(o))
			return ((Long) o).floatValue();
		if (isFloat(o))
			return ((Float) o).floatValue();
		if (isDouble(o))
			return ((Double) o).floatValue();
		if (isString(o)) {
			try {
				return Float.parseFloat((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Float getFloat(Object o, Float dv) {
		return new Float(getFloat(o, (dv == null) ? 0.0F : dv.floatValue()));
	}

	public static double getDouble(Object o, double dv) {
		if (o == null)
			return dv;
		if (isByte(o))
			return ((Byte) o).doubleValue();
		if (isShort(o))
			return ((Short) o).doubleValue();
		if (isInt(o))
			return ((Integer) o).doubleValue();
		if (isLong(o))
			return ((Long) o).doubleValue();
		if (isFloat(o))
			return ((Float) o).doubleValue();
		if (isDouble(o))
			return ((Double) o).doubleValue();
		if (isDate(o))
			return ((Date) o).getTime();
		if (isString(o)) {
			try {
				return Double.parseDouble((String) o);
			} catch (Exception e) {
				return dv;
			}
		}
		return dv;
	}

	public static Double getDouble(Object o, Double dv) {
		return new Double(getDouble(o, (dv == null) ? 0.0D : dv.doubleValue()));
	}

	public static Date getDateTime(Object o, String pattern, Date dv) {
		if (o == null)
			return dv;
		if (isLong(o))
			return new Date(((Long) o).longValue());
		if (isDate(o))
			return ((Date) o);
		if (isString(o))
			return parseDateTime((String) o, pattern, dv);
		return dv;
	}

	private static Date parseDateTime(String s, String pattern, Date dv) {
		try {
			SimpleDateFormat df = new SimpleDateFormat();
			if ((pattern != null) && (pattern.length() > 0))
				df.applyPattern(pattern);
			return df.parse(s);
		} catch (Exception e) {
		}
		return dv;
	}

	public static Date getDateTime(Object o, Date paramDate) {
		return getDateTime(o, "yyyy-MM-dd HH:mm:ss", paramDate);
	}

	public static Date getDate(Object o, Date paramDate) {
		return getDateTime(o, "yyyy-MM-dd", paramDate);
	}

	public static Date getTime(Object o, Date paramDate) {
		return getDateTime(o, "HH:mm:ss", paramDate);
	}

	public static String getString(Object o, String dv) {
		if (o == null)
			return dv;
		if (o instanceof String) {
			String s = (String) o;
			if (s.trim().length() == 0)
				return dv;
		}
		return o.toString();
	}

	public static List getList(Object o, List dv) {
		if (isList(o))
			return ((List) o);
		return dv;
	}

	public static Map getMap(Object o, Map dv) {
		if (isMap(o))
			return ((Map) o);
		return dv;
	}

	public static Set getSet(Object o, Set dv) {
		if (isSet(o))
			return ((Set) o);
		return dv;
	}

	public static boolean[] getBooleans(Object o, boolean[] dv) {
		if (o instanceof boolean[])
			return ((boolean[]) o);
		if (isArray(o)) {
			int len;
			dv = new boolean[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getBoolean(Array.get(o, i), false);
		}
		return dv;
	}

	public static byte[] getBytes(Object o, byte[] dv) {
		if (o instanceof byte[])
			return ((byte[]) o);
		if (isArray(o)) {
			int len;
			dv = new byte[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getByte(Array.get(o, i), (byte) -128);
		}
		return dv;
	}

	public static short[] getShorts(Object o, short[] dv) {
		if (o instanceof short[])
			return ((short[]) o);
		if (isArray(o)) {
			int len;
			dv = new short[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getShort(Array.get(o, i), (short) -128);
		}
		return dv;
	}

	public static int[] getInts(Object o, int[] dv) {
		if (o instanceof int[])
			return ((int[]) o);
		if (isArray(o)) {
			int len;
			dv = new int[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getInt(Array.get(o, i), 0);
		}
		return dv;
	}

	public static long[] getLongs(Object o, long[] dv) {
		if (o instanceof long[])
			return ((long[]) o);
		if (isArray(o)) {
			int len;
			dv = new long[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getLong(Array.get(o, i), 0L);
		}
		return dv;
	}

	public static float[] getFloats(Object o, float[] dv) {
		if (o instanceof float[])
			return ((float[]) o);
		if (isArray(o)) {
			int len;
			dv = new float[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getFloat(Array.get(o, i), 0.0F);
		}
		return dv;
	}

	public static double[] getDoubles(Object o, double[] dv) {
		if (o instanceof double[])
			return ((double[]) o);
		if (isArray(o)) {
			int len;
			dv = new double[len = Array.getLength(o)];

			for (int i = 0; i < len; ++i)
				dv[i] = getDouble(Array.get(o, i), 0.0D);
		}
		return dv;
	}

	public static String[] getStrings(Object o, String[] paramArrayOfString) {
		if (o instanceof String[])
			return ((String[]) o);
		if (isArray(o)) {
			int i;
			paramArrayOfString = new String[i = Array.getLength(o)];

			for (int j = 0; j < i; ++j)
				paramArrayOfString[j] = getString(Array.get(o, j), "");
		}
		return paramArrayOfString;
	}

	public static List[] getLists(Object o, List[] dv) {
		if (o instanceof List[])
			return ((List[]) o);
		return dv;
	}

	public static Map[] getMaps(Object o, Map[] dv) {
		if (o instanceof Map[])
			return ((Map[]) o);
		return dv;
	}

	public static Set[] getSets(Object o, Set[] dv) {
		if (o instanceof Set[])
			return ((Set[]) o);
		return dv;
	}

	public static Object[] getObjects(Object o, Object[] dv) {
		if (o instanceof Object[])
			return ((Object[]) o);
		if (isArray(o)) {
			int len = Array.getLength(o);
			if (o instanceof boolean[])
				dv = new Boolean[len];
			else if (o instanceof byte[])
				dv = new Byte[len];
			else if (o instanceof char[])
				dv = new Character[len];
			else if (o instanceof short[])
				dv = new Short[len];
			else if (o instanceof int[])
				dv = new Integer[len];
			else if (o instanceof long[])
				dv = new Long[len];
			else if (o instanceof float[])
				dv = new Float[len];
			else if (o instanceof double[])
				dv = new Double[len];
			else
				dv = new Object[len];
			for (int i = 0; i < len; ++i)
				dv[i] = Array.get(o, i);
		}
		return dv;
	}

	public static String objectToString(Object o) {
		if (o == null)
			return "null";
		if (isString(o))
			return ((String) o);
		if (isSQLDate(o))
			return getDate("yyyy-MM-dd", (Date) o);
		if (isSQLTime(o))
			return getDate("HH:mm:ss", (Date) o);
		if (isDate(o))
			return getDate("yyyy-MM-dd HH:mm:ss", (Date) o);
		if (isChar(o))
			return ((Character) o).toString();
		if (isChars(o))
			return new String((char[]) o);
		if (isBytes(o))
			return new String((byte[]) o);
		return o.toString();
	}

	private static String getDate(String pattern, Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(pattern);
		return sdf.format(d);
	}

	public static boolean objectEquals(Object o1, Object o2) {
		if (o1 == null)
			return (o2 == null);
		return o1.equals(o2);
	}

	public static int objectCompares(Object o1, Object o2) {
		if (o1 == o2)
			return 0;
		if ((o1 != null) && (o2 == null))
			return 1;
		if ((o1 == null) && (o2 != null))
			return -1;
		if (o1 instanceof Comparable)
			return ((Comparable) o1).compareTo(o2);
		if (o2 instanceof Comparable) {
			return (-((Comparable) o2).compareTo(o1));
		}
		return o1.toString().compareTo(o2.toString());
	}

	public static Object[] toObjectArray(byte[] ar) {
		Byte[] or = new Byte[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Byte(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(char[] ar) {
		Character[] or = new Character[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Character(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(short[] ar) {
		Short[] or = new Short[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Short(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(int[] ar) {
		Integer[] or = new Integer[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Integer(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(long[] ar) {
		Long[] or = new Long[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Long(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(float[] ar) {
		Float[] or = new Float[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Float(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(double[] ar) {
		Double[] or = new Double[ar.length];
		for (int i = 0; i < ar.length; ++i)
			or[i] = new Double(ar[i]);
		return or;
	}

	public static Object[] toObjectArray(Object o) {
		if (o instanceof byte[])
			return toObjectArray((byte[]) o);
		if (o instanceof char[])
			return toObjectArray((char[]) o);
		if (o instanceof short[])
			return toObjectArray((short[]) o);
		if (o instanceof int[])
			return toObjectArray((int[]) o);
		if (o instanceof long[])
			return toObjectArray((long[]) o);
		if (o instanceof float[])
			return toObjectArray((float[]) o);
		if (o instanceof double[]) {
			return toObjectArray((double[]) o);
		}
		return toObjectArray((Object[]) o);
	}

	public static byte[] toByteArray(Object[] ar, byte dv) {
		byte[] br = new byte[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getByte(ar[i], dv);
		return br;
	}

	public static char[] toCharArray(Object[] ar, char dv) {
		char[] br = new char[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getChar(ar[i], dv);
		return br;
	}

	public static short[] toShortArray(Object[] ar, short dv) {
		short[] br = new short[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getShort(ar[i], dv);
		return br;
	}

	public static int[] toIntArray(Object[] ar, int dv) {
		int[] br = new int[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getInt(ar[i], dv);
		return br;
	}

	public static long[] toLongArray(Object[] ar, long dv) {
		long[] br = new long[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getLong(ar[i], dv);
		return br;
	}

	public static float[] toFloatArray(Object[] ar, float dv) {
		float[] br = new float[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getFloat(ar[i], dv);
		return br;
	}

	public static double[] toDoubleArray(Object[] ar, double dv) {
		double[] br = new double[ar.length];
		for (int i = 0; i < ar.length; ++i)
			br[i] = getDouble(ar[i], dv);
		return br;
	}

	public static List arrayToList(Object[] ar) {
		List ls = new LinkedList();
		arrayToCollection(ar, ls);
		return ls;
	}

	public static Set arrayToSet(Object[] ar) {
		Set set = new HashSet();
		arrayToCollection(ar, set);
		return set;
	}

	public static void arrayToCollection(Object[] arr, Collection col) {
		for (int i = 0; i < arr.length; ++i)
			col.add(arr[i]);
	}

}
