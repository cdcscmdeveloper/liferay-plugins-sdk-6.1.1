/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.common;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.common.UomManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Common Supplier Detail Backing Bean
 * 
 * @author mangaiah Garikapati.
 * @since 27.02.2018
 */
@ManagedBean(name ="uomBackingBean")
@RequestScoped
public class UomBackingBean extends BaseBackingBean<UomManagedBean> implements Serializable {
	public static Logger log = EpLogger.getLogger(UomBackingBean.class);

	/**
	 * Retrieve measurement.
	 */
	public void retrieveMeasurement() {
		getManagedBean().getUnitOfMeasurementDataModel().reloadData();
	}

	/**
	 * Gets the search qt data model.
	 * 
	 * @return the search qt data model
	 */
	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		IPagingDataModel<PmUom> searchUomDataModel = getManagedBean()
				.getUnitOfMeasurementDataModel();
		if (searchUomDataModel == null) {
			searchUomDataModel = new UnitOfMeasurementPagingDataModel();
			getManagedBean().setUnitOfMeasurementDataModel(searchUomDataModel);
		}
		return searchUomDataModel;
	}

	private class UnitOfMeasurementPagingDataModel extends
			PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR",false);
		}

		@Override
		public ISearchResultBean<PmUom> pageChangeListener(
				ISearchBean<PmUom> searchBean) {
			try {
				ISearchBean<PmUom> searchBeanData = getMeasurementSearchBeanData(searchBean);
				UomReqDTO reqDTO = new UomReqDTO();
				reqDTO.setMeasurementSearchBean(searchBeanData);
				IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUomSO.class);
				UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getMeasurementSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * Gets the measurement search bean data.
	 * 
	 * @param searchBean
	 *            the search bean
	 * @return the measurement search bean data
	 */
	private ISearchBean<PmUom> getMeasurementSearchBeanData(
			ISearchBean<PmUom> searchBean) {

		if (getManagedBean().getUomCode() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUomCode())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean.setSearchField("uomCode");
			uomSearchBean.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean().getUomCode());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}

		if (getManagedBean().getUomName() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUomName())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean.setSearchField("uomName");
			uomSearchBean.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean().getUomName());
			searchBean.getDataFilterBeans().add(uomSearchBean);
		}
		//Bug #21863
		SearchFilterBean uomSearchBean = new SearchFilterBean(
				SearchOperator.OPERATOR_EQUALS.value());
		uomSearchBean.setSearchField("recordStatusFlg");
		uomSearchBean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
		searchBean.getDataFilterBeans().add(uomSearchBean);
		return searchBean;
	}

	/**
	 * Reset measurement search.
	 */
	public void resetMeasurementSearch() {
		this.getManagedBean().setUomCode(null);
		this.getManagedBean().setUomName(null);
		//getManagedBean().setUnitOfMeasurementDataModel().resetData();
		//getManagedBean().getUnitOfMeasurementDataModel().reloadData();
	}

}
