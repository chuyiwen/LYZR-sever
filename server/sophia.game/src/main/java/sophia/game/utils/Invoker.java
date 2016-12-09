package sophia.game.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class Invoker {
	private static final Logger logger = Logger.getLogger(Invoker.class);

	public <T> void set(String fieldName, T t) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(this, t);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public <T> T get(String fieldName) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(this);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public <T> T invoke(String methodName, Class<?>[] parameterTypes, Object... args) {
		try {
			Method method = getClass().getDeclaredMethod(methodName, parameterTypes);
			return (T) method.invoke(this, args);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public <T> T invoke(String methodName) {
		try {
			Method method = getClass().getDeclaredMethod(methodName);
			return (T) method.invoke(this);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	public <T> void fillAllFields(T t) {
		Field[] fields = getClass().getDeclaredFields();
		for (Field f : fields) {
			if (!Modifier.isFinal(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
				try {
					f.setAccessible(true);
					Field tf = t.getClass().getDeclaredField(f.getName());
					tf.setAccessible(true);
					tf.set(t, f.get(this));
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
	}
}
