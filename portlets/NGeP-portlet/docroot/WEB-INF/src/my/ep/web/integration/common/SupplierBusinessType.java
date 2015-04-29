package my.ep.web.integration.common;

public enum SupplierBusinessType {
	
	BNM("BNM"), 
	SSM("SSM"), 
	ROS("ROS"), 
	SKM("SKM"), 
	JPN("JPN"), 
	JPA("JPA"), 
	KPT("KPT"), 
	MAMP("MAMP"),
	JKIM("JKIM"), 
	KPM("KPM"), 
	JIM("JIM"), 
	BHEU("BHEU"), 
	NOTE("NOTE"), 
	PTJ("PTJ"), 
	NEG("NEG"), 
	PER("PER");

	private String suppType;

	SupplierBusinessType(String suppType) {
		this.suppType = suppType;
	}

	public String value() {
		return suppType;
	}
}
