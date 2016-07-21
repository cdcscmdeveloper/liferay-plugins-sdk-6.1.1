/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.integration.interfacegfmas;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceAgOffSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceCmBankSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceGlAccSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceKptjSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePpSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePrgActSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceProjectSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfacePtjSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotDanaSO;
import my.ep.biz.service.pm.common.interfaceptj.IInterfaceVotSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceAgOffReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceCmBankReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceGlAccReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceKptjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogReqDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfacePpReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePrgActReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceProjectReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotDanaReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.DIInterfaceLog;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.persistence.entity.pm.integration.PmInterfaceBank;
import my.ep.persistence.entity.pm.integration.PmInterfaceDana;
import my.ep.persistence.entity.pm.integration.PmInterfaceGlAccount;
import my.ep.persistence.entity.pm.integration.PmInterfaceKptj;
import my.ep.persistence.entity.pm.integration.PmInterfacePP;
import my.ep.persistence.entity.pm.integration.PmInterfaceProgAct;
import my.ep.persistence.entity.pm.integration.PmInterfaceProject;
import my.ep.persistence.entity.pm.integration.PmInterfacePtj;
import my.ep.persistence.entity.pm.integration.PmInterfaceVot;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.ProcessCodeType;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.intergration.interfacegfmas.GfMasManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class GfMasBackingBean.
 * 
 * @author NAGASIVA
 * @since 17.06.2016
 */
@ManagedBean(name = "gfMasBackingBean")
@ViewScoped
public class GfMasBackingBean extends BaseBackingBean<GfMasManagedBean> {

	/** The log. */
	private static Logger log = Logger.getLogger(GfMasBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/gfMas/gfmassearch/gfmasSearchMain";

	/** The Constant NAV_TO_PRGACT. */
	public static final String NAV_TO_PRGACT = "/xhtml/gfMas/gfmassearch/childprocess/gfmasPrgAct.xhtml";
	
	/** The Constant NAV_TO_PROJECT. */
	public static final String NAV_TO_PROJECT = "/xhtml/gfMas/gfmassearch/childprocess/gfmasProject.xhtml";
	
	/** The Constant NAV_TO_VOT. */
	public static final String NAV_TO_VOT = "/xhtml/gfMas/gfmassearch/childprocess/gfmasVot.xhtml";
	
	/** The Constant NAV_TO_DANA. */
	public static final String NAV_TO_DANA = "/xhtml/gfMas/gfmassearch/childprocess/gfmasDana.xhtml";
	
	/** The Constant NAV_TO_KPTJ. */
	public static final String NAV_TO_KPTJ = "/xhtml/gfMas/gfmassearch/childprocess/gfmasKPTJ.xhtml";
	
	/** The Constant NAV_TO_PTJ. */
	public static final String NAV_TO_PTJ = "/xhtml/gfMas/gfmassearch/childprocess/gfmasPTJ.xhtml";
	
	/** The Constant NAV_TO_PP. */
	public static final String NAV_TO_PP = "/xhtml/gfMas/gfmassearch/childprocess/gfmasPP.xhtml";
	
	/** The Constant NAV_TO_GLACCOUNT. */
	public static final String NAV_TO_GLACCOUNT = "/xhtml/gfMas/gfmassearch/childprocess/gfmasGlAccount.xhtml";
	
	/** The Constant NAV_TO_BANK. */
	public static final String NAV_TO_BANK = "/xhtml/gfMas/gfmassearch/childprocess/gfmasBank.xhtml";
	
	/** The Constant NAV_TO_AGOFFICE. */
	public static final String NAV_TO_AGOFFICE = "/xhtml/gfMas/gfmassearch/childprocess/gfmasAgOffice.xhtml";

	/** The search gf mas. */
	private IPagingDataModel<DIInterfaceLog> searchGfMas = new SearchGfMasPagingDataModel();

	/** The child process status. */
	private boolean childProcessStatus = false;

	/**
	 * Instantiates a new gf mas backing bean.
	 */
	public GfMasBackingBean() {

		log.info("inside GfMasBackingBean constructor");
		searchGfMas();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}
	
	/*public void init(){
		log.info("inside init ");
		searchGfMas();
	}*/

	/**
	 * Search gf mas.
	 */
	public void searchGfMas() {
		searchGfMas.reloadData();
	}

	/**
	 * Pre loading gfmas.
	 */
	public void preLoadingGfmas() {
		log.info("preLoadingGfmas");	
		this.getManagedBean().setProcessId(null);		
		FacesContext fc = FacesContext.getCurrentInstance();	
		searchGfMas();	
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Reset gf mas search bean.
	 */
	public void resetGfMasSearchBean() {
		log.info("resetGfMasSearchBean");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setInterfaceLogId(null);
		this.getManagedBean().setServiceCode(null);
		this.getManagedBean().setProcessId(null);
		this.getManagedBean().setDirection(null);
		this.getManagedBean().setCreatedDate(null);
		searchGfMas = new SearchGfMasPagingDataModel();
	}

	/**
	 * Gets the search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the search bean data
	 */
	private ISearchBean<DIInterfaceLog> getSearchBeanData(
			ISearchBean<DIInterfaceLog> searchBean) {
		log.info("getSearchBeanData");
		/*if (this.getManagedBean().getInterfaceLogId() != null) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("logId",
							SearchOperator.OPERATOR_EQUALS.value(), this
							.getManagedBean().getInterfaceLogId()));
		}
		if (StringUtils.isNotEmpty(this.getManagedBean().getServiceCode())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("serviceCode",
							SearchOperator.OPERATOR_EQUALS.value(), this
							.getManagedBean().getServiceCode()
							.toUpperCase(), true));
		}

		if (StringUtils.isNotEmpty(this.getManagedBean().getDirection())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("direction",
							SearchOperator.OPERATOR_EQUALS.value(), this
							.getManagedBean().getDirection()
							.toUpperCase(), true));
		}*/

		List<ProcessCodeType> listOfProcessCodes = new ArrayList<ProcessCodeType>();

		listOfProcessCodes.add(ProcessCodeType.GLBAC);
		listOfProcessCodes.add(ProcessCodeType.GLSEG);
		listOfProcessCodes.add(ProcessCodeType.GLVOT);
		listOfProcessCodes.add(ProcessCodeType.GLDNA);
		listOfProcessCodes.add(ProcessCodeType.GLPRG);
		listOfProcessCodes.add(ProcessCodeType.GLPRJ);
		listOfProcessCodes.add(ProcessCodeType.GLGLC);
		listOfProcessCodes.add(ProcessCodeType.GLPCG);
		listOfProcessCodes.add(ProcessCodeType.GLPTJ);
		listOfProcessCodes.add(ProcessCodeType.CMBNK);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("processId", SearchOperator.OPERATOR_IN
						.value(), listOfProcessCodes));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("processStatus", SearchOperator.OPERATOR_EQUALS
						.value(), new Long(0)));

		searchBean.getDataFilterBeans()
		.add(new SearchMultiFilterBean(new SearchFilterBean(
				"targetSystem", SearchOperator.OPERATOR_EQUALS.value(),
				"ePerolehan"), new SearchFilterBean("fileName",
						SearchOperator.OPERATOR_NOT_LIKE.value(), ".txt",
						SearchCondition.CONDITION_AND.value())));

		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchFilterBean("fileName",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), null),
						new SearchFilterBean("fileName",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								"N/A", SearchCondition.CONDITION_AND.value())));

		// this search criteria used to get the parent records with mapping of
		// all NULL child records
		SearchFilterBean progAcctgFilterBean = new SearchFilterBean(
				"pmInterfaceProgAct.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean projectFilterBean = new SearchFilterBean(
				"pmInterfaceProject.dIInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean glAcntFilterBean = new SearchFilterBean(
				"pmInterfaceGlAccount.dIInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean agOffFilterBean = new SearchFilterBean(
				"pmInterfaceAgOff.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean bankFilterBean = new SearchFilterBean(
				"pmInterfaceBank.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean kptjFilterBean = new SearchFilterBean(
				"pmInterfaceKptj.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean ptjFilterBean = new SearchFilterBean(
				"pmInterfacePtj.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean ppFilterBean = new SearchFilterBean(
				"pmInterfacePP.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean votFilterBean = new SearchFilterBean(
				"pmInterfaceVot.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean danaFilterBean = new SearchFilterBean(
				"pmInterfaceDana.dIInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null,
				SearchCondition.CONDITION_OR.value());

		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(progAcctgFilterBean,SearchCondition.CONDITION_AND.value(),
						new SearchMultiFilterBean(projectFilterBean,SearchCondition.CONDITION_OR.value(),
								new SearchMultiFilterBean(glAcntFilterBean,SearchCondition.CONDITION_OR.value(),
										new SearchMultiFilterBean(agOffFilterBean,SearchCondition.CONDITION_OR.value(),
												new SearchMultiFilterBean(bankFilterBean,SearchCondition.CONDITION_OR.value(),
														new SearchMultiFilterBean(kptjFilterBean,SearchCondition.CONDITION_OR.value(),
																new SearchMultiFilterBean(ptjFilterBean,SearchCondition.CONDITION_OR.value(),
																		new SearchMultiFilterBean(ppFilterBean,SearchCondition.CONDITION_OR.value(),
																				new SearchMultiFilterBean(votFilterBean,SearchCondition.CONDITION_OR.value(),
																						danaFilterBean))))))))));

		if (StringUtils.isNotEmpty(this.getManagedBean().getProcessId())) {
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(new SearchFilterBean("processId",
							SearchOperator.OPERATOR_EQUALS.value(), this
							.getManagedBean().getProcessId()
							.toUpperCase()), new SearchFilterBean(
									"fileName", SearchOperator.OPERATOR_LIKE.value(),
									"1000"
											+ this.getManagedBean().getProcessId()
											.toUpperCase() + "400001")));
		}

		searchBean.getSortColumns().add(
				new SearchOrderBean("createdDate", true));


		return searchBean;
	}


	/**
	 * The Class SearchGfMasPagingDataModel.
	 */
	private class SearchGfMasPagingDataModel extends
	PagingDataModel<DIInterfaceLog> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search gf mas paging data model.
		 */
		public SearchGfMasPagingDataModel() {
			super("gfMas", getManagedBean().isLazy());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<DIInterfaceLog> pageChangeListener(
				ISearchBean<DIInterfaceLog> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IDIInterfaceSO gfMasSO;

			try {
				ISearchBean<DIInterfaceLog> searchBeanData = getSearchBeanData(searchBean);

				InterfaceLogReqDto gfMasReqDTO = new InterfaceLogReqDto();

				gfMasReqDTO.setViewSearchBean(searchBeanData);

				gfMasSO = (IDIInterfaceSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IDIInterfaceSO.class);

				InterfaceLogResDto resDTO = gfMasSO
						.retrieveInterfaceLogDetaisls(gfMasReqDTO);
				System.out.println("getTotalRecord:"+resDTO.getViewGfMasResultBean().getTotalRecord());
				getManagedBean().setLazy(true);

				if (resDTO.isSuccessful()) {

					return resDTO.getViewGfMasResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	/**
	 * Gets the status options.
	 * 
	 * @return the status options
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getCommonStatusOptions();
	}

	/**
	 * Go to gf mas process maintaince page.
	 * 
	 * @param selectedProcess
	 *            the selected process
	 * @return the string
	 */
	public String goToGfMasProcessMaintaincePage(DIInterfaceLog selectedProcess) {
		log.info("In goToGfMasProcessMaintaincePage");

		String childProcessPage = null;
		
		this.getManagedBean().setSelectedProcess(selectedProcess);
		this.getManagedBean().setFileName(selectedProcess.getFileName());
		this.getManagedBean().setTransId(selectedProcess.getTransId());
		this.getManagedBean().setMapOfProcess(null);
		getUnprocessedRecordsByPreviousDate(selectedProcess);
		
		return childProcessPage;
	}
		
	
	
	/**
	 * Gets the interface prog act process status.
	 * 
	 * @param progActList
	 *            the prog act list
	 * @return the interface prog act process status
	 */
	private boolean getInterfaceProgActProcessStatus(
			List<PmInterfaceProgAct> progActList) {
		log.info("getInterfaceProgActProcessStatus");
		if (CollectionUtils.isNotEmpty(progActList)) {
			for (int i = 0; progActList.size() > 0; i++) {
				if (progActList.get(i).getProcessStatus().equals(new Long(0))
						|| progActList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| progActList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}
	
	/**
	 * Gets the interface project process status.
	 * 
	 * @param projectList
	 *            the project list
	 * @return the interface project process status
	 */
	private boolean getInterfaceProjectProcessStatus(
			List<PmInterfaceProject> projectList) {
		log.info("getInterfaceProjectProcessStatus");
		if (CollectionUtils.isNotEmpty(projectList)) {
			for (int i = 0; projectList.size() > 0; i++) {
				if (projectList.get(i).getProcessStatus().equals(new Long(0))
						|| projectList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| projectList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	
	/**
	 * Gets the interface ptj process status.
	 * 
	 * @param ptjList
	 *            the ptj list
	 * @return the interface ptj process status
	 */
	private boolean getInterfacePtjProcessStatus(List<PmInterfacePtj> ptjList) {
		log.info("getInterfacePtjProcessStatus");
		if (CollectionUtils.isNotEmpty(ptjList)) {
			for (int i = 0; ptjList.size() > 0; i++) {
				if (ptjList.get(i).getProcessStatus().equals(new Long(0))
						|| ptjList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| ptjList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	/**
	 * Gets the interface kptj process status.
	 * 
	 * @param kptjList
	 *            the kptj list
	 * @return the interface kptj process status
	 */
	private boolean getInterfaceKptjProcessStatus(List<PmInterfaceKptj> kptjList) {
		log.info("getInterfaceKptjProcessStatus");
		if (CollectionUtils.isNotEmpty(kptjList)) {
			for (int i = 0; kptjList.size() > 0; i++) {
				if (kptjList.get(i).getProcessStatus().equals(new Long(0))
						|| kptjList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| kptjList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	/**
	 * Gets the interface ag office process status.
	 * 
	 * @param agOffList
	 *            the ag off list
	 * @return the interface ag office process status
	 */
	private boolean getInterfaceAgOfficeProcessStatus(
			List<PmInterfaceAgOff> agOffList) {
		log.info("getInterfaceAgOfficeProcessStatus");
		if (CollectionUtils.isNotEmpty(agOffList)) {
			for (int i = 0; agOffList.size() > 0; i++) {
				if (agOffList.get(i).getProcessStatus().equals(new Long(0))
						|| agOffList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| agOffList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	
	/**
	 * Gets the interface pp process status.
	 * 
	 * @param ppList
	 *            the pp list
	 * @return the interface pp process status
	 */
	private boolean getInterfacePpProcessStatus(List<PmInterfacePP> ppList) {
		log.info("getInterfacePpProcessStatus");
		if (CollectionUtils.isNotEmpty(ppList)) {
			for (int i = 0; ppList.size() > 0; i++) {
				if (ppList.get(i).getProcessStatus().equals(new Long(0))
						|| ppList.get(i).getProcessStatus().equals(new Long(1))
						|| ppList.get(i).getProcessStatus().equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}
	
	/**
	 * Gets the interface vot process status.
	 * 
	 * @param votList
	 *            the vot list
	 * @return the interface vot process status
	 */
	private boolean getInterfaceVotProcessStatus(List<PmInterfaceVot> votList) {
		log.info("getInterfaceVotProcessStatus");
		if (CollectionUtils.isNotEmpty(votList)) {
			for (int i = 0; votList.size() > 0; i++) {
				if (votList.get(i).getProcessStatus().equals(new Long(0))
						|| votList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| votList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}

		}
		return childProcessStatus;
	}

	
	/**
	 * Gets the interface bank process status.
	 * 
	 * @param bankList
	 *            the bank list
	 * @return the interface bank process status
	 */
	private boolean getInterfaceBankProcessStatus(List<PmInterfaceBank> bankList) {

		log.info("getInterfaceBankProcessStatus");

		if (CollectionUtils.isNotEmpty(bankList)) {
			for (int i = 0; bankList.size() > 0; i++) {
				if (bankList.get(i).getProcessStatus().equals(new Long(0))
						|| bankList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| bankList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}
	
	/**
	 * Gets the interface gl acnt process status.
	 * 
	 * @param glAcntList
	 *            the gl acnt list
	 * @return the interface gl acnt process status
	 */
	private boolean getInterfaceGlAcntProcessStatus(
			List<PmInterfaceGlAccount> glAcntList) {
		log.info("getInterfaceGlAcntProcessStatus");
		if (CollectionUtils.isNotEmpty(glAcntList)) {
			for (int i = 0; glAcntList.size() > 0; i++) {
				if (glAcntList.get(i).getProcessStatus().equals(new Long(0))
						|| glAcntList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| glAcntList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	/**
	 * Gets the interface dana process status.
	 * 
	 * @param danaList
	 *            the dana list
	 * @return the interface dana process status
	 */
	private boolean getInterfaceDanaProcessStatus(List<PmInterfaceDana> danaList) {
		log.info("getInterfaceDanaProcessStatus");
		if (CollectionUtils.isNotEmpty(danaList)) {
			for (int i = 0; danaList.size() > 0; i++) {

				if (danaList.get(i).getProcessStatus().equals(new Long(0))
						|| danaList.get(i).getProcessStatus()
								.equals(new Long(1))
						|| danaList.get(i).getProcessStatus()
								.equals(new Long(2))) {
					childProcessStatus = true;
					break;
				} else {
					childProcessStatus = false;
				}
			}
		}
		return childProcessStatus;
	}

	/**
	 * Gets the unprocessed records by previous date.
	 * 
	 * @param selectedProcess
	 *            the selected process
	 * @return the unprocessed records by previous date
	 */
	private void getUnprocessedRecordsByPreviousDate(
			DIInterfaceLog selectedProcess) {

		List<DIInterfaceLog> interfaceLogParentList = searchGfMas.getList();

		List<DIInterfaceLog> interfaceLogParentFilteredList = null;

		FacesContext fc = FacesContext.getCurrentInstance();

		Map<String, List<DIInterfaceLog>> mapOfProcess = this.getManagedBean()
				.getMapOfProcess();

		if (CollectionUtils.isNotEmpty(interfaceLogParentList)) {

			for (DIInterfaceLog allRecords : interfaceLogParentList) {
				String processId = allRecords.getProcessId();

				if (!mapOfProcess.containsKey(processId)) {
					mapOfProcess
							.put(processId, new ArrayList<DIInterfaceLog>());
				}
				mapOfProcess.get(processId).add(allRecords);
			}
		}
		interfaceLogParentFilteredList = mapOfProcess.get(selectedProcess
				.getProcessId());

		/*
		 * String logId = this.getManagedBean().getMapfPross()
		 * .get(selectedProcess.getProcessId()).get(0).getLogId() .toString();
		 */

		String processId = this.getManagedBean().getMapOfProcess()
				.get(selectedProcess.getProcessId()).get(0).getProcessId();
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String createdDate = dateFormat.format(this.getManagedBean()
				.getMapOfProcess().get(selectedProcess.getProcessId()).get(0)
			.getCreatedDate());	

		if (CollectionUtils.isNotEmpty(interfaceLogParentFilteredList)) {

			for (DIInterfaceLog filteredProcessCode : interfaceLogParentFilteredList) {

				if (filteredProcessCode.getProcessStatus() != null
						&& filteredProcessCode.getProcessStatus().equals(
								new Long(0))) {

					switch (ProcessCodeType.valueOf(filteredProcessCode
							.getProcessId())) {

					case GLPRG:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_PRGACT);
						this.getManagedBean().getSearchProgAct().reloadData();
						if (getInterfaceProgActProcessStatus(this
								.getManagedBean().getSearchProgAct().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLBAC:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_AGOFFICE);
						this.getManagedBean().getSearchAgOff().reloadData();
						if (getInterfaceAgOfficeProcessStatus(this
								.getManagedBean().getSearchAgOff().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLSEG:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_PP);
						this.getManagedBean().getSearchPp().reloadData();
						if (getInterfacePpProcessStatus(this.getManagedBean()
								.getSearchPp().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLVOT:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_VOT);
						this.getManagedBean().getSearchVot().reloadData();
						if (getInterfaceVotProcessStatus(this.getManagedBean()
								.getSearchVot().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLDNA:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_DANA);
						this.getManagedBean().getSearchDana().reloadData();
						if (getInterfaceDanaProcessStatus(this.getManagedBean()
								.getSearchDana().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLPRJ:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_PROJECT);
						this.getManagedBean().getSearchProject().reloadData();
						if (getInterfaceProjectProcessStatus(this
								.getManagedBean().getSearchProject().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLGLC:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_GLACCOUNT);
						this.getManagedBean().getSearchGlAccount().reloadData();
						if (getInterfaceGlAcntProcessStatus(this
								.getManagedBean().getSearchGlAccount()
								.getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLPCG:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_KPTJ);
						this.getManagedBean().getSearchKptj().reloadData();
						if (getInterfaceKptjProcessStatus(this.getManagedBean()
								.getSearchKptj().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case GLPTJ:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_PTJ);
						this.getManagedBean().getSearchPtj().reloadData();
						if (getInterfacePtjProcessStatus(this.getManagedBean()
								.getSearchPtj().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					case CMBNK:
						fc.getApplication().getNavigationHandler()
								.handleNavigation(fc, null, NAV_TO_BANK);
						this.getManagedBean().getSearchBank().reloadData();
						if (getInterfaceBankProcessStatus(this.getManagedBean()
								.getSearchBank().getList())) {
							showErrorMsg(selectedProcess, processId,
									createdDate, filteredProcessCode, fc);
						}
						break;
					}
				}
			}

		}

	}

	/**
	 * Show error msg.
	 * 
	 * @param selectedProcess
	 *            the selected process
	 * @param processId
	 *            the process id
	 * @param createdDate
	 *            the created date
	 * @param filteredProcessCode
	 *            the filtered process code
	 * @param fc
	 *            the fc
	 */
	private void showErrorMsg(DIInterfaceLog selectedProcess,
			String processId, String createdDate,
			DIInterfaceLog filteredProcessCode,FacesContext fc) {
		if (!selectedProcess.getLogId().equals(filteredProcessCode.getLogId())) {
			if (!(selectedProcess.getCreatedDate().compareTo(
					filteredProcessCode.getCreatedDate())<0)) {
				fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);			
				log.info("Label--->"+this.getManagedBean().getProcessLabels().get(processId));
				throw new EpException(MessageConst.ERROR_780001, new String[] {
						this.getManagedBean().getProcessLabels().get(processId),createdDate });
			}
		}
	}

	/**
	 * Redirect gf mas search.
	 * 
	 * @return the string
	 */
	public String redirectGfMasSearch() {

		log.info("In redirectGfMasSearch");

		this.getManagedBean().setSelectedProgActList(null);
		this.getManagedBean().setSelectedProjectList(null);
		this.getManagedBean().setSelectedBankList(null);
		this.getManagedBean().setSelectedGlAcntList(null);
		this.getManagedBean().setSelectedKptjList(null);
		this.getManagedBean().setSelectedAgOfficeList(null);
		this.getManagedBean().setSelectedPtjList(null);
		this.getManagedBean().setSelectedDanaList(null);
		this.getManagedBean().setSelectedPpList(null);
		this.getManagedBean().setSelectedVotList(null);
		this.getManagedBean().setProcessButtonStatus(false);

		return "/xhtml/gfMas/gfmassearch/gfmasSearchMain.xhtml?faces-redirect=true";

	}

	/**
	 * Process all vendors.
	 * 
	 * @return the string
	 */
	public String processAllVendors() {

		log.info("processAllVendors");
		String returnUrl = null;

		if (this.getManagedBean().getSelectedProgActList() != null) {
			log.info("***************ProgActList***SIZE******************>>"
					+ this.getManagedBean().getSelectedProgActList().size());


			IInterfacePrgActSO interfaceprgActSO = (IInterfacePrgActSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfacePrgActSO.class);
			InterfacePrgActReqDTO reqDTO = new InterfacePrgActReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfacePrgActLs(this.getManagedBean().getSelectedProgActList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceprgActSO.saveOrUpdatePrgActList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PRGACT;
			}




		} else if (this.getManagedBean().getSelectedProjectList() != null) {
			log.info("************ProjectList******SIZE******************>>"
					+ this.getManagedBean().getSelectedProjectList().size());

			IInterfaceProjectSO interfaceprojectActSO = (IInterfaceProjectSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceProjectSO.class);


			InterfaceProjectReqDTO reqDTO = new InterfaceProjectReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceProjectLs(this.getManagedBean().getSelectedProjectList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceprojectActSO.saveOrUpdateProjectList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PROJECT ;
			}


		} else if (this.getManagedBean().getSelectedAgOfficeList() != null) {

			log.info("***********AgOfficeList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedAgOfficeList().size());

			IInterfaceAgOffSO reqSO = (IInterfaceAgOffSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceAgOffSO.class);

			List<PmInterfaceAgOff> list = this.getManagedBean()
					.getSelectedAgOfficeList();

			InterfaceAgOffReqDTO reqDTO = new InterfaceAgOffReqDTO();
			reqDTO.setInterfaceAgOffLs(list);

			try {
				reqSO.saveOrUpdateInterfaceAgOffList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_AGOFFICE ;
			
			}

		} else if (this.getManagedBean().getSelectedBankList() != null) {

			log.info("************BankList******SIZE******************>>"
					+ this.getManagedBean().getSelectedBankList().size());

			InterfaceCmBankReqDTO reqDTO = new InterfaceCmBankReqDTO();

			reqDTO.setInterfaceBankLs(this.getManagedBean().getSelectedBankList());
			reqDTO.setFileName(this.getManagedBean().getFileName());

			try {

				IInterfaceCmBankSO reqSO = (IInterfaceCmBankSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfaceCmBankSO.class);

				reqSO.saveOrUpdateInterfaceBankList(reqDTO);

			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_BANK ;
			
			}

		} else if (this.getManagedBean().getSelectedGlAcntList() != null) {
			log.info("***********GlAcntList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedGlAcntList().size());
			
			IInterfaceGlAccSO interfaceGlAccSO = (IInterfaceGlAccSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceGlAccSO.class);


			InterfaceGlAccReqDTO reqDTO = new InterfaceGlAccReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceGlAccLs(this.getManagedBean().getSelectedGlAcntList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceGlAccSO.saveOrUpdateGlAccList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_GLACCOUNT ;
			}
			
		} else if (this.getManagedBean().getSelectedKptjList() != null) {
			log.info("************KptjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedKptjList().size());
	        InterfaceKptjReqDTO reqDTO = new InterfaceKptjReqDTO();
			
			reqDTO.setInterfaceKptjLs(this.getManagedBean().getSelectedKptjList());
			reqDTO.setFileName(this.getManagedBean().getFileName());
			
			try {
				
				IInterfaceKptjSO reqSO = (IInterfaceKptjSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfaceKptjSO.class);

				reqSO.saveOrUpdateInterfaceKptjList(reqDTO);
				
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_KPTJ ;
			}
			
			this.getManagedBean().setSelectedPtjList(null);
		} else if (this.getManagedBean().getSelectedPtjList() != null) {

			log.info("************PtjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPtjList().size());

			InterfacePtjReqDTO reqDTO = new InterfacePtjReqDTO();

			reqDTO.setInterfacePtjLs(this.getManagedBean().getSelectedPtjList());
			reqDTO.setFileName(this.getManagedBean().getFileName());

			try {

				IInterfacePtjSO reqSO = (IInterfacePtjSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfacePtjSO.class);

				reqSO.saveOrUpdateInterfacePtjList(reqDTO);

			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PTJ ;
			
			}

		} else if (this.getManagedBean().getSelectedDanaList() != null) {

			log.info("***********DanaList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedDanaList().size());

			IInterfaceVotDanaSO interfaceVotDanaSO = (IInterfaceVotDanaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceVotDanaSO.class);

			InterfaceVotDanaReqDTO reqDTO = new InterfaceVotDanaReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceDanaLs(this.getManagedBean().getSelectedDanaList());
			reqDTO.setTransId(this.getManagedBean().getTransId());
			try {
				interfaceVotDanaSO.saveOrUpdateVotDanaList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_DANA;
			}
			
		} else if (this.getManagedBean().getSelectedPpList() != null) {

			log.info("************PpList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPpList().size());

			InterfacePpReqDTO reqDTO = new InterfacePpReqDTO();

			reqDTO.setInterfacePpLs(this.getManagedBean().getSelectedPpList());
			reqDTO.setFileName(this.getManagedBean().getFileName());

			try {

				IInterfacePpSO reqSO = (IInterfacePpSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfacePpSO.class);

				reqSO.saveOrUpdateInterfacePpList(reqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (this.getManagedBean().getSelectedVotList() != null) {
			log.info("***********VotList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedVotList().size());

			IInterfaceVotSO interfacevotSO = (IInterfaceVotSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceVotSO.class);

			InterfaceVotReqDTO reqDTO = new InterfaceVotReqDTO();
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			reqDTO.setInterfaceVotLs(this.getManagedBean().getSelectedVotList());
			try {
				interfacevotSO.saveOrUpdateVotFundList(reqDTO);
			} catch (Exception e) {


				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_VOT;
			}
		}
		returnUrl = NAV_TO_MAIN;
		if (getSelectedAllProcessListStatus()) {
			returnUrl = NAV_TO_MAIN;
			PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
					FacesMessage.SEVERITY_INFO, MessageConst.INFO_780002, null);
		} 
		return returnUrl;
	}

	/**
	 * Gets the selected all process list.
	 * 
	 * @return the selected all process list
	 */
	private boolean getSelectedAllProcessListStatus() {

		this.getManagedBean().setProcessButtonStatus(false);

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getSelectedProgActList())
				|| CollectionUtils.isNotEmpty(this.getManagedBean()
						.getSelectedProjectList())
						|| CollectionUtils.isNotEmpty(this.getManagedBean()
								.getSelectedAgOfficeList())
								|| CollectionUtils.isNotEmpty(this.getManagedBean()
										.getSelectedBankList())
										|| CollectionUtils.isNotEmpty(this.getManagedBean()
												.getSelectedGlAcntList())
												|| CollectionUtils.isNotEmpty(this.getManagedBean()
														.getSelectedKptjList())
														|| CollectionUtils.isNotEmpty(this.getManagedBean()
																.getSelectedPpList())
																|| CollectionUtils.isNotEmpty(this.getManagedBean()
																		.getSelectedDanaList())
																		|| CollectionUtils.isNotEmpty(this.getManagedBean()
																				.getSelectedPtjList())
																				|| CollectionUtils.isNotEmpty(this.getManagedBean()
																						.getSelectedVotList())) {
			this.getManagedBean().setProcessButtonStatus(true);

		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		return this.getManagedBean().isProcessButtonStatus();
	}

	/**
	 * Row select event.
	 * 
	 * @param e
	 *            the e
	 */
	public void rowSelectEvent(AjaxBehaviorEvent e) {

		getSelectedAllProcessListStatus();

	}
	/*public void selectPrgActEvent(PmInterfaceProgAct pmInterfaceProgAct) {
		 this.getManagedBean().setSelectedProgActList(new ArrayList<PmInterfaceProgAct>());
		if (pmInterfaceProgAct.isSelected()) {
			this.getManagedBean().getSelectedProgActList()
					.add(pmInterfaceProgAct);
		}else{
			this.getManagedBean().getSelectedProgActList().remove(pmInterfaceProgAct);
		}
	}*/
	
	

	/**
	 * Gets the search gf mas.
	 * 
	 * @return the searchGfMas
	 */
	public IPagingDataModel<DIInterfaceLog> getSearchGfMas() {
		return searchGfMas;
	}

	/**
	 * Sets the search gf mas.
	 * 
	 * @param searchGfMas
	 *            the searchGfMas to set
	 */
	public void setSearchGfMas(IPagingDataModel<DIInterfaceLog> searchGfMas) {
		this.searchGfMas = searchGfMas;
	}

	/**
	 * Change process code.
	 */
	public void changeProcessCode() {
		searchGfMas();
	}

	/**
	 * Gets the process codelist.
	 * 
	 * @return the process codelist
	 */
	/*public List<SelectItem> getProcessCodelist() {
		log.info("In getProcessCodelist");

		List<ProcessCodeType> list = new ArrayList<ProcessCodeType>();

		List<SelectItem> processCodeValues = new ArrayList<SelectItem>();

		list.add(ProcessCodeType.GLBAC);
		list.add(ProcessCodeType.GLSEG);
		list.add(ProcessCodeType.GLVOT);
		list.add(ProcessCodeType.GLDNA);
		list.add(ProcessCodeType.GLPRG);
		list.add(ProcessCodeType.GLPRJ);
		list.add(ProcessCodeType.GLGLC);
		list.add(ProcessCodeType.GLPCG);
		list.add(ProcessCodeType.GLPTJ);
		list.add(ProcessCodeType.CMBNK);

		for (int i = 0; i < list.size(); i++) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(list.get(i));
			selectItem.setLabel(list.get(i).toString());

			processCodeValues.add(selectItem);
		}

		return processCodeValues;
	}*/
	
	public SelectItem[] getProcessCodelist() {

		SelectItem[] items = new SelectItem[10];

		Map<String, String> processLabels = new HashMap<String, String>();

		items[0] = new SelectItem("GLBAC",
				ResourceBundleUtils.getMessage("lbl.gfmas.glbacdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[1] = new SelectItem("GLSEG",
				ResourceBundleUtils.getMessage("lbl.gfmas.glsegdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[2] = new SelectItem("GLVOT",
				ResourceBundleUtils.getMessage("lbl.gfmas.glvotdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[3] = new SelectItem("GLDNA",
				ResourceBundleUtils.getMessage("lbl.gfmas.gldanadef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[4] = new SelectItem("GLPRG",
				ResourceBundleUtils.getMessage("lbl.gfmas.glprgdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[5] = new SelectItem("GLPRJ",
				ResourceBundleUtils.getMessage("lbl.gfmas.glprjdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[6] = new SelectItem("GLGLC",
				ResourceBundleUtils.getMessage("lbl.gfmas.glglcdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[7] = new SelectItem("GLPCG",
				ResourceBundleUtils.getMessage("lbl.gfmas.glpcgdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[8] = new SelectItem("GLPTJ",
				ResourceBundleUtils.getMessage("lbl.gfmas.glptjdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		items[9] = new SelectItem("CMBNK",
				ResourceBundleUtils.getMessage("lbl.gfmas.glbnkdef",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));

		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				String processIdLabel = items[i].getLabel().trim();
				if (!processLabels.containsKey(processIdLabel)) {
					processLabels.put(items[i].getValue().toString(),processIdLabel);
				}
			}
		}
		this.getManagedBean().setProcessLabels(processLabels);

		return items;
	}
}
