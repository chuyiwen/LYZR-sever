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
package sophia.mmorpg.base.scene.aoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.Sprite;

public final class SetImplSceneAOIGrid implements SceneAOIGrid {
	private int row;
	
	private int column;
	
	private Set<Sprite> spriteSet;
	
	public SetImplSceneAOIGrid() {
		
	}
	
	public SetImplSceneAOIGrid(final int row, final int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public int getRow() {
		return row;
	}

	@Override
	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public void setRowAndColumn(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public Set<Sprite> getSpriteSet() {
		return spriteSet;
	}
	
	@Override
	public List<Sprite> getSpriteList(String spriteType) {
		if (spriteType == null) {
			return null;
		}
		
		List<Sprite> spriteList = new ArrayList<>();
		for (Sprite sprite : spriteSet) {
			if (sprite.getGameSpriteType().equals(spriteType)) {
				spriteList.add(sprite);
			}
		}
		
		return spriteList;
	}
	
	@Override
	public List<Sprite> getSpriteList(String spriteType, int row, int column) {
		if (spriteSet == null) {
			return null;
		}
		
		List<Sprite> spriteList = new ArrayList<>();
		for (Sprite sprite : spriteSet) {
			if (sprite.getGameSpriteType().equals(spriteType)) {
				Position pos = sprite.getCrtPosition();
				if (pos.getX() == column && pos.getY() == row) {
					spriteList.add(sprite);
				}
			}
		}
		
		return spriteList;
	}

	@Override
	public void addSprite(Sprite sprite) {
		if (spriteSet == null) {
			spriteSet = Collections
					.newSetFromMap(new ConcurrentHashMap<Sprite, Boolean>());
		}
		
		spriteSet.add(sprite);
	}
	
	public void addSpriteSet(Set<Sprite> spriteSet) {
		this.spriteSet.addAll(spriteSet);
	}


	@Override
	public boolean removeSprite(Sprite sprite) {
		if (spriteSet != null) {
			return spriteSet.remove(sprite);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SetImplSceneAOIGrid other = (SetImplSceneAOIGrid) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "G(" + row + "," + column + ")";
	}
}
