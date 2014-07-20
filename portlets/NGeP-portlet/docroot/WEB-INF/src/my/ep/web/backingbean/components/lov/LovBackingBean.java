package my.ep.web.backingbean.components.lov;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cc.lov.ILovSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cc.lov.LovReqDTO;
import my.ep.dto.cc.lov.LovResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.aut.AutUser;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.LovBean;
import my.ep.web.bean.LovColumnBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

	WebApplicationContext webApplicationContext = null;
//	private ILovEvent ?ookupEvent = null;
	
	private IPagingDataModel searchDataModel = new SearchPagingDataModel();
	private SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private String lovId = null;
	private ISearchBean lovSearchBean = new SearchBean();
	private LovBean lovBean = null;
	private List<SelectItem> searchOptions = new ArrayList<SelectItem>();
	
	private List resultList = new ArrayList();
	private BaseEntity selectedEntity;
	private List<BaseEntity> selectedEntities;
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
			
			debugLogger("pageChangeListener");
			
			searchBean.getDataFilterBeans().clear();
			return searchAction(searchBean, false);
			
//			ILovSO lovSO = null;
//			try {				
//				LovReqDTO reqDTO = new LovReqDTO();
//				
//				if(lovSearchBean.getDataFilterBeans().size() <= 3) {
//					constructSearchBean(getLovBean().getConditionField());
//				}
//				searchBean.getDataFilterBeans().clear();
//				searchBean.getDataFilterBeans().addAll(getLovSearchBean().getDataFilterBeans());
//				
//				// check if operator = LIKE, then set to case insensitive
//				for(int i = 0; i < searchBean.getDataFilterBeans().size(); i++) {
//					SearchFilterBean filterBean = (SearchFilterBean) searchBean.getDataFilterBeans().get(i);
//					if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
//						filterBean.setCaseInsensitive(true);
//					}
//				}
//				
//				debugLogger("searchBean.getDataFilterBeans(): " + searchBean.getDataFilterBeans());
//				debugLogger("searchBean.getDataFilterBeans().size(): " + searchBean.getDataFilterBeans().size());
//				
//				reqDTO.setSearchBean(searchBean);
//				reqDTO.setDaoClass(getLovBean().getDaoClass());
//				reqDTO.setSearchMethod(getLovBean().getSearchMethod());
//				
//				lovSO = (ILovSO)BusinessDelegate.getInstance().getServiceObject("cc", ILovSO.class);
////				ISearchResultBean resultBean = lovSO.getSearchList(lovSearchBean);
//				
//				debugLogger("reqDTO: " + reqDTO);
//				debugLogger("reqDTO.getSearchBean(): " + reqDTO.getSearchBean());
//				
//				LovResDTO resDTO = lovSO.getSearchList(reqDTO);
//				ISearchResultBean resultBean = resDTO.getResultBean();	
//				
//				return resultBean;
//			} finally {
//				lovSO = null;
//			}
		}
	}

	protected ISearchResultBean searchAction(ISearchBean searchBean, boolean isThis) {
		
		ILovSO lovSO = null;
		try {				
			LovReqDTO reqDTO = new LovReqDTO();
			
			if(lovSearchBean.getDataFilterBeans().size() <= 3) {
				constructSearchBean(getLovBean().getConditionField(), isThis);
			}
//			searchBean.getDataFilterBeans().clear();
			searchBean.getDataFilterBeans().addAll(getLovSearchBean().getDataFilterBeans());

			// check if operator = LIKE, then set to case insensitive
			for(int i = 0; i < searchBean.getDataFilterBeans().size(); i++) {
				SearchFilterBean filterBean = (SearchFilterBean) searchBean.getDataFilterBeans().get(i);
				if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
					filterBean.setCaseInsensitive(true);
				}
			}
			
			debugLogger("searchBean.getDataFilterBeans(): " + searchBean.getDataFilterBeans());
			debugLogger("searchBean.getDataFilterBeans().size(): " + searchBean.getDataFilterBeans().size());
			
			reqDTO.setSearchBean(searchBean);
			reqDTO.setDaoClass(getLovBean().getDaoClass());
			reqDTO.setSearchMethod(getLovBean().getSearchMethod());
			
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
	
//	public static String parseColumnName() {
//		
//	}
	
/*	public void searchAction() {
		System.out.println("===============================search action");
	
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
		ServletContext servletContext = request.getSession().getServletContext();
		webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		Object object = webApplicationContext.getBean("SCR_NOTIFICATION_LIST");
		
		System.out.println(object);
	}*/
	
	public void initLov() {

		FacesScopes facesScopes = new FacesScopes();
		String headerText = facesScopes.getParameter("headerText");
		String saveObject = facesScopes.getParameter("saveObject");
		String componentId = facesScopes.getParameter("componentId");
		String conditionField = facesScopes.getParameter("conditionField");
		String clientId = facesScopes.getParameter("clientId");
		String update = facesScopes.getParameter("update");
		String postUpdateListener = facesScopes.getParameter("postUpdateListener");
		String selectionMode = facesScopes.getParameter("selectionMode");
		
		debugLogger("headerText: " + headerText);
		debugLogger("saveObject: " + saveObject);
		debugLogger("componentId: " + componentId);
		debugLogger("conditionField: " + conditionField);
		debugLogger("clientId: " + clientId);
		debugLogger("update: " + update);
		debugLogger("postUpdateListener: " + postUpdateListener);

//		if(lovId != null && lovId.equals(facesScopes.getParameter("lovId"))) {
//			
//		} else if(lovId == null || !lovId.equals(facesScopes.getParameter("lovId"))) {
			lovId = facesScopes.getParameter("lovId");
			lovBean = getBeanByLovId(lovId);
			lovBean.setLovId(lovId);
			lovBean.setTitle(headerText);
			lovBean.setSaveObject(saveObject);
			lovBean.setComponentId(componentId);
			lovBean.setConditionField(conditionField);
			lovBean.setUpdate(update);
			lovBean.setPostUpdateListener(postUpdateListener);
			lovBean.setSelectionMode(selectionMode);
			
//			lovBean.setClientId(clientId.substring(clientId.indexOf(":")).concat(":").concat(componentId));
			lovBean.setClientId("@(:input[id*=" + StringUtils.replace(clientId.substring(clientId.indexOf(":") + 1).concat(":").concat(componentId), ":", "\\\\:") + "])");

			debugLogger("client id: " + lovBean.getClientId());
			
			prepareColumnTitle(lovBean);
			createSearchOptions(lovBean);
			
			columnsToDisplay.clear();
			for(LovColumnBean column : lovBean.getColumns()) {
				if(column.isViewable()) {

					ValueExpression valueExpression = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
							.createValueExpression(FacesContext.getCurrentInstance().getELContext(),
									"#{entity." + column.getColumnName() + "}", Object.class);
					
					column.setColumnNameve(valueExpression);
					
					columnsToDisplay.add(column);
				}
			}
			
//			constructSearchBean(conditionField);
//			initSearchBean();
//		}
		resetAction();
	}
	
	public void searchAction() {
		
		searchDataModel.reloadData();
		
/*		ILovSO lovSO = null;
		try {
			LovReqDTO reqDTO = new LovReqDTO();
			
			if(lovSearchBean.getDataFilterBeans().size() <= 5) {
				constructSearchBean(this.getLovBean().getConditionField());
			}
			
			reqDTO.setSearchBean(lovSearchBean);
			reqDTO.setDaoClass(this.getLovBean().getDaoClass());
			reqDTO.setSearchMethod(this.getLovBean().getSearchMethod());
			
			for(int i = 0; i < lovSearchBean.getDataFilterBeans().size(); i++) {
				SearchFilterBean filter = (SearchFilterBean)lovSearchBean.getDataFilterBeans().get(i);
				System.out.println(filter.getSearchField() + " " + filter.getSearchOperator() + " " + filter.getSearchValue());
			}
			
			lovSO = (ILovSO)BusinessDelegate.getInstance().getServiceObject("cc", ILovSO.class);
//			ISearchResultBean resultBean = lovSO.getSearchList(lovSearchBean);
			
			LovResDTO resDTO = lovSO.getSearchList(reqDTO);
			ISearchResultBean resultBean = resDTO.getResultBean();			
			
			this.setResultList(resultBean.getResultList());
		} finally {
			lovSO = null;
		}*/
	}

	public void resetAction() {
//		this.setResultList(new ArrayList());
		this.getSearchDataModel().updatePagingResults(null);
		this.setSearchDataModel(new SearchPagingDataModel());
		this.setLovSearchBean(new SearchBean());
//		this.setLovBean(new LovBean());
	}
	
	private void constructSearchBean(String conditionField, boolean isThis) {
		debugLogger("In constructSearchBean");
		if(StringUtils.isNotBlank(conditionField)) {
			debugLogger("conditionField: " + conditionField);
			
			String[] conditionFields = conditionField.split("\\|");
			for(int i = 0; i < conditionFields.length; i++) {
				String[] fieldMapping = conditionFields[i].split("\\^");
				SearchFilterBean filterBean = new SearchFilterBean();
				
				filterBean.setSearchField(fieldMapping[0]);
				
				if(filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
//					filterBean.setSearchValue(String.valueOf(filterBean.getSearchValue()).toLowerCase());
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
						System.out.println("this 3");
						filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
						filterBean.setSearchValue(fieldMapping[1]);
						filterBean.setCaseInsensitive(true);
					}
				}				
				lovSearchBean.getDataFilterBeans().add(filterBean);					
			}
		}
	}
	
	private void createSearchOptions(LovBean lovBean) {
		this.setSearchOptions(new ArrayList<SelectItem>());
		if(lovBean.getColumns() != null && lovBean.getColumns().size() > 0) {
			for(LovColumnBean column : lovBean.getColumns()) {
				SelectItem item = new SelectItem(column.getColumnName(), column.getColumnTitle());
				this.getSearchOptions().add(item);
			}
		}
	}
	
	public List<SelectItem> getSearchOperators() {
		List<SelectItem> operatorList = new ArrayList<SelectItem>();
		
		SelectItem item1 = new SelectItem(SearchOperator.OPERATOR_EQUALS.value(), "EQUALS");
		SelectItem item2 = new SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(), "STARTS WITH");
		SelectItem item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(), "END WITH");
//		SelectItem item4 = new SelectItem(SearchOperator.OPERATOR_BETWEEN.value(), "BETWEEN");
		SelectItem item4 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), "LIKE");
		
		operatorList.add(item1);
		operatorList.add(item2);
		operatorList.add(item3);
		operatorList.add(item4);
		
		return operatorList;
	}
	
/*	private void initSearchBean() {
		lovSearchBean = new SearchBean();
		for(int i = 0; i < 4; i++) {
			SearchFilterBean filterBean = new SearchFilterBean();
			lovSearchBean.getDataFilterBeans().add(filterBean);
		}
	}*/
	
	protected LovBean getBeanByLovId(String lovId) {
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
		ServletContext servletContext = request.getSession().getServletContext();
		webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		LovBean lovBean = (LovBean)webApplicationContext.getBean(lovId);
		
		return lovBean;
	}
	
	public void populateToSaveObject() {
		debugLogger("In populateToSaveObject");
		
		FacesContext context = FacesContext.getCurrentInstance();
		String expressionString = this.getLovBean().getSaveObject();
			
		debugLogger("Save Object: " + expressionString);
			
		if(!FacesScopes.isValueReference(expressionString)) {
			expressionString = "#{" + expressionString + "}";
		}
		
		if(this.getLovBean().getSelectionMode().equalsIgnoreCase("single")) {
			
			debugLogger("this.getSelectedEntity(): " + this.getSelectedEntity());
			
			if(selectedEntity != null) {
				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
													.createValueExpression(context.getELContext(),
															expressionString, BaseEntity.class);
		
				valueExpression.setValue(context.getELContext(), selectedEntity);
		
				debugLogger("postUpdateListener" + this.getLovBean().getPostUpdateListener());
					
				if(StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListener())) {
		
					MethodExpression postUpdateListenerMethodExpr = FacesScopes
		                        .createMethodExpression(context,
		                        		this.getLovBean().getPostUpdateListener(), null,
		                                new Class[] { BaseEntity.class });
					
		            postUpdateListenerMethodExpr.invoke(context.getELContext(),
		                        new Object[] { selectedEntity });
				}
	
				this.resetAction();
				this.setLovBean(new LovBean());
	
			}
		} else	if(this.getLovBean().getSelectionMode().equalsIgnoreCase("multiple")) {
			
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
				this.setLovBean(new LovBean());
	
			}
		}
		
	}
	
	private void prepareColumnTitle(LovBean lovBean) {
		ResourceBundle bundle = null;
		for(LovColumnBean column : lovBean.getColumns()) {
			bundle = ResourceBundle.getBundle(column.getBundleFile());
			column.setColumnTitle(bundle.getString(column.getBundleKey()));
		}
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
			for(int i = 0; i < 3; i++) {
				SearchFilterBean filterBean = new SearchFilterBean();
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

	public BaseEntity getSelectedEntity() {
		return selectedEntity;
	}

	public void setSelectedEntity(BaseEntity selectedEntity) {
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
	
}
