/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum MemberType implements IEnumType {
	GOVT_USER(1),
	EXTERNAL_USER(2);
	
	
	private int memeberType;

	/**
	 * 
	 */
	private MemberType(int type) {
		this.memeberType=type;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getMemeberType() {
		return memeberType;
	}
	public void setMemeberType(int memeberType) {
		this.memeberType = memeberType;
	}

}
