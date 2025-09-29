package my.ep.web.managedbean.sm.compexp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @redmine #9299
 */
@ManagedBean(name = "smCompExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();
	private SmSupplier supplier;
	private boolean agreeUpdateTnC;
	private boolean draftExist;
	private transient IPagingDataModel<SmCompExpDraft> draftSmDataModel = new DraftSmPagingDataModel();
	private boolean draftUpdate;
	private SmCompExpDraft draft;
	private String editHeader;
	private List<SelectItem> clientTypeItems;
	private boolean disableClienType;

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	public boolean isAgreeUpdateTnC() {
		return agreeUpdateTnC;
	}

	public void setAgreeUpdateTnC(boolean agreeUpdateTnC) {
		this.agreeUpdateTnC = agreeUpdateTnC;
	}

	public boolean isDraftExist() {
		return draftExist;
	}

	public void setDraftExist(boolean draftExist) {
		this.draftExist = draftExist;
	}

	public IPagingDataModel<SmCompExpDraft> getDraftSmDataModel() {
		return draftSmDataModel;
	}

	public void setDraftSmDataModel(IPagingDataModel<SmCompExpDraft> draftSmDataModel) {
		this.draftSmDataModel = draftSmDataModel;
	}

	public boolean isDraftUpdate() {
		return draftUpdate;
	}

	public void setDraftUpdate(boolean draftUpdate) {
		this.draftUpdate = draftUpdate;
	}

	public SmCompExpDraft getDraft() {
		return draft;
	}

	public void setDraft(SmCompExpDraft draft) {
		this.draft = draft;
	}

	public String getEditHeader() {
		return editHeader;
	}

	public void setEditHeader(String editHeader) {
		this.editHeader = editHeader;
	}

	public List<SelectItem> getClientTypeItems() {
		return clientTypeItems;
	}

	public void setClientTypeItems(List<SelectItem> clientTypeItems) {
		this.clientTypeItems = clientTypeItems;
	}

	public boolean isDisableClienType() {
		return disableClienType;
	}

	public void setDisableClienType(boolean disableClienType) {
		this.disableClienType = disableClienType;
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
				return smCompExpSo.retriveDraftList(reqDTO, searchBean).getDraftResultBean();
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "retrieve", supplier.getSupplierId().toString(),
						"ISmCompExpSO", logKey.getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}

	}

}
