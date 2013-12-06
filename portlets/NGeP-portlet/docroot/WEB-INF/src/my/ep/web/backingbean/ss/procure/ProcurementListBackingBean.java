package my.ep.web.backingbean.ss.procure;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.xml.rpc.ServiceException;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtApprovalAttachment;
import my.ep.sc.bpm.bpmobject.Data.SC_QuotationTender_Data.SC_QuotationTender_DataType;
import my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationPortType;
import my.ep.sc.bpmnProcess.ServiceQuotationTenderCreation.ServiceQuotationTenderCreationServiceLocator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.managedbean.ss.procure.ProcureCodeManageBean;
import my.ep.web.ss.quationtender.common.QTConstants;

import org.primefaces.event.RowEditEvent;

/**
 * 
 * @author Tran Ngoc Hoa
 * 
 */
@ManagedBean(name = "procurementListBackingBean")
@RequestScoped
public class ProcurementListBackingBean extends
		BaseBackingBean<ProcureCodeManageBean> {

	public ProcurementListBackingBean() {

	}

	public String initiateBPMTask(PpPlan ppPlan) {
		
		ScQt scQt = new ScQt();
		scQt.setPlanned(true);
		scQt.setPpPlan(ppPlan);
		scQt.setBrandNew(false);
		
		scQt.setFileNo("qt123q");
		scQt.setFinancialYear(2014);
		scQt.setQtNo("planned"+ppPlan.getPlanId());
		scQt.setQtTitle("sourcing");
		scQt.setProcurementMode(ppPlan.getPpPlanMaster().getProcurementMode());
		scQt.setQtType(ppPlan.getPpPlanMaster().getQtType());
		scQt.setQtType("o");
		scQt.setQtAmount(new BigDecimal(10000));
		scQt.setResponseRule("A");
		scQt.setBsvRequired(false);
		scQt.setSameBsv(false);
		
				
		scQt.setPmOrgProfile(ppPlan.getPpPlanMaster().getOrgProfile());
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(1l);
		IStatusSO iStatusSO=EpEjbBusinessDelegate.getInstance().getServiceObject(QTConstants.PM_MODULE, IStatusSO.class);
		StatusResDTO pmStatus = iStatusSO.retrieveStatusByStatusId(reqDTO);
		PmStatus status = pmStatus.getPmStatus();
		scQt.setPmStatus(status);
	QTDetailBackingBean backingBean= new QTDetailBackingBean();
	backingBean.getManagedBean().setQt(scQt);
	QuationTenderReqDTO tenderReqDTO = backingBean.prepareQTInfo();
		IQTDetailSO detailSO= EpEjbBusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE,IQTDetailSO.class);
		
		QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO.createQTforPPlan(tenderReqDTO);
		
		if(tenderResDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.",
							"Committe Type. : "

					));

			return "/xhtml/shared/acknowledge.xhtml";
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry We are unable to Process.",""));
			return "/xhtml/shared/acknowledge.xhtml";
		}
	}

	public void onEdit(RowEditEvent event) {
		PpPlan ppPlan = (PpPlan) event.getObject();
		// TODO call procurement plan to update notification date
	}

	public void test() {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);

		reqDTO.setProcessId("Quotation_Tender.DirectPurchase");
		reqDTO.setOutcome("");
		// PSBudgetDataType obj = new PSBudgetDataType();
		// obj.setApprover("Asdqwe");
		// obj.setBudgetId("Asdqwe");
		// obj.setPtj("Asdqwe");
		// reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
	}

	/**
	 * method to search the procurement plans
	 */

	public void retriveProcuremntPlanDtls() {
		IProcurementPlanSO procurementSO = getProcurementSO();
		ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
		planReqDTO.setAction(this.getManagedBean().getPlanSearchBean()
				.getStatus());
		planReqDTO.setPlanNumber(this.getManagedBean()
				.getPlanSearchBean().getPlanNo());
		planReqDTO.setProcurementMode(this.getManagedBean().getPlanSearchBean()
				.getProcurementMode());
		ProcurementPlanResDTO planResDTO = procurementSO
				.retrievePlanByParams(planReqDTO);
		List<PpPlan> pplans = planResDTO.getPlanList();
		this.getManagedBean().setProcurementPlanList(pplans);
		System.out.println(this.getManagedBean().getPlanSearchBean());

	}

	private IProcurementPlanSO getProcurementSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				QTConstants.PP_MODULE, IProcurementPlanSO.class);
	}
}
