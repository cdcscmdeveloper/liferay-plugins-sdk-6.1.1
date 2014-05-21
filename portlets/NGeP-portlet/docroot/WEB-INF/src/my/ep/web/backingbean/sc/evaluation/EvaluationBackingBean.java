package my.ep.web.backingbean.sc.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.EvaluationReqDTO;
import my.ep.dto.sc.quationtender.EvaluationResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtMeeting;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.evaluation.EvaluationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "evaluationBackingBean")
@RequestScoped
public class EvaluationBackingBean extends
		SourcingBaseBackingBean<EvaluationManagedBean> {
	private boolean skip;
	private int currentTab;
	private int outputTab;

	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	public static final String NAV_TO_STAGE2 = "/xhtml/ss/evaluation/peringkat2.xhtml";

	public EvaluationBackingBean() throws ParseException {

	}

	@PostConstruct
	public void init() throws IOException {
		try {
            if(this.getManagedBean().getScQtMeetings()==null){
			retriveDetails();
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void summaryChange(AjaxBehaviorEvent event){
	String view=(String)event.getComponent().getAttributes().get("value");

	this.getManagedBean().setSummaryViewBy(view);
	}

	public String retriveDetails() {
		IQTMeetingSO meetingSO;
		
		try {
			this.getManagedBean().setPrepareActiveIndex(0);
			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommitteType("SC");
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getMeetingDtls() != null) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMeetingDtls());

				}
				

				ScQtCommittee committee = resDTO.getQtCommittee();
				if (null == committee) {
					committee = new ScQtCommittee();
				}

				this.getManagedBean().setScQtCommittee(committee);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			meetingSO = null;
		}
		return "evaluationstage1view.xhtml";
	}

	

	

	public String retriveOpeningCommitteeDtls() {
		IQTMeetingSO meetingSO = null;
		
		this.getManagedBean().setPrepareActiveIndex(1);
		try {

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			MeetingResDTO resDTO = new MeetingResDTO();

			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			if (this.getManagedBean().getCommitteeMemberList() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommitteType("SC");
				resDTO = meetingSO.retrieveMeetingDls(reqDTO);

				if (resDTO.getCommitteMembers() != null
						&& !resDTO.getCommitteMembers().isEmpty()) {
					this.getManagedBean().setCommitteeMemberList(
							resDTO.getCommitteMembers());

				}
			}
			ScQtCommittee committee = resDTO.getQtCommittee();
			if (null == committee) {
				committee = new ScQtCommittee();
			}

			
			List<ScQtCommitteRemarks> qtRemarkList =committee
					.getQtCommitteRemarks();
			if (qtRemarkList == null) {
				qtRemarkList = new ArrayList<ScQtCommitteRemarks>();
			}
		
			if ( null!=qtRemarkList && !qtRemarkList.isEmpty()) {
				for (ScQtCommitteRemarks remark : qtRemarkList) {
					ScQtCommitteRemarks qtRemark = (ScQtCommitteRemarks) remark;
					//qtRemarkList.add(qtRemark);
				}
				this.getManagedBean().setQtCommitteeRemarks(qtRemarkList);
			}
			initializeRemarks();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "evaluationstage1view.xhtml";
	}

	public void initializeRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScQtCommitteRemarks remark = null;
		if (this.getManagedBean().getQtCommitteeRemarks() == null) {
			this.getManagedBean().setQtCommitteeRemarks(
					new ArrayList<ScQtCommitteRemarks>());
		}

		remark = new ScQtCommitteRemarks();
		List<ScQtRemark> qtRemarkList = new ArrayList<ScQtRemark>();
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQtCommittee(this.getManagedBean().getScQtCommittee());
		PmStatus status = new PmStatus();
		status.setStatusId(1L);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());

		}
		// qtRemarkList.add(remark);
		this.getManagedBean().getQtCommitteeRemarks().add(remark);
	}

	public String retriveTurutHadirDtls() {
		this.getManagedBean().setPrepareActiveIndex(2);
		try {

			if (this.getManagedBean().getScQtCommittee()
					.getMeetingAttendances() != null
					&& !this.getManagedBean().getScQtCommittee()
							.getMeetingAttendances().isEmpty()) {
				this.getManagedBean().setMeetingAttendanceList(
						this.getManagedBean().getScQtCommittee()
								.getMeetingAttendances());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "evaluationstage1view.xhtml";
	}

	public String technicalComplianceAction(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;
		EvaluationResDTO resDTO = null;
		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(checklist.getQtChecklistId());
			resDTO = evaluationSo.retrieveComplianceDtls(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {
				
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO
						.getComplianceResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "technicalComplianceStage.xhtml";
	}

	public String financialComplianceAction(ScQtCheckList checklist) {
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);

			reqDTO.setCheckListId(checklist.getQtChecklistId());
			EvaluationResDTO resDTO = evaluationSo
					.retrieveComplianceDtls(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList().isEmpty()
					&& resDTO.getComplianceResultBean().getResultList() != null) {

				this.getManagedBean().setComplianceFinancialProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {
				
				this.getManagedBean().setComplianceFinancialProposalCheckList(
						resDTO
						.getComplianceResultBean().getResultList());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "financialComplianceStage.xhtml";
	}
	
	
	public String sampleCompliance(ScQtSampleLable label){
		IQTEvaluationSO evaluationSo = null;

		try {
			this.getManagedBean().setScQtSampleLbl(label);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
       
			reqDTO.setLableId(label.getSampleLableId());
			EvaluationResDTO resDTO = evaluationSo
					.retrieveSampleItemDtlsByLable(reqDTO);
			if (resDTO.getItemResultBean().getResultList().isEmpty()
					&& resDTO.getItemResultBean().getResultList() != null) {
                 this.getManagedBean().setComplianceSampleItemList(new ArrayList<ScQtSampleItem>());
				

			} else {
				
		
			  this.getManagedBean().setComplianceSampleItemList(resDTO.getItemResultBean().getResultList());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "sampleComplianceStage.xhtml";
	}

	public void saveFinacialCompliance() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> finacialCompliance = this
					.getManagedBean().getComplianceFinancialProposalCheckList();
			if (finacialCompliance != null && !finacialCompliance.isEmpty()) {
				reqDTO.setComplianceList(finacialCompliance);
			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.", ""));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void saveTechnicalCompliance() {
		IQTEvaluationSO evaluationSo = null;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			List<ScQtProposalChecklist> technicalCompliance = this
					.getManagedBean().getComplianceTechnicalProposalCheckList();
			if (technicalCompliance != null && !technicalCompliance.isEmpty()) {
				reqDTO.setComplianceList(technicalCompliance);
			}
			evaluationSo.saveOrUpdateComplianceDtls(reqDTO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Compliance saved successfully.", ""));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String evaluationBackaction() {
		
			return "evaluationstage1view.xhtml";
		
	}

	

	public String retriveComplianceTechnical() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		this.getManagedBean().setComplianceActiveIndex(0);
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setSpecType("1");

			resDTO = specSo.retrieveCheckLists(reqDTO);

			this.getManagedBean().setComplianceTechnicalCheckList(
					resDTO.getSpecResultBean().getResultList());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceSample() {
		IQTEvaluationSO evaluationSo=null;

		this.getManagedBean().setComplianceActiveIndex(1);
		try {
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				evaluationSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
				reqDTO.setQtId(146L);
			EvaluationResDTO	resDTO = evaluationSo.retrieveSampleLableDtls(reqDTO);
			if(resDTO.getLableResultBean().getResultList()!=null && resDTO.getLableResultBean().getResultList().isEmpty()){
				if (this.getManagedBean().getScqt() == null) {
					getManagedBean().setScQt(new ScQt());
				}
				this.getManagedBean().setComplianceSampleLblsupplier(new ArrayList<ScQtSampleLable>());
			}
				
			else{
				
				Date d1 = new Date();
				DateUtils dateUtil = new DateUtils();
				for(ScQtSampleLable label:resDTO.getLableResultBean().getResultList()){
				long days = dateUtil.getDaysDifference(d1, label.getScQtProposal().getScQtSupplier().getScQt()
						.getSampleClosingDate());
				getManagedBean().setScQt(label.getScQtProposal().getScQtSupplier().getScQt());
				int day = (int) days;
				
				this.getManagedBean().setCountDays(day);
				}

			}
			this.getManagedBean().setComplianceSampleLblsupplier(resDTO.getLableResultBean().getResultList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceFinancial() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		this.getManagedBean().setComplianceActiveIndex(2);
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setSpecType("2");

			resDTO = specSo.retrieveCheckLists(reqDTO);

			this.getManagedBean().setCompliancefinancialCheckList(
					resDTO.getSpecResultBean().getResultList());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	public String retriveComplianceSummary() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		this.getManagedBean().setComplianceActiveIndex(3);
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setSpecType("2");

			resDTO = specSo.retrieveCheckLists(reqDTO);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return "evaluationstage1view.xhtml";
	}

	
	public void retriveTechnicalEvaluationStage(){
		
			
			IQTSpecSO specSo = null;
			SpecificationResDTO resDTO = null;
			this.getManagedBean().setEvaluatioStageActiveIndex(0);
			try {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				// this.getManagedBean().getScQt().getQtId()
				reqDTO.setQtId(791L);
				reqDTO.setSpecType("1");

				resDTO = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setEvaluationStgTechnicalCheckList(
						resDTO.getSpecResultBean().getResultList());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

			}
			//return "evaluationstage1view.xhtml";
			
		
	}
	
	public void retriveFinancialEvaluationStage(){
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		this.getManagedBean().setEvaluatioStageActiveIndex(1);
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(791L);
			reqDTO.setSpecType("2");

			resDTO = specSo.retrieveCheckLists(reqDTO);

			this.getManagedBean().setEvaluationStgfinancialCheckList(
					resDTO.getSpecResultBean().getResultList());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
	
	public void retriveSummaryEvaluationStage(){
		try{
			this.getManagedBean().setEvaluatioStageActiveIndex(2);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String viewTechnicalEvaluation(ScQtCheckList checklist){
		IQTEvaluationSO evaluationSo=null;
		EvaluationResDTO resDTO=null;
		this.getManagedBean().setScQtChecklist(checklist);
		try{
			/*this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(checklist.getQtChecklistId());
			resDTO = evaluationSo.retrieveComplianceDtls(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {
				
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO
						.getComplianceResultBean().getResultList());
			}*/
			if(184==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "technicalEvaluationStgView.xhtml";
			}
			if(185==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				//return "technicalSpecEvaluation.xhtml";
				return "productServiceTechScoreSpec.xhtml";
			}
			else{
				return "standardEvaluationTechList.xhtml";
				//return "techGovCompanyExperience.xhtml";
				//return "techPrivateCompanyExperience.xhtml";
				//return "senariTechnical.xhtml";
				//return "bilanganPerjaka.xhtml";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	return null;
	}
	
	public String evaluateTechnicalEvaluation(ScQtCheckList checklist){
		try{
			this.getManagedBean().setScQtChecklist(checklist);
			if(184==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "technicalEvaluationEvaluateDocument.xhtml";	
			}
			if(185==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				//return "technicalSpecEvaluation.xhtml";
				return "productServiceTechScoreSpec.xhtml";
			}else{
				return "standardEvaluationTechList.xhtml";
				//return "techGovCompanyExperience.xhtml";
				//return "techPrivateCompanyExperience.xhtml";
				//return "senariTechnical.xhtml";
				//return "bilanganPerjaka.xhtml";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	return null;
	}
	
	
	
	public String viewfinacialEvaluation(ScQtCheckList checklist){
		IQTEvaluationSO evaluationSo=null;
		EvaluationResDTO resDTO=null;
		this.getManagedBean().setScQtChecklist(checklist);
		try{
			/*this.getManagedBean().setScQtChecklist(checklist);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			evaluationSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTEvaluationSO.class);
			reqDTO.setCheckListId(checklist.getQtChecklistId());
			resDTO = evaluationSo.retrieveComplianceDtls(reqDTO);
			if (resDTO.getComplianceResultBean().getResultList() != null
					&& resDTO.getComplianceResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						new ArrayList<ScQtProposalChecklist>());

			} else {
				
				this.getManagedBean().setComplianceTechnicalProposalCheckList(
						resDTO
						.getComplianceResultBean().getResultList());
			}*/
			if(184==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "technicalEvaluationStgView.xhtml";
			}
			if(185==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "evaluationFinancialSpec.xhtml";
			}
			else{
				return "standardEvaluationFinList.xhtml";
				//return "standardEvaluationFinList.xhtml";
				//return "finGovCompanyExperience.xhtml";
				//return "finPrivateCompanyExperience.xhtml";
				//return "bankStatement.xhtml";
				//return "cashOnHand.xhtml";
				//return "creditFacility.xhtml";
				//return "assetsLiabilities.xhtml";
				//return "paidUpCapital.xhtml";
				//return "supplierInfo.xhtml";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	return null;
	}
	
	public String evaluatefinancialEvaluation(ScQtCheckList checklist){
		try{
			this.getManagedBean().setScQtChecklist(checklist);
			if(184==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "financialEvaluationEvaluationDocument.xhtml";	
			}
			if(185==this.getManagedBean().getScQtChecklist().getDefinationType().getParameterId()){
				return "evaluationFinancialSpec.xhtml";
			}else{
				return "standardEvaluationFinList.xhtml";	
				//return "finGovCompanyExperience.xhtml";
				//return "finPrivateCompanyExperience.xhtml";
				//return "bankStatement.xhtml";
				//return "cashOnHand.xhtml";
				//return "creditFacility.xhtml";
				//return "assetsLiabilities.xhtml";
				//return "paidUpCapital.xhtml";
				//return "supplierInfo.xhtml";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	return null;
	}

	public void reportDownLoadZipFile() throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(new File("C:\\upload\\clone.jpg"));
		files.add(new File("C:\\upload\\scan.docx"));
		files.add(new File("C:\\upload\\console.txt"));

		PortletResponse portalresponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(portalresponse);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=Ngep.zip");

		// ServletOutputStream out = response.getOutputStream();
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
				response.getOutputStream()));

		for (File file : files) {

			System.out.println("Adding file " + file.getName());
			zos.putNextEntry(new ZipEntry(file.getName()));

			// Get the file
			FileInputStream fis = null;
			try {

				fis = new FileInputStream(file);

			} catch (FileNotFoundException fnfe) {
				// If the file does not exists, write an error entry instead of
				// file
				// contents
				zos.write(("ERROR: Could not find file " + file.getName())
						.getBytes());
				zos.closeEntry();
				System.out.println("Could not find file "
						+ file.getAbsolutePath());
				continue;
			}

			BufferedInputStream fif = new BufferedInputStream(fis);

			// Write the contents of the file
			int data = 0;
			while ((data = fif.read()) != -1) {

				zos.write(data);
			}
			fif.close();
			zos.closeEntry();
			System.out.println("Finished adding file " + file.getName());
		}
		zos.close();
		response.flushBuffer();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void reportDownLoad() throws IOException {

		File file = new File("C:\\upload\\console.txt");

		InputStream input = new FileInputStream(file);
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition",
				"attachment; filename=\"" + file.getName() + "\"");
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();

		try {
			URLConnection uc = new URL("C:\\upload\\scan.docx")
					.openConnection();

			input = new BufferedInputStream(uc.getInputStream());
		} catch (Exception e) {
			// Cannot open connection, maybe local file
			e.printStackTrace();
			input = new FileInputStream(file.getPath());
		}
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			buffer = new byte[4096];
		}

		input.close();
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public void setTabValue(int currentTab, int outputTab) {
		try {

			this.currentTab = currentTab;
			this.outputTab = outputTab;
			if (outputTab == 1) {
				retriveComplianceTechnical();
				// retrives1Details();
			}
			if (outputTab == 2) {
				retriveTechnicalEvaluationStage();
				// getManagedBean().getTemplateSpecBean().setSelectedSpecQuestion(new
				// TplSpecQuestion());
				// retrives2Details();
			}
			if (outputTab == 3) {
				// this.getManagedBean().setScqt(new ScQt());
				// retriveStageOneSummary();

			}
			if (outputTab == 4) {
				// this.getManagedBean().setScqt(new ScQt());
				// retrivesummary();

			}
			if (outputTab == 5) {

			}
			if (outputTab == 6) {

			}
			if (outputTab == 0) {
				 retriveDetails();
			}

			if (getManagedBean().getTempTab() < outputTab) {
				getManagedBean().setTempTab(outputTab);
			}
			getManagedBean().setTabActiveIndex(outputTab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String navigateAction(String tabIndex) {

		if (!StringUtils.isEmpty(tabIndex)) {
			getManagedBean().setTabActiveIndex(new Integer(tabIndex));
		}
		return null;
	}

	public void updateSmAppl() throws Exception {
		try {
			// retrives2Details();
			getManagedBean().setTabActiveIndex(outputTab);

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
			// return SupplierManagementConst.PROCEED_MOF_REG;
		}

	}

	public String onFlowProcess(FlowEvent event) {
		if (skip) {
			skip = false; // reset in case user goes back
			return "confirm";
		} else {
			return event.getNewStep();
		}
	}

	public void addMeetingAction() {

		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();

			if (null == meetingList) {
				meetingList = new ArrayList<ScQtMeeting>();
			}

			ScQtMeeting meeting = new ScQtMeeting();
			// meeting.setPmStatus(new PmStatus());
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			 setUserDetails(meeting);
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			meeting.setMeetingStatus(statusres.getPmStatus());
			meetingList.add(meeting);

			this.getManagedBean().setScQtMeetings(meetingList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addTurutDetails() {

		try {
			List<ScQtMeetingAttendance> meetingAttendanceList = getManagedBean()
					.getMeetingAttendanceList();

			if (null == meetingAttendanceList) {
				meetingAttendanceList = new ArrayList<ScQtMeetingAttendance>();
			}
             
			ScQtMeetingAttendance meeting = new ScQtMeetingAttendance();
			meeting.setQtCommittee(this.getManagedBean().getScQtCommittee());
			 setUserDetails(meeting);
			meetingAttendanceList.add(meeting);

			this.getManagedBean().setMeetingAttendanceList(
					meetingAttendanceList);
           
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	 

	
	
	
	public void saveMeetingDtls(){
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			if (this.getManagedBean().getScQtMeetings() != null
					&& !this.getManagedBean().getScQtMeetings().isEmpty()) {
				qtCommittee.setScQtMeetings(this.getManagedBean()
						.getScQtMeetings());
			}
		
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
		} finally {

		}
		
	}
	
	
	public void saveOpenCommitteeDtls(){
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			
			if (this.getManagedBean().getCommitteeMemberList() != null
					&& !this.getManagedBean().getCommitteeMemberList()
							.isEmpty()) {
				qtCommittee.setCommitteeActivities(this.getManagedBean()
						.getCommitteeMemberList());
			}
			

		
		List<ScQtCommitteRemarks> remarksList=	this.getManagedBean().getQtCommitteeRemarks();
			List<ScQtCommitteRemarks> committeeRemarks = null;
			if (isNotNullOREmpty(remarksList)) {
				committeeRemarks = new ArrayList<ScQtCommitteRemarks>();
				for (ScQtCommitteRemarks remarks : remarksList) {
					if (remarks.getRemark() != null && "" != remarks.getRemark()) {
						committeeRemarks.add(remarks);
					}

				}

			}
			qtCommittee.setQtCommitteRemarks(committeeRemarks);
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
		} finally {

		}
		
	}
	
	
	public void saveMeetingAttendenceDtls(){
		try {

			IQTMeetingSO meetingso = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();
			ScQtCommittee qtCommittee = this.getManagedBean()
					.getScQtCommittee();
			
			if (getManagedBean().getMeetingAttendanceList() != null
					&& !getManagedBean().getMeetingAttendanceList().isEmpty()) {
				qtCommittee.setMeetingAttendances(getManagedBean()
						.getMeetingAttendanceList());
			}
			
			metReqDtO.setScQtCommitte(qtCommittee);
			meetingso.saveQTDtl(metReqDtO);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Preperation Saved Successfully",
							"Evaluation"));
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
		} finally {

		}
		
	}



	public void deleteMeetingDetails() {
		try {
			List<ScQtMeeting> meetingList = getManagedBean().getScQtMeetings();
			List<ScQtMeeting> selectMeeting = getManagedBean()
					.getMeetingMultiSelect();
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeeting meeting : selectMeeting)
					meetingList.remove(meeting);
			}
		} catch (Exception e) {

		}
	}

	public void deleteTurutHadirDetails() {
		try {
			List<ScQtMeetingAttendance> meetingList = getManagedBean()
					.getMeetingAttendanceList();
			List<ScQtMeetingAttendance> selectMeeting = getManagedBean()
					.getMeetingAttendanceSelectList();
			if (meetingList.size() == selectMeeting.size()) {
				meetingList.clear();
			} else {
				for (ScQtMeetingAttendance meeting : selectMeeting)
					meetingList.remove(meeting);
			}
		} catch (Exception e) {

		}
	}

	

	

	public void handleFileUpload(FileUploadEvent event) {

		this.getManagedBean().setFileName(event.getFile().getFileName());

		
	}

	public void removeFile() {
		this.getManagedBean().setFileName(new String());
	}
	
	public void reportUpload(){
		System.out.println("yeye7348445459");
		
	}

	/**
	 * @return the skip
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * @param skip
	 *            the skip to set
	 */
	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * @return the outputTab
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * @param outputTab
	 *            the outputTab to set
	 */
	public void setOutputTab(int outputTab) {
		this.outputTab = outputTab;
	}

}
