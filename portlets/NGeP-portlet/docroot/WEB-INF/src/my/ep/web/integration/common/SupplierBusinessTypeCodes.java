package my.ep.web.integration.common;

public enum SupplierBusinessTypeCodes {
	
	A(SupplierBusinessType.JPN.value()), 
	B(SupplierBusinessType.SSM.value()), 
	C(SupplierBusinessType.SSM.value()), 
	D(SupplierBusinessType.NEG.value()), 
	E(SupplierBusinessType.NEG.value()), 
	F(SupplierBusinessType.SSM.value()), 
	G(SupplierBusinessType.NEG.value()), 
	H(SupplierBusinessType.NEG.value()), 
	I(SupplierBusinessType.SSM.value()), 
	J(SupplierBusinessType.SSM.value()), 
	K(SupplierBusinessType.ROS.value()), 
	L(SupplierBusinessType.SKM.value()), 
	
	M(SupplierBusinessType.NEG.value()),   // need to confirm
	N(SupplierBusinessType.NEG.value()),   //
	O(SupplierBusinessType.NEG.value()),   //
	P(SupplierBusinessType.NEG.value());   //

	private String codeType;

	SupplierBusinessTypeCodes(String codeType) {
		this.codeType = codeType;
	}

	public String value() {
		return codeType;
	}
}
