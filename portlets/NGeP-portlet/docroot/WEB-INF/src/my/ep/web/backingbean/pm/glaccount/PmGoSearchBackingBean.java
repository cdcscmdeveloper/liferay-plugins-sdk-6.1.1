package my.ep.web.backingbean.pm.glaccount;

import java.util.List;

import javax.el.MethodExpression;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "pmGoSearchBackingBean")
@ViewScoped
public class PmGoSearchBackingBean extends BaseBackingBean<PmGoSearchManagedBean> {

		private static Logger log = Logger.getLogger(PmGoSearchBackingBean.class);

		// Paging for listing
	private IPagingDataModel<VPmGo> pmGoDataModel = new PmGoPagingDataModel();

	
	public PmGoSearchBackingBean() {

	}

	public void initPmGoPopup(){
		this.renewSesionObject();
		getPmGoDataModel().resetData();	
		try{
			FacesScopes facesScopes = new FacesScopes();
			Integer index = Integer.parseInt(facesScopes.getParameter("index"));
			String type = facesScopes.getParameter("type");
			String update = facesScopes.getParameter("update");
			Long kptjId = Long.parseLong(facesScopes.getParameter("kptjId"));
			getManagedBean().setIndex(index);
			getManagedBean().setType(type);
			getManagedBean().setUpdate(update);
			getManagedBean().setKptjId(kptjId);
		}catch(Exception e){
			throw new EpException(e.getMessage());
		}	
	}
	
	public void resetAction(){
		getManagedBean().setLazy(false);
		getManagedBean().setDesc(null);
		getManagedBean().setSelectedAccClass(null);
		getManagedBean().setSelectedAccType(null);
		getManagedBean().setSelectedGenObjCode(null);
		getPmGoDataModel().resetData();
	}
	
	
	public List<PmParameterDesc> retrieveAccountType() {
		log.info("retrieveAccountType");
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(
				ParameterTypeConst.TYPE_OF_ACCOUNT_TYPE, null);
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
		
		if (StringUtils.isNotBlank(getManagedBean().getDesc())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("desc",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getDesc().toLowerCase(),true));
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

}
