package my.ep.web.backingbean.sm.compexp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * @redmine #9299 
 */
@ManagedBean(name = "smCompExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;
	private static String updateInfoTncXhtml = "updateInfoTnc.xhtml";
	private static String updateInfoMainXhtml = "updateInfoMain.xhtml";
	private static String updateDraftMainXhtml = "updateDraftMain.xhtml";
	private static String updateDraftEditXhtml = "updateDraftEdit.xhtml";
	private static String lblDraftHeaderSm = "lbl.sm.comp.exp.update.draft.edit.header.sm";
	private static String lblClientTypeFed = "lbl.sm.comp.exp.update.federal";
	private static String lblClientTypeNonFed = "lbl.sm.comp.exp.update.non.federal";	

	/**
	 * Prerender for Update Info
	 */
	public void preRenderUpdateInfo() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage(updateInfoTncXhtml, "", null, null);
	}

	/**
	 * Redirect to Update Info main screen
	 */
	public void proceedAfterUpdateTnC() {
		this.getManagedBean().setDraftExist(checkDraftExist());
		SmCommonUtil.redirectToPage(updateInfoMainXhtml, "", null, null);
	}

	private boolean checkDraftExist() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSupplier().getSupplierId());
		return smCompExpSo().checkDraftExist(reqDTO);
	}

	private ISmCompExpSO smCompExpSo() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
	}

	/**
	 * Redirect to Update Draft list screen
	 */
	public void redirectToDraftMain() {
		this.getManagedBean().getDraftSmDataModel().reloadData();
		SmCommonUtil.redirectToPage(updateDraftMainXhtml, "", null, null);
	}

	/**
	 * Redirect to Sm Company Experience Draft update screen
	 * 
	 * @param draft SmCompExpDraft
	 */
	public void redirectToDraftEdit(SmCompExpDraft draft) {
		this.getManagedBean().setDraftUpdate(true);
		initSelectedDraft(draft);
		draft = this.getManagedBean().getDraft();
		
		if(draft.getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			this.getManagedBean().setEditHeader(lblDraftHeaderSm);
		}
		
		if(null != draft.getClientType() && !draft.getClientType().isEmpty()){
			this.getManagedBean().setDisableClienType(true);
		} else {
			this.getManagedBean().setDisableClienType(false);
		}
		initClientType();
		
		SmCommonUtil.redirectToPage(updateDraftEditXhtml, "", null, null);
	}

	private void initSelectedDraft(SmCompExpDraft draft) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraft(draft);
		this.getManagedBean().setDraft(smCompExpSo().retrieveDraft(reqDTO).getDraft());
	}	

	private void initClientType() {		
		List<SelectItem> items = new ArrayList<SelectItem>(); 
		
		SelectItem clientTypeFederal = new SelectItem();
		clientTypeFederal.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
		clientTypeFederal.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		items.add(clientTypeFederal);

		SelectItem clientTypeNonFed = new SelectItem();
		clientTypeNonFed.setLabel(SmCommonUtil.getMessage(lblClientTypeNonFed, null));
		clientTypeNonFed.setValue(ParameterConst.CLIENT_TYPE_NON_FEDERAL);
		items.add(clientTypeNonFed);
		
		this.getManagedBean().setClientTypeItems(items);
	}

	/**
	 * Listener for clienType
	 */
	public void clientTypeChange() {
		String oriAgencyName = this.getManagedBean().getDraft().getOriAgencyName();
		if (this.getManagedBean().getDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_NON_FEDERAL)) {
			if (null != oriAgencyName && !oriAgencyName.isEmpty()) {
				this.getManagedBean().getDraft().setAgencyName(oriAgencyName);
			}
		} else if (this.getManagedBean().getDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			this.getManagedBean().getDraft().setAgencyName(null);
		}
	}

}