package my.ep.web.backingbean.sm.cancelregistration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.cancelregistration.CancelRegistartionManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
		System.out.println("Business Reg Number:" + reqDTO.getRegNo());
		System.out.println("Company Name:" + reqDTO.getCompanyName());
		System.out.println("AccountType=" + getManagedBean().getAccType());
		System.out.println("suppAdminIdentificationNo="
				+ getManagedBean().getSuppAdminIdentificationNo());
		String accTypeStr = StringUtils.EMPTY;

		try {

			
			
			SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO()
					.retrieveNewSupplierAndPersonnelsByBusRegNoAndName(reqDTO);
			System.out.println("---------1");

			if (resDTO.getSmSupplier() != null
					&& resDTO.getSmSupplier().getSmAppl() != null
					&& !EntityHelper.isCollectionEmpty(resDTO.getSmSupplier()
							.getSmAppl().getSmPersonnels())) {
				System.out.println("---------2");
				
				selectedPersonnel = getAdminPersonnel(resDTO.getSmSupplier());
				System.out.println("getAminPersonnel-------"+selectedPersonnel);
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				if (selectedPersonnel ==null) {
					System.out.println("Error Message(missing data in DB)1="+MessageConst.ERROR_721070);
					throw new EpException(MessageConst.ERROR_721070);
					
				} else {
					// check the status of softcert request
					if(selectedPersonnel.getSoftcertFlg()==(DbValueConst.RECORD_STATUS_EXPIRED)){
						System.out.println("Error Message softcertFlg 2="+MessageConst.ERROR_721071);
						throw new EpException(MessageConst.ERROR_721071);
					}
					//check new MOF Account (Contractor/Consultant) pending supplier application
					if(resDTO.getSmSupplier().getSmAppl().getRecordStatusFlg()==(DbValueConst.RECORD_STATUS_EXPIRED)){
						
						System.out.println("Error Message MOF error="+MessageConst.ERROR_721041);
						throw new EpException(MessageConst.ERROR_721041,new String[]{resDTO.getSmSupplier().getSmAppl().getApplNo(),
								resDTO.getSmSupplier().getSmAppl().getPmStatus().getPmStatusDescs().get(0).getLanguageCode()});
						
					}

					if (selectedPersonnel.getIdentificationNo().equals(
							getManagedBean().getSuppAdminIdentificationNo())
							&& selectedPersonnel
									.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
						
						
						
					if(getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)){
						accTypeStr=SmCommonUtil.getMessage("lbl.sm.basic.account.basic", null);
						
						System.out.println("accTypeStr-------1=="+accTypeStr);
						
					}else if(getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
						accTypeStr=SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor", null);
						
						System.out.println("accTypeStr-------2=="+accTypeStr);
						
					}else if(getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
						accTypeStr=SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant", null);
						
						System.out.println("accTypeStr-------3=="+accTypeStr);
					}else{
						getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.STYPE_GOVT_SELLER);
						accTypeStr=SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
						System.out.println("accTypeStr-------4=="+accTypeStr);
					}
						
						
						String msg = SmCommonUtil.getMessage("lbl.sm.req.cancel.confirmation.msg", new String[] {accTypeStr});
						getManagedBean().setAccType(msg);
						
//						getManagedBean().setConformPopUpMsg(SmCommonUtil.getMessage(
//								"lbl.sm.req.cancel.confirmation.msg",
//								new String[] {this.getManagedBean().getAccType() }));

						RequestContext.getCurrentInstance().execute(
								"confirmDialog.show();");
						
						
						
						
						

					} else {
						System.out.println("Error Message(incorrect Identification number)3="+MessageConst.ERROR_721070);
						throw new EpException(MessageConst.ERROR_721070);
					}
				}
			} else {
				System.out.println("Error Message(incorrect Regno/company name)4="+MessageConst.ERROR_721070);
				throw new EpException(MessageConst.ERROR_721070);
			}
			
			
		}finally {
			
		} 
		/*
		 * catch (Exception ex) {
		 * System.out.println("Error Message 5="+MessageConst.ERROR_721070);
		 * throw new EpException(MessageConst.ERROR_721070); }
		 */
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
		System.out.println("AccountType1=" + getManagedBean().getAccType());
		System.out.println("AccountType2=" + getManagedBean().getAccType());
		if (getManagedBean().getAccType() != null) {
			System.out.println("1------AccountType="
					+ getManagedBean().getAccType());
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
				System.out.println("2----------AccountType="
						+ getManagedBean().getAccType());
				getManagedBean().setAccType(
						SupplierManagementConst.STYPE_GOVT_SELLER);
				return cancelG2GAccountRegistration;
			}
			return cancelBasicMofAccountRegistration;

		}

		return cancelPreRegistration;
	}

	public void cancelDeleteAction() {
		// set Record Status=2(Cancelled)
		IPersonnelSO personnelSO = SmServiceLookup.getPersonnelSO();
		SmSupplier supplier = this.getManagedBean().getSmSupplier();
		SmPersonnel currentPersonnel = null;
		System.out.println("personnel value1="+supplier.getSmAppl().getApplId());
		System.out.println("personnel value2="+supplier.getSmAppl().getSmPersonnels());
		for (SmPersonnel personnel : supplier.getSmAppl().getSmPersonnels()) {		
			if (this.getManagedBean().getSuppAdminIdentificationNo()
					.equalsIgnoreCase(personnel.getIdentificationNo())) {
				currentPersonnel = personnel;
				break;
			}
		}
		if (currentPersonnel != null) {
			PersonnelReqDTO anotherReqDTO = new PersonnelReqDTO();

			anotherReqDTO.setSmPersonnel(currentPersonnel);
			PersonnelResDTO pResDTO = personnelSO.inactiveSupplierRole(anotherReqDTO);
			//System.out.println("personal ID======="+pResDTO.getSmPersonnel().getPersonnelId());
			cancelReasonInfo();
		} else {
			throw new EpException("Missing personnel");
		}

	}

	private void cancelReasonInfo() {
		System.out.println("getManagedBean().getSmAppl().getApplId():="
				+ getManagedBean().getSmAppl().getApplId());
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocType(ParameterConst.SM_APPLN_RMRK_CANCEL_NEW_REGISTRATION_INPROGRESS);
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());

		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO()
				.retrieveSmRemarksByDocument(reqDTO);

		if (resDTO.getRemarksList() != null
				&& resDTO.getRemarksList().size() > 0) {
			SmRemark remark = resDTO.getRemarksList().get(0);
			getManagedBean().setCancellationReason(remark.getDocType());

		}
	}
}