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
package newbee.morningGlory.mmorpg.player.depot.persistence;

import java.util.ArrayList;
import java.util.Collection;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.persistence.ItemBagReadWrite;

public class DepotPersistenceObject extends AbstractPersistenceObject {
	private static final String depotFieldData = "depotData";
	private PersistenceParameter depotPersistenceParameter = new PersistenceParameter();
	private ItemBagReadWrite depotReadWrite;
	private ItemBag depot;

	public DepotPersistenceObject(ItemBag depot) {
		super();
		this.setDepotReadWrite(new ItemBagReadWrite(depot));
		this.setDepot(depot);
		this.depotPersistenceParameter.setName(depotFieldData);
		this.persistenceParameters = new ArrayList<>(1);
		this.persistenceParameters.add(this.depotPersistenceParameter);
	}

	@Override
	public void snapshot() {
		depotPersistenceParameter.setValue(depotReadWrite.toBytes(depot));

	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (depotFieldData.equals(name)) {
				depotReadWrite.fromBytes((byte[]) persistenceParameter.getValue());
			}
		}

	}

	public PersistenceParameter getDepotPersistenceParameter() {
		return depotPersistenceParameter;
	}

	public void setDepotPersistenceParameter(PersistenceParameter depotPersistenceParameter) {
		this.depotPersistenceParameter = depotPersistenceParameter;
	}

	public ItemBagReadWrite getDepotReadWrite() {
		return depotReadWrite;
	}

	public void setDepotReadWrite(ItemBagReadWrite depotReadWrite) {
		this.depotReadWrite = depotReadWrite;
	}

	public ItemBag getDepot() {
		return depot;
	}

	public void setDepot(ItemBag depot) {
		this.depot = depot;
	}

}
