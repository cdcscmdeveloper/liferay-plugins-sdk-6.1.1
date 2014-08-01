package my.ep.web.backingbean.pm.educationfield;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.type.aut.SearchOperator;
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
		this.getManagedBean().setPmEduField(pmEduField);

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";

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
	 * get salutation info into maintenance page
	 */
	public String goToMaintaincePage(PmEduField pmEduField) {
		debugLogger("goToMaintaincePage");
		this.getManagedBean().setPmEduField(pmEduField);

		return "/xhtml/pm/educationfield/educationFieldMaintenance.xhtml";
	}	
	
}
