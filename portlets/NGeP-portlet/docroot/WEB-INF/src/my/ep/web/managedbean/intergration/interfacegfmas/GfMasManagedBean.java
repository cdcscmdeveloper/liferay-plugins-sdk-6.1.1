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
package my.ep.web.managedbean.intergration.interfacegfmas;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
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
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

// TODO: Auto-generated Javadoc
/**
 * The Class GfMasManagedBean.
 *   @author NAGASIVA
 *   @since 17.06.2016
 */
@ManagedBean(name = "gfMasManagedBean")
@SessionScoped
public class GfMasManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private IPagingDataModel<PmInterfaceProgAct> searchProgAct = new SearchProgActPagingDataModel();
	private IPagingDataModel<PmInterfaceProject> searchProject = new SearchProjectPagingDataModel();
	private IPagingDataModel<PmInterfaceGlAccount> searchGlAccount = new SearchGlAcntPagingDataModel();
	private IPagingDataModel<PmInterfaceAgOff> searchAgOff = new SearchAgOffPagingDataModel();
	private IPagingDataModel<PmInterfaceBank> searchBank = new SearchBankPagingDataModel();
	private IPagingDataModel<PmInterfaceKptj> searchKptj = new SearchKptjPagingDataModel();
	private IPagingDataModel<PmInterfacePtj> searchPtj = new SearchPtjPagingDataModel();
	private IPagingDataModel<PmInterfacePP> searchPp = new SearchPpPagingDataModel();
	private IPagingDataModel<PmInterfaceVot> searchVot = new SearchVotPagingDataModel();	
	private IPagingDataModel<PmInterfaceDana> searchDana = new SearchDanaPagingDataModel();	
	
	private DIInterfaceLog selectedProcess;
	
	private Boolean disableAllCheckBox=false;

    /** The service code. */
    private String serviceCode;
    
    /** The process id. */
    private String processId;
    
    private String pageProcessId;
    
    /** The direction. */
    private String direction;
    
    /** The interface log id. */
    private Long interfaceLogId;
    
    /** The created date. */
    private Date createdDate;
    
    /** The file name. */
    private String fileName;
    
    /** The trans id. */
    private String transId;

	/** The is lazy. */
	private boolean isLazy;	
	
	/** The disable. */
	private boolean disable;
	
	/** The process button status. */
	private boolean processButtonStatus;
	
	private Map<String, String> processLabels;
	
	/** The d i interface log. */
	private DIInterfaceLog dIInterfaceLog;
	
	/** The map of process. */
	private Map<String, List<DIInterfaceLog>> mapOfProcess;
	
    /** The selected prog act list. */
    private List<PmInterfaceProgAct> selectedProgActList;
	

	/** The selected ag office list. */
	private List<PmInterfaceAgOff> selectedAgOfficeList;
	

	/** The selected bank list. */
	private List<PmInterfaceBank> selectedBankList;
	
	
	/** The selected dana list. */
	private List<PmInterfaceDana> selectedDanaList;
	
	
	/** The selected gl acnt list. */
	private List<PmInterfaceGlAccount> selectedGlAcntList;
	
	
	/** The selected kptj list. */
	private List<PmInterfaceKptj> selectedKptjList;
	
	

	/** The selected project list. */
	private List<PmInterfaceProject> selectedProjectList;
	
	
	/** The selected ptj list. */
	private List<PmInterfacePtj> selectedPtjList;
	
	
	/** The selected vot list. */
	private List<PmInterfaceVot> selectedVotList;
	
	
	/** The selected pp list. */
	private List<PmInterfacePP> selectedPpList;
	
	private static Logger log = Logger.getLogger(GfMasManagedBean.class);
	
	public  GfMasManagedBean() {
	       log.info("In GfMasManagedBean");
	}
	
	public void searchProgAct() {
		 log.info("In searchProgAct");
		searchProgAct.reloadData();
	}

	public void searchProject() {
		 log.info("In searchProject");
		searchProject.reloadData();
	}

	public void searchGlAccount() {
		 log.info("In searchGlAccount");
		searchGlAccount.reloadData();
	}

	public void searchAgOff() {
		 log.info("In searchAgOff");
		searchAgOff.reloadData();
	}

	public void searchBank() {
		 log.info("In searchBank");
		searchBank.reloadData();
	}

	public void searchKptj() {
		 log.info("In searchKptj");
		searchKptj.reloadData();
	}

	public void searchPtj() {
		 log.info("In searchPtj");
		searchPtj.reloadData();
	}

	public void searchPp() {
		 log.info("In searchPp");
		searchPp.reloadData();
	}

	public void searchVot() {
		 log.info("In searchVot");
		searchVot.reloadData();
	}

	public void searchDana() {
		 log.info("In searchDana");
		searchDana.reloadData();
	}
	
	private ISearchBean<PmInterfaceProgAct> getPrgActSearchBeanData(
			ISearchBean<PmInterfaceProgAct> searchBean) {
		
		log.info("getPrgActSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchProgActPagingDataModel extends
	  PagingDataModel<PmInterfaceProgAct> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchProgActPagingDataModel() {
			super("gfMasPrgActId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceProgAct> pageChangeListener(
				ISearchBean<PmInterfaceProgAct> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfacePrgActSO iPrgActSO;	        

			try {				
				ISearchBean<PmInterfaceProgAct> searchBeanData = getPrgActSearchBeanData(searchBean);

				InterfacePrgActReqDTO prgActReqDTO = new InterfacePrgActReqDTO();

				prgActReqDTO.setViewSearchBean(searchBeanData);

				iPrgActSO = (IInterfacePrgActSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfacePrgActSO.class);	

				   InterfacePrgActResDTO resDTO = iPrgActSO
						.getProgActSearchDetails(prgActReqDTO);
                 
				if (resDTO.isSuccessful()) {

					return resDTO.getViewPrgActResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	
	private ISearchBean<PmInterfaceProject> getProjectSearchBeanData(
			ISearchBean<PmInterfaceProject> searchBean) {
		
		log.info("getProjectSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("dIInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchProjectPagingDataModel extends
	  PagingDataModel<PmInterfaceProject> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchProjectPagingDataModel() {
			super("gfMasProjectId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceProject> pageChangeListener(
				ISearchBean<PmInterfaceProject> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
			IInterfaceProjectSO IProjectSO;	        

			try {				
				ISearchBean<PmInterfaceProject> searchBeanData = getProjectSearchBeanData(searchBean);

				InterfaceProjectReqDTO projectReqDTO = new InterfaceProjectReqDTO();

				projectReqDTO.setViewProjectSearchBean(searchBeanData);

				IProjectSO = (IInterfaceProjectSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceProjectSO.class);	

				   InterfaceProjectResDTO resDTO = IProjectSO
						.getProjectSearchDetails(projectReqDTO);
				
				if (resDTO.isSuccessful()) {

					return resDTO.getViewProjectResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfaceGlAccount> getGlAcntSearchBeanData(
			ISearchBean<PmInterfaceGlAccount> searchBean) {
		
		log.info("getSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("dIInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchGlAcntPagingDataModel extends
	  PagingDataModel<PmInterfaceGlAccount> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchGlAcntPagingDataModel() {
			super("gfMasGlAcntId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceGlAccount> pageChangeListener(
				ISearchBean<PmInterfaceGlAccount> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceGlAccSO IglAcntSO;	        

			try {				
				ISearchBean<PmInterfaceGlAccount> searchBeanData = getGlAcntSearchBeanData(searchBean);

				InterfaceGlAccReqDTO glAcntReqDTO = new InterfaceGlAccReqDTO();

				glAcntReqDTO.setViewGlAcntSearchBean(searchBeanData);

				IglAcntSO = (IInterfaceGlAccSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceGlAccSO.class);	

				InterfaceGlAccResDTO resDTO = IglAcntSO
						.getGlAcntSearchDetails(glAcntReqDTO);
		
				if (resDTO.isSuccessful()) {

					return resDTO.getViewGlAcntResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfaceAgOff> getAgOffSearchBeanData(
			ISearchBean<PmInterfaceAgOff> searchBean) {
		
		log.info("getAgOffSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchAgOffPagingDataModel extends
	  PagingDataModel<PmInterfaceAgOff> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchAgOffPagingDataModel() {
			super("gfMasAgOfficeId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceAgOff> pageChangeListener(
				ISearchBean<PmInterfaceAgOff> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceAgOffSO iInterfaceAgOffSO;	        

			try {				
				ISearchBean<PmInterfaceAgOff> searchBeanData = getAgOffSearchBeanData(searchBean);

				InterfaceAgOffReqDTO agOffReqDTO = new InterfaceAgOffReqDTO();

				agOffReqDTO.setViewAgOffSearchBean(searchBeanData);

				iInterfaceAgOffSO = (IInterfaceAgOffSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceAgOffSO.class);	

				InterfaceAgOffResDTO resDTO = iInterfaceAgOffSO
						.getAgOffSearchDetails(agOffReqDTO);
			
				if (resDTO.isSuccessful()) {

					return resDTO.getViewAgOffResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	
	private ISearchBean<PmInterfaceBank> getBankSearchBeanData(
			ISearchBean<PmInterfaceBank> searchBean) {
		
		log.info("getBankSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchBankPagingDataModel extends
	  PagingDataModel<PmInterfaceBank> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchBankPagingDataModel() {
			super("gfMasBankId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceBank> pageChangeListener(
				ISearchBean<PmInterfaceBank> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceCmBankSO IbankSO;	        

			try {				
				ISearchBean<PmInterfaceBank> searchBeanData = getBankSearchBeanData(searchBean);

				InterfaceCmBankReqDTO bankReqDTO = new InterfaceCmBankReqDTO();

				bankReqDTO.setViewBankSearchBean(searchBeanData);

				IbankSO = (IInterfaceCmBankSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceCmBankSO.class);	

				InterfaceCmBankResDTO resDTO = IbankSO
						.getBankSearchDetails(bankReqDTO);
				/*List<PmInterfaceBank> list=resDTO.getViewBankResultBean().getResultList();
				
				int totalCount = list.size();
				int disabledCount = 0;
				if (list != null) {
					for (PmInterfaceBank pmInterfaceBank : list) {
						if (pmInterfaceBank.getProcessStatus().equals(
								new Long(1))
								|| pmInterfaceBank.getProcessStatus().equals(
										new Long(2))) {
							disabledCount++;
						}
					}
				}
				if (disabledCount == totalCount) {
					setDisableAllCheckBox(true);
				} else {
					setDisableAllCheckBox(false);
				}
			*/
				if (resDTO.isSuccessful()) {

					return resDTO.getViewBankResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfaceKptj> getKptjSearchBeanData(
			ISearchBean<PmInterfaceKptj> searchBean) {
		
		log.info("getKptjSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchKptjPagingDataModel extends
	  PagingDataModel<PmInterfaceKptj> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchKptjPagingDataModel() {
			super("gfMasKPTJId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceKptj> pageChangeListener(
				ISearchBean<PmInterfaceKptj> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceKptjSO IkptjSO;	        

			try {				
				ISearchBean<PmInterfaceKptj> searchBeanData = getKptjSearchBeanData(searchBean);

				InterfaceKptjReqDTO kptjReqDTO = new InterfaceKptjReqDTO();

				kptjReqDTO.setViewKptjSearchBean(searchBeanData);

				IkptjSO = (IInterfaceKptjSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceKptjSO.class);	

				InterfaceKptjResDTO resDTO = IkptjSO
						.getKptjSearchDetails(kptjReqDTO);
			
				if (resDTO.isSuccessful()) {

					return resDTO.getViewKptjResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfacePtj> getPtjSearchBeanData(
			ISearchBean<PmInterfacePtj> searchBean) {
		
		log.info("getPtjSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchPtjPagingDataModel extends
	  PagingDataModel<PmInterfacePtj> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchPtjPagingDataModel() {
			super("gfMasPTJId");
		}	
		@Override
		public ISearchResultBean<PmInterfacePtj> pageChangeListener(
				ISearchBean<PmInterfacePtj> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfacePtjSO iPtjSO;	        

			try {				
				ISearchBean<PmInterfacePtj> searchBeanData = getPtjSearchBeanData(searchBean);

				InterfacePtjReqDTO ptjReqDTO = new InterfacePtjReqDTO();

				ptjReqDTO.setViewPtjSearchBean(searchBeanData);

				iPtjSO = (IInterfacePtjSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfacePtjSO.class);	

				   InterfacePtjResDTO resDTO = iPtjSO
						.getPtjSearchDetails(ptjReqDTO);
				
				if (resDTO.isSuccessful()) {

					return resDTO.getViewPtjResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfacePP> getPpSearchBeanData(
			ISearchBean<PmInterfacePP> searchBean) {
		
		log.info("getPpSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchPpPagingDataModel extends
	  PagingDataModel<PmInterfacePP> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchPpPagingDataModel() {
			super("gfMasPPId");
		}	
		@Override
		public ISearchResultBean<PmInterfacePP> pageChangeListener(
				ISearchBean<PmInterfacePP> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfacePpSO iPpSO;	        

			try {				
				ISearchBean<PmInterfacePP> searchBeanData = getPpSearchBeanData(searchBean);

				InterfacePpReqDTO ppReqDTO = new InterfacePpReqDTO();

				ppReqDTO.setViewPpSearchBean(searchBeanData);

				iPpSO = (IInterfacePpSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfacePpSO.class);	

				   InterfacePpResDTO resDTO = iPpSO
						.getPpSearchDetails(ppReqDTO);
				
				if (resDTO.isSuccessful()) {

					return resDTO.getViewPpResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfaceVot> getVotSearchBeanData(
			ISearchBean<PmInterfaceVot> searchBean) {
		
		log.info("getVotSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	

	private class SearchVotPagingDataModel extends
	  PagingDataModel<PmInterfaceVot> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchVotPagingDataModel() {
			super("gfMasVotId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceVot> pageChangeListener(
				ISearchBean<PmInterfaceVot> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceVotSO iVotSO;	        

			try {				
				ISearchBean<PmInterfaceVot> searchBeanData = getVotSearchBeanData(searchBean);

				InterfaceVotReqDTO votReqDTO = new InterfaceVotReqDTO();

				votReqDTO.setViewVotSearchBean(searchBeanData);

				iVotSO = (IInterfaceVotSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceVotSO.class);	

				   InterfaceVotResDTO resDTO = iVotSO
						.getVotSearchDetails(votReqDTO);
				
                
				if (resDTO.isSuccessful()) {

					return resDTO.getViewVotResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	private ISearchBean<PmInterfaceDana> getDanaSearchBeanData(
			ISearchBean<PmInterfaceDana> searchBean) {
		
		log.info("getDanaSearchBeanData");
		
		if ( this.getSelectedProcess().getLogId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("diInterfaceLog.logId",
							SearchOperator.OPERATOR_EQUALS.value(), this.getSelectedProcess().getLogId()));
		}
		return searchBean;
	}	
	
	private class SearchDanaPagingDataModel extends
	  PagingDataModel<PmInterfaceDana> {
	
		private static final long serialVersionUID = 1L;
		
		public SearchDanaPagingDataModel() {
			super("gfMasDanaId");
		}	
		@Override
		public ISearchResultBean<PmInterfaceDana> pageChangeListener(
				ISearchBean<PmInterfaceDana> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}			
			
		    IInterfaceVotDanaSO iDanaSO;	        

			try {				
				ISearchBean<PmInterfaceDana> searchBeanData = getDanaSearchBeanData(searchBean);

				InterfaceVotDanaReqDTO danaReqDTO = new InterfaceVotDanaReqDTO();

				danaReqDTO.setViewDanaSearchBean(searchBeanData);

				iDanaSO = (IInterfaceVotDanaSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IInterfaceVotDanaSO.class);	

				InterfaceVotDanaResDTO resDTO = iDanaSO
						.getDanaSearchDetails(danaReqDTO);
				
				if (resDTO.isSuccessful()) {

					return resDTO.getViewDanaResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			return null;
		}
	}
	
	/**
	 * Gets the trans id.
	 * 
	 * @return the transId
	 */
	public String getTransId() {
		return transId;
	}

	

	/**
	 * @return the selectedProcess
	 */
	public DIInterfaceLog getSelectedProcess() {
		return selectedProcess;
	}

	/**
	 * @return the searchProject
	 */
	public IPagingDataModel<PmInterfaceProject> getSearchProject() {
		return searchProject;
	}

	/**
	 * @param searchProject the searchProject to set
	 */
	public void setSearchProject(IPagingDataModel<PmInterfaceProject> searchProject) {
		this.searchProject = searchProject;
	}

	/**
	 * @return the searchGlAccount
	 */
	public IPagingDataModel<PmInterfaceGlAccount> getSearchGlAccount() {
		return searchGlAccount;
	}

	/**
	 * @param searchGlAccount the searchGlAccount to set
	 */
	public void setSearchGlAccount(
			IPagingDataModel<PmInterfaceGlAccount> searchGlAccount) {
		this.searchGlAccount = searchGlAccount;
	}

	/**
	 * @return the searchAgOff
	 */
	public IPagingDataModel<PmInterfaceAgOff> getSearchAgOff() {
		return searchAgOff;
	}

	/**
	 * @param searchAgOff the searchAgOff to set
	 */
	public void setSearchAgOff(IPagingDataModel<PmInterfaceAgOff> searchAgOff) {
		this.searchAgOff = searchAgOff;
	}

	/**
	 * @return the searchBank
	 */
	public IPagingDataModel<PmInterfaceBank> getSearchBank() {
		return searchBank;
	}

	/**
	 * @param searchBank the searchBank to set
	 */
	public void setSearchBank(IPagingDataModel<PmInterfaceBank> searchBank) {
		this.searchBank = searchBank;
	}

	/**
	 * @return the searchKptj
	 */
	public IPagingDataModel<PmInterfaceKptj> getSearchKptj() {
		return searchKptj;
	}

	/**
	 * @param searchKptj the searchKptj to set
	 */
	public void setSearchKptj(IPagingDataModel<PmInterfaceKptj> searchKptj) {
		this.searchKptj = searchKptj;
	}

	/**
	 * @return the searchPtj
	 */
	public IPagingDataModel<PmInterfacePtj> getSearchPtj() {
		return searchPtj;
	}

	/**
	 * @param searchPtj the searchPtj to set
	 */
	public void setSearchPtj(IPagingDataModel<PmInterfacePtj> searchPtj) {
		this.searchPtj = searchPtj;
	}

	/**
	 * @return the searchPp
	 */
	public IPagingDataModel<PmInterfacePP> getSearchPp() {
		return searchPp;
	}

	/**
	 * @param searchPp the searchPp to set
	 */
	public void setSearchPp(IPagingDataModel<PmInterfacePP> searchPp) {
		this.searchPp = searchPp;
	}

	/**
	 * @return the searchVot
	 */
	public IPagingDataModel<PmInterfaceVot> getSearchVot() {
		return searchVot;
	}

	/**
	 * @param searchVot the searchVot to set
	 */
	public void setSearchVot(IPagingDataModel<PmInterfaceVot> searchVot) {
		this.searchVot = searchVot;
	}

	/**
	 * @return the searchDana
	 */
	public IPagingDataModel<PmInterfaceDana> getSearchDana() {
		return searchDana;
	}

	

	/**
	 * @param searchDana the searchDana to set
	 */
	public void setSearchDana(IPagingDataModel<PmInterfaceDana> searchDana) {
		this.searchDana = searchDana;
	}

	/**
	 * @param selectedProcess the selectedProcess to set
	 */
	public void setSelectedProcess(DIInterfaceLog selectedProcess) {
		this.selectedProcess = selectedProcess;
	}

	/**
	 * Sets the trans id.
	 * 
	 * @param transId
	 *            the transId to set
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

	

	/**
	 * Checks if is process button status.
	 * 
	 * @return the processButtonStatus
	 */
	public boolean isProcessButtonStatus() {
		return processButtonStatus;
	}

	/**
	 * Sets the process button status.
	 * 
	 * @param processButtonStatus
	 *            the processButtonStatus to set
	 */
	public void setProcessButtonStatus(boolean processButtonStatus) {
		this.processButtonStatus = processButtonStatus;
	}

	/**
	 * Gets the map of process.
	 * 
	 * @return the mapOfProcess
	 */
	public Map<String, List<DIInterfaceLog>> getMapOfProcess() {
		if (mapOfProcess == null) {
			mapOfProcess = new HashMap<String, List<DIInterfaceLog>>();
		}
		return mapOfProcess;
	}

	/**
	 * Sets the map of process.
	 * 
	 * @param mapOfProcess
	 *            the mapOfProcess to set
	 */
	public void setMapOfProcess(Map<String, List<DIInterfaceLog>> mapOfProcess) {
		this.mapOfProcess = mapOfProcess;
	}

	/**
	 * Gets the file name.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Checks if is disable.
	 * 
	 * @return the disable
	 */
	public boolean isDisable() {
		return disable;
	}

	/**
	 * Sets the disable.
	 * 
	 * @param disable
	 *            the disable to set
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	/**
	 * Checks if is lazy.
	 * 
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * Gets the d i interface log.
	 * 
	 * @return the dIInterfaceLog
	 */
	public DIInterfaceLog getdIInterfaceLog() {
		return dIInterfaceLog;
	}

	

	/**
	 * Sets the d i interface log.
	 * 
	 * @param dIInterfaceLog
	 *            the dIInterfaceLog to set
	 */
	public void setdIInterfaceLog(DIInterfaceLog dIInterfaceLog) {
		this.dIInterfaceLog = dIInterfaceLog;
	}

	/**
	 * Gets the service code.
	 * 
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * Sets the service code.
	 * 
	 * @param serviceCode
	 *            the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * Gets the process id.
	 * 
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * Sets the process id.
	 * 
	 * @param processId
	 *            the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @return the searchProgAct
	 */
	public IPagingDataModel<PmInterfaceProgAct> getSearchProgAct() {
		return searchProgAct;
	}

	/**
	 * @param searchProgAct the searchProgAct to set
	 */
	public void setSearchProgAct(IPagingDataModel<PmInterfaceProgAct> searchProgAct) {
		this.searchProgAct = searchProgAct;
	}

	/**
	 * Gets the interface log id.
	 * 
	 * @return the interfaceLogId
	 */
	public Long getInterfaceLogId() {
		return interfaceLogId;
	}

	/**
	 * Sets the interface log id.
	 * 
	 * @param interfaceLogId
	 *            the interfaceLogId to set
	 */
	public void setInterfaceLogId(Long interfaceLogId) {
		this.interfaceLogId = interfaceLogId;
	}

	/**
	 * Sets the direction.
	 * 
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * Gets the created date.
	 * 
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	/**
	 * Sets the created date.
	 * 
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
	 * @return the selectedProgActList
	 */
	public List<PmInterfaceProgAct> getSelectedProgActList() {
		return selectedProgActList;
	}

	/**
	 * @param selectedProgActList the selectedProgActList to set
	 */
	public void setSelectedProgActList(List<PmInterfaceProgAct> selectedProgActList) {
		this.selectedProgActList = selectedProgActList;
	}

	/**
	 * @return the selectedAgOfficeList
	 */
	public List<PmInterfaceAgOff> getSelectedAgOfficeList() {
		return selectedAgOfficeList;
	}

	/**
	 * @return the pageProcessId
	 */
	public String getPageProcessId() {
		return pageProcessId;
	}

	/**
	 * @param pageProcessId the pageProcessId to set
	 */
	public void setPageProcessId(String pageProcessId) {
		this.pageProcessId = pageProcessId;
	}

	/**
	 * @param selectedAgOfficeList the selectedAgOfficeList to set
	 */
	public void setSelectedAgOfficeList(List<PmInterfaceAgOff> selectedAgOfficeList) {
		this.selectedAgOfficeList = selectedAgOfficeList;
	}

	/**
	 * @return the selectedBankList
	 */
	public List<PmInterfaceBank> getSelectedBankList() {
		return selectedBankList;
	}

	/**
	 * @param selectedBankList the selectedBankList to set
	 */
	public void setSelectedBankList(List<PmInterfaceBank> selectedBankList) {
		this.selectedBankList = selectedBankList;
	}

	/**
	 * @return the selectedDanaList
	 */
	public List<PmInterfaceDana> getSelectedDanaList() {
		return selectedDanaList;
	}

	/**
	 * @return the disableAllCheckBox
	 */
	public Boolean getDisableAllCheckBox() {
		return disableAllCheckBox;
	}

	/**
	 * @param disableAllCheckBox the disableAllCheckBox to set
	 */
	public void setDisableAllCheckBox(Boolean disableAllCheckBox) {
		this.disableAllCheckBox = disableAllCheckBox;
	}

	/**
	 * @param selectedDanaList the selectedDanaList to set
	 */
	public void setSelectedDanaList(List<PmInterfaceDana> selectedDanaList) {
		this.selectedDanaList = selectedDanaList;
	}

	/**
	 * @return the selectedGlAcntList
	 */
	public List<PmInterfaceGlAccount> getSelectedGlAcntList() {
		return selectedGlAcntList;
	}

	/**
	 * @param selectedGlAcntList the selectedGlAcntList to set
	 */
	public void setSelectedGlAcntList(List<PmInterfaceGlAccount> selectedGlAcntList) {
		this.selectedGlAcntList = selectedGlAcntList;
	}

	/**
	 * @return the selectedKptjList
	 */
	public List<PmInterfaceKptj> getSelectedKptjList() {
		return selectedKptjList;
	}

	/**
	 * @param selectedKptjList the selectedKptjList to set
	 */
	public void setSelectedKptjList(List<PmInterfaceKptj> selectedKptjList) {
		this.selectedKptjList = selectedKptjList;
	}

	/**
	 * @return the selectedProjectList
	 */
	public List<PmInterfaceProject> getSelectedProjectList() {
		return selectedProjectList;
	}

	/**
	 * @param selectedProjectList the selectedProjectList to set
	 */
	public void setSelectedProjectList(List<PmInterfaceProject> selectedProjectList) {
		this.selectedProjectList = selectedProjectList;
	}

	/**
	 * @return the selectedPtjList
	 */
	public List<PmInterfacePtj> getSelectedPtjList() {
		return selectedPtjList;
	}

	/**
	 * @param selectedPtjList the selectedPtjList to set
	 */
	public void setSelectedPtjList(List<PmInterfacePtj> selectedPtjList) {
		this.selectedPtjList = selectedPtjList;
	}

	/**
	 * @return the selectedVotList
	 */
	public List<PmInterfaceVot> getSelectedVotList() {
		return selectedVotList;
	}

	/**
	 * @param selectedVotList the selectedVotList to set
	 */
	public void setSelectedVotList(List<PmInterfaceVot> selectedVotList) {
		this.selectedVotList = selectedVotList;
	}

	/**
	 * @return the selectedPpList
	 */
	public List<PmInterfacePP> getSelectedPpList() {
		return selectedPpList;
	}

	/**
	 * @return the processLabels
	 */
	public Map<String, String> getProcessLabels() {
		return processLabels;
	}

	/**
	 * @param processLabels the processLabels to set
	 */
	public void setProcessLabels(Map<String, String> processLabels) {
		this.processLabels = processLabels;
	}

	/**
	 * @param selectedPpList the selectedPpList to set
	 */
	public void setSelectedPpList(List<PmInterfacePP> selectedPpList) {
		this.selectedPpList = selectedPpList;
	}

}
