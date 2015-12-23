package my.ep.web.backingbean.sm.complaint;

import java.sql.Timestamp;
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
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.DisciplinaryActionStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintOffense;
import my.ep.persistence.entity.sm.SmComplaintResponse;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.ComplaintSearchBean;
import my.ep.web.managedbean.sm.complaint.ComplaintManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name = "complaintBackingBean")
@RequestScoped
public class ComplaintBackingBean extends BaseBackingBean<ComplaintManagedBean> {
	public static Logger log = Logger.getLogger(ComplaintManagedBean.class);
	//private IPagingDataModel<SmComplaint> complaintDataModel = new ComplaintPagingDataModel();
	
	private final String COMPLAINT_VIEW = "/xhtml/sm/disciplinaryAction/complaintView.xhtml";
	
	public ComplaintBackingBean() {

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
		}
	}

	public String lodgeComplaint() {
		String complNo = String.valueOf((new Date()).getTime());
		Date date = new Date();
		Timestamp currDate = new Timestamp(date.getTime());

		this.getManagedBean().getSmComplaint().setComplaintDate(currDate);
		this.getManagedBean().getSmComplaint().setComplaintNo(complNo);
		this.getManagedBean().getSmComplaint().setComplaintCategory("O");
		this.getManagedBean().getSmComplaint().setPmStatus(SmCommonUtil.getInstance().retrievePmStatusById(DisciplinaryActionStatusConst.NEW_COMPLAINT));

		this.getManagedBean().getSmComplainant()
				.setSmComplaint(this.getManagedBean().getSmComplaint());
		this.getManagedBean().getSmComplaint().setSmComplainant(getManagedBean().getSmComplainant());

		ComplaintReqDTO reqDTO = new ComplaintReqDTO();
		IComplaintSO reqSO = (IComplaintSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IComplaintSO.class);
		reqDTO.setSmComplaint(this.getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.lodgeComplaint(reqDTO);

		//if (resDTO.isSuccessful()) {
			renewSesionObject();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Your complaint has been submitted successfully",
							""));
			return "/xhtml/sm/complaint/outputMsg";
		/*} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Something Error", ""));
			return "lodgeComplaint";
		}*/
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
		
		Long currentStatus = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
		log.info("*****currentStatus*****" + currentStatus);
		if(currentStatus.longValue() == DisciplinaryActionStatusConst.NEW_COMPLAINT.longValue()){
			log.info("****NEW_COMPLAINT*****");
			getManagedBean().setSmComplReview(new SmComplaintReview());
			getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());
			
		}else if(currentStatus.longValue() >= DisciplinaryActionStatusConst.PREPARE_SL_LETTER.longValue()){
			String committee = null;
			Integer roundNo = null;
			log.info("****" + currentStatus +"*****");
			
			if(currentStatus.longValue() >= DisciplinaryActionStatusConst.PREPARE_SL_LETTER.longValue()){
				committee = DisciplinaryActionStatusConst.PTPK;
				roundNo = DisciplinaryActionStatusConst.PTPK_R1;

				if(currentStatus.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP){
					roundNo = DisciplinaryActionStatusConst.PTPK_R2;
				}
				
				if(currentStatus.longValue() == DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT){
					roundNo = null;
					committee = DisciplinaryActionStatusConst.JKTT;
				}
				
				SmComplaintReview complReview = getComplReviewByCommitteeAndRoundNo(committee,roundNo);
				getManagedBean().setSmComplReview(complReview);
				
				if(currentStatus.longValue() == DisciplinaryActionStatusConst.UPDATE_SL_RECEIVE_DATE || 
						currentStatus.longValue() == DisciplinaryActionStatusConst.UPDATE_AC_RECEIVE_DATE){
					getManagedBean().getSmComplReview().setSmComplaintResponse(new SmComplaintResponse());
					getManagedBean().getSmComplReview().getSmComplaintResponse().setSmComplaintReview(getManagedBean().getSmComplReview());
				}
				
				if(currentStatus.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP || 
						currentStatus.longValue() == DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT){
					getManagedBean().setSmComplReview(new SmComplaintReview());
					getManagedBean().getSmComplReview().setRoundNo(roundNo);
					getManagedBean().getSmComplReview().setReviewCommittee(committee);
					getManagedBean().getSmComplReview().setSmComplaint(getManagedBean().getSmComplaint());
				}
			}
			
			/*else if {
				committee = DisciplinaryActionStatusConst.JKTT;
				roundNo = DisciplinaryActionStatusConst.PTPK_R1;
			}*/
			
			
		}
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

	public String outputMsg(boolean succ, String msg) {
		if (succ) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, msg, ""));
			return "/xhtml/sm/disciplinaryAction/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}

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
		
		log.info("Status ID>>>>" + getManagedBean().getSmComplaint().getPmStatus().getStatusId());
		updateComplaintOffenses();
		updateComplaintInfoByStatus();
		
		reqDTO.setSmComplaint(getManagedBean().getSmComplaint());
		ComplaintResDTO resDTO = reqSO.updateComplaint(reqDTO);

		return outputMsg(resDTO.isSuccessful(), "Complaint has been updated.");
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
		if(getManagedBean().getSmComplaint().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
			Long curStatusId = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
			
			if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_BY_PTPK.longValue() || 
				curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP.longValue() || 
				curStatusId.longValue() == DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
				
				Integer round = null;
				String committee = null;
				
				if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_BY_PTPK.longValue()){
					round = DisciplinaryActionStatusConst.PTPK_R1;
					committee = DisciplinaryActionStatusConst.PTPK;
				}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP.longValue()){
					round = DisciplinaryActionStatusConst.PTPK_R2;
					committee = DisciplinaryActionStatusConst.PTPK;
				}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue()){
					committee = DisciplinaryActionStatusConst.JKTT;
				}
				
				if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
					//updateComplaintOffenses();
					updateComplaintStatus();
				}else{
					getManagedBean().getSmComplaint().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				getManagedBean().getSmComplReview().setRoundNo(round);
				getManagedBean().getSmComplReview().setReviewCommittee(committee);
				getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());
				
			}else{
				updateComplaintStatus();
			}
			
/*			if(curStatusId.longValue() == DisciplinaryActionStatusConst.NEW_COMPLAINT.longValue()){
				updateComplaintStatus();
				
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_BY_PTPK.longValue()){
				if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
					//updateComplaintOffenses();
					updateComplaintStatus();
				}else{
					getManagedBean().getSmComplaint().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				getManagedBean().getSmComplReview().setRoundNo(DisciplinaryActionStatusConst.PTPK_R1);
				getManagedBean().getSmComplReview().setReviewCommittee(DisciplinaryActionStatusConst.PTPK);
				getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());
			}
			else if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_BY_PTPK){
				updateComplaintStatus();
				
			}
			else if(curStatusId.longValue() == DisciplinaryActionStatusConst.PREPARE_SL_LETTER.longValue()){
				updateComplaintStatus();
				
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.UPDATE_SL_RECEIVE_DATE.longValue()){
				updateComplaintStatus();
				//setSmComplaintResponse(getManagedBean().getSmComplResponse());
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.WAITING_SUPP_SL_RESP.longValue()){
				updateComplaintStatus();
				//setSmComplaintResponse(getManagedBean().getSmComplResponse());
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP.longValue()){
				updateComplaintStatus();
				getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());
				//setSmComplaintResponse(getManagedBean().getSmComplResponse());
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.PREPARE_AC_LETTER.longValue()){
				updateComplaintStatus();
				//setSmComplaintResponse(getManagedBean().getSmComplResponse());
				getManagedBean().getSmComplReview().setRoundNo(DisciplinaryActionStatusConst.PTPK_R1);
				getManagedBean().getSmComplReview().setReviewCommittee(DisciplinaryActionStatusConst.PTPK);
				
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.UPDATE_AC_RECEIVE_DATE.longValue()){
				updateComplaintStatus();
			}else if(curStatusId.longValue() == DisciplinaryActionStatusConst.WAITING_SUPP_AC_RESP.longValue()){
				updateComplaintStatus();
			}else if((curStatusId.longValue() == DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT.longValue())){
				if(getManagedBean().getSmComplReview().getDecisionTaken().equalsIgnoreCase(String.valueOf(DbValueConst.RECORD_STATUS_ACTIVE))){
					//updateComplaintOffenses();
					updateComplaintStatus();
				}else{
					getManagedBean().getSmComplaint().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				}
				//getManagedBean().getSmComplReview().setRoundNo(DisciplinaryActionStatusConst.PTPK_R2);
				getManagedBean().getSmComplReview().setReviewCommittee(DisciplinaryActionStatusConst.JKTT);
				getManagedBean().getSmComplaint().getSmComplaintReviews().add(getManagedBean().getSmComplReview());
			}*/
			
		}
	}
	
	
	
	public void updateComplaintStatus(){
		log.info("***updateComplaintStatus***");
		Long curStatusId = getManagedBean().getSmComplaint().getPmStatus().getStatusId();
		Long nextStatusId = getNextStatus(curStatusId);
		
		log.info("***nextStatusId***" + nextStatusId);
		PmStatus nextStatus = SmCommonUtil.getInstance().retrievePmStatusById(nextStatusId);

		getManagedBean().getSmComplaint().setPmStatus(nextStatus);
	}

/*	public void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();

		this.getManagedBean().setCountryLs(resDTO.getCountryList());
	}*/

	/*public void retrievePmStateList() {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(this.getManagedBean().getSmComplainant()
				.getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);

		this.getManagedBean().setStateLs(resDTO.getStateList());
	}

	public List<PmDistrict> retrievePmDistrictList() {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(this.getManagedBean().getSmComplainant().getPmCountry().getCountryId());
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		return resDTO.getDistrictList();
	}

	public void retrievePmCityList(long districtId) {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		this.getManagedBean().setCityLs(resDTO.getCityList());
	}*/

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
		log.info("******DisciplinaryActionStatusConst*****" + DisciplinaryActionStatusConst.NEW_COMPLAINT);

		if(currentStatus.longValue() == DisciplinaryActionStatusConst.NEW_COMPLAINT.longValue()){
			log.info("******getNextStatus*****" + currentStatus);
			newStatus = DisciplinaryActionStatusConst.REVIEW_BY_PTPK;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.REVIEW_BY_PTPK.longValue()){
			newStatus = DisciplinaryActionStatusConst.PREPARE_SL_LETTER;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.PREPARE_SL_LETTER.longValue()){
			newStatus = DisciplinaryActionStatusConst.UPDATE_SL_RECEIVE_DATE;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.UPDATE_SL_RECEIVE_DATE.longValue()){
			newStatus = DisciplinaryActionStatusConst.WAITING_SUPP_SL_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.WAITING_SUPP_SL_RESP){
			newStatus = DisciplinaryActionStatusConst.REVIEW_SUPP_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.REVIEW_SUPP_RESP.longValue()){
			newStatus = DisciplinaryActionStatusConst.PREPARE_AC_LETTER;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.PREPARE_AC_LETTER.longValue()){
			newStatus = DisciplinaryActionStatusConst.UPDATE_AC_RECEIVE_DATE;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.UPDATE_AC_RECEIVE_DATE.longValue()){
			newStatus = DisciplinaryActionStatusConst.WAITING_SUPP_AC_RESP;
			
		}else if(currentStatus.longValue() == DisciplinaryActionStatusConst.WAITING_SUPP_AC_RESP.longValue()){
			newStatus = DisciplinaryActionStatusConst.PENDING_REVIEW_RESP_FROM_JKTT;
		}
		
		return newStatus;
	}
	
	public SelectItem[] getDAStatusDropDown(){
		SelectItem[] statusLs = getStatusDropdownValues(ModuleCodeConst.SUPPLIER_MANAGEMENT, "DA");
		
		return statusLs;
	}
	
	
	
	/*private String getComplaintSearchType(String type){
		String searchType = null;
		
		switch(type){
			case DisciplinaryActionStatusConst.SEARCHTYPE_EP_NO :
				searchType = "smSupplier.epNo";
				break;
	
			case DisciplinaryActionStatusConst.SEARCHTYPE_SUPPLIER_NAME :
				searchType = "smSupplier.companyName";
				break;
			
			case DisciplinaryActionStatusConst.SEARCHTYPE_COMPL_NO :
				searchType = "complaintNo";	
				break;
			
			case DisciplinaryActionStatusConst.SEARCHTYPE_MOF_NO :
				searchType = "complaintNo";	
			case DisciplinaryActionStatusConst.SEARCHTYPE_REG_NO :
				searchType = "smSupplier.regNo";
				break;
				
		}
		
		return searchType;
	}*/
	
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
	 * reset search bean fields.
	 */
	public void resetComplaintSearchBean() {
		this.getManagedBean().setComplaintSearchBean(new ComplaintSearchBean());
		this.getManagedBean().setLazy(false);         
		//complaintDataModel = new ComplaintPagingDataModel();
	}
		
/*	*//**
	 * Gets the search complaint
	 * 
	 * @return the searchAccountingOffice
	 *//*
	public IPagingDataModel<SmComplaint> getComplaintDataModel() {
		return complaintDataModel;
	}*/

}
