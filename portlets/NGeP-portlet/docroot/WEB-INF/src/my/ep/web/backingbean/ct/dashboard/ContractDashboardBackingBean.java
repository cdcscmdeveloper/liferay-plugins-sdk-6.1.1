/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.dashboard;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ct.dashboard.ContractDashboardManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
/**
 * Contract Dashboard Backing Bean
 * 
 * @author Alam
 * @since 12/5/2016
 */
@ManagedBean(name="contractDashboardBackingBean")
@ViewScoped
public class ContractDashboardBackingBean extends BaseBackingBean<ContractDashboardManagedBean>{

	  /**
	     * The function constructor used for initializing the variable.
	 */
	  public ContractDashboardBackingBean(){
		}
		
	  @PostConstruct
	  public void init(){
		defaultMinistryPtjSearchValue();
		getManagedBean().setLessThan30Days(calculateTotalRecord(1));
		getManagedBean().setLessThan120Days(calculateTotalRecord(2));
 	}
	
	  /**
	   * This method is used to forward 
	   * page on contract maintenance after user click
	   * */
	public void goToMaintenancePage() throws IOException {
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);
		String commonUrl;
		try {
			commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
							.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
							.getFriendlyURL().length());
			System.out.println("commonUrl" + commonUrl);

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(commonUrl + "/" +ContractConst.NAV_TO_CONTRACT_MAINT);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method used to add date in current date
	 * @param Date date, int days
	 * @return Date date
	 * */
	public static Date addDays(Date date, int days)
		{
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(date);
		    cal.add(Calendar.DATE, days); 
		    return cal.getTime();
	}
		
	 /**
   	 * The method used to set the ministry 
   	 * profile id and PTJ profile id
   	 */
    private void defaultMinistryPtjSearchValue() {
    	FacesScopes facesScopes = new FacesScopes();
    	if (facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
    			|| facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
    		this.getManagedBean().setSearchMinistryProfile(null);
    		this.getManagedBean().setSearchPtjGroupProfile(null);
    		this.getManagedBean().setSearchPtjProfile(null);
			
		} else {
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList()!=null && userResDTO.getUserOrgList().size()>0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			
			if (facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {			
				this.getManagedBean().setSearchMinistryProfile(orgProfile);
				this.getManagedBean().setSearchPtjGroupProfile(null);
				this.getManagedBean().setSearchPtjProfile(null);
			} else if (facesScopes.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
					|| facesScopes.hasRoleUser(RoleCode.BPK_CCA.value())) {
				this.getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
				if (this.getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == -1) {
					this.getManagedBean().setSearchPtjGroupProfile(null);
				}
				this.getManagedBean().setSearchPtjProfile(orgProfile);
			}
		}
    }
	
    /**
     * This method used to calculate total 
     * record into database table between defined date
     * @param int index
     * @return int record list
     * */
	public int calculateTotalRecord(int index){

		IContractSO contractSO = null;
		try {
					
					contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
					ContractReqDTO reqDTO = new ContractReqDTO();
					reqDTO.setSearchCriteria(index);
					if (getManagedBean().getSearchPtjProfile() != null) {
						reqDTO.setUserOrgProfileID(getManagedBean().getSearchPtjProfile().getOrgProfileId());
					} else if (getManagedBean().getSearchPtjGroupProfile() != null) {
						reqDTO.setUserOrgProfileID(getManagedBean().getSearchPtjGroupProfile().getOrgProfileId());
					} else if (getManagedBean().getSearchMinistryProfile() != null) {
						reqDTO.setUserOrgProfileID(getManagedBean().getSearchMinistryProfile().getOrgProfileId());
					}
					
					ContractResDTO resDTO = contractSO.searchDashBoardContract(reqDTO);	
					if (resDTO.isSuccessful()) {
							return resDTO.getRecordCount();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		return 0;
	}
}
