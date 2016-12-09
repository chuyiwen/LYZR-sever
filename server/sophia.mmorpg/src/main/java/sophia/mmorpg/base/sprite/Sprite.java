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
package sophia.mmorpg.base.sprite;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.Position;
import sophia.game.component.GameObject;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.aoi.SpritePathComponent;
import sophia.mmorpg.camp.Camp;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public abstract class Sprite extends GameObject {
	protected String name;

	protected Camp camp;

	protected GameScene crtScene;

	protected volatile Position crtPosition = new Position();

	protected SpriteAOIComponent<?> aoiComponent;

	protected SpritePathComponent<?> pathComponent;

	protected SpritePerceiveComponent<?> perceiveComponent;

	protected Sprite() {

	}

	public abstract String getGameSpriteType();
	
	public abstract byte getSpriteType();

	public final Camp getCamp() {
		return camp;
	}

	public final void setCamp(Camp camp) {
		this.camp = camp;
	}

	public final GameScene getCrtScene() {
		return crtScene;
	}

	public final void setCrtScene(GameScene crtScene) {
		this.crtScene = crtScene;
	}

	public final SpriteAOIComponent<?> getAoiComponent() {
		return aoiComponent;
	}

	public  final void setAoiComponent(SpriteAOIComponent<?> aoiComponent) {
		this.aoiComponent = aoiComponent;
	}

	public final SpritePathComponent<?> getPathComponent() {
		return pathComponent;
	}

	public final void setPathComponent(SpritePathComponent<?> pathComponent) {
		this.pathComponent = pathComponent;
	}

	public final SpritePerceiveComponent<?> getPerceiveComponent() {
		return perceiveComponent;
	}

	public final void setPerceiveComponent(SpritePerceiveComponent<?> perceiveComponent) {
		this.perceiveComponent = perceiveComponent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getCrtPosition() {
		return crtPosition;
	}

	public void setCrtPosition(Position crtPosition) {
		this.crtPosition = crtPosition;
	}
	
	public String getSceneRefId() {
		return MGPropertyAccesser.getSceneRefId(getProperty());
	}

	public byte getProfession() {
		return MGPropertyAccesser.getProfessionId(getProperty());
	}

	public int getLevel() {
		return MGPropertyAccesser.getLevel(getProperty());
	}

	public SceneGrid getSceneGrid() {
		return crtScene.getTerrainLayer().getSceneGrid(crtPosition.getY(), crtPosition.getX());
	}
	
	/**
	 * 目前只是用于OtherSprite精灵的场景数据(PlayerAvatar等)
	 * @param buffer
	 */
	public void packSceneData(IoBuffer buffer) {
		// 默认场景属性字典长度为0
		buffer.putInt(0);
		// 默认state个数为0
		buffer.putShort((short)0);
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sprite other = (Sprite) obj;
		if (!StringUtils.equals(other.getId(), getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sprite [name=" + name + ", Id=" + getId() + "current position" + getCrtPosition() + "]";
	}
}
