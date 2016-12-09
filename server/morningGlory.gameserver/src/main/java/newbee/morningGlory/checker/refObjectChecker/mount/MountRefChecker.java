package newbee.morningGlory.checker.refObjectChecker.mount;

import newbee.morningGlory.checker.BaseRefChecker;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MountRefChecker extends BaseRefChecker<MountRef> {

	private static final String RIDE_REF_ID_PRE = "ride_";

	@Override
	public void check(GameRefObject gameRefObject) {
		MountRef info = (MountRef) gameRefObject;
		this.checkStartsWithTheString(gameRefObject, info.getId(), MountRefChecker.RIDE_REF_ID_PRE, "refId");
		this.checkMountPropertyData(info);
		this.checkEffectData(info);

		PropertyDictionary tmpEffectPd = info.getTmpEffect();
		this.checkMoreThanOrEqualToTheNumber(gameRefObject, MGPropertyAccesser.getMoveSpeedPer(tmpEffectPd), 0, "moveSpeedPer");
	}

	@Override
	public String getDescription() {
		return "坐骑";
	}

	// ============================================================================================================================
	private void checkMountPropertyData(MountRef ref) {
		PropertyDictionary pd = ref.getProperty();

		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getFightValue(pd), 0, "fightValue");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxExp(pd), 0, "maxExp");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getRideMedicineMaxConsume(pd), 0, "rideMedicineMaxConsume");
		this.checkStartsWithTheStringAndInGameRefObjectManager(ref, MGPropertyAccesser.getRideNextRefId(pd), "rideNextRefId");
		this.checkStartsWithTheStringAndInGameRefObjectManager(ref, MGPropertyAccesser.getRidePreRefId(pd), "ridePreRefId");

	}

	private void checkEffectData(MountRef ref) {
		PropertyDictionary effectPd = ref.getEffect();
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getHP(effectPd), 0, "HP");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMDodgePer(effectPd), 0, "MDodgePer");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMImmunityPer(effectPd), 0, "MImmunityPer");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMP(effectPd), 0, "MP");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getPDodgePer(effectPd), 0, "PDodgePer");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getPImmunityPer(effectPd), 0, "PImmunityPer");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getAtkSpeedPer(effectPd), 0, "atkSpeedPer");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getCrit(effectPd), 0, "crit");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getCritInjure(effectPd), 0, "critInjure");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getDodge(effectPd), 0, "dodge");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getFortune(effectPd), 0, "fortune");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getHit(effectPd), 0, "hit");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getIgnoreMDef(effectPd), 0, "ignoreMDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getIgnorePDef(effectPd), 0, "ignorePDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxMAtk(effectPd), 0, "maxMAtk");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxMDef(effectPd), 0, "maxMDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxPAtk(effectPd), 0, "maxPAtk");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxPDef(effectPd), 0, "maxPDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMaxTao(effectPd), 0, "maxTao");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMinMAtk(effectPd), 0, "minMAtk");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMinMDef(effectPd), 0, "minMDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMinPAtk(effectPd), 0, "minPAtk");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMinPDef(effectPd), 0, "minPDef");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getMinTao(effectPd), 0, "minTao");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getPerHP(effectPd), 0, "perHP");
		this.checkMoreThanOrEqualToTheNumber(ref, MGPropertyAccesser.getPerMP(effectPd), 0, "perMP");
	}

	private void checkStartsWithTheStringAndInGameRefObjectManager(GameRefObject gameRefObject, String rideRefId, String fieldName) {
		if (!StringUtils.isEmpty(rideRefId)) {
			this.checkStartsWithTheString(gameRefObject, rideRefId, MountRefChecker.RIDE_REF_ID_PRE, fieldName);
			this.checkInGameRefObjectManager(gameRefObject, rideRefId, fieldName);
		}
	}

}
