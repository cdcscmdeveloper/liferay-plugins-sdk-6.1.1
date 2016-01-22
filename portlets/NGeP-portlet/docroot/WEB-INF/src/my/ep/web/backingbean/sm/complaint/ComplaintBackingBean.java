package my.ep.web.backingbean.sm.complaint;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.DisciplinaryActionConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintOffense;
import my.ep.persistence.entity.sm.SmComplaintResponse;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.ComplaintSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);
	//private IPagingDataModel<SmComplaint> complaintDataModel = new ComplaintPagingDataModel();
	
	private final String COMPLAINT_VIEW = "/xhtml/sm/disciplinaryAction/complaintView.xhtml";
	private final String LODGE_COMPLAINT = "/xhtml/sm/complaint/lodgeComplaint.xhtml";
	
	public ComplaintBackingBean() {
		
	}

	public String lodgeComplaint() {
		String complNo = String.valueOf((new Date()).getTime());
		Date date = new Date();
		Timestamp currDate = new Timestamp(date.getTime());

		this.getManagedBean().getSmComplaint().setComplaintDate(currDate);
		this.getManagedBean().getSmComplaint().setComplaintNo(complNo);
		this.getManagedBean().getSmComplaint().setComplaintCategory("O");
		this.getManagedBean().getSmComplaint().setPmStatus(SmCommonUtil.getInstance().retrievePmStatusById(DisciplinaryActionConst.NEW_COMPLAINT));

		this.getManagedBean().getSmComplainant()
				.setSmComplaint(this.getManagedBean().getSmComplaint());
		this.getManagedBean().getSmComplaint().setSmComplainant(getManagedBean().getSmComplainant());

		if(getManagedBean().getComplaintDocs().size() > 0){
			for(SmAttachment attachment : getManagedBean().getComplaintDocs()){
//				attachment.setDocId(getManagedBean().getSmComplaint().getComplaintId());
				attachment.setDocType(DisciplinaryActionConst.COMPLAINT_DOC_TYPE);
			}
		}
		
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		reqDTO.setAttachmentLs(getManagedBean().getComplaintDocs());
		
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IComplaintSO.class);
		reqDTO.setSmComplaint(this.getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.lodgeComplaint(reqDTO);

		renewSesionObject();
/*		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Your complaint has been submitted successfully",""));*/
		
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
				MessageUtils.getMessage(MessageConst.INFO_723000 , new String[]{complNo}, 
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 				  
		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}

	public void retrieveComplaintInfo() {
		//renewSesionObject();
		// this.getManagedBean().setComplaintId(31);
		log.info("--------------retrieveComplaintInfo--------------------"
				+ this.getManagedBean().getComplaintId());
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IComplaintSO.class);
		reqDTO.setComplaintId(this.getManagedBean().getComplaintId());
		ComplaintResDTO resDTO = reqSO.retrieveComplaintInfoById(reqDTO);

		log.info("--------------statusId-------------------"
				+ resDTO.getSmComplaint().getPmStatus().getStatusId());

		getManagedBean().setSmComplaint(resDTO.getSmComplaint());
		getManagedBean().setSmComplainant(resDTO.getSmComplaint().getSmComplainant());
		/*getManagedBean().setSmComplReview(new SmComplaintReview());*/
		log.info("--------------complaint Review Ls---------" + getManagedBean().getSmComplaint().getSmComplaintReviews().size());
		if(getManagedBean().getSmComplaint().getSmComplaintReviews() == null){
			getManagedBean().getSmComplaint().setSmComplaintReviews(new ArrayList<SmComplaintReview>());
		}
		
		if(getManagedBean().getSmComplaint().getSmComplaintOffenses() == null){
			getManagedBean().getSmComplaint().setSmComplaintOffenses(new ArrayList<SmComplaintOffense>());
		}
		
		getManagedBean().setSelectedOffenses(new ArrayList<String>());
		if(!getManagedBean().getSmComplaint().getSmComplaintOffenses().isEmpty()){
			for(SmComplaintOffense offense : getManagedBean().getSmComplaint().getSmComplaintOffenses()){
				getManagedBean().getSelectedOffenses().add(offense.getOffense().getParameterId().toString());
			}
		}
		
		populateInfoByStatus();
		
		/*Long currentStatus = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
		log.info("*****currentStatus*****" + currentStatus);
		if(currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT.longValue() || 
				currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT_REJECT.longValue() ||
				currentStatus.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){

			getManagedBean().setSmComplReview(new SmComplaintReview());
			getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());
			
		}else if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
			String committee = null;
			Integer roundNo = null;
			log.info("****" + currentStatus +"*****");
			
			if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
				committee = DisciplinaryActionConst.PTPK;
				roundNo = DisciplinaryActionConst.PTPK_R1;

				if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP){
					roundNo = DisciplinaryActionConst.PTPK_R2;
				}
				
				if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT){
					roundNo = null;
					committee = DisciplinaryActionConst.JKTT;
				}
				
				SmComplaintReview complReview = getComplReviewByCommitteeAndRoundNo(committee,roundNo);
				getManagedBean().setSmComplReview(complReview);
				
				log.info("complReview roundNo>>>>" + roundNo);
				log.info("complReview committee>>>>" + committee);
				log.info("complReview>>>>" + complReview.getComplaintReviewId());
				if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue() || 
						currentStatus.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue()){
					
					if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
						getManagedBean().setShowCauseLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_LETTER_DOC_TYPE, 
								complReview.getComplaintReviewId()));
					}
					
					getManagedBean().getSmComplReview().setSmComplaintResponse(new SmComplaintResponse());
					getManagedBean().getSmComplReview().getSmComplaintResponse().setSmComplaintReview(getManagedBean().getSmComplReview());
				}
				
				if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue() || 
						currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
					if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
						getManagedBean().setShowCauseResponseDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_RESP_DOC_TYPE, 
								complReview.getComplaintReviewId()));
					}
					
					getManagedBean().setSmComplReview(new SmComplaintReview());
					getManagedBean().getSmComplReview().setRoundNo(roundNo);
					getManagedBean().getSmComplReview().setReviewCommittee(committee);
					getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());
				}
			}*/
			
			/*else if {
				committee = DisciplinaryActionConst.JKTT;
				roundNo = DisciplinaryActionConst.PTPK_R1;
			}*/
			
		
		
		/*//Complaint DOCs
		getManagedBean().setComplaintDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.COMPLAINT_DOC_TYPE, 
				getManagedBean().getSmComplaint().getComplaintId()));
*/
	}
	
	private SmComplaintReview getComplReviewByCommitteeAndRoundNo(String committee, Integer roundNo){
		SmComplaintReview complReview = null;
		log.info("getComplReviewByCommitteeAndRoundNo>>>" + getManagedBean().getSmComplaint().getSmComplaintReviews().size());
		
		if(!getManagedBean().getSmComplaint().getSmComplaintReviews().isEmpty()){
			for(SmComplaintReview complRev : getManagedBean().getSmComplaint().getSmComplaintReviews()){
				if(complRev.getReviewCommittee().equalsIgnoreCase(committee) && complRev.getRoundNo() == roundNo){
					complReview = complRev;
					break;
				}
			}
		}
		
		return complReview;
	}

	/*public void searchSupplier() {
		log.info("--------searchSupplier-------");
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		reqDTO.setSupplierId(3L);
		reqDTO.setSupplierName("MDEC malaysia");
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		ComplaintResDTO resDTO = reqSO.retrieveSupplier(reqDTO);
		this.getManagedBean().setSmSupplierLs(resDTO.getSmSuppLs());
	}*/

	public String onComplaintRowSelect(Long complId) {
		log.info("---------------onComplaintRowSelect------------------");
		this.getManagedBean().setComplaintId(complId);
		this.retrieveComplaintInfo();
		return "complaintMain";
	}

/*	public String outputMsg(boolean succ, String msg) {
		if (succ) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, msg, ""));
			return "/xhtml/sm/disciplinaryAction/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}*/

	public void retrieveAllActiveComplaint() {
		// SmComplaintReview bBean = this.getManagedBean().getSmComplReview();
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IComplaintSO.class);
		ComplaintResDTO resDTO = reqSO.retrieveActiveComplaint(reqDTO);

		this.getManagedBean().setSmComplLs(resDTO.getSmComplLs());
	}

	public String updateComplaint() {
		log.info("--------------updateComplaint------------");
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IComplaintSO.class);
		
		List<SmAttachment> attachmentLs = validateDocumentUploadStatus();

		reqDTO.setCurrentStatus(getManagedBean().getSmComplaint().getPmStatus().getStatusId());
		
		updateComplaintOffenses();
		updateComplaintInfoByStatus();
		
		reqDTO.setSmComplaint(getManagedBean().getSmComplaint());
		
		if(attachmentLs.size() > 0){
			reqDTO.setAttachmentLs(attachmentLs);
		}
		
		ComplaintResDTO resDTO = reqSO.updateComplaint(reqDTO);

		
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
				MessageUtils.getMessage(MessageConst.INFO_723001 , new String[]{getManagedBean().getSmComplaint().getComplaintNo()}, 
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()))); 
		renewSesionObject();
		
		return CommonUIConst.ACKNOWLEDGE_PAGE;
		//return outputMsg(true, "Complaint has been updated.");
	}
	
	public void updateComplaintOffenses(){
		Map<Long, SmComplaintOffense> offensesMap = new HashMap<Long, SmComplaintOffense>();
		for(SmComplaintOffense complOffense : getManagedBean().getSmComplaint().getSmComplaintOffenses()){
			offensesMap.put(complOffense.getOffense().getParameterId(), complOffense);
		}
		
		//add new offenses
		for(String offenseParamId : getManagedBean().getSelectedOffenses()){
			if(offensesMap.get(Long.valueOf(offenseParamId))  == null ){
				PmParameter offenseParam = ParameterHelper.getInstance().getPmParameterById(Long.valueOf(offenseParamId));
				SmComplaintOffense offense = new SmComplaintOffense();
				offense.setSmComplaint(getManagedBean().getSmComplaint());
				offense.setOffense(offenseParam);
				getManagedBean().getSmComplaint().getSmComplaintOffenses().add(offense);
			}
		}
		
		//remove those non-selected offenses which saved before
		for (Map.Entry<Long, SmComplaintOffense> complOffense : offensesMap.entrySet()) {
			if(!getManagedBean().getSelectedOffenses().contains(complOffense.getKey().toString())){
				getManagedBean().getSmComplaint().getSmComplaintOffenses().remove(complOffense.getValue());
				log.info("Key>>>>>>" + complOffense.getKey().toString());
				log.info("Value>>>>>>" + complOffense.getValue().getComplaintOffenseId());
			}
		}
		
		log.info("final offenses size - " + getManagedBean().getSmComplaint().getSmComplaintOffenses().size());
		
	}
	
	
	public void updateComplaintInfoByStatus(){
		//if(getManagedBean().getSmComplaint().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
			Long curStatusId = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
			
			if(curStatusId.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue() || 
				curStatusId.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue() || 
				curStatusId.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
				
				Integer round = null;
				String committee = null;
				
				if(curStatusId.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
					round = DisciplinaryActionConst.PTPK_R1;
					committee = DisciplinaryActionConst.PTPK;
					
					if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
						updateComplaintStatus();
					}else{
						PmStatus nextStatus = SmCommonUtil.getInstance().retrievePmStatusById(DisciplinaryActionConst.REVIEW_BY_PTPK_REJECT);
						getManagedBean().getSmComplaint().setPmStatus(nextStatus);
					}
					getManagedBean().getSmComplReview().setRoundNo(round);
					getManagedBean().getSmComplReview().setReviewCommittee(committee);
					getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());
					
				}else if(curStatusId.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
					round = DisciplinaryActionConst.PTPK_R2;
					committee = DisciplinaryActionConst.PTPK;
					
					if(getManagedBean().getSecondReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
						updateComplaintStatus();
					}else{
						PmStatus nextStatus = SmCommonUtil.getInstance().retrievePmStatusById(DisciplinaryActionConst.REVIEW_SUPP_RESP_REJECT);
						getManagedBean().getSmComplaint().setPmStatus(nextStatus);
					}
					getManagedBean().getSecondReview().setRoundNo(round);
					getManagedBean().getSecondReview().setReviewCommittee(committee);
					getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSecondReview());	
				}
				else if(curStatusId.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
					committee = DisciplinaryActionConst.JKTT;
					updateComplaintStatus();
					//getManagedBean().getSecondReview().setRoundNo(round);
					//getManagedBean().getSecondReview().setReviewCommittee(committee);
					getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getJkttReview());
				}
				
			}else if(curStatusId.longValue() == DisciplinaryActionConst.NEW_COMPLAINT.longValue()){
				log.info("DECISION>>>>>>>>>" + getManagedBean().getSmComplaint().getDecision());
				if(getManagedBean().getSmComplaint().getDecision() == DisciplinaryActionConst.DECISION_INVALID){
					PmStatus nextStatus = SmCommonUtil.getInstance().retrievePmStatusById(DisciplinaryActionConst.NEW_COMPLAINT_REJECT);
					getManagedBean().getSmComplaint().setPmStatus(nextStatus);
				}else{
					updateComplaintStatus();
				}
			}else{
				updateComplaintStatus();
			}
		//}
	}
	
	
	
	public void updateComplaintStatus(){
		log.info("***updateComplaintStatus***");
		Long curStatusId = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
		Long nextStatusId = getNextStatus(curStatusId);
		
		log.info("***nextStatusId***" + nextStatusId);
		PmStatus nextStatus = SmCommonUtil.getInstance().retrievePmStatusById(nextStatusId);

		getManagedBean().getSmComplaint().setPmStatus(nextStatus);
	}

	public String issueShowCauseLetter() {
		return "issueShowCauseLetter";
	}

	public String pendingResponseLetterView() {
		return "pendingLetterResponse";
	}
	
	public List<SelectItem> getCountryDropDown(){
		return SmCommonUtil.getCountryList();
	}
	
	public List<PmState> getStateDropDownByCountry(Long countryId){
		log.info("*******getStateDropDownByCountry***********" + countryId);
		
		List<PmState> stateLs = null;
		
		if(countryId != null){
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			StateResDTO resDTO = SmServiceLookup.getStateSO().retriveStatesListByCountryID(reqDTO);
			stateLs = resDTO.getStateList();
		}
		
		return stateLs;
	}
	
	public List<PmDistrict> getDistrictDropDownByState(Long stateId){
		log.info("*******getDistrictDropDownByState***********" + stateId);
		
		List<PmDistrict> districtLs = null;
		
		if(stateId != null){
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			DistrictResDTO resDTO = SmServiceLookup.getDistrictSO().retriveDistrictListByStateID(reqDTO);
			districtLs = resDTO.getDistrictList();
		}
		
		return districtLs;
	}
	
	public List<PmCity> getCityDropDownByDistrict(Long districtId){
		log.info("*******getCityDropDownByDistrict***********" + districtId);
		
		List<PmCity> cityLs = null;
		if(districtId != null){
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			CityResDTO resDTO = SmServiceLookup.getCitySO().retriveCityListByDistrictId(reqDTO);
			cityLs = resDTO.getCityList();
		}
		
		return cityLs;
	}
	
	
	
	public Long getNextStatus(Long currentStatus){
		log.info("******getNextStatus*****" + currentStatus);
		Long newStatus = null;
		log.info("******DisciplinaryActionConst*****" + DisciplinaryActionConst.NEW_COMPLAINT);

		if(currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT.longValue()){
			log.info("******getNextStatus*****" + currentStatus);
			newStatus = DisciplinaryActionConst.REVIEW_BY_PTPK;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
			newStatus = DisciplinaryActionConst.PREPARE_SC_LETTER;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
			newStatus = DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
			newStatus = DisciplinaryActionConst.WAITING_SUPP_SC_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_SC_RESP){
			newStatus = DisciplinaryActionConst.REVIEW_SUPP_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
			newStatus = DisciplinaryActionConst.PREPARE_AC_LETTER;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_AC_LETTER.longValue()){
			newStatus = DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue()){
			newStatus = DisciplinaryActionConst.WAITING_SUPP_AC_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_AC_RESP.longValue()){
			newStatus = DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT;
			
		}else if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
			newStatus = DisciplinaryActionConst.COMPLAINT_COMPLETED;
		}
		
		return newStatus;
	}
	
	public SelectItem[] getDAStatusDropDown(){
		SelectItem[] statusLs = getStatusDropdownValues(ModuleCodeConst.SUPPLIER_MANAGEMENT, "DA");
		
		return statusLs;
	}
	
	public void retrieveComplaint(){
		log.info("--retrieveComplaint--");
		//this.getManagedBean().setLazy(true); 
		getManagedBean().getComplaintDataModel().reloadData();
		
	}		
	
	public void prerenderComplaintView(){
		renewSesionObject();
		resetComplaintSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, COMPLAINT_VIEW);
	}
	
	public void prerenderLodgeComplaint(){
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		if (this.getManagedBean().getSmComplaint() == null) {
			log.info("--------------BackingBean is Null--------------------");
			getManagedBean().setSmComplaint(new SmComplaint());
			getManagedBean().setSmComplainant(new SmComplainant());
			getManagedBean().getSmComplainant().setSmAddress(new SmAddress());
			getManagedBean().getSmComplainant().getSmAddress().setPmCountry(new PmCountry());
			getManagedBean().getSmComplainant().getSmAddress().setPmState(new PmState());
			getManagedBean().getSmComplainant().getSmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getSmComplainant().getSmAddress().setPmCity(new PmCity());
			getManagedBean().getSmComplainant().setIdentificationType(new PmParameter());
			getManagedBean().getSmComplainant().setNationality(new PmCountry());
			
			//Default country to MY
			getManagedBean().getSmComplainant().getSmAddress().setPmCountry(SmCommonUtil.retrieveCountryByCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE));
			getManagedBean().getSmComplainant().setNationality(SmCommonUtil.retrieveCountryByCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE));
			
			getManagedBean().setComplaintDocs(new ArrayList<SmAttachment>());
			getManagedBean().getSmComplaint().setSmSupplier(new SmSupplier());
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, LODGE_COMPLAINT);
	}
	
	/**
	 * reset search bean fields.
	 */
	public void resetComplaintSearchBean() {
		this.getManagedBean().setComplaintSearchBean(new ComplaintSearchBean());
		this.getManagedBean().setLazy(false);         
		//complaintDataModel = new ComplaintPagingDataModel();
	}
	
	public void identificationTypeOnChange(){
		PmParameter identType = retrieveIdentificationParam();
	
		if(getManagedBean().getSmComplainant().getIdentificationNo() != null){
			if(getManagedBean().getSmComplainant().getIdentificationType() != null){
				if(getManagedBean().getSmComplainant().getIdentificationType().getParameterCode() != null){
					if(!getManagedBean().getSmComplainant().getIdentificationType().
							getParameterCode().equalsIgnoreCase(identType.getParameterCode())){
						getManagedBean().getSmComplainant().setIdentificationNo(null);
					}
				}
			}
		}

		getManagedBean().getSmComplainant().setIdentificationType(identType);
		
	}
		
	
	public PmParameter retrieveIdentificationParam(){
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(getManagedBean().getSmComplainant().getIdentificationType().getParameterId());
		
		ParameterResDTO resDTO = SmServiceLookup.getParameterSO().retrieveParameterById(reqDTO);
		
		return resDTO.getPmParameter();
	}
	
	/**
	 * Sitevisit report upload
	 */
	public void docUpload(FileUploadEvent event) throws Exception {
		if (event.getFile() != null) {
			log.info("******siteVisiteReportUpload******");
			 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = new Date();
			 
			String subFolder = dateFormat.format(date);
			String filePath = SmCommonUtil.getUploadFilePath(DisciplinaryActionConst.COMPLAINT_DOC_PATH) + subFolder + "/";
			String fileName = String.valueOf((new Date()).getTime()) + "_" + event.getFile().getFileName();
			
			
			SmAttachment attachment = new SmAttachment();
			attachment.setFileName(fileName);
			attachment.setFilePath(filePath);
/*			attachment.setDocId(getManagedBean().getSmComplaint().getComplaintId());
			attachment.setDocType(DisciplinaryActionConst.COMPLAINT_DOC_TYPE);*/
			PmStatus pmStatus= getManagedBean().getSmComplaint().getPmStatus();
			
			if(pmStatus == null || pmStatus.getStatusId() == null){
				getManagedBean().getComplaintDocs().add(attachment);
			}else{
				log.info("current status ---" + pmStatus.getStatusId().longValue());
				
				if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
					attachment.setDocType(DisciplinaryActionConst.FIRST_PTPK_REVIEW_DOC_TYPE);
					if(getManagedBean().getPtpkDecisionDocs() == null){
						getManagedBean().setPtpkDecisionDocs(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getPtpkDecisionDocs().add(attachment);
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
					attachment.setDocType(DisciplinaryActionConst.SHOW_CAUSE_LETTER_DOC_TYPE);
					if(getManagedBean().getShowCauseLetters() == null){
						getManagedBean().setShowCauseLetters(new ArrayList<SmAttachment>()); 
					}
					
					if(getManagedBean().getShowCauseLetters().size() > 0){
						getManagedBean().getShowCauseLetters().set(0, attachment);
					}else{
						getManagedBean().getShowCauseLetters().add(attachment);
					}
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.WAITING_SUPP_SC_RESP.longValue()){
					attachment.setDocType(DisciplinaryActionConst.SHOW_CAUSE_RESP_DOC_TYPE);
					if(getManagedBean().getShowCauseResponseDocs() == null){
						getManagedBean().setShowCauseResponseDocs(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getShowCauseResponseDocs().add(attachment);
					
					log.info("ShowCauseResponseDocs>>>" +getManagedBean().getShowCauseResponseDocs().size());
					//RequestContext.getCurrentInstance().update("pnlLetterResponse");
					
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
					attachment.setDocType(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE);
					if(getManagedBean().getSecondPtpkDecisionDocs() == null){
						getManagedBean().setSecondPtpkDecisionDocs(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getSecondPtpkDecisionDocs().add(attachment);
					
					log.info("SecondPtpkDecisionDocs>>>" +getManagedBean().getSecondPtpkDecisionDocs().size());
					//RequestContext.getCurrentInstance().update("pnlLetterResponse");
					
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.PREPARE_AC_LETTER.longValue()){
					attachment.setDocType(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE);
					if(getManagedBean().getAccusationLetters() == null){
						getManagedBean().setAccusationLetters(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getAccusationLetters().add(attachment);

				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.WAITING_SUPP_AC_RESP.longValue()){
					attachment.setDocType(DisciplinaryActionConst.ACCUSATION_RESP_DOC_TYPE);
					if(getManagedBean().getAccusationResponseDocs() == null){
						getManagedBean().setAccusationResponseDocs(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getAccusationResponseDocs().add(attachment);
					
					
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
					attachment.setDocType(DisciplinaryActionConst.JKTT_DECISION_DOC_TYPE);
					if(getManagedBean().getJkttDecisionDocs() == null){
						getManagedBean().setJkttDecisionDocs(new ArrayList<SmAttachment>()); 
					}
					
					getManagedBean().getJkttDecisionDocs().add(attachment);
					
					log.info("setJkttDecisionDocs>>>" +getManagedBean().getJkttDecisionDocs().size());
					
				}
			}
			
			SmCommonUtil.processUpload(filePath, event, fileName);
		}
	}
	
	public void removeAttachment(){
		SmAttachment attachment = getManagedBean().getSelectedAttachment();
		
		if(attachment != null){
			List<SmAttachment> docLs = null;
			PmStatus pmStatus= getManagedBean().getSmComplaint().getPmStatus();
			if(pmStatus == null || pmStatus.getStatusId() == null){
				docLs = getManagedBean().getComplaintDocs();
			}else{
				if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
					docLs = getManagedBean().getPtpkDecisionDocs();
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
					docLs = getManagedBean().getShowCauseLetters();
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.WAITING_SUPP_SC_RESP.longValue()){
					docLs = getManagedBean().getShowCauseResponseDocs();
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
					docLs = getManagedBean().getSecondPtpkDecisionDocs();
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.PREPARE_AC_LETTER.longValue()){
					docLs = getManagedBean().getAccusationLetters();
				}else if(pmStatus.getStatusId().longValue() ==  DisciplinaryActionConst.WAITING_SUPP_AC_RESP.longValue()){
					docLs = getManagedBean().getAccusationResponseDocs();
				}
			}
			
			if(docLs!= null && !docLs.isEmpty()){
				int index = docLs.indexOf(attachment);
				SmAttachment attach = docLs.get(index);
				if(attach.getAttachmentId() != null){
					attach.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}else{
					docLs.remove(attachment);
				}
			}
		}
	}
	
	public void showDeleteDialog(SmAttachment attachment){
		String msg = SmCommonUtil.getMessage("lbl.sm.complainant.upload.delete.confirm", new String[]{attachment.getFileName()});
		
		getManagedBean().setAttachmentDeleteMsg(msg);
		getManagedBean().setSelectedAttachment(attachment);

	}
	
	public void attachmentDownload(SmAttachment attachment) throws IOException{
		if(attachment != null){
			String file = attachment.getFilePath() + "/" + attachment.getFileName();
			commonDownload(file,attachment.getFileName());
		}
	}
	
	private List<SmAttachment> getSmAttachmentsByDocTypeAndId(String docType, Long docId){
		SmAttachmentReqDTO reqDTO = new SmAttachmentReqDTO();
		reqDTO.setDocType(docType);
		reqDTO.setDocId(docId);
		
		List<SmAttachment> attachmentLs = SmServiceLookup.getSmAttachmentSO().
				retrieveSmAttachmentListByDocIdAndType(reqDTO).getAttachmentLs();
		
		return attachmentLs;
	}
	
	private List<SmAttachment> getDocumentsByTypeAndId(String docType, Long docId){
		List<SmAttachment> docLs = getSmAttachmentsByDocTypeAndId(docType, docId);
		log.info("docLs>>>>>" + docLs.size());
		
		return docLs;
	}
	
	private List<SmAttachment> validateDocumentUploadStatus(){
		List<SmAttachment> attachmentLs = new ArrayList<SmAttachment>();
		
		if(getManagedBean().getSmComplaint().getPmStatus() != null && 
				getManagedBean().getSmComplaint().getPmStatus().getStatusId() != null){
			Long currentStatus = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
			if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK){
				if(getManagedBean().getPtpkDecisionDocs() == null || getManagedBean().getPtpkDecisionDocs().size() < 1){
					throw new EpException(MessageConst.ERROR_723000);
				}
				attachmentLs = getManagedBean().getPtpkDecisionDocs();
				
			}else if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_SC_LETTER){
				if(getManagedBean().getShowCauseLetters() == null || getManagedBean().getShowCauseLetters().size() < 1){
					throw new EpException(MessageConst.ERROR_723001);
				}
				attachmentLs = getManagedBean().getShowCauseLetters();
				
			}else if(currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_SC_RESP){
				if(getManagedBean().getShowCauseResponseDocs()== null || getManagedBean().getShowCauseResponseDocs().size() < 1){
					throw new EpException(MessageConst.ERROR_723002);
				}
				attachmentLs = getManagedBean().getShowCauseResponseDocs();
				
			}else if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP){
				if(getManagedBean().getSecondPtpkDecisionDocs()== null || getManagedBean().getSecondPtpkDecisionDocs().size() < 1){
					throw new EpException(MessageConst.ERROR_723001);
				}
				attachmentLs = getManagedBean().getSecondPtpkDecisionDocs();
			}else if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_AC_LETTER){
				if(getManagedBean().getAccusationLetters()== null || getManagedBean().getAccusationLetters().size() < 1){
					throw new EpException(MessageConst.ERROR_723003);
				}
				attachmentLs = getManagedBean().getAccusationLetters();
			}else if(currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_AC_RESP){
				if(getManagedBean().getAccusationResponseDocs()== null || getManagedBean().getAccusationResponseDocs().size() < 1){
					throw new EpException(MessageConst.ERROR_723003);
				}
				attachmentLs = getManagedBean().getAccusationResponseDocs();
			}else if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT){
				if(getManagedBean().getJkttDecisionDocs()== null || getManagedBean().getJkttDecisionDocs().size() < 1){
					throw new EpException(MessageConst.ERROR_723000);
				}
				attachmentLs = getManagedBean().getJkttDecisionDocs();
			}
		}
		
		return attachmentLs;
	}
	
	private void populateInfoByStatus(){
		Long currentStatus = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
		log.info("*****currentStatus*****" + currentStatus);
		if(currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT.longValue() || 
				currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT_REJECT.longValue() ||
				currentStatus.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){

			getManagedBean().setSmComplReview(new SmComplaintReview());
			getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());

		}else if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
			String committee = null;
			Integer roundNo = null;
			log.info("****" + currentStatus +"*****");

			if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
				committee = DisciplinaryActionConst.PTPK;
				roundNo = DisciplinaryActionConst.PTPK_R1;

				if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP){
					roundNo = DisciplinaryActionConst.PTPK_R1;
				}else if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT){
					roundNo = DisciplinaryActionConst.PTPK_R2;
					committee = DisciplinaryActionConst.PTPK;
				}

				//for review 1 and JKTT
				//TODO may rearrange the flow
				SmComplaintReview complReview = getComplReviewByCommitteeAndRoundNo(committee,roundNo);
				getManagedBean().setSmComplReview(complReview);

				log.info("complReview roundNo>>>>" + roundNo);
				log.info("complReview committee>>>>" + committee);
				log.info("complReview>>>>" + complReview.getComplaintReviewId());
//				log.info("complReview received date>>>>" + complReview.getSmComplaintResponse().getSupplierReceivedDate());
				
				populateAttachedDocsByReviewIdAndStatus(complReview.getComplaintReviewId(),currentStatus);
				
				if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
					getManagedBean().getSmComplReview().setSmComplaintResponse(new SmComplaintResponse());
					getManagedBean().getSmComplReview().getSmComplaintResponse().setSmComplaintReview(getManagedBean().getSmComplReview());
				}
				
				if(currentStatus.longValue() >= DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
					
					if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_AC_LETTER.longValue() || 
							currentStatus.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue() || 
									currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_AC_RESP.longValue()){
						
						SmComplaintReview secondReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK,DisciplinaryActionConst.PTPK_R2);
						getManagedBean().setSecondReview(secondReview);
						populateAttachedDocsByReviewIdAndStatus(secondReview.getComplaintReviewId(),currentStatus);
						
						log.info("secondReview ID" + secondReview.getComplaintReviewId());
						
						if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue()){
							log.info("UPDATE_AC_RECEIVE_DATE - Prepare SmComplaintResponse");
							getManagedBean().getSecondReview().setSmComplaintResponse(new SmComplaintResponse());
							getManagedBean().getSecondReview().getSmComplaintResponse().setSmComplaintReview(secondReview);
						}
					}
					
					if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
						roundNo = DisciplinaryActionConst.PTPK_R2;
						
						getManagedBean().setSmComplReview(complReview);
						log.info("COmplaint Reponse----"  + 
						getManagedBean().getSmComplReview().getSmComplaintResponse().getSupplierResponseDate());
						
						getManagedBean().setSecondReview(new SmComplaintReview());
						getManagedBean().getSecondReview().setRoundNo(roundNo);
						getManagedBean().getSecondReview().setReviewCommittee(committee);
						getManagedBean().getSecondReview().setSmComplaint(getManagedBean().getSmComplaint());
					}
					
					if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
						SmComplaintReview secondReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK,DisciplinaryActionConst.PTPK_R2);
						getManagedBean().setSecondReview(secondReview);
						populateAttachedDocsByReviewIdAndStatus(secondReview.getComplaintReviewId(),currentStatus);
						
						log.info("secondReview----" + secondReview.getComplaintReviewId());
						
						getManagedBean().setJkttReview(new SmComplaintReview());
						getManagedBean().getJkttReview().setReviewCommittee(DisciplinaryActionConst.JKTT);
						getManagedBean().getJkttReview().setSmComplaint(getManagedBean().getSmComplaint());
					}
					
					if(currentStatus.longValue() == DisciplinaryActionConst.COMPLAINT_COMPLETED.longValue()){
						SmComplaintReview secondReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK,DisciplinaryActionConst.PTPK_R2);
						getManagedBean().setSecondReview(secondReview);
						populateAttachedDocsByReviewIdAndStatus(secondReview.getComplaintReviewId(),currentStatus);
						
						SmComplaintReview jkttReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.JKTT,null);
						getManagedBean().setJkttReview(jkttReview);
						populateAttachedDocsByReviewIdAndStatus(jkttReview.getComplaintReviewId(),currentStatus);
						
						log.info("secondReview----" + secondReview.getComplaintReviewId());
						log.info("jkttReview----" + jkttReview.getComplaintReviewId());
					}
				}
			}

			/*else if {
				committee = DisciplinaryActionConst.JKTT;
				roundNo = DisciplinaryActionConst.PTPK_R1;
			}*/
		}
		
		//Complaint DOCs
		getManagedBean().setComplaintDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.COMPLAINT_DOC_TYPE, 
				getManagedBean().getSmComplaint().getComplaintId()));
	}
	
	
	private void populateAttachedDocsByReviewIdAndStatus(Long reviewId, Long statusId){
		if(statusId.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
			getManagedBean().setShowCauseLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_LETTER_DOC_TYPE, reviewId));
			
		}else if(statusId.longValue() == DisciplinaryActionConst.WAITING_SUPP_SC_RESP.longValue()){
			getManagedBean().setShowCauseLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_LETTER_DOC_TYPE, reviewId));
			
		}else if(statusId.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
			getManagedBean().setShowCauseLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_LETTER_DOC_TYPE, reviewId));
			getManagedBean().setShowCauseResponseDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SHOW_CAUSE_RESP_DOC_TYPE, reviewId));
			
		}else if(statusId.longValue() == DisciplinaryActionConst.PREPARE_AC_LETTER.longValue()){
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, reviewId));
		}
		else if(statusId.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue()){
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, reviewId));
			getManagedBean().setAccusationLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE, reviewId));
			
		}else if(statusId.longValue() == DisciplinaryActionConst.WAITING_SUPP_AC_RESP.longValue()){
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, reviewId));
			getManagedBean().setAccusationLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE, reviewId));
			
		}else if(statusId.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, reviewId));
			getManagedBean().setAccusationLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE, reviewId));
			getManagedBean().setAccusationResponseDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_RESP_DOC_TYPE, reviewId));
		}
		else if(statusId.longValue() == DisciplinaryActionConst.COMPLAINT_COMPLETED.longValue()){
			log.info("review ID = "+reviewId);
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, reviewId));
			getManagedBean().setAccusationLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE, reviewId));
			getManagedBean().setAccusationResponseDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_RESP_DOC_TYPE, reviewId));
			getManagedBean().setJkttDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.JKTT_DECISION_DOC_TYPE, reviewId));
		}
	}
}
	
