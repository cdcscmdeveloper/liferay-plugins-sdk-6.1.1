/**
 * 
 */
package my.ep.web.managedbean.ss.committee;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="committeMbean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log= EpLogger.getLogger(CommitteManagedBean.class);
	
	private List<ScQtCommittee>	qtcommittee;
	
	
	public List<ScQtCommittee> getQtcommittee() {
		return qtcommittee;
	}

	public void setQtcommittee(List<ScQtCommittee> qtcommittee) {
		this.qtcommittee = qtcommittee;
	}

	
	private String selectedCommitte;
	
	
	public CommitteManagedBean(){
		log.info("inside ");		
	}
	
	
	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}
}
