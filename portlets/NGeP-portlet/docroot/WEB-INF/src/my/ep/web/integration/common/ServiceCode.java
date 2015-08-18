package my.ep.web.integration.common;

public enum ServiceCode {

	PHS001("PHS-030"), 
	PHS002("PHS-040"), 
	PHS003("PHS-050"), 
	PHS004("PHS-060"), 
	PHS005("PHS-070"),
	PHS006("PHS-090"), 
	PHS007("PHS-100"), 
	PHS008("PHS-110"),
	PHS009("PHS-120"),
	PHS010("PHS-140");
	
	private String serviceCode;

	ServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String value() {
		return serviceCode;
	}

}
