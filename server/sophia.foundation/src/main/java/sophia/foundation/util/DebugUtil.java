package sophia.foundation.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtil {

	public static StringBuilder printStack() {
		Throwable t = new Throwable();
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append(ste + "\n");
		}
		return sb;
	}

	public static String printStack(Exception e) {
		StringWriter message = new StringWriter();
		PrintWriter writer = new PrintWriter(message);
		e.printStackTrace(writer);
		return message.toString();
	}
	
	public static StringBuilder printStack(Throwable t) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : t.getStackTrace()) {
			sb.append(ste + "\n");
		}
		return sb;
	}
}
