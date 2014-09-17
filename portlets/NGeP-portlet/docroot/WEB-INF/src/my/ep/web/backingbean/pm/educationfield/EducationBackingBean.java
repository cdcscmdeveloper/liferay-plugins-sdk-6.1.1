package my.ep.web.backingbean.pm.educationfield;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduFieldRestr;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.educationfield.EducationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "educationBackingBean")
@ViewScoped
public class EducationBackingBean extends
	BaseBackingBean<EducationManagedBean> {
	
	private static final Logger log = EpLogger
			.getLogger(EducationBackingBean.class);	
	
	private IPagingDataModel<PmEduField> educationFieldDataModel = new EducationFieldPagingDataModel();	
	
	public EducationBackingBean() {
		log.info("inside constructor");

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}	
	
	/**
	 * reload to education field search page 
	 * 
	 */		
	public String redirectEduSearch() {
		debugLogger("redirectEduSearch");

		return "/xhtml/pm/educationfield/educationFieldMain.xhtml";

	}		
	
	/**
	 * reload to education field creation page
	 * 
	 */	
	public String createEducation() {
		debugLogger("createEducation");
		PmEduField pmEduField = new PmEduField();
		List<PmEduFieldRestr> refEduFieldRestr = new ArrayList<PmEduFieldRestr>();
		this.getManagedBean().setPmEduField(pmEduField);
		this.getManagedBean().setEduRestrList(refEduFieldRestr);

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";

	}
	
	/**
	 * get status item dropdown
	 * 
	 */	
	 public SelectItem[] getEducationStatusList() {
		 
			SelectItem[] items =  new SelectItem[2];
			items[0] = new SelectItem(1, "Active");
			items[1] = new SelectItem(0, "Inactive");
		    return items;
		}	
	 
	
	/**
	 * search education field info based 
	 * on education field names and codes search
	 */
	private ISearchBean<PmEduField> getEducationSearchBeanData(ISearchBean<PmEduField> searchBean){
			
		if (getManagedBean().getFieldCodes() != null
				&& !"".equals(getManagedBean().getFieldCodes())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean
					.setSearchField("fieldCode");
			codesSearchBean.setSearchValue(getManagedBean()
					.getFieldCodes());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}

		if (getManagedBean().getFieldNames() != null
				&& !"".equals(getManagedBean().getFieldNames())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean
					.setSearchField("fieldName");
			codesSearchBean.setSearchValue(getManagedBean()
					.getFieldNames());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}
		
		if (getManagedBean().getStatus() != null
				&& !"".equals(getManagedBean().getStatus())) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean
					.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(getManagedBean()
					.getStatus());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}	

		return searchBean;
	}		
	
	public void retrieveEducationField(){
		debugLogger("retrieveEducationField");
		
		educationFieldDataModel.reloadData();
		
	}

	public IPagingDataModel<PmEduField> getEducationFieldDataModel() {
		return educationFieldDataModel;
	}

	public void setEducationFieldDataModel(
			IPagingDataModel<PmEduField> educationFieldDataModel) {
		this.educationFieldDataModel = educationFieldDataModel;
	}	
	
	private class EducationFieldPagingDataModel extends PagingDataModel<PmEduField> {
		private static final long serialVersionUID = 1L;

		public EducationFieldPagingDataModel() {
			super("educationSR");
		}

		@Override
		public ISearchResultBean<PmEduField> pageChangeListener(ISearchBean<PmEduField> searchBean) {

			try {	
				ISearchBean<PmEduField> searchBeanData = getEducationSearchBeanData(searchBean);
				EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
				

				reqDTO.setEducationSearchBean(searchBeanData);
				
				IEducationFieldSO reqSO = (IEducationFieldSO) BusinessDelegate.getInstance().getServiceObject("pm", IEducationFieldSO.class);	
				EducationFieldResDTO resDTO = reqSO.retrieveEducationBySearch(reqDTO);
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
	 * get pmEduField object info into maintenance page
	 */
	public String goToMaintaincePage(PmEduField pmEduField) {
		debugLogger("goToMaintaincePage");
		
		this.getManagedBean().setPmEduField(pmEduField);
		this.getManagedBean().setEduRestrList(null);

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";
	}	
	
	/**
	 * Validation message for pmfield details
	 * 
	 */
	private void validationMessage(PmEduField eduField){
		debugLogger("validationMessage");
		if(StringUtils.isBlank(eduField.getFieldCode())){
			throw new EpException("Field Code is Mandatory");
		}
		
		if(StringUtils.isBlank(eduField.getFieldName())){
			throw new EpException("Field Name is Mandatory");
		}		
	}	
	
	/**
	 * save and update education field details
	 * 
	 */
	public String saveEducationDtls() {
		debugLogger("saveEducationDtls");
		
		PmEduField eduField = this.getManagedBean().getPmEduField();
		
		//List<PmEduFieldRestr> edufieldRetrs = this.getManagedBean().getEduRestrList();
		//if (edufieldRetrs != null && !edufieldRetrs.isEmpty()) {
	//		eduField.setPmEduFieldRestrs(edufieldRetrs);
	//	}
		
		validationMessage(eduField);
		
		EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
		reqDTO.setEduField(eduField);
		
		IEducationFieldSO iEducationfieldSO = EpEjbBusinessDelegate.getInstance().
				getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IEducationFieldSO.class);
		
		 EducationFieldResDTO resDTO= iEducationfieldSO.saveOrUpdateEduFieldDtls(reqDTO);
		 this.getManagedBean().setPmEduField(resDTO.getEduField());
		 
		 if(resDTO.isSuccessful()){
			 FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,"","Saved Successfully"));
			 
			}
		  		return "/xhtml/pm/educationfield/educationFieldMain.xhtml";
		  		
		}  	
	
	
	
	/**
	 * add list of category into PmEduFieldRestr
	 * 
	 */
	public void addEducationList(List<PmCategoryL3>pmCategoryL3s) {
		debugLogger("addEducationList");
		
		if(pmCategoryL3s!=null){
			if(this.getManagedBean().getEduRestrList()==null){
				this.getManagedBean().setEduRestrList(new ArrayList<PmEduFieldRestr>());
			}

				for(PmCategoryL3 pmCategoryL3: pmCategoryL3s){
					PmEduFieldRestr eduFieldRestr = new PmEduFieldRestr();
					eduFieldRestr.setPmCategoryL3(pmCategoryL3);
					this.getManagedBean().getEduRestrList().add(eduFieldRestr);
				}
				this.getManagedBean().getEduRestrSelectList().clear();

		}
	}

	/**
	 * delete list of category into PmEduFieldRestr
	 * by selection
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
	}
	
