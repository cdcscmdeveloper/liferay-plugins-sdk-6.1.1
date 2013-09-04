package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.RemarksVO;

@ManagedBean(name = "unspscCodeMB")
@SessionScoped
public class UnspscCodeManagedBean extends BaseManagedBean {
	
	
	private String cmNavigationURL;
	
	private List<RemarksVO> unspscList;
	private RemarksVO selectedUNSPSCObj;
	
	
	
	
	
	

	

	public String getCmNavigationURL() {
		return cmNavigationURL;
	}

	public void setCmNavigationURL(String cmNavigationURL) {
		this.cmNavigationURL = cmNavigationURL;
	}

	public List<RemarksVO> getUnspscList() {
		if(unspscList==null){
			unspscList = new ArrayList<RemarksVO>();
		}
		return unspscList;
	}

	public void setUnspscList(List<RemarksVO> unspscList) {
		this.unspscList = unspscList;
	}

	public RemarksVO getSelectedUNSPSCObj() {
		if(selectedUNSPSCObj==null)
			selectedUNSPSCObj = new RemarksVO();
		return selectedUNSPSCObj;
	}

	public void setSelectedUNSPSCObj(RemarksVO selectedUNSPSCObj) {
		this.selectedUNSPSCObj = selectedUNSPSCObj;
	}
	
	
	
	

}
