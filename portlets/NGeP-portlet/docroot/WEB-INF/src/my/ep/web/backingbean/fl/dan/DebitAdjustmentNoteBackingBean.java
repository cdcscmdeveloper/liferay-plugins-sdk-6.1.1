/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.math.BigDecimal;
import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Managed Bean for Debit Adjustment Note
 * 
 * @author KK LIM
 * @since 1/11/2013
 *
 */
@ManagedBean(name = "danBackingBean")
@RequestScoped
public class DebitAdjustmentNoteBackingBean extends BaseBackingBean<DebitAdjustmentNoteManagedBean> {
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteBackingBean.class);

	private FacesScopes getFacesScopes(){
		return new FacesScopes();
	}
	
	public String submitAction(String action) throws ParseException {
		log.debug("DebitAdjustmentNoteBackingBean.submitAction()");
		IAdjustmentSO adjustmentSO = null;
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlAdjustment adjustment = getManagedBean().getAdjustment();
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			reqDTO.setAdjustment(adjustment);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			
			FulfilmentResDTO resDTO = adjustmentSO.saveAdjustment(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700026;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE), getManagedBean().getAdjustment().getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				reqDTO.setDanId(adjustment.getAdjustmentId());
				resDTO = adjustmentSO.retrieveAdjustmentById(reqDTO);
				setAdjustmentNote(resDTO);
				getManagedBean().setSelectedTabIndex(0);
			}
		} finally {
			adjustmentSO = null;
		}
		return null;
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		if(taskId != null){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(outcome);

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLDanDataType danObj = (FLDanDataType)obj;
			flReqDTO.setDanId(danObj.getDanId());
			IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
			setAdjustmentNote(resDTO);
			getManagedBean().setSelectedTabIndex(0);
		}
	}
	
	private void setAdjustmentNote(FulfilmentResDTO resDTO) {
		FlAdjustment adjustment = resDTO.getAdjustment();
		calInvSummary(adjustment);
		getManagedBean().setAdjustment(adjustment);
		
		if(getManagedBean().getAdjustment().getStatus()==null) {
			IStatusSO statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(FulfilmentStatusConst.PENDING_SUBSEQUENT_DEBIT_APPROVAL);
			getManagedBean().getAdjustment().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
		}
		if(getManagedBean().getAdjustment().getFlOrder().isFactored()){
			getFactoringBranchDtl();
		}else{
			getManagedBean().setAccountNoName(getManagedBean().getAdjustment().getFlInvoice().getAccountNo()+"  "+getManagedBean().getAdjustment().getFlInvoice().getPaymentBank().getFinOrgName());
		}
		
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(adjustment.getFlOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE, adjustment.getFlRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
		} else {
			new FulfilmentBackingBean(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE, adjustment.getFlRequest().getCtContract().getContractNo());
		}			
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
	}
	
	private void calInvSummary(FlAdjustment adjustment) {
		BigDecimal sumOrderedAmt = new BigDecimal(0);
		BigDecimal sumInvoiceAmt = new BigDecimal(0);
		BigDecimal sumDanAmt = new BigDecimal(0);
		for(FlAdjustmentItem adjustmentItem : adjustment.getFlAdjustmentItem()){
			sumOrderedAmt =sumOrderedAmt.add(adjustmentItem.getFlInvoiceItem().getOrderedAmt());
			sumInvoiceAmt = sumInvoiceAmt.add(adjustmentItem.getFlInvoiceItem().getInvAmt());
			sumDanAmt = sumDanAmt.add(adjustmentItem.getFlInvoiceItem().getInvAmt().subtract(adjustmentItem.getFlInvoiceItem().getOrderedAmt()));
			getManagedBean().setOrderedAmtTotal(sumOrderedAmt);
			getManagedBean().setInvAmtTotal(sumInvoiceAmt);
			getManagedBean().setDanAmtTotal(sumDanAmt);
		}
	}
	
	public void getFactoringBranchDtl(){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setBranchName(null);
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(getManagedBean().getAdjustment().getFlOrder().getPmFinancialBranch()!=null && getManagedBean().getAdjustment().getFlOrder().getPmFinancialBranch().getFinancialBranchId()!=null) {
			getManagedBean().setBranchName(getManagedBean().getAdjustment().getFlOrder().getPmFinancialBranch().getBranchName());
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(getManagedBean().getAdjustment().getFlOrder().getPmFinancialBranch().getFinancialBranchId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.toString());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(resDTO.getAddressTypeList().get(0).getPmContacts() != null ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
			getManagedBean().setAccountNoName(getManagedBean().getAdjustment().getFlInvoice().getAccountNo()+"  "+getManagedBean().getAdjustment().getFlInvoice().getPaymentBank().getFinOrgName());
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(getManagedBean().getAdjustment().getFlOrder().getPmFinancialOrg().getFinancialOrgId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
			getManagedBean().setAccountNoName(getManagedBean().getAdjustment().getFlInvoice().getAccountNo()+"  "+getManagedBean().getAdjustment().getFlInvoice().getPaymentBank().getFinOrgName());
		}
	}
	
	public void selectDanItem() {
		if(null !=getManagedBean().getSelectedDanItem().getFlItem().getPmVotFund()){
			getManagedBean().setVotFundCode(getManagedBean().getSelectedDanItem().getFlItem().getPmVotFund().getVotFundCode());
		}
		if(null != getManagedBean().getSelectedDanItem().getFlItem().getAssetInd()) {
			getManagedBean().setAssetIndCode(getManagedBean().getSelectedDanItem().getFlItem().getAssetInd().getParameterCode());
		}
		if(null != getManagedBean().getSelectedDanItem().getFlItem().getPmGlAccount()) {
			getManagedBean().setGlAccountCode(getManagedBean().getSelectedDanItem().getFlItem().getPmGlAccount().getGlAccCode());
		}
		if(null != getManagedBean().getSelectedDanItem().getFlItem().getPmPrgActivity()) {
			getManagedBean().setPrgActivityCode( getManagedBean().getSelectedDanItem().getFlItem().getPmPrgActivity().getPrgActivityCode());
		}
		if(null != getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia()) {
			getManagedBean().setSubSetiaCode((getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getSubSetiaCode()));
			
			if( getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getPmSetia()!=null) {
				getManagedBean().setSetiaCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getPmSetia().getSetiaCode());
				
				if(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getPmSetia().getPmProject()!=null) {
					getManagedBean().setProjectCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getPmSetia().getPmProject().getProjectCode());
				}
			}
			getManagedBean().setFinancingTypeCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getFinancingType().getParameterCode());
		}
	}
	
	public void onNodeUnSelect() {
		getManagedBean().setVotFundCode("");
		getManagedBean().setAssetIndCode("");
		getManagedBean().setGlAccountCode("");
		getManagedBean().setPrgActivityCode("");
		getManagedBean().setProjectCode("");
		getManagedBean().setSetiaCode("");
		getManagedBean().setSubSetiaCode("");
		getManagedBean().setFinancingTypeCode("");		
	}
	
	public String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
	/**
	 * Page navigation to FLDoc search page in DAN
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
}
