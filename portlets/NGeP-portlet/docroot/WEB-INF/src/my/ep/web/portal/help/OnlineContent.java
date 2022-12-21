package my.ep.web.portal.help;

import java.util.Date;

public class OnlineContent {
	private int helpCount; 
	private String helpCountString; 
	private String helpTitle; 
	private Date displayDate;
    private String helpContent;
    private String helpCategory;
        
    public OnlineContent() {}
    
    public OnlineContent(int helpCount, String helpCountString, String helpTitle, Date displayDate, String helpContent, String helpCategory) {
        this.helpCount = helpCount;
        this.helpCountString = helpCountString;
        this.helpTitle = helpTitle;
        this.displayDate = displayDate;
        this.helpContent = helpContent;
        this.helpCategory = helpCategory;
    }
    
    public void setHelpCount(int helpCount){
        this.helpCount = helpCount;
    }

    public int getHelpCount(){
        return helpCount;
    }
    
    public void setHelpCountString(String helpCount){
        this.helpCountString = helpCountString;
    }

    public String getHelpCountString(){
        return helpCountString;
    }
    
    public void setHelpTitle(String helpTitle){
        this.helpTitle = helpTitle;
    }

    public String getHelpTitle(){
        return helpTitle;
    }

	public void setDisplayDate(Date displayDate){
        this.displayDate = displayDate;
    }

    public Date getDisplayDate(){
        return displayDate;
    }
	
	public void setHelpContent(String helpContent){
    	this.helpContent = helpContent;	
    }

    public String getHelpContent(){
        return helpContent;		
    }
    
    public void setHelpCategory(String helpCategory){
    	this.helpCategory = helpCategory;	
    }

    public String getHelpCategory(){
        return helpCategory;		
    }
}
