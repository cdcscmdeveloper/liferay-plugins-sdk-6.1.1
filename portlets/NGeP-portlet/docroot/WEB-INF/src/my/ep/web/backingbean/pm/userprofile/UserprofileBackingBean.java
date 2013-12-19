package my.ep.web.backingbean.pm.userprofile;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.userprofile.UserprofileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
/**
 * @author DK
 * 
 */
@ManagedBean(name = "userprofileBackingBean")
@RequestScoped
public class UserprofileBackingBean extends BaseBackingBean<UserprofileManagedBean>{
	
	private static final Logger logger = EpLogger
			.getLogger(UserprofileBackingBean.class);	
	
	public static final String SESS_USER_SESSION_BEAN = "USER_SESSION_BEAN";	

	public UserprofileBackingBean() {
		getUserSessionBean();
	}
	
	
	public PmUser getUserSessionBean() {
		IUserSO userSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);

		UserReqDTO reqDTO = new UserReqDTO();
 		reqDTO.setUserName(new FacesScopes().getUserScreenName());

		UserResDTO resDTO1 = userSO.retrieveUserByName(reqDTO);

		PmUser user = resDTO1.getPmUser();
				
		System.out.println("user:@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + user);
		try{
		
		UserReqDTO dto= new UserReqDTO();
		dto.setUserId(user.getUserId());
		
		IUserSO iUserSO= EpEjbBusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserResDTO resDTO  =  iUserSO.retrieveUserDetails(dto);
		user = resDTO.getPmUser();
				List<PmAddressType> addressType = user.getPmAddressTypes();
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+addressType.size());
				addressType.get(0);
				if(addressType!=null){
				for(PmAddressType addressType2: addressType){
					if(addressType2.getAddressType().equalsIgnoreCase("C")){
						this.getManagedBean().setPmAddress(addressType2.getPmAddress());
					System.out.println("########################################"+addressType2.getPmAddress());
						this.getManagedBean().setPmUser(user);
						break;
					}
				}
				}
				System.out.println("id " + user.getUserId());
				
			} catch (Exception e) {
				e.printStackTrace();

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e
								.toString(), e.getMessage()));

				user = new PmUser();
			}
		

		return user;
	}
			
	
}
