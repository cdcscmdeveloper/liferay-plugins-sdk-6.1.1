/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.tpl.spec;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.TreeNode;
import my.ep.biz.bean.sc.QTActivityBean;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Template Specification Managed Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */

@ManagedBean(name="templateSpecMB")
@SessionScoped
public class TemplateSpecManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String searchSpecName;
	private String searchSpecDesc; 
	private Long searchProcurementCategoryId;
	private String searchTemplateTypeCode;
	private PmOrgProfile searchOrgProfile = new PmOrgProfile();
	private String searchTemplateTypeUser;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;
	private boolean pidSQ;
	private boolean isG2G;
	
	private List<TplSpecQuestion> tplSpecQuestions;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private ScQt scQt;
	private String docType;
	private PmOrgProfile orgProfileOfLoginUser;
	private boolean cloneBtnRendered;
	private SelectItem[] uomDropDownList;
/* #21695 START */
	private boolean secretaryValidate;
	private boolean zoneSpecUpdate;
	private boolean zonesWarnMessageUpdate;
	private String loginRoleType;
	
	/** from specification bean */
	private boolean selectAll;
	private TplSpecMaster specMaster;
	private String contentType;
	private SmSupplier supplier;
	private Long selectedScoreTypeId;
	private String specComponentId;
	private TplSpecQuestion selectedSpecQuestion;
	private TplSpecQuestion oriSpecQuestion;
	private List<TplSpecAnswer> specAnswerList;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private boolean updateSpecByAnswers;
	private List<TplSpecAnswer> specAnswersToDelete;
	/** from specification bean */
	
	/** for specification Zonal ****************************/
	/** The total suppliers spec loc. */
	private List<TplSpecLoc> totalSuppliersSpecLoc;
	
	/** The selected general tree nodes. */
	private TreeNode[] selectedGeneralTreeNodes;
	
	/** End for specification Zonal **************************/
	
	private Long parentSpecQuestionId;
	
	/** The all state code. */
	private PTJLocalityCode allStateCode;
	
	/** The delivery loc bean. */
	private List<BriefingZoneBean> deliveryLocBean;
	
	/** The is zone location. */
	private boolean isZoneLocation;
	
	/** The delivery loc remove list. */
	private List<ScQtDeliveryLoc> deliveryLocRemoveList;
	
	private String statecode;
	/** The zone name. */
	private String zoneName;
	
	/** The beans. */
	private List<ZonesBean> beans = new ArrayList<ZonesBean>();
	
	/** The qt activity beans. */
	private List<QTActivityBean> qtActivityBeans;
	
	/** The root zoanl tree. */
	private TreeNode rootZoanlTree;
	
	/** The index. */
	private int index = -1;
	
	/** The selected flag. */
	private boolean selectedFlag;
	
	/** The sc qt check st2tech proposal. */
	private List<ScQtCheckList> scQtCheckSt2techProposal;
	
	/** The sc qt check st2finance proposal list. */
	private List<ScQtCheckList> scQtCheckSt2financeProposalList;
	
	private List<SelectItem> offer;
	
	private Boolean offerType;
	
	private boolean offerZoneSelected;
	
	/** The selected nodes. */
	private TreeNode[] selectedNodes;
	
	/** The qt. */
	private ScQt qt;
	
	/**
	 * @return the beans
	 */
	public List<ZonesBean> getBeans() {
		return beans;
	}
	
	/** The temp zone name. */
	private String tempZoneName;
	
	/** The evaluatio stage active index. */
	private int evaluatioStageActiveIndex;//#25138
	
	/** Import & Export */
	private boolean enableImportExport;//#25633
	
	/**
	 * @return the statecode
	 */
	public String getStatecode() {
		return statecode;
	}

	/**
	 * @param statecode
	 *            the statecode to set
	 */
	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}
	
	/**
	 * @param beans the beans to set
	 */
	public void setBeans(List<ZonesBean> beans) {
		this.beans = beans;
	}
	
	/**
	 * @return the scQtCheckSt2techProposal
	 */
	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	/**
	 * @param scQtCheckSt2techProposal the scQtCheckSt2techProposal to set
	 */
	public void setScQtCheckSt2techProposal(
			List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}
	

	
	/** The zone general trees. */
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();

	
	
	public TemplateSpecManagedBean() {
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}
/* #21695 END */	
	/**
	 * @return the searchSpecName
	 */
	public String getSearchSpecName() {
		return searchSpecName;
	}

	/**
	 * @param searchSpecName the searchSpecName to set
	 */
	public void setSearchSpecName(String searchSpecName) {
		this.searchSpecName = searchSpecName;
	}

	/**
	 * @return the searchSpecDesc
	 */
	public String getSearchSpecDesc() {
		return searchSpecDesc;
	}

	/**
	 * @param searchSpecDesc the searchSpecDesc to set
	 */
	public void setSearchSpecDesc(String searchSpecDesc) {
		this.searchSpecDesc = searchSpecDesc;
	}

	/**
	 * @return the searchProcurementCategoryId
	 */
	public Long getSearchProcurementCategoryId() {
		return searchProcurementCategoryId;
	}

	/**
	 * @param searchProcurementCategoryId the searchProcurementCategoryId to set
	 */
	public void setSearchProcurementCategoryId(Long searchProcurementCategoryId) {
		this.searchProcurementCategoryId = searchProcurementCategoryId;
	}

	/**
	 * @return the searchTemplateTypeCode
	 */
	public String getSearchTemplateTypeCode() {
		return searchTemplateTypeCode;
	}

	/**
	 * @param searchTemplateTypeCode the searchTemplateTypeCode to set
	 */
	public void setSearchTemplateTypeCode(String searchTemplateTypeCode) {
		this.searchTemplateTypeCode = searchTemplateTypeCode;
	}

	/**
	 * @return the searchOrgProfile
	 */
	public PmOrgProfile getSearchOrgProfile() {
		return searchOrgProfile;
	}

	/**
	 * @param searchOrgProfile the searchOrgProfile to set
	 */
	public void setSearchOrgProfile(PmOrgProfile searchOrgProfile) {
		this.searchOrgProfile = searchOrgProfile;
	}

	/**
	 * @return the searchTemplateTypeUser
	 */
	public String getSearchTemplateTypeUser() {
		return searchTemplateTypeUser;
	}

	/**
	 * @param searchTemplateTypeUser the searchTemplateTypeUser to set
	 */
	public void setSearchTemplateTypeUser(String searchTemplateTypeUser) {
		this.searchTemplateTypeUser = searchTemplateTypeUser;
	}

	/**
	 * @return the searchItemTypeId
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	/**
	 * @param searchItemTypeId the searchItemTypeId to set
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	/**
	 * @return the searchSpecTypeId
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	/**
	 * @param searchSpecTypeId the searchSpecTypeId to set
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	/**
	 * @return the tplSpecQuestions
	 */
	public List<TplSpecQuestion> getTplSpecQuestions() {
		return tplSpecQuestions;
	}

	/**
	 * @param tplSpecQuestions the tplSpecQuestions to set
	 */
	public void setTplSpecQuestions(List<TplSpecQuestion> tplSpecQuestions) {
		this.tplSpecQuestions = tplSpecQuestions;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the pidSQ
	 */
	public boolean isPidSQ() {
		return pidSQ;
	}

	/**
	 * @param pidSQ the pidSQ to set
	 */
	public void setPidSQ(boolean pidSQ) {
		this.pidSQ = pidSQ;
	}

	/**
	 * @return the isG2G
	 */
	public boolean isG2G() {
		return isG2G;
	}

	/**
	 * @param isG2G the isG2G to set
	 */
	public void setG2G(boolean isG2G) {
		this.isG2G = isG2G;
	}

	/**
	 * @return the orgProfileOfLoginUser
	 */
	public PmOrgProfile getOrgProfileOfLoginUser() {
		return orgProfileOfLoginUser;
	}

	/**
	 * @param orgProfileOfLoginUser the orgProfileOfLoginUser to set
	 */
	public void setOrgProfileOfLoginUser(PmOrgProfile orgProfileOfLoginUser) {
		this.orgProfileOfLoginUser = orgProfileOfLoginUser;
	}

	/**
	 * @return the cloneBtnRendered
	 */
	public boolean isCloneBtnRendered() {
		return cloneBtnRendered;
	}

	/**
	 * @param cloneBtnRendered the cloneBtnRendered to set
	 */
	public void setCloneBtnRendered(boolean cloneBtnRendered) {
		this.cloneBtnRendered = cloneBtnRendered;
	}

	/**
	 * @return the uomDropDownList
	 */
	public SelectItem[] getUomDropDownList() {
		return uomDropDownList;
	}

	/**
	 * @param uomDropDownList the uomDropDownList to set
	 */
	public void setUomDropDownList(SelectItem[] uomDropDownList) {
		this.uomDropDownList = uomDropDownList;
	}
/* #21695 START */
	public boolean isSecretaryValidate() {
		return secretaryValidate;
	}

	public void setSecretaryValidate(boolean secretaryValidate) {
		this.secretaryValidate = secretaryValidate;
	}
	/**
	 * @return the orgProfileOfLoginUser
	 */
	public boolean getSecretaryValidate() {
		return secretaryValidate;
	}
	
	/**
	 * @return the zoneGeneralTrees
	 */
	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	/**
	 * @param zoneGeneralTrees the zoneGeneralTrees to set
	 */
	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}
	
	/**
	 * @return the rootZoanlTree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * @param rootZoanlTree the rootZoanlTree to set
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}
	
	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	/**
	 * @return the tempZoneName
	 */
	public String getTempZoneName() {
		return tempZoneName;
	}

	/**
	 * @param tempZoneName the tempZoneName to set
	 */
	public void setTempZoneName(String tempZoneName) {
		this.tempZoneName = tempZoneName;
	}
	
	/**
	 * @return the isZoneLocation
	 */
	public boolean isZoneLocation() {
		return isZoneLocation;
	}

	/**
	 * @param isZoneLocation the isZoneLocation to set
	 */
	public void setZoneLocation(boolean isZoneLocation) {
		this.isZoneLocation = isZoneLocation;
	}
	
	/**
	 * @return the selectedFlag
	 */
	public boolean isSelectedFlag() {
		return selectedFlag;
	}

	/**
	 * @param selectedFlag
	 *            the selectedFlag to set
	 */
	public void setSelectedFlag(boolean selectedFlag) {
		this.selectedFlag = selectedFlag;
	}
	
	/**
	 * @return the loginRoleType
	 */
	public String getLoginRoleType() {
		return loginRoleType;
	}

	/**
	 * @param loginRoleType the loginRoleType to set
	 */
	public void setLoginRoleType(String loginRoleType) {
		this.loginRoleType = loginRoleType;
	}
	
	/**
	 * @return the deliveryLocRemoveList
	 */
	public List<ScQtDeliveryLoc> getDeliveryLocRemoveList() {
		return deliveryLocRemoveList;
	}

	/**
	 * @param deliveryLocRemoveList the deliveryLocRemoveList to set
	 */
	public void setDeliveryLocRemoveList(List<ScQtDeliveryLoc> deliveryLocRemoveList) {
		this.deliveryLocRemoveList = deliveryLocRemoveList;
	}
	
	/**
	 * @return the deliveryLocBean
	 */
	public List<BriefingZoneBean> getDeliveryLocBean() {
		return deliveryLocBean;
	}

	/**
	 * @param deliveryLocBean the deliveryLocBean to set
	 */
	public void setDeliveryLocBean(List<BriefingZoneBean> deliveryLocBean) {
		this.deliveryLocBean = deliveryLocBean;
	}
	
	/**
	 * @return the scQtCheckSt2financeProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeProposalList() {
		return scQtCheckSt2financeProposalList;
	}

	/**
	 * @param scQtCheckSt2financeProposalList the scQtCheckSt2financeProposalList to set
	 */
	public void setScQtCheckSt2financeProposalList(
			List<ScQtCheckList> scQtCheckSt2financeProposalList) {
		this.scQtCheckSt2financeProposalList = scQtCheckSt2financeProposalList;
	}
	
	/**
	 * @return the zoneSpecUpdate
	 */
	public boolean isZoneSpecUpdate() {
		return zoneSpecUpdate;
	}

	/**
	 * @param zoneSpecUpdate the zoneSpecUpdate to set
	 */
	public void setZoneSpecUpdate(boolean zoneSpecUpdate) {
		this.zoneSpecUpdate = zoneSpecUpdate;
	}

	/**
	 * @return the zonesWarnMessageUpdate
	 */
	public boolean isZonesWarnMessageUpdate() {
		return zonesWarnMessageUpdate;
	}

	/**
	 * @param zonesWarnMessageUpdate the zonesWarnMessageUpdate to set
	 */
	public void setZonesWarnMessageUpdate(boolean zonesWarnMessageUpdate) {
		this.zonesWarnMessageUpdate = zonesWarnMessageUpdate;
	}

	/**
	 * Gets the all state code.
	 * 
	 * @return the all state code
	 */
	public PTJLocalityCode getAllStateCode() {
		return allStateCode;
	}

	/**
	 * Sets the all state code.
	 * 
	 * @param allStateCode
	 *            the new all state code
	 */
	public void setAllStateCode(PTJLocalityCode allStateCode) {
		this.allStateCode = allStateCode;
	}
	
	/**
	 * @return the yearsDisplayList
	 */
	public List<SelectItem> getOffer() {
		return offer;
	}

	/**
	 * @param yearsDisplayList
	 *            the yearsDisplayList to set
	 */
	public void setOffer(List<SelectItem> offer) {
		this.offer = offer;
	}

	public Boolean getOfferType() {
		return offerType;
	}

	public void setOfferType(Boolean offerType) {
		this.offerType = offerType;
		
	}
	
	public Boolean getOfferZoneSelected() {
		return offerZoneSelected;
	}

	public void setOfferZoneSelected(Boolean offerZoneSelected) {
		this.offerZoneSelected = offerZoneSelected;
		
	}

	/**
	 * @return the contentType
	 */
	public Long getParentSpecQuestionId() {
		return parentSpecQuestionId;
	}



	public void setParentSpecQuestionId(TplSpecQuestion parentSpecQuestion, Long parentSpecQuestionId) {
		// TODO Auto-generated method stub
		this.parentSpecQuestionId = parentSpecQuestionId;
	}
	
	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	/**
	 * @return the specMaster
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * @param specMaster the specMaster to set
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}
	
	
	/**
	 * Constructor for template specification bean
	 * 
	 * @param contentType
	 * 		the content type
	 */
	public TemplateSpecManagedBean(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the supplier
	 */
	public SmSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}
	
	/**
	 * @return the selectedScoreTypeId
	 */
	public Long getSelectedScoreTypeId() {
		return selectedScoreTypeId;
	}

	/**
	 * @param selectedScoreTypeId the selectedScoreTypeId to set
	 */
	public void setSelectedScoreTypeId(Long selectedScoreTypeId) {
		this.selectedScoreTypeId = selectedScoreTypeId;
	}

	/**
	 * @return the specComponentId
	 */
	public String getSpecComponentId() {
		return specComponentId;
	}

	/**
	 * @param specComponentId the specComponentId to set
	 */
	public void setSpecComponentId(String specComponentId) {
		this.specComponentId = specComponentId;
	}
	
	/**
	 * @return the selectedSpecQuestion
	 */
	public TplSpecQuestion getSelectedSpecQuestion() {
		return selectedSpecQuestion;
	}

	/**
	 * @param selectedSpecQuestion the selectedSpecQuestion to set
	 */
	public void setSelectedSpecQuestion(TplSpecQuestion selectedSpecQuestion) {
		this.selectedSpecQuestion = selectedSpecQuestion;
	}

	/**
	 * @return the oriSpecQuestion
	 */
	public TplSpecQuestion getOriSpecQuestion() {
		return oriSpecQuestion;
	}

	/**
	 * @param oriSpecQuestion the oriSpecQuestion to set
	 */
	public void setOriSpecQuestion(TplSpecQuestion oriSpecQuestion) {
		this.oriSpecQuestion = oriSpecQuestion;
	}
	
	/**
	 * @return the specAnswerList
	 */
	public List<TplSpecAnswer> getSpecAnswerList() {
		return specAnswerList;
	}

	/**
	 * @param specAnswerList the specAnswerList to set
	 */
	public void setSpecAnswerList(List<TplSpecAnswer> specAnswerList) {
		this.specAnswerList = specAnswerList;
	}
	
	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}
	
	/**
	 * @return the updateSpecByAnswers
	 */
	public boolean isUpdateSpecByAnswers() {
		return updateSpecByAnswers;
	}

	/**
	 * @param updateSpecByAnswers the updateSpecByAnswers to set
	 */
	public void setUpdateSpecByAnswers(boolean updateSpecByAnswers) {
		this.updateSpecByAnswers = updateSpecByAnswers;
	}

	/**
	 * @return the specAnswersToDelete
	 */
	public List<TplSpecAnswer> getSpecAnswersToDelete() {
		return specAnswersToDelete;
	}

	/**
	 * @param specAnswersToDelete the specAnswersToDelete to set
	 */
	public void setSpecAnswersToDelete(List<TplSpecAnswer> specAnswersToDelete) {
		this.specAnswersToDelete = specAnswersToDelete;
	}
	
	/**
	 * Gets the total suppliers spec loc.
	 * 
	 * @return the total suppliers spec loc
	 */
	public List<TplSpecLoc> getTotalSuppliersSpecLoc() {
		return totalSuppliersSpecLoc;
	}

	/**
	 * Sets the total suppliers spec loc.
	 * 
	 * @param totalSuppliersSpecLoc
	 *            the new total suppliers spec loc
	 */
	public void setTotalSuppliersSpecLoc(List<TplSpecLoc> totalSuppliersSpecLoc) {
		this.totalSuppliersSpecLoc = totalSuppliersSpecLoc;
	}
	
	/**
	 * @return the selectedGeneralTreeNodes
	 */
	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	/**
	 * @param selectedGeneralTreeNodes the selectedGeneralTreeNodes to set
	 */
	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}
	

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Sets the selected nodes.
	 * 
	 * @param selectedNodes
	 *            the new selected nodes
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public int getEvaluatioStageActiveIndex() {
		return evaluatioStageActiveIndex;
	}

	public void setEvaluatioStageActiveIndex(int evaluatioStageActiveIndex) {
		this.evaluatioStageActiveIndex = evaluatioStageActiveIndex;
	}
	
/* #21695 END */	
	
	//#25633
	public boolean isEnableImportExport() {
		return enableImportExport;
	}

	public void setEnableImportExport(boolean enableImportExport) {
		this.enableImportExport = enableImportExport;
	}
	//#25633 - END
}
