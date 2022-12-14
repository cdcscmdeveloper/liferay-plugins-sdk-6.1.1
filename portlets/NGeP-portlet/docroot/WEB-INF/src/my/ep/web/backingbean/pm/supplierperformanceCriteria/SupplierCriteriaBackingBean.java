package my.ep.web.backingbean.pm.supplierperformanceCriteria;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.supplierperformanceCriteria.ISupplierCriteriaSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.supplierperformanceCriteria.SupplierCriteriaReqDTO;
import my.ep.dto.pm.common.supplierperformanceCriteria.SupplierCriteriaResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmSupplierCriteria;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;

import my.ep.web.managedbean.pm.supplierperformanceCriteria.SupplierCriteriaManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "supplierCriteriaBackingBean")
@ViewScoped
public class SupplierCriteriaBackingBean extends
		BaseBackingBean<SupplierCriteriaManagedBean> implements Serializable { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_MAIN =  "supplierCriteriaMain";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/supplierperformanceCriteria/supplierCriteriaMaintenance";
	
	private static final Logger log = EpLogger
			.getLogger(SupplierCriteriaManagedBean.class);	
	
	private IPagingDataModel<PmSupplierCriteria> supplierCriteriaDataModel = new SupplierCriteriaPagingDataModel();	
	public IPagingDataModel<PmSupplierCriteria> getSupplierCriteriaDataModel() {
		return supplierCriteriaDataModel;
	}

	public void setSupplierCriteriaDataModel(
			IPagingDataModel<PmSupplierCriteria> supplierCriteriaDataModel) {
		this.supplierCriteriaDataModel = supplierCriteriaDataModel;
	}

	/**
	 *  The constructor used for save user logging.
	 *  	 
	 **/
	public SupplierCriteriaBackingBean() {
		log.info("inside constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			log.info("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}	
	
	/**
	 * reload to SupplierCriteria search page 
	 * 
	 */		
	public String redirectSupCriteriaSearch() {
		debugLogger("redirectSupCriteriaSearch");
		//this.getManagedBean().setLazy(true);
		return NAV_TO_MAIN;

	}	
	
	/**
	 * Reset supplierCriteria search 
	 */
	public void resetSupCriteriaSearch() {
		debugLogger("resetSalSearch");
		this.getManagedBean().setLazy(false);
        this.getManagedBean().setSupplierCriteriaCodes(new String());
        this.getManagedBean().setSupplierCriteriaNames(new String());
        this.getManagedBean().setStatus(null);

        supplierCriteriaDataModel = new SupplierCriteriaPagingDataModel();    
	}
	
	/**
	 * reload to Supplier Criteria maintain page
	 * 
	 */	
	public String createSupplierCriteria() {
		debugLogger("createSupplierCriteria");
		PmSupplierCriteria pmSupplierCriteria= new PmSupplierCriteria();
		this.getManagedBean().setPmSupplierCriteria(pmSupplierCriteria);
		return "/xhtml/pm/supplierperformanceCriteria/supplierCriteriaMaintenance.xhtml";
	}	
    
	
	
	
	/**
	 * get status item dropdown
	 * 
	 */	
	 
	 public static SelectItem[] getStatusList() {
			return PmCommonUtil.getCommonStatusOptions();
		}


		/**
		 * search SupplierCriteria info based 
		 * on SupplierCriteria codes and names search
		 */
		private ISearchBean<PmSupplierCriteria> getSearchBeanData(ISearchBean<PmSupplierCriteria> searchBean){
				
			if (StringUtils.isNotBlank(getManagedBean().getSupplierCriteriaCodes())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("supplierCriteriaCode");
				codesSearchBean
						.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean()
						.getSupplierCriteriaCodes());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getSupplierCriteriaNames())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("supplierCriteriaName");
				codesSearchBean
						.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean()
						.getSupplierCriteriaNames());
				searchBean.getDataFilterBeans().add(codesSearchBean);
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
			else{
				List<String> activeAndInActiveStatus = new ArrayList<String>();
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_IN.value(), 
								activeAndInActiveStatus
				));
			}

			return searchBean;
		}	 
	 
	public void retrieveSupplierCriteria(){
			debugLogger("retrieveSupplierCriteria");
			
			supplierCriteriaDataModel.reloadData();
			
		}	 
	 
	 
	
	private class SupplierCriteriaPagingDataModel extends PagingDataModel<PmSupplierCriteria> {
		private static final long serialVersionUID = 1L;

		public SupplierCriteriaPagingDataModel() {
			super("supplierCriteriaSR",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmSupplierCriteria> pageChangeListener(ISearchBean<PmSupplierCriteria> searchBean) {

			try {	
				ISearchBean<PmSupplierCriteria> searchBeanData = getSearchBeanData(searchBean);
				SupplierCriteriaReqDTO reqDTO = new SupplierCriteriaReqDTO();
				

				reqDTO.setSupplierCriteriaSearchBean(searchBeanData);
				
				ISupplierCriteriaSO reqSO = (ISupplierCriteriaSO) BusinessDelegate.getInstance().getServiceObject("pm", ISupplierCriteriaSO.class);	
				SupplierCriteriaResDTO resDTO = reqSO.retrieveSupplierCriteriaBySearch(reqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {
					return resDTO.getSupplierCriteriaSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * get supplierCriteria info into maintenance page
	 */
	public String goToMaintaincePage(PmSupplierCriteria pmSupplierCriteria) {
		debugLogger("goToMaintainceAction");
		this.getManagedBean().setPmSupplierCriteria(pmSupplierCriteria);

		return "/xhtml/pm/supplierperformanceCriteria/supplierCriteriaMaintenance.xhtml";
	}	
	
	/**
	 * Validation message for supplierCriteria details
	 * 
	 */
	private void validationMessage(PmSupplierCriteria supplierCriteria){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(supplierCriteria.getSupplierCriteriaCode())){
			throw new EpException(MessageConst.ERROR_100022,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.code")});
		}
		
		if(StringUtils.isBlank(supplierCriteria.getSupplierCriteriaName())){
			throw new EpException(MessageConst.ERROR_100022,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.name")});
		}	
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == supplierCriteria.getRecordStatusFlg()){
			if (isExistSupplierCriteriaCode(supplierCriteria)) {
				if(supplierCriteria.isBrandNew()){
					throw new EpException(MessageConst.ERROR_779007);
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.code")
					});	
				}
			}
			
			if(isExistSupplierCriteriaName(supplierCriteria)){
				if(supplierCriteria.isBrandNew()){
					throw new EpException(MessageConst.ERROR_770221, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.name"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.name")
					});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.criteria.name")
					});	
				}
			}
		}
		
	}		

	/**
	 * save and update SupplierCriteria details
	 * 
	 */
	 public String saveSupplierCriteriaDtls(){
		 debugLogger("saveSupplierCriteriaDtls");
		 ISupplierCriteriaSO supplierCriteriaSO = null;
		 String returnUrl = null;
		 
		 try{
			 PmSupplierCriteria supplierCriteria= this.getManagedBean().getPmSupplierCriteria();
		        validationMessage(supplierCriteria);
			 SupplierCriteriaReqDTO reqDTO = new SupplierCriteriaReqDTO();
			 reqDTO.setSupplierCriteria(supplierCriteria);
			 
			 supplierCriteriaSO = EpEjbBusinessDelegate.getInstance().
					 getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ISupplierCriteriaSO.class);
			 
			 SupplierCriteriaResDTO resDTO= supplierCriteriaSO.saveOrUpdateSupplierCriteriaDtls(reqDTO);
			 this.getManagedBean().setPmSupplierCriteria(resDTO.getSupplierCriteria());
			 if(resDTO.isSuccessful()){
				 if (supplierCriteria.isBrandNew()) {
					 returnUrl = NAV_TO_MAIN;
					 PmCommonUtil.showFacesMsg(
							 FacesContext.getCurrentInstance(), 
							 FacesMessage.SEVERITY_INFO, 
							 MessageConst.INFO_779005, 
							 new String[] {
										resDTO.getSupplierCriteria().getSupplierCriteriaCode(),
										resDTO.getSupplierCriteria().getSupplierCriteriaCode()});
					}else{
						returnUrl = NAV_TO_MAINTAIN;
						PmCommonUtil.showFacesMsg(
								 FacesContext.getCurrentInstance(), 
								 FacesMessage.SEVERITY_INFO, 
								 MessageConst.INFO_779006, 
								 new String[] {
											resDTO.getSupplierCriteria().getSupplierCriteriaCode(),
											resDTO.getSupplierCriteria().getSupplierCriteriaCode()});					
					}
				}
		 }catch(Exception e){
			 throw e;
		 }finally{
			 supplierCriteriaSO = null;
		 }
		 return returnUrl;
	 }
	 public boolean isExistSupplierCriteriaCode(PmSupplierCriteria supplierCriteria) {
		 ISupplierCriteriaSO reqSO = null;
			boolean isExist = false;
			try {
				SupplierCriteriaReqDTO reqDTO = new SupplierCriteriaReqDTO();
				reqSO = (ISupplierCriteriaSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ISupplierCriteriaSO.class);
				ISearchBean<PmSupplierCriteria> searchBean = new SearchBean<PmSupplierCriteria>();
			
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("supplierCriteriaCode",
								SearchOperator.OPERATOR_EQUALS.value(), 
								supplierCriteria.getSupplierCriteriaCode().toLowerCase().trim(),
								true
				));
			
				
				if(!supplierCriteria.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("supplierCriteriaId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									supplierCriteria.getSupplierCriteriaId()
					));
				}
			
				reqDTO.setSupplierCriteriaSearchBean(searchBean);
			
				SupplierCriteriaResDTO resDTO = reqSO.retrieveSupplierCriteriaBySearch(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getSupplierCriteriaList() !=null 
						&& !resDTO.getSupplierCriteriaList().isEmpty()){
					isExist = true;
				}
				
			} finally {
				reqSO = null;
			}
			return isExist;
	}
	 /**
		 *  This method used for reset the search bean field and
		 *  navigate to the search main page. 
		 * 
		 **/
	 public void preLoadSupplierCriteria(){
			log.info("preLoadgroupSearch....");
			renewSesionObject();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_TO_MAIN);
		}
	 
	 public void setUserDetails(BaseEntity baseObject) {
			if (baseObject.isBrandNew()) {
				baseObject.setCreatedBy(1L);
				baseObject
						.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			}
			baseObject.setChangedBy(1L);
	}
	 
	 public boolean isExistSupplierCriteriaName(PmSupplierCriteria supplierCriteria) {
			ISupplierCriteriaSO reqSO = null;
			boolean isExist = false;
			try {
				SupplierCriteriaReqDTO reqDTO = new SupplierCriteriaReqDTO();
				reqSO = (ISupplierCriteriaSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ISupplierCriteriaSO.class);
				ISearchBean<PmSupplierCriteria> searchBean = new SearchBean<PmSupplierCriteria>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), 
								DbValueConst.RECORD_STATUS_ACTIVE
				));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("supplierCriteriaName",
								SearchOperator.OPERATOR_EQUALS.value(), 
								supplierCriteria.getSupplierCriteriaName().toLowerCase().trim(),
								true
				));
				
				if(!supplierCriteria.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("supplierCriteriaId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									supplierCriteria.getSupplierCriteriaId()
					));
				}
				
				searchBean.getSortColumns().add(
						new SearchOrderBean("supplierCriteriaName"));

				reqDTO.setSupplierCriteriaSearchBean(searchBean);
				SupplierCriteriaResDTO resDTO = reqSO.retrieveSupplierCriteriaBySearch(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getSupplierCriteriaList() !=null 
						&& !resDTO.getSupplierCriteriaList().isEmpty()){
					isExist = true;
				}
				
			} finally {
				reqSO = null;
			}
			return isExist;
		}
	 
	 
}
