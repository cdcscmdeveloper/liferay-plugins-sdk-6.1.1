/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum RecordStatus implements IEnumType {

	ACTIVE(false),DELETED(true),INACTIVE(true),MARKFORDELETE(true);
	
	private Boolean status;
		
	private RecordStatus(Boolean status) {
		this.status=status;
	}


	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Boolean value() {
		// TODO Auto-generated method stub
		return status;
	}


	public Boolean getStatus() {
		return status;
	}


	public void setStatus(Boolean status) {
		this.status = status;
	}
}
