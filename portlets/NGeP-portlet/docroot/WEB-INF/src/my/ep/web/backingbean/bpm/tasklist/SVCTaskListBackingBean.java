package my.ep.web.backingbean.bpm.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.managedbean.bpm.tasklist.SVCTaskListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "svcTasklistBackingBean")
@RequestScoped
public class SVCTaskListBackingBean extends BaseBackingBean<SVCTaskListManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SVCTaskListBackingBean.class);
	private List<SmAppl> resultList = new ArrayList<SmAppl>(); 






	public SVCTaskListBackingBean(){

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		try{
			StateReqDTO reqStateDTO = new StateReqDTO();
			reqStateDTO.setCountryCode("MY");
			StateResDTO resStateDTO = stateSO.retriveStatesListByCountryCode(reqStateDTO);

			this.getManagedBean().setStateList(resStateDTO.getStateList());
		}finally {
			stateSO = null;
		}
	}



	public void searchAction() {
		
		System.out.println("*********searchAction*********");
		
		this.getManagedBean().getSearchDataModel().reloadData();
	}

	public void testNavigate() {
		System.out.println("testNavigate.........");
	}

	public String navigateToSiteVisitAssignment() {
		/*System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
		log.info("In navigateToSiteVisitAssignment");
		String selectedApplNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selApplId"); 
		log.info("selectedApplNo: " + selectedApplNo);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		Object token = bpmSO.authenticate("weblogic");

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(selectedApplNo);
		reqDTO.setCtxToken(token);
		reqDTO.setState("ASSIGNED");
		BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(reqDTO);

		if(resDTO.getWorklistVO().getWorklistItem() != null && !resDTO.getWorklistVO().getWorklistItem().isEmpty()) {
			String taskId = resDTO.getWorklistVO().getWorklistItem().get(0).getTaskId();

			FacesScopes facesScope = new FacesScopes();
			ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
					.getRequestScope(WebKeys.THEME_DISPLAY);

			log.info("getURLCurrent" + themeDisplay.getURLCurrent());

			try {
				String commonUrl = themeDisplay.getURLCurrent().substring(
						0,
						themeDisplay.getURLCurrent().indexOf(
								themeDisplay.getLayout().getGroup()
								.getFriendlyURL())
								+ themeDisplay.getLayout().getGroup()
								.getFriendlyURL().length());
				log.info("commonUrl" + commonUrl);

				ExternalContext externalContext = FacesContext.getCurrentInstance()
						.getExternalContext();

				// externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
				log.info("site visit url: " + commonUrl + "/" + "site-visit" + "?taskId=" + taskId);
				externalContext.redirect(commonUrl + "/" + "site-visit" + "?taskId=" + taskId);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}*/
		Long selectedApplId = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selApplId")); 
		SvcSiteAssignBackingBean svcAssignmentBB = new SvcSiteAssignBackingBean();
		svcAssignmentBB.retrieveSiteAssignInfo(selectedApplId);
		
		
		
		return "/xhtml/sm/svcassignment/assignSiteVisitView.xhtml";
	}

	public void findDistrict() {

		log.info("*****************findDistrict***************");
		log.info("Id:" + this.getManagedBean().getStateId());

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		log.info("*********************************");

		reqDTO.setStateId(this.getManagedBean().getStateId());
		DistrictResDTO resDTO = districtSO
				.retriveDistrictListByStateID(reqDTO);

		for (PmDistrict district : resDTO.getDistrictList()) {

			this.getManagedBean().getDistrictList().add(district);

		}


		log.info("this.getManagedBean().getDistrictList().size():"
				+ this.getManagedBean().getDistrictList().size());

	}

	public List<SmAppl> getResultList() {
		return resultList;
	}

	public void setResultList(List<SmAppl> resultList) {
		this.resultList = resultList;
	}

}
