/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.noofsuppliersreview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.marketresearch.IMarketResearchSO;
import my.ep.biz.service.sc.suppliersreview.ISuppliersReviewSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.suppliersreview.SuppliersReviewReqDTO;
import my.ep.dto.sc.suppliersreview.SuppliersReviewResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.SuppliersReviewSearchBean;
import my.ep.web.managedbean.sc.noofsuppliersreview.SuppliersReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SuppliersReviewBackingBean.
 */
@ManagedBean(name = "suppliersReviewBackingBean")
@ViewScoped
public class SuppliersReviewBackingBean extends
		BaseBackingBean<SuppliersReviewManagedBean> {

	/** The log. */
	private static Logger log = Logger
			.getLogger(SuppliersReviewBackingBean.class);

	/** The Constant NAV_TO_MAIN. */
	public static final String NAV_TO_MAIN = "/xhtml/sc/supplierreview/noofsuppliersreview/suppliersReviewSearchMain";

	/**
	 * Instantiates a new suppliers review backing bean.
	 */
	public SuppliersReviewBackingBean() {

		log.info("inside SuppliersReviewBackingBean constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	/**
	 * Select all states.
	 */
	public void selectAllStates() {
		log.info("selectAllStates method");
		if (this.getManagedBean().isAllStates()) {
			this.getManagedBean()
					.getSuppliersReviewSearchBean()
					.setSelectedStatesList(
							this.getManagedBean()
									.getSuppliersReviewSearchBean()
									.getStatesList());
			this.getManagedBean().setDisabled(true);
		} else {
			this.getManagedBean().getSuppliersReviewSearchBean()
					.setSelectedStatesList(null);
			this.getManagedBean().setDisabled(false);
		}
		// RequestContext.getCurrentInstance().execute("selectAll()");

	}

	/**
	 * Pre loading suppliers review.
	 */
	public void preLoadingSuppliersReview() {
		log.info("preLoadingSuppliersReview method");
		this.resetSuppliersReviewSearchBean();
		retrievePmStateList();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reset search bean fields.
	 */
	public void resetSuppliersReviewSearchBean() {

		debugLogger("resetSuppliersReviewSearchBean");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setNoOfSuppliers(null);

		this.getManagedBean().setSuppliersReviewSearchBean(
				new SuppliersReviewSearchBean());

		this.getManagedBean().setAllStates(false);
		this.getManagedBean().setDisabled(false);
		this.getManagedBean().setQtCategoryList(null);

		retrievePmStateList();

	}

	/**
	 * Retrieve pm country list.
	 * 
	 * @return the list of PmCountry
	 */
	public List<PmState> retrievePmStateList() {

		debugLogger("in retrievePmStateList");

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		List<PmState> stateList = new ArrayList<PmState>();
		StateResDTO stateResDTO = stateSO.retrievePmStateList();
		for (PmState pmState : stateResDTO.getStateList()) {
			if (!pmState.getStateName().contains(ParameterConst.PM_STATE_VALUE)) {
				stateList.add(pmState);
			}
		}
		this.getManagedBean().getSuppliersReviewSearchBean()
				.setStatesList(stateList);
		return stateList;
	}

	/**
	 * Search suppliers review details.
	 */
	public void searchSuppliersReviewDetails() {
		debugLogger("searchSuppliersReviewDetails");

		ISuppliersReviewSO suppliersReviewSO;

		SuppliersReviewReqDTO suppliersReviewReqDTO = new SuppliersReviewReqDTO();

		List<ScQtCategory> catList = this.getManagedBean().getQtCategoryList();

		if (this.getManagedBean().getSuppliersReviewSearchBean()
				.getPaidUpCapitalAmtFrom() != null
				&& this.getManagedBean().getSuppliersReviewSearchBean()
						.getPaidUpCapitalAmtTo() != null) {

			suppliersReviewReqDTO.setPaidUpCapitalAmtFrom(this.getManagedBean()
					.getSuppliersReviewSearchBean().getPaidUpCapitalAmtFrom());
			suppliersReviewReqDTO.setPaidUpCapitalAmtTo(this.getManagedBean()
					.getSuppliersReviewSearchBean().getPaidUpCapitalAmtTo());
		} else {
			throw new EpException(MessageConst.ERROR_790002);
		}

		if (catList != null && !catList.isEmpty()) {

			List<Long> categoryL3L = new ArrayList<Long>();

			for (ScQtCategory scQtCategory : catList) {

				categoryL3L.add(scQtCategory.getPmCategoryL3()
						.getCategoryL3Id());
			}
			this.getManagedBean().getSuppliersReviewSearchBean()
					.setCategoryIdList(categoryL3L);

		} else {
			throw new EpException(MessageConst.ERROR_790001);
		}
		List<Long> stateIdList = new ArrayList<Long>();

		if (this.getManagedBean().isAllStates()) {
			if (this.getManagedBean().getSuppliersReviewSearchBean()
					.getSelectedStatesList() != null
					&& !this.getManagedBean().getSuppliersReviewSearchBean()
							.getSelectedStatesList().isEmpty()) {
				for (PmState pmState : this.getManagedBean()
						.getSuppliersReviewSearchBean().getSelectedStatesList()) {
					stateIdList.add(pmState.getStateId());
				}
			}
		} else if (this.getManagedBean().getSuppliersReviewSearchBean()
				.getDropDownStatesIdList() != null
				&& this.getManagedBean().getSuppliersReviewSearchBean()
						.getDropDownStatesIdList().length > 0) {

			stateIdList.addAll(Arrays.asList(this.getManagedBean()
					.getSuppliersReviewSearchBean().getDropDownStatesIdList()));

		}
		suppliersReviewReqDTO.setStateIdList(stateIdList);
		suppliersReviewReqDTO.setCategoryIdList(this.getManagedBean()
				.getSuppliersReviewSearchBean().getCategoryIdList());
		suppliersReviewReqDTO.setCategoriesListSize(this.getManagedBean()
				.getSuppliersReviewSearchBean().getCategoryIdList().size());
		try {
			suppliersReviewSO = (ISuppliersReviewSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION,
							ISuppliersReviewSO.class);

			SuppliersReviewResDTO suppliersReviewResDTO = suppliersReviewSO
					.searchSuppliersReviewData(suppliersReviewReqDTO);

			getManagedBean().setLazy(true);

			if (suppliersReviewResDTO.isSuccessful()) {

				suppliersReviewResDTO.getCount();

				this.getManagedBean().setNoOfSuppliers(
						suppliersReviewResDTO.getCount().longValue());

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770005);
		}

	}

	/**
	 * Adds the new category action.
	 * 
	 * @param pmCategoryL3
	 *            the pm category l3
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		debugLogger("addNewCategoryAction");
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}
		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		category.setPmCategoryL1Name(
				pmCategoryL2.getPmCategoryL1().getCategoryName());
		category.setPmCategoryL2Name(
				pmCategoryL2.getCategoryName());
		category.setPmCategoryL3Name(
				pmCategoryL3.getCategoryName());		
		
		qtCategoryList.add(category);
	}

	/**
	 * Delete category codes.
	 */
	public void deleteCategoryCodes() {
		debugLogger("deleteCategoryCodes");
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();
		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSelectedCategoryList();
		if (null == openSelectList || openSelectList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}
		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (ScQtCategory category : openSelectList)
				openList.remove(category);
		}
	}

}
