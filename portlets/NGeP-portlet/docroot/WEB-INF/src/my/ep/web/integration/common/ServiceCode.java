package my.ep.web.integration.common;

public enum ServiceCode {

	//PHIS
	PHS001("PHS-030"), 
	PHS002("PHS-040"), 
	PHS003("PHS-050"), 
	PHS004("PHS-060"), 
	PHS005("PHS-070"),
	PHS006("PHS-090"), 
	PHS007("PHS-100"), 
	PHS008("PHS-110"),
	PHS009("PHS-120"),
	PHS010("PHS-140"),
	PHS011("PHS-200"),
	//1GFMAS
	GLMDE("GFM-150"),
	MMINF("MMINF"),
	APIVE("GFM-010"),
	AP516("GFM-350"),
	AR502("AR502"),
	GLBAC("GFM-190"),
	GLSEG("GFM-200"),
	GLVOT("GFM-210"),
	GLDNA("GFM-220"),
	GLPRG("GFM-230"),
	GLPRJ("GFM-240"),
	GLGLC("GFM-260"),
	GLPCG("GFM-270"),
	GLPTJ("GFM-280"),
	AP511("GFM-140"),
	APERR("GFM-090"),
	CMBNK("GFM-360"),
	APOVE("GFM-370"),
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
	
	private String serviceCode;

	ServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String value() {
		return serviceCode;
	}

}
