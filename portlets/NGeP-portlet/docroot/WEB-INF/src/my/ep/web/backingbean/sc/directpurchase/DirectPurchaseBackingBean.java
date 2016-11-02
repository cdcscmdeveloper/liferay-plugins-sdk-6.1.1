/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.backingbean.trackingdiary.TrackingDiarySearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sc.directpurchase.DirectPurchaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.springframework.util.CollectionUtils;
// TODO: Auto-generated Javadoc

/**
 * The Class DirectPurchaseBackingBean.
 *
 * @author Sri
 * @since 29/12/2015
 */
@ManagedBean(name = "directPurchaseBackingBean")
@RequestScoped
public class DirectPurchaseBackingBean extends SourcingBaseBackingBean<DirectPurchaseManagedBean> {
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	/** The direct purchase search page nav. */
	private String DIRECT_PURCHASE_SEARCH_PAGE_NAV = "directPurchaseSearch";

	/**
	 * Pre render direct purchase search.
	 */
	public void preRenderDirectPurchaseSearch() {
		debugLogger("preRenderDirectPurchaseSearch");
		FacesContext fc = FacesContext.getCurrentInstance();
		// reset search info
		reset();
		
		if(isGovernment()){
			getDocumentTypes(Boolean.TRUE);
			getManagedBean().setGovernment(Boolean.TRUE);
			
			// show default Ministry, Kumpulan PTJ and PTJ
			getDefaultPtjAndMinistryValues();

		} else if(isSupplier()){
			getDocumentTypes(Boolean.FALSE);
			getManagedBean().setGovernment(Boolean.FALSE);
		}
		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null, DIRECT_PURCHASE_SEARCH_PAGE_NAV);
	}
	
	
	/**
	 * Checks if is government.
	 *
	 * @return true, if is government
	 */
	private boolean isGovernment(){
		if(getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())  // Government 
				|| getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())){

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Checks if is supplier.
	 *
	 * @return true, if is supplier
	 */
	public boolean isSupplier() {
		if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()) // supplier and govt seller
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())){

			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Gets the document types.
	 *
	 * @param isGovernament the is governament
	 * @return the document types
	 */
	private void getDocumentTypes(boolean isGovernament) {
		List<SelectItem> items = null;
		List<String> docTypeList = null;
		try {
			items = new ArrayList<SelectItem>();
			docTypeList = new ArrayList<String>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));

			docTypeList.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
			docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
			if(isGovernament){
				docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
			}
			docTypeList.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
			docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);

			for (String docType : docTypeList) {
				items.add(new SelectItem(docType, ResourceBundleUtils.getMessage(
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		getManagedBean().setDocumentTypes(items);
	}

	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public List<SelectItem> searchStatusDesc() throws NamingException {
		List<SelectItem> items = null;

		try {
			items = new ArrayList<SelectItem>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));

			List<PmStatusDesc> statusDescs = StatusHelper.getInstance().getStatusDescList(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), getManagedBean().getSearchDocumentType());
			for (PmStatusDesc statusDesc : statusDescs) {
				items.add(new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName()));
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
	 * Search.
	 */
	public void search() {
		debugLogger("DpTrackingDiaryBackingBean search");
		if ((null == getManagedBean().getSearchDocumentType())) {
			throw new EpException(MessageConst.ERROR_776101);
		} else{
			if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
				getManagedBean().setDocumentTypeDesc(ParameterConst.SQ_TITLE);
			}else if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_PURCHASE_INQUIRY)){
				getManagedBean().setDocumentTypeDesc(ParameterConst.PI_TITLE);
			}else if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)){
				getManagedBean().setDocumentTypeDesc(ParameterConst.PD_TITLE);
			}else if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_REQUEST_NOTE)){
				getManagedBean().setDocumentTypeDesc(ParameterConst.RN_TITLE);
			}else if(getManagedBean().getSearchDocumentType().equals(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
				getManagedBean().setDocumentTypeDesc(ParameterConst.LA_TITLE);
			}
		}
		getManagedBean().getDpDocumentDataModel().reloadData();
	}


	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {
		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {
			Object[] dpObj = getManagedBean().getDpObj();
			String docType = (String) dpObj[3];
			Long docId = (Long) dpObj[0];

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			List<Object[]> parents = getParentDocument(docType, docId);
			if(!CollectionUtils.isEmpty(parents)) {
				for(Object[] parent : parents) {
					BaseSearchFilterBean multiFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), parent[0]), SearchCondition.CONDITION_OR.value(), 
							new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), parent[1]));
					searchBean.getDataFilterBeans().add(multiFilterBean);
				}
			}

			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resDTO.getResultBean();
		}
	}

	/**
	 * View doc.
	 *
	 * @param dpObj the dp obj
	 */
	public void viewDoc(Object[] dpObj){
		debugLogger("viewDoc");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];

		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);

		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_NOTE)){
			RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
			rnBackingBean.viewRequestNote(docId);
			rnBackingBean.getManagedBean().setDpObj(dpObj);
			rnBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_INQUIRY)){
			PurchaseInquiryBackingBean prInqBackingBean = new PurchaseInquiryBackingBean();
			prInqBackingBean.viewPurchaseInquiry(docId);
			prInqBackingBean.getManagedBean().setDpObj(dpObj);
			prInqBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)){
			PurchaseInDefaultBackingBean pidBackingBean = new PurchaseInDefaultBackingBean();
			pidBackingBean.viewPurchaseInDefault(docId);
			pidBackingBean.getManagedBean().setDpObj(dpObj);
			pidBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
			SimpleQuoteBackingBean sqBackingBean = new SimpleQuoteBackingBean();
			sqBackingBean.viewQuote(docId);
			sqBackingBean.getManagedBean().setDpObj(dpObj);
			sqBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.viewLOA(docId);
			loaDetailBackingBean.getManagedBean().setDpObj(dpObj);
			loaDetailBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);
		}
	}
	
	/**
	 * Generate report.
	 *
	 * @param dpObj the dp obj
	 */
	public void generateReport(Object[] dpObj){
		debugLogger("generateReport");
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		
		Map<String, String> paramMap = new HashMap<String, String>();
		String reportName = null;
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_NOTE)){
			reportName = ReportConst.DP_REQUEST_NOTE_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_RN_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_STATUS);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_INQUIRY)){
			reportName = ReportConst.DP_ITEM_INQUIRY_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_PI_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_STATUS);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)){
			reportName = ReportConst.DP_PID_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_PID_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_S1_PURCHASE_DEFAULT_DTLS_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_PID_GROUP);
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
			reportName = ReportConst.DP_SIMPLE_QUOTE_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_QUOTE_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL);
			paramMap.put(ReportConst.PARAM_GROUP, ReportConst.PARAM_QUOTE_GROUP);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
			reportName = ReportConst.DP_LOA_DTL_REPORT_NAME;
			paramMap.put(ReportConst.PARAM_LOA_NO, docNo);
			paramMap.put(ReportConst.PARAM_REPORT_ID, ReportConst.SC_S1_LOA_RPT_VAL);
		}
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		
		if(reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}

	/**
	 * Reset direct purchase search.
	 */
	public void reset(){
		this.getManagedBean().setSearchDocumentNo(null);
		this.getManagedBean().setSearchDocumentType(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setSearchDocumentTitle(null);
		this.getManagedBean().setSearchDocumentToDuration(null);
		this.getManagedBean().setSearchDocumentFromDuration(null);
		getManagedBean().setSearchMinistryProfile(null);
		getManagedBean().setSearchPtjGroupProfile(null);
		getManagedBean().setSearchPtjProfile(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	/**
	 * Gets the search tracking diary data model.
	 *
	 * @return the search tracking diary data model
	 */
	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	/**
	 * Sets the search tracking diary data model.
	 *
	 * @param searchTrackingDiaryDataModel the new search tracking diary data model
	 */
	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	private List<Object[]> getParentDocument(String docType, Long docId) {
		List<Object[]> docParents = new ArrayList<Object[]>();

		SourcingIntegrationReqDTO searchReqDTO = new SourcingIntegrationReqDTO();
		searchReqDTO.setDocType(docType);
		searchReqDTO.setDocId(Long.valueOf(docId));

		Long groupDocId = docId;
		String groupDocType = docType;

		while(groupDocId != null && groupDocType != null) {
			SourcingIntegrationResDTO resDTO = getSourcingIntegrationSO().retrieveGroupDocByDocIdAndDocType(searchReqDTO);
			groupDocId = resDTO.getGroupDocId();
			groupDocType = resDTO.getGroupDocType();
			if(resDTO != null) {
				if(groupDocId != null && groupDocType != null) {
					docParents.add(new Object[]{groupDocType, groupDocId});
				}
			}
			searchReqDTO.setDocType(groupDocType);
			searchReqDTO.setDocId(groupDocId);
		}

		return docParents;
	}

	/**
	 * Gets the user name.
	 *
	 * @param userId the user id
	 * @return the user name
	 */
	public String getUserName(long userId){
		String userName = "";
		UserReqDTO userReqDTO  = new UserReqDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setUserId(userId);
		PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
		if(pmUser != null){
			userName = pmUser.getUserName();
		}
		return userName;
	}

	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the org name.
	 *
	 * @param orgProfileId the org profile id
	 * @return the org name
	 */
	public String getOrgName(Long orgProfileId) {
		String orgName="";

		if (orgProfileId != null) {
			IOrgProfileSO orgProfileSO = null;
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(orgProfileReqDTO);
			if(orgProfileResDTO.getPmOrgProfile() != null){
				orgName = orgProfileResDTO.getPmOrgProfile().getOrgName();
			}
			return orgName;
		}
		return null;
	}
	
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				for (int i = 0; i < orgLevel; i++) {
					condition.append("parentOrgProfile.");
				}
				condition.append("orgProfileId");
				condition.append(SourcingConstants.LOV_CONDITION_SEPARATOR);
				condition.append(searchMinistryProfile.getOrgProfileId());

			}

		} 
		return condition.toString();
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPtjCondition() {
		debugLogger("getPTJCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		PmOrgProfile searchMinistryProfile = this.getManagedBean().getSearchMinistryProfile();
		PmOrgProfile searchPtjGroupProfile = this.getManagedBean().getSearchPtjGroupProfile();
		if (null != searchPtjGroupProfile && searchPtjGroupProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchPtjGroupProfile.getOrgProfileId());
		} else if (null != searchMinistryProfile && searchMinistryProfile.getOrgProfileId() != null) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + searchMinistryProfile.getOrgProfileId());
		} 
		return condition.toString();
	}
	
	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void postUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("postUpdateOrgProfileListener");
		
		if (orgProfile instanceof PmOrgProfile) {
			PmOrgProfile pmOrgProfile = (PmOrgProfile)orgProfile;
			String orgType = pmOrgProfile.getOrgTypeCode().getParameterCode();
			
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);
				getManagedBean().setSearchPtjGroupProfile(ministryOrgProfile);
				
				postUpdateOrgProfileListener(ministryOrgProfile);
				
			} else { 
				if (getManagedBean().getSearchMinistryProfile() == null || 
						getManagedBean().getSearchMinistryProfile().getOrgProfileId() == null) {
					getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(
							pmOrgProfile.getOrgProfileId()));	
				}
				
				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					getManagedBean().setSearchPtjProfile(new PmOrgProfile());
					
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					if (getManagedBean().getSearchPtjGroupProfile() == null || 
							getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == null) {
						getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(
								pmOrgProfile.getOrgProfileId()));	
					}
				}
			}
				
		}
	}
    
    /**
   	 * The method used to disable the ministry lov
   	 * if login user is or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is ministry contract administrator/internal auditor
   	 *         false, if login user is not ministry contract administrator/internal auditor
	 */
    public boolean disableMinistryLov() {
    	return (isReqOrApprover() || isContractAdminOrInternalAudit());
    }
     
  
    /**
     * Checks if is requisitioner or approver.
     *
     * @return true, if is requisitioner or approver
     */
    public boolean isReqOrApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value()));
	}
   
   
    /**
     * Checks if is contract admin or internal audit.
     *
     * @return true, if is contract admin or internal audit
     */
    public boolean isContractAdminOrInternalAudit() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()));
	}
    
    /**
     * Checks if is search all.
     *
     * @return true, if is search all
     */
    public boolean isSearchAll() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()));
	}
    
    /**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		
		PmOrgProfile orgProfile = getOrgProfileByLoginUser();
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		
		getManagedBean().setSearchMinistryProfile(ministryProfile);
		getManagedBean().setSearchPtjGroupProfile(ptjGroupProfile);
		getManagedBean().setSearchPtjProfile(ptjProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& resDTO.getUserOrgList().size() > 0) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}

}

