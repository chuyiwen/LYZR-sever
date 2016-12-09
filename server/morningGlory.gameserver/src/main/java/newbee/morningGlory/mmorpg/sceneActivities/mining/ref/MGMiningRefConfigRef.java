package newbee.morningGlory.mmorpg.sceneActivities.mining.ref;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;
import sophia.game.ref.AbstractGameRefObjectBase;

public final class MGMiningRefConfigRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -3068694445799294323L;
	// 挖矿次数
	private byte limitCount;
	// 限制进入的等级
	private int level;

	private ComponentRegistry componentRegistry = new ComponentRegistryImpl();

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public byte getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(byte limitCount) {
		this.limitCount = limitCount;
	}

	public ComponentRegistry getComponentRegistry() {
		return componentRegistry;
	}

	public void setComponentRegistry(ComponentRegistry componentRegistry) {
		this.componentRegistry = componentRegistry;
	}
}
