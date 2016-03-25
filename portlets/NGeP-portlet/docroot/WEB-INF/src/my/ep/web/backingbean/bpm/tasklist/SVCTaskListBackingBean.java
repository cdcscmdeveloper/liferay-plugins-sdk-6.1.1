package my.ep.web.backingbean.bpm.tasklist;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvcSiteAssignBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.bpm.tasklist.SVCTaskListManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmSvcTaskVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "svcTasklistBackingBean")
@RequestScoped
public class SVCTaskListBackingBean extends BaseBackingBean<SVCTaskListManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SVCTaskListBackingBean.class);
	private List<SmAppl> resultList = new ArrayList<SmAppl>(); 
	private static String SVC_TASK_LIST = "svc-assignment";


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
		if(StringUtils.isBlank(getManagedBean().getApplNo()) && StringUtils.isBlank(getManagedBean().getSupplierName()) 
			&& StringUtils.isBlank(getManagedBean().getEpNo()) && getManagedBean().getDistrictId() == 0 && 
			getManagedBean().getStateId() == 0){
			throw new EpException(MessageConst.ERROR_721048);
		}
		
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
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);

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
	
	public SmWorkflowStatus getWorkflowInfoByApplId(Long applId){
		ISmWorkFlowStatusSO reqSO = SmServiceLookup.getSmWorkFlowStatusSO();
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setApplId(applId);
		SmWorkFlowStatusResDTO resDTO = reqSO.retrieveCurrentWorkFlowStatusByDocId(reqDTO);
		SmWorkflowStatus workFlowStatus = resDTO.getWorkflowStatus();
		
		//if(worskFlowStatus != null){
			//SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
			
			//return sdf.format(worskFlowStatus.getCreatedDate());
		//}
		
		return workFlowStatus;
	}
	
	/**
	 * retrieve Malaysia State List
	 * @return stateLs
	 */
	public List<SmSvcTaskVO> retrieveSvcStateTaskList(){
		ISiteAssignSO reqSO = SmServiceLookup.getSiteAssignSO();
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		reqDTO.setCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		//List<Long> statusLs = getManagedBean().getSiteVisitStatusesByType(SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_ALL);
		//reqDTO.setSiteVisitStatuses(statusLs);
		SiteAssignResDTO resDTO = reqSO.retrievSvcTaskSummaryByCountryCode(reqDTO);
		
		List<SmSvcTaskVO> svcStateLs = new ArrayList<SmSvcTaskVO>();
		
		if(resDTO.getResultStrList().size() > 0){
			for(Object[] resultArr : resDTO.getResultStrList()){
				Long stateId = Long.valueOf(resultArr[0].toString());
				String stateName = resultArr[1].toString();
				int totalBefore = resultArr[2] == null ? 0 : Integer.valueOf(resultArr[2].toString());
				int totalAfter = resultArr[3] == null ? 0 : Integer.valueOf(resultArr[3].toString());
				
				SmSvcTaskVO svcTaskVO = new SmSvcTaskVO();
				svcTaskVO.setStateId(stateId);
				svcTaskVO.setStateName(stateName);
				svcTaskVO.setTotalTaskBefore(totalBefore);
				svcTaskVO.setTotalTaskAfter(totalAfter);
				
				svcStateLs.add(svcTaskVO);
			}
		}
		return svcStateLs;
	}
	
	/**
	 * search by state
	 */
	public void searchByState() {
		String selectedState = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("stateId"); 
		getManagedBean().setApplNo(null);
		getManagedBean().setStateId(Long.valueOf(selectedState));
		getManagedBean().setDistrictId(0);
		getManagedBean().setSupplierName(null);
		getManagedBean().setSiteVisitType(SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_ALL);
		findDistrict();
		this.getManagedBean().getSearchDataModel().reloadData();
	}
	
	/**
	 * search by state
	 * @throws SystemException 
	 * @throws PortalException 
	 * @throws IOException 
	 */
	public void navigateByState() throws PortalException, SystemException, IOException {
		String selectedState = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("stateId"); 
		getManagedBean().setApplNo(null);
		getManagedBean().setStateId(Long.valueOf(selectedState));
		getManagedBean().setDistrictId(0);
		getManagedBean().setSupplierName(null);
		getManagedBean().setSiteVisitType(SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_ALL);
		findDistrict();
		this.getManagedBean().getSearchDataModel().reloadData();
		
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String commonUrl = themeDisplay.getURLCurrent().substring(0,themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		
		externalContext.redirect(commonUrl + "/" + SVC_TASK_LIST);
	}
	
	/**
	 * reset search criteria
	 */
	public void resetSearchCriteria(){
		getManagedBean().setApplNo(null);
		getManagedBean().setStateId(0);
		getManagedBean().setDistrictId(0);
		getManagedBean().setSupplierName(null);
		getManagedBean().setSiteVisitType(SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_ALL);
		getManagedBean().setDistrictId(0);

		getManagedBean().getSearchDataModel().getList().clear();
	}
}
