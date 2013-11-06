package my.ep.web.backingbean.ss.committee;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.ss.committe.ICommitteSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.ss.committe.CommitteReqDTO;
import my.ep.dto.ss.committe.CommitteResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;
import my.ep.web.type.ss.quationtender.CommitteType;
import my.ep.web.type.ss.quationtender.IdentificationType;
import my.ep.web.type.ss.quationtender.MemberRole;
import my.ep.web.type.ss.quationtender.MemberType;
import my.ep.web.type.ss.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.ptg.MemErrPtg;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeBackingBean")
@RequestScoped
public class CommitteBackingBean extends BaseBackingBean<CommitteManagedBean> {
	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);
	private static final String MODULE_NAME = "ss";

	private static final String CREATE_COMMITTE = "committeemaintanancepage";

	private ICommitteSO committeSO;

	public CommitteBackingBean() {
		logger.info("inside committe backing bean constructor");

	}

	@PostConstruct
	public void initialize() {

		committeSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, ICommitteSO.class);
		logger.info("Successfully created So object for Committe" + committeSO);
	}

	/**
	 * Method to save the committe details.
	 */
	public void saveCommitte() {
		// TODO Need to get the committe object from managed bean.
		ScQtCommittee committee = new ScQtCommittee();

		// Need to remove below 3 properties
		committee.setChangedBy(1l);
		committee.setCreatedBy(1l);
		committee.setCreatedDate(new Timestamp(System.currentTimeMillis()));

		ScCommittee scCommittee = getManagedBean().getScCommittee();
		// Need to remove below properties
		scCommittee.setChangedBy(10l);

		scCommittee.setCommitteeType("TEC");
		scCommittee.setCreatedBy(1l);
		scCommittee.setCreatedDate(new Timestamp(100l));
		scCommittee.setRecordStatus(Boolean.TRUE);
		scCommittee.setIsApprovalReq(Boolean.TRUE);
		scCommittee.setStatusId(1);
		List<ScCommitteeMember> member = getManagedBean().getCommittemembers();
		List<ScCommitteeMember> committeMembers = new ArrayList<ScCommitteeMember>();
		for (ScCommitteeMember commMember : member) {
			commMember.setChangedBy(1l);
			commMember.setCreatedBy(1l);
			commMember
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));

			committeMembers.add(commMember);

		}
		scCommittee.setScCommitteeMembers(committeMembers);

		committee.setScCommittee(scCommittee);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtCommittee(committee);
		CommitteResDTO resDTO = committeSO.saveOrUpdateCommitte(committeReqDTO);

	}

	public void sarchQtCommitte() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		this.getManagedBean().setQtcommittees(qtCommittee);
	}

	private List<ScQtCommittee> retriveQtCommittes() {
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean = new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto = null;

		SearchFilterBean equalSearchBean = new SearchFilterBean(
				SearchOperator.OPERATOR_EQUALS.value());

		equalSearchBean.setSearchField("scCommittee.committeeId");
		if (getManagedBean().getCommitteNo() != null) {
			equalSearchBean.setSearchValue(getManagedBean().getCommitteNo());
			searchBean.getDataFilterBeans().add(equalSearchBean);

		}

		if (getManagedBean().getSelectedCommitte() != null) {
			equalSearchBean.setSearchField("scCommittee.committeeType");
			equalSearchBean.setSearchValue(getManagedBean()
					.getSelectedCommitte());
			searchBean.getDataFilterBeans().add(equalSearchBean);
		}

		if (getManagedBean().getCreatedDt() != null) {
			equalSearchBean.setSearchField("scCommittee.createdDate");
			equalSearchBean.setSearchValue(getManagedBean().getCreatedDt());
			searchBean.getDataFilterBeans().add(equalSearchBean);
		}

		if (getManagedBean().getQtNo() != null) {
			equalSearchBean.setSearchField("scQt.qtNo");
			equalSearchBean.setSearchValue(getManagedBean().getQtNo());

			searchBean.getDataFilterBeans().add(equalSearchBean);
		}
		committeReqDTO.setCommitteSearchBean(searchBean);

		try {
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("ss", ICommitteSO.class);
			resDto = committeSO.searchQTCommittees(committeReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resDto.getCommitteResultBean().getResultList();
	}

	private void populateCommitteType() {

		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add(new SelectItem("", "Select One"));
		CommitteType[] committeType = CommitteType.values();
		for (CommitteType mode : committeType) {
			items.add(new SelectItem(mode.value(), mode.name()));
		}
		this.getManagedBean().setCommitteType(items);
	}

	public void viewQtDetail() {
		System.out
				.println("&&&&&&&&&&&&&&&&&inside view qt details method&&&&&&&&&&&&&&&&&");
	}

	/**
	 * Method to provide nessasary info to commitperson
	 */
	public void showCommitteMember() {
		System.out
				.println("@@@@@@@@@@@@@@@@@@@@@inside show committemember screen");

		//this.getManagedBean().setCommitteMember(new ScCommitteeMember());
	}

	public void addCommitteMember() {
		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$");
		List<ScCommitteeMember> committeMemebers = getManagedBean()
				.getCommittemembers();
		if (committeMemebers == null) {
			committeMemebers = new ArrayList<ScCommitteeMember>();
		}
		committeMemebers.add(getManagedBean().getCommitteMember());
		getManagedBean().setCommittemembers(committeMemebers);
		
		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$"
				+ getManagedBean().getCommittemembers().size());
	}

	/**
	 * Method to navigate to create committepage.
	 * 
	 * @return
	 */
	public String createCommittee() {

		getManagedBean().setScCommittee(new ScCommittee());
		//getManagedBean().setCommitteMember(new ScCommitteeMember());
		populateCommitteType();
		retriveCommitteDetails();
		getSelectionTypes();
		getUserTypes();
		getRoles();
		getIdTypes();
		getMinistryList();
		return CREATE_COMMITTE;
	}

	private void retriveCommitteDetails() {

	}

	private void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}

	private void getUserTypes() {
		MemberType[] type = MemberType.values();
		List<SelectItem> userTypes = new ArrayList<SelectItem>();

		for (MemberType memberType : type) {
			userTypes
					.add(new SelectItem(memberType.value(), memberType.name()));
		}
		this.getManagedBean().setUserTypes(userTypes);
	}

	private void getRoles() {
		MemberRole[] roles = MemberRole.values();
		List<SelectItem> role = new ArrayList<SelectItem>();
		role.add(new SelectItem("", "SelectOne"));
		for (MemberRole memberRole : roles) {
			role.add(new SelectItem(memberRole.value(), memberRole.name()));
		}
		this.getManagedBean().setRoles(role);
	}

	private void getIdTypes() {
		IdentificationType identificationType[] = IdentificationType.values();
		List<SelectItem> idtypes = new ArrayList<SelectItem>();
		idtypes.add(new SelectItem("", "SelectOne"));
		for (IdentificationType type : identificationType) {
			idtypes.add(new SelectItem(type.value(), type.name()));
		}
		this.getManagedBean().setIdTypes(idtypes);
	}

	private void getMinistryList() {
		IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
		profileReqDTO.setOrgType(OrganizationType.MINISTRY.value());
		profileReqDTO.setParentOrgProfileId(null);
		if (this.getManagedBean().getMinstryList() == null) {
			OrgProfileResDTO orgResDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(profileReqDTO);
			List<PmOrgProfile> minstryList = orgResDTO.getOrgProfileList();
			prepareMinistryList(minstryList);
		}
	}

	private void prepareMinistryList(List<PmOrgProfile> orgProfile) {
		List<SelectItem> minstryList = new ArrayList<SelectItem>();
		minstryList.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgProfile) {
			minstryList.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}

		this.getManagedBean().setMinstryList(minstryList);
	}

	public void fetchDepartements() {
		System.out.println("**************inside fetchDepartments"
				+ getManagedBean().getCommitteMember().getMinistry());
		String minstrycode = getManagedBean().getCommitteMember().getMinistry();
		if (null == minstrycode || "".equals(minstrycode)) {
			throw new IllegalArgumentException();
		} else {
			Long parentId = new Long(minstrycode);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			reqDTO.setParentOrgProfileId(parentId);
			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			prepareDeptList(resDTO.getOrgProfileList());
		}
	}

	private void prepareDeptList(List<PmOrgProfile> orgprofiles) {
		List<SelectItem> deptcodes = new ArrayList<SelectItem>();
		deptcodes.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgprofiles) {
			deptcodes.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}
		this.getManagedBean().setDeptList(deptcodes);
	}

	public void fetchDivisions() {
		System.out.println("**************inside fetchDepartments"
				+ getManagedBean().getCommitteMember().getDepartment());
		String minstrycode = getManagedBean().getCommitteMember()
				.getDepartment();
		if (null == minstrycode || "".equals(minstrycode)) {
			throw new IllegalArgumentException();
		} else {
			Long parentId = new Long(minstrycode);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(parentId);
			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			preparedDivisions(resDTO.getOrgProfileList());
		}
	}

	private void preparedDivisions(List<PmOrgProfile> orgprofiles) {
		List<SelectItem> devisions = new ArrayList<SelectItem>();
		devisions.add(new SelectItem("", "Select One"));
		for (PmOrgProfile pmOrgProfile : orgprofiles) {
			devisions.add(new SelectItem(pmOrgProfile.getOrgProfileId()
					.toString(), pmOrgProfile.getOrgCode()));
		}
		this.getManagedBean().setDivisionList(devisions);
	}

}
