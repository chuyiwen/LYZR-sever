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

import java.util.List;
import java.util.Set;

import sophia.mmorpg.base.sprite.Sprite;


public interface SceneAOIGrid {
	int getRow();
	
	void setRow(int row);
	
	int getColumn();
	
	void setColumn(int column);
	
	void setRowAndColumn(int row, int column);
	
	Set<Sprite> getSpriteSet();
	
	void addSprite(Sprite sprite);
	
	boolean removeSprite(Sprite sprite);
	
	List<Sprite> getSpriteList(String spriteType);
	
	List<Sprite> getSpriteList(String spriteType, int row, int column);
}
