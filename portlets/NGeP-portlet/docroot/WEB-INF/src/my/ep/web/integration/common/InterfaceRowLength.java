package my.ep.web.integration.common;

public enum InterfaceRowLength {
	
		//1GFMAS
		GLMDE(0),
		MMINF(0),
		APIVE(0),
		AP516(0),
		AR502(0),
		GLBAC(264),
		GLSEG(262),
		GLVOT(281),
		GLDNA(280),
		GLPRG(285),
		GLPRJ(292),
		GLGLC(270),
		GLPCG(278),
		GLPTJ(290),
		AP511(0),
		APERR(0),
		CMBNK(53),
		APOVE(0),
		AR902(0);
		
		private int serviceCode;

		InterfaceRowLength(int serviceCode) {
			this.serviceCode = serviceCode;
		}

		public int value() {
			return serviceCode;
		}

}
