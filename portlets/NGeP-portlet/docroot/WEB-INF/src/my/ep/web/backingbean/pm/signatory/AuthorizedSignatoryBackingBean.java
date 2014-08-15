package my.ep.web.backingbean.pm.signatory;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.common.signatory.ISignatorySO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.signatory.SignatoryReqDTO;
import my.ep.dto.pm.common.signatory.SignatoryResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
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
	
	private static final Logger log = EpLogger
			.getLogger(AuthorizedSignatoryManagedBean.class);	

	public AuthorizedSignatoryBackingBean() {
		if(null == this.getManagedBean().getZones()){
			this.getManagedBean().setZones(new PmZone());
		}
	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * get status item dropdown
	 * 
	 */	
	 public SelectItem[] getSignatoryStatusList() {
		 
			SelectItem[] items =  new SelectItem[2];
			items[0] = new SelectItem(1, "Active");
			items[1] = new SelectItem(0, "Inactive");
		    return items;
		}	
	
	/**
	 * get zone list for dropdown
	 * 
	 */		
	public List<PmZone> retrieveZones() {
		IZoneSO iZoneSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = iZoneSO.retrieveZoneList();
		return resDTO.getZoneList();
	}
	
	/**
	 * delete selected signatory list 
	 * 
	 */		
	public void deleteAuthorizedSignatory() {
		debugLogger("deleteAuthorizedSignatory");
		
		List<PmUser> signatoryData = this.getManagedBean().getSignatoryUser();
		if (signatoryData.size() == signatoryData.size()) {
			signatoryData.clear();
		} else {
			for (PmUser signatoryUser : signatoryData) {
				signatoryData.remove(signatoryUser);
			}
		}
	}	
	
	/**
	 * save signatory details
	 * 
	 */	
	public void saveCertSignatoryDetials() {
		debugLogger("saveCertSignatoryDetials");
		
		List<PmUser> users = this.getManagedBean().getSignatoryUser();
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
			certSignatory.setPmUser(user);
			if(null != user.getPmUserOrgs() && null != user.getPmUserOrgs().get(0)){
				PmUserOrg  userOrg = user.getPmUserOrgs().get(0);
				certSignatory.setEffDate(userOrg.getEffDate());
				certSignatory.setExpDate(userOrg.getExpDate());
				certSignatory.setRecordStatusFlg(userOrg.getRecordStatusFlg());
				
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
