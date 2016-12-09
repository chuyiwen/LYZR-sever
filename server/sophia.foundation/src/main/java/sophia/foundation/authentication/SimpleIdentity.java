/**
 * 
 */
package sophia.foundation.authentication;


public class SimpleIdentity implements Identity {
	protected String id;
	protected String name;
	protected String charId;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public  void setId(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCharId() {
		return charId;
	}

	@Override
	public void setCharId(String charId) {
		this.charId = charId;
	}
}
