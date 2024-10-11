package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierCategorySO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.search.SupplierCategoryReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmDeleteCategoryCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
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
				log.info("******** The application id is *******" + smAppl.getApplId());
				getManagedBean().setSmAppl(retriveLatestApplAfterUpdate(smAppl));
			} else {
				smAppl = new SmAppl();
				smAppl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
				getManagedBean().setSmAppl(smAppl);
			}

			getManagedBean().setSmSupplier(smApplSupplier);
			assignActiveCategoryList();
			//14150
			SmApplicationUtil.sortSupplierCategory(getManagedBean()
					.getActiveCategoryList());
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
				IUserSO userSO = null;
				
				for (SmSupplierCategory selCat : getManagedBean()
						.getSelectedCategories()) {
					selCat.setRecordStatusFlg(SupplierManagementConst.DELETED_RECORD_STATUS);
					selCat.setCancelledDate(new Date());
					/*#28404 - Start*/
					Long loginSessionUserId = 1L;
					userSO = (IUserSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IUserSO.class);
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
					PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
					if (pmUser == null) {
						loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
					}
					else{
						loginSessionUserId = pmUser.getUserId();
					}
					new LogKey(loginSessionUserId);
					log.info("**** User ID for ChangedBy (Delete Category Code) Before *************"+loginSessionUserId);
					selCat.setChangedBy(loginSessionUserId);
					log.info("**** User ID for ChangedBy (Delete Category Code) After *************"+loginSessionUserId);
					/*#28404 - End*/
					//#30766 & #34209 - START
					updateDelCodesInAllRejApplNewerThanLatestAppl();
					//#30766 & #34209 - END
				}
				//this.getManagedBean().getSmAppl().setQuestionnaire(false);
				this.getManagedBean().setSmAppl(updateSmAppl(this.getManagedBean().getSmAppl()));
				
				//26418
				saveDeletedCodesTrackingDiary();
				
				SmVirtualCertHelper.getInstance().
				autoGenerateVirtualCert(getManagedBean().getSmAppl().getApplId(), true);
				
				assignActiveCategoryList();

				//25963 - update deleted codes in rejected appl, if any
				updateDeletedCodesInRejAppl();
				//START 14150
				SmApplicationUtil.sortSupplierCategory(getManagedBean().getActiveCategoryList());			
				SmApplicationUtil.sortbyDate(getManagedBean().getActiveCategoryList());
				//END 14150
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										"",
										delListSize + " " 
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
	private SmAppl updateSmAppl(SmAppl smAppl) {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm",
					IMofRegistrationSO.class);

			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSmAppl(smAppl);
			mofRegistrationSO.updateApplication(reqDto);
			return retriveLatestApplAfterUpdate(smAppl);
		} catch (Exception e) {
			e.printStackTrace();
			mofRegistrationSO = null;
			return retriveLatestApplAfterUpdate(smAppl);
		}
	}

	/**
	 * Update deleted codes in rejected appl, if any
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private void updateDeletedCodesInRejAppl() {
		SmAppl latestRejectedAppl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppType(this
				.getManagedBean().getSmSupplier().getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES);

		IUserSO userSO = null;
		
		if (latestRejectedAppl != null) {
			for (SmSupplierCategory cat : latestRejectedAppl.getSmSupplierCategories()) {
				for (SmSupplierCategory delCat : getManagedBean().getSelectedCategories())
					if (cat.getPmCategoryL1().getCategoryL1Code().equals(delCat.getPmCategoryL1().getCategoryL1Code())
							&& cat.getPmCategoryL2().getCategoryL2Code()
									.equals(delCat.getPmCategoryL2().getCategoryL2Code())
							&& cat.getPmCategoryL3().getCategoryL3Code()
									.equals(delCat.getPmCategoryL3().getCategoryL3Code())) {
						cat.setRecordStatusFlg(SupplierManagementConst.DELETED_RECORD_STATUS);
						cat.setCancelledDate(new Date());
						/*#28404 - Start*/
						Long loginSessionUserId = 1L;
						userSO = (IUserSO) BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
										IUserSO.class);
						UserReqDTO userReqDTO = new UserReqDTO();
						userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
						PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
						if (pmUser == null) {
							loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
						}
						else{
							loginSessionUserId = pmUser.getUserId();
						}
						new LogKey(loginSessionUserId);
						log.info("**** User ID for ChangedBy (Delete Category Code) Before *************"+loginSessionUserId);
						cat.setChangedBy(loginSessionUserId);
						log.info("**** User ID for ChangedBy (Delete Category Code) After *************"+loginSessionUserId);
						/*#28404 - End*/
					}
			}
			updateSmAppl(latestRejectedAppl);
		}
	}
	
	
	/**
	 * save supplier delete category tracking diary.
	 */
	//Bug #26418
	private void saveDeletedCodesTrackingDiary() {
		StringBuffer sb = new StringBuffer();
		String actionDesc = null;
		SmTrackingDairyVO smTrackingDairyVO = null;
		List<SmTrackingDairyVO> listSmTrackingDiary = new ArrayList<SmTrackingDairyVO>();
		
		int i = getManagedBean().getSelectedCategories().size();

		try {
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null) {
				sb.append("Bidang berikut telah dihapuskan : ");
				
				for (SmSupplierCategory selCat : getManagedBean()
						.getSelectedCategories()) {
					
						i = i - 1;
						sb.append(selCat.getPmCategoryL1().getCategoryL1Code()
								+ selCat.getPmCategoryL2().getCategoryL2Code()
								+ selCat.getPmCategoryL3().getCategoryL3Code());
						if (i > 0) {
							sb.append(", ");
						}
						actionDesc = sb.toString();

					}
				}

				smTrackingDairyVO = SmCommonUtil.assignTrackingDiaryValues(
						actionDesc, getManagedBean().getSmAppl().getApplId(),
						getManagedBean().getSmAppl().getApplNo(),
						SmCommonUtil.getUserRoleValue());
				if (smTrackingDairyVO !=null){
					smTrackingDairyVO.setStatus(getManagedBean().getSmAppl().getPmStatus());
					listSmTrackingDiary.add(smTrackingDairyVO);
				}
			
			processTrackingDiaryList(listSmTrackingDiary);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

	}
	//Bug #26418
	private void processTrackingDiaryList(List<SmTrackingDairyVO> trackingDiaryVOList) {
		for(SmTrackingDairyVO trackingDiaryVO : trackingDiaryVOList) {
			SmServiceLookup.getTrackingDiarySO().insertTrackingDiary(
					trackingDiaryVO.getActionDesc(),
					trackingDiaryVO.getActionedBy() != null ? trackingDiaryVO
							.getActionedBy() : null, trackingDiaryVO
							.getActionedByRole(), trackingDiaryVO.getDocId(),
					trackingDiaryVO.getDocNo(), trackingDiaryVO.getDocType(), null,
					trackingDiaryVO.getStatus() != null ? trackingDiaryVO
							.getStatus().getStatusId() : null, null);
		}
	}
	
	/**
	 * Retrive latest SmAppl after updates.
	 */
	private SmAppl retriveLatestApplAfterUpdate(SmAppl smAppl) {

		IMofRegistrationSO mofRegistrationSO = null;
		mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm",
				IMofRegistrationSO.class);
		SmAppl appl = smAppl;
		try {

			MofRegistrationReqDTO finReqDto = new MofRegistrationReqDTO();
			finReqDto.setApplId(appl.getApplId());

			MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveCurrentDraftApplication(finReqDto);
			appl = resDTO.getSmAppl();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mofRegistrationSO = null;
		}
		return appl;
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
	
	//#30766 & #34209 - START
	public void updateDelCodesInAllRejApplNewerThanLatestAppl(){
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm",IMofRegistrationSO.class);
		ISupplierCategorySO supplierCategorySO = (ISupplierCategorySO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierCategorySO.class);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
		List<SmAppl> rejectedApplsAfterLatestAppl = mofRegistrationSO.retrieveSupplierRejectedApplNewerThanLatestAppl(this.getManagedBean().getSmAppl()).getApplList();
		Long loginSessionUserId = 1L;
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		if (pmUser == null) {
			loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
		}
		else{
			loginSessionUserId = pmUser.getUserId();
		}
		
		
		for(SmAppl rejectedAppl : rejectedApplsAfterLatestAppl){
			SupplierCategoryReqDTO supplierCategoryReqDTO = new SupplierCategoryReqDTO();
			supplierCategoryReqDTO.setApplId(rejectedAppl.getApplId());
			rejectedAppl.setSmSupplierCategories(supplierCategorySO.retrieveCategoryByApplId(supplierCategoryReqDTO).getSupplierCategoryList());
			for(SmSupplierCategory rejectedApplCat : rejectedAppl.getSmSupplierCategories()){
				for(SmSupplierCategory selCat : getManagedBean().getSelectedCategories()) {
					if(rejectedApplCat.getCategoryL1Name().equals(selCat.getCategoryL1Name()) && 
					   rejectedApplCat.getCategoryL2Name().equals(selCat.getCategoryL2Name()) && 
					   rejectedApplCat.getCategoryL3Name().equals(selCat.getCategoryL3Name())){
						rejectedApplCat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						rejectedApplCat.setCancelledDate(new Date());
						rejectedApplCat.setChangedBy(loginSessionUserId);
						rejectedApplCat.setRemarks(selCat.getRemarks());
					}
				}
			}
			MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
			mofRegistrationReqDTO.setSmAppl(rejectedAppl);
			mofRegistrationSO.updateSmAppl(mofRegistrationReqDTO);
		}
	}
	//#30766 & #34209 - END
}
