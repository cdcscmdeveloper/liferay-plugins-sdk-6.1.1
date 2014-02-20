/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum SelectionType implements IEnumType {
	//PROFILE("P"),
	EXISITING("E"),
	New("N");

	private String userSelection;
	/**
	 * 
	 */
	private SelectionType(String type) {
		this.userSelection=type;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public String value() {
		// TODO Auto-generated method stub
		return userSelection;
	}
	public String getUserSelection() {
		return userSelection;
	}
	public void setUserSelection(String userSelection) {
		this.userSelection = userSelection;
	}

}
