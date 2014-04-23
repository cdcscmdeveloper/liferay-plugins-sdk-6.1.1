package my.ep.web.backingbean.sc.specification;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.specificationdata.SCSpecificationDataType;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.specification.SpecificationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "specificationBusinessOwner")
@ViewScoped
public class SpecificationBusinessOwner extends
		SourcingBaseBackingBean<SpecificationManagedBean> {

	private IPagingDataModel<CtCircularVer> searchCircular = new SearchCircularPagingDataModel();
	private IPagingDataModel<ScQtDoc> templateSpecCreate = new CreateTemplateSpecPagingDataModel();

	public SpecificationBusinessOwner() {
		FacesScopes scope = new FacesScopes();

	}

	private List<ScQtLocCriteria> getQuoteZones(List<TreeNode> treeNodes,
			ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();

		for (TreeNode treeNode : treeNodes) {
			List<TreeNode> stateNodes = treeNode.getChildren();

			for (TreeNode treeNode2 : stateNodes) {
				List<TreeNode> district = treeNode2.getChildren();

				for (TreeNode devision : district) {
					if (devision.getChildCount() > 0) {
						for (TreeNode districtNode : devision.getChildren()) {
							ScQtLocCriteria criteria = new ScQtLocCriteria();
							criteria.setPmDistrict(((ScQtLocCriteria) districtNode
									.getData()).getPmDistrict());
							criteria.setPmDivision(((ScQtLocCriteria) districtNode
									.getParent().getData()).getPmDivision());
							criteria.setPmState(((ScQtLocCriteria) treeNode2
									.getData()).getPmState());
							criteria.setScQtSupplierCriteria(this
									.getManagedBean().getSupplierCriteria());
							criteria.setChangedBy(1L);
							criteria.setCreatedBy(1L);
							criteria.setCreatedDate(new Timestamp(System
									.currentTimeMillis()));
							results.add(criteria);
						}
					} else {
						ScQtLocCriteria criteria = new ScQtLocCriteria();
						criteria.setPmDistrict(((ScQtLocCriteria) devision
								.getData()).getPmDistrict());
						criteria.setPmState(((ScQtLocCriteria) treeNode2
								.getData()).getPmState());
						criteria.setScQtSupplierCriteria(this.getManagedBean()
								.getSupplierCriteria());
						criteria.setChangedBy(1L);
						criteria.setCreatedBy(1L);
						criteria.setCreatedDate(new Timestamp(System
								.currentTimeMillis()));
						results.add(criteria);
					}

				}
			}

		}

		return results;
	}

	private List<TplSpecZoneDtl> getZoneBusinessOwnerLocations(
			List<TreeNode> treeNodes, TplSpecZoneDtl zoneDtl) {
		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		List<TplSpecZoneDtl> zoneList = new ArrayList<TplSpecZoneDtl>();

		for (TreeNode treeNode : treeNodes) {
			List<TreeNode> zoneNodes = treeNode.getChildren();
			// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
			zoneDtl = (TplSpecZoneDtl) zoneNodes.get(0).getData();
			for (TreeNode treeNode1 : zoneNodes) {
				List<TreeNode> stateNodes = treeNode1.getChildren();

				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();

					for (TreeNode devision : district) {
						if (devision.getChildCount() > 0) {
							for (TreeNode districtNode : devision.getChildren()) {
								TplSpecZoneLoc criteria = new TplSpecZoneLoc();

								criteria.setPmDistrict(((TplSpecZoneLoc) districtNode
										.getData()).getPmDistrict());
								criteria.setPmDivision(((TplSpecZoneLoc) districtNode
										.getParent().getData()).getPmDivision());
								criteria.setPmState(((TplSpecZoneLoc) treeNode2
										.getData()).getPmState());
								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
										.getData()));
								// criteria.setScQtSupplierCriteria(supplierCriteria);
								criteria.setChangedBy(1L);
								criteria.setCreatedBy(1L);
								criteria.setCreatedDate(new Timestamp(System
										.currentTimeMillis()));
								results.add(criteria);

							}
						} else {

							TplSpecZoneLoc criteria = new TplSpecZoneLoc();
							criteria.setPmDistrict(((TplSpecZoneLoc) devision
									.getData()).getPmDistrict());
							criteria.setPmState(((TplSpecZoneLoc) treeNode2
									.getData()).getPmState());
							criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
									.getData()));
							// criteria.setScQtSupplierCriteria(supplierCriteria);
							criteria.setChangedBy(1L);
							criteria.setCreatedBy(1L);
							criteria.setCreatedDate(new Timestamp(System
									.currentTimeMillis()));
							results.add(criteria);
							/*
							 * zoneDtl.setTplSpecZoneLocs(results);
							 * zoneDtl.setTplSpecZone
							 * (getManagedBean().getScQt().
							 * getScQtZoneCriteria()); zoneDtl.setChangedBy(1L);
							 * zoneDtl.setCreatedBy(1L);
							 * zoneDtl.setCreatedDate(new
							 * Timestamp(System.currentTimeMillis()));
							 * zoneList.add(zoneDtl);
							 */

						}

						// zoneDtl.setTplSpecZoneLocs(results);

					}
				}

			}

			zoneDtl.setTplSpecZoneLocs(results);
			// zoneDtl.setTplSpecZone(new ScQtZoneCriteria());
			zoneDtl.setChangedBy(1L);
			zoneDtl.setCreatedBy(1L);
			zoneDtl.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			zoneList.add(zoneDtl);
		}

		return zoneList;
	}

	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode treeNode : treeNodes) {

			ScQtLocCriteria locationDTO = (ScQtLocCriteria) treeNode.getData();
			if (locationDTO.getPmState() != null) {
				results.add(locationDTO.getPmState());
			}

		}
		return results;
	}

	private List<PmDivision> getPmDivisions(List<TreeNode> treeNodes) {
		List<PmDivision> results = new ArrayList<PmDivision>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode childNode : treeNode.getChildren()) {
				for (TreeNode divNode : childNode.getChildren()) {
					if (divNode.getChildren() != null) {
						ScQtLocCriteria locationDTO = (ScQtLocCriteria) divNode
								.getData();
						if (locationDTO.getPmDivision() != null) {
							results.add(locationDTO.getPmDivision());
						}
					}
				}
			}
		}
		return results;
	}

	private List<PmDistrict> getPmDistricts(List<TreeNode> treeNodes) {
		List<PmDistrict> results = new ArrayList<PmDistrict>();
		for (TreeNode treeNode : treeNodes) {
			if (treeNode.isLeaf()) {
				ScQtLocCriteria locationDTO = (ScQtLocCriteria) treeNode
						.getData();
				if (locationDTO.getPmDistrict() != null) {
					results.add(locationDTO.getPmDistrict());
				}
			} else {
				if (treeNode.getChildren() != null) {
					results.addAll(getPmDistricts(treeNode.getChildren()));
				}
			}
		}
		return results;
	}

	public void saveSpecificationDtls() {
		IQTDetailSO detailSO;
		try {
			QuationTenderReqDTO reqDTO = prepareSpecReq();
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderResDTO resDTo = detailSO.saveQTDtl(reqDTO);

			/*
			 * //SpecificationResDTO resDTO = specSO.saveQTDtl(reqDTO); if
			 * (resDTO.isSuccessful()) {
			 * FacesContext.getCurrentInstance().addMessage( null, new
			 * FacesMessage(FacesMessage.SEVERITY_INFO,
			 * "Record saved successfully.", ""
			 * 
			 * )); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			detailSO = null;
		}
	}

	public String newProceedAction() {
		if (this.getManagedBean().getScQt().isPanel() == false) {
			return "specificationCreation.xhtml?faces-redirect=true";
		}
		if (this.getManagedBean().getScQt().isPanel() == true
				&& this.getManagedBean().getCircularType()
						.equalsIgnoreCase("General")
				|| this.getManagedBean().getCircularType()
						.equalsIgnoreCase("Specific")) {
			return "specificationCreation.xhtml?faces-redirect=true";
			// return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}

		if (this.getManagedBean().getScQt().isPanel() == true
				&& this.getManagedBean().getCircularType()
						.equalsIgnoreCase("Central Contract")) {
			return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}
		return null;
	}

	public void resetCircular() {
		getManagedBean().setCirCularNo("");
		getManagedBean().setCircularName("");
		getManagedBean().setItemName("");
	}

	public String backAction() {

		return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";

	}

	public String businessOwnerTemplateSpec() {
		return "/xhtml/sc/main/createTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * To add empty row to category open code data table
	 */
	public void addNewCategoryAction() {
		// debugLogger("addNewCategoryAction");
		// ScQt scQt = getManagedBean().getScQtSpec().getScQt();
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getSuppCriteriaopenList();
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setSuppCriteriaopenList(qtCategoryList);
		}

		ScQtCategory category = new ScQtCategory();
		// .setScQt(scQt);
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		qtCategoryList.add(category);
	}

	public void addRestrictedCategoryAction() {
		// debugLogger("addNewCategoryAction");

		// ScQt scQt = getManagedBean().getScQtSpec().getScQt();
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setSuppCriteriarestrictedList(qtCategoryList);
		}

		ScQtCategory category = new ScQtCategory();
		// category.setScQt(scQt);
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		qtCategoryList.add(category);

	}

	public void saveCompleteTemplateSpec() {
		IQTTemplateSO qtTemplateSO = null;
		IStatusSO statussO = null;
		IOrgProfileSO ProfileSo = null;
		IQTDetailSO QtDetailsSO = null;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
			if (this.getManagedBean().getScqtDoc().getScQtCheckList()
					.getQtChecklistId() == null) {
				statussO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(this.getManagedBean().getScqtDoc()
						.getTplSpecMaster().getStatus().getStatusId());
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);

				ProfileSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
				profileReq.setOrgProfileId(1L);
				OrgProfileResDTO profileRes = ProfileSo
						.retrieveOrgProfileByProfileId(profileReq);
				PmParameter parameter = this.getManagedBean().getScqtDoc()
						.getTplSpecMaster().getTemplateTypeCode();
				parameter.setParameterId(146L);

				ScQtDoc scQtDOc = this.getManagedBean().getScqtDoc();
				TplSpecMaster specMaster = this.getManagedBean().getScqtDoc()
						.getTplSpecMaster();

				ScQtCheckList checkList = this.getManagedBean().getScqtDoc()
						.getScQtCheckList();
				checkList.setScQtDoc(scQtDOc);
				// specMaster.setTemplateTypeCode(parameter);

				java.util.Date date = new java.util.Date(); // Right now
				java.sql.Timestamp timestamp = new java.sql.Timestamp(
						date.getTime());

				/*
				 * TemplateSpecBackingBean specBB = new
				 * TemplateSpecBackingBean();
				 * 
				 * List<TplSpecQuestion> questions = getManagedBean()
				 * .getTemplateSpecBean().getSpecMaster()
				 * .getTplSpecQuestions(); ; // TplSpecQuestion queston =
				 * questions.remove(0); List<TplSpecQuestion> questions1 = new
				 * ArrayList<TplSpecQuestion>();
				 * 
				 * questions1.addAll(questions); List<TplSpecQuestion>
				 * questions2 = new ArrayList<TplSpecQuestion>(); for
				 * (TplSpecQuestion tpl : questions1) {
				 * 
				 * // TplSpecQuestion tpl2 = new TplSpecQuestion();
				 * TplSpecQuestion tpl1 = (TplSpecQuestion) BeanUtils
				 * .cloneBean(tpl); tpl1.setSpecQuestionId(null);
				 * tpl1.setTplSpecMaster(specMaster);
				 * tpl1.setTplSpecAnswers(null); tpl1.setTplSpecScorings(null);
				 * tpl1.setTplSpecTierQties(null); tpl1.setChangedBy(2L);
				 * tpl1.setCreatedBy(2L); tpl1.setCreatedDate(timestamp);
				 * questions2.add(tpl1); }
				 */

				// specMaster.setTplSpecQuestions(questions2);
				specMaster.setTemplateTypeCode(parameter);
				specMaster.setProcessType("SC");
				specMaster.setOrgProfile(profileRes.getPmOrgProfile());
				// specMaster.setDurationMonth(12);
				specMaster.setTemplateStatus("D");
				specMaster.setScQtDocs(null);
				specMaster.setChangedBy(2L);
				specMaster.setCreatedBy(2L);
				specMaster.setBrandNew(false);
				specMaster.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				/*
				 * this.getManagedBean().getScqtDoc()
				 * .getScQtCheckList().getTitle()
				 */
				specMaster.setSpecName(specMaster.getSpecName());
				specMaster.setCreatedDate(timestamp);

				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

				tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
						.getScQt().getQtNo());

				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

				tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

				checkList.setDocCategory("2");
				// checkList.setDefinationType(1);
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(specMaster.getSpecName());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(this.getManagedBean().getScQt());
				checkList.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				checkList.setPmStatus(statusres.getPmStatus());
				// checkList.setScQtDoc(qtDoc);

				scQtDOc.setScQt(tenderResDTO.getQtDetail());
				scQtDOc.setChangedBy(2L);
				scQtDOc.setCreatedBy(2L);
				scQtDOc.setCreatedDate(timestamp);
				scQtDOc.setTplDocMaster(null);
				scQtDOc.setScQtCheckList(checkList);
				scQtDOc.setTplSpecMaster(specMaster);
				// scQtDOc.setScQtZoneCriteria(null);

				// qtTemplateReqDTO.setSpecMaster(specMaster);
				qtTemplateReqDTO.setScQtDoc(scQtDOc);
			} else {

				ScQtDoc qtDoc = this.getManagedBean().getScqtDoc();
				ScQtCheckList checklist = this.getManagedBean().getScqtDoc()
						.getScQtCheckList();
				checklist.setTitle(qtDoc.getTplSpecMaster().getSpecName());
				/*
				 * List<TplSpecQuestion> questions = getManagedBean()
				 * .getTemplateSpecBean().getSpecMaster()
				 * .getTplSpecQuestions();
				 * 
				 * for (TplSpecQuestion tplSpecQuestion : questions) {
				 * tplSpecQuestion.setTplSpecMaster(qtDoc.getTplSpecMaster()); }
				 */
				qtDoc.setScQtCheckList(checklist);
				// qtDoc.getTplSpecMaster().setTplSpecQuestions(questions);
				qtTemplateReqDTO.setScQtDoc(qtDoc);

			}

			try {
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDTO = qtTemplateSO.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
				if (resDTO.isSuccessful()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Record saved successfully.", ""

							));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		} finally {
			qtTemplateSO = null;
			statussO = null;
			ProfileSo = null;
			QtDetailsSO = null;

		}

	}

	public void saveTemplateSpec() {
		IQTTemplateSO qtTemplateSO = null;
		IStatusSO statussO = null;
		IOrgProfileSO ProfileSo = null;
		IQTDetailSO QtDetailsSO = null;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
			if (this.getManagedBean().getScqtDoc().getScQtCheckList()
					.getQtChecklistId() == null) {
				statussO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(this.getManagedBean().getScqtDoc()
						.getTplSpecMaster().getStatus().getStatusId());
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);

				ProfileSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
				profileReq.setOrgProfileId(1L);
				OrgProfileResDTO profileRes = ProfileSo
						.retrieveOrgProfileByProfileId(profileReq);
				PmParameter parameter = this.getManagedBean().getScqtDoc()
						.getTplSpecMaster().getTemplateTypeCode();
				parameter.setParameterId(146L);

				ScQtDoc scQtDOc = this.getManagedBean().getScqtDoc();
				TplSpecMaster specMaster = this.getManagedBean().getScqtDoc()
						.getTplSpecMaster();

				ScQtCheckList checkList = this.getManagedBean().getScqtDoc()
						.getScQtCheckList();
				checkList.setScQtDoc(scQtDOc);
				// specMaster.setTemplateTypeCode(parameter);

				java.util.Date date = new java.util.Date(); // Right now
				java.sql.Timestamp timestamp = new java.sql.Timestamp(
						date.getTime());

				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();

				List<TplSpecQuestion> questions = getManagedBean()
						.getTemplateSpecBean().getSpecMaster()
						.getTplSpecQuestions();
				;
				// TplSpecQuestion queston = questions.remove(0);
				List<TplSpecQuestion> questions1 = new ArrayList<TplSpecQuestion>();

				questions1.addAll(questions);
				List<TplSpecQuestion> questions2 = new ArrayList<TplSpecQuestion>();
				for (TplSpecQuestion tpl : questions1) {

					// TplSpecQuestion tpl2 = new TplSpecQuestion();
					TplSpecQuestion tpl1 = (TplSpecQuestion) BeanUtils
							.cloneBean(tpl);
					tpl1.setSpecQuestionId(null);
					tpl1.setTplSpecMaster(specMaster);
					tpl1.setTplSpecAnswers(null);
					tpl1.setTplSpecScorings(null);
					tpl1.setTplSpecTierQties(null);
					tpl1.setChangedBy(2L);
					tpl1.setCreatedBy(2L);
					tpl1.setCreatedDate(timestamp);
					questions2.add(tpl1);
				}

				specMaster.setTplSpecQuestions(questions2);
				specMaster.setTemplateTypeCode(parameter);
				specMaster.setProcessType("SC");
				specMaster.setOrgProfile(profileRes.getPmOrgProfile());
				// specMaster.setDurationMonth(12);
				specMaster.setTemplateStatus("D");
				specMaster.setScQtDocs(null);
				specMaster.setChangedBy(2L);
				specMaster.setCreatedBy(2L);
				specMaster.setBrandNew(false);
				specMaster.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				/*
				 * this.getManagedBean().getScqtDoc()
				 * .getScQtCheckList().getTitle()
				 */
				specMaster.setSpecName(specMaster.getSpecName());
				specMaster.setCreatedDate(timestamp);

				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();

				tenderReqDTO.setQtTndrNo(this.getManagedBean().getScqtDoc()
						.getScQt().getQtNo());

				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();

				tenderResDTO = QtDetailsSO.retriveQtByQtNumber(tenderReqDTO);

				checkList.setDocCategory("1");
				// checkList.setDefinationType(1);
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(specMaster.getSpecName());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(this.getManagedBean().getScQt());
				checkList.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				checkList.setPmStatus(statusres.getPmStatus());
				// checkList.setScQtDoc(qtDoc);

				scQtDOc.setScQt(tenderResDTO.getQtDetail());
				scQtDOc.setChangedBy(2L);
				scQtDOc.setCreatedBy(2L);
				scQtDOc.setCreatedDate(timestamp);
				scQtDOc.setTplDocMaster(null);
				scQtDOc.setScQtCheckList(checkList);
				scQtDOc.setTplSpecMaster(specMaster);
				// scQtDOc.setScQtZoneCriteria(null);

				// qtTemplateReqDTO.setSpecMaster(specMaster);
				qtTemplateReqDTO.setScQtDoc(scQtDOc);
			} else {

				ScQtDoc qtDoc = this.getManagedBean().getScqtDoc();
				ScQtCheckList checklist = this.getManagedBean().getScqtDoc()
						.getScQtCheckList();
				checklist.setTitle(qtDoc.getTplSpecMaster().getSpecName());
				List<TplSpecQuestion> questions = getManagedBean()
						.getTemplateSpecBean().getSpecMaster()
						.getTplSpecQuestions();

				for (TplSpecQuestion tplSpecQuestion : questions) {
					tplSpecQuestion.setTplSpecMaster(qtDoc.getTplSpecMaster());
				}
				qtDoc.setScQtCheckList(checklist);
				qtDoc.getTplSpecMaster().setTplSpecQuestions(questions);
				qtTemplateReqDTO.setScQtDoc(qtDoc);

			}

			try {
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDTO = qtTemplateSO.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
				if (resDTO.isSuccessful()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Record saved successfully.", ""

							));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		} finally {
			qtTemplateSO = null;
			statussO = null;
			ProfileSo = null;
			QtDetailsSO = null;

		}

	}

	public void businessOwnerTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if (tabTitle.equalsIgnoreCase("General")) {
			retriveSpecdetails(this.getManagedBean().getScQt().getQtId());
		} else if (tabTitle.equalsIgnoreCase("Supplier Qualification Criteria")) {
			retriveBusinessOwnerQualificationCriteri(this.getManagedBean()
					.getScQt());

		} else if (tabTitle.equalsIgnoreCase("Technical Specification")) {
			retriveTechnicalSpecification();

		} else if (tabTitle.equalsIgnoreCase("Financial Specification")) {
			retriveFinancialSpecification();
		}

		else if (tabTitle.equalsIgnoreCase("Approval")) {
			retriveApprovalRemarks();
		}
	}

	public void retriveBusinessOwnerQualificationCriteri(ScQt scQt) {
		try {
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			// tenderReqDTO.setQtTndrNo("xxx");

			QuationTenderResDTO tenderResDTO = QtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);

			if (tenderResDTO.getQtDetail() != null) {
				// this.getManagedBean().setScQt(tenderResDTO.getQtDetail());
				this.getManagedBean().setSuppCriteriaopenList(
						tenderResDTO.getCategoryResultBean().getResultList());
				this.getManagedBean().setSuppCriteriarestrictedList(
						tenderResDTO.getCategoryResultBean().getResultList());
				if (tenderResDTO.getQtDetail().getScQtSupplierCriteria() == null) {
					this.getManagedBean().setSupplierCriteria(
							new ScQtSupplierCriteria());
				}

				else {
					this.getManagedBean().setSupplierCriteria(
							tenderResDTO.getQtDetail()
									.getScQtSupplierCriteria());
					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();

					for (TplSpecLoc localLoc : tenderResDTO
							.getLocationDetails()) {
						ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
						locCriteria.add(criteria);
					}

					List<PmState> states = SourcingLocationHelper
							.getStateFromLocationDTOs(locCriteria);

					List<PmDivision> divisions = SourcingLocationHelper
							.getDivisionFromLocationDTOs(locCriteria);
					List<PmDistrict> districts = SourcingLocationHelper
							.getDistrictFromLocationDTOs(locCriteria);
					List<PmCity> cities = SourcingLocationHelper
							.getCityFromLocationDTOs(locCriteria);

					getManagedBean().getZoneTrees().add(
							LocationSingleton.getInstance().buildZoneTree(null,
									states, districts, divisions, cities));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void retriveTechnicalSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;

		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSpecType("T");
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto != null) {
				List<ScQtCheckList> techSpecList = this.getManagedBean()
						.getTechnicalSpecList();
				if (techSpecList == null) {
					techSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getSpecResultBean().getResultList().size() != techSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto.getSpecResultBean()
							.getResultList()) {
						/*
						 * checklist.setScQtSpec(this.getManagedBean()
						 * .getScQtSpec());
						 */
						localList.add(checklist);
					}
					this.getManagedBean().setTechnicalSpecList(localList);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void retriveFinancialSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;

		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSpecType("F");
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);

			if (resDto != null) {
				List<ScQtCheckList> financialSpecList = this.getManagedBean()
						.getFinancialSpecList();
				if (financialSpecList == null) {
					financialSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getSpecResultBean().getResultList().size() != financialSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto.getSpecResultBean()
							.getResultList()) {
						/*
						 * checklist.setScQtSpec(this.getManagedBean()
						 * .getScQtSpec());
						 */
						localList.add(checklist);
					}
					this.getManagedBean().setFinancialSpecList(localList);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void retriveApprovalRemarks() {
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;
		try {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocType("QT");
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			resDto = qtDetailSO.retriveQtRemarks(reqDTO);
			List<ScQtRemark> qtRemarkList = this.getManagedBean()
					.getScQtRemarksList();
			if (qtRemarkList == null) {
				qtRemarkList = new ArrayList<ScQtRemark>();
			}
					
			if (resDto.getRemarks() != null) {
				for (ScRemark remark : resDto.getRemarks()) {
					ScQtRemark qtRemark = (ScQtRemark) remark;
					qtRemarkList.add(qtRemark);
				}
				this.getManagedBean().setScQtRemarksList(qtRemarkList);
			}
			initializeRemarks();
		} catch (Exception e) {

		} finally {

		}
	}

	public void initializeRemarks() {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		ScQtRemark remark = null;

		remark = new ScQtRemark();
		List<ScQtRemark> qtRemarkList = new ArrayList<ScQtRemark>();
		remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setCreatedUser(facesScopes.getUserSessionBean());
		remark.setScQt(this.getManagedBean().getScQt());
		PmStatus status = new PmStatus();
		status.setStatusId(1L);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		if (status != null) {
			statusReqDTO.setStatusId(status.getStatusId());
			StatusResDTO statusResDTO = iStatusSO
					.retrieveStatusByStatusId(statusReqDTO);
			remark.setPmStatus(statusResDTO.getPmStatus());

		}
		// qtRemarkList.add(remark);
		this.getManagedBean().getScQtRemarksList().add(remark);
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	private List<ScQtLocCriteria> getLocations(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (zoneLocation.isLeaf()) {
			ScQtLocCriteria locationDTO = (ScQtLocCriteria) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	private List<TplSpecZoneLoc> getZoneLocations(TreeNode zoneLocation) {

		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getZoneLocations(childrendLocationNode));
			}
		}

		return results;
	}

	private TplSpecZoneDtl getZone(TreeNode zoneLocation) {
		TplSpecZoneDtl result = null;
		if (zoneLocation.getData() instanceof TplSpecZoneDtl) {
			TplSpecZoneDtl locationDTO = (TplSpecZoneDtl) zoneLocation
					.getData();
			if (locationDTO != null) {
				result = locationDTO;
			} else {
				if (zoneLocation.getChildren() != null
						&& zoneLocation.getChildren().size() > 0) {
					for (TreeNode childLocationNode : zoneLocation
							.getChildren()) {
						result = getZone(childLocationNode);
					}
				}
			}
		} else {
			if (zoneLocation.getChildren() != null
					&& zoneLocation.getChildren().size() > 0) {
				for (TreeNode childLocationNode : zoneLocation.getChildren()) {
					result = getZone(childLocationNode);
				}
			}
		}

		return result;
	}

	public void editZoneLocation(TreeNode rootZoneLocation, int index) {
		List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);

		// ScQuoteZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L,
						states, districts, divisions, cities, 3));
		int i = index;

		this.getManagedBean().setIndex(i);

		// getManagedBean().setZoneName(zone.getZoneName());

	}

	public void editBusinessOwnerZoneLocation(TreeNode rootZoneLocation,
			int index) {
		List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

		TplSpecZoneDtl zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromZoneLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromZoneLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromZoneLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromZoneLocationDTOs(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZonalTree(1L,
						states, districts, divisions, cities, 3));
		int i = index;

		this.getManagedBean().setIndex(i);

		getManagedBean().setZoneName(zone.getZoneName());

	}

	public void showZoneLocationPopup() {

		Integer locationLevel = (Integer) this.getManagedBean().getScQt()
				.getZoneLocationLevel();

		if (locationLevel == null) {
			locationLevel = 3;
		}
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
	}

	public void showGeneralZoneLocationPopup() {

		Integer locationLevel = (Integer) this.getManagedBean().getScQt()
				.getZoneLocationLevel();

		if (locationLevel == null) {
			locationLevel = 3;
		}
		getManagedBean().setRootZoanlTree(
				LocationSingleton.getInstance().buildLocationZonalTree(1L,
						null, null, null, null, locationLevel));
	}

	public void showZoneLocationListPopup() {
		getManagedBean().setRootLocationTreeList(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
	}

	public void showZoneDeliveryAddressListPopup() {
		getManagedBean().setRootAddressLocationTreeList(
				LocationSingleton.getInstance().buildAddressLocationTree(1L,
						null));
	}

	public void updateZoneLocation() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}

		// ScQuoteZone zone = new ScQuoteZone();

		// zone.setZoneName(getManagedBean().getZoneName());

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		if (getManagedBean().getIndex() == -1) {

			getManagedBean().getZoneTrees().add(
					LocationSingleton.getInstance().buildZoneTree(null, states,
							districts, divisions, cities));
		}

		if (getManagedBean().getIndex() >= 0) {
			getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildZoneTree(null, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);

	}

	public void updateZoneGeneralLocation() {
		// List<TplSpecZoneLoc> locationDTOs = new ArrayList<TplSpecZoneLoc>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (!treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					allNodes.addAll(locationDTO);
				}

			}
		}
	

		TplSpecZoneDtl zone = new TplSpecZoneDtl();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromZoneLocationDTOs(allNodes);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromZoneLocationDTOs(allNodes);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromZoneLocationDTOs(allNodes);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromZoneLocationDTOs(allNodes);

		if (getManagedBean().getIndex() == -1) {
			getManagedBean().getZoneGeneralTrees().add(
					LocationSingleton.getInstance().buildGeneralZoneTree(zone,
							states, districts, divisions, cities));

		}

		else if (getManagedBean().getIndex() >= 0) {
			getManagedBean().getZoneGeneralTrees().remove(
					getManagedBean().getIndex());
			getManagedBean().getZoneGeneralTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildGeneralZoneTree(zone,
							states, districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);

	}

	public void updateZoneLocationList() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (!treeNode.getParent().isSelected()) {
					ScQtLocCriteria locationDTO = (ScQtLocCriteria) treeNode
							.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}

		TplSpecZoneDtl zone = new TplSpecZoneDtl();
		zone.setZoneName(getManagedBean().getZoneName1());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		getManagedBean().getZoneTreeList().add(
				LocationSingleton.getInstance().buildZoneTree(zone, states,
						districts, divisions, cities));
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationTree(1L, null,
						null, null, null));
		
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public QuationTenderReqDTO prepareSpecReq() {
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

		// ScQtSpec scqtSpec = this.getManagedBean().getScQtSpec();
		ScQt scQt = this.getManagedBean().getScQt();
		scQt.setItemType("P");

		List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
				this.getManagedBean().getZoneTrees(), null);
		if (locCriteria != null && !locCriteria.isEmpty()) {
			this.getManagedBean().getSupplierCriteria()
					.setScQtLocCriteria(locCriteria);
			this.getManagedBean().getSupplierCriteria()
					.setScQt(this.getManagedBean().getScQt());
			this.getManagedBean().getSupplierCriteria().setChangedBy(1L);
			this.getManagedBean().getSupplierCriteria().setCreatedBy(1L);
			this.getManagedBean().getSupplierCriteria()
					.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			scQt.setScQtSupplierCriteria(this.getManagedBean()
					.getSupplierCriteria());
		}
		List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
				this.getManagedBean().getZoneGeneralTrees(), null);
		if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
			ScQtZoneCriteria zoneCriteria = this.getManagedBean().getScQt()
					.getScQtZoneCriteria();
			List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
			for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
				tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
				zoneLocal.add(tplSpecZoneDtl);
			}
			zoneCriteria.setTplSpecZoneDtls(zoneLocal);
			zoneCriteria.setChangedBy(1L);
			zoneCriteria.setCreatedBy(1L);
			zoneCriteria.setCreatedDate(new Timestamp(System
					.currentTimeMillis()));
			scQt.setScQtZoneCriteria(zoneCriteria);

		}

				
		List<ScQtCategory> openList = this.getManagedBean()
				.getSuppCriteriaopenList();
		List<ScQtCategory> categoryList = new ArrayList();
		if (openList != null && !openList.isEmpty()) {
			for (ScQtCategory scQtCategory : openList) {

				scQtCategory.setPmCategoryL1(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				scQtCategory.setPmCategoryL2(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2());
				scQtCategory.setScQt(this.getManagedBean().getScQt());
				scQtCategory.setCreatedBy(1L);
				scQtCategory.setChangedBy(1L);
				scQtCategory.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				categoryList.add(scQtCategory);
				scQt.setScQtCategorys(categoryList);
			}

		}

		List<ScQtCategory> restrictedList = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (restrictedList != null && !restrictedList.isEmpty()) {

			for (ScQtCategory scQtCategory : restrictedList) {

				scQtCategory.setPmCategoryL1(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				scQtCategory.setPmCategoryL2(scQtCategory.getPmCategoryL3()
						.getPmCategoryL2());
				scQtCategory.setScQt(this.getManagedBean().getScQt());
				scQtCategory.setCreatedBy(1L);
				scQtCategory.setChangedBy(1L);
				scQtCategory.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));
				categoryList.add(scQtCategory);
				scQt.setScQtCategorys(categoryList);
			}
		}
		List<ScQtRemark> remarksList = this.getManagedBean()
				.getScQtRemarksList();
		if (remarksList != null && !remarksList.isEmpty()) {
			scQt.setScQtRemarks(remarksList);
		}

		reqDTO.setQtdetail(scQt);

		return reqDTO;

	}

	@PostConstruct
	public void init() {
		try {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			this.getManagedBean().setApprovalDate(sdf.format(d));

			if (getManagedBean().getScQt() == null) {
				// this.getManagedBean().setApprovalList(getAppDetails());
				retriveSpecdetails(146L);

			}

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void retriveSpecdetails(Long qtId) {
		try {
			IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(qtId);
			// tenderReqDTO.setQtTndrNo("xxx");
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			if (tenderResDTO.getQtDetail() != null) {
				getManagedBean().setScQt(tenderResDTO.getQtDetail());

				if (tenderResDTO.getQtDetail().getScQtZoneCriteria() == null) {
					getManagedBean().getScQt().setScQtZoneCriteria(
							new ScQtZoneCriteria());
				}
				if (tenderResDTO.getQtDetail().getPmProcurementCategory() == null) {
					getManagedBean().getScQt().setPmProcurementCategory(
							new PmParameter());
				}
				for (TplSpecZoneDtl zone : tenderResDTO.getZoneDetails()) {

					List<PmState> states = SourcingLocationHelper
							.getStateFromZoneLocationDTOs(zone
									.getTplSpecZoneLocs());
					List<PmDivision> divisions = SourcingLocationHelper
							.getDivisionFromZoneLocationDTOs(zone
									.getTplSpecZoneLocs());
					List<PmDistrict> districts = SourcingLocationHelper
							.getDistrictFromZoneLocationDTOs(zone
									.getTplSpecZoneLocs());
					List<PmCity> cities = SourcingLocationHelper
							.getCityFromZoneLocationDTOs(zone
									.getTplSpecZoneLocs());
					getManagedBean().getZoneGeneralTrees().add(
							LocationSingleton.getInstance()
									.buildGeneralZoneTree(zone, states,
											districts, divisions, cities));

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	public void onChangeQtType(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getScQt().setQtType(type);
	}

	public void checklistActionChange(AjaxBehaviorEvent event) {
		Long type = (Long) event.getComponent().getAttributes().get("value");
		/*
		 * this.getManagedBean().getScQtCheckSt2techProposal().get(0)
		 * .setChecklistAction(type);
		 */
	}

	public void fulfilmentChange(AjaxBehaviorEvent event) {
		try {
			String type = (String) event.getComponent().getAttributes()
					.get("value");
			this.getManagedBean().setFulfilmentType(type);
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
			/*
			 * if (type != null) { for (PmParameterDesc paramDesc :
			 * localParamDesc) { if (paramDesc.getPmParameter().getParameterId()
			 * .equals(Long.valueOf(type))) {
			 * 
			 * this.getManagedBean().setFulfilmentType(
			 * paramDesc.getCodeName()); } } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void procurementChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");

			this.getManagedBean().getScQt()
					.setFulfilmentType(String.valueOf(type));
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setProcuremntCategory(
							paramDesc.getPmParameter().getParameterCode());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getSupplierCriteria().setAllState(state);
	}

	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setStandardTemplate(template);
	}

	public void selectLocation(AjaxBehaviorEvent event) {
		Integer location = (Integer) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQt().setZoneLocationLevel(location);
	}

	public String technicalSpecEditAction(ScQtCheckList checklist) {

		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			this.getManagedBean().setScqtDoc(checklist.getScQtDoc());

			QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();

			try {

				docReqDTO.setCheckListId(checklist.getQtChecklistId());
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDto = qtTemplateSO
						.retriveTemplateDocDetailsByCheckListId(docReqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(resDto.getScQtCheckList()
					.getScQtDoc().getTplSpecMaster());

			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			this.getManagedBean()
					.getScqtDoc()
					.setTplSpecMaster(
							resDto.getScQtCheckList().getScQtDoc()
									.getTplSpecMaster());

			this.getManagedBean().setScqtDoc(
					resDto.getScQtCheckList().getScQtDoc());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";

	}

	public String createCommitteeSpecification() {
		this.getManagedBean().setScqtDoc(new ScQtDoc());
		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";
	}

	public String financialSpecEditAction(ScQtCheckList checklist) {

		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			this.getManagedBean().setScqtDoc(checklist.getScQtDoc());

			QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();

			try {

				docReqDTO.setCheckListId(checklist.getQtChecklistId());
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDto = qtTemplateSO
						.retriveTemplateDocDetailsByCheckListId(docReqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(resDto.getScQtCheckList()
					.getScQtDoc().getTplSpecMaster());

			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			this.getManagedBean()
					.getScqtDoc()
					.setTplSpecMaster(
							resDto.getScQtCheckList().getScQtDoc()
									.getTplSpecMaster());

			this.getManagedBean().setScqtDoc(
					resDto.getScQtCheckList().getScQtDoc());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}
		return "/xhtml/sc/main/financialTemplate.xhtml?faces-redirect=true";
	}

	public String committeeSpecEditAction(ScQtCheckList checklist) {

		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			this.getManagedBean().setScqtDoc(checklist.getScQtDoc());

			QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();

			try {

				docReqDTO.setCheckListId(checklist.getQtChecklistId());
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				resDto = qtTemplateSO
						.retriveTemplateDocDetailsByCheckListId(docReqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(resDto.getScQtCheckList()
					.getScQtDoc().getTplSpecMaster());

			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			this.getManagedBean()
					.getScqtDoc()
					.setTplSpecMaster(
							resDto.getScQtCheckList().getScQtDoc()
									.getTplSpecMaster());

			this.getManagedBean().setScqtDoc(
					resDto.getScQtCheckList().getScQtDoc());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}
		return "/xhtml/sc/main/financialCommitteeTemplate.xhtml?faces-redirect=true";
	}

	public String cloneAction(ScQtDoc scqtDoc) {
		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			if (this.getManagedBean().getScqtDoc() == null) {

				this.getManagedBean().setScqtDoc(new ScQtDoc());

			}
			if (this.getManagedBean().getScqtDoc().getScQt() == null) {
				this.getManagedBean().getScqtDoc().setScQt(new ScQt());
			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster() == null) {

				this.getManagedBean().getScqtDoc()
						.setTplSpecMaster(new TplSpecMaster());

			}

			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmParameter());

			}

			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getStatus() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setStatus(new PmStatus());

			}

			if (this.getManagedBean().getScqtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getScqtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			/*
			 * scqtDoc.getTplSpecMaster() .getSpecMasterId()
			 */
			specMaster1.setSpecMasterId(new Long(scqtDoc.getTplSpecMaster()
					.getSpecMasterId()));
			// specMaster.setSpecMasterId(1L);
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.setSpecName(scqtDoc.getTplSpecMaster().getSpecName());

			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getProcurementCategory()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getProcurementCategory()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getItemTypeCode() .getParameterId()
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getItemTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getItemTypeCode()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getSpecTypeCode() .getParameterId(
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getSpecTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getSpecTypeCode()
									.getParameterId());
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getStatus()
					.setStatusId(
							scqtDoc.getTplSpecMaster().getStatus()
									.getStatusId());
			this.getManagedBean().getScqtDoc().getScQt()
					.setQtNo(scqtDoc.getScQt().getQtNo());

			qtTemplateReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			qtTemplateReqDTO.setDocType("1");
			// qtTemplateReqDTO.set(scqtDoc.getTplSpecMaster());
			// qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
			// .getSpecMasterId());

			try {
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				// resDto = qtTemplateSO
				// .retriveTemplateDocDetails(qtTemplateReqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";
	}

	public String cloneCommitteeAction(ScQtDoc scqtDoc) {
		IQTTemplateSO qtTemplateSO;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			QTTemplateDocResDTO resDto = null;

			if (this.getManagedBean().getScqtDoc() == null) {

				this.getManagedBean().setScqtDoc(new ScQtDoc());

			}
			if (this.getManagedBean().getScqtDoc().getScQt() == null) {
				this.getManagedBean().getScqtDoc().setScQt(new ScQt());
			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster() == null) {

				this.getManagedBean().getScqtDoc()
						.setTplSpecMaster(new TplSpecMaster());

			}

			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getProcurementCategory() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setProcurementCategory(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getItemTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setItemTypeCode(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getSpecTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setSpecTypeCode(new PmParameter());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getOrgProfile() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setOrgProfile(new PmOrgProfile());

			}
			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getTemplateTypeCode() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setTemplateTypeCode(new PmParameter());

			}

			if (this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.getStatus() == null) {

				this.getManagedBean().getScqtDoc().getTplSpecMaster()
						.setStatus(new PmStatus());

			}

			if (this.getManagedBean().getScqtDoc().getScQtCheckList() == null) {
				this.getManagedBean().getScqtDoc()
						.setScQtCheckList(new ScQtCheckList());
			}
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			/*
			 * scqtDoc.getTplSpecMaster() .getSpecMasterId()
			 */
			specMaster1.setSpecMasterId(new Long(scqtDoc.getTplSpecMaster()
					.getSpecMasterId()));
			// specMaster.setSpecMasterId(1L);
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			this.getManagedBean().getScqtDoc().getTplSpecMaster()
					.setSpecName(scqtDoc.getTplSpecMaster().getSpecName());

			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getProcurementCategory()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getProcurementCategory()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getItemTypeCode() .getParameterId()
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getItemTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getItemTypeCode()
									.getParameterId());
			/*
			 * scqtDoc.getTplSpecMaster().getSpecTypeCode() .getParameterId(
			 */
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getSpecTypeCode()
					.setParameterId(
							scqtDoc.getTplSpecMaster().getSpecTypeCode()
									.getParameterId());
			this.getManagedBean()
					.getScqtDoc()
					.getTplSpecMaster()
					.getStatus()
					.setStatusId(
							scqtDoc.getTplSpecMaster().getStatus()
									.getStatusId());
			this.getManagedBean().getScqtDoc().getScQt()
					.setQtNo(scqtDoc.getScQt().getQtNo());

			qtTemplateReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			qtTemplateReqDTO.setDocType("1");
			// qtTemplateReqDTO.set(scqtDoc.getTplSpecMaster());
			// qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
			// .getSpecMasterId());

			try {
				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);

				// resDto = qtTemplateSO
				// .retriveTemplateDocDetails(qtTemplateReqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/technicalCommitteeTemplate.xhtml?faces-redirect=true";
	}

	

	public List<SelectItem> getProcurementDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ProcurementReqDTO reqDTO = new ProcurementReqDTO();
		ProcurementResDTO resDto = null;
		try {

			IProcurementSO procurementSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IProcurementSO.class);
			resDto = procurementSO.retriveProcurementList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getProcurementList().size(); i++) {
				PmProcurementCategory pmProcurement = (PmProcurementCategory) resDto
						.getProcurementList().get(i);
				selectItems.add(new SelectItem(pmProcurement.getCategoryCode(),
						pmProcurement.getCategoryName()));
			}
			this.getManagedBean().setProcurementListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getProcurementListDetails();
	}

	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		this.getManagedBean().setTaskId(taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCSpecificationDataType specificationDataType = (SCSpecificationDataType) obj;

		this.getManagedBean().setSpecificationDataType(specificationDataType);

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setQtId(new Long(specificationDataType.getQtId())
				.longValue());
		QuationTenderResDTO tenderResDTO = detailSO
				.retriveQTDetails(tenderReqDTO);
		QTDetailBackingBean detailBackingBean = new QTDetailBackingBean();
		detailBackingBean.getManagedBean().setQt(tenderResDTO.getQtDetail());
		detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		// detailBackingBean.populateParentOrgProfile(tenderResDTO.getQtDetail());
		retriveSpecdetails(specificationDataType.getQtId().longValue());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(
						fc,
						null,
						"/xhtml/ss/specification/specificationview.xhtml"
								+ "?faces-redirect=true");
	}

	public String submitSpecDetails(String outCome) {

		QuationTenderReqDTO specReqDTO = prepareSpecReq();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		specReqDTO.setToken(ctxToken);

		specReqDTO.setTaskId(this.getManagedBean().getTaskId());

		// specReqDTO.setScSpecificationDataType(this.getManagedBean()
		// .getSpecificationDataType());
		specReqDTO.setOutcome(outCome);
		IQTSpecSO iqtSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		QuationTenderResDTO resDTO = new QuationTenderResDTO();
		try {
			// SpecificationResDTO resDTO =
			// iqtSpecSO.updateBPMAction(specReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {

			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		}
		return null;
	}

	public String updateBpmTask(String action) {
		QuationTenderReqDTO spReqDTO = prepareSpecReq();
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();
		QuationTenderResDTO resDTO = new QuationTenderResDTO();
		try {
			// QuationTenderResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process ", " "

					));
		}
		return null;

	}

	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}

	public void updateContractZoneLocation() {

		if (getManagedBean().getCtZoneTreeSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getCtZoneTreeSelectedNodes()) {
				List<TreeNode> localList = new ArrayList<TreeNode>();
				/*
				 * List<TreeNode> supplierList=
				 * this.getManagedBean().getCtZoneSuppliers();
				 * if(supplierList==null){ supplierList=new
				 * ArrayList<TreeNode>(); }
				 */

				TreeNode treelocal = consturctTreeNode(getManagedBean()
						.getCtZoneTreeSelectedNodes());

				localList.add(treelocal);
				this.getManagedBean().setCtZoneSuppliers(treelocal);
			}
		}
	}

	public TreeNode consturctTreeNode(TreeNode[] selectedNode) {
		Set<TreeNode> parentNodes = new HashSet<>();
		for (TreeNode treeNode : selectedNode) {

			if ("zone"
					.equalsIgnoreCase((treeNode.getParent() != null) ? treeNode
							.getParent().getType() : null)) {
				parentNodes.add(treeNode.getParent());

			}
		}

		TreeNode root = new DefaultTreeNode("Root", null);
		for (TreeNode node : parentNodes) {
			TreeNode zoneNode = new DefaultTreeNode(node.getType(),
					node.getData(), root);
			if (node.getChildCount() > 0) {
				for (TreeNode stateNode : node.getChildren()) {

					if (stateNode.isSelected()) {
						new DefaultTreeNode(stateNode.getType(),
								stateNode.getData(), zoneNode);
					}
				}

			}

		}

		return root;

	}

	public void retriveZoneBySupplier(NodeSelectEvent event) {
		/*
		 * //CtCircularZone circularZone = (CtCircularZone) event.getTreeNode()
		 * .getData(); PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		 * System.out.println("====ctzone id===" +
		 * circularZone.getCircularZoneId()); reqDTO.setCircularZoneId(1L);
		 * IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO)
		 * BusinessDelegate .getInstance().getServiceObject(
		 * ModuleCodeConst.CONTRACT_MANAGEMENT, IPanelSupplierSO.class);
		 * PanelSupplierResDTO resDTO = panelSupplierSO
		 * .retrieveSupplierByZone(reqDTO); if (resDTO.getPanelSuppliers() !=
		 * null) { System.out.println(resDTO.getPanelSuppliers());
		 * this.getManagedBean().setTotalSuppliers(
		 * resDTO.getPanelSuppliers().size());
		 * this.getManagedBean().setCtZoneSupplierList(
		 * resDTO.getPanelSuppliers()); }
		 */

	}

	public void circularListSelect(SelectEvent selectEvent) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			CtCircularVer circular = (CtCircularVer) selectEvent.getObject();

			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject("ct",
							ICircularDetailSO.class);

			reqDTO.setCircularVerId(circular.getCircularVerId());
			CircularDetailResDTO resDTO = circularDetailSO
					.getPanelItemsByCircularVerId(reqDTO);
			for (CtPanelItem panelItem : resDTO.getPanelItemList()) {
			}
			this.getManagedBean().setCircularExpDate(
					(circular.getEndDate().toString()));
			this.getManagedBean().setCirCularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			/*
			 * this.getManagedBean().setCircularType(
			 * circular.getItemCategory().getPmParameterDescs().get(0)
			 * .getCodeName());
			 */
			this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());
			/*
			 * this.getManagedBean().setCircularType(
			 * circular.getCtCircular().getCircularType());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void panelListSelect(SelectEvent selectEvent) {
		CtPanelItem ctPanelItem = (CtPanelItem) selectEvent.getObject();
		List<CtPanelItem> panelList = this.getManagedBean()
				.getSelectpanelItem();
		if (panelList == null) {
			panelList = new ArrayList<CtPanelItem>();
		}
		panelList.add(ctPanelItem);
		this.getManagedBean().setSelectpanelItem(panelList);

	}

	public void panelPopulate() {
		try {
			this.getManagedBean().getPanelItemList();
			List<CtPanelItem> detailsList = this.getManagedBean()
					.getPanelItemDetailsList();
			if (detailsList == null) {
				detailsList = new ArrayList<CtPanelItem>();
			}
			for (CtPanelItem panelItem : this.getManagedBean()
					.getPanelItemList()) {
				detailsList.add(panelItem);
			}
			this.getManagedBean().setPanelItemDetailsList(detailsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the searchCircular
	 */
	public IPagingDataModel<CtCircularVer> getSearchCircular() {
		return searchCircular;
	}

	/**
	 * @param searchCircular
	 *            the searchCircular to set
	 */
	public void setSearchCircular(IPagingDataModel<CtCircularVer> searchCircular) {
		this.searchCircular = searchCircular;
	}

	public void searchCircularList() {
		searchCircular.reloadData();
	}

	private class SearchCircularPagingDataModel extends
			PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public SearchCircularPagingDataModel() {

			super("circularList");

		}

		public ISearchResultBean<CtCircularVer> pageChangeListener(
				ISearchBean<CtCircularVer> searchBean) {
			ICircularSO circularSO;
			CircularResDTO resDto;

			try {
				CircularReqDTO circularReqDTO = new CircularReqDTO();

				if (getManagedBean().getCirCularNo() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCirCularNo())) {
					SearchFilterBean circularBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					circularBean.setSearchField("circularNo");
					circularBean.setSearchValue(getManagedBean()
							.getCirCularNo());
					searchBean.getDataFilterBeans().add(circularBean);

				}

				if (getManagedBean().getCircularName() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCircularName())) {
					SearchFilterBean circularBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					circularBean.setSearchField("name");
					circularBean.setSearchValue(getManagedBean()
							.getCircularName());
					searchBean.getDataFilterBeans().add(circularBean);
				}

				if (getManagedBean().getItemName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getItemName())) {
					SearchFilterBean circularBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					circularBean.setSearchField("ctPanelItems.itemName");
					circularBean.setSearchValue(getManagedBean().getItemName());
					searchBean.getDataFilterBeans().add(circularBean);
				}

				circularReqDTO.setCircularVerSearchBean(searchBean);

				circularSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("ct", ICircularSO.class);
				resDto = circularSO.searchCircularVer(circularReqDTO);
				/*
				 * List<CtCircularVer>
				 * circularVer=resDto.getCircularVerResultBean
				 * ().getResultList(); for(CtCircularVer ver:circularVer){
				 * System
				 * .out.println("=====panel type===="+ver.getItemCategory()
				 * .getParameterId());
				 * //System.out.println("=====panel type===="
				 * +ver.getItemCategory
				 * ().getPmParameterDescs().get(0).getCodeName()); }
				 */
				if (resDto.isSuccessful()) {
					// System.out.println("circular and panel type"+resDto.getCircularVerResultBean().getResultList());
					return resDto.getCircularVerResultBean();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				circularSO = null;
			}
			return null;

		}
	}

	public void searchScqtDocAction() {
		templateSpecCreate.reloadData();
	}

	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<ScQtDoc> {
		private static final long serialVersionUID = 1L;

		public CreateTemplateSpecPagingDataModel() {

			super("createList");

		}

		public ISearchResultBean<ScQtDoc> pageChangeListener(
				ISearchBean<ScQtDoc> searchBean) {
			IQTTemplateSO qtTemplateSO;
			QTTemplateDocResDTO resDto;

			try {
				QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

				if (getManagedBean().getSpecName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					SearchFilterBean specSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					specSearchBean.setSearchField("tplSpecMaster.specName");
					specSearchBean.setSearchValue(getManagedBean()
							.getSpecName());
					searchBean.getDataFilterBeans().add(specSearchBean);

				}

				if (getManagedBean().getStandardTemplate() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {

					SearchFilterBean standardSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					standardSearchBean
							.setSearchField("tplSpecMaster.processType");
					standardSearchBean.setSearchValue(getManagedBean()
							.getStandardTemplate());
					searchBean.getDataFilterBeans().add(standardSearchBean);
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					itemSearchBean
							.setSearchField("tplSpecMaster.itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (getManagedBean().getSearchProcurementId() != null) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					procurementSearchBean
							.setSearchField("tplSpecMaster.procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}
				if (getManagedBean().getQtNum() != null
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("scQt.qtNo");
					qtnoSearchBean.setSearchValue(getManagedBean().getQtNum());

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}

				qtTemplateReqDTO.setTemplateSpecSearchBean(searchBean);

				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTTemplateSO.class);
				resDto = qtTemplateSO
						.retrieveSpecTemplateByType(qtTemplateReqDTO);

				if (resDto != null) {
					return resDto.getTemplateSpecList();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * @return the templateSpecCreate
	 */
	public IPagingDataModel<ScQtDoc> getTemplateSpecCreate() {
		return templateSpecCreate;
	}

	/**
	 * @param templateSpecCreate
	 *            the templateSpecCreate to set
	 */
	public void setTemplateSpecCreate(
			IPagingDataModel<ScQtDoc> templateSpecCreate) {
		this.templateSpecCreate = templateSpecCreate;
	}

}
