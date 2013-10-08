package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.RemarksVO;

@ManagedBean(name = "requestCodeMB")
@SessionScoped
public class UnspscCodeManagedBean extends BaseManagedBean {
	
	
	private CmRequestCode requestCode;

	public CmRequestCode getRequestCode() {
		if(requestCode==null)
			requestCode = new CmRequestCode();
		return requestCode;
	}

	public void setRequestCode(CmRequestCode requestCode) {
		this.requestCode = requestCode;
	}

	
	
	

}
