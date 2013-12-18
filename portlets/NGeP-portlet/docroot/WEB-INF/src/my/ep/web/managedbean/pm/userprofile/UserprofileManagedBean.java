package my.ep.web.managedbean.pm.userprofile;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;


/**
 * @author DK
 *
 */
@ManagedBean(name="userprofileManagedBean")
@SessionScoped
public class UserprofileManagedBean extends BaseManagedBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(UserprofileManagedBean.class);	
	
	private PmUser pmUser;
	
	private PmAddress pmAddress;
	
	private String loginId;	
	
	private String userName;	
		
	
	
	public String getLoginId() {
		return loginId;
	}



	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}


	public PmUser getPmUser() {
		return pmUser;
	}



	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}



	public PmAddress getPmAddress() {
		return pmAddress;
	}



	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}



	public UserprofileManagedBean(){
		log.info("inside ");		
	}	
	

}
