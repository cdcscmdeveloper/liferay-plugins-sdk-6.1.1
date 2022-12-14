package my.ep.web.validator.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.sun.faces.taglib.jsf_core.ConvertNumberTag;


@FacesConverter("bigDecimalConverter")
public class CmDecimalConvertor extends javax.faces.convert.NumberConverter {

/*	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue) {
		
		
		 if (submittedValue.trim().equals("")) {  
	            return null;  
	        } else {  
	            try {  
	            	System.out.println("submittedValue:"+submittedValue);
	              
	            	String completeAmt = "";
	    			
	    			if(submittedValue.contains(".")){

	    				String decimal= submittedValue.substring(submittedValue.indexOf(".")+1,submittedValue.trim().length());

	    				if(decimal.trim().length()==2){
	    					String amout = submittedValue.substring(0, submittedValue.indexOf("."));

	    					completeAmt = amout+"."+ decimal;

	    				}else if(decimal.trim().length()==1){
	    					String amout = submittedValue.substring(0, submittedValue.indexOf("."));

	    					completeAmt = amout+"."+ decimal+"0";   

	    				}else{
	    					String amout = submittedValue.substring(0,submittedValue.indexOf("."));
	    					String subSrtingDecimal = decimal.substring(0, 2);
	    					completeAmt = amout+"."+ subSrtingDecimal;

	    				}
	    			}else{
	    				completeAmt = submittedValue +".00";
	    			}
	    			
	    			
	    			DecimalFormat df = new DecimalFormat("#,###.00");
	    			
	    			String formatAmt = df.format(new BigDecimal(completeAmt));
	    			System.out.println("formatAmt:"+formatAmt);
	    			return new BigDecimal(formatAmt);
	    			
	  
	            } catch(Exception ex) {  
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ex.getMessage()));  
	            }  
	        }  
	}*/

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue) {
		
		System.out.println("submittedValue: " + submittedValue);
		return super.getAsObject(arg0, arg1, submittedValue);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		DecimalFormat df = new DecimalFormat("#,###.00");
		 if (arg2 == null || arg2.equals("")) {  
	            return "";  
	        } else {  
	        	String oriValue = String.valueOf(((BigDecimal) arg2));
	        	
	        	if(oriValue.contains(".")){

    				String decimal= oriValue.substring(oriValue.indexOf(".")+1,oriValue.trim().length());

    				if(decimal.trim().length() > 2){
    					
    					String amout = oriValue.substring(0,oriValue.indexOf("."));
    					String subSrtingDecimal = decimal.substring(0, 2);
    					String completeAmt = amout+"."+ subSrtingDecimal;
    					
    					
    	    			
    	    			String formatAmt = df.format(new BigDecimal(completeAmt));
    					return formatAmt;					
    				}
	        	}
	        	
	        	return df.format(new BigDecimal(oriValue));  
	        }  
		
		
	}
}
