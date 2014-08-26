package my.ep.web.backingbean.pm.searchalluser;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "searchAllUserBackingBean")
@ViewScoped //@RequestScoped
public class SearchAllUserBackingBean extends BaseBackingBean<SearchAllUserManagedBean> {
	public static final String NAV_TO_SEARCH = "/xhtml/pm/searchAllUser/tempSearch.xhtml"; // main page for portlet
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
	

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	
	/**
	 * load clean view for search all user
	 */
	public void preLoadingSearchUser(){
		logger.info("preLoadingUserProfile");
		this.getManagedBean().setLoginId(null);
		this.resetSearchInfo();
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	private void resetSearchInfo(){
		this.getManagedBean().setResultStatus(null);
		this.getManagedBean().setOimRoleList(null);
		this.getManagedBean().setPmRoleList(null);
		this.getManagedBean().setLiferayRoleList(null);
		this.getManagedBean().setResultLoginId(null);
	}
	
	public void tempSearchUserProfile() throws Exception{
		logger.info("tempSearchUserProfile start");
		this.resetSearchInfo();
		
		String loginId = this.getManagedBean().getLoginId();
		
		if(StringUtils.isBlank(loginId)){
			throw new IllegalArgumentException("Login ID is mandatory.");
		}
		
		try{
			UserReqDTO userReqDTO = new UserReqDTO();
			UserResDTO resDto = null;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);	
			userReqDTO.setLoginId(loginId.toLowerCase());
			userReqDTO.setRoleRequired(true);
			resDto = userSO.isUserExist(userReqDTO);
			if(resDto!=null && resDto.isSuccessful()){
				this.getManagedBean().setResultStatus(resDto.getAllResult());
				this.getManagedBean().setOimRoleList(resDto.getOimRoleNames());
				this.getManagedBean().setPmRoleList(resDto.getPmRoleNames());
				this.getManagedBean().setLiferayRoleList(resDto.getLiferayRoleNames());
				this.getManagedBean().setResultLoginId(loginId);
			}
				
			
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		logger.info("tempSearchUserProfile end");
		
	}

	
	
}
