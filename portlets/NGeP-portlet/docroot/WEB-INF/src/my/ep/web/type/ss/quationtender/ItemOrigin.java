package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;
/**
 * @author SudheerKumar
 *
 */

public enum ItemOrigin implements IEnumType {
	
	LOCAL("L"),IMPORTED("I"),LOCALIMPORTED("LI");
	
	
private String itemOrigin;
	
	private ItemOrigin(String origin) {
		this.itemOrigin=origin;
	}
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return itemOrigin;
	}
}

