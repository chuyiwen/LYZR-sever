package sophia.game.utils;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReloadableClassFactory {
	protected final ClassLoader parent;
	protected final URL[] urls;
	protected final Map<String, Class<?>> loadedClasses;
	protected final Set<String> reloadableClasses;
	protected final Set<String> reloadablePackages;
	protected ReloadableClassLoader curLoader;
	protected final Map<String, byte[]> classDatas;

	public ReloadableClassFactory(URL[] urls) {
		this(urls, null);
	}

	public static ReloadableClassFactory newInstance(URL[] urls, ClassLoader contextClassLoader) {
		return new ReloadableClassFactory(urls, contextClassLoader);
	}

	public ReloadableClassFactory(URL[] urls, ClassLoader parent) {
		this.loadedClasses = Collections.synchronizedMap(new HashMap<String, Class<?>>());
		this.classDatas = Collections.synchronizedMap(new HashMap<String, byte[]>());
		this.reloadableClasses = Collections.synchronizedSet(new HashSet<String>());
		this.reloadablePackages = Collections.synchronizedSet(new HashSet<String>());
		this.curLoader = null;
		this.parent = parent == null ? Thread.currentThread().getContextClassLoader() : parent;
		this.urls = urls == null ? new URL[0] : urls;
	}

	public ClassLoader getParent() {
		return parent;
	}

	public URL[] getURLs() {
		return (URL[]) urls.clone();
	}

	public void addReloadableClass(String className) {
		if (!(className = ClazzUtil.getBaseClassName(className.trim())).startsWith("java."))
			reloadableClasses.add(className);
	}

	public void addReloadableClasses(String[] classNames) {
		for (int i = 0; i < classNames.length; ++i) {
			String name = classNames[i].trim();
			if (name.length() > 0)
				addReloadableClass(name);
		}
	}

	public void addReloadablePackage(String packageName) {
		if (packageName == null)
			throw new NullPointerException();
		if (!packageName.startsWith("java.") && !packageName.equals("java"))
			reloadablePackages.add(packageName);
	}

	public void addReloadablePackages(String[] packageNames) {
		for (int i = 0; i < packageNames.length; ++i) {
			String name = packageNames[i].trim();
			if (name.length() > 0)
				addReloadablePackage(name);
		}
	}

	public boolean isReloadable(String className) {
		return reloadableClasses.contains(className) || reloadablePackages.contains(ClazzUtil.getPackageName(className))
				|| reloadableClasses.contains(ClazzUtil.getBaseClassName(className));
	}

	public Class<?> findLoadedClass(String className) {
		return (Class<?>) loadedClasses.get(className);
	}

	public void removeLoadedClasses(String className) {
		className = ClazzUtil.getBaseClassName(className);
		String str = className + "$";
		synchronized (loadedClasses) {
			Iterator<?> it = loadedClasses.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) it.next();
				String s = entry.getKey().toString();
				if (s.equals(className) || s.startsWith(str))
					it.remove();
			}
		}
		if (curLoader != null)
			curLoader.removeReloadableClass(className);
	}

	public void removeLoadedPackageClasses(String packageName) {
		synchronized (loadedClasses) {
			Iterator<?> it = loadedClasses.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) it.next();
				String key = entry.getKey().toString();
				if (ClazzUtil.getPackageName(key).equals(packageName))
					it.remove();
			}
			return;
		}
	}

	public void clearLoadedClasses() {
		loadedClasses.clear();
	}

	public Class<?> reloadClass(String className) throws ClassNotFoundException {
		removeLoadedClasses(className);
		addReloadableClass(className);
		return loadClass(className);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (isReloadable(name)) {
			Class<?> clazz = findLoadedClass(name);
			if (clazz != null)
				return clazz;
			ReloadableClassLoader loader = null;
			String baseName = ClazzUtil.getBaseClassName(name);
			if (!name.equals(baseName))
				loader = (ReloadableClassLoader) loadClass(baseName).getClassLoader();
			else {
				if (curLoader == null || curLoader.isDefined(name)) {
					curLoader = new ReloadableClassLoader(this, getURLs(), parent);
					Thread.currentThread().setContextClassLoader(curLoader);
				}
				loader = curLoader;
			}
			try {
				Map<String, byte[]> loadClassData = ClazzUtil.loadClassData(name);
				classDatas.putAll(loadClassData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			synchronized (loader) {
				if (name.indexOf("$") == -1)
					loader.addReloadableClass(name);
				if (classDatas.containsKey(name) && !curLoader.isDefined(name)) {
					clazz = loader.defineClassData(name, (byte[]) classDatas.get(name));
				} else {
					clazz = loader.loadClass(name);
				}
			}
			loadedClasses.put(name, clazz);
			return clazz;
		}
		return parent.loadClass(name);
	}

	public void updateClasses(String rcs) {
		try {
			Map<String, byte[]> m = ClazzUtil.loadClassData(rcs);
			classDatas.putAll(m);
			reloadableClasses.addAll(m.keySet());
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl instanceof BootClassLoader) {
				((BootClassLoader) cl).classDatas.putAll(classDatas);
				((BootClassLoader) cl).classDatas.putAll(m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}