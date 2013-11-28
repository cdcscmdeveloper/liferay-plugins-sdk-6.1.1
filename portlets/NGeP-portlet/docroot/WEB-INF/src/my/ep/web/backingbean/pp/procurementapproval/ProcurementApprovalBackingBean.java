package my.ep.web.backingbean.pp.procurementapproval;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.procurementapproval.ProcurementApprovalManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name="procurementApprovalBackingBean")
@RequestScoped
public class ProcurementApprovalBackingBean extends BaseBackingBean<ProcurementApprovalManagedBean>{
	private static Logger log = Logger.getLogger(ProcurementApprovalBackingBean.class);
	private final static Long USER_ID_PTJ= 4L; //TEMP
	/**
	 * constructor of the backingbean
	 */
	public ProcurementApprovalBackingBean() {
		log.info("ProcurementApprovalBackingBean - Start");
		if(this.getManagedBean().getPendingApprovalPlans()==null 
				|| (this.getManagedBean().getPendingApprovalPlans()!=null && this.getManagedBean().getPendingApprovalPlans().isEmpty())){
			
			//set current user profile
			List <PmOrgProfile> ptjProfiles = getOrgProfiles(USER_ID_PTJ, OrganizationType.DIVISION.value());// TODO dynamic
			this.getManagedBean().setCurrentUser(ptjProfiles!=null ? ptjProfiles.get(0) : new PmOrgProfile());

			retrievePendingApprovalList(this.getManagedBean().getCurrentUser().getOrgProfileId());
			
		}
		
		
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, getFacesScopes().getUserSessionBean().getUserId());
		}
		log.info("ProcurementApprovalBackingBean - End");
	}
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	
	/**
	 * retrieve list of pending approval plans
	 * @param loginUser
	 */
	private void retrievePendingApprovalList(Long loginUser){
		log.info("retrievePendingApprovalList - Start");
		List<PpPlan>planList = new ArrayList<PpPlan>();
		ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
		planReqDTO.setLoginPtj(loginUser);
		IProcurementPlanSO planSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject("pp", IProcurementPlanSO.class);
		ProcurementPlanResDTO planResDTO = planSO.retrievePendingApprovalList(planReqDTO);
		if(planResDTO.isSuccessful()){
			log.info("success");
			planList = planResDTO.getPlanList();
		}
		this.getManagedBean().setPendingApprovalPlans(planList);
		log.info("retrievePendingApprovalList - End");

	}
	
	/**
	 * generate header for view based on the viewMode
	 * @param viewMode
	 * @return
	 */
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_PLAN);
	}
	
	/**
	 * retrieve org profile by login user and type
	 * @param userId
	 * @param type
	 * @return org profile
	 */
	private List <PmOrgProfile> getOrgProfiles(Long userId, String orgType){
		log.info("getOrgProfiles - Start");
		log.info("userId " + userId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(userId) && CommonMethod.isSelected(orgType)){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(orgType);
				
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getOrgProfileList();
					System.out.println("size..." + profileList.size());
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
	
	public String approvePlans(){
		log.info("approvePlans - Start");
		String returnPage = "";
		boolean isSuccess = false;
		
		isSuccess = updatePlanMaster();
		
		if(isSuccess){
			returnPage = ProcurementPlanConst.NAV_TO_TASK_LIST;
		}
		log.info("isSuccess: " + isSuccess + ", returnPage: "+ returnPage);
		log.info("approvePlans - End");
		return returnPage;
	}
	
	private boolean updatePlanMaster(){
		log.info("updatePlanMaster - Start");
		boolean result  = false;
		try{
			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
			ProcurementPlanResDTO resDTO = new ProcurementPlanResDTO();
			
			for(PpPlan plan:this.getManagedBean().getPendingApprovalPlans()){
				if(plan.isApproved()){
//					reqDTO.setUpdateBudget(false);
//					reqDTO.setAction(ProcurementPlanConst.BPM_APPROVE);
//					reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
//					reqDTO.setTaskId(this.getManagedBean().getBpmTaskId());
//					reqDTO.setPlanMaster(plan.getPpPlanMaster());
//					IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
//					resDTO =  procurementPlanSO.updatePlan(reqDTO);
//					
//					result = resDTO.isSuccessful();
				}
			}
			result = resDTO.isSuccessful();
			log.info("updatePlan result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			
		}

		log.info("updatePlanMaster - End");
		return result;
		
	}

}
