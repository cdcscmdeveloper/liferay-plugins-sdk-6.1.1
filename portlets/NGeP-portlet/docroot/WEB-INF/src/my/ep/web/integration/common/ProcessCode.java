package my.ep.web.integration.common;

public enum ProcessCode {

	//PHIS
	PHS000("PHS000"),
	PHS001("PHS001"), 
	PHS002("PHS002"), 
	PHS003("PHS003"), 
	PHS004("PHS004"), 
	PHS005("PHS005"), 
	PHS006("PHS006"), 
	PHS007("PHS007"), 
	PHS008("PHS008"), 
	PHS009("PHS009"), 
	PHS010("PHS010"), 
	PHS011("PHS011"),
	// 1GFMAS
	GLMDE("GLMDE"), 
	MMINF("MMINF"), 
	APIVE("APIVE"), 
	AP516("AP516"), 
	AR502("AR502"), 
	GLBAC("GLBAC"), 
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
	AR902("AR902"),
	//Eperunding
	EPE01("EPE01"),
	EPE02("EPE02"),
	EPE03("EPE03"),
	EPE04("EPE04"),
	EPE05("EPE05"),
	EPE06("EPE06"),
	EPE07("EPE07"),
	EPE08("EPE08"),
	EPE09("EPE09"),
	EPE10("EPE10"),
	EPE11("EPE11");
		

	private String processCode;

	ProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String value() {
		return processCode;
	}

}
