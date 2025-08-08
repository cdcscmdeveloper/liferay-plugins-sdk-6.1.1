package my.ep.web.backingbean.components.lovAudit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.common.audit.ILovAuditSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.dto.cc.lov.LovReqDTO;
import my.ep.dto.cc.lov.LovResDTO;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMException;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.bean.LovBean;
import my.ep.web.bean.LovColumnBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.simplequote.SimpleQuoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author normuliana #35509 PM Audit
 * 
 */
@ManagedBean(name = "lovAuditBackingBean")
@ViewScoped
public class LovAuditBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LovAuditBackingBean.class);

	private String lovId = null;
	@SuppressWarnings("rawtypes")
	private transient ISearchBean lovSearchBean = new SearchBean();
	private transient IPagingDataModel<PmUser> searchPmUserDataModel = new SearchPmUserPagingDataModel();

	private LovBean lovBean = new LovBean();
	private List<SelectItem> searchOptions = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	private transient List resultList = new ArrayList();
	private transient Object selectedEntity;
	private List<BaseEntity> selectedEntities;
	private Map<Long, BaseEntity> persistedEntities = new HashMap<>();

	private List<LovColumnBean> columnsToDisplay = new ArrayList<>();

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	private class SearchPmUserPagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public SearchPmUserPagingDataModel() {
			super("lovTbSP", true);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public ISearchResultBean pageChangeListener(ISearchBean searchBean) {

			debugLogger("getLovBean(): " + getLovBean());
			if (getLovBean() == null) {
				return new SearchResultBean();
			}

			if (getLovBean().getLovId() == null) {
				debugLogger("=======================lovId null, return empty result ===========================");
				SearchResultBean srb = new SearchResultBean();
				srb.setResultList(new ArrayList());
				return srb;
			}

			if (!getLovBean().isDisplayResultWhenFirstLoad()) {
				enableDisplayResult();
				SearchResultBean srb = new SearchResultBean();
				srb.setResultList(new ArrayList());
				return srb;
			}

			debugLogger("pageChangeListener");

			searchBean.getDataFilterBeans().clear();
			return searchActionByList(searchBean, false);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ISearchResultBean searchActionByList(ISearchBean searchBean, boolean isThis) {
		ILovAuditSO lovSO = null;
		try {
			LovReqDTO reqDTO = new LovReqDTO();

			if (lovSearchBean.getDataFilterBeans().size() <= 1) {
				constructSearchBean(getLovBean().getConditionField(), isThis);
			}

			searchBean.getDataFilterBeans().addAll(getLovSearchBean().getDataFilterBeans());
			log.info("AAAAAAAAa :::: " + lovSearchBean.getDataFilterBeans().size());
			log.info("BBBBBBBB :::: " + searchBean.getDataFilterBeans().size());

			for (int i = 0; i < lovSearchBean.getDataFilterBeans().size(); i++) {
				if (searchBean.getDataFilterBeans().get(i) instanceof SearchMultiFilterBean) {
					SearchMultiFilterBean multiFilterBean = (SearchMultiFilterBean) searchBean.getDataFilterBeans().get(i);
					SearchFilterBean filterBean1 = (SearchFilterBean) multiFilterBean.getSearchFilterBean1();
					SearchFilterBean filterBean2 = (SearchFilterBean) multiFilterBean.getSearchFilterBean2();

					if (filterBean1.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean1.setCaseInsensitive(true);
					}
					if (filterBean2.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean2.setCaseInsensitive(true);
					}
					log.info("filterBean1 ### ::: " + filterBean1);
					log.info("filterBean2 ### ::: " + filterBean2);
				} else {
					SearchFilterBean filterBean = (SearchFilterBean) searchBean.getDataFilterBeans().get(i);
					log.info("filterBeangetSearchField###$$$ ::: " + filterBean.getSearchField());
					log.info("filterBean ###$$$ getSearchValue::: " + filterBean.getSearchValue());
					if (filterBean.getSearchField() != null) {
						if ("userName".equalsIgnoreCase(filterBean.getSearchField())) {
							reqDTO.setUserName(filterBean.getSearchValue().toString());
						} else if ("identificationNo".equalsIgnoreCase(filterBean.getSearchField())) {
							reqDTO.setIdentificationNo(filterBean.getSearchValue().toString());
						} else if ("designation".equalsIgnoreCase(filterBean.getSearchField())) {
							reqDTO.setDesignation(filterBean.getSearchValue().toString());
						}
					}

					if (filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean.setCaseInsensitive(true);
					}
				}
			}
			log.info("getLovBean().getRoleCode() :::: " + getLovBean().getRoleCode());
			reqDTO.setSearchBean(searchBean);

			reqDTO.setRoleCode(lovBean.getRoleCode());

			lovSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ILovAuditSO.class);

			debugLogger("reqDTO: " + reqDTO);
			debugLogger("reqDTO.getSearchBean(): " + reqDTO.getSearchBean());

			LovResDTO resDTO = lovSO.getSearchListSP(reqDTO);
			return resDTO.getResultBean();
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "searchActionByList", "resDTO",
					"searchResult", Long.valueOf(searchBean.getUserId()), e);
		}
		return null;
	}

	/**
	 * this method is to search and populate the actual object from DB when user
	 * enter any value in the LoV inputText
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void populateObject(ValueChangeEvent event) throws AuditPMException {
		lovSearchBean.getDataFilterBeans().clear();

		lovBean = getBeanByLovId((String) event.getComponent().getAttributes().get("lovId"));
		lovBean = getrolecode((String) event.getComponent().getAttributes().get("roleCode"));
		String conditionField = (String) event.getComponent().getAttributes().get("conditionField");
		String saveObject = (String) event.getComponent().getAttributes().get("saveObject");

		lovBean.setSaveObject(saveObject);
		lovBean.setSelectionMode(AuditConst.SINGLE);

		debugLogger("event.getNewValue(): " + event.getNewValue());
		debugLogger("lovBean.getConditionField(): " + lovBean.getConditionField());

		if (StringUtils.contains(conditionField, "^this")) {
			getLovBean().setConditionField(StringUtils.replace(conditionField, "this", (String) event.getNewValue()));
			debugLogger("new condition: " + getLovBean().getConditionField());
			ISearchResultBean resultBean = searchActionByList(lovSearchBean, true);
			if (!resultBean.getResultList().isEmpty()) {
				BaseEntity entity = (BaseEntity) resultBean.getResultList().get(0);
				log.info("entity: " + entity);
				this.setSelectedEntity(entity);
				populateToSaveObject();
			} else {
				throw new AbortProcessingException("Invalid valid");
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
		String isStoreProc = facesScopes.getParameter("isStoreProc");
		String roleCode = facesScopes.getParameter("roleCode");

		FacesContext context = FacesContext.getCurrentInstance();
		String expressionString = roleCode;

		debugLogger("Save Object : " + expressionString);
		if (!FacesScopes.isValueReference(expressionString)) {
			expressionString = "#{" + expressionString + "}";
		}
		ValueExpression valueExpressions = context.getApplication().getExpressionFactory()
				.createValueExpression(context.getELContext(), expressionString, Object.class);

		String roleCodeVal = (String) valueExpressions.getValue(context.getELContext());

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
		debugLogger("isStoreProc: " + isStoreProc);
		debugLogger("roleCode: " + roleCodeVal);

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
		lovBean.setStoreProc(Boolean.valueOf(isStoreProc));
		lovBean.setRoleCode(String.valueOf(roleCodeVal));
		if (AuditConst.SINGLE.equals(lovBean.getSelectionMode())) {
			lovBean.setClientId("@(:input[id*="
					+ StringUtils.replace(clientId.substring(clientId.indexOf(':') + 1).concat(":").concat(componentId), ":",
							"\\\\:") + "])");
		}
		debugLogger("client id: " + lovBean.getClientId());

		prepareColumnTitle(lovBean);
		createSearchOptions(lovBean);

		columnsToDisplay.clear();
		for (LovColumnBean column : lovBean.getColumns()) {
			if (column.isViewable()) {

				ValueExpression valueExpression = null;
				if (column.getMethodName() != null) {
					valueExpression = FacesContext
							.getCurrentInstance()
							.getApplication()
							.getExpressionFactory()
							.createValueExpression(FacesContext.getCurrentInstance().getELContext(),
									"#{" + column.getMethodName() + "}", Object.class);
				} else {
					valueExpression = FacesContext
							.getCurrentInstance()
							.getApplication()
							.getExpressionFactory()
							.createValueExpression(FacesContext.getCurrentInstance().getELContext(),
									"#{entity." + column.getColumnName() + "}", Object.class);
				}

				column.setColumnNameve(valueExpression);

				columnsToDisplay.add(column);
			}
		}

		resetAction();
	}

	/**
	 * method bind to the 'Search' button in the LoV popup
	 */
	public void searchActionByList() {

		searchPmUserDataModel.reloadData();
	}

	/**
	 * extract the saveObejct string from user's parameter
	 * 
	 * @param saveObject
	 * @return
	 */
	private int extractSaveObjectIndex(String saveObject) {
		int index1 = saveObject.indexOf('[');
		int index2 = saveObject.indexOf(']');

		if (index1 < 0 || index2 < 0) {
			return -1;
		}

		String saveOjbectIndex = saveObject.substring(index1 + 1, index2);

		return (saveOjbectIndex == null) ? 0 : Integer.parseInt(saveOjbectIndex);
	}

	/**
	 * resetAction()
	 */
	@SuppressWarnings("rawtypes")
	public void resetAction() {
		if (getLovBean().isStoreProc()) {
			this.getSearchPmUserDataModel().resetData();
		}

		this.setLovSearchBean(new SearchBean());

		if (this.getSelectedEntities() != null) {
			this.getSelectedEntities().clear();

		}

		if (!getLovBean().isDisplayResultWhenReset()) {
			disableDisplayResult();
		}

		persistedEntities.clear();
	}

	/**
	 * Reset all.
	 */
	@SuppressWarnings("rawtypes")
	public void resetAll() {

		this.getSearchPmUserDataModel().resetData(); // #29416
		this.setLovSearchBean(new SearchBean());
		this.setLovBean(new LovBean());

		persistedEntities.clear();

		if (selectedEntities != null && !selectedEntities.isEmpty()) {
			selectedEntities.clear();
		}
	}

	// OR: columnName1^value1*columnName1^value2
	@SuppressWarnings("unchecked")
	private void constructSearchBean(String conditionField, boolean isThis) {
		debugLogger("In constructSearchBean");
		if (StringUtils.isNotBlank(conditionField)) {
			debugLogger("conditionField: " + conditionField);

			String[] conditionFields = conditionField.split("\\|");
			for (int i = 0; i < conditionFields.length; i++) {
				debugLogger("conditionFields[" + i + "] " + conditionFields[i]);
				/* For OR condition - BEGIN */
				String[] orFields = conditionFields[i].split("\\*");
				if (orFields != null && orFields.length == 2) {
					debugLogger("filter OR");
					SearchFilterBean[] filterBeans = new SearchFilterBean[2];

					for (int j = 0; j < orFields.length; j++) {
						debugLogger("orFields[" + j + "] " + orFields[j]);
						String[] fieldMapping = orFields[j].split("\\^");
						SearchFilterBean filterBean = new SearchFilterBean();

						filterBean.setSearchField(fieldMapping[0]);

						if (filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
							filterBean.setCaseInsensitive(true);
						}

						if (fieldMapping.length >= 3) {
							filterBean.setSearchOperator(Integer.parseInt(fieldMapping[2]));

							if (Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_IN.value()
									|| Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_NOT_IN.value()) {
								String[] listArray = fieldMapping[1].split("\\,");
								List<String> list = new ArrayList<>();
								for (String obj : listArray) {
									list.add(obj);
								}
								filterBean.setSearchValue(list);
							} else {
								filterBean.setSearchValue(fieldMapping[1]);
							}

						} else {
							if (!isThis && "this".equals(fieldMapping[1])) {
								continue;
							} else if (!isThis) {
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
					lovSearchBean.getDataFilterBeans().add(new SearchMultiFilterBean(filterBeans[0], filterBeans[1]));
					/* For OR condition - END */
				} else {
					debugLogger("filter AND");
					String[] fieldMapping = conditionFields[i].split("\\^");
					SearchFilterBean filterBean = new SearchFilterBean();

					filterBean.setSearchField(fieldMapping[0]);

					if (filterBean.getSearchOperator() == SearchOperator.OPERATOR_LIKE.value()) {
						filterBean.setCaseInsensitive(true);
					}

					if (fieldMapping.length >= 3) {
						filterBean.setSearchOperator(Integer.parseInt(fieldMapping[2]));

						if (Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_IN.value()
								|| Integer.parseInt(fieldMapping[2]) == SearchOperator.OPERATOR_NOT_IN.value()) {
							String[] listArray = fieldMapping[1].split("\\,");
							List<String> list = new ArrayList<>();
							for (String obj : listArray) {
								list.add(obj);
							}
							filterBean.setSearchValue(list);
						} else {
							filterBean.setSearchValue(fieldMapping[1]);
						}

					} else {
						if (!isThis && "this".equals(fieldMapping[1])) {
							continue;
						} else if (!isThis) {
							// 9442 - START
							if ("levelCode".equalsIgnoreCase(filterBean.getSearchField())) {
								filterBean.setSearchOperator(SearchOperator.OPERATOR_NOT_EQUALS.value());
							} else {
								filterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
							}
							// 9442 - END
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
		if (lovBean.getColumns() != null && lovBean.getColumns().size() > 0) {
			for (LovColumnBean column : lovBean.getColumns()) {
				if (column.isSearchable()) {
					SelectItem item = new SelectItem(column.getColumnName(), column.getColumnTitle());
					this.getSearchOptions().add(item);
				}
			}
		}
	}

	public List<SelectItem> getSearchOperators() {
		List<SelectItem> operatorList = new ArrayList<>();

		SelectItem item1 = new SelectItem(SearchOperator.OPERATOR_LIKE.value(), ResourceBundleUtils.getMessage(
				"lbl.operator.equals", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		SelectItem item2 = new SelectItem(SearchOperator.OPERATOR_STARTS_WITH.value(), ResourceBundleUtils.getMessage(
				"lbl.operator.startWith", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		SelectItem item3 = new SelectItem(SearchOperator.OPERATOR_END_WITH.value(), ResourceBundleUtils.getMessage(
				"lbl.operator.endWith", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

		operatorList.add(item1);
		operatorList.add(item2);
		operatorList.add(item3);

		return operatorList;
	}

	protected LovBean getBeanByLovId(String lovId) {
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest());
		ServletContext servletContext = request.getSession().getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		lovBean = (LovBean) webApplicationContext.getBean(lovId);
		return lovBean;
	}

	protected LovBean getrolecode(String rolecode) {
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest());
		ServletContext servletContext = request.getSession().getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return (LovBean) webApplicationContext.getBean(rolecode);
	}

	/**
	 * populateToSaveObject()
	 */
	public void populateToSaveObject() {
		debugLogger("In populateToSaveObject");

		FacesContext context = FacesContext.getCurrentInstance();
		String expressionString = this.getLovBean().getSaveObject();

		debugLogger("Save Object: " + expressionString);
		log.info("FL Debug. LovBackingBean.populateToSaveObject()");
		log.info("Save Object: " + expressionString);
		log.info("LOV ID:" + getLovBean().getLovId());

		if (!FacesScopes.isValueReference(expressionString)) {
			expressionString = "#{" + expressionString + "}";
		}

		if (this.getLovBean().getSelectionMode().equalsIgnoreCase(AuditConst.SINGLE)) {

			debugLogger("this.getSelectedEntity(): " + this.getSelectedEntity());

			if (selectedEntity != null) {
				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
						.createValueExpression(context.getELContext(), expressionString, Object.class);

				if (valueExpression != null) {
					valueExpression.setValue(context.getELContext(), selectedEntity);
				}

				debugLogger("postUpdateListener: " + this.getLovBean().getPostUpdateListener());

				if (StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListener())) {

					MethodExpression postUpdateListenerMethodExpr = FacesScopes.createMethodExpression(context, this.getLovBean()
							.getPostUpdateListener(), null, new Class[] { BaseEntity.class });

					postUpdateListenerMethodExpr.invoke(context.getELContext(), new Object[] { selectedEntity });
				}

				debugLogger("postUpdateListenerWithIndex: " + this.getLovBean().getPostUpdateListenerWithIndex());

				if (StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListenerWithIndex())) {

					MethodExpression postUpdateListenerMethodExpr = FacesScopes.createMethodExpression(context, this.getLovBean()
							.getPostUpdateListenerWithIndex(), null, new Class[] { BaseEntity.class, int.class });

					postUpdateListenerMethodExpr.invoke(context.getELContext(), new Object[] { selectedEntity,
							this.getLovBean().getSaveObjectIndex() });
				}

				this.resetAction();

			}
		} else if (this.getLovBean().getSelectionMode().equalsIgnoreCase(AuditConst.MULTIPLE)) {
			if (!persistedEntities.isEmpty())
				selectedEntities = new ArrayList<>(persistedEntities.values());
			debugLogger("this.getSelectedEntities().size(): " + this.getSelectedEntities().size());
			debugLogger("this.getSelectedEntities(): " + this.getSelectedEntities());

			if (selectedEntities != null && !selectedEntities.isEmpty()) {

				ValueExpression valueExpression = context.getApplication().getExpressionFactory()
						.createValueExpression(context.getELContext(), expressionString, List.class);

				valueExpression.setValue(context.getELContext(), selectedEntities);

				debugLogger("postUpdateListener" + this.getLovBean().getPostUpdateListener());

				if (StringUtils.isNotEmpty(this.getLovBean().getPostUpdateListener())) {

					MethodExpression postUpdateListenerMethodExpr = FacesScopes.createMethodExpression(context, this.getLovBean()
							.getPostUpdateListener(), null, new Class[] { List.class });

					postUpdateListenerMethodExpr.invoke(context.getELContext(), new Object[] { selectedEntities });
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
				String enableAutoUpdate = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
						.get("enableAutoUpdate");

				if (StringUtils.isNotBlank(enableAutoUpdate)) {
					if (!FacesScopes.isValueReference(autoUpdateValue)) {
						autoUpdateValue = "#{" + autoUpdateValue + "}";
					}

					FacesContext context = FacesContext.getCurrentInstance();
					ValueExpression autoUpdateVE = context.getApplication().getExpressionFactory()
							.createValueExpression(context.getELContext(), autoUpdateValue, Boolean.class);
					autoUpdateVE.setValue(context.getELContext(), Boolean.valueOf(enableAutoUpdate));
				}
			}
		}
	}

	public boolean isSelectButtonDisable() {
		if (getLovBean() != null && getLovBean().getSelectionMode() != null) {
			if (getLovBean().getSelectionMode().equalsIgnoreCase(AuditConst.SINGLE)) {
				if (getSelectedEntity() != null) {
					return false;
				}
			} else if (getSelectedEntities() != null && getSelectedEntities().size() > 0) {
				// #29832 & #9442 added if else to make sure the selected cb
				// save after tick.
				// Default save after click next page onPageChange listener
				if ("PM_BUSINESS_NATURE".equalsIgnoreCase(getLovBean().getLovId())) {
					for (BaseEntity item : selectedEntities) {
						if (item instanceof PmBusinessNature) {
							Long key = ((PmBusinessNature) item).getBusinessNatureId();
							if (!persistedEntities.containsKey(key))
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
		for (LovColumnBean column : lovBean.getColumns()) {
			ResourceBundle bundle = ResourceBundle.getBundle(column.getBundleFile(), new FacesScopes().getUserLocale());
			column.setColumnTitle(bundle.getString(column.getBundleKey()));
		}
	}

	/**
	 * @param event
	 */
	public void onUnselectRow(UnselectEvent event) {
		Object value = event.getObject();
		if (value instanceof PmBusinessNature) {
			Long key = ((PmBusinessNature) value).getBusinessNatureId();
			if (persistedEntities.containsKey(key))
				persistedEntities.remove(key);
		}
	}

	/**
	 * @param event
	 */
	public void onToggleSelect(ToggleSelectEvent event) {
		List<? extends BaseEntity> templist = !selectedEntities.isEmpty() ? selectedEntities : searchPmUserDataModel.getList();
		if (templist instanceof BaseEntity) {
			for (BaseEntity value : templist) {
				if (value instanceof PmBusinessNature) {
					Long key = ((PmBusinessNature) value).getBusinessNatureId();
					if (persistedEntities.containsKey(key))
						persistedEntities.remove(key);
					else
						persistedEntities.put(key, value);
				}
			}
		}
	}

	/**
	 * @param event
	 */
	public void onPageChange(PageEvent event) {
		if ("PM_BUSINESS_NATURE".equalsIgnoreCase(getLovBean().getLovId())) {
			log.info("getLovId().equalsIgnoreCase(PM_BUSINESS_NATURE)");
		} else {
			for (BaseEntity item : selectedEntities) {
				if (item instanceof PmBusinessNature) {
					Long key = ((PmBusinessNature) item).getBusinessNatureId();
					if (!persistedEntities.containsKey(key))
						persistedEntities.put(key, item);
				}
			}
		}

	}

	/**
	 * populateSelectedItems()
	 */
	public void populateSelectedItems() {
		selectedEntities = new ArrayList<>(persistedEntities.values());
	}

	private void enableDisplayResult() {
		getLovBean().setDisplayResultWhenFirstLoad(true);
	}

	private void disableDisplayResult() {
		getLovBean().setDisplayResultWhenFirstLoad(false);
	}

	public List<SelectItem> getSearchOptions() {
		return searchOptions;
	}

	public void setSearchOptions(List<SelectItem> searchOptions) {
		this.searchOptions = searchOptions;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ISearchBean getLovSearchBean() {
		if (lovSearchBean.getDataFilterBeans().size() == 0) {
			for (int i = 0; i < 1; i++) {
				SearchFilterBean filterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
				lovSearchBean.getDataFilterBeans().add(filterBean);
			}
		}
		return lovSearchBean;
	}

	@SuppressWarnings("rawtypes")
	public void setLovSearchBean(ISearchBean lovSearchBean) {
		this.lovSearchBean = lovSearchBean;
	}

	public LovBean getLovBean() {
		return lovBean;
	}

	public void setLovBean(LovBean lovBean) {
		this.lovBean = lovBean;
	}

	@SuppressWarnings("rawtypes")
	public List getResultList() {
		return resultList;
	}

	@SuppressWarnings("rawtypes")
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

	public Map<Long, BaseEntity> getPersistedEntities() {
		return persistedEntities;
	}

	public void setPersistedEntities(Map<Long, BaseEntity> persistedEntities) {
		this.persistedEntities = persistedEntities;
	}

	/**
	 * @author zaim #29832 start - to remove auto populate checkbox after delete
	 *         the list for business nature
	 * 
	 * **/
	public void removeSelectedBusinessNature() {
		if (getLovBean().getSimpleQuoteBackingBean() != null) {
			SimpleQuoteManagedBean simpleQuoteMB = getLovBean().getSimpleQuoteBackingBean().deleteSuccess();
			if (simpleQuoteMB.getDeleteBusinessNature()) {
				if (simpleQuoteMB.getDeletedBusinessNatureMap() != null) {
					if (simpleQuoteMB.getDeletedBusinessNatureMap().size() > 0) {
						for (Long item : simpleQuoteMB.getDeletedBusinessNatureMap().keySet()) {
							debugLogger("Delete key " + item);

							for (Iterator<Map.Entry<Long, BaseEntity>> it = persistedEntities.entrySet().iterator(); it.hasNext();) {
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

	public IPagingDataModel<PmUser> getSearchPmUserDataModel() {
		return searchPmUserDataModel;
	}

	public void setSearchPmUserDataModel(IPagingDataModel<PmUser> searchPmUserDataModel) {
		this.searchPmUserDataModel = searchPmUserDataModel;
	}
}