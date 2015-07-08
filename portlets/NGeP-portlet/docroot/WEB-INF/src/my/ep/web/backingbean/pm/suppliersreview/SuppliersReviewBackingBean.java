package my.ep.web.backingbean.pm.suppliersreview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.suppliersreview.ISuppliersReviewSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.SuppliersReviewResDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.suppliersreview.SuppliersReviewReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.SuppliersReviewSearchBean;
import my.ep.web.managedbean.pm.suppliersreview.SuppliersReviewManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "suppliersReviewBackingBean")
@ViewScoped
public class SuppliersReviewBackingBean extends
BaseBackingBean<SuppliersReviewManagedBean> {
	private static Logger log = Logger
			.getLogger(SuppliersReviewBackingBean.class);

	public static final String NAV_TO_MAIN = "/xhtml/pm/suppliersreview/suppliersReviewSearchMain";	

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

	public void selectAllStates() {

		if(this.getManagedBean().isAllStates()){
			this.getManagedBean()
			.getSuppliersReviewSearchBean().setSelectedStatesList(this.getManagedBean()
					.getSuppliersReviewSearchBean().getStatesList());
			this.getManagedBean().setDisabled(true);
		}else{
			this.getManagedBean()
			.getSuppliersReviewSearchBean().setSelectedStatesList(null);
			this.getManagedBean().setDisabled(false);
		}
		RequestContext.getCurrentInstance().execute("selectAll()");


	}

	public void preLoadingSuppliersReview() {
		log.info("preLoadingSuppliersReview method");
		this.resetSuppliersReviewSearchBean();
		retrievePmStateList();				
		FacesContext fc = FacesContext.getCurrentInstance();		
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, NAV_TO_MAIN);
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * reset search bean fields
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

		retrievePmStateList();

	}

	/**
	 * Retrieve pm country list.
	 * 
	 * @return the list of PmCountry
	 */
	public List<PmState> retrievePmStateList() {
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


	public void searchSuppliersReviewDetails() {


		ISuppliersReviewSO suppliersReviewSO;

		SuppliersReviewReqDTO suppliersReviewReqDTO = new SuppliersReviewReqDTO();


		List<Long> stateIdList = new ArrayList<Long>();
		List<Long> categoryL3L = new ArrayList<Long>();


		List<ScQtCategory> catList = this.getManagedBean().getQtCategoryList();

		if(catList!=null && !catList.isEmpty()){

			for(ScQtCategory scQtCategory:catList){

				categoryL3L.add(scQtCategory.getPmCategoryL3().getCategoryL3Id());
			}
			this.getManagedBean().getSuppliersReviewSearchBean().setCategoryIdList(categoryL3L);

		}
		else{
			throw new EpException(MessageConst.ERROR_790001);	
		}

		if(this.getManagedBean().getSuppliersReviewSearchBean().getDropDownStatesIdList()!=null &&
				this.getManagedBean().getSuppliersReviewSearchBean().getDropDownStatesIdList().length>0){

			stateIdList.addAll(Arrays.asList(this.getManagedBean().getSuppliersReviewSearchBean().getDropDownStatesIdList()));

		}

		if(this.getManagedBean().getSuppliersReviewSearchBean().getPaidUpCapitalAmtFrom()!=null 
				&& this.getManagedBean().getSuppliersReviewSearchBean().getPaidUpCapitalAmtTo()!=null){

			suppliersReviewReqDTO.setPaidUpCapitalAmtFrom(this.getManagedBean().getSuppliersReviewSearchBean().getPaidUpCapitalAmtFrom());
			suppliersReviewReqDTO.setPaidUpCapitalAmtTo(this.getManagedBean().getSuppliersReviewSearchBean().getPaidUpCapitalAmtTo());
		}
		else{
			throw new EpException(MessageConst.ERROR_790002);
		    }

		suppliersReviewReqDTO.setStateIdList(stateIdList);
		suppliersReviewReqDTO.setCategoryIdList(this.getManagedBean().getSuppliersReviewSearchBean().getCategoryIdList());
		suppliersReviewReqDTO.setCategoriesListSize(this.getManagedBean().getSuppliersReviewSearchBean().getCategoryIdList().size());
		try {
			suppliersReviewSO = (ISuppliersReviewSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							ISuppliersReviewSO.class);
			SuppliersReviewResDTO suppliersReviewResDTO = suppliersReviewSO
					.searchSuppliersReviewData(suppliersReviewReqDTO);

			getManagedBean().setLazy(true);

			if (suppliersReviewResDTO.isSuccessful()) {

				suppliersReviewResDTO.getCount();

				this.getManagedBean().setNoOfSuppliers(suppliersReviewResDTO.getCount().longValue());			

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770005);
		}

	}

	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		debugLogger("addNewCategoryAction");
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();	
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}
		//validateDuplicateCategoryCode(pmCategoryL3, qtCategoryList);	
		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		//category.setScQt(this.getManagedBean().getQt());
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		qtCategoryList.add(category);
	}

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
