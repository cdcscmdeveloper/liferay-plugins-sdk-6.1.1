/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.currency.ICurrencySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.procurementcategory.IProcurementSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTEvaluationSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.SCProcessNameConst;
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
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.currency.CurrencyReqDTO;
import my.ep.dto.pm.common.currency.CurrencyResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementReqDTO;
import my.ep.dto.pm.common.procurementcategory.ProcurementResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCurrency;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmProcurementCategory;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.specification.SpecificationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
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
	private IPagingDataModel<TplSpecMaster> templateSpecCreate = new CreateTemplateSpecPagingDataModel();
	private IPagingDataModel<PmAddressType> searchDeliveryAddress = new DeliveryPagingDataModel();

	// private List<SpecificationCompilanceBean> selectedCompilenceList;
	private int count;

	private String selctValue;
	private int currentTab;
	private int outputTab;

	public SpecificationBackingBean() {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String saveCommitteeGeneral() {
		return "specificationCommitteeView.xhtml";
	}

	public String saveCommitteeTechnical() {
		this.getManagedBean().setTabActiveIndex(2);
		return "technicalChecklist.xhtml";
	}

	public String saveCommitteeFinancial() {
		this.getManagedBean().setTabActiveIndex(3);
		return "financialChecklist.xhtml";
	}

	public String saveCommitteeRecommendation() {
		this.getManagedBean().setTabActiveIndex(6);
		return "recomondation.xhtml";
	}

	public String saveCommitteeFulfilment() {
		this.getManagedBean().setTabActiveIndex(7);
		return "schedule.xhtml";
	}

	public String saveCommitteeDeliveryAddress() {
		this.getManagedBean().setTabActiveIndex(8);
		return "deliveryAddress.xhtml";
	}

	public String saveCommitteeEndorsment() {
		this.getManagedBean().setTabActiveIndex(9);
		return "specificationSignoff.xhtml";
	}

	public String saveCommitteeQualificationCriteria() {
		this.getManagedBean().setTabActiveIndex(1);
		return "SupplierCommitteeQualificationCriteria.xhtml";
	}

	private List<ScQtLocCriteria> getQuoteZones(List<TreeNode> treeNodes,
			ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		for (TreeNode treeNode : treeNodes) {
			List<TreeNode> stateNodes = treeNode.getChildren();
			for (TreeNode treeNode2 : stateNodes) {
				List<TreeNode> district = treeNode2.getChildren();
				if (null != district && !district.isEmpty()) {
					for (TreeNode devision : district) {
						if (devision.getChildCount() > 0) {
							for (TreeNode districtNode : devision.getChildren()) {
								ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode
										.getData());
								criteria.setPmDistrict(((ScQtLocCriteria) districtNode
										.getData()).getPmDistrict());
								criteria.setPmDivision(((ScQtLocCriteria) districtNode
										.getParent().getData()).getPmDivision());
								criteria.setPmState(((ScQtLocCriteria) treeNode2
										.getData()).getPmState());
								criteria.setScQtSupplierCriteria(this
										.getManagedBean().getSupplierCriteria());
								setUserDetails(criteria);
								results.add(criteria);
							}
						} else {
							ScQtLocCriteria criteria = ((ScQtLocCriteria) devision
									.getData());
							criteria.setPmDistrict(((ScQtLocCriteria) devision
									.getData()).getPmDistrict());
							criteria.setPmState(((ScQtLocCriteria) treeNode2
									.getData()).getPmState());
							criteria.setScQtSupplierCriteria(this
									.getManagedBean().getSupplierCriteria());
							setUserDetails(criteria);
							results.add(criteria);
						}

					}
				} else {

					ScQtLocCriteria criteria = (ScQtLocCriteria) treeNode2
							.getData();
					criteria.setScQtSupplierCriteria(this.getManagedBean()
							.getSupplierCriteria());
					setUserDetails(criteria);

					results.add(criteria);
				}
			}

		}

		return results;
	}

	private List<TplSpecZoneDtl> getZoneBusinessOwnerLocations(
			List<TreeNode> treeNodes, TplSpecZoneDtl zoneDtl) {

		List<TplSpecZoneDtl> zoneList = new ArrayList<TplSpecZoneDtl>();
		List<TplSpecZoneLoc> results = null;
		for (TreeNode treeNode : treeNodes) {

			List<TreeNode> zoneNodes = treeNode.getChildren();
			// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
			// zoneDtl = (TplSpecZoneDtl) zoneNodes.getData();
			// TplSpecZoneDtl zoneDtl=new TplSpecZoneDtl();
			zoneDtl = (TplSpecZoneDtl) zoneNodes.get(0).getData();
			for (TreeNode treeNode1 : zoneNodes) {
				results = new ArrayList<TplSpecZoneLoc>();
				List<TreeNode> stateNodes = treeNode1.getChildren();

				for (TreeNode treeNode2 : stateNodes) {

					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {

							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {
									TplSpecZoneLoc criteria = (TplSpecZoneLoc) districtNode
											.getData();

									criteria.setPmDistrict(((TplSpecZoneLoc) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((TplSpecZoneLoc) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((TplSpecZoneLoc) treeNode2
											.getData()).getPmState());
									criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
											.getData()));
									// criteria.setScQtSupplierCriteria(supplierCriteria);
									setUserDetails(criteria);
									results.add(criteria);

								}
							} else {

								TplSpecZoneLoc criteria = (TplSpecZoneLoc) devision
										.getData();
								criteria.setPmDistrict(((TplSpecZoneLoc) devision
										.getData()).getPmDistrict());
								criteria.setPmState(((TplSpecZoneLoc) treeNode2
										.getData()).getPmState());

								criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
										.getData()));
								// criteria.setScQtSupplierCriteria(supplierCriteria);
								setUserDetails(criteria);
								results.add(criteria);

							}

							// zoneDtl.setTplSpecZoneLocs(results);

						}
					} else {

						TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
								.getData();
						criteria.setPmState(((TplSpecZoneLoc) treeNode2
								.getData()).getPmState());

						criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
								.getData()));
						// criteria.setScQtSupplierCriteria(supplierCriteria);
						// setUserDetails(criteria);
						setUserDetails(criteria);
						results.add(criteria);
					}
				}

			}
			zoneDtl.setTplSpecZoneLocs(results);
			// zoneDtl.setTplSpecZone(new ScQtZoneCriteria());
			setUserDetails(zoneDtl);

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

	public void saveSpecificationDtls() {
		IQTDetailSO detailSO;
		try {
			QuationTenderReqDTO reqDTO = prepareSpecReq();
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderResDTO resDTo = null;
			List<FacesMessage> messages = new ArrayList<FacesMessage>();
			for (Iterator<FacesMessage> iter = FacesContext
					.getCurrentInstance().getMessages(); iter.hasNext();) {
				messages.add(iter.next());
				// iter.remove();
			}
			if (messages.size() == 0) {
				resDTo = detailSO.saveQTDtl(reqDTO);
			}

			if (null != resDTo && resDTo.isSuccessful()) {
				ScQt scQt = resDTo.getQtDetail();
				this.getManagedBean().setScQt(scQt);
				this.getManagedBean().setSupplierCriteria(
						scQt.getScQtSupplierCriteria());
				if ("O".equalsIgnoreCase(scQt.getQtType())) {
					this.getManagedBean().setSuppCriteriaopenList(
							scQt.getScQtCategorys());
				} else {
					this.getManagedBean().setSuppCriteriarestrictedList(
							scQt.getScQtCategorys());
				}
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004,
										new String[] { "Specification For ",
												scQt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}

		} finally {
			detailSO = null;
		}
	}

	public void saveSpecificationCommitteeDtls() {
		IQTDetailSO detailSO;
		try {
			QuationTenderReqDTO reqDTO = constructSpecCommitteDtls();
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderResDTO resDTo = detailSO.saveQTDtl(reqDTO);
			if (resDTo.isSuccessful()) {
				ScQt scQt = resDTo.getQtDetail();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700004, new String[] {
												"Specification Details For ",
												scQt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

		} finally {
			detailSO = null;
		}
	}

	private List<ScQtCheckList> constructFinSpecsNSocre(
			ScQtCheckList scQtCheckList) {
		List<ScQtCheckList> finSpecCheckLists = this.getManagedBean()
				.getFinSpecList();
		if (null != finSpecCheckLists) {
			for (ScQtCheckList scQtCheckList2 : finSpecCheckLists) {
				scQtCheckList2.setQtScoreWeightage(scQtCheckList
						.getQtScoreWeightage());
			}
		}
		return finSpecCheckLists;
	}

	public String newProceedAction() {
		if (!this.getManagedBean().getScQt().isPanel()) {
			return "specificationCreation.xhtml?faces-redirect=true";
		} else if (this.getManagedBean().getScQt().isPanel()
				&& "PCR".equalsIgnoreCase(this.getManagedBean()
						.getCircularType())
				&& "Specific".equalsIgnoreCase(this.getManagedBean()
						.getPanelItemCategory())
				|| "General".equalsIgnoreCase(this.getManagedBean()
						.getPanelItemCategory())) {

			return "specificationCreation.xhtml?faces-redirect=true";
			// return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}

		else if (this.getManagedBean().getScQt().isPanel()
				&& "CCR".equalsIgnoreCase(this.getManagedBean()
						.getCircularType())) {

			FacesScopes facesScopes = new FacesScopes();
			ScQtRemark remark = new ScQtRemark();
			remark.setScQt(this.getManagedBean().getScQt());
			remark = new ScQtRemark();
			setUserDetails(remark);
			remark.setCreatedUser(facesScopes.getUserSessionBean());
			PmStatus status = new PmStatus();
			status.setStatusId(62651L);
			remark.setPmStatus(status);
			this.getManagedBean().setContractremark(remark);

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

		return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

	}

	public String businessOwnerTemplateSpec() {
		return "/xhtml/sc/main/createTemplate.xhtml?faces-redirect=true";
	}

	public String retriveTemplatespec() {
		return "/xhtml/sc/main/createCommitteeTemplate.xhtml?faces-redirect=true";
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

		}

		ScQtCategory category = new ScQtCategory();
		// .setScQt(scQt);
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		qtCategoryList.add(category);
		this.getManagedBean().setSuppCriteriaopenList(qtCategoryList);
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

	public void deleteCategoryCodes() {

		List<ScQtCategory> openList = this.getManagedBean()
				.getSuppCriteriaopenList();

		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}

		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSuppCriteriaopenSelectList();
		if (null == openSelectList) {
			openSelectList = new ArrayList<ScQtCategory>();
		}

		if (openList.size() == openSelectList.size()) {
			openList.clear();
		} else {
			for (ScQtCategory category : openSelectList)
				openList.remove(category);
		}

		List<ScQtCategory> restrictedList = this.getManagedBean()
				.getSuppCriteriarestrictedList();
		if (null == restrictedList) {
			restrictedList = new ArrayList<ScQtCategory>();
		}

		List<ScQtCategory> restrictedSelectList = this.getManagedBean()
				.getSuppCriteriarestrictedBoSelectList();
		if (null == restrictedSelectList) {
			restrictedSelectList = new ArrayList<ScQtCategory>();
		}

		if (restrictedList.size() == restrictedSelectList.size()) {
			restrictedList.clear();
		} else {
			for (ScQtCategory category : restrictedSelectList)
				restrictedList.remove(category);
		}

	}

	public void saveEditTemplateSpec() {
		IQTTemplateSO qtTemplateSO = null;
		try {
			QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();
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

			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			QTTemplateDocResDTO resDTO = qtTemplateSO
					.saveOrUpdateTemplateDoc(qtTemplateReqDTO);
			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));
			}
		} finally {
			qtTemplateSO = null;
		}

	}

	public String completeTemplateSpec(String processName) {

		try {
			ScQtDoc technicalScqtDoc = this.getManagedBean().getScqtDoc();
			ScQtCheckList techChecklist = technicalScqtDoc.getScQtCheckList();
			if (null == techChecklist.getPmStatus()) {
				PmStatus status = new PmStatus();
				status.setStatusId(62702L);
				technicalScqtDoc.getScQtCheckList().setPmStatus(status);
			} else {
				PmStatus status = techChecklist.getPmStatus();
				status.setStatusId(62702L);
				technicalScqtDoc.getScQtCheckList().setPmStatus(status);
			}
			saveTempleateSpecDtls(technicalScqtDoc, "1");

			ScQtDoc finanicalScQtDoc = constructQtDocByType(
					technicalScqtDoc.getTplSpecMaster(), "2");

			PmStatus status = new PmStatus();
			status.setStatusId(62702L);
			finanicalScQtDoc.getScQtCheckList().setPmStatus(status);
			finanicalScQtDoc.getTplSpecMaster().setSpecMasterId(null);
			finanicalScQtDoc.getTplSpecMaster().setChangedDate(null);
			saveTempleateSpecDtls(finanicalScQtDoc, "2");
			retriveTechnicalSpecification();

		} finally {

		}
		if (null == processName || "".equals(processName)) {
			return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
		} else {
			return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

		}

	}

	public String saveTemplateSpec(ScQtDoc scQtDoc, String specType,
			String processName) {

		QTTemplateDocResDTO resDTO = saveTempleateSpecDtls(scQtDoc, specType);
		this.getManagedBean().setScqtDoc(resDTO.getScQtDoc());
		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Record saved successfully.", ""

					));
			retriveTechnicalSpecification();
			if (null == processName || "".equals(processName)) {
				return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
			} else {
				return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

			}
		}

		return null;

	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();

		if (tabTitle.equalsIgnoreCase("General")) {
			// retriveSpecdetails(this.getManagedBean().getScQt().getQtId());
		} else if (tabTitle.equalsIgnoreCase("Supplier Qualification Criteria")) {
			retriveBusinessOwnerQualificationCriteri();
		} else if (tabTitle.equalsIgnoreCase("Prepare Specification")) {
			retriveFinancialSpecification();
		}

		else if (tabTitle.equalsIgnoreCase("Technical Checklist")) {
			retriveTechnicalCheckList();

		} else if (tabTitle.equalsIgnoreCase("Financial Checklist")) {
			retriveFinancialCheckList();

		} else if (tabTitle.equalsIgnoreCase("Technical Scoring")) {
			retriveTechnicalScoring();
		} else if (tabTitle.equalsIgnoreCase("Financial Scoring")) {
			retriveFinancialScoring();
		} else if (tabTitle.equalsIgnoreCase("Recommendation")) {
			retriveRecommendationDetails();
		} else if (tabTitle.equalsIgnoreCase("Fulfilment Schedule")) {
			retriveFulfilmentDtls();
		}

		else if (tabTitle.equalsIgnoreCase("Delivery/Service Address")) {
			retriveDeliveryDetails();
		}

		else if (tabTitle.equalsIgnoreCase("Committee Endorsement")) {

			retriveCommitteeEndorsment();
		}

	}

	public void businessOwnerTabChange(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if (tabTitle.equalsIgnoreCase("General")) {
			retriveSpecdetails(this.getManagedBean().getScQt().getQtId());
		} else if (tabTitle.equalsIgnoreCase("Supplier Qualification Criteria")) {
			retriveBusinessOwnerQualificationCriteri();

		} else if (tabTitle.equalsIgnoreCase("Technical Specification")) {
			retriveTechnicalSpecification();

		} else if (tabTitle.equalsIgnoreCase("Financial Specification")) {
			retriveFinancialSpecification();
		}

		else if (tabTitle.equalsIgnoreCase("Approval")) {
			retriveApprovalRemarks();
		}
	}

	public String retriveBusinessOwnerQualificationCriteri() {
		IQTDetailSO QtDetailsSO;
		this.getManagedBean().setActiveIndex(1);

		try {

			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getScQt());
			QuationTenderResDTO tenderResDTO = QtDetailsSO
					.retriveQTSuppQulCriDetails(tenderReqDTO);
			List<ScQtCategory> categoryResults = tenderResDTO
					.getCategoryResultBean().getResultList();

			if (isNotNullOREmpty(categoryResults)) {

				if ("O".equalsIgnoreCase(tenderResDTO.getQtDetail().getQtType())) {
					this.getManagedBean().setSuppCriteriaopenList(
							categoryResults);
				} else if ("R".equalsIgnoreCase(tenderResDTO.getQtDetail()
						.getQtType())) {
					this.getManagedBean().setSuppCriteriarestrictedList(
							categoryResults);
				}
			}

			if (!tenderResDTO.getQtDetail().isZonal()
					&& !tenderResDTO.getQtDetail().isPanel()) {

				this.getManagedBean().setSupplierCriteria(
						tenderResDTO.getQtDetail().getScQtSupplierCriteria());

				if (this.getManagedBean().getZoneTrees() != null
						&& this.getManagedBean().getZoneTrees().size() == 0) {

					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();

					if (null != tenderResDTO.getLocationDetails()) {
						for (TplSpecLoc localLoc : tenderResDTO
								.getLocationDetails()) {

							ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
							locCriteria.add(criteria);
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.constuctTreeNode(locCriteria));
					}

				}

			}

			else if (tenderResDTO.getQtDetail().isZonal()
					&& tenderResDTO.getQtDetail().isPanel()) {

				this.getManagedBean().setSupplierCriteria(
						tenderResDTO.getQtDetail().getScQtSupplierCriteria());

				if (null != this.getManagedBean().getCtZoneSuppliers()
						&& this.getManagedBean().getCtZoneSuppliers().size() == 0) {

					List<CtPanelZone> ctPanelZone = new ArrayList<CtPanelZone>();
					if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
						for (TplSpecZoneDtl zoneDtl : tenderResDTO
								.getZoneDetails()) {
							
							ctPanelZone.add(zoneDtl.getPanelZone());
							
						}
					}
					if (null != ctPanelZone && !ctPanelZone.isEmpty()) {
						

						getManagedBean().getCtZoneSuppliers().add(
								LocationSingleton.getInstance()
										.constuctPanelTreeNodes(ctPanelZone));

					}
				}

			}

		} finally {
			QtDetailsSO = null;
		}
		return "specificationCreation.xhtml";
	}

	public void retriveTechnicalSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		this.getManagedBean().setActiveIndex(2);
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

		} finally {
			specSO = null;
		}

	}

	public void retriveFinancialSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		this.getManagedBean().setActiveIndex(3);
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

		} finally {
			specSO = null;
		}

	}

	public void retriveApprovalRemarks() {
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;
		this.getManagedBean().setActiveIndex(4);

		try {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocType("QT");
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(reqDTO);

			if (resDto.getRemarks() == null) {

				this.getManagedBean().setScQtRemarksList(
						new ArrayList<ScQtRemark>());
			}

			else {

				List<ScQtRemark> remarkList = new ArrayList<ScQtRemark>();
				for (ScRemark remark : resDto.getRemarks()) {
					ScQtRemark qtRemark = (ScQtRemark) remark;
					remarkList.add(qtRemark);

				}
				this.getManagedBean().setScQtRemarksList(remarkList);
			}

			initializeRemarks();

		} finally {
			qtDetailSO = null;
		}
	}

	public void initializeRemarks() {
		IStatusSO iStatusSO;

		try {

			FacesScopes facesScopes = new FacesScopes();
			iStatusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			ScQtRemark remark = null;

			remark = new ScQtRemark();

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

		} finally {
			iStatusSO = null;
		}
	}

	public void procurementCategoryCode(ScQt scQt) {
		try {
			PmParameter procurementCategory = scQt.getPmProcurementCategory();
			if (null != procurementCategory
					&& null != procurementCategory.getParameterId()) {
				this.getManagedBean().setProcuremntCategory(
						getParameterDesc(procurementCategory));
			}

			/*
			 * List<PmParameterDesc> localParamDesc =
			 * retrieveParameterDescByType
			 * (ParameterTypeConst.PROCUREMENT_CATEGORY); for (PmParameterDesc
			 * paramDesc : localParamDesc) { if
			 * (paramDesc.getPmParameter().getParameterId()
			 * .equals(Long.valueOf(
			 * scQt.getPmProcurementCategory().getParameterId()))) {
			 * 
			 * this.getManagedBean().setProcuremntCategory(
			 * paramDesc.getPmParameter().getParameterCode()); }
			 * 
			 * }
			 */
		} finally {

		}
	}

	public void retriveTechnicalCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSpecType("1");
			reqDTO.setCheckListTypeCode(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST);
			procurementCategoryCode(this.getManagedBean().getScQt());
			// this.getManagedBean().getProcuremntCategory();
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			List<ScQtCheckList> checkListList = new ArrayList<ScQtCheckList>();
			if (this.getManagedBean().getScQtCheckSt2techProposal() == null) {
				resDTO = specSo.retrieveCheckLists(reqDTO);
				if (resDTO.getDocumentResultBean() != null
						&& resDTO.getDocumentResultBean().size() > 0) {
					// List<PmDocument>
					// document=(List<PmDocument>)resDTO.getDocumentResultBean().get(0);

					for (PmDocument pmDoc : resDTO.getDocumentResultBean()) {
						ScQtCheckList checkList = new ScQtCheckList();
						checkList.setTitle(pmDoc.getDocName());
						checkList.setDefinationType(pmDoc
								.getDocDefinitionTypeCode());
						checkList.setManditory(true);
						checkList.setScoringReq(true);
						checkList.setScQt(this.getManagedBean().getScQt());
						checkList.setQtScoreWeightage(new ScQtScoreWeightage());
						checkList.setAction(pmDoc.getChecklistActionCode());
						checkList.setPmDocument(pmDoc);
						checkList.setDocCategory("1");
						PmStatus status = new PmStatus();
						status.setStatusId(1L);
						checkList.setPmStatus(status);
						checkList.setChecklistSeq(1);
						checkListList.add(checkList);
					}

				}
				if (resDTO.getSpecResultBean() != null
						&& resDTO.getSpecResultBean().getTotalRecord() > 0) {
					for (ScQtCheckList checklist : resDTO.getSpecResultBean()
							.getResultList()) {
						if (checklist.getQtScoreWeightage() == null) {
							checklist
									.setQtScoreWeightage(new ScQtScoreWeightage());
						}

						checkListList.add(checklist);
					}

				}
				this.getManagedBean()
						.setScQtCheckSt2techProposal(checkListList);
			}

		} finally {
			specSo = null;
		}
	}

	public void retriveFinancialCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			// this.getManagedBean().getScQt().getQtId()
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setSpecType("2");
			reqDTO.setCheckListTypeCode(ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST);
			List<ScQtCheckList> checkListList = new ArrayList<ScQtCheckList>();
			// this.getManagedBean().getProcuremntCategory();
			procurementCategoryCode(this.getManagedBean().getScQt());
			// this.getManagedBean().getProcuremntCategory();
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			if (this.getManagedBean().getScQtCheckSt2financeProposalList() == null) {
				resDTO = specSo.retrieveCheckLists(reqDTO);
				if (resDTO.getDocumentResultBean() != null
						&& resDTO.getDocumentResultBean().size() > 0) {

					for (PmDocument pmDoc : resDTO.getDocumentResultBean()) {
						ScQtCheckList checkList = new ScQtCheckList();
						checkList.setTitle(pmDoc.getDocName());
						checkList.setDefinationType(pmDoc
								.getDocDefinitionTypeCode());
						checkList.setManditory(true);
						checkList.setChecklistSeq(1);
						checkList.setScQt(this.getManagedBean().getScQt());
						checkList.setAction(pmDoc.getChecklistActionCode());
						checkList.setDocCategory("2");
						checkList.setScoringReq(true);
						checkList.setPmDocument(pmDoc);
						checkList.setQtScoreWeightage(new ScQtScoreWeightage());
						PmStatus status = new PmStatus();
						status.setStatusId(1L);
						checkList.setPmStatus(status);
						checkListList.add(checkList);
					}
				}
				if (resDTO.getSpecResultBean() != null
						&& resDTO.getSpecResultBean().getTotalRecord() > 0) {
					for (ScQtCheckList checklist : resDTO.getSpecResultBean()
							.getResultList()) {
						if (checklist.getQtScoreWeightage() == null) {
							checklist
									.setQtScoreWeightage(new ScQtScoreWeightage());
						}

						checkListList.add(checklist);
					}
				}
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						checkListList);
			}

		} finally {
			specSo = null;
		}
	}

	public String retriveTechnicalScoring() {
		IQTSpecSO specSo = null;
		try {
			this.getManagedBean().setTabActiveIndex(4);
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

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			// reqDTO.setSpecType("T");
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(1);
			settinglist.add(3);
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationResDTO resDTO = specSo
					.retrieveScoreSettingDtls(reqDTO);

			if (resDTO.getScoreSettingResultBean().getResultList() != null
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setTechnicalScoreSetting(
						new ScQtScoreSetting());
				this.getManagedBean().getTechnicalScoreSetting()
						.setSettingType(1);

				setUserDetails(this.getManagedBean().getTechnicalScoreSetting());
				this.getManagedBean().getTechnicalScoreSetting()
						.setScQt(this.getManagedBean().getScQt());
				this.getManagedBean().setDemoScoreSetting(
						new ScQtScoreSetting());
				this.getManagedBean().getDemoScoreSetting().setSettingType(3);
				setUserDetails(this.getManagedBean().getDemoScoreSetting());
				this.getManagedBean().getDemoScoreSetting()
						.setScQt(this.getManagedBean().getScQt());
				this.getManagedBean().getDemoScoreSetting()
						.setWeightageReq(false);

			}

			else {

				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == 1) {
						this.getManagedBean().setRecommendationPackageScore(
								recommnendationSetting);
						this.getManagedBean().setTechnicalScoreSetting(
								recommnendationSetting);
					}

					if (recommnendationSetting.getSettingType() == 3) {
						this.getManagedBean().setDemoScoreSetting(
								recommnendationSetting);
					}

				}
			}

			if (checklist.size() != technicalScoring.size()) {

				List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
				int score = 0;
				float addScore = 0;
				for (ScQtCheckList list : checklist) {

					if (list.getQtScoreWeightage().getScore() == null) {
						list.getQtScoreWeightage().setScore(new BigDecimal(0));

					}
					list.getQtScoreWeightage().setChangedBy(1L);
					list.getQtScoreWeightage().setCreatedBy(1L);
					list.getQtScoreWeightage().setCreatedDate(
							new Timestamp(System.currentTimeMillis()));
					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();
					score = (int) addScore;

				}

				this.getManagedBean().setTechPoints(score);

				this.getManagedBean().setTechnicalScoring(checklist);
			}

		} finally {
			specSo = null;
		}
		return "weightageGroup.xhtml";
	}

	public String retriveFinancialScoring() {
		IQTSpecSO specSo = null;
		try {
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

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(2);

			// reqDTO.setSpecType("T");
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationResDTO resDTO = specSo
					.retrieveScoreSettingDtls(reqDTO);

			if (resDTO.getScoreSettingResultBean().getResultList() != null
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				this.getManagedBean().setFinancialScoreSetting(
						new ScQtScoreSetting());
				this.getManagedBean().getFinancialScoreSetting()
						.setSettingType(2);
				this.getManagedBean().getFinancialScoreSetting()
						.setScQt(this.getManagedBean().getScQt());

				setUserDetails(this.getManagedBean().getFinancialScoreSetting());

			} else {
				for (ScQtScoreSetting scoreSttting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					this.getManagedBean()
							.setFinancialScoreSetting(scoreSttting);
				}

			}

			if (checklist.size() != financialScoring.size()) {
				List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
				ScQtCheckList tmpCheckList = new ScQtCheckList();
				List<ScQtCheckList> specChecklist = new ArrayList<ScQtCheckList>();
				int score = 0;
				float addScore = 0;
				// String localTitle="";
				StringBuffer sb = new StringBuffer();
				for (ScQtCheckList list : checklist) {

					if (list.getQtScoreWeightage().getScore() == null) {
						list.getQtScoreWeightage().setScore(new BigDecimal(0));
					}
					list.getQtScoreWeightage().setChangedBy(1L);
					list.getQtScoreWeightage().setCreatedBy(1L);
					list.getQtScoreWeightage().setCreatedDate(
							new Timestamp(System.currentTimeMillis()));
					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();

					if (list.getDefinationType().getParameterId() == 185) {
						// String Title=list.getTitle();
						// =localTitle.concat(Title)+"<br/>";

						sb.append(list.getTitle());
						sb.append(",");

						specChecklist.add(list);
					} else {
						localChecklist.add(list);
					}

					int local = (int) addScore;
					this.getManagedBean().setFinPoints(local);
				} //
					// ScQtCheckList schecklist = specChecklist.get(0);
				if (null != specChecklist && specChecklist.size() > 0) {
					tmpCheckList.setQtScoreWeightage(specChecklist.get(0)
							.getQtScoreWeightage());
					if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {
						getManagedBean().setScoreVarience(
								specChecklist.get(0).getQtScoreWeightage()
										.getScore());
					}
				}
				tmpCheckList.setTitle(sb.toString());
				localChecklist.add(0, tmpCheckList);
				if (null == tmpCheckList.getQtScoreWeightage()
						|| tmpCheckList.getQtScoreWeightage().getScore() == null) {

					this.getManagedBean().setScQtChecklist(tmpCheckList);
					// String title=localTitle.substring(0,
					// localTitle.length());
					// String lineSep = System.getProperty("line.separator");

					ScQtScoreWeightage qtScoreWeightage = new ScQtScoreWeightage();
					qtScoreWeightage.setScore(new BigDecimal(0));
					setUserDetails(qtScoreWeightage);

					tmpCheckList.setQtScoreWeightage(qtScoreWeightage);
				}
				List<ScQtScoreOfferPrice> offerPrice = tmpCheckList
						.getQtScoreWeightage().getScQtScoreOfferPrices();
				if (null == offerPrice || offerPrice.isEmpty()) {

					offerPrice = new ArrayList<ScQtScoreOfferPrice>();
					this.getManagedBean().setOfferPricePackage(
							new ScQtScoreOfferPrice());
					this.getManagedBean()
							.getOfferPricePackage()
							.setScQtScoreWeightage(
									tmpCheckList.getQtScoreWeightage());
					this.getManagedBean().getOfferPricePackage()
							.setOfferPriceType("P");

					setUserDetails(this.getManagedBean().getOfferPricePackage());
					offerPrice
							.add(this.getManagedBean().getOfferPricePackage());
					// this.getManagedBean().getScQtChecklist().getQtScoreWeightage().getScQtScoreOfferPrices()

					this.getManagedBean().setOfferPriceItem(
							new ScQtScoreOfferPrice());
					this.getManagedBean().getOfferPriceItem()
							.setOfferPriceType("I");
					this.getManagedBean()
							.getOfferPriceItem()
							.setScQtScoreWeightage(
									tmpCheckList.getQtScoreWeightage());
					setUserDetails(this.getManagedBean().getOfferPriceItem());
					offerPrice.add(this.getManagedBean().getOfferPriceItem());
					tmpCheckList.getQtScoreWeightage().setScQtScoreOfferPrices(
							offerPrice);

				} else {
					for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList
							.getQtScoreWeightage().getScQtScoreOfferPrices()) {
						if ("P".equalsIgnoreCase(scoreOfferPrice
								.getOfferPriceType())) {
							this.getManagedBean().setOfferPricePackage(
									scoreOfferPrice);
						} else if ("I".equalsIgnoreCase(scoreOfferPrice
								.getOfferPriceType())) {
							this.getManagedBean().setOfferPriceItem(
									scoreOfferPrice);
						}
					}

				}

				this.getManagedBean().setFinSpecList(specChecklist);
				this.getManagedBean().setFinancialScoring(localChecklist);

			}
		} finally {
			specSo = null;
		}
		return "financialScoring.xhtml";

	}

	public String retriveRecommendationDetails() {
		IQTSpecSO specSo = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			// reqDTO.setSpecType("T");

			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(4);
			settinglist.add(5);
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationResDTO resDTO = specSo
					.retrieveScoreSettingDtls(reqDTO);

			if (resDTO.getScoreSettingResultBean().getResultList() != null
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {

				this.getManagedBean().setRecommendationPackageScore(
						new ScQtScoreSetting());
				this.getManagedBean().getRecommendationPackageScore()
						.setSettingType(4);
				this.getManagedBean().getRecommendationPackageScore()
						.setScQt(this.getManagedBean().getScQt());
				this.getManagedBean().getRecommendationPackageScore()
						.setWeightageReq(false);

				setUserDetails(this.getManagedBean()
						.getRecommendationPackageScore());

				this.getManagedBean().setRecommendationScore(
						new ScQtScoreSetting());

				this.getManagedBean().getRecommendationScore()
						.setSettingType(5);
				this.getManagedBean().getRecommendationScore()
						.setScQt(this.getManagedBean().getScQt());
				this.getManagedBean().getRecommendationScore()
						.setWeightageReq(false);
				setUserDetails(this.getManagedBean().getRecommendationScore());

			} else {

				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == 4) {
						this.getManagedBean().setRecommendationPackageScore(
								recommnendationSetting);
					}

					if (recommnendationSetting.getSettingType() == 5) {
						this.getManagedBean().setRecommendationScore(
								recommnendationSetting);
					}

				}

			}

		} finally {
			specSo = null;
		}
		return "recomondation.xhtml";
	}

	public void retriveFulfilmentDtls() {
		IQTSpecSO specSo = null;
		try {
			this.getManagedBean().getScQt().setScheduleType("F");
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();
			if (scheduleList == null) {
				scheduleList = new ArrayList<ScQtSchedule>();
			}

			List<ScQtProgressivePayment> paymentList = this.getManagedBean()
					.getProgressivePaymentList();
			if (paymentList == null) {
				paymentList = new ArrayList<ScQtProgressivePayment>();
			}

			if (null != resDTO.getScheduleResults()
					&& null != resDTO.getScheduleResults().getResultList()
					&& !resDTO.getScheduleResults().getResultList().isEmpty()) {

				this.getManagedBean().setTotalFrequency(
						resDTO.getScheduleResults().getResultList().size());
				String startMonth = "";
				String endMonth = "";
				for (ScQtSchedule scQtSchedule : resDTO.getScheduleResults()
						.getResultList()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");

					String fromdate = dateFormat.format(scQtSchedule
							.getFromDate());
					String todate = dateFormat.format(scQtSchedule.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6, fromdate.length());
					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());

					if (startdate.equalsIgnoreCase("01")) {
						startMonth = "0";
					}
					if (startdate.equalsIgnoreCase("02")) {
						startMonth = "1";
					}
					if (startdate.equalsIgnoreCase("03")) {
						startMonth = "2";
					}
					if (startdate.equalsIgnoreCase("04")) {
						startMonth = "3";
					}
					if (startdate.equalsIgnoreCase("05")) {
						startMonth = "4";
					}
					if (startdate.equalsIgnoreCase("06")) {
						startMonth = "5";
					}

					if (startdate.equalsIgnoreCase("07")) {
						startMonth = "6";
					}
					if (startdate.equalsIgnoreCase("08")) {
						startMonth = "7";
					}
					if (startdate.equalsIgnoreCase("09")) {
						startMonth = "8";
					}
					if (startdate.equalsIgnoreCase("10")) {
						startMonth = "9";
					}
					if (startdate.equalsIgnoreCase("11")) {
						startMonth = "10";
					}
					if (startdate.equalsIgnoreCase("12")) {
						startMonth = "11";
					}

					if (enddate.equalsIgnoreCase("01")) {
						endMonth = "0";
					}
					if (enddate.equalsIgnoreCase("02")) {
						endMonth = "1";
					}
					if (enddate.equalsIgnoreCase("03")) {
						endMonth = "2";
					}
					if (enddate.equalsIgnoreCase("04")) {
						endMonth = "3";
					}
					if (enddate.equalsIgnoreCase("05")) {
						endMonth = "4";
					}
					if (enddate.equalsIgnoreCase("06")) {
						endMonth = "5";
					}

					if (enddate.equalsIgnoreCase("07")) {
						endMonth = "6";
					}
					if (enddate.equalsIgnoreCase("08")) {
						endMonth = "7";
					}
					if (enddate.equalsIgnoreCase("09")) {
						endMonth = "8";
					}
					if (enddate.equalsIgnoreCase("11")) {
						endMonth = "10";
					}
					if (enddate.equalsIgnoreCase("12")) {
						endMonth = "11";
					}
					int start = Integer.parseInt(startMonth);
					int end = Integer.parseInt(endMonth);
					int count = 0;
					for (int i = start + 1; i <= end + 1; i++) {
						count++;
					}

					scQtSchedule.setStartMonth(startMonth);
					scQtSchedule.setEndMonth(endMonth);
					scQtSchedule.setStartYear(startYear);
					scQtSchedule.setEndYear(endYear);
					scQtSchedule.setTotalMonths(new BigDecimal(count));
					scheduleList.add(scQtSchedule);

					this.getManagedBean().setScheduleList(scheduleList);

				}
			}

			// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());

			if (null != resDTO.getProgressivePaymentResults()
					&& null != resDTO.getProgressivePaymentResults()
							.getResultList()
					&& !resDTO.getProgressivePaymentResults().getResultList()
							.isEmpty()) {

				this.getManagedBean().setTotalProgressivePayment(
						resDTO.getProgressivePaymentResults().getResultList()
								.size());
				String startMonth = "";
				String endMonth = "";
				for (ScQtProgressivePayment payment : resDTO
						.getProgressivePaymentResults().getResultList()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");

					String fromdate = dateFormat.format(payment.getFromDate());
					String todate = dateFormat.format(payment.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6, fromdate.length());
					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6, todate.length());

					if (startdate.equalsIgnoreCase("01")) {
						startMonth = "0";
					}
					if (startdate.equalsIgnoreCase("02")) {
						startMonth = "1";
					}
					if (startdate.equalsIgnoreCase("03")) {
						startMonth = "2";
					}
					if (startdate.equalsIgnoreCase("04")) {
						startMonth = "3";
					}
					if (startdate.equalsIgnoreCase("05")) {
						startMonth = "4";
					}
					if (startdate.equalsIgnoreCase("06")) {
						startMonth = "5";
					}

					if (startdate.equalsIgnoreCase("07")) {
						startMonth = "6";
					}
					if (startdate.equalsIgnoreCase("08")) {
						startMonth = "7";
					}
					if (startdate.equalsIgnoreCase("09")) {
						startMonth = "8";
					}
					if (startdate.equalsIgnoreCase("10")) {
						startMonth = "9";
					}
					if (startdate.equalsIgnoreCase("11")) {
						startMonth = "10";
					}
					if (startdate.equalsIgnoreCase("12")) {
						startMonth = "11";
					}

					if (enddate.equalsIgnoreCase("01")) {
						endMonth = "0";
					}
					if (enddate.equalsIgnoreCase("02")) {
						endMonth = "1";
					}
					if (enddate.equalsIgnoreCase("03")) {
						endMonth = "2";
					}
					if (enddate.equalsIgnoreCase("04")) {
						endMonth = "3";
					}
					if (enddate.equalsIgnoreCase("05")) {
						endMonth = "4";
					}
					if (enddate.equalsIgnoreCase("06")) {
						endMonth = "5";
					}

					if (enddate.equalsIgnoreCase("07")) {
						endMonth = "6";
					}
					if (enddate.equalsIgnoreCase("08")) {
						endMonth = "7";
					}
					if (enddate.equalsIgnoreCase("09")) {
						endMonth = "8";
					}
					if (enddate.equalsIgnoreCase("10")) {
						endMonth = "9";
					}
					if (enddate.equalsIgnoreCase("11")) {
						endMonth = "10";
					}
					if (enddate.equalsIgnoreCase("12")) {
						endMonth = "11";
					}
					payment.setStartMonth(startMonth);
					payment.setEndMonth(endMonth);
					payment.setStartYear(startYear);
					payment.setEndYear(endYear);
					paymentList.add(payment);

				}
				this.getManagedBean().setProgressivePaymentList(paymentList);
			}

		} finally {
			specSo = null;
		}

	}

	public void retriveDeliveryDetails() {
		IQTSpecSO specSo = null;
		try {
			retriveStateList();
			retriveMinistryList();

			if (this.getManagedBean().getDeliveryNonZoneList() == null
					&& null == this.getManagedBean().getDeliveryLocBean()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				SpecificationResDTO resDTO = specSo
						.retrieveDeliverSeriviceAddress(reqDTO);
				if (resDTO.getSpecZoneDtls() == null
						|| resDTO.getSpecZoneDtls().isEmpty()) {
					List<ScQtDeliveryLoc> deliveryLoc = this.getManagedBean()
							.getDeliveryNonZoneList();
					if (deliveryLoc == null) {
						deliveryLoc = new ArrayList<ScQtDeliveryLoc>();
					}

					this.getManagedBean().setDeliveryNonZoneList(
							resDTO.getAddressResultBean());

				} else {

					this.getManagedBean().setDeliveryZones(
							resDTO.getSpecZoneDtls());

					for (TplSpecZoneDtl tplSpecZoneDtl : this.getManagedBean()
							.getDeliveryZones()) {

						reqDTO.setQtId(this.getManagedBean().getScQt()
								.getQtId());
						reqDTO.setZoneId(tplSpecZoneDtl.getSpecZoneId());
						resDTO = specSo.retrieveDeliveryAddressDtls(reqDTO);
						if (this.getManagedBean().getDeliveryLocBean() == null) {
							this.getManagedBean().setDeliveryLocBean(
									new ArrayList());
						}
						BriefingZoneBean bean = new BriefingZoneBean(
								tplSpecZoneDtl, resDTO.getAddressResultBean());
						this.getManagedBean().getDeliveryLocBean().add(bean);
					}
				}
			}
		} finally {
			specSo = null;
		}
	}

	public void retriveStateList() {
		IStateSO stateSo = null;
		StateResDTO resDto = null;

		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1L);
			stateSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			resDto = stateSo.retriveStatesListByCountryID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setStateList(resDto.getStateList());
			}

		} finally {
			stateSo = null;
		}

	}

	public void districtBystate(AjaxBehaviorEvent event) {
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			Long stateId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (stateId != null) {
				List<PmState> stateList = this.getManagedBean().getStateList();
				for (PmState sate : stateList) {
					if (sate.getStateId().equals(stateId)) {
						this.getManagedBean().setStateCode(sate.getStateCode());

					}
				}
			}

			reqDTO.setStateId(stateId);
			districtSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			resDto = districtSo.retriveDistrictListByStateID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setDistrictList(resDto.getDistrictList());
			}

		} finally {
			districtSo = null;
		}

	}

	public void retriveMinistryList() {
		IOrgProfileSO orgProfileSO = null;
		try {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			reqDTO.setParentOrgProfileId(null);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			if (resDTO.getOrgProfileList() != null) {
				this.getManagedBean().setMinistryList(
						resDTO.getOrgProfileList());
			}
		} finally {
			orgProfileSO = null;
		}
	}

	public void dePartMentByMinistry(AjaxBehaviorEvent event) {
		IOrgProfileSO orgProfileSO = null;
		try {
			Long ministryId = (Long) event.getComponent().getAttributes()
					.get("value");
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			reqDTO.setParentOrgProfileId(ministryId);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			if (resDTO.getOrgProfileList() != null) {
				this.getManagedBean().setDepartmentList(
						resDTO.getOrgProfileList());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	public void ptjByDepartMent(AjaxBehaviorEvent event) {
		IOrgProfileSO orgProfileSO = null;
		try {
			Long departmentd = (Long) event.getComponent().getAttributes()
					.get("value");
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(departmentd);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			if (resDTO.getOrgProfileList() != null) {
				this.getManagedBean().setPtjtList(resDTO.getOrgProfileList());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	public void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType("SC");
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);

			this.getManagedBean().setCommitteeMemberList(
					resDTO.getCommMemberResultBean().getResultList());

		} finally {
			committeSO = null;
			resDTO = null;
		}
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

	/**
	 * Method to edit the zones By BO..
	 * 
	 * @param rootZoneLocation
	 * @param index
	 */
	public void editZoneLocation(TreeNode rootZoneLocation, int index) {
		List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);
		Long locationLevel = this.getManagedBean().getScQt()
				.getZoneLocationLevel().getParameterId();
		if (locationLevel == null) {
			locationLevel = 121L;
		}

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
		String locationLevelCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(locationLevel)) {
				locationLevelCode = paramDesc.getCodeName();
				break;
			}
		}

		// ScQuoteZone zone = getZone(rootZoneLocation);
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		getManagedBean().setRootLocationTree(
				LocationSingleton.getInstance().buildLocationPopupTree(1L,
						states, districts, divisions, cities,
						locationLevelCode, ScQtLocCriteria.class));
		int i = index;

		this.getManagedBean().setIndex(i);

		// getManagedBean().setZoneName(zone.getZoneName());

	}

	public void editBusinessOwnerZoneLocation(TreeNode rootZoneLocation,
			int index) {
		List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

		TplSpecZoneDtl zone = getZone(rootZoneLocation);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);
		getManagedBean().setRootZoanlTree(
				LocationSingleton.getInstance().buildLocationPopupTree(1L,
						states, districts, divisions, cities,
						ParameterConst.LOCATION_LEVEL_DISTRICT,
						TplSpecZoneLoc.class));
		int i = index;

		this.getManagedBean().setIndex(i);
		getManagedBean().setZoneName(
				locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());

	}

	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
	}

	public void technicalSpecBoValidate() {
		getManagedBean().setRequired(Boolean.TRUE);
	}

	public SelectItem[] getDropdownLocationValues(String locationLevel) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	public void showZoneLocationPopup() {
		if (null == this.getManagedBean().getSupplierCriteria()
				.getLocationLevel()) {
			this.getManagedBean().getSupplierCriteria()
					.setLocationLevel(new PmParameter());
		}

		Long locationLevel = this.getManagedBean().getSupplierCriteria()
				.getLocationLevel().getParameterId();

		if (locationLevel == null) {
			locationLevel = 121L;
		}

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
		String parameterCode = "";
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(locationLevel)) {

				getManagedBean().setRootLocationTree(
						LocationSingleton.getInstance().buildLocationPopupTree(
								1L, null, null, null, null,
								paramDesc.getPmParameter().getParameterCode(),
								ScQtLocCriteria.class));
			}

		}
	}

	public void showGeneralZoneLocationPopup() {

		Long locationLevel = this.getManagedBean().getScQt()
				.getZoneLocationLevel().getParameterId();

		if (locationLevel == null) {
			locationLevel = 121L;
		}

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
		String parameterCode = "";
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(locationLevel)) {

				getManagedBean().setRootZoanlTree(
						LocationSingleton.getInstance().buildLocationPopupTree(
								1L, null, null, null, null,
								paramDesc.getPmParameter().getParameterCode(),
								TplSpecZoneLoc.class));
			}

		}

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

				else {

					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					locationDTOs.addAll(locationDTO);
				}
			}
		}

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(locationDTOs);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(locationDTOs);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(locationDTOs);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(locationDTOs);

		if (getManagedBean().getIndex() == -1) {

			getManagedBean().getZoneTrees().add(
					LocationSingleton.getInstance().buildLocationTree(null,
							states, districts, divisions, cities,
							ScQtLocCriteria.class));
		}

		if (getManagedBean().getIndex() >= 0) {
			getManagedBean().getZoneTrees().remove(getManagedBean().getIndex());
			getManagedBean().getZoneTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildLocationTree(null,
							states, districts, divisions, cities,
							ScQtLocCriteria.class));
		}
		this.getManagedBean().setIndex(-1);

	}

	public void updateZoneGeneralLocation() {

		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (!treeNode.getParent().isSelected()) {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);

					allNodes.addAll(locationDTO);
				}

				else {
					List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);

					allNodes.addAll(locationDTO);
				}

			}
		}

		TplSpecZoneDtl zone = new TplSpecZoneDtl();

		zone.setZoneName(getManagedBean().getZoneName());
		getManagedBean().setZoneName(null);

		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(allNodes);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(allNodes);

		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(allNodes);

		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(allNodes);

		if (getManagedBean().getIndex() == -1) {

			getManagedBean().getZoneGeneralTrees().add(
					LocationSingleton.getInstance().buildLocationTree(zone,
							states, districts, divisions, cities,
							TplSpecZoneLoc.class));

		}

		else if (getManagedBean().getIndex() >= 0) {

			getManagedBean().getZoneGeneralTrees().remove(
					getManagedBean().getIndex());
			getManagedBean().getZoneGeneralTrees().add(
					getManagedBean().getIndex(),
					LocationSingleton.getInstance().buildLocationTree(zone,
							states, districts, divisions, cities,
							TplSpecZoneLoc.class));
		}
		this.getManagedBean().setIndex(-1);

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
		// score.setPercentFrom(new BigDecimal(this.getManagedBean()
		// .getFromJulat()));
		// score.setPercentTo(new
		// BigDecimal(this.getManagedBean().getToJulat()));
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
		// score.setPercentFrom(new BigDecimal(diff));
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
		// score.setRangeTo(new BigDecimal(diff));
		packFromScoring.add(score);
		getManagedBean().getPackScoringList().remove(index);
		this.getManagedBean().setPackScoringList(packFromScoring);

	}

	/**
	 * Method to construct spec details to save...
	 * 
	 * @return
	 */
	private QuationTenderReqDTO prepareSpecReq() {
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();

		// ScQtSpec scqtSpec = this.getManagedBean().getScQtSpec();
		ScQt scQt = this.getManagedBean().getScQt();
		// scQt.setItemType("P");
		if (scQt.getEstimatedAmount().intValue() < 20000) {
			throw new EpException(MessageConst.ERROR_100022);
			/*
			 * FacesContext .getCurrentInstance() .addMessage( null, new
			 * FacesMessage( FacesMessage.SEVERITY_ERROR, null, MessageUtils
			 * .getMessage( MessageConst.ERROR_100022, new String[] {
			 * "Amount Must Be Greater Then 20000", },
			 * MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
			 * .getUserLocale())));
			 */
		}

		if (!scQt.isPanel()) {
			
			if (!scQt.isZonal()) {
				scQt.setZoneLocationLevel(null);
				List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
						this.getManagedBean().getZoneTrees(), null);
				if (locCriteria != null && !locCriteria.isEmpty()) {
					this.getManagedBean().getSupplierCriteria()
							.setScQtLocCriteria(locCriteria);

				}
				ScQtSupplierCriteria qtSupplierCriteria = this.getManagedBean()
						.getSupplierCriteria();

				this.getManagedBean().getSupplierCriteria()
						.setQtZoneCriterias(null);
				if (null != qtSupplierCriteria
						&& null != qtSupplierCriteria.getLocationLevel()
						&& null == qtSupplierCriteria.getLocationLevel()
								.getParameterId()) {

					// qtSupplierCriteria.setLocationLevel(null);
					throw new EpException(MessageConst.ERROR_100022);
					/*FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_100022,
															new String[] { "Location Level", },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));*/
				}

			} else {
				List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
						this.getManagedBean().getZoneGeneralTrees(), null);
				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					if (null == this.getManagedBean().getSupplierCriteria()
							.getQtZoneCriterias()) {
						this.getManagedBean().getSupplierCriteria()
								.setQtZoneCriterias(new ScQtZoneCriteria());
					}
					ScQtZoneCriteria zoneCriteria = this.getManagedBean()
							.getSupplierCriteria().getQtZoneCriterias();
					zoneCriteria.setScQtSupplierCriteria(this.getManagedBean()
							.getSupplierCriteria());
					setUserDetails(zoneCriteria);
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
						zoneLocal.add(tplSpecZoneDtl);
					}
					zoneCriteria.setTplSpecZoneDtls(zoneLocal);

					this.getManagedBean().getSupplierCriteria()
							.setQtZoneCriterias(zoneCriteria);

				}

				else {
					throw new EpException(MessageConst.ERROR_100022);
					/*FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_100022,
															new String[] { "Zone Location", },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));*/
				}
				this.getManagedBean().getSupplierCriteria()
						.setScQtLocCriteria(null);
				if (null != scQt.getZoneLocationLevel()
						&& null != scQt.getZoneLocationLevel().getParameterId()) {
					this.getManagedBean().getSupplierCriteria()
							.setLocationLevel(scQt.getZoneLocationLevel());
				} else {
					// scQt.setZoneLocationLevel(null);
					/*FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											null,
											MessageUtils
													.getMessage(
															MessageConst.ERROR_100022,
															new String[] { "Location Level", },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));*/
					throw new EpException(MessageConst.ERROR_100022);
				}

			}

			this.getManagedBean().getSupplierCriteria()
					.setScQt(this.getManagedBean().getScQt());
			setUserDetails(this.getManagedBean().getSupplierCriteria());
			scQt.setScQtSupplierCriteria(this.getManagedBean()
					.getSupplierCriteria());

			if ("O".equalsIgnoreCase(scQt.getQtType())) {

				List<ScQtCategory> openList = this.getManagedBean()
						.getSuppCriteriaopenList();
				updateCategoryCodesToQt(openList, scQt);
			}

			else if ("R".equalsIgnoreCase(scQt.getQtType())) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getSuppCriteriarestrictedList();
				updateCategoryCodesToQt(openList, this.getManagedBean()
						.getScQt());

			}
		} else {
			

			if (null != this.getManagedBean().getCtPanelZones()
					&& !this.getManagedBean().getCtPanelZones().isEmpty()) {
				if (null == this.getManagedBean().getSupplierCriteria()
						.getQtZoneCriterias()) {
					this.getManagedBean().getSupplierCriteria()
							.setQtZoneCriterias(new ScQtZoneCriteria());
				}
				ScQtZoneCriteria zoneCriteria = this.getManagedBean()
						.getSupplierCriteria().getQtZoneCriterias();
				zoneCriteria.setScQtSupplierCriteria(this.getManagedBean()
						.getSupplierCriteria());
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (CtPanelZone panelzone : this.getManagedBean()
						.getCtPanelZones()) {
					TplSpecZoneDtl zoneDtl = new TplSpecZoneDtl();
					setUserDetails(zoneDtl);
					zoneDtl.setTplSpecZone(zoneCriteria);
					zoneDtl.setPanelZone(panelzone);
					zoneLocal.add(zoneDtl);
				}
				zoneCriteria.setTplSpecZoneDtls(zoneLocal);

				this.getManagedBean().getSupplierCriteria()
						.setQtZoneCriterias(zoneCriteria);

			}
			this.getManagedBean().getSupplierCriteria()
					.setCtPanelItem(this.getManagedBean().getCtPanelItem());
			this.getManagedBean().getSupplierCriteria()
					.setScQt(this.getManagedBean().getScQt());
			setUserDetails(this.getManagedBean().getSupplierCriteria());
			scQt.setScQtSupplierCriteria(this.getManagedBean()
					.getSupplierCriteria());
			scQt.setZoneLocationLevel(null);
			this.getManagedBean().getSupplierCriteria().setLocationLevel(null);
			scQt.setZonal(true);

		}

		List<ScQtRemark> remarksList = this.getManagedBean()
				.getScQtRemarksList();
		List<ScQtRemark> specRemarks = null;
		if (isNotNullOREmpty(remarksList)) {
			specRemarks = new ArrayList<ScQtRemark>();
			for (ScQtRemark remarks : remarksList) {
				if (remarks.getRemark() != null && "" != remarks.getRemark()) {
					specRemarks.add(remarks);
				}

			}

		}
		scQt.setScQtRemarks(specRemarks);
		scQt.setScQtCommittees(null);
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
				// retriveSpecdetails(1366L);
				// retriveSpecdetails(1426L);
			}

		} finally {

		}
	}

	public String retriveSpecdetails(Long qtId) {
		IQTDetailSO QtDetailsSO;

		try {
			this.getManagedBean().setActiveIndex(0);
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(qtId);
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			if (tenderResDTO.getQtDetail() != null) {
				getManagedBean().setScQt(tenderResDTO.getQtDetail());

				if (null == tenderResDTO.getQtSupplierCriteria()) {

					ScQtSupplierCriteria qtSupplierCriteria = new ScQtSupplierCriteria();
					qtSupplierCriteria
							.setQtZoneCriterias(new ScQtZoneCriteria());
					this.getManagedBean().setSupplierCriteria(
							qtSupplierCriteria);

					if (null == this.getManagedBean().getSupplierCriteria()
							.getLocationLevel()) {
						this.getManagedBean().getSupplierCriteria()
								.setLocationLevel(new PmParameter());
					}

				} else {
					this.getManagedBean().setSupplierCriteria(
							tenderResDTO.getQtSupplierCriteria());
					if (null == this.getManagedBean().getSupplierCriteria()
							.getLocationLevel()) {
						this.getManagedBean().getSupplierCriteria()
								.setLocationLevel(new PmParameter());
					}

				}
				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (tenderResDTO.getQtDetail().isZonal()) {
						
						if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
							for (TplSpecZoneDtl zone : tenderResDTO
									.getZoneDetails()) {
								
								getManagedBean()
										.getZoneGeneralTrees()
										.add(LocationSingleton
												.getInstance()
												.constuctTreeNode(
														zone,
														zone.getTplSpecZoneLocs()));

							}
						}
					}
				}
			}

		} finally {
			QtDetailsSO = null;
		}
		return "specificationCreation.xhtml";
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
	
	public void changeZones(AjaxBehaviorEvent event){
		boolean zone = (boolean) event.getComponent().getAttributes()
				.get("value");
		if(zone){
			if(null==this.getManagedBean().getScQt().getZoneLocationLevel()){
				this.getManagedBean().getScQt().setZoneLocationLevel(new PmParameter());
			}
			if(null==this.getManagedBean().getSupplierCriteria()){
				this.getManagedBean().setSupplierCriteria(new ScQtSupplierCriteria());
			}
			if(null==this.getManagedBean().getSupplierCriteria().getQtZoneCriterias()){
				this.getManagedBean().getSupplierCriteria().setQtZoneCriterias(new ScQtZoneCriteria());
			}
			
		}
		else{
			
			if(null==this.getManagedBean().getSupplierCriteria()){
				this.getManagedBean().setSupplierCriteria(new ScQtSupplierCriteria());
			}
			
			if (null == this.getManagedBean().getSupplierCriteria()
					.getLocationLevel()) {
				this.getManagedBean().getSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}
		}
		
	}

	public void typeChange(AjaxBehaviorEvent event) {
		Long type = (Long) event.getComponent().getAttributes().get("value");

		try {
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			if (type == 1) {
				selectItems.add(new SelectItem(0, "--Select One--"));
				this.getManagedBean().setTechnicalChecklistAction(null);

				selectItems.add(new SelectItem(5, "Manual"));
				this.getManagedBean().setTechnicalChecklistAction(selectItems);
			}
			if (type == 184) {

				this.getManagedBean().setTechnicalChecklistAction(null);
				selectItems.add(new SelectItem(0, "--Select One--"));
				selectItems.add(new SelectItem(8, "View"));
				selectItems.add(new SelectItem(9, "Online Submit"));
				this.getManagedBean().setTechnicalChecklistAction(selectItems);
			}
			if (type == 2) {

				this.getManagedBean().setTechnicalChecklistAction(null);
				selectItems.add(new SelectItem(0, "--Select One--"));
				selectItems.add(new SelectItem(7, "Presentation/Demo"));
				this.getManagedBean().setTechnicalChecklistAction(selectItems);
			}
		} finally {

		}
		/*
		 * this.getManagedBean().getScQtCheckSt2techProposal().get(0)
		 * .setChecklistType(type);
		 */
	}

	public void checklistActionChange(AjaxBehaviorEvent event) {
		Long type = (Long) event.getComponent().getAttributes().get("value");
		/*
		 * this.getManagedBean().getScQtCheckSt2techProposal().get(0)
		 * .setChecklistAction(type);
		 */
	}

	public void setProcurementMode(ScQt scQt) {
		if (scQt.getEstimatedAmount().intValue() <= 500000) {
			PmParameter quotation = new PmParameter();
			quotation.setParameterId(145L);
			this.getManagedBean().getScQt().setProcurementMode(quotation);
		}

		if (scQt.getEstimatedAmount().intValue() > 500000) {
			PmParameter tender = new PmParameter();
			tender.setParameterId(146L);
			this.getManagedBean().getScQt().setProcurementMode(tender);
		}

	}

	public void fulfilmentChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");

			this.getManagedBean().setFulfilmentType(type.toString());

		} finally {

		}

	}

	public void procurementChange(AjaxBehaviorEvent event) {
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setProcuremntCategory(
							paramDesc.getPmParameter().getParameterCode());
				}

			}
		} finally {

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
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (this.getManagedBean().getScQt().isZonal()) {
			this.getManagedBean().getScQt().getZoneLocationLevel()
					.setParameterId(location);
		} else {
			this.getManagedBean().getSupplierCriteria().getLocationLevel()
					.setParameterId(location);
		}
	}

	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getSupplierCriteria().getLocationLevel()
				.setParameterId(location);
	}

	public void scoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");

		if (scoreType.equalsIgnoreCase("2")) {

			this.getManagedBean().setScQtCheckListScore(
					new ScQtCheckListScore());

		}
		this.getManagedBean().setChecklistScoreType(scoreType);
	}

	public void companyscoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");

		if (scoreType.equalsIgnoreCase("2")) {

			this.getManagedBean()
					.setScQtContractScore(new ScQtCheckListScore());

		}
		this.getManagedBean().setCompanyScoreType(scoreType);
	}

	public void fromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setFromMonth(formvalue);
		int n = Integer.parseInt(this.getManagedBean().getFromMonth());
		int j = n + 2;
		List<BankingBean> monthList = new ArrayList<BankingBean>();
		String monthvalue = "";
		String amount = "";
		for (int i = n; i <= j; i++) {
			if (i == 0) {

				monthvalue = "January 2014";
				amount = "1000";
			} else if (i == 1) {

				monthvalue = "February 2014";
				amount = "2000";

			} else if (i == 2) {

				monthvalue = "March 2014";
				amount = "3000";

			} else if (i == 3) {

				monthvalue = "April 2014";
				amount = "4000";

			} else if (i == 4) {

				monthvalue = "May 2014";
				amount = "5000";

			} else if (i == 5) {

				monthvalue = "June 2014";
				amount = "6000";

			} else if (i == 6) {

				monthvalue = "July 2014";
				amount = "7000";

			} else if (i == 7) {

				monthvalue = "August 2014";
				amount = "8000";

			} else if (i == 8) {

				monthvalue = "September 2014";
				amount = "9000";

			} else if (i == 9) {

				monthvalue = "October 2014";
				amount = "10000";

			} else if (i == 10) {

				monthvalue = "November 2014";
				amount = "11000";

			} else if (i == 11) {

				monthvalue = "December 2014";
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
		this.getManagedBean().setToMonth(String.valueOf(j));
	}

	public String technicalChecklistEditAction(int index,
			ScQtCheckList checkList) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {
			this.getManagedBean().setIndex(index);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (185 == checkList.getDefinationType().getParameterId()) {
				checkList.setScQtCheckListScores(null);
				checkList.setQtScoreWeightage(null);
				this.getManagedBean().setScqtDoc(checkList.getScQtDoc());
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();

				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());

				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

				specBB.retrieveTemplateSpecMaster(templateSpecBean);
				return "/xhtml/sc/main/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);

				if ("TG".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());
						this.getManagedBean().setScoringForYears(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 10) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == 11) {

								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean().setScQtContractScore(
											checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "companyExperience.xhtml?faces-redirect=true";
				} else if ("TN".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setNonFederalScore(
								new ArrayList<ScQtCheckListScore>());
						this.getManagedBean().setNonFederalScoreyears(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 15) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setNonFederalScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == 16) {

								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean()
											.setNonFederalScoreyears(noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean().setScQtContractScore(
											checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}

					return "companyExperiencePrivate.xhtmlfaces-redirect=true";
				}

				else if ("WR".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					// this.getManagedBean().setWarantyScore(null);

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setWarantyScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> noOfWarranty = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 30) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfWarranty.add(checklistScore);
									this.getManagedBean().setWarantyScore(
											noOfWarranty);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "waranty.xhtml?faces-redirect=true";
				}

				else if ("TR".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					// this.getManagedBean().setTrainingScore(null);
					return "training.xhtml?faces-redirect=true";
				} else if ("TL".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setTechnicalStaffScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> technicalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 20) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									technicalEmployee.add(checklistScore);
									this.getManagedBean()
											.setTechnicalStaffScore(
													technicalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "technicalEmployeeList.xhtml?faces-redirect=true";
				} else if ("NE".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setTotalEmployeeScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> totalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 21) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									totalEmployee.add(checklistScore);
									this.getManagedBean()
											.setTotalEmployeeScore(
													totalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}

					return "totalEmployees.xhtml?faces-redirect=true";
				}

			}
		} finally {
			specSo = null;
		}

		return null;

	}

	public String financialChecklistEditAction(int index,
			ScQtCheckList checkList) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {
			this.getManagedBean().setIndex(index);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (185 == checkList.getDefinationType().getParameterId()) {
				checkList.setScQtCheckListScores(null);
				checkList.setQtScoreWeightage(null);
				this.getManagedBean().setScqtDoc(checkList.getScQtDoc());
				TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();

				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());

				templateSpecBean
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

				specBB.retrieveTemplateSpecMaster(templateSpecBean);
				return "/xhtml/sc/main/financialCommitteeTemplate.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);

				if ("FG".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setContractAmount(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 12) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setContractAmount(
											contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "financialGovernementCompany.xhtml?faces-redirect=true";
				} else if ("FN".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setNonFederalContractScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 17) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean()
											.setNonFederalContractScore(
													contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}

					return "financialPrivateCompany.xhtml?faces-redirect=true";

				} else if ("CF".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setCreditFacility(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 51) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									creditFacility.add(checklistScore);
									this.getManagedBean().setCreditFacility(
											creditFacility);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "creditFacility.xhtml?faces-redirect=true";
				}

				else if ("CH".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setScoringCash(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> cashOnHand = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 52) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									cashOnHand.add(checklistScore);
									this.getManagedBean().setScoringCash(
											cashOnHand);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}

					return "cashOnhand.xhtml?faces-redirect=true";
				}

				else if ("BS".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setBankStamentScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 50) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									bankStatement.add(checklistScore);
									this.getManagedBean().setBankStamentScore(
											bankStatement);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "bankStatement.xhtml?faces-redirect=true";
				}

				else if ("AL".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setAssetScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 60) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									assetLiability.add(checklistScore);
									this.getManagedBean().setAssetScore(
											assetLiability);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "assetsLiabilities.xhtml?faces-redirect=true";
				} else if ("SD".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					return "supplierinfo.xhtml?faces-redirect=true";
				} else if ("PC".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {

					reqDTO.setCheckListId(checkList.getQtChecklistId());
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScoreResultBean() == null
							|| resDTO.getScQtCheckListValue() == null) {

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setPaidUpCapitalScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScoreResultBean().getResultList()) {
							if (checklistScore.getCriteriaType() == 70) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									paidUpCapital.add(checklistScore);
									this.getManagedBean()
											.setPaidUpCapitalScore(
													paidUpCapital);
								} else if (checklistScore.getScoreType()
										.intValue() == 2) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							}

						}
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
					}
					return "paidUpCapital.xhtml?faces-redirect=true";
				}
			}
		} finally {
			specSo = null;
		}

		return null;

	}

	public String technicalSpecEditAction(ScQtCheckList checklist) {

		IQTTemplateSO qtTemplateSO;
		try {
			// QTTemplateDocReqDTO qtTemplateReqDTO = new QTTemplateDocReqDTO();

			// QTTemplateDocResDTO resDto = null;

			this.getManagedBean().setScqtDoc(checklist.getScQtDoc());

			// QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();

			/*
			 * try {
			 * 
			 * docReqDTO.setCheckListId(checklist.getQtChecklistId());
			 * qtTemplateSO = BusinessDelegate.getInstance()
			 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
			 * IQTTemplateSO.class);
			 * 
			 * resDto = qtTemplateSO
			 * .retriveTemplateDocDetailsByCheckListId(docReqDTO);
			 * 
			 * } catch (Exception e) { e.printStackTrace(); }
			 */
			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(checklist.getScQtDoc()
					.getTplSpecMaster());

			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

			specBB.retrieveTemplateSpecMaster(templateSpecBean);

			/*
			 * this.getManagedBean() .getScqtDoc() .setTplSpecMaster(
			 * resDto.getScQtCheckList().getScQtDoc() .getTplSpecMaster());
			 * 
			 * this.getManagedBean().setScqtDoc(
			 * resDto.getScQtCheckList().getScQtDoc());
			 */

		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/BusinessOwnerTechnicalSpecEdit.xhtml?faces-redirect=true";

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

			docReqDTO.setCheckListId(checklist.getQtChecklistId());
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			resDto = qtTemplateSO
					.retriveTemplateDocDetailsByCheckListId(docReqDTO);

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

			docReqDTO.setCheckListId(checklist.getQtChecklistId());
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			resDto = qtTemplateSO
					.retriveTemplateDocDetailsByCheckListId(docReqDTO);

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
			this.getManagedBean().getScqtDoc().getTplSpecMaster().getStatus()
					.setStatusId(60730L);
			this.getManagedBean().getScqtDoc().getScQt()
					.setQtNo(scqtDoc.getScQt().getQtNo());

			qtTemplateReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			qtTemplateReqDTO.setDocType("1");
			// qtTemplateReqDTO.set(scqtDoc.getTplSpecMaster());
			// qtTemplateReqDTO.setSpecMasterId(scqtDoc.getTplSpecMaster()
			// .getSpecMasterId());

			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			// resDto = qtTemplateSO
			// .retriveTemplateDocDetails(qtTemplateReqDTO);

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

			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			// resDto = qtTemplateSO
			// .retriveTemplateDocDetails(qtTemplateReqDTO);

		} finally {
			qtTemplateSO = null;
		}

		return "/xhtml/sc/main/technicalCommitteeTemplate.xhtml?faces-redirect=true";
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
		// ScQtSpec scQtSpec = getManagedBean().getScQtSpec();
		ScQtScoreWeightage weightage = new ScQtScoreWeightage();
		setUserDetails(weightage);

		ScQtCheckList additionProposalst1 = new ScQtCheckList();
		additionProposalst1.setQtScoreWeightage(weightage);
		// additionProposalst1.sets

		// additionProposalst1.setScQtSpec(scQtSpec);
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
			additionProposalst1.setDocCategory("2");
			// additionProposalst1.setEvaluationStage("1");
			// additionProposalst1.setChecklistType("UP");
			st1additionalList.add(additionProposalst1);
			// mediumCarsModel = new CarDataModel(st1additionalList);
			this.getManagedBean().setScQtCheckSt1techAdditionalProposalList(
					st1additionalList);
		} finally {

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
			// st1financeproposalList1.setDocType("3");
			// st1financeproposalList1.setEvaluationStage("1");
			// st1financeproposalList1.setChecklistType("UP");
			st1financeproposalList.add(st1financeproposalList1);
			this.getManagedBean().setScQtCheckSt1financeProposalList(
					st1financeproposalList);
		} finally {

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
			// st1financeAdditionalproposalList1.setDocType("4");
			// st1financeAdditionalproposalList1.setEvaluationStage("1");
			// st1financeAdditionalproposalList1.setChecklistType("UP");
			st1financeAdditionalproposalList
					.add(st1financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt1financeAdditionalProposalList(
					st1financeAdditionalproposalList);
		} finally {

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
			// additionProposalst2.setDocType("2");
			// additionProposalst2.setEvaluationStage("2");
			// additionProposalst2.setChecklistType("UP");
			st2additionalList.add(additionProposalst2);
			this.getManagedBean().setScQtCheckSt2techAdditionalProposalList(
					st2additionalList);

		} finally {

		}
	}

	public void stageTwoFiProposalAdd() {
		IStatusSO statussO;

		try {
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList("2");

			// st2financeproposalList2.setDefinationType(1);
			st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2.setDocCategory("2");
			st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2.setChecklistSeq(1);

			st2financeproposalList2.setScQt(this.getManagedBean().getScQt());
			PmParameter parameter = new PmParameter();
			parameter.setParameterId(184L);
			st2financeproposalList2.setDefinationType(parameter);
			st2financeproposalList2.setAction(new PmParameter());
			st2financeproposalList2.setCreatedDate(new Timestamp(System
					.currentTimeMillis()));
			setUserDetails(st2financeproposalList2);
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			st2financeproposalList2.setPmStatus(statusres.getPmStatus());
			// st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList.add(st2financeproposalList2);
			this.getManagedBean().setScoringList(null);
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
		} finally {
			statussO = null;
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
			// st2financeAdditionalproposalList1.setDocType("4");
			// st2financeAdditionalproposalList1.setEvaluationStage("2");
			// st2financeAdditionalproposalList1.setChecklistType("UP");
			st2financeAdditionalproposalList
					.add(st2financeAdditionalproposalList1);
			this.getManagedBean().setScQtCheckSt2financeAdditionalProposalList(
					st2financeAdditionalproposalList);
		} finally {

		}
	}

	public void stageTwoTechProposal() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoTechComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");

				scqtlist.setTitle(document.getDocName());
				scqtlist.setDefinationType(document.getDocDefinitionTypeCode());
				scqtlist.setAction(document.getChecklistActionCode());
				scqtlist.setManditory(false);
				scqtlist.setScoringReq(true);
				scqtlist.setDocCategory("1");
				scqtlist.setChecklistSeq(1);
				setUserDetails(scqtlist);

				scqtlist.setScQt(this.getManagedBean().getScQt());

				scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
				IStatusSO statussO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(1L);
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);

				scqtlist.setPmStatus(statusres.getPmStatus());
				compilance.add(scqtlist);

			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} finally {

		}
	}

	public void stageTwoTechnicalAddProposal() {
		IStatusSO statussO;

		try {
			List<ScQtCheckList> st2technicalproposalList = getManagedBean()
					.getScQtCheckSt2techProposal();

			if (null == st2technicalproposalList) {
				st2technicalproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2technicalproposalList1 = createSpecList("2");
			st2technicalproposalList1.setDefinationType(new PmParameter());
			st2technicalproposalList1.setDocCategory("1");
			st2technicalproposalList1.setScoringReq(true);
			st2technicalproposalList1.setChecklistSeq(1);

			st2technicalproposalList1.setScQt(this.getManagedBean().getScQt());
			st2technicalproposalList1.setAction(new PmParameter());

			setUserDetails(st2technicalproposalList1);
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			st2technicalproposalList1.setPmStatus(statusres.getPmStatus());
			// st2technicalproposalList1.setChecklistType("UP");
			// st2technicalproposalList1.setEvaluationStage("2");
			// st2technicalproposalList1.setScQtCheckListScores();
			this.getManagedBean().setScoringList(null);
			st2technicalproposalList.add(st2technicalproposalList1);
			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} finally {
			statussO = null;
		}
	}

	public void stageTwotechproposal(AjaxBehaviorEvent e) {

		try {
			String dropdown = (String) e.getComponent().getAttributes()
					.get("value");
			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			setSelctValue(dropdown);
		} finally {

		}
		// this.getManagedBean().getScQtChecklist().setChecklistType(dropdown);
	}

	public void stageTwoFinanceCheckList() {
		IStatusSO statussO;

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoFinComplianceSelect();

			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				ScQtCheckList scqtlist = createSpecList("2");
				scqtlist.setTitle(document.getDocName());
				scqtlist.setDocCategory("2");
				scqtlist.setScoringReq(true);
				// scqtlist.setEnable(true);
				scqtlist.setDefinationType(document.getDocDefinitionTypeCode());
				scqtlist.setAction(document.getChecklistActionCode());
				// scqtlist.setEvaluationStage("2");
				scqtlist.setManditory(false);
				scqtlist.setChecklistSeq(1);

				scqtlist.setScQt(this.getManagedBean().getScQt());

				setUserDetails(scqtlist);
				scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
				statussO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(1L);
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);

				scqtlist.setPmStatus(statusres.getPmStatus());
				compilance.add(scqtlist);
			}
			this.getManagedBean().setScoringList(null);
			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} finally {
			statussO = null;
		}
	}

	public void stageTwoTechcompilenceList() {
		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getScQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode("T");

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);

			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmDocument>());

			this.getManagedBean().setStageTwoTechComplianceList(
					resDTO.getDocumentList());
		} finally {
			documentSo = null;
		}

	}

	public void deliveryList(TplSpecZoneDtl zoneDetail) {
		try {
			this.getManagedBean().setTplSpecZoneDtl(zoneDetail);

		} finally {

		}

	}

	public void zoneDeliveryChange(TabChangeEvent event) {
		System.out.println("==========delivery tab change name==="
				+ event.getTab().getTitle());
	}

	public void deliveryAddressReset() {
		try {
			getManagedBean().setStateCode(null);
			getManagedBean().setDistrictId(null);
			getManagedBean().setMinistryId(null);
			getManagedBean().setDepartmentId(null);
			getManagedBean().setPtjId(null);
		} finally {

		}
	}

	public void deliveryAccept() {

		try {
			List<PmAddressType> deliveryList = this.getManagedBean()
					.getDeliveryAddressSelect();
			if (deliveryList == null) {
				deliveryList = new ArrayList<PmAddressType>();
			}
			// List<ScQtDeliveryLoc> nonZone=new ArrayList<ScQtDeliveryLoc>();

			List<BriefingZoneBean> zoneBean = this.getManagedBean()
					.getDeliveryLocBean();
			if (this.getManagedBean().getScQt().isZonal() == true) {
				for (BriefingZoneBean deliverybean : zoneBean) {
					/*
					 * if(deliverybean.getTplSpecZoneDtl()==null){
					 * deliverybean.setTplSpecZoneDtl(new TplSpecZoneDtl()); }
					 * if(deliverybean.getTplSpecZoneDtl().getZoneName()==null){
					 * deliverybean.getTplSpecZoneDtl().setZoneName(""); }
					 * if(this.getManagedBean().getTplSpecZoneDtl()==null){
					 * this.getManagedBean().setTplSpecZoneDtl(new
					 * TplSpecZoneDtl()); }
					 */
					if (deliverybean
							.getTplSpecZoneDtl()
							.getZoneName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getZoneName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						}
						for (PmAddressType addressType : deliveryList) {
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							loc.setPmAddress(addressType.getPmAddress());
							String address1 = addressType.getPmAddress()
									.getAddress1();
							String address2 = addressType.getPmAddress()
									.getAddress2();
							String address3 = addressType.getPmAddress()
									.getAddress3();
							String address4 = addressType.getPmAddress()
									.getAddress4();
							String address = address1 + "," + address2 + ","
									+ address3 + "," + address4;
							loc.setDeliveryAddress(address);
							loc.setPmOrgProfile(addressType.getPmOrgProfile());
							loc.setTplSpecZoneDtl(this.getManagedBean()
									.getTplSpecZoneDtl());
							loc.setScQt(this.getManagedBean().getScQt());

							setUserDetails(loc);
							delLoc.add(loc);
							deliverybean.setDeliveryLocList(delLoc);
							List<BriefingZoneBean> addressbean = this
									.getManagedBean().getAddressZoneBean();
							if (addressbean == null) {
								addressbean = new ArrayList<BriefingZoneBean>();
							}
							addressbean.add(deliverybean);
							this.getManagedBean().setAddressZoneBean(
									addressbean);
						}
						break;
					}

				}
			} else {
				List<ScQtDeliveryLoc> nonZone = this.getManagedBean()
						.getDeliveryNonZoneList();
				if (nonZone == null) {
					nonZone = new ArrayList<ScQtDeliveryLoc>();
				}
				for (PmAddressType addressType : deliveryList) {
					ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
					loc.setPmAddress(addressType.getPmAddress());
					String address1 = addressType.getPmAddress().getAddress1();
					String address2 = addressType.getPmAddress().getAddress2();
					String address3 = addressType.getPmAddress().getAddress3();
					String address4 = addressType.getPmAddress().getAddress4();
					String address = address1 + "," + address2 + "," + address3
							+ "," + address4;
					loc.setDeliveryAddress(address);
					loc.setPmOrgProfile(addressType.getPmOrgProfile());
					loc.setTplSpecZoneDtl(null);
					loc.setScQt(this.getManagedBean().getScQt());

					setUserDetails(loc);
					nonZone.add(loc);
					this.getManagedBean().setDeliveryNonZoneList(nonZone);
				}
			}

		} finally {

		}

	}

	public void deleteDeliveryZone(String deliveryZoneName) {
		List<BriefingZoneBean> deliveryBean = this.getManagedBean()
				.getDeliveryLocBean();

		for (BriefingZoneBean zonebean : deliveryBean) {
			if (zonebean.getZoneName().equalsIgnoreCase(deliveryZoneName)) {
				List<ScQtDeliveryLoc> deliveryList = zonebean
						.getDeliveryLocList();

				List<ScQtDeliveryLoc> selectdeliveryList = this
						.getManagedBean().getSelectDeliveryList();

				if (deliveryList.size() == selectdeliveryList.size()) {

					deliveryList.clear();

				} else {

					for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
						deliveryList.remove(delLoc);
					}
				}
			}
		}

	}

	public void stageTwoFincompilenceList() {

		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getScQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode("F");

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);
			this.getManagedBean().setStageTwoFinComplianceSelect(
					new ArrayList<PmDocument>());

			this.getManagedBean().setStageTwoFinComplianceList(
					resDTO.getDocumentList());
		} finally {
			documentSo = null;
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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

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

		} finally {

		}
	}

	public void addScheduleDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtSchedule> freqSchedule = this.getManagedBean()
					.getScheduleList();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtSchedule>();

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
				System.out.println("==========if loop====");
				freqSchedule = new ArrayList<ScQtSchedule>();
				for (int j = 0; j < textsize; j++) {
					ScQtSchedule schedule = new ScQtSchedule();

					freqSchedule.add(schedule);
				}
			}
			for (int i = freqSchedule.size(); i < this.getManagedBean()
					.getTotalFrequency(); i++) {

				;
				ScQtSchedule schedule = new ScQtSchedule();
				schedule.setStartYear(String.valueOf(year));
				schedule.setEndYear(String.valueOf(year));

				setUserDetails(schedule);
				freqSchedule.add(schedule);
			}
			this.getManagedBean().setScheduleList(freqSchedule);

		} finally {

		}
	}

	public void calculateStartMonth(AjaxBehaviorEvent event) {
		String startMonth = (String) event.getComponent().getAttributes()
				.get("value");

	}

	public void calculateProgressiveStartMonth(AjaxBehaviorEvent event) {
		String startMonth = (String) event.getComponent().getAttributes()
				.get("value");

	}

	public void calculateProgressivepayment(AjaxBehaviorEvent event) {
		/*
		 * List<ScQtProgressivePayment> paymentList = this.getManagedBean()
		 * .getProgressivePaymentList(); if (null == paymentList) { paymentList
		 * = new ArrayList<ScQtProgressivePayment>();
		 * 
		 * } for(ScQtProgressivePayment progressive:paymentList){ int index =
		 * paymentList.indexOf(progressive); paymentList.set(index,
		 * progressive);
		 * 
		 * this.getManagedBean().setProgressivePaymentList(paymentList); }
		 */
		String startMonth = (String) event.getComponent().getAttributes()
				.get("value");

	}

	public void calculateSchedulePercentage(ScQtSchedule schedule) {
		/*
		 * String endMonth = (String) event.getComponent().getAttributes()
		 * .get("value");
		 */
		// this.getManagedBean().setScQt(this.getManagedBean().getScQt());
		List<ScQtSchedule> freqSchedule = this.getManagedBean()
				.getScheduleList();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}

		int start = Integer.parseInt(schedule.getStartMonth());
		int end = Integer.parseInt(schedule.getEndMonth());
		int count = 0;

		for (int i = start + 1; i <= end + 1; i++) {

			count++;

			schedule.setTotalMonths(new BigDecimal(count));
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);

		this.getManagedBean().setScheduleList(freqSchedule);
	}

	public void addProgressiveDetails() {

		try {
			/* System.out.println("=enter addscore method==="); */
			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtProgressivePayment> progressive = this.getManagedBean()
					.getProgressivePaymentList();
			if (null == progressive) {
				progressive = new ArrayList<ScQtProgressivePayment>();

			}
			int total = progressive.size()
					+ this.getManagedBean().getTotalProgressivePayment();

			if (progressive.size() > this.getManagedBean()
					.getTotalProgressivePayment()) {
				int listsize = progressive.size();
				int textsize = this.getManagedBean()
						.getTotalProgressivePayment();
				int lesstotal = listsize - textsize;

				progressive = new ArrayList<ScQtProgressivePayment>();
				for (int j = 0; j < textsize; j++) {
					ScQtProgressivePayment payment = new ScQtProgressivePayment();

					progressive.add(payment);
				}
			}
			for (int i = progressive.size(); i < this.getManagedBean()
					.getTotalProgressivePayment(); i++) {
				System.out.println("=====dispaly  payment year===" + year);
				ScQtProgressivePayment payment = new ScQtProgressivePayment();
				payment.setStartYear(String.valueOf(year));
				payment.setEndYear(String.valueOf(year));
				// payment.setScheduleType(new BigDecimal(1));

				setUserDetails(payment);
				progressive.add(payment);
			}
			this.getManagedBean().setProgressivePaymentList(progressive);

			// progressive.add(payment);
			// this.getManagedBean().setProgressivePaymentList(progressive);
			/*
			 * System.out.println("===exit add score=="+ this.getManagedBean()
			 * .getCheckListScore().size());
			 */
		} finally {

		}
	}

	public void calculateTotalPackage(ScQtScoreSetting technical) {
		try {
			if (technical.getOverallTechnical() == null) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (technical.getOverallFin() == null) {
				technical.setOverallFin(new BigDecimal(0));
			}
			int local = technical.getOverallTechnical().intValue()
					+ technical.getOverallFin().intValue();

			this.getManagedBean().setRecommendationPackageTotal(
					new BigDecimal(local));
		} finally {

		}
	}

	public void calculateRecommendtechnicalScore(ScQtScoreSetting technical) {
		try {
			if (technical.getOverallTechnical() == null) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (technical.getOverallOtherTechnical() == null) {
				technical.setOverallOtherTechnical(new BigDecimal(0));
			}
			int local = technical.getOverallTechnical().intValue()
					+ technical.getOverallOtherTechnical().intValue();

			this.getManagedBean().setRecommendTechnicalScore(
					new BigDecimal(local));
		} finally {

		}
	}

	public void calculateRecommendFinancialScore(ScQtScoreSetting financial) {
		try {
			if (financial.getOverallFin() == null) {
				financial.setOverallFin(new BigDecimal(0));
			}
			if (financial.getOverallOtherFin() == null) {
				financial.setOverallOtherFin(new BigDecimal(0));
			}
			int local = financial.getOverallFin().intValue()
					+ financial.getOverallOtherFin().intValue();

			this.getManagedBean().setRecommendfinancialScore(
					new BigDecimal(local));
		} finally {

		}
	}

	public void acceptCompanyScoring() {
		try {
			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			ScQtCheckListScore noContract = this.getManagedBean()
					.getScQtCheckListScore();
			ScQtCheckListScore scoreOfYears = this.getManagedBean()
					.getScQtContractScore();
			if (noContract == null) {
				noContract = new ScQtCheckListScore();
			}
			if (scoreOfYears == null) {
				scoreOfYears = new ScQtCheckListScore();
			}

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
			int checklistScore = 0;
			if (checkListScore.size() > 0) {

				getManagedBean().setTechnicalChecklistScoring(0);
				for (ScQtCheckListScore score : checkListScore) {

					if (getManagedBean().getTechnicalChecklistScoring() < score
							.getScore().intValue()) {
						;
						getManagedBean().setTechnicalChecklistScoring(
								score.getScore().intValue());
						checklistScore = getManagedBean()
								.getTechnicalChecklistScoring();

					}
					score.setScoreType(new BigDecimal(this.getManagedBean()
							.getChecklistScoreType()));
					score.setCriteriaType(10);

					setUserDetails(score);
					int index = checkListScore.indexOf(score);
					checkListScore.set(index, score);
				}
				localList.addAll(checkListScore);
			}
			int scoreYear = 0;
			if (scoreYears.size() > 0) {

				getManagedBean().setTechnicalChecklistScoring(0);
				for (ScQtCheckListScore score : scoreYears) {

					if (getManagedBean().getTechnicalChecklistScoring() < score
							.getScore().intValue()) {
						;
						getManagedBean().setTechnicalChecklistScoring(
								score.getScore().intValue());
						scoreYear = getManagedBean()
								.getTechnicalChecklistScoring();

					}
					score.setScoreType(new BigDecimal(this.getManagedBean()
							.getCompanyScoreType()));
					score.setCriteriaType(11);

					setUserDetails(score);
					int index = scoreYears.indexOf(score);
					scoreYears.set(index, score);
				}
				localList.addAll(scoreYears);
			}

			if (noContract.getScore() != null) {

				noContract.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				noContract.setCriteriaType(10);

				noContract.setCheckList(checkList);

				setUserDetails(noContract);
				checklistScore = noContract.getScore().intValue();
				localList.add(noContract);
			}

			if (scoreOfYears.getScore() != null) {

				scoreOfYears.setScoreType(new BigDecimal(this.getManagedBean()
						.getCompanyScoreType()));
				scoreOfYears.setCriteriaType(11);

				scoreOfYears.setCheckList(checkList);

				setUserDetails(scoreOfYears);
				scoreYear = scoreOfYears.getScore().intValue();
				localList.add(scoreOfYears);
			}
			int localScore = scoreYear + checklistScore;

			checkList.getQtScoreWeightage()
					.setScore(new BigDecimal(localScore));
			// itemIndex = checklist.indexOf(list); // checklist.remove(list);
			// * checklist.set(itemIndex,list);
			/*
			 * if (scoreContract != null) {
			 * 
			 * localList.addAll(scoreContract); }
			 */
			checkList.setScQt(this.getManagedBean().getScQt());
			checkList.setScQtCheckListScores(localList);
			// this.getManagedBean().setCollectionScoring(localList);

			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQtCheckList(checkList);
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			reqDTO.setScQtCheckList(checkList);
			// specSo.saveCheckListScore(reqDTO);

			this.getManagedBean()
					.getScQtCheckSt2techProposal()
					.add(this.getManagedBean().getIndex(),
							resDTO.getScQtCheckList());
			// }ll
		} finally {

		}
	}

	public void acceptCompanyPrivateScoring() {
		try {
			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			ScQtCheckListScore noContract = this.getManagedBean()
					.getScQtCheckListScore();
			ScQtCheckListScore scoreOfYears = this.getManagedBean()
					.getScQtContractScore();
			if (noContract == null) {
				noContract = new ScQtCheckListScore();
			}
			if (scoreOfYears == null) {
				scoreOfYears = new ScQtCheckListScore();
			}

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
			int checklistScore = 0;
			if (nonFederalScore.size() > 0) {
				getManagedBean().setTechnicalChecklistScoring(0);
				for (ScQtCheckListScore score : nonFederalScore) {

					if (getManagedBean().getTechnicalChecklistScoring() < score
							.getScore().intValue()) {
						;
						getManagedBean().setTechnicalChecklistScoring(
								score.getScore().intValue());
						checklistScore = getManagedBean()
								.getTechnicalChecklistScoring();
					}

					score.setScoreType(new BigDecimal(this.getManagedBean()
							.getChecklistScoreType()));
					scoreOfYears.setCriteriaType(15);

					setUserDetails(scoreOfYears);
					int index = nonFederalScore.indexOf(score);
					nonFederalScore.set(index, score);
				}

				localList.addAll(nonFederalScore);
			}
			int scoreYear = 0;
			if (scoreYears.size() > 0) {
				getManagedBean().setTechnicalChecklistScoring(0);
				for (ScQtCheckListScore score : scoreYears) {

					if (getManagedBean().getTechnicalChecklistScoring() < score
							.getScore().intValue()) {
						;
						getManagedBean().setTechnicalChecklistScoring(
								score.getScore().intValue());
						scoreYear = getManagedBean()
								.getTechnicalChecklistScoring();
					}
					score.setScoreType(new BigDecimal(this.getManagedBean()
							.getChecklistScoreType()));
					scoreOfYears.setCriteriaType(16);

					setUserDetails(scoreOfYears);
					int index = scoreYears.indexOf(score);
					scoreYears.set(index, score);
				}

				localList.addAll(scoreYears);
			}
			if (scoreContract != null) {

				localList.addAll(scoreContract);
			}
			if (noContract.getScore() != null) {

				noContract.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				noContract.setCriteriaType(15);

				noContract.setCheckList(checkList);

				setUserDetails(noContract);
				checklistScore = noContract.getScore().intValue();
				localList.add(noContract);
			}

			if (scoreOfYears.getScore() != null) {

				scoreOfYears.setScoreType(new BigDecimal(this.getManagedBean()
						.getCompanyScoreType()));
				scoreOfYears.setCriteriaType(16);

				scoreOfYears.setCheckList(checkList);

				setUserDetails(scoreOfYears);
				scoreYear = scoreOfYears.getScore().intValue();
				localList.add(scoreOfYears);
			}

			int localScore = scoreYear + checklistScore;

			checkList.getQtScoreWeightage()
					.setScore(new BigDecimal(localScore));
			checkList.setScQt(this.getManagedBean().getScQt());
			checkList.setScQtCheckListScores(localList);
			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setScQtCheckList(checkList);
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			reqDTO.setScQtCheckList(checkList);
			// specSo.saveCheckListScore(reqDTO);
			this.getManagedBean()
					.getScQtCheckSt2techProposal()
					.add(this.getManagedBean().getIndex(),
							resDTO.getScQtCheckList());
			// this.getManagedBean().setCollectionScoring(localList);
			// }ll
		} finally {

		}
	}

	public void acceptFinanceGovCompanyScoring() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		ScQtCheckListScore finCompanyScore = this.getManagedBean()
				.getScQtCheckListScore();
		if (finCompanyScore == null) {
			finCompanyScore = new ScQtCheckListScore();
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

		int contractScore = 0;
		if (scoreContract.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : scoreContract) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					contractScore = getManagedBean()
							.getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(12);

				setUserDetails(score);

				int index = scoreContract.indexOf(score);
				scoreContract.set(index, score);
			}

			localList.addAll(scoreContract);
		}

		if (finCompanyScore.getScore() != null) {

			finCompanyScore.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			finCompanyScore.setCriteriaType(12);

			finCompanyScore.setCheckList(checkList);

			setUserDetails(finCompanyScore);
			contractScore = finCompanyScore.getScore().intValue();
			localList.add(finCompanyScore);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(contractScore));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);

		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

	}

	public void acceptFinancePrivateCompanyPrivateScoring() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore finPrivateScore = this.getManagedBean()
				.getScQtCheckListScore();
		if (finPrivateScore == null) {
			finPrivateScore = new ScQtCheckListScore();
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

		int contractScore = 0;
		if (scoreContract.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : scoreContract) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					contractScore = getManagedBean()
							.getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(17);

				setUserDetails(score);

				int index = scoreContract.indexOf(score);
				scoreContract.set(index, score);
			}

			localList.addAll(scoreContract);
		}
		if (finPrivateScore.getScore() != null) {

			finPrivateScore.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			finPrivateScore.setCriteriaType(17);
			finPrivateScore.setCheckList(checkList);

			setUserDetails(finPrivateScore);

			contractScore = finPrivateScore.getScore().intValue();
			localList.add(finPrivateScore);
		}

		checkList.getQtScoreWeightage().setScore(new BigDecimal(contractScore));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

	}

	public void acceptWarrantyScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore checkListScore = this.getManagedBean()
				.getScQtCheckListScore();
		if (checkListScore == null) {
			checkListScore = new ScQtCheckListScore();
		}
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
		int warranty = 0;
		if (warrantyScore.size() > 0) {

			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : warrantyScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					warranty = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(30);

				setUserDetails(score);

				int index = warrantyScore.indexOf(score);
				warrantyScore.set(index, score);

			}

			localList.addAll(warrantyScore);
		}
		if (checkListScore.getScore() != null) {

			checkListScore.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			checkListScore.setCriteriaType(30);
			checkListScore.setCheckList(checkList);

			setUserDetails(checkListScore);
			warranty = checkListScore.getScore().intValue();
			localList.add(checkListScore);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(warranty));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);
		this.getManagedBean()
				.getScQtCheckSt2techProposal()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

	}

	public void acceptTrainingScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore checkListScore = this.getManagedBean()
				.getScQtCheckListScore();
		if (checkListScore == null) {
			checkListScore = new ScQtCheckListScore();
		}

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
		int training = 0;
		if (trainingScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : trainingScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					training = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(40);

				setUserDetails(score);

				int index = trainingScore.indexOf(score);
				trainingScore.set(index, score);
			}

			localList.addAll(trainingScore);
		}

		if (checkListScore.getScore() != null) {

			checkListScore.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			checkListScore.setCriteriaType(40);
			checkListScore.setCheckList(checkList);

			setUserDetails(checkListScore);

			training = checkListScore.getScore().intValue();
			localList.add(checkListScore);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(training));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);
		this.getManagedBean()
				.getScQtCheckSt2techProposal()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
	}

	public void acceptTechnicalScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore technicalEmployee = this.getManagedBean()
				.getScQtCheckListScore();
		if (technicalEmployee == null) {
			technicalEmployee = new ScQtCheckListScore();
		}

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
		int technical = 0;
		if (technicalScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : technicalScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					technical = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(20);

				setUserDetails(score);

				int index = technicalScore.indexOf(score);
				technicalScore.set(index, score);
			}

			localList.addAll(technicalScore);
		}
		if (technicalEmployee.getScore() != null) {

			technicalEmployee.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			technicalEmployee.setCriteriaType(20);
			technicalEmployee.setCheckList(checkList);

			setUserDetails(technicalEmployee);

			technical = technicalEmployee.getScore().intValue();
			localList.add(technicalEmployee);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(technical));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);
		this.getManagedBean()
				.getScQtCheckSt2techProposal()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

	}

	public void acceptEmployeeScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore employeeChecklist = this.getManagedBean()
				.getScQtCheckListScore();
		if (employeeChecklist == null) {
			employeeChecklist = new ScQtCheckListScore();
		}

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
		int employee = 0;
		if (employeeScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : employeeScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					employee = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(21);

				setUserDetails(score);

				int index = employeeScore.indexOf(score);
				employeeScore.set(index, score);
			}

			localList.addAll(employeeScore);
		}

		if (employeeChecklist.getScore() != null) {

			employeeChecklist.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			employeeChecklist.setCriteriaType(21);
			employeeChecklist.setCheckList(checkList);

			setUserDetails(employeeChecklist);
			employee = employeeChecklist.getScore().intValue();
			localList.add(employeeChecklist);
		}

		checkList.getQtScoreWeightage().setScore(new BigDecimal(employee));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);
		this.getManagedBean()
				.getScQtCheckSt2techProposal()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());

	}

	public void acceptCreditScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore creditFacility = this.getManagedBean()
				.getScQtCheckListScore();
		if (creditFacility == null) {
			creditFacility = new ScQtCheckListScore();
		}

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
		int credit = 0;
		if (creditFacilityScore.size() > 0) {

			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : creditFacilityScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					credit = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(51);

				setUserDetails(score);

				int index = creditFacilityScore.indexOf(score);
				creditFacilityScore.set(index, score);
			}

			localList.addAll(creditFacilityScore);
		}
		if (creditFacility.getScore() != null) {

			creditFacility.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			creditFacility.setCriteriaType(51);
			creditFacility.setCheckList(checkList);

			setUserDetails(creditFacility);

			credit = creditFacility.getScore().intValue();
			localList.add(creditFacility);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(credit));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());
	}

	public void acceptCashScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore acceptCash = this.getManagedBean()
				.getScQtCheckListScore();
		if (acceptCash == null) {
			acceptCash = new ScQtCheckListScore();
		}

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
		int cash = 0;
		if (cashScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : cashScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					cash = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(52);

				setUserDetails(score);

				int index = cashScore.indexOf(score);
				cashScore.set(index, score);
			}

			localList.addAll(cashScore);
		}

		if (acceptCash.getScore() != null) {

			acceptCash.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			acceptCash.setCriteriaType(52);
			acceptCash.setCheckList(checkList);

			setUserDetails(acceptCash);

			cash = acceptCash.getScore().intValue();
			localList.add(acceptCash);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(cash));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());
	}

	public void acceptBankScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore bankDetails = this.getManagedBean()
				.getScQtCheckListScore();
		if (bankDetails == null) {
			bankDetails = new ScQtCheckListScore();
		}

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
		int bank = 0;
		if (bankScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : bankScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					bank = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(50);

				setUserDetails(score);

				int index = bankScore.indexOf(score);
				bankScore.set(index, score);
			}

			localList.addAll(bankScore);
		}

		if (bankDetails.getScore() != null) {

			bankDetails.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			bankDetails.setCriteriaType(50);
			bankDetails.setCheckList(checkList);

			setUserDetails(bankDetails);

			bank = bankDetails.getScore().intValue();
			localList.add(bankDetails);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(bank));
		String Startmonth = "";
		String endmonth = "";
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("0")) {
			Startmonth = "01";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("1")) {
			Startmonth = "02";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("2")) {
			Startmonth = "03";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("3")) {
			Startmonth = "04";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("4")) {
			Startmonth = "05";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("5")) {
			Startmonth = "06";
		}

		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("6")) {
			Startmonth = "07";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("7")) {
			Startmonth = "08";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("8")) {
			Startmonth = "09";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("9")) {
			Startmonth = "10";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("10")) {
			Startmonth = "11";
		}
		if (this.getManagedBean().getFromMonth().equalsIgnoreCase("11")) {
			Startmonth = "12";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("0")) {
			endmonth = "01";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("1")) {
			endmonth = "02";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("2")) {
			endmonth = "03";
		}

		if (this.getManagedBean().getToMonth().equalsIgnoreCase("3")) {
			endmonth = "04";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("4")) {
			endmonth = "05";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("5")) {
			endmonth = "06";
		}

		if (this.getManagedBean().getToMonth().equalsIgnoreCase("6")) {
			endmonth = "07";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("7")) {
			endmonth = "08";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("8")) {
			endmonth = "09";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("9")) {
			endmonth = "10";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("10")) {
			endmonth = "11";
		}
		if (this.getManagedBean().getToMonth().equalsIgnoreCase("11")) {
			endmonth = "12";
		}
		Date d1 = new Date();

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String localDate = format.format(d1);
		String date = localDate.substring(0, 2);
		String fromdate = date + "/" + Startmonth + "/"
				+ this.getManagedBean().getStartYear();
		String toDate = date + "/" + endmonth + "/"
				+ this.getManagedBean().getEndYear();
		try {
			checkList.getScQtCheckListValue().setFromDate(df.parse(fromdate));
			checkList.setScQt(this.getManagedBean().getScQt());
			checkList.getScQtCheckListValue().setToDate(df.parse(toDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll

		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());
	}

	public void acceptAssetScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore assetChecklist = this.getManagedBean()
				.getScQtCheckListScore();
		if (assetChecklist == null) {
			assetChecklist = new ScQtCheckListScore();
		}

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
		int asset = 0;
		if (assetScore.size() > 0) {
			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : assetScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					asset = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(60);

				setUserDetails(score);

				int index = assetScore.indexOf(score);
				assetScore.set(index, score);
			}

			localList.addAll(assetScore);
		}

		if (assetChecklist.getScore() != null) {

			assetChecklist.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			assetChecklist.setCriteriaType(60);
			assetChecklist.setCheckList(checkList);

			setUserDetails(assetChecklist);

			asset = assetChecklist.getScore().intValue();
			localList.add(assetChecklist);
		}
		checkList.getQtScoreWeightage().setScore(new BigDecimal(asset));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll
		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());
	}

	public void acceptPaidUpScore() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore paidUpChecklist = this.getManagedBean()
				.getScQtCheckListScore();
		if (paidUpChecklist == null) {
			paidUpChecklist = new ScQtCheckListScore();
		}

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
		int paidUp = 0;
		if (paidUpScore.size() > 0) {

			getManagedBean().setTechnicalChecklistScoring(0);
			for (ScQtCheckListScore score : paidUpScore) {

				if (getManagedBean().getTechnicalChecklistScoring() < score
						.getScore().intValue()) {
					;
					getManagedBean().setTechnicalChecklistScoring(
							score.getScore().intValue());
					paidUp = getManagedBean().getTechnicalChecklistScoring();

				}
				score.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				score.setCriteriaType(70);

				setUserDetails(score);

				int index = paidUpScore.indexOf(score);
				paidUpScore.set(index, score);
			}

			localList.addAll(paidUpScore);
		}
		if (paidUpChecklist.getScore() != null) {

			paidUpChecklist.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			paidUpChecklist.setCriteriaType(70);
			paidUpChecklist.setCheckList(checkList);

			setUserDetails(paidUpChecklist);
			paidUp = paidUpChecklist.getScore().intValue();
			localList.add(paidUpChecklist);
		}

		checkList.getQtScoreWeightage().setScore(new BigDecimal(paidUp));
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setScQtCheckListScores(localList);
		// this.getManagedBean().setCollectionScoring(localList);
		// }ll

		IQTSpecSO specSo;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		reqDTO.setScQtCheckList(checkList);
		SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);

		reqDTO.setScQtCheckList(checkList);
		// specSo.saveCheckListScore(reqDTO);

		this.getManagedBean()
				.getScQtCheckSt2financeProposalList()
				.add(this.getManagedBean().getIndex(),
						resDTO.getScQtCheckList());
	}

	public List<SelectItem> getAppDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		UserReqDTO resDto = null;
		try {

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
		} finally {

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
		} finally {

		}
		return getManagedBean().getYearList();
	}

	/*
	 * public void checkListDefChange(AjaxBehaviorEvent event) { String type =
	 * (String) event.getComponent().getAttributes() .get("value");
	 * System.out.println("=====checklist change type value===" + type); try {
	 * List<SelectItem> selectItems = new ArrayList<SelectItem>(); if
	 * (type.equalsIgnoreCase("S")) {
	 * this.getManagedBean().setTechnicalChecklistAction(null);
	 * System.out.println("====sample==="); selectItems.add(new SelectItem("M",
	 * "Manual"));
	 * this.getManagedBean().setTechnicalChecklistAction(selectItems); } if
	 * (type.equalsIgnoreCase("UP")) { System.out.println("====upload===");
	 * this.getManagedBean().setTechnicalChecklistAction(null);
	 * selectItems.add(new SelectItem(null, "--Select One--"));
	 * selectItems.add(new SelectItem("V", "View")); selectItems.add(new
	 * SelectItem("O", "Online Submit"));
	 * this.getManagedBean().setTechnicalChecklistAction(selectItems); } } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */

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
		} finally {

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
		} finally {

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
		QuationTenderReqDTO spReqDTO = prepareSpecReq();
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTDetailSO detailSO = getDetailSO();

		try {
			QuationTenderResDTO resDTO = detailSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getQtDetail();

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_700026,
										new String[] { "Specification For ",
												qt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			detailSO = null;
		}
		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	private IQTDetailSO getDetailSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
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
		try {
			this.getManagedBean().getSelectpanelItem();
			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
			reqDTO.setPanelItem(this.getManagedBean().getCtPanelItem());
			IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);

			PanelSupplierResDTO resDTO = panelSupplierSO
					.retrievePanelZonesByPanelItem(reqDTO);

			if (isNotNullOREmpty(resDTO.getCtPanelZones())) {

				getManagedBean().setCtZoneTrees(
						LocationSingleton.getInstance().constuctPanelTreeNodes(
								resDTO.getCtPanelZones()));

			}

		} finally {

		}

	}

	public void updateContractZoneLocation() {
		try {
			if (getManagedBean().getCtZoneTreeSelectedNodes() != null) {
				TreeNode treelocal = null;
				for (TreeNode treeNode : getManagedBean()
						.getCtZoneTreeSelectedNodes()) {

					treelocal = consturctTreeNode(getManagedBean()
							.getCtZoneTreeSelectedNodes());

				}
				this.getManagedBean().getCtZoneSuppliers().add(treelocal);
			}
		} finally {

		}
	}

	public TreeNode consturctTreeNode(TreeNode[] selectedNode) {
		Set<TreeNode> parentNodes = new HashSet<>();
		List<CtPanelZone> zone = this.getManagedBean().getCtPanelZones();
		CtPanelZone panelZone = null;
		if (null == zone) {
			zone = new ArrayList<CtPanelZone>();
		}
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
						TreeNode state = new DefaultTreeNode(
								stateNode.getType(), stateNode.getData(),
								zoneNode);

						for (TreeNode districtNode : stateNode.getChildren()) {
							if (districtNode.isSelected()) {
								new DefaultTreeNode(districtNode.getType(),
										districtNode.getData(), state);

							}
						}

					}

				}

			}

			panelZone = (CtPanelZone) node.getData();
			if (!zone.contains(panelZone)) {
				zone.add(panelZone);
			}
			this.getManagedBean().setCtPanelZones(zone);

		}

		return root;

	}

	public void retriveZoneBySupplier(NodeSelectEvent event) {

		CtPanelZone ctPanelzone = (CtPanelZone) event.getTreeNode().getData();

		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		reqDTO.setCircularZoneId(ctPanelzone.getPanelZoneId());
		IPanelSupplierSO panelSupplierSO = (IPanelSupplierSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT,
						IPanelSupplierSO.class);
		PanelSupplierResDTO resDTO = panelSupplierSO
				.retrieveSupplierByZone(reqDTO);
		if (null != resDTO.getPanelSuppliers()
				&& !resDTO.getPanelSuppliers().isEmpty()) {
			this.getManagedBean().setTotalSuppliers(
					resDTO.getPanelSuppliers().size());
			this.getManagedBean().setCtZoneSupplierList(
					resDTO.getPanelSuppliers());
		}

	}

	public void retriveSupplierDtls(CtPanelSupplier panelSupplier) {

		this.getManagedBean().setCtpanelSupplier(panelSupplier);
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
		try {
			// delete for company experience for Government company Technical
			// checklist
			List<ScQtCheckListScore> scoringList = getManagedBean()
					.getCheckListScore();
			if (scoringList == null) {
				scoringList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> selectchecklistScoring = this
					.getManagedBean().getCheckListSelectScore();
			if (selectchecklistScoring == null) {
				selectchecklistScoring = new ArrayList<ScQtCheckListScore>();
			}

			if (scoringList.size() == selectchecklistScoring.size()) {
				scoringList.clear();
			} else {
				for (ScQtCheckListScore scoring : selectchecklistScoring)
					scoringList.remove(scoring);
			}

			// delete company experience for federal government company scoring
			// years technical checklist
			List<ScQtCheckListScore> scoringYears = getManagedBean()
					.getScoringForYears();
			if (scoringYears == null) {
				scoringYears = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> selectGovScoringYears = this
					.getManagedBean().getScoringForSelectYears();
			if (selectGovScoringYears == null) {
				selectGovScoringYears = new ArrayList<ScQtCheckListScore>();
			}

			if (scoringYears.size() == selectGovScoringYears.size()) {
				scoringYears.clear();
			} else {
				for (ScQtCheckListScore scoring : selectGovScoringYears)
					scoringYears.remove(scoring);
			}

			// delete for company experience for federal private company scoring
			// Technical Checklist
			List<ScQtCheckListScore> nonfederalCompany = getManagedBean()
					.getNonFederalScore();
			if (nonfederalCompany == null) {
				nonfederalCompany = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> nonFederalSelect = this.getManagedBean()
					.getNonFederalSelectScore();
			if (nonFederalSelect == null) {
				nonFederalSelect = new ArrayList<ScQtCheckListScore>();
			}

			if (nonfederalCompany.size() == nonFederalSelect.size()) {
				nonfederalCompany.clear();
			} else {
				for (ScQtCheckListScore scoring : nonFederalSelect)
					nonfederalCompany.remove(scoring);
			}

			// delete for company experience for federal private company scoring
			// yers Technical Checklist
			List<ScQtCheckListScore> nonfederalCompanyYears = getManagedBean()
					.getNonFederalScoreyears();
			if (nonfederalCompanyYears == null) {
				nonfederalCompanyYears = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> nonFederalSelectYears = this
					.getManagedBean().getNonFederalSelectScoreyears();
			if (nonFederalSelectYears == null) {
				nonFederalSelectYears = new ArrayList<ScQtCheckListScore>();
			}

			if (nonfederalCompanyYears.size() == nonFederalSelectYears.size()) {
				nonfederalCompanyYears.clear();
			} else {
				for (ScQtCheckListScore scoring : nonFederalSelectYears)
					nonfederalCompanyYears.remove(scoring);
			}

			// delete for warranty scoring Technical Checklist
			List<ScQtCheckListScore> warrantyScore = getManagedBean()
					.getWarantySelectScore();
			if (warrantyScore == null) {
				warrantyScore = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> warrantySelectList = this.getManagedBean()
					.getWarantySelectScore();
			if (warrantySelectList == null) {
				warrantySelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (warrantyScore.size() == warrantySelectList.size()) {
				warrantyScore.clear();
			} else {
				for (ScQtCheckListScore scoring : warrantySelectList)
					warrantyScore.remove(scoring);
			}

			// delete for training scoring Technical Checklist
			List<ScQtCheckListScore> trainingScoreList = getManagedBean()
					.getTrainingScore();
			if (trainingScoreList == null) {
				trainingScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> trainingSelectList = this.getManagedBean()
					.getTrainingSelectList();
			if (trainingSelectList == null) {
				trainingSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (trainingScoreList.size() == trainingSelectList.size()) {
				trainingScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : trainingSelectList)
					trainingScoreList.remove(scoring);
			}

			// delete for technical staff scoring Technical Checklist
			List<ScQtCheckListScore> staffScoreList = getManagedBean()
					.getTechnicalStaffScore();
			if (staffScoreList == null) {
				staffScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> staffSelectList = this.getManagedBean()
					.getTechnicalStaffSelectList();
			if (staffSelectList == null) {
				staffSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (staffScoreList.size() == staffSelectList.size()) {
				staffScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : staffSelectList)
					staffScoreList.remove(scoring);
			}

			// delete for total employee scoring Technical Checklist
			List<ScQtCheckListScore> totalEmployeeScoreList = getManagedBean()
					.getTotalEmployeeScore();
			if (totalEmployeeScoreList == null) {
				totalEmployeeScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> totalEmployeeSelectList = this
					.getManagedBean().getTotalEmployeeSelectList();
			if (totalEmployeeSelectList == null) {
				totalEmployeeSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (totalEmployeeScoreList.size() == totalEmployeeSelectList.size()) {
				totalEmployeeScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : totalEmployeeSelectList)
					totalEmployeeScoreList.remove(scoring);
			}

			// delete for federal Government financial Checklist
			List<ScQtCheckListScore> finGovScoreList = getManagedBean()
					.getContractAmount();
			if (finGovScoreList == null) {
				finGovScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> finGovSelectList = this.getManagedBean()
					.getFinGovContractSelectList();
			if (finGovSelectList == null) {
				finGovSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (finGovScoreList.size() == finGovSelectList.size()) {
				finGovScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : finGovSelectList)
					finGovScoreList.remove(scoring);
			}

			// delete for federal Private financial Checklist
			List<ScQtCheckListScore> finPrivateScoreList = getManagedBean()
					.getNonFederalContractScore();
			if (finPrivateScoreList == null) {
				finPrivateScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> finPrivateSelectList = this
					.getManagedBean().getFinPrivateContractSelectList();
			if (finPrivateSelectList == null) {
				finPrivateSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (finPrivateScoreList.size() == finPrivateSelectList.size()) {
				finPrivateScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : finPrivateSelectList)
					finPrivateScoreList.remove(scoring);
			}

			// delete for credit facility financial Checklist
			List<ScQtCheckListScore> creditfacilitycoreList = getManagedBean()
					.getCreditFacility();
			if (creditfacilitycoreList == null) {
				creditfacilitycoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> creditFacilitySelectList = this
					.getManagedBean().getCreditfacilitySelectList();
			if (creditFacilitySelectList == null) {
				creditFacilitySelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (creditfacilitycoreList.size() == creditFacilitySelectList
					.size()) {
				creditfacilitycoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : creditFacilitySelectList)
					creditfacilitycoreList.remove(scoring);
			}

			// delete for cah on hand financial Checklist
			List<ScQtCheckListScore> cashScoreList = getManagedBean()
					.getScoringCash();
			if (cashScoreList == null) {
				cashScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> cashSelectList = this.getManagedBean()
					.getCreditfacilitySelectList();
			if (cashSelectList == null) {
				cashSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (cashScoreList.size() == cashSelectList.size()) {
				cashScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : cashSelectList)
					cashScoreList.remove(scoring);
			}

			// delete for bank statement financial Checklist
			List<ScQtCheckListScore> bankScoreList = getManagedBean()
					.getBankStamentScore();
			if (bankScoreList == null) {
				bankScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> bankSelectList = this.getManagedBean()
					.getBankSelectList();
			if (bankSelectList == null) {
				bankSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (bankScoreList.size() == bankSelectList.size()) {
				bankScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : bankSelectList)
					bankScoreList.remove(scoring);
			}

			// delete for Assets Liabilities financial Checklist
			List<ScQtCheckListScore> assetScoreList = getManagedBean()
					.getAssetScore();
			if (assetScoreList == null) {
				assetScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> assetSelectList = this.getManagedBean()
					.getAssetLiabilitySelectList();
			if (assetSelectList == null) {
				assetSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (assetScoreList.size() == assetSelectList.size()) {
				assetScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : assetSelectList)
					assetScoreList.remove(scoring);
			}

			// delete for PaidUp Capital financial Checklist
			List<ScQtCheckListScore> paidUpScoreList = getManagedBean()
					.getPaidUpCapitalScore();
			if (paidUpScoreList == null) {
				paidUpScoreList = new ArrayList<ScQtCheckListScore>();
			}

			List<ScQtCheckListScore> paidUpSelectList = this.getManagedBean()
					.getPaidUpSelectList();
			if (paidUpSelectList == null) {
				paidUpSelectList = new ArrayList<ScQtCheckListScore>();
			}

			if (paidUpScoreList.size() == paidUpSelectList.size()) {
				paidUpScoreList.clear();
			} else {
				for (ScQtCheckListScore scoring : paidUpSelectList)
					paidUpScoreList.remove(scoring);
			}

		} finally {

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
				this.getManagedBean().setCtPanelItem(panelItem);
			}
			this.getManagedBean().setCircularExpDate(circular.getEndDate());
			this.getManagedBean().setCirCularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			this.getManagedBean().setCircularType(
					circular.getCtCircular().getCircularType());

			// this.getManagedBean().setCircularType(getParameterDesc(circular.getCtPanelItems().get(0).getItemCategoryId()));
			// System.out.println("=======circular type==="+
			// this.getManagedBean().getCircularType());

			this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());

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
		this.getManagedBean().setPanelItemCategory(
				getParameterDesc(ctPanelItem.getItemCategoryId()));
		if (null != panelList && panelList.isEmpty()) {

			panelList.add(ctPanelItem);
		}
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
		} finally {

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
				if (weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				total = total
						+ weight.getQtScoreWeightage().getWeightage()
								.intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setTechnicalWeightageScore(techWeightage1);
			}
		} finally {

		}

	}

	public void techScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				total = total
						+ weight.getQtScoreWeightage().getScore().intValue();
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setTechPoints(techScore.intValue());
			}
		} finally {

		}

	}

	public void finScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getFinancialScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getDefinationType()
						&& weight.getDefinationType().getParameterId() == 185) {
					getManagedBean().setTechnicalChecklistScoring(
							weight.getQtScoreWeightage().getScore().intValue());
					getManagedBean().setScoreVarience(
							new BigDecimal(getManagedBean()
									.getTechnicalChecklistScoring()));
				}

				if (weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				total = total
						+ weight.getQtScoreWeightage().getScore().intValue();
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setFinPoints(techScore.intValue());
			}
		} finally {

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
				if (weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				/*
				 * if (getManagedBean().getTechnicalChecklistScoring() < weight
				 * .getQtScoreWeightage().getWeightage().intValue()) {
				 * getManagedBean().setTechnicalChecklistScoring(
				 * weight.getQtScoreWeightage().getWeightage() .intValue());
				 * getManagedBean().setScoreVarience( new
				 * BigDecimal(getManagedBean()
				 * .getTechnicalChecklistScoring())); }
				 */

				total = total
						+ weight.getQtScoreWeightage().getWeightage()
								.intValue();
				BigDecimal b1 = new BigDecimal(total);
				BigDecimal techWeightage = new BigDecimal(100);
				MathContext mc = new MathContext(4);
				BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				this.getManagedBean()
						.setFinancialWeightageScore(techWeightage1);
			}
		} finally {

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
			} finally {
				circularSO = null;
			}
			return null;

		}
	}

	public void searchScqtDocAction() {
		templateSpecCreate.reloadData();
	}

	public void searchDeliveryAddress() {
		searchDeliveryAddress.reloadData();
	}

	public void resetTemplate() {
		getManagedBean().setSpecName(null);
		getManagedBean().setStandardTemplate(null);
		getManagedBean().setSearchProcurementId(null);
		getManagedBean().setQtNum(null);
		getManagedBean().setSearchItemTypeId(null);
	}

	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<TplSpecMaster> {
		private static final long serialVersionUID = 1L;

		public CreateTemplateSpecPagingDataModel() {

			super("createList");

		}

		public ISearchResultBean<TplSpecMaster> pageChangeListener(
				ISearchBean<TplSpecMaster> searchBean) {
			ITemplateSpecSO qtTemplateSO;
			TemplateSpecResDTO resDto;

			try {
				TemplateSpecReqDTO qtTemplateReqDTO = new TemplateSpecReqDTO();
				Long specTypeCode = 125L;
				if (getManagedBean().getSpecName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					SearchFilterBean specSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					specSearchBean.setSearchField("specName");
					specSearchBean.setSearchValue(getManagedBean()
							.getSpecName());
					searchBean.getDataFilterBeans().add(specSearchBean);

				}

				if (getManagedBean().getStandardTemplate() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {

					SearchFilterBean standardSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					standardSearchBean.setSearchField("processType");
					standardSearchBean.setSearchValue(getManagedBean()
							.getStandardTemplate());
					searchBean.getDataFilterBeans().add(standardSearchBean);
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					itemSearchBean.setSearchField("itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (getManagedBean().getSearchProcurementId() != null) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					procurementSearchBean
							.setSearchField("procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}
				if (getManagedBean().getQtNum() != null
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("scQtDocs.scQt.qtNo");
					qtnoSearchBean.setSearchValue(getManagedBean().getQtNum());

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}
				if (specTypeCode != null) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("specTypeCode.parameterId");
					qtnoSearchBean.setSearchValue(specTypeCode);

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}

				qtTemplateReqDTO.setSearchBean(searchBean);

				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				resDto = qtTemplateSO.searchTemplateMaster(qtTemplateReqDTO);

				if (resDto != null) {
					return resDto.getSearchResultBean();
				}

			} finally {

			}
			return null;
		}

	}

	private class DeliveryPagingDataModel extends
			PagingDataModel<PmAddressType> {
		private static final long serialVersionUID = 1L;

		public DeliveryPagingDataModel() {

			super("deliveryAddress");

		}

		public ISearchResultBean<PmAddressType> pageChangeListener(
				ISearchBean<PmAddressType> searchBean) {
			IAddressSO addressSO;
			AddressResDTO resDto;
			System.out.println("============search bean state code====="
					+ getManagedBean().getStateCode());
			try {
				// SearchBean<PmAddressType> searchBean2=searchBean;
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO
						.setSearchAddressBean(new SearchBean<PmAddressType>());
				if (getManagedBean().getStateCode() != null
						&& !getManagedBean().getStateCode().endsWith("")) {
					SearchFilterBean stateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					stateSearchBean
							.setSearchField("pmAddress.pmState.stateCode");
					stateSearchBean.setSearchValue(getManagedBean()
							.getStateCode());
					searchBean.getDataFilterBeans().add(stateSearchBean);
				}

				if (getManagedBean().getDistrictId() != null) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDistrict.districtId");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDistrictId());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}

				if (getManagedBean().getMinistryId() != null) {
					SearchFilterBean ministrySearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					ministrySearchBean
							.setSearchField("pmOrgProfile.orgProfileId");
					ministrySearchBean.setSearchValue(getManagedBean()
							.getMinistryId());
					searchBean.getDataFilterBeans().add(ministrySearchBean);
				}

				if (getManagedBean().getDepartmentId() != null) {
					SearchFilterBean departMentSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					departMentSearchBean
							.setSearchField("pmOrgProfile.orgProfileId");
					departMentSearchBean.setSearchValue(getManagedBean()
							.getDepartmentId());
					searchBean.getDataFilterBeans().add(departMentSearchBean);
				}
				if (getManagedBean().getPtjId() != null) {
					SearchFilterBean ptjSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					ptjSearchBean.setSearchField("pmOrgProfile.orgProfileId");
					ptjSearchBean.setSearchValue(getManagedBean().getPtjId());
					searchBean.getDataFilterBeans().add(ptjSearchBean);
				}

				addressReqDTO.setSearchAddressBean(searchBean);

				addressSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
				resDto = addressSO.searchDeliveryAddress(addressReqDTO);

				if (resDto != null) {
					/*
					 * System.out.println("======display list size====" +
					 * resDto.getSearchAddressResultBean()
					 * .getResultList().size());
					 */
					return resDto.getSearchAddressResultBean();
				}

			} finally {

			}
			return null;
		}

	}

	/**
	 * @return the templateSpecCreate
	 */
	public IPagingDataModel<TplSpecMaster> getTemplateSpecCreate() {
		return templateSpecCreate;
	}

	/**
	 * @param templateSpecCreate
	 *            the templateSpecCreate to set
	 */
	public void setTemplateSpecCreate(
			IPagingDataModel<TplSpecMaster> templateSpecCreate) {
		this.templateSpecCreate = templateSpecCreate;
	}

	/**
	 * @return the currentTab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * @param currentTab
	 *            the currentTab to set
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * @return the outputTab
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * @param outputTab
	 *            the outputTab to set
	 */
	public void setOutputTab(int outputTab) {
		this.outputTab = outputTab;
	}

	/**
	 * @return the searchDeliveryAddress
	 */
	public IPagingDataModel<PmAddressType> getSearchDeliveryAddress() {
		return searchDeliveryAddress;
	}

	/**
	 * @param searchDeliveryAddress
	 *            the searchDeliveryAddress to set
	 */
	public void setSearchDeliveryAddress(
			IPagingDataModel<PmAddressType> searchDeliveryAddress) {
		this.searchDeliveryAddress = searchDeliveryAddress;
	}

	/**
	 * To redirect BO prepareSpec Screen..
	 */
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
		SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) obj;
		this.getManagedBean().setSpecificationDataType(quotationTenderDataType);
		retriveSpecdetails(quotationTenderDataType.getQtId().longValue());

		FacesContext fc = FacesContext.getCurrentInstance();
		String taskName = res.getTaskName();
		if (SCProcessNameConst.PREPARE_SPECIFICATION.equalsIgnoreCase(taskName)) {
			ScQt scQt = this.getManagedBean().getScQt();
			if (null == scQt.getEstimatedAmount()) {
				this.getManagedBean().getScQt().setPanel(true);
				this.getManagedBean().getScQt().setOpenFor("A");
				this.getManagedBean().getScQt()
						.setPmProcurementCategory(new PmParameter());
			}
			if (null == scQt.getZoneLocationLevel()) {
				this.getManagedBean().getScQt()
						.setZoneLocationLevel(new PmParameter());
			}

			if (this.getManagedBean().getScQt().isPanel()) {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BUSINESS_OWNER_SPEC_PREPARATION);
			} else {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BO_SPEC_CREATION_PAGE);
			}

		} else if (SCProcessNameConst.CONSOLIDATE_SPECIFICATION
				.equalsIgnoreCase(taskName)) {
			ScQt scQt = this.getManagedBean().getScQt();
			if (null == scQt.getEstimatedAmount()) {
				this.getManagedBean().getScQt().setPanel(true);
				this.getManagedBean().getScQt().setOpenFor("A");
				this.getManagedBean().getScQt()
						.setPmProcurementCategory(new PmParameter());
			}

			if (null == scQt.getZoneLocationLevel()) {
				this.getManagedBean().getScQt()
						.setZoneLocationLevel(new PmParameter());
			}
			if (this.getManagedBean().getScQt().isPanel()) {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BUSINESS_OWNER_SPEC_PREPARATION);
			} else {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BO_SPEC_CREATION_PAGE);
			}
		} else {
			if (this.getManagedBean().getScQt().getFulfilmentType() == null) {

				this.getManagedBean().getScQt()
						.setFulfilmentType(new PmParameter());
			}
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.SPECIFICICATON_PREPARATION);
		}

	}

	public String createCommitteeTemplateSpec(TplSpecMaster tplSpecMaster) {
		ScQtDoc scqtDocDest = null;
		try {
			scqtDocDest = constructQtDocByType(tplSpecMaster, "1");
			this.getManagedBean().setScqtDoc(scqtDocDest);

			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			specMaster1.setSpecMasterId(new Long(scqtDocDest.getTplSpecMaster()
					.getSpecMasterId()));
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);
			scqtDocDest.getTplSpecMaster().setSpecMasterId(null);
			scqtDocDest.getTplSpecMaster().setChangedDate(null);

		} finally {

		}
		return "/xhtml/sc/main/technicalCommitteeTemplate.xhtml?faces-redirect=true";

	}

	public String createTemplateSpec(TplSpecMaster tplSpecMaster) {
		ScQtDoc scqtDocDest = null;
		try {
			scqtDocDest = constructQtDocByType(tplSpecMaster, "1");

			TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster1 = new TplSpecMaster();
			specMaster1.setSpecMasterId(new Long(scqtDocDest.getTplSpecMaster()
					.getSpecMasterId()));
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			templateSpecBean.setSpecMaster(specMaster1);
			specBB.retrieveTemplateSpecMaster(templateSpecBean);
			scqtDocDest.getTplSpecMaster().setSpecMasterId(null);
			scqtDocDest.getTplSpecMaster().setChangedDate(null);
			this.getManagedBean().setScqtDoc(scqtDocDest);
		} finally {

		}
		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";

	}

	private ScQtDoc constructQtDocByType(TplSpecMaster specMaster,
			String docType) {
		ScQtDoc scqtDocDest = new ScQtDoc();
		setUserDetails(scqtDocDest);
		PmStatus status = specMaster.getStatus();
		if (null == status) {
			status = new PmStatus();
		}
		status.setStatusId(60550L);
		specMaster.setStatus(status);
		scqtDocDest.setTplSpecMaster(specMaster);

		scqtDocDest.setTplDocMaster(null);
		ScQtCheckList checkList = new ScQtCheckList();
		checkList.setDocCategory(docType);

		PmParameter parameter = new PmParameter();
		parameter.setParameterId(185L);
		PmParameter action = new PmParameter();
		action.setParameterId(689L);
		checkList.setChecklistSeq(1);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(true);
		checkList.setAction(action);
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setDefinationType(parameter);
		scqtDocDest.setScQtCheckList(checkList);
		setUserDetails(checkList);
		return scqtDocDest;
	}

	private QuationTenderReqDTO constructSpecCommitteDtls() {
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		ScQt scQt = this.getManagedBean().getScQt();

		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (techCheckList != null && !techCheckList.isEmpty()) {
			localChecklist.addAll(techCheckList);
			scQt.setScQtChecklists(localChecklist);
		}
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (financialCheckList != null && !financialCheckList.isEmpty()) {
			localChecklist.addAll(financialCheckList);
			scQt.setScQtChecklists(localChecklist);
		}
		List<ScQtCheckList> technicalScoring = this.getManagedBean()
				.getTechnicalScoring();
		if (technicalScoring != null && !technicalScoring.isEmpty()) {
			System.out.println("@@@@@@@@@@@@@@@@"
					+ technicalScoring.get(0).getChangedDate());
			for (ScQtCheckList scQtCheckList : technicalScoring) {
				System.out.println("===score for technical"
						+ scQtCheckList.getQtScoreWeightage().getWeightage());
			}
			localChecklist.addAll(technicalScoring);
			scQt.setScQtChecklists(localChecklist);
		}
		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();
		if (financialScoring != null && !financialScoring.isEmpty()) {

			List<ScQtCheckList> finSpecs = constructFinSpecsNSocre(financialScoring
					.get(0));
			financialScoring.remove(0);
			localChecklist.addAll(finSpecs);
			localChecklist.addAll(financialScoring);
			scQt.setScQtChecklists(localChecklist);
		}
		/*
		 * ScQtScoreOfferPrice
		 * packageOfferPrice=this.getManagedBean().getPackageScoreSetting();
		 * if(packageOfferPrice!=null){ packageOfferPrice.setChangedBy(1L);
		 * packageOfferPrice.setCreatedBy(1L);
		 * packageOfferPrice.setCreatedDate(new
		 * Timestamp(System.currentTimeMillis()));
		 * 
		 * }
		 */
		List<ScQtSchedule> freqschedule = this.getManagedBean()
				.getScheduleList();
		List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
		if (freqschedule != null && !freqschedule.isEmpty()) {
			String Startmonth = "";
			String endmonth = "03";
			for (ScQtSchedule schedule : freqschedule) {
				if (schedule.getStartMonth().equalsIgnoreCase("0")) {
					Startmonth = "01";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("1")) {
					Startmonth = "02";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("2")) {
					Startmonth = "03";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("3")) {
					Startmonth = "04";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("4")) {
					Startmonth = "05";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("5")) {
					Startmonth = "06";
				}

				if (schedule.getStartMonth().equalsIgnoreCase("6")) {
					Startmonth = "07";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("7")) {
					Startmonth = "08";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("8")) {
					Startmonth = "09";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("9")) {
					Startmonth = "10";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("10")) {
					Startmonth = "11";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("11")) {
					Startmonth = "12";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}

				if (schedule.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}

				if (schedule.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}
				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/" + Startmonth + "/"
						+ schedule.getStartYear();
				String toDate = date + "/" + endmonth + "/"
						+ schedule.getEndYear();
				// System.out.println("======display schedule No==="+schedule.getScheduleNo());
				schedule.setScheduleNo(schedule.getScheduleNo());
				schedule.setScheduleType(1);
				try {
					schedule.setFromDate(df.parse(fromdate));
					schedule.setToDate(df.parse(toDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				schedule.setScQt(this.getManagedBean().getScQt());
				localScheduleList.add(schedule);
				scQt.setScQtSchedules(localScheduleList);

			}
		}

		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getProgressivePaymentList();
		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
			String Startmonth = "";
			String endmonth = "";
			for (ScQtProgressivePayment schedule : progressivePayemnt) {
				if (schedule.getStartMonth().equalsIgnoreCase("0")) {
					Startmonth = "01";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("1")) {
					Startmonth = "02";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("2")) {
					Startmonth = "03";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("3")) {
					Startmonth = "04";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("4")) {
					Startmonth = "05";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("5")) {
					Startmonth = "06";
				}

				if (schedule.getStartMonth().equalsIgnoreCase("6")) {
					Startmonth = "07";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("7")) {
					Startmonth = "08";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("8")) {
					Startmonth = "09";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("9")) {
					Startmonth = "10";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("10")) {
					Startmonth = "11";
				}
				if (schedule.getStartMonth().equalsIgnoreCase("11")) {
					Startmonth = "12";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("0")) {
					endmonth = "01";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("1")) {
					endmonth = "02";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("2")) {
					endmonth = "03";
				}

				if (schedule.getEndMonth().equalsIgnoreCase("3")) {
					endmonth = "04";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("4")) {
					endmonth = "05";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("5")) {
					endmonth = "06";
				}

				if (schedule.getEndMonth().equalsIgnoreCase("6")) {
					endmonth = "07";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("7")) {
					endmonth = "08";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("8")) {
					endmonth = "09";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("9")) {
					endmonth = "10";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("10")) {
					endmonth = "11";
				}
				if (schedule.getEndMonth().equalsIgnoreCase("11")) {
					endmonth = "12";
				}
				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/" + Startmonth + "/"
						+ schedule.getStartYear();
				String toDate = date + "/" + endmonth + "/"
						+ schedule.getEndYear();
				try {
					schedule.setFromDate(df.parse(fromdate));
					schedule.setToDate(df.parse(toDate));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				schedule.setScQt(this.getManagedBean().getScQt());
				paymentList.add(schedule);
				scQt.setScQtProgressivePayments(paymentList);

			}
		}

		List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
		ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
				.getTechnicalScoreSetting();
		if (technicalScoreSetting != null) {
			scoreSetting.add(technicalScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting financialScoreSetting = this.getManagedBean()
				.getFinancialScoreSetting();
		if (financialScoreSetting != null) {
			scoreSetting.add(financialScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting demoScoreSetting = this.getManagedBean()
				.getDemoScoreSetting();
		if (demoScoreSetting != null) {
			scoreSetting.add(demoScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting packageScoreSetting = this.getManagedBean()
				.getRecommendationPackageScore();
		if (packageScoreSetting != null) {
			scoreSetting.add(packageScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		ScQtScoreSetting itemScoreSetting = this.getManagedBean()
				.getRecommendationScore();
		if (itemScoreSetting != null) {
			scoreSetting.add(itemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		List<ScQtDeliveryLoc> localDeliveryList = new ArrayList<ScQtDeliveryLoc>();
		// List<BriefingZoneBean> deliveryList=
		// this.getManagedBean().getDeliveryLocBean();
		List<BriefingZoneBean> deliveryList = this.getManagedBean()
				.getAddressZoneBean();
		if (deliveryList != null && !deliveryList.isEmpty()) {
			for (BriefingZoneBean briefingZoneBean : deliveryList) {
				if (briefingZoneBean.getDeliveryLocList() != null
						&& !briefingZoneBean.getDeliveryLocList().isEmpty()) {

					localDeliveryList.addAll(briefingZoneBean
							.getDeliveryLocList());

				}
				scQt.setScQtDeliveryLocs(localDeliveryList);
			}

		}
		List<ScQtDeliveryLoc> nonZoneList = this.getManagedBean()
				.getDeliveryNonZoneList();
		if (nonZoneList != null && !nonZoneList.isEmpty()) {
			scQt.setScQtDeliveryLocs(nonZoneList);
		}

		scQt.setScQtCommittees(null);
		reqDTO.setQtdetail(scQt);
		return reqDTO;
	}

	public String updateSpecCommitteBpmTask(String action) {
		QuationTenderReqDTO spReqDTO = constructSpecCommitteDtls();
		if ("SUBMIT".equalsIgnoreCase(action)) {
			spReqDTO.getQtdetail().setRequestItems(
					constructRequestItems(this.getManagedBean().getScQt()));
		}
		spReqDTO.setOutcome(action);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTDetailSO qtDetailSO = getDetailSO();
		// QuationTenderResDTO resDTO = new QuationTenderResDTO();
		try {
			QuationTenderResDTO resDTO = qtDetailSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getQtDetail();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										getMessgeConstant(action),
										new String[] { "Specification",
												qt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			qtDetailSO = null;
		}
		return null;

	}

	private QTTemplateDocResDTO saveTempleateSpecDtls(ScQtDoc qtDoc,
			String specType) {
		IQTTemplateSO qtTemplateSO = null;

		IOrgProfileSO ProfileSo = null;
		QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();
		QTTemplateDocResDTO resDTO = new QTTemplateDocResDTO();
		ScQtDoc scQtDoc = qtDoc;
		try {
			if (null == scQtDoc) {
				scQtDoc = this.getManagedBean().getScqtDoc();
				PmStatus status = new PmStatus();
				status.setStatusId(60550L);
				scQtDoc.getScQtCheckList().setPmStatus(status);
			}

			ProfileSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = ProfileSo
					.retrieveOrgProfileByProfileId(profileReq);
			PmParameter parameter = scQtDoc.getTplSpecMaster()
					.getTemplateTypeCode();
			parameter.setParameterId(146L);
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

			scQtDoc.getScQtCheckList().setTitle(specMaster.getSpecName());

			java.util.Date date = new java.util.Date(); // Right now
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					date.getTime());
			if ("1".equalsIgnoreCase(specType)) {

				List<TplSpecQuestion> questions = getManagedBean()
						.getTemplateSpecBean().getSpecMaster()
						.getTplSpecQuestions();

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
					setUserDetails(tpl1);
					tpl1.setChangedDate(null);
					questions2.add(tpl1);
				}
				specMaster.setTplSpecQuestions(questions2);
				PmParameter pmParameter = new PmParameter();
				pmParameter.setParameterId(125l);
				specMaster.setSpecTypeCode(pmParameter);

			} else {
				List<TplSpecQuestion> questions = getManagedBean()
						.getTemplateSpecBean().getSpecMaster()
						.getTplSpecQuestions();

				// TplSpecQuestion queston = questions.remove(0);
				List<TplSpecQuestion> questions1 = new ArrayList<TplSpecQuestion>();

				questions1.addAll(questions);
				List<TplSpecQuestion> questions2 = new ArrayList<TplSpecQuestion>();
				for (TplSpecQuestion tpl : questions1) {

					// TplSpecQuestion tpl2 = new TplSpecQuestion();
					if ("I".equalsIgnoreCase(tpl.getQuestionType())) {

						TplSpecQuestion tpl1 = (TplSpecQuestion) BeanUtils
								.cloneBean(tpl);

						tpl1.setSpecQuestionId(null);
						tpl1.setTplSpecMaster(specMaster);
						tpl1.setTplSpecAnswers(null);
						tpl1.setTplSpecScorings(null);
						tpl1.setTplSpecTierQties(null);
						setUserDetails(tpl1);
						tpl1.setChangedDate(null);
						questions2.add(tpl1);

					}
				}

				specMaster.setTplSpecQuestions(questions2);

				PmParameter pmParameter = new PmParameter();
				pmParameter.setParameterId(126l);
				specMaster.setSpecTypeCode(pmParameter);
			}

			specMaster.setTemplateTypeCode(parameter);
			specMaster.setProcessType("SC");
			specMaster.setOrgProfile(profileRes.getPmOrgProfile());
			// specMaster.setDurationMonth(12);
			specMaster.setTemplateStatus("D");
			specMaster.setScQtDocs(null);
			specMaster.setScQuoteDocs(null);
			specMaster.setBrandNew(false);
			specMaster.setRecordStatus(true);
			setUserDetails(specMaster);

			scQtDoc.setScQt(this.getManagedBean().getScQt());

			// scQtDOc.setScQtCheckList(checkList);
			scQtDoc.setTplSpecMaster(specMaster);

			docReqDTO.setScQtDoc(scQtDoc);
			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);

			resDTO = qtTemplateSO.saveOrUpdateTemplateDoc(docReqDTO);

		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resDTO;
	}

	private List<ScRequestItem> constructRequestItems(ScQt scQt) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		ISearchBean<TplSpecQuestion> searchBean = new SearchBean<TplSpecQuestion>();
		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("tplSpecMaster.scQtDocs.scQt.qtId",
						SearchOperator.OPERATOR_EQUALS.value(), scQt.getQtId()));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("questionType",
						SearchOperator.OPERATOR_EQUALS.value(), "I"));
		reqDTO.setQuestionSearchBean(searchBean);
		TemplateSpecResDTO specResDTO = templateSpecSO
				.getTemplateSpecItems(reqDTO);
		ISearchResultBean<TplSpecQuestion> resultBean = specResDTO
				.getQuestionsResultBean();
		List<TplSpecQuestion> questions = resultBean.getResultList();
		List<ScRequestItem> items = null;
		if (null != questions) {
			items = new ArrayList<ScRequestItem>();
			for (TplSpecQuestion question : questions) {
				ScRequestItem requestItem = new ScRequestItem();
				requestItem.setTplSpecQuestion(question);
				requestItem.setScQt(scQt);
				setUserDetails(requestItem);
				items.add(requestItem);
			}
		}
		return items;
	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteTreeNode() {
		TreeNode selectedTreeNode = this.getManagedBean().getSelectedTreeNode();
		TplSpecZoneDtl zoneDtls = (TplSpecZoneDtl) selectedTreeNode
				.getChildren().get(0).getData();
		if (null != selectedTreeNode) {
			List<TreeNode> zoneTrees = this.getManagedBean()
					.getZoneGeneralTrees();
			for (TreeNode node : zoneTrees) {
				if (zoneDtls.getZoneName().equalsIgnoreCase(
						((TplSpecZoneDtl) node.getChildren().get(0).getData())
								.getZoneName())) {
					zoneTrees.remove(node);
					break;
				}
			}
			this.getManagedBean().setZoneGeneralTrees(zoneTrees);
		} else {
			throw new EpException(MessageConst.ERROR_715006);
		}
	}

	private void updateCategoryCodesToQt(List<ScQtCategory> categories,
			ScQt scQt) {
		if (null != categories) {
			for (ScQtCategory category : categories) {
				category.setPmCategoryL1(category.getPmCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setPmCategoryL2(category.getPmCategoryL3()
						.getPmCategoryL2());
				category.setScQt(scQt);
				setUserDetails(category);
			}
			scQt.setScQtCategorys(categories);
		} else {
			throw new EpException(MessageConst.ERROR_100022);
		}
	}
}
