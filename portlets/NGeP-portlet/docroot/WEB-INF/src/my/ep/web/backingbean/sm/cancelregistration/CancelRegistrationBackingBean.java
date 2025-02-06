package my.ep.web.backingbean.sm.cancelregistration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.cancelregistration.CancelRegistartionManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "cancelRegistrationBackingBean")
@ViewScoped
public class CancelRegistrationBackingBean extends
		BaseBackingBean<CancelRegistartionManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8242421500840047056L;

	public static Logger log = Logger
			.getLogger(CancelRegistrationBackingBean.class);
	private static String cancelBasicMofAccountRegistration = "/xhtml/sm/cancelRegistration/cancelBasicMofAccountRegistration.xhtml?faces-redirect=true";
	private static String cancelG2GAccountRegistration = "/xhtml/sm/cancelRegistration/cancelG2GAccountRegistration.xhtml?faces-redirect=true";
	private static String cancelPreRegistration = "/xhtml/sm/cancelRegistration/cancelPreRegistration.xhtml?faces-redirect=true";

	private boolean agreeTC;

	private SmPersonnel selectedPersonnel;

	public void preRender() {
		renewSesionObject();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/cancelRegistration/cancelPreRegistration.xhtml", "",
				null, null);
	}

	public void cancelPreRegistraction() {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(this.getManagedBean().getBusinessRegNo());
		reqDTO.setCompanyName(this.getManagedBean().getCompanyName());
		reqDTO.setApplType(getManagedBean().getAccType());
		String accTypeStr = StringUtils.EMPTY;

		try {

			SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO()
					.retrieveNewSupplierAndPersonnelsByBusRegNoAndName(reqDTO);

			if (resDTO.getSmSupplier() != null
					&& resDTO.getSmSupplier().getSmAppl() != null
					&& !EntityHelper.isCollectionEmpty(resDTO.getSmSupplier()
							.getSmAppl().getSmPersonnels()) && resDTO.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
				
				getManagedBean().setSmAppl(resDTO.getSmSupplier().getSmAppl());
				
				
				selectedPersonnel = getAdminPersonnel(resDTO.getSmSupplier());
				if (selectedPersonnel == null && resDTO.getSmSupplier().getSmAppl() != null
						&& !EntityHelper.isCollectionEmpty(resDTO.getSmSupplier().getSmAppl()
								.getSmPersonnels())) {
					for (SmPersonnel per : resDTO.getSmSupplier().getSmAppl().getSmPersonnels()) {
						if (per.getEpRole() != null && (per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
							selectedPersonnel = per;
							break;
						}
					}
				}
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				if (selectedPersonnel == null) {
					throw new EpException(MessageConst.ERROR_721090);

					} else {
						// check the status of softcert request
						if (selectedPersonnel.getSoftcertFlg() == (DbValueConst.RECORD_STATUS_EXPIRED)) {
	
							throw new EpException(MessageConst.ERROR_721091);
						}
						//boolean activeAppl = SmApplicationUtil.checkActiveSmApplForSupp(resDTO.getSmSupplier());
						SmAppl activeAppl = SmCommonUtil
								.getActiveApplBySupplier(getManagedBean().getSmSupplier());
						// check new MOF Account (Contractor/Consultant) pending
						// supplier application
						if(activeAppl != null){
							PmStatusDesc pmStatusDesc = getPmStatusDesc(activeAppl
									.getPmStatus().getStatusId());
							throw new EpException(MessageConst.ERROR_721092,
									new String[] {
									activeAppl.getApplNo(),
									pmStatusDesc.getStatusName()
									});
							
						}
						//to check active rejected application
						SmAppl appl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(resDTO.getSmSupplier().getSupplierId(),ParameterConst.NEW_SM_APPLN_FOR_MOF);
						
						if (selectedPersonnel.getIdentificationNo().equals(getManagedBean().getSuppAdminIdentificationNo().trim())) {
							//#25474------13072020
							//start #30992
						if ((resDTO.getSmSupplier().getRecordStatusFlg() == 5 && resDTO.getSmSupplier().getSmAppl()
								.getPmStatus() == null)
								|| (resDTO.getSmSupplier().getSmAppl().getPmStatus() != null 
								&& (resDTO.getSmSupplier().getSmAppl().getPmStatus().getStatusId()
										.equals(Long.valueOf(DbValueConst.PM_STATUS_ID))
										|| resDTO.getSmSupplier().getSmAppl().getPmStatus().getStatusId()
												.equals(Long.valueOf(DbValueConst.PM_PENDING_ACTIVATON_ID)) || appl != null))) {// PM_STATTUS_ID=20199
							//end #30992
								//#25474 - same message with basic account if appl was rejected by PO for more than 60 days
								boolean poRejectFlag = false;
								if(appl != null && appl.getSdoReceivedDate() != null){
									Calendar c = new GregorianCalendar();
									c.setTime(appl.getSdoReceivedDate());
									int maxResubmissionDays = SmRuleHelper.getInstance().getApplicationResubmissionDays();
									c.add(Calendar.DATE, maxResubmissionDays);
									Date rejectedDate = c.getTime();
									Date currentDate = new Date();
									if(currentDate.before(rejectedDate)) poRejectFlag = true;
								}								
								
								if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR) && poRejectFlag) {
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor",null);
									String msg = SmCommonUtil.getMessage("lbl.sm.req.cancel.confirmation.msg.ApplStatus.Contractor",new String[] { accTypeStr });
									getManagedBean().setConfirmationMsg(msg);
									RequestContext.getCurrentInstance().execute("confirmDialog.show();");
								}else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT) && poRejectFlag) {
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant",null);
									String mssg = SmCommonUtil.getMessage("lbl.sm.req.cancel.confirmation.msg.ApplStatus.Consulutent",new String[] { accTypeStr });
									getManagedBean().setConfirmationMsg(mssg);
									RequestContext.getCurrentInstance().execute("confirmDialog.show();");
								}else{							
								//#25474------13072020
									if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)) {
										accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.basic", null);
									} else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
										accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor",null);
									} else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
										accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant",null);
									} else {getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_GOVT_SELLER);
										accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
									}
		
										String msg = SmCommonUtil.getMessage("lbl.sm.req.cancel.confirmation.msg",new String[] { accTypeStr });
										getManagedBean().setConfirmationMsg(msg);
										RequestContext.getCurrentInstance().execute("confirmDialog.show();");
									}
						} else if (resDTO.getSmSupplier().getSmAppl().getPmStatus() != null //#30992
								&& resDTO
										.getSmSupplier()
										.getSmAppl()
										.getPmStatus()
										.getStatusId()
										.equals(Long
												.valueOf(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_DECLARATION))) {
							
								if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor",null);
								}else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant", null);
								}else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)) {
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.basic", null);
								}  else if(getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_GOVT_SELLER)){
									accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
								}
								String msg = SmCommonUtil.getMessage("lbl.sm.req.cancel.confirmation.msg",new String[] { accTypeStr });
								getManagedBean().setConfirmationMsg(msg);
								RequestContext.getCurrentInstance().execute("confirmDialog.show();");
						}else {
							throw new EpException(MessageConst.ERROR_721090);
						}
					}else{
						throw new EpException(MessageConst.ERROR_721090);
					}
				}
			}else {
				if(resDTO.getSmSupplier() != null && resDTO.getSmSupplier().getSmAppl()!= null && resDTO.getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
					String labelText = null;
					if(resDTO.getSmSupplier().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
						labelText = SmCommonUtil.getMessage("lbl.sm.contractor", null);
					}else if(resDTO.getSmSupplier().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
						labelText = SmCommonUtil.getMessage("lbl.sm.consultant", null);
					}else if(resDTO.getSmSupplier().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)){
						labelText = SmCommonUtil.getMessage("lbl.sm.basic", null);
					}else if(resDTO.getSmSupplier().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_GOVT_SELLER)){
						labelText = SmCommonUtil.getMessage("lbl.sm.g2g", null);
					}
					throw new EpException(MessageConst.ERROR_721093,new String[]{labelText,resDTO.getSmSupplier().getEpNo()});
				}else{
					throw new EpException(MessageConst.ERROR_721090);
				}
				
			}
		}finally {

		}
	}

	/**
	 * Clear entered information on change account type
	 * 
	 * @param e
	 */
	public void onChangeAccType(AjaxBehaviorEvent e) {
		this.getManagedBean().setCompanyName(StringUtils.EMPTY);
		this.getManagedBean().setBusinessRegNo(StringUtils.EMPTY);
		this.getManagedBean().setSuppAdminIdentificationNo(StringUtils.EMPTY);
	}

	private SmPersonnel getAdminPersonnel(SmSupplier supplier) {
		SmPersonnel retVal = null;
		if (supplier.getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(supplier.getSmAppl()
						.getSmPersonnels())) {
			for (SmPersonnel per : supplier.getSmAppl().getSmPersonnels()) {
				if (per.getEpRole() != null
						&& my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
								.contains(per.getEpRole())
						&& per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					retVal = per;
					break;
				}
			}
		}
		return retVal;
	}

	public SmPersonnel getSelectedPersonnel() {
		return selectedPersonnel;
	}

	public void setSelectedPersonnel(SmPersonnel selectedPersonnel) {
		this.selectedPersonnel = selectedPersonnel;
	}

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public String onClickProceed() {
		if (getManagedBean().getAccType() != null) {
			if (getManagedBean().getAccType().equals(
					SupplierManagementConst.STYPE_BASIC)) {
				getManagedBean()
						.setAccType(SupplierManagementConst.STYPE_BASIC);
			} else if (getManagedBean().getAccType().equals(
					SupplierManagementConst.STYPE_CONTRACTOR)) {
				getManagedBean().setAccType(
						SupplierManagementConst.STYPE_CONTRACTOR);
			} else if (getManagedBean().getAccType().equals(
					SupplierManagementConst.STYPE_CONSULTANT)) {
				getManagedBean().setAccType(
						SupplierManagementConst.STYPE_CONSULTANT);
			} else {
				getManagedBean().setAccType(
						SupplierManagementConst.STYPE_GOVT_SELLER);
				return cancelG2GAccountRegistration;
			}
			return cancelBasicMofAccountRegistration;

		}

		return cancelPreRegistration;
	}

	public void cancelDeleteAction() {
		IPersonnelSO personnelSO = SmServiceLookup.getPersonnelSO();
		SmSupplier supplier = this.getManagedBean().getSmSupplier();
		SmPersonnel currentPersonnel = null;
		for (SmPersonnel personnel : supplier.getSmAppl().getSmPersonnels()) {		
			if (this.getManagedBean().getSuppAdminIdentificationNo()
					.equalsIgnoreCase(personnel.getIdentificationNo())) {
				currentPersonnel = personnel;
				break;
			}
		}
		if (currentPersonnel != null) {
			PersonnelReqDTO anotherReqDTO = new PersonnelReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			anotherReqDTO.setSmPersonnel(currentPersonnel);
			anotherReqDTO.setUserLocale(facesScopes.getUserLocale());
			PersonnelResDTO pResDTO = personnelSO.inactiveSupplierRole(anotherReqDTO);
//			personnelSO.inactiveSupplierRole(anotherReqDTO);
			cancelReasonInfo();
			
			if(pResDTO.isSuccessful()){
				String accTypeStr = "";				
				if (getManagedBean().getAccType().equalsIgnoreCase(
						SupplierManagementConst.STYPE_BASIC)) {
					accTypeStr = SmCommonUtil
							.getMessage("lbl.sm.basic.account.basic", null);
				} else if (getManagedBean()
						.getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
					accTypeStr = SmCommonUtil
							.getMessage("lbl.sm.basic.account.mof.contractor",null);
				} else if (getManagedBean()
						.getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
					accTypeStr = SmCommonUtil
							.getMessage("lbl.sm.basic.account.mof.consultant",null);
				} else {
					getManagedBean().getAccType().equalsIgnoreCase(
							SupplierManagementConst.STYPE_GOVT_SELLER);
					accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
				}
				
				String succesfulMsg = SmCommonUtil.getMessage("lbl.sm.supplier.pre.registration.deleted", 
						new String[] { accTypeStr });
				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, null, succesfulMsg));
			}
		} else {
			throw new EpException("Missing personnel");
		}

	}

	private void cancelReasonInfo() {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		SmRemaksResDTO resDTO = new SmRemaksResDTO();
		reqDTO.setDocType(ParameterConst.SM_APPLN_RMRK_CANCEL_NEW_REGISTRATION_INPROGRESS);
		reqDTO.setSupplierRemark(this.getManagedBean().getCancellationReason());
		reqDTO.setDocId(this.getManagedBean().getSmAppl().getApplId());
	    resDTO = SmServiceLookup.getSmRemarksSO()
				.updateSupplierReason(reqDTO);

	}
	
	/**
	 * Sets the pm status desc.
	 * 
	 * @param statusId
	 *            - pm status id passed as String
	 */
	private PmStatusDesc getPmStatusDesc(Long docStatus) {

		StatusReqDTO reqDTO = new StatusReqDTO();
		//SQ remove new long to docstatus
		reqDTO.setStatusId(docStatus);
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			return resDTO.getPmStatusDesc();
		}
		return null;
	}
}