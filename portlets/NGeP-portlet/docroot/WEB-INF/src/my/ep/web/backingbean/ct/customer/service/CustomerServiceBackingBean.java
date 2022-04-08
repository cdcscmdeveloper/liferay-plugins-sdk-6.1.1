package my.ep.web.backingbean.ct.customer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.CustomerServiceEntity;
import my.ep.web.managedbean.ct.customer.service.CustomerServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean (name = "customerServiceBackingBean") 
@ViewScoped
public class CustomerServiceBackingBean extends BaseBackingBean<CustomerServiceManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String SERVICE_VIEW_PAGE = "/xhtml/ct/customercare/viewcontractservice.xhtml?faces-redirect=true";
	private static final String CONTRACT_SERVICE_PAGE = "/xhtml/ct/customercare/customerservice.xhtml?faces-redirect=true";
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract search screen.
	 */
	public void preRenderService() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_SERVICE_PAGE);
	}

	/**
	 * The method used for searching the contract.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchCustomerService() throws Exception {
		if (getManagedBean().getSearchPtjProfile().getOrgProfileId() == null) {
			throw new EpException(MessageConst.ERROR_100022, 
					new String[] {ResourceBundleUtils.getMessage("lbl.ct.ptj", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		} else {
			getManagedBean().getCustomerServiceDataModel().reloadData();
		}
	}

	/**
	 * The method used to reset the search criteria for contract.
	 */
	public void searchReset() {
		this.getManagedBean().setContractNo(null);
		this.getManagedBean().setPtjCode(null);	
		this.getManagedBean().setPtjName(null);	
		this.getManagedBean().setContractTitle(null);	
		this.getManagedBean().setPhysicalContactNo(null);
		this.getManagedBean().setSearchContractStatus(0);	
		getManagedBean().setSearchPtjProfile(new PmOrgProfile());
		getManagedBean().getCustomerServiceDataModel().resetData();
	}

	/**
	 * The method used to navigate to customer service screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoViewPage(VCtContract ctContract, int status, int contractStatus){

		getManagedBean().setCotractStatus(contractStatus);
		getManagedBean().setStatus(status);
		getManagedBean().setKumpulanPtjName(retrieveJabatanByOrgProfileId(getManagedBean().getSearchPtjProfile().getOrgProfileId()));
		getManagedBean().setMinistryName(retrieveMinistryByOrgProfileId(getManagedBean().getSearchPtjProfile().getOrgProfileId()));

		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractVerId(ctContract.getCtContractVer().getContractVerId());
		reqDTO.setDocType(ctContract.getCtContractVer().getDocType());
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractResDTO resDTO = contractSO.retrieveContractAgency(reqDTO);	
		getManagedBean().setCtContractVer(resDTO.getCtContractVer());

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String sysUserId = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.BPM_PRINCIPAL);
		Object ctxToken = bpmSO.authenticate(sysUserId);
		getManagedBean().getCustomerServiceEntity().clear();
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		bpmReqDTO.setUserId(sysUserId);
		bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		if (ctContract.getCtContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION)
				&& ContractStatusConst.FD_PENDING_CONTRACT_CREATION.intValue() == getManagedBean().getCtContractVer().getCtWorkflowStatus().getPmStatus().getStatusId().intValue()) {
			bpmReqDTO.setDocNumber(getManagedBean().getCtContractVer().getCtContract().getLoa().getLoaNo());
		} else if (ctContract.getCtContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION)
				&& ctContract.getAgStatus() == 2) {
			bpmReqDTO.setDocNumber(getManagedBean().getCtContractVer().getCtContract().getLoa().getLoaNo());
		}

		bpmReqDTO.setCtxToken(ctxToken);
		BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);

		List<WorklistItemVO>  worklistItemVo = new ArrayList<WorklistItemVO>();
		if (bpmResDTO.getWorklistVO().getWorklistItem() != null && bpmResDTO.getWorklistVO().getWorklistItem().size() > 0) {
			worklistItemVo.addAll(bpmResDTO.getWorklistVO().getWorklistItem());
		}
		bpmReqDTO.setDocNumber(getManagedBean().getCtContractVer().getCtContract().getContractNo());
		BpmResponseDTO bpmResDTO1 = bpmSO.getTasksByDocNumber(bpmReqDTO);

		if (bpmResDTO1.getWorklistVO().getWorklistItem()!=null && bpmResDTO1.getWorklistVO().getWorklistItem().size() > 0) {
			worklistItemVo.addAll(bpmResDTO1.getWorklistVO().getWorklistItem());
		}
		
		if (worklistItemVo != null && worklistItemVo.size() > 0) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqUserDTO = new UserReqDTO();
			CustomerServiceEntity custServiceEntity = null;
			
			// Pending supplier LOA acknowledgement
			if (ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT.intValue() == getManagedBean().getCtContractVer().getCtWorkflowStatus().getPmStatus().getStatusId().intValue()) {
				ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
				SupplierSearchResDTO supplierSearchResDTO = null; 
				for (WorklistItemVO worklistItemVO : worklistItemVo) {
					custServiceEntity = new CustomerServiceEntity();
					 if (worklistItemVO.getAssignees() != null && !worklistItemVO.getAssignees().isEmpty()) {
						 for (String loginId : worklistItemVO.getAssignees()) {
							 reqUserDTO.setLoginId(loginId);
							 UserResDTO userResDTO=	userSO.retrieveUserByLoginId(reqUserDTO);
							 if (null != userResDTO && null != userResDTO.getPmUser()) {
								 supplierSearchReqDTO.setUserId(userResDTO.getPmUser().getUserId());
								 supplierSearchResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
								 if (supplierSearchResDTO.getResultBean() != null) {
									 custServiceEntity.setOfficerName(supplierSearchResDTO.getResultBean().getResultList().get(0).getCompanyName());
									 break;
								 }
							 }
						 }
						 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
					 }
				}
			} else {
				for (WorklistItemVO worklistItemVO : worklistItemVo) {
					custServiceEntity = new CustomerServiceEntity();
					if(worklistItemVO.getAssignees() != null && !worklistItemVO.getAssignees().isEmpty()) {
						for (String assignee : worklistItemVO.getAssignees()) {
							 custServiceEntity = new CustomerServiceEntity();
							 reqUserDTO.setLoginId(assignee);
							 UserResDTO userResDTO =	userSO.retrieveUserByLoginId(reqUserDTO);
							 if (null != userResDTO && null != userResDTO.getPmUser()) {
								 custServiceEntity.setOfficerName(userResDTO.getPmUser().getUserName());
								 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
							 }
						 }
					 }
				 }	
			}
		}			
		return SERVICE_VIEW_PAGE;
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

			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

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
	 * The method used reset PTJ profile value
	 */
	public void resetPtjProfile() {
		getManagedBean().setSearchPtjProfile(null);
	}

	/**
	 * The method used to navigate to customer service screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoViewPage() {
		return SERVICE_VIEW_PAGE;
	}

	/**
	 * The method used to navigate to contract's customer service screen
	 * 
	 * @return the navigate URL 
	 */
	public String gotoBackPage() throws IOException {
		return CONTRACT_SERVICE_PAGE;
	}
}
