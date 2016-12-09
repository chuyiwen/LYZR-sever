/**
 * 
 */
package sophia.game.core;


public final class ModuleException extends RuntimeException {

	private static final long serialVersionUID = 8342281913189741452L;

	private static String noSuchModule(Class<?> moduleClass) {
		StringBuffer sb = new StringBuffer("There is no module of type ");
		sb.append(moduleClass.toString());
		sb.append(" registered.");
		return sb.toString();
	}
	
	private ModuleException(String message) {
		super(message);
	}
	
	protected ModuleException(Class<?> moduleClass) {
		this(noSuchModule(moduleClass));
	}
}
