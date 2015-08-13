package my.ep.web.backingbean.fl.relieforder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.order.FlReliefOrderItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.relieforder.ReliefOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
	
	/** The class name. */
	private String className="@(.myReliefOrderClassName)";
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
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		
		if(taskId != null){
			
		}
	}
	
	/**
	 * save,submit the relief order
	 * 
	 * * @param String action 
	 *  <p>submit - save data into database and BPM process will route to the next respective user.
	 * 	<p>save - save data into database.
	 *  navigate to acknowledge page.
	 */
	public String submitAction(String action) throws Exception {
		
		if(null==getManagedBean().getChargeLineItemRoot()){
			populateReliefItemData();
		}
			
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			TreeNode root=getManagedBean().getChargeLineItemRoot();
			for(TreeNode node:root.getChildren()){
				node.getChildCount();
				FlReliefOrderItem reliefItem=(FlReliefOrderItem) node.getData();
				if(!reliefItem.isSelectedReliefChargeItem()){
					throw new EpException(MessageConst.ERROR_745073);
				}else{
					//TODO
					System.out.println("selected");
				}
			}
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
	 * call this method  as user click on relief order items menu
	 * <p>Populate relief item details.
	 */
	private void populateReliefItemData(){
		getManagedBean().setChargeLineItemRoot(new DefaultTreeNode("root", null));
		
		Map<String, List<FlReliefOrderItem>> reliefOrderItemsMap = new TreeMap<String, List<FlReliefOrderItem>>();
		List<FlReliefOrderItem> flReliefOrderItemList = null;
			for(FlReliefOrderItem  flReliefOrderItem : getManagedBean().getFlReliefOrder().getFlReliefOrderItems()){
				if(flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					if (reliefOrderItemsMap.containsKey(flReliefOrderItem.getFlFulfilmentItem().getItemCode())) {
						flReliefOrderItemList = reliefOrderItemsMap.get(flReliefOrderItem.getFlFulfilmentItem().getItemCode());
					} else {
						flReliefOrderItemList = new ArrayList<FlReliefOrderItem>();
					}
					flReliefOrderItemList.add(flReliefOrderItem);
					reliefOrderItemsMap.put(flReliefOrderItem.getFlFulfilmentItem().getItemCode(), flReliefOrderItemList);
				}
				
			}
		
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		for(Entry<String, List<FlReliefOrderItem>> itemEntry : reliefOrderItemsMap.entrySet()){	
			List<FlReliefOrderItem> reliefOrderItems = itemEntry.getValue();
			FlReliefOrderItem flReliefOrderItem = (FlReliefOrderItem) reliefOrderItems.get(0).cloneEntity(); 
			flReliefOrderItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flReliefOrderItem.getFlFulfilmentItem().getItemCode(), flReliefOrderItem, getManagedBean().getChargeLineItemRoot());
			
			BigDecimal totalOrderAmt = new BigDecimal(0);
			BigDecimal totalReceiveAmt = new BigDecimal(0);
			for(FlReliefOrderItem chargeLineItem : reliefOrderItems) {
				if(flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getItemCode(),chargeLineItem,childItemNode);
					for(FlFulfilmentItemAddr itemAddress: chargeLineItem.getFlFulfilmentItem().getFlFulfilmentItemAddr()){
						totalOrderAmt=totalOrderAmt.add(itemAddress.getOrderedAmt());
						totalReceiveAmt=totalReceiveAmt.add(itemAddress.getReceivedAmt());
					}
				}
			}			

			flReliefOrderItem.setOrderAmount(totalOrderAmt);
			flReliefOrderItem.setReceivedAmt(totalReceiveAmt);
			
			grandTotalOrderAmt = grandTotalOrderAmt.add(totalOrderAmt);
			grandTotalReceivedAmt = grandTotalReceivedAmt.add(totalReceiveAmt);
		}
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		getManagedBean().setReceivedAmtGrandTotal(grandTotalReceivedAmt);
	}
	
	
	/**
	 * call this method  as user click on Non relief order items menu
	 * <p>Populate non relief item details.
	 */
	private void populateNonReliefItemData(){
		getManagedBean().setChargeLineNonReliefItemRoot(new DefaultTreeNode("root", null));
		
		Map<String, List<FlReliefOrderItem>> reliefOrderItemsMap = new TreeMap<String, List<FlReliefOrderItem>>();
		List<FlReliefOrderItem> flReliefOrderItemList = null;
			for(FlReliefOrderItem  flReliefOrderItem : getManagedBean().getFlReliefOrder().getFlReliefOrderItems()){
				if(!flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					if (reliefOrderItemsMap.containsKey(flReliefOrderItem.getFlFulfilmentItem().getItemCode())) {
						flReliefOrderItemList = reliefOrderItemsMap.get(flReliefOrderItem.getFlFulfilmentItem().getItemCode());
					} else {
						flReliefOrderItemList = new ArrayList<FlReliefOrderItem>();
					}
					flReliefOrderItemList.add(flReliefOrderItem);
					reliefOrderItemsMap.put(flReliefOrderItem.getFlFulfilmentItem().getItemCode(), flReliefOrderItemList);
				}
				
			}
		
		BigDecimal grandTotalOrderAmt = new BigDecimal(0);
		BigDecimal grandTotalReceivedAmt = new BigDecimal(0);
		for(Entry<String, List<FlReliefOrderItem>> itemEntry : reliefOrderItemsMap.entrySet()){	
			List<FlReliefOrderItem> reliefOrderItems = itemEntry.getValue();
			FlReliefOrderItem flReliefOrderItem = (FlReliefOrderItem) reliefOrderItems.get(0).cloneEntity(); 
			flReliefOrderItem.setParent(true);
			TreeNode childItemNode = new DefaultTreeNode(flReliefOrderItem.getFlFulfilmentItem().getItemCode(), flReliefOrderItem, getManagedBean().getChargeLineNonReliefItemRoot());
			
			BigDecimal totalOrderAmt = new BigDecimal(0);
			BigDecimal totalReceiveAmt = new BigDecimal(0);
			for(FlReliefOrderItem chargeLineItem : reliefOrderItems) {
				if(!flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					new DefaultTreeNode(chargeLineItem.getFlFulfilmentItem().getItemCode(),chargeLineItem,childItemNode);
					for(FlFulfilmentItemAddr itemAddress: chargeLineItem.getFlFulfilmentItem().getFlFulfilmentItemAddr()){
						totalOrderAmt=totalOrderAmt.add(itemAddress.getOrderedAmt());
						totalReceiveAmt=totalReceiveAmt.add(itemAddress.getReceivedAmt());
					}
				}
			}			

			flReliefOrderItem.setOrderAmount(totalOrderAmt);
			flReliefOrderItem.setReceivedAmt(totalReceiveAmt);
			
			grandTotalOrderAmt = grandTotalOrderAmt.add(totalOrderAmt);
			grandTotalReceivedAmt = grandTotalReceivedAmt.add(totalReceiveAmt);
		}
		getManagedBean().setOrderedAmtGrandTotal(grandTotalOrderAmt);
		getManagedBean().setReceivedAmtGrandTotal(grandTotalReceivedAmt);
	}
	
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
	
	/**
	 * On tab change.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void onTabChange(int selectedTabIndex){
		
		if(selectedTabIndex==1){
			if(null==getManagedBean().getChargeLineItemRoot()){
				populateReliefItemData();
			}
			setClassName("@(.reliefOrderItemPnlClass)");
		}else if(selectedTabIndex==2){
			if(null==getManagedBean().getChargeLineNonReliefItemRoot()){
				populateNonReliefItemData();
			}
			setClassName("@(.nonReliefOrderItemPnlClass)");
		}
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
		 * Page navigation to FLDoc search page in Order.
		 *
		 * @return the string
		 */
		public String backToPervious() {
			return "flDocSearch";
		}
		
		/**
		 * Signing action.
		 *
		 * @param outcome the outcome
		 * @return the string
		 * @throws Exception the exception
		 */
				
		/**
		 * Enable auto update.
		 */
		public void enableAutoUpdate(){
			getManagedBean().setAutoUpdate(Boolean.TRUE);
			RequestContext.getCurrentInstance().update("@form");
		}

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
}
