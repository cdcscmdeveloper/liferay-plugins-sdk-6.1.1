package my.ep.web.backingbean.cm.codification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
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
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPendingCodifiedItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.circular.panel.PanelBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.cm.CmConst;
import my.ep.web.managedbean.cm.codification.CodificationMaintenanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.ExtensionCodeVO;
import my.ep.web.vo.NameNode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "codificationMaintenanceBackingBean")
@ViewScoped
public class CodificationMaintenanceBackingBean extends BaseBackingBean<CodificationMaintenanceManagedBean> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAV_TO_CODIFICATION_MAINTENANCE = "codificationMaintenance";
	public static final String NO_69 = "69";

	private static Logger log = Logger.getLogger(CodificationMaintenanceBackingBean.class);

	private SearchFilterBean codeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean levelFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();
	private IPagingDataModel<Object[]> extensionSearchItemDataModel = new ExtensionSearchItemDataModel();


	private SearchFilterBean colorFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean titleEngFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	
		

	private TreeNode rootNodes;
	private TreeNode selectedNode;
	private String itemTypeParameter;

	public CodificationMaintenanceBackingBean() {

		if (this.getManagedBean().isRefreshPageNoFlag()) {
			System.out.println("****************************");

			this.getManagedBean().initRefreshPageNoFlag();
		}
	}
	
		
		/**
		 * This method is used to reset the value of UNSPSC Search
		 * 
		 */

		public String resetAction() {
			log.info("In reset action...................");

			this.getManagedBean().reset();
			log.info("after reset.........................");
			return null;
		}
		
		
	
	/**
	 * Extension Search Data Model for displaying list of extension records.
	 * 
	 */

//	private class ExtensionSearchItemDataModel extends
//	PagingDataModel<Object[]> {
//
//		public ExtensionSearchItemDataModel() {
//			super("itemTableID");
//		}
//
//		@Override
//		protected ISearchResultBean<Object[]> pageChangeListener(
//				ISearchBean<Object[]> searchBean) {
//
//
//			if (StringUtils.isBlank((String) colorFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) typeFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) brandFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) measurementFilterBean.getSearchValue())) {
//				return new SearchResultBean();
//			}
//
//
//			System.out.println("........search extension combination 11.................");
//			log.info("Unspsc Code:"+ getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
//
//			ICodificationSO codificationSO = null;
//			CodificationResDTO resDTO = null;
//			try {
//				codificationSO = (ICodificationSO) BusinessDelegate
//						.getInstance().getServiceObject("cm",
//								ICodificationSO.class);
//
//				CodificationReqDTO reqDTO = new CodificationReqDTO();
//				reqDTO.setExtSearchBean(searchBean);
//
//				reqDTO.setUnspscCode(getManagedBean().getSelectedUNSPSCObj()
//						.getUnspscCode());
//				
//				System.out.println("colorFilterBean.getSearchValue():"+colorFilterBean.getSearchValue());
//				if (StringUtils.isNotBlank((String) colorFilterBean
//						.getSearchValue())) {
//					reqDTO.setColorId(Long.valueOf(colorFilterBean
//							.getSearchValue().toString()));
//				}
//
//				System.out.println("typeFilterBean.getSearchValue()"+ typeFilterBean.getSearchValue());
//				if (StringUtils.isNotBlank((String) typeFilterBean
//						.getSearchValue())) {
//					reqDTO.setTypeId(Long.valueOf(typeFilterBean
//							.getSearchValue().toString()));
//
//				}
//
//				if (StringUtils.isNotBlank((String) brandFilterBean
//						.getSearchValue())) {
//					reqDTO.setBrandId(Long.valueOf(brandFilterBean
//							.getSearchValue().toString()));
//
//				}
//
//				if (StringUtils.isNotBlank((String) measurementFilterBean
//						.getSearchValue())) {
//					reqDTO.setMeasurementId(Long.valueOf(measurementFilterBean
//							.getSearchValue().toString()));
//				}
//				System.out.println("*********in********");
//				resDTO = codificationSO.searchExtension(reqDTO);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				codificationSO = null;
//			}
//			if (resDTO != null) {
//				return resDTO.getExtensionResultBean();
//			}
//			return null;
//		}
//
//	}
//


	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(ISearchBean<CmUnspsc> searchBean) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>titleFilterBean.getSearchValue()="
					+ titleFilterBean.getSearchValue());
			if (getManagedBean().getAutoCompleteUnspscCode().getUnspscCode() != null
					&& getManagedBean().getAutoCompleteUnspscCode().getUnspscCode().trim().length() > 0) {
				codeFilterBean.setSearchValue(getManagedBean().getAutoCompleteUnspscCode().getUnspscCode());
			}

			if (StringUtils.isBlank((String) codeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) titleFilterBean.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {

				unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
						ICodificationSO.class);

				if (getManagedBean().getAutoCompleteUnspscCode().getUnspscCode() != null
						&& getManagedBean().getAutoCompleteUnspscCode().getUnspscCode().trim().length() > 0) {
					log.info("*********codeFilterBean***********");
					codeFilterBean.setSearchField("unspscCode");
					codeFilterBean.setSearchValue(getManagedBean().getAutoCompleteUnspscCode().getUnspscCode());
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String) titleFilterBean.getSearchValue())) {
					log.info("*********titleFilterBean***********");
					titleFilterBean.setSearchField("unspscTitle");
					titleFilterBean.setCaseInsensitive(true);
					titleFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());

					titleEngFilterBean.setSearchField("unspscTitleEng");
					titleEngFilterBean.setCaseInsensitive(true);
					titleEngFilterBean.setSearchValue(titleFilterBean.getSearchValue());
					titleEngFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());

					SearchMultiFilterBean bilingualFilter = new SearchMultiFilterBean(titleFilterBean,
							titleEngFilterBean);

					searchBean.getDataFilterBeans().add(bilingualFilter);
				}
				FacesScopes fs = new FacesScopes();
				String itemType = fs.getParameter("itemType");

				log.info("............itemType=" + itemType);
				if (itemType != null) {// save parameter first time it come from
					// search button
					setItemTypeParameter(itemType);
				}
				log.info("............getItemTypeParameter=" + getItemTypeParameter());
				log.info("............ isAssignCodeFlg =" + getManagedBean().isAssignCodeFlg());
				// get parameter from backup property whenever table be reloaded
				if (!getManagedBean().isAssignCodeFlg()) {
					if (getItemTypeParameter() != null && !getItemTypeParameter().isEmpty()) {

						if (getItemTypeParameter().equals("S") || getItemTypeParameter().equals("2")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("unspscCode", SearchOperator.OPERATOR_GREATER_EQUAL.value(),
											"70000000"));
						} else if (getItemTypeParameter().equals("P") || getItemTypeParameter().equals("1")) {
							searchBean.getDataFilterBeans()
									.add(new SearchFilterBean("unspscCode", SearchOperator.OPERATOR_LESS.value(),
											"70000000"));
						}
					}
				}

				/*
				 * levelFilterBean.setSearchField("unspscLevel");
				 * levelFilterBean.setSearchValue(3);
				 * searchBean.getDataFilterBeans().add(levelFilterBean);
				 */

				System.out.println("this.getSortField():" + this.getSortField());
				if (this.getSortField() == null) {
					SearchOrderBean orderBean = new SearchOrderBean("unspscCode", true);

					searchBean.getSortColumns().add(orderBean);
				}

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				log.info("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				log.info("*********after***********");
				return resDTO.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				unspscSO = null;
			}
			return new SearchResultBean();
		}

	}

	public SearchFilterBean getCodeFilterBean() {
		return codeFilterBean;
	}

	public void setCodeFilterBean(SearchFilterBean codeFilterBean) {
		this.codeFilterBean = codeFilterBean;
	}

	public SearchFilterBean getTitleFilterBean() {
		return titleFilterBean;
	}

	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
		this.titleFilterBean = titleFilterBean;
	}

	public SearchFilterBean getLevelFilterBean() {
		return levelFilterBean;
	}

	public void setLevelFilterBean(SearchFilterBean levelFilterBean) {
		this.levelFilterBean = levelFilterBean;
	}

	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	/**
	 * UNSPSC Search Data Model for displaying list of UNSPSC records.
	 * 
	 */

	public void unspscSearch() {
		String unspscCode = getManagedBean().getAutoCompleteUnspscCode().getUnspscCode();
		String unspscTitle = this.getManagedBean().getTitleFilterBean().getSearchValue().toString();

		if ((unspscCode == null && (unspscTitle == null || unspscTitle.isEmpty()))
				|| ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		if ((unspscCode != null && !unspscCode.isEmpty())
				&& (unspscCode.length() != 8 || !StringUtils.isNumeric(unspscCode))) {
			throw new EpException(MessageConst.ERROR_715026);
		}

		if (unspscCode == null && unspscTitle.trim().length() < 3) {
			throw new EpException(MessageConst.ERROR_715060);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}
	
	public void extensionCodeSearch() {
		//this.getManagedBean().setExtensionSearchcheckBoxAll(false);
		//this.getManagedBean().setCloneExtensionVOs(null);
		this.getManagedBean().getExtensionSearchItemDataModel().reloadData();
	}

	private int compareFirstChoice(String code1, String code2, int last) {
		if ((code1.equals("000") || code1.equals("00")) && (code2.equals("000") || code2.equals("00"))) {
			return 0;
		} else if ((code1.equals("000") || code1.equals("00"))) {
			return -1;
		} else if ((code2.equals("000") || code2.equals("00"))) {
			return 1;
		}
		return last;
	};



	/**
	 * filter family code by segment code
	 * 
	 */

	public void filterFamily() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		log.info("this.getManagedBean().getSegmentCode()" + this.getManagedBean().getSegmentCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getSegmentCode());

		CodificationResDTO resDTO = codificationSO.searchFamily(reqDTO);

		for (CmUnspsc family : resDTO.getFamilyList()) {

			this.getManagedBean().getFamilyCriteria()
					.add(new SelectItem(family.getUnspscCode(), family.getUnspscTitle()));
		}

	}

	/**
	 * filter class code by family code
	 * 
	 */
	public void filterClass() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		log.info("this.getManagedBean().getFamilyCode()" + this.getManagedBean().getFamilyCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getFamilyCode());

		CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

		for (CmUnspsc classes : resDTO.getClassList()) {

			this.getManagedBean().getClassCriteria()
					.add(new SelectItem(classes.getUnspscCode(), classes.getUnspscTitle()));
		}

	}

	/**
	 * filter commodity code by class code
	 * 
	 */

	public void filterCommodity() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		log.info("this.getManagedBean().getClassCode()" + this.getManagedBean().getClassCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getClassCode());

		CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

		for (CmUnspsc commodity : resDTO.getCommodityList()) {

			this.getManagedBean().getCommodityCriteria()
					.add(new SelectItem(commodity.getUnspscCode(), commodity.getUnspscTitle()));
		}

	}

	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}

	public String backToPrevPortal() {
		this.getManagedBean().initRefreshMainPageNoFlag();
		return "codificationMaintenance";
	}
	

	public String getParentCode(long code) {

		String parent = null;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			parent = (String) String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:" + parent);
		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				parent = (String) String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:" + parent);
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					parent = (String) String.valueOf(code).substring(0, 2) + "000000";
					log.debug("parent:" + parent);
				} else {
					log.debug("segment");
				}

			}

		}

		return parent;
	}

	public Integer checkUnspscLevel(long code) {

		Integer level = 0;

		String check68 = (String) String.valueOf(code).substring(6, 8);
		String check46 = (String) String.valueOf(code).substring(4, 6);
		String check24 = (String) String.valueOf(code).substring(2, 4);

		if (!check68.equalsIgnoreCase("00")) {
			log.debug("commodity");
			level = 3;

		} else {
			if (!check46.equalsIgnoreCase("00")) {
				log.debug("class");
				level = 2;
			} else {
				if (!check24.equalsIgnoreCase("00")) {
					log.debug("family");
					level = 1;
				} else {
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}

	public void onNodeSelect(NodeSelectEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();

		NameNode tr = (NameNode) tree.getData();
		log.debug("sequence:" + tr.getSeq());

	}

	public void onNodeExpand(NodeExpandEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();
		NameNode tr = (NameNode) tree.getData();

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		log.info("tr.getUnspscLevel()" + tr.getUnspscLevel());
		if (tr.getUnspscLevel() == 1) {
			log.info("1111111111111111");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

			for (CmUnspsc catClass : resDTO.getClassList()) {

				ClassesVO classesVO = new ClassesVO();
				classesVO.setName(catClass.getUnspscTitle());
				classesVO.setParentCode(catClass.getUnspscCode());
				classesVO.setUnspscLevel(2);

				TreeNode families = new DefaultTreeNode("class", classesVO, tree);

			}

		}

		if (tr.getUnspscLevel() == 2) {
			log.info("2222222222222222222");
			reqDTO.setParentCode(tr.getParentCode());
			CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

			for (CmUnspsc catCommodity : resDTO.getCommodityList()) {

				CommoditiesVO commoditiesVO = new CommoditiesVO();
				commoditiesVO.setName(catCommodity.getUnspscTitle());
				commoditiesVO.setSeq(catCommodity.getUnspscId());
				commoditiesVO.setParentCode(catCommodity.getUnspscCode());
				commoditiesVO.setUnspscLevel(3);

				TreeNode families = new DefaultTreeNode("commodities", commoditiesVO, tree);

			}

		}

	}

	public TreeNode getRootNodes() {
		return rootNodes;
	}

	public void setRootNodes(TreeNode rootNodes) {
		this.rootNodes = rootNodes;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public List<String> autoCompleteUnspsc(String query) {

		log.info("query:::::::::::::::::::::::::::::: " + query);

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);

		List<String> unspscCodes = new ArrayList<String>();

		try {
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(query);

			CodificationResDTO resDTO = codificationSO.searchUnspscCodeByAuto(reqDTO);

			List<Integer> filters = new ArrayList<Integer>();
			filters.add(69);
			filters.add(68);
			filters.add(99);
			if (resDTO.getUnspscList() != null)
				for (CmUnspsc unspsc : resDTO.getUnspscList()) {
					if (!filters.contains(Integer.parseInt(unspsc.getUnspscCode().substring(0, 2)))) {
						unspscCodes.add(unspsc.getUnspscCode());
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			codificationSO = null;
		}

		return unspscCodes;
	}

	public boolean productCode(String unspscCode) {

		boolean flag = true;
		String segmentCode = "";

		if (unspscCode != null && unspscCode.trim().length() > 0) {
			segmentCode = unspscCode.substring(0, 2);
		}

		if (segmentCode.compareTo(NO_69) > 0) {
			flag = false;
		}

		return flag;
	}


	public String getItemTypeParameter() {
		return itemTypeParameter;
	}

	public void setItemTypeParameter(String itemTypeParameter) {
		log.info(".................setItemTypeParameter................");
		this.itemTypeParameter = itemTypeParameter;
	}



	// 20190821- Start
	public void resetSearch() {
		this.getManagedBean().setAutoCompleteUnspscCode(null);
		this.getManagedBean().setTitleFilterBean(null);
		resetAction();
		// 20190821- End
	}

	// 2019/08/27 - Start
	public String PopupOption(CmUnspsc obj) {

		this.getManagedBean().setCommodityCode("");
		this.getManagedBean().setCommodityTitle("");
		this.getManagedBean().setCommodityTitleEng("");

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
				
			reqDTO.setUnspscCode(obj.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);

			
			if (resDTO.isSuccessful()) {
				CmUnspsc commodity = resDTO.getCommodityObj();

				
				this.getManagedBean().setSelectedUNSPSCObj(commodity);

				this.getManagedBean().setClassCode(
						commodity.getCmUnspsc().getUnspscCode());
				this.getManagedBean().setClassTitle(
						commodity.getCmUnspsc().getUnspscTitle());
				this.getManagedBean().setClassTitleEng(
						commodity.getCmUnspsc().getUnspscTitleEng());

				this.getManagedBean().setFamilyCode(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
				this.getManagedBean().setFamilyTitle(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
				this.getManagedBean().setFamilyTitleEng(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());


				this.getManagedBean().setSegmentCode(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscCode());
				this.getManagedBean().setSegmentTitle(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscTitle());
				this.getManagedBean().setSegmentTitleEng(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscTitleEng());

			}

		return "optionItemCodeMaintenance.xhtml?faces-redirect=true";
	}

	
	public String PopupExtensionCode(CmUnspsc unspscObj) {
		log.debug("**********goToPopupExtensionCode************");
		

		this.getManagedBean().setCommodityCode("");
		this.getManagedBean().setCommodityTitle("");
		this.getManagedBean().setCommodityTitleEng("");

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
			
//		Integer level = checkUnspscLevel(new Long(unspscObj.getUnspscCode()));
//
//		log.info("level:" + level);
//
//		if (level == 0) {
//
//			log.info("***********0000****************");
//			reqDTO.setParentCode(unspscObj.getUnspscCode());
//			CodificationResDTO familyDTO = unspscSO.searchFamily(reqDTO);
//
//			this.getManagedBean().setUnspscPopUpList(familyDTO.getFamilyList());
//
//			this.getManagedBean().setSegmentCode(unspscObj.getUnspscCode());
//			this.getManagedBean().setSegmentTitle(unspscObj.getUnspscTitle());
//			this.getManagedBean().setSegmentTitleEng(unspscObj.getUnspscTitleEng());
//
//		} else if (level == 1) {
//			log.info("***********11111****************");
//			reqDTO.setParentCode(unspscObj.getUnspscCode());
//			CodificationResDTO ClassDTO = unspscSO.searchClass(reqDTO);
//
//			this.getManagedBean().setUnspscPopUpList(ClassDTO.getClassList());
//
//			this.getManagedBean().setFamilyCode(unspscObj.getUnspscCode());
//			this.getManagedBean().setFamilyTitle(unspscObj.getUnspscTitle());
//			this.getManagedBean().setFamilyTitleEng(unspscObj.getUnspscTitleEng());
//
//			String segmentCode = getParentCode(new Long(unspscObj.getUnspscCode()));
//
//			reqDTO.setUnspscCode(segmentCode);
//			CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);
//
//			this.getManagedBean().setSegmentCode(segmentDTO.getUnspscObj().getUnspscCode());
//			this.getManagedBean().setSegmentTitle(segmentDTO.getUnspscObj().getUnspscTitle());
//			this.getManagedBean().setSegmentTitleEng(segmentDTO.getUnspscObj().getUnspscTitleEng());
//
//		} else if (level == 2) {
//			log.info("***********222****************" + unspscObj.getUnspscCode());
//			reqDTO.setParentCode(unspscObj.getUnspscCode());
//			CodificationResDTO CommodityDTO = unspscSO.searchCommodity(reqDTO);
//
//			this.getManagedBean().setUnspscPopUpList(CommodityDTO.getCommodityList());
//
//			this.getManagedBean().setClassCode(unspscObj.getUnspscCode());
//			this.getManagedBean().setClassTitle(unspscObj.getUnspscTitle());
//			this.getManagedBean().setClassTitleEng(unspscObj.getUnspscTitleEng());
//
//			String familyCode = getParentCode(new Long(unspscObj.getUnspscCode()));
//			log.info("familyCode:" + familyCode);
//			reqDTO.setUnspscCode(familyCode);
//			CodificationResDTO familyDTO = unspscSO.searchUnspscObj(reqDTO);
//
//			this.getManagedBean().setFamilyCode(familyDTO.getUnspscObj().getUnspscCode());
//			this.getManagedBean().setFamilyTitle(familyDTO.getUnspscObj().getUnspscTitleEng());
//			this.getManagedBean().setFamilyTitleEng(familyDTO.getUnspscObj().getUnspscTitleEng());
//
//			String segmentCode = getParentCode(new Long(familyDTO.getUnspscObj().getUnspscCode()));
//			log.info("segmentCode:" + segmentCode);
//			reqDTO.setUnspscCode(segmentCode);
//			CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);
//
//			this.getManagedBean().setSegmentCode(segmentDTO.getUnspscObj().getUnspscCode());
//			this.getManagedBean().setSegmentTitle(segmentDTO.getUnspscObj().getUnspscTitle());
//			this.getManagedBean().setSegmentTitleEng(segmentDTO.getUnspscObj().getUnspscTitleEng());
//
//		}else if (level == 3) {
			reqDTO.setUnspscCode(unspscObj.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);

			
			if (resDTO.isSuccessful()) {
				CmUnspsc commodity = resDTO.getCommodityObj();

				
				this.getManagedBean().setSelectedUNSPSCObj(commodity);

				this.getManagedBean().setClassCode(
						commodity.getCmUnspsc().getUnspscCode());
				this.getManagedBean().setClassTitle(
						commodity.getCmUnspsc().getUnspscTitle());
				this.getManagedBean().setClassTitleEng(
						commodity.getCmUnspsc().getUnspscTitleEng());

				this.getManagedBean().setFamilyCode(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
				this.getManagedBean().setFamilyTitle(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
				this.getManagedBean().setFamilyTitleEng(
						commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());


				this.getManagedBean().setSegmentCode(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscCode());
				this.getManagedBean().setSegmentTitle(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscTitle());
				this.getManagedBean().setSegmentTitleEng(
						commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
						.getUnspscTitleEng());

			}
		Collections.sort(resDTO.getCommodityObj().getCmBrand(), new Comparator<CmBrand>() {
		    @Override
		    public int compare(CmBrand t1, CmBrand t2) {
		        return compareFirstChoice(t1.getBrandCode(), t2.getBrandCode(), t1.getBrandName().trim().compareToIgnoreCase(t2.getBrandName().trim()));
		    }
		});
		Collections.sort(resDTO.getCommodityObj().getCmType(), new Comparator<CmType>() {
		    @Override
		    public int compare(CmType t1, CmType t2) {
		        return compareFirstChoice(t1.getTypeCode(), t2.getTypeCode(), t1.getTypeName().trim().compareToIgnoreCase(t2.getTypeName().trim()));
		    }
		});
		Collections.sort(resDTO.getCommodityObj().getCmMeasurement(), new Comparator<CmMeasurement>() {
		    @Override
		    public int compare(CmMeasurement t1, CmMeasurement t2) {
		        return compareFirstChoice(t1.getMeasurementCode(), t2.getMeasurementCode(), t1.getMeasurementName().trim().compareToIgnoreCase(t2.getMeasurementName().trim()));
		    }
		});
		
		for (CmBrand brand : resDTO.getCommodityObj().getCmBrand()) {
			if (!StringUtils.isBlank(brand.getBrandName()) && brand.getRecordStatusFlg() ==1) {
				this.getManagedBean()
				.getBrandCriteria()
				.add(new SelectItem(brand.getBrandId(), brand
						.getBrandName()));
			}
		}

		for (CmType type : resDTO.getCommodityObj().getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName()) && type.getRecordStatusFlg() ==1) {
				this.getManagedBean()
				.getTypeCriteria()
				.add(new SelectItem(type.getTypeId(), type
						.getTypeName()));
			}
		}

		for (CmMeasurement measurement : resDTO.getCommodityObj()
				.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName()) && measurement.getRecordStatusFlg()==1) {
				this.getManagedBean()
				.getMeasurementCriteria()
				.add(new SelectItem(measurement.getMeasurementId(),
						measurement.getMeasurementName()));
			}
		}

		resDTO = unspscSO.searchColor(reqDTO);
		Collections.sort(resDTO.getColorList(), new Comparator<CmColor>() {
		    @Override
		    public int compare(CmColor t1, CmColor t2) {
		        return compareFirstChoice(t1.getColorCode(), t2.getColorCode(), t1.getColorName().trim().compareToIgnoreCase(t2.getColorName().trim()));
		    }
		});
		for (CmColor color : resDTO.getColorList()) {
			if (!StringUtils.isBlank(color.getColorName()) && color.getRecordStatusFlg()==1) {
				this.getManagedBean()
				.getColorCriteria()
				.add(new SelectItem(color.getColorId(), color
						.getColorName()));
		}
			
		this.getManagedBean().getExtensionSearchItemDataModel().reloadData();
		this.getManagedBean().getExtensionSearchItemDataModel().resetData();
		this.getManagedBean().getColorFilterBean().setSearchValue("");
		this.getManagedBean().getTypeFilterBean().setSearchValue("");
		this.getManagedBean().getMeasurementFilterBean().setSearchValue("");
		this.getManagedBean().getBrandFilterBean().setSearchValue("");
		
		}
		return "optionExtensionCodeMaintenance.xhtml?faces-redirect=true";
	}

		
	// * End

	// azza//

	public void SubmitUpdateData() {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		CodificationResDTO resDTO = null;

		UserReqDTO userReqDTO = new UserReqDTO();
		
			log.info("**Codification Officer***");
			userReqDTO.setRoleCode(RoleCode.CD_OFFICER.value());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
			if(userResDTO.getUserList()!=null && userResDTO.getUserList().size()>0){
				for(PmUser user :userResDTO.getUserList()){
					log.info("user.getLoginId():"+user.getLoginId());
					reqDTO.getCdcList().add(user.getLoginId());
				}
			}else{
				throw new EpException("User no found!");
			}
	
		// Segment..
			
			
			
		if (this.getManagedBean().isEditMode()==false){		
		if (this.getManagedBean().getSegmentTitle().isEmpty() || this.getManagedBean().getSegmentTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return;

		} else {
			
			log.info("***update***");
			
			reqDTO.setUnspscCode(this.getManagedBean()
					.getSegmentCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			
			if (resDTO.getUnspscObj() != null) {
				
				resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean().
						getSegmentTitle());
				resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
						.getSegmentTitleEng());
				resDTO.getUnspscObj().getUnspscId();
				
				
				log.info("unspsc id for segment " + resDTO.getUnspscObj().getUnspscId());
						
				reqDTO.getUnspscList().add(resDTO.getUnspscObj());
						
				resDTO = unspscSO.updateUnspsc(reqDTO);
				
				
			}
		  }
		}
		// Family..
	    if (this.getManagedBean().isEditModeFamily()== false){
		if (this.getManagedBean().getFamilyTitle().isEmpty() || this.getManagedBean().getFamilyTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return;

		} else {
			
			log.info("***update***");
			
			reqDTO.setUnspscCode(this.getManagedBean()
					.getFamilyCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			
			if (resDTO.getUnspscObj() != null) {
	
			resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
					.getFamilyTitle());
			resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
					.getFamilyTitleEng());
			resDTO.getUnspscObj().getUnspscId();
			
			log.info("unspsc id for famliy " + resDTO.getUnspscObj().getUnspscId());
			
			
			reqDTO.getFamilyList().add(resDTO.getUnspscObj());
					
			resDTO = unspscSO.updateFamilyList(reqDTO);
			
			
			}
		}
	 }

		// Class
		if (this.getManagedBean().isEditModeClass()==false){	
		if (this.getManagedBean().getClassTitle().isEmpty() || this.getManagedBean().getClassTitleEng().isEmpty()) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return;

		} else {

	        log.info("***update***");
			
			reqDTO.setUnspscCode(this.getManagedBean()
					.getClassCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			
			if (resDTO.getUnspscObj() != null) {
	
			resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean()
					.getClassTitle());
			resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean()
					.getClassTitleEng());
			resDTO.getUnspscObj().getUnspscId();
			
			log.info("unspsc id for class " + resDTO.getUnspscObj().getUnspscId());
			
			
			reqDTO.getClassList().add(resDTO.getUnspscObj());
						
			resDTO = unspscSO.updateClassList(reqDTO);

			}
		  }
		}
			
		// Commodity..				
		if (this.getManagedBean().getSelectedUNSPSCObj().getUnspscTitle().isEmpty()||
				this.getManagedBean().getSelectedUNSPSCObj().getUnspscTitleEng().isEmpty()){

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_715050, MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale())));
			return;

		} else {
			
			log.info("***update***");
			
			
			reqDTO.setUnspscCode(this.getManagedBean().getSelectedUNSPSCObj()
					.getUnspscCode());
			resDTO = unspscSO.searchUnspscObj(reqDTO);
			
			if (resDTO.getUnspscObj() != null) {
		
			resDTO.getUnspscObj().setUnspscTitle(this.getManagedBean().getSelectedUNSPSCObj()
					.getUnspscTitle());
			resDTO.getUnspscObj().setUnspscTitleEng(this.getManagedBean().getSelectedUNSPSCObj()
					.getUnspscTitleEng());
			resDTO.getUnspscObj().getUnspscId();
			
			
			log.info("unspsc id for commodity " + resDTO.getUnspscObj().getUnspscId());
			
			reqDTO.getCommodityList().add(resDTO.getUnspscObj());
					
			resDTO = unspscSO.updateCommodityList(reqDTO);
			
			
		    }
		}
		
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_710023, MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));
		
		this.getManagedBean().setEditMode(true);
		this.getManagedBean().setEditModeFamily(true);
		this.getManagedBean().setEditModeClass(true);

				
		return;
		
	 }	



	public void editSegment() {
		log.info("editSegment...");
		this.getManagedBean().setEditMode(false);

	}
	
	
	public void editFamily() {
		log.info("editFamily...");
		this.getManagedBean().setEditModeFamily(false);

	}
	
	public void editClass() {
		log.info("editClass...");
		this.getManagedBean().setEditModeClass(false);

	}
	
	
	
	private class ExtensionSearchItemDataModel extends
	PagingDataModel<Object[]> {

		public ExtensionSearchItemDataModel() {
			super("unspscTableID");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {


//			if (StringUtils.isBlank((String) colorFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) typeFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) brandFilterBean.getSearchValue())
//					&& StringUtils.isBlank((String) measurementFilterBean.getSearchValue())) {
//				return new SearchResultBean();
//			}


			System.out.println("........search extension combination 11.................");
			log.info("Unspsc Code:"+ getManagedBean().getSelectedUNSPSCObj().getUnspscCode());

			ICodificationSO codificationSO = null;
			CodificationResDTO resDTO = null;
			try {
				

			
				
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm",
								ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setExtSearchBean(searchBean);

				reqDTO.setUnspscCode(getManagedBean().getSelectedUNSPSCObj()
						.getUnspscCode());
				
			
				
				
//				System.out.println("colorFilterBean.getSearchValue():"+colorFilterBean.getSearchValue());
//				if (StringUtils.isNotBlank((String) colorFilterBean
//						.getSearchValue())) {
//					reqDTO.setColorId(Long.valueOf(colorFilterBean
//							.getSearchValue().toString()));
//				}
//
//				System.out.println("typeFilterBean.getSearchValue()"+ typeFilterBean.getSearchValue());
//				if (StringUtils.isNotBlank((String) typeFilterBean
//						.getSearchValue())) {
//					reqDTO.setTypeId(Long.valueOf(typeFilterBean
//							.getSearchValue().toString()));
//
//				}
//
//				if (StringUtils.isNotBlank((String) brandFilterBean
//						.getSearchValue())) {
//					reqDTO.setBrandId(Long.valueOf(brandFilterBean
//							.getSearchValue().toString()));
//
//				}
//
//				if (StringUtils.isNotBlank((String) measurementFilterBean
//						.getSearchValue())) {
//					reqDTO.setMeasurementId(Long.valueOf(measurementFilterBean
//							.getSearchValue().toString()));
//				}
				System.out.println("*********in********");
				resDTO = codificationSO.SearchExtensionType(reqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				codificationSO = null;
			}
			if (resDTO != null) {
				return resDTO.getResultBeanType();
			}
			return null;
		}

	}
	
	public String data = "";

	   public String getData() {
	      return data;
	   }

	   public void setData(String data) {
	      this.data = data;
	   }
	   
		public String status = "0";

		   public String getstatus() {
		      return status;
		   }

		   public void setStatus(String status) {
		      this.status = status;
		   }
	   
	   
	   /**
		 * reset action after change search plan type
		 * 
		 * @param paramCode
		 */
		public void changeSearchPlanType(String data){
			if(data != null){
				log.info("DataType" + data);
				this.getManagedBean().getExtensionSearchItemDataModel().reloadData();
			}
		}
		
		
		public String unspscSearchPortal(CmUnspsc unspscObj) {
			
			this.getManagedBean().setAutoCompleteUnspscCode(unspscObj);	
	
			this.getManagedBean().getSearchDataModel().reloadData();
			
			System.out.println("NAV_TO_CODIFICATION_MAINTENANCE");
			return NAV_TO_CODIFICATION_MAINTENANCE + "?faces-redirect=true";
				
		}
		
		
		public void unspscPopUpSearch(CmUnspsc obj) {

			this.getManagedBean().setAutoCompleteUnspscCode(null);
			this.getManagedBean().setTitleFilterBean(null);
			resetAction();	

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICodificationSO.class);
			CodificationReqDTO reqDTO = new CodificationReqDTO();

			Integer level = checkUnspscLevel(new Long(obj.getUnspscCode()));

			log.info("level:" + level);

			if (level == 0) {

				log.info("***********0000****************");
				reqDTO.setParentCode(obj.getUnspscCode());
				CodificationResDTO familyDTO = unspscSO.searchFamily(reqDTO);

				this.getManagedBean().setUnspscPopUpList(familyDTO.getFamilyList());

				this.getManagedBean().setSegmentCode(obj.getUnspscCode());
				this.getManagedBean().setSegmentTitle(obj.getUnspscTitle());

			} else if (level == 1) {
				log.info("***********11111****************");
				reqDTO.setParentCode(obj.getUnspscCode());
				CodificationResDTO ClassDTO = unspscSO.searchClass(reqDTO);

				this.getManagedBean().setUnspscPopUpList(ClassDTO.getClassList());

				this.getManagedBean().setFamilyCode(obj.getUnspscCode());
				this.getManagedBean().setFamilyTitle(obj.getUnspscTitle());

				String segmentCode = getParentCode(new Long(obj.getUnspscCode()));

				reqDTO.setUnspscCode(segmentCode);
				CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);

				this.getManagedBean().setSegmentCode(
						segmentDTO.getUnspscObj().getUnspscCode());
				this.getManagedBean().setSegmentTitle(
						segmentDTO.getUnspscObj().getUnspscTitle());

			} else if (level == 2) {
				log.info("***********222****************"
						+ obj.getUnspscCode());
				reqDTO.setParentCode(obj.getUnspscCode());
				CodificationResDTO CommodityDTO = unspscSO.searchCommodity(reqDTO);

				this.getManagedBean().setUnspscPopUpList(
						CommodityDTO.getCommodityList());

				this.getManagedBean().setClassCode(obj.getUnspscCode());
				this.getManagedBean().setClassTitle(obj.getUnspscTitle());

				String familyCode = getParentCode(new Long(obj.getUnspscCode()));
				log.info("familyCode:" + familyCode);
				reqDTO.setUnspscCode(familyCode);
				CodificationResDTO familyDTO = unspscSO.searchUnspscObj(reqDTO);

				this.getManagedBean().setFamilyCode(
						familyDTO.getUnspscObj().getUnspscCode());
				this.getManagedBean().setFamilyTitle(
						familyDTO.getUnspscObj().getUnspscTitle());

				String segmentCode = getParentCode(new Long(familyDTO
						.getUnspscObj().getUnspscCode()));
				log.info("segmentCode:" + segmentCode);
				reqDTO.setUnspscCode(segmentCode);
				CodificationResDTO segmentDTO = unspscSO.searchUnspscObj(reqDTO);

				this.getManagedBean().setSegmentCode(
						segmentDTO.getUnspscObj().getUnspscCode());
				this.getManagedBean().setSegmentTitle(
						segmentDTO.getUnspscObj().getUnspscTitle());
				
			
			}
		}
		

}



// end
