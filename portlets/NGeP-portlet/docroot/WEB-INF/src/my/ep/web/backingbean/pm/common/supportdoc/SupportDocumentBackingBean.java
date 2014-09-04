package my.ep.web.backingbean.pm.common.supportdoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

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
	public static final int RESTR_APP = 0;
	public static final int RESTR_COMP = 1;
	public static final int RESTR_SUPP = 2;
	public static final int RESTR_CATEGORY = 3;
	
	
	//Mapper for DDL object
	private static Map<Long, Object> docCategoryMapper;
	
	public void preLoading(){
		log.info("preLoading");
		this.resetSupportDocSearchBean();
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
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(ParameterTypeConst.DOCUMENT_CATEGORY, null);
		
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
			if(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST.equals(pmParameter.getParameterCode()) 
					|| ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST.equals(pmParameter.getParameterCode())){
				this.getManagedBean().setCheckList(true);
			}else{
				this.getManagedBean().setCheckList(false);
			}
		}else{
			this.getManagedBean().setCheckList(false);
		}
	}
	
	/**
	 * initial new supporting document object
	 */
	public void initialSupportDoc(){
		log.info("initialSupportDoc - Start");
		PmDocument pmDocument = new PmDocument();
		pmDocument.setDocCategoryCode(new PmParameter());
		pmDocument.setPmZone(new PmZone());
		pmDocument.setDocDefinitionTypeCode(new PmParameter());
		this.getManagedBean().setAppTypeRestrictions(null);
		this.getManagedBean().setComTypeRestrictions(null);
		this.getManagedBean().setSuppTypeRestrictions(null);
		this.getManagedBean().setCategoryRestrictions(null);
		this.getManagedBean().setSelectedDocument(pmDocument);
		this.getManagedBean().setCheckList(false);
		log.info("initialSupportDoc - End");
	}
	
	/**
	 * load supporting document object by id
	 */
	public void loadSupportDoc(Long id){
		log.info("loadSupportDoc - Start");
		log.info("loadSupportDoc - End");
	}
	
	/**
	 * load the pm document to tab view by restriction type
	 * @param pmDocument
	 */
	public void loadRestriction(PmDocument pmDocument){
		log.info("loadRestriction - Start");
		if(pmDocument!=null){
			List <PmDocumentRestr> appTypeRestrictions = new ArrayList<PmDocumentRestr>();
			List <PmDocumentRestr> comTypeRestrictions = new ArrayList<PmDocumentRestr>();
			List <PmDocumentRestr> suppTypeRestrictions = new ArrayList<PmDocumentRestr>();
			List <PmDocumentRestr> categoryRestrictions = new ArrayList<PmDocumentRestr>();
			for(PmDocumentRestr pmDocumentRestr:pmDocument.getPmDocumentRestrs()){
				if(pmDocument.isByAppType()){
					appTypeRestrictions.add(pmDocumentRestr);
				}else if(pmDocument.isByCompType()){
					comTypeRestrictions.add(pmDocumentRestr);
				}else if(pmDocument.isBySupplierType()){
					suppTypeRestrictions.add(pmDocumentRestr);
				}else if(pmDocument.isByCategory()){
					categoryRestrictions.add(pmDocumentRestr);
				}
			}
			this.getManagedBean().setAppTypeRestrictions(appTypeRestrictions);
			this.getManagedBean().setComTypeRestrictions(comTypeRestrictions);
			this.getManagedBean().setSuppTypeRestrictions(suppTypeRestrictions);
			this.getManagedBean().setCategoryRestrictions(categoryRestrictions);
		}
		log.info("loadRestriction - End");
	}
	
	/**
	 * add new document restriction
	 * @param restrictionType
	 */
	public void addRestriction(int restrictionType){
		log.info("addRestriction");
		log.info("restrictionType = " + restrictionType);
		
	}
	
	/**
	 * delete document restriction
	 * @param restrictionType
	 */
	public void deleteRestrictions(int restrictionType){
		log.info("deleteRestrictions");
	}

	/**
	 * search document
	 */
	public void searchDocument(){
		log.info("searchDocument - Start");
//		pmDocumentDataMode.reloadData();
		log.info("searchDocument - End");
		
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
	 * retrieve document category
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] documentCategoryOptions() {
		log.info("documentCategoryOptions");
		return getDropdownValues(ParameterTypeConst.DOCUMENT_CATEGORY);
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
		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(1, "Yes");
		items[1] = new SelectItem(0, "No");
	    return items;
	}
	

}
