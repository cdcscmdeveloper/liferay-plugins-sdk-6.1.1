package my.ep.web.managedbean.pm.approveauthority;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAuthority;
import my.ep.persistence.entity.pm.common.PmAuthorityCommittee;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * This ApproveAuthorityManagedBean class is to manage MOF Approving Authority page
 */

@ManagedBean
@SessionScoped
public class ApproveAuthorityManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private Long authId;
	
	private String authCodeDesc;
	
	private String committeeName;
		
	private PmAuthority authority;
	
	private PmAuthorityCommittee pmAuthCommittee;
	
	private PmAuthorityCommittee updateAuthCommittee;
	
	private PmAuthorityCommittee prevCommitteeDetail;
	
	private List<PmAuthority> listAuthCode;
	
	private List<PmAuthorityCommittee> listAuthCommittee;
	
	private boolean autoUpdate = Boolean.TRUE;
	
	private String remark;
	
	private int selectedTabIndex;
	
	private String authIdInString;
	
	private String comName;
					
	public ApproveAuthorityManagedBean() {
		//ApproveAuthorityManagedBean constructor
	}

	public String getAuthCodeDesc() {
		return authCodeDesc;
	}

	public void setAuthCodeDesc(String authCodeDesc) {
		this.authCodeDesc = authCodeDesc;
	}

	public Long getAuthId() {
		return authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	public PmAuthority getAuthority() {
		return authority;
	}

	public void setAuthority(PmAuthority authority) {
		this.authority = authority;
	}

	public List<PmAuthority> getListAuthCode() {
		return listAuthCode;
	}

	public void setListAuthCode(List<PmAuthority> listAuthCode) {
		this.listAuthCode = listAuthCode;
	}

	public List<PmAuthorityCommittee> getListAuthCommittee() {
		return listAuthCommittee;
	}

	public void setListAuthCommittee(List<PmAuthorityCommittee> listAuthCommittee) {
		this.listAuthCommittee = listAuthCommittee;
	}

	@Override
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	@Override
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public PmAuthorityCommittee getPmAuthCommittee() {
		return pmAuthCommittee;
	}

	public void setPmAuthCommittee(PmAuthorityCommittee pmAuthCommittee) {
		this.pmAuthCommittee = pmAuthCommittee;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PmAuthorityCommittee getPrevCommitteeDetail() {
		return prevCommitteeDetail;
	}

	public void setPrevCommitteeDetail(PmAuthorityCommittee prevCommitteeDetail) {
		this.prevCommitteeDetail = prevCommitteeDetail;
	}

	public String getCommitteeName() {
		return committeeName;
	}

	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}

	public PmAuthorityCommittee getUpdateAuthCommittee() {
		return updateAuthCommittee;
	}

	public void setUpdateAuthCommittee(PmAuthorityCommittee updateAuthCommittee) {
		this.updateAuthCommittee = updateAuthCommittee;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public String getAuthIdInString() {
		return authIdInString;
	}

	public void setAuthIdInString(String authIdInString) {
		this.authIdInString = authIdInString;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}
}
