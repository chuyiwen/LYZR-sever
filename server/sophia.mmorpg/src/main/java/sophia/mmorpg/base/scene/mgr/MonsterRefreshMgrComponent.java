package sophia.mmorpg.base.scene.mgr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monsterRefresh.MonsterGroupRefData;
import sophia.mmorpg.monsterRefresh.RefreshMonsterClosures;
import sophia.mmorpg.monsterRefresh.RefreshMonsterGroupDelay;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRefData;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRuntime;
import sophia.mmorpg.monsterRefresh.RefreshSceneMonsterMgr;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.common.base.Preconditions;

public class MonsterRefreshMgrComponent extends ConcreteComponent<GameScene> {
	private static final Logger logger = Logger.getLogger(MonsterRefreshMgrComponent.class);

	private RefreshSceneMonsterMgr refreshSceneMonsterMgr;
	private RefreshMonsterRuntime refreshMonsterRuntime;

	private GameScene crtGameScene;
	private SFTimer timer;

	/** 延迟刷新的怪物组 **/
	private CopyOnWriteArrayList<RefreshMonsterGroupDelay> refreshMonsterGourpDelayList = new CopyOnWriteArrayList<>();

	@Override
	public void ready() {
		crtGameScene = this.getConcreteParent();
		refreshSceneMonsterMgr = new RefreshSceneMonsterMgr(this.getConcreteParent().getRef().getRefreshMonsterRefDatas());
		refreshMonsterRuntime = new RefreshMonsterRuntime(new RefreshMonsterClosures());

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				tick();
			}

			@Override
			public void handleServiceShutdown() {
			}
		});
	}

	@Override
	public void suspend() {
		if (timer != null) {
			timer.cancel();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("timer stopped");
		}
		super.suspend();
	}

	/**
	 * 场景创建监听
	 */
	public void sceneCreatedListener() {
		List<RefreshMonsterRefData> monsterGroupListOnSceneCreated = refreshSceneMonsterMgr.getMonsterGroupListOnSceneCreated();
		if (monsterGroupListOnSceneCreated == null || monsterGroupListOnSceneCreated.size() == 0) {
			return;
		}
		for (RefreshMonsterRefData refreshMonsterRefData : monsterGroupListOnSceneCreated) {
			refreshMonsterRuntime.createScene(this.getConcreteParent(), refreshMonsterRefData);
			refreshMonsterGroupDelay(refreshMonsterRefData);
		}
	}

	/**
	 * 场景怪物刷新时间
	 */
	public void sceneTimeInRange(String unionName, int killMonsterTime) {
		List<RefreshMonsterRefData> monsterGroupListInSceneTimeRange = refreshSceneMonsterMgr.getMonsterGroupListInSceneTimeRange();
		if (monsterGroupListInSceneTimeRange == null || monsterGroupListInSceneTimeRange.size() == 0) {
			return;
		}
		for (RefreshMonsterRefData refreshMonsterRefData : monsterGroupListInSceneTimeRange) {
			refreshMonsterRuntime.createScene(this.getConcreteParent(), refreshMonsterRefData);
			refreshMonsterGroupDelay(refreshMonsterRefData, unionName, killMonsterTime);
		}
	}

	/**
	 * 怪物出现监听
	 * 
	 * @param ariseMonsterRefId
	 * @param ariseCount
	 */
	public void monsterAriseListener(String ariseMonsterRefId, int ariseCount) {
		List<RefreshMonsterRefData> monsterGroupListOnMonsterArise = refreshSceneMonsterMgr.listenerOnPreMonsterArise(ariseMonsterRefId, ariseCount);
		if (monsterGroupListOnMonsterArise == null || monsterGroupListOnMonsterArise.size() == 0) {
			return;
		}
		for (RefreshMonsterRefData refreshMonsterRefData : monsterGroupListOnMonsterArise) {
			refreshMonsterRuntime.monsterGroupAppear(this.getConcreteParent(), refreshMonsterRefData);
			refreshMonsterGroupDelay(refreshMonsterRefData);
		}
	}

	/**
	 * 怪物死亡监听
	 * 
	 * @param deadMonsterRefId
	 * @param deadCount
	 */
	public void monsterDeadListener(String deadMonsterRefId, int deadCount) {
		List<RefreshMonsterRefData> monsterGroupListOnMonsterDead = refreshSceneMonsterMgr.listenerOnPreMonsterDead(deadMonsterRefId, deadCount);
		if (monsterGroupListOnMonsterDead == null || monsterGroupListOnMonsterDead.size() == 0) {
			return;
		}
		for (RefreshMonsterRefData refreshMonsterRefData : monsterGroupListOnMonsterDead) {
			refreshMonsterRuntime.monsterGroupDead(this.getConcreteParent(), refreshMonsterRefData);
			refreshMonsterGroupDelay(refreshMonsterRefData);
		}
	}

	public void tick() {
		if (refreshMonsterGourpDelayList.size() == 0) {
			return;
		}
		long now = System.currentTimeMillis() / 1000;
		for (RefreshMonsterGroupDelay refreshMonsterGroup : refreshMonsterGourpDelayList) {
			RefreshMonsterRefData refreshMonsterRefData = refreshMonsterGroup.getRefreshMonsterRefData();
			if (refreshMonsterRefData == null || refreshMonsterRefData.getMonsterGroup() == null) {
				refreshMonsterGourpDelayList.remove(refreshMonsterGroup);
				continue;
			}
			int delaySec = refreshMonsterRefData.getMonsterGroup().getDelaySec();
			long refreshMonsterGroupCreateTime = refreshMonsterGroup.getTime();
			if (refreshMonsterGroupCreateTime <= 0) {
				refreshMonsterGourpDelayList.remove(refreshMonsterGroup);
				continue;
			}
			if (now >= refreshMonsterGroupCreateTime + delaySec) {
				refreshMonsterGroupImmediately(refreshMonsterRefData);
				refreshMonsterGourpDelayList.remove(refreshMonsterGroup);
			}
		}
	}

	// ===================================================================================================================================================
	private void refreshMonsterGroupDelay(RefreshMonsterRefData refreshMonsterRefData, String unionName, int killMonsterTime) {
		MonsterGroupRefData monsterGroup = refreshMonsterRefData.getMonsterGroup();
		Preconditions.checkNotNull(monsterGroup);
		if (monsterGroup.getDelaySec() <= 0) {
			refreshMonsterGroupImmediately(refreshMonsterRefData, unionName, killMonsterTime);
			return;
		}
		RefreshMonsterGroupDelay refreshMonsterGroup = new RefreshMonsterGroupDelay(refreshMonsterRefData);
		this.refreshMonsterGourpDelayList.add(refreshMonsterGroup);
	}

	private void refreshMonsterGroupDelay(RefreshMonsterRefData refreshMonsterRefData) {
		refreshMonsterGroupDelay(refreshMonsterRefData, "", 0);
	}

	private void refreshMonsterGroupImmediately(RefreshMonsterRefData refreshMonsterRefData, String unionName, int killMonsterTime) {
		MonsterGroupRefData monsterGroup = refreshMonsterRefData.getMonsterGroup();
		Preconditions.checkNotNull(monsterGroup);
		int monsterNum = monsterGroup.getNumber();
		String monsterRefId = monsterGroup.getMonsterRefId();
		List<Monster> monsterList = new ArrayList<>();
		for (int i = 0; i < monsterNum; i++) {
			Monster monster = crtGameScene.getMonsterMgrComponent().createMonster(monsterRefId);
			if (!StringUtils.isEmpty(unionName)) {
				MGPropertyAccesser.setOrPutUnionName(monster.getProperty(), unionName);
			}
			if (killMonsterTime > 0) {
				monsterLevelUpByKillTime(monster, killMonsterTime);
			}
			monsterList.add(monster);
		}
		crtGameScene.getMonsterMgrComponent().enterWorld(monsterList, crtGameScene, refreshMonsterRefData.getPosition(monsterNum));
	}

	private void refreshMonsterGroupImmediately(RefreshMonsterRefData refreshMonsterRefData) {
		refreshMonsterGroupImmediately(refreshMonsterRefData, "", 0);
	}

	/**
	 * 根据夏蔚需求：每次攻城怪物复活，增加原来属性值为，血量：10%，攻击：5% 计算公式：HP=HP*(1+n*5%) APK/MPK/TAO =
	 * APK/MPK/TAO * (1+n*10%)
	 * 
	 * @param gameScene
	 * @param monsterKillTime
	 */
	private void monsterLevelUpByKillTime(Monster monster, int monsterKillTime) {
		int addHPRate = 10;
		int addATKRate = 5;
		
		PropertyDictionary monsterPd = monster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
		int maxHP = MGPropertyAccesser.getMaxHP(monsterPd) * monsterKillTime * addHPRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxHP_Id, maxHP);

		int minPAtk = MGPropertyAccesser.getMinPAtk(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinPAtk_Id, minPAtk);

		int maxPAtk = MGPropertyAccesser.getMaxPAtk(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxPAtk_Id, maxPAtk);

		int minMAtk = MGPropertyAccesser.getMinMAtk(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinMAtk_Id, minMAtk);

		int maxMAtk = MGPropertyAccesser.getMaxMAtk(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxMAtk_Id, maxMAtk);

		int minTao = MGPropertyAccesser.getMinTao(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinTao_Id, minTao);

		int maxTao = MGPropertyAccesser.getMaxTao(monsterPd) * monsterKillTime * addATKRate / 100;
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxTao_Id, maxTao);

		monster.modifyHP(monster, maxHP);
	}
}
