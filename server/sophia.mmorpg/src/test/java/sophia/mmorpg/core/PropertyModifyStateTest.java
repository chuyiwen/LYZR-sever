package sophia.mmorpg.core;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class PropertyModifyStateTest {
	private static Logger logger = Logger.getLogger(PropertyModifyStateTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPropertyModifyStatePropertyModifyState() {
		PropertyModifyState s = new PropertyModifyState((short)10, true);
		PropertyModifyState new_s = new PropertyModifyState(s);
		logger.info("s: " + s);
		logger.info("new_s: " + new_s);
		assertTrue(s != new_s);
	}

}
