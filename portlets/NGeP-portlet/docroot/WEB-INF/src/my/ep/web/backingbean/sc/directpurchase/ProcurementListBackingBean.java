package my.ep.web.backingbean.sc.directpurchase;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.sc.directpurchase.ProcurementListManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

@ManagedBean(name = "ssProcurementListBackingBean")
@RequestScoped
public class ProcurementListBackingBean extends BaseBackingBean<ProcurementListManageBean> {
	private static Logger log = Logger.getLogger(ProcurementListBackingBean.class);

	public ProcurementListBackingBean() {
		super();
		System.out.println("test test test");
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
		ProcurementPlanReqDTO reqDTO	=	new ProcurementPlanReqDTO();
		ISearchBean<PpPlan> searchBean = new SearchBean<PpPlan>();
		reqDTO.setSearchBean(searchBean);
		IProcurementPlanSO	procurementPlanSO	=	(IProcurementPlanSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
		ProcurementPlanResDTO	resDTO	=	procurementPlanSO.retrievePlanByParams(reqDTO);
		// remove the already started sourcing
		List<PpPlan>	pplans	=	resDTO.getPlanList();
		RequestNoteReqDTO	requestNoteReqDTO	=	new RequestNoteReqDTO();
		requestNoteReqDTO.setPlans(pplans);
		requestNoteReqDTO.setRemovePlanSourcingStarted(true);
		RequestNoteResDTO requestNoteResDTO	=	directPurchaseSO.checkPlanEditable(requestNoteReqDTO);
		pplans = requestNoteResDTO.getPlans();
		getManagedBean().setProcurementPlanList(pplans);
	}
	
	public String initiateBPMTask(PpPlan ppPlan) {
		if (ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equalsIgnoreCase(ppPlan.getProcurementModeParam().getParameterCode())) {
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
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
			//scQt.setFinancialYear(2014);
			scQt.setQtNo("planned"+ppPlan.getPlanId());
			scQt.setQtTitle("sourcing");
			scQt.setProcurementMode(ppPlan.getProcurementModeParam().getParameterCode());
			scQt.setQtType(ppPlan.getQtTypeParam().getParameterCode());
			scQt.setQtType("o");
			scQt.setQtAmount(new BigDecimal(10000));
			//scQt.setResponseRule("A");
			scQt.setBsvRequired(false);
			scQt.setSameBsv(false);
			
					
			scQt.setPmOrgProfile(ppPlan.getPtjOrgProfile());
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(1l);
			IStatusSO iStatusSO=BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusResDTO pmStatus = iStatusSO.retrieveStatusByStatusId(reqDTO);
			PmStatus status = pmStatus.getPmStatus();
			scQt.setPmStatus(status);
			QTDetailBackingBean backingBean= new QTDetailBackingBean();
			backingBean.getManagedBean().setQt(scQt);
			QuationTenderReqDTO tenderReqDTO = backingBean.prepareQTInfo();
			IQTDetailSO detailSO= BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTDetailSO.class);
			
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
}
