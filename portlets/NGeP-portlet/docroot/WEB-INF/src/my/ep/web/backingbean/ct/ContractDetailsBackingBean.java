/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAssociationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAssociationReqDTO;
import my.ep.dto.ct.maintain.ContractAssociationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.assoc.CtAssociation;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.CtSetting;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.agreement.AgreementBackingBean;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.PaymentSummary;
import my.ep.web.bean.ct.ZoneDetails;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean
@RequestScoped
public class ContractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml";
	private static final String PROCESS_VIEW_PAGE = "/xhtml/ct/agreement/shared/sidemenu/agreementViewTab.xhtml";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml";
	private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml";
	private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml";
	private static final String PROCESS_BANK_PAGE = "/xhtml/ct/supplier/bank/tabView.xhtml";
	private static final String PROCESS_SUPPLIER_TERMINATE_PAGE = "/xhtml/ct/supplier/termination/tabView.xhtml";

	private TreeNode rootNode;
	private TreeNode selectedNode;

	private boolean paymentHistory;
	private TreeNode rootNodeAgency;
	private boolean activeSuspension;
	private String fulfillmentNo = "01234567889";

	/* Testing Purpose Start */
	private List<CtSetting> ctSetting;
	private List<CtAgency> ctAgencyList;
	private List<CtSchedule> ctSchedules;
	private List<CtFactoring> ctFactoring;
	private CtAgency[] selectedAgencyList;
	private CtContract[] selectedContractList;
	private List<CtItemPrice> ctItemPrice;
	private List<SmSupplierBank> smSupplier;
	private CtContractItem ctContractedItems;
	private ContractListItem contractListItem;
	private List<CtContractValue> ctContractValue;
	private List<CtContractItem> ctContractedItem;
	private List<CtItemCeilingQty> ctItemCeilingQty;
	private List<CtPerformanceBond> CtPerformanceBonds;
	private List<CreateContractDetails> agencyDetails = null;
	private CtContract contract = new CtContract();
	/* Testing purpose End */

	private DefaultStreamedContent download;
	private CtDeductionClause[] chkDeductionClause;
	private final static String[] deductions;
	static {

		deductions = new String[6];
		deductions[0] = "Royalty";
		deductions[1] = "Penalty";
		deductions[2] = "Advanced Payment";
		deductions[3] = "Fixed";
		deductions[4] = "Non-Fixed";
		deductions[5] = "Others";
	}

	/* Termination */
	private String editorValue;

	public String[] getDeductions() {
		return deductions;
	}

	public ContractDetailsBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		if (getManagedBean().getApproverModel() == null) {
			List<String> source = getAvailableApproverList();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
		contractListItem = new ContractListItem();

		rootNode = new DefaultTreeNode("root", null);
		TreeNode zone1 = new DefaultTreeNode(new ZoneDetails("Zone Name  ZON TENGAH", "0"), rootNode);
		TreeNode Zone2 = new DefaultTreeNode(new ZoneDetails("Zone Name ZON UTARA", "0"), rootNode);
		new DefaultTreeNode(new ZoneDetails("PUTRAJAYA"), zone1);
		new DefaultTreeNode(new ZoneDetails("SELANGOR"), Zone2);

		rootNodeAgency = new DefaultTreeNode("root", null);
		this.getManagedBean().getCreateContractDetails().setMinistryName("KEMENTERIAN PELAJARAN");
		this.getManagedBean().getCreateContractDetails().setDepartmentName("BAHAGIAN MATRIKULASI");

		TreeNode agencyOne = new DefaultTreeNode(new CreateContractDetails("KEMENTERIAN PELAJARAN"), rootNodeAgency);
		TreeNode agencytwo = new DefaultTreeNode(new CreateContractDetails("BAHAGIAN MATRIKULASI"), agencyOne);

		new DefaultTreeNode(new CreateContractDetails("0211780401","NEGERI SEMBILAN", "5000.00", new Date(), new Date()),agencytwo);

		if (this.getManagedBean().getOutComes() == null) {
			this.getManagedBean().setOutComes(new String[] { BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT });
		}
	}

	@PostConstruct
	public void initializeContractValue() throws ParseException {
		/* Insert Data Contract Table */
		if (this.getManagedBean().getCtContract() == null) {
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());

			this.getManagedBean().setPaymentSummary(new ArrayList<PaymentSummary>());
			PaymentSummary summpary = new PaymentSummary();
			summpary.setId(1);
			summpary.setDescription("Contract Amount");
			summpary.setAmount(50000);
			PaymentSummary summpary1 = new PaymentSummary();
			summpary1.setId(2);
			summpary1.setDescription("Total Invoice Amount");
			summpary1.setAmount(40000);
			PaymentSummary summpary2 = new PaymentSummary();
			summpary2.setId(3);
			summpary2.setDescription("Balance Amount");
			summpary2.setAmount(10000);
			this.getManagedBean().getPaymentSummary().add(summpary);
			this.getManagedBean().getPaymentSummary().add(summpary1);
			this.getManagedBean().getPaymentSummary().add(summpary2);
		}
	}

	public List<SelectItem> getItemDetailsMethod() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("Item Pricing", "Item  Pricing"));
		list.add(new SelectItem("Item Ceiling Quantity", "Item Ceiling Quantity"));
		return list;
	}

	public List<SelectItem> getTerminationType() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("Warning Letter", "Warning Letter"));
		list.add(new SelectItem("Termination", "Termination"));
		list.add(new SelectItem("Reject Termination", "Reject Termination"));
		return list;
	}

	private void flush() {
		this.getManagedBean().setItemPricing(false);
		this.getManagedBean().setItemCeilingQuantity(false);
		paymentHistory = false;
		this.getManagedBean().setChkSchedule(false);
		this.getManagedBean().setChkProgressive(false);
		this.getManagedBean().setTerminateNotice(false);
		this.getManagedBean().setTerminateRejectLetter(false);
		this.getManagedBean().setTerminateWarningLetter(false);
	}

	public void changeField() {
		try {
			flush();
			if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Pricing")) {
				this.getManagedBean().setItemPricing(true);
			} else if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Ceiling Quantity")) {
				this.getManagedBean().setItemCeilingQuantity(true);
			} else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("155")) {
				this.getManagedBean().setTerminateWarningLetter(true);
			} else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("154")) {
				this.getManagedBean().setTerminateNotice(true);
			} else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("156")) {
				this.getManagedBean().setTerminateRejectLetter(true);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void changePaymentValue() {
		try {
			flush();
			if (getFulfillmentNo() != null) {
				paymentHistory = true;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();
		if (newValue != null && !newValue.equals(oldValue)) {
		}
	}

	/* Testing purpose started */
	public void addAdvanceContract(ActionEvent event) {
		for (int i = 0; i < selectedContractList.length; i++) {
			CtContract ad = selectedContractList[i];
			this.getManagedBean().getAssociateSearchList().add(ad);
		}
		selectedContractList = null;
	}

	public void deleteAdvanceContract() {
		this.getManagedBean().getAssociateSearchList().remove(this.getManagedBean().getCtContract());
	}

	public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
		this.getManagedBean().getTermination().getCtTerminationReasons().add(terminateReason);
	}

	public void deleteTerminateReason() {
		this.getManagedBean().getTermination().getCtTerminationReasons().remove(this.getManagedBean().getTerminationReason());
	}

	public void deleteTerminateCaluse() {
		this.getManagedBean().getTerminationClauseList().remove(this.getManagedBean().getTerminationClause());
	}

	public void createAssociation() {
		List<CtAssocContract> assocContract = new ArrayList<CtAssocContract>();
		CtAssocContract association = new CtAssocContract();
		IContractAssociationSO contractAssociationSO = null;

		try {
			this.getManagedBean().getAssociation().setProjectName(this.getManagedBean().getProjectName());
			this.getManagedBean().getAssociation().setDescription(this.getManagedBean().getProjectDescription());
			for (CtContract ctContract : this.getManagedBean().getAssociateSearchList()) {
				association.setCtContract(ctContract);
				association.setCtAssociation(this.getManagedBean().getAssociation());
				assocContract.add(association);
			}
			this.getManagedBean().getAssociation().setCtAssocContracts(assocContract);

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setAssociation(this.getManagedBean().getAssociation());
			contractAssociationSO.createAssociation(reqDTO);
		} catch (Exception ex) {
			log.error("Exception>>>" + ex);
		} finally {
			contractAssociationSO = null;
			assocContract = null;
			association = null;
		}
	}

	public void searchAssociation() {
		IContractAssociationSO contractAssociationSO = null;
		ISearchBean<CtAssociation> searchBean = new SearchBean<CtAssociation>();

		try {
			SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

			if (this.getManagedBean().getProjectName() != null) {
				equalFilterBean.setSearchField("projectName");
				equalFilterBean.setSearchValue(this.getManagedBean().getProjectName());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}

			if (this.getManagedBean().getProjectDescription() != null) {
				equalFilterBean.setSearchField("description");
				equalFilterBean.setSearchValue(getManagedBean().getProjectDescription());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setSearchBean(super.createSearchBean(CtAssociation.class,"associateSearch"));
			reqDTO.setSearchBean(searchBean);
			ContractAssociationResDTO ResDTO = contractAssociationSO.searchAssociation(reqDTO);
			this.getManagedBean().setAssociationList(ResDTO.getResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractAssociationSO = null;
			searchBean = null;
		}
	}

	public void deleteAssociation() {
		IContractAssociationSO contractAssociationSO = null;
		try {
			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setAssociation(this.getManagedBean().getAssociation());
			log.info("deleteing value>>>>>>>"+ this.getManagedBean().getAssociation().getAssociationId());
			contractAssociationSO.deleteAssociation(reqDTO);
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractAssociationSO = null;
		}
	}

	public List<SelectItem> getSelectedBankAccount() {
		List<SelectItem> list = new ArrayList<SelectItem>();
		for (SmSupplierBank smSupplier : this.getSmSupplier()) {
			list.add(new SelectItem(smSupplier.getAccountNo()));
		}
		return list;
	}

	public void changeAccDetails() {
		try {
			for (SmSupplierBank smSuppliers : this.getSmSupplier()) {
				if (this.getManagedBean().getSmSupplierBank().getAccountNo().equals(smSuppliers.getAccountNo())) {
					this.getManagedBean().setSmSupplierBank(smSuppliers);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	/* Testing purpose End */

	public String saveContract(String action) {
		IContractSO contractSO = null;
		try {
			ContractReqDTO reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setCtContract(this.getManagedBean().getCtContract());
			String token = getManagedBean().getToken();
			if (token == null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				token = bpmSO.authenticate("roslan", "welcome1");
				if (token == null) {
					throw new EpException("BPM token not found");
				}
			}
			reqDTO.setOutcome(action);
			if (action == BpmOutcomeConst.BPM_SAVE) {
				reqDTO.setOutcome("");
			}
			reqDTO.setToken(token);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			ContractResDTO resDTO = contractSO.saveContract(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ this.getManagedBean().getCtContract().getContractName()));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Contract save failed."));
		} finally {
			contractSO = null;
		}
		return null;
	}

	public String terminateContract(String action) {
		getManagedBean().getCtContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
		return saveContract(action);
	}

	public String amendContract(String action) {
		getManagedBean().getCtContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
		return saveContract(action);
	}

	public void taskListAmendmentAction(Long amendmentId, Long contractId) {
		IContractSO contractSO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			ctReqDTO.setContractId(contractId);
			ContractResDTO ctResDTO = contractSO.retrieveContract(ctReqDTO);
			this.getManagedBean().setCtContract(ctResDTO.getContract());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),e.getMessage()));
		} finally {
			contractSO = null;
		}
	}

	public void taskListTerminationAction(Long terminationId, Long contractId) {
		IContractSO contractSO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			ctReqDTO.setContractId(contractId);
			ContractResDTO ctResDTO = contractSO.retrieveContract(ctReqDTO);
			this.getManagedBean().setCtContract(ctResDTO.getContract());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),e.getMessage()));
		} finally {
			contractSO = null;
		}
	}

	/* Code for agency */
	public List<CtAgency> getCtAgencyList() {
		if (ctAgencyList == null) {
			ctAgencyList = new ArrayList<CtAgency>();
			CtAgency agencyDetails1 = new CtAgency();
			CtAgency agencyDetails2 = new CtAgency();
			agencyDetails1.setAgencyId(new Long(1));
			agencyDetails1.setCeilingAmount(5000.00);
			agencyDetails1.setEffDate(new Date());
			agencyDetails1.setExpDate(new Date());
			PmOrgProfile pmOrgProfile = new PmOrgProfile();
			pmOrgProfile.setOrgCode("123451101");
			pmOrgProfile.setOrgName("KOLEJ MATRIKULASI NEGERIbx");
			agencyDetails1.setOrgProfile(pmOrgProfile);
			ctAgencyList.add(agencyDetails1);

			agencyDetails2.setAgencyId(new Long(2));
			agencyDetails2.setCeilingAmount(6000.00);
			agencyDetails2.setEffDate(new Date());
			agencyDetails2.setExpDate(new Date());
			PmOrgProfile pmOrgProfile1 = new PmOrgProfile();
			pmOrgProfile.setOrgCode("123451102");
			pmOrgProfile.setOrgName("KOLEJ MATRIKULASI PULAU PINANG");
			agencyDetails1.setOrgProfile(pmOrgProfile1);
			ctAgencyList.add(agencyDetails1);
		}
		return ctAgencyList;
	}

	public void searchContract() {
		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		if (this.getManagedBean().getQtLoaNo() != null) {
			equalFilterBean.setSearchField("qtLoaId");
			equalFilterBean.setSearchValue(this.getManagedBean().getQtLoaNo());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if (this.getManagedBean().getContractName() != null) {
			equalFilterBean.setSearchField("contractName");
			equalFilterBean.setSearchValue(getManagedBean().getContractName());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		IContractSO contractSO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "dataTable"));
			reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
		}
	}

	public void advanceContractSearch() {
		log.info("searchAction starts");
		IContractSO contractSO = null;
		try {
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			if (this.getManagedBean().getQtLoaNo() != null) {
				equalFilterBean.setSearchField("qtLoaId");
				equalFilterBean.setSearchValue(this.getManagedBean().getQtLoaNo());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "advanceCtSearch"));
			reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);
		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
		}
	}

	public void setCtAgencyList(List<CtAgency> ctAgencyList) {
		this.ctAgencyList = ctAgencyList;
	}

	public CtDeductionClause[] getChkDeductionClause() {
		return chkDeductionClause;
	}

	public void setChkDeductionClause(CtDeductionClause[] chkDeductionClause) {
		if (chkDeductionClause != null)
			this.chkDeductionClause = Arrays.copyOf(chkDeductionClause, chkDeductionClause.length);
	}

	public void addDeductionClause(ActionEvent event) {
		for (int i = 0; i < chkDeductionClause.length; i++) {
			CtDeductionClause ad = chkDeductionClause[i];
			this.getManagedBean().getAddDeductionClause().add(ad);
		}
		chkDeductionClause = null;
	}

	public void deleteClauseDetails() {
		this.getManagedBean().getAddDeductionClause().remove(this.getManagedBean().getDeductionClause());
	}

	public void addDeductionClause() {

	}

	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>();
		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			// TODO
			reqDTO.setOrgProfileId(1250l);
			UserResDTO resDTO = userSO.retrieveUserGroupList(reqDTO);
			for (PmUserGroupUser user : resDTO.getUserGroupList().get(0)
					.getPmUserGroupUsers()) {
				availableApprovers.add(user.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
	}
	
	public void fileUpload(FileUploadEvent event) {
		
	}

	public List<SmSupplierBank> getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(List<SmSupplierBank> smSupplier) {
		this.smSupplier = smSupplier;
	}

	public CtAgency[] getSelectedAgencyList() {
		return selectedAgencyList;
	}

	public void setSelectedAgencyList(CtAgency[] selectedAgencyList) {
		if (selectedAgencyList != null)
			this.selectedAgencyList = Arrays.copyOf(selectedAgencyList,
					selectedAgencyList.length);
		;
	}

	public void onRowUnselect(UnselectEvent event) {
	}

	public List<CtContractItem> getCtContractedItem() {
		return ctContractedItem;
	}

	public void setCtContractedItem(List<CtContractItem> ctContractedItem) {
		this.ctContractedItem = ctContractedItem;
	}

	public List<CtItemPrice> getCtItemPrice() {
		return ctItemPrice;
	}

	public void setCtItemPrice(List<CtItemPrice> ctItemPrice) {
		this.ctItemPrice = ctItemPrice;
	}

	public CtContractItem getCtContractedItems() {
		return ctContractedItems;
	}

	public void setCtContractedItems(CtContractItem ctContractedItems) {
		this.ctContractedItems = ctContractedItems;
	}

	public List<CtItemCeilingQty> getCtItemCeilingQty() {
		return ctItemCeilingQty;
	}

	public void setCtItemCeilingQty(List<CtItemCeilingQty> ctItemCeilingQty) {
		this.ctItemCeilingQty = ctItemCeilingQty;
	}

	public List<CtContractValue> getCtContractValue() {
		return ctContractValue;
	}

	public void setCtContractValue(List<CtContractValue> ctContractValue) {
		this.ctContractValue = ctContractValue;
	}

	public void progressivePayment() {
		flush();
		if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("1"))
			this.getManagedBean().setChkSchedule(true);
		else if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("2"))
			this.getManagedBean().setChkProgressive(true);
	}

	public List<CtSchedule> getCtSchedules() {
		return ctSchedules;
	}

	public void setCtSchedules(List<CtSchedule> ctSchedules) {
		this.ctSchedules = ctSchedules;
	}

	public List<CtFactoring> getCtFactoring() {
		return ctFactoring;
	}

	public void setCtFactoring(List<CtFactoring> ctFactoring) {
		this.ctFactoring = ctFactoring;
	}

	public List<CtSetting> getCtSetting() {
		return ctSetting;
	}

	public void setCtSetting(List<CtSetting> ctSetting) {
		this.ctSetting = ctSetting;
	}

	public List<CtPerformanceBond> getCtPerformanceBonds() {
		return CtPerformanceBonds;
	}

	public void setCtPerformanceBonds(List<CtPerformanceBond> ctPerformanceBonds) {
		CtPerformanceBonds = ctPerformanceBonds;
	}

	public void onChange(TabChangeEvent event) {
	}

	public String gotoTerminatePage() {
		return PROCESS_TERMINATE_PAGE;
	}

	public String gotoAmendPage() {
		return PROCESS_AMEND_PAGE;
	}

	public boolean isActiveSuspension() {
		return activeSuspension;
	}

	public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}

	public void setActiveSuspension(boolean activeSuspension) {
		this.activeSuspension = activeSuspension;
	}

	public TreeNode getRootNodeAgency() {
		return rootNodeAgency;
	}

	public void setRootNodeAgency(TreeNode rootNodeAgency) {
		this.rootNodeAgency = rootNodeAgency;
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<CreateContractDetails> getAgencyLists() {
		agencyDetails = new ArrayList<CreateContractDetails>();
		return agencyDetails;
	}

	public List<CreateContractDetails> getAgencyDetails() {
		return agencyDetails;
	}

	public void setAgencyDetails(List<CreateContractDetails> agencyDetails) {
		this.agencyDetails = agencyDetails;
	}

	public String getFulfillmentNo() {
		return fulfillmentNo;
	}

	public void setFulfillmentNo(String fulfillmentNo) {
		this.fulfillmentNo = fulfillmentNo;
	}

	public boolean isPaymentHistory() {
		return paymentHistory;
	}

	public void setPaymentHistory(boolean paymentHistory) {
		this.paymentHistory = paymentHistory;
	}

	public void addDeductionDetails() {
		CtDeduction ctDeductions = new CtDeduction();
		this.getManagedBean().getCtContractVer().getCtDeductions().add(ctDeductions);
	}

	public void deleteDeductionDetails() {
		this.getManagedBean().getCtContractVer().getCtDeductions().remove(this.getManagedBean().getCtDeduction());
	}
	
	public void addFrequencyDetails(){
		CtSchedule ctSchedules = new CtSchedule();
		this.getManagedBean().getCtContractVer().getCtSchedules().add(ctSchedules);
		
	}
	
	public void deleteFrequencyDetails(){
		this.getManagedBean().getCtContractVer().getCtSchedules().remove(this.getManagedBean().getCtSchedules());
		
	}

	public void addScheduleDetails() {
		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
		this.getManagedBean().getCtContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	public void deleteScheduleDetails() {
		this.getManagedBean().getCtContractVer().getCtProgressivePayments().remove(this.getManagedBean().getCtProgressivePayment());
	}

	public List<CtDeduction> getDeducationList() {
		CtDeduction ctDeduction1 = new CtDeduction();
		this.getManagedBean().getCtContractVer().getCtDeductions().add(ctDeduction1);
		return null;
	}

	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}

	public String gotoSuspendPage() {
		contract = null;
		return PROCESS_SUSPEND_PAGE;
	}

	public String gotoViewPage() {

		AgreementBackingBean aBB = new AgreementBackingBean();
		aBB.getManagedBean().setContract(contract);
		return PROCESS_VIEW_PAGE;
	}

	public String gotoAssociatePage() {
		return PROCESS_ASSOCIATE_PAGE;
	}

	public String gotoCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
	}
	
	public String gotoBankPage(){
		return PROCESS_BANK_PAGE;
	}
	
	public String gotoSupplierTerminatePage(){
		return PROCESS_SUPPLIER_TERMINATE_PAGE;
	}

	public CtContract getContract() {
		return contract;
	}

	public void setContract(CtContract contract) {
		this.contract = contract;
	}

	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	public DefaultStreamedContent getDownload() {
		return download;
	}

	public void setDownload(DefaultStreamedContent download) {
		this.download = download;
	}

}
