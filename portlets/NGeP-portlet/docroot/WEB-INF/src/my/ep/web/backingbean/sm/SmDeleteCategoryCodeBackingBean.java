package my.ep.web.backingbean.sm;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmDeleteCategoryCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
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

			SmSupplier smApplSupplier = getCurrentSupplier();
			System.out.println("**** Supplier Id is ********** "
					+ smApplSupplier.getSupplierId());
			SmAppl smAppl = smApplSupplier.getSmAppl();

			if (smAppl != null) {
				System.out.println("******** The application id is *******"
						+ smAppl.getApplId());
				getManagedBean().setSmAppl(smAppl);
				retriveLatestApplAfterUpdate();

			} else {
				smAppl = new SmAppl();
				smAppl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
				getManagedBean().setSmAppl(smAppl);
			}

			getManagedBean().setSmSupplier(smApplSupplier);

			return SupplierManagementConst.PROCEED_DELETE_CATCODES_APPLN;
		} catch (Exception e) {
			e.printStackTrace();
			return SupplierManagementConst.DEFAULT_DELETE_CATCODES_APPLN;

		}

	}

	public void sampleTest(ComponentSystemEvent event) {
		System.out.println(" **** EXCUTE ON LOAD EVENT **********");
	}

	// private MofRegistrationResDTO retriveSupplierById(long id) {
	// IMofRegistrationSO mofRegistrationSO = null;
	//
	// try {
	// mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
	// .getInstance().getServiceObject("sm",
	// IMofRegistrationSO.class);
	// MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
	// reqDto.setSupplierId(id);
	// MofRegistrationResDTO resDTO = mofRegistrationSO
	// .retrieveSupplierProfile(reqDto);
	// return resDTO;
	//
	// } catch (Exception e) {
	// return new MofRegistrationResDTO();
	// } finally {
	//
	// }
	// }
	//
	// public void onRowSelect(SelectEvent event) {
	// System.out
	// .println("*********** Enterd THE RoW SELECT METHOD ***********"
	// + getManagedBean().getSelectedCategories().size());
	// manageSelectedRecords();
	//
	// }

	public void onToggleSelect(ToggleSelectEvent event) {
		System.out.println("*********** On Select or Unslect all ***********"
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
						// sdbTemp.setRemarks("");
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
				// sdbTemp.setRemarks("");
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
			updateSmAppl();
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

	private void updateSmAppl() {

		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);

			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();

			reqDto.setSmAppl(this.getManagedBean().getSmAppl());

			MofRegistrationResDTO resDTO = mofRegistrationSO
					.updateApplication(reqDto);
			retriveLatestApplAfterUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			retriveLatestApplAfterUpdate();
			mofRegistrationSO = null;
		}

	}

	private void retriveLatestApplAfterUpdate() {

		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", IMofRegistrationSO.class);
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(getManagedBean().getSmAppl().getApplId());

			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveCurrentDraftApplication(finReqDto);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mofRegistrationSO = null;
		}

	}

	private SmSupplier getCurrentSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
					.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return smSupp;
	}
}
