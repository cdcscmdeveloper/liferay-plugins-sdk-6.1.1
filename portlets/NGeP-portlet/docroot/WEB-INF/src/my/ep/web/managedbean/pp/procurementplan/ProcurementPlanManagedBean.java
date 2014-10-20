package my.ep.web.managedbean.pp.procurementplan;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "pPlanManagedBean")
@SessionScoped
public class ProcurementPlanManagedBean extends BaseManagedBean{

	private List<String> modeList; //TODO

	/**
	 * @return the modeList
	 */
	public List<String> getModeList() {
		if(modeList==null)
			modeList = new ArrayList<String>();
		modeList.add("REVISION");
		modeList.add("APPROVAL");
		modeList.add("VERIFICATION");
		modeList.add("VIEW");
		modeList.add("NEW");
		return modeList;
	}

	/**
	 * @param modeList the modeList to set
	 */
	public void setModeList(List<String> modeList) {
		this.modeList = modeList;
	}
	

}
