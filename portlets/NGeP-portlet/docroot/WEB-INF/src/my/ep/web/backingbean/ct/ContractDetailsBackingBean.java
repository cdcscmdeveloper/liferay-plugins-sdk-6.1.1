/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;


import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet; //#30014
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.itemsummary.IFlItemSummarySO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.ct.audit.AuditContractConst; //35506
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.ct.audit.AuditContractReqDTO; //35506
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.itemsummary.FlItemSummaryReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtBondWaiverDetail;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract; //Fix - #30866
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
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
import my.ep.persistence.entity.pm.common.PmFinancialOrg;//24333
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc; //Fix - #30866
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.audit.AuditContractBackingBean; //35506
import my.ep.web.backingbean.ct.circular.CircularBackingBean; //Fix - #30866
import my.ep.web.backingbean.ct.request.ctadmin.RequestCtAdminBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.pm.helper.StatusHelper; //Fix - #30866

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;




/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean (name = "contractDetailsBackingBean") 
@ViewScoped
public class ContractDetailsBackingBean extends ContractBaseBackingBean<ContractDetailsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml?faces-redirect=true";
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String CONTRACT_COMMON_PAGE = "/xhtml/ct/viewcontract/fulfilmentTab.xhtml?faces-redirect=true";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml?faces-redirect=true";
	private static final String CHANGE_BANK_ACCOUNT_PAGE = "/xhtml/ct/supplier/bank/changeBankAccount.xhtml?faces-redirect=true";
	private static final String REQUEST_FACTORING_PAGE = "/xhtml/ct/supplier/factoring/requestOfFactoring.xhtml?faces-redirect=true";

	private static final String PROCESS_OWNER_MANAGEMENT_PAGE = "/xhtml/ct/ownermanagement/ownerManagement.xhtml?faces-redirect=true";
//	/*#21387-basri start*/
	private static final String REQUEST_CONTRACT_ADMIN = "/xhtml/ct/fulfilmentdetails/contractadmin/requestContractAdmin.xhtml?faces-redirect=true";/*#21387-basri*/
//	/*#21387-basri end*/
	//29551 - start
	private static final String DEDUCTION_CLAUSE_PAGE = "/xhtml/fl/deductionsearch/search.xhtml?faces-redirect=true";
	private static final String CONTRACT_VIEW_AUDIT = "/xhtml/ct/viewcontract/resultViewAudit.xhtml?faces-redirect=true"; // #30866
	//29551 - end
	private String proceedForFulfilmentMsg;
	private ContractBackingBean ctBB = null;
	private CtAgency selectedRecommendedAgency;
	
	//#13847 start
	/**
	 * initialize mapping to contain status for approval for bond waiver/deferment
	 */
	private static final Map<Integer, Long> approvalStatus = new HashMap<>();
	static{
		Integer i = 0;
		approvalStatus.put(i++, ContractStatusConst.FD_PENDING_APPROVAL);
		approvalStatus.put(i++, ContractStatusConst.FD_PENDING_REVISION_APPROVAL);
		approvalStatus.put(i++, ContractStatusConst.FU_PENDING_APPROVAL);
		approvalStatus.put(i++, ContractStatusConst.FU_PENDING_REVISION_APPROVAL);
		approvalStatus.put(i++, ContractStatusConst.FW_PENDING_APPROVAL);
		approvalStatus.put(i++, ContractStatusConst.FW_PENDING_REVISION_APPROVAL);
	}
	//#13847 end

	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public ContractDetailsBackingBean() {
		/* Initialization of button */
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}

		if (getManagedBean().getOutComes() == null) {
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBB.getManagedBean().setContractVer(new CtContractVer());
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	}

	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(),  new ArrayList<PmUser>()));
		}
	}

	/**
	 * @return the proceedForFulfilmentMsg
	 */
	public String getProceedForFulfilmentMsg() {
		return proceedForFulfilmentMsg;
	}

	/**
	 * @param proceedForFulfilmentMsg the proceedForFulfilmentMsg to set
	 */
	public void setProceedForFulfilmentMsg(String proceedForFulfilmentMsg) {
		this.proceedForFulfilmentMsg = proceedForFulfilmentMsg;
	}

	/**
	 * The method used for searching the contract.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchContract() throws Exception {
		if (StringUtils.isBlank(getManagedBean().getSearchContractNo())
				&& StringUtils.isBlank(getManagedBean().getSearchContractName()) 
				&& StringUtils.isBlank(getManagedBean().getSearchCircularNo()) 
				&& StringUtils.isBlank(getManagedBean().getSearchCircularName()) 
				&& StringUtils.isBlank(getManagedBean().getSearchLoaNo()) 
				&& StringUtils.isBlank(getManagedBean().getSearchSupplierName()) 
				&& getManagedBean().getSearchCtStartDate() == null
				&& getManagedBean().getSearchCtEndDate() == null 
				&& StringUtils.isBlank(getManagedBean().getSearchCtPhysicalNo()) 
				&& StringUtils.isBlank(getManagedBean().getSearchLoaPhysicalNo()) 
				&& getManagedBean().getSearchStatus() == 0 
				&& getManagedBean().getSearchTerminationType() == null
				&& (getManagedBean().getSearchContractStatus() == 0 && !isSupplierRole())
				&& getManagedBean().getSearchMinistryProfile() == null
				&& getManagedBean().getSearchPtjGroupProfile() == null
				&& getManagedBean().getSearchPtjProfile() == null) {
			throw new EpException(MessageConst.ERROR_100060);
		} else {
			getManagedBean().getContractDataModel().reloadData();

		}
	}

	/**
	 * The method used to reset the search criteria for contract.
	 */
	public void searchReset() {
		this.getManagedBean().setSearchContractNo(null);
		this.getManagedBean().setSearchContractName(null);	
		this.getManagedBean().setSearchCircularNo(null);	
		this.getManagedBean().setSearchCircularName(null);	
		this.getManagedBean().setSearchLoaNo(null);	
		this.getManagedBean().setSearchSupplierName(null);	
		this.getManagedBean().setSearchCtStartDate(null);	
		this.getManagedBean().setSearchCtEndDate(null);	
		this.getManagedBean().setSearchCtPhysicalNo(null);	
		this.getManagedBean().setSearchLoaPhysicalNo(null);
		this.getManagedBean().setSearchStatus(0);
		this.getManagedBean().setSearchTerminationType(null);
		this.getManagedBean().setSearchContractStatus(0);
		defaultMinistryPtjSearchValue();
	}

	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract search screen.
	 */
	public void preRenderSearch(boolean trackingDiarySearch) {
		renewSesionObject();
		defaultMinistryPtjSearchValue();
		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
		// Start - #30866
		String fullViewRootPath = "";
		FacesContext fc = FacesContext.getCurrentInstance();
		fullViewRootPath = fc.getViewRoot().getViewId().toString();
		if (fullViewRootPath.contains("trackingDiarySearch.xhtml")) {
			getManagedBean().setContinuityCircularAuditSearch(true);
		}
		// End - #30866
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_MAINT_PAGE);
	}

	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract details screen.
	 */
	public void preRenderContract() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi 06/12/2022
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_PAGE);
	}

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		
		try {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");

			// Bug 34142, 15851 - shaqimi start 04/12/2022
			if (taskId != null) {
				ctBB.getManagedBean().setInPage(false);
			}
			if (!ctBB.getManagedBean().isInPage()) {
				if (ctBB.getManagedBean().isBackFromSCL()) {
					getManagedBean().setSelectedTabIndex(0);
					ctBB.getManagedBean().setBackFromSCL(false);

					if (ctBB.getManagedBean().getContractVerIdFromAgreementTaskList() != null) {
						ContractReqDTO reqDTO = new ContractReqDTO();
						reqDTO.setContractVerId(ctBB.getManagedBean().getContractVerIdFromAgreementTaskList());
						reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
						reqDTO.setViewOnly(true);

						setValidateContractField(reqDTO);
					}
				}

				if (!ctBB.getManagedBean().isViewContractFromAgreement()) {
					if (taskId != null && !taskId.isEmpty()) {
						ctBB.getManagedBean().setDocTypeFromTaskList(null);
						ctBB.getManagedBean().setTaskIdFromTaskList(taskId);
					} else {
						if (ctBB.getManagedBean().getTaskIdFromTaskList() != null && !ctBB.getManagedBean().getTaskIdFromTaskList().isEmpty()) {
							taskId = ctBB.getManagedBean().getTaskIdFromTaskList();
						}
					}
				}
				ctBB.getManagedBean().setInPage(true);
			}
			// Bug 34142, 15851 - shaqimi end 04/12/2022

			if (taskId == null) {
				// #35156 - Start
				// log.info("**Start preRenderView from Maintenance** Check Bug #35156 :"+" ContractVer :"+ctBB.getManagedBean().getContractVer().getContractVerId()
				// +", Contract No :"+ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
				if (getManagedBean().getWarningPercentage() != null) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.INFO_745008, new String[] {
									ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(), 
									getManagedBean().getWarningPercentage() }, 
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
					getManagedBean().setWarningPercentage(null);
				}

				if (ctBB.getManagedBean().getContractVer().getDocType() != null 
						&& ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
						&& ctBB.getManagedBean().isServiceTaxAlertFlg()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null,
									MessageUtils.getMessage(MessageConst.INFO_755004, 
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					ctBB.getManagedBean().setServiceTaxAlertFlg(false);
				}

				// Start Fix - #30856
				if (ctBB.getManagedBean().getCtContractAmount().isBondExempted()) {

					if (!ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().isEmpty()) {
						removePerformanceDatatableRow();
					}

					// Start - Fix #29368
					if (ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)) {
						checkandResetBondExempted();
					}
					// End - Fix #29368
				}
				// End Fix - #30856

				// Bug 34142, 15851 - shaqimi start 04/12/2022
				if (ctBB.getManagedBean().getOutcomesFromContract() != null) {
					this.getManagedBean().setOutComes(ctBB.getManagedBean().getOutcomesFromContract());
				} else {
					ctBB.getManagedBean().setOutcomesFromContract(getManagedBean().getOutComes());
				}
				// Bug 34142, 15851 - shaqimi end 04/12/2022
				//log.info("**End preRenderView from Maintenance** Check Bug #35156 :" 
						//+ " ContractVer :" + ctBB.getManagedBean().getContractVer().getContractVerId() 
						//+ ", Contract No :" + ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
				return;
			}

			// Bug 34142, 15851 - shaqimi start 06/12/2022
			if (ctBB.getManagedBean().getDocTypeFromTaskList() != null) {
				ContractReqDTO reqDTO = new ContractReqDTO();

				reqDTO.setTaskName(ctBB.getManagedBean().getTaskNameFromTaskList());
				reqDTO.setDocType(ctBB.getManagedBean().getDocTypeFromTaskList());
				reqDTO.setContractId(ctBB.getManagedBean().getContractIdFromTaskList());
				reqDTO.setContractVerId(ctBB.getManagedBean().getContractVerIdFromTaskList());
				reqDTO.setViewOnly(true);// Bug 34142 - shaqimi 04/12/2022

				setValidateContractField(reqDTO);
				return;
			}
			// Bug 34142, 15851 - shaqimi end 06/12/2022

			Object obj = super.getBpmTaskDetails(Object.class, new String[] { BpmOutcomeConst.BPM_SAVE });
			getManagedBean().setSelectedTabIndex(0);

			// #25406 - Start
			/* re-Setting Cancel button */
			//log.info("added checking include process name without space to cater for Oracle 12C [" + this.getClass().getSimpleName() + "][preRenderView]");
			if (this.getManagedBean().getOutComes() != null 
					&& getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_OWNER_MANAGEMENT_INITIATION_PROCESS_NAME)
					|| getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_OWNER_MANAGEMENT_INITIATION_PROCESS_NAME.replaceAll("\\s", ""))) {
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
			// #25406 - End

			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setViewOnly(true);// Bug 34142 - shaqimi 04/12/2022
			reqDTO.setTaskName(getManagedBean().getTaskName());
			//log.info("added checking include process name without space to cater for Oracle 12C [" + this.getClass().getSimpleName() + "][preRenderView]");
			if ((getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME) 
					|| getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME.replaceAll("\\s", "")))
					|| (getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME) 
					|| getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME.replaceAll("\\s", "")))) {
				CTContractAmendmentDataType dataType = (CTContractAmendmentDataType) obj;
				reqDTO.setContractId(new Long(dataType.getContractId()));
				reqDTO.setContractVerId(new Long(dataType.getDocId()));
				reqDTO.setDocType(dataType.getDocType());
			} else {
				CTContractDataType dataType = (CTContractDataType) obj;
				reqDTO.setContractId(new Long(dataType.getContractId()));
				reqDTO.setContractVerId(new Long(dataType.getDocId()));
				reqDTO.setDocType(dataType.getDocType());
			}

			// #35156 - Start
			//log.info("**Start preRenderView from TaskList** Check Bug #35156 :" + " ContractVer :" + reqDTO.getContractVerId() + ", Contract Id :" + reqDTO.getContractId());

			// Bug 34030 - shaqimi start
			ctBB.getManagedBean().setTaskNameFromTaskList(reqDTO.getTaskName());
			ctBB.getManagedBean().setDocTypeFromTaskList(reqDTO.getDocType());
			ctBB.getManagedBean().setContractIdFromTaskList(reqDTO.getContractId());
			ctBB.getManagedBean().setContractVerIdFromTaskList(reqDTO.getContractVerId());
			// Bug 34030 - shaqimi end

			// Bug 34142, 15851 - shaqimi start 04122022
			if (ctBB.getManagedBean().getOutcomesFromContract() != null) {
				this.getManagedBean().setOutComes(ctBB.getManagedBean().getOutcomesFromContract());
			} else {
				ctBB.getManagedBean().setOutcomesFromContract(getManagedBean().getOutComes());
			}
			// Bug 34142, 15851 - shaqimi start 04122022

			/* #26083 start - basri */
			if (reqDTO.getDocType().equals(ParameterConst.DOCTYPE_NOTIFICATION_SETTING)) {
				ctBB.getManagedBean().setNotificationSetting(true);
			} else {
				ctBB.getManagedBean().setNotificationSetting(false); // #28320 - related to #26083
			}
			setValidateContractField(reqDTO);

			// #35156 - End
			//log.info("**END preRenderView from TaskList** Check Bug #35156 :" 
			//+ " ContractVer :" + ctBB.getManagedBean().getContractVer().getContractVerId() 
			//+ ", Contract No :" + ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());

			FacesContext fc = FacesContext.getCurrentInstance();
			// #25406
			if (reqDTO.getDocType().equals(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_OWNER_MANAGEMENT);
			} else {
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}		
	}

	/**
	 * The method used to set the contract details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	public void setValidateContractField(ContractReqDTO reqDTO) {
		IContractSO contractSO = null;
		ContractResDTO resDTO = null;
		IAgreementSO agreementSO = null;
		ContractAgreementResDTO ctAgreementResDTO = null;
		ContractAgreementReqDTO ctAgreementReqDTO = null;
		ctBB = new ContractBackingBean();
		if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
			ctBB.getManagedBean().getDeleteAttachmentList().clear();
		}
		
		if(ctBB.getManagedBean().getCtAgreementList() != null ){
			ctBB.getManagedBean().getCtAgreementList().clear();
		}
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			log.info("code dev island cek value contract version = "+resDTO.getCtContractVer());
			ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
			
			//#35156 - Start
			log.info("**Start setValidateContractField** Check Bug #35156 :"+" ContractVer :"+ctBB.getManagedBean().getContractVer().getContractVerId() 
					+", Contract No :"+ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
			
			//Retrieve agreement list
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IAgreementSO.class);
			ctAgreementReqDTO = new ContractAgreementReqDTO();
			ctAgreementReqDTO.setContractId(resDTO.getCtContractVer().getCtContract().getContractId());
			ctAgreementReqDTO.setViewOnly(reqDTO.isViewOnly());//Enhancement 29418 - shaqimi;
			ctAgreementResDTO = agreementSO.retrieveContractAgreements(ctAgreementReqDTO);
			
			ctBB.getManagedBean().setCtAgreementList(ctAgreementResDTO.getAgreementList());
			//CT_CHANGE_LOG
			ctBB.getManagedBean().setNewAgencyMap(resDTO.getNewAgencyMap());
			ctBB.getManagedBean().setUpdateAgencyMap(resDTO.getUpdateAgencyMap());
			ctBB.getManagedBean().setDeleteAgencyMap(resDTO.getDeleteAgencyMap());
			ctBB.getManagedBean().setUpdateZoneMap(resDTO.getUpdateZoneMap());
			ctBB.getManagedBean().setUpdateItemMap(resDTO.getUpdateItemMap());

			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				// Retrieve Agencies
				if (ctBB.getManagedBean().getContractVer().getCtAgencies() != null  && ctBB.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
					ctBB.getManagedBean().setSelectedAgency(null);
					ctBB.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
							ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
					//#25406 - Start
					if(reqDTO.getDocType().equals(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
						if(ctBB.getManagedBean().getContractVer().getOwnerOrgProfile() != null){
							this.getManagedBean().setOldMinistryProfile(retrieveMinistryOrgProfile(
									ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(
									ctBB.getManagedBean().getContractVer().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(ctBB.getManagedBean().getContractVer().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchPtjProfile(ctBB.getManagedBean().getContractVer().getOwnerOrgProfile());
						}else{
							this.getManagedBean().setOldMinistryProfile(retrieveMinistryOrgProfile(
									ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(
									ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
							this.getManagedBean().setSearchPtjProfile(null);
//							this.getManagedBean().setSearchPtjProfile(ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile());
						}
					}
					//#25406 - End
					//Bug #27579
					ctBB.getManagedBean().setDisableAgent(true);
					ctBB.getManagedBean().setOwnerOrgProfile(true);
					for (CtAgency ctAgency : ctBB.getManagedBean().getContractVer().getCtAgencies()) {
						ctBB.checkAgencyStatus(ctAgency);
						// Start Fix - #36694
						if (!reqDTO.isViewOnly()) {
							if (ctBB.getManagedBean().getMinistryProfileId().equals(ctAgency.getOrgProfile().getMinstryId())) {
								ctAgency.setAp178(false);
								//Bug #27579
							} else {
								ctAgency.setAp178(true);
								ctBB.getManagedBean().setDisableAgent(false);
								ctBB.getManagedBean().setOwnerOrgProfile(false); // Bug #27579
							}
						}
						// End Fix - #36694
					}
				}
			}

			// Retrieve Items
			ContractItemSearchBackingBean cisbb = new ContractItemSearchBackingBean();
			cisbb.getManagedBean().setItemProductExist(false);
			cisbb.getManagedBean().setItemServiceExist(false);
			ctBB.getManagedBean().getCtItemList().clear();
			ctBB.getManagedBean().getCtItemServiceList().clear();	        	   
			ctBB.getManagedBean().setShowItem(null);
			ctBB.getManagedBean().setShowServiceItem(null);
			ctBB.getManagedBean().setSelectedItem(null);
			ctBB.getManagedBean().setSelectedServiceItem(null);	  
			if (ctBB.getManagedBean().getContractVer().getCtContractItems().size() > 0) {  	  
				for (CtContractItem item : ctBB.getManagedBean().getContractVer().getCtContractItems()) {
					// If effective date is after current date, status is still display old status
					if (item.getRecordStatusFlg() == 0) {				       		
						if (item.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
							item.setChkStatus(true);
						} else {
							item.setChkStatus(false);
						}
					} else {
						if (item.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
							item.setChkStatus(false);
						} else {
							item.setChkStatus(true);
						}
					}

					if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
						cisbb.getManagedBean().setItemServiceExist(true);
						Collections.sort(item.getCtItemPrices(), new CtItemPrice());
						ctBB.getManagedBean().getCtItemServiceList().add(item);			           
					} else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
						cisbb.getManagedBean().setItemProductExist(true);
						Collections.sort(item.getCtItemPrices(), new CtItemPrice());
						if (item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0) {
							for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {			        	        	    	   
								ctBB.checkAgencyStatus(ceilingQty.getCtAgency());		        	   
							}
						}
						ctBB.getManagedBean().getCtItemList().add(item);
					}
				}
			}

			// Retrieve Deduction Clauses
			if (ctBB.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
				ctBB.getManagedBean().setSelectedDeduction(null);
				for (CtDeduction deduct : ctBB.getManagedBean().getContractVer().getCtDeductions()) {
					if (deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)) {
						ctBB.getManagedBean().setFlgPaymentTracking(true);
						ctBB.getManagedBean().setAdvancePaymentAmt(deduct.getAmount());	    			
					}
				}
			}

			//Retrieve Payment Tracking	    
			calPaymentTracking();

			// Retrieve Schedules
			if (ctBB.getManagedBean().getContractVer().getCtSchedules() != null 
					&& !ctBB.getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
				ctBB.getManagedBean().setChkSchedule(true);
				Collections.sort(ctBB.getManagedBean().getContractVer().getCtSchedules(), new CtSchedule());
				for (CtSchedule ctSchedule : ctBB.getManagedBean().getContractVer().getCtSchedules()) {
					if (ctSchedule.isUseInFulfilment()) {
						ctBB.getManagedBean().setDisableScheduleRadio(true);
						break;
					}
				}
				ctBB.scheduleDate();
			}

			if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !ctBB.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				Collections.sort(ctBB.getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment());
				for (CtProgressivePayment ctProgressivePayment : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (ctProgressivePayment.isUseInFulfilment()) {
						ctBB.getManagedBean().setDisableScheduleRadio(true);
						break;
					}
				}
				ctBB.progressiveDate();
			}

			// Retrieve Delivery Terms
			if (ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null) {
				ctBB.getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
				PmParameter pmParameter = getParameterByTypeAndCode(
						ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS);
				ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(pmParameter.getParameterId());
			}

			if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
					ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				ctBB.getManagedBean().setDirectPurchase(true);
				//ctBB.getManagedBean().setFlgPaymentTracking(false);
			} else {
				ctBB.getManagedBean().setDirectPurchase(false);
			}
				
			if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getQtType() !=null) {				       		
				if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getQtType().getParameterCode().equalsIgnoreCase(
						ParameterTypeConst.QUOTATION_TENDER_TYPE) && ctBB.newProcurement()!= null){
				ctBB.getManagedBean().setNewPk7(true);
				} else {
					ctBB.getManagedBean().setNewPk7(false);
				}
			} else {
				//#27551 - add null checking
				if (ctBB.getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().
						getRequestNote() != null &&
						ctBB.getManagedBean().getContractVer().getCtContractItems().get(0).getScRequestItem().
						getRequestNote().getRequestNoteDtls().get(0).getProcurementType().getParameterType().equalsIgnoreCase(
						ParameterTypeConst.PROCUREMENT_TYPE) && ctBB.newJustification()!= null) {
					ctBB.getManagedBean().setNewPk7(true);
				} else {
					ctBB.getManagedBean().setNewPk7(false);
				}
			}
			
			if (getManagedBean().getUserMap().size() == 0) {
				initializeApproverList();
			}
			// Retrieve Approver List Information
			if (isApproverSetting()) {				
				getManagedBean().getApproverModel().setSource(
						getAvailableApproverList(ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode(),
								getManagedBean().getUserMap()));

				if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
					getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
					List<PmUser> invalidApproverList = new ArrayList<PmUser>();
					for (PmUser approverUser : resDTO.getApproverList())  {
						Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();	
						boolean isValidApprover = false;
						while (sourceIt.hasNext()) {
							PmUser user = sourceIt.next();
							log.debug("user : "+user+"; approverUser: "+approverUser);
							if (user!=null && approverUser!=null && (user.getUserId().longValue() == approverUser.getUserId().longValue())) {
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

			getManagedBean().setRemarkList(resDTO.getCtRemarks());
			getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			initializeRemarks();
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
					ModuleCodeConst.CONTRACT_MANAGEMENT, ctBB.getManagedBean().getContractVer().getDocType(), ctBB.getManagedBean().getContractVer().getCtContract().getContractNo()));

			ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
			// Retrieve Zones
			if (ctBB.getManagedBean().getContractVer().getCtContractZones() != null 
					&& ctBB.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
				Collections.sort(ctBB.getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
				ctBB.retriveZonesByContractVer();
			}

			//ctBB.getManagedBean().setSmAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
			ctBB.getManagedBean().setSupplierAddress(getFullAddress(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress()));
			ctBB.getManagedBean().setSupplierCompanyName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			ctBB.getManagedBean().setSupplierBranchName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
			ctBB.getManagedBean().setSupplierRegNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
			ctBB.getManagedBean().setSupplierMofNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
			//#25406
			if (!ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_CREATION)
					&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				ctBB.renderUpdateCheckbox(ctBB.getManagedBean().getContractVer());
			}
			ctBB.retrieveBranchInfo();
			if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
				ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else {
				ctBB.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}
			if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				ctBB.getManagedBean().setDisableScheduleRadio(true);
				ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules()!= null  && !ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules().isEmpty()) {
				ctBB.getManagedBean().setDisableScheduleRadio(true);
				ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			}  else if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments() != null && !ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments().isEmpty()) {
				ctBB.getManagedBean().setDisableScheduleRadio(true);
				ctBB.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}
			retrieveFulfilmentDetails();
			
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			ctBB.initializeRemarkAndAttachement(resDTO.getCtContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());

			ctBB.getManagedBean().setDisableContractAmount(true);
			
			// Enable or Disable GST AND STAX field
			if (!ctBB.isLoaContractWithGst(ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getGstEffDate())
					&& !ctBB.isLoaContractWithGst(ctBB.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				ctBB.getManagedBean().setGstCheckingGUI(false);	// Display N/A
				
			} else {
				if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() == null) {
					ctBB.getManagedBean().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				}
				ctBB.getManagedBean().setGstCheckingGUI(true);
				ctBB.getManagedBean().setDisableGstAmount(false);
				

				if (!ctBB.getManagedBean().isViewContract()) {
					// If original contract is fulfilled imposed service tax rule
					if (ctBB.isBeforeGstReliefDueDate() && ctBB.imposedServiceTaxCondition()) {
						if (ctBB.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
							ctBB.getManagedBean().setDisableContractAmount(false);
						} else {
							if (ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= ctBB.getGstReliefDueDate().getTime()) {
								ctBB.getManagedBean().setDisableGstAmount(true);
							}
						}
					}
				}
			}
			
			if (ctBB.getManagedBean().getCtContractAmount().getStaxAmount() == null) {
				ctBB.getManagedBean().getCtContractAmount().setStaxAmount(BigDecimal.ZERO);
			}
			ctBB.getManagedBean().setStaxCheckingGUI(true);
		//	ctBB.getManagedBean().setDisableStaxAmount(false);
			

			if (!ctBB.getManagedBean().isViewContract()) {
				// If original contract is fulfilled imposed service tax rule
				if (ctBB.isBeforeGstReliefDueDate() && ctBB.imposedServiceTaxCondition()) {
					if (ctBB.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
						ctBB.getManagedBean().setDisableContractAmount(false);
					} else {
						if (ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= ctBB.getGstReliefDueDate().getTime()) {
							ctBB.getManagedBean().setDisableStaxAmount(true);
						}
					}
				}
			}
			
			chkBondWaivedAttachment();
			sortDeductions();
			
			//#35156 - End
			log.info("**END setValidateContractField** Check Bug #35156 :"+"ContractVer :"+ctBB.getManagedBean().getContractVer().getContractVerId()
					+", Contract No :"+ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			resDTO = null;
			contractSO = null;
			agreementSO = null;
			ctAgreementResDTO = null;
			ctAgreementReqDTO = null;
		}
	}	
	
	public void downloadFile(String filePath, String fileName) throws IOException {
		commonDownload(filePath, fileName);
	}

	//#13847 start
	/**
	 * method to check all the attachment of the bond waiver (active, in
	 * progress and suspended) and grouping the bond waiver into latest and
	 * history to show on the screen which one is the history.
	 * 
	 * @throws Exception if an exception occurred
	 */
	private void chkBondWaivedAttachment() {
		try {
			resetBondWaiverDetails();

			if (CollectionUtils.isNotEmpty(ctBB.getManagedBean().getContractVer().getCtBondWaiverDetails())) {
				long noOfDays = 0;
				List<CtBondWaiverDetail> bondWaiverDetails = ctBB.getManagedBean().getContractVer()
						.getCtBondWaiverDetails();
				List<CtAttachment> bondWaiverAttachments = ctBB.getManagedBean().getContractVer()
						.getBondWaiverAttachments();

				for (CtBondWaiverDetail waiverDetail : bondWaiverDetails) {
					updateWaiverDetailAttachment(waiverDetail, bondWaiverAttachments);
					noOfDays += processWaiverDetail(waiverDetail);
				}

				ctBB.getManagedBean().setDisableDeferment(noOfDays >= ContractBackingBean.MAX_DEFERMENT_BOND_DAYS);

				sortBondWaiverDefermentHistory();
			}

			handleBondWaiverDefermentLatest();
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					ContractConst.SLA_CONTRACTDETAILSBACKINGBEAN,
					ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(),
					ContractConst.SLA_CHK_BOND_WAIVED_ATTACHMENT, getUserIdSessionFromLoginId(), e);
		}
	}
	
	/**
	 * Resets the latest bond waiver and history.
	 */
	private void resetBondWaiverDetails() {
	    ctBB.getManagedBean().getContractVer().setBondWaiverDefermentLatest(null);
	    ctBB.getManagedBean().getContractVer().setBondWaiverDefermentHistory(null);
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
	 * Processes the given waiver detail and updates the latest bond waiver or history.
	 *
	 * @param waiverDetail The bond waiver detail to process.
	 * @return The number of days between the waiver start and end dates.
	 */
	private long processWaiverDetail(CtBondWaiverDetail waiverDetail) {
		long noOfDays = 0;

		if (waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)) {
			setLatestBondWaiver(waiverDetail);
		} else if (waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)
				|| waiverDetail.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
			addToBondWaiverHistory(waiverDetail);
			noOfDays += ctBB.findDifference(waiverDetail.getWaiverStartDate(), waiverDetail.getWaiverEndDate());
		}

		return noOfDays;
	}

	/**
	 * Sets the latest bond waiver detail.
	 *
	 * @param waiverDetail The bond waiver detail to set as the latest.
	 */
	private void setLatestBondWaiver(CtBondWaiverDetail waiverDetail) {
		if (ctBB.getManagedBean().getContractVer().getBondWaiverDefermentLatest() == null) {
			ctBB.getManagedBean().getContractVer().setBondWaiverDefermentLatest(new CtBondWaiverDetail());
		}
		ctBB.getManagedBean().getContractVer().setBondWaiverDefermentLatest(waiverDetail);
	}

	/**
	 * Adds the given waiver detail to the bond waiver deferment history.
	 *
	 * @param waiverDetail The bond waiver detail to add.
	 */
	private void addToBondWaiverHistory(CtBondWaiverDetail waiverDetail) {
		if (ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory() == null) {
			ctBB.getManagedBean().getContractVer().setBondWaiverDefermentHistory(new ArrayList<CtBondWaiverDetail>());
		}
		ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory().add(waiverDetail);
	}

	/**
	 * Sorts the bond waiver deferment history by waiver start date in descending order.
	 */
	private void sortBondWaiverDefermentHistory() {
		List<CtBondWaiverDetail> history = ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory();
		if (CollectionUtils.isNotEmpty(history)) {
			Collections.sort(history, new Comparator<CtBondWaiverDetail>() {
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

	/**
	 * Handles the latest bond waiver deferment detail based on the contract amount.
	 */
	private void handleBondWaiverDefermentLatest() {
		if (ctBB.getManagedBean().getContractVer().getCtContractAmount() != null
				&& ctBB.getManagedBean().getContractVer().getCtContractAmount().isBondWaived()
				&& ctBB.getManagedBean().getContractVer().getBondWaiverDefermentLatest() == null) {
			ctBB.getManagedBean().getContractVer().setBondWaiverDefermentLatest(new CtBondWaiverDetail());
		}
	}
	
	/**
	 * Method to set all the bond waiver and its attachment 
	 * (latest, history, latest delete and history delete) into full waiver to be save into database.
	 * Separate the bond waiver with the attachment due to the save method for attachment 
	 * is not in the same persistance with the bond waiver.
	 * 
	 * @param outcome The outcome of the operation (e.g., approve).
	 * @param status The status of the bond waiver.
	 * @return A list of updated bond waiver details.
	 */
	private List<CtBondWaiverDetail> setBondWaiverLatestHistory(String outcome, Long status) {
		List<CtBondWaiverDetail> bondWaiverFull = new ArrayList<>();
		List<CtAttachment> waiverAttachmentFull = new ArrayList<>();
		CtBondWaiverDetail bondWaiverLatest = ctBB.getManagedBean().getContractVer().getBondWaiverDefermentLatest();
		Long tempAttachmentId = 1L; // Use Long directly for clarity

		try {
			// Process history
			tempAttachmentId = processBondWaiverHistory(bondWaiverFull, waiverAttachmentFull, status, outcome,
					tempAttachmentId);

			// Process latest bond waiver
			if (bondWaiverLatest != null) {
				processLatestBondWaiver(bondWaiverLatest, tempAttachmentId);
				waiverAttachmentFull.add(bondWaiverLatest.getCtAttachment());
				bondWaiverFull.add(bondWaiverLatest);
			}

			// Set bond waiver attachments if any
			if (CollectionUtils.isNotEmpty(waiverAttachmentFull)) {
				ctBB.getManagedBean().getContractVer().setBondWaiverAttachments(waiverAttachmentFull);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					ContractConst.SLA_SET_BOND_WAIVER_LATEST_HISTORY,
					ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(),
					ContractConst.SLA_SET_BOND_WAIVER_LATEST_HISTORY, getUserIdSessionFromLoginId(), e);
		}

		return bondWaiverFull;
	}
	
	/**
	 * Processes the bond waiver history and updates the record status and attachment IDs.
	 *
	 * @param bondWaiverFull The list to store updated bond waiver details.
	 * @param waiverAttachmentFull The list to store updated waiver attachments.
	 * @param status The status of the bond waiver.
	 * @param outcome The outcome of the operation (e.g., approve).
	 * @param tempAttachmentId The temporary attachment ID to be assigned.
	 * @return tempAttachId return new value of tempAttachmentId.
	 */
	private Long processBondWaiverHistory(List<CtBondWaiverDetail> bondWaiverFull,
			List<CtAttachment> waiverAttachmentFull, Long status, String outcome, Long tempAttachmentId) {
		Long tempAttachId = tempAttachmentId;
		if (CollectionUtils.isNotEmpty(ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory())) {
			for (CtBondWaiverDetail waiver : ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory()) {
				if (approvalStatus.containsValue(status) && outcome.equals(BpmOutcomeConst.BPM_APPROVE)) {
					waiver.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					waiver.getCtAttachment().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				}
				waiver.setTempAttachmentId(tempAttachId);
				waiver.getCtAttachment().setTempAttachmentId(tempAttachId);
				tempAttachId++;
				waiverAttachmentFull.add(waiver.getCtAttachment());
				bondWaiverFull.add(waiver);
			}
		}
		return tempAttachId;
	}

	/**
	 * Processes the latest bond waiver and updates its record status and attachment ID.
	 *
	 * @param bondWaiverLatest The latest bond waiver detail to process.
	 * @param tempAttachmentId The temporary attachment ID to be assigned.
	 */
	private void processLatestBondWaiver(CtBondWaiverDetail bondWaiverLatest, Long tempAttachmentId) {
		bondWaiverLatest.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
		bondWaiverLatest.setTempAttachmentId(tempAttachmentId);
		bondWaiverLatest.getCtAttachment().setTempAttachmentId(tempAttachmentId);
	}
	//#13847 end
	
	
	//Start - 25890 - Minh
	private void validateFDDescAndAmount(String action) {
		List<CtDeduction> ctDeductions = ctBB.getManagedBean().getContractVer().getCtDeductions();
		for (CtDeduction ctDeduction : ctDeductions) {
			Long parameterId = ctDeduction.getDeductionType().getParameterId();
			PmParameter pmParameter = retrieveParameterById(parameterId);
			if (ParameterConst.DEDUCTION_TYPE_FIXED.equalsIgnoreCase(pmParameter.getParameterCode())) {
				validateFDDescAndAmount(action, ctDeduction);
			}
		}
	}

	private void validateFDDescAndAmount(String action, CtDeduction ctDeduction) {
		String actionUpper = action.toUpperCase();
		switch (actionUpper) {
		case BpmOutcomeConst.BPM_SUBMIT:
			if (ctDeduction.getAmount() <= 0) {
				throwExceptions(MessageConst.ERROR_740059, null);
			}
			break;
		case BpmOutcomeConst.BPM_APPROVE:
			if (ctDeduction.getAmount() <= 0) {
				throwExceptions(MessageConst.ERROR_740059, null);
			}
			break;
		default:
			break;
		}
	}

	private void throwExceptions(String messageId, String paramMessage) {
		if (paramMessage == null) {
			throw new EpException(messageId);
		} else {
			throw new EpException(messageId, new String[] { ResourceBundleUtils.getMessage(paramMessage,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
	}
	//End - 25890 - Minh
	/**
	 * The method used to save the contract.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this contract
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	@SuppressWarnings("unchecked")
	public String saveContract(String action) throws Exception {
		IContractSO contractSO = null;
		//#29173 - CR 26475: Static Code Analysis
		//start
		//#29713 -  removed try-finally clause and useless null value assignment to variable contractSO as it is not needed. 
		//			exceptions are always thrown in main code
		
		//#35156 - Start
		log.debug("**Start** Check Bug #35156 :"+"***saveContract** "+"ContractVer :"+ctBB.getManagedBean().getContractVer().getContractVerId());
		
		/*Validation for login user's org profile before initiate contract task*/
		if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)
				&& ctBB.getManagedBean().getContractVer().getStatus() != null/*#26083 start - basri*/
				&& (ctBB.getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.FU_PENDING_SUBMISSION.longValue()
					|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.FW_PENDING_SUBMISSION.longValue()
					|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.NT_PENDING_SUBMISSION.longValue())) {
			validateOwnerOrgProfile(
					ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		}
		
		validateFDDescAndAmount(action);
		ctBB.getZoneObjDetails();

		if(!ctBB.getManagedBean().getContractVer().getCtContractValue().isPid()){
			ctBB.getManagedBean().getContractVer().setCtPidReasons(null);
		}
		//#25406 - Start
		if(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
			validateContractOwnerManagement(action);		
		}else{
			validateCtContractVer(action);
		}
		//#25406 - End
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(getManagedBean().getTaskId());
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());

		if(ctBB.getManagedBean().getContractVer().getCtSchedules() != null){
			for(CtSchedule schedules : ctBB.getManagedBean().getContractVer().getCtSchedules()){
				PmParameter parameter =  new PmParameter();
				parameter.setParameterId(schedules.getFulfilmentTypeId());
				schedules.setScheduleType(parameter);
			}
		}

		if (ctBB.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
			ctBB.getManagedBean().getContractVer().getCtContractItems().clear();
		}
		ctBB.getManagedBean().getContractVer().getCtContractItems().addAll(ctBB.getManagedBean().getCtItemList());
		ctBB.getManagedBean().getContractVer().getCtContractItems().addAll(ctBB.getManagedBean().getCtItemServiceList());

		if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			ctBB.scheduleProgressiveDate();
		}

		reqDTO.setCtContract(ctBB.getManagedBean().getContractVer().getCtContract());
		CtContractVer ctContractVer = ctBB.getManagedBean().getContractVer();
		//#25406 - Start
		if (ctBB.getManagedBean().getContractVer().getDocType().equals(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
			ctContractVer.setOwnerOrgProfile(getManagedBean().getSearchPtjProfile());
		}
		
		if (ctBB.getManagedBean().getContractVer().getDocType().equals(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)){
			ctContractVer.setOwnerOrgProfile(ctContractVer.getCtContract().getOwnerOrgProfile());			
			ctContractVer.getCtContract().setOwnerOrgProfile(getManagedBean().getSearchPtjProfile());
		}
		//#25406 - End

		ctContractVer.getCtContractValue().setSupplierName(ctBB.getManagedBean().getSupplierCompanyName());
		ctContractVer.getCtContractValue().setSupplierBranch(ctBB.getManagedBean().getSupplierBranchName());
		ctContractVer.getCtContractValue().setSupplierRegNo(ctBB.getManagedBean().getSupplierRegNo());
		ctContractVer.getCtContractValue().setSupplierMofNo(ctBB.getManagedBean().getSupplierMofNo());
		if (ctBB.getManagedBean().getCtAddress() != null) {
			ctContractVer.getCtContractValue().setSupplierAddress(ctBB.getManagedBean().getCtAddress());
		}
		//#24333 - Start
		if (ctBB.getManagedBean().getCtContractAmount().isBondReq() &&
				(!ctBB.getManagedBean().isDirectPurchase() ||  ctBB.isPk7FromDp()) &&
		        !ctBB.getManagedBean().getCtContractAmount().isBondExempted()) {

		        Date currentDate = new Date();
		        CtContractAmount uiBondData = ctBB.getManagedBean().getCtContractAmount();
		        Date ctValueExpDate = ctContractVer.getCtContractValue().getExpDate();

		        // Combine bonds from the UI into a single list.
		        List<CtPerformanceBond> allBondsFromUI = new ArrayList<CtPerformanceBond>();
		        if (uiBondData.getCtPerformanceBondsOriginal() != null) {
		            allBondsFromUI.addAll(uiBondData.getCtPerformanceBondsOriginal());
		        }
		        if (uiBondData.getCtPerformanceBondsNew() != null) {
		            allBondsFromUI.addAll(uiBondData.getCtPerformanceBondsNew());
		        }

		        for (CtContractAmount contractAmt : ctContractVer.getCtContractAmounts()) {
		            Date eff = contractAmt.getEffDate();
		            Date exp = contractAmt.getExpDate();

		            // Calculate active or extended conditions once.
		            boolean isActiveOrExtend = (!currentDate.before(eff) && !currentDate.after(exp)) 
			                || (exp != null && exp.equals(ctValueExpDate));

		            if (!isActiveOrExtend) {
		                continue;
		            }
		            // Update total amount
		            contractAmt.setTotalBondAmt(uiBondData.getTotalBondAmt());
		            
		            // Pass the `isEffectiveContract` flag to the sync method.
		            boolean isEffectiveContract = !currentDate.before(eff) && !currentDate.after(exp);
		            syncBondsToTarget(contractAmt, allBondsFromUI, isEffectiveContract);

		            // Set the attachment flag with more readable logic.
		            boolean isApproved = BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action);
		            boolean hasBonds = !contractAmt.getCtPerformanceBonds().isEmpty();
		            int flag = (hasBonds && isApproved) ? ParameterConst.FLAG_INT_YES : ParameterConst.FLAG_INT_NO;
		            contractAmt.setIsBondAttachment(flag);
		        }
		    }
		// 24333 - End
		
		//#13847 start
		if (ctBB.getManagedBean().getContractVer().getBondWaiverDefermentLatest() != null
				|| ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory() != null) {
			ctContractVer.setCtBondWaiverDetails(setBondWaiverLatestHistory(action, ctBB.getManagedBean()
					.getContractVer().getStatus().getStatusId()));
		}
		//#13847 end
		
		//#30014 - Start (Set Company Experience Date and Flag to 4 - App Realtime/Data Patching)
		if (isValidDocTypeForCallSp(ctBB.getManagedBean().getContractVer().getDocType()) && action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			ctContractVer.getCtContract().setCompExprDate(new Date());
			ctContractVer.getCtContract().setCompExprFlag(4);			
		}
		//#30014 - End
		
		reqDTO.setCtContractVer(ctContractVer);
		// Start - #28292/#26475
		reqDTO.setScLoa(ctContractVer.getCtContract().getLoa());
		// End  - #28292/#26475
		reqDTO.setAgreementContractVer(ctBB.getManagedBean().getAgreementContractVer());
		reqDTO.setUserRole(getUserRole());
		reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
		reqDTO.setAttachments(getManagedBean().getAttachmentList());
		reqDTO.setUserMap(getManagedBean().getUserMap());
		reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		reqDTO.setDeleteAttachments(ctBB.getManagedBean().getDeleteAttachmentList());
		reqDTO.setNewAgencyMap(ctBB.getManagedBean().getNewAgencyMap());
		reqDTO.setUpdateAgencyMap(ctBB.getManagedBean().getUpdateAgencyMap());
		reqDTO.setDeleteAgencyMap(ctBB.getManagedBean().getDeleteAgencyMap());
		reqDTO.setUpdateZoneMap(ctBB.getManagedBean().getUpdateZoneMap());
		reqDTO.setUpdateItemMap(ctBB.getManagedBean().getUpdateItemMap());

		if (this.getManagedBean().getPmDigiSign() != null) {
			reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
		}
		contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
		ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);			

		//#35156 - End
		log.debug("**END** Check Bug #35156 :"+"***saveContract** "+"ContractVer :"+ctBB.getManagedBean().getContractVer().getContractVerId());
		if (resDTO.isSuccessful()) {

			log.debug("Record saved successfully");
			String messageId = MessageConst.INFO_700005;
			String nextRole = null;
					 
			// Start Fix - #31740
			if(ctBB.getManagedBean().getSelectedAgencyChanges() != null){
			    if(ctBB.getManagedBean().getSelectedAgencyChanges().size()>0) {
				   log.info(" getSelectedAgencyChanges ::::::::::::::::::::::: "+ctBB.getManagedBean().getSelectedAgencyChanges().size());
				   for (CtAgency ctAgencysz : new ArrayList<CtAgency>(ctBB.getManagedBean().getSelectedAgencyChanges().values())){
					   if(ctBB.getManagedBean().getAgencyDataDeletion().containsKey(ctAgencysz.getOrgProfile().getOrgProfileId())){
						   ctBB.getManagedBean().getSelectedAgencyChanges().remove(ctAgencysz.getOrgProfile().getOrgProfileId());
						   log.info("Deleted agency profile ID :: "+ctAgencysz.getOrgProfile().getOrgProfileId());
						   log.info("Deleted agency name test :: "+ctAgencysz.getOrgProfile().getOrgValidity().getOrgName());

					   }else{
				    	    log.info(" agency org profile id :: "+ctAgencysz.getOrgProfile().getOrgProfileId());
						    log.info(" agency name test :: "+ctAgencysz.getOrgProfile().getOrgValidity().getOrgName() );
							contractSO.updateAgencyCeilingAmount(ctAgencysz.getCeilingAmount(), ctAgencysz.getOrgProfile().getOrgProfileId(),resDTO.getCtContractVer().getContractVerId());
							    	  
				      }				   
					   
				   }
				}
			    
			    ctBB.getManagedBean().getSelectedAgencyChanges().clear();
				  // log.info(" getSelectedAgencyChanges after clear() ::::::::::::::::::::::: "+ctBB.getManagedBean().getSelectedAgencyChanges().size());

			}
			//#30014 - Start
			sendToSmModuleForCompanyExp(resDTO, action, contractSO);
			//#30014 - End
			
			// #35506 Start
			AuditContractBackingBean auditCtBean = new AuditContractBackingBean();
			if (auditCtBean.getAuditCtConfigState()) {
				if (!ctBB.getManagedBean().getContractVer().getDocType().equals(ParameterConst.DOCTYPE_CONTRACT_CREATION)
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
								|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT))) {

					AuditContractReqDTO auCtData = new AuditContractReqDTO();
					auCtData.setDocId(ctContractVer.getCtContract().getContractId());
					auCtData.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					auCtData.setPrevContractVerId(ctContractVer.getCtContract().getLatestContractVer().getContractVerId());
					auCtData.setDocType(resDTO.getCtContractVer().getDocType());
					auCtData.setCtType(ctContractVer.getCtContract().getContractType().getParameterCode());
					auCtData.setRoleName(getUserRole());
					auCtData.setStatusId(resDTO.getStatusId());

					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)
							|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && ctBB.getManagedBean().getContractVer().getDocType()
									.equals(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT))) {
						auCtData.setCtIsFinal(AuditContractConst.CT_YES);
						auCtData.setCtIsApproved(AuditContractConst.CT_YES);
					} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						auCtData.setCtIsFinal(AuditContractConst.CT_YES);
						auCtData.setCtIsApproved(AuditContractConst.CT_NO);
					}

					auditCtBean.saveAuditData(auCtData);
				}
			}
			// #35506 End

			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				messageId = MessageConst.INFO_700021;
				if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					nextRole = RoleCode.CT_APPROVER.value();
				} else { // Central Contract
					nextRole = RoleCode.BPK_CC_APPROVER.value();
				}
			} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				messageId = MessageConst.INFO_700025;
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
				messageId = MessageConst.INFO_700009;
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				messageId = MessageConst.INFO_700007;
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				messageId = MessageConst.INFO_700008;
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) && !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
				messageId = MessageConst.INFO_700010;
				//#25406
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) &&
					ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
				messageId = MessageConst.INFO_700086;
				if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_MINISTRY)) {
					nextRole = RoleCode.CT_APPROVER.value();
				} else { // Central Contract
					nextRole = RoleCode.BPK_CC_APPROVER.value();
				}
			} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) &&
					ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
				messageId = MessageConst.INFO_700087;
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) &&
					ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
				messageId = MessageConst.INFO_700088;
			}

			if(StringUtils.isNotBlank(nextRole)&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
				PmRoleDesc roleDesc = RoleHelper.getInstance().getRoleDescByCode(nextRole);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ctBB.getManagedBean().getContractVer().getDocType()), 
								ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(),
								roleDesc.getRoleName()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			} else if(StringUtils.isNotBlank(nextRole)&& ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
				PmRoleDesc roleDesc = RoleHelper.getInstance().getRoleDescByCode(nextRole);

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ctBB.getManagedBean().getContractVer().getDocType()), 
								ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(),
								roleDesc.getRoleName(), getManagedBean().getSearchPtjProfile().getOrgName()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ctBB.getManagedBean().getContractVer().getDocType()), 
								ctBB.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}

			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
					|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) { 				  
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {				    	
				ContractReqDTO ctReqDTO = new ContractReqDTO();
				ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
				setValidateContractField(ctReqDTO);
			}
		} else {
			throw new EpException(MessageConst.ERROR_700020);
		}
		//end #29173 - CR 26475: Static Code Analysis
		return null;	
	}

	private IParameterSO getParameterSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}
	
	private PmParameter retrieveParameterById(Long parameterId) {
		if (parameterId == null) {
			throwExceptions(MessageConst.ERROR_100022, "lbl.ct.deduction.type");
		}

		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		paramReqDTO.setParameterId(parameterId);

		PmParameter pmParameter = getParameterSO().retrieveParameterById(paramReqDTO).getPmParameter();
		return pmParameter;
	}
	/**
	 * The method used for validating the contract information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this contract. The validation 
	 * 					will invoke base on the action
	 */
	public void validateCtContractVer(String action) throws EpException {
		getManagedBean().setAutoUpdate(true);

		/*Validation for delivery term main page*/
		if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) &&
				!ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
			if(ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTerm() <= 0){
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_100062);
			}
		}
		
//		//#25406 - Start
//		/*Validation for Contract Owner Management */
//		if((action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) &&
//				ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
//			if(getManagedBean().getSearchPtjProfile() != null){
//				if(getManagedBean().getSearchPtjProfile().getOrgProfileId() == ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()){
//					getManagedBean().setSelectedTabIndex(0);
//					throw new EpException(MessageConst.ERROR_775156);
//				}
//			}else{
//				getManagedBean().setSelectedTabIndex(0);
//				throw new EpException(MessageConst.ERROR_775156);
//			}
//		}
//		//#25406 - End
		
		// Validation for performance bond exempted
		if (ctBB.getManagedBean().getCtContractAmount().isBondReq() 
				&& !ctBB.getManagedBean().isDirectPurchase()
				&& ctBB.getManagedBean().getCtContractAmount().isBondExempted()
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& (ctBB.getManagedBean().getCtContractAmount().getBondExemptedFileName() == null 
				|| ctBB.getManagedBean().getCtContractAmount().getBondExemptedFileName().equalsIgnoreCase(""))
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			getManagedBean().setSelectedTabIndex(0);
			throwExceptions(MessageConst.ERROR_100022, "lbl.ct.bond.exempted.letter");
		}

		// Validation for remark
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
			String agencyRemark = ctBB.getManagedBean().getContractVer().getAgencyRemarks().get(0).getRemark();
			String zoneRemark = ctBB.getManagedBean().getContractVer().getZoneRemarks().get(0).getRemark();
			String itemRemark = ctBB.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
			String deductionRemark = ctBB.getManagedBean().getContractVer().getDeductionRemarks().get(0).getRemark();
			String scheduleRemark = ctBB.getManagedBean().getContractVer().getScheduleRemarks().get(0).getRemark();
			String settingRemark = ctBB.getManagedBean().getContractVer().getSettingRemarks().get(0).getRemark();
			String notificationRemark = ctBB.getManagedBean().getContractVer().getNotiSettingRemarks().get(0).getRemark();/*#26083 start - basri*/
			if ((remark == null || remark.length() == 0) 
					&& (agencyRemark == null || agencyRemark.length() == 0) 
					&& (zoneRemark == null || zoneRemark.length() == 0)
					&& (itemRemark == null || itemRemark.length() == 0) 
					&& (deductionRemark == null || deductionRemark.length() == 0)
					&& (scheduleRemark == null || scheduleRemark.length() == 0) 
					&& (settingRemark == null || settingRemark.length() == 0)
					&& (notificationRemark == null || notificationRemark.length() == 0)) {/*#26083 start - basri*/
				getManagedBean().setSelectedTabIndex(0);
				throwExceptions(MessageConst.ERROR_100022, "lbl.ct.remark");
			}
		}

		//Validation for PID
		if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && 
				ctBB.getManagedBean().getContractVer().getCtContractValue().isPid() && 
				(ctBB.getManagedBean().getContractVer().getCtPidReasons() == null || ctBB.getManagedBean().getContractVer().getCtPidReasons().size() == 0)
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			getManagedBean().setSelectedTabIndex(0);
			throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pid.reason");
		}
		// Validation for PID Reason
		if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL) && !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			if(ctBB.getManagedBean().getContractVer().getCtPidReasons() != null && ctBB.getManagedBean().getContractVer().getCtPidReasons().size() > 0 ){
				for(CtPidReason reason : ctBB.getManagedBean().getContractVer().getCtPidReasons()){
					if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){
						getManagedBean().setSelectedTabIndex(0);
						throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pid.reason");
					}

					ctBB.validatePidReason(reason.getPmReason().getReasonId());
					if(ctBB.PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
						if(reason.getReasonDesc().isEmpty()){
							getManagedBean().setSelectedTabIndex(0);
							throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pid.reason.desc");
						}
					}
				}
			}//  End validation for PID Reason
		}

		// Validation for GST amount before GST Relief Due Date
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			if (ctBB.isBeforeGstReliefDueDate()
					&& ctBB.imposedServiceTaxCondition()
					&& ctBB.isLoaContractWithGst(ctBB.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())
					&& ctBB.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
				if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() == null
						|| ctBB.getManagedBean().getCtContractAmount().getGstAmount().doubleValue() == 0) {
					getManagedBean().setSelectedTabIndex(0);
					throwExceptions(MessageConst.ERROR_100022, "lbl.ct.gst.amt");
				}
				
				if (ctBB.getManagedBean().getCtContractAmount().getStaxAmount() == null
						|| ctBB.getManagedBean().getCtContractAmount().getStaxAmount().doubleValue() == 0) {
					getManagedBean().setSelectedTabIndex(0);
					throwExceptions(MessageConst.ERROR_100022, "lbl.ct.stax.amt");
				}	

				if (ctBB.getManagedBean().getCtContractAmount().getContractAmount() == null
						|| ctBB.getManagedBean().getCtContractAmount().getContractAmount().doubleValue() == 0) {
					getManagedBean().setSelectedTabIndex(0);
					throwExceptions(MessageConst.ERROR_100022, "lbl.ct.contract.amount");
				}
			}
		}

		// Validation for zone
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			ctBB.getZoneObjDetails();
			if (ctBB.getManagedBean().getContractVer().getCtContractZones() != null) {
				for (CtContractZone contractZone : ctBB.getManagedBean().getContractVer().getCtContractZones()) {
					if (contractZone.getDeliveryTerm() <= 0) {
						getManagedBean().setSelectedTabIndex(1);
						throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
					}
				}
			}
		}		

		// Validation for agency
		if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){ 
			if(ctBB.getManagedBean().getContractVer().getCtAgencies().size() == 0) {
				setTabValue("AG");
				throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
			} else {
				double ceilingAmt = 0.0;
				DecimalFormat df = new DecimalFormat("#.00");
				String contractNO = ctBB.getManagedBean().getContractVer().getCtContract().getContractNo();
				for (CtAgency ctAgency : ctBB.getManagedBean().getContractVer().getCtAgencies()) {
					ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
				}
				ceilingAmt = Double.valueOf(df.format(ceilingAmt));

				double overallContractAmount = 0.0;
				for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContractAmounts()){
					if (ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
							&& ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
						overallContractAmount = contractAmt.getContractAmount().doubleValue();
						if (contractAmt.getGstAmount() != null) {
							overallContractAmount  += contractAmt.getGstAmount().doubleValue();
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
					if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						setTabValue("AG");
					}else{
						setTabCentralValue("AG");
					}
					throw new EpException(MessageConst.ERROR_750007, new String[] {nf.format(overallContractAmount)}); 
					// Total ceiling value exceeded contract value. Please check your ceiling amount.
				}
			}	

			//#28289 - During Notification Setting, no need to validate
			if(!ctBB.getManagedBean().isOwnerOrgProfile() && !(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_NOTIFICATION_SETTING))){
				if(ctBB.getManagedBean().getContractVer().getCtContractValue().getAp178RefNo() == null
						|| ctBB.getManagedBean().getContractVer().getCtContractValue().getAp178RefNo().isEmpty()){
					setTabValue("AG");
					throw new EpException(MessageConst.ERROR_750045);
				}//25703 start
				else if(ctBB.getManagedBean().getContractVer().getAgencyContractSharingAttachements() == null ||
						ctBB.getManagedBean().getContractVer().getAgencyContractSharingAttachements().isEmpty()){
					setTabValue("AG");
					throw new EpException(MessageConst.ERROR_750046);
				}
				//25703 end
			}
		}

		//Validation for contract item details
		//#28320 - During Notification Setting, no need to validate
		if ((ctBB.getManagedBean().getContractVer().getCtContractItems() != null) && !(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_NOTIFICATION_SETTING))
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {

			for (CtContractItem item : ctBB.getManagedBean().getContractVer().getCtContractItems()) {
				// Validation for minimum order quantity
				if (item.getMinimumOrderQty() <= 0) {
					throw new EpException(MessageConst.ERROR_100033,
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.min.order.qty",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				
				// Start - 28292
				if (!item.getCtItemMaxQties().isEmpty()) {
					Double maxValue = new Double(0);
					for (CtItemMaxQty maxQty : item.getCtItemMaxQties()) {
						if(maxQty.getMaxQuantity().doubleValue() > maxValue)
							maxValue = maxQty.getMaxQuantity().doubleValue();				
					}
					if (item.getMinimumOrderQty() > maxValue) {
						throwExceptions(MessageConst.ERROR_100034, "lbl.ct.min.order.qty");						
					}
					
				}
				// End - 28292
				
				// Validation for ceiling quantity
				double totalCeilingQuantity = 0.0;
				if (item.getCtItemMaxQties().size() != 0) {						
					for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
						totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
					}

					if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
						if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							setTabValue("IT");
						}else{
							setTabCentralValue("IT");
						}
						throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
					}
				}
			}
		}

		// Validation for Deduction
		//#32123 - During Notification Setting, no need to validate
		if((ctBB.getManagedBean().getContractVer().getCtDeductions() != null 
				&& ctBB.getManagedBean().getContractVer().getCtDeductions().size() > 0)
				&& !ctBB.getManagedBean().getContractVer().getDocType().equals(ParameterConst.DOCTYPE_NOTIFICATION_SETTING)){
			for(CtDeduction deduction : ctBB.getManagedBean().getContractVer().getCtDeductions()){
				if(deduction.getDeductionType() == null ||deduction.getDeductionType().getParameterId() == null ){
					setTabValue("DE");
					throwExceptions(MessageConst.ERROR_100022, "lbl.ct.deduction.type");
				}

				ctBB.validateDeductionType(deduction);
				if(deduction.getCtDeductionClauses() != null && deduction.getCtDeductionClauses().size() > 0){
					for(CtDeductionClause ctDeductionClauses : deduction.getCtDeductionClauses()){
						if(ctDeductionClauses.getClauseDescription().isEmpty()){
							setTabValue("DE");
							throwExceptions(MessageConst.ERROR_100022, "lbl.ct.deduction.clause.desc");
						}
					}
				/*#25592-basri*/
				/*validation error if deduction clause is empty*/
				} else { 
						setTabValue("DE");
						throwExceptions(MessageConst.ERROR_100022, "lbl.ct.deduction.clause.desc");	
					   // End Fix - #32123				
				}
				/*end*/
			}  
		}		

		// Validation for schedule
		//basri -  #28649 During Notification Setting, no need to validate 
		if(ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
				&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)
				&& !(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_NOTIFICATION_SETTING))
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
				ctBB.getManagedBean().getContractVer().setCtProgressivePayments(null);
			} else {
				ctBB.getManagedBean().getContractVer().setCtSchedules(null);  	
			}

			List<String> schedulePhaseNoList = new ArrayList<String>();
			if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
				if (ctBB.getManagedBean().getContractVer().getCtSchedules() == null || ctBB.getManagedBean().getContractVer().getCtSchedules().size() < 2 
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
					setTabValue("SC");
					throw new EpException(MessageConst.ERROR_750022);
				}

				for (CtSchedule schedules : ctBB.getManagedBean().getContractVer().getCtSchedules()) {
					if (schedules.getScheduleNo() == null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100046, 
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					} else {
						schedulePhaseNoList.add(schedules.getScheduleNo().toString());
					}

					if (schedules.getStartMonth() == null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (schedules.getEndMonth() == null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (schedules.getFulfilmentTypeId() == null) {
						setTabValue("SC");
						throwExceptions(MessageConst.ERROR_100022, "lbl.ct.schedule.type");
					}
				}     	
			} else if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() == null || ctBB.getManagedBean().getContractVer().getCtProgressivePayments().size() < 2 
					&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()
					&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
				setTabValue("SC");
				throw new EpException(MessageConst.ERROR_750022);
			}

			if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !ctBB.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty() 
					&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
				BigDecimal percentage = BigDecimal.ZERO;
				for (CtProgressivePayment progressivePayment : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (progressivePayment.getPhaseNo()== null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.phase", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100046, 
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.phase", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					} else {
						schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
					}

					if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.description", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (progressivePayment.getStartMonth() == null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022, 
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}

					if (progressivePayment.getEndMonth() == null) {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022, 
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}     

					if(progressivePayment.getPercentage()!=null) {
						percentage = percentage.add(progressivePayment.getPercentage());
					} else {
						setTabValue("SC");
						throw new EpException(MessageConst.ERROR_100022,
								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
										+ " " + ResourceBundleUtils.getMessage("lbl.ct.percentage", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}

				int value = percentage.compareTo(new BigDecimal(100));
				if (value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
					setTabValue("SC");
					throwExceptions(MessageConst.ERROR_100022, "lbl.ct.percentage.progressive");
				}
			}
		}
		
		//basri -  #29441 During Notification Setting, no need to validate
		// Validation for performance bond
		if (ctBB.getManagedBean().getCtContractAmount().isBondReq() && (!ctBB.getManagedBean().isDirectPurchase() || ctBB.isPk7FromDp())
				&& !ctBB.getManagedBean().getCtContractAmount().isBondExempted()
				&& (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL))
				&& !(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_NOTIFICATION_SETTING))
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			//In CT Update(bond required): if previously the bond already defined, number of performance bond must be > 0 while submission; 
			//In CT Update(bond required): if previously no bond defined, system allow submission of FD Update even the number of performance bond entered = 0.
			if (ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
				//#24333 - Start
				CtContractVer latestVer = ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer();
				if (latestVer != null) {
					long asAtTime = getManagedBean().getAsAtDate().getTime();
					for (CtContractAmount contractAmt : latestVer.getCtContractAmounts()) {
						if (asAtTime >= contractAmt.getEffDate().getTime() && asAtTime <= contractAmt.getExpDate().getTime()) {
							boolean previouslyHasBond = contractAmt.getCtPerformanceBonds() != null && !contractAmt.getCtPerformanceBonds().isEmpty();
							//#24333 - End
							// Bug #25359 
							// Fahmi alter condition with putra
							//#24333 - Start
							if (previouslyHasBond) {
								List<CtPerformanceBond> bondsToCheck = ctBB.isContractExtendOrIsBond() ? ctBB.getManagedBean().getCtContractAmount()
										.getCtPerformanceBondsNew() : ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal();

								if (bondsToCheck == null || bondsToCheck.isEmpty()) {
									blockSubmission("PB");
								}
							}
							//#24333 - End
						}
					} 
				}
			}	
			log.info("validateCtContractVer method test : Start Checking ");			
			//#24333 - Start
		    List<CtPerformanceBond> originalBonds = ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBondsOriginal();
		    List<CtPerformanceBond> newBonds = ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBondsNew();

		    if (originalBonds != null) {
		        validateBonds(originalBonds, action);
		    }
		    if (newBonds != null) {
		        validateBonds(newBonds, action);
		    }
		    log.info("validateCtContractVer method test : End Checking ");
		  //#24333 - End
		}
		validationPerformanceBond(action); //#13847
		
		// Validation for Approver
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting() 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				setTabValue("SE");
			}else{
				setTabCentralValue("SE");
			}
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		// Start - 28292
		
		/*ContractItemSearchBackingBean  contractBB= new ContractItemSearchBackingBean();
		List<CtContractItem> itemlistService = contractBB.getManagedBean().getServiceListingPagingDataModel().getList();
		List<CtContractItem> itemlistProduct = contractBB.getManagedBean().getContractListingPagingDataModel().getList();


		if(itemlistService != null && !itemlistService.isEmpty()){
			CommonUtils commonUtils = new CommonUtils();
			for (CtContractItem ctItem : itemlistService) {
				String minValue = commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_QTY, ctItem
						.getScRequestItem().getUom().getUomCode());
				DecimalFormat formatter = new DecimalFormat(minValue);
				String result = formatter.format(ctItem.getMinimumOrderQty());
				ctItem.setContractItemId(ctItem.getContractItemId());
				ctItem.setMinimumOrderQty(Double.parseDouble(result));

				ctBB.onChangeMinimumQty(ctItem);

			}
		}
		
		if(itemlistProduct != null && !itemlistProduct.isEmpty()){
			CommonUtils commonUtils = new CommonUtils();
			for (CtContractItem ctItem : itemlistProduct) {
				String minValue = commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_QTY, ctItem
						.getScRequestItem().getUom().getUomCode());
				DecimalFormat formatter = new DecimalFormat(minValue);
				String result = formatter.format(ctItem.getMinimumOrderQty());
				ctItem.setContractItemId(ctItem.getContractItemId());
				ctItem.setMinimumOrderQty(Double.parseDouble(result)); 

				ctBB.onChangeMinimumQty(ctItem);

			}
		}*/
		// End - 28292
	}
	//LOG : 13847, by Fahmi 22032019 start
	/**
	 * Validates the performance bond based on the specified action and the current state of the contract.
	 *
	 * @param action The action being performed (e.g., save, submit, reject, revise, approve).
	 * @throws EpException if any validation fails, indicating the specific error.
	 */
	public void validationPerformanceBond(String action) {
		Long status = ctBB.getManagedBean().getContractVer().getStatus().getStatusId();
		CtBondWaiverDetail latestWaiver = ctBB.getManagedBean().getContractVer().getBondWaiverDefermentLatest();

		validateBondWaiverHistory(ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory());
		if (isBondValidationRequired(action)) {
			if (!isBondWaiverConditionsMet(latestWaiver)) {
				setTabValue("PB");
				throw new EpException(MessageConst.ERROR_750061);
			} else {
				validateWaiverDates(latestWaiver);

				if (approvalStatus.containsValue(status) && !isBondWaiverInHistory(latestWaiver)) {
					setTabValue("PB");
					throw new EpException(MessageConst.ERROR_750071);
				}
			}
		}
	}

	/**
	 * Checks if bond validation is required based on the action and CtContractAmount conditions.
	 *
	 * @param action The action being performed (e.g., save, submit, approve).
	 * @return true if bond validation is required; false otherwise.
	 */
	private boolean isBondValidationRequired(String action) {
		CtContractAmount contractAmount = ctBB.getManagedBean().getCtContractAmount();
		boolean isActionValid = isActionRequiringValidation(action);

		return contractAmount.isBondWaived() && contractAmount.isBondReq() && !contractAmount.isBondExempted()
				&& isActionValid;
	}
	
	/**
	 * Checks if the specified action requires bond validation.
	 *
	 * @param action The action to check.
	 * @return true if the action requires bond validation; false otherwise.
	 */
	private boolean isActionRequiringValidation(String action) {
		return action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE); // 33824 remove duplicate reject
	}

	/**
	 * Validates if the bond waiver conditions are met.
	 *
	 * @param latestWaiver The latest bond waiver details.
	 * @return true if all conditions are met; false otherwise.
	 */
	private boolean isBondWaiverConditionsMet(CtBondWaiverDetail latestWaiver) {
		boolean isAttachmentValid = isAttachmentValid(latestWaiver.getCtAttachment());

		return latestWaiver.getWaiverStartDate() != null && latestWaiver.getWaiverEndDate() != null
				&& StringUtils.isNotBlank(latestWaiver.getReferenceNo()) && isAttachmentValid;
	}
	
	/**
	 * Checks if the attachment is valid.
	 *
	 * @param attachment The attachment to check.
	 * @return true if the attachment is valid; false otherwise.
	 */
	private boolean isAttachmentValid(CtAttachment attachment) {
	    return attachment != null && StringUtils.isNotBlank(attachment.getFileName());
	}

	/**
	 * Validates that the waiver start date is not after the waiver end date.
	 *
	 * @param latestWaiver The latest bond waiver details.
	 * @throws EpException if the start date is after the end date.
	 */
	private void validateWaiverDates(CtBondWaiverDetail latestWaiver) {
	    if (latestWaiver.getWaiverStartDate().compareTo(latestWaiver.getWaiverEndDate()) > 0) {
	        setTabValue("PB");
	        throw new EpException(MessageConst.ERROR_750080);
	    }
	}

	/**
	 * Checks if the latest bond waiver is present in the history of bond waivers.
	 *
	 * @param latestWaiver The latest bond waiver details.
	 * @return true if the latest waiver is found in history; false otherwise.
	 */
	private boolean isBondWaiverInHistory(CtBondWaiverDetail latestWaiver) {
	    List<CtBondWaiverDetail> historyList = ctBB.getManagedBean().getContractVer().getBondWaiverDefermentHistory();
	    if (historyList != null) {
	        for (CtBondWaiverDetail history : historyList) {
	            if (history.getWaiverStartDate().equals(latestWaiver.getWaiverStartDate()) &&
	                history.getWaiverEndDate().equals(latestWaiver.getWaiverEndDate()) &&
	                history.getReferenceNo().equals(latestWaiver.getReferenceNo()) &&
	                history.getCtAttachment().getFileName().equals(latestWaiver.getCtAttachment().getFileName())) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	/**
	 * Validates the history of bond waivers to ensure all required details are present.
	 *
	 * @param bondWaiverHistory The list of bond waiver history details.
	 * @throws EpException if any historical bond waiver is missing required details.
	 */
	private void validateBondWaiverHistory(List<CtBondWaiverDetail> bondWaiverHistory) {
	    if (bondWaiverHistory != null && !bondWaiverHistory.isEmpty()) {
	        for (CtBondWaiverDetail history : bondWaiverHistory) {
	            if (history.getCtAttachment() == null || 
	                StringUtils.isBlank(history.getCtAttachment().getFileName()) || 
	                StringUtils.isBlank(history.getCtAttachment().getFilePath())) {
	                setTabValue("PB");
	                throw new EpException(MessageConst.ERROR_750065);
	            }
	        }
	    }
	}
	//#13847 - end
	
	/**
	 * The method used to authenticate user during submit the contract.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save contract method once authentication success.
	 * 
	 * @param  outcome  the user action on this contract
	 * @return result of the saveContract process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	
	public String gpkiSignAction(String outcome) throws Exception {
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome) || BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
			validateCtContractVer(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			// Start - 25890 - Minh
			if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
				PmStatus pmStatus = ctBB.getManagedBean().getContractVer().getStatus();
				if (pmStatus != null
						&& ContractStatusConst.FD_PENDING_APPROVAL.longValue() == pmStatus.getStatusId().longValue()) {
					validateFDDescAndAmount(outcome);
				}
			}
			// End - 25890 - Minh
			StringBuilder sb = new StringBuilder();
			sb.append(ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ctBB.getManagedBean().getContractVer().getDocType());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ctBB.getManagedBean().getContractVer().getCtContractValue().getEffDate());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ctBB.getManagedBean().getCtContractAmount().getContractAmount());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);		
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());		
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		//#25406 - Start	
		} else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)
				&& !ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		}else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome) 
				&& ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)) {
			validateRemarkCancel(outcome);
			RequestContext.getCurrentInstance().execute("cancelComConfirmDlg.show()");
			return null;
		//#25406 - End
		} else {
			return this.saveContract(outcome);
		}
	}

	/**
	 * The method used to set value for security check
	 * before save the contract.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveContract process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
	 */
	public String preSubmitContract(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContract(getManagedBean().getButtonAction());
	}

	/**
	 * The method used for controlling the tab menu
	 * of central contract dynamically.
	 * 
	 * @param  outputTab   current selected tab constant
	 */
	public void setTabCentralValue(String outputTab) {		
		getManagedBean().setItemIndex(11);
		getManagedBean().setDeductionIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setSettingIndex(11);		
		getManagedBean().setPaymentTracking(11);
		getManagedBean().setBondIndex(11);
		getManagedBean().setNotificationIndex(111); //#35187
		ctBB.getManagedBean().setSelectAll(false);

		boolean typeZonalPrice = false;
		boolean typeFactoring = false;
		boolean typeLoaAmt = false;
		boolean bondRequired = false;
		boolean approverSetting = false; //#35187

		/*Set value true if contract is zonal*/
		if(ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
			typeZonalPrice = true;
		}

		if(ctBB.isPerformanceBond()){
			bondRequired = true;
		}

		/*Set value true if contract is factor*/
		if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored()  && checkFactoringExpiryDate()){
			typeFactoring = true;
		}

		/*Set value true if contract follow loa amount*/
		if (ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()){
			typeLoaAmt = true;
		}

		//#35187 start
		/*Set value true if contract is on approver*/
		if(isApproverSetting()){
			approverSetting = true;
		}
		//#35187 end
		
		if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
			getManagedBean().setSelectedTabIndex(0);
		} 

		/*Set tab value if contract is zonal*/
		if(typeZonalPrice){

			if (outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
				getManagedBean().setSelectedTabIndex(1);

			} else if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
				getManagedBean().setSelectedTabIndex(2);
				getManagedBean().setItemIndex(2);
				ContractItemSearchBackingBean cmbb=new ContractItemSearchBackingBean();
				cmbb.getManagedBean().reassignValue(true);
				
				//cmbb.getManagedBean().allValue(true);
				//cmbb.getManagedBean().setResetval(true);
				cmbb.getManagedBean().getContractListingPagingDataModel().reloadData();
				cmbb.getManagedBean().getServiceListingPagingDataModel().reloadData();
				cmbb.getManagedBean().allValue(true);
				cmbb.searchReset(); // Fix - #31042
				//log.info("Value from the Cental Tab ********>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
			} else if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
				getManagedBean().setSelectedTabIndex(3);
				getManagedBean().setDeductionIndex(3);
				
			}else if(bondRequired && outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL)){
				getManagedBean().setSelectedTabIndex(4);
				getManagedBean().setBondIndex(4);
				
			} else if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
				if(bondRequired){
					getManagedBean().setSelectedTabIndex(5);
				} else {
					getManagedBean().setSelectedTabIndex(4);
				}
				getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());

			} else  if (typeLoaAmt && outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
				if(typeFactoring && bondRequired){
					getManagedBean().setSelectedTabIndex(6);
				} else if(typeFactoring || bondRequired){
					getManagedBean().setSelectedTabIndex(5);
				}else {
					getManagedBean().setSelectedTabIndex(4);
				}
				getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());

			} else if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {

				if(bondRequired){
					getManagedBean().setSelectedTabIndex(5);
					if (typeFactoring && typeLoaAmt) {
						getManagedBean().setSelectedTabIndex(7);
					}else if(typeLoaAmt || typeFactoring){
						getManagedBean().setSelectedTabIndex(6);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(5);
					if(typeLoaAmt){
						getManagedBean().setSelectedTabIndex(6);
					}
				} else if(typeLoaAmt){
					getManagedBean().setSelectedTabIndex(5);
				}else {
					getManagedBean().setSelectedTabIndex(4);
				}
				getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
				
			}else if(outputTab.equalsIgnoreCase(ContractConst.NOTIFICATION_DETAIL)){/*#26083 /bug 28314 start - basri*/
				if(bondRequired){
					getManagedBean().setSelectedTabIndex(6);
					if (typeFactoring && typeLoaAmt) {
						getManagedBean().setSelectedTabIndex(8);
					}else if(typeLoaAmt || typeFactoring){
						getManagedBean().setSelectedTabIndex(7);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(6);
					if(typeLoaAmt){
						getManagedBean().setSelectedTabIndex(7);
					}
				} else if(typeLoaAmt){
					getManagedBean().setSelectedTabIndex(6);
				}else {
					getManagedBean().setSelectedTabIndex(5);
				}
				
				//#35187 start
				if(!approverSetting){
					getManagedBean().setSelectedTabIndex(getManagedBean().getSelectedTabIndex()-1);
				}
				//#35187 end
				
				getManagedBean().setNotificationIndex(getManagedBean().getSelectedTabIndex());
			}	  /*#26083 /bug 28314 end - basri*/

			/*Set tab value if contract is not zonal*/	 
		} else {

			if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
				getManagedBean().setSelectedTabIndex(1);
				getManagedBean().setItemIndex(1);
				// Start Fix - #31042
				ContractItemSearchBackingBean cisbb = new ContractItemSearchBackingBean();
				cisbb.searchReset();
				// End Fix - #31042

			} else if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
				getManagedBean().setSelectedTabIndex(2);
				getManagedBean().setDeductionIndex(2);
				
			}else if(bondRequired && outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL)){
				getManagedBean().setSelectedTabIndex(3);
				getManagedBean().setBondIndex(3);

			} else if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
				if(bondRequired){
					getManagedBean().setSelectedTabIndex(4);
				}else{
					getManagedBean().setSelectedTabIndex(3);
				}
				getManagedBean().setFactoring( getManagedBean().getSelectedTabIndex());

			} else  if (typeLoaAmt && outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
				if(bondRequired && typeFactoring){
					getManagedBean().setSelectedTabIndex(5);
				} else if(bondRequired || typeFactoring){
					getManagedBean().setSelectedTabIndex(4);
				} else {
					getManagedBean().setSelectedTabIndex(3);
				}
				getManagedBean().setPaymentTracking( getManagedBean().getSelectedTabIndex());

			} else if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
				if(bondRequired){
					getManagedBean().setSelectedTabIndex(4);
					if (typeFactoring && typeLoaAmt) {
						getManagedBean().setSelectedTabIndex(6);
					}else if(typeLoaAmt || typeFactoring){
						getManagedBean().setSelectedTabIndex(5);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(4);
					if(typeLoaAmt){
						getManagedBean().setSelectedTabIndex(5);
					}
				} else if(typeLoaAmt){
					getManagedBean().setSelectedTabIndex(4);
				}else {
					getManagedBean().setSelectedTabIndex(3);
				}
				getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
			}else if(outputTab.equalsIgnoreCase(ContractConst.NOTIFICATION_DETAIL)){/*#26083 start - basri*/
				if(bondRequired){
					getManagedBean().setSelectedTabIndex(5);
					if (typeFactoring && typeLoaAmt) {
						getManagedBean().setSelectedTabIndex(7);
					}else if(typeLoaAmt || typeFactoring){
						getManagedBean().setSelectedTabIndex(6);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(5);
					if(typeLoaAmt){
						getManagedBean().setSelectedTabIndex(6);
					}
				} else if(typeLoaAmt){
					getManagedBean().setSelectedTabIndex(5);
				}else {
					getManagedBean().setSelectedTabIndex(4);
				}
				
				//#35187 start
				if(!approverSetting){
					getManagedBean().setSelectedTabIndex(getManagedBean().getSelectedTabIndex()-1);
				}
				//#35187 end
				
				getManagedBean().setNotificationIndex(getManagedBean().getSelectedTabIndex());
			}
		}
	} 

	/**
	 * The method used for controlling the tab menu
	 * of ministry contract dynamically.
	 * 
	 * @param  outputTab   current selected tab constant
	 */
	public void setTabValue(String outputTab) {	 
		getManagedBean().setItemIndex(11);
		getManagedBean().setDeductionIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setScheduleIndex(11);
		getManagedBean().setPaymentTracking(11);
		getManagedBean().setSettingIndex(11);
		getManagedBean().setAgencyIndex(111);
		getManagedBean().setBondIndex(113);
		getManagedBean().setNotificationIndex(111); //#35187
		ctBB.getManagedBean().setSelectAll(false);

		boolean typeZonalPrice = false;
		boolean typeSchedule = false;
		boolean typeFactoring = false;
		boolean bondRequired = false;
		boolean approverSetting = false; // #35187
		
		CtContract ctContract = ctBB.getManagedBean().getContractVer().getCtContract();

		/*Set value true if contract is zonal*/
		if(ctContract.isZonalPricing()){
			typeZonalPrice = true;
		}
		
		if(ctBB.isPerformanceBond()){
			bondRequired = true;
		}
		
		/*Set value true if contract is schedule*/
		//#25002 start
		if(ctContract.getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){	
			typeSchedule = true;
		}
		//#25002 end
		
		/*Set value true if contract is factor*/
		if(allowShowFactoring()){/*#28868 - basri*/
			typeFactoring = true;
		}
		
		//#35187 start
		/*Set value true if contract is on approver*/
		if(isApproverSetting()){
			approverSetting = true;
		}
		//#35187 end
		
		if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
			getManagedBean().setSelectedTabIndex(0);
		}

		/*Setting tab value if zonal is true*/
		if(typeZonalPrice) {
			if (outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
				getManagedBean().setSelectedTabIndex(1);
			}
			if(outputTab.equalsIgnoreCase(ContractConst.AGENCY_DETAIL)) {
				getManagedBean().setSelectedTabIndex(2);
				getManagedBean().setAgencyIndex(2);
			}
			if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
				getManagedBean().setSelectedTabIndex(3);
				getManagedBean().setItemIndex(3);
				ContractItemSearchBackingBean cmbb=new ContractItemSearchBackingBean();

				cmbb.getManagedBean().reassignValue(true);
				cmbb.getManagedBean().getContractListingPagingDataModel().reloadData();
				cmbb.getManagedBean().getServiceListingPagingDataModel().reloadData();
				//cmbb.getManagedBean().allValue(true);
				cmbb.searchReset(); //Fix - #31042
			}
			if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
				getManagedBean().setSelectedTabIndex(4);
				getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
			} 
			if (outputTab.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) && typeSchedule) {
				getManagedBean().setSelectedTabIndex(5);
				getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
			}
			if (outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL) && bondRequired) {
				getManagedBean().setSelectedTabIndex(5);
				if (typeSchedule) {
					getManagedBean().setSelectedTabIndex(6);
				}
				getManagedBean().setBondIndex(getManagedBean().getSelectedTabIndex());
			}
			if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
				getManagedBean().setSelectedTabIndex(5);
				if (typeSchedule && bondRequired) {
					getManagedBean().setSelectedTabIndex(7);
				} else if(typeSchedule || bondRequired){
					getManagedBean().setSelectedTabIndex(6);
				}
				getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
			}

			if (outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
				getManagedBean().setSelectedTabIndex(5);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(6);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(8);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(7);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(6);
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(7);
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(6);
				}
				getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
			}	

			if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
				getManagedBean().setSelectedTabIndex(6);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(7);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(9);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(8);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(7);
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(8);
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(7);
				}
				getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
			}
			/*#26083 start - basri*/
			if(outputTab.equalsIgnoreCase(ContractConst.NOTIFICATION_DETAIL)){
				getManagedBean().setSelectedTabIndex(7);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(8);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(10);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(9); //#35187
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(8); //#35187
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(9); //#35187
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(8); //#35187
				}
				
				//#35187 start
				if(!approverSetting){
					getManagedBean().setSelectedTabIndex(getManagedBean().getSelectedTabIndex()-1);
				}
				//#35187 end
				
				getManagedBean().setNotificationIndex(getManagedBean().getSelectedTabIndex());
			}
			/*Setting tab value if zonal is false*/
		} else {
			if(outputTab.equalsIgnoreCase(ContractConst.AGENCY_DETAIL)) {
				getManagedBean().setSelectedTabIndex(1);
				getManagedBean().setAgencyIndex(1);
			}
			if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
				getManagedBean().setSelectedTabIndex(2);
				getManagedBean().setItemIndex(2);
			
				ContractItemSearchBackingBean contractBb= new ContractItemSearchBackingBean();
				contractBb.getManagedBean().reassignValue(true);
				contractBb.getManagedBean().getContractListingPagingDataModel().reloadData();
				contractBb.getManagedBean().getServiceListingPagingDataModel().reloadData();
				contractBb.searchReset(); //  Fix - #31042
				
			}
			if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
				getManagedBean().setSelectedTabIndex(3);
				getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
			} 
			if (outputTab.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) && typeSchedule) {
				getManagedBean().setSelectedTabIndex(4);
				getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
			}
			if (outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL) && bondRequired) {
				getManagedBean().setSelectedTabIndex(4);
				if (typeSchedule) {
					getManagedBean().setSelectedTabIndex(5);
				}
				getManagedBean().setBondIndex(getManagedBean().getSelectedTabIndex());
			}
			if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
				getManagedBean().setSelectedTabIndex(4);
				if (typeSchedule && bondRequired) {
					getManagedBean().setSelectedTabIndex(6);
				} else if(typeSchedule || bondRequired){
					getManagedBean().setSelectedTabIndex(5);
				}
				getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
			}
			if (outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
				getManagedBean().setSelectedTabIndex(4);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(5);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(7);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(6);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(5);
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(6);
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(5);
				}
				getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
			}

			if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
				getManagedBean().setSelectedTabIndex(5);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(6);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(8);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(7);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(6);
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(7);
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(6);
				}
				getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
			}
			/*#26083 start - basri*/
			if (outputTab.equalsIgnoreCase(ContractConst.NOTIFICATION_DETAIL)) {
				getManagedBean().setSelectedTabIndex(6);
				if(typeSchedule){
					getManagedBean().setSelectedTabIndex(7);
					if (typeFactoring && bondRequired) {
						getManagedBean().setSelectedTabIndex(9);
					}else if(bondRequired || typeFactoring){
						getManagedBean().setSelectedTabIndex(8);
					}
				}else if(typeFactoring){
					getManagedBean().setSelectedTabIndex(7);
					if(bondRequired){
						getManagedBean().setSelectedTabIndex(8);
					}
				} else if(bondRequired){
					getManagedBean().setSelectedTabIndex(7);
				}
				
				//#35187 start
				if(!approverSetting){
					getManagedBean().setSelectedTabIndex(getManagedBean().getSelectedTabIndex()-1);
				}
				//#35187 end
				
				getManagedBean().setNotificationIndex(getManagedBean().getSelectedTabIndex());
			}
		}
	}

	/**
	 * The method used to check the accessibility of factoring tab menu.
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
	 */
	public boolean checkFactoringExpiryDate() {
		if (ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring() != null 
				&& ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus() != null
				&& (ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue() 
				|| ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.UF_COMPLETE.longValue() 
				|| ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.XF_PENDING_ACKNOWLEDGEMENT.longValue()
				|| ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.XF_PENDING_ENDORSEMENT.longValue())) {
			return true;
		}
		return false;
	}

	/*#28868 - basri start*/
	/**
	 * The method used to check boolean if allow factoring to show .
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
	 */
	public boolean allowShowFactoring(){
		
		boolean allow = false;
			if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()){
					allow = true;
				
			}else if(!ctBB.getManagedBean().getContractVer().getCtContract().isFactored()){
				if(ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoringExpired() != null){
					if(ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoringExpired().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_EXPIRED){
						allow = true;
				
					}
				}
				
			}
	
		
		return allow;
	}
	/*#28868 - basri end*/
	
	
	/**
	 * The method used to navigate to contract termination screen
	 * for termination purpose.
	 * 
	 * @param contractId
	 * @return the navigate URL 
	 */
	public String gotoTerminatePage(Long contractId ) {
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		TerminationBackingBean trBB = null;

		try {
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();

			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);	
			ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
			
			trBB = new TerminationBackingBean();
			trBB.getManagedBean().setViewContract(false);
			ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
			trBB.getManagedBean().setButtonHideActive(true);
			trBB.getManagedBean().setExternalPageUrl(null);
			trBB.setTerminationContractVer(reqDTO);
		} finally{
			contractSO = null;
			reqDTO = null;
			trBB= null;
		}
		return PROCESS_TERMINATE_PAGE;
	} 

	/**
	 * The method used to navigate to contract suspension screen
	 * for suspension purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoSuspendPage(Long contractId) {
		IContractSO	contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO  = new ContractReqDTO();   
		reqDTO.setContractId(contractId);
		Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		reqDTO.setContractVerId(contractVerId);
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
		reqDTO.setUpdateAction(true);
		ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
		ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
		SuspensionBackingBean	suspBB = new SuspensionBackingBean();
		
		//Development #29214 -performance Hafiz start
		suspBB.getManagedBean().getSuspensionProductListingPagingDataModel().resetData();
		suspBB.getManagedBean().getAgencyPersistDataModel().resetData();
		//Development #29214 -performance Hafiz end
		
		suspBB.getManagedBean().setViewOnly(false);
		suspBB.setSuspensionContractVer(reqDTO);
		return PROCESS_SUSPEND_PAGE;
	}

	/**
	 * The method used to navigate to contract amendment screen
	 * for amendment purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoAmendPage(Long contractId) {		
		ContractReqDTO reqDTO = new ContractReqDTO();
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		reqDTO.setContractId(contractId);
		Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		reqDTO.setContractVerId(contractVerId);
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
		reqDTO.setUpdateAction(true);

		ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
		ctBB.getManagedBean().setAmendFromViewPage(true); // #15851
		ctBB.getManagedBean().setViewSCL(false);//34142, 15851 to reset viewSCL before anything
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("amendManagedBean", null);//#15851
		AmendmentBackingBean amendBB = new AmendmentBackingBean();
		amendBB.getManagedBean().setSelectedTabIndex(0);
		/*#25956 Start*/
		amendBB.getManagedBean().setViewOnly(false); /*#25956 notification setting*/
		/*#25956 End*/
		//log.info("Check Putra gotoAmendPage contractId "+contractId);
		amendBB.setAmendmentContractVer(reqDTO);
		return PROCESS_AMEND_PAGE;
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoViewPage(Long contractId, Long contractVerId) {

		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractId(contractId);
// Fixing Bug #28550 - Start		
//		reqDTO.setContractVerId(contractVerId);
		CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		if (latestContractVer != null) {
			reqDTO.setContractVerId(latestContractVer.getContractVerId());
		} else {
			reqDTO.setContractVerId(contractVerId);
		}
		// Fixing Bug #28550 - End			
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
		reqDTO.setViewOnly(true);
		ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
		ctBB.getManagedBean().setViewContract(true);
		ctBB.getManagedBean().setFromViewPage(true); //#15851
		ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
		getManagedBean().setAsAtDate(null);
		setValidateContractField(reqDTO);
		getManagedBean().setSelectedTabIndex(0);
		getManagedBean().setExternalPageUrl(null);
		// Start - #30866
		if (getManagedBean().isContinuityCircularAuditSearch()) {
			getManagedBean().setFromContractViewPage(true);
		}
		// End - #30866
		getManagedBean().setLoaContractVerId(ctBB.getManagedBean().getContractVer().getContractVerId());
		reqDTO.setCtContractVer( ctBB.getManagedBean().getContractVer());
		getManagedBean().setWarningPercentage(contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage());
		return CONTRACT_PAGE;
	}

	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * This method is used by fulfillment module.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoCommonViewPage(Long contractId) {	
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractId(contractId);
		reqDTO.setContractVerId( contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
		reqDTO.setViewOnly(true); 
		reqDTO.setCommonView(true);
		ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
		ctBB.getManagedBean().setViewContract(true);
		ctBB.getManagedBean().setCommonView(true);
		ctBB.getManagedBean().setOutcomesFromContract(getManagedBean().getOutComes());//Bug 34142, 15851 - shaqimi 04/12/2022
		setValidateContractField(reqDTO);
		getManagedBean().setSelectedTabIndex(0); 
		getManagedBean().setAsAtDate(DateUtils.getCurrentDateWithoutTime());
		reqDTO.setCtContractVer(ctBB.getManagedBean().getContractVer());
		getManagedBean().setWarningPercentage(contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_COMMON_PAGE);

		return null;
	}

	/**
	 * The method used to navigate to contract update screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoUpdatePage(Long contractId, int agStatus) {
		IContractSO contractSO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);	
			reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			if (agStatus == 1) {
				reqDTO.setDocType(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT);
			} else {
				reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			}

			reqDTO.setUpdateAction(true);
			reqDTO.setViewOnly(true);//Bug 34142, 15851 - shaqimi start 04/12/2022
			ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
			ctBB.getManagedBean().setViewSCL(false);//34142, 15851 to reset viewSCL before anything
			ctBB.getManagedBean().setNotificationSetting(false);
			ctBB.getManagedBean().setViewContract(false);
			ctBB.getManagedBean().setUpdateContract(true);
			ctBB.getManagedBean().setCtAddress(null);
			setValidateContractField(reqDTO);
			getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBB.chkServiceTaxAlert();
			getManagedBean().setExternalPageUrl(null);
			getManagedBean().setSelectedTabIndex(0);	
			return CONTRACT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	
	/*#26083 start - basri*/
	/**
	 * The method used to navigate to notification setting screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
	
	public String gotoNotificationSetting(Long contractId){
		IContractSO contractSO = null;
		try{
			 contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			
			//#35156 - Start
			log.info("**Start gotoNotificationSetting From Contract Maintenance** Check Bug #35156 :"+" ContractVer :"+reqDTO.getContractVerId() 
					+", Contract id :"+reqDTO.getContractId());
				
			reqDTO.setUpdateAction(true);
			reqDTO.setViewOnly(true);//Bug 34142, 15851 - shaqimi start 04/12/2022
			reqDTO.setDocType(ParameterConst.DOCTYPE_NOTIFICATION_SETTING);
			ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
			ctBB.getManagedBean().setViewContract(false);
			ctBB.getManagedBean().setUpdateContract(true);
			ctBB.getManagedBean().setNotificationSetting(true);
			ctBB.getManagedBean().setCtAddress(null);
			setValidateContractField(reqDTO);
			getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBB.chkServiceTaxAlert();
			getManagedBean().setExternalPageUrl(null);
			getManagedBean().setSelectedTabIndex(0);
			
			//#35156 - End
			log.info("**END gotoNotificationSetting From Contract Maintenance** Check Bug #35156 :"+" ContractVer :"+reqDTO.getContractVerId() 
					+", Contract id :"+reqDTO.getContractId());
			
			return CONTRACT_PAGE;
		}finally{
			contractSO = null;
		}
	}
	/*#26083 end*/

	/**
	 * The method used to navigate to contract termination history screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoTerminateHistoryPage(Long contractId) {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);

		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(contractId);
		ctReqDTO.setContractVerId(contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId());

		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
		ctBB.renewSesionObject(); //#15851
		trBB.setCtBB(new ContractBackingBean());	
		trBB.getCtBB().getManagedBean().setViewContract(true);
		trBB.getCtBB().getManagedBean().setContractVer(resDTO.getCtContractVer());
		trBB.getManagedBean().setContractVer(resDTO.getCtContractVer()); //#15851
		trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract()); //#15851
		trBB.getManagedBean().setExternalPageUrl(null);
		ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/

		// Enable or Disable GST AND STAX field
		if (!ctBB.isLoaContractWithGst(trBB.getCtBB().getManagedBean().getContractVer().getCtContract().getLoa().getGstEffDate())
				&& !ctBB.isLoaContractWithGst(trBB.getCtBB().getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
			ctBB.getManagedBean().setGstCheckingGUI(false);	// Display N/A
		} else {
			if (trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
				trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
			}
			ctBB.getManagedBean().setGstCheckingGUI(true);
		}
		
		if (trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().getStaxAmount() == null) {
			trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().setStaxAmount(BigDecimal.ZERO);
		}
		ctBB.getManagedBean().setStaxCheckingGUI(true);
	
		
		
		
		trBB.retrieveContractVerOfAgreement();
		trBB.getManagedBean().setTerminationList(contractSO.retrieveTerminationHistoryList(ctReqDTO).getTerminationHistoryList());	
		return TERMINATE_HISTORY_PAGE;
	}

	/**
	 * The method used to navigate to change bank account screen
	 * for change bank purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoChangeBankPage(Long contractId) {

		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;

		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			//reqDTO = new ContractReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
			ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
			reqDTO.setUpdateAction(true);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.setChangeBankDetails(reqDTO);
			//supplierBB.renewSesionObject();
		} finally {
			reqDTO  = null;
			contractSO   = null;
		}
		return CHANGE_BANK_ACCOUNT_PAGE;
	}

	/**
	 * The method ised to navigate to request or remove contract admin
	 * @param contractId
	 * @return the navigate URL
	 */
	
	/*#21387-basri start*/
	 public String goToRequestCtAdmin(Long contractId,String doctype){
		 
		 	IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			CtContractVer contractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
			
			reqDTO.setContractVerId(contractVer.getContractVerId());
			reqDTO.setDocType(doctype);
			reqDTO.setUpdateAction(true);
			ctBB.renewSesionObject();//Bug 34142, 15851 - shaqimi start 06/12/2022
			ctBB.getManagedBean().setNotificationSetting(false);
			RequestCtAdminBackingBean	rcaBB = new RequestCtAdminBackingBean();
			rcaBB.getManagedBean().setViewOnly(false);
			rcaBB.getManagedBean().setExternalPageUrl(null);
			rcaBB.setRequestCtAdminContractVer(reqDTO);
			
			/*if(rcaBB.getManagedBean().getContractVer())*/
			
			return REQUEST_CONTRACT_ADMIN;
		
		
	 }
	 /*#21387-basri end*/
	
	/**
	 * The method used to navigate to request factoring screen
	 * for request or remove factoring company purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoFactoringPage(Long contractId,boolean update) {		
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;

		try {
			ctBB.getManagedBean().setNotificationSetting(false);/*#26083 start - basri*/
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);

			resDTO = contractSO.retrieveFactoringDetailsByContractId(reqDTO);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.renewSesionObject();
			if (resDTO.isSuccessful()) {
				supplierBB.getManagedBean().setCtContract(resDTO.getContract());
				if (resDTO.getContract().getLatestContractVer() != null) {
					supplierBB.getManagedBean().setContractVer(resDTO.getContract().getLatestContractVer());
				} else {
					supplierBB.getManagedBean().setContractVer(resDTO.getContract().getCurrentContractVer());
				}	    	

				if (supplierBB.getManagedBean().getCtContract().getCtFactoring() != null &&  supplierBB.getManagedBean().getCtContract().isFactored()
						&& (supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()
								|| supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.UF_COMPLETE.longValue())
						&& !update) {
					log.debug("remove factoring");
					supplierBB.getManagedBean().setFactoring(resDTO.getContract().getCtFactoring());
					supplierBB.getManagedBean().getFactoring().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
					supplierBB.getManagedBean().setRemoveFactoring(true);
					supplierBB.getManagedBean().setUpdateFactoring(false);
					supplierBB.getManagedBean().setDocType(ParameterConst.DOCTYPE_REMOVE_FACTORING);
					PmStatus pmStatus = new PmStatus();
					pmStatus.setStatusId(ContractStatusConst.XF_PENDING_SUBMISSION);
					supplierBB.getManagedBean().getFactoring().setStatus(pmStatus);
					supplierBB.getManagedBean().setAccBankName(null);
					supplierBB.getManagedBean().setAccBankName(
							supplierBB.getManagedBean().getFactoring().getBankAccountNo()
							+ ContractConst.SPACE_SLASH 
							+ supplierBB.getManagedBean().getFactoring().getBankName());
					supplierBB.getCtBB().initializeRemarkAndAttachement(null, supplierBB.getManagedBean().getRemarkList(), supplierBB.getManagedBean().getAttachmentList());
					supplierBB.getManagedBean().setFcRequestType(ParameterConst.REMOVE_TYPE_FACTORING);
					
				}else if(supplierBB.getManagedBean().getCtContract().getCtFactoring() != null &&  supplierBB.getManagedBean().getCtContract().isFactored()
						&& (supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue() 
						|| supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.UF_COMPLETE.longValue())
						&& update){
					/*25139-basri*/
					log.debug("update factoring");
					supplierBB.initializeUpdateFactoring(resDTO);
					supplierBB.getManagedBean().getFactoring().setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
					supplierBB.getManagedBean().setRemoveFactoring(false);
					supplierBB.getManagedBean().setUpdateFactoring(true);
					supplierBB.getManagedBean().setDocType(ParameterConst.DOCTYPE_UPDATE_FACTORING);
					supplierBB.getManagedBean().setFcRequestType(ParameterConst.UPDATE_TYPE_FACTORING);
					/*25139-end*/
				}else {
					log.debug("request factoring");
					supplierBB.initializeFactoring();
					supplierBB.getManagedBean().setRemoveFactoring(false);
					supplierBB.getManagedBean().setUpdateFactoring(false);
					supplierBB.getManagedBean().setDocType(ParameterConst.DOCTYPE_REQUEST_FACTORING);
					//#32555 start
					supplierBB.getManagedBean().setFcRequestType(ParameterConst.REQUEST_TYPE_FACTORING);
					//#32555 end
				}
				supplierBB.getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, supplierBB.getManagedBean().getDocType(), supplierBB.getManagedBean().getCtContract().getContractNo()));
				supplierBB.setCtBB(new ContractBackingBean(supplierBB.getManagedBean().getFileDirectory()));
			}			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			contractSO = null;
		}
		return REQUEST_FACTORING_PAGE;
	}

	/**
	 * The method used to navigate to previous screen.
	 * 
	 * @return the navigate URL 
	 * @throws IOException 
	 */
	public String gotoBackPage() throws IOException {
		try {
			String url = getManagedBean().getGotoPreviewPage();
			Long contractId =  ctBB.getManagedBean().getContractVer().getCtContract().getContractId();
			boolean isTrue =  getManagedBean().isBackPageAmendment();
			String taskId = getManagedBean().getTaskId();
			//Start Fix - #30866
			if(!getManagedBean().isContinuityCircularAuditSearch()){
				renewSesionObject();
			}
			//End Fix - #30866
			if(isTrue && taskId == null){
				return gotoAmendPage(contractId);
			}else if (isTrue && taskId != null){
				FacesScopes facesScope = new FacesScopes();
				ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
				String commonUrl = themeDisplay.getURLCurrent().substring(
						0,themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup()
								.getFriendlyURL())	+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(commonUrl + "/" +ContractConst.NAV_TO_CONTRACT_AMENDMENT + "?taskId="+ taskId);
			} else{
				return url;
			}
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * The method used to navigate to previous screen.
	 * This method will navigate to contract maintenance screen
	 * when the previous screen is not set. 
	 * 
	 * @return the navigate URL 
	 */
	public String backAction() {
		ctBB.getManagedBean().setViewContract(false);
		ctBB.getManagedBean().setFromViewPage(false); //#15851
		getManagedBean().setLoaContractVerList(null);
		getManagedBean().setLoaContractVerId(null); //#15851
		ContractItemSearchBackingBean  contractBB= new ContractItemSearchBackingBean();
		contractBB.getManagedBean().setItemcode(null);
		contractBB.getManagedBean().setItemname(null);
		contractBB.getManagedBean().setItemstatus(null);
		contractBB.getManagedBean().setItemtype(null);
		contractBB.getManagedBean().setUOM(null);
		contractBB.getManagedBean().setSkuno(null);
		contractBB.getManagedBean().setTypeofgoods(null);

		ctBB.getManagedBean().setSelectedItem(null);
		ctBB.getManagedBean().setServiceselectedItem(null);
		
		//Bug 34030 - shaqimi start
		//Bug 34142 - shaqimi start 25/11/2022
		if(!ctBB.getManagedBean().isViewContractFromAgreement()){
			ctBB.getManagedBean().setAmendFromTaskList(false);
		}
		//Bug 34142 - shaqimi end 25/11/2022
		ctBB.getManagedBean().setAmendFromViewPage(false);
		ctBB.getManagedBean().setViewSCL(false);
		ctBB.getManagedBean().setHasAmendmentLoaNo(false);
		ctBB.getManagedBean().setViewContract(false);
		ctBB.getManagedBean().setBackFromSCL(false);
		ctBB.getManagedBean().setBackFromContract(true);//Bug 34142 - shaqimi 04/12/2022
		ctBB.getManagedBean().setInPage(false);//Bug 34142 - shaqimi 04/12/2022
		//Bug 34030 - shaqimi end
		
		ctBB.getManagedBean().setViewContractFromAgreement(false);//Bug 34142 - shaqimi 16/11/2022
		
		contractBB.getManagedBean().getContractListingPagingDataModel().resetData();
		contractBB.getManagedBean().getServiceListingPagingDataModel().resetData();
		contractBB.getManagedBean().setResetval(true);
		// Start - #30866
		CircularBackingBean circularBB = new CircularBackingBean();
		if (circularBB.getManagedBean().getExternalPageUrlForAudit() != null) {
			getManagedBean().getContractDataModel().reloadData();
			getManagedBean().setExternalPageUrl(circularBB.getManagedBean().getExternalPageUrlForAudit());
		}

		if (getManagedBean().isContinuityCircularAuditSearch()) {

			if (getManagedBean().isFromContractViewPage()) {
				getManagedBean().setFromContractViewPage(false);
				return CONTRACT_VIEW_AUDIT;
			} else if (getManagedBean().isFromResultViewAudit()) {
				getManagedBean().setFromResultViewAudit(false);
				getManagedBean().setSearchContractNo(null);
				return CONTRACT_MAINT_PAGE;
			}
		}
		// End - #30866
		//#25790 - start
		if(getManagedBean().getDpContinuitySession() != null){
			getManagedBean().setExternalPageUrl(getManagedBean().getDpContinuitySession());
		}			
	    //#25790 - end
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
	}

	/**
	 * The method used to retrieve the payment tracking information.
	 */
	private void calPaymentTracking() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		
		IFlItemSummarySO itemSummaryPaid = (IFlItemSummarySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFlItemSummarySO.class);
	
		ContractReqDTO reqDTO = new ContractReqDTO();
		ContractResDTO resDTO = new ContractResDTO();
		reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
		reqDTO.setAsAtDate(getManagedBean().getAsAtDate());
		resDTO = contractSO.calAdvancePaymentTracking(reqDTO);
		
		FlItemSummaryReqDTO reqsumDTO = new FlItemSummaryReqDTO();
		reqsumDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
		
		BigDecimal prevSumAmt = itemSummaryPaid.retrieveContractAccumulatedAmount(reqsumDTO);
		BigDecimal sumOrderedAmt = BigDecimal.ZERO;


		BigDecimal paidAdvancePaymentAmt = (resDTO.getPaidAdvancePaymentAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidAdvancePaymentAmt();
		BigDecimal paidInvoiceAmt = (resDTO.getPaidInvoiceAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidInvoiceAmt();
		BigDecimal paidsumInvoiceAmt = (sumOrderedAmt.add(prevSumAmt) == null) ? BigDecimal.valueOf(0) : sumOrderedAmt.add(prevSumAmt);

		ctBB.getManagedBean().setPaidAdvancePaymentAmt(paidAdvancePaymentAmt);
		ctBB.getManagedBean().setPaidInvoiceAmt(paidInvoiceAmt);
		ctBB.getManagedBean().setPaidsumInvoiceAmt(paidsumInvoiceAmt);
	}


	/**
	 * The method used for generating a list of status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of status
	 */
	public SelectItem[] getSearchStatusDropdown() {
		SelectItem[] items = null;
		try {
			items = new SelectItem[4];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_ALL,
					ResourceBundleUtils.getMessage("lbl.bpm.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_COMPLETE,  
					ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_INCOMPLETE,  
					ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * The method used for generating a list of contract status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of contract status
	 */
	public SelectItem[] getSearchContractStatusDropdown() {
		SelectItem[] items = null;

		try {
			items = new SelectItem[5];
			int i = 0;
			if (isSupplierRole()) {
				items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.bpm.all", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			} else {
				items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			}
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_EFFECTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_INACTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_EXPIRED,  
					ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_TERMINATED,  
					ResourceBundleUtils.getMessage("lbl.ct.terminated", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}

		return items;
	}

	/**
	 * The method used to retrieve the contract status of the contract.
	 * 
	 * @return Effective, Terminated, Expired, Inactive
	 */
	public String getContractStatusDesc(int contractStatus) {
		if (contractStatus == ContractConst.CONTRACT_STATUS_EFFECTIVE) {
			return ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_INACTIVE	) {
			return ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_EXPIRED	) {
			return ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_TERMINATED	) {
			return ResourceBundleUtils.getMessage("lbl.ct.terminated", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return "";
		}
	}

	/**
	 * The method used to retrieve the status completion of the contract.
	 * 
	 * @return complete or incomplete
	 */
	public String chkContractStatusType(int status) {
		if (status == ContractConst.STATUS_COMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (status == ContractConst.STATUS_INCOMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage("lbl.ct.not.applicable", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
	}

	/**
	 * The method used for initializing the remarks of contract.
	 */
	private void initializeRemarks() {
		initiateRemark(ctBB.getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getSettingRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getNotiSettingRemarks());/*#26083 start - basri*/
	}

	/**
	 * The method used for initializing the remarks of contract.
	 * This method will create new remark object for every single new 
	 * process, else will display all remarks.
	 * 
	 * @param remarkList  the list of remarks to display. If the remarkList is null, 
	 *                    initiate a new remark
	 */
	private void initiateRemark(List<CtRemark> remarks) {
		boolean ctWorkflowStatusMatch = false;
		if (ctBB.getManagedBean().getContractVer().getCtWorkflowStatus() != null) {
			if (remarks.size()>0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (ctBB.getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId() 
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}

		//Bug #29446 - start
		//log.info(ctBB.getManagedBean().isViewContract());
		if(!ctBB.getManagedBean().isViewContract()){
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
		//Bug #29446 - end
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
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
	 */
	public String getPortletTitle() {
		String portletTitle = null;
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.ministry.contract.maintenance",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.central.contract.maintenance",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (isSupplierRole()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.list",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}

	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
	 */
	public String getViewPortletTitle() {
		String portletTitle = null;
		if (ctBB.getManagedBean().isViewContract()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.view.contract",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
			//#25406
		}  else if(ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.owner.management",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.maintenance",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}

	/**
	 * The method used to retrieve the fulfilment
	 *  details base on contract expire date or current date
	 */
	private void retrieveFulfilmentDetails() {
		// Retrieve fulfilment details base on contract exp date
		if (getManagedBean().getAsAtDate() == null) {
			getManagedBean().setAsAtDate(ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		}
		
		// #24333,#36491 - Start
		String docType = ctBB.getManagedBean().getContractVer().getDocType();
		boolean isAmendment = ctBB.isAlreadyAmendment();
		// #24333,#36491 - End

		for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContractAmounts()) {
			if (getManagedBean().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
					&& getManagedBean().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()){
				processPerformanceBonds(contractAmt, docType, isAmendment); //#24333
				ctBB.getManagedBean().setCtContractAmount(contractAmt);
			}

			// Bug #26542 - start
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date CurrentDate = new Date();
			if(dateFormat.format(contractAmt.getEffDate()).compareTo(dateFormat.format(CurrentDate))<=0
					&& dateFormat.format(contractAmt.getExpDate()).compareTo(dateFormat.format(CurrentDate)) >=0){
				processPerformanceBonds(contractAmt, docType, isAmendment); //#24333
				ctBB.getManagedBean().setCtContractAmount(contractAmt);
				break; // Exit loop after processing the first matching
						// contractAmt
			}
			// Bug #26542 - end
		}
		

		//Retrieve agreement contract version for physical no & signed date
		retrieveContractVerOfAgreement();

		// Retrieve overall contract amount
		if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() != null && ctBB.getManagedBean().getCtContractAmount().getStaxAmount() == null ) {
			ctBB.getManagedBean().setGstTotalAmount
					(ctBB.getManagedBean().getCtContractAmount().getGstAmount().add
					(ctBB.getManagedBean().getCtContractAmount().getContractAmount()));
		} else
		if (ctBB.getManagedBean().getCtContractAmount().getStaxAmount() != null && ctBB.getManagedBean().getCtContractAmount().getGstAmount() == null) {
			ctBB.getManagedBean().setGstTotalAmount
					(ctBB.getManagedBean().getCtContractAmount().getStaxAmount().add
					(ctBB.getManagedBean().getCtContractAmount().getContractAmount()));
		} else
		if (ctBB.getManagedBean().getCtContractAmount().getStaxAmount() != null && ctBB.getManagedBean().getCtContractAmount().getGstAmount() != null) {
					ctBB.getManagedBean().setGstTotalAmount
							(ctBB.getManagedBean().getCtContractAmount().getStaxAmount().add
							(ctBB.getManagedBean().getCtContractAmount().getGstAmount().add
							(ctBB.getManagedBean().getCtContractAmount().getContractAmount())));

		} else {
			ctBB.getManagedBean().setGstTotalAmount(ctBB.getManagedBean().getCtContractAmount().getContractAmount());

			}
		
	}

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

			if (ctBB.getManagedBean().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(ctBB.getManagedBean().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);				
				if (amendmentResDTO.getCtAmendment() != null) {
					ctBB.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);

					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					ctBB.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}
				// Display the contract physical no of the agreement version during contract update
				if (getManagedBean().getTaskId() == null) {
					ctBB.getManagedBean().getContractVer().setContractPhysicalNo(ctBB.getManagedBean().getAgreementContractVer().getContractPhysicalNo());
				}
			} else {
				ctBB.getManagedBean().setAgreementContractVer(ctBB.getManagedBean().getContractVer());
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
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * The method used to set update value false for disable purpose.
	 */
	public void disableUpdate() {
		getManagedBean().setAutoUpdate(false);
	}	

	/**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {
		String userRole = null;	
		log.info("added checking include process name without space to cater for Oracle 12C ["+this.getClass().getSimpleName()+"][getUserRole]");
		if (getManagedBean().getTaskName() == null
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_COM_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_COM_HT_NAME.replaceAll("\\s", "")))
				|| (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_COM_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_COM_HT_NAME.replaceAll("\\s", "")))) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
				//#25406 - Start
			}else if ((ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					|| ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY))
					&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
				//#25406 - End
			}
		} else if (getManagedBean().getTaskName() != null
				&& ((getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FD_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FD_HT_NAME.replaceAll("\\s", ""))) || (getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME.replaceAll("\\s", ""))))) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
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
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The method used to verify the login user is central contract administrator.
	 * 
	 * @return true, if login user is central contract administrator
	 *         false, if login user is not central contract administrator
	 */
	public boolean isBPKCCA() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
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

	
//	#21387 - basri
	public boolean isSelectedCtAdmin(Long userName ,Long flag){
		
		if(userName.intValue() == 0){
			return true;
		}
		else if(userName.intValue() == getFacesScopes().getUserSessionBean().getUserId().intValue() && flag == 1 && isContractAdmin()){
			return true;
		}else if(userName.intValue() != getFacesScopes().getUserSessionBean().getUserId().intValue() && flag == 1 && isContractAdmin()){
			return false;
		}else{
			return true;
		}
		
		
	}
//	#21387 - basri
	
	/**
	 * The method used to disable the ministry lov
	 * if login user is central contract administrator
	 * or ministry administrator
	 * or internal auditor.
	 * 
	 * @return true, if login user is central contract administrator/ministry administrator/internal auditor
	 *         false, if login user is not central contract administrator/ministry administrator/internal auditor
	 */
	public boolean disableMinistryLov() {
		return (isContractAdmin()
				|| ctBB.isApprover() 
				|| (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) 
						&& !getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())));
	}

	/**
	 * The method used to set the ministry 
	 * profile id and PTJ profile id
	 */
	private void defaultMinistryPtjSearchValue() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) {
			this.getManagedBean().setSearchMinistryProfile(null);
			this.getManagedBean().setSearchPtjGroupProfile(null);
			this.getManagedBean().setSearchPtjProfile(null);

		} else {
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			
			//Enhancement 29418 - shaqimi start 21/10/2022
			Boolean hasPBQCRole = 
					(getFacesScopes().hasRoleUser(RoleCode.QCA_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(RoleCode.QCA_MEMBER.value()) || getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())
							|| getFacesScopes().hasRoleUser(RoleCode.QCB_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(RoleCode.QCB_MEMBER.value()) || getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())
							|| getFacesScopes().hasRoleUser(RoleCode.PBA_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(RoleCode.PBA_MEMBER.value()) || getFacesScopes().hasRoleUser(RoleCode.PBA_SECRETARY.value())
							|| getFacesScopes().hasRoleUser(RoleCode.PBB_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(RoleCode.PBB_MEMBER.value()) || getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())
							|| getFacesScopes().hasRoleUser(RoleCode.PPB_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(RoleCode.PPB_MEMBER.value()) || getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value()));
			//Enhancement 29418 - shaqimi end 21/10/2022
			
			if (userResDTO.getUserOrgList()!=null && userResDTO.getUserOrgList().size()>0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}

			if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {			
				this.getManagedBean().setSearchMinistryProfile(orgProfile);
				this.getManagedBean().setSearchPtjGroupProfile(null);
				this.getManagedBean().setSearchPtjProfile(null);
			} else if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
					|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())
					|| hasPBQCRole) {//Enhancement 29418 - shaqimi 21/10/2022
				this.getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
				if (this.getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == -1) {
					this.getManagedBean().setSearchPtjGroupProfile(null);
				}
				this.getManagedBean().setSearchPtjProfile(orgProfile);
			}
		}
	}

	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		String condition = OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getSearchMinistryProfile().getOrgProfileId();
		return condition;
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPtjCondition() {
		String condition = null;

		if (this.getManagedBean().getSearchPtjGroupProfile() != null) {
			condition = OrgProfileUtil.getFromToSearchBeanParentString(
					OrganizationType.DIVISION.value(), OrganizationType.DEPARTMENT.value())+"^"+
					this.getManagedBean().getSearchPtjGroupProfile().getOrgProfileId();
		} else {
			condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
					this.getManagedBean().getSearchMinistryProfile().getOrgProfileId();
		}
		return condition;
	}

	/**
	 * The method used reset PTJ/PTJ Group profile value once select the Ministry
	 */
	public void resetPtjGroupProfile() {
		getManagedBean().setSearchPtjProfile(null);
		getManagedBean().setSearchPtjGroupProfile(null);
	}

	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setSearchPtjProfile(null);
	}

	/**
	 * The method used for generating a list of available history of
	 * loa contract version as selection.
	 * 
	 * @return loaContractVerList  the selection list
	 */
	public SelectItem[] getLoaContractVerList() {
		if(getManagedBean().getLoaContractVerList() == null ) 
		{
			SelectItem[] items = null;
			IContractSO contractSO = null;
			ContractReqDTO reqDTO = null;
			ContractResDTO resDTO = null;
			try {
				int i = 0;
				if(ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) 
				{	        		
					contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
					reqDTO = new ContractReqDTO();
					resDTO = new ContractResDTO();

					reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
					resDTO = contractSO.retrieveLoaContractVerByContractId(reqDTO);

					if (resDTO.getList() != null && resDTO.getList().size() > 0) {
						items = new SelectItem[resDTO.getList().size()];
						for (Object[] object : resDTO.getList()) {
							items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
						}
					}
				}  else {
					items = new SelectItem[1];
					items[i++] = new SelectItem(ctBB.getManagedBean().getContractVer().getContractVerId(), ctBB.getManagedBean().getContractVer().getCtContractValue().getLoaPhysicalNo());
				}
				getManagedBean().setLoaContractVerList(items);	        		
			} finally {
				contractSO = null;
				reqDTO = null;
				resDTO = null;
			}
		}
		return getManagedBean().getLoaContractVerList();
	}

	/**
	 * The method used to reflect the contract details base on selected loa
	 */
	public void changeLoaContractVer() {
		if (getManagedBean().getLoaContractVerId() != null)
		{
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
			reqDTO.setViewOnly(true);
			reqDTO.setContractVerId(getManagedBean().getLoaContractVerId());
			log.info(getManagedBean().getLoaContractVerId());
			ctBB.getManagedBean().setViewContract(true);
			getManagedBean().setAsAtDate(null);
			setValidateContractField(reqDTO);
		}
	}

	/**
	 * The method used to set the contract id to
	 * proceed for fulfilment
	 * 
	 * @param contractId contract id
	 */
	public void setContractId(Long contractId){
		getManagedBean().setContractId(contractId);
	}

	/**
	 * The method used to proceed for fulfilment with / without agreement
	 * of migrated contract.
	 * 
	 * @param withAgreement  the flag to proceed with agreement
	 */
	public void proceedForFulfilment(boolean withAgreement) {
		if (withAgreement) {
			proceedForFulfilmentMsg = ResourceBundleUtils.getMessage("lbl.ct.proceed.with.agreement",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()); 			
		} else {
			proceedForFulfilmentMsg = ResourceBundleUtils.getMessage("lbl.ct.proceed.without.agreement",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}

		IContractSO	contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO  = new ContractReqDTO();
		reqDTO.setContractId(getManagedBean().getContractId());
		log.info("CEK VerID :"+getManagedBean().getLoaContractVerId());
		Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		
		reqDTO.setContractVerId(contractVerId);
		reqDTO.setAgreement(withAgreement);
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		contractSO.proceedForFulfilment(reqDTO);
		getManagedBean().getContractDataModel().reloadData();

	}
	
	private void sortDeductions() {
		// sort created date asc
		Collections.sort(ctBB.getManagedBean().getContractVer().getCtDeductions(),
				new Comparator<CtDeduction>() {
					public int compare(CtDeduction o1, CtDeduction o2) {
						if (o2.getCreatedDate() == null
								|| o1.getCreatedDate() == null)
							return 0;
						return o1.getCreatedDate().compareTo(
								o2.getCreatedDate());
					}
				});
	}
	
	//#25406 - Start
	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoOwnerManagementPage(Long contractId) {
		IContractSO contractSO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT);
			
		
			reqDTO.setUpdateAction(true);
			ctBB.renewSesionObject(); //#15851
			ctBB.getManagedBean().setNotificationSetting(false);
			ctBB.getManagedBean().setViewContract(false);
			ctBB.getManagedBean().setUpdateContract(true);
			ctBB.getManagedBean().setCtAddress(null);
			setValidateContractField(reqDTO);
			getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBB.chkServiceTaxAlert();
			ctBB.getManagedBean().setOutcomesFromContract(getManagedBean().getOutComes());//Bug 34142, 15851 - shaqimi 04/12/2022
			getManagedBean().setExternalPageUrl(null);
			getManagedBean().setSelectedTabIndex(0);	
			return PROCESS_OWNER_MANAGEMENT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	/**
	 * @return the selectedRecommendedAgency
	 */
	public CtAgency getSelectedRecommendedAgency() {
		return selectedRecommendedAgency;
	}
	
	/**
	 * @param selectedRecommendedAgency the selectedRecommendedAgency to set
	 */
	public void setSelectedRecommendedAgency(CtAgency selectedRecommendedAgency) {
		this.selectedRecommendedAgency = selectedRecommendedAgency;
	}
	
	/**
	 * Method to set the selected recommended agency
	 */
	public void setSelectedRecommendedAgency() {
		if (ctBB.getManagedBean().getAgencySelected() != null && ctBB.getManagedBean().getAgencySelected().size() >0 ) {
			for(PmOrgProfile newOrgProfile :ctBB.getManagedBean().getAgencySelected() ){
				getManagedBean().setOldMinistryProfile(retrieveMinistryOrgProfile(
						ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
				getManagedBean().setSearchPtjProfile(newOrgProfile);
				getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(newOrgProfile.getOrgProfileId()));
				getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(newOrgProfile.getOrgProfileId()));
				//log.info("Cek OrgName List : "+newOrgProfile.getOrgName());
				//log.info("Cek OrgName List : "+getManagedBean().getSearchPtjProfile().getOrgProfileId());
				//log.info("Cek OrgName List : "+getManagedBean().getSearchMinistryProfile().getOrgProfileId());
				//log.info("Cek OrgName List : "+getManagedBean().getSearchPtjGroupProfile().getOrgProfileId());
			}
			
		}
	}
	
	/**
	 * The method used to verify the login user is BPK CC approver 
	 * or contract approver.
	 * 
	 * @return true, if login user is BPK CC approver or contract approver
	 *         false, if login user is not BPK CC approver or contract approver
	 */
	public boolean isApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return (facesScopes.hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| facesScopes.hasRoleUser(RoleCode.BPK_CC_APPROVER.value()));
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract owner management details screen.
	 */
	public void preRenderContractOwnerManagement() {
		renewSesionObject();
		defaultMinistryPtjSearchValue();
		FacesContext fc = FacesContext.getCurrentInstance();			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, PROCESS_OWNER_MANAGEMENT_PAGE);	
	}
	//#25406 - End
	
	//#25406 - Start
	/**
	 * The method used for validating Remark Cancel Button.
	 * A warning message will display on screen when Remark is null
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void validateRemarkCancel(String action) throws Exception {
		
		String remark = getManagedBean().getRemarkList().get(0).getRemark();
		String agencyRemark = ctBB.getManagedBean().getContractVer().getAgencyRemarks().get(0).getRemark();
		String zoneRemark = ctBB.getManagedBean().getContractVer().getZoneRemarks().get(0).getRemark();
		String itemRemark = ctBB.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
		String deductionRemark = ctBB.getManagedBean().getContractVer().getDeductionRemarks().get(0).getRemark();
		String scheduleRemark = ctBB.getManagedBean().getContractVer().getScheduleRemarks().get(0).getRemark();
		String settingRemark = ctBB.getManagedBean().getContractVer().getSettingRemarks().get(0).getRemark();
		String notificationRemark = ctBB.getManagedBean().getContractVer().getNotiSettingRemarks().get(0).getRemark();/*#26083 start - basri*/
		if ((remark == null || remark.length() == 0) 
				&& (agencyRemark == null || agencyRemark.length() == 0) 
				&& (zoneRemark == null || zoneRemark.length() == 0)
				&& (itemRemark == null || itemRemark.length() == 0) 
				&& (deductionRemark == null || deductionRemark.length() == 0)
				&& (scheduleRemark == null || scheduleRemark.length() == 0) 
				&& (settingRemark == null || settingRemark.length() == 0)
				&& (notificationRemark == null || notificationRemark.length() == 0)) {
			getManagedBean().setSelectedTabIndex(0);
			throwExceptions(MessageConst.ERROR_100064, "lbl.ct.remark");
		}
		

	}
	//#25406 - End
	//29551 - start
	/**
	 * The method used to retrieve proper deduction clause reference portlet name from property file.
	 * 
	 * @return portletTitle  the name of the portlet
	 */
	public String getFlDeductionClausePortletTitle() {
		return ResourceBundleUtils.getMessage("lbl.fl.deduction.clause.ref.title",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	/**
	 * The method used for searching the Deduction Clause Reference of contract.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchContractDeductionClause() throws Exception {
		getManagedBean().getContractFlDataModel().reloadData();		
	}

	/**
	 * The method used to clear the previous viewed information before
	 * rendering the Deduction Clause Reference screen.
	 * @throws Exception 
	 */
	public void preRenderDeductionClause() {
		renewSesionObject();
		Calendar now = Calendar.getInstance();
		getManagedBean().setSearchFinancialYear(now.get(Calendar.YEAR));//set current year as default
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, DEDUCTION_CLAUSE_PAGE);
	}
	
	/**
	 * The method used to reset the search criteria for the Deduction Clause Reference screen.
	 */
	public void searchContractDeductionClauseReset() {
		this.getManagedBean().setSearchContractNo(null);
		this.getManagedBean().setSearchContractName(null);
		this.getManagedBean().setSearchCtStartDate(null);	
		this.getManagedBean().setSearchCtEndDate(null);	
		this.getManagedBean().setSearchCtPhysicalNo(null);	
		this.getManagedBean().setSearchLoaNo(null);
		this.getManagedBean().setSearchPurchaseOrderNo(null);
		this.getManagedBean().setSearchContractStatus(0);//ALL default
		Calendar now = Calendar.getInstance();
		this.getManagedBean().setSearchFinancialYear(now.get(Calendar.YEAR));
		this.getManagedBean().setSearchMinistryProfile(null);
		this.getManagedBean().setSearchPtjGroupProfile(null);
		this.getManagedBean().setSearchPtjProfile(null);
	}
	/**
	 * Reset selected deduction on row toggle.
	 * 
	 */
	public void onRowToggle() {
		getManagedBean().setSelectedDeduction(null);
    }
	/**
	 * Get the user current language
	 * @return languageCode
	 */
	public String getLanguageCode() {
		 return new FacesScopes().getUserLocale().getLanguage();
	}
	
	/**
	 * Get year drop down
	 * 
	 * @return array of SelectItem
	 */
	public SelectItem[] getYearDropDownOpt() {
		SelectItem[] items = null;
		try {
			Calendar now = Calendar.getInstance();
			int currentYear = now.get(Calendar.YEAR);
			int yearRange = CommonConstants.YEAR_RANGE;
			int yearDiff = currentYear - yearRange;

			items = new SelectItem[yearRange + 1];
			int i = 0;

			for (int y = currentYear; y >= yearDiff; y--) {
				items[i++] = new SelectItem(y, String.valueOf(y));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;

	}	
	//29551 - end
	
	
	//#25406 - Start
	/**
	 * The method used for validating the Contract Owner Management.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this process. The validation 
	 * 					will invoke base on the action
	 */
	public void validateContractOwnerManagement(String action) throws EpException {
		getManagedBean().setAutoUpdate(true);
		
		/*Validation for Contract Owner Management */
		if((action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) &&
				ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT)){
			if(getManagedBean().getSearchPtjProfile() != null){
				if(getManagedBean().getSearchPtjProfile().getOrgProfileId().equals(ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId())){
					getManagedBean().setSelectedTabIndex(0);
					throw new EpException(MessageConst.ERROR_775156);
				}
			}else{
				getManagedBean().setSelectedTabIndex(0);
				throw new EpException(MessageConst.ERROR_775156);
			}
		}
	}
	//#25406 - End
	
	// Start Fix - #30856
	/**
	 * The method used to delete Datatable row
	 * for ctPerformanceBond Datatable,
	 * No index selection required 
	 */
	public void removePerformanceDatatableRow() {
		try {
			if (ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null) {
				Iterator<CtPerformanceBond> performanceBond = ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().iterator();
				while (performanceBond.hasNext()) {
					CtPerformanceBond  bond = performanceBond.next();
				      if (bond != null){
						performanceBond.remove();
				    }
				}
			}
	
			ctBB.getManagedBean().setButtonHideActive(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {

		}
	}
	//Start - Fix #29368
	/**
	 * The method used to check the bond exempted condition,
	 * set bondExempted false if no attachment/letter for bond_exemption
	 * otherwise true
	 */
	public void checkandResetBondExempted(){
				
		if(ctBB.getManagedBean().getContractVer().getCtContractAmount().isBondReq() && ctBB.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()){
			if(ctBB.getManagedBean().getContractVer().getCtContractAmount().getBondExemptedFileName() == null && ctBB.getManagedBean().getContractVer().getCtContractAmount().getBondExemptedFilePath() == null){
				ctBB.getManagedBean().getCtContractAmount().setBondExempted(false);
			  }
		   }
	//End - Fix #29368

	}
	// End Fix - #30856
	
	// Start - #30866
	/**
	 * method to display audit instruction
	 * 
	 * @return true if user is from audit menu (directory)
	 */
	public boolean isAuditCheck() {
		boolean auditCheck = false;
		if (getManagedBean().isContinuityCircularAuditSearch()) {
			auditCheck = true;
		}
		return auditCheck;
	}

	/**
	 * method to determine specific user roles for audit(supplier)
	 * 
	 * Only allow user with supplier_role and the request is only from the audit
	 * menu
	 * 
	 * @return true if user has either one of the describe roles
	 */
	public boolean isAuditCheckForSupplier() {
		boolean auditSupplierCheck = false;
		if (isSupplierRole()) {
			auditSupplierCheck = getManagedBean().isContinuityCircularAuditSearch();
		}
		return auditSupplierCheck;
	}

	/**
	 * method to determine specific user roles for audit
	 * 
	 * Only allow user with specific designated role and the request is only
	 * from audit menu
	 * 
	 * @return true if user has either one of the describe roles
	 */
	public boolean isAuditContinuityRoleCheck() {
		return (getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.SYS_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value()));
	}

	/**
	 * method to determine specific user roles that has to disable for audit
	 * 
	 * @return true if user has either one of described roles
	 */
	public boolean isAuditRoleForChairpersonAndMember() {
		return (getFacesScopes().hasRoleUser(RoleCode.QCA_MEMBER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCB_MEMBER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBA_MEMBER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBB_MEMBER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PPB_MEMBER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCA_CHAIRPERSON.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCB_CHAIRPERSON.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBA_CHAIRPERSON.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBB_CHAIRPERSON.value()) || getFacesScopes().hasRoleUser(
				RoleCode.PPB_CHAIRPERSON.value()));
	}

	/**
	 * method to hide FL subTable, and to check its roles combination
	 * 
	 * @return true to hide the table
	 */
	public boolean isToHideFlSubTable() {
		boolean hide = false;
		if (!isAuditContinuityRoleCheck() && isAuditRoleForChairpersonAndMember()) {
			hide = true;
		}
		return hide;
	}

	/**
	 * Get Status description by status id.
	 * 
	 * @param statusId
	 *            the status id.
	 * @return status description
	 */
	public String getStatusDescById(Long statusId) {
		String statusDesc = null;

		if (statusId != null) {
			// Get the status description.
			PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(statusId);

			if (pmStatusDesc != null) {
				statusDesc = pmStatusDesc.getStatusName();
			}
		}

		return statusDesc;
	}

	/**
	 * contract search from audit maintenance screen.
	 * 
	 * @param contractNO
	 *            , VCtContract
	 * 
	 * @return navigate to audit view page
	 */
	public String searchContractCtFlForAudit(String contractNO, VCtContract contractData) {
		getManagedBean().setSearchContractNo(contractNO);
		getManagedBean().setFromResultViewAudit(true);
		getManagedBean().setContractDetailsAuditList(contractData);
		getManagedBean().getContractDataModelForCtAuditViewFl().reloadData();
		return CONTRACT_VIEW_AUDIT;
	}
	// End - #30866
	
	// #24333,#36491 - Start
	/**
	 * Processes and classifies performance bonds based on the document type and amendment status.
	 *
	 * @param contractAmt The contract amount containing the performance bonds.
	 * @param docType     The type of the document.
	 * @param isAmendment A flag indicating if the document is an amendment.
	 */
	private void processPerformanceBonds(CtContractAmount contractAmt, String docType, boolean isAmendment) {
	    ctBB.getManagedBean().setTotalBondAmt(contractAmt.getTotalBondAmt());
	    boolean isContractExtend = ctBB.isContractExtendOrIsBond();

	    // Guard Clause: Exit early if bond processing is not required.
	    if (shouldSkipBondProcessing(docType, isAmendment)) {
	        contractAmt.setCtPerformanceBondsOriginal(contractAmt.getCtPerformanceBonds());
	        return;
	    }

	    // store to partition bonds into 'new' and 'original'.
	    List<CtPerformanceBond> bondNew = new ArrayList<CtPerformanceBond>();
	    List<CtPerformanceBond> bondOriginal = new ArrayList<CtPerformanceBond>();

	    for (CtPerformanceBond bond : contractAmt.getCtPerformanceBonds()) {
	        CtPerformanceBond clonedBond = cloneBond(bond, contractAmt, bond.isBrandNew(), isContractExtend);
	        if (clonedBond.isBondNew()) {
	            bondNew.add(clonedBond);
	        } else {
	            bondOriginal.add(clonedBond);
	        }
	    }

	    // If no new bonds exist and conditions are met, create new bonds from the original ones.
	    if (bondNew.isEmpty() && shouldCreateNewBond()) {
	        CtContractValue contractValue = ctBB.getManagedBean().getContractVer().getCtContractValue();
	        for (CtPerformanceBond bond : bondOriginal) {
	            bondNew.add(createNewBondFrom(bond, contractValue));
	        }
	    }

	    // Sorting both lists by bond name (case-insensitive)
	    Comparator<CtPerformanceBond> byRefNo = new Comparator<CtPerformanceBond>() {
	        @Override
	        public int compare(CtPerformanceBond b1, CtPerformanceBond b2) {
	            // Ensure getRefNo() is not null to avoid NullPointerException
	            String refNo1 = b1.getRefNo() == null ? "" : b1.getRefNo();
	            String refNo2 = b2.getRefNo() == null ? "" : b2.getRefNo();
	            return refNo1.compareToIgnoreCase(refNo2);
	        }
	    };
	    
	    // Use Collections.sort()
	    Collections.sort(bondNew, byRefNo);
	    Collections.sort(bondOriginal, byRefNo);

	    contractAmt.setCtPerformanceBondsNew(bondNew);
	    contractAmt.setCtPerformanceBondsOriginal(bondOriginal);
	}
	
	/**
	 * Synchronizes (updates or inserts) a list of bonds to a target contract amount.
	 *
	 * @param targetContractAmount The destination contract amount.
	 * @param sourceBonds          The list of source bonds from the UI.
	 * @param isEffectiveContract  A flag indicating if the contract is currently effective.
	 */
	private void syncBondsToTarget(CtContractAmount target, List<CtPerformanceBond> sourceBonds, boolean isEffectiveContract) {
	    if (target == null || sourceBonds == null) return;

	    List<CtPerformanceBond> targetBonds = target.getCtPerformanceBonds();

	    // Buat map untuk bond yang sudah ada berdasarkan ID (hanya untuk effective contract)
	    Map<Long, CtPerformanceBond> existingByIdMap = new HashMap<>();
	    if (isEffectiveContract && !target.isBrandNew()) {
	        for (CtPerformanceBond bond : targetBonds) {
	            if (bond.getPerformanceBondId() != null) {
	                existingByIdMap.put(bond.getPerformanceBondId(), bond);
	            }
	        }
	    }

	    // Clear target bonds only after creating the map to preserve existing IDs
	    targetBonds.clear();

	    for (CtPerformanceBond src : sourceBonds) {
	        if (src == null) continue;

	        CtPerformanceBond bondToSave;

	        if (target.isBrandNew() || !isEffectiveContract) {
	            // Untuk brand new atau extended contract, selalu buat bond baru
	            bondToSave = copyBondData(new CtPerformanceBond(), src, true, target);
	        } else {
	            // Untuk effective contract, update bond yang sudah ada berdasarkan ID
	            CtPerformanceBond existing = existingByIdMap.get(src.getPerformanceBondId());
	            if (existing != null) {
	                // Update bond yang sudah ada tanpa mengubah ID
	                bondToSave = copyBondData(existing, src, false, null);
	            } else {
	                // Jika ID tidak ditemukan (bond baru ditambahkan), buat baru
	                bondToSave = copyBondData(new CtPerformanceBond(), src, true, target);
	            }
	        }

	        targetBonds.add(bondToSave);
	    }
	}

	// #24333,#36491 - End
	
	// #24333 - Start
	/**
	 * Validates a list of performance bonds based on required fields and
	 * contract rules.
	 * 
	 * @param bonds
	 *            the performance bonds to validate
	 * @param action
	 *            the user action triggering validation (e.g. submit, approve)
	 * @throws EpException
	 *             if any validation check fails
	 */
	private void validateBonds(List<CtPerformanceBond> bonds, String action) throws EpException {
		log.info("validateBonds method test : Start ");
		if (bonds == null || bonds.isEmpty())
			return;

		double bondAmount = 0.0;
		String contractType = ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode();
		boolean isCentral = ParameterConst.CONTRACT_TYPE_CENTRAL.equalsIgnoreCase(contractType);

		for (CtPerformanceBond bond : bonds) {
			bondAmount += bond.getBondAmount();

			if (bond.getRefNo() == null || bond.getRefNo().isEmpty()) {
				setTabValue("PB");
				throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pb.ref.no");
			}

			if (bond.getFinancialOrg() == null || bond.getFinancialOrg().getFinancialOrgId() == null) {
				setTabValue("PB");
				throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pb.financial.org");
			}

			if (isCentral && bond.getBondAmount() == 0) {
				setTabValue("PB");
				throwExceptions(MessageConst.ERROR_100033, "lbl.ct.pb.amount");
			}

			if (bond.getFileName() == null || bond.getFileName().isEmpty()) {
				setTabValue("PB");
				throwExceptions(MessageConst.ERROR_100022, "lbl.ct.pb.ref.letter");
			}
		}

		if ((BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action) || BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action))
				&& ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt() != null) {

			DecimalFormat df = new DecimalFormat("#.00");

			bondAmount = Double.valueOf(df.format(bondAmount));
			double originalBondAmt = Double.valueOf(df.format(ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt()
					.doubleValue()));

			if (Double.compare(bondAmount, originalBondAmt) < 0) {
				setTabValue("PB");
				throw new EpException(MessageConst.ERROR_750016);
			}
		}
		log.info("validateBonds method test : End ");
	}
	
	/**
	 * Blocks submission by setting the given tab and throwing 
	 * a missing performance bond error.
	 *
	 * @param tab the tab identifier to activate (e.g., "PB")
	 */
	private void blockSubmission(String tab) {
	    setTabValue(tab);
	    throwExceptions(MessageConst.ERROR_100022, "lbl.ct.menu.performance.bond");
	}
	
	/**
	 * Creates a copy of a performance bond and assigns it to a new contract amount.
	 * Includes file attachment info (filename and path).
	 * 
	 * @param target
	 *            The target bond to copy data into.
	 * @param source
	 *            The source bond to copy from.
	 * @param forInsert
	 *            Flag to indicate if this is a new bond (needs new ID).
	 * @param parent
	 *            The new contract amount that will own the bond.
	 * @return The updated bond with copied data.
	 */
	private CtPerformanceBond copyBondData(CtPerformanceBond target, CtPerformanceBond source, boolean forInsert, CtContractAmount parent) {
	    if (target == null || source == null) return target;

	    if (forInsert) {
	        target.setPerformanceBondId(null); // ID akan digenerate oleh sistem
	        if (parent != null) {
	            target.setCtContractAmount(parent);
	        }
	    }

	    // Copy all mutable fields
	    target.setBondAmount(source.getBondAmount()); // Copy bond amount
	    target.setRefNo(source.getRefNo()); // Copy reference number
	    target.setFileName(source.getFileName()); // Copy file name
	    target.setFilePath(source.getFilePath()); // Copy file path
	    target.setEffDate(source.getEffDate()); // Copy effective date
	    target.setExpDate(source.getExpDate()); // Copy expiration date
	    target.setFinancialOrg(source.getFinancialOrg()); // Copy financial organization
	    target.setFinancialOrgName(source.getFinancialOrgName()); // Copy financial organization Name
	    target.setBondNew(source.isBondNew()); // Copy bond new status
	    target.setPrevId(source.getPrevId());
	    return target;
	}
	
	/**
	 * Creates a copy of a performance bond and assigns it to a new contract amount.
	 * Includes file attachment info (filename and path).
	 * 
	 * @param src The bond to copy.
	 * @param parent The new contract amount that will own the bond.
	 * @return A new bond with the same data and new parent.
	 */
	private CtPerformanceBond cloneBond(CtPerformanceBond src, CtContractAmount parent, boolean isBrandNew, boolean isContractExtend) {
	    CtPerformanceBond clone = new CtPerformanceBond();
	    if (src != null) {
	    	// Set performanceBondId based on conditions
	        if (isBrandNew) {
	            clone.setPerformanceBondId(null); // Generate new ID only for new or extend without ID
	        } else {
	            clone.setPerformanceBondId(src.getPerformanceBondId()); // Retain existing ID
	        }
	        clone.setBondAmount(src.getBondAmount()); // Copy bond amount
	        clone.setRefNo(src.getRefNo()); // Copy reference number
	        clone.setFileName(src.getFileName()); // Copy file name
	        clone.setFilePath(src.getFilePath()); // Copy file path
	        clone.setEffDate(src.getEffDate()); // Copy effective date
	        clone.setExpDate(src.getExpDate()); // Copy expiration date
	        PmFinancialOrg newOrg = new PmFinancialOrg();
	        if (src.getFinancialOrg() != null) {
	            newOrg.setFinancialOrgId(src.getFinancialOrg().getFinancialOrgId());
	        }
	        clone.setFinancialOrg(newOrg); // Copy financial organization
	        clone.setFinancialOrgName(src.getFinancialOrgName()); // Copy financial organization Name
	        clone.setBondNew(src.isBondNew()); // Copy bond new status
	        clone.setPrevId(src.getPrevId()); // Copy prevId bond
	        clone.setCtContractAmount(parent); // Set the new parent contract amount
	    }
	    return clone;
	}

	/**
	 * Creates a new bond from an existing one (e.g., for contract extensions).
	 *
	 * @param existingBond  The old bond to be used as a template.
	 * @param contractValue The contract information for effective and expiration dates.
	 * @return A new bond object.
	 */
	private CtPerformanceBond createNewBondFrom(CtPerformanceBond existingBond, CtContractValue contractValue) {
	    if (existingBond == null || existingBond == null) {
	        return existingBond;
	    }
	    
	    CtPerformanceBond newBond = new CtPerformanceBond();
	    newBond.setPerformanceBondId(null); // Always a new ID.
	    newBond.setBondNew(true);
	    newBond.setBondAmount(existingBond.getBondAmount()); // Copy bond amount
	    newBond.setRefNo(existingBond.getRefNo()); // Copy reference number
		PmFinancialOrg newOrg = new PmFinancialOrg();
	    if (existingBond.getFinancialOrg() != null) {
	        newOrg.setFinancialOrgId(existingBond.getFinancialOrg().getFinancialOrgId());
	    }
	    newBond.setFinancialOrg(newOrg); // Copy financial organization
	    newBond.setFinancialOrgName(existingBond.getFinancialOrgName()); // Copy financial organization Name
	    newBond.setEffDate(contractValue.getEffDate());
	    newBond.setExpDate(getExtendedExpDate(contractValue));
	    newBond.setFileName(null);
	    newBond.setFilePath(null);
	    newBond.setPrevId(existingBond.getPrevId());
	    return newBond;
	}

	/**
	 * Helper to check if the bond classification process should be skipped.
	 *
	 * @param docType     The type of the document.
	 * @param isAmendment The amendment status.
	 * @return True if the process should be skipped.
	 */
	private boolean shouldSkipBondProcessing(String docType, boolean isAmendment) {
	    return !isAmendment || (!docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
	           && !docType.equalsIgnoreCase(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT)
	           && !docType.equalsIgnoreCase(ParameterConst.DOCTYPE_NOTIFICATION_SETTING));
	}

	/**
	 * Helper to check for conditions that require automatic creation of a new bond.
	 *
	 * @return True if a new bond should be created.
	 */
	private boolean shouldCreateNewBond() {
	    return (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().isBondReq() || ctBB.getManagedBean().getCtContractAmount().isBondReq())
	           && ctBB.getManagedBean().isDisableOriginalBond();
	}

	/**
	 * Calculates an extended expiration date by adding one year to the contract's expiration date.
	 *
	 * @param contractValue The contract value information.
	 * @return The new, extended date.
	 */
	private Date getExtendedExpDate(CtContractValue contractValue) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(contractValue.getExpDate());
	    cal.add(Calendar.YEAR, 1);
	    return cal.getTime();
	}
	// #24333 - End
	
	// #30014 - Start
	/**
	 * Sends contract data to SM module for Company Experience if conditions are
	 * met (specific doc types and APPROVE action).
	 * 
	 * @param resDTO
	 *            the contract response data
	 * @param action
	 *            the BPM action process
	 * @param contractSO
	 *            the contract service object
	 */
	private void sendToSmModuleForCompanyExp(ContractResDTO resDTO, String action, IContractSO contractSO) {
		// Validate input
		if (resDTO.getContract() == null || resDTO.getCtContractVer() == null) {
			return;
		}

		// Get docType safely
		String docType = null;
		if (ctBB.getManagedBean() != null && ctBB.getManagedBean().getContractVer() != null) {
			docType = ctBB.getManagedBean().getContractVer().getDocType();
		}

		// Check conditions action process
		if (!BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
			return;
		}

		if (!isValidDocTypeForCallSp(docType)) {
			return;
		}

		// All conditions met send to SP
		contractSO.insertCtToSmModule(resDTO.getContract().getContractId(), resDTO.getCtContractVer().getContractVerId());
	}

	/**
	 * Checks if the given docType is eligible for process to call SP.
	 */
	private boolean isValidDocTypeForCallSp(String docType) {
		HashSet<String> validDocTypes = new HashSet<>();
		validDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
		validDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		validDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_OWNER_MANAGEMENT);
		return validDocTypes.contains(docType);
	}
	// #30014 - End
}
