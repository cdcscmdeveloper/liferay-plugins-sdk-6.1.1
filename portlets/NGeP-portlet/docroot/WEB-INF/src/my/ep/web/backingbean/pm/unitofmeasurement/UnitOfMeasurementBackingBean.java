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
package my.ep.web.backingbean.pm.unitofmeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.unitofmeasurement.UnitOfMeasurementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * @author DK
 * 
 */
@ManagedBean(name = "unitOfMeasurementBackingBean")
@ViewScoped
public class UnitOfMeasurementBackingBean extends
		BaseBackingBean<UnitOfMeasurementManagedBean> {
	
	private static final Logger log = EpLogger
			.getLogger(UnitOfMeasurementManagedBean.class);
	//Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/unitofmeasurement/unitOfMeasurementMain.xhtml";
		
	private IPagingDataModel<PmUom> unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();
	public static final boolean SEARCH_DEFAULT = false;
	
	public UnitOfMeasurementBackingBean() {
		log.info("inside constructor");

	}
	
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	/**
	 * load clean view
	 */
	public void preLoading(){
		log.info("preLoading");
		this.resetMeasurementSearch();
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}


	/**
	 * reload to UOM search page 
	 * 
	 */		
	public String redirectMeasurementSearch() {
		log.info("redirectMeasurementSearch");
//		this.getManagedBean().setLazy(true); 

		return NAV_TO_SEARCH;

	}	
	
	/**
	 * Reset measurement search 
	 */
	public void resetMeasurementSearch() {
		log.info("resetMeasurementSearch");
		this.getManagedBean().setLazy(false);
		this.getManagedBean().setStatus(null);
		this.getManagedBean().setMeasurementNames(new String());
		this.getManagedBean().setMeasurementCodes(new String());
		unitOfMeasurementDataModel = new UnitOfMeasurementPagingDataModel();	
		
	}	
	
	/**
	 * get unit of measurement 
	 * measurement status item dropdown
	 * 1-active
	 * 2-inactive
	 * 
	 */	
	 public SelectItem[] getMeasurementStatusList() {
		 
		 	return PmCommonUtil.getCommonStatusOptions();
		}	
	 
		/**
		 * get unit of measurement 
		 * measurement status item dropdown
		 * 1-active
		 * 2-inactive
		 * 
		 */	
		 public SelectItem[] getMeasurementDecimal() {
			 
				SelectItem[] items =  new SelectItem[4];
				items[0] = new SelectItem(0, "0");
				items[1] = new SelectItem(1, "1");
				items[2] = new SelectItem(2, "2");
				items[3] = new SelectItem(3, "3");
			    return items;
			}		 
	
	/**
	 * reload to UOM maintain page
	 * 
	 */	
	public String createMeasurement() {
		log.info("createMeasurement");
		PmUom pmUom = new PmUom();
		this.getManagedBean().setPmUom(pmUom);

		return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";

	}

	/**
	 * search unit of measurement info based 
	 * on UOM codes and names search
	 */
	private ISearchBean<PmUom> getMeasurementSearchBeanData(ISearchBean<PmUom> searchBean){
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		if (getManagedBean().getMeasurementCodes() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementCodes())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomCode");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementCodes());
			searchBean.getDataFilterBeans().add(uomSearchBean);
			isEmpty = false;
		}

		if (getManagedBean().getMeasurementNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getMeasurementNames())) {
			SearchFilterBean uomSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			uomSearchBean
					.setSearchField("uomName");
			uomSearchBean
					.setCaseInsensitive(true);
			uomSearchBean.setSearchValue(getManagedBean()
					.getMeasurementNames());
			searchBean.getDataFilterBeans().add(uomSearchBean);
			isEmpty = false;
		}	
		
		if(getManagedBean().getStatus() != null) {
			SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			codesSearchBean	.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(getManagedBean()	.getStatus());
			searchBean.getDataFilterBeans().add(codesSearchBean);
		}else{
			SearchFilterBean codesSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
			codesSearchBean	.setSearchField("recordStatusFlg");
			codesSearchBean.setSearchValue(new ArrayList<String>(Arrays.asList(ParameterConst.RECORD_STATUS_INACTIVE, ParameterConst.RECORD_STATUS_ACTIVE)));
			searchBean.getDataFilterBeans().add(codesSearchBean);
			
		}
		
		return searchBean;
	}		
	
	public void retrieveMeasurement(){
		log.info("retrieveMeasurement");
		this.getManagedBean().setLazy(true); 
		unitOfMeasurementDataModel.reloadData();
		
	}		

	public IPagingDataModel<PmUom> getUnitOfMeasurementDataModel() {
		return unitOfMeasurementDataModel;
	}

	public void setUnitOfMeasurementDataModel(
			IPagingDataModel<PmUom> unitOfMeasurementDataModel) {
		this.unitOfMeasurementDataModel = unitOfMeasurementDataModel;
	}
	
	private class UnitOfMeasurementPagingDataModel extends PagingDataModel<PmUom> {
		private static final long serialVersionUID = 1L;

		public UnitOfMeasurementPagingDataModel() {
			super("measurementSR",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmUom> pageChangeListener(ISearchBean<PmUom> searchBean) {

			try {	
				ISearchBean<PmUom> searchBeanData = getMeasurementSearchBeanData(searchBean);
				UomReqDTO reqDTO = new UomReqDTO();
				

				reqDTO.setMeasurementSearchBean(searchBeanData);
				
				IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);	
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
	 * get unit of measurement info 
	 * into maintenance page (PmUom pmUom)
	 */	
	public String goToUnitMaintaincePage(PmUom pmUom){
		log.info("goToUnitMaintaincePage");
		this.getManagedBean().setPmUom(pmUom);
		
		PmUom oldPmUom = null;
		try {
			oldPmUom = (PmUom)BeanUtils.cloneBean(pmUom);
		     this.getManagedBean().setOldPmUom(oldPmUom);		
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";		
	}
	
	
	/**
	 * get unit of measurement info 
	 * into maintenance page (PmUom pmUom)
	 */	
	private void reloadUOMData(PmUom pmUom){
		log.info("goToUnitMaintaincePage");
		this.getManagedBean().setPmUom(pmUom);
		
		PmUom oldPmUom = null;
		try {
			oldPmUom = (PmUom)BeanUtils.cloneBean(pmUom);
		     this.getManagedBean().setOldPmUom(oldPmUom);		
		}
		catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Validation message for unit of measurement details
	 * 
	 */
	private void validationMessage(PmUom uom){
		log.info("validationMessage");
		if(StringUtils.isBlank(uom.getUomCode())){
			throw new EpException(MessageConst.ERR_900010);
		}
		
		if(StringUtils.isBlank(uom.getUomName())){
			throw new EpException(MessageConst.ERR_900011);
		}
		
//		if(uom.isBrandNew()){
		if(DbValueConst.RECORD_STATUS_ACTIVE == uom.getRecordStatusFlg().intValue()){
		   uniqueUOMCode(uom);
		}
			
//		}
		
	}	
	private void uniqueUOMCode(PmUom uom){

		ISearchBean<PmUom> searchBean =  new SearchBean<PmUom>();
		String uomCode = uom.getUomCode().trim();
		Long uomId = uom.getUomId();
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"uomCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						uomCode, true));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						1));
		if(uomId!=null){			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"uomId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							uomId));
		}
		
		
		UomReqDTO reqDTO = new UomReqDTO();
		reqDTO.setMeasurementSearchBean(searchBean);
		
		IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);	
		UomResDTO resDTO = reqSO.retrieveMeasurementBySearch(reqDTO);
		if (resDTO.isSuccessful()) {
			if(resDTO.getMeasurementSearchResultBean()!=null && resDTO.getMeasurementSearchResultBean().getResultList()!=null 
					&& !resDTO.getMeasurementSearchResultBean().getResultList().isEmpty()){
				if(uom.isBrandNew()){
					throw new EpException(MessageConst.ERR_900012,new String[]{uomCode});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.uom.uomcode")
					});	
				}
				  
			}
		}
	}
	
	/**
	 * save and update unit of measurement details.
	 * 
	 * @return the string
	 */
	public String saveMeasurementDtls() {
		log.info("saveMeasurementDtls");

		String messageId = null;

		// PmUom oldPmUom=this.getManagedBean().getOldPmUom();
		PmUom pmUom = this.getManagedBean().getPmUom();
     	validationMessage(pmUom);

		UomReqDTO reqDTO = new UomReqDTO();
		reqDTO.setPmUom(pmUom);

		String[] msgParam = new String[] { pmUom.getUomCode() };

		IUomSO iUomSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUomSO.class);

	

		try {
			if (pmUom.isBrandNew()) {
				messageId = MessageConst.TD_80600;
			} else {
				messageId = MessageConst.TD_80601;

				/*
				 * if(oldPmUom!=null && pmUom!=null &&
				 * !oldPmUom.getRecordStatusFlg
				 * ().equals(pmUom.getRecordStatusFlg())){
				 * if(pmUom.getRecordStatusFlg
				 * ().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
				 * recordStatusMessageId = MessageConst.TD_80603; } else{
				 * recordStatusMessageId = MessageConst.TD_80602; }
				 * reqDTO.getTrackDairyVOs
				 * ().add(PmCommonUtil.assignTrackingDairyForUOM
				 * (pmUom.getUomId(),
				 * pmUom.getUomCode(),getPmActionDesc(recordStatusMessageId
				 * ,msgParam))); }
				 */
			}
			reqDTO.setTrackingStatus(true);

			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyForUOM(pmUom
							.getUomId(), pmUom.getUomCode(), 
							pmUom.getRecordStatusFlg().longValue(),
							getPmActionDesc(messageId, msgParam)));

			UomResDTO resDTO = iUomSO.saveOrUpdateMeasurement(reqDTO);
			reloadUOMData(resDTO.getPmUom());
			PmUom oldPmUomData = (PmUom) BeanUtils.cloneBean(resDTO.getPmUom());
			this.getManagedBean().setOldPmUom(oldPmUomData);

			if (resDTO.isSuccessful()) {
				if (pmUom.isBrandNew()) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_780091,
															new String[] { resDTO
																	.getPmUom()
																	.getUomCode() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_780092,
															new String[] { resDTO
																	.getPmUom()
																	.getUomCode() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			iUomSO = null;
		}
		if (pmUom.isBrandNew())
			return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMain.xhtml";
		else
			return "/xhtml/pm/unitofmeasurement/unitOfMeasurementMaintenance.xhtml";
	}	

	/**
	 * Gets the pm action desc.
	 * 
	 * @param messageId
	 *            the message id
	 * @param params
	 *            the params
	 * @return the pm action desc
	 */
	private String getPmActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}
	
}
