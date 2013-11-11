/**
 * 
 */
package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.common.categorys.CategorysReqDTO;
import my.ep.dto.pm.common.categorys.CategorysResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;

/**
 * @author VidyasagarReddy
 *
 */
@ManagedBean(name="qTSupplierQulCriteriaBackingBean")
@RequestScoped
public class QTSupplierQulCriteriaBackingBean extends BaseBackingBean<QTDetailManagedBean> {
	private static final Logger log=EpLogger.getLogger(QTSupplierQulCriteriaBackingBean.class);
	public QTSupplierQulCriteriaBackingBean(){
		
	}
	
	/*
	 * Method to load the categorieslist for suppliers screen
	 */
	public void retriveQtCriteria() {

		// Retrive categoryL1 and set to managedbean for firsttime other wise
		// skip
		List<PmCategoryL1> l1categoryList = this.getManagedBean()
				.getCategoryL1s();
		if (l1categoryList == null || !l1categoryList.isEmpty()) {
			ICategorysSO categorysSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", ICategorysSO.class);
			CategorysResDTO catResDTO = categorysSO.retiveCategoryL1List();
			if (catResDTO.getCategoryL1List() != null
					&& !catResDTO.getCategoryL1List().isEmpty()) {
				this.getManagedBean().setCategoryL1s(
						catResDTO.getCategoryL1List());
			}
			// Retrive states and set to managedbean for firsttime otherwise
			// skip
			List<PmState> stateList = this.getManagedBean().getStates();
			if (stateList == null || !stateList.isEmpty()) {
				StateReqDTO reqDTO = new StateReqDTO();
				reqDTO.setCountryId(new Long(1));
				IStateSO stateSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
				StateResDTO resDTO = stateSO
						.retriveStatesListByCountryID(reqDTO);
				List<PmState> states = resDTO.getStateList();
				if (states != null && !states.isEmpty()) {
					this.getManagedBean().setStates(states);
				}
			}
		}
	}
	/**
	 * To add empty row to category code data table
	 */
	public void addNewCategoryItem() {
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories == null) {
			categories = new ArrayList<ScQtCategory>();

		} else {
			ScQtCategory category = new ScQtCategory();
			categories.add(category);
		}
		this.getManagedBean().getQt().setScQtCategorys(categories);
	}

	/*
	 * To remove categoryCode from data table
	 */
	public void deleteCategoryCode(ValueChangeEvent changeEvent) {
		ScQtCategory category = (ScQtCategory) changeEvent.getNewValue();
		List<ScQtCategory> categories = this.getManagedBean().getQt()
				.getScQtCategorys();
		if (categories.contains(category)) {
			categories.remove(category);
		}

	}
	/*
	 * Method to call onchange of L1category
	 */
	public void handleL2CategoryChange(ValueChangeEvent changeEvent) {
		PmCategoryL1 categoryL1 = (PmCategoryL1) changeEvent.getNewValue();
		if (null != categoryL1 && categoryL1.getCategoryL1Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL1CategoryId(categoryL1.getCategoryL1Id());
			ICategorysSO categorysSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", ICategorysSO.class);
			CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL2ListByL1CategoryId(reqDTO);
			List<PmCategoryL2> catL2List = categorysResDTO.getCategoryL2List();
			this.getManagedBean().getCategoryL2s().clear();
			this.getManagedBean().setCategoryL2s(catL2List);
		}
	}

	/**
	 * Method to call onchange of L2category
	 */
	public void handleL3CategoryChange(ValueChangeEvent changeEvent) {

		PmCategoryL2 categoryL2 = (PmCategoryL2) changeEvent.getNewValue();
		if (null != categoryL2 && categoryL2.getCategoryL2Id() != null) {
			CategorysReqDTO reqDTO = new CategorysReqDTO();
			reqDTO.setL2CategoryId(categoryL2.getCategoryL2Id());
			ICategorysSO categorysSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", ICategorysSO.class);
						CategorysResDTO categorysResDTO = categorysSO
					.retiveCategoryL3ListByL2CategoryId(reqDTO);
			List<PmCategoryL3> catL3List = categorysResDTO.getCategoryL3List();
			this.getManagedBean().getCategoryL3s().clear();
			this.getManagedBean().setCategoryL3s(catL3List);
		}
	}

	/**
	 * To retrive districts based on state value changes.
	 */
	public void handleStateChange(ValueChangeEvent changeEvent) {
		PmState state = (PmState) changeEvent.getNewValue();
		if (state != null && state.getStateId() != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(state.getStateId());
			IDistrictSO districtSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);
			if (resDTO != null && !resDTO.getDistrictList().isEmpty()) {
				this.getManagedBean().getDistricts().clear();
				this.getManagedBean().setDistricts(resDTO.getDistrictList());
			}
		}
	}

	/**
	 * Method to search the supplier for panel and restricted tender.
	 */
	public void searchQtPanelSupplier(){
	
		List<SmSupplier> panelSupplier = retriverSuppliersForPanelQT();
		
		if(!getManagedBean().getPanelSuppliers().isEmpty()){
			getManagedBean().getPanelSuppliers().clear();
		}
		getManagedBean().getPanelSuppliers().addAll(panelSupplier);
	}
	/**
	 * Method to search the supplier for normal tender
	 */
	public void searchNormalQtSupplier(){
		
	}
	private List<SmSupplier> retriverSuppliersForPanelQT(){
		
		CircularReqDTO reqDTO = new CircularReqDTO();
		ISearchBean<CtPanelSupplier> searchBean = new SearchBean<CtPanelSupplier>();
		
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		SearchFilterBean likeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
			
		equalFilterBean.setSearchField("ctCircular.circularNo");
		equalFilterBean.setSearchValue("PCR_00001");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		likeFilterBean.setSearchField("ctPanelSupplierItems.ctPanelItem.itemName");
		likeFilterBean.setSearchValue("Pen");
		searchBean.getDataFilterBeans().add(likeFilterBean);
		
		equalFilterBean.setSearchField("ctCircular.name");
		equalFilterBean.setSearchValue("");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		equalFilterBean.setSearchField("");
		equalFilterBean.setSearchValue("");
		searchBean.getDataFilterBeans().add(equalFilterBean);
		
		reqDTO.setSearchBean(searchBean);
		CircularResDTO resDTO=null;
		try{
		ICircularSO circularSO= EpEjbBusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
		resDTO = circularSO.searchPanelSuppliers(reqDTO);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resDTO.getSupplierList();
	}

}
