package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmDeleteCategoryCodeManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@ManagedBean(name = "deleteCategoryCodeBB")
@RequestScoped
public class SmDeleteCategoryCodeBackingBean extends
		BaseBackingBean<SmDeleteCategoryCodeManagedBean> implements Cloneable {

	public static Logger log = Logger
			.getLogger(SmDeleteCategoryCodeBackingBean.class);

	public SmDeleteCategoryCodeBackingBean() {
	}

	// @PostConstruct
	// public void init() {
	// System.out.println(" **** EXCUTE ON LOAD EVENT **********");
	// }

	public String getRegCategoryCodes() {
		try {
			System.out.println("**** ENTER On Page Load Event  *************");

			SmSupplier smApplSupplier = retriveSupplierById((long) 3)
					.getSmSupplier();
			if (smApplSupplier == null) {
				smApplSupplier = new SmSupplier();
				smApplSupplier.setSupplierName("My Sample Supplier");
				smApplSupplier.setBusinessType("1");
				smApplSupplier.setRegNo("12345");
				smApplSupplier.setEstablishDate(new Date());
				smApplSupplier.setWebSite("www.samplecompany.com");
			}
			System.out.println("Retrieved Value ==="
					+ smApplSupplier.getSupplierName() + " ==== tax num == "
					+ smApplSupplier.getIncomeTaxNo() + " === EST DATE ==== "
					+ smApplSupplier.getEstablishDate());
			System.out.println("******** SIZE ********** "
					+ smApplSupplier.getSmAppls().size());
			// TODO Get the latest application
			// SmAppl smAppl = smApplSupplier.getSmAppl();
			SmAppl smAppl = new SmAppl();
			List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
			for (int i = 0; i < 10; i++) {
				PmCategoryL1 parent = new PmCategoryL1();
				parent.setCategoryL1Code(String.valueOf(i + 1));
				parent.setCategoryName("Parent" + (i + 1));
				PmCategoryL2 clvl1 = new PmCategoryL2();
				clvl1.setCategoryL2Code(String.valueOf(i + 1));
				clvl1.setCategoryName("Child" + (i + 1));
				PmCategoryL3 sublvl1 = new PmCategoryL3();
				sublvl1.setCategoryL3Code(String.valueOf(i + 1));
				sublvl1.setCategoryName("Sub - Child" + (i + 1));
				SmSupplierCategory cat = new SmSupplierCategory();
				cat.setSupplierCategoryId((i + 1));
				if (i % 3 == 0) {
					cat.setCategoryType("S");
				} else {
					cat.setCategoryType("N");
				}

				cat.setPmCategoryL1(parent);
				cat.setPmCategoryL2(clvl1);
				cat.setPmCategoryL3(sublvl1);
				catList.add(cat);

			}

			smAppl.setSmSupplierCategories(catList);
			smAppl.setSmSupplier(smApplSupplier);
			getManagedBean().setSmSupplier(smApplSupplier);
			getManagedBean().setSmAppl(smAppl);
			return SupplierManagementConst.PROCEED_DELETE_CATCODES_APPLN;
		} catch (Exception e) {
			return SupplierManagementConst.DEFAULT_DELETE_CATCODES_APPLN;

		}

	}

	public void sampleTest(ComponentSystemEvent event) {
		System.out.println(" **** EXCUTE ON LOAD EVENT **********");
	}

	private MofRegistrationResDTO retriveSupplierById(long id) {
		IMofRegistrationSO mofRegistrationSO = null;

		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(id);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSupplierProfile(reqDto);
			return resDTO;

		} catch (Exception e) {
			return new MofRegistrationResDTO();
		} finally {

		}
	}

	public void onRowSelect(SelectEvent event) {
		System.out
				.println("*********** Enterd THE RoW SELECT METHOD ***********"
						+ getManagedBean().getSelectedCategories().size());
		manageSelectedRecords();

	}

	public void onRowUnSelect(UnselectEvent event) {
		System.out
				.println("*********** Enterd THE RoW UN SELECT METHOD ***********"
						+ getManagedBean().getSelectedCategories().size());
		manageSelectedRecords();

	}

	private void manageSelectedRecords() {
		if (getManagedBean().getSelectedCategories().size() != 0) {
			for (int i = 0; i < getManagedBean().getSmAppl()
					.getSmSupplierCategories().size(); i++) {
				SmSupplierCategory sdbTemp = getManagedBean().getSmAppl()
						.getSmSupplierCategories().get(i);
				for (SmSupplierCategory sdb : getManagedBean()
						.getSelectedCategories()) {
					if (sdbTemp.equals(sdb)) {
						sdbTemp.setSelected(Boolean.TRUE);
						System.out.println("*** ENTER *****");
						break;
					} else {
						sdbTemp.setRemarks("");
						sdbTemp.setSelected(Boolean.FALSE);
					}
				}
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.set(i, sdbTemp);
				System.out.println("Data List For Index = "
						+ i
						+ "  is "
						+ getManagedBean().getSmAppl()
								.getSmSupplierCategories().get(i).isSelected());
			}

		} else {
			for (int i = 0; i < getManagedBean().getSmAppl()
					.getSmSupplierCategories().size(); i++) {
				SmSupplierCategory sdbTemp = getManagedBean().getSmAppl()
						.getSmSupplierCategories().get(i);
				sdbTemp.setRemarks("");
				sdbTemp.setSelected(Boolean.FALSE);
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.set(i, sdbTemp);
			}
		}
	}

	public void deleteSelectedCatCodes() {
		try {
			int delListSize = getManagedBean().getSelectedCategories().size();
			System.out.println(" **** SIZE OF SELECTED *** " + delListSize);
			if (delListSize == getManagedBean().getSmAppl()
					.getSmSupplierCategories().size()) {
				getManagedBean().getSmAppl().getSmSupplierCategories().clear();
			} else {
				for (SmSupplierCategory selCat : getManagedBean()
						.getSelectedCategories()) {
					getManagedBean().getSmAppl().getSmSupplierCategories()
							.remove(selCat);

				}
			}
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									"",
									delListSize
											+ " - Category Code(s) Deleted Successfully"));
		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
		}

	}
}
