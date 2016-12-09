/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.player.mount;

import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.mount.mountModule.FeedModule;
import sophia.mmorpg.player.mount.mountModule.LevelModule;

public final class MountManager {
	private Mount crtMount;

	// 喂养
	private FeedModule feedModule;
	// 等级
	private LevelModule levelModule;
	private Player owner;

	public MountManager() {
		feedModule = new FeedModule(this);
		levelModule = new LevelModule(this);
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	public Mount getCrtMount() {
		return crtMount;
	}

	public void setCrtMount(Mount crtMount) {
		this.crtMount = crtMount;
	}

	public FeedModule getFeedModule() {
		return feedModule;
	}

	public void setFeedModule(FeedModule feedModule) {
		this.feedModule = feedModule;
	}

	public LevelModule getLevelModule() {
		return levelModule;
	}

	public void setLevelModule(LevelModule levelModule) {
		this.levelModule = levelModule;
	}

	public Player getPlayer() {
		return owner;
	}

	public void rewardExp(int totalExp) {
		getFeedModule().rewardExp(totalExp);
	}



}
