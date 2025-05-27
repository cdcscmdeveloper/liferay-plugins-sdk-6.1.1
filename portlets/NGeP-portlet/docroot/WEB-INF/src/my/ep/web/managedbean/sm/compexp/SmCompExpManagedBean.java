package my.ep.web.managedbean.sm.compexp;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmCompExpCatDraft;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmCompExpMasterDoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * ManagedBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smCompExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();

	private SmSupplier supplier;
	private boolean agreeTnC;
	private boolean draftExist;
	private transient IPagingDataModel<SmCompExpDraft> draftSmDataModel = new DraftSmPagingDataModel();
	private SmCompExpDraft selectedDraft;
	private String headerTitle;
	private boolean disableClienType;
	private Date contractEffDateTemp;
	private Date maxEffDate;
	private Date minExpDate;
	private List<SmCompExpMasterDoc> smCompExpDocList;
	private String smCompExpFilePath;
	private List<SmCompExpCatDraft> smCompExpCatDraftList;
	private boolean smCompExpCatDraftListEmpty;
	private boolean smCompExpCatDrafNotComplete;
	private List<PmCategoryL1> supplierMainCatList;
	private List<PmCategoryL2> supplierSubCatList;
	private List<PmCategoryL3> supplierSubDivCatList;
	private boolean selectedAll;
	private boolean selected;
	private List<SmCompExpCatDraft> selectedSmCompExpCatDraft;
	private boolean agreeDeclare;
	private SmCompExpMasterDoc smCompExpDoc;

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	public boolean isAgreeTnC() {
		return agreeTnC;
	}

	public void setAgreeTnC(boolean agreeTnC) {
		this.agreeTnC = agreeTnC;
	}

	public boolean isDraftExist() {
		return draftExist;
	}

	public void setDraftExist(boolean draftExist) {
		this.draftExist = draftExist;
	}

	private class DraftSmPagingDataModel extends PagingDataModel<SmCompExpDraft> {

		private static final long serialVersionUID = 1L;

		public DraftSmPagingDataModel() {
			super("draftSmDataTable", true);
		}

		@Override
		public ISearchResultBean<SmCompExpDraft> pageChangeListener(ISearchBean<SmCompExpDraft> searchBean) {

			SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setSourceType(ParameterConst.SOURCE_TYPE_ID_SM);

			try {
				ISmCompExpSO smCompExpSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
				return smCompExpSo.retriveSmCompExpDraftList(reqDTO, searchBean).getDraftResultBean();
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve", supplier.getSupplierId().toString(),
						"ISmCompExpSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}

	}

	public IPagingDataModel<SmCompExpDraft> getDraftSmDataModel() {
		return draftSmDataModel;
	}

	public void setDraftSmDataModel(IPagingDataModel<SmCompExpDraft> draftSmDataModel) {
		this.draftSmDataModel = draftSmDataModel;
	}

	public SmCompExpDraft getSelectedDraft() {
		return selectedDraft;
	}

	public void setSelectedDraft(SmCompExpDraft selectedDraft) {
		this.selectedDraft = selectedDraft;
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}

	public boolean isDisableClienType() {
		return disableClienType;
	}

	public void setDisableClienType(boolean disableClienType) {
		this.disableClienType = disableClienType;
	}

	public Date getContractEffDateTemp() {
		return contractEffDateTemp;
	}

	public void setContractEffDateTemp(Date contractEffDateTemp) {
		this.contractEffDateTemp = contractEffDateTemp;
	}

	public Date getMaxEffDate() {
		return maxEffDate;
	}

	public void setMaxEffDate(Date maxEffDate) {
		this.maxEffDate = maxEffDate;
	}

	public Date getMinExpDate() {
		return minExpDate;
	}

	public void setMinExpDate(Date minExpDate) {
		this.minExpDate = minExpDate;
	}

	public List<SmCompExpMasterDoc> getSmCompExpDocList() {
		return smCompExpDocList;
	}

	public void setSmCompExpDocList(List<SmCompExpMasterDoc> smCompExpDocList) {
		this.smCompExpDocList = smCompExpDocList;
	}

	public String getSmCompExpFilePath() {
		return smCompExpFilePath;
	}

	public void setSmCompExpFilePath(String smCompExpFilePath) {
		this.smCompExpFilePath = smCompExpFilePath;
	}

	public List<SmCompExpCatDraft> getSmCompExpCatDraftList() {
		return smCompExpCatDraftList;
	}

	public void setSmCompExpCatDraftList(List<SmCompExpCatDraft> smCompExpCatDraftList) {
		this.smCompExpCatDraftList = smCompExpCatDraftList;
	}

	public boolean isSmCompExpCatDraftListEmpty() {
		return smCompExpCatDraftListEmpty;
	}

	public void setSmCompExpCatDraftListEmpty(boolean smCompExpCatDraftListEmpty) {
		this.smCompExpCatDraftListEmpty = smCompExpCatDraftListEmpty;
	}

	public boolean isSmCompExpCatDrafNotComplete() {
		return smCompExpCatDrafNotComplete;
	}

	public void setSmCompExpCatDrafNotComplete(boolean smCompExpCatDrafNotComplete) {
		this.smCompExpCatDrafNotComplete = smCompExpCatDrafNotComplete;
	}

	public List<PmCategoryL1> getSupplierMainCatList() {
		return supplierMainCatList;
	}

	public void setSupplierMainCatList(List<PmCategoryL1> supplierMainCatList) {
		this.supplierMainCatList = supplierMainCatList;
	}

	public List<PmCategoryL2> getSupplierSubCatList() {
		return supplierSubCatList;
	}

	public void setSupplierSubCatList(List<PmCategoryL2> supplierSubCatList) {
		this.supplierSubCatList = supplierSubCatList;
	}

	public List<PmCategoryL3> getSupplierSubDivCatList() {
		return supplierSubDivCatList;
	}

	public void setSupplierSubDivCatList(List<PmCategoryL3> supplierSubDivCatList) {
		this.supplierSubDivCatList = supplierSubDivCatList;
	}

	public boolean isSelectedAll() {
		return selectedAll;
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public List<SmCompExpCatDraft> getSelectedSmCompExpCatDraft() {
		return selectedSmCompExpCatDraft;
	}

	public void setSelectedSmCompExpCatDraft(List<SmCompExpCatDraft> selectedSmCompExpCatDraft) {
		this.selectedSmCompExpCatDraft = selectedSmCompExpCatDraft;
	}

	public boolean isAgreeDeclare() {
		return agreeDeclare;
	}

	public void setAgreeDeclare(boolean agreeDeclare) {
		this.agreeDeclare = agreeDeclare;
	}

	public SmCompExpMasterDoc getSmCompExpDoc() {
		return smCompExpDoc;
	}

	public void setSmCompExpDoc(SmCompExpMasterDoc smCompExpDoc) {
		this.smCompExpDoc = smCompExpDoc;
	}

}