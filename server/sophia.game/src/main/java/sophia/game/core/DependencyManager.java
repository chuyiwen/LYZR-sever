/**
 * 
 */
package sophia.game.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


class DependencyManager {
	private List<PlugIn<?>> plugIns;

	protected DependencyManager(List<PlugIn<?>> plugIns) {
		assert (plugIns != null);
		this.plugIns = plugIns;
	}

	protected void sortPlugIns() {

		List<PlugIn<?>> unsorted = new LinkedList<PlugIn<?>>(plugIns);
		assert (unsorted.size() == plugIns.size());

		List<PlugIn<?>> sorted = new LinkedList<PlugIn<?>>();
		Stack<PlugIn<?>> pending = new Stack<PlugIn<?>>();

		// 先加入没有依赖的插件
		Class<?> curClass;
		for (int i = 0; i < unsorted.size(); i++) {
			curClass = unsorted.get(i).getClass();
			if (getDependencyFields(curClass).size() == 0) {
				sorted.add(unsorted.get(i));
			}
		}
		// 移除已经加入的插件
		for (PlugIn<?> plugIn : sorted) {
			unsorted.remove(plugIn);
		}

		// 创建插件的依赖
		PlugIn<?> curPlugIn;
		List<Field> dependencies;

		while (unsorted.size() > 0) {
			pending.push(unsorted.get(0));
			unsorted.remove(0);

			while (pending.size() > 0) {
				curPlugIn = pending.lastElement();
				dependencies = getDependencyFields(curPlugIn.getClass());
				for (Field dependency : dependencies) {
					if (containsModule(dependency.getType(), sorted)) {
					} else if (containsModule(dependency.getType(), unsorted)) {
						PlugIn<?> next = null;
						for (int i = 0; i < unsorted.size(); i++) {
							if (dependency.getType().isAssignableFrom(
									getModuleClass(unsorted.get(i)))) {
								next = unsorted.get(i);
							}
						}
						assert (next != null);
						unsorted.remove(next);
						// 压入堆栈
						pending.push(next);
						// 跳出循环，开始查找最新压入堆栈的插件的依赖
						break;
					} else if (containsModule(dependency.getType(), pending)) {
						List<PlugIn<?>> list2 = pending.subList(0,
								pending.size() - 1);
						list2.add(curPlugIn);
						// 循环依赖，抛出异常
						throw new DependencyException(list2);
					}

					// 找到了所有的依赖，把插件加入sorted
					if (pending.size() > 0) {
						sorted.add(pending.pop());
					}
				}
			}
		}

		assert (sorted.size() == plugIns.size());
		assert (unsorted.size() == 0);
		assert (pending.size() == 0);

		plugIns.clear();
		for (int i = 0; i < sorted.size(); i++) {
			plugIns.add(sorted.get(i));
		}
	}

	protected void injectDependencies(PlugIn<?> plugIn,
			List<PlugIn<?>> availablePlugIns) {

		List<Field> dependencies = getDependencyFields(plugIn.getClass());
		Method setterMethod = null;
		Object dependency = null;

		try {
			for (Field field : dependencies) {
				setterMethod = getSetterMethod(plugIn.getClass(), field);
				dependency = getModule(field.getType(), availablePlugIns);
				setterMethod.invoke(plugIn, dependency);
			}
		} catch (IllegalArgumentException e) {
			throw new DependencyException(dependency, plugIn, e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DependencyException(dependency, plugIn, e.getMessage());
		} catch (InvocationTargetException e) {
			throw new DependencyException(dependency, plugIn, e.getMessage());
		}
	}

	private List<Field> getDependencyFields(Class<?> clazz) {

		List<Field> dependencies = new LinkedList<Field>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Dependency.class)) {
				dependencies.add(field);
			}
		}

		return dependencies;
	}

	protected Class<?> getModuleClass(PlugIn<?> plugIn) {
		try {
			Method m = plugIn.getClass().getDeclaredMethod("getModule",
					new Class<?>[0]);
			return m.getReturnType();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	protected boolean containsModule(Class<?> moduleClass, List<PlugIn<?>> list) {

		Iterator<PlugIn<?>> iter = list.iterator();
		while (iter.hasNext()) {
			if (moduleClass.isAssignableFrom(getModuleClass(iter.next()))) {
				return true;
			}
		}

		return false;
	}

	protected Object getModule(Class<?> moduleClass, List<PlugIn<?>> list) {

		Iterator<PlugIn<?>> iter = list.iterator();
		PlugIn<?> plugIn;
		while (iter.hasNext()) {
			plugIn = iter.next();
			if (moduleClass.isAssignableFrom(getModuleClass(plugIn))) {
				return plugIn.getModule();
			}
		}
		return null;
	}

	private Method getSetterMethod(Class<?> clazz, Field field) {

		for (Method method : clazz.getMethods()) {
			if (method.getName().compareToIgnoreCase("set" + field.getName()) == 0) {
				return method;
			}
		}

		// 没有定义setter方法
		throw new DependencyException(clazz.getName(), field.getType()
				.getSimpleName(), field.getName(), Modifier.toString(field
				.getModifiers()));

	}
}
