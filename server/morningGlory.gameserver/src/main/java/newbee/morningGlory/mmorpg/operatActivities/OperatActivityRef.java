package newbee.morningGlory.mmorpg.operatActivities;

import java.util.Date;

import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * 运营活动引用数据<br>
 */
public class OperatActivityRef extends AbstractGameRefObjectBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2941467395782355236L;
	private String id;
	private AwardContent awardContent;
	private Date openTime;
	private Date endTime;
	// 后台传输的数据
	public String sourceData;
	private int type;
	private OABObject map = new OABObject();

	@Override
	public String toString() {
		return "OperatActivityRef@" + getType();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * 类型
	 * 
	 * @return
	 */
	public OperatActivityType getType() {
		return OperatActivityType.get(type);
	}
	
	public void setType(int type){
		this.type = type;
	}
	/**
	 * 分组
	 * 
	 * @return
	 */
	public OperatActivityGroup getGroup() {
		return OperatActivityGroup.get(map.getInt("group"));
	}

	/**
	 * 标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return map.getString("title");
	}

	/**
	 * 描述
	 * 
	 * @return
	 */
	public String getDesc() {
		return map.getString("desc");
	}

	/**
	 * 奖励内容
	 * 
	 * @return
	 */
	public AwardContent getAwardContent() {
		return awardContent;
	}

	public void setAwardContent(AwardContent awardContent) {
		this.awardContent = awardContent;
	}

	public OABObject getData() {
		return this.map;
	}
}
