package my.ep.web.backingbean.ss.supplierfinalization;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.ss.quationtender.IQTEvaluationReportSO;
import my.ep.biz.service.ss.quationtender.IQTSuppFinalizationSO;
import my.ep.dto.ss.quationtender.EvaluationReportReqDTO;
import my.ep.dto.ss.quationtender.EvaluationReportResDTO;
import my.ep.dto.ss.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.ss.quationtender.SuppFinalizationResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.ss.quationtender.ScQtItem;
import my.ep.persistence.entity.ss.quationtender.ScQtMeeting;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalRanking;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.supplierfinalization.SupplierFinalizationManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * @author sri Supplier Finalization Backing Bean
 */
@ManagedBean(name = "sfBackingBean")
@RequestScoped
public class SupplierFinalizationBackingBean extends
		BaseBackingBean<SupplierFinalizationManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBackingBean.class);
	
	public SupplierFinalizationBackingBean() {
		optionsEvent();
		awardTypeEvent();
		retrieveMeetingCommitteeMemberDtl();
		retrieveSummaryDetsils();
	}
	
	/**
	 * retrieving  Meeting and Committee Member Listing
	 */
	public void retrieveMeetingCommitteeMemberDtl() {
		debugLogger("Meeting and Committee Member Details");
		IQTSuppFinalizationSO suppFinalizationSO = null;
	   try {
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setCommitteeType("QC");
			SuppFinalizationResDTO resDTO = suppFinalizationSO.retrieveSuppFinalizationDtl(reqDTO);
			
			// Meeting Details Listing
			if(getManagedBean().getMeetingDetails() == null ){
				List<ScQtMeeting> meetingDetails = resDTO.getMeetingDtls();
				this.getManagedBean().setMeetingDetails(meetingDetails);
				
			}
		
			// Committee Member Listing
			List<ScQtCommitteeActivity> committeeMemberList = resDTO.getCommitteMembers();
			this.getManagedBean().setCommitteeMemberList(committeeMemberList);
			
			// Committee sign-off Listing
			List<ScQtCommitteeActivity> signOffMemberlist = resDTO.getCommitteSignOffMembers();
			this.getManagedBean().setCommitteeSignOffMemberlist(signOffMemberlist);
			
			// Zone Listing
			List<ScQtZoneCriteria> zonelist = resDTO.getScQt().getScQtCriterias().getScQtZoneCriterias();
			this.getManagedBean().setZonelist(zonelist);
			
			List<ScQtItem> itemlist = resDTO.getItemResultBean().getResultList();
			this.getManagedBean().setItemSummarylist(itemlist);
			
			// populate Header Details
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
		    this.getManagedBean().setScQt(scQt);
		    
		    
		    if (!scQt.getScQtCriterias().isMultiLocality()){ 
		    	
				// Recommended Supplier Listing
				List<ScQtProposalRanking> recommendedSuppList = resDTO.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setRecommendedSuppList(recommendedSuppList);
				
				//  Shortlisted Supplier Listing
				List<ScQtProposalRanking> shortlistedSuppList = resDTO.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setShortlistedSuppList(shortlistedSuppList);
				this.getManagedBean().setTotalSelectedSupplier(shortlistedSuppList.size());
		    }

			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
			} finally {
				suppFinalizationSO = null;
			}
	}
	
	/**
	 * retrieving  Shortlisted and Recommended Supplier Listing Based on Zone
	 */
	public void shortlistedRecommendedSuppDtl(ScQtZoneCriteria scQtZoneCriteria) {
		debugLogger("Shortlisted and Recommended Supplier");
		IQTSuppFinalizationSO suppFinalizationSO = null;
	   try {
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			
		   if (scQtZoneCriteria != null ) {
				reqDTO.setQtId(146L);
				reqDTO.setQtZoneCriteriaId(scQtZoneCriteria.getQtZoneCriteriaId());
				SuppFinalizationResDTO resDTO = suppFinalizationSO.retrieveItemSupplierDtlByZone(reqDTO);
		
				// Recommended Supplier Listing
				List<ScQtProposalRanking> recommendedSuppList = resDTO.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setRecommendedSuppList(recommendedSuppList);
				
				//  Shortlisted Supplier Listing
				List<ScQtProposalRanking> shortlistedSuppList = resDTO.getQualifiedSuppResultBean().getResultList();
				this.getManagedBean().setShortlistedSuppList(shortlistedSuppList);
				this.getManagedBean().setTotalSelectedSupplier(shortlistedSuppList.size());
				this.getManagedBean().setScQtZoneCriteria(scQtZoneCriteria);
				
			}
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
			} finally {
				suppFinalizationSO = null;
			}
	}
	
	/**
	 * Method To call to save Shortlisted and Recommended Suppliers in Supplier Finalization
	 */
	public String saveSupplierFinalization() {
		debugLogger("Save Supplier Finalization");
		IQTSuppFinalizationSO suppFinalizationSO = null;
	   try {
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			List<ScQtProposalRanking> proposalRaningList=new ArrayList<ScQtProposalRanking>();
	        
			if(getManagedBean().getMeetingDetails() != null){
				reqDTO.setMeetingDtls(getManagedBean().getMeetingDetails());
			}
			
			if(getManagedBean().getCommitteeMemberList() != null){
				reqDTO.setCommitteMembers(getManagedBean().getCommitteeMemberList());
			}
			
			// Shortlisted Supplier listing
			List<ScQtProposalRanking> list = getManagedBean().getShortlistedSuppList();
					
			if (list != null){
			    proposalRaningList.addAll(list);
			}
			
			// Recommended Supplier listing
			list = getManagedBean().getRecommendedSuppList();
	        if (list != null){
			     proposalRaningList.addAll(list);
			}
			
	        if (proposalRaningList.size() > 0) {
	        	reqDTO.setQualifiedSuppliers(proposalRaningList);
	        	
	        }	
	           reqDTO.setScQt(this.getManagedBean().getScQt());
	           suppFinalizationSO.saveQTDtl(reqDTO);
	           FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Supplier Finalization "));
	    
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		} finally { 
			suppFinalizationSO = null;
			
		}
	return null;

	}
	
	/**
	 * Method To call to save Summary Details
	 */
	public String saveSummaryDtl() {
		debugLogger("Save Summary Details");
		IQTEvaluationReportSO evaluationReportSO = null;
	   try {
		    evaluationReportSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTEvaluationReportSO.class);
	      
	        if (getManagedBean().getQualifiedSupplist() != null) {
				EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
	        	reqDTO.setQualifiedSuppliers(getManagedBean().getQualifiedSupplist());
	        	evaluationReportSO.saveQTDtl(reqDTO);
	        	FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Summary Details "));
		         
	         }
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			evaluationReportSO = null;
		}
	     return null;
	}
	
	/**
	 * Adding new row for Meeting Details in Preparation
	 */
	public void addMeetingDetails() {
		debugLogger("add Meeting Details");
		IQTSuppFinalizationSO suppFinalizationSO = null;
		ScQtMeeting meetingDetails = new ScQtMeeting();
		 suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
		try {
			List<ScQtMeeting> meetingDtllist = getManagedBean()
					.getMeetingDetails();
			if (null == meetingDtllist) {
				meetingDtllist = new ArrayList<ScQtMeeting>();
			}
		
			reqDTO.setQtId(146L);
			SuppFinalizationResDTO resDTO = suppFinalizationSO.retrieveSuppFinalizationDtl(reqDTO);
			meetingDetails.setScQt(resDTO.getScQt());
			meetingDetails.setMeetingType("QC");
			meetingDtllist.add(meetingDetails);
			this.getManagedBean().setMeetingDetails(meetingDtllist);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}	
	
	/**
	 * retrieving  Meeting and Committee Member Listing
	 */
	public void retrieveSummaryDetsils() {
		debugLogger("Summery Details");
		IQTEvaluationReportSO evaluationReportSO = null;
	   try {
		   evaluationReportSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTEvaluationReportSO.class);
			
			EvaluationReportReqDTO reqDTO = new EvaluationReportReqDTO();
			reqDTO.setQtId(146L);
			EvaluationReportResDTO resDTO = evaluationReportSO.retrieveSummaryDtl(reqDTO);
						
			List<ScQtZoneCriteria> list = resDTO.getScQt().getScQtCriterias().getScQtZoneCriterias();
			this.getManagedBean().setZonelist(list);
			
			List<ScQtItem> itemlist = resDTO.getItemResultBean().getResultList();
			this.getManagedBean().setItemSuppFinalizationlist(itemlist);
			
			// populate Header Details
			ScQt scQt = resDTO.getScQt();
			populateParentOrgProfile(scQt);
		    this.getManagedBean().setScQt(scQt);
		    
		    if (!scQt.getScQtCriterias().isMultiLocality()){
		       if (resDTO.getQualifiedSuppResultBean() != null) {
					List<ScQtProposalRanking> qualifiedSupplist = resDTO.getQualifiedSuppResultBean().getResultList();
				       this.getManagedBean().setQualifiedSupplist(qualifiedSupplist);
				       this.getManagedBean().setTotalQulifiedSupplier(qualifiedSupplist.size());
				  }
					
			   if (resDTO.getDisqualifiedSuppResultBean() != null) {
					List<ScQtProposalRanking> disqualifiedSupplist = resDTO.getDisqualifiedSuppResultBean().getResultList();
					this.getManagedBean().setDisQualifiedSupplist(disqualifiedSupplist);
					this.getManagedBean().setTotalDisQualifiedSupplier(disqualifiedSupplist.size());
				 }
		    }
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
			} finally {
				evaluationReportSO = null;
			}
	   }
	
	
	/**
	 * retrieving  Qualified and Disqualified Supplier Listing By Zone in Summary
	 */
	public void qualifiedDisQualifiedSuppDtl(ScQtZoneCriteria scQtZoneCriteria) {
		debugLogger("Qualified And DisQualified Supplier");
		IQTSuppFinalizationSO suppFinalizationSO = null;
	   try {
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSuppFinalizationSO.class);
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			
		   if (scQtZoneCriteria != null ) {
				reqDTO.setQtId(146L);
				reqDTO.setQtZoneCriteriaId(scQtZoneCriteria.getQtZoneCriteriaId());
				SuppFinalizationResDTO resDTO = suppFinalizationSO.retrieveItemSupplierDtlByZone(reqDTO);
		
			   if (resDTO.getQualifiedSuppResultBean() != null) {
					List<ScQtProposalRanking> qualifiedSupplist = resDTO.getQualifiedSuppResultBean().getResultList();
			        this.getManagedBean().setQualifiedSupplist(qualifiedSupplist);
			        this.getManagedBean().setTotalQulifiedSupplier(qualifiedSupplist.size());
			   }
				
			  if (resDTO.getDisqualifiedSuppResultBean() != null) {
					List<ScQtProposalRanking> disqualifiedSupplist = resDTO.getDisqualifiedSuppResultBean().getResultList();
					this.getManagedBean().setDisQualifiedSupplist(disqualifiedSupplist);
					this.getManagedBean().setTotalDisQualifiedSupplier(disqualifiedSupplist.size());
			   }
			  
			  this.getManagedBean().setScQtZoneCriteria(scQtZoneCriteria);
			}
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
			} finally {
				suppFinalizationSO = null;
			}
	}
	
	/**
	 * Supplier Finalization Header Details
	 */
	public void populateParentOrgProfile(ScQt scQt) {
		PmOrgProfile parentOrgProfile = scQt.getPmOrgProfile()
				.getParentOrgProfile();

		while (parentOrgProfile != null) {
			// Ministry
			if (OrganizationType.MINISTRY.value().equals(
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setMinistryOrgProfile(parentOrgProfile);
			}
			// Department
			else if (OrganizationType.DEPARTMENT.value().equals(
					parentOrgProfile.getPmCode().getCode())) {
				scQt.setDepartmentOrgProfile(parentOrgProfile);
			}

			parentOrgProfile = parentOrgProfile.getParentOrgProfile();
		}
	}
	
	/**
	 *  Event for By Item and  By Package in Supplier Finalization
	 */
	public void awardTypeEvent() {
		debugLogger("awardTypeEvent");

		if (getManagedBean().isAwardType()) {
			this.getManagedBean().setByItemStatus(true);
			this.getManagedBean().setByPackageStatus(false);
		} else {
			this.getManagedBean().setByItemStatus(false);
			this.getManagedBean().setByPackageStatus(true);
		}

	}

	/**
	 *  Event for selecting Option for Forward Higher Authority and  
	 *  Re-Evaluation in Supplier Finalization
	 */
	public void decisionEvent() {   
	    debugLogger("Decision Event ");
		if (getManagedBean().getDecisionArray().length > 0) {
			for (int i = 0; i < getManagedBean().getDecisionArray().length; i++) {
				String[] decisionArray = getManagedBean().getDecisionArray();

				if (decisionArray[i].equalsIgnoreCase(QTConstants.SF_FORWARD_TO_HIGHER_AUTH)) {
					this.getManagedBean().setFhAuthority(true);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);

				} else if (decisionArray[i].equalsIgnoreCase(QTConstants.SF_RE_EVALUATION)) {
					this.getManagedBean().setReEvaluation(true);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);

				}else if (decisionArray[i].equalsIgnoreCase(QTConstants.SF_ONLINE_BIDDING) || (decisionArray[i].equalsIgnoreCase(QTConstants.SF_MULTIPLE_LOI))) {
					this.getManagedBean().setShortlistedSuppVisible(true);
					this.getManagedBean().setRecommendedSuppVisible(false);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);

			    } else if (decisionArray[i].equalsIgnoreCase(QTConstants.SF_AWARD)) {
			    	this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(true);
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);

			    }else {
					this.getManagedBean().setFhAuthority(false);
					this.getManagedBean().setReEvaluation(false);
					this.getManagedBean().setShortlistedSuppVisible(false);
					this.getManagedBean().setRecommendedSuppVisible(false);
				}

			}
		}
	}
	
	/**
	 *  Event for selecting Option for Basic , Multiple Items and 
	 *   By Suppler in Supplier Finalization  
	 */
	public void optionsEvent() {
		debugLogger("Options Event ");
		if (getManagedBean().getOptionType() != null) {
			if (getManagedBean().getOptionType().equalsIgnoreCase(QTConstants.SF_BASIC)) {
				this.getManagedBean().setBasicSelection(false);
				this.getManagedBean().setMultipleItemSelection(false);
				this.getManagedBean().setSelectionMode(QTConstants.SF_SINGLE);

			} else if (getManagedBean().getOptionType().equalsIgnoreCase(
					QTConstants.SF_MULTIPLE_ITEMS)) {
				       this.getManagedBean().setMultipleItemSelection(true);
				       this.getManagedBean().setBasicSelection(false);
				       this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
				
			} else if (getManagedBean().getOptionType().equalsIgnoreCase(QTConstants.SF_BY_SUPPLIER)) {
				      this.getManagedBean().setMultipleItemSelection(false);
				      this.getManagedBean().setBasicSelection(true);
				      this.getManagedBean().setSelectionMode(QTConstants.SF_MULTIPLE);
			}

		}
	}
	
	/**
	 * Method to call Supplier Details popup  in supplier finalization
	 */
	public void showSupplierDetails() {
		debugLogger("showSuppDetails");
		if (getManagedBean().getScQtProposalRanking() != null) {
			this.getManagedBean().setScQtProposalRanking(getManagedBean().getScQtProposalRanking());
	    }
     }
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
}
