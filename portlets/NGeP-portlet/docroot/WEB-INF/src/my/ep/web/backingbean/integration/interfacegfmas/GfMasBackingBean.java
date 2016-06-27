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
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.batch.IDIInterfaceSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogReqDto;
import my.ep.dto.pm.common.interfaceptj.InterfaceLogResDto;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.DIInterfaceLog;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.persistence.entity.pm.integration.PmInterfaceProgAct;
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
import org.apache.commons.lang3.StringUtils;

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
				"pmInterfaceBank.dIInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean kptjFilterBean = new SearchFilterBean(
				"pmInterfaceKptj.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean ptjFilterBean = new SearchFilterBean(
				"pmInterfacePtj.diInterfaceLog",
				SearchOperator.OPERATOR_IS_NOT_NULL.value(), null);

		SearchFilterBean ppFilterBean = new SearchFilterBean(
				"pmInterfacePP.dIInterfaceLog",
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
	 * @param interfaceLog
	 *            the interface log
	 * @return the string
	 */
	public String goToGfMasProcessMaintaincePage(DIInterfaceLog interfaceLog) {
		log.info("In goToGfMasProcessMaintaincePage");
		IDIInterfaceSO gfMasSO = (IDIInterfaceSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IDIInterfaceSO.class);

		String childProcessPage = null;

		try {
			InterfaceLogReqDto interfaceGfMasReqDto = new InterfaceLogReqDto();
			interfaceGfMasReqDto.setdIInterfaceLog(interfaceLog);
			interfaceGfMasReqDto.setLogId(interfaceLog.getLogId());
			interfaceGfMasReqDto.setProcessId(interfaceLog.getProcessId());

			InterfaceLogResDto interfaceGfMasResDto = gfMasSO
					.retrieveProcessDataForAll(interfaceGfMasReqDto);

			childProcessPage = getChildProcessScreen(interfaceLog,
					interfaceGfMasResDto);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			gfMasSO = null;
		}

		return childProcessPage;
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
			this.getManagedBean().setListOfPrgAct(
					interfaceGfMasResDto.getListOfPmInterfacePrgAct());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPrgAct.xhtml";

		case GLBAC:
			this.getManagedBean().setListOfAgOffice(
					interfaceGfMasResDto.getListOfPmInterfaceAgOffice());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasAgOffice.xhtml";

		case GLVOT:
			this.getManagedBean().setListOfVot(
					interfaceGfMasResDto.getListOfPmInterfaceVot());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasVot.xhtml";

		case GLDNA:
			this.getManagedBean().setListOfDana(
					interfaceGfMasResDto.getListOfPmInterfaceDana());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasDana.xhtml";

		case GLPRJ:
			this.getManagedBean().setListOfProjectt(
					interfaceGfMasResDto.getListOfPmInterfaceProject());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasProject.xhtml";

		case GLGLC:
			this.getManagedBean().setListOfGlAccount(
					interfaceGfMasResDto.getListOfPmInterfaceGlAccount());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasGlAccount.xhtml";

		case GLPCG:
			this.getManagedBean().setListOfKPTJ(
					interfaceGfMasResDto.getListOfPmInterfaceKptj());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasKPTJ.xhtml";

		case GLPTJ:
			this.getManagedBean().setListOfPTJ(
					interfaceGfMasResDto.getListOfPmInterfacePtj());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPTJ.xhtml";

		case CMBNK:
			this.getManagedBean().setListOfBank(
					interfaceGfMasResDto.getListOfPmInterfaceBank());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasBank.xhtml";

		case GLSEG:
			this.getManagedBean().setListOfPP(
					interfaceGfMasResDto.getListOfPmInterfacePP());
			return "/xhtml/gfMas/gfmassearch/childprocess/gfmasPP.xhtml";

		default: {
			System.out.println("unknown child page \t"
					+ interfaceLog.getProcessId());
			log.info("unknown child page \t" + interfaceLog.getProcessId());
			return null;
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
		
		return "/xhtml/gfMas/gfmassearch/gfmasSearchMain.xhtml?faces-redirect=true";

	}

	/**
	 * Process all vendors.
	 * 
	 * @return the string
	 */
	public String processAllVendors() {

		log.info("processAllVendors");

		if (this.getManagedBean().getSelectedProgActList() != null) {
			log.info("***************ProgActList***SIZE******************>>"
					+ this.getManagedBean().getSelectedProgActList().size());
		} else if (this.getManagedBean().getSelectedProjectList() != null) {
			log.info("************ProjectList******SIZE******************>>"
					+ this.getManagedBean().getSelectedProjectList().size());
		} else if (this.getManagedBean().getSelectedAgOfficeList() != null) {

			log.info("***********AgOfficeList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedAgOfficeList().size());

			IAgOfficeSO reqSO = (IAgOfficeSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAgOfficeSO.class);

			List<PmInterfaceAgOff> list = this.getManagedBean()
					.getSelectedAgOfficeList();

			for (int i = 0; i < list.size(); i++) {
				PmInterfaceAgOff pmInterfaceAgOff = list.get(i);
				reqSO.execute(pmInterfaceAgOff);
			}
		} else if (this.getManagedBean().getSelectedBankList() != null) {
			log.info("************BankList******SIZE******************>>"
					+ this.getManagedBean().getSelectedBankList().size());
		} else if (this.getManagedBean().getSelectedGlAcntList() != null) {
			log.info("***********GlAcntList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedGlAcntList().size());
		} else if (this.getManagedBean().getSelectedKptjList() != null) {
			log.info("************KptjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedKptjList().size());
		} else if (this.getManagedBean().getSelectedPtjList() != null) {
			log.info("************PtjList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPtjList().size());
		} else if (this.getManagedBean().getSelectedDanaList() != null) {
			log.info("***********DanaList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedDanaList().size());
		} else if (this.getManagedBean().getSelectedPpList() != null) {
			log.info("************PpList******SIZE******************>>"
					+ this.getManagedBean().getSelectedPpList().size());
		} else if (this.getManagedBean().getSelectedVotList() != null) {
			log.info("***********VotList*******SIZE******************>>"
					+ this.getManagedBean().getSelectedVotList().size());
		}
		return null;
	}

	public void userActionProcess(AjaxBehaviorEvent e) {

		if (this.getManagedBean().getSelectedProgActList() != null) {
			Iterator<PmInterfaceProgAct> iterator = this.getManagedBean()
					.getSelectedProgActList().iterator();
			while (iterator.hasNext()) {
				PmInterfaceProgAct pmInterfaceProgAct = iterator.next();
				if (pmInterfaceProgAct.getPrgActCode() != null
						&& pmInterfaceProgAct.getProcessStatus() != null
						&& pmInterfaceProgAct.getProcessStatus().equals("1"))
					;
				{
					iterator.remove();
				}
			}
		}
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
