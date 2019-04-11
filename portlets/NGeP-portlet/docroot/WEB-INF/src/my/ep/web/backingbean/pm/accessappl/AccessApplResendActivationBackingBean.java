/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.pm.accessappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * The backing bean for Access Appl Resend Activation
 * 
 * @author Ken
 * @since 27.06.2016
 */
@ManagedBean(name = "accessApplResendActivationBB")
@ViewScoped
public class AccessApplResendActivationBackingBean extends
	BaseBackingBean<BaseManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger
			.getLogger(AccessApplResendActivationBackingBean.class);
	
	private static final String ACCESS_APPL_RESEND_ACTIVATION_MAIN_PAGE ="/xhtml/pm/accessappl/resendactivation/resendAccessApplActivationEmail.xhtml?faces-redirect=true";
	private static final String ACCESS_APPL_OUTPUT_MSG = "/xhtml/pm/accessappl/outputMsg.xhtml";
	
	private String email;
	private String companyName;
	private String identificationNo;
	private Long selectedOrgType;
	private PmOrgProfile selectedOrgProfile;
	private Map<String,SelectItem[]> orgTypeMap = 
			new HashMap<String, SelectItem[]>();
	
	public AccessApplResendActivationBackingBean(){}
	
	/**
	 * Pre Loading
	 * 	 
	 * */
	public void preLoading() {
		log.info("preLoading");
		renewSesionObject();
		
		PmOrgProfile orgProfile = new PmOrgProfile();
		orgProfile.setPmOrgValidity(new PmOrgValidity());
		setSelectedOrgProfile(orgProfile);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ACCESS_APPL_RESEND_ACTIVATION_MAIN_PAGE);
	}
	
	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		SelectItem[] items = null;
		if(getOrgTypeMap().get("somAccessApplOrgType") == null){
			ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
					PmParameterDesc.class, "somAccessApplOrgType");
			
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
			recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES_STATE.value());
			recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());
	
	
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), recommendList));
			if(searchBean != null){
				searchBean.getSortColumns();
				items = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			}			
			getOrgTypeMap().put("somAccessApplOrgType", items);
		}else{
			items = getOrgTypeMap().get("somAccessApplOrgType");
		}
		
		return items;
	}
	
	/**
	 * The method used for construct org lov condition fields.
	 * 
	 */
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getSelectedOrgType());
		sb.append("|recordStatus^1");
		return sb.toString();
	}

	/**
	 * The method used for cater ajax change event for org type select one menu
	 * 
	 */
	public void changeOrgType() {
		log.info("changeResOrgType");
		PmOrgProfile orgProfile = new PmOrgProfile();
		orgProfile.setPmOrgValidity(new PmOrgValidity());
		setSelectedOrgProfile(orgProfile);
	}
	
	/**
	 * The method used for request to resend access appl activation email
	 * 
	 */
	public void resendAccessApplActivationEmail(){
		log.info("resendAccessApplActivationEmail");
		IAccessApplSO accessApplSO = null;		
		try{
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			reqDTO.setIdentificationNo(getIdentificationNo());
			reqDTO.setOrgProfileId(getSelectedOrgProfile().getOrgProfileId());
			AccessApplResDTO resDTO = 
					accessApplSO.retrievePendingActivationAccessApplByIcNoAndOrgProfileId(reqDTO);
			if(resDTO.isSuccessful() && resDTO.getAccessAppl() != null){
				reqDTO = new AccessApplReqDTO();
				PmAccessAppl accessApp = resDTO.getAccessAppl();
				reqDTO.setAccessAppl(accessApp);
				reqDTO.setPortalBaseUrl(PmCommonUtil.getBasePortalUrl());
				reqDTO.setEmail(getEmail());
				resDTO = 
						accessApplSO.resendPendingActivationAccessAppl(reqDTO);
				if(resDTO.isSuccessful()){
					PmCommonUtil.redirectToPage(
							ACCESS_APPL_OUTPUT_MSG,
							MessageConst.INFO_776040,new String[] {getEmail() });
				}else{
					log.info("****Failed resending Activation Email******");
					PmCommonUtil.redirectToPage(
							ACCESS_APPL_OUTPUT_MSG,
							MessageConst.ERROR_721018,null);
				}
			}else{
				log.info("****Invalid Access Appl******");
				PmCommonUtil.redirectToPage(
						ACCESS_APPL_OUTPUT_MSG,
						MessageConst.ERROR_721018,null);
			}
		} catch (Exception ex) {
			PmCommonUtil.redirectToPage(
					ACCESS_APPL_OUTPUT_MSG,
					MessageConst.ERROR_721018,null);
			ex.printStackTrace();
		}finally{
			accessApplSO = null;
		}
		
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the identificationNo
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * @param identificationNo the identificationNo to set
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the selectedOrgType
	 */
	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType the selectedOrgType to set
	 */
	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the orgTypeMap
	 */
	public Map<String,SelectItem[]> getOrgTypeMap() {
		return orgTypeMap;
	}

	/**
	 * @param orgTypeMap the orgTypeMap to set
	 */
	public void setOrgTypeMap(Map<String,SelectItem[]> orgTypeMap) {
		this.orgTypeMap = orgTypeMap;
	}
	
	
	
	
}
