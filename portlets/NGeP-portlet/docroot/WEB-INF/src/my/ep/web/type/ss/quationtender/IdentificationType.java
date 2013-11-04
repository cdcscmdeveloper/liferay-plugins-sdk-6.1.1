/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum IdentificationType implements IEnumType{

	PASSPORT(1),
	IC(2);
	
	private int idType;
	/**
	 * 
	 */
	private IdentificationType(int type) {
		this.idType=type;
	}

	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}
}
