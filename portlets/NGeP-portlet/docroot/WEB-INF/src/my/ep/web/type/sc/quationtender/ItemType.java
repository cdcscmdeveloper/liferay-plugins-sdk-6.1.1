/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum ItemType implements IEnumType{

	PRODUCT(1),SERVICE(2),PRODUCTANDSERVICE(3);
	
	private Integer item;
	
	ItemType(Integer ch){
		this.item=ch;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	
	@Override
	public Integer value() {
		
		return item;
	}
}
