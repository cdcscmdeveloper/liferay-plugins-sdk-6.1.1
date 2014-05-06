package my.ep.web.backingbean.pm.userprofile;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.userprofile.UserprofileManagedBean;

import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "userprofileBackingBean")
@RequestScoped
public class UserprofileBackingBean extends
		BaseBackingBean<UserprofileManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UserprofileBackingBean.class);
		

	public UserprofileBackingBean() {
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
	

}
