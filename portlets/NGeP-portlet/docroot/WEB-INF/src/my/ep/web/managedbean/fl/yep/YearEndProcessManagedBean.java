package my.ep.web.managedbean.fl.yep;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.IFulfilmentYepSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.fl.FlYepReqDTO;
import my.ep.dto.fl.FlYepResDTO;
import my.ep.persistence.entity.fl.yep.FlYepTasklist;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "yepMB")
@SessionScoped
public class YearEndProcessManagedBean extends BaseManagedBean {

	/**
	 * constant serial version uid
	 */
	private static final long serialVersionUID = 1L;

	/** the log */
	public static Logger log = Logger
			.getLogger(YearEndProcessManagedBean.class);

	private String searchDocType;
	private String searchFunctionType;
	private String searchOrderNo;
	private String searchTitle;
	private int searchFinancialYear;
	private String searchCircularNo;
	private String searchSuppName;
	private String searchLoaNo;
	private String searchPhysLoaNo;
	private String searchContractNo;
	private String searchPhysContractNo;
	private Date searchContractFromDuration;
	private Date searchContractToDuration;
	private Date searchToDate;
	private Date searchStartDate;
	private String searchCentralPanelContractName;
	private IPagingDataModel<FlYepTasklist> flYepDataModel = new FlYepPagingDataModel();
	
	private Boolean searchAp58 = true;
	private Boolean searchCarryFoward;
	private Boolean searchCloseMnualPay;
	private Boolean searchNonAction;

	private Boolean checkDocType;
	private Boolean checkPoDocType;
	private Boolean checkPaDocType;
	private Boolean checkDocNo;
	private Long statusDescId;
	/*#28375 - Logan*/
	private Long yepTaskId;

	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile ptjGroup;

	/**
	 * Gets the check doc type.
	 * 
	 * @return the checkDocType
	 */
	public Boolean getCheckDocType() {
		return checkDocType;
	}

	/**
	 * Sets the check doc type.
	 * 
	 * @param checkDocType
	 *            the checkDocType to set
	 */
	public void setCheckDocType(Boolean checkDocType) {
		this.checkDocType = checkDocType;
	}

	/**
	 * Gets the check po doc type.
	 * 
	 * @return the checkPoDocType
	 */
	public Boolean getCheckPoDocType() {
		return checkPoDocType;
	}

	/**
	 * Sets the check po doc type.
	 * 
	 * @param checkPoDocType
	 *            the checkPoDocType to set
	 */
	public void setCheckPoDocType(Boolean checkPoDocType) {
		this.checkPoDocType = checkPoDocType;
	}

	/**
	 * Gets the check pa doc type.
	 * 
	 * @return the checkPaDocType
	 */
	public Boolean getCheckPaDocType() {
		return checkPaDocType;
	}

	/**
	 * Sets the check pa doc type.
	 * 
	 * @param checkPaDocType
	 *            the checkPaDocType to set
	 */
	public void setCheckPaDocType(Boolean checkPaDocType) {
		this.checkPaDocType = checkPaDocType;
	}

	/**
	 * Gets the check doc no.
	 * 
	 * @return the checkDocNo
	 */
	public Boolean getCheckDocNo() {
		return checkDocNo;
	}

	/**
	 * Sets the check doc no.
	 * 
	 * @param checkDocNo
	 *            the checkDocNo to set
	 */
	public void setCheckDocNo(Boolean checkDocNo) {
		this.checkDocNo = checkDocNo;
	}

	/**
	 * Gets the search doc type.
	 * 
	 * @return the search doc type
	 */
	public String getSearchDocType() {
		return searchDocType;
	}

	/**
	 * Sets the search doc type.
	 * 
	 * @param searchDocType
	 *            the new search doc type
	 */
	public void setSearchDocType(String searchDocType) {
		this.searchDocType = searchDocType;
	}

	/**
	 * Gets the search order no.
	 * 
	 * @return the searchOrderNo
	 */
	public String getSearchOrderNo() {
		return searchOrderNo;
	}

	/**
	 * Sets the search order no.
	 * 
	 * @param searchOrderNo
	 *            the searchOrderNo to set
	 */
	public void setSearchOrderNo(String searchOrderNo) {
		this.searchOrderNo = searchOrderNo;
	}

	/**
	 * Gets the search title.
	 * 
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Sets the search title.
	 * 
	 * @param searchTitle
	 *            the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * Gets the status desc id.
	 * 
	 * @return the statusDescId
	 */
	public Long getStatusDescId() {
		return statusDescId;
	}

	/**
	 * Sets the status desc id.
	 * 
	 * @param statusDescId
	 *            the statusDescId to set
	 */
	public void setStatusDescId(Long statusDescId) {
		this.statusDescId = statusDescId;
	}
	
	/*#28375 - Logan - Beginning*/
	public Long getYepTaskId() {
		return yepTaskId;
	}

	public void setYepTaskId(Long yepTaskId) {
		this.yepTaskId = yepTaskId;
	}
	
	

	
	/*Ending*/

	/**
	 * Gets the search contract to duration.
	 * 
	 * @return the searchContractToDuration
	 */
	public Date getSearchContractToDuration() {
		return searchContractToDuration;
	}

	/**
	 * Sets the search contract to duration.
	 * 
	 * @param searchContractToDuration
	 *            the searchContractToDuration to set
	 */
	public void setSearchContractToDuration(Date searchContractToDuration) {
		this.searchContractToDuration = searchContractToDuration;
	}

	/**
	 * @return the searchFinancialYear
	 */
	public int getSearchFinancialYear() {
		return searchFinancialYear;
	}

	/**
	 * @param searchFinancialYear
	 *            the searchFinancialYear to set
	 */
	public void setSearchFinancialYear(int searchFinancialYear) {
		this.searchFinancialYear = searchFinancialYear;
	}

	/**
	 * Gets the search supp name.
	 * 
	 * @return the searchSuppName
	 */
	public String getSearchSuppName() {
		return searchSuppName;
	}

	/**
	 * Sets the search supp name.
	 * 
	 * @param searchSuppName
	 *            the searchSuppName to set
	 */
	public void setSearchSuppName(String searchSuppName) {
		this.searchSuppName = searchSuppName;
	}

	/**
	 * @return the searchCentralContractName
	 */
	public String getSearchCentralPanelContractName() {
		return searchCentralPanelContractName;
	}

	/**
	 * @param searchCentralContractName
	 *            the searchCentralContractName to set
	 */
	public void setSearchCentralPanelContractName(
			String searchCentralContractName) {
		this.searchCentralPanelContractName = searchCentralContractName;
	}

	/**
	 * Gets the search circular no.
	 * 
	 * @return the search circular no
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	/**
	 * Sets the search circular no.
	 * 
	 * @param searchCircularNo
	 *            the new search circular no
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	/**
	 * Gets the search loa no.
	 * 
	 * @return the search loa no
	 */
	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	/**
	 * Sets the search loa no.
	 * 
	 * @param searchLoaNo
	 *            the new search loa no
	 */
	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	/**
	 * Gets the search phys loa no.
	 * 
	 * @return the searchPhysLoaNo
	 */
	public String getSearchPhysLoaNo() {
		return searchPhysLoaNo;
	}

	/**
	 * Sets the search phys loa no.
	 * 
	 * @param searchPhysLoaNo
	 *            the searchPhysLoaNo to set
	 */
	public void setSearchPhysLoaNo(String searchPhysLoaNo) {
		this.searchPhysLoaNo = searchPhysLoaNo;
	}

	/**
	 * Gets the search contract no.
	 * 
	 * @return the search contract no
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}

	/**
	 * Sets the search contract no.
	 * 
	 * @param searchContractNo
	 *            the new search contract no
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}

	/**
	 * Gets the search phys contract no.
	 * 
	 * @return the searchPhysContractNo
	 */
	public String getSearchPhysContractNo() {
		return searchPhysContractNo;
	}

	/**
	 * Sets the search phys contract no.
	 * 
	 * @param searchPhysContractNo
	 *            the searchPhysContractNo to set
	 */
	public void setSearchPhysContractNo(String searchPhysContractNo) {
		this.searchPhysContractNo = searchPhysContractNo;
	}

	/**
	 * Gets the search contract from duration.
	 * 
	 * @return the searchContractFromDuration
	 */
	public Date getSearchContractFromDuration() {
		return searchContractFromDuration;
	}

	/**
	 * Sets the search contract from duration.
	 * 
	 * @param searchContractFromDuration
	 *            the searchContractFromDuration to set
	 */
	public void setSearchContractFromDuration(Date searchContractFromDuration) {
		this.searchContractFromDuration = searchContractFromDuration;
	}

	public Date getSearchToDate() {
		return searchToDate;
	}

	/**
	 * Sets the search to date.
	 * 
	 * @param searchToDate
	 *            the searchToDate to set
	 */
	public void setSearchToDate(Date searchToDate) {
		this.searchToDate = searchToDate;
	}

	public Date getSearchStartDate() {
		return searchStartDate;
	}

	/**
	 * Sets the search from date.
	 * 
	 * @param searchStartDate
	 *            the searchStartDate to set
	 */
	public void setSearchStartDate(Date searchStartDate) {
		this.searchStartDate = searchStartDate;
	}
	

	public Boolean getSearchAp58() {
		return searchAp58;
	}

	public void setSearchAp58(Boolean searchAp58) {
		this.searchAp58 = searchAp58;
	}

	public Boolean getSearchCarryFoward() {
		return searchCarryFoward;
	}

	public void setSearchCarryFoward(Boolean searchCarryFoward) {
		this.searchCarryFoward = searchCarryFoward;
	}

	public Boolean getSearchCloseMnualPay() {
		return searchCloseMnualPay;
	}

	public void setSearchCloseMnualPay(Boolean searchCloseMnualPay) {
		this.searchCloseMnualPay = searchCloseMnualPay;
	}

	public Boolean getSearchNonAction() {
		return searchNonAction;
	}

	public void setSearchNonAction(Boolean searchNonAction) {
		this.searchNonAction = searchNonAction;
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
	
	
	public String getSearchFunctionType() {
		return searchFunctionType;
	}

	public void setSearchFunctionType(String searchFunctionType) {
		this.searchFunctionType = searchFunctionType;
	}

	
	public PmOrgProfile getPtj() {
		return ptj;
	}

	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}

	public PmOrgProfile getMinistry() {
		return ministry;
	}

	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;
	}

	public PmOrgProfile getPtjGroup() {
		return ptjGroup;
	}

	public void setPtjGroup(PmOrgProfile ptjGroup) {
		this.ptjGroup = ptjGroup;
	}

	/**
	 * Checks if is Auditor General/Internal user.
	 *
	 * @return true, if is Auditor General/Internal user
	 */
	public boolean isAuditor() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || 
				getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) || 
				getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}
	
	/**
	 * Checks if is fl fulfilment requisitioner.
	 *
	 * @return true, if is fl fulfilment requisitioner
	 */
	public boolean isFlFulfilmentRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isFlFulfilmentApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
	
	/**
	 * Checks if is acknowledge officer
	 *
	 * @return true, if is acknowledge officer
	 */
	public boolean isAcknowledgeOfficer() {
		return getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value());
	}
	
	/**
	 * Checks if is fl fulfilment receving officer.
	 *
	 * @return true, if is fl fulfilment receving officer
	 */
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	
	/**
	 * Checks if is fl fulfilment payment officer.
	 *
	 * @return true, if is fl fulfilment payment officer
	 */
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
	}
	
	
	/**
	 * Gets the fl YEP data model.
	 * 
	 * @return the flYepDataModel
	 */
	public IPagingDataModel<FlYepTasklist> getFlYepDataModel() {
		return flYepDataModel;
	}
	
	private List<String> getUserRoles() {
		List<String> roleList = new ArrayList<String>();
		if(isBPKApprover()) {
			roleList.add(RoleCode.BPK_APPROVER.value()); 
		} else if(isAuditor()) {
			if(getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())) {
				roleList.add(RoleCode.BPK_AUDITOR.value()); 
			} else if(getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
				roleList.add(RoleCode.AUDITOR_GENERAL.value());
			} else {
				roleList.add(RoleCode.AUDITOR_INTERNAL.value()); 
			}
		} else {
			// PTJ User
			if(isFlFulfilmentRequisitioner()) {
				roleList.add(RoleCode.REQUISITIONER.value()); 
			}
			if(isFlFulfilmentApprover()) {
				roleList.add(RoleCode.FL_APPROVER.value()); 
			}
			if(isFlFulfilmentPaymentOfficer()) {
				roleList.add(RoleCode.PAY_OFFICER.value()); 
			}
			if(isFlFulfilmentRecevingOfficer()) {
				roleList.add(RoleCode.RECEIVING_OFFICER.value()); 
			}
			if(isAcknowledgeOfficer()) {
				roleList.add(RoleCode.ACKNOWLEDGE_OFFICER.value()); 
			}
			// Supplier User
			if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
				roleList.add(RoleCode.MOF_SUPPLIER_ADMIN.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				roleList.add(RoleCode.MOF_SUPPLIER_USER.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				roleList.add(RoleCode.BASIC_SUPPLIER_ADMIN.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
				roleList.add(RoleCode.BASIC_SUPPLIER_USER.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
				roleList.add(RoleCode.FL_USER.value());				
			} else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
				roleList.add(RoleCode.GOVT_SELLER.value());		
			} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) { 
				roleList.add(RoleCode.G2G_SUPPLIER_ADMIN.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) { 
				roleList.add(RoleCode.G2G_SUPPLIER_USER.value());	
			}
			// Factoring User
			if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
				roleList.add(RoleCode.FACTORING_ADMIN.value());
			} else if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
				roleList.add(RoleCode.FACTORING_USER.value());
			}
		}
		return roleList;
	}

	private class FlYepPagingDataModel extends PagingDataModel<FlYepTasklist> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FlYepPagingDataModel() {
			super("flYepList",false);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<FlYepTasklist> pageChangeListener(
				ISearchBean<FlYepTasklist> searchBean) {
			
			if(log.isDebugEnabled()){
				log.debug("pagechangelistener");
			}
			
			try{
				FlYepReqDTO reqDTO = new FlYepReqDTO();
				searchBean.setUseEntityClass(true);
				
				reqDTO.setSearchBean(searchBean);
				
				if(getSearchFunctionType()!=null) {
					reqDTO.setFunctionType(getSearchFunctionType());
				}
				
				/*purchase order*/
				if(getSearchOrderNo()!=null && getSearchOrderNo()!="") {
					reqDTO.setOrderNo(getSearchOrderNo());
				}	
				
				/*title*/
				if(getSearchTitle()!=null && getSearchTitle()!="") {
					reqDTO.setTitle(getSearchTitle());
				}
				
				/*doc type*/
				if(getSearchDocType()!=null) {
					reqDTO.setDocType(getSearchDocType());
				} 
				
				/*po status*/
				if(getStatusDescId()!=null) {
					reqDTO.setStatusId(getStatusDescId());
				}
				
				/*#28375 Logan - Beginning*/
				if(getYepTaskId()!=null){
					reqDTO.setTaskId(String.valueOf(getYepTaskId()));
				}
				/*Ending*/
				
				/*ap 58*/
				if(getSearchAp58() != null && getSearchAp58() == true){
					reqDTO.setAp58(1);
				}
				
				/*carry fward*/
				if(getSearchCarryFoward() != null){
					reqDTO.setCarryFoward(getSearchCarryFoward());
				}

				/*close manual*/
				if(getSearchCloseMnualPay() != null){
					reqDTO.setCloseMnualPay(getSearchCloseMnualPay());
				}
				
				/*non action*/
				if(getSearchNonAction() != null){
					reqDTO.setNonAction(getSearchNonAction());
				}
				
				/*start date*/
				if(getSearchStartDate() != null){
					reqDTO.setStartDate(getSearchStartDate());
				}
				
				/*to date*/
				if(getSearchToDate() != null){
					reqDTO.setToDate(getSearchToDate());
				}
				
				/*business company name*/
				if(getSearchSuppName()!=null && getSearchSuppName()!="") {
					reqDTO.setSupplierName(getSearchSuppName());
				}
				
				/*financial year*/
				if(getSearchFinancialYear()!= 0){
					reqDTO.setFinancialYear(getSearchFinancialYear());
				}
				
				/*circular name*/
				if(getSearchCentralPanelContractName()!=null && getSearchCentralPanelContractName()!="") {
					reqDTO.setSearchCentralContractName(getSearchCentralPanelContractName());
				}
				
				/*circular no*/
				if(getSearchCircularNo()!=null && getSearchCircularNo()!="")  {
					reqDTO.setCircularNo(getSearchCircularNo());
				}
				
				/*loa no*/
				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
					reqDTO.setLoaNo(getSearchLoaNo());
				}
				
				/*loa reference*/
				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
					reqDTO.setPhysicalLoaNo(getSearchPhysLoaNo());
				}	
				
				/*contract no*/
				if(getSearchContractNo()!=null && getSearchContractNo()!="") {
					reqDTO.setContractNo(getSearchContractNo());
				}
				
				/*physical no*/
				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
					reqDTO.setPhysicalContractNo(getSearchPhysContractNo());
				}
				
				/*contract start date*/
				if(getSearchContractFromDuration()!=null) {
					reqDTO.setContractStartDate(getSearchContractFromDuration());
				}
				
				/*contract end date*/
				if(getSearchContractToDuration()!=null) {
					reqDTO.setContractEndDate(getSearchContractToDuration());
				}
				
				if( getPtj() != null){
					reqDTO.setPtj(getPtj());
				}
				if(getMinistry() != null){
					reqDTO.setMinistry(getMinistry());
				}
				if(getPtjGroup() != null){
					reqDTO.setPtjGroup(getPtjGroup());
				}
				
				reqDTO.setUserRoleList(getUserRoles());
				reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				
				
				IFulfilmentYepSO flYepSO = (IFulfilmentYepSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentYepSO.class);
				FlYepResDTO resDTO = flYepSO.searchFulfilmentYep(reqDTO);
				
				if(resDTO.isSuccessful()){
					if(null != resDTO.getResultBean()){
						log.info("Yep resDTO.getResultBean().getTotalRecord(): "+resDTO.getResultBean().getTotalRecord());
					}
					return resDTO.getResultBean();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}
	}


}
