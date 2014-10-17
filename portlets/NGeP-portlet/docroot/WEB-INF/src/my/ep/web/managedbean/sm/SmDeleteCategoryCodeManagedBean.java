package my.ep.web.managedbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smDeleteCategoryCodeMB")
@SessionScoped
public class SmDeleteCategoryCodeManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SmAppl smAppl;

	private SmSupplier smSupplier;

	private List<SmSupplierCategory> selectedCategories = new ArrayList<SmSupplierCategory>();

	private String deleteConfirmMsg;

	private boolean termsAccepted;
	
	private List<SmSupplierCategory> activeCategoryList;

	public SmDeleteCategoryCodeManagedBean() {
		smSupplier = new SmSupplier();
		smAppl = new SmAppl();
		// getRegCategoryCodes();

	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public List<SmSupplierCategory> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(
			List<SmSupplierCategory> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	// private void getRegCategoryCodes() {
	//
	// System.out.println("**** ENTER On Page Load Event  *************");
	//
	// SmSupplier smApplSupplier = new SmSupplier();
	// // TODO Get the latest application
	// // SmAppl smAppl = smApplSupplier.getSmAppl();
	// SmAppl smAppl = new SmAppl();
	// List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
	// for (int i = 0; i < 15; i++) {
	// PmCategoryL1 parent = new PmCategoryL1();
	// parent.setCateogryL1Code(String.valueOf(i + 1));
	// parent.setCategoryName("Parent" + (i + 1));
	// PmCategoryL2 clvl1 = new PmCategoryL2();
	// clvl1.setCateogryL2Code(String.valueOf(i + 1));
	// clvl1.setCategoryName("Child" + (i + 1));
	// PmCategoryL3 sublvl1 = new PmCategoryL3();
	// sublvl1.setCateogryL3Code(String.valueOf(i + 1));
	// sublvl1.setCategoryName("Sub - Child" + (i + 1));
	// SmSupplierCategory cat = new SmSupplierCategory();
	// cat.setSupplierCategoryId((i + 1));
	// if (i % 3 == 0) {
	// cat.setCategoryType("S");
	// } else {
	// cat.setCategoryType("N");
	// }
	//
	// cat.setPmCategoryL1(parent);
	// cat.setPmCategoryL2(clvl1);
	// cat.setPmCategoryL3(sublvl1);
	// catList.add(cat);
	//
	// }
	//
	// smAppl.setSmSupplierCategories(catList);
	// smAppl.setSmSupplier(smApplSupplier);
	// setSmSupplier(smApplSupplier);
	// setSmAppl(smAppl);
	//
	// }

	public String getDeleteConfirmMsg() {
		return deleteConfirmMsg;
	}

	public void setDeleteConfirmMsg(String deleteConfirmMsg) {
		this.deleteConfirmMsg = deleteConfirmMsg;
	}

	public boolean isTermsAccepted() {
		return termsAccepted;
	}

	public void setTermsAccepted(boolean termsAccepted) {
		this.termsAccepted = termsAccepted;
	}

	public List<SmSupplierCategory> getActiveCategoryList() {
		return activeCategoryList;
	}

	public void setActiveCategoryList(List<SmSupplierCategory> activeCategoryList) {
		this.activeCategoryList = activeCategoryList;
	}

}
