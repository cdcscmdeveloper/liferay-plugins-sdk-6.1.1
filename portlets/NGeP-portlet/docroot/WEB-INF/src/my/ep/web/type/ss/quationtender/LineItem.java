/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum LineItem implements IEnumType{
	ALL_LINE_ITEM_ALL_ZONE("ALIAZ"),ALL_LINE_ITEM_CERTAIN_ZONE("ALICZ"),CERTAIN_LINE_ITEM_ALL_ZONE("CLIAZ"),CERTAIN_LINE_ITEM_CERTAIN_ZONE("CLICZ"),
	ALL_LINE("AL"),CERTAIN_LINE("CL");

	private String lineItem;
	
	private LineItem(String item) {
		this.lineItem=item;
	}
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return lineItem;
	}

}
