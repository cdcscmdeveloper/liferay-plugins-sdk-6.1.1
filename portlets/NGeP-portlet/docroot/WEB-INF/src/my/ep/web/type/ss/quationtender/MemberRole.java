/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum MemberRole implements IEnumType {

	CHIRPERSON("C"),SECRETARY("S"),MEMBER("M");
	
	private String memberRole;
	/**
	 * 
	 */
	private MemberRole(String role) {
		this.memberRole=role;
	}
	public String getMemberRole() {
		return memberRole;
	}
	public void setMemberRole(String memberRole) {
		this.memberRole = memberRole;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return null;
	}
}
