package my.ep.web.backingbean.ss.evaluation;

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
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTEvaluationSO;
import my.ep.biz.service.ss.quationtender.IQTMeetingSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.ss.quationtender.EvaluationReqDTO;
import my.ep.dto.ss.quationtender.EvaluationResDTO;
import my.ep.dto.ss.quationtender.MeetingReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.ss.quationtender.ScQtManualEvaluation;
import my.ep.persistence.entity.ss.quationtender.ScQtMeeting;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalLabel;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.managedbean.ss.evaluation.EvaluationManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
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
						.getServiceObject(QTConstants.SS_MODULE,
								IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(146L);
				QuationTenderResDTO tenderResDTO = detailSO
						.retriveQTDetails(tenderReqDTO);
				QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
				detailBackingBean.getManagedBean().setQt(
						tenderResDTO.getQtDetail());
				detailBackingBean.populateParentOrgProfile(tenderResDTO
						.getQtDetail());

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
			reqDTO1.setEvaluationStg("1");
			resDTO1 = evaluationSO.retrieveEvaluationDtlsByStage(reqDTO1);
			this.getManagedBean().setS1FinEvalList(
					resDTO1.getFinancialEvaluationResults().getResultList());
			this.getManagedBean().setS1TechEvalList(
					resDTO1.getTechinicalEvaluationResults().getResultList());
			this.getManagedBean().setManuals1SupplierList(
					resDTO1.getManualSubEvaluations().getResultList());
			this.getManagedBean().setManualList(
					resDTO1.getManualSubEvaluations().getResultList().size());
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

			reqDTO1.setEvaluationStg("2");
			resDTO1 = evaluationSO.retrieveEvaluationDtlsByStage(reqDTO1);
			this.getManagedBean().setS2FinSupplier(
					resDTO1.getFinancialEvaluationResults().getResultList());
			this.getManagedBean().setS2TechSupplier(
					resDTO1.getTechinicalEvaluationResults().getResultList());

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
			EvaluationReqDTO rankingReqDTO = new EvaluationReqDTO();
			EvaluationReqDTO supplierReqDTO = new EvaluationReqDTO();

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			/*
			 * supplierReqDTO.setQtId(146L); supplierReqDTO.setSpecType("F");
			 * evaluationSO.updateSupplierQulifiedStatus(supplierReqDTO);
			 */

			rankingReqDTO.setQtId(146L);
			rankingReqDTO.setEvaluationStg("2");
			EvaluationResDTO rankingResDTO = evaluationSO
					.generateSupplierRanking(rankingReqDTO);

			reqDTO.setQtId(146L);
			reqDTO.setEvaluationStg("2");
			reqDTO.setResponseRule("I");
			EvaluationResDTO resDTO = evaluationSO
					.retrieveStage2SummaryDtls(reqDTO);

			this.getManagedBean().setLocationList(resDTO.getZones());

			this.getManagedBean().setScQtItemList(resDTO.getItems());
			this.getManagedBean().setQualifiedList(
					resDTO.getQualifiedSuppResultBean().getResultList());
			this.getManagedBean().setDisQualifiedList(
					resDTO.getDisqualifiedSuppResultBean().getResultList());
			System.out.println("====qualified list===="
					+ resDTO.getQualifiedSuppResultBean().getResultList()
							.size());
			System.out.println("====DisQualified list===="
					+ resDTO.getDisqualifiedSuppResultBean().getResultList()
							.size());
			/*
			 * ScQtZoneCriteria zone=new ScQtZoneCriteria(); ScQtItem item=new
			 * ScQtItem(); zone.setQtZoneCriteriaId(1L); item.setQtItemId(1L);
			 * reqDTO.setScQtItem(item); reqDTO.setQtZoneCriteria(zone);
			 * EvaluationResDTO SupplierresDTO=
			 * evaluationSO.retrieveSuppliersByZoneAndItem(reqDTO);
			 * this.getManagedBean().setQualifiedList(
			 * SupplierresDTO.getQualifiedSuppResultBean().getResultList());
			 * this.getManagedBean().setDisQualifiedList(
			 * SupplierresDTO.getDisqualifiedSuppResultBean().getResultList());
			 * System.out.println("====qualified list===="+SupplierresDTO.
			 * getQualifiedSuppResultBean().getResultList().size());
			 * System.out.println("====DisQualified list===="+
			 * SupplierresDTO.getDisqualifiedSuppResultBean
			 * ().getResultList().size());
			 */

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			evaluationSO = null;
		}
	}

	public void retriveStageOneSummary() {

		IQTEvaluationSO evaluationSO;
		try {

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			reqDTO.setQtId(146L);
			reqDTO.setEvaluationStg("1");
			EvaluationResDTO resDTO = evaluationSO
					.generateStageOneEvaluationResults(reqDTO);
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
		IQTEvaluationSO evaluationSo;
		try {
			List<String> docType = new ArrayList<String>();
			ScQtEvaluationResult result = (ScQtEvaluationResult) selectEvent
					.getObject();

			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setEvaluationResultId(result.getEvaluationResultId());
			reqDTO.setEvaluationStg("2");
			docType.add("1");
			docType.add("2");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = evaluationSo
					.retrieveEvaluationByEvaluationResultId(reqDTO);
			this.getManagedBean().setScQtEvaluationResult(result);
			this.getManagedBean().setS2TechChecklist(
					dto.getEvaluationCheckList());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onFinRowSelect(SelectEvent selectEvent) {
		IQTEvaluationSO evaluationSO;
		try {
			List<String> docType = new ArrayList<String>();
			ScQtEvaluationResult result = (ScQtEvaluationResult) selectEvent
					.getObject();

			evaluationSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setEvaluationResultId(result.getEvaluationResultId());
			reqDTO.setEvaluationStg("2");
			docType.add("3");
			docType.add("4");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = evaluationSO
					.retrieveEvaluationByEvaluationResultId(reqDTO);
			this.getManagedBean().setScQtEvaluationResult(result);
			this.getManagedBean().setS2FinChecklist(
					dto.getEvaluationCheckList());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

	}

	public void complianceSaveAction() {
		IQTEvaluationSO evaluationSo;
		try {
			List<ScQtProposalChecklist> complianceList = this.getManagedBean()
					.getComplianceList();
			if (null != complianceList) {
				evaluationSo = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(QTConstants.SS_MODULE,
								IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setEvaluationCheckList(complianceList);
				reqDTO.setScQtEvaluationResult(this.getManagedBean()
						.getScQtEvaluationResult());
				EvaluationResDTO resdto = evaluationSo
						.saveCheckListEvaluationDtl(reqDTO);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Evaluation Compliance SuccessFully Saved",
								"Evaluation"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}
	}

	public void stageTwoSaveAction() {
		IQTEvaluationSO evaluationSo;
		try {

			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();

			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			// this.getManagedBean().getSpecMaster().setTplSpecQuestions(specBB.getManagedBean().getSpecMaster().getTplSpecQuestions());
			reqDTO.setSpecQuestions(specBB.getManagedBean().getSpecMaster()
					.getTplSpecQuestions());
			reqDTO.setEvaluationResultDtls(this.getManagedBean()
					.getScQtEvaluationResult().getEvaluationResultDtls());
			evaluationSo.saveOrUpdateEvaluationDtlAndSpecAnswer(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}
	}

	public void manualShowResult() {
		IQTEvaluationSO evaluationSo;
		try {
			String overAllResult = "Success";
			List<ScQtManualEvaluation> manualList = this.getManagedBean()
					.getManualEvaluateList();
			List<ScQtManualEvaluation> localmanualList = new ArrayList<ScQtManualEvaluation>();
			if (null != manualList && manualList.size() > 0) {
				for (ScQtManualEvaluation checklistEvaluation : manualList) {
					if (!"M".equalsIgnoreCase(checklistEvaluation
							.getUserScore())) {
						overAllResult = "Failed";
					}
				}
				this.getManagedBean().setOverAllResult(overAllResult);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void stageOneShowResult() {
		IQTEvaluationSO evaluationSo;
		try {
			String overAllResult = "Success";
			List<ScQtProposalChecklist> complianceList = this.getManagedBean()
					.getComplianceList();
			if (null != complianceList && complianceList.size() > 0) {
				for (ScQtProposalChecklist checklist : complianceList) {
					if (!"M".equalsIgnoreCase(checklist.getUserScore())) {
						overAllResult = "Failed";
					}
				}
				this.getManagedBean().setStageOneoverAllResult(overAllResult);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void manualSaveAction() {
		IQTSpecSO specSO;
		IQTEvaluationSO evaluationSo;
		try {
			specSO = BusinessDelegate.getInstance().getServiceObject(
					QTConstants.SS_MODULE, IQTSpecSO.class);
			SpecificationReqDTO specreqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = null;
			List<ScQtManualEvaluation> manualList = this.getManagedBean()
					.getManualEvaluateList();
			List<ScQtManualEvaluation> localmanualList = new ArrayList<ScQtManualEvaluation>();
			if (null != manualList) {
				for (ScQtManualEvaluation checklistEvaluation : manualList) {
					ScQtEvaluationResult evalResult = this.getManagedBean()
							.getScQtEvaluationResult();
					/*
					 * evalResult.setEvaluationResultId(this.getManagedBean()
					 * .getEvaluationResultId());
					 */
					ScQtProposalLabel label = new ScQtProposalLabel();
					label.setProposalLabelId(new Long(checklistEvaluation
							.getProposalLabel().getProposalLabelId()));
					specreqDTO.setQtId(146L);
					resDTO = specSO.retrieveSpecDetails(specreqDTO);
					checklistEvaluation.setEvaluationResult(evalResult);
					checklistEvaluation.setProposalLabel(label);
					checklistEvaluation.setScQtSpec(resDTO.getScQtSpec());
					localmanualList.add(checklistEvaluation);
				}

				evaluationSo = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(QTConstants.SS_MODULE,
								IQTEvaluationSO.class);

				EvaluationReqDTO reqDTO = new EvaluationReqDTO();
				reqDTO.setManualEvaluations(localmanualList);
				EvaluationResDTO resdto = evaluationSo
						.saveOrUpdateManualEvaluationDtl(reqDTO);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			specSO = null;
			evaluationSo = null;
		}
	}

	public void changedView(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setViewBy(type);
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
				retrives1Details();
			}
			if (outputTab == 2) {
				TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
				templateSpecBackingBean.getManagedBean()
						.setSelectedSpecQuestion(new TplSpecQuestion());
				retrives2Details();
			}
			if (outputTab == 3) {
				// this.getManagedBean().setScqt(new ScQt());
				retriveStageOneSummary();

			}
			if (outputTab == 4) {
				// this.getManagedBean().setScqt(new ScQt());
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
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
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

		try {
			List<ScQtManualEvaluation> manuals1List = getManagedBean()
					.getManualEvaluateList();

			if (null == manuals1List) {
				manuals1List = new ArrayList<ScQtManualEvaluation>();
			}

			ScQtManualEvaluation manual = new ScQtManualEvaluation();

			manual.setProposalLabel(new ScQtProposalLabel());
			manuals1List.add(manual);

			this.getManagedBean().setManualEvaluateList(manuals1List);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void saveAction() {
		try {

			IQTMeetingSO meetingso = EpEjbBusinessDelegate
					.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, IQTMeetingSO.class);

			MeetingReqDTO metReqDtO = new MeetingReqDTO();

			metReqDtO.setCommittyMembers(this.getManagedBean()
					.getCommitteeMemberList());
			metReqDtO.setMeetingDtls(this.getManagedBean().getScQtMeetings());
			meetingso.saveOrUpdateMeetingAndCommitteMemDtls(metReqDtO);

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

	public void generateStageOneEvaluationAction() {

		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

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

	public void generateStageTwoEvaluationAction() {

		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setQtId(146L);
			reqDTO.setEvaluationStg("2");
			reqDTO.setCommType("EC");
			evaluationSo.generateEvaluationByStage(reqDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}
	}

	public void evaluations1ManualAction(ScQtEvaluationResult results) {
		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			/*
			 * ScQtProposal proposal = this.getManagedBean()
			 * .getScQtEvaluationResult().getScQtProposal();
			 */

			reqDTO.setEvaluationResultId(results.getEvaluationResultId());
			EvaluationResDTO manualdto = evaluationSo
					.retrieveManualEvaluationByEvaluationResultId(reqDTO);
			EvaluationReqDTO labelreqDTO = new EvaluationReqDTO();
			labelreqDTO.setScQtProposal(results.getScQtProposal());
			EvaluationResDTO labelResDTO = evaluationSo
					.retrieveProposalLablesByProposalId(labelreqDTO);
			this.getManagedBean().setManualProposalLabel(
					labelResDTO.getProposalLabelResultBean().getResultList());
			this.getManagedBean().setManualEvaluateList(
					manualdto.getManualEvaluatons());
			/*
			 * this.getManagedBean().setEvaluationResultId(
			 * results.getEvaluationResultId());
			 */

			this.getManagedBean().setScQtEvaluationResult(results);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSo = null;
		}

	}

	public void evaluations1TechAction(ScQtEvaluationResult results) {
		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setEvaluationResultId(results.getEvaluationResultId());
			reqDTO.setEvaluationStg("1");
			List<String> docType = new ArrayList<String>();
			docType.add("1");
			docType.add("2");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = evaluationSo
					.retrieveEvaluationByEvaluationResultId(reqDTO);
			this.getManagedBean().setComplianceList(
					dto.getEvaluationCheckList());
			this.getManagedBean().setScQtEvaluationResult(results);
			/*
			 * this.getManagedBean().setEvaluationResultId(
			 * results.getEvaluationResultId());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void evaluations1FinAction(ScQtEvaluationResult results) {
		IQTEvaluationSO evaluationSo;
		try {
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);

			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setEvaluationResultId(results.getEvaluationResultId());
			reqDTO.setEvaluationStg("1");
			List<String> docType = new ArrayList<String>();
			docType.add("3");
			docType.add("4");
			reqDTO.setCheckListType(docType);
			EvaluationResDTO dto = evaluationSo
					.retrieveEvaluationByEvaluationResultId(reqDTO);
			this.getManagedBean().setScQtEvaluationResult(results);

			this.getManagedBean().setComplianceList(
					dto.getEvaluationCheckList());
			/*
			 * this.getManagedBean().setEvaluationResultId(
			 * results.getEvaluationResultId());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void evaluations2TechAction(ScQtProposalChecklist evaluation) {
		IQTEvaluationSO evaluationSo;
		IRegistrationSO registrationSO;
		try {
			ScQtCheckList scQtCheckList = evaluation.getScQtCheckList();

			ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
			this.getManagedBean().setSpecMaster(specMaster);
			templateSpecBackingBean.getManagedBean().setSpecMaster(specMaster);
			templateSpecBackingBean.getManagedBean().setContentType(
					TemplateSpecConst.CONTENT_TYPE_SCORE);
			registrationSO = BusinessDelegate.getInstance().getServiceObject(
					"sm", IRegistrationSO.class);
			RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
			// evaluation.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId()
			registrationReqDTO.setSupplierId(1L);
			RegistrationResDTO registrationResDTO = registrationSO
					.retrieveSuppOnlineAcc(registrationReqDTO);
			templateSpecBackingBean.getManagedBean().setSupplier(
					registrationResDTO.getSmSupp());

			templateSpecBackingBean.retrieveTemplateSpecMaster();

			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setProposalChecklist(evaluation);
			EvaluationResDTO resDTO = evaluationSo.evaluateCheckLists(reqDTO);
			this.getManagedBean().getScQtEvaluationResult()
					.setEvaluationResultDtls(resDTO.getEvaluationResultDtl());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			registrationSO = null;
			evaluationSo = null;
		}

	}

	public void evaluations2FinAction(ScQtProposalChecklist evaluation) {
		IQTEvaluationSO evaluationSo;
		IRegistrationSO registrationSO;
		try {
			ScQtCheckList scQtCheckList = evaluation.getScQtCheckList();

			ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

			templateSpecBackingBean.getManagedBean().setSpecMaster(specMaster);
			templateSpecBackingBean.getManagedBean().setContentType(
					TemplateSpecConst.CONTENT_TYPE_SCORE);
			registrationSO = BusinessDelegate.getInstance().getServiceObject(
					"sm", IRegistrationSO.class);
			RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
			// evaluation.getScQtProposal().getScQtSupplier().getSmSupplier().getSupplierId()
			registrationReqDTO.setSupplierId(1L);
			RegistrationResDTO registrationResDTO = registrationSO
					.retrieveSuppOnlineAcc(registrationReqDTO);
			templateSpecBackingBean.getManagedBean().setSupplier(
					registrationResDTO.getSmSupp());
			templateSpecBackingBean.retrieveTemplateSpecMaster();
			evaluationSo = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE,
							IQTEvaluationSO.class);
			EvaluationReqDTO reqDTO = new EvaluationReqDTO();
			reqDTO.setProposalChecklist(evaluation);
			EvaluationResDTO resDTO = evaluationSo.evaluateCheckLists(reqDTO);
			this.getManagedBean().getScQtEvaluationResult()
					.setEvaluationResultDtls(resDTO.getEvaluationResultDtl());
			// return NAV_TO_STAGE2;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			registrationSO = null;
			evaluationSo = null;
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

	public void deleteManualSubmission() {
		try {
			List<ScQtManualEvaluation> manualList = getManagedBean()
					.getManualEvaluateList();
			List<ScQtManualEvaluation> selectManual = getManagedBean()
					.getManualMultiSelect();
			if (manualList.size() == selectManual.size()) {
				manualList.clear();
			} else {
				for (ScQtManualEvaluation manualEvaluation : selectManual)
					manualList.remove(manualEvaluation);
			}
		} catch (Exception e) {

		}
	}

	public void onManualRowSelect(SelectEvent selectEvent) {

		try {

			ScQtManualEvaluation score = (ScQtManualEvaluation) selectEvent
					.getObject();
			List<ScQtManualEvaluation> selectManual = getManagedBean()
					.getManualMultiSelect();
			if (selectManual == null) {
				selectManual = new ArrayList<ScQtManualEvaluation>();
			}
			selectManual.add(score);
			this.getManagedBean().setManualMultiSelect(selectManual);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void onManualRowUnSelect(UnselectEvent selectEvent) {

		try {

			ScQtManualEvaluation score = (ScQtManualEvaluation) selectEvent
					.getObject();
			List<ScQtManualEvaluation> selectManual = getManagedBean()
					.getManualMultiSelect();
			if (selectManual == null) {
				selectManual = new ArrayList<ScQtManualEvaluation>();
			}
			selectManual.add(score);
			this.getManagedBean().setManualMultiSelect(selectManual);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void handleFileUpload(FileUploadEvent event) {

		System.out.println("=======uploaded file name===="
				+ event.getFile().getFileName());
		this.getManagedBean().setFileName(event.getFile().getFileName());
		System.out.println("=========get file name======="
				+ this.getManagedBean().getFileName());
		/*
		 * ScQtCheckList scQtChecklist =
		 * this.getManagedBean().getScQtChecklist();
		 * 
		 * FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
		 * "The file was uploaded successfully.", null);
		 * FacesContext.getCurrentInstance().addMessage(null, message);
		 * 
		 * ScQtCheckListAttachment attachment = scQtChecklist
		 * .getScQtCheckListAttachment(); if (null == attachment) { attachment =
		 * new ScQtCheckListAttachment();
		 * attachment.setScQtCheckList(scQtChecklist); }
		 * uploadAttachment(event.getFile(), attachment);
		 * 
		 * scQtChecklist.setScQtCheckListAttachment(attachment);
		 */
	}

	public void removeFile() {
		this.getManagedBean().setFileName(new String());
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
