/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.dan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.fl.adjust.FlAdjustment;
import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.dan.DebitAdjustmentNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdjustmentItemNodeVO;

// TODO: Auto-generated Javadoc
/**
 * Managed Bean for Debit Adjustment Note.
 *
 * @author KK LIM
 * @since 1/11/2013
 */
@ManagedBean(name = "danBackingBean")
@RequestScoped
public class DebitAdjustmentNoteBackingBean extends FulfilmentBaseBackingBean<DebitAdjustmentNoteManagedBean> {
	
	/** The log. */
	private static Logger log = Logger.getLogger(DebitAdjustmentNoteBackingBean.class);
	
	/** The Constant NAV_ORDERNO. */
	private static final String NAV_ORDERNO = "/xhtml/fl/purchaseorder/poBasePage.xhtml";
	
	/** The Constant NAV_INVOICE. */
	private static final String NAV_INVOICE = "/xhtml/fl/invoice/invBasePage.xhtml";
	
	
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
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();
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
			if(getManagedBean().getPmDigiSign()!=null) {
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			
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
			FLDanDataType danObj = super.getBpmTaskDetails(FLDanDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});

			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			flReqDTO.setDanId(danObj.getDanId());
			IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
			setAdjustmentNote(resDTO);
			getManagedBean().setSelectedTabIndex(0);
			if(danObj.getErrorId() != null && danObj.getErrorId() > 0){
				getManagedBean().setAutoUpdate(Boolean.FALSE);
				getManagedBean().setGfmasError(true);
				FulfilmentReqDTO errReq = new FulfilmentReqDTO();
				errReq.setErrorId(danObj.getErrorId());
				FulfilmentResDTO errRes = fulfilmentSO.retrieveErrorContent(errReq);
				getManagedBean().setGfmasErrorContent(errRes.getFlErrorContent());
			}
		}else{
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(null == getManagedBean().getButtonVal() || !getManagedBean().getButtonVal().equalsIgnoreCase("View")){
				flBB.getManagedBean().setHideRemarks(false);
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
			statusReqDTO.setStatusId(FulfilmentStatusConst.DN_PENDING_APPROVAL);
			getManagedBean().getAdjustment().setStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
		}
//		if(getManagedBean().getAdjustment().getFlOrder().isFactored()){
//			getFactoringBranchDtl();
//		}else{
			getManagedBean().setAccountNoName(getManagedBean().getAdjustment().getFlInvoice().getAccountNo()+"  "+getManagedBean().getAdjustment().getFlInvoice().getPaymentBank().getFinOrgName());
//		}
		
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
			sumOrderedAmt =sumOrderedAmt.add(adjustmentItem.getFlInvoiceItemSummary().getOrderedAmt());
			sumInvoiceAmt = sumInvoiceAmt.add(adjustmentItem.getFlInvoiceItemSummary().getInvAmt());
			sumDanAmt = sumDanAmt.add(adjustmentItem.getFlInvoiceItemSummary().getInvAmt().subtract(adjustmentItem.getFlInvoiceItemSummary().getOrderedAmt()));
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
//	public void getFactoringBranchDtl(){
//		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
//		getManagedBean().setBranchName(null);
//		getManagedBean().setSupplierAddress(null);
//		getManagedBean().setPhoneNo(null);
//		getManagedBean().setFaxNo(null);
//		getManagedBean().setContactPerson(null);
//		getManagedBean().setContactEmail(null);
//		getManagedBean().setContactPhoneNo(null);
//		
//		//reset back to HQ info
//		AddressReqDTO reqDTO = new AddressReqDTO();
//		reqDTO.setFinancialOrgId(getManagedBean().getAdjustment().getFlOrder().getPmFinancialOrg().getFinancialOrgId());
//		reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
//		AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
//		if(!resDTO.getAddressTypeList().isEmpty()){
//			getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
//			getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
//			getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
//			if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
//				PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
//				getManagedBean().setContactPerson(pmContact.getContactName());
//				getManagedBean().setContactEmail(pmContact.getEmail());
//				getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
//			}
//		}
//		getManagedBean().setAccountNoName(getManagedBean().getAdjustment().getFlInvoice().getAccountNo()+"  "+getManagedBean().getAdjustment().getFlInvoice().getPaymentBank().getFinOrgName());
//
//	}
	
	/**
	 * call this method as user select charge line row
	 * 
	 */
	public void selectDanItem() {
		onNodeUnSelect();
		if(getManagedBean().getSelectedNode()!=null) {
			AdjustmentItemNodeVO adjustmentItemNodeVO = (AdjustmentItemNodeVO) getManagedBean().getSelectedNode().getData();
			if(adjustmentItemNodeVO.getFlAdjustmentItem()!=null) {
				if(null !=adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmVotFund()){
					getManagedBean().setVotFundCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmVotFund().getVotFundCode());
					getManagedBean().setVotFundType(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmVotFund().getVotFundType());
				}
				if(null != adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getAssetInd()) {
					getManagedBean().setAssetIndCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getAssetInd().getParameterCode());
				}
				if(null != adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmGlAccount()) {
					getManagedBean().setGlAccountCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmGlAccount().getGlAccCode());
				}
				if(null != adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmPrgActivity()) {
					getManagedBean().setPrgActivityCode( adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmPrgActivity().getPrgActivityCode());
				}
				if(null != adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia()) {
					getManagedBean().setSubSetiaCode((adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia().getSubSetiaCode()));
					
					if( adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia()!=null) {
						getManagedBean().setSetiaCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia().getSetiaCode());
						getManagedBean().setProjectCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia().getProjectCode());
					}
					getManagedBean().setFinancingTypeCode(adjustmentItemNodeVO.getFlAdjustmentItem().getFlItem().getPmSubSetia().getFinancingType().getParameterCode());
				}
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
	 * Page navigation to FLDoc search page in DAN.
	 *
	 * @return the string
	 */
	public String backToPervious() {
		if(getManagedBean().getBackToPrevious() !=null){
			return getManagedBean().getBackToPrevious();
		}
		else{
			return "flDocSearch";
		}
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
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setFlOrderId(getManagedBean().getAdjustment().getFlOrder().getFulfilmentOrderId());
			reqDTO.setDocType(docType);
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);

			FulfilmentOrderBackingBean orderBackingBean = new FulfilmentOrderBackingBean();
			orderBackingBean.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			orderBackingBean.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			orderBackingBean.getManagedBean().setHyperLinkPage(true);
			orderBackingBean.getManagedBean().setButtonVal("View");
			FacesContext fc = FacesContext.getCurrentInstance();
			orderBackingBean.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			flBB.getManagedBean().setHideRemarks(true);
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
			invBB.getManagedBean().setHyperLinkPage(true);
			invBB.getManagedBean().setButtonVal("View");
			invBB.getManagedBean().setDocMaintain(false);
			FacesContext fc = FacesContext.getCurrentInstance();
			invBB.getManagedBean().setBackToPrevious(fc.getViewRoot().getViewId());
			flBB.getManagedBean().setHideRemarks(true);
			invBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					invBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}
			
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
	
	/**
	 * selected tab index ==1 
	 * populate chargeline tree data
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			sortItems(getManagedBean().getAdjustment().getFlAdjustmentItem());
			populateChargeLineData();
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
	
	/**
	 * Retrieve adjustment information by danId and display it in UI 
	 *
	 */
	public void viewDebitAdjustment() {
		IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		
		String danId = originalServletRequest.getParameter("danId");
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		if(danId!=null) {
			reqDTO.setDanId(Long.valueOf(danId));
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			reqDTO.setPoLink(Boolean.TRUE);
			renewSesionObject();
			try {
				resDTO = adjustmentSO.retrieveAdjustmentById(reqDTO);
			} catch (EpException e) {
				handleException(e);
				return;
			}
			getManagedBean().setSelectedTabIndex(0);
			setAdjustmentNote(resDTO);
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setTaskId(BpmOutcomeConst.BPM_VIEW);//for the purpose of hide the back button.
			flBB.getManagedBean().setHideRemarks(true);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "danBasePage?faces-redirect=true");
		}
	}
	
	private void populateChargeLineData() {
		getManagedBean().setDanChargeLineTree(new DefaultTreeNode("root", null));
		
		AdjustmentItemNodeVO adjustmentItemNodeVO = null;
		TreeNode childItemNode = null;
		for(FlAdjustmentItem adjustmentItem : getManagedBean().getAdjustment().getFlAdjustmentItem()) {
			adjustmentItemNodeVO = new AdjustmentItemNodeVO();
			adjustmentItemNodeVO.setLevel(1);
			adjustmentItemNodeVO.setFlAdjustmentItem(adjustmentItem);
			childItemNode = new DefaultTreeNode(adjustmentItem.getAdjustmentItemId().toString(), adjustmentItemNodeVO, getManagedBean().getDanChargeLineTree());
			
			for(FlInvoiceItem invoiceItem : adjustmentItem.getFlInvoiceItemSummary().getFlInvoiceItem()) {
				adjustmentItemNodeVO = new AdjustmentItemNodeVO();
				adjustmentItemNodeVO.setLevel(2);
				adjustmentItemNodeVO.setFlInvoiceItem(invoiceItem);
				new DefaultTreeNode(invoiceItem.getInvoiceItemId().toString(), adjustmentItemNodeVO, childItemNode);
			}
		}
	}
	
	/**
	 * Enable auto update.
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Pre submit action.
	 *
	 * @param pmDigiSign the pm digi sign
	 * @return the string
	 * @throws Exception the exception
	 */
	public String preSubmitAction(
			PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		getManagedBean().setValidated(Boolean.TRUE);
		log.info("######## FRN SLA start : "+System.currentTimeMillis()+" : "+new Date());
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Gpki sign action.
	 *
	 * @param outcome the outcome
	 */
	public String gpkiSignAction(String outcome){
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.PAY_OFFICER.toString());
			userReqDTO.setOrgProfileId(getManagedBean().getAdjustment().getFlRequest().getPreparedOrgProfile().getOrgProfileId());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
			if(userResDTO.getUserList() == null || userResDTO.getUserList().isEmpty()){
				throw new EpException(MessageConst.ERROR_740020, new String[]{ResourceBundleUtils.getMessage("lbl.txt.paymentMatchOfficer",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
			}
			// Temp setTextBeforeSign : DocNo+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getAdjustment().getFlOrder().getDocNo()+"|");
			sb.append(getManagedBean().getAdjustment().getDocNo()+"|");
			sb.append(outcome+"|");
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
			getManagedBean().setTextBeforeSign(sb.toString());			
			//validateFulfilmentNote(outcome);
			// set action
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			getManagedBean().setButtonAction(outcome);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}
		return null; 
		
	}
}
