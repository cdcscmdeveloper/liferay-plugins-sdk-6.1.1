package my.ep.web.backingbean.ss.committee;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.committe.ICommitteSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.committe.CommitteReqDTO;
import my.ep.dto.ss.committe.CommitteResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.type.ss.quationtender.CommitteType;
import my.ep.web.type.ss.quationtender.IdentificationType;
import my.ep.web.type.ss.quationtender.MemberRole;
import my.ep.web.type.ss.quationtender.MemberType;
import my.ep.web.type.ss.quationtender.SelectionType;

import org.apache.log4j.Logger;

/**
 * @author VidyasagarReddy
 * 
 */
@ManagedBean(name = "committeBackingBean")
@RequestScoped
public class CommitteBackingBean extends
		SourcingBaseBackingBean<CommitteManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(CommitteBackingBean.class);

	public CommitteBackingBean() {
		retriveCommitteTypes();
	}

	/**
	 * Method to save the committe details.
	 */
	public void saveCommitteDetails() {
		// TODO Need to get the committe object from managed bean.
		
			saveCommitte();
	}
	/**
	 * Method to save and submit the data to BPM
	 */
	public void submitCommitteDetails(){
		CommitteResDTO committeResDTO = saveCommitte();
		ICommitteSO commso=EpEjbBusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		CommitteReqDTO reqDTO= new CommitteReqDTO();
		reqDTO.setCommittee(committeResDTO.getCommittee().getScCommittee());
		commso.submitCommitteeDetails(reqDTO);
	}

	private CommitteResDTO saveCommitte(){
		ScQtCommittee committee = getManagedBean().getQtCommittee();

		String tenderNumber = getManagedBean().getTenderNum();
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
		if(member !=null){
		List<ScCommitteeMember> committeMembers = new ArrayList<ScCommitteeMember>();
		for (ScCommitteeMember commMember : member) {
			commMember.setChangedBy(1l);
			commMember.setCreatedBy(1l);
			commMember
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			commMember.setScCommittee(scCommittee);
			committeMembers.add(commMember);
			

		}
		
		scCommittee.setScCommitteeMembers(committeMembers);
		}
		committee.setScCommittee(scCommittee);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtCommittee(committee);
		committeReqDTO.setQtNumber(tenderNumber);		
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
		CommitteResDTO resDTO = committeSO.saveOrUpdateCommitte(committeReqDTO);
		
		return resDTO;
	}
	/**
	 * To search committes
	 */
	public void sarchQtCommitte() {
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		this.getManagedBean().setQtcommittees(qtCommittee);
	}

	/**
	 * retrive qtcommittees
	 * 
	 * @return
	 */
	private List<ScQtCommittee> retriveQtCommittes() {
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean = new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto = null;

		

		if (getManagedBean().getCommitteNo() != null && !"".equalsIgnoreCase(getManagedBean().getCommitteNo())) {
			SearchFilterBean committeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeSearchBean.setSearchField("scCommittee.committeeId");
			committeSearchBean.setSearchValue(getManagedBean().getCommitteNo());
			searchBean.getDataFilterBeans().add(committeSearchBean);

		}

		if (getManagedBean().getSelectedCommitte() != null && !"".equalsIgnoreCase(getManagedBean().getSelectedCommitte())) {
			SearchFilterBean committeTypeSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			committeTypeSearchBean.setSearchField("scCommittee.committeeType");
			committeTypeSearchBean.setSearchValue(getManagedBean()
					.getSelectedCommitte());
			searchBean.getDataFilterBeans().add(committeTypeSearchBean);
		}

		if (getManagedBean().getCreatedDt() != null) {
			SearchFilterBean creatDtSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			creatDtSearchBean.setSearchField("scCommittee.createdDate");
			creatDtSearchBean.setSearchValue(getManagedBean().getCreatedDt());
			searchBean.getDataFilterBeans().add(creatDtSearchBean);
		}

		if (getManagedBean().getQtNo() != null && !"".equalsIgnoreCase(getManagedBean().getQtNo())) {
			SearchFilterBean qtnoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			qtnoSearchBean.setSearchField("scQt.qtNo");
			qtnoSearchBean.setSearchValue(getManagedBean().getQtNo());

			searchBean.getDataFilterBeans().add(qtnoSearchBean);
		}
		if (getManagedBean().getStatus() != null && !"".equalsIgnoreCase(getManagedBean().getStatus())) {
			/*SearchFilterBean statusSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			statusSearchBean.setSearchField("scQt.qtNo");
			statusSearchBean.setSearchValue(getManagedBean().getQtNo());

			searchBean.getDataFilterBeans().add(statusSearchBean);*/
		}
		committeReqDTO.setCommitteSearchBean(searchBean);

		try {
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
			resDto = committeSO.searchQTCommittees(committeReqDTO);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resDto.getCommitteResultBean().getResultList();
	}

	
	public void viewQtDetail(Long qtId) {
	
		IQTDetailSO detailSO= EpEjbBusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		QuationTenderReqDTO reqDTO= new QuationTenderReqDTO();
		reqDTO.setQtDetailId(qtId);
		QuationTenderResDTO tenderResDTO =detailSO.retriveQtById(reqDTO);
		List<ScQt> qtList= new ArrayList<ScQt>();
		qtList.add(tenderResDTO.getQtDetail());
		getManagedBean().setScQt(qtList);
	}

	/**
	 * Method to provide nessasary info to commitperson
	 */
	public void showCommitteMember() {
		
		getManagedBean().setCommitteMember(new ScCommitteeMember());
		getSelectionTypes();
		getUserTypes();
		
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
		try {
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(QTConstants.SS_MODULE, ICommitteSO.class);
			CommitteResDTO committeResDTO = committeSO
					.retriveQTCommitteDetails(new CommitteReqDTO());
			getManagedBean().setScCommittee(committeResDTO.getCommittee().getScCommittee());
			if (getManagedBean().getCommitteType() == null) {
				retriveCommitteTypes();
			}
			
			getManagedBean().setApprovers(toUserSelectItem(committeResDTO.getCommitteApprovers()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return QTConstants.CREATE_COMMITTE;
	}

	/**
	 * 
	 */
	private void retriveCommitteTypes() {
		List<SelectItem> committeTypes = new ArrayList<SelectItem>();
		committeTypes.add(new SelectItem("", "SelectOne"));
		for (CommitteType type : CommitteType.values()) {
			committeTypes.add(new SelectItem(type.value(), type.name()));
		}
		this.getManagedBean().setCommitteType(committeTypes);

	}

	/**
	 * User selection types gov external new
	 */
	private void getSelectionTypes() {
		SelectionType[] type = SelectionType.values();
		List<SelectItem> selectionTypes = new ArrayList<SelectItem>();

		for (SelectionType selectionType : type) {
			selectionTypes.add(new SelectItem(selectionType.value(),
					selectionType.name()));
		}
		this.getManagedBean().setSelections(selectionTypes);
	}

	/**
	 * User type lie gov or non gov users
	 */
	private void getUserTypes() {
		MemberType[] type = MemberType.values();
		List<SelectItem> userTypes = new ArrayList<SelectItem>();

		for (MemberType memberType : type) {
			userTypes
					.add(new SelectItem(memberType.value(), memberType.name()));
		}
		this.getManagedBean().setUserTypes(userTypes);
	}

	/**
	 * Memeber roles like secreatey, chirperson, member
	 */
	private void getMemberRoles() {
		MemberRole[] roles = MemberRole.values();
		List<SelectItem> role = new ArrayList<SelectItem>();
		role.add(new SelectItem("", "SelectOne"));
		for (MemberRole memberRole : roles) {
			role.add(new SelectItem(memberRole.value(), memberRole.name()));
		}
		this.getManagedBean().setRoles(role);
	}

	/**
	 * Id type like ic or passport
	 */
	private void getIdTypes() {
		IdentificationType identificationType[] = IdentificationType.values();
		List<SelectItem> idtypes = new ArrayList<SelectItem>();
		idtypes.add(new SelectItem("", "SelectOne"));
		for (IdentificationType type : identificationType) {
			idtypes.add(new SelectItem(type.value(), type.name()));
		}
		this.getManagedBean().setIdTypes(idtypes);
	}

	/**
	 * Method to get the minstrylist based on with userid without userid
	 */
	private void getMinistryList() {

		OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
		// TODO:Need to set userid if govusers and external user
		profileReqDTO.setUserId(1l);
		profileReqDTO.setOrgType(OrganizationType.MINISTRY.value());
		profileReqDTO.setParentOrgProfileId(null);
		if (this.getManagedBean().getMinstryList() == null) {
			List<PmOrgProfile> orgProfiles = retriveOrgProfiles(profileReqDTO);
			List<SelectItem> minstryOrgProfiles = toOrgProfileSelectItems(orgProfiles);
			this.getManagedBean().setMinstryList(minstryOrgProfiles);
		}
	}

	/**
	 * On change of minstrycodes to get departments
	 */
	public void retriveDepartements() {
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

			List<PmOrgProfile> orgProfiles = retriveOrgProfiles(reqDTO);
			List<SelectItem> deptOrgProfiles = toOrgProfileSelectItems(orgProfiles);
			getManagedBean().setDeptList(deptOrgProfiles);
		}
	}

	/**
	 * On change of department get the divisions
	 */

	public void retriveDevisions() {
		System.out.println("**************inside fetchDepartments"
				+ getManagedBean().getCommitteMember().getDepartment());
		String deptCode = getManagedBean().getCommitteMember().getDepartment();
		if (null == deptCode || "".equals(deptCode)) {
			throw new IllegalArgumentException();
		} else {
			Long parentId = new Long(deptCode);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(parentId);
			List<PmOrgProfile> orgProfiles = retriveOrgProfiles(reqDTO);
			List<SelectItem> divisonOrgProfiles = toOrgProfileSelectItems(orgProfiles);
			getManagedBean().setDivisionList(divisonOrgProfiles);
		}
	}

	/**
	 * Method to retrive govusers
	 */
	public void retriveGovUsers() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				"pp", IUserSO.class);
		if (getManagedBean().getGovUsers() == null) {
			UserResDTO resDTO = userSO.retrieveGovUserList();
			List<PmUser> userList = resDTO.getUserList();
			List<SelectItem> govUsers = toUserSelectItem(userList);
			getManagedBean().setGovUsers(govUsers);
		}

	}

	/**
	 * Method to retrive externalusers
	 */
	public void retriveExternalUsers() {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				"pp", IUserSO.class);
		if (getManagedBean().getExternalUsers() == null) {
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setOrgType(OrganizationType.EXTERNAL.value());
			UserResDTO resDTO = userSO.retrieveUserListByOrgType(reqDTO);
			List<PmUser> externalUsers = resDTO.getUserList();
			List<SelectItem> extUsers = toUserSelectItem(externalUsers);
			getManagedBean().setExternalUsers(extUsers);
		}
	}

	private List<SelectItem> toUserSelectItem(List<PmUser> pmUsers) {
		List<SelectItem> users = new ArrayList<SelectItem>();
		users.add(new SelectItem("", "SelectOne"));
		for (PmUser user : pmUsers) {
			users.add(new SelectItem(user.getUserId(),user.getUserName()));
		}
		return users;
	}

}
