/**
 * 
 */
package sophia.mmorpg.auth;

import sophia.foundation.authentication.Identity;

public class AuthIdentity implements Identity {

	private String id;
	private String name;
	private String uuid;
	private String charId;
	private int qdCode1;
	private int qdCode2;

	public AuthIdentity() {
		super();
	}

	public AuthIdentity(String id, String name, String uuid, int qdCode1,
			int qdCode2) {
		this.id = id;
		this.name = name;
		this.uuid = uuid;
		this.qdCode1 = qdCode1;
		this.qdCode2 = qdCode2;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getQdCode1() {
		return qdCode1;
	}

	public void setQdCode1(int qdCode1) {
		this.qdCode1 = qdCode1;
	}

	public int getQdCode2() {
		return qdCode2;
	}

	public void setQdCode2(int qdCode2) {
		this.qdCode2 = qdCode2;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AuthIdentity))
			return false;
		AuthIdentity ai = (AuthIdentity) obj;
		return ai.getId().equals(getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return "AuthIdentity [id=" + id + ", name=" + name + "]";
	}
}
