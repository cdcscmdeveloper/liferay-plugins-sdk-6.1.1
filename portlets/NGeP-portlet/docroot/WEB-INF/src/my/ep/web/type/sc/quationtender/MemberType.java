/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum MemberType implements IEnumType {
	GovernmentUser(true),
	NonGovernmentUser(false);
	
	
	private boolean memeberType;

	/**
	 * 
	 */
	private MemberType(boolean type) {
		this.memeberType=type;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return memeberType;
	}
	public boolean getMemeberType() {
		return memeberType;
	}
	public void setMemeberType(boolean memeberType) {
		this.memeberType = memeberType;
	}

}
