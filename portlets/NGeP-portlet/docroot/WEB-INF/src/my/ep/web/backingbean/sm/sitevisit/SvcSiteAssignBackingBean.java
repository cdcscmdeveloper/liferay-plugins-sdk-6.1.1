package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvOfficer;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	public SvcSiteAssignBackingBean() {
//		if (MDC.get(LogKey.USER_ID) == null) {
//			MDC.put(LogKey.USER_ID, 1L);
//		}
	}

	public void retrieveSiteAssignInfo() {
		retrieveSVOList();
		//this.getManagedBean().setAppId(1009);
		//Old Version - Working set
		log.info("----------------------retrieveSiteAssignInfo start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
		log.info("----------------------retrieveSiteAssignInfo----------------" + this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		SiteAssignResDTO resDTO = reqSO.retrieveSiteVisitByApplId(reqDTO);
		this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
		this.getManagedBean().setSmRemark(new SmRemark());
	}
	
	
	public String submitSiteVisitAssignment(String action){
		log.info("----------------------submitSiteVisitAssignment start----------------");
		
		try{
			SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
			ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISiteAssignSO.class);
			String token = getFacesScopes().getBPMAuthenticationCtxToken();

			SmSvTask svTask = this.getManagedBean().getSmSvTask();
			svTask.setSmSvOfficers(new ArrayList<SmSvOfficer>());

			for(PmUser pmUsr : this.getManagedBean().getSvoUserList().getTarget()){
				SmSvOfficer svOfficer = new SmSvOfficer();
				svOfficer.setSmSvTask(svTask);
				svOfficer.setPmUser(pmUsr);
				svTask.getSmSvOfficers().add(svOfficer);
			}
			log.info("Task ID>>>>>" + this.getManagedBean().getSmSvTask().getSvTaskId());
			//System.out.println("****************** submitSiteVisitAssignment *****" + svTask.getSmSvOfficers().get(0).getPmUser());
			this.getManagedBean().getSmRemark().setDocType(SupplierManagementConst.SM_RMK_DOC_TYPE_SVC);
			this.getManagedBean().getSmRemark().setDocId(this.getManagedBean().getSmSvTask().getSvTaskId());
			
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setBpmPojo(this.getManagedBean().getBpmPojo());
			reqDTO.setSmSvTask(svTask);
			reqDTO.setToken(token);
			reqDTO.setSmRemark(this.getManagedBean().getSmRemark());
			SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("Submission is success>>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721201,
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				log.info("Submission is failed >>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721202);
			}
		}catch(Exception ex){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
			throw new EpException(MessageConst.ERROR_721202);
		}

		return null;
	}
	

	private void retrieveSVOList(){
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setRoleCode(SupplierManagementConst.SVO); //pending, select user base on role code.
		IUserSO reqSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
		
		this.getManagedBean().setSourceSVO(new ArrayList<PmUser>());
		this.getManagedBean().setSelectedSVO(new ArrayList<PmUser>());

		log.info("********PM SVO********" + resDTO.getUserList().size());
		for(PmUser usr : resDTO.getUserList()){
			this.getManagedBean().getSourceSVO().add(usr);
			System.out.println("************SVO user" + usr.getUserName());
		}
		
		this.getManagedBean().setSvoUserList(new DualListModel<PmUser>(this.getManagedBean().getSourceSVO(), this.getManagedBean().getSelectedSVO()));
	}
	
	public void validateForm(){
		if(this.getManagedBean().getSvoUserList().getTarget().size() < 1){
			throw new EpException(MessageConst.ERROR_721201);
		}else{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
		}
	}
	
	public String navigateToAck(){
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721103,
		MessageUtils.BUNDLE_COMMON_MESSAGES,
		new FacesScopes().getUserLocale())));

		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}

	//for local testing purpose
	public void preRenderView(){
		if(this.getManagedBean().getSmSvTask() == null){
			retrieveSiteAssignInfo();
		}
	}
}
