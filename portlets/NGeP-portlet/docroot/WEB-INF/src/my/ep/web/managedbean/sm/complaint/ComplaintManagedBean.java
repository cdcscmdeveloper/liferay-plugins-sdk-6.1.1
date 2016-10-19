package my.ep.web.managedbean.sm.complaint;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sm.complaint.IComplaintSO;
import my.ep.constant.sm.DisciplinaryActionConst;
import my.ep.dto.sm.complaint.ComplaintReqDTO;
import my.ep.dto.sm.complaint.ComplaintResDTO;
import my.ep.persistence.entity.pm.common.DiMyidentity;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmComplainant;
import my.ep.persistence.entity.sm.SmComplainantAddress;
import my.ep.persistence.entity.sm.SmComplaint;
import my.ep.persistence.entity.sm.SmComplaintReview;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.ComplaintSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmComplaintSummaryVO;

@ManagedBean(name = "complaintMB")
@SessionScoped
public class ComplaintManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private SmComplaint smComplaint;
	private List<SmSupplier> smSupplierLs;
	private SmSupplier selectedSupplier;
	private SmComplaintReview smComplReview;
	private Long complaintId;
	private String taskId;
	private String[] bpmAction;
	private String taskName;
	private List<SmComplaint> smComplLs;
	private SmComplaint[] selectedComplaint;
	
	private ComplaintSearchBean complaintSearchBean;

	private List<String> selectedOffenses;
	private IPagingDataModel<SmComplaint> complaintDataModel = new ComplaintPagingDataModel();
	
	private SmComplainant smComplainant;
	//private SmComplaintResponse smComplResponse;
/*	private List<PmCountry> countryLs;
	private List<PmState> stateLs;
	private List<PmCity> cityLs;
	private List<PmDistrict> districtLs;*/
	
	private boolean sameAddress;
	
	private List<PmParameter> complaintTypes;
	private boolean isLazy;
	
	private List<SmAttachment> complaintDocs;
	private SmAttachment selectedAttachment;
	private String attachmentDeleteMsg;

	private boolean complaintTerm;
	private List<SmAttachment> ptpkDecisionDocs;
	private List<SmAttachment> secondPtpkDecisionDocs;
	private List<SmAttachment> showCauseLetters;
	private List<SmAttachment> showCauseResponseDocs;
	private List<SmAttachment> accusationLetters;
	private List<SmAttachment> accusationResponseDocs;
	private List<SmAttachment> jkttDecisionDocs;
	
	private SmComplaintReview secondReview;
	private SmComplaintReview jkttReview;
	
	private DiMyidentity diMyidentity;
	private String myIdentityStatusDesc;
	private String myIdentityResidentialStatus;
	
	private List<SmComplaintSummaryVO> complaintSummaryLs;
	private SmComplainantAddress identificationAddress;
	private SmComplainantAddress mailingAddress;

	public SmComplaint getSmComplaint() {
		return smComplaint;
	}

	public void setSmComplaint(SmComplaint smComplaint) {
		this.smComplaint = smComplaint;
	}

	public List<SmSupplier> getSmSupplierLs() {
		return smSupplierLs;
	}

	public void setSmSupplierLs(List<SmSupplier> smSupplierLs) {
		this.smSupplierLs = smSupplierLs;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public SmComplaintReview getSmComplReview() {
		return smComplReview;
	}

	public void setSmComplReview(SmComplaintReview smComplReview) {
		this.smComplReview = smComplReview;
	}


	public Long getComplaintId() {
		return complaintId;
	}

	public void setComplaintId(Long complaintId) {
		this.complaintId = complaintId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public List<SmComplaint> getSmComplLs() {
		return smComplLs;
	}

	public void setSmComplLs(List<SmComplaint> smComplLs) {
		this.smComplLs = smComplLs;
	}

	public SmComplaint[] getSelectedComplaint() {
		return selectedComplaint;
	}

	public void setSelectedComplaint(SmComplaint[] selectedComplaint) {
		this.selectedComplaint = selectedComplaint;
	}

	public SmComplainant getSmComplainant() {
		return smComplainant;
	}

	public void setSmComplainant(SmComplainant smComplainant) {
		this.smComplainant = smComplainant;
	}


	public boolean isSameAddress() {
		return sameAddress;
	}

	public void setSameAddress(boolean sameAddress) {
		this.sameAddress = sameAddress;
	}

	public List<PmParameter> getComplaintTypes() {
		return complaintTypes;
	}

	public void setComplaintTypes(List<PmParameter> complaintTypes) {
		this.complaintTypes = complaintTypes;
	}

	public List<String> getSelectedOffenses() {
		return selectedOffenses;
	}

	public void setSelectedOffenses(List<String> selectedOffenses) {
		this.selectedOffenses = selectedOffenses;
	}

	public ComplaintSearchBean getComplaintSearchBean() {
		return complaintSearchBean;
	}

	public void setComplaintSearchBean(ComplaintSearchBean complaintSearchBean) {
		this.complaintSearchBean = complaintSearchBean;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
	private class ComplaintPagingDataModel extends PagingDataModel<SmComplaint> {
		/**
		 * Instantiates a new search office paging data model.
		 */
		public ComplaintPagingDataModel() {
			super("complaintSR");
		}
		
		@Override
		public ISearchResultBean<SmComplaint> pageChangeListener(ISearchBean<SmComplaint> searchBean) {

			try {	
				ISearchBean<SmComplaint> searchBeanData = getComplaintSearchBeanData(searchBean);
				ComplaintReqDTO reqDTO = new ComplaintReqDTO();
				reqDTO.setComplaintSearchBean(getComplaintSearchBeanData(searchBeanData));
				
				IComplaintSO reqSO = SmServiceLookup.getComplaintSO();
				ComplaintResDTO resDTO = reqSO.retrieveComplaintBySearch(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getComplaintSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * search business nature info based on codes and names search
	 */
	private ISearchBean<SmComplaint> getComplaintSearchBeanData(ISearchBean<SmComplaint> searchBean) {
		
		ComplaintSearchBean complaintSearchBean = getComplaintSearchBean();
		if (StringUtils.isNotBlank(complaintSearchBean.getSearchBy())){	
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(getComplaintSearchType(complaintSearchBean.getSearchBy()),
							SearchOperator.OPERATOR_EQUALS.value(), complaintSearchBean.getKeyword() , true));
			
		}
		
		if(complaintSearchBean.getStatus() != null){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmStatus.statusId",
							SearchOperator.OPERATOR_EQUALS.value(), complaintSearchBean.getStatus().toString(), true));
		}
		
		return searchBean;
		
	}
	
	private String getComplaintSearchType(String type){
		String searchType = null;
		
		switch(type){
			case DisciplinaryActionConst.SEARCHTYPE_EP_NO :
				searchType = "smSupplier.epNo";
				break;
	
			case DisciplinaryActionConst.SEARCHTYPE_SUPPLIER_NAME :
				searchType = "smSupplier.companyName";
				break;
			
			case DisciplinaryActionConst.SEARCHTYPE_COMPL_NO :
				searchType = "complaintNo";	
				break;
			
			case DisciplinaryActionConst.SEARCHTYPE_MOF_NO :
				searchType = "complaintNo";	
			case DisciplinaryActionConst.SEARCHTYPE_REG_NO :
				searchType = "smSupplier.regNo";
				break;
				
		}
		
		return searchType;
	}

	public IPagingDataModel<SmComplaint> getComplaintDataModel() {
		return complaintDataModel;
	}

	public void setComplaintDataModel(
			IPagingDataModel<SmComplaint> complaintDataModel) {
		this.complaintDataModel = complaintDataModel;
	}

	public List<SmAttachment> getComplaintDocs() {
		return complaintDocs;
	}

	public void setComplaintDocs(List<SmAttachment> complaintDocs) {
		this.complaintDocs = complaintDocs;
	}

	public SmAttachment getSelectedAttachment() {
		return selectedAttachment;
	}

	public void setSelectedAttachment(SmAttachment selectedAttachment) {
		this.selectedAttachment = selectedAttachment;
	}

	public String getAttachmentDeleteMsg() {
		return attachmentDeleteMsg;
	}

	public void setAttachmentDeleteMsg(String attachmentDeleteMsg) {
		this.attachmentDeleteMsg = attachmentDeleteMsg;
	}

	public boolean isComplaintTerm() {
		return complaintTerm;
	}

	public void setComplaintTerm(boolean complaintTerm) {
		this.complaintTerm = complaintTerm;
	}

	public List<SmAttachment> getPtpkDecisionDocs() {
		return ptpkDecisionDocs;
	}

	public void setPtpkDecisionDocs(List<SmAttachment> ptpkDecisionDocs) {
		this.ptpkDecisionDocs = ptpkDecisionDocs;
	}

	public List<SmAttachment> getShowCauseLetters() {
		return showCauseLetters;
	}

	public void setShowCauseLetters(List<SmAttachment> showCauseLetters) {
		this.showCauseLetters = showCauseLetters;
	}

	public List<SmAttachment> getShowCauseResponseDocs() {
		return showCauseResponseDocs;
	}

	public void setShowCauseResponseDocs(List<SmAttachment> showCauseResponseDocs) {
		this.showCauseResponseDocs = showCauseResponseDocs;
	}

	public List<SmAttachment> getSecondPtpkDecisionDocs() {
		return secondPtpkDecisionDocs;
	}

	public void setSecondPtpkDecisionDocs(List<SmAttachment> secondPtpkDecisionDocs) {
		this.secondPtpkDecisionDocs = secondPtpkDecisionDocs;
	}

	public SmComplaintReview getSecondReview() {
		return secondReview;
	}

	public void setSecondReview(SmComplaintReview secondReview) {
		this.secondReview = secondReview;
	}

	public List<SmAttachment> getAccusationLetters() {
		return accusationLetters;
	}

	public void setAccusationLetters(List<SmAttachment> accusationLetters) {
		this.accusationLetters = accusationLetters;
	}

	public List<SmAttachment> getAccusationResponseDocs() {
		return accusationResponseDocs;
	}

	public void setAccusationResponseDocs(List<SmAttachment> accusationResponseDocs) {
		this.accusationResponseDocs = accusationResponseDocs;
	}

	public SmComplaintReview getJkttReview() {
		return jkttReview;
	}

	public void setJkttReview(SmComplaintReview jkttReview) {
		this.jkttReview = jkttReview;
	}

	public List<SmAttachment> getJkttDecisionDocs() {
		return jkttDecisionDocs;
	}

	public void setJkttDecisionDocs(List<SmAttachment> jkttDecisionDocs) {
		this.jkttDecisionDocs = jkttDecisionDocs;
	}

	public DiMyidentity getDiMyidentity() {
		return diMyidentity;
	}

	public void setDiMyidentity(DiMyidentity diMyidentity) {
		this.diMyidentity = diMyidentity;
	}

	public String getMyIdentityStatusDesc() {
		return myIdentityStatusDesc;
	}

	public void setMyIdentityStatusDesc(String myIdentityStatusDesc) {
		this.myIdentityStatusDesc = myIdentityStatusDesc;
	}

	public String getMyIdentityResidentialStatus() {
		return myIdentityResidentialStatus;
	}

	public void setMyIdentityResidentialStatus(String myIdentityResidentialStatus) {
		this.myIdentityResidentialStatus = myIdentityResidentialStatus;
	}

	public List<SmComplaintSummaryVO> getComplaintSummaryLs() {
		return complaintSummaryLs;
	}

	public void setComplaintSummaryLs(List<SmComplaintSummaryVO> complaintSummaryLs) {
		this.complaintSummaryLs = complaintSummaryLs;
	}

	public SmComplainantAddress getIdentificationAddress() {
		return identificationAddress;
	}

	public SmComplainantAddress getMailingAddress() {
		return mailingAddress;
	}

	public void setIdentificationAddress(SmComplainantAddress identificationAddress) {
		this.identificationAddress = identificationAddress;
	}

	public void setMailingAddress(SmComplainantAddress mailingAddress) {
		this.mailingAddress = mailingAddress;
	}
}
