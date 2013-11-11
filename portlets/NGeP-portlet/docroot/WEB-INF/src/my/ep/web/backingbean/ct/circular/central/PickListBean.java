package my.ep.web.backingbean.ct.circular.central;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmUser;

import org.primefaces.model.DualListModel;

public class PickListBean {

	private DualListModel<String> approverList;  
	//private Logger logger = Logger.getLogger(PickListBean.class);
    public PickListBean() {  
    }
    
    public PickListBean(List<PmUser> lst) {  

    	//Cities  
        List<String> approverListSource = new ArrayList<String>();  
        List<String> approverListTarget = new ArrayList<String>();  
        if(lst!=null)
        {
	        for(PmUser ds: lst)
	        {
	        	//logger.info("^^^^^^^^^^^^^^^^^^^^^^^ds.getName() is "+ds.getName());
	          //  if(ds instanceof RelationalDataSource){    	
	            	approverListSource.add(ds.getUserName());
	           // }
	        }
        }
  
        approverList = new DualListModel<String>(approverListSource, approverListTarget);  
    }

	public DualListModel<String> getApproverList() {
		return approverList;
	}

	public void setApproverList(DualListModel<String> approverList) {
		this.approverList = approverList;
	}

}
