package my.ep.web.backingbean.fl.relieforder;

import java.util.ArrayList;
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

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.order.IReliefOrderSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.common.order.FLOrderDataType;
import my.ep.persistence.entity.fl.order.FlReliefOrder;
import my.ep.persistence.entity.fl.order.FlReliefOrderItem;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.GSFType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.common.CommonBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.relieforder.ReliefOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

// TODO: Auto-generated Javadoc
/**
 * Backing Bean for Relief Order.
 *
 * @author Giribabu
 * @since 06/08/2015
 */
@ManagedBean(name="reliefOrderBB")
@RequestScoped
public class ReliefOrderBackingBean extends BaseBackingBean<ReliefOrderManagedBean>{

	private static Logger log = Logger.getLogger(ReliefOrderBackingBean.class);
	
	FulfilmentBackingBean flBB = new FulfilmentBackingBean();
	/**
	 * Gets the faces scopes.
	 *
	 * @return the faces scopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * Instantiates a new Relief Order BackingBean .
	 */
	public ReliefOrderBackingBean(){
	}
	
	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		IReliefOrderSO reliwfOrderSo = (IReliefOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReliefOrderSO.class);
		
		FulfilmentReqDTO fulfilmentReqDTO = new FulfilmentReqDTO();
		if(taskId != null){

			FLOrderDataType flOrderObj = super.getBpmTaskDetails(FLOrderDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			
			fulfilmentReqDTO.setDocType(ParameterConst.DOCTYPE_RELIEF_ORDER);
			fulfilmentReqDTO.setFlOrderId(flOrderObj.getOrderId());
			
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reliwfOrderSo = (IReliefOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReliefOrderSO.class);
			Long fulfilmentOrderId=flOrderObj.getOrderId();
			fulfilmentReqDTO.setFulfilmentOrderId(fulfilmentOrderId);
			fulfilmentReqDTO.setFlOrderId(flOrderObj.getOrderId());
			FulfilmentResDTO rsResDto = reliwfOrderSo.retrieveReliefOrderByOrderId(fulfilmentReqDTO);
			getManagedBean().setFlReliefOrder(rsResDto.getReliefOrder());
			FulfilmentResDTO orderResDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(fulfilmentReqDTO);
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setFlRequest(orderResDTO.getFlOrder().getFlFulfilmentRequest());
			getManagedBean().setFulfilmentOrder(orderResDTO.getFlOrder());
			if(orderResDTO.getFlOrder().isFactored()) {
				getManagedBean().setFactoringCheck("true");
			} else {
				getManagedBean().setFactoringCheck("false");
			}
			getManagedBean().setFlReliefOrder(rsResDto.getReliefOrder());
			getManagedBean().setNonReliefItems(new ArrayList<FlReliefOrderItem>());
			getManagedBean().setReliefItems(new ArrayList<FlReliefOrderItem>());
			getManagedBean().setNonReliefMenuDisplay(false);
			for(FlReliefOrderItem  flReliefOrderItem : getManagedBean().getFlReliefOrder().getFlReliefOrderItems()){
				if(!flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					getManagedBean().setNonReliefMenuDisplay(true);
					getManagedBean().getNonReliefItems().add(flReliefOrderItem);
				}else{
					getManagedBean().getReliefItems().add(flReliefOrderItem);
				}
			}
			sortReliefOrderItems(getManagedBean().getNonReliefItems());
			sortReliefOrderItems(getManagedBean().getReliefItems());
			
			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(getManagedBean().getFlRequest().getCtContract() != null){
				if(getManagedBean().getFlRequest().getCtContract().isFactored()){
					Long supplieContractId=orderResDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractId();
					contractSupplierReqDTO.setContractId(supplieContractId);
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						orderResDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							contractSupplierResDTO.getFactoring().getFinancialOrg().getFinOrgName();
							getCrSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
						}
					}else{
						getManagedBean().getFlRequest().getCtContract().setFactored(false);
					}
				}
			}
			
			
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(null, rsResDto.getRemarkList(), rsResDto.getAttachmentList());
			
		}
	}
	
	/**
	 * save,approve the relief order
	 * 
	 * * @param String action 
	 *  <p>approve - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 */
	public String submitAction(String action) throws Exception {
		FulfilmentReqDTO reliefOrderReqDTO = new FulfilmentReqDTO();
		IReliefOrderSO reliwfOrderSo = null;
		//saveReliefOrder
		Object token = getFacesScopes().getBPMAuthenticationCtxToken();
		//FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reliefOrderReqDTO.setTaskId(this.getManagedBean().getTaskId());
		reliefOrderReqDTO.setOutcome(action);
		reliefOrderReqDTO.setToken(token);
		reliefOrderReqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
		if(!flBB.getManagedBean().getRemarkList().isEmpty()){
			reliefOrderReqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
		}
		FlReliefOrder reliefOrder=getManagedBean().getFlReliefOrder();
		
		reliefOrderReqDTO.setReliefOrder(reliefOrder);
		reliefOrderReqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		reliefOrderReqDTO.setUserRole(getUserRole());
		reliefOrderReqDTO.setTaskName(getManagedBean().getTaskName());
		reliwfOrderSo = (IReliefOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReliefOrderSO.class);
		FulfilmentResDTO resDTO =reliwfOrderSo.saveReliefOrder(reliefOrderReqDTO);
		if (resDTO.isSuccessful()) {
			log.info("Record saved successfully");
			String messageId = MessageConst.INFO_700005;
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
				messageId = MessageConst.INFO_700004;
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				messageId = MessageConst.INFO_700019;
			} 
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(messageId, 
							new String[]{ResourceBundleUtils.getMessage("lbl.fl.cogstr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), reliefOrder.getReliefOrderNo()}, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ReportConst.PARAM_RELIEF_ORDER_DOC_NO, resDTO.getFlReliefOrder().getReliefOrderNo());
				paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
				CommonBackingBean commonBackingBean = new CommonBackingBean();
				commonBackingBean.getManagedBean().setReportName(ReportConst.FL_NGEP_COGSTR);
				commonBackingBean.getManagedBean().setReportDisplayName(ResourceBundleUtils.getMessage("lbl.fl.cogstr",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
				commonBackingBean.getManagedBean().setParamMap(paramMap);
				return CommonUIConst.ACKNOWLEDGE_REPORT_PAGE;
			}	
		} else {
			throw new EpException(MessageConst.ERROR_700020);
		} 
			
		return null;
	}
	
	/**
	 * Gets the contract request factoring company  details based on financial OrgId nad address type.
	 *
	 * @param financialOrgId the financial org id
	 * @return the cr selected factoring comp dtl
	 */
	public void getCrSelectedFactoringCompDtl(Long financialOrgId){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialOrgId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
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
	
	/**
	 * Set the portlet title for remove factoring company
	 *
	 * @return portletTitle
	 */
	public String getPortletTitle() {
		String rfc = ResourceBundleUtils.getMessage("lbl.fl.rfc",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		String title = ResourceBundleUtils.getMessage("lbl.fl.doc.maintenance",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale());
		String portletTitle = title;
		if(getManagedBean().isRemoveFactoring()) {
			portletTitle = rfc;
		}
		return portletTitle;
	}
		
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}

	// Please add role if there is new role for this backingbean
	/**
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();	
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return RoleCode.MOF_SUPPLIER_ADMIN.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();			
		else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()))
			return RoleCode.GOVT_SELLER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value()))
			return RoleCode.FACTORING_ADMIN.value();	
		else if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value()))
			return RoleCode.FACTORING_USER.value();	
		else
			return null;
	}
	
	/**
	 * Gpki sign action.
	 *
	 * @param outcome the outcome
	 * @return the string
	 * @throws Exception the exception
	 */
	public String gpkiSignAction(String outcome) throws Exception{
		if ((BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome))) {
			validateReliefOrder(outcome);
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getFlRequest().getDocNo());
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}	
		return submitAction(outcome);
	}
	
	public String preSubmitFulfillmentRequest(
			PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		return submitAction(getManagedBean().getButtonAction());
	}
	
	/**
	 * Method do validations Before save purchase request details
	 *
	 * @param outcome the outcome
	 */
	public void validateReliefOrder(String outcome){		
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		if(BpmOutcomeConst.BPM_APPROVE.equals(outcome)){
			for(FlReliefOrderItem  reliefItem : getManagedBean().getFlReliefOrder().getFlReliefOrderItems()){
				if(reliefItem.getGstFlag().getParameterCode().equalsIgnoreCase(GSFType.RELIEF.value()) && !reliefItem.isSelectedReliefChargeItem()){
					throw new EpException(MessageConst.ERROR_745073);
				}
			}
		}	
	}
	
	
	/**
	* Page navigation to FLDoc search page in Order.
	*
	* @return the string
	*/
	public String backToPervious() {
		return "flDocSearch";
	}
				
	/**
	* Enable auto update.
	*/
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * sorting all the relief and non relief items showing in Relief and Non relief Item Tab
	 *
	 * @param flReliefOrderList 
	 */
	
	public void sortReliefOrderItems(List<FlReliefOrderItem> itemsList) {
		// Creating Comparator
		if (null == itemsList || itemsList.isEmpty()) {
			return;
		}
		Comparator<FlReliefOrderItem> comparator = new Comparator<FlReliefOrderItem>() {
			@Override
			public int compare(FlReliefOrderItem o1, FlReliefOrderItem o2) {
				return new CompareToBuilder().append(
						o1.getFlFulfilmentItem().getItemCode(),
						o2.getFlFulfilmentItem().getItemCode())
						.toComparison();
			}
		};
		Collections.sort(itemsList, comparator);
	}
}
