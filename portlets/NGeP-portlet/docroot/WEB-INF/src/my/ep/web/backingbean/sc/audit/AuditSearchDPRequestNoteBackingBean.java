package my.ep.web.backingbean.sc.audit;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.audit.AuDPRequestNoteSPDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.directpurchase.AuditDPSearchBean;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.sc.dp.requestnote.audit.AuDPRequestNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * #28895_35492
 * 
 * @author normuliana
 * 
 *         AuditSearchDPRequestNoteBackingBean for search view
 */
@ManagedBean(name = "auditSearchDPRequestNoteBackingBean")
@ViewScoped
public class AuditSearchDPRequestNoteBackingBean extends BaseBackingBean<AuDPRequestNoteManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditSearchDPRequestNoteBackingBean.class);
	private IPagingDataModel<AuDPRequestNoteSPDTO> searchDP = new SearchDPPagingDataModel();
	private String requestNoneView = "/xhtml/sc/directpurchase/requestnote/audit/auditDPRequestNoteView.xhtml";

	public AuditSearchDPRequestNoteBackingBean() {
		/**/
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * @return
	 */
	public boolean getRoleCode() {
		boolean isRole ;
		if (isRoleCodeAuditorGeneral() || isRoleCodeEPAdmin() || isRoleCodeBPKAuditor() || isRoleCodeAuditorInternal()) {
			isRole = true;
		}else{
			isRole = false;
		}
		return isRole;
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeAuditorInternal() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeAuditorGeneral() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeEPAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeBPKAuditor() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}

	/**
	 * loadDPRequestnote() - reload searchDP data
	 */
	public void loadDPRequestnote() {
		this.getManagedBean().setLazy(true);
		searchDP.reloadData();
	}

	/**
	 * redirectToAudittrailView
	 * 
	 * @param docId
	 */
	public void redirectToAudittrailView(Long docId) throws IOException {
		AuditDPSearchBean auditDPSearchBean = new AuditDPSearchBean();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditDPSearchBean.setDocId(BigInteger.valueOf(docId));
		getManagedBean().setLanguage(languageCode);
		getManagedBean().setDocId(BigInteger.valueOf(docId));
		getManagedBean().setAuditDPSearchBean(auditDPSearchBean);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		loadDPRequestnote();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, requestNoneView);
	}

	/**
	 * redirectToMainPage()
	 */
	public void redirectToMainPage() {
		log.info("redirectToMainPage");
		log.info("............this.getManagedBean().getBackURL()" + this.getManagedBean().getBackURL());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getBackURL());
	}

	/**
	 * search DP list
	 * 
	 */
	private class SearchDPPagingDataModel extends PagingDataModel<AuDPRequestNoteSPDTO> {

		private static final long serialVersionUID = 1L;

		public SearchDPPagingDataModel() {

			super("searchDPId", getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * @param searchBean
		 * @return SearchResultBean
		 */
		public ISearchResultBean<AuDPRequestNoteSPDTO> pageChangeListener(
				ISearchBean<AuDPRequestNoteSPDTO> searchBean) {

			SearchResultBean<AuDPRequestNoteSPDTO> searchResult = new SearchResultBean<>();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				AuditDPSearchBean dpSearchBean = getManagedBean().getAuditDPSearchBean();
				IAuditDPRequestNoteSO auditSO = (IAuditDPRequestNoteSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);

				ISearchResultBean<AuDPRequestNoteSPDTO> resDTO = auditSO.searchDPCancelRNResultList(languageCode,
						dpSearchBean.getDocId(), searchBean);
				searchResult.setTotalRecord(resDTO.getResultList().size());
				searchResult.setResultList(resDTO.getResultList());
				return searchResult;

			} catch (Exception e) {
				log.error(e);
				throw new EpException(MessageConst.ERROR_770005);
			}
		}
	}

	public IPagingDataModel<AuDPRequestNoteSPDTO> getSearchDP() {
		return searchDP;
	}

	public void setSearchDP(IPagingDataModel<AuDPRequestNoteSPDTO> searchDP) {
		this.searchDP = searchDP;
	}
	
	//SECTION: Generate Report (PDF & EXCEL)
	
	/**
	 * Generate Report: PDF Format
	 * @param docId	,referring to selected REQUEST_NOTE_ID
	 * @throws IOException 
	 */
	public void generateReport(BigInteger docId) throws IOException{
		
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());//P_DOCID
		
		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_DP, paramMap);
	}
	
}
