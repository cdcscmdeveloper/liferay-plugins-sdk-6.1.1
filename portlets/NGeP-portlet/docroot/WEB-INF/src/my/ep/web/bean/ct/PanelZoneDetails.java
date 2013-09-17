package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PanelZoneDetails implements Serializable {
   
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	
	private String zoneName;
	private String zoneValue;

	
	private List<PanelZoneDetails> children = new ArrayList<PanelZoneDetails>();
	
	public static final int LEVEL_ZONE = 0;
	public static final int LEVEL_STATE = 1;
	
	private int level;
	
	

	public PanelZoneDetails(String zoneName, String zoneValue){
		this.zoneName=zoneName;
		this.zoneValue=zoneValue;
	
		
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public List<PanelZoneDetails> getChildren() {
		return children;
	}

	public void setChildren(List<PanelZoneDetails> children) {
		this.children = children;
	}

	public PanelZoneDetails(){
		
	}
	
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getZoneValue() {
		return zoneValue;
	}
	public void setZoneValue(String zoneValue) {
		this.zoneValue = zoneValue;
	}
	
}
