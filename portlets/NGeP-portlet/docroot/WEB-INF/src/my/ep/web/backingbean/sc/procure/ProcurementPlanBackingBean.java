package my.ep.web.backingbean.sc.procure;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.IPpSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.sc.procure.ProcurementPlanManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Tran Ngoc Hoa
 * 
 */
@ManagedBean(name = "procurementPlanBackingBean")
@RequestScoped
public class ProcurementPlanBackingBean extends
		BaseBackingBean<ProcurementPlanManageBean> {

	

	private String SEARCH_CATALOG_URL = "/xhtml/cm/catalogue/supplierItemView.xhtml";

	public ProcurementPlanBackingBean() {
		if (this.getManagedBean().getCurrentPlan() == null) {
			PpPlan plan = new PpPlan();
			plan.setPmStatus(new PmStatus());
			this.getManagedBean().setCurrentPlan(plan);
		}
	}

	public SelectItem[] statusDropdownValues() {
		return getStatusDropdownValues(ProcurementPlanConst.MODULE,
				ParameterConst.DOCTYPE_PLAN);
	}

	public String initiateBPMTask(PpPlan ppPlan) {
		if (ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equalsIgnoreCase(ppPlan
				.getPlanTypeParam().getParameterCode())) {

			return SEARCH_CATALOG_URL + "?faces-redirect=true";
		} else if (ProcurementPlanConst.PLAN_TYPE_PROCUREMENT
				.equalsIgnoreCase(ppPlan.getPlanTypeParam().getParameterCode())) {
			ScQt scQt = new ScQt();
			scQt.setPlanned(true);
			scQt.setPpPlan(ppPlan);
			scQt.setBrandNew(false);

			scQt.setFileNo("qt123q");

			scQt.setQtNo("planned" + ppPlan.getPlanId());
			scQt.setQtTitle("sourcing");
			scQt.setProcurementMode(ppPlan.getProcurementModeParam()
					.getParameterCode());
			scQt.setQtType(ppPlan.getQtTypeParam().getParameterCode());
			scQt.setQtType("o");
			scQt.setQtAmount(new BigDecimal(10000));

			scQt.setBsvRequired(false);
			scQt.setSameBsv(false);

			scQt.setPmOrgProfile(ppPlan.getPtjOrgProfile());
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			IStatusSO iStatusSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);
			StatusResDTO pmStatus = iStatusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			// scQt.setPmStatus(status);
			QTDetailBackingBean backingBean = new QTDetailBackingBean();
			backingBean.getManagedBean().setQt(scQt);
			QuationTenderReqDTO tenderReqDTO = backingBean.prepareQTInfo();
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

			QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO
					.createQTforPPlan(tenderReqDTO);

			if (tenderResDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.",
								"Committe Type. : "

						));

				return "/xhtml/shared/acknowledge.xhtml";
			}

			else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Sorry We are unable to Process.", ""));
				return "/xhtml/shared/acknowledge.xhtml";
			}
		}
		return "";
	}

	public void editPlan(PpPlan plan,int index) {
		this.getManagedBean().setNotificationDate(null);
		this.getManagedBean().setCurrentPlan(plan);
		this.getManagedBean().getProcurementPlanList().remove(index);
		this.getManagedBean().setSelectedIndex(index);
	}

	public void updatePlannedInfo() {
		IProcurementPlanSO iProcurementPlanSO=EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN,IProcurementPlanSO.class);
         ProcurementPlanReqDTO dto= new ProcurementPlanReqDTO();
         dto.setPlan(this.getManagedBean().getCurrentPlan());
         //TODO Need to change the code in ProcurementSO and make a SO call
		this.getManagedBean().getProcurementPlanList().add(this.getManagedBean().getSelectedIndex(),this.getManagedBean().getCurrentPlan());
	}

	/**
	 * method to search the procurement plans
	 */

	public void retriveProcuremntPlanDtls() {
		IProcurementPlanSO procurementSO = getProcurementSO();

		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION,
						IDirectPurchaseSO.class);

		ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
		// planReqDTO.setPlanId(Long.valueOf(getManagedBean().getPlanSearchBean().getPlanNo()));
		if (StringUtils.isNotBlank(getManagedBean().getPlanNumber())) {
			planReqDTO.setPlanNumber(getManagedBean().getPlanNumber());
		}
		// TODO Need to add planstatus in requestbean
		/*
		 * if (StringUtils.isNotBlank(getManagedBean().getPlanStatus() )) {
		 * planReqDTO.setp(getManagedBean().getPlanStatus()))
		 * 
		 * }
		 */
		if (StringUtils.isNotBlank(getManagedBean().getProcurementMode())) {
			planReqDTO
					.setProcurementMode(getManagedBean().getProcurementMode());
		}

		ProcurementPlanResDTO planResDTO = procurementSO
				.retrievePlanByParams(planReqDTO);
		List<PpPlan> pplans = planResDTO.getPlanList();
		List<PpPlan> expenditurePlans = new ArrayList<PpPlan>();
		List<PpPlan> procurementPlans = new ArrayList<PpPlan>();
		for (PpPlan plan : pplans) {
			if (ProcurementPlanConst.PLAN_TYPE_EXPENDITURE
					.equalsIgnoreCase(plan.getPlanTypeParam()
							.getParameterCode())) {
				expenditurePlans.add(plan);
			} else if (ProcurementPlanConst.PLAN_TYPE_PROCUREMENT
					.equalsIgnoreCase(plan.getPlanTypeParam()
							.getParameterCode())) {
				procurementPlans.add(plan);
			}
		}

		// check plan has started sourcing or not
		RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
		reqDTO.setExpenditurePlans(expenditurePlans);
		reqDTO.setProcurementPlans(procurementPlans);
		RequestNoteResDTO resDTO = directPurchaseSO.checkPlanEditable(reqDTO);
		pplans = resDTO.getPlans();
		System.out.println("Size@@@@@@@@@@@@@@@@@@@@@" + pplans.size());

		// List<PpPlan> pplans = new ArrayList<>();
		// pplans.add(planResDTO.getPlan());
		this.getManagedBean().setProcurementPlanList(pplans);

	}

	private IProcurementPlanSO getProcurementSO() {
		return BusinessDelegate.getInstance().getServiceObject("pp",
				IProcurementPlanSO.class);
	}

	private IQTDetailSO retrieveQtDetailsSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
	}

	

	public void handleDateSelect() {
		

		Date actualStartDate = this.getManagedBean().getCurrentPlan()
				.getPpPlanActivities().get(0).getPlanStartDate();
		Date notificationDate = this.getManagedBean().getNotificationDate();
		Long diffInDays = (Long) ((actualStartDate.getTime() - notificationDate
				.getTime()) / (1000 * 60 * 60 * 24));
		getManagedBean().getCurrentPlan().setNotificationDays(
				diffInDays.intValue());

	}

	public void handleNotificationDate() {

		Date actualStartDate = this.getManagedBean().getCurrentPlan()
				.getPpPlanActivities().get(0).getPlanStartDate();

		Long nextDay = (long) ((actualStartDate.getTime() - (this
				.getManagedBean().getCurrentPlan().getNotificationDays() * 1000 * 60 * 60 * 24)));
		getManagedBean().getCurrentPlan().setNotificationDays(
				nextDay.intValue());
		this.getManagedBean().setNotificationDate(new Date(nextDay));

	}

}
