package my.ep.web.backingbean.sm.compexp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.dto.sm.compexp.SmCompExpResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmCompExpCatDraft;
import my.ep.persistence.entity.sm.SmCompExpCatMaster;
import my.ep.persistence.entity.sm.SmCompExpCatMasterHistory;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmCompExpMaster;
import my.ep.persistence.entity.sm.SmCompExpMasterDoc;
import my.ep.persistence.entity.sm.SmCompExpMasterHistory;
import my.ep.persistence.entity.sm.SmCompExpMasterHistoryDoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 * BackingBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smCompExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;
	private static String smCompExpTncXhtml = "smCompExpTnc.xhtml";
	private static String smCompExpMainXhtml = "smCompExpMain.xhtml";
	private static String smCompExpDraftXhtml = "smCompExpDraft.xhtml";
	private static String smCompExpDraftUpdateXhtml = "smCompExpDraftUpdate.xhtml";
	private static String filePathDateFormat = "yyyyMMdd_HHmmss";
	private static String smCompExpMasterUpdateXhtml = "smCompExpMasterUpdate.xhtml";
	private static String lblClientTypeFed = "lbl.sm.comp.exp.update.client.type.federal";

	/**
	 * Prerender for Sm Company Experience
	 */
	public void preRenderSmCompExp() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage(smCompExpTncXhtml, "", null, null);
	}

	/**
	 * Redirect to Sm Company Experience main screen
	 */
	public void proceedAfterTnC() {
		this.getManagedBean().setDraftExist(checkSmCompExpDraftExist());
		this.getManagedBean().getMasterFedDataModel().reloadData();
		this.getManagedBean().getMasterNonFedDataModel().reloadData();
		SmCommonUtil.redirectToPage(smCompExpMainXhtml, "", null, null);
	}

	private boolean checkSmCompExpDraftExist() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSupplier().getSupplierId());
		return getSmCompExpSo().checkSmCompExpDraftExist(reqDTO);
	}

	private ISmCompExpSO getSmCompExpSo() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
	}

	/**
	 * Redirect to Sm Company Experience Draft list screen
	 */
	public void redirectToDraft() {
		this.getManagedBean().getDraftSmDataModel().reloadData();
		this.getManagedBean().getDraftCtDataModel().reloadData();
		SmCommonUtil.redirectToPage(smCompExpDraftXhtml, "", null, null);
	}

	/**
	 * Redirect to Sm Company Experience Draft update screen
	 * 
	 * @param draft
	 *            SmCompExpDraft
	 */
	public void redirectToDraftUpdate(SmCompExpDraft draft) {
		this.getManagedBean().setDraftUpdate(true);
		initSelectedDraft(draft);
		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		SelectItem clientTypeFederalItem = new SelectItem();
		clientTypeFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
		clientTypeFederalItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		this.getManagedBean().getClientTypeItems().add(clientTypeFederalItem);
		if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.header.sm", null));
			SelectItem clientTypeNonFederalItem = new SelectItem();
			clientTypeNonFederalItem.setLabel(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.client.type.other", null));
			clientTypeNonFederalItem.setValue(ParameterConst.CLIENT_TYPE_NON_FEDERAL);
			this.getManagedBean().getClientTypeItems().add(clientTypeNonFederalItem);
		} else if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_CT)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.header.ct", null));
		}

		if (null == this.getManagedBean().getSelectedDraft().getClientType() || this.getManagedBean().getSelectedDraft().getClientType().isEmpty()) {
			this.getManagedBean().setDisableClienType(false);
		} else {
			this.getManagedBean().setDisableClienType(true);
		}

		if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			dateChange();
		}

		initSuppDoc();
		initCatDraft();

		this.getManagedBean().setSelectedAll(false);
		this.getManagedBean().setSelected(false);
		this.getManagedBean().setSelectedSmCompExpCatDraft(null);
		this.getManagedBean().setAgreeDeclare(false);

		SmCommonUtil.redirectToPage(smCompExpDraftUpdateXhtml, "", null, null);
	}

	private void initSelectedDraft(SmCompExpDraft draft) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(draft.getCompExpDraftId());
		this.getManagedBean().setSelectedDraft(getSmCompExpSo().retrieveSmCompExpDraft(reqDTO).getSelectedDraft());
	}

	/**
	 * Listener for ContractEffDate and ContractExpDate
	 */
	public void dateChange() {
		Date expDate = new Date();
		Date effDate = new Date();
		Long sourceType = null;
		Integer recordStatusFlg = null;
		if (this.getManagedBean().isDraftUpdate()) {
			expDate = this.getManagedBean().getSelectedDraft().getContractExpDate();
			effDate = this.getManagedBean().getSelectedDraft().getContractEffDate();
			sourceType = this.getManagedBean().getSelectedDraft().getSourceType().getParameterId();
			recordStatusFlg = this.getManagedBean().getSelectedDraft().getRecordStatusFlg();
		} else if (this.getManagedBean().isMasterUpdate()) {
			expDate = this.getManagedBean().getSelectedMaster().getContractExpDate();
			effDate = this.getManagedBean().getSelectedMaster().getContractEffDate();
			sourceType = this.getManagedBean().getSelectedMaster().getSourceType().getParameterId();
			recordStatusFlg = this.getManagedBean().getSelectedMaster().getRecordStatusFlg();
		}

		setMaxEffDate(expDate);
		setMinExpDate(effDate);
		Long contractStatus = null;

		if (null != sourceType && sourceType.equals(ParameterConst.SOURCE_TYPE_ID_CT)
				&& recordStatusFlg.toString().equals(ParameterConst.RECORD_STATUS_INACTIVE)) {
			contractStatus = ParameterConst.CONTRACT_STATUS_TERMINATED;
		}

		if (this.getManagedBean().isDraftUpdate()) {
			this.getManagedBean().getSelectedDraft().setContractStatus(setContractStatus(contractStatus, expDate, effDate));
		} else if (this.getManagedBean().isMasterUpdate()) {
			this.getManagedBean().getSelectedMaster().setContractStatus(setContractStatus(contractStatus, expDate, effDate));
		}

	}

	private void setMinExpDate(Date effDate) {
		if (null != effDate) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(effDate);
			cal.add(Calendar.DATE, +1);
			Date minExpDate = cal.getTime();
			this.getManagedBean().setMinExpDate(minExpDate);
		} else {
			this.getManagedBean().setMinExpDate(null);
		}
	}

	private void setMaxEffDate(Date expDate) {
		if (null != expDate) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(expDate);
			cal.add(Calendar.DATE, 1);
			Date maxEffDate = cal.getTime();
			this.getManagedBean().setMaxEffDate(maxEffDate);
		} else {
			this.getManagedBean().setMaxEffDate(null);
		}
	}

	private Long setContractStatus(Long status, Date expDate, Date effDate) {
		Date curDate = new Date();
		Long contractStatus = status;
		if (null == status && null != expDate && null != effDate) {
			if (curDate.before(effDate)) {
				contractStatus = ParameterConst.CONTRACT_STATUS_INACTIVE;
			} else if ((curDate.equals(effDate) || curDate.after(effDate)) && curDate.before(expDate)) {
				contractStatus = ParameterConst.CONTRACT_STATUS_EFFECTIVE;
			} else if (curDate.after(expDate)) {
				contractStatus = ParameterConst.CONTRACT_STATUS_EXPIRED;
			}
		}
		return contractStatus;
	}

	private void initSuppDoc() {
		List<SmCompExpMasterDoc> smCompExpDocList = new ArrayList<>();
		this.getManagedBean().setSmCompExpDocList(smCompExpDocList);

		String regNo = "";
		if (this.getManagedBean().isDraftUpdate()) {
			regNo = this.getManagedBean().getSelectedDraft().getSmSupplier().getRegNo();
		} else if (this.getManagedBean().isMasterUpdate()) {
			regNo = this.getManagedBean().getSelectedMaster().getSmSupplier().getRegNo();
		}
		String filePath = SmCommonUtil.getUploadFilePath(SupplierManagementConst.SM_COMP_EXP_FILE_PATH) + regNo + "/"
				+ DateUtils.dateToString(filePathDateFormat, Calendar.getInstance().getTime()) + "/";

		this.getManagedBean().setSmCompExpFilePath(filePath);
	}

	private void initCatDraft() {
		List<SmCompExpCatDraft> smCompExpCatDraftList = new ArrayList<>();
		this.getManagedBean().setSmCompExpCatDraftList(smCompExpCatDraftList);

		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(this.getManagedBean().getSelectedDraft().getCompExpDraftId());
		smCompExpCatDraftList = getSmCompExpSo().retrieveSmCompExpCatDraftList(reqDTO).getSmCompExpCatDraftList();

		if (smCompExpCatDraftList.isEmpty()) {
			addSmCompExpCatDraft();
			this.getManagedBean().setSmCompExpCatDraftListEmpty(true);
			getSupplierMainCatList();
		} else {
			this.getManagedBean().setSmCompExpCatDraftList(smCompExpCatDraftList);
			this.getManagedBean().setSmCompExpCatDraftListEmpty(false);
		}
	}

	/**
	 * Add new Sm Company Experience Category Draft
	 */
	public void addSmCompExpCatDraft() {
		SmCompExpCatDraft smCompExpCatDraft = new SmCompExpCatDraft();

		smCompExpCatDraft.setCategoryL1(new PmCategoryL1());
		smCompExpCatDraft.setCategoryL2(new PmCategoryL2());
		smCompExpCatDraft.setCategoryL3(new PmCategoryL3());

		this.getManagedBean().getSmCompExpCatDraftList().add(smCompExpCatDraft);
		this.getManagedBean().setSmCompExpCatDraftNotComplete(true);
	}

	/**
	 * Add new Sm Company Experience Category Draft
	 */
	public void addSmCompExpCatMaster() {
		SmCompExpCatMaster smCompExpCatMaster = new SmCompExpCatMaster();

		smCompExpCatMaster.setCategoryL1(new PmCategoryL1());
		smCompExpCatMaster.setCategoryL2(new PmCategoryL2());
		smCompExpCatMaster.setCategoryL3(new PmCategoryL3());

		this.getManagedBean().getSelectedMaster().getCompExpMasterCats().add(smCompExpCatMaster);
		this.getManagedBean().setSmCompExpCatMasterNotComplete(true);
	}

	/**
	 * Retrieve Supplier Main Code Category List
	 */
	public void getSupplierMainCatList() {
		this.getManagedBean().setSupplierSubCatList(null);
		this.getManagedBean().setSupplierSubDivCatList(null);

		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		SmSupplier supplier = new SmSupplier();
		if (this.getManagedBean().isDraftUpdate()) {
			supplier = this.getManagedBean().getSelectedDraft().getSmSupplier();
		} else if (this.getManagedBean().isMasterUpdate()) {
			supplier = this.getManagedBean().getSelectedMaster().getSmSupplier();
		}
		reqDTO.setSupplierTypeCode(supplier.getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(supplier.getBusinessType());
		reqDTO.setApplId(supplier.getSmAppl().getApplId());

		ICategoryCodeSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);

		if (resDTO != null) {
			this.getManagedBean().setSupplierMainCatList(resDTO.getCategoryL1List());
		} else {
			this.getManagedBean().setSupplierMainCatList(null);
		}
	}

	/**
	 * Listener for clienType
	 */
	public void clientTypeChange() {
		String oriAgencyName = this.getManagedBean().getSelectedDraft().getOriAgencyName();
		if (this.getManagedBean().getSelectedDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_NON_FEDERAL)) {
			if (null != oriAgencyName && !oriAgencyName.isEmpty()) {
				this.getManagedBean().getSelectedDraft().setAgencyName(oriAgencyName);
			}
		} else if (this.getManagedBean().getSelectedDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			this.getManagedBean().getSelectedDraft().setAgencyName(null);
		}
	}

	/**
	 * Set orgProfile from PTJ popup to Sm Company Experience Category Draft
	 * 
	 * @param orgProfile
	 *            PmOrgProfile
	 */
	public void selectAgencyAction(PmOrgProfile orgProfile) {
		if (this.getManagedBean().isDraftUpdate()) {
			this.getManagedBean().getSelectedDraft().setAgencyName(orgProfile.getPmOrgValidity().getOrgName());
			this.getManagedBean().getSelectedDraft().setOrgProfile(orgProfile);
		}
		if (this.getManagedBean().isMasterUpdate()) {
			this.getManagedBean().getSelectedMaster().setAgencyName(orgProfile.getPmOrgValidity().getOrgName());
			this.getManagedBean().getSelectedMaster().setOrgProfile(orgProfile);
		}
	}

	/**
	 * Remove special character from File Reference No
	 */
	public void trimFileReferenceNo() {
		if (this.getManagedBean().isDraftUpdate() && null != this.getManagedBean().getSelectedDraft().getFileReferenceNo()
				&& !this.getManagedBean().getSelectedDraft().getFileReferenceNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedDraft().getFileReferenceNo();
			this.getManagedBean().getSelectedDraft().setFileReferenceNo(cleanFileReferenceNo(fileNo));
		}
		if (this.getManagedBean().isMasterUpdate() && null != this.getManagedBean().getSelectedMaster().getFileReferenceNo()
				&& !this.getManagedBean().getSelectedMaster().getFileReferenceNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedMaster().getFileReferenceNo();
			this.getManagedBean().getSelectedMaster().setFileReferenceNo(cleanFileReferenceNo(fileNo));
		}
	}

	private String cleanFileReferenceNo(String input) {
		String fileNo = input;
		fileNo = fileNo.replaceAll("\\s+", "");
		fileNo = fileNo.replaceAll("^[^azAZ09]+", "");
		fileNo = fileNo.replaceAll("[^azAZ09]+$", "");
		return fileNo;
	}

	/**
	 * Remove special character from File Extension No
	 */
	public void trimFileExtensionNo() {
		if (this.getManagedBean().isDraftUpdate() && null != this.getManagedBean().getSelectedDraft().getFileExtensionNo()
				&& !this.getManagedBean().getSelectedDraft().getFileExtensionNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedDraft().getFileExtensionNo();
			this.getManagedBean().getSelectedDraft().setFileExtensionNo(cleanFileExtensionNo(fileNo));
		}
		if (this.getManagedBean().isMasterUpdate() && null != this.getManagedBean().getSelectedMaster().getFileExtensionNo()
				&& !this.getManagedBean().getSelectedMaster().getFileExtensionNo().isEmpty()) {
			String fileNo = this.getManagedBean().getSelectedMaster().getFileExtensionNo();
			this.getManagedBean().getSelectedMaster().setFileExtensionNo(cleanFileExtensionNo(fileNo));
		}
	}

	private String cleanFileExtensionNo(String input) {
		String fileNo = input;
		fileNo = fileNo.replaceAll("\\s+", "");
		fileNo = fileNo.replaceAll("[^azAZ09]+", "");
		return fileNo;
	}

	/**
	 * Handle Supporting Document file upload event
	 * 
	 * @param event
	 *            FileUploadEvent
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			String filePath = this.getManagedBean().getSmCompExpFilePath();

			SmCompExpMasterDoc docFile = new SmCompExpMasterDoc();
			docFile.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", ""));
			docFile.setFilePath(filePath);
			this.getManagedBean().getSmCompExpDocList().add(docFile);

			commonUploadFile(event, filePath);
		}
	}

	/**
	 * Show Supporting Document file delete confirmation
	 * 
	 * @param doc
	 *            SmCompExpMasterDoc
	 */
	public void onDeleteSupDocFileAttachment(SmCompExpMasterDoc doc) {
		this.getManagedBean().setSmCompExpDoc(doc);
		RequestContext.getCurrentInstance().execute("confirmDelSuppDocDialogWV.show();");
	}

	/**
	 * Handle Supporting Document file delete event
	 */
	public void delSuppDocFileAttachment() {
		SmCompExpMasterDoc suppDoc = this.getManagedBean().getSmCompExpDoc();

		if (null != suppDoc) {
			this.getManagedBean().getSmCompExpDocList().remove(suppDoc);
		}

		this.getManagedBean().setSmCompExpDoc(null);
	}

	/**
	 * Handle Draft Category Code select all event
	 */
	public void selectAllDraftCatCodes() {
		List<SmCompExpCatDraft> selectedList = new ArrayList<>();

		if (this.getManagedBean().isSelectedAll()) {
			this.getManagedBean().setSelected(true);
			// Expected Max Size in loop (30) based on NGEP_PDS database
			for (SmCompExpCatDraft cat : this.getManagedBean().getSmCompExpCatDraftList()) {
				cat.setSelected(true);
				selectedList.add(cat);
			}
		} else {
			this.getManagedBean().setSelected(false);
			// Expected Max Size in loop (30) based on NGEP_PDS database
			for (SmCompExpCatDraft cat : this.getManagedBean().getSmCompExpCatDraftList()) {
				cat.setSelected(false);
				selectedList.remove(cat);
			}
		}

		this.getManagedBean().setSelectedSmCompExpCatDraft(selectedList);
	}

	/**
	 * Handle Master Category Code select all event
	 */
	public void selectAllMasterCatCodes() {
		List<SmCompExpCatMaster> selectedList = new ArrayList<>();

		if (this.getManagedBean().isSelectedAll()) {
			this.getManagedBean().setSelected(true);
			// Expected Max Size in loop (30) based on NGEP_PDS database
			for (SmCompExpCatMaster cat : this.getManagedBean().getSelectedMaster().getCompExpMasterCats()) {
				cat.setSelected(true);
				selectedList.add(cat);
			}
		} else {
			this.getManagedBean().setSelected(false);
			// Expected Max Size in loop (30) based on NGEP_PDS database
			for (SmCompExpCatMaster cat : this.getManagedBean().getSelectedMaster().getCompExpMasterCats()) {
				cat.setSelected(false);
				selectedList.remove(cat);
			}
		}

		this.getManagedBean().setSelectedSmCompExpCatMaster(selectedList);
	}

	/**
	 * Handle Draft Category Code single select event
	 * 
	 * @param index
	 *            int
	 */
	public void selectDraftCatCode(int index) {
		List<SmCompExpCatDraft> selectedList = this.getManagedBean().getSelectedSmCompExpCatDraft();

		if (null == selectedList || selectedList.isEmpty()) {
			selectedList = new ArrayList<>();
		}

		SmCompExpCatDraft selected = this.getManagedBean().getSmCompExpCatDraftList().get(index);

		if (selected.isSelected()) {
			selected.setSelected(true);
			selectedList.add(selected);
		} else {
			this.getManagedBean().setSelectedAll(false);
			selected.setSelected(false);
			selectedList.remove(selected);
		}

		this.getManagedBean().setSelectedSmCompExpCatDraft(selectedList);
	}

	/**
	 * Handle Master Category Code single select event
	 * 
	 * @param index
	 *            int
	 */
	public void selectMasterCatCode(int index) {
		List<SmCompExpCatMaster> selectedList = this.getManagedBean().getSelectedSmCompExpCatMaster();

		if (null == selectedList || selectedList.isEmpty()) {
			selectedList = new ArrayList<>();
		}

		SmCompExpCatMaster selected = this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index);

		if (selected.isSelected()) {
			selected.setSelected(true);
			selectedList.add(selected);
		} else {
			this.getManagedBean().setSelectedAll(false);
			selected.setSelected(false);
			selectedList.remove(selected);
		}

		this.getManagedBean().setSelectedSmCompExpCatMaster(selectedList);
	}

	/**
	 * Retrieve Supplier Sub Code Category List
	 * 
	 * @param categoryL1Id
	 *            Long
	 * @param index
	 *            int
	 */
	public void getSupplierSubCatList(Long categoryL1Id, int index) {
		this.getManagedBean().setSupplierSubDivCatList(null);
		SmSupplier supplier = new SmSupplier();
		if (this.getManagedBean().isDraftUpdate()) {
			this.getManagedBean().getSmCompExpCatDraftList().get(index).getCategoryL2().setCategoryL2Id(null);
			this.getManagedBean().getSmCompExpCatDraftList().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSelectedDraft().getSmSupplier();
		} else if (this.getManagedBean().isMasterUpdate()) {
			this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL2().setCategoryL2Id(null);
			this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSelectedMaster().getSmSupplier();
		}
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		reqDTO.setSupplierTypeCode(supplier.getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(supplier.getBusinessType());
		reqDTO.setApplId(supplier.getSmAppl().getApplId());

		ICategoryCodeSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);

		if (resDTO != null) {
			this.getManagedBean().setSupplierSubCatList(resDTO.getCategoryL2List());
		} else {
			this.getManagedBean().setSupplierSubCatList(null);
		}
	}

	/**
	 * Retrieve Supplier Sub Division Code Category List
	 * 
	 * @param categoryL2Id
	 *            Long
	 * @param index
	 *            int
	 */
	public void getSupplierSubDivCatList(Long categoryL2Id, int index) {
		SmSupplier supplier = new SmSupplier();
		if (this.getManagedBean().isDraftUpdate()) {
			this.getManagedBean().getSmCompExpCatDraftList().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSelectedDraft().getSmSupplier();
		} else if (this.getManagedBean().isMasterUpdate()) {
			supplier = this.getManagedBean().getSelectedMaster().getSmSupplier();
			this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
		}
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		reqDTO.setSupplierTypeCode(supplier.getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(supplier.getBusinessType());
		reqDTO.setApplId(supplier.getSmAppl().getApplId());

		ICategoryCodeSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		if (resDTO != null) {
			this.getManagedBean().setSupplierSubDivCatList(resDTO.getCategoryL3List());
		} else {
			this.getManagedBean().setSupplierSubDivCatList(null);
		}
	}

	/**
	 * Check for duplicate Draft Code Category
	 * 
	 * @param categoryL3Id
	 * @param index
	 *            int
	 */
	public void checkDuplicateDraftCatCode(Long categoryL3Id, int index) {
		int i = 0;
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatDraft cat : this.getManagedBean().getSmCompExpCatDraftList()) {
			if (i != index && cat.getCategoryL3().getCategoryL3Id().equals(categoryL3Id)) {
				this.getManagedBean().getSmCompExpCatDraftList().get(index).getCategoryL3().setCategoryL3Id(null);
				this.getManagedBean().setSmCompExpCatDraftNotComplete(true);
				throw new EpException(MessageConst.ERROR_775222);
			}
			i++;
		}
		this.getManagedBean().setSmCompExpCatDraftNotComplete(false);
	}

	/**
	 * Check for duplicate Draf Code Category
	 * 
	 * @param categoryL3Id
	 * @param index
	 *            int
	 */
	public void checkDuplicateMasterCatCode(Long categoryL3Id, int index) {
		int i = 0;
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatMaster cat : this.getManagedBean().getSelectedMaster().getCompExpMasterCats()) {
			if (i != index && cat.getCategoryL3().getCategoryL3Id().equals(categoryL3Id)) {
				this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
				this.getManagedBean().setSmCompExpCatMasterNotComplete(true);
				throw new EpException(MessageConst.ERROR_775222);
			}
			i++;
		}
		this.getManagedBean().setSmCompExpCatMasterNotComplete(false);
	}

	/**
	 * Delete selected Draft Code Category
	 */
	public void delSmCompExpCatDraft() {
		List<SmCompExpCatDraft> catList = this.getManagedBean().getSmCompExpCatDraftList();
		List<SmCompExpCatDraft> selectedList = this.getManagedBean().getSelectedSmCompExpCatDraft();
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatDraft selected : selectedList) {
			catList.remove(selected);
			if (null == selected.getCategoryL3().getCategoryL3Id()) {
				this.getManagedBean().setSmCompExpCatDraftNotComplete(false);
			}
		}
		if (catList.isEmpty()) {
			addSmCompExpCatDraft();
			this.getManagedBean().setSupplierSubCatList(null);
			this.getManagedBean().setSupplierSubDivCatList(null);

		} else {
			this.getManagedBean().setSmCompExpCatDraftList(catList);
		}
		this.getManagedBean().setSelectedSmCompExpCatDraft(null);
	}

	/**
	 * Delete selected Master Code Category
	 */
	public void delSmCompExpCatMaster() {
		List<SmCompExpCatMaster> catList = this.getManagedBean().getSelectedMaster().getCompExpMasterCats();
		List<SmCompExpCatMaster> selectedList = this.getManagedBean().getSelectedSmCompExpCatMaster();
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatMaster selected : selectedList) {
			catList.remove(selected);
			if (null == selected.getCategoryL3().getCategoryL3Id()) {
				this.getManagedBean().setSmCompExpCatMasterNotComplete(false);
			}
		}
		if (catList.isEmpty()) {
			addSmCompExpCatMaster();
			this.getManagedBean().setSupplierSubCatList(null);
			this.getManagedBean().setSupplierSubDivCatList(null);

		} else {
			this.getManagedBean().getSelectedMaster().setCompExpMasterCats(catList);
		}
		this.getManagedBean().setSelectedSmCompExpCatMaster(null);
	}

	/**
	 * Validate field
	 */
	public void onDeclareDraft() {
		boolean error = false;
		String clienType = this.getManagedBean().getSelectedDraft().getClientType();
		String agencyName = this.getManagedBean().getSelectedDraft().getAgencyName();
		String fileReferenceNo = this.getManagedBean().getSelectedDraft().getFileReferenceNo();
		String fileExtensionNo = this.getManagedBean().getSelectedDraft().getFileExtensionNo();
		Date contractEffDate = this.getManagedBean().getSelectedDraft().getContractExpDate();
		Date contractExpDate = this.getManagedBean().getSelectedDraft().getContractExpDate();
		Long sourceType = this.getManagedBean().getSelectedDraft().getSourceType().getParameterId();
		List<SmCompExpMasterDoc> smCompExpDocList = this.getManagedBean().getSmCompExpDocList();

		error = validateDetail(error, clienType, agencyName, fileReferenceNo, fileExtensionNo);
		error = validateDate(error, contractEffDate, contractExpDate);
		error = validateList(error, sourceType, smCompExpDocList);

		if (!error) {
			if (null != this.getManagedBean().getSelectedDraft().getOrgProfile()) {
				SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
				reqDTO.setPtjId(this.getManagedBean().getSelectedDraft().getOrgProfile().getOrgProfileId());
				reqDTO.setFileReferenceNo(this.getManagedBean().getSelectedDraft().getFileReferenceNo());
				reqDTO.setFileExtensionNo(this.getManagedBean().getSelectedDraft().getFileExtensionNo());
				checkDuplicate(getSmCompExpSo().checkDuplicate(reqDTO));
			} else {
				declareSmComExp();
			}
		}
	}

	private boolean validateDetail(boolean err, String clienType, String agencyName, String fileReferenceNo, String fileExtensionNo) {
		boolean error = err;
		if (null == clienType || clienType.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775223,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == agencyName || agencyName.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775224,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == fileReferenceNo || fileReferenceNo.isEmpty() || null == fileExtensionNo || fileExtensionNo.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775225,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		return error;
	}

	private boolean validateDate(boolean err, Date contractEffDate, Date contractExpDate) {
		boolean error = err;
		if (null == contractEffDate) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775226,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == contractExpDate) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775227,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		return error;
	}

	private boolean validateList(boolean err, Long sourceType, List<SmCompExpMasterDoc> smCompExpDocList) {
		boolean error = err;
		if (!sourceType.equals(ParameterConst.SOURCE_TYPE_ID_CT) && (null == smCompExpDocList || smCompExpDocList.isEmpty())) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775228,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		// Expected Max Size in loop (30) based on NGEP_PDS database
		if (this.getManagedBean().isDraftUpdate()) {
			for (SmCompExpCatDraft cat : this.getManagedBean().getSmCompExpCatDraftList()) {
				if (null == cat.getCategoryL3().getCategoryL3Id()) {
					error = true;
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775229,
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
			}
		}
		if (this.getManagedBean().isMasterUpdate()) {
			for (SmCompExpCatMaster cat : this.getManagedBean().getSelectedMaster().getCompExpMasterCats()) {
				if (null == cat.getCategoryL3().getCategoryL3Id()) {
					error = true;
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775229,
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				}
			}
		}
		return error;
	}

	/**
	 * Delete selected Draft
	 */
	public void delSmCompExpDraft() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(this.getManagedBean().getSelectedDraft().getCompExpDraftId());
		SmCompExpResDTO resDTO;
		resDTO = getSmCompExpSo().deleteDraft(reqDTO);
		if (resDTO.isSuccessful()) {
			redirectToDraft();
		}
	}

	private void declareSmComExp() {

		SmCompExpDraft draft = this.getManagedBean().getSelectedDraft();
		SmCompExpMaster master = new SmCompExpMaster();

		master.setSmSupplier(draft.getSmSupplier());
		master.setSmAppl(draft.getSmSupplier().getSmAppl());
		master.setTitle(draft.getTitle());
		master.setProcumentMode(draft.getProcumentMode());
		master.setOriAgencyName(draft.getOriAgencyName());
		master.setAgencyName(draft.getAgencyName());
		master.setOrgProfile(draft.getOrgProfile());
		master.setFileReferenceNo(draft.getFileReferenceNo());
		master.setFileExtensionNo(draft.getFileExtensionNo());
		master.setLoaNo(draft.getLoaNo());
		master.setContractValue(draft.getContractValue());
		master.setPaymentMethod(draft.getPaymentMethod());
		master.setClientType(draft.getClientType());
		master.setContractEffDate(draft.getContractEffDate());
		master.setContractExpDate(draft.getContractExpDate());
		master.setContractStatus(draft.getContractStatus());
		master.setSourceType(SmApplicationUtil.retrievePmParameterById(draft.getSourceType().getParameterId()));
		for (SmCompExpMasterDoc doc : this.getManagedBean().getSmCompExpDocList()) {
			doc.setCompExpMaster(master);
		}
		master.setCompExpMasterDocs(this.getManagedBean().getSmCompExpDocList());

		List<SmCompExpCatMaster> catMasterList = new ArrayList<>();
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatDraft catDraft : this.getManagedBean().getSmCompExpCatDraftList()) {
			SmCompExpCatMaster catMaster = new SmCompExpCatMaster();
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

			reqDTO.setCategoryL1Id(catDraft.getCategoryL1().getCategoryL1Id());
			catDraft.setCategoryL1(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL1DtlById(reqDTO).getCategoryL1());
			catMaster.setCategoryL1(catDraft.getCategoryL1());
			catMaster.setCategoryL1Name(catDraft.getCategoryL1().getCategoryName());

			reqDTO.setCategoryL2Id(catDraft.getCategoryL2().getCategoryL2Id());
			catDraft.setCategoryL2(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL2DtlById(reqDTO).getCategoryL2());
			catMaster.setCategoryL2(catDraft.getCategoryL2());
			catMaster.setCategoryL2Name(catDraft.getCategoryL2().getCategoryName());

			reqDTO.setCategoryL3Id(catDraft.getCategoryL3().getCategoryL3Id());
			catDraft.setCategoryL3(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL3DtlById(reqDTO).getCategoryL3());
			catMaster.setCategoryL3(catDraft.getCategoryL3());
			catMaster.setCategoryL3Name(catDraft.getCategoryL3().getCategoryName());

			catMaster.setCompExpMaster(master);
			catMasterList.add(catMaster);
		}
		master.setCompExpMasterCats(catMasterList);

		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDeclareMaster(master);

		SmCompExpResDTO resDTO;
		resDTO = getSmCompExpSo().declareDraft(reqDTO);
		if (resDTO.isSuccessful()) {
			deactivateDraft();
			redirectToDraft();
		}
	}

	/**
	 * Show delete draft conformation
	 */
	public void onDeleteDraft() {
		RequestContext.getCurrentInstance().execute("confirmDelDraftDialogWV.show();");
	}

	/**
	 * Delete selected Draft
	 */
	public void deactivateDraft() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setDraftId(this.getManagedBean().getSelectedDraft().getCompExpDraftId());
		SmCompExpResDTO resDTO;
		resDTO = getSmCompExpSo().deactivateDraft(reqDTO);
		if (resDTO.isSuccessful()) {
			redirectToDraft();
		}
	}

	private void checkDuplicate(Long masterId) {
		if (null != masterId) {
			if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_CT)) {
				SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
				reqDTO.setMasterId(masterId);
				SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
				if (resDTO.getMaster().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_CT)) {
					RequestContext.getCurrentInstance().execute("delDuplicateDialogWV.show();");
				} else {
					deactivateMaster(resDTO.getMaster().getCompExpMasterId());
					declareSmComExp();
				}
			} else {
				RequestContext.getCurrentInstance().execute("delDuplicateDialogWV.show();");
			}
		} else {
			declareSmComExp();
		}
	}

	private void deactivateMaster(Long compExpMasterId) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(compExpMasterId);
		getSmCompExpSo().deactivateMaster(reqDTO);
	}

	/**
	 * Initialize Master view popup dialog
	 * 
	 * @param selectedMaster
	 */
	public void initViewPopup(SmCompExpMaster selectedMaster) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(selectedMaster.getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		this.getManagedBean().setSelectedMaster(resDTO.getMaster());

		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		SelectItem clientTypeFederalItem = new SelectItem();
		clientTypeFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
		clientTypeFederalItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		this.getManagedBean().getClientTypeItems().add(clientTypeFederalItem);
	}

	/**
	 * Redirect to Sm Company Experience main screen
	 * 
	 * @param selectedMaster
	 */
	public void redirectToUpdateMaster(SmCompExpMaster selectedMaster) {
		this.getManagedBean().setMasterUpdate(true);
		this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.master.update.fed.header.self.declared", null));

		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(selectedMaster.getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		this.getManagedBean().setSelectedMaster(resDTO.getMaster());

		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		SelectItem clientTypeFederalItem = new SelectItem();
		clientTypeFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
		clientTypeFederalItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		this.getManagedBean().getClientTypeItems().add(clientTypeFederalItem);

		dateChange();

		if (null != this.getManagedBean().getSelectedMaster().getCompExpMasterDocs()
				&& !this.getManagedBean().getSelectedMaster().getCompExpMasterDocs().isEmpty()) {
			this.getManagedBean().setSmCompExpDocList(this.getManagedBean().getSelectedMaster().getCompExpMasterDocs());
			this.getManagedBean().setSmCompExpFilePath(this.getManagedBean().getSmCompExpDocList().get(0).getFilePath());
		} else {
			initSuppDoc();
		}

		getSupplierMainCatList();
		this.getManagedBean().setSmCompExpCatMasterNotComplete(false);
		this.getManagedBean().setSelectedSmCompExpCatMaster(null);

		SmCommonUtil.redirectToPage(smCompExpMasterUpdateXhtml, "", null, null);
	}

	/**
	 * Validate field before save update
	 * 
	 * @param update
	 */
	public void onSaveMaster(boolean update) {
		boolean error = false;
		String clienType = this.getManagedBean().getSelectedMaster().getClientType();
		String agencyName = this.getManagedBean().getSelectedMaster().getAgencyName();
		String fileReferenceNo = this.getManagedBean().getSelectedMaster().getFileReferenceNo();
		String fileExtensionNo = this.getManagedBean().getSelectedMaster().getFileExtensionNo();
		Date contractEffDate = this.getManagedBean().getSelectedMaster().getContractExpDate();
		Date contractExpDate = this.getManagedBean().getSelectedMaster().getContractExpDate();
		Long sourceType = this.getManagedBean().getSelectedMaster().getSourceType().getParameterId();
		List<SmCompExpMasterDoc> smCompExpDocList = this.getManagedBean().getSmCompExpDocList();

		error = validateDetail(error, clienType, agencyName, fileReferenceNo, fileExtensionNo);
		error = validateDate(error, contractEffDate, contractExpDate);
		error = validateList(error, sourceType, smCompExpDocList);

		if (!error && update) {
			updateSmComExpMaster();
		}
	}

	private void updateSmComExpMaster() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(this.getManagedBean().getSelectedMaster().getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		SmCompExpMaster master = resDTO.getMaster();
		SmCompExpMasterHistory history = new SmCompExpMasterHistory();

		history.setSmSupplier(master.getSmSupplier());
		history.setSmAppl(master.getSmSupplier().getSmAppl());
		history.setTitle(master.getTitle());
		history.setProcumentMode(master.getProcumentMode());
		history.setOriAgencyName(master.getOriAgencyName());
		history.setAgencyName(master.getAgencyName());
		history.setOrgProfile(master.getOrgProfile());
		history.setFileReferenceNo(master.getFileReferenceNo());
		history.setFileExtensionNo(master.getFileExtensionNo());
		history.setLoaNo(master.getLoaNo());
		history.setContractValue(master.getContractValue());
		history.setPaymentMethod(master.getPaymentMethod());
		history.setClientType(master.getClientType());
		history.setContractEffDate(master.getContractEffDate());
		history.setContractExpDate(master.getContractExpDate());
		history.setContractStatus(master.getContractStatus());
		history.setSourceType(SmApplicationUtil.retrievePmParameterById(master.getSourceType().getParameterId()));

		List<SmCompExpMasterHistoryDoc> newDocList = new ArrayList<SmCompExpMasterHistoryDoc>();
		for (SmCompExpMasterDoc doc : this.getManagedBean().getSmCompExpDocList()) {
			SmCompExpMasterHistoryDoc newDoc = new SmCompExpMasterHistoryDoc();
			newDoc.setFileName(doc.getFileName());
			newDoc.setFilePath(doc.getFilePath());
			newDoc.setCompExpMasterHistory(history);
			newDocList.add(newDoc);
		}
		history.setCompExpMasterHistoryDocs(newDocList);

		List<SmCompExpCatMasterHistory> newCatList = new ArrayList<>();
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatMaster oldCat : master.getCompExpMasterCats()) {
			SmCompExpCatMasterHistory newCat = new SmCompExpCatMasterHistory();
			CategoryCodeReqDTO reqDTO2 = new CategoryCodeReqDTO();

			reqDTO2.setCategoryL1Id(oldCat.getCategoryL1().getCategoryL1Id());
			oldCat.setCategoryL1(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL1DtlById(reqDTO2).getCategoryL1());
			newCat.setCategoryL1(oldCat.getCategoryL1());
			newCat.setCategoryL1Name(oldCat.getCategoryL1().getCategoryName());

			reqDTO2.setCategoryL2Id(oldCat.getCategoryL2().getCategoryL2Id());
			oldCat.setCategoryL2(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL2DtlById(reqDTO2).getCategoryL2());
			newCat.setCategoryL2(oldCat.getCategoryL2());
			newCat.setCategoryL2Name(oldCat.getCategoryL2().getCategoryName());

			reqDTO2.setCategoryL3Id(oldCat.getCategoryL3().getCategoryL3Id());
			oldCat.setCategoryL3(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL3DtlById(reqDTO2).getCategoryL3());
			newCat.setCategoryL3(oldCat.getCategoryL3());
			newCat.setCategoryL3Name(oldCat.getCategoryL3().getCategoryName());

			newCat.setCompExpMasterHistory(history);
			newCatList.add(newCat);
		}
		history.setCompExpCatMasterHistorys(newCatList);

		SmCompExpReqDTO reqDTO3 = new SmCompExpReqDTO();
		reqDTO3.setHistory(history);

		SmCompExpResDTO resDTO1;
		resDTO1 = getSmCompExpSo().declareDraft(reqDTO);
		if (resDTO.isSuccessful()) {
			deactivateDraft();
			redirectToDraft();
		}
	}

	/**
	 * Show delete master conformation
	 */
	public void onDeleteMaster() {
		RequestContext.getCurrentInstance().execute("confirmDelMasterDialogWV.show();");
	}

	/**
	 * Delete selected Master
	 */
	public void delSmCompExpMaster() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(this.getManagedBean().getSelectedMaster().getCompExpMasterId());
		SmCompExpResDTO resDTO;
		resDTO = getSmCompExpSo().deleteMaster(reqDTO);
		if (resDTO.isSuccessful()) {
			proceedAfterTnC();
		}
	}
}