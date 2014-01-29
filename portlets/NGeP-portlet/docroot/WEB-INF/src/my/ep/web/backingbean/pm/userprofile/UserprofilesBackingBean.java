package my.ep.web.backingbean.pm.userprofile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.userprofile.UserprofilesManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "userprofilesBackingBean")
@RequestScoped
public class UserprofilesBackingBean extends
		BaseBackingBean<UserprofilesManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UserprofilesBackingBean.class);

	private IPagingDataModel<PmUser> userProfileDataModel = new UserProfilePagingDataModel();	
	
    public void reset() {  
        RequestContext.getCurrentInstance().reset("f:searchProfile");  
    }  
		
	/**
	 * retrive qtcommittees
	 * 
	 * @return
	 */
	public void retriveUserProfile() {
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
		
		

		/*if (getManagedBean().getUserRole() != null) {
			SearchFilterBean userRoleSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			userRoleSearchBean.setSearchField("scCommittee.createdDate");
			userRoleSearchBean.setSearchValue(getManagedBean().getUserRole());
			searchBean.getDataFilterBeans().add(userRoleSearchBean);
		}

		if (getManagedBean().getUserorganizationNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserorganizationNames())) {
			SearchFilterBean UserOrgNamesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			UserOrgNamesSearchBean.setSearchField("scQt.qtNo");
			UserOrgNamesSearchBean.setSearchValue(getManagedBean().getUserorganizationNames());

			searchBean.getDataFilterBeans().add(UserOrgNamesSearchBean);
		}
		if (getManagedBean().getUserStatus() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserStatus())) {
			
			 * SearchFilterBean statusSearchBean = new SearchFilterBean(
			 * SearchOperator.OPERATOR_EQUALS.value());
			 * statusSearchBean.setSearchField("scQt.qtNo");
			 * statusSearchBean.setSearchValue(getManagedBean().getQtNo());
			 * 
			 * searchBean.getDataFilterBeans().add(statusSearchBean);
			 
		}*/
		userReqDTO.setUserSearchBean(searchBean);

		try {
			IUserSO userSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.PM_MODULE, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if(resDto !=null){
				this.getManagedBean().setUsers(resDto.getUserResultBean().getResultList());
			}
			System.out.println("#########################################"+resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}
	
	/**
	 * retrive qtcommittees
	 * 
	 * @return
	 */
	public void retriveUserGroupProfile() {
		UserReqDTO userReqDTO = new UserReqDTO();
		SearchBean<PmUserGroup> searchBean = new SearchBean<PmUserGroup>();		
		searchBean.setNavigatable(false);
		UserResDTO resDto = null;

		if (getManagedBean().getUserGroupNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserGroupNames())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean().getUserGroupNames());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

		}
		
		

		/*if (getManagedBean().getUserRole() != null) {
			SearchFilterBean userRoleSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			userRoleSearchBean.setSearchField("scCommittee.createdDate");
			userRoleSearchBean.setSearchValue(getManagedBean().getUserRole());
			searchBean.getDataFilterBeans().add(userRoleSearchBean);
		}

		if (getManagedBean().getUserorganizationNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserorganizationNames())) {
			SearchFilterBean UserOrgNamesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			UserOrgNamesSearchBean.setSearchField("scQt.qtNo");
			UserOrgNamesSearchBean.setSearchValue(getManagedBean().getUserorganizationNames());

			searchBean.getDataFilterBeans().add(UserOrgNamesSearchBean);
		}
		if (getManagedBean().getUserStatus() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserStatus())) {
			
			 * SearchFilterBean statusSearchBean = new SearchFilterBean(
			 * SearchOperator.OPERATOR_EQUALS.value());
			 * statusSearchBean.setSearchField("scQt.qtNo");
			 * statusSearchBean.setSearchValue(getManagedBean().getQtNo());
			 * 
			 * searchBean.getDataFilterBeans().add(statusSearchBean);
			 
		}*/
		userReqDTO.setUserGroupSearchBean(searchBean);

		try {
			IUserSO userSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.PM_MODULE, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if(resDto !=null){
				this.getManagedBean().setUsers(resDto.getUserResultBean().getResultList());
			}
			System.out.println("#########################################"+resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}	
	

	private class UserProfilePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public UserProfilePagingDataModel() {
			super("searchProfile");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			// TODO Auto-generated method stub
			return null;
		}


	}		
	
	public IPagingDataModel<PmUser> getUserProfileDataModel() {
		return userProfileDataModel;
	}

	public void setUserProfileDataModel(
			IPagingDataModel<PmUser> userProfileDataModel) {
		this.userProfileDataModel = userProfileDataModel;
	}

	public String goToMaintainceAction(PmUser user) {
		this.getManagedBean().setPmUser(user);
		return "/xhtml/pm/profilemanagement/userProfileCreation.xhtml";
	}	
	

}
