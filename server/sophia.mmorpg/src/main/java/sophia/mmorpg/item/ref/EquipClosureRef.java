/**
 * 
 */
package sophia.mmorpg.item.ref;

/**
 * @author Administrator
 *
 */
public final class EquipClosureRef {
	
	private String equipClosure;
	private String deequipClosure;
	public EquipClosureRef() {}
	
	public EquipClosureRef(String equipClosure,String deequipClosure) {
		this.equipClosure = equipClosure;
		this.deequipClosure = deequipClosure;
	}

	public String getEquipClosure() {
		return equipClosure;
	}

	public void setEquipClosure(String equipClosure) {
		this.equipClosure = equipClosure;
	}

	public String getDeequipClosure() {
		return deequipClosure;
	}

	public void setDeequipClosure(String deequipClosure) {
		this.deequipClosure = deequipClosure;
	}

}
