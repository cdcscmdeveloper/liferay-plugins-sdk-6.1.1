/**
 * 
 */
package my.ep.web.backingbean.ss.specification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.currency.ICurrencySO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.ss.quationtender.IQTSpecSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.currency.CurrencyReqDTO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.ss.specification.SpecificationReqDTO;
import my.ep.dto.ss.specification.SpecificationResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.specification.SpecificationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationBackingBean")
@RequestScoped
public class SpecificationBackingBean extends
		BaseBackingBean<SpecificationManagedBean> {
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
		/*
		 * System.out.println("====constructor====="); ICurrencySO CurrencySO =
		 * BusinessDelegate.getInstance() .getServiceObject("pm",
		 * ICurrencySO.class); System.out.println(CurrencySO); CurrencyResDTO
		 * resDTO=CurrencySO.retriveCurrencyList();
		 * System.out.println("======currency List Size===="
		 * +resDTO.getCurrencyList().size()); CurrencyReqDTO reqDTO=new
		 * CurrencyReqDTO(); reqDTO.setCurrencyCode("1"); CurrencyResDTO
		 * resDTO1=CurrencySO.retriveCurrencyCodeByCodeId(reqDTO);
		 * System.out.println
		 * ("======currency entity value===="+resDTO1.getPmCurrency
		 * ().getCurrencyName());
		 */
		/*
		 * IProcurementSO CurrencySO = BusinessDelegate.getInstance()
		 * .getServiceObject("pm", IProcurementSO.class);
		 * System.out.println(CurrencySO); ProcurementResDTO
		 * resDTO=CurrencySO.retriveProcurementList();
		 * System.out.println("======procurement List Size===="
		 * +resDTO.getProcurementList().size()); ProcurementReqDTO reqDTO=new
		 * ProcurementReqDTO(); reqDTO.setProcurementId(1); ProcurementResDTO
		 * resDTO1=CurrencySO.retriveProcurementCategoryById(reqDTO);
		 * System.out.
		 * println("======procurement name value===="+resDTO1.getProcureCategory
		 * ().getCategoryName());
		 */
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.getManagedBean().setApprovalDate(sdf.format(d));
		compilenceList();
	}

	public void save() {
		try {
		

			IProcurementSO procurementSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IProcurementSO.class);
			System.out.println(procurementSO);
		
			ProcurementReqDTO reqDTO = new ProcurementReqDTO();
			reqDTO.setProcurementId(this.getManagedBean().getScQtSpec()
					.getPmProcurementCategory().getProcurementCategoryId());
			ProcurementResDTO procResDTO1 = procurementSO
					.retriveProcurementCategoryById(reqDTO);
			ScQtSpec scqtSpec = new ScQtSpec();
			scqtSpec.setEvaluationType(this.getManagedBean().getScQtSpec()
					.getEvaluationType());
			scqtSpec.setPmProcurementCategory(procResDTO1.getProcureCategory());
			ICurrencySO CurrencySO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICurrencySO.class);
			System.out.println(CurrencySO);
			// CurrencyResDTO resDTO=CurrencySO.retriveCurrencyList();
			// System.out.println("======currency List Size===="+resDTO.getCurrencyList().size());
			CurrencyReqDTO currencyreqDTO = new CurrencyReqDTO();
			currencyreqDTO.setCurrencyCode(this.getManagedBean().getScQtSpec()
					.getPmCurrency().getCurrencyCode());
			CurrencyResDTO currencyresDTO = CurrencySO
					.retriveCurrencyCodeByCodeId(currencyreqDTO);
			scqtSpec.setPmCurrency(currencyresDTO.getPmCurrency());
			scqtSpec.setApprovalDate(new Date());
				
		/*	System.out.println("====max proposal==="+this.getManagedBean().getScQtSpec()
					.getMaxProposal());*/
			System.out.println("=====date==="+this.getManagedBean().getApprovalDate());
			scqtSpec.setApproverName(this.getManagedBean().getScQtSpec()
					.getApproverName());
			
			scqtSpec.setMaxProposal(this.getManagedBean().getScQtSpec()
					.getMaxProposal());
			scqtSpec.setAwardType(this.getManagedBean().getScQtSpec()
					.getAwardType());
			scqtSpec.setFulfilmentType(this.getManagedBean().getScQtSpec()
					.getFulfilmentType());
			scqtSpec.setJustificationRemark(this.getManagedBean().getScQtSpec()
					.getJustificationRemark());
			scqtSpec.setSampleReq(this.getManagedBean().getScQtSpec()
					.isSampleReq());
			scqtSpec.setApprovalReq(this.getManagedBean().getScQtSpec()
					.isApprovalReq());
			scqtSpec.setContractDuration(this.getManagedBean().getScQtSpec()
					.getContractDuration());
			// scqtSpec.setScQt(scQt);
			IStatusSO statussO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			System.out.println("====status Object====" + statussO);
			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(this.getManagedBean().getScQtSpec()
					.getPmStatus().getStatusId());
			StatusResDTO statusresspec = statussO
					.retrieveStatusByStatusId(statusreq);
			
			scqtSpec.setPmStatus(statusresspec.getPmStatus());

			ScQt qt = new ScQt();
			qt.setQtAmount(this.getManagedBean().getScQtSpec().getScQt()
					.getQtAmount());

			ScQtRemark remarks = new ScQtRemark();
			remarks.setRemark(this.getManagedBean().getScQtRemarks()
					.getRemark());

			List<ScQtCheckList> st1tech = this.getManagedBean()
					.getScQtCheckSt1techProposal();
			System.out.println("=====st1tech=="+st1tech.size());
			List<ScQtCheckList> checkListSt1 = new ArrayList<ScQtCheckList>();
			for(ScQtCheckList checkst1tech:st1tech){
				checkst1tech.setDocType("1");
				checkst1tech.setEvaluationStage("1");
			}

			
				IQTSpecSO specSO = BusinessDelegate.getInstance()
						.getServiceObject("pm", IQTSpecSO.class);
				SpecificationReqDTO specreqDTO=new SpecificationReqDTO();
				//specSO.saveSpecDetails(reqDTO)

		} catch (Exception e) {
			System.out.println("=====cath exception method===");
			e.printStackTrace();
			throw new IllegalArgumentException();
		}

	}

	@PostConstruct
	public void init() {
		try {

			if (getManagedBean().getScQtSpec() == null) {
				getManagedBean().setScQtSpec(new ScQtSpec());
				getManagedBean().getScQtSpec().setPmCurrency(new PmCurrency());
				getManagedBean().getScQtSpec().setPmProcurementCategory(
						new PmProcurementCategory());
				getManagedBean().getScQtSpec().setScQt(new ScQt());
				getManagedBean().setScQtRemarks(new ScQtRemark());
				getManagedBean().setScQtChecklist(new ScQtCheckList());
				getManagedBean().getScQtSpec().setPmStatus(new PmStatus());
				// getManagedBean().setScRemark(new ScRemark());
				// getManagedBean().setScQtDeduction(new ScQtDeduction());
				// getManagedBean().setScQtDeductionList(new ScQtDeduction());
				// getManagedBean().getScQtChecklist().setTitle("Manga");
				// getManagedBean().getScQtChecklist().setChecklistType("upload");
				// List<ScQtCheckList> list=new ArrayList<ScQtCheckList>();
				// ScQtCheckList checklist=new ScQtCheckList();
				// checklist.setTitle("Manga");
				// checklist.setChecklistType("Upload");
				// checklist.setChecklistType("Upload");
				// checklist.setDocType("1");
				// checklist.setEvaluationStage("1");
				// list.add(checklist);
				// this.getManagedBean().setScQtCheckSt1techProposal(list);

			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void retriveSpecdetails(){
		IQTSpecSO specSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IQTSpecSO.class);
		SpecificationReqDTO reqDTO=new SpecificationReqDTO();
		SpecificationResDTO resDTO=new SpecificationResDTO();
		reqDTO.setQtId(146L);
		resDTO=specSO.retrieveSpecDetails(reqDTO);
		//List<ScQ>resDTO.getS1TecResultBean()
		//this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(resDTO.getS1TecResultBean());
				
		
	}
	
	public void specificAdd() {
		System.out.println("====enter specification details add method====");
		try {
			System.out.println("enter try loop");
			List<ScQtDeduction> deductionList = getManagedBean()
					.getScQtDeductionList();
			if (null == deductionList) {
				System.out.println("==enter null loop===");
				deductionList = new ArrayList<ScQtDeduction>();

			}
			/*
			 * if(deductionList!=null){ for(int i=0;i<deductionList.size();i++){
			 * ScQtDeduction deduction= deductionList.get(i);
			 * System.out.println(
			 * "====add method value==="+deduction.getDeductionType()); } }
			 */
			ScQtDeduction deduction = new ScQtDeduction();
			if (null == deductionList) {
				deduction.setQtDeductionId(1L);

			}
			
			deductionList.add(deduction);
			// this.getManagedBean().getScQtDeductionList().addAll(c);
			this.getManagedBean().setScQtDeductionList(deductionList);
		} catch (Exception e) {
			System.out.println("===enter catch loop");
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
		// return getManagedBean().getScQtDeductionList();
	}

	public void stageOneAdditionalAdd() {
		System.out.println("====enter stage1 additional add method====");
		try {
			List<ScQtCheckList> st1additionalList = getManagedBean()
					.getScQtCheckSt1techAdditionalProposalList();

			if (null == st1additionalList) {
				st1additionalList = new ArrayList<ScQtCheckList>();
				// mediumCarsModel = new CarDataModel(st1additionalList);

			}

			ScQtCheckList additionProposalst1 = new ScQtCheckList();
			additionProposalst1.setQtChecklistId(1L);
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

			ScQtCheckList st1financeproposalList1 = new ScQtCheckList();
			st1financeproposalList1.setQtChecklistId(1L);
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

			ScQtCheckList st1financeAdditionalproposalList1 = new ScQtCheckList();
			st1financeAdditionalproposalList1.setQtChecklistId(1L);
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

			ScQtCheckList additionProposalst2 = new ScQtCheckList();
			additionProposalst2.setQtChecklistId(1L);
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

			ScQtCheckList st2financeproposalList2 = new ScQtCheckList();
			st2financeproposalList2.setQtChecklistId(1L);
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

			ScQtCheckList st2financeAdditionalproposalList1 = new ScQtCheckList();
			st2financeAdditionalproposalList1.setQtChecklistId(1L);
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
			/*
			 * List<String> compilnceList= getManagedBean().getCompilanceList();
			 * System
			 * .out.println("========compilance List size in backing bean====="
			 * +compilnceList.size()); String[] strarray = (String[])
			 * compilnceList.toArray(new String[0]); String value = "";
			 * List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			 * for (int i = 0; i < strarray.length; i++) { value = strarray[i];
			 * System
			 * .out.println("=======display compilance loop values===="+value);
			 * ScQtCheckList scqtlist=new ScQtCheckList();
			 * scqtlist.setTitle(value);
			 * 
			 * //TabBean mcities1 = getManagedBean().getTabview().get(value);
			 * 
			 * compilance.add(scqtlist);
			 * 
			 * //getManagedBean().setCompilanceList(compilance);
			 * getManagedBean().setScQtCheckSt1techProposal(compilance);
			 */
			List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			List<SpecificationCompilanceBean> list = getSelectedCompilenceList();
			List<SpecificationCompilanceBean> list1 = this.getManagedBean()
					.getCompilaenceList();
			System.out.println("======tech one list size====" + list.size());
			System.out.println("=======compilence bean list one size===="
					+ list1.size());
			for (int i = 0; i < list.size(); i++) {
				SpecificationCompilanceBean bean = list.get(i);
				ScQtCheckList scqtlist = new ScQtCheckList();
				scqtlist.setTitle(bean.getCompilanceTitle());
				scqtlist.setQtChecklistId(new Long(i));
				compilance.add(scqtlist);

				// getManagedBean().setCompilanceList(compilance);
				getManagedBean().setScQtCheckSt1techProposal(compilance);

			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechProposal() {
		System.out.println("====enter stage2 tech proposal====");
		try {
			/*
			 * List<String> compilnceList= getManagedBean().getCompilanceList();
			 * System
			 * .out.println("========compilance List size in backing bean====="
			 * +compilnceList.size()); String[] strarray = (String[])
			 * compilnceList.toArray(new String[0]); String value = ""
			 */;
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			ScQtCheckList scqtlist = new ScQtCheckList();

			scqtlist.setTitle("TemplateTitle");
			scqtlist.setChecklistType("Technical/Financial/Others");

			// TabBean mcities1 = getManagedBean().getTabview().get(value);

			compilance.add(scqtlist);

			// getManagedBean().setCompilanceList(compilance);
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

			ScQtCheckList st2technicalproposalList1 = new ScQtCheckList();
			if (st2technicalproposalList == null) {
				st2technicalproposalList1.setQtChecklistId(1L);
			} else {

				st2technicalproposalList1.setQtChecklistId(new Long(
						st2technicalproposalList.size() + 1));
			}
			st2technicalproposalList.add(st2technicalproposalList1);
			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwotechproposal(AjaxBehaviorEvent e) {
		System.out.println("=====stage two drop down ====");
		try {
			String dropdown = (String) e.getComponent().getAttributes()
					.get("value");
			System.out.println("=====stage two drop down value====" + dropdown);
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			/*
			 * List<ScQtCheckList> compilance =
			 * this.getManagedBean().getScQtCheckSt2techProposal();
			 * if(compilance==null){ compilance=new ArrayList<ScQtCheckList>();
			 * }
			 * 
			 * ScQtCheckList scqtlist=new ScQtCheckList();
			 * scqtlist.setChecklistType(dropdown); compilance.add(scqtlist);
			 */
			// this.getManagedBean().setScQtCheckSt2techProposal(compilance);

			setSelctValue(dropdown);
		} catch (Exception e1) {
			throw new IllegalArgumentException();
		}
		// this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}

	public void compilenceList() {

		try {
			/*
			 * List<String> compilnceList= getManagedBean().getCompilanceList();
			 * System
			 * .out.println("========compilance List size in backing bean====="
			 * +compilnceList.size()); String[] strarray = (String[])
			 * compilnceList.toArray(new String[0]); String value = "";
			 * List<ScQtCheckList> compilance = new ArrayList<ScQtCheckList>();
			 * for (int i = 0; i < strarray.length; i++) { value = strarray[i];
			 * System
			 * .out.println("=======display compilance loop values===="+value);
			 * ScQtCheckList scqtlist=new ScQtCheckList();
			 * scqtlist.setTitle(value);
			 * 
			 * //TabBean mcities1 = getManagedBean().getTabview().get(value);
			 * 
			 * compilance.add(scqtlist);
			 * 
			 * //getManagedBean().setCompilanceList(compilance);
			 * getManagedBean().setScQtCheckSt1techProposal(compilance);
			 */
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
			resDto=CurrencySO.retriveCurrencyList();
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

}
