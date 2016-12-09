package newbee.morningGlory.mmorpg.operatActivities;

import java.util.List;

import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;

import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class OperatActivityRefChecker {
	private static final Logger logger = Logger.getLogger(OperatActivityRefChecker.class.getName());

	public String getDescription() {
		return "运营活动礼包";
	}

	public void check(GameRefObject gameRefObject) {
		OperatActivityRef ref = (OperatActivityRef) gameRefObject;
		if (ref.getEndTime() == null)
			error(gameRefObject, ref.getTitle() + "," + "结束时间设置错误");
		if (ref.getOpenTime() == null)
			error(gameRefObject, ref.getTitle() + "," + "开始时间设置错误");

		OperatActivityType type = ref.getType();
		if (type == null)
			error(gameRefObject, ref.getTitle() + "," + "错误的类型：" + ref.getData().getInt("type"));

		AwardContent awardContent = ref.getAwardContent();
		if (awardContent == null)
			error(gameRefObject, ref.getId() + "没有配置奖励内容");
		List<AwardItem> awardItems = awardContent.getAwardItems();
		if (awardItems == null || awardItems.size() == 0)
			error(gameRefObject, ref.getTitle() + "," + "没有设置奖励物品");
		for(AwardItem item : awardItems){
			for(ItemPair itemPair : item.getItems()){
				GameRefObject itemRef =  GameRoot.getGameRefObjectManager().getManagedObject(itemPair.getItemRefId());
				if(itemRef == null){
					error(gameRefObject,"道具refId:"+itemPair.getItemRefId() + "不存在");
				}
			}
		}
	}

	private void error(GameRefObject gameRefObject, String string) {
		Error error = new Error();
		logger.error(getDescription() + "->" + gameRefObject.getClass().getSimpleName() + "[" + gameRefObject.getId() + " # " + gameRefObject.toString() + "] @ ", error);
		throw error;
	}

}
