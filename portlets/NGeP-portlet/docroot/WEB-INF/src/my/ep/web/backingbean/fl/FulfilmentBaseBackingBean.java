/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl;

import java.math.BigDecimal;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
	
}
