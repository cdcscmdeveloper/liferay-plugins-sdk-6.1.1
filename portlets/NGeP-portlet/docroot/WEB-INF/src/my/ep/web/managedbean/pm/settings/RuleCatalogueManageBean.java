package my.ep.web.managedbean.pm.settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;

import my.ep.biz.service.pm.general.settings.kpi.IKPIStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.dto.pm.general.settings.kpi.KPIReqDTO;
import my.ep.dto.pm.general.settings.kpi.KPIResDTO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.common.PmKpiStatus;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;
import my.ep.persistence.entity.sm.SmSupplier;

import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.BaseManagedBean;

import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "ruleCatalogueMB")
@SessionScoped
public class RuleCatalogueManageBean extends BaseManagedBean {

	private Integer tabActiveIndex = 0;
	private Integer feeExemptionIndex = 0;
	private PmConfigGeneral selectedConfigGeneral;
	private PmConfigRuleValue selectedRuleValue;
	private PmConfigRule selectedRuleType;
	private List<PmConfigRule> configRuleList;
	private String selectedDocumentType;
	private List<PmKpiStatus> kpiStatusList;
	private PmKpiStatus selectedKpiStatus;
	private Date  today = new Date();
	private List<PmFeeExemption> feeExemptionsForOrgAndItem;
	private List<PmFeeExemption> feeExemptionsForComodity;
	private List<PmFeeExemption> feeExemptionsForContract;
	private PmFeeExemption selectedFeeExemption;
	private SmSupplier selectedSmSupplier;
	private CtContractItem selectedCtContractItem;
//	private List<SelectItem> typeCriteria;
	private CmUnspsc selectedComodity;
	private CmItem selectedCmItem;
	private String cmItemName;
	private List<PmFeeExemption> deletedOrgAndItemFeeExemptions;
	private List<PmFeeExemption> deletedComodityFeeExemptions;
	private List<PmFeeExemption> deletedContractFeeExemptions;
	
	public RuleCatalogueManageBean() {
		
	}

//	private PagingDataModel<PmKpiStatus> kpiListDataModel = new kpiPagingDataModel();

//	public PagingDataModel<PmKpiStatus> getKpiListDataModel() {
//		return kpiListDataModel;
//	}
//
//	public void setKpiListDataModel(PagingDataModel<PmKpiStatus> kpiListDataModel) {
//		this.kpiListDataModel = kpiListDataModel;
//	}

	public List<PmKpiStatus> getKpiStatusList() {
		return kpiStatusList;
	}

	public String getCmItemName() {
		return cmItemName;
	}

	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	public List<PmFeeExemption> getFeeExemptionsForOrgAndItem() {
		if(this.feeExemptionsForOrgAndItem == null){
			this.feeExemptionsForOrgAndItem = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForOrgAndItem;
	}

	public void setFeeExemptionsForOrgAndItem(
			List<PmFeeExemption> feeExemptionsForOrgAndItem) {
		this.feeExemptionsForOrgAndItem = feeExemptionsForOrgAndItem;
	}

	public List<PmFeeExemption> getFeeExemptionsForComodity() {
		if(this.feeExemptionsForComodity == null){
			this.feeExemptionsForComodity = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForComodity;
	}

	public void setFeeExemptionsForComodity(
			List<PmFeeExemption> feeExemptionsForComodity) {
		this.feeExemptionsForComodity = feeExemptionsForComodity;
	}

	public List<PmFeeExemption> getFeeExemptionsForContract() {
		if(this.feeExemptionsForContract == null){
			this.feeExemptionsForContract = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForContract;
	}

	public void setFeeExemptionsForContract(
			List<PmFeeExemption> feeExemptionsForContract) {
		this.feeExemptionsForContract = feeExemptionsForContract;
	}

	public void setKpiStatusList(List<PmKpiStatus> kpiStatusList) {
		this.kpiStatusList = kpiStatusList;
	}

	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public PmConfigGeneral getSelectedConfigGeneral() {
		return selectedConfigGeneral;
	}

	public void setSelectedConfigGeneral(PmConfigGeneral selectedConfigGeneral) {
		System.out.println(".....setSelectedConfigGeneral called");
		this.selectedConfigGeneral = selectedConfigGeneral;
	}

	public PmConfigRule getSelectedRuleType() {

		return selectedRuleType;
	}

	public void setSelectedRuleType(PmConfigRule selectedRuleType) {

		this.selectedRuleType = selectedRuleType;
	}

	public Date getToday() {
		return today;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public List<PmConfigRule> getConfigRuleList() {
		return configRuleList;
	}

	public void setConfigRuleList(List<PmConfigRule> configRuleList) {
		this.configRuleList = configRuleList;
	}

	public PmConfigRuleValue getSelectedRuleValue() {
		return selectedRuleValue;
	}

	public void setSelectedRuleValue(PmConfigRuleValue selectedRuleValue) {
		this.selectedRuleValue = selectedRuleValue;
	}

	public String getSelectedDocumentType() {
		return selectedDocumentType;
	}

	public void setSelectedDocumentType(String selectedDocumentType) {
		System.out.println("....................setSelectedDocumentType to="+selectedDocumentType);
		this.selectedDocumentType = selectedDocumentType;
	}

	public PmKpiStatus getSelectedKpiStatus() {
		return selectedKpiStatus;
	}

	public void setSelectedKpiStatus(PmKpiStatus selectedKpiStatus) {
		this.selectedKpiStatus = selectedKpiStatus;
	}

	public PmFeeExemption getSelectedFeeExemption() {
		return selectedFeeExemption;
	}

	public void setSelectedFeeExemption(PmFeeExemption selectedFeeExemption) {
		this.selectedFeeExemption = selectedFeeExemption;
	}

	public SmSupplier getSelectedSmSupplier() {
		return selectedSmSupplier;
	}

	public void setSelectedSmSupplier(SmSupplier selectedSmSupplier) {
		this.selectedSmSupplier = selectedSmSupplier;
	}

	public CtContractItem getSelectedCtContractItem() {
		return selectedCtContractItem;
	}

	public void setSelectedCtContractItem(CtContractItem selectedCtContractItem) {
		this.selectedCtContractItem = selectedCtContractItem;
	}

	public Integer getFeeExemptionIndex() {
		return feeExemptionIndex;
	}

	public void setFeeExemptionIndex(Integer feeExemptionIndex) {
		this.feeExemptionIndex = feeExemptionIndex;
	}
	
//	public List<SelectItem> getTypeCriteria() {
//		if (typeCriteria == null)
//			typeCriteria = new ArrayList<SelectItem>();
//		return typeCriteria;
//	}
//
//	public void setTypeCriteria(List<SelectItem> typeCriteria) {
//		this.typeCriteria = typeCriteria;
//	}

	public CmUnspsc getSelectedComodity() {
		return selectedComodity;
	}

	public void setSelectedComodity(CmUnspsc selectedComodity) {
		this.selectedComodity = selectedComodity;
	}

	public CmItem getSelectedCmItem() {
		return selectedCmItem;
	}

	public void setSelectedCmItem(CmItem selectedCmItem) {
		this.selectedCmItem = selectedCmItem;
	}

	public List<PmFeeExemption> getDeletedOrgAndItemFeeExemptions() {
		if(deletedOrgAndItemFeeExemptions == null){
			deletedOrgAndItemFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedOrgAndItemFeeExemptions;
	}

	public void setDeletedOrgAndItemFeeExemptions(
			List<PmFeeExemption> deletedOrgAndItemFeeExemptions) {
		this.deletedOrgAndItemFeeExemptions = deletedOrgAndItemFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedComodityFeeExemptions() {
		if(deletedComodityFeeExemptions == null){
			deletedComodityFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedComodityFeeExemptions;
	}

	public void setDeletedComodityFeeExemptions(
			List<PmFeeExemption> deletedComodityFeeExemptions) {
		this.deletedComodityFeeExemptions = deletedComodityFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedContractFeeExemptions() {
		if(deletedContractFeeExemptions == null){
			deletedContractFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedContractFeeExemptions;
	}

	public void setDeletedContractFeeExemptions(
			List<PmFeeExemption> deletedContractFeeExemptions) {
		this.deletedContractFeeExemptions = deletedContractFeeExemptions;
	}
	

}
