package my.ep.web.backingbean.sm.compexp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmCompExpCatDraft;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmCompExpMaster;
import my.ep.persistence.entity.sm.SmCompExpMasterDoc;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

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
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
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
		initSelectedDraft(draft);

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
		initDraftDoc();
		initCatDraft();
		this.getManagedBean().setSelectedAll(false);
		this.getManagedBean().setSelected(false);
		this.getManagedBean().setSelectedCompExpCatCode(null);
		this.getManagedBean().setAgreeDeclare(false);
		SmCommonUtil.redirectToPage("smCompExpDraftUpdate.xhtml", "", null, null);
	}

	private void initSelectedDraft(SmCompExpDraft draft) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(draft.getCompExpDraftId());
		this.getManagedBean().setSelectedDraft(getCompExpSo().retrieveCompExpDraft(reqDTO).getExpDraf());
	}

	private void initDraftDoc() {
		List<SmCompExpMasterDoc> compExpDocList = new ArrayList<SmCompExpMasterDoc>();
		this.getManagedBean().setCompExpDocList(compExpDocList);
		String filePath = SmCommonUtil.getUploadFilePath(SupplierManagementConst.SM_COMP_EXP_FILE_PATH)
				+ this.getManagedBean().getSelectedDraft().getSmSupplier().getRegNo() + "/"
				+ DateUtils.dateToString("yyyyMMdd_HHmmss", Calendar.getInstance().getTime()) + "/";
		this.getManagedBean().setCompExpFilePath(filePath);
	}

	private void initCatDraft() {
		List<SmCompExpCatDraft> catCodeList = new ArrayList<SmCompExpCatDraft>();
		this.getManagedBean().setCompExpCatCodeList(catCodeList);
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(this.getManagedBean().getSelectedDraft().getCompExpDraftId());
		catCodeList = getCompExpSo().retrieveCompExpCatDraft(reqDTO).getCompExpCatCodeList();
		if (catCodeList.isEmpty()) {
			addCompExpCat();
			this.getManagedBean().setCompExpCatCodeListEmpty(true);
		} else {
			this.getManagedBean().setCompExpCatCodeList(catCodeList);
			this.getManagedBean().setCompExpCatCodeListEmpty(false);
		}
		getSupplierMainCatList();
	}

	public void selectAllCatCodes() {
		List<SmCompExpCatDraft> selectedList = new ArrayList<SmCompExpCatDraft>();
		if (this.getManagedBean().isSelectedAll()) {
			this.getManagedBean().setSelected(true);
			for (SmCompExpCatDraft cat : this.getManagedBean().getCompExpCatCodeList()) {
				cat.setSelected(true);
				selectedList.add(cat);
			}
		} else {
			this.getManagedBean().setSelected(false);
			for (SmCompExpCatDraft cat : this.getManagedBean().getCompExpCatCodeList()) {
				cat.setSelected(false);
				selectedList.remove(cat);
			}
		}
		this.getManagedBean().setSelectedCompExpCatCode(selectedList);
	}

	public void selectCatCode(int index) {
		List<SmCompExpCatDraft> selectedList = this.getManagedBean().getSelectedCompExpCatCode();
		if (null == selectedList || selectedList.isEmpty()) {
			selectedList = new ArrayList<SmCompExpCatDraft>();
		}
		SmCompExpCatDraft selected = this.getManagedBean().getCompExpCatCodeList().get(index);
		if (selected.isSelected()) {
			selected.setSelected(true);
			selectedList.add(selected);
		} else {
			this.getManagedBean().setSelectedAll(false);
			selected.setSelected(false);
			selectedList.remove(selected);
		}
		this.getManagedBean().setSelectedCompExpCatCode(selectedList);
	}

	public void addCompExpCat() {
		SmCompExpCatDraft newCat = new SmCompExpCatDraft();
		newCat.setCategoryL1(new PmCategoryL1());
		newCat.setCategoryL2(new PmCategoryL2());
		newCat.setCategoryL3(new PmCategoryL3());
		this.getManagedBean().getCompExpCatCodeList().add(newCat);
		this.getManagedBean().setCatCodeNotComplete(true);
	}

	public void delCompExpCat() {
		List<SmCompExpCatDraft> catList = this.getManagedBean().getCompExpCatCodeList();
		List<SmCompExpCatDraft> selectedList = this.getManagedBean().getSelectedCompExpCatCode();
		this.getManagedBean().setCatCodeNotComplete(true);
		for (SmCompExpCatDraft selected : selectedList) {
			catList.remove(selected);
			if (null == selected.getCategoryL3().getCategoryL3Id()) {
				this.getManagedBean().setCatCodeNotComplete(false);
			}
		}
		if (catList.isEmpty()) {
			addCompExpCat();
			this.getManagedBean().setSupplierSubCatList(null);
			this.getManagedBean().setSupplierSubDivCatList(null);

		} else {
			this.getManagedBean().setCompExpCatCodeList(catList);
		}
		this.getManagedBean().setSelectedCompExpCatCode(null);
	}

	public void getSupplierMainCatList() {
		this.getManagedBean().setSupplierSubCatList(null);
		this.getManagedBean().setSupplierSubDivCatList(null);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setSupplierTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getBusinessType());
		reqDTO.setApplId(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getApplId());
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);
		if (resDTO != null) {
			this.getManagedBean().setSupplierMainCatList(resDTO.getCategoryL1List());
		} else {
			this.getManagedBean().setSupplierMainCatList(null);
		}
	}

	public void getSupplierSubCatList(Long categoryL1Id, int index) {
		this.getManagedBean().setSupplierSubDivCatList(null);
		this.getManagedBean().getCompExpCatCodeList().get(index).getCategoryL2().setCategoryL2Id(null);
		this.getManagedBean().getCompExpCatCodeList().get(index).getCategoryL3().setCategoryL3Id(null);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		reqDTO.setSupplierTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getBusinessType());
		reqDTO.setApplId(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getApplId());
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);
		if (resDTO != null) {
			this.getManagedBean().setSupplierSubCatList(resDTO.getCategoryL2List());
		} else {
			this.getManagedBean().setSupplierSubCatList(null);
		}
	}

	public void getSupplierSubDivCatList(Long categoryL2Id, int index) {
		this.getManagedBean().getCompExpCatCodeList().get(index).getCategoryL3().setCategoryL3Id(null);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		reqDTO.setSupplierTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(this.getManagedBean().getSelectedDraft().getSmSupplier().getBusinessType());
		reqDTO.setApplId(this.getManagedBean().getSelectedDraft().getSmSupplier().getSmAppl().getApplId());
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);
		if (resDTO != null) {
			this.getManagedBean().setSupplierSubDivCatList(resDTO.getCategoryL3List());
		} else {
			this.getManagedBean().setSupplierSubDivCatList(null);
		}
	}

	public void checkDuplicateCatCode(Long categoryL3Id, int index) {
		int i = 0;
		for (SmCompExpCatDraft cat : this.getManagedBean().getCompExpCatCodeList()) {
			if (i != index && cat.getCategoryL3().getCategoryL3Id().equals(categoryL3Id)) {
				this.getManagedBean().getCompExpCatCodeList().get(index).getCategoryL3().setCategoryL3Id(null);
				throw new EpException(MessageConst.ERROR_775222);
			}
		}
		this.getManagedBean().setCatCodeNotComplete(false);
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

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			String filePath = this.getManagedBean().getCompExpFilePath();
			SmCompExpMasterDoc docFile = new SmCompExpMasterDoc();
			docFile.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
			docFile.setFilePath(filePath);
			this.getManagedBean().getCompExpDocList().add(docFile);
			commonUploadFile(event, filePath);
		}
	}

	public void onDeleteSupDocFileAttachment(SmCompExpMasterDoc doc) {
		this.getManagedBean().setCompExpDoc(doc);
		RequestContext.getCurrentInstance().execute("confirmDelSuppDocDialogWV.show();");
	}

	public void delSuppDocFileAttachment() {
		SmCompExpMasterDoc suppDoc = this.getManagedBean().getCompExpDoc();
		if (null != suppDoc) {
			this.getManagedBean().getCompExpDocList().remove(suppDoc);
		}
		this.getManagedBean().setCompExpDoc(null);
	}

	public void onDeclare() {
		if (null == this.getManagedBean().getSelectedDraft().getAgencyName() || this.getManagedBean().getSelectedDraft().getAgencyName().isEmpty()) {
			throw new EpException(MessageConst.ERROR_775223);
		}
		if (null == this.getManagedBean().getSelectedDraft().getFileReferenceNo()
				|| this.getManagedBean().getSelectedDraft().getFileReferenceNo().isEmpty()
				|| null == this.getManagedBean().getSelectedDraft().getFileExtensionNo()
				|| this.getManagedBean().getSelectedDraft().getFileExtensionNo().isEmpty()) {
			throw new EpException(MessageConst.ERROR_775224);
		}
		if (null == this.getManagedBean().getSelectedDraft().getContractExpDate()) {
			throw new EpException(MessageConst.ERROR_775225);
		}
		if (null == this.getManagedBean().getCompExpDocList() || this.getManagedBean().getCompExpDocList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_775226);
		}
		for (SmCompExpCatDraft cat : this.getManagedBean().getCompExpCatCodeList()) {
			if (null == cat.getCategoryL3().getCategoryL3Id()) {
				throw new EpException(MessageConst.ERROR_775227);
			}
		}
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setPtjId(this.getManagedBean().getSelectedDraft().getOrgProfile().getOrgProfileId());
		reqDTO.setFileReferenceNo(this.getManagedBean().getSelectedDraft().getFileReferenceNo());
		reqDTO.setFileExtensionNo(this.getManagedBean().getSelectedDraft().getFileExtensionNo());
		if (getCompExpSo().checkDuplicateExist(reqDTO)) {
			RequestContext.getCurrentInstance().execute("delDuplicateDialogWV.show();");
		} else {
			declareComExp();
		}

	}

	public void declareComExp() {

	}

	public void delCompExp() {

	}
}