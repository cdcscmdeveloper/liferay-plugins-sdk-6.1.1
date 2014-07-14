package my.ep.web.bean.cm;

import java.util.ArrayList;
import java.util.List;

public class DashSummaryViewBean {
	
	private String docTypeId;
	private List<DashSummaryStatus> dashSummaryStatusList;
	
	public String getDocTypeId() {
		return docTypeId;
	}
	public void setDocTypeId(String docTypeId) {
		this.docTypeId = docTypeId;
	}
	public List<DashSummaryStatus> getDashSummaryStatusList() {
		if(this.dashSummaryStatusList == null){
			this.dashSummaryStatusList = new ArrayList<DashSummaryStatus>();
		}
		return this.dashSummaryStatusList;
	}
	public void setDashSummaryStatusList(
			List<DashSummaryStatus> dashSummaryStatusList) {
		this.dashSummaryStatusList = dashSummaryStatusList;
	}
	
	
	
}
