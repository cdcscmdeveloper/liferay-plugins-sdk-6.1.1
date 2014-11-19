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
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.businessnature.BusinessNatureManagedBean;
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
		
		this.getManagedBean().setCategoryLvl(10L);
	}
	
	
	/**
	 * reload to business nature maintain page
	 * 
	 */	
	public String createBusinessNature() {
		debugLogger("createBusinessNature");
		PmBusinessNature pmBusinessNature = new PmBusinessNature();

		this.getManagedBean().setPmBusinessNature(pmBusinessNature);

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
			
			if(getManagedBean().getCategoryLvl()==20L){
				//division
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
				if (getManagedBean().getCategoryLvl() != null && !"".equals(getManagedBean().getCategoryLvl())) {
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
		debugLogger("goToBusinessMaintaincePage");
		this.getManagedBean().setPmBusinessNature(pmBusinessNature);

		return "/xhtml/pm/businessnature/businessNatureMaintenance.xhtml";
	}	
	
	/**
	 * Validation message for business nature details
	 * 
	 */
	private void validationMessage(PmBusinessNature businessNature){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(businessNature.getLevelCode())){
			throw new EpException("Business Nature Code is Mandatory");
		}
		
		if(StringUtils.isBlank(businessNature.getLevelName())){
			throw new EpException("Business Nature Name is Mandatory");
		}		
	}	
	
	/**
	 * save and update business nature detail
	 * 
	 */	
	public String saveBusinessNatureDtls(){
		
		PmBusinessNature businessnature = getManagedBean().getPmBusinessNature();
		validationMessage(businessnature);
		
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessNature(businessnature);
		
		IBusinessNatureSO iBusinessNatureSO= EpEjbBusinessDelegate.getInstance().
		getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IBusinessNatureSO.class);	
		
		BusinessNatureResDTO resDTO = iBusinessNatureSO.saveBusinessNatureDtls(reqDTO);
		 this.getManagedBean().setPmBusinessNature(resDTO.getBusinessNature());
		 if(resDTO.isSuccessful()){
				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,"","Saved Successfully"));
			}
		
			return "/xhtml/pm/businessnature/businessNatureMain.xhtml";
	 }
	
	public void updateCategoryLevel(){
		log.info("getManagedBean().getCategoryLvl() "+getManagedBean().getCategoryLvl());
	}
	
}
