/**
 * 
 */
package sophia.game.core;

import java.util.List;

public final class DependencyException extends RuntimeException {

	private static final long serialVersionUID = 8200800415311229916L;

	private static String injectionFailed(Object dependency, PlugIn<?> dependent, String reason) {
		StringBuffer sb = new StringBuffer("Could not inject dependency ");
		if (dependency != null) {
			sb.append(dependency.getClass().toString());
			sb.append(" into ");
		}
		if (dependent != null) {
			sb.append(dependent.getClass().toString());
			sb.append(" due to ");
		}
		sb.append(reason);

		return sb.toString();
	}

	private static String circularDependency(List<PlugIn<?>> list) {
		StringBuffer sb = new StringBuffer("Detected a circular dependency between: ");

		for (int i = 0; i < list.size(); i++) {
			sb.append("\n   ->");
			sb.append(list.get(i).getClass().toString());
		}

		return sb.toString();
	}

	private static String noSetterMethod(String fullQualifiedPlugInName, String simpleFieldType, String fieldName, String fieldModifiers) {
		StringBuffer sb = new StringBuffer("@Dependency ");
		sb.append(fieldModifiers);
		sb.append(" ");
		sb.append(fieldName);
		sb.append(" in class ");
		sb.append(fullQualifiedPlugInName);
		sb.append(" has no correctly named setter method 'public void set");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1, fieldName.length()));
		sb.append("(");
		sb.append(simpleFieldType);
		sb.append(" ");
		sb.append(fieldName);
		sb.append(")'.");

		return sb.toString();
	}

	private DependencyException(String message) {
		super(message);
	}

	protected DependencyException(String fullQualifiedPlugInName, String simpleFieldType, String fieldName, String fieldModifiers) {
		this(noSetterMethod(fullQualifiedPlugInName, simpleFieldType, fieldName, fieldModifiers));
	}

	protected DependencyException(List<PlugIn<?>> circularDependencies) {
		this(circularDependency(circularDependencies));
	}

	protected DependencyException(Object dependency, PlugIn<?> dependent, String reason) {
		this(injectionFailed(dependency, dependent, reason));
	}
}
