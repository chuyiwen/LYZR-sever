/**
 * 
 */
package sophia.game.component.communication;

import java.util.LinkedList;
import java.util.List;


public class Signal {
	
	private List<Slot> slots;
	
	protected void addSlot(Slot slot) {
		assert(slot != null);
		if (slots == null) {
			slots = new LinkedList<Slot>();
		}
		if (!slots.contains(slot)) {
			slots.add(slot);
		}
	}
	
	protected void removeSlot(Slot slot) {
		assert(slot != null);
		if (slots != null) {
			slots.remove(slot);
		}
	}
	
	public void unlink() {
		if (slots == null) return;
		for (Slot slot : slots) {
			slot.removeSignal(this);
		}
	}
	
	/**
	 * 接受发送过来的信号
	 */
	public void signal() {
	
	}
}
