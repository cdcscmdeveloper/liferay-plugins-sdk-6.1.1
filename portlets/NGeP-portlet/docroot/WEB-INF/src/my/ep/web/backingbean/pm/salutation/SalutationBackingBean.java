package my.ep.web.backingbean.pm.salutation;

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
import my.ep.biz.service.pm.common.salutation.ISalutationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.salutation.SalutationReqDTO;
import my.ep.dto.pm.common.salutation.SalutationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.salutation.SalutationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "salutationBackingBean")
@ViewScoped
public class SalutationBackingBean extends
		BaseBackingBean<SalutationManagedBean> { 
	
	private static final String NAV_TO_MAIN =  "salutationMain";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/salutation/salutationMaintenance";
	
	private static final Logger log = EpLogger
			.getLogger(SalutationManagedBean.class);	
	
	private IPagingDataModel<PmSalutation> salutationDataModel = new SalutationPagingDataModel();	
	/**
	 *  The constructor used for save user logging.
	 *  	 
	 **/
	public SalutationBackingBean() {
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
	 * reload to salutation search page 
	 * 
	 */		
	public String redirectSalSearch() {
		debugLogger("redirectSalSearch");
		//this.getManagedBean().setLazy(true);
		return NAV_TO_MAIN;

	}	
	
	/**
	 * Reset salutation search 
	 */
	public void resetSalSearch() {
		debugLogger("resetSalSearch");
		this.getManagedBean().setLazy(false);
        this.getManagedBean().setSalutationCodes(new String());
        this.getManagedBean().setSalutationNames(new String());
        this.getManagedBean().setStatus(null);

        salutationDataModel = new SalutationPagingDataModel();    
	}
	
	/**
	 * reload to salutation maintain page
	 * 
	 */	
	public String createSalutation() {
		debugLogger("createSalutation");
		PmSalutation pmSalutation= new PmSalutation();
		this.getManagedBean().setPmSalutation(pmSalutation);
		return "/xhtml/pm/salutation/salutationMaintenance.xhtml";
	}	

	/**
	 * get status item dropdown
	 * 
	 */	
	 
	 public static SelectItem[] getStatusList() {
			return PmCommonUtil.getCommonStatusOptions();
		}


		/**
		 * search salutation info based 
		 * on salutation codes and names search
		 */
		private ISearchBean<PmSalutation> getSearchBeanData(ISearchBean<PmSalutation> searchBean){
				
			if (StringUtils.isNotBlank(getManagedBean().getSalutationCodes())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("salutationCode");
				codesSearchBean
						.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean()
						.getSalutationCodes());
				searchBean.getDataFilterBeans().add(codesSearchBean);
			}

			if (StringUtils.isNotBlank(getManagedBean().getSalutationNames())) {
				SearchFilterBean codesSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				codesSearchBean
						.setSearchField("salutationName");
				codesSearchBean
						.setCaseInsensitive(true);
				codesSearchBean.setSearchValue(getManagedBean()
						.getSalutationNames());
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
	 
	public void retrieveSalutation(){
			debugLogger("retrieveSalutation");
			
			salutationDataModel.reloadData();
			
		}	 
	 
	 
	public IPagingDataModel<PmSalutation> getSalutationDataModel() {
		return salutationDataModel;
	}

	public void setSalutationDataModel(
			IPagingDataModel<PmSalutation> salutationDataModel) {
		this.salutationDataModel = salutationDataModel;
	}	

	
	private class SalutationPagingDataModel extends PagingDataModel<PmSalutation> {
		private static final long serialVersionUID = 1L;

		public SalutationPagingDataModel() {
			super("salutationSR",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmSalutation> pageChangeListener(ISearchBean<PmSalutation> searchBean) {

			try {	
				ISearchBean<PmSalutation> searchBeanData = getSearchBeanData(searchBean);
				SalutationReqDTO reqDTO = new SalutationReqDTO();
				

				reqDTO.setSalutationSearchBean(searchBeanData);
				
				ISalutationSO reqSO = (ISalutationSO) BusinessDelegate.getInstance().getServiceObject("pm", ISalutationSO.class);	
				SalutationResDTO resDTO = reqSO.retrieveSalutationBySearch(reqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {
					return resDTO.getSalutationSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}
	
	/**
	 * get salutation info into maintenance page
	 */
	public String goToMaintaincePage(PmSalutation pmSalutation) {
		debugLogger("goToMaintainceAction");
		this.getManagedBean().setPmSalutation(pmSalutation);

		return "/xhtml/pm/salutation/salutationMaintenance.xhtml";
	}	
	
	/**
	 * Validation message for salutation details
	 * 
	 */
	private void validationMessage(PmSalutation salutation){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(salutation.getSalutationCode())){
			throw new EpException(MessageConst.ERROR_100022,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.code")});
		}
		
		if(StringUtils.isBlank(salutation.getSalutationName())){
			throw new EpException(MessageConst.ERROR_100022,
					new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.name")});
		}	
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == salutation.getRecordStatusFlg()){
			if (isExistSalutationCode(salutation)) {
				if(salutation.isBrandNew()){
					throw new EpException(MessageConst.ERROR_779003);
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.code")
					});	
				}
			}
			
			if(isExistSalutationName(salutation)){
				if(salutation.isBrandNew()){
					throw new EpException(MessageConst.ERROR_770221, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.name"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.name")
					});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.sa.name")
					});	
				}
			}
		}
		
	}		
	
	/**
	 * save and update salutation details
	 * 
	 */
	 public String saveSaluationDtls(){
		 debugLogger("saveSaluationDtls");
		 ISalutationSO salutationSO = null;
		 String returnUrl = null;
		 
		 try{
			 PmSalutation salutation= this.getManagedBean().getPmSalutation();
			 validationMessage(salutation);
			 SalutationReqDTO reqDTO = new SalutationReqDTO();
			 reqDTO.setSalutation(salutation);
			 
			 salutationSO = EpEjbBusinessDelegate.getInstance().
					 getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ISalutationSO.class);
			 
			 SalutationResDTO resDTO= salutationSO.saveOrUpdateSaluationDtls(reqDTO);
			 this.getManagedBean().setPmSalutation(resDTO.getSalutation());
			 if(resDTO.isSuccessful()){
				 if (salutation.isBrandNew()) {
					 returnUrl = NAV_TO_MAIN;
					 PmCommonUtil.showFacesMsg(
							 FacesContext.getCurrentInstance(), 
							 FacesMessage.SEVERITY_INFO, 
							 MessageConst.INFO_779001, 
							 new String[] {
										resDTO.getSalutation().getSalutationCode(),
										resDTO.getSalutation().getSalutationCode()});
					}else{
						returnUrl = NAV_TO_MAINTAIN;
						PmCommonUtil.showFacesMsg(
								 FacesContext.getCurrentInstance(), 
								 FacesMessage.SEVERITY_INFO, 
								 MessageConst.INFO_779002, 
								 new String[] {
											resDTO.getSalutation().getSalutationCode(),
											resDTO.getSalutation().getSalutationCode()});					
					}
				}
		 }catch(Exception e){
			 throw e;
		 }finally{
			 salutationSO = null;
		 }
		 return returnUrl;
	 }
	 public boolean isExistSalutationCode(PmSalutation salutation) {
		 ISalutationSO reqSO = null;
			boolean isExist = false;
			try {
				SalutationReqDTO reqDTO = new SalutationReqDTO();
				reqSO = (ISalutationSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ISalutationSO.class);
				ISearchBean<PmSalutation> searchBean = new SearchBean<PmSalutation>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("salutationCode",
								SearchOperator.OPERATOR_EQUALS.value(), 
								salutation.getSalutationCode().toLowerCase().trim(),
								true
				));
				
				if(!salutation.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("salutationId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									salutation.getSalutationId()
					));
				}

				reqDTO.setSalutationSearchBean(searchBean);
				SalutationResDTO resDTO = reqSO.retrieveSalutationBySearch(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getSalutationList() !=null 
						&& !resDTO.getSalutationList().isEmpty()){
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
	 public void preLoadSalutation(){
			log.info("preLoadgroupSearch ....");
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
	 
	 public boolean isExistSalutationName(PmSalutation salutation) {
			ISalutationSO reqSO = null;
			boolean isExist = false;
			try {
				SalutationReqDTO reqDTO = new SalutationReqDTO();
				reqSO = (ISalutationSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ISalutationSO.class);
				ISearchBean<PmSalutation> searchBean = new SearchBean<PmSalutation>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), 
								DbValueConst.RECORD_STATUS_ACTIVE
				));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("salutationName",
								SearchOperator.OPERATOR_EQUALS.value(), 
								salutation.getSalutationName().toLowerCase().trim(),
								true
				));
				
				if(!salutation.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("salutationId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									salutation.getSalutationId()
					));
				}
				
				searchBean.getSortColumns().add(
						new SearchOrderBean("salutationName"));

				reqDTO.setSalutationSearchBean(searchBean);
				SalutationResDTO resDTO = reqSO.retrieveSalutationBySearch(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getSalutationList() !=null 
						&& !resDTO.getSalutationList().isEmpty()){
					isExist = true;
				}
				
			} finally {
				reqSO = null;
			}
			return isExist;
		}
	 
	 
}
