package my.ep.web.backingbean.sc.audittrails;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.audit.AuDPRequestNoteSPDTO;
import my.ep.dto.sc.audit.AuditQTDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.au.AuditTrailsSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.audittrails.AuditTrailsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "auditTrailsBackingBean")
@RequestScoped
public class AuditTrailsBackingBean extends
BaseBackingBean<AuditTrailsManagedBean> {
	private ISearchBean<PmParameterDesc> docTypeSearchBean;
	private static Logger log = Logger
			.getLogger(AuditTrailsBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/sc/quatationtender/audittrails/auditTrailsMain.xhtml";
	public static final String SC_TRACKING_DIARY_MANAGED_BEAN = "scTrackingDiaryManagedBean";
//	public static final String DP_TRACKING_DIARY_MANAGED_BEAN = "dpTrackingDiaryManagedBean";
//	public static final String FL_TRACKING_DIARY_MANAGED_BEAN = "flTrackingDiaryManagedBean";
//	public static final String CT_TRACKING_DIARY_MANAGED_BEAN = "ctTrackingDiaryManagedBean";
//	public static final String CM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String CD_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String SM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String PP_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String PM_TRACKING_DIARY_MANAGED_BEAN = "pmTrackingDiaryManagedBean";
	
	private transient IPagingDataModel<AuditQTDTO> searchDP = new SearchGeneralInfoPagingDataModel();

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		log.debug("preRenderView");
		this.renewSesionObject();
		
		//Enhancement 32675 - shaqimi start
//		if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())
//				|| getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
//			this.getManagedBean().setFactoringUser(true);
//		}
		//Enhancement 32675 - shaqimi start
		
		SCEvaluationDataType evaluationDataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
		//Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		//String committeeType = ((SCEvaluationDataType) evaluationDataType).getCommitteeType();
		Long qtId = evaluationDataType.getQtId().longValue();
		getManagedBean().setQtId(qtId);
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackUrl(fc.getViewRoot().getViewId());
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_CT_REPORT_MAIN);
		
//		getManagedBean().
	}
	
	public void redirectToMainPage(){
		log.info(".....redirectToMainPage.....");
		log.info("............this.getManagedBean().getBackURL()"+this.getManagedBean().getBackUrl());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, this.getManagedBean().getBackUrl()); 
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
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String getRoleDescByRoleCode(String roleCode) {
		log.info("getRoleDescByRoleCode");
		if(roleCode==null || roleCode.isEmpty()) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}
	
	public void redirectToAuditTrailsViews(String docType, Long docId)  throws IOException,PortalException, SystemException{
		redirectToQtAuditTrailsViews(docType, docId);
	}
	
	public void redirectToQtAuditTrailsViews(String docType, Long docId)  throws IOException,PortalException, SystemException{
//		log.info("redirectToSupplierTrackingDiary - start:docType=" + docType + " docId=" + docId
//				+ " isFilterBySupplier=" + isFilterBySupplier);
		// setTrackingDiaryParam(docType, docId, isFilterBySupplier);

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackUrl(fc.getViewRoot().getViewId());
		log.info("fc.getViewRoot().getViewId()=" + fc.getViewRoot().getViewId());
		Long qtId = getManagedBean().getQtId();
		//navigateToGeneralInfoTab(qtId);

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/sc/quatationtender/auditTrails/auditTrailDetailsMain.xhtml");
	}
	
	/**
	 * Method to navigate back to previous page
	 * 
	 * @return the previous page
	 */
	public String backAction() {
		String t = getManagedBean().getPreviousPageUrl();
		getManagedBean().setPreviousPageUrl(null);
		return t;
	}
	
	/**
	 * #35500
	 * To navigate to the selected tab
	 */
	public void onTabMenuChangeAction() {
		log.info("onTabMenuChangeAction");
		Integer selectedTabIndex = null; 
		String selectedTabIndexStr = FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedTabIndex");
		
		if (StringUtils.isNotBlank(selectedTabIndexStr)) {
			selectedTabIndex = Integer.valueOf(selectedTabIndexStr);
		} else {
			selectedTabIndex = getManagedBean().getActiveIndex();
		}
		
		switch (selectedTabIndex) {
			case 0:
//				if (getManagedBean().isInitialDecisionFwdToBPK()) { //#28240-start
//					navigateToEvaluationReportTab();	
//				}else{
				Long qtId = getManagedBean().getQtId();
				navigateToGeneralInfoTab(qtId);
//				}
				break;
			case 1:
//				if (getManagedBean().isInitialDecisionFwdToBPK()) {//#28240-start
//					navigateToBriefingPaperTab();	
//				}else{
				navigateToBriefingSiteVisitTab();
//				}
				break;
			case 2:
//				if (getManagedBean().isInitialDecisionFwdToBPK()) {//#28240-start
//					navigateToSupplierFinalizationTab();	
//				}else{
				navigateToSupplierListTab();
//				}
				break;
			case 3:
				navigateToDeskOfficerAssistantTab();
				break;
			case 4:
				navigateToDeskOfficerTab();
				break;
			case 5:
				navigateToSpecCommitteeAppointTab();	
				break;
			case 6:				
				navigateToEvalCommitteeAppointTab();
				break;
			case 7:				
				navigateToTechCommitteeAppointTab();
				break;
			case 8:
				navigateToFinanceCommitteeAppointTab();
				break;
			case 9:
				navigateToOpenCommitteeAppointTab();
				break;
			case 10:
				navigateToQuotCommitteeAppointTab();
				break;
			case 11:
				navigateToProcBoardAppointTab();
				break;
		}
	}

	private void navigateToGeneralInfoTab(Long qtId) {
		log.info("navigateToGeneralInfoTab");
		//this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		searchDP.reloadData();
		
		
	}
	
	private void navigateToProcBoardAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToQuotCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToOpenCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToFinanceCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToTechCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToEvalCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToSpecCommitteeAppointTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToDeskOfficerTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToDeskOfficerAssistantTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToSupplierListTab() {
		// TODO Auto-generated method stub
		
	}

	private void navigateToBriefingSiteVisitTab() {
		// TODO Auto-generated method stub
		
	}
	
	private class SearchGeneralInfoPagingDataModel extends PagingDataModel<AuditQTDTO> {

		private static final long serialVersionUID = 1L;

		public SearchGeneralInfoPagingDataModel() {

			super("qtId", getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditQTDTO> pageChangeListener(ISearchBean<AuditQTDTO> searchBean) {

			SearchResultBean<AuDPRequestNoteSPDTO> searchResult = new SearchResultBean<>();
			AuditTrailsSearchBean qtSearchBean = getManagedBean().getAuditTrailsSearchBean();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				IAuditDPRequestNoteSO auditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);

				ISearchResultBean<AuditQTDTO> resDTO = null;
				//resDTO = auditSO.searchDPCancelRNResultList(languageCode, qtSearchBean.getDocId(), searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance()
						.logSLAError(SLALogConstant.COMMON_MODULE, "AU", qtSearchBean.getDocId().toString(),
								"AuditSearchDPRequestNoteBackingBean.SearchDPPagingDataModel",
								getUserIdSessionFromLoginId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	
	
}

