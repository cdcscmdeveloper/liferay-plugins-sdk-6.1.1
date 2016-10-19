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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sm.DisciplinaryActionConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.myidentity.DiMyidentityReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.attachment.SmAttachmentReqDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.DiMyidentity;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplainantAddress;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintOffense;
import my.ep.persistence.entity.sm.SmComplaintResponse;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.ComplaintSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmComplaintSummaryVO;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);
	//private IPagingDataModel<SmComplaint> complaintDataModel = new ComplaintPagingDataModel();
	
	private final String COMPLAINT_VIEW = "/xhtml/sm/disciplinaryAction/complaintView.xhtml";
	private final String LODGE_COMPLAINT = "/xhtml/sm/complaint/lodgeComplaint.xhtml";
	private final String COMPLAINT_MANAGEMENT = "complaint-management";
	
	public ComplaintBackingBean() {
		
	}

	public String lodgeComplaint() {
		String complNo = String.valueOf((new Date()).getTime());
		Date date = new Date();
		Timestamp currDate = new Timestamp(date.getTime());
		
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		
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
		
		if(getManagedBean().isSameAddress()){
			getManagedBean().getMailingAddress().setSmAddress((SmAddress)getManagedBean().getIdentificationAddress().getSmAddress().cloneEntity());
		}
		getManagedBean().getSmComplainant().getSmComplainantAddresses().add(getManagedBean().getMailingAddress());
		getManagedBean().getSmComplainant().getSmComplainantAddresses().add(getManagedBean().getIdentificationAddress());
		
		
		reqDTO.setAttachmentLs(getManagedBean().getComplaintDocs());
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IComplaintSO.class);
		reqDTO.setSmComplaint(this.getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.lodgeComplaint(reqDTO);

		renewSesionObject();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Your complaint has been submitted successfully",""));
		
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
		
		getManagedBean().setSmComplaint(resDTO.getSmComplaint());
		getManagedBean().setSmComplainant(resDTO.getSmComplaint().getSmComplainant());
		SmComplaint complaint = getManagedBean().getSmComplaint();
		
		//retrieve complainant MyIdentity Info
		DiMyidentityReqDTO diMyidentityReqDTO = new DiMyidentityReqDTO();
		diMyidentityReqDTO.setDocId(complaint.getSmComplainant().getComplainantId());
		diMyidentityReqDTO.setDocType(ParameterConst.DOCTYPE_SUPPLIER_COMPLAINT);
		
		DiMyidentity diMyidentity = 
				SmServiceLookup.getDiMyidentitySO().retrieveDiMyIdentityByDocIdAndType(diMyidentityReqDTO).getDiMyidentity();
		getManagedBean().setDiMyidentity(diMyidentity);
		getManagedBean().setMyIdentityStatusDesc(getMyIdentityStatusDesc());
		getManagedBean().setMyIdentityResidentialStatus(getMyIdentityResidentialStatusDesc());
		
		if(diMyidentity != null){
			PmState pmState = SmCommonUtil.getInstance().getStateById(Long.valueOf(diMyidentity.getStateCode()));
			if(pmState != null){
				diMyidentity.setStateDesc(pmState.getStateName());
			}
		}
		log.info("--------------statusId-------------------"+ resDTO.getSmComplaint().getPmStatus().getStatusId());


		/*getManagedBean().setSmComplReview(new SmComplaintReview());*/
		log.info("--------------complaint Review Ls---------" + getManagedBean().getSmComplaint().getSmComplaintReviews().size());
		if(complaint.getSmComplaintReviews() == null){
			complaint.setSmComplaintReviews(new ArrayList<SmComplaintReview>());
		}
		
		if(complaint.getSmComplaintOffenses() == null){
			complaint.setSmComplaintOffenses(new ArrayList<SmComplaintOffense>());
		}
		
		getManagedBean().setSelectedOffenses(new ArrayList<String>());
		if(!getManagedBean().getSmComplaint().getSmComplaintOffenses().isEmpty()){
			for(SmComplaintOffense offense : getManagedBean().getSmComplaint().getSmComplaintOffenses()){
				getManagedBean().getSelectedOffenses().add(offense.getOffense().getParameterId().toString());
			}
		}
		
		if(resDTO.getSmComplaint().getSmComplainant().getSmComplainantAddresses().size() > 0){
			for(SmComplainantAddress complaintAddr : resDTO.getSmComplaint().getSmComplainant().getSmComplainantAddresses()){
				if(complaintAddr.getAddressType().equalsIgnoreCase(SupplierManagementConst.PERM_ADD)){
					getManagedBean().setIdentificationAddress(complaintAddr);
				}else{
					getManagedBean().setMailingAddress(complaintAddr);
				}
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
	
	public String getMyIdentityStatusDesc(){
		String label = null;
		
		if(getManagedBean().getDiMyidentity() != null){
			log.info("***getMyIdentityStatusDesc****" + getManagedBean().getDiMyidentity().getMyidentityStatus());
			if(my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_RECORD_STATUS_ACTIVE.
					contains(getManagedBean().getDiMyidentity().getMyidentityStatus())){
				label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.status.active", new String[]{});
			}else if(my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_RECORD_STATUS_DEAD.
					contains(getManagedBean().getDiMyidentity().getMyidentityStatus())){
				label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.status.dead", new String[]{});
			}
		}else{
			label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.status.invalid", new String[]{});
		}
		
		if(label != null){
			return SmCommonUtil.getMessage(label, null);
		}
		
		return label;
	}
	
	public String getMyIdentityResidentialStatusDesc(){
		String label = null;
		
		if(getManagedBean().getDiMyidentity() != null){
			if(my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_RESIDENTIAL_STATUS_CITIZEN.
					contains(getManagedBean().getDiMyidentity().getResidentialStatus())){
				label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.residential.citizen", new String[]{});
			}else if(my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_RESIDENTIAL_STATUS_PERMENANT.
					contains(getManagedBean().getDiMyidentity().getResidentialStatus())){
				label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.residential.permenant", new String[]{});
			}else if(my.ep.constant.sm.SupplierManagementConst.MYIDENTITY_RESIDENTIAL_STATUS_NON_CITIZEN.
					contains(getManagedBean().getDiMyidentity().getResidentialStatus())){
				label = SmCommonUtil.getMessage("lbl.sm.complainant.myidentity.residential.non.citizen", new String[]{});
			}
		}
		
		if(label != null){
			return SmCommonUtil.getMessage(label, null);
		}


		return label;
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


		Integer round = null;
		String committee = null;

		if(curStatusId.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
			round = DisciplinaryActionConst.PTPK_R1;
			committee = DisciplinaryActionConst.PTPK;

			getManagedBean().getSmComplReview().setRoundNo(round);
			getManagedBean().getSmComplReview().setReviewCommittee(committee);
			getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());

		}else if(curStatusId.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
			round = DisciplinaryActionConst.PTPK_R2;
			committee = DisciplinaryActionConst.PTPK;

			getManagedBean().getSecondReview().setRoundNo(round);
			getManagedBean().getSecondReview().setReviewCommittee(committee);
			getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSecondReview());	
		}
		else if(curStatusId.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
			committee = DisciplinaryActionConst.JKTT;
			getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getJkttReview());
		}

		updateComplaintStatus();
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
			if(getManagedBean().getSmComplaint().getDecision() == DisciplinaryActionConst.DECISION_INVALID){
				newStatus = DisciplinaryActionConst.NEW_COMPLAINT_REJECT;
			}else{
				newStatus = DisciplinaryActionConst.REVIEW_BY_PTPK;
			}
		}else if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_BY_PTPK.longValue()){
			if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
				newStatus = DisciplinaryActionConst.PREPARE_SC_LETTER;
			}else{
				newStatus = DisciplinaryActionConst.REVIEW_BY_PTPK_REJECT;
			}

		}else if(currentStatus.longValue() == DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
			newStatus = DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE;

		}else if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
			newStatus = DisciplinaryActionConst.WAITING_SUPP_SC_RESP;

		}else if(currentStatus.longValue() == DisciplinaryActionConst.WAITING_SUPP_SC_RESP){
			newStatus = DisciplinaryActionConst.REVIEW_SUPP_RESP;

		}else if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
			if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
				newStatus = DisciplinaryActionConst.PREPARE_AC_LETTER;
			}else{
				newStatus = DisciplinaryActionConst.REVIEW_SUPP_RESP_REJECT;
			}
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
	
	/**
	 * Prerednder for lodge Complaint
	 */
	public void prerenderLodgeComplaint(){
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		if (this.getManagedBean().getSmComplaint() == null) {
			PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			PmParameter defaultIdentificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
			
			log.info("--------------BackingBean is Null--------------------");
			getManagedBean().setSmComplaint(new SmComplaint());
			getManagedBean().setSmComplainant(new SmComplainant());

			getManagedBean().getSmComplainant().setIdentificationType(new PmParameter());
			getManagedBean().getSmComplainant().setNationality(new PmCountry());
			getManagedBean().setIdentificationAddress(new SmComplainantAddress());
			getManagedBean().getIdentificationAddress().setSmAddress(new SmAddress());
			getManagedBean().getIdentificationAddress().getSmAddress().setPmCountry(defaultCountry);
			getManagedBean().getIdentificationAddress().getSmAddress().setPmState(new PmState());
			getManagedBean().getIdentificationAddress().getSmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getIdentificationAddress().getSmAddress().setPmCity(new PmCity());
			getManagedBean().getIdentificationAddress().setAddressType(SupplierManagementConst.MAIL_ADD);
			getManagedBean().getIdentificationAddress().setSmComplainant(getManagedBean().getSmComplainant());
			
			getManagedBean().setMailingAddress(new SmComplainantAddress());
			getManagedBean().getMailingAddress().setSmAddress(new SmAddress());
			getManagedBean().getMailingAddress().getSmAddress().setPmCountry((PmCountry)defaultCountry.cloneEntity());
			getManagedBean().getMailingAddress().getSmAddress().setPmState(new PmState());
			getManagedBean().getMailingAddress().getSmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getMailingAddress().getSmAddress().setPmCity(new PmCity());
			getManagedBean().getMailingAddress().setAddressType(SupplierManagementConst.PERM_ADD);
			getManagedBean().getMailingAddress().setSmComplainant(getManagedBean().getSmComplainant());
			
			//Default country to MY
			getManagedBean().getSmComplainant().setNationality(SmCommonUtil.retrieveCountryByCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE));
			getManagedBean().getSmComplainant().setIdentificationType(defaultIdentificationType);
			
			getManagedBean().setComplaintDocs(new ArrayList<SmAttachment>());
			getManagedBean().getSmComplaint().setSmSupplier(new SmSupplier());
			
			getManagedBean().getSmComplainant().setSmComplainantAddresses(new ArrayList<SmComplainantAddress>());
			
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, LODGE_COMPLAINT);
	}
	
	public void nationalityOnChange(){
		SmComplainant complainant = getManagedBean().getSmComplainant();
		Long countryId = getManagedBean().getSmComplainant().getNationality().getCountryId();
		String identicationCode = null;
	
		if(countryId != null){
			PmCountry country = SmCommonUtil.retrievePmCountryById(countryId);
			complainant.setNationality(country);
			if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) || 
					country.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE)){
				identicationCode = ParameterConst.IDENTIFICATION_TYPES_IC;
			}else{
				identicationCode = ParameterConst.IDENTIFICATION_TYPES_PASSPORT;
			}
		}else{
/*			PmCountry defaultNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			complainant.setNationality(defaultNationality);*/
			identicationCode = ParameterConst.IDENTIFICATION_TYPES_IC;
		}
		
		//owner.setNationality(country);
		PmParameter identObj = ParameterHelper.getInstance().
				getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, identicationCode);
		complainant.setIdentificationType(identObj);
	}
	
	/**
	 * reset search bean fields.
	 */
	public void resetComplaintSearchBean() {
		this.getManagedBean().setComplaintSearchBean(new ComplaintSearchBean());
		this.getManagedBean().setLazy(false);         
		//complaintDataModel = new ComplaintPagingDataModel();
	}
	
	/**
	 * Identification Type on change
	 */
	public void identificationTypeOnChange(){
		PmParameter identType = ParameterHelper.getInstance().
				getPmParameterById(getManagedBean().getSmComplainant().getIdentificationType().getParameterId());
	
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
					
					if(getManagedBean().getAccusationLetters().size() > 0){
						getManagedBean().getAccusationLetters().set(0, attachment);
					}else{
						getManagedBean().getAccusationLetters().add(attachment);
					}

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
	
	public String returnToMenu(){
		return "complaintMain";
	}
	
	/**
	 * Retrieve Complaint Summary
	 * @return List<SmComplaintSummaryVO>
	 */
	public List<SmComplaintSummaryVO> retriveComplaintSummary(){
		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		
		ComplaintResDTO resDTO = SmServiceLookup.getComplaintSO().retrieveComplaintCountAndDesc(reqDTO);
		List<SmComplaintSummaryVO> complaintSummaryLs = new ArrayList<SmComplaintSummaryVO>();
		
		if(resDTO.getResultStrList().size() > 0){
			for(Object[] resultArr : resDTO.getResultStrList()){
				Long statusId = Long.valueOf(resultArr[0].toString());
				String statusName = resultArr[1].toString();
				int total = Integer.valueOf(resultArr[2].toString());
				
				SmComplaintSummaryVO complaintSummaryVO = new SmComplaintSummaryVO();
				complaintSummaryVO.setStatusId(statusId);
				complaintSummaryVO.setStatusName(statusName);
				complaintSummaryVO.setTotalRecord(total);
				
				if(statusId.longValue() != DisciplinaryActionConst.NEW_COMPLAINT_REJECT && statusId.longValue() != DisciplinaryActionConst.REVIEW_BY_PTPK_REJECT && 
					statusId.longValue() != DisciplinaryActionConst.REVIEW_SUPP_RESP_REJECT && statusId.longValue() != DisciplinaryActionConst.COMPLAINT_COMPLETED){
					complaintSummaryLs.add(complaintSummaryVO);
				}
			}
		}
		
		return complaintSummaryLs;
	}
	
	/**
	 * Navigate to Complaint Search Page
	 * @throws SystemException 
	 * @throws PortalException 
	 * @throws IOException 
	 */
	public void navigateToComplaintSearch() throws IOException, PortalException, SystemException{
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String commonUrl = themeDisplay.getURLCurrent().substring(0,themeDisplay.getURLCurrent().indexOf(
				themeDisplay.getLayout().getGroup().getFriendlyURL())+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());

		externalContext.redirect(commonUrl + "/" + COMPLAINT_MANAGEMENT);

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

			if(currentStatus.longValue() == DisciplinaryActionConst.NEW_COMPLAINT_REJECT.longValue()){
				SmComplaintReview complReview = getFirstReview();
				getManagedBean().setSmComplReview(complReview);
			}else{
				getManagedBean().setSmComplReview(new SmComplaintReview());
				getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());
			}

		}else if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
			log.info("****" + currentStatus +"*****");

			if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_SC_LETTER.longValue()){
				//for review 1 and JKTT
				getManagedBean().setSmComplReview(getFirstReview());
				
				//populate 1st review doc
				populateAttachedDocsByReviewIdAndStatus(getManagedBean().getSmComplReview().getComplaintReviewId(), currentStatus);
				
				if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_SC_RECEIVE_DATE.longValue()){
					getManagedBean().getSmComplReview().setSmComplaintResponse(new SmComplaintResponse());
					getManagedBean().getSmComplReview().getSmComplaintResponse().setSmComplaintReview(getManagedBean().getSmComplReview());
				}
				
				if(currentStatus.longValue() >= DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
					

					if(currentStatus.longValue() == DisciplinaryActionConst.REVIEW_SUPP_RESP.longValue()){
						//getManagedBean().setSmComplReview(complReview);
						log.info("COmplaint Reponse----"  + getManagedBean().getSmComplReview().getSmComplaintResponse().getSupplierResponseDate());
						
						getManagedBean().setSecondReview(new SmComplaintReview());
						getManagedBean().getSecondReview().setRoundNo(DisciplinaryActionConst.PTPK_R2);
						getManagedBean().getSecondReview().setReviewCommittee(DisciplinaryActionConst.PTPK);
						getManagedBean().getSecondReview().setSmComplaint(getManagedBean().getSmComplaint());
					}
					
					if(currentStatus.longValue() >= DisciplinaryActionConst.PREPARE_AC_LETTER.longValue()){
						SmComplaintReview secondReview = getSecondReview();
						getManagedBean().setSecondReview(secondReview);
						populateAttachedDocsByReviewIdAndStatus(secondReview.getComplaintReviewId(),currentStatus);
						
						log.info("secondReview ID" + secondReview.getComplaintReviewId());
						
						if(currentStatus.longValue() == DisciplinaryActionConst.UPDATE_AC_RECEIVE_DATE.longValue()){
							log.info("UPDATE_AC_RECEIVE_DATE - Prepare SmComplaintResponse");
							getManagedBean().getSecondReview().setSmComplaintResponse(new SmComplaintResponse());
							getManagedBean().getSecondReview().getSmComplaintResponse().setSmComplaintReview(secondReview);
						}
						
						if(currentStatus.longValue() == DisciplinaryActionConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
							//SmComplaintReview secondReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK,DisciplinaryActionConst.PTPK_R2);
							//getManagedBean().setSecondReview(secondReview);
							//(secondReview.getComplaintReviewId(),currentStatus);
							
							log.info("secondReview----" + secondReview.getComplaintReviewId());
							
							getManagedBean().setJkttReview(new SmComplaintReview());
							getManagedBean().getJkttReview().setReviewCommittee(DisciplinaryActionConst.JKTT);
							getManagedBean().getJkttReview().setSmComplaint(getManagedBean().getSmComplaint());
						}
						
						if(currentStatus.longValue() == DisciplinaryActionConst.COMPLAINT_COMPLETED.longValue()){
							getManagedBean().setSecondReview(getSecondReview());
							getManagedBean().setJkttReview(getJKTTReview());
							populateAttachedDocsByReviewIdAndStatus(getManagedBean().getJkttReview().getComplaintReviewId(),
									currentStatus.longValue());
							log.info("secondReview----" + getManagedBean().getSecondReview().getComplaintReviewId());
							log.info("jkttReview----" + getManagedBean().getJkttReview().getComplaintReviewId());
						}
					}
					
				}
			}
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
			
			//get Second review Info
			Long secondReviewId = getSecondReview().getComplaintReviewId();
			getManagedBean().setSecondPtpkDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.SECOND_PTPK_REVIEW_DOC_TYPE, secondReviewId));
			getManagedBean().setAccusationLetters(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_LETTER_DOC_TYPE, secondReviewId));
			getManagedBean().setAccusationResponseDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.ACCUSATION_RESP_DOC_TYPE, secondReviewId));

			getManagedBean().setJkttDecisionDocs(getDocumentsByTypeAndId(DisciplinaryActionConst.JKTT_DECISION_DOC_TYPE, reviewId));
		}
	}
	
	private SmComplaintReview getFirstReview(){
		SmComplaintReview complReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK, DisciplinaryActionConst.PTPK_R1);
		return complReview;
	}
	
	private SmComplaintReview getSecondReview(){
		SmComplaintReview secondReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.PTPK, DisciplinaryActionConst.PTPK_R2);
		return secondReview;
	}
	
	private SmComplaintReview getJKTTReview(){
		SmComplaintReview jkttReview = getComplReviewByCommitteeAndRoundNo(DisciplinaryActionConst.JKTT, null);
		
		return jkttReview;
	}
	
}
	
