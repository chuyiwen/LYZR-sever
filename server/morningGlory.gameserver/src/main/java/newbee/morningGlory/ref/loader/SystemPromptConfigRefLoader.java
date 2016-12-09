package newbee.morningGlory.ref.loader;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.chat.SystemPromptConfigRef;

import com.google.gson.JsonObject;

public class SystemPromptConfigRefLoader extends
		AbstractGameRefObjectLoader<SystemPromptConfigRef> {

	private static final Logger logger = Logger
			.getLogger(SystemPromptConfigRefLoader.class);

	public SystemPromptConfigRefLoader() {
		super(RefKey.systemPromptConfig);
	}

	protected SystemPromptConfigRef create() {
		return new SystemPromptConfigRef();
	}

	protected void fillNonPropertyDictionary(SystemPromptConfigRef ref,
			JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("loading systemPromptConfigRef refId: " + ref.getId());
		}
		super.fillNonPropertyDictionary(ref, refData);

	}
}
