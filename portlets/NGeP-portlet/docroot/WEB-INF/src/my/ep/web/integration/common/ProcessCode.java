package my.ep.web.integration.common;

public enum ProcessCode {

	//PHIS
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
	PHS011("PHS-011"),
	// 1GFMAS
	GLMDE("GLMDE"), 
	MMINF("MMINF"), 
	APIVE("APIVE"), 
	AP516("AP516"), 
	AR502("AR502"), 
	GLBAC("AR502"), 
	GLSEG("GLSEG"), 
	GLVOT("GLVOT"), 
	GLDNA("GLDNA"), 
	GLPRG("GLPRG"), 
	GLPRJ("GLPRJ"), 
	GLGLC("GLGLC"), 
	GLPCG("GLPCG"), 
	GLPTJ("GLPTJ"), 
	AP511("AP511"), 
	APERR("APERR"), 
	CMBNK("CMBNK"), 
	APOVE("APOVE"), 
	AR902("AR902");

	private String processCode;

	ProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String value() {
		return processCode;
	}

}
