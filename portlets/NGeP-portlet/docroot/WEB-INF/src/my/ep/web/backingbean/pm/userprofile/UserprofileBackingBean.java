package my.ep.web.backingbean.pm.userprofile;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.userprofile.UserprofileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

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

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public UserprofileBackingBean() {
		retriveUserDtls();
		retriveStateList();
		
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	public List<PmCountry> retrieveCountryList() {
		ICountrySO countrySO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = countrySO.retrieveCountryList();
		return resDTO.getCountryList();

	}
	
	public void retriveStateList() {
		IStateSO stateSo = null;
		StateResDTO resDto = null;

		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1L);
			stateSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			resDto = stateSo.retriveStatesListByCountryID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setStateList(resDto.getStateList());
			}

		} finally {
			stateSo = null;
		}

	}

	public void districtBystate(AjaxBehaviorEvent event) {
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			Long stateId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (stateId != null) {
				List<PmState> stateList = this.getManagedBean().getStateList();
				for (PmState sate : stateList) {
					if (sate.getStateId().equals(stateId)) {
						this.getManagedBean().setStateCode(sate.getStateCode());

					}
				}
			}

			reqDTO.setStateId(stateId);
			districtSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			resDto = districtSo.retriveDistrictListByStateID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setDistrictList(resDto.getDistrictList());
			}

		} finally {
			districtSo = null;
		}

	}

	public void districtCode(AjaxBehaviorEvent event) {
		try {
			Long districtId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (null != districtId) {
				List<PmDistrict> districtList = this.getManagedBean()
						.getDistrictList();
				for (PmDistrict district : districtList) {
					if (district.getDistrictId().equals(districtId)) {
						this.getManagedBean().setDistrictCode(
								district.getDistrictCode());

					}

				}

			}

		} finally {

		}

	}	


	private void retriveUserDtls() {
		debugLogger("retriveUserDtls");
		IUserSO userSO = null;

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		debugLogger("================1");
		FacesScopes facesScopes = new FacesScopes();
		PmUser sUsers = facesScopes.getUserSessionBean();
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(sUsers.getLoginId());
		debugLogger("================2");
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
		debugLogger("================3");
		PmUser user = resDTO.getPmUser();
		if (user != null && null != user.getUserId()) {
			this.getManagedBean().setPmUser(user);
			List<PmAddressType> addressType = user.getPmAddressTypes();

			if (addressType != null) {
				debugLogger("=========address type["+addressType.size()+"]");				
				for (PmAddressType addressType2 : addressType) {
					debugLogger("=========no address type["
							+ addressType2.getAddressType() + "]");
					if (addressType2.getAddressType().equalsIgnoreCase("C")) {
						debugLogger("=========no address type CCCC=========");
						this.getManagedBean().setPmAddress(
								addressType2.getPmAddress());
						logger.info(addressType2.getPmAddress());

						break;
					}
				}
			} else {
				debugLogger("=========no address type=========");
			}
		} else {
			logger.debug("Unable to get the userDetails");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to Retrive User Details", ""));
		}
	}

	public void updateMyProfileAction(PmUser pmUser) {
		debugLogger("updateMyProfileAction");

//		debugLogger("=========no address type["
//				+ getManagedBean().getPmAddress().getAddress1() + "]");
//		getManagedBean().getPmAddress().getAddress1();
//				
//		List<PmAddressType> addressType = pmUser.getPmAddressTypes();
//
//		if (addressType != null) {				
//			debugLogger("=========no null");
//			for (PmAddressType addressType2 : addressType) {
//				if (addressType2.getAddressType().equalsIgnoreCase("C")) {
//					debugLogger("=========no null");
//					addressType2.setPmAddress(this.getManagedBean().getPmAddress());
//				}				
//			}
		
		pmUser.setPmAddressTypes(null);
//		}

		IUserSO userSO = null;
		try {
			
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(this.getManagedBean().getPmUser());
			// reqDTO.setPmUser(pmUser);
			UserResDTO resDTO = userSO.saveMyProfile(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"User Profiles successfully saved", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
	}

}
