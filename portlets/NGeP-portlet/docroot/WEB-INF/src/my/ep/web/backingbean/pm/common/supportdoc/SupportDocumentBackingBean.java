/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.backingbean.pm.common.supportdoc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.SupportDocumentSearchBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Backing Bean for Support Document
 * 
 * @author RAIN POH
 * @since 8/7/2013
 * 
 */
@ManagedBean(name = "supportDocBackingBean")
@ViewScoped
public class SupportDocumentBackingBean extends
		BaseBackingBean<SupportDocumentManagedBean> {
	private static Logger log = Logger
			.getLogger(SupportDocumentBackingBean.class);
	// Navigation Details
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/supportingdocument/supportingDocumentMaintain.xhtml";
	public static final String NAV_TO_SEARCH = "supportingDocument";
	public static final String NAV_TO_MAINTAIN_SUP = "supportingDocumentMaintain";
	public static final String NAV_TO_SEARCH_SUP = "/xhtml/pm/supplierDocument/supplierDocument.xhtml";

	// Document Restriction Type
	public static final int RESTR_APP = 1;
	public static final int RESTR_COMP = 2;
	public static final int RESTR_SUPP = 3;
	public static final int RESTR_CATEGORY = 4;
	public static final int RESTR_CERTBODY = 5;
	public static final int CHECKLIST = 6;

	// Mapper for DDL object
	private static Map<Long, Object> docCategoryMapper;

	// List for Paging
	private IPagingDataModel<PmDocument> pmDocumentDataModel = new PmDocumentDataModel();

	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	private static final String LABEL_dISPLAY_PRIORITY_KEY = "lbl.pm.display.priority";

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void preLoading() {
		log.info("preLoading");
		this.resetSupportDocSearchBean();
		this.getManagedBean().setSelectedTabIndex(0);		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	public void preLoadingSupplierDoc() {
		log.info("preLoading");
		this.resetSupportDocSearchBean();
		this.getManagedBean().setSelectedTabIndex(0);		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH_SUP);
	}

	/**
	 * reset search bean fields
	 */
	public void resetSupportDocSearchBean() {
		log.info("resetSupportDocSearchBean");
		this.getManagedBean().setSupportDocSearchBean(
				new SupportDocumentSearchBean());
		pmDocumentDataModel = new PmDocumentDataModel();
	}

	/**
	 * redirect to Support Doc Details
	 * 
	 * @param id
	 * @return xhtml
	 */
	public String redirectToMaintainDoc(Long id) {
		log.info("redirectToMaintainDoc - Start");
		try {
			this.retrieveDocCategoryMap();
			if (id != null && id > 0) {
				loadSupportDoc(id);
			} else {
				initialSupportDoc();
			}
			docDefinitionOptions();
			initialSupplierActionItems();
		} catch (Exception e) {
			e.printStackTrace();

		}
		log.info("redirectToMaintainDoc - End");
		return NAV_TO_MAINTAIN;
	}

	public void fillSupplierDocDependencies(){
		log.info("fillSupplierDocDependencies - start");
		this.getManagedBean().setCheckList(true);
		// load default value for checklist
		
		if (this.getManagedBean().getSelectedDocument().isBrandNew()) {

			if (this.getManagedBean().getSelectedDocument()
					.getDocDefinitionTypeCode()!=null && this.getManagedBean().getSelectedDocument()
					.getDocDefinitionTypeCode().getParameterId() != null) {
				this.getManagedBean().setSupplierActionItems(
						initialSupplierActionItems());
			}else{
				this.getManagedBean().getSelectedDocument()
				.setChecklistActionCode(new PmParameter());
			}
			
			this.getManagedBean().getSelectedDocument()
					.setDocDefinitionTypeCode(new PmParameter());			
			
		}			
		if (this.getManagedBean().getSelectedDocument().isBrandNew()) {
			this.getManagedBean().getSelectedDocument().setPmZone(null);
		}  
		
		
	
	}
	
	
	
	
	/**
	 * redirect to Support Doc Details
	 * 
	 * @param id
	 * @return xhtml
	 */
	public String redirectToMaintainDocForSupp(Long id) {
		log.info("redirectToMaintainDocForSupp - Start");
		try {
			this.retrieveDocCategoryMap();
			if (id != null && id > 0) {
				loadSupportDoc(id);
			} else {
				initialSupportDoc();
				this.getManagedBean().getSelectedDocument().setDocCategoryCode(this.getManagedBean().getSupplierDocParameter());
				docCategoryChange();
			}
			docDefinitionOptions();
			initialSupplierActionItems();
		} catch (Exception e) {
			e.printStackTrace();

		}
		log.info("redirectToMaintainDoc - End");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * redirect to Support Doc Details
	 * 
	 * @param id
	 * @return xhtml
	 */
	public String reloadEditData(Long id) {
		log.info("redirectToMaintainDoc - Start");
		try {
			this.retrieveDocCategoryMap();
			if (id != null && id > 0) {
				loadSupportDoc(id);
			} else {
				initialSupportDoc();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		log.info("redirectToMaintainDoc - End");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * retrieve all document category into mapper
	 */
	private void retrieveDocCategoryMap() {
		log.info("retrieveDocCategoryMap");
//		List<PmParameterDesc> parameterDescs = getDocumentCategoryFilteredOptions();
		List<PmParameterDesc> parameterDescs =getDocumentCategoryFilteredOptions();
		docCategoryMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			docCategoryMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
			
			if(pmDesc.getPmParameter().getParameterCode().equals(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT) && pmDesc.getPmParameter().getParameterType().equals(ParameterTypeConst.DOCUMENT_CATEGORY)){
				this.getManagedBean().setSupplierDocParameter(pmDesc.getPmParameter());
			}
		}
		
	}

	/**
	 * reset doc search fields
	 */
	public void docCategorySearchChange() {
		log.info("docCategorySearchChange");
		if (this.getManagedBean().getSupportDocSearchBean() != null) {
			if (ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(this
					.getManagedBean().getSupportDocSearchBean()
					.getDocCategoryParamCode())) {
				this.getManagedBean().getSupportDocSearchBean()
						.setProcureCatParam(null);
			} else if (ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST
					.equals(this.getManagedBean().getSupportDocSearchBean()
							.getDocCategoryParamCode())
					|| ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST
							.equals(this.getManagedBean()
									.getSupportDocSearchBean()
									.getDocCategoryParamCode())) {
				this.getManagedBean().getSupportDocSearchBean()
						.setAppTypeParam(null);
				this.getManagedBean().getSupportDocSearchBean()
						.setBusinessCategory(null);
				this.getManagedBean().getSupportDocSearchBean()
						.setComTypeParam(null);
				this.getManagedBean().getSupportDocSearchBean().setPmZone(null);
				this.getManagedBean().getSupportDocSearchBean()
						.setSuppTypeParam(null);
			}
		}
	}

	/**
	 * set document category
	 */
	public void docCategoryChange() {
		log.info("docCategoryChange");
		log.info(".........this.getManagedBean().getSelectedDocument().getDocCategoryCode()="+this.getManagedBean().getSelectedDocument().getDocCategoryCode());
		if (this.getManagedBean().getSelectedDocument().getDocCategoryCode()
				.getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) docCategoryMapper.get(this
					.getManagedBean().getSelectedDocument()
					.getDocCategoryCode().getParameterId());
			log.info("......pmParameter="+pmParameter);
			this.getManagedBean().getSelectedDocument().getDocCategoryCode()
					.setParameterCode(pmParameter.getParameterCode());
			getManagedBean().getSelectedDocument().getDocCategoryCode()
					.setParameterType(pmParameter.getParameterType());
			if (!ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
					.equals(pmParameter.getParameterCode())) {
				this.getManagedBean().setCheckList(true);
				// load default value for checklist
				
				if (this.getManagedBean().getSelectedDocument().isBrandNew()) {

					if (this.getManagedBean().getSelectedDocument()
							.getDocDefinitionTypeCode()!=null && this.getManagedBean().getSelectedDocument()
							.getDocDefinitionTypeCode().getParameterId() != null) {
						this.getManagedBean().setSupplierActionItems(
								initialSupplierActionItems());
					}else{
						this.getManagedBean().getSelectedDocument()
						.setChecklistActionCode(new PmParameter());
					}
					
					this.getManagedBean().getSelectedDocument()
							.setDocDefinitionTypeCode(new PmParameter());			
					
				}
				
				/*if (this.getManagedBean().getSelectedDocument().isBrandNew()
						&& this.getManagedBean().getSelectedDocument()
								.getDocDefinitionTypeCode() == null) {
					if (this.getManagedBean().getSelectedDocument()
							.getDocDefinitionTypeCode() == null) {
						this.getManagedBean().getSelectedDocument()
								.setDocDefinitionTypeCode(new PmParameter());

					}
					
					
					 * PmParameter tempParam = PmCommonUtil.retrievePmParameter(
					 * ParameterTypeConst.DOCUMENT_DEFINITION_TYPE,
					 * ProfileManagementConst.DEFAULT_DOC_DEFINITION);
					 * this.getManagedBean().getSelectedDocument()
					 * .getDocDefinitionTypeCode()
					 * .setParameterId(tempParam.getParameterId());
					 
				}*/
				/*if (this.getManagedBean().getSelectedDocument().isBrandNew()
						&& this.getManagedBean().getSelectedDocument()
								.getChecklistActionCode() == null) {
					if (this.getManagedBean().getSelectedDocument()
							.getChecklistActionCode() == null) {
						this.getManagedBean().getSelectedDocument()
								.setChecklistActionCode(new PmParameter());
					}

					
					 * PmParameter tempParam = PmCommonUtil.retrievePmParameter(
					 * ParameterTypeConst.SC_SUPPLIER_ACTION,
					 * ProfileManagementConst.DEFAULT_SUPPLIER_ACTION);
					 * this.getManagedBean().getSelectedDocument()
					 * .getChecklistActionCode()
					 * .setParameterId(tempParam.getParameterId());
					 
				}*/				
	             
				if (this.getManagedBean().getSelectedDocument().isBrandNew()) {
					this.getManagedBean().getSelectedDocument().setPmZone(null);
				}
				
			} else {
				if (this.getManagedBean().getSelectedDocument().isBrandNew()) {
					this.getManagedBean().setCheckList(false);
					this.getManagedBean().getSelectedDocument()
							.setPmZone(new PmZone());
					this.getManagedBean().getSelectedDocument().getPmZone()
							.setZoneId(0L); // if zone selected is All.
					this.getManagedBean().getSelectedDocument()
							.setDocDefinitionTypeCode(null);
					this.getManagedBean().getSelectedDocument()
							.setChecklistActionCode(null);
				}
   
			}
		} else {
			if (this.getManagedBean().getSelectedDocument().isBrandNew()) {
				this.getManagedBean().setCheckList(false);
			}
		}

	}

	/**
	 * initial new supporting document object
	 */
	public void initialSupportDoc() {
		log.info("initialSupportDoc - Start");
		PmDocument pmDocument = new PmDocument();
		pmDocument.setDocCategoryCode(new PmParameter()); // checklist, supp doc
		pmDocument.setPmZone(new PmZone()); // supp doc
		pmDocument.getPmZone().setZoneId(0L); // if zone selected is All.
		pmDocument.setDocDefinitionTypeCode(null); // checklist
		pmDocument.setChecklistActionCode(null); // checklist
		this.getManagedBean().setAppTypeRestrictions(null);
		this.getManagedBean().setComTypeRestrictions(null);
		this.getManagedBean().setSuppTypeRestrictions(null);
		this.getManagedBean().setCategoryRestrictions(null);
		this.getManagedBean().setCheckListRestrictions(null);
		this.getManagedBean().setCertBodyRestrictions(null);
		this.getManagedBean().setDeleteAppTypeRestrictions(null);
		this.getManagedBean().setDeleteComTypeRestrictions(null);
		this.getManagedBean().setDeleteSuppTypeRestrictions(null);
		this.getManagedBean().setDeleteCategoryRestrictions(null);
		this.getManagedBean().setDeleteCertBodyRestrictions(null);
		this.getManagedBean().setDeleteCheckListRestrictions(null);
		this.getManagedBean().setDeleteRestrictions(null);
		this.getManagedBean().setSelectedDocument(pmDocument);
		this.getManagedBean().setCheckList(false);
		this.getManagedBean().setSelectedTabIndex(0);
		this.getManagedBean().setSupplierActionItems(null);
		
		log.info("initialSupportDoc - End");
	}

	/**
	 * load supporting document object by id
	 */
	public void loadSupportDoc(Long id) {
		log.info("loadSupportDoc - Start");
		try {
			initialSupportDoc();
			PmDocument pmDocument = retrieveDocumentById(id);
			log.info("......pmDocument.getDocCategoryCode().getParameterCode()="+pmDocument.getDocCategoryCode().getParameterCode());
			if (pmDocument.getDocCategoryCode() == null) {
				pmDocument.setDocCategoryCode(new PmParameter());
			}
			this.getManagedBean().setSelectedDocument(pmDocument);
			loadRestriction(pmDocument);
			docCategoryChange();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770062,
					new String[] { e.getMessage() });

		}
		log.info("loadSupportDoc - End");
	}

	/**
	 * set default tab
	 * 
	 * @param pmDocument
	 */
	public void defaultTab(PmDocument pmDocument) {
		log.info("defaultTab");
		if (pmDocument.isByAppType()) {
			this.getManagedBean().setSelectedTabIndex(RESTR_APP - 1);
		} else if (pmDocument.isByCompType()) {
			this.getManagedBean().setSelectedTabIndex(RESTR_COMP - 1);
		} else if (pmDocument.isBySupplierType()) {
			this.getManagedBean().setSelectedTabIndex(RESTR_SUPP - 1);
		} else if (pmDocument.isByCategory()) {
			this.getManagedBean().setSelectedTabIndex(RESTR_CATEGORY - 1);
		} else if (pmDocument.isByCertBody()) {
			this.getManagedBean().setSelectedTabIndex(RESTR_CERTBODY - 1);
		}
	}

	/**
	 * load the pm document to tab view by restriction type
	 * 
	 * @param pmDocument
	 */
	public void loadRestriction(PmDocument pmDocument) {
		log.info("loadRestriction - Start");
		if (pmDocument != null && pmDocument.getDocCategoryCode() != null) {
			if (ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
					.equals(pmDocument.getDocCategoryCode().getParameterCode())) {
				if (pmDocument.getPmZone() == null) {
					pmDocument.setPmZone(new PmZone());
				}

				List<PmDocumentRestr> appTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List<PmDocumentRestr> comTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List<PmDocumentRestr> suppTypeRestrictions = new ArrayList<PmDocumentRestr>();
				List<PmDocumentRestr> categoryRestrictions = new ArrayList<PmDocumentRestr>();
				List<PmDocumentRestr> certBodyRestrictions = new ArrayList<PmDocumentRestr>();
				for (PmDocumentRestr pmDocumentRestr : pmDocument
						.getPmDocumentRestrs()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr
							.getRecordStatusFlg()) {
						if (pmDocument.isByAppType()
								&& pmDocumentRestr.getAppTypeCode() != null) {
							appTypeRestrictions.add(pmDocumentRestr);
						}
						if (pmDocument.isByCompType()
								&& pmDocumentRestr.getCompanyTypeCode() != null) {
							comTypeRestrictions.add(pmDocumentRestr);
						}
						if (pmDocument.isBySupplierType()
								&& pmDocumentRestr.getSupplierTypeCode() != null) {
							suppTypeRestrictions.add(pmDocumentRestr);
						}
						if (pmDocument.isByCategory()
								&& pmDocumentRestr.getPmCategoryL3() != null) {
							pmDocumentRestr
									.setPmCategoryL3(retrieveCategoryL3DtlById(pmDocumentRestr
											.getPmCategoryL3()
											.getCategoryL3Id()));
							categoryRestrictions.add(pmDocumentRestr);
						}
						if (pmDocument.isByCertBody()
								&& pmDocumentRestr.getPmCertBody() != null) {
							certBodyRestrictions.add(pmDocumentRestr);
						}
					}
				}
				this.getManagedBean().setAppTypeRestrictions(
						appTypeRestrictions);
				this.getManagedBean().setComTypeRestrictions(
						comTypeRestrictions);
				this.getManagedBean().setSuppTypeRestrictions(
						suppTypeRestrictions);
				this.getManagedBean().setCategoryRestrictions(
						categoryRestrictions);
				this.getManagedBean().setCertBodyRestrictions(
						certBodyRestrictions);

				defaultTab(pmDocument);
			} else if (ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST
					.equals(pmDocument.getDocCategoryCode().getParameterCode())
					|| ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST
							.equals(pmDocument.getDocCategoryCode()
									.getParameterCode())) {
				if (pmDocument.getDocDefinitionTypeCode() == null) {
					pmDocument.setDocDefinitionTypeCode(new PmParameter());
				}
				if (pmDocument.getChecklistActionCode() == null) {
					pmDocument.setChecklistActionCode(new PmParameter());
				}

				List<PmDocumentRestr> checklistRestriction = new ArrayList<PmDocumentRestr>();
				for (PmDocumentRestr pmDocumentRestr : pmDocument
						.getPmDocumentRestrs()) {
					if (pmDocument.isByProcureCat()
							&& pmDocumentRestr.getProcurementCatCode() != null
							&& DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr
									.getRecordStatusFlg()) {
						checklistRestriction.add(pmDocumentRestr);
					}
				}
				this.getManagedBean().setCheckListRestrictions(
						checklistRestriction);

			}
		}
		log.info("loadRestriction - End");
	}

	/**
	 * redirect to Support Doc Search
	 * 
	 * @return xhtml
	 */
	public String redirectToSearchSupportDoc() {
		log.info("redirectToSearchSupportDoc - Start");
		try {

		} catch (Exception e) {
			e.printStackTrace();

		}
		log.info("redirectToSearchSupportDoc - End");
		
//	if(!this.getManagedBean().getSelectedDocument().isBrandNew()){
//		log.info("...........this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterCode()="+this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterCode());
//		if(this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterCode().equals(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT)){
//			return NAV_TO_SEARCH_SUP;
//		}else{
//		    return NAV_TO_SEARCH;
//		}
//	}else{//brandNew, create
		if(this.getManagedBean().getSelectedDocument().getDocCategoryCode() != null && this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterCode() != null && this.getManagedBean().getSelectedDocument().getDocCategoryCode().getParameterCode().equals(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT)){
			return NAV_TO_SEARCH_SUP;
		}else{
			 return NAV_TO_SEARCH;
		}
		
//	}

	}

	/**
	 * add new document restriction
	 * 
	 * @param restrictionType
	 */
	public void addRestriction(int restrictionType) {
		log.info("addRestriction");
		log.info("restrictionType = " + restrictionType);

		if (validateUniqueRestrType(restrictionType)) {
			PmDocumentRestr pmDocumentRestr = new PmDocumentRestr();
			if (restrictionType == RESTR_APP) {
				if (this.getManagedBean().getAppTypeRestrictions() == null) {
					this.getManagedBean().setAppTypeRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setAppTypeCode(new PmParameter());
				this.getManagedBean().getAppTypeRestrictions()
						.add(pmDocumentRestr);
			} else if (restrictionType == RESTR_COMP) {
				if (this.getManagedBean().getComTypeRestrictions() == null) {
					this.getManagedBean().setComTypeRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setCompanyTypeCode(new PmParameter());
				this.getManagedBean().getComTypeRestrictions()
						.add(pmDocumentRestr);
			} else if (restrictionType == RESTR_SUPP) {
				if (this.getManagedBean().getSuppTypeRestrictions() == null) {
					this.getManagedBean().setSuppTypeRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setSupplierTypeCode(new PmParameter());
				this.getManagedBean().getSuppTypeRestrictions()
						.add(pmDocumentRestr);
			} else if (restrictionType == RESTR_CATEGORY) {
				if (this.getManagedBean().getCategoryRestrictions() == null) {
					this.getManagedBean().setCategoryRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setPmCategoryL3(new PmCategoryL3());
				this.getManagedBean().getCategoryRestrictions()
						.add(pmDocumentRestr);
			} else if (restrictionType == CHECKLIST) {
				if (this.getManagedBean().getCheckListRestrictions() == null) {
					this.getManagedBean().setCheckListRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocumentRestr.setProcurementCatCode(new PmParameter());
				this.getManagedBean().getCheckListRestrictions()
						.add(pmDocumentRestr);
			}
		}
	}

//	/**
//	 * delete document restriction
//	 * 
//	 * @param restrictionType
//	 */
//	public void deleteRestrictions(int restrictionType) {
//		log.info("deleteRestrictions");
//		if (restrictionType == RESTR_APP) {
//			if (this.getManagedBean().getDeleteAppTypeRestrictions() != null
//					&& !this.getManagedBean().getDeleteAppTypeRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getAppTypeRestrictions() == null) {
//					this.getManagedBean().setAppTypeRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteAppTypeRestrictions()) {
//					this.getManagedBean().getAppTypeRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteAppTypeRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//		} else if (restrictionType == RESTR_COMP) {
//			if (this.getManagedBean().getDeleteComTypeRestrictions() != null
//					&& !this.getManagedBean().getDeleteComTypeRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getComTypeRestrictions() == null) {
//					this.getManagedBean().setComTypeRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteComTypeRestrictions()) {
//					this.getManagedBean().getComTypeRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteComTypeRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//		} else if (restrictionType == RESTR_SUPP) {
//			if (this.getManagedBean().getDeleteSuppTypeRestrictions() != null
//					&& !this.getManagedBean().getDeleteSuppTypeRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getSuppTypeRestrictions() == null) {
//					this.getManagedBean().setSuppTypeRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteSuppTypeRestrictions()) {
//					this.getManagedBean().getSuppTypeRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteSuppTypeRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//		} else if (restrictionType == RESTR_CATEGORY) {
//			if (this.getManagedBean().getDeleteCategoryRestrictions() != null
//					&& !this.getManagedBean().getDeleteCategoryRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getCategoryRestrictions() == null) {
//					this.getManagedBean().setCategoryRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteCategoryRestrictions()) {
//					this.getManagedBean().getCategoryRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteCategoryRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//		} else if (restrictionType == CHECKLIST) {
//			if (this.getManagedBean().getDeleteCheckListRestrictions() != null
//					&& !this.getManagedBean().getDeleteCheckListRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getCheckListRestrictions() == null) {
//					this.getManagedBean().setCheckListRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteCheckListRestrictions()) {
//					this.getManagedBean().getCheckListRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteCheckListRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//		} else if (restrictionType == RESTR_CERTBODY) {
//			if (this.getManagedBean().getDeleteCertBodyRestrictions() != null
//					&& !this.getManagedBean().getDeleteCertBodyRestrictions()
//							.isEmpty()) {
//				if (this.getManagedBean().getCertBodyRestrictions() == null) {
//					this.getManagedBean().setCertBodyRestrictions(
//							new ArrayList<PmDocumentRestr>());
//				}
//				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
//						.getDeleteCertBodyRestrictions()) {
//					this.getManagedBean().getCertBodyRestrictions()
//							.remove(pmDocumentRestr);
//				}
//				this.getManagedBean().getDeleteCertBodyRestrictions().clear();
//
//			} else {
//				throw new EpException(MessageConst.ERROR_770063);
//			}
//
//		}
//	}

	public void deleteRestrictions(int restrictionType, int rowIndex) {
		log.info("deleteRestrictions");
		PmDocumentRestr pmDocRestr = null;
		if (restrictionType == RESTR_APP) {
			pmDocRestr = getManagedBean().getAppTypeRestrictions()
					.get(rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getAppTypeRestrictions().remove(pmDocRestr);
		} else if (restrictionType == RESTR_COMP) {
			pmDocRestr = getManagedBean().getComTypeRestrictions()
					.get(rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getComTypeRestrictions().remove(pmDocRestr);

		} else if (restrictionType == RESTR_SUPP) {
			pmDocRestr = getManagedBean().getSuppTypeRestrictions().get(
					rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getSuppTypeRestrictions().remove(pmDocRestr);

		} else if (restrictionType == RESTR_CATEGORY) {
			pmDocRestr = getManagedBean().getCategoryRestrictions().get(
					rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getCategoryRestrictions().remove(pmDocRestr);

		} else if (restrictionType == CHECKLIST) {
			pmDocRestr = getManagedBean().getCheckListRestrictions().get(
					rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getCheckListRestrictions().remove(pmDocRestr);

		} else if (restrictionType == RESTR_CERTBODY) {
			pmDocRestr = getManagedBean().getCertBodyRestrictions().get(
					rowIndex);
			pmDocRestr.setDeleted(true);
			getManagedBean().getCertBodyRestrictions().remove(pmDocRestr);

		}

		if (pmDocRestr != null && !pmDocRestr.isBrandNew()) {
			if (getManagedBean().getDeleteRestrictions() == null) {
				getManagedBean().setDeleteRestrictions(
						new ArrayList<PmDocumentRestr>());
			}
			pmDocRestr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			pmDocRestr.setDeleted(true);
			getManagedBean().getDeleteRestrictions().add(pmDocRestr);

			/*
			 * try { oldDeleteRestrictions =
			 * (List<PmDocumentRestr>)BeanUtils.cloneBean
			 * (getManagedBean().getDeleteRestrictions()); } catch (Exception e)
			 * { e.printStackTrace(); }
			 * 
			 * this.getManagedBean().setOldDeleteRestrictions(oldDeleteRestrictions
			 * );
			 */
		}
	}
	
	public void deleteRestrictions(int restrictionType) {
		log.info("deleteRestrictions");
		PmDocumentRestr pmDocRestr = null;
		Integer rowIndex = getManagedBean().getDeleteRowIndex();
		if(rowIndex != null){
			if (restrictionType == RESTR_APP) {
				pmDocRestr = getManagedBean().getAppTypeRestrictions()
						.get(rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getAppTypeRestrictions().remove(pmDocRestr);
			} else if (restrictionType == RESTR_COMP) {
				pmDocRestr = getManagedBean().getComTypeRestrictions()
						.get(rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getComTypeRestrictions().remove(pmDocRestr);

			} else if (restrictionType == RESTR_SUPP) {
				pmDocRestr = getManagedBean().getSuppTypeRestrictions().get(
						rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getSuppTypeRestrictions().remove(pmDocRestr);

			} else if (restrictionType == RESTR_CATEGORY) {
				pmDocRestr = getManagedBean().getCategoryRestrictions().get(
						rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getCategoryRestrictions().remove(pmDocRestr);

			} else if (restrictionType == CHECKLIST) {
				pmDocRestr = getManagedBean().getCheckListRestrictions().get(
						rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getCheckListRestrictions().remove(pmDocRestr);

			} else if (restrictionType == RESTR_CERTBODY) {
				pmDocRestr = getManagedBean().getCertBodyRestrictions().get(
						rowIndex.intValue());
				pmDocRestr.setDeleted(true);
				getManagedBean().getCertBodyRestrictions().remove(pmDocRestr);

			}

			if (pmDocRestr != null && !pmDocRestr.isBrandNew()) {
				if (getManagedBean().getDeleteRestrictions() == null) {
					getManagedBean().setDeleteRestrictions(
							new ArrayList<PmDocumentRestr>());
				}
				pmDocRestr.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				pmDocRestr.setDeleted(true);
				getManagedBean().getDeleteRestrictions().add(pmDocRestr);

				/*
				 * try { oldDeleteRestrictions =
				 * (List<PmDocumentRestr>)BeanUtils.cloneBean
				 * (getManagedBean().getDeleteRestrictions()); } catch (Exception e)
				 * { e.printStackTrace(); }
				 * 
				 * this.getManagedBean().setOldDeleteRestrictions(oldDeleteRestrictions
				 * );
				 */
			}
		}
	}

	/**
	 * Add selected PmCategoryL3 to categoryRestrictions
	 * 
	 * @param pmCategoryL3s
	 */
	public void updateCategoryL3(PmCategoryL3 pmCategoryL3) {
		log.info("updateCategoryL3");
		this.getManagedBean().setSelectedCategoryL3(pmCategoryL3);
		if (pmCategoryL3 != null) {
			if (this.getManagedBean().getCategoryRestrictions() == null) {
				this.getManagedBean().setCategoryRestrictions(
						new ArrayList<PmDocumentRestr>());
			}
			if (isUniqueCategoryL3()) {
				
					PmDocumentRestr categoryRestr = new PmDocumentRestr();
//					categoryRestr
//							.setPmCategoryL3(retrieveCategoryL3DtlById(pmCategoryL3
//									.getCategoryL3Id()));
					categoryRestr.setPmCategoryL3(pmCategoryL3);
					this.getManagedBean().getCategoryRestrictions()
							.add(categoryRestr);
//				this.getManagedBean().setSelectedCategoryL3(null);
			} else {
				throw new EpException(MessageConst.ERROR_770064);
			}
		}
	}

	/**
	 * Validate if selected category are not duplicated
	 * 
	 * @return boolean
	 */
	private boolean isUniqueCategoryL3() {
		log.info("isUniqueCategoryL3");
		boolean isUnique = true;
		if (this.getManagedBean().getSelectedCategoryL3() != null) {
			if (this.getManagedBean().getCategoryRestrictions() != null
					&& !this.getManagedBean().getCategoryRestrictions()
							.isEmpty()) {
				List<Long> categoryL3Id = new ArrayList<Long>();

				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
						.getCategoryRestrictions()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr
							.getRecordStatusFlg()) {
						categoryL3Id.add(pmDocumentRestr.getPmCategoryL3()
								.getCategoryL3Id());
					}
				}


					if (categoryL3Id.contains( this.getManagedBean().getSelectedCategoryL3().getCategoryL3Id())) {
						isUnique = false;
					}


				// Set<Long> tempRestrictIdSet = new
				// HashSet<Long>(tempRestrictIdList);
				// if(!categoryL3Id.isEmpty() &&
				// (tempRestrictIdSet.size()!=categoryL3Id.size())){
				// isUnique = false;
				// }

			}
		}
		return isUnique;
	}

	/**
	 * LOV PostListener - add the selected certbody to the pmCertBodies list.
	 * 
	 * @param pmCertBodies
	 * **/
	public void updateCertBody(List<BaseEntity> pmCertBodies) {
		log.info("updateCertBody");
		if (pmCertBodies != null) {
			if (this.getManagedBean().getCertBodyRestrictions() == null) {
				this.getManagedBean().setCertBodyRestrictions(
						new ArrayList<PmDocumentRestr>());
			}
			if (isUniqueCertBody()) {
				PmDocumentRestr certBodyRestr = null;
				for (int i = 0; i < pmCertBodies.size(); i++) {
					PmCertBody pmCertBody = (PmCertBody) pmCertBodies.get(i);
					certBodyRestr = new PmDocumentRestr();
					certBodyRestr.setPmCertBody(pmCertBody);
					this.getManagedBean().getCertBodyRestrictions()
							.add(certBodyRestr);
				}
				this.getManagedBean().getSelectedCertBodies().clear();
			} else {
				throw new EpException(MessageConst.ERROR_770159);
			}
		}
	}

	/**
	 * check if the selected certbody existed in the pmCertBodies list.
	 * 
	 * @return boolean
	 * **/
	private boolean isUniqueCertBody() {
		log.info("isUniqueCertBody");
		boolean isUnique = true;
		if (this.getManagedBean().getSelectedCertBodies() != null
				&& !this.getManagedBean().getSelectedCertBodies().isEmpty()) {
			if (this.getManagedBean().getCertBodyRestrictions() != null
					&& !this.getManagedBean().getCertBodyRestrictions()
							.isEmpty()) {
				List<Long> certBodyId = new ArrayList<Long>();

				for (PmDocumentRestr pmDocumentRestr : this.getManagedBean()
						.getCertBodyRestrictions()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr
							.getRecordStatusFlg()) {
						certBodyId.add(pmDocumentRestr.getPmCertBody()
								.getCertBodyId());
					}
				}

				for (PmCertBody pmCertBody : this.getManagedBean()
						.getSelectedCertBodies()) {
					if (certBodyId.contains(pmCertBody.getCertBodyId())) {
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
	 * 
	 * @param documentId
	 * @return PmDocument
	 */
	public PmDocument retrieveDocumentById(Long docId) {
		log.info("retrieveDocumentById - Start");
		PmDocument pmDocument = new PmDocument();
		try {
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			reqDTO.setDocumentId(docId);
			IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDocumentSO.class);
			DocumentResDTO resDTO = reqSO.retrieveDocumentDtlById(reqDTO);
			if (resDTO.isSuccessful()) {
				pmDocument = resDTO.getDocument();
			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770062,
					new String[] { e.getMessage() });
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

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.BASIC_ACCOUNT));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.SUPPLIER_APPLICATION_TYPES, searchBean);
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
		List<PmParameterDesc> paramDescs = PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.SUPPLIER_TYPES,
						Arrays.asList(ProfileManagementConst.SUPP_TYPE_EXCLUDE),
						null);
		SelectItem[] items = new SelectItem[paramDescs.size() + 1];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				getFacesScopes().getUserLocale()));
		int i = 1;
		for (PmParameterDesc paramDesc : paramDescs) {
			items[i++] = new SelectItem(paramDesc.getPmParameter()
					.getParameterId(), paramDesc.getCodeName());
		}
		return items;
	}

	/**
	 * retrieve doc definition type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] docDefinitionOptions() {
		log.info("docDefinitionOptions");
		SelectItem[]  selectdocTypeOptions = null;
		if(!getManagedBean().getSelectedDocument().isBrandNew()){
			selectdocTypeOptions=getDropdownValuesBySearchBeanNew(
					ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, null);			
		}else{
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			
			List<String> docDefinitionForNew = new ArrayList<String>();
			docDefinitionForNew.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			docDefinitionForNew.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							docDefinitionForNew));
			
			selectdocTypeOptions=getDropdownValuesBySearchBeanNew(
					ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		}
		
		this.getManagedBean().setDocDefinitionOptions(selectdocTypeOptions);
		return selectdocTypeOptions;
	}

	/**
	 * Get parameter code select items by parameter type and search bean for
	 * dropdown.
	 * 
	 * @param parameterType
	 *            the parameter type
	 * @param searchBean
	 *            the search bean instance
	 * @return array of SelectItem
	 */
	public SelectItem[] getDropdownValuesBySearchBeanNew(String parameterType,
			ISearchBean<PmParameterDesc> searchBean) {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBeanNew(
							parameterType, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterId(), parameterDesc.getCodeName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
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
		// return
		// getDropdownValuesBySearchBeanNew(ParameterTypeConst.SC_SUPPLIER_ACTION,null);
		return this.getManagedBean().getSupplierActionItems();
	}

	/**
	 * retrieve supplier zone
	 * 
	 * @return List<PmZone>
	 */
	public List<PmZone> supplierZoneOptions() {
		log.info("supplierZoneOptions");
		List<PmZone> zoneList = new ArrayList<PmZone>();
		try {
			IZoneSO reqSO = (IZoneSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IZoneSO.class);
			ZoneResDTO resDTO = reqSO.retrieveActiveZoneList();
			if (resDTO.isSuccessful()) {
				zoneList = resDTO.getZoneList();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770065,
					new String[] { e.getMessage() });
		}
		return zoneList;
	}

	/**
	 * Get only applicable doc category (Technical Check List, Financial Check
	 * List, Supplier Registration)
	 * 
	 * @return
	 */
	private List<PmParameterDesc> getDocumentCategoryFilteredOptions() {
		log.info("getDocumentCategoryFilteredOptions");
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somDocCate");
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		bean.setSearchField("pmParameter.parameterCode");
		bean.setSearchValue(new ArrayList<String>(Arrays
				.asList(ProfileManagementConst.SUPPORT_DOC_CATEGORY)));
		searchBean.getDataFilterBeans().add(bean);
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.DOCUMENT_CATEGORY, searchBean);
	}
	/**
	 * Get only applicable doc category except supplier Doc(Technical Check List, Financial Check
	 * List)
	 * 
	 * @return
	 */
	private List<PmParameterDesc> getDocCategoryFilteredOptionsExceptSupp() {
		log.info("getDocCategoryFilteredOptionsExceptSupp");
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "somDocCate");
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		bean.setSearchField("pmParameter.parameterCode");
		bean.setSearchValue(new ArrayList<String>(Arrays
				.asList(ProfileManagementConst.SUPPORT_DOC_CATEGORY_NEW)));
		searchBean.getDataFilterBeans().add(bean);
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.DOCUMENT_CATEGORY, searchBean);
	}

	/**
	 * retrieve document category (parameterId)
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] documentCategoryOptions(boolean byId) {
		log.info("documentCategoryOptions");
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = getDocumentCategoryFilteredOptions();

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
			int i = 1;
			if (byId) {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterId(), parameterDesc.getCodeName());
				}
			} else {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	/**
	 * retrieve document category without supplier doc(parameterId)
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] documentCategoryWithoutSupplierDoc(boolean byId) {
		log.info("documentCategoryWithoutSupplierDoc");
		SelectItem[] items = null;
		try {
			List<PmParameterDesc> parameterDescs = getDocCategoryFilteredOptionsExceptSupp();

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
			int i = 1;
			if (byId) {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterId(), parameterDesc.getCodeName());
				}
			} else {
				for (PmParameterDesc parameterDesc : parameterDescs) {
					items[i++] = new SelectItem(parameterDesc.getPmParameter()
							.getParameterCode(), parameterDesc.getCodeName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}
	/**
	 * retrieve categoryL3 details
	 * 
	 * @param id
	 * @return PmCategoryL3
	 */
	public PmCategoryL3 retrieveCategoryL3DtlById(Long id) {
		log.info("supplierZoneOptions");
		PmCategoryL3 pmCategoryL3 = new PmCategoryL3();
		try {
			ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL3Id(id);
			CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3DtlById(reqDTO);
			if (resDTO.isSuccessful()) {
				pmCategoryL3 = resDTO.getCategoryL3();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770066,
					new String[] { e.getMessage() });
		}
		return pmCategoryL3;
	}

	/**
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getStatusOptions() {

		return PmCommonUtil.getRecordStatusOptions();
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}

	/**
	 * validate selected restriction
	 * 
	 * @param restrictList
	 * @param restrictList
	 * @param restrictType
	 */
	private void validateRestriction(PmDocument pmDocument,
			List<PmDocumentRestr> restrictList, int restrictType) {
		log.info("validateRestriction");
		// if(restrictList!=null){
		boolean isSelected = true;
		boolean isNotEmpty = true;
		if (ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
				.equals(pmDocument.getDocCategoryCode().getParameterCode())) {
			// Validation for mandatory field
			if (RESTR_APP == restrictType && pmDocument.isByAppType()) {
				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getAppTypeCode().getParameterId() == null) {
							isSelected = false;
						}
					}
				}
			} else if (RESTR_COMP == restrictType && pmDocument.isByCompType()) {
				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getCompanyTypeCode()
								.getParameterId() == null) {
							isSelected = false;
						}
					}
				}
			} else if (RESTR_SUPP == restrictType
					&& pmDocument.isBySupplierType()) {

				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getSupplierTypeCode()
								.getParameterId() == null) {
							isSelected = false;
						}
					}
				}
			} else if (RESTR_CATEGORY == restrictType
					&& pmDocument.isByCategory()) {
				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getPmCategoryL3().getCategoryL3Id() == null) {
							isSelected = false;
						}
					}
				}
			} else if (RESTR_CERTBODY == restrictType
					&& pmDocument.isByCertBody()) {
				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getPmCertBody().getCertBodyId() == null) {
							isSelected = false;
						}
					}
				}
			}
		} else {
			if (CHECKLIST == restrictType) {
				if (restrictList == null
						|| (restrictList != null && restrictList.isEmpty())) {
					isNotEmpty = false;
				} else {
					for (PmDocumentRestr pmDocumentRestr : restrictList) {
						if (pmDocumentRestr.getProcurementCatCode()
								.getParameterId() == null) {
							isSelected = false;
						}
					}
				}
			}
		}
		// throw validation message accordingly
		if (!isSelected) {
			if (RESTR_APP == restrictType) {
				throw new EpException(MessageConst.ERROR_770067);
			} else if (RESTR_COMP == restrictType) {
				throw new EpException(MessageConst.ERROR_770068);
			} else if (RESTR_SUPP == restrictType) {
				throw new EpException(MessageConst.ERROR_770069);
			} else if (RESTR_CATEGORY == restrictType) {
				throw new EpException(MessageConst.ERROR_770070);
			} else if (RESTR_CERTBODY == restrictType) {
				throw new EpException(MessageConst.ERROR_770157);
			} else if (CHECKLIST == restrictType) {
				throw new EpException(MessageConst.ERROR_770071);
			}
		} else if (!isNotEmpty) {
			if (RESTR_APP == restrictType) {
				throw new EpException(MessageConst.ERROR_770072);
			} else if (RESTR_COMP == restrictType) {
				throw new EpException(MessageConst.ERROR_770073);
			} else if (RESTR_SUPP == restrictType) {
				throw new EpException(MessageConst.ERROR_770074);
			} else if (RESTR_CATEGORY == restrictType) {
				throw new EpException(MessageConst.ERROR_770075);
			} else if (RESTR_CERTBODY == restrictType) {
				throw new EpException(MessageConst.ERROR_770158);
			} else if (CHECKLIST == restrictType) {
				throw new EpException(MessageConst.ERROR_770076);
			}
		}
		// }
	}

	/**
	 * validate unique restr type
	 * 
	 * @param pmParam
	 * @param id
	 * @param restrictType
	 */
	public boolean validateUniqueRestrType(int restrictType) {
		log.info("validateUniqueRestrType");
		boolean isUnique = true;
		List<PmDocumentRestr> pmDocumentRestrs = null;
		if (RESTR_APP == restrictType) {
			pmDocumentRestrs = this.getManagedBean().getAppTypeRestrictions();
		} else if (RESTR_COMP == restrictType) {
			pmDocumentRestrs = this.getManagedBean().getComTypeRestrictions();
		} else if (RESTR_SUPP == restrictType) {
			pmDocumentRestrs = this.getManagedBean().getSuppTypeRestrictions();
		} else if (RESTR_CATEGORY == restrictType) {
			pmDocumentRestrs = this.getManagedBean().getCategoryRestrictions();
		} else if (CHECKLIST == restrictType) {
			pmDocumentRestrs = this.getManagedBean().getCheckListRestrictions();
		}

		// Validation for unique restriction
		if (pmDocumentRestrs != null && pmDocumentRestrs.size() > 1) {
			List<Long> tempRestrictIdList = new ArrayList<Long>();
			for (PmDocumentRestr pmDocumentRestr : pmDocumentRestrs) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr
						.getRecordStatusFlg()) {
					if (RESTR_APP == restrictType) {
						tempRestrictIdList.add(pmDocumentRestr.getAppTypeCode()
								.getParameterId());
					} else if (RESTR_COMP == restrictType) {
						tempRestrictIdList.add(pmDocumentRestr
								.getCompanyTypeCode().getParameterId());
					} else if (RESTR_SUPP == restrictType) {
						tempRestrictIdList.add(pmDocumentRestr
								.getSupplierTypeCode().getParameterId());
					} else if (RESTR_CATEGORY == restrictType) {
						tempRestrictIdList.add(pmDocumentRestr
								.getPmCategoryL3().getCategoryL3Id());
					} else if (CHECKLIST == restrictType) {
						tempRestrictIdList.add(pmDocumentRestr
								.getProcurementCatCode().getParameterId());
					}
				}
			}

			Set<Long> tempRestrictIdSet = new HashSet<Long>(tempRestrictIdList);
			if (!tempRestrictIdList.isEmpty()
					&& (tempRestrictIdSet.size() != tempRestrictIdList.size())) {
				isUnique = false;
				if (RESTR_APP == restrictType) {
					throw new EpException(MessageConst.ERROR_770077);
				} else if (RESTR_COMP == restrictType) {
					throw new EpException(MessageConst.ERROR_770078);
				} else if (RESTR_SUPP == restrictType) {
					throw new EpException(MessageConst.ERROR_770079);
				} else if (RESTR_CATEGORY == restrictType) {
					throw new EpException(MessageConst.ERROR_770080);
				} else if (CHECKLIST == restrictType) {
					throw new EpException(MessageConst.ERROR_770081);
				}
			}

		}
		return isUnique;
	}

	/**
	 * add restr to document
	 * 
	 * @param pmDocumentRestrs
	 */
	private void addDocRestrToDocument(PmDocument pmDocument,
			List<PmDocumentRestr> pmDocumentRestrs, DocumentReqDTO reqDTO,
			StatusResDTO statusResDTO) {
		log.info("addDocRestrToDocument");
		if (pmDocumentRestrs != null && pmDocumentRestrs.size() > 0) {
			int activeRecord = 0;
			for (PmDocumentRestr documentRestr : pmDocumentRestrs) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == documentRestr
						.getRecordStatusFlg()) {
					activeRecord++;
				}
				if (documentRestr.isBrandNew()) {
					if (!documentRestr.isSelected()) {
						if (pmDocument.getPmDocumentRestrs() == null) {
							pmDocument
									.setPmDocumentRestrs(new ArrayList<PmDocumentRestr>());
						}
						pmDocument.addPmDocumentRestrs(documentRestr);
						getAddedAndDeletedRestrToDocument(pmDocument,
								documentRestr, reqDTO, statusResDTO);
						documentRestr.setSelected(true);
					}
				} else {
					if (!documentRestr.isSelected()) {
						PmDocumentRestr pmDocumentRestr = null;
						for (int i = 0; i < pmDocument.getPmDocumentRestrs()
								.size(); i++) {
							pmDocumentRestr = pmDocument.getPmDocumentRestrs()
									.get(i);
							if (pmDocumentRestr != null
									&& pmDocumentRestr.getDocumentRestrId()
											.equals(documentRestr
													.getDocumentRestrId())) {
								pmDocument.getPmDocumentRestrs().set(i,
										documentRestr);
								if (documentRestr.isDeleted()) {
									getAddedAndDeletedRestrToDocument(
											pmDocument, documentRestr, reqDTO,
											statusResDTO);
								}
								break;
							}
						}
						documentRestr.setSelected(true);
					}

				}
			}
			if (!ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
					.equals(pmDocument.getDocCategoryCode().getParameterCode())) {
				if (activeRecord > 0) {
					pmDocument.setByProcureCat(true);
				}
			}

			/*
			 * if(this.getManagedBean().getOldDeleteRestrictions().size()<this.
			 * getManagedBean().getDeleteRestrictions().size()){
			 * getAddedAndDeletedRestrToDocument
			 * (pmDocument,reqDTO,statusResDTO); }
			 */

		}
		// else{
		// if(!ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT.equals(pmDocument.getDocCategoryCode().getParameterCode())){
		// pmDocument.setByProcureCat(false);
		// }
		// }
	}

	private void getAddedAndDeletedRestrToDocument(PmDocument pmDocument,
			PmDocumentRestr documentRestr, DocumentReqDTO reqDTO,
			StatusResDTO statusResDTO) {

		String docRestreMessageId = "";
		// String[] commonMsgParam =null;

		if (pmDocument.isByAppType() && documentRestr.getAppTypeCode() != null) {
			if (documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80804;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80805;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}
		}
		if (pmDocument.isByCompType()
				&& documentRestr.getCompanyTypeCode() != null) {
			if (documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80806;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80807;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}

		}
		if (pmDocument.isBySupplierType()
				&& documentRestr.getSupplierTypeCode() != null) {
			if (documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80808;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80809;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}
		}

		if (pmDocument.isByCategory()
				&& documentRestr.getPmCategoryL3() != null) {
			if (documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80810;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80811;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}
		}
		if (pmDocument.isByCertBody() && documentRestr.getPmCertBody() != null) {
			if (!documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80812;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80813;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}
		}
		if (pmDocument.isByProcureCat()
				&& documentRestr.getProcurementCatCode() != null) {
			if (documentRestr.isDeleted()) {
				docRestreMessageId = MessageConst.TD_80802;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			} else if (documentRestr.isBrandNew()) {
				docRestreMessageId = MessageConst.TD_80803;
				commonTrackingDiaryData(pmDocument, reqDTO, statusResDTO,
						docRestreMessageId);
			}

		}
	}

	private void commonTrackingDiaryData(PmDocument pmDocument,
			DocumentReqDTO reqDTO, StatusResDTO statusResDTO,
			String docRestreMessageId) {
		String[] commonMsgParam;
		commonMsgParam = new String[] { pmDocument.getDocName() };
		trackingDairyData(pmDocument, reqDTO, statusResDTO, docRestreMessageId,
				commonMsgParam);
	}

	private void trackingDairyData(PmDocument pmDocument,
			DocumentReqDTO reqDTO, StatusResDTO statusResDTO,
			String docRestreMessageId, String[] commonMsgParam) {
		reqDTO.getTrackDairyVOs().add(
				PmCommonUtil.assignTrackingDairyForSupportingDocument(
						pmDocument.getDocumentId(),
						pmDocument.getDocCategoryCode().getParameterCode(),
						statusResDTO.getPmStatus() != null ? statusResDTO
								.getPmStatus() : new PmStatus(),
						getPmActionDesc(docRestreMessageId, commonMsgParam)));
	}

	/**
	 * validate all restrictions
	 * 
	 * @param pmDocument
	 */
	private void validateRestrictions(PmDocument pmDocument) {
		log.info("validateRestrictions");

		// validate required field
		validateRestriction(pmDocument, this.getManagedBean()
				.getAppTypeRestrictions(), RESTR_APP);
		validateRestriction(pmDocument, this.getManagedBean()
				.getComTypeRestrictions(), RESTR_COMP);
		validateRestriction(pmDocument, this.getManagedBean()
				.getSuppTypeRestrictions(), RESTR_SUPP);
		validateRestriction(pmDocument, this.getManagedBean()
				.getCategoryRestrictions(), RESTR_CATEGORY);
		validateRestriction(pmDocument, this.getManagedBean()
				.getCertBodyRestrictions(), RESTR_CERTBODY);
		validateRestriction(pmDocument, this.getManagedBean()
				.getCheckListRestrictions(), CHECKLIST);

		// validate unique field
		validateUniqueRestrType(RESTR_APP);
		validateUniqueRestrType(RESTR_COMP);
		validateUniqueRestrType(RESTR_SUPP);
		validateUniqueRestrType(RESTR_CATEGORY);
		validateUniqueRestrType(RESTR_CERTBODY);
		validateUniqueRestrType(CHECKLIST);
	}

	private void getAllRestrictions(PmDocument pmDocument,
			DocumentReqDTO reqDTO, StatusResDTO statusResDTO) {
		log.info("getAllRestrictions");
		try {

			// add doc restr to document
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getAppTypeRestrictions(), reqDTO, statusResDTO);
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getComTypeRestrictions(), reqDTO, statusResDTO);
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getSuppTypeRestrictions(), reqDTO, statusResDTO);
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getCategoryRestrictions(), reqDTO, statusResDTO);
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getCertBodyRestrictions(), reqDTO, statusResDTO);
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getCheckListRestrictions(), reqDTO, statusResDTO);
			// update delete doc restr to 9
			addDocRestrToDocument(pmDocument, this.getManagedBean()
					.getDeleteRestrictions(), reqDTO, statusResDTO);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770082,
					new String[] { e.getMessage() });

		}
	}

	/**
	 * validate document
	 * 
	 * @param pmDocument
	 */
	private void validateDocument(PmDocument pmDocument) {
		log.info("validateDocument");
		if (pmDocument.getDocCategoryCode().getParameterId() == null) {
			throw new EpException(MessageConst.ERROR_770083);
		}
		String displaySeq = pmDocument.getDisplaySeq();
		if (StringUtils.isBlank(displaySeq)) {
			throw new EpException(MessageConst.ERROR_770086,
					new String[] { ResourceBundleUtils.getMessage(
							LABEL_dISPLAY_PRIORITY_KEY,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()) });

		} else if (!StringUtils.isNumeric(displaySeq)) {

			throw new EpException(MessageConst.ERROR_770201, new String[] {
					ResourceBundleUtils.getMessage(LABEL_dISPLAY_PRIORITY_KEY,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), displaySeq });
		}

		if (!ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
				.equals(pmDocument.getDocCategoryCode().getParameterCode())) {
			if (StringUtils.isBlank(pmDocument.getDocName())) {
				throw new EpException(MessageConst.ERROR_770084);
			} else {
					
				// validate Display Seq
				pmDocument.setDisplaySeq(displaySeq.trim());
				if (validateDisplaySequence(pmDocument)) {
					throw new EpException(MessageConst.ERROR_770200,
							new String[] { ResourceBundleUtils.getMessage(
									LABEL_dISPLAY_PRIORITY_KEY,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale()) });
				}
				
				if(DbValueConst.RECORD_STATUS_ACTIVE == pmDocument.getRecordStatusFlg()){
					// doc name unique
					SearchBean<PmDocument> searchBean = new SearchBean<PmDocument>();

					// docCategoryCode
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("docCategoryCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									pmDocument.getDocCategoryCode()
											.getParameterCode()));
					// recordStatusFlg
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(),
									DbValueConst.RECORD_STATUS_ACTIVE));

					// docName
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					bean.setSearchField("docName");
					bean.setCaseInsensitive(true);
					bean.setSearchValue(pmDocument.getDocName());
					searchBean.getDataFilterBeans().add(bean);

					// documentId
					if (!pmDocument.isBrandNew()) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("documentId",
										SearchOperator.OPERATOR_NOT_EQUALS.value(),
										pmDocument.getDocumentId()));
					}
					if (searchDocument(searchBean)) {
						if(pmDocument.isBrandNew()){
							throw new EpException(MessageConst.ERROR_770085);
						}else{
							throw new EpException(MessageConst.ERROR_770220, new String[] {
									PmCommonUtil.getResourceBundleLabels("lbl.pm.document.name")
							});	
						}
						
					}
				}
				
			}

			/*
			 * if(pmDocument.getDisplaySeq()==null ||
			 * pmDocument.getDisplaySeq()<=0){ throw new
			 * EpException(MessageConst.ERROR_770086); }else{ //display seq
			 * unique SearchBean<PmDocument> searchBean = new
			 * SearchBean<PmDocument>();
			 * 
			 * //docCategoryCode searchBean.getDataFilterBeans().add( new
			 * SearchFilterBean("docCategoryCode.parameterCode",
			 * SearchOperator.OPERATOR_EQUALS.value(),
			 * pmDocument.getDocCategoryCode().getParameterCode()));
			 * 
			 * //recordStatusFlg searchBean.getDataFilterBeans().add( new
			 * SearchFilterBean("recordStatusFlg",
			 * SearchOperator.OPERATOR_EQUALS.value(),
			 * DbValueConst.RECORD_STATUS_ACTIVE)); //displaySeq
			 * SearchFilterBean bean = new SearchFilterBean();
			 * bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			 * bean.setSearchField("displaySeq");
			 * bean.setSearchValue(pmDocument.getDisplaySeq());
			 * searchBean.getDataFilterBeans().add(bean);
			 * 
			 * //documentId if(!pmDocument.isBrandNew()){
			 * searchBean.getDataFilterBeans().add( new
			 * SearchFilterBean("documentId",
			 * SearchOperator.OPERATOR_NOT_EQUALS.value(),
			 * pmDocument.getDocumentId())); } if(searchDocument(searchBean)){
			 * throw new EpException(MessageConst.ERROR_770087); } }
			 */

		} else {
			if (StringUtils.isBlank(pmDocument.getDocName())) {
				throw new EpException(MessageConst.ERROR_770084);
			}

			if (pmDocument.getPmZone().getZoneId() != null
					&& pmDocument.getPmZone().getZoneId() == 0) {
				throw new EpException(MessageConst.ERROR_770088);
			}

			// validate Display Seq
			pmDocument.setDisplaySeq(displaySeq.trim());
			if (validateDisplaySequence(pmDocument)) {
				throw new EpException(MessageConst.ERROR_770200,
						new String[] { ResourceBundleUtils.getMessage(
								LABEL_dISPLAY_PRIORITY_KEY,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale()) });
			}

			// doc name unique + zone id unique
			SearchBean<PmDocument> searchBean = new SearchBean<PmDocument>();

			// docCategoryCode
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("docCategoryCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(), pmDocument
									.getDocCategoryCode().getParameterCode()));

			// docName
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("docName");
			bean.setCaseInsensitive(true);
			bean.setSearchValue(pmDocument.getDocName());
			searchBean.getDataFilterBeans().add(bean);

			// recordStatusFlg
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			/*
			 * //displaySeq searchBean.getDataFilterBeans().add( new
			 * SearchFilterBean("displaySeq",
			 * SearchOperator.OPERATOR_EQUALS.value(),
			 * pmDocument.getDisplaySeq()));
			 */

			// zoneId
			if (pmDocument.getPmZone() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmZone.zoneId",
								SearchOperator.OPERATOR_EQUALS.value(),
								pmDocument.getPmZone().getZoneId()));
			}

			// documentId
			if (!pmDocument.isBrandNew()) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("documentId",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								pmDocument.getDocumentId()));
			}
			if (searchDocument(searchBean) && DbValueConst.RECORD_STATUS_ACTIVE == pmDocument.getRecordStatusFlg().intValue()) {
				if(pmDocument.isBrandNew()){
					throw new EpException(MessageConst.ERROR_770089);
				}else{
					throw new EpException(MessageConst.ERROR_770220,
							new String[]{PmCommonUtil.getResourceBundleLabels("lbl.pm.document.name")});
				}
			}
		}

	}

	private boolean searchDocument(ISearchBean<PmDocument> searchBean) {
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		reqDTO.setDocumentSearchBean(searchBean);
		IDocumentSO reqSO = (IDocumentSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDocumentSO.class);
		DocumentResDTO resDTO = reqSO.retrieveDocumentBySearchBean(reqDTO);
		if (resDTO.isSuccessful()) {

			return (resDTO.getDocumentResultBean() != null
					&& resDTO.getDocumentResultBean().getResultList() != null && !resDTO
					.getDocumentResultBean().getResultList().isEmpty());
		}
		return false;
	}

	/**
	 * save to Supporting Document
	 * 
	 * @return xhtml
	 */
	public String saveSupportDoc() {
		log.info("saveSupportDoc - Start");

		DocumentReqDTO reqDTO = null;
		DocumentResDTO resDTO = null;
		IDocumentSO reqSO = null;
		try {
			PmDocument pmDocument = this.getManagedBean().getSelectedDocument();

			reqDTO = new DocumentReqDTO();
			validateDocument(pmDocument);
			validateRestrictions(pmDocument);

			// update or create
			boolean createAction = pmDocument.isBrandNew();

			// this is for tracking diary
			String messageId = "";
			String[] msgParam = new String[] { pmDocument.getDocName() };

			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();

			reqDTO.setBrandNew(createAction);
			reqDTO.setTrackDairyVOs((new ArrayList<PmTrackingDairyVO>()));

			if (createAction) {
				messageId = MessageConst.TD_80800;
			} else {
				messageId = MessageConst.TD_80801;
			}

			statusReqDTO.setStatusId(pmDocument.getRecordStatusFlg()
					.longValue());
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			System.out.println("statusResDTO.getPmStatus().getStatusId():"
					+ statusResDTO.getPmStatus().getStatusId());
			log.info("statusResDTO.getPmStatus().getStatusId():"
					+ statusResDTO.getPmStatus().getStatusId());

			getAllRestrictions(pmDocument, reqDTO, statusResDTO);
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyForSupportingDocument(
							pmDocument.getDocumentId(),
							pmDocument.getDocCategoryCode().getParameterCode(),
							statusResDTO.getPmStatus() != null ? statusResDTO
									.getPmStatus() : new PmStatus(),
							getPmActionDesc(messageId, msgParam)));

			reqDTO.setPmDocument(pmDocument);
			reqSO = (IDocumentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDocumentSO.class);
			resDTO = reqSO.saveDocument(reqDTO);
			reloadEditData(resDTO.getDocument().getDocumentId());

			if (resDTO.isSuccessful()) {
				// loadSupportDoc(resDTO.getDocument().getDocumentId());
				if (createAction) {
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(), 
							FacesMessage.SEVERITY_INFO, 
							MessageConst.INFO_770094, 
							new String[] {
									getParameterDesc(pmDocument
											.getDocCategoryCode()),
									pmDocument
											.getDocName()});					
					return redirectToSearchSupportDoc();
				} else {
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(),
							FacesMessage.SEVERITY_INFO, 
							MessageConst.INFO_770095, 
							new String[] { getParameterDesc(pmDocument
									.getDocCategoryCode()) });
					redirectToMaintainDoc(pmDocument.getDocumentId());
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			reqSO = null;
		}

		log.info("saveSupportDoc - End");
		return null;

	}

	/**
	 * Gets the pm action desc.
	 * 
	 * @param messageId
	 *            the message id
	 * @param params
	 *            the params
	 * @return the pm action desc
	 */
	private String getPmActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	/**
	 * This method used to trigger uncheck action and set remove existing item
	 * in to list for update later.
	 * 
	 * @param index
	 * **/
	public void checkDocumentRestrictions(int index) {
		if (getManagedBean().getDeleteRestrictions() == null) {
			getManagedBean().setDeleteRestrictions(
					new ArrayList<PmDocumentRestr>());
		}

		int tabIndex = index - 1;
		if (tabIndex == 0
				&& !getManagedBean().getSelectedDocument().isByAppType()) {
			if (getManagedBean().getAppTypeRestrictions() != null
					&& !getManagedBean().getAppTypeRestrictions().isEmpty()) {
				for (PmDocumentRestr documentRestr : getManagedBean()
						.getAppTypeRestrictions()) {
					if (!documentRestr.isBrandNew()) {
						documentRestr
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(
								documentRestr);
					}
				}
				getManagedBean().getAppTypeRestrictions().clear();
			}
		} else if (tabIndex == 1
				&& !getManagedBean().getSelectedDocument().isByCompType()) {
			if (getManagedBean().getComTypeRestrictions() != null
					&& !getManagedBean().getComTypeRestrictions().isEmpty()) {
				for (PmDocumentRestr documentRestr : getManagedBean()
						.getComTypeRestrictions()) {
					if (!documentRestr.isBrandNew()) {
						documentRestr
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(
								documentRestr);
					}
				}
				getManagedBean().getComTypeRestrictions().clear();
			}
		} else if (tabIndex == 2
				&& !getManagedBean().getSelectedDocument().isBySupplierType()) {
			if (getManagedBean().getSuppTypeRestrictions() != null
					&& !getManagedBean().getSuppTypeRestrictions().isEmpty()) {
				for (PmDocumentRestr documentRestr : getManagedBean()
						.getSuppTypeRestrictions()) {
					if (!documentRestr.isBrandNew()) {
						documentRestr
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(
								documentRestr);
					}
				}
				getManagedBean().getSuppTypeRestrictions().clear();
			}
		} else if (tabIndex == 3
				&& !getManagedBean().getSelectedDocument().isByCategory()) {
			if (getManagedBean().getCategoryRestrictions() != null
					&& !getManagedBean().getCategoryRestrictions().isEmpty()) {
				for (PmDocumentRestr documentRestr : getManagedBean()
						.getCategoryRestrictions()) {
					if (!documentRestr.isBrandNew()) {
						documentRestr
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(
								documentRestr);
					}
				}
				getManagedBean().getCategoryRestrictions().clear();
			}
		} else if (tabIndex == 4
				&& !getManagedBean().getSelectedDocument().isByCertBody()) {
			if (getManagedBean().getCertBodyRestrictions() != null
					&& !getManagedBean().getCertBodyRestrictions().isEmpty()) {
				for (PmDocumentRestr documentRestr : getManagedBean()
						.getCertBodyRestrictions()) {
					if (!documentRestr.isBrandNew()) {
						documentRestr
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						getManagedBean().getDeleteRestrictions().add(
								documentRestr);
					}
				}
				getManagedBean().getCertBodyRestrictions().clear();
			}
		}

		updateTabSelectedIndex(index);
	}

	/**
	 * update the tab index after for checkbox action
	 * 
	 * @param index
	 * **/
	public void updateTabSelectedIndex(int index) {
		defaultTab(getManagedBean().getSelectedDocument());

		int tabIndex = index - 1;
		if (tabIndex == 0
				&& getManagedBean().getSelectedDocument().isByAppType()) {
			getManagedBean().setSelectedTabIndex(tabIndex);
		} else if (tabIndex == 1
				&& getManagedBean().getSelectedDocument().isByCompType()) {
			getManagedBean().setSelectedTabIndex(tabIndex);
		} else if (tabIndex == 2
				&& getManagedBean().getSelectedDocument().isBySupplierType()) {
			getManagedBean().setSelectedTabIndex(tabIndex);
		} else if (tabIndex == 3
				&& getManagedBean().getSelectedDocument().isByCategory()) {
			getManagedBean().setSelectedTabIndex(tabIndex);
		} else if (tabIndex == 4
				&& getManagedBean().getSelectedDocument().isByCertBody()) {
			getManagedBean().setSelectedTabIndex(tabIndex);
		}

	}

	/**
	 * Below codes are for document search
	 */

	/**
	 * search document
	 */
	public void searchDocument() {
		log.info("searchDocument - Start");
		if (this.getManagedBean().getSupportDocSearchBean()
				.getDocCategoryParamCode() == null
				|| StringUtils.isBlank(this.getManagedBean()
						.getSupportDocSearchBean().getDocCategoryParamCode())) {
			throw new EpException(MessageConst.ERROR_770083);
		}
		pmDocumentDataModel.reloadData();
		log.info("searchDocument - End");

	}
	public void searchDocumentForSup() {
		log.info("searchDocumentForSup - Start");
		this.getManagedBean().getSupportDocSearchBean()
				.setDocCategoryParamCode(ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT);
		pmDocumentDataModel.reloadData();
		log.info("searchDocumentForSup - End");

	}

	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmDocument>
	 */
	private ISearchBean<PmDocument> getSearchBeanData(
			ISearchBean<PmDocument> searchBean) {
		log.info("getSearchBeanData - Start");

		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		SupportDocumentSearchBean sdSearchBean = this.getManagedBean()
				.getSupportDocSearchBean();

		// Doc Category
		if (sdSearchBean.getDocCategoryParamCode() != null
				&& !"".equalsIgnoreCase(sdSearchBean.getDocCategoryParamCode())) {
			log.info("+ docCategoryParamCode "
					+ sdSearchBean.getDocCategoryParamCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("docCategoryCode.parameterCode");
			bean.setSearchValue(sdSearchBean.getDocCategoryParamCode());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		// Document Name
		if (sdSearchBean.getDocName() != null
				&& !"".equalsIgnoreCase(sdSearchBean.getDocName())) {
			log.info("+ docName " + sdSearchBean.getDocName());
			SearchFilterBean bean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			bean.setCaseInsensitive(true);
			bean.setSearchField("docName");
			bean.setSearchValue(sdSearchBean.getDocName());
			searchBean.getDataFilterBeans().add(bean);
			isEmpty = false;
		}
		if (ParameterConst.DOCUMENT_CATEGORY_SUPPLIER_DOCUMENT
				.equalsIgnoreCase(sdSearchBean.getDocCategoryParamCode())) {
			// App Type
			if (sdSearchBean.getAppTypeParam() != null
					&& sdSearchBean.getAppTypeParam() > 0L) {
				log.info("+ appTypeParam " + sdSearchBean.getAppTypeParam());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("pmDocumentRestrs.appTypeCode.parameterId");
				bean.setSearchValue(sdSearchBean.getAppTypeParam());
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
			// Comp Type
			if (sdSearchBean.getComTypeParam() != null
					&& sdSearchBean.getComTypeParam() > 0L) {
				log.info("+ comTypeParam " + sdSearchBean.getComTypeParam());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("pmDocumentRestrs.companyTypeCode.parameterId");
				bean.setSearchValue(sdSearchBean.getComTypeParam());
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
			// Supp Type
			if (sdSearchBean.getSuppTypeParam() != null
					&& sdSearchBean.getSuppTypeParam() > 0L) {
				log.info("+ suppTypeParam " + sdSearchBean.getSuppTypeParam());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("pmDocumentRestrs.supplierTypeCode.parameterId");
				bean.setSearchValue(sdSearchBean.getSuppTypeParam());
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}

			// Business Category
			if (sdSearchBean.getBusinessCategory() != null
					&& !"".equalsIgnoreCase(sdSearchBean.getBusinessCategory())) {
				log.info("+ businessCategory "
						+ sdSearchBean.getBusinessCategory());
				SearchFilterBean bean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				bean.setCaseInsensitive(true);
				bean.setSearchField("pmDocumentRestrs.pmCategoryL3.categoryName");
				bean.setSearchValue(sdSearchBean.getBusinessCategory());
				bean.setAndOr(SearchCondition.CONDITION_OR.value());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}

			// Pm Zone
			if (sdSearchBean.getPmZone() != null) {
				log.info("+ pmZone " + sdSearchBean.getPmZone());

				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("pmZone.zoneId");
				searchBean.getDataFilterBeans().add(bean);

				if (sdSearchBean.getPmZone() > 0L) {
					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
							.value());
					bean.setSearchValue(sdSearchBean.getPmZone());

				} else {
					// if zone selected is All.
					bean.setSearchOperator(SearchOperator.OPERATOR_IS_NULL
							.value());
				}

				isEmpty = false;
			}

		} else {
			// Procurement Category
			if (sdSearchBean.getProcureCatParam() != null
					&& sdSearchBean.getProcureCatParam() > 0L) {
				log.info("+ procureCatParam "
						+ sdSearchBean.getProcureCatParam());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("pmDocumentRestrs.procurementCatCode.parameterId");
				bean.setSearchValue(sdSearchBean.getProcureCatParam());
				searchBean.getDataFilterBeans().add(bean);
				isEmpty = false;
			}
		}

		// base on the flag, default list is either select all or empty
		if (isEmpty && !isSearchable) {
			log.info("+ status " + null);
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
	 * @param pmDocumentDataModel
	 *            the pmDocumentDataModel to set
	 */
	public void setPmDocumentDataModel(
			IPagingDataModel<PmDocument> pmDocumentDataModel) {
		this.pmDocumentDataModel = pmDocumentDataModel;
	}

	/**
	 * paginate for document list
	 * 
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
		public ISearchResultBean<PmDocument> pageChangeListener(
				ISearchBean<PmDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {
				// ISearchBean<PmDocument> searchBeanData =
				// getSearchBeanData(searchBean);
				DocumentReqDTO reqDTO = new DocumentReqDTO();
				reqDTO.setDocumentSearchBean(searchBean);
				reqDTO.setDocCategoryCode(getManagedBean()
						.getSupportDocSearchBean().getDocCategoryParamCode());
				reqDTO.setDocName(getManagedBean().getSupportDocSearchBean()
						.getDocName());
				reqDTO.setAppTypeId(getManagedBean().getSupportDocSearchBean()
						.getAppTypeParam());
				reqDTO.setCompanyTypeId(getManagedBean()
						.getSupportDocSearchBean().getComTypeParam());
				reqDTO.setSupplierTypeId(getManagedBean()
						.getSupportDocSearchBean().getSuppTypeParam());
				reqDTO.setCategoryName(getManagedBean()
						.getSupportDocSearchBean().getBusinessCategory());
				reqDTO.setZoneId(getManagedBean().getSupportDocSearchBean()
						.getPmZone());
				reqDTO.setProcurementCatId(getManagedBean()
						.getSupportDocSearchBean().getProcureCatParam());
				reqDTO.setDocStatus(getManagedBean().getSupportDocSearchBean()
						.getDocStatus());
//				reqDTO.setCertBodyName(getManagedBean()
//						.getSupportDocSearchBean().getCertBodyName());
				reqDTO.setCertBodyId(getManagedBean()
						.getSupportDocSearchBean().getGlpCertBodyId());
				IDocumentSO reqSO = (IDocumentSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IDocumentSO.class);
				DocumentResDTO resDTO = reqSO.retrieveDocumentList(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getDocumentResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public static SelectItem[] getActiveAndInactiveStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
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

	/**
	 * @return the restrCertbody
	 */
	public int getRestrCertbody() {
		return RESTR_CERTBODY;
	}

	/**
	 * Returns true if display seq & doc category id holds same as prev values
	 * 
	 * @param pmDocument
	 * @return boolean
	 */
	private boolean validateDisplaySequence(PmDocument pmDocument) {

		SearchBean<PmDocument> searchBean = new SearchBean<PmDocument>();
		// docCategoryCode
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("docCategoryCode.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(), pmDocument
								.getDocCategoryCode().getParameterCode()));
		// display Seq
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("displaySeq",
						SearchOperator.OPERATOR_EQUALS.value(), pmDocument
								.getDisplaySeq()));
		// zoneId
		if (pmDocument.getPmZone() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmZone.zoneId",
							SearchOperator.OPERATOR_EQUALS.value(), pmDocument
									.getPmZone().getZoneId()));
		}

		// documentId
		if (!pmDocument.isBrandNew()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("documentId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							pmDocument.getDocumentId()));
		}

		return searchDocument(searchBean);
	}

	/**
	 * Gets the supplier action details.
	 * 
	 * @param parameterId
	 *            the parameter id
	 * @return the supplier action details
	 */
	public void getSupplierActionDetails(Long parameterId) {

		log.info("getSupplierActionDetails values");

		IParameterSO parameterSO = null;
		SelectItem[] supplierActionItems = null;
		FacesScopes face = new FacesScopes();
		parameterSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(parameterId);
		ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
		PmParameter pmParameter = resDTO.getPmParameter();

		List<String> paramCodeList = new ArrayList<String>();
		List<PmParameterDesc> parameterDescs = null;
		if (pmParameter != null
				&& (ParameterTypeConst.DOCUMENT_DEFINITION_PTJ_UPLOAD)
						.equalsIgnoreCase(pmParameter.getParameterCode())) {
			// pass select * from pm_parameter where parameter_type='SAT' and
			// parameter_code in('V','O');

			paramCodeList.add(ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW);
			paramCodeList
					.add(ParameterTypeConst.SC_SUPPLIER_ACTION_DOWN_UPLOAD);
			parameterDescs = getParameterDescByParamTypeAndParamCode(paramCodeList);

		} else if (pmParameter != null
				&& (ParameterTypeConst.DOCUMENT_DEFINITION_SUPPLIER_UPLOAD)
						.equalsIgnoreCase(pmParameter.getParameterCode())) {
			// pass select * from pm_parameter where parameter_type='SAT' and
			// parameter_code in('U');
			paramCodeList.add(ParameterTypeConst.SC_SUPPLIER_ACTION_UPLOAD);
			parameterDescs = getParameterDescByParamTypeAndParamCode(paramCodeList);
		}

		if (null != parameterDescs && parameterDescs.size() > 0) {

			supplierActionItems = new SelectItem[parameterDescs.size() + 1];
			supplierActionItems[0] = new SelectItem(null,
					ResourceBundleUtils.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				supplierActionItems[i++] = new SelectItem(parameterDesc
						.getPmParameter().getParameterId(),
						parameterDesc.getCodeName());

			}
		}
		this.getManagedBean().setSupplierActionItems(supplierActionItems);

	}

	private SelectItem[] initialSupplierActionItems() {
		List<PmParameterDesc> parameterDescs;
		List<String> paramCodeList = new ArrayList<String>();
		SelectItem[] supplierActionItems = null;
		FacesScopes face = new FacesScopes();
		// pass select * from pm_parameter where parameter_type='SAT' and
		// parameter_code in('V','O','U');
		paramCodeList.add(ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW);
		paramCodeList.add(ParameterTypeConst.SC_SUPPLIER_ACTION_DOWN_UPLOAD);
		paramCodeList.add(ParameterTypeConst.SC_SUPPLIER_ACTION_UPLOAD);
		parameterDescs = getParameterDescByParamTypeAndParamCode(paramCodeList);

		if (null != parameterDescs && parameterDescs.size() > 0) {

			supplierActionItems = new SelectItem[parameterDescs.size() + 1];
			supplierActionItems[0] = new SelectItem(null,
					ResourceBundleUtils.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				supplierActionItems[i++] = new SelectItem(parameterDesc
						.getPmParameter().getParameterId(),
						parameterDesc.getCodeName());

			}
		}
        this.getManagedBean().setSupplierActionItems(supplierActionItems);
		return supplierActionItems;
	}

	/**
	 * Gets the parameter desc by param type and param code.
	 * 
	 * @param paramCodeList
	 *            the param code list
	 * @return the parameter desc by param type and param code
	 */
	private List<PmParameterDesc> getParameterDescByParamTypeAndParamCode(
			List<String> paramCodeList) {

		List<PmParameterDesc> parameterDescs = new ArrayList<PmParameterDesc>();
		for (String paramCode : paramCodeList) {
			PmParameterDesc pmParameterDesc = ParameterHelper.getInstance()
					.getParameterDescByParamTypeAndParamCode(
							ParameterTypeConst.SC_SUPPLIER_ACTION, paramCode);
			parameterDescs.add(pmParameterDesc);
		}
		return parameterDescs;
	}
	
	
	public void selectCategoryAction(PmCategoryL3 pmCategoryL3) {
		log.info("*****selectCategoryAction*******");
		this.getManagedBean().getSupportDocSearchBean().setBusinessCategory(pmCategoryL3.getCategoryName());
	}
	
	public List<PmCertBody> retrieveGlpList(){
		ICertBodySO bodySO = null;
		List<PmCertBody> certBodyList = new ArrayList<>();
		try{
			ISearchBean<PmCertBody> searchBean = new SearchBean<PmCertBody>();
			
			List<String> activeAndInActiveStatus = new ArrayList<String>();
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), 
							activeAndInActiveStatus));
				
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("isGlp", SearchOperator.OPERATOR_EQUALS
							.value(), true));
			
			bodySO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICertBodySO.class);
			CertBodyReqDTO certBodyReqDTO = new CertBodyReqDTO();
			certBodyReqDTO.setCertBodySearchBean(searchBean);
			ISearchResultBean<PmCertBody> certBodyResultBean = 
					bodySO.retrieveBodyList(certBodyReqDTO).getCertBodyResultBean();
			if(certBodyResultBean != null && certBodyResultBean.getResultList() != null 
					&& !certBodyResultBean.getResultList().isEmpty()){
				certBodyList.addAll(certBodyResultBean.getResultList());
			}
		}finally{
			bodySO = null;
		}
		return certBodyList;
	}
}
