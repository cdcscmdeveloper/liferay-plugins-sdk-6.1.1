/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.rejectionreason;

import java.sql.Timestamp; //#28962
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
import javax.faces.model.SelectItem; //#28962

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
//#28962 - Start
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
//#28962 - End
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CommonConstants;
//#28962 - Start
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
//#28962 - End
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile; //#28962
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmPIDReason;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonCategory;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmReasonRestr; //#28962
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils; //#28962
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMReasonMaintenanceBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ReasonSearchBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.ReasonHelper;

import org.apache.commons.collections.CollectionUtils; //#28962
import org.apache.commons.lang3.StringUtils;
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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	//#8411 - START
	// Reason Category values
	public static final int JUSTIFICATION = 1;
	public static final int REJECTION_REASON = 2;
	//#8411 - END

	/**
	 * Pre Loading
	 */
	public void preLoading(){
		log.info("preLoading");
		//load critical mappers
		this.loadReasonCategoryMapper();
		this.loadSourcingMethodMapper();
		this.loadConditionFieldMapper();
		this.getManagedBean().setSelectedTabIndex(0);
		this.resetReasonSearchBean();
		this.reasonCategoryChange(true, null);
		this.resetAuditBean(); // 35509
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 *  Backing Bean Constructor
	 */
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
	 * Delete Reason Applicability
	 * 
	 * @param rowIndex
	 */
	public void deleteReasonApplicability(int rowIndex){
		log.info("...............deleteReasonApplicability................");

		// 35509
		if (getManagedBean().getDeletePIDReasonList() == null) {
			getManagedBean().setDeletePIDReasonList(new ArrayList<PmPIDReason>());
		}

		this.getManagedBean().getDeletePIDReasonList()
				.add(this.getManagedBean().getSelectedReason().getPmPIDReasonList().get(rowIndex));
		// 35509
		
			this.getManagedBean().getSelectedReason().getPmPIDReasonList()
					.remove(rowIndex);
	}
	
	/**
	 * Reason Applicable Change
	 * 
	 * @param pidReason
	 */
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
	
	/**
	 * Add more reason applicability
	 */
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
		//#8411 - START
		PmReasonCategory pmReasonCategory = new PmReasonCategory();
		pmReason.addPmReasonCategory(pmReasonCategory);
		//#8411 - END
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
		
		//#28962 - Start
		this.getManagedBean().setSuppAccountTypeList(null);
		this.getManagedBean().setDeleteSuppAccountTypeList(null);
		this.getManagedBean().setAgencyList(null);
		this.getManagedBean().setDeleteAgencyList(null);
		this.getManagedBean().setMinistryCodeName(null);
		this.getManagedBean().setKumpPtjCodeName(null);
		this.getManagedBean().setAgencyCodeName(null);
		//#28962 - End
		
		this.resetAuditBean(); // 35509
		
		log.info("initialRejectReason - End");
	}
	
	/**
	 * load rejection reason object by id
	 * 
	 * @param id
	 */
	public void loadRejectReason(Long id){
		log.info("loadRejectReason - Start");
		try{

			PmReason pmReason = retrieveReasonObjById(id);
			if(pmReason.getConditionField() == null){
			  pmReason.setConditionField(new PmParameter());
			}
			if(pmReason.getSourceMethod() == null){
			pmReason.setSourceMethod(new PmParameter());
			}
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
			loadRestriction(pmReason);
			//#28962 - Start
			if (ParameterConst.WIP_ITEM_PK7.equalsIgnoreCase(getManagedBean().getSelectedReason()
					.getReasonCategoryCode().getParameterCode())) {
				loadSuppType(pmReason);
			}
			if (ParameterConst.SPECIAL_ITEM_WITH_APPROVAL.equalsIgnoreCase(getManagedBean().getSelectedReason()
					.getReasonCategoryCode().getParameterCode())) {
				getManagedBean().setMinistryCodeName(null);
				getManagedBean().setKumpPtjCodeName(null);
				getManagedBean().setAgencyCodeName(null);
				getManagedBean().setAgencyList(null);
				loadAgency();
				loadSuppType(pmReason);
			}
			//#28962 - end
			this.getManagedBean().setDeleteAppTypeRestrictions(null);
			this.getManagedBean().setDeleteComTypeRestrictions(null);
			this.getManagedBean().setDeleteSuppTypeRestrictions(null);
			this.getManagedBean().setDeleteRestrictions(null);
			//#28962 - Start
			this.getManagedBean().setDeleteSuppAccountTypeList(null);
			this.getManagedBean().setDeleteAgencyList(null);
			//#28962 - end
			if(pmReason.getSourceMethod() != null && pmReason.getSourceMethod().getParameterId() != null){
					this.getManagedBean().setSelectedSourceMethodId(pmReason.getSourceMethod().getParameterId());
			}
		
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
						addAuditAppTypeRestrictions(tempReason); // 35509
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
						addAuditComTypeRestrictions(tempReason);// 35509
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
						addAuditSuppTypeRestrictions(tempReason);// 35509
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
		//#8411 - START
		this.getManagedBean().setReasonCategory(null);
		//#8411 - END
		pmReasonDescDataModel = new PmReasonPagingDataModel();
	}
	
	/**
	 * save to Rejection Reason
	 * @return xhtml
	 */
	/**
	 * save to Rejection Reason
	 * @return xhtml
	 */
	public String saveRejectReason(){
		log.info("saveRejectReason - Start");
		String returnPage = null;
		
		//#34445 Start
		try {
			if (this.getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterCode()
					.equals(ParameterConst.PURCHASE_IN_DEFAULT)
					&& (this.getManagedBean().getSelectedReason().getPmPIDReasonList() == null || this.getManagedBean()
							.getSelectedReason().getPmPIDReasonList().size() == 0)) {
				throw new EpException(MessageConst.ERROR_770300, new String[] {});
			}
			// remove pmParameter empty objs
			if (this.getManagedBean().getSelectedReason().getConditionField() != null
					&& this.getManagedBean().getSelectedReason().getConditionField().getParameterId() == null) {
				this.getManagedBean().getSelectedReason().setConditionField(null);
			}
			if (this.getManagedBean().getSelectedReason().getSourceMethod() != null
					&& this.getManagedBean().getSelectedReason().getSourceMethod().getParameterId() == null) {
				this.getManagedBean().getSelectedReason().setSourceMethod(null);
			}
			if (this.getManagedBean().getSelectedReason().isBrandNew()) {
				generateReasonCode();
			}

			if (this.getManagedBean().isSuppReason()) {
				validateRestrictions(this.getManagedBean().getSelectedReason());
				getAllRestrictions();
			}

			if (DbValueConst.RECORD_STATUS_ACTIVE == getManagedBean().getSelectedReason().getRecordStatusFlg()
					.intValue()) {
				validateRejectionDesc();
			}
			
			processRestrSuppAccTypeAgency(); //#28962
			
			/************ AUDIT #28895-35509 *************/
			/*AuditPMBackingBeanTemplate auTemplate = new AuditPMReasonMaintenanceBackingBean();
			IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAuditPMSO.class);
			PmReason selectedPmReason = this.getManagedBean().getSelectedReason();

			if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_REASON) && selectedPmReason.getReasonId() != null) {
				PmReason oldPmReason = retrieveReasonObjById(selectedPmReason.getReasonId());
				getManagedBean().setOldPmReason(oldPmReason);
			}*/
			/************ AUDIT #28895-35509 END *************/
			if (this.getManagedBean().getSelectedReason().isBrandNew()) {
				returnPage = createRejectReason(this.getManagedBean().getSelectedReason());
			} else {
				returnPage = updateRejectReason(this.getManagedBean().getSelectedReason());
			}
			log.info("saveRejectReason - End");

			/************ AUDIT *************/
			/*if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_REASON)) {

				auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_REASON);
			}*/
			/************ AUDIT *************/

		} catch (EpException e) {
			handleException(e);
			returnPage = NAV_TO_MAINTAIN;
		}
		//#34445 End

		return returnPage;	
	}
	
	/**
	 * Generate Reason Code
	 */
	private void generateReasonCode() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
		IParameterSO parameterSO  = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		ParameterReqDTO paramReq = new ParameterReqDTO();
		paramReq.setParameterId(getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterId());
		ParameterResDTO paramRes = parameterSO.retrieveParameterById(paramReq);	
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
				log.info("createRejectReason - successful"); //TODO 770015
				getManagedBean().setNewPmReason(resDTO.getPmReason()); // 35509 Audit PM
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
			List<PmPIDReason> auditPIDReason  = new ArrayList<>(); // 35509
			for(PmPIDReason pidReason: rejectReason.getPmPIDReasonList()){
				log.info(".............pidReason="+pidReason.getDocType());
				if(pidReason.isBrandNew()){
					log.info(".......pidReason.isBrandNew()"+pidReason.isBrandNew());
					log.info(".......this.getPmPidListOrigin().size()"+this.getManagedBean().getPmPidListOrigin().size());
					auditPIDReason.add(pidReason); // 35509
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
			/************ AUDIT #28895-35509 START*************/
			if (getManagedBean().getAuditAddPIDReasonList() == null) {
				getManagedBean().setAuditAddPIDReasonList(new ArrayList<PmPIDReason>());
			}
			this.getManagedBean().getAuditAddPIDReasonList().addAll(auditPIDReason);
			/************ AUDIT #28895-35509 END*************/
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
	
	/**
	 * To load reason category mapper
	 */
	public void loadReasonCategoryMapper(){
		log.info("loadReasonCategoryMapper");
		reasonCategoryMapper = new HashMap<Long,Object>();
		List<PmParameterDesc> parameterDescs = retrieveParamDescObjs(ParameterTypeConst.REASON_CATEGORY);
		for(PmParameterDesc desc : parameterDescs){
			reasonCategoryMapper.put(desc.getPmParameter().getParameterId(), desc.getPmParameter());
		}
	}
	
	/**
	 * To load condition field mapper
	 */
	public void loadConditionFieldMapper(){
		log.info("loadConditionFieldMapper");
		conditionFieldMapper = new HashMap<Long,Object>();
		List<PmParameterDesc> parameterDescs = retrieveParamDescObjs(ParameterTypeConst.CONTRACT_CIRCULAR_CONDITION);
		for(PmParameterDesc desc : parameterDescs){
			conditionFieldMapper.put(desc.getPmParameter().getParameterId(), desc.getPmParameter());
		}
	}
	
	/**
	 * To load sourcing method mapper
	 */
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
	public SelectItem[] reasonCategoryOptions(boolean isSearchBean) {
		log.info("reasonCategoryOptions");
		//#8411 - START
		Integer reasonCategory = null;
		if (isSearchBean){
			reasonCategory = getManagedBean().getReasonCategory();
		} else {
			reasonCategory = getManagedBean().getSelectedReason().getPmReasonCategory().getCategoryCode();
		}			
		return getCategoryTypeDropdownValuesBySearchBean(reasonCategory, ParameterTypeConst.REASON_CATEGORY, 
														 getDocTypeSearchBean());
		//#8411 - END
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

    PmParameter contractRequestParam = getParameterByTypeAndCode(ParameterTypeConst.SOURCING_METHOD, ParameterConst.PID_SOURCE_METHOD_CR);
	List<String> parameterCodes = new ArrayList<String>();
	//20210125@Jacky - Enhancement#29764 - Start
	parameterCodes.add(ParameterConst.DOCTYPE_DIRECT_PURCHASE);
	//20210125@Jacky - Enhancement#29764 - End
	parameterCodes.add(ParameterConst.CONTRACT_TYPE_MINISTRY);
	if(getManagedBean().getSelectedReason().getSourceMethod() != null && getManagedBean().getSelectedReason().getSourceMethod().getParameterId() != null && 
			!getManagedBean().getSelectedReason().getSourceMethod().getParameterId().equals(contractRequestParam.getParameterId())){
		parameterCodes.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
	}
	parameterCodes.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
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
	
	/**
	 * To Purchase From Contract Change
	 */
	public void purchaseFromContractChange(){
		if(this.getManagedBean().getSelectedReason().isContractPurchase()){
			PmParameter paramObj = (PmParameter) souringMethodMapper.get(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
			this.getManagedBean().getSelectedReason().getSourceMethod().setParameterId(paramObj.getParameterId());
		}else{
			
			this.getManagedBean().getSelectedReason().setConditionField(new PmParameter());
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
	 */
	public void sourceMethodChange(){
		log.info("conditionFieldChange");
		boolean invalid = false;
		if(getManagedBean().getSelectedReason().getSourceMethod() != null && getManagedBean().getSelectedReason().getSourceMethod().getParameterId() != null){
			PmParameter contractRequestParam = getParameterByTypeAndCode(ParameterTypeConst.SOURCING_METHOD, ParameterConst.PID_SOURCE_METHOD_CR);
			if(getManagedBean().getSelectedSourceMethodId() != null && getManagedBean().getSelectedSourceMethodId().equals(contractRequestParam.getParameterId()) 
					&& getManagedBean().getSelectedReason().getPmPIDReasonList() != null){
				for(PmPIDReason reason : getManagedBean().getSelectedReason().getPmPIDReasonList()){
					if(reason.getDocType() != null && reason.getDocType().equals(ParameterConst.DOCTYPE_PANEL_CIRCULAR)){
						invalid = true;
						break;
					}
				}
			}
		}
		
		if(invalid){
			PmParameter paramObj = (PmParameter) souringMethodParameterIdMapper.get(this.getManagedBean().getSelectedReason().getSourceMethod().getParameterId());
			this.getManagedBean().getSelectedReason().setSourceMethod(paramObj);
			this.getManagedBean().setSelectedSourceMethodId(paramObj.getParameterId());
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							null,
							"Reason Applicability contains Panel Item."));
			
		}else{
			if(getManagedBean().getSelectedSourceMethodId() != null){
				PmParameter paramObj = (PmParameter) souringMethodParameterIdMapper.get(getManagedBean().getSelectedSourceMethodId());
				this.getManagedBean().getSelectedReason().setSourceMethod(paramObj);
				System.out.println(paramObj.getParameterCode());
				
			}else{
				this.getManagedBean().getSelectedReason().setSourceMethod(null);
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
		//#8411 - START
		if(this.getManagedBean().getReasonCategory()==null){// if reason category is null
			throw new EpException(MessageConst.ERROR_770303);
		} else if(this.getManagedBean().getReasonSearchBean().getReasonCategoryParam()==null 
				|| this.getManagedBean().getReasonSearchBean().getReasonCategoryParam().getParameterId()==null){// if reason type is null
			throw new EpException(MessageConst.ERROR_770129);
		}
		//#8411 - END
		
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
	
	/**
	 * 
	 * @return docTypeSearchBean
	 */
	public ISearchBean<PmParameterDesc> getDocTypeSearchBean() {
		if (this.docTypeSearchBean == null) {
			this.docTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "somReasonCat");
		}

		return this.docTypeSearchBean;
	}
	
	/**
	 * 
	 * @return catCirSearchBean
	 */
	public ISearchBean<PmParameterDesc> getCatCirSearchBean() {
		return catCirSearchBean;
	}


	/**
	 * To Delete Restriction
	 * 
	 * @param restrictionType
	 * @param rowIndex
	 */
	public void deleteRestrictions(int restrictionType,int rowIndex){
		log.info("deleteRestrictions");
		PmSupplierReason supplierReason = null;
		if(restrictionType == RESTR_APP){
			supplierReason = getManagedBean().getAppTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getAppTypeRestrictions().remove(supplierReason);
			deleteAuditAppTypeRestrictions(supplierReason); // 35509
			
		}else if(restrictionType == RESTR_COMP){
			supplierReason = getManagedBean().getComTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getComTypeRestrictions().remove(supplierReason);	
			deleteAuditComTypeRestrictions(supplierReason);// 35509
			
		}else if(restrictionType == RESTR_SUPP){
			supplierReason = getManagedBean().getSuppTypeRestrictions()
					.get(rowIndex);
			supplierReason.setDeleted(true);
			getManagedBean().getSuppTypeRestrictions().remove(supplierReason);
			deleteAuditSuppTypeRestrictions(supplierReason);// 35509
		}

		if(supplierReason != null && !supplierReason.isBrandNew()){
			if(getManagedBean().getDeleteRestrictions() == null){
				getManagedBean().setDeleteRestrictions(new ArrayList<PmSupplierReason>());
			}
			supplierReason.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			supplierReason.setDeleted(true);
			getManagedBean().getDeleteRestrictions().add(supplierReason);
			
			/** 35509 START
			if(getManagedBean().getAuditDeleteRestrictions() == null){
				getManagedBean().setAuditDeleteRestrictions(new ArrayList<PmSupplierReason>());
			}
			getManagedBean().getAuditDeleteRestrictions().add(supplierReason);
			/** 35509 END*/
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
						deleteAuditAppTypeRestrictions(supplierReason); // 35509
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
						deleteAuditComTypeRestrictions(supplierReason);// 35509
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
						deleteAuditSuppTypeRestrictions(supplierReason);// 35509
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
	
	

	
	/**
	 * validate rejection desc if exist
	 * 
	 */
	private void validateRejectionDesc(){
		log.info("validateRejectionDesc");
		String enDesc = null;
		String msDesc = null;
		for(PmReasonDesc desc : getManagedBean().getSelectedReason().getPmReasonDescs()){
			if(CommonConstants.DEFAULT_LANGUAGE_CODE.equals(desc.getPmLanguage().getLanguageCode())){
				enDesc = desc.getReasonDesc();
			}
			
			if(CommonConstants.LANGUAGE_CODE_MALAY.equals(desc.getPmLanguage().getLanguageCode())){
				msDesc = desc.getReasonDesc();
			}
		}
		
		if(StringUtils.isBlank(enDesc) ||StringUtils.isBlank(msDesc) ){
			throw new EpException("Error Desctiptions");
		}
		
		//Create search bean
		SearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>();
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(
						new SearchFilterBean(
								"reasonDesc", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								enDesc.toLowerCase().trim(),
								true),
						new SearchFilterBean(
								"pmLanguage.languageCode", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								CommonConstants.DEFAULT_LANGUAGE_CODE, 
								SearchCondition.CONDITION_AND.value())));
		
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(
						new SearchFilterBean(
								"reasonDesc", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								msDesc.toLowerCase().trim(),
								true),
						SearchCondition.CONDITION_OR.value(),
						new SearchFilterBean(
								"pmLanguage.languageCode", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								CommonConstants.LANGUAGE_CODE_MALAY, 
								SearchCondition.CONDITION_AND.value())));
		
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmReason.reasonCategoryCode.parameterCode", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				getManagedBean().getSelectedReason().getReasonCategoryCode().getParameterCode()
				));
		
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"recordStatusFlg", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				DbValueConst.RECORD_STATUS_ACTIVE
				));
		
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmReason.recordStatusFlg", 
				SearchOperator.OPERATOR_EQUALS.value(), 
				DbValueConst.RECORD_STATUS_ACTIVE
				));
		
		if(!getManagedBean().getSelectedReason().isBrandNew()){
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmReason.reasonId", 
					SearchOperator.OPERATOR_NOT_EQUALS.value(), 
					getManagedBean().getSelectedReason().getReasonId()
					));
		}
		
		//Filter by search bean
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonDescSearchBean(searchBean);
		reqDTO.setValidation(true);
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);	
		ReasonResDTO resDTO = reqSO.retrieveReasonDescBySearchBean(reqDTO);
		if(resDTO.isSuccessful()&& resDTO.getReasonDescList()!=null && !resDTO.getReasonDescList().isEmpty()){ 
			if(getManagedBean().getSelectedReason().isBrandNew()){
				throw new EpException(MessageConst.ERROR_770302,new String[]{
					PmCommonUtil.getResourceBundleLabels("lbl.pm.reason.desc"),
					getParameterDesc(getManagedBean().getSelectedReason().getReasonCategoryCode()),
					PmCommonUtil.getResourceBundleLabels("lbl.pm.reason.desc")					
				});
			}else{
				throw new EpException(MessageConst.ERROR_770220, new String[] {
						PmCommonUtil.getResourceBundleLabels("lbl.pm.reason.desc")
				});	
			}
		}
	}
	
	//#8411 - START
	/**
	 * @return the justification
	 */
	public int getJustification() {
		return JUSTIFICATION;
	}

	/**
	 * @return the rejection reason
	 */
	public int getRejectionReason() {
		return REJECTION_REASON;
	}
	
	/**
	 * remove suppReason component when changing reason category
	 */
	public void resetCategoryChange(boolean isSearchBean) {
		log.info("resetOnCategoryChange");
		if (isSearchBean){
			this.getManagedBean().getReasonSearchBean().setReasonCategoryParam(new PmParameter());
			this.getManagedBean().setSearchSuppReason(false);
		} else {
			this.getManagedBean().getSelectedReason().setReasonCategoryCode(new PmParameter());
			this.getManagedBean().setSuppReason(false);
		}
	}
	
	//#8411 - END
	
	//35509
	/**
	 * private List <PmSupplierReason> auditAddAppTypeRestrictions; private List
	 * <PmSupplierReason> auditAddComTypeRestrictions; private List
	 * <PmSupplierReason> auditAddSuppTypeRestrictions;
	 * 
	 * @param pmSupplierReason
	 */
	private void addAuditAppTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditAddAppTypeRestrictions() == null) {
			this.getManagedBean().setAuditAddAppTypeRestrictions(new ArrayList<PmSupplierReason>());
		}
		this.getManagedBean().getAuditAddAppTypeRestrictions().add(pmSupplierReason);
	}

	private void addAuditComTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditAddComTypeRestrictions() == null) {
			this.getManagedBean().setAuditAddComTypeRestrictions(new ArrayList<PmSupplierReason>());
		}
		this.getManagedBean().getAuditAddComTypeRestrictions().add(pmSupplierReason);
	}

	private void addAuditSuppTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditAddSuppTypeRestrictions() == null) {
			this.getManagedBean().setAuditAddSuppTypeRestrictions(new ArrayList<PmSupplierReason>());
		}
		this.getManagedBean().getAuditAddSuppTypeRestrictions().add(pmSupplierReason);
	}
	
	private void deleteAuditAppTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditDeleteAppRestrictions() == null) {
			this.getManagedBean().setAuditDeleteAppRestrictions(new ArrayList<PmSupplierReason>());
		}
		if (!pmSupplierReason.isBrandNew()) {
			this.getManagedBean().getAuditDeleteAppRestrictions().add(pmSupplierReason);
		}
		// remove from added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddAppTypeRestrictions())) {
			for (PmSupplierReason supplierReason : this.getManagedBean().getAuditAddAppTypeRestrictions()) {
				if (supplierReason.getRestrType().getParameterCode()
						.equals(pmSupplierReason.getRestrType().getParameterCode())) {
					getManagedBean().getAuditAddAppTypeRestrictions().remove(pmSupplierReason);
				}
			}
		}
	}

	private void deleteAuditComTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditDeleteComRestrictions() == null) {
			this.getManagedBean().setAuditDeleteComRestrictions(new ArrayList<PmSupplierReason>());
		}
		if (!pmSupplierReason.isBrandNew()) {
			this.getManagedBean().getAuditDeleteComRestrictions().add(pmSupplierReason);
		}
		// remove from added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddComTypeRestrictions())) {
			for (PmSupplierReason supplierReason : this.getManagedBean().getAuditAddComTypeRestrictions()) {
				if (supplierReason.getRestrType().getParameterCode()
						.equals(pmSupplierReason.getRestrType().getParameterCode())) {
					getManagedBean().getAuditAddComTypeRestrictions().remove(pmSupplierReason);
				}
			}
		}
	}

	private void deleteAuditSuppTypeRestrictions(PmSupplierReason pmSupplierReason) {

		if (this.getManagedBean().getAuditDeleteSuppRestrictions() == null) {
			this.getManagedBean().setAuditDeleteSuppRestrictions(new ArrayList<PmSupplierReason>());
		}
		if (!pmSupplierReason.isBrandNew()) {
			this.getManagedBean().getAuditDeleteSuppRestrictions().add(pmSupplierReason);
		}
		// remove from added list
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getAuditAddSuppTypeRestrictions())) {
			for (PmSupplierReason supplierReason : this.getManagedBean().getAuditAddSuppTypeRestrictions()) {
				if (supplierReason.getRestrType().getParameterCode()
						.equals(pmSupplierReason.getRestrType().getParameterCode())) {
					getManagedBean().getAuditAddSuppTypeRestrictions().remove(pmSupplierReason);
				}
			}
		}
	}
	
	private void resetAuditBean(){
		this.getManagedBean().setAuditAddAppTypeRestrictions(null);
		this.getManagedBean().setAuditAddComTypeRestrictions(null);
		this.getManagedBean().setAuditAddSuppTypeRestrictions(null);
		this.getManagedBean().setAuditAddPIDReasonList(null);
		this.getManagedBean().setDeletePIDReasonList(null);
		this.getManagedBean().setAuditDeleteAppRestrictions(null);
		this.getManagedBean().setAuditDeleteComRestrictions(null);
		this.getManagedBean().setAuditDeleteSuppRestrictions(null);
	}
	//35509 - END
	
	//#28962 - Start
	/**
	 * load reasonSuppAccTypeDropDown
	 * 
	 * @return getDropdownValuesBySearchBean
	 */
	public SelectItem[] reasonSuppAccTypeDropDown() {
		return getDropdownValuesBySearchBean(ParameterTypeConst.REASON_SUPPLIER_ACCOUNT_TYPE, null);
	}

	/**
	 * load the pm_reason_restr to tab view by supplier type
	 * 
	 * @param PmReasonRestr
	 * @param pmReason
	 */
	public void loadSuppType(PmReason pmReason) {
		log.info("loadSuppType - Start");
		List<PmReasonRestr> suppAccType = new ArrayList<>();
		if (pmReason != null) {
		    for (PmReasonRestr pmSuppAppType : pmReason.getPmReasonRestr()) {
		        if (pmSuppAppType.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
		                && pmSuppAppType.getSupplAcctTypeId() != null) {
		            suppAccType.add(pmSuppAppType);
		        }
		    }
		}
		this.getManagedBean().setSuppAccountTypeList(suppAccType);

		log.info("loadSuppType - End");
	}
	
	/**
	 * load the pm_reason_restr to tab view agency list
	 * 
	 */
	public void loadAgency() {
		log.info("loadAgency - Start");

		IReasonSO reqSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		
		reqDTO.setReasonId(getManagedBean().getSelectedReason().getReasonId());
		if (getManagedBean().getMinistryCodeName() != null && !getManagedBean().getMinistryCodeName().isEmpty()) {
			reqDTO.setMinistryCodeName(getManagedBean().getMinistryCodeName());
		}
		if (getManagedBean().getKumpPtjCodeName() != null && !getManagedBean().getKumpPtjCodeName().isEmpty()) {
			reqDTO.setKumpPtjCodeName(getManagedBean().getKumpPtjCodeName());
		}
		if (getManagedBean().getAgencyCodeName() != null && !getManagedBean().getAgencyCodeName().isEmpty()) {
			reqDTO.setAgencyCodeName(getManagedBean().getAgencyCodeName());
		}
		ReasonResDTO resDTO = reqSO.retrieveAgencyListByReasonId(reqDTO);
		this.getManagedBean().setAgencyList(resDTO.getAgencyResultBeanList());

		log.info("loadAgency - End");
	}

	/**
	 * Initializes and resets the PTJ popup screen.
	 */
	public void initPtjPopup() {
		this.getManagedBean().setMinistryProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		this.getManagedBean().setPtjCodeName(ProfileManagementConst.PTJ_CODE);
		this.getManagedBean().setPtjCodeNameSearchValues(null);
		this.getManagedBean().setSelectedOrgsProfile(null);
		this.getManagedBean().setPtjList(null);
	}

	/**
	 * Triggers a search for organization profiles.
     * This method refresh the data model to reload the organisation profile list
	 */
	public void searchOrgProfile() {
		getManagedBean().setPtjList(null);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if(getManagedBean().getMinistryProfileId() != null){
			reqDTO.setMinistryId(getManagedBean().getMinistryProfileId());
		}
		if(getManagedBean().getJabatanProfileId() != null){
			reqDTO.setJabatanId(getManagedBean().getJabatanProfileId());
		}
		
		ISearchBean<PmOrgProfile> searchBeanData = getOrgProfileSearchBeanData();
		
		IOrgProfileSO orgProfileSO = BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		reqDTO.setOrgProfileSearchBean(searchBeanData);
		OrgProfileResDTO resDTO;
		resDTO = orgProfileSO.retrievePtjListByParents(reqDTO);
		
		if (resDTO != null && resDTO.isSuccessful() && resDTO.getOrgProfileResultBean() !=null) {
			getManagedBean().setPtjList(resDTO.getOrgProfileResultBean().getResultList());
		}
	}
	
	private ISearchBean<PmOrgProfile> getOrgProfileSearchBeanData(){
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<>();
		if (StringUtils.isNotBlank(getManagedBean().getPtjCodeName())
				&& (StringUtils.isNotBlank(getManagedBean().getPtjCodeNameSearchValues()))) {
			if (getManagedBean().getPtjCodeName().equalsIgnoreCase(
					ProfileManagementConst.PTJ_CODE) || 
					getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_CODE )
							|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
									ProfileManagementConst.PEGAWAI_CODE)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgValidity.orgCode",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);

			} else if (getManagedBean().getPtjCodeName()
					.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME) ||
					 getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_NAME)
					|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.PEGAWAI_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgValidity.orgName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
		searchBean.getSortColumns().add(new SearchOrderBean("pmOrgValidity.orgName"));
		return searchBean;
	}

	/**
	 * Resets the organisation profile search filters and reloads the data model.
	 */
	public void resetOrgProfile() {
		this.getManagedBean().setMinistryProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		this.getManagedBean().setPtjCodeNameSearchValues(null);
		this.getManagedBean().setSelectedOrgsProfile(null);
		this.getManagedBean().setPtjList(null);
	}
	
	/**
	 * search agency list by Ministry, Kumpulan Ptj and Ptj
	 */
	public void searchOrganisation() {
		
		String ministry = getManagedBean().getMinistryCodeName();
		String kumpPtj = getManagedBean().getKumpPtjCodeName();
		String ptj = getManagedBean().getAgencyCodeName();
		List<String> paramList = new ArrayList<>();
		if (ministry != null && !ministry.trim().isEmpty() && ministry.trim().length() < 3) {
			paramList.add(ResourceBundleUtils.getMessage("lbl.pm.reason.ministry",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		}

		if (kumpPtj != null && !kumpPtj.trim().isEmpty() && kumpPtj.trim().length() < 3) {
			paramList.add(ResourceBundleUtils.getMessage("lbl.pm.reason.kumpulan.ptj",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		}

		if (ptj != null && !ptj.trim().isEmpty() && ptj.trim().length() < 3) {
			paramList.add(ResourceBundleUtils.getMessage("lbl.pm.reason.ptj", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		if (paramList != null && !paramList.isEmpty()) {
			String joined = joinList(paramList, ", ");
			throw new EpException(MessageConst.ERROR_770304, new String[] { joined });
		}
		
		long start = System.currentTimeMillis();
		loadAgency();
		long end = System.currentTimeMillis();
		log.info("Execution time for searchOrganisation : " + (end - start) + " ms");
	}

	/**
	 * reset agency list
	 */
	public void resetOrganisationSearchBean() {
		long start = System.currentTimeMillis();
		getManagedBean().setMinistryCodeName(null);
		getManagedBean().setKumpPtjCodeName(null);
		getManagedBean().setAgencyCodeName(null);
		getManagedBean().setAgencyList(null);
		loadAgency();
		long end = System.currentTimeMillis();
		log.info("Execution time for resetOrganisationSearchBean : " + (end - start) + " ms");
	}
	
	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}

	public SelectItem[] getJabatanDropdown() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean().getMinistryProfileId());
	}

	/**
	 * Returns a list of {SelectItem} options for PTJ or KPTJ
	 * dropdown based on the provided organization type.
	 *
	 * The method dynamically builds a dropdown list depending on the type of
	 * organization:
	 * 
	 * @param orgType
	 * @return items
	 */
	public SelectItem[] getPtjOrKptjDropDown(String orgType) {
		SelectItem[] items = new SelectItem[2];
		if (ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(orgType)) {
			items[0] = new SelectItem(ProfileManagementConst.PEGAWAI_CODE,
					ResourceBundleUtils.getMessage("lbl.pm.org.pegaiCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PEGAWAI_NAME,
					ResourceBundleUtils.getMessage("lbl.pm.org.pegaiName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		}
		if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
			items[0] = new SelectItem(ProfileManagementConst.KPTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.KPTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
			items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		}

		return items;
	}
	
	/**
	 * Checks if the supplier account type already exists in the list
	 * 
	 * @param rowIndex
	 * @param newSuppAcountType
	 */
	public void checkExtSuppAccType(int rowIndex, Long newSuppAcountType) {

		PmReasonRestr current = getManagedBean().getSuppAccountTypeList().get(rowIndex);

		List<PmReasonRestr> extList = this.getManagedBean().getSuppAccountTypeList();
		if (extList == null || extList.isEmpty() || newSuppAcountType == null) {
			return;
		}

		for (PmReasonRestr ext : extList) {
			Long suppAccTypeId = ext.getSupplAcctTypeId().getParameterId();
			if (ext != current && newSuppAcountType.equals(suppAccTypeId)) {
				current.setSupplAcctTypeId(new PmParameter());
				break;
			}
		}
	}

	/**
	 * Add new supplier account type
	 */
	public void addSuppAccType() {
		log.info("addSuppAccType");

		PmReasonRestr suppAccType = new PmReasonRestr();
		suppAccType.setRecordStatus(true);
		suppAccType.setSupplAcctTypeId(new PmParameter());
		suppAccType.setCreatedBy(getUserIdSessionFromLoginId());
		suppAccType.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		suppAccType.setChangedBy(suppAccType.getCreatedBy());

		if (this.getManagedBean().getSuppAccountTypeList() == null) {
			this.getManagedBean().setSuppAccountTypeList(new ArrayList<PmReasonRestr>());
		}

		int index = this.getManagedBean().getSuppAccountTypeList().size();
		log.info(".......addSuppAccType index.......=" + index);
		this.getManagedBean().getSuppAccountTypeList().add(index, suppAccType);
	}

	/**
	 * To delete supplier account type
	 * 
	 * @param rowIndex
	 */
	public void deleteSuppAccType(int rowIndex) {
	    log.info("deleteSuppAccType" + rowIndex);

	    PmReasonRestr suppAccType;
	    suppAccType = getManagedBean().getSuppAccountTypeList().get(rowIndex);

	    if (suppAccType != null
	            && suppAccType.getReasonRestrId() != null
	            && !suppAccType.isBrandNew()) {
	        if (getManagedBean().getDeleteSuppAccountTypeList() == null) {
	            getManagedBean().setDeleteSuppAccountTypeList(new ArrayList<PmReasonRestr>());
	        }
	        suppAccType.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
	        suppAccType.setDeleted(true);
	        getManagedBean().getDeleteSuppAccountTypeList().add(suppAccType);
	    }

	    getManagedBean().getSuppAccountTypeList().remove(rowIndex);
	}

	/**
	 * Add new agency values
	 */
	public void addAgency(boolean selectedAllAgency) {
		log.info("addAgency ");
		long start = System.currentTimeMillis();
		
		List<PmOrgProfile> selected = null;
		
		//add all agency..
		if (selectedAllAgency && CollectionUtils.isNotEmpty(getManagedBean().getPtjList())) {
			this.getManagedBean().setSelectedOrgsProfile(null);
			selected = getManagedBean().getPtjList();
		} else {
		//add selected agency..
			selected = getManagedBean().getSelectedOrgsProfile();
		}

		List<PmReasonRestr> agencyList = ensureAgencyList();
		Set<Long> existingOrgProfileIds = extractExistingOrgProfileIds(agencyList);

		Long userId = getUserIdSessionFromLoginId();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		PmOrgProfile ministry = retrieveMinistryOrgProfile(selected.get(0).getOrgProfileId());		

		for (PmOrgProfile orgProfile : selected) {
			addAgencyNew(agencyList, existingOrgProfileIds, orgProfile, userId, now, ministry);
		}
		
		long end = System.currentTimeMillis();
		log.info("Execution time for addAgency: " + (end - start) + " ms");

		this.getManagedBean().setSelectedOrgsProfile(null);
		this.getManagedBean().setPtjList(null);
	}

	/* -------------------- helpers (small + focused) -------------------- */

	private List<PmReasonRestr> ensureAgencyList() {
		if (getManagedBean().getAgencyList() == null) {
			getManagedBean().setAgencyList(new ArrayList<PmReasonRestr>());
		}
		return getManagedBean().getAgencyList();
	}

	private Set<Long> extractExistingOrgProfileIds(List<PmReasonRestr> agencyList) {
		Set<Long> ids = new HashSet<>();
		for (PmReasonRestr existingAgency : agencyList) {
			ids.add(existingAgency.getAgencyId().getOrgProfileId());
		}
		return ids;
	}

	private void addAgencyNew(List<PmReasonRestr> agencyList, Set<Long> existingOrgProfileIds,
			PmOrgProfile orgProfile, Long userId, Timestamp now, PmOrgProfile ministryId) {
		if (orgProfile == null) {
			return;
		}

		Long orgId = orgProfile.getOrgProfileId();
		if (orgId == null) {
			return;
		}

		if (existingOrgProfileIds.contains(orgId)) {
			return;
		}

		PmReasonRestr agency = new PmReasonRestr();
		agency.setRecordStatus(true);
		agency.setAgencyId(orgProfile);
		agency.setCreatedBy(userId);
		agency.setCreatedDate(now);
		agency.setChangedBy(userId);
		agency.setMinistryId(ministryId);
		agency.setKumpPtjId(retrieveJabatanOrgProfile(orgId));

		agencyList.add(agency);
		existingOrgProfileIds.add(orgId);

		log.info(".......agencyList.size.......=" + agencyList.size());
	}
	
	/**
	 * To delete agency
	 * 
	 * @param rowIndex
	 */
	public void deleteAgency(int rowIndex) {
		log.info("deleteAgencyValues" + rowIndex);

		PmReasonRestr agency;
		agency = getManagedBean().getAgencyList().get(rowIndex);

		if (agency != null && agency.getReasonRestrId() != null && !agency.isBrandNew()) {
			if (getManagedBean().getDeleteAgencyList() == null) {
				getManagedBean().setDeleteAgencyList(new ArrayList<PmReasonRestr>());
			}
			agency.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			agency.setDeleted(true);
			getManagedBean().getDeleteAgencyList().add(agency);
		}
		getManagedBean().getAgencyList().remove(rowIndex);
	}

	/**
	 * Prepares and updates the list of {PmReasonRestr} records for the selected reason.
	 * 
	 * This method consolidates supplier account types, agencies, and their
	 * deleted records into the selected reason's {PmReasonRestr} list.
	 */
	public void processRestrSuppAccTypeAgency() {
		long start = System.currentTimeMillis();
		try {
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getSuppAccountTypeList())) {
				saveSuppAccType(this.getManagedBean().getSuppAccountTypeList());
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getAgencyList())) {
				saveAgency(this.getManagedBean().getAgencyList());
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getDeleteSuppAccountTypeList())) {
				deletedSuppAccType(this.getManagedBean().getDeleteSuppAccountTypeList());
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getDeleteAgencyList())) {
				deletedAgency(this.getManagedBean().getDeleteAgencyList());
			}
			
			long end = System.currentTimeMillis();
			log.info("Execution time for processRestrSuppAccTypeAgency : " + (end - start) + " ms");
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("PM", "processRestrSuppAccTypeAgency", null, "RejectionReasonBackingBean", null, e);
		}
	}

	/* ======================== Extracted processors (logic preserved)========================*/

	private void saveSuppAccType(List<PmReasonRestr> satList) {
		for (PmReasonRestr tempReason : satList) {
			if (tempReason.isBrandNew()) {
				log.info("new supplier account type");
				if (hasSuppAccTypeKey(tempReason)) {
					// maintained: add new supplier item
					ensureTargetListExists();
					this.getManagedBean().getSelectedReason().addPmReasonRestr(tempReason);
				}
			} else {
				if (hasSuppAccTypeKey(tempReason)) {
					log.info("update supplier account type");
					List<PmReasonRestr> target = getManagedBean().getSelectedReason().getPmReasonRestr();
					updateExtById(target, tempReason);
				}
			}
		}
	}

	private void saveAgency(List<PmReasonRestr> agencyList) {
		for (PmReasonRestr agency : agencyList) {
			if (agency.isBrandNew()) {
				log.info("new agency");
				if (hasValidAgencyKey(agency)) {
					ensureTargetListExists();
					this.getManagedBean().getSelectedReason().addPmReasonRestr(agency);
				}
			}
		}
	}

	private void deletedSuppAccType(List<PmReasonRestr> delSatList) {
		for (PmReasonRestr tempReason : delSatList) {
			if (tempReason.isBrandNew()) {
				if (hasSuppAccTypeKey(tempReason)) {
					ensureTargetListExists();
					this.getManagedBean().getSelectedReason().addPmReasonRestr(tempReason);
				}
			} else {
				if (hasSuppAccTypeKey(tempReason)) {
					List<PmReasonRestr> target = getManagedBean().getSelectedReason().getPmReasonRestr();
					updateExtById(target, tempReason);
				}
			}
		}
	}

	private void deletedAgency(List<PmReasonRestr> delAgencyList) {
		for (PmReasonRestr delAgency : delAgencyList) {
			if (delAgency.isBrandNew()) {
				if (hasValidAgencyKey(delAgency)) {
					ensureTargetListExists();
					this.getManagedBean().getSelectedReason().addPmReasonRestr(delAgency);
				}
			} else {
				if (hasValidAgencyKey(delAgency)) {
					List<PmReasonRestr> target = getManagedBean().getSelectedReason().getPmReasonRestr();
					updateExtById(target, delAgency);
				}
			}
		}
	}

	/* ======================== Helpers (no logic change) ========================*/

	// EXACT original validation for supplier key (also used by agency update branch)
	private boolean hasSuppAccTypeKey(PmReasonRestr r) {
		return r.getSupplAcctTypeId() != null && r.getSupplAcctTypeId().getParameterId() != null;
	}

	private boolean hasValidAgencyKey(PmReasonRestr r) {
		return r.getAgencyId() != null && r.getAgencyId().getOrgProfileId() != null;
	}

	private void ensureTargetListExists() {
		if (this.getManagedBean().getSelectedReason().getPmReasonRestr() == null) {
			this.getManagedBean().getSelectedReason().setPmReasonRestr(new ArrayList<PmReasonRestr>());
		}
	}

	/**
	 * Replace elements with the same reasonRestrId; if not found, do nothing
	 * (matches original loop behavior that only sets matches). Note: original
	 * loop never broke after a set; we keep that behavior.
	 */
	private void updateExtById(List<PmReasonRestr> target, PmReasonRestr pmReasonRestr) {
		if (target == null) {
			return;
		}
		Long id = pmReasonRestr.getReasonRestrId();
		if (id == null) {
			return;
		}

		for (int i = 0; i < target.size(); i++) {
			PmReasonRestr existing = target.get(i);
			if (existing != null && existing.getReasonRestrId() != null && id.equals(existing.getReasonRestrId())) {
				target.set(i, pmReasonRestr);
				// no break (maintain original)
			}
		}
	}
	
	private static String joinList(List<String> list, String delimiter) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < list.size(); i++) {
	        sb.append(list.get(i));
	        if (i < list.size() - 1) {
	            sb.append(delimiter);
	        }
	    }
	    return sb.toString();
	}
	//#28962 - End
}