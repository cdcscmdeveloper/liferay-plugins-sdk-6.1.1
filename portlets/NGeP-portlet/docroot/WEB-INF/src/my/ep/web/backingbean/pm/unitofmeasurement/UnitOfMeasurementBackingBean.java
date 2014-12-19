package my.ep.web.backingbean.pm.unitofmeasurement;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.unitofmeasurement.UnitOfMeasurementManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;


/**
 * @author DK
 * 
 */
@ManagedBean(name = "unitOfMeasurementBackingBean")
@ViewScoped
public class UnitOfMeasurementBackingBean extends
		BaseBackingBean<UnitOfMeasurementManagedBean> {
	
	private static final Logger log = EpLogger
			.getLogger(UnitOfMeasurementManagedBean.class);
	//Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/unitofmeasurement/unitOfMeasurementMain.xhtml";
		
	private IPagingDataModel<PmUom> unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
	public static final boolean SEARCH_DEFAULT = false;
	
	public UnitOfMeasurementBackingBean() {
		log.info("inside constructor");

	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * load clean view
	 */
	public void preLoading(){
		debugLogger("preLoading");
		this.resetMeasurementSearch();
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}


	/**
	 * reload to UOM search page 
	 * 
	 */		
	public String redirectMeasurementSearch() {
		debugLogger("redirectMeasurementSearch");

		return NAV_TO_SEARCH;

	}	
	
	/**
	 * Reset measurement search 
	 */
	public void resetMeasurementSearch() {
		debugLogger("resetMeasurementSearch");
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setMeasurementCodes(new String());
		this.getManagedBean().setMeasurementCodes(new String());
		unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
		
		
	}	
	
	/**
	 * get unit of measurement 
	 * measurement status item dropdown
	 * 1-active
	 * 2-inactive
	 * 
	 */	
	 public SelectItem[] getMeasurementStatusList() {
		 
			SelectItem[] items =  new SelectItem[2];
			items[0] = new SelectItem(1, "Active");
			items[1] = new SelectItem(0, "Inactive");
		    return items;
		}	
	 
		/**
		 * get unit of measurement 
		 * measurement status item dropdown
		 * 1-active
		 * 2-inactive
		 * 
		 */	
		 public SelectItem[] getMeasurementDecimal() {
			 
				SelectItem[] items =  new SelectItem[4];
				items[0] = new SelectItem(0, "0");
				items[1] = new SelectItem(1, "1");
				items[2] = new SelectItem(2, "2");
				items[3] = new SelectItem(3, "3");
			    return items;
			}		 
	
	/**
	 * reload to UOM maintain page
	 * 
	 */	
	public String createMeasurement() {
		debugLogger("createMeasurement");
		PmUom pmUom = new PmUom();
		this.getManagedBean().setPmUom(pmUom);

		return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";

	}

	/**
	 * search unit of measurement info based 
	 * on UOM codes and names search
	 */
	private ISearchBean<PmUom> getMeasurementSearchBeanData(ISearchBean<PmUom> searchBean){
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		if (getManagedBean().getMeasurementCodes() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementCodes())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomCode");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementCodes());
			searchBean.getDataFilterBeans().add(uomSearchBean);
			isEmpty = false;
		}

		if (getManagedBean().getMeasurementNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementNames())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomName");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementNames());
			searchBean.getDataFilterBeans().add(uomSearchBean);
			isEmpty = false;
		}	
		
		if(getManagedBean().getStatus() != null) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean
					.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(getManagedBean()
					.getStatus());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}			
		
		if(isEmpty && !isSearchable && getManagedBean().getStatus()==null){
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean
					.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}
		return searchBean;
	}		
	
	public void retrieveMeasurement(){
		debugLogger("retrieveMeasurement");
		
		unitOfMeasurementDataModel.reloadData();
		
	}		

	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		return unitOfMeasurementDataModel;
	}

	public void setUnitOfMeasurementDataModel(
			IPagingDataModel<PmUom> unitOfMeasurementDataModel) {
		this.unitOfMeasurementDataModel = unitOfMeasurementDataModel;
	}
	
	private class UnitOfMeasurementPagingDataModel extends PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR");
		}

		@Override
		public ISearchResultBean<PmUom> pageChangeListener(ISearchBean<PmUom> searchBean) {

			try {	
				ISearchBean<PmUom> searchBeanData = getMeasurementSearchBeanData(searchBean);
				UomReqDTO reqDTO = new UomReqDTO();
				

				reqDTO.setMeasurementSearchBean(searchBeanData);
				
				IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject("pm", IUomSO.class);	
				UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getMeasurementSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * get unit of measurement info 
	 * into maintenance page (PmUom pmUom)
	 */	
	public String goToUnitMaintaincePage(PmUom pmUom){
		debugLogger("goToUnitMaintaincePage");
		this.getManagedBean().setPmUom(pmUom);

		return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";		
	}
	
	/**
	 * Validation message for unit of measurement details
	 * 
	 */
	private void validationMessage(PmUom uom){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(uom.getUomCode())){
			throw new EpException("UOM Code is Mandatory");
		}
		
		if(StringUtils.isBlank(uom.getUomName())){
			throw new EpException("UOM Name is Mandatory");
		}
		
//		if(uom.isBrandNew()){
			uniqueUOMCode(uom.getUomCode(), uom.getUomId());
//		}
		
	}	
	private void uniqueUOMCode(String uomCode, Long uomId){

		ISearchBean<PmUom> searchBean =  new SearchBean<PmUom>();
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"uomCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						uomCode, true));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						1));
		if(uomId!=null){
			System.out.println("uomId: " +uomId);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"uomId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							uomId));
		}
		
		
		UomReqDTO reqDTO = new UomReqDTO();
		reqDTO.setMeasurementSearchBean(searchBean);
		
		IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject("pm", IUomSO.class);	
		UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
		if (resDTO.isSuccessful()) {
			if(resDTO.getMeasurementSearchResultBean()!=null && resDTO.getMeasurementSearchResultBean().getResultList()!=null 
					&& !resDTO.getMeasurementSearchResultBean().getResultList().isEmpty()){
				throw new EpException("UOM Code is existing.");
			}
		}
	}
	
	/**
	 * save and update unit of measurement details
	 * 
	 */
	public String saveMeasurementDtls() {
		debugLogger("saveMeasurementDtls");
		
		PmUom uom = this.getManagedBean().getPmUom();
		validationMessage(uom);
		
		UomReqDTO reqDTO = new UomReqDTO();
		reqDTO.setPmUom(uom);

		IUomSO iUomSO = EpEjbBusinessDelegate.getInstance().
				getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);
	
		UomResDTO resDTO = iUomSO.saveOrUpdateMeasurement(reqDTO);
				this.getManagedBean().setPmUom(resDTO.getPmUom());
		 
		 if(resDTO.isSuccessful()){
			 FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,"","Saved Successfully"));
			 
			}
	
		  return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMain.xhtml";
	}	

}
