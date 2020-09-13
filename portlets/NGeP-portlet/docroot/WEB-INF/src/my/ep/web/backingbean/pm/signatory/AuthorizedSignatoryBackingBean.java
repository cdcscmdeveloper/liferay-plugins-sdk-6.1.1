package my.ep.web.backingbean.pm.signatory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
//20200825@Jacky - #28739 - Start
import java.util.Arrays;
//20200825@Jacky - #28739 - End
import java.util.Date;
import java.util.List;
import java.util.Locale;
//20200910@Jacky - #28740 - Start
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//20200910@Jacky - #28740 - End
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.signatory.ISignatorySO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
//20200825@Jacky - #28739 - Start
import my.ep.constant.pm.ParameterConst;
//20200825@Jacky - #28739 - End
import my.ep.dto.pm.common.PmTrackingDairyVO;
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
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
//20200825@Jacky - #28739 - Start
import org.apache.commons.lang.StringUtils;
//20200825@Jacky - #28739 - End

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		//20200825@Jacky - #28739 - Start
		resetAuthorizedSignatorySearch();
		//20200825@Jacky - #28739 - End
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
		//20200825@Jacky - #28739 - Start
		pmCertSignatory.setNewMof(0);
		pmCertSignatory.setRenewMof(0);
		pmCertSignatory.setBumiStat(0);
		pmCertSignatory.setEffDate(this.getManagedBean().getTomorrow());
		this.getManagedBean().getCertSignatories().add(pmCertSignatory);
		this.getManagedBean().setSignatoryUser(null);
		//20200825@Jacky - #28739 - End
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
		}//20200825@Jacky - #28739 - Start
		//remove activeNumber>1
		//20200825@Jacky - #28739 - End
		if(activeNumber==0){
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
		//20200825@Jacky - #28739 - Start
		List<PmCertSignatory> activeList = new ArrayList<>();
		for(PmCertSignatory activeAppl: certSignatories){
			if(DbValueConst.RECORD_STATUS_ACTIVE == activeAppl.getRecordStatusFlg()){
				if(!activeAppl.isBrandNew()){
					activeList.add(activeAppl);
				}
			}
		}
		
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
				
				if(pmcertSignatory.getSelectedApplType().length < 1){
					getManagedBean().setValidationError(true);
					throw new EpException(MessageConst.ERROR_770233);
				}
						
				String[] newSelectedApplType = new String[3];
				String[] newSelectedApplTypeTemp = pmcertSignatory.getSelectedApplType();
				for (int i = 0; i < newSelectedApplTypeTemp.length; i++) {
					newSelectedApplType[i] = newSelectedApplTypeTemp[i];
				}
				boolean newMof = Arrays.asList(newSelectedApplType).contains(ParameterConst.NEW_SM_APPLN_FOR_MOF);
				boolean reNewMof = Arrays.asList(newSelectedApplType).contains(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
				boolean bumiStat = Arrays.asList(newSelectedApplType).contains(ParameterConst.NEW_SM_APPLN_FOR_BUMI);
				if(newMof){
					pmcertSignatory.setNewMof(1);
				}
				if(reNewMof){
					pmcertSignatory.setRenewMof(1);
				}
				if(bumiStat){
					pmcertSignatory.setBumiStat(1);
				}
				
				for(PmCertSignatory activeAutSign: activeList){
					boolean activeNewMof = Arrays.asList(activeAutSign.getSelectedApplType()).contains(ParameterConst.NEW_SM_APPLN_FOR_MOF);
					boolean activeRenewMof = Arrays.asList(activeAutSign.getSelectedApplType()).contains(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
					boolean activeBumiStat = Arrays.asList(activeAutSign.getSelectedApplType()).contains(ParameterConst.NEW_SM_APPLN_FOR_BUMI);
					if((pmcertSignatory.getSinnatoryName().equals(activeAutSign.getSinnatoryName())) && ((newMof == activeNewMof) && (reNewMof == activeRenewMof) && (bumiStat == activeBumiStat))){
						getManagedBean().setValidationError(true);
						pmcertSignatory.setNewMof(0);
						pmcertSignatory.setRenewMof(0);
						pmcertSignatory.setBumiStat(0);
						throw new EpException(MessageConst.ERROR_770236, new String[] {pmcertSignatory.getSinnatoryName() + " "});
					}
					if(pmcertSignatory.getSinnatoryName().equals(activeAutSign.getSinnatoryName())){
						getManagedBean().setValidationError(true);
						pmcertSignatory.setNewMof(0);
						pmcertSignatory.setRenewMof(0);
						pmcertSignatory.setBumiStat(0);
						throw new EpException(MessageConst.ERROR_770234, new String[] {pmcertSignatory.getSinnatoryName() + " "});
					}
					if((newMof == activeNewMof) && (reNewMof == activeRenewMof) && (bumiStat == activeBumiStat)){
						getManagedBean().setValidationError(true);
						pmcertSignatory.setNewMof(0);
						pmcertSignatory.setRenewMof(0);
						pmcertSignatory.setBumiStat(0);
						throw new EpException(MessageConst.ERROR_770235);
					}
				}
				//20200825@Jacky - #28739 - End
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
			if (messageId != null & msgParam != null) {
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyForSignatory(pmcertSignatory,
								getActionDesc(messageId, msgParam),pmcertSignatory.getRecordStatusFlg().longValue()));
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
	//20200825@Jacky - #28739 - Start
	public void searchAuthorizedSignatory(Long zoneId) {
		log.info("searchUserProfile - start");
		
		if (this.getManagedBean().getZone().getZoneId() == null) {
			getManagedBean().setValidationError(true);
			throw new EpException(MessageConst.ERROR_770140);
		}
		
		ISignatorySO iSignatorySO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ISignatorySO.class);
		SignatoryReqDTO reqDTO = new SignatoryReqDTO();	
		
		reqDTO.setZoneId(zoneId);
		reqDTO.setApplType(this.getManagedBean().getApplType());
		reqDTO.setSignatoryName(this.getManagedBean().getSignatoryName());
		SignatoryResDTO resDTO = iSignatorySO.retrieveCertSignatoryByZone(reqDTO);
		
		this.getManagedBean().setCertSignatories(resDTO.getCertSignatories());
		
	}

	public void resetAuthorizedSignatorySearch(){
		getManagedBean().getZone().setZoneId(null);
		getManagedBean().setApplType(null);
		getManagedBean().setSignatoryName(StringUtils.EMPTY);
		getManagedBean().setCertSignatories(null);
	}
	//20200825@Jacky - #28739 - End
	//20200910@Jacky - #28740 - Start
	public String getAuthSignName(String curActionDesc){
		String sabah = "SABAH";
		String sarawak = "SARAWAK";
		String semenanjung = "SEMENANJUNG"; 
		boolean zonSabah = curActionDesc.contains(sabah);
		boolean zonSarawak = curActionDesc.contains(sarawak);
		boolean zonSemenanjung = curActionDesc.contains(semenanjung);
		String result = null;
		
		if(zonSabah){
			if(curActionDesc.contains("Penandatangan Sijil Maya")){
				if(curActionDesc.contains("telah ditambah")){
					Pattern pattern = Pattern.compile("Zon SABAH : Penandatangan Sijil Maya (.*) telah ditambah");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah dinyahaktifkan")){
					Pattern pattern = Pattern.compile("Zon SABAH : Penandatangan Sijil Maya (.*) telah dinyahaktifkan");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah diaktifkan semula")){
					Pattern pattern = Pattern.compile("Zon SABAH : Penandatangan Sijil Maya (.*) telah diaktifkan semula");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}
			}else if(curActionDesc.contains("Butiran Penandatangan")){
				Pattern pattern = Pattern.compile("Zon SABAH : Butiran Penandatangan (.*) telah dikemaskini.");
				Matcher matcher = pattern.matcher(curActionDesc);
				if (matcher.find())
				{
					result = matcher.group(1);
				}
			}
		}else if(zonSarawak){
			if(curActionDesc.contains("Penandatangan Sijil Maya")){
				if(curActionDesc.contains("telah ditambah")){
					log.info("IM inside Zon sarawak pendandatangan Sijil Maya ........ telah ditambah");
					Pattern pattern = Pattern.compile("Zon SARAWAK : Penandatangan Sijil Maya (.*) telah ditambah");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah dinyahaktifkan")){
					Pattern pattern = Pattern.compile("Zon SARAWAK : Penandatangan Sijil Maya (.*) telah dinyahaktifkan");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah diaktifkan semula")){
					Pattern pattern = Pattern.compile("Zon SARAWAK : Penandatangan Sijil Maya (.*) telah diaktifkan semula");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}
			}else if(curActionDesc.contains("Butiran Penandatangan")){
				Pattern pattern = Pattern.compile("Zon SARAWAK : Butiran Penandatangan (.*) telah dikemaskini.");
				Matcher matcher = pattern.matcher(curActionDesc);
				if (matcher.find())
				{
					result = matcher.group(1);
				}
			}
		}else if(zonSemenanjung){
			if(curActionDesc.contains("Penandatangan Sijil Maya")){
				if(curActionDesc.contains("telah ditambah")){
					Pattern pattern = Pattern.compile("Zon SEMENANJUNG : Penandatangan Sijil Maya (.*) telah ditambah");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah dinyahaktifkan")){
					Pattern pattern = Pattern.compile("Zon SEMENANJUNG : Penandatangan Sijil Maya (.*) telah dinyahaktifkan");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}else if(curActionDesc.contains("telah diaktifkan semula")){
					Pattern pattern = Pattern.compile("Zon SEMENANJUNG : Penandatangan Sijil Maya (.*) telah diaktifkan semula");
					Matcher matcher = pattern.matcher(curActionDesc);
					if (matcher.find())
					{
						result = matcher.group(1);
					}
				}
			}else if(curActionDesc.contains("Butiran Penandatangan")){
				Pattern pattern = Pattern.compile("Zon SEMENANJUNG : Butiran Penandatangan (.*) telah dikemaskini.");
				Matcher matcher = pattern.matcher(curActionDesc);
				if (matcher.find())
				{
					result = matcher.group(1);
				}
			}
		}

		return result;
	}
	//20200910@Jacky - #28740 - End
}
