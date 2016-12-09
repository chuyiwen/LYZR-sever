/**
 * 
 */
package sophia.foundation.authentication;


public interface IdentityBase<T> {
	T getId();
	void setId(T id);
	
	String getName();
	void setName(String name);
	
	T getCharId();
	void setCharId(T charId);
}
