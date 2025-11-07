package my.ep.web.backingbean.sm.travellingTicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.travel.ITravellingTicketSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.mofAccount.MachineReqDTO;
import my.ep.dto.sm.mofAccount.MachineResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineSection;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.MachineSearchBean;
import my.ep.web.managedbean.sm.travellingTicket.TravellingTicketManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
		resetMachineSearchBean();
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
		ParameterReqDTO paramReq = new ParameterReqDTO();
		
		
		if(CollectionUtils.isNotEmpty(this.getManagedBean().getListOfCategories())){
			return this.getManagedBean().getListOfCategories();
		}

		IParameterSO listOfCategoriesSO = null;

			try {

				listOfCategoriesSO = (IParameterSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IParameterSO.class);
				
				paramReq.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
				ParameterResDTO resDTO = listOfCategoriesSO.retrieveListOfTravelCategoryType(paramReq);
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

			/*searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmMachine.machineDesc",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getName()));	*/
			
			SearchFilterBean filterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			filterBean.setSearchField("pmMachine.machineDesc");
			filterBean.setCaseInsensitive(true);
			filterBean.setSearchValue(getManagedBean().getMachineSearchBean().getName());
			searchBean.getDataFilterBeans().add(filterBean);
			
		}

		if (StringUtils.isNotBlank(machineSearchBean.getCode())) {

			SearchFilterBean filterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			filterBean.setSearchField("pmMachine.machineCode");
			filterBean.setCaseInsensitive(true);
			filterBean.setSearchValue(getManagedBean().getMachineSearchBean().getCode());
			searchBean.getDataFilterBeans().add(filterBean);
			
		}
		
		if (machineSearchBean.getCategoryId() != null) {

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("categoryType.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(),
							machineSearchBean.getCategoryId()));	
		}
		
		if (machineSearchBean.getTtStatus() != null
				&& machineSearchBean.getTtStatus().trim().length() > 0) {
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmMachine.recordStatus", SearchOperator.OPERATOR_EQUALS
						.value(), machineSearchBean
						.getTtStatus()));
		 }
			 else{
					SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
					codesSearchBean	.setSearchField("pmMachine.recordStatus");
					codesSearchBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
					searchBean.getDataFilterBeans().add(codesSearchBean);
					
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
			if(this.getManagedBean().getSelectedMachine().isBrandNew()){
				this.generateMachineCode();
			}
			pmMachine.setMachineCode(pmMachine.getMachineCode().toUpperCase());
			pmMachine.setMachineDesc(pmMachine.getMachineDesc().trim());
			machineReqDTO.setPmMachine(pmMachine);
			
			PmMachineSection pmMachineSection = this.getManagedBean().getPmMachineSection();
			machineReqDTO.setPmMachineSection(pmMachineSection);
			machineCategoryValidationMsg(pmMachineSection);
			
			MachineResDTO machineResDTO = travelTicketSO.saveOrUpdateTravelTicket(machineReqDTO);
			
			if (machineResDTO.isSuccessful()) {
				message = getManagedBean().getPmMachine().isBrandNew() 
						? MessageConst.INFO_900038 : MessageConst.INFO_900037;
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
		//#34445 Start
		} catch (EpException e) {
			handleException(e);
			return VIEW_MAINTENANCE_RESULT_PAGE;
		//#34445 End
		} catch(Exception e){
			throw e;
		}
	}
	private void machineNameValidationMsg(PmMachine pmMachine) {
		debugLogger("machineValidationMsg");

		if (StringUtils.isBlank(pmMachine.getMachineDesc())) {
			throw new EpException(MessageConst.ERR_900035);
		}
		/*if (StringUtils.isBlank(pmMachine.getMachineCode())) {
			throw new EpException(MessageConst.ERR_900039);
		}
		if (null == pmMachine.getRecordStatusFlg() || 0 == pmMachine.getRecordStatusFlg()) {
			throw new EpException(MessageConst.ERR_900009);
		}*/
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
	 public static SelectItem[] getStatusList() {
			return PmCommonUtil.getCommonStatusOptions();
		}
	
	 private void generateMachineCode() {
		 ITravellingTicketSO machineSO = (ITravellingTicketSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ITravellingTicketSO.class);
			IParameterSO parameterSO  = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			MachineReqDTO reqDTO = new MachineReqDTO();
			//reqDTO.setCode(MachineCategory.valueOf(this.getManagedBean().getTestCheck().toString()));
			
			ParameterReqDTO paramReq = new ParameterReqDTO();
			String machineCode = null;
			paramReq.setParameterId(getManagedBean().getPmMachineSection().getCategoryType().getParameterId());
			ParameterResDTO paramRes = parameterSO.retrieveParamById(paramReq);	
			
			if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRANSPORTATION)){
			machineCode = ProfileManagementConst.CATEGORY_TYPE_TRANSPORTATION;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_WORKSHOP)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_WORKSHOP;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_INSURANCE)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_INSURANCE;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_DOCKYARD)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_DOCKYARD;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_MANUFACTURING)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_MANUFACTURING;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_NORMAL)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_NORMAL;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_PRINTING)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_PRINTING;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_TRAVEL;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_BUMIPUTRA)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_BUMIPUTRA;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_READING_MATERIAL)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_READING_MATERIAL;
			}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_NOT_APPLICABLE)){
				machineCode = ProfileManagementConst.CATEGORY_TYPE_NOT_APPLICABLE;
			}
			reqDTO.setMachineCode(machineCode);
			//reqDTO.setMachineCode(paramRes.getPmParameter().getParameterCode());
			MachineResDTO resDTO = machineSO.generateMachineCode(reqDTO);
			this.getManagedBean().getPmMachine().setMachineCode(resDTO.getMachineCode());
}
}