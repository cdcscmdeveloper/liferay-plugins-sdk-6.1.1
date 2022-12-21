package my.ep.web.portal.information;

import java.util.Date;

public class InformationContent {
	private String infoTitle; 
	private Date displayDate;
    private String infoContent;
    
    public InformationContent() {}
    
    public InformationContent(String infoTitle, Date displayDate, String infoContent) {
        this.infoTitle = infoTitle;
        this.displayDate = displayDate;
        this.infoContent = infoContent;
    }
    
    public void setInfoTitle(String infoTitle){
        this.infoTitle = infoTitle;
    }

    public String getInfoTitle(){
        return infoTitle;
    }

	public void setDisplayDate(Date displayDate){
        this.displayDate = displayDate;
    }

    public Date getDisplayDate(){
        return displayDate;
    }
	
	public void setInfoContent(String infoContent){
    	this.infoContent = infoContent;	
    }

    public String getInfoContent(){
        return infoContent;		
    }
}
