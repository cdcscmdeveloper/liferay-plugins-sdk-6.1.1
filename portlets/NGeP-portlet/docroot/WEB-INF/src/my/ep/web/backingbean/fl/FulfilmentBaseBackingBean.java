/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseRequestSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
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
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
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
	
	private static final long serialVersionUID = 1L;
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
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
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
					itemAddr.setGst(true);//Redmine #20011
				}
			}
			if(amtChanged == true){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_745009, new String[]{flRequest.getDocNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		}
	}
	
	/**
	 * Sort Approver User List
	 * @param approver, approverGroupUserList, groupId
	 * @return PmUser List
	 */
	protected List<PmUser> sortApproverUserList(List<PmUserGroupUser> approverGroupUserList, PmUser approver,Long groupId){
		IUserGroupSO userGroupSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
		ArrayList<String> names = new ArrayList<String>();
		List<PmUser> list1 = new ArrayList<PmUser>();
		List<PmUser> list2 = new ArrayList<PmUser>();
		UserGroupReqDTO reqDTO = new UserGroupReqDTO();
		UserGroupReqDTO reqDTOCheckActive = new UserGroupReqDTO();//28857
		BigDecimal isRemove = BigDecimal.ONE;//28857
		
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
				//34461-Start
				else{
					match = false;
				}
				//34461-False
				if(match){
					list2.add(pmUser);
				}else{
					list1.add(pmUser);
				}
			}
			//28857-start
			reqDTOCheckActive.setGroupId(groupId);
			reqDTOCheckActive.setUserId(approver.getUserId());
			UserGroupResDTO resDTOCheckActive = userGroupSO.checkApproverUserActive(reqDTOCheckActive);
			isRemove = resDTOCheckActive.getCheckActive();
			if(isRemove.equals(BigDecimal.ZERO)){
				list2.remove(0);
			}else{
				reqDTO.setGroupId(groupId);
				reqDTO.setUserId(approver.getUserId());
				UserGroupResDTO resDTO = userGroupSO.retrieveUserGroupUserByUserIdAndGroupId(reqDTO);
				if(resDTO.getUserGroupUser()==null || (resDTO.getUserGroupUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE)){
					list2.remove(0);
				}
			}
			//28857-end
			list2.addAll(list1);
		}
		return list2;
	}
	
	/**
	 * Checks if is supplier role.
	 *
	 * @return true, if is supplier role
	 */
	public boolean hasSupplierRole(){
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
	
	/**
	 * Update Supplier Branch Address
	 */
	protected void updateSupplierBranchAddress(SmSupplierBranch supplierBranch,FlSupplierDtl supplierDtl){
		FlAddress flAddress = supplierDtl.getFlAddress();
		SmAddress smAddress = supplierBranch.getSmAddress();
		if(smAddress!=null) {
			flAddress.setAddress1(smAddress.getAddress1());
			flAddress.setAddress2(smAddress.getAddress2());
			flAddress.setAddress3(smAddress.getAddress3());
			flAddress.setMapLatitude(smAddress.getMapLatitude());
			flAddress.setMapLongitude(smAddress.getMapLongitude());
			flAddress.setPmCity(smAddress.getPmCity());
			flAddress.setPmCountry(smAddress.getPmCountry());
			flAddress.setPmDistrict(smAddress.getPmDistrict());
			flAddress.setPmDivision(smAddress.getPmDivision());
			flAddress.setPmState(smAddress.getPmState());
			flAddress.setPostcode(smAddress.getPostcode());
		}
		
		flAddress.setFaxNo(supplierBranch.getFaxNo());
		flAddress.setFaxArea(supplierBranch.getFaxArea());
		flAddress.setFaxCountry(supplierBranch.getFaxCountry());
		flAddress.setPhoneNo(supplierBranch.getPhoneNo());
		flAddress.setPhoneArea(supplierBranch.getPhoneArea());
		flAddress.setPhoneCountry(supplierBranch.getPhoneCountry());
		//#31920 filter active user
		if(supplierBranch.getSmPersonnel() != null && 
				supplierBranch.getSmPersonnel().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE  ){
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
		}
		
		
		supplierDtl.setBranchName(supplierBranch.getBranchName());
		supplierDtl.setBranchCode(supplierBranch.getBranchCode());
				
	}
	
	/**
	 * Update Supplier Hq Address
	 */
	protected void updateSupplierHqAddress(FlSupplierDtl supplierDtl,Long supplierId, List<SmCompanyBasic> compBasicList ){//29728
		FlAddress flAddress = supplierDtl.getFlAddress();
		//29728-Start
		for (SmCompanyBasic companyBasic : compBasicList) {
			for(SmCompanyAddress smCompanyAddress : companyBasic.getSmCompanyAddresses()){
				if(smCompanyAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())){
					SmAddress smAddress = smCompanyAddress.getSmAddress();
					
					flAddress.setAddress1(smAddress.getAddress1());
					flAddress.setAddress2(smAddress.getAddress2());
					flAddress.setAddress3(smAddress.getAddress3());
					flAddress.setMapLatitude(smAddress.getMapLatitude());
					flAddress.setMapLongitude(smAddress.getMapLongitude());
					flAddress.setPmCity(smAddress.getPmCity());
					flAddress.setPmCountry(smAddress.getPmCountry());
					flAddress.setPmDistrict(smAddress.getPmDistrict());
					flAddress.setPmDivision(smAddress.getPmDivision());
					flAddress.setPmState(smAddress.getPmState());
					flAddress.setPostcode(smAddress.getPostcode());
					flAddress.setFaxNo(companyBasic.getFaxNo());
					flAddress.setFaxArea(companyBasic.getFaxArea());
					flAddress.setFaxCountry(companyBasic.getFaxCountry());
					flAddress.setPhoneNo(companyBasic.getPhoneNo());
					flAddress.setPhoneArea(companyBasic.getPhoneArea());
					flAddress.setPhoneCountry(companyBasic.getPhoneCountry());
				}
			}
		}
		
		IPersonnelSO personnelSO;
		personnelSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
				IPersonnelSO.class);

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		PersonnelResDTO personnelResDTO = personnelSO.retrieveContactPersonBySupplierId(reqDTO);

		if (!personnelResDTO.getSmPersonnelList().isEmpty()) {
			for (SmPersonnel smPersonnel : personnelResDTO.getSmPersonnelList()) {
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
			//29728-End
		}
	}
	
	/**
	 * Convert Ct Address to FL Address 
	 */
	protected void convertCtAddressToFlAddress(CtContractValue ctSupplierDtl,FlSupplierDtl supplierDtl){//33917
		//CtContractValue ctSupplierDtl = flRequest.getCtContract().getLatestContractVer().getCtContractValue(); //33917
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
	
	/**
	 * Convert Pm Address to FL Address 
	 * 
	 * @return flAddress
	 */
	protected FlAddress convertPmAddressToFlAddress(PmAddress pmAddress){
		FlAddress flAddress = new FlAddress();
		flAddress.setAddressName(pmAddress.getAddressName());
		flAddress.setAddress1(pmAddress.getAddress1());
		flAddress.setAddress2(pmAddress.getAddress2());
		flAddress.setAddress3(pmAddress.getAddress3());
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
	
	/**
	 * Convert Financial Account Address to FL Address 
	 * 
	 * @return flAddress
	 */
	protected FlAddress convertFinAccAddressToFlAddress(PmAddressType pmAddressType){
		FlAddress flAddress = new FlAddress();
		PmAddress pmAddress = pmAddressType.getPmAddress();
				flAddress.setAddress1(pmAddress.getAddress1());
				flAddress.setAddress2(pmAddress.getAddress2());
				flAddress.setAddress3(pmAddress.getAddress3());
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
	
	/**
	 * Convert Sm Address to FL Address 
	 * 
	 * @return flAddress
	 */
	protected FlAddress convertSmAddressToFlAddress(SmAddress smAddress,FlAddress flAddress){
		if(smAddress == null){
			return null;
		}
		flAddress.setAddress1(smAddress.getAddress1());
		flAddress.setAddress2(smAddress.getAddress2());
		flAddress.setAddress3(smAddress.getAddress3());
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
	
	/**
	 * Get Document Transaction Date. To display Org Profile Name based on date.
	 * @param flRequest
	 * @return Date
	 */
	public Date getTransactionDate(FlFulfilmentRequest flRequest) {
		if(flRequest!=null) {
			if(flRequest.getAgApprovedDate()!=null) {
				return flRequest.getAgApprovedDate();
			} else {
				return flRequest.getCreatedDate();
			}
		}
		return null;
	}
	
	// 26279 - start
	/**
	 * Retrieve Imposed Disciplinary Action By Supplier
	 * 
	 * @param smSupplier
	 * @return SupplierDataMaintenanceResDTO SmDisciplinaryActions info
	 */
	protected SmDisciplinaryAction getImposedDisciplinaryAction(SmSupplier smSupplier) {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(smSupplier);

		ISupplierDataMaintenanceSO supplierDataMaintenanceSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierDataMaintenanceSO.class);

		SupplierDataMaintenanceResDTO resDTO = supplierDataMaintenanceSO
				.retrieveImposedDisciplinaryActionBySupplier(reqDTO);// always return single result

		if (resDTO.getSmDisciplinaryActions() != null && !resDTO.getSmDisciplinaryActions().isEmpty()) {
			return resDTO.getSmDisciplinaryActions().get(0);
		}
		return null;
	}

	/**
	 * Get Disciplinary Action Message
	 * 
	 * @param startDate
	 * @param endDate
	 * @param langCode
	 * @return msg
	 */
	protected String getDisciplinaryActionMsg(Date startDate, Date endDate, boolean isWarningOnly) {
		StringBuilder msg = new StringBuilder();
		if (startDate != null && endDate != null) {
			msg.append(" ");
			String messagePrefix;
			if (isWarningOnly) {
				messagePrefix = "lbl.sm.supplier.warning.disc.action";
			} else {
				messagePrefix = "lbl.sm.supplier.imposed.disc.action";
			}
			
			msg.append(ResourceBundleUtils.getMessage(
					messagePrefix,
					new String[] { DateUtils.dateToString(CommonConstants.DATE_FORMAT_DASH, startDate),
							DateUtils.dateToString(CommonConstants.DATE_FORMAT_DASH, endDate) }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));

		}
		return msg.toString();
	}

	// 26279 - Remove the retrieveDisciplinaryActions method because the method
	// from the SM module is used for displaying the Disciplinary Action info in
	// the pop-up
	
	// 26279 - remove getMessageByKey and use ResourceBundleUtils.getMessage

	/**
	 * Check if the disciplinary action is a warning-only action.
	 * 
	 * @param smDisciplinaryAction
	 *            the action to check
	 * @return true if the action is only a warning, false if otherwise actions
	 */
	public boolean isWarningOnly(SmDisciplinaryAction smDisciplinaryAction) {
		Set<Boolean> actionSet = new HashSet<>();
		actionSet.add(smDisciplinaryAction.isCancelCategory());
		actionSet.add(smDisciplinaryAction.isSuspendRegistration());
		actionSet.add(smDisciplinaryAction.isCancelRegistration());
		actionSet.add(smDisciplinaryAction.isBlacklistOwner());
		actionSet.add(smDisciplinaryAction.isBumiputeraCancel());
		actionSet.add(smDisciplinaryAction.isManufacturerCancel());

		boolean isImposedAction  = actionSet.contains(true);
		return !isImposedAction && smDisciplinaryAction.isWarning();
	}
	
	/**
	 * Sets the disciplinary action message
	 * 
	 * @param smDisciplinaryAction
	 * @return warningOrDisciplinaryActionMsg
	 */
	public String getDisciplinaryActionMsg(SmDisciplinaryAction smDisciplinaryAction) {
		boolean isWarningOnly = isWarningOnly(smDisciplinaryAction);

		return getDisciplinaryActionMsg(smDisciplinaryAction.getStartDate(), smDisciplinaryAction.getEndDate(),
				isWarningOnly);
	}

	// 26279 - end
	
	//29728 - start	
	protected boolean isSelectedBranchDeleted(Long branchCode, Long supplierId) {
		IMofRegistrationSO mofRegistrationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setBranchCode(branchCode);
		reqDTO.setSupplierId(supplierId);
		MofRegistrationResDTO resDTO = mofRegistrationSO.isSupplierBranchDeleted(reqDTO);
		if(resDTO.isSupplierBranchDeleted()) {
			FacesContext.getCurrentInstance().addMessage("delBranchMsg", new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					MessageUtils.getMessage(MessageConst.INFO_745087, 
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return true;
		}
		return false;
	}
	//29728 - end
	
	//21416 start
	public String filterItemNameNA(String itemName) {
		String filteredItemName = itemName.replaceAll("\\s(NA|N/A|N A)\\b", "");
		return filteredItemName;
	}
	//21416 end
	
	//36386 -Start
	/**
	 * Checks if is Government User Role
	 *
	 * @return true, if user has Government User Role
	 */
	public boolean hasFlGovernmentUserRole() {
		List<String> roles = Arrays.asList(RoleCode.REQUISITIONER.value(), RoleCode.FL_APPROVER.value(),
				RoleCode.RECEIVING_OFFICER.value(), RoleCode.ACKNOWLEDGE_OFFICER.value(), RoleCode.PAY_OFFICER.value(),
				RoleCode.BPK_APPROVER.value(), RoleCode.BPK_AUDITOR.value(), RoleCode.AUDITOR_GENERAL.value(),
				RoleCode.AUDITOR_INTERNAL.value(), RoleCode.SYS_ADMIN.value());

		for (String role : roles) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Determines whether the PRCR link can be viewed based on user roles.
	 * 
	 * @return {@code true} if the user can view the PRCR link, {@code false} otherwise
	 */
	public boolean canViewPRCRLink() {
		
		if (hasSupplierRole() || hasFactoringUserRole()) {
			if(hasGovtSellerRole() && hasFlGovernmentUserRole()){
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	//36386 -End
	
	//30753-Start
	/**
	 * Retrieves the Contract Order (CO) limit amount for a given fulfilment
	 * request.
	 * 
	 * @param flRequestId
	 * @return resDTO
	 */
	public FulfilmentResDTO retrieveCOLimitAmount(Long flRequestId) {

		IFulfilmentRequestSO requestSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);

		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(flRequestId);
		FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequestById(reqDTO);

		if (resDTO != null && resDTO.getFlFulfilmentRequest() != null) {
			FlFulfilmentRequest fulfilmentReq = resDTO.getFlFulfilmentRequest();
			String contractType = fulfilmentReq.getCtContract().getContractType().getParameterCode();

			CtContractVer latestVer = fulfilmentReq.getCtContract().getLatestContractVer();
			Long contractVerId = latestVer != null ? latestVer.getContractVerId() : null;
			Long orgProfileId = fulfilmentReq.getPreparedOrgProfile() != null ? fulfilmentReq.getPreparedOrgProfile()
					.getOrgProfileId() : null;

			if (contractType.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {

				resDTO = retrieveCOLimitAmountForMinistry(resDTO, contractVerId, orgProfileId);

			} else if (contractType.equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {

				boolean followContractValue = latestVer != null && latestVer.getCtContractValue() != null
						&& latestVer.getCtContractValue().isFollowLoaAmt();
				if (followContractValue) {
					BigDecimal totalContractAmount = checkContractAmount(contractVerId);
					resDTO.setLimitAmount(totalContractAmount);
				} else {
					// no limit
					resDTO.setLimitAmount(BigDecimal.ZERO);
				}
			}
		}

		return resDTO;
	}

	/**
	 * Retrieves the Contract Order (CO) limit amount for a given fulfilment
	 * request.
	 * 
	 * @param resDTO
	 * @param contractVerId
	 * @param orgProfileId
	 * @return resDTO
	 */
	public FulfilmentResDTO retrieveCOLimitAmountForMinistry(FulfilmentResDTO resDTO, Long contractVerId,
			Long orgProfileId) {
		IContractIntegrationSO contractIntegrationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);
		ContractIntegrationReqDTO ciReqDTO = new ContractIntegrationReqDTO();
		ciReqDTO.setContractVerId(contractVerId);
		ciReqDTO.setPtjOrgId(orgProfileId);

		ContractIntegrationResDTO ciResDTO = contractIntegrationSO.getAgencyByVersionAndOrgProfile(ciReqDTO);

		if (ciResDTO.getCtAgency() != null && ciResDTO.getCtAgency().getCeilingAmount() != null
				&& ciResDTO.getCtAgency().getCeilingAmount().compareTo(BigDecimal.ZERO) > 0) {
			// Contract Sharing Ceiling
			resDTO.setLimitAmount(ciResDTO.getCtAgency().getCeilingAmount());
			resDTO.setContractSharing(true);
		} else {
			// No Sharing use overall contract amount
			BigDecimal totalContractAmount = checkContractAmount(contractVerId);
			resDTO.setLimitAmount(totalContractAmount);
		}

		return resDTO;
	}

	/**
	 * Get total contract amount (including STAX and GST) for the latest
	 * contract version
	 * 
	 * @param contractLastestVer
	 * @return total contract amount with tax
	 */
	public BigDecimal checkContractAmount(Long contractLastestVer) {
		IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
				IContractSO.class);
		ContractReqDTO contractReqDTO = new ContractReqDTO();
		contractReqDTO.setContractVerId(contractLastestVer);
		contractReqDTO.setAsAtDate(new Date());
		CtContractAmount ctContractAmount = contractSO.getContractAmtByLatestVer(contractReqDTO).getCtContractAmount();

		if (ctContractAmount.getStaxAmount() == null) {
			ctContractAmount.setStaxAmount(new BigDecimal(0));
		}

		if (ctContractAmount.getGstAmount() == null) {
			ctContractAmount.setGstAmount(new BigDecimal(0));
		}

		return ctContractAmount.getContractAmount().add(ctContractAmount.getStaxAmount())
				.add(ctContractAmount.getGstAmount());

	}
	
	/**
	 * Validates and retrieves the threshold amount for a Purchase Order (PO)
	 * based on procurement type (panel threshold or item threshold).
	 * 
	 * @param flRequestId
	 * @param totalDeliveryAmount
	 * @return resDTO
	 */
	public FulfilmentResDTO validatePOThresholdAmount(Long flRequestId, BigDecimal totalDeliveryAmount) {

		IFulfilmentRequestSO requestSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentRequestSO.class);

		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(flRequestId);
		FulfilmentResDTO resDTO = requestSO.retrieveFulfilmentRequestById(reqDTO);

		if (resDTO != null && resDTO.getFlFulfilmentRequest() != null) {
			FlFulfilmentRequest fulfilmentReq = resDTO.getFlFulfilmentRequest();
			PmParameter procType = fulfilmentReq.getProcurementType();

			if (isPK7OrSpecialItem(procType)) {

				return getPOThresholdAmountForPK7AndSpecialItem(resDTO, fulfilmentReq, totalDeliveryAmount);

			} else if (procType != null && procType.getParameterCode() != null) {

				return getPOThresholdAmountForForNormal(resDTO, fulfilmentReq, totalDeliveryAmount);
			}

		}
		return resDTO;
	}
	
	/**
	 * Checks if the given procurement type is either PK7 or Special Item.
	 * 
	 * @param procType
	 * @return true if procType is PK7 or Special Item, false otherwise
	 */
	private boolean isPK7OrSpecialItem(PmParameter procType) {
		return procType != null
				&& procType.getParameterCode() != null
				&& (procType.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_PK7) || procType
						.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TEMP_FL_TYPE_SPECIAL_ITEM));
	}
	
	/**
	 * Calculates and returns the threshold amount for PK7 and special item
	 * Purchase Orders (PO). If the total delivery amount exceeds the PK7
	 * threshold, it marks the limit as exceeded.
	 * 
	 * @param resDTO
	 * @param fulfilmentReq
	 * @param totalDeliveryAmount
	 * @return resDTO
	 */
	private FulfilmentResDTO getPOThresholdAmountForPK7AndSpecialItem(FulfilmentResDTO resDTO,
			FlFulfilmentRequest fulfilmentReq, BigDecimal totalDeliveryAmount) {
		BigDecimal thresholdAmountPk7 = getThresholdForPK7AndSpecialItem(fulfilmentReq.getScPurchaseRequest()
				.getPurchaseRequestId());
		PmParameter procType = fulfilmentReq.getProcurementType();

		if (thresholdAmountPk7.compareTo(BigDecimal.ZERO) > 0 && totalDeliveryAmount.compareTo(thresholdAmountPk7) > 0) {
			resDTO.setExceedLimit(true);
			resDTO.setLimitAmount(thresholdAmountPk7);
			resDTO.setProcurementType(this.getParameterDesc(procType));
		}
		return resDTO;
	}

	/**
	 * Retrieves the threshold amount for PK7 and Special Item.
	 * 
	 * @param purchaseRequestId
	 * @return thresholdAmount
	 */
	private BigDecimal getThresholdForPK7AndSpecialItem(Long purchaseRequestId) {
		IPurchaseRequestSO purchaseRequestSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseRequestSO.class);
		BigDecimal thresholdAmount = BigDecimal.ZERO;

		if (purchaseRequestId == null) {
			return thresholdAmount;
		}

		PurchaseRequestReqDTO reqDTO = new PurchaseRequestReqDTO();
		reqDTO.setPurchaseRequestId(purchaseRequestId);
		reqDTO.setIncludeDetail(true);
		PurchaseRequestResDTO purResDTO = purchaseRequestSO.getPurchaseRequest(reqDTO);

		if (purResDTO == null || purResDTO.getPurchaseRequest() == null) {
			return thresholdAmount;
		}

		List<ScRequestNoteDtl> noteDtlList = new ArrayList<>();
		noteDtlList.addAll(purResDTO.getPurchaseRequest().getRequestNoteDtls());

		if (!noteDtlList.isEmpty()) {
			for (ScRequestNoteDtl purchaseItem : noteDtlList) {
				if (purchaseItem.getJustificationType() != null
						&& purchaseItem.getJustificationType().getPrThreshold() != null) {
					thresholdAmount = purchaseItem.getJustificationType().getPrThreshold();
				}

			}
		}
		return thresholdAmount;
	}
	
	/**
	 * Calculates and returns the threshold amount for a normal procurement type
	 * Purchase Order (PO). for panel or non-panel procurement type.
	 * 
	 * @param resDTO
	 * @param fulfilmentReq
	 * @param totalDeliveryAmount
	 * @return resDTO
	 */	
	private FulfilmentResDTO getPOThresholdAmountForForNormal(FulfilmentResDTO resDTO,
			FlFulfilmentRequest fulfilmentReq, BigDecimal totalDeliveryAmount) {
		resDTO.setSupplierId(fulfilmentReq.getSmSupplier().getSupplierId());
		FulfilmentResDTO resDTO2 = getThresholdForNormal(resDTO, totalDeliveryAmount);

		boolean isPanel = resDTO2.isPanel();
		PmParameter procType = fulfilmentReq.getProcurementType();

		BigDecimal thresholdAmt = resDTO2.getLimitAmount();

		if (procType.getParameterCode().equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			if (isPanel) {
				resDTO2.setLimitAmount(thresholdAmt);
				resDTO2.setProcurementType(this.getParameterDesc(procType) + " (" + ParameterConst.PANEL + ")");
			} else {
				resDTO2.setProcurementType(this.getParameterDesc(procType) + " (" + ParameterConst.NON_PANEL + ")");
				resDTO2.setSupplierTypeId(resDTO2.getSupplierTypeId());
			}
		} else {
			resDTO2.setProcurementType(this.getParameterDesc(procType));
		}

		return resDTO2;
	}

		
	/**
	 * Validates delivery amount vs threshold based on panel or non-panel.
	 * 
	 * @param resDTO
	 * @param totalDeliveryAmount
	 * @return resDTO
	 */
	private FulfilmentResDTO getThresholdForNormal(FulfilmentResDTO resDTO, BigDecimal totalDeliveryAmount) {

		ScRequestNote requestNote = null;
		if (resDTO.getFlFulfilmentRequest() != null && resDTO.getFlFulfilmentRequest().getScPurchaseRequest() != null) {
			requestNote = resDTO.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote();
		}

		if (requestNote != null && requestNote.getQuote() != null && requestNote.getQuote().isPanel()) {
			return validateTheresholdForPanel(totalDeliveryAmount);
		} else {
			return validateThresholdForNonPanel(resDTO, totalDeliveryAmount);
		}
	}
	

	/**
	 * Validates the threshold limit for panel based on panel threshold rule
	 * configuration.
	 * 
	 * @param totalDeliveryAmount
	 * @return resDTO
	 * @throws EpException
	 *             if no rule configuration is found for panel threshold
	 */
	private FulfilmentResDTO validateTheresholdForPanel(BigDecimal totalDeliveryAmount) {
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IRuleSO.class);

		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleTypeConst.PANEL_THRESHOLD);
		resDTO.setPanel(true);

		RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);

		if (ruleResDTO.isSuccessful() && ruleResDTO.getRuleValueList() != null
				&& !ruleResDTO.getRuleValueList().isEmpty()) {

			String thresholdStr = ruleResDTO.getRuleValueList().get(0).getRuleValue();

			if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
				BigDecimal limitAmount = new BigDecimal(thresholdStr.trim());

				if (limitAmount.compareTo(BigDecimal.ZERO) > 0 && totalDeliveryAmount.compareTo(limitAmount) > 0) {
					resDTO.setExceedLimit(true);
					resDTO.setLimitAmount(limitAmount);
				}

			}
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[] { getRuleTypeDesc(ruleReqDTO.getRuleType()) });
		}
		return resDTO;

	}

	/**
	 * Validates the threshold limit for non-panel suppliers based on supplier
	 * type (MOF or BASIC).
	 * 
	 * @param resDTO
	 * @param totalDeliveryAmount
	 * @return resDTO
	 */
	private FulfilmentResDTO validateThresholdForNonPanel(FulfilmentResDTO resDTO, BigDecimal totalDeliveryAmount) {
		IMofRegistrationSO mofRegistrationSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofRegistrationSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD);
		resDTO.setPanel(false);

		// check supplier mof or basic
		SmMofAccount mofAcc = mofRegistrationSO.getLatestMofAccountBySupplierId(resDTO.getSupplierId());

		long suppTypeId = (mofAcc != null && mofAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) ? FulfilmentConst.ACC_TYPE_ID_MOF
				: FulfilmentConst.ACC_TYPE_ID_BASIC;

		reqDTO.setSupplierTypeId(suppTypeId);
		reqDTO.setValidationAmt(totalDeliveryAmount);
		FulfilmentResDTO resNonPanel = validationThresholdForSupplierType(reqDTO);

		resDTO.setLimitAmount(resNonPanel.getLimitAmount());
		resDTO.setExceedLimit(resNonPanel.isExceedLimit());
		resDTO.setSupplierTypeId(suppTypeId);
		return resDTO;
	}
	
	/**
	 * Validates the totalDeliveryAmount vs threshold limit based on supplier
	 * type and configured rule ranges.
	 * 
	 * @param reqDTO
	 * @return resDTO
	 * @throws EpException
	 *             if no rule configuration exists for the supplier type
	 */
	public FulfilmentResDTO validationThresholdForSupplierType(FulfilmentReqDTO reqDTO) {
		IRuleSO ruleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IRuleSO.class);

		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleTypeConst.PURCHASE_REQUEST_THERSHOLD);

		RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
		BigDecimal validationAmt = reqDTO.getValidationAmt();

		if (roleResDTO.isSuccessful() && !EntityHelper.isCollectionEmpty(roleResDTO.getRuleValueList())) {
			return checkThresholdForSupplierType(resDTO, roleResDTO.getRuleValueList(), reqDTO.getSupplierTypeId(),
					validationAmt, ruleReqDTO.getRuleType());

		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[] { getRuleTypeDesc(ruleReqDTO.getRuleType()) });
		}
	}
	
	/**
	 * Check the threshold limit for a given supplier type based on
	 * configured rule values.
	 * 
	 * @param resDTO
	 * @param ruleValueList
	 * @param supplierTypeId
	 * @param validationAmt
	 * @param ruleType
	 * @return resDTO
	 * @throws EpException
	 *             if no configuration exists for the supplier type
	 */
	private FulfilmentResDTO checkThresholdForSupplierType(FulfilmentResDTO resDTO,
			List<PmConfigRuleValue> ruleValueList, Long supplierTypeId, BigDecimal validationAmt, String ruleType) {

		// Find threshold configuration for the given supplier type and validation amount
		ThresholdResult thresholdResult = findThresholdForSupplierType(ruleValueList, supplierTypeId, validationAmt);
		BigDecimal limitAmount = thresholdResult.limitAmount;
		boolean foundRange = thresholdResult.foundRange;
		boolean hasSupplierTypeConfig = thresholdResult.hasSupplierTypeConfig;

		// Supplier type has threshold config but validation amount is outside all defined ranges
		if (hasSupplierTypeConfig && !foundRange) {
			limitAmount = determineMaxThresholdLimit(resDTO, ruleValueList, supplierTypeId, validationAmt);
		}

		// No threshold configuration found for this supplier type
		if (!hasSupplierTypeConfig) {
			throw new EpException(MessageConst.ERROR_770222, new String[] { getRuleTypeDesc(ruleType) });
		}

		resDTO.setLimitAmount(limitAmount);
		return resDTO;
	}

	/**
	 * Finds the threshold for a given supplier type and validation amount.
	 *
	 * @param ruleValueList 
	 * @param supplierTypeId
	 * @param validationAmt
	 * @return ThresholdResult
	 */
	private ThresholdResult findThresholdForSupplierType(List<PmConfigRuleValue> ruleValueList, Long supplierTypeId,
			BigDecimal validationAmt) {
		BigDecimal limitAmount = BigDecimal.ZERO;
		boolean foundRange = false;
		boolean hasSupplierTypeConfig = false;
		for (PmConfigRuleValue rule : ruleValueList) {
			if (supplierTypeId.equals(rule.getAccTypeId())) {
				hasSupplierTypeConfig = true;

				BigDecimal fromVal = rule.getFromValue() != null ? rule.getFromValue() : BigDecimal.ZERO;
				BigDecimal toVal = rule.getToValue() != null ? rule.getToValue() : BigDecimal.ZERO;

				if (validationAmt.compareTo(fromVal) >= 0 && validationAmt.compareTo(toVal) <= 0) {
					limitAmount = toVal;
					foundRange = true;
					break;
				}

				limitAmount = rule.getToValue();
			}
		}

		return new ThresholdResult(limitAmount, foundRange, hasSupplierTypeConfig);
	}

	/**
	 * Determines the maximum threshold limit for a supplier type and updates the response DTO if exceeded.
	 *
	 * @param resDTO 
	 * @param ruleValueList 
	 * @param supplierTypeId 
	 * @param validationAmt 
	 * @return maxToValue
	 */
	private BigDecimal determineMaxThresholdLimit(FulfilmentResDTO resDTO, List<PmConfigRuleValue> ruleValueList,
			Long supplierTypeId, BigDecimal validationAmt) {
		BigDecimal maxToValue = getMaxConfiguredThreshold(ruleValueList, supplierTypeId);

		if (validationAmt.compareTo(maxToValue) > 0) {
			// Exceed max range
			resDTO.setExceedLimit(true);
			resDTO.setLimitAmount(maxToValue);
		} else {
			// Not exceed but not in any configured threshold range
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
							MessageConst.INFO_745100, new String[] { validationAmt.toPlainString() },
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		return maxToValue;
	}

	/**
	 * Returns the maximum configured 'toValue' for the given supplier type.
	 *
	 * @param ruleValueList 
	 * @param supplierTypeId 
	 * @return the maximum toValue among all rules for the supplier type, or 0 if none
	 */
	private BigDecimal getMaxConfiguredThreshold(List<PmConfigRuleValue> ruleValueList, Long supplierTypeId) {
		BigDecimal maxToValue = BigDecimal.ZERO;

		for (PmConfigRuleValue rule : ruleValueList) {
			if (supplierTypeId.equals(rule.getAccTypeId()) && rule.getToValue() != null
					&& rule.getToValue().compareTo(maxToValue) > 0) {
				maxToValue = rule.getToValue();
			}
		}

		return maxToValue;
	}

	/**
	 * Inner static class ThresholdResult represents the result of threshold
	 * validation for a specific supplier type.
	 * 
	 * This class holds information about the applicable limit amount, whether
	 * the validation amount falls within a defined range, and whether any
	 * configuration exists for the supplier type.
	 */
	private static class ThresholdResult {
		final BigDecimal limitAmount;
		final boolean foundRange;
		final boolean hasSupplierTypeConfig;

		ThresholdResult(BigDecimal limitAmount, boolean foundRange, boolean hasSupplierTypeConfig) {
			this.limitAmount = limitAmount;
			this.foundRange = foundRange;
			this.hasSupplierTypeConfig = hasSupplierTypeConfig;
		}
	}
	// 30753-End

}
