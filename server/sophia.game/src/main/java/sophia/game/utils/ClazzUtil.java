package sophia.game.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClazzUtil {

	public static List<File> readJarFiles(File dir) {
		List<File> files = new ArrayList<File>();
		File[] listFiles = dir.listFiles();
		for (File file : listFiles) {
			if (file.isDirectory()) {
				files.addAll(readJarFiles(file));
			} else {
				String name = file.getName();
				if (name.endsWith(".jar") || name.endsWith(".JAR"))
					files.add(file);
			}
		}
		return files;
	}

	public static String trimClassPath(String classPath) {
		return trimClassPath(classPath, null);
	}

	public static String trimClassPath(String classPath, File basePath) {
		if (classPath == null || classPath.length() == 0)
			return "";
		String str = System.getProperty("path.separator", ";");
		String[] arr = classPath.split(str);
		List<String> ls = new ArrayList<String>();
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = arr[i].trim();
			if (arr[i].length() <= 0)
				continue;
			File file = new File(arr[i]);
			if (!file.isAbsolute() && basePath != null)
				file = new File(basePath, arr[i]);
			try {
				ls.add(file.getCanonicalPath());
			} catch (Exception e) {
			}
		}
		return join(ls.toArray(), 0, ls.size(), str);
	}

	public static URL[] getClassPathURLs(File basePath, String classPath, String libPath) throws IOException {
		List<URL> ls = new ArrayList<URL>();
		if (classPath.length() > 0) {
			URL[] urls = classPathToURLs(classPath = trimClassPath(classPath, basePath));
			for (int i = 0; i < urls.length; ++i)
				if (!ls.contains(urls[i]))
					ls.add(urls[i]);
		}
		if (libPath.length() > 0) {
			FileFilter ff = new FileFilter() {
				public final boolean accept(File pathName) {
					return pathName.getName().toLowerCase().endsWith(".jar") || pathName.getName().toLowerCase().endsWith(".zip");
				}
			};
			String[] arr = libPath.split(System.getProperty("path.separator", ";"));
			for (int j = 0; j < arr.length; ++j) {
				String str = arr[j].trim();
				if (str.length() <= 0)
					continue;
				File file = new File(str);
				if (!file.isAbsolute())
					file = new File(basePath, str);
				if (!file.exists() || !file.isDirectory())
					continue;
				File[] fs = file.listFiles(ff);
				for (int k = 0; k < fs.length; ++k) {
					URL url = fs[k].toURI().toURL();
					if (!ls.contains(url))
						ls.add(url);
				}
			}
		}
		URL[] urls = new URL[ls.size()];
		ls.toArray(urls);
		return urls;
	}

	public static URL[] classPathToURLs(String classPath) {
		if (classPath == null || classPath.length() == 0)
			return new URL[0];
		String[] arr = classPath.split(System.getProperty("path.separator", ";"));
		List<URL> ls = new LinkedList<URL>();
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = arr[i].trim();
			if (arr[i].length() <= 0)
				continue;
			try {
				ls.add(new File(arr[i]).toURI().toURL());
			} catch (MalformedURLException e) {
			}
		}
		URL[] urls = new URL[ls.size()];
		ls.toArray(urls);
		return urls;
	}

	public static String getBaseClassName(String className) {
		int pos = className.indexOf(36);
		if (pos == -1)
			return className;
		return className.substring(0, pos);
	}

	public static String getPackageName(String className) {
		int i = className.lastIndexOf(46);
		if (i == -1)
			return "";
		return className.substring(0, i);
	}

	public static Map<String, byte[]> loadClassData(String classes) throws Exception {
		return loadClassData(new File(System.getProperty("game_server_classes_path", "classes")), classes);
	}

	public static Map<String, byte[]> loadClassData(File classDir, String classes) throws Exception {
		Map<String, byte[]> ds = new HashMap<String, byte[]>();
		String[] names = classes.split("[;,\\s]+");
		Map<String, File> files = new HashMap<String, File>();
		for (int i = 0; i < names.length; i++) {
			if (names[i].length() == 0)
				continue;
			String pkg = getPackageName(names[i]);
			String cn = pkg.length() > 0 ? names[i].substring(pkg.length() + 1) : names[i];
			if (cn.equals("*")) {
				String[] ns = searchPackageClasses(new File[] { classDir }, pkg, true);
				for (int j = 0; j < ns.length; j++)
					files.put(ns[j], new File(classDir, ns[j].replace('.', '/') + ".class"));
			} else {
				File cd = new File(classDir, pkg.replace('.', '/'));
				final String cnc = cn + ".class";
				final String cns = cn + "$";
				File[] cfs = cd.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						if (name.equals(cnc) || (name.endsWith(".class") && name.startsWith(cns)))
							return true;
						return false;
					}
				});
				if (cfs != null)
					for (int j = 0; j < cfs.length; j++)
						files.put(pkg + "." + removeExtName(cfs[j].getName()), cfs[j]);
			}
		}
		Iterator<String> ite = files.keySet().iterator();
		while (ite.hasNext()) {
			String cn = (String) ite.next();
			File file = (File) files.get(cn);
			byte[] data = readFile(file);
			ds.put(cn, data);
		}
		return ds;
	}

	public static void main(String[] args) throws Exception {
		JarFile jf = new JarFile("E:\\you_ai\\projects\\project_3\\server_sulation\\sophia.game\\target\\sophia-game.jar");
		StringBuffer s = new StringBuffer();
		for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
			JarEntry je = e.nextElement();
			String string = je.toString();
			if (string.endsWith("/")) {
				string = string.replace("/", ".");
				s.append("import ").append(string).append("*;");
			}
		}
	}

	public static byte[] readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}

	public static byte[] readFile(File file) throws IOException {
		FileInputStream fis = null;
		byte[] data = null;
		try {
			fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
		} finally {
			if (fis != null)
				fis.close();
		}
		return data;
	}

	public static String[] searchPackageClasses(String classPath, String packageName, boolean subPackage) {
		return searchPackageClasses(classPathToURLs(classPath), packageName, subPackage);
	}

	public static String[] searchPackageClasses(URL[] searchUrls, String packageName, boolean subPackage) {
		List<File> ls = new LinkedList<File>();
		for (int i = 0; i < searchUrls.length; ++i) {
			if (!"file".equalsIgnoreCase(searchUrls[i].getProtocol()))
				continue;
			File file = new File(searchUrls[i].getPath().substring(1).toLowerCase());
			if (file.exists())
				ls.add(file);
		}
		File[] files = new File[ls.size()];
		ls.toArray(files);
		return ((String[]) searchPackageClasses(files, packageName, subPackage));
	}

	public static String[] searchPackageClasses(File[] searchPaths, String packageName, final boolean subPackage) {
		List<String> ls = new ArrayList<String>();
		String str = packageName.replace('.', '/');
		FileFilter ff = new FileFilter() {
			private boolean valOfSubPackage = subPackage;

			public boolean accept(File file) {
				if (valOfSubPackage) {
					if ((file.isDirectory() && (!file.getName().equalsIgnoreCase("META-INF"))) || file.getName().endsWith(".class"))
						return true;
				} else if (file.isFile() && file.getName().endsWith(".class"))
					return true;
				return false;
			}
		};
		for (int i = 0; i < searchPaths.length; ++i) {
			File file1;
			if (!(file1 = searchPaths[i]).exists())
				continue;
			String fname = file1.getName().toLowerCase();
			if (file1.isFile() && (fname.endsWith(".jar") || fname.endsWith(".zip"))) {
				try {
					Enumeration<?> e = new ZipFile(file1).entries();
					while (e.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) e.nextElement();
						String s = entry.getName();
						if (!s.endsWith(".class") || !s.startsWith(str))
							continue;
						s = removeExtName(s).substring(str.length() + 1);

						if (subPackage)
							ls.add(packageName + "." + s.replace('/', '.'));
						else {
							if (s.indexOf(47) != -1)
								continue;
							ls.add(packageName + "." + s);
						}
					}
				} catch (IOException e) {
				}
			} else {
				File f = null;
				if (!file1.isDirectory() || !(f = new File(file1, str)).exists())
					continue;
				File[] files = f.listFiles(ff);
				for (int j = 0; j < files.length; ++j) {
					if (files[j].isDirectory()) {
						String[] arr = searchPackageClasses(new File[] { file1 }, packageName + "." + files[j].getName(), true);
						ls.addAll(Arrays.asList(arr));
					} else
						ls.add(packageName + "." + removeExtName(files[j].getName()));
				}
			}
		}
		String[] sarr = new String[ls.size()];
		ls.toArray(sarr);
		return sarr;
	}

	public static String removeExtName(String name) {
		String s;
		int pos1 = (s = name.replace('\\', '/')).lastIndexOf(47);
		int pos = s.lastIndexOf(46);
		if (pos == -1 || pos < pos1)
			return name;
		return name.substring(0, pos);
	}

	public static String getExtName(String name) {
		String s;
		int pos1 = (s = name.replace('\\', '/')).lastIndexOf(47);
		int pos = s.lastIndexOf(46);
		if (pos == -1 || pos < pos1)
			return "";
		return name.substring(pos);
	}

	/**
	 * 列表中连接字符串
	 * 
	 * @param arr
	 *            对象列表，可以是任意对象列表
	 * @param from
	 *            列表的起始位置
	 * @param to
	 *            列表的结束位置
	 * @param obj
	 *            连接对象，可以是任意对象
	 * @return 返回连接后的字符串
	 */
	public static String join(Object[] arr, int from, int to, Object obj) {
		if (from >= 0 && to <= arr.length && to - from >= 0) {
			StringBuffer sb = new StringBuffer();
			if (to - from > 0) {
				sb.append(arr[from]);
				from += 1;
			}
			if (to - from > 0) {
				while (true) {
					sb.append(obj + "" + arr[from]);
					++from;
					if (from >= to) {
						return sb.toString();
					}
				}
			} else
				return sb.toString();
		}
		throw new IndexOutOfBoundsException();
	}

}
