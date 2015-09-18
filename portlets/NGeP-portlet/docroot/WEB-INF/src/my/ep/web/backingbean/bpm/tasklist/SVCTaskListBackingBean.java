package my.ep.web.backingbean.bpm.tasklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "svcTasklistBackingBean")
@SessionScoped
public class SVCTaskListBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SVCTaskListBackingBean.class);
	private List<SmAppl> resultList = new ArrayList<SmAppl>(); 
	private String applNo;
	private List<PmState> stateList;
	private List<PmDistrict> districtList;
	private long stateId;
	private long districtId;
	private String supplierName;
	
	
	public SVCTaskListBackingBean(){
		
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		try{
			StateReqDTO reqStateDTO = new StateReqDTO();
			reqStateDTO.setCountryCode("MY");
			StateResDTO resStateDTO = stateSO.retriveStatesListByCountryCode(reqStateDTO);

			this.setStateList(resStateDTO.getStateList());
		}finally {
			stateSO = null;
		}
	}
	
	

	public void searchAction() {
		ISiteAssignSO siteAssignSO = null;
		try {
			System.out.println("**************getApplNo*************"+this.getApplNo());
			System.out.println("******getStateId*****"+this.getStateId());
			System.out.println("******getDistrictId*****"+this.getDistrictId());
			siteAssignSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
			
			SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
			reqDTO.setApplNo(this.getApplNo());
			reqDTO.setSupplierName(this.getSupplierName());
			
			if(this.getDistrictId() >0){
				reqDTO.setDistrictId(this.getDistrictId());
			}
			
			if(this.getDistrictId() ==0){
				if(this.getStateId() >0){
					reqDTO.setStateId(this.getStateId());
				}
			}

		
			SiteAssignResDTO resDTO = siteAssignSO.retrievesSiteVisitTasks(reqDTO);
			
		
			this.setResultList(resDTO.getSmAppls());
		} finally {
			siteAssignSO = null;
		}
	}

	public void testNavigate() {
		System.out.println("testNavigate.........");
	}
	
	public void navigateToSiteVisitAssignment() {
		System.out.println("nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
		log.info("In navigateToSiteVisitAssignment");
		String selectedApplNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selApplNo"); 
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
			
		}
	}
	
	public void findDistrict() {

		log.info("*****************findDistrict***************");
		log.info("Id:" + this.getStateId());

		this.setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		log.info("*********************************");
		
			reqDTO.setStateId(this.getStateId());
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);

			for (PmDistrict district : resDTO.getDistrictList()) {

				this.getDistrictList().add(district);

			}


		log.info("this.getManagedBean().getDistrictList().size():"
				+ this.getDistrictList().size());

	}
	
	public List<SmAppl> getResultList() {
		return resultList;
	}

	public void setResultList(List<SmAppl> resultList) {
		this.resultList = resultList;
	}

	public String getApplNo() {
		return applNo;
	}

	public void setApplNo(String applNo) {
		this.applNo = applNo;
	}

	public List<PmState> getStateList() {
		return stateList;
	}

	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	public List<PmDistrict> getDistrictList() {
		if(districtList==null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}

	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}



	public long getStateId() {
		return stateId;
	}



	public void setStateId(long stateId) {
		this.stateId = stateId;
	}



	public long getDistrictId() {
		return districtId;
	}



	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}



	public String getSupplierName() {
		return supplierName;
	}



	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	
	
	
	
	
	
	
}
