/**
 * 
 */
package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.sm.MofRegistrationManagedBean;

@ManagedBean
@SessionScoped
public class MofRegistrationBackingBean extends
		BaseBackingBean<MofRegistrationManagedBean> implements Cloneable {

	private List<SupportingDocumentBean> dataList;

	public MofRegistrationBackingBean() {
		System.out.println("******** ENTER THE BACKING BEAN *********");
		dataList = new ArrayList<SupportingDocumentBean>();
		prepareDataList();
		System.out.println("******** LIST SIZE *********" + dataList.size());
	}

	public List<SupportingDocumentBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<SupportingDocumentBean> dataList) {
		this.dataList = dataList;
	}

	private List<SupportingDocumentBean> prepareDataList() {
		dataList.add(new SupportingDocumentBean("1", "Document1", "BORANG PENGISYTIHARAN ", Boolean.TRUE, "Document1", "Document1","Y","s",Boolean.TRUE));
		dataList.add(new SupportingDocumentBean("2", "Document2", "BORANG PENGISYTIHARAN KEMENTERIAN KEWANGAN YANG  ", Boolean.FALSE, "Document1", "Document1","Y","d",Boolean.FALSE));
		return dataList;

	}

}