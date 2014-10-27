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
import my.ep.web.bean.pm.UserGroupSearchBean;
import my.ep.web.managedbean.pm.usergroup.UsergroupManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupBackingBean")
@ViewScoped
public class UsergroupBackingBean extends BaseBackingBean<UsergroupManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UsergroupBackingBean.class);

	private IPagingDataModel<PmUserGroup> userGroupList = new UserGroupProfilePagingDataModel();

	private UserGroupSearchBean userGrpBean = new UserGroupSearchBean();

	
	/**
	 *  reset search group detail
	 */
	public void reset() {
		userGrpBean = new UserGroupSearchBean();

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
		    return items;
		}	

	/**
	 *  pagination for user search list
	 */
	private class UserGroupProfilePagingDataModel extends
			PagingDataModel<PmUserGroup> {
		private static final long serialVersionUID = 1L;

		public UserGroupProfilePagingDataModel() {
			super("searchGroupProfile");
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
			return groupSO.retrieveUserGroups(reqDTO).getUserGroupResultBean();
		}

		/**
		 * @param userGrpBean
		 * @param searchBean
		 */
		private void populateSearchBean(ISearchBean<PmUserGroup> searchBean) {

		
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
			
			if (getManagedBean().getRole() != null
					&& !"".equalsIgnoreCase(getManagedBean().getRole())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("pmUserGroupUsers.pmUser.pmUserOrgs.pmUserRoles.pmRole.roleCode");
				codesSearchBean.setSearchValue(getManagedBean()
						.getRole());
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
				codesSearchBean.setSearchValue(getManagedBean()
						.getStatus());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}


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
		reset();
		return "/xhtml/pm/usergroupsearch/userGroupSearchMain.xhtml";
	}

	/**
	 *  pagination for user search list
	 */
	public String goToMaintenanceAction(PmUserGroup userGroup) {
		this.getManagedBean().setUserGroup(userGroup);
		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	}

	public void searchUserGroupAction() {
		userGroupList.reloadData();
	}

	public UserGroupSearchBean getUserGrpBean() {
		return userGrpBean;
	}

	public void setUserGrpBean(UserGroupSearchBean userGrpBean) {
		this.userGrpBean = userGrpBean;
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
	}

	/**
	 *  save or create user group details
	 */
	public String saveUserGroup() {
		debugLogger("saveUserGroup");
		
		PmUserGroup userGroup = this.getManagedBean().getUserGroup();
		userGroupValidationMsg(userGroup);
		
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
		
		  return "/xhtml/pm/usergroupsearch/userGroupSearchMain.xhtml";
	 }
		
}

