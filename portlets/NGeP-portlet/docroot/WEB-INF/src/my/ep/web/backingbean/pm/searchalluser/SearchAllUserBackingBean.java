package my.ep.web.backingbean.pm.searchalluser;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.searchalluser.SearchAllUserManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author DK, RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "searchAllUserBackingBean")
@ViewScoped //@RequestScoped
public class SearchAllUserBackingBean extends BaseBackingBean<SearchAllUserManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(SearchAllUserBackingBean.class);

	
	
	
	public SearchAllUserBackingBean(){
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	
	}
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public void tempSearchUserProfile() throws Exception{
		logger.info("tempSearchUserProfile start");
		String userName = this.getManagedBean().getUserNames();
		try{
		if(userName!=null){
			UserReqDTO userReqDTO = new UserReqDTO();
			UserResDTO resDto = null;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);	
			userReqDTO.setLoginId(userName);
			
			resDto = userSO.tempSearchAll(userReqDTO);
			if(resDto!=null && resDto.isSuccessful()){
				this.getManagedBean().setResult(resDto.getAllResult());
			}
			
		}
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		logger.info("tempSearchUserProfile end");
		
	}

	
	
}
