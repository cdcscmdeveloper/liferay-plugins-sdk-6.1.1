package my.ep.web.backingbean.pm.userprofile;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

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
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.userprofile.UserprofilesManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

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
	 * retrive pmuser
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
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
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
	 * retrive pmusergroup
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
		
		userReqDTO.setUserGroupSearchBean(searchBean);

		try {
			IUserSO userSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if(resDto !=null){
				this.getManagedBean().setUsers(resDto.getUserResultBean().getResultList());
			}
			System.out.println("#########################################"+resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
	}
	
	public UserprofilesBackingBean() {
		retriveUserDtls();

	}	
	
	private void retriveUserDtls() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser user = facesScopes.getUserSessionBean();
		if (user != null && null != user.getUserId()) {
			this.getManagedBean().setPmUser(user);
			List<PmAddressType> addressType = user.getPmAddressTypes();
			
			if (addressType != null) {
				for (PmAddressType addressType2 : addressType) {
					if (addressType2.getAddressType().equalsIgnoreCase("C")) {
						this.getManagedBean().setPmAddress(
								addressType2.getPmAddress());
						logger.info(addressType2.getPmAddress());

						break;
					}
				}
			}
		} else {
			logger.debug("Unable to get the userDetails");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to Retrive User Details", ""));
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
	
    public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  	
	

}
