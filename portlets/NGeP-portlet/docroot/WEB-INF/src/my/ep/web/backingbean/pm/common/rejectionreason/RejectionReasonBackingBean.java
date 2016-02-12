/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.rejectionreason;

import java.util.ArrayList;
import java.util.Arrays;
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
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmPIDReason;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ReasonSearchBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
	//Mapper for DDL
	private static Map<Long, Object> souringMethodParameterIdMapper;
	//Mapper for DDL
	private static Map<Long, Object> conditionFieldMapper;
	//Mapper for DDL
	private static Map<String, Object> souringMethodMapper;
	//List for Paging
	private IPagingDataModel<PmReasonDesc> pmReasonDescDataModel = new PmReasonPagingDataModel();
	
	//Default value
	public static final boolean SEARCH_DEFAULT = false;
	private ISearchBean<PmParameterDesc> docTypeSearchBean;
	private ISearchBean<PmParameterDesc> catCirSearchBean = new SearchBean<PmParameterDesc>();
	private ISearchBean<PmParameterDesc> reasonApplicablitySearchBean;
	//Document Restriction Type
	public static final int RESTR_APP = 1;
	public static final int RESTR_COMP = 2;
	public static final int RESTR_SUPP = 3;


	public void preLoading(){
		log.info("preLoading");
		//load critical mappers
		this.loadReasonCategoryMapper();
		this.loadSourcingMethodMapper();
		this.loadConditionFieldMapper();
		this.getManagedBean().setSelectedTabIndex(0);
		this.resetReasonSearchBean();
		this.reasonCategoryChange(true, null);
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
	public void deleteReasonApplicability(int rowIndex){
		log.info("...............deleteReasonApplicability................");

			this.getManagedBean().getSelectedReason().getPmPIDReasonList()
					.remove(rowIndex);
	}
	public void reasonApplicableChange(PmPIDReason pidReason){
		
		List<PmPIDReason> temp = new ArrayList<PmPIDReason>();
		temp.addAll(this.getManagedBean().getSelectedReason().getPmPIDReasonList());
		temp.remove(pidReason);
		for(PmPIDReason pReason : temp){
			if(pReason.getDocType()!= null && pReason.getDocType().equals(pidReason.getDocType())){
				throw new EpException(MessageConst.ERROR_770301, new String[]{ResourceBundleUtils.getMessage(
						"lbl.pm.reason.applicability", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())});
			}
		}
	}
	public void addMoreReasonApplicablity() {
		log.info(".................addMoreReasonApplicablity...................");
		
		PmPIDReason pidReason = new PmPIDReason();
		pidReason.setPmReason(this.getManagedBean().getSelectedReason());
         if(this.getManagedBean().getSelectedReason()
				.getPmPIDReasonList() == null){
        	 
        	 this.getManagedBean().getSelectedReason().setPmPIDReasonList(new ArrayList<PmPIDReason>());
         }
		int index = this.getManagedBean().getSelectedReason()
				.getPmPIDReasonList().size();
		log.info(".......index.......="+index);
		this.getManagedBean().getSelectedReason().getPmPIDReasonList()
				.add(index, pidReason);
		log.info("..this.getManagedBean().getSelectedReason().getPmPIDReasonList().size()="+this.getManagedBean().getSelectedReason().getPmPIDReasonList().size());
		log.info("..end..");
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
	public String redirectToMaintainReason(Long id)throws Exception{
		log.info("redirectToMaintainReason - Start");
//		try{
	
			this.loadReasonCategoryMapper();
			this.loadSourcingMethodMapper();
			this.loadConditionFieldMapper();
			this.getManagedBean().setSuppAppCheck(Boolean.FALSE);
			this.getManagedBean().setSuppComCheck(Boolean.FALSE);
			this.getManagedBean().setSuppTypeCheck(Boolean.FALSE);
	
			if(id!=null && id>0){
				loadRejectReason(id);
			}else{
				initialRejectReason();
			}
//		}catch (Exception e){
//			e.printStackTrace();
//			
//		}
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
		pmReason.setConditionField(new PmParameter());
		pmReason.setSourceMethod(new PmParameter());
		
		//set languages into description
		List<PmLanguage> languages = PmCommonUtil.retrieveLanguageObjs();
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
		this.getManagedBean().setDeleteAppTypeRestrictions(null);
		this.getManagedBean().setDeleteComTypeRestrictions(null);
		this.getManagedBean().setDeleteSuppTypeRestrictions(null);
		this.getManagedBean().setDeleteRestrictions(null);
		this.reasonCategoryChange(false, null);
		this.getManagedBean().setSelectedTabIndex(0);
		log.info("initialRejectReason - End");
	}
	
	/**
	 * load rejection reason object by id
	 */
	public void loadRejectReason(Long id){
		log.info("loadRejectReason - Start");
		try{
			
//			initialRejectReason();
			PmReason pmReason = retrieveReasonObjById(id);
			//set supplier type flag
			this.getManagedBean().setSuppReason( 
					ParameterConst.REASON_CATEGORY_SUPPLIER.equals(pmReason.getReasonCategoryCode().getParameterCode())
					? true : false);
			//add all languages to description list
			List <PmLanguage> languagues = PmCommonUtil.retrieveLanguageObjs();
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
			this.getManagedBean().getPmPidListOrigin().clear();
			this.getManagedBean().setSelectedReason(pmReason);
			this.getManagedBean().getPmPidListOrigin().addAll(pmReason.getPmPIDReasonList());//get backup to validation
			log.info("./.......pmReason.getPmPIDReasonList().size()="+pmReason.getPmPIDReasonList().size());
			log.info("./.......this.getPmPidListOrigin().size()="+this.getManagedBean().getPmPidListOrigin().size());
			loadRestriction(pmReason);
			this.getManagedBean().setDeleteAppTypeRestrictions(null);
			this.getManagedBean().setDeleteComTypeRestrictions(null);
			this.getManagedBean().setDeleteSuppTypeRestrictions(null);
			this.getManagedBean().setDeleteRestrictions(null);
//			this.getManagedBean().setAppTypeRestrictions(loadRestriction(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES, id));
//			this.getManagedBean().setComTypeRestrictions(loadRestriction(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE, id));
//			this.getManagedBean().setSuppTypeRestrictions(loadRestriction(ParameterTypeConst.SUPPLIER_TYPES, id));
		}catch (Exception e){
			e.printStackTrace();
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
				if(pmSuppReason.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
					if(pmSuppReason.getRestrType()!=null){
						if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
							appTypeRestrictions.add(pmSuppReason);
						}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
							comTypeRestrictions.add(pmSuppReason);
						}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(pmSuppReason.getRestrType().getPmParameterType().getParameterType())){
							suppTypeRestrictions.add(pmSuppReason);
						}
					}
				}
			}
			if(!appTypeRestrictions.isEmpty()){
				this.getManagedBean().setSuppAppCheck(Boolean.TRUE);
			}
			if(!comTypeRestrictions.isEmpty()){
				this.getManagedBean().setSuppComCheck(Boolean.TRUE);
			}
			if(!suppTypeRestrictions.isEmpty()){
				this.getManagedBean().setSuppTypeCheck(Boolean.TRUE);
			}
			
			this.getManagedBean().setAppTypeRestrictions(appTypeRestrictions);
			this.getManagedBean().setComTypeRestrictions(comTypeRestrictions);
			this.getManagedBean().setSuppTypeRestrictions(suppTypeRestrictions);
			
			defaultTab();
		}
		log.info("loadRestriction - End");
	}
	
	/**
	 * set default tab
	 * @param pmDocument
	 */
	public void defaultTab(){
		log.info("defaultTab");
		if(getManagedBean().isSuppAppCheck()){
			this.getManagedBean().setSelectedTabIndex(RESTR_APP-1);
		}else if(getManagedBean().isSuppComCheck()){
			this.getManagedBean().setSelectedTabIndex(RESTR_COMP-1);
		}else if(getManagedBean().isSuppTypeCheck()){
			this.getManagedBean().setSelectedTabIndex(RESTR_SUPP-1);
		}
	}
	
	/**
	 * validate unique restr type
	 * @param pmParam
	 * @param id
	 * @param restrictType
	 */
	public boolean validateUniqueRestrType(String restrictType){
		log.info("validateUniqueRestrType");
		boolean isUnique = true;
		String errorMesg = null;
		List <PmSupplierReason> pmSupplierReasons = null; 

		if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictType)){
			pmSupplierReasons = this.getManagedBean().getAppTypeRestrictions();
			errorMesg = MessageConst.ERROR_770011;
		}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(restrictType)){
			pmSupplierReasons = this.getManagedBean().getComTypeRestrictions();
			errorMesg =MessageConst.ERROR_770012;
		}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictType)){
			pmSupplierReasons = this.getManagedBean().getSuppTypeRestrictions();
			errorMesg = MessageConst.ERROR_770013;
		}
		
		//Validation for unique restriction
		if(pmSupplierReasons!=null && pmSupplierReasons.size()>1){	
			List <Long> tempRestrictIdList = new ArrayList<Long>();
			for(PmSupplierReason suppReason :pmSupplierReasons){
				tempRestrictIdList.add(suppReason.getRestrType().getParameterId());
			}
			Set<Long> tempRestrictIdSet = new HashSet<Long>(tempRestrictIdList);
			if(tempRestrictIdSet.size()!=pmSupplierReasons.size()){
				isUnique = false;
			}
			
			if(!isUnique){
				throw new EpException(errorMesg);
			}	
		}
		return isUnique;
	}
	/**
	 * validate all restrictions
	 * @param pmReason
	 */
	private void validateRestrictions(PmReason pmReason){
		log.info("validateRestrictions");
		//validate required field
		if(getManagedBean().isSuppAppCheck()){
			validateRestriction(this.getManagedBean().getAppTypeRestrictions(), ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
		}
		
		if(getManagedBean().isSuppComCheck()){
			validateRestriction(this.getManagedBean().getComTypeRestrictions(), ParameterTypeConst.FINANCIAL_ORG_REG_TYPE);
		}
		
		if(getManagedBean().isSuppTypeCheck()){
			validateRestriction(this.getManagedBean().getSuppTypeRestrictions(), ParameterTypeConst.SUPPLIER_TYPES);
		}
		
		
		
		//validate unique field
		validateUniqueRestrType(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES);
		validateUniqueRestrType(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE);
		validateUniqueRestrType(ParameterTypeConst.SUPPLIER_TYPES);
	}
	/**
	 * collect all 3 restriction type to one list
	 */
	private void getAllRestrictions(){
		log.info("getAllRestrictions");
		try{
			PmSupplierReason supplierReason = null;
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
								supplierReason = this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i);
								if(supplierReason.getSupplierReasonId() != null && supplierReason.getSupplierReasonId()
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
								supplierReason = this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i);
								if(supplierReason.getSupplierReasonId() != null && supplierReason.getSupplierReasonId()
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
								supplierReason = this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i);
								if(supplierReason.getSupplierReasonId() != null && supplierReason.getSupplierReasonId()
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
			
			//for deleted Restrictions
			if(this.getManagedBean().getDeleteRestrictions()!=null && this.getManagedBean().getDeleteRestrictions().size()>0){
				this.getManagedBean().getSelectedReason().setBySupplierType(true);
				for(PmSupplierReason tempReason:this.getManagedBean().getDeleteRestrictions()){
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
								supplierReason = this.getManagedBean().getSelectedReason().getPmSupplierReasons().get(i);
								if(supplierReason.getSupplierReasonId() != null && supplierReason.getSupplierReasonId()
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
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
			ReasonResDTO resDTO = reqSO.retrieveReasonDtlById(reqDTO);
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
		this.getManagedBean().setSearchSuppReason(false);
		pmReasonDescDataModel = new PmReasonPagingDataModel();
	}
	
	/**
	 * save to Rejection Reason
	 * @return xhtml
	 */
	public String saveRejectReason(){
		log.info("saveRejectReason - Start");
		String returnPage = null;
		//unique reason code
		if(this.getManagedBean().getSelectedReason().getPmPIDReasonList() == null || this.getManagedBean().getSelectedReason().getPmPIDReasonList().size() == 0){
			throw new EpException(MessageConst.ERROR_770300, new String[]{});
		}
		if(this.getManagedBean().getSelectedReason().isBrandNew()){
			generateReasonCode();
		}
		
//		if(!isUniqueReasonCode(this.getManagedBean().getSelectedReason().getReasonCode()
//				, this.getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterId()
//				, this.getManagedBean().getSelectedReason().isBrandNew()?null:this.getManagedBean().getSelectedReason().getReasonId())){
//			throw new EpException(MessageConst.ERROR_770003, new String[]{this.getManagedBean().getSelectedReason().getReasonCode()});
//
//		}
	
		if(this.getManagedBean().isSuppReason()){
			validateRestrictions(this.getManagedBean().getSelectedReason());
			getAllRestrictions();
		}
		
		
		
		if(this.getManagedBean().getSelectedReason().isBrandNew()){
			returnPage = createRejectReason(this.getManagedBean().getSelectedReason());
		}else{
			returnPage = updateRejectReason(this.getManagedBean().getSelectedReason());
		}
		
		log.info("saveRejectReason - End");
		return returnPage;
//		if(isSuccess){
//			return this.redirectToSearchReason();
//		}else{
//			return null;
//		}
		
	
	}
	
	private void generateReasonCode() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
		IParameterSO parameterSO  = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		ParameterReqDTO paramReq = new ParameterReqDTO();
		String reasonCode = null;
		paramReq.setParameterId(getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterId());
		ParameterResDTO paramRes = parameterSO.retrieveParameterById(paramReq);	
		
//		if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_PLAN_CANCELATION)){
//			reasonCode = ProfileManagementConst.PROCUREMENT_PLAN_CANCELATION;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_PLAN_REJECTION)){
//			reasonCode = ProfileManagementConst.PROCUREMENT_PLAN_REJECTION;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.REASON_CATEGORY_SUPPLIER)){
//			reasonCode = ProfileManagementConst.REASON_CATEGORY_SUPPLIER;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.STOP_INSTRUCTION)){
//			reasonCode = ProfileManagementConst.STOP_INSTRUCTION;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TERMINATION)){
//			reasonCode = ProfileManagementConst.CONTRACT_TERMINATION;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.FWD_HIGHER_AUTH)){
//			reasonCode = ProfileManagementConst.FWD_HIGHER_AUTH;
//		}else if(paramRes.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.PURCHASE_IN_DEFAULT)){
//			reasonCode = ProfileManagementConst.PURCHASE_IN_DEFAULT;
//		}
//		reqDTO.setReasonCode(reasonCode);
		reqDTO.setReasonCode(paramRes.getPmParameter().getParameterCode());
		
		ReasonResDTO resDTO = reasonSO.generateReasonCode(reqDTO);
		getManagedBean().getSelectedReason().setReasonCode(resDTO.getReasonCode());
	}


	/**
	 * create new reject reason
	 * @param rejectReason
	 */
	private String createRejectReason(PmReason rejectReason){
		log.info("createRejectReason");
		try{
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setPmReason(rejectReason);
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);	
			ReasonResDTO resDTO = reqSO.createReason(reqDTO);
			if(resDTO.isSuccessful()){
//				loadRejectReason(resDTO.getPmReason().getReasonId());
				log.info("createRejectReason - successful"); //TODO 770015
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_770006, new String[]{rejectReason.getReasonCode()},
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				
				return redirectToSearchReason();
			}
		}catch (Exception e){
			log.info("createRejectReason - fail");
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770004);
		}
		return null;
	}
	
	/**
	 * update reject reason
	 * @param rejectReason
	 */
	private String updateRejectReason(PmReason rejectReason){
		log.info("updateRejectReason");
		try{
			List<PmPIDReason> toDeletePReasons  = new ArrayList<PmPIDReason>();
			List<PmPIDReason> toReplacePReasons  = new ArrayList<PmPIDReason>();
			for(PmPIDReason pidReason: rejectReason.getPmPIDReasonList()){
				log.info(".............pidReason="+pidReason.getDocType());
				if(pidReason.isBrandNew()){
					log.info(".......pidReason.isBrandNew()"+pidReason.isBrandNew());
					log.info(".......this.getPmPidListOrigin().size()"+this.getManagedBean().getPmPidListOrigin().size());
					for(PmPIDReason pReason: this.getManagedBean().getPmPidListOrigin()){
						log.info(".......pReason.getDocType="+pReason.getDocType());
					    if(pidReason.getDocType().equals(pReason.getDocType())){
					    	log.info("equal");
					    	//remove the brandnew added PIDReason, And replace it with the origin obj had been red from DB
					    	toDeletePReasons.add(pidReason);
					    	toReplacePReasons.add(pReason);
//					    	break; // break first loop
					    }
					}
				}
			}
			for(PmPIDReason p :toDeletePReasons){
				log.info(".toDeletePReasons.......p.getDocType()="+p.getDocType());
				log.info(".toDeletePReasons.......p.getDocType()="+p.getPidReasonId());
			}
			for(PmPIDReason m :toReplacePReasons){
				log.info(".toReplacePReasons.......p.getDocType()="+m.getDocType());
				log.info(".toReplacePReasons.......p.getDocType()="+m.getPidReasonId());
			}
			rejectReason.getPmPIDReasonList().removeAll(toDeletePReasons);
			rejectReason.getPmPIDReasonList().addAll(toReplacePReasons);
			
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setPmReason(rejectReason);
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);	
			ReasonResDTO resDTO = reqSO.updateReason(reqDTO);
			if(resDTO.isSuccessful()){
				loadRejectReason(resDTO.getPmReason().getReasonId());
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
		return null;
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
		log.info("reasonCode: " + reasonCode);
		beanReason.setSearchField("pmReason.reasonCode");
		beanReason.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		beanReason.setSearchValue(reasonCode);
		beanReason.setCaseInsensitive(true);
		searchBean.getDataFilterBeans().add(beanReason);
		
		//add reason category
		log.info("reasonCategoryCode: " + reasonCategory);
		SearchFilterBean beanCategory = new SearchFilterBean();
		beanCategory.setSearchField("pmReason.reasonCategoryCode.parameterId");
		beanCategory.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		beanCategory.setSearchValue(reasonCategory);
		searchBean.getDataFilterBeans().add(beanCategory);
		
		if(id!=null){
			//excluded the current id
			log.info("reasonId: " + id);
			SearchFilterBean idReason = new SearchFilterBean();
			idReason.setSearchField("pmReason.reasonId");
			idReason.setSearchOperator(SearchOperator.OPERATOR_NOT_EQUALS.value());
			idReason.setSearchValue(id);
			searchBean.getDataFilterBeans().add(idReason);
		}
		
				
		
		//Filter by search bean
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonDescSearchBean(searchBean);
		reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);	
		ReasonResDTO resDTO = reqSO.retrieveReasonDescBySearchBean(reqDTO);
		if(resDTO.isSuccessful()&& resDTO.getReasonDescList()!=null && !resDTO.getReasonDescList().isEmpty()){ //&& resDTO.getPmReason().isRecordStatus()
			isUnique = false;
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
	public void loadConditionFieldMapper(){
		log.info("loadConditionFieldMapper");
		conditionFieldMapper = new HashMap<Long,Object>();
		List<PmParameterDesc> parameterDescs = retrieveParamDescObjs(ParameterTypeConst.CONTRACT_CIRCULAR_CONDITION);
		for(PmParameterDesc desc : parameterDescs){
			conditionFieldMapper.put(desc.getPmParameter().getParameterId(), desc.getPmParameter());
		}
	}
	
	public void loadSourcingMethodMapper(){
		log.info("loadSourcingMethodMapper");
		souringMethodMapper = new HashMap<String,Object>();
		souringMethodParameterIdMapper = new HashMap<Long,Object>();
		List<PmParameterDesc> parameterDescs = retrieveParamDescObjs(ParameterTypeConst.SOURCING_METHOD);
		for(PmParameterDesc desc : parameterDescs){
			souringMethodMapper.put(desc.getPmParameter().getParameterCode(), desc.getPmParameter());
			souringMethodParameterIdMapper.put(desc.getPmParameter().getParameterId(), desc.getPmParameter());
		}
	}

	/**
	 * retrieve reason category
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] reasonCategoryOptions() {
		log.info("reasonCategoryOptions");
		return getDropdownValuesBySearchBean(ParameterTypeConst.REASON_CATEGORY,getDocTypeSearchBean());
	}
	
	/**
	 * retrieve contract/circular condition
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] contractCircularConditionOptions() {
		log.info("contractCircularConditionOptions");
		return getDropdownValuesBySearchBean(ParameterTypeConst.CONTRACT_CIRCULAR_CONDITION,getCatCirSearchBean());
	}
	
	/**
	 * retrieve sourcing method
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] sourcingMethodOptions() {
		log.info("sourcingMethodOptions");
		return getDropdownValuesBySearchBean(ParameterTypeConst.SOURCING_METHOD,getCatCirSearchBean());
	}
	/**
	 * retrieve reasonApplicability doc type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] reasonApplicablityOptions(){
		log.info("reasonApplicablityOptions");
		return getDropdownCodeValuesBySearchBean(ParameterTypeConst.DOCUMENT_TYPES, getReasonApplicablitySearchBean());
	}
	public ISearchBean<PmParameterDesc> getReasonApplicablitySearchBean(){
		reasonApplicablitySearchBean = new SearchBean<PmParameterDesc>();

	List<String> parameterCodes = new ArrayList<String>();
	parameterCodes.add("MC");
	parameterCodes.add("PQ");
	parameterCodes.add("CQ");
	reasonApplicablitySearchBean.getDataFilterBeans().add(
			new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_IN.value(),
					parameterCodes));
	
	return reasonApplicablitySearchBean;
	}
	
	/**
	 * retrieve application type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] applicationTypeOptions() {
		log.info("applicationTypeOptions");
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String recommend = ParameterConst.SUPPLIER_APPL_CODE_O;

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), recommend));

		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES, searchBean);
		return items;
	}
	
	/**
	 * retrieve company type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] companyTypeOptions() {
		log.info("companyTypeOptions");
		return getDropdownValues(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE);
	}
	
	/**
	 * retrieve supplier type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] supplierTypeOptions() {
		log.info("supplierTypeOptions");
		List<PmParameterDesc> paramDescs = PmCommonUtil.retrieveFilteredParamDesc(ParameterTypeConst.SUPPLIER_TYPES, Arrays.asList(ProfileManagementConst.SUPP_TYPE_EXCLUDE), null);
		SelectItem[] items =  new SelectItem[paramDescs.size()+1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for(PmParameterDesc paramDesc:paramDescs){
			items[i++] = new SelectItem(paramDesc.getPmParameter()
					.getParameterId(), paramDesc.getCodeName());
		}
		return items;
	}
	
	/**
	 * get record status options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public SelectItem[] getStatusOptions() {
	    return PmCommonUtil.getRecordStatusOptions();
	}
	

	/**
	 * add new supplier reason restriction
	 * @param restrictionType
	 */
	public void addRestriction(String restrictionType){
		log.info("addRestriction");
		log.info("restrictionType = " + restrictionType);
		if(validateUniqueRestrType(restrictionType)){
			PmSupplierReason restriction = new PmSupplierReason();
			restriction.setRecordStatus(true);
			restriction.setRestrType(new PmParameter());
			restriction.setDeleted(false);
			
			if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictionType)){
				if(this.getManagedBean().getAppTypeRestrictions()==null){
					this.getManagedBean().setAppTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				this.getManagedBean().getAppTypeRestrictions().add(restriction);
			}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(restrictionType)){
				if(this.getManagedBean().getComTypeRestrictions()==null){
					this.getManagedBean().setComTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				this.getManagedBean().getComTypeRestrictions().add(restriction);
			}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictionType)){
				if(this.getManagedBean().getSuppTypeRestrictions()==null){
					this.getManagedBean().setSuppTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				this.getManagedBean().getSuppTypeRestrictions().add(restriction);
			}
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
					if(!suppReason.isBrandNew()){
						suppReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					}
					
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
		}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(restrictionType)){
			if(this.getManagedBean().getDeleteComTypeRestrictions()!=null && !this.getManagedBean().getDeleteComTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getComTypeRestrictions()==null){
					this.getManagedBean().setComTypeRestrictions(new ArrayList<PmSupplierReason>());
				}
				
				for(PmSupplierReason suppReason: this.getManagedBean().getDeleteComTypeRestrictions()){
					if(!suppReason.isBrandNew()){
						suppReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					}
					
					this.getManagedBean().getComTypeRestrictions().remove(suppReason);
				}
				this.getManagedBean().getDeleteComTypeRestrictions().clear();
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
					if(!suppReason.isBrandNew()){
						suppReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					}
					this.getManagedBean().getSuppTypeRestrictions().remove(suppReason);
				}
				this.getManagedBean().getDeleteSuppTypeRestrictions().clear();
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
//		if(restrictList!=null){
			boolean isSelected = true;			
			boolean isNotEmpty = true;
			
			//Validation for mandatory field
			if(restrictList==null || (restrictList!=null && restrictList.isEmpty())){
				isNotEmpty = false;
			}else{
				for(PmSupplierReason pmSupplierReason: restrictList){
					if(pmSupplierReason.getRestrType().getParameterId()==null){
						isSelected = false;
					}
				}
			}
			log.info(".........isNotEmpty="+isNotEmpty);
			//throw validation message accordingly
			if(!isSelected){
				if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770008);
				}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770009);
				}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770010);
				}
			}
			else if(!isNotEmpty){
				if(ParameterTypeConst.SUPPLIER_APPLICATION_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770072);
				}else if(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770073);
				}else if(ParameterTypeConst.SUPPLIER_TYPES.equals(restrictType)){
					throw new EpException(MessageConst.ERROR_770074);
				}
			}
//		}		
	}

	/**

	
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
	
	public void purchaseFromContractChange(){
		if(this.getManagedBean().getSelectedReason().isContractPurchase()){
			PmParameter paramObj = (PmParameter) souringMethodMapper.get(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
			this.getManagedBean().getSelectedReason().getSourceMethod().setParameterId(paramObj.getParameterId());
		}
	}
	/**
	 * get reason category object by selected id
	 * @param isSearchBean
	 * @param id
	 */
	public void reasonCategoryChange(boolean isSearchBean, Long id){
		log.info("reasonCategoryChange");
		getManagedBean().getReasonSearchBean().setApplicationType(null);
		getManagedBean().getReasonSearchBean().setSupplierType(null);
		getManagedBean().getReasonSearchBean().setCompanyType(null);
		if(id!=null && id>0L){
			PmParameter paramObj = (PmParameter) reasonCategoryMapper.get(id);
			this.getManagedBean().getSelectedReason().setReasonCategoryCode(paramObj);
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
	 * get conditionField object by selected parameter id
	 * @param isSearchBean
	 * @param id
	 */
	public void conditionFieldChange(boolean isSearchBean, Long id){
		log.info("conditionFieldChange");

		if(id!=null && id>0L){
			PmParameter paramObj = (PmParameter) conditionFieldMapper.get(id);
			this.getManagedBean().getSelectedReason().setConditionField(paramObj);
			System.out.println(paramObj.getParameterCode());
			
		}
	}
	/**
	 * get sourceMethod object by selected parameter id
	 * @param isSearchBean
	 * @param id
	 */
	public void sourceMethodChange(boolean isSearchBean, Long id){
		log.info("conditionFieldChange");

		if(id!=null && id>0L){
			PmParameter paramObj = (PmParameter) souringMethodParameterIdMapper.get(id);
			this.getManagedBean().getSelectedReason().setSourceMethod(paramObj);
			System.out.println(paramObj.getParameterCode());
			
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
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		ReasonSearchBean rSearchBean = this.getManagedBean().getReasonSearchBean();
		
		if(rSearchBean.getReasonCategoryParam()!=null && rSearchBean.getReasonCategoryParam().getParameterId()!=null 
				&& rSearchBean.getReasonCategoryParam().getParameterId()>0L ){
			log.info("+ reasonCategory "  +rSearchBean.getReasonCategoryParam().getParameterId());
			SearchFilterBean cateBean = new SearchFilterBean();
			cateBean.setSearchField("pmReason.reasonCategoryCode.parameterId"); //reasonCategory.parameterId
			cateBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			cateBean.setSearchValue(rSearchBean.getReasonCategoryParam().getParameterId());
			searchBean.getDataFilterBeans().add(cateBean);
			isEmpty = false;
			
			if(rSearchBean.getReasonCode()!=null && rSearchBean.getReasonCode().trim().length()>0){
				log.info("+ reasonCode "  +rSearchBean.getReasonCode());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("pmReason.reasonCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(rSearchBean.getReasonCode());
				bean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
			if(rSearchBean.getReasonDesc()!=null && rSearchBean.getReasonDesc().trim().length()>0){
				log.info("+ reasonDesc "  +rSearchBean.getReasonDesc());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("reasonDesc");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(rSearchBean.getReasonDesc());
				bean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
			
			if(rSearchBean.getStatus()!=null){
				log.info("+ status "  +rSearchBean.getStatus());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("pmReason.recordStatusFlg");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(rSearchBean.getStatus());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
		}

		//base on the flag, default list is either select all or empty
		if(isEmpty && !isSearchable){
			log.info("+ status "  + null);
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(null);
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
		if(this.getManagedBean().getReasonSearchBean().getReasonCategoryParam()==null 
				|| this.getManagedBean().getReasonSearchBean().getReasonCategoryParam().getParameterId()==null){
			throw new EpException(MessageConst.ERROR_770129);
		}
		
		pmReasonDescDataModel.reloadData();
		log.info("searchReason - End");
		
	}
	
	/**
	 * @return the pmReasonDescDataModel
	 */
	public IPagingDataModel<PmReasonDesc> getPmReasonDescDataModel() {
		return pmReasonDescDataModel;
	}

	/**
	 * @param pmReasonDescDataModel the pmReasonDescDataModel to set
	 */
	public void setPmReasonDataModel(IPagingDataModel<PmReasonDesc> pmReasonDescDataModel) {
		this.pmReasonDescDataModel = pmReasonDescDataModel;
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
				ReasonSearchBean rSearchBean = getManagedBean().getReasonSearchBean();
				ReasonReqDTO reqDTO = new ReasonReqDTO();
				FacesScopes fs = new FacesScopes();
				reqDTO.setLanguage(fs.getUserLocale().getLanguage());
				reqDTO.setReasonDescSearchBean(searchBeanData);
				reqDTO.setAppTypeId(rSearchBean.getApplicationType());
				reqDTO.setSuppTypeId(rSearchBean.getSupplierType());
				reqDTO.setComTypeId(rSearchBean.getCompanyType());
				IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);	
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
	
	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "somReasonCat");
		}

		return this.docTypeSearchBean;
	}
	
	
	public ISearchBean<PmParameterDesc> getCatCirSearchBean() {
		return catCirSearchBean;
	}


	public void deleteRestrictions(int restrictionType,int rowIndex){
		log.info("deleteRestrictions");
		PmSupplierReason supplierReason = null;
		if(restrictionType == RESTR_APP){
			supplierReason = getManagedBean().getAppTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getAppTypeRestrictions().remove(supplierReason);		
		}else if(restrictionType == RESTR_COMP){
			supplierReason = getManagedBean().getComTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getComTypeRestrictions().remove(supplierReason);	
			
		}else if(restrictionType == RESTR_SUPP){
			supplierReason = getManagedBean().getSuppTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getSuppTypeRestrictions().remove(supplierReason);
			
		}
		
		if(supplierReason != null && !supplierReason.isBrandNew()){
			if(getManagedBean().getDeleteRestrictions() == null){
				getManagedBean().setDeleteRestrictions(new ArrayList<PmSupplierReason>());
			}
			supplierReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			supplierReason.setDeleted(true);
			getManagedBean().getDeleteRestrictions().add(supplierReason);
		}
	}
	
	/**
	 * This method used to trigger uncheck action and set remove existing item in to list for update later.
	 * @param index
	 * **/
	public void checkDocumentRestrictions(int index){
		if(getManagedBean().getDeleteRestrictions() == null){
			getManagedBean().setDeleteRestrictions(new ArrayList<PmSupplierReason>());
		}
		
		int tabIndex = index - 1;		
		if(tabIndex == 0 && !getManagedBean().isSuppAppCheck()){
			if(getManagedBean().getAppTypeRestrictions() !=null &&
					!getManagedBean().getAppTypeRestrictions().isEmpty()){
				for(PmSupplierReason supplierReason : getManagedBean().getAppTypeRestrictions()){
					if(!supplierReason.isBrandNew()){
						supplierReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(supplierReason);
					}
				}
				getManagedBean().getAppTypeRestrictions().clear();
			}		
		}else if(tabIndex == 1 && !getManagedBean().isSuppComCheck()){
			if(getManagedBean().getComTypeRestrictions() !=null &&
					!getManagedBean().getComTypeRestrictions().isEmpty()){
				for(PmSupplierReason supplierReason : getManagedBean().getComTypeRestrictions()){
					if(!supplierReason.isBrandNew()){
						supplierReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(supplierReason);
					}
				}
				getManagedBean().getComTypeRestrictions().clear();
			}
		}else if(tabIndex == 2 && !getManagedBean().isSuppTypeCheck()){
			if(getManagedBean().getSuppTypeRestrictions() !=null &&
					!getManagedBean().getSuppTypeRestrictions().isEmpty()){
				for(PmSupplierReason supplierReason : getManagedBean().getSuppTypeRestrictions()){
					if(!supplierReason.isBrandNew()){
						supplierReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(supplierReason);
					}
				}
				getManagedBean().getSuppTypeRestrictions().clear();
			}
		}
		
		updateTabSelectedIndex(index);
	}
	
	
	/**
	 * update the tab index after for checkbox action
	 * @param index
	 * **/
	public void updateTabSelectedIndex(int index){
		defaultTab();		
		int tabIndex = index - 1;
		if(tabIndex == 0 && getManagedBean().isSuppAppCheck()){
			getManagedBean().setSelectedTabIndex(tabIndex);			
		}else if(tabIndex == 1 && getManagedBean().isSuppComCheck()){
			getManagedBean().setSelectedTabIndex(tabIndex);
		}else if(tabIndex == 2 && getManagedBean().isSuppTypeCheck()){
			getManagedBean().setSelectedTabIndex(tabIndex);
		}		
	}


	/**
	 * @return the restrApp
	 */
	public int getRestrApp() {
		return RESTR_APP;
	}


	/**
	 * @return the restrComp
	 */
	public int getRestrComp() {
		return RESTR_COMP;
	}


	/**
	 * @return the restrSupp
	 */
	public int getRestrSupp() {
		return RESTR_SUPP;
	}
	
	
}
