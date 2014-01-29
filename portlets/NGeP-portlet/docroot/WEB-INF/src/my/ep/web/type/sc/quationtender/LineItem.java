/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum LineItem implements IEnumType{
	ALL_LINE_ITEM_ALL_ZONE(1),ALL_LINE_ITEM_CERTAIN_ZONE(2),CERTAIN_LINE_ITEM_ALL_ZONE(3),CERTAIN_LINE_ITEM_CERTAIN_ZONE(4),
	ALL_LINE(5),CERTAIN_LINE(6);

	private Integer lineItem;
	
	private LineItem(Integer item) {
		this.lineItem=item;
	}
	@Override
	public Integer value() {
		// TODO Auto-generated method stub
		return lineItem;
	}
	public Integer getLineItem() {
		return lineItem;
	}
	public void setLineItem(Integer lineItem) {
		this.lineItem = lineItem;
	}

}
