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
import my.ep.web.bean.sm.PersonnelBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.sm.MofRegistrationManagedBean;

@ManagedBean
@SessionScoped
public class MofRegistrationBackingBean extends
		BaseBackingBean<MofRegistrationManagedBean> implements Cloneable {

	private List<SupportingDocumentBean> dataList;
	private List<MofRegistrationUtilBean> utilList;
	private List<PersonnelBean> personList;
	private List<PersonnelBean> authList;
	private List<PersonnelBean> signList;

	public MofRegistrationBackingBean() {
		dataList = new ArrayList<SupportingDocumentBean>();
		utilList = new ArrayList<MofRegistrationUtilBean>();
		personList = new ArrayList<PersonnelBean>();
		authList = new ArrayList<PersonnelBean>();
		prepareDataList();
		prepareUtilList();
		preparePersonList();
		preparAuthList();
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

	public List<PersonnelBean> getPersonList() {
		return personList;
	}

	public void setPersonList(List<PersonnelBean> personList) {
		this.personList = personList;
	}

	public List<PersonnelBean> getAuthList() {
		return authList;
	}

	public void setAuthList(List<PersonnelBean> authList) {
		this.authList = authList;
	}

	public List<PersonnelBean> getSignList() {
		return signList;
	}

	public void setSignList(List<PersonnelBean> signList) {
		this.signList = signList;
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

	private List<PersonnelBean> preparePersonList() {
		personList
				.add(new PersonnelBean(1, "XXXX", Boolean.TRUE, Boolean.TRUE));
		personList.add(new PersonnelBean(2, "YYYY", Boolean.FALSE,
				Boolean.FALSE));
		personList
				.add(new PersonnelBean(3, "ZZZZ", Boolean.TRUE, Boolean.FALSE));

		return personList;

	}

	private List<PersonnelBean> preparAuthList() {
		authList.add(new PersonnelBean(1, "XXXX", Boolean.TRUE, Boolean.TRUE));

		authList.add(new PersonnelBean(3, "ZZZZ", Boolean.TRUE, Boolean.FALSE));

		return authList;

	}

	public List<PersonnelBean> getSignatoryList() {
		signList = new ArrayList<PersonnelBean>();
		signList.addAll(authList);
		System.out.println("ENTER THE ACTION CLASS");
		System.out.println("****** LIST ******" + signList.size());
		return signList;

	}

}