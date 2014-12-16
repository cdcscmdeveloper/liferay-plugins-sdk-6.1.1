package my.ep.web.backingbean.pm.signatory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.signatory.ISignatorySO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.signatory.SignatoryReqDTO;
import my.ep.dto.pm.common.signatory.SignatoryResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.signatory.AuthorizedSignatoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;


/**
 * Backing Bean for Authorized Singatory
 * @author DK, RAIN POH
 * @since 8/12/2014
 *
 */
@ManagedBean(name = "authorizedsignatoryBackingBean")
@ViewScoped
public class AuthorizedSignatoryBackingBean extends
		BaseBackingBean<AuthorizedSignatoryManagedBean> {
	
	private static final Logger log = EpLogger
			.getLogger(AuthorizedSignatoryManagedBean.class);	
	//Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/authorizedsignatory/authorizedSignatoryMain.xhtml";
	
	public AuthorizedSignatoryBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}
	
	public void preLoading(){
		this.getManagedBean().setZone(new PmZone());
		this.getManagedBean().setCertSignatories(new ArrayList<PmCertSignatory>());
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
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
		 return PmCommonUtil.getStatusOptionsDefault();
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
	 * Retrieve cert signatory when zone changed 
	 * @param zoneId
	 */
	public void changeZone(Long zoneId){
		log.info("changeZone");
		retrieveCertSignatoryByZone(zoneId);
	}
	
	/**
	 * Add authorize signatory to list
	 * @param pmuser
	 */
	public void addAuthorizer(PmUser pmuser){
		log.info("addAuthorizer");
		if(this.getManagedBean().getCertSignatories()==null){
			this.getManagedBean().setCertSignatories(new ArrayList<PmCertSignatory>());
		}
		
		PmZone zone = new PmZone();
		zone.setZoneId(this.getManagedBean().getZone().getZoneId());
		PmCertSignatory pmCertSignatory = new PmCertSignatory();
		pmCertSignatory.setPmUser(pmuser);
		pmCertSignatory.setPmZone(zone);
		pmCertSignatory.setEffDate(new Timestamp(System.currentTimeMillis()));
		this.getManagedBean().getCertSignatories().add(pmCertSignatory);
		this.getManagedBean().setSignatoryUser(null);
	}
	
	/**
	 * retrieve cert signatory by zone id
	 * @param zoneId
	 */
	private void retrieveCertSignatoryByZone(Long zoneId){
		if(zoneId!=null){
			ISignatorySO iSignatorySO = EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					ISignatorySO.class);
			SignatoryReqDTO reqDTO = new SignatoryReqDTO();
			reqDTO.setZoneId(zoneId);
			SignatoryResDTO resDTO = iSignatorySO.retrieveCertSignatoryByZone(reqDTO);
			this.getManagedBean().setCertSignatories(resDTO.getCertSignatories());
		}else{
			this.getManagedBean().setCertSignatories(null);
		}
	}
	
	/**
	 * delete selected signatory list 
	 * 
	 */		
	public void deleteAuthorizedSignatory() {
		debugLogger("deleteAuthorizedSignatory");
		
		if(this.getManagedBean().getDeletedCertSignatories()!=null && !this.getManagedBean().getDeletedCertSignatories().isEmpty()){
			if(this.getManagedBean().getCertSignatories()==null){
				this.getManagedBean().setCertSignatories(new ArrayList<PmCertSignatory>());
			}
			for (PmCertSignatory pmCertSignatory : this.getManagedBean().getDeletedCertSignatories()) {
				if(pmCertSignatory.isBrandNew()){
					this.getManagedBean().getCertSignatories().remove(pmCertSignatory);
				}
			}
			this.getManagedBean().getDeletedCertSignatories().clear();
		}else{
			throw new EpException(MessageConst.ERROR_770139);
		}
	}	
	
	/**
	 * Basic validation for mandatory fields
	 */
	private void validation(){
		debugLogger("validation");
		if (null == this.getManagedBean().getZone().getZoneId()) {
			throw new EpException(MessageConst.ERROR_770140);
		}
		
		if (null == this.getManagedBean().getCertSignatories() || this.getManagedBean().getCertSignatories().isEmpty()) {
			throw new EpException(MessageConst.ERROR_770141);
		}
	}
	
	/**
	 * validation for effective date and expiry date
	 * @param certSignatories
	 * @throws Exception
	 */
	private void validationDate(List<PmCertSignatory> certSignatories)throws Exception {
		debugLogger("validationDate");
		if(certSignatories==null || certSignatories.isEmpty()){
			throw new EpException(MessageConst.ERROR_770142);
		}
		
		//date overlapping
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date endLess = sdf.parse("2099-12-31");
		for (int index1 = 0; index1 < certSignatories.size(); index1++) {
			if (certSignatories.get(index1).getRecordStatusFlg() 
					== DbValueConst.RECORD_STATUS_ACTIVE) {
				if (certSignatories.size() > 1) {
					for (int index2 = index1 + 1; index2 < certSignatories.size(); index2++) {
						if (certSignatories.get(index2).getRecordStatusFlg() 
								== DbValueConst.RECORD_STATUS_ACTIVE) {
							// Overlap exists if (StartA <= EndB) and (EndA
							// >= StartB)
							Date startA = certSignatories.get(index1).getEffDate();
							Date endA = certSignatories.get(index1).getExpDate();
							Date startB = certSignatories.get(index2).getEffDate();
							Date endB = certSignatories.get(index2).getExpDate();

							// not null
							if(endA==null){
								endA = endLess;
							}if(endB==null){
								endB = endLess;
							}
							
							boolean isCurrentPairOverlap = false;
							isCurrentPairOverlap = PmCommonUtil.dateOverlapping(startA, endA, startB, endB, true);

							if (isCurrentPairOverlap) {
								throw new EpException(MessageConst.ERROR_770143);
							}
						}
					}
				} 
			}
		}
		
		//only one active
		int activeNumber = 0;
		if(certSignatories!=null){
			for(PmCertSignatory pmCertSignatory: certSignatories){
				if(pmCertSignatory.getRecordStatusFlg()== DbValueConst.RECORD_STATUS_ACTIVE){
					if(checkEffDate(pmCertSignatory)){
						activeNumber++;
					}
				}
			}
		}
		if(activeNumber>1){
			throw new EpException(MessageConst.ERROR_770144);
		}else if(activeNumber==0){
			throw new EpException(MessageConst.ERROR_770145);
		}
	}
	
	
	/**
	 * update only active 
	 * @param pmCertSignatory
	 * @return boolean
	 */
	private boolean checkEffDate(PmCertSignatory pmCertSignatory){
		log.info("checkEffDate");
		Date currentDate = new Date();
		boolean isActive = true;
		// effDate after currentDate
		if(pmCertSignatory.getEffDate().compareTo(currentDate)>0){
			isActive = false;
		// expDate not null and expDate before currentDate
		}else if(pmCertSignatory.getExpDate()!=null && pmCertSignatory.getExpDate().compareTo(currentDate)<0){
			isActive = false;
		}
		return isActive;
	}

	
	/**
	 * save signatory details
	 * @throws Exception 
	 * 
	 */	
	public void saveCertSignatoryDetials() throws Exception {
		debugLogger("saveCertSignatoryDetials");
		
		validation();
		
		List<PmCertSignatory> certSignatories = this.getManagedBean().getCertSignatories();
		
		validationDate(certSignatories);
		
		ISignatorySO iSignatorySO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ISignatorySO.class);
		SignatoryReqDTO reqDTO = new SignatoryReqDTO();
		reqDTO.setCertSignatorys(certSignatories);
		SignatoryResDTO resDTO = iSignatorySO.saveCertSignatoryDtls(reqDTO);
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
		     FacesMessage.SEVERITY_INFO,
		     null,
		     MessageUtils.getMessage(MessageConst.INFO_770146, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		}
	}
}
