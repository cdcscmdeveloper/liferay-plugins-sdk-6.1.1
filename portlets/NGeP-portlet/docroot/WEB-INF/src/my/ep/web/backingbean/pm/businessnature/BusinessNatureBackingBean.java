package my.ep.web.backingbean.pm.businessnature;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.businessnature.BusinessNatureManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "businessNatureBackingBean")
@ViewScoped
public class BusinessNatureBackingBean extends BaseBackingBean<BusinessNatureManagedBean> {

	private static final Logger log = EpLogger
			.getLogger(BusinessNatureManagedBean.class);
	
	private IPagingDataModel<PmBusinessNature> businessNatureDataModel = new BusinessNaturePagingDataModel();	

	
	public BusinessNatureBackingBean() {
		log.info("inside constructor");

	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * reload to business nature search page 
	 * 
	 */		
	public String redirectBusinessSearch() {
		debugLogger("redirectBusinessSearch");

		return "/xhtml/pm/businessnature/businessNatureMain.xhtml?faces-redirect=true";

	}	
	
	/**
	 * reset search for Business Nature
	 * 
	 */
	public void resetBusinessNatureSearch(){
		debugLogger("resetBusinessNatureSeatch");
		
		this.getManagedBean().setSecLevelCode(new String());
		this.getManagedBean().setSecLevelName(new String());
		
		this.getManagedBean().setDivLevelCode(new String());
		this.getManagedBean().setDivLevelName(new String());
		
		this.getManagedBean().setGroupLevelCode(new String());
		this.getManagedBean().setGroupLevelName(new String());
		
		this.getManagedBean().setClassLevelCode(new String());
		this.getManagedBean().setClassLevelName(new String());
		
		this.getManagedBean().setItemLevelCode(new String());
		this.getManagedBean().setItemLevelName(new String());
		
		this.getManagedBean().setCategoryLvl(0L);
		
		businessNatureDataModel.reloadData();
	}
	
	
	/**
	 * reload to business nature maintain page
	 * 
	 */	
	public String createBusinessNature() {
		debugLogger("createBusinessNature");
		getManagedBean().setNewcategory(true);
		PmBusinessNature pmBusinessNature = new PmBusinessNature();
		pmBusinessNature.setBusinessLevel("10");

		this.getManagedBean().setPmBusinessNature(pmBusinessNature);
		this.getManagedBean().setBrandNew(true);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";

	}	
	
	public String createBusinessNature(PmBusinessNature pmBusinessNature) {
		
		PmBusinessNature pmBusinessNature2 = new PmBusinessNature();
		
		IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		try{
			
			if(Integer.valueOf(pmBusinessNature.getBusinessLevel())>10){
				BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
				businessNatureReqDTO.setBusinessLevel(pmBusinessNature.getBusinessLevel());
				businessNatureReqDTO.setBusinessNatureId(pmBusinessNature.getBusinessNatureId());
				BusinessNatureResDTO businessNatureResDTO = businessNatureSO.retrieveBusinessNatureFetchAll(businessNatureReqDTO);
				pmBusinessNature = businessNatureResDTO.getBusinessNature();
			}
			
			pmBusinessNature2.setParentBusinessNature(pmBusinessNature);
			pmBusinessNature2.setBusinessLevel(String.valueOf(Integer.valueOf(pmBusinessNature.getBusinessLevel())+10));
			this.getManagedBean().setPmBusinessNature(pmBusinessNature2);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			businessNatureSO = null;
		}
		
		
		this.getManagedBean().setBrandNew(true);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";

	}
	
	/**
	 * get status dropdown list
	 */
	public SelectItem[] getBusinessNatureStatus(){
		
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		
		return items;
	}
	
	/**
	 * get business level category list
	 */
	public SelectItem[] getBusinessNatureCategoryLv(){
		
		SelectItem[] items = new SelectItem[5];
		items[0] = new SelectItem(10, "SECTION");
		items[1] = new SelectItem(20, "DIVISION");
		items[2] = new SelectItem(30, "GROUP");
		items[3] = new SelectItem(40, "CLASS");
		items[4] = new SelectItem(50, "ITEM");
		
		return items;
	}	

	/**
	 * search business nature info based 
	 * on codes and names search
	 */
	private ISearchBean<PmBusinessNature> getSearchBeanData(ISearchBean<PmBusinessNature> searchBean){		
		
		if (getManagedBean().getCategoryLvl() != null && !"".equals(getManagedBean().getCategoryLvl())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean.setSearchField("businessLevel");
			codesSearchBean.setSearchValue(getManagedBean().getCategoryLvl());
			searchBean.getDataFilterBeans().add(codesSearchBean);
			
			//section
			if(getManagedBean().getCategoryLvl()==10L){
				if (getManagedBean().getSecLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getSecLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
			}
			
			//division
			if(getManagedBean().getCategoryLvl()==20L){
				if (getManagedBean().getDivLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getDivLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getSecLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getSecLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}
			
			//group
			if(getManagedBean().getCategoryLvl()==30L){
				if (getManagedBean().getGroupLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getGroupLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getDivLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getDivLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getSecLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getSecLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}
			
			//class
			if(getManagedBean().getCategoryLvl()==40L){
				if (getManagedBean().getClassLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getClassLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getClassLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getClassLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getClassLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getClassLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getGroupLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getGroupLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getDivLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getDivLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getSecLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getSecLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}
			
			//item
			if(getManagedBean().getCategoryLvl()==50L){
				if (getManagedBean().getItemLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getItemLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getItemLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getItemLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getItemLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getItemLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getClassLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getClassLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getClassLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getClassLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getClassLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getClassLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getGroupLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getGroupLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getGroupLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getGroupLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getDivLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getDivLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getDivLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getDivLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
				
				if (getManagedBean().getSecLevelCode() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelCode())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.parentBusinessNature.levelCode");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelCode());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}

				if (getManagedBean().getSecLevelName() != null && !"".equalsIgnoreCase(getManagedBean().getSecLevelName())) {
					codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
					codesSearchBean.setSearchField("parentBusinessNature.parentBusinessNature.parentBusinessNature.parentBusinessNature.levelName");
					codesSearchBean.setCaseInsensitive(true);
					codesSearchBean.setSearchValue(getManagedBean().getSecLevelName());
					searchBean.getDataFilterBeans().add(codesSearchBean);
				}
			}
		}

	return searchBean;
}	 		
	
	public void retrieveBusinessNature(){
		debugLogger("retrieveBusinessNature");
		
		businessNatureDataModel.reloadData();
		
	}

	public IPagingDataModel<PmBusinessNature> getBusinessNatureDataModel() {
		return businessNatureDataModel;
	}

	public void setBusinessNatureDataModel(
			IPagingDataModel<PmBusinessNature> businessNatureDataModel) {
		this.businessNatureDataModel = businessNatureDataModel;
	}

	private class BusinessNaturePagingDataModel extends PagingDataModel<PmBusinessNature> {
		private static final long serialVersionUID = 1L;

		public BusinessNaturePagingDataModel() {
			super("businessNatureDataModel");
		}

		@Override
		public ISearchResultBean<PmBusinessNature> pageChangeListener(ISearchBean<PmBusinessNature> searchBean) {

			try {	
				if (getManagedBean().getCategoryLvl() != null && !"".equals(getManagedBean().getCategoryLvl()) && getManagedBean().getCategoryLvl()>0L) {
					ISearchBean<PmBusinessNature> searchBeanData = getSearchBeanData(searchBean);
					BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
					reqDTO.setBusinessNatureSearchBean(searchBeanData);
					reqDTO.setBusinessLevel(getManagedBean().getCategoryLvl().toString());
					
					IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);	
					BusinessNatureResDTO  resDTO = reqSO.retrieveBusinessNatureBySearch(reqDTO);
					if (resDTO.isSuccessful()) {
						return resDTO.getBusinessNatureSearchResultBean();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * get business nature info into maintenance page
	 */
	public String goToBusinessMaintaincePage(PmBusinessNature pmBusinessNature) {
		getManagedBean().setNewcategory(false);
		debugLogger("goToBusinessMaintaincePage");
		IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		try{
			BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
			businessNatureReqDTO.setBusinessLevel(pmBusinessNature.getBusinessLevel());
			businessNatureReqDTO.setBusinessNatureId(pmBusinessNature.getBusinessNatureId());
			BusinessNatureResDTO businessNatureResDTO = businessNatureSO.retrieveBusinessNatureFetchAll(businessNatureReqDTO);
			
			this.getManagedBean().setPmBusinessNature(businessNatureResDTO.getBusinessNature());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			businessNatureSO = null;
		}
			
		this.getManagedBean().setBrandNew(false);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
	}	
	
	/**
	 * Validation message for business nature details
	 * 
	 */
	private boolean validationMessage(PmBusinessNature businessNature){

		IBusinessNatureSO businessNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		try{
			if(StringUtils.isBlank(businessNature.getLevelCode())){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			
			if(StringUtils.isBlank(businessNature.getLevelName())){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return false;
			}
			
			if(this.getManagedBean().isBrandNew()){
				BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
				businessNatureReqDTO.setBusinessLevel(businessNature.getBusinessLevel());
				businessNatureReqDTO.setLevelCode(businessNature.getLevelCode());
				BusinessNatureResDTO businessNatureResDTO = businessNatureSO.retrieveBusinessNatureListByLevel(businessNatureReqDTO);
				if(businessNatureResDTO.getBusinessNatureList().size()>0){
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(
								MessageConst.ERROR_775005,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
					return false;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			businessNatureSO = null;
		}
		
		return true;		
	}	
	
	/**
	 * save and update business nature detail
	 * 
	 */	
	public String saveBusinessNatureDtls(){
		
		IBusinessNatureSO iBusinessNatureSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);	
		try{
			PmBusinessNature businessnature = getManagedBean().getPmBusinessNature();
			
			if (!validationMessage(businessnature)){
				return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
			}
			
			
			BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
			reqDTO.setBusinessNature(businessnature);
			
			BusinessNatureResDTO resDTO = iBusinessNatureSO.saveBusinessNatureDtls(reqDTO);
			 this.getManagedBean().setPmBusinessNature(resDTO.getBusinessNature());
			 if(resDTO.isSuccessful()){
				 
				 if (this.getManagedBean().isBrandNew()){
					 if(Integer.valueOf(this.getManagedBean().getPmBusinessNature().getBusinessLevel())>10){
						 
						 BusinessNatureReqDTO businessNatureReqDTO = new BusinessNatureReqDTO();
						 businessNatureReqDTO.setBusinessLevel(this.getManagedBean().getPmBusinessNature().getParentBusinessNature().getBusinessLevel());
						 businessNatureReqDTO.setBusinessNatureId(this.getManagedBean().getPmBusinessNature().getParentBusinessNature().getBusinessNatureId());
						 BusinessNatureResDTO businessNatureResDTO = iBusinessNatureSO.retrieveBusinessNatureFetchAll(businessNatureReqDTO);
						 
						 businessNatureResDTO.getBusinessNature().getChildBusinessNatures().add(this.getManagedBean().getPmBusinessNature());
						 reqDTO.setBusinessNature(businessNatureResDTO.getBusinessNature());
						 iBusinessNatureSO.saveBusinessNatureDtls(reqDTO);
					 }
				 }
				 
				 FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_700004,
							new String[]{"",ResourceBundleUtils.getMessage("lbl.pm.category",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			 }
		 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			iBusinessNatureSO = null;
		}
		
		return "/xhtml/pm/businessnature/businessNatureMain.xhtml";
	 }
	
	public void updateCategoryLevel(){
		log.info("getManagedBean().getCategoryLvl() "+getManagedBean().getCategoryLvl());
	}
	
}
