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

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
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
	
	protected List<PmUser> sortApproverUserList(List<PmUserGroupUser> approverGroupUserList, PmUser approver){
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		ArrayList<String> names = new ArrayList<String>();
		List<PmUser> list1 = new ArrayList<PmUser>();
		List<PmUser> list2 = new ArrayList<PmUser>();

		UserReqDTO userReqDTO = new UserReqDTO();
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
			userReqDTO.setLoginId(approver.getLoginId());
			UserResDTO userResDTO = userSO.retrieveUserByLoginId(userReqDTO);
			if(userResDTO.getPmUser().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
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
	
}
