package my.ep.web.backingbean.pm.usergroup;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.usergroup.UsergroupManagedBean;
import my.ep.web.managedbean.pm.usergroup.UsergroupSearchBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupBackingBean")
@ViewScoped
public class UsergroupBackingBean extends BaseBackingBean<UsergroupManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UsergroupBackingBean.class);
	
	private String NAV_TO_MAIN =  "userGroupSearchMain";
	
	private IPagingDataModel<PmUserGroup> userGroupList = new UserGroupProfilePagingDataModel();
	
	private Long orgProfileId = null;
	
	
	public UsergroupBackingBean() {
	}
	
	/**
	 *  reset search group detail
	 */
	public void reset() {
		 this.getManagedBean().setUsergroupSearchBean(new UsergroupSearchBean());
		 this.getManagedBean().setStatus(null);
		 userGroupList = new UserGroupProfilePagingDataModel(); 
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	
	/**
	 * get user group status record list
	 * 
	 */	
	 public SelectItem[] getUserGroupStatus() {
		 
			SelectItem[] items =  new SelectItem[2];
			items[0] = new SelectItem(1, "Active");
			items[1] = new SelectItem(0, "Inactive");
			return items;//PmCommonUtil.getStatusOptionsDefault();
		}	

	/**
	 *  pagination for user search list
	 */
	private class UserGroupProfilePagingDataModel extends
			PagingDataModel<PmUserGroup> {
		private static final long serialVersionUID = 1L;

		public UserGroupProfilePagingDataModel() {
			super("searchGroupProfile",false);
		}

		@Override
		protected ISearchResultBean<PmUserGroup> pageChangeListener(
				ISearchBean<PmUserGroup> searchBean) {
			populateSearchBean(searchBean);
		
			UserGroupReqDTO reqDTO = new UserGroupReqDTO();
			reqDTO.setUserGroupSearchBean(searchBean);
			IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserGroupSO.class);
			ISearchResultBean<PmUserGroup> iSearchResultBean = groupSO.retrieveUserGroups(reqDTO).getUserGroupResultBean();
			return iSearchResultBean;
		}

		/**
		 * @param userGrpBean
		 * @param searchBean
		 */
		private void populateSearchBean(ISearchBean<PmUserGroup> searchBean) {
			
			if(null==searchBean ){
				searchBean = new SearchBean<PmUserGroup>();
			}

			if (getManagedBean().getUserName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getUserName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmUserGroupUsers.pmUser.userName");
				codesSearchBean.setSearchValue(getManagedBean()
						.getUserName());
				codesSearchBean
						.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			
			if (getManagedBean().getGroupCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getGroupCode())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("groupCode");
				codesSearchBean.setSearchValue(getManagedBean()
						.getGroupCode());
				codesSearchBean
						.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			
			if (getManagedBean().getGroupName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getGroupName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("groupName");
				codesSearchBean.setSearchValue(getManagedBean()
						.getGroupName());
				codesSearchBean
						.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			
			if (getManagedBean().getPtjCode() != null
					&& !"".equalsIgnoreCase(getManagedBean().getPtjCode())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmOrgProfile.pmOrgValidity.orgCode");
				codesSearchBean.setSearchValue(getManagedBean()
						.getPtjCode());
				codesSearchBean
						.setCaseInsensitive(true);				
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			
			if (getManagedBean().getPtjName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getPtjName())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmOrgProfile.pmOrgValidity.orgName");
				codesSearchBean
						.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean()
						.getPtjName());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			
			if(getManagedBean().getStatus() != null
					&& !"".equals(getManagedBean().getStatus())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				codesSearchBean
						.setSearchField("recordStatusFlg");
				codesSearchBean.setSearchValue(getManagedBean().getStatus());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean.setSearchField("roleCode");
			codesSearchBean.setSearchValue("FL_APPROVER");
			codesSearchBean.setCaseInsensitive(true);				
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}
		

	}

	public IPagingDataModel<PmUserGroup> getUserGroupList() {
		return userGroupList;
	}

	/**
	 *  reload to user group creation page xhtml
	 */	
	public String createUserGroupProfiles() {
		PmUserGroup userGroup = new PmUserGroup();
		List<PmUserGroupUser> userGroupUser = new ArrayList<PmUserGroupUser>();
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(userGroupUser);
		
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";

	}

	/**
	 *  reload back to user group search page
	 */
	public String backToMainPages() {
		debugLogger("backToMainPages");
		return NAV_TO_MAIN;
	}

	/**
	 *  pagination for user search list
	 */
	public String goToMaintenanceAction(PmUserGroup userGroup) {
		this.getManagedBean().setUserGroup(userGroup);
		System.out.println("inside  maintanace...");
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}
	
	public void searchUserGroupAction() {
		 getUserGroupList().reloadData();
	}

	public void setUserGroupList(IPagingDataModel<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public void userGroupListner(List<PmUser> users) {
		List<PmUserGroupUser> groupUsers = new ArrayList<PmUserGroupUser>();
		if (null != users) {
			for (PmUser pmUser : users) {
				PmUserGroupUser groupUser = new PmUserGroupUser();
				groupUser.setPmUser(pmUser);
				groupUser.setPmUserGroup(this.getManagedBean().getUserGroup());
				groupUsers.add(groupUser);
			}
		}
		this.getManagedBean().setUserGroups(groupUsers);
	}
	
	public void  userListner(){
		orgProfileId = this.getManagedBean().getUserGroup().getPmOrgProfile().getOrgProfileId();
		this.getManagedBean().setOrgProfileId(orgProfileId);
		System.out.println("orgProfileId :"+orgProfileId);
		RequestContext.getCurrentInstance().update("usergrouping");
	}

	public void deleteUserGroup() {
		
		List<PmUserGroupUser> grpUsers = this.getManagedBean().getUserGroups();
		List<PmUserGroupUser> selectedUsrGrp = this.getManagedBean()
				.getSelectedUsrGrps();
		
		for (int i = 0; i < grpUsers.size(); i++) {
			for (int j = 0; j < selectedUsrGrp.size(); j++) {
				if (grpUsers
						.get(i)
						.getPmUser()
						.getIdentificationNo()
						.equalsIgnoreCase(
								selectedUsrGrp.get(j).getPmUser()
										.getIdentificationNo())) {
					grpUsers.remove(i);
					break;
				}
			}
		}
		this.getManagedBean().setUserGroups(grpUsers);
	}

	/**
	 *  pass object into user maintenance page
	 */
	public String gotoMaintanance(PmUserGroup userGroup) {
		debugLogger("gotoMaintanance");
		orgProfileId = userGroup.getPmOrgProfile().getOrgProfileId();
		this.getManagedBean().setOrgProfileId(orgProfileId);
		this.getManagedBean().setUserGroup(userGroup);
		this.getManagedBean().setUserGroups(userGroup.getPmUserGroupUsers());
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}
	
	/**
	 * Validation message for user group details
	 * 
	 */	
	private void userGroupValidationMsg(PmUserGroup userGroup) {
		debugLogger("userGroupValidationMsg");
		
		if(StringUtils.isBlank(userGroup.getGroupCode())){
			throw new EpException("Group Code is Mandatory");
		}
		
		if(StringUtils.isBlank(userGroup.getGroupName())){
			throw new EpException("Group Name is Mandatory");
		}	
		
		if(userGroup.getPmOrgProfile()==null){
			throw new EpException("PTJ Code and Name is Mandatory");
		}
		
		if(this.getManagedBean().getUserGroups().size()==0){
			throw new EpException("Please select atleast one profile.");
			}
	}

	/**
	 *  save or create user group details
	 */
	public String saveUserGroup() {
		debugLogger("saveUserGroup");
		PmUserGroup userGroup = this.getManagedBean().getUserGroup();
		userGroup.setRoleCode("FL_APPROVER");
		findDuplicateGroup(userGroup);
		userGroup.setGroupCode(userGroup.getGroupCode().toUpperCase());
		userGroupValidationMsg(userGroup);
		
		List<PmUserGroupUser> pmUserGroupUsers = this.getManagedBean().getUserGroups();
		
		userGroup.setPmUserGroupUsers(pmUserGroupUsers );
		
		UserGroupReqDTO reqDto = new UserGroupReqDTO();
		reqDto.setUserGroup(userGroup);
		
		IUserGroupSO userGrpSo = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);

		UserGroupResDTO resDTO = userGrpSo.saveOrUpdateUserGroup(reqDto);
		
		 this.getManagedBean().setUserGroup(resDTO.getUserGroup());
		 if(resDTO.isSuccessful()){
				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,"","Saved Successfully"));
			}
		
		  return NAV_TO_MAIN;
	 }
	
	public String constuctConditionFields() {
		 orgProfileId = this.getManagedBean().getOrgProfileId();
			if (null != orgProfileId) {
				return "pmUserOrgs.pmOrgProfile.orgProfileId^" + orgProfileId+"|"+"pmUserOrgs.pmUserRoles.pmRole.roleCode^"+"FL_APPROVER";
		           }
		return "";
	}
	
	private void findDuplicateGroup(PmUserGroup userGroup){
		
		ISearchBean<PmUserGroup> searchBean = null; 
			UserGroupReqDTO reqDTO = new UserGroupReqDTO();
			reqDTO.setUserGroupSearchBean(searchBean);
			IUserGroupSO groupSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserGroupSO.class);
			ISearchResultBean<PmUserGroup> iSearchResultBean = groupSO.retrieveUserGroups(reqDTO).getUserGroupResultBean();
			List<PmUserGroup> pmUserGroups = iSearchResultBean.getResultList();
			for(PmUserGroup pmUserGroup:pmUserGroups){
				if(pmUserGroup.getGroupCode().equalsIgnoreCase(userGroup.getGroupCode())&&(pmUserGroup.getPmOrgProfile().getOrgProfileId()==(userGroup.getPmOrgProfile().getOrgProfileId()))){
					throw new EpException("Duplicate Group name please.");
				}
			}
			System.out.println("findDuplicateGroup :"+pmUserGroups.size());

     }
	
	public void preLoadgroupSearch(){
		this.reset();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}
}

