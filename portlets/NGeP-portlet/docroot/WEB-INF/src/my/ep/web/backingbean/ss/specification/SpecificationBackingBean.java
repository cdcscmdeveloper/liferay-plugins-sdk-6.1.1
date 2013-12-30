/**
 * 
 */
package my.ep.web.backingbean.ss.specification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.currency.ICurrencySO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.ss.quationtender.IQTBriefingSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.biz.service.ss.specification.ISpecificationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.BaseResponseDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.currency.CurrencyReqDTO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.dto.ss.quationtender.BriefingReqDTO;
import my.ep.dto.ss.quationtender.BriefingResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.specificationdata.SCSpecificationDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.ss.specification.SpecificationManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationBackingBean")
@RequestScoped
public class SpecificationBackingBean extends
		SourcingBaseBackingBean<SpecificationManagedBean> {
	private int count;

	private List<SpecificationCompilanceBean> selectedCompilenceList;
	private ScQtCheckList[] selectedst1TechAddProposal;
	private String selctValue;

	private ScQtCheckList[] selectedst1FinanceProposal;
	private ScQtCheckList[] selectedst1FinanceAddProposal;
	private ScQtCheckList[] selectedst2TechProposal;
	private ScQtCheckList[] selectedst2TechAddProposal;
	private ScQtCheckList[] selectedst2FinanceProposal;
	private ScQtCheckList[] selectedst2FinanceAddProposal;
	private ScQtDeduction[] selectedDeduction;
	private ScQtCheckList[] selectedst1TechProposal;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public SpecificationBackingBean() {

		// retriveSpecdetails();
	}
	public void saveSpecificationDtls(){
		try{
		SpecificationReqDTO reqDTO=prepareSpecReq();
		 IQTSpecSO specSO =
				  BusinessDelegate.getInstance().getServiceObject( "ss",
				 IQTSpecSO.class); System.out.println("=====specSO===" + specSO);
				 specSO.saveQTDtl(reqDTO);
				 System.out.println("=========save data successfully ===");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public SpecificationReqDTO prepareSpecReq() {
		SpecificationReqDTO specreqDTO = new SpecificationReqDTO();

		ScQtSpec scqtSpec = this.getManagedBean().getScQtSpec();
		scqtSpec.setApprovalDate(new Date());
		// scqtSpec.setAwardType("L");
		scqtSpec.setContractDuration(1);
		scqtSpec.getScQt().setQtAmount(
				this.getManagedBean().getScQtSpec().getScQt().getQtAmount());

		List<ScQtCheckList> st1tech = this.getManagedBean()
				.getScQtCheckSt1techProposal();

		List<ScQtCheckList> collectionAll = new ArrayList<ScQtCheckList>();
		if (st1tech != null) {
			collectionAll.addAll(st1tech);
		}

		List<ScQtCheckList> st1TechAdd = this.getManagedBean()
				.getScQtCheckSt1techAdditionalProposalList();

		if (st1TechAdd != null) {
			collectionAll.addAll(st1TechAdd);
		}

		List<ScQtCheckList> st1Fin = this.getManagedBean()
				.getScQtCheckSt1financeProposalList();

		if (st1Fin != null) {
			collectionAll.addAll(st1Fin);
		}
		List<ScQtCheckList> st1FinAdd = this.getManagedBean()
				.getScQtCheckSt1financeAdditionalProposalList();

		if (st1FinAdd != null) {
			collectionAll.addAll(st1FinAdd);
		}
		List<ScQtCheckList> st2Tech = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (st2Tech != null) {
			collectionAll.addAll(st2Tech);
		}
		List<ScQtCheckList> st2TechAdd = this.getManagedBean()
				.getScQtCheckSt2techAdditionalProposalList();

		if (st2TechAdd != null) {
			collectionAll.addAll(st2TechAdd);
		}
		List<ScQtCheckList> st2Fin = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();

		if (st2Fin != null) {
			collectionAll.addAll(st2Fin);
		}
		List<ScQtCheckList> st2FinAdd = this.getManagedBean()
				.getScQtCheckSt2financeAdditionalProposalList();

		if (st2FinAdd != null) {
			collectionAll.addAll(st2FinAdd);
		}

		scqtSpec.setScQtCheckLists(collectionAll);
		System.out.println("scQtSpec Object====="
				+ scqtSpec.getScQtCheckLists().size());

		/*
		 * IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
		 * "ss", IQTSpecSO.class); System.out.println("=====specSO===" +
		 * specSO);
		 */

		specreqDTO.setScQtSpec(scqtSpec);
		// specSO.saveSpecDetails(specreqDTO);

		return specreqDTO;

	}

	@PostConstruct
	public void init() {
		try {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.getManagedBean().setApprovalDate(sdf.format(d));
			compilenceList();
			if (getManagedBean().getScQtSpec() == null) {
				// retriveSpecdetails(146L);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void retriveSpecdetails(Long qtId) {
		try {
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
					"ss", IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = null;
			reqDTO.setQtId(qtId);
			resDTO = specSO.retrieveSpecDetails(reqDTO);
			if (resDTO.getScQtSpec() == null) {

				getManagedBean().setScQtSpec(new ScQtSpec());
				// getManagedBean().getScQtSpec().setAwardType("L");
				getManagedBean().getScQtSpec().setPmCurrency(new PmCurrency());
				getManagedBean().getScQtSpec().setPmProcurementCategory(
						new PmProcurementCategory());
				IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
						.getServiceObject("ss", IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(qtId);
				// tenderReqDTO.setQtTndrNo("xxx");
				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
				tenderResDTO = QtDetailsSO.retriveQtById(tenderReqDTO);
				getManagedBean().getScQtSpec().setScQt(
						tenderResDTO.getQtDetail());
				getManagedBean().setScQtRemarks(new ScQtRemark());
				// getManagedBean().setScQtChecklist(new ScQtCheckList());
				getManagedBean().getScQtSpec().setPmStatus(new PmStatus());

			} else {
				System.out.println("=====S1TecResult===="
						+ resDTO.getS1TecResultBean().getResultList().size());
				System.out.println("=====CommMemberResult===="
						+ resDTO.getCommMemberResultBean().getResultList()
								.size());
				getManagedBean().setScQtSpec(resDTO.getScQtSpec());
				getManagedBean().setScQtCheckSt1techProposal(
						resDTO.getS1TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1techAdditionalProposalList(
						resDTO.getS1AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeProposalList(
						resDTO.getS1FinResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
						resDTO.getS1AddFinResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getS2TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techAdditionalProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
						resDTO.getS2AddFinResultBean().getResultList());
				getManagedBean().setCommitteeMemberList(
						resDTO.getCommMemberResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void specificAdd() {
		System.out.println("====enter specification details add method====");
		try {
			List<ScQtDeduction> deductionList = getManagedBean()
					.getScQtDeductionList();
			if (null == deductionList) {

				deductionList = new ArrayList<ScQtDeduction>();

			}
			ScQtDeduction deduction = new ScQtDeduction();
			deductionList.add(deduction);
			this.getManagedBean().setScQtDeductionList(deductionList);
		} catch (Exception e) {
			System.out.println("===enter catch loop");
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
		// return getManagedBean().getScQtDeductionList();
	}

	public ScQtCheckList createSpecList() {
		ScQtSpec scQtSpec = getManagedBean().getScQtSpec();
		ScQtCheckList additionProposalst1 = new ScQtCheckList();
		additionProposalst1.setScQtSpec(scQtSpec);
		return additionProposalst1;
	}

	public void stageOneAdditionalAdd() {
		System.out.println("====enter stage1 additional add method====");
		try {
			List<ScQtCheckList> st1additionalList = getManagedBean()
					.getScQtCheckSt1techAdditionalProposalList();

			if (null == st1additionalList) {
				st1additionalList = new ArrayList<ScQtCheckList>();
			}

			ScQtCheckList additionProposalst1 = createSpecList();
			additionProposalst1.setDocType("2");
			additionProposalst1.setEvaluationStage("1");
			additionProposalst1.setChecklistType("UP");
			st1additionalList.add(additionProposalst1);
			// mediumCarsModel = new CarDataModel(st1additionalList);
			this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(
					st1additionalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiProposalAdd() {
		System.out.println("====enter stage1 finance proposal add method====");
		try {
			List<ScQtCheckList> st1financeproposalList = getManagedBean()
					.getScQtCheckSt1financeProposalList();
			if (null == st1financeproposalList) {
				st1financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeproposalList1 = createSpecList();
			st1financeproposalList1.setDocType("3");
			st1financeproposalList1.setEvaluationStage("1");
			st1financeproposalList1.setChecklistType("UP");
			st1financeproposalList.add(st1financeproposalList1);
			this.getManagedBean().setScQtCheckSt1financeProposalList(
					st1financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiAdditioalProposalAdd() {
		System.out
				.println("====enter stage1 finannce additional add method====");
		try {
			List<ScQtCheckList> st1financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt1financeAdditionalProposalList();
			if (null == st1financeAdditionalproposalList) {
				st1financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeAdditionalproposalList1 = createSpecList();
			st1financeAdditionalproposalList1.setDocType("4");
			st1financeAdditionalproposalList1.setEvaluationStage("1");
			st1financeAdditionalproposalList1.setChecklistType("UP");
			st1financeAdditionalproposalList
					.add(st1financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
					st1financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoAdditionalAdd() {
		System.out.println("====enter stage2 additional add method====");
		try {
			List<ScQtCheckList> st2additionalList = getManagedBean()
					.getScQtCheckSt2techAdditionalProposalList();
			if (null == st2additionalList) {
				st2additionalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList additionProposalst2 = createSpecList();
			additionProposalst2.setDocType("2");
			additionProposalst2.setEvaluationStage("2");
			additionProposalst2.setChecklistType("UP");
			st2additionalList.add(additionProposalst2);
			this.getManagedBean().setScQtCheckSt2techAdditionalProposalList(
					st2additionalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiProposalAdd() {
		System.out.println("====enter stage2 finance proposal add method====");
		try {
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList();
			st2financeproposalList2.setDocType("3");
			st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList.add(st2financeproposalList2);
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiAdditioalProposalAdd() {
		System.out
				.println("====enter stage2 finannce additional proposal add method====");
		try {
			List<ScQtCheckList> st2financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt2financeAdditionalProposalList();
			if (null == st2financeAdditionalproposalList) {
				st2financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeAdditionalproposalList1 = createSpecList();
			st2financeAdditionalproposalList1.setDocType("4");
			st2financeAdditionalproposalList1.setEvaluationStage("2");
			st2financeAdditionalproposalList1.setChecklistType("UP");
			st2financeAdditionalproposalList
					.add(st2financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
					st2financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneTechProposal() {
		System.out.println("====enter stage1 tech proposal====");
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt1techProposal();
			List<SpecificationCompilanceBean> list = getSelectedCompilenceList();
			List<SpecificationCompilanceBean> list1 = this.getManagedBean()
					.getCompilaenceList();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < list.size(); i++) {
				SpecificationCompilanceBean bean = list.get(i);
				ScQtCheckList scqtlist = createSpecList();

				scqtlist.setTitle(bean.getCompilanceTitle());
				// scqtlist.setQtChecklistId(new Long(i));
				compilance.add(scqtlist);
				scqtlist.setDocType("1");
				scqtlist.setEvaluationStage("1");
				scqtlist.setChecklistType("UP");

				// getManagedBean().setCompilanceList(compilance);

			}
			getManagedBean().setScQtCheckSt1techProposal(compilance);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechProposal() {
		System.out.println("====enter stage2 tech proposal====");
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			ScQtCheckList scqtlist = new ScQtCheckList();

			scqtlist.setTitle("TemplateTitle");
			scqtlist.setChecklistType("Technical/Financial/Others");
			scqtlist.setDocType("1");
			scqtlist.setEvaluationStage("2");
			compilance.add(scqtlist);
			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechnicalAddProposal() {
		System.out.println("====enter stage2 stageTwoTechnicalAddProposal====");
		try {
			List<ScQtCheckList> st2technicalproposalList = getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null == st2technicalproposalList) {
				st2technicalproposalList = new ArrayList<ScQtCheckList>();

			}
			ScQtCheckList st2technicalproposalList1 = createSpecList();
			st2technicalproposalList1.setDocType("1");
			st2technicalproposalList1.setEvaluationStage("2");
			st2technicalproposalList.add(st2technicalproposalList1);
			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwotechproposal(AjaxBehaviorEvent e) {

		try {
			String dropdown = (String) e.getComponent().getAttributes()
					.get("value");
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			setSelctValue(dropdown);
		} catch (Exception e1) {
			throw new IllegalArgumentException();
		}
		// this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}

	public void compilenceList() {

		try {

			List<SpecificationCompilanceBean> compilaenceList = new ArrayList<SpecificationCompilanceBean>();

			compilaenceList
					.add(new SpecificationCompilanceBean(1, "Sijil MOF"));
			compilaenceList.add(new SpecificationCompilanceBean(2, "Sijil 29"));
			compilaenceList.add(new SpecificationCompilanceBean(3, "Sijil 49"));
			compilaenceList
					.add(new SpecificationCompilanceBean(4, "Sijil KDN"));
			compilaenceList.add(new SpecificationCompilanceBean(5,
					"Sijil Bumiputera"));

			this.getManagedBean().setCompilaenceList(compilaenceList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}

	public List<SelectItem> getApproverNames() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		UserReqDTO resDto = null;
		try {
			ScQtZoneCriteria scQtZone;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public List<SelectItem> getProcurementDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ProcurementReqDTO reqDTO = new ProcurementReqDTO();
		ProcurementResDTO resDto = null;
		try {

			IProcurementSO procurementSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IProcurementSO.class);
			System.out.println(procurementSO);
			resDto = procurementSO.retriveProcurementList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getProcurementList().size(); i++) {
				PmProcurementCategory pmProcurement = (PmProcurementCategory) resDto
						.getProcurementList().get(i);
				selectItems.add(new SelectItem(pmProcurement.getCategoryCode(),
						pmProcurement.getCategoryName()));
			}
			this.getManagedBean().setProcurementListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getProcurementListDetails();
	}

	public List<SelectItem> getCurrencyDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		CurrencyReqDTO reqDTO = new CurrencyReqDTO();
		CurrencyResDTO resDto = null;
		try {

			ICurrencySO CurrencySO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICurrencySO.class);
			resDto = CurrencySO.retriveCurrencyList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getCurrencyList().size(); i++) {
				PmCurrency currency = (PmCurrency) resDto.getCurrencyList()
						.get(i);
				selectItems.add(new SelectItem(currency.getCurrencyCode(),
						currency.getCurrencyName()));
			}
			this.getManagedBean().setCurrencyListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getCurrencyListDetails();
	}

	public void handleFileUpload(FileUploadEvent event) {

		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);

		ScQtCheckListAttachment attachment = scQtChecklist
				.getScQtCheckListAttachment();
		if (null == attachment) {
			attachment = new ScQtCheckListAttachment();
			attachment.setScQtCheckList(scQtChecklist);
		}
		uploadAttachment(event.getFile(), attachment);

		scQtChecklist.setScQtCheckListAttachment(attachment);
		System.out.println("===scQtChecklist size====="
				+ scQtChecklist.getScQtCheckListAttachment().getFileName());
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		this.getManagedBean().setTaskId(taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCSpecificationDataType specificationDataType = (SCSpecificationDataType) obj;

		this.getManagedBean().setSpecificationDataType(specificationDataType);

		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(specificationDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
		detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		retriveSpecdetails(specificationDataType.getQtId().longValue());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(
						fc,
						null,
						"/xhtml/ss/specification/specificationview.xhtml"
								+ "?faces-redirect=true");
	}

	public String submitSpecDetails(String outCome) {

		SpecificationReqDTO specReqDTO = prepareSpecReq();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		specReqDTO.setToken(ctxToken);

		specReqDTO.setTaskId(this.getManagedBean().getTaskId());

		specReqDTO.setScSpecificationDataType(this.getManagedBean()
				.getSpecificationDataType());
		specReqDTO.setOutcome(outCome);
		IQTSpecSO iqtSpecSO = BusinessDelegate.getInstance().getServiceObject(
				QTConstants.SS_MODULE, IQTSpecSO.class);
		try {
			SpecificationResDTO resDTO = iqtSpecSO.updateBPMAction(specReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {

			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the selctValue
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * @param selctValue
	 *            the selctValue to set
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	/**
	 * @return the selectedCompilenceList
	 */
	public List<SpecificationCompilanceBean> getSelectedCompilenceList() {
		return selectedCompilenceList;
	}

	/**
	 * @param selectedCompilenceList
	 *            the selectedCompilenceList to set
	 */
	public void setSelectedCompilenceList(
			List<SpecificationCompilanceBean> selectedCompilenceList) {
		this.selectedCompilenceList = selectedCompilenceList;
	}

	/**
	 * @return the selectedst1TechAddProposal
	 */
	public ScQtCheckList[] getSelectedst1TechAddProposal() {
		return selectedst1TechAddProposal;
	}

	/**
	 * @param selectedst1TechAddProposal
	 *            the selectedst1TechAddProposal to set
	 */
	public void setSelectedst1TechAddProposal(
			ScQtCheckList[] selectedst1TechAddProposal) {
		this.selectedst1TechAddProposal = selectedst1TechAddProposal;
	}

	/**
	 * @return the selectedst1FinanceProposal
	 */
	public ScQtCheckList[] getSelectedst1FinanceProposal() {
		return selectedst1FinanceProposal;
	}

	/**
	 * @param selectedst1FinanceProposal
	 *            the selectedst1FinanceProposal to set
	 */
	public void setSelectedst1FinanceProposal(
			ScQtCheckList[] selectedst1FinanceProposal) {
		this.selectedst1FinanceProposal = selectedst1FinanceProposal;
	}

	/**
	 * @return the selectedst1FinanceAddProposal
	 */
	public ScQtCheckList[] getSelectedst1FinanceAddProposal() {
		return selectedst1FinanceAddProposal;
	}

	/**
	 * @param selectedst1FinanceAddProposal
	 *            the selectedst1FinanceAddProposal to set
	 */
	public void setSelectedst1FinanceAddProposal(
			ScQtCheckList[] selectedst1FinanceAddProposal) {
		this.selectedst1FinanceAddProposal = selectedst1FinanceAddProposal;
	}

	/**
	 * @return the selectedst2TechProposal
	 */
	public ScQtCheckList[] getSelectedst2TechProposal() {
		return selectedst2TechProposal;
	}

	/**
	 * @param selectedst2TechProposal
	 *            the selectedst2TechProposal to set
	 */
	public void setSelectedst2TechProposal(
			ScQtCheckList[] selectedst2TechProposal) {
		this.selectedst2TechProposal = selectedst2TechProposal;
	}

	/**
	 * @return the selectedst2TechAddProposal
	 */
	public ScQtCheckList[] getSelectedst2TechAddProposal() {
		return selectedst2TechAddProposal;
	}

	/**
	 * @param selectedst2TechAddProposal
	 *            the selectedst2TechAddProposal to set
	 */
	public void setSelectedst2TechAddProposal(
			ScQtCheckList[] selectedst2TechAddProposal) {
		this.selectedst2TechAddProposal = selectedst2TechAddProposal;
	}

	/**
	 * @return the selectedst2FinanceProposal
	 */
	public ScQtCheckList[] getSelectedst2FinanceProposal() {
		return selectedst2FinanceProposal;
	}

	/**
	 * @param selectedst2FinanceProposal
	 *            the selectedst2FinanceProposal to set
	 */
	public void setSelectedst2FinanceProposal(
			ScQtCheckList[] selectedst2FinanceProposal) {
		this.selectedst2FinanceProposal = selectedst2FinanceProposal;
	}

	/**
	 * @return the selectedst2FinanceAddProposal
	 */
	public ScQtCheckList[] getSelectedst2FinanceAddProposal() {
		return selectedst2FinanceAddProposal;
	}

	/**
	 * @param selectedst2FinanceAddProposal
	 *            the selectedst2FinanceAddProposal to set
	 */
	public void setSelectedst2FinanceAddProposal(
			ScQtCheckList[] selectedst2FinanceAddProposal) {
		this.selectedst2FinanceAddProposal = selectedst2FinanceAddProposal;
	}

	/**
	 * @return the selectedDeduction
	 */
	public ScQtDeduction[] getSelectedDeduction() {
		return selectedDeduction;
	}

	/**
	 * @param selectedDeduction
	 *            the selectedDeduction to set
	 */
	public void setSelectedDeduction(ScQtDeduction[] selectedDeduction) {
		this.selectedDeduction = selectedDeduction;
	}

	/**
	 * @return the selectedst1TechProposal
	 */
	public ScQtCheckList[] getSelectedst1TechProposal() {
		return selectedst1TechProposal;
	}

	/**
	 * @param selectedst1TechProposal
	 *            the selectedst1TechProposal to set
	 */
	public void setSelectedst1TechProposal(
			ScQtCheckList[] selectedst1TechProposal) {
		this.selectedst1TechProposal = selectedst1TechProposal;
	}

	public String updateBpmTask(String action) {
		System.out.println("Succeesfully update the task");
		SpecificationReqDTO spReqDTO = prepareSpecReq();
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();
		try {
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process ", " "

					));
		}
		return null;

	}
	
	

	private IQTSpecSO getSpecSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				QTConstants.SS_MODULE, IQTSpecSO.class);
	}
	public void tierEvent(AjaxBehaviorEvent event){
		  String type = (String) event.getComponent().getAttributes().get("value");
		  this.getManagedBean().getScQtSpec().setEvaluationType(type);
		 }
}
