/**
 * 
 */
package sophia.game.component.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Slot {

	private ArrayList<Signal> signals;
	
	public void addSignal(Signal signal) {
		if (signals == null) {
			signals = new ArrayList<Signal>();
		}
		
		signals.add(signal);
		signal.addSlot(this);
	
		signals.trimToSize();
	}
	
	public void removeSignal(Signal signal) {
		if (signals == null) { return; }
		signals.remove(signal);
	}
	
	public List<Signal> getSignals() {
		return Collections.unmodifiableList(signals);
	}
	
	public void send() {
		if (signals == null) { return; }
		
		for (int i = 0; i < signals.size(); i++) {
			signals.get(0).signal();
		}	
	}
	
	public void unlink() {
		if (signals == null) { return; }
		
		for (Signal signal : signals) {
			signal.removeSlot(this);
		}
	}
}
