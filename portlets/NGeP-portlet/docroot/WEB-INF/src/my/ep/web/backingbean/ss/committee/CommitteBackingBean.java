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
import my.ep.biz.service.ss.committe.ICommitteSO;
import my.ep.dto.ss.committe.CommitteReqDTO;
import my.ep.dto.ss.committe.CommitteResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.committee.CommitteManagedBean;
import my.ep.web.type.ss.quationtender.CommitteType;
import my.ep.web.type.ss.quationtender.MemberType;
import my.ep.web.type.ss.quationtender.SelectionType;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument.Union.MemberTypes;

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

	private ICommitteSO committeSO;

	public CommitteBackingBean() {
		logger.info("inside committe backing bean constructor");
		populateCommitteType();
	}

	@PostConstruct
	public void initialize() {
		
		committeSO = BusinessDelegate.getInstance().getServiceObject(
				MODULE_NAME, ICommitteSO.class);
		logger.info("Successfully created So object for Committe"+committeSO);
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

		// TODO need to get the sccommitte object from the managed bean
		ScCommittee scCommittee = new ScCommittee();

		// Need to remove below properties
		scCommittee.setChangedBy(10l);
		scCommittee.setChangedDate(new Timestamp(10l));
		scCommittee.setCommitteeType("TEC");
		scCommittee.setCreatedBy(1l);
		scCommittee.setCreatedDate(new Timestamp(100l));
		scCommittee.setRecordStatus(Boolean.TRUE);
		scCommittee.setIsApprovalReq(Boolean.TRUE);
		scCommittee.setStatusId(1);
		committee.setScCommittee(scCommittee);

		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtCommittee(committee);
		CommitteResDTO resDTO = committeSO.saveOrUpdateCommitte(committeReqDTO);

	}
	
	public void sarchQtCommitte(){
		List<ScQtCommittee> qtCommittee = retriveQtCommittes();
		this.getManagedBean().setQtcommittees(qtCommittee);
	}
	
	private List<ScQtCommittee> retriveQtCommittes(){
		CommitteReqDTO committeReqDTO= new CommitteReqDTO();
		ISearchBean<ScQtCommittee> searchBean= new SearchBean<ScQtCommittee>();
		CommitteResDTO resDto=null;
		ScQtCommittee qtCommittee = getManagedBean().getQtCommittee();
		if(qtCommittee !=null){
		SearchFilterBean equalSearchBean= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		
		equalSearchBean.setSearchField("scCommittee.committeeId");
		
		equalSearchBean.setSearchValue(qtCommittee.getCommNumber());
	
		searchBean.getDataFilterBeans().add(equalSearchBean);
		
		equalSearchBean.setSearchField("scCommittee.committeeType");
		if(getManagedBean().getSelectedCommitte() !=null){
		equalSearchBean.setSearchValue(getManagedBean().getSelectedCommitte());
		}
		searchBean.getDataFilterBeans().add(equalSearchBean);
		
		equalSearchBean.setSearchField("scCommittee.createdDate");
		if(qtCommittee.getCommitteCreatedDate() !=null){
		equalSearchBean.setSearchValue(qtCommittee.getCommitteCreatedDate());
		}
		searchBean.getDataFilterBeans().add(equalSearchBean);
		
		equalSearchBean.setSearchField("scQt.qtNo");
	
		equalSearchBean.setSearchValue(qtCommittee.getQtno());
		
		searchBean.getDataFilterBeans().add(equalSearchBean);
		
		committeReqDTO.setCommitteSearchBean(searchBean);
		
		try{
		ICommitteSO committeSO=EpEjbBusinessDelegate.getInstance().getServiceObject("ss",ICommitteSO.class);
		 resDto=committeSO.searchQTCommittees(committeReqDTO);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		}
		return resDto.getCommitteResultBean().getResultList();
	}
	
	private void populateCommitteType(){
		
			List<SelectItem> items = new ArrayList<SelectItem>();
			items.add(new SelectItem("", "Select One"));
			CommitteType[] committeType = CommitteType.values();
			for (CommitteType mode : committeType) {
				items.add(new SelectItem(mode.value(), mode.name()));
			}
			this.getManagedBean().setCommitteType(items);
		}
	
	public void viewQtDetail(){
		System.out.println("&&&&&&&&&&&&&&&&&inside view qt details method&&&&&&&&&&&&&&&&&");
	}
	/**
	 * Method to provide nessasary info to commitperson
	 */
	public void showCommitteMember(){
		System.out.println("@@@@@@@@@@@@@@@@@@@@@inside show committemember screen");
		getManagedBean().setCommitteMember(new ScCommitteeMember());
		getSelectionType();
		getUserType();
		
	}
	
	public void addCommitteMember(){
		System.out.println("inside add committemeber$$$$$$$$$$$$$$$$$$$$$");
		List<ScCommitteeMember> committeMemebers= getManagedBean().getCommittemembers();
		if(committeMemebers == null){
			committeMemebers= new ArrayList<ScCommitteeMember>();
		}
		committeMemebers.add(getManagedBean().getCommitteMember());
		getManagedBean().setCommittemembers(committeMemebers);
	}
	
	private void getSelectionType(){
		
		List<SelectItem> type= new ArrayList<SelectItem>();
		type.add(new SelectItem("", "Select One"));
		SelectionType selectionTypes[]=SelectionType.values();
		for(SelectionType selType: selectionTypes){
			type.add(new SelectItem(selType.value(),selType.name()));
		}
		this.getManagedBean().setSelectionTypes(type);
	}
	
	private void getUserType(){
		List<SelectItem> userType= new ArrayList<SelectItem>();
		userType.add(new SelectItem("","Select One"));
		MemberType memberType[]=MemberType.values();
		for(MemberType type:memberType){
			userType.add(new SelectItem(type.value(),type.name()));
		}
		this.getManagedBean().setMemeberTypes(userType);
		
		
	}
}
