/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.tpl;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.persistence.entity.ct.template.CtTemplate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ct.tpl.ContractTemplateManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean For Contract Template
 * 
 * @author Alam
 * @since 23/9/2013	
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
	 private CtTemplate[] selectedTemplates;
	
	 public String getEditorValue() {
		return editorValue;
	 }

	 public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	 }
	 
     public String createTemplate() throws ParseException{
    	 
    	 IContractTemplateSO contractTemplateSO = null;
		 try{
			   ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
		       CtTemplate ctTemplate = this.getManagedBean().getCtTemplate();
			
		       String value = this.getEditorValue();
		       byte[] content = value.getBytes();
		       ctTemplate.setContent(content);
		       
		       contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
		       reqDTO.setCtTemplate(ctTemplate);
		       ContractTemplateResDTO resDTO = contractTemplateSO.createTemplate(reqDTO);
		       if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Save ContractTemplate Name : "+ 
							this.getManagedBean().getCtTemplate().getName()));
					 if(this.getManagedBean().getSearchFilterBean().getSearchField() == null){
			        	   this.getManagedBean().getSearchFilterBean().setSearchField("name");
			           }
			           searchActionListener();
			  }
	     
		  } catch (Exception ex) {
				log.error("Exception>>", ex);
		  }finally {
			  contractTemplateSO = null;
	      }
		return TEMPLATE_RETURN_PAGE;
	}
	
	public String updateTemplate() throws ParseException{
		 IContractTemplateSO contractTemplateSO = null;
		 try{
			   ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
		       String value = this.getEditorValue();
		       byte[] content = value.getBytes();
		       this.getManagedBean().getCtTemplate().setContent(content);
		       
		       contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
		       reqDTO.setCtTemplate(this.getManagedBean().getCtTemplate());
		       contractTemplateSO.updateTemplate(reqDTO);
	           
	           if(this.getManagedBean().getSearchFilterBean().getSearchField() == null){
	        	   this.getManagedBean().getSearchFilterBean().setSearchField("name");
	           }
	           searchActionListener();
		  } catch (Exception ex) {
				log.error("Exception>>", ex);
		  }finally {
			  contractTemplateSO = null;
	      }
		return TEMPLATE_RETURN_PAGE;
	}
	 
	public void searchActionListener() {
		 log.info("searchAction starts");
		 IContractTemplateSO contractTemplateSO = null;
		 try {
			 this.getManagedBean().getSearchFilterBean().setSearchValue(this.getSearchValue());
			 SearchFilterBean searchFilterBean = this.getManagedBean().getSearchFilterBean();	
			
			 contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
			 ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			 reqDTO.setTemplatesSearchBean(super.createSearchBean(CtTemplate.class, "dataTable"));
			 
			 if (StringUtils.isNotBlank((String) searchFilterBean.getSearchValue())) 
			    reqDTO.getTemplatesSearchBean().getDataFilterBeans().add(searchFilterBean);
				
			 ContractTemplateResDTO ResDTO = contractTemplateSO.searchTemplate(reqDTO);
			 this.getManagedBean().setCtTemplatesList(ResDTO.getTemplateResultBean().getResultList());
			 this.getManagedBean().setSelectedIndex(0);

		 } catch (Exception ex) {
			log.error("Exception>>", ex);
		 } finally {
			 contractTemplateSO = null;
		 }
	}
	
	 public void deleteTemplate() {

		    IContractTemplateSO contractTemplateSO = null;
		    ContractTemplateReqDTO reqDTO = null;
			
			try {
				 reqDTO = new ContractTemplateReqDTO();
				 contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
					
				 for(int i = 0; i < selectedTemplates.length; i++) {
					 CtTemplate ctTemplate  = selectedTemplates[i];   
					 reqDTO.setCtTemplate(ctTemplate);
					 ContractTemplateResDTO resDTO= contractTemplateSO.deleteTemplate(reqDTO);
					 if(resDTO.isSuccessful()){
						 searchActionListener();
					 }
				 }
		 	 } catch (Exception ex) {
				log.error("Exception>>", ex); 
			} finally {
				contractTemplateSO = null;
			} 
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

	public CtTemplate[] getSelectedTemplates() {
		return selectedTemplates;
	}

	public void setSelectedTemplates(CtTemplate[] selectedTemplates) {
		this.selectedTemplates = selectedTemplates;
	}

}
