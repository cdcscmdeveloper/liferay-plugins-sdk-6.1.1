package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/*
 * Created By vidyasagar.
 * 
 */
public enum QTType implements IEnumType {

	OPEN('O'), BUMIPUTRA('B'), PANEL('P'), RESTRICTED_TENDER('R');

	private char name;

	private QTType(char name) {
		this.name = name;
	}

	@Override
	public Character value() {
		// TODO Auto-generated method stub
		return name;
	}

}