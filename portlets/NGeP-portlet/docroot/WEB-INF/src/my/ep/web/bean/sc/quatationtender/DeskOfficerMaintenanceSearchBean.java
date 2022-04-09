/**
 * 
 */
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;

/**
 * @author Mallaredy Belageri
 *
 */
public class DeskOfficerMaintenanceSearchBean extends QtnTenderSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String originalDeskOfficer;
	
	public String getOriginalDeskOfficer() {
		return originalDeskOfficer;
	}

	public void setOriginalDeskOfficer(String originalDeskOfficer) {
		this.originalDeskOfficer = originalDeskOfficer;
	}
	
}
