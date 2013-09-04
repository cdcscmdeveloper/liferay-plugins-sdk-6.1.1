/**
 * 
 */
package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.MofRegistrationUtilBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.sm.MofRegistrationManagedBean;

@ManagedBean
@SessionScoped
public class MofRegistrationBackingBean extends
		BaseBackingBean<MofRegistrationManagedBean> implements Cloneable {

	private List<SupportingDocumentBean> dataList;
	private List<MofRegistrationUtilBean> utilList;

	public MofRegistrationBackingBean() {
		dataList = new ArrayList<SupportingDocumentBean>();
		utilList = new ArrayList<MofRegistrationUtilBean>();
		prepareDataList();
		prepareUtilList();
	}

	public List<SupportingDocumentBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<SupportingDocumentBean> dataList) {
		this.dataList = dataList;
	}

	public List<MofRegistrationUtilBean> getUtilList() {
		return utilList;
	}

	public void setUtilList(List<MofRegistrationUtilBean> utilList) {
		this.utilList = utilList;
	}

	private List<SupportingDocumentBean> prepareDataList() {
		dataList.add(new SupportingDocumentBean("1", "Document1",
				"BORANG PENGISYTIHARAN ", Boolean.TRUE, "Document1",
				"Document1", "Y", "s", Boolean.TRUE));
		dataList.add(new SupportingDocumentBean("2", "Document2",
				"BORANG PENGISYTIHARAN KEMENTERIAN KEWANGAN YANG  ",
				Boolean.FALSE, "Document1", "Document1", "Y", "d",
				Boolean.FALSE));
		return dataList;

	}

	private List<MofRegistrationUtilBean> prepareUtilList() {
		utilList.add(new MofRegistrationUtilBean(1, "Sample Name"));

		return utilList;
	}

}