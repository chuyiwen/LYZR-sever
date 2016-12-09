/**
 * 
 */
package sophia.foundation.communication.practice.simulatorPattern;

import sophia.foundation.communication.practice.SocketCommunicationService;


public interface SimulatorCommunicationService extends SocketCommunicationService {
	ActionEventMessageHandler getActionEventMessageHandler();
	void setActionEventMessageHandler(ActionEventMessageHandler handler);
}
