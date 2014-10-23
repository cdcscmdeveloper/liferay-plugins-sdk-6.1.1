package my.ep.web.managedbean.pp.procurementplan;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

/**
 * Managed Bean for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 20/10/2013
 *
 */
@ManagedBean(name = "pPlanManagedBean")
@SessionScoped
public class ProcurementPlanManagedBean extends BaseManagedBean{

	private ProcurementPlanSearchBean planSearchBean;
	private PpPlan selectedPlan;
	private PpExpenditureItem selectedEpItem; //TODO remove
	private PpProcurementItem selectedPpItem; //TODO remove
	private List<PpExpenditureItem> deleteEpItems;
	private List<PpProcurementItem> deletePpItems;
	private DualListModel<String> approverModel;
	private DualListModel<String> verifierModel;
	private List<PpRejectReason> selectedRejectReasons;
	
	
	
	
	/**
	 * @return the selectedRejectReasons
	 */
	public List<PpRejectReason> getSelectedRejectReasons() {
		return selectedRejectReasons;
	}

	/**
	 * @param selectedRejectReasons the selectedRejectReasons to set
	 */
	public void setSelectedRejectReasons(List<PpRejectReason> selectedRejectReasons) {
		this.selectedRejectReasons = selectedRejectReasons;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the verifierModel
	 */
	public DualListModel<String> getVerifierModel() {
		return verifierModel;
	}

	/**
	 * @param verifierModel the verifierModel to set
	 */
	public void setVerifierModel(DualListModel<String> verifierModel) {
		this.verifierModel = verifierModel;
	}

	/**
	 * @return the deleteEpItems
	 */
	public List<PpExpenditureItem> getDeleteEpItems() {
		return deleteEpItems;
	}

	/**
	 * @param deleteEpItems the deleteEpItems to set
	 */
	public void setDeleteEpItems(List<PpExpenditureItem> deleteEpItems) {
		this.deleteEpItems = deleteEpItems;
	}

	/**
	 * @return the deletePpItems
	 */
	public List<PpProcurementItem> getDeletePpItems() {
		return deletePpItems;
	}

	/**
	 * @param deletePpItems the deletePpItems to set
	 */
	public void setDeletePpItems(List<PpProcurementItem> deletePpItems) {
		this.deletePpItems = deletePpItems;
	}

	/**
	 * @return the selectedEpItem
	 */
	public PpExpenditureItem getSelectedEpItem() {
		return selectedEpItem;
	}

	/**
	 * @param selectedEpItem the selectedEpItem to set
	 */
	public void setSelectedEpItem(PpExpenditureItem selectedEpItem) {
		this.selectedEpItem = selectedEpItem;
	}

	/**
	 * @return the selectedPpItem
	 */
	public PpProcurementItem getSelectedPpItem() {
		return selectedPpItem;
	}

	/**
	 * @param selectedPpItem the selectedPpItem to set
	 */
	public void setSelectedPpItem(PpProcurementItem selectedPpItem) {
		this.selectedPpItem = selectedPpItem;
	}

	/**
	 * @return the planSearchBean
	 */
	public ProcurementPlanSearchBean getPlanSearchBean() {
		if(planSearchBean==null)
			planSearchBean = new ProcurementPlanSearchBean();
		return planSearchBean;
	}

	/**
	 * @param planSearchBean the planSearchBean to set
	 */
	public void setPlanSearchBean(ProcurementPlanSearchBean planSearchBean) {
		this.planSearchBean = planSearchBean;
	}

	/**
	 * @return the selectedPlan
	 */
	public PpPlan getSelectedPlan() {
		return selectedPlan;
	}

	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setSelectedPlan(PpPlan selectedPlan) {
		this.selectedPlan = selectedPlan;
	}
	
	
	
	
	
	
	
	
	
	
	
//	private List<String> modeList; //TODO
//
//	//TODO remove here
//	/**
//	 * @return the modeList
//	 */
//	public List<String> getModeList() {
//		if(modeList==null)
//			modeList = new ArrayList<String>();
//		modeList.add("REVISION");
//		modeList.add("APPROVAL");
//		modeList.add("VERIFICATION");
//		modeList.add("VIEW");
//		modeList.add("NEW");
//		return modeList;
//	}
//
//	/**
//	 * @param modeList the modeList to set
//	 */
//	public void setModeList(List<String> modeList) {
//		this.modeList = modeList;
//	}
	

}
