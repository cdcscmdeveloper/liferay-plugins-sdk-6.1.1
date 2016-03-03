package my.ep.web.backingbean.trackingdiary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.bean.pm.UnitOfMeasurmentSearchBean;
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodySearchBean;
import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "pmTrackingDiaryBackingBean")
@ViewScoped
public class PmTrackingDiaryBackingBean extends
		BaseBackingBean<PmTrackingDiaryManagedBean> {
	private static Logger log = Logger
			.getLogger(PmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_PM_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_PM_DETAIL_SEARCH = "pmTrackingDiaryDetailSearch.xhtml";
	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;
	// Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;

	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		return orgTypeSearchBean;
	}

	public void setOrgTypeSearchBean(
			ISearchBean<PmParameterDesc> orgTypeSearchBean) {
		this.orgTypeSearchBean = orgTypeSearchBean;
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	/**
	 * Update category level.
	 */
	public void updateCategoryLevel() {
		log.info("getManagedBean().getPmCategoryLevel() "
				+ getManagedBean().getPmCategoryLevel());
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrganisationSearchBean");
		OrganisationSearchBean orgSearch = new OrganisationSearchBean();
		this.getManagedBean().setOrganisationSearchBean(orgSearch);
		this.getManagedBean().resetOrgResultBean();
	}

	/**
	 * get zone list for dropdown
	 * 
	 */
	public List<PmZone> retrieveZones() {
		IZoneSO iZoneSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = iZoneSO.retrieveActiveZoneList();
		return resDTO.getZoneList();
	}

	/**
	 * reset search bean fields
	 */
	public void resetUserProfilesSearchBean() {
		log.info("resetUserProfilesSearchBean");
		this.getManagedBean().setUserProfileSearchBean(
				new UserProfilesSearchBean());
		this.getManagedBean().resetUserResultBean();
	}

	/**
	 * reset search bean fields
	 */
	public void resetUOMSearchBean() {
		log.info("resetUOMSearchBean");
		this.getManagedBean()
				.setUomSearchBean(new UnitOfMeasurmentSearchBean());
		this.getManagedBean().resetUOMResultBean();
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetUGSearchBean() {
		log.info("resetUGSearchBean");
		this.getManagedBean()
				.setUserGroupSearchBean(new UserGroupSearchBean());
		this.getManagedBean().resetUSGResultBean();
	}
	/**
	 * reset search bean fields
	 */
	public void resetEduSearchBean() {
		log.info("resetEduSearchBean");
		this.getManagedBean().setEduFieldName("");
		this.getManagedBean().resetEduResultBean();
	}

	/**
	 * reset search bean fields
	 */
	public void resetPcbSearchBean() {
		log.info("resetPcbSearchBean");
		this.getManagedBean().setPcbSearchBean(
				new ProfCertifiedBodySearchBean());
		this.getManagedBean().resetPcbResultBean();
	}

	/**
	 * reset search bean fields
	 */
	public void resetDocSearchBean() {
		log.info("resetDocSearchBean");
		this.getManagedBean().setSupportDocSearchBean(
				new SupportDocumentSearchBean());
		this.getManagedBean().resetDocResultBean();
	}
	
	public String getUserName(Long userId) {
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		if(userSO != null && resDTO.getPmUser() !=null){
			return resDTO.getPmUser().getUserName();
		}else{
			return "";
		}
		
	}
	/**
	 * reset search bean fields
	 */
	public void resetCategorySearchBean() {
		log.info("resetCategorySearchBean");
		this.getManagedBean().setCategoryCode("");
		this.getManagedBean().setCategoryDesc("");
		this.getManagedBean().setPmCategoryLevel(0);
		this.getManagedBean().resetCatResultBean();
	}
	/**
	 * reset search bean fields
	 */
	public void resetFOrgSearchBean() {
		log.info("resetFOrgSearchBean");
		this.getManagedBean().setFinancialOrgName("");
		this.getManagedBean().resetFOrgResultBean();
	}

	/**
	 * retrieve document category (parameterId)
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] documentCategoryOptions(boolean byId) {
		log.info("documentCategoryOptions");
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = getDocumentCategoryFilteredOptions();

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
			int i = 1;
			if (byId) {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterId(), parameterDesc.getCodeName());
				}
			} else {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	/**
	 * Get only applicable doc category (Technical Check List, Financial Check
	 * List, Supplier Registration)
	 * 
	 * @return
	 */
	private List<PmParameterDesc> getDocumentCategoryFilteredOptions() {
		log.info("getDocumentCategoryFilteredOptions");
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somDocCate");
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		bean.setSearchField("pmParameter.parameterCode");
		bean.setSearchValue(new ArrayList(Arrays
				.asList(ProfileManagementConst.SUPPORT_DOC_CATEGORY)));
		searchBean.getDataFilterBeans().add(bean);
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.DOCUMENT_CATEGORY, searchBean);
	}

	/**
	 * reset doc search fields
	 */
	public void docCategorySearchChange() {
		log.info("docCategorySearchChange");
		if (this.getManagedBean().getSupportDocSearchBean() != null) {
			if (ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(this
					.getManagedBean().getSupportDocSearchBean()
					.getDocCategoryParamCode())) {
				this.getManagedBean().getSupportDocSearchBean()
						.setProcureCatParam(null);
			} else if (ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST
					.equals(this.getManagedBean().getSupportDocSearchBean()
							.getDocCategoryParamCode())
					|| ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST
							.equals(this.getManagedBean()
									.getSupportDocSearchBean()
									.getDocCategoryParamCode())) {
				this.getManagedBean().getSupportDocSearchBean().setPmZone(null);
			}
		}
	}

	/**
	 * retrieve supplier zone
	 * 
	 * @return List<PmZone>
	 */
	public List<PmZone> supplierZoneOptions() {
		log.info("supplierZoneOptions");
		List<PmZone> zoneList = new ArrayList<PmZone>();
		try {
			IZoneSO reqSO = (IZoneSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IZoneSO.class);
			ZoneResDTO resDTO = reqSO.retrieveActiveZoneList();
			if (resDTO.isSuccessful()) {
				zoneList = resDTO.getZoneList();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770065,
					new String[] { e.getMessage() });
		}
		return zoneList;
	}
	/**
	 * Generate report based on doc type.
	 *
	 * @param docType the doc type
	 * @param docNo the doc no
	 */
	public void generateReport(String docType, String docId) {
		Map<String, String> paramMap = new HashMap<String, String>();
		log.info("......docType="+docType);
		log.info("......docId="+docId);
		paramMap.put(ReportConst.PARAM_DOC_ID, docId);
		paramMap.put(ReportConst.PARAM_DOC_TYPE, docType);
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		String reportName = ReportConst.PM_TRACKING_DAIRY_REPORT_NAME;
		
		if(docType !=null && docId !=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}
	
	public void redirectToMainPage(){
		log.info(".....redirectToMainPage.....");
		log.info("............this.getManagedBean().getBackURL()"+this.getManagedBean().getBackURL());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, this.getManagedBean().getBackURL()); 
//		return this.getManagedBean().getBackURL();
	}
	/**
	 * Get Role description by role code and language.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @param languageCode
	 *            the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode) {
		log.info("getRoleDescByRoleCode");
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}

	
	public void redirectToTrackingDiary(String docType, Long docId)  throws IOException,PortalException, SystemException{
		System.out.println("...........redirectToTrackingDiary............");
		System.out.println("...........docType:"+docType);
		System.out.println("...........docId:"+docId);

		//search and redirect to trackingDiaryView.xhtml
		this.getManagedBean().setSelectedDocType(docType);
		this.getManagedBean().setSelectedDocId(docId);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		log.info("fc.getViewRoot().getViewId()="+fc.getViewRoot().getViewId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();

		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "/xhtml/trackingdiary/trackingDiaryView.xhtml");

		
	}
	public void onFunctionSelect() {
		System.out.println("............onFunctionSelect..........this.getManagedBean().getFunctionType()...."+this.getManagedBean().getFunctionType());

		if (this.getManagedBean().getFunctionType() != null) {
			if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase(ParameterConst.ORG_PROFILE_PARAM_CODE)) {
				resetOrganisationSearchBean();
				retrieveOrgTypeMap();
				this.getManagedBean().setOrgProfileRequired(true);
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase(ParameterConst.USER_PROFILE_PARAM_CODE)) {
				resetUserProfilesSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("VC")) {
				this.getManagedBean().getZoneList().addAll(retrieveZones());
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("UO")) {
				resetUOMSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("EF")) {
				resetEduSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("PB")) {
				resetPcbSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("SD")) {
				resetPcbSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("FO")) {
				resetFOrgSearchBean();
			}else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("UG")) {
				resetUGSearchBean();
			}else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("CC")) {
				resetCategorySearchBean();
			}
		}

	}
	
	
	public void onTransactionTypeSelect() {
		System.out.println("............onTransactionTypeSelect..........this.getManagedBean().getTransactionType...."+this.getManagedBean().getTransactionType());

		if (this.getManagedBean().getTransactionType() != null) {
//			if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.ORG_PROFILE_PARAM_CODE)) {
//				resetOrganisationSearchBean();
//				retrieveOrgTypeMap();
//				this.getManagedBean().setOrgProfileRequired(true);
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.USER_PROFILE_PARAM_CODE)) {
//				resetUserProfilesSearchBean();
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.VIRTUAL_CERTIFICATE_PARAM_CODE)) {
////				this.getManagedBean().getZoneList().addAll(retrieveZones());
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.UNIT_OF_MEASUREMENT_PARAM_CODE)) {
//				resetUOMSearchBean();
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.EDU_PARAM_CODE)) {
//				resetEduSearchBean();
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.PROFESSIONAL_CERT_BODY)) {
//				resetPcbSearchBean();
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.SUPPORTING_DOC_PARAM_CODE)) {
//				resetDocSearchBean();
//			} else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.FINANCIAL_ORG_PARAM_CODE)) {
//				resetFOrgSearchBean();
//			}else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.USER_GROUP_PARAM_CODE)) {
//				resetUGSearchBean();
//			}else if (this.getManagedBean().getTransactionType()
//					.equalsIgnoreCase(ParameterConst.CATEGORY_CODE_PARAM_CODE)) {
//				resetCategorySearchBean();
//			}
		}

	}
	public String goToOrgTrackingDiarySearch(PmOrgProfile orgProfile) {
		log.debug("**********goToOrgTrackingDiarySearch************"
				+ orgProfile.getOrgProfileId());
		getManagedBean().setSelectedDocType(
				ParameterConst.ORG_PROFILE_PARAM_CODE);
		getManagedBean().setSelectedDocId(orgProfile.getOrgProfileId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToUserTrackingDiarySearch(PmUser user) {
		log.debug("**********goToOrgTrackingDiarySearch************"
				+ user.getUserId());
		getManagedBean().setSelectedDocType(
				ParameterConst.USER_PROFILE_PARAM_CODE);
		getManagedBean().setSelectedDocId(user.getUserId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToUOMTrackingDiarySearch(PmUom uom) {
		log.debug("**********goToUOMTrackingDiarySearch************"
				+ uom.getUomId());
		getManagedBean().setSelectedDocType(
				ParameterConst.UNIT_OF_MEASUREMENT_PARAM_CODE);
		getManagedBean().setSelectedDocId(uom.getUomId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	
	public String goToCategoryTrackingDiarySearch(Long id, int catLevel) {
		log.debug("**********goToCategoryTrackingDiarySearch************"
				+"..id="+ id +"...level="+catLevel);
		if(catLevel == 1){
		getManagedBean().setSelectedDocType(
				ParameterConst.MAIN_CATEGORY_PARAM_CODE);
		}else if(catLevel == 2){
			getManagedBean().setSelectedDocType(
					ParameterConst.SUB_CATEGORY_PARAM_CODE);
		}else if(catLevel == 3){
			getManagedBean().setSelectedDocType(
					ParameterConst.SUB_DIVISION_CATEGORY_PARAM_CODE);
		}
		
		getManagedBean().setSelectedDocId(id);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	public String goToUSGTrackingDiarySearch(PmUserGroup userGroup) {
		log.debug("**********goToUSGTrackingDiarySearch************"
				+ userGroup.getUserGroupId());
		getManagedBean().setSelectedDocType(
				ParameterConst.USER_GROUP_PARAM_CODE);
		getManagedBean().setSelectedDocId(userGroup.getUserGroupId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	public String goToEduTrackingDiarySearch(PmEduField edu) {
		log.debug("**********goToEduTrackingDiarySearch************"
				+ edu.getEduFieldId());
		getManagedBean().setSelectedDocType(ParameterConst.EDU_PARAM_CODE);
		getManagedBean().setSelectedDocId(edu.getEduFieldId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToPcbTrackingDiarySearch(PmCertBody pcb) {
		log.debug("**********goToPcbTrackingDiarySearch************"
				+ pcb.getCertBodyId());
		getManagedBean().setSelectedDocType(
				ParameterConst.PROFESIONAL_BODIES_PARAM_CODE);
		getManagedBean().setSelectedDocId(pcb.getCertBodyId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToDocTrackingDiarySearch(PmDocument doc) {
		log.debug("**********goToPcbTrackingDiarySearch************"
				+ doc.getDocumentId());
		getManagedBean().setSelectedDocType(
				ParameterConst.SUPPORTING_DOC_PARAM_CODE);
		getManagedBean().setSelectedDocId(doc.getDocumentId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToRuleConfigTrackingDiarySearch() {
		log.info("**********goToRuleConfigTrackingDiarySearch************");
		getManagedBean().setSelectedDocType2(null);
		getManagedBean().setMultiRuleType(false);
		getManagedBean().setSelectedDocType(
				ParameterConst.RULE_CONFIG_PARAM_CODE);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToFOrgTrackingDiarySearch(PmFinancialOrg org) {
		log.debug("**********goToFOrgTrackingDiarySearch************"
				+ org.getFinancialOrgId());
		getManagedBean().setSelectedDocType(
				ParameterConst.FINANCIAL_ORG_PARAM_CODE);
		getManagedBean().setSelectedDocId(org.getFinancialOrgId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToFeeSettTrackingDiarySearch() {
		log.info("**********goToFeeSettTrackingDiarySearch************");
		getManagedBean().setSelectedDocType2(null);
		getManagedBean().setMultiRuleType(false);
		getManagedBean().setSelectedDocType(
				ParameterConst.FEE_EXEMPTION_PARAM_CODE);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String goToThresholdTrackingDiarySearch() {
		log.info("**********goToThresholdTrackingDiarySearch************");
		getManagedBean().setSelectedDocType(
				ParameterConst.UNPLANDED_THRESHOLD_PARAM_CODE);
		getManagedBean().setSelectedDocType2(
				ParameterConst.BUDGET_VS_SPENT_THRESHOLD_PARAM_CODE);
		getManagedBean().setMultiRuleType(true);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
		// ///

		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String getTrackingForTheZone(PmZone zone) {
		log.info("**********getTrackingForTheZone************");
		if (zone.getZoneId() != null) {

			getManagedBean().setSelectedDocType(
					ParameterConst.VIRTUAL_CERTIFICATE_PARAM_CODE);
			getManagedBean().setSelectedDocType2(null);
			getManagedBean().setMultiRuleType(false);
			getManagedBean().setSelectedDocId(zone.getZoneId());
			getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
			// ///

			return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
		}
		return "";
	}

	public String backToPrev() {

		return NAV_TO_PM_SEARCH + "?faces-redirect=true";
	}

	/**
	 * retrieve and set org type mapper
	 */
	private void retrieveOrgTypeMap() {
		log.info("retrieveOrgTypeMap");
		List<PmParameterDesc> parameterDescs = getOrgTypeList();
		orgTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * Retrieve Org. Type from PM
	 * 
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getOrgTypeList() {
		log.info("getOrgTypeList");
		return PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.ORGANISATION_TYPES,
						new ArrayList(
								Arrays.asList(ProfileManagementConst.PM_TRACKING_DIARY_ORG_TYPE_EXCLUDE)),
						null);
	}

	public void searchUserOrgProfile() {
		log.info("searchUserOrgProfile");
		this.getManagedBean().getUserOrgDataModel().reloadData();
	}
	public void searchTrackingDiaryByDocType(String docType){
		log.debug("**********searchTrackingDiaryByDocType************"
				+ docType);
	
		getManagedBean().setSelectedDocType(docType);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();
	}
	public void searchUserGroup() {
		log.info("searchUserGroup");
		this.getManagedBean().getUserGroupDataModel().reloadData();
	}
	
	public void searchUserProfile() {
		log.info(",,,searchUserProfile");
		if ((getManagedBean().getUserProfileSearchBean().getIdentificationNo() == null || getManagedBean()
				.getUserProfileSearchBean().getIdentificationNo().isEmpty())
				&& (getManagedBean().getUserProfileSearchBean().getUserName() == null
				|| getManagedBean().getUserProfileSearchBean().getUserName()
						.isEmpty())){
			throw new EpException("please input UserName or Identification No.");
		}
			
		this.getManagedBean().getUserDataModel().reloadData();
	}

	public void searchUnitOfMeasurement() {
		log.info(",,,searchUnitOfMeasurement");
		if ((getManagedBean().getUomSearchBean().getUOMCode() == null || getManagedBean()
				.getUomSearchBean().getUOMCode().isEmpty())
				&& (getManagedBean().getUomSearchBean().getUOMName() == null
				|| getManagedBean().getUomSearchBean().getUOMName()
						.isEmpty())){
			throw new EpException("please input UOM Code or UOM Name.");
		}
		this.getManagedBean().getUomDataModel().reloadData();
	}
	public void searchCategoryCode() {
		log.info(",,,searchCategoryCode");
		this.getManagedBean().getCategoryDataModel().reloadData();
	}
	

	public void searchEducationOfField() {
		log.info(",,,searchEducationOfField");
		this.getManagedBean().getEduDataModel().reloadData();
	}

	public void searchSupportingDocument() {
		log.info("searchDocument - Start");
		if (this.getManagedBean().getSupportDocSearchBean()
				.getDocCategoryParamCode() == null
				|| StringUtils.isBlank(this.getManagedBean()
						.getSupportDocSearchBean().getDocCategoryParamCode())) {
			throw new EpException(MessageConst.ERROR_770083);
		}
		this.getManagedBean().getPmDocumentDataModel().reloadData();
		log.info("searchDocument - End");
	}

	public void searchProfesionalCertified() {
		log.info(",,,searchProfesionalCertified");
		this.getManagedBean().getPcbDataModel().reloadData();
	}

	public void searchFinancialOrg() {
		log.info(",,,searchFinancialOrg");
		this.getManagedBean().getFinancialOrgDataModel().reloadData();
	}

	/**
	 * change organization type
	 */
	public void changeOrgType() {

		log.info("changeOrgType");
		if (this.getManagedBean().getOrganisationSearchBean() != null) {

			if (this.getManagedBean().getOrganisationSearchBean()
					.getOrgProfile() == null) {
				this.getManagedBean().getOrganisationSearchBean()
						.setOrgProfile(new PmOrgProfile());
			}
			//
			// this.getManagedBean().getOrganisationSearchBean().getOrgProfile()
			// .setOrgName("");

			if (this.getManagedBean().getOrganisationSearchBean().getOrgType()
					.getParameterId() != null) {
				System.out
						.println("...........this.getManagedBean().getOrganisationSearchBean().getOrgType().getParameterId()="
								+ this.getManagedBean()
										.getOrganisationSearchBean()
										.getOrgType().getParameterId());
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
						.getManagedBean().getOrganisationSearchBean()
						.getOrgType().getParameterId());
				System.out.println("...........pmParameter" + pmParameter);
				System.out.println("...........pmParameter.getParameterCode()"
						+ pmParameter.getParameterCode());
				System.out.println("...........pmParameter.getParameterCode()"
						+ pmParameter.getParameterId());
				this.getManagedBean().getOrganisationSearchBean().getOrgType()
						.setParameterCode(pmParameter.getParameterCode());

				if (ParameterConst.ORG_TYPE_SM_BPK_HQ.equals(pmParameter
						.getParameterCode())
						|| ParameterConst.ORG_TYPE_SM_PMS_SABAH
								.equals(pmParameter.getParameterCode())
						|| ParameterConst.ORG_TYPE_SM_PMS_SARAWAK
								.equals(pmParameter.getParameterCode())
						|| ParameterConst.ORG_TYPE_BPK_HQ.equals(pmParameter
								.getParameterCode())
						|| ParameterConst.ORG_TYPE_PMS_SABAH.equals(pmParameter
								.getParameterCode())
						|| ParameterConst.ORG_TYPE_PMS_SARAWAK
								.equals(pmParameter.getParameterCode())) {
					this.getManagedBean().setOrgProfileRequired(false);
					// default org profile
					PmOrgValidity pmOrgValidity = getOrgProfile(pmParameter
							.getParameterCode());
					this.getManagedBean()
							.getOrganisationSearchBean()
							.getOrgProfile()
							.setOrgProfileId(
									pmOrgValidity.getPmOrgProfile()
											.getOrgProfileId());
					this.getManagedBean().getOrganisationSearchBean()
							.getOrgProfile()
							.setOrgName(pmOrgValidity.getOrgName());
				} else {
					this.getManagedBean().setOrgProfileRequired(true);
				}
			}
		}
	}

	/**
	 * retrieve valid org profile based on selected org type
	 * 
	 * @param paramCode
	 * @return PmOrgValidity
	 */
	private PmOrgValidity getOrgProfile(String paramCode) {
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();

		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("orgTypeCode.parameterCode");
		bean.setSearchValue(paramCode);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmOrgValidity.recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);

		PmOrgValidity pmOrgValidity = new PmOrgValidity();

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = null;
		reqDTO.setOrgType(paramCode);
		// reqDTO.setOrgProfileSearchBean(searchBean);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		resDTO = orgProfileSO.retrieveValidOrgProfile(reqDTO);
		if (resDTO.isSuccessful()) {
			pmOrgValidity = resDTO.getPmOrgValidity();
			// System.out.println("size>>>>" +
			// resDTO.getOrgProfileResultBean().getResultList().size());
			// searchOrgProfile
			// for(PmOrgProfile
			// temp:resDTO.getOrgProfileResultBean().getResultList()){
			// System.out.println(temp.getPmOrgValidity().getOrgName());
			// }
		}
		return pmOrgValidity;

	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getOrganisationSearchBean().getOrgType()
				.getParameterId());
		sb.append("|recordStatus^1");
		// if(OrganizationType.MINISTRY.value()
		// .equals(getManagedBean().getOrganisationSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }else if(OrganizationType.DEPARTMENT.value()
		// .equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else
		// if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }else if(OrganizationType.DIVISION.value()
		// .equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else
		// if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }

		return sb.toString();
	}

}
