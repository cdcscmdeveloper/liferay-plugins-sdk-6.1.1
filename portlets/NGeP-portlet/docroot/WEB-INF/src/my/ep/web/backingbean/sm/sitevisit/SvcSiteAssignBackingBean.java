package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmApplSiteVisit;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	public SvcSiteAssignBackingBean() {
		retrieveSVOList();
	}

	public void retrieveSiteAssignInfo() {
		this.getManagedBean().setAppId(1009);
		//retrieveSVOList();
		//this.getManagedBean().setSmApplSv(new SmApplSiteVisit());
		//this.getManagedBean().getSmApplSv().get
		
		//Old Version - Working set
		log.info("----------------------retrieveSiteAssignInfo start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteAssignSO.class);
		log.info("----------------------retrieveSiteAssignInfo----------------" + this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		//reqDTO.setAppId(2);
		SiteAssignResDTO resDTO = reqSO.retrieveSiteVisitByApplId(reqDTO);
		log.info("*********Address Size*************" + resDTO.getSmApplSv().getSmSiteVisitAddresses().size());

		this.getManagedBean().setSmApplSv(resDTO.getSmApplSv());
	}
	
	public String submitSiteVisitAssignment(String action){
		log.info("----------------------submitSiteVisitAssignment start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISiteAssignSO.class);
		String bpmUsrName = new FacesScopes().getUserScreenName();
		
		//reqDTO.setAppId(this.getManagedBean().getAppId());
		reqDTO.setOutcome(action);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setBpmUserName(bpmUsrName);
		reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
		
		SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);
		
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Site Visit has been submitted.", ""));
			return "/xhtml/sm/sitevisit/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}
	
	public String submitSiteVisitTest (){
		

		return "";
	}
	

	private void retrieveSVOList(){
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setRoleCode(SupplierManagementConst.SVO_HQ);
		IUserSO reqSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
		
		this.getManagedBean().setSourceSVO(new ArrayList<PmUser>());
		this.getManagedBean().setSelectedSVO(new ArrayList<PmUser>());

		for(PmUser usr : resDTO.getUserList()){
			this.getManagedBean().getSourceSVO().add(usr);
		}
		
		this.getManagedBean().setSvoUserList(new DualListModel<PmUser>(this.getManagedBean().getSourceSVO(), this.getManagedBean().getSelectedSVO()));
	}
}
