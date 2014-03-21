/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.checkList.ICheckListSO;
import my.ep.biz.service.pm.common.currency.ICurrencySO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.ct.supplier.PanelSupplierReqDTO;
import my.ep.dto.ct.supplier.PanelSupplierResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.checkList.CheckListReqDTO;
import my.ep.dto.pm.common.checkList.CheckListResDTO;
import my.ep.dto.pm.common.currency.CurrencyReqDTO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
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
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularZone;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularZoneDtl;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCheckListCategory;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScQuote;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZone;
import my.ep.persistence.entity.sc.directpurchase.ScQuoteZoneLocation;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtFreqSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtProgessivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.directpurchase.LocationDTO;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.specification.SpecificationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationBackingBean")
@ViewScoped
public class SpecificationBackingBean extends
		SourcingBaseBackingBean<SpecificationManagedBean> {
	private IPagingDataModel<CtCircularVer> searchCircular = new SearchCircularPagingDataModel();
	private IPagingDataModel<ScQtDoc> templateSpecCreate = new CreateTemplateSpecPagingDataModel();

	// private List<SpecificationCompilanceBean> selectedCompilenceList;
	private int count;

	private String selctValue;

	public SpecificationBackingBean() {
		FacesScopes scope = new FacesScopes();
		this.getManagedBean().setTitle(
				scope.getSessionScope("title").toString());
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private List<ScQuoteZone> getQuoteZones(List<TreeNode> treeNodes,
			ScQuote quote) {
		List<ScQuoteZone> results = new ArrayList<ScQuoteZone>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode childNode : treeNode.getChildren()) {
				LocationDTO locationDTO = (LocationDTO) childNode.getData();
				if (locationDTO.getZone() != null) {
					ScQuoteZone zone = locationDTO.getZone();
					List<PmState> pmStates = getPmStates(treeNode.getChildren());
					System.out.println("======pmStates====" + pmStates.size());
					List<ScQuoteZoneLocation> zoneStateLocations = new ArrayList<ScQuoteZoneLocation>();
					if (pmStates != null && pmStates.size() > 0) {
						for (PmState pmState : pmStates) {
							ScQuoteZoneLocation zoneStateLocation = new ScQuoteZoneLocation();
							zoneStateLocation.setZone(zone);
							zoneStateLocation.setPmState(pmState);
							zoneStateLocations.add(zoneStateLocation);
						}
					}
					List<ScQuoteZoneLocation> zoneDivisionLocation = new ArrayList<ScQuoteZoneLocation>();
					List<PmDivision> pmDivisions = getPmDivisions(treeNode
							.getChildren());
					System.out.println("======pmDivisions===="
							+ pmDivisions.size());
					if (pmDivisions != null && pmDivisions.size() > 0) {
						for (PmDivision pmDivision : pmDivisions) {
							ScQuoteZoneLocation zoneStateLocation = new ScQuoteZoneLocation();
							zoneStateLocation.setZone(zone);
							zoneStateLocation.setDivision(pmDivision);
							zoneDivisionLocation.add(zoneStateLocation);
						}
					}
					List<ScQuoteZoneLocation> zoneDistrictLocation = new ArrayList<ScQuoteZoneLocation>();
					List<PmDistrict> pmDistricts = getPmDistricts(treeNode
							.getChildren());
					System.out.println("======pmDistricts===="
							+ pmDistricts.size());
					if (pmDivisions != null && pmDivisions.size() > 0) {
						for (PmDistrict pmDistrict : pmDistricts) {
							ScQuoteZoneLocation zoneStateLocation = new ScQuoteZoneLocation();
							zoneStateLocation.setZone(zone);
							zoneStateLocation.setPmDistrict(pmDistrict);
							zoneDistrictLocation.add(zoneStateLocation);
						}
					}
					zone.setZoneLocations(zoneStateLocations);
					zone.getZoneLocations().addAll(zoneDivisionLocation);
					zone.getZoneLocations().addAll(zoneDistrictLocation);
					zone.setQuote(quote);
					results.add(zone);
				}
			}
		}
		return results;
	}

	private List<PmState> getPmStates(List<TreeNode> treeNodes) {
		List<PmState> results = new ArrayList<PmState>();
		for (TreeNode treeNode : treeNodes) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (treeNode.getChildren() != null) {
					LocationDTO locationDTO = (LocationDTO) stateNode.getData();
					if (locationDTO.getState() != null) {
						results.add(locationDTO.getState());
					}
				}
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
						LocationDTO locationDTO = (LocationDTO) divNode
								.getData();
						if (locationDTO.getDivision() != null) {
							results.add(locationDTO.getDivision());
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
				LocationDTO locationDTO = (LocationDTO) treeNode.getData();
				if (locationDTO.getDistrict() != null) {
					results.add(locationDTO.getDistrict());
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
		IQTSpecSO specSO;
		try {
			SpecificationReqDTO reqDTO = prepareSpecReq();
			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
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
			specSO = null;
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
			System.out.println("==enter pcr");
			return "specificationCreation.xhtml?faces-redirect=true";
			// return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}

		if (this.getManagedBean().getScQt().isPanel() == true
				&& this.getManagedBean().getCircularType()
						.equalsIgnoreCase("Central Contract")) {
			System.out.println("==enter CCR");
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

	public String committeeBackAction() {

		return "specificationCommitteeView.xhtml?faces-redirect=true";

	}

	public String retriveTemplatespec() {
		return "/xhtml/sc/main/createTemplate.xhtml?faces-redirect=true";
	}

	public void cateGoryUpdate(BaseEntity entity) {
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getSuppCriteriaopenList();

		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setSuppCriteriaopenList(qtCategoryList);
		}
		PmCategoryL3 category = (PmCategoryL3) entity;

		ScQtCategory category1 = new ScQtCategory();
		if (category1.getPmCategoryL3() == null) {
			category1.setPmCategoryL3(new PmCategoryL3());

		}
		if (category1.getPmCategoryL2() == null) {
			category1.setPmCategoryL2(new PmCategoryL2());

		}
		if (category1.getPmCategoryL1() == null) {
			category1.setPmCategoryL1(new PmCategoryL1());

		}
		category1.getPmCategoryL3().setCategoryL3Code(
				category.getCategoryL3Code());
		category1.getPmCategoryL2().setCategoryL2Code(
				category.getPmCategoryL2().getCategoryL2Code());
		category1.getPmCategoryL1().setCategoryL1Code(
				category.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code());
		// category1.getPmCategoryL3().setC
		qtCategoryList.add(category1);
	}

	/**
	 * To add empty row to category open code data table
	 */
	public void addNewCategoryAction() {
		// debugLogger("addNewCategoryAction");
		System.out.println("======enter new category method action===");
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

				checkList.setDocType("3");
				checkList.setChecklistType("TS");
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(specMaster.getSpecName());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(tenderResDTO.getQtDetail());
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

				checkList.setDocType("1");
				checkList.setChecklistType("TS");
				checkList.setChecklistSeq(1);
				checkList.setRecordStatus(this.getManagedBean().getScqtDoc()
						.isRecordStatus());
				checkList.setManditory(true);
				checkList.setTitle(specMaster.getSpecName());
				checkList.setScQtCheckListAttachment(null);
				checkList.setChangedBy(10l);
				checkList.setCreatedBy(1l);
				checkList.setScQt(tenderResDTO.getQtDetail());
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

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();

		if (tabTitle.equalsIgnoreCase("General")) {

		} else if (tabTitle.equalsIgnoreCase("Supplier Qualification Criteria")) {

		} else if (tabTitle.equalsIgnoreCase("Technical Checklist")) {

		} else if (tabTitle.equalsIgnoreCase("Financial Checklist")) {

		} else if (tabTitle.equalsIgnoreCase("Technical Scoring")) {
			retriveTechnicalScoring();
		} else if (tabTitle.equalsIgnoreCase("Financial Scoring")) {
			retriveFinancialScoring();
		} else if (tabTitle.equalsIgnoreCase("Fulfilment Schedule")) {

		} else if (tabTitle.equalsIgnoreCase("Committee Endorsment")) {

		}

	}

	public void businessOwnerTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if (tabTitle.equalsIgnoreCase("General")) {

		} else if (tabTitle.equalsIgnoreCase("Supplier Qualification Criteria")) {

		} else if (tabTitle.equalsIgnoreCase("Technical Specification")) {
			retriveTechnicalSpecification();

		} else if (tabTitle.equalsIgnoreCase("Financial Specification")) {
			retriveFinancialSpecification();
		}
	}

	public void retriveTechnicalSpecification() {
		IQTTemplateSO qtTemplateSO;
		QTTemplateDocResDTO resDto;

		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			qtTemplateReqDTO.setQtId(146L);
			qtTemplateReqDTO.setDocType("1");
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
			resDto = qtTemplateSO.retriveTemplatesByType(qtTemplateReqDTO);
			if (resDto != null) {
				List<ScQtCheckList> techSpecList = this.getManagedBean()
						.getTechnicalSpecList();
				if (techSpecList == null) {
					techSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getCheckListResultBean().getResultList().size() != techSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto
							.getCheckListResultBean().getResultList()) {
						/*checklist.setScQtSpec(this.getManagedBean()
								.getScQtSpec());*/
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
		IQTTemplateSO qtTemplateSO;
		QTTemplateDocResDTO resDto;

		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
			qtTemplateReqDTO.setQtId(146L);
			qtTemplateReqDTO.setDocType("3");
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
			resDto = qtTemplateSO.retriveTemplatesByType(qtTemplateReqDTO);

			if (resDto != null) {
				List<ScQtCheckList> financialSpecList = this.getManagedBean()
						.getFinancialSpecList();
				if (financialSpecList == null) {
					financialSpecList = new ArrayList<ScQtCheckList>();
				}
				List<ScQtCheckList> localList = new ArrayList<ScQtCheckList>();
				if (resDto.getCheckListResultBean().getResultList().size() != financialSpecList
						.size()) {
					for (ScQtCheckList checklist : resDto
							.getCheckListResultBean().getResultList()) {
						/*checklist.setScQtSpec(this.getManagedBean()
								.getScQtSpec());*/
						localList.add(checklist);
					}
					this.getManagedBean().setFinancialSpecList(localList);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void retriveTechnicalScoring() {
		List<ScQtCheckList> checklist = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (checklist == null) {
			checklist = new ArrayList<ScQtCheckList>();
		}

		List<ScQtCheckList> technicalScoring = this.getManagedBean()
				.getTechnicalScoring();
		if (technicalScoring == null) {
			technicalScoring = new ArrayList<ScQtCheckList>();
		}

		if (checklist.size() != technicalScoring.size()) {
			System.out.println("====enter not null loop===");
			List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
			int score = 0;
			for (ScQtCheckList list : checklist) {
				float addScore = 0;
				if (list.getScQtCheckListScores() != null) {

					for (ScQtCheckListScore checklistScore : list
							.getScQtCheckListScores()) {
						addScore = addScore + checklistScore.getScore();
						System.out.println(" add score====" + addScore);

						list.setMaxScore(new BigDecimal(addScore));
						// int local;
						int local = (int) addScore;
						score = score + local;
						this.getManagedBean().setTechPoints(score);
					}
					//checklist.remove(list);
					checklist.add(list);
					// localChecklist.add(list);

				}

			}
			this.getManagedBean().setTechnicalScoring(checklist);
		}
		/*
		 * else if(checklist.size()==technicalScoring.size()){
		 * System.out.println("====equal===");
		 * this.getManagedBean().setTechnicalScoring(checklist); }
		 */
	}

	public void retriveFinancialScoring() {
		List<ScQtCheckList> checklist = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (checklist == null) {
			checklist = new ArrayList<ScQtCheckList>();
		}

		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();
		if (financialScoring == null) {
			financialScoring = new ArrayList<ScQtCheckList>();
		}

		if (checklist.size() != financialScoring.size()) {
			List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
			int score = 0;
			for (ScQtCheckList list : checklist) {
				float addScore = 0;
				if (list.getScQtCheckListScores() != null) {

					for (ScQtCheckListScore checklistScore : list
							.getScQtCheckListScores()) {
						addScore = addScore + checklistScore.getScore();

						list.setMaxScore(new BigDecimal(addScore));
						int local = (int) addScore;
						score = score + local;
						this.getManagedBean().setFinPoints(score);
					}
					checklist.remove(list);
					checklist.add(list);
					// localChecklist.add(list);
				}

			}
			this.getManagedBean().setFinancialScoring(checklist);
		}
		/*
		 * else if(checklist.size()==financialScoring.size()){
		 * 
		 * this.getManagedBean().setFinancialScoring(checklist); }
		 */
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	private List<LocationDTO> getLocations(TreeNode zoneLocation) {
		List<LocationDTO> results = new ArrayList<LocationDTO>();
		if (zoneLocation.isLeaf()) {
			LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getLocations(childrendLocationNode));
			}
		}
		return results;
	}

	private ScQuoteZone getZone(TreeNode zoneLocation) {
		ScQuoteZone result = null;
		if (zoneLocation.getData() instanceof LocationDTO) {
			LocationDTO locationDTO = (LocationDTO) zoneLocation.getData();
			if (locationDTO.getZone() != null) {
				result = locationDTO.getZone();
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
		List<LocationDTO> locationDTOs = getLocations(rootZoneLocation);

		ScQuoteZone zone = getZone(rootZoneLocation);
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
		/*
		 * getManagedBean().getZoneTrees().remove(
		 * LocationSingleton.getInstance().buildZoneTree(zone, states,
		 * districts, divisions, cities));
		 */
		getManagedBean().setZoneName(zone.getZoneName());
		/*
		 * if (zone != null) { getManagedBean().setZoneName(zone.getZoneName());
		 * }
		 */
	}

	public void showZoneLocationPopup() {

		Integer locationLevel =(Integer) this.getManagedBean().getScQt().getZoneLocationLevel();
				
		if (locationLevel == null) {
			locationLevel = 3;
		}
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationZoneTree(1L, null,
						null, null, null, locationLevel));
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
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<LocationDTO> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}

		ScQuoteZone zone = new ScQuoteZone();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);
		if (this.getManagedBean().getScQt().isZonal() == true
				&& this.getManagedBean().getSupplierCriteria().isAllState() == false) {

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
						LocationSingleton.getInstance().buildZoneTree(zone,
								states, districts, divisions, cities));
			}
		}

		if (this.getManagedBean().getScQt().isZonal() == false
				&& this.getManagedBean().getSupplierCriteria().isAllState() == false) {

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
						LocationSingleton.getInstance().buildZoneTree(null,
								states, districts, divisions, cities));
			}
		}

		if (this.getManagedBean().getScQt().isZonal() == true
				&& this.getManagedBean().getSupplierCriteria().isAllState() == true) {

			List<PmState> states = new ArrayList<PmState>();

			List<PmDivision> divisions = new ArrayList<PmDivision>();
			List<PmDistrict> district = new ArrayList<PmDistrict>();
			List<PmCity> cities = new ArrayList<PmCity>();
			System.out.println("===========true true Zone name==="
					+ zone.getZoneName());
			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZonesList().add(
						LocationSingleton.getInstance().buildZoneTree(zone,
								states, district, divisions, cities));
			}
		}

		if (getManagedBean().getIndex() >= 0) {
			System.out.println("====enter greate o loop====");
			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTOs(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTOs(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTOs(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTOs(locationDTOs);

			getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);
		/*
		 * getManagedBean().setRootLocationTree(
		 * LocationSingleton.getInstance().buildLocationTree(1L, null, null,
		 * null, null));
		 */

		// System.out.println("Zone Tree : " +
		// getManagedBean().getZoneTrees().get(0).getData());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public void updateZoneGeneralLocation() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {
				if (!treeNode.getParent().isSelected()) {
					List<LocationDTO> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}
		System.out.println("==managed bean zone name==="
				+ getManagedBean().getZoneName());

		ScQuoteZone zone = new ScQuoteZone();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTOs(locationDTOs);
		System.out.println("===stae===" + states.size());
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTOs(locationDTOs);
		System.out.println("===divisions===" + divisions.size());
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTOs(locationDTOs);
		System.out.println("===districts===" + districts.size());
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTOs(locationDTOs);

		if (getManagedBean().getIndex() == -1) {
			System.out.println("====enter -1 loop" + zone.getZoneName());
			getManagedBean().getZoneGeneralTrees().add(
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));

		}

		else if (getManagedBean().getIndex() >= 0) {
			System.out.println("====enter 0 loop");
			getManagedBean().getZoneGeneralTrees().remove(
					getManagedBean().getIndex());
			getManagedBean().getZoneGeneralTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildZoneTree(zone, states,
							districts, divisions, cities));
		}
		this.getManagedBean().setIndex(-1);
		/*
		 * getManagedBean().setRootLocationTree(
		 * LocationSingleton.getInstance().buildLocationTree(1L, null, null,
		 * null, null));
		 */

		// System.out.println("Zone Tree : " +
		// getManagedBean().getZoneTrees().get(0).getData());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public void updateZoneLocationList() {
		List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (getManagedBean().getSelectedLocNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedLocNodes()) {
				if (!treeNode.getParent().isSelected()) {
					LocationDTO locationDTO = (LocationDTO) treeNode.getData();
					locationDTOs.add(locationDTO);
				}
			}
		}

		ScQuoteZone zone = new ScQuoteZone();
		System.out.println("zone name : " + getManagedBean().getZoneName1());
		zone.setZoneName(getManagedBean().getZoneName1());
		getManagedBean().setZoneName(null);
		System.out.println("Zone Name : " + zone.getZoneName());

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
		System.out.println("Zone Tree : "
				+ getManagedBean().getZoneTrees().size());
		// System.out.println("Zone Name : " + getManagedBean().getZoneName());
	}

	public void calculateFromAmount() {

		int amount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue()
				* this.getManagedBean().getFromJulat() / 100;
		int totalamount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue()
				+ amount;
		this.getManagedBean().setFromPrice(new BigDecimal(totalamount));
	}

	public void calculateToAmount() {

		int amount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue()
				* this.getManagedBean().getToJulat() / 100;
		int totalamount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue()
				+ amount;
		this.getManagedBean().setToPrice(new BigDecimal(totalamount));
		List<ScQtCheckListScore> scoreList = this.getManagedBean()
				.getPackScoringList();
		if (scoreList == null) {
			scoreList = new ArrayList<ScQtCheckListScore>();
		}
		ScQtCheckListScore score = new ScQtCheckListScore();
		score.setRangeFrom(this.getManagedBean().getFromPrice().floatValue());
		score.setRangeTo(this.getManagedBean().getToPrice().floatValue());
		score.setPercentFrom(new BigDecimal(this.getManagedBean()
				.getFromJulat()));
		score.setPercentTo(new BigDecimal(this.getManagedBean().getToJulat()));
		scoreList.add(score);
		if (scoreList.size() < 2) {
			System.out.println("====score list size===" + scoreList.size());
			this.getManagedBean().setPackScoringList(scoreList);
		}
	}

	public void calculatePackageFromPercent(ScQtCheckListScore score, int index) {

		int amount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue();

		List<ScQtCheckListScore> packFromScoring = this.getManagedBean()
				.getPackScoringList();
		if (packFromScoring == null) {
			packFromScoring = new ArrayList<ScQtCheckListScore>();
		}
		float diff = 0;
		float local = score.getRangeFrom().intValue();
		diff = local - amount;
		diff = diff / 100;
		score.setPercentFrom(new BigDecimal(diff));
		packFromScoring.add(score);
		getManagedBean().getPackScoringList().remove(index);
		this.getManagedBean().setPackScoringList(packFromScoring);

	}

	public void calculatePackageToPercent(ScQtCheckListScore score, int index) {

		int amount = this.getManagedBean().getScQt().getEstimatedAmount()
				.intValue();

		List<ScQtCheckListScore> packFromScoring = this.getManagedBean()
				.getPackScoringList();
		if (packFromScoring == null) {
			packFromScoring = new ArrayList<ScQtCheckListScore>();
		}
		float diff = 0;
		float local = score.getRangeTo().intValue();
		diff = local - amount;
		diff = diff / 100;
		score.setPercentTo(new BigDecimal(diff));
		packFromScoring.add(score);
		getManagedBean().getPackScoringList().remove(index);
		this.getManagedBean().setPackScoringList(packFromScoring);

	}

	public SpecificationReqDTO prepareSpecReq() {
		SpecificationReqDTO specreqDTO = new SpecificationReqDTO();

		//ScQtSpec scqtSpec = this.getManagedBean().getScQtSpec();
		ScQt scQt = this.getManagedBean().getScQt();
		/*
		 * scqtSpec.setApprovalDate(new Date()); PmParameter parameter =
		 * this.getManagedBean().getScQtSpec() .getPmProcurementCategory();
		 * parameter.setParameterId( this.getManagedBean().getScQtSpec()
		 * .getPmProcurementCategory().getParameterId());
		 * scqtSpec.setPmProcurementCategory(parameter);
		 */
		// scqtSpec.setAwardType("L");
		// scqtSpec.setContractDuration(1);
		// scqtSpec.getScQt().setQtAmount(
		// this.getManagedBean().getScQtSpec().getScQt().getQtAmount());

		List<ScQtCheckList> specList = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> techSpeclist = this.getManagedBean()
				.getTechnicalSpecList();
		if (techSpeclist != null) {
			specList.addAll(techSpeclist);
		}

		List<ScQtCheckList> finSpeclist = this.getManagedBean()
				.getFinancialSpecList();
		if (finSpeclist != null) {
			specList.addAll(finSpeclist);
		}

		/*
		 * List<ScQtCheckList> st1tech = this.getManagedBean()
		 * .getScQtCheckSt1techProposal();
		 * 
		 * List<ScQtCheckList> collectionAll = new ArrayList<ScQtCheckList>();
		 * if (st1tech != null) {
		 * 
		 * collectionAll.addAll(st1tech); }
		 * 
		 * List<ScQtCheckList> st1TechAdd = this.getManagedBean()
		 * .getScQtCheckSt1techAdditionalProposalList();
		 * 
		 * if (st1TechAdd != null) {
		 * 
		 * collectionAll.addAll(st1TechAdd); }
		 * 
		 * List<ScQtCheckList> st1Fin = this.getManagedBean()
		 * .getScQtCheckSt1financeProposalList();
		 * 
		 * if (st1Fin != null) {
		 * 
		 * collectionAll.addAll(st1Fin); } List<ScQtCheckList> st1FinAdd =
		 * this.getManagedBean()
		 * .getScQtCheckSt1financeAdditionalProposalList();
		 * 
		 * if (st1FinAdd != null) {
		 * 
		 * collectionAll.addAll(st1FinAdd); } List<ScQtCheckList> st2Tech =
		 * this.getManagedBean() .getScQtCheckSt2techProposal();
		 * 
		 * if (st2Tech != null) {
		 * 
		 * collectionAll.addAll(st2Tech); } List<ScQtCheckList> st2TechAdd =
		 * this.getManagedBean() .getScQtCheckSt2techAdditionalProposalList();
		 * 
		 * if (st2TechAdd != null) {
		 * 
		 * collectionAll.addAll(st2TechAdd); } List<ScQtCheckList> st2Fin =
		 * this.getManagedBean() .getScQtCheckSt2financeProposalList();
		 * 
		 * if (st2Fin != null) {
		 * 
		 * collectionAll.addAll(st2Fin); } List<ScQtCheckList> st2FinAdd =
		 * this.getManagedBean()
		 * .getScQtCheckSt2financeAdditionalProposalList();
		 * 
		 * if (st2FinAdd != null) {
		 * 
		 * collectionAll.addAll(st2FinAdd); } List<ScQtWeightage>
		 * technicalWeight = this.getManagedBean() .getWeightageTechnical();
		 * 
		 * List<ScQtWeightage> weightList = new ArrayList<ScQtWeightage>();
		 * 
		 * if (technicalWeight != null) {
		 * 
		 * weightList.addAll(technicalWeight); }
		 * 
		 * List<ScQtWeightage> financialWeight = this.getManagedBean()
		 * .getWeightageFinancial();
		 * 
		 * if (financialWeight != null) {
		 * 
		 * weightList.addAll(financialWeight); }
		 */

		/*
		 * List<ScQtCheckListScore> scoreList = this.getManagedBean()
		 * .getCollectionScoring();
		 * 
		 * if (scoreList != null) {
		 * System.out.println("=======scoring list size===="+scoreList.size());
		 * for(ScQtCheckListScore score:scoreList){ ScQtCheckList
		 * checkList=this.getManagedBean().getScQtChecklist();
		 * checkList.setScQtCheckListScores(scoreList); }
		 * 
		 * }
		 */

		// scqtSpec.setScQtCheckLists(collectionAll);
		// scqtSpec.setScQtWeightages(weightList);
		getQuoteZones(this.getManagedBean().getZoneGeneralTrees(),
				new ScQuote());
		
		// scQt.setScQtSpec(scqtSpec);
	
		// specreqDTO.setScQtSpec(scqtSpec);
		// specSO.saveSpecDetails(specreqDTO);

		return specreqDTO;

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
			System.out.println("======enter spec details method===");
			IQTSpecSO specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			SpecificationResDTO resDTO = new SpecificationResDTO();
			reqDTO.setQtId(qtId);
			// getManagedBean().setScQtSpec(new ScQtSpec());
			System.out.println("====qtid===" + reqDTO.getQtId());
			resDTO = specSO.retrieveSpecDetails(reqDTO);
			System.out.println("=========resDTO.getScQt()===="
					+ resDTO.getScQt().getQtId());
		
			if (resDTO.getScQt()!= null) {
				getManagedBean().setScQt(resDTO.getScQt());
				
			if(this.getManagedBean().getSupplierCriteria()==null){
				this.getManagedBean().setSupplierCriteria(new ScQtSupplierCriteria());
			}
				
			
				IQTDetailSO QtDetailsSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(qtId);
				// tenderReqDTO.setQtTndrNo("xxx");
				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
				tenderResDTO = QtDetailsSO.retriveQtById(tenderReqDTO);
				getManagedBean().setScQt(tenderResDTO.getQtDetail());
			
			}

			// }
			else {
				System.out.println("=========enter else method===");
				
				getManagedBean().setScQtCheckSt1techProposal(
						resDTO.getS1TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1techAdditionalProposalList(
						resDTO.getS1AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeProposalList(
						resDTO.getS1FinResultBean().getResultList());
				getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
						resDTO.getS1AddFinResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getS2TecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2techAdditionalProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getS2AddTecResultBean().getResultList());
				getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
						resDTO.getS2AddFinResultBean().getResultList());
				getManagedBean().setCommitteeMemberList(
						resDTO.getCommMemberResultBean().getResultList());

			

				
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
		System.out.println("========type value===" + type);
		this.getManagedBean().getScQt().setQtType(type);
	}

	public void typeChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtCheckSt2techProposal().get(0)
				.setChecklistType(type);
	}

	public void fulfilmentChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		System.out.println("====fulfilment value====" + type);
		this.getManagedBean().getScQt().setFulfilmentType(type);
	}

	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======state====" + state);
		this.getManagedBean().getSupplierCriteria().setAllState(state);
	}

	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======standard template====" + template);
		this.getManagedBean().setStandardTemplate(template);
	}

	public void selectLocation(AjaxBehaviorEvent event) {
		Integer location = (Integer) event.getComponent().getAttributes()
				.get("value");
		System.out.println("=======location level====" + location);
		this.getManagedBean().getScQt().setZoneLocationLevel(location);
	}

	public void fromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setFromMonth(formvalue);
	}

	public String technicalChecklistEditAction(ScQtCheckList checkList) {
		if (checkList.getTitle().contains("Company Experience")) {
			System.out.println("===checkList.getTitle()==="
					+ checkList.getTitle());
			StringTokenizer tokenizer = new StringTokenizer(
					checkList.getTitle(), "(,)");
			// System.out.println("tokenizer value==="+tokenizer.nextElement());
			String compnayName = "";
			while (tokenizer.hasMoreTokens()) {
				// System.out.println(tokenizer.nextToken());
				compnayName = tokenizer.nextToken();
				if (compnayName
						.equalsIgnoreCase("The List of Contract based on number of contract from Federal Government")) {
					this.getManagedBean().setCheckListScore(null);
					this.getManagedBean().setScoringForYears(null);
					this.getManagedBean().setContractAmount(null);
					return "companyExperience.xhtml?faces-redirect=true";
				} else if (compnayName
						.equalsIgnoreCase("The List of Contract based on number of contract from Federal Private")) {
					this.getManagedBean().setNonFederalScore(null);
					this.getManagedBean().setNonFederalScoreyears(null);
					this.getManagedBean().setNonFederalContractScore(null);
					return "companyExperiencePrivate.xhtmlfaces-redirect=true";
				}
			}

		}

		else if (checkList.getTitle().contains("Warranty")) {
			this.getManagedBean().setWarantyScore(null);
			return "waranty.xhtml?faces-redirect=true";
		}

		else if (checkList.getTitle().contains("Training")) {
			this.getManagedBean().setTrainingScore(null);
			return "training.xhtml?faces-redirect=true";
		} else if (checkList.getTitle().contains("Senari")) {
			return "technicalEmployeeList.xhtml?faces-redirect=true";
		} else if (checkList.getTitle().contains("Bilangan")) {
			return "totalEmployees.xhtml?faces-redirect=true";
		}

		return null;

	}

	public String financialChecklistEditAction(ScQtCheckList checkList) {
		if (checkList.getTitle().contains("Company Experience")) {
			StringTokenizer tokenizer = new StringTokenizer(
					checkList.getTitle(), "(,)");

			String compnayName = "";
			while (tokenizer.hasMoreTokens()) {

				compnayName = tokenizer.nextToken();
				if (compnayName
						.equalsIgnoreCase("The List of Contract based on Value (RM) of Contract from Federal Government")) {
					return "companyExperience.xhtml?faces-redirect=true";
				} else if (compnayName
						.equalsIgnoreCase("The List of Contract based on Value (RM) of Contract from Federal Private")) {
					return "companyExperiencePrivate.xhtmlfaces-redirect=true";
				}
			}

		} else if (checkList.getTitle().contains("Kemudahan")) {
			return "creditFacility.xhtml?faces-redirect=true";
		}

		else if (checkList.getTitle().contains("Tunai")) {
			return "cashOnhand.xhtml?faces-redirect=true";
		}

		else if (checkList.getTitle().contains("Bank")) {
			return "bankStatement.xhtml?faces-redirect=true";
		}

		else if (checkList.getTitle().contains("Assets")) {
			return "assetsLiabilities.xhtml?faces-redirect=true";
		} else if (checkList.getTitle().contains("Maklumat")) {
			return "supplierinfo.xhtml?faces-redirect=true";
		} else if (checkList.getTitle().contains("Paid")) {
			return "paidUpCapital.xhtml?faces-redirect=true";
		}

		return null;

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

			qtTemplateReqDTO.setQtId(146L);
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

	public void toChange(AjaxBehaviorEvent event) {
		String toValue = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setToMonth(toValue);
		int n = Integer.parseInt(this.getManagedBean().getFromMonth());
		int j = Integer.parseInt(toValue);
		List<BankingBean> monthList = new ArrayList<BankingBean>();
		String monthvalue = "";
		String amount = "";
		for (int i = n; i <= j; i++) {
			if (i == 0) {

				monthvalue = "January";
				amount = "1000";
			} else if (i == 1) {

				monthvalue = "February";
				amount = "2000";
			} else if (i == 2) {

				monthvalue = "March";
				amount = "3000";
			} else if (i == 3) {

				monthvalue = "April";
				amount = "4000";
			} else if (i == 4) {

				monthvalue = "May";
				amount = "5000";
			} else if (i == 5) {

				monthvalue = "June";
				amount = "6000";
			} else if (i == 6) {

				monthvalue = "July";
				amount = "7000";
			} else if (i == 7) {

				monthvalue = "August";
				amount = "8000";
			} else if (i == 8) {

				monthvalue = "September";
				amount = "9000";
			} else if (i == 9) {

				monthvalue = "October";
				amount = "10000";
			} else if (i == 10) {

				monthvalue = "November";
				amount = "11000";
			} else if (i == 11) {

				monthvalue = "December";
				amount = "12000";
			}
			monthList.add(new BankingBean(monthvalue, amount));
		}
		int totalamount = 0;
		this.getManagedBean().setBankList(monthList);
		for (int i = 0; i < this.getManagedBean().getBankList().size(); i++) {
			BankingBean bean = (BankingBean) this.getManagedBean()
					.getBankList().get(i);
			totalamount = totalamount + Integer.parseInt(bean.getAmount());
			BigDecimal b1 = new BigDecimal(totalamount);
			BigDecimal techWeightage = new BigDecimal(this.getManagedBean()
					.getBankList().size());
			MathContext mc = new MathContext(4);
			BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
			this.getManagedBean().setTotalAmountAvg(techWeightage1);
		}
		this.getManagedBean().setTotalAmount(totalamount);

	}

	public ScQtCheckList createSpecList(String stage) {
		//ScQtSpec scQtSpec = getManagedBean().getScQtSpec();
		ScQtCheckList additionProposalst1 = new ScQtCheckList();

		if (stage.equalsIgnoreCase("1")) {
			// additionProposalst1.setScQtWeightage(null);
		} else if (stage.equalsIgnoreCase("2")) {
			// additionProposalst1.setScQtWeightage(new ScQtWeightage());
		}
		//additionProposalst1.setScQtSpec(scQtSpec);
		return additionProposalst1;
	}

	public void stageOneAdditionalAdd() {

		try {
			List<ScQtCheckList> st1additionalList = getManagedBean()
					.getScQtCheckSt1techAdditionalProposalList();

			if (null == st1additionalList) {
				st1additionalList = new ArrayList<ScQtCheckList>();
			}

			ScQtCheckList additionProposalst1 = createSpecList("1");
			additionProposalst1.setDocType("2");
			additionProposalst1.setEvaluationStage("1");
			additionProposalst1.setChecklistType("UP");
			st1additionalList.add(additionProposalst1);
			// mediumCarsModel = new CarDataModel(st1additionalList);
			this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(
					st1additionalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiProposalAdd() {

		try {
			List<ScQtCheckList> st1financeproposalList = getManagedBean()
					.getScQtCheckSt1financeProposalList();
			if (null == st1financeproposalList) {
				st1financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeproposalList1 = createSpecList("1");
			st1financeproposalList1.setDocType("3");
			st1financeproposalList1.setEvaluationStage("1");
			st1financeproposalList1.setChecklistType("UP");
			st1financeproposalList.add(st1financeproposalList1);
			this.getManagedBean().setScQtCheckSt1financeProposalList(
					st1financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneFiAdditioalProposalAdd() {

		try {
			List<ScQtCheckList> st1financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt1financeAdditionalProposalList();
			if (null == st1financeAdditionalproposalList) {
				st1financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st1financeAdditionalproposalList1 = createSpecList("1");
			st1financeAdditionalproposalList1.setDocType("4");
			st1financeAdditionalproposalList1.setEvaluationStage("1");
			st1financeAdditionalproposalList1.setChecklistType("UP");
			st1financeAdditionalproposalList
					.add(st1financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
					st1financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoAdditionalAdd() {
		try {
			List<ScQtCheckList> st2additionalList = getManagedBean()
					.getScQtCheckSt2techAdditionalProposalList();
			if (null == st2additionalList) {
				st2additionalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList additionProposalst2 = createSpecList("2");
			additionProposalst2.setDocType("2");
			additionProposalst2.setEvaluationStage("2");
			additionProposalst2.setChecklistType("UP");
			st2additionalList.add(additionProposalst2);
			this.getManagedBean().setScQtCheckSt2techAdditionalProposalList(
					st2additionalList);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiProposalAdd() {

		try {
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList("2");
			st2financeproposalList2.setDocType("3");
			st2financeproposalList2.setChecklistType("UP");
			st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList.add(st2financeproposalList2);
			this.getManagedBean().setScoringList(null);
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoFiAdditioalProposalAdd() {
		try {
			List<ScQtCheckList> st2financeAdditionalproposalList = getManagedBean()
					.getScQtCheckSt2financeAdditionalProposalList();
			if (null == st2financeAdditionalproposalList) {
				st2financeAdditionalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeAdditionalproposalList1 = createSpecList("2");
			st2financeAdditionalproposalList1.setDocType("4");
			st2financeAdditionalproposalList1.setEvaluationStage("2");
			st2financeAdditionalproposalList1.setChecklistType("UP");
			st2financeAdditionalproposalList
					.add(st2financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
					st2financeAdditionalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOneTechProposal() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt1techProposal();

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageOneComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("1");

				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());
				// scqtlist.setQtChecklistId(new Long(i));
				scqtlist.setDocType("1");
				scqtlist.setEvaluationStage("1");
				scqtlist.setChecklistType("UP");
				compilance.add(scqtlist);
			}
			getManagedBean().setScQtCheckSt1techProposal(compilance);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechProposal() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			System.out.println("===========select tech list size====="
					+ this.getManagedBean().getStageTwoTechComplianceSelect());

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageTwoTechComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");
				System.out.println("=========checklist name======"
						+ bean.getPmCheckList().getCheckListName());
				System.out.println("====type==="
						+ bean.getPmCheckList().getChecklistType());
				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());
				scqtlist.setManditory(true);
				compilance.add(scqtlist);
				scqtlist.setDocType("1");
				// scqtlist.setEnable(true);
				scqtlist.setChecklistType(bean.getPmCheckList()
						.getChecklistType());
				scqtlist.setEvaluationStage("2");
			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwoTechnicalAddProposal() {
		try {
			List<ScQtCheckList> st2technicalproposalList = getManagedBean()
					.getScQtCheckSt2techProposal();

			if (null == st2technicalproposalList) {
				st2technicalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2technicalproposalList1 = createSpecList("2");

			st2technicalproposalList1.setDocType("1");
			// st2technicalproposalList1.setChecklistType("UP");
			st2technicalproposalList1.setEvaluationStage("2");
			// st2technicalproposalList1.setScQtCheckListScores();
			this.getManagedBean().setScoringList(null);
			st2technicalproposalList.add(st2technicalproposalList1);
			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageTwotechproposal(AjaxBehaviorEvent e) {

		try {
			String dropdown = (String) e.getComponent().getAttributes()
					.get("value");
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			setSelctValue(dropdown);
		} catch (Exception e1) {
			throw new IllegalArgumentException();
		}
		// this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}

	public void stageTwoFinanceCheckList() {
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			List<PmCheckListCategory> selectList = this.getManagedBean()
					.getStageTwoFinComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmCheckListCategory bean = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");
				scqtlist.setTitle(bean.getPmCheckList().getCheckListName());
				scqtlist.setDocType("3");
				// scqtlist.setEnable(true);
				scqtlist.setChecklistType(bean.getPmCheckList()
						.getChecklistType());
				scqtlist.setEvaluationStage("2");
				scqtlist.setManditory(true);
				compilance.add(scqtlist);
			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void stageOnecompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */
			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("134");
			reqDTO.setDocType("1");
			reqDTO.setEvalStage("1");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);
			this.getManagedBean().setStageOneComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public void stageTwoTechcompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */

			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("131");
			reqDTO.setDocType("1");
			// reqDTO.setEvalStage("2");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);

			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmCheckListCategory>());

			this.getManagedBean().setStageTwoTechComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public void deliveryList(String deliveryZoneName) {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */

			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("131");
			reqDTO.setDocType("1");
			// reqDTO.setEvalStage("2");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);

			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmCheckListCategory>());

			this.getManagedBean().setStageTwoTechComplianceList(
					resDTO.getCheckListResultBean().getResultList());
			System.out.println("========delivery zone name===="
					+ deliveryZoneName);
			this.getManagedBean().setDeliveyzoneName(deliveryZoneName);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public void deliveryAccept() {
		List<PmCheckListCategory> checklist = this.getManagedBean()
				.getStageTwoTechComplianceSelect();
		System.out.println("==========deliveryAccept zone name=========="
				+ getManagedBean().getDeliveyzoneName());
		
		for (PmCheckListCategory cat : checklist) {
			System.out.println("===========dispaly names in values==="
					+ cat.getPmCheckList().getCheckListName());
		
		List<BriefingZoneBean> deliveryBean = this.getManagedBean()
				.getDeliveryLocBean();
		
		for (BriefingZoneBean zonebean : deliveryBean) {
			if(zonebean.getZoneName().equalsIgnoreCase(getManagedBean().getDeliveyzoneName())){
				List<ScQtDeliveryLoc> docLoc=zonebean.getDeliveryLocList();
				if(docLoc==null){
					docLoc=new ArrayList<ScQtDeliveryLoc>();
				}
						
				System.out.println("====zone avilable==="+zonebean.getDeliveryLocList());
				ScQtDeliveryLoc loc=new ScQtDeliveryLoc();
				loc.setDeliveryAddress(cat.getPmCheckList().getCheckListName());
				docLoc.add(loc);
				zonebean.setDeliveryLocList(docLoc);
				break;
			}
			
		}
		 if(getManagedBean().getDeliveyzoneName()==null){
			 List<ScQtDeliveryLoc> docLoc= this.getManagedBean().getDeliveryNonZoneList();
			 if(docLoc==null){
					docLoc=new ArrayList<ScQtDeliveryLoc>();
				}
			System.out.println("========no zone avilable====");
			ScQtDeliveryLoc loc=new ScQtDeliveryLoc();
			loc.setDeliveryAddress(cat.getPmCheckList().getCheckListName());
			docLoc.add(loc);
			this.getManagedBean().setDeliveryNonZoneList(docLoc);
			
		}
		}

	}
	
	
	public void deleteDeliveryZone(String deliveryZoneName){
		List<BriefingZoneBean> deliveryBean = this.getManagedBean()
				.getDeliveryLocBean();
		System.out.println("====delete zonename==="+deliveryZoneName);
		for (BriefingZoneBean zonebean : deliveryBean) {
			if(zonebean.getZoneName().equalsIgnoreCase(deliveryZoneName)){
				List<ScQtDeliveryLoc> deliveryList=zonebean.getDeliveryLocList();
				System.out.println("=========delivery list size===="+deliveryList.size());
				List<ScQtDeliveryLoc> selectdeliveryList=this.getManagedBean().getSelectDeliveryList();
				System.out.println("=========selectdeliveryList list size===="+selectdeliveryList.size());
				if(deliveryList.size() == selectdeliveryList.size()){
					System.out.println("======equals=====");
					deliveryList.clear();
					
				}
				else{
					System.out.println("===delete not equals====");
					for(ScQtDeliveryLoc delLoc: selectdeliveryList){
						deliveryList.remove(delLoc);
					}
				}
			}
		}
		
	}

	public void stageTwoFincompilenceList() {
		ICheckListSO checkListSO;
		CheckListResDTO resDTO;
		try {
			/*
			 * System.out.println("=====procuremt id=====" +
			 * this.getManagedBean().getProcurementId());
			 */
			checkListSO = BusinessDelegate.getInstance().getServiceObject("pm",
					ICheckListSO.class);
			CheckListReqDTO reqDTO = new CheckListReqDTO();
			reqDTO.setProcurementMode("131");
			reqDTO.setDocType("3");
			// reqDTO.setEvalStage("2");
			resDTO = checkListSO
					.retrieveCheckListByEvalStageAndProcurementMode(reqDTO);

			this.getManagedBean().setStageTwoFinComplianceSelect(
					new ArrayList<PmCheckListCategory>());

			this.getManagedBean().setStageTwoFinComplianceList(
					resDTO.getCheckListResultBean().getResultList());
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		} finally {
			checkListSO = null;
		}

	}

	public ScQtCheckListScore createCheckList() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore score = new ScQtCheckListScore();
		score.setCheckList(checkList);
		return score;
	}

	public void addScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCheckListScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setCheckListScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addScoringWaranty() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getWarantyScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setWarantyScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addTrainingScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getTrainingScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setTrainingScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addTotalEmployeeScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getTotalEmployeeScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setTotalEmployeeScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addTechnicalStaffScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getTechnicalStaffScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setTechnicalStaffScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addPaidUpCapitalScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getPaidUpCapitalScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setPaidUpCapitalScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addScoringyears() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringForYears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addContratAmount() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getContractAmount();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setContractAmount(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addPrivateScoringyears() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getNonFederalScoreyears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setNonFederalScoreyears(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addPrivateContratAmount() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getNonFederalContractScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setNonFederalContractScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addPrivateScoring() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getNonFederalScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setNonFederalScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addBankStatement() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getBankStamentScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setBankStamentScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void creditFacilityScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCreditFacility();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setCreditFacility(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void scoringCash() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringCash();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setScoringCash(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addAssetScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getAssetScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setAssetScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addPackageScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getPackScoringList();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setPackScoringList(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addItemScore() {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getItemScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();

			checkListScore.add(score);
			this.getManagedBean().setItemScore(checkListScore);

		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void addScheduleDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtFreqSchedule> freqSchedule = this.getManagedBean()
					.getScheduleList();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtFreqSchedule>();

			}

			int total = freqSchedule.size()
					+ this.getManagedBean().getTotalFrequency();
			/*
			 * if(freqSchedule.size()>this.getManagedBean().getTotalFrequency()){
			 * ScQtFreqSchedule schedule = new ScQtFreqSchedule();
			 * System.out.println("=====greater===");
			 * freqSchedule.add(schedule); }
			 */
			if (freqSchedule.size() > this.getManagedBean().getTotalFrequency()) {
				int listsize = freqSchedule.size();
				int textsize = this.getManagedBean().getTotalFrequency();
				int lesstotal = listsize - textsize;

				freqSchedule = new ArrayList<ScQtFreqSchedule>();
				for (int j = 0; j < textsize; j++) {
					ScQtFreqSchedule schedule = new ScQtFreqSchedule();

					freqSchedule.add(schedule);
				}
			}
			for (int i = freqSchedule.size(); i < this.getManagedBean()
					.getTotalFrequency(); i++) {
				ScQtFreqSchedule schedule = new ScQtFreqSchedule();

				freqSchedule.add(schedule);
			}
			this.getManagedBean().setScheduleList(freqSchedule);

		} catch (Exception e) {
			e.printStackTrace();
			// throw new IllegalArgumentException();
		}
	}

	public void addProgressiveDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			List<ScQtProgessivePayment> progressive = this.getManagedBean()
					.getProgressivePaymentList();
			if (null == progressive) {
				progressive = new ArrayList<ScQtProgessivePayment>();

			}
			int total = progressive.size()
					+ this.getManagedBean().getTotalProgressivePayment();

			if (progressive.size() > this.getManagedBean()
					.getTotalProgressivePayment()) {
				int listsize = progressive.size();
				int textsize = this.getManagedBean()
						.getTotalProgressivePayment();
				int lesstotal = listsize - textsize;

				progressive = new ArrayList<ScQtProgessivePayment>();
				for (int j = 0; j < textsize; j++) {
					ScQtProgessivePayment payment = new ScQtProgessivePayment();

					progressive.add(payment);
				}
			}
			for (int i = progressive.size(); i < this.getManagedBean()
					.getTotalProgressivePayment(); i++) {
				ScQtProgessivePayment payment = new ScQtProgessivePayment();

				progressive.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressive);

			// progressive.add(payment);
			// this.getManagedBean().setProgressivePaymentList(progressive);
			/*
			 * System.out.println("===exit add score=="+ this.getManagedBean()
			 * .getCheckListScore().size());
			 */
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	public void acceptCompanyScoring() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> checkListScore = this.getManagedBean()
				.getCheckListScore();
		if (null == checkListScore) {
			checkListScore = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> scoreYears = this.getManagedBean()
				.getScoringForYears();
		if (null == scoreYears) {
			scoreYears = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> scoreContract = this.getManagedBean()
				.getContractAmount();
		if (null == scoreContract) {
			scoreContract = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (checkListScore != null) {

			localList.addAll(checkListScore);
		}
		if (scoreYears != null) {

			localList.addAll(scoreYears);
		}
		if (scoreContract != null) {

			localList.addAll(scoreContract);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptCompanyPrivateScoring() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> nonFederalScore = this.getManagedBean()
				.getNonFederalScore();
		if (null == nonFederalScore) {
			nonFederalScore = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> scoreYears = this.getManagedBean()
				.getNonFederalScoreyears();
		if (null == scoreYears) {
			scoreYears = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> scoreContract = this.getManagedBean()
				.getNonFederalContractScore();
		if (null == scoreContract) {
			scoreContract = new ArrayList<ScQtCheckListScore>();

		}
		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (nonFederalScore != null) {

			localList.addAll(nonFederalScore);
		}
		if (scoreYears != null) {

			localList.addAll(scoreYears);
		}
		if (scoreContract != null) {

			localList.addAll(scoreContract);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptWarrantyScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> warrantyScore = this.getManagedBean()
				.getWarantyScore();
		if (null == warrantyScore) {
			warrantyScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (warrantyScore != null) {

			localList.addAll(warrantyScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptTrainingScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> trainingScore = this.getManagedBean()
				.getTrainingScore();
		if (null == trainingScore) {
			trainingScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (trainingScore != null) {

			localList.addAll(trainingScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptTechnicalScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> technicalScore = this.getManagedBean()
				.getTechnicalStaffScore();
		if (null == technicalScore) {
			technicalScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (technicalScore != null) {

			localList.addAll(technicalScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptEmployeeScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> employeeScore = this.getManagedBean()
				.getTotalEmployeeScore();
		if (null == employeeScore) {
			employeeScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (employeeScore != null) {

			localList.addAll(employeeScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptCreditScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> creditFacilityScore = this.getManagedBean()
				.getCreditFacility();
		if (null == creditFacilityScore) {
			creditFacilityScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (creditFacilityScore != null) {

			localList.addAll(creditFacilityScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptCashScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> cashScore = this.getManagedBean()
				.getScoringCash();
		if (null == cashScore) {
			cashScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (cashScore != null) {

			localList.addAll(cashScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptBankScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> bankScore = this.getManagedBean()
				.getBankStamentScore();
		if (null == bankScore) {
			bankScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (bankScore != null) {

			localList.addAll(bankScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptAssetScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> assetScore = this.getManagedBean()
				.getAssetScore();
		if (null == assetScore) {
			assetScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (assetScore != null) {

			localList.addAll(assetScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptPaidUpScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		List<ScQtCheckListScore> paidUpScore = this.getManagedBean()
				.getPaidUpCapitalScore();
		if (null == paidUpScore) {
			paidUpScore = new ArrayList<ScQtCheckListScore>();

		}

		List<ScQtCheckListScore> localList = this.getManagedBean()
				.getCollectionScoring();
		if (localList == null) {
			localList = new ArrayList<ScQtCheckListScore>();
		}
		if (paidUpScore != null) {

			localList.addAll(paidUpScore);
		}

		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public List<SelectItem> getAppDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		UserReqDTO resDto = null;
		try {
			ScQtZoneCriteria scQtZone;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);
			List<PmUser> comitteeUsersUsers = new ArrayList<PmUser>();
			if (getManagedBean().getComitteeUsers() == null) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(RoleCode.MSC_COMMITTEE.value());
				UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				comitteeUsersUsers = resDTO.getUserList();
			}
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < comitteeUsersUsers.size(); i++) {
				PmUser user = (PmUser) comitteeUsersUsers.get(i);
				selectItems.add(new SelectItem(user.getUserId(), user
						.getUserName()));
			}
			this.getManagedBean().setComitteeUsers(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getComitteeUsers();
	}

	public List<SelectItem> getYearDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, "--Select One--"));
			selectItems.add(new SelectItem(0, "January"));
			selectItems.add(new SelectItem(1, "February"));
			selectItems.add(new SelectItem(2, "March"));
			selectItems.add(new SelectItem(3, "April"));
			selectItems.add(new SelectItem(4, "May"));
			selectItems.add(new SelectItem(5, "June"));
			selectItems.add(new SelectItem(6, "July"));
			selectItems.add(new SelectItem(7, "August"));
			selectItems.add(new SelectItem(8, "September"));
			selectItems.add(new SelectItem(9, "October"));
			selectItems.add(new SelectItem(10, "November"));
			selectItems.add(new SelectItem(11, "December"));

			this.getManagedBean().setYearList(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getYearList();
	}

	public List<SelectItem> getProcurementDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ProcurementReqDTO reqDTO = new ProcurementReqDTO();
		ProcurementResDTO resDto = null;
		try {

			IProcurementSO procurementSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", IProcurementSO.class);
			System.out.println(procurementSO);
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

	public List<SelectItem> getCurrencyDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		CurrencyReqDTO reqDTO = new CurrencyReqDTO();
		CurrencyResDTO resDto = null;
		try {

			ICurrencySO CurrencySO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICurrencySO.class);
			resDto = CurrencySO.retriveCurrencyList();
			selectItems.add(new SelectItem(null, "--Select One--"));
			for (int i = 0; i < resDto.getCurrencyList().size(); i++) {
				PmCurrency currency = (PmCurrency) resDto.getCurrencyList()
						.get(i);
				selectItems.add(new SelectItem(currency.getCurrencyCode(),
						currency.getCurrencyName()));
			}
			this.getManagedBean().setCurrencyListDetails(selectItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getManagedBean().getCurrencyListDetails();
	}

	public void handleFileUpload(FileUploadEvent event) {

		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);

		ScQtCheckListAttachment attachment = scQtChecklist
				.getScQtCheckListAttachment();
		if (null == attachment) {
			attachment = new ScQtCheckListAttachment();
			attachment.setScQtCheckList(scQtChecklist);
		}
		uploadAttachment(event.getFile(), attachment);

		scQtChecklist.setScQtCheckListAttachment(attachment);
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

		SpecificationReqDTO specReqDTO = prepareSpecReq();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		specReqDTO.setToken(ctxToken);

		specReqDTO.setTaskId(this.getManagedBean().getTaskId());

		specReqDTO.setScSpecificationDataType(this.getManagedBean()
				.getSpecificationDataType());
		specReqDTO.setOutcome(outCome);
		IQTSpecSO iqtSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		try {
			SpecificationResDTO resDTO = iqtSpecSO.updateBPMAction(specReqDTO);
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

	/**
	 * @return the selctValue
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * @param selctValue
	 *            the selctValue to set
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	public String updateBpmTask(String action) {
		System.out.println("Succeesfully update the task");
		SpecificationReqDTO spReqDTO = prepareSpecReq();
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();
		try {
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
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

	/*
	 * public void handleS2TechChanged(ScQtCheckList checklist) { try {
	 * 
	 * ScQtWeightage weightage = checklist.getScQtWeightage();
	 * 
	 * if (weightage != null) {
	 * 
	 * List<ScQtWeightage> weightList = this.getManagedBean()
	 * .getWeightageTechnical(); for (ScQtWeightage techweight : weightList) {
	 * 
	 * if (techweight.getWeightageTitle().equalsIgnoreCase(
	 * weightage.getWeightageTitle())) {
	 * 
	 * checklist.setScQtWeightage(techweight);
	 * 
	 * break; } }
	 * 
	 * } } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * public void handleS2AddTechChanged(ScQtCheckList checklist) { try {
	 * 
	 * ScQtWeightage weightage = checklist.getScQtWeightage();
	 * 
	 * if (weightage != null) {
	 * 
	 * List<ScQtWeightage> weightList = this.getManagedBean()
	 * .getWeightageTechnical(); for (ScQtWeightage techweight : weightList) {
	 * 
	 * if (techweight.getWeightageTitle().equalsIgnoreCase(
	 * weightage.getWeightageTitle())) {
	 * 
	 * checklist.setScQtWeightage(techweight);
	 * 
	 * break; }
	 * 
	 * } } } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * public void handleS2FinChanged(ScQtCheckList checklist) {
	 * 
	 * try { ScQtWeightage weightage = checklist.getScQtWeightage();
	 * 
	 * if (weightage != null) {
	 * 
	 * List<ScQtWeightage> weightList = this.getManagedBean()
	 * .getWeightageFinancial(); for (ScQtWeightage techweight : weightList) {
	 * 
	 * if (techweight.getWeightageTitle().equalsIgnoreCase(
	 * weightage.getWeightageTitle())) {
	 * 
	 * checklist.setScQtWeightage(techweight);
	 * 
	 * break; } }
	 * 
	 * } } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * public void handleS2AddFinChanged(ScQtCheckList checklist) {
	 * 
	 * try { ScQtWeightage weightage = checklist.getScQtWeightage();
	 * 
	 * if (weightage != null) {
	 * 
	 * List<ScQtWeightage> weightList = this.getManagedBean()
	 * .getWeightageFinancial(); for (ScQtWeightage techweight : weightList) {
	 * 
	 * if (techweight.getWeightageTitle().equalsIgnoreCase(
	 * weightage.getWeightageTitle())) {
	 * 
	 * checklist.setScQtWeightage(techweight);
	 * 
	 * break; } }
	 * 
	 * } } catch (Exception e) { e.printStackTrace(); } }
	 */

	public void retriveZonesByCircularNo() {
		String CirCularNo = this.getManagedBean().getCirCularNo();
		CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
		reqDTO.setCircularNo(CirCularNo);
		ICircularDetailSO circularDetailSO = (ICircularDetailSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						ICircularDetailSO.class);
		CircularDetailResDTO resDTO = circularDetailSO
				.retrieveZonesByCircularNo(reqDTO);

		if (resDTO.getCircularZones() != null) {
			List<TreeNode> localList = new ArrayList<TreeNode>();
			TreeNode rootLocationTree = new DefaultTreeNode("Root", null);
			// System.out.println("=======zone size==="+resDTO.getCircularZones().size());
			// this.getManagedBean().setCtZoneList(resDTO.getCircularZones());
			for (CtCircularZone zone : resDTO.getCircularZones()) {
				System.out.println(zone.getName());
				TreeNode zoneNode = new DefaultTreeNode("zone", zone,
						rootLocationTree);
				TreeNode stateNode = null;
				if (zone.getCtCircularZoneDtls() != null) {
					for (CtCircularZoneDtl zoneDtl : zone
							.getCtCircularZoneDtls()) {
						stateNode = new DefaultTreeNode("state",
								zoneDtl.getState(), zoneNode);
						// System.out.println(zoneDtl.getState().getStateName());pa
						// System.out.println(zoneDtl.getDistrict().getDistrictName());
					}
				}
				localList.add(rootLocationTree);
			}
			this.getManagedBean().setCtZoneTrees(rootLocationTree);
		}

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
			System.out.println(node.getChildCount());
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
		CtCircularZone circularZone = (CtCircularZone) event.getTreeNode()
				.getData();
		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		System.out.println("====ctzone id==="
				+ circularZone.getCircularZoneId());
		reqDTO.setCircularZoneId(1L);
		IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IPanelSupplierSO.class);
		PanelSupplierResDTO resDTO = panelSupplierSO
				.retrieveSupplierByZone(reqDTO);
		if (resDTO.getPanelSuppliers() != null) {
			System.out.println(resDTO.getPanelSuppliers());
			this.getManagedBean().setTotalSuppliers(
					resDTO.getPanelSuppliers().size());
			this.getManagedBean().setCtZoneSupplierList(
					resDTO.getPanelSuppliers());
		}
		

	}

	

	

	public void deleteS1Tech() {
		List<ScQtCheckList> s1Technical = this.getManagedBean()
				.getScQtCheckSt1techProposal();

		List<ScQtCheckList> selectTenical = this.getManagedBean()
				.getS1TechSelect();

		if (s1Technical.size() == selectTenical.size()) {
			s1Technical.clear();
		} else {
			for (ScQtCheckList checkList : selectTenical)
				s1Technical.remove(checkList);
		}

	}

	public void deleteS1TechAdd() {
		List<ScQtCheckList> s1TechnicalAdd = getManagedBean()
				.getScQtCheckSt1techAdditionalProposalList();
		List<ScQtCheckList> selectTenicalAdd = getManagedBean()
				.getS1TechAddSelect();
		if (s1TechnicalAdd.size() == selectTenicalAdd.size()) {
			s1TechnicalAdd.clear();
		} else {
			for (ScQtCheckList checkList : selectTenicalAdd)
				s1TechnicalAdd.remove(checkList);
		}

	}

	public void deleteS1Fin() {
		List<ScQtCheckList> s1Fin = getManagedBean()
				.getScQtCheckSt1financeProposalList();
		List<ScQtCheckList> selectFin = getManagedBean().getS1FinSelect();
		if (s1Fin.size() == selectFin.size()) {
			s1Fin.clear();
		} else {
			for (ScQtCheckList checkList : selectFin)
				s1Fin.remove(checkList);
		}

	}

	public void deleteS1FinAdd() {
		List<ScQtCheckList> s1FinAdd = getManagedBean()
				.getScQtCheckSt1financeAdditionalProposalList();
		List<ScQtCheckList> selectFinAdd = getManagedBean().getS1FinAddSelect();
		if (s1FinAdd.size() == selectFinAdd.size()) {
			s1FinAdd.clear();
		} else {
			for (ScQtCheckList checkList : selectFinAdd)
				s1FinAdd.remove(checkList);
		}

	}

	public void deleteS2Tech() {
		List<ScQtCheckList> s2Technical = getManagedBean()
				.getScQtCheckSt2techProposal();
		List<ScQtCheckList> selects2Tenical = getManagedBean()
				.getS2TechSelect();

		if (s2Technical.size() == selects2Tenical.size()) {
			s2Technical.clear();
		} else {
			for (ScQtCheckList checkList : selects2Tenical)
				s2Technical.remove(checkList);
		}

	}

	public void deleteS2TechAdd() {
		List<ScQtCheckList> s2TechnicalAdd = getManagedBean()
				.getScQtCheckSt2techAdditionalProposalList();
		List<ScQtCheckList> selects2TenicalAdd = getManagedBean()
				.getS2TechAddSelect();
		if (s2TechnicalAdd.size() == selects2TenicalAdd.size()) {
			s2TechnicalAdd.clear();
		} else {
			for (ScQtCheckList checkList : selects2TenicalAdd)
				s2TechnicalAdd.remove(checkList);
		}

	}

	public void deleteS2Fin() {
		List<ScQtCheckList> s2Fin = getManagedBean()
				.getScQtCheckSt2financeProposalList();
		List<ScQtCheckList> selects2Fin = getManagedBean().getS2FinSelect();
		if (s2Fin.size() == selects2Fin.size()) {
			s2Fin.clear();
		} else {
			for (ScQtCheckList checkList : selects2Fin)
				s2Fin.remove(checkList);
		}

	}

	public void deleteS2FinAdd() {
		List<ScQtCheckList> s2FinAdd = getManagedBean()
				.getScQtCheckSt2financeAdditionalProposalList();
		List<ScQtCheckList> selects2FinAdd = getManagedBean()
				.getS2FinAddSelect();
		if (s2FinAdd.size() == selects2FinAdd.size()) {
			s2FinAdd.clear();
		} else {
			for (ScQtCheckList checkList : selects2FinAdd)
				s2FinAdd.remove(checkList);
		}

	}

	public void deleteScoring() {
		List<ScQtCheckListScore> scoringList = getManagedBean()
				.getScoringList();

		List<ScQtCheckListScore> selectScoring = this.getManagedBean()
				.getSelectscoring();

		if (scoringList.size() == selectScoring.size()) {
			scoringList.clear();
		} else {
			for (ScQtCheckListScore scoring : selectScoring)
				scoringList.remove(scoring);
		}

	}

	public void onScoringRowSelect(SelectEvent selectEvent) {
		IQTEvaluationSO evaluationSO;
		try {
			List<String> docType = new ArrayList<String>();
			ScQtCheckListScore score = (ScQtCheckListScore) selectEvent
					.getObject();
			List<ScQtCheckListScore> selectScoring = this.getManagedBean()
					.getSelectscoring();
			if (selectScoring == null) {
				selectScoring = new ArrayList<ScQtCheckListScore>();
			}
			selectScoring.add(score);
			this.getManagedBean().setSelectscoring(selectScoring);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			evaluationSO = null;
		}

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
				System.out.println(panelItem.getItemName());
			}
			this.getManagedBean().setCircularExpDate(
					(circular.getEndDate().toString()));
			this.getManagedBean().setCirCularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			this.getManagedBean().setCircularType(
					circular.getItemCategory().getPmParameterDescs().get(0)
							.getCodeName());
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

	/*
	 * public void deleteItemDetails() { List<CtPanelItem> itemDetailsList=
	 * this.getManagedBean().getPanelItemDetailsList();
	 * 
	 * List<CtPanelItem> selectItem =this. getManagedBean()
	 * .getSelectItemDetailsList();
	 * 
	 * if (itemDetailsList.size() == selectItem.size()) {
	 * itemDetailsList.clear(); } else { for (CtPanelItem item : selectItem)
	 * itemDetailsList.remove(item); }
	 * 
	 * }
	 */

	public void techWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				total = total + weight.getWeightage().intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setTechnicalWeightageScore(techWeightage1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void finWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getFinancialScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				total = total + weight.getWeightage().intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setFinancialWeightageScore(techWeightage1);
			}
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
