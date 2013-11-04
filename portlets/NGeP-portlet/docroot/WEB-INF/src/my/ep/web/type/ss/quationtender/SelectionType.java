/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum SelectionType implements IEnumType {
	PROFILE(1),
	EXISITING(2),
	New(3);

	private int userSelection;
	/**
	 * 
	 */
	private SelectionType(int type) {
		this.userSelection=type;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return userSelection;
	}
	public int getUserSelection() {
		return userSelection;
	}
	public void setUserSelection(int userSelection) {
		this.userSelection = userSelection;
	}

}
