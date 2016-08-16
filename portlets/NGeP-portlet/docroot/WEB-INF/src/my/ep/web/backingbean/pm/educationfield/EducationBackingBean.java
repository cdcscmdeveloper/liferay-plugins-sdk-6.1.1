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
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.educationfield.EducationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.beanutils.BeanUtils;
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
		// this.getManagedBean().setLazy(true);
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
		this.getManagedBean().setPmEduField(pmEduField);

		return NAV_TO_MAINTAIN;

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
		//return PmCommonUtil.getStatusOptions();
		return PmCommonUtil.getCommonStatusOptions();
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

		if (StringUtils.isNotBlank(getManagedBean().getFieldCodes())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("fieldCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFieldCodes(),
							true));
		}

		if (StringUtils.isNotBlank(getManagedBean().getFieldNames())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("fieldName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFieldNames(),
							true));
		}

		if (getManagedBean().getStatus() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getStatus()));
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
		PmEduField oldPmEduField = null;
		try {
			oldPmEduField = (PmEduField) BeanUtils.cloneBean(pmEduField);
			this.getManagedBean().setOldPmEduField(oldPmEduField);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";
	}

	private void reloadEduData(PmEduField pmEduField) {
		debugLogger("goToMaintaincePage");

		this.getManagedBean().setPmEduField(pmEduField);
		PmEduField oldPmEduField = null;
		try {
			oldPmEduField = (PmEduField) BeanUtils.cloneBean(pmEduField);
			this.getManagedBean().setOldPmEduField(oldPmEduField);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			//throw new EpException("Field Code is Mandatory");
			throw new EpException(MessageConst.ERROR_759000);
		}

		if (StringUtils.isBlank(eduField.getFieldName())) {
			//throw new EpException("Field Name is Mandatory");
			throw new EpException(MessageConst.ERROR_759001);
		}
		
		if (eduField.isBrandNew()) {
			if (!isExistEduField(eduField.getFieldCode())) {
				throw new EpException(MessageConst.ERROR_770160,new String[] {
						getManagedBean()
						.getPmEduField()
						.getFieldCode(),
				getManagedBean()
						.getPmEduField()
						.getFieldName()});
			}

		}
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == eduField.getRecordStatusFlg()){
			validateEduNameField(eduField);
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
	 * @throws Exception 
	 */
	public String saveEducationDtls() throws Exception {
		debugLogger("saveEducationDtls");
		PmEduField eduField = this.getManagedBean().getPmEduField();
		IEducationFieldSO educationfieldSO = null;	
		String returnUrl = null;
		try {
			validationMessage(eduField);
			setUserDetails(eduField);
			
			educationfieldSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IEducationFieldSO.class);
			
			
			EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
			reqDTO.setEduField(eduField);
			reqDTO.setBrandNew(eduField.isBrandNew());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			reqDTO.setTrackingStatus(true);

			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyForEducation(
							eduField.getEduFieldId(),
							eduField.getFieldCode(),
							eduField.getRecordStatusFlg().longValue(),
							getPmActionDesc(eduField.isBrandNew() ? MessageConst.TD_81000 :MessageConst.TD_81001,
									new String[] { this.getManagedBean().getPmEduField().getFieldName()})));

			EducationFieldResDTO resDTO = educationfieldSO
					.saveOrUpdateEduFieldDtls(reqDTO);
			this.getManagedBean().setPmEduField(resDTO.getEduField());
			reloadEduData(resDTO.getEduField());
			PmEduField oldPmEduField = (PmEduField) BeanUtils.cloneBean(resDTO
					.getEduField());
			this.getManagedBean().setOldPmEduField(oldPmEduField);

			if (resDTO.isSuccessful()) {
				if (eduField.isBrandNew()) {
					returnUrl= NAV_TO_MAIN;
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(), 
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_780094, 
							new String[] {
									resDTO.getEduField()
											.getFieldCode(),
									resDTO.getEduField()
											.getFieldName()});
				} else {
					returnUrl = NAV_TO_MAINTAIN;
					PmCommonUtil.showFacesMsg(
							FacesContext.getCurrentInstance(), 
							FacesMessage.SEVERITY_INFO,
							MessageConst.INFO_780095,
							new String[] {
									resDTO.getEduField()
											.getFieldCode(),
									resDTO.getEduField()
											.getFieldName() });
				}
			}

		} catch (Exception e) {
			throw (e);
		} finally {
			educationfieldSO = null;
		}
		return returnUrl;
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

	private boolean isExistEduField(String newEduFieldCode) {
		IEducationFieldSO educationfieldSO = null;
		boolean isValid = true;
		try{
			educationfieldSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IEducationFieldSO.class);
			EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
			ISearchBean<PmEduField> searchBean =  new SearchBean<PmEduField>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("fieldCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							newEduFieldCode.toLowerCase().trim(),
							true));
			reqDTO.setEducationSearchBean(searchBean);
			
			EducationFieldResDTO resDTO = educationfieldSO
					.retrieveEducationBySearch(reqDTO);

			if (resDTO.getEduFieldList() != null
					&& !resDTO.getEduFieldList().isEmpty()) {
				isValid = false;
			}
		}finally{
			educationfieldSO = null;
		}
		
		return isValid;
	}

	/**
	 * add list of category into PmEduFieldRestr.
	 * 
	 * @param pmCategoryL3s
	 *            (List<PmCategoryL3> pmCategoryL3s)
	 */
	public void addEducationList(List<PmCategoryL3> pmCategoryL3s) {
		debugLogger("addEducationList");

	}

	/**
	 * delete list of category into PmEduField by selection.
	 */
	public void deleteEducationList() {
		debugLogger("deleteEducationList");

	}

	/**
	 * Pre load education filed.
	 */
	public void preLoadEducationFiled() {
		log.info("preLoadgroupSearch ....");
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
	private void setUserDetails(BaseEntity baseObject) {
		if (baseObject.isBrandNew()) {
			baseObject.setCreatedBy(1L);
			baseObject
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		}
		baseObject.setChangedBy(1L);
	}
	
	private void validateEduNameField(PmEduField eduField) {
		IEducationFieldSO educationfieldSO = null;
		boolean isOriInactiveStatus = false;
		try{
			educationfieldSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IEducationFieldSO.class);
			EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
			EducationFieldResDTO resDTO = null;
			ISearchBean<PmEduField> searchBean =  new SearchBean<PmEduField>();
			
			if(!eduField.isBrandNew()){
				reqDTO.setEduFieldId(eduField.getEduFieldId());
				resDTO = educationfieldSO
						.retrieveEducationFieldById(reqDTO);
				
				if(resDTO.isSuccessful() && resDTO.getEduField() != null){
					PmEduField oriEduField = resDTO.getEduField();
					if(DbValueConst.RECORD_STATUS_SUSPENDED == oriEduField.getRecordStatusFlg()){
						isOriInactiveStatus = true;
					}
				}
				searchBean.getDataFilterBeans().add(
					new SearchFilterBean("eduFieldId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							eduField.getEduFieldId()));
			}
			
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("fieldName",
							SearchOperator.OPERATOR_EQUALS.value(),
							eduField.getFieldName().toLowerCase().trim(),
							true));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			reqDTO = new EducationFieldReqDTO();
			reqDTO.setEducationSearchBean(searchBean);			
			resDTO = educationfieldSO
					.retrieveEducationBySearch(reqDTO);

			if (resDTO.getEduFieldList() != null
					&& !resDTO.getEduFieldList().isEmpty()) {
				if(eduField.isBrandNew()){
					throw new EpException(MessageConst.ERROR_770221,new String[]{
							PmCommonUtil.getResourceBundleLabels("lbl.pm.ef.fname"),
							PmCommonUtil.getResourceBundleLabels("lbl.pm.ef.fname")					
						});
				}else{
					if(isOriInactiveStatus){
						throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.ef.fname")	});	
					}else{
						throw new EpException(MessageConst.ERROR_770221,new String[]{
								PmCommonUtil.getResourceBundleLabels("lbl.pm.ef.fname"),
								PmCommonUtil.getResourceBundleLabels("lbl.pm.ef.fname")					
							});
					}
				}
			}
		}finally{
			educationfieldSO = null;
		}
	}

}
