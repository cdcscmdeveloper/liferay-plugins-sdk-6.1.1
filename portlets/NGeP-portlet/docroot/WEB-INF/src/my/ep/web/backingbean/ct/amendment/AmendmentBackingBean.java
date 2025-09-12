/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat; //#24333
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormatSymbols; //24333
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale; //24333
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.bank.IChangeBankAccSO; // Fix - #33674
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO; //26186
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.comparator.ct.CtContractZoneComparator;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.bpm.CompositeValidityReqDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper; //Fix - #36708
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtBondWaiverDetail;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtMeeting;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.MemberRole;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.ct.CtAddressUtil;
import my.ep.util.shared.PerformanceBondUtil;//24333
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.backingbean.ct.ContractDetailsBackingBean;
import my.ep.web.backingbean.ct.request.ctadmin.RequestCtAdminBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.backingbean.sm.applicationView.ViewSuppProfileBackingBean;
import my.ep.web.bean.ct.CtLetterOfAcceptance;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst; //#24333
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.util.NumberWordUtil; //#24333
import my.ep.web.util.NumberWordUtilEn; //#24333

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Amendment Details
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "amendmentBackingBean")
@ViewScoped
public class AmendmentBackingBean extends ContractBaseBackingBean<AmendmentManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AmendmentBackingBean.class);
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true"; // #15851
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String CONTRACT_COMMON_PAGE = "/xhtml/ct/viewcontract/fulfilmentTab.xhtml?faces-redirect=true";
	private static final String UPDATE_COMMITTEE_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/pbqcc/committeeList.xhtml"; //#29403
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml?faces-redirect=true"; //#29403
	private static final String PROCESS_AMEND_AGREEMENT_PAGE = "/xhtml/ct/agreement/shared/sidemenu/agreementTab.xhtml?faces-redirect=true"; //#15851
	private ContractBackingBean ctBackingBean = null;
	private CtMeeting[] selectedMeetingUser;
	private CtPerformanceBond[] selectedBondList;
	private List<SelectItem> populateBankAccountList;
	private IPagingDataModel<PmUser> committeDataModel = new CommittePagingDataModel(); 
	//Start Fix - #33287
	private boolean isUntickContractExtension = false;
	private boolean isUntickItemPrice = false;
	private boolean isUntickItemMaxQty = false;
	private boolean isUntickOther = false;
	private boolean spToCheck = false;
	//End Fix - #33287
	//#34097/#26186 start
	private static final Set<Long> CANCEL_STATUS_CONST = new HashSet<>(Arrays.asList(
            ContractStatusConst.FA_PENDING_SUBMISSION,
            ContractStatusConst.FA_PENDING_REVISION,
            ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION,
            ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION_REVISION,
            ContractStatusConst.FA_PENDING_MEETING_DECISION,
            ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION,
            ContractStatusConst.FA_PENDING_MOF_MEETING_DECISION,
            ContractStatusConst.FA_PENDING_LOA_SUBMISSION,
            ContractStatusConst.FA_PENDING_LOA_REVISION
    ));
	private static final Set<Long> REJECT_STATUS_CONST = new HashSet<>(Arrays.asList(
			ContractStatusConst.FA_PENDING_APPROVAL,
			ContractStatusConst.FA_PENDING_REVISION_APPROVAL,
			ContractStatusConst.FA_PENDING_LOA_APPROVAL,
			ContractStatusConst.FA_PENDING_LOA_REVISION_APPROVAL
    ));
	//#34097/#26186 end
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public AmendmentBackingBean() {
		/* Initialization of button */
		if(ctBackingBean == null){
			ctBackingBean = new ContractBackingBean();
		}
		if (this.getManagedBean().getOutComes() == null) {
			this.getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBackingBean.getManagedBean().setContractVer(new CtContractVer());
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
//		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
//		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
//		if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
//			getManagedBean().setViewOnly(true);
	}

	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}

	/**
	 * @return the selectedMeetingUser
	 */
	public CtMeeting[] getSelectedMeetingUser() {
		return selectedMeetingUser;
	}

	/**
	 * @param selectedMeetingUser the selectedMeetingUser to set
	 */
	public void setSelectedMeetingUser(CtMeeting[] selectedMeetingUser) {
		this.selectedMeetingUser = selectedMeetingUser;
	}

	/**
	 * @return the selectedBondList
	 */
	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	/**
	 * @param selectedBondList the selectedBondList to set
	 */
	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
	}

	/**
	 * @return the committeDataModel
	 */
	public IPagingDataModel<PmUser> getCommitteDataModel() {
		return committeDataModel;
	}

	/**
	 * @param committeDataModel the committeDataModel to set
	 */
	public void setCommitteDataModel(IPagingDataModel<PmUser> committeDataModel) {
		this.committeDataModel = committeDataModel;
	}

	/**
	 * Pagination for available committee search list
	 */
	private class CommittePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public CommittePagingDataModel() {
			super("newCommitteTable");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
			CommitteReqDTO committeReqDTO = null;
			ICommitteSO committeSO = null;
			try {
				committeReqDTO = new CommitteReqDTO();
				//Enhancement #26520 - start
				committeReqDTO.setDocId(ctBackingBean.getManagedBean().getContractVer().getContractVerId());
				committeReqDTO.setOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				String committeeType = getManagedBean().getCommitteeType();
				if(committeeType != null && committeeType != ""){
					if(committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
							|| committeeType.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
						committeReqDTO.setCommitteeType(SourcingConstants.COMMTYPE_SABAH_SARAWAK_ROLE);
					} else {
						committeReqDTO.setCommitteeType(committeeType);
					}
				}
				committeReqDTO.setDocType(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
				//Enhancement #26520 - end
				if (getManagedBean().getCommitteeName() != null && !"".equalsIgnoreCase(getManagedBean().getCommitteeName())) {
					SearchFilterBean filterBean = new SearchFilterBean("userName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCommitteeName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getManagedBean().getIdentificationType() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationType())){
					searchBean.getDataFilterBeans().add(new SearchFilterBean("identificationCode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getIdentificationType()));
				}
				if (getManagedBean().getIdentificationNo() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())){
					searchBean.getDataFilterBeans().add(new SearchFilterBean("identificationNo", SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getIdentificationNo()));
				}
				// Sort by user name
				searchBean.getSortColumns().add(new SearchOrderBean("userName"));

				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				committeReqDTO.setUserSearchBean(searchBean);

				return committeSO.retrieveUserListByCommType(committeReqDTO)
						.getUserResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				committeReqDTO = null;
				committeSO = null;
			}
			return null;
		}
	}

	/**
	 * The method used for searching the available committee member.
	 */
	public void searchExistingCommitteMember() {
		try { 
			committeDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}

	/**
	 * The method used to reset the search criteria for committee member.
	 */
	public void searchExistingCommitteMemberReset() {
		this.getManagedBean().setCommitteeName(null);
		this.getManagedBean().setIdentificationType(null);	
		this.getManagedBean().setIdentificationNo(null);	
		committeDataModel.resetData();
	}

	
	 /**
	 * The method used to reset the search criteria for circular.
	 */
	public void searchReset() {

		AmendmentBackingBean ccBB = new AmendmentBackingBean();
		ContractBackingBean bbBB = new ContractBackingBean();

		getManagedBean().setItemcode(null);
		getManagedBean().setItemname(null);
		getManagedBean().setItemstatus(null);
		getManagedBean().setItemtype(null);
		getManagedBean().setUOM(null);
		getManagedBean().setSkuno(null);
		getManagedBean().setTypeofgoods(null);
		ccBB.getManagedBean().setSelectedItem(null);
		ccBB.getManagedBean().setSelectedServiceItem(null);
		// Start Fix - #31042
		getManagedBean().getContractListingPagingDataModel().resetData();
		getManagedBean().getServiceListingPagingDataModel().resetData();

		if (bbBB.getManagedBean().getCtItemList().size() > 0 || !bbBB.getManagedBean().getCtItemList().isEmpty()) {
			getManagedBean().setItemProductExist(true);
		} else {
			getManagedBean().setItemProductExist(false);
		}

		if (bbBB.getManagedBean().getCtItemServiceList().size() > 0 || !bbBB.getManagedBean().getCtItemServiceList().isEmpty()) {
			getManagedBean().setItemServiceExist(true);
		} else {
			getManagedBean().setItemServiceExist(false);
		}
		// End Fix - #31042

	}

		
	/**
	 * The method used for searching the circular list.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCircular() throws Exception {
		// 20997 - Start - Fixing
		try {
			if (getManagedBean().getItemcode().equals("") 
					&& getManagedBean().getItemname().equals("") 
					&& getManagedBean().getItemstatus() == null 
					&& getManagedBean().getItemtype() == null 
					&& getManagedBean().getSkuno().equals("")
					&& getManagedBean().getTypeofgoods() == null) {

				throw new EpException(MessageConst.ERROR_750049);

			} else if (getManagedBean().getItemtype() != null) {
				getManagedBean().setResetval(true);
				ContractBackingBean ccBB = new ContractBackingBean();
				ccBB.getManagedBean().setSelectedItem(null);
				ccBB.getManagedBean().setServiceselectedItem(null);
				if (getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT)) {
					getManagedBean().getContractListingPagingDataModel().reloadData();
					getManagedBean().setItemProductExist(true);
					getManagedBean().setItemServiceExist(false);

				} else if (getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_SERVICE)) {
					getManagedBean().getServiceListingPagingDataModel().reloadData();
					getManagedBean().setItemServiceExist(true);
					getManagedBean().setItemProductExist(false);

				} else if (getManagedBean().getItemtype().equals(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE)) {
					getManagedBean().getContractListingPagingDataModel().reloadData();
					getManagedBean().getServiceListingPagingDataModel().reloadData();
					if (ccBB.getManagedBean().getCtItemList().size() > 0 || !ccBB.getManagedBean().getCtItemList().isEmpty()) {
						getManagedBean().setItemProductExist(true);
					} else {
						getManagedBean().setItemProductExist(false);
					}

					if (ccBB.getManagedBean().getCtItemServiceList().size() > 0 || !ccBB.getManagedBean().getCtItemServiceList().isEmpty()) {
						getManagedBean().setItemServiceExist(true);
					} else {
						getManagedBean().setItemServiceExist(false);
					}
					// 20997 - End-Fixing
				}
			}
			// Start Fix - #31042
			else if (getManagedBean().getItemtype() == null) {

				getManagedBean().getContractListingPagingDataModel().reloadData();
				getManagedBean().getServiceListingPagingDataModel().reloadData();

			}
			// End Fix - #31042
		} finally {
			// Do nothing
		}
	}

	/** to concat Itemcode and Item Name */
	// #31558 start
	public ScRequestItem contactItem(CtContractItem ctItem) {

		String itemcode = ctItem.getScRequestItem().getCmItem().getExtensionCode();
		String itemName = ctItem.getScRequestItem().getItemName();
		if (itemcode == null) {
			itemcode = ParameterConst.ITEM_CODE;
		}
		if (itemName == null) {
			itemName = ParameterConst.ITEM_NAME;
		}

		ScRequestItem scRequestItem = new ScRequestItem();
		scRequestItem.setItemName(itemName);
		scRequestItem.setCmItem(ctItem.getScRequestItem().getCmItem());
		scRequestItem.getCmItem().setExtensionCode(itemcode);

		return scRequestItem;

	}
	// #31558 end
		
	public String contactItemProd(CtContractItem ctItem) {
		//String result=new String();
		
		String itemcode = ctItem.getScRequestItem().getCmItem().getExtensionCode();
		String itemName = ctItem.getScRequestItem().getItemName();
		String sKuNo = ctItem.getSku();
		if (itemcode == null) {
			itemcode = ParameterConst.ITEM_CODE;
		}
		if (itemName == null) {
			itemName = ParameterConst.ITEM_NAME;
		}
		if (sKuNo == null) {
			sKuNo = ParameterConst.SKUNO;
		}
		String result = itemName + "," + itemcode + "," + sKuNo;

		/*result.append(itemcode);
		result.append(",");
		result.append(itemName);
		*/
	
		return result;
	     
	}
		
	/**
	* Gets the local goods radio values.
	* 
	* @param itemType
	* the item type
	* @return the local goods radio values
	*/
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);
		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode().equals(location)) {
						items[i++] = new SelectItem(paramDesc.getPmParameter().getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} finally {

		}
		return items;
	}
		
	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	//Bug 34142, 15851 - shaqimi start 06/12/2022
	public void preRenderAmendment() {
		renewSesionObject();
		ctBackingBean.renewSesionObject();//Bug 34142, 15851 - shaqimi 06/12/2022
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"amendmentTab.xhtml?faces-redirect=true");
	}
	//Bug 34142, 15851 - shaqimi end 06/12/2022
		
	//#15851 - shaqimi,putra start
	public void preRenderAmendView() throws UnsupportedEncodingException {
		ConstructTabSideMenu();
		ctBackingBean.getManagedBean().setViewContract(true);
		return;
	}
	//#15851 - shaqimi,putra end
	
	public void preRenderView() throws UnsupportedEncodingException {

		IContractAmendmentSO contractAmendmentSO = null;
		ContractReqDTO ctReqDTO = null;
		ContractResDTO resDTO = null;
		try{
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			//#25956 - Start (view Contract Amendment url from notification)
			String contractVerId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);
			//#25956 - End (view Contract Amendment url from notification)
			log.info("Check Putra Amendment preRenderView taskId*******"+taskId);
			log.info("Check Putra Amendment preRenderView contractVerId*******"+contractVerId);

			//Bug 34142, 15851 - shaqimi start 04/12/2022
			if(!ctBackingBean.getManagedBean().isInPage()){
				if(ctBackingBean.getManagedBean().isBackFromSCL()){
					getManagedBean().setSelectedTabIndex(0);
					ctBackingBean.getManagedBean().setBackFromSCL(false);
				}
				
				if(!ctBackingBean.getManagedBean().isAmendFromTaskList()){
					if(taskId != null && !taskId.isEmpty()){
						ctBackingBean.getManagedBean().setTaskIdFromTaskList(taskId);
					} else if(ctBackingBean.getManagedBean().getTaskIdFromTaskList() != null && 
							!ctBackingBean.getManagedBean().getTaskIdFromTaskList().isEmpty()){
						taskId = ctBackingBean.getManagedBean().getTaskIdFromTaskList();
					}
				}
				ctBackingBean.getManagedBean().setInPage(true);
			}
			//Bug 34142, 15851 - shaqimi end 04/12/2022
			
			// #25956 - Start (view Contract Amendment url from notification)
			if (contractVerId != null) {

				ctReqDTO = new ContractReqDTO();
				ctReqDTO.setContractVerId(Long.parseLong(contractVerId));
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
				ctReqDTO.setUpdateAction(false);
				ctReqDTO.setViewOnly(true);
				getManagedBean().setBackToNtf("notifications"); // 25956
				getManagedBean().setViewOnly(true);
				getManagedBean().setSelectedTabIndex(0);				

				setAmendmentContractVer(ctReqDTO);
				ctBackingBean.getManagedBean().setViewContract(true); // 25956
				ctBackingBean.getManagedBean().setDisableAmendDetails(true); // 25956
				ConstructTabSideMenu();
				getManagedBean().setTaskId(taskId);
				return;
			}
			// #25956 - End (view Contract Amendment url from notification)
			
			if (taskId == null) {
				ConstructTabSideMenu();

				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
						&& ctBackingBean.getManagedBean().isServiceTaxAlertFlg()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, 
									MessageUtils.getMessage(MessageConst.INFO_755004,
											MessageUtils.BUNDLE_COMMON_MESSAGES, 
											new FacesScopes().getUserLocale())));
					ctBackingBean.getManagedBean().setServiceTaxAlertFlg(false);				
				}

				// During supplier claim the task
				if (getManagedBean().getAckFA() != null && getManagedBean().getAckFA().equals(ContractConst.SUPPLIER_ACK_LOA)) {
					BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
					getManagedBean().setReadOnly(!bb.getManagedBean().getClaim());
					// Default the supplier signer to claimant
					if (!getManagedBean().isReadOnly()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy() == null
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy().getUserId() == null)) {
						ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setAckBy(
								getFacesScopes().getUserSessionBean());
					}
				}	
				//Start Fix- #29368 / #30856
				//Amendment with supplement
				
				if(ctBackingBean.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT)){
					//log.info("contract param from amendmentbackingbean :: "+ctBackingBean.getManagedBean().getContractVer().getDocType());
					checkandResetBondExempted();
				  }
				//End Fix- #29368 / #30856
				return;
			}
			
			//#15851 start
			if(ctBackingBean.getManagedBean().getDocTypeFromTaskList()!=null){
				ctReqDTO = new ContractReqDTO();
				ctReqDTO.setUpdateAction(false);
				ctReqDTO.setContractId(ctBackingBean.getManagedBean().getContractIdFromTaskList());
				ctReqDTO.setContractVerId(ctBackingBean.getManagedBean().getContractVerIdFromTaskList());
				ctReqDTO.setTaskName(ctBackingBean.getManagedBean().getTaskNameFromTaskList());
				ctReqDTO.setDocType(ctBackingBean.getManagedBean().getDocTypeFromTaskList());
				if(ctBackingBean.getManagedBean().getTaskNameFromTaskList()!=null){
					getManagedBean().setTaskName(ctBackingBean.getManagedBean().getTaskNameFromTaskList());
				}
				setAmendmentContractVer(ctReqDTO);
					
				getManagedBean().setViewOnly(false);
			}else{
				
				/*Retrieve bpm details*/
				getManagedBean().setSelectedTabIndex(0);
				CTContractAmendmentDataType dataType =  super.getBpmTaskDetails(CTContractAmendmentDataType.class, null);
				
				//#34097/#26186 start
				/* check bpm version*/
				checkBPMVersion(getManagedBean().getBpmVersionNo());
				//#34097/#26186 end
				
				// #25956 - Start
				/* re-Setting Cancel button */
				if (this.getManagedBean().getOutComes() != null) {
					String[] str_array = this.getManagedBean().getOutComes();
					List<String> list = new ArrayList<String>(Arrays.asList(str_array));
					if (list.contains(BpmOutcomeConst.BPM_CANCEL)) {
						list.remove(BpmOutcomeConst.BPM_CANCEL);
						list.add(BpmOutcomeConst.BPM_CANCEL);
						str_array = list.toArray(new String[0]);
						this.getManagedBean().setOutComes(null);
						this.getManagedBean().setOutComes(str_array);
					}
				}
				// #25956 - End
	
				/* Setting save button as per requirement*/
				String[] outcome = new String[getManagedBean().getOutComes().length];
				int i = 0; 
				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME) 
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
					outcome = new String[getManagedBean().getOutComes().length+1];
					outcome[i++] = BpmOutcomeConst.BPM_SAVE;
				}
				for (String action : getManagedBean().getOutComes()) {
					outcome[i++] = action;
				}
				getManagedBean().setOutComes(outcome);
	
				/*Retrieve contract amendment details - Bug 34142, 15851 - shaqimi start 04/12/2022 - revert old code 10 months ago*/
				ctReqDTO = new ContractReqDTO();
				ctReqDTO.setUpdateAction(false);
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
				ctReqDTO.setTaskName(getManagedBean().getTaskName());
				setAmendmentContractVer(ctReqDTO);
				//Bug 34142, 15851 - shaqimi end 04/12/2022 - revert old code 10 months ago
				
				//Bug 34142, 15851 - shaqimi start 04/12/2022
				ctBackingBean.getManagedBean().setAmendFromViewPage(false);
				ctBackingBean.getManagedBean().setBackFromSCL(false);
				ctBackingBean.getManagedBean().setContractIdFromTaskList(new Long(dataType.getContractId()));
				ctBackingBean.getManagedBean().setContractVerIdFromTaskList(new Long(dataType.getDocId()));
				ctBackingBean.getManagedBean().setDocTypeFromTaskList(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
				ctBackingBean.getManagedBean().setTaskNameFromTaskList(getManagedBean().getTaskName());
				//Bug 34142, 15851 - shaqimi start 04/12/2022
			}
			//#15851 end
			
			/*Define boolean value to control gui details*/
			getManagedBean().setSelectedItem(null);
			getManagedBean().setSelectedServiceItem(null);
			getManagedBean().setMemberSignOff(false);  
			getManagedBean().setPbConfirmMeeting(false);
			getManagedBean().setSignUpdateDecision(false);
			getManagedBean().setLetterOfAcceptance(false);
			getManagedBean().setPrepareLOA(false);
			getManagedBean().setDecisionPbQcLOA(false);
			getManagedBean().setSignOffDecision(false);
			
//			getManagedBean().setViewOnly(false); /*#25956 notification setting*/

			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class); 
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
				getManagedBean().setPbConfirmMeeting(true);
				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
					getManagedBean().setSignUpdateDecision(true);
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbQcApprovedFlag() == null) {
						getManagedBean().setPbQcApproved("");
					} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbQcApprovedFlag() == 1) {
						getManagedBean().setPbQcApproved("A");
					} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbQcApprovedFlag() == 0) {
						getManagedBean().setPbQcApproved("R");
					}
				}
			} 
			else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME)) {
				getManagedBean().setSignUpdateDecision(true);  
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofApprovedFlag() == null) {
					getManagedBean().setMofApproved("");
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofApprovedFlag() == 1) {
					getManagedBean().setMofApproved("A");
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofApprovedFlag() == 0) {
					getManagedBean().setMofApproved("R");
				}
			}
			else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				getManagedBean().setLetterOfAcceptance(true);
				getManagedBean().setPrepareLOA(true);
				getManagedBean().setPbQcApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbQcApproved() ? "A" : "R");
				getManagedBean().setMofApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofApproved() ? "A" : "R");

				//retrieve approver from CtActor
				String roleCode = null;
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					roleCode = RoleCode.CT_APPROVER.value();
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_CENTRAL)) {
					roleCode = RoleCode.BPK_CC_APPROVER.value();
				}
				CtActor approverActor = retrieveContractActor(
						ctBackingBean.getManagedBean().getContractVer().getContractVerId(),
						ctBackingBean.getManagedBean().getContractVer().getDocType(),
						roleCode);
				if (approverActor != null) {	  
					getManagedBean().setApproverName(approverActor.getUserName());
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofAuthorityReq()) {
					getManagedBean().setDecisionPbQcLOA(true);

				} if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() == null) {
					ctReqDTO = new ContractReqDTO();
					ctReqDTO.setCtContractVer(ctBackingBean.getManagedBean().getContractVer());
					resDTO = contractAmendmentSO.setScLoa(ctReqDTO);
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentLoa(resDTO.getAmendmentScLoa());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin() == null) {
						ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractAdmin(getFacesScopes().getUserSessionBean());
					}
				}if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)){
					getManagedBean().setDisableLoaLetter(true);
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
				this.getManagedBean().setMemberSignOff(true);
				this.getManagedBean().setPbConfirmMeeting(true);
				this.getManagedBean().setSignUpdateDecision(false);
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				this.getManagedBean().setAckFA(ContractConst.SUPPLIER_ACK_LOA);
				setBankDetails(); // Fix - #33674
				changeAccountActionListener();		

			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME) 
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)) {
				this.getManagedBean().setSignOffDecision(true);
				getManagedBean().setPbQcApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbQcApproved() ? "A" : "R");
			}

			if (this.getManagedBean().isLetterOfAcceptance()) {
				getLoaDocumentList();
			}
			
			ConstructTabSideMenu();
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			contractAmendmentSO = null;
			resDTO = null;
		}
	}

	/**
	 * The method used to navigate to contract maintenance screen.
	 * 
	 * @return the navigate URL 
	 * @throws IOException 
	 * //25956
	 */
	public String backAction() throws IOException {
		// 25956 - Start ( Back to notification pages )
		if (getManagedBean().getBackToNtf() != null) {

			getManagedBean().setBackToNtf("");
			getManagedBean().setViewOnly(false);
			ctBackingBean.getManagedBean().setViewContract(false);
			ctBackingBean.getManagedBean().setDisableAmendDetails(false);

			String commonUrl = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(commonUrl + "notifications");

			return null;
		}
		// 25956 - End ( Back to notification pages )
		
		//Bug 34142, 15851 - shaqimi start 04/12/2022
		ctBackingBean.getManagedBean().setInPage(false);
		
		if(ctBackingBean.getManagedBean().isViewSCL()){
			return viewSCLBackAction();
		}
		//Bug 34142, 15851 - shaqimi end 04/12/2022
		this.getManagedBean().setSecretaryList(null);
		//Bug 34030 - shaqimi start
		ctBackingBean.getManagedBean().setAmendFromTaskList(false);
		ctBackingBean.getManagedBean().setAmendFromViewPage(false);
		ctBackingBean.getManagedBean().setViewSCL(false);
		ctBackingBean.getManagedBean().setHasAmendmentLoaNo(false);
		ctBackingBean.getManagedBean().setViewContract(false);
		ctBackingBean.getManagedBean().setBackFromSCL(false);
		this.getManagedBean().setViewOnly(false);
		//Bug 34030 - shaqimi end
		return CONTRACT_MAINT_PAGE;
		//Enhancement 15851 - shaqimi end
	}
	
	//#15851 start
	/**
	 * The method used to set and navigate return page from SL link.
	 * 
	 * @return the navigate URL
	 * @exception SLAUtils
	 */
	public String viewSCLBackAction(){
		ContractDetailsBackingBean ctDT =  new ContractDetailsBackingBean();
		Long contractId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId();
		Long contractVerId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
		
		ContractReqDTO ctReqDTO = new ContractReqDTO();//Bug 34142, 15851 - shaqimi
		ctReqDTO.setContractId(contractId);
		ctReqDTO.setContractVerId(ctBackingBean.getManagedBean().getContractVerIdFromTaskList());
		ctReqDTO.setDocType(ctBackingBean.getManagedBean().getDocTypeFromTaskList());
		ctReqDTO.setUpdateAction(false);
		
		try{
			if(ctBackingBean.getManagedBean().isFromViewPage()){//contmaintenance > view
				ctBackingBean.getManagedBean().setViewContract(false);
				ctBackingBean.getManagedBean().setViewSCL(false);
				getManagedBean().setDisableFinalAmendDetails(false);
				getManagedBean().setDisableFields(false);
				
				return ctDT.gotoViewPage(contractId, contractVerId);
				
			} else if(ctBackingBean.getManagedBean().isAmendFromViewPage()){ //contractmaintenance > amendment
				ctBackingBean.getManagedBean().setViewContract(false);
				ctBackingBean.getManagedBean().setViewSCL(false);
				getManagedBean().setDisableFinalAmendDetails(false);
				getManagedBean().setDisableFields(false);
				
				return ctDT.gotoAmendPage(contractId);
			} else if(ctBackingBean.getManagedBean().getDocTypeFromTaskList() != null || 
					ctBackingBean.getManagedBean().getDocTypeFromAgreementTaskList() != null){
				
				Integer i = 0;
				HashMap<Integer, String> docTypeList = new HashMap<>();
				docTypeList.put(i++, ParameterConst.DOCTYPE_NOTIFICATION_SETTING); //Notification Setting
				docTypeList.put(i++, ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT); //Amendment without Supplement
				docTypeList.put(i++, ParameterConst.DOCTYPE_CONTRACT_UPDATE); //Fulfilment Update
				docTypeList.put(i++, ParameterConst.DOCTYPE_REQUEST_CT_ADMIN); //Designation of Contract Administrator Request 
				docTypeList.put(i++, ParameterConst.DOCTYPE_REMOVE_REQUEST_CT_ADMIN); //Designation of Contract Administrator Remove
				docTypeList.put(i++, ParameterConst.DOCTYPE_CONTRACT_TERMINATION); //Termination
				docTypeList.put(i++, ParameterConst.DOCTYPE_UPDATE_FACTORING); //Supplier - Update Factoring
				docTypeList.put(i++, ParameterConst.DOCTYPE_REQUEST_FACTORING); //Supplier - Request Factoring
				docTypeList.put(i++, ParameterConst.DOCTYPE_REMOVE_FACTORING); //Supplier - Remove Factoring
				docTypeList.put(i++, ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT); //Supplier - Change Bank Account
				docTypeList.put(i++, ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT); //Change PTJ
				docTypeList.put(i++, ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE); //Suspend
				
				if(ctBackingBean.getManagedBean().getDocTypeFromTaskList() != null
						&& docTypeList.containsValue(ctBackingBean.getManagedBean().getDocTypeFromTaskList())){
					ctBackingBean.getManagedBean().setViewContract(false);
					ctBackingBean.getManagedBean().setViewSCL(false);
					
					switch (ctBackingBean.getManagedBean().getDocTypeFromTaskList()){
					case ParameterConst.DOCTYPE_NOTIFICATION_SETTING:
						if(ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.NT_PENDING_SUBMISSION)
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoNotificationSetting(contractId);
						}else{
							ctDT.setValidateContractField(ctReqDTO);
						}
						break;
						
					case ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT:
					case ParameterConst.DOCTYPE_CONTRACT_UPDATE:
						if((ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.FW_PENDING_SUBMISSION)
								|| ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.FU_PENDING_SUBMISSION))
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoUpdatePage(contractId, 
									ctBackingBean.getManagedBean().getDocTypeFromTaskList().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)
									? 1 : 2);
						}else{
							ctDT.setValidateContractField(ctReqDTO);
						}
						break;
						
					case ParameterConst.DOCTYPE_REQUEST_CT_ADMIN: 
					case ParameterConst.DOCTYPE_REMOVE_REQUEST_CT_ADMIN:
						getManagedBean().setViewOnly(false);
						ctBackingBean.getManagedBean().setHasAmendmentLoaNo(false);
						
						if((ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.RA_PENDING_SUBMISSION)
								|| ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.XA_PENDING_SUBMISSION)
								)&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.goToRequestCtAdmin(contractId, ctBackingBean.getManagedBean().getDocTypeFromTaskList());
						}else{
							RequestCtAdminBackingBean rcaBB = new RequestCtAdminBackingBean();
							rcaBB.getManagedBean().setViewOnly(false);
							rcaBB.getManagedBean().setExternalPageUrl(null);
							rcaBB.setRequestCtAdminContractVer(ctReqDTO);
						}
						break;
						
					case ParameterConst.DOCTYPE_CONTRACT_TERMINATION:
						if(ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.FT_PENDING_SUBMISSION)
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoTerminatePage(contractId);
						}else{
							TerminationBackingBean trBB = new TerminationBackingBean();
							trBB.setTerminationContractVer(ctReqDTO);
						}
						break;

					case ParameterConst.DOCTYPE_REQUEST_FACTORING:
					case ParameterConst.DOCTYPE_UPDATE_FACTORING:
					case ParameterConst.DOCTYPE_REMOVE_FACTORING:
						if((ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.RF_PENDING_SUBMISSION)
								|| ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.UF_PENDING_SUBMISSION)
								|| ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.XF_PENDING_SUBMISSION))
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							ctDT.gotoFactoringPage(contractId, 
									ctBackingBean.getManagedBean().getDocTypeFromTaskList().equalsIgnoreCase(ParameterConst.DOCTYPE_UPDATE_FACTORING)
											? true : false);
						}else{
							SupplierBackingBean sBB = new SupplierBackingBean();
							ContractSupplierReqDTO reqDTO = new ContractSupplierReqDTO();
							reqDTO.setFactoringId(ctBackingBean.getManagedBean().getContractVerIdFromTaskList());
							reqDTO.setDocType(ctBackingBean.getManagedBean().getDocTypeFromTaskList());
							sBB.setFactoringDetails(reqDTO);
						}
						break;
					
					case ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT:
						if(ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.CB_PENDING_SUBMISSION)
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoChangeBankPage(contractId);
						}else{
							SupplierBackingBean sBB = new SupplierBackingBean();
							sBB.setChangeBankDetails(ctReqDTO);
						} 
						break;
						
					case ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT:
						if(ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.COM_PENDING_SUBMISSION)
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoOwnerManagementPage(contractId);
						}else{
							ctDT.setValidateContractField(ctReqDTO);
						}
						break;
						
					case ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE:
						if(ctBackingBean.getManagedBean().getStatusIdFromTaskList().equals(ContractStatusConst.FS_PENDING_SUBMISSION)
								&& ctBackingBean.getManagedBean().getContractVerIdFromTaskList()==null){
							return ctDT.gotoSuspendPage(contractId);
						}else{
							SuspensionBackingBean spBB = new SuspensionBackingBean();
							spBB.setSuspensionContractVer(ctReqDTO);
						}
						break;
					}
				} else if(ctBackingBean.getManagedBean().isAmendFromTaskList()){//tasklist
					
					//if it goes scl link first, this will be fired as ViewSCLFromContract is false
					if(!ctBackingBean.getManagedBean().isViewSCLFromContract()){
						ctBackingBean.getManagedBean().setViewSclFromAgreement(false); //#15851
					}
					//this is back button from contract link > scl link > back button. if it goes contract link > scl link > back button, 
					//this will be fired first before going on the scl page > back button.
					if(ctBackingBean.getManagedBean().isViewContractFromAgreement() && ctBackingBean.getManagedBean().isViewSCLFromContract()){
						ctBackingBean.getManagedBean().setViewSCLFromContract(false);
						return this.getManagedBean().getBackToPreviousPage();
					}
					
					if(!ctBackingBean.getManagedBean().isViewSclFromAgreement() && !ctBackingBean.getManagedBean().isViewSCLFromContract()){ //#15851
						getManagedBean().setViewOnly(false);
						ctBackingBean.getManagedBean().setViewContract(false);
						ctBackingBean.getManagedBean().setAmendFromTaskList(false);
						ctBackingBean.getManagedBean().setHasAmendmentLoaNo(false);
						
						if(getManagedBean().getCtLetterOfAcceptance() != null){
							getManagedBean().getCtLetterOfAcceptance().clear();
						}
					}
				}
			}
		}
		catch(Exception e){
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "viewSCLBackAction", 
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(), "viewSCLBackAction", getUserIdSessionFromLoginId(), e);
		}
		ctBackingBean.getManagedBean().setViewSCL(false);
		ctBackingBean.getManagedBean().setBackFromSCL(true);
		return this.getManagedBean().getBackToPreviousPage();
	}
	//#15851 end

	//#15851 - shaqimi,putra start
	/**
	 * The method used to navigate to supplemental contract view screen.
	 * 
	 * @return the navigate URL 
	 */
	public String backAmendViewAction() {
		ContractDetailsBackingBean ctDT =  new ContractDetailsBackingBean();
		ContractReqDTO reqDTO = new ContractReqDTO();
		Long contractId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId();
		
		if(ctBackingBean.getManagedBean().isFromViewPage()){
			Long contractVerId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId();
			ctDT.gotoViewPage(contractId, contractVerId);
			
			return CONTRACT_PAGE;
		}else if(ctBackingBean.getManagedBean().isAmendFromViewPage()){
			ctDT.gotoAmendPage(contractId);
			
			return PROCESS_AMEND_PAGE;
		} else {
			Long contractVerId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getCurrentContractVer().getContractVerId();
			reqDTO.setContractId(contractId);
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			reqDTO.setUpdateAction(false);
			getManagedBean().setSelectedTabIndex(0);
			this.getManagedBean().setSecretaryList(null);
			this.getManagedBean().setViewOnly(false);
			setAmendmentContractVer(reqDTO);
	
			ctBackingBean.getManagedBean().setViewContract(true);
			ctBackingBean.getManagedBean().setHasAmendmentLoaNo(false);
			return (this.getManagedBean().getBackToPreviousPage() != null) ? this.getManagedBean().getBackToPreviousPage() : CONTRACT_MAINT_PAGE;
		}
	}
	//#15851 - shaqimi,putra end

	/**
	 * The method used to set the contract amendment information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	@SuppressWarnings("unchecked") //#36726
	public void setAmendmentContractVer(ContractReqDTO reqDTO) {	
		IContractAmendmentSO contractAmendmentSO = null;
		ContractResDTO resDTO = null;
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		IAgreementSO agreementSO = null;
		ContractAgreementResDTO ctAgreementResDTO = null;
		ContractAgreementReqDTO ctAgreementReqDTO = null;
		if(ctBackingBean.getManagedBean().getDeleteAttachmentList() != null ){
			ctBackingBean.getManagedBean().getDeleteAttachmentList().clear();
		}
		try {
			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			log.info("Check Putra gotoAmendPage setAmendmentContractVer >START<");
			resDTO = contractAmendmentSO.retrieveContractAmendVerDtls(reqDTO);
			log.info("Check Putra gotoAmendPage setAmendmentContractVer resDTO.getCtContractVer()"+resDTO.getCtContractVer().getContractVerId());
			ctBackingBean.getManagedBean().setContractVer(resDTO.getCtContractVer());
			//26753 - start  #29403 start (comment old code)
//			if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(resDTO.getCtContractVer().getStatus().getStatusId())) {
//				ContractReqDTO ctReqDTO = new ContractReqDTO();		
//				ContractResDTO ctResDTO = null;
//				contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
//				ctReqDTO.setAmendmentId(resDTO.getCtContractVer().getCtAmendment().getAmendmentId());
//				ctResDTO = contractAmendmentSO.retrieveContractCommitteeMemberByAmendmentId(ctReqDTO);
//				getManagedBean().setUpdatedCommitteeMembers(ctResDTO.getCtCommitteeMembers());
//				getManagedBean().setHasChanges(Boolean.FALSE);
//				getManagedBean().setFilteredUpdatedCommitteeMembers(new ArrayList<CtCommitteeMember>());
//				for(CtCommitteeMember newMember: getManagedBean().getUpdatedCommitteeMembers()) {
//					if(newMember.getUpdateType() != null) {
//						getManagedBean().setHasChanges(Boolean.TRUE);		
//						break;
//					}
//				}
//			}
			//26753 - end   #29403 end (comment old code)
			
			//retrieve agreement list
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IAgreementSO.class);
			ctAgreementReqDTO = new ContractAgreementReqDTO();
			ctAgreementReqDTO.setContractId(resDTO.getCtContractVer().getCtContract().getContractId());
			ctAgreementReqDTO.setViewOnly(true);//Bug 34142 - shaqimi 04/12/2022
			ctAgreementResDTO = agreementSO.retrieveContractAgreements(ctAgreementReqDTO);
			ctBackingBean.getManagedBean().setCtAgreementList(ctAgreementResDTO.getAgreementList());
			
			//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();

			//CT_CHANGE_LOG
			ctBackingBean.getManagedBean().setNewAgencyMap(resDTO.getNewAgencyMap());
			ctBackingBean.getManagedBean().setUpdateAgencyMap(resDTO.getUpdateAgencyMap());
			ctBackingBean.getManagedBean().setDeleteAgencyMap(resDTO.getDeleteAgencyMap());
			ctBackingBean.getManagedBean().setUpdateZoneMap(resDTO.getUpdateZoneMap());
			ctBackingBean.getManagedBean().setUpdateItemMap(resDTO.getUpdateItemMap());
			
			reqDTO = new ContractReqDTO();
			reqDTO.setContractVerId(ctBackingBean.getManagedBean().getContractVer().getContractVerId());
			reqDTO.setContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);

			/*Setting extension history list for amendment*/
			getManagedBean().setAmendHistoryList(contractAmendmentSO.retrieveExtensionHistory(reqDTO).getExtensionHistoryList());
			getManagedBean().setNewAmendmentEffDate(retrieveNewEffDate());

			// Initialize new amendment details
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment() == null) {
				ctBackingBean.getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setFileRefNo("");
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setTitle(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractName());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setDescription(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractName());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getNewAmendmentEffDate());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount());	
				} else {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(BigDecimal.ZERO);	
				}
				
				if ( ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getStaxAmount() != null) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewStaxAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getStaxAmount());
				} else {	
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewStaxAmount(BigDecimal.ZERO);
				}
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(0);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(null);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(null);
			}

			if (getManagedBean().getTaskName() != null 
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				PmUser supplierSigner = new PmUser();
				if (!getManagedBean().isReadOnly()) {
					supplierSigner = getFacesScopes().getUserSessionBean();
				}
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setAckBy(supplierSigner);
			}

			getManagedBean().setSecretaryId(null);
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary() != null) {
				getManagedBean().setSecretaryId(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary().getUserId());
			}

			//Enhancement 15851 - Prefix for supplemental contract letter
			//start
			ctBackingBean.getManagedBean().setHasAmendmentLoaNo(false);
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() != null){
				String slLoaNo = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaNo();
				if(StringUtils.isNotBlank(slLoaNo)
						&& slLoaNo.substring(0,2).equals(ParameterConst.DOCTYPE_SUPPLEMENTAL_CONTRACT_LETTER)
						&& !ctBackingBean.getManagedBean().isViewSCL()){
					ctBackingBean.getManagedBean().setHasAmendmentLoaNo(true);
				}
			}
			if (ctBackingBean.getManagedBean().isViewSCL()) { //33096					
				getManagedBean().setLetterOfAcceptance(true);
				getManagedBean().setPrepareLOA(true);
				getManagedBean().setPbQcApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbQcApproved() ? "A" : "R");
				getManagedBean().setMofApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofApproved() ? "A" : "R");
			}
			//Enhancement 15851 - end

			// Retrieve agencies
			if (ctBackingBean.getManagedBean().getContractVer().getCtAgencies() != null 
					&& this.ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
				ctBackingBean.getManagedBean().setSelectedAgency(null);
				ctBackingBean.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
						ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
				for (CtAgency ctAgency : ctBackingBean.getManagedBean().getContractVer().getCtAgencies()) {
					// If effective date is after current date, status is still display old status
					ctBackingBean.checkAgencyStatus(ctAgency);
					if (ctBackingBean.getManagedBean().getMinistryProfileId().equals(ctAgency.getOrgProfile().getMinstryId())) {
						ctAgency.setAp178(false);
					} else {
						ctAgency.setAp178(true);
					}
				}
			}
			
			// Retrieve Items
			ctBackingBean.getManagedBean().getCtItemList().clear();
			ctBackingBean.getManagedBean().getCtItemServiceList().clear();
			//Bug #29323
			AmendmentBackingBean abb=new AmendmentBackingBean();
			abb.getManagedBean().reassignValue(true);
			abb.getManagedBean().getServiceListingPagingDataModel().reloadData();
			abb.getManagedBean().getContractListingPagingDataModel().reloadData();
			getManagedBean().setItemProductExist(false);
			getManagedBean().setItemServiceExist(false);
			ctBackingBean.getManagedBean().setShowItem(null);
			ctBackingBean.getManagedBean().setShowServiceItem(null);
			getManagedBean().setSelectedItem(null);
			getManagedBean().setSelectedServiceItem(null);
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtContractItems().size() > 0) {				
				for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
					// If effective date is after current date, status is still display old status
					if (item.getRecordStatusFlg() == 0) {				       		
						if (item.getEffDate().after(currentDate)) {
							item.setChkStatus(true);
						} else {
							item.setChkStatus(false);
						}
					} else {
						if (item.getEffDate().after(currentDate)) {
							item.setChkStatus(false);
						} else {
							item.setChkStatus(true);		        		   
						}
					}
					
					if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
						getManagedBean().setItemProductExist(true);
						//Collections.sort(item.getCtItemPrices(), new CtItemPrice());
						//Start Fix - #33256
						sortItemPrice(item.getCtItemPrices());
						sortItemMaxQty(item.getCtItemMaxQties());
						//End Fix - #33256
				
						//Bug #29323
//						Collections.sort(item.getCtItemPrices(), new CtItemPrice());
						//Start Fix - #34110 , #34950
						if (item.getCtItemPrices() != null) {
							if (item.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
								sortItemPriceForZonalTier(item.getCtItemPrices());
							} else if (item.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)) {
								sortItemPriceForTier(item.getCtItemPrices());
							} else {
								if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
									sortItemPriceForZonal(item.getCtItemPrices());
								} else {
									sortItemPrice(item.getCtItemPrices());
								}
								
							}
						}
						

						if (item.getCtItemMaxQties() != null) {
							if(ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
								sortItemMaxQtyForZonal(item.getCtItemMaxQties());
							}else{
								sortItemMaxQty(item.getCtItemMaxQties());
							}
							
						}
						//End Fix - #34110 , #34950
						ctBackingBean.getManagedBean().getCtItemList().add(item);
					} else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
						getManagedBean().setItemServiceExist(true);
						//Collections.sort(item.getCtItemPrices(), new CtItemPrice());
						//Start Fix - #34110 , #34950
						if (item.getCtItemPrices() != null
								&& item.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
							   sortItemPriceForZonalTier(item.getCtItemPrices());
						}else if(item.getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)){
							   sortItemPriceForTier(item.getCtItemPrices());
						} else {
							if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
								sortItemPriceForZonal(item.getCtItemPrices());
							} else {
								sortItemPrice(item.getCtItemPrices());
							}
						}
						//End Fix - #34110 , #34950
						ctBackingBean.getManagedBean().getCtItemServiceList().add(item);
					}					
				}
			}
			// Retrieve Zone
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractZones() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
				CustomUtils.sort(ctBackingBean.getManagedBean().getContractVer().getCtContractZones(), new CtContractZoneComparator()); //#36726
				ctBackingBean.retriveZonesByContractVer();
			}

			// Retrieve Deduction Clauses
			if (ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
				ctBackingBean.getManagedBean().setSelectedDeduction(null);
				for (CtDeduction deduct : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()) {
					if (deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)) {
						ctBackingBean.getManagedBean().setFlgPaymentTracking(true);
						ctBackingBean.getManagedBean().setAdvancePaymentAmt(deduct.getAmount());	    			
					}
				}
			}

			// Retrieve Schedules
			if (ctBackingBean.getManagedBean().getContractVer().getCtSchedules() != null 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
				ctBackingBean.getManagedBean().setChkSchedule(true);
				CustomUtils.sort(ctBackingBean.getManagedBean().getContractVer().getCtSchedules(), new CtSchedule()); //#36726
				for (CtSchedule ctSchedule : ctBackingBean.getManagedBean().getContractVer().getCtSchedules()) {
					if (ctSchedule.isUseInFulfilment()) {
						ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
						break;
					}
				}
				ctBackingBean.scheduleDate();
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				CustomUtils.sort(ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment()); //#36726
				for (CtProgressivePayment ctProgressivePayment : ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (ctProgressivePayment.isUseInFulfilment()) {
						ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
						break;
					}
				}
				ctBackingBean.progressiveDate();
			}

			if (getManagedBean().getUserMap().size() == 0) {
				initializeApproverList();
			}
			// Retrieve approver list
			if (isApproverSetting()) {
				getManagedBean().getApproverModel().setSource(
						getAvailableApproverList(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode(),
								getManagedBean().getUserMap()));
				if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
					getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
					List<PmUser> invalidApproverList = new ArrayList<PmUser>();
					for (PmUser approverUser : resDTO.getApproverList())  {
						Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
						boolean isValidApprover = false;
						while (sourceIt.hasNext()) {
							PmUser user = sourceIt.next();
							if (user.getUserId().longValue() == approverUser.getUserId().longValue()) {
								sourceIt.remove();
								isValidApprover = true;
								break;
							}
						}
						if (!isValidApprover) {
							invalidApproverList.add(approverUser);
						}
					}
					if (invalidApproverList.size() > 0) {
						getManagedBean().getApproverModel().getTarget().removeAll(invalidApproverList);
					}
				} else {
					getManagedBean().getApproverModel().setTarget(new ArrayList<PmUser>());
				} 
			}
			// Enhancement 15851 - shaqimi start
			if (((getManagedBean().getTaskName() != null && (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) 
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME))))
					|| ctBackingBean.getManagedBean().isViewSCL()) {
				// Enhancement 15851 - shaqimi end
				//Bug #33221
				getManagedBean().getCtLetterOfAcceptance().clear();
				for (int j =0; j < 2; j++) {
					CtLetterOfAcceptance ctloa = new CtLetterOfAcceptance();
					if (j == 0) {
						ctloa.setContentTypeDesc(ResourceBundleUtils.getMessage("lbl.ct.amend.loa.sst", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
						ctloa.setDocDefinitionKey(ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE);

						if (ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().size() > 0
								&& ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j).getSectionId() == ContractConst.ATTACHMENT_LOA_LETTER_SST
								&& ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j).getFilePath() != null) {
							ctloa.setLoaAttachments(ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j));
						} else {
							CtAttachment attach = new CtAttachment();
							ctloa.setLoaAttachments(attach);
						}
					} else {
						ctloa.setContentTypeDesc(ResourceBundleUtils.getMessage("lbl.ct.amend.loa.receipt", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
						ctloa.setDocDefinitionKey(ParameterConst.DOCUMENT_DEFINITION_KEY_ACK_OF_LOA_RECEIPT);

						if (ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().size() > 1
								&&  ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j).getSectionId() == ContractConst.ATTACHMENT_LOA_LETTER_RECEIPT
								&& ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j).getFilePath() != null ){
							ctloa.setLoaAttachments(ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().get(j));
						} else {
							CtAttachment attach = new CtAttachment();
							ctloa.setLoaAttachments(attach);
						}
					}
					getManagedBean().getCtLetterOfAcceptance().add(ctloa);
				}
				ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().clear();
			}

 			initializeRemarks();
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
					ModuleCodeConst.CONTRACT_MANAGEMENT, ctBackingBean.getManagedBean().getContractVer().getDocType(), ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()));

			getManagedBean().setRemarkList(resDTO.getCtRemarks());
			getManagedBean().setAttachmentList(resDTO.getCtAttachments());	
			ctBackingBean.getManagedBean().setCtContractAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount());
			if (ctBackingBean.getManagedBean().getCtContractAmount() != null
					&& ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() != null
					&& ctBackingBean.getManagedBean().getCtContractAmount().getStaxAmount() != null) {
				ctBackingBean.getManagedBean()
						.setGstTotalAmount(
								ctBackingBean
										.getManagedBean()
										.getCtContractAmount()
										.getStaxAmount()
										.add(ctBackingBean
												.getManagedBean()
												.getCtContractAmount()
												.getGstAmount()
												.add(ctBackingBean.getManagedBean().getCtContractAmount()
														.getContractAmount())));  
			// #29390 - Start
			} else if (ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() != null && ctBackingBean.getManagedBean().getCtContractAmount().getStaxAmount() == null ) {
				ctBackingBean.getManagedBean().setGstTotalAmount
							(ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount().add
							(ctBackingBean.getManagedBean().getCtContractAmount().getContractAmount()));
				
			} else if (ctBackingBean.getManagedBean().getCtContractAmount().getStaxAmount() != null && ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() == null) {
					ctBackingBean.getManagedBean().setGstTotalAmount
							(ctBackingBean.getManagedBean().getCtContractAmount().getStaxAmount().add
							(ctBackingBean.getManagedBean().getCtContractAmount().getContractAmount()));
			// #29390 - End	
			}else{
				ctBackingBean.getManagedBean().setGstTotalAmount(ctBackingBean.getManagedBean().getCtContractAmount().getContractAmount());
			}
			ctBackingBean = new ContractBackingBean(this.getManagedBean().getFileDirectory());
			ctBackingBean.getManagedBean().setViewContract(false);
			ctBackingBean.getManagedBean().setUpdateCompanyName(false);
			ctBackingBean.getManagedBean().setUpdateSupplierAddress(false);
			ctBackingBean.getManagedBean().setCtAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
			ctBackingBean.getManagedBean().setSupplierAddress(getFullAddress(ctBackingBean.getManagedBean().getCtAddress()));
			ctBackingBean.getManagedBean().setSupplierCompanyName(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			ctBackingBean.getManagedBean().setSupplierBranchName(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
			ctBackingBean.getManagedBean().setSupplierRegNo(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			ctBackingBean.getManagedBean().setSupplierMofNo(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
			ctBackingBean.retrieveBranchInfo();
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
				ctBackingBean.renderUpdateCheckbox(ctBackingBean.getManagedBean().getContractVer());
			}			    
			ctBackingBean.initializeRemarkAndAttachement(ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else {
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules()!= null  && !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules().isEmpty()) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			}  else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments() != null && !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments().isEmpty()) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}

			// Enable or Disable GST field
			if (!ctBackingBean.isLoaContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getGstEffDate())
					&& !ctBackingBean.isLoaContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				ctBackingBean.getManagedBean().setGstCheckingGUI(false); // Display N/A
			} else {
				if (ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() == null) {
					ctBackingBean.getManagedBean().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				}
				ctBackingBean.getManagedBean().setGstCheckingGUI(true);
				// If original contract is fulfilled imposed service tax rule
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
						&& ctBackingBean.isBeforeGstReliefDueDate()
						&& ctBackingBean.imposedServiceTaxCondition()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isServiceTax()
						&& ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= ctBackingBean.getGstReliefDueDate().getTime()) {
					ctBackingBean.getManagedBean().setDisableGstAmount(true);
				}
			}
			
			// Enable or Disable STAX field
			if (ctBackingBean.getManagedBean().getCtContractAmount().getStaxAmount() == null) {
				ctBackingBean.getManagedBean().getCtContractAmount().setStaxAmount(BigDecimal.ZERO);				
			}
			ctBackingBean.getManagedBean().setStaxCheckingGUI(true);
			
			// Enable or Disable New GST field
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount() == null) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(BigDecimal.ZERO);			
			}
			
			
			// Enable or Disable NewSTAX field
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewStaxAmount() == null) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewStaxAmount(BigDecimal.ZERO);				
			}
			
			// 24333 - Start
			List<CtContractAmount> ctAmounts = ctBackingBean.getManagedBean().getContractVer().getCtContractAmounts();
			BigDecimal overall = BigDecimal.ZERO;
			BigDecimal totalBond = BigDecimal.ZERO;
			BigDecimal periodBond = BigDecimal.ZERO;

			if (ctAmounts != null && !ctAmounts.isEmpty()) {
			    CtContractAmount selectedAmount = getSelectedContractAmount(ctAmounts);
			    if (selectedAmount != null) {
			        overall = selectedAmount.getOverallCtAmtPbond() != null ? selectedAmount.getOverallCtAmtPbond() : BigDecimal.ZERO;
			        totalBond = selectedAmount.getTotalBondAmt() != null ? selectedAmount.getTotalBondAmt() : BigDecimal.ZERO;
			        periodBond = new BigDecimal(selectedAmount.getContractPeriodBond());
			    }
			}

			ctBackingBean.getManagedBean().setOriginalOverallAmount(overall);
			ctBackingBean.getManagedBean().setTotalBondAmt(totalBond);
			ctBackingBean.getManagedBean().setBondPercentage(PerformanceBondUtil.calculatePerformanceBondPercentage(overall, periodBond));
			// 24333 - End
			
			// Amendment process	  
			// Redmine 19600 - Amendment with Supplement
			if(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().longValue() >= 20000L){
				getManagedBean().setCommitteeType(retrieveCommitteeType());
			}
			ctBackingBean.getManagedBean().setDisableAmendDetails(false);
			if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) 
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME))) {
				ctBackingBean.getManagedBean().setDisableAmendDetails(true);
			}
			if (getManagedBean().getTaskName() != null 
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				getManagedBean().setDisableEndorseAmendDetails(true);
			}
			if (getManagedBean().getTaskName() != null 
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME))) {
				getManagedBean().setDisableFinalAmendDetails(true);
			}
			if (getManagedBean().getTaskName() != null 
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
				getManagedBean().setDisableMeetingDetails(true);
				ctBackingBean.getManagedBean().setViewContract(true);
			}
			
			// reset
			ctBackingBean.getManagedBean().setContractExpired(false);
			
			if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getRecordStatusFlg() == 3) { 
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setContractExtend(true);
				ctBackingBean.getManagedBean().setContractExpired(true);
			}
			
			//#29403 start
			getManagedBean().setUpdateCommittee(Boolean.FALSE);
			if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(resDTO.getCtContractVer().getStatus().getStatusId()) ||
					ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(resDTO.getCtContractVer().getStatus().getStatusId())) {	 //#26688
				copyCommitteeMember();
//				getManagedBean().setUpdatedCommitteeMembers(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers());
//				getManagedBean().getBackupCtCommitteeMembers().clear();
//				getManagedBean().getBackupCtCommitteeMembers().addAll(getManagedBean().getUpdatedCommitteeMembers());
				getManagedBean().setHasChanges(Boolean.FALSE);
				getManagedBean().setFilteredUpdatedCommitteeMembers(new ArrayList<CtCommitteeMember>());
				for(CtCommitteeMember newMember: getManagedBean().getUpdatedCommitteeMembers()) {
					if(newMember.getUpdateType() != null) {
						getManagedBean().setHasChanges(Boolean.TRUE);		
						break;
					}
				}
			}
			//#29403 end
			
			// #25956 start
			if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(
							CTProcessNameConst.ACK_FA_CANCEL_MEETING_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(
									CTProcessNameConst.ACK_FA_CANCEL_MEETING_DECISION_HT_NAME) || getManagedBean()
							.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_REJECT_LOA_HT_NAME))) {
				ctBackingBean.getManagedBean().setDisableAmendDetails(true);
				ctBackingBean.getManagedBean().setViewContract(true);
			}
			// #25956 end
			
			//#13847 start
			//check & set performance bond deferment
			chkBondWaivedAttachment();
			//#13847 end
			
			//Enhancement #26186/#29793/#32860/#34097 start  start - retrieve disciplinary info
			if (checkContractConditionForSupplier()) {
				if (!ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId()
						.equals(ContractStatusConst.FA_PENDING_VERIFICATION)) {
					checkAndSetDA();
				}
				checkSupplierDataMaintenance(); // #34097
			}
			// Enhancement #26186/#29793/#32860/#34097 start end - retrieve
			// disciplinary info
			
			log.info("Check Putra gotoAmendPage setAmendmentContractVer >END<");
			
		} finally {
			contractAmendmentSO = null;
			reqDTO = null;
			resDTO = null;
			agreementSO = null;
			ctAgreementResDTO = null;
			ctAgreementReqDTO = null;
		}
	}

	/**
	 * The method used to save the contract amendment.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this contract
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public String saveContractAmendment(String outcome) throws Exception {
		IContractAmendmentSO contractAmendmentSO = null;
		ContractReqDTO reqDTO = null;
		try {
			splittedAccNoAndBankname(); //Fix - #33674
			/*Validation for login user's org profile before initiate contract task*/
			if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)
					&& ctBackingBean.getManagedBean().getContractVer().getStatus() != null
					&& ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.FA_PENDING_SUBMISSION.longValue()) {
				validateOwnerOrgProfile(
						ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			}
			
			setSecretary();
			if(!ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isPid()){
				ctBackingBean.getManagedBean().getContractVer().setCtPidReasons(null);
			}
			
			// #25956 - Start
			if (!isCancelOrRejectProcess(outcome)) {
				validateContractAmendment(outcome);
			}
			// #25956 - End

			// Generate Supplier Acknowledgement Receipt
			if (getManagedBean().getTaskName() != null 
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
					&& outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {

				String filePath = getAcknowledgmentReceiptContent(true);
				CtAttachment attachment = new CtAttachment();
				attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
				attachment.setFileName("PengakuanPenerimaanSuratBerkaitanKontrakTambahan.pdf");
				attachment.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
				attachment.setCreatedUser(getFacesScopes().getUserSessionBean());
				attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				attachment.setSectionId(ContractConst.ATTACHMENT_LOA_LETTER_RECEIPT);
				getManagedBean().getCtLetterOfAcceptance().get(1).setLoaAttachments(attachment);
			}
			for(CtLetterOfAcceptance loaLetters : getManagedBean().getCtLetterOfAcceptance()){
				if(loaLetters.getLoaAttachments().getFilePath() != null ){
					ctBackingBean.getManagedBean().getContractVer().getLoaFileAttachments().add(loaLetters.getLoaAttachments());
				}
			}
			reqDTO = new ContractReqDTO();
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(outcome);
			reqDTO.setTaskId(getManagedBean().getTaskId());

			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());

			// Change Code #20997 Start
			//Bug #29323 - Start Remove Code
//			if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
//				ctBackingBean.getManagedBean().getContractVer().getCtContractItems().clear();
//				List<CtContractItem> ctItemList = new ArrayList<CtContractItem>();
//				List<CtContractItem> ctServiceList = new ArrayList<CtContractItem>();
//
//				if (ctBackingBean.getManagedBean().getCtItemList().size() > 0 && !ctBackingBean.getManagedBean().getCtItemList().isEmpty()) {
//				for (CtContractItem ctContractItem :  ctBackingBean.getManagedBean().getCtItemList()) {
//					
//						if(ctBackingBean.getManagedBean().getContractVer().isBrandNew()){
//							ctContractItem.setContractItemId(null);
//						for(CtItemPrice ctItemPrice2 : ctContractItem.getCtItemPrices()){
//							ctItemPrice2.setItemPriceId(null);
//						}									
//
//						for(CtItemMaxQty itemMaxQty2 : ctContractItem.getCtItemMaxQties()){
//							itemMaxQty2.setItemMaxQtyId(null);
//						}									
//
//						for(CtItemCeilingQty ceilingQty2 : ctContractItem.getCtItemCeilingQties()){
//							ceilingQty2.setItemCeilingQtyId(null);
//							}									
//					}
//				}
//				
//				if(ctBackingBean.getManagedBean().getContractVer().isBrandNew()){
//					ctItemList.clear();
//					ctItemList.addAll(ctBackingBean.getManagedBean().getCtItemList());
//				}else{
//					ctItemList.addAll(ctBackingBean.getManagedBean().getCtItemList());
//					}
//					ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctItemList);
//				}else{
//					ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctBackingBean.getManagedBean().getCtItemList());
//				}	
//			
//				if (ctBackingBean.getManagedBean().getCtItemServiceList().size() > 0 && !ctBackingBean.getManagedBean().getCtItemServiceList().isEmpty()) {
//				for (CtContractItem ctContractItem :  ctBackingBean.getManagedBean().getCtItemServiceList()) {
//					
//					if(ctBackingBean.getManagedBean().getContractVer().isBrandNew()){
//						ctContractItem.setContractItemId(null);
//					for(CtItemPrice ctItemPrice2 : ctContractItem.getCtItemPrices()){
//						ctItemPrice2.setItemPriceId(null);
//					}									
//
//					for(CtItemMaxQty itemMaxQty2 : ctContractItem.getCtItemMaxQties()){
//						itemMaxQty2.setItemMaxQtyId(null);
//					}									
//
//					for(CtItemCeilingQty ceilingQty2 : ctContractItem.getCtItemCeilingQties()){
//						ceilingQty2.setItemCeilingQtyId(null);
//						}									
//				}
//			}
//				if(ctBackingBean.getManagedBean().getContractVer().isBrandNew()){
//					ctServiceList.clear();
//					ctServiceList.addAll(ctBackingBean.getManagedBean().getCtItemServiceList());
//				}else{
//					ctServiceList.addAll(ctBackingBean.getManagedBean().getCtItemServiceList());
//					}
//					ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctServiceList);
//				}else{
//					ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctBackingBean.getManagedBean().getCtItemServiceList());
//				}
//				
//				// for Fixing Enhancement #20997 - Start
//				//#Bug #28339 - Failed to Update Max QTY & price
//				if(getManagedBean().getSelectedItem() != null){
//				if(getManagedBean().getSelectedItem().getCtItemPrices() != null){
//					for(CtItemPrice itemPrice : getManagedBean().getSelectedItem().getCtItemPrices() ){
//						itemPrice.setChangePrice(false);
//					}	
//				}
//
//				if(getManagedBean().getSelectedItem().getCtItemMaxQties() != null){
//					for(CtItemMaxQty itemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties() ){
//						itemMaxQty.setChangeQuantity(false);
//					}
//				}
//			}				
//		}
			//Bug #29323 - Start Remove Code
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
				ctBackingBean.getManagedBean().getContractVer().getCtContractItems().clear();
				//Bug #29323
				ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctBackingBean.getManagedBean().getCtItemList());
				ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(ctBackingBean.getManagedBean().getCtItemServiceList());

				// for Fixing Enhancement #20997 - Start
				//#Bug #28339 - Failed to Update Max QTY & price
				//#30879,#31065 remove Code for Max QTY & price -Start
//				if(getManagedBean().getSelectedItem() != null){
//				if(getManagedBean().getSelectedItem().getCtItemPrices() != null){
//					for(CtItemPrice itemPrice : getManagedBean().getSelectedItem().getCtItemPrices() ){
//						itemPrice.setChangePrice(false);
//					}	
//				}
//
//				if(getManagedBean().getSelectedItem().getCtItemMaxQties() != null){
//					for(CtItemMaxQty itemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties() ){
//						itemMaxQty.setChangeQuantity(false);
//					}
//				}
//			}
				//#30879,#31065 remove Code for Max QTY & price -Start	
		}
		// for Fixing Enhancement #20997 and #Bug #28339 - End	
		log.info("Check Putra CtContractItems :"+ctBackingBean.getManagedBean().getContractVer().getCtContractItems().size());

			// Change Code #20997 End
			
			ctBackingBean.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() == null) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(
						ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
			}

			if(ctBackingBean.getManagedBean().getContractVer().getCtSchedules() != null){
				for(CtSchedule schedules : ctBackingBean.getManagedBean().getContractVer().getCtSchedules()){
					PmParameter parameter =  new PmParameter();
					parameter.setParameterId(schedules.getFulfilmentTypeId());
					schedules.setScheduleType(parameter);
				}
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setSupplierName(ctBackingBean.getManagedBean().getSupplierCompanyName());
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setSupplierBranch(ctBackingBean.getManagedBean().getSupplierBranchName());
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setSupplierRegNo(ctBackingBean.getManagedBean().getSupplierRegNo());
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setSupplierMofNo(ctBackingBean.getManagedBean().getSupplierMofNo());
				if (ctBackingBean.getManagedBean().getCtAddress() != null) {
					ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setSupplierAddress(ctBackingBean.getManagedBean().getCtAddress());
				}
			}

			if (getManagedBean().isPrepareLOA()) {
				int contractDuration = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() 
						+ ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getContractPeriod();
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractDuration(new BigDecimal(contractDuration));

				getSupplierListForLoaAck();
				reqDTO.setSupplierList(getManagedBean().getSupplierList());
			}

			// Retrieve the proper committee role during sign off process and set the sign off flag to true
			if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
					&& outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
				String role = null;
				for(CtCommitteeMember committeeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
					if (committeeMember.getPmUser().getUserId().equals(getFacesScopes().getUserSessionBean().getUserId())) {
						committeeMember.setSignedOff(true);
						if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
								|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) { 
							role = "FPB_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
						} else {
							role = getManagedBean().getCommitteeType() + "_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
						}
						reqDTO.setUserRole(role);
						break;
					}
				}	     		
			} else if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
					&& outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) { //Start Fix - #38210
				String role;
				for(CtCommitteeMember committeeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
					committeeMember.setSignedOff(false);
					if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
							|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) { 
						role = "FPB_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
					} else {
						role = getManagedBean().getCommitteeType() + "_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
					}
					reqDTO.setUserRole(role);
				} //End Fix - #38210
			} else {
				reqDTO.setUserRole(getUserRole());
			}
			reqDTO.setTaskName(getManagedBean().getTaskName());
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				ctBackingBean.scheduleProgressiveDate();
			}
			//#25956 - Start
			getContractAdminlist();
			//#25956 - End

			//26753 - start
			if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId()) 
					|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setCtCommitteeMembers(getManagedBean().getUpdatedCommitteeMembers());
			}
			//26753 - end

			reqDTO.setCtContract(ctBackingBean.getManagedBean().getContractVer().getCtContract());
			reqDTO.setCtContractVer(ctBackingBean.getManagedBean().getContractVer());
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setDeleteAttachments(ctBackingBean.getManagedBean().getDeleteAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setNewAgencyMap(ctBackingBean.getManagedBean().getNewAgencyMap());
			reqDTO.setUpdateAgencyMap(ctBackingBean.getManagedBean().getUpdateAgencyMap());
			reqDTO.setDeleteAgencyMap(ctBackingBean.getManagedBean().getDeleteAgencyMap());
			reqDTO.setUpdateZoneMap(ctBackingBean.getManagedBean().getUpdateZoneMap());
			reqDTO.setUpdateItemMap(ctBackingBean.getManagedBean().getUpdateItemMap());
			reqDTO.setContractAdminlist(getManagedBean().getContractAdminlist());
			
			//26186/#32860/#34097 start
			this.getManagedBean().setValidationDA(false);
			if(checkValidationDA()){
				this.getManagedBean().setValidationDA(true);
				reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
			}
			if ((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) || 
					outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT))
					&& getManagedBean().isOldBPMVersion()
					&& getManagedBean().isWipButtonCancelReject()) {
				reqDTO.setCancelOldProcess(true);
			}
			reqDTO.setCancelType(getContractCancelType());
			//26186/#32860/#34097 end

			if (getManagedBean().getPmDigiSign() != null) {
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			// Start Fix - #30022
			contractadminstatuschecking();
			// End Fix - #30022
			ContractResDTO resDTO = contractAmendmentSO.saveContractAmendmentVerDtls(reqDTO);
			if (resDTO.isSuccessful()) {
				log.debug("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				String[] params = new String[] { getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
						this.ctBackingBean.getManagedBean().getContractVer().getDocType()),
						this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()};
				//Start Fix - #33287
				if(spToCheck){
					amendmentRevert(ctBackingBean.getManagedBean().getContractVer().getContractVerId());
				}
				//End Fix - #33287
				// Start Fix - #33256, #33287, #34110
				if(ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
					if (getManagedBean().getSelectedChangeItemQuantity() != null
							&& getManagedBean().getSelectedChangeItemQuantity().size() > 0) {
						for (CtItemMaxQty itemMaxQty : getManagedBean().getSelectedChangeItemQuantity().values()) {
							if (itemMaxQty.getItemMaxQtyId() != null && !itemMaxQty.isBrandNew() && itemMaxQty.getPrevId() != null) {
								contractAmendmentSO.updateExpDateItemMaxQty(itemMaxQty.getExpDate(),
										itemMaxQty.getItemMaxQtyId());
							}else if(itemMaxQty.isChangeQuantity()){
								contractAmendmentSO.updateExpDateAndEffDateItemMaxQty(itemMaxQty.getMaxQuantity(), itemMaxQty.getExpDate(),
										itemMaxQty.getEffDate(), itemMaxQty.getItemMaxQtyId());
							}
						}
						getManagedBean().getSelectedChangeItemQuantity().clear();
					}
				
					if (getManagedBean().getSelectedChangeItemPrice() != null
							&& getManagedBean().getSelectedChangeItemPrice().size() > 0) {
						for (CtItemPrice ctItemPrice : getManagedBean().getSelectedChangeItemPrice().values()) {
							if (ctItemPrice.getItemPriceId() != null && !ctItemPrice.isBrandNew() && ctItemPrice.getPrevId() != null) {
								contractAmendmentSO.updateExpDateItemPrice(ctItemPrice.getExpDate(),
										ctItemPrice.getItemPriceId());
							}else if(ctItemPrice.isChangePrice()) {
								contractAmendmentSO.updateExpDateAndEffDateItemPrice(ctItemPrice.getUnitPrice(), ctItemPrice.getExpDate(),
										ctItemPrice.getEffDate(), ctItemPrice.getItemPriceId());
							}
						}
						getManagedBean().getSelectedChangeItemPrice().clear();
					}
				}
				// End Fix - #33256, #33287, #34110
  
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					
					//#26688 start
					if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME) || 
							getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)){
						messageId = MessageConst.INFO_700100;
						params = new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()};
					}else{
						messageId = MessageConst.INFO_700009;
					}
					//#26688 end
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
						messageId = MessageConst.INFO_700061;
					} else {
						if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()) {
							messageId = MessageConst.INFO_700051;
						} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofAuthorityReq()) {
							messageId = MessageConst.INFO_700007;
						} else {
							messageId = MessageConst.INFO_700007;
						}
					}
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					// #25956 - Start
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
						messageId = MessageConst.INFO_700082;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();

					} else {
						messageId = MessageConst.INFO_700118;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();
					}
					// #25956 - Start
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
						messageId = MessageConst.INFO_700001;
					} else if (getManagedBean().getTaskName().equalsIgnoreCase(
							CTProcessNameConst.ACK_FA_REJECT_LOA_HT_NAME)) {
						messageId = MessageConst.INFO_700120;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();
					} else if (getManagedBean().getTaskName().equalsIgnoreCase(
							CTProcessNameConst.ACK_FA_CANCEL_MEETING_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(
									CTProcessNameConst.ACK_FA_CANCEL_MEETING_DECISION_HT_NAME)) {
						messageId = MessageConst.INFO_700121;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();
					} else {
						messageId = MessageConst.INFO_700011;
					}
					// #25956 - End
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					if (getManagedBean().getTaskName() == null
							|| (getManagedBean().getTaskName() != null
							&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
									|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME)))) {
						messageId = MessageConst.INFO_700021;
						if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
								ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							params = new String[] { getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
									this.ctBackingBean.getManagedBean().getContractVer().getDocType()),
									this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
									RoleHelper.getInstance().getRoleDescByCode(RoleCode.CT_APPROVER.value()).getRoleName()};
						} else { // Central Contract
							params = new String[] { getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
									this.ctBackingBean.getManagedBean().getContractVer().getDocType()),
									this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
									RoleHelper.getInstance().getRoleDescByCode(RoleCode.BPK_CC_APPROVER.value()).getRoleName()};
						}
					}

					if (getManagedBean().getTaskName() != null) {
						if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)) {
							messageId = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isApprovalReq()
									? MessageConst.INFO_700046 : MessageConst.INFO_700043;

						} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)) {
							messageId = MessageConst.INFO_758002;
							params = new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()};
						} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
							messageId = MessageConst.INFO_758003;
							params = new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()};
						} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME)) {
							messageId = MessageConst.INFO_758004;
							params = new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()};
						}
					}
					// #25956 - Start
				} else if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(
									CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
						messageId = MessageConst.INFO_700119;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();

					} else {
						messageId = MessageConst.INFO_700083;
						params[0] = this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
								.getContractNo();
					}
					// #25956 - End
				}
				
				//#26186, #34097 start
				if (this.getManagedBean().isDisciplinaryActionCancel()
						|| this.getManagedBean().isSupplierBumiCancel()) {
					messageId = handleAcknowledgeMessageDAandBumiCancel(outcome);
					params = new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract()
							.getContractNo() };

				}
				//#26186, #34097 end

				//SIR#14189: Replace the doc type in acknowledge message for Supplemental Contract Letter process
				if (getManagedBean().getTaskName() != null && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)//#25956
						&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
								|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)
								|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
								|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME))) {
					params[0] = ResourceBundleUtils.getMessage("lbl.ct.amend.loa.sst", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				}
				successMessage(messageId, params);
				
				//#32860 start
				if ((!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
						|| (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null))
						&& (!getManagedBean().isValidationDA())) {
					//#32860 end
					//#26186 start
					if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)){
						RequestContext.getCurrentInstance().execute("sendParamToSupplierDA();");
					}
					//#26186 end
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					ContractReqDTO ctReqDTO = new ContractReqDTO();
					ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
					setAmendmentContractVer(ctReqDTO);
					ConstructTabSideMenu();
					//26753 - start
					if(getManagedBean().getFilteredUpdatedCommitteeMembers() != null){
						getManagedBean().getFilteredUpdatedCommitteeMembers().clear();						
					}
					//26753 - end
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			contractAmendmentSO = null;
		}
		return null;		
	}

	//24333 - Start, Refactor code for Validate Amendment because tab menu changes
	// And remove unused block comment code
	/**
	 * The method used for validating the contract amendment information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  action	the user action on this contract. The validation 
	 * 					will invoke base on the action
	 */
	public void validateContractAmendment(String action) throws EpException {
		getManagedBean().setAutoUpdate(true);	
		// Validation for remark
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
			String itemRemark = ctBackingBean.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
			String supplierRemark = ctBackingBean.getManagedBean().getContractVer().getSupplierRemarks().get(0).getRemark();

			if ((remark == null || remark.length() == 0) && (itemRemark == null || itemRemark.length() == 0) 
					&& (supplierRemark == null || supplierRemark.length() == 0)) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.REMARK_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}/*Remark validation end*/
		

		/*validation for other  selection*/
		// #25956 remove outcome cancel
		if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {

			// Validation for delivery term 
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) &&
					!ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
				if(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getDeliveryTerm() <= 0){

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.FULFILMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100062);
				}
			}
			//Validation for PID
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && 
					ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isPid() && 
					(ctBackingBean.getManagedBean().getContractVer().getCtPidReasons() == null || ctBackingBean.getManagedBean().getContractVer().getCtPidReasons().size() == 0)) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.FULFILMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.PID_REASON_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Validation for PID Reason
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
				if(ctBackingBean.getManagedBean().getContractVer().getCtPidReasons() != null 
						&& ctBackingBean.getManagedBean().getContractVer().getCtPidReasons().size() > 0 ){
					for(CtPidReason reason : ctBackingBean.getManagedBean().getContractVer().getCtPidReasons()){
						if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.FULFILMENT_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage(ContractConst.PID_REASON_DESC_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						ctBackingBean.validatePidReason(reason.getPmReason().getReasonId());
						if(ctBackingBean.PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
							if(reason.getReasonDesc().isEmpty()){

								getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.FULFILMENT_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
								
								throw new EpException(MessageConst.ERROR_100022, 
										new String[]{ResourceBundleUtils.getMessage(ContractConst.PID_REASON_DESC_MENU_LABEL, 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}
					}
				}
			}
			// Validation for GST amount before GST Relief Due Date
			if (ctBackingBean.isBeforeGstReliefDueDate()
					&& ctBackingBean.imposedServiceTaxCondition()
					&& ctBackingBean.isLoaContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())
					&& ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount() == null
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount().doubleValue() == 0) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.NEW_GST_AMT_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewStaxAmount() == null
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewStaxAmount()
								.doubleValue() == 0) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
							ContractConst.NEW_SST_AMT_MENU_LABEL, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
				}
			}

			// Validation for zone
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				ctBackingBean.getZoneObjDetails();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractZones() != null) {
					for (CtContractZone contractZone : ctBackingBean.getManagedBean().getContractVer().getCtContractZones()) {
						if (contractZone.getDeliveryTerm() <= 0) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.ZONE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
						}
					}
				}
			}		
			// Validation for agency
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY) 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AGENCY_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
			}

			// Validation for AP178 agency
			if (ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size()>0) {
				double ceilingAmt = 0.0;
				DecimalFormat df = new DecimalFormat("#.00");
				for (CtAgency ctAgency : ctBackingBean.getManagedBean().getContractVer().getCtAgencies()) {
					ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
				}
				ceilingAmt = Double.valueOf(df.format(ceilingAmt));

				double overallContractAmount = 0.0;
				for (CtContractAmount contractAmt : ctBackingBean.getManagedBean().getContractVer().getCtContractAmounts()){
					if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
							&& ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
						overallContractAmount = contractAmt.getContractAmount().doubleValue();
						if (contractAmt.getGstAmount() != null) {
							overallContractAmount += contractAmt.getGstAmount().doubleValue();
						}
						if (contractAmt.getStaxAmount() != null ) {
							overallContractAmount  += contractAmt.getStaxAmount().doubleValue();
						}
						if (contractAmt.getGstAmount() != null && contractAmt.getStaxAmount() != null) {
							overallContractAmount += contractAmt.getGstAmount().doubleValue() + contractAmt.getStaxAmount().doubleValue();
						}
						overallContractAmount = Double.valueOf(df.format(overallContractAmount));
						break;
					}
				}
				if (ceilingAmt >  overallContractAmount) {
					CommonUtils commonUtils = new CommonUtils();
					DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
							CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AGENCY_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_750007, 
							new String[] {nf.format(overallContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
				}

				if(!ctBackingBean.getManagedBean().isOwnerOrgProfile()){
					if(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getAp178RefNo() == null
							|| ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getAp178RefNo().isEmpty()){

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AGENCY_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_750045);
					}
					//#25703 start
					else if(ctBackingBean.getManagedBean().getContractVer().getAgencyContractSharingAttachements() == null ||
							ctBackingBean.getManagedBean().getContractVer().getAgencyContractSharingAttachements().isEmpty()){

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AGENCY_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_750046);
					}
					//#25703 end
				}
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems() != null) {
				for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
					// Validation for minimum order quantity
					//#28479 - Start Putra
					if (item.getMinimumOrderQty() <= 0) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.ITEM_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100033,
								new String[]{ResourceBundleUtils.getMessage(ContractConst.MIN_QTY_MENU_LABEL,
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					if (!item.getCtItemMaxQties().isEmpty()) {
						Double maxValue = new Double(0);							
						for (CtItemMaxQty maxQty : item.getCtItemMaxQties()) {
							if(maxQty.getMaxQuantity().doubleValue() > maxValue)
								maxValue = maxQty.getMaxQuantity().doubleValue();	

						}
						log.info(item.getMinimumOrderQty() > maxValue);
						if (item.getMinimumOrderQty() > maxValue) {
							throw new EpException(MessageConst.ERROR_100034,
									new String[]{ResourceBundleUtils.getMessage(ContractConst.MIN_QTY_MENU_LABEL,
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});					
						}
						//#28479 - End Putra
					}
					// Validation for ceiling quantity
					double totalCeilingQuantity = 0.0;
					if (item.getCtItemMaxQties().size() != 0) {						
						for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
							totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
						}
						if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.ITEM_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
						}
					}
				}
			}

			// Validation for Deduction
			if(ctBackingBean.getManagedBean().getContractVer().getCtDeductions() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0){
				for(CtDeduction deduction : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()){
					if(deduction.getDeductionType() == null ||deduction.getDeductionType().getParameterId() == null ){

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_TYPE_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					ctBackingBean.validateDeductionType(deduction);
					if(deduction.getCtDeductionClauses() != null && deduction.getCtDeductionClauses().size() > 0){
						for(CtDeductionClause ctDeductionClauses : deduction.getCtDeductionClauses()){
							if(ctDeductionClauses.getClauseDescription().isEmpty()){

								getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
								
								throw new EpException(MessageConst.ERROR_100022, 
										new String[]{ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_CLAUSE_DESC_MENU_LABEL, 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}
					}else{
						/*mandatory to fill deduction clause*/

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.DEDUCTION_CLAUSE_DESC_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}  
			}

			// Validation for schedule
			if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					ctBackingBean.getManagedBean().getContractVer().setCtProgressivePayments(null);
				} else {
					ctBackingBean.getManagedBean().getContractVer().setCtSchedules(null);  	
				}
				List<String> schedulePhaseNoList = new ArrayList<String>();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					if (ctBackingBean.getManagedBean().getContractVer().getCtSchedules() == null || ctBackingBean.getManagedBean().getContractVer().getCtSchedules().size() < 2 
							&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_750022);
					}
					for (CtSchedule schedules : ctBackingBean.getManagedBean().getContractVer().getCtSchedules()) {
						if (schedules.getScheduleNo() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_NO_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100046, 
									new String[] {ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_NO_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(schedules.getScheduleNo().toString());
						}
						if (schedules.getStartMonth() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.FREQUENCY_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.START_PERIOD_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedules.getEndMonth() == null) {
							
							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.FREQUENCY_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
											+ " " + ResourceBundleUtils.getMessage(ContractConst.END_PERIOD_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedules.getFulfilmentTypeId() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_TYPE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
					}     	
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() == null || ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().size() < 2 
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_750022);
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() != null && !ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty() 
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					BigDecimal percentage = BigDecimal.ZERO;
					for (CtProgressivePayment progressivePayment : ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments()) {
						if (progressivePayment.getPhaseNo()== null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PROGRESSIVE_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.PHASE_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100046, 
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PHASE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
						}
						if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PROGRESSIVE_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.DESCRIPTION_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getStartMonth() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PROGRESSIVE_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.START_PERIOD_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getEndMonth() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PROGRESSIVE_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.END_PERIOD_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}     	
						if(progressivePayment.getPercentage()!=null) {
							percentage = percentage.add(progressivePayment.getPercentage());
						} else {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage(ContractConst.PROGRESSIVE_SCHEDULE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage(ContractConst.PERCENTAGE_MENU_LABEL, 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});							
						}						
					}
					int value = percentage.compareTo(new BigDecimal(100));
					if (value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
							|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.SCHEDULE_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[] {ResourceBundleUtils.getMessage(ContractConst.PERCENTAGE_PROGRESSIVE_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
		}
		if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)  && !action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			// Validation for amendment type
			if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange() 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst() 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()
					&&  !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_TYPE_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Validation for reference no
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo() == null 
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo() == "") {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100061, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.FILE_REF_NO_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Validation for amendment title
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle() == null
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle() == "") {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_TITLE_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Validation for amendment description
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getDescription() == null
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getDescription() == "") {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_DESC_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Start Fix - #33952, #25956			
			if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() && ctBackingBean
					.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() == 0)
					&& ctBackingBean.getManagedBean().isContractExpired()){
				
				if ((ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))
						|| (ContractStatusConst.FA_PENDING_MEETING_CONFIRMATION_REVISION.equals(ctBackingBean
								.getManagedBean().getContractVer().getStatus().getStatusId()))
						|| (ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))
						|| (ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean																				   
								.getManagedBean().getContractVer().getStatus().getStatusId())																	   
						|| (ContractStatusConst.FA_PENDING_LOA_SUBMISSION.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId())
						|| (ContractStatusConst.FA_PENDING_LOA_REVISION.equals(ctBackingBean.getManagedBean()																							
								.getContractVer().getStatus().getStatusId())
						|| (ContractStatusConst.FA_PENDING_MOF_MEETING_DECISION.equals(ctBackingBean.getManagedBean()																						
								.getContractVer().getStatus().getStatusId())
						|| (ContractStatusConst.FA_PENDING_VERIFICATION.equals(ctBackingBean.getManagedBean()																					
								.getContractVer().getStatus().getStatusId()))
								))))){

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
							throw new EpException(MessageConst.ERROR_750076, new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()});
			
				}else if ((ContractStatusConst.FA_PENDING_LOA_APPROVAL.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))
						|| (ContractStatusConst.FA_PENDING_LOA_REVISION_APPROVAL.equals(ctBackingBean
								.getManagedBean().getContractVer().getStatus().getStatusId()))){

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
							throw new EpException(MessageConst.ERROR_750077, new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()});
				}else if ((ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))){

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_DETAILS_MENU_LABEL, //25956
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							throw new EpException(MessageConst.ERROR_750078, new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()});
				}else if ((ContractStatusConst.FA_PENDING_SIGN_OFF.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))
						|| (ContractStatusConst.FA_PENDING_CHAIRPERSON_SIGN_OFF.equals(ctBackingBean.getManagedBean()
								.getContractVer().getStatus().getStatusId()))){

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
							throw new EpException(MessageConst.ERROR_750079, new String[] { this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()});
				} else {
			// Validation for amendment extension period for expired contract
			//28848
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() == 0){

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			   throw new EpException(MessageConst.ERROR_750048);
			  }
			}
		  }
			// End Fix - #33952
			
			//Start Fix - #38613, #33287 - *apply for both contract expired and non-expired
			// Validation for amendment when extension period - 0 
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() == 0){

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			   throw new EpException(MessageConst.ERROR_750048);
			  }
			//End Fix - #38613, #33287

			// Validation for amendment effective date
			if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() != null
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate().getTime() > ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime()) {

				throw new EpException(MessageConst.ERROR_750035);
			}
			/* Validation mandatory for approving authority */
			if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofAuthorityReq() 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isApproverAuthorityReq()) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				throw new EpException(MessageConst.ERROR_750038);
			}
			// Validation mandatory for secretary if pbqc authority is required
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()) {
				//Validation for committee
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.SECRETARY_COMMITTEE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			// Validation mandatory for approval letter if approver authority is required
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isApproverAuthorityReq() && (!isContractAmountAmend() && 
					!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange())) {
				//Validation for approval letter
				if (ctBackingBean.getManagedBean().getContractVer().getApprovalLetterAttachement() == null
						|| ctBackingBean.getManagedBean().getContractVer().getApprovalLetterAttachement().getFilePath() == null) {						

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.AMENDMENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.APPROVAL_LETTER_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		}

		if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME))){
				//Start Fix - #36708
				// Validation for SCL File Reference No
				if (StringUtils.isEmpty(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSclRefNo())){

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_DETAILS_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.LOA_PHYSICAL_NO_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				//End Fix - #36708
				// Validation for contract signer
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner() == null 
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner().getUserId() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_DETAILS_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.LOA_SIGNER_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaSignerDesig() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_DETAILS_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.SIGNER_DESIGNATION_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				// Validation for Supplemental Contract Letter Date
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getGovtSignDate() ==  null){

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_CONTENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.SCL_DATE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				// Validation for Respond Period
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaResponsePeriod() ==  null){

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_CONTENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.SCL_RESPONSE_PERIOD_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				// Validation for Supplemental Contract Letter
				if (getManagedBean().getCtLetterOfAcceptance().get(0).getLoaAttachments() != null
						&& getManagedBean().getCtLetterOfAcceptance().get(0).getLoaAttachments().getFilePath() == null ) {						

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_CONTENT_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(ResourceBundleUtils.getMessage(ContractConst.LOA_CONTENT_FILE_NAME_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				}
			} 
		}

		if (getManagedBean().getTaskName() != null 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			// Validation for bank account number
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() == null) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.LOA_DETAILS_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage(ContractConst.BANK_ACCOUNT_NAME_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			// Validation for term and condition
			if (!getManagedBean().isTermAndConditionFlg()) {

				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECLARATION_MENU_LABEL, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750002);
			}		
		}
		if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			// Check all the member role must be selected
			if (getManagedBean().getTaskName() != null 
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
				validateCommiteeMemberDetails();
			}
			// Validation for meeting and committee
			if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
							|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME))) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().size() == 0) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100023, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL,
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				} else {
					int counter = 0;
					for (CtMeeting ctMeeting : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings()) {
						if (ctMeeting.getMeetingDate() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage(ContractConst.MEETING_DATE_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} 
						if (ctMeeting.getMeetingLocation() == null || ctMeeting.getMeetingLocation().isEmpty()) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage(ContractConst.LOCATION_MEETING_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} 
						if (ctMeeting.getMeetingStatusId() == null) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage(ContractConst.MEETING_STATUS_MENU_LABEL, 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
							if (ctMeeting.getMeetingStatusId().equals(ContractStatusConst.FA_MEETING_PENDING)) {

								getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
								
								throw new EpException(MessageConst.ERROR_750027);
							}
						}
						if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
							if (ctMeeting.getMeetingStatusId().equals(ContractStatusConst.FA_MEETING_APPROVE)) {
								counter++;
							}
						}
					}
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
						if (counter == 0) {

							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_750028);
						}
					}
				}

				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
						&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
					int chairPersonCount = 0;
					Long chairPersonId = getParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON).getParameterId();
					for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
						if (committeMember.getMemberRoleId().longValue() == chairPersonId.longValue()) {
							chairPersonCount++;
							if (chairPersonCount > 1) {

								getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.MEETING_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
								
								throw new EpException(MessageConst.ERROR_750026);
							}
						}
					}
				}
				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
					//Enhancement #26186 - start
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbqcDecisionDate() == null) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.PBM_DECISION_DATE_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					//Enhancement #26186 - end
					
					if (getManagedBean().getPbQcApproved() == null || getManagedBean().getPbQcApproved().equalsIgnoreCase("")) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbqcJustification() == null
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbqcJustification().equalsIgnoreCase("")) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.REMARK_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					if (ctBackingBean.getManagedBean().getContractVer().getPbqcDecisionLetterAttachment() == null
							|| ctBackingBean.getManagedBean().getContractVer().getPbqcDecisionLetterAttachment().getFilePath() == null) {

						getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage(ContractConst.DECISION_LETTER_MENU_LABEL, 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			if (getManagedBean().getTaskName() != null
					&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME)) {
				//Enhancement #26186 - start
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofDecisionDate() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.MOF_DECISION_DATE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				//Enhancement #26186 - end
				
				if (getManagedBean().getMofApproved() == null || getManagedBean().getMofApproved().equalsIgnoreCase("")) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofJustification() == null
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofJustification().equalsIgnoreCase("")) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.REMARK_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getMofMeetingDate() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.APPROVAL_LETTER_DATE_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (ctBackingBean.getManagedBean().getContractVer().getMofDecisionLetterAttachment() == null
						|| ctBackingBean.getManagedBean().getContractVer().getMofDecisionLetterAttachment().getFilePath() == null) {

					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.DECISION_MENU_LABEL, 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage(ContractConst.APPROVAL_LETTER_MENU_LABEL, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		}
		// Validation for committee member
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && 
				(getManagedBean().getTaskName() != null &&
				(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)
						|| getManagedBean().isSignOffDecision()))
						&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null
						&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() > 0) {
			validateMeetingDetails();
		}
		// Validation for approver
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting() 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {

			getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage(ContractConst.APPROVER_SETTING_MENU_LABEL, 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		// Validation for valid supplier for acknowledge loa task assignment
		if (getManagedBean().isPrepareLOA() 
				&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
			getSupplierListForLoaAck();
			if (getManagedBean().getSupplierList() == null 
					|| getManagedBean().getSupplierList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_750039);
			}
		}

		if ((action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) 
				|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))
				|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)
						|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)))) {
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
			sb.append(ContractConst.SEPRATE_LINE);
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()){
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
			}else {
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			}
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(newOverallContractAmount());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(action);		
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(action);
		} 
		
		//#25890 - Start
		/*Validation for deduction if Amount <=0*/
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)
			&&	(ctBackingBean.getManagedBean().getContractVer().getCtDeductions() != null 
				|| ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0)){
			for(CtDeduction deduction : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()){
					Long parameterId = deduction.getDeductionType().getParameterId();
					PmParameter pmParameter = retrieveParameterById(parameterId);
					if (ParameterConst.DEDUCTION_TYPE_FIXED.equalsIgnoreCase(pmParameter.getParameterCode())
							&& deduction.getAmount() <= 0) {
						throw new EpException(MessageConst.ERROR_740059);
					}
			}
		}
		//#25890 - End
	}
	//24333 - End, Refactor code for Validate Amendment because tab menu changes

	/**
	 * The method used to authenticate user during submit the contract amendment.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save contract amendment method once authentication success.
	 * 
	 * @param  outcome  the user action on this contract
	 * @return result of the saveContractAmendment process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	public String gpkiSignAction(String outcome) throws Exception {
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) && !isSupplierRole()) {
			
			setSecretary();
			validateContractAmendment(outcome);
			// Temp setTextBeforeSign : DocNo+Type
			StringBuilder sb = new StringBuilder();
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
			sb.append(ContractConst.SEPRATE_LINE);
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()){
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
			}else {
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			}
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(newOverallContractAmount());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);		
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());	
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_ACKNOWLEDGE.equals(outcome)
				&& isSupplierRole()
				&& !getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
//			preRejectContractAmendment(outcome); //Removing
			validateContractAmendment(outcome);	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			if(hasGovtSellerRole()) {
				getManagedBean().setTextBeforeSign(prepareSupplierOriginalText());
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} else {
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			}
			return null;
		}
		else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
//			validateContractAmendment(outcome);
			validateRemarkCancel(outcome);
			if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)){
				RequestContext.getCurrentInstance().execute("cancelofficerConfirmDlg.show()");				
			}else{
				RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");	
			}			
			return null;
		} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
//			validateContractAmendment(outcome);
			validateRemarkCancel(outcome);
			if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)){
				RequestContext.getCurrentInstance().execute("supplierrejectConfirmDlg.show()");
			
			}else {
				RequestContext.getCurrentInstance().execute("rejectConfirmDlg.show()");				
			}
			return null;
		}
		
		//#26688 start
		else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
			validateRemarkCancel(outcome);
			if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)){
				RequestContext.getCurrentInstance().execute("reviseConfirmDlg.show()");
			}
			else{
				return saveContractAmendment(outcome);
			}
			return null;
		}
		//#26688 end
		
		else {
			return saveContractAmendment(outcome);
		}
	}

	/**
	 * The method used to set value for security check
	 * before save the contract amendment.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveContractAmendment process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	public String preSubmitContractAmendment(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null && isSupplierRole() && !hasGovtSellerRole()) {
			pmDigiSign.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
			pmDigiSign.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			pmDigiSign.setOriginalData(prepareSupplierOriginalText());
			pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(pmDigiSign.getOriginalData()));
			getManagedBean().setPmDigiSign(pmDigiSign);			
		} else if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		return saveContractAmendment(getManagedBean().getButtonAction());
	}

	/**
	 * The method used to retrieve the supplier information for security check.
	 * 
	 * @return orginalText the supplier information
	 */
	private String prepareSupplierOriginalText() {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode ="";
		String orginalText = "";

		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier() != null) {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size() > 0) {
				List<SmCompanyAddress> companyAddress = ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();

				for (SmCompanyAddress compaAddress : companyAddress) {
					if (compaAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) {
						countryCode = compaAddress.getSmAddress().getPmCountry().getCountryCode();
						break;
					}
				}
			}
		}		
		sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo())
		.append("|")
		.append(facesScopes.getUserSessionBean().getIdentificationNo())
		.append("|")
		.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getRegNo())
		.append("|")
		.append(countryCode);
		orginalText  = sb.toString();
		return orginalText;
	}

	/**
	 * The method used to construct 
	 * the tab menu for contract amendment.
	 */
	public void ConstructTabSideMenu() {
		AmendTabSideMenus sideMenu = new AmendTabSideMenus();
		getManagedBean().setFinalListOfSideMenu(new ArrayList<String>());
		getManagedBean().setConfirmMeeting(false);
		getManagedBean().setIndexing(new LinkedHashMap<String, Integer>()); //#35132
		
		int indexing = -1;//to start the indexing at 0;

		if (getManagedBean().getAckFA() == null) {
			getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendFulfilment);

			if (ctBackingBean.getManagedBean().getContractVer() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment() != null
					&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {

				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithZone);

				} if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
						&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAgency);
				}
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithItem);
			}

			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuDeduction);
			}
			//#25002 - add new rules for schedule
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange() && 
					(ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
							|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED))
					&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuSchedule);
			}
			//Enhancement 15851 - shaqimi start
			if (getManagedBean().isPrepareLOA() || ctBackingBean.getManagedBean().isViewSCL()) {
			//Enhancement 15851 - shaqimi end
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareLoa);					 
				if (((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq()
						|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()) //24333
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
						&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
								ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))
								|| ctBackingBean.isPk7FromDp())) { //24333
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareBond);
				}
				if (getManagedBean().isDecisionPbQcLOA() && !ctBackingBean.getManagedBean().isViewSCL()) { //#15851
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);	    
				}
			}
			if (getManagedBean().isPbConfirmMeeting()) {
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithMeetings);
			}			
			//24333 - Start
			if (!getManagedBean().isPrepareLOA() && ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
					&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
							ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)
							|| ctBackingBean.isPk7FromDp())){ //24333
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithBond);
			}
			//24333 - End
			if (getManagedBean().isSignUpdateDecision()) {
				getManagedBean().setConfirmMeeting(true);
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			}
			if (getManagedBean().isSignOffDecision()) {
				getManagedBean().setConfirmMeeting(true);
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			}
			if (isApproverSetting() && !getManagedBean().isPrepareLOA()) {
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuApprover); //#35132
			}

		} else if (getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)) { //24333
			getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendSupplier);
			if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq() && !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())
					&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
							ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)
							|| ctBackingBean.isPk7FromDp())) { //24333
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareBond);
			}
			getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDeclaration);
		}
		
		for(String sidemenu : getManagedBean().getFinalListOfSideMenu()){  //#35132
			getManagedBean().getIndexing().put(sidemenu, ++indexing);
		}
		log.info("Check getFinalListOfSideMenu Size"+getManagedBean().getFinalListOfSideMenu().size());
		log.info("Check getFinalListOfSideMenu "+getManagedBean().getFinalListOfSideMenu().toString());
	}
	
	public void setMenuByIndex(int outputValue) { //#35132
		getManagedBean().setSelectedTabIndex(outputValue);
	}

	/**
	 * The method used for controlling the tab menu
	 * of contract amendment dynamically.
	 * 
	 * @param  outputTab   current selected tab constant
	 */

	/**
	//#24333 - Start ReSetting on whole SideTabMenu this method
	public void setTabValue(String outputValue) {
		getManagedBean().setConfirmMeeting(false);
		if (getManagedBean().getAckFA() == null) {
			int index = 1;
			if (outputValue.equalsIgnoreCase("FD")) {
				getManagedBean().setSelectedTabIndex(0);
			}
			if (outputValue.equalsIgnoreCase("AD")) {
				getManagedBean().setSelectedTabIndex(1);
			}
			
			// 24333
			if (outputValue.equalsIgnoreCase("PB")) {
				if((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())
						&& (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())){
					getManagedBean().setSelectedTabIndex(index + 1);	
				}
			}

			if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange() || 
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){

				if(ctBackingBean.getManagedBean().getContractVer() != null && ctBackingBean.getManagedBean().getContractVer().getCtAmendment() != null
						&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() || ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {
					index = 2;
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
						index = 3;
						if (outputValue.equalsIgnoreCase("ZO")) {
							getManagedBean().setSelectedTabIndex(2);
						}
						if (outputValue.equalsIgnoreCase("IT")) {
							getManagedBean().setSelectedTabIndex(3);
	                        searchReset(); // Fix - #31042
						}
						if(outputValue.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)){
							getManagedBean().setSelectedTabIndex(4);
						}
						if(outputValue.equalsIgnoreCase("PB")) {// 24333
							if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
							&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
								getManagedBean().setSelectedTabIndex(5);
							}else{
								getManagedBean().setSelectedTabIndex(4);
							}
						}
					} else if(outputValue.equalsIgnoreCase("IT")) {
						getManagedBean().setSelectedTabIndex(2);
						searchReset(); // Fix - #31042
					}else if(outputValue.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)){
						getManagedBean().setSelectedTabIndex(3);
					}else if(outputValue.equalsIgnoreCase("PB")) {// 24333
						if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
						&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
				getManagedBean().setSelectedTabIndex(4);
			}else{
				getManagedBean().setSelectedTabIndex(3);
			}
		}
					
					
					if(isApproverSetting() && !getManagedBean().isPrepareLOA() && outputValue.equalsIgnoreCase("SE")){
						getManagedBean().setSelectedTabIndex(index + 2);
					}
				}
				if (getManagedBean().isPrepareLOA()|| ctBackingBean.getManagedBean().isViewSCL()) { // Enhancement 15851 - shaqimi
					if (outputValue.equalsIgnoreCase("LOD")) {
						if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
							getManagedBean().setSelectedTabIndex(index + 2);
						else
							getManagedBean().setSelectedTabIndex(index + 1);
					}
					if (outputValue.equalsIgnoreCase("CNT")) {
						if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
							getManagedBean().setSelectedTabIndex(index + 3);
						else
							getManagedBean().setSelectedTabIndex(index + 2);
					}
					if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq() && outputValue.equalsIgnoreCase("PB") //#24333
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
									ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))) {
						if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
							getManagedBean().setSelectedTabIndex(index + 4);
						else
							getManagedBean().setSelectedTabIndex(index + 3);
					}
					//#24333
					if ((getManagedBean().isDecisionPbQcLOA() || ctBackingBean.getManagedBean().isViewSCL()) && outputValue.equalsIgnoreCase("DCN")) {//33096
						if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
										ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))) {
							if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
								getManagedBean().setSelectedTabIndex(index + 5);
							else
								getManagedBean().setSelectedTabIndex(index + 4);
							
						} 
						//#35193 start
						else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) 
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
							getManagedBean().setSelectedTabIndex(index + 4);
						}
						//#35193 end
						else {
							getManagedBean().setSelectedTabIndex(index+3);
						}
					}
				}
				if (getManagedBean().isPbConfirmMeeting() && outputValue.equalsIgnoreCase("MET")) {
					//#31361, #31391
					//getManagedBean().setSelectedTabIndex(index + 1);
					//#31441
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&&(ctBackingBean.getManagedBean().getContractVer().getCtDeductions()!= null
							&& ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0)){
							getManagedBean().setSelectedTabIndex(index + 2);					
					}
					//#35193 start
					else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
						getManagedBean().setSelectedTabIndex(index + 2);
					}
					//#35193 end
					else{
							getManagedBean().setSelectedTabIndex(index + 1);
					}
				}
				//#24333 - Start
				if (getManagedBean().isPbConfirmMeeting() && outputValue.equalsIgnoreCase("PB")) {
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&&(ctBackingBean.getManagedBean().getContractVer().getCtDeductions()!= null
							&& ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0)
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(index + 3);					
					}
					//#35193 start
					else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(index + 3);
					}
					//#35193 end
					else{
							getManagedBean().setSelectedTabIndex(index + 2);
					}
				}//#24333 - End

					if ((getManagedBean().isSignUpdateDecision() || getManagedBean().isSignOffDecision()) 
						&& outputValue.equalsIgnoreCase("DCN")) {
					getManagedBean().setConfirmMeeting(true);
					//#24333 - Start
//					index++;
					if(getManagedBean().isPbConfirmMeeting()){
						if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
							
						if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(7);
						}else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
								&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(6);
						}
						//#35193 start
						else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(6);
						}
						/*
						else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								||ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
								&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(5);
						}
						else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								||ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(5);
						}*/
				/**		
						else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								||ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
							getManagedBean().setSelectedTabIndex(5);
						}
						//#35193 end
						else  if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(4);
						}else if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								|| ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(3);
						}
				}else{
					if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(6);
						}else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(5);
						}else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
								&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(5);
						}else  if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								||ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(4);
						}else  if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(4);
						}else if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
								|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
								&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
							getManagedBean().setSelectedTabIndex(3);
							}							
						}
					}else{
						if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
							
							if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
									&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
											&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
								getManagedBean().setSelectedTabIndex(6);
								}else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
										&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
									getManagedBean().setSelectedTabIndex(5);
								}
								//#35193 start
								else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
										&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
												&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
									getManagedBean().setSelectedTabIndex(5);
								}
								//#35193 end
								else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
										&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
												|| ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted() )){
									getManagedBean().setSelectedTabIndex(4);
								}else  if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
									&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
											&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
								getManagedBean().setSelectedTabIndex(3);
								}else if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										|| ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
									getManagedBean().setSelectedTabIndex(2);
								}							
							} else {
							
							if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
									&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
											&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
								getManagedBean().setSelectedTabIndex(5);
								}else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
										&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
									getManagedBean().setSelectedTabIndex(4);
								}else if((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
										&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
												|| ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted() )){
									getManagedBean().setSelectedTabIndex(3);
								}else  if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())
									&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
											&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
								getManagedBean().setSelectedTabIndex(3);
								}else if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
										|| !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
										&& (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
										|| ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
									getManagedBean().setSelectedTabIndex(2);
								}																	
							}
						}
					}
					//#24333 - End

				// #24333 - Start
				if (isApproverSetting() && !getManagedBean().isPrepareLOA() && outputValue.equalsIgnoreCase("SE")){ 
						if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
						&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()){ 
					getManagedBean().setSelectedTabIndex(index + 3);
				}else if((!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange() || ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())
						&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()){
					getManagedBean().setSelectedTabIndex(index + 2);
				}
				//#35193 start
				else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()) {
					getManagedBean().setSelectedTabIndex(index + 2);
				}
				//#35193 end
				else{
					getManagedBean().setSelectedTabIndex(index + 1);
				}
			}				
			// #24333 - End
				
			} 
			/*Menu for other started*/
	/**
			else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
					&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY) ){
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
					if (outputValue.equalsIgnoreCase("ZO")) {
						getManagedBean().setSelectedTabIndex(2);
					} else if (outputValue.equalsIgnoreCase("AG")) {
						getManagedBean().setSelectedTabIndex(3);
					} else if (outputValue.equalsIgnoreCase("IT")) {
						getManagedBean().setSelectedTabIndex(4);
						searchReset(); // Fix - #31042
					}
				} else {
					if (outputValue.equalsIgnoreCase("AG")) {
						getManagedBean().setSelectedTabIndex(2);
					}else if(outputValue.equalsIgnoreCase("IT")) {
						getManagedBean().setSelectedTabIndex(3);
						searchReset(); // Fix - #31042
					}
				}
				if (outputValue.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
						getManagedBean().setSelectedTabIndex(5);
					} else {
						getManagedBean().setSelectedTabIndex(4);
					}
				} 
				if (outputValue.equalsIgnoreCase("PB")) { //#24333
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
									ParameterConst.FULFILMENT_TYPE_SCHEDULED)
									|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED))) { //#25002
						getManagedBean().setSelectedTabIndex(7);
					} 
					//#35193 start
					else if(!ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing() 
							&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
							&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
									&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())){
						getManagedBean().setSelectedTabIndex(5);
					}
					//#35193 end
					else {
						getManagedBean().setSelectedTabIndex(6);
					}
				}
				if (outputValue.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) 
						&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_SCHEDULED)
								|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED))) { //#25002
					//24333 - Start
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){  
						getManagedBean().setSelectedTabIndex(6);
					} else{
						getManagedBean().setSelectedTabIndex(5);
					}
					//24333 - End
				} 
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
							ParameterConst.FULFILMENT_TYPE_SCHEDULED)
							|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)){ //#25002
						index = 6;
					} else {
						index = 5;
					}
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
						ParameterConst.FULFILMENT_TYPE_SCHEDULED)
						|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) { //#25002
					index = 5;
				} else {
					index = 4;
				}
				if (getManagedBean().isPrepareLOA()|| ctBackingBean.getManagedBean().isViewSCL()) { //Enhancement 15851 - shaqimi
					if (outputValue.equalsIgnoreCase("LOD")) {
						getManagedBean().setSelectedTabIndex(index + 1);
					}
					if (outputValue.equalsIgnoreCase("CNT")) {
						getManagedBean().setSelectedTabIndex(index + 2);
					}
					if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq() && outputValue.equalsIgnoreCase("PB") //#24333
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
									ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))) {
						getManagedBean().setSelectedTabIndex(index + 3);
					}
					if ((getManagedBean().isDecisionPbQcLOA() || ctBackingBean.getManagedBean().isViewSCL()) && outputValue.equalsIgnoreCase("DCN")) { //#15851
						if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
										ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))) {
							getManagedBean().setSelectedTabIndex(index + 4);
						} else {
							getManagedBean().setSelectedTabIndex(index+3);
						}
					}
				}
				if (getManagedBean().isPbConfirmMeeting() && outputValue.equalsIgnoreCase("MET")) {
					getManagedBean().setSelectedTabIndex(index + 1);
				}

				if ((getManagedBean().isSignUpdateDecision() || getManagedBean().isSignOffDecision()) 
						&& outputValue.equalsIgnoreCase("DCN")) {
					getManagedBean().setConfirmMeeting(true);
					//#24333 -Start
					
						if (getManagedBean().isPbConfirmMeeting() && (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())) {
							getManagedBean().setSelectedTabIndex(index + 3);
						} else if (!getManagedBean().isPbConfirmMeeting() && (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())) {
							getManagedBean().setSelectedTabIndex(index + 2);
						} else if (getManagedBean().isPbConfirmMeeting() && (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())) {
							getManagedBean().setSelectedTabIndex(index + 2);
						} else if (getManagedBean().isPbConfirmMeeting() && (!ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
								&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted())) {
							getManagedBean().setSelectedTabIndex(index + 2);
						}else {
							getManagedBean().setSelectedTabIndex(index + 1);
						}
					//#24333 -End
				}
				if (isApproverSetting() && !getManagedBean().isPrepareLOA() && outputValue.equalsIgnoreCase("SE")) {
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq()
							&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) //#24333
						getManagedBean().setSelectedTabIndex(index + 2);
					 else 
						getManagedBean().setSelectedTabIndex(index + 1);						
				}
			} 
		} else if(getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)){
			if (outputValue.equalsIgnoreCase("LOD")) {
				getManagedBean().setSelectedTabIndex(0);
			}
			if (outputValue.equalsIgnoreCase("CNT")) {
				getManagedBean().setSelectedTabIndex(1);
			}
			if (outputValue.equalsIgnoreCase("SLOA")) {
				getManagedBean().setSelectedTabIndex(2);
			}
			if ((ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq() //#24333
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
							ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE))) {
				if (outputValue.equalsIgnoreCase("PB")) {
					getManagedBean().setSelectedTabIndex(3);
				}
				if (outputValue.equalsIgnoreCase("SDEC")) {
					getManagedBean().setSelectedTabIndex(4);
				}
			} else {
				if (outputValue.equalsIgnoreCase("SDEC")) {
					getManagedBean().setSelectedTabIndex(3);
				}
			}
		}
		log.info("Check index "+getManagedBean().getSelectedTabIndex());
		log.info("Check Output "+outputValue.toString());
	}
	**/
	//#24333 - End ReSetting on whole SideTabMenu this method

	/**
	 * The method used for retrieving the contract version 
	 * for agreement or non-agreement contract.
	 * If it's original agreement, return the first contract version.
	 * If it's supplementary agreement, return the amended contract version.
	 */
	private void retrieveContractVerOfAgreement() {
		IContractSO contractSO = null;
		IContractAmendmentSO amendmentSO = null;
		ContractResDTO resDTO = null;
		ContractAmendmentResDTO amendmentResDTO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();

			if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);

				if (amendmentResDTO.getCtAmendment() != null) {
					ctBackingBean.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);

					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					ctBackingBean.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}				
			} else {
				ctBackingBean.getManagedBean().setAgreementContractVer(ctBackingBean.getManagedBean().getContractVer());
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			contractSO = null;
			amendmentSO = null;
		}
	}

	/**
	 * The method used for retrieving the committee type
	 * base on loa amount and procurement type
	 */
	private String retrieveCommitteeType() {	    

		IContractAmendmentSO contractAmendmentSO = null;
		contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		String committeeType = contractAmendmentSO.getCommitteeType(ctBackingBean.getManagedBean().getContractVer()); 
		if(committeeType == null) {
			throw new EpException(MessageConst.ERROR_750017);
		}
		return committeeType;
	}

	/**
	 * The method used to set the
	 *  secretary for different committee type.
	 */
	private void setSecretary() {
		PmUser pmUser = null;
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()) {				
			UserReqDTO userReqDTO = new UserReqDTO();
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			if (getManagedBean().getSecretaryId() != null) {
				userReqDTO.setUserId(getManagedBean().getSecretaryId());
				pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
			}				
		}
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setSecretary(pmUser);
	}

	/**
	 * The method used to retrieve the 
	 * supplier list for LOA acknowledgment.
	 */
	private void getSupplierListForLoaAck() {		
		IContractAmendmentSO amendSO = null;
		try{
			if (getManagedBean().getSupplierList() == null) {
				getManagedBean().setSupplierList(new ArrayList<SmPersonnel>());
				amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
				ContractAmendmentReqDTO amendReqDTO = new ContractAmendmentReqDTO();
				amendReqDTO.setSupplierId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
				amendReqDTO.setContractSigner(true);
				getManagedBean().setSupplierList(amendSO.retrieveSupplierListById(amendReqDTO).getSupplierList());
			}
		} finally {
			amendSO = null;
		}
	}

	/**
	 * The method used for retrieving the list of loa documents
	 * during loa preparation for contract amendment.
	 */
	private void getLoaDocumentList() {
		IDocumentSO documentSO = null;
		DocumentReqDTO reqDTO = null;
		DocumentResDTO resDTO = null;

		try {

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports() == null) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaReports(new ArrayList<ScLoaReport>());
				reqDTO = new DocumentReqDTO();
				documentSO = (IDocumentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
				reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
				resDTO = documentSO.retrieveDocumentCheckList(reqDTO);

				for (PmDocument document : resDTO.getDocumentList()) {
					ScLoaReport reports = new ScLoaReport();
					reports.setPmDocument(document);
					reports.setTitle(document.getDocName());
					reports.setDefinationType(document.getDocDefinitionTypeCode());
					reports.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
					reports.setScLoa( this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
					this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().add(reports);	
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			documentSO = null;
			reqDTO = null;
			resDTO = null;
		}		
	}

	/**
	 * The method used for rendering the related information for
	 * different amendment type.
	 * 
	 * @param value
	 */
	public boolean getAmendValue(int value) {		
		if (value == 1 && (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange())) {
			return true;
		}		
		if (value == 2  && !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()
				&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange() 
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {
			return true;
		}		
		if (value == 3  && (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange() 
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {
			return true;
		}
		return false;
	}

	/**
	 * The method used to retrieve the new amendment effective date.
	 * The new amendment effective date will not lesser than the previous
	 * amendment effective date.
	 * 
	 * @return amendment effective date.
	 */
	private Date retrieveNewEffDate() {
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		ContractAmendmentResDTO resAmendDto = amendSO.getCtAmendmentByContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
		if (resAmendDto.getAmendmentList() != null && !resAmendDto.getAmendmentList().isEmpty()) {
			int index = resAmendDto.getAmendmentList().size() - 1;
			CtAmendment ctAmendment = resAmendDto.getAmendmentList().get(index);
			if (ctAmendment.getAmendmentEffDate().getTime() < currentDate.getTime()) {
				return currentDate;
			} else {	
				return DateUtils.addDays(ctAmendment.getAmendmentEffDate(), 1);
			}
		} else {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate().equals(currentDate)) {
				return DateUtils.addDays(currentDate, 1);
			} else {
				return currentDate;
			}
		}
	}

	/**
	 * The method used to retrieve the new contract amount
	 * with addition of old contract amount and additional amount.
	 * 
	 * @return new contract amount
	 */
	public BigDecimal newContractAmt() {
		//Bug 33096 - shaqimi start
		BigDecimal ctAmt = null;
		if(ctBackingBean.getManagedBean().isViewSCL()){
			ctAmt = ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount();
		} else {
			ctAmt = ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().add(
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount());
		}
		return ctAmt;
		//Bug 33096 - shaqimi end
	}
    
	/**
	 * The method used to retrieve the new overall contract amount
	 * with addition of new contract amount and new gst amount amount.
	 * 
	 * @return new overall contract amount
	 */
	// Start - #29589
	public BigDecimal newOverallContractAmt() {
		BigDecimal contractAmt = newContractAmt();
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount() != null )  {
			contractAmt = newContractAmt().add(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount());
		
		}
		return contractAmt;
	}
	
	
	public BigDecimal newOverallContractAmount() {
		BigDecimal contractAmount = newOverallContractAmt();
	
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewStaxAmount() != null )  {
			contractAmount = newOverallContractAmt().add(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewStaxAmount());
		
		}
		return contractAmount;
	}
	// End - #29589
	
  
	/**
	 * The method used to retrieve the new contract period
	 * with addition of old contract period and extended period.
	 * 
	 * @return new contract period
	 */
	public String calculateNewContractPeriod(boolean isCalculateNew) {
		if (isCalculateNew) {
			return String.valueOf(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getContractDuration().intValue()
					+ ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod()); //#24333
		} else {
			return String.valueOf(ctBackingBean.getManagedBean().getContractVer().getCtContract()
					.getLatestContractVer().getCtContractValue().getContractPeriod());
		}
	}

	//#24333 - Start
	/**
	 * Retrieves the bond amount in text format (in words).
	 * Returns "N/A" if bond amount is not available or zero.
	 *
	 * @return bond amount in words, or "N/A" if null or zero
	 */
	public String getBondAmountInText() {
		FacesScopes scopes = new FacesScopes();
	    Locale locale = scopes.getUserLocale();
	    String lang = locale.getLanguage();

	    String na = ResourceBundleUtils.getMessage("lbl.txt.na", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);

	    // Traverse the object chain with null checks
	    CtContractVer contractVer = ctBackingBean.getManagedBean().getContractVer();
	    if (contractVer == null || 
	        contractVer.getCtAmendment() == null || 
	        contractVer.getCtAmendment().getAmendmentLoa() == null || 
	        contractVer.getCtAmendment().getAmendmentLoa().getBondAmt() == null) {
	        return na;
	    }

	    BigDecimal bondAmt = contractVer.getCtAmendment().getAmendmentLoa().getBondAmt();

	    if (BigDecimal.ZERO.compareTo(bondAmt) == 0) {
	        return na;
	    }

	    // Format to 2 decimal places
	    DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(locale));
	    String formatted = df.format(bondAmt);

	    // Convert based on language
	    if (ReportConst.ENG_LANGUAGE_CODE.equalsIgnoreCase(lang)) {
	        return NumberWordUtilEn.convert(formatted);
	    } else if (ReportConst.MALAY_LANGUAGE_CODE.equalsIgnoreCase(lang)) {
	        return NumberWordUtil.convert(formatted);
	    }

	    return na;
	}
	//#24333 - End
	
	/**
	 * The method used to indicate the label 
	 * flag for new price of product or service item.
	 * 
	 * @param ctContractItem	the object of contract item
	 */
	private void newItemPrice(CtContractItem ctContractItem) {
		//#20997--Start
		//Bug #29323 - Start 
		//Bug 34961 - shaqimi start 12/07/2023 - insert code fix, and reformat code for readability
		if(getManagedBean().getSelectedItem() != null){	
			for(CtContractItem itemPrice : ctBackingBean.getManagedBean().getCtItemList()){
				if(itemPrice.getScRequestItem().getRequestNoteItemId().equals(getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId())){
					//#30879,#31065 for Max QTY & price -Start
					getManagedBean().getSelectedItem().getCtItemPrices().remove(itemPrice.getCtItemPrices());
					getManagedBean().getSelectedItem().setCtItemPrices(itemPrice.getCtItemPrices());
					//#30879,#31065 for Max QTY & price - End
					for(CtItemPrice ctItemPrice : itemPrice.getCtItemPrices()){
						if(itemPrice.isBrandNew()){
							ctItemPrice.setItemPriceId(null);
						}
						if (ctContractItem != null && !ctContractItem.getCtItemPrices().isEmpty() 
								&& (ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
							if(ctItemPrice.isChangePrice()){
								ctItemPrice.setChangeZoneLabel(true);
							}
						}
					}
					break;
				}							
			}	
		}
		
		if(getManagedBean().getSelectedServiceItem() != null){	
			for(CtContractItem itemPrice : ctBackingBean.getManagedBean().getCtItemServiceList()){
				if(itemPrice.getScRequestItem().getRequestNoteItemId().equals(getManagedBean().getSelectedServiceItem().getScRequestItem().getRequestNoteItemId())){
					//#30879,#31065 for Max QTY & price -Start
					getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(itemPrice.getCtItemPrices());
					getManagedBean().getSelectedServiceItem().setCtItemPrices(itemPrice.getCtItemPrices());
					//#30879,#31065 for Max QTY & price - End
					for(CtItemPrice ctItemPrice : itemPrice.getCtItemPrices()){
						if(itemPrice.isBrandNew()){
							ctItemPrice.setItemPriceId(null);
						}
						if (ctContractItem != null && !ctContractItem.getCtItemPrices().isEmpty() 
								&& (ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
							if(ctItemPrice.isChangePrice()){
								ctItemPrice.setChangeZoneLabel(true);
							}
						}
					}
					break;
				}							
			}	
		}
	}
	//Bug 34961 - shaqimi end 12/07/2023
	//Bug #29323 - End
			//Bug #28339 for Fixing #20997--End
	//#20997 -- End
	/**
	 * The method used to edit the product item price.
	 * This method will creating a new records in the item price list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new item price for the product item.
	 */
	public void editItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();

		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		// Start Fix - #33287
		Date currentAmendEffDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate();

		Calendar nextDate = Calendar.getInstance();
		nextDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		nextDate.add(Calendar.DATE, 1);
		// End Fix - #33287

		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						//addDupObj.setExpDate(ctItemPrice.getExpDate());
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate()); //Fix - #33256
					}
					if (ctItemPrice.getCtContractZone() != null) {
						addDupObj.setCtContractZone(ctItemPrice.getCtContractZone());
					}
					if (ctItemPrice.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemPrice.getCtContractItem());
					}
					addDupObj.setChangePrice(true);
					// Start Fix - #33287
					if (ctItemPrice.getEffDate().compareTo(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()) == 0
							|| ctItemPrice.getEffDate().compareTo(ctItemPrice.getExpDate()) == 1) {
						ctItemPrice.setExpDate(currentAmendEffDate);
						addDupObj.setEffDate(nextDate.getTime());
					} else {
						ctItemPrice.setExpDate(previousDate.getTime());
					}
					// End Fix - #33287
					addDupObj.setAssociateCtItemPrice(ctItemPrice);
					ctItemPrice.setChkIndexOfRow(true);
					ctItemPriceList.add(addDupObj);
					log.info("totalsize itemprices "+addDupObj.getItemPriceId());
					log.info("totalsize itemprices "+ctItemPriceList.size());
				}
			}
			// Start fix - #33256
			log.info(" Added changed value index in getSelectedChangeItemPrice");
			getManagedBean().getSelectedChangeItemPrice().put(ctItemPrice.getItemPriceId(),ctItemPrice);
			// End fix - #33256
		}
		this.getManagedBean().getSelectedItem().getCtItemPrices().clear();
		this.getManagedBean().getSelectedItem().getCtItemPrices().addAll(ctItemPriceList);
		//Bug #29323 - Remove Code
//		for(CtItemPrice itemPrice : getManagedBean().getSelectedItem().getCtItemPrices()){
//			log.info("totalsize itemprices "+itemPrice.getItemPriceId());
//		}
		//Start Fix - #34110 , #34950
		if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
			   sortItemPriceForZonalTier(ctItemPriceList);
		}else if(getManagedBean().getSelectedItem().getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)){
			   sortItemPriceForTier(ctItemPriceList);
		} else {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				sortItemPriceForZonal(ctItemPriceList);
			} else {
				sortItemPrice(ctItemPriceList);
			}
		}
		//End Fix - #34110 , #34950
		newItemPrice(getManagedBean().getSelectedItem());
		ctItemPriceList.clear();

		//CT_CHANGE_LOG
		ctBackingBean.getManagedBean().getUpdateItemMap().put(getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId(), getManagedBean().getSelectedItem());
	}

	/**
	 * The method used to edit the service item price.
	 * This method will creating a new records in the item price list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new item price for the service item.
	 */	
	public void editServiceItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();

		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		// Start Fix - #33287
		Date currentAmendEffDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate();

		Calendar nextDate = Calendar.getInstance();
		nextDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		nextDate.add(Calendar.DATE, 1);
		// End Fix - #33287

		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtServiceItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						//addDupObj.setExpDate(ctItemPrice.getExpDate());
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate()); //Fix - #33256

					}
					if (ctItemPrice.getCtContractZone() != null) {
						addDupObj.setCtContractZone(ctItemPrice.getCtContractZone());
					}
					if (ctItemPrice.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemPrice.getCtContractItem());
					}
					addDupObj.setChangePrice(true);
					//Start Fix - #33287
					if(ctItemPrice.getEffDate().compareTo(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()) == 0
							|| ctItemPrice.getEffDate().compareTo(ctItemPrice.getExpDate()) == 1){
						ctItemPrice.setExpDate(currentAmendEffDate);
						addDupObj.setEffDate(nextDate.getTime());
					}else{
						ctItemPrice.setExpDate(previousDate.getTime());
					}
					//End Fix - #33287
					addDupObj.setAssociateCtItemPrice(ctItemPrice);
					ctItemPrice.setChkIndexOfRow(true);
					ctItemPriceList.add(addDupObj);
				}
			}
			// Start fix - #33256
			log.info(" Added changed value index in editServiceItemPriceOnIndexs ");
			getManagedBean().getSelectedChangeItemPrice().put(ctItemPrice.getItemPriceId(),ctItemPrice);
			// End fix - #33256
		}
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().clear();
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().addAll(ctItemPriceList);
		// Start Fix - #34110 , #34950
		if (getManagedBean().getSelectedServiceItem().getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
			sortItemPriceForZonalTier(ctItemPriceList);
		} else if (getManagedBean().getSelectedServiceItem().getPriceType().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER)) {
			sortItemPriceForTier(ctItemPriceList);
		} else {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				sortItemPriceForZonal(ctItemPriceList);
			} else {
				sortItemPrice(ctItemPriceList);
			}
		}
		// End Fix - #34110 , #34950
		newItemPrice(getManagedBean().getSelectedServiceItem());
		ctItemPriceList.clear();

		//CT_CHANGE_LOG
		ctBackingBean.getManagedBean().getUpdateItemMap().put(getManagedBean().getSelectedServiceItem().getScRequestItem().getRequestNoteItemId(), getManagedBean().getSelectedServiceItem());
	}

	/**
	 * The method used to delete the 
	 * newly added product item price.
	 */
	public void deleteItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		getManagedBean().getSelectedItem().getCtItemPrices().remove(getManagedBean().getCtItemPrices());
		//#30879,#31065 for Max QTY & price -Start
//		for(CtItemPrice itemPrice : getManagedBean().getSelectedItem().getCtItemPrices()){
//			itemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
//			itemPrice.setChkIndexOfRow(false);			
//		}
		//#30879,#31065 for Max QTY & price -End
		newItemPrice(getManagedBean().getSelectedItem());
		getManagedBean().getSelectedChangeItemPrice().put(ctItemPrice.getItemPriceId(),ctItemPrice); //Fix - #33256

	}

	/**
	 * The method used to delete 
	 * the newly added service item price.
	 */
	public void deleteServiceItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtServiceItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(getManagedBean().getCtServiceItemPrices());
		//#30879,#31065 for Max QTY & price -Start
//		for(CtItemPrice itemPrice : getManagedBean().getSelectedServiceItem().getCtItemPrices()){
//			itemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
//			itemPrice.setChkIndexOfRow(false);			
//		}
		//#30879,#31065 for Max QTY & price -End
		newItemPrice(getManagedBean().getSelectedServiceItem());
		getManagedBean().getSelectedChangeItemPrice().put(ctItemPrice.getItemPriceId(),ctItemPrice); //Fix - #33256

	}

	/**
	 * The method used to edit the ceiling quantity of product item.
	 * This method will creating a new records in the ceiling quantity list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new ceiling quantity for the product item.
	 */	
	public void editCeilingQuantityOnIndexs() {
		List<CtItemCeilingQty> ctItemCeilingQtys = new ArrayList<CtItemCeilingQty>();
		CtItemCeilingQty addDupObj = new CtItemCeilingQty();

		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);

		for (CtItemCeilingQty ceilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			ctItemCeilingQtys.add(ceilingQty);
			if (!ceilingQty.isChangeCeilQuantity() && !ceilingQty.isChkIndexOfRow()) {
				if (ceilingQty.equals(this.getManagedBean().getCtItemCeilingQtys())) {
					addDupObj.setCeilingQuantity(ceilingQty.getCeilingQuantity());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						addDupObj.setExpDate(ceilingQty.getExpDate());
					}
					if (ceilingQty.getCtAgency() != null) {
						addDupObj.setCtAgency(ceilingQty.getCtAgency());
					}
					if (ceilingQty.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ceilingQty.getCtContractItem());
					}
					addDupObj.setChangeCeilQuantity(true);
					addDupObj.setAssociateCtItemCeilingQty(ceilingQty);
					ceilingQty.setChkIndexOfRow(true);
					ceilingQty.setExpDate(previousDate.getTime());
					ctItemCeilingQtys.add(addDupObj);
				}
			}
		}
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().addAll(ctItemCeilingQtys);
		ctItemCeilingQtys.clear();
	}

	/**
	 * The method used to delete
	 *  the newly added ceiling quantity.
	 */
	public void deleteCeilingQuantityOnIndexs() {
		CtItemCeilingQty ctItemCeilingQty = getManagedBean().getCtItemCeilingQtys().getAssociateCtItemCeilingQty();
		ctItemCeilingQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemCeilingQty.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().remove(getManagedBean().getCtItemCeilingQtys());		
	}

	/**
	 * The method used to edit the maximum quantity of product item.
	 * This method will creating a new records in the maximum quantity list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new maximum quantity for the product item.
	 */
	public void editItemMaxQtyOnIndexs() {
		List<CtItemMaxQty> ctItemMaxQtyList = new ArrayList<CtItemMaxQty>();
		CtItemMaxQty addDupObj = new CtItemMaxQty();

		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		// Start Fix - #33287
		Date currentAmendEffDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate();

		Calendar nextDate = Calendar.getInstance();
		nextDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		nextDate.add(Calendar.DATE, 1);
		// End Fix - #33287

		for (CtItemMaxQty ctItemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			ctItemMaxQtyList.add(ctItemMaxQty);
			if (!ctItemMaxQty.isChangeQuantity() && !ctItemMaxQty.isChkIndexOfRow()) {
				if (ctItemMaxQty.equals(getManagedBean().getCtItemMaxQty())) {
					addDupObj.setMaxQuantity(ctItemMaxQty.getMaxQuantity());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						//addDupObj.setExpDate(ctItemMaxQty.getExpDate());
						addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate()); // Fix -#33256

					}
					if (ctItemMaxQty.getCtContractZone() != null) {
						addDupObj.setCtContractZone(ctItemMaxQty.getCtContractZone());
					}
					if (ctItemMaxQty.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemMaxQty.getCtContractItem());
					}
					addDupObj.setChangeQuantity(true);
					//Start Fix - #33287
					if(ctItemMaxQty.getEffDate().compareTo(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()) == 0
							|| ctItemMaxQty.getEffDate().compareTo(ctItemMaxQty.getExpDate()) == 1){
						ctItemMaxQty.setExpDate(currentAmendEffDate);
						addDupObj.setEffDate(nextDate.getTime());
					}else{
						ctItemMaxQty.setExpDate(previousDate.getTime());
					}
					//End Fix - #33287
					addDupObj.setAssociateCtItemMaxQty(ctItemMaxQty);
					ctItemMaxQty.setChkIndexOfRow(true);
					ctItemMaxQtyList.add(addDupObj);
				}
			}
			// Start fix - #33256
			log.info(" Added changed value index in getSelectedChangeItemQuantity");
			getManagedBean().getSelectedChangeItemQuantity().put(ctItemMaxQty.getItemMaxQtyId(),ctItemMaxQty);
			// End fix - #33256
		}
		getManagedBean().getSelectedItem().getCtItemMaxQties().clear();
		getManagedBean().getSelectedItem().getCtItemMaxQties().addAll(ctItemMaxQtyList);
		//Start Fix - #34110 , #34950
		if(ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
			sortItemMaxQtyForZonal(ctItemMaxQtyList);
		}else{
			sortItemMaxQty(ctItemMaxQtyList);
		}
		//End Fix - #34110 , #34950
		newItemMaxQty(getManagedBean().getSelectedItem());
		ctItemMaxQtyList.clear();

		//CT_CHANGE_LOG
		ctBackingBean.getManagedBean().getUpdateItemMap().put(getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId(), getManagedBean().getSelectedItem());
	}

	/**
	 * The method used to delete 
	 * the newly added maximum quantity.
	 */
	public void deleteItemMaxQtyOnIndexs() {
		CtItemMaxQty ctItemMaxQty = getManagedBean().getCtItemMaxQty().getAssociateCtItemMaxQty();
		ctItemMaxQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemMaxQty.setChkIndexOfRow(false);
		getManagedBean().getSelectedItem().getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty());
		//#30879,#31065 for Max QTY & price -Start
//		for(CtItemMaxQty itemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties()){
//			itemMaxQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
//			itemMaxQty.setChkIndexOfRow(false);			
//		}
		//#30879,#31065 for Max QTY & price -Start
		newItemMaxQty(getManagedBean().getSelectedItem());
		getManagedBean().getSelectedChangeItemQuantity().put(ctItemMaxQty.getItemMaxQtyId(),ctItemMaxQty); //Fix - #33256
		System.out.println("Process item removed ::::::::::::::: !!"+getManagedBean().getSelectedItem().getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty()));
	

	}

	/**
	 * The method used to indicate the label 
	 * flag for new max order quantity of product item.
	 */
	private void newItemMaxQty(CtContractItem ctContractItem) {
		//#20997--Start
		//Bug #29323 - Start
		if(getManagedBean().getSelectedItem() != null){	
			
			for(CtContractItem itemPrice : ctBackingBean.getManagedBean().getCtItemList()){
				if(itemPrice.getScRequestItem().getRequestNoteItemId().equals(getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId())){
					//#30879,#31065 for Max QTY & price -Start
					getManagedBean().getSelectedItem().getCtItemMaxQties().remove(itemPrice.getCtItemMaxQties());
					getManagedBean().getSelectedItem().setCtItemMaxQties(itemPrice.getCtItemMaxQties());
					//#30879,#31065 for Max QTY & price -End
					for(CtItemMaxQty ctItemMaxQty : itemPrice.getCtItemMaxQties()){
						if(itemPrice.isBrandNew()){
							ctItemMaxQty.setItemMaxQtyId(null);
						}
						//Bug 34961 - shaqimi start 12/07/2023
						if (ctContractItem != null && !ctContractItem.getCtItemPrices().isEmpty() 
								&& (ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
							if(ctItemMaxQty.isChangeQuantity()){
								ctItemMaxQty.setChangeZoneLabel(true);
							}
						}
						//Bug 34961
					}
					break;
				}							
			}	
		}
		
		if(getManagedBean().getSelectedServiceItem() != null){	
			
			for(CtContractItem itemPrice : ctBackingBean.getManagedBean().getCtItemServiceList()){
				if(itemPrice.getScRequestItem().getRequestNoteItemId().equals(getManagedBean().getSelectedServiceItem().getScRequestItem().getRequestNoteItemId())){
					//#30879,#31065 for Max QTY & price -Start
					getManagedBean().getSelectedServiceItem().getCtItemMaxQties().remove(itemPrice.getCtItemMaxQties());
					getManagedBean().getSelectedServiceItem().setCtItemMaxQties(itemPrice.getCtItemMaxQties());
					//#30879,#31065 for Max QTY & price -End
					for(CtItemMaxQty ctItemMaxQty : itemPrice.getCtItemMaxQties()){
						if(itemPrice.isBrandNew()){
							ctItemMaxQty.setItemMaxQtyId(null);
						}
						//Bug 34961 - shaqimi start 12/07/2023
						if (ctContractItem != null && !ctContractItem.getCtItemPrices().isEmpty() 
								&& (ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER) 
										|| ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
							if(ctItemMaxQty.isChangeQuantity()){
								ctItemMaxQty.setChangeZoneLabel(true);
							}
						}
						//Bug 34961
					}
					break;
				}							
			}	
		}	
	}
	//Bug #29323 - End
	//#20997--End
	/**
	 * The method used to retrieve the item ceiling quantity which in active status
	 * to be displayed on screen.
	 * 
	 * @return activeList  the list of active ceiling quantity to be displayed 
	 */
	public List<CtItemCeilingQty> getActiveItemCeilingQty() {
		List<CtItemCeilingQty> activeList = new ArrayList<CtItemCeilingQty>();
		if (getManagedBean().getSelectedItem() != null) {
			for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
				//#30428 - agency not display because isChkStatus is false. To re-check status for isChkStatus if it is false
				if (!ctItemCeilingQty.getCtAgency().isChkStatus()) {
					  if (ctItemCeilingQty.getCtAgency().getRecordStatusFlg() != 0 
							  && ctItemCeilingQty.getCtAgency().getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
						  ctItemCeilingQty.getCtAgency().setChkStatus(false);
					  } else {
						  ctItemCeilingQty.getCtAgency().setChkStatus(true);
					  }
				}
				
				if (ctItemCeilingQty.getCtAgency().isChkStatus()) {
					activeList.add(ctItemCeilingQty);
				}
			}
		}
		return activeList;
	}

	/**
	 * The method used for rendering the item price or ceiling quantity
	 * of selected product item that to be displayed on screen.
	 */
	public void onSelectProductItem() {

		if (getManagedBean().getSelectedItem() != null) {
			log.info("Cek Putra VerId :"+getManagedBean().getSelectedItem().getContractItemId().toString());
			//getManagedBean().setItemServiceExist(false); #20997
			if (ctBackingBean.getManagedBean().getShowItem() != null 
					&& ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				ctBackingBean.getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);

			} else if (ctBackingBean.getManagedBean().getShowItem() != null 
					&& ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
				ctBackingBean.getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			} else if (ctBackingBean.getManagedBean().getShowItem() != null 
					&& ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
				ctBackingBean.getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);				
			} else {
				ctBackingBean.getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
			}
			
			if (ctBackingBean.getManagedBean().getShowItem() == null){
				getManagedBean().setItemProductExist(false);
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
				newItemPrice(getManagedBean().getSelectedItem());
				changeSelectedItemPriceAmendDate(getManagedBean().getSelectedItem());
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
				newItemMaxQty(getManagedBean().getSelectedItem());
				changeSelectedItemMaxQtyAmendDate(getManagedBean().getSelectedItem());
			}
		}
	}

	/**
	 * The method used to assign the amendment period for new
	 * item price and old item price.
	 * 
	 * @param selectedItem  the selected item instance
	 */
	private void changeSelectedItemPriceAmendDate(CtContractItem selectedItem) {
		// Start Fix - #33287
		Date currentAmendEffDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate();
		Calendar nextDate = Calendar.getInstance();
		nextDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		nextDate.add(Calendar.DATE, 1);
		// End Fix - #33287
		if (selectedItem != null && selectedItem.getCtItemPrices() != null && selectedItem.getCtItemPrices().size() > 0) {
			List<CtItemPrice> itemPriceList = selectedItem.getCtItemPrices();
			for (int i = 0; i < itemPriceList.size(); i++) {
				if (itemPriceList.get(i).isChangePrice()) {
					//Start Fix - #33287
					if(itemPriceList.get(i - 1).getEffDate().compareTo(currentAmendEffDate) == 0){
						itemPriceList.get(i - 1).setExpDate(currentAmendEffDate);
						itemPriceList.get(i).setEffDate(nextDate.getTime());						
					}else{
						itemPriceList.get(i - 1).setExpDate(getPreviousDate().getTime());
						itemPriceList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					}
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						itemPriceList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						itemPriceList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
						getManagedBean().getSelectedChangeItemPrice().put(itemPriceList.get(i - 1).getItemPriceId(),
								itemPriceList.get(i - 1));
						getManagedBean().getSelectedChangeItemPrice().put(itemPriceList.get(i).getItemPriceId(),
								itemPriceList.get(i));
					}
				   //End Fix - #33287
				}
			}
		}
	}

	/**
	 * The method used to assign the amendment period for new
	 * item max order quantity and old item max order quantity.
	 * 
	 * @param selectedItem  the selected item instance
	 */
	private void changeSelectedItemMaxQtyAmendDate(CtContractItem selectedItem) {
		// Start Fix - #33287
		Date currentAmendEffDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate();
		Calendar nextDate = Calendar.getInstance();
		nextDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		nextDate.add(Calendar.DATE, 1);
		// End Fix - #33287
		if (selectedItem != null && selectedItem.getCtItemMaxQties() != null && selectedItem.getCtItemMaxQties().size() > 0) {
			List<CtItemMaxQty> itemMaxQtyList = selectedItem.getCtItemMaxQties();
			for (int i = 0; i < itemMaxQtyList.size(); i++) {
				if (itemMaxQtyList.get(i).isChangeQuantity()) {
					//Start Fix - #33287
					if(itemMaxQtyList.get(i - 1).getEffDate().compareTo(currentAmendEffDate) == 0){
						itemMaxQtyList.get(i - 1).setExpDate(currentAmendEffDate);
						itemMaxQtyList.get(i).setEffDate(nextDate.getTime());
					}else{
						itemMaxQtyList.get(i - 1).setExpDate(getPreviousDate().getTime());
						itemMaxQtyList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					}
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						itemMaxQtyList.get(i).setExpDate(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						itemMaxQtyList.get(i).setExpDate(this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
					if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
						getManagedBean().getSelectedChangeItemQuantity().put(itemMaxQtyList.get(i - 1).getItemMaxQtyId(),
								itemMaxQtyList.get(i - 1));
						getManagedBean().getSelectedChangeItemQuantity().put(itemMaxQtyList.get(i).getItemMaxQtyId(),
								itemMaxQtyList.get(i));
					}
					//End Fix - #33287
				}
			}
		}
	}

	/**
	 * The method used for rendering the item price
	 * of selected service item that to be displayed on screen.
	 */
	public void onSelectServiceItem() {
		if (getManagedBean().getSelectedServiceItem() != null) {
			//getManagedBean().setItemProductExist(false); #20997
			if (ctBackingBean.getManagedBean().getShowServiceItem() != null
					&& ctBackingBean.getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				ctBackingBean.getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
			} else if (ctBackingBean.getManagedBean().getShowServiceItem() != null
					&& ctBackingBean.getManagedBean().getShowServiceItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_QTY)) {
				ctBackingBean.getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_QTY);
			} else {
				ctBackingBean.getManagedBean().setShowServiceItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
			}
			
			if (ctBackingBean.getManagedBean().getShowServiceItem() == null){
				getManagedBean().setItemServiceExist(false);
			}

			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
				newItemPrice(getManagedBean().getSelectedServiceItem());
				changeSelectedItemPriceAmendDate(getManagedBean().getSelectedServiceItem());
			}
		}

	}

	/**
	 * The method used to add a new meeting record.
	 * This method will generate a new record in meeting
	 * list, user allow to enter the meeting details. 
	 */
	public void addPbQccMeetingDetails() {
		CtMeeting ctMeeting = new CtMeeting();
		ctMeeting.setCtAmendment(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment());
		this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().add(ctMeeting);
	}

	/**
	 * The method used to remove the selected meeting detail(s).
	 */
	public void delPbQccMeetingDetails() {
		for (int i = 0; i <selectedMeetingUser.length; i++) {
			CtMeeting meeting = selectedMeetingUser[i];
			this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().remove(meeting);
		}
		selectedMeetingUser = null;
		ctBackingBean.getManagedBean().setButtonHideActive(true);
	}

	/**
	 * The method used to invoke and initiate the search committee 
	 * member dialog.
	 */
	public void fetchCommitteMembers() {
		getManagedBean().setCommitteeName(null);
		getManagedBean().setIdentificationNo(null);
		getManagedBean().setIdentificationType(null);
		getCommitteDataModel().resetData();
		RequestContext.getCurrentInstance().execute("dlg2.show()");
	}

	/**
	 * The method used to add the selected existing committee
	 * member as the meeting committee.
	 * This method will generate new record(s) in meeting
	 * committee list.
	 */
	public void addSelectedCommitteMembers() {			
		Map<Long, CtCommitteeMember> duplicatePmUserMap = new HashMap<Long, CtCommitteeMember>();
		List<CtCommitteeMember> memberList = new ArrayList<CtCommitteeMember>();

		Iterator<PmUser> pmUserItr = getCommitteDataModel().getList().iterator();
		while(pmUserItr.hasNext()) {
			PmUser users = pmUserItr.next();
			if(users.isSelectedCheckBox()) {
				CtCommitteeMember committeeMember = new CtCommitteeMember();
				committeeMember.setCtAmendment(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment());
				committeeMember.setPmUser(users);
				committeeMember.setMemberRole(new PmParameter());
				committeeMember.setMemberRoleId(null);
				committeeMember.setMemberName(users.getUserName());
				committeeMember.setIcPassport(users.getIdentificationNo());
				committeeMember.setGrade(users.getGrade());
				committeeMember.setPnp(users.isPMP());
				committeeMember.setDesignation(users.getDesignation());
				committeeMember.setEmail(users.getEmail());

				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() == 0) {
					memberList.add(committeeMember);
				} else {
					duplicatePmUserMap.put(users.getUserId(), committeeMember);
				}
			}
		}
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() == 0) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().addAll(memberList);
		} else {
			List<CtCommitteeMember> committeMember = new ArrayList<CtCommitteeMember>();
			for (Map.Entry<Long, CtCommitteeMember> entry : duplicatePmUserMap.entrySet()) {
				boolean chkDataDuplication = false;
				for (CtCommitteeMember ctMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
					if (entry.getKey().equals(ctMember.getPmUser().getUserId())) {
						chkDataDuplication = true;
					}
				}
				if (!chkDataDuplication) {
					committeMember.add(entry.getValue());
				}	    
			}
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().addAll(committeMember);
		} 
		//Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers());  
	}

	/**
	 * The method used to select all added meeting committee(s).
	 * And for status fa pending meeting decision or fa pending meeting decision revision will use updateCommitteeMember
	 * else will use ctCommitteeMember
	 */
	public void selectAllCommittees() {
		
		if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null 
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() > 0){
			
			//#29403 start #26688 start
			for (CtCommitteeMember  ctCommiteMember : (ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
					|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) ?
							getManagedBean().getUpdatedCommitteeMembers() : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
			//#29403 end #26688 end
				if (getManagedBean().isSelectAll()) {
					if (ctCommiteMember.getMemberRole() != null && ctCommiteMember.getMemberRole().getParameterCode() != null
							&& ctCommiteMember.getMemberRole().getParameterCode().equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
						continue;
					} else {
						ctCommiteMember.setSelectedCheckBox(true);
						if(ctBackingBean.getManagedBean().isButtonHideActive()){
							ctBackingBean.getManagedBean().setButtonHideActive(false);
						}
					}
				} else {
					ctCommiteMember.setSelectedCheckBox(false);
					if(!ctBackingBean.getManagedBean().isButtonHideActive()){
						ctBackingBean.getManagedBean().setButtonHideActive(true);
					}
				}
			}
		}
	}

	/**
	 * The method used to delete the selected meeting committee(s).
	 */
	public void removeCommitteeMembers(){
		try {
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
				Iterator<CtCommitteeMember> committeeMembers = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().iterator();
				while (committeeMembers.hasNext()) {
					CtCommitteeMember  members = committeeMembers.next();
					if (members.isSelectedCheckBox()) {
						committeeMembers.remove();
					}
				}
				ctBackingBean.getManagedBean().setButtonHideActive(true);

			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally{

		}
	}	

	/**
	 * The method used to get the parameter description by parameter.
	 * with additional setting for secretary role.
	 * Attendance is compulsory for secretary.
	 * 
	 * @param   index      the current record index
	 * @param   parameter  the PmParameter instance
	 * @return  parameter description
	 */
	public String getParameterDesc(int index,  PmParameter parameter) {
		//#26520 - start
		String strDescription = null;
		//26753 - start
		CtCommitteeMember ctCommitteeMember = null;
		if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
			|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
			ctCommitteeMember = getManagedBean().getUpdatedCommitteeMembers().get(index);
			
			//#29403 start
			if (parameter == null){
				parameter = ctCommitteeMember.getMemberRole(); 
			}
			//#29403 end
			
		} else {
			ctCommitteeMember = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().get(index);
		}
		//26753 - end
		if (parameter != null && parameter.getParameterCode() != null 
				&& parameter.getParameterCode().equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
			ctCommitteeMember.setAttended(true);
			//26753 - start
			if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId()) 
					|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
				getManagedBean().getUpdatedCommitteeMembers().remove(index);
				getManagedBean().getUpdatedCommitteeMembers().add(index, ctCommitteeMember);
			}else {//26753			
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().remove(index);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().add(index, ctCommitteeMember);
			}//26753 - end
			strDescription = ResourceBundleUtils.getMessage("lbl.ct.secretary", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			strDescription = getParameterDesc(parameter);
		}
		return strDescription;
		//#26520 - end
	}

	/**
	 * The method used for rendering the representative type
	 * selection.
	 * 
	 * @param   parameterId  the parameter id for member role
	 * @param   pnp  		 the pnp flag
	 * @return  true   if the committee has member role and is not pnp
	 * 			false  if the committee has not member role and is pnp
	 */
	public boolean chkBooleanRepresentType(Long parameterId, boolean pnp) {		
		if ((parameterId == null || parameterId == 0) && !pnp) {
			return true;
		}
		if (parameterId != null && parameterId != 0
				&& ParameterHelper.getInstance().getPmParameterById(parameterId).getParameterCode().equalsIgnoreCase(
						ParameterConst.COMMITTEE_ROLE_MEMBER)) {
			return true;
		} 
		return false;
	}

	/**
	 * The method used to set the meeting decision. 
	 */
	public void setPbQcDecisionValue() {
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApprovedFlag(0);
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApproved(false);
		if (getManagedBean().getPbQcApproved()!=null && getManagedBean().getPbQcApproved().equalsIgnoreCase("A")) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApprovedFlag(1);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApproved(true);
		}
	}

	/**
	 * The method used to set the MOF meeting decision. 
	 */
	public void setMofDecisionValue() {
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setMofApprovedFlag(0);
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setMofApproved(false);
		if (getManagedBean().getMofApproved()!=null && getManagedBean().getMofApproved().equalsIgnoreCase("A")) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setMofApprovedFlag(1);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setMofApproved(true);
		}
	}
	//Start Fix - #33287
	/**
	 * The method used to set the amendment information
	 * when user wish to amend the contract period.
	 */
	public void onContractExtensionChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(1); // default to 1 month
			Calendar c = Calendar.getInstance();
			c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			c.add(Calendar.DATE, 1);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());

			//#26076 - start
			//c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DATE, -1);
			//#26076 - end
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
			spToCheck = false; //Fix - #33287
			isUntickContractExtension = false; //Fix - #33287
		}
		else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(0);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(null);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(null);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getNewAmendmentEffDate());
			//Start Fix - #33287
			isUntickContractExtension = true; 
			spToCheck = true; 
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime()); 
			//End Fix - #33287
		}
		changeAmendmentEffDate();
	}

	/**
	 * The method used to set the related amendment information
	 * when user wish to amend the item price.
	 */
	public void onItemPriceChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			// contract type: central and followLoaAmount: false, disallow to change contract value
			if (isCentralContractWithoutLoaAmt() && isNotChangedByGstOrSst()) { //Fix - #33287
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);				
			}
			spToCheck = false; //Fix - #33287
			isUntickItemPrice = false; //Fix - #33287
		} else if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			onChangeValueChecked();
//			resetItemPrice(); // #30879 
		}
		if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			resetItemPrice(); // #30879
			//Start Fix - #33287
			isUntickItemPrice = true;
			spToCheck = true;
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			//End Fix - #33287
		}
		ConstructTabSideMenu();
	}

	// Start Fix - #33287
	private boolean isCentralContractWithoutLoaAmt() {
		return ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)
				&& !ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt();
	}

	private boolean isNotChangedByGstOrSst() {
		return !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst();
	}
	// End Fix - #33287
	
	/**
	 * The method used to set the related amendment information
	 * when user wish to amend the item quantity.
	 */
	public void onQuantityChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			// contract type: central and followLoaAmount: false, disallow to change contract value
			if (isCentralContractWithoutLoaAmt() && isNotChangedByGstOrSst()) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);				
			}
			spToCheck = false; //Fix - #33287
			isUntickItemMaxQty = false; //Fix - #33287
		} else if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			onChangeValueChecked();
//			resetItemQty(); // #30879 
		}
		if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			resetItemQty(); // #30879
			//Start Fix - #33287
			isUntickItemMaxQty = true;
			spToCheck = true;
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			//End Fix - #33287
		}
		ConstructTabSideMenu();
	}

	/**
	 * The method used to set the amendment information
	 * when user wish to amend the contract value.
	 */
	public void onChangeValueChecked() {
		//Start Fix - #33287
		if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange()){
			spToCheck = false;
		}
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
		if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange()){
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			spToCheck = true;
		}
		//End Fix - #33287
	}

	/**
	 * The method used to set the amendment information
	 * when user wish to amend the contract value caused by gst.
	 */
	public void onChangeValueByGstChecked() {
		if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()){ //Fix - #33287
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
		setGstAndStaxAmounts();
		
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setApproverAuthorityReq(false);
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst() || ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setApproverAuthorityReq(true);
		}
		spToCheck = false; //Fix - #33287
		}//Fix - #33287
		else if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByStax()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			//Start Fix - #33287
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setApproverAuthorityReq(false);
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			spToCheck = true;
		}else if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChangeByGst()){
			isCentralContractTypeCheck();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setApproverAuthorityReq(false);
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			spToCheck = true;
		}
		//End Fix - #33287
	}
	
	// Start Fix - #33287
	/**
	 * To reset the value of change contract value(auto-tick mechanism) that
	 * depends on onChangeValueByGstChecked() method
	 */
	private void isCentralContractTypeCheck() {
		if (isCentralContractWithoutLoaAmt() && isNotChangedByGstOrSst()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
		}
	}

	/**
	 * Extracted criteria to set from onChangeValueByGstChecked() method to set
	 * the gstAmount and staxAmout
	 */
	private void setGstAndStaxAmounts() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null) {
			ctBackingBean
					.getManagedBean()
					.getContractVer()
					.getCtAmendment()
					.setNewGstAmount(
							ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount());
		} else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(BigDecimal.ZERO);
		}

		if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getStaxAmount() != null) {
			ctBackingBean
					.getManagedBean()
					.getContractVer()
					.getCtAmendment()
					.setNewStaxAmount(
							ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getStaxAmount());
		} else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewStaxAmount(BigDecimal.ZERO);
		}
	}

	// End fix - #33287
	
	//Start Fix - #33287
	public void onClauseChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()) {
			spToCheck = false;
		}

		if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()) {
			Calendar c = Calendar.getInstance();
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
			spToCheck = true;
		}
	//End Fix - #33287

	}
	//End Fix - #33287
	/**
	 * The method used to set the related amendment information
	 * when user extended the contract period.
	 */
	public void onExtensionPeriodChanged() {
		Calendar c = Calendar.getInstance();
		c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.DATE, 1);
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());

		//#26076 - start
		//c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.MONTH, ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod());
		c.add(Calendar.DATE, -1);
		//#26076 - end
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
		changeAmendmentEffDate();
	}

	/**
	 * The method used to set default value of additional contract amount
	 * when is additional contract amount is null
	 */
	public void changeAdditionalValue() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount() == null) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
		}
	}

	/**
	 * The method used to set the related amendment information
	 * when user changed the amendment effective date.
	 */
	public void changeAmendmentEffDate() {
		if (this.getManagedBean().getSelectedServiceItem() != null) {
			changeSelectedItemPriceAmendDate(this.getManagedBean().getSelectedServiceItem());
		}
		if (this.getManagedBean().getSelectedItem() != null ){
			if (this.getManagedBean().getSelectedItem().getCtItemPrices() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemPrices().size() > 0) {
				changeSelectedItemPriceAmendDate(this.getManagedBean().getSelectedItem());				  
			}
			if (this.getManagedBean().getSelectedItem().getCtItemCeilingQties() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemCeilingQties().size() > 0) {
				List <CtItemCeilingQty> ceilingQtyList = this.getManagedBean().getSelectedItem().getCtItemCeilingQties();
				for (int i = 0; i < ceilingQtyList.size(); i++) {
					if (ceilingQtyList.get(i).isChangeCeilQuantity()) {						   
						ceilingQtyList.get(i - 1).setExpDate(getPreviousDate().getTime());
						ceilingQtyList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
						if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
							ceilingQtyList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
						} else {
							ceilingQtyList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
						}
					}
				}
			}
			if (this.getManagedBean().getSelectedItem().getCtItemMaxQties() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemMaxQties().size() > 0) {
				changeSelectedItemMaxQtyAmendDate(this.getManagedBean().getSelectedItem());				  
			}
		}
	}
	
	/**
	 * To construct contract signer condition fields for opUserLov
	 * 
	 * @return String
	 */
	public String constructSignerConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("pmUserOrgs.pmUserRoles.pmRole.roleCode^");
		sb.append(RoleCode.CONTRACT_SIGNER.value());
		sb.append("|pmUserOrgs.pmUserRoles.pmRole.isLimitRequired^");
		sb.append(Boolean.TRUE);
		sb.append("|pmUserOrgs.pmUserRoles.approvalLimit^");
		sb.append(newOverallContractAmount());
		sb.append("^");
		sb.append(SearchOperator.OPERATOR_GREATER_EQUAL.value());
		sb.append("|pmUserOrgs.pmUserRoles.recordStatus^1");
		return sb.toString();
	}

	/**
	 * The method used to set the contract signer information
	 * when user select a signer from the selection list.
	 */
	public void contractSignerChange() {
		try {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			Long contractSignerId = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner().getUserId();
			if (contractSignerId != null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(contractSignerId);			   	
				UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
				PmUser pmUser = resDTO.getPmUser();
				if (pmUser != null) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractSigner(pmUser);
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setLoaSignerDesig(pmUser.getDesignation());
				}
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setLoaSignerDesig(null);
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
//  Start Fix - #33674 
	/**
	 * The method used to set the bank and account information
	 * when user select a bank from the selection list.
	 */
	public void changeAccountActionListener() {
		IChangeBankAccSO changeBankAccSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		try {
			String[] splittedBankDetails = splittedAccNoAndBankname();
			if (splittedBankDetails != null
					&& ((splittedBankDetails[0] != null && !splittedBankDetails[0].isEmpty()) && (splittedBankDetails[1] != null && !splittedBankDetails[1]
							.isEmpty()))) {
				String bankAccNo = splittedBankDetails[0];
				String bankname = splittedBankDetails[1];
				SmSupplierBank supplierBank = ctBackingBean.getManagedBean().getSupplierBankAccNo().get(bankAccNo);
				SmSupplierBank supplierBankname = ctBackingBean.getManagedBean().getSupplierBankName().get(bankname);

				changeBankAccSO = (IChangeBankAccSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IChangeBankAccSO.class);
				reqDTO = new ContractReqDTO();
				reqDTO.setBankAccNo(bankAccNo);
				reqDTO.setBankName(bankname);
				if (ctBackingBean.getManagedBean().getApplID() != null) {
					reqDTO.setApplID(ctBackingBean.getManagedBean().getApplID());
				}
				resDTO = changeBankAccSO.getBankDetails(reqDTO);
				if (resDTO.isSuccessful()) {
					if (supplierBank != null && supplierBankname != null) {
						if ((supplierBank.getAccountNo() != null && supplierBank.getAccountNo().equals(
								resDTO.getBankAccNO()))
								&& (supplierBankname.getPmFinancialOrg().getFinOrgName() != null && supplierBankname
										.getPmFinancialOrg().getFinOrgName().equals(resDTO.getBankName()))) {
							ctBackingBean.getManagedBean().getContractVer().getCtContractValue()
									.setBankName(resDTO.getBankName());
							ctBackingBean.getManagedBean().getContractVer().getCtContractValue()
									.setBankBranch(resDTO.getBankBranch());
							ctBackingBean
									.getManagedBean()
									.getContractVer()
									.getCtContractValue()
									.setBankAddress(
											CtAddressUtil.convertSmAddressToCtAddress(resDTO.getBankAddressId()));
						} 
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			changeBankAccSO = null;
			resDTO = null;
		}
	}
//  End Fix - #33674 

	/**
	 * The method used for validating the duplicate chairperson.
	 * A warning message will display on screen when more than one chairperson
	 * is added as committee.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void validateDuplicateChairperson(Long parameterId) throws Exception {
		if (parameterId == null || parameterId.intValue() == 0) {
			return;
		}
		int counter = 0;
		String parametercode = ParameterHelper.getInstance().getPmParameterById(parameterId).getParameterCode();
		if (parametercode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {  
				if (committeMember.getMemberRole() != null && committeMember.getMemberRole().getParameterId() != null
						&& committeMember.getMemberRoleId().longValue() == parameterId.longValue()) {
					// #26520 - to reset existing value saved before
					if(committeMember.getRepresentativeType() != null && committeMember.getRepresentativeType().getParameterId() != null) {
						committeMember.setRepresentativeType(null);
					}
					// #26520 - end
					counter++;
					if (counter == 2) {
						throw new EpException(MessageConst.ERROR_750008);
					}
				}else{
					if (parametercode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON) || parametercode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER))
						if(committeMember.getRepresentativeType() != null)
							committeMember.getRepresentativeType().setParameterId(null);
				}
			}
		}
	}

	/**
	 * The method used for validating the meeting attendance.
	 * A warning message will display on screen when the required
	 * committee doesn't not attended the meeting.
	 */
	private void validateMeetingDetails() {
		boolean secretaryRole = true;
		boolean chairpersonRole = true;
		boolean memberRole = true;
		boolean representativeJKR = true;
		boolean representativeBPK = true;
		boolean representativeSecretary = true;
		boolean representativeOfficer = true;
		boolean representativeDirector = true;
				
		//26753 - start
		List<CtCommitteeMember> ctCommitteeMembers = null;
		if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
				|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
//			ctCommitteeMembers = getManagedBean().getUpdatedCommitteeMembers();
			ctCommitteeMembers = getFilteredCommitteeMembers(); //#29403
		} else {
			ctCommitteeMembers = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers();
		}
		//26753 - end		
		
		
		
		for (CtCommitteeMember committeObj : ctCommitteeMembers) {//26753
			
			//#29403 start
			if(committeObj.getMemberRole().getParameterId()!=null && committeObj.getMemberRole().getParameterCode()==null){
				committeObj.setMemberRole(ParameterHelper.getInstance().getPmParameterById(committeObj.getMemberRole().getParameterId()));
			}else if(committeObj.getMemberRole().getParameterId()==null 
					&& committeObj.getMemberRole().getParameterCode()==null 
					&& committeObj.getMemberRoleId()!=null){
				committeObj.setMemberRole(ParameterHelper.getInstance().getPmParameterById(committeObj.getMemberRoleId()));
			}
			//29403 end
			
			if (committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
					ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
				chairpersonRole = false;
			} else if(committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
					ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				secretaryRole = false;
			} else if (committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
					ParameterConst.COMMITTEE_ROLE_MEMBER)){
				memberRole = false;
			}

			if (committeObj.getRepresentativeType() != null && committeObj.getRepresentativeType().getParameterCode() != null) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_SECRETARY_HEAD_DEPARTMENT)) {
						representativeSecretary = false;
					} 
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_BPK)) {
						representativeBPK = false;  
					}
				}
				
				//#26687 start
				
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SBH)) {
						representativeOfficer = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_ASSISTANT_DIRECTOR_SBH)) {
						representativeDirector = false;  
					} else if(committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SBH)) {
						representativeSecretary = false;  
					}
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if(committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst. COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SWK)) {
						representativeSecretary = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SWK )) {
						representativeOfficer = false;  
					} else if (committeObj.isAttended() &&committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR_DIRECTOR_SWK)) {
						representativeDirector = false;  
					}
				}
				
				//#26687 end
			}
		}    
		if (chairpersonRole) {
//			setTabValue("DCN");
			getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.chairperson.attendance", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		
		//#26687 start
		if (!getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
				&& !getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
			if (secretaryRole){
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.secretary.attendance", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});

			}
			if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)
					|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
				if (memberRole) {
//					setTabValue("DCN");
					getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.member.attendance", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		}
		
		//#26687 end
		
		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
			if (representativeJKR) {
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeSecretary) {
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.secretary", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
			if (representativeJKR) {
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeBPK) {
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.bpk", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		} 
		//#26687 start
		else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
			if(representativeOfficer && representativeDirector && representativeSecretary){
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750057);
			}
			else if((representativeSecretary && representativeOfficer) 
					|| (representativeSecretary && representativeDirector)
					|| (representativeOfficer && representativeDirector)){
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750058);
			}
		}else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
			if(representativeOfficer && representativeDirector && representativeSecretary){
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750057);
			}
			else if((representativeSecretary && representativeOfficer) 
					|| (representativeSecretary && representativeDirector)
					|| (representativeOfficer && representativeDirector)){
//				setTabValue("DCN");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750058);
			}
		}
		
		//#26687 end
	}

	/**
	 * The method used for validating the committee member details.
	 * A warning message will display on screen when the required
	 * committee doesn't not included in the meeting.
	 */
	private void validateCommiteeMemberDetails() {
		boolean secretaryRole = true;
		boolean chairpersonRole = true;
		boolean memberRole = true;
		boolean representativeJKR = true;
		boolean representativeBPK = true;
		boolean representativeSecretary = true;
		boolean representativeOfficer = true;
		boolean representativeDirector = true;
		
		//#29403 start
		int chairpersonCount = 0;
		//#29403 end
		
		
		//26753 - start
		List<CtCommitteeMember> ctCommitteeMembers = null;
		if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
			|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
//			ctCommitteeMembers = getManagedBean().getUpdatedCommitteeMembers();
			ctCommitteeMembers = getFilteredCommitteeMembers(); //#29403
		} else {
			ctCommitteeMembers = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers();
		}
		//26753 - end
		for (CtCommitteeMember committeMember : ctCommitteeMembers) {//26753
			if (committeMember.getMemberRole() != null 
					&& (committeMember.getMemberRoleId() != null || committeMember.getMemberRole().getParameterId() != null)) { //#29403
				String representativeCode = "";
				
				//#29403 start
				String parameterCode = "";
				if (committeMember.getMemberRoleId() != null){
					parameterCode = ParameterHelper.getInstance().getPmParameterById(committeMember.getMemberRoleId()).getParameterCode();
				}else{
					parameterCode = ParameterHelper.getInstance().getPmParameterById(committeMember.getMemberRole().getParameterId()).getParameterCode();
				}
				//#29403 end
				
				if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
					chairpersonCount++; //#29403
					chairpersonRole = false;
				} else if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
					secretaryRole = false;
				} else if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
					memberRole = false;
					if (!getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B) 
							&& !getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
						if (committeMember.getRepresentativeType() == null 
								|| committeMember.getRepresentativeType().getParameterId() == null) {
//							setTabValue("MET");
							getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
							
							throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage("lbl.ct.represent.type", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							representativeCode = ParameterHelper.getInstance().getPmParameterById(
									committeMember.getRepresentativeType().getParameterId()).getParameterCode();
						}
					}
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
					if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_SECRETARY_HEAD_DEPARTMENT)) {
						representativeSecretary = false;
					} 
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
					if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;  
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_BPK)) {
						representativeBPK = false;  
					}
				}
				
				//#26687 start

				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
					if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SBH)) {
						representativeOfficer = false;  
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_ASSISTANT_DIRECTOR_SBH)) {
						representativeDirector = false;  
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SBH)) {
						representativeSecretary = false;  
					}
				}
				
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (representativeCode.equalsIgnoreCase(ParameterConst. COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SWK)) {
						representativeSecretary = false;  
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SWK )) {
						representativeOfficer = false;  
					} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR_DIRECTOR_SWK)) {
						representativeDirector = false;  
					}
				}
				//#26687 end
				
			} else if (committeMember.getMemberRole() == null || committeMember.getMemberRoleId() == null) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750025); //#29403
			}
		}
		
		if (chairpersonRole) {
//			setTabValue("MET");
			getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.chairperson", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		
		//#29403 start
		if (chairpersonCount>1){
//			setTabValue("MET");
			getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			
			throw new EpException(MessageConst.ERROR_750008);
		}
		//#29403 end
		
		//#26687 start
		if (!getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)
				&& !getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
			if (secretaryRole) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.secretary", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			if(memberRole) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.member", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}
		//#26687 end

		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
			if (representativeJKR) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeSecretary) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.secretary", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
			if (representativeJKR) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeBPK) {
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_100022,
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.bpk", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		} 
		
		//#26687 start
		else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {

			if(representativeOfficer && representativeDirector && representativeSecretary){
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750059);
			}
			else if((representativeSecretary && representativeOfficer) 
					|| (representativeSecretary && representativeDirector)
					|| (representativeOfficer && representativeDirector)){
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750060);
			}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {

			if(representativeOfficer && representativeDirector && representativeSecretary){
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750055);
			}
			else if((representativeSecretary && representativeOfficer) 
					|| (representativeSecretary && representativeDirector)
					|| (representativeOfficer && representativeDirector)){
//				setTabValue("MET");
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.meeting", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				
				throw new EpException(MessageConst.ERROR_750056);
			}
		}
		//#26687 end
		
	}
//     Start Fix - #33674 
	/**
	 * The method used for generating a list of available bank and account
	 * for supplier as selection.
	 * 
	 * @return populateBankAccountList  the selection list of bank and account
	 */
	public List<SelectItem> getPopulateBankAccountList() {
		if (populateBankAccountList == null) {
			if (this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
					&& this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				populateBankAccountList = new ArrayList<SelectItem>();
				for (SmSupplierBank obj : ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()) {
					String account = obj.getAccountNo();
					if (obj.getPmFinancialOrg() != null) {
						account = account + ContractConst.SPACE_SLASH + obj.getPmFinancialOrg().getFinOrgName();
					}
					populateBankAccountList.add(new SelectItem(account,account));
					if(!ctBackingBean.getManagedBean().getSupplierBankAccNo().containsKey(obj.getAccountNo())){
						ctBackingBean.getManagedBean().getSupplierBankAccNo().put(obj.getAccountNo(), obj);
					}
					if(!ctBackingBean.getManagedBean().getSupplierBankName().containsKey(obj.getPmFinancialOrg().getFinOrgName())){
						ctBackingBean.getManagedBean().getSupplierBankName().put(obj.getPmFinancialOrg().getFinOrgName(), obj);
					}			
				}    
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() == 1) {
					ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAccountNo(
							ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().get(0).getAccountNo());
					changeAccountActionListener();
				}
				ctBackingBean.getManagedBean().setApplID(ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getApplId());
			}
		}
		return populateBankAccountList;
	}
//  End Fix - #33674 
	/**
	 * The method used for generating a list of available display type
	 * of product or service item as selection.
	 * 
	 * @param itemType	product or service item
	 * @return the selection list of display type
	 */
	public SelectItem[] getItemDisplayTypeDropdownList(String itemType) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> itemDisplayTypeList = new ArrayList<String> ();

		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
		}

		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			if (itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtAgencies() != null
						&& ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size() > 1) {
					itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
				}
				itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
			} else if (itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_QTY);				
			}
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						itemDisplayTypeList));

		return super.getDropdownCodeValuesBySearchBean(ParameterTypeConst.ITEM_DISPLAY_TYPE, searchBean);
	}

	/**
	 * The method used for generating a list of available secretary 
	 * as selection.
	 * 
	 * @return the selection list of secretary
	 */
	public SelectItem[]  getSecretaryDropdownList() {
		if (getManagedBean().getSecretaryList() == null ) {
			SelectItem[] items = null;
			ICommitteSO committeSO = null;
			ISearchResultBean<PmUser> userResultList = null;
			ISearchResultBean<PmUser> userListDO = null; //Bug #29740
			try {

				int i = 1;
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setDocId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId());
				committeReqDTO.setDocType(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
				//Bug #25326
				committeReqDTO.setOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				//end Bug #25326
				
				//Bug #29740 - Add DO PB
				CommitteReqDTO commReqDO = new CommitteReqDTO();
				commReqDO.setDocId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getContractVerId());
				commReqDO.setDocType(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode());
				//commReqDO.setOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()); //Bug #33857
								
				if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					committeReqDTO.setRoleCode(RoleCode.FPB_SECRETARY.value());
					//Bug #29740 - Add DO PB
					commReqDO.setRoleCode(RoleCode.PPB_SECRETARY.value());
					userListDO = committeSO.retrieveUserListByCommType(commReqDO).getUserResultBean();
					
				} else {
					committeReqDTO.setRoleCode(getManagedBean().getCommitteeType()+"_SECRETARY");
				}
				userResultList = committeSO.retrieveUserListByCommType(committeReqDTO).getUserResultBean();			

				if (userResultList != null && userResultList.getResultList() != null) {
					//Bug #29740
					if(userListDO != null && userListDO.getResultList() != null) {
						items = new SelectItem[userResultList.getResultList().size() + userListDO.getResultList().size() + 1];
					} else {
						items = new SelectItem[userResultList.getResultList().size() + 1];
					}

					items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

					for (PmUser user : userResultList.getResultList()) {
						items[i++] = new SelectItem(user.getUserId(), user.getUserName());
					}
					
					//Bug #29740
					if(userListDO != null && userListDO.getResultList() != null) {
						for (PmUser userDO : userListDO.getResultList()) {
							items[i++] = new SelectItem(userDO.getUserId(), userDO.getUserName());
						}
					}
					//Bug #29740 - End
					
					// Default the secretary if selection list only have one secretary
					if (items != null && items.length == 2) {
						getManagedBean().setSecretaryId((Long)items[1].getValue());
					}
					getManagedBean().setSecretaryList(items);
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			}
		}
		return getManagedBean().getSecretaryList();
	}	

	/**
	 * The method used for generating a list of available financial institution
	 * for performance bond as selection.
	 * 
	 * @return the selection list of financial institution
	 */
	public SelectItem[] retrieveFinancialOrgList() {
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO = null;
		FinancialOrgReqDTO reqDTO = null;
		FinancialOrgResDTO resDTO = null;

		try {
			int i = 1;
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);

			items = new SelectItem[resDTO.getFinancialOrgList().size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmFinancialOrg pmFinancialOrg : resDTO.getFinancialOrgList()) {
				items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());   
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}   	
		return items;
	}

	/**
	 * The method used for generating a list of available meeting status
	 * as selection.
	 * 
	 * @return the selection list of meeting status
	 */
	public SelectItem[] getMeetingStatusDropdown() {
		SelectItem[] items = null;     
		IStatusSO statusSO = null;
		StatusResDTO resDTO = null;
		StatusReqDTO reqDTO = null;
		try {
			int i = 1;
			statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("SC");
			reqDTO.setDocType("MS");
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			resDTO = statusSO.retrieveStatusDescList(reqDTO);
			items = new SelectItem[resDTO.getStatusDescList().size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmStatusDesc statusDesc : resDTO.getStatusDescList()) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}finally{
			resDTO = null;
			statusSO = null;
		}
		return items;
	}

	/**
	 * The method used for generating a list of available representative role
	 * as selection.
	 * 
	 * @return the selection list of representative role
	 */
	public SelectItem[] getRepresentativeDropdown() {		
		//26753 - start
		if(ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
			|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { //#26688
			if(getManagedBean().isUpdateCommittee()) {
				for (CtCommitteeMember committeeMember : getManagedBean().getUpdatedCommitteeMembers()) {
					if (committeeMember.getRepresentativeType() == null) {
						committeeMember.setRepresentativeType(new PmParameter());
					}
				}
			}			
		} 
		//26753 - end
		for (CtCommitteeMember committeeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
			if (committeeMember.getRepresentativeType() == null) {
				committeeMember.setRepresentativeType(new PmParameter());
			}
		}
		String representativeRole ="";
		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD)) {
			representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_PPR;
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
			representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH;
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
			representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK;
		} 
		return getDropdownValues(representativeRole);
	}

	/**
	 * The method used for generating a list of available role type
	 * as selection.
	 * 
	 * @return the selection list of role type
	 */
	public SelectItem[] getRoleTypeDropdown(CtCommitteeMember committeeMember) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> committeRole = new ArrayList<String>();
		if (committeeMember.isPnp() && allowChairpersonRole(committeeMember)) {
			committeRole.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		}
		committeRole.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), committeRole));
		return getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
	}

	/**
	 * The method used for identifying whether the committee user
	 * is allow to be chairperson
	 * 
	 * @return the flag to allow chairperson role
	 */
	private boolean allowChairpersonRole(CtCommitteeMember committeeMember) {
		boolean allowChairpersonRole = false;
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		//#26751
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(committeeMember.getPmUser().getUserId());
		
		UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);	
		/*#25892-basri*/
		userReqDTO.setLanguageCode("EN");
		userResDTO.setRoleDescList(userSO.retrieveUserRoleDescListByUserId(userReqDTO).getRoleDescList());
		userResDTO.setPmUser(userSO.retrieveUserByUserId(userReqDTO).getPmUser());
		/*end*/
		
		PmOrgProfile orgProfile = null;

		if (userResDTO.getUserOrgList() != null && userResDTO.getUserOrgList().size() > 0) {
			orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			
			//Bug #19918 Unable to assign Chairperson QCA/QCB from other PTJ within same ministry
			Long orgProfileId = orgProfile.getOrgProfileId();
			Long ctOwnerOrgProfileId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId();
			PmOrgProfile contractMinistry = retrieveMinistryOrgProfile(ctOwnerOrgProfileId);
			PmOrgProfile committeeMinistry = retrieveMinistryOrgProfile(orgProfileId);
			
			log.info(":::allowChairpersonRole() orgProfileId: "+ orgProfileId);
			
			if (contractMinistry != null && committeeMinistry != null
					&& contractMinistry.getOrgProfileId().longValue() == committeeMinistry.getOrgProfileId().longValue()) {	
				log.info(":::allowChairpersonRole() "+ orgProfileId +". contract ministryOrgProfileId: "+ contractMinistry.getOrgProfileId());
				log.info(":::allowChairpersonRole() "+ orgProfileId +". committee ministryOrgProfileId: "+ committeeMinistry.getOrgProfileId());
				
				allowChairpersonRole = true;
			}
			
			
			/*#25892-basri*/
			/*code for allow bpk chairperson*/
			if(userResDTO.getPmUser().getOrgTypeCode().getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_BPK)){
				for(PmRoleDesc pm : userResDTO.getRoleDescList())
				{
					if(pm.getPmRole().getRoleCode().equalsIgnoreCase(RoleCode.PBB_CHAIRPERSON.value()) 
							|| pm.getPmRole().getRoleCode().equalsIgnoreCase(RoleCode.PBA_CHAIRPERSON.value())
							|| pm.getPmRole().getRoleCode().equalsIgnoreCase(RoleCode.PPB_CHAIRPERSON.value())){
						allowChairpersonRole = true;
						continue;
						
					}
				}
			}
			
			/*end*/
			
			log.info(":::allowChairpersonRole() "+ orgProfileId +". committee type: "+ getManagedBean().getCommitteeType());

			// QCA/QCB, need to be same PTJ
			if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)
					|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)) {
				log.info(":::allowChairpersonRole() "+ orgProfileId +". contract ownerOrgProfileId: "+ ctOwnerOrgProfileId);
				
				if (orgProfileId.longValue() == ctOwnerOrgProfileId.longValue()) {
					allowChairpersonRole = true;
				} else {
					allowChairpersonRole = false;
				}
			}
			
			//Bug #26520 - start
			if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
					|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)){
				//#26751 - start
				PmOrgProfile pmOrgProfile = new PmOrgProfile();
				OrgProfileReqDTO mofReqDTO = new OrgProfileReqDTO();
				
				if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)){
					mofReqDTO.setOrgCode("24010201");//LPPS Sabah
				} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					mofReqDTO.setOrgCode("24010301");//LPPS Sarawak
				}

				OrgProfileResDTO mofResDTO = orgProfileSO.retrieveOrgProfileByCode(mofReqDTO);
				pmOrgProfile = mofResDTO.getPmOrgProfile();
				
				log.info(":::allowChairpersonRole() user profile id: "+ orgProfileId.longValue() +". committee profile id: "+ pmOrgProfile.getOrgProfileId());
				// FSB/FSW - open to MOF Procurement Board
				if(orgProfileId.longValue() == pmOrgProfile.getOrgProfileId().longValue()) {
					allowChairpersonRole = true;
				} else {
					allowChairpersonRole = false;
				}
				//#26751 -end
			}
			//Bug #26520 - end
			
//			} else {	// Others same Ministry
//				PmOrgProfile contractMinistry = retrieveMinistryOrgProfile(
//						ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
//				PmOrgProfile committeeMinistry = retrieveMinistryOrgProfile(orgProfile.getOrgProfileId());
//				if (contractMinistry != null && committeeMinistry != null
//						&& contractMinistry.getOrgProfileId() == committeeMinistry.getOrgProfileId()) {
//					allowChairpersonRole = true;
//				}
//			}
			//end of Bug #19918
		}
		
		return allowChairpersonRole;
	}

	/**
	 * The method used for initializing the remarks of contract amendment.
	 */
	public void initializeRemarks() {
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getSupplierRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getSettingRemarks());
	}

	/**
	 * The method used for initializing the remarks of contract amendment.
	 * This method will create new remark object for every single new 
	 * process, else will display all remarks.
	 * 
	 * @param remarkList  the list of remarks to display. If the remarkList is null, 
	 *                    initiate a new remark
	 */
	private void initiateRemark(List<CtRemark> remarks) {

		boolean ctWorkflowStatusMatch = false;
		if (ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus() != null) {
			if (remarks.size()>0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId() 
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}		

		if (remarks.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}

	/**
	 * The method used to get the Surat Pengesahan Setuju Terima Pembekal
	 * that to be displayed on screen.
	 * 
	 * @throws FileNotFoundException  if an file cannot be found
	 * @throws Exception  if an exception occurred
	 * @see    FileNotFoundException, Exception
	 */	
	public String getLoaStreamFilePath() throws FileNotFoundException, Exception {

		String filePath = null;
		if(getManagedBean().getCtLetterOfAcceptance() != null){
			filePath = getManagedBean().getCtLetterOfAcceptance().get(0).getLoaAttachments().getFilePath();
		}
		if (filePath != null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
			filePath = URLEncoder.encode(filePath, "UTF-8"); //#33817
		}
		return filePath;
	}

	/**
	 * The method used to set the file index of attachment list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}

	/**
	 * The method used to delete the meeting decision attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteCommitteAttachmentAction(int index) {
		CtAttachment attachment = ctBackingBean.getManagedBean().getContractVer().getCommitteDecisionAttachements().get(index);
		if(attachment.getAttachmentId() != null){
			ctBackingBean.getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		ctBackingBean.getManagedBean().getContractVer().getCommitteDecisionAttachements().remove(index);
	}

	/**
	 * The method used to upload loa content file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */	
	public void UploadLetterOfAcceptance(FileUploadEvent event) throws Exception {
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		UploadedFile item = event.getFile();
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}

		String fileFormat= attachmentFormat.format(item.getFileName(), null);
		filePath = filePath + "/" + fileFormat;
		CtAttachment attachment = new CtAttachment();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setFileName(item.getFileName());
		
		attachment.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		if(getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME))){
			attachment.setSectionId(ContractConst.ATTACHMENT_LOA_LETTER_SST);
			getManagedBean().getCtLetterOfAcceptance().get(0).setLoaAttachments(attachment);
		} else if(getManagedBean().getAckFA() != null
				&& getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)){
			attachment.setSectionId(ContractConst.ATTACHMENT_LOA_LETTER_RECEIPT);
			getManagedBean().getCtLetterOfAcceptance().get(1).setLoaAttachments(attachment);
		}
	}

	/**
	 * The method used to delete loa attachment from the datatable list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteLoaAttachment(int index) {
		CtAttachment attachment = getManagedBean().getCtLetterOfAcceptance().get(index).getLoaAttachments();
		if(attachment.getAttachmentId() != null){
			ctBackingBean.getManagedBean().getDeleteAttachmentList().add(attachment);
		}
		getManagedBean().getCtLetterOfAcceptance().get(index).setLoaAttachments(new CtAttachment());
	}

	/**
	 * The method used to upload meeting decision file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void committeeFileUpload(FileUploadEvent event) throws Exception {
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();

		UploadedFile item = event.getFile();

		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);

		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		fileOuputStream.write(item.getContents());
		fileOuputStream.close();

		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(getUserIdSessionFromLoginId()); //#31849 add log key
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		ctBackingBean.getManagedBean().getContractVer().getCommitteDecisionAttachements().add(attachment);
	}

	/**
	 * The method used to download the sample bond letter
	 * as pdf file.
	 */
	public void downloadSampleBondLetter() {
		FileOutputStream fileOutputStream = null;
		
		try {
			String filePath = getManagedBean().getFileDirectory();		      
			File file = new File(filePath);
			file.mkdirs();

			String fileName = "Sample Bond Letter.pdf";
			filePath = getManagedBean().getFileDirectory() +  "/" + fileName;

			File pdfFile = new File(filePath);
			if (!pdfFile.exists()) {
				VelocityContext context = new VelocityContext();
				int type = 0;				
				context.put("type", type);
				context.put("contractNo", "...........................");
				context.put("supplierName", "...........................");
				context.put("sourcingTitle", "...........................");
				context.put("bondAmount", "..............................");
				context.put("bondAmountWord", "..................................................");
				context.put("expiryPeriod", "..........................");	

				fileOutputStream = new FileOutputStream(filePath);
				DocumentGenerator.getInstance().generatePdfFromVelocity(
						SourcingConstants.LOA_BOND_LETTER, context,
						fileOutputStream);
			}			
			commonDownload(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);			
		} catch (FileNotFoundException e) {
			log.error(e.toString());
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}

	/**
	 * The method used to download the sample of LOA content file
	 */
	public void downloadLOAContentFile(String docDefinitionKey) throws Exception {

		AttachmentFormat attachmentFormat = null;
		FileOutputStream fileOutputStream = null;
		try {
			String fileName = (docDefinitionKey.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE))
					? "SuratBerkaitanKontrakTambahan.docx" : "PengakuanPenerimaanSuratBerkaitanKontrakTambahan.pdf";
			String content = null;
			String filePath = getManagedBean().getFileDirectory();
			File file = new File(filePath);
			file.mkdirs();

			attachmentFormat = new AttachmentDateFormat();
			filePath = filePath + attachmentFormat.format(fileName, null);

			content = getContent(docDefinitionKey);
			if (content != null) {
				if (docDefinitionKey.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE)) {
					//DocumentGenerator.getInstance().generateDocxtoHtml(filePath, content);					
					// Download orginal template in doc format
					StringBuilder templateFilePath = new StringBuilder();
					
					templateFilePath.append(EpPropertiesUtils.getInstance().getProperty(
							EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR));
					templateFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
					templateFilePath.append(ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
					templateFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
					templateFilePath.append(ContractConst.DOCNAME_SUPPLEMENTAL_CONTRACT_LETTER);

					commonDownload(templateFilePath.toString(), ContractConst.DOCNAME_SUPPLEMENTAL_CONTRACT_LETTER);
				} else {
					fileOutputStream = new FileOutputStream(filePath);
					DocumentGenerator.getInstance().generatePdf(fileOutputStream, content);
					commonDownload(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
				}			
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			attachmentFormat = null;
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}

	/**
	 * The method used to download the uploaded Supplemental Contract Letter
	 */
	public void downloadSupplementalLetter(CtAttachment ctAttchment) {
		try {
			commonDownload(ctAttchment.getFilePath(), ctAttchment.getFileName());

			// Supplier only allow to acknowledge after letter is downloaded 
			if (getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)) {
				BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
				if (bb.getManagedBean().getClaim()) {
					getManagedBean().setAllowAcknowledge(true);
				} else {
					getManagedBean().setAllowAcknowledge(false);
				}
			} else {
				getManagedBean().setAllowAcknowledge(false);
			}
		} catch (IOException e) {
			log.error(e.toString());
		}
	}

	/**
	 * The method used to retrieve the Acknowledgment Receipt content
	 */
	public String getAcknowledgmentReceiptContent(boolean isCompleted) throws Exception {
		String filePath = getManagedBean().getFileDirectory();		      
		File file = new File(filePath);
		file.mkdirs();
		FileOutputStream fileOutputStream = null;
		String fileName = "PengakuanPenerimaanSuratBerkaitanKontrakTambahan.pdf";
		try {
			if (isCompleted) {
				AttachmentDateFormat attachmentFormat = new AttachmentDateFormat();
				filePath = getManagedBean().getFileDirectory() +  "/" + attachmentFormat.format(fileName, null);
			} else {
				filePath = getManagedBean().getFileDirectory() +  "/" + fileName;
			}
			String content = null;	
			content = getContent(ParameterConst.DOCUMENT_DEFINITION_KEY_ACK_OF_LOA_RECEIPT);
			if (content != null) {
				fileOutputStream = new FileOutputStream(filePath);
				DocumentGenerator.getInstance().generatePdf(
						fileOutputStream, content);
			}
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
		return filePath;
	}

	/**
	 * The method used for generating the content of loa reports
	 * that defined from template.
	 * 
	 * @param docDefinitionKey  the defination key for different report. Different key will have different template.
	 * @return the complete content of the template
	 * @throws Exception        if an exception occurred
	 * @see    Exception
	 */	
	private String getContent(String docDefinitionKey) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat percentageDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat amountNoCommaDF = new DecimalFormat("#.00");
		String  sclRefNo =  EntityHelper.getStringNestedProperty( ctBackingBean.getManagedBean(), "contractVer.ctAmendment.sclRefNo"); //Fix - #36708
		// Supplemental Contract Letter
		if (ParameterConst.DOCUMENT_DEFINITION_KEY_LETTER_OF_ACCEPTANCE.equalsIgnoreCase(docDefinitionKey)) {
			FacesScopes fs = new FacesScopes();
			String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
			vmFile = ContractConst.DOCPATH_CT_LOA_AGREEMENT_LETTER;
			ScLoa scLoa = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa();
			ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
			context = new VelocityContext();
			PmOrgProfile pmOrgProfile = ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile();

			context.put("ptjName", pmOrgProfile.getOrgName());
			String jabtanName = retrieveJabatanByOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			StringBuffer  ministryJabatan = new StringBuffer();
			if(!jabtanName.isEmpty()) {
				ministryJabatan.append(jabtanName);
				ministryJabatan.append(" / ");
			}

			PmOrgProfile ministryOrgProfile = retrieveMinistryOrgProfile(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			if(ministryOrgProfile!=null) {
				ministryJabatan.append(ministryOrgProfile.getOrgName());
			}				
			context.put("ministryJabatanName", ministryJabatan );

			context.put("hostUrl", hostUrl);
			String ptjAddress = "";
			String phoneNo = "";
			String faxNo = "";
			if (ctBackingBean.getManagedBean().getContractVer().getOwnerOrgAddress() != null) {
				ptjAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getOwnerOrgAddress());
				ptjAddress = ptjAddress.replaceAll("\r\n", "<br />");
				ptjAddress = ptjAddress.replaceAll("&", "&#38;");
				phoneNo = ctBackingBean.getManagedBean().getContractVer().getOwnerOrgAddress().getPhoneNoFull();
				faxNo = ctBackingBean.getManagedBean().getContractVer().getOwnerOrgAddress().getFaxNoFull();
			}
			context.put("ptjAddress", ptjAddress);
			context.put("phoneNo", phoneNo);
			context.put("faxNo", faxNo);
			context.put("fileNo", sclRefNo + "-" + scLoa.getFileExtNo()); //Fix - #36708
			context.put("loaNo", scLoa.getLoaNo());

			String issueDate = "";
			if (scLoa.getIssueDate() != null) {
				issueDate = dateSDF.format(scLoa.getIssueDate());
			}
			context.put("issueDate", issueDate);
			context.put("supplierName", scLoiLoa.getSupplierName());


			String supplierAddress = "";
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
				supplierAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
				supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
				supplierAddress = supplierAddress.replaceAll("&", "&#38;");
			}
			context.put("supplierAddress", supplierAddress);			
			context.put("contractNo",ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			context.put("contractName", ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractName());

			String procurementMode = getParameterDesc(ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode());
			context.put("procurementMode", procurementMode);

			if (scLoa.getBondAmt() != null && scLoa.isBondReq()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
							ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				context.put("bondAmt200k", true);

				String bondAmt = "";
				if (scLoa.getBondAmt() != null) {
					bondAmt = amountDF.format(scLoa.getBondAmt());
				}
				context.put("bondAmt", bondAmt);
				context.put("bondAmtText", scLoa.getBondAmt() != null && scLoa.getBondAmt().doubleValue()!=0 ? NumberWordUtil.convert(amountNoCommaDF.format(scLoa.getBondAmt()) ) : ""); 

				String bondPercentage = "";
				if (scLoa.getBondPercentage() != null) {
					bondPercentage = percentageDF.format(scLoa.getBondPercentage());
				}
				context.put("bondPercentage", bondPercentage);
				Date bondExpDate = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate();
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate() != null) {
					bondExpDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate();
				}
				Calendar bondDate = Calendar.getInstance();
				bondDate.setTime(bondExpDate);
				bondDate.add(Calendar.YEAR, 1);
				context.put("bondExpDate", dateSDF.format(bondDate.getTime())); 

				String bondResDueDays = "30";
				context.put("bondResDueDays", bondResDueDays);
			} else {
				context.put("bondAmt200k", false);
			}

			context.put("contractWithGst", ctBackingBean.isLoaContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate()));
			String gstRegNo = "TB";
			String gstEffDate = "TB";
			String gstDeclareDate = "TB";
			BigDecimal overallAmt = scLoa.getLoaAmt();

			if (ctBackingBean.isLoaContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				context.put("contractAmt", amountDF.format(overallAmt));
				context.put("contractAmtTxt", (scLoa.getLoaAmt() != null && scLoa.getLoaAmt().doubleValue() != 0) ? NumberWordUtil.convert(amountNoCommaDF.format(overallAmt)) : ""); 

				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstRegNo() != null)
					gstRegNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstRegNo();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate() != null)
					gstEffDate = df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate());
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstDeclaredDate() != null)
					gstDeclareDate = df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstDeclaredDate());

				if (scLoa.getGstAmt() != null) {
					context.put("gstAmt", amountDF.format(scLoa.getGstAmt()));
					context.put("gstAmtTxt", scLoa.getGstAmt().doubleValue() != 0 ? NumberWordUtil.convert(amountNoCommaDF.format(scLoa.getGstAmt())) : ""); 
					overallAmt = scLoa.getLoaAmt().add(scLoa.getGstAmt());
				} else {
					context.put("gstAmt", "TB");
					context.put("gstAmtTxt", "TB");
				}
			}

			context.put("gstRegNo", gstRegNo);
			context.put("gstEffDate", gstEffDate);
			context.put("gstDeclareDate", gstDeclareDate);
			context.put("overallContractValue", amountDF.format(overallAmt));
			context.put("overallContractValueTxt", (scLoa.getLoaAmt() != null && scLoa.getLoaAmt().doubleValue() != 0) ? NumberWordUtil.convert(amountNoCommaDF.format(overallAmt)) : ""); 


			String contractEffDate = "";
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() != null) {
				contractEffDate = dateSDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
			}
			context.put("contractEffDate", contractEffDate);
			if (scLoa.getContractSigner() != null) {
				context.put("contractSigner", scLoa.getContractSigner().getUserName());
				String loaSignerDesig = scLoa.getLoaSignerDesig();
				if(StringUtils.isNotEmpty(loaSignerDesig) && loaSignerDesig.length()>86){
					loaSignerDesig = splitLongLengthString(loaSignerDesig, 86);
				}
				context.put("contractSignerDesignation", loaSignerDesig);
			}

			String ackDate = "";
			String bankAccNo = "";
			String bankName = "";
			String bankBranch = "";
			String bankAddress = "";				
			String supplierUserName = "";
			String supplierUserID = "";
			String supplierUserDesignation = "";
			String supplierTelNo = "";
			String supplierFaxNo = "";
			String supplierMobileNo = "";
			String supplierEmail = "";
			String supplierCompName = "";
			String supplierCompAddress = "";

			if (ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT.equals(
					ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) {
				ackDate = dateSDF.format(new Date());	
				// Bank account details.
				bankAccNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
				if (bankAccNo == null) {
					bankAccNo = "";
				}
				bankName = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName();
				if (bankName == null) {
					bankName = "";
				}
				bankBranch = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankBranch();
				if (bankBranch == null) {
					bankBranch = "";
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress() != null) {
					bankAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress());
					bankAddress = bankAddress.replaceAll("\r\n", "<br />");
					bankAddress = bankAddress.replaceAll("&", "&#38;");
				}
				// Supplier User
				supplierUserName = getFacesScopes().getUserSessionBean().getUserName();
				supplierUserID = getFacesScopes().getUserSessionBean().getIdentificationNo();
				supplierUserDesignation = getFacesScopes().getUserSessionBean().getDesignation() != null
						? getFacesScopes().getUserSessionBean().getDesignation(): "";
						supplierTelNo = getFacesScopes().getUserSessionBean().getPhoneNo() != null 
								? getFacesScopes().getUserSessionBean().getPhoneNo() : ""; 
								supplierFaxNo = getFacesScopes().getUserSessionBean().getFaxNo() != null
										? getFacesScopes().getUserSessionBean().getFaxNo() : "";
										supplierMobileNo = getFacesScopes().getUserSessionBean().getMobileNo() != null
												? getFacesScopes().getUserSessionBean().getMobileNo() : "";
												supplierEmail = getFacesScopes().getUserSessionBean().getEmail() != null
														? getFacesScopes().getUserSessionBean().getEmail() : "";
														supplierCompName = scLoiLoa.getSupplierName();
														supplierCompAddress = supplierAddress;
			} else if (ContractStatusConst.FA_PENDING_VERIFICATION.equals(
					ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) {
				ackDate = dateSDF.format(scLoa.getAckDate());
				// Bank account details.
				bankAccNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
				if (bankAccNo == null) {
					bankAccNo = "";
				}
				bankName = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName();
				if (bankName == null) {
					bankName = "";
				}
				bankBranch = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankBranch();
				if (bankBranch == null) {
					bankBranch = "";
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress() != null) {
					bankAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress());
					bankAddress = bankAddress.replaceAll("\r\n", "<br />");
					bankAddress = bankAddress.replaceAll("&", "&#38;");
				}
				// Supplier User
				supplierUserName = scLoa.getAckBy().getUserName();
				supplierUserID = scLoa.getAckBy().getIdentificationNo();
				supplierUserDesignation = scLoa.getAckBy().getDesignation() != null ? scLoa.getAckBy().getDesignation() : "";
				supplierTelNo = scLoa.getAckBy().getPhoneNo() != null ? scLoa.getAckBy().getPhoneNo() : "";
				supplierFaxNo = scLoa.getAckBy().getFaxNo() != null ? scLoa.getAckBy().getFaxNo() : "";
				supplierMobileNo = scLoa.getAckBy().getMobileNo() != null ? scLoa.getAckBy().getMobileNo() : "";
				supplierEmail = scLoa.getAckBy().getEmail() != null ? scLoa.getAckBy().getEmail() : "";
				supplierCompName = scLoiLoa.getSupplierName();
				supplierCompAddress = supplierAddress;					

			}
			context.put("ackDate", ackDate);
			context.put("bankAccNo", bankAccNo);
			context.put("bankName", bankName.replaceAll("&", "&#38;"));
			context.put("bankBranch", bankBranch);
			context.put("bankAddress", bankAddress);
			// Supplier User
			context.put("supplierUserName", supplierUserName);
			context.put("supplierUserID", supplierUserID);
			context.put("supplierUserDesignation", supplierUserDesignation);
			context.put("supplierTelNo", supplierTelNo);
			context.put("supplierFaxNo", supplierFaxNo);
			context.put("supplierMobileNo", supplierMobileNo);
			context.put("supplierEmail", supplierEmail);
			context.put("supplierCompName", supplierCompName);
			context.put("supplierCompAddress", supplierCompAddress);
			// Witness
			context.put("witnessName", "");
			context.put("witnessID", "");
			context.put("witnessDesignation", "");
			context.put("witnessAddress", "");
			context.put("witnessTelNo", "");
			context.put("witnessFaxNo", "");
			context.put("witnessMobileNo", "");
			context.put("witnessEmail", "");

		}
		// Acknowledgment Receipt of the Supplemental Contract Letter
		else if (ParameterConst.DOCUMENT_DEFINITION_KEY_ACK_OF_LOA_RECEIPT.equalsIgnoreCase(docDefinitionKey)) {
			context = new VelocityContext();	
			vmFile = ContractConst.DOCPATH_CT_LOA_RECEIPT;
			context.put("supplierName", "");
			context.put("supplierIcNo", "");
			context.put("supplierAckDate", "");
			if (getManagedBean().getTaskName() != null) {
				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
						&&  !getManagedBean().isReadOnly()) {
					context.put("supplierName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("supplierIcNo", getFacesScopes().getUserSessionBean().getIdentificationNo());
					context.put("supplierAckDate", dateSDF.format(new Date()));
				} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
					context.put("supplierName", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy().getUserName());
					context.put("supplierIcNo", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy().getIdentificationNo());
					context.put("supplierAckDate", dateSDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckDate()));
				}
			}
			context.put("companyName", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			String supplierAddress = "";

			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
				supplierAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
				supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
				supplierAddress = supplierAddress.replaceAll("&", "&#38;");
			}
			context.put("supplierAddress", supplierAddress);
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getGovtSignDate() != null) {
				context.put("govSignDate", dateSDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getGovtSignDate()));
			} else {
				context.put("govSignDate", "_______________");
			}
			if (!StringUtils.isEmpty(sclRefNo)) { //Fix - #36708
				context.put("agencyRefNo", sclRefNo);
			} else {
				context.put("agencyRefNo", "_______________");
			}			
		}

		if (vmFile != null && context != null) {
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
		}
		return content;
	}

	private static String splitLongLengthString(String text, int split){
		StringBuilder sb = new StringBuilder();
		String[] array = text.split("(?<=\\G.{" + split + "})");
		int counter=1;
		for (String string : array) {
			sb.append(string);
			if(array.length != counter){
				sb.append("<br />");
				++counter;
			}
		}
		return sb.toString();
	}

	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
	 */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.amendment", 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}

	/**
	 * The method used to retrieve yesterday date from system.
	 *  
	 * @return  yesterday date
	 */
	private Calendar getPreviousDate() {
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		return previousDate;
	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {
		String userRole = null;	
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_MOF_RESULT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_CANCEL_MEETING_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_CANCEL_MEETING_DECISION_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_REJECT_LOA_HT_NAME)) {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();			
			}
		} else {
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_MINISTRY)
						&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
					userRole = RoleCode.CT_APPROVER.value();
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_CENTRAL)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
					userRole = RoleCode.BPK_CC_APPROVER.value();
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
					userRole = RoleCode.MOF_SUPPLIER_USER.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
					userRole = RoleCode.MOF_SUPPLIER_ADMIN.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
					userRole = RoleCode.BASIC_SUPPLIER_USER.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
					userRole = RoleCode.BASIC_SUPPLIER_ADMIN.value();
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
					if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {
						userRole = RoleCode.DESK_OFFICER.value();
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_DESK_OFFICER";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
				// #25956 - Start - Fixing for _Secretary Role
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)) {
					if (getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())) {
						userRole = RoleCode.QCA_SECRETARY.value();
					} else if (getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())) {
						userRole = RoleCode.QCB_SECRETARY.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_SECRETARY";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}

				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD)
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(
								ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
					if (getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())) {
						userRole = RoleCode.PBB_SECRETARY.value();
					} else if (getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())) {
						userRole = RoleCode.PPB_SECRETARY.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_SECRETARY";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
				// #25956 - End - Fixing for _Secretary Role	
					
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_SECRETARY.value()) || getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())) { //#29740
						userRole = RoleCode.FPB_SECRETARY.value();
					} else if (getFacesScopes().getUserRoleList().size() != 0) {
						userRole = RoleCode.FPB_SECRETARY.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_SECRETARY";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_CHAIRPERSON.value())) {
						userRole = RoleCode.FPB_CHAIRPERSON.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_CHAIRPERSON";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
						|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_MEMBER.value())) {
						userRole = RoleCode.FPB_MEMBER.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_MEMBER";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			}
		}
		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}

	/**
	 * The method used to check whether approver setting tab menu is required to display.
	 * 
	 * @return true, if approver setting tab menu is required to display
	 *         false, if approver setting tab menu is not required
	 */
	public boolean isApproverSetting() {
		if (isContractAdmin() && (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
	 */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}

	/**
	 * The method used to check the accessibility of factoring tab menu.
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
	 */
	public boolean checkFactoringExpiryDate() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring() != null 
				&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus() != null
				&& (ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()
				|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.XF_PENDING_ACKNOWLEDGEMENT.longValue()
				|| ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.XF_PENDING_ENDORSEMENT.longValue())) {
			return true;
		}
		return false;
	}

	/**
	 * The method used to check base on service contract rules
	 * when Others is selected/not selected
	 */
	public void onChangeOther() {
		if (ctBackingBean.isBeforeGstReliefDueDate() && ctBackingBean.imposedServiceTaxCondition()) {
			ctBackingBean.onChangeServiceTax();
		}
		ctBackingBean.renderUpdateCheckbox(ctBackingBean.getManagedBean().getContractVer());
	}

	/**
	 * Function to check / uncheck single checkbox into committee member datatable
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllComitMemCheckBox(AjaxBehaviorEvent e) {
		getManagedBean().setSelectAll(false);
		for (PmUser pmUsers : getCommitteDataModel().getList()) {
			if (pmUsers.isSelectedCheckBox()) {
				ctBackingBean.getManagedBean().setButtonHideActive(false);
				return;
			}
		}
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			ctBackingBean.getManagedBean().setButtonHideActive(false);
		} else {
			ctBackingBean.getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Function to check / uncheck single checkbox into committee member datatable.
	 * 
	 * @param pass ajax event type
	 * */
	public void selectAllComitMem(AjaxBehaviorEvent e) {

		for (PmUser pmUsers : getCommitteDataModel().getList()) {
			if (getManagedBean().isSelectAll()) {
				pmUsers.setSelectedCheckBox(true);
			} else {
				pmUsers.setSelectedCheckBox(false);
			}
		}

		if ((boolean) e.getComponent().getAttributes().get("value")) {
			ctBackingBean.getManagedBean().setButtonHideActive(false);
		} else {
			ctBackingBean.getManagedBean().setButtonHideActive(true);
		}
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * This method is used by fulfillment module.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoCommonViewPage(Long contractId) {	
		ContractDetailsBackingBean ctDT = null;
		try {
			ctDT =  new ContractDetailsBackingBean();
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			reqDTO.setContractVerId( contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
			reqDTO.setViewOnly(true); 
			reqDTO.setCommonView(true);
			ctBackingBean.getManagedBean().setViewContract(true);
			ctBackingBean.getManagedBean().setOutcomesFromContract(getManagedBean().getOutComes());//Bug 34142, 15851 - shaqimi 04/12/2022
			ctDT.getManagedBean().setTaskId(getManagedBean().getTaskId());
			ctDT.setValidateContractField(reqDTO);
			getManagedBean().setSelectedTabIndex(0); 
			ctDT.getManagedBean().setBackPageAmendment(true);
			ctDT.getManagedBean().setAsAtDate(DateUtils.getCurrentDateWithoutTime());
			reqDTO.setCtContractVer(ctBackingBean.getManagedBean().getContractVer());
			ctDT.getManagedBean().setWarningPercentage(contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage());
			FacesContext fc = FacesContext.getCurrentInstance();
			ctDT.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId()+"?faces-redirect=true");
			ctDT.getManagedBean().setBackPageAmendment(true);
			//26753 - start
			getManagedBean().setUpdateCommittee(Boolean.FALSE);
			//26753 - end
			return CONTRACT_COMMON_PAGE;
		} finally {
			ctDT = null;
		}
	}

	/**
	 * The method used to check whether exist previous extension history
	 * and the extend start date is already started.
	 * 
	 * @return true, if valid to perform contract extension
	 *         false, if not valid to perform contract extension
	 */
	public boolean validExtensionDate() {
		List<CtContractVer> extensionHistoryList = getManagedBean().getAmendHistoryList();
		if (extensionHistoryList != null && extensionHistoryList.size() > 0) {
			CtContractVer latestExtensionVer = extensionHistoryList.get(extensionHistoryList.size() - 1);
			if (latestExtensionVer.getCtAmendment().getExtendStartDate().after(DateUtils.getCurrentDateWithoutTime())) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	/**
	 * The method used to retrieve the supplemental contract letter
	 * response due date base on the selected sign date and response period.
	 * 
	 * @return response due date
	 */
	public String retrieveResponseDueDate() {
		String dueDate = ResourceBundleUtils.getMessage("lbl.txt.na", 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());

		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getGovtSignDate() != null
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaResponsePeriod() != null) {

			Date ackDueDate = DateUtils.addDays(
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getGovtSignDate(),
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaResponsePeriod());

			CommonUtils commonUtils = new CommonUtils();
			SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
			dueDate = dateSDF.format(ackDueDate);

			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setAckDueDate(ackDueDate);
		} else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setAckDueDate(null);
		}
		return dueDate;
	}
	
	// Redmine 19600 - Amendment with Supplement
	/**
	 * to validate contract amount below 20k
	 * 
	 * @return
	 */
	public boolean isContractAmountAmend() {
		boolean isContractAmountAmend = false;
		if(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().longValue() <= 20000L){ //#36635
			isContractAmountAmend = true;
		}				
		return isContractAmountAmend;
		}
	
	//#26520 - start
	public void onChangeRepType(CtCommitteeMember currCommitteeMember, Long parameterId) {
		PmParameter repType = null;
		if (parameterId == null || parameterId == 0) {
			return;
		}
		if (parameterId != null && parameterId != 0) {
			repType = ParameterHelper.getInstance().getPmParameterById(parameterId);
			if(repType != null) {
				currCommitteeMember.setRepresentativeType(repType);
				currCommitteeMember.getRepresentativeType().setParameterId(parameterId);

				/*for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
					if(committeMember.getPmUser().getUserId() == currCommitteeMember.getPmUser().getUserId()) {
						committeMember.setRepresentativeType(repType);
						committeMember.getRepresentativeType().setParameterId(parameterId);
					}
				}*/
			}
		}
	}
	//#26520 - end
	
	//#26186 start 
	/**
	 * Checks the contract conditions for the supplier.
	 * 
	 * This method evaluates several conditions related to the contract status and task name.
	 * It ensures that:
	 * - The task name is not null.
	 * - The viewSCL is not being viewed.
	 * - The contract status is not loa completed and rejection acknowledgement.
	 * 
	 * @return true if all conditions are met; false otherwise.
	 */
	public boolean checkContractConditionForSupplier() {
		this.getManagedBean().setDisciplinaryActionCancel(false);
		this.getManagedBean().setSupplierBumiCancel(false);
		Long statusId = ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId();

		boolean contractStatus = !statusId.equals(ContractStatusConst.FA_LOA_COMPLETED)
				&& !statusId.equals(ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT)
				&& !statusId.equals(ContractStatusConst.FA_PENDING_REJECTION_ACKNOWLEDGEMENT);

		return getManagedBean().getTaskName() != null && !ctBackingBean.getManagedBean().isViewSCL() && contractStatus;
	}
	
	/**
	 * checkAndSetDA - function to retrieve (get) DA, set DA and set button cancel
	 * isDisciplinaryAction - Variable for the contract got DA or not
	 * isDisciplinaryActionNotification - Variable for type of punishment either small or big (true - small || false - big)  
	 * isDisciplinaryActionCancel - Variable for DA to be cancel (to set button cancel for the DA that need to be cancel)
	 * 
	 * @exception SLAUtils
	**/
	// #29793 #26186 start
	public void checkAndSetDA() { // #32860 start
		try {
			Long statusId = ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId();
				// #32860 start
			if (!this.getManagedBean().isValidationDA()) {
				checkDA();
			}
			setDACancel(statusId);
			// #32860 end

			if (getManagedBean().isDisciplinaryActionCancel()) {
				handleMessageDACancel(statusId);
			}
			this.getManagedBean().setValidationDA(false);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkAndSetDA",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(), "checkAndSetDA",
					getUserIdSessionFromLoginId(), e);
			throw new EpException(MessageConst.ERROR_750053, new String[] { e.getMessage() });
		}
	}
	
	/**
	 * Method to handle message display and button when disciplinary action cancel is true
	 * 
	 * @param statusId The status of the current contract
	 */
	public void handleMessageDACancel(Long statusId) {
		String msgThrowDisc = null;
		String[] outcome = new String[getManagedBean().getOutComes().length];
		String[] strArray = handleWIPTransactionButton(statusId);
		List<String> listButton = new ArrayList<>(Arrays.asList(strArray));

		if (statusId.equals(ContractStatusConst.FA_PENDING_SIGN_OFF)
				|| statusId.equals(ContractStatusConst.FA_PENDING_CHAIRPERSON_SIGN_OFF)) {
			if (listButton.contains(BpmOutcomeConst.BPM_REVISE)) {
				outcome = new String[] { BpmOutcomeConst.BPM_REVISE };
				msgThrowDisc = MessageConst.ERROR_750069;
			}
		} else if (listButton.contains(BpmOutcomeConst.BPM_CANCEL)
				|| statusId.equals(ContractStatusConst.FA_PENDING_REVISION)) {
			outcome = new String[] { BpmOutcomeConst.BPM_CANCEL };
			msgThrowDisc = MessageConst.ERROR_750052;
		} else if (listButton.contains(BpmOutcomeConst.BPM_REJECT)) {
			outcome = new String[] { BpmOutcomeConst.BPM_REJECT };
			msgThrowDisc = MessageConst.ERROR_750068;
		}

		if (msgThrowDisc != null) {
			getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);
			getManagedBean().setOutComes(outcome);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(msgThrowDisc,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * The method will set the DA for every status. 
	 * isDisciplinaryActionCancel to be set so that the cancel button can be set
	 * 
	 * @param statusId The status of the current contract
	 **/
	public void setDACancel(Long statusId) {
		if (getManagedBean().isDisciplinaryAction() && !getManagedBean().isDisciplinaryActionNotification()) {
			if (ContractStatusConst.FA_DA.contains(statusId)) {
				// #32652 start
				checkPbqcMofAuthorityDA();
				// #32652 end
			} else if (ContractStatusConst.FA_DA_LOA.contains(statusId)) {
				// contract approver authority
				// #32652 start
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isApproverAuthorityReq()) {
					checkApproverAuthorityDA();
				}
				checkPbqcMofAuthorityDA();
				// #32652 end
			} else {
				getManagedBean().setDisciplinaryActionCancel(true);
			}
		}
	}
	
	/**
	 * Method to check and set DA for approver authority.
	 */
	private void checkApproverAuthorityDA(){
		CtActor approverActor = retrieveContractActor(
				ctBackingBean.getManagedBean().getContractVer().getContractVerId(),
				ctBackingBean.getManagedBean().getContractVer().getDocType(),
				getApproverRoleCode());
		if (approverActor != null && 
				(approverActor.getActionDate().equals(getManagedBean().getDisciplinaryActionStartDate()) 
						|| approverActor.getActionDate().after(getManagedBean().getDisciplinaryActionStartDate()))) {
			getManagedBean().setDisciplinaryActionCancel(true);
		}
	}
	
	/**
	 * Method to get approver role code
	 * @return RoleCode
	 */
	private String getApproverRoleCode() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			return RoleCode.CT_APPROVER.value();
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode()
				.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
			return RoleCode.BPK_CC_APPROVER.value();
		}
		return null;
	}
	
	/**
	 * Method to set DA cancel during PBQC and/or MOF authority
	 */
	private void checkPbqcMofAuthorityDA() {
	    if (isPbqcAuthorityDA() || isMofAuthorityDA()) {
	        getManagedBean().setDisciplinaryActionCancel(true);
	    }
	}
	
	/**
	 * Method to check is in PBQC authority and DA date has been pass or not
	 */
	private boolean isPbqcAuthorityDA() {
		Date pbqcDate = StringUtils.isNotBlank(EntityHelper.getStringNestedProperty(ctBackingBean.getManagedBean(),
				"contractVer.ctAmendment.pbqcDecisionDate")) ? ctBackingBean.getManagedBean().getContractVer()
				.getCtAmendment().getPbqcDecisionDate() : null;
		return ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()
				&& pbqcDate != null
				&& (pbqcDate.equals(getManagedBean().getDisciplinaryActionStartDate()) || 
						pbqcDate.after(getManagedBean().getDisciplinaryActionStartDate()));
	}
	
	/**
	 * Method to check is in MOF authority and DA date has been pass or not
	 */
	private boolean isMofAuthorityDA() {
		Date mofDate = StringUtils.isNotBlank(EntityHelper.getStringNestedProperty(ctBackingBean.getManagedBean(),
				"contractVer.ctAmendment.mofDecisionDate")) ? ctBackingBean.getManagedBean().getContractVer()
				.getCtAmendment().getMofDecisionDate() : null;
		return ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isMofAuthorityReq()
				&& mofDate != null
				&& (mofDate.equals(getManagedBean().getDisciplinaryActionStartDate()) || 
						mofDate.after(getManagedBean().getDisciplinaryActionStartDate()));
	}

	
	/**
	 * The method will retrieve the disciplinary action from supplier and contract module.
	 * 1. Retrieve the info from supplier, if supplier has disciplinary action, then will get message from supplier
	 * 2. Will retrieve info from contract if the supplier has disciplinary action. 
	 **/
	public void checkDA() {
		ISupplierDataMaintenanceSO suppDataSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
		FacesScopes facesScopes = new FacesScopes();
		try {
			PmUser sUsers = facesScopes.getUserSessionBean();

			retrieveDA();
			this.getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);

			if (this.getManagedBean().isDisciplinaryAction()) {
				SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();

				String language = facesScopes.getUserLocale().getLanguage() != null ? facesScopes.getUserLocale()
						.getLanguage() : sUsers.getPreferredLanguage();
				reqDTO.setLanguage(language);

				SmSupplier currentSupplier = getSupplier(); // #34097
				if (currentSupplier != null) {
					reqDTO.setSupplier(currentSupplier);
				}

				SupplierDataMaintenanceResDTO resDTO = suppDataSO.retrieveDisciplinaryActionMsg(reqDTO);
				if (resDTO.getErrorMessage() != null) {
					this.getManagedBean().setDisciplinaryActionMsg(resDTO.getErrorMessage());
				}
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkDA",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(), "checkDA",
					getUserIdSessionFromLoginId(), e);
			throw new EpException(MessageConst.ERROR_750053, new String[] { e.getMessage() });
		}
	}
	
	/**
	 * the method will retrieve the disciplinary action info from supplier module.
	 * If supplier type of punishment is “Suspend Registration” or “Cancel Registration” 
	 * or “Blacklist Owner / Director of Company” or “Cancellation of Bumiputera Status” 
	 * or “Cancellation of Manufacturer Status”, this Amendment With Supplemental process not able to proceed
	**/
	private void retrieveDA() { // #32712
		SmSupplier currentSupplier = getSupplier(); // #34097
		if (currentSupplier == null) {
			return;
		}

		this.getManagedBean().setDisciplinaryAction(false);
		// #28527 start
		List<SmDisciplinaryAction> resultList = SmApplicationUtil
				.retrieveActiveSmDisciplinaryActionBySupplier(currentSupplier); // #34097
		// 28527 end

		if (CollectionUtils.isNotEmpty(resultList)) {
			for (SmDisciplinaryAction disciplinaryAction : resultList) {
				if (isCurrentDateWithinAction(disciplinaryAction)
						&& (!getManagedBean().isDisciplinaryAction() && !getManagedBean()
								.isDisciplinaryActionNotification())) { // #32712
					handleDASubject(disciplinaryAction);
				}
			}
		}
	}
	//29793 end
	
	/**
	 * Checks if the current date is within the start and end dates of the disciplinary action.
	 *
	 * @param disciplinaryAction the disciplinary action to check against
	 * @return true if the current date is within the range, false otherwise
	 */
	private boolean isCurrentDateWithinAction(SmDisciplinaryAction disciplinaryAction) {
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
	    Date startDate = disciplinaryAction.getStartDate();
	    Date endDate = disciplinaryAction.getEndDate();
	    
	    return (currentDate.equals(startDate) || currentDate.after(startDate)) &&
	           (currentDate.equals(endDate) || currentDate.before(endDate));
	}
	
	/**
	 * method to check which DA that has been applied to the supplier and set notification DA true/false
	 * @param disciplinaryAction
	 */
	public void handleDASubject(SmDisciplinaryAction disciplinaryAction) {
		if (disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration()
				|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isBumiputeraCancel()) {
			getManagedBean().setDisciplinaryActionNotification(false);
			getManagedBean().setDisciplinaryAction(true);
		} else if (disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory()
				|| disciplinaryAction.isManufacturerCancel()) {
			getManagedBean().setDisciplinaryActionNotification(true);
			getManagedBean().setDisciplinaryAction(true);
		}
		getManagedBean().setDisciplinaryActionStartDate(disciplinaryAction.getStartDate());
		getManagedBean().setDisciplinaryActionEndDate(disciplinaryAction.getEndDate());
	}
	
	/**
	 * Process to validate the DA again after the PBM/MOF Decision DATE has been key in and being submit.
	 * (isDisciplinaryActionNotification - false) to check the type of punishment.
	 * Once the DA has been validate, it will do the following 
	 * 1. Set the BPM outcome to SAVE so that the contract will not undergo Tracking Diary and BPM process in SO file.
	 * 2. Set the checkValidationDisciplinaryAction to true so that after the record has been saved, it will skip checkDA process.
	 * 
	 * @return true if DA is true, PBQC and/or MOF decision is true.
	 */
	public boolean checkValidationDA(){
		return getManagedBean().isDisciplinaryAction() && !getManagedBean().isDisciplinaryActionCancel()
				&& (isPbqcAuthorityDA() || isMofAuthorityDA());
	}
	
	/**
	 * Method to list out the history of DA on view page
	 * @exception SLAUtils
	 */
	public void onViewDisciplinaryAction() {
		try {
			SmSupplier currSupp = getSupplier();

			// #30756 start
			currSupp.setFlagSupplier(true);
			currSupp.setFlagDASuppProfile(true);
			// #30756 end

			List<DisciplinaryActionVO> voList = ViewSuppProfileBackingBean.getDisciplinaryActionVO(currSupp);

			getManagedBean().setDisciplineVOs(
					CollectionUtils.isNotEmpty(voList) ? voList : new ArrayList<DisciplinaryActionVO>());

			RequestContext.getCurrentInstance().execute("disciplinaryActionHistoryDlg.show();");
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "onViewDisciplinaryAction",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
					"onViewDisciplinaryAction", getUserIdSessionFromLoginId(), e);
			throw new EpException(MessageConst.ERROR_770045, new String[] { e.getMessage() });
		}
	}
	//#26186 end
		
	//#25890 - Start
	private PmParameter retrieveParameterById(Long parameterId) {
		if (parameterId == null) {
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.deduction.type", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}

		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		paramReqDTO.setParameterId(parameterId);

		PmParameter pmParameter = getParameterSO().retrieveParameterById(paramReqDTO).getPmParameter();
		return pmParameter;
	}

	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	//#25890 - End

		
	/**
	 * The method used to retrieve the contract administrator list.
	 * This method will get the all contract administrators with 
	 * contract owner organization profile when the loa's administrator 
	 * is invalid.
	 */
	private void getContractAdminlist() {
		IUserSO userSO = null;
		UserReqDTO userReqDTO = null;
		try {
			if (getManagedBean().getContractAdminlist() == null) {
				getManagedBean().setContractAdminlist(new ArrayList<PmUser>());

				String roleCode = (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) ? RoleCode.MIN_CONTRACT_ADMIN.value() : RoleCode.BPK_CCA
						.value();

				CtContractVer ctContractVer = (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) ? ctBackingBean.getManagedBean().getContractVer().getCtContract().getLatestContractVer() : ctBackingBean
						.getManagedBean().getContractVer().getCtContract().getLatestContractVer();
				CtActor ctActor = retrieveContractActor(ctContractVer.getContractVerId(), ctContractVer.getDocType(), roleCode);

				if (ctActor != null) {
					getManagedBean().getContractAdminlist().add(ctActor.getUser());
				} else {
					userReqDTO = new UserReqDTO();
					userReqDTO.setRoleCode(roleCode);
					userReqDTO.setOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
					userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					getManagedBean().setContractAdminlist(userSO.retrieveUserListByRole(userReqDTO).getUserList());
				}
			}
		} finally {
			userSO = null;
		}
	}
	// 26753 - start
	
	// #29403 start
	public void copyCommitteeMember() {
		if (getManagedBean().getUpdatedCommitteeMembers() != null) {
			getManagedBean().getUpdatedCommitteeMembers().clear();
		}

		@SuppressWarnings("unchecked")
		List<CtCommitteeMember> copyMember = (List<CtCommitteeMember>) SerializationUtils.clone((Serializable) ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers());
		getManagedBean().setUpdatedCommitteeMembers(copyMember);
	}

	// #29403 end

	public void updateCommittee() {
		// #29403 start
		// getManagedBean().setUpdateCommittee(Boolean.TRUE);
		getManagedBean().setSelectAll(false);
		ctBackingBean.getManagedBean().setButtonHideActive(true);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, UPDATE_COMMITTEE_PAGE);
		// #29403 end
	}

	// #29403 start
	public void backToDecisionTab() {
		getManagedBean().setUpdateCommittee(Boolean.FALSE);
		if (!getManagedBean().isHasChanges()) {
			if (ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
					|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { // #26688
				copyCommitteeMember();
			}
		}
		if (getManagedBean().getFilteredUpdatedCommitteeMembers() != null) {
			getFilteredCommitteeMembers().clear();
		}

		// setTabValue("DCN");
		getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));

		backToAmendmentPage();

	}

	public void backToAmendmentPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PROCESS_AMEND_PAGE);
	}
		
	//#29403 end
	/**
	 * The method used to invoke and initiate the search committee 
	 * member dialog.
	 */
	public void getCommitteeMembers() {
		getManagedBean().setCommitteeName(null);
		getManagedBean().setIdentificationNo(null);
		getManagedBean().setIdentificationType(null);
		getCommitteDataModel().resetData();
		RequestContext.getCurrentInstance().execute("dlgCommittee.show()");
	}
	
	/**
	 * The method used to add the selected existing committee
	 * member as the meeting committee.
	 * This method will generate new record(s) in meeting
	 * committee list.
	 */
	public void addSelectedCommitteeMembers() {
		Map<Long, CtCommitteeMember> duplicatePmUserMap = new HashMap<Long, CtCommitteeMember>();
		if (getManagedBean().getFilteredUpdatedCommitteeMembers() == null) {
			getManagedBean().setFilteredUpdatedCommitteeMembers(new ArrayList<CtCommitteeMember>());
		}

		Iterator<PmUser> pmUserItr = getCommitteDataModel().getList().iterator();
		while (pmUserItr.hasNext()) {
			PmUser users = pmUserItr.next();
			if (users.isSelectedCheckBox()) {
				CtCommitteeMember committeeMember = new CtCommitteeMember();
				committeeMember.setCtAmendment(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment());
				committeeMember.setPmUser(users);
				committeeMember.setMemberRole(new PmParameter());
				committeeMember.setMemberRoleId(null);
				committeeMember.setMemberName(users.getUserName());
				committeeMember.setIcPassport(users.getIdentificationNo());
				committeeMember.setGrade(users.getGrade());
				committeeMember.setPnp(users.isPMP());
				committeeMember.setDesignation(users.getDesignation());
				committeeMember.setEmail(users.getEmail());
				committeeMember.setRepresentativeType(new PmParameter()); // #29403
				duplicatePmUserMap.put(users.getUserId(), committeeMember);
			}
		}

		for (Map.Entry<Long, CtCommitteeMember> entry : duplicatePmUserMap.entrySet()) {
			boolean chkDataDuplication = false;
			for (CtCommitteeMember ctMember : getManagedBean().getUpdatedCommitteeMembers()) {
				if (entry.getKey().equals(ctMember.getPmUser().getUserId())) {
					chkDataDuplication = true;
					if (ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctMember.getUpdateType()) || ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctMember.getUpdateType())) {
						getManagedBean().getFilteredUpdatedCommitteeMembers().add(ctMember);
						if (ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctMember.getUpdateType())) {
							ctMember.setUpdateType(null);
						} else {
							ctMember.setUpdateType(ContractConst.ADD_NEW_MEMBER);
						}
					}
					break;
				}
				// #29403 start
				if (ctMember.isSelectedCheckBox()) {
					ctMember.setSelectedCheckBox(false);
				}
				// #29403 end
			}
			if (!chkDataDuplication) {
				entry.getValue().setUpdateType(ContractConst.ADD_NEW_MEMBER);
				getManagedBean().getUpdatedCommitteeMembers().add(entry.getValue());
			}
		}
		getManagedBean().setHasChanges(Boolean.FALSE); // #29403
	}  
		
	/**
	 * The method used for validating the duplicate chairperson.
	 * A warning message will display on screen when more than one chairperson
	 * is added as committee. Also updating current committee member role id.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	//#29403 start
//	public void validateUpdatedCommitteeListDuplicateChairperson(Long parameterId) throws Exception {
	public void validateUpdatedCommitteeListDuplicateChairperson(CtCommitteeMember curCommitteeMember, Long parameterId) throws Exception {

		int counter = 0;
		String curParameterCode = "";

		if (parameterId != null) {
			if (curCommitteeMember.getMemberRoleId() != null && !curCommitteeMember.getMemberRoleId().equals(parameterId)) {
				curCommitteeMember.getRepresentativeType().setParameterId(null);
				curCommitteeMember.setRepresentativeType(null);
			}
			if (curCommitteeMember.getRepresentativeType() == null) {
				curCommitteeMember.setRepresentativeType(new PmParameter());
			}
			curCommitteeMember.setMemberRole(ParameterHelper.getInstance().getPmParameterById(parameterId));
			curCommitteeMember.setMemberRoleId(parameterId);
		}

		if (curCommitteeMember.getMemberRoleId() != null) {
			curParameterCode = ParameterHelper.getInstance().getPmParameterById(curCommitteeMember.getMemberRoleId()).getParameterCode();
		} else {
			curParameterCode = ParameterHelper.getInstance().getPmParameterById(curCommitteeMember.getMemberRole().getParameterId()).getParameterCode();
		}

		if (curParameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			for (CtCommitteeMember allCommitteMember : getFilteredCommitteeMembers()) {
				String allParameterCode = "";
				if (allCommitteMember.getMemberRoleId() != null) {
					allParameterCode = ParameterHelper.getInstance().getPmParameterById(allCommitteMember.getMemberRoleId()).getParameterCode();
				} else {
					allParameterCode = ParameterHelper.getInstance().getPmParameterById(allCommitteMember.getMemberRole().getParameterId()).getParameterCode();
				}

				if (allCommitteMember.getMemberRole() != null && allCommitteMember.getMemberRole().getParameterId() != null && allParameterCode == curParameterCode) {
					counter++;
				}
			}
		}

		if (counter > 1) {
			throw new EpException(MessageConst.ERROR_750008);
		}
	}

	// 29403 end
		
	//#29403 start
	/**
	 * The method used to get the parameter description by parameter.
	 * with additional setting for secretary role.
	 * Attendance is compulsory for secretary.
	 * 
	 * @param   index      the current record index (only used for secretary role)
	 * @param   curCommitteeMember  the current committee member
	 * @return  parameter description
	 */
	public String getParameterDescCommitteeByMember(CtCommitteeMember curCommitteeMember, int index) {
		String strDescription = null;
		PmParameter curParameter = null;

		if (curCommitteeMember.getMemberRoleId() != null) {
			curParameter = ParameterHelper.getInstance().getPmParameterById(curCommitteeMember.getMemberRoleId());
		} else {
			curParameter = ParameterHelper.getInstance().getPmParameterById(curCommitteeMember.getMemberRole().getParameterId());
		}

		if (curParameter != null && curParameter.getParameterCode().equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
			curCommitteeMember.setAttended(true);
			if (ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
					|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { // #26688
				getManagedBean().getUpdatedCommitteeMembers().remove(index);
				getManagedBean().getUpdatedCommitteeMembers().add(index, curCommitteeMember);
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().remove(index);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().add(index, curCommitteeMember);
			}
			strDescription = ResourceBundleUtils.getMessage("lbl.ct.secretary", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			strDescription = getParameterDesc(curParameter);
		}
		return strDescription;
	}

	// #29403 end
		
	/**
	 * The method used to delete the selected meeting committee(s).
	 */
	public void removeCommitteeMembersFromUpdatedList() {
		CtCommitteeMember member = null;
		CtCommitteeMember tempMember = null;
		try {
			if (getManagedBean().getUpdatedCommitteeMembers() != null) {
				Iterator<CtCommitteeMember> committeeMembers = getManagedBean().getUpdatedCommitteeMembers().iterator();
				while (committeeMembers.hasNext()) {
					CtCommitteeMember members = committeeMembers.next();
					if (members.isSelectedCheckBox()) {
						member = members;
						for (CtCommitteeMember updMember : getManagedBean().getUpdatedCommitteeMembers()) {
							if (updMember.getPmUser().getUserId().equals(member.getPmUser().getUserId())) {
								tempMember = updMember;
								if (updMember.getUpdateType() == null || ContractConst.UPDATE_EXISTING_MEMBER.equalsIgnoreCase(updMember.getUpdateType())) {
									updMember.setUpdateType(ContractConst.REMOVE_FROM_ATTENDANCE_LIST);
									break;
								} else if (ContractConst.ADD_NEW_MEMBER.equalsIgnoreCase(updMember.getUpdateType())) {
									updMember.setUpdateType(ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST);
									break;
								}
							}
						}
						if (tempMember != null) {
							for (CtCommitteeMember filteredMembered : getManagedBean().getFilteredUpdatedCommitteeMembers()) {
								if (tempMember.getPmUser().getUserId().equals(filteredMembered.getPmUser().getUserId())) {
									getManagedBean().getFilteredUpdatedCommitteeMembers().remove(filteredMembered);
									break;
								}
							}
						}
					}
					members.setSelectedCheckBox(false); // #29403
				}
				getManagedBean().setHasChanges(Boolean.FALSE); // #29403
				ctBackingBean.getManagedBean().setButtonHideActive(true);

			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
		
	public void updateLatestCommitteeMembers() {
		boolean isNew = true;
		int countModified = 0;
		PmParameter memberParameter = null;
		PmParameter chairpersonParameter = null;
		for (CtCommitteeMember ctUpdatedMember : getManagedBean().getUpdatedCommitteeMembers()) {
			isNew = true;
			for (CtCommitteeMember originalCtMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
				if (ParameterConst.COMMITTEE_ROLE_MEMBER.equalsIgnoreCase(originalCtMember.getMemberRole().getParameterCode())) {
					memberParameter = originalCtMember.getMemberRole();
				} else if (ParameterConst.COMMITTEE_ROLE_CHAIRPERSON.equalsIgnoreCase(originalCtMember.getMemberRole().getParameterCode())) {
					chairpersonParameter = originalCtMember.getMemberRole();
				}
				if (ctUpdatedMember.getPmUser().getUserId().equals(originalCtMember.getPmUser().getUserId()) && !ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())
						&& !ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
					if (ctUpdatedMember.getRepresentativeType() != null && ctUpdatedMember.getRepresentativeType().getParameterId() != null) {
						if (originalCtMember.getRepresentativeType() != null && !originalCtMember.getRepresentativeType().getParameterId().equals(ctUpdatedMember.getRepresentativeType().getParameterId())) { // #29403
							ctUpdatedMember.setNewRepresentativeType(ctUpdatedMember.getRepresentativeType());
						} else {
							if (!ContractConst.ADD_NEW_MEMBER.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
								ctUpdatedMember.setNewRepresentativeType(null);
								ctUpdatedMember.setUpdateType(null);
							}
						}
						isNew = false;
					}
					if (ctUpdatedMember.getMemberRole() != null && ctUpdatedMember.getMemberRole().getParameterId() != null) {
						if (!originalCtMember.getMemberRole().getParameterId().equals(ctUpdatedMember.getMemberRole().getParameterId())) {
							ctUpdatedMember.setNewMemberRole(ctUpdatedMember.getMemberRole());
						} else {
							if (!ContractConst.ADD_NEW_MEMBER.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
								ctUpdatedMember.setNewMemberRole(null);
								ctUpdatedMember.setUpdateType(null);
							}
						}
						isNew = false;
					}
					if (ctUpdatedMember.getUpdateType() != null) {
						countModified = countModified + 1;
					}

					for (CtCommitteeMember tempMember : getManagedBean().getFilteredUpdatedCommitteeMembers()) {
						if (tempMember.getPmUser().getUserId().equals(ctUpdatedMember.getPmUser().getUserId())) {
							getManagedBean().getFilteredUpdatedCommitteeMembers().remove(tempMember);
							break;
						}
					}
					break;
				}
			}
			if (ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
				countModified = countModified + 1;
				isNew = false;
			} else if (ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
				isNew = false;
			}
			if (isNew) {
				if (ctUpdatedMember.getMemberRole().getParameterId().equals(memberParameter.getParameterId())) {
					ctUpdatedMember.getMemberRole().setParameterCode(memberParameter.getParameterCode());
				} else if (ctUpdatedMember.getMemberRole().getParameterId().equals(chairpersonParameter.getParameterId())) {
					ctUpdatedMember.getMemberRole().setParameterCode(chairpersonParameter.getParameterCode());
				}
				ctUpdatedMember.getMemberRole().setParameterType(ParameterTypeConst.COMMITTEE_ROLE);
				ctUpdatedMember.setUpdateType(ContractConst.ADD_NEW_MEMBER);
				countModified = countModified + 1;
			} else {
				if (!ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType()) && !ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
					if (ctUpdatedMember.getNewMemberRole() != null || ctUpdatedMember.getNewRepresentativeType() != null) {
						ctUpdatedMember.setUpdateType(ContractConst.UPDATE_EXISTING_MEMBER);
					}
				}
			}

		}
		if (countModified > 0) {
			getManagedBean().setHasChanges(Boolean.TRUE);
		} else {
			getManagedBean().setHasChanges(Boolean.FALSE);
		}
		getManagedBean().setUpdateCommittee(Boolean.FALSE);

		// #29403 start
		// setTabValue("DCN");
		getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));

		backToAmendmentPage();
		// #29403 end
	}
		
	public void validateLatestCommitteeMembers() {

		List<CtCommitteeMember> outer = getManagedBean().getUpdatedCommitteeMembers();
		List<CtCommitteeMember> inner = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers();
		boolean isUpdateNeeded = false;
		boolean isFound = false;

		for (CtCommitteeMember ctUpdatedMember : outer) {
			// #29403 start
			if (ctUpdatedMember.isSelectedCheckBox()) {
				ctUpdatedMember.setSelectedCheckBox(false);
			}
			// #29403 end
			for (CtCommitteeMember originalCtMember : inner) {
				if (ctUpdatedMember.getPmUser().getUserId().equals(originalCtMember.getPmUser().getUserId())) {

					// #29403 start
					if (originalCtMember.isSelectedCheckBox()) {
						originalCtMember.setSelectedCheckBox(false);
					}
					// #29403 end

					if (!ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
						isFound = true;

						// #29403 start
						if (!getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B) && !getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
							if (ctUpdatedMember.getRepresentativeType() != null && ctUpdatedMember.getRepresentativeType().getParameterId() != null) { // #29403
								if (!originalCtMember.getRepresentativeType().getParameterId().equals(ctUpdatedMember.getRepresentativeType().getParameterId())) {
									isUpdateNeeded = true;
								}
							}
						}
						// #29403 end

						if (ctUpdatedMember.getMemberRole().getParameterId() != null) {
							if (!originalCtMember.getMemberRole().getParameterId().equals(ctUpdatedMember.getMemberRole().getParameterId())) {
								isUpdateNeeded = true;
							}
						}
						break;
					} else {
						isUpdateNeeded = true;
						break;
					}
				} else {
					if (ContractConst.ADD_NEW_MEMBER.equalsIgnoreCase(ctUpdatedMember.getUpdateType())) {
						isUpdateNeeded = true;
						break;
					}
				}
			}
			if (isUpdateNeeded) {
				break;
			}
		}
			
		// #29403 start
		validateCommiteeMemberDetails();
		// setTabValue("DCN");
		getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.decision", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
		// #29403 end

		if (isUpdateNeeded || !isFound) {
			RequestContext.getCurrentInstance().execute("updLatestCommitteeDlg.show()");
		} else {
			getManagedBean().setUpdateCommittee(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("validateLatestCommitteeDlg.show()");
		}

	}
		
	public List<CtCommitteeMember> getFilteredCommitteeMembers() {
		List<CtCommitteeMember> filteredCommitteeMembers = new ArrayList<CtCommitteeMember>();
		if (ContractStatusConst.FA_PENDING_MEETING_DECISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())
				|| ContractStatusConst.FA_PENDING_MEETING_DECISION_REVISION.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) { // #26688
			for (CtCommitteeMember member : getManagedBean().getUpdatedCommitteeMembers()) {
				if (member.getUpdateType() == null
						|| (member.getUpdateType() != null && !ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(member.getUpdateType()) && !ContractConst.REMOVE_NEW_MEMBER_FROM_ATTENDANCE_LIST.equalsIgnoreCase(member.getUpdateType()))) {
					filteredCommitteeMembers.add(member);
				}
			}
		} else {
			for (CtCommitteeMember member : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
				if (!ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(member.getUpdateType())) {
					filteredCommitteeMembers.add(member);
				}
			}
		}

		return filteredCommitteeMembers;
	}
		
	public List<CtCommitteeMember> getFilteredOriginalCommitteeMembers() {
		List<CtCommitteeMember> filteredCommitteeMembers = new ArrayList<CtCommitteeMember>();
		for (CtCommitteeMember member : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
			if (member.getUpdateType() == null || ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(member.getUpdateType())) {
				filteredCommitteeMembers.add(member);
			}
		}
		return filteredCommitteeMembers;
	}
		
	public List<CtCommitteeMember> getFilteredSignedCommitteeMembers() {
		List<CtCommitteeMember> filteredCommitteeMembers = new ArrayList<CtCommitteeMember>();
		for (CtCommitteeMember member : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
			if (!ContractConst.REMOVE_FROM_ATTENDANCE_LIST.equalsIgnoreCase(member.getUpdateType())) {
				filteredCommitteeMembers.add(member);
			}
		}
		return filteredCommitteeMembers;
	}

	// 26753 - end
				
	// #25956 - Start
	/**
	 * The method used to authenticate user during reject for approval the
	 * contract amendment. This method will invoke a confirmation dialog for
	 * user authentication.
	 * 
	 * @param outcome
	 *            the user action on this contract
	 * @return result of the saveContractAmendment process
	 * @throws Exception
	 *             if an exception occurred
	 * @see Exception
	 */
	public String preRejectContractAmendment(String outcome) throws Exception {
		if ((BpmOutcomeConst.BPM_REJECT.equals(outcome) && !isSupplierRole())) {

			setSecretary();
			// validateContractAmendment(outcome);
			validateRemarkCancel(outcome);
			// Temp setTextBeforeSign : DocNo+Type
			StringBuilder sb = new StringBuilder();
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
			sb.append(ContractConst.SEPRATE_LINE);
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
			} else {
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				sb.append(ContractConst.SEPRATE_LINE);
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			}
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(newOverallContractAmount());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome) && isSupplierRole()
				&& !getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
			// preRejectContractAmendment(outcome);
			// validateContractAmendment(outcome);
			validateRemarkCancel(outcome);
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			if (hasGovtSellerRole()) {
				getManagedBean().setTextBeforeSign(prepareSupplierOriginalText());
				// preRejectContractAmendment(outcome);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			} else {
				// preRejectContractAmendment(outcome);
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			}
			return null;
		} else {
			return saveContractAmendment(outcome);
		}
	}
	// #25956 - End
		
		
	// #25956 - Start
	/**
	 * The method used for validating Remark Cancel/Revise/Reject Button. A
	 * warning message will display on screen when Remark is null
	 * 
	 * @throws Exception
	 *             if an exception occurred
	 * @see Exception
	 */
	public void validateRemarkCancel(String action) throws Exception {

		String remark = getManagedBean().getRemarkList().get(0).getRemark();
		String itemRemark = ctBackingBean.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
		String supplierRemark = ctBackingBean.getManagedBean().getContractVer().getSupplierRemarks().get(0).getRemark();

		// #26688 start
		String message = null;
		if (BpmOutcomeConst.BPM_REVISE.equals(action)) {
			message = MessageConst.ERROR_100022;
		} else if (BpmOutcomeConst.BPM_CANCEL.equals(action) || BpmOutcomeConst.BPM_REJECT.equals(action)) {
			message = MessageConst.ERROR_100064;
		}

		if (message != null 
				&& ((remark == null || remark.length() == 0) 
						&& (itemRemark == null || itemRemark.length() == 0) 
						&& (supplierRemark == null || supplierRemark.length() == 0))) {
			// setTabValue("AD");

			// #35434 - Start
			if (getManagedBean().getAckFA() != null && getManagedBean().getAckFA().equals(ContractConst.SUPPLIER_ACK_LOA)) {
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.loa.details", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			} else {
				getManagedBean().setSelectedTabIndex(getManagedBean().getIndexing().get(ResourceBundleUtils.getMessage("lbl.ct.menu.amendment", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			}
			// #35434 - End

			throw new EpException(message, new String[] { ResourceBundleUtils.getMessage("lbl.ct.remark.amendment", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
		// #26688 end

	}
	// #25956 - End
		
	//#30428 - Start
	/**
	 * The method used to entered new ceiling quantity.
	 * 
	 * @param ctItemCeilingQty   object of CeilingQty item
	 */
	public void onChangeCeilingQty(CtItemCeilingQty ctItemCeilingQty) {

		if (getManagedBean().getSelectedItem().getCtItemCeilingQties().size() > 0) {
			for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
				if (getManagedBean().getSelectedItem().getScRequestItem().getRequestNoteItemId().equals(item.getScRequestItem().getRequestNoteItemId())) {
					// Start Fix - #34539
					if (item.isBrandNew()) {
						if (item.getCtItemCeilingQties().size() != getManagedBean().getSelectedItem().getCtItemCeilingQties().size()) {
							getManagedBean().getSelectedItem().getCtItemCeilingQties().remove(item.getCtItemCeilingQties());
							getManagedBean().getSelectedItem().setCtItemCeilingQties(item.getCtItemCeilingQties());
						}
					} else {
						if (item.getCtItemCeilingQties().size() != getManagedBean().getSelectedItem().getCtItemCeilingQties().size()) {
							item.getCtItemCeilingQties().clear();
							item.getCtItemCeilingQties().addAll(getManagedBean().getSelectedItem().getCtItemCeilingQties());
						}
					}
					// End Fix - #34539
					if (!item.getCtItemCeilingQties().isEmpty()) {
						for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {
							if (ceilingQty.getCtAgency().getCommitmentNo().equalsIgnoreCase(ctItemCeilingQty.getCtAgency().getCommitmentNo())) {
								ceilingQty.setCeilingQuantity(ctItemCeilingQty.getCeilingQuantity());
								break;
							}
						}
					}
				}
			}
		}
	}
	//#30428 - End
		
	// Start Fix - #30022	
	/**
	 * The method used for validating the contractadmin status.
	 * Checking process to check contractadmin status when user is inactive or
	 * move to other ptj/active in other ptj 
	 */	
	public void contractadminstatuschecking() {
		CtAmendment ctAmendment = ctBackingBean.getManagedBean().getContractVer().getCtAmendment();
		UserReqDTO userReqDTO = null;
		IUserSO userSO = null;
		UserReqDTO reqLoadDTO = null;

		if (ctAmendment.getAmendmentLoa() != null) {
			userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(ctAmendment.getAmendmentLoa().getContractAdmin().getUserId());
			userReqDTO.setOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());

			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				userReqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
				userReqDTO.setRoleCode(RoleCode.BPK_CCA.value());
			}

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ctAmendment.getAmendmentLoa().setContractAdmin(userSO.checkingcontractadminstatus(userReqDTO).getPmUser());
			if (ctAmendment.getAmendmentLoa().getContractAdmin() == null) {
				reqLoadDTO = new UserReqDTO();
				reqLoadDTO.setOrgProfileId(userReqDTO.getOrgProfileId());
				reqLoadDTO.setRoleCode(userReqDTO.getRoleCode());
				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				ctAmendment.getAmendmentLoa().setContractAdmin(userSO.checkingcontractadminstatus(reqLoadDTO).getPmUser());

			}

		}
	}
	// End Fix - #30022
	 
	 // Start Fix - #29368 / #30856
	 /**
	 * The method used to check the bond exempted condition,
	 * set bondExempted false if no attachment/letter for bond_exemption
	 * otherwise true
	 */
	public void checkandResetBondExempted() {

		if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondReq() && ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getBondExemptedFileName() == null && ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getBondExemptedFilePath() == null) {

				ctBackingBean.getManagedBean().getCtContractAmount().setBondExempted(false);
			}
		}

	}
	// End Fix - #29368 / #30856
	
	// #30879 - Start
	/**
	 * The method used to reset item price on amendment 
	 * when user changed Price on the amendment.
	 */
	public void resetItemPrice() {
		
		for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
			log.info("Check Size Price :"+item.getCtItemPrices().size());
			if(item.getCtItemPrices().size() >= 2){
				for(CtItemPrice itemPrice : item.getCtItemPrices()){
					if(itemPrice.isChkIndexOfRow()){
						itemPrice.setChkIndexOfRow(false);
						itemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
					if(itemPrice.isChangePrice()){
						getManagedBean().setCtItemPrices(itemPrice);
					}
				}
				item.getCtItemPrices().remove(getManagedBean().getCtItemPrices());
				newItemPrice(item);
			}
		}
	}
	// #30879 - End
				
	// #30879 - Start
	/**
	 * The method used to reset item quantity on amendment 
	 * when user changed Max Quantity or Ceiling Quantity on the amendment.
	 */
	public void resetItemQty() {
					
		for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {						
			if(item.getCtItemMaxQties().size() >= 2){
				for(CtItemMaxQty itemMaxQty : item.getCtItemMaxQties()){
					if(itemMaxQty.isChkIndexOfRow()){
						itemMaxQty.setChkIndexOfRow(false);
						itemMaxQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
					if(itemMaxQty.isChangeQuantity()){
						getManagedBean().setCtItemMaxQty(itemMaxQty);
					}
				}
				item.getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty());
				newItemMaxQty(item);
			}
			
			if(item.getCtItemCeilingQties().size() >= 2){
				for(CtItemCeilingQty itemCeilingQty : item.getCtItemCeilingQties()){
					if(itemCeilingQty.isChkIndexOfRow()){
						itemCeilingQty.setChkIndexOfRow(false);
						itemCeilingQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
					if(itemCeilingQty.isChangeCeilQuantity()){
						getManagedBean().setCtItemCeilingQtys(itemCeilingQty);
					}
				}
				item.getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty());
			}
		}
	}
	// #30879 - End	
	
	//Enhancement 15851 - shaqimi,putra start
	public void gotoAmendViewPage(Long contractId, long loaId) {
	
		ContractDetailsBackingBean ctDT =  new ContractDetailsBackingBean();
		ContractReqDTO reqDTO = new ContractReqDTO();
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		reqDTO.setContractId(contractId);					
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
		
		//Bug 33096 - shaqimi start
		Long amendmentContractVerId = null;
		if(!getManagedBean().getSupplementalLoaNo().equals(null)){
			amendmentContractVerId = contractSO.getAmendmentContractVerId(getManagedBean().getSupplementalLoaNo());
		}
		
		if(ctBackingBean.getManagedBean().isViewSCL() && !amendmentContractVerId.equals(null)){
			reqDTO.setContractVerId(amendmentContractVerId);
		} else {
			reqDTO.setContractVerId(ctDT.getManagedBean().getLoaContractVerId());
		}
		//Bug 33096 - shaqimi end
		
		//Bug 34142 - shaqimi start 16112022
		reqDTO.setViewOnly(true);
		getManagedBean().setSelectedTabIndex(0);
		getManagedBean().setViewOnly(true);
		
		//#15851 start
		//Resetting the managed bean
		getManagedBean().setSelectedItem(null);
		getManagedBean().setSelectedServiceItem(null);
		getManagedBean().setMemberSignOff(false);  
		getManagedBean().setPbConfirmMeeting(false);
		getManagedBean().setSignUpdateDecision(false);
		getManagedBean().setLetterOfAcceptance(false);
		getManagedBean().setPrepareLOA(false);
		getManagedBean().setDecisionPbQcLOA(false);
		getManagedBean().setSignOffDecision(false);
		//#15851 end
		
		if(getManagedBean().getCtLetterOfAcceptance() != null){
			getManagedBean().getCtLetterOfAcceptance().clear();
		}
		setAmendmentContractVer(reqDTO);
		setCtContractAmountForViewSL(); //#38823
					
//		if(ctBackingBean.getManagedBean().isViewContractFromAgreement()){
			ctBackingBean.getManagedBean().setViewContract(true);
//		}
			
		//retrieve approver from CtActor
		String roleCode = null;
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			roleCode = RoleCode.CT_APPROVER.value();
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
			roleCode = RoleCode.BPK_CC_APPROVER.value();
		}
		CtActor approverActor = retrieveContractActor(ctBackingBean.getManagedBean().getContractVer().getContractVerId(), ctBackingBean.getManagedBean().getContractVer().getDocType(), roleCode);
		if (approverActor != null) {
			getManagedBean().setApproverName(approverActor.getUserName());
		}

		//Bug 34142 - shaqimi start 16112022
					
		//for loa header
		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		AwardSupplierReqDTO reqQtDTO = new AwardSupplierReqDTO();
		reqQtDTO.setLoaId(loaId);
		reqQtDTO.setScLoa(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa());
		AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOAHeader(reqQtDTO);
		if (resDTO.getScLoa().getScQt() != null) {
			ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().setScQt(resDTO.getScLoa().getScQt());
		}

		if (resDTO.getScLoa().getScQuote() != null) {
			ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().setScQuote(resDTO.getScLoa().getScQuote());
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PROCESS_AMEND_PAGE);
	}
	//Enhancement 15851 - shaqimi,putra end
	
	//Start Fix - #33256
	public void sortItemMaxQty (List<CtItemMaxQty> list){
		if(list!=null){
			CustomUtils.sort(list, new Comparator<CtItemMaxQty>() {	//#36726
				public int compare(CtItemMaxQty l1, CtItemMaxQty l2) {
					return l1.getExpDate().compareTo(l2.getExpDate());
				}	
			});
		}
	}
	
	public void sortItemPrice (List<CtItemPrice> list){
		if(list!=null){
			CustomUtils.sort(list, new Comparator<CtItemPrice>() { //#36726
				public int compare(CtItemPrice l1, CtItemPrice l2) {
					return l1.getExpDate().compareTo(l2.getExpDate());
				}
			});
		}
	}	
	// End Fix - #33256
	
	// Start Fix - #33674
	public String[] splittedAccNoAndBankname() {
		String bankDetails = ctBackingBean.getManagedBean().getBankInfoDetails();
		String[] bankDetailsSplitted = null;
		if (bankDetails != null) {
			if (bankDetails.length() > 30 || bankDetails.contains(ContractConst.SPACE_SLASH)) {
				bankDetailsSplitted = bankDetails.split(ContractConst.SPACE_SLASH);
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAccountNo(bankDetailsSplitted[0]);
				ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankName(bankDetailsSplitted[1]);
			}
		}
		return bankDetailsSplitted;
	}

	public void setBankDetails() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() != null) {
			String account = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName() != null) {
				account = account + ContractConst.SPACE_SLASH + ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName();
			}
			ctBackingBean.getManagedBean().setBankInfoDetails(account);
		}
	}
	// End Fix - #33674
	
	// Start Fix - #34110 , #34950
	public void sortItemPriceForTier(List<CtItemPrice> list) {
		CustomUtils.sort(list, new Comparator<CtItemPrice>() { //#36726
			public int compare(CtItemPrice o1, CtItemPrice o2) {

				BigDecimal z1 = o1.getFromQty();
				BigDecimal z2 = o2.getFromQty();
				if (z1 != null && z2 != null) {
					int fromQtyComp = z1.compareTo(z2);
					if (fromQtyComp != 0) {
						return fromQtyComp;
					}

				}

				BigDecimal y1 = o1.getToQty();
				BigDecimal y2 = o2.getToQty();
				if (y1 != null && y2 != null) {
					int toQtyComp = y1.compareTo(y2);
					if (toQtyComp != 0) {
						return toQtyComp;
					}
				}

				Date d1 = o1.getExpDate();
				Date d2 = o2.getExpDate();
				return d1.compareTo(d2);
			}
		});
	}
	
	public void sortItemPriceForZonalTier(List<CtItemPrice> list) {
		CustomUtils.sort(list, new Comparator<CtItemPrice>() { //#36726
			public int compare(CtItemPrice o1, CtItemPrice o2) {
				String x1 = o1.getCtContractZone().getZoneName();
				String x2 = o2.getCtContractZone().getZoneName();
				int zoneComp = x1.compareTo(x2);
				if (zoneComp != 0) {
					return zoneComp;
				}

				BigDecimal z1 = o1.getFromQty();
				BigDecimal z2 = o2.getFromQty();
				if (z1 != null && z2 != null) {
					int fromQtyComp = z1.compareTo(z2);
					if (fromQtyComp != 0) {
						return fromQtyComp;
					}
				}

				BigDecimal y1 = o1.getToQty();
				BigDecimal y2 = o2.getToQty();
				if (y1 != null && y2 != null) {
					int toQtyComp = y1.compareTo(y2);
					if (toQtyComp != 0) {
						return toQtyComp;
					}
				}

				Date d1 = o1.getExpDate();
				Date d2 = o2.getExpDate();
				return d1.compareTo(d2);
			}
		});
	}
	
	public void sortItemMaxQtyForZonal (List<CtItemMaxQty> list){
		if(list!=null){
			CustomUtils.sort(list, new Comparator<CtItemMaxQty>() {	//#36726
				public int compare(CtItemMaxQty o1, CtItemMaxQty o2) {
					String x1 = o1.getCtContractZone().getZoneName();
					String x2 = o2.getCtContractZone().getZoneName();
					int zoneComp = x1.compareTo(x2);
					if (zoneComp != 0) {
						return zoneComp;
					}
					Date d1 = o1.getExpDate();
					Date d2 = o2.getExpDate();
					return d1.compareTo(d2);
				}	
			});
		}
	}
	
	public void sortItemPriceForZonal(List<CtItemPrice> list) {
		CustomUtils.sort(list, new Comparator<CtItemPrice>() { //#36726
			public int compare(CtItemPrice o1, CtItemPrice o2) {

				String x1 = o1.getCtContractZone().getZoneName();
				String x2 = o2.getCtContractZone().getZoneName();
				int zoneComp = x1.compareTo(x2);
				if (zoneComp != 0) {
					return zoneComp;
				}
				Date d1 = o1.getExpDate();
				Date d2 = o2.getExpDate();
				return d1.compareTo(d2);
			}
		});
	}
	// End Fix - #34110 , #34950
	
	// Start Fix - #34539
	public void changeShowItem() {
		try {
			if (ctBackingBean.getManagedBean().getShowItem() != null) {
				if (ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_QTY)) {
					refreshAgencyCeilingQuantityList();
				} else if (ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
					refreshAgencyCeilingQuantityList();
				} else if (ctBackingBean.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
					refreshAgencyCeilingQuantityList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method used to refresh the ceiling quantity information of Product
	 * details listing in UI.
	 */
	private void refreshAgencyCeilingQuantityList() {

		Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
		for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
		}

		getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		// getManagedBean().getServiceselectedItem().getCtItemCeilingQties().clear();
		for (CtAgency ctAgency : ctBackingBean.getManagedBean().getContractVer().getCtAgencies()) {
			if (!itemCeilingQtyMap.containsKey(ctAgency.getOrgProfile().getOrgProfileId())) {

				CtItemCeilingQty ctItemCeilingQty = new CtItemCeilingQty();
				ctItemCeilingQty.setCtAgency(ctAgency);
				ctItemCeilingQty.setCtContractItem(getManagedBean().getSelectedItem());

				ctItemCeilingQty.setCeilingQuantity(new BigDecimal(0));
				ctItemCeilingQty.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				ctItemCeilingQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
				getManagedBean().getSelectedItem().getCtItemCeilingQties().add(ctItemCeilingQty);

			} else {
				getManagedBean().getSelectedItem().getCtItemCeilingQties().add(itemCeilingQtyMap.get(ctAgency.getOrgProfile().getOrgProfileId()));

			}
		}
	}
	// End Fix - #34539
	
	// Start Fix - #33287
	private void amendmentRevert(Long currentAmendmentVerID) {

		IContractAmendmentSO contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		ContractAmendmentReqDTO amendReqDTO = new ContractAmendmentReqDTO();

		int updateCheck = 0;
		if (isUntickContractExtension
				&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() || ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange())) {
			updateCheck = 1;
		}

		amendReqDTO.setUpdateStatusCheck(updateCheck);
		amendReqDTO.setContractVerId(currentAmendmentVerID);
		ContractAmendmentResDTO resDTO = contractAmendmentSO.amendmentRevertSO(amendReqDTO);
		if (resDTO.isSuccessful()) {
			log.info(" Done !!! ");
		}
	}

	// End Fix - #33287
	
	// Start Fix - #34110
	/**
	 * The method used to pick-up new item price.
	 * 
	 * @param ctItemPrice
	 *            object of CtItemPrice item
	 */
	public void onChangeCtItemPrice(CtItemPrice ctItemPrice) {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
			getManagedBean().getSelectedChangeItemPrice().put(ctItemPrice.getItemPriceId(), ctItemPrice);
		}
	}

	/**
	 * The method used to pick-up new item max qty.
	 * 
	 * @param ctItemPrice
	 *            object of CtItemMaxQty item
	 */
	public void onChangeCtItemMaxQty(CtItemMaxQty ctItemPMaxQty) {
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				getManagedBean().getSelectedChangeItemQuantity().put(ctItemPMaxQty.getItemMaxQtyId(), ctItemPMaxQty);
		}

	}
	// End Fix - #34110
	
	//#13847 start
	/**
	 * Method to check all the attachment of the bond waiver (active, in progress and pending activation)
	 * and grouping the bond waiver into latest and history to show on the screen which one is the history.
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	private void chkBondWaivedAttachment() {
		try {
			resetBondWaiverDetails();

			if (CollectionUtils.isNotEmpty(ctBackingBean.getManagedBean().getContractVer().getCtBondWaiverDetails())) {
				List<CtBondWaiverDetail> bondWaiverDetails = ctBackingBean.getManagedBean().getContractVer()
						.getCtBondWaiverDetails();
				List<CtAttachment> bondWaiverAttachments = ctBackingBean.getManagedBean().getContractVer()
						.getBondWaiverAttachments();

				for (CtBondWaiverDetail waiverDetail : bondWaiverDetails) {
					updateWaiverDetailAttachment(waiverDetail, bondWaiverAttachments);
					processWaiverDetail(waiverDetail);
				}
				sortBondWaiverDefermentHistory();
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					ContractConst.SLA_AMENDMENTBACKINGBEAN,
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
					ContractConst.SLA_CHK_BOND_WAIVED_ATTACHMENT, getUserIdSessionFromLoginId(), e);
		}
	}
	
	/**
	 * Resets the latest bond waiver and history.
	 */
	private void resetBondWaiverDetails() {
		ctBackingBean.getManagedBean().getContractVer().setBondWaiverDefermentLatest(null);
		ctBackingBean.getManagedBean().getContractVer().setBondWaiverDefermentHistory(null);
	}
	
	/**
	 * Updates the attachment for the given waiver detail based on the available bond waiver attachments.
	 *
	 * @param waiverDetail The bond waiver detail to update.
	 * @param bondWaiverAttachments The list of available bond waiver attachments.
	 */
	private void updateWaiverDetailAttachment(CtBondWaiverDetail waiverDetail, List<CtAttachment> bondWaiverAttachments) {
		waiverDetail.setCtAttachment(new CtAttachment());

		if (CollectionUtils.isNotEmpty(bondWaiverAttachments)) {
			if (waiverDetail.getAttachmentId() != null) {
				setAttachmentById(waiverDetail, bondWaiverAttachments);
			} else if (waiverDetail.getTempAttachmentId() != null) {
				setAttachmentByTempId(waiverDetail, bondWaiverAttachments);
			}
		}
	}

	/**
	 * Sets the attachment for the waiver detail based on the attachment ID.
	 *
	 * @param waiverDetail The bond waiver detail to update.
	 * @param bondWaiverAttachments The list of available bond waiver attachments.
	 * @param attachmentId The ID of the attachment to set.
	 */
	private void setAttachmentById(CtBondWaiverDetail waiverDetail, List<CtAttachment> bondWaiverAttachments) {
		for (CtAttachment waiverAttach : bondWaiverAttachments) {
			if (waiverDetail.getAttachmentId().equals(waiverAttach.getAttachmentId())) {
				waiverDetail.setCtAttachment(waiverAttach);
				break;
			}
		}
	}

	/**
	 * Sets the attachment for the waiver detail based on the temporary attachment ID.
	 *
	 * @param waiverDetail The bond waiver detail to update.
	 * @param bondWaiverAttachments The list of available bond waiver attachments.
	 * @param tempAttachmentId The temporary ID of the attachment to set.
	 */
	private void setAttachmentByTempId(CtBondWaiverDetail waiverDetail, List<CtAttachment> bondWaiverAttachments) {
		for (CtAttachment waiverAttach : bondWaiverAttachments) {
			if (waiverDetail.getTempAttachmentId().equals(waiverAttach.getTempAttachmentId())) {
				waiverDetail.setCtAttachment(waiverAttach);
				break;
			}
		}
	}

	/**
	 * Processes the given waiver detail and updates the latest bond waiver (In
	 * Progress) or history (Active/Pending Active).
	 * 
	 * @param waiverDetail The bond waiver detail to process.
	 */
	private void processWaiverDetail(CtBondWaiverDetail waiverDetail) {
		if (waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)) {
			setLatestBondWaiver(waiverDetail);
		} else if (waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)
				|| waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
			addToBondWaiverHistory(waiverDetail);
		}
	}

	/**
	 * Sets the latest bond waiver detail.
	 *
	 * @param waiverDetail The bond waiver detail to set as the latest.
	 */
	private void setLatestBondWaiver(CtBondWaiverDetail waiverDetail) {
		if (ctBackingBean.getManagedBean().getContractVer().getBondWaiverDefermentLatest() == null) {
			ctBackingBean.getManagedBean().getContractVer().setBondWaiverDefermentLatest(new CtBondWaiverDetail());
		}
		ctBackingBean.getManagedBean().getContractVer().setBondWaiverDefermentLatest(waiverDetail);
	}

	/**
	 * Adds the given waiver detail to the bond waiver deferment history.
	 *
	 * @param waiverDetail The bond waiver detail to add.
	 */
	private void addToBondWaiverHistory(CtBondWaiverDetail waiverDetail) {
		if (ctBackingBean.getManagedBean().getContractVer().getBondWaiverDefermentHistory() == null) {
			ctBackingBean.getManagedBean().getContractVer().setBondWaiverDefermentHistory(new ArrayList<CtBondWaiverDetail>());
		}
		ctBackingBean.getManagedBean().getContractVer().getBondWaiverDefermentHistory().add(waiverDetail);
	}
	
	/**
	 * Sorts the bond waiver deferment history by waiver start date in descending order.
	 */
	private void sortBondWaiverDefermentHistory() {
		List<CtBondWaiverDetail> defermentHistory = ctBackingBean.getManagedBean().getContractVer().getBondWaiverDefermentHistory();
		if (CollectionUtils.isNotEmpty(defermentHistory)) {
			Collections.sort(defermentHistory, new Comparator<CtBondWaiverDetail>() {
				@Override
				public int compare(CtBondWaiverDetail bwd1, CtBondWaiverDetail bwd2) {
					if (bwd1.getWaiverStartDate() != null && bwd2.getWaiverStartDate() != null) {
						return bwd2.getWaiverStartDate().compareTo(bwd1.getWaiverStartDate());
					}
					return 0;
				}
			});
		}
	}
	//#13847 end
	
	// #25956 - Start
	/**
	 * The method used to check this process is cancellation.
	 * 
	 * @return true, if is cancellation process. false, if is not cancellation
	 *         process.
	 */
	public boolean isCancelOrRejectProcess(String action) {
		if ((action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) 
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) 
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE))
				|| (ContractStatusConst.FA_PENDING_REJECTION_ACKNOWLEDGEMENT.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId()))
				|| (ContractStatusConst.FA_PENDING_CANCELLATION_ACKNOWLEDGEMENT.equals(ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId()))) {
			return true;
		} else {
			return false;
		}
	}
	// #25956 - End
	
	// #34097/#26186 start
	/**
	 * Method to check supplier for Cancelled Category Code/ Cancelled Category Related to Manufacturing
	 * and Cancelled Bumiputera Status
	 */
	public void checkSupplierDataMaintenance() {
		try {
			SmSupplier supplier = getSupplier();
			
			if(!this.getManagedBean().isDisciplinaryActionCancel()){
				checkSupplierBumiputera(supplier);
				if(!this.getManagedBean().isSupplierBumiCancel()){
					checkSupplierCategory(supplier);
				}
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkSupplierDataMaintenance",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
					"checkSupplierDataMaintenance", getUserIdSessionFromLoginId(), e);
		}
	}
	
	/**
	 * Method to get current supplier
	 * 
	 * @return the supplier for the current contract 
	 */
	public SmSupplier getSupplier(){
		return StringUtils.isNotBlank(EntityHelper.getStringNestedProperty(
				ctBackingBean.getManagedBean(), "contractVer.ctContract.smSupplier")) ? ctBackingBean
				.getManagedBean().getContractVer().getCtContract().getSmSupplier() : SmApplicationUtil
				.getCurrentSupplier();
	}

	/**
	 * Method to check supplier for Cancelled Category Code/ Cancelled Category Related to Manufacturing
	 * 
	 * @param supplier the supplier for the current contract
	 */
	public void checkSupplierCategory(SmSupplier supplier) {
		ISupplierDataMaintenanceSO suppDataSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
		try {
			this.getManagedBean().setSupplierMsg(StringUtils.EMPTY);
			StringBuilder sb = new StringBuilder();

			List<SmSupplierCategory> suppCategory = suppDataSO.nonDisciplinaryActionCancelCategoryCode(supplier
					.getSupplierId());
			if(CollectionUtils.isNotEmpty(suppCategory)){
				for (SmSupplierCategory supCat : suppCategory) {
					appendSupplierCategoryMsg(sb, supCat);
				}
			}

			if (StringUtils.isNotBlank(sb.toString())) {
				sb.insert(0, "(");
			    sb.append(")");
				String[] params = new String[] { sb.toString() };
				this.getManagedBean().setSupplierMsg(
						ResourceBundleUtils.getMessage("lbl.ct.amend.supplier.maintenance.cancel.category", params,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkSupplierCategory",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(), "checkSupplierCategory",
					getUserIdSessionFromLoginId(), e);
		}
	}
	
	/**
	 * Method to append message contain category code for supplier.
	 * 
	 * @param sb The stringbuilder of the message
	 * @param supCat The list of the category code of the supplier
	 */
	private void appendSupplierCategoryMsg(StringBuilder sb, SmSupplierCategory supCat) {
		if(StringUtils.isNotBlank(sb.toString())){
			sb.append(", ");
		}
		sb.append(supCat.getCategoryL1Name().trim());
	    sb.append(", ");
	    sb.append(supCat.getCategoryL2Name().trim());
	    sb.append(", ");
	    sb.append(supCat.getCategoryL3Name().trim());
	}

	/**
	 * Method to check supplier for Cancelled Bumiputera Status
	 * 
	 * @param supplier the supplier for the current contract
	 */
	public void checkSupplierBumiputera(SmSupplier supplier) {
		ISupplierDataMaintenanceSO suppDataSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);
		IQTDetailSO iQtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		try {
			boolean suppBumiputera = suppDataSO.nonDisciplinaryActionCancelBumiputera(supplier.getSupplierId());
			boolean openFor = iQtDetailSO.getQTOpenForBumiByContractNo(ctBackingBean.getManagedBean().getContractVer().getCtContract()
					.getContractNo());

			if (!suppBumiputera && openFor) {
				this.getManagedBean().setSupplierBumiCancel(true);
				if (StringUtils.isNotBlank(this.getManagedBean().getDisciplinaryActionMsg())) {
					this.getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);
				}
				handleMessageSupplierMaintenance();
			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkSupplierBumiputera",
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo(),
					"checkSupplierBumiputera", getUserIdSessionFromLoginId(), e);
		}
	}
	
	/**
	 * Method to handle warning message according to button/status due to cancelled Bumiputera
	 */
	public void handleMessageSupplierMaintenance() {

		String msgThrow = null;
		Long statusId = ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId();
		String[] outcome = new String[getManagedBean().getOutComes().length];

		String[] strArray = handleWIPTransactionButton(statusId);
		List<String> listButton = new ArrayList<>(Arrays.asList(strArray));

		if ((statusId.equals(ContractStatusConst.FA_PENDING_SIGN_OFF) || 
				statusId.equals(ContractStatusConst.FA_PENDING_CHAIRPERSON_SIGN_OFF))
				&& listButton.contains(BpmOutcomeConst.BPM_REVISE)) {
			outcome = new String[] { BpmOutcomeConst.BPM_REVISE };
			msgThrow = MessageConst.ERROR_750087;

		} else if (listButton.contains(BpmOutcomeConst.BPM_CANCEL)
				|| statusId.equals(ContractStatusConst.FA_PENDING_REVISION)) {
			outcome = new String[] { BpmOutcomeConst.BPM_CANCEL };
			msgThrow = MessageConst.ERROR_750085;
		} else if (listButton.contains(BpmOutcomeConst.BPM_REJECT)) {
			outcome = new String[] { BpmOutcomeConst.BPM_REJECT };
			msgThrow = MessageConst.ERROR_750086;
		}

		if (msgThrow != null) {
			getManagedBean().setOutComes(outcome);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(msgThrow,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
	}
	
	/**
	 * This method to handle WIP transaction for DA and Supplier Bumi Cancelled 
	 * that doesnt have button reject/cancel due to the old process.
	 * And to include the reject/cancel button and submit to BPM API
	 * 
	 * @param statusId The status of the current contract
	 * @return The outcomes that has been set contain the reject/cancel button based on the status
	 */
	public String[] handleWIPTransactionButton(Long statusId) {
		String[] strButton = this.getManagedBean().getOutComes();
		List<String> listButton = new ArrayList<>(Arrays.asList(strButton));
		boolean condition = false;

		if (!listButton.contains(BpmOutcomeConst.BPM_CANCEL) && CANCEL_STATUS_CONST.contains(statusId)) {
			listButton.add(BpmOutcomeConst.BPM_CANCEL);
			condition = true;
		} else if (!listButton.contains(BpmOutcomeConst.BPM_REJECT) && REJECT_STATUS_CONST.contains(statusId)) {
			listButton.add(BpmOutcomeConst.BPM_REJECT);
			condition = true;
		} else if (!listButton.contains(BpmOutcomeConst.BPM_REVISE) &&
				(statusId.equals(ContractStatusConst.FA_PENDING_SIGN_OFF) 
				|| statusId.equals(ContractStatusConst.FA_PENDING_CHAIRPERSON_SIGN_OFF))){
			listButton.add(BpmOutcomeConst.BPM_REVISE);
			condition = true;
		}

		if(condition){
			strButton = listButton.toArray(new String[0]);
			this.getManagedBean().setOutComes(null);
			this.getManagedBean().setOutComes(strButton);
			this.getManagedBean().setWipButtonCancelReject(true);
		}
		return getManagedBean().getOutComes();
	}

	/**
	 * Retrieves and handle the appropriate acknowledge message for the
	 * acknowledge page based on the outcome and the state of the managed bean.
	 * 
	 * @param outcome
	 *            The outcome string that determines the message to return.
	 * @return The corresponding message constant based on the outcome and
	 *         conditions, or null if no match is found.
	 */
	private String handleAcknowledgeMessageDAandBumiCancel(String outcome) {
		if (this.getManagedBean().isDisciplinaryActionCancel()) {
			return getDisciplinaryActionCancelAckMessage(outcome);
		} else if (this.getManagedBean().isSupplierBumiCancel()) {
			return getSupplierBumiCancelAckMessage(outcome);
		}
		return null;
	}

	/**
	 * Retrieves the acknowledge message for disciplinary action cancellation
	 * based on the outcome.
	 * 
	 * @param outcome
	 *            The outcome string that determines the message to return.
	 * @return The corresponding message constant for disciplinary action
	 *         cancellation, or null if no match is found.
	 */
	private String getDisciplinaryActionCancelAckMessage(String outcome) {
		switch (outcome.toUpperCase()) {
			case BpmOutcomeConst.BPM_ACKNOWLEDGE:
				return MessageConst.INFO_700102;
			case BpmOutcomeConst.BPM_REJECT:
				return MessageConst.INFO_700103;
			case BpmOutcomeConst.BPM_REVISE:
				return MessageConst.INFO_700104;
			case BpmOutcomeConst.BPM_CANCEL:
				return MessageConst.INFO_700105;
			default:
				return null;
		}
	}

	/**
	 * Retrieves the acknowledge message for supplier Bumi cancellation based on the outcome.
	 * 
	 * @param outcome The outcome string that determines the message to return.
	 * @return The corresponding message constant for supplier Bumi cancellation, or null if no match is found.
	 */
	private String getSupplierBumiCancelAckMessage(String outcome) {
		switch (outcome.toUpperCase()) {
			case BpmOutcomeConst.BPM_ACKNOWLEDGE:
				return MessageConst.INFO_700122;
			case BpmOutcomeConst.BPM_REJECT:
				return MessageConst.INFO_700123;
			case BpmOutcomeConst.BPM_REVISE:
				return MessageConst.INFO_700124;
			case BpmOutcomeConst.BPM_CANCEL:
				return MessageConst.INFO_700125;
			default:
				return null;
		}
	}
	
	/**
	 * Method to get constant for contract cancellation. Disciplinary Action
	 * Cancel is much higher status compare to Supplier Bumiputera Cancel. If
	 * contract have both DA Cancel and Bumi Cancel, will return DA Cancel.
	 * 
	 * @return The type of cancellation (e.g., BUMI or DA).
	 */
	public String getContractCancelType() {
		acknowledgedValidation();
		if (this.getManagedBean().isDisciplinaryActionCancel()) {
			return ContractConst.CANCEL_DA;
		} else if (this.getManagedBean().isSupplierBumiCancel()) {
			return ContractConst.CANCEL_BUMI;
		}
		return null;
	}
	
	/**
	 * Method to check when no DA cancel and no Bumi Cancel during acknowledged
	 * after has been cancel by DA/Bumi
	 */
	public void acknowledgedValidation() {
		Long statusId = ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId();
		if (!this.getManagedBean().isDisciplinaryActionCancel() && !this.getManagedBean().isSupplierBumiCancel()) {
			if (statusId.equals(ContractStatusConst.FA_DISCIPLINARY_PENDING_CANCELLATION_ACKNOWLEDGEMENT)) {
				this.getManagedBean().setDisciplinaryActionCancel(true);
			} else if (statusId.equals(ContractStatusConst.FA_BUMI_PENDING_CANCELLATION_ACKNOWLEDGEMENT)) {
				this.getManagedBean().setSupplierBumiCancel(true);
			}
		}
	}
	
	/**
	 * Method to check BPM composite version. If the minimum version in DB is
	 * much lower than current version, then it will set oldBPMVersion as true.
	 * 
	 * @param bpmCurrentVer
	 */
	public void checkBPMVersion(String bpmCurrentVer) {
		getManagedBean().setOldBPMVersion(false);
		try {
			ICompositeValiditySO compositeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);

			CompositeValidityReqDTO cReqDTO = new CompositeValidityReqDTO();
			cReqDTO.setCompositeValidityId(ContractConst.COMPOSITE_VALIDITY_ID_CANCEL_BUTTON);

			String minCompositeVer = compositeSO.getCompositeByValidityId(cReqDTO).getPmCompositeValidity()
					.getMinCompositeVersion();

			if (bpmCurrentVer != null && CustomUtils.compositeVersionComparator(bpmCurrentVer, minCompositeVer) < 0) {
				getManagedBean().setOldBPMVersion(true);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "checkBPMVersion", null,
					"checkBPMVersion", getUserIdSessionFromLoginId(), e);
		}
	}
	
	// #34097/#26186 end
	
	//#38823 start
	/**
	 * re-setting CtContractAmount in CtContractVer for ViewSCL to show previous
	 * CtContractAmount amendment details
	 */
	public void setCtContractAmountForViewSL(){
		Date asAtDate = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate();
		for (CtContractAmount ctContractAmount : ctBackingBean.getManagedBean().getContractVer().getCtContractAmounts()) {
			if (ctContractAmount.getEffDate().getTime() <= asAtDate.getTime()
					&& ctContractAmount.getExpDate().getTime() >= asAtDate.getTime()) {
				ctBackingBean.getManagedBean().getContractVer().setCtContractAmount(ctContractAmount);
			}
		}
	}
	//#38823 end
	
	//#24333 - Start
	/**
	 * Determines the primary contract amount to use.
	 * <p>
	 * The method returns the first active contract amount found in the list.
	 * If no active contract is found, it defaults to the last one in the list.
	 * </p>
	 *
	 * @param ctAmounts The list of contract amounts. Must not be null or empty.
	 * @return The selected {@link CtContractAmount}.
	 */
	private CtContractAmount getSelectedContractAmount(List<CtContractAmount> ctAmounts) {
	    // Find the first active contract amount
	    for (CtContractAmount ctAmount : ctAmounts) {
	        if (isContractActive(ctAmount, new Date())) {
	            return ctAmount;
	        }
	    }
	    // If no active contract is found, return the last one
	    return ctAmounts.get(ctAmounts.size() - 1);
	}
	
	/**
	 * Checks if a contract is active on a current date.
	 *
	 * @param contractAmt The contract to validate.
	 * @param now The date to check against.
	 * @return {@code true} if active; {@code false} otherwise.
	 */
	private boolean isContractActive(CtContractAmount contractAmt, Date now) {
	    if (contractAmt == null || contractAmt.getEffDate() == null || contractAmt.getExpDate() == null) {
	        return false;
	    }
	    return !contractAmt.getEffDate().after(now) && !contractAmt.getExpDate().before(now);
	}
	//#24333 - End
	
	}