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

import java.util.ArrayList;
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

	/** The search gf mas. */
	private IPagingDataModel<DIInterfaceLog> searchGfMas = new SearchGfMasPagingDataModel();

	private boolean childProcessStatus = false;

	/**
	 * Instantiates a new gf mas backing bean.
	 */
	public GfMasBackingBean() {

		log.info("inside GfMasBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

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
		this.resetGfMasSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
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
		if (this.getManagedBean().getInterfaceLogId() != null) {

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
		}

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
		IDIInterfaceSO gfMasSO = (IDIInterfaceSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IDIInterfaceSO.class);

		String childProcessPage = null;
		this.getManagedBean().setMapOfProcess(null);

		try {
			InterfaceLogReqDto interfaceGfMasReqDto = new InterfaceLogReqDto();
			interfaceGfMasReqDto.setdIInterfaceLog(selectedProcess);
			interfaceGfMasReqDto.setLogId(selectedProcess.getLogId());
			interfaceGfMasReqDto.setProcessId(selectedProcess.getProcessId());			

			this.getManagedBean().setFileName(selectedProcess.getFileName());
			this.getManagedBean().setTransId(selectedProcess.getTransId());

			InterfaceLogResDto interfaceGfMasResDto = gfMasSO
					.retrieveProcessDataForAll(interfaceGfMasReqDto);

			getUnprocessedRecordsByPreviousDate(selectedProcess,interfaceGfMasResDto);

			childProcessPage = getChildProcessScreen(selectedProcess,
					interfaceGfMasResDto);

		} /*catch (Exception e) {
			e.printStackTrace();
		}*/ finally {
			gfMasSO = null;
		}

		return childProcessPage;
	}

	/**
	 * Gets the interface prog act process status.
	 * 
	 * @param log
	 *            the log
	 * @return the interface prog act process status
	 */
	private boolean getInterfaceProgActProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceProgAct> progActList=resDTO.getListOfPmInterfacePrgAct();

		if (CollectionUtils.isNotEmpty(progActList)) {

			for (int i = 0; progActList.size() > 0; i++) {

				if (progActList.get(i).getProcessStatus().equals(new Long(0))
						|| progActList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface project process status
	 */
	private boolean getInterfaceProjectProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceProject> projectList=resDTO.getListOfPmInterfaceProject();

		if (CollectionUtils.isNotEmpty(projectList)) {

			for (int i = 0; projectList.size() > 0; i++) {

				if (projectList.get(i).getProcessStatus().equals(new Long(0))
						|| projectList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface ptj process status
	 */
	private boolean getInterfacePtjProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfacePtj> ptjList=resDTO.getListOfPmInterfacePtj();

		if (CollectionUtils.isNotEmpty(ptjList)) {

			for (int i = 0; ptjList.size() > 0; i++) {

				if (ptjList.get(i).getProcessStatus().equals(new Long(0))
						|| ptjList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface kptj process status
	 */
	private boolean getInterfaceKptjProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceKptj> kptjList=resDTO.getListOfPmInterfaceKptj();

		if (CollectionUtils.isNotEmpty(kptjList)) {

			for (int i = 0; kptjList.size() > 0; i++) {

				if (kptjList.get(i).getProcessStatus().equals(new Long(0))
						|| kptjList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface ag office process status
	 */
	private boolean getInterfaceAgOfficeProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceAgOff> agOffList = resDTO
				.getListOfPmInterfaceAgOffice();

		if (CollectionUtils.isNotEmpty(agOffList)) {

			for (int i = 0; agOffList.size() > 0; i++) {

				if (agOffList.get(i).getProcessStatus().equals(new Long(0))
						|| agOffList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface pp process status
	 */
	private boolean getInterfacePpProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfacePP> ppList = resDTO.getListOfPmInterfacePP();

		if (CollectionUtils.isNotEmpty(ppList)) {

			for (int i = 0; ppList.size() > 0; i++) {

				if (ppList.get(i).getProcessStatus().equals(new Long(0))
						|| ppList.get(i).getProcessStatus().equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface vot process status
	 */
	private boolean getInterfaceVotProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceVot> votList=resDTO.getListOfPmInterfaceVot();

		if (CollectionUtils.isNotEmpty(votList)) {

			for (int i = 0; votList.size() > 0; i++) {

				if (votList.get(i).getProcessStatus()
						.equals(new Long(0))
						|| votList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface bank process status
	 */
	private boolean getInterfaceBankProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceBank> bankList=resDTO.getListOfPmInterfaceBank();

		if (CollectionUtils.isNotEmpty(bankList)) {

			for (int i = 0; bankList.size() > 0; i++) {

				if (bankList.get(i).getProcessStatus().equals(new Long(0))
						|| bankList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface gl acnt process status
	 */
	private boolean getInterfaceGlAcntProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceGlAccount> glAcntList=resDTO.getListOfPmInterfaceGlAccount();

		if (CollectionUtils.isNotEmpty(glAcntList)) {

			for (int i = 0; glAcntList.size() > 0; i++) {

				if (glAcntList.get(i).getProcessStatus().equals(new Long(0))
						|| glAcntList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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
	 * @param log
	 *            the log
	 * @return the interface dana process status
	 */
	private boolean getInterfaceDanaProcessStatus(InterfaceLogResDto resDTO) {

		//boolean childProcessStatus = false;

		List<PmInterfaceDana> danaList = resDTO.getListOfPmInterfaceDana();

		if (CollectionUtils.isNotEmpty(danaList)) {

			for (int i = 0; danaList.size() > 0; i++) {

				if (danaList.get(i).getProcessStatus().equals(new Long(0))
						|| danaList.get(i).getProcessStatus()
						.equals(new Long(1))) {
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

			DIInterfaceLog selectedProcess,InterfaceLogResDto resDto) {

		List<DIInterfaceLog> interfaceLogParentList = searchGfMas.getList();

		List<DIInterfaceLog> interfaceLogParentFilteredList = null;

		// Map<String, List<DIInterfaceLog>> mapOfProcess = new HashMap<String,
		// List<DIInterfaceLog>>();
		Map<String, List<DIInterfaceLog>> mapOfProcess = this.getManagedBean()
				.getMapOfProcess();

		/*
		 * Collections.sort(interfaceLogParentList, new
		 * Comparator<DIInterfaceLog>() { public int compare(DIInterfaceLog o1,
		 * DIInterfaceLog o2) { return o1.getCreatedDate().compareTo(
		 * o2.getCreatedDate()); } });
		 * 
		 * Collections.sort(interfaceLogParentList, Collections.reverseOrder());
		 */

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

		String logId = this.getManagedBean().getMapOfProcess()
				.get(selectedProcess.getProcessId()).get(0).getLogId()
				.toString();

		String processId = this.getManagedBean().getMapOfProcess()
				.get(selectedProcess.getProcessId()).get(0).getProcessId();

		String createdDate = this.getManagedBean().getMapOfProcess()
				.get(selectedProcess.getProcessId()).get(0).getCreatedDate()
				.toString();

		if (CollectionUtils.isNotEmpty(interfaceLogParentFilteredList)) {

			for (DIInterfaceLog filteredProcessCode : interfaceLogParentFilteredList) {

				if (filteredProcessCode.getProcessStatus()!=null && filteredProcessCode.getProcessStatus().equals(new Long(0))) {

					switch (ProcessCodeType.valueOf(filteredProcessCode
							.getProcessId())) {

							case GLPRG:
								if (getInterfaceProgActProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLBAC:
								if (getInterfaceAgOfficeProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLSEG:
								if (getInterfacePpProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLVOT:
								if (getInterfaceVotProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}					
							case GLDNA:
								if (getInterfaceDanaProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLPRJ:
								if (getInterfaceProjectProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}					

							case GLGLC:
								if (getInterfaceGlAcntProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLPCG:
								if (getInterfaceKptjProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case GLPTJ:
								if (getInterfacePtjProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}						

							case CMBNK:
								if (getInterfaceBankProcessStatus(resDto)) {
									showErrorMsg(selectedProcess, logId, processId,
											createdDate, filteredProcessCode);
								}		

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
	 * @param logId
	 *            the log id
	 * @param processId
	 *            the process id
	 * @param createdDate
	 *            the created date
	 * @param filteredProcessCode
	 *            the filtered process code
	 */
	private void showErrorMsg(DIInterfaceLog selectedProcess, String logId,
			String processId, String createdDate,
			DIInterfaceLog filteredProcessCode) {

		if (!selectedProcess.getLogId().equals(filteredProcessCode.getLogId())) {
			if (!(selectedProcess.getCreatedDate().compareTo(
					filteredProcessCode.getCreatedDate())<0)) {
				throw new EpException(MessageConst.ERROR_780001, new String[] {
						processId,logId,createdDate });
			}
		}
	}


	/**
	 * Gets the child process screen.
	 * 
	 * @param interfaceLog
	 *            the interface log
	 * @param interfaceGfMasResDto
	 *            the interface gf mas res dto
	 * @return the child process screen
	 */
	private String getChildProcessScreen(DIInterfaceLog interfaceLog,
			InterfaceLogResDto interfaceGfMasResDto) {
		log.info("In getChildProcessScreen");

		switch (ProcessCodeType.valueOf(interfaceLog.getProcessId())) {

		case GLPRG:
			if (getInterfaceProgActProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfPrgAct(
						interfaceGfMasResDto.getListOfPmInterfacePrgAct());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPrgAct.xhtml";
			}
			break;
		case GLBAC:
			if (getInterfaceAgOfficeProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfAgOffice(
						interfaceGfMasResDto.getListOfPmInterfaceAgOffice());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasAgOffice.xhtml";
			}
			break;
		case GLVOT:
			if (getInterfaceVotProcessStatus(interfaceGfMasResDto)) {

				this.getManagedBean().setListOfVot(
						interfaceGfMasResDto.getListOfPmInterfaceVot());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasVot.xhtml";
			}
			break;
		case GLDNA:
			if (getInterfaceDanaProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfDana(
						interfaceGfMasResDto.getListOfPmInterfaceDana());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasDana.xhtml";
			}
			break;
		case GLPRJ:
			if (getInterfaceProjectProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfProjectt(
						interfaceGfMasResDto.getListOfPmInterfaceProject());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasProject.xhtml";
			}
			break;
		case GLGLC:
			if (getInterfaceGlAcntProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfGlAccount(
						interfaceGfMasResDto.getListOfPmInterfaceGlAccount());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasGlAccount.xhtml";
			}
			break;
		case GLPCG:
			if (getInterfaceKptjProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfKPTJ(
						interfaceGfMasResDto.getListOfPmInterfaceKptj());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasKPTJ.xhtml";
			}
			break;
		case GLPTJ:
			if (getInterfacePtjProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfPTJ(
						interfaceGfMasResDto.getListOfPmInterfacePtj());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPTJ.xhtml";
			}
			break;
		case CMBNK:
			if (getInterfaceBankProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfBank(
						interfaceGfMasResDto.getListOfPmInterfaceBank());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasBank.xhtml";
			}
			break;
		case GLSEG:
			if (getInterfacePpProcessStatus(interfaceGfMasResDto)) {
				this.getManagedBean().setListOfPP(
						interfaceGfMasResDto.getListOfPmInterfacePP());
				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPP.xhtml";
			}
			break;
		default: {
			System.out.println("unknown child page \t"
					+ interfaceLog.getProcessId());
			log.info("unknown child page \t" + interfaceLog.getProcessId());
			return null;
		}
		}
		return null;
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

			try {
				interfaceprgActSO.saveOrUpdatePrgActList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPrgAct.xhtml";
			}




		} else if (this.getManagedBean().getSelectedProjectList() != null) {
			log.info("************ProjectList******SIZE******************>>"
					+ this.getManagedBean().getSelectedProjectList().size());

			IInterfaceProjectSO interfaceprojectActSO = (IInterfaceProjectSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceProjectSO.class);


			InterfaceProjectReqDTO reqDTO = new InterfaceProjectReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceProjectLs(this.getManagedBean().getSelectedProjectList());

			try {
				interfaceprojectActSO.saveOrUpdateProjectList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasProject.xhtml";
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
				e.printStackTrace();
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
				e.printStackTrace();
			}

			this.getManagedBean().setSelectedBankList(null);

		} else if (this.getManagedBean().getSelectedGlAcntList() != null) {
			log.info("***********GlAcntList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedGlAcntList().size());
			
			IInterfaceGlAccSO interfaceGlAccSO = (IInterfaceGlAccSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceGlAccSO.class);


			InterfaceGlAccReqDTO reqDTO = new InterfaceGlAccReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceGlAccLs(this.getManagedBean().getSelectedGlAcntList());

			try {
				interfaceGlAccSO.saveOrUpdateGlAccList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasGlAccount.xhtml";
			}
			
			
			
		} else if (this.getManagedBean().getSelectedKptjList() != null) {
			log.info("************KptjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedKptjList().size());
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
				System.out.println("error:"+e.getMessage());
			}

			this.getManagedBean().setSelectedPtjList(null);

		} else if (this.getManagedBean().getSelectedDanaList() != null) {

			log.info("***********DanaList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedDanaList().size());

			IInterfaceVotDanaSO interfaceVotDanaSO = (IInterfaceVotDanaSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceVotDanaSO.class);

			InterfaceVotDanaReqDTO reqDTO = new InterfaceVotDanaReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceDanaLs(this.getManagedBean().getSelectedDanaList());

			try {
				interfaceVotDanaSO.saveOrUpdateVotDanaList(reqDTO);
			} catch (Exception e) {
				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasDana.xhtml";
			}


			this.getManagedBean().setSelectedDanaList(null);

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

			this.getManagedBean().setSelectedPpList(null);

		} else if (this.getManagedBean().getSelectedVotList() != null) {
			log.info("***********VotList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedVotList().size());

			IInterfaceVotSO interfacevotSO = (IInterfaceVotSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IInterfaceVotSO.class);

			InterfaceVotReqDTO reqDTO = new InterfaceVotReqDTO();
			reqDTO.setFileName(this.getManagedBean().getFileName());

			reqDTO.setInterfaceVotLs(this.getManagedBean().getSelectedVotList());
			try {
				interfacevotSO.saveOrUpdateVotFundList(reqDTO);
			} catch (Exception e) {


				System.out.println(""+e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return "/xhtml/gfMas/gfmassearch/childprocess/gfmasVot.xhtml";
			}

			this.getManagedBean().setSelectedVotList(null);

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
		if (StringUtils.isNotEmpty(this.getManagedBean().getProcessId())) {
			this.getManagedBean().getProcessId();
		}
	}

	/**
	 * Gets the process codelist.
	 * 
	 * @return the process codelist
	 */
	public List<SelectItem> getProcessCodelist() {
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
	}
}
