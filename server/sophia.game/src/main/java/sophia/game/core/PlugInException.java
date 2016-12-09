/**
 * 
 */
package sophia.game.core;


public final class PlugInException extends RuntimeException {
	
	private static final long serialVersionUID = 2047904236099632766L;

	private static String alreadyRegisteredPlugIn(PlugIn<?> plugIn) {
		StringBuffer sb = new StringBuffer("Can not register plug in (");
		sb.append(plugIn.getClass().toString());
		sb.append("). Plug in is already registered.");
		return sb.toString();
	}
	
	private static String unknownPlugIn(Class<? extends PlugIn<?>> plugInClass) {
		StringBuffer sb = new StringBuffer("The plug in ");
		sb.append(plugInClass.toString());
		sb.append(" is unknown.");
		return sb.toString();
	}
	
	private static String illegalCoreStatus(PlugIn<?> plugIn, GameFrame.Status coreStatus) {
		StringBuffer sb = new StringBuffer("Can not register or unregister plug in ");
		sb.append(plugIn.getClass().toString());
		sb.append(". The current core status '");
		sb.append(coreStatus);
		sb.append("' does not allow modifications of the plug ins collection.");
		return sb.toString();
	}
	
	private PlugInException(String message) {
		super(message);
	}

	protected PlugInException(PlugIn<?> plugIn) {
		this(alreadyRegisteredPlugIn(plugIn));
	}
	
	protected PlugInException(PlugIn<?> plugIn, GameFrame.Status coreStatus) {
		this(illegalCoreStatus(plugIn, coreStatus));
	}
	
	protected PlugInException(Class<? extends PlugIn<?>> clazz) {
		this(unknownPlugIn(clazz));
	}
}
