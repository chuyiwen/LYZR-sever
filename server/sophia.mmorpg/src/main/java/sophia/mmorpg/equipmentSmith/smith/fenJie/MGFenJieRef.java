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
package sophia.mmorpg.equipmentSmith.smith.fenJie;

import java.util.ArrayList;
import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MGFenJieRef extends AbstractGameRefObjectBase{

	private static final long serialVersionUID = -7853406435530449043L;
	private List<MGFenJieItem> fenJieItem = new ArrayList<>();
	
	public List<MGFenJieItem> getFenJieItem() {
		return fenJieItem;
	}
	public void setFenJieItem(List<MGFenJieItem> fenJieItem) {
		this.fenJieItem = fenJieItem;
	}
	
	
}
