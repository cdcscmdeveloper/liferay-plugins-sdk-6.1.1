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
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.type.aut.SearchOperator;
import my.ep.web.util.lookupservice.BusinessDelegate;


public class CmColorConverter implements Converter {
	
	ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
	CodificationReqDTO reqDTO = new CodificationReqDTO();
	CodificationResDTO resDTO = null;
	private SearchFilterBean colorFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		
		
        if (submittedValue.trim().equals("")) {  
            return null;  
        } else {  
            try {  
            	System.out.println("submittedValue:"+submittedValue);
                Integer number = Integer.parseInt(submittedValue);  
               
                ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
                colorFilterBean.setSearchField("colorId");
                colorFilterBean.setSearchValue(number.longValue());
    			searchBeanColor.getDataFilterBeans().add(colorFilterBean);

    			reqDTO.setColorSearchBean(searchBeanColor);

    			resDTO = unspscSO.searchColorObj(reqDTO);
    			
    			return resDTO.getColorList().get(0);
    			
  
            } catch(Exception ex) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ex.getMessage()));  
            }  
        }  
  
     
    }  
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
        	return String.valueOf(((CmColor) value).getColorId());  
        }  
    }  
	
}