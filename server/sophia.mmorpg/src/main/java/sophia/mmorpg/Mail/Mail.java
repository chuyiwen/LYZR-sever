package sophia.mmorpg.Mail;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.IoBufferUtil;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-10 下午3:30:03
 * @version 1.0
 */
public class Mail {
	// 接受者id
	private String playerId;
	// 邮件Id
	private String mailId;
	// 邮件标题
	private String title;
	// 邮件内容
	private String Content;
	// 元宝
	private int gold = 0;
	// 绑定元宝
	private int bindGold = 0;
	// 游戏币
	private int coin = 0;
	// 物品
	private String item = "";
	// 是否已读
	private boolean isRead = false;
	// 关联id
	private String relateMailId;

	// 物品实例
	private Item itemInstance;
	// 发送时间
	private long time = System.currentTimeMillis();

	public final static byte huodong = 0;
	public final static byte gonggao = 1;
	public final static byte GMReplyCustomType = 2; // GM邮件
	public final static byte CustomToGMType = 3; // 客户发送给GM的邮件
	public final static byte auctionNormal = 4;
	public final static byte auctionCancel = 5;
	public final static byte auctionExpired = 6;
	public final static byte auctionDelayed = 7;

	// 邮件类型
	private byte mailType;

	// 是否系统
	// private boolean isSystem;

	public void writeToBufferList(IoBuffer buffer) {
		IoBufferUtil.putString(buffer, mailId);
		int len = Content.length();
		if (len > 10) {
			len = 10;
		}
		// String shortContent = Content.substring(0, len);
		IoBufferUtil.putString(buffer, title);
		if (isRead) {
			buffer.put((byte) 1);
		} else {
			buffer.put((byte) 0);
		}
		buffer.putLong(time);
		buffer.put(mailType);
		if (gold > 0 || bindGold > 0 || coin > 0 || item.length() > 1 || getItemInstance() != null) {
			buffer.put((byte) 1);
		} else {
			buffer.put((byte) 0);
		}
	}

	private IoBuffer putString(IoBuffer out, String s) {
		return IoBufferUtil.putString(out, s);
	}

	public void writeToBuffer(IoBuffer buffer) {
		// IoBufferUtil.putString(buffer, playerId);
		IoBufferUtil.putString(buffer, mailId);
		// IoBufferUtil.putString(buffer, sendername);
		// IoBufferUtil.putString(buffer, title);
		IoBufferUtil.putString(buffer, Content);
		buffer.putInt(gold);
		buffer.putInt(bindGold);
		buffer.putInt(coin);
		IoBufferUtil.putString(buffer, item);

		if (itemInstance != null) {
			buffer.putInt(1);

			putString(buffer, itemInstance.getId());
			ItemRef itemRef = itemInstance.getItemRef();
			String refId = itemRef.getId();
			putString(buffer, refId);

			PropertyDictionary dictionary = null;
			if (!itemInstance.isNonPropertyItem())
				dictionary = itemInstance.getProperty();
			else {
				dictionary = new PropertyDictionary();
				dictionary.copyFrom(itemInstance.getItemRef().getEffectProperty());
			}

			int number = itemInstance.getNumber();
			byte bindStatus = itemInstance.getBindStatus();
			int fightValue = MGPropertyAccesser.getFightValue(itemInstance.getProperty());
			byte strenglevel = MGPropertyAccesser.getStrengtheningLevel(itemInstance.getProperty());
			MGPropertyAccesser.setOrPutNumber(dictionary, number);
			MGPropertyAccesser.setOrPutBindStatus(dictionary, bindStatus);
			MGPropertyAccesser.setOrPutFightValue(dictionary, fightValue);
			MGPropertyAccesser.setOrPutStrengtheningLevel(dictionary, strenglevel);
			// pd的数量
			byte pdCount = 1;
			EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(itemInstance);
			PropertyDictionary xiLianPd = equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
			if (xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
				pdCount++;
			}
			buffer.put(pdCount);
			buffer.put(ItemCode.TOTAL_PD_UPDATE);
			byte[] byteArray = dictionary.toByteArray();
			buffer.putShort((short) byteArray.length);
			buffer.put(byteArray);

			if (xiLianPd != null && xiLianPd.getDictionary().size() != 0) {
				buffer.put(ItemCode.WASH_PD_UPDATE);
				byte[] xiLianByteArray = xiLianPd.toByteArray();
				buffer.putShort((short) xiLianByteArray.length);
				buffer.put(xiLianByteArray);
			}
		} else {
			buffer.putInt(0);
		}

		// if (isRead) {
		// buffer.put((byte) 1);
		// } else {
		// buffer.put((byte) 0);
		// }
		// IoBufferUtil.putString(buffer, relateMailId);
		// buffer.putLong(time);
		// buffer.put(mailType);
		// if (isDelete) {
		// buffer.put((byte) 1);
		// } else {
		// buffer.put((byte) 0);
		// }
		// if (isSystem) {
		// buffer.put((byte) 1);
		// } else {
		// buffer.put((byte) 0);
		// }

	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	// public String getSendername() {
	// return sendername;
	// }
	//
	// public void setSendername(String sendername) {
	// this.sendername = sendername;
	// }

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getBindGold() {
		return bindGold;
	}

	public void setBindGold(int bindGold) {
		this.bindGold = bindGold;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getRelateMailId() {
		return relateMailId;
	}

	public void setRelateMailId(String relateMailId) {
		this.relateMailId = relateMailId;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public byte getMailType() {
		return mailType;
	}

	public void setMailType(byte mailType) {
		this.mailType = mailType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Item getItemInstance() {
		return itemInstance;
	}

	public void setItemInstance(Item itemInstance) {
		this.itemInstance = itemInstance;
	}

	// public boolean isSystem() {
	// return isSystem;
	// }
	//
	// public void setSystem(boolean isSystem) {
	// this.isSystem = isSystem;
	// }
}
