package my.ep.web.backingbean.cm.codification;

import java.io.Serializable;
import java.util.ArrayList;
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
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.cm.codification.CodificationMaintenanceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.NameNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

//35503 Start
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.cm.AuditCMConst;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBean;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBeanTemplate;
//35503 End

@ManagedBean(name = "codificationMaintenanceBackingBean")
@ViewScoped
public class CodificationMaintenanceBackingBean extends BaseBackingBean<CodificationMaintenanceManagedBean> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAV_TO_CODIFICATION_MAINTENANCE = "codificationMaintenance";
	public static final String NO_69 = "69";

	private static Logger log = Logger.getLogger(CodificationMaintenanceBackingBean.class);

	private SearchFilterBean codeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleEngFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());	
	private SearchFilterBean levelFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean nameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean statusExtensionFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	
	
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();
	private IPagingDataModel<Object[]> extensionAttributeSearchData = new ExtensionAttributeSearchData();
	
	public IPagingDataModel<Object[]> getExtensionAttributeSearchData() {
		return extensionAttributeSearchData;
	}

	public void setExtensionAttributeSearchData(
			IPagingDataModel<Object[]> extensionAttributeSearchData) {
		this.extensionAttributeSearchData = extensionAttributeSearchData;
	}
	

	private TreeNode rootNodes;
	private TreeNode selectedNode;
	private String itemTypeParameter;
	private String EXTENSION_TYPE = "t";
	private String EXTENSION_BRAND = "b";
	private String EXTENSION_MEASUREMENT = "m";
	private String EXTENSION_COLOR = "c";
	private String EXTENSION_NULL = null;
	private String RECORD_STATUS_NULL = null;
	
	

	public CodificationMaintenanceBackingBean() { 
	}
		/**
		 * This method is used to reset the value of UNSPSC Search
		 * 
		 */

		public String resetAction() {
			log.info("In reset action...................");

			
			log.info("after reset.........................");
			return null;
		}
		
		public String resetActionextension() {
			log.info("In reset action...................");

			
			log.info("after reset.........................");
			return null;
		}


		private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

			private static final long serialVersionUID = 1L;

			public SearchPagingDataModel() {
				super("unspscObjectTableID");
				// TODO Auto-generated constructor stub
			}

			@Override
			protected ISearchResultBean<CmUnspsc> pageChangeListener(
					ISearchBean<CmUnspsc> searchBean) {

				if (getManagedBean().getAutoCompleteUnspscCode().getUnspscCode() != null
						&& getManagedBean().getAutoCompleteUnspscCode()
						.getUnspscCode().trim().length() > 0) {
					codeFilterBean.setSearchValue(getManagedBean().getAutoCompleteUnspscCode().getUnspscCode());
				}
				
				if (StringUtils.isBlank((String) codeFilterBean.getSearchValue())
						&& StringUtils.isBlank((String) getManagedBean().getTitleFilterBean()
								.getSearchValue())) {
					return new SearchResultBean();
				}

				ICodificationSO unspscSO = null;
				try {

					unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
							.getServiceObject("cm", ICodificationSO.class);

					String unspscCode = "";
					if (getManagedBean().getAutoCompleteUnspscCode()
							.getUnspscCode() != null
							&& getManagedBean().getAutoCompleteUnspscCode()
							.getUnspscCode().trim().length() > 0) {
						
						if(getManagedBean().getAutoCompleteUnspscCode().getUnspscCode().equalsIgnoreCase("00000000")){
							unspscCode = "notValid";
						}else{
							unspscCode = getManagedBean().getAutoCompleteUnspscCode().getUnspscCode();
						}

						codeFilterBean.setSearchField("unspscCode");
						codeFilterBean.setSearchValue(unspscCode);
						searchBean.getDataFilterBeans().add(codeFilterBean);
					}

					if (StringUtils.isNotBlank((String) getManagedBean().getTitleFilterBean()
							.getSearchValue())) {
						log.info("*********titleFilterBean***********");
						getManagedBean().getTitleFilterBean().setSearchField("unspscTitle");
						getManagedBean().getTitleFilterBean().setCaseInsensitive(true);
						getManagedBean().getTitleFilterBean().setAndOr(SearchCondition.CONDITION_OR.value());

						titleEngFilterBean.setSearchField("unspscTitleEng");
						titleEngFilterBean.setCaseInsensitive(true);
						titleEngFilterBean.setSearchValue(getManagedBean().getTitleFilterBean().getSearchValue());
						titleEngFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
						
						SearchMultiFilterBean bilingualFilter = new SearchMultiFilterBean(getManagedBean().getTitleFilterBean(),titleEngFilterBean);
						
						searchBean.getDataFilterBeans().add(bilingualFilter);
					}
					
					/*[#27625] begin*/
					String itemType;
					if(StringUtils.isNotBlank(getManagedBean().getTypeName())){
						itemType = getManagedBean().getTypeName();
					}else{
						FacesScopes fs = new FacesScopes();
						itemType = fs.getParameter("itemType");
					}

					if (itemType != null) {// save parameter first time it come from
						// search button
						setItemTypeParameter(itemType);
					}else
						setItemTypeParameter(null);
					/*[#27625] end*/
					

					if (itemType != null) {// save parameter first time it come from
						// search button
						setItemTypeParameter(itemType);
					}

					// get parameter from backup property whenever table be reloaded
					if (getItemTypeParameter() != null
							&& !getItemTypeParameter().isEmpty()) {

						if (getItemTypeParameter().equals("S")
								|| getItemTypeParameter().equals("2")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("unspscCode",
											SearchOperator.OPERATOR_GREATER_EQUAL
											.value(), "70000000"));
						} else if (getItemTypeParameter().equals("P")
								|| getItemTypeParameter().equals("1")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("unspscCode",
											SearchOperator.OPERATOR_LESS.value(),
											"70000000"));
						}
					}


					if(this.getSortField()==null){
						SearchOrderBean orderBean = new SearchOrderBean("unspscCode", true);

						searchBean.getSortColumns().add(orderBean);
					}

					CodificationReqDTO reqDTO = new CodificationReqDTO();
					reqDTO.setSearchBean(searchBean);

					CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);


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

		this.getSearchDataModel().reloadData();
	}
	
	public void extensionCodeSearch() {
	if (this.getManagedBean().getData()== null){
        this.getManagedBean().setDisableSelectBtn(true);
        extensionAttributeSearchData.reloadData();
	}else{
	   	this.getManagedBean().setDisableSelectBtn(false);
		extensionAttributeSearchData.reloadData();
	}
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

	public String backToPrevPortal(){
		return "codificationMaintenance.xhtml?faces-redirect=true";
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

		boolean flag = false;
		String segmentCode = "";

		if (unspscCode != null && unspscCode.trim().length() > 0) {
			segmentCode = unspscCode.substring(0, 2);
		}
		
		log.info("segmentCode " + segmentCode);
		
		if (Integer.parseInt(segmentCode) > 69) {
			flag = true;
		}
		
		log.info("flag " + flag);

		return flag;
		
	}


	public String getItemTypeParameter() {
		return itemTypeParameter;
	}

	public void setItemTypeParameter(String itemTypeParameter) {
		log.info(".................setItemTypeParameter................");
		this.itemTypeParameter = itemTypeParameter;
	}


	//20190821@azza - CR25551 - Start
	public void resetSearch() {
		this.getManagedBean().setTitleFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.setCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.setSearchDataModel(new SearchPagingDataModel());
		this.getManagedBean().setAutoCompleteUnspscCode(null);
		//20190821@azza - CR25551 - End
		this.getManagedBean().setTypeName(null);
	}

	//20190827@azza - CR25551 - Start
	public String PopupOption(CmUnspsc obj) {

		clearPopup();

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
				
			reqDTO.setUnspscCode(obj.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);

			
		if (resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();

			this.getManagedBean().setSelectedUNSPSCObj(commodity);

			this.getManagedBean().setClassCode(commodity.getCmUnspsc().getUnspscCode());
			this.getManagedBean().setClassTitle(commodity.getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setClassTitleEng(commodity.getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setFamilyCode(commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setFamilyTitle(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setFamilyTitleEng(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setSegmentCode(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setSegmentTitle(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setSegmentTitleEng(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			//35503 Start
			this.getManagedBean().setAuditCommodityCode(commodity.getUnspscCode());
			this.getManagedBean().setAuditCommodityTitle(commodity.getUnspscTitle());
			this.getManagedBean().setAuditCommodityTitleEng(commodity.getUnspscTitleEng());

			this.getManagedBean().setAuditClassCode(commodity.getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditClassTitle(commodity.getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditClassTitleEng(commodity.getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setAuditFamilyCode(commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditFamilyTitle(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditFamilyTitleEng(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setAuditSegmentCode(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditSegmentTitle(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditSegmentTitleEng(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitleEng());
			//35503 End

		}

		return "optionItemCodeMaintenance.xhtml?faces-redirect=true";
	}

	
	public String PopupExtensionCode(CmUnspsc unspscObj) {
		log.debug("**********goToPopupExtensionCode************");
		

		this.getManagedBean().setCommodityCode("");
		this.getManagedBean().setCommodityTitle("");
		this.getManagedBean().setCommodityTitleEng("");
		
		//35503 Start - To set null for all list
		this.getManagedBean().setAuditTypeExtList(null);
		this.getManagedBean().setAuditBrandExtList(null);
		this.getManagedBean().setAuditMeasurementExtList(null);
		this.getManagedBean().setAuditColourExtList(null);
		//35503 End

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
			

			reqDTO.setUnspscCode(unspscObj.getUnspscCode());
			CodificationResDTO resDTO = unspscSO
					.retrieveUNSPSCByCommodityCode(reqDTO);
			
		
		if (resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();

			this.getManagedBean().setSelectedUNSPSCObj(commodity);

			this.getManagedBean().setClassCode(commodity.getCmUnspsc().getUnspscCode());
			this.getManagedBean().setClassTitle(commodity.getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setClassTitleEng(commodity.getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setFamilyCode(commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setFamilyTitle(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setFamilyTitleEng(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setSegmentCode(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setSegmentTitle(commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setSegmentTitleEng(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			//35503 Start - To get code and title for all code
			this.getManagedBean().setAuditCommodityCode(commodity.getUnspscCode());
			this.getManagedBean().setAuditCommodityTitle(commodity.getUnspscTitle());
			this.getManagedBean().setAuditCommodityTitleEng(commodity.getUnspscTitleEng());

			this.getManagedBean().setAuditClassCode(commodity.getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditClassTitle(commodity.getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditClassTitleEng(commodity.getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setAuditFamilyCode(commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditFamilyTitle(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditFamilyTitleEng(commodity.getCmUnspsc().getCmUnspsc().getUnspscTitleEng());

			this.getManagedBean().setAuditSegmentCode(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscCode());
			this.getManagedBean().setAuditSegmentTitle(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitle());
			this.getManagedBean().setAuditSegmentTitleEng(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc().getUnspscTitleEng());
			//35503 End

			//20200406@azza - CR25551 - Start
			this.getManagedBean().setExtension(null);
			this.getManagedBean().setStatus(null);
			//20200406@azza - CR25551 - End
			this.getManagedBean().setData(null);
			this.getExtensionAttributeSearchData().resetData();
			this.getManagedBean().setDisableSelectBtn(true);
		}
		return "optionExtensionCodeMaintenance.xhtml?faces-redirect=true";
	}

		
	  //20190827@azza - CR25551 - End


	public String SubmitUpdateData() {

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
			return null;

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
			return null;

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
			return null;

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
			return null;

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
		
		//35503 Start - for audit trail required
		if (resDTO.isSuccessful()) {
			IAuditTrailSO auditCodificationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
					IAuditTrailSO.class);

			if (auditCodificationSO.isAuditConfig(AuditCMConst.CM_AU_CONF)) {
				AuditCodificationManagementBackingBeanTemplate auTemplate = new AuditCodificationManagementBackingBean();
				auTemplate.saveAuditItemCodeMaintenance();
			}
		}
		//35503 End - for audit trail required
		
		renewSesionObject(); 
		
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
			MessageConst.INFO_710023, MessageUtils.BUNDLE_COMMON_MESSAGES,
     		new FacesScopes().getUserLocale()));	
		    FacesContext context = FacesContext.getCurrentInstance();
		    context.getExternalContext().getFlash().setKeepMessages(true);
		    context.addMessage(null, fm);
		
	  	FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null,  CommonUIConst.ACKNOWLEDGE_PAGE);
	  	
		return null;			
	
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
	
	public SearchFilterBean getNameFilterBean() {
		return nameFilterBean;
	}

	public void setNameFilterBean(SearchFilterBean nameFilterBean) {
		this.nameFilterBean = nameFilterBean;
	}
	
	public SearchFilterBean getStatusExtensionFilterBean() {
		return statusExtensionFilterBean;
	}

	public void setStatusExtensionFilterBean(SearchFilterBean statusExtensionFilterBean) {
		this.statusExtensionFilterBean = statusExtensionFilterBean;
	}



	private class ExtensionAttributeSearchData extends
	PagingDataModel<Object[]> {
		
		private static final long serialVersionUID = 1L;

		public ExtensionAttributeSearchData() {
			super("extensionAttributeTableID");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {



			System.out.println("........search extension.................");
			log.info("Unspsc Code:"+ getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
			
			
			CodificationMaintenanceBackingBean bb = new CodificationMaintenanceBackingBean();


			ICodificationSO codificationSO = null;
			CodificationResDTO resDTO = null;
			try {
				

			
				
				codificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm",
								ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				
				System.out.println("statusfilter " + getManagedBean().getstatus());
				
				reqDTO.setSsearchBeanTypeearchBeanType(searchBean);
				reqDTO.setUnspscCode(getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
				reqDTO.setExtensionName(getManagedBean().getExtension());
				reqDTO.setExtension(getManagedBean().getData());
				reqDTO.setStatus(getManagedBean().getstatus());
				

				resDTO = codificationSO.searchUnspscObj(reqDTO);
				
				if (resDTO.getUnspscObj() != null) {
					
					log.info(" unspsc id " + resDTO.getUnspscObj().getUnspscId());
					
					reqDTO.setUnspscId(resDTO.getUnspscObj().getUnspscId());
				}
			
				
				System.out.println("*********in********");
				
				resDTO = codificationSO.SearchExtensionType(reqDTO);
				
						
				Map tempMap = new HashMap();
				for (int i = 0; i < resDTO.getResulttypeBeanType().getResultList().size(); i++) {
					Object[] objects = (Object[]) resDTO.getResulttypeBeanType().getResultList().get(i);
					
					log.info(" objects " + String.valueOf(objects[0]) + String.valueOf(objects[1]) + String.valueOf(objects[2]) + String.valueOf(objects[3]));
							
					tempMap.put(objects, objects);
				}

				
				if (resDTO != null) {	
					
					log.info("*************End*****************");
					return resDTO.getResulttypeBeanType();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return null;
		}

	}
	   
	   /**
		 * reset action after change search plan type
		 * 
		 * @param paramCode
		 */

		
		public void changeSearchPlanType(String data) {
			log.info("++++++++changeSearchPlanType++++++" + data);
			
				if(data == "t"){
					System.out.println("typeextension " +  data);
				}else if(data == "b"){
					System.out.println("typeextension " +  data);
				}else if(data == "m"){
					System.out.println("typeextension " +  data);
				}else if(data == "c"){
					System.out.println("typeextension " +  data);
				}
			}
		
		
		public String unspscSearchPortal(CmUnspsc unspscObj) {
			
			this.getManagedBean().setAutoCompleteUnspscCode(unspscObj);	
						
			//20200529@azza - CR25551 - Start 
			this.getManagedBean().setTitleFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
			//20200529@azza - CR25551 - End 
			
			this.getSearchDataModel().reloadData();
			
			System.out.println("NAV_TO_CODIFICATION_MAINTENANCE");
			return NAV_TO_CODIFICATION_MAINTENANCE + "?faces-redirect=true";
					
		}
		
		
		public void unspscPopUpSearch(CmUnspsc obj) {
			
			log.info("obj " + obj );

			clearPopup();

			
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
		
		
		public void goToAddServiceConfirmPopUp(CmUnspsc unspscObj) {
			// set commodity
			System.out
			.println("...............goToAddServiceConfirmPopUp..............");
			log.info("..............unspscObj.getUnspscCode()="
					+ unspscObj.getUnspscCode());
			this.getManagedBean().setSelectedUNSPSCObj(unspscObj);
			
		}
				
		
		/**
		 * get record status options
		 * 
		 * @return SelectItem[]
		 * 
		 */
		
		
		public static SelectItem[] getStatusOptionsDefault() {
			CodificationMaintenanceBackingBean bb = new CodificationMaintenanceBackingBean();
			
			SelectItem[] items = new SelectItem[3];
			items[0] = new SelectItem(bb.RECORD_STATUS_NULL,
					ResourceBundleUtils.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
		
			items[1] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
					ResourceBundleUtils.getMessage("lbl.cm.active",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[2] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED,
					ResourceBundleUtils.getMessage("lbl.cm.inactive",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			return items;
		}
		
		
		public static SelectItem[] getStatusExtensionOptionsDefault() {
			SelectItem[] items = new SelectItem[2];
			items[0] = new SelectItem(DbValueConst.RECORD_STATUS_ACTIVE,
					ResourceBundleUtils.getMessage("lbl.cm.active",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(DbValueConst.RECORD_STATUS_SUSPENDED,
					ResourceBundleUtils.getMessage("lbl.cm.inactive",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			return items;
		}
	
		
		public static SelectItem[] getStatusExtensionDefault() {
			
		CodificationMaintenanceBackingBean bb = new CodificationMaintenanceBackingBean();
	
			
			SelectItem[] items = new SelectItem[5];
			items[0] = new SelectItem(bb.EXTENSION_NULL,
					ResourceBundleUtils.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(bb.EXTENSION_TYPE,
					ResourceBundleUtils.getMessage("lbl.cm.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[2] = new SelectItem(bb.EXTENSION_BRAND,
					ResourceBundleUtils.getMessage("lbl.cm.brand",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[3] = new SelectItem(bb.EXTENSION_MEASUREMENT,
					ResourceBundleUtils.getMessage("lbl.cm.measurement",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			items[4] = new SelectItem(bb.EXTENSION_COLOR,
					ResourceBundleUtils.getMessage("lbl.cm.color",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
			return items;
		}
		
		
		/**
		 * get Status options
		 * 
		 * @return SelectItem[]
		 */
		public SelectItem[] getStatusOptions() {
			log.info("getStatusOptions");
			return CodificationMaintenanceBackingBean.getStatusOptionsDefault();
		}
		
		public SelectItem[] getExtensionOptions() {
			log.info("getExtensionOptions");
			return CodificationMaintenanceBackingBean.getStatusExtensionDefault();
		}
		
		
		public SelectItem[] getStatusExtensionOptions() {
			log.info("getStatusExtensionOptions ");
			return CodificationMaintenanceBackingBean.getStatusExtensionOptionsDefault();
		}
	
		
		
		//Submit button to update status for extension type..START
		
		public void SubmitUpdateStatusExtension(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
			
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		CodificationResDTO resDTO = null;
		
		//35503 Start
		this.getManagedBean().setAuditTypeExtList(null);
		this.getManagedBean().setAuditBrandExtList(null);
		this.getManagedBean().setAuditMeasurementExtList(null);
		this.getManagedBean().setAuditColourExtList(null);
		//35503 End
		
		log.info("Data Type " + this.getManagedBean().getData());
		
		if (this.getManagedBean().getData().equals("t")) {

			for (int i = 0; i < getManagedBean().getUpdatedStatus().size(); i++) {
				Object[] objects = (Object[]) getManagedBean().getUpdatedStatus().get(i);

				log.info("OBJECT " + getManagedBean().getUpdatedStatus().size());

				reqDTO.setTypeId(Long.parseLong(objects[2].toString()));
				reqDTO.setUnspscId(Long.parseLong(objects[0].toString()));

				resDTO = unspscSO.searchCmTypeObj(reqDTO);

				if (resDTO.getCmType() != null) {

					System.out.println("search " + resDTO.getCmType().getTypeId());
					System.out.println("search " + objects[5].toString());

					Integer result = Integer.valueOf(objects[5].toString());

					resDTO.getCmType().setRecordStatusFlg(result);
					resDTO.getCmType().getTypeId();
					resDTO.getCmType().getTypeCode();

					log.info("Type id for CmType " + resDTO.getCmType().getTypeId());

				}

				reqDTO.getExtensionStatusList().add(resDTO.getCmType());

			}

			log.info("------ start to update ------");
			
			//35503 Start
			this.getManagedBean().getAuditTypeExtList().addAll(reqDTO.getExtensionStatusList());
			//35503 End

			resDTO = unspscSO.updateTypeExtensionStatus(reqDTO);
		}
		
		if (this.getManagedBean().getData().equals("b")) {

			for (int i = 0; i < getManagedBean().getUpdatedStatus().size(); i++) {
				Object[] objects = (Object[]) getManagedBean().getUpdatedStatus().get(i);

				reqDTO.setBrandId(Long.parseLong(objects[2].toString()));
				reqDTO.setUnspscId(Long.parseLong(objects[0].toString()));

				resDTO = unspscSO.searchCmBrandObj(reqDTO);

				if (resDTO.getCmBrand() != null) {

					System.out.println("search " + resDTO.getCmBrand().getBrandId());

					Integer result = Integer.valueOf(objects[5].toString());

					resDTO.getCmBrand().setRecordStatusFlg(result);
					resDTO.getCmBrand().getBrandId();
					resDTO.getCmBrand().getBrandCode();

					log.info("Brand id for CmBrand " + resDTO.getCmBrand().getBrandId());

				}
				reqDTO.getBrandExtensionStatusList().add(resDTO.getCmBrand());

			}
			log.info("------ start to update Brand ------");

			//35503 Start
			this.getManagedBean().getAuditBrandExtList().addAll(reqDTO.getBrandExtensionStatusList());
			//35503 End

			resDTO = unspscSO.updateBrandExtensionStatus(reqDTO);
		}

		if (this.getManagedBean().getData().equals("m")) {

			for (int i = 0; i < getManagedBean().getUpdatedStatus().size(); i++) {
				Object[] objects = (Object[]) getManagedBean().getUpdatedStatus().get(i);

				reqDTO.setMeasurementId(Long.parseLong(objects[2].toString()));
				reqDTO.setUnspscId(Long.parseLong(objects[0].toString()));

				resDTO = unspscSO.searchCmMeasurementObj(reqDTO);

				if (resDTO.getCmMeasurement() != null) {

					System.out.println("search " + resDTO.getCmMeasurement().getMeasurementId());

					Integer result = Integer.valueOf(objects[5].toString());

					resDTO.getCmMeasurement().setRecordStatusFlg(result);
					resDTO.getCmMeasurement().getMeasurementId();
					resDTO.getCmMeasurement().getMeasurementCode();

					log.info("Measurement id for CmMeasurement " + resDTO.getCmMeasurement().getMeasurementId());

				}
				
				//35503 Start - Fix as-is bug
				reqDTO.getMeasurementExtensionStatusList().add(resDTO.getCmMeasurement());
				//35503 End - Fix as-is bug

			}

			log.info("------ start to update measurment ------");
			
			//35503 Start
			this.getManagedBean().getAuditMeasurementExtList().addAll(reqDTO.getMeasurementExtensionStatusList());
			//35503 End
			
			resDTO = unspscSO.updateMeasurementExtensionStatus(reqDTO);
		}
		
		if (this.getManagedBean().getData().equals("c")) {

			for (int i = 0; i < getManagedBean().getUpdatedStatus().size(); i++) {
				Object[] objects = (Object[]) getManagedBean().getUpdatedStatus().get(i);

				reqDTO.setColorId(Long.parseLong(objects[2].toString()));

				resDTO = unspscSO.searchCmColorObj(reqDTO);

				if (resDTO.getCmColor() != null) {

					System.out.println("search " + resDTO.getCmColor().getColorId());

					Integer result = Integer.valueOf(objects[5].toString());

					resDTO.getCmColor().setRecordStatusFlg(result);
					resDTO.getCmColor().getColorId();
					resDTO.getCmColor().getColorCode();

					log.info("Color id for CmColor " + resDTO.getCmColor().getColorId());
				}

				//35503 Start - Fix as-is bug
				reqDTO.getColorExtensionStatusList().add(resDTO.getCmColor());
				//35503 End - Fix as-is bug
			}

			log.info("------ start to update color ------");

			//35503 Start
			this.getManagedBean().getAuditColourExtList().addAll(reqDTO.getColorExtensionStatusList());
			//35503 End

			//35503 Start - Fix as-is bug
			resDTO = unspscSO.updateColorExtensionStatus(reqDTO);
			//35503 End - Fix as-is bug
		}
		
		//35503 Start - for audit trail required
		if (resDTO.isSuccessful()) {
			IAuditTrailSO auditCodificationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
					IAuditTrailSO.class);

			if (auditCodificationSO.isAuditConfig(AuditCMConst.CM_AU_CONF)) {
				AuditCodificationManagementBackingBeanTemplate auTemplate = new AuditCodificationManagementBackingBean();
				auTemplate.saveAuditExtensionCodeMaintenance();
			}
		}
		//35503 End - for audit trail required
		   	 	
		renewSesionObject();

		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
				MessageConst.INFO_710024, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		context.addMessage(null, fm);

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
				.handleNavigation(FacesContext.getCurrentInstance(), null,  CommonUIConst.ACKNOWLEDGE_PAGE);			
	}
				
	
		
	public void resetExtension(){
		
		   log.info("reset Extension Data");
		
		    this.getManagedBean().setExtension(null);
		    this.getManagedBean().setStatus(null);
			this.getManagedBean().setData(null);
			this.getExtensionAttributeSearchData().reloadData();
			this.getManagedBean().setDisableSelectBtn(true);
			
		}
	
	
	
	public void clearPopup() {
		log.info("...............clearPopup...................");
		this.getManagedBean().setClassCode(null);
		this.getManagedBean().setClassTitle(null);

		this.getManagedBean().setFamilyCode(null);
		this.getManagedBean().setFamilyTitle(null);

		this.getManagedBean().setSegmentCode(null);
		this.getManagedBean().setSegmentTitle(null);
		
	}
	
	
	
}


// end
