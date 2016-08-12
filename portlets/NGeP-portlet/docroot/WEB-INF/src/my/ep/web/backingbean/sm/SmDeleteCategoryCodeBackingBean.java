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
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmDeleteCategoryCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

// TODO: Auto-generated Javadoc
@ManagedBean(name = "deleteCategoryCodeBB")
@RequestScoped
public class SmDeleteCategoryCodeBackingBean extends
		BaseBackingBean<SmDeleteCategoryCodeManagedBean> implements Cloneable {

	public static Logger log = Logger
			.getLogger(SmDeleteCategoryCodeBackingBean.class);

	/**
	 * Instantiates a new sm delete category code backing bean.
	 */
	public SmDeleteCategoryCodeBackingBean() {
	}

	// @PostConstruct
	// public void init() {
	// log.info(" **** EXCUTE ON LOAD EVENT **********");
	// }

	/**
	 * Gets the list of registered category codes and assigns to the managed
	 * bean list.
	 * 
	 * @return String - navigation page.
	 */
	public String getRegCategoryCodes() {
		SmSupplier smApplSupplier = null;
		String retView = "";
		try {
			log.info("**** ENTER On Page Load Event  *************");
			smApplSupplier = getCurrentSupplier();
			log.info("**** Supplier Id is ********** "
					+ smApplSupplier.getSupplierId());
			// if (SmApplicationUtil.checkActiveSmApplForSupp(smApplSupplier)) {
			// throw new EpException(MessageConst.ERROR_722542);
			// }
			checkIsAllowedToProceed(smApplSupplier);
			SmAppl smAppl = smApplSupplier.getSmAppl();
			if (smAppl != null) {
				log.info("******** The application id is *******"
						+ smAppl.getApplId());
				getManagedBean().setSmAppl(smAppl);
				retriveLatestApplAfterUpdate();

			} else {
				smAppl = new SmAppl();
				smAppl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
				getManagedBean().setSmAppl(smAppl);
			}

			getManagedBean().setSmSupplier(smApplSupplier);
			assignActiveCategoryList();
			retView = SupplierManagementConst.PROCEED_DELETE_CATCODES_APPLN;
		} finally {

		}
		// catch (Exception e) {
		// e.printStackTrace();
		// retView = SupplierManagementConst.DEFAULT_DELETE_CATCODES_APPLN;
		//
		// }

		return retView;
	}

	/**
	 * Filter the list of categories in the SmAppl and Assign active category
	 * list to the managed bean.
	 */
	public void assignActiveCategoryList() {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				catList.add(cat);
			}
		}
		if (getManagedBean().getActiveCategoryList() != null
				&& !getManagedBean().getActiveCategoryList().isEmpty()) {
			getManagedBean().getActiveCategoryList().clear();
		}
		getManagedBean().setActiveCategoryList(catList);
	}

	/**
	 * Sample test.
	 * 
	 * @param event
	 *            the event
	 */
	@Deprecated
	public void sampleTest(ComponentSystemEvent event) {
		log.info(" **** EXCUTE ON LOAD EVENT **********");
	}

	/**
	 * Ajax function called when a row selected in the data table , to manage
	 * the selected records for enable or disable of remarks text area.
	 * 
	 * @param event
	 *            - SelectEvent
	 */
	public void onRowSelect(SelectEvent event) {
		log.info("*********** Enterd THE RoW SELECT METHOD ***********"
				+ getManagedBean().getSelectedCategories().size());
		manageSelectedRecords();

	}

	/**
	 * Ajax function called when a toggle selection in the data table , to
	 * manage the selected records for enable or disable of remarks text area.
	 * 
	 * @param event
	 *            - SelectEvent
	 */
	public void onToggleSelect(ToggleSelectEvent event) {
		log.info("*********** On Select or Unslect all ***********"
				+ getManagedBean().getSelectedCategories().size());
		manageSelectedRecords();

	}

	/**
	 * Ajax function called when a row is unselected in the data table , to
	 * manage the selected records for enable or disable of remarks text area.
	 * 
	 * @param event
	 *            - SelectEvent
	 */
	public void onRowUnSelect(UnselectEvent event) {
		log.info("*********** Enterd THE RoW UN SELECT METHOD ***********"
				+ getManagedBean().getSelectedCategories().size());
		manageSelectedRecords();

	}

	/**
	 * Internal method called on row select/ unselect to manage list selected
	 * records.
	 */
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
						log.info("*** ENTER *****");
						break;
					} else {
						// sdbTemp.setRemarks("");
						sdbTemp.setSelected(Boolean.FALSE);
					}
				}
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.set(i, sdbTemp);
				log.info("Data List For Index = "
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
	
	public void onDeleteSelectedCatCodes(){
		SmApplicationUtil.checkCategoryPendingTransaction(getManagedBean()
				.getSelectedCategories(), getManagedBean().getSmSupplier()
				.getSupplierId());
		RequestContext.getCurrentInstance().execute("confirmation.show()");
	}

	/**
	 * Delete selected category codes for the SmAppl object in the managed bean.
	 */
	public void deleteSelectedCatCodes() {
		try {
			int delListSize = getManagedBean().getSelectedCategories().size();
			log.info(" **** SIZE OF SELECTED *** " + delListSize);
			if (getManagedBean().getActiveCategoryList().size() == delListSize) {
				throw new EpException(MessageConst.ERROR_722578);
			}
			
			Boolean invalidRemarks = false;
			StringBuilder sb = new StringBuilder();
			sb.append(SmCommonUtil.getMessage("lbl.sm.category.deleteRemarks",
					null));
			int i = 0;
			for (SmSupplierCategory selCat : getManagedBean()
					.getSelectedCategories()) {
				if (selCat.getRemarks() == null
						|| selCat.getRemarks().trim().equals("")) {
					invalidRemarks = true;
					sb.append("<br/>")
							.append(++i)
							.append(".) ")
							.append(selCat.getPmCategoryL1()
									.getCategoryL1Code())
							.append(selCat.getPmCategoryL2()
									.getCategoryL2Code())
							.append(selCat.getPmCategoryL3()
									.getCategoryL3Code());
				}
			}
			if (invalidRemarks) {
				throw new EpException(sb.toString());
			} else {
				for (SmSupplierCategory selCat : getManagedBean()
						.getSelectedCategories()) {
					selCat.setRecordStatusFlg(SupplierManagementConst.DELETED_RECORD_STATUS);
					selCat.setCancelledDate(new Date());
				}
				//this.getManagedBean().getSmAppl().setQuestionnaire(false);
				updateSmAppl();
				
				SmVirtualCertHelper.getInstance().
				autoGenerateVirtualCert(getManagedBean().getSmAppl().getApplId(), true);
				
				assignActiveCategoryList();
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										"",
										delListSize
												+ SmCommonUtil
														.getMessage(
																"lbl.sm.category.categorySucessfullyDel",
																null)));
			}
		} catch (Exception ex) {
			throw new EpException(ex.getMessage());
		}

	}

	/**
	 * Update SmAppl object in the managed bean.
	 */
	private void updateSmAppl() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmAppl(this.getManagedBean().getSmAppl());
			mofRegistrationSO.updateApplication(reqDto);
			retriveLatestApplAfterUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			retriveLatestApplAfterUpdate();
			mofRegistrationSO = null;
		}

	}

	/**
	 * Retrive latest SmAppl after updates.
	 */
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

	/**
	 * Gets the current supplier based on the logged in user details.
	 * 
	 * @return supplier - SmSupplier
	 */
	private SmSupplier getCurrentSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			supplierSearchReqDTO.setAllStatuses(true);

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

	/**
	 * Check if the supplier is allowed to proceed for deletion of records
	 * (Checking on any active / draft application in progress for the
	 * supplier).
	 * 
	 * @param smSupplier
	 *            - SmSupplier
	 */
	private void checkIsAllowedToProceed(SmSupplier smSupplier) {
		boolean cannotProceedAlertMsg = false;
		// check if can proceed
		if (smSupplier != null) {
			cannotProceedAlertMsg = SmApplicationUtil
					.checkActiveSmApplForSupp(smSupplier);
		}
		// throw exception if cannot proceed
		if (cannotProceedAlertMsg) {
			throw new EpException(MessageConst.ERROR_722542);
		}
	}
}
