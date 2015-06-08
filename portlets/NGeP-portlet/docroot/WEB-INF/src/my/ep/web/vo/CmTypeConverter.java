package my.ep.web.vo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.type.aut.SearchOperator;
import my.ep.web.util.lookupservice.BusinessDelegate;


public class CmTypeConverter implements Converter {
	
	ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
	CodificationReqDTO reqDTO = new CodificationReqDTO();
	CodificationResDTO resDTO = null;
	private SearchFilterBean typeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		
		
        if (submittedValue.trim().equals("")) {  
            return null;  
        } else {  
            try {  
            	System.out.println("submittedValue:"+submittedValue);
                Integer number = Integer.parseInt(submittedValue);  
               
                ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
    			typeFilterBean.setSearchField("typeId");
    			typeFilterBean.setSearchValue(number.longValue());
    			searchBeanColor.getDataFilterBeans().add(typeFilterBean);

    			reqDTO.setTypeSearchBean(searchBeanColor);

    			resDTO = unspscSO.searchTypeObj(reqDTO);
    			System.out.println("getTypeName:"+resDTO.getTypeList().get(0).getTypeName());
    			return resDTO.getTypeList().get(0);
    			
  
            } catch(Exception ex) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ex.getMessage()));  
            }  
        }  
  
     
    }  
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
        	return String.valueOf(((CmType) value).getTypeId());  
        }  
    }  
	
}