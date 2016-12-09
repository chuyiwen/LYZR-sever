package newbee.morningGlory.mmorpg.player.store.event;

import java.util.List;
import java.util.Map;
import java.util.Set;

import newbee.morningGlory.mmorpg.store.ref.MallItemRef;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Store_ItemListResp extends ActionEventBase {
	private Map<String, MallItemRef> mallItemList;
	
	public G2C_Store_ItemListResp(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort((short)mallItemList.size());
		Set<String> keys = mallItemList.keySet();
		for (String key : keys) {
			putString(buffer, key);
			putString(buffer, MGPropertyAccesser.getStoreType(mallItemList.get(key).getProperty()));
			putString(buffer, MGPropertyAccesser.getItemId(mallItemList.get(key).getProperty()));
			buffer.put(MGPropertyAccesser.getItemSellType(mallItemList.get(key).getProperty()));
			List<Integer> nowOrigenPrice = mallItemList.get(key).getNowItemPrice().getOrigenPrice();
			buffer.putInt(nowOrigenPrice.get(1));
			buffer.putInt(nowOrigenPrice.get(2));
			if (mallItemList.get(key).getOldItemPrice() != null) {
				List<Integer> oldOrigenPrice = mallItemList.get(key).getOldItemPrice().getOrigenPrice();
				buffer.putInt(oldOrigenPrice.get(1));
				buffer.putInt(oldOrigenPrice.get(2));
			} else {
				buffer.putInt(0);
				buffer.putInt(0);
			}
			
			buffer.put(MGPropertyAccesser.getItemLimitType(mallItemList.get(key).getProperty()));
			buffer.putShort((short)MGPropertyAccesser.getItemLimitNum(mallItemList.get(key).getProperty()));
			buffer.putShort((short)MGPropertyAccesser.getNumber(mallItemList.get(key).getProperty()));
			putString(buffer, MGPropertyAccesser.getStoreLimitTime(mallItemList.get(key).getProperty()));
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public Map<String, MallItemRef> getMallItemList() {
		return mallItemList;
	}

	public void setMallItemList(Map<String, MallItemRef> mallItemList) {
		this.mallItemList = mallItemList;
	}

}