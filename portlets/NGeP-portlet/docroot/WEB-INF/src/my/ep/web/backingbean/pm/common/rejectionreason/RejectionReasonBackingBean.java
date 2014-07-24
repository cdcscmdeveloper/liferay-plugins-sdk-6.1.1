/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.rejectionreason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.language.ILanguageSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.language.LanguageResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ReasonSearchBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
/**
 * Backing Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2014
 *
 */

@ManagedBean(name="rejectionReasonBackingBean")
@ViewScoped
public class RejectionReasonBackingBean extends BaseBackingBean<RejectionReasonManagedBean>{
	private static Logger log = Logger.getLogger(RejectionReasonBackingBean.class);
	//Navigation Details
	public static final String NAV_TO_MAINTAIN = "rejectionReasonMaintain";
	public static final String NAV_TO_SEARCH = "rejectionReason";
	//Mapper for DDL
	private static Map<Long, Object> reasonCategoryMapper;
	//List for Paging
	private IPagingDataModel<PmReasonDesc> pmReasonDescDataMode = new PmReasonPagingDataModel();

	public void preLoading(){
		log.info("preLoading");
		//load critical mappers
		this.loadReasonCategoryMapper();
		this.resetReasonSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	
	public RejectionReasonBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * redirect to Rejection Reason Details
	 * @param id
	 * @return xhtml
	 */
	public String redirectToMaintainReason(Long id){
		log.info("redirectToMaintainReason - Start");
		try{
			this.loadReasonCategoryMapper();
			if(id!=null && id>0){
				loadRejectReason(id);
			}else{
				initialRejectReason();
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToMaintainReason - End");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * redirect to Rejection Reason Search
	 * @return xhtml
	 */
	public String redirectToSearchReason(){
		log.info("redirectToSearchReason - Start");
		try{
			
			
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToSearchReason - End");
		return NAV_TO_SEARCH;
	}
	
	/**
	 * initial new rejection reason object
	 */
	public void initialRejectReason(){
		log.info("initialRejectReason - Start");
		PmReason pmReason = new PmReason();
		pmReason.setReasonCategoryCode(new PmParameter());
		
		//set languages into description
		List<PmLanguage> languages = retrieveLanguageObjs();
		pmReason.setPmReasonDescs(new ArrayList<PmReasonDesc>());
		for(PmLanguage language: languages){
			PmReasonDesc pmReasonDesc = new PmReasonDesc();
			pmReasonDesc.setLanguageCode(language.getLanguageCode());
			pmReasonDesc.setPmLanguage(language);
			pmReason.addPmReasonDescs(pmReasonDesc);
		}
		this.getManagedBean().setSelectedReason(pmReason);
		this.getManagedBean().setAppTypeRestrictions(null);
		this.getManagedBean().setComTypeRestrictions(null);
		this.getManagedBean().setSuppTypeRestrictions(null);
		this.getManagedBean().setSuppReason(false);
		log.info("initialRejectReason - End");
	}
	
	/**
	 * load rejection reason object by id
	 */
	public void loadRejectReason(Long id){
		log.info("loadRejectReason - Start");
		try{
			PmReason pmReason = retrieveReasonObjById(id);
			//set supplier type flag
			this.getManagedBean().setSuppReason( 
					(pmReason.isByAppType() || pmReason.isByRegType() || pmReason.isBySupplierType()) 
					? true : false);
			//add all languages to description list
			List <PmLanguage> languagues = retrieveLanguageObjs();
			if(pmReason.getPmReasonDescs().size() != languagues.size()){
				for(PmLanguage langauge:languagues){
					boolean isRequired = true;
					for(PmReasonDesc desc: pmReason.getPmReasonDescs()){
						if(langauge.getLanguageCode().equals(desc.getLanguageCode())){
							isRequired = false;
							break;
						}
					}
					if(isRequired){
						PmReasonDesc reasonDesc = new PmReasonDesc();
						reasonDesc.setLanguageCode(langauge.getLanguageCode());
						reasonDesc.setPmLanguage(langauge);
						pmReason.addPmReasonDescs(reasonDesc);
					}
				}
			}
			this.getManagedBean().setSelectedReason(pmReason);
			loadRestriction(pmReason);
//			this.getManagedBean().setAppTypeRestrictions(loadRestriction(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES, id));
//			this.getManagedBean().setComTypeRestrictions(loadRestriction(ParameterTypeConst.COMPANY_TYPES, id));
//			this.getManagedBean().setSuppTypeRestrictions(loadRestriction(ParameterTypeConst.SUPPLIER_TYPES, id));
		}catch (Exception e){
			throw new EpException(MessageConst.ERROR_770004);
		}
		
		log.info("loadRejectReason - End");
	}
	
	/**
	 * load the pm supplier reasons to tab view by restriction type
	 * @param pmReason
	 */
	public void loadRestriction(PmReason pmReason){
		log.info("loadRestriction - Start");
		if(pmReason!=null){
			List <PmSupplierReason> appTypeRestrictions = new ArrayList<PmSupplierReason>();
			List <PmSupplierReason> comTypeRestrictions = new ArrayList<PmSupplierReason>();
			List <PmSupplierReason> suppTypeRestrictions = new ArrayList<PmSupplierReason>();
			for(PmSupplierReason pmSuppReason:pmReason.getPmSupplierReasons()){
				if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
					appTypeRestrictions.add(pmSuppReason);
				}else if(ParameterTypeConst.COMPANY_TYPES.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
					comTypeRestrictions.add(pmSuppReason);
				}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
					suppTypeRestrictions.add(pmSuppReason);
				}
			}
			this.getManagedBean().setAppTypeRestrictions(appTypeRestrictions);
			this.getManagedBean().setComTypeRestrictions(comTypeRestrictions);
			this.getManagedBean().setSuppTypeRestrictions(suppTypeRestrictions);
		}
		log.info("loadRestriction - End");
	}
	
	/**
	 * collect all 3 restriction type to one list
	 */
	private void getAllRestrictions(){
		log.info("getAllRestrictions");
		try{
			//validate before getting all
			validateRestriction(this.getManagedBean().getAppTypeRestrictions(), ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
			validateRestriction(this.getManagedBean().getComTypeRestrictions(), ParameterTypeConst.COMPANY_TYPES);
			validateRestriction(this.getManagedBean().getSuppTypeRestrictions(), ParameterTypeConst.SUPPLIER_TYPES);
			
			
			//for application type
			if(this.getManagedBean().getAppTypeRestrictions()!=null && this.getManagedBean().getAppTypeRestrictions().size()>0){
				this.getManagedBean().getSelectedReason().setByAppType(true);
				for(PmSupplierReason tempReason:this.getManagedBean().getAppTypeRestrictions()){
					if(tempReason.isBrandNew()){
						if(!tempReason.isSelected()){
							if(this.getManagedBean().getSelectedReason().getPmSupplierReasons()==null){
								this.getManagedBean().getSelectedReason().setPmSupplierReasons(new ArrayList<PmSupplierReason>());
							}
							this.getManagedBean().getSelectedReason().addPmSupplierReason(tempReason);
							tempReason.setSelected(true);
						}
					}else{
						if(!tempReason.isSelected()){
							for (int i=0; i <this.getManagedBean().getSelectedReason().getPmSupplierReasons().size(); i++){
								if(this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i).getSupplierReasonId()
										.equals(tempReason.getSupplierReasonId())){
									this.getManagedBean().getSelectedReason().getPmSupplierReasons().set(i, tempReason);
								}
							}
							tempReason.setSelected(true);
						}
					}
				}
			}else{
				this.getManagedBean().getSelectedReason().setByAppType(false);
			}
			
			//for company type
			if(this.getManagedBean().getComTypeRestrictions()!=null && this.getManagedBean().getComTypeRestrictions().size()>0){
				this.getManagedBean().getSelectedReason().setByRegType(true);
				for(PmSupplierReason tempReason:this.getManagedBean().getComTypeRestrictions()){
					if(tempReason.isBrandNew()){
						if(!tempReason.isSelected()){
							if(this.getManagedBean().getSelectedReason().getPmSupplierReasons()==null){
								this.getManagedBean().getSelectedReason().setPmSupplierReasons(new ArrayList<PmSupplierReason>());
							}
							this.getManagedBean().getSelectedReason().addPmSupplierReason(tempReason);
							tempReason.setSelected(true);
						}
					}else{
						if(!tempReason.isSelected()){
							for (int i=0; i <this.getManagedBean().getSelectedReason().getPmSupplierReasons().size(); i++){
								if(this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i).getSupplierReasonId()
										.equals(tempReason.getSupplierReasonId())){
									this.getManagedBean().getSelectedReason().getPmSupplierReasons().set(i, tempReason);
								}
							}
							tempReason.setSelected(true);
						}
					}
					
				}
			}else{
				this.getManagedBean().getSelectedReason().setByRegType(false);
			}
			
			//for supplier type
			if(this.getManagedBean().getSuppTypeRestrictions()!=null && this.getManagedBean().getSuppTypeRestrictions().size()>0){
				this.getManagedBean().getSelectedReason().setBySupplierType(true);
				for(PmSupplierReason tempReason:this.getManagedBean().getSuppTypeRestrictions()){
					if(tempReason.isBrandNew()){
						if(!tempReason.isSelected()){
							if(this.getManagedBean().getSelectedReason().getPmSupplierReasons()==null){
								this.getManagedBean().getSelectedReason().setPmSupplierReasons(new ArrayList<PmSupplierReason>());
							}
							this.getManagedBean().getSelectedReason().addPmSupplierReason(tempReason);
							tempReason.setSelected(true);
						}
					}else{
						if(!tempReason.isSelected()){
							for (int i=0; i <this.getManagedBean().getSelectedReason().getPmSupplierReasons().size(); i++){
								if(this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i).getSupplierReasonId()
										.equals(tempReason.getSupplierReasonId())){
									this.getManagedBean().getSelectedReason().getPmSupplierReasons().set(i, tempReason);
								}
							}
							tempReason.setSelected(true);
						}
					}
					
				}
			}else{
				this.getManagedBean().getSelectedReason().setBySupplierType(false);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770001, new String[]{e.getMessage()});

		}
	}
//	/**
//	 * load the Pm Supplier Reason by the restriction type
//	 * @param restrictType
//	 * @param reasonId
//	 * @return List<PmSupplierReason>
//	 */
//	public List<PmSupplierReason> loadRestriction(String restrictType, Long reasonId){
//		log.info("loadRestriction - Start");
//		log.info("restrictType: "+ restrictType);
//		log.info("reasonId: " + reasonId);
//		List <PmSupplierReason> supplierReason = new ArrayList<PmSupplierReason>();
//		try {
//			ReasonReqDTO reqDTO = new ReasonReqDTO();
//			reqDTO.setReasonId(reasonId);
//			reqDTO.setRestrictionType(restrictType);
//			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);
//			ReasonResDTO resDTO = reqSO.retrieveSuppReasonByRestrType(reqDTO);
//			log.info("isSuccessful " + resDTO.isSuccessful());
//			if(resDTO.isSuccessful()){
//				supplierReason = resDTO.getSuppReasonList();
//				log.info("size " + resDTO.getSuppReasonList().size());
//			}
//				
//		} catch (Exception e) {
//			//TODO: throw exception
//			throw new EpException("Failed to get restriction list due to " + e.getMessage());
//
//		}
//		log.info("loadRestriction - End");
//		return supplierReason;
//	}
	
	/**
	 * retrieve reason object by id
	 * @param reasonId
	 * @return PmReason
	 */
	public PmReason retrieveReasonObjById(Long reasonId) {
		log.info("retrieveReasonObjById - Start");
		PmReason pmReason = new PmReason();
		try {
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonId(reasonId);
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);
			ReasonResDTO resDTO = reqSO.retrieveReasonById(reqDTO);
			if(resDTO.isSuccessful()){
				pmReason = resDTO.getPmReason();
			}
				
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770002, new String[]{e.getMessage()});
		}
		log.info("retrieveReasonObjById - End");
		return pmReason;
	}
	
	
	/**
	 * reset search bean fields
	 */
	public void resetReasonSearchBean(){
		log.info("resetReasonSearchBean");
		this.getManagedBean().setReasonSearchBean(new ReasonSearchBean());
	}
	
	/**
	 * save to Rejection Reason Search
	 * @return xhtml
	 */
	public String saveRejectReason(){
		log.info("saveRejectReason - Start");
		boolean isSuccess = false;
		if(this.getManagedBean().isSuppReason()){
			getAllRestrictions();
		}
		
		//unique reason code
		if(!isUniqueReasonCode(this.getManagedBean().getSelectedReason().getReasonCode()
				, this.getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterId()
				, this.getManagedBean().getSelectedReason().isBrandNew()?null:this.getManagedBean().getSelectedReason().getReasonId())){
			throw new EpException(MessageConst.ERROR_770003, new String[]{this.getManagedBean().getSelectedReason().getReasonCode()});

		}
		
		if(this.getManagedBean().getSelectedReason().isBrandNew()){
			isSuccess = createRejectReason(this.getManagedBean().getSelectedReason());
		}else{
			isSuccess = updateRejectReason(this.getManagedBean().getSelectedReason());
		}

		log.info("saveRejectReason - End");
		if(isSuccess){
			return this.redirectToSearchReason();
		}else{
			return null;
		}
		
	
	}
	
	/**
	 * create new reject reason
	 * @param rejectReason
	 * @return PmReason
	 */
	private boolean createRejectReason(PmReason rejectReason){
		log.info("createRejectReason");
		boolean isSuccess = false;
		try{
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setPmReason(rejectReason);
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);	
			ReasonResDTO resDTO = reqSO.createReason(reqDTO);
			if(resDTO.isSuccessful()){
				isSuccess = true;
				log.info("createRejectReason - successful"); //TODO 770015
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_770006, new String[]{rejectReason.getReasonCode()},
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			}
		}catch (Exception e){
			log.info("createRejectReason - fail");
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770004);
		}
		return isSuccess;
	}
	
	/**
	 * update reject reason
	 * @param rejectReason
	 * @return boolean
	 */
	private boolean updateRejectReason(PmReason rejectReason){
		log.info("updateRejectReason");
		boolean isSuccess = false;
		try{
			
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setPmReason(rejectReason);
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);	
			ReasonResDTO resDTO = reqSO.updateReason(reqDTO);
			if(resDTO.isSuccessful()){
				isSuccess = true;
				log.info("updateRejectReason - successful");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_770007, new String[]{rejectReason.getReasonCode()},
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			}
		}catch (Exception e){
			log.info("updateRejectReason - fail"); //TODO
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770004);
		}
		return isSuccess;
	}
	
	/**
	 * check duplicate reason code
	 * @param reasonCode
	 * @param id
	 * @return
	 */
	private boolean isUniqueReasonCode(String reasonCode, Long reasonCategory, Long id){
		log.info("isUniqueReasonCode");
		boolean isUnique = true;
		//Create search bean
		SearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>();
		SearchFilterBean beanReason = new SearchFilterBean();
		
		//add reason code
		beanReason.setSearchField("pmReason.reasonCode");
		beanReason.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
		beanReason.setSearchValue(reasonCode);
		searchBean.getDataFilterBeans().add(beanReason);
		
		//add reason category
		SearchFilterBean beanCategory = new SearchFilterBean();
		beanCategory.setSearchField("pmReason.reasonCategoryCode.parameterId");
		beanCategory.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		beanCategory.setSearchValue(reasonCategory);
		searchBean.getDataFilterBeans().add(beanCategory);
		
		//Filter by search bean
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonDescSearchBean(searchBean);
		reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);	
		ReasonResDTO resDTO = reqSO.retrieveReasonDescBySearchBean(reqDTO);
		if(resDTO.isSuccessful()&& resDTO.getReasonDescList()!=null && !resDTO.getReasonDescList().isEmpty()){ //&& resDTO.getPmReason().isRecordStatus()
			if(id!=null){
				isUnique = id.equals(resDTO.getReasonDescList().get(0).getPmReason().getReasonId());
			}else{
				isUnique = false;
			}
			
		}
		return isUnique;
		
	}
	public void loadReasonCategoryMapper(){
		log.info("loadReasonCategoryMapper");
		reasonCategoryMapper = new HashMap<Long,Object>();
		List<PmParameterDesc> parameterDescs = retrieveParamDescObjs(ParameterTypeConst.REASON_CATEGORY);
		for(PmParameterDesc desc : parameterDescs){
			reasonCategoryMapper.put(desc.getPmParameter().getParameterId(), desc.getPmParameter());
		}
	}

	/**
	 * retrieve reason category
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] reasonCategoryOptions() {
		log.info("reasonCategoryOptions");
		return getDropdownValues(ParameterTypeConst.REASON_CATEGORY);
	}
	
	/**
	 * retrieve application type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] applicationTypeOptions() {
		log.info("applicationTypeOptions");
		return getDropdownValues(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
	}
	
	/**
	 * retrieve company type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] companyTypeOptions() {
		log.info("companyTypeOptions");
		return getDropdownValues(ParameterTypeConst.COMPANY_TYPES);
	}
	
	/**
	 * retrieve supplier type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] supplierTypeOptions() {
		log.info("supplierTypeOptions");
		return getDropdownValues(ParameterTypeConst.SUPPLIER_TYPES);
	}
	
	/**
	 * get record status options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public SelectItem[] getStatusOptions() {
		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
	    return items;
	}
	

	/**
	 * add new supplier reason restriction
	 * @param restrictionType
	 */
	public void addRestriction(String restrictionType){
		log.info("addRestriction");
		log.info("restrictionType = " + restrictionType);
		
		
		PmSupplierReason restriction = new PmSupplierReason();
		restriction.setRecordStatus(true);
		restriction.setRestrType(new PmParameter());
		restriction.setDeleted(false);
		
		if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getAppTypeRestrictions()==null){
				this.getManagedBean().setAppTypeRestrictions(new ArrayList<PmSupplierReason>());
			}
//			validateRestriction(this.getManagedBean().getAppTypeRestrictions(), restrictionType);
			this.getManagedBean().getAppTypeRestrictions().add(restriction);
		}else if(ParameterTypeConst.COMPANY_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getComTypeRestrictions()==null){
				this.getManagedBean().setComTypeRestrictions(new ArrayList<PmSupplierReason>());
			}
//			validateRestriction(this.getManagedBean().getComTypeRestrictions(), restrictionType);
			this.getManagedBean().getComTypeRestrictions().add(restriction);
		}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getSuppTypeRestrictions()==null){
				this.getManagedBean().setSuppTypeRestrictions(new ArrayList<PmSupplierReason>());
			}
//			validateRestriction(this.getManagedBean().getSuppTypeRestrictions(), restrictionType);
			this.getManagedBean().getSuppTypeRestrictions().add(restriction);
		}
	}


	/**
	 * delete supplier reason restriction
	 * @param restrictionType
	 */
	public void deleteRestrictions(String restrictionType){
		log.info("deleteRestrictions");

//		List<PmSupplierReason> tempList = new ArrayList<PmSupplierReason>();
		if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getDeleteAppTypeRestrictions()!=null && !this.getManagedBean().getDeleteAppTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getAppTypeRestrictions()==null){
					this.getManagedBean().setAppTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				for(PmSupplierReason suppReason: this.getManagedBean().getDeleteAppTypeRestrictions()){
					this.getManagedBean().getAppTypeRestrictions().remove(suppReason);
				}
				this.getManagedBean().getDeleteAppTypeRestrictions().clear();
	//			for(PmSupplierReason suppReason: this.getManagedBean().getAppTypeRestrictions()){
	//				if(suppReason.isDeleted()){
	//					tempList.add(suppReason);	
	//				}
	//			}
	//			for(PmSupplierReason suppReason: tempList){
	//				this.getManagedBean().getAppTypeRestrictions().remove(suppReason);
	//			}
			}else{
				throw new EpException(MessageConst.ERROR_770014);
			}
		}else if(ParameterTypeConst.COMPANY_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getDeleteComTypeRestrictions()!=null && !this.getManagedBean().getDeleteComTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getComTypeRestrictions()==null){
					this.getManagedBean().setComTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				
				for(PmSupplierReason suppReason: this.getManagedBean().getDeleteComTypeRestrictions()){
					this.getManagedBean().getComTypeRestrictions().remove(suppReason);
				}
				this.getManagedBean().getComTypeRestrictions().clear();
	//			for(PmSupplierReason suppReason: this.getManagedBean().getComTypeRestrictions()){
	//				if(suppReason.isDeleted()){
	//					tempList.add(suppReason);	
	//				}
	//			}
	//			for(PmSupplierReason suppReason: tempList){
	//				this.getManagedBean().getComTypeRestrictions().remove(suppReason);
	//			}
			}else{
				throw new EpException(MessageConst.ERROR_770014);
			}
		}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictionType)){
			if(this.getManagedBean().getDeleteSuppTypeRestrictions()!=null && !this.getManagedBean().getDeleteSuppTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getSuppTypeRestrictions()==null){
					this.getManagedBean().setSuppTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				
				for(PmSupplierReason suppReason: this.getManagedBean().getDeleteSuppTypeRestrictions()){
					this.getManagedBean().getSuppTypeRestrictions().remove(suppReason);
				}
				this.getManagedBean().getSuppTypeRestrictions().clear();
	//			for(PmSupplierReason suppReason: this.getManagedBean().getSuppTypeRestrictions()){
	//				if(suppReason.isDeleted()){
	//					tempList.add(suppReason);	
	//					this.getManagedBean().getSuppTypeRestrictions().remove(suppReason);
	//				}
	//			}
	//			for(PmSupplierReason suppReason: tempList){
	//				this.getManagedBean().getSuppTypeRestrictions().remove(suppReason);
	//			}
			}else{
				throw new EpException(MessageConst.ERROR_770014);
			}
		}
			
	}
	
	/**
	 * validate selected restriction
	 * @param restrictList
	 * @param restrictType
	 */
	private void validateRestriction(List<PmSupplierReason> restrictList, String restrictType){
		log.info("validateRestriction");
		if(restrictList!=null){
			boolean isSelected = true;
			boolean isUnique = true;
			
			//Validation for mandatory field
			if(restrictList.size()>1 
					&& restrictList.get(
							restrictList.size()-1).getRestrType().getParameterId()==null){
				isSelected = false;
			}
			
			//Validation for unique restriction
			if(isSelected && restrictList.size()>1){	
				List <Long> tempRestrictIdList = new ArrayList<Long>();
				for(PmSupplierReason suppReason :restrictList){
					tempRestrictIdList.add(suppReason.getRestrType().getParameterId());
				}
				Set<Long> tempRestrictIdSet = new HashSet<Long>(tempRestrictIdList);
				if(tempRestrictIdSet.size()!=restrictList.size()){
					isUnique = false;
				}
			}
			
			//throw validation message accordingly
			if(!isSelected){
				if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770008);
				}else if(ParameterTypeConst.COMPANY_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770009);
				}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770010);
				}
			}else if(!isUnique){
				if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770011);
				}else if(ParameterTypeConst.COMPANY_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770012);
				}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770013);
				}
			}	
		}		
	}

	/**
	 * retrieve all languages from pm
	 * @param paramType
	 * @return
	 */
	private List <PmLanguage> retrieveLanguageObjs() {
		log.info("retrieveLanguageObjs");
		List<PmLanguage> languages = new ArrayList<PmLanguage>();
		try {
			ILanguageSO languageSO = (ILanguageSO) BusinessDelegate.getInstance().getServiceObject("pm", ILanguageSO.class);
			LanguageResDTO languagecResDTO = languageSO.retrieveLanguages();
			languages = languagecResDTO.getLanguageList();
				
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(),
							e.getMessage()));
		}
		return languages;
	}
	
	/**
	 * retrieve parameter desc obj list by param type
	 * @param paramType
	 * @return
	 */
	private List <PmParameterDesc> retrieveParamDescObjs(String paramType) {
		log.info("retrieveParamDescObjs");
		List<PmParameterDesc> parameterDescs = new ArrayList<PmParameterDesc>();
		try {
			parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(paramType, null);
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(),
							e.getMessage()));
		}
		return parameterDescs;
	}
	

	/**
	 * get reason category object by selected id
	 * @param isSearchBean
	 * @param id
	 */
	public void reasonCategoryChange(boolean isSearchBean, Long id){
		log.info("reasonCategoryChange");
		if(id!=null && id>0L){
			PmParameter paramObj = (PmParameter) reasonCategoryMapper.get(id);
			System.out.println(paramObj.getParameterCode());
			if(ParameterConst.REASON_CATEGORY_SUPPLIER.equals(paramObj.getParameterCode())){
				if(isSearchBean){
					this.getManagedBean().setSearchSuppReason(true);
				}else{
					this.getManagedBean().setSuppReason(true);
				}
			}else{
				if(isSearchBean){
					this.getManagedBean().setSearchSuppReason(false);
				}else{
					this.getManagedBean().setSuppReason(false);
				}
			}
		}else{
			if(isSearchBean){
				this.getManagedBean().setSearchSuppReason(false);
			}else{
				this.getManagedBean().setSuppReason(false);
			}
		}
	}

	/**
	 * Below codes are for reason search
	 */
	
	/**
	 * get search bean 
	 * @return ISearchBean<PmReason>
	 */
	private ISearchBean<PmReasonDesc> getSearchBeanData(ISearchBean<PmReasonDesc> searchBean){
		log.info("getSearchBeanData - Start");
		ReasonSearchBean rSearchBean = this.getManagedBean().getReasonSearchBean();
		if(rSearchBean.getReasonCategoryParam()!=null && rSearchBean.getReasonCategoryParam().getParameterId()!=null 
				&& rSearchBean.getReasonCategoryParam().getParameterId()>0L ){
			log.info("+ reasonCategory "  +rSearchBean.getReasonCategoryParam().getParameterId());
			SearchFilterBean cateBean = new SearchFilterBean();
			cateBean.setSearchField("pmReason.reasonCategoryCode.parameterId"); //reasonCategory.parameterId
			cateBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			cateBean.setSearchValue(rSearchBean.getReasonCategoryParam().getParameterId());
			searchBean.getDataFilterBeans().add(cateBean);
			
			//for supplier reason category
			PmParameter paramObj = (PmParameter) reasonCategoryMapper.get(rSearchBean.getReasonCategoryParam().getParameterId());
			
			if(ParameterConst.REASON_CATEGORY_SUPPLIER.equals(paramObj.getParameterCode())){
				rSearchBean.setRestrType(null);
				log.info("+ reasonCategory "  +paramObj.getParameterCode());
				List <Long> restrTypes = new ArrayList<Long>();
				
				if(rSearchBean.getApplicationType()!=null && rSearchBean.getApplicationType()>0L){
					log.info("+ applicationType "  +rSearchBean.getApplicationType());
//					SearchFilterBean bean = new SearchFilterBean();
//					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//					bean.setSearchField("pmReason.isByAppType");
//					bean.setSearchValue(true);
//					searchBean.getDataFilterBeans().add(bean);
//					rSearchBean.setRestrType(rSearchBean.getApplicationType());
					restrTypes.add(rSearchBean.getApplicationType());
				}
				if(rSearchBean.getCompanyType()!=null && rSearchBean.getCompanyType()>0L){
					log.info("+ companyType "  +rSearchBean.getCompanyType());
//					SearchFilterBean bean = new SearchFilterBean();
//					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//					bean.setSearchField("pmReason.isByRegType");
//					bean.setSearchValue(true);
//					searchBean.getDataFilterBeans().add(bean);
//					rSearchBean.setRestrType(rSearchBean.getCompanyType());
					restrTypes.add(rSearchBean.getCompanyType());
				}
				if(rSearchBean.getSupplierType()!=null && rSearchBean.getSupplierType()>0L){
					log.info("+ supplierType "  +rSearchBean.getSupplierType());
//					SearchFilterBean bean = new SearchFilterBean();
//					bean.setSearchField("pmReason.pmSupplierReasons.supplierType.parameterId");
//					bean.setSearchField("pmReason.isBySupplierType");
//					bean.setSearchValue(true);
//					searchBean.getDataFilterBeans().add(bean);
//					rSearchBean.setRestrType(rSearchBean.getSupplierType());
					restrTypes.add(rSearchBean.getSupplierType());
				}
				
				if(restrTypes!=null && !restrTypes.isEmpty()){
					log.info("+ getRestrType "  + rSearchBean.getRestrType());
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("pmReason.pmSupplierReasons.restrType.parameterId");
//					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
					bean.setSearchValue(restrTypes);
					searchBean.getDataFilterBeans().add(bean);
				}
			}
		}
		
		if(rSearchBean.getReasonCode()!=null && rSearchBean.getReasonCode().trim().length()>0){
			log.info("+ reasonCode "  +rSearchBean.getReasonCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("pmReason.reasonCode");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(rSearchBean.getReasonCode());
			searchBean.getDataFilterBeans().add(bean);
		}
		if(rSearchBean.getReasonDesc()!=null && rSearchBean.getReasonDesc().trim().length()>0){
			log.info("+ reasonDesc "  +rSearchBean.getReasonDesc());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("reasonDesc");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(rSearchBean.getReasonDesc());
			searchBean.getDataFilterBeans().add(bean);
		}

		if(rSearchBean.getStatus()!=null){
			log.info("+ status "  +rSearchBean.getStatus());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("pmReason.recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(rSearchBean.getStatus());
			searchBean.getDataFilterBeans().add(bean);
		}
		
		log.info("getSearchBeanData - End");
		return searchBean;
	}
	
	/**
	 * search reason
	 */
	public void searchReason(){
		log.info("searchReason - Start");
		pmReasonDescDataMode.reloadData();
		log.info("searchReason - End");
		
	}
	
	/**
	 * @return the pmReasonDescDataMode
	 */
	public IPagingDataModel<PmReasonDesc> getPmReasonDescDataModel() {
		return pmReasonDescDataMode;
	}

	/**
	 * @param pmReasonDescDataMode the pmReasonDescDataMode to set
	 */
	public void setPmReasonDataModel(IPagingDataModel<PmReasonDesc> pmReasonDescDataMode) {
		this.pmReasonDescDataMode = pmReasonDescDataMode;
	}
	
	/**
	 * paginate for reason list
	 * @author RainPoh
	 *
	 */
	private class PmReasonPagingDataModel extends PagingDataModel<PmReasonDesc> {
		private static final long serialVersionUID = 1L;

		public PmReasonPagingDataModel() {
			super("dtRejectReasonResult");
		}
		/**
		 * @author rainpoh
		 * @since 09.07.2014
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmReasonDesc> pageChangeListener(ISearchBean<PmReasonDesc> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {	
				ISearchBean<PmReasonDesc> searchBeanData = getSearchBeanData(searchBean);
				ReasonReqDTO reqDTO = new ReasonReqDTO();
				FacesScopes fs = new FacesScopes();
				reqDTO.setLanguage(fs.getUserLocale().getLanguage());
				reqDTO.setReasonDescSearchBean(searchBeanData);
				IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);	
				ReasonResDTO resDTO = reqSO.retrieveReasonDescBySearchBean(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getReasonDescResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}
	}

	
}
