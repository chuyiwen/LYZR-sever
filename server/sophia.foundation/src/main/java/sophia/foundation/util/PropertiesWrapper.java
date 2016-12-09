/**
 * 
 */
package sophia.foundation.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;


public class PropertiesWrapper {
	protected Properties properties;

	public PropertiesWrapper(Properties properties) {
		if (properties == null) {
			throw new NullPointerException("The argument must not be null");
		} else {
			this.properties = properties;
			return;
		}
	}

	public Properties getProperties() {
		return properties;
	}

	/**
	 * �������ļ���ȡָ��name��Stringֵ
	 * 
	 * @param name
	 * @return
	 */
	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * �������ļ���ȡָ��name��Stringֵ������ֵ�����ڣ���ָ����ȱʡֵ����
	 * 
	 * @param name
	 *            the property name
	 * @param defaultValue
	 *            ȱʡֵ
	 * @return
	 */
	public String getProperty(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
	}

	/**
	 * ����ָ�����Ե�booleanֵ
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanProperty(String name, boolean defaultValue) {
		String value = properties.getProperty(name);
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

	/**
	 * ����ָ�����Ե�intֵ����������ָ����ȱʡֵ���
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getIntProperty(String name, int defaultValue) {
		String value = properties.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw (NumberFormatException) new NumberFormatException(
					"The value of the " + name + " property must be a valid "
							+ "int: \"" + value + "\"").initCause(e);
		}
	}
	
	public int[] getIntArrayProperty(String name, int[] defaultValue) {
		String value = properties.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		
		String[] split = value.split(",");
		int[] intArray = new int[split.length];
		for(int i = 0; i < split.length; i++) {
			try {
				intArray[i] = Integer.valueOf(split[i]);
			} catch (NumberFormatException e) {
				throw (NumberFormatException) new NumberFormatException(
						"The value of the " + name + " property must be a valid "
								+ "int: \"" + value + "\"").initCause(e);
			}
		}
		
		return intArray;
	}
	
	public String[] getStringArrayProperty(String name, String[] defaultValue) {
		String value = properties.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		
		String[] split = value.split(",");
		String[] stringArray = new String[split.length];
		for(int i = 0; i < split.length; i++) {
			try {
				stringArray[i] = split[i];
			} catch (NumberFormatException e) {
				throw new RuntimeException("getStringArrayProperty error!name="+name+" value="+value);
			}
		}
		
		return stringArray;
	}

	/**
	 * ����ָ�����Ե�intֵ,�����ʱ����
	 * @param name
	 * @return
	 */
	public int getRequiredIntProperty(String name) {
		String value = properties.getProperty(name);
		if (value == null) {
			throw new IllegalArgumentException("The " + name
					+ " property must be specified");
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw (NumberFormatException) new NumberFormatException(
					"The value of the " + name + " property must be a valid "
							+ "int: \"" + value + "\"").initCause(e);
		}
	}

	/**
	 * ����ָ�����Ե�intֵ, ȱʡֵ������ֵ��������(min,max)
	 * @param name
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return
	 */
	public int getIntProperty(String name, int defaultValue, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException(
					"The min must not be greater than the max");
		} else if (min > defaultValue || defaultValue > max) {
			throw new IllegalArgumentException(
					"The default value must be between the min and the max");
		}
		int result = getIntProperty(name, defaultValue);
		if (min > result) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be less " + "than " + min + ": "
					+ result);
		} else if (result > max) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be greater " + "than " + max + ": "
					+ result);
		}
		return result;
	}

	/**
	 * ����ָ�����Ե�intֵ,�����ʱ����,ȱʡֵ������ֵ��������(min,max)
	 * @param name
	 * @param min
	 * @param max
	 * @return
	 */
	public int getRequiredIntProperty(String name, int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException(
					"The min must not be greater than the max");
		}
		int result = getRequiredIntProperty(name);
		if (min > result) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be less " + "than " + min + ": "
					+ result);
		} else if (result > max) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be greater " + "than " + max + ": "
					+ result);
		}
		return result;
	}

	public long getLongProperty(String name, long defaultValue) {
		String value = properties.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw (NumberFormatException) new NumberFormatException(
					"The value of the " + name + " property must be a valid "
							+ "long: \"" + value + "\"").initCause(e);
		}
	}

	public long getLongProperty(String name, long defaultValue, long min,
			long max) {
		if (min > max) {
			throw new IllegalArgumentException(
					"The min must not be greater than the max");
		} else if (min > defaultValue || defaultValue > max) {
			throw new IllegalArgumentException(
					"The default value must be between the min and the max");
		}
		long result = getLongProperty(name, defaultValue);
		if (min > result) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be less " + "than " + min + ": "
					+ result);
		} else if (result > max) {
			throw new IllegalArgumentException("The value of the " + name
					+ " property must not be greater " + "than " + max + ": "
					+ result);
		}
		return result;
	}

	public <T> T getClassInstanceProperty(String name, Class<T> type,
			Class<?>[] paramTypes, Object... args) {
		String className = properties.getProperty(name);
		if (className == null) {
			return null;
		}
		try {
			return Class.forName(className).asSubclass(type).getConstructor(
					paramTypes).newInstance(args);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("The class specified by the "
					+ name + " property was not " + "found: " + className, e);
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("The class specified by the "
					+ name + " property does not " + "implement "
					+ type.getName() + ": " + className, e);
		} catch (NoSuchMethodException e) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Class<?> paramType : paramTypes) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(paramType.getName());
			}
			throw new IllegalArgumentException("The class specified by the "
					+ name + " property, " + className
					+ ", does not have a constructor with required "
					+ "parameters: " + sb, e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw new IllegalArgumentException(
						"Problem calling the constructor for the class "
								+ "specified by the " + name + " property: "
								+ className + ": " + cause, cause);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Problem creating an instance of the class specified by the "
							+ name + " property: " + className + ": " + e, e);
		}
	}

}

