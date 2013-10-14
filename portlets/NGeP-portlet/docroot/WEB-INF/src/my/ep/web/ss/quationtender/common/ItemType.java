/**
 * 
 */
package my.ep.web.ss.quationtender.common;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum ItemType implements IEnumType{

	PRODUCT('P'),SERVICE('S');
	
	private Character item;
	
	ItemType(Character ch){
		this.item=ch;
	}

	public Character getItem() {
		return item;
	}

	public void setItem(Character item) {
		this.item = item;
	}

	
	@Override
	public Object value() {
		
		return item;
	}
}
