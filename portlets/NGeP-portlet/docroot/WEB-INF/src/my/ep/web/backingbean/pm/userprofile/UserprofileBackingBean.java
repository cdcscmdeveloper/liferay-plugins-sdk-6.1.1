package my.ep.web.backingbean.pm.userprofile;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
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
		retrievePmStateList();
		retrievePmDistrictList();
		retrievePmCityList();
		retrievePmDivisionList();
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

	public void retrievePmStateList() {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);

		this.getManagedBean().setStates(resDTO.getStateList());
	}

	public void retrievePmDistrictList() {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(1l);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);

		this.getManagedBean().setDistricts(resDTO.getDistrictList());
	}

	public void retrievePmCityList() {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(1L);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);

		this.getManagedBean().setCitys(resDTO.getCityList());
	}

	public void retrievePmDivisionList() {
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(14L);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);

		this.getManagedBean().setDivisions(resDTO.getDivisionList());
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
						debugLogger("=========no address type CCCC");
						this.getManagedBean().setPmAddress(
								addressType2.getPmAddress());
						logger.info(addressType2.getPmAddress());

						break;
					}
				}
			} else {
				debugLogger("=========no address type");
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

		pmUser.setPmNotifyLog(null);
		pmUser.setPmUserNotifications(null);
		pmUser.setPmNotifyUser(null);
		pmUser.setPmUserPreference(null);
		pmUser.setPmAddressTypes(null);
		pmUser.setPmUserOrgs(null);

		// pmUser.setSecretAnswer(null);
		// pmUser.setOrgTypeCode(null);

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
								"Record update successfully", "Record ID: "
										+ resDTO.getPmUser().getUserId()));
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
