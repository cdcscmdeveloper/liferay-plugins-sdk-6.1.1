package my.ep.web.backingbean.pm.glaccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.VPmGo;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.glaccount.PmGoSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import com.sun.faces.util.CollectionsUtils;

@ManagedBean(name = "pmGoSearchBackingBean")
@ViewScoped
public class PmGoSearchBackingBean extends BaseBackingBean<PmGoSearchManagedBean> {

		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(PmGoSearchBackingBean.class);

		// Paging for listing
	private IPagingDataModel<VPmGo> pmGoDataModel = new PmGoPagingDataModel();

	//210108@akmal - Enhancement27624 - Start
	private List<VPmGo> selectedEntities = new ArrayList<VPmGo>();
	private Map<String,VPmGo> persistedEntities = new HashMap<String,VPmGo>();
	//210108@akmal - Enhancement27624 - End
	
	public PmGoSearchBackingBean() {

	}

	public void initPmGoPopup(){
		this.renewSesionObject();
		//17022021 - 30071 & 24975 - start -->
		resetAction();
		//17022021 - 30071 & 24975 - end -->	
		try{
			FacesScopes facesScopes = new FacesScopes();
			Integer index = Integer.parseInt(facesScopes.getParameter("index"));
			String type = facesScopes.getParameter("type");
			String update = facesScopes.getParameter("update");
			Long kptjId = Long.parseLong(facesScopes.getParameter("kptjId"));
			//210801@akmal - Enhancement27624 - Start
			String selectionMode = facesScopes.getParameter("selectionMode");
			if(selectionMode != null && selectionMode.equals("multiple")){
				getManagedBean().setSelectionMode(selectionMode);
				getManagedBean().setDisableSelectbtn(true);
			}else{//11022021@azza - Bug #30066 - Start
				getManagedBean().setSelectionMode("single");
			}//11022021@azza - Bug #30066 - End
			String saveObject = facesScopes.getParameter("saveObject");
			getManagedBean().setSaveObject(saveObject);
			//210801@akmal - Enhancement27624 - End
			getManagedBean().setIndex(index);
			getManagedBean().setType(type);
			getManagedBean().setUpdate(update);
			getManagedBean().setKptjId(kptjId);
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}	
	}
	
	//210118@akmal - Enhancement27624 - Start
	public void resetAction(){
		log.info("resetAction - START");
		getManagedBean().setLazy(false);
		getManagedBean().setDesc(null);
		getManagedBean().setSelectedAccClass(null);
		getManagedBean().setSelectedAccType(null);
		getManagedBean().setSelectedGenObjCode(null);
		getManagedBean().setSelectedGoSoObjCode(null);
		retrieveAccountType();
		retrieveAccountClass();
		//11022021@azza - Bug #30066 - Start
		if (this.getManagedBean().isDisableSelectbtnPmGoDt() == true){
       	this.getManagedBean().setDisableSelectbtnPmGoDt(false);
		}
        //11022021@azza - Bug #30066 - End
		//200911@akmal - Enhancement27624 - Start
		selectedEntities.clear();
		persistedEntities.clear();
		//200911@akmal - Enhancement27624 - End
		getPmGoDataModel().resetData();
		log.info("selectedEntities - "+selectedEntities.size());
		log.info("persistedEntities - "+persistedEntities.size());
		log.info("resetAction - END");
	}
	//210118@akmal - Enhancement27624 - End
	
	public List<PmParameterDesc> retrieveAccountType() {
		log.info("retrieveAccountType");
		List<PmParameterDesc> accountTypeList = ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.TYPE_OF_ACCOUNT_TYPE, null);
		if(CollectionUtils.isNotEmpty(accountTypeList) && accountTypeList.size() == 1){
			getManagedBean().setAccTypeDisabled(true);
			getManagedBean().setSelectedAccType(accountTypeList.get(0).getCodeName());
		}
		return accountTypeList;
	}

	public SelectItem[] retrieveAccountClass() {
		log.info("retrieveAccountClass");
		SelectItem[] items = null;
		if (StringUtils.isNotBlank(getManagedBean().getSelectedAccType())) {
			IObjectSO objectSO = null;
			try {
				int i = 1;
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				SearchBean<String> accGoSearchBean = new SearchBean<String>();
				accGoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedAccType()));
				accGoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				accGoSearchBean.getSortColumns().add(new SearchOrderBean("accClass"));

				reqDTO.setAccGoSearchBean(accGoSearchBean);
				objectSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IObjectSO.class);
				ObjectResDTO resDTO = objectSO
						.retrieveAccClassListBySearchBean(reqDTO);
				List<String> list = resDTO.getAccGoResultBean().getResultList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				for (String object : list) {
					items[i++] = new SelectItem(object,
							object);
				}
				
				if(CollectionUtils.isNotEmpty(list) && list.size() == 1){
					getManagedBean().setAccClassDisabled(true);
					getManagedBean().setSelectedAccClass(list.get(0));
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
			  objectSO = null;
			}
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		return items;
	}
	
	public SelectItem[] retrieveGoCodeList() {
		log.info("retrieveGoCodeList");
		SelectItem[] items = null;
		if (StringUtils.isNotBlank(getManagedBean().getSelectedAccType())) {
			IObjectSO objectSO = null;
			try {
				int i = 1;
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				SearchBean<String> accGoSearchBean = new SearchBean<String>();
				accGoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedAccType()));
				
				accGoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accClass",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedAccClass()));
				
				accGoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				accGoSearchBean.getSortColumns().add(new SearchOrderBean("goCode"));

				reqDTO.setAccGoSearchBean(accGoSearchBean);
				objectSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IObjectSO.class);
				ObjectResDTO resDTO = objectSO
						.retrieveGoCodeListBySearchBean(reqDTO);
				List<String> list = resDTO.getAccGoResultBean().getResultList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				for (String object : list) {
					items[i++] = new SelectItem(object,
							object);
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
			  objectSO = null;
			}
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		return items;
	}

	public void searchGoView(){
		this.getManagedBean().setLazy(true);
		//11022021@azza - Bug #30066 - Start
		if (this.getManagedBean().isDisableSelectbtnPmGoDt() == true) {
			this.getManagedBean().setDisableSelectbtnPmGoDt(false);
		}
	    //11022021@azza - Bug #30066 - End
		getPmGoDataModel().reloadData();
	}
	
	private class PmGoPagingDataModel extends PagingDataModel<VPmGo> {
		private static final long serialVersionUID = 1L;

		public PmGoPagingDataModel() {
			super("pmGoDt",getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<VPmGo> pageChangeListener(
				ISearchBean<VPmGo> searchBean) {
			IObjectSO objectSO = null;
			try {
				ISearchBean<VPmGo> searchBeanData = getSearchBeanData(searchBean);
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setvPmGoSearchBean(searchBeanData);
				
				objectSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveGOListBySearchBean(reqDTO);				
				if (resDTO != null && resDTO.isSuccessful()) {
					return resDTO.getvPmGoResultBean();
				}
			}finally{
				objectSO = null;
			}
			return null;
		}
	}
	
	private ISearchBean<VPmGo> getSearchBeanData(
			ISearchBean<VPmGo> searchBean) {
		log.info("getSearchBeanData - Start");
	
		if (StringUtils.isNotBlank(getManagedBean().getSelectedAccType())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("accType",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedAccType()));
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getSelectedAccClass())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("accClass",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedAccClass()));
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getSelectedGenObjCode())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("goCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedGenObjCode()));
		}
		
		//log.info("[#24973] - new filter bean for GoSo code - start");
		if (StringUtils.isNotBlank(getManagedBean().getSelectedGoSoObjCode())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("gosoCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedGoSoObjCode()));
		}
		//log.info("[#24973] - new filter bean for GoSo code - end");
		
		if (StringUtils.isNotBlank(getManagedBean().getDesc())) {
//			searchBean.getDataFilterBeans().add(
//					new SearchFilterBean("desc",
//							SearchOperator.OPERATOR_LIKE.value(),
//							getManagedBean().getDesc().toLowerCase(),true));
			
//			SearchFilterBean subClassFilterBean = new SearchFilterBean("glAccCode",
//					SearchOperator.OPERATOR_LIKE.value(), 
//					getManagedBean().getDesc().toLowerCase(),
//					SearchCondition.CONDITION_OR.value());
//			subClassFilterBean.setCaseInsensitive(true);
			
			//log.info("[#24973] - modified filter - start");
			SearchFilterBean subClassFilterBean = new SearchFilterBean("gosoCode",
			SearchOperator.OPERATOR_LIKE.value(), 
			getManagedBean().getDesc().toLowerCase(),
			SearchCondition.CONDITION_OR.value());
			subClassFilterBean.setCaseInsensitive(true);
			//log.info("[#24973] - modified filter - end");
			
			searchBean.getDataFilterBeans().add(
					new SearchMultiFilterBean(new SearchFilterBean("desc",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getDesc().toLowerCase(),true), 
							subClassFilterBean));
		}
		
		if (getManagedBean().getKptjId() != null) {
			searchBean.getDataFilterBeans().add(
			new SearchMultiFilterBean(new SearchFilterBean(
					"ptjGroupId",
					SearchOperator.OPERATOR_EQUALS.value(),
					getManagedBean().getKptjId()), new SearchFilterBean(
					"ptjGroupId",
					SearchOperator.OPERATOR_IS_NULL.value(), null,
					SearchCondition.CONDITION_OR.value())));
		}
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatus",
						SearchOperator.OPERATOR_EQUALS.value(),
						DbValueConst.RECORD_STATUS_ACTIVE));
		
		return searchBean;
		
	}
	
	public void okAction(String methodActionStr) {
		log.info("pmGO - okAction");
		//200911@akmal - Enhancement27624 - Start
		if(this.getManagedBean().getSelectionMode().equals("single")){
			if (StringUtils.isNotBlank(methodActionStr) && getManagedBean().getvPmGo() != null){
				PmGlAccount glAccount = getManagedBean().getvPmGo().getGlAccount();
				MethodExpression methodExp = null;
				if(getManagedBean().getIndex() != null && StringUtils.isNotBlank(getManagedBean().getType())){
					methodExp = FacesScopes
			                .createMethodExpression(FacesContext.getCurrentInstance(),
			                		methodActionStr, null,
			                        new Class[] { PmGlAccount.class, Integer.class, String.class });
					methodExp.invoke(
							FacesContext.getCurrentInstance().getELContext(), 
							new Object[]{glAccount,getManagedBean().getIndex(),getManagedBean().getType()});
				}else{
					methodExp = FacesScopes
			                .createMethodExpression(FacesContext.getCurrentInstance(),
			                		methodActionStr, null,
			                        new Class[] { PmGlAccount.class });
					methodExp.invoke(
							FacesContext.getCurrentInstance().getELContext(), 
							new Object[]{glAccount});
				}
				RequestContext.getCurrentInstance().execute("pmGoPopup.hide();");
			}
		}
		else if(this.getManagedBean().getSelectionMode().equals("multiple")){
			FacesContext context = FacesContext.getCurrentInstance();
			String expressionString = this.getManagedBean().getSaveObject();
			
			if(!FacesScopes.isValueReference(expressionString)) {
				expressionString = "#{" + expressionString + "}";
			}
			
			if (StringUtils.isNotBlank(methodActionStr) && selectedEntities.size() > 0){
				MethodExpression methodExp = null;
				if(!persistedEntities.isEmpty()) selectedEntities = new ArrayList<VPmGo>(persistedEntities.values());
				
				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
						.createValueExpression(context.getELContext(),
								expressionString, List.class);

				valueExpression.setValue(context.getELContext(), selectedEntities);
				
				methodExp = FacesScopes
		                .createMethodExpression(context,
		                		methodActionStr, null,
		                        new Class[] { List.class });
				methodExp.invoke(
						context.getELContext(), 
						new Object[]{selectedEntities});
				RequestContext.getCurrentInstance().execute("pmGoPopup.hide();");
			}
		}
		//200911@akmal - Enhancement27624 - End
	}

	/**
	 * @return the pmGoDataModel
	 */
	public IPagingDataModel<VPmGo> getPmGoDataModel() {
		return pmGoDataModel;
	}

	/**
	 * @param pmGoDataModel the pmGoDataModel to set
	 */
	public void setPmGoDataModel(IPagingDataModel<VPmGo> pmGoDataModel) {
		this.pmGoDataModel = pmGoDataModel;
	}
	
	public void onChangeAccType(){
		getManagedBean().setSelectedAccClass(null);
		getManagedBean().setSelectedGenObjCode(null);
		getManagedBean().setSelectedGoSoObjCode(null);
	}
	
	public void onChangeAccClass(){
		getManagedBean().setSelectedGenObjCode(null);
		getManagedBean().setSelectedGoSoObjCode(null);
	}
	
	public void onChangeGenObjCode(){
		getManagedBean().setSelectedGoSoObjCode(null);
	}
	
	public SelectItem[] retrieveGoSoCodeList() {
		log.info("retrieveGoSoCodeList");
		
		SelectItem[] items = null;
		//log.info("[#24973] - GO Code: " + getManagedBean().getSelectedGenObjCode());
		if (StringUtils.isNotBlank(getManagedBean().getSelectedGenObjCode())) {
			IObjectSO objectSO = null;
			try {
				int i = 1;
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				SearchBean<String> accGoSoSearchBean = new SearchBean<String>();
				accGoSoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accType",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedAccType()));
				
				accGoSoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("accClass",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedAccClass()));
				
				accGoSoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("goCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getSelectedGenObjCode()));
				
				accGoSoSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				
				accGoSoSearchBean.getSortColumns().add(new SearchOrderBean("gosoCode"));

				reqDTO.setAccGoSoSearchBean(accGoSoSearchBean);
				objectSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IObjectSO.class);
				ObjectResDTO resDTO = objectSO
						.retrieveGoSoCodeListBySearchBean(reqDTO);
				List<String> list = resDTO.getAccGoSoResultBean().getResultList();
				items = new SelectItem[list.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
				for (String object : list) {
					items[i++] = new SelectItem(object, object);
				}
			} catch (Exception e) {
				throw new EpException(e.getMessage());
			} finally {
			  objectSO = null;
			}
		}else{
			items = new SelectItem[1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		return items;
	}

	//210801@akmal - Enhancement27624 - Start
	public void onSelectRow(SelectEvent event){
		VPmGo value = (VPmGo) event.getObject();
			String key = value.getGlAccCode();
			if(!persistedEntities.containsKey(key)) 
				persistedEntities.put(key, value);
			if(selectedEntities.isEmpty())
				getManagedBean().setDisableSelectbtn(true);
			else
				getManagedBean().setDisableSelectbtn(false);
	}
	
	public void onUnselectRow(UnselectEvent event){
		VPmGo value = (VPmGo) event.getObject();
			String key = value.getGlAccCode();
			if(persistedEntities.containsKey(key)) 
				persistedEntities.remove(key);
			if(selectedEntities.isEmpty())
				getManagedBean().setDisableSelectbtn(true);
			else
				getManagedBean().setDisableSelectbtn(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onToggleSelect(ToggleSelectEvent event){
		List<VPmGo> templist = (List<VPmGo>) (!selectedEntities.isEmpty() ? selectedEntities : pmGoDataModel.getList());
		for(VPmGo value : templist){
				String key = value.getGlAccCode();
				if(persistedEntities.containsKey(key)) 
					persistedEntities.remove(key);
				else
					persistedEntities.put(key,value);
		}
		if(selectedEntities.isEmpty())
			getManagedBean().setDisableSelectbtn(true);
		else
			getManagedBean().setDisableSelectbtn(false);
	}
	
	public void onPageChange(PageEvent event){
		// put selected into hashmap
		for(VPmGo item : selectedEntities){
			String key = item.getGlAccCode();
			if(!persistedEntities.containsKey(key))
				persistedEntities.put(key, item);
		}
		if(selectedEntities.isEmpty())
			getManagedBean().setDisableSelectbtn(true);
		else
			getManagedBean().setDisableSelectbtn(false);
	}
	
	public void populateSelectedItems(){
		selectedEntities = new ArrayList<VPmGo>(persistedEntities.values()); 
	}
	
	public List<VPmGo> getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntities(List<VPmGo> selectedEntities) {
		this.selectedEntities = selectedEntities;
	}

	public Map<String,VPmGo> getPersistedEntities() {
		return persistedEntities;
	}

	public void setPersistedEntities(Map<String,VPmGo> persistedEntities) {
		this.persistedEntities = persistedEntities;
	}
	//210801@akmal - Enhancement27624 - End
	
	// 11022021@azza - Bug #30066 - Start
	public void rowSelectPmGoDt() {
		System.out.println(this.getManagedBean().getvPmGo());
		if (this.getManagedBean().getvPmGo() != null) {
			this.getManagedBean().setDisableSelectbtnPmGoDt(true);
			System.out.println("rowSelect is Enable Button !!:" + this.getManagedBean().isDisableSelectbtnPmGoDt());
		} else {
			this.getManagedBean().setDisableSelectbtnPmGoDt(false);
			System.out.println("rowSelect is Disable Button !!:" + this.getManagedBean().isDisableSelectbtnPmGoDt());
		}
	}

	public void onPageChangePmGoDt(PageEvent event) {
		System.out.println(this.getManagedBean().getvPmGo());
		if (this.getManagedBean().getvPmGo() != null) {
			this.getManagedBean().setDisableSelectbtnPmGoDt(true);
			System.out.println("rowSelect is Enable Button !!:" + this.getManagedBean().isDisableSelectbtnPmGoDt());
		} else {
			this.getManagedBean().setDisableSelectbtnPmGoDt(false);
			System.out.println("rowSelect is Disable Button !!:" + this.getManagedBean().isDisableSelectbtnPmGoDt());
		}
	}
	//11022021@azza - Bug #30066 - End
}
