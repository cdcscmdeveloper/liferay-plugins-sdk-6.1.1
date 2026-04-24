package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

public class ApproverSettings {

	private static final Logger log = EpLogger
			.getLogger(ApproverSettings.class);
	
	//12471 - start
	/**
	 * Retrieve approver user list and generate its pick list model
	 * @param code
	 * @param orgProfileId
	 * @param approval
	 * @return DualListModel
	 */
	public static DualListModel<ScQtApproverSettings> getDetails(RoleCode code, Long[] orgProfileId, ScQtApproval approval){
		return getDetails(code, orgProfileId, approval, null);
	}
	/**
	 * Retrieve approver user list and generate its pick list model
	 * Method overload to help update user object as disabled if exist in disabled list
	 * @param code
	 * @param orgProfileId
	 * @param approval
	 * @param disabledUserIds
	 * @return DualListModel
	 */
	public static DualListModel<ScQtApproverSettings> getDetails(RoleCode code, Long[] orgProfileId, ScQtApproval approval, List<Long> disabledUserIds){
		
		List<ScQtApproverSettings> availableApprovers = new ArrayList<>();
		List<ScQtApproverSettings> selectedApprovers = new ArrayList<>();
		List<PmUser> approvers = new ArrayList<>();
		for(Long opid:orgProfileId){
			List<PmUser> res=getApprovers(code,opid);
			for(PmUser u:res){
				if(!isApproverExist(approvers,u.getUserId())){
					approvers.add(u);
				}
			}
		}
		for(PmUser pmUser:approvers){
			ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
			qtApproverSettings.setPmUser(pmUser);
			qtApproverSettings.setScQtApproval(approval);
			if(checkSelectedApprover(approval,pmUser.getUserId())){
				selectedApprovers.add(qtApproverSettings);
			}else{
				if(disabledUserIds!=null && disabledUserIds.contains(qtApproverSettings.getPmUser().getUserId())){
					qtApproverSettings.setDisableRow(true);
				}
				availableApprovers.add(qtApproverSettings);
			}
			
		}		
		
		return new DualListModel<>(availableApprovers,
				selectedApprovers);
	}
	//12471 - end
	
	private static boolean checkSelectedApprover(ScQtApproval approval,Long userId){
		if (null != approval) {
			if (null != approval.getApproverSettings()
				&& !approval.getApproverSettings().isEmpty()) {
				for(ScQtApproverSettings scQtApproverSettings : approval
						.getApproverSettings()){
					
					if (scQtApproverSettings.getPmUser().getUserId()
							.equals(userId)) {
						return true;
					}
					
				}
				
			}
		}

		return false;
	}
	
	private static boolean isApproverExist(List<PmUser> approvers,Long userId){
		for(PmUser u:approvers){
			if(u.getUserId().equals(userId)){
				return true;
			}
		}
		
		return false;
	}
	
	
	private static List<PmUser> getApprovers(RoleCode code, Long orgProfileId){
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(code.value());
		userReqDto.setOrgProfileId(orgProfileId);
		userReqDto.setWithProfileGroup(true);
		UserResDTO userResDTO = iUserSO
				.retrieveUserListWithProfileGroupUserByRole(userReqDto);
		return userResDTO.getUserList();
	}
	
	private static void removeDeskOfficer(List<PmUser> approvers) {
	      log.debug("removeDeskOfficer");
			if (CollectionUtils.isNotEmpty(approvers)) {
				PmUser currentDO = new FacesScopes().getUserSessionBean();
				//boolean isDO = false;
				//int index = 0;
				for (PmUser pmUser : approvers) {
					if (pmUser.getUserId().equals(currentDO.getUserId())) {
						log.info("loopremoveDeskOfficer");
						//isDO = true;
						//index++;
						approvers.remove(pmUser);
						break;
					}
				}
				/*if (isDO) {
					approvers.remove(index);
				}*/
			}
			log.debug("endremoveDeskOfficer");
		}
}
