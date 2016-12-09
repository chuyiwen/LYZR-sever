package newbee.morningGlory.mmorpg.player.offLineAI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.offLineAI.model.AILogModel;
import newbee.morningGlory.mmorpg.player.offLineAI.model.OfflineBagStateModel;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOTask;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.HandUpSkillRef;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.OfflineAIMapRef;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.AbstractComponent;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.gson.Gson;

public class PlayerAvatarMgr extends AbstractComponent{
	
	private static final Logger logger = Logger.getLogger(PlayerAvatarMgr.class);

	
	public static final String MAGICBLOODSTONE_ITEMREFID = "item_lixianmuxueshi";	//魔血石 道具编号
	public static final int MAGICBLOODSTONE_VAR = 800;			//魔血石每次使用后的回复值
	public static final int ANSWER_CD = 1000 * 8;				//每次吃药的CD时间  八秒
	public static final int BAG_MAX_NUM = 160;					//背包的最大数量  加上 经验存储 和 金币存储
	public static final int PLAYERAVATAR_WATCH_TIME = 5;		//守护时间 秒
	public static final long TIME_STAMP = 1000 * 5;				//玩家在重新进入场景时的间隔时间
	public static final long RANDOMSKILLTIME_STAMP = 1000 * 10;	//玩家每次随机使用技能时的间隔时间
	public static final String[] HP_ITEM_REFID = {"item_drug_1","item_drug_2","item_drug_3","item_drug_7","item_drug_8","item_drug_9"};	//hp药瓶道具编号  从小到大
	public static final String[] MP_ITEM_REFID = {"item_drug_4","item_drug_5","item_drug_6","item_drug_7","item_drug_8","item_drug_9"};	//mp药瓶道具编号  从小到大
	
	
	
	private static ConcurrentHashMap<String,PlayerAvatar> allPlayerAvatar = new ConcurrentHashMap<String,PlayerAvatar>() ;//所有替身集合 key:玩家ID value:替身
	private static List<OfflineBagStateModel> OfflineBagStateModelList = new ArrayList<>();//所有离线背包的状态集合
	private static Map<Integer,String> offlineAIMapRefMap = new HashMap<Integer, String>();//所有离线AI等级地图REF
	private static Map<String,HandUpSkillRef> handUpSkillRefMap = new HashMap<String,HandUpSkillRef>();//所有的可用AI技能 TODO: 暂时这里没有用，由代码那里写死的
	
	private SFTimer secondInterval;
	private SFTimer minuteCalendarChime;
	
	@Override
	public void ready() {
		super.ready();
		secondInterval = MMORPGContext.getTimerCreater().secondInterval(PlayerAvatarWatchTask.getInstance());
		minuteCalendarChime = MMORPGContext.getTimerCreater().minuteCalendarChime(OffLineAIDAOTask.getInstance());
		
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(0, 16,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(17, 32,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(33, 48,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(49, 64,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(65, 80,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(81, 96,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(97, 112,6005));
		OfflineBagStateModelList.add(OfflineBagStateModel.createOfflineBagStateModel(113, 128,6005));
	}
	@Override
	public void suspend() {
		if (secondInterval != null) {
			secondInterval.cancel();
		}
		
		if (minuteCalendarChime != null) {
			minuteCalendarChime.cancel();
		}
		
		//TODO: 这里就是停服操作???
		OffLineAIDAOMgr.afterWrite();
	}
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		super.handleGameEvent(event);
	}
	
	
	public static void putOfflineAIMapRef(OfflineAIMapRef ref){
		for (int i = ref.getMinLevelId(); i <= ref.getMaxLevelId(); i++) {
			offlineAIMapRefMap.put(i, ref.getMapId());
		}
	}
	public static String getOfflineAIMap(int playerLv){
		return offlineAIMapRefMap.get(playerLv);
	}
	public static void putHandUpSkillRef(HandUpSkillRef ref){
		handUpSkillRefMap.put(ref.getSkillRefId(),ref);
	}
	public static HandUpSkillRef getHandUpSkillRef(String skillRefId){
		return handUpSkillRefMap.get(skillRefId);
	}
	public static int getOfflineBagState(int num){
		for (int i = 0; i < OfflineBagStateModelList.size(); i++) {
			OfflineBagStateModel offlineBagStateModel = OfflineBagStateModelList.get(i);
			if(offlineBagStateModel.isOK(num)){
				return offlineBagStateModel.getState();
			}
		}
		return 1;
	}
	static void putPlayerAvatar(PlayerAvatar playerAvatar){
		allPlayerAvatar.put(playerAvatar.getPlayerId(), playerAvatar);
	}
	static PlayerAvatar removePlayerAvatar(String id){
		return allPlayerAvatar.remove(id);
	}
	
	
	/** 替身的守护通知处理 */
	static void watchPlayerAvatar(){
		long nowTime = System.currentTimeMillis();
		for (Iterator<Entry<String,PlayerAvatar>> iterator = allPlayerAvatar.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, PlayerAvatar> next = iterator.next();
			PlayerAvatar playerAvatar = next.getValue();
			if(playerAvatar.isDestructionState()){
				iterator.remove();
				PlayerAvatarMgr.leaveWorld(playerAvatar);
				OffLineAIDAOMgr.removePlayerAvatarData(playerAvatar.getPlayerId());
				continue;
			}
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			String playerId = playerAvatar.getPlayerId();
			
			//保护当玩家的在线状态
			if(playerManager.getOnlinePlayer(playerId) != null){
				leaveWorld(playerAvatar);
				continue;
			}
			
			//如果是刚创建的则时间到了 就放入地图
			if(playerAvatar.isCreateState() && nowTime - playerAvatar.getCreateTime() >= TIME_STAMP){
				enterWorldImp(playerAvatar);
				playerAvatar.setActivityState();
				
				AILogModel aiLogModel = AILogModel.createInToGameSceneLogModel(playerAvatar.getAiGameScene().getRef().getId());
				PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerId);
				playerAvatarData.addAILogModel(aiLogModel);
				continue;
				
			}
			//如果是死亡状态则时间到了 就放入地图
			if(playerAvatar.isDeathState() && nowTime - playerAvatar.getDeathTime() >= TIME_STAMP){
				enterWorldImp(playerAvatar);
				playerAvatar.revive();
				playerAvatar.setActivityState();
				continue;
				
			}
		}
	}
	
	
	/** 将一个替身放入地图 */
	static void enterWorldImp(PlayerAvatar playerAvatar) {
		GameScene aiGameScene = playerAvatar.getAiGameScene();
		Position randomGameScenePosition = randomGameScenePosition(playerAvatar.getAiGameScene());
		int x = randomGameScenePosition.getX();
		int y = randomGameScenePosition.getY();
		GameRoot.getGameObjectManager().addGameObject(playerAvatar);
		playerAvatar.getAoiComponent().enterScene(aiGameScene, x, y);
		playerAvatar.setOnline(true);
		logger.info("enterWorldImpl " + randomGameScenePosition);
	}
	/** 将一个替身离开地图 */
	static void leaveWorld(PlayerAvatar playerAvatar) {
		if(GameRoot.getGameObjectManager().getObjectForId(playerAvatar.getId()) == null){
			//如果都不对象池里面了 则表示已经离开地图了
			return;
		}
		GameScene crtScene = playerAvatar.getAiGameScene();
		playerAvatar.getAoiComponent().leaveScene(crtScene);
		GameRoot.getGameObjectManager().removeGameObject(playerAvatar);
		playerAvatar.setOnline(false);
	}
	
	/**
	 * 查询玩家可以离线AI挂机的地图 
	 * @param player
	 * @return 如果为null表示不能离线挂机
	 */
	static GameScene searchGameScene(Player player){
		//TODO： 离线AI挂机的地图 
		int playerLv = player.getExpComponent().getLevel();
		String mapRefId = getOfflineAIMap(playerLv);
//		return MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(mapRefId);
		return MMORPGContext.getGameAreaComponent().getGameArea().getSceneById("S001");
	}
	/** 随机指定场景的坐标位置 */
	static Position randomGameScenePosition(GameScene gameScene){
		//TODO： 随机指定场景的坐标位置
		SceneGrid randomWalkableGrid = GameSceneHelper.getRandomWalkableGrid(gameScene);
//		return new Position(randomWalkableGrid.getColumn(), randomWalkableGrid.getRow());
		return new Position(42, 52);
//		return new Position(182,302);
	}
	
	/**
	 * 放入指定道具列表到玩家背包，当放不下的时候则以邮件的形式发放
	 * @param player
	 * @param itemList
	 */
	static void putItemAndEmail(Player player,List<ItemPair> itemList){
		if(player == null || itemList == null){
			return;
		}
		List<ItemPair> emailItemList = new ArrayList<ItemPair>();
		StringBuilder sb = new StringBuilder("成功领取离线挂机产出：");
		for (int i = 0; i < itemList.size(); i++) {
			ItemPair itemPair = itemList.get(i);
			RuntimeResult addItemResult = ItemFacade.addItem(player, itemPair, ItemOptSource.Avatar);
			if(!addItemResult.isOK()){
				emailItemList.add(itemPair);
				ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(itemPair.getItemRefId());
				sb.append(MGPropertyAccesser.getName(itemRef.getProperty())).append(" x").append(itemPair.getNumber()).append(" ");
			}
		}
		String itemJsonStr = (new Gson()).toJson(emailItemList);
		MailMgr.sendMailById(player.getId(), "成功领取离线挂机产出", (byte) 0, itemJsonStr, 0, 0, 0);
	}
}
