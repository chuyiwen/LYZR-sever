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
package sophia.mmorpg.core;

public final class PropertyModifyState {
	private Short id;
	
	private boolean isDirty = false;
	
	public PropertyModifyState() {
		
	}
	
	public PropertyModifyState(Short id) {
		this.id = id;
		this.isDirty = false;
	}
	
	public PropertyModifyState(Short id, boolean isDirty) {
		this.id = id;
		this.isDirty = isDirty;
	}
	
	public PropertyModifyState(PropertyModifyState other) {
		this.id = other.id;
		this.isDirty = other.isDirty;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public String toString() {
		return "PropertyModifyState [id=" + id + ", isDirty=" + isDirty + "]";
	}
	
}
