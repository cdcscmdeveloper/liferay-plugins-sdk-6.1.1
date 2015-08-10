package my.ep.web.backingbean.cm.codification;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.cm.codification.RequestCodeManagedBean;

@ManagedBean(name = "requestCodeBackingBean")
@ViewScoped
public class RequestCodeBackingBean extends
BaseBackingBean<RequestCodeManagedBean> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	public void taskDetSearch() {

		this.getManagedBean().getRequestCodeSearchDataModel().reloadData();

	}
	
	
	
	
	
	
}
