package my.ep.web.backingbean.ss.procure;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.ss.directpurchase.RequestNoteReqDTO;
import my.ep.dto.ss.directpurchase.RequestNoteResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.ss.procure.ProcureCodeManageBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Tran Ngoc Hoa
 * 
 */
@ManagedBean(name = "procurementListBackingBean")
@RequestScoped
public class ProcurementListBackingBean extends BaseBackingBean<ProcureCodeManageBean> {

	public ProcurementListBackingBean() {

	}

	public String initiateBPMTask(PpPlan ppPlan) {
		if (ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equalsIgnoreCase(ppPlan.getProcurementModeParam().getParameterCode())) {
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
			// direct purchase, create empty request note and initial BPM task
			RequestNoteReqDTO	requestDTO	=	new RequestNoteReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			requestDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			requestDTO.setPlan(ppPlan);
			RequestNoteResDTO	responseDTO = directPurchaseSO.startSourcing(requestDTO);
			return "/xhtml/shared/acknowledge.xhtml";
		} else {
			ScQt scQt = new ScQt();
			scQt.setPlanned(true);
			scQt.setPpPlan(ppPlan);
			scQt.setBrandNew(false);
			
			scQt.setFileNo("qt123q");
			scQt.setFinancialYear(2014);
			scQt.setQtNo("planned"+ppPlan.getPlanId());
			scQt.setQtTitle("sourcing");
			scQt.setProcurementMode(ppPlan.getProcurementModeParam().getParameterCode());
			scQt.setQtType(ppPlan.getQtTypeParam().getParameterCode());
			scQt.setQtType("o");
			scQt.setQtAmount(new BigDecimal(10000));
			scQt.setResponseRule("A");
			scQt.setBsvRequired(false);
			scQt.setSameBsv(false);
			
					
			scQt.setPmOrgProfile(ppPlan.getPtjOrgProfile());
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
	}

	public	void	editPlan(PpPlan plan){
		getManagedBean().setCurrentPlan(plan);
	}
	
	public	String	updatePlan(){
		return "/xhtml/shared/acknowledge.xhtml";
	}

	/**
	 * method to search the procurement plans
	 */

	public void retriveProcuremntPlanDtls() {
		IProcurementPlanSO procurementSO = getProcurementSO();
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		
		ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
		//planReqDTO.setPlanId(Long.valueOf(getManagedBean().getPlanSearchBean().getPlanNo()));
		if(StringUtils.isNotBlank(getManagedBean().getPlanSearchBean().getStatus())){
			planReqDTO.setAction(getManagedBean().getPlanSearchBean().getStatus());
		}
		if(StringUtils.isNotBlank(getManagedBean().getPlanSearchBean().getPlanNo())){
			planReqDTO.setPlanNumber(getManagedBean().getPlanSearchBean().getPlanNo());
		}
		if(StringUtils.isNotBlank(getManagedBean().getPlanSearchBean().getProcurementMode())){
			planReqDTO.setProcurementMode(getManagedBean().getPlanSearchBean().getProcurementMode());
		}
		//planReqDTO.setSearchBean(getManagedBean().getPlanSearchBean());
		//ProcurementPlanResDTO planResDTO = procurementSO.retrievePlanById(planReqDTO);
		ProcurementPlanResDTO planResDTO = procurementSO.retrievePlanByParams(planReqDTO);
		List<PpPlan> pplans = planResDTO.getPlanList();
		//check plan has started sourcing or not
		RequestNoteReqDTO	reqDTO	=	new RequestNoteReqDTO();
		reqDTO.setPlans(pplans);
		RequestNoteResDTO resDTO	=	directPurchaseSO.checkPlanEditable(reqDTO);
		pplans = resDTO.getPlans();
		//List<PpPlan> pplans	=	new ArrayList<>();
		//pplans.add(planResDTO.getPlan());
		this.getManagedBean().setProcurementPlanList(pplans);
		System.out.println(this.getManagedBean().getPlanSearchBean());

	}
	
	public String updatePlannedInfo() {
		  IProcurementPlanSO procurementPlanSO = null;
		  try {
			   procurementPlanSO	=	(IProcurementPlanSO)BusinessDelegate.getInstance().getServiceObject(SourcingConst.PROCUREMENT_MODULE, IProcurementPlanSO.class);
			   ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
			   if(getManagedBean().getCurrentPlan() !=null){
				  reqDTO.setPlan(getManagedBean().getCurrentPlan());
				  procurementPlanSO.updateProcurementPlanPartially(reqDTO);
			   }
			   FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.", ""));
			} catch (Exception ex) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sorry We are unable to process.", ""));
				ex.printStackTrace();
			} finally {
				procurementPlanSO = null;
			}
			return null;
		}

	private IProcurementPlanSO getProcurementSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject("pp", IProcurementPlanSO.class);
	}
}
