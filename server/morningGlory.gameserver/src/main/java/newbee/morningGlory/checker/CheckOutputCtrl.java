package newbee.morningGlory.checker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

public interface CheckOutputCtrl {
	static final Logger logger = Logger.getLogger(CheckOutputCtrl.class);

	public void print(String txt);

	public void println(String txt);

	public void error(String info, Throwable ex);

	public void warn(String info);

	public static final CheckOutputCtrl Default = new CheckOutputCtrl() {
		private PrintWriter out;

		protected synchronized void append(String msg, Throwable t) {
			if (out == null) {
				FileOutputStream os;
				try {
					os = new FileOutputStream("checker.log");
					out = new PrintWriter(os, true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return;
			}

			out.println(msg);
			logger.info(msg);
			if (t != null) {
				t.printStackTrace(out);
				t.printStackTrace(System.out);
			}
			out.flush();
		}

		@Override
		public void println(String txt) {
			append(txt, null);
		}

		@Override
		public void print(String txt) {
			append(txt, null);
		}

		@Override
		public void error(String info, Throwable ex) {
			append(info, ex);
		}

		@Override
		public void warn(String info) {
			append(info, null);
		}
	};

}