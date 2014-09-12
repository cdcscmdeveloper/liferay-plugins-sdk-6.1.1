package my.ep.web.backingbean.pm.common.supportdoc;

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
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/**
 * Backing Bean for Support Document
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name="supportDocBackingBean")
@ViewScoped
public class SupportDocumentBackingBean extends BaseBackingBean<SupportDocumentManagedBean>{
	private static Logger log = Logger.getLogger(SupportDocumentBackingBean.class);
	//Navigation Details
	public static final String NAV_TO_MAINTAIN = "supportingDocumentMaintain";
	public static final String NAV_TO_SEARCH = "supportingDocument";
	
	//Document Restriction Type
	public static final int RESTR_APP = 1;
	public static final int RESTR_COMP = 2;
	public static final int RESTR_SUPP = 3;
	public static final int RESTR_CATEGORY = 4;
	public static final int CHECKLIST = 5;
	
	
	
	//Mapper for DDL object
	private static Map<Long, Object> docCategoryMapper;
	
	//List for Paging
	private IPagingDataModel<PmDocument> pmDocumentDataModel = new PmDocumentDataModel();

	//Default value
	public static final boolean SEARCH_DEFAULT = false;
	
	public void preLoading(){
		log.info("preLoading");
		this.resetSupportDocSearchBean();
		this.getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetSupportDocSearchBean(){
		log.info("resetSupportDocSearchBean");
		this.getManagedBean().setSupportDocSearchBean(new SupportDocumentSearchBean());
	}
	
	
	/**
	 * redirect to Support Doc Details
	 * @param id
	 * @return xhtml
	 */
	public String redirectToMaintainDoc(Long id){
		log.info("redirectToMaintainDoc - Start");
		try{
			this.retrieveDocCategoryMap();
			if(id!=null && id>0){
				loadSupportDoc(id);
			}else{
				initialSupportDoc();
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToMaintainDoc - End");
		return NAV_TO_MAINTAIN;
	}
	
	/**
	 * retrieve all document category into mapper
	 */
	private void retrieveDocCategoryMap(){
		log.info("retrieveDocCategoryMap");
		List<PmParameterDesc> parameterDescs = getDocumentCategoryFilteredOptions();
		docCategoryMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			docCategoryMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * set document category
	 */
	public void docCategoryChange(){
		log.info("docCategoryChange");
		if(this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterId()!=null){
			PmParameter pmParameter = (PmParameter) docCategoryMapper.get(this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterId());
			this.getManagedBean().getSelectedDocument().getDocCategoryCode().setParameterCode(pmParameter.getParameterCode());
			if(!ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(pmParameter.getParameterCode())){
				this.getManagedBean().setCheckList(true);
				//load default value for checklist
				if(this.getManagedBean().getSelectedDocument().isBrandNew() && this.getManagedBean().getSelectedDocument().getDocDefinitionTypeCode()==null){
					PmParameter tempParam = PmCommonUtil.retrievePmParameter(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, ProfileManagementConst.DEFAULT_DOC_DEFINITION);
					this.getManagedBean().getSelectedDocument().getDocDefinitionTypeCode().setParameterId(tempParam.getParameterId());
				}
				if(this.getManagedBean().getSelectedDocument().isBrandNew() && this.getManagedBean().getSelectedDocument().getChecklistActionCode()==null){
					PmParameter tempParam = PmCommonUtil.retrievePmParameter(ParameterTypeConst.SC_SUPPLIER_ACTION, ProfileManagementConst.DEFAULT_SUPPLIER_ACTION);
					this.getManagedBean().getSelectedDocument().getChecklistActionCode().setParameterId(tempParam.getParameterId());
				}
				if(this.getManagedBean().getSelectedDocument().isBrandNew()){
					this.getManagedBean().getSelectedDocument().setPmZone(null);
				}
				
			}else{
				if(this.getManagedBean().getSelectedDocument().isBrandNew()){
					this.getManagedBean().setCheckList(false);
					this.getManagedBean().getSelectedDocument().setPmZone(new PmZone());
					this.getManagedBean().getSelectedDocument().setDocDefinitionTypeCode(null);
					this.getManagedBean().getSelectedDocument().setChecklistActionCode(null);
				}
			
			}
		}else{
			if(this.getManagedBean().getSelectedDocument().isBrandNew()){
				this.getManagedBean().setCheckList(false);
			}
		}
			
	}
	
	/**
	 * initial new supporting document object
	 */
	public void initialSupportDoc(){
		log.info("initialSupportDoc - Start");
		PmDocument pmDocument = new PmDocument();
		pmDocument.setDocCategoryCode(new PmParameter()); // checklist, supp doc
		pmDocument.setPmZone(new PmZone()); //supp doc
		pmDocument.setDocDefinitionTypeCode(null);  // checklist
		pmDocument.setChecklistActionCode(null); // checklist
		this.getManagedBean().setAppTypeRestrictions(null);
		this.getManagedBean().setComTypeRestrictions(null);
		this.getManagedBean().setSuppTypeRestrictions(null);
		this.getManagedBean().setCategoryRestrictions(null);
		this.getManagedBean().setCheckListRestrictions(null);
		this.getManagedBean().setDeleteAppTypeRestrictions(null);
		this.getManagedBean().setDeleteComTypeRestrictions(null);
		this.getManagedBean().setDeleteSuppTypeRestrictions(null);
		this.getManagedBean().setDeleteCategoryRestrictions(null);
		this.getManagedBean().setDeleteCheckListRestrictions(null);
		this.getManagedBean().setSelectedDocument(pmDocument);
		this.getManagedBean().setCheckList(false);
		this.getManagedBean().setSelectedTabIndex(0);
		log.info("initialSupportDoc - End");
	}
	
	/**
	 * load supporting document object by id
	 */
	public void loadSupportDoc(Long id){
		log.info("loadSupportDoc - Start");
		try{
			initialSupportDoc();
			PmDocument pmDocument = retrieveDocumentById(id);
			if(pmDocument.getDocCategoryCode()==null){
				pmDocument.setDocCategoryCode(new PmParameter());
			}
			this.getManagedBean().setSelectedDocument(pmDocument);
			loadRestriction(pmDocument);
			docCategoryChange();
		}catch (Exception e){
			e.printStackTrace();
			throw new EpException("Unable to retrieve document. " + e.toString()); //TODO
		}
		log.info("loadSupportDoc - End");
	}
	/**
	 * set default tab
	 * @param pmDocument
	 */
	public void defaultTab(PmDocument pmDocument){
		log.info("defaultTab");
		if(pmDocument.isByAppType()){
			this.getManagedBean().setSelectedTabIndex(RESTR_APP);
		}else if(pmDocument.isByCompType()){
			this.getManagedBean().setSelectedTabIndex(RESTR_COMP);
		}else if(pmDocument.isBySupplierType()){
			this.getManagedBean().setSelectedTabIndex(RESTR_SUPP);
		}else if(pmDocument.isByCategory()){
			this.getManagedBean().setSelectedTabIndex(RESTR_CATEGORY);
		}
	}
	/**
	 * load the pm document to tab view by restriction type
	 * @param pmDocument
	 */
	public void loadRestriction(PmDocument pmDocument){
		log.info("loadRestriction - Start");
		if(pmDocument!=null && pmDocument.getDocCategoryCode()!=null){
			if(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(pmDocument.getDocCategoryCode().getParameterCode())){
				if(pmDocument.getPmZone()==null){
					pmDocument.setPmZone(new PmZone());
				}
				
				List <PmDocumentRestr> appTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List <PmDocumentRestr> comTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List <PmDocumentRestr> suppTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List <PmDocumentRestr> categoryRestrictions = new ArrayList<PmDocumentRestr>();
				for(PmDocumentRestr pmDocumentRestr:pmDocument.getPmDocumentRestrs()){
					if(pmDocument.isByAppType() && pmDocumentRestr.getAppTypeCode()!=null){
						appTypeRestrictions.add(pmDocumentRestr);
					}
					if(pmDocument.isByCompType() && pmDocumentRestr.getCompanyTypeCode()!=null){
						comTypeRestrictions.add(pmDocumentRestr);
					}
					if(pmDocument.isBySupplierType() && pmDocumentRestr.getSupplierTypeCode()!=null){
						suppTypeRestrictions.add(pmDocumentRestr);
					}
					if(pmDocument.isByCategory() && pmDocumentRestr.getPmCategoryL3()!=null){
						pmDocumentRestr.setPmCategoryL3(retrieveCategoryL3DtlById(pmDocumentRestr.getPmCategoryL3().getCategoryL3Id()));
						categoryRestrictions.add(pmDocumentRestr);
					}
				}

				this.getManagedBean().setAppTypeRestrictions(appTypeRestrictions);
				this.getManagedBean().setComTypeRestrictions(comTypeRestrictions);
				this.getManagedBean().setSuppTypeRestrictions(suppTypeRestrictions);
				this.getManagedBean().setCategoryRestrictions(categoryRestrictions);
				
				defaultTab(pmDocument);
			}else if(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST.equals(pmDocument.getDocCategoryCode().getParameterCode())
					|| ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST.equals(pmDocument.getDocCategoryCode().getParameterCode())){
				if(pmDocument.getDocDefinitionTypeCode()==null){
					pmDocument.setDocDefinitionTypeCode(new PmParameter());
				}
				if(pmDocument.getChecklistActionCode()==null){
					pmDocument.setChecklistActionCode(new PmParameter());
				}
				
				List <PmDocumentRestr> checklistRestriction = new ArrayList<PmDocumentRestr>();
				for(PmDocumentRestr pmDocumentRestr:pmDocument.getPmDocumentRestrs()){
					checklistRestriction.add(pmDocumentRestr);
				}
				this.getManagedBean().setCheckListRestrictions(checklistRestriction);

			}
		}
		log.info("loadRestriction - End");
	}
	
	

	/**
	 * redirect to Support Doc Search
	 * @return xhtml
	 */
	public String redirectToSearchSupportDoc(){
		log.info("redirectToSearchSupportDoc - Start");
		try{
			
			
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToSearchSupportDoc - End");
		return NAV_TO_SEARCH;
	
	}
	
	
	
	/**
	 * add new document restriction
	 * @param restrictionType
	 */
	public void addRestriction(int restrictionType){
		log.info("addRestriction");
		log.info("restrictionType = " + restrictionType);
		if(validateUniqueRestrType(restrictionType)){
			PmDocumentRestr pmDocumentRestr = new PmDocumentRestr();
			if(restrictionType == RESTR_APP){
				if(this.getManagedBean().getAppTypeRestrictions()==null){
					this.getManagedBean().setAppTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setAppTypeCode(new PmParameter());
				this.getManagedBean().getAppTypeRestrictions().add(pmDocumentRestr);
			}else if(restrictionType == RESTR_COMP){
				if(this.getManagedBean().getComTypeRestrictions()==null){
					this.getManagedBean().setComTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setCompanyTypeCode(new PmParameter());
				this.getManagedBean().getComTypeRestrictions().add(pmDocumentRestr);
			}else if(restrictionType == RESTR_SUPP){
				if(this.getManagedBean().getSuppTypeRestrictions()==null){
					this.getManagedBean().setSuppTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setSupplierTypeCode(new PmParameter());
				this.getManagedBean().getSuppTypeRestrictions().add(pmDocumentRestr);
			}else if(restrictionType == RESTR_CATEGORY){
				if(this.getManagedBean().getCategoryRestrictions()==null){
					this.getManagedBean().setCategoryRestrictions(new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setPmCategoryL3(new PmCategoryL3());
				this.getManagedBean().getCategoryRestrictions().add(pmDocumentRestr);
			}else if(restrictionType == CHECKLIST){
				if(this.getManagedBean().getCheckListRestrictions()==null){
					this.getManagedBean().setCheckListRestrictions(new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setProcurementCatCode(new PmParameter());
				this.getManagedBean().getCheckListRestrictions().add(pmDocumentRestr);
			}
		}
	}
	
	/**
	 * delete document restriction
	 * @param restrictionType
	 */
	public void deleteRestrictions(int restrictionType){
		log.info("deleteRestrictions");
		if(restrictionType == RESTR_APP){
			if(this.getManagedBean().getDeleteAppTypeRestrictions()!=null && !this.getManagedBean().getDeleteAppTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getAppTypeRestrictions()==null){
					this.getManagedBean().setAppTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getDeleteAppTypeRestrictions()){
					this.getManagedBean().getAppTypeRestrictions().remove(pmDocumentRestr);
				}
				this.getManagedBean().getDeleteAppTypeRestrictions().clear();

			}else{
				throw new EpException("At least one restriction must be selected.");
			}
		}else if(restrictionType == RESTR_COMP){
			if(this.getManagedBean().getDeleteComTypeRestrictions()!=null && !this.getManagedBean().getDeleteComTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getComTypeRestrictions()==null){
					this.getManagedBean().setComTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getDeleteComTypeRestrictions()){
					this.getManagedBean().getComTypeRestrictions().remove(pmDocumentRestr);
				}
				this.getManagedBean().getDeleteComTypeRestrictions().clear();

			}else{
				throw new EpException("At least one restriction must be selected.");
			}
		}else if(restrictionType == RESTR_SUPP){
			if(this.getManagedBean().getDeleteSuppTypeRestrictions()!=null && !this.getManagedBean().getDeleteSuppTypeRestrictions().isEmpty()){
				if(this.getManagedBean().getSuppTypeRestrictions()==null){
					this.getManagedBean().setSuppTypeRestrictions(new ArrayList<PmDocumentRestr>());
				}
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getDeleteSuppTypeRestrictions()){
					this.getManagedBean().getSuppTypeRestrictions().remove(pmDocumentRestr);
				}
				this.getManagedBean().getDeleteSuppTypeRestrictions().clear();

			}else{
				throw new EpException("At least one restriction must be selected.");
			}
		}else if(restrictionType == RESTR_CATEGORY){
			if(this.getManagedBean().getDeleteCategoryRestrictions()!=null && !this.getManagedBean().getDeleteCategoryRestrictions().isEmpty()){
				if(this.getManagedBean().getCategoryRestrictions()==null){
					this.getManagedBean().setCategoryRestrictions(new ArrayList<PmDocumentRestr>());
				}
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getDeleteCategoryRestrictions()){
					this.getManagedBean().getCategoryRestrictions().remove(pmDocumentRestr);
				}
				this.getManagedBean().getDeleteCategoryRestrictions().clear();

			}else{
				throw new EpException("At least one restriction must be selected.");
			}
		}else if(restrictionType == CHECKLIST){
			if(this.getManagedBean().getDeleteCheckListRestrictions()!=null && !this.getManagedBean().getDeleteCheckListRestrictions().isEmpty()){
				if(this.getManagedBean().getCheckListRestrictions()==null){
					this.getManagedBean().setCheckListRestrictions(new ArrayList<PmDocumentRestr>());
				}
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getDeleteCheckListRestrictions()){
					this.getManagedBean().getCheckListRestrictions().remove(pmDocumentRestr);
				}
				this.getManagedBean().getDeleteCheckListRestrictions().clear();

			}else{
				throw new EpException("At least one restriction must be selected.");
			}
		}
	}

	/**
	 * Add selected PmCategoryL3 to categoryRestrictions
	 * @param pmCategoryL3s
	 */
	public void updateCategoryL3(List<PmCategoryL3>pmCategoryL3s){
		log.info("updateCategoryL3");
		if(pmCategoryL3s!=null){
			if(this.getManagedBean().getCategoryRestrictions()==null){
				this.getManagedBean().setCategoryRestrictions(new ArrayList<PmDocumentRestr>());
			}
			if(isUniqueCategoryL3()){
				for(PmCategoryL3 pmCategoryL3: pmCategoryL3s){
					PmDocumentRestr categoryRestr = new PmDocumentRestr();
					categoryRestr.setPmCategoryL3(retrieveCategoryL3DtlById(pmCategoryL3.getCategoryL3Id()));
					this.getManagedBean().getCategoryRestrictions().add(categoryRestr);
				}
				this.getManagedBean().getSelectedCategoryL3s().clear();
			}else{
				throw new EpException("Duplicate CategoryL3s are selected.");
			}
		}
	}
	
	/**
	 * Validate if selected category are not duplicated
	 * @return boolean
	 */
	private boolean isUniqueCategoryL3(){
		log.info("isUniqueCategoryL3");
		boolean isUnique = true;
		if(this.getManagedBean().getSelectedCategoryL3s()!=null 
				&& !this.getManagedBean().getSelectedCategoryL3s().isEmpty()){
			if(this.getManagedBean().getCategoryRestrictions()!=null && !this.getManagedBean().getCategoryRestrictions().isEmpty()){
				List <Long> categoryL3Id = new ArrayList<Long>();
				
				for(PmDocumentRestr pmDocumentRestr: this.getManagedBean().getCategoryRestrictions()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()){
						categoryL3Id.add(pmDocumentRestr.getPmCategoryL3().getCategoryL3Id());
					}
				}	
			
				for(PmCategoryL3 pmCategoryL3: this.getManagedBean().getSelectedCategoryL3s()){
					if(categoryL3Id.contains(pmCategoryL3.getCategoryL3Id())){
						isUnique = false;
						break;
					}
				}
			}
		}
		return isUnique;
	}
	
	

	/**
	 * retrieve document object by id
	 * @param documentId
	 * @return PmDocument
	 */
	public PmDocument retrieveDocumentById(Long docId) {
		log.info("retrieveDocumentById - Start");
		PmDocument pmDocument = new PmDocument();
		try {
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			reqDTO.setDocumentId(docId);
			IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentResDTO resDTO = reqSO.retrieveDocumentDtlById(reqDTO);
			if(resDTO.isSuccessful()){
				pmDocument = resDTO.getDocument();
			}
				
		} catch (Exception e) {
			throw new EpException("Unable to retrieve document: ", new String[]{e.getMessage()}); //TODO
		}
		log.info("retrieveDocumentById - End");
		return pmDocument;
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
	 * retrieve doc definition type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] docDefinitionOptions() {
		log.info("docDefinitionOptions");
		return getDropdownValues(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
	}
	
	/**
	 * retrieve procurement type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] procurementTypeOptions() {
		log.info("procurementTypeOptions");
		return getDropdownValues(ParameterTypeConst.PROCUREMENT_CATEGORY);
	}
	


	/**
	 * retrieve supplier action
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] supplierActionOptions() {
		log.info("supplierActionOptions");
		return getDropdownValues(ParameterTypeConst.SC_SUPPLIER_ACTION);
	}
	
	
	/**
	 * retrieve supplier zone
	 * 
	 * @return List<PmZone>
	 */
	public List<PmZone> supplierZoneOptions() {
		log.info("supplierZoneOptions");
		List<PmZone> zoneList = new ArrayList<PmZone>();
		try{
			IZoneSO reqSO = (IZoneSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);	
			ZoneResDTO resDTO = reqSO.retrieveZoneList();
			if(resDTO.isSuccessful()){
				zoneList = resDTO.getZoneList();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to retrieve zone list."); //TODO
		}
		return zoneList;
	}
	
	
	/**
	 * Get only applicable doc category (Technical Check List, Financial Check List, Supplier Registration)
	 * @return
	 */
	private List<PmParameterDesc>  getDocumentCategoryFilteredOptions(){
		log.info("getDocumentCategoryFilteredOptions");
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		bean.setSearchField("pmParameter.parameterCode");
		bean.setSearchValue(new ArrayList(Arrays.asList(ProfileManagementConst.SUPPORT_DOC_CATEGORY)));
		searchBean.getDataFilterBeans().add(bean);
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.DOCUMENT_CATEGORY, searchBean);
	}
	
	/**
	 * retrieve document category
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] documentCategoryOptions() {
		log.info("documentCategoryOptions");
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = getDocumentCategoryFilteredOptions();
			
		    items = new SelectItem[parameterDescs.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterId(), 
		    			parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null, 
					new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							e.toString(),
							e.getMessage()));
		}
		
	    return items;
	}
	
	/**
	 * retrieve categoryL3 details
	 * @param id
	 * @return PmCategoryL3
	 */
	public PmCategoryL3 retrieveCategoryL3DtlById(Long id){
		log.info("supplierZoneOptions");
		PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
		try{
			ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3DtlById(reqDTO);
			if(resDTO.isSuccessful()){
				pmCategoryL3 =resDTO.getCategoryL3();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to retrieve pmCategoryL3."); //TODO
		}
		return pmCategoryL3;
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
	 * get YES & NO options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}
	
	/**
	 * validate selected restriction
	 * @param restrictList
	 * @param restrictList
	 * @param restrictType
	 */
	private void validateRestriction(PmDocument pmDocument, List<PmDocumentRestr> restrictList, int restrictType){
		log.info("validateRestriction");
		if(restrictList!=null){
			boolean isSelected = true;
			if(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(pmDocument.getDocCategoryCode().getParameterCode())){
				//Validation for mandatory field
				if(pmDocument.isByAppType()){
					if(restrictList.size()>1 
							&& restrictList.get(
									restrictList.size()-1).getAppTypeCode().getParameterId()==null){
						isSelected = false;
					}
				}else if(pmDocument.isByCompType()){
					if(restrictList.size()>1 
							&& restrictList.get(
									restrictList.size()-1).getCompanyTypeCode().getParameterId()==null){
						isSelected = false;
					}
				}else if(pmDocument.isBySupplierType()){
					if(restrictList.size()>1 
							&& restrictList.get(
									restrictList.size()-1).getSupplierTypeCode().getParameterId()==null){
						isSelected = false;
					}
				}else if(pmDocument.isByCategory()){
					if(restrictList.size()>1 
							&& restrictList.get(
									restrictList.size()-1).getPmCategoryL3().getCategoryL3Id()==null){
						isSelected = false;
					}
				}
			}else{
				if(restrictList.size()>1 
						&& restrictList.get(
								restrictList.size()-1).getProcurementCatCode().getParameterId()==null){
					isSelected = false;
				}
			}
			//throw validation message accordingly
			if(!isSelected){
				if(RESTR_APP == restrictType){
					throw new EpException(" Please select Application Type.");
				}else if(RESTR_COMP == restrictType){
					throw new EpException(" Please select Company Type.");
				}else if(RESTR_SUPP == restrictType){
					throw new EpException(" Please select Supplier Type.");
				}else if(RESTR_CATEGORY == restrictType){
					throw new EpException(" Please select Category Type.");
				}else if(CHECKLIST == restrictType){
					throw new EpException(" Please select Procurement Type.");
				}
			}
		}		
	}

	/**
	 * validate unique restr type
	 * @param pmParam
	 * @param id
	 * @param restrictType
	 */
	public boolean validateUniqueRestrType(int restrictType){
		log.info("validateUniqueRestrType");
		boolean isUnique = true;
		String errorMesg = null;
		List <PmDocumentRestr> pmDocumentRestrs = null; 
		if(RESTR_APP == restrictType){
			pmDocumentRestrs = this.getManagedBean().getAppTypeRestrictions();
			errorMesg = "Duplicate Application Type being added to the list.";
		}else if(RESTR_COMP == restrictType){
			pmDocumentRestrs = this.getManagedBean().getComTypeRestrictions();
			errorMesg = "Duplicate Company Type being added to the list.";
		}else if(RESTR_SUPP == restrictType){
			pmDocumentRestrs = this.getManagedBean().getSuppTypeRestrictions();
			errorMesg = "Duplicate Supplier Type being added to the list.";
		}else if(RESTR_CATEGORY == restrictType){
			pmDocumentRestrs = this.getManagedBean().getCategoryRestrictions();
			errorMesg = "Duplicate Category Type being added to the list.";
		}else if(CHECKLIST == restrictType){
			pmDocumentRestrs = this.getManagedBean().getCheckListRestrictions();
			errorMesg = "Duplicate Procurement Type being added to the list.";
		}

		//Validation for unique restriction
		if(pmDocumentRestrs!=null && pmDocumentRestrs.size()>1){	
			List <Long> tempRestrictIdList = new ArrayList<Long>();
			for(PmDocumentRestr pmDocumentRestr :pmDocumentRestrs){
				if(DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()){
					if(RESTR_APP == restrictType){
						tempRestrictIdList.add(pmDocumentRestr.getAppTypeCode().getParameterId());
					}else if(RESTR_COMP == restrictType){
						tempRestrictIdList.add(pmDocumentRestr.getCompanyTypeCode().getParameterId());
					}else if(RESTR_SUPP == restrictType){
						tempRestrictIdList.add(pmDocumentRestr.getSupplierTypeCode().getParameterId());
					}else if(RESTR_CATEGORY == restrictType){
						tempRestrictIdList.add(pmDocumentRestr.getPmCategoryL3().getCategoryL3Id());
					}else if(CHECKLIST == restrictType){
						tempRestrictIdList.add(pmDocumentRestr.getProcurementCatCode().getParameterId());
					}
				}
			}
		
			Set<Long> tempRestrictIdSet = new HashSet<Long>(tempRestrictIdList);
			if(!tempRestrictIdList.isEmpty() && (tempRestrictIdSet.size()!=pmDocumentRestrs.size())){
				isUnique = false;
				throw new EpException(errorMesg);
			}
			
		}
		return isUnique;
	}

	/**
	 * add restr to document
	 * @param pmDocumentRestrs
	 */
	private void addDocRestrToDocument(PmDocument pmDocument, List <PmDocumentRestr> pmDocumentRestrs){
		log.info("addDocRestrToDocument");
		if(pmDocumentRestrs!=null && pmDocumentRestrs.size()>0){
			for(PmDocumentRestr documentRestr:pmDocumentRestrs){
				if(documentRestr.isBrandNew()){
					if(!documentRestr.isSelected()){
						if(pmDocument.getPmDocumentRestrs()==null){
							pmDocument.setPmDocumentRestrs(new ArrayList<PmDocumentRestr>());
						}
						pmDocument.addPmDocumentRestrs(documentRestr);
						documentRestr.setSelected(true);
					}
				}else{
					if(!documentRestr.isSelected()){
						for (int i=0; i <pmDocument.getPmDocumentRestrs().size(); i++){
							if(pmDocument.getPmDocumentRestrs().get(i).getDocumentRestrId()
									.equals(documentRestr.getDocumentRestrId())){
								pmDocument.getPmDocumentRestrs().set(i, documentRestr);
							}
						}
						documentRestr.setSelected(true);
					}
				}
			}
		}
	}
	/**
	 * validate all restrictions
	 * @param pmDocument
	 */
	private void validateRestrictions(PmDocument pmDocument){
		log.info("validateRestrictions");
		
		//validate required field
		validateRestriction(pmDocument, this.getManagedBean().getAppTypeRestrictions(), RESTR_APP);
		validateRestriction(pmDocument, this.getManagedBean().getComTypeRestrictions(), RESTR_COMP);
		validateRestriction(pmDocument, this.getManagedBean().getSuppTypeRestrictions(), RESTR_SUPP);
		validateRestriction(pmDocument, this.getManagedBean().getCategoryRestrictions(), RESTR_CATEGORY);
		validateRestriction(pmDocument, this.getManagedBean().getCheckListRestrictions(), CHECKLIST);
		
		//validate unique field
		validateUniqueRestrType(RESTR_APP);
		validateUniqueRestrType(RESTR_COMP);
		validateUniqueRestrType(RESTR_SUPP);
		validateUniqueRestrType(RESTR_CATEGORY);
		validateUniqueRestrType(CHECKLIST);
	}
	
	private void getAllRestrictions(PmDocument pmDocument){
		log.info("getAllRestrictions");
		try{
	
			//add doc restr to document
			addDocRestrToDocument(pmDocument, this.getManagedBean().getAppTypeRestrictions());
			addDocRestrToDocument(pmDocument, this.getManagedBean().getComTypeRestrictions());
			addDocRestrToDocument(pmDocument, this.getManagedBean().getSuppTypeRestrictions());
			addDocRestrToDocument(pmDocument, this.getManagedBean().getCategoryRestrictions());
			addDocRestrToDocument(pmDocument, this.getManagedBean().getCheckListRestrictions());
			if(pmDocument.getPmDocumentRestrs()!=null){
				System.out.println("PmDocumentRestrs>>>" + pmDocument.getPmDocumentRestrs().size());
			}else{
				System.out.println("PmDocumentRestrs>>>>>null");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770001, new String[]{e.getMessage()});

		}
	}
	
	
	
	/**
	 * save to Supporting Document
	 * @return xhtml
	 */
	public void saveSupportDoc(){
		log.info("saveSupportDoc - Start");

		DocumentReqDTO reqDTO = null;
		DocumentResDTO resDTO = null;
		IDocumentSO reqSO = null;
		try{
			PmDocument pmDocument = this.getManagedBean().getSelectedDocument();
			validateRestrictions(pmDocument);
			getAllRestrictions(pmDocument);
			
			reqDTO = new DocumentReqDTO();
			reqDTO.setPmDocument(pmDocument);
			reqSO = (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);	
			resDTO = reqSO.saveDocument(reqDTO);
			if(resDTO.isSuccessful()){
				loadSupportDoc(resDTO.getDocument().getDocumentId());
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(
						    FacesMessage.SEVERITY_INFO,
						    null,
				    		MessageUtils.getMessage(MessageConst.INFO_770049, new String[]{resDTO.getDocument().getDocName()},  //TODO
					    		MessageUtils.BUNDLE_COMMON_MESSAGES, 
					    		new FacesScopes().getUserLocale())
				    		));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			reqSO = null;
		}
		
		log.info("saveSupportDoc - End");
		
	
	}

	
	/**
	 * Below codes are for document search
	 */
	
	/**
	 * search document
	 */
	public void searchDocument(){
		log.info("searchDocument - Start");
		pmDocumentDataModel.reloadData();
		log.info("searchDocument - End");
		
	}
	
	
	/**
	 * get search bean 
	 * @return ISearchBean<PmDocument>
	 */
	private ISearchBean<PmDocument> getSearchBeanData(ISearchBean<PmDocument> searchBean){
		log.info("getSearchBeanData - Start");

		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		SupportDocumentSearchBean sdSearchBean = this.getManagedBean().getSupportDocSearchBean();
			
		// Doc Category
		if(sdSearchBean.getDocCategoryParam()!=null && sdSearchBean.getDocCategoryParam()>0L){
			log.info("+ docCategoryParam "  +sdSearchBean.getDocCategoryParam());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("docCategoryCode.parameterId");
			bean.setSearchValue(sdSearchBean.getDocCategoryParam());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		// App Type
		if(sdSearchBean.getAppTypeParam()!=null && sdSearchBean.getAppTypeParam()>0L){
			log.info("+ appTypeParam "  +sdSearchBean.getAppTypeParam());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmDocumentRestrs.appTypeCode.parameterId");
			bean.setSearchValue(sdSearchBean.getAppTypeParam());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		// Comp Type
		if(sdSearchBean.getComTypeParam()!=null && sdSearchBean.getComTypeParam()>0L){
			log.info("+ comTypeParam "  +sdSearchBean.getComTypeParam());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmDocumentRestrs.companyTypeCode.parameterId");
			bean.setSearchValue(sdSearchBean.getComTypeParam());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		// Supp Type
		if(sdSearchBean.getSuppTypeParam()!=null && sdSearchBean.getSuppTypeParam()>0L){
			log.info("+ suppTypeParam "  +sdSearchBean.getSuppTypeParam());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmDocumentRestrs.supplierTypeCode.parameterId");
			bean.setSearchValue(sdSearchBean.getSuppTypeParam());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		//Document Name
		if (sdSearchBean.getDocName() != null && !"".equalsIgnoreCase(sdSearchBean.getDocName())) {
			log.info("+ docName "  +sdSearchBean.getDocName());
			SearchFilterBean bean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			bean.setCaseInsensitive(true);
			bean.setSearchField("docName");
			bean.setSearchValue(sdSearchBean.getDocName());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		//Business Category
		if (sdSearchBean.getBusinessCategory() != null && !"".equalsIgnoreCase(sdSearchBean.getBusinessCategory())) {
			log.info("+ businessCategory "  +sdSearchBean.getBusinessCategory());
			SearchFilterBean bean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			bean.setCaseInsensitive(true);
			bean.setSearchField("pmDocumentRestrs.pmCategoryL3.categoryName");
			bean.setSearchValue(sdSearchBean.getBusinessCategory());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
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
	 * @return the pmDocumentDataModel
	 */
	public IPagingDataModel<PmDocument> getPmDocumentDataModel() {
		return pmDocumentDataModel;
	}

	/**
	 * @param pmDocumentDataModel the pmDocumentDataModel to set
	 */
	public void setPmDocumentDataModel(
			IPagingDataModel<PmDocument> pmDocumentDataModel) {
		this.pmDocumentDataModel = pmDocumentDataModel;
	}
	/**
	 * paginate for document list
	 * @author RainPoh
	 *
	 */
	private class PmDocumentDataModel extends PagingDataModel<PmDocument> {
		private static final long serialVersionUID = 1L;

		public PmDocumentDataModel() {
			super("dtSupportDocResult");
		}
		/**
		 * @author rainpoh
		 * @since 05.09.2014
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmDocument> pageChangeListener(ISearchBean<PmDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			try {	
				ISearchBean<PmDocument> searchBeanData = getSearchBeanData(searchBean);
				DocumentReqDTO reqDTO = new DocumentReqDTO();
				reqDTO.setDocumentSearchBean(searchBeanData);
				IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);	
				DocumentResDTO resDTO = reqSO.retrieveDocumentBySearchBean(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getDocumentResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
//				throw new EpException(MessageConst.ERROR_770005); //TODO
			}

			return null;
		}
	}
	
	/**
	 * End
	 */
	
	
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
	 * @return the restrCategory
	 */
	public int getRestrCategory() {
		return RESTR_CATEGORY;
	}

	/**
	 * @return the checklist
	 */
	public int getChecklist() {
		return CHECKLIST;
	}
	

}
