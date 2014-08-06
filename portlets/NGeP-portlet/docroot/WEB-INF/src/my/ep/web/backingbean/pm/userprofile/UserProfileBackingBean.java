package my.ep.web.backingbean.pm.userprofile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.pm.ProfileManagementConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.userprofile.UserProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "userProfileBackingBean")
@RequestScoped
public class UserProfileBackingBean extends BaseBackingBean<UserProfileManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(UserProfileBackingBean.class);
	
	public static final String NAV_TO_MYPROFILE = "/xhtml/pm/userprofiles/userprofilesmain.xhtml"; 
	/**
	 * load clean view for my profile
	 */
	public void preLoadingMyProfile(){
		logger.info("preLoadingMyProfile");
		FacesScopes facesScopes = new FacesScopes();
		PmUser sUsers = facesScopes.getUserSessionBean();
		if(sUsers==null){
			throw new EpException("Unable to get the user details."); 
		}
		loadUserProfile(sUsers.getUserId());
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MYPROFILE);
	}
	
	private void loadUserProfile(Long id){
		logger.info("loadUserProfile");
		try {
			//load user
			PmUser pmUser = PmCommonUtil.retrieveUserById(id);
			this.getManagedBean().setSelectedUser(pmUser);
			if (pmUser!=null && pmUser.getPmAddressTypes() != null) {
				populateUserAddress(pmUser);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to view User Profile.", new String[]{e.getMessage()}); //TODO exception
		}	
	}
	
	/**
	 * populate user address to managed bean
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser){
		logger.info("populateUserAddress");
		try {
			for (PmAddressType addressTypeObj : pmUser.getPmAddressTypes()) {
				if (addressTypeObj.getAddressType().equalsIgnoreCase(ProfileManagementConst.ADDRESS_USER)) {
					this.getManagedBean().setUserAddress(addressTypeObj.getPmAddress());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to retrieve/populate address.", new String[]{e.getMessage()}); //TODO exception
		}
	}
	
}
