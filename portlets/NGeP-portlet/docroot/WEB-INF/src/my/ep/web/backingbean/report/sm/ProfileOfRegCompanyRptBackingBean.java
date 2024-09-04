package my.ep.web.backingbean.report.sm;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.OrganizationType;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.managedbean.report.sm.ProfileOfRegCompanyRptManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "profileOfRegCompanyRptBB")
@ViewScoped
public class ProfileOfRegCompanyRptBackingBean extends
		SmReportBackingBean<ProfileOfRegCompanyRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ProfileOfRegCompanyRptBackingBean.class);
	
	public ProfileOfRegCompanyRptBackingBean() {
		resetSearch();
		onChangeBussType();
		retrieveAgencyTypes();
	}

	public void generateReport() {
		// #29712, #32564 - start
		validateParameterValue(getManagedBean().getReportCode());
		// #29712, #32564 - end
		
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

		log.info("********Report Code*************" + getManagedBean().getReportCode());
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone() == null ? StringUtils.EMPTY : getManagedBean().getZone());
		if (getManagedBean().getRegType() != null && !(getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_MOF))) {
			getManagedBean().setReportCode(SmReportConst.SM_REG_COMPANY_IN_BASIC_G2G);
			if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_BASIC)) {
				params.put(SmReportConst.PARAM_REG_TYPE, SmReportConst.REG_ACC_TYPE_BASIC);
				// #29712 & #30824, #32564 - start
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean().getSelectedBasicAccount().getCompanyName());
				// #29712 & #30824, #32564 - end
				// #32564 & #26238, #30824 - Start
				params.put(SmReportConst.PARAM_SUPPLIER_ID,
						String.valueOf(getManagedBean().getSelectedBasicAccount().getSupplierId()));
				// #32564 & #26238, #30824 - End
			} else {
				params.put(SmReportConst.PARAM_REG_TYPE, SmReportConst.REG_ACC_TYPE_G2G);
				// #29712 & #30824, #32564 - start
				params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean().getSelectedOrgProfile().getPmOrgValidity().getOrgName());
				// #29712 & #30824, #32564 - end
				// #32564 & #26238, #30824 - Start
				params.put(SmReportConst.PARAM_SUPPLIER_ID,
						String.valueOf(getManagedBean().getSelectedOrgProfile().getSmSupplier().getSupplierId()));
				// #32564 & #26238, #30824 - End
			}
		} else {
			params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getSelectedMofAccount().getMofNo()); //#30824
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
			// #32564 & #26238 & #30824 - Start
			if(getManagedBean().getSelectedMofAccount().getSupplierId() != null){
				params.put(SmReportConst.PARAM_SUPPLIER_ID,
						String.valueOf(getManagedBean().getSelectedMofAccount().getSupplierId()));	
			}
			// #32564 & #26238 & #30824 - End
		}
		log.info("********Report Code************* after set newCode: " + getManagedBean().getReportCode());
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			log.info("Key : " + entry.getKey() + "=" + entry.getValue() + "&");
		}

		retrieveReportValueFromDb();
		generateSmReport(params);
	}

	public void resetSearch() {
		this.getManagedBean().setZone(null);
		this.getManagedBean().setSupplierType(null);
		this.getManagedBean().setMofNo(null);
		// #23080 - start
		this.getManagedBean().setRegType(null);
		this.getManagedBean().setOrgTypeCode(null);
		this.getManagedBean().setSelectedOrgProfile(null);
		// #23080 - end
		// #29712 & #30824, #32564 - start
		this.getManagedBean().setSelectedSupplier(null);
		this.getManagedBean().setSelectedMofAccount(null);
		this.getManagedBean().setSelectedBasicAccount(null);
		// #29712 & #30824, #32564 - end
	}
	
	public void populateMofNo(BaseEntity entity) {
		SmMofAccount selectedMof= (SmMofAccount)entity;
		this.getManagedBean().setMofNo(selectedMof.getMofNo());
	}
	
	// #29712, #32564 - start
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isNotEmpty(getManagedBean().getRegType()) || getManagedBean().getRegType() != null) {
			if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_G2G)) {
//				sb.append("recordStatus^1"); -- #30824
//				sb.append("|pmOrgValidity.recordStatus^1"); -- #30824
				sb.append("orgTypeCode.parameterType^");
				sb.append(ParameterTypeConst.ORGANISATION_TYPES);

				if (this.getManagedBean().getOrgTypeCode() != null) {
					if (this.getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.STATUTORY_BODIES.value())) {
						sb.append("|orgTypeCode.parameterCode^");
						sb.append(OrganizationType.STATUTORY_BODIES.value());
					}
					if (this.getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.STATUTORY_BODIES_STATE.value())) {
						sb.append("|orgTypeCode.parameterCode^");
						sb.append(OrganizationType.STATUTORY_BODIES_STATE.value());
					}
					if (this.getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.STATE_GOVERNMENT.value())) {
						sb.append("|orgTypeCode.parameterCode^");
						sb.append(OrganizationType.STATE_GOVERNMENT.value());
					}
					if (this.getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.LOCAL_AUTHORITY.value())) {
						sb.append("|orgTypeCode.parameterCode^");
						sb.append(OrganizationType.LOCAL_AUTHORITY.value());
					}
				} else {
					sb.append("|orgTypeCode.parameterCode^");
					sb.append(OrganizationType.STATUTORY_BODIES.value());
					sb.append(",");
					sb.append(OrganizationType.STATUTORY_BODIES_STATE.value());
					sb.append(",");
					sb.append(OrganizationType.STATE_GOVERNMENT.value());
					sb.append(",");
					sb.append(OrganizationType.LOCAL_AUTHORITY.value());
					sb.append("^9");
				}
				if(getManagedBean().getZone() != null){
					if(sb.length() != 0) sb.append("|");
					if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SEMENANJUNG")) {
						sb.append("pmZone.zoneId^1");
					} else if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SABAH")) {
						sb.append("pmZone.zoneId^2");
					} else if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SARAWAK")) {
						sb.append("pmZone.zoneId^3");
					}
				}
			} else if (getManagedBean().getRegType().equalsIgnoreCase(SmReportConst.SM_REG_TYPE_MOF)) {
				if (this.getManagedBean().getZone() != null) {
					if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SEMENANJUNG")) {
						sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^1"); //#30824
					} else if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SABAH")) {
						sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^2"); //#30824
					} else if (getManagedBean().getZone().toUpperCase().equalsIgnoreCase("SARAWAK")) {
						sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^3"); //#30824
					}
				}
				//#32564 - start - ac:smReportSupplierType
				if (getManagedBean().getSupplierType() != null && !getManagedBean().getSupplierType().equals("all")){
					if(sb.length() != 0) sb.append("|");
					sb.append("smAppl.supplierType^" + this.getManagedBean().getSupplierType()); //#30824
				} else {
					if(sb.length() != 0) sb.append("|");
					sb.append("smAppl.supplierType^" + SupplierManagementConst.SUPP_TYPE_CONSULTANT //#30824
							+ "*smAppl.supplierType^" + SupplierManagementConst.SUPP_TYPE_CONTRACTOR);//#30824
				}
				//#32564 - end
			}
		}
		//32564
		log.info("32564- constructConditionFields():"+sb.toString());
		return sb.toString();
	}
	// #29712, #32564 - end
	
	//#32564, #30824 - start
	public String constructConditionFieldsBasic(){
		StringBuilder sb = new StringBuilder();
		if (getManagedBean().getZone() != null) {
			if (getManagedBean().getZone().equalsIgnoreCase("SEMENANJUNG")) {
				sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^1");
			} else if (getManagedBean().getZone().equalsIgnoreCase("SABAH")) {
				sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^2");
			} else if (getManagedBean().getZone().equalsIgnoreCase("SARAWAK")) {
				sb.append("smAppl.smCompanyBasic.smCompanyAddresses.smAddress.pmState.pmZoneStates.pmZone.zoneId^3");
			}
		}
		log.info("32564- constructConditionFieldsBasic():"+sb.toString());
		return sb.toString();
	}
	//#32564, #30824 - end
	
}