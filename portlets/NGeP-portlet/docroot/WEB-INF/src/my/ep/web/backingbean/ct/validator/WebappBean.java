package my.ep.web.backingbean.ct.validator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: bob
 * Date: 5/29/12
 * Time: 5:12 PM
 */

@ManagedBean(name="webappBean")
@ApplicationScoped
public class WebappBean {
    
    public void validateEmail(FacesContext ctx, UIComponent fieldName, Object obj){
		 String errMsg="Email not valid";
		 String inputValue = (String) obj;
		 inputValue=inputValue==null? "":inputValue;
		 Pattern mask = null;
		 String pattern="^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		 mask = Pattern.compile(pattern);
		 Matcher matcher = mask.matcher(inputValue);
		 
		 if (!matcher.matches()) {
			 throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
		 }		
	}
    
    public void checkSpaces(FacesContext ctx, UIComponent fieldName, Object obj){
        String inputValue=(String)obj;
        inputValue=inputValue==null? "":inputValue;
        String errMsg="Only alphabets,underscore,hyphen are allowed";
        String pattern="^[a-zA-Z\\s]+$";  

        if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
        }
    }
    
    public void checkInputField(FacesContext ctx, UIComponent fieldName, Object obj){
        String inputValue=(String)obj;
        inputValue=inputValue==null? "":inputValue;
        if(!inputValue.isEmpty()){
           String errMsg="Only alphabets and space are allowed";
           String pattern="^[a-zA-Z\\s]+$";  
          if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
          }
        }
    }
    
    public void validateNumbers(FacesContext ctx, UIComponent fieldName, Object obj){
        String inputValue=(String)obj;
        inputValue=inputValue==null? "":inputValue;
        if(!inputValue.isEmpty()){
             String errMsg="Only alphabets and numbers are allow";
             String pattern="^[a-zA-Z0-9]+$";  
           if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
          }
        }
    }
    
    public void checkPhoneNumber(FacesContext ctx, UIComponent fieldName, Object obj){
       
       String inputValue=(String)obj;
       if(inputValue == null || inputValue.equalsIgnoreCase(""))
    	   return;
       String errMsg="Invalid phone number";
       String pattern = "\\d{10}";
       if(!inputValue.matches(pattern)){
           throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
       }
    }

    public void checkMobileNumber(FacesContext ctx, UIComponent fieldName, Object obj){
        
        String inputValue=(String)obj;
        if(inputValue == null || inputValue.equalsIgnoreCase(""))
     	   return;
        String errMsg="Invalid mobile number";
        String pattern = "\\d{10}";
        if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
        }
     }

    public void checkFaxNumber(FacesContext ctx, UIComponent fieldName, Object obj){
        
        String inputValue=(String)obj;
        if(inputValue == null || inputValue.equalsIgnoreCase(""))
     	   return;
        String errMsg="Invalid fax number";
        String pattern = "\\d{10}";
        if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
        }
     }

    public void checkEmail(FacesContext ctx, UIComponent fieldName, Object obj){
        String inputValue=(String)obj;
        if(inputValue == null || inputValue.equalsIgnoreCase(""))
        	return;
        String errMsg="Invalid email format";
        String pattern="[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]";//[a-zA-Z]=alphabet; [\\d]or[0-9]=number; $=line end follows;

        if(!inputValue.matches(pattern)){
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
        }
    }
   
    public void validateDPeriod(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	Integer inputValue = (Integer) value;
    	 String errMsg="Delivery Term must greater than 0.";
        if(inputValue >= 1)
          return;
        else
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
    }
    
    public void validateDate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	String errMsg = "From date cannot be greater than to date";
        if (value == null || component.getAttributes().get("fromDate") == null) {
            return;
        }

        Date toDate   = (Date) value; 
        
        //<f:attribute name="fromDate" value="#{edf.value}" />
        Date fromDate = (Date) component.getAttributes().get("fromDate");//<===
        if (!toDate.after(fromDate)) {
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errMsg));
        }
    }
    
    protected void addInfo(String message){
        addMessage(FacesMessage.SEVERITY_INFO,"Information:",message);
    }

    protected void addInfo(String id,String message){
        addMessage(id,FacesMessage.SEVERITY_INFO,"Information:",message);
    }



    protected void addError(String message) {
        addMessage(FacesMessage.SEVERITY_ERROR,"Error:",message);
    }

    protected void addError(String id,String message) {
        addMessage(id,FacesMessage.SEVERITY_ERROR,"Error:",message);
    }

    private void addMessage(FacesMessage.Severity severity,String msgTitle,String message){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity,msgTitle, message));
    }

    private void addMessage(String id,FacesMessage.Severity severity,String msgTitle,String message){
        FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(severity,msgTitle, message));
    }

}
