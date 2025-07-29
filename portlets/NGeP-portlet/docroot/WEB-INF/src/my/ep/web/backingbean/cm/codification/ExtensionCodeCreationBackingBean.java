package my.ep.web.backingbean.cm.codification;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import my.ep.constant.MessageConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.cm.codification.ExtensionCodeCreationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ClassesVO;
import my.ep.web.vo.CommoditiesVO;
import my.ep.web.vo.NameNode;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

//35503 Start
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.cm.AuditCMConst;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBean;
import my.ep.web.backingbean.cm.audit.AuditCodificationManagementBackingBeanTemplate;
import my.ep.constant.ModuleCodeConst;
//35503 End

@ManagedBean(name = "extensionCodeCreationBackingBean")
@ViewScoped
public class ExtensionCodeCreationBackingBean extends
BaseBackingBean<ExtensionCodeCreationManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;


	public static final String NAV_TO_EXTENSION_REQUEST_FORM = "extensionCodeCreationRequestForm";
	public static final String NO_61 = "61";

	private static Logger log = Logger.getLogger(ExtensionCodeCreationBackingBean.class);

	private SearchFilterBean codeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean titleFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean levelFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmUnspsc> searchDataModel = new SearchPagingDataModel();

	private SearchFilterBean colorFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean titleEngFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());


	private TreeNode rootNodes;
	private TreeNode selectedNode;
	private String itemTypeParameter;

	public ExtensionCodeCreationBackingBean() {
	}
		
	/**
	 * This method is used to reset the value of UNSPSC Search
	 * 
	 */

	public String resetAction(){
		log.info("In reset action...................");

		this.getManagedBean().reset();
		log.info("after reset.........................");
		return null;
	}


	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			System.out
			.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>titleFilterBean.getSearchValue()="
					+ titleFilterBean.getSearchValue());
			if (getManagedBean().getAutoCompleteUnspscCode().getUnspscCode() != null
					&& getManagedBean().getAutoCompleteUnspscCode()
					.getUnspscCode().trim().length() > 0) {
				codeFilterBean.setSearchValue(getManagedBean()
						.getAutoCompleteUnspscCode().getUnspscCode());
			}

			if (StringUtils.isBlank((String) codeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) titleFilterBean
							.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {

				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);


				if (getManagedBean().getAutoCompleteUnspscCode()
						.getUnspscCode() != null
						&& getManagedBean().getAutoCompleteUnspscCode()
						.getUnspscCode().trim().length() > 0) {
					log.info("*********codeFilterBean***********");
					codeFilterBean.setSearchField("unspscCode");
					codeFilterBean.setSearchValue(getManagedBean()
							.getAutoCompleteUnspscCode().getUnspscCode());
					searchBean.getDataFilterBeans().add(codeFilterBean);
				}

				if (StringUtils.isNotBlank((String) titleFilterBean
						.getSearchValue())) {
					log.info("*********titleFilterBean***********");
					titleFilterBean.setSearchField("unspscTitle");
					titleFilterBean.setCaseInsensitive(true);
					titleFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());

					titleEngFilterBean.setSearchField("unspscTitleEng");
					titleEngFilterBean.setCaseInsensitive(true);
					titleEngFilterBean.setSearchValue(titleFilterBean.getSearchValue());
					titleEngFilterBean.setAndOr(SearchCondition.CONDITION_OR.value());
					
					SearchMultiFilterBean bilingualFilter = new SearchMultiFilterBean(titleFilterBean,titleEngFilterBean);
					
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
				log.info("............itemType=" + itemType);

				if (itemType != null) {// save parameter first time it come from
					// search button
					setItemTypeParameter(itemType);
				}else
					setItemTypeParameter(null);
				/*[#27625] end*/

				log.info("............getItemTypeParameter="+ getItemTypeParameter());
				log.info("............ isAssignCodeFlg ="+ getManagedBean().isAssignCodeFlg());
				// get parameter from backup property whenever table be reloaded
				//if(!getManagedBean().isAssignCodeFlg()) { /*[#27625]
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
				//}


				System.out.println("this.getSortField():"+this.getSortField());
				if(this.getSortField()==null){
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
		String unspscCode = getManagedBean().getAutoCompleteUnspscCode()
				.getUnspscCode();
		String unspscTitle = this.getManagedBean().getTitleFilterBean().getSearchValue().toString();


		if ((unspscCode == null && (unspscTitle == null || unspscTitle
				.isEmpty()))
				|| ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle
				.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		if ((unspscCode != null && !unspscCode.isEmpty())
				&& (unspscCode.length() != 8 || !StringUtils.isNumeric(unspscCode))){
			throw new EpException(MessageConst.ERROR_715026);
		}


		if(unspscCode == null && unspscTitle.trim().length()<3){
			throw new EpException(MessageConst.ERROR_715060);
		}

		this.getManagedBean().getSearchDataModel().reloadData();
	}

	
	private int compareFirstChoice(String code1, String code2, int last){
		if((code1.equals("000") || code1.equals("00")) && (code2.equals("000") || code2.equals("00"))){
			return 0;
		}else if((code1.equals("000") || code1.equals("00"))){
			return -1;
		}else if((code2.equals("000") || code2.equals("00"))){
			return 1;
		}
		return last;
	}; 
	


	/**
	 * Navigate to extension Search screen.
	 * 
	 * @return extensionSearch.xhtml
	 */

	public String goToExtensionSearchPortal(CmUnspsc obj) {
		log.debug("**********goToExtensionSearchPortal************");
		
		log.info("obj " + obj);

		this.getManagedBean().setMeasurementCriteria(null);
		this.getManagedBean().setColorCriteria(null);
		this.getManagedBean().setBrandCriteria(null);
		this.getManagedBean().setTypeCriteria(null);
		this.getManagedBean().setDisableSelectBtn(true);
		

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(obj.getUnspscCode());
		CodificationResDTO resDTO = unspscSO
				.retrieveUNSPSCByCommodityCode(reqDTO);

		
		if (resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();
			
			this.getManagedBean().setSelectedLovUnspsc(commodity);
			
			log.info("unspscCode " + this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			log.info("unspscTitle " + this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		}
		
		this.getManagedBean().getRequestExtCodeList().clear();
		System.out.println("NAV_TO_EXTENSION_REQUEST_FORM");
		return NAV_TO_EXTENSION_REQUEST_FORM + "?faces-redirect=true";
	}


	public SearchFilterBean getColorFilterBean() {
		return colorFilterBean;
	}

	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
		this.colorFilterBean = colorFilterBean;
	}

	public SearchFilterBean getTypeFilterBean() {
		return typeFilterBean;
	}

	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
		this.typeFilterBean = typeFilterBean;
	}

	public SearchFilterBean getBrandFilterBean() {
		return brandFilterBean;
	}

	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
		this.brandFilterBean = brandFilterBean;
	}

	public SearchFilterBean getMeasurementFilterBean() {
		return measurementFilterBean;
	}

	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
		this.measurementFilterBean = measurementFilterBean;
	}

	

	/**
	 * filter family code by segment code
	 * 
	 */

	public void filterFamily() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getSegmentCode()"
				+ this.getManagedBean().getSegmentCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getSegmentCode());

		CodificationResDTO resDTO = codificationSO.searchFamily(reqDTO);

		for (CmUnspsc family : resDTO.getFamilyList()) {

			this.getManagedBean()
			.getFamilyCriteria()
			.add(new SelectItem(family.getUnspscCode(), family
					.getUnspscTitle()));
		}

	}

	/**
	 * filter class code by family code
	 * 
	 */
	public void filterClass() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getFamilyCode()"
				+ this.getManagedBean().getFamilyCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getFamilyCode());

		CodificationResDTO resDTO = codificationSO.searchClass(reqDTO);

		for (CmUnspsc classes : resDTO.getClassList()) {

			this.getManagedBean()
			.getClassCriteria()
			.add(new SelectItem(classes.getUnspscCode(), classes
					.getUnspscTitle()));
		}

	}

	/**
	 * filter commodity code by class code
	 * 
	 */

	public void filterCommodity() {
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		log.info("this.getManagedBean().getClassCode()"
				+ this.getManagedBean().getClassCode());
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setParentCode(this.getManagedBean().getClassCode());

		CodificationResDTO resDTO = codificationSO.searchCommodity(reqDTO);

		for (CmUnspsc commodity : resDTO.getCommodityList()) {

			this.getManagedBean()
			.getCommodityCriteria()
			.add(new SelectItem(commodity.getUnspscCode(), commodity
					.getUnspscTitle()));
		}

	}

	public void filterExtType() {
		log.info("this.getManagedBean().getCommodityCode()"
				+ this.getManagedBean().getCommodityCode());
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setUnspscCode(this.getManagedBean().getCommodityCode());

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

		for (CmBrand brand : resDTO.getCommodityObj().getCmBrand()) {
			log.debug("code:" + brand.getBrandCode());
			if (!StringUtils.isBlank(brand.getBrandName())) {
				this.getManagedBean()
				.getBrandCriteria()
				.add(new SelectItem(brand.getBrandId(), brand
						.getBrandName()));
			}
		}

		for (CmType type : resDTO.getCommodityObj().getCmType()) {
			if (!StringUtils.isBlank(type.getTypeName())) {
				this.getManagedBean()
				.getTypeCriteria()
				.add(new SelectItem(type.getTypeId(), type
						.getTypeName()));
			}
		}

		for (CmMeasurement measurement : resDTO.getCommodityObj()
				.getCmMeasurement()) {
			if (!StringUtils.isBlank(measurement.getMeasurementName())) {
				this.getManagedBean()
				.getMeasurementCriteria()
				.add(new SelectItem(measurement.getMeasurementId(),
						measurement.getMeasurementName()));
			}
		}

		resDTO = codificationSO.searchColor(reqDTO);
		for (CmColor color : resDTO.getColorList()) {
			if (!StringUtils.isBlank(color.getColorName())) {
				this.getManagedBean()
				.getColorCriteria()
				.add(new SelectItem(color.getColorId(), color
						.getColorName()));
			}
		}

	}


	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}
	

	public void backToPrevPortal() {
		renewSesionObject(); 
		System.out.println("NAV_TO_MAIN_PAGE");
		FacesContext context = FacesContext.getCurrentInstance(); 
		javax.faces.context.ExternalContext externalContext = context.getExternalContext(); 
		try {
			externalContext.redirect("codification");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
					parent = (String) String.valueOf(code).substring(0, 2)
							+ "000000";
					log.debug("parent:" + parent);
				} else {
					log.debug("segment");
				}

			}

		}

		return parent;
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

			this.getManagedBean().setExtensionPopUpList(familyDTO.getFamilyList());

			this.getManagedBean().setSegmentCode(obj.getUnspscCode());
			this.getManagedBean().setSegmentTitle(obj.getUnspscTitle());

		} else if (level == 1) {
			log.info("***********11111****************");
			reqDTO.setParentCode(obj.getUnspscCode());
			CodificationResDTO ClassDTO = unspscSO.searchClass(reqDTO);

			this.getManagedBean().setExtensionPopUpList(ClassDTO.getClassList());

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

			this.getManagedBean().setExtensionPopUpList(
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


	public String backToExtensionSearch() {
		return "extensionCodeCreation.xhtml?faces-redirect=true";
	}

	public void onNodeSelect(NodeSelectEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();

		NameNode tr = (NameNode) tree.getData();
		log.debug("sequence:" + tr.getSeq());

	}

	public void onNodeExpand(NodeExpandEvent event) {

		DefaultTreeNode tree = (DefaultTreeNode) event.getTreeNode();
		NameNode tr = (NameNode) tree.getData();

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
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

				TreeNode families = new DefaultTreeNode("class", classesVO,
						tree);

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

				TreeNode families = new DefaultTreeNode("commodities",
						commoditiesVO, tree);

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
		
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		
	

		List<String> unspscCodes = new ArrayList<String>();
		
		
		try {
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			reqDTO.setUnspscCode(query);
			
			CodificationResDTO resDTO = codificationSO
					.searchUnspscCodeByAuto(reqDTO);
			
		
			if (resDTO.getUnspscList() != null)
				for (CmUnspsc unspsc : resDTO.getUnspscList()) {
						
					int code = (Integer.parseInt(unspsc.getUnspscCode().substring(0, 2)));
					
                
					if (code < 68){ //#35503, Fix as-is bug: #38632
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



	public void checkExistingExtension(String input, boolean isSelectFromList, String attr) {
		if (StringUtils.isNotBlank(input)) {
			List<SelectItem> sl = null;
			String label = null;
			if(attr.equalsIgnoreCase("type")){
				sl =  this.getManagedBean().getTypeCriteria();
				label = "lbl.cm.type";
			}else	if(attr.equalsIgnoreCase("brand")){
				sl =  this.getManagedBean().getBrandCriteria();
				label = "lbl.cm.brand";
			}else	if(attr.equalsIgnoreCase("measurement")){
				sl =  this.getManagedBean().getMeasurementCriteria();
				label = "lbl.cm.measurement";
			}else	if(attr.equalsIgnoreCase("color")){
				sl =  this.getManagedBean().getColorCriteria();
				label = "lbl.cm.color";
			}
			if(sl!=null && sl.size()>0){
				for (SelectItem ext : sl) {
					if (!StringUtils.isBlank(input)) {
						if (input.toLowerCase().trim().equals(ext.getLabel().toString().toLowerCase().trim())) {
							if(isSelectFromList){
								throw new EpException(MessageUtils
										.getMessage(MessageConst.ERROR_715068, new String[] { ""
												+ input },
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale()));
							}
							throw new EpException(
									MessageConst.ERROR_716009,
									new String[] { ResourceBundleUtils
											.getMessage(
													label,
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes()
													.getUserLocale()) });
						}
					}
				}
			}
		}
	}
	


	public CmType searchTypeOjbyTypeId(Long typeId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmType> searchBeanType = new SearchBean<CmType>();
		typeFilterBean.setSearchField("typeId");
		typeFilterBean.setSearchValue(typeId);
		searchBeanType.getDataFilterBeans().add(typeFilterBean);
		reqDTO.setTypeSearchBean(searchBeanType);
		resDTO = codificationSO.searchTypeObj(reqDTO);

		return resDTO.getTypeList().get(0);
	}

	public CmColor searchColorObjByColorId(long colorId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorId");
		colorFilterBean.setSearchValue(colorId);

		searchBeanColor.getDataFilterBeans().add(colorFilterBean);
		reqDTO.setColorSearchBean(searchBeanColor);
		resDTO = codificationSO.searchColorObj(reqDTO);

		return resDTO.getColorList().get(0);

	}

	public CmColor searchColorObjByColorCode(String colorCode) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
		colorFilterBean.setSearchField("colorCode");
		colorFilterBean.setSearchValue(colorCode);

		searchBeanColor.getDataFilterBeans().add(colorFilterBean);
		reqDTO.setColorSearchBean(searchBeanColor);
		resDTO = codificationSO.searchColorObj(reqDTO);

		return resDTO.getColorList().get(0);

	}

	public CmBrand searchBrandObjByBrandId(Long brandId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		ISearchBean<CmBrand> searchBeanBrand = new SearchBean<CmBrand>();
		brandFilterBean.setSearchField("brandId");
		brandFilterBean.setSearchValue(brandId);
		searchBeanBrand.getDataFilterBeans().add(brandFilterBean);

		reqDTO.setBrandSearchBean(searchBeanBrand);

		resDTO = codificationSO.searchBrandObj(reqDTO);

		return resDTO.getBrandList().get(0);

	}

	public CmMeasurement searchMeasurementByMeasurementId(Long measurementId) {

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		ISearchBean<CmMeasurement> searchBeanMeasurement = new SearchBean<CmMeasurement>();
		measurementFilterBean.setSearchField("measurementId");
		measurementFilterBean.setSearchValue(measurementId);
		searchBeanMeasurement.getDataFilterBeans().add(measurementFilterBean);

		reqDTO.setMeasurementSearchBean(searchBeanMeasurement);

		resDTO = codificationSO.searchMeasurementObj(reqDTO);

		return resDTO.getMeasurementList().get(0);

	}


	public void requestNewColor() {

		if (this.getManagedBean().isRequestColor()) {
			this.getManagedBean().setColorDisable(false);
			this.getManagedBean().setTempColorName(null);
		} else {
			this.getManagedBean().setColorDisable(true);
			this.getManagedBean().setTempColorName("N/A");
		}
	}

	public void requestNewType() {

		if (this.getManagedBean().isRequestType()) {
			this.getManagedBean().setTypeDisable(false);
			this.getManagedBean().setTempTypeName(null);
		} else {
			this.getManagedBean().setTypeDisable(true);
			this.getManagedBean().setTempTypeName("N/A");
		}
	}

	public void requestNewBrand() {

		if (this.getManagedBean().isRequestBrand()) {
			this.getManagedBean().setBrandDisable(false);
			this.getManagedBean().setTempBrandName(null);
		} else {
			this.getManagedBean().setBrandDisable(true);
			this.getManagedBean().setTempBrandName("N/A");
		}
	}

	public void requestNewMeasurement() {

		if (this.getManagedBean().isRequestMeasurement()) {
			this.getManagedBean().setMeasurementDisable(false);
			this.getManagedBean().setTempMeasurementName(null);
		} else {
			this.getManagedBean().setMeasurementDisable(true);
			this.getManagedBean().setTempMeasurementName("N/A");
		}
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public String getItemTypeParameter() {
		return itemTypeParameter;
	}

	public void setItemTypeParameter(String itemTypeParameter) {
		log.info(".................setItemTypeParameter................");
		this.itemTypeParameter = itemTypeParameter;
	}

	
	public void addMoreRequest() {
		log.info(".................addMoreRequest...................");
			
		this.getManagedBean().setEditMode(false);
		this.getManagedBean().setRequestType(false);
		this.getManagedBean().setRequestBrand(false);
		this.getManagedBean().setRequestMeasurement(false);
		this.getManagedBean().setRequestColor(false);

		this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		clearForm();
		this.getManagedBean().setTypeDisable(true);
		this.getManagedBean().setBrandDisable(true);
		this.getManagedBean().setMeasurementDisable(true);
		this.getManagedBean().setColorDisable(true);
	}
	
	
	
	public void addNewRequest() {
		log.info("...............addNewRequest...................");
		
	
		CmRequestCode cmRequestCode = new CmRequestCode();

		// ///check that there be at least one new request
		if (!this.getManagedBean().isRequestType()
				&& !this.getManagedBean().isRequestBrand()
				&& !this.getManagedBean().
			     isRequestMeasurement()
				&& !this.getManagedBean().isRequestColor()) {
			throw new EpException(MessageConst.ERROR_716010);

		}
		// //////////checking for redundancy
		if (this.getManagedBean().getTempTypeName()!= null) {
			checkingTypeRedundency(this.getManagedBean().getTempTypeName());
		}
		if (this.getManagedBean().getTempBrandName()!= null) {
			checkingBrandRedundency(this.getManagedBean().getTempBrandName());
		}
		if (this.getManagedBean().getTempMeasurementName()!= null) {
			checkingMeasurmentRedundency(this.getManagedBean().getTempMeasurementName());
		}
		if (this.getManagedBean().getTempColorName()!= null) {
			checkingColorRedundency(this.getManagedBean().getTempColorName());
		}
	
		// type
	 if (this.getManagedBean().isRequestType()== true) {     
		 if (this.getManagedBean().getTempTypeName().isEmpty()) {
		
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.type",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
				
				System.out.println("set TypeName");
				
				cmRequestCode.setTypeName(this.getManagedBean().getTempTypeName());
				cmRequestCode.setRequestType(true);

		}
	 }else{
		 if(this.getManagedBean().getTempTypeName()!= "N/A"){
				cmRequestCode.setTypeName(this.getManagedBean().getTempTypeName());
				cmRequestCode.setRequestType(true);
			}else{
				cmRequestCode.setTypeName("N/A");
			}
	 }
		// brand
	if (this.getManagedBean().isRequestBrand()== true) {
		if (this.getManagedBean().getTempBrandName().isEmpty()) {
			

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.brand",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
				
				System.out.println("set BrandName");
				
				cmRequestCode.setBrandName(this.getManagedBean().getTempBrandName());
				cmRequestCode.setRequestBrand(true);
		}			
	}else{
		if(this.getManagedBean().getTempBrandName()!= "N/A"){
			cmRequestCode.setBrandName(this.getManagedBean().getTempBrandName());
			cmRequestCode.setRequestBrand(true);
		}else{
			cmRequestCode.setBrandName("N/A");
		}
	}
		// color
	if (this.getManagedBean().isRequestColor()== true) {	
		 if (this.getManagedBean().getTempColorName().isEmpty()) {
			

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.color",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
					
				System.out.println("set Color");
				
				cmRequestCode.setColorName(this.getManagedBean().getTempColorName());
				cmRequestCode.setRequestColor(true);
		}
	}else{
		if(this.getManagedBean().getTempColorName()!= "N/A"){
			cmRequestCode.setColorName(this.getManagedBean().getTempColorName());
			cmRequestCode.setRequestColor(true);
		}else{
			cmRequestCode.setColorName("N/A");
		}	
	}
		// measurement
	if (this.getManagedBean().isRequestMeasurement()== true) {
		if (this.getManagedBean().getTempMeasurementName().isEmpty()) {
			

			throw new EpException(MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.cm.measurement",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		} else {
			
				System.out.println("set MeasurementName");

				cmRequestCode.setMeasurementName(this.getManagedBean().getTempMeasurementName());
				cmRequestCode.setRequestMeasurement(true);
		}
	}else{
		if(this.getManagedBean().getTempMeasurementName()!= "N/A"){
			cmRequestCode.setMeasurementName(this.getManagedBean().getTempMeasurementName());
			cmRequestCode.setRequestMeasurement(true);
		}else{
			cmRequestCode.setMeasurementName("N/A");
		}		
	}

		if(this.getManagedBean().isEditMode()){
			
			cmRequestCode.setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			cmRequestCode.setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
       
			
			this.getManagedBean().getRequestExtCodeList().set(this.getManagedBean().getEditModeIndex(), cmRequestCode);
		}else{	
			
			cmRequestCode.setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			cmRequestCode.setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
             
			
			System.out.println("type " + cmRequestCode.getTypeName());
			System.out.println("brand " + cmRequestCode.getBrandName());
			System.out.println("measurement " + cmRequestCode.getMeasurementName());
			System.out.println("color " + cmRequestCode.getColorName());
			
			
			this.getManagedBean().getRequestExtCodeList().add(cmRequestCode);
			
			this.getManagedBean().setDisableSelectBtn(false);
			
		}
		clearForm();
		this.getManagedBean().setTypeDisable(false);
		this.getManagedBean().setBrandDisable(false);
		this.getManagedBean().setMeasurementDisable(false);
		this.getManagedBean().setColorDisable(false);
		this.getManagedBean().setRequestType(false);
		this.getManagedBean().setRequestBrand(false);
		this.getManagedBean().setRequestMeasurement(false);
		this.getManagedBean().setRequestColor(false);
		

		RequestContext.getCurrentInstance().execute("extensionPopUp.hide();");
	}
	
	
	public void editExtensionRequest(int index){
		
		//35503 Start
		this.getManagedBean().setAuditRequestExtList(null);
		//35503 End
		this.getManagedBean().setEditMode(true);
		this.getManagedBean().setEditModeIndex(index);
		if(this.getManagedBean().getRequestExtCodeList()!=null && this.getManagedBean().getRequestExtCodeList().size()>0){
			CmRequestCode code = this.getManagedBean().getRequestExtCodeList().get(index);

			
			this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
			this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		
			this.getManagedBean().setRequestExtCode(code);
			
			
			
			if(code.getBrandName()!= "N/A"){
 			   this.getManagedBean().setTempBrandName(code.getBrandName());
 			   this.getManagedBean().setRequestBrand(true);
 			   this.getManagedBean().setBrandDisable(false);  //text box
			}else{
			   this.getManagedBean().setTempBrandName(code.getBrandName());
	 		   this.getManagedBean().setRequestBrand(false);
	 		   this.getManagedBean().setBrandDisable(true);  //text box	
			}
			
			
			if(code.getTypeName()!= "N/A"){
			   this.getManagedBean().setTempTypeName(code.getTypeName());
			   this.getManagedBean().setRequestType(true);
			   this.getManagedBean().setTypeDisable(false);  //text box
			}else{
			   this.getManagedBean().setTempTypeName(code.getTypeName());
			   this.getManagedBean().setRequestType(false);
			   this.getManagedBean().setTypeDisable(true);  //text box
			}
			
			
			if(code.getColorName()!= "N/A"){
			   this.getManagedBean().setTempColorName(code.getColorName());
			   this.getManagedBean().setRequestColor(true);
			   this.getManagedBean().setColorDisable(false);  //text box
			}else{
			   this.getManagedBean().setTempColorName(code.getColorName());
			   this.getManagedBean().setRequestColor(false);
			   this.getManagedBean().setColorDisable(true);  //text box
			}
		
			
			if(code.getMeasurementName()!= "N/A"){
			   this.getManagedBean().setTempMeasurementName(code.getMeasurementName());
			   this.getManagedBean().setRequestMeasurement(true);
			   this.getManagedBean().setMeasurementDisable(false);  //text box
			}else{
			    this.getManagedBean().setTempMeasurementName(code.getMeasurementName());
			    this.getManagedBean().setRequestMeasurement(false);
				this.getManagedBean().setMeasurementDisable(true);  //text box
				
			}
	   	}
	}

	
	public void deleteExtensionRequest(int index){
		this.getManagedBean().getRequestExtCodeList().remove(index);
		//35503 Start
		this.getManagedBean().setAuditRequestExtList(null);
		//35503 End
		
		if (this.getManagedBean().getRequestExtCodeList().size()> 0){
		    this.getManagedBean().setDisableSelectBtn(false);
		}else{
			 this.getManagedBean().setDisableSelectBtn(true);
		}

	}


	public void clearForm() {
		log.info("...............clearForm...................");
		this.getManagedBean().setTempTypeName("N/A");
		this.getManagedBean().setTempBrandName("N/A");
		this.getManagedBean().setTempMeasurementName("N/A");
		this.getManagedBean().setTempColorName("N/A");
		

	}
	
	
	public void resetSearch() {
		this.getManagedBean().setAutoCompleteUnspscCode(null);
		this.getManagedBean().setTitleFilterBean(null);
		this.getManagedBean().setTypeName(null);
		resetAction();
		
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
	
	
	public void checkingTypeRedundency(String typeInput) {
		log.info(".....checking redundancy for typeInput=" + typeInput);
		this.getManagedBean().setTypeRedundancy(false);
		if (typeInput != null && !typeInput.equals("")) {
			for (CmType type : this.getManagedBean().getTypeList()) {
				if (!StringUtils.isBlank(type.getTypeName())) {
					if (type.getTypeName().toLowerCase()
							.equals(typeInput.toLowerCase())) {
						this.getManagedBean().setTypeRedundancy(true);						
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.type",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingBrandRedundency(String brandInput) {
		log.info(".....checking redundancy for brandInput=" + brandInput);
		this.getManagedBean().setBarndRedundancy(false);
		if (brandInput != null && !brandInput.equals("")) {
			for (CmBrand brand : this.getManagedBean().getBrandList()) {
				if (!StringUtils.isBlank(brand.getBrandName())) {
					if (brand.getBrandName().toLowerCase()
							.equals(brandInput.toLowerCase())) {

						this.getManagedBean().setBarndRedundancy(true);
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.brand",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}

	public void checkingMeasurmentRedundency(String MeasurmentInput) {
		log.info(".....checking redundancy for MeasurmentInput="
				+ MeasurmentInput);
		this.getManagedBean().setMeasurRedundancy(false);
		if (MeasurmentInput != null && !MeasurmentInput.equals("")) {
			for (CmMeasurement measur : this.getManagedBean()
					.getMeasurementList()) {

				if (!StringUtils.isBlank(measur.getMeasurementName())) {
					if (measur.getMeasurementName().toLowerCase()
							.equals(MeasurmentInput.toLowerCase())) {
						this.getManagedBean().setMeasurRedundancy(true);
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.measurement",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });
					}
				}
			}
		}
	}


	public void checkingColorRedundency(String colorInput) {
		log.info(".....checking redundancy for colorInput=" + colorInput);
		log.info("colorList " + this.getManagedBean().getColorList());
		this.getManagedBean().setColorRedundancy(false);
		if (colorInput != null && !colorInput.equals("")) {
			for (CmColor color : this.getManagedBean().getColorList()) {
				if (!StringUtils.isBlank(color.getColorName())) {
					if (color.getColorName().toLowerCase()
							.equals(colorInput.toLowerCase())) {
						this.getManagedBean().setColorRedundancy(true);
						log.info("this.getManagedBean().isColorRedundancy()="
								+ this.getManagedBean().isColorRedundancy());
						throw new EpException(
								MessageConst.ERROR_716009,
								new String[] { ResourceBundleUtils
										.getMessage(
												"lbl.cm.color",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
												.getUserLocale()) });

					}
				}
			}
			log.info("end no exception");
		}
	}
	
	
	public String submitIntoDB() {

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",
				ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		
		//35503 Start
		this.getManagedBean().getAuditRequestExtList().addAll(this.getManagedBean().getRequestExtCodeList());
		this.getManagedBean().setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		//35503 End
		
		for (CmRequestCode code :  this.getManagedBean().getRequestExtCodeList()){
		
		reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().
				getUnspscCode());
		resDTO = unspscSO.searchUnspscObj(reqDTO);
		
		if (resDTO.getUnspscObj() != null) {
			
			log.info("unspscId" + resDTO.getUnspscObj().getUnspscId());
			log.info("title" + resDTO.getUnspscObj().getUnspscTitle());
			log.info("titleEng" + resDTO.getUnspscObj().getUnspscTitleEng());
			log.info("level" + resDTO.getUnspscObj().getUnspscLevel());
			
		reqDTO.getCommodityList().add(resDTO.getUnspscObj());
	   }
		reqDTO.getRequestExtList().add(code);
	   }
		resDTO = unspscSO.publishExtension(reqDTO);
		
		//35503 Start - for audit trail required
		if (resDTO.isSuccessful()) {
			IAuditTrailSO auditCodificationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
					IAuditTrailSO.class);

			if (auditCodificationSO.isAuditConfig(AuditCMConst.CM_AU_CONF)) {
				AuditCodificationManagementBackingBeanTemplate auTemplate = new AuditCodificationManagementBackingBean();
				auTemplate.saveAuditExtensionCodeCreation();
			}
		}
		//35503 End - for audit trail required
					
		renewSesionObject(); 
		
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
				MessageConst.INFO_710025 , MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		context.addMessage(null, fm);

		FacesContext.getCurrentInstance().getApplication().getNavigationHandler()
				.handleNavigation(FacesContext.getCurrentInstance(), null, CommonUIConst.ACKNOWLEDGE_PAGE);

		log.info("End Publish");
		return null;
   }
}
		


