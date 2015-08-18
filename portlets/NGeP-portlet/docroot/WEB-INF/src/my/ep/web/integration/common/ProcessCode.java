package my.ep.web.integration.common;

public enum ProcessCode {
	
	PHS001("PHS-001"), 
	PHS002("PHS-002"), 
	PHS003("PHS-003"), 
	PHS004("PHS-004"), 
	PHS005("PHS-005"),
	PHS006("PHS-006"), 
	PHS007("PHS-007"), 
	PHS008("PHS-008"),
	PHS009("PHS-009"),
	PHS010("PHS-010"),
	PHS011("PHS-011");
	
	private String processCode;

	ProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String value() {
		return processCode;
	}

}
