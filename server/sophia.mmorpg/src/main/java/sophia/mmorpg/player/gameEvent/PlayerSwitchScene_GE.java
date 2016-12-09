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
package sophia.mmorpg.player.gameEvent;

import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.player.Player;

public final class PlayerSwitchScene_GE {
	
	private Player player;
	
	private GameScene fromScene;
	
	private int fromX;
	
	private int fromY;

	private GameScene dstScene;
	
	private int dstX;
	
	private int dstY;
	
	public PlayerSwitchScene_GE(Player player, GameScene fromScene, int fromX, int fromY, GameScene dstScene, int dstX, int dstY) {
		this.player = player;
		this.fromScene = fromScene;
		this.fromX = fromX;
		this.fromY = fromY;
		this.dstScene = dstScene;
		this.dstX = dstX;
		this.dstY = dstY;
	}

	public GameScene getFromScene() {
		return fromScene;
	}

	public void setFromScene(GameScene fromScene) {
		this.fromScene = fromScene;
	}

	public GameScene getDstScene() {
		return dstScene;
	}

	public void setDstScene(GameScene dstScene) {
		this.dstScene = dstScene;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getFromX() {
		return fromX;
	}

	public void setFromX(int fromX) {
		this.fromX = fromX;
	}

	public int getFromY() {
		return fromY;
	}

	public void setFromY(int fromY) {
		this.fromY = fromY;
	}

	public int getDstX() {
		return dstX;
	}

	public void setDstX(int dstX) {
		this.dstX = dstX;
	}

	public int getDstY() {
		return dstY;
	}

	public void setDstY(int dstY) {
		this.dstY = dstY;
	}
}
