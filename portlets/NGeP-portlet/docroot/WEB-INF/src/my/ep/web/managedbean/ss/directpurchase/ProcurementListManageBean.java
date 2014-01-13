package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="ssProcurementListMB")
@SessionScoped
public class ProcurementListManageBean extends BaseManagedBean{
	private	List<PpPlan>	procurementPlanList	=	new ArrayList<PpPlan>();
	
	public ProcurementListManageBean() {
		super();
		ProcurementPlanReqDTO reqDTO	=	new ProcurementPlanReqDTO();
		ISearchBean<PpPlan> searchBean = new SearchBean<PpPlan>();
		reqDTO.setSearchBean(searchBean);
		IProcurementPlanSO	procurementPlanSO	=	(IProcurementPlanSO)BusinessDelegate.getInstance().getServiceObject(SourcingConst.PROCUREMENT_MODULE, IProcurementPlanSO.class);
		ProcurementPlanResDTO	resDTO	=	procurementPlanSO.retrievePlanByParams(reqDTO);
		setProcurementPlanList(resDTO.getPlanList());
	}

	public List<PpPlan> getProcurementPlanList() {
		return procurementPlanList;
	}

	public void setProcurementPlanList(List<PpPlan> procurementPlanList) {
		this.procurementPlanList = procurementPlanList;
	}
	
}
