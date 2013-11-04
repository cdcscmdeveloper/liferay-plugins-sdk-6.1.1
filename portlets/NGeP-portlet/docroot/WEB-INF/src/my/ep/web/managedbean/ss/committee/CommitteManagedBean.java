/**
 * 
 */
package my.ep.web.managedbean.ss.committee;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="committeManagedBean")
@SessionScoped
public class CommitteManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log= EpLogger.getLogger(CommitteManagedBean.class);
	
	private List<ScQtCommittee>	qtcommittees= new ArrayList();
	
	private ScQtCommittee qtCommittee= new ScQtCommittee();
	
	private String selectedCommitte;
	
	private List<SelectItem> committeType;
	
	private List<SelectItem> selectionTypes = new ArrayList<SelectItem>();
	
	private List<SelectItem> memeberTypes= new ArrayList<SelectItem>();
	
	private String profileSelction;
	
	private ScCommitteeMember committeMember;
	
	private List<ScCommitteeMember> committemembers;
	
	private ScCommittee scCommittee =new ScCommittee();
	
	public CommitteManagedBean(){
		log.info("inside ");		
	}
	
	
	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}


	public List<ScQtCommittee> getQtcommittees() {
		return qtcommittees;
	}


	public void setQtcommittees(List<ScQtCommittee> qtcommittees) {
		this.qtcommittees = qtcommittees;
	}


	public ScQtCommittee getQtCommittee() {
		return qtCommittee;
	}


	public void setQtCommittee(ScQtCommittee qtCommittee) {
		this.qtCommittee = qtCommittee;
	}


	public List<SelectItem> getCommitteType() {
		return committeType;
	}


	public void setCommitteType(List<SelectItem> committeType) {
		this.committeType = committeType;
	}


	public List<SelectItem> getSelectionTypes() {
		return selectionTypes;
	}


	public void setSelectionTypes(List<SelectItem> selectionTypes) {
		this.selectionTypes = selectionTypes;
	}


	public List<SelectItem> getMemeberTypes() {
		return memeberTypes;
	}


	public void setMemeberTypes(List<SelectItem> memeberTypes) {
		this.memeberTypes = memeberTypes;
	}


	public String getProfileSelction() {
		return profileSelction;
	}


	public void setProfileSelction(String profileSelction) {
		this.profileSelction = profileSelction;
	}


	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}


	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}


	public List<ScCommitteeMember> getCommittemembers() {
		return committemembers;
	}


	public void setCommittemembers(List<ScCommitteeMember> committemembers) {
		this.committemembers = committemembers;
	}


	public ScCommittee getScCommittee() {
		return scCommittee;
	}


	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}


}
