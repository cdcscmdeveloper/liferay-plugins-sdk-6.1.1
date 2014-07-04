/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
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
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.pm.AddressType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ChargeLineVO;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String submitAction(String action) throws ParseException {
		System.out.println("--DAN submit--");
		IAdjustmentSO adjustmentSO = null;
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlAdjustment adjustment = getManagedBean().getAdjustment();
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl", IAdjustmentSO.class);
			reqDTO.setAdjustment(adjustment);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			FulfilmentResDTO resDTO = adjustmentSO.saveAdjustment(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700043;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700004;
				}
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,	null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_DELIVERY_ORDER), getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} 
			
		} finally {
			adjustmentSO = null;
		}
		return null;
	}
	
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
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

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLDanDataType danObj = (FLDanDataType)obj;
			System.out.println("danObj.getDanId(): "+danObj.getDanId());
			flReqDTO.setDanId(danObj.getDanId());
			IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject("fl",IAdjustmentSO.class);
			resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
			FlAdjustment adjustment = resDTO.getAdjustment();
			populateChargeLineTree(adjustment);
			calInvSummary(adjustment);
			DebitAdjustmentNoteBackingBean danBB = new DebitAdjustmentNoteBackingBean();
			danBB.getManagedBean().setAdjustment(adjustment);
			danBB.getManagedBean().setTaskId(taskId);
			danBB.getManagedBean().setOutComes(outcome);
			if(getManagedBean().getAdjustment().getStatus()==null) {
				PmStatus pmStatus = new PmStatus();
				pmStatus.setStatusId(FulfilmentStatusConst.PENDING_SUBSEQUENT_DEBIT_APPROVAL);
				getManagedBean().getAdjustment().setStatus(pmStatus);
			}
			if(getManagedBean().getAdjustment().getFlOrder().isFactored()){
				getFactoringBranchDtl();
			}
		}
	}
	
	private void calInvSummary(FlAdjustment adjustment) {
		// TODO Auto-generated method stub
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

	private void populateChargeLineTree(FlAdjustment adjustment) {
		Map<Long, TreeNode> chargeLineMap = new HashMap<Long, TreeNode>();

		TreeNode root = getManagedBean().getDanChargeLineTree();
		root = new DefaultTreeNode("root", null);
		TreeNode parent = null;
		TreeNode child = null;
		BigDecimal sumOrderedAmt = new BigDecimal(0);
		BigDecimal sumInvoiceAmt = new BigDecimal(0);
		BigDecimal sumDanAmt = new BigDecimal(0);
		for(FlAdjustmentItem adjustmentItem : adjustment.getFlAdjustmentItem()){
			if(chargeLineMap.containsKey(adjustmentItem.getFlItem().getCmItem().getItemId())){
				parent = chargeLineMap.get(adjustmentItem.getFlItem().getCmItem().getItemId());
				child = new DefaultTreeNode(
						new ChargeLineVO("","","",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),parent);
				ChargeLineVO chargeLineVO = (ChargeLineVO)parent.getData();
				sumOrderedAmt = chargeLineVO.getOrderedAmt().add(adjustmentItem.getFlInvoiceItem().getOrderedAmt());
				sumInvoiceAmt = chargeLineVO.getInvoiceAmt().add(adjustmentItem.getFlInvoiceItem().getInvAmt());
				sumDanAmt = chargeLineVO.getDanAmt().add(adjustmentItem.getAdjustedAmt());
				chargeLineVO.setOrderedAmt(sumOrderedAmt);
				chargeLineVO.setInvoiceAmt(sumInvoiceAmt);
				chargeLineVO.setDanAmt(sumDanAmt);
			}else{
				parent = new DefaultTreeNode(
						new ChargeLineVO(adjustmentItem.getFlItem().getCmItem().getExtensionCode(),adjustmentItem.getFlItem().getCmItem().getItemName(),"",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),root);
				child = new DefaultTreeNode(
						new ChargeLineVO("","","",adjustmentItem.getFlItem().getUnitPrice(),adjustmentItem.getFlInvoiceItem().getOrderedAmt(),adjustmentItem.getFlInvoiceItem().getInvAmt(),adjustmentItem.getAdjustedAmt()),parent);
				chargeLineMap.put(adjustmentItem.getFlItem().getCmItem().getItemId(),parent);
			}
		}
		getManagedBean().setDanChargeLineTree(root);
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
		
		if(getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch()!=null && getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId()!=null) {
			getManagedBean().setBranchName(getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getBranchName());
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId());
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
			for (PmFinancialAcc financialAcc : getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getPmFinancialAccs()) {
				if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))) {
					getManagedBean().getAdjustment().getFlInvoice().setAccountNo(financialAcc.getAccountNo());
					getManagedBean().getAdjustment().getFlInvoice().setPaymentBank(financialAcc.getPmPaymentBankOrg());
					break;
				}
	        }
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
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
			for (PmFinancialAcc financialAcc : getManagedBean().getAdjustment().getFlInvoice().getFlFulfilmentOrder().getPmFinancialOrg().getPmFinancialAccs()) {
				if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))) {
					getManagedBean().getAdjustment().getFlInvoice().setAccountNo(financialAcc.getAccountNo());
					getManagedBean().getAdjustment().getFlInvoice().setPaymentBank(financialAcc.getPmPaymentBankOrg());
					break;
				}
	        }
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
}
