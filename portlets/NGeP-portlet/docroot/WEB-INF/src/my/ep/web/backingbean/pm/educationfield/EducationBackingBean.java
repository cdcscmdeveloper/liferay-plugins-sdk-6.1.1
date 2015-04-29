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
package my.ep.web.backingbean.pm.educationfield;

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
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;


import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduFieldRestr;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.educationfield.EducationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * The Class EducationBackingBean.
 * 
 * @author DK
 */
@ManagedBean(name = "educationBackingBean")
@ViewScoped
public class EducationBackingBean extends BaseBackingBean<EducationManagedBean> {

	/** The Constant NAV_TO_MAIN. */
	private static final String NAV_TO_MAIN = "educationFieldMain";
	
	/** The Constant NAV_TO_MAINTAIN. */
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/educationfield/educationFieldMaintenance";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(EducationBackingBean.class);

	/** The education field data model. */
	private IPagingDataModel<PmEduField> educationFieldDataModel = new EducationFieldPagingDataModel();

	/**
	 * Instantiates a new education backing bean.
	 */
	public EducationBackingBean() {
		log.info("inside constructor");

	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reload to education field search page.
	 * 
	 * @return the string
	 */
	public String redirectEduSearch() {
		debugLogger("redirectEduSearch");
		//this.getManagedBean().setLazy(true);
		return NAV_TO_MAIN;

	}

	/**
	 * reset search for education field.
	 */
	public void resetSearchEdu() {
		debugLogger("resetSearchEdu");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setFieldCodes(new String());
		this.getManagedBean().setFieldNames(new String());
		this.getManagedBean().setStatus(null);
		educationFieldDataModel = new EducationFieldPagingDataModel();
		

	}

	/**
	 * reload to education field creation page.
	 * 
	 * @return (/xhtml/pm/educationfield/educationFieldMaintenance.xhtml)
	 */
	public String createEducation() {
		debugLogger("createEducation");
		PmEduField pmEduField = new PmEduField();
//		List<PmEduFieldRestr> pmEduFieldRestr = new ArrayList<PmEduFieldRestr>();
		this.getManagedBean().setPmEduField(pmEduField);
		//this.getManagedBean().setEduRestrList(pmEduFieldRestr);

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";

	}

	/**
	 * get status item dropdown.
	 * 
	 * @return the education status list
	 */
	public SelectItem[] getEducationStatusList() {

		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(1, "Active");
		items[1] = new SelectItem(0, "Inactive");
		return items;
	}	
	
	/**
	 * Gets the pm education status list.
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getPmEducationStatusList() {
		return PmCommonUtil.getStatusOptions();
	}

	/**
	 * search education field info based on education field names and codes
	 * search.
	 * 
	 * @param searchBean
	 *            (ISearchBean<PmEduField> searchBean)
	 * @return ISearchBean<PmEduField> searchBean
	 */
	private ISearchBean<PmEduField> getEducationSearchBeanData(
			ISearchBean<PmEduField> searchBean) {

		if (getManagedBean().getFieldCodes() != null
				&& !"".equalsIgnoreCase(getManagedBean().getFieldCodes())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			codesSearchBean.setSearchField("fieldCode");
			codesSearchBean.setCaseInsensitive(true);
			codesSearchBean.setSearchValue(getManagedBean().getFieldCodes());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}

		if (getManagedBean().getFieldNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getFieldNames())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			codesSearchBean.setSearchField("fieldName");
			codesSearchBean.setCaseInsensitive(true);
			codesSearchBean.setSearchValue(getManagedBean().getFieldNames());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}

		if (getManagedBean().getStatus() != null
				&& !"".equals(getManagedBean().getStatus())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(getManagedBean().getStatus());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}

		return searchBean;
	}

	/**
	 * Retrieve education field.
	 */
	public void retrieveEducationField() {
		debugLogger("retrieveEducationField");

		educationFieldDataModel.reloadData();

	}

	/**
	 * Gets the education field data model.
	 * 
	 * @return the education field data model
	 */
	public IPagingDataModel<PmEduField> getEducationFieldDataModel() {
		return educationFieldDataModel;
	}

	/**
	 * Sets the education field data model.
	 * 
	 * @param educationFieldDataModel
	 *            the new education field data model
	 */
	public void setEducationFieldDataModel(
			IPagingDataModel<PmEduField> educationFieldDataModel) {
		this.educationFieldDataModel = educationFieldDataModel;
	}

	/**
	 * The Class EducationFieldPagingDataModel.
	 */
	private class EducationFieldPagingDataModel extends
			PagingDataModel<PmEduField> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new education field paging data model.
		 */
		public EducationFieldPagingDataModel() {
			super("educationSR", getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmEduField> pageChangeListener(
				ISearchBean<PmEduField> searchBean) {

			try {
				ISearchBean<PmEduField> searchBeanData = getEducationSearchBeanData(searchBean);
				EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();

				reqDTO.setEducationSearchBean(searchBeanData);

				IEducationFieldSO reqSO = (IEducationFieldSO) BusinessDelegate
						.getInstance().getServiceObject("pm",
								IEducationFieldSO.class);
				EducationFieldResDTO resDTO = reqSO
						.retrieveEducationBySearch(reqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {
					return resDTO.getEducationSearchResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * get pmEduField object info into maintenance page.
	 * 
	 * @param pmEduField
	 *            (PmEduField pmEduField)
	 * @return (/xhtml/pm/educationfield/educationFieldMaintenance.xhtml)
	 */
	public String goToMaintaincePage(PmEduField pmEduField) {
		debugLogger("goToMaintaincePage");

		this.getManagedBean().setPmEduField(pmEduField);
		this.getManagedBean().setEduRestrList(pmEduField.getPmEduFieldRestrs());

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";
	}

	/**
	 * Validation message for pmEdufield details.
	 * 
	 * @param eduField
	 *            (PmEduField eduField)
	 */
	private void validationMessage(PmEduField eduField) {
		debugLogger("validationMessage");
		if (StringUtils.isBlank(eduField.getFieldCode())) {
			throw new EpException("Field Code is Mandatory");
		}

		if (StringUtils.isBlank(eduField.getFieldName())) {
			throw new EpException("Field Name is Mandatory");
		}

		// if(getManagedBean().getEduRestrList() == null ||
		// getManagedBean().getEduRestrList().isEmpty() ){
		// throw new EpException("Please Add at Least One Category");
		// }

	}

	/**
	 * save and update education field details.
	 * 
	 * @return the string
	 */
	public String saveEducationDtls() {
		debugLogger("saveEducationDtls");

		PmEduField eduField = this.getManagedBean().getPmEduField();
		validationMessage(eduField);
		if (getManagedBean().getEduRestrList() != null&& getManagedBean().getEduRestrList().size() > 0) {
			for(PmEduFieldRestr eduFieldRestr :getManagedBean().getEduRestrList()){
				setUserDetails(eduFieldRestr);
			}
			eduField.setPmEduFieldRestrs(getManagedBean().getEduRestrList());
		} else {
			eduField.setPmEduFieldRestrs(null);
		}
		
		setUserDetails(eduField);

		if (getManagedBean().getEduRestrList() != null
				&& !getManagedBean().getEduRestrList().isEmpty()) {

			for (PmEduFieldRestr restr : eduField.getPmEduFieldRestrs()) {
				restr.setPmEduField(eduField);

			}
		}

		EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
		reqDTO.setEduField(eduField);

		IEducationFieldSO iEducationfieldSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IEducationFieldSO.class);
		
		
		if (eduField.isBrandNew()) {			
			if (!isExistEduField(eduField.getFieldCode())) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_770160, new String[] {
												getManagedBean().getPmEduField().getFieldCode(),
												getManagedBean().getPmEduField().getFieldName()},
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
				return NAV_TO_MAINTAIN;
			}
		
		}
		
		EducationFieldResDTO resDTO = iEducationfieldSO
				.saveOrUpdateEduFieldDtls(reqDTO);
		this.getManagedBean().setPmEduField(resDTO.getEduField());

		if (resDTO.isSuccessful()) {
			if (eduField.isBrandNew()){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_780094, new String[] {
												resDTO.getEduField()
												.getFieldCode(),
												resDTO.getEduField()
														.getFieldName() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_780095, new String[] {
												resDTO.getEduField()
												.getFieldCode(),
												resDTO.getEduField()
														.getFieldName() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

		}
		
		if(!eduField.isBrandNew()){
			return NAV_TO_MAINTAIN;
		}
		else{
			return NAV_TO_MAIN;
		}

	}

	
	public boolean isExistEduField(String newEduFieldCode) {

		EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();

		reqDTO.setFieldCode(newEduFieldCode);

		IEducationFieldSO educationfieldSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IEducationFieldSO.class);		

		EducationFieldResDTO resDTO=educationfieldSO.retrieveEducationFieldList(reqDTO);


		List<String> eduFieldTypeLs = new ArrayList<String>();

		if(resDTO.getEduFieldList()!=null && !resDTO.getEduFieldList().isEmpty()){			

			for(PmEduField eduField:resDTO.getEduFieldList())
			{
				String oldEduField=eduField.getFieldCode().toString();
				if(newEduFieldCode.equals(oldEduField)){
					eduFieldTypeLs.add(oldEduField);
					if(eduFieldTypeLs.contains(oldEduField)){
						return false;
					}
				}
				eduFieldTypeLs.add(oldEduField);

			}
		}
		return true;
	}

	
	/**
	 * add list of category into PmEduFieldRestr.
	 * 
	 * @param pmCategoryL3s
	 *            (List<PmCategoryL3> pmCategoryL3s)
	 */
	public void addEducationList(List<PmCategoryL3> pmCategoryL3s) {
		debugLogger("addEducationList");

		if (pmCategoryL3s != null) {
			if (this.getManagedBean().getEduRestrList() == null) {
				this.getManagedBean().setEduRestrList(
						new ArrayList<PmEduFieldRestr>());
			}

			for (PmCategoryL3 pmCategoryL3 : pmCategoryL3s) {
				PmEduFieldRestr eduFieldRestr = new PmEduFieldRestr();
				eduFieldRestr.setPmCategoryL3(pmCategoryL3);
				this.getManagedBean().getEduRestrList().add(eduFieldRestr);
			}
			this.getManagedBean().getEduRestrSelectList().clear();

		}
	}

	/**
	 * delete list of category into PmEduFieldRestr by selection.
	 */
	public void deleteEducationList() {
		debugLogger("deleteEducationList");

		List<PmEduFieldRestr> educatList = this.getManagedBean()
				.getEduRestrList();

		if (null == educatList) {
			educatList = new ArrayList<PmEduFieldRestr>();
		}

		List<PmEduFieldRestr> eduSelectList = this.getManagedBean()
				.getEduRestrDeleteList();
		if (null == eduSelectList) {
			eduSelectList = new ArrayList<PmEduFieldRestr>();
		}

		if (educatList.size() == eduSelectList.size()) {
			educatList.clear();
		} else {
			for (PmEduFieldRestr category : eduSelectList)
				educatList.remove(category);
		}

	}

	/**
	 * Pre load education filed.
	 */
	public void preLoadEducationFiled() {
		System.out.println("preLoadgroupSearch ....");
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Sets the user details.
	 * 
	 * @param baseObject
	 *            (BaseEntity baseObject)
	 */
	public void setUserDetails(BaseEntity baseObject) {
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(1L);
			baseObject.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(1L);
	}

}
