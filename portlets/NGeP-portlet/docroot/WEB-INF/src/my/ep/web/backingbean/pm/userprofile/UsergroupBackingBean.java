package my.ep.web.backingbean.pm.userprofile;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.userprofile.UsergroupManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "usergroupBackingBean")
@RequestScoped
public class UsergroupBackingBean extends BaseBackingBean<UsergroupManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UsergroupBackingBean.class);

	private IPagingDataModel<PmUser> UserGroupList = new UserGroupProfilePagingDataModel();

	// reset search group detail
	public void reset() {

		this.getManagedBean().setUserNames(null);
		this.getManagedBean().setRoleNames(null);
		this.getManagedBean().setPtjCodes(null);
		this.getManagedBean().setPtjNames(null);

	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	/**
	 * search user group 
	 * 
	 * @return
	 */
	public void retriveUserGroupProfile() {
		UserReqDTO userReqDTO = new UserReqDTO();
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		searchBean.setNavigatable(false);
		UserResDTO resDto = null;

		if (getManagedBean().getUserNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserNames())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean().getUserNames());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

		}  

		userReqDTO.setUserSearchBean(searchBean);

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setUserGroup(
						resDto.getUserResultBean().getResultList());
			}
			System.out.println("######USER GROUP RESULT######"
					+ resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// pagination for user search list
	private class UserGroupProfilePagingDataModel extends
			PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public UserGroupProfilePagingDataModel() {
			super("searchGroupProfile");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public IPagingDataModel<PmUser> getUserGroupList() {
		return UserGroupList;
	}

	public void setUserGroupList(IPagingDataModel<PmUser> userGroupList) {
		UserGroupList = userGroupList;
	}

	// reload to user group creation page xhtml
	public String createUserGroupProfiles() {

		return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";

	}
	
    // back to main screen
	public String backToMainPages() {
		reset();

		return "/xhtml/pm/usergroupsearch/userGroupSearchMain.xhtml";
	}

	// get user group detail into maintenance pages 
	 public String goToMaintenanceAction(PmUser pmUser) {
	 this.getManagedBean().setPmUser(pmUser);	
	
	 return "/xhtml/pm/usergroupsearch/userGroupMaintenance.xhtml";
	 }

}
