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
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
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
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
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
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.type.pm.AddressType;
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
	
	private static Map<Long, PmVotFund> votFundCodeMap = new HashMap<Long, PmVotFund>();
	private static Map<Long, PmGlAccount> glAccountCodeMap = new HashMap<Long, PmGlAccount>();
	private static Map<Long, PmPrgActivity> progmActivityCodeMap = new HashMap<Long, PmPrgActivity>();
	private static Map<Long, PmProject> projectCodeMap = new HashMap<Long, PmProject>();
	private static Map<Long, PmSetia> setiaCodeMap = new HashMap<Long, PmSetia>();
	private static Map<Long, PmSubSetia> subSetiaCodeMap = new HashMap<Long, PmSubSetia>();
	
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
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLPaymentInstructionDataType paymentObj = (FLPaymentInstructionDataType)obj;
			flReqDTO.setFlOrderId(paymentObj.getFlOrderId());
			IInvoiceSO invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);
			IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject("fl",IPaymentMatchSO.class);
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
			getManagedBean().setOutComes(outcome);
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
			paymentDeduction.setPmGlAccount(new PmGlAccount());
			//paymentDeduction.setPmGlAccount(resDTO.getInvoice().getFlRequest().getIssuedOrgProfile().getPmPtjGroup().getPmGlAccounts().get(0));
			paymentAdvice.setFlPaymentDeduction(new ArrayList<FlPaymentDeduction>());
			paymentAdvice.getFlPaymentDeduction().add(paymentDeduction);

			// For PO No Deduction Panel 
			getManagedBean().setDocType(resDTO.getInvoice().getFlRequest().getDocType());
			
			// get address for supplier
			//combineSupAddress(resDTO.getInvoice().getFlRequest());
			
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
			
			getManagedBean().getPaymentAdvice().setStatus(paymentMatchSO.retrievePaymentMatchStatus(flReqDTO).getPmStatus());
			if(getManagedBean().getPaymentAdvice().getFlInvoice().getFlFulfilmentOrder().isFactored()){
				getFactoringBranchDtl(); 
			}
			
		}
		getManagedBean().getPaymentAdvice();
	}
	
	public void getFactoringBranchDtl(){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		Long financialBranchId = getManagedBean().getPaymentAdvice().getFlInvoice().getFlFulfilmentOrder().getPmFinancialBranch().getFinancialBranchId();
		
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialBranchId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(financialBranchId);
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
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId( getManagedBean().getPaymentAdvice().getFlInvoice().getFlFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
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
		}
	}
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			getManagedBean().populateChargeLineData();	
			setClassName("@(.pmChargeLinePnlClass)");
		}
		if(selectedTabIndex==3){
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
		onNodeUnSelect();
		if(null!=selectedNode){
			for(TreeNode children:selectedNode.getChildren()){
				FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
				if(null !=childData.getPmVotFund()){
					String votFundCode = votFundCodeMap.get(childData.getPmVotFund().getVotFundId()).getVotFundCode();
					getManagedBean().setVotFundCode(votFundCode);
				}
				
				if(childData.getAssetInd()!=null) {
					getManagedBean().setAssetIndCode(childData.getAssetInd().getParameterCode());
				}
					
				if(childData.getPmGlAccount()!=null) {
					String glAccountCode = glAccountCodeMap.get(childData.getPmGlAccount().getGlAccountId()).getGlAccCode();
					getManagedBean().setGlAccountCode(glAccountCode);
				}
				
				if(childData.getPmPrgActivity()!=null) {
					if(progmActivityCodeMap.size()==0) {
						IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProgramActivitySO.class);
						ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
						reqDTO.setVotFundId(childData.getPmVotFund().getVotFundId());
						ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
						
						for (PmPrgActivity pmPrgActivity : resDTO.getPrgActivityList()) {
							progmActivityCodeMap.put(pmPrgActivity.getPrgActivityId(), pmPrgActivity);
						}
					}
					String progActvCode = progmActivityCodeMap.get(childData.getPmPrgActivity().getPrgActivityId()).getPrgActivityCode();
					getManagedBean().setPrgActivityCode(progActvCode);
				}
				
				if(childData.getPmSubSetia()!=null) {
					if(subSetiaCodeMap.size()==0) {
						IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IProjectSetiaSO.class);
						ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
						reqDTO.setSetiaId(childData.getPmSubSetia().getPmSetia().getSetiaId());
						ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
						for (PmSubSetia pmSubSetia : resDTO.getSubSetiaList()) {
							subSetiaCodeMap.put(pmSubSetia.getSubSetiaId(), pmSubSetia);
						}						
					}
					String subSetiaCode = subSetiaCodeMap.get(childData.getPmSubSetia().getSubSetiaId()).getSubSetiaCode();
					getManagedBean().setSubSetiaCode((subSetiaCode));
					
					if(childData.getPmSubSetia().getPmSetia()!=null) {
						//String setiaCode = setiaCodeMap.get(childData.getPmSubSetia().getPmSetia().getSetiaId()).getSetiaCode();
						getManagedBean().setSetiaCode(childData.getPmSubSetia().getPmSetia().getSetiaCode());
						
						if(childData.getPmSubSetia().getPmSetia().getPmProject()!=null) {
//							String projectCode = projectCodeMap.get(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId()).getProjectCode();
							getManagedBean().setProjectCode(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectCode());
						}
					}
					getManagedBean().setFinancingTypeCode(subSetiaCodeMap.get(childData.getPmSubSetia().getSubSetiaId()).getFinancingType().getParameterCode());
				}
			}
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
	/*//get chargeline code by ID
		
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
	}*/
	
	
	public FulfilmentResDTO getPaymentAdviseByPaymentAdviseId(Long paymentAdviseId){
		IPaymentMatchSO paymentMatchSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IPaymentMatchSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPaymentAdviseId(paymentAdviseId);
		FulfilmentResDTO resDTO = paymentMatchSO.retrievePaymentAdviseByPaymentId(reqDTO);
		resDTO.getPaymentAdvice();//this is the payment advise object
		return resDTO;
	}
	/**
	 * @return the votFundCodeMap
	 */
	public static Map<Long, PmVotFund> getVotFundCodeMap() {
		return votFundCodeMap;
	}
	/**
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, PmVotFund> votFundCodeMap) {
		PaymentMatchBackingBean.votFundCodeMap = votFundCodeMap;
	}
	/**
	 * @return the glAccountCodeMap
	 */
	public static Map<Long, PmGlAccount> getGlAccountCodeMap() {
		return glAccountCodeMap;
	}
	/**
	 * @param glAccountCodeMap the glAccountCodeMap to set
	 */
	public static void setGlAccountCodeMap(Map<Long, PmGlAccount> glAccountCodeMap) {
		PaymentMatchBackingBean.glAccountCodeMap = glAccountCodeMap;
	}
	/**
	 * @return the progmActivityCodeMap
	 */
	public static Map<Long, PmPrgActivity> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}
	/**
	 * @param progmActivityCodeMap the progmActivityCodeMap to set
	 */
	public static void setProgmActivityCodeMap(
			Map<Long, PmPrgActivity> progmActivityCodeMap) {
		PaymentMatchBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}
	/**
	 * @return the projectCodeMap
	 */
	public static Map<Long, PmProject> getProjectCodeMap() {
		return projectCodeMap;
	}
	/**
	 * @param projectCodeMap the projectCodeMap to set
	 */
	public static void setProjectCodeMap(Map<Long, PmProject> projectCodeMap) {
		PaymentMatchBackingBean.projectCodeMap = projectCodeMap;
	}
	/**
	 * @return the setiaCodeMap
	 */
	public static Map<Long, PmSetia> getSetiaCodeMap() {
		return setiaCodeMap;
	}
	/**
	 * @param setiaCodeMap the setiaCodeMap to set
	 */
	public static void setSetiaCodeMap(Map<Long, PmSetia> setiaCodeMap) {
		PaymentMatchBackingBean.setiaCodeMap = setiaCodeMap;
	}
	/**
	 * @return the subSetiaCodeMap
	 */
	public static Map<Long, PmSubSetia> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}
	/**
	 * @param subSetiaCodeMap the subSetiaCodeMap to set
	 */
	public static void setSubSetiaCodeMap(Map<Long, PmSubSetia> subSetiaCodeMap) {
		PaymentMatchBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}
}
