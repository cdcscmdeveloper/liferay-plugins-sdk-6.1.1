package my.ep.web.backingbean.sc.procure;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.procure.ProcurementPlanManageBean;

/**
 * 
 * @author Tran Ngoc Hoa
 * 
 */
@ManagedBean(name = "procurementPlanBackingBean")
@ViewScoped
public class ProcurementPlanBackingBean extends
		BaseBackingBean<ProcurementPlanManageBean> implements Serializable{

	
	private static final long serialVersionUID = 1L;

//	private String SEARCH_CATALOG_URL = "/xhtml/cm/catalogue/supplierItemView.xhtml";
//
//	private IPagingDataModel<PpPlan> planDataModel = new PpPlanPagingDataModel();
//
//	public ProcurementPlanBackingBean() {
//		if (this.getManagedBean().getCurrentPlan() == null) {
//			PpPlan plan = new PpPlan();
//			plan.setPmStatus(new PmStatus());
//			this.getManagedBean().setCurrentPlan(plan);
//		}
//	}
//
//	public SelectItem[] statusDropdownValues() {
//		return getStatusDropdownValues(ProcurementPlanConst.MODULE,
//				ParameterConst.DOCTYPE_PLAN);
//	}
//
//	public String initiateBPMTask(PpPlan ppPlan) {
//		String REDIRECT_PAGE = null;
//		if (ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan
//				.getPlanTypeParam().getParameterCode())) {
//
//			REDIRECT_PAGE = SEARCH_CATALOG_URL + "?faces-redirect=true";
//		} else if (ProcurementPlanConst.PLAN_TYPE_PROCUREMENT
//				.equalsIgnoreCase(ppPlan.getPlanTypeParam().getParameterCode())) {
//			/*
//			 * ScQt scQt = new ScQt(); scQt.setPlanned(true);
//			 * scQt.setPpPlan(ppPlan); scQt.setBrandNew(false);
//			 * 
//			 * scQt.setFileNo("qt123q");
//			 * 
//			 * scQt.setQtNo("planned" + ppPlan.getPlanId());
//			 * scQt.setQtTitle("sourcing");
//			 * scQt.setProcurementMode(ppPlan.getProcurementModeParam()
//			 * .getParameterCode());
//			 * scQt.setQtType(ppPlan.getQtTypeParam().getParameterCode());
//			 * scQt.setQtType("o"); scQt.setQtAmount(new BigDecimal(10000));
//			 * 
//			 * scQt.setBsvRequired(false); scQt.setSameBsv(false);
//			 * 
//			 * scQt.setPmOrgProfile(ppPlan.getPtjOrgProfile()); StatusReqDTO
//			 * reqDTO = new StatusReqDTO(); reqDTO.setStatusId(1l); IStatusSO
//			 * iStatusSO = BusinessDelegate.getInstance()
//			 * .getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
//			 * IStatusSO.class); StatusResDTO pmStatus =
//			 * iStatusSO.retrieveStatusByStatusId(reqDTO); PmStatus status =
//			 * pmStatus.getPmStatus(); // scQt.setPmStatus(status);
//			 * QTDetailBackingBean backingBean = new QTDetailBackingBean();
//			 * backingBean.getManagedBean().setQt(scQt); QuationTenderReqDTO
//			 * tenderReqDTO = backingBean.prepareQTInfo(); IQTDetailSO detailSO
//			 * = BusinessDelegate.getInstance()
//			 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
//			 * IQTDetailSO.class);
//			 * 
//			 * QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO
//			 * .createQTforPPlan(tenderReqDTO);
//			 * 
//			 * if (tenderResDTO.isSuccessful()) {
//			 * FacesContext.getCurrentInstance().addMessage( null, new
//			 * FacesMessage(FacesMessage.SEVERITY_INFO,
//			 * "Record saved successfully.", "Committe Type. : "
//			 * 
//			 * ));
//			 * 
//			 * return "/xhtml/shared/acknowledge.xhtml"; }
//			 * 
//			 * else { FacesContext.getCurrentInstance().addMessage( null, new
//			 * FacesMessage(FacesMessage.SEVERITY_INFO,
//			 * "Sorry We are unable to Process.", "")); return
//			 * "/xhtml/shared/acknowledge.xhtml"; }
//			 */
//
//			REDIRECT_PAGE = "/xhtml/shared/acknowledge.xhtml"
//					+ "faces-redirect=true";
//		}
//		return REDIRECT_PAGE;
//	}
//
//	public void editPlan(PpPlan plan, int index) {
//		this.getManagedBean().setNotificationDate(null);
//		this.getManagedBean().setCurrentPlan(plan);		
//		
//	}
//
//	public void updatePlannedInfo() {
//		IProcurementPlanSO iProcurementPlanSO = EpEjbBusinessDelegate
//				.getInstance().getServiceObject(
//						ModuleCodeConst.PROCUREMENT_PLAN,
//						IProcurementPlanSO.class);
//		ProcurementPlanReqDTO dto = new ProcurementPlanReqDTO();
//		dto.setPlan(this.getManagedBean().getCurrentPlan());
//		
//		ProcurementPlanResDTO resDTO = iProcurementPlanSO.updateProcurementPlanPartially(dto);
//		
//	}
//
//	/**
//	 * method to search the procurement plans
//	 */
//
//	public void retriveProcuremntPlanDtls() {
//		/*
//		 * IProcurementPlanSO procurementSO = getProcurementSO();
//		 * 
//		 * IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO)
//		 * BusinessDelegate .getInstance().getServiceObject(
//		 * ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
//		 * 
//		 * ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO(); //
//		 * planReqDTO
//		 * .setPlanId(Long.valueOf(getManagedBean().getPlanSearchBean()
//		 * .getPlanNo())); if
//		 * (StringUtils.isNotBlank(getManagedBean().getPlanNumber())) {
//		 * planReqDTO.setPlanNumber(getManagedBean().getPlanNumber()); } // TODO
//		 * Need to add planstatus in requestbean
//		 * 
//		 * if (StringUtils.isNotBlank(getManagedBean().getPlanStatus() )) {
//		 * planReqDTO.setp(getManagedBean().getPlanStatus()))
//		 * 
//		 * }
//		 * 
//		 * if (StringUtils.isNotBlank(getManagedBean().getProcurementMode())) {
//		 * planReqDTO
//		 * .setProcurementMode(getManagedBean().getProcurementMode()); }
//		 * 
//		 * ProcurementPlanResDTO planResDTO = procurementSO
//		 * .retrievePlanByParams(planReqDTO); List<PpPlan> pplans =
//		 * planResDTO.getPlanList(); List<PpPlan> expenditurePlans = new
//		 * ArrayList<PpPlan>(); List<PpPlan> procurementPlans = new
//		 * ArrayList<PpPlan>(); for (PpPlan plan : pplans) { if
//		 * (ProcurementPlanConst.PLAN_TYPE_EXPENDITURE
//		 * .equalsIgnoreCase(plan.getPlanTypeParam() .getParameterCode())) {
//		 * expenditurePlans.add(plan); } else if
//		 * (ProcurementPlanConst.PLAN_TYPE_PROCUREMENT
//		 * .equalsIgnoreCase(plan.getPlanTypeParam() .getParameterCode())) {
//		 * procurementPlans.add(plan); } }
//		 * 
//		 * // check plan has started sourcing or not RequestNoteReqDTO reqDTO =
//		 * new RequestNoteReqDTO();
//		 * reqDTO.setExpenditurePlans(expenditurePlans);
//		 * reqDTO.setProcurementPlans(procurementPlans); RequestNoteResDTO
//		 * resDTO = directPurchaseSO.checkPlanEditable(reqDTO); pplans =
//		 * resDTO.getPlans();
//		 * 
//		 * // List<PpPlan> pplans = new ArrayList<>(); //
//		 * pplans.add(planResDTO.getPlan());
//		 * this.getManagedBean().setProcurementPlanList(pplan);
//		 */
//		planDataModel.reloadData();
//	}
//
//	private IProcurementPlanSO getProcurementSO() {
//		return BusinessDelegate.getInstance().getServiceObject("pp",
//				IProcurementPlanSO.class);
//	}
//
//	private IQTDetailSO retrieveQtDetailsSO() {
//		return BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
//	}
//
//	public void handleDateSelect() {
//
//		Date actualStartDate = this.getManagedBean().getCurrentPlan()
//				.getPpPlanActivities().get(0).getPlanStartDate();
//		Date notificationDate = this.getManagedBean().getNotificationDate();
//
//		getManagedBean().getCurrentPlan().setNotificationDays(
//				new Long(DateUtils.getDaysDifference(actualStartDate,
//						notificationDate)).intValue());
//
//	}
//
//	public void handleNotificationDate() {
//
//		Date actualStartDate = this.getManagedBean().getCurrentPlan()
//				.getPpPlanActivities().get(0).getPlanStartDate();
//		getManagedBean().getCurrentPlan().setNotificationDays(
//				this.getManagedBean().getCurrentPlan().getNotificationDays());
//
//		this.getManagedBean().setNotificationDate(
//				DateUtils.addDays(actualStartDate, this.getManagedBean()
//						.getCurrentPlan().getNotificationDays()));
//
//	}
//
//	public void resetPlanDetails() {
//		this.getManagedBean().setPlanNumber(null);
//		this.getManagedBean().setProcurementMode(null);
//		this.getManagedBean().setPlanStatus(null);
//		this.getManagedBean().setProcurementPlanList(null);
//	}
//
//	private class PpPlanPagingDataModel extends PagingDataModel<PpPlan> {
//
//		/**
//		 * @param componentId
//		 */
//		public PpPlanPagingDataModel() {
//			super("dtPlanResult");
//			// TODO Auto-generated constructor stub
//		}
//
//		@Override
//		public ISearchResultBean<PpPlan> pageChangeListener(
//				ISearchBean<PpPlan> searchBean) {
//			// TODO Auto-generated method stub
//			IProcurementPlanSO procurementSO = getProcurementSO();
//
//			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate
//					.getInstance().getServiceObject(
//							ModuleCodeConst.SUPPLIER_SELECTION,
//							IDirectPurchaseSO.class);
//
//			ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
//			// planReqDTO.setPlanId(Long.valueOf(getManagedBean().getPlanSearchBean().getPlanNo()));
//			if (StringUtils.isNotBlank(getManagedBean().getPlanNumber())) {
//				planReqDTO.setPlanNumber(getManagedBean().getPlanNumber());
//			}
//			// TODO Need to add planstatus in requestbean
//			/*
//			 * if (StringUtils.isNotBlank(getManagedBean().getPlanStatus() )) {
//			 * planReqDTO.setp(getManagedBean().getPlanStatus()))
//			 * 
//			 * }
//			 */
//			if (StringUtils.isNotBlank(getManagedBean().getProcurementMode())) {
//				planReqDTO.setProcurementMode(getManagedBean()
//						.getProcurementMode());
//			}
//
//			ProcurementPlanResDTO planResDTO = procurementSO
//					.retrievePlanByParams(planReqDTO);
//			List<PpPlan> pplans = planResDTO.getPlanList();
//			List<PpPlan> expenditurePlans = new ArrayList<PpPlan>();
//			List<PpPlan> procurementPlans = new ArrayList<PpPlan>();
//			for (PpPlan plan : pplans) {
//				if (ProcurementPlanConst.PLAN_TYPE_EXPENDITURE
//						.equalsIgnoreCase(plan.getPlanTypeParam()
//								.getParameterCode())) {
//					expenditurePlans.add(plan);
//				} else if (ProcurementPlanConst.PLAN_TYPE_PROCUREMENT
//						.equalsIgnoreCase(plan.getPlanTypeParam()
//								.getParameterCode())) {
//					procurementPlans.add(plan);
//				}
//			}
//
//			// check plan has started sourcing or not
//			RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
//			reqDTO.setExpenditurePlans(expenditurePlans);
//			reqDTO.setProcurementPlans(procurementPlans);
//			RequestNoteResDTO resDTO = directPurchaseSO
//					.checkPlanEditable(reqDTO);
//			pplans = resDTO.getPlans();
//
//			// List<PpPlan> pplans = new ArrayList<>();
//			// pplans.add(planResDTO.getPlan());
//			SearchResultBean<PpPlan> resultBean = new SearchResultBean<PpPlan>();
//			resultBean.setResultList(pplans);
//			resultBean.setTotalRecord(pplans.size());
//			return resultBean;
//		}
//
//	}
//
//	public IPagingDataModel<PpPlan> getPlanDataModel() {
//		return planDataModel;
//	}
//
//	public void setPlanDataModel(IPagingDataModel<PpPlan> planDataModel) {
//		this.planDataModel = planDataModel;
//	}
}
