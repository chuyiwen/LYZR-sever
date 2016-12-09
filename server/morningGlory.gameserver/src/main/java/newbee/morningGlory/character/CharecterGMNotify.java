/**
 * 
 */
package newbee.morningGlory.character;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.http.util.UrlBase64;
import newbee.morningGlory.mmorpg.operatActivities.utils.DefaultLoadCallBack;
import newbee.morningGlory.mmorpg.operatActivities.utils.HttpConnection;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.PropertiesWrapper;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.util.concurrent.AbstractIdleService;

/**
 * @author Administrator
 * 
 */
public class CharecterGMNotify extends AbstractIdleService {
	private static Logger logger = Logger.getLogger(CharecterGMNotify.class);

	private static final String LoadUrl = "newbee.morningGlory.http.HttpService.HttpWebUrl";

	private static final long insertIntervalTime = 5 * 1000;

	private static CharecterGMNotify instance = new CharecterGMNotify();

	private Set<Player> sendImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> sendImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	private Set<Player> createImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());
	private Set<Player> createImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<Player, Boolean>());

	private ScheduledFuture<?> scheduledFuture;

	private CharecterGMNotify() {
	}

	public static CharecterGMNotify getInstance() {
		return instance;
	}

	public void sendLogin(Player player) {
		sendImmediateSetPrimary.add(player);
	}

	public void sendCreate(Player player) {
		createImmediateSetPrimary.add(player);
	}

	private boolean isNeedSendLogin() {
		return sendImmediateSetPrimary.size() > 0;
	}

	private boolean isNeedSendCreate() {
		return createImmediateSetPrimary.size() > 0;
	}

	@Override
	protected void startUp() throws Exception {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					if (isNeedSendCreate()) {
						doSendCreate();
					}

					if (isNeedSendLogin()) {
						doSendLogin();
					}

				} catch (Throwable e) {
					logger.error("CharecterGMNotify Exception:" + DebugUtil.printStack(e));
				}
			}
		}, insertIntervalTime, insertIntervalTime, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown() throws Exception {
		scheduledFuture.cancel(false);
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info("waiting for character gm notify service future stop");
			}	
			scheduledFuture.get();	
		} catch (Exception e) {
		}

		if (logger.isInfoEnabled()) {
			logger.info("character gm notify service future was terminated");
		}
	}

	private void doSendLogin() {
		sendImmediateSetSecondary.addAll(sendImmediateSetPrimary);
		sendImmediateSetPrimary.removeAll(sendImmediateSetSecondary);
		for (Player player : sendImmediateSetSecondary) {
			notifyPlayerLogin(player);
		}
		sendImmediateSetSecondary.clear();
	}

	private void doSendCreate() {
		createImmediateSetSecondary.addAll(createImmediateSetPrimary);
		createImmediateSetPrimary.removeAll(createImmediateSetSecondary);
		for (Player player : createImmediateSetSecondary) {
			notifyPlayerCreate(player);
		}
		createImmediateSetSecondary.clear();
	}

	private void notifyPlayerCreate(Player player) {
		Identity identity = player.getIdentity();
		String playerName = player.getName();
		String identityName = identity.getName();
		playerName = UrlBase64.encode(playerName);
		identityName = UrlBase64.encode(identityName);
		long birthday = MGPropertyAccesser.getBirthday(player.getProperty());
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		DefaultLoadCallBack callBack = new DefaultLoadCallBack();
		String url = properties.getProperty(LoadUrl, "") + "?action=create&fid=" + properties.getProperty("newbee.morningGlory.http.HttpService.serverId") + "&playerName="
				+ playerName + "&identityName=" + identityName + "&birthday=" + birthday;
		if (logger.isDebugEnabled()) {
			logger.debug(url);
		}

		HttpConnection httpConnection = HttpConnection.create(url, callBack);
		httpConnection.exec(false);
	}

	private void notifyPlayerLogin(Player player) {
		Identity identity = player.getIdentity();
		String playerName = player.getName();
		String identityName = identity.getName();
		identityName = UrlBase64.encode(identityName);
		playerName = UrlBase64.encode(playerName);
		long lastLoginTime = MGPropertyAccesser.getLastLoginTime(player.getProperty());
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		DefaultLoadCallBack callBack = new DefaultLoadCallBack();
		String url = properties.getProperty(LoadUrl, "") + "?action=login&fid=" + properties.getProperty("newbee.morningGlory.http.HttpService.serverId") + "&playerName="
				+ playerName + "&identityName=" + identityName + "&lastLoginTime=" + lastLoginTime;
		if (logger.isDebugEnabled()) {
			logger.debug(url);
		}

		HttpConnection httpConnection = HttpConnection.create(url, callBack);
		httpConnection.exec(false);
	}
}
