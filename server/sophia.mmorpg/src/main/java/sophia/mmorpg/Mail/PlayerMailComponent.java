package sophia.mmorpg.Mail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.event.C2G_GM_Mail_Send;
import sophia.mmorpg.Mail.event.C2G_Mail_Content;
import sophia.mmorpg.Mail.event.C2G_Mail_List;
import sophia.mmorpg.Mail.event.C2G_Mail_Pickup;
import sophia.mmorpg.Mail.event.C2G_Mail_Pickup_LeftTime;
import sophia.mmorpg.Mail.event.C2G_Mail_Read;
import sophia.mmorpg.Mail.event.G2C_Mail_List;
import sophia.mmorpg.Mail.event.G2C_Mail_Pickup_LeftTime;
import sophia.mmorpg.Mail.event.MailEventDefines;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.utils.HttpConnection;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-11 下午5:28:39
 * @version 1.0
 */
public class PlayerMailComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerMailComponent.class.getName());
	
	public static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();
	public static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();
	
	private long lastSendTime = 0;
	
	public PlayerMailComponent() {
	}

	@Override
	public void ready() {
		addActionEventListener(MailEventDefines.C2G_Mail_List);
		addActionEventListener(MailEventDefines.C2G_Mail_Read);
		addActionEventListener(MailEventDefines.C2G_Mail_Pickup);
		addActionEventListener(MailEventDefines.C2G_GM_Mail_Send);
		addActionEventListener(MailEventDefines.C2G_Mail_Content);
		addActionEventListener(MailEventDefines.C2G_Mail_Pickup_LeftTime);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		addInterGameEventListener(LeaveWorld_GE_ID);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(MailEventDefines.C2G_Mail_List);
		removeActionEventListener(MailEventDefines.C2G_Mail_Read);
		removeActionEventListener(MailEventDefines.C2G_Mail_Pickup);
		removeActionEventListener(MailEventDefines.C2G_GM_Mail_Send);
		removeActionEventListener(MailEventDefines.C2G_Mail_Content);
		removeActionEventListener(MailEventDefines.C2G_Mail_Pickup_LeftTime);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		removeInterGameEventListener(LeaveWorld_GE_ID);
		super.suspend();
	}
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		Player player = getConcreteParent();
		if (event.isId(EnterWorld_SceneReady_ID)) {		
			createGMMail(player);
		}
		else if(event.isId(LeaveWorld_GE_ID)){
			MailMgr.removeMailList(player.getId());
		}
		super.handleGameEvent(event);
	}
	
	public void  createGMMail(Player player){
		List<GmMail> gmMaillist = GmMailMgr.getInstance().getGmMaillist();
		if (gmMaillist == null || gmMaillist.size()==0)
			return;
		List<Mail> mails = MailMgr.getMailsByPlayerId(player.getId());
		// 等级判断
		int level = player.getLevel();
		long currTime = System.currentTimeMillis();
		long regTime = player.getBirthDay();
		// 检查有没gm邮件
		for (GmMail gmMail : gmMaillist) {
			if(checkGmMail(level,regTime,gmMail,mails,currTime)){
				MailMgr.sendMailById(player.getId(),gmMail.getTitle(), gmMail.getContent(), Mail.GMReplyCustomType, gmMail.getItem(), gmMail.getGold(), gmMail.getBindGold(), gmMail.getCoin(),gmMail.getMailId());		
			}
		}
	}
	
	private boolean checkGmMail(int level,long regTime,GmMail gmMail,List<Mail> mails,long currTime) {
		
		if (gmMail.getPlayerMinLevel() != 0 && level < gmMail.getPlayerMinLevel())
			return false;

		if (gmMail.getPlayerMaxLevel() != 0 && level > gmMail.getPlayerMaxLevel())
			return false;
		
		if (gmMail.getEffectBeginTime()!=0 && (currTime < gmMail.getEffectBeginTime())) {
			return false;
		}

		long endtime = gmMail.getEffectEndTime();
		if (endtime != 0 && (currTime > endtime || regTime > endtime)) {
			return false;
		}

		for (Mail mail : mails) {
			if (StringUtils.equals(mail.getRelateMailId(),gmMail.getMailId()))
				 return false;
		}
		
		return true;
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();

		switch (actionEventId) {
		case MailEventDefines.C2G_Mail_List:
			handle_Mail_List((C2G_Mail_List) event, actionEventId, identity);
			break;
		case MailEventDefines.C2G_Mail_Read:
			handle_Mail_Read((C2G_Mail_Read) event, actionEventId, identity);
			break;
		case MailEventDefines.C2G_Mail_Pickup:
			handle_Mail_Pickup((C2G_Mail_Pickup) event, actionEventId, identity);
			break;
		case MailEventDefines.C2G_GM_Mail_Send:
			handle_GM_Mail_Send((C2G_GM_Mail_Send) event, actionEventId, identity);
			break;
		case MailEventDefines.C2G_Mail_Content:
			handle_Mail_Content((C2G_Mail_Content) event, actionEventId, identity);
			break;	
		case MailEventDefines.C2G_Mail_Pickup_LeftTime:
			handle_Mail_Pickup_LeftTime((C2G_Mail_Pickup_LeftTime) event, actionEventId, identity);
			break;
		default:
			break;
		}

		super.handleActionEvent(event);
	}


	private void handle_Mail_List(C2G_Mail_List event, short actionEventId, Identity identity) {
		
		List<Mail> mails = MailMgr.getMailsByPlayerId(getConcreteParent().getId());
		G2C_Mail_List g2c_Mail_List = MessageFactory.getConcreteMessage(MailEventDefines.G2C_Mail_List);
		g2c_Mail_List.setMails(mails);
		GameRoot.sendMessage(event.getIdentity(), g2c_Mail_List);
	}

	private void handle_Mail_Read(C2G_Mail_Read event, short actionEventId, Identity identity) {
		String mailId = event.getMailId();
		Mail mail = MailMgr.getMail(getConcreteParent().getId(),mailId);
		if(mail!=null){
			if(mail.getGold()!=0 || mail.getBindGold() !=0 || mail.getCoin()!=0 ||(mail.getItem()!=null && !mail.getItem().equals(""))|| mail.getItemInstance()!=null){
				logger.info("read mail error mailid="+mail);
			}else{
				MailMgr.setRead(concreteParent,mailId);
			}
		}		
	}

	private void handle_Mail_Pickup(C2G_Mail_Pickup event, short actionEventId, Identity identity) {
		RuntimeResult runtimeResult = MailMgr.pickup(concreteParent, event.getMailId());
		if (runtimeResult.isOK()) {
			ResultEvent.sendResult(identity, event.getActionEventId(), MMORPGSuccessCode.CODE_SUCCESS);
		} else {
			ResultEvent.sendResult(identity, event.getActionEventId(), runtimeResult.getApplicationCode());
		}
	}
	
	private void handle_Mail_Pickup_LeftTime(C2G_Mail_Pickup_LeftTime event, short actionEventId, Identity identity){
		String mailId = event.getMailId();
		Mail mail = MailMgr.getMail(getConcreteParent().getId(),mailId);
		if(mail!=null && mail.getMailType() == Mail.auctionDelayed){
			long leftTime = 0;
			long passTime = System.currentTimeMillis() - mail.getTime();
			if(passTime < 60*60*1000){
				leftTime = 	60*60*1000 - passTime;
			}
			
			G2C_Mail_Pickup_LeftTime res = MessageFactory.getConcreteMessage(MailEventDefines.G2C_Mail_Pickup_LeftTime);
			res.setMailId(mailId);
			res.setLeftTime(leftTime);
			GameRoot.sendMessage(event.getIdentity(), res);
		}else{
			ResultEvent.sendResult(identity, event.getActionEventId(),  MMORPGErrorCode.CODE_MAIL_NOT_EXIST);
		}
	}
	
	private void handle_Mail_Content(C2G_Mail_Content event, short actionEventId, Identity identity) {
		RuntimeResult runtimeResult = MailMgr.getMailContent(concreteParent, event.getMailId());
		if (!runtimeResult.isOK()) {
			ResultEvent.sendResult(identity, event.getActionEventId(), runtimeResult.getApplicationCode());
		}		
	}
	
	private void handle_GM_Mail_Send(C2G_GM_Mail_Send event, short actionEventId, Identity identity){
		
		Player player = getConcreteParent();
		if(player == null)
			return;
		
		long curTime = System.currentTimeMillis();
		if(curTime - lastSendTime < 3 * 60 * 1000) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MMORPGErrorCode.CODE_SEND_CUSTOMER_MAIL_FAST);
			return;
		}
		
		lastSendTime = curTime;
		
		if(event.getContent()==null || event.getContent().length()>300){
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MMORPGErrorCode.CODE_SEND_CUSTOMER_MAIL_CONENT_ERROR);
			return;
		}
		
		Mail customerMail = MailMgr.newCustomerMail(player.getId(), event.getContent());
		
		String posturl = event.getUrl();
		List<NameValuePair> _params = new ArrayList<NameValuePair>();


		_params.add(new BasicNameValuePair("mailId", customerMail.getMailId()));
		try {
			_params.add(new BasicNameValuePair("content", URLEncoder.encode(event.getContent(), "utf-8")));
			_params.add(new BasicNameValuePair("sendName", URLEncoder.encode(player.getName(), "utf-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("发送客户邮件编码错误", e);
		}
		_params.add(new BasicNameValuePair("action", "mail"));
		_params.add(new BasicNameValuePair("type", "" + event.getType()));
		_params.add(new BasicNameValuePair("serverId", String.valueOf(MMORPGContext.getServerId())));
//		_params.add(new BasicNameValuePair("qdCode_1", "" + player.getIdentity().getQdCode1()));
//		_params.add(new BasicNameValuePair("qdCode_2", "" + player.getIdentity().getQdCode2()));

		MailHttpListenerImpl httpListener = new MailHttpListenerImpl();
		customerMail.setRead(true);
		httpListener.setOwner(player);	
		httpListener.setMail(customerMail);
		httpListener.setActionEventId(actionEventId);

		HttpConnection httpConnection = HttpConnection.create(posturl, _params, httpListener);
		httpConnection.exec(false);
	}

}
