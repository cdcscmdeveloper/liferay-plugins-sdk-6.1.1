package my.ep.web.managedbean.pm.userprofile;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.web.managedbean.pm.profilepersonalization.ProfilePersonalizationManagedBean;

import org.apache.log4j.Logger;


/**
 * @author DK
 *
 */
@ManagedBean(name="userProfileManagedBean")
@SessionScoped
public class UserProfileManagedBean extends ProfilePersonalizationManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(UserProfileManagedBean.class);	
	
	

}	
	

