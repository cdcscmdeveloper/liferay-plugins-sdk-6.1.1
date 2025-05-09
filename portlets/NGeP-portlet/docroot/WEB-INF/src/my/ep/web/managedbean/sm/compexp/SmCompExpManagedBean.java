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
@ManagedBean(name = "compExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();

	private SmSupplier supplier;
	private boolean agreeTnC;
	private boolean draftExist;
	private transient IPagingDataModel<SmCompExpDraft> draftSmDataModel = new DraftSmPagingDataModel();
	private transient IPagingDataModel<SmCompExpDraft> draftCtDataModel = new DraftCtPagingDataModel();
	private SmCompExpDraft selectedDraft;
	private String headerTitle;
	private boolean disableClienType;
	private Date maxEffDate;
	private Date contractEffDateTemp;
	private Date minExpDate;
	private List<SmCompExpMasterDoc> compExpDocList;
	private String compExpFilePath;
	private SmCompExpMasterDoc compExpDoc;
	private List<SmCompExpCatDraft> compExpCatCodeList;

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
			super("draftSmDataTableId", true);
		}

		@Override
		public ISearchResultBean<SmCompExpDraft> pageChangeListener(ISearchBean<SmCompExpDraft> searchBean) {

			SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setSourceType(ParameterConst.SOURCE_TYPE_ID_SM);

			try {
				ISmCompExpSO compExpSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
				return compExpSo.retriveCompExpDraftList(reqDTO, searchBean).getDraftResultBean();
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

	private class DraftCtPagingDataModel extends PagingDataModel<SmCompExpDraft> {

		private static final long serialVersionUID = 1L;

		public DraftCtPagingDataModel() {
			super("draftCtDataTableId", true);
		}

		@Override
		public ISearchResultBean<SmCompExpDraft> pageChangeListener(ISearchBean<SmCompExpDraft> searchBean) {

			SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setSourceType(ParameterConst.SOURCE_TYPE_ID_CT);

			try {
				ISmCompExpSO compExpSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
				return compExpSo.retriveCompExpDraftList(reqDTO, searchBean).getDraftResultBean();
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve", supplier.getSupplierId().toString(),
						"ISmCompExpSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}

	}

	public IPagingDataModel<SmCompExpDraft> getDraftCtDataModel() {
		return draftCtDataModel;
	}

	public void setDraftCtDataModel(IPagingDataModel<SmCompExpDraft> draftCtDataModel) {
		this.draftCtDataModel = draftCtDataModel;
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

	public Date getMaxEffDate() {
		return maxEffDate;
	}

	public void setMaxEffDate(Date maxEffDate) {
		this.maxEffDate = maxEffDate;
	}

	public Date getContractEffDateTemp() {
		return contractEffDateTemp;
	}

	public void setContractEffDateTemp(Date contractEffDateTemp) {
		this.contractEffDateTemp = contractEffDateTemp;
	}

	public Date getMinExpDate() {
		return minExpDate;
	}

	public void setMinExpDate(Date minExpDate) {
		this.minExpDate = minExpDate;
	}

	public List<SmCompExpMasterDoc> getCompExpDocList() {
		return compExpDocList;
	}

	public void setCompExpDocList(List<SmCompExpMasterDoc> compExpDocList) {
		this.compExpDocList = compExpDocList;
	}

	public String getCompExpFilePath() {
		return compExpFilePath;
	}

	public void setCompExpFilePath(String compExpFilePath) {
		this.compExpFilePath = compExpFilePath;
	}

	public SmCompExpMasterDoc getCompExpDoc() {
		return compExpDoc;
	}

	public void setCompExpDoc(SmCompExpMasterDoc compExpDoc) {
		this.compExpDoc = compExpDoc;
	}

	public List<SmCompExpCatDraft> getCompExpCatCodeList() {
		return compExpCatCodeList;
	}

	public void setCompExpCatCodeList(List<SmCompExpCatDraft> compExpCatCodeList) {
		this.compExpCatCodeList = compExpCatCodeList;
	}

}