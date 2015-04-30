/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
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
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Debit Adjustment Note.
 *
 * @author KK LIM
 * @since 1/11/2013
 */
@ManagedBean(name = "danBackingBean")
@RequestScoped
public class DebitAdjustmentNoteBackingBean extends BaseBackingBean<DebitAdjustmentNoteManagedBean> {
	
	/** The log. */
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteBackingBean.class);
	
	/** The Constant NAV_ORDERNO. */
	private static final String NAV_ORDERNO = "/xhtml/fl/shared/hyperlinkView/orderNoPage/orderNoHyperlink.xhtml";
	
	/** The Constant NAV_INVOICE. */
	private static final String NAV_INVOICE = "/xhtml/fl/shared/hyperlinkView/invoicePage/invoiceHyperlink.xhtml";

	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		return new FacesScopes();
	}
	
	/**
	 * save,submit the Debit Adjustment Note
	 * 
	 *  @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 */
	public String submitAction(String action) throws ParseException {
		log.debug("DebitAdjustmentNoteBackingBean.submitAction()");
		IAdjustmentSO adjustmentSO = null;
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlAdjustment adjustment = getManagedBean().getAdjustment();
			adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			reqDTO.setAdjustment(adjustment);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setToken(token);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
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
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(ReportConst.PARAM_DOC_NO, getManagedBean().getAdjustment().getDocNo());
					paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
					CommonBackingBean commonBackingBean = new CommonBackingBean();
					commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_DEBIT_NOTE);
					commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.dan",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
					commonBackingBean.getManagedBean().setParamMap(paramMap);
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
				}		
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
	
	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		if(taskId != null){
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			FLDanDataType danDataType = (FLDanDataType)obj;
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
			if(danDataType.getErrorId() != null && danDataType.getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(danDataType.getErrorId());
				FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
		}
	}
	
	/**
	 * set adjustment note attribute into managedbean
	 *
	 * @param resDTO the new adjustment note
	 */
	public void setAdjustmentNote(FulfilmentResDTO resDTO) {
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
		if(!resDTO.isAdjLink()){
			flBB.initializeRemarkAndAttachement(adjustment.getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
		}
	}
	
	/**
	 * Calculate total order amount,total invoice amount and total dan amount .
	 *
	 * @param adjustment the adjustment
	 */
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
	
	/**
	 * Gets the factoring branch details based on financial org id and address type.
	 *
	 * @return the factoring branch dtl
	 */
	public void getFactoringBranchDtl(){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setBranchName(null);
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		//reset back to HQ info
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
	
	/**
	 * call this method as user select charge line row
	 * 
	 */
	public void selectDanItem() {
		if(getManagedBean().getSelectedDanItem()!=null) {
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
				
				if( getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia()!=null) {
					getManagedBean().setSetiaCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getSetiaCode());
					getManagedBean().setProjectCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getProjectCode());
				}
				getManagedBean().setFinancingTypeCode(getManagedBean().getSelectedDanItem().getFlItem().getPmSubSetia().getFinancingType().getParameterCode());
			}
		}
	}
	
	/**
	 * On node un select.
	 */
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
	
	/**
	 * Retrieve mof no based on supplier id.
	 *
	 * @param smSupplier the sm supplier
	 * @return the string
	 */
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
	 * Page navigation to FLDoc search page in DAN.
	 *
	 * @return the string
	 */
	public String backToPervious() {
		return "flDocSearch";
	}
	
	/**
	 * Back to document.
	 *
	 * @return the string
	 */
	public String backToDocument(){
		return getManagedBean().getBackToDocument();
		
	}
	
	/**
	 * Navigate the page based on doc type.
	 *
	 * @param docType the doc type
	 * @return the string
	 */
	public String navigateDocument(String docType){
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(getManagedBean().getAdjustment().getFlOrder().getFulfilmentOrderId());
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);

			FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
			orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			FacesContext fc = FacesContext.getCurrentInstance();
			orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());

			return NAV_ORDERNO;
		}else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_INVOICE)){
			IInvoiceSO invoiceSO = (IInvoiceSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IInvoiceSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setInvoiceId(getManagedBean().getAdjustment().getFlInvoice().getInvoiceId());
			reqDTO.setDocType(docType);
			reqDTO.setInvoiceLink(true);
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			FacesContext fc = FacesContext.getCurrentInstance();
			invBB.getManagedBean().setBackToDocument(fc.getViewRoot().getViewId());

			return NAV_INVOICE;
		}
		return null;
	}
	
	/**
	 * sorting all the items showing in Item menu
	 *
	 * @param flFulfilmentNoteItemList the Fl Fulfilment Note Item list
	 */
	public void sortItems(List<FlAdjustmentItem> flAdjustmentItem) {
		//Creating Comparator
		if(null==flAdjustmentItem || flAdjustmentItem.isEmpty()){
			return;
		}
		  Comparator<FlAdjustmentItem> comparator = new Comparator<FlAdjustmentItem>() {
	            @Override
	            public int compare(FlAdjustmentItem o1, FlAdjustmentItem o2) {
	            	String object1Code = o1.getFlItem().getItemCode();
	            	String object2Code = o2.getFlItem().getItemCode();
	                return new CompareToBuilder()
		                .append(object1Code, object2Code)
		                .toComparison();
	            }
	        };
			Collections.sort(flAdjustmentItem, comparator);
    }
	
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			sortItems(getManagedBean().getAdjustment().getFlAdjustmentItem());
		}
		
	}
	/**
	 * Gets the user role based on role code.
	 *
	 * @return the user role
	 */
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())) 
			return RoleCode.FL_APPROVER.value();
		else 
			return null;
	}
	
	/**
	 * Show 1GFMAS error
	 *
	 */
	public void show1GFMASError(){
		getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().update("@form");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,null,getManagedBean().getGfmasErrorContent()));
	}
}
