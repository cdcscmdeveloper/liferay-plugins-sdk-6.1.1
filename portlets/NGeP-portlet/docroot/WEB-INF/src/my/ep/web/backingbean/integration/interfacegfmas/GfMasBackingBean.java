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
import my.ep.biz.bean.search.SearchBean;
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
import my.ep.dto.pm.common.interfaceptj.InterfaceAgOffResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceCmBankReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceCmBankResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceGlAccReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceGlAccResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceKptjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceKptjResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogReqDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.dto.pm.common.interfaceptj.InterfacePpReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePpResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePrgActReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePrgActResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceProjectReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceProjectResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfacePtjResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotDanaReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotDanaResDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotReqDTO;
import my.ep.dto.pm.common.interfaceptj.InterfaceVotResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.DIInterfaceLog;
import my.ep.persistence.entity.pm.common.PmVotFund;
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
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;





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

	boolean isContains = false;

	private boolean flag;

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
		/*
		 * if (this.getManagedBean().getInterfaceLogId() != null) {
		 * 
		 * searchBean.getDataFilterBeans().add( new SearchFilterBean("logId",
		 * SearchOperator.OPERATOR_EQUALS.value(), this
		 * .getManagedBean().getInterfaceLogId())); } if
		 * (StringUtils.isNotEmpty(this.getManagedBean().getServiceCode())) {
		 * searchBean.getDataFilterBeans().add( new
		 * SearchFilterBean("serviceCode",
		 * SearchOperator.OPERATOR_EQUALS.value(), this
		 * .getManagedBean().getServiceCode() .toUpperCase(), true)); }
		 * 
		 * if (StringUtils.isNotEmpty(this.getManagedBean().getDirection())) {
		 * searchBean.getDataFilterBeans().add( new
		 * SearchFilterBean("direction", SearchOperator.OPERATOR_EQUALS.value(),
		 * this .getManagedBean().getDirection() .toUpperCase(), true)); }
		 */

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
				new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_EQUALS.value(), new Long(0)));

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
				"pmInterfaceDana.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null,
				SearchCondition.CONDITION_OR.value());

		searchBean
				.getDataFilterBeans()
				.add(new SearchMultiFilterBean(
						progAcctgFilterBean,
						SearchCondition.CONDITION_AND.value(),
						new SearchMultiFilterBean(
								projectFilterBean,
								SearchCondition.CONDITION_OR.value(),
								new SearchMultiFilterBean(
										glAcntFilterBean,
										SearchCondition.CONDITION_OR.value(),
										new SearchMultiFilterBean(
												agOffFilterBean,
												SearchCondition.CONDITION_OR
														.value(),
												new SearchMultiFilterBean(
														bankFilterBean,
														SearchCondition.CONDITION_OR
																.value(),
														new SearchMultiFilterBean(
																kptjFilterBean,
																SearchCondition.CONDITION_OR
																		.value(),
																new SearchMultiFilterBean(
																		ptjFilterBean,
																		SearchCondition.CONDITION_OR
																				.value(),
																		new SearchMultiFilterBean(
																				ppFilterBean,
																				SearchCondition.CONDITION_OR
																						.value(),
																				new SearchMultiFilterBean(
																						votFilterBean,
																						SearchCondition.CONDITION_OR
																								.value(),
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
						.retrieveInterfaceLogDetails(gfMasReqDTO);
				System.out.println("getTotalRecord:"
						+ resDTO.getViewGfMasResultBean().getTotalRecord());

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
		// this.getManagedBean().setProcessId(selectedProcess.getProcessId());
		this.getManagedBean().setPageProcessId(selectedProcess.getProcessId());
		this.getManagedBean().setTransId(selectedProcess.getTransId());
		//this.getManagedBean().setMapOfProcess(null);
		getUnprocessedRecordsByPreviousDate(selectedProcess);
		//refreshReload();		
		

	/*	this.getManagedBean().setSelectedBankList(
				this.getManagedBean().getSearchBank().getList());
*/
		return childProcessPage;
	}

	private void refreshReload() {		
		this.getManagedBean()
				.getSearchBank().reloadData();
		selectAllBanks(false);		
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

		DIInterfaceLog oldDateLog = getMasterDataByCreatedDate(selectedProcess);

		FacesContext fc = FacesContext.getCurrentInstance();

		String processId = oldDateLog.getProcessId();

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String createdDate = dateFormat.format(oldDateLog.getCreatedDate());

		if (selectedProcess.getProcessStatus() != null
				&& selectedProcess.getProcessStatus().equals(new Long(0))) {

			switch (ProcessCodeType.valueOf(selectedProcess.getProcessId())) {

			case GLPRG:
				if (getPrgActDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_PRGACT);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}	
				this.getManagedBean()
				.getSearchProgAct().reloadData();
				selectAllPrgAct(false);				
				break;
			case GLBAC:
				if (getAgOffDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_AGOFFICE);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}				
				this.getManagedBean()
				.getSearchAgOff().reloadData();
				selectAllAccountingOffice(false);			
				break;
			case GLSEG:
				if (getPpDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_PP);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}		
				this.getManagedBean()
				.getSearchPp().reloadData();
				selectAllPp(false);				
				break;
			case GLVOT:
				if (getVotDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_VOT);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}		
				this.getManagedBean()
				.getSearchVot().reloadData();
				selectAllVot(false);				
				break;
			case GLDNA:
				if (getDanaDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_DANA);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchDana().reloadData();
				selectAllDana(false);			
				break;
			case GLPRJ:
				if (getProjectDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_PROJECT);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchProject().reloadData();
				selectAllProject(false);
				break;
			case GLGLC:
				if (getGlAccountDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_GLACCOUNT);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchGlAccount().reloadData();
				selectAllGlAccount(false);			
				break;
			case GLPCG:
				if (getKptjDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_KPTJ);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchKptj().reloadData();
				selectAllKptj(false);
				break;
			case GLPTJ:
				if (getPtjDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_PTJ);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchPtj().reloadData();
				selectAllPtj(false);
				break;
			case CMBNK:
				if (getBankDataCountByProcessStatus(selectedProcess)) {
					fc.getApplication().getNavigationHandler()
							.handleNavigation(fc, null, NAV_TO_BANK);
					showErrorMsg(selectedProcess, processId, createdDate,
							oldDateLog, fc);
				}
				this.getManagedBean()
				.getSearchBank().reloadData();
				selectAllBanks(false);
				break;
			}
		}

	}

	private boolean getAgOffDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfaceAgOffSO agOffSO;

		SearchBean<PmInterfaceAgOff> searchBean = new SearchBean<PmInterfaceAgOff>();

		ISearchBean<PmInterfaceAgOff> searchBeanData = getAgOffSearchBeanData(
				searchBean, selectedProcess);

		InterfaceAgOffReqDTO reqDTO = new InterfaceAgOffReqDTO();

		reqDTO.setViewAgOffSearchBean(searchBeanData);

		agOffSO = (IInterfaceAgOffSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceAgOffSO.class);

		InterfaceAgOffResDTO resDTO = agOffSO
				.getAgOffDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;
	}

	private boolean getVotDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfaceVotSO votSO;

		SearchBean<PmInterfaceVot> searchBean = new SearchBean<PmInterfaceVot>();

		ISearchBean<PmInterfaceVot> searchBeanData = getVotSearchBeanData(
				searchBean, selectedProcess);

		InterfaceVotReqDTO reqDTO = new InterfaceVotReqDTO();

		reqDTO.setViewVotSearchBean(searchBeanData);

		votSO = (IInterfaceVotSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceVotSO.class);

		InterfaceVotResDTO resDTO = votSO
				.getVotDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;

	}

	private boolean getDanaDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {
		IInterfaceVotDanaSO iDanaSO;

		SearchBean<PmInterfaceDana> searchBean = new SearchBean<PmInterfaceDana>();

		ISearchBean<PmInterfaceDana> searchBeanData = getDanaSearchBeanData(
				searchBean, selectedProcess);

		InterfaceVotDanaReqDTO reqDTO = new InterfaceVotDanaReqDTO();

		reqDTO.setViewDanaSearchBean(searchBeanData);

		iDanaSO = (IInterfaceVotDanaSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceVotDanaSO.class);

		InterfaceVotDanaResDTO resDTO = iDanaSO
				.getDanaDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;

	}

	private boolean getProjectDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {
		IInterfaceProjectSO iProjectSO;

		SearchBean<PmInterfaceProject> searchBean = new SearchBean<PmInterfaceProject>();

		ISearchBean<PmInterfaceProject> searchBeanData = getProjectSearchBeanData(
				searchBean, selectedProcess);

		InterfaceProjectReqDTO reqDTO = new InterfaceProjectReqDTO();

		reqDTO.setViewProjectSearchBean(searchBeanData);

		iProjectSO = (IInterfaceProjectSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceProjectSO.class);

		InterfaceProjectResDTO resDTO = iProjectSO
				.getProjectDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;

	}

	private boolean getGlAccountDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfaceGlAccSO glAccountSO;
		SearchBean<PmInterfaceGlAccount> searchBean = new SearchBean<PmInterfaceGlAccount>();

		ISearchBean<PmInterfaceGlAccount> searchBeanData = getGlAccountSearchBeanData(
				searchBean, selectedProcess);

		InterfaceGlAccReqDTO reqDTO = new InterfaceGlAccReqDTO();

		reqDTO.setViewGlAcntSearchBean(searchBeanData);

		glAccountSO = (IInterfaceGlAccSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceGlAccSO.class);

		InterfaceGlAccResDTO resDTO = glAccountSO
				.getGlAcntDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}
		return isContains;
	}

	private boolean getKptjDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfaceKptjSO iKptjSO;

		SearchBean<PmInterfaceKptj> searchBean = new SearchBean<PmInterfaceKptj>();

		ISearchBean<PmInterfaceKptj> searchBeanData = getKptjSearchBeanData(
				searchBean, selectedProcess);

		InterfaceKptjReqDTO reqDTO = new InterfaceKptjReqDTO();

		reqDTO.setViewKptjSearchBean(searchBeanData);

		iKptjSO = (IInterfaceKptjSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceKptjSO.class);

		InterfaceKptjResDTO resDTO = iKptjSO
				.getKptjDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}
		return isContains;
	}

	private boolean getPtjDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfacePtjSO iPtjSO;

		SearchBean<PmInterfacePtj> searchBean = new SearchBean<PmInterfacePtj>();

		ISearchBean<PmInterfacePtj> searchBeanData = getPtjSearchBeanData(
				searchBean, selectedProcess);

		InterfacePtjReqDTO reqDTO = new InterfacePtjReqDTO();

		reqDTO.setViewPtjSearchBean(searchBeanData);

		iPtjSO = (IInterfacePtjSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfacePtjSO.class);

		InterfacePtjResDTO resDTO = iPtjSO
				.getPtjDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;

	}

	private boolean getBankDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfaceCmBankSO iBankSO;

		SearchBean<PmInterfaceBank> searchBean = new SearchBean<PmInterfaceBank>();

		ISearchBean<PmInterfaceBank> searchBeanData = getBankSearchBeanData(
				searchBean, selectedProcess);

		InterfaceCmBankReqDTO bankReqDTO = new InterfaceCmBankReqDTO();

		bankReqDTO.setViewBankSearchBean(searchBeanData);

		iBankSO = (IInterfaceCmBankSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfaceCmBankSO.class);

		InterfaceCmBankResDTO resDTO = iBankSO
				.getBankDataCountByProcessStatus(bankReqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;

	}

	private boolean getPrgActDataCountByProcessStatus(
			DIInterfaceLog selectedProcess) {

		IInterfacePrgActSO iPrgActSO;

		SearchBean<PmInterfaceProgAct> searchBean = new SearchBean<PmInterfaceProgAct>();

		ISearchBean<PmInterfaceProgAct> searchBeanData = getPrgActSearchBeanData(
				searchBean, selectedProcess);

		InterfacePrgActReqDTO reqDTO = new InterfacePrgActReqDTO();

		reqDTO.setViewSearchBean(searchBeanData);

		iPrgActSO = (IInterfacePrgActSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfacePrgActSO.class);

		InterfacePrgActResDTO resDTO = iPrgActSO
				.getPrjActDataCountByProcessStatus(reqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}
		return isContains;

	}

	private ISearchBean<PmInterfacePP> getPpSearchBeanData(
			ISearchBean<PmInterfacePP> searchBean, DIInterfaceLog diLog) {

		log.info("getPpSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceBank> getBankSearchBeanData(
			ISearchBean<PmInterfaceBank> searchBean, DIInterfaceLog diLog) {

		log.info("getBankSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceVot> getVotSearchBeanData(
			ISearchBean<PmInterfaceVot> searchBean, DIInterfaceLog diLog) {

		log.info("getVotSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceKptj> getKptjSearchBeanData(
			ISearchBean<PmInterfaceKptj> searchBean, DIInterfaceLog diLog) {

		log.info("getKptjSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfacePtj> getPtjSearchBeanData(
			ISearchBean<PmInterfacePtj> searchBean, DIInterfaceLog diLog) {

		log.info("getPtjSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceDana> getDanaSearchBeanData(
			ISearchBean<PmInterfaceDana> searchBean, DIInterfaceLog diLog) {

		log.info("getDanaSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceAgOff> getAgOffSearchBeanData(
			ISearchBean<PmInterfaceAgOff> searchBean, DIInterfaceLog diLog) {

		log.info("getAgOffSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceGlAccount> getGlAccountSearchBeanData(
			ISearchBean<PmInterfaceGlAccount> searchBean, DIInterfaceLog diLog) {

		log.info("getGlAccountSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("dIInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceProgAct> getPrgActSearchBeanData(
			ISearchBean<PmInterfaceProgAct> searchBean, DIInterfaceLog diLog) {

		log.info("getPrgActSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private ISearchBean<PmInterfaceProject> getProjectSearchBeanData(
			ISearchBean<PmInterfaceProject> searchBean, DIInterfaceLog diLog) {

		log.info("getProjectSearchBeanData");

		if (diLog.getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("dIInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), diLog
									.getLogId()));
		}

		List<Long> listOfProcessStatus = new ArrayList<Long>();
		listOfProcessStatus.add(new Long(0));
		listOfProcessStatus.add(new Long(1));
		listOfProcessStatus.add(new Long(2));

		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_IN.value(), listOfProcessStatus));

		return searchBean;
	}

	private boolean getPpDataCountByProcessStatus(DIInterfaceLog diInterfaceLog) {
		log.info("getChildDataCountByProcessStatus");

		IInterfacePpSO iPpSO;

		boolean isContains = false;

		SearchBean<PmInterfacePP> searchBean = new SearchBean<PmInterfacePP>();

		ISearchBean<PmInterfacePP> searchBeanData = getPpSearchBeanData(
				searchBean, diInterfaceLog);

		InterfacePpReqDTO ppReqDTO = new InterfacePpReqDTO();

		ppReqDTO.setViewPpSearchBean(searchBeanData);

		iPpSO = (IInterfacePpSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IInterfacePpSO.class);

		InterfacePpResDTO resDTO = iPpSO
				.getPpDataCountByProcessStatus(ppReqDTO);
		if (resDTO.getCount() > 0) {
			isContains = true;
		}

		return isContains;
	}

	private ISearchBean<DIInterfaceLog> getMasterSearchBeanData(
			ISearchBean<DIInterfaceLog> searchBean,
			DIInterfaceLog selectedProcess) {

		log.info("getMasterSearchBeanData");
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("processStatus",
						SearchOperator.OPERATOR_EQUALS.value(), selectedProcess
								.getProcessStatus()));

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("targetSystem",
						SearchOperator.OPERATOR_EQUALS.value(), selectedProcess
								.getTargetSystem()));

		if (StringUtils.isNotEmpty(selectedProcess.getProcessId())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("processId",
							SearchOperator.OPERATOR_EQUALS.value(),
							selectedProcess.getProcessId()));
		}
		searchBean.getSortColumns().add(
				new SearchOrderBean("createdDate", true));

		return searchBean;
	}

	private DIInterfaceLog getMasterDataByCreatedDate(
			DIInterfaceLog selectedProcess) {

		IDIInterfaceSO gfMasSO;
		DIInterfaceLog interfaceLog = null;

		InterfaceLogReqDto ReqDTO = new InterfaceLogReqDto();

		SearchBean<DIInterfaceLog> searchBean = new SearchBean<DIInterfaceLog>();

		ISearchBean<DIInterfaceLog> searchBeanData = this
				.getMasterSearchBeanData(searchBean, selectedProcess);

		ReqDTO.setViewSearchBean(searchBeanData);

		gfMasSO = (IDIInterfaceSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDIInterfaceSO.class);

		InterfaceLogResDto resDTO = gfMasSO
				.retrieveInterfaceLogDetailsByCreatedDate(ReqDTO);

		if (CollectionUtils.isNotEmpty(resDTO.getViewGfMasResultBean()
				.getResultList())) {

			interfaceLog = resDTO.getViewGfMasResultBean().getResultList()
					.get(0);
		}

		return interfaceLog;
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
	private void showErrorMsg(DIInterfaceLog selectedProcess, String processId,
			String createdDate, DIInterfaceLog oldCreatedDateLog,
			FacesContext fc) {
		/*
		 * if (!selectedProcess.getLogId().equals(oldCreatedDateLog.getLogId()))
		 * { if (!(selectedProcess.getCreatedDate().compareTo(
		 * oldCreatedDateLog.getCreatedDate())<0)) {
		 * fc.getApplication().getNavigationHandler() .handleNavigation(fc,
		 * null, NAV_TO_MAIN);
		 * log.info("Label--->"+this.getManagedBean().getProcessLabels
		 * ().get(processId)); throw new EpException(MessageConst.ERROR_780001,
		 * new String[] {
		 * this.getManagedBean().getProcessLabels().get(processId),createdDate
		 * }); }else{
		 * 
		 * } }
		 */
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		String selectedDate = dateFormat.format(selectedProcess.getCreatedDate());
		
		if (!selectedProcess.getLogId().equals(oldCreatedDateLog.getLogId())
				&& (!selectedDate.equals(createdDate))) {
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_TO_MAIN);
			log.info("Label--->"
					+ this.getManagedBean().getProcessLabels().get(processId));
			throw new EpException(MessageConst.ERROR_780001, new String[] {
					this.getManagedBean().getProcessLabels().get(processId),
					createdDate });
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
		this.setFlag(false);
		selectAllBanks(false);
		selectAllVot(false);
		selectAllDana(false);
		selectAllAccountingOffice(false);
		selectAllGlAccount(false);
		selectAllKptj(false);
		selectAllPtj(false);
		selectAllPp(false);
		selectAllPrgAct(false);
		selectAllProject(false);

		return "/xhtml/gfMas/gfmassearch/gfmasSearchMain.xhtml?faces-redirect=true";

	}

	/**
	 * Process all vendors.
	 * 
	 * @return the string
	 */
	public String processAllVendors() {

		log.info("processAllVendors");
		
        
		if (this.getManagedBean().getSelectedProgActList() != null && this.getManagedBean().getSelectedProgActList().size()>0 ) {
			log.info("***************ProgActList***SIZE******************>>"
					+ this.getManagedBean().getSelectedProgActList().size());
			
			IInterfacePrgActSO interfaceprgActSO = (IInterfacePrgActSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfacePrgActSO.class);
			InterfacePrgActReqDTO reqDTO = new InterfacePrgActReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfacePrgActLs(this.getManagedBean()
					.getSelectedProgActList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceprgActSO.saveOrUpdatePrgActList(reqDTO);
				this.getManagedBean().setSelectedProgActList(null);				
				this.getManagedBean().getSearchProgAct().reloadDataWithoutReset();
			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PRGACT;
			}

		} else if (this.getManagedBean().getSelectedProjectList() != null && this.getManagedBean().getSelectedProjectList().size()>0) {
			log.info("************ProjectList******SIZE******************>>"
					+ this.getManagedBean().getSelectedProjectList().size());

			IInterfaceProjectSO interfaceprojectActSO = (IInterfaceProjectSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceProjectSO.class);

			InterfaceProjectReqDTO reqDTO = new InterfaceProjectReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceProjectLs(this.getManagedBean()
					.getSelectedProjectList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceprojectActSO.saveOrUpdateProjectList(reqDTO);
				this.getManagedBean().setSelectedProjectList(null);
				this.getManagedBean().getSearchProject().reloadDataWithoutReset();
			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PROJECT;
			}

		} else if (this.getManagedBean().getSelectedAgOfficeList() != null && this.getManagedBean().getSelectedAgOfficeList().size()>0) {

			log.info("***********AgOfficeList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedAgOfficeList().size());

			IInterfaceAgOffSO reqSO = (IInterfaceAgOffSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceAgOffSO.class);

			List<PmInterfaceAgOff> list = this.getManagedBean()
					.getSelectedAgOfficeList();

			InterfaceAgOffReqDTO reqDTO = new InterfaceAgOffReqDTO();

			reqDTO.setInterfaceAgOffLs(list);
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				reqSO.saveOrUpdateInterfaceAgOffList(reqDTO);
				this.getManagedBean().setSelectedAgOfficeList(null);
				this.getManagedBean().getSearchAgOff().reloadDataWithoutReset();
			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_AGOFFICE;

			}

		} else if (this.getManagedBean().getSelectedBankList() != null && this.getManagedBean().getSelectedBankList().size()>0) {

			log.info("************BankList******SIZE******************>>"
					+ this.getManagedBean().getSelectedBankList().size());

			InterfaceCmBankReqDTO reqDTO = new InterfaceCmBankReqDTO();

			reqDTO.setInterfaceBankLs(this.getManagedBean()
					.getSelectedBankList());
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {

				IInterfaceCmBankSO reqSO = (IInterfaceCmBankSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfaceCmBankSO.class);

				reqSO.saveOrUpdateInterfaceBankList(reqDTO);
				this.getManagedBean().setSelectedBankList(null);
				this.getManagedBean().getSearchBank().reloadDataWithoutReset();

			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_BANK;

			}

		} else if (this.getManagedBean().getSelectedGlAcntList() != null && this.getManagedBean().getSelectedGlAcntList().size()>0) {
			log.info("***********GlAcntList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedGlAcntList().size());

			IInterfaceGlAccSO interfaceGlAccSO = (IInterfaceGlAccSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceGlAccSO.class);

			InterfaceGlAccReqDTO reqDTO = new InterfaceGlAccReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceGlAccLs(this.getManagedBean()
					.getSelectedGlAcntList());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {
				interfaceGlAccSO.saveOrUpdateGlAccList(reqDTO);
				this.getManagedBean().setSelectedGlAcntList(null);
				this.getManagedBean().getSearchGlAccount().reloadDataWithoutReset();
			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_GLACCOUNT;
			}

		} else if (this.getManagedBean().getSelectedKptjList() != null && this.getManagedBean().getSelectedKptjList().size()>0) {
			log.info("************KptjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedKptjList().size());
			InterfaceKptjReqDTO reqDTO = new InterfaceKptjReqDTO();

			reqDTO.setInterfaceKptjLs(this.getManagedBean()
					.getSelectedKptjList());
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {

				IInterfaceKptjSO reqSO = (IInterfaceKptjSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfaceKptjSO.class);

				reqSO.saveOrUpdateInterfaceKptjList(reqDTO);
				this.getManagedBean().setSelectedKptjList(null);
				this.getManagedBean().getSearchKptj().reloadDataWithoutReset();

			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_KPTJ;
			}

			
		} else if (this.getManagedBean().getSelectedPtjList() != null && this.getManagedBean().getSelectedPtjList().size()>0) {

			log.info("************PtjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPtjList().size());

			InterfacePtjReqDTO reqDTO = new InterfacePtjReqDTO();

			reqDTO.setInterfacePtjLs(this.getManagedBean().getSelectedPtjList());
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {

				IInterfacePtjSO reqSO = (IInterfacePtjSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfacePtjSO.class);

				reqSO.saveOrUpdateInterfacePtjList(reqDTO);
				this.getManagedBean().setSelectedPtjList(null);
				this.getManagedBean().getSearchPtj().reloadDataWithoutReset();

			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_PTJ;

			}

		} else if (this.getManagedBean().getSelectedDanaList() != null && this.getManagedBean().getSelectedDanaList().size()>0) {

			log.info("***********DanaList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedDanaList().size());

			IInterfaceVotDanaSO interfaceVotDanaSO = (IInterfaceVotDanaSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceVotDanaSO.class);

			InterfaceVotDanaReqDTO reqDTO = new InterfaceVotDanaReqDTO();

			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setInterfaceDanaLs(this.getManagedBean()
					.getSelectedDanaList());
			reqDTO.setTransId(this.getManagedBean().getTransId());
			try {
				interfaceVotDanaSO.saveOrUpdateVotDanaList(reqDTO);
				this.getManagedBean().setSelectedDanaList(null);
				this.getManagedBean().getSearchDana().reloadDataWithoutReset();
			} catch (Exception e) {
				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_DANA;
			}

		} else if (this.getManagedBean().getSelectedPpList() != null && this.getManagedBean().getSelectedPpList().size()>0) {

			log.info("************PpList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPpList().size());

			InterfacePpReqDTO reqDTO = new InterfacePpReqDTO();

			reqDTO.setInterfacePpLs(this.getManagedBean().getSelectedPpList());
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			try {

				IInterfacePpSO reqSO = (IInterfacePpSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IInterfacePpSO.class);

				reqSO.saveOrUpdateInterfacePpList(reqDTO);
				this.getManagedBean().setSelectedPpList(null);
				this.getManagedBean().getSearchPp().reloadDataWithoutReset();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (this.getManagedBean().getSelectedVotList() != null && this.getManagedBean().getSelectedVotList().size()>0) {
			log.info("***********VotList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedVotList().size());

			IInterfaceVotSO interfacevotSO = (IInterfaceVotSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IInterfaceVotSO.class);

			InterfaceVotReqDTO reqDTO = new InterfaceVotReqDTO();
			reqDTO.setFileName(this.getManagedBean().getFileName());
			reqDTO.setTransId(this.getManagedBean().getTransId());

			reqDTO.setInterfaceVotLs(this.getManagedBean().getSelectedVotList());
			try {
				interfacevotSO.saveOrUpdateVotFundList(reqDTO);
				this.getManagedBean().setSelectedVotList(null);
				this.getManagedBean().getSearchVot().reloadDataWithoutReset();
			} catch (Exception e) {

				System.out.println("" + e.getMessage());
				PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
						FacesMessage.SEVERITY_ERROR, e.getMessage(), null);

				return NAV_TO_VOT;
			}
		}

		String processPage = goToProcessPage(this.getManagedBean()
				.getPageProcessId());
		PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(),
				FacesMessage.SEVERITY_INFO, MessageConst.INFO_780002, null);

		return processPage;
	}

	private String goToProcessPage(String processId) {

		log.info("goToProcessPage");

		String processPage = null;

		switch (ProcessCodeType.valueOf(processId)) {

		case GLPRG:
			processPage = NAV_TO_PRGACT;
			break;
		case GLBAC:
			processPage = NAV_TO_AGOFFICE;
			break;
		case GLSEG:
			processPage = NAV_TO_PP;
			break;
		case GLVOT:
			processPage = NAV_TO_VOT;
			break;
		case GLDNA:
			processPage = NAV_TO_DANA;
			break;
		case GLPRJ:
			processPage = NAV_TO_PROJECT;
			break;
		case GLGLC:
			processPage = NAV_TO_GLACCOUNT;
			break;
		case GLPCG:
			processPage = NAV_TO_KPTJ;
			break;
		case GLPTJ:
			processPage = NAV_TO_PTJ;
			break;
		case CMBNK:
			processPage = NAV_TO_BANK;
			break;
		}
		return processPage;
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
	 * @return the flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
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

	/*
	 * public void selectPrgActEvent(PmInterfaceProgAct pmInterfaceProgAct) {
	 * this.getManagedBean().setSelectedProgActList(new
	 * ArrayList<PmInterfaceProgAct>()); if (pmInterfaceProgAct.isSelected()) {
	 * this.getManagedBean().getSelectedProgActList() .add(pmInterfaceProgAct);
	 * }else{
	 * this.getManagedBean().getSelectedProgActList().remove(pmInterfaceProgAct
	 * ); } }
	 */

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
	/*
	 * public List<SelectItem> getProcessCodelist() {
	 * log.info("In getProcessCodelist");
	 * 
	 * List<ProcessCodeType> list = new ArrayList<ProcessCodeType>();
	 * 
	 * List<SelectItem> processCodeValues = new ArrayList<SelectItem>();
	 * 
	 * list.add(ProcessCodeType.GLBAC); list.add(ProcessCodeType.GLSEG);
	 * list.add(ProcessCodeType.GLVOT); list.add(ProcessCodeType.GLDNA);
	 * list.add(ProcessCodeType.GLPRG); list.add(ProcessCodeType.GLPRJ);
	 * list.add(ProcessCodeType.GLGLC); list.add(ProcessCodeType.GLPCG);
	 * list.add(ProcessCodeType.GLPTJ); list.add(ProcessCodeType.CMBNK);
	 * 
	 * for (int i = 0; i < list.size(); i++) { SelectItem selectItem = new
	 * SelectItem(); selectItem.setValue(list.get(i));
	 * selectItem.setLabel(list.get(i).toString());
	 * 
	 * processCodeValues.add(selectItem); }
	 * 
	 * return processCodeValues; }
	 */

	public SelectItem[] getProcessCodelist() {

		SelectItem[] items = new SelectItem[10];

		Map<String, String> processLabels = new HashMap<String, String>();

		items[0] = new SelectItem("GLBAC", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glbacdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[1] = new SelectItem("GLSEG", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glsegdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[2] = new SelectItem("GLVOT", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glvotdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[3] = new SelectItem("GLDNA", ResourceBundleUtils.getMessage(
				"lbl.gfmas.gldanadef",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[4] = new SelectItem("GLPRG", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glprgdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[5] = new SelectItem("GLPRJ", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glprjdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[6] = new SelectItem("GLGLC", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glglcdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[7] = new SelectItem("GLPCG", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glpcgdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[8] = new SelectItem("GLPTJ", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glptjdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		items[9] = new SelectItem("CMBNK", ResourceBundleUtils.getMessage(
				"lbl.gfmas.glbnkdef", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));

		if (items != null && items.length > 0) {
			for (int i = 0; i < items.length; i++) {
				String processIdLabel = items[i].getLabel().trim();
				if (!processLabels.containsKey(processIdLabel)) {
					processLabels.put(items[i].getValue().toString(),
							processIdLabel);
				}
			}
		}
		this.getManagedBean().setProcessLabels(processLabels);

		return items;
	}

	public void selectAllPrgAct(boolean selectAll) {

		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceProgAct> prgActList = getManagedBean()
				.getSearchProgAct().getList();
		
		this.getManagedBean().setSelectedProgActList(
				new ArrayList<PmInterfaceProgAct>());
		if (prgActList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceProgAct pmInterfaceProgAct : prgActList) {
				if (!childProcessStatus().contains(
						pmInterfaceProgAct.getProcessStatus())
						&& selectAll) {
					pmInterfaceProgAct.setSelected(true);
					this.getManagedBean().getSelectedProgActList()
							.add(pmInterfaceProgAct);
				} else {
					pmInterfaceProgAct.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedProgActList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}

	public void selectAllGlAccount (boolean selectAll) {

		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceGlAccount> glAccountList = getManagedBean()
				.getSearchGlAccount().getList();
		
		this.getManagedBean().setSelectedGlAcntList(
				new ArrayList<PmInterfaceGlAccount>());
		if (glAccountList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceGlAccount pmInterfaceGlAccount : glAccountList) {
				if (!childProcessStatus().contains(
						pmInterfaceGlAccount.getProcessStatus())
						&& selectAll) {
					pmInterfaceGlAccount.setSelected(true);
					this.getManagedBean().getSelectedGlAcntList()
							.add(pmInterfaceGlAccount);
				} else {
					pmInterfaceGlAccount.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedGlAcntList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}

	public void selectAllAccountingOffice(boolean selectAll) {

		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceAgOff> agOffList = getManagedBean()
				.getSearchAgOff().getList();
		
		this.getManagedBean().setSelectedAgOfficeList(
				new ArrayList<PmInterfaceAgOff>());
		if (agOffList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceAgOff pmInterfaceAgOff : agOffList) {
				if (!childProcessStatus().contains(
						pmInterfaceAgOff.getProcessStatus())
						&& selectAll) {
					pmInterfaceAgOff.setSelected(true);
					this.getManagedBean().getSelectedAgOfficeList()
							.add(pmInterfaceAgOff);
				} else {
					pmInterfaceAgOff.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedAgOfficeList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}

	public void selectAllKptj(boolean selectAll) {
		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceKptj> kptjList = getManagedBean()
				.getSearchKptj().getList();
		
		this.getManagedBean().setSelectedKptjList(
				new ArrayList<PmInterfaceKptj>());
		if (kptjList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceKptj pmInterfaceKptj : kptjList) {
				if (!childProcessStatus().contains(
						pmInterfaceKptj.getProcessStatus())
						&& selectAll) {
					pmInterfaceKptj.setSelected(true);
					this.getManagedBean().getSelectedKptjList()
							.add(pmInterfaceKptj);
				} else {
					pmInterfaceKptj.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedKptjList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	
	}

	public void selectAllPtj(boolean selectAll) {
		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfacePtj> ptjList = getManagedBean()
				.getSearchPtj().getList();
		
		this.getManagedBean().setSelectedPtjList(
				new ArrayList<PmInterfacePtj>());
		if (ptjList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfacePtj pmInterfacePtj : ptjList) {
				if (!childProcessStatus().contains(
						pmInterfacePtj.getProcessStatus())
						&& selectAll) {
					pmInterfacePtj.setSelected(true);
					this.getManagedBean().getSelectedPtjList()
							.add(pmInterfacePtj);
				} else {
					pmInterfacePtj.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedPtjList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
		
	}

	public void selectAllPp(boolean selectAll) {
		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfacePP> pPList = getManagedBean()
				.getSearchPp().getList();
		
		this.getManagedBean().setSelectedPpList(
				new ArrayList<PmInterfacePP>());
		if (pPList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfacePP pmInterfacePP : pPList) {
				if (!childProcessStatus().contains(
						pmInterfacePP.getProcessStatus())
						&& selectAll) {
					pmInterfacePP.setSelected(true);
					this.getManagedBean().getSelectedPpList()
							.add(pmInterfacePP);
				} else {
					pmInterfacePP.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedPpList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}

	public void selectAllVot(boolean selectAll) {

		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceVot> votList = getManagedBean()
				.getSearchVot().getList();
		
		this.getManagedBean().setSelectedVotList(
				new ArrayList<PmInterfaceVot>());
		if (votList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceVot pmInterfaceVot : votList) {
				if (!childProcessStatus().contains(
						pmInterfaceVot.getProcessStatus())
						&& selectAll) {
					pmInterfaceVot.setSelected(true);
					this.getManagedBean().getSelectedVotList()
							.add(pmInterfaceVot);
				} else {
					pmInterfaceVot.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedVotList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}

	public void selectAllDana(boolean selectAll) {
		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceDana> danaList = getManagedBean()
				.getSearchDana().getList();
		
		this.getManagedBean().setSelectedDanaList(
				new ArrayList<PmInterfaceDana>());
		if (danaList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceDana pmInterfaceDana : danaList) {
				if (!childProcessStatus().contains(
						pmInterfaceDana.getProcessStatus())
						&& selectAll) {
					pmInterfaceDana.setSelected(true);
					this.getManagedBean().getSelectedDanaList()
							.add(pmInterfaceDana);
				} else {
					pmInterfaceDana.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedDanaList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/

	}

	public void selectAllProject(boolean selectAll) {

		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceProject> projectList = getManagedBean()
				.getSearchProject().getList();
		
		this.getManagedBean().setSelectedProjectList(
				new ArrayList<PmInterfaceProject>());
		if (projectList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceProject pmInterfaceProject : projectList) {
				if (!childProcessStatus().contains(
						pmInterfaceProject.getProcessStatus())
						&& selectAll) {
					pmInterfaceProject.setSelected(true);
					this.getManagedBean().getSelectedProjectList()
							.add(pmInterfaceProject);
				} else {
					pmInterfaceProject.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedProjectList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}
	
 public void selectAllBanks(boolean selectAll) {
		
		/*int disabledCount = 0;
		int totalCount=0;*/
		List<PmInterfaceBank> banksList = getManagedBean()
				.getSearchBank().getList();
		
		this.getManagedBean().setSelectedBankList(
				new ArrayList<PmInterfaceBank>());
		if (banksList != null) {
			/*totalCount= banksList.size();*/
			for (PmInterfaceBank pmInterfaceBank : banksList) {
				if (!childProcessStatus().contains(
						pmInterfaceBank.getProcessStatus())
						&& selectAll) {
					pmInterfaceBank.setSelected(true);
					this.getManagedBean().getSelectedBankList()
							.add(pmInterfaceBank);
				} else {
					pmInterfaceBank.setSelected(false);
					//disabledCount++;					
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(
				getManagedBean().getSelectedBankList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		/*if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
		}*/
	}
	

	public void selectAllPrgAct(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllPrgAct(flag);
	}
	public void selectAllBanks(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllBanks(flag);
	}
	public void selectAllAccountingOffice(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllAccountingOffice(flag);
	}
	public void selectAllGlAccount(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllGlAccount(flag);
	}
	public void selectAllKptj(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllKptj(flag);
	}
	public void selectAllPtj(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllPtj(flag);
	}
	public void selectAllPp(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllPp(flag);
	}
	public void selectAllVot(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllVot(flag);
	}
	public void selectAllDana(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllDana(flag);
	}
	public void selectAllProject(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		selectAllProject(flag);
	}
	
	

	private List<Long> childProcessStatus() {
		List<Long> disableProcessList = new ArrayList<Long>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add(1L); 
				add(2L);
			}
		};
		return disableProcessList;
	}

	public void selectBankSingleRow(PmInterfaceBank singleSelectedBank) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceBank> banksList = getManagedBean().getSearchBank()
				.getList();

		if (this.getManagedBean().getSelectedBankList() == null) {
			this.getManagedBean().setSelectedBankList(
					new ArrayList<PmInterfaceBank>());
		}
		if (singleSelectedBank.isSelected()) {
			this.getManagedBean().getSelectedBankList().add(singleSelectedBank);
		} else {
			this.getManagedBean().getSelectedBankList()
					.remove(singleSelectedBank);
		}
		if (banksList != null) {
			totalCount = banksList.size();
			for (PmInterfaceBank pmInterfaceBank : banksList) {
				if (pmInterfaceBank.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceBank.getProcessStatus())) {
						pmInterfaceBank.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedBankList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	
	
	public void selectProjectSingleRow(PmInterfaceProject singleSelectedProject) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceProject> projectList = getManagedBean().getSearchProject()
				.getList();

		if (this.getManagedBean().getSelectedProjectList() == null) {
			this.getManagedBean().setSelectedProjectList(
					new ArrayList<PmInterfaceProject>());
		}
		if (singleSelectedProject.isSelected()) {
			this.getManagedBean().getSelectedProjectList().add(singleSelectedProject);
		} else {
			this.getManagedBean().getSelectedProjectList()
					.remove(singleSelectedProject);
		}
		if (projectList != null) {
			totalCount = projectList.size();
			for (PmInterfaceProject pmInterfaceProject : projectList) {
				if (pmInterfaceProject.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceProject.getProcessStatus())) {
						pmInterfaceProject.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedProjectList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	
	public void selectVotSingleRow(PmInterfaceVot singleSelectedVot) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceVot> votList = getManagedBean().getSearchVot()
				.getList();

		if (this.getManagedBean().getSelectedVotList() == null) {
			this.getManagedBean().setSelectedVotList(
					new ArrayList<PmInterfaceVot>());
		}
		if (singleSelectedVot.isSelected()) {
			this.getManagedBean().getSelectedVotList().add(singleSelectedVot);
		} else {
			this.getManagedBean().getSelectedVotList()
					.remove(singleSelectedVot);
		}
		if (votList != null) {
			totalCount = votList.size();
			for (PmInterfaceVot pmInterfaceVot : votList) {
				if (pmInterfaceVot.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceVot.getProcessStatus())) {
						pmInterfaceVot.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedVotList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	
	public void selectKptjSingleRow(PmInterfaceKptj singleSelectedKptj) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceKptj> kptjList = getManagedBean().getSearchKptj()
				.getList();

		if (this.getManagedBean().getSelectedKptjList() == null) {
			this.getManagedBean().setSelectedKptjList(
					new ArrayList<PmInterfaceKptj>());
		}
		if (singleSelectedKptj.isSelected()) {
			this.getManagedBean().getSelectedKptjList().add(singleSelectedKptj);
		} else {
			this.getManagedBean().getSelectedKptjList()
					.remove(singleSelectedKptj);
		}
		if (kptjList != null) {
			totalCount = kptjList.size();
			for (PmInterfaceKptj pmInterfaceKptj : kptjList) {
				if (pmInterfaceKptj.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceKptj.getProcessStatus())) {
						pmInterfaceKptj.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedKptjList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	
	public void selectPtjSingleRow(PmInterfacePtj singleSelectedPtj) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfacePtj> ptjList = getManagedBean().getSearchPtj()
				.getList();

		if (this.getManagedBean().getSelectedPtjList() == null) {
			this.getManagedBean().setSelectedPtjList(
					new ArrayList<PmInterfacePtj>());
		}
		if (singleSelectedPtj.isSelected()) {
			this.getManagedBean().getSelectedPtjList().add(singleSelectedPtj);
		} else {
			this.getManagedBean().getSelectedPtjList()
					.remove(singleSelectedPtj);
		}
		if (ptjList != null) {
			totalCount = ptjList.size();
			for (PmInterfacePtj pmInterfacePtj : ptjList) {
				if (pmInterfacePtj.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfacePtj.getProcessStatus())) {
						pmInterfacePtj.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedPtjList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	public void selectPpSingleRow(PmInterfacePP singleSelectedPp) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfacePP> pPList = getManagedBean().getSearchPp()
				.getList();

		if (this.getManagedBean().getSelectedPpList() == null) {
			this.getManagedBean().setSelectedPpList(
					new ArrayList<PmInterfacePP>());
		}
		if (singleSelectedPp.isSelected()) {
			this.getManagedBean().getSelectedPpList().add(singleSelectedPp);
		} else {
			this.getManagedBean().getSelectedPpList()
					.remove(singleSelectedPp);
		}
		if (pPList != null) {
			totalCount = pPList.size();
			for (PmInterfacePP pmInterfacePP : pPList) {
				if (pmInterfacePP.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfacePP.getProcessStatus())) {
						pmInterfacePP.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedPpList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	public void selectAgOfficeSingleRow(PmInterfaceAgOff singleSelectedAgOff) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceAgOff> agOffList = getManagedBean().getSearchAgOff()
				.getList();

		if (this.getManagedBean().getSelectedAgOfficeList() == null) {
			this.getManagedBean().setSelectedAgOfficeList(
					new ArrayList<PmInterfaceAgOff>());
		}
		if (singleSelectedAgOff.isSelected()) {
			this.getManagedBean().getSelectedAgOfficeList().add(singleSelectedAgOff);
		} else {
			this.getManagedBean().getSelectedAgOfficeList()
					.remove(singleSelectedAgOff);
		}
		if (agOffList != null) {
			totalCount = agOffList.size();
			for (PmInterfaceAgOff pmInterfaceAgOff : agOffList) {
				if (pmInterfaceAgOff.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceAgOff.getProcessStatus())) {
						pmInterfaceAgOff.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedAgOfficeList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	public void selectGlAccountSingleRow(PmInterfaceGlAccount singleSelectedGlAccount) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceGlAccount> glAccountList = getManagedBean().getSearchGlAccount()
				.getList();

		if (this.getManagedBean().getSelectedGlAcntList() == null) {
			this.getManagedBean().setSelectedGlAcntList(
					new ArrayList<PmInterfaceGlAccount>());
		}
		if (singleSelectedGlAccount.isSelected()) {
			this.getManagedBean().getSelectedGlAcntList().add(singleSelectedGlAccount);
		} else {
			this.getManagedBean().getSelectedGlAcntList()
					.remove(singleSelectedGlAccount);
		}
		if (glAccountList != null) {
			totalCount = glAccountList.size();
			for (PmInterfaceGlAccount pmInterfaceGlAccount : glAccountList) {
				if (pmInterfaceGlAccount.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceGlAccount.getProcessStatus())) {
						pmInterfaceGlAccount.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedGlAcntList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	public void selectPrgActSingleRow(PmInterfaceProgAct singleSelectedPrgAct) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceProgAct> prgActList = getManagedBean().getSearchProgAct()
				.getList();

		if (this.getManagedBean().getSelectedProgActList() == null) {
			this.getManagedBean().setSelectedProgActList(
					new ArrayList<PmInterfaceProgAct>());
		}
		if (singleSelectedPrgAct.isSelected()) {
			this.getManagedBean().getSelectedProgActList().add(singleSelectedPrgAct);
		} else {
			this.getManagedBean().getSelectedProgActList()
					.remove(singleSelectedPrgAct);
		}
		if (prgActList != null) {
			totalCount = prgActList.size();
			for (PmInterfaceProgAct pmInterfaceProgAct : prgActList) {
				if (pmInterfaceProgAct.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceProgAct.getProcessStatus())) {
						pmInterfaceProgAct.setSelected(false);
						disabledCount++;
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedProgActList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}

	}
	public void selectDanaSingleRow(PmInterfaceDana singleSelectedDana) {

		int disabledCount = 0;
		int selectedCount = 0;
		int totalCount=0;

		setFlag(false);
		List<PmInterfaceDana> danaList = getManagedBean().getSearchDana()
				.getList();

		if (this.getManagedBean().getSelectedDanaList() == null) {
			this.getManagedBean().setSelectedDanaList(
					new ArrayList<PmInterfaceDana>());
		}
		if (singleSelectedDana.isSelected()) {
			this.getManagedBean().getSelectedDanaList().add(singleSelectedDana);
		} else {
			this.getManagedBean().getSelectedDanaList()
					.remove(singleSelectedDana);
		}
		if (danaList != null) {
			totalCount = danaList.size();
			for (PmInterfaceDana pmInterfaceDana : danaList) {
				if (pmInterfaceDana.isSelected()) {
					selectedCount++;
				} else {
					if (childProcessStatus().contains(
							pmInterfaceDana.getProcessStatus())) {
						pmInterfaceDana.setSelected(false);
						disabledCount++;
					}
				}
			}
		}		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedDanaList())) {
			this.getManagedBean().setProcessButtonStatus(true);
		} else {
			this.getManagedBean().setProcessButtonStatus(false);
		}
		if ((totalCount - disabledCount) == selectedCount) {
			setFlag(true);
		} else if (disabledCount == totalCount) {
			this.getManagedBean().setDisableAllCheckBox(true);
			setFlag(false);
		} else {
			setFlag(false);
		}
	}

}
