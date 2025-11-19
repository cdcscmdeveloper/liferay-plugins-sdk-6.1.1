package my.ep.web.backingbean.sm.compexp;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.dto.sm.compexp.SmCompExpResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmCompExpCatDraft;
import my.ep.persistence.entity.sm.SmCompExpCatMaster;
import my.ep.persistence.entity.sm.SmCompExpCatMasterHistory;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmCompExpMaster;
import my.ep.persistence.entity.sm.SmCompExpMasterDoc;
import my.ep.persistence.entity.sm.SmCompExpMasterHistory;
import my.ep.persistence.entity.sm.SmCompExpMasterHistoryDoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.MessageUtils;
import my.ep.util.SLAUtils;
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

import com.liferay.portal.util.PortalUtil;

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
	private static String alertFlag = "?redirect";
	private static String smCompExpAlertXhtml = "smCompExpAlert.xhtml";
	private static String smCompExpMainXhtml = "smCompExpMain.xhtml";
	private static String smCompExpDraftXhtml = "smCompExpDraft.xhtml";
	private static String smCompExpDraftUpdateXhtml = "smCompExpDraftUpdate.xhtml";
	private static String filePathDateFormat = "yyyyMMdd_HHmmss";
	private static String smCompExpMasterUpdateXhtml = "smCompExpMasterUpdate.xhtml";
	private static String lblClientTypeFed = "lbl.sm.comp.exp.update.client.type.federal";
	private static String lblClientTypeNonFed = "lbl.sm.comp.exp.update.client.type.other";
	private static String smCompExpNewXhtml = "smCompExpNew.xhtml";

	/**
	 * Prerender for Company Experience Alert
	 */
	public void preRenderSmCompExpAlert() {
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		if (checkSmCompExpDraftExist()) {
			SmCommonUtil.redirectToPage(smCompExpAlertXhtml, "", null, null);
		}
	}

	/**
	 * Prerender for Sm Company Experience
	 */
	public void preRenderSmCompExp() {
		renewSesionObject();
		HttpServletRequest request = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		if (PortalUtil.getCurrentCompleteURL(request).contains(alertFlag)) {
			this.getManagedBean().setAlertFlag(true);
		}

		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		this.getManagedBean().setConsultant(checkSupplierType());

		if (this.getManagedBean().isAlertFlag()) {
			redirectToDraft();
		} else {
			SmCommonUtil.redirectToPage(smCompExpTncXhtml, "", null, null);
		}
	}

	private boolean checkSupplierType() {
		boolean consultant = false;
		String supplierTypeCode = this.getManagedBean().getSupplier().getSmAppl().getSupplierType();
		if (supplierTypeCode.equals(ParameterConst.SUPPLIER_TYPE_CONSULTANT)) {
			consultant = true;
		}
		return consultant;
	}

	/**
	 * Redirect to Sm Company Experience main screen
	 * 
	 * @throws IOException
	 */
	public void proceedAfterTnC() throws IOException {
		if (this.getManagedBean().isAlertFlag()) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect("./");
		} else {
			this.getManagedBean().setDraftExist(checkSmCompExpDraftExist());
			this.getManagedBean().getMasterFedDataModel().reloadData();
			this.getManagedBean().getMasterNonFedDataModel().reloadData();
			SmCommonUtil.redirectToPage(smCompExpMainXhtml, "", null, null);
		}
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
		this.getManagedBean().getDraftQtDataModel().reloadData();
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
		this.getManagedBean().setMasterUpdate(false);
		this.getManagedBean().setMasterNew(false);
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
		} else if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_QT)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.update.header.qt", null));
		}

		if (null == this.getManagedBean().getSelectedDraft().getClientType() || this.getManagedBean().getSelectedDraft().getClientType().isEmpty()) {
			this.getManagedBean().setDisableClienType(false);
		} else {
			this.getManagedBean().setDisableClienType(true);
			clientTypeChange();
		}

		if (!this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			this.getManagedBean().getSelectedDraft().setPaymentMethod(new PmParameter());
		}

		this.getManagedBean().setTempEffDate(false);
		dateChange();

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
		Long contractStatus = null;
		if (this.getManagedBean().isDraftUpdate()) {
			expDate = this.getManagedBean().getSelectedDraft().getContractExpDate();
			effDate = this.getManagedBean().getSelectedDraft().getContractEffDate();
			contractStatus = this.getManagedBean().getSelectedDraft().getContractStatus();
		} else if (this.getManagedBean().isMasterUpdate()) {
			expDate = this.getManagedBean().getSelectedMaster().getContractExpDate();
			effDate = this.getManagedBean().getSelectedMaster().getContractEffDate();
			contractStatus = this.getManagedBean().getSelectedMaster().getContractStatus();
		} else if (this.getManagedBean().isMasterNew()) {
			expDate = this.getManagedBean().getNewMaster().getContractExpDate();
			effDate = this.getManagedBean().getNewMaster().getContractEffDate();
			contractStatus = this.getManagedBean().getSelectedMaster().getContractStatus();
		}

		if (null == effDate) {
			this.getManagedBean().setTempEffDate(true);
		}

		setMaxEffDate(expDate);
		setMinExpDate(effDate);

		if (!contractStatus.equals(ParameterConst.CONTRACT_STATUS_TERMINATED)) {
			if (this.getManagedBean().isDraftUpdate()) {
				this.getManagedBean().getSelectedDraft().setContractStatus(setContractStatus(contractStatus, expDate, effDate));
			} else if (this.getManagedBean().isMasterUpdate()) {
				this.getManagedBean().getSelectedMaster().setContractStatus(setContractStatus(contractStatus, expDate, effDate));
			} else if (this.getManagedBean().isMasterNew()) {
				this.getManagedBean().getNewMaster().setContractStatus(setContractStatus(contractStatus, expDate, effDate));
			}
		}

	}

	private void setMaxEffDate(Date expDate) {
		if (null != expDate) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(expDate);
			cal.add(Calendar.DATE, -1);
			Date maxEffDate = cal.getTime();
			this.getManagedBean().setMaxEffDate(maxEffDate);
		} else {
			this.getManagedBean().setMaxEffDate(null);
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

	private Long setContractStatus(Long status, Date expDate, Date effDate) {
		Date curDate = new Date();
		Long contractStatus = status;
		if (null != expDate && null != effDate) {
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

		String regNo = "";
		if (this.getManagedBean().isDraftUpdate()) {
			regNo = this.getManagedBean().getSelectedDraft().getSmSupplier().getRegNo();
			this.getManagedBean().setSmCompExpDocList(smCompExpDocList);
		} else if (this.getManagedBean().isMasterUpdate()) {
			regNo = this.getManagedBean().getSelectedMaster().getSmSupplier().getRegNo();
			this.getManagedBean().getSelectedMaster().setCompExpMasterDocs(smCompExpDocList);
		} else if (this.getManagedBean().isMasterNew()) {
			regNo = this.getManagedBean().getNewMaster().getSmSupplier().getRegNo();
			this.getManagedBean().getNewMaster().setCompExpMasterDocs(smCompExpDocList);
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
		smCompExpCatDraftList = filterCatCode(smCompExpCatDraftList, this.getManagedBean().getSupplier().getSmAppl().getApplId());

		if (smCompExpCatDraftList.isEmpty()) {
			addSmCompExpCatDraft();
			this.getManagedBean().setSmCompExpCatDraftListEmpty(true);
			getSupplierMainCatList();
		} else {
			this.getManagedBean().setSmCompExpCatDraftList(smCompExpCatDraftList);
			this.getManagedBean().setSmCompExpCatDraftListEmpty(false);
		}
	}

	private List<SmCompExpCatDraft> filterCatCode(List<SmCompExpCatDraft> catCodeList, Long applId) {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		ISupplierSearchSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		reqDTO.setApplId(applId);
		SupplierSearchResDTO resDTO = reqSO.retrieveSupplierActiveCategoryCodeByApplId(reqDTO);
		for (SmCompExpCatDraft draftCatCode : catCodeList) {
			for (SmSupplierCategory activeCatCode : resDTO.getSmSupplierCategoryList()) {
				if (activeCatCode.getPmCategoryL3().getCategoryL3Id().equals(draftCatCode.getCategoryL3().getCategoryL3Id())) {
					draftCatCode.setActive(true);
					break;
				}
			}
		}
		ListIterator<SmCompExpCatDraft> iter = catCodeList.listIterator();
		while (iter.hasNext()) {
			if (!iter.next().isActive()) {
				iter.remove();
			}
		}
		return catCodeList;
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

		if (this.getManagedBean().isMasterUpdate()) {
			this.getManagedBean().getSelectedMaster().getCompExpMasterCats().add(smCompExpCatMaster);
		} else if (this.getManagedBean().isMasterNew()) {
			this.getManagedBean().getNewMaster().getCompExpMasterCats().add(smCompExpCatMaster);
		}
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
		} else if (this.getManagedBean().isMasterNew()) {
			supplier = this.getManagedBean().getSupplier();
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
		if (this.getManagedBean().isDraftUpdate()) {
			String oriAgencyName = this.getManagedBean().getSelectedDraft().getOriAgencyName();
			if (this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
				if (this.getManagedBean().getSelectedDraft().getClientType().equalsIgnoreCase(ParameterConst.CLIENT_TYPE_FEDERAL)) {
					this.getManagedBean().getSelectedDraft().setAgencyName(null);
					this.getManagedBean().getSelectedDraft().setOrgProfile(null);
				} else {
					this.getManagedBean().getSelectedDraft().setAgencyName(oriAgencyName);
					this.getManagedBean().getSelectedDraft().setOrgProfile(null);
				}
			}
		} else if (this.getManagedBean().isMasterNew()) {
			this.getManagedBean().getNewMaster().setAgencyName(null);
			this.getManagedBean().getNewMaster().setOrgProfile(null);
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
		} else if (this.getManagedBean().isMasterUpdate()) {
			this.getManagedBean().getSelectedMaster().setAgencyName(orgProfile.getPmOrgValidity().getOrgName());
			this.getManagedBean().getSelectedMaster().setOrgProfile(orgProfile);
		} else {
			this.getManagedBean().getNewMaster().setAgencyName(orgProfile.getPmOrgValidity().getOrgName());
			this.getManagedBean().getNewMaster().setOrgProfile(orgProfile);
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
		if (this.getManagedBean().isMasterNew() && null != this.getManagedBean().getNewMaster().getFileReferenceNo()
				&& !this.getManagedBean().getNewMaster().getFileReferenceNo().isEmpty()) {
			String fileNo = this.getManagedBean().getNewMaster().getFileReferenceNo();
			this.getManagedBean().getNewMaster().setFileReferenceNo(cleanFileReferenceNo(fileNo));
		}
	}

	private String cleanFileReferenceNo(String input) {
		String fileNo = input;
		fileNo = fileNo.replaceAll("\\s+", "");
		fileNo = fileNo.replaceAll("^[^a-zA-Z0-9]+", "");
		fileNo = fileNo.replaceAll("[^a-zA-Z0-9]+$", "");
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
		if (this.getManagedBean().isMasterNew() && null != this.getManagedBean().getNewMaster().getFileExtensionNo()
				&& !this.getManagedBean().getNewMaster().getFileExtensionNo().isEmpty()) {
			String fileNo = this.getManagedBean().getNewMaster().getFileExtensionNo();
			this.getManagedBean().getNewMaster().setFileExtensionNo(cleanFileExtensionNo(fileNo));
		}
	}

	private String cleanFileExtensionNo(String input) {
		String fileNo = input;
		fileNo = fileNo.replaceAll("\\s+", "");
		fileNo = fileNo.replaceAll("[^a-zA-Z0-9]+", "");
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
			if (this.getManagedBean().isDraftUpdate()) {
				this.getManagedBean().getSmCompExpDocList().add(docFile);
			} else if (this.getManagedBean().isMasterUpdate()) {
				this.getManagedBean().getSelectedMaster().getCompExpMasterDocs().add(docFile);
			} else if (this.getManagedBean().isMasterNew()) {
				this.getManagedBean().getNewMaster().getCompExpMasterDocs().add(docFile);
			}

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
			if (this.getManagedBean().isDraftUpdate()) {
				this.getManagedBean().getSmCompExpDocList().remove(suppDoc);
			} else if (this.getManagedBean().isMasterUpdate()) {
				this.getManagedBean().getSelectedMaster().getCompExpMasterDocs().remove(suppDoc);
			} else if (this.getManagedBean().isMasterNew()) {
				this.getManagedBean().getNewMaster().getCompExpMasterDocs().remove(suppDoc);
			}
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
		List<SmCompExpCatMaster> catList = new ArrayList<>();
		if (this.getManagedBean().isMasterUpdate()) {
			catList = this.getManagedBean().getSelectedMaster().getCompExpMasterCats();
		} else if (this.getManagedBean().isMasterNew()) {
			catList = this.getManagedBean().getNewMaster().getCompExpMasterCats();
		}

		if (this.getManagedBean().isSelectedAll()) {
			this.getManagedBean().setSelected(true);
			// Expected Max Size in loop (30) based on NGEP_PDS database
			for (SmCompExpCatMaster cat : catList) {
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

		SmCompExpCatMaster selected = new SmCompExpCatMaster();
		if (this.getManagedBean().isMasterUpdate()) {
			selected = this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index);
		} else if (this.getManagedBean().isMasterNew()) {
			selected = this.getManagedBean().getNewMaster().getCompExpMasterCats().get(index);
		}

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
		} else if (this.getManagedBean().isMasterNew()) {
			this.getManagedBean().getNewMaster().getCompExpMasterCats().get(index).getCategoryL2().setCategoryL2Id(null);
			this.getManagedBean().getNewMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSupplier();
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
			this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSelectedMaster().getSmSupplier();
		} else if (this.getManagedBean().isMasterNew()) {
			this.getManagedBean().getNewMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
			supplier = this.getManagedBean().getSupplier();
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
		List<SmCompExpCatMaster> catList = new ArrayList<>();
		if (this.getManagedBean().isMasterUpdate()) {
			catList = this.getManagedBean().getSelectedMaster().getCompExpMasterCats();
		} else if (this.getManagedBean().isMasterNew()) {
			catList = this.getManagedBean().getNewMaster().getCompExpMasterCats();
		}

		int i = 0;
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatMaster cat : catList) {
			if (i != index && cat.getCategoryL3().getCategoryL3Id().equals(categoryL3Id)) {
				if (this.getManagedBean().isMasterUpdate()) {
					this.getManagedBean().getSelectedMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
				} else if (this.getManagedBean().isMasterNew()) {
					this.getManagedBean().getNewMaster().getCompExpMasterCats().get(index).getCategoryL3().setCategoryL3Id(null);
				}
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
		List<SmCompExpCatMaster> catList = new ArrayList<>();
		if (this.getManagedBean().isMasterUpdate()) {
			catList = this.getManagedBean().getSelectedMaster().getCompExpMasterCats();
		} else if (this.getManagedBean().isMasterNew()) {
			catList = this.getManagedBean().getNewMaster().getCompExpMasterCats();
		}
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

		if (!sourceType.equals(ParameterConst.SOURCE_TYPE_ID_QT)) {
			error = validateDetail(error, clienType, agencyName, fileReferenceNo, fileExtensionNo);
		}
		if (sourceType.equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
			error = validateDate(error, contractEffDate, contractExpDate);
		}
		error = validateList(error, sourceType, smCompExpDocList);

		if (!error) {
			if (clienType.equals(ParameterConst.CLIENT_TYPE_FEDERAL)) {
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
		if (this.getManagedBean().isDraftUpdate()) {
			error = validateDraftUpdateList(error, sourceType, smCompExpDocList);
		}
		if (this.getManagedBean().isMasterUpdate()) {
			error = validateMasterUpdateList(error, sourceType);
		}
		return error;
	}

	private boolean validateDraftUpdateList(boolean err, Long sourceType, List<SmCompExpMasterDoc> smCompExpDocList) {
		boolean error = err;
		if (sourceType.equals(ParameterConst.SOURCE_TYPE_ID_SM) && (null == smCompExpDocList || smCompExpDocList.isEmpty())) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775228,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatDraft cat : this.getManagedBean().getSmCompExpCatDraftList()) {
			if (null == cat.getCategoryL3().getCategoryL3Id()) {
				error = true;
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775229,
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		}
		return error;
	}

	private boolean validateMasterUpdateList(boolean err, Long sourceType) {
		boolean error = err;
		if (!sourceType.equals(ParameterConst.SOURCE_TYPE_ID_CT)
				&& (null == this.getManagedBean().getSelectedMaster().getCompExpMasterDocs() || this.getManagedBean().getSelectedMaster()
						.getCompExpMasterDocs().isEmpty())) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775228,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmCompExpCatMaster cat : this.getManagedBean().getSelectedMaster().getCompExpMasterCats()) {
			if (null == cat.getCategoryL3().getCategoryL3Id()) {
				error = true;
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775229,
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
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
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.ERROR_775237, new String[] { this.getManagedBean().getSelectedDraft().getTitle() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
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
		if (null != draft.getPaymentMethod() && null != draft.getPaymentMethod().getParameterId()) {
			master.setPaymentMethod(draft.getPaymentMethod());
		}
		master.setClientType(draft.getClientType());
		master.setContractEffDate(draft.getContractEffDate());
		master.setContractExpDate(draft.getContractExpDate());
		master.setContractStatus(draft.getContractStatus());
		if (this.getManagedBean().isConsultant()) {
			master.setProjectLoc(draft.getProjectLoc());
			master.setWorkServDesc(draft.getWorkServDesc());
			master.setConsultantFee(draft.getConsultantFee());
		}
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
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.ERROR_775236, new String[] { this.getManagedBean().getSelectedDraft().getTitle() },
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			redirectToDraft();
		}
	}

	private void checkDuplicate(Long masterId) {
		if (null != masterId) {
			if (!this.getManagedBean().getSelectedDraft().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
				SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
				reqDTO.setMasterId(masterId);
				SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
				if (!resDTO.getMaster().getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
					RequestContext.getCurrentInstance().execute("delDuplicateDialogWV.show();");
				} else {
					deactivateMaster(resDTO.getMaster().getCompExpMasterId());
					declareSmComExp();
				}
			} else {
				RequestContext.getCurrentInstance().execute("delDuplicateSmDialogWV.show();");
			}
		} else {
			declareSmComExp();
		}
	}

	private void deactivateMaster(Long compExpMasterId) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(compExpMasterId);
		getSmCompExpSo().deleteMaster(reqDTO);
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
	 * Initialize Master view popup dialog
	 * 
	 * @param selectedMaster
	 */
	public void initViewPopupSuppProfile(SmCompExpMaster selectedMaster) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(selectedMaster.getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		this.getManagedBean().setSelectedMaster(resDTO.getMaster());

		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		SelectItem clientTypeFederalOrNonItem = new SelectItem();

		if (selectedMaster.getClientType().equals(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			clientTypeFederalOrNonItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
			clientTypeFederalOrNonItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		} else if (selectedMaster.getClientType().equals(ParameterConst.CLIENT_TYPE_NON_FEDERAL)) {
			clientTypeFederalOrNonItem.setLabel(SmCommonUtil.getMessage(lblClientTypeNonFed, null));
			clientTypeFederalOrNonItem.setValue(ParameterConst.CLIENT_TYPE_NON_FEDERAL);
		}

		this.getManagedBean().getClientTypeItems().add(clientTypeFederalOrNonItem);
	}

	/**
	 * Redirect to Sm Company Experience main screen
	 * 
	 * @param selectedMaster
	 */
	public void redirectToUpdateMaster(SmCompExpMaster selectedMaster) {
		this.getManagedBean().setDraftUpdate(false);
		this.getManagedBean().setMasterUpdate(true);
		this.getManagedBean().setMasterNew(false);
		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		if (selectedMaster.getClientType().equals(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.master.update.fed.header.self.declared", null));
			SelectItem clientTypeFederalItem = new SelectItem();
			clientTypeFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
			clientTypeFederalItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
			this.getManagedBean().getClientTypeItems().add(clientTypeFederalItem);
		} else if (selectedMaster.getClientType().equals(ParameterConst.CLIENT_TYPE_NON_FEDERAL)) {
			this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.master.update.non.fed.header.self.declared", null));
			SelectItem clientTypeNonFederalItem = new SelectItem();
			clientTypeNonFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeNonFed, null));
			clientTypeNonFederalItem.setValue(ParameterConst.CLIENT_TYPE_NON_FEDERAL);
			this.getManagedBean().getClientTypeItems().add(clientTypeNonFederalItem);
		}

		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(selectedMaster.getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		this.getManagedBean().setSelectedMaster(resDTO.getMaster());
		this.getManagedBean().getSelectedMaster().getProcumentMode().getParameterId();

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
	 * @throws IOException
	 */
	public void onSaveMaster(boolean update) throws IOException {
		boolean error = false;
		SmCompExpMaster master = new SmCompExpMaster();
		if (this.getManagedBean().isMasterUpdate()) {
			master = this.getManagedBean().getSelectedMaster();
		} else if (this.getManagedBean().isMasterNew()) {
			master = this.getManagedBean().getNewMaster();
		}
		String clienType = master.getClientType();
		String agencyName = master.getAgencyName();
		String fileReferenceNo = master.getFileReferenceNo();
		String fileExtensionNo = master.getFileExtensionNo();
		error = validateDetail(error, clienType, agencyName, fileReferenceNo, fileExtensionNo);

		String title = master.getTitle();
		BigDecimal contractValue = master.getContractValue();
		PmParameter procumentMode = master.getProcumentMode();
		error = validateNewDetail(error, title, contractValue, procumentMode);

		if (this.getManagedBean().isConsultant()) {
			String projectLoc = master.getProjectLoc();
			String workServDesc = master.getWorkServDesc();
			BigDecimal consultantFee = master.getConsultantFee();
			error = validateNewConsultant(error, projectLoc, workServDesc, consultantFee);
		}

		Date contractEffDate = master.getContractExpDate();
		Date contractExpDate = master.getContractExpDate();
		error = validateDate(error, contractEffDate, contractExpDate);

		Long sourceType = master.getSourceType().getParameterId();
		List<SmCompExpMasterDoc> smCompExpDocList = master.getCompExpMasterDocs();
		error = validateList(error, sourceType, smCompExpDocList);

		if (!error && update) {
			updateSmComExpMaster(false);
		} else if (!error) {
			master.setSmAppl(this.getManagedBean().getSupplier().getSmAppl());			
			for (SmCompExpMasterDoc doc : master.getCompExpMasterDocs()) {
				doc.setCompExpMaster(master);
			}
			for (SmCompExpCatMaster cat : master.getCompExpMasterCats()) {
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				reqDTO.setCategoryL1Id(cat.getCategoryL1().getCategoryL1Id());
				cat.setCategoryL1(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL1DtlById(reqDTO).getCategoryL1());
				cat.setCategoryL1Name(cat.getCategoryL1().getCategoryName());

				reqDTO.setCategoryL2Id(cat.getCategoryL2().getCategoryL2Id());
				cat.setCategoryL2(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL2DtlById(reqDTO).getCategoryL2());
				cat.setCategoryL2Name(cat.getCategoryL2().getCategoryName());

				reqDTO.setCategoryL3Id(cat.getCategoryL3().getCategoryL3Id());
				cat.setCategoryL3(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL3DtlById(reqDTO).getCategoryL3());
				cat.setCategoryL3Name(cat.getCategoryL3().getCategoryName());
				
				cat.setCompExpMaster(master);
			}
			saveMaster(master, clienType);
		}
	}

	private void saveMaster(SmCompExpMaster master, String clienType) throws IOException {
		Long masterId = null;
		if (clienType.equals(ParameterConst.CLIENT_TYPE_FEDERAL)) {
			SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
			reqDTO.setPtjId(master.getOrgProfile().getOrgProfileId());
			reqDTO.setFileReferenceNo(master.getFileReferenceNo());
			reqDTO.setFileExtensionNo(master.getFileExtensionNo());
			masterId = getSmCompExpSo().checkDuplicate(reqDTO);
		}
		if (masterId != null) {
			RequestContext.getCurrentInstance().execute("duplicateDialogWV.show();");
		} else {
			SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
			reqDTO.setDeclareMaster(master);
			SmCompExpResDTO resDTO;
			resDTO = getSmCompExpSo().declareDraft(reqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.ERROR_775240,
								new String[] { master.getTitle() }, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				proceedAfterTnC();
			}

		}
	}

	private boolean validateNewConsultant(boolean err, String projectLoc, String workServDesc, BigDecimal consultantFee) {
		boolean error = err;
		if (null == projectLoc || projectLoc.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775233,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == workServDesc || workServDesc.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775234,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == consultantFee) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775235,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		return error;
	}

	private boolean validateNewDetail(boolean err, String title, BigDecimal contractValue, PmParameter procumentMode) {
		boolean error = err;
		if (null == title || title.isEmpty()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775230,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == contractValue) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775232,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		if (null == procumentMode.getParameterId()) {
			error = true;
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_775231,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		}
		return error;
	}

	/**
	 * Add details to History Table
	 * 
	 * @param delete
	 * @throws IOException
	 */
	public void updateSmComExpMaster(boolean delete) throws IOException {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(this.getManagedBean().getSelectedMaster().getCompExpMasterId());
		SmCompExpResDTO resDTO = getSmCompExpSo().retrieveSmCompExpMaster(reqDTO);
		SmCompExpMaster master = resDTO.getMaster();
		SmCompExpMasterHistory history = new SmCompExpMasterHistory();

		history.setCompExpMaster(master);
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
		if (this.getManagedBean().isConsultant()) {
			history.setProjectLoc(master.getProjectLoc());
			history.setWorkServDesc(master.getWorkServDesc());
			history.setConsultantFee(master.getConsultantFee());
		}
		history.setSourceType(SmApplicationUtil.retrievePmParameterById(master.getSourceType().getParameterId()));
		setActionType(history, delete);

		List<SmCompExpMasterHistoryDoc> histDocList = new ArrayList<>();
		for (SmCompExpMasterDoc doc : master.getCompExpMasterDocs()) {
			SmCompExpMasterHistoryDoc newDoc = new SmCompExpMasterHistoryDoc();
			newDoc.setFileName(doc.getFileName());
			newDoc.setFilePath(doc.getFilePath());
			newDoc.setCompExpMasterHist(history);
			histDocList.add(newDoc);
		}
		history.setCompExpMasterHistDocs(histDocList);

		List<SmCompExpCatMasterHistory> histCatList = new ArrayList<>();
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

			newCat.setCompExpMasterHist(history);
			histCatList.add(newCat);
		}
		history.setCompExpCatMasterHists(histCatList);

		SmCompExpReqDTO reqDTO3 = new SmCompExpReqDTO();
		reqDTO3.setHistory(history);

		SmCompExpResDTO resDTO1;
		resDTO1 = getSmCompExpSo().addHistory(reqDTO3);
		if (resDTO1.isSuccessful()) {
			SmCompExpMaster selectedMaster = this.getManagedBean().getSelectedMaster();
			addHistoryDoc(selectedMaster);
			SmCompExpReqDTO reqDTO4 = new SmCompExpReqDTO();
			reqDTO4.setUpdateMaster(selectedMaster);
			SmCompExpResDTO resDTO2;
			resDTO2 = getSmCompExpSo().updateMaster(reqDTO4);
			if (resDTO2.isSuccessful()) {
				if (delete) {
					delSmCompExpMaster();
				} else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.ERROR_775238,
									new String[] { selectedMaster.getTitle() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
					proceedAfterTnC();
				}
			}
		}
	}

	private void addHistoryDoc(SmCompExpMaster selectedMaster) {
		for (SmCompExpMasterDoc doc : selectedMaster.getCompExpMasterDocs()) {
			if (null == doc.getSmComExpMasterDocId()) {
				doc.setCompExpMaster(selectedMaster);
			}
		}
		for (SmCompExpCatMaster cat : selectedMaster.getCompExpMasterCats()) {
			if (null == cat.getCompExpCatMasterId()) {
				CategoryCodeReqDTO reqDTO4 = new CategoryCodeReqDTO();

				reqDTO4.setCategoryL1Id(cat.getCategoryL1().getCategoryL1Id());
				cat.setCategoryL1(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL1DtlById(reqDTO4).getCategoryL1());
				cat.setCategoryL1Name(cat.getCategoryL1().getCategoryName());

				reqDTO4.setCategoryL2Id(cat.getCategoryL2().getCategoryL2Id());
				cat.setCategoryL2(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL2DtlById(reqDTO4).getCategoryL2());
				cat.setCategoryL2Name(cat.getCategoryL2().getCategoryName());

				reqDTO4.setCategoryL3Id(cat.getCategoryL3().getCategoryL3Id());
				cat.setCategoryL3(SmServiceLookup.getCategoryCodeSO().retrieveCategoryL3DtlById(reqDTO4).getCategoryL3());
				cat.setCategoryL3Name(cat.getCategoryL3().getCategoryName());

				cat.setCompExpMaster(selectedMaster);
			}
		}
	}

	private void setActionType(SmCompExpMasterHistory history, boolean delete) {
		if (delete) {
			history.setActionType(ParameterConst.ACTION_TYPE_DELETE);
		} else {
			history.setActionType(ParameterConst.ACTION_TYPE_UPDATE);
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
	 * 
	 * @throws IOException
	 */
	public void delSmCompExpMaster() throws IOException {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(this.getManagedBean().getSelectedMaster().getCompExpMasterId());
		SmCompExpResDTO resDTO;
		resDTO = getSmCompExpSo().deleteMaster(reqDTO);
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(MessageConst.ERROR_775239, new String[] { this
							.getManagedBean().getSelectedMaster().getTitle() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			proceedAfterTnC();
		}
	}

	/**
	 * Check if history exist
	 * 
	 * @param masterId
	 * @return boolean
	 */
	public boolean checkSmCompExpHistoryExist(Long masterId) {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setMasterId(masterId);
		return getSmCompExpSo().checkSmCompExpHistoryExist(reqDTO);
	}

	/**
	 * Initialize history view popup dialog
	 * 
	 * @param masterId
	 */
	public void initHistPopup(Long masterId) {
		this.getManagedBean().setHistMasterId(masterId);
		this.getManagedBean().getHistoryDataModel().reloadData();
	}

	/**
	 * Redirect to Add New Company Experience
	 */
	public void redirectToAddNew() {
		this.getManagedBean().setDraftUpdate(false);
		this.getManagedBean().setMasterUpdate(false);
		this.getManagedBean().setMasterNew(true);
		this.getManagedBean().setAgreeDeclare(false);

		this.getManagedBean().setHeaderTitle(SmCommonUtil.getMessage("lbl.sm.comp.exp.add.new", null));
		this.getManagedBean().setNewMaster(new SmCompExpMaster());

		this.getManagedBean().setClientTypeItems(new ArrayList<SelectItem>());
		SelectItem clientTypeFederalItem = new SelectItem();
		clientTypeFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeFed, null));
		clientTypeFederalItem.setValue(ParameterConst.CLIENT_TYPE_FEDERAL);
		this.getManagedBean().getClientTypeItems().add(clientTypeFederalItem);
		SelectItem clientTypeNonFederalItem = new SelectItem();
		clientTypeNonFederalItem.setLabel(SmCommonUtil.getMessage(lblClientTypeNonFed, null));
		clientTypeNonFederalItem.setValue(ParameterConst.CLIENT_TYPE_NON_FEDERAL);
		this.getManagedBean().getClientTypeItems().add(clientTypeNonFederalItem);

		this.getManagedBean().getNewMaster().setPaymentMethod(new PmParameter());
		this.getManagedBean().getNewMaster().setProcumentMode(new PmParameter());
		this.getManagedBean().getNewMaster().setSourceType(new PmParameter());
		this.getManagedBean().getNewMaster().getSourceType().setParameterId(ParameterConst.SOURCE_TYPE_ID_SM);

		dateChange();
		
		List<SmCompExpCatMaster> catList = new ArrayList<>();
		this.getManagedBean().getNewMaster().setCompExpMasterCats(catList);
		addSmCompExpCatMaster();
		getSupplierMainCatList();

		this.getManagedBean().getNewMaster().setSmSupplier(this.getManagedBean().getSupplier());
		initSuppDoc();

		SmCommonUtil.redirectToPage(smCompExpNewXhtml, "", null, null);
	}

	/**
	 * Download supporting document
	 * 
	 * @param fileName
	 * @param filePath
	 */
	public void downloadSelectedSuppDoc(String fileName, String filePath) {
		String finalFilePath = filePath + fileName;
		try {
			commonDownload(finalFilePath, fileName);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "download", null, null, getUserIdSessionFromLoginId(), e);
		}
	}
}