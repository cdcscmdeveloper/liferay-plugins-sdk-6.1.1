package my.ep.web.backingbean.sm.compexp;

import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.compexp.SmCompExpDraftReqDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * BackingBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "compExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Prerender method for screen Update Company Experience Information
	 */
	public void preRenderCompExp() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage("smCompExpTnc.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information screen
	 */
	public void proceedAfterAcceptTnC() {
		this.getManagedBean().setDraftExist(checkCompExpDraftExist());
		SmCommonUtil.redirectToPage("smCompExpMain.xhtml", "", null, null);
	}

	private ISmCompExpSO getCompExpSo() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
	}

	private boolean checkCompExpDraftExist() {
		SmCompExpDraftReqDTO reqDTO = new SmCompExpDraftReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSupplier().getSupplierId());
		return getCompExpSo().checkCompExpDraftExist(reqDTO);
	}

	/**
	 * To redirect to SM Update Company Experience Information Draft screen
	 */
	public void redirectToDraft() {
		SmCommonUtil.redirectToPage("smCompExpDraft.xhtml", "", null, null);
	}

	/**
	 * To show to draft update popup
	 * 
	 * @param draft
	 * @param fromSM
	 * @param fromCT
	 */
	public void redirectToDraftUpdate(SmCompExpDraft draft) {
		SmCompExpDraftReqDTO reqDTO = new SmCompExpDraftReqDTO();
		reqDTO.setDraftId(draft.getCompExpDraftId());
		this.getManagedBean().setSelectedDraft(getCompExpSo().retrieveCompExpDraft(reqDTO).getExpDraf());
		if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.header.sm", null));
		} else if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_CT)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.header.ct", null));
		}
		if (null == this.getManagedBean().getSelectedDraft().getClientType() || this.getManagedBean().getSelectedDraft().getClientType().isEmpty()) {
			this.getManagedBean().setDisableClienType(false);
		} else {
			this.getManagedBean().setDisableClienType(true);
		}
		if (null != this.getManagedBean().getSelectedDraft().getContractEffDate()) {
			this.getManagedBean().setContractEffDateTemp(this.getManagedBean().getSelectedDraft().getContractEffDate());
		} else {
			this.getManagedBean().setContractEffDateTemp(null);
		}
		dateChange();
		SmCommonUtil.redirectToPage("smCompExpDraftUpdate.xhtml", "", null, null);
	}

	public void clientTypeChange() {
		if (this.getManagedBean().getSelectedDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_NON_FEDERAL)) {
			if (null != this.getManagedBean().getSelectedDraft().getOriAgencyName()
					&& !this.getManagedBean().getSelectedDraft().getOriAgencyName().isEmpty()) {
				this.getManagedBean().getSelectedDraft().setAgencyName(this.getManagedBean().getSelectedDraft().getOriAgencyName());
			}
		} else if (this.getManagedBean().getSelectedDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			this.getManagedBean().getSelectedDraft().setAgencyName(null);
		}
	}

	public void selectAgencyAction(PmOrgProfile orgProfile) {
		this.getManagedBean().getSelectedDraft().setAgencyName(orgProfile.getPmOrgValidity().getOrgName());
		this.getManagedBean().getSelectedDraft().setOrgProfile((orgProfile));
	}

	public void trimFileReferenceNo() {
		if (null != this.getManagedBean().getSelectedDraft().getFileReferenceNo()
				&& !this.getManagedBean().getSelectedDraft().getFileReferenceNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedDraft().getFileReferenceNo();
			fileNo = fileNo.replaceAll("\\s+", "");
			fileNo = fileNo.replaceAll("^[^a-zA-Z0-9]+", "");
			fileNo = fileNo.replaceAll("[^a-zA-Z0-9]+$", "");
			this.getManagedBean().getSelectedDraft().setFileReferenceNo(fileNo);
		}
	}

	public void trimFileExtensionNo() {
		if (null != this.getManagedBean().getSelectedDraft().getFileExtensionNo()
				&& !this.getManagedBean().getSelectedDraft().getFileExtensionNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedDraft().getFileExtensionNo();
			fileNo = fileNo.replaceAll("\\s+", "");
			fileNo = fileNo.replaceAll("[^a-zA-Z0-9]+", "");
			this.getManagedBean().getSelectedDraft().setFileExtensionNo(fileNo);
		}
	}

	public void dateChange() {
		if (null != this.getManagedBean().getSelectedDraft().getContractExpDate()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.getManagedBean().getSelectedDraft().getContractExpDate());
			cal.add(Calendar.DATE, -1);
			Date maxEffDate = cal.getTime();
			this.getManagedBean().setMaxEffDate(maxEffDate);
		} else {
			this.getManagedBean().setMaxEffDate(null);
		}
		if (null != this.getManagedBean().getContractEffDateTemp()) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.getManagedBean().getContractEffDateTemp());
			cal.add(Calendar.DATE, +1);
			Date minExpDate = cal.getTime();
			this.getManagedBean().setMinExpDate(minExpDate);
		} else {
			this.getManagedBean().setMinExpDate(null);
		}
		Date curDate = new Date();
		this.getManagedBean().getSelectedDraft().setContractStatus(null);
		if (null != this.getManagedBean().getSelectedDraft().getContractExpDate() && null != this.getManagedBean().getContractEffDateTemp()) {
			if (curDate.before(this.getManagedBean().getContractEffDateTemp())) {
				this.getManagedBean().getSelectedDraft().setContractStatus(ParameterConst.CONTRACT_STATUS_INACTIVE);
			} else if ((curDate.equals(this.getManagedBean().getContractEffDateTemp()) || curDate.after(this.getManagedBean()
					.getContractEffDateTemp())) && curDate.before(this.getManagedBean().getSelectedDraft().getContractExpDate())) {
				this.getManagedBean().getSelectedDraft().setContractStatus(ParameterConst.CONTRACT_STATUS_EFFECTIVE);
			} else if (curDate.after(this.getManagedBean().getSelectedDraft().getContractExpDate())) {
				this.getManagedBean().getSelectedDraft().setContractStatus(ParameterConst.CONTRACT_STATUS_EXPIRED);
			}
		}
	}
}