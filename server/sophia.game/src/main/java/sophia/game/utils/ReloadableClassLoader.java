package sophia.game.utils;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class ReloadableClassLoader extends BootClassLoader {
	protected ReloadableClassFactory factory;
	protected final Set<String> reloadableClasses = new HashSet<String>();

	public ReloadableClassLoader(ReloadableClassFactory factory, URL[] urls, ClassLoader parent) {
		super(urls, parent);
		this.factory = factory;
	}

	public synchronized void addReloadableClass(String name) {
		name = ClazzUtil.getBaseClassName(name.trim());
		if (!name.startsWith("java."))
			reloadableClasses.add(name);
	}

	public synchronized void removeReloadableClass(String name) {
		name = ClazzUtil.getBaseClassName(name);
		Iterator<String> it = reloadableClasses.iterator();
		while (it.hasNext()) {
			String str = it.next();
			if (str.startsWith(name))
				it.remove();
		}
	}

	public synchronized void addReloadableClasses(String[] classNames) {
		for (int i = 0; i < classNames.length; ++i) {
			String name = classNames[i].trim();
			if (name.length() > 0)
				addReloadableClass(name);
		}
	}

	public synchronized boolean isReloadable(String className) {
		return this.reloadableClasses.contains(className) || this.reloadableClasses.contains(ClazzUtil.getBaseClassName(className));
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null)
			try {
				if (isReloadable(name))
					clazz = findClass(name);
				else
					clazz = this.factory.loadClass(name);
				if (clazz != null) {
					if (resolve)
						resolveClass(clazz);
					return clazz;
				}
			} catch (Exception e) {
			}
		return super.loadClass(name, resolve);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (factory != null && factory.classDatas.containsKey(name)) {
			byte[] data = (byte[]) factory.classDatas.get(name);
			return defineClassData(data);
		}
		return super.findClass(name);
	}
}