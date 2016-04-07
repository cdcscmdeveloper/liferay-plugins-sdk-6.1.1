/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.fl.FlAddress;
import my.ep.persistence.entity.fl.FlSupplierDtl;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Base Backing Bean for Fulfilment.
 *
 * @author kieyun
 * @since 09/09/2015
 */

public abstract class FulfilmentBaseBackingBean<T extends BaseManagedBean> extends BaseBackingBean<T> {
	
	/** The log. **/
	private static Logger log = Logger.getLogger(FulfilmentBaseBackingBean.class);
	private static String separator = ", ";

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	protected FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * Get Gst Rate from Profile Management - Rules.
	 * Required in Contract Request and Purchase Request
	 * 
	 * @return GST Rate(BigDecimal)
	 */
	protected BigDecimal getGstRate() {
		if(log.isDebugEnabled()) {
			log.debug("FulfilmentBaseBackingBean.getGstRate()");
		}
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		BigDecimal gstRate = BigDecimal.ZERO;
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		RuleResDTO roleResDTO = null;
		ruleReqDTO.setRuleType(RuleTypeConst.GST);
		roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
		if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
			gstRate = new BigDecimal(roleResDTO.getRuleValueList().get(0).getRuleValue());
		}
		return gstRate;
	}
	
	/**
	 * Get the latest Supplier GST Information for Purchase Request and Contract Request
	 * Recalculate item GST amount and order amount if status changed or Gst Rate changed. 
	 * 
	 * @param FlFulfilmentRequest flRequest
	 * @param boolean isGstRateChanged
	 */
	protected void getSupplierGst(FlFulfilmentRequest flRequest, boolean isGstRateChanged) {
		if(log.isDebugEnabled()) {
			log.debug("FulfilmentBaseBackingBean.getSupplierGst()");
		}
		ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
		
		Date systemDate = flRequest.getChangedDate()==null?DateUtils.getCurrentDateWithoutTime():flRequest.getChangedDate();
		boolean dbFrGstEnabled = flRequest.getGstEffectiveDate() != null && flRequest.getGstEffectiveDate().getTime()<=systemDate.getTime();
		
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setBranchCode(flRequest.getFlSupplierDtl().getBranchCode());
		reqDTO.setSupplierId(flRequest.getSmSupplier().getSupplierId());
		SupplierGstResDTO resDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(reqDTO);
		if(resDTO.getSmGstSupplier() != null){
			flRequest.setGstEffectiveDate(resDTO.getSmGstSupplier().getGstEffDate());
			flRequest.setGstDeclaredDate(resDTO.getSmGstSupplier().getGstDeclaredDate());
			flRequest.setGstRegNo(resDTO.getSmGstSupplier().getGstRegNo());
		} else {
			flRequest.setGstEffectiveDate(null);
			flRequest.setGstDeclaredDate(null);
			flRequest.setGstRegNo(null);
		}
		
		boolean newFrGstEnabled = flRequest.getGstEffectiveDate() != null && flRequest.getGstEffectiveDate().getTime()<=DateUtils.getCurrentDateWithoutTime().getTime();
		// recalculate gst amount/gst unit price/order amount if both flag difference
		if(isGstRateChanged || (dbFrGstEnabled && !newFrGstEnabled) || (!dbFrGstEnabled && newFrGstEnabled)) {
			BigDecimal orderAmt = BigDecimal.ZERO;
			BigDecimal gstRate = BigDecimal.ZERO;
			if(flRequest.getGstRate()!=null) {
				gstRate = flRequest.getGstRate();
			}
			BigDecimal originalGSTAmt = BigDecimal.ZERO;
			boolean amtChanged = false;
			for(FlDeliveryAddress flDeliveryAddress : flRequest.getFlDeliveryAddresses()) {
				for(FlFulfilmentItemAddr itemAddr : flDeliveryAddress.getFlFulfilmentItemAddr()) {
					originalGSTAmt = itemAddr.getGstAmount();
					if(originalGSTAmt==null) {
						originalGSTAmt = BigDecimal.ZERO;
					}
					if(newFrGstEnabled && itemAddr.isGst()) {
						itemAddr.setGstAmount(itemAddr.getUnitPrice().multiply(gstRate).divide(new BigDecimal(100), CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
					} else {
						itemAddr.setGstAmount(BigDecimal.ZERO);
					}
					if(!amtChanged && originalGSTAmt.compareTo(itemAddr.getGstAmount())!=0) {
						amtChanged = true;
					}
					itemAddr.setGstUnitPrice(itemAddr.getUnitPrice().add(itemAddr.getGstAmount()));
					orderAmt = itemAddr.getGstUnitPrice().multiply(itemAddr.getOrderedQty());
					itemAddr.setOrderedAmt(orderAmt.setScale(CommonConstants.DEFAULT_AMOUNT_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP));
				}
			}
			if(amtChanged == true){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745009, new String[]{flRequest.getDocNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		}
	}
	
	protected List<PmUser> sortApproverUserList(List<PmUserGroupUser> approverGroupUserList, PmUser approver,Long groupId){
		IUserGroupSO userGroupSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
		ArrayList<String> names = new ArrayList<String>();
		List<PmUser> list1 = new ArrayList<PmUser>();
		List<PmUser> list2 = new ArrayList<PmUser>();
		UserGroupReqDTO reqDTO = new UserGroupReqDTO();
		
		List<PmUser> pmUserList = new ArrayList<PmUser>();
		if(approver != null){
			for(PmUserGroupUser groupUser : approverGroupUserList){
				names.add(groupUser.getPmUser().getUserName());
				pmUserList.add(groupUser.getPmUser());
			}
			if(!names.contains(approver.getUserName())){
				names.add(approver.getUserName());
				pmUserList.add(approver);
			}
			Collections.sort(pmUserList, new Comparator<PmUser>() {
				@Override
				public int compare(PmUser s1, PmUser s2) {
					return s1.getUserName().compareToIgnoreCase(s2.getUserName());
				}
			});
			boolean match = false;
			for(PmUser pmUser : pmUserList){
				if(pmUser.getUserName().equals(approver.getUserName())){
					match = true;
				}
				if(match){
					list2.add(pmUser);
				}else{
					list1.add(pmUser);
				}
			}
			reqDTO.setGroupId(groupId);
			reqDTO.setUserId(approver.getUserId());
			UserGroupResDTO resDTO = userGroupSO.retrieveUserGroupUserByUserIdAndGroupId(reqDTO);
			if(resDTO.getUserGroupUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
				list2.remove(0);
			}
			list2.addAll(list1);
		}
		return list2;
	}
	
	/**
	 * Checks if is supplier role.
	 *
	 * @return true, if is supplier role
	 */
	protected boolean hasSupplierRole(){
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			return Boolean.TRUE;
		} else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Retrieve supplier user's role
	 *
	 * @return user supplier role
	 */
	protected String getUserSupplierRole(){
		if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) 
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) 
			return RoleCode.FL_USER.value();				
		else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()))
			return RoleCode.GOVT_SELLER.value();		
		else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) 
			return RoleCode.G2G_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) 
			return RoleCode.G2G_SUPPLIER_USER.value();	
		else 
			throw new EpException(MessageConst.ERROR_750040);
	}
	
	/**
	 * Check is it login user is Government Seller
	 * 
	 * @return true, if user is Governement Seller
	 */
	protected boolean hasGovtSellerRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()))
			return true;
		else
			return false;
	}
	
	/**
	 * Check is it login user is Factoring User
	 * 
	 * @return true, if user is Factoring User
	 */
	protected boolean hasFactoringUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value()))
			return true;
		else
			return false;
	}
	
	/**
	 * Check is it login user is Government User
	 * 
	 * @return true, if user is Government User
	 */
	protected boolean hasGovernmentUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value()))
			return true;		
		else if (getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value()))
			return true;		
		else
			return false;		
	}
	
	protected void updateSupplierBranchAddress(SmSupplierBranch supplierBranch,FlSupplierDtl supplierDtl){
		FlAddress flAddress = supplierDtl.getFlAddress();
		SmAddress smAddress = supplierBranch.getSmAddress();
		flAddress.setAddress1(smAddress.getAddress1());
		flAddress.setAddress2(smAddress.getAddress2());
		flAddress.setAddress3(smAddress.getAddress3());
		flAddress.setAddress4(smAddress.getAddress4());
		flAddress.setMapLatitude(smAddress.getMapLatitude());
		flAddress.setMapLongitude(smAddress.getMapLongitude());
		flAddress.setPmCity(smAddress.getPmCity());
		flAddress.setPmCountry(smAddress.getPmCountry());
		flAddress.setPmDistrict(smAddress.getPmDistrict());
		flAddress.setPmDivision(smAddress.getPmDivision());
		flAddress.setPmState(smAddress.getPmState());
		flAddress.setPostcode(smAddress.getPostcode());
		
		flAddress.setFaxNo(supplierBranch.getFaxNo());
		flAddress.setFaxArea(supplierBranch.getFaxArea());
		flAddress.setFaxCountry(supplierBranch.getFaxCountry());
		flAddress.setPhoneNo(supplierBranch.getPhoneNo());
		flAddress.setPhoneArea(supplierBranch.getPhoneArea());
		flAddress.setPhoneCountry(supplierBranch.getPhoneCountry());
		
		flAddress.setContactEmail(supplierBranch.getSmPersonnel().getEmail());
		flAddress.setContactFaxNo(supplierBranch.getSmPersonnel().getFaxNo());
		flAddress.setContactMobileNo(supplierBranch.getSmPersonnel().getMobileNo());
		flAddress.setContactPerson(supplierBranch.getSmPersonnel().getName());
		flAddress.setContactPhoneNo(supplierBranch.getSmPersonnel().getPhoneNo());
		flAddress.setContactFaxArea(supplierBranch.getSmPersonnel().getFaxArea());
		flAddress.setContactFaxCountry(supplierBranch.getSmPersonnel().getFaxCountry());
		flAddress.setContactMobileArea(supplierBranch.getSmPersonnel().getMobileArea());
		flAddress.setContactMobileCountry(supplierBranch.getSmPersonnel().getMobileCountry());
		flAddress.setContactPhoneArea(supplierBranch.getSmPersonnel().getPhoneArea());
		flAddress.setContactPhoneCountry(supplierBranch.getSmPersonnel().getPhoneCountry());
		
		supplierDtl.setBranchName(supplierBranch.getBranchName());
		supplierDtl.setBranchCode(supplierBranch.getBranchCode());
				
	}
	
	protected void updateSupplierHqAddress(FlSupplierDtl supplierDtl, SmSupplier supplier){
		FlAddress flAddress = supplierDtl.getFlAddress();
		for(SmCompanyAddress smCompanyAddress : supplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
			if(smCompanyAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())){
				SmAddress smAddress = smCompanyAddress.getSmAddress();
				flAddress.setAddress1(smAddress.getAddress1());
				flAddress.setAddress2(smAddress.getAddress2());
				flAddress.setAddress3(smAddress.getAddress3());
				flAddress.setAddress4(smAddress.getAddress4());
				flAddress.setMapLatitude(smAddress.getMapLatitude());
				flAddress.setMapLongitude(smAddress.getMapLongitude());
				flAddress.setPmCity(smAddress.getPmCity());
				flAddress.setPmCountry(smAddress.getPmCountry());
				flAddress.setPmDistrict(smAddress.getPmDistrict());
				flAddress.setPmDivision(smAddress.getPmDivision());
				flAddress.setPmState(smAddress.getPmState());
				flAddress.setPostcode(smAddress.getPostcode());
				flAddress.setFaxNo(supplier.getSmAppl().getSmCompanyBasic().getFaxNo());
				flAddress.setFaxArea(supplier.getSmAppl().getSmCompanyBasic().getFaxArea());
				flAddress.setFaxCountry(supplier.getSmAppl().getSmCompanyBasic().getFaxCountry());
				flAddress.setPhoneNo(supplier.getSmAppl().getSmCompanyBasic().getPhoneNo());
				flAddress.setPhoneArea(supplier.getSmAppl().getSmCompanyBasic().getPhoneArea());
				flAddress.setPhoneCountry(supplier.getSmAppl().getSmCompanyBasic().getPhoneCountry());
			}
		}
		for(SmPersonnel smPersonnel: supplier.getSmAppl().getSmPersonnels()){
			if(smPersonnel.isContactPerson()){
				flAddress.setContactEmail(smPersonnel.getEmail());
				flAddress.setContactFaxNo(smPersonnel.getFaxNo());
				flAddress.setContactMobileNo(smPersonnel.getMobileNo());
				flAddress.setContactPerson(smPersonnel.getName());
				flAddress.setContactPhoneNo(smPersonnel.getPhoneNo());
				flAddress.setContactFaxArea(smPersonnel.getFaxArea());
				flAddress.setContactFaxCountry(smPersonnel.getFaxCountry());
				flAddress.setContactMobileArea(smPersonnel.getMobileArea());
				flAddress.setContactMobileCountry(smPersonnel.getMobileCountry());
				flAddress.setContactPhoneArea(smPersonnel.getPhoneArea());
				flAddress.setContactPhoneCountry(smPersonnel.getPhoneCountry());
				
			}
		}
	}
	
	protected void convertCtAddressToFlAddress(FlFulfilmentRequest flRequest,FlSupplierDtl supplierDtl){
		CtContractValue ctSupplierDtl = flRequest.getCtContract().getLatestContractVer().getCtContractValue();
		if(ctSupplierDtl.getSupplierBranch() != null){
			supplierDtl.setBranchCode(null);
			supplierDtl.setBranchName(ctSupplierDtl.getSupplierBranch());
		}else{
			supplierDtl.setBranchCode(null);
			supplierDtl.setBranchName(null);
		}
		FlAddress flAddress = new FlAddress();
		CtAddress ctAddress = ctSupplierDtl.getSupplierAddress();
		flAddress.setAddress1(ctAddress.getAddress1());
		flAddress.setAddress2(ctAddress.getAddress2());
		flAddress.setAddress3(ctAddress.getAddress3());
		flAddress.setAddress4(ctAddress.getAddress4());
		flAddress.setMapLatitude(ctAddress.getMapLatitude());
		flAddress.setMapLongitude(ctAddress.getMapLongitude());
		flAddress.setPmCity(ctAddress.getPmCity());
		flAddress.setPmCountry(ctAddress.getPmCountry());
		flAddress.setPmDistrict(ctAddress.getPmDistrict());
		if(ctAddress.getPmDivision()!=null){
			flAddress.setPmDivision(ctAddress.getPmDivision());
		}
		
		flAddress.setPmState(ctAddress.getPmState());
		flAddress.setPostcode(ctAddress.getPostcode());
		
		flAddress.setFaxNo(ctAddress.getFaxNo());
		flAddress.setFaxArea(ctAddress.getFaxArea());
		flAddress.setFaxCountry(ctAddress.getFaxCountry());
		flAddress.setPhoneNo(ctAddress.getPhoneNo());
		flAddress.setPhoneArea(ctAddress.getPhoneArea());
		flAddress.setPhoneCountry(ctAddress.getPhoneCountry());

		flAddress.setContactEmail(ctAddress.getContactEmail());
		flAddress.setContactFaxNo(ctAddress.getContactFaxNo());
		flAddress.setContactMobileNo(ctAddress.getContactMobileNo());
		flAddress.setContactPerson(ctAddress.getContactPerson());
		flAddress.setContactPhoneNo(ctAddress.getContactPhoneNo());
		flAddress.setContactFaxArea(ctAddress.getContactFaxArea());
		flAddress.setContactFaxCountry(ctAddress.getContactFaxCountry());
		flAddress.setContactMobileArea(ctAddress.getContactMobileArea());
		flAddress.setContactMobileCountry(ctAddress.getContactMobileCountry());
		flAddress.setContactPhoneArea(ctAddress.getContactPhoneArea());
		flAddress.setContactPhoneCountry(ctAddress.getContactPhoneCountry());
		supplierDtl.setFlAddress(flAddress);
	}
	
	protected FlAddress convertPmAddressToFlAddress(PmAddress pmAddress){
		FlAddress flAddress = new FlAddress();
		flAddress.setAddressName(pmAddress.getAddressName());
		flAddress.setAddress1(pmAddress.getAddress1());
		flAddress.setAddress2(pmAddress.getAddress2());
		flAddress.setAddress3(pmAddress.getAddress3());
		flAddress.setAddress4(pmAddress.getAddress4());
		flAddress.setMapLatitude(pmAddress.getMapLatitude());
		flAddress.setMapLongitude(pmAddress.getMapLongitude());
		flAddress.setPmCity(pmAddress.getPmCity());
		flAddress.setPmCountry(pmAddress.getPmCountry());
		flAddress.setPmDistrict(pmAddress.getPmDistrict());
		if(pmAddress.getPmDivision()!=null){
			flAddress.setPmDivision(pmAddress.getPmDivision());
		}
		flAddress.setPmState(pmAddress.getPmState());
		flAddress.setPostcode(pmAddress.getPostcode());
		flAddress.setFaxNo(pmAddress.getFaxNo());
		flAddress.setFaxArea(pmAddress.getFaxArea());
		flAddress.setFaxCountry(pmAddress.getFaxCountry());
		flAddress.setPhoneNo(pmAddress.getPhoneNo());
		flAddress.setPhoneArea(pmAddress.getPhoneArea());
		flAddress.setPhoneCountry(pmAddress.getPhoneCountry());
		return flAddress;
	}
	
	protected FlAddress convertFinAccAddressToFlAddress(PmAddressType pmAddressType){
		FlAddress flAddress = new FlAddress();
		PmAddress pmAddress = pmAddressType.getPmAddress();
				flAddress.setAddress1(pmAddress.getAddress1());
				flAddress.setAddress2(pmAddress.getAddress2());
				flAddress.setAddress3(pmAddress.getAddress3());
				flAddress.setAddress4(pmAddress.getAddress4());
				flAddress.setMapLatitude(pmAddress.getMapLatitude());
				flAddress.setMapLongitude(pmAddress.getMapLongitude());
				flAddress.setPmCity(pmAddress.getPmCity());
				flAddress.setPmCountry(pmAddress.getPmCountry());
				flAddress.setPmDistrict(pmAddress.getPmDistrict());
				if(pmAddress.getPmDivision()!=null){
					flAddress.setPmDivision(pmAddress.getPmDivision());
				}
				flAddress.setPmState(pmAddress.getPmState());
				flAddress.setPostcode(pmAddress.getPostcode());
				flAddress.setFaxNo(pmAddress.getFaxNo());
				flAddress.setFaxArea(pmAddress.getFaxArea());
				flAddress.setFaxCountry(pmAddress.getFaxCountry());
				flAddress.setPhoneNo(pmAddress.getPhoneNo());
				flAddress.setPhoneArea(pmAddress.getPhoneArea());
				flAddress.setPhoneCountry(pmAddress.getPhoneCountry());
				
				if(!pmAddressType.getPmContacts().isEmpty() ){
					PmContact pmContact = pmAddressType.getPmContacts().get(0);
					flAddress.setContactEmail(pmContact.getEmail());
					flAddress.setContactFaxNo(pmContact.getFaxNo());
					flAddress.setContactMobileNo(pmContact.getMobileNo());
					flAddress.setContactPerson(pmContact.getContactName());
					flAddress.setContactPhoneNo(pmContact.getPhoneNo());
					flAddress.setContactFaxArea(pmContact.getFaxArea());
					flAddress.setContactFaxCountry(pmContact.getFaxCountry());
					flAddress.setContactMobileArea(pmContact.getMobileArea());
					flAddress.setContactMobileCountry(pmContact.getMobileCountry());
					flAddress.setContactPhoneArea(pmContact.getPhoneArea());
					flAddress.setContactPhoneCountry(pmContact.getPhoneCountry());
				}
	
		return flAddress;
		
	}
	
	protected FlAddress convertSmAddressToFlAddress(SmAddress smAddress,FlAddress flAddress){
		
		flAddress.setAddress1(smAddress.getAddress1());
		flAddress.setAddress2(smAddress.getAddress2());
		flAddress.setAddress3(smAddress.getAddress3());
		flAddress.setAddress4(smAddress.getAddress4());
		flAddress.setMapLatitude(smAddress.getMapLatitude());
		flAddress.setMapLongitude(smAddress.getMapLongitude());
		flAddress.setPmCity(smAddress.getPmCity());
		flAddress.setPmCountry(smAddress.getPmCountry());
		flAddress.setPmDistrict(smAddress.getPmDistrict());
		if(smAddress.getPmDivision()!=null){
			flAddress.setPmDivision(smAddress.getPmDivision());
		}
		flAddress.setPmState(smAddress.getPmState());
		flAddress.setPostcode(smAddress.getPostcode());
		
		return flAddress;
	}
	
	/**
	 * Get Full Address based on SmAddress
	 * @param flAddress
	 * @return full address
	 */
	public String getFullAddress(FlAddress flAddress) {
		StringBuilder address = new StringBuilder();
		if(flAddress!=null) {
			if (flAddress.getAddressName() != null && flAddress.getAddressName().length() > 0 && !flAddress.getAddressName().equals("-")) {
				address.append(flAddress.getAddressName());
				address.append(separator);
			}
			if (flAddress.getAddress1() != null && flAddress.getAddress1().length() > 0) {
				address.append(address.length() > 0 ? System.getProperty("line.separator") : "");
				address.append(flAddress.getAddress1());
				address.append(separator);
			}
			if (flAddress.getAddress2() != null && flAddress.getAddress2().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(flAddress.getAddress2());
				address.append(separator);
			}
			if (flAddress.getAddress3() != null && flAddress.getAddress3().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(flAddress.getAddress3());
				address.append(separator);
			}
			if (flAddress.getAddress4() != null && flAddress.getAddress4().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(flAddress.getAddress4());
				address.append(separator);
			}
			if (flAddress.getPostcode() != null && flAddress.getPostcode().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(flAddress.getPostcode());
				address.append(separator);
			}
			if (flAddress.getPmCity() != null && flAddress.getPmCity().getCityName() !=null && flAddress.getPmCity().getCityName().length() > 0) {
				address.append(flAddress.getPmCity().getCityName());
				address.append(separator);
			}
			if (flAddress.getPmState() != null && flAddress.getPmState().getStateName() !=null && flAddress.getPmState().getStateName().length() > 0) {
				address.append(flAddress.getPmState().getStateName());
			}						
		}
		return address.toString();
	}
	
	/**
	 * Get Full Address based on pmAddress
	 * @param pmAddress
	 * @return full address
	 */
	public String getPmFullAddress(PmAddress pmAddress) {
		StringBuilder address = new StringBuilder();
		if(pmAddress!=null) {
			if (pmAddress.getAddressName() != null && pmAddress.getAddressName().length() > 0) {
				address.append(pmAddress.getAddressName());
				address.append(separator);
			}
			if (pmAddress.getAddress1() != null && pmAddress.getAddress1().length() > 0) {
				address.append(pmAddress.getAddress1());
				address.append(separator);
			}
			if (pmAddress.getAddress2() != null && pmAddress.getAddress2().length() > 0) {
				address.append(pmAddress.getAddress2());
				address.append(separator);
			}
			if (pmAddress.getAddress3() != null && pmAddress.getAddress3().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getAddress3());
				address.append(separator);
			}
			if (pmAddress.getAddress4() != null && pmAddress.getAddress4().length() > 0) {
				address.append(pmAddress.getAddress4());
				address.append(separator);
			}
			
			if (pmAddress.getPmDistrict() != null && pmAddress.getPmDistrict().getDistrictName() !=null && pmAddress.getPmDistrict().getDistrictName().length() > 0) {
				address.append(pmAddress.getPmDistrict().getDistrictName());
				address.append(separator);
			}
			
			if (pmAddress.getPostcode() != null && pmAddress.getPostcode().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getPostcode());
				address.append(" ");
			}
			if (pmAddress.getPmCity() != null && pmAddress.getPmCity().getCityName() !=null && pmAddress.getPmCity().getCityName().length() > 0) {
				address.append(pmAddress.getPmCity().getCityName());
				address.append(separator);
			}

			if (pmAddress.getPmState() != null && pmAddress.getPmState().getStateName() !=null && pmAddress.getPmState().getStateName().length() > 0) {
				address.append(System.getProperty("line.separator"));
				address.append(pmAddress.getPmState().getStateName());
			}			
		}
		return address.toString();
	}
	
}
