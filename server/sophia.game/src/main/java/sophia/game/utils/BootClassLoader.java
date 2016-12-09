package sophia.game.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class BootClassLoader extends URLClassLoader {
	public Map<String, byte[]> classDatas;

	public BootClassLoader() {
		this((String) null, null);
	}

	public BootClassLoader(ClassLoader parent) {
		this((String) null, parent);
	}

	public BootClassLoader(URL[] urls) {
		this(urls, null);
	}

	public BootClassLoader(String classPath) {
		this(classPath, null);
	}

	public BootClassLoader(String classPath, ClassLoader parent) {
		this(ClazzUtil.classPathToURLs(classPath), parent);
	}

	public BootClassLoader(URL[] urls, ClassLoader parent) {
		super(urls == null ? new URL[0] : urls, parent != null ? parent : Thread.currentThread().getContextClassLoader());
		this.classDatas = new HashMap<String, byte[]>();
	}

	public BootClassLoader(ClassLoader parent, Map<String, byte[]> classDatas) {
		this(parent);
		this.classDatas = classDatas;
	}

	public Class<?> defineClassData(byte[] data) throws ClassFormatError {
		return defineClassData(null, data, 0, data.length);
	}

	public Class<?> defineClassData(String name, byte[] data) throws ClassFormatError {
		return defineClassData(name, data, 0, data.length);
	}

	public Class<?> defineClassData(byte[] data, int offset, int len) throws ClassFormatError {
		return defineClassData(null, data, offset, len);
	}

	public synchronized Class<?> defineClassData(String name, byte[] data, int offset, int len) throws ClassFormatError {
		return defineClass(name, data, offset, len);
	}

	public synchronized boolean isDefined(String name) {
		return findLoadedClass(name) != null;
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null)
			if (this.classDatas.containsKey(name)) {
				byte[] data = (byte[]) this.classDatas.get(name);
				clazz = defineClassData(name, data);
				if (clazz == null)
					clazz = findClass(name);
			} else
				return super.loadClass(name, resolve);
		if (resolve)
			resolveClass(clazz);
		return clazz;
	}
}