package sophia.mmorpg.Mail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.event.G2C_Mail_Add;
import sophia.mmorpg.Mail.event.G2C_Mail_Content;
import sophia.mmorpg.Mail.event.MailEventDefines;
import sophia.mmorpg.Mail.persistence.MailDao;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-10 下午3:28:33
 * @version 1.0
 */
public final class MailMgr {
	private static final Logger logger = Logger.getLogger(MailMgr.class.getName());

	private static final int MaxMailNum = 200;

	private static ConcurrentHashMap<String, List<Mail>> mailMap = new ConcurrentHashMap<String, List<Mail>>();

	private static Mail newMail(String playerId, String title, String content, byte mailType, String item, Item itemIns, int gold, int bindGold, int coin, String relateMailId) {
		Mail mail = new Mail();
		mail.setPlayerId(playerId);
		mail.setContent(content);
		mail.setMailId(UUID.randomUUID().toString());
		mail.setMailType(mailType);
		mail.setCoin(coin);
		mail.setGold(gold);
		mail.setBindGold(bindGold);

		if (title != null)
			mail.setTitle(title);

		if (relateMailId != null)
			mail.setRelateMailId(relateMailId);

		if (itemIns != null) {
			mail.setItemInstance(itemIns);

		}

		if (checkJson(item) != null)
			mail.setItem(item);

		return mail;
	}

	public static Mail newCustomerMail(String playerId, String content) {
		return newMail(playerId, null, content, Mail.CustomToGMType, null, null, 0, 0, 0, null);
	}

	public static GmMail newGmMail(String title, String content, String item, int gold, int bindGold, int coin, int playerMinLevel, int playerMaxLevel, long effectBeginTime,
			long effectEndTime) {
		GmMail gMail = new GmMail();
		gMail.setMailId(UUID.randomUUID().toString());
		gMail.setTitle(title);
		gMail.setContent(content);
		gMail.setCoin(coin);
		gMail.setBindGold(bindGold);
		gMail.setGold(gold);

		if (checkJson(item) != null)
			gMail.setItem(item);

		gMail.setPlayerMinLevel(playerMinLevel);
		gMail.setPlayerMaxLevel(playerMaxLevel);
		gMail.setEffectBeginTime(effectBeginTime);
		gMail.setEffectEndTime(effectEndTime);

		return gMail;

	}

	private static boolean sendMail(Mail newMail) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(newMail.getPlayerId());
		boolean bsuc = MailDao.getInstance().insertMail(newMail);
		if (bsuc && player != null) {
			addMail(player.getId(), newMail);
			G2C_Mail_Add g2c_Mail_Add = MessageFactory.getConcreteMessage(MailEventDefines.G2C_Mail_Add);
			g2c_Mail_Add.setMail(newMail);
			GameRoot.sendMessage(player.getIdentity(), g2c_Mail_Add);

			int count = getNoReadMailCount(player.getId());

			if (count >= MailMgr.MaxMailNum) {
				ResultEvent.sendResult(player.getIdentity(), g2c_Mail_Add.getActionEventId(), MMORPGErrorCode.CODE_MAIL_NOREAD_FULL);
			} else if (count >= MailMgr.MaxMailNum / 2) {
				ResultEvent.sendResult(player.getIdentity(), g2c_Mail_Add.getActionEventId(), MMORPGErrorCode.CODE_MAIL_NOREAD_HALF_FULL);
			}
		}

		return bsuc;
	}

	public static boolean sendMailById(String playerId, String content, byte mailType) {//
		return sendMailById(playerId, content, mailType, "", 0, 0, 0);
	}

	public static boolean sendMailById(String playerId, String content, byte mailType, String item, int gold, int bindGold, int coin) {
		// TODO 根据名字发邮件
		// String playerId = playerId;
		Mail newMail = newMail(playerId, null, content, mailType, item, null, gold, bindGold, coin, null);
		return sendMail(newMail);
	}

	// public static boolean sendMailById(String playerId, String content,
	// List<Item> itemList, byte mailType) {//
	// Mail newMail = newMail(playerId, null, content, mailType, null, itemList,
	// 0, 0, 0, null);
	// return sendMail(newMail);
	// }

	public static boolean sendMailById(String playerId, String title, String content, Item item, byte mailType) {//
		if (item == null)
			return false;

		Mail newMail = newMail(playerId, title, content, mailType, null, item, 0, 0, 0, null);
		return sendMail(newMail);
	}

	// 为了不影响以前的，重写一个
	public static boolean sendMailById(String playerId, String title, String content, byte mailType, String item, int gold, int bindGold, int coin, String relateMailId) {
		Mail newMail = newMail(playerId, title, content, mailType, item, null, gold, bindGold, coin, relateMailId);
		return sendMail(newMail);
	}

	public static List<Mail> getMailsByPlayerId(String playerId) {
		List<Mail> mailLists = getMailList(playerId);
		if (mailLists == null) {
			mailLists = MailDao.getInstance().getMailsByPlayerId(playerId);

			if (mailLists != null && mailLists.size() > 0)
				addMailList(playerId, mailLists);
		}

		return mailLists;
	}

	public static Item getItemByPlayerItemId(String playerId, String itemId) {
		List<Mail> mailLists = getMailList(playerId);
		for (Mail mail : mailLists) {
			Item item = mail.getItemInstance();
			if (item != null && StringUtils.equals(itemId, item.getId())) {
				return item;
			}
		}

		return null;
	}

	/**
	 * 取得邮件类型为 4或者5的按时间排序的前1W条邮件，找出对应itemId并且邮件时间最大的邮件
	 * 
	 * @param itemId
	 * @return
	 */
	public static Mail getMailByItemId(String itemId) {
		List<Mail> mails = MailDao.getInstance().selectCancelOrNormalAuctionMail();
		Mail maxTimeMail = null;
		for (Mail mail : mails) {
			Item item = mail.getItemInstance();
			if (item == null) {
				continue;
			}
			if (!StringUtils.equals(itemId, item.getId())) {
				continue;
			}

			if (maxTimeMail == null) {
				maxTimeMail = mail;
				continue;
			}

			if (maxTimeMail.getTime() < mail.getTime()) {
				maxTimeMail = mail;
			}

		}

		return maxTimeMail;
	}

	public static boolean setRead(Player player, String mailId) {
		if (MailDao.getInstance().readMail(mailId)) {
			setMailRead(player.getId(), mailId);
			return true;
		}
		return false;
	}

	public static RuntimeResult pickup(Player player, String mailId) {
		RuntimeResult runtimeResult = RuntimeResult.OK();
		// Mail mail = MailDao.getInstance().getMail(mailId);
		Mail mail = getMail(player.getId(), mailId);
		if (mail == null || !StringUtils.equals(player.getId(), mail.getPlayerId())) {
			logger.info("mail can not pick up  mailid=" + mailId + "  playerid=" + player.getId());
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_MAIL_NOT_EXIST);
		}
		// if (StringUtils.isEmpty(mail.getItem())) {
		// return
		// RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_MAIL_NOT_ITEM);
		// }
		if (mail.isRead()) {
			logger.info("mail is already read mailId=" + mailId + "  playerid=" + player.getId());
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_MAIL_IS_READ);
		}

		if (mail.getMailType() <= Mail.auctionDelayed && mail.getMailType() >= Mail.auctionNormal) {
			long now = System.currentTimeMillis();

			if (mail.getMailType() == Mail.auctionDelayed && now - mail.getTime() < 60 * 60 * 1000) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_MAIL_FETCH_NO_ENOUGHT_TIME);
			}

			if (mail.getItemInstance() != null) {
				runtimeResult = ItemFacade.addItems(player, mail.getItemInstance(), ItemOptSource.Mail);
				if (runtimeResult.isOK()) {
					pickupMoney(player, mail);
				}
			} else {
				pickupMoney(player, mail);
			}

		} else {
			List<ItemPair> itemPairs = checkJson(mail.getItem());
			runtimeResult = ItemFacade.addItem(player, itemPairs, ItemOptSource.Mail);
			if (runtimeResult.isOK()) {
				pickupMoney(player, mail);
				return runtimeResult;
			}
		}
		return runtimeResult;
	}

	private static void pickupMoney(Player player, Mail mail) {

		if (mail.getGold() != 0 || mail.getBindGold() != 0 || mail.getCoin() != 0) {
			PlayerMoneyComponent playerMoneyComponent = player.getPlayerMoneyComponent();
			playerMoneyComponent.addUnbindGold(mail.getGold(), ItemOptSource.Mail);
			playerMoneyComponent.addBindGold(mail.getBindGold(), ItemOptSource.Mail);
			playerMoneyComponent.addGold(mail.getCoin(), ItemOptSource.Mail);
		}

		setMailRead(player.getId(), mail.getMailId());
		MailDao.getInstance().readMail(mail.getMailId());
	}

	public static RuntimeResult getMailContent(Player player, String mailId) {
		RuntimeResult runtimeResult = RuntimeResult.OK();
		// Mail mail = MailDao.getInstance().getMail(mailId);
		Mail mail = getMail(player.getId(), mailId);
		if (mail == null || !StringUtils.equals(player.getId(), mail.getPlayerId())) {
			logger.info("getMailContent is error  mailid=" + mailId + "  playerid=" + player.getId());
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_MAIL_NOT_EXIST);
		}

		G2C_Mail_Content g2c_Mail_Content = MessageFactory.getConcreteMessage(MailEventDefines.G2C_Mail_Content);
		g2c_Mail_Content.setMail(mail);
		GameRoot.sendMessage(player.getIdentity(), g2c_Mail_Content);

		return runtimeResult;

	}

	public static List<ItemPair> checkJson(String item) {

		if (item == null || item.equals("[]"))
			return null;

		Gson gson = new Gson();
		Type listType = new TypeToken<List<ItemPair>>() {
		}.getType();
		List<ItemPair> itemPairs = null;
		try {
			itemPairs = gson.fromJson(item, listType);
		} catch (Exception e) {
			logger.error("非法的json=" + item);
		}

		return itemPairs;
	}

	public static void setMailRead(String playerId, String mailId) {
		Mail mail = getMail(playerId, mailId);
		if (mail != null) {
			mail.setRead(true);
		}
	}

	public static Mail getMail(String playerId, String mailId) {
		List<Mail> mailLists = getMailList(playerId);
		if (mailLists != null) {
			for (Mail mail : mailLists) {
				if (mail.getMailId().equals(mailId))
					return mail;
			}
		}
		return null;

	}

	private static Comparator<Mail> mailComparator = new Comparator<Mail>() {
		@Override
		public int compare(Mail o1, Mail o2) {
			return (int) (o2.getTime() - o1.getTime());
		}
	};

	public static void addMail(String playerId, Mail mail) {
		List<Mail> mailLists = getMailList(playerId);
		if (mailLists != null) {
			mailLists.add(mail);
			// 按时间排序
			Collections.sort(mailLists, mailComparator);

			int size = mailLists.size();
			if (size > MailMgr.MaxMailNum) {
				mailLists.remove(size - 1);
			}
		} else {
			mailLists = new ArrayList<Mail>();
			mailLists.add(mail);
			addMailList(playerId, mailLists);
		}
	}

	public static List<Mail> getMailList(String playerId) {
		if (playerId == null)
			return null;

		List<Mail> mailList = mailMap.get(playerId);
		return mailList;
	}

	/*
	 * 
	 * 未读邮件的数目
	 */
	public static int getNoReadMailCount(String playerId) {
		List<Mail> mails = getMailList(playerId);
		if (mails == null)
			return 0;

		int count = 0;
		for (Mail mail : mails) {
			if (!mail.isRead()) {
				count++;
			}
		}

		return count;

	}

	public static void addMailList(String playerId, List<Mail> mailList) {
		if (getMailList(playerId) == null)
			mailMap.putIfAbsent(playerId, mailList);
	}

	public static void removeMailList(String playerId) {
		if (getMailList(playerId) != null)
			mailMap.remove(playerId);
	}

}