/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

// TODO: Auto-generated Javadoc
/**
 * 
 * Enum class for SelectionType.
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public enum SelectionType implements IEnumType {
	//PROFILE("P"),
	/** The Existing. */
	Existing("E"),
	
	/** The New. */
	New("N");

	/** The user selection. */
	private String userSelection;
	
	/**
	 * Instantiates a new selection type.
	 *
	 * @param type the type
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
	
	/**
	 * Gets the user selection.
	 *
	 * @return the user selection
	 */
	public String getUserSelection() {
		return userSelection;
	}
	
	/**
	 * Sets the user selection.
	 *
	 * @param userSelection the new user selection
	 */
	public void setUserSelection(String userSelection) {
		this.userSelection = userSelection;
	}

}
