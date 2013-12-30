package my.ep.web.backingbean.ss.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTEvaluationSO;
import my.ep.biz.service.ss.quationtender.IQTMeetingSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.biz.service.ss.quationtender.IQTTemplateSO;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.ss.quationtender.EvaluationReqDTO;
import my.ep.dto.ss.quationtender.EvaluationResDTO;
import my.ep.dto.ss.quationtender.MeetingReqDTO;
import my.ep.dto.ss.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.ss.quationtender.QTTemplateDocResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.persistence.entity.ss.quationtender.ScEvaluationResult;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtChecklistEvaluation;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.ss.quationtender.ScQtManualEvaluation;
import my.ep.persistence.entity.ss.quationtender.ScQtMeeting;
import my.ep.persistence.entity.ss.quationtender.ScQtProposal;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalLabel;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ss.evaluation.EvaluationManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "evaluationBackingBean")
@RequestScoped
public class EvaluationBackingBean extends
		SourcingBaseBackingBean<EvaluationManagedBean> {
	private boolean skip;
	private int currentTab;
	private int outputTab;
	private IPagingDataModel<ScQtChecklistEvaluation> checkListEvaluationtDataModel = new CheckListEvaluationPagingDataModel();
	private IPagingDataModel<ScEvaluationResult> evaluationResultDataModel = new EvaluationResultPagingDataModel();
	private Map<Long, Boolean> checked = new HashMap<Long, Boolean>();
	public static final String NAV_TO_STAGE2 = "/xhtml/ss/evaluation/peringkat2.xhtml";

	public EvaluationBackingBean() throws ParseException {

	}

	@PostConstruct
	public void init() throws IOException {
		try {
			if (this.getManagedBean().getChecklistEvaluation() == null) {
				getManagedBean().setChecklistEvaluation(
						new ScQtChecklistEvaluation());

			}
			if (this.getManagedBean().getChecklistEvaluation()
					.getScQtCheckList() == null) {

				getManagedBean().getChecklistEvaluation().setScQtCheckList(
						new ScQtCheckList());
			}

			if (this.getManagedBean().getChecklistEvaluation()
					.getScQtProposal() == null) {
				getManagedBean().getChecklistEvaluation().setScQtProposal(
						new ScQtProposal());

			}

			if (this.getManagedBean().getMeeting() == null) {
				getManagedBean().setMeeting(new ScQtMeeting());
			}

			// downLoadAction();
			retriveDetails();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void retriveDetails() {
		IQTEvaluationSO evaluationSO;
		try {
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationResDTO resDTO = new EvaluationResDTO();

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			this.getManagedBean().setEvaluateStatus(true);
			if (this.getManagedBean().getScQtMeetings() == null) {
				reqDTO.setQtId(146L);
				reqDTO.setCommType("QC");
				resDTO = evaluationSO
						.retrieveEvaluationPreparationDtlsByQtId(reqDTO);
				IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(146L);
				QuationTenderResDTO tenderResDTO = detailSO
						.retriveQTDetails(tenderReqDTO);
				QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
				detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
				populateParentOrgProfile(tenderResDTO.getQtDetail());

				if (resDTO.getMettingDls() != null) {

					this.getManagedBean().setScQtMeetings(
							resDTO.getMettingDls());

				}
				if (resDTO.getCommitteMembersList() != null) {
					this.getManagedBean().setCommitteeMemberList(
							resDTO.getCommitteMembersList());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

	}

	public void retrives1Details() {
		IQTEvaluationSO evaluationSO;
		try {

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO1 = new EvaluationReqDTO();
			EvaluationResDTO resDTO1 = new EvaluationResDTO();
			reqDTO1.setQtId(146L);
			reqDTO1.setEvaluationStg("1");
			resDTO1 = evaluationSO.retrieveStageDtlsByQtId(reqDTO1);
			this.getManagedBean().setS1FinEvalList(
					resDTO1.getFinancialEvaluationResults());
			this.getManagedBean().setS1TechEvalList(
					resDTO1.getTechinicalEvaluationResults());
			this.getManagedBean().setManuals1SupplierList(
					resDTO1.getManualSubEvaluations());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

	}

	public void retrives2Details() {
		IQTEvaluationSO evaluationSO;
		try {

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO1 = new EvaluationReqDTO();
			EvaluationResDTO resDTO1 = new EvaluationResDTO();
			reqDTO1.setQtId(146L);
			reqDTO1.setEvaluationStg("2");
			resDTO1 = evaluationSO.retrieveStageDtlsByQtId(reqDTO1);

			this.getManagedBean().setS2FinSupplier(
					resDTO1.getFinancialEvaluationResults());
			this.getManagedBean().setS2TechSupplier(
					resDTO1.getTechinicalEvaluationResults());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

	}

	public void retrivesummary() {

		IQTEvaluationSO evaluationSO;
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationResDTO resDTO = new EvaluationResDTO();
			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			reqDTO.setQtId(146L);
			reqDTO.setCommType("QC");
			resDTO = evaluationSO
					.retrieveEvaluationPreparationDtlsByQtId(reqDTO);
			ScQt scQt = resDTO.getScQt();
			this.getManagedBean().setScqt(scQt);

			// List<ScQtZoneCriteria> list =
			// scQt.getScQtCriterias().getScQtZoneCriterias();
			this.getManagedBean().setLocationList(
					scQt.getScQtCriterias().getScQtZoneCriterias());
			this.getManagedBean().setScQtItemList(
					resDTO.getItemResultBean().getResultList());
			this.getManagedBean().setQualifiedList(
					resDTO.getQualifiedSuppResultBean().getResultList());
			this.getManagedBean().setDisQualifiedList(
					resDTO.getDisqualifiedSuppResultBean().getResultList());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			evaluationSO = null;
		}
	}

	public void retriveSignOff() {

		IQTEvaluationSO evaluationSO;
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			EvaluationResDTO resDTO = new EvaluationResDTO();

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			reqDTO.setQtId(146L);
			reqDTO.setCommType("QC");
			resDTO = evaluationSO
					.retrieveEvaluationPreparationDtlsByQtId(reqDTO);
			this.getManagedBean().setSignOffMemberList(
					resDTO.getCommitteSignOffMembers());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			evaluationSO = null;
		}

	}

	public void onRowSelect(SelectEvent selectEvent) {
		try {

			ScQtEvaluationResult result = (ScQtEvaluationResult) selectEvent
					.getObject();

			IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);
			System.out.println(so);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setScQtProposal(result.getScQtProposal());
			reqDTO.setEvaluationStg("2");
			List<String> docType = new ArrayList<String>();
			docType.add("1");
			docType.add("2");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = so.retrieveEvaluationByProposal(reqDTO);
			this.getManagedBean().getChecklistEvaluation().getScQtProposal()
					.setProposalNo(result.getScQtProposal().getProposalNo());
			this.getManagedBean().setS2TechChecklist(
					dto.getEvaluationCheckList());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onFinRowSelect(SelectEvent selectEvent) {

		try {
			ScQtEvaluationResult result = (ScQtEvaluationResult) selectEvent
					.getObject();

			IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);
			System.out.println(so);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setScQtProposal(result.getScQtProposal());
			reqDTO.setEvaluationStg("2");
			List<String> docType = new ArrayList<String>();
			docType.add("3");
			docType.add("4");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = so.retrieveEvaluationByProposal(reqDTO);
			this.getManagedBean().getChecklistEvaluation().getScQtProposal()
					.setProposalNo(result.getScQtProposal().getProposalNo());
			this.getManagedBean().setS2FinChecklist(
					dto.getEvaluationCheckList());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void complianceSaveAction() {

		try {
			List<ScQtChecklistEvaluation> complianceList = this
					.getManagedBean().getComplianceList();
			if (null != complianceList) {
				IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("ss", IQTEvaluationSO.class);
				System.out.println(so);
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setEvaluationCheckList(complianceList);
				EvaluationResDTO resdto = so.saveCheckListEvaluationDtl(reqDTO);
				System.out
						.println("====compliance successfully saved in db===");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Evaluation Compliance SuccessFully Saved",
								"Evaluation"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void manualSaveAction() {
		System.out.println("======enter complieance save action method===");
		try {
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
					"ss", IQTSpecSO.class);
			SpecificationReqDTO specreqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = null;
			List<ScQtManualEvaluation> manualList = this.getManagedBean()
					.getManualEvaluateList();
			List<ScQtManualEvaluation> localmanualList = new ArrayList<ScQtManualEvaluation>();
			if (null != manualList) {
				for (ScQtManualEvaluation checklistEvaluation : manualList) {

					ScQtProposal proposal = new ScQtProposal();
					System.out.println("=======proposal id===="
							+ this.getManagedBean().getProposalId());

					proposal.setQtProposalId(this.getManagedBean()
							.getProposalId());
					ScQtProposalLabel label = new ScQtProposalLabel();
					label.setProposalLabelId(new Long(checklistEvaluation
							.getProposalLabel().getLabelNumber()));
					specreqDTO.setQtId(146L);
					resDTO = specSO.retrieveSpecDetails(specreqDTO);
					System.out.println("====resDTO spec id==="
							+ resDTO.getScQtSpec().getQtSpecId());
					checklistEvaluation.setScQtProposal(proposal);
					checklistEvaluation.setProposalLabel(label);
					checklistEvaluation.setScQtSpec(resDTO.getScQtSpec());
					localmanualList.add(checklistEvaluation);
				}

				IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("ss", IQTEvaluationSO.class);
				System.out.println(so);
				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setManualEvaluations(localmanualList);
				EvaluationResDTO resdto = so
						.saveOrUpdateManualEvaluationDtl(reqDTO);
				/*
				 * FacesContext.getCurrentInstance().addMessage( null, new
				 * FacesMessage(FacesMessage.SEVERITY_INFO,
				 * "Evaluation Manual SuccessFully Saved", "Evaluation"));
				 */
				System.out
						.println("====manual list successfully saved in db===");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downLoadAction() throws IOException {

		List<File> files = new ArrayList<File>();
		files.add(new File("C:\\upload\\clone.jpg"));
		files.add(new File("C:\\upload\\scan.docx"));
		files.add(new File("C:\\upload\\console.txt"));

		PortletResponse response1 = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil
				.getHttpServletResponse(response1);
		response.setContentType("Content-type: text/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=mytest.zip");

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
				System.out.println("======data===" + fif.read());
				zos.write(data);
			}
			fif.close();
			zos.closeEntry();
			System.out.println("Finished adding file " + file.getName());
		}
		zos.close();
		response.flushBuffer();

	}

	public void evaluationSearchAction() {
		checkListEvaluationtDataModel.reloadData();
	}

	public void evaluationResultSearchAction() {
		evaluationResultDataModel.reloadData();
	}

	public void setTabValue(int currentTab, int outputTab) {
		try {

			this.currentTab = currentTab;
			this.outputTab = outputTab;
			if (outputTab == 1) {
				retrives1Details();
			}
			if (outputTab == 2) {
				TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
				templateSpecBackingBean.getManagedBean()
						.setSelectedSpecQuestion(new TplSpecQuestion());
				retrives2Details();
			}
			if (outputTab == 3) {
				this.getManagedBean().setScqt(new ScQt());
				retrivesummary();

			}
			if (outputTab == 4) {
				this.getManagedBean().setScqt(new ScQt());
				retrivesummary();

			}
			if (outputTab == 5) {

			}
			if (outputTab == 6) {
				retriveSignOff();
			}
			if (outputTab == 0) {
				retriveDetails();
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
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject("ss", IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(146L);
			// tenderReqDTO.setQtTndrNo("xxx");
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderResDTO = QtDetailsSO.retriveQtById(tenderReqDTO);

			meeting.setScQt(tenderResDTO.getQtDetail());
			meeting.setMeetingType("4");
			meetingList.add(meeting);

			this.getManagedBean().setScQtMeetings(meetingList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void adds1ManualAction() {
		System.out.println("=======enter method====");
		try {
			List<ScQtManualEvaluation> manuals1List = getManagedBean()
					.getManualEvaluateList();

			if (null == manuals1List) {
				manuals1List = new ArrayList<ScQtManualEvaluation>();
			}

			ScQtManualEvaluation manual = new ScQtManualEvaluation();
			System.out.println("====manual object====");
			manual.setProposalLabel(new ScQtProposalLabel());
			manuals1List.add(manual);
			System.out.println("===exit method===");
			this.getManagedBean().setManualEvaluateList(manuals1List);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void saveAction() {
		try {

			IQTMeetingSO meetingso = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();

			metReqDtO.setCommittyMembers(this.getManagedBean()
					.getCommitteeMemberList());
			metReqDtO.setMeetingDtls(this.getManagedBean().getScQtMeetings());
			meetingso.saveOrUpdateMeetingAndCommitteMemDtls(metReqDtO);
			System.out.println("====meeting details saved successfully====");

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Evaluation Meeting Saved Successfully",
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

	public void submitAction() {
		System.out.println("=======submit Action==");

	}

	public void generateEvaluationAction() {

		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setEvaluationStg("1");
			evaluationSo.generateEvaluationByStage(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}
	}

	public void generateEvaluationActionStage2() {

		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setEvaluationStg("2");
			evaluationSo.generateEvaluationByStage(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}
	}

	public void evaluations1ManualAction(ScQtEvaluationResult results) {
		try {
			IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);
			System.out.println(so);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			ScQtProposal proposal = this.getManagedBean()
					.getChecklistEvaluation().getScQtProposal();
			reqDTO.setScQtProposal(results.getScQtProposal());
			System.out.println("========manual evaluate proposal id===="
					+ results.getScQtProposal().getQtProposalId());
			// System.out.println("========manual evaluate Qt id===="+results.getScQtProposal().getScQtSupplier().getScQt().getQtId());
			// reqDTO.setEvaluationStg("1");
			// List<String> docType = new ArrayList<String>();
			// docType.add("1");
			// docType.add("2");
			// reqDTO.setCheckListType(docType);
			// EvaluationResDTO dto = so.retrieveEvaluationByProposal(reqDTO);
			EvaluationResDTO dto = so.retriveManualEvaluationByProposal(reqDTO);
			System.out.println("===========proposal label list size==="
					+ dto.getProposalLabelResultBean().getResultList().size());
			// getLabelDetails(results);
			this.getManagedBean().setManualProposalLabel(
					dto.getProposalLabelResultBean().getResultList());
			/*
			 * this.getManagedBean().setManualEvaluateList(dto.get)
			 * dto.getProposalLabelResultBean().getResultList());
			 */
			this.getManagedBean().setProposalId(
					results.getScQtProposal().getQtProposalId());
			this.getManagedBean().getChecklistEvaluation().getScQtProposal()
					.setSupplierNo(results.getScQtProposal().getSupplierNo());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	/*
	 * public List<SelectItem> getLabelDetails(ScQtEvaluationResult results) {
	 * List<SelectItem> selectItems = new ArrayList<SelectItem>();
	 * 
	 * try { IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
	 * .getServiceObject("ss", IQTEvaluationSO.class); System.out.println(so);
	 * EvaluationReqDTO reqDTO = new EvaluationReqDTO(); ScQtProposal proposal =
	 * this.getManagedBean() .getChecklistEvaluation().getScQtProposal();
	 * reqDTO.setScQtProposal(results.getScQtProposal());
	 * 
	 * 
	 * EvaluationResDTO dto=so.retriveManualEvaluationByProposal(reqDTO);
	 * selectItems.add(new SelectItem(null, "--Select One--")); for (int i = 0;
	 * i < dto.getProposalLabelResultBean().getResultList().size(); i++) {
	 * ScQtProposalLabel label =
	 * (ScQtProposalLabel)dto.getProposalLabelResultBean
	 * ().getResultList().get(i);
	 * 
	 * selectItems.add(new SelectItem(label.getProposalLabelId(),
	 * label.getLabelNumber())); }
	 * this.getManagedBean().setCurrencyListDetails(selectItems); } catch
	 * (Exception e) { e.printStackTrace(); } return
	 * getManagedBean().getCurrencyListDetails(); }
	 */

	public void evaluations1TechAction(ScQtEvaluationResult results) {
		try {
			IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);
			System.out.println(so);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			ScQtProposal proposal = this.getManagedBean()
					.getChecklistEvaluation().getScQtProposal();
			// proposal.setQtProposalId(results.getScQtProposal().getQtProposalId());
			reqDTO.setScQtProposal(results.getScQtProposal());
			reqDTO.setEvaluationStg("1");
			List<String> docType = new ArrayList<String>();
			docType.add("1");
			docType.add("2");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = so.retrieveEvaluationByProposal(reqDTO);
			this.getManagedBean().setComplianceList(
					dto.getEvaluationCheckList());
			this.getManagedBean().getChecklistEvaluation().getScQtProposal()
					.setSupplierNo(results.getScQtProposal().getSupplierNo());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void evaluations1FinAction(ScQtEvaluationResult results) {
		try {
			IQTEvaluationSO so = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", IQTEvaluationSO.class);
			System.out.println(so);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			ScQtProposal proposal = this.getManagedBean()
					.getChecklistEvaluation().getScQtProposal();
			// proposal.setQtProposalId(results.getScQtProposal().getQtProposalId());
			reqDTO.setScQtProposal(results.getScQtProposal());
			reqDTO.setEvaluationStg("1");
			List<String> docType = new ArrayList<String>();
			docType.add("3");
			docType.add("4");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = so.retrieveEvaluationByProposal(reqDTO);
			this.getManagedBean().getChecklistEvaluation().getScQtProposal()
					.setSupplierNo(results.getScQtProposal().getSupplierNo());
			System.out.println("===========s1 fin list====="
					+ dto.getEvaluationCheckList().size());
			this.getManagedBean().setComplianceList(
					dto.getEvaluationCheckList());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void evaluations2TechAction(ScQtChecklistEvaluation evaluation) {
		try {
			ScQtCheckList scQtCheckList = evaluation.getScQtCheckList();

			ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

			templateSpecBackingBean.getManagedBean().setSpecMaster(specMaster);
			templateSpecBackingBean.getManagedBean().setContentType(
					TemplateSpecConst.CONTENT_TYPE_SCORE);
			IRegistrationSO registrationSO = BusinessDelegate.getInstance()
					.getServiceObject("sm", IRegistrationSO.class);
			RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();

			registrationReqDTO.setSupplierId(1L);
			RegistrationResDTO registrationResDTO = registrationSO
					.retrieveSuppOnlineAcc(registrationReqDTO);
			templateSpecBackingBean.getManagedBean().setSupplier(
					registrationResDTO.getSmSupp());
			this.getManagedBean()
					.getChecklistEvaluation()
					.getScQtProposal()
					.setSupplierNo(evaluation.getScQtProposal().getSupplierNo());

			templateSpecBackingBean.retrieveTemplateSpecMaster();
			// return NAV_TO_STAGE2;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void evaluations2FinAction(ScQtChecklistEvaluation evaluation) {
		try {
			ScQtCheckList scQtCheckList = evaluation.getScQtCheckList();

			ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

			templateSpecBackingBean.getManagedBean().setSpecMaster(specMaster);
			templateSpecBackingBean.getManagedBean().setContentType(
					TemplateSpecConst.CONTENT_TYPE_SCORE);
			IRegistrationSO registrationSO = BusinessDelegate.getInstance()
					.getServiceObject("sm", IRegistrationSO.class);
			RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();

			registrationReqDTO.setSupplierId(1L);
			RegistrationResDTO registrationResDTO = registrationSO
					.retrieveSuppOnlineAcc(registrationReqDTO);
			templateSpecBackingBean.getManagedBean().setSupplier(
					registrationResDTO.getSmSupp());
			this.getManagedBean()
					.getChecklistEvaluation()
					.getScQtProposal()
					.setSupplierNo(evaluation.getScQtProposal().getSupplierNo());

			templateSpecBackingBean.retrieveTemplateSpecMaster();
			// return NAV_TO_STAGE2;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

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

	/**
	 * @return the checkListEvaluationtDataModel
	 */
	public IPagingDataModel<ScQtChecklistEvaluation> getCheckListEvaluationtDataModel() {
		return checkListEvaluationtDataModel;
	}

	/**
	 * @param checkListEvaluationtDataModel
	 *            the checkListEvaluationtDataModel to set
	 */
	public void setCheckListEvaluationtDataModel(
			IPagingDataModel<ScQtChecklistEvaluation> checkListEvaluationtDataModel) {
		this.checkListEvaluationtDataModel = checkListEvaluationtDataModel;
	}

	private class CheckListEvaluationPagingDataModel extends
			PagingDataModel<ScQtChecklistEvaluation> {
		private static final long serialVersionUID = 1L;

		public CheckListEvaluationPagingDataModel() {
			super("searchDocument");
		}

		@Override
		public ISearchResultBean<ScQtChecklistEvaluation> pageChangeListener(
				ISearchBean<ScQtChecklistEvaluation> searchBean) {

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
				searchBean = new SearchBean<ScQtChecklistEvaluation>();
				QTTemplateDocResDTO resDto = null;
				// qtTemplateReqDTO.setDocSearchBean(searchBean);
				IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(QTConstants.SS_MODULE,
								IQTTemplateSO.class);

				resDto = qtTemplateSO.fetchTemplates(qtTemplateReqDTO);
				System.out.println("========scQTDOC List size===="
						+ resDto.getSearchResultBean().getResultList().size());

				if (resDto.isSuccessful()) {
					// return resDto.getSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class EvaluationResultPagingDataModel extends
			PagingDataModel<ScEvaluationResult> {
		private static final long serialVersionUID = 1L;

		public EvaluationResultPagingDataModel() {
			super("searchDocument");
		}

		@Override
		public ISearchResultBean<ScEvaluationResult> pageChangeListener(
				ISearchBean<ScEvaluationResult> searchBean) {

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
				searchBean = new SearchBean<ScEvaluationResult>();
				QTTemplateDocResDTO resDto = null;
				// qtTemplateReqDTO.setDocSearchBean(searchBean);
				IQTTemplateSO qtTemplateSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(QTConstants.SS_MODULE,
								IQTTemplateSO.class);
				resDto = qtTemplateSO.fetchTemplates(qtTemplateReqDTO);
				System.out.println("========scQTDOC List size===="
						+ resDto.getSearchResultBean().getResultList().size());
				if (resDto.isSuccessful()) {
					// return resDto.getSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * @return the evaluationResultDataModel
	 */
	public IPagingDataModel<ScEvaluationResult> getEvaluationResultDataModel() {
		return evaluationResultDataModel;
	}

	/**
	 * @param evaluationResultDataModel
	 *            the evaluationResultDataModel to set
	 */
	public void setEvaluationResultDataModel(
			IPagingDataModel<ScEvaluationResult> evaluationResultDataModel) {
		this.evaluationResultDataModel = evaluationResultDataModel;
	}
}
