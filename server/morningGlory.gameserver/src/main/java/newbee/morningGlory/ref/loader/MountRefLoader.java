package newbee.morningGlory.ref.loader;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.mount.MountRef;

import com.google.gson.JsonObject;

public class MountRefLoader extends AbstractGameRefObjectLoader<MountRef> {

	private static final Logger logger = Logger.getLogger(MountRefLoader.class);

	public MountRefLoader() {
		super(RefKey.mount);
	}

	protected MountRef create() {
		return new MountRef();
	}

	protected void fillNonPropertyDictionary(MountRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("loading MountRef refId: " + ref.getId());
		}
		super.fillNonPropertyDictionary(ref, refData);
		// effect
		if (logger.isDebugEnabled()) {
			logger.debug("effectData: " + refData.get("effectData"));
		}
		JsonObject effectData = refData.get("effectData").getAsJsonObject();
		PropertyDictionary effect = new PropertyDictionary();
		fillPropertyDictionary(effect, effectData);
		ref.setEffect(effect);

		// tmpEffect
		if (logger.isDebugEnabled()) {
			logger.debug("tmpEffectData: " + refData.get("tmpEffectData"));
		}
		JsonObject tmpEffectData = refData.get("tmpEffectData").getAsJsonObject();
		PropertyDictionary tmpEffect = new PropertyDictionary();
		fillPropertyDictionary(tmpEffect, tmpEffectData);
		ref.setTmpEffect(tmpEffect);
	}
}
