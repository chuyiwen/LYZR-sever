package newbee.morningGlory.ref.loader;

import java.util.Collection;

import newbee.morningGlory.ref.JSONDataManagerContext;
import newbee.morningGlory.ref.symbol.PropertySymbolLoader;

import org.junit.Before;
import org.junit.Test;

import sophia.mmorpg.item.ref.ItemRef;

public class ItemRefLoaderTest {

	@Before
	public void setUp() throws Exception {
		JSONDataManagerContext.load();
		PropertySymbolLoader.load();
	}

	@Test
	public void testLoadAll() {
		PropsItemRefLoader loader = new PropsItemRefLoader();
		Collection<ItemRef> itemRefs = loader.loadAll();
		ItemRef itemRef = (ItemRef) itemRefs.toArray()[0];
	}
}
