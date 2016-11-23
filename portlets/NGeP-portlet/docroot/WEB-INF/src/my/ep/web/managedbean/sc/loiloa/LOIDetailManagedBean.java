/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.loiloa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiNegoLocation;
import my.ep.persistence.entity.sc.loaloi.ScLoiReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiSupportingDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoiTerm;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResult;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtMeetingAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.sc.quatationtender.LoiLoaBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

// TODO: Auto-generated Javadoc
/**
 * The Class LOIDetailManagedBean.
 *
 * @author Viswanath
 * 
 * @author Sri
 */
@ManagedBean(name = "lOIDetailManagedBean")
@SessionScoped
public class LOIDetailManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The comittee users. */
	private List<SelectItem> comitteeUsers;
	
	/** The approval date. */
	public String approvalDate;

	/** The sc loi. */
	private ScLoi scLoi;
	
	/** The sm supplier. */
	private SmSupplier smSupplier;
	
	/** The sm mof account. */
	private SmMofAccount smMofAccount;
	
	/** The sm company basic. */
	private SmCompanyBasic smCompanyBasic;
	
	/** The sm personnel. */
	private SmPersonnel smPersonnel;
	
	/** The sm address. */
	private SmAddress supplierAddress;
	
	/** The supplier branch. */
	private String supplierBranch;
	
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The supplier hq personnel. */
	private SmPersonnel supplierHQPersonnel;

	
	/** The sc nego location. */
	private ScLoiNegoLocation scNegoLocation;
	
	/** The nego location list. */
	private List<ScLoiNegoLocation> negoLocationList;
	
	/** The selected nego location list. */
	private List<ScLoiNegoLocation> selectedNegoLocationList;
	
	/** The sc nego term. */
	private ScLoiTerm scNegoTerm;
	
	/** The nego terms list. */
	private List<ScLoiTerm> negoTermsList;
	
	/** The selected nego terms list. */
	private List<ScLoiTerm> selectedNegoTermsList;
	
	/** The pm user. */
	private PmUser pmUser;
	
	/** The branchs list. */
	private List<SmSupplierBranch> branchsList;
	
	/** The sc qt evaluation result. */
	private ScQtEvaluationResult scQtEvaluationResult;
	
	/** The status. */
	private String status;
	
	/** The req approve. */
	public String reqApprove;
	
	/** The Address. */
	public String Address;
	
	/** The sc qt item list. */
	private List<ScRequestItem> scQtItemList;
	
	/** The user ministry list. */
	private List<PmOrgProfile> userMinistryList;
	
	/** The user department list. */
	private List<PmOrgProfile> userDepartmentList;

	/** The sm supplier branch. */
	private SmSupplierBranch smSupplierBranch;
	
	/** The branch name. */
	private String branchName;
	
	/** The select item list. */
	private List<SelectItem> selectItemList;
	
	/** The approver names. */
	private List<PmUser>  approverNames; // Drop down Listing for Approver Names
	
	/** The contract signers. */
	private List<PmUser>  contractSigners; // Drop down Listing for Contract Signers
	
	/** The sc qt finalization. */
	private ScQtFinalization scQtFinalization;
	
	/** The sc qt recommends. */
	private List<ScQtRecommend> scQtRecommends= new ArrayList<ScQtRecommend>();
	
	/** The recommend. */
	private ScQtRecommend recommend = new ScQtRecommend();
	
	/** The sc qt recommend dtls. */
	private List<ScQtRecommendDtl> scQtRecommendDtls = new ArrayList<ScQtRecommendDtl>();
	
	/** The loi loas. */
	private List<ScLoiLoa> loiLoas = new ArrayList<ScLoiLoa>();
	
	/** The rec supplier list. */
	private List<ScLoiLoa> recSupplierList = new ArrayList<ScLoiLoa>();
	
	/** The lois. */
	private List<ScLoi> lois = new ArrayList<ScLoi>();
	
	/** The org profile id. */
	private Long orgProfileId;
	
	/** The selected ptj. */
	private PmOrgProfile selectedPtj = new PmOrgProfile();
	
	/** The ptj list. */
	private List<PmOrgProfile> ptjList;
	
	/** The source id. */
	private Long sourceId;
	
	/** The loi loa beans. */
	private List<LoiLoaBean>  loiLoaBeans  = new ArrayList<LoiLoaBean>();
	
	/** The selected loi loa. */
	private ScLoiLoa selectedLoiLoa = new ScLoiLoa();

	/** The sc loi loa items. */
	private List<ScLoiLoaItem> scLoiLoaItems;
	
	/** The contract signer user. */
	private PmUser contractSignerUser;
	
	/** The doc type. */
	private String docType;
	
	/** The approver user. */
	private PmUser approverUser;
	
	/** The sc loi approval. */
	private ScLoiApproval scLoiApproval;
	
	/** The ec activity list. */
	private List<ScQtCommitteeActivity> ecActivityList;
	
	/** The tec activity list. */
	private List<ScQtCommitteeActivity> tecActivityList;
	
	/** The fec activity list. */
	private List<ScQtCommitteeActivity> fecActivityList;
	
	/** The finalization committee activity list */
	private List<ScQtCommitteeActivity> fcActivityList;
	
	/** The sc qt committee. */
	private ScQtCommittee scQtCommittee;
	
	/** The selected meeting att list. */
	private List<ScQtMeetingAttendance> selectedMeetingAttList;
	
	/** The recommend dtl ids. */
	private List<String> recommendDtlIds;
	
	/** The selected sc qt deduction. */
	private ScQtDeduction selectedScQtDeduction;
	
	/** The selected deduction clauses. */
	private List<ScQtDeductionClause> selectedDeductionClauses;
	
	/** The total frequency. */
	private int totalFrequency;
	
	/** The schedule list. */
	private List<ScQtSchedule> scheduleList;
	
	/** The year list. */
	private List<SelectItem> yearList;
	
	/** The years display list. */
	private List<SelectItem> yearsDisplayList;
	
	/** The total progressive payment. */
	private int totalProgressivePayment;
	
	/** The progressive payment list. */
	private List<ScQtProgressivePayment> progressivePaymentList;
	
	/** The recommended loi. */
	private ScLoi recommendedLoi;
	
	/** The selected sc loi reports. */
	private List<ScLoiReport> selectedScLoiReports;
	
	/** The delete sc loi reports. */
	private List<ScLoiReport> deletedScLoiReports;	
	
	/** The selected sc loi report. */
	private ScLoiReport selectedScLoiReport;
	
	/** The attachment doc type. */
	private String attachmentDocType;
	
	/** The sc qt. */
	private ScQt scQt;
	
	/** The selected sc loi supp docs. */
	private List<ScLoiSupportingDoc> selectedScLoiSuppDocs;
	
	/** The selected sc loi supp doc. */
	private ScLoiSupportingDoc selectedScLoiSuppDoc;
	
	/** The recommend list. */
	private List<ScQtRecommend> recommendList;
	
	/** The selected recommend list. */
	private List<ScQtRecommend> selectedRecommendList;
	
	/** The action. */
	private String action;
	
	/** The edit flag. */
	private boolean editFlag;
	
	/** The streamed content. */
	private StreamedContent streamedContent;
	
	/** The award supplier id. */
	private Long awardSupplierId;
	
	/** The committee type. */
	private String committeeType;
	
	/** The multiple loi flag. */
	private boolean multipleLoiFlag;
	
	/** The item details flag. */
	private boolean itemDetailsFlag;
	
	/** The hide column flag. */
	private boolean hideColumnFlag = true;
	
	/** The back to previous page. */
	private String backToPreviousPage;
	
	/** The terms conditions. */
	private String termsConditions;
	
	
	/** The button flag. */
	private boolean buttonFlag;
	
	
	/** The clauses title. */
	private String clausesTitle;
	
	/** The loi loa list title. */
	private String loiLoaListTitle;
	
	/** Is Itemized */
	private boolean itemized;
	
	
	/** The loi required. */
	private boolean loiRequired;
	
	/** The recommended list of Suppliers */
	private List<ScQtEvaluationResultDtl> recommendedSupplierList;
	
	/** The selected supplier */
	private ScQtEvaluationResultDtl selectedSupplier;
	
	/** The role code. */
	private String roleCode;
	    
    /** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    /** The Multi LOI Zone Details */
    private List<TplSpecZoneDtl> multiLOIZoneDetails;
    
    /** Zone map */
    private Map<Long,TplSpecZoneDtl> zoneMap;
    
    /** Items by zone */
    private Map<Long,List<ScQtRecommend>> scQtRecommendsByZone;
    
    /** ScLoiLoaItems by zone */
    private Map<Long,List<ScLoiLoaItem>> loiLoaItemsMap;  
    
    /** ScLoiLoaItems by supplier */
    private Map<Long,List<ScLoiLoaItem>> supplierLoiLoaItemsMap;
    
    /** The zone active index */
	private int ZoneActiveIndex;
	
	/** The zone menu model */
	private MenuModel zoneMenuModel;
	
	/** The select zone */
	private TplSpecZoneDtl selectedZone;
	
	/** Is FC Secretary User */
	private boolean isSecretaryUser;
	
	/** The zonal. */
	private boolean zonal;
	
	private List<ScQtMeetingAttendance> deletedMeetingAttendanceList;
	
	/** The selected supp loi loa bean. */
	private LoiLoaBean selectedLoiLoaBean;
	
	/** The is refinalized bidding. */
	private boolean  refinalizedBidding = Boolean.FALSE;
	
	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;
	
	/** The QT is cancel requested. */
    private boolean qtCancelRequested;
	
    /**
     * Gets the spki backing bean.
     *
     * @return the spki backing bean
     */
    public SpkiBackingBean getSpkiBackingBean() {
    	return spkiBackingBean;
    }

    /**
     * Sets the spki backing bean.
     *
     * @param spkiBackingBean the new spki backing bean
     */
    public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
    	this.spkiBackingBean = spkiBackingBean;
    }
	
	/**
	 * Gets the sc qt evaluation result.
	 *
	 * @return the scQtEvaluationResult
	 */
	public ScQtEvaluationResult getScQtEvaluationResult() {
		return scQtEvaluationResult;
	}

	/**
	 * Sets the sc qt evaluation result.
	 *
	 * @param scQtEvaluationResult the scQtEvaluationResult to set
	 */
	public void setScQtEvaluationResult(ScQtEvaluationResult scQtEvaluationResult) {
		this.scQtEvaluationResult = scQtEvaluationResult;
	}

	/**
	 * Gets the approver names.
	 *
	 * @return the approverNames
	 */
	public List<PmUser> getApproverNames() {
		return approverNames;
	}

	/**
	 * Sets the approver names.
	 *
	 * @param approverNames the approverNames to set
	 */
	public void setApproverNames(List<PmUser> approverNames) {
		this.approverNames = approverNames;
	}

	/**
	 * Gets the select item list.
	 *
	 * @return the selectItemList
	 */
	public List<SelectItem> getSelectItemList() {
		return selectItemList;
	}

	/**
	 * Sets the select item list.
	 *
	 * @param selectItemList the selectItemList to set
	 */
	public void setSelectItemList(List<SelectItem> selectItemList) {
		this.selectItemList = selectItemList;
	}

	/**
	 * Gets the selected nego terms list.
	 *
	 * @return the selectedNegoTermsList
	 */
	public List<ScLoiTerm> getSelectedNegoTermsList() {
		return selectedNegoTermsList;
	}

	/**
	 * Sets the selected nego terms list.
	 *
	 * @param selectedNegoTermsList the selectedNegoTermsList to set
	 */
	public void setSelectedNegoTermsList(List<ScLoiTerm> selectedNegoTermsList) {
		this.selectedNegoTermsList = selectedNegoTermsList;
	}

	/**
	 * Gets the selected nego location list.
	 *
	 * @return the selectedNegoLocationList
	 */
	public List<ScLoiNegoLocation> getSelectedNegoLocationList() {
		return selectedNegoLocationList;
	}

	/**
	 * Sets the selected nego location list.
	 *
	 * @param selectedNegoLocationList the selectedNegoLocationList to set
	 */
	public void setSelectedNegoLocationList(
			List<ScLoiNegoLocation> selectedNegoLocationList) {
		this.selectedNegoLocationList = selectedNegoLocationList;
	}

	/**
	 * Gets the branch name.
	 *
	 * @return the branchName
	 */
	public String getBranchName() {
		return branchName;
	}

	/**
	 * Sets the branch name.
	 *
	 * @param branchName the branchName to set
	 */
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	/**
	 * Gets the sm supplier branch.
	 *
	 * @return the smSupplierBranch
	 */
	public SmSupplierBranch getSmSupplierBranch() {
		return smSupplierBranch;
	}

	/**
	 * Sets the sm supplier branch.
	 *
	 * @param smSupplierBranch the smSupplierBranch to set
	 */
	public void setSmSupplierBranch(SmSupplierBranch smSupplierBranch) {
		this.smSupplierBranch = smSupplierBranch;
	}

	/**
	 * Gets the user ministry list.
	 *
	 * @return the userMinistryList
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * Sets the user ministry list.
	 *
	 * @param userMinistryList the userMinistryList to set
	 */
	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	/**
	 * Gets the user department list.
	 *
	 * @return the userDepartmentList
	 */
	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	/**
	 * Sets the user department list.
	 *
	 * @param userDepartmentList the userDepartmentList to set
	 */
	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}


	/**
	 * Gets the sc qt item list.
	 *
	 * @return the scQtItemList
	 */
	public List<ScRequestItem> getScQtItemList() {
		return scQtItemList;
	}

	/**
	 * Sets the sc qt item list.
	 *
	 * @param scQtItemList the scQtItemList to set
	 */
	public void setScQtItemList(List<ScRequestItem> scQtItemList) {
		this.scQtItemList = scQtItemList;
	}

	/**
	 * Gets the branchs list.
	 *
	 * @return the branchsList
	 */
	public List<SmSupplierBranch> getBranchsList() {
		return branchsList;
	}

	/**
	 * Sets the branchs list.
	 *
	 * @param branchsList the branchsList to set
	 */
	public void setBranchsList(List<SmSupplierBranch> branchsList) {
		this.branchsList = branchsList;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return Address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address            the address to set
	 */
	public void setAddress(String address) {
		Address = address;
	}

	/**
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser            the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Gets the req approve.
	 *
	 * @return the reqApprove
	 */
	public String getReqApprove() {
		return reqApprove;
	}

	/**
	 * Sets the req approve.
	 *
	 * @param reqApprove            the reqApprove to set
	 */
	public void setReqApprove(String reqApprove) {
		this.reqApprove = reqApprove;
	}
	
	/**
	 * Gets the sc nego term.
	 *
	 * @return the scNegoTerm
	 */
	public ScLoiTerm getScNegoTerm() {
		return scNegoTerm;
	}

	/**
	 * Sets the sc nego term.
	 *
	 * @param scNegoTerm            the scNegoTerm to set
	 */
	public void setScNegoTerm(ScLoiTerm scNegoTerm) {
		this.scNegoTerm = scNegoTerm;
	}

	/**
	 * Gets the nego terms list.
	 *
	 * @return the negoTermsList
	 */
	public List<ScLoiTerm> getNegoTermsList() {
		return negoTermsList;
	}

	/**
	 * Sets the nego terms list.
	 *
	 * @param negoTermsList            the negoTermsList to set
	 */
	public void setNegoTermsList(List<ScLoiTerm> negoTermsList) {
		this.negoTermsList = negoTermsList;
	}

	/**
	 * Gets the nego location list.
	 *
	 * @return the negoLocationList
	 */
	public List<ScLoiNegoLocation> getNegoLocationList() {
		return negoLocationList;
	}

	/**
	 * Sets the nego location list.
	 *
	 * @param negoLocationList            the negoLocationList to set
	 */
	public void setNegoLocationList(List<ScLoiNegoLocation> negoLocationList) {
		this.negoLocationList = negoLocationList;
	}

	/**
	 * Gets the sc nego location.
	 *
	 * @return the scNegoLocation
	 */
	public ScLoiNegoLocation getScNegoLocation() {
		return scNegoLocation;
	}

	/**
	 * Sets the sc nego location.
	 *
	 * @param scNegoLocation            the scNegoLocation to set
	 */
	public void setScNegoLocation(ScLoiNegoLocation scNegoLocation) {
		this.scNegoLocation = scNegoLocation;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	
	/**
	 * Gets the sm supplier.
	 *
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * Sets the sm supplier.
	 *
	 * @param smSupplier            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * Gets the sm mof account.
	 *
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * Sets the sm mof account.
	 *
	 * @param smMofAccount            the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the smCompanyBasic
	 */
	public SmCompanyBasic getSmCompanyBasic() {
		return smCompanyBasic;
	}

	/**
	 * @param smCompanyBasic 
	 * 				the smCompanyBasic to set
	 */
	public void setSmCompanyBasic(SmCompanyBasic smCompanyBasic) {
		this.smCompanyBasic = smCompanyBasic;
	}

	/**
	 * @return the smPersonnel
	 */
	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	/**
	 * @param smPersonnel 
	 * 				the smPersonnel to set
	 */
	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	/**
	 * @return the supplierAddress
	 */
	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress 
	 * 				the supplierAddress to set
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierBranch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * @param supplierBranch 
	 * 				the supplierBranch to set
	 */
	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	/**
	 * Gets the sc loi.
	 *
	 * @return the scLoi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}

	/**
	 * Sets the sc loi.
	 *
	 * @param scLoi            the scLoi to set
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}

	/**
	 * Gets the comittee users.
	 *
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * Sets the comittee users.
	 *
	 * @param comitteeUsers            the comitteeUsers to set
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * Gets the approval date.
	 *
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * Sets the approval date.
	 *
	 * @param approvalDate            the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * Gets the selected tab index.
	 *
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Gets the sc qt finalization.
	 *
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * Sets the sc qt finalization.
	 *
	 * @param scQtFinalization the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * Gets the sc qt recommends.
	 *
	 * @return the scQtRecommends
	 */
	public List<ScQtRecommend> getScQtRecommends() {
		return scQtRecommends;
	}

	/**
	 * Sets the sc qt recommends.
	 *
	 * @param scQtRecommends the scQtRecommends to set
	 */
	public void setScQtRecommends(List<ScQtRecommend> scQtRecommends) {
		this.scQtRecommends = scQtRecommends;
	}

	/**
	 * Gets the sc qt recommend dtls.
	 *
	 * @return the scQtRecommendDtls
	 */
	public List<ScQtRecommendDtl> getScQtRecommendDtls() {
		return scQtRecommendDtls;
	}

	/**
	 * Sets the sc qt recommend dtls.
	 *
	 * @param scQtRecommendDtls the scQtRecommendDtls to set
	 */
	public void setScQtRecommendDtls(List<ScQtRecommendDtl> scQtRecommendDtls) {
		this.scQtRecommendDtls = scQtRecommendDtls;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the loi loas.
	 *
	 * @return the loiLoas
	 */
	public List<ScLoiLoa> getLoiLoas() {
		return loiLoas;
	}

	/**
	 * Sets the loi loas.
	 *
	 * @param loiLoas the loiLoas to set
	 */
	public void setLoiLoas(List<ScLoiLoa> loiLoas) {
		this.loiLoas = loiLoas;
	}

	/**
	 * Gets the lois.
	 *
	 * @return the lois
	 */
	public List<ScLoi> getLois() {
		return lois;
	}

	/**
	 * Sets the lois.
	 *
	 * @param lois the lois to set
	 */
	public void setLois(List<ScLoi> lois) {
		this.lois = lois;
	}

	/**
	 * Gets the recommend.
	 *
	 * @return the recommend
	 */
	public ScQtRecommend getRecommend() {
		return recommend;
	}

	/**
	 * Sets the recommend.
	 *
	 * @param recommend the recommend to set
	 */
	public void setRecommend(ScQtRecommend recommend) {
		this.recommend = recommend;
	}

	/**
	 * Gets the selected ptj.
	 *
	 * @return the selectedPtj
	 */
	public PmOrgProfile getSelectedPtj() {
		return selectedPtj;
	}

	/**
	 * Sets the selected ptj.
	 *
	 * @param selectedPtj the selectedPtj to set
	 */
	public void setSelectedPtj(PmOrgProfile selectedPtj) {
		this.selectedPtj = selectedPtj;
	}

	/**
	 * Gets the ptj list.
	 *
	 * @return the ptjList
	 */
	public List<PmOrgProfile> getPtjList() {
		return ptjList;
	}

	/**
	 * Sets the ptj list.
	 *
	 * @param ptjList the ptjList to set
	 */
	public void setPtjList(List<PmOrgProfile> ptjList) {
		this.ptjList = ptjList;
	}

	/**
	 * Gets the org profile id.
	 *
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}

	/**
	 * Sets the org profile id.
	 *
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}

	/**
	 * Gets the contract signers.
	 *
	 * @return the contractSigners
	 */
	public List<PmUser> getContractSigners() {
		return contractSigners;
	}

	/**
	 * Sets the contract signers.
	 *
	 * @param contractSigners the contractSigners to set
	 */
	public void setContractSigners(List<PmUser> contractSigners) {
		this.contractSigners = contractSigners;
	}

	/**
	 * Gets the rec supplier list.
	 *
	 * @return the recSupplierList
	 */
	public List<ScLoiLoa> getRecSupplierList() {
		return recSupplierList;
	}

	/**
	 * Sets the rec supplier list.
	 *
	 * @param recSupplierList the recSupplierList to set
	 */
	public void setRecSupplierList(List<ScLoiLoa> recSupplierList) {
		this.recSupplierList = recSupplierList;
	}

	/**
	 * Gets the source id.
	 *
	 * @return the sourceId
	 */
	public Long getSourceId() {
		return sourceId;
	}

	/**
	 * Sets the source id.
	 *
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * Gets the sc loi loa items.
	 *
	 * @return the scLoiLoaItems
	 */
	public List<ScLoiLoaItem> getScLoiLoaItems() {
		return scLoiLoaItems;
	}

	/**
	 * Sets the sc loi loa items.
	 *
	 * @param scLoiLoaItems the scLoiLoaItems to set
	 */
	public void setScLoiLoaItems(List<ScLoiLoaItem> scLoiLoaItems) {
		this.scLoiLoaItems = scLoiLoaItems;
	}

	/**
	 * Gets the loi loa beans.
	 *
	 * @return the loiLoaBeans
	 */
	public List<LoiLoaBean> getLoiLoaBeans() {
		return loiLoaBeans;
	}

	/**
	 * Sets the loi loa beans.
	 *
	 * @param loiLoaBeans the loiLoaBeans to set
	 */
	public void setLoiLoaBeans(List<LoiLoaBean> loiLoaBeans) {
		this.loiLoaBeans = loiLoaBeans;
	}

	/**
	 * Gets the selected loi loa.
	 *
	 * @return the selectedLoiLoa
	 */
	public ScLoiLoa getSelectedLoiLoa() {
		return selectedLoiLoa;
	}

	/**
	 * Sets the selected loi loa.
	 *
	 * @param selectedLoiLoa the selectedLoiLoa to set
	 */
	public void setSelectedLoiLoa(ScLoiLoa selectedLoiLoa) {
		this.selectedLoiLoa = selectedLoiLoa;
	}

	/**
	 * Gets the supplier hq personnel.
	 *
	 * @return the supplier hq personnel
	 */
	public SmPersonnel getSupplierHQPersonnel() {
		return supplierHQPersonnel;
	}

	/**
	 * Sets the supplier hq personnel.
	 *
	 * @param supplierHQPersonnel the new supplier hq personnel
	 */
	public void setSupplierHQPersonnel(SmPersonnel supplierHQPersonnel) {
		this.supplierHQPersonnel = supplierHQPersonnel;
	}

	/**
	 * Gets the contract signer user.
	 *
	 * @return the contract signer user
	 */
	public PmUser getContractSignerUser() {
		return contractSignerUser;
	}

	/**
	 * Sets the contract signer user.
	 *
	 * @param contractSignerUser the new contract signer user
	 */
	public void setContractSignerUser(PmUser contractSignerUser) {
		this.contractSignerUser = contractSignerUser;
	}

	/**
	 * Gets the doc type.
	 *
	 * @return the doc type
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the doc type.
	 *
	 * @param docType the new doc type
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets the ec activity list.
	 *
	 * @return the ecActivityList
	 */
	public List<ScQtCommitteeActivity> getEcActivityList() {
		return ecActivityList;
	}

	/**
	 * Sets the ec activity list.
	 *
	 * @param ecActivityList the ecActivityList to set
	 */
	public void setEcActivityList(List<ScQtCommitteeActivity> ecActivityList) {
		this.ecActivityList = ecActivityList;
	}

	/**
	 * Gets the tec activity list.
	 *
	 * @return the tecActivityList
	 */
	public List<ScQtCommitteeActivity> getTecActivityList() {
		return tecActivityList;
	}

	/**
	 * Sets the tec activity list.
	 *
	 * @param tecActivityList the tecActivityList to set
	 */
	public void setTecActivityList(List<ScQtCommitteeActivity> tecActivityList) {
		this.tecActivityList = tecActivityList;
	}

	/**
	 * Gets the fec activity list.
	 *
	 * @return the fecActivityList
	 */
	public List<ScQtCommitteeActivity> getFecActivityList() {
		return fecActivityList;
	}

	/**
	 * Sets the fec activity list.
	 *
	 * @param fecActivityList the fecActivityList to set
	 */
	public void setFecActivityList(List<ScQtCommitteeActivity> fecActivityList) {
		this.fecActivityList = fecActivityList;
	}
	
	/**
	 * @return the fcActivityList
	 */
	public List<ScQtCommitteeActivity> getFcActivityList() {
		return fcActivityList;
	}

	/**
	 * @param fcActivityList 
	 * 				the fcActivityList to set
	 */
	public void setFcActivityList(
					List<ScQtCommitteeActivity> fcActivityList) {
		this.fcActivityList = fcActivityList;
	}
	
	/**
	 * Gets the sc qt committee.
	 *
	 * @return the scQtCommittee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * Sets the sc qt committee.
	 *
	 * @param scQtCommittee the scQtCommittee to set
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Gets the selected meeting att list.
	 *
	 * @return the selectedMeetingAttList
	 */
	public List<ScQtMeetingAttendance> getSelectedMeetingAttList() {
		return selectedMeetingAttList;
	}

	/**
	 * Sets the selected meeting att list.
	 *
	 * @param selectedMeetingAttList the selectedMeetingAttList to set
	 */
	public void setSelectedMeetingAttList(
			List<ScQtMeetingAttendance> selectedMeetingAttList) {
		this.selectedMeetingAttList = selectedMeetingAttList;
	}



	/**
	 * Gets the sc loi approval.
	 *
	 * @return the sc loi approval
	 */
	public ScLoiApproval getScLoiApproval() {
		return scLoiApproval;
	}

	/**
	 * Sets the sc loi approval.
	 *
	 * @param scLoiApproval the new sc loi approval
	 */
	public void setScLoiApproval(ScLoiApproval scLoiApproval) {
		this.scLoiApproval = scLoiApproval;
	}

	/**
	 * Gets the approver user.
	 *
	 * @return the approver user
	 */
	public PmUser getApproverUser() {
		return approverUser;
	}

	/**
	 * Sets the approver user.
	 *
	 * @param approverUser the new approver user
	 */
	public void setApproverUser(PmUser approverUser) {
		this.approverUser = approverUser;
	}

	/**
	 * Gets the recommend dtl ids.
	 *
	 * @return the recommend dtl ids
	 */
	public List<String> getRecommendDtlIds() {
		return recommendDtlIds;
	}

	/**
	 * Sets the recommend dtl ids.
	 *
	 * @param recommendDtlIds the new recommend dtl ids
	 */
	public void setRecommendDtlIds(List<String> recommendDtlIds) {
		this.recommendDtlIds = recommendDtlIds;
	}


	/**
	 * Gets the selected sc qt deduction.
	 *
	 * @return the selected sc qt deduction
	 */
	public ScQtDeduction getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	/**
	 * Sets the selected sc qt deduction.
	 *
	 * @param selectedScQtDeduction the new selected sc qt deduction
	 */
	public void setSelectedScQtDeduction(ScQtDeduction selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	/**
	 * Gets the selected deduction clauses.
	 *
	 * @return the selected deduction clauses
	 */
	public List<ScQtDeductionClause> getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * Sets the selected deduction clauses.
	 *
	 * @param selectedDeductionClauses the new selected deduction clauses
	 */
	public void setSelectedDeductionClauses(List<ScQtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	/**
	 * Gets the total frequency.
	 *
	 * @return the totalFrequency
	 */
	public int getTotalFrequency() {
		return totalFrequency;
	}

	/**
	 * Sets the total frequency.
	 *
	 * @param totalFrequency the totalFrequency to set
	 */
	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	/**
	 * Gets the schedule list.
	 *
	 * @return the scheduleList
	 */
	public List<ScQtSchedule> getScheduleList() {
		return scheduleList;
	}

	/**
	 * Sets the schedule list.
	 *
	 * @param scheduleList the scheduleList to set
	 */
	public void setScheduleList(List<ScQtSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	/**
	 * Gets the year list.
	 *
	 * @return the yearList
	 */
	public List<SelectItem> getYearList() {
		return yearList;
	}

	/**
	 * Sets the year list.
	 *
	 * @param yearList the yearList to set
	 */
	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	/**
	 * Gets the years display list.
	 *
	 * @return the yearsDisplayList
	 */
	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	/**
	 * Sets the years display list.
	 *
	 * @param yearsDisplayList the yearsDisplayList to set
	 */
	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	/**
	 * Gets the total progressive payment.
	 *
	 * @return the totalProgressivePayment
	 */
	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	/**
	 * Sets the total progressive payment.
	 *
	 * @param totalProgressivePayment the totalProgressivePayment to set
	 */
	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}

	/**
	 * Gets the progressive payment list.
	 *
	 * @return the progressivePaymentList
	 */
	public List<ScQtProgressivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	/**
	 * Sets the progressive payment list.
	 *
	 * @param progressivePaymentList the progressivePaymentList to set
	 */
	public void setProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}


	/**
	 * Gets the recommended loi.
	 *
	 * @return the recommendedLoi
	 */
	public ScLoi getRecommendedLoi() {
		return recommendedLoi;
	}

	/**
	 * Sets the recommended loi.
	 *
	 * @param recommendedLoi the recommendedLoi to set
	 */
	public void setRecommendedLoi(ScLoi recommendedLoi) {
		this.recommendedLoi = recommendedLoi;
	}

	/**
	 * Gets the selected sc loi reports.
	 *
	 * @return the selectedScLoiReports
	 */
	public List<ScLoiReport> getSelectedScLoiReports() {
		return selectedScLoiReports;
	}

	/**
	 * Sets the selected sc loi reports.
	 *
	 * @param selectedScLoiReports the selectedScLoiReports to set
	 */
	public void setSelectedScLoiReports(List<ScLoiReport> selectedScLoiReports) {
		this.selectedScLoiReports = selectedScLoiReports;
	}

	/**
	 * @return the deletedScLoiReports
	 */
	public List<ScLoiReport> getDeletedScLoiReports() {
		return deletedScLoiReports;
	}

	/**
	 * @param deletedScLoiReports
	 *            the deletedScLoiReports to set
	 */
	public void setDeletedScLoiReports(List<ScLoiReport> deletedScLoiReports) {
		this.deletedScLoiReports = deletedScLoiReports;
	}

	/**
	 * Gets the selected sc loi report.
	 *
	 * @return the selectedScLoiReport
	 */
	public ScLoiReport getSelectedScLoiReport() {
		return selectedScLoiReport;
	}

	/**
	 * Sets the selected sc loi report.
	 *
	 * @param selectedScLoiReport the selectedScLoiReport to set
	 */
	public void setSelectedScLoiReport(ScLoiReport selectedScLoiReport) {
		this.selectedScLoiReport = selectedScLoiReport;
	}

	/**
	 * Gets the attachment doc type.
	 *
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}

	/**
	 * Sets the attachment doc type.
	 *
	 * @param attachmentDocType the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
	}

	/**
	 * Gets the sc qt.
	 *
	 * @return the sc qt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * Sets the sc qt.
	 *
	 * @param scQt the new sc qt
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * Gets the selected sc loi supp doc.
	 *
	 * @return the selected sc loi supp doc
	 */
	public ScLoiSupportingDoc getSelectedScLoiSuppDoc() {
		return selectedScLoiSuppDoc;
	}

	/**
	 * Sets the selected sc loi supp doc.
	 *
	 * @param selectedScLoiSuppDoc the new selected sc loi supp doc
	 */
	public void setSelectedScLoiSuppDoc(ScLoiSupportingDoc selectedScLoiSuppDoc) {
		this.selectedScLoiSuppDoc = selectedScLoiSuppDoc;
	}

	/**
	 * Gets the selected sc loi supp docs.
	 *
	 * @return the selected sc loi supp docs
	 */
	public List<ScLoiSupportingDoc> getSelectedScLoiSuppDocs() {
		return selectedScLoiSuppDocs;
	}

	/**
	 * Sets the selected sc loi supp docs.
	 *
	 * @param selectedScLoiSuppDocs the new selected sc loi supp docs
	 */
	public void setSelectedScLoiSuppDocs(List<ScLoiSupportingDoc> selectedScLoiSuppDocs) {
		this.selectedScLoiSuppDocs = selectedScLoiSuppDocs;
	}

	
	/**
	 * Gets the recommend list.
	 *
	 * @return the recommendList
	 */
	public List<ScQtRecommend> getRecommendList() {
		return recommendList;
	}

	/**
	 * Sets the recommend list.
	 *
	 * @param recommendList the recommendList to set
	 */
	public void setRecommendList(List<ScQtRecommend> recommendList) {
		this.recommendList = recommendList;
	}

	/**
	 * Gets the selected recommend list.
	 *
	 * @return the selectedRecommendList
	 */
	public List<ScQtRecommend> getSelectedRecommendList() {
		return selectedRecommendList;
	}

	/**
	 * Sets the selected recommend list.
	 *
	 * @param selectedRecommendList the selectedRecommendList to set
	 */
	public void setSelectedRecommendList(List<ScQtRecommend> selectedRecommendList) {
		this.selectedRecommendList = selectedRecommendList;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Checks if is edits the flag.
	 *
	 * @return true, if is edits the flag
	 */
	public boolean isEditFlag() {
		return editFlag;
	}

	/**
	 * Sets the edits the flag.
	 *
	 * @param editFlag the new edits the flag
	 */
	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	/**
	 * Gets the streamed content.
	 *
	 * @return the streamed content
	 */
	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	/**
	 * Sets the streamed content.
	 *
	 * @param streamedContent the new streamed content
	 */
	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	/**
	 * Gets the award supplier id.
	 *
	 * @return the awardSupplierId
	 */
	public Long getAwardSupplierId() {
		return awardSupplierId;
	}

	/**
	 * Sets the award supplier id.
	 *
	 * @param awardSupplierId the awardSupplierId to set
	 */
	public void setAwardSupplierId(Long awardSupplierId) {
		this.awardSupplierId = awardSupplierId;
	}

	/**
	 * Gets the committee type.
	 *
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * Sets the committee type.
	 *
	 * @param committeeType the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * Checks if is multiple loi flag.
	 *
	 * @return true, if is multiple loi flag
	 */
	public boolean isMultipleLoiFlag() {
		return multipleLoiFlag;
	}

	/**
	 * Sets the multiple loi flag.
	 *
	 * @param multipleLoiFlag the new multiple loi flag
	 */
	public void setMultipleLoiFlag(boolean multipleLoiFlag) {
		this.multipleLoiFlag = multipleLoiFlag;
	}

	/**
	 * Checks if is item details flag.
	 *
	 * @return true, if is item details flag
	 */
	public boolean isItemDetailsFlag() {
		return itemDetailsFlag;
	}

	/**
	 * Sets the item details flag.
	 *
	 * @param itemDetailsFlag the new item details flag
	 */
	public void setItemDetailsFlag(boolean itemDetailsFlag) {
		this.itemDetailsFlag = itemDetailsFlag;
	}

	/**
	 * Checks if is hide column flag.
	 *
	 * @return true, if is hide column flag
	 */
	public boolean isHideColumnFlag() {
		return hideColumnFlag;
	}

	/**
	 * Sets the hide column flag.
	 *
	 * @param hideColumnFlag the new hide column flag
	 */
	public void setHideColumnFlag(boolean hideColumnFlag) {
		this.hideColumnFlag = hideColumnFlag;
	}

	/**
	 * Gets the back to previous page.
	 *
	 * @return the back to previous page
	 */
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}

	/**
	 * Sets the back to previous page.
	 *
	 * @param backToPreviousPage the new back to previous page
	 */
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}

	/**
	 * Gets the terms conditions.
	 *
	 * @return the termsConditions
	 */
	public String getTermsConditions() {
		return termsConditions;
	}

	/**
	 * Sets the terms conditions.
	 *
	 * @param termsConditions the termsConditions to set
	 */
	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
	}

	/**
	 * Checks if is button flag.
	 *
	 * @return the buttonFlag
	 */
	public boolean isButtonFlag() {
		return buttonFlag;
	}

	/**
	 * Sets the button flag.
	 *
	 * @param buttonFlag the buttonFlag to set
	 */
	public void setButtonFlag(boolean buttonFlag) {
		this.buttonFlag = buttonFlag;
	}

	/**
	 * Gets the clauses title.
	 *
	 * @return the clauses title
	 */
	public String getClausesTitle() {
		return clausesTitle;
	}

	/**
	 * Sets the clauses title.
	 *
	 * @param clausesTitle the new clauses title
	 */
	public void setClausesTitle(String clausesTitle) {
		this.clausesTitle = clausesTitle;
	}

	/**
	 * Gets the loi loa list title.
	 *
	 * @return the loi loa list title
	 */
	public String getLoiLoaListTitle() {
		return loiLoaListTitle;
	}

	/**
	 * Sets the loi loa list title.
	 *
	 * @param loiLoaListTitle the new loi loa list title
	 */
	public void setLoiLoaListTitle(String loiLoaListTitle) {
		this.loiLoaListTitle = loiLoaListTitle;
	}

	/**
	 * @return the itemized
	 */
	public boolean isItemized() {
		return itemized;
	}

	/**
	 * @param itemized 
	 * 			the itemized to set
	 */
	public void setItemized(boolean itemized) {
		this.itemized = itemized;
	}

	/**
	 * Checks if is loi required.
	 *
	 * @return true, if is loi required
	 */
	public boolean isLoiRequired() {
		return loiRequired;
	}

	/**
	 * Sets the loi required.
	 *
	 * @param loiRequired the new loi required
	 */
	public void setLoiRequired(boolean loiRequired) {
		this.loiRequired = loiRequired;
	}

	/**
	 * @return the recommendedSupplierList
	 */
	public List<ScQtEvaluationResultDtl> getRecommendedSupplierList() {
		return recommendedSupplierList;
	}

	/**
	 * @param recommendedSupplierList 
	 * 				the recommendedSupplierList to set
	 */
	public void setRecommendedSupplierList(
			List<ScQtEvaluationResultDtl> recommendedSupplierList) {
		this.recommendedSupplierList = recommendedSupplierList;
	}

	/**
	 * @return the selectedSupplier
	 */
	public ScQtEvaluationResultDtl getSelectedSupplier() {
		return selectedSupplier;
	}

	/**
	 * @param selectedSupplier 
	 * 				the selectedSupplier to set
	 */
	public void setSelectedSupplier(ScQtEvaluationResultDtl selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	/**
	 * Gets the role code.
	 *
	 * @return the role code
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * Sets the role code.
	 *
	 * @param roleCode the new role code
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the multiLOIZoneDetails
	 */
	public List<TplSpecZoneDtl> getMultiLOIZoneDetails() {
		return multiLOIZoneDetails;
	}

	/**
	 * @param multiLOIZoneDetails 
	 * 				the multiLOIZoneDetails to set
	 */
	public void setMultiLOIZoneDetails(
					List<TplSpecZoneDtl> multiLOIZoneDetails) {
		this.multiLOIZoneDetails = multiLOIZoneDetails;
	}

	/**
	 * @return the zoneMap
	 */
	public Map<Long,TplSpecZoneDtl> getZoneMap() {
		return zoneMap;
	}

	/**
	 * @param zoneMap 
	 * 			the zoneMap to set
	 */
	public void setZoneMap(Map<Long,TplSpecZoneDtl> zoneMap) {
		this.zoneMap = zoneMap;
	}

	/**
	 * @return the scQtRecommendsByZone
	 */
	public Map<Long, List<ScQtRecommend>> getScQtRecommendsByZone() {
		return scQtRecommendsByZone;
	}

	/**
	 * @param scQtRecommendsByZone 
	 * 					the scQtRecommendsByZone to set
	 */
	public void setScQtRecommendsByZone(
			Map<Long, List<ScQtRecommend>> scQtRecommendsByZone) {
		this.scQtRecommendsByZone = scQtRecommendsByZone;
	}

	/**
	 * @return the loiLoaItemsMap
	 */
	public Map<Long, List<ScLoiLoaItem>> getLoiLoaItemsMap() {
		return loiLoaItemsMap;
	}

	/**
	 * @param loiLoaItemsMap 
	 * 				the loiLoaItemsMap to set
	 */
	public void setLoiLoaItemsMap(
					Map<Long, List<ScLoiLoaItem>> loiLoaItemsMap) {
		this.loiLoaItemsMap = loiLoaItemsMap;
	}

	/**
	 * @return the supplierLoiLoaItemsMap
	 */
	public Map<Long, List<ScLoiLoaItem>> getSupplierLoiLoaItemsMap() {
		return supplierLoiLoaItemsMap;
	}

	/**
	 * @param supplierLoiLoaItemsMap 
	 * 				the supplierLoiLoaItemsMap to set
	 */
	public void setSupplierLoiLoaItemsMap(
			Map<Long, List<ScLoiLoaItem>> supplierLoiLoaItemsMap) {
		this.supplierLoiLoaItemsMap = supplierLoiLoaItemsMap;
	}

	/**
	 * @return the zoneActiveIndex
	 */
	public int getZoneActiveIndex() {
		return ZoneActiveIndex;
	}

	/**
	 * @param zoneActiveIndex 
	 * 				the zoneActiveIndex to set
	 */
	public void setZoneActiveIndex(int zoneActiveIndex) {
		ZoneActiveIndex = zoneActiveIndex;
	}

	/**
	 * @return the zoneMenuModel
	 */
	public MenuModel getZoneMenuModel() {
		return zoneMenuModel;
	}

	/**
	 * @param zoneMenuModel 
	 * 				the zoneMenuModel to set
	 */
	public void setZoneMenuModel(MenuModel zoneMenuModel) {
		this.zoneMenuModel = zoneMenuModel;
	}

	/**
	 * @return the selectedZone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * @param selectedZone 
	 * 				the selectedZone to set
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * @return the isSecretaryUser
	 */
	public boolean isSecretaryUser() {
		return isSecretaryUser;
	}

	/**
	 * @param isSecretaryUser 
	 * 				the isSecretaryUser to set
	 */
	public void setSecretaryUser(boolean isSecretaryUser) {
		this.isSecretaryUser = isSecretaryUser;
	}

	/**
	 * @return the zonal
	 */
	public boolean isZonal() {
		return zonal;
	}

	/**
	 * @param zonal the zonal to set
	 */
	public void setZonal(boolean zonal) {
		this.zonal = zonal;
	}

	/**
	 * @return the deletedMeetingAttendanceList
	 */
	public List<ScQtMeetingAttendance> getDeletedMeetingAttendanceList() {
		return deletedMeetingAttendanceList;
	}

	/**
	 * @param deletedMeetingAttendanceList 
	 * 				the deletedMeetingAttendanceList to set
	 */
	public void setDeletedMeetingAttendanceList(
			List<ScQtMeetingAttendance> deletedMeetingAttendanceList) {
		this.deletedMeetingAttendanceList = deletedMeetingAttendanceList;
	}

	/**
	 * @return the selectedLoiLoaBean
	 */
	public LoiLoaBean getSelectedLoiLoaBean() {
		return selectedLoiLoaBean;
	}

	/**
	 * @param selectedLoiLoaBean the selectedLoiLoaBean to set
	 */
	public void setSelectedLoiLoaBean(LoiLoaBean selectedLoiLoaBean) {
		this.selectedLoiLoaBean = selectedLoiLoaBean;
	}

	/**
	 * @return the refinalizedBidding
	 */
	public boolean isRefinalizedBidding() {
		return refinalizedBidding;
	}

	/**
	 * @param refinalizedBidding the refinalizedBidding to set
	 */
	public void setRefinalizedBidding(boolean refinalizedBidding) {
		this.refinalizedBidding = refinalizedBidding;
	}
	

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the qtCancelRequested
	 */
	public boolean isQtCancelRequested() {
		return qtCancelRequested;
	}

	/**
	 * @param qtCancelRequested the qtCancelRequested to set
	 */
	public void setQtCancelRequested(boolean qtCancelRequested) {
		this.qtCancelRequested = qtCancelRequested;
	}
	
	
	
}
