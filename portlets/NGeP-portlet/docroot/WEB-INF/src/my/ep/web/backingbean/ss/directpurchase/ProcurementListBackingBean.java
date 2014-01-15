package my.ep.web.backingbean.ss.directpurchase;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ss.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.ss.directpurchase.ProcurementListManageBean;
import my.ep.web.ss.quationtender.common.QTConstants;
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
		IProcurementPlanSO	procurementPlanSO	=	(IProcurementPlanSO)BusinessDelegate.getInstance().getServiceObject(SourcingConst.PROCUREMENT_MODULE, IProcurementPlanSO.class);
		ProcurementPlanResDTO	resDTO	=	procurementPlanSO.retrievePlanByParams(reqDTO);
		getManagedBean().setProcurementPlanList(resDTO.getPlanList());
	}
	
	public String initiateBPMTask(PpPlan ppPlan) {
		if (ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equalsIgnoreCase(ppPlan.getProcurementModeParam().getParameterCode())) {
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
			// direct purchase, create empty request list and initial BPM task
			//set status to new
			
			//TODO hard code org profile first
			PmOrgProfile	orgProfile	=	new PmOrgProfile();
			orgProfile.setOrgProfileId(1L);
			// Initial BPM task
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
			SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
			obj.setDpId(ppPlan.getPlanId().intValue());
			obj.setDpMode("PP");
			obj.setPtj("Asdqwe");
			// obj.getSuppliers().add(String.valueOf(
			// this.getManagedBean().getSelectedItem().getSmSupplier().getSupplierId()));
			reqDTO.setPojo(obj);
			BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
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
}
