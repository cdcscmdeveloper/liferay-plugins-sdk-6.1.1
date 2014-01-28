package my.ep.web.portal.alert;

import java.util.Date;

public class TickerContent {
	private Long entryId;
	private String alertTitle; 
    private String alertContent;
    private String alertType;
	private Date displayDate;
    
    public TickerContent() {}
    
    public TickerContent(Long entryId, String alertTitle, String alertContent, String alertType, Date displayDate) {
    	this.entryId = entryId;
    	this.alertTitle = alertTitle;
    	this.alertContent = alertContent;
    	this.alertType = alertType;
        this.displayDate = displayDate;
    }
    
    public void setEntryId(Long entryId){
        this.entryId = entryId;
    }

    public Long getEntryId(){
        return entryId;
    }
    
    public void setAlertTitle(String alertTitle){
        this.alertTitle = alertTitle;
    }

    public String getAlertTitle(){
        return alertTitle;
    }
    
    public void setAlertContent(String alertContent){
    	this.alertContent = alertContent;	
    }

    public String getAlertContent(){
        return alertContent;		
    }
    
    public void setAlertType(String alertType){
    	this.alertType = alertType;	
    }

    public String getAlertType(){
        return alertType;		
    }
    
	public void setDisplayDate(Date displayDate){
        this.displayDate = displayDate;
    }

    public Date getDisplayDate(){
        return displayDate;
    }	
}