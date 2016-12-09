package sophia.mmorpg.equipmentSmith.smith;

import java.util.List;

import sophia.foundation.collect.GenericTree;
import sophia.foundation.collect.GenericTreeNode;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.equipment.AbstractSmithRoot;
import sophia.mmorpg.item.equipment.EquipmentSmith;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-4 下午8:15:22
 * @version 1.0
 */
public class EquipmentSmithRoot extends AbstractSmithRoot {
	private GenericTree<EquipmentSmith> tree;
	PropertyDictionary propertyDictionary;

	public EquipmentSmithRoot(Item owner, GenericTree<EquipmentSmith> tree) {
		super(owner);
		this.tree = tree;
	}

	@Override
	public PropertyDictionary getPropertyDictionary() {
		return this.propertyDictionary;
	}

	@Override
	protected void calculate() {
		List<GenericTreeNode<EquipmentSmith>> children = tree.find(this)
				.getChildren();
		propertyDictionary = new PropertyDictionary();
		for (GenericTreeNode<EquipmentSmith> genericTreeNode : children) {
			PropertyDictionary childPropertyDictionary = genericTreeNode
					.getData().getPropertyDictionary();
			
			if (childPropertyDictionary != null) {
				
				propertyDictionary = propertyDictionary.add(childPropertyDictionary);
			}
		}
		
	}

}
