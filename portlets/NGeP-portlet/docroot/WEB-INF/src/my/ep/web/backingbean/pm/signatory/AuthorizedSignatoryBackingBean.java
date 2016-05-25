package my.ep.web.backingbean.pm.signatory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.signatory.ISignatorySO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.signatory.SignatoryReqDTO;
import my.ep.dto.pm.common.signatory.SignatoryResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.signatory.AuthorizedSignatoryManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;


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
	/**
	 *  The constructor used for save user logging.
	 *  	 
	 **/
	public AuthorizedSignatoryBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}
	/**
	 *  This method used for reset the search bean field and
	 *  navigate to the search main page. 
	 * 
	 **/
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
	public void expDateChangedListener(ValueChangeEvent event){
		log.info("...event.getOldValue()="+event.getOldValue());
		log.info("...event.getNewValue()="+event.getNewValue());
		PmCertSignatory certSignatory = (PmCertSignatory) event.getComponent().getAttributes().get("certSignatory");
		//set modified for Tracking Diary and save the original expireDate if this Signatory for later compare
		if(certSignatory != null){
		Date originalExpDate = this.getManagedBean().getIdToCertSignMapForChangedRecs().get(certSignatory.getCertSignatoryId());
		log.info("...originalExpDate="+originalExpDate);
		if(originalExpDate != null){
			if(originalExpDate == (Date) event.getNewValue()){
				 this.getManagedBean().getIdToCertSignMapForChangedRecs().remove(certSignatory.getCertSignatoryId());
			}
		}else{
          this.getManagedBean().getIdToCertSignMapForChangedRecs().put(certSignatory.getCertSignatoryId(), (Date) event.getOldValue());
		}
		}
	}
	/**
	 * get status item dropdown
	 * 
	 */	
	 public SelectItem[] getSignatoryStatusList() {
		 return PmCommonUtil.getCommonStatusOptions();
	 }
	
	/**
	 * get zone list for dropdown
	 * 
	 */		
	public List<PmZone> retrieveZones() {
		IZoneSO iZoneSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
		ZoneResDTO resDTO = iZoneSO.retrieveActiveZoneList();
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
	public void addAuthorizer(){
		log.info("addAuthorizer");
		if(this.getManagedBean().getCertSignatories()==null){
			this.getManagedBean().setCertSignatories(new ArrayList<PmCertSignatory>());
		}
		
		PmZone zone = new PmZone();
		zone.setZoneId(this.getManagedBean().getZone().getZoneId());
		PmCertSignatory pmCertSignatory = new PmCertSignatory();
		//pmCertSignatory.setPmUser(pmuser);
		pmCertSignatory.setPmZone(zone);
		pmCertSignatory.setEffDate(this.getManagedBean().getTomorrow());
		this.getManagedBean().getCertSignatories().add(pmCertSignatory);
		this.getManagedBean().setSignatoryUser(null);
		RequestContext.getCurrentInstance().execute(
				"searchSignatoryResultsWidVar.paginator.endLink.click()");
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
		PmCertSignatory pmCertSignatory = getManagedBean().getDeleteSignatory();
		if (pmCertSignatory != null && pmCertSignatory.isBrandNew()) {
			if (this.getManagedBean().getCertSignatories() == null) {
				this.getManagedBean().setCertSignatories(
						new ArrayList<PmCertSignatory>());
			}
			this.getManagedBean().getCertSignatories().remove(pmCertSignatory);
		}
	}
	
	/**
	 * Basic validation for mandatory fields
	 */
	private void validation(){
		debugLogger("validation");
		if (null == this.getManagedBean().getZone().getZoneId()) {
			getManagedBean().setValidationError(true);
			throw new EpException(MessageConst.ERROR_770140);
		}
		
		if (null == this.getManagedBean().getCertSignatories() || this.getManagedBean().getCertSignatories().isEmpty()) {
			getManagedBean().setValidationError(true);
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
							isCurrentPairOverlap = PmCommonUtil.dateOverlapping(startA, endA, startB, endB, false);

							if (isCurrentPairOverlap) {
								getManagedBean().setValidationError(true);
								throw new EpException(MessageConst.ERROR_770143);
							}
						}
					}
				} 
			}
		}
		// eff date must not be after exp date
		PmCertSignatory certSignatory = null;
		Date effDate = null;
		Date expDate =null;
		for (int i = 0; i < certSignatories.size(); i++) {
			certSignatory = certSignatories.get(i);
			if(DbValueConst.RECORD_STATUS_ACTIVE == certSignatory.getRecordStatusFlg()){
				if(certSignatory.getEffDate()!=null 
						&& certSignatory.getExpDate()!=null 
						&& certSignatory.getExpDate().compareTo(certSignatory.getEffDate())<0){
					getManagedBean().setValidationError(true);
					throw new EpException(MessageConst.ERROR_770146);
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
			getManagedBean().setValidationError(true);
			throw new EpException(MessageConst.ERROR_770144);
		}else if(activeNumber==0){
			getManagedBean().setValidationError(true);
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
		Date currentDate =  DateUtils.getCurrentDateWithoutTime();
		boolean isActive = true;
		// effDate after currentDate
		if(! pmCertSignatory.isBrandNew() && pmCertSignatory.getEffDate().compareTo(currentDate)>0){
			isActive = false;
		// expDate not null and expDate before currentDate
		}else if(pmCertSignatory.getExpDate()!=null && pmCertSignatory.getExpDate().compareTo(currentDate)<0){
			isActive = false;
		}
		return isActive;
	}
	
	
	public boolean checkExpDate(PmCertSignatory pmCertSignatory){
		log.info("checkExpDate");
		Date currentDate =   DateUtils.getCurrentDateWithoutTime();
		boolean isActive = true;
		if(!pmCertSignatory.isBrandNew() && !getManagedBean().isValidationError()){
			if(pmCertSignatory.getEffDate() == null){
				isActive =false;
			}
			// effDate after currentDate
			if(pmCertSignatory.getExpDate()!=null && pmCertSignatory.getExpDate().compareTo(currentDate)<0){
				isActive = false;
			}
		}
		return isActive;
	}
	
	public Date getExpDateMinDate(PmCertSignatory pmCertSignatory){
		Date date = pmCertSignatory.getEffDate();
		return date;
	}

	
	/**
	 * save signatory details
	 * @throws Exception 
	 * 
	 */	
	public void saveCertSignatoryDetials() throws Exception {
		debugLogger("saveCertSignatoryDetials");
		getManagedBean().setValidationError(false);
		validation();
		
		List<PmCertSignatory> certSignatories = this.getManagedBean().getCertSignatories();
		
		validationDate(certSignatories);
		
		ISignatorySO iSignatorySO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ISignatorySO.class);
		SignatoryReqDTO reqDTO = new SignatoryReqDTO();
		reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
		
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		
		for(PmCertSignatory pmcertSignatory:certSignatories){
				
			if(pmcertSignatory.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED){
				pmcertSignatory.setExpDate(new Date());
			}
			String messageId = null;
			String[] msgParam = null;
			if (pmcertSignatory.isBrandNew()) {
				log.info("Tracking diary for virtual certificate: add new value :saveCertSignatoryDetials");
				log.info(".............pmcertSignatory.getSinnatoryName()="
						+ pmcertSignatory.getSinnatoryName());
				messageId = MessageConst.TD_80900;
				msgParam = new String[] {this.getManagedBean().getCertSignatories().get(0).getPmZone().getZoneName(),pmcertSignatory.getSinnatoryName()};

			} else {
				//TODO: check previous value 
				if (pmcertSignatory.isModified()
						&& pmcertSignatory.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					log.info("Tracking diary for virtual certificate: de-activating status :saveRuleCatalog");
					messageId = MessageConst.TD_80902;
					msgParam = new String[] {pmcertSignatory.getPmZone().getZoneName(),pmcertSignatory.getSinnatoryName()};

					
				}else if (pmcertSignatory.isModified()
						&& pmcertSignatory.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					log.info("Tracking diary for virtual certificate: re-activating status :saveRuleCatalog");
					messageId = MessageConst.TD_80903;
					msgParam = new String[] {pmcertSignatory.getPmZone().getZoneName(),pmcertSignatory.getSinnatoryName()};
				}
				Date originalExpDate = this.getManagedBean().getIdToCertSignMapForChangedRecs().get(pmcertSignatory.getCertSignatoryId());
				if(originalExpDate != null && pmcertSignatory.getExpDate() != originalExpDate){
					log.info("Tracking diary for virtual certificate: updating Expire Date :saveRuleCatalog");
					messageId = MessageConst.TD_80901;
					msgParam = new String[] {pmcertSignatory.getPmZone().getZoneName(),pmcertSignatory.getSinnatoryName()};
				}
			}
			
			statusReqDTO.setStatusId(pmcertSignatory.getRecordStatusFlg().longValue());
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			
			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForSignatory(pmcertSignatory,
								getActionDesc(messageId, msgParam),
								statusResDTO.getPmStatus() != null ? statusResDTO
										.getPmStatus() : new PmStatus()));
			}			
		}
		reqDTO.setCertSignatorys(certSignatories);
		SignatoryResDTO resDTO = iSignatorySO.saveCertSignatoryDtls(reqDTO);
		if (resDTO.isSuccessful()) {
			retrieveCertSignatoryByZone(certSignatories.get(0).getPmZone().getZoneId());
			
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
		     FacesMessage.SEVERITY_INFO,
		     null,
		     MessageUtils.getMessage(MessageConst.INFO_770146, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

		}
	}
	/**
	 * Track Signatory status
	 * 
	 * @param signatory
	 */
	public void certSignatoryStatusChange(PmCertSignatory signatory) {
		log.info("certSignatoryStatusChange");

		if (!signatory.isModified()) {
			signatory.setModified(true);
		} else {
			signatory.setModified(false);
		}

	}
	
	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

}
