package my.ep.web.backingbean.ss.directpurchase;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.ProcurementListManageBean;
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
}
