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
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.type.aut.SearchOperator;
import my.ep.web.util.lookupservice.BusinessDelegate;


public class CmBrandConverter implements Converter {
	
	ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
	CodificationReqDTO reqDTO = new CodificationReqDTO();
	CodificationResDTO resDTO = null;
	private SearchFilterBean brandFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		
		
        if (submittedValue.trim().equals("")) {  
            return null;  
        } else {  
            try {  
            	System.out.println("submittedValue:"+submittedValue);
                Integer number = Integer.parseInt(submittedValue);  
               
                ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
                brandFilterBean.setSearchField("brandId");
                brandFilterBean.setSearchValue(number.longValue());
    			searchBeanColor.getDataFilterBeans().add(brandFilterBean);

    			reqDTO.setBrandSearchBean(searchBeanColor);

    			resDTO = unspscSO.searchBrandObj(reqDTO);
    			
    			return resDTO.getBrandList().get(0);
    			
  
            } catch(Exception ex) {  
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ex.getMessage()));  
            }  
        }  
  
     
    }  
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
        	return String.valueOf(((CmBrand) value).getBrandId());  
        }  
    }  
	
}