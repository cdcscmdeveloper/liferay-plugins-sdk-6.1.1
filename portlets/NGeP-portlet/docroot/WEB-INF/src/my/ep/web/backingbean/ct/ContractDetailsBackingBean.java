/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractZoneSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.dtl.ContractZoneReqDTO;
import my.ep.dto.ct.dtl.ContractZoneResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

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
	
	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml?faces-redirect=true";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml?faces-redirect=true";
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String CONTRACT_COMMON_PAGE = "/xhtml/ct/viewcontract/fulfilmentTab.xhtml?faces-redirect=true";
	private String editorValue;
	private BigDecimal paidAdvancePercentage = new BigDecimal(0);
	private BigDecimal balanceToPaid;
	private BigDecimal paidPercentage;	
	private ContractBackingBean ctBB = null;
	private CtSchedule[] selectedSchedule;
	private CtProgressivePayment[] selectedProgressive;
	private CtDeductionClause[] selectedDeductionClauses;
	private PmOrgProfile[] selectedPmOrgProfile;
	private IPagingDataModel<PmOrgProfile> agencyDataModel = new AgencyPagingDataModel();	
	SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
	
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public ContractDetailsBackingBean() {
		/* Initialization of button */
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			this.getManagedBean().setContractVer(new CtContractVer());
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	 }
	
	/**
     * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	/**
	 * The method used to retrieve available approver(s).
	 *
	 * @return sourceList  the approver list
	 */
	private List<String> getAvailableApproverList() {
		IUserSO userSO = null;
		List<String> sourceList = new ArrayList<String>();
		
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if (resDTO.getUserOrgList().size() > 0) {
				reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			}
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			} else { // Central Contract	
				reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
			}			
			resDTO = userSO.retrieveUserListByRole(reqDTO);
			
			List<PmUser> list = resDTO.getUserList();			
			for (PmUser user : list) {
				sourceList.add(user.getUserName());
				getManagedBean().getUserMap().put(user.getUserName(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}
	
	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
	/**
	 * @return the paidAdvancePercentage
	 */
	public BigDecimal getPaidAdvancePercentage() {
		if (getManagedBean().getPaidAdvancePaymentAmt().intValue() != 0) {
		    paidAdvancePercentage = (getManagedBean().getPaidAdvancePaymentAmt().multiply(
		    		BigDecimal.valueOf(100)).divide(
		    				BigDecimal.valueOf(getManagedBean().getAdvancePaymentAmt()), 2, RoundingMode.HALF_UP));
		}
		return paidAdvancePercentage;
	}

	/**
	 * @param paidAdvancePercentage the paidAdvancePercentage to set
	 */
	public void setPaidAdvancePercentage(BigDecimal paidAdvancePercentage) {
		this.paidAdvancePercentage = paidAdvancePercentage;
	}

	/**
	 * @return the balanceToPaid
	 */
	public BigDecimal getBalanceToPaid() {
		balanceToPaid = (BigDecimal.valueOf(this.getManagedBean().getAdvancePaymentAmt()).subtract(
		this.getManagedBean().getPaidAdvancePaymentAmt()));
		return balanceToPaid;
	}

	/**
	 * @param balanceToPaid the balanceToPaid to set
	 */
	public void setBalanceToPaid(BigDecimal balanceToPaid) {
		this.balanceToPaid = balanceToPaid;
	}

	/**
	 * @return the paidPercentage
	 */
	public BigDecimal getPaidPercentage() {
			paidPercentage = (this.getManagedBean().getPaidInvoiceAmt().multiply(
				BigDecimal.valueOf(100)).divide(
						this.getManagedBean().getContractVer().getCtContractAmount().getContractAmount(), 2, RoundingMode.HALF_UP));
		return paidPercentage;
	}

	/**
	 * @param paidPercentage the paidPercentage to set
	 */
	public void setPaidPercentage(BigDecimal paidPercentage) {
		this.paidPercentage = paidPercentage;
	}
	
	/**
	 * @return the selectedSchedule
	 */
	public CtSchedule[] getSelectedSchedule() {
		return selectedSchedule;
	}

	/**
	 * @param selectedSchedule the selectedSchedule to set
	 */
	public void setSelectedSchedule(CtSchedule[] selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}

	/**
	 * @return the selectedProgressive
	 */
	public CtProgressivePayment[] getSelectedProgressive() {
		return selectedProgressive;
	}

	/**
	 * @param selectedProgressive the selectedProgressive to set
	 */
	public void setSelectedProgressive(CtProgressivePayment[] selectedProgressive) {
		this.selectedProgressive = selectedProgressive;
	}

	/**
	 * @return the selectedDeductionClauses
	 */
	public CtDeductionClause[] getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * @param selectedDeductionClauses the selectedDeductionClauses to set
	 */
	public void setSelectedDeductionClauses(CtDeductionClause[] selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}	

	/**
	 * @return the selectedPmOrgProfile
	 */
	public PmOrgProfile[] getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	/**
	 * @param selectedPmOrgProfile the selectedPmOrgProfile to set
	 */
	public void setSelectedPmOrgProfile(PmOrgProfile[] selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}

	/**
	 * @return the agencyDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getAgencyDataModel() {
		return agencyDataModel;
	}

	/**
	 * @param agencyDataModel the agencyDataModel to set
	 */
	public void setAgencytDataModel(IPagingDataModel<PmOrgProfile> agencyDataModel) {
		this.agencyDataModel = agencyDataModel;
	}
	
	/**
	 * Pagination for available agency search list
	 */
	private class AgencyPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public AgencyPagingDataModel() {
			super("tableAgency", false);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {			
			try {
				// Filter By - parameterType
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
			
				// Filter By - Org Type
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
				
				
				// Filter By - Ministry Profile Id
				Long ministryProfileId = getManagedBean().getMinistryProfileId();
				if (ministryProfileId == null) {
					ministryProfileId = 0L;
				}
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryProfileId));
				
				
				if (getManagedBean().getJabatanProfileId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getJabatanProfileId()));
				}
				
				Date currentDate = DateUtils.getCurrentDateWithoutTime();
				// Filter By - Org effDate
				searchBean.getDataFilterBeans()
						.add(new SearchFilterBean("pmOrgValidity.effDate",
								SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

				// Filter By - Org expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"pmOrgValidity.expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
								"pmOrgValidity.expDate",
								SearchOperator.OPERATOR_IS_NULL.value(), null,
								SearchCondition.CONDITION_OR.value())));
				
				if (getManagedBean().getPtjCodeName() != null
						&& (getManagedBean().getPtjCodeNameSearchValues() != null && !getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))) {
					if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					
					} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileSearchBean(searchBean);
				OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
				if (resDTO.isSuccessful()) {
					 return resDTO.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
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
		try { 
			if ((getManagedBean().getCtNumber() == null || getManagedBean().getCtNumber().trim().length() == 0)
					&& (getManagedBean().getCtName() == null || getManagedBean().getCtName().trim().length() == 0) 
					&& (getManagedBean().getCtCircularNo() == null || getManagedBean().getCtCircularNo().trim().length() == 0) 
					&& (getManagedBean().getCtCircularName() == null || getManagedBean().getCtCircularName().trim().length() == 0) 
					&& (getManagedBean().getCtSupplierId() == null || getManagedBean().getCtSupplierId().trim().length() == 0) 
					&& (getManagedBean().getCtSupplierName() == null || getManagedBean().getCtSupplierName().trim().length() == 0) 
					&& getManagedBean().getCtDateStart() == null
					&& getManagedBean().getCtDateEnd() == null 
					&& (getManagedBean().getCtPhysicalNo() == null || getManagedBean().getCtPhysicalNo().trim().length() == 0) 
					&& (getManagedBean().getCtLOANo() == null || getManagedBean().getCtLOANo().trim().length() == 0) 
					&& getManagedBean().getCtStatus() == 0 
					&& getManagedBean().getTerminationTypeId() == null 
					&& getManagedBean().getCtContractStatus() == null) {
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null,
								MessageUtils.getMessage(MessageConst.WARN_755000,
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			} else {
				getManagedBean().getContractDataModel().reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * The method used to reset the search criteria for contract.
	 */
	public void searchReset() {
		this.getManagedBean().setCtNumber(null);
		this.getManagedBean().setCtName(null);	
		this.getManagedBean().setCtCircularNo(null);	
		this.getManagedBean().setCtCircularName(null);	
		this.getManagedBean().setCtSupplierId(null);	
		this.getManagedBean().setCtSupplierName(null);	
		this.getManagedBean().setCtDateStart(null);	
		this.getManagedBean().setCtDateEnd(null);	
		this.getManagedBean().setCtPhysicalNo(null);	
		this.getManagedBean().setCtLOANo(null);
		this.getManagedBean().setCtStatus(0);
		this.getManagedBean().setTerminationTypeId(null);
		this.getManagedBean().setCtContractStatus(null);
	}
	
	/**
	 * The method used for searching the agency.
	 */
	public void searchAgencyDetails() {
		try {
			agencyDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetAgencyDetails() {
		getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
				getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		agencyDataModel.resetData();
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract search screen.
	 */
	public void preRenderSearch() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_MAINT_PAGE);
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract details screen.
	 */
	public void preRenderContract() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
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
			
			if (taskId == null) {
				if (getManagedBean().getWarningPercentage() != null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, 
									MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[] {getManagedBean().getCtContract().getContractNo(),
												getManagedBean().getWarningPercentage()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					getManagedBean().setWarningPercentage(null);
				}
				return;
			}			
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);

			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO); 
			Object obj = res.getPojo();
      
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setAction(outcome);
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setSelectedTabIndex(0);

			ContractReqDTO ctReqDTO = new ContractReqDTO();
			if ((res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME) 
					|| res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME)) 
					&& res.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)) {
				CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				ctReqDTO.setDocType(dataType.getDocType());
			} else {
				CTContractDataType dataType = (CTContractDataType) obj;
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				ctReqDTO.setDocType(dataType.getDocType());
			}
		    
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
			setValidateContractField(resDTO);         
			    
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, 
					ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			
		}
	}
	
	/**
	 * The method used to set the contract details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	public void setValidateContractField(ContractResDTO resDTO) {
		getManagedBean().setContractVer(resDTO.getCtContractVer());
	    getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	         
	    // Retrieve Agencies
	    if (getManagedBean().getContractVer().getCtAgencies() != null 
	    		&& this.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
	    	getManagedBean().setSelectedAgency(null);
	    	//Collections.sort(this.getManagedBean().getContractVer().getCtAgencies(), new CtAgency());		           
	    	sortAgencyList(this.getManagedBean().getContractVer().getCtAgencies());
	    	for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
	    		checkAgencyStatus(ctAgency);		        	   
	    	}
	    }
		// Retrieve Items
	    if (getManagedBean().getContractVer().getCtContractItems().size() > 0) {	        	   
	    	getManagedBean().setCtItemList(new ArrayList<CtContractItem>());
	    	getManagedBean().setCtItemServiceList(new ArrayList<CtContractItem>());
	    	getManagedBean().setSelectedItem(null);
	    	getManagedBean().setSelectedServiceItem(null);	    	  
 		         
	    	for (CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
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
	    			Collections.sort(item.getCtItemPrices(), new CtItemPrice());
	    			getManagedBean().getCtItemServiceList().add(item);			           
	    		} else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
	    			Collections.sort(item.getCtItemPrices(), new CtItemPrice());
	    			if (item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0) {
	    				//Collections.sort(item.getCtItemCeilingQties(), new CtItemCeilingQty());
	    				sortItemCeilingQtyList(item.getCtItemCeilingQties());
	    				for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {			        	        	    	   
	    					checkAgencyStatus(ceilingQty.getCtAgency());		        	   
	    				}
	    			}
	    			this.getManagedBean().getCtItemList().add(item);
	    		}
	    	}
	    	
	    	if (this.getManagedBean().getCtItemList() != null && this.getManagedBean().getCtItemList().size() > 0) {
	    		Collections.sort(this.getManagedBean().getCtItemList(), new CtContractItem());
	    	}
	    	if (this.getManagedBean().getCtItemServiceList() != null && this.getManagedBean().getCtItemServiceList().size() > 0) {
	    		Collections.sort(this.getManagedBean().getCtItemServiceList(), new CtContractItem());
	    	}
	    }
		// Retrieve Deduction Clauses
	    if (this.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
	    	for (CtDeduction deduct : this.getManagedBean().getContractVer().getCtDeductions()) {
	    		if (deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)) {
	    			getManagedBean().setFlgPaymentTracking(true);
	    			getManagedBean().setAdvancePaymentAmt(deduct.getAmount());
	    			calAdvancePaymentTracking(this.getManagedBean().getCtContract().getContractId());
	    		}
	    	}
	    }
	    // Retrieve Schedules
	    if (getManagedBean().getContractVer().getCtSchedules() != null 
	    		&& !getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
	    	getManagedBean().setChkSchedule(true);
	    	Collections.sort(getManagedBean().getContractVer().getCtSchedules(), new CtSchedule());
	    	for (CtSchedule ctSchedule : getManagedBean().getContractVer().getCtSchedules()) {
	    		if (ctSchedule.isUseInFulfilment()) {
	    			getManagedBean().setDisableScheduleRadio(true);
	    			break;
	    		}
	    	}
	    	scheduleDate();
	    }
	    if (getManagedBean().getContractVer().getCtProgressivePayments() != null 
	    		&& !getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
	    	Collections.sort(getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment());
	    	for (CtProgressivePayment ctProgressivePayment : getManagedBean().getContractVer().getCtProgressivePayments()) {
	    		if (ctProgressivePayment.isUseInFulfilment()) {
	    			getManagedBean().setDisableScheduleRadio(true);
	    			break;
	    		}
			}
	    	progressiveDate();
	    }
	    // Retrieve Delivery Terms
	    if (getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null) {
	    	getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
	    	PmParameter pmParameter = getParameterByTypeAndCode(
	    			ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS);
	    	getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(pmParameter.getParameterId());
	    }
	    if (getManagedBean().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
	    		ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			getManagedBean().setDirectPurchase(true);
			getManagedBean().setFlgPaymentTracking(false);
		} else {
			this.getManagedBean().setDirectPurchase(false);
		}
	    // Retrieve Zones
	    if (getManagedBean().getContractVer().getCtContractZones() != null 
	    		&& this.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
	    	Collections.sort(getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
	    	retriveZonesByContractVer();
	    }
	    // Retrieve Supplier Information
	    getManagedBean().setSmAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
	    getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
		renderUpdateCheckbox(getManagedBean().getContractVer());
		getManagedBean().setSupplierCompanyName(this.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		
		getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
		} 
	 	getManagedBean().setRemarkList(resDTO.getCtRemarks());
		getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
		if (getManagedBean().getCtContract().isFrequencySchedule()) {
			getManagedBean().setFrequencySchedule("S");
		} else {
			getManagedBean().setFrequencySchedule("P");
		}
		if (getManagedBean().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			getManagedBean().setDisableScheduleRadio(true);
			getManagedBean().setFrequencySchedule("S");
		} else if (getManagedBean().getCtContract().getLoa().getScQtSchedules()!= null 
				&& !getManagedBean().getCtContract().getLoa().getScQtSchedules().isEmpty()) {
			getManagedBean().setDisableScheduleRadio(true);
		    getManagedBean().setFrequencySchedule("S");
		}  else if (getManagedBean().getCtContract().getLoa().getScQtProgPayments() != null 
				&& !getManagedBean().getCtContract().getLoa().getScQtProgPayments().isEmpty()) {
			getManagedBean().setDisableScheduleRadio(true);
			getManagedBean().setFrequencySchedule("P");
		}
		
		// Retrieve contract amount
		// Default to current date when view/update contract
		if (getManagedBean().getContractVer().getAsAtDate() == null 
				|| getManagedBean().isViewContract()
				|| getManagedBean().isUpdateContract()) {
			getManagedBean().getContractVer().setAsAtDate(DateUtils.getCurrentDateWithoutTime());
		}
		// Default to contract expire date when contract is expired
		if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() < DateUtils.getCurrentDateWithoutTime().getTime()) {
			getManagedBean().getContractVer().setAsAtDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		}
		updateAsAtDate();
		
		// Get performance bond rule amount
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleTypeConst.BOND);
		RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
		if (roleResDTO.isSuccessful() && roleResDTO.getRuleValueList() != null 
				&& roleResDTO.getRuleValueList().size() > 0) {
			getManagedBean().setPerformanceBondRuleAmt(Double.parseDouble(roleResDTO.getRuleValueList().get(0).getRuleValue()));
		}
		initializeRemarks();
		getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
				ModuleCodeConst.CONTRACT_MANAGEMENT, getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.getManagedBean().setViewContract(getManagedBean().isViewContract());
		ctBB.initializeRemarkAndAttachement(resDTO.getCtContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	}
	
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
    public String saveContract(String action) throws Exception {
    	IContractSO contractSO = null;
    	
    	try {
    		getZoneObjDetails();
    		validateCtContractVer(action);
    		ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			   
			if (getManagedBean().getContractVer().getCtContractItems().size() > 0) {
				getManagedBean().getContractVer().getCtContractItems().clear();
			}
			getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtItemList());
         	getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtItemServiceList());
              
         	if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
         		scheduleProgressiveDate();
         	}
         	reqDTO.setCtContract(getManagedBean().getContractVer().getCtContract());
         	CtContractVer ctContractVer = getManagedBean().getContractVer();
         	
         	if (ctContractVer.getCtContractValue().isCompanyNameChange()) {
         		ctContractVer.getCtContractValue().setSupplierName(getManagedBean().getSupplierCompanyName());
         	}
         	if (ctContractVer.getCtContractValue().isAddressChange()) {
         		ctContractVer.getCtContractValue().setSupplierAddress(getManagedBean().getSmAddress());
         	}         	
            reqDTO.setCtContractVer(ctContractVer);
            reqDTO.setAgreementContractVer(getManagedBean().getAgreementContractVer());
            reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
	        reqDTO.setAttachments(getManagedBean().getAttachmentList());
	        reqDTO.setUserMap(getManagedBean().getUserMap());
	        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		    reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			
		    if (this.getManagedBean().getPmDigiSign() != null) {
		    	reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
		    }
		    contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
		    ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
	
		    if (resDTO.isSuccessful()) {
		    	log.info("Record saved successfully");
		    	String messageId = MessageConst.INFO_700005;
		    	if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
		    		messageId = MessageConst.INFO_700025;
		    	} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
		    		messageId = MessageConst.INFO_700009;
		    	} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
		    		messageId = MessageConst.INFO_700007;
		    	} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
		    		messageId = MessageConst.INFO_700008;
		    	} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
		    		messageId = MessageConst.INFO_700010;
		    	}
		    	FacesContext.getCurrentInstance().addMessage(null,
		    			new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
		    					new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		    	
		    	if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
		    			|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) { 				  
		    		renewSesionObject();
		    		return CommonUIConst.ACKNOWLEDGE_PAGE;
		    	} else {				    	
				 	ContractReqDTO ctReqDTO = new ContractReqDTO();
				 	ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				 	ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
				    	
					ContractResDTO ctResDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
					setValidateContractField(ctResDTO);
				}
		    }
    	} finally {
    		contractSO = null;
    	}
    	return null;	
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
    	CtContractVer contractVer = getManagedBean().getContractVer();
    	Date today = DateUtils.getCurrentDateWithoutTime();
    	getManagedBean().setAutoUpdate(true);
		// Validation for remark
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
 			String agencyRemark = getManagedBean().getContractVer().getAgencyRemarks().get(0).getRemark();
 			String zoneRemark = getManagedBean().getContractVer().getZoneRemarks().get(0).getRemark();
 			String itemRemark = getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
 			String deductionRemark = getManagedBean().getContractVer().getDeductionRemarks().get(0).getRemark();
 			String scheduleRemark = getManagedBean().getContractVer().getScheduleRemarks().get(0).getRemark();
 			String settingRemark = getManagedBean().getContractVer().getSettingRemarks().get(0).getRemark();
 			if ((remark == null || remark.length() == 0) 
 					&& (agencyRemark == null || agencyRemark.length() == 0) 
 					&& (zoneRemark == null || zoneRemark.length() == 0)
 					&& (itemRemark == null || itemRemark.length() == 0) 
 					&& (deductionRemark == null || deductionRemark.length() == 0)
 					&& (scheduleRemark == null || scheduleRemark.length() == 0) 
 					&& (settingRemark == null || settingRemark.length() == 0)) {
 				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
 			}
 		}
		// Validation for zone
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
	    	getZoneObjDetails();
			if (contractVer.getCtContractZones() != null) {
				for (CtContractZone contractZone : contractVer.getCtContractZones()) {
					if (contractZone.getDeliveryTerm() == 0) {
						throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
					}
				}
			}
		}
		
		if (contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_CREATION) 
				|| contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
			// Validation for agency
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY) 
					&& contractVer.getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
			}
			// Validation for performance bond
			if (getManagedBean().getCtContract().isBondReq() && !getManagedBean().isDirectPurchase()) {
				//In CT Update(bond required): if previously the bond already defined, number of performance bond must be > 0 while submission; 
				//In CT Update(bond required): if previously no bond defined, system allow submission of FD Update even the number of performance bond entered = 0.
				if (contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
					if (getManagedBean().getCtContract().getLatestContractVer() != null) {
						for (CtContractAmount contractAmt : getManagedBean().getCtContract().getLatestContractVer().getCtContractAmounts()) {
				    		if (getManagedBean().getContractVer().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
				    				&& getManagedBean().getContractVer().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()) {
				    			if (contractAmt.getCtPerformanceBonds().size() > 0 
				    					&& (getManagedBean().getCtContractAmount().getCtPerformanceBonds() == null 
				    					|| getManagedBean().getCtContractAmount().getCtPerformanceBonds().isEmpty())) {
				    				throw new EpException(MessageConst.ERROR_100022, 
											new String[]{ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				    					
				    			}
				    		}
				    	} 
					}
				}				
				if (getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null && getManagedBean().getCtContractAmount().getCtPerformanceBonds().size() > 0) {
					double bondAmount = 0.0;
					for (CtPerformanceBond performanceBond : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
						bondAmount = bondAmount + performanceBond.getBondAmount();
						if (performanceBond.getRefNo() == null || performanceBond.getRefNo().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.no",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}						
						if (performanceBond.getFinancialOrg() == null || performanceBond.getFinancialOrg().getFinancialOrgId() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.financial.org", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}						
						if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
							if (performanceBond.getBondAmount() == 0) {
								throw new EpException(MessageConst.ERROR_100033,
										new String[]{ResourceBundleUtils.getMessage("lbl.ct.pb.amount",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}						
						if (performanceBond.getFileName() == null || performanceBond.getFileName().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.letter",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} 
					}				 
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						double bondAmendAmount = 0.0;
						DecimalFormat df = new DecimalFormat("#.00");
						
						// Retrieve amendment list
						if (getManagedBean().getCtContract().getLatestContractVer() != null) {
							IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
							ContractAmendmentResDTO resAmendDto = amendSO.getCtAmendmentByContractId(getManagedBean().getCtContract().getContractId());

							if (resAmendDto.getAmendmentList() != null && !resAmendDto.getAmendmentList().isEmpty()) {
								for (CtAmendment ctAmendment : resAmendDto.getAmendmentList()){
									if (getManagedBean().getContractVer().getAsAtDate().getTime() >= ctAmendment.getAmendmentEffDate().getTime() 
						    				&& getManagedBean().getContractVer().getAsAtDate().getTime() <= ctAmendment.getCtContractVer().getCtContractValue().getExpDate().getTime()){
						    			if (ctAmendment.getAmendmentLoa() != null && ctAmendment.getAmendmentLoa().isBondReq()) {
											bondAmendAmount = bondAmendAmount + ctAmendment.getAmendmentLoa().getBondAmt().doubleValue();
										}				
						    			break;
						    		}						    		
						    	}
							}
						}						
						
						if (contractVer.getCtContract().getLoa().getBondAmt() == null 
							&& getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
									ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							throw new EpException(MessageConst.ERROR_750021);
				
						} else if (contractVer.getCtContract().getLoa().getBondAmt() != null 
							&& getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
									ParameterConst.CONTRACT_TYPE_MINISTRY)) {						
							bondAmount = Double.valueOf(df.format(bondAmount));
							double oriBondAmount = Double.valueOf(df.format(contractVer.getCtContract().getLoa().getBondAmt().doubleValue()));
							bondAmendAmount = Double.valueOf(df.format(bondAmendAmount));
						
							int retval = Double.compare(bondAmount , oriBondAmount);
							if (bondAmendAmount != 0) {
								retval = Double.compare(bondAmount , bondAmendAmount);
							}
							if (retval >0) {
								throw new EpException(MessageConst.ERROR_750015);
							}
							if (retval < 0) {
								throw new EpException(MessageConst.ERROR_750017);  
							}	
						}
					}
				}	
			}
			// Validation for schedule
			if(getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
				if (getManagedBean().getCtContract().isFrequencySchedule()) {
					getManagedBean().getContractVer().setCtProgressivePayments(null);
				} else {
					getManagedBean().getContractVer().setCtSchedules(null);  	
				}
				List<String> schedulePhaseNoList = new ArrayList<String>();
				if (getManagedBean().getCtContract().isFrequencySchedule()) {
					if (contractVer.getCtSchedules() == null || contractVer.getCtSchedules().size() < 2 
							&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
						throw new EpException(MessageConst.ERROR_750022);
			     	}
					for (CtSchedule schedules : contractVer.getCtSchedules()) {
						if (schedules.getStartMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedules.getEndMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        	}
						if (schedules.getScheduleNo() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {
							throw new EpException(MessageConst.ERROR_100046, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(schedules.getScheduleNo().toString());
						}
						if (schedules.getScheduleType() == null || schedules.getScheduleType().getParameterId() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.type", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
					}     	
				} else if (contractVer.getCtProgressivePayments() == null || contractVer.getCtProgressivePayments().size() < 2 
						&& !getManagedBean().getCtContract().isFrequencySchedule()
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
					throw new EpException(MessageConst.ERROR_750022);
				}
				if (contractVer.getCtProgressivePayments() != null && !contractVer.getCtProgressivePayments().isEmpty() 
						&& !getManagedBean().getCtContract().isFrequencySchedule()) {
					float percentage = 0.0f;
					for (CtProgressivePayment progressivePayment : contractVer.getCtProgressivePayments()) {
						percentage = percentage + progressivePayment.getPercentage();
						if (progressivePayment.getPhaseNo()== null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.phase", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {
		           			throw new EpException(MessageConst.ERROR_100046, 
		           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.phase", 
		           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
						}
						if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.description", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getStartMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getEndMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}     	
					}
					int value = Float.compare(percentage,100);
					if (value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
							|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
						throw new EpException(MessageConst.ERROR_100022, 
	           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.percentage.progressive", 
	           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			// Validation for AP178 agency
			if (contractVer.getCtAgencies().size()>0) {
				double ceilingAmt = 0.0;
				for (CtAgency ctAgency : contractVer.getCtAgencies()) {
					ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
					if (ctAgency.isAp178() == true && (ctAgency.getFileName() == null  || ctAgency.getFileName().equalsIgnoreCase(""))) {
						throw new EpException(MessageConst.ERROR_750008); 
						// Approval document is mandatory for AP178 agency. Please upload the document.
					}
					
					if (ctAgency.isAp178() == true && (ctAgency.getRefNo() == null  || ctAgency.getRefNo().equalsIgnoreCase(""))) {
						throw new EpException(MessageConst.ERROR_100022, 
	           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.file.ref.no", 
	           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
				double latestContractAmount = 0.0;
				for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContractAmounts()){
		    		if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
		    				&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
		    			latestContractAmount = contractAmt.getContractAmount().doubleValue();
		    			break;
		    		}
		    	}
				if (ceilingAmt >  latestContractAmount) {
					CommonUtils commonUtils = new CommonUtils();
					DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
							CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
					throw new EpException(MessageConst.ERROR_750007, 
							new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
				}
			}			
			// Validation for ceiling quantity
			if (contractVer.getCtContractItems() != null) {
				for (CtContractItem item : contractVer.getCtContractItems()) {
					double totalCeilingQuantity = 0.0;
					if (item.getCtItemMaxQties().size() != 0) {
						BigDecimal maxCeilingQty = new BigDecimal(0);
						for (CtItemMaxQty ctItemMaxQty : item.getCtItemMaxQties()) {
							if (ctItemMaxQty.getEffDate().getTime()<=today.getTime() && ctItemMaxQty.getExpDate().getTime()>=today.getTime()) {
								maxCeilingQty = ctItemMaxQty.getMaxQuantity();
								break;
							}
						}
						for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
							totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
						}
						if (maxCeilingQty.doubleValue() < totalCeilingQuantity) {
							throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
						}
					}
				}
			}
		}
		
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		if (getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& (getManagedBean().getContractVer().getCtContractValue().getAp201FileName() == null 
				||getManagedBean().getContractVer().getCtContractValue().getAp201FileName().equalsIgnoreCase("")) 
				&& getManagedBean().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			throw new EpException(MessageConst.ERROR_100022, 
   					new String[] {ResourceBundleUtils.getMessage("lbl.ct.attachment.ap201", 
   							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
	}
    
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
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
			validateCtContractVer(outcome);
			CtContractVer contractVer = getManagedBean().getContractVer();
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(contractVer.getCtContract().getContractNo());
			sb.append(contractVer.getDocType());
			sb.append(contractVer.getCtContractValue().getEffDate());
			sb.append(contractVer.getCtContractValue().getExpDate());
			sb.append(getManagedBean().getCtContractAmount().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
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
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
    public void setTabCentralValue(int currentTab, String outputTab) {		
		getManagedBean().setItemIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setSettingIndex(11);		
		
		if (outputTab.equalsIgnoreCase("FD")) {
			getManagedBean().setSelectedTabIndex(0);
		}
		if (!getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("IT")) {
			getManagedBean().setSelectedTabIndex(1);
			getManagedBean().setItemIndex(1);
	   	}
		if (getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("ZO")) {
			getManagedBean().setSelectedTabIndex(1);
     	}
		if (getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("IT")) {
			getManagedBean().setSelectedTabIndex(2);
			getManagedBean().setItemIndex(2);
     	}
		if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate() && outputTab.equalsIgnoreCase("FT")) {
			if (getManagedBean().getCtContract().isZonalPricing()) {
				getManagedBean().setSelectedTabIndex(3);
  	    	} else{
  	    		getManagedBean().setSelectedTabIndex(2);
  	     	}
  	  		getManagedBean().setFactoring( getManagedBean().getSelectedTabIndex());
      	}
		if (outputTab.equalsIgnoreCase("SE")) {
			if (getManagedBean().getCtContract().isZonalPricing()) {  
				if(getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate() 
						&& outputTab.equalsIgnoreCase("FT")) {
					getManagedBean().setSelectedTabIndex(4);
				} else {
					getManagedBean().setSelectedTabIndex(3);
				}
       	  	} else {
       	  		if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate() 
       	  				&& outputTab.equalsIgnoreCase("FT")) {
       	  			getManagedBean().setSelectedTabIndex(3);
       	  		} else {
       	  			getManagedBean().setSelectedTabIndex(2);
       	  		}
       	  	}
			getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
		} 
	}
	
    /**
	 * The method used for controlling the tab menu
	 * of ministry contract dynamically.
	 * 
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
	public void setTabValue(int currentTab, String outputTab) {	 
		getManagedBean().setItemIndex(11);
		getManagedBean().setDeductionIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setScheduleIndex(11);
		getManagedBean().setPaymentTracking(11);
		getManagedBean().setSettingIndex(11);
		
	  	if (outputTab.equalsIgnoreCase("FD")) {
			getManagedBean().setSelectedTabIndex(0);
	    
		}
	  	if(outputTab.equalsIgnoreCase("AG")) {
	  		getManagedBean().setSelectedTabIndex(1);
	  	}
	  	if (!getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("IT")) {
	  		getManagedBean().setSelectedTabIndex(2);
	  		getManagedBean().setItemIndex(2);
	  	}
	  	if (getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("ZO")) {
	  		getManagedBean().setSelectedTabIndex(2);
	  	}
	  	if (getManagedBean().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase("IT")) {
	  		getManagedBean().setSelectedTabIndex(3);
	  		getManagedBean().setItemIndex(3);
		}
	  	if (outputTab.equalsIgnoreCase("DE") && !getManagedBean().isDirectPurchase() 
	  			&& !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  		if (getManagedBean().getCtContract().isZonalPricing()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  		} else {
	  			getManagedBean().setSelectedTabIndex(3);
	      	}
	  		getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
	  	} 
	  	if (outputTab.equalsIgnoreCase("SC")&& getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  		if (getManagedBean().getCtContract().isZonalPricing()) {  
	  			if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(4);
	  			}
	  		} else {
	  			if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(4);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(3);
	  			}
	  		}
	  		getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()
	  			&& outputTab.equalsIgnoreCase("FT")) {
	  		if (getManagedBean().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	        	} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	        			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	        		getManagedBean().setSelectedTabIndex(5);	   
	        	}
	  		} else if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);	   
	  			}
	  		} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(4);
	  		} else {
	  			getManagedBean().setSelectedTabIndex(3);
	       	}
	        getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (getManagedBean().isFlgPaymentTracking() && outputTab.equalsIgnoreCase("PY")) {
	  		if (getManagedBean().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(6);
	  					if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  						getManagedBean().setSelectedTabIndex(7);
	  					}
	  				} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);
    	    	 	if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
    	    	 		getManagedBean().setSelectedTabIndex(6);
    	    	 	}
	  			} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);	   
	  				if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	        	} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	        		getManagedBean().setSelectedTabIndex(5);
	        	}
	  		} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	        } else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
   	     		getManagedBean().setSelectedTabIndex(4);
			} else {
				getManagedBean().setSelectedTabIndex(3);
			}
	  		getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (outputTab.equalsIgnoreCase("SE")) {
	  		if (getManagedBean().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(6);
	  					if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  						getManagedBean().setSelectedTabIndex(7);
	  						if (getManagedBean().isFlgPaymentTracking()) {
	        	    	        	     getManagedBean().setSelectedTabIndex(8);
	  						}
	  					} else if (getManagedBean().isFlgPaymentTracking()) {
	  						getManagedBean().setSelectedTabIndex(7);
	  					}
	  				} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  					if (getManagedBean().isFlgPaymentTracking()) {
	  						getManagedBean().setSelectedTabIndex(7);
	  					}
	  				} else if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  				if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(7);
	  				}
	  			} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (getManagedBean().isFlgPaymentTracking()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (!getManagedBean().isDirectPurchase() && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);	   
	  				if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  					if (getManagedBean().isFlgPaymentTracking()) {
    	    	          	    	    	 getManagedBean().setSelectedTabIndex(7);
	  					}
	  				} else if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if(getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (getManagedBean().isFlgPaymentTracking()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (getManagedBean().isFlgPaymentTracking()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (getManagedBean().isFlgPaymentTracking()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (getManagedBean().isFlgPaymentTracking()) {
     	    	    	 getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (getManagedBean().isFlgPaymentTracking()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  		} else {
	  			getManagedBean().setSelectedTabIndex(3);
	  		}
	  		getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
	  	}
	}
	
	/**
     * The method used to check the accessibility of factoring tab menu.
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
     */
	public boolean checkFactoringExpiryDate() {
    	if (getManagedBean().getCtContract().getCtFactoring() != null 
    			&& getManagedBean().getCtContract().getCtFactoring().getStatus() != null
    			&& getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()) {
    		return true;
    	}
    	return false;
    }
    
	/**
	 * The method used to navigate to contract termination screen
	 * for termination purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoTerminatePage() {
		IContractSO contractSO = null;
		ContractResDTO resDTO = null;
		ContractReqDTO reqDTO = null;
		TerminationBackingBean trBB = null;
		
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			trBB = new TerminationBackingBean();
			trBB.getManagedBean().setViewContract(false);
			trBB.setTerminationContractVer(resDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			contractSO = null;
			resDTO = null;
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
	public String gotoSuspendPage() {
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		SuspensionBackingBean suspBB = null;
		
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		    reqDTO.setContractId(contractId);
		    Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			
			suspBB = new SuspensionBackingBean();
			suspBB.getManagedBean().setViewOnly(false);
			suspBB.setSuspensionContractVer(resDTO);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			contractSO = null;
			resDTO = null;
			reqDTO = null;
			suspBB = null;
		}
		return PROCESS_SUSPEND_PAGE;
	}
	
	/**
	 * The method used to navigate to contract amendment screen
	 * for amendment purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoAmendPage() {
		IContractSO contractSO = null;
		AmendmentBackingBean amendBB = null;
		
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
				
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			if (action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
			}
			ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			amendBB = new AmendmentBackingBean();
			amendBB.getManagedBean().setSelectedTabIndex(0);
			amendBB.getManagedBean().setShowItem(null);
			amendBB.getManagedBean().setSelectedItem(null);
			amendBB.getManagedBean().setSelectedServiceItem(null);
			amendBB.setAmendmentContractVer(resDTO);
		} finally {
			contractSO = null;
			ctBB = null;
		}
		return PROCESS_AMEND_PAGE;
	}
	
	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoViewPage() {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractId(contractId);
		CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		if (latestContractVer != null) {
			reqDTO.setContractVerId(latestContractVer.getContractVerId());
		} else {
			reqDTO.setContractVerId(contractVerId);
		}
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		reqDTO.setViewOnly(true);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
	    getManagedBean().setViewContract(true);
	    setValidateContractField(resDTO);
	    getManagedBean().setSelectedTabIndex(0);
	    reqDTO.setCtContractVer(resDTO.getCtContractVer());
	    String percentage = contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage();
	    getManagedBean().setWarningPercentage(percentage);
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
		CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		reqDTO.setContractVerId(latestContractVer.getContractVerId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		reqDTO.setViewOnly(true);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
	    setValidateContractField(resDTO);
	    getManagedBean().setSelectedTabIndex(0);
	    reqDTO.setCtContractVer(resDTO.getCtContractVer());
	    String percentage = contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage();
	    getManagedBean().setWarningPercentage(percentage);
		return CONTRACT_COMMON_PAGE;
	}
	
	public String gotoCicularPage(Long contractVerId){
		
		   CentralContractBackingBean centralBB = new CentralContractBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			centralBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId());
			centralBB.getManagedBean().setChkCentralCircular(true);
			centralBB.gotoCommonPage(contractVerId);
			centralBB.getManagedBean().setAction(" ");
			return "/xhtml/ct/circular/portal/centralCircularTab.xhtml?faces-redirect=true";
	}
	
	/**
	 * The method used to navigate to contract update screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoUpdatePage() {
		IContractSO contractSO = null;
		
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
				
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			reqDTO.setUpdateAction(true);
			getManagedBean().setUpdateContract(true);
			
		    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
		    setValidateContractField(resDTO);		    
		    getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		    
	        getManagedBean().setViewContract(false);
	        this.getManagedBean().setSelectedTabIndex(0);	     
			return CONTRACT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	/**
	 * The method used to navigate to contract termination history screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoTerminateHistoryPage() {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(contractId);
		Long contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();
    	ctReqDTO.setContractVerId(contractVerId);
	
    	ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
		trBB.getManagedBean().setViewContract(true);
		trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
		trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		trBB.retrieveContractVerOfAgreement();
		
		resDTO = contractSO.retrieveTerminationHistoryList(ctReqDTO);
		trBB.getManagedBean().setTerminationList(resDTO.getTerminationHistoryList());		
		return TERMINATE_HISTORY_PAGE;
	}
    
	/**
	 * The method used to navigate to previous screen.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoBackPage() {
		return getManagedBean().getGotoPreviewPage();
	}
	
	/**
	 * The method used to navigate to previous screen.
	 * This method will navigate to contract maintenance screen
	 * when the previous screen is not set. 
	 * 
	 * @return the navigate URL 
	 */
	public String backAction() {
		getManagedBean().setViewContract(false);
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
	}

    /**
	 * The method used to set status of agency.
	 * The status is displayed according to the effective date.
	 * 
	 * @param  ctAgency  an object of agency
     */
	public void checkAgencyStatus(CtAgency ctAgency) {
		// If effective date is after current date, status is still display old status
 	   	if (ctAgency.getRecordStatusFlg() == 0) {
 	   		if (ctAgency.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
 	   			ctAgency.setChkStatus(true);
 	   		} else {
 	   			ctAgency.setChkStatus(false);
 	   		}
 	   	} else {
 	   		if (ctAgency.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
 	   			ctAgency.setChkStatus(false);
 	   		} else {
 	   			ctAgency.setChkStatus(true);
 	   		}
 	   	}
	}
	
	/**
	 * The method used to retrieve the advance payment tracking information.
	 * 
	 * @param  contractId  the contract id
     */
	private void calAdvancePaymentTracking(Long contractId) {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractResDTO resDTO = new ContractResDTO();
		resDTO = contractSO.calAdvancePaymentTracking(contractId);
		
		BigDecimal paidAdvancePaymentAmt = (resDTO.getPaidAdvancePaymentAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidAdvancePaymentAmt();
		BigDecimal paidInvoiceAmt = (resDTO.getPaidInvoiceAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidInvoiceAmt();
		
		this.getManagedBean().setPaidAdvancePaymentAmt(paidAdvancePaymentAmt);
		this.getManagedBean().setPaidInvoiceAmt(paidInvoiceAmt);
	}
	
	/**
	 * The method used to set the date of frequency sSchedule.
     */
	private void scheduleDate() {	
		for (CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
				String fromDate = dateFormat.format(ctSchedules.getFromDate());
				ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));
					   
				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
				ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}
	
	/**
	 * The method used to set the date of progressive payment.
     */
	private void progressiveDate() {	
		for (CtProgressivePayment ctSchedules : getManagedBean().getContractVer().getCtProgressivePayments()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
			    String fromDate = dateFormat.format(ctSchedules.getFromDate());
			    ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));
			   
				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
			    ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}
	
	/**
	 * The method used for creating the zone tree node.
     */
	private void retriveZonesByContractVer() {
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;
	    
		for (CtContractZone zone : this.getManagedBean().getContractVer().getCtContractZones()) {
			if (zone.getDeliveryTermUom() == null) {
				zone.setDeliveryTermUom(new PmParameter()); 
				zone.getDeliveryTermUom().setParameterId(0L);
			}
			zoneNode = new DefaultTreeNode("zone", new LocationDTO(
					zone.toString(), zone.getZoneName(), zone.getDeliveryTerm(), 
						zone.getDeliveryTermUom().getParameterId()), rootLocationTree);
			List<PmState> state = new ArrayList<PmState>();
			if (zone.getCtContractZoneDtls() != null) {
				for (CtContractZoneDtl zoneDtl : zone.getCtContractZoneDtls()) {
					if (zoneDtl.getState() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(
							ParameterConst.LOCATION_LEVEL_STATE)) {
						if (!state.contains(zoneDtl.getState())) {
							new DefaultTreeNode("state",new LocationDTO(zoneDtl.getContractZoneDtlId(), zoneDtl.getState()), zoneNode);
						}
						state.add(zoneDtl.getState());
					}
					if (zoneDtl.getDistrict() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(
							ParameterConst.LOCATION_LEVEL_DISTRICT)) {
						new DefaultTreeNode("district", new LocationDTO(zoneDtl.getContractZoneDtlId(), zoneDtl.getDistrict()), zoneNode);
					}
				}
			}
		}
		this.getManagedBean().setCtZoneTrees(rootLocationTree);
	}
	
	/**
	 * The method used to render the check box for company name
	 * and supplier address if there are any updates from profile
	 * management module.
     */
	private void renderUpdateCheckbox(CtContractVer ctContractVer) {
		if (ctContractVer.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				&& this.getManagedBean().isUpdateContract()) {
			if (!ctContractVer.getCtContractValue().getSupplierName().equals(
					this.getManagedBean().getCtContract().getSmSupplier().getCompanyName())) {
				this.getManagedBean().setUpdateCompanyName(true);
			}
			SmAddress latestAddress = retrieveLatestSupplierAddress();
			if (!ctContractVer.getCtContractValue().getSupplierAddress().equals(latestAddress)) {
				this.getManagedBean().setUpdateSupplierAddress(true);
			}
		} else {
			this.getManagedBean().setUpdateCompanyName(ctContractVer.getCtContractValue().isCompanyNameChange());
			this.getManagedBean().setUpdateSupplierAddress(ctContractVer.getCtContractValue().isAddressChange());
		}
	}
	
	/**
	 * The method used to retrieve the latest supplier address.
     */
	private SmAddress retrieveLatestSupplierAddress() {
		SmAddress smAddress = new SmAddress();
		List<SmCompanyAddress> smCompanyAddresses = new ArrayList<SmCompanyAddress>();
		smCompanyAddresses = this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();
		for (SmCompanyAddress address : smCompanyAddresses) {
			if (address.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) { // C-Company Address, M-Mailing Address
				smAddress = address.getSmAddress();
			}
		}
		return smAddress;
	}
	
	/**
	 * The method used to retrieve the zone object.
     */
	private void getZoneObjDetails() {
    	if (!getManagedBean().getContractVer().getCtContractZones().isEmpty()) {
    		for (CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
    			for (TreeNode zonename : getManagedBean().getCtZoneTrees().getChildren()) {
    				if (ctContractZone.toString().equalsIgnoreCase(((LocationDTO)zonename.getData()).getZoneId())) {
    					if (ctContractZone.getDeliveryTermUom() == null) {
     					   ctContractZone.setDeliveryTermUom(new PmParameter());
    					}
    					ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
    					ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
    				}
    			}
    		}
    	}
	}
	
	/**
	 * The method used to retrieve schedule or progressive 
	 * from date and to date value.
     */
	private void scheduleProgressiveDate() {    	
		if (getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
			if (getManagedBean().getContractVer().getCtSchedules() != null 
					&& !getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
				for (CtSchedule schedule : this.getManagedBean().getContractVer().getCtSchedules()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						Date d1 = new Date();
						String localDate = dateFormat.format(d1);
						String date = localDate.substring(0, 2);
						String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				getManagedBean().getContractVer().setCtProgressivePayments(null);
			}
		} else {
			if (getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				for (CtProgressivePayment schedule : getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						Date d1 = new Date();
						String localDate = dateFormat.format(d1);
						String date = localDate.substring(0, 2);
						String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							log.debug(e.getMessage());
						}
					}
				}
				getManagedBean().getContractVer().setCtSchedules(null);
			} 
		}	
	}
	
	/**
	 * The method used for generating a list of available financial institution
	 * for performance bond as selection.
	 * 
	 * @return the selection list of financial institution
	 */
	public SelectItem[] retrieveFinancialOrgList() {
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;
		
		try {
		    int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		    
		    for (PmFinancialOrg pmFinancialOrg : list) {
				  items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName()); 
		    }
	    } catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			financialOrgSO = null;
		}
		return items;
	}
	
	/**
	 * The method used for generating a list of available ministry
	 * for searching agency as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 0;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
		    
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size()];
			for (PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for generating a list of available jabatan
	 * for searching agency as selection.
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
			reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
			
		    List<Object[]> list = resDTO.getOrgProfileDDList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			
		    for(Object[] object : list){
		    //for (PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
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
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			
			int i = 1;
		 	StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			List <String> docTypeList = new ArrayList<String>();
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			docTypeList.add(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
			
			reqDTO.setDocTypeList(docTypeList);
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
		    List<PmStatusDesc> list =  resDTO.getStatusDescList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		      
			for (PmStatusDesc statusDesc : list) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			statusSO = null;
		}
	    return items;
	}
	
	/**
	 * The method used for generating a list of fulfillment type
	 * for schedule as selection.
	 * 
	 * @return the selection list of fulfillment type
	 */
	public SelectItem[] getScheduleTypeDropDown() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = null;
		
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					ParameterTypeConst.SCHEDULE_TYPE, searchBean);
			
		    items = new SelectItem[parameterDescs.size()];
		    int i = 0;
		    for (PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterId(), 
		    			parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used to retrieve the status completion of the contract.
	 * 
	 * @return complete or incomplete
     */
	public String chkContractStatusType(int status) {
		if (status == ContractConst.STATUS_COMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
	}
	
	/**
	 * The method used to check whether the contract is fulfilled the criteria
	 * for amendment.
	 * 
	 * @return true, if the criteria is fulfilled
	 *         false, if the criteria is not fulfilled
     */
	public boolean checkAmendmentAllow(VCtContract vCtContract) {
		if (vCtContract.getCtContract().getLatestContractVer() != null) {			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractVerId(vCtContract.getCtContract().getLatestContractVer().getContractVerId());
			// Amendment always get the latest contract amount
			reqDTO.setAsAtDate(vCtContract.getCtContract().getLatestContractVer().getCtContractValue().getExpDate());
			
			ContractResDTO resDTO = contractSO.retrieveContractAmount(reqDTO);			
			double contractAmount = resDTO.getCtContractAmount().getContractAmount().doubleValue();
			if (contractAmount <= ContractConst.CONTRACT_THRESHOLD_200k) {
				return false;
			} else if (contractAmount <= ContractConst.CONTRACT_THRESHOLD_500k) {
				if (vCtContract.getCtContract().isAgreement()) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}	

    /**
	 * The method used to get the modifiability of contract physical no.
	 * 
	 * @return true, if contract physical no field is disabled
	 *         false, if contract physical no field is enabled
     */
    public boolean getConditionalValue() {
    	if (getManagedBean().isViewContract()) {
    		return true;
    	} else if (!getManagedBean().getCtContractAmount().isAgreement() 
    			&& getManagedBean().getContractVer().getContractPhysicalNo() == null) {
    		if (getManagedBean().getContractVer().getCtContractValue().isConcession()) {
    			return false;
    		} else {
    			return true;
    		}
    	} else {
    		return false;
    	}
    }
    
    /**
	 * The method used to set the justification value.
     */
    public void chkJustificationAction() {
    	try {
    		if (this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()) {
    			this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
    		}
    		if (this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
    			this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
    			if (!this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()){
    				this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
    				this.getManagedBean().setChkJustification(true);
    			}
    		}
    		if (this.getManagedBean().isChkJustification() 
    				&& !this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
    			this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
    			this.getManagedBean().setChkJustification(false);
			}
    		if (this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() 
    				&& this.getManagedBean().getContractVer().getCtContractValue().isConcession()) {
    			this.getManagedBean().setChkConceValue(true);
			} else {
				this.getManagedBean().setChkConceValue(false);
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
	 * The method used to retrieve the company name of supplier.
     */
	public void retrieveCompanyName(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			this.getManagedBean().setSupplierCompanyName(
					this.getManagedBean().getCtContract().getSmSupplier().getCompanyName());
		} else {
			this.getManagedBean().setSupplierCompanyName(
				this.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		}
	}
	
	/**
	 * The method used to retrieve the address of supplier.
     */
	public void retrieveSupplierAddress(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			this.getManagedBean().setSmAddress(retrieveLatestSupplierAddress());
		}
		else {
			this.getManagedBean().setSmAddress(this.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
		}
		getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
	}
	
	/**
	 * The method used to add a new performance bond record.
	 * This method will generate a new record in performance bond
	 * list, user allow to enter the bond details and attachment. 
	 */
    public void addBondDetails() {
		CtPerformanceBond newBond = new CtPerformanceBond();
		newBond.setFinancialOrg(new PmFinancialOrg());
		newBond.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
		
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);		
		ContractAmendmentReqDTO amendReqDTO = new ContractAmendmentReqDTO();
		amendReqDTO.setContractId(getManagedBean().getCtContract().getContractId());
		amendReqDTO.setAmendmentEffectiveDate(getManagedBean().getCtContractAmount().getEffDate());		
		ContractAmendmentResDTO resAmendDto = amendSO.retrieveContractEndDateByAmendmentEffectiveDate(amendReqDTO);
		
        newBond.setExpDate(resAmendDto.getPerformanceBondExpDate());
     	getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(newBond);
	}
	
	/**
	 * The method used to select all added performance bond(s).
     */
	public void selectAllBond() {
		if (getManagedBean().isSelectAll()) {
			for (CtPerformanceBond bonds : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
				bonds.setSelectedCheckBox(true);
			}
		}
	}
	
	/**
	 * The method used to remove the selected performance bond(s).
     */
	public void removePerformanceBond() {
		try {
			if (getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null) {
				Iterator<CtPerformanceBond> performanceBond = getManagedBean().getCtContractAmount().getCtPerformanceBonds().iterator();
				while (performanceBond.hasNext()) {
					CtPerformanceBond  bond = performanceBond.next();
					if (bond.isSelectedCheckBox()) {
						performanceBond.remove();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			
		}
	}
	
	/**
	 * The method used to add a new agency record.
	 * This method will generate a new record in agency
	 * list, user allow to enter the agency details. 
	 */
	public void addAgencyDetails() {
		List<CtAgency> ctAgencyList = new ArrayList<CtAgency>();		
		for (int i = 0; i < selectedPmOrgProfile.length; i++) {
			//Remove if already exist, avoid duplicate
		  	for (CtAgency agency : this.getManagedBean().getContractVer().getCtAgencies()) {
		  		if (agency.getOrgProfile().getOrgProfileId().equals(selectedPmOrgProfile[i].getOrgProfileId())) {
		  			throw new EpException(MessageConst.INFO_758000, new String[]{agency.getOrgProfile().getOrgName()}); 
		  		}
		  	}
			
			CtAgency ctAgency = new CtAgency();
			if (getManagedBean().getContractVer().getCtContractValue().getEffDate().before(DateUtils.getCurrentDateWithoutTime())) {
				ctAgency.setEffDate(DateUtils.getCurrentDateWithoutTime());
			} else {
				ctAgency.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			}
			ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			ctAgency.setCeilingAmount(new BigDecimal(0));
			ctAgency.setChkStatus(true);
			
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(selectedPmOrgProfile[i].getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileDtlByProfileId(reqDTO);
			ctAgency.setOrgProfile(resDTO.getPmOrgProfile());
			 
			if (!retrieveMinistryOrgProfile(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId().equals(
					retrieveMinistryOrgProfile(ctAgency.getOrgProfile().getOrgProfileId()).getOrgProfileId())) {
				ctAgency.setAp178(true);
			}		  	
			ctAgency.setCtContractVer(this.getManagedBean().getContractVer());
			ctAgencyList.add(ctAgency);
			 
			if (ctAgency != null && this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 0) {
				for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
					CtItemCeilingQty itemCeilingQty = new CtItemCeilingQty();
					itemCeilingQty.setCtAgency(ctAgency);
					itemCeilingQty.setCtContractItem(contractItem);
					itemCeilingQty.setCeilingQuantity(new BigDecimal(0));
					itemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
					itemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
					contractItem.getCtItemCeilingQties().add(itemCeilingQty);
				}
			}
		}
		if (selectedPmOrgProfile.length>0) {
			this.getManagedBean().getContractVer().getCtAgencies().addAll(ctAgencyList);
		}
		selectedPmOrgProfile = null;
	}
	
	/**
	 * The method used to delete the selected agency.
	 */
	public void deleteAgencyDetails() {
		try {
			if (this.getManagedBean().getContractVer().getCtAgencies() != null) {
				Iterator<CtAgency> agencyIterator = this.getManagedBean().getContractVer().getCtAgencies().iterator();
				while (agencyIterator.hasNext()) {
					CtAgency agency = agencyIterator.next();
					if (agency.isSelectedCheckBox()) {
						agencyIterator.remove();
						if (this.getManagedBean().getCtItemList() != null && this.getManagedBean().getCtItemList().size() > 0) {
							for (CtContractItem contractItem: this.getManagedBean().getCtItemList()) {
								Iterator<CtItemCeilingQty> ctItemCeilingQtyIterator =  contractItem.getCtItemCeilingQties().iterator();
								while (ctItemCeilingQtyIterator.hasNext()) {
									CtItemCeilingQty ctItemCeilingQty = ctItemCeilingQtyIterator.next();
									if (ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId().equals(
											agency.getOrgProfile().getOrgProfileId())) {
										ctItemCeilingQtyIterator.remove();
									}
								}
							}	
						}
					}
				}
			}
			this.getManagedBean().setSelectedAgency(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * The method used to activate the delete agency button 
	 * once the agency record is selected.
	 */	
	public void selectAgencyListner(AjaxBehaviorEvent e) {		
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			getManagedBean().setButtonHideActive(false);
		} else {
			getManagedBean().setButtonHideActive(true);
		}	
	}
	
	/**
	 * The method used to retrieve the condition for construct the LOV listing
	 * @return the condition
	 */
	public String getDeliveryAddressCondition() {
		String condition = "recordStatus^1|addressType^" + AddressType.DELIVERY_ADDRESS.value()
				+ "|pmOrgProfile.orgProfileId^" + this.getManagedBean().getSelectedAgency().getOrgProfile().getOrgProfileId();
		
		PmParameter locationLevel = getManagedBean().getContractVer().getCtContract().getLocationLevel();
		if (locationLevel != null) {
			IContractZoneSO contractZoneSO = (IContractZoneSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractZoneSO.class);
			ContractZoneReqDTO reqDTO = new ContractZoneReqDTO();
			reqDTO.setCtContract(getManagedBean().getCtContract());
			
			ContractZoneResDTO resDTO = contractZoneSO.getContractZoneDtls(reqDTO);
			String stateList = "";
			String districtList = "";
			
			for (CtContractZoneDtl zoneDtl : resDTO.getZoneDtlList()) {
				if (locationLevel.getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)
						&& zoneDtl.getState() != null) {
					stateList += zoneDtl.getState().getStateId() + ",";
				}
				if (locationLevel.getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)
						&& zoneDtl.getDistrict() != null) {
					districtList += zoneDtl.getDistrict().getDistrictId() + ",";
				}
			}
			if (!stateList.isEmpty()) {
				condition += "|pmAddress.pmState.stateId^" + stateList + "^" + SearchOperator.OPERATOR_IN.value();
			}
			if (!districtList.isEmpty()) {
				condition += "|pmAddress.pmDistrict.districtId^" + districtList + "^" + SearchOperator.OPERATOR_IN.value();
			}
		}
		return condition;
	}
	
	/**
	 * The method used to check whether is contract sharing for agencies.
	 * 
	 * @return true, if added agency's ministry different with contract's ministry
	 *         false, if added agency's ministry same with contract's ministry
     */
	public boolean isContractSharing() {
		PmOrgProfile contractMinistry = retrieveMinistryOrgProfile(
				getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		
		for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			if (!contractMinistry.getOrgProfileId().equals(
					retrieveMinistryOrgProfile(ctAgency.getOrgProfile().getOrgProfileId()).getOrgProfileId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * The method used to add new agency's delivery address.
	 * This method will generate new record in delivery address
	 * list. 
	 */	
	public void addDeliveryAddress(List<PmAddressType> addressTypeList) {
		try {
			if (addressTypeList != null) {
				List<CtAgencyAddress> ctAgencyAddress = new ArrayList<CtAgencyAddress>();
				List<CtAgencyAddress> agencyAddressList = new ArrayList<CtAgencyAddress>();				
				ctAgencyAddress = getManagedBean().getSelectedAgency().getCtAgencyAddresses();
				
				for (PmAddressType addressType : addressTypeList) {
					CtAgencyAddress agencyAddress = new CtAgencyAddress();
					agencyAddress.setAddressType(addressType);
					agencyAddress.setCtAgency(getManagedBean().getSelectedAgency());		
					agencyAddressList.add(agencyAddress);
				   
					//Remove if already exist, avoid duplicate
					for (CtAgencyAddress address : ctAgencyAddress) {
						if( address.getAddressType().getAddressTypeId().equals(addressType.getAddressTypeId())) {
							agencyAddressList.remove(agencyAddress);
						}
					}
				}
				ctAgencyAddress.addAll(agencyAddressList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * The method used to delete the selected agency's delivery address.
	 */
	public void deleteDeliveryAddress() {		
		try {
			getManagedBean().getSelectedAgency().getCtAgencyAddresses().removeAll(getManagedBean().getSelectedListOfAddress());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * The method used for validating the ceiling amount of agencies.
	 * A warning message will display on screen when the total ceiling amount is 
	 * larger than contract value.
	 */
	public void validateCeilingAmount(CtAgency agencyData) {
    	List<CtAgency> agencyList = this.getManagedBean().getContractVer().getCtAgencies();
    	if (agencyList.size() > 0) {
			double ceilingAmt = 0.0;
			for (CtAgency ctAgency : agencyList) {
				ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
			}
			double latestContractAmount = 0.0;
			for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContractAmounts()){
	    		if (getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
	    				&& getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
	    			latestContractAmount = contractAmt.getContractAmount().doubleValue();
	    			break;
	    		}
	    	}
			if (ceilingAmt > latestContractAmount) {
				agencyData.setCeilingAmount(BigDecimal.valueOf(0));
				CommonUtils commonUtils = new CommonUtils();
				DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
						CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
				throw new EpException(MessageConst.ERROR_750007,
						new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
			}
    	}
    }
	
	/**
	 * The method used to render the selected product item information.
	 */
	public void changeField() {
		try {
			flush();
			if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				getManagedBean().setItemPricing(true);
			} else if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
				getManagedBean().setItemCeilingQuantity(true);
				refreshAgencyCeilingQuantityList();
			}
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the product item details.
	 */
	private void flush() {
	   this.getManagedBean().setItemPricing(false);
	   this.getManagedBean().setItemCeilingQuantity(false);
	   this.getManagedBean().setProductItem(false);
	}
    
	/**
	 * The method used to render the ceiling quantity information
	 * of the selected product item.
	 */
    public void refreshCeilingQuantity() {
    	if (getManagedBean().getSelectedItem() != null) {
    		if (getManagedBean().getShowItem() != null
    				&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
    			getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
    		} else if (getManagedBean().getShowItem() != null
    				&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
    			getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
    			refreshAgencyCeilingQuantityList();
    		} else {
    			getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
    		}
    		if (!getManagedBean().getSelectedItem().getCtItemPrices().isEmpty()
    				&&(getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
    						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
    						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
    			for (int temp = 0;temp<getManagedBean().getSelectedItem().getCtItemPrices().size(); temp++) {
    				Date date = getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getEffDate();
    				int counter =0;
    				int index=0;
    				int countRow =0;
    				for (CtItemPrice itemPrice1 : getManagedBean().getSelectedItem().getCtItemPrices()) {
    					countRow++;
    					if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
    							|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
    							|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF)
    							|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
    						if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getFromQty().equals(itemPrice1.getFromQty()) 
    								&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getToQty().equals( itemPrice1.getToQty()) 
    								&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
    							counter++;
    							if (counter >= 2) {
    								if (date.before(itemPrice1.getEffDate())) {
    									index = countRow;
    									date = itemPrice1.getEffDate();
    								}	
    							}
    						}
    					} else if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
    						counter++;
    						if (counter >= 2) {
    							if (date.before(itemPrice1.getEffDate())) {
    								index = countRow;
    								date = itemPrice1.getEffDate();
    							}
    						}
    					}
    				}
    				if (index > 0) {
    					CtItemPrice priceOfItem = getManagedBean().getSelectedItem().getCtItemPrices().get(index-1);
    					priceOfItem.setChangeZoneLabel(true);
	    	          	getManagedBean().getSelectedItem().getCtItemPrices().remove(index-1);
	    	          	getManagedBean().getSelectedItem().getCtItemPrices().add(index-1, priceOfItem);
    				}
    			}
    		}
	  	}
	}
    
    /**
	 * The method used to refresh the ceiling quantity information.
	 */
    private void refreshAgencyCeilingQuantityList() {
    	Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
    	for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
    		itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
    	}
    	for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
    		if (!itemCeilingQtyMap.containsKey(ctAgency.getOrgProfile().getOrgProfileId())) {
    			CtItemCeilingQty ctItemCeilingQty = new CtItemCeilingQty();
    			ctItemCeilingQty.setCtAgency(ctAgency);
    			ctItemCeilingQty.setCtContractItem(getManagedBean().getSelectedItem());
    			ctItemCeilingQty.setCeilingQuantity(new BigDecimal(0));
    			ctItemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
    			ctItemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
    			getManagedBean().getSelectedItem().getCtItemCeilingQties().add(ctItemCeilingQty);
    		}
    	}
	}
    
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
				if (ctItemCeilingQty.getCtAgency().isChkStatus()) {
					activeList.add(ctItemCeilingQty);
				}
			}
		}
		return activeList;
	}
    
    /**
	 * The method used for validating the ceiling quantity of items.
	 * A warning message will display on screen when the total ceiling quantity is 
	 * larger than the maximum quantity of the item.
	 */
    public void validateCeilingQty(CtItemCeilingQty ceilingQtyData) {
    	Date today = DateUtils.getCurrentDateWithoutTime();
    	double totalCeilingQuantity = 0.0;
    	CtContractItem item = this.getManagedBean().getSelectedItem();
		if (item.getCtItemMaxQties().size() != 0) {
			BigDecimal maxCeilingQty = new BigDecimal(0);
			for (CtItemMaxQty ctItemMaxQty : item.getCtItemMaxQties()) {
				if (ctItemMaxQty.getEffDate().getTime()<=today.getTime() && ctItemMaxQty.getExpDate().getTime()>=today.getTime()) {
					maxCeilingQty = ctItemMaxQty.getMaxQuantity();
					break;
				}
			}
			for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
				totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
			}
			if (maxCeilingQty.doubleValue() < totalCeilingQuantity) {
				ceilingQtyData.setCeilingQuantity(BigDecimal.valueOf(0));
				throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
			}
		}
    }
	
	/**
	 * The method used to add new deduction clause.
	 * This method will generate new record in deduction clause
	 * list. 
	 */	
	public void addDeductionClause() {
		CtDeductionClause clause = new CtDeductionClause();
		this.getManagedBean().getSelectedDeduction().getCtDeductionClauses().add(clause);
	}

	/**
	 * The method used to delete the selected deduction clause.
	 */
	public void deleteClauseDetails() {
		for (int i = 0; i < selectedDeductionClauses.length; i++) {
			 CtDeductionClause clause = selectedDeductionClauses[i];
			 this.getManagedBean().getSelectedDeduction().getCtDeductionClauses().remove(clause);
		}
		selectedDeductionClauses = null;
	}
	
	/**
	 * The method used to reset the schedule information once 
	 * user select the schedule type.
	 */
    public void changeScheduleTypeListener() {		
       getManagedBean().setChkSchedule(false);
  	   getManagedBean().setChkProgressive(false);
  	   getManagedBean().setProgSchedButtonDeactive(false);
  	   if (getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
  		   getManagedBean().getContractVer().getCtContract().setFrequencySchedule(true);
  	   } else if (getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
  	 	   getManagedBean().getContractVer().getCtContract().setFrequencySchedule(false);
  	   }   
	}
    
    /**
	 * The method used to add a new frequency schedule.
	 * This method will generate a new record in frequency schedule
	 * list. 
	 */
    public void addScheduleDetails() {
		CtSchedule ctSchedules = new CtSchedule();
		ctSchedules.setScheduleNo(getManagedBean().getContractVer().getCtSchedules().size() + 1);
		ctSchedules.setScheduleType(new PmParameter());
		getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
	}
	
    /**
	 * The method used to delete the selected frequency schedule.
	 */
	public void deleteScheduleDetails() {
		for (int i = 0; i < selectedSchedule.length; i++) {
			CtSchedule ctSchedule = selectedSchedule[i];
			this.getManagedBean().getContractVer().getCtSchedules().remove(ctSchedule);
		}
		selectedSchedule = null;	
	}

	/**
	 * The method used to add a new progressive payment.
	 * This method will generate a new record in progressive payment
	 * list. 
	 */
	public void addFrequencyDetails() {
		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
		ctProgressivePayment.setPhaseNo(getManagedBean().getContractVer().getCtProgressivePayments().size() + 1);
		getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	/**
	 * The method used to delete the selected progressive payment.
	 */
	public void deleteFrequencyDetails() {
		getManagedBean().setProgSchedButtonDeactive(false);
		for (int i = 0; i < selectedProgressive.length; i++) {
			CtProgressivePayment ctProgressivePayment = selectedProgressive[i];
			this.getManagedBean().getContractVer().getCtProgressivePayments().remove(ctProgressivePayment);
		}
		selectedProgressive = null;
	}
	
	/**
	 * The method used for generating a list of available start month
	 * as selection.
	 * 
	 * @return selectItems the selection list of start month
	 */
	public List<SelectItem> retrieveStartMonthList(String  tableStartYear) {
		List<SelectItem> selectItems = null;
	   	Map<String, String> months = null;
	    
	   	try {
	   		months = new HashMap<>();
			months.put("01", "January");
			months.put("02", "February");
			months.put("03", "March");
			months.put("04", "April");
			months.put("05", "May");
			months.put("06", "June");
			months.put("07", "July");
			months.put("08", "August");
			months.put("09", "September");
			months.put("10", "October");
			months.put("11", "November");
			months.put("12", "December");  
					
			int schedStartYear = 0;
			String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			int startMonth = Integer.valueOf(fromdate.substring(3, 5));
			int startYear = Integer.valueOf(fromdate.substring(6, 10));
				
			if (tableStartYear != null && !tableStartYear.equalsIgnoreCase("")) {
				schedStartYear = Integer.valueOf(tableStartYear);
			}
			String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			int endMonth = Integer.valueOf(enddate.substring(3, 5));
			int endYear = Integer.valueOf(enddate.substring(6, 10));
		 
			selectItems = new ArrayList<SelectItem>();
			selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));		
			if (schedStartYear == 0 && startYear == endYear) { 
				for (int i = startMonth; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if ((schedStartYear == 0) || (schedStartYear == startYear && schedStartYear != endYear)) { 
				for (int i = startMonth; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear == startYear  && schedStartYear == endYear) { 
				for (int i = startMonth; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear != startYear  && schedStartYear == endYear) { 
				for (int i = 1; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} else if (schedStartYear > startYear && schedStartYear < endYear) {
				for (int i = 1; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
			} 
	   	} finally { 
	   		
	   	}
	   	return selectItems;
	}
	
	/**
	 * The method used for generating a list of available end month
	 * as selection.
	 * 
	 * @return selectItems the selection list of end month
	 */
	public List<SelectItem> retrieveEndMonthList(String tableEndYear) {
		List<SelectItem> selectItems = null;
		Map<String, String> months = null;
		
		try {
			months = new HashMap<>();
			months.put("01", "January");
			months.put("02", "February");
			months.put("03", "March");
			months.put("04", "April");
			months.put("05", "May");
			months.put("06", "June");
			months.put("07", "July");
			months.put("08", "August");
			months.put("09", "September");
			months.put("10", "October");
			months.put("11", "November");
			months.put("12", "December");  
						
		    int schedEndYear =0;
			String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			int startMonth = Integer.valueOf(fromdate.substring(3, 5));
			int startYear = Integer.valueOf(fromdate.substring(6, 10));
		
			if (tableEndYear != null && !tableEndYear.equalsIgnoreCase("")) {
				schedEndYear = Integer.valueOf(tableEndYear);
			}
			
			String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			int endMonth = Integer.valueOf(enddate.substring(3, 5));
		 	int endYear = Integer.valueOf(enddate.substring(6, 10));
			 
		 	selectItems = new ArrayList<SelectItem>();
		 	selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));						 
		 	if (schedEndYear == endYear && schedEndYear == startYear) { 
		 		for (int i = startMonth; i <= endMonth; i++) {
		 			selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
		 		}
		 	} else if (schedEndYear == endYear && schedEndYear != startYear) { 
				for (int i = 1; i <= endMonth; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				}
		 	} else if (schedEndYear != endYear && schedEndYear == startYear) { 
		 		for (int i = startMonth; i <= 12; i++) { 
		 			selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
		 		}
		 	} else if ( schedEndYear >  startYear && schedEndYear < endYear) {
		 		for (int i = 1; i <= 12; i++) {
					selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
		 		}
		 	} 
		} finally { 
			
		}
		return selectItems;
	}
	
	/**
	 * The method used to convert the month index to
	 * string text. 
	 * 
	 * @param index    the index of the month
	 * @return months  the string text of the month
	 */
	private String changeIntToString(int index) {
		String months = "";
		if (index == 1) {
			months = "01";
		} else if (index == 2) {
			months = "02";
		} else if (index == 3) {
			months = "03";
		} else if (index == 4) {
			months = "04";
		} else if (index == 5) {
			months = "05";
		} else if (index == 6) {
			months = "06";
		} else if (index == 7) {
			months = "07";
		} else if (index == 8) {
			months = "08";
		} else if (index == 9) {
			months = "09";
		} else if (index == 10) {
			months = "10";
		} else if (index == 11) {
			months = "11";
		} else if (index == 12) {
			months = "12";
		}
		return months;		
	}

	/**
	 * The method used for generating a list of available year
	 * as selection.
	 * 
	 * @return the selection list of year
	 */
	public List<String> getYearList() {	 
		if (getManagedBean().getYearList() == null) {
			getManagedBean().setYearList(new ArrayList<String>());
		    int startYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()).substring(6, 10));
		    int endYear = Integer.valueOf(dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()).substring(6, 10));
		    for (int i = startYear; i <= endYear; i++) {
		    	getManagedBean().getYearList().add(String.valueOf(i));
		    }
		}
		return getManagedBean().getYearList();
	}
	
	/**
	 * The method used to retrieve the total month from selected
	 * start month to selected end month.
	 * 
	 * @return the total number of month(s)
	 */
	public int calculateTotalMonth(Object Var, String frequencyType) {
    	DateFormat dateFormat = null;
    	String fromDate = null;
    	String toDate = null;
    	
    	try {		     
    		dateFormat = new SimpleDateFormat("MM-yyyy");		  
    		if (frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
    			CtSchedule schedules = (CtSchedule)Var;
    			if (schedules.getEndMonth() != null) {
    				fromDate = schedules.getStartMonth()+"-"+ schedules.getStartYear();
    				toDate = schedules.getEndMonth() +"-"+schedules.getEndYear();
    			}
    		} else if (frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
    			CtProgressivePayment progressivePayment = (CtProgressivePayment)Var;
    			if (progressivePayment.getEndMonth() != null) {
    				fromDate = progressivePayment.getStartMonth() + "-" + progressivePayment.getStartYear();
    				toDate =  progressivePayment.getEndMonth() + "-" + progressivePayment.getEndYear();
    			}
    		}
    		Date startDate = dateFormat.parse((fromDate));
    		Date endDate = dateFormat.parse((toDate));
    		Calendar sDate = Calendar.getInstance();
    		Calendar eDate = Calendar.getInstance();
    		sDate.setTime(startDate);
    		eDate.setTime(endDate);
		   
    		int months  = (eDate.get(Calendar.YEAR) - sDate.get(Calendar.YEAR)) * 12
    				+ (eDate.get(Calendar.MONTH)- sDate.get(Calendar.MONTH));
		 
    		if (months < 0) {
    			return 0;
    		}
    		return months + 1;
    	} catch(Exception ex) {
    		
    	} finally {
		 
    	}
    	return 0;
    }
	
	/**
	 * The method used for validating the period of frequency schedule.
	 * A warning message will display on screen when the end date is 
	 * larger than start date.
	 */
   	public void validateScheduleDate(CtSchedule ctSchedule, int index) {
   		getManagedBean().getContractVer().getCtSchedules().remove(index);
    	getManagedBean().getContractVer().getCtSchedules().add(index, ctSchedule);
    	 
    	getManagedBean().setProgSchedButtonDeactive(false);
    	if (ctSchedule.getEndMonth() != null && ctSchedule.getStartMonth() != null) {
    		int startYear = Integer.valueOf((String) ctSchedule.getStartYear());  
			int endYear = Integer.valueOf((String) ctSchedule.getEndYear());
			int startMonth = Integer.valueOf((String) ctSchedule.getStartMonth());  
			int endMonth = Integer.valueOf((String) ctSchedule.getEndMonth());
			      
			if ((startMonth > endMonth && startYear >= endYear)|| (startMonth <= endMonth && startYear > endYear)) {
				for (CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {			            
					if (dataList.toString().equalsIgnoreCase(ctSchedule.toString())) {
						getManagedBean().setProgSchedButtonDeactive(true);
						throw new EpException(MessageConst.ERROR_750023);
					}
				}
			} 
    	}
	}
	
   	/**
	 * The method used for validating the period of progressive payment.
	 * A warning message will display on screen when the end date is 
	 * larger than start date.
	 */
    public void validateProgressiveDate(CtProgressivePayment  ctProgressivePayment, int index) {
    	getManagedBean().getContractVer().getCtProgressivePayments().remove(index);
    	getManagedBean().getContractVer().getCtProgressivePayments().add(index, ctProgressivePayment);
	    
    	calculatePercentage(ctProgressivePayment);
    	getManagedBean().setProgSchedButtonDeactive(false);
    	if (ctProgressivePayment.getEndMonth() != null && ctProgressivePayment.getStartMonth() != null) {
    		int startYear = Integer.valueOf((String) ctProgressivePayment.getStartYear());  
			int endYear = Integer.valueOf((String) ctProgressivePayment.getEndYear());
			int startMonth = Integer.valueOf((String) ctProgressivePayment.getStartMonth());  
			int endMonth = Integer.valueOf((String) ctProgressivePayment.getEndMonth());
		
			if ((startMonth > endMonth  &&  startYear >= endYear)||(startMonth <= endMonth  && startYear > endYear)) {
				for (CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (dataList.equals(ctProgressivePayment)) {
						getManagedBean().setProgSchedButtonDeactive(true);
						throw new EpException(MessageConst.ERROR_750024);
					}
			 	}
			}
    	}
	}
    
    /**
	 * The method used for calculating the percentage of progressive payment.
	 * A warning message will display on screen when the percentage
	 * is more than 100%.
	 */
    public void calculatePercentage(CtProgressivePayment progPayment) {
		float addValue = 0.0f;
  	    getManagedBean().setProgSchedButtonDeactive(false);
  	    for (CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
		    addValue = addValue + progressivePayment.getPercentage();
  		}
 		for (CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
 			if (progressivePayment.equals(progPayment)) {
 				if (Math.abs(addValue) > 100) {
 					getManagedBean().setProgSchedButtonDeactive(true);
 					progressivePayment.setPercentageExceeded(true);
 				} else if (Math.abs(addValue) == 100) {
 					getManagedBean().setProgSchedButtonDeactive(true);
 					progressivePayment.setPercentageExceeded(false);
 				} else {
 					progressivePayment.setPercentageExceeded(false);
 				}
			} else {
				progressivePayment.setPercentageExceeded(false);
			}
 		}  
	}
    
    /**
	 * The method used to retrieve the bank and account information
	 * of factoring.
	 * 
	 * @return bank and account information of factoring
     */
    public String getBankAccNoName() {    	
    	IFinancialOrgSO financialOrgSO;
    	FinancialOrgReqDTO reqDTO = null;
      
    	try {
    		financialOrgSO = BusinessDelegate.getInstance().getServiceObject(
    				ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
    		reqDTO = new FinancialOrgReqDTO();
    		if (getManagedBean().getCtContract().getCtFactoring().getFinancialOrg() != null) {
    			reqDTO.setFinancialOrgId(getManagedBean().getCtContract().getCtFactoring().getFinancialOrg().getFinancialOrgId());
    			FinancialOrgResDTO resDTO  = financialOrgSO.retriveFinAccDtlByFinOrgId(reqDTO);
    			PmFinancialAcc pmFinancialAcc = resDTO.getPmFinancialAcc();
    			getManagedBean().setFinancialAccNo(pmFinancialAcc.getAccountNo());
    			return pmFinancialAcc.getPmPaymentBankOrg().getFinOrgName();
    		} 
    	          
        } catch(Exception ex) {
        	
        }
    	return null;
    }
    
    /**
	 * The method used for initializing the remarks of contract.
	 */
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(getManagedBean().getContractVer().getSettingRemarks());
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
		if (getManagedBean().getContractVer().getCtWorkflowStatus() != null) {
			if (remarks.size()>0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId() 
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}
		
		if (remarks.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
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
	 * The method used to upload AP201 file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void uploadAp201File(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
			
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    getManagedBean().getContractVer().getCtContractValue().setAp201FileName(item.getFileName());
	    getManagedBean().getContractVer().getCtContractValue().setAp201FilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	}
	
	/**
	 * The method used to set the upload index for
	 * file upload purpose.
	 * The index is used to differentiate what type of file has
	 * been uploaded.
	 * 
	 * @param index index of the uploaded file
	 */
    public void setIndexProperty(int index){
    	  getManagedBean().setIndex(index);
    }
    
	/**
	 * The method used to upload agency file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void agencyFileUpload(FileUploadEvent event) throws Exception {
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    CtAgency agency =  getManagedBean().getContractVer().getCtAgencies().get(getManagedBean().getIndex());
	    getManagedBean().getContractVer().getCtAgencies().remove(getManagedBean().getIndex());
	    agency.setFileName(item.getFileName());
	    agency.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	    getManagedBean().getContractVer().getCtAgencies().add(getManagedBean().getIndex(), agency);
	 
	}
	
	/**
	 * The method used to delete the uploaded agency file.
	 */    
    public void deleteAgencyAttachement(){
    	CtAgency agency =  getManagedBean().getContractVer().getCtAgencies().get(getManagedBean().getIndex());
 	    getManagedBean().getContractVer().getCtAgencies().remove(getManagedBean().getIndex());
 	    agency.setFileName(null);
 	    agency.setFilePath(null);
 	    getManagedBean().getContractVer().getCtAgencies().add(getManagedBean().getIndex(), agency);
    }
    
	/**
	 * The method used to upload performance bond file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
    public void fileUpload(FileUploadEvent event) throws Exception {
    	UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    for (CtPerformanceBond ctPerformanceBond : getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
	    	if (ctPerformanceBond.equals(getManagedBean().getCtPerformanceBond())) {
	    		ctPerformanceBond.setFileName(item.getFileName());
	    		ctPerformanceBond.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	    	 }
	     }
	}
    
    /**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
	    if (filePath != null) {
	    	filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.ministry.contract.maintenance",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		if (isBPKCCA()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.central.contract.maintenance",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	/**
   	 * The method used to update the as at date and refresh the
   	 * contract amount and performance bond base on as at date
   	 */
	public void updateAsAtDate() {
    	for (CtContractAmount contractAmt : getManagedBean().getContractVer().getCtContractAmounts()){
    		if (getManagedBean().getContractVer().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
    				&& getManagedBean().getContractVer().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()){
    			getManagedBean().setCtContractAmount(contractAmt);
    			break;
    		}
    	}
    	// Skip for central contract because central contract always is off line agreement
    	// Thus physical no and signed date always refer to the current contract version
    	if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
				ParameterConst.CONTRACT_TYPE_MINISTRY)) {
	    	//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();
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
			
			if (getManagedBean().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(getManagedBean().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(getManagedBean().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);
				
				if (amendmentResDTO.getCtAmendment() != null) {
					getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);
					
					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}
				// Display the contract physical no of the agreement version during contract update
				if (getManagedBean().getTaskId() == null) {
					getManagedBean().getContractVer().setContractPhysicalNo(getManagedBean().getAgreementContractVer().getContractPhysicalNo());
				}
			} else {
				getManagedBean().setAgreementContractVer(getManagedBean().getContractVer());
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
	 * The method used to check whether current process is contract creation.
	 * 
	 * @return true, if current process is contract creation
	 *         false, if current process is not contract creation
     */
	public boolean contractCreationProcess() {
		if (getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_SUBMISSION.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_CONTRACT_CREATION.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_APPROVAL.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_REVISION.longValue()
				|| getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_REVISION_APPROVAL.longValue()) {
			return true;
		} else {
			return false;
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
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
			return RoleCode.CT_APPROVER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
			return RoleCode.BPK_CCA.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) {
			return RoleCode.BPK_APPROVER.value();
		} else {
			return null;
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
     * The method used to verify the login user is BPK approver 
     * or contract approver.
	 * 
	 * @return true, if login user is BPK approver or contract approver
	 *         false, if login user is not BPK approver or contract approver
     */
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
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
    
    public boolean chkCircularActiveRecord(){
    	if(getManagedBean().getCtContract().getCtCircularContracts().size() > 0) {
    			  for(CtCircularContract circularContract : getManagedBean().getCtContract().getCtCircularContracts()) {
    			      if(circularContract.getCtCircularVer().getRecordStatusFlg() == 1) {
    			    	  getManagedBean().setCtCircularVer(circularContract.getCtCircularVer());
    			    	  return true;
    			      }
    		  } 
    	}
      	return false;
    }
}
