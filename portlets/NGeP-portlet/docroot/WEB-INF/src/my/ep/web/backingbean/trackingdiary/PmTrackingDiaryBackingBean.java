package my.ep.web.backingbean.trackingdiary;

import java.math.BigDecimal; //#25790
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
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.ContinuityDPConst;//#25790
import my.ep.constant.fl.FulfilmentStatusConst;//#25790
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;//#25790
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;//#25790
import my.ep.exception.EpException;
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
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;//#25790
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.ProfCertifiedBodySearchBean;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.bean.pm.UnitOfMeasurmentSearchBean;
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

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
		if(userId.longValue()==CommonConstants.TRACKING_DIARY_SYSTEM_USER_ID) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		if(userSO != null && resDTO.getPmUser() !=null){
			return resDTO.getPmUser().getUserName();
		} else {
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
		// Start Fix - #36608
		if (getManagedBean().getSelectedDocNo() != null) {
			paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getSelectedDocNo());
		} else {
			paramMap.put(ReportConst.PARAM_DOC_NO, ReportConst.EMPTY_VAL);
		}
		// End Fix - #36608
		//START -#Bug:28933,#Bug:28934,#Bug:28985,#Bug:28983
		//String reportName = ReportConst.PM_TRACKING_DAIRY_REPORT_NAME;
		String reportName = getTrackingDiaryReportName(docType,docId);
		if(docType !=null && docId !=null & reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
		//END- #Bug:28933,#Bug:28934,#Bug:28985,#Bug:28983
	}
	
	public void redirectToMainPage(){
		log.info(".....redirectToMainPage.....");
		log.info("............this.getManagedBean().getBackURL()"+this.getManagedBean().getBackURL());
		
		// 25791 -start-
		if (this.getManagedBean().isBiddingUrl()) {
			this.getManagedBean().setBackURL(QTConstants.QT_MAINTENANCE_BIDDING_MANAGEMENT_VIEW);
			this.getManagedBean().setBiddingUrl(Boolean.FALSE);
		}
		// 25791 -end-
		// Start Fix - #36608
		if (getManagedBean().getSelectedDocNo() != null) {
			getManagedBean().setSelectedDocNo(null);
		}
		// End Fix - #36608
		FacesContext fc = FacesContext.getCurrentInstance();		
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, this.getManagedBean().getBackURL()); 
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
		if(roleCode==null || roleCode.isEmpty()) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}
	
	/**
	 * Fix - #36608
	 * Add docNo / circular no for data from circular
	 * @param docType
	 * @param docId
	 * @param docNo
	 * @throws IOException
	 * @throws PortalException
	 * @throws SystemException
	 */
	public void redirectToTrackingDiaryForCircularContract(String docType, Long docId, String docNo)  throws IOException,PortalException, SystemException{
		getManagedBean().setSelectedDocNo(docNo);
		redirectToSupplierTrackingDiary(docType, docId, false);
	}
	
	public void redirectToTrackingDiary(String docType, Long docId)  throws IOException,PortalException, SystemException{
		redirectToSupplierTrackingDiary(docType, docId, false);
	}
	
	public void redirectToSupplierTrackingDiary(String docType, Long docId, boolean isFilterBySupplier)  throws IOException,PortalException, SystemException{
		log.info("redirectToSupplierTrackingDiary - start:docType=" + docType + " docId=" + docId + " isFilterBySupplier="+ isFilterBySupplier);
		setTrackingDiaryParam(docType, docId, isFilterBySupplier);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		log.info("fc.getViewRoot().getViewId()="+fc.getViewRoot().getViewId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();

		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "/xhtml/trackingdiary/trackingDiaryView.xhtml");
	}
	
	public void setTrackingDiaryParam(String docType, Long docId, boolean isFilterBySupplier){
		this.getManagedBean().setSelectedDocType(docType);
		this.getManagedBean().setSelectedDocId(docId);
		this.getManagedBean().setFilterBySupplier(isFilterBySupplier);
	}
	
	public void onFunctionSelect() {
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
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("UG")) {
				resetUGSearchBean();
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase("CC")) {
				resetCategorySearchBean();
			}
		}
	}
	
	public void onTransactionTypeSelect() {
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
		
			if (this.getManagedBean().getOrganisationSearchBean().getOrgType()
					.getParameterId() != null) {
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
						.getManagedBean().getOrganisationSearchBean()
						.getOrgType().getParameterId());
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
		}
		return pmOrgValidity;
	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getOrganisationSearchBean().getOrgType()
				.getParameterId());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	/**
	 * START - #Bug:28933,#Bug:28934,#Bug:28985,#Bug:28983
	 * Function : To get tracking diary report name based on the document type/module
	 * @param docType , document type
	 * @return Tracking diary report name
	 */
	private String getTrackingDiaryReportName(String docType, String docId){
		String reportName = null;
		List<String> flDocTypeList = Arrays.asList(
				ParameterConst.DOCTYPE_CONTRACT_ORDER,ParameterConst.DOCTYPE_CONTRACT_REQUEST,ParameterConst.DOCTYPE_DELIVERY_ORDER,
				ParameterConst.DOCTYPE_FULFILMENT_NOTE,ParameterConst.DOCTYPE_INVOICE,ParameterConst.DOCTYPE_STOP_DELIVERY,
				ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE,ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE,
				ParameterConst.DOCTYPE_PAYMENT_ADVICE,ParameterConst.DOCTYPE_PURCHASE_ORDER,ParameterConst.DOCTYPE_PURCHASE_REQUEST);
		
		List<String> dpDocTypeList = Arrays.asList(
				ParameterConst.DOCTYPE_SIMPLE_QUOTE,ParameterConst.DOCTYPE_REQUEST_NOTE,ParameterConst.DOCTYPE_PURCHASE_INQUIRY,
				ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
		
		List<String> qtDocTypeList = Arrays.asList(ParameterConst.DOCTYPE_QUOTATION,ParameterConst.DOCTYPE_BIDDING,
				ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		List<String> contractDocTypeList = Arrays.asList("MC","CC");
		List<String> ppDocTypeList = Arrays.asList(ParameterConst.DOCTYPE_PLAN_PROCUREMENT,ParameterConst.DOCTYPE_PLAN_EXPENDITURE);
		List<String> circularDocTypeList = Arrays.asList(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR,ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		List<String> pmDocTypeList = Arrays.asList(ParameterConst.DOCTYPE_COMPANY_PROFILE, ParameterConst.DOCTYPE_MARKET_RESEARCH);
		
		if(flDocTypeList.contains(docType)){//Fulfilment
			reportName = ReportConst.FL_TRACKING_DIARY;
		}else if(contractDocTypeList.contains(docType)){//Contract 
			reportName = ReportConst.CT_TRACKING_DIARY;
		}else if(circularDocTypeList.contains(docType)){//Circular Search
			reportName = ReportConst.CT_CIRCULAR_TRACKING_DIARY;
		}else if(dpDocTypeList.contains(docType)){//Direct Purchase
			reportName = ReportConst.DP_TRACKING_DIARY;
		}else if(qtDocTypeList.contains(docType)){//Quotation/Tender
			reportName = ReportConst.QT_TRACKING_DIARY;
		}else if(ppDocTypeList.contains(docType)){//Procurement Plan
			reportName = ReportConst.PP_TRACKING_DIARY;
		}else if(pmDocTypeList.contains(docType)){//Profile Management
			reportName = ReportConst.PM_TRACKING_DIARY;
		}else if(ParameterConst.DOCTYPE_SUPPLIER_APPLICATION.equalsIgnoreCase(docType)){//Supplier Application
			reportName = ReportConst.SM_APPL_TRACKING_DIARY;
		}else if("CD".equalsIgnoreCase(docType)){//Codification
			reportName = ReportConst.CD_TRACKING_DIARY;
		}else if("CM".equalsIgnoreCase(docType)){//Catalogue
			reportName = ReportConst.CM_TRACKING_DIARY;
		}else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType)){//LOA
			log.info("INSIDE DOC TYPE = LA ");
			ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
			try{
				String docTypeSourcingForLOA = trackingDiarySO.getTrackingDiaryDocTypeSourcingForLOAByDocId(Long.valueOf(docId));
				log.info("docTypeSourcingForLOA == >> "+docTypeSourcingForLOA);
				if("PR".equalsIgnoreCase(docTypeSourcingForLOA)){
					reportName = ReportConst.DP_TRACKING_DIARY;
				}else{//QT or NG(Direct LOA)
					reportName = ReportConst.QT_TRACKING_DIARY;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return reportName;
	}
	//END - #Bug:28933,#Bug:28934,#Bug:28985,#Bug:28983
	// #25790 - start
	/**
	 * Redirects to the Tracking Diary page after linking to the purchase
	 * request and setting parameters.
	 * 
	 * @param docType
	 *            Type of document (e.g., "PR").
	 * @param docId
	 *            Document ID.
	 * @param isFilterBySupplier
	 *            Whether to filter by supplier.
	 * @param purchaseRequestMapList
	 *            Purchase request data.
	 */
	public void redirectToRnContinuityTrackingDiary(String docType, Long docId, boolean isFilterBySupplier,
			List<Map<String, String>> purchaseRequestMapList) {

		linkRnWithPrTrackingDiary(docType, docId, purchaseRequestMapList);
		setTrackingDiaryParam(docType, docId, isFilterBySupplier);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		log.info("fc.getViewRoot().getViewId()=" + fc.getViewRoot().getViewId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/trackingdiary/trackingDiaryView.xhtml");
	}

	/**
	 * Links a Purchase Request (PR) to a Tracking Diary.
	 * 
	 * This method iterates over a list of purchase requests and updates the
	 * corresponding Tracking Diary with the provided document type and document
	 * ID if they are not already set.
	 * 
	 * @param docType
	 *            The document type (e.g., "PR").
	 * @param docId
	 *            The document ID to link with the Tracking Diary.
	 * @param purchaseRequestMapList
	 *            A list of maps containing purchase request details.
	 */
	private void linkRnWithPrTrackingDiary(String docType, Long docId, List<Map<String, String>> purchaseRequestMapList) {
		ITrackingDiarySO trackingDiarySO = BusinessDelegate.getInstance()
				.getServiceObject("cc", ITrackingDiarySO.class);

		for (Map<String, String> map : purchaseRequestMapList) {
			TrackingDiaryReqDTO trackDiaryReqDTO = new TrackingDiaryReqDTO();

			String docNo = map.get(ContinuityDPConst.getPrNo());
			trackDiaryReqDTO.setStatusId(new BigDecimal(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION));
			trackDiaryReqDTO.setRoleCode(null);
			trackDiaryReqDTO.setDocNo(docNo);

			TrackingDiaryResDTO trackDiaryResDTO = trackingDiarySO
					.retrieveByDocNoAndStatusIdAndRoleCode(trackDiaryReqDTO);
			if (trackDiaryResDTO.getPmTrackingDiary() != null && trackDiaryResDTO.isSuccessful()) {
				PmTrackingDiary trackDiary = trackDiaryResDTO.getPmTrackingDiary();
				trackDiary.setGroupDocType(docType);
				trackDiary.setGroupId(docId);
				trackDiaryReqDTO.setPmTrackingDiary(trackDiary);
				trackingDiarySO.updateTrackingDiary(trackDiaryReqDTO);
			}
		}
	}

	/**
	 * Redirects to the Tracking Diary page after linking PO to the purchase
	 * request and setting tracking parameters.
	 * 
	 * @param docType
	 *            Type of document (e.g., "PO").
	 * @param docId
	 *            Document ID.
	 * @param purchaseRequestNo
	 *            Purchase request number.
	 */
	public void redirectToPoContinuityTrackingDiary(String docType, Long docId, String purchaseRequestNo) {

		linkPoWithPrTrackingDiary(docType, docId, purchaseRequestNo);
		setTrackingDiaryParam(docType, docId, false);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		log.info("fc.getViewRoot().getViewId()=" + fc.getViewRoot().getViewId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/trackingdiary/trackingDiaryView.xhtml");
	}

	/**
	 * Links a purchase order (PO) to a purchase request in the Tracking Diary.
	 * Retrieves the tracking diary entry for the given purchase request number
	 * and updates it with the provided PO document type and ID if a valid entry
	 * is found.
	 * 
	 * @param docType
	 *            The type of document (e.g., "PO").
	 * @param docId
	 *            The ID of the document to link.
	 * @param purchaseRequestNo
	 *            The purchase request number to associate with the PO in the
	 *            Tracking Diary.
	 */
	private void linkPoWithPrTrackingDiary(String docType, Long docId, String purchaseRequestNo) {
		ITrackingDiarySO trackingDiarySO = BusinessDelegate.getInstance()
				.getServiceObject("cc", ITrackingDiarySO.class);

		TrackingDiaryReqDTO trackDiaryReqDTO = new TrackingDiaryReqDTO();
		trackDiaryReqDTO.setStatusId(new BigDecimal(FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION));
		trackDiaryReqDTO.setRoleCode(null);
		trackDiaryReqDTO.setDocNo(purchaseRequestNo);

		TrackingDiaryResDTO trackDiaryResDTO = trackingDiarySO.retrieveByDocNoAndStatusIdAndRoleCode(trackDiaryReqDTO);
		if (trackDiaryResDTO.getPmTrackingDiary() != null && trackDiaryResDTO.isSuccessful()) {
			PmTrackingDiary trackDiary = trackDiaryResDTO.getPmTrackingDiary();
			trackDiary.setGroupDocType(docType);
			trackDiary.setGroupId(docId);
			trackDiaryReqDTO.setPmTrackingDiary(trackDiary);
			trackingDiarySO.updateTrackingDiary(trackDiaryReqDTO);
		}

	}
	// #25790 - end
}
