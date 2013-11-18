package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScLocationQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean
@SessionScoped
public class SimpleQuoteBidManagedBean extends BaseManagedBean{
	private	ScRequestList	scRequestList;
	private	ScQuoteBid		scQuoteBid;
	
	private	List<ScRequestListItem>	quoteBidItems = new ArrayList<ScRequestListItem>();
	private	List<ScBusinessNatureQuoteBidCriteria>	businessNatureCriterias	=	new ArrayList<ScBusinessNatureQuoteBidCriteria>();
	private	List<PmState>	states	=	new ArrayList<PmState>();
	
	private static final long serialVersionUID = 1L;

	public SimpleQuoteBidManagedBean() {
		prepareDummyData();
	}
	
	private	void	prepareDummyData(){
		//TODO	hard code dummy data, remove it later
		ScBusinessNatureQuoteBidCriteria	criteria1	=	new ScBusinessNatureQuoteBidCriteria();
		criteria1.setCriteriaValue("100201");
		businessNatureCriterias.add(criteria1);
	}
	
	
	public ScRequestList getScRequestList() {
		return scRequestList;
	}

	public void setScRequestList(ScRequestList scRequestList) {
		this.scRequestList = scRequestList;
	}

	public ScQuoteBid getScQuoteBid() {
		return scQuoteBid;
	}

	public void setScQuoteBid(ScQuoteBid scQuoteBid) {
		this.scQuoteBid = scQuoteBid;
	}

	public List<ScRequestListItem> getQuoteBidItems() {
		return quoteBidItems;
	}

	public void setQuoteBidItems(List<ScRequestListItem> quoteBidItems) {
		this.quoteBidItems = quoteBidItems;
	}

	public List<ScBusinessNatureQuoteBidCriteria> getBusinessNatureCriterias() {
		return businessNatureCriterias;
	}

	public void setBusinessNatureCriterias(List<ScBusinessNatureQuoteBidCriteria> businessNatureCriterias) {
		this.businessNatureCriterias = businessNatureCriterias;
	}

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
	}
	
}
