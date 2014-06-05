/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.paymentmatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlPaymentAdvice;
import my.ep.persistence.entity.fl.payment.FlPaymentDeduction;
import my.ep.persistence.entity.fl.payment.FlPaymentItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.paymentmatch.PaymentMatchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Payment Match Backing Bean
 * @author KK LIM
 * @since 30/10/2013
 *
 */
@ManagedBean(name = "paymentMatchBackingBean")
@RequestScoped
public class PaymentMatchBackingBean extends BaseBackingBean<PaymentMatchManagedBean>{

	private static Logger log = Logger.getLogger(PaymentMatchBackingBean.class);
	private String className="@(.myPmClassName)";
	private static Map<Long, String> votFundCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> progmActivityCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> projectCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> setiaCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> subSetiaCodeMap = new HashMap<Long, String>();
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	@PostConstruct
	public void init() {
		if (getManagedBean().getFulfilmentRequest() == null) {
			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
			getManagedBean().getFulfilmentRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
		}
		//getManagedBean().populateChargeLineData();
	}
	

	public String submitAction(String action){
		IPaymentMatchSO paymentSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlPaymentAdvice paymentAdvice = getManagedBean().getPaymentAdvice();
			
			if(null==getManagedBean().getChargeLineItemRoot()){
				getManagedBean().populateChargeLineData();
			}
			TreeNode root= getManagedBean().getChargeLineItemRoot();
			List<FlPaymentItem> paymentItemList=new ArrayList<FlPaymentItem>();
			   for(TreeNode childNode:root.getChildren()){
				   FlPaymentItem paymentItem=new FlPaymentItem();
				  
				   FlFulfilmentItem flItem=null;
				   for(TreeNode subNode:childNode.getChildren()){
					   FlFulfilmentItemAddr subData=(FlFulfilmentItemAddr)subNode.getData();
					   flItem=subData.getFlFulfilmentItem();
					  
				   }
				   paymentItem.setFlPaymentAdvice(paymentAdvice);
				   paymentItem.setFlItem(flItem);
				   
				   for(FlInvoiceItem invItems :paymentAdvice.getFlInvoice().getFlInvoiceItem()){
					   paymentItem.setFlInvoiceItem(invItems);
					   
				   }
				   paymentItemList.add(paymentItem);
			   }
			  
			getManagedBean().getPaymentAdvice().setFlPaymentItems(paymentItemList);  
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject("fl",IPaymentMatchSO.class);
			reqDTO.setPaymentAdvice(paymentAdvice);
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				action = "";
			}
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
//			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			System.out.println("action: "+action);
			FulfilmentResDTO resDTO = paymentSO.createPaymentMatch(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700026;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), getManagedBean().getPaymentAdvice().getPaymentAdviceNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}	
			else {
				throw new EpException("Failed.");
			}
			
			
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}finally {
			paymentSO = null;
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
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
		IVotFundSO votFundSO = (IVotFundSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IVotFundSO.class);
		IGlAccountSO glAccountSO = (IGlAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IGlAccountSO.class);
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLPaymentInstructionDataType paymentObj = (FLPaymentInstructionDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFlOrderId());
			IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);
			resDTO = invoiceSO.retrieveInvoiceByFlOrderId(flReqDTO);
			FlPaymentAdvice paymentAdvice = new FlPaymentAdvice();
			paymentAdvice.setFlInvoice(resDTO.getInvoice());
			paymentAdvice.setFeeAmt(resDTO.getInvoice().getFeeAmt());
			paymentAdvice.setFlRequest(resDTO.getInvoice().getFlRequest());
			paymentAdvice.setFulfilmentOrder(resDTO.getInvoice().getFlFulfilmentOrder());
			System.out.println("fulfilment req id::: "+paymentObj.getFlRequestId());
			// Get Remark and Attachment
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

			getManagedBean().setPaymentAdvice(paymentAdvice);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
			parameterReqDTO.setParameterCode(ParameterConst.TRANSACTION_FEE);
			ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			
			VotFundReqDTO votFundReqDTO = new VotFundReqDTO();//TODO supposely pm should have a transaction fee vot fund and GOSODO
			votFundReqDTO.setVotFundId(125L);
			VotFundResDTO votFundResDTO = votFundSO.retrieveVotFundByFundId(votFundReqDTO);
			
			FlPaymentDeduction paymentDeduction = new FlPaymentDeduction();
			paymentDeduction.setFlPaymentAdvice(paymentAdvice);
			paymentDeduction.setDeductionType(parameterResDTO.getPmParameter());
			paymentDeduction.setDeductionAmt(paymentAdvice.getFeeAmt());
			paymentDeduction.setDeductionDesc(" ");
			paymentDeduction.setPmVotFund(votFundResDTO.getPmVotFund());
			paymentDeduction.setPmGlAccount(resDTO.getInvoice().getFlRequest().getIssuedOrgProfile().getPmPtjGroup().getPmGlAccounts().get(0));
			paymentAdvice.setFlPaymentDeduction(new ArrayList<FlPaymentDeduction>());
			paymentAdvice.getFlPaymentDeduction().add(paymentDeduction);

			// For PO No Deduction Panel 
			getManagedBean().setDocType(resDTO.getInvoice().getFlRequest().getDocType());
			
			// get address for supplier
			combineSupAddress(resDTO.getInvoice().getFlRequest());
			
			getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			// pm summary
			BigDecimal sampleDeductionAmt = new BigDecimal(50);
			BigDecimal calPaymentAmt = new BigDecimal(0.0);
			getManagedBean().getPaymentAdvice().getFlInvoice().setInvoiceAmtNew(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew());
			getManagedBean().getPaymentAdvice().setFeeAmt(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt());
			getManagedBean().setDeductionAmt(sampleDeductionAmt);
			calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt().add(sampleDeductionAmt));
			BigDecimal deductPaymentAmt = new BigDecimal(0.0);
			deductPaymentAmt=getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(calPaymentAmt);
			getManagedBean().getPaymentAdvice().setPaymentAmt(deductPaymentAmt);
			
		}
		getManagedBean().getPaymentAdvice();
	}
	
	public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    StringBuilder address = new StringBuilder();
	    String strSpace = " ";
	    if(null != fulfilmentRequest.getSupplierBranch()){
	    	if(null != fulfilmentRequest.getSupplierBranch().getSmAddress()){
	    		if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1() ){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    	    	address.append(strSpace);
	    	    	
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    	    	address.append(strSpace);
	    	    	
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() ){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    	    	address.append(strSpace);
	    	    	
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    	    	address.append(strSpace);
	    	    		
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    	    	address.append(strSpace);
	    	    	
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    	    	address.append(strSpace);
	    	    	
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    	    	address.append("\n");
	    	    }if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode()){
	    	    	address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	    }
	    	    if(null != address.toString()){
	    	    	System.out.println("addresss     "+address.toString());
	    	    }
	    	}
	    }	
	    	
	    	this.getManagedBean().setSupplierAddress(address.toString());
	}
	 
	public void onTabChange(TabChangeEvent tevent){
		if("pmChargeLineTabId".equals(tevent.getTab().getId())){
			getManagedBean().populateChargeLineData();	
			setClassName("@(.pmChargeLinePnlClass)");
		}
		if("pmSummaryTabId".equals(tevent.getTab().getId())){
			calPmSummary();
			setClassName("@(.pmSummaryTabId)");
		}
	}
	public void calPmSummary(){
		BigDecimal sampleDeductionAmt = new BigDecimal(50);
		BigDecimal calPaymentAmt = new BigDecimal(0.0);
		getManagedBean().getPaymentAdvice().getFlInvoice().setInvoiceAmtNew(getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew());
		getManagedBean().getPaymentAdvice().setFeeAmt(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt());
		getManagedBean().setDeductionAmt(sampleDeductionAmt);
		calPaymentAmt=calPaymentAmt.add(getManagedBean().getPaymentAdvice().getFlInvoice().getFeeAmt().add(sampleDeductionAmt));
		BigDecimal deductPaymentAmt = new BigDecimal(0.0);
		deductPaymentAmt=getManagedBean().getPaymentAdvice().getFlInvoice().getInvoiceAmtNew().subtract(calPaymentAmt);
		getManagedBean().getPaymentAdvice().setPaymentAmt(deductPaymentAmt);
		
	} 
	public void onNodeSelect() {
		TreeNode selectedNode=getManagedBean().getSelectedNode();
			if(null!=selectedNode){
				for(TreeNode children:selectedNode.getChildren()){
					FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
						if(null !=childData.getPmVotFund()){
							//do it for others
							String votFundCode=getVotFundCodeById(childData.getPmVotFund().getVotFundId());
							getManagedBean().setVotFundCode(votFundCode);
							String progActvCode=getprogActivityCodeById(childData.getPmPrgActivity().getPrgActivityId());
							getManagedBean().setPrgActivityCode(progActvCode);
							String projectCode=getProjectCodeById(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId());
							getManagedBean().setProjectCode(projectCode);
							String setiaCode=getSetiaCodeById((childData.getPmSubSetia().getPmSetia().getSetiaId()));
							getManagedBean().setSetiaCode(setiaCode);
							String subSetiaCode=getSubSetiaCodeById((childData.getPmSubSetia().getSubSetiaId()));
							getManagedBean().setSubSetiaCode((subSetiaCode));
								
						}else{
							getManagedBean().setVotFundCode("");
							getManagedBean().setPrgActivityCode("");
							getManagedBean().setProjectCode("");
							getManagedBean().setSetiaCode("");
							getManagedBean().setSubSetiaCode((""));
								
						}
							
							
					}
			}
		}
	//get chargeline code by ID
		
	public String getVotFundCodeById(Long votFundId) {
		return votFundCodeMap.get(votFundId);
	}
	public String getprogActivityCodeById(Long progActvId) {
		return progmActivityCodeMap.get(progActvId);
	}
	public String getProjectCodeById(Long projectId) {
		return projectCodeMap.get(projectId);
	}
	public String getSetiaCodeById(Long setiaId) {
		return setiaCodeMap.get(setiaId);
	}
	public String getSubSetiaCodeById(Long subSetiaId) {
		return subSetiaCodeMap.get(subSetiaId);
	}
		/**
		 * @return the votFundCodeMap
		 */
	public static Map<Long, String> getVotFundCodeMap() {
		return votFundCodeMap;
	}
		/**
		 * @param votFundCodeMap the votFundCodeMap to set
		 */
	public static void setVotFundCodeMap(Map<Long, String> votFundCodeMap) {
		PaymentMatchBackingBean.votFundCodeMap = votFundCodeMap;
	}
		/**
		 * @return the progmActivityCodeMap
		 */
	public static Map<Long, String> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}
		/**
		 * @param progmActivityCodeMap the progmActivityCodeMap to set
		 */
	public static void setProgmActivityCodeMap(
		Map<Long, String> progmActivityCodeMap) {
		PaymentMatchBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}
		/**
		 * @return the projectCodeMap
		 */
	public static Map<Long, String> getProjectCodeMap() {
		return projectCodeMap;
	}
		/**
		 * @param projectCodeMap the projectCodeMap to set
		 */
	public static void setProjectCodeMap(Map<Long, String> projectCodeMap) {
		PaymentMatchBackingBean.projectCodeMap = projectCodeMap;
	}
		/**
		 * @return the setiaCodeMap
		 */
	public static Map<Long, String> getSetiaCodeMap() {
			return setiaCodeMap;
	}
		/**
		 * @param setiaCodeMap the setiaCodeMap to set
		 */
	public static void setSetiaCodeMap(Map<Long, String> setiaCodeMap) {
		PaymentMatchBackingBean.setiaCodeMap = setiaCodeMap;
	}
		/**
		 * @return the subSetiaCodeMap
		 */
	public static Map<Long, String> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}
		/**
		 * @param subSetiaCodeMap the subSetiaCodeMap to set
		 */
	public static void setSubSetiaCodeMap(Map<Long, String> subSetiaCodeMap) {
		PaymentMatchBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
}
