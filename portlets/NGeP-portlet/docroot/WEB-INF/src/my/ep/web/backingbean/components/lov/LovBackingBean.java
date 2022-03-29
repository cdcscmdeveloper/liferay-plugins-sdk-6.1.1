package my.ep.web.backingbean.components.lov;

import java.io.Serializable;
import java.util.ArrayList;
//20200909@akmal - Enhancement28951 - Start
import java.util.HashMap;
import java.util.Iterator;
//20200909@akmal - Enhancement28951 - End
import java.util.List;
//20200909@akmal - Enhancement28951 - Start
import java.util.Map;
//20200909@akmal - Enhancement28951 - End
import java.util.ResourceBundle;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cc.lov.ILovSO;
import my.ep.constant.MessageConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.cc.lov.LovReqDTO;
import my.ep.dto.cc.lov.LovResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.aut.AutUser;
//20200909@akmal - Enhancement28951 - Start
import my.ep.persistence.entity.pm.common.PmBusinessNature;
//20200909@akmal - Enhancement28951 - End
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.bean.LovBean;
import my.ep.web.bean.LovColumnBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.simplequote.SimpleQuoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//20200909@akmal - Enhancement28951 - Start
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
//20200909@akmal - Enhancement28951 - End
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name="lovBackingBean")
@ViewScoped
public class LovBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LovBackingBean.class);

//	private ILovEvent ?ookupEvent = null;
	
	private IPagingDataModel searchDataModel = new SearchPagingDataModel();
	private SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private String lovId = null;
	private ISearchBean lovSearchBean = new SearchBean();
	
	private LovBean lovBean = null;
	private List<SelectItem> searchOptions = new ArrayList<SelectItem>();
	
	private List<?> resultList = new ArrayList();
	private Object selectedEntity;
	private List<BaseEntity> selectedEntities;
	//20200909@akmal - Enhancement28951 - Start
	private Map<Long,BaseEntity> persistedEntities = new HashMap<Long,BaseEntity>();
	//20200909@akmal - Enhancement28951 - End
//	private BaseEntity[] selectedEntities;
	
	private List<LovColumnBean> columnsToDisplay = new ArrayList<LovColumnBean>();
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
        log.info(message);
    }
	
	private class SearchPagingDataModel extends PagingDataModel<AutUser> {
		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("lovTb", true);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public ISearchResultBean pageChangeListener(ISearchBean searchBean) {
			
			debugLogger("getLovBean(): " + getLovBean());
			if(getLovBean() == null) {
				return new SearchResultBean();
			}
			
			if(getLovBean().getLovId() == null) {
				debugLogger("=======================lovId null, return empty result ===========================");
				SearchResultBean srb = new SearchResultBean();
				srb.setResultList(new ArrayList());
				return srb;
			}
			
			if(!getLovBean().isDisplayResultWhenFirstLoad()) {
				enableDisplayResult();
				SearchResultBean srb = new SearchResultBean();
				srb.setResultList(new ArrayList());
				return srb;
			}
			
			debugLogger("pageChangeListener");
			
			searchBean.getDataFilterBeans().clear();
			return searchAction(searchBean, false);
		}
	}

	protected ISearchResultBean searchAction(ISearchBean searchBean, boolean isThis) {
		ILovSO lovSO = null;
		try {				
			LovReqDTO reqDTO = new LovReqDTO();
			
			if(lovSearchBean.getDataFilterBeans().size() <= 1) {
				constructSearchBean(getLovBean().getConditionField(), isThis);
			}
//			searchBean.getDataFilterBeans().clear();
			searchBean.getDataFilterBeans().addAll(getLovSearchBean().getDataFilterBeans());

			// check if operator = LIKE, then set to case insensitive
			for(int i = 0; i < searchBean.getDataFilterBeans().size(); i++) {
				/* For OR condition - BEGIN */
				if(searchBean.getDataFilterBeans().get(i) instanceof SearchMultiFilterBean){
					SearchMultiFilterBean multiFilterBean = (SearchMultiFilterBean) searchBean.getDataFilterBeans().get(i);
					SearchFilterBean filterBean1 = (SearchFilterBean) multiFilterBean.getSearchFilterBean1();
					SearchFilterBean filterBean2 = (SearchFilterBean) multiFilterBean.getSearchFilterBean2();
					
					if(filterBean1.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean1.setCaseInsensitive(true);
					}
					if(filterBean2.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean2.setCaseInsensitive(true);
					}
				/* For OR condition - END */
				}else{
					SearchFilterBean filterBean = (SearchFilterBean) searchBean.getDataFilterBeans().get(i);
					if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean.setCaseInsensitive(true);
					}
				}
			}
			
			debugLogger("searchBean.getDataFilterBeans(): " + searchBean.getDataFilterBeans());
			debugLogger("searchBean.getDataFilterBeans().size(): " + searchBean.getDataFilterBeans().size());
			
			reqDTO.setSearchBean(searchBean);
			reqDTO.setDaoClass(getLovBean().getDaoClass());
			reqDTO.setSearchMethod(getLovBean().getSearchMethod());
			
			// Added below method as part of SIR 22631.
			//excludeConsultant(reqDTO);
						
			lovSO = (ILovSO)BusinessDelegate.getInstance().getServiceObject("cc", ILovSO.class);
//			ISearchResultBean resultBean = lovSO.getSearchList(lovSearchBean);
			
			debugLogger("reqDTO: " + reqDTO);
			debugLogger("reqDTO.getSearchBean(): " + reqDTO.getSearchBean());
			
			LovResDTO resDTO = lovSO.getSearchList(reqDTO);
			ISearchResultBean resultBean = resDTO.getResultBean();	
			
			return resultBean;
		} finally {
			lovSO = null;
		}
	}
	
	/**
	 * This method will add filter to exclude <b>Consultant</b> supplier type from Supplier Listing.
	 * 
	 * @param reqDTO
	 */
	@SuppressWarnings("unchecked")
	private void excludeConsultant(LovReqDTO reqDTO) {
		debugLogger("Entered " + this.getClass().getName() + ".excludeConsultant(): ");
		if ("retriveSupplierAndGSTInfos".equals(reqDTO.getSearchMethod()) && null != reqDTO.getSearchBean()
				&& null != reqDTO.getSearchBean().getDataFilterBeans()) {
			debugLogger("Entered " + this.getClass().getName() + ".excludeConsultant(): Adding filter to exclude Consultant.");
			SearchFilterBean searchFilterBean = new SearchFilterBean();
			searchFilterBean.setAndOr(SearchCondition.CONDITION_AND.value());
			searchFilterBean.setCaseInsensitive(true);
			searchFilterBean.setSearchField("smAppl.smSupplier.smMofAccounts.supplierType");
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_NOT_EQUALS.value());
			searchFilterBean.setSearchValue(SupplierManagementConst.SUPP_TYPE_CONSULTANT);
			reqDTO.getSearchBean().getDataFilterBeans().add((searchFilterBean));
			debugLogger("Entered " + this.getClass().getName() + ".excludeConsultant(): Added filter to exclude Consultant.");
		}
		debugLogger("Exit " + this.getClass().getName() + ".excludeConsultant(): ");
	}
	
	/**
	 * this method is to search and populate the actual object from DB when user enter any value in the LoV inputText
	 * @param event
	 * @throws Exception
	 */
	public void populateObject(ValueChangeEvent event) throws Exception {
		lovSearchBean.getDataFilterBeans().clear();
		
		lovBean = getBeanByLovId((String)event.getComponent().getAttributes().get("lovId"));
//		ISearchResultBean resultBean = searchAction(lovSearchBean, true);
		String conditionField = (String)event.getComponent().getAttributes().get("conditionField");
		String saveObject = (String)event.getComponent().getAttributes().get("saveObject");
		
		lovBean.setSaveObject(saveObject);
		lovBean.setSelectionMode("single");
		
		debugLogger("event.getNewValue(): " + event.getNewValue());
		debugLogger("lovBean.getConditionField(): " + lovBean.getConditionField());
		
		if(StringUtils.contains(conditionField, "^this")) {
			getLovBean().setConditionField(StringUtils.replace(conditionField, "this", (String)event.getNewValue()));
			debugLogger("new condition: " + getLovBean().getConditionField());
			
			ISearchResultBean resultBean = searchAction(lovSearchBean, true);
			if(!resultBean.getResultList().isEmpty()) {
				BaseEntity entity = (BaseEntity)resultBean.getResultList().get(0);
				log.info("entity: " + entity);
				this.setSelectedEntity(entity);
				populateToSaveObject();
			} else {
//				throw new EpException(MessageConst.ERROR_721011);
				throw new AbortProcessingException("Invalid valid");
//				throw Exce
			}
		}
	}
	
	/**
	 * initialize LoV's properties when user click on the LoV button. 
	 */
	public void initLov() {

		FacesScopes facesScopes = new FacesScopes();
		String headerText = facesScopes.getParameter("headerText");
		String saveObject = facesScopes.getParameter("saveObject");
		int saveObjectIndex = extractSaveObjectIndex(saveObject);
		String componentId = facesScopes.getParameter("componentId");
		String conditionField = facesScopes.getParameter("conditionField");
		String clientId = facesScopes.getParameter("clientId");
		String update = facesScopes.getParameter("update");
		String postUpdateListener = facesScopes.getParameter("postUpdateListener");
		String postUpdateListenerWithIndex = facesScopes.getParameter("postUpdateListenerWithIndex");
		String selectionMode = facesScopes.getParameter("selectionMode");
		String autoUpdateValue = facesScopes.getParameter("autoUpdateValue");
		String displayResultWhenFirstLoad = facesScopes.getParameter("displayResultWhenFirstLoad");
		String displayResultWhenReset = facesScopes.getParameter("displayResultWhenReset");
		
		debugLogger("headerText: " + headerText);
		debugLogger("saveObject: " + saveObject);
		debugLogger("componentId: " + componentId);
		debugLogger("conditionField: " + conditionField);
		debugLogger("clientId: " + clientId);
		debugLogger("update: " + update);
		debugLogger("postUpdateListener: " + postUpdateListener);
		debugLogger("autoUpdateValue: " + autoUpdateValue);
		debugLogger("displayResultWhenFirstLoad: " + displayResultWhenFirstLoad);
		debugLogger("displayResultWhenReset: " + displayResultWhenReset);

//		if(lovId != null && lovId.equals(facesScopes.getParameter("lovId"))) {
//			
//		} else if(lovId == null || !lovId.equals(facesScopes.getParameter("lovId"))) {
			lovId = facesScopes.getParameter("lovId");
			lovBean = getBeanByLovId(lovId);
			lovBean.setLovId(lovId);
			lovBean.setTitle(headerText);
			lovBean.setSaveObject(saveObject);
			lovBean.setSaveObjectIndex(saveObjectIndex);
			lovBean.setComponentId(componentId);
			lovBean.setConditionField(conditionField);
			lovBean.setUpdate(update);
			lovBean.setPostUpdateListener(postUpdateListener);
			lovBean.setPostUpdateListenerWithIndex(postUpdateListenerWithIndex);
			lovBean.setSelectionMode(selectionMode);
			lovBean.setAutoUpdateValue(autoUpdateValue);
			lovBean.setDisplayResultWhenFirstLoad(Boolean.valueOf(displayResultWhenFirstLoad));
			lovBean.setDisplayResultWhenReset(Boolean.valueOf(displayResultWhenReset));
			
//			lovBean.setClientId(clientId.substring(clientId.indexOf(":")).concat(":").concat(componentId));
			if(lovBean.getSelectionMode().equals("single")) {
				lovBean.setClientId("@(:input[id*=" + StringUtils.replace(clientId.substring(clientId.indexOf(":") + 1).concat(":").concat(componentId), ":", "\\\\:") + "])");
			}
			debugLogger("client id: " + lovBean.getClientId());
			
			prepareColumnTitle(lovBean);
			createSearchOptions(lovBean);
			
			columnsToDisplay.clear();
			for(LovColumnBean column : lovBean.getColumns()) {
				if(column.isViewable()) {
					
					ValueExpression valueExpression = null;
					if(column.getMethodName() != null) {
						valueExpression = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
								.createValueExpression(FacesContext.getCurrentInstance().getELContext(),
										"#{" + column.getMethodName() + "}", Object.class);
					} else {
						valueExpression = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
								.createValueExpression(FacesContext.getCurrentInstance().getELContext(),
										"#{entity." + column.getColumnName() + "}", Object.class);
					}
					
					column.setColumnNameve(valueExpression);
					
					columnsToDisplay.add(column);
				}
			}
			
//			constructSearchBean(conditionField);
//			initSearchBean();
//		}
		resetAction();
		//#29832 - start
		if(getLovBean().getLovId().equalsIgnoreCase("PM_BUSINESS_NATURE")){
			FacesScopes facesScopesLovID = new FacesScopes();
			facesScopesLovID.setSessionScope("LovID_Business_Nature", "PM_BUSINESS_NATURE");
			removeSelectedBusinessNature();
		}
		//#29832 - end
	}
	
	/**
	 * method bind to the 'Search' button in the LoV popup
	 */
	public void searchAction() {

		validateBeforeSearch();
		
		searchDataModel.reloadData();
	}
	
	/** 
	 * extract the saveObejct string from user's parameter
	 * @param saveObject
	 * @return
	 */
	private int extractSaveObjectIndex(String saveObject) {
		int index1 = saveObject.indexOf("[");
		int index2 = saveObject.indexOf("]");

		if(index1 < 0 || index2 < 0) {
			return -1;
		}
		
		String saveOjbectIndex = saveObject.substring(index1 + 1, index2);
		
		return (saveOjbectIndex == null) ? 0 : Integer.parseInt(saveOjbectIndex);
	}
	
	/**
	 * validate the search value in the lov popup
	 */
	private void validateBeforeSearch() {
		
		Object searchValue1 = ((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(0)).getSearchValue();
		if(StringUtils.isNotBlank(searchValue1.toString())) {
			if(((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(0)).getSearchField() == null ||
					((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(0)).getSearchField().length() == 0) {
				throw new EpException(MessageConst.ERROR_100060);
			}
		}
		
		/*Object searchValue2 = ((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(2)).getSearchValue();
		if(StringUtils.isNotBlank(searchValue2.toString())) {
			if(((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(2)).getSearchField() == null ||
					((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(2)).getSearchField().length() == 0) {
				throw new EpException(MessageConst.WARN_755000);
			}
		}
		
		Object searchValue3 = ((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(1)).getSearchValue();
		if(StringUtils.isNotBlank(searchValue3.toString())) {
			if(((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(1)).getSearchField() == null ||
					((SearchFilterBean)this.getLovSearchBean().getDataFilterBeans().get(1)).getSearchField().length() == 0) {
				throw new EpException(MessageConst.WARN_755000);
			}
		}*/
	}

	public void resetAction() {
//		this.setResultList(new ArrayList());
		this.getSearchDataModel().resetData();
		this.setLovSearchBean(new SearchBean());
//		this.setLovBean(new LovBean());
		//20200909@akmal - Enhancement28951 - Start
		if(this.getSelectedEntities() != null){
			this.getSelectedEntities().clear();
		}		
		//20200909@akmal - Enhancement28951 - End
		if(!getLovBean().isDisplayResultWhenReset()) {
			disableDisplayResult();
		}
		
		//9442 - start
		persistedEntities.clear();
		//9442 - end
	}
	

	/**
	 * Reset all.
	 */
	public void resetAll() {
		this.getSearchDataModel().resetData();
		this.setLovSearchBean(new SearchBean());
		this.setLovBean(new LovBean());
		
		//9442 - start
		persistedEntities.clear();
		//9442 - end
	}
	
	//OR: columnName1^value1*columnName1^value2
	private void constructSearchBean(String conditionField, boolean isThis) {
		debugLogger("In constructSearchBean");		
		if(StringUtils.isNotBlank(conditionField)) {
			debugLogger("conditionField: " + conditionField);
			
			String[] conditionFields = conditionField.split("\\|");
			for(int i = 0; i < conditionFields.length; i++) {
				debugLogger("conditionFields[" + i +"] " + conditionFields[i]);
				/* For OR condition - BEGIN */
				String[] orFields = conditionFields[i].split("\\*"); 
				if(orFields!=null && orFields.length==2){
					debugLogger("filter OR");
					SearchFilterBean[] filterBeans = new SearchFilterBean[2];
					
					for(int j=0; j<orFields.length; j++){
						debugLogger("orFields["+j+"] "+orFields[j]);
						String[] fieldMapping =orFields[j].split("\\^");
						SearchFilterBean filterBean = new SearchFilterBean();
						
						filterBean.setSearchField(fieldMapping[0]);
						
						if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
//							filterBean.setSearchValue(String.valueOf(filterBean.getSearchValue()).toLowerCase());
							filterBean.setCaseInsensitive(true);
						}
						
						if(fieldMapping.length >= 3) {
							filterBean.setSearchOperator(Integer.parseInt(fieldMapping[2]));
							
							if (Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_IN.value()
									|| Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_NOT_IN.value()) {
								String[] listArray = fieldMapping[1].split("\\,");
								List<String> list = new ArrayList<String>();
								for(String obj : listArray) {
									list.add(obj);
								}
								filterBean.setSearchValue(list);
							} else {
								filterBean.setSearchValue(fieldMapping[1]);
							}
							
						} else {
							if(!isThis && fieldMapping[1].equals("this")) {
								continue;
							} else if(!isThis) {
								filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());					
								filterBean.setSearchValue(fieldMapping[1]);
							} else {
								filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
								filterBean.setSearchValue(fieldMapping[1]);
								filterBean.setCaseInsensitive(true);
							}
						}
						filterBeans[j] = filterBean;
					}

					filterBeans[1].setAndOr(SearchCondition.CONDITION_OR.value());
					lovSearchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									filterBeans[0],
									filterBeans[1])
							);
				/* For OR condition - END */
				}else{
					debugLogger("filter AND");
					String[] fieldMapping = conditionFields[i].split("\\^");
					SearchFilterBean filterBean = new SearchFilterBean();
					
					filterBean.setSearchField(fieldMapping[0]);
					
					if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
//						filterBean.setSearchValue(String.valueOf(filterBean.getSearchValue()).toLowerCase());
						filterBean.setCaseInsensitive(true);
					}
					
					if(fieldMapping.length >= 3) {
						filterBean.setSearchOperator(Integer.parseInt(fieldMapping[2]));
						
						if (Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_IN.value()
								|| Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_NOT_IN.value()) {
							String[] listArray = fieldMapping[1].split("\\,");
							List<String> list = new ArrayList<String>();
							for(String obj : listArray) {
								list.add(obj);
							}
							filterBean.setSearchValue(list);
						} else {
							filterBean.setSearchValue(fieldMapping[1]);
						}
						
					} else {
						if(!isThis && fieldMapping[1].equals("this")) {
							continue;
						} else if(!isThis) {
							filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());					
							filterBean.setSearchValue(fieldMapping[1]);
						} else {
							filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
							filterBean.setSearchValue(fieldMapping[1]);
							filterBean.setCaseInsensitive(true);
						}
					}				
					lovSearchBean.getDataFilterBeans().add(filterBean);		
				}		
			}
		}
	}
	
	private void createSearchOptions(LovBean lovBean) {
		this.setSearchOptions(new ArrayList<SelectItem>());
		if(lovBean.getColumns() != null && lovBean.getColumns().size() > 0) {
			for(LovColumnBean column : lovBean.getColumns()) {
				if(column.isSearchable()){
					SelectItem item = new SelectItem(column.getColumnName(), column.getColumnTitle());
					this.getSearchOptions().add(item);
				}
			}
		}
	}
	
	public List<SelectItem> getSearchOperators() {
		List<SelectItem> operatorList = new ArrayList<SelectItem>();
		
		SelectItem item1 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), ResourceBundleUtils.getMessage("lbl.operator.equals",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item2 = new SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.startWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		SelectItem item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(), ResourceBundleUtils.getMessage("lbl.operator.endWith",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
//		SelectItem item4 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), ResourceBundleUtils.getMessage("lbl.operator.like",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()));
		
		operatorList.add(item1);
		operatorList.add(item2);
		operatorList.add(item3);
//		operatorList.add(item4);
		
		return operatorList;
	}
	
	
	protected LovBean getBeanByLovId(String lovId) {
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
		ServletContext servletContext = request.getSession().getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		LovBean lovBean = (LovBean)webApplicationContext.getBean(lovId);
		
		return lovBean;
	}
	
	public void populateToSaveObject() {
		debugLogger("In populateToSaveObject");
		
		FacesContext context = FacesContext.getCurrentInstance();
		String expressionString = this.getLovBean().getSaveObject();
			
		debugLogger("Save Object: " + expressionString);
		// FL Debug. Please remove it after tested.
		log.info("FL Debug. LovBackingBean.populateToSaveObject()");
		log.info("Save Object: " + expressionString);
		log.info("LOV ID:"+ getLovBean().getLovId());
			
		if(!FacesScopes.isValueReference(expressionString)) {
			expressionString = "#{" + expressionString + "}";
		}
		
		if(this.getLovBean().getSelectionMode().equalsIgnoreCase("single")) {
			
			debugLogger("this.getSelectedEntity(): " + this.getSelectedEntity());
			
			if(selectedEntity != null) {
				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
													.createValueExpression(context.getELContext(),
															expressionString, Object.class);
				
				if(valueExpression != null) {
					valueExpression.setValue(context.getELContext(), selectedEntity);
				}
		
				debugLogger("postUpdateListener: " + this.getLovBean().getPostUpdateListener());
					
				if(StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListener())) {
		
					MethodExpression postUpdateListenerMethodExpr = FacesScopes
		                        .createMethodExpression(context,
		                        		this.getLovBean().getPostUpdateListener(), null,
		                                new Class[] { BaseEntity.class });
					
		            postUpdateListenerMethodExpr.invoke(context.getELContext(),
		                        new Object[] { selectedEntity });
				}
				
				debugLogger("postUpdateListenerWithIndex: " + this.getLovBean().getPostUpdateListenerWithIndex());
				
				if(StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListenerWithIndex())) {
					
					MethodExpression postUpdateListenerMethodExpr = FacesScopes
		                        .createMethodExpression(context,
		                        		this.getLovBean().getPostUpdateListenerWithIndex(), null,
		                                new Class[] { BaseEntity.class, int.class });
					
		            postUpdateListenerMethodExpr.invoke(context.getELContext(),
		                        new Object[] { selectedEntity, this.getLovBean().getSaveObjectIndex() });
				}
				
	
				this.resetAction();
	
			}
		} else	if(this.getLovBean().getSelectionMode().equalsIgnoreCase("multiple")) {
			//20200909@akmal - Enhancement28951 - Start
			if(!persistedEntities.isEmpty()) selectedEntities = new ArrayList<BaseEntity>(persistedEntities.values());
			//20200909@akmal - Enhancement28951 - End
			debugLogger("this.getSelectedEntities().size(): " + this.getSelectedEntities().size());
			debugLogger("this.getSelectedEntities(): " + this.getSelectedEntities());
			
			if(selectedEntities != null && !selectedEntities.isEmpty()) {
				
				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
													.createValueExpression(context.getELContext(),
															expressionString, List.class);
		
				valueExpression.setValue(context.getELContext(), selectedEntities);
		
				debugLogger("postUpdateListener" + this.getLovBean().getPostUpdateListener());
					
				if(StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListener())) {
		
					MethodExpression postUpdateListenerMethodExpr = FacesScopes
		                        .createMethodExpression(context,
		                        		this.getLovBean().getPostUpdateListener(), null,
		                                new Class[] { List.class });
					
		            postUpdateListenerMethodExpr.invoke(context.getELContext(),
		                        new Object[] { selectedEntities });
				}
	
				this.resetAction();
	
			}
		}
		
	}
	
	/**
	 * set Auto Update Value
	 * 
	 */
	public void setAutoUpdateValue() {
		LovBean lovBean = this.getLovBean();
		if (lovBean != null) {
			String autoUpdateValue = lovBean.getAutoUpdateValue();
			if (StringUtils.isNotBlank(autoUpdateValue)) {
				String enableAutoUpdate = (String)FacesContext.getCurrentInstance()
						.getExternalContext().getRequestParameterMap().get("enableAutoUpdate");
				
				if (StringUtils.isNotBlank(enableAutoUpdate)) {
					if(!FacesScopes.isValueReference(autoUpdateValue)) {
						autoUpdateValue = "#{" + autoUpdateValue + "}";
					}
					
					FacesContext context = FacesContext.getCurrentInstance();
					ValueExpression autoUpdateVE = context.getApplication().getExpressionFactory()
							.createValueExpression(context.getELContext(),
									autoUpdateValue, Boolean.class);
					autoUpdateVE.setValue(context.getELContext(), Boolean.valueOf(enableAutoUpdate));	
				}
			}	
		}
	}
	
	public boolean isSelectButtonDisable() {
		if(getLovBean()!=null && getLovBean().getSelectionMode()!=null) {
			if(getLovBean().getSelectionMode().equalsIgnoreCase("single")) {
				if(getSelectedEntity()!=null) {
					return false;
				}
			} else if(getSelectedEntities()!=null && getSelectedEntities().size()>0){
				// #29832 & #9442 added if else to make sure the selected cb save after tick. 
				// Default save after click next page onPageChange listener
				if(getLovBean().getLovId().equalsIgnoreCase("PM_BUSINESS_NATURE")){
					for(BaseEntity item : selectedEntities){
						if(item instanceof PmBusinessNature){
							Long key = ((PmBusinessNature) item).getBusinessNatureId();
							if(!persistedEntities.containsKey(key))
								persistedEntities.put(key, item);
							
						}
					}
				}
				return false;
			}
		}
		return true;
	}
	
	private void prepareColumnTitle(LovBean lovBean) {
		ResourceBundle bundle = null;
		for(LovColumnBean column : lovBean.getColumns()) {
			bundle = ResourceBundle.getBundle(column.getBundleFile(), new FacesScopes().getUserLocale());
			column.setColumnTitle(bundle.getString(column.getBundleKey()));
		}
	}
	
	//20200909@akmal - Enhancement28951 - Start
	public void onUnselectRow(UnselectEvent event){
		Object value = event.getObject();
		if(value instanceof PmBusinessNature){
			Long key = ((PmBusinessNature) value).getBusinessNatureId();
			if(persistedEntities.containsKey(key)) 
				persistedEntities.remove(key);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onToggleSelect(ToggleSelectEvent event){
		List<BaseEntity> templist = !selectedEntities.isEmpty() ? selectedEntities : searchDataModel.getList();
		//#32721 start
		if (templist instanceof BaseEntity){
			for(BaseEntity value : templist){
				if(value instanceof PmBusinessNature){
					Long key = ((PmBusinessNature) value).getBusinessNatureId();
					if(persistedEntities.containsKey(key)) 
						persistedEntities.remove(key);
					else
						persistedEntities.put(key,value);
				}
			}
		}
		//#32721 ends
	}
	
	public void onPageChange(PageEvent event){
		// put selected into hashmap
		
		//#29832 #9442 added if else for Pm_business_nature to skip save selected cb onPageChage
		if (getLovBean().getLovId().equalsIgnoreCase("PM_BUSINESS_NATURE")){
			
		}else{
			for(BaseEntity item : selectedEntities){
				if(item instanceof PmBusinessNature){
					Long key = ((PmBusinessNature) item).getBusinessNatureId();
					if(!persistedEntities.containsKey(key))
						persistedEntities.put(key, item);
				}
			}
		}
		
	}
	
	public void populateSelectedItems(){
		selectedEntities = new ArrayList<BaseEntity>(persistedEntities.values()); 
	}
	//20200909@akmal - Enhancement28951 - End
	
	private void enableDisplayResult() {
		getLovBean().setDisplayResultWhenFirstLoad(true);
	}
	
	private void disableDisplayResult() {
		getLovBean().setDisplayResultWhenFirstLoad(false);
	}
	
	public IPagingDataModel<AutUser> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<AutUser> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}
	
	public List<SelectItem> getSearchOptions() {
		return searchOptions;
	}

	public void setSearchOptions(List<SelectItem> searchOptions) {
		this.searchOptions = searchOptions;
	}

	public ISearchBean getLovSearchBean() {
		if(lovSearchBean.getDataFilterBeans().size() == 0) {
			for(int i = 0; i < 1; i++) {
				SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				lovSearchBean.getDataFilterBeans().add(filterBean);
			}
		}
		return lovSearchBean;
	}

	public void setLovSearchBean(ISearchBean lovSearchBean) {
		this.lovSearchBean = lovSearchBean;
	}

	public LovBean getLovBean() {
		return lovBean;
	}

	public void setLovBean(LovBean lovBean) {
		this.lovBean = lovBean;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public Object getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(Object selectedEntity) {
		this.selectedEntity = selectedEntity;
	}

	public List<BaseEntity> getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntities(List<BaseEntity> selectedEntities) {
		this.selectedEntities = selectedEntities;
	}


	public List<LovColumnBean> getColumnsToDisplay() {
		return columnsToDisplay;
	}

	public void setColumnsToDisplay(List<LovColumnBean> columnsToDisplay) {
		this.columnsToDisplay = columnsToDisplay;
	}
	//20200909@akmal - Enhancement28951 - Start
	public Map<Long, BaseEntity> getPersistedEntities() {
		return persistedEntities;
	}

	public void setPersistedEntities(Map<Long, BaseEntity> persistedEntities) {
		this.persistedEntities = persistedEntities;
	}
	//20200909@akmal - Enhancement28951 - End
	
	/**
	 * @author zaim
	 * #29832 start - to remove auto populate checkbox after delete the list for business nature
	 * 
	 * **/
	public void removeSelectedBusinessNature() {
		SimpleQuoteManagedBean simpleQuoteMB = new SimpleQuoteManagedBean();
		if (getLovBean().getSimpleQuoteBackingBean() != null) {
			simpleQuoteMB = getLovBean().getSimpleQuoteBackingBean().deleteSuccess();
			if (simpleQuoteMB.getDeleteBusinessNature()) {
				if (simpleQuoteMB.getDeletedBusinessNatureMap() != null) {
					if (simpleQuoteMB.getDeletedBusinessNatureMap().size() > 0) {
						for (Long item : simpleQuoteMB.getDeletedBusinessNatureMap().keySet()) {
							debugLogger("Delete key " + item);

							for (Iterator<Map.Entry<Long, BaseEntity>> it = persistedEntities.entrySet().iterator(); it
									.hasNext();) {
								Map.Entry<Long, BaseEntity> entry = it.next();
								if (entry.getKey().equals(item)) {
									it.remove();
								}
							}
						}
						simpleQuoteMB = new SimpleQuoteManagedBean();
					}
				}
			}
		} else {
			SimpleQuoteBackingBean bb = new SimpleQuoteBackingBean();
			getLovBean().setSimpleQuoteBackingBean(bb);
		}
	}
	// #29832 end
}
