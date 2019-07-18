package my.ep.web.backingbean.sm.travellingTicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.admin.adminmatrix.IAdminMatrixSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.states.IListOfStatesSO;
import my.ep.biz.service.sm.travel.ITravellingTicketSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorResDTO;
import my.ep.dto.sm.mofAccount.MachineReqDTO;
import my.ep.dto.sm.mofAccount.MachineResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineSection;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.CitySearchBean;
import my.ep.web.constant.sm.MachineSearchBean;
import my.ep.web.managedbean.sm.travellingTicket.TravellingTicketManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "travellingTicketBB")
@ViewScoped
public class TravellingTicketBackingBean extends BaseBackingBean<TravellingTicketManagedBean>{
	
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(TravellingTicketBackingBean.class);
	private static final String TRAVELLING_TICKET_MAIN = "/xhtml/sm/travellingTicketing/travellingTicketMain.xhtml";
	private static final String VIEW_MAINTENANCE_RESULT_PAGE = "/xhtml/sm/travellingTicketing/travellingTicketMaintenanceMain.xhtml";;
	
	private IPagingDataModel<PmMachineSection> searchMachine = new TravellingTicketPagingDataModel();

	public IPagingDataModel<PmMachineSection> getSearchMachine() {
		return searchMachine;
	}

	public void setSearchMachine(IPagingDataModel<PmMachineSection> searchMachine) {
		this.searchMachine = searchMachine;
	}

	public TravellingTicketBackingBean() {

		log.info("inside CityBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void preRender(){
		renewSesionObject();
		this.resetMachineSearchBean();
		getSearchMachine().reloadData();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, TRAVELLING_TICKET_MAIN);
	
	}

	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public String backToSearch() {

		return TRAVELLING_TICKET_MAIN;
	}
	

	public void resetMachineSearchBean() {

		debugLogger("resetsMachineSearchBean");
		this.getManagedBean().setLazy(false);	
		this.getManagedBean().setMachineSearchBean(new MachineSearchBean());
		searchMachine = new TravellingTicketPagingDataModel();

	}
	
	public void searchMachine() {
		searchMachine.reloadData();
	}
	
	public String getCategoryTypeDescById(PmParameter parameter){

		try {			
			return getParameterDesc(parameter, new FacesScopes().getUserLocale());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public List<SelectItem> getListOfCategories() {

		IParameterSO listOfCategoriesSO = null;

			try {

				listOfCategoriesSO = (IParameterSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);

				ParameterResDTO resDTO = listOfCategoriesSO.retrieveListOfTravelCategoryType();

				List<Object[]> obj = resDTO.getTravelCategoryTypeList();

				for (int i=0; i<obj.size(); i++){	
					
					SelectItem selectItem = new SelectItem();

					selectItem.setValue(obj.get(i)[0].toString());
					selectItem.setLabel(obj.get(i)[2].toString());

					this.getManagedBean().getListOfCategories().add(selectItem);

				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		return this.getManagedBean().getListOfCategories();
	}

	public void retrieveTravellingTicketList() {
		
		getSearchMachine().reloadData();
		
	}
	
	private ISearchBean<PmMachineSection> getSearchBeanData(
			ISearchBean<PmMachineSection> searchBean) {

		MachineSearchBean machineSearchBean = this.getManagedBean()
				.getMachineSearchBean();

		if (StringUtils.isNotBlank(machineSearchBean.getName())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmMachine.machineDesc",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getName()));	
			
		}

		if (StringUtils.isNotBlank(machineSearchBean.getCode())) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmMachine.machineCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getCode()));	
		}
		
		if (machineSearchBean.getCategoryId() != null) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("categoryType.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getCategoryId()));	
		}
		
		if (machineSearchBean.getTtStatus() != 0 ) {
		
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmMachine.recordStatus",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getTtStatus()));
			
		}

		return searchBean;
	}
	
	
	/**
	 * Navigate to Machine Maintenance Screen
	 * 
	 * @param PmMachine
	 *            selectedPmMachine
	 */
	public String navigateToMaintenance(PmMachineSection selectedPmMachineSection,PmMachine selectedMachine) {
		
		getListOfCategories();
		if ( selectedPmMachineSection != null && selectedMachine!=null ) {
			getManagedBean().setDisabledVal(true);
			
			maintainTravellingTicket(selectedPmMachineSection,selectedMachine);
		} else {
			getManagedBean().setDisabledVal(false);
			initialTravellingTicket();
		}
		
		return VIEW_MAINTENANCE_RESULT_PAGE;
	}
	
	public String saveTravellingTicket() {
		
		ITravellingTicketSO travelTicketSO = null;
		String message = null;
		String[] messageParam = null;
		Long machineId = null;
		
		try{
			
			
			
			travelTicketSO = (ITravellingTicketSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ITravellingTicketSO.class);
			
			MachineReqDTO machineReqDTO = new MachineReqDTO();
			
			PmMachine pmMachine = this.getManagedBean().getPmMachine();
			machineNameValidationMsg(pmMachine);
			pmMachine.setMachineCode(pmMachine.getMachineCode().toUpperCase());
			pmMachine.setMachineDesc(pmMachine.getMachineDesc().trim());
			machineReqDTO.setPmMachine(pmMachine);
			
			PmMachineSection pmMachineSection = this.getManagedBean().getPmMachineSection();
			machineReqDTO.setPmMachineSection(pmMachineSection);
			machineCategoryValidationMsg(pmMachineSection);
			
			MachineResDTO machineResDTO = travelTicketSO.saveOrUpdateTravelTicket(machineReqDTO);
			
			if (machineResDTO.isSuccessful()) {
				message = getManagedBean().getPmMachine().isBrandNew() 
						? MessageConst.ERR_900038 : MessageConst.ERR_900037;
				messageParam = new String[] {ResourceBundleUtils.getMessage(
						"lbl.pm.state", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()),
						this.getManagedBean().getPmMachine()
						.getMachineDesc()};
				machineId = getManagedBean().getPmMachine().getMachineId();
				
			}
			
			
			if(message != null && messageParam != null) {	
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										message, messageParam,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

			
			if(getManagedBean().isDisabledVal()){
				if(machineId != null ){
					maintainTravellingTicket(pmMachineSection,pmMachine);
				}
				return VIEW_MAINTENANCE_RESULT_PAGE;
			}
			else{
				this.getManagedBean().setPmMachine(null);
				this.getManagedBean().setPmMachineSection(null);
				return TRAVELLING_TICKET_MAIN;
			}
			
		} catch(Exception e){
			throw e;
		}
	}
	
	private void machineNameValidationMsg(PmMachine pmMachine) {
		debugLogger("machineValidationMsg");

		if (StringUtils.isBlank(pmMachine.getMachineDesc())) {
			throw new EpException(MessageConst.ERR_900035);
		}
		if (StringUtils.isBlank(pmMachine.getMachineCode())) {
			throw new EpException(MessageConst.ERR_900039);
		}
		if (null == pmMachine.getRecordStatusFlg() || 0 == pmMachine.getRecordStatusFlg()) {
			throw new EpException(MessageConst.ERR_900009);
		}
	}
	
	private void machineCategoryValidationMsg(PmMachineSection pmMachineSection) {
		debugLogger("machineValidationMsg");

		if (pmMachineSection.getCategoryType().getParameterId() == null) {
			throw new EpException(MessageConst.ERR_900036);
		}
	}
	
	public void initialTravellingTicket(){
		
		getManagedBean().setPmMachine(new PmMachine());
		getManagedBean().setPmMachineSection(new PmMachineSection());
		getManagedBean().getPmMachineSection().setCategoryType(new PmParameter());
		getManagedBean().getPmMachine().setRecordStatusFlg(1);
		
	}

	
	public void maintainTravellingTicket(PmMachineSection pmMachineSection,PmMachine pmMachine) {
		
		ITravellingTicketSO travelTicketSO;
		
		try{
			MachineReqDTO machineReqDTO = new MachineReqDTO();
			machineReqDTO.setPmMachineSection(pmMachineSection);
			machineReqDTO.setPmMachine(pmMachine);
			
			travelTicketSO =  (ITravellingTicketSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ITravellingTicketSO.class);
			
			
			MachineResDTO machineResDTO = new MachineResDTO();
			machineResDTO = travelTicketSO.retrieveTravelTicketByMachineId(machineReqDTO);
			
			getManagedBean().setPmMachine(machineResDTO.getPmMachine());
			getManagedBean().setPmMachineSection(machineResDTO.getPmMachineSection());	
			
		} catch(Exception e){
			throw new EpException(MessageConst.ERROR_776106);
		}finally{
			travelTicketSO = null;
		}
	}
	
	
	private class TravellingTicketPagingDataModel extends PagingDataModel<PmMachineSection> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new pending do paging data model.
		 */
		public TravellingTicketPagingDataModel() {
			super("ttTravellingListDt",getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<PmMachineSection> pageChangeListener(ISearchBean<PmMachineSection> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			ITravellingTicketSO travelTicketSO;
			
			try {
				
				ISearchBean<PmMachineSection> searchBeanData = getSearchBeanData(searchBean);
				MachineReqDTO machineReqDTO = new MachineReqDTO();
				machineReqDTO.setMachineSearchBean(searchBeanData);
				
				travelTicketSO = (ITravellingTicketSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ITravellingTicketSO.class);
				
				MachineResDTO resDTO = travelTicketSO
						.searchTravelTicketList(machineReqDTO);
				getManagedBean().setLazy(true);
				
				if(resDTO.isSuccessful()) {
					
					return resDTO.getTravelTicketResultBean();
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}
			
			return null;
		}
		
	}
	

}