package my.ep.web.backingbean.pm.signatory;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.signatory.ISignatorySO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.signatory.SignatoryReqDTO;
import my.ep.dto.pm.common.signatory.SignatoryResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.signatory.AuthorizedSignatoryManagedBean;


/**
 * @author DK
 * 
 */
@ManagedBean(name = "authorizedsignatoryBackingBean")
@ViewScoped
public class AuthorizedSignatoryBackingBean extends
		BaseBackingBean<AuthorizedSignatoryManagedBean> {

	public AuthorizedSignatoryBackingBean() {
		if(null == this.getManagedBean().getZones()){
			this.getManagedBean().setZones(new PmZone());
		}
	}
	
	public List<PmZone> retrieveZones() {
		IZoneSO iZoneSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = iZoneSO.retrieveZoneList();
		return resDTO.getZoneList();
	}

	public void saveCertSignatoryDetials() {
		List<PmUser> users = this.getManagedBean().getUsers();
		if (null == users || users.isEmpty()) {
			throw new EpException("Please select Users");
		}
		PmZone pmZone = this.getManagedBean().getZones();
		if (null == pmZone || null == pmZone.getZoneId()) {
			throw new EpException("Please select Zone id");
		}
		List<PmCertSignatory> certSignatories = new ArrayList<PmCertSignatory>();
		for (PmUser user : users) {
			PmCertSignatory certSignatory = new PmCertSignatory();
			certSignatory.setUser(user);
			if(null != user.getPmUserOrgs() && null != user.getPmUserOrgs().get(0)){
				PmUserOrg  userOrg = user.getPmUserOrgs().get(0);
				certSignatory.setEffectiveDate(userOrg.getEffDate());
				certSignatory.setExpDate(userOrg.getExpDate());
			}
			certSignatory.setPmZone(pmZone);
			certSignatories.add(certSignatory);
		}
		ISignatorySO iSignatorySO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ISignatorySO.class);
		SignatoryReqDTO reqDTO = new SignatoryReqDTO();
		reqDTO.setCertSignatorys(certSignatories);
		SignatoryResDTO resDTO = iSignatorySO.saveCertSignatoryDtls(reqDTO);
		if (resDTO.isSuccessful()) {
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_INFO,
							null,"Saved SuccessFully"));
		}
	}
}
