/*
 * Copyright © 2017 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.pojobweight;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.common.pojobweight.IPoJobWeightSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.pojobweight.PoJobWeightReqDTO;
import my.ep.dto.pm.common.pojobweight.PoJobWeightResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMJobWeightBackingBean;
import my.ep.web.managedbean.pm.pojobweight.PoJobWeightManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/**
 * The Backing Bean for Po Job Weight
 * 
 * @author Ken
 * @since 13/3/2017
 */
@ManagedBean(name = "poJobWeightBackingBean")
@ViewScoped
public class PoJobWeightBackingBean extends
		BaseBackingBean<PoJobWeightManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_MAIN = "/xhtml/pm/pojobweight/poJobWeightMain.xhtml?faces-redirect=true";

	private static final Logger log = EpLogger
			.getLogger(PoJobWeightBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		renewSesionObject();
		initJobWeight();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	private void initJobWeight() {
		log.info("initJobWeight");
		getManagedBean().setJobWeightList(PmCommonUtil.retrievePmPoJobWeightList());
		PmPoJobWeight oriJobWeight = null;
		for(PmPoJobWeight jobWeight : getManagedBean().getJobWeightList()){
			oriJobWeight = (PmPoJobWeight)jobWeight.cloneEntity();  
			getManagedBean().getOriJobWeightList().add(oriJobWeight);
		}
	}

	public void saveJobWeight() {
		log.info("saveJobWeight");
		IPoJobWeightSO jobWeightSO = null;
		validateJobWeight();
		try {
			jobWeightSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IPoJobWeightSO.class);
			PoJobWeightReqDTO reqDTO = new PoJobWeightReqDTO();
			reqDTO.setJobWeightList(getManagedBean().getJobWeightList());
			setupTrackingDiaryVal(reqDTO);
			/************ AUDIT START #28895-35509 *************/
			AuditPMBackingBeanTemplate auTemplate = new AuditPMJobWeightBackingBean();
			IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAuditPMSO.class);

			/************ AUDIT END #28895-35509 *************/
			PoJobWeightResDTO resDTO = jobWeightSO.saveJobWeight(reqDTO);
			if (resDTO.isSuccessful()) {
				initJobWeight();
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_INFO, MessageConst.INFO_779200,
						new String[] {});
				/************ AUDIT START #28895-35509 *************/
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_JOB_WEIGHT_RATIO)) {
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_JOB_WEIGHT_RATIO);
				}
				/************ AUDIT END #28895-35509 *************/
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			jobWeightSO = null;
		}
	}

	private void validateJobWeight() {
		Integer moderate = null;
		Integer high = null;
		Integer extraHigh = null;
		for (PmPoJobWeight weight : getManagedBean().getJobWeightList()) {
			if (ParameterConst.JOB_WEIGHT_MODERATE.equals(weight
					.getWeightType().getParameterCode())) {
				moderate = weight.getWeightValue();
			} else if (ParameterConst.JOB_WEIGHT_HIGH.equals(weight
					.getWeightType().getParameterCode())) {
				high = weight.getWeightValue();
			} else if (ParameterConst.JOB_WEIGHT_EXTRA_HIGH
					.equals(weight.getWeightType().getParameterCode())) {
				extraHigh = weight.getWeightValue();
			}
		}
		
		if(isNullOrZero(moderate) || isNullOrZero(high) || isNullOrZero(extraHigh)){
			throw new EpException(MessageConst.ERROR_779202);
		}
		
		if (moderate > 9) {
			throw new EpException("Cannot more than 9");
		}
			
		if (high > 9) {
			throw new EpException("Cannot more than 9");
		}
		
		if (extraHigh > 9) {
			throw new EpException("Cannot more than 9");
		}
		
		if(moderate.equals(high) || moderate.equals(extraHigh) || high.equals(extraHigh) ){
			throw new EpException(MessageConst.ERROR_779201);
		}
		
		if(moderate > high){
			throw new EpException(MessageConst.ERROR_779203,new String[]{
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.high"),
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.moderate")
			});
		}
		
		if(moderate > extraHigh){
			throw new EpException(MessageConst.ERROR_779203,new String[]{
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.extra"),
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.moderate")
			});
		}
		
		if(high > extraHigh){
			throw new EpException(MessageConst.ERROR_779203,new String[]{
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.extra"),
					PmCommonUtil.getResourceBundleLabels("lbl.pm.po.jobweight.high")
			});
		}
		
	}

	public void resetJobWeight() {
		for (PmPoJobWeight weight : getManagedBean().getJobWeightList()) {
			weight.setWeightValue(null);
		}
	}
	
	private boolean isNullOrZero(Integer value){
		if(value == null || value == 0){
			return true;
		}
		return false;
	}
	
	private void setupTrackingDiaryVal(PoJobWeightReqDTO reqDTO){
		String messageId = null;
		for(PmPoJobWeight jobWeight : getManagedBean().getJobWeightList()){
			for(PmPoJobWeight oriJobWeight : getManagedBean().getOriJobWeightList()){
				if(jobWeight.getPoJobWeightId().equals(oriJobWeight.getPoJobWeightId()) &&
						!jobWeight.getWeightValue().equals(oriJobWeight.getWeightValue())){
					messageId = null;
					if(ParameterConst.JOB_WEIGHT_MODERATE.equals(jobWeight.getWeightType().getParameterCode())){
						messageId = MessageConst.TD_81500;
					}else if(ParameterConst.JOB_WEIGHT_HIGH.equals(jobWeight.getWeightType().getParameterCode())){
						messageId = MessageConst.TD_81501;
					}else if(ParameterConst.JOB_WEIGHT_EXTRA_HIGH.equals(jobWeight.getWeightType().getParameterCode())){
						messageId = MessageConst.TD_81502;
					}	
					if(messageId != null){
						reqDTO.getTrackingDiaryList().add(
								PmCommonUtil.assignTrackingDairyForJobWeight(jobWeight,
										 PmCommonUtil.getActionDesc(messageId, 
												 new String[]{oriJobWeight.getWeightValue().toString(),
												 jobWeight.getWeightValue().toString()}), 
												 jobWeight.getRecordStatusFlg().longValue()));
				
					}
				}
			}
		}
	}
	
	
}
