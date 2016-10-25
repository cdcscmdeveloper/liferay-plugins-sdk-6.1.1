/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.race;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.race.IRaceSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.race.RaceReqDTO;
import my.ep.dto.pm.common.race.RaceResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.race.RaceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.ResourceBundleUtil;

/**
 * Backing Bean Class for Race.
 * 
 * @author Ken
 * @since 24.10.2016
 * 
 */
@ManagedBean(name = "raceBackingBean")
@ViewScoped
public class RaceBackingBean extends
		BaseBackingBean<RaceManagedBean> implements Serializable { 
	
	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_MAIN =  "raceMain";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/race/raceMaintenance.xhtml";
	
	private static final Logger log = EpLogger
			.getLogger(RaceBackingBean.class);	
	
	private IPagingDataModel<PmRace> raceDataModel = new RacePagingDataModel();	
	
	/**
	 *  The constructor used for save user logging.
	 *  	 
	 **/
	public RaceBackingBean() {
		log.info("inside constructor");
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			log.info("***userId: " + pmUser.getUserId());
			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}	
	
	/**
	 * reload to race search page 
	 * 
	 */		
	public String redirectRaceSearch() {
		debugLogger("redirectRaceSearch");
		return NAV_TO_MAIN;

	}	
	
	/**
	 * Reset race search 
	 */
	public void resetRaceSearch() {
		debugLogger("resetRaceSearch");
		this.getManagedBean().setLazy(false);
        this.getManagedBean().setRaceCode(null);
        this.getManagedBean().setRaceNames(null);
        this.getManagedBean().setRecordStatus(null);

        raceDataModel.resetData();
	}
	
	/**
	 * reload to race maintain page
	 * 
	 */	
	public String createRace() {
		debugLogger("createRace");
		PmRace pmRace= new PmRace();
		this.getManagedBean().setPmRace(pmRace);
		return NAV_TO_MAINTAIN;
	}	

	/**
	 * get status item dropdown
	 * 
	 */	
	 
	 public static SelectItem[] getStatusList() {
		return PmCommonUtil.getCommonStatusOptions();
	}


	/**
	 * search race info based 
	 * on race codes and names search
	 */
	private ISearchBean<PmRace> getSearchBeanData(ISearchBean<PmRace> searchBean){				
			if (StringUtils.isNotBlank(getManagedBean().getRaceCode())) {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"raceCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						getManagedBean().getRaceCode(),
						true));
			}

			if (StringUtils.isNotBlank(getManagedBean().getRaceNames())) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"raceName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getRaceNames(),
						true));
			}	
			
			if(getManagedBean().getRecordStatus() != null) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"recordStatusFlg",
						SearchOperator.OPERATOR_EQUALS.value(),
						getManagedBean().getRecordStatus()
				));
			}
			else{
				List<String> activeAndInActiveStatus = new ArrayList<String>();
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
				activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_IN.value(), 
								activeAndInActiveStatus
				));
			}

			searchBean.getSortColumns().add(new SearchOrderBean("raceName",true));
			
			return searchBean;
		}	 
	 
	public void retrieveRace(){
			debugLogger("retrieveRace");		
			getManagedBean().setLazy(true);
			raceDataModel.reloadData();
	}	
	
	private class RacePagingDataModel extends PagingDataModel<PmRace> {
		private static final long serialVersionUID = 1L;

		public RacePagingDataModel() {
			super("raceTb",getManagedBean().isLazy());
		}

		@Override
		public ISearchResultBean<PmRace> pageChangeListener(ISearchBean<PmRace> searchBean) {
			IRaceSO raceSO = null;
			try {	
				ISearchBean<PmRace> searchBeanData = getSearchBeanData(searchBean);
				RaceReqDTO reqDTO = new RaceReqDTO();
				reqDTO.setSearchBean(searchBeanData);
				raceSO = (IRaceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRaceSO.class);	
				RaceResDTO resDTO = raceSO.retrieveRaceListBySearchBean(reqDTO);
				if (resDTO.isSuccessful() && resDTO.getSearchResultBean() != null) {
					return resDTO.getSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				 raceSO = null;
			}
			return null;
		}
	}
	
	/**
	 * get race info into maintenance page
	 */
	public String goToMaintaincePage(PmRace pmRace) {
		debugLogger("goToMaintaincePage");
		this.getManagedBean().setPmRace(pmRace);

		return NAV_TO_MAINTAIN;
	}	
	
	/**
	 * Validation message for race details
	 * 
	 */
	private void validationMessage(PmRace race){
		debugLogger("validationMessage");
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == race.getRecordStatusFlg()){
			if(!StringUtils.isNumeric(race.getRaceCode())){
				String numericErrors = ResourceBundleUtils.getMessage(
						"lbl.pm.race.code.error", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
				throw new EpException(numericErrors);
			}
			
			String raceNameErrors = isExistRaceCode(race);
			if (StringUtils.isNotBlank(raceNameErrors)) {
				if(race.isBrandNew()){
					throw new EpException(MessageConst.ERROR_779100, new String[] {
							race.getRaceCode(),
							raceNameErrors
					});	
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.race.code")
					});	
				}
			}
			
			if(isExistRaceName(race)){
				if(race.isBrandNew()){
					throw new EpException(MessageConst.ERROR_779101, new String[] {
							race.getRaceName()
					});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.race.name")
					});	
				}
			}
		}
		
	}		
	
	/**
	 * save and update race details
	 * 
	 */
	 public String saveRaceDtls(){
		 debugLogger("saveRaceDtls");
		 IRaceSO raceSO = null;
		 String returnUrl = null;
		 
		 try{
			 PmRace race= this.getManagedBean().getPmRace();
			 validationMessage(race);
			 RaceReqDTO reqDTO = new RaceReqDTO();
			 reqDTO.setRace(race);
			 
			 raceSO = EpEjbBusinessDelegate.getInstance().
					 getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRaceSO.class);
			 
			 RaceResDTO resDTO= raceSO.saveOrUpdateRaceDtls(reqDTO);			 
			 if(resDTO.isSuccessful()){
				 this.getManagedBean().setPmRace(resDTO.getRace());
				 if (race.isBrandNew()) {
					 returnUrl = NAV_TO_MAIN;
					 PmCommonUtil.showFacesMsg(
							 FacesContext.getCurrentInstance(), 
							 FacesMessage.SEVERITY_INFO, 
							 MessageConst.INFO_779102, 
							 new String[] {
										resDTO.getRace().getRaceCode(),
										resDTO.getRace().getRaceCode()});
					}else{
						returnUrl = NAV_TO_MAINTAIN;
						PmCommonUtil.showFacesMsg(
								 FacesContext.getCurrentInstance(), 
								 FacesMessage.SEVERITY_INFO, 
								 MessageConst.INFO_779103,new String[]{});				
					}
				}
		 }catch(Exception e){
			 throw e;
		 }finally{
			 raceSO = null;
		 }
		 return returnUrl;
	 }
	 
	 public String isExistRaceCode(PmRace race) {
		   IRaceSO raceSO = null;
			String raceName = null;
			try {
				RaceReqDTO reqDTO = new RaceReqDTO();
				raceSO = (IRaceSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IRaceSO.class);
				ISearchBean<PmRace> searchBean = new SearchBean<PmRace>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), 
								DbValueConst.RECORD_STATUS_ACTIVE
				));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("raceCode",
								SearchOperator.OPERATOR_EQUALS.value(), 
								race.getRaceCode().toLowerCase().trim(),
								true
				));
				
				if(!race.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("raceId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									race.getRaceId()
					));
				}

				reqDTO.setSearchBean(searchBean);
				RaceResDTO resDTO = raceSO.retrieveRaceListBySearchBean(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getRaceList() !=null 
						&& !resDTO.getRaceList().isEmpty()){
					raceName = resDTO.getRaceList().get(0).getRaceName();
				}
				
			} finally {
				raceSO = null;
			}
			return raceName;
	}
	 /**
		 *  This method used for reset the search bean field and
		 *  navigate to the search main page. 
		 * 
		 **/
	 public void preLoadingRace(){
			log.info("preLoadingRace");
			renewSesionObject();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, NAV_TO_MAIN);
	}
	 
	 public boolean isExistRaceName(PmRace race) {
			IRaceSO raceSO = null;
			boolean isExist = false;
			try {
				RaceReqDTO reqDTO = new RaceReqDTO();
				raceSO = (IRaceSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IRaceSO.class);
				ISearchBean<PmRace> searchBean = new SearchBean<PmRace>();
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(), 
								DbValueConst.RECORD_STATUS_ACTIVE
				));
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("raceName",
								SearchOperator.OPERATOR_EQUALS.value(), 
								race.getRaceName().toLowerCase().trim(),
								true
				));
				
				if(!race.isBrandNew()){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("raceId",
									SearchOperator.OPERATOR_NOT_EQUALS.value(), 
									race.getRaceId()
					));
				}
				
				reqDTO.setSearchBean(searchBean);
				RaceResDTO resDTO = raceSO.retrieveRaceListBySearchBean(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getRaceList() !=null 
						&& !resDTO.getRaceList().isEmpty()){
					isExist = true;
				}
				
			} finally {
				raceSO = null;
			}
			return isExist;
		}

	/**
	 * @return the raceDataModel
	 */
	public IPagingDataModel<PmRace> getRaceDataModel() {
		return raceDataModel;
	}

	/**
	 * @param raceDataModel the raceDataModel to set
	 */
	public void setRaceDataModel(IPagingDataModel<PmRace> raceDataModel) {
		this.raceDataModel = raceDataModel;
	}
	 
	 
}
