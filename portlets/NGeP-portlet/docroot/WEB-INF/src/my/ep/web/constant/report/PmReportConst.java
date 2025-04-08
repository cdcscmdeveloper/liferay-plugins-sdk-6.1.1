package my.ep.web.constant.report;

import java.io.Serializable;

public class PmReportConst implements Serializable {

	private static final long serialVersionUID = 1L;

	// Report Parameter Key
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_USER_ID = "PUSER";
	public static final String PARAM_REPORT_ID = "PREPORTID";
	public static final String PARAM_PM_SUPP_ZONE = "PSUPPZONE";
	public static final String PARAM_PM_SUPP_PROFILE_STATUS = "PPRSTAT";
	public static final String PARAM_PM_FROM_DATE = "PFROMDATE";
	public static final String PARAM_PM_TO_DATE = "PTOMDATE";
	public static final String PARAM_PM_CONTRACT_NO = "PCONTRACTNO";
	public static final String PARAM_PM_PHYSICAL_CONTRACT_NO= "PPHYSICALCON";
	public static final String PARAM_PM_LOA_NO = "PLOA";
	public static final String PARAM_PM_PHYSICAL_LOA_NO = "PPHYSICALLOA";
	public static final String PARAM_PM_ITEM_NAME = "PITEMNAME";
	public static final String PARAM_PM_ITEM_CODE = "PITEMCODE";
	public static final String PARAM_PM_BIZREG_NO= "BIZREGNO";
	public static final String PARAM_PM_EP_NO = "PEPNO";
	public static final String PARAM_PM_COMPANY_NAME = "PCOMPANYNAME";
	public static final String PARAM_PM_SERVICE_TYPE = "SERVICETYPEID";
	public static final String PARAM_PM_FINORG_NAME = "FINORGNAME";
	public static final String PARAM_PM_STATUS = "PSTATUS";
	public static final String PARAM_PM_G2G = "PG2G"; //shafie - #28833 
	public static final String PARAM_PM_ORG_NAME = "PORGNAME";
	public static final String PARAM_PM_ORG_CODE = "PORGCODE";
	public static final String PARAM_PM_ORG_CATEGORY = "PCATTYPE";
	public static final String PARAM_PM_ORG_TYPE_PARAM_ID = "PORGTYPE";
	public static final String PARAM_PM_USERNAME = "PUSERNAME";
	public static final String PARAM_PM_IDENTIFICATION_NO = "PIDENNO";
	public static final String PARAM_PM_ROLE_CODE = "PROLECODE";
	public static final String PARAM_PM_APPLICANT_NAME = "PAPPLICANTNAME";
	public static final String PARAM_PM_ACCESS_TYPE = "PACCESSTYPE";
	public static final String PARAM_PM_APPLICATION_STATUS = "PAPPLICATIONSTATUS";
	public static final String PARAM_PM_APPLICATION_STATUS_1 = "PAPPLICATIONSTATUS1";
	//shafie - #28833 -start
	public static final String PARAM_PM_ORG_TYPE_MINISTRY = "P2";
	public static final String PARAM_PM_ORG_TYPE_KUMPULAN_PTJ = "P4";
	public static final String PARAM_PM_ORG_TYPE_PTJ = "P5";
	//shafie - #28833 -end
	//shafie - #29486 - start
	public static final String PARAM_PM_MINISTRY = "PMINISTRY";
	public static final String PARAM_PM_KUMPULAN_PTJ = "PKPTJ";
	public static final String PARAM_PM_PTJ = "PPTJ"; 
	public static final String PARAM_PM_USER_STATUS = "PSTATUS"; 
	public static final String PARAM_PM_MONTH = "PMTH"; 
	//shafie - #29486 - end
	//#29486 - start 
    public static final String PARAM_PM_PTJ_CODE = "PPTJCD";
    //#29486 - end
	

	// PM Report Code
	public static final String PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL = "PM_001";
	public static final String PM_LIST_OF_ORGANIZATION_RPT_VAL = "PM_002";
	public static final String PM_LIST_OF_PROFILES_RPT_VAL = "PM_003";
	public static final String PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL = "PM_004";
	public static final String PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL = "PM_005";
	public static final String PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL = "PM_006";
	public static final String PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL = "PM_007";
	public static final String PM_BPK_USER_PROFILE_RPT_VAL = "PM_008";
	public static final String PM_BPK_ADMIN_PROFILE_RPT_VAL = "PM_009";
	public static final String PM_NO_OF_CP_AND_MR_ACCESS_APPLICATION_RPT_VAL = "PM_010";
	public static final String PM_LIST_OF_PTJ_ALLOWED_TO_APPROVE_OTHER_PTJ_PROCUREMENT = "PM_012"; //shafie - #28833
	public static final String PM_MONTHLY_USER_AND_ROLE_RPT_VAL = "PM_013"; //Shafie - #29486
	

	// Org Category values
	public static final int ORG_CAT_FEDERAL_GOVT = 0;
	public static final int ORG_CAT_OTHERS = 1;
	public static final int ORG_CAT_CP_MR = 2;
	
	//shafie - #28833 -start
	//Org Type Values
	public static final int ORG_TYPE_EP_PROFILE = 1;
	public static final int ORG_TYPE_MINISTRY = 2;
	public static final int ORG_TYPE_PEGAWAI_PENGAWAL = 3;
	public static final int ORG_TYPE_KUMPULAN_PTJ = 4;
	public static final int ORG_TYPE_PTJ = 5;
	public static final int ORG_TYPE_PEROLEHAN_KERAJAAN = 6;
	//shafie - #28833 -end
	//Org Type Values and Org Cat eq Others
	public static final int ORG_TYPE_SUPPLIER = 15; //#29991
	public static final String SUPPLIER_PARAM_CODE = "SUP"; //#29991
	public static final String PARAM_PM_BUSINESS_REGNO = "PREGNO"; //#29991
	
	public PmReportConst() {
	}

	/**
	 * @return the pmListOfFinacialInstitutionsRptVal
	 */
	public String getPmListOfFinacialInstitutionsRptVal() {
		return PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL;
	}

	/**
	 * @return the pmListOfOrganizationRptVal
	 */
	public String getPmListOfOrganizationRptVal() {
		return PM_LIST_OF_ORGANIZATION_RPT_VAL;
	}

	/**
	 * @return the pmListOfProfilesRptVal
	 */
	public String getPmListOfProfilesRptVal() {
		return PM_LIST_OF_PROFILES_RPT_VAL;
	}

	/**
	 * @return the pmListOfGovernmentSellersRptVal
	 */
	public String getPmListOfGovernmentSellersRptVal() {
		return PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL;
	}

	/**
	 * @return the 

	 */
	public String getPmListOfExemptedItemsBySupplierRptVal() {
		return PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL;
	}

	/**
	 * @return the pmListOfExemptedItemsRptVal
	 */
	public String getPmListOfExemptedItemsRptVal() {
		return PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL;
	}

	/**
	 * @return the pmListOfExemptedContractedItemsRptVal
	 */
	public String getPmListOfExemptedContractedItemsRptVal() {
		return PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL;
	}

	/**
	 * @return the pmBpkUserProfileRptVal
	 */
	public String getPmBpkUserProfileRptVal() {
		return PM_BPK_USER_PROFILE_RPT_VAL;
	}

	/**
	 * @return the pmBpkAdminProfileRptVal
	 */
	public String getPmBpkAdminProfileRptVal() {
		return PM_BPK_ADMIN_PROFILE_RPT_VAL;
	}

	/**
	 * @return the pmListOfCompanyProfileAndMarketResearchAccessApplicationRptVal
	 */
	public String getPmNoOfCpAndMrAccessApplicationRptVal() {
		return PM_NO_OF_CP_AND_MR_ACCESS_APPLICATION_RPT_VAL;
	}
	
	// shafie #28833  start
	/**
	 * @return the pmListOfPTJAllowedToApproveOtherPTJProcurementRptVal
	 */
	public String getpmListOfPTJAllowedToApproveOtherPTJProcurementRptVal() {
		return PM_LIST_OF_PTJ_ALLOWED_TO_APPROVE_OTHER_PTJ_PROCUREMENT;
	}
	
	/**
	 * @return the orgTypeEPProfile
	 */
	public int getOrgTypeEPProfile() {
		return ORG_TYPE_EP_PROFILE;
	}
	
	/**
	 * @return the orgTypeMinistry
	 */
	public int getOrgTypeMinistry() {
		return ORG_TYPE_MINISTRY;
	}
	
	/**
	 * @return the orgTypePegawaiPengawal
	 */
	public int getOrgTypePegawaiPengawal() {
		return ORG_TYPE_PEGAWAI_PENGAWAL;
	}
	
	/**
	 * @return the orgTypeKumpulanPTJ
	 */
	public int getOrgTypeKumpulanPTJ() {
		return ORG_TYPE_KUMPULAN_PTJ;
	}
	
	/**
	 * @return the orgTypePTJ
	 */
	public int getOrgTypePTJ() {
		return ORG_TYPE_PTJ;
	}
	
	/**
	 * @return the orgTypePerolehanKerajaan
	 */
	public int getOrgTypePerolehanKerajaan() {
		return ORG_TYPE_PEROLEHAN_KERAJAAN;
	}
	// shafie #28833  end

	//shafie - #29486 - start
	/**
	 * @return the pmMonthlyUserAndRoleRptVal
	 */
	public String getPmMonthlyUserAndRoleRptVal() {
		return PM_MONTHLY_USER_AND_ROLE_RPT_VAL;
	}	
	//shafie - #29486 - end
	
	/**
	 * @return the orgCatFederalGovt
	 */
	public int getOrgCatFederalGovt() {
		return ORG_CAT_FEDERAL_GOVT;
	}

	/**
	 * @return the orgCatOthers
	 */
	public int getOrgCatOthers() {
		return ORG_CAT_OTHERS;
	}

	/**
	 * @return the orgCatCpMr
	 */
	public static int getOrgCatCpMr() {
		return ORG_CAT_CP_MR;
	}
	
	//#29991 start
	/**
	 * @return the orgTypeSupplier
	 */
	public int getOrgTypeSupplier() {
		return ORG_TYPE_SUPPLIER;
	}
	//#29991 end

}
