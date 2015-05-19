package my.ep.web.backingbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.siteVisit.SiteVisitReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvOfficer;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.sitevisit.SvcSiteAssignManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "svcSiteAssignBackingBean")
@RequestScoped
public class SvcSiteAssignBackingBean extends
		BaseBackingBean<SvcSiteAssignManagedBean> {
	public static Logger log = Logger.getLogger(SvcSiteAssignBackingBean.class);
	
	public SvcSiteAssignBackingBean() {

	}

	/**
	 * retrieve site visit assignment Info by applId
	 * and populate on screen
	 */
	public void retrieveSiteAssignInfo() {
		//this.getManagedBean().setAppId(1259020L);
		//Old Version - Working set
		log.info("----------------------retrieveSiteAssignInfo start----------------");
		SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
		log.info("----------------------retrieveSiteAssignInfo----------------" + this.getManagedBean().getAppId());
		reqDTO.setAppId(this.getManagedBean().getAppId());
		SiteAssignResDTO resDTO = reqSO.retrieveSiteVisitByApplId(reqDTO);
		
		retrieveSVOList(resDTO.getSmSvTask().getSmAppl());	
		if(resDTO.getSmSvTask().getSmAppl().getSmApplSectionReviews() != null){
			setPoRemarkToSVC(resDTO.getSmSvTask().getSmAppl().getSmApplSectionReviews());
		}
		this.getManagedBean().setSmSvTask(resDTO.getSmSvTask());
		this.getManagedBean().setSmRemark(new SmRemark());
		setSiteVisitAddressesFacilityName();
	}
	
	/**
	 * set PO Remark and populate
	 * @param List<SmApplSectionReview> secReviews
	 */
	public void setPoRemarkToSVC(List<SmApplSectionReview> secReviews){
		for(SmApplSectionReview rev : secReviews){
			if(rev.getRoleType() != null && rev.getRoleType().equalsIgnoreCase(
					SupplierManagementConst.SECTION_REV_ROLE_PO)){
				getManagedBean().setPoRmrkToSvc((rev.getSitevisitRemark()!=null) ? rev.getSitevisitRemark():"");
			}
		}
	}
	
	/**
	 * Submit Site Visi Assignment
	 * @param String action
	 * return String
	 */
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
			reqDTO.setTrackingDairyVO(
					SmCommonUtil.assignTrackingDiaryValues(SupplierManagementConst.SVC_ASSIGN_SITEVISIT, 
							this.getManagedBean().getSmSvTask().getSmAppl().getApplId(), this.getManagedBean().getSmSvTask().getSmAppl().getApplNo(), getUserRole()));
			SiteAssignResDTO resDTO = reqSO.submitBPMTaskAssignment(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("Submission is success>>>>>>>>"); 
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721201,
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
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

	}
	
	/**
	 * retrieve the SVO based on supplier type and zone. 
	 * Populate the SVO List on screen for SVC to pick up
	 * @param SmAppl smAppl - SmAppl Object
	 */
	private void retrieveSVOList(SmAppl smAppl){
		SiteAssignReqDTO userListReqDTO = new SiteAssignReqDTO();
		ISiteAssignSO reqSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);
		
		userListReqDTO.setSmAppl(smAppl);
		SiteAssignResDTO userListResDTO = reqSO.retrieveSVOListBySupplierTypeAndZone(userListReqDTO);
		
		this.getManagedBean().setSourceSVO(new ArrayList<PmUser>());
		this.getManagedBean().setSelectedSVO(new ArrayList<PmUser>());
		
		log.info("********PM SVO********" + userListResDTO.getPmUsers().size());
		if(userListResDTO.getPmUsers().size() > 0){
			this.getManagedBean().setSourceSVO(userListResDTO.getPmUsers());
		}

		this.getManagedBean().setSvoUserList(new DualListModel<PmUser>(this.getManagedBean().getSourceSVO(), this.getManagedBean().getSelectedSVO()));
	}
	
	/**
	 * Validation - to ensure at least 2 SVO being selected
	 */
	public void validateForm(){
		if(this.getManagedBean().getSvoUserList().getTarget().size() < 2){
			throw new EpException(MessageConst.ERROR_721201);
		}else{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
		}
	}
	
	/**
	 * Navigation to Acknowledgement page
	 * @return String
	 */
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
	
	/**
	 * get Supplier Application Type Description
	 * @param String applType - application type
	 * @return String
	 */
	public String getsupplierApplTypeDesc(String applType){
		return SmCommonUtil.getApplTypeDesc(applType);
	}

	//for local testing purpose
	public void preRenderView(){
		if(this.getManagedBean().getSmSvTask() == null){
			retrieveSiteAssignInfo();
		}
	}
	
	/**
	 * to get User Role - SVC
	 * @return String
	 */
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.UPS_SVC.value()))
			return RoleCode.UPS_SVC.value();		
		else 
			return null;
	}
	
	/**
	 * Set site visit address list facility name in data table
	 */
	private void setSiteVisitAddressesFacilityName() {
		SmFacility smFacility = null;
		SiteVisitReqDTO reqDTO = new SiteVisitReqDTO();
		if (getManagedBean().getSmSvTask() != null
				&& getManagedBean().getSmSvTask().getSmSvAddresses() != null
				&& !getManagedBean().getSmSvTask().getSmSvAddresses().isEmpty()) {
			for (SmSvAddress svAddress : getManagedBean().getSmSvTask()
					.getSmSvAddresses()) {
				if (svAddress.getSmAddress() != null) {
					reqDTO.setSmAddressId(svAddress.getSmAddress()
							.getAddressId());
					smFacility = SmServiceLookup.getSiteVisitSO()
							.retrieveSmFacilityByAddressId(reqDTO)
							.getSmFacility();
					svAddress.setFacilityName(smFacility == null ? ""
							: smFacility.getFacilityName());
					if(smFacility != null && smFacility.getFacilityType() != null){
						svAddress.setFacilityType(smFacility.getFacilityType());
					}
				}
			}
			
			sortSmSvAddressByFacilityType();
		}
	}
	
	/**
	 * Sort smSvAddress by facility type
	 */
	private void sortSmSvAddressByFacilityType(){
		if(getManagedBean().getSmSvTask().getSmSvAddresses().size() > 0){
			 Collections.sort(getManagedBean().getSmSvTask().getSmSvAddresses(), new Comparator<SmSvAddress>(){
				@Override
				public int compare(SmSvAddress arg0, SmSvAddress arg1) {
					List<String> orderLs = Arrays.asList("B", "H", "D", "F","S","W");
					
					int result = 0;
					int argIndex0 = orderLs.indexOf(arg0.getFacilityType());
					int argIndex1 = orderLs.indexOf(arg1.getFacilityType());
					
				    //if neither are found in the order list, then do natural sort
				    //if only one is found in the order list, float it above the other
				    //if both are found in the order list, then do the index compare
					
					if (argIndex0 < 0 && argIndex1 < 0){ 
						if(arg0.getFacilityType() != null && arg1.getFacilityType() != null){
							result = arg0.getFacilityType().compareTo(arg1.getFacilityType());
						}
					}else if (argIndex0 < 0){ 
						result = 1;
					}else if (argIndex1 < 0){ 
						result = -1;
					}else{ 
						result = argIndex0 - argIndex1;
					}
				    return result;
				}
			 });
		}
	}
}
