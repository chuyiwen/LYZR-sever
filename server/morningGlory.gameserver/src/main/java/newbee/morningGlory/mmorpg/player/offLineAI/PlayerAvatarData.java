package newbee.morningGlory.mmorpg.player.offLineAI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import newbee.morningGlory.mmorpg.player.offLineAI.model.AILogModel;
import newbee.morningGlory.mmorpg.player.offLineAI.model.HpMpModel;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

public class PlayerAvatarData {

	private static final Logger logger = Logger.getLogger(PlayerAvatarData.class);
	
	
	private int currentVersion = 10000;
	
	private String playerId;
	
	private List<ItemPair> itemList = new ArrayList<ItemPair>();//道具
	private int exp;//经验存储
	private int money;//金币存储
	private List<AILogModel> aiLogModelList = new ArrayList<AILogModel>();//日志存储
	private OffLineAISeting offLineAISeting = new OffLineAISeting();//离线AI挂机设置  不用存储
	private long exitCacheTime = 0;//准备移除缓存的开始时间

	private ReentrantLock lock = new ReentrantLock();
	
	public PlayerAvatarData(String playerId) {
		this.playerId = playerId;
	}
	
	
	/**
	 * 掉落物品 随机掉落百分之5-10的道具
	 * 不包含魔血石
	 * @return 掉落出来的道具集合
	 */
	Map<String,ItemPair> dropItem(){
		lock.lock();
		try {
			int itemListSize = this.itemList == null ? 0 : this.itemList.size();

			//计算背包里可以掉落的道具总数量
			int sumNum = 0;
			for (int i = 0; i < itemListSize; i++) {
				ItemPair itemPair = this.itemList.get(i);
				if(itemPair.getItemRefId().equals(PlayerAvatarMgr.MAGICBLOODSTONE_ITEMREFID)){
					continue;
				}
				sumNum += itemPair.getNumber();
			}
			if (sumNum <= 0) {
				// 没任何东东可以掉落
				return null;
			}
			// 在百分之随机计算可以掉落的道具个数
			int random = SFRandomUtils.random(5, 10);
			double probability = random / 100;
			int dropNum = (int)(sumNum * probability);
			dropNum = dropNum < 0 ? 1 : dropNum;
			
			Map<String, ItemPair> dropItemMap = new HashMap<String, ItemPair>();
			//随机一个开始位置
			int index = SFRandomUtils.random(itemListSize) - 1;
			for (int i = 0; i < itemListSize; i++) {
				ItemPair itemPair = this.itemList.get(index);
				if(itemPair.getItemRefId().equals(PlayerAvatarMgr.MAGICBLOODSTONE_ITEMREFID)){
					continue;
				}
				//计算可以从当前位置掉落出多少数量出来
				int canDropNum = 0;
				if (itemPair.getNumber() <= dropNum) {
					//如果不足则全掉
					this.itemList.remove(index);
					canDropNum = itemPair.getNumber();
					dropNum -= canDropNum;
				}else{
					//如果有剩余 只掉落出需要掉落的数量
					itemPair.setNumber(itemPair.getNumber() - dropNum);
					canDropNum = dropNum;
					dropNum = 0;
				}
				//放入掉落池
				ItemPair dropItem = dropItemMap.get(itemPair.getItemRefId());
				if (dropItem == null) {
					dropItem = new ItemPair(itemPair.getItemRefId(), canDropNum, false);
					dropItemMap.put(itemPair.getItemRefId(), dropItem);
				} else {
					dropItem.setNumber(dropItem.getNumber() + canDropNum);
				}
				//已经足够了就跳出
				if(dropNum <= 0){
					break;
				}
				//跳到下一个位置索引
				index ++;
				if(index >= this.itemList.size()){
					index = 0;
				}
			}
			return dropItemMap;
		} finally {
			lock.unlock();
		}
	}
	/**
	 * 添加一个日志记录
	 * @param aiLogModel
	 */
	void addAILogModel(AILogModel aiLogModel){
		this.aiLogModelList.add(aiLogModel);
		if(this.aiLogModelList.size() >= 10){
			this.aiLogModelList.remove(this.aiLogModelList.size() - 1);
		}
	}
	/** 添加经验 */
	void addExp(int var){
		this.exp += var;
	}
	/** 添加金钱 */
	void addMoney(int var){
		this.money += var;
	}
	/**
	 * 添加道具进入背包 要么全成功要么全失败
	 * @param itemRefId	道具ref
	 * @param num		道具数量
	 * @return
	 */
	boolean addItem(String itemRefId,int num){	
		int itemListSize = this.itemList == null ? 0 : this.itemList.size();
		if(itemListSize >= PlayerAvatarMgr.BAG_MAX_NUM){
			return false;
		}
		ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
		int maxStackNumber = MGPropertyAccesser.getMaxStackNumber(itemRef.getProperty());
		boolean canStack = MGPropertyAccesser.getMaxStackNumber(itemRef.getProperty()) > 1;
		
		lock.lock();
		try {
			int canPutItemNum = this.getCanPutItemNum(itemRefId);
			if(canPutItemNum < num){
				return false;
			}
			
			int addItemNum = num;
			// 先看是否可以叠加 如果可以叠加 先叠加完
			if (canStack) {
				for (int i = 0; i < itemListSize; i++) {
					ItemPair itemPair = itemList.get(i);
					if (!itemPair.getItemRefId().equals(itemRefId)) {
						continue;
					}
					int freeStackNumber = maxStackNumber - itemPair.getNumber();
					if (addItemNum > freeStackNumber) {
						addItemNum -= freeStackNumber;
						itemPair.setNumber(itemPair.getNumber() + freeStackNumber);
					} else {
						itemPair.setNumber(itemPair.getNumber() + addItemNum);
						addItemNum = 0;
					}
					if (addItemNum <= 0) {
						break;
					}
				}
			}
			// 剩下的 就算是可以叠加也需要分成几个道具去存储 因为前面已经叠加完了
			if (addItemNum > 0) {
				int slotNumber = addItemNum / maxStackNumber;
				if (addItemNum % maxStackNumber != 0) {
					slotNumber += 1;
				}
				for (int i = 0; i < slotNumber; i++) {
					if (addItemNum <= 0) {
						break;
					}
					int newItemNumber = 0;
					if (addItemNum > maxStackNumber) {
						newItemNumber = maxStackNumber;
					} else {
						newItemNumber = addItemNum;
					}
					ItemPair itemPair = new ItemPair(itemRefId, newItemNumber,false);
					addItemNum -= newItemNumber;
					this.itemList.add(itemPair);
					if (this.itemList.size() >= PlayerAvatarMgr.BAG_MAX_NUM) {
						return true;
					}
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	
	
	
	/**
	 * 扣除一个HP回复药  
	 * @return 可以恢复的HP
	 */
	HpMpModel reomveHpItem(){
		lock.lock();
		try {
			int hp = 0;
			int mp = 0;
			for (int i = 0; i < PlayerAvatarMgr.HP_ITEM_REFID.length; i++) {
				boolean removeItemByItemRefId = this.removeItemByItemRefId(PlayerAvatarMgr.HP_ITEM_REFID[i],1);
				if(!removeItemByItemRefId){
					continue;
				}
				ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(PlayerAvatarMgr.HP_ITEM_REFID[i]);
				hp = MGPropertyAccesser.getHP(itemRef.getEffectProperty());
				mp = MGPropertyAccesser.getMP(itemRef.getEffectProperty());
			}
			if(hp <= 0){
				if(this.removeItemByItemRefId(PlayerAvatarMgr.MAGICBLOODSTONE_ITEMREFID,1)){
					hp = PlayerAvatarMgr.MAGICBLOODSTONE_VAR;
				}
			}
			if(hp <= 0){
				return null;
			}
			return HpMpModel.createHpMpModel(hp, mp);
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 扣除MP回复药 
	 * @return 可以恢复的MP
	 */
	HpMpModel reomveMpItem(){
		lock.lock();
		try {
			int hp = 0;
			int mp = 0;
			for (int i = 0; i < PlayerAvatarMgr.MP_ITEM_REFID.length; i++) {
				boolean removeItemByItemRefId = this.removeItemByItemRefId(PlayerAvatarMgr.MP_ITEM_REFID[i],1);
				if(!removeItemByItemRefId){
					continue;
				}
				ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(PlayerAvatarMgr.MP_ITEM_REFID[i]);
				hp = MGPropertyAccesser.getHP(itemRef.getEffectProperty());
				mp = MGPropertyAccesser.getMP(itemRef.getEffectProperty());
			}
			if(mp <= 0){
				if(this.removeItemByItemRefId(PlayerAvatarMgr.MAGICBLOODSTONE_ITEMREFID,1)){
					mp = PlayerAvatarMgr.MAGICBLOODSTONE_VAR;
				}
			}
			if(mp <= 0){
				return null;
			}
			return HpMpModel.createHpMpModel(hp, mp);
		}finally{
			lock.unlock();
		}
	}
	
	/** 获取可以放入指定的道具数量 */
	int getCanPutItemNum_lock(String itemRefId) {
		lock.lock();
		try{
			return this.getCanPutItemNum(itemRefId);
		}finally{
			lock.unlock();
		}
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	private boolean isHaveItemByItemRefId(String itemRefId, int num) {
		// 先检查是否有足够的数量
		int oldNum = 0;
		for (int i = 0; i < this.itemList.size(); i++) {
			ItemPair itemPair = this.itemList.get(i);
			if (!itemPair.getItemRefId().equals(itemRefId)) {
				continue;
			}
			oldNum += itemPair.getNumber();
		}
		if (oldNum < num) {
			return false;
		}
		return true;
	}
	
	/** 获取可以放入指定的道具数量 */
	private int getCanPutItemNum(String itemRefId) {
		ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager()
				.getManagedObject(itemRefId);
		int maxStackNumber = MGPropertyAccesser.getMaxStackNumber(itemRef
				.getProperty());
		int num = PlayerAvatarMgr.BAG_MAX_NUM - this.itemList.size();
		for (int i = 0; i < itemList.size(); i++) {
			ItemPair itemPair = itemList.get(i);
			if (!itemPair.getItemRefId().equals(itemRefId)) {
				continue;
			}
			num = num + (maxStackNumber - itemPair.getNumber());
		}
		return num;
	}
	
	
	private boolean removeItemByItemRefId(String itemRefId, int num) {
		if (!this.isHaveItemByItemRefId(itemRefId, num)) {
			return false;
		}
		int removeNumn = num;
		for (int i = this.itemList.size() - 1; i <= 0; i--) {
			ItemPair itemPair = this.itemList.get(i);
			if (!itemPair.getItemRefId().equals(itemRefId)) {
				continue;
			}
			if (itemPair.getNumber() <= removeNumn) {
				removeNumn -= itemPair.getNumber();
				this.itemList.remove(i);
			} else {
				itemPair.setNumber(itemPair.getNumber() - removeNumn);
			}
		}
		return true;
	}
	
	
	
	public List<ItemPair> getReadItemList() {
//		if (this.itemList.size() <= 0) {
//			this.addItem("item_danyao_1", 1);
//			this.addItem("item_danyao_2", 2);
//			this.addItem("item_danyao_3", 3);
//			this.addItem("item_danyao_4", 4);
//			this.addItem("item_danyao_5", 5);
//			this.addItem("item_2exp", 5);
//			this.addItem("item_3exp", 5);
//		}
		return new ArrayList<ItemPair>(this.itemList);
	}
	
	
	public String getPlayerId() {
		return playerId;
	}
	public OffLineAISeting getOffLineAISeting() {
		return offLineAISeting;
	}
	public void setExitTime(long exitCacheTime) {
		this.exitCacheTime = exitCacheTime;
	}
	public boolean isCanExitCache(long now) {
		if(this.exitCacheTime <= 0){
			return false;
		}
		if((now - this.exitCacheTime) >= OffLineAIDAOMgr.EXIT_CACHE_TIME){
			return true;
		}
		return false;
	}
	
	
	public int getExp() {
		return exp;
	}
	public int getMoney() {
		return money;
	}
	public List<AILogModel> getAiLogModelList() {
		return aiLogModelList;
	}
	
	
	public int getExpAndClear() {
		int var = exp;
		this.exp = 0;
		return var;
	}
	public int getMoneyAndClear() {
		int var = money;
		this.money = 0;
		return var;
	}
	public List<ItemPair> getItemListAndClear() {
		lock.lock();
		try {
			List<ItemPair> list = new ArrayList<ItemPair>(this.itemList);
			this.itemList.clear();
			return list;
		} finally {
			lock.unlock();
		}
	}
	
	
	/** 编码 */
	public byte[] encoded() {
		lock.lock();
		try {
			ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
			
			buffer.writeInt(currentVersion);//写入当前版本
			
			int itemListSize = this.itemList == null ? 0 : this.itemList.size();
			buffer.writeShort((short) itemListSize);
			for (int i = 0; i < itemListSize; i++) {
				ItemPair itemPair = this.itemList.get(i);
				buffer.writeString(itemPair.getItemRefId());
				buffer.writeInt(itemPair.getNumber());
			}
			buffer.writeInt(this.exp);
			buffer.writeInt(this.money);

			int aiLogModelListSize = this.aiLogModelList == null ? 0
					: this.aiLogModelList.size();
			buffer.writeShort((short) aiLogModelListSize);
			for (int i = 0; i < aiLogModelListSize; i++) {
				AILogModel aiLogModel = this.aiLogModelList.get(i);
				buffer.writeInt(aiLogModel.getType());
				if (aiLogModel.getType() == AILogModel.TYPE_1) {
					buffer.writeString(aiLogModel.getAiGameSceneRefId());
				} else if (aiLogModel.getType() == AILogModel.TYPE_2) {
					buffer.writeString(aiLogModel.getPlayerId());
					buffer.writeString(aiLogModel.getPlayerName());

					List<ItemPair> dorpItems = aiLogModel.getDorpItems();
					int dorpItemsSize = dorpItems == null ? 0 : dorpItems
							.size();
					buffer.writeShort((short) dorpItemsSize);
					for (int j = 0; j < dorpItemsSize; j++) {
						ItemPair itemPair = dorpItems.get(j);
						buffer.writeString(itemPair.getItemRefId());
						buffer.writeInt(itemPair.getNumber());
					}
				}
			}

			return buffer.getData();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}finally{
			lock.unlock();
		}
	}
	/** 解码 */
	public void decode(byte[] bytes){
		if(bytes == null){
			return;
		}
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(bytes);
		int currentVersion = buffer.readInt();
		if (currentVersion == 10000) {
			decode10000(buffer);
		} else{
			logger.error("读出版本没有对应读出方法");
		}
	}
	public void decode10000(ByteArrayReadWriteBuffer buffer){
		this.itemList.clear();
		short itemListSize = buffer.readShort();
		for (int i = 0; i < itemListSize; i++) {
			String id = buffer.readString();
			int num = buffer.readInt();
			ItemPair itemPair = new ItemPair(id, num, false);
			this.itemList.add(itemPair);
		}
		this.exp = buffer.readInt();
		this.money = buffer.readInt();
		
		this.aiLogModelList.clear();
		int aiLogModelListSize = buffer.readShort();
		for (int i = 0; i < aiLogModelListSize; i++) {
			int type = buffer.readInt();
			if(type == AILogModel.TYPE_1){
				String aiGameSceneRefId = buffer.readString();
				this.aiLogModelList.add(AILogModel.createInToGameSceneLogModel(aiGameSceneRefId));
			}else if(type == AILogModel.TYPE_2){
				String playerId = buffer.readString();
				String playerName = buffer.readString();
				
				List<ItemPair> dorpItems = new ArrayList<ItemPair>();
				int dorpItemsSize = buffer.readShort();
				for (int j = 0; j < dorpItemsSize; j++) {
					String id = buffer.readString();
					int num = buffer.readInt();
					ItemPair itemPair = new ItemPair(id, num, false);
					dorpItems.add(itemPair);
				}
				this.aiLogModelList.add(AILogModel.createDeadLogModel(playerId, playerName, dorpItems));
			}
		}
	}

	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("道具:");
		for (int i = 0; i < itemList.size(); i++) {
			ItemPair item = itemList.get(i);
			sb.append("(").append(item.getItemRefId()).append(",").append(item.getNumber()).append(")");
		}
		sb.append("\n经验:").append(this.exp);
		sb.append("\n金币:").append(this.money);
		sb.append("\n");
		for (int i = 0; i < aiLogModelList.size(); i++) {
			AILogModel aiLogModel = aiLogModelList.get(i);
			sb.append(aiLogModel.getType()).append("  ");
		}
		return sb.toString();
	}
}
