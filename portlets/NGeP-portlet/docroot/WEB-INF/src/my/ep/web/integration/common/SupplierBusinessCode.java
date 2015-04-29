package my.ep.web.integration.common;

public enum SupplierBusinessCode {
	
	A("A"), 
	B("B"), 
	C("C"), 
	D("D"), 
	E("E"), 
	F("F"), 
	G("G"), 
	H("H"), 
	I("I"), 
	J("J"), 
	K("K"), 
	L("L"), 
	
	M("M"),   // need to confirm
	N("N"),   //
	O("O"),   //
	P("P");   //

	private String suppCode;

	SupplierBusinessCode(String suppCode) {
		this.suppCode = suppCode;
	}

	public String value() {
		return suppCode;
	}

}
