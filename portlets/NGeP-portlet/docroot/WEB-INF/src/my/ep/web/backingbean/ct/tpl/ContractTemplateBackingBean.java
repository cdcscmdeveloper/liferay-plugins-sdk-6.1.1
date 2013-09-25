/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.tpl;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.persistence.entity.ct.CtTemplate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ct.tpl.ContractTemplateManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.commons.lang.StringUtils;

/**
 * Backing Bean For Contract Template
 * 
 * @author Alam
 * @since 23/9/2013	
 * 
 */

@ManagedBean
@RequestScoped
public class ContractTemplateBackingBean extends BaseBackingBean<ContractTemplateManagedBean> {

	 private static Logger log = Logger.getLogger(ContractTemplateBackingBean.class);
	
	 private static final String TEMPLATE_UPDATE_PAGE = "/xhtml/ct/templates/updateTemplates.xhtml";
     private static final String TEMPLATE_ADD_PAGE = "/xhtml/ct/templates/createTemplates.xhtml";
     private static final String TEMPLATE_RETURN_PAGE = "/xhtml/ct/templates/ctTemplates.xhtml";
 
	 private Object searchValue;
	 private String editorValue;
	
	 public String getEditorValue() {
		return editorValue;
	 }

	 public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	 }

	 public ContractTemplateBackingBean(){
		
	 }

     public String createTemplate() throws ParseException{
	     
		 IContractTemplateSO iContractTemplateSO = null;
		  try{
		      
			   ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
		       CtTemplate ctTemplate = this.getManagedBean().getCtTemplate();
		      
	    	   Date d1=new Date();
		       DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		       Date date = dateFormat.parse(dateFormat.format(d1));
		       long time = date.getTime();
		       ctTemplate.setCreatedDate(new Timestamp(time));
		       ctTemplate.setRecordStatus(true);
		       
		       ctTemplate.setChangedBy(new Long(2345));  
		       ctTemplate.setCreatedBy(new Long(2345));
		      // ctTemplate.setCreatedBy(new FacesScopes().getUserScreenName());
			  
			
		       String value = this.getEditorValue();
		       byte[] content = value.getBytes();
		       
		       ctTemplate.setContent(content);
		       iContractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject(IContractTemplateSO.class);
		       reqDTO.setCtTemplate(ctTemplate);
	           iContractTemplateSO.createTemplate(reqDTO);
	           if(this.getManagedBean().getSearchFilterBean().getSearchField() == null){
	        	   this.getManagedBean().getSearchFilterBean().setSearchField("name");
	           }
	           searchActionListener();
	     
		  } catch (Exception ex) {
				log.error("Exception>>", ex);
		  }finally {
			  iContractTemplateSO = null;
	      }
		return TEMPLATE_RETURN_PAGE;
	}
	
	public String updateTemplate() throws ParseException{
	     
		 IContractTemplateSO iContractTemplateSO = null;
		 try{
		      
			   ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
		       //ctTemplate.setCreatedBy(new FacesScopes().getUserScreenName());
		       this.getManagedBean().getCtTemplate().setChangedBy(new Long(2345));  
		       String value = this.getEditorValue();
		       byte[] content = value.getBytes();
		       this.getManagedBean().getCtTemplate().setContent(content);
		       
		       iContractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject(IContractTemplateSO.class);
		       reqDTO.setCtTemplate(this.getManagedBean().getCtTemplate());
	           iContractTemplateSO.updateTemplate(reqDTO);
	           
	           if(this.getManagedBean().getSearchFilterBean().getSearchField() == null){
	        	   this.getManagedBean().getSearchFilterBean().setSearchField("name");
	           }
	           searchActionListener();
		  } catch (Exception ex) {
				log.error("Exception>>", ex);
		  }finally {
			  iContractTemplateSO = null;
	      }
		return TEMPLATE_RETURN_PAGE;
	}
	 
	public void searchActionListener() {
		
		 log.info("searchAction starts");
		 IContractTemplateSO iContractTemplateSO = null;
		 try {
			 this.getManagedBean().getSearchFilterBean().setSearchValue(this.getSearchValue());
			 SearchFilterBean searchFilterBean = this.getManagedBean().getSearchFilterBean();	
			
			 iContractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject(IContractTemplateSO.class);
			 ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			 reqDTO.setTemplatesSearchBean(super.createSearchBean(CtTemplate.class, "dataTable"));
			 
			 if (StringUtils.isNotBlank((String) searchFilterBean.getSearchValue())) 
			    reqDTO.getTemplatesSearchBean().getDataFilterBeans().add(searchFilterBean);
				
			 ContractTemplateResDTO ResDTO = iContractTemplateSO.retrieveTemplate(reqDTO);
			 this.getManagedBean().setCtTemplatesList(ResDTO.getTemplateResultBean().getResultList());
			 this.getManagedBean().setSelectedIndex(0);

			} catch (Exception ex) {
				log.error("Exception>>", ex);
			} finally {
				iContractTemplateSO = null;
			}
	}
	
	public void deleteTemplate(){
		
		 IContractTemplateSO iContractTemplateSO = null;
		 iContractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject(IContractTemplateSO.class);
		 ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
		 reqDTO.setTemplateId(this.getManagedBean().getCtTemplate().getTemplateId());
		 iContractTemplateSO.deleteTemplate(reqDTO);
		 searchActionListener();
	 }

	 public List<SelectItem> getSearchOptions() {
      	 List<SelectItem> selectItems = new ArrayList<SelectItem>();
		 selectItems.add(new SelectItem("name","Name"));
		 return selectItems;
	}
	    
	public Object getSearchValue() {
			return searchValue;
	}

	public void setSearchValue(Object searchValue) {
			this.searchValue = searchValue;
	}
		
	public String gotoEditPage() {
		
		try {
			  byte[] value= this.getManagedBean().getCtTemplate().getContent();
			  String decoded = new String(value, "UTF-8");
			  this.setEditorValue(decoded);
		   } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		 }	
	  return TEMPLATE_UPDATE_PAGE;
	}

	public String gotoAddPage() {
		   editorValue = "";
		   this.getManagedBean().setCtTemplate(new CtTemplate());
		   return TEMPLATE_ADD_PAGE;
	}
		
	public String gotoListPage() {
		   return TEMPLATE_RETURN_PAGE;
	}
}
