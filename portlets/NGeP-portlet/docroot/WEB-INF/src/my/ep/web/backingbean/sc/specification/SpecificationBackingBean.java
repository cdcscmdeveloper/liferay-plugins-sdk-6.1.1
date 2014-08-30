/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
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
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
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
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
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
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
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
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.specification.SpecificationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.primefaces.context.RequestContext;
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
	private String textBeforeSign;

	private PmDigiSign pmDigiSign;

	public SpecificationBackingBean() {

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String businessOwnerBackAction() {
		this.getManagedBean().setActiveIndex(0);
		this.getManagedBean().setTempIndex(0);
		this.getManagedBean().setCircularType("");
		this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		this.getManagedBean().setPanelItemCategory(null);
		this.getManagedBean().setSelectPanelItem(false);
		return "specificationview.xhtml";
	}

	private List<ScQtLocCriteria> getQuoteZones(List<TreeNode> treeNodes,
			ScQtSupplierCriteria supplierCriteria) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				List<TreeNode> stateNodes = treeNode.getChildren();
				for (TreeNode treeNode2 : stateNodes) {
					List<TreeNode> district = treeNode2.getChildren();
					if (null != district && !district.isEmpty()) {
						for (TreeNode devision : district) {
							if (devision.getChildCount() > 0) {
								for (TreeNode districtNode : devision
										.getChildren()) {
									ScQtLocCriteria criteria = ((ScQtLocCriteria) districtNode
											.getData());
									criteria.setPmDistrict(((ScQtLocCriteria) districtNode
											.getData()).getPmDistrict());
									criteria.setPmDivision(((ScQtLocCriteria) districtNode
											.getParent().getData())
											.getPmDivision());
									criteria.setPmState(((ScQtLocCriteria) treeNode2
											.getData()).getPmState());
									criteria.setScQtSupplierCriteria(supplierCriteria);
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
								criteria.setScQtSupplierCriteria(supplierCriteria);
								setUserDetails(criteria);
								results.add(criteria);
							}

						}
					} else {

						ScQtLocCriteria criteria = (ScQtLocCriteria) treeNode2
								.getData();
						criteria.setScQtSupplierCriteria(supplierCriteria);
						setUserDetails(criteria);

						results.add(criteria);
					}
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

	// Method to save the specifcation Details..
	public void saveSpecificationDtls() {
		validateBusinessOwner();
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_700004, new String[] {
								"Specification For ",
								this.getManagedBean().getScQt().getQtNo() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

	}

	public void validateBusinessOwner() {

		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();

		SpecificationResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		boolean selectPanelItem = this.getManagedBean().isSelectPanelItem();
		CtPanelItem panelItem = null;
		CtCircularVer circularVer = null;
		List<CtPanelItem> panelList = this.getManagedBean()
				.getSelectpanelItem();

		panelItem = this.getManagedBean().getCtPanelItem();

		circularVer = this.getManagedBean().getCtCircularVer();

		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		switch (activeIndex) {
		case 0:
			ScQt scQt = prepareSpecReq().getScQt();
			specReqDTO.setScQt(scQt);
			specSO.validateBoGeneralInfo(specReqDTO);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
			setProcurementMode(resDTO.getScQt());
			// this.getManagedBean().setQt(resDTO.getQtDetail());
			break;
		case 1:
			ScQt scQtSupplier = prepareSpecReq().getScQt();
			specReqDTO.setScQt(scQtSupplier);
			specSO.validateSupplierQulificationInfo(specReqDTO);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveBusinessOwnerQualificationCriteri();

			break;
		case 2:
			ScQt techSpec = prepareSpecReq().getScQt();

			specReqDTO.setScQt(techSpec);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveTechnicalSpecification();
			break;
		case 3:
			ScQt finSpec = prepareSpecReq().getScQt();

			specReqDTO.setScQt(finSpec);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveFinancialSpecification();
			break;
		case 4:
			ScQt remarks = prepareSpecReq().getScQt();

			specReqDTO.setScQt(remarks);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveApprovalRemarks();
			break;
		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}
		this.getManagedBean().setSelectPanelItem(selectPanelItem);
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setCtPanelItem(panelItem);
		this.getManagedBean().setCtCircularVer(circularVer);
		this.getManagedBean().setSelectpanelItem(panelList);

	}

	public void secretarySubmitTask() {
		ICommitteSO committeSO = null;

		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType(QTConstants.DOCTYPE_SPEC_COMMITTE);

			reqDTO.setSubject(this.getManagedBean().getSubject());
			reqDTO.setBodyText(this.getManagedBean().getMessage());
			committeSO.sendCommitteEndorseNotification(reqDTO);

		} finally {

		}
	}

	public void saveSpecificationCommitteeDtls() {

		try {
			validatePrepareQtChecklist();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] {
											"Specification Details For ",
											this.getManagedBean().getScQt()
													.getQtNo() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		} finally {

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
		this.getManagedBean().setScQtCheckSt2techProposal(
				new ArrayList<ScQtCheckList>());
		this.getManagedBean().setScQtCheckSt2financeProposalList(
				new ArrayList<ScQtCheckList>());

		if (!this.getManagedBean().getScQt().isPanel()) {

			if (this.getManagedBean().getScQt().isPanel() != this
					.getManagedBean().isPanelChange()) {
				removespecDtls();
				this.getManagedBean().getScQt().setZonal(false);
				getManagedBean().setZoneGeneralTrees(null);
				getManagedBean().setZoneTrees(null);
			}

			return "specificationCreation.xhtml?faces-redirect=true";

		} else if (this.getManagedBean().getScQt().isPanel()
				&& QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
						.getManagedBean().getCircularType())
				&& QTConstants.ITEM_SPECIFIC_TYPE.equalsIgnoreCase(this
						.getManagedBean().getPanelItemCategory())
				|| QTConstants.ITEM_GENERAL_TYPE.equalsIgnoreCase(this
						.getManagedBean().getPanelItemCategory())) {

			ScQt scQt = this.getManagedBean().getScQt();
			scQt.setZonal(false);
			this.getManagedBean().setCtZoneSuppliers(new ArrayList<TreeNode>());
			ScQtSupplierCriteria supplierCriteria = scQt
					.getScQtSupplierCriteria();
			if (supplierCriteria != null
					&& this.getManagedBean().getScQt().isPanel() != this
							.getManagedBean().isPanelChange()) {
				supplierCriteria.setCtPanelItem(this.getManagedBean()
						.getCtPanelItem());
				removespecDtls();

			} else {
				if (supplierCriteria != null
						&& !supplierCriteria.getCtPanelItem().getPanelItemId()
								.equals(this.getManagedBean().getCtPanelItem())) {
					supplierCriteria.setCtPanelItem(this.getManagedBean()
							.getCtPanelItem());
					removespecDtls();
				}
			}

			if (null == this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()) {
				supplierCriteria = new ScQtSupplierCriteria();
				supplierCriteria.setScQt(scQt);
				supplierCriteria.setCtPanelItem(this.getManagedBean()
						.getCtPanelItem());
				setUserDetails(supplierCriteria);
				this.getManagedBean().getScQt()
						.setScQtSupplierCriteria(supplierCriteria);
			}

			this.getManagedBean().setScQtCheckSt2techProposal(null);
			return "specificationCreation.xhtml?faces-redirect=true";
			// return "searchCircularPanelContract.xhtml?faces-redirect=true";
		}

		else if (this.getManagedBean().getScQt().isPanel()
				&& QTConstants.CIRCULAR_CENTRAL_CONTRACT_TYPE
						.equalsIgnoreCase(this.getManagedBean()
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
		getManagedBean().setCtSearchBean(new CtCircularSearchBean());
	}

	public String backAction() {

		return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";

	}

	public String committeeBackAction() {
		// this.getManagedBean().setScQtCheckSt2techProposal(null);
		// retriveTechnicalCheckList();
		return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

	}

	public String businessOwnerTemplateSpec() {
		return "/xhtml/sc/main/createTemplate.xhtml?faces-redirect=true";
	}

	public String retriveTemplatespec() {
		return "/xhtml/sc/main/createCommitteeTemplate.xhtml?faces-redirect=true";
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

		}

		ScQtCategory category = new ScQtCategory();
		// .setScQt(scQt);
		category.setPmCategoryL1(new PmCategoryL1());
		category.setPmCategoryL2(new PmCategoryL2());
		category.setPmCategoryL3(new PmCategoryL3());
		qtCategoryList.add(category);
		this.getManagedBean().setSuppCriteriaopenList(qtCategoryList);

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

	}

	private ISearchResultBean<ScQtCheckList> retriveChecklistByIdAndTitle(
			String title) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTo = null;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			SearchBean<ScQtCheckList> searchBean = new SearchBean<ScQtCheckList>();

			String doccategory = QTConstants.FINANCIAL_DOC_TYPE;
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			usernameSearchBean.setSearchField("scQt.qtId");
			usernameSearchBean.setSearchValue(getManagedBean().getScQt()
					.getQtId());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

			SearchFilterBean idNoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("title");
			idNoSearchBean.setSearchValue(title);
			searchBean.getDataFilterBeans().add(idNoSearchBean);

			SearchFilterBean specType = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			specType.setSearchField("docCategory");
			specType.setSearchValue(doccategory);
			searchBean.getDataFilterBeans().add(specType);

			reqDTO.setTitleSearchChecklistBean(searchBean);

			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDTo = specSo.retrieveChecklistByIdAndTitle(reqDTO);

		} finally {

		}
		return resDTo.getSpecResultBean();
	}

	public String saveEditTemplateSpec(String processName) {
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

			List<ScQtDoc> docs = new ArrayList<ScQtDoc>();
			docs.add(qtDoc);
			qtTemplateReqDTO.setScQtDoc(docs);

			qtTemplateSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTTemplateSO.class);
			QTTemplateDocResDTO resDTO = qtTemplateSO
					.saveOrUpdateTemplateDoc(qtTemplateReqDTO);

			List<ScQtDoc> savedSpecs = resDTO.getSavedDocs();
			String specType = this.getManagedBean().getSpecType();
			ScQtCheckList savedCheckList = null;
			if (null != savedSpecs && savedSpecs.size() > 1) {
				for (int i = 0; i < savedSpecs.size(); i++) {
					if (SourcingConstants.TECHINICAL_STAGE
							.equalsIgnoreCase(savedSpecs.get(i)
									.getScQtCheckList().getDocCategory())) {
						savedCheckList = savedSpecs.get(i).getScQtCheckList();
					}
				}

			} else {
				if (SourcingConstants.TECHINICAL_STAGE
						.equalsIgnoreCase(specType)) {
					savedCheckList = savedSpecs.get(0).getScQtCheckList();

				} else if (SourcingConstants.FINANCIAL_STAGE
						.equalsIgnoreCase(specType)) {
					savedCheckList = savedSpecs.get(0).getScQtCheckList();

				}

			}
			int index = this.getManagedBean().getSelectedCheckList();
			if (SourcingConstants.TECHINICAL_STAGE.equalsIgnoreCase(specType)) {
				this.getManagedBean().getScQtCheckSt2techProposal()
						.remove(index);
				this.getManagedBean().getScQtCheckSt2techProposal()
						.add(index, savedCheckList);

			} else if (SourcingConstants.FINANCIAL_STAGE
					.equalsIgnoreCase(specType)) {
				this.getManagedBean().getScQtCheckSt2financeProposalList()
						.remove(index);
				this.getManagedBean().getScQtCheckSt2financeProposalList()
						.add(index, savedCheckList);
			}

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
		if (null == processName || "".equals(processName)) {

			return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
		} else {
			return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

		}

	}

	public void businessOwnerTabChange(int activeIndex) {
		int previousIndex = this.getManagedBean().getActiveIndex();

		IQTSpecSO specSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

		switch (previousIndex) {
		case 0:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			specSo.validateBoGeneralInfo(specReqDTO);
			// retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId());
			break;
		case 1:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			specSo.validateSupplierQulificationInfo(specReqDTO);
			// retrieveCommitteeAppointmentTabDtl();
			break;

		case 2:

			break;
		case 3:

			break;

		case 4:
			break;

		}
		switch (activeIndex) {
		case 0:
			// retriveSpecdetails(this.getManagedBean().getScQt().getQtId(),
			// "QT");
			break;
		case 1:
			retriveBusinessOwnerQualificationCriteri();
			break;

		case 2:

			retriveTechnicalSpecification();
			break;
		case 3:
			// detailSO.validateApprovalDetails(prepareQTInfo());
			retriveFinancialSpecification();
			break;

		case 4:
			retriveApprovalRemarks();
			break;

		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}
		this.getManagedBean().setActiveIndex(activeIndex);

	}

	public void qtChecklistTabChange(int activeIndex) {

		int previousIndex = this.getManagedBean().getActiveIndex();

		IQTSpecSO specSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

		switch (previousIndex) {
		case 0:
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			specSo.validateSpecCommitteGeneralInfo(specReqDTO);

			break;
		case 1:
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			// specSo.validateSupplierQulificationInfo(specReqDTO);

			break;

		case 2:
			ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())) {
				// specReqDTO.setScQt(constructSpecCommitteDtls().getQtdetail());
				// specSo.validateSupplierQulificationInfo(specReqDTO);

			}

			if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())) {
				specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
				specSo.validateCheckList(specReqDTO);

			}

			break;
		case 3:

			break;

		case 4:
			ScQt scQtTechScore = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scQtTechScore
					.getEvaluationType())) {
				List<ScQtCheckList> financialScoring = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring);
				specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
				specReqDTO.setScQt(scQtTechScore);
				specSo.validateSCScoreSettings(specReqDTO);

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scQtTechScore
					.getEvaluationType())) {
				List<ScQtCheckList> technicalScoring = this.getManagedBean()
						.getTechnicalScoring();
				ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
						.getTechnicalScoreSetting();

				if (technicalScoreSetting != null) {
					specReqDTO.setScoreSetting(technicalScoreSetting);
				}
				ScQtScoreSetting demoScoreSetting = this.getManagedBean()
						.getDemoScoreSetting();
				if (demoScoreSetting != null) {
					specReqDTO.setScoreSetting(demoScoreSetting);
				}
				specReqDTO.setCheckLists(technicalScoring);
				specReqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
				specReqDTO.setScQt(scQtTechScore);
				specSo.validateSCScoreSettings(specReqDTO);

			}
			break;

		case 5:

			ScQt scQtFinScore = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scQtFinScore
					.getEvaluationType())) {
				List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
				ScQtScoreSetting packageScoreSetting = this.getManagedBean()
						.getRecommendationPackageScore();
				if (null != packageScoreSetting
						&& packageScoreSetting.getSettingType() == 4) {
					scoreSetting.add(packageScoreSetting);

				}

				ScQtScoreSetting itemScoreSetting = this.getManagedBean()
						.getRecommendationScore();
				if (null != itemScoreSetting
						&& itemScoreSetting.getSettingType() == 5) {
					scoreSetting.add(itemScoreSetting);

				}
				specReqDTO.setScoreSettings(scoreSetting);
				specReqDTO.setScQt(scQtFinScore);
				specSo.validateSCRecommandationSettings(specReqDTO);

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scQtFinScore
					.getEvaluationType())) {
				List<ScQtCheckList> financialScoring = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting
						&& financialScoreSetting.getSettingType() == 2) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring);
				specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
				specReqDTO.setScQt(scQtFinScore);
				specSo.validateSCScoreSettings(specReqDTO);
			}
			break;
		case 6:

			ScQt scQtRecommendation = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(scQtRecommendation.getEvaluationType())) {

				if (scQtRecommendation.isZonal()) {
					if (null == scQtRecommendation.getScQtDeliveryLocs()) {
						List<BriefingZoneBean> zoneBean = this.getManagedBean()
								.getDeliveryLocBean();
						if (null != zoneBean) {
							for (BriefingZoneBean deliverybean : zoneBean) {
								if (null != deliverybean.getDeliveryLocList()) {
									scQtRecommendation
											.setScQtDeliveryLocs(deliverybean
													.getDeliveryLocList());
								}
							}
						}
					}
				}

				specReqDTO.setScQt(scQtRecommendation);
				specSo.validateSCdelieryLocs(specReqDTO);

			}

			if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtRecommendation.getEvaluationType())) {

				List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
				ScQtScoreSetting packageScoreSetting = this.getManagedBean()
						.getRecommendationPackageScore();
				if (null != packageScoreSetting
						&& packageScoreSetting.getSettingType() == 4) {
					scoreSetting.add(packageScoreSetting);

				}

				ScQtScoreSetting itemScoreSetting = this.getManagedBean()
						.getRecommendationScore();
				if (null != itemScoreSetting
						&& itemScoreSetting.getSettingType() == 5) {
					scoreSetting.add(itemScoreSetting);

				}
				specReqDTO.setScoreSettings(scoreSetting);
				specReqDTO.setScQt(scQtRecommendation);
				specSo.validateSCRecommandationSettings(specReqDTO);
			}

			break;

		case 7:
			ScQt deliveryAddress = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(deliveryAddress.getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {

					specReqDTO.setScQt(deliveryAddress);
					specSo.validateSCFulfillmentSchedules(specReqDTO);
				}

			}

			if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(deliveryAddress.getEvaluationType())) {

				if (deliveryAddress.isZonal()) {
					if (null == deliveryAddress.getScQtDeliveryLocs()) {
						List<BriefingZoneBean> zoneBean = this.getManagedBean()
								.getDeliveryLocBean();
						if (null != zoneBean) {
							for (BriefingZoneBean deliverybean : zoneBean) {
								if (null != deliverybean.getDeliveryLocList()) {
									deliveryAddress
											.setScQtDeliveryLocs(deliverybean
													.getDeliveryLocList());
								}
							}
						}
					}
				}

				specReqDTO.setScQt(deliveryAddress);
				specSo.validateSCdelieryLocs(specReqDTO);
			}

			break;

		case 8:

			ScQt fulfilMentDtls = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(fulfilMentDtls
					.getEvaluationType())) {
				// specReqDTO.setScQt(constructSpecCommitteDtls().getQtdetail());
				// specSo.validateSupplierQulificationInfo(specReqDTO);

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(fulfilMentDtls
					.getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					specReqDTO.setScQt(fulfilMentDtls);
					specSo.validateSCFulfillmentSchedules(specReqDTO);
				}
			}

			break;

		case 9:
			break;

		}
		switch (activeIndex) {
		case 0:

			retriveSpecdetails(this.getManagedBean().getScQt().getQtId(), "QT");
			break;
		case 1:
			retriveBusinessOwnerQualificationCriteri();
			break;

		case 2:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveTechnicalSpecification();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveTechnicalCheckList();
			}
			break;
		case 3:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveFinancialCheckList();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveFinancialCheckList();
			}
			break;

		case 4:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveFinancialScoring();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveTechnicalScoring();
			}
			break;

		case 5:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveRecommendationDetails();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveFinancialScoring();
			}
			break;

		case 6:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveDeliveryDetails();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveRecommendationDetails();
			}
			break;

		case 7:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					retriveFulfilmentDtls();
				} else if (ParameterConst.FULFILMENT_TYPE_ONE_OFF
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())) {
					retriveCommitteeEndorsment();

				}
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveDeliveryDetails();
			}
			break;

		case 8:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveCommitteeEndorsment();
			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					retriveFulfilmentDtls();
				} else if (ParameterConst.FULFILMENT_TYPE_ONE_OFF
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())) {
					retriveCommitteeEndorsment();

				}
			}
			break;

		case 9:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {

			}
			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				retriveCommitteeEndorsment();
			}
			break;

		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}

		this.getManagedBean().setActiveIndex(activeIndex);

	}

	/**
	 * To reload the data after saving...
	 */
	public void loadPrepareSpecDetails(Long qtId, String docType) {

		retriveSpecdetails(qtId, docType);
		retriveBusinessOwnerQualificationCriteri();
		retriveTechnicalSpecification();
		retriveFinancialSpecification();
		retriveApprovalRemarks();
		// this.getManagedBean().setActiveIndex(0);

	}

	/**
	 * To reload the data after saving...
	 */
	public void loadPrepareSpecCommitteeDetails(Long qtId, String docType) {

		retriveSpecdetails(qtId, docType);

		retriveBusinessOwnerQualificationCriteri();
		if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
				.getManagedBean().getScQt().getEvaluationType())) {
			retriveTechnicalSpecification();
		}
		if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
				.getManagedBean().getScQt().getEvaluationType())) {
			retriveTechnicalCheckList();
			retriveTechnicalScoring();
		}
		retriveFinancialCheckList();
		retriveFinancialScoring();
		retriveRecommendationDetails();
		retriveFulfilmentDtls();
		retriveDeliveryDetails();
		retriveCommitteeEndorsment();
		this.getManagedBean().setActiveIndex(0);

	}

	/**
	 * To get the Supplier Qualification Criteria Details for BO PrepareSpec..
	 */
	public void retriveBusinessOwnerQualificationCriteri() {

		IQTDetailSO QtDetailsSO;

		try {

			if (this.getManagedBean().getTempIndex() == 0) {

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
					this.getManagedBean().setSuppCriteriaopenList(
							categoryResults);
				}
				ScQt scQt = tenderResDTO.getQtDetail();
				this.getManagedBean().setAllStateCode(
						tenderResDTO.getStateCode());
				this.getManagedBean().setScQt(scQt);
				if (!scQt.isPanel()) {
					if (scQt.isZonal()
							&& scQt.getEstimatedAmount().intValue() <= 500000
							&& !scQt.getScQtSupplierCriteria().isAllState()

							&& null != this.getManagedBean()
									.getZoneGeneralTrees()
							&& this.getManagedBean().getZoneGeneralTrees()
									.isEmpty()) {
						retriveNonpanelZoneDtls(this.getManagedBean().getScQt()
								.getQtId(), "QT");

					}

					if (scQt.getEstimatedAmount().intValue() > 500000
							&& this.getManagedBean().getAllStateCode() == 3
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
					}
					if ((!scQt.isZonal())
							|| (scQt.getEstimatedAmount().intValue() > 500000 && !scQt
									.getScQtSupplierCriteria().isAllState())) {

						List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
						getManagedBean().setZoneTrees(null);
						if (null != tenderResDTO.getLocationDetails()) {

							for (TplSpecLoc localLoc : tenderResDTO
									.getLocationDetails()) {
								getManagedBean().setZoneTrees(
										new ArrayList<TreeNode>());
								ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
								locCriteria.add(criteria);
							}
							if (null != getManagedBean().getZoneTrees()
									&& getManagedBean().getZoneTrees().size() == 0) {
								getManagedBean().getZoneTrees().add(
										LocationSingleton.getInstance()
												.constuctTreeNode(locCriteria));
							}
						}

					}
				} else {
					this.getManagedBean().setCtPanelItem(
							scQt.getScQtSupplierCriteria().getCtPanelItem());

					this.getManagedBean().setCtCircularVer(
							scQt.getScQtSupplierCriteria().getCtPanelItem()
									.getCtCircularVer());
					this.getManagedBean().setPanelItemCategory(
							getParameterDesc(scQt.getScQtSupplierCriteria()
									.getCtPanelItem().getItemCategoryId()));
					List<CtPanelItem> panelList = this.getManagedBean()
							.getSelectpanelItem();
					panelList = new ArrayList<CtPanelItem>();

					panelList.add(this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getCtPanelItem());
					this.getManagedBean().setSelectpanelItem(panelList);

					if (null != this.getManagedBean().getCtCircularVer()) {
						this.getManagedBean().setCircularExpDate(
								this.getManagedBean().getCtCircularVer()
										.getEndDate());
						this.getManagedBean().setCirCularNo(
								this.getManagedBean().getCtCircularVer()
										.getCircularNo());
						this.getManagedBean().setCircularName(
								this.getManagedBean().getCtCircularVer()
										.getName());
						this.getManagedBean().setCircularType(
								this.getManagedBean().getCtCircularVer()
										.getDocType());
					}

					if (null != this.getManagedBean().getCtZoneSuppliers()
							&& this.getManagedBean().getCtZoneSuppliers()
									.isEmpty() && !scQt.isZonal()) {
						retriveZonesByCircularNo();
						if (null != this.getManagedBean().getCtPanelZones()
								&& !this.getManagedBean().getCtPanelZones()
										.isEmpty()) {
							scQt.setZonal(true);
						}
					} else if (scQt.isZonal()
							&& null != this.getManagedBean()
									.getCtZoneSuppliers()
							&& this.getManagedBean().getCtZoneSuppliers()
									.isEmpty()) {
						List<CtPanelZone> ctPanelZone = new ArrayList<CtPanelZone>();
						if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
							for (TplSpecZoneDtl zoneDtl : tenderResDTO
									.getZoneDetails()) {
								ctPanelZone.add(zoneDtl.getPanelZone());
							}
							getManagedBean()
									.getCtZoneSuppliers()
									.add(LocationSingleton
											.getInstance()
											.constuctPanelTreeNodes(ctPanelZone));
							this.getManagedBean().setCtzones(true);
						}

					}

				}
			}
		} finally {

		}

	}

	private void removespecDtls() {
		IQTSpecSO specSo = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setScQt(this.getManagedBean().getScQt());
			SpecificationResDTO resDTO = specSo.removeBOSpecDetails(reqDTO);
			this.getManagedBean().setScQt(resDTO.getScQt());
		} finally {
			specSo = null;
		}
	}

	private void createCircularItemSpec(CtPanelItem panelItem) {
		IParameterSO parameterSo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ScQtCheckList checkList = new ScQtCheckList();
			ScQtDoc scqtDocDest = new ScQtDoc();
			TplSpecMaster specMaster = new TplSpecMaster();

			ParameterReqDTO specType = new ParameterReqDTO();
			specType.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			specType.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
			ParameterResDTO specTyperesDTO = parameterSo
					.retrieveParameterByTypeAndCode(specType);

			ParameterReqDTO checkListActionType = new ParameterReqDTO();
			checkListActionType
					.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
			checkListActionType
					.setParameterCode(ParameterConst.CHECKLIST_ACTION_INPUT);
			ParameterResDTO checkListactionresDTO = parameterSo
					.retrieveParameterByTypeAndCode(checkListActionType);

			PmStatus status = new PmStatus();
			status.setStatusId(60550L);
			checkList.setTitle(panelItem.getItemName());
			checkList.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			checkList.setChecklistSeq(1);
			checkList.setScoringReq(true);
			checkList.setAction(checkListactionresDTO.getPmParameter());
			checkList.setScQt(this.getManagedBean().getScQt());
			checkList.setDefinationType(specTyperesDTO.getPmParameter());
			setUserDetails(checkList);
			checkList.setPmStatus(status);
			checkList.setManditory(false);
			checkList.setRecordStatus(true);
			checkList.setScQtDoc(scqtDocDest);

			scqtDocDest.setScQt(this.getManagedBean().getScQt());
			scqtDocDest.setTplSpecMaster(specMaster);
			scqtDocDest.setScQtCheckList(checkList);
			// scqtDocDest.setTplDocMaster(null);
			// scqtDocDest.setTplDocMaster(null);
			scqtDocDest.setRecordStatus(true);
			setUserDetails(scqtDocDest);

			IOrgProfileSO profileSo = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
			profileReq.setOrgProfileId(1L);
			OrgProfileResDTO profileRes = profileSo
					.retrieveOrgProfileByProfileId(profileReq);

			ParameterReqDTO techSpecType = new ParameterReqDTO();
			techSpecType
					.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
			techSpecType.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			ParameterResDTO techspecTyperesDTO = parameterSo
					.retrieveParameterByTypeAndCode(techSpecType);

			ParameterReqDTO templateTypeReqDTO = new ParameterReqDTO();
			templateTypeReqDTO
					.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
			templateTypeReqDTO
					.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
			ParameterResDTO templateTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(templateTypeReqDTO);

			ParameterReqDTO procurementReqDTO = new ParameterReqDTO();
			procurementReqDTO
					.setParameterType(ParameterTypeConst.PROCUREMENT_CATEGORY);
			procurementReqDTO
					.setParameterCode(ParameterConst.PROCUREMENT_CATEGORY_PERALATAN_PEJABAT);
			ParameterResDTO procurementResDTO = parameterSo
					.retrieveParameterByTypeAndCode(procurementReqDTO);

			ParameterReqDTO itemTypeReqDTO = new ParameterReqDTO();
			itemTypeReqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
			itemTypeReqDTO
					.setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE);
			ParameterResDTO itemTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(itemTypeReqDTO);

			PmStatus templateStaus = new PmStatus();
			templateStaus.setStatusId(60550L);

			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			ParameterResDTO localGoodsResDTO = parameterSo
					.retrieveParameterByTypeAndCode(localGoodsReqDTO);

			specMaster.setSpecName(panelItem.getItemName());
			specMaster.setSpecDesc(panelItem.getItemName());

			specMaster.setProcessType(TemplateSpecConst.PROCESS_TYPE_SOURCING);
			specMaster.setOrgProfile(profileRes.getPmOrgProfile());
			// specMaster.setDurationMonth(12);
			specMaster
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			specMaster.setScQtDocs(null);
			specMaster.setScQuoteDocs(null);
			specMaster.setBrandNew(false);
			specMaster.setRecordStatus(true);
			specMaster.setTemplateTypeCode(templateTypeResDTO.getPmParameter());
			specMaster.setSpecTypeCode(techspecTyperesDTO.getPmParameter());
			specMaster.setProcurementCategory(procurementResDTO
					.getPmParameter());
			specMaster.setItemTypeCode(itemTypeResDTO.getPmParameter());
			specMaster.setStatus(templateStaus);
			specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
			setUserDetails(specMaster);
			List<TplSpecQuestion> questionsList = new ArrayList<TplSpecQuestion>();
			new TemplateSpecBackingBean().addQuestion(specMaster,
					questionsList, questionsList.size() - 1,
					TemplateSpecConst.QUESTION_TYPE_ITEM);
			TplSpecQuestion tplSpecQuestion = questionsList.get(0);

			// tplSpecQuestion.setTplSpecMaster(specMaster);
			tplSpecQuestion.setClItem(panelItem.getItemName());
			tplSpecQuestion.setClSpecDetail(panelItem.getSpecification());
			tplSpecQuestion.setClUom(panelItem.getPmUom());
			tplSpecQuestion.setClQuantity(new BigDecimal(panelItem
					.getMinimumOrderQty()));
			tplSpecQuestion.setTplSpecEstimations(null);
			ParameterReqDTO PriceTypereqDTO = new ParameterReqDTO();
			PriceTypereqDTO.setParameterType(ParameterTypeConst.PRICE_TYPE);
			if (specMaster.getTplSpecZone() != null) {
				PriceTypereqDTO
						.setParameterCode(ParameterConst.PRICE_TYPE_ZONAL_STANDARD);
			} else {
				PriceTypereqDTO
						.setParameterCode(ParameterConst.PRICE_TYPE_STANDARD);
			}

			ParameterResDTO PriceTyperesDTO = parameterSo
					.retrieveParameterByTypeAndCode(PriceTypereqDTO);
			tplSpecQuestion
					.setClPriceTypeCode(PriceTyperesDTO.getPmParameter());
			setUserDetails(tplSpecQuestion);
			// questionsList.add(tplSpecQuestion);
			specMaster.setTplSpecQuestions(questionsList);

			this.getManagedBean().getScQt().setZoneLocationLevel(null);
			this.getManagedBean().getScQt().getScQtSupplierCriteria()
					.setLocationLevel(null);
			this.getManagedBean().getScQt().setFulfilmentType(null);

			QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();
			List<ScQtDoc> qtDocs = new ArrayList<ScQtDoc>();
			qtDocs.add(scqtDocDest);
			docReqDTO.setScQtDoc(qtDocs);

			IQTTemplateSO qtTemplateSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTTemplateSO.class);

			qtTemplateSO.saveOrUpdateTemplateDoc(docReqDTO);
		} finally {

		}

	}

	public void retriveTechnicalSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		// this.getManagedBean().setActiveIndex(2);

		try {

			if (QTConstants.ITEM_SPECIFIC_TYPE.equalsIgnoreCase(this
					.getManagedBean().getPanelItemCategory())
					&& this.getManagedBean().isSelectPanelItem()) {
				createCircularItemSpec(this.getManagedBean().getCtPanelItem());
				this.getManagedBean().setSelectPanelItem(false);
			}

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);

			this.getManagedBean().setScQtCheckSt2techProposal(
					resDto.getSpecResultBean().getResultList());
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					resDto.getFinSpecResultBean().getResultList());

		} finally {
			specSO = null;
		}

	}

	public void retriveFinancialSpecification() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		// this.getManagedBean().setActiveIndex(3);

		try {
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());

				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				resDto = specSO.retrieveSpecDetails(reqDTO);

				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDto.getFinSpecResultBean().getResultList());
			}

		} finally {
			specSO = null;
		}

	}

	public void retriveApprovalRemarks() {
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;
		// this.getManagedBean().setActiveIndex(4);

		try {
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setDocType(QTConstants.DOCTYPE_QT);
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
			initializeRemarks(this.getManagedBean().getScQt(), this
					.getManagedBean().getScQtRemarksList(), ScQtRemark.class);

		} finally {
			qtDetailSO = null;
		}
	}

	public void procurementCategoryCode(ScQt scQt) {
		try {
			PmParameter procurementCategory = scQt.getPmProcurementCategory();
			if (null != procurementCategory
					&& null != procurementCategory.getParameterId()) {
				this.getManagedBean().setProcuremntCategory(
						procurementCategory.getParameterCode());

			}

		} finally {

		}
	}

	/**
	 * Will be called to get the both the checklists if technical is empty
	 */
	public void retriveTechnicalCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getScQt());
				resDTO = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getTechnicalCheckList());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());

			}
		} finally {
			specSo = null;
		}
	}

	private boolean isAtleastOneSeriveReq(List<ScQtCheckList> checklist) {
		int i = 0;
		for (ScQtCheckList specChecklist : checklist) {
			if (specChecklist.getScQtDoc().getTplSpecMaster().getItemTypeCode()
					.getParameterCode().equalsIgnoreCase("2")) {
				i++;
				if (i >= 1) {
					return true;
				}
			}
		}

		return false;
	}

	public List<ScQtCheckList> convertCheckListByDocument(
			List<PmDocument> documentList, String docType) {
		List<ScQtCheckList> checkListList = new ArrayList<ScQtCheckList>();
		try {
			for (PmDocument pmDoc : documentList) {
				ScQtCheckList checkList = new ScQtCheckList();
				checkList.setTitle(pmDoc.getDocName());
				checkList.setDefinationType(pmDoc.getDocDefinitionTypeCode());
				checkList.setManditory(true);
				checkList.setScoringReq(true);
				checkList.setScQt(this.getManagedBean().getScQt());
				// checkList.setQtScoreWeightage(new
				// ScQtScoreWeightage());
				checkList.setAction(pmDoc.getChecklistActionCode());
				checkList.setPmDocument(pmDoc);
				checkList.setDocCategory(docType);
				PmStatus status = new PmStatus();
				status.setStatusId(1L);
				checkList.setPmStatus(status);
				checkList.setChecklistSeq(1);
				checkListList.add(checkList);
			}

		} finally {

		}
		return checkListList;

	}

	/**
	 * Will be called to get financial check list if not exist in managed bean
	 */
	public void retriveFinancialCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;
		try {
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getScQt());
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
			}
		} finally {
			specSo = null;
		}
	}

	public ScQtScoreSetting constructScoreSetting(int settingType) {
		ScQtScoreSetting scQtScoreSetting = new ScQtScoreSetting();
		scQtScoreSetting.setPassingScore(new BigDecimal(0));
		scQtScoreSetting.setOverallFin(new BigDecimal(0));
		scQtScoreSetting.setOverallOtherFin(new BigDecimal(0));
		scQtScoreSetting.setOverallTechnical(new BigDecimal(0));
		scQtScoreSetting.setOverallOtherTechnical(new BigDecimal(0));
		scQtScoreSetting.setSettingType(settingType);
		scQtScoreSetting.setScQt(this.getManagedBean().getScQt());
		setUserDetails(scQtScoreSetting);
		return scQtScoreSetting;

	}

	public String retriveTechnicalScoring() {
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getScQt());
		SpecificationResDTO resDTO = null;
		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

				resDTO = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getTechnicalCheckList());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
			}
			// reqDTO.setSpecType("T");
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(1);
			settinglist.add(3);
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				if (null == this.getManagedBean().getTechnicalScoreSetting()) {
					ScQtScoreSetting technicalScoresetting = constructScoreSetting(1);
					this.getManagedBean().setTechnicalScoreSetting(
							technicalScoresetting);

				}

				if (null == this.getManagedBean().getDemoScoreSetting()) {
					ScQtScoreSetting demoScoresetting = constructScoreSetting(3);
					this.getManagedBean().setDemoScoreSetting(demoScoresetting);

				}

			}

			else {

				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == 1) {

						this.getManagedBean().setTechnicalScoreSetting(
								recommnendationSetting);
					}

					if (recommnendationSetting.getSettingType() == 3) {
						this.getManagedBean().setDemoScoreSetting(
								recommnendationSetting);
					}

				}
			}
			this.getManagedBean().setTechnicalScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checkList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != checkList && !checkList.isEmpty()) {
				int score = 0;
				float addScore = 0;
				for (ScQtCheckList list : checkList) {
					if (null == list.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(new BigDecimal(0));
						list.setQtScoreWeightage(scoreWeightage);
						setUserDetails(list.getQtScoreWeightage());
					}

					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();
					score = (int) addScore;
					this.getManagedBean().getTechnicalScoring().add(list);
				}

				this.getManagedBean().setTechPoints(score);
				techWeightageSum();
				techScoreSum();
			}

		} finally {
			specSo = null;
		}
		return "weightageGroup.xhtml";
	}

	public String retriveFinancialScoring() {
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getScQt());
		try {
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				SpecificationResDTO resDTO1 = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO1.getFinancialCheckLists());
			}
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
				ScQtScoreSetting scoreSetting = constructScoreSetting(2);
				this.getManagedBean().setFinancialScoreSetting(scoreSetting);

			} else {
				for (ScQtScoreSetting scoreSttting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					this.getManagedBean()
							.setFinancialScoreSetting(scoreSttting);
				}

			}
			this.getManagedBean().setFinancialScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null != checklist && !checklist.isEmpty()) {

				List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

				List<ScQtCheckList> specChecklist = new ArrayList<ScQtCheckList>();

				float addScore = 0;
				// String localTitle="";
				StringBuffer sb = new StringBuffer();
				for (ScQtCheckList list : checklist) {
					if (null == list.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(new BigDecimal(0));
						setUserDetails(scoreWeightage);
						list.setQtScoreWeightage(scoreWeightage);

					}

					addScore = addScore
							+ list.getQtScoreWeightage().getScore().intValue();

					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
							.equalsIgnoreCase(list.getDefinationType()
									.getParameterCode())) {
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
					ScQtCheckList tmpCheckList = new ScQtCheckList();
					PmParameter definationType = new PmParameter();
					definationType
							.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
					tmpCheckList.setDefinationType(definationType);
					tmpCheckList.setQtScoreWeightage(specChecklist.get(0)
							.getQtScoreWeightage());
					// this.getManagedBean().setScQtChecklist(tmpCheckList);
					if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {
						getManagedBean().setScoreVarience(
								specChecklist.get(0).getQtScoreWeightage()
										.getScore());
					}
					tmpCheckList.setTitle(sb.toString());

					List<ScQtScoreOfferPrice> offerPrice = tmpCheckList
							.getQtScoreWeightage().getScQtScoreOfferPrices();
					if (null == offerPrice || offerPrice.isEmpty()) {
						offerPrice = new ArrayList<ScQtScoreOfferPrice>();
						if (null == this.getManagedBean()
								.getOfferPricePackage()) {
							ScQtScoreOfferPrice pkgOfferPrice = new ScQtScoreOfferPrice();
							pkgOfferPrice.setScQtScoreWeightage(tmpCheckList
									.getQtScoreWeightage());
							pkgOfferPrice
									.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);
							setUserDetails(pkgOfferPrice);
							this.getManagedBean().setOfferPricePackage(
									pkgOfferPrice);
							offerPrice.add(pkgOfferPrice);
						}
						if (null == this.getManagedBean().getOfferPriceItem()) {
							ScQtScoreOfferPrice itemOfferPrice = new ScQtScoreOfferPrice();
							itemOfferPrice.setScQtScoreWeightage(tmpCheckList
									.getQtScoreWeightage());
							itemOfferPrice
									.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_ITEM);
							setUserDetails(itemOfferPrice);
							this.getManagedBean().setOfferPriceItem(
									itemOfferPrice);
							offerPrice.add(itemOfferPrice);

						}
						tmpCheckList.getQtScoreWeightage()
								.setScQtScoreOfferPrices(offerPrice);

					} else {
						for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList
								.getQtScoreWeightage()
								.getScQtScoreOfferPrices()) {

							if (ParameterConst.RECOMMEND_TYPE_PACKAGE
									.equalsIgnoreCase(scoreOfferPrice
											.getOfferPriceType())) {

								this.getManagedBean().setOfferPricePackage(
										scoreOfferPrice);
							} else if (ParameterConst.RECOMMEND_TYPE_ITEM
									.equalsIgnoreCase(scoreOfferPrice
											.getOfferPriceType())) {

								this.getManagedBean().setOfferPriceItem(
										scoreOfferPrice);
							}
						}

					}
					this.getManagedBean().getFinancialScoring()
							.add(0, tmpCheckList);
				}

				// this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);

				this.getManagedBean().getFinancialScoring().addAll(checklist);

				finWeightageSum();
				finScoreSum();

			}
		} finally {
			specSo = null;
		}
		return "financialScoring.xhtml";

	}

	public String retriveRecommendationDetails() {
		IQTSpecSO specSo = null;

		try {
			if (null == this.getManagedBean().getRecommendationPackageScore()
					&& null == this.getManagedBean().getRecommendationScore()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setScQt(this.getManagedBean().getScQt());
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

					if (null == this.getManagedBean()
							.getRecommendationPackageScore()) {
						ScQtScoreSetting pkgScoreSetting = constructScoreSetting(4);

						this.getManagedBean().setRecommendationPackageScore(
								pkgScoreSetting);
					}
					if (null == this.getManagedBean().getRecommendationScore()) {
						ScQtScoreSetting itemScoreSetting = constructScoreSetting(5);
						this.getManagedBean().setRecommendationScore(
								itemScoreSetting);
					}

				} else {

					for (ScQtScoreSetting recommnendationSetting : resDTO
							.getScoreSettingResultBean().getResultList()) {

						if (recommnendationSetting.getSettingType() == 4) {
							this.getManagedBean()
									.setRecommendationPackageScore(
											recommnendationSetting);
							calculateTotalPackage(this.getManagedBean()
									.getRecommendationPackageScore());
						}

						if (recommnendationSetting.getSettingType() == 5) {
							this.getManagedBean().setRecommendationScore(
									recommnendationSetting);
							calculateRecommendtechnicalScore(this
									.getManagedBean().getRecommendationScore());
							calculateRecommendFinancialScore(this
									.getManagedBean().getRecommendationScore());
						}

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

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			List<ScQtProgressivePayment> paymentList = this.getManagedBean()
					.getProgressivePaymentList();

			if (scheduleList == null) {
				scheduleList = new ArrayList<ScQtSchedule>();
				if (null != resDTO.getScheduleResults()
						&& null != resDTO.getScheduleResults().getResultList()
						&& !resDTO.getScheduleResults().getResultList()
								.isEmpty()) {

					this.getManagedBean().setTotalFrequency(
							resDTO.getScheduleResults().getResultList().size());
					String startMonth = "";
					String endMonth = "";
					for (ScQtSchedule scQtSchedule : resDTO
							.getScheduleResults().getResultList()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String fromdate = dateFormat.format(scQtSchedule
								.getFromDate());
						String todate = dateFormat.format(scQtSchedule
								.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
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
			}

			// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());
			if (null == paymentList) {
				paymentList = new ArrayList<ScQtProgressivePayment>();
				if (null != resDTO.getProgressivePaymentResults()
						&& null != resDTO.getProgressivePaymentResults()
								.getResultList()
						&& !resDTO.getProgressivePaymentResults()
								.getResultList().isEmpty()) {

					this.getManagedBean().setTotalProgressivePayment(
							resDTO.getProgressivePaymentResults()
									.getResultList().size());
					String startMonth = "";
					String endMonth = "";
					for (ScQtProgressivePayment payment : resDTO
							.getProgressivePaymentResults().getResultList()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String fromdate = dateFormat.format(payment
								.getFromDate());
						String todate = dateFormat.format(payment.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
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
					this.getManagedBean()
							.setProgressivePaymentList(paymentList);
				}
			}

		} finally {
			specSo = null;
		}

	}

	public void retriveDeliveryDetails() {
		IQTSpecSO specSo = null;

		try {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				this.getManagedBean().setActiveIndex(6);
			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				this.getManagedBean().setActiveIndex(7);
			}
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
									new ArrayList<BriefingZoneBean>());
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
		this.getManagedBean().setDeliveryAddressSelect(
				new ArrayList<PmAddressType>());
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

	public void districtCode(AjaxBehaviorEvent event) {
		try {
			Long districtId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (null != districtId) {
				List<PmDistrict> districtList = this.getManagedBean()
						.getDistrictList();
				for (PmDistrict district : districtList) {
					if (district.getDistrictId().equals(districtId)) {
						this.getManagedBean().setDistrictCode(
								district.getDistrictCode());

					}

				}

			}

		} finally {

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
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				this.getManagedBean().setActiveIndex(8);
			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				this.getManagedBean().setActiveIndex(9);
			}
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType(QTConstants.DOCTYPE_SPEC_COMMITTE);
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);

			this.getManagedBean().setCommitteeActivities(
					resDTO.getCommitteActivities());

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
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;

		try {

			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			List<ScQtLocCriteria> locationDTOs = getLocations(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();

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
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootLocationTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, ScQtLocCriteria.class));
			int i = index;

			this.getManagedBean().setIndex(i);
		} finally {
			parameterSo = null;
		}

		// getManagedBean().setZoneName(zone.getZoneName());

	}

	// Edit General ZoneLocations Method
	public void editBusinessOwnerZoneLocation(TreeNode rootZoneLocation,
			int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();

					break;
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
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, TplSpecZoneLoc.class));
			int i = index;

			this.getManagedBean().setIndex(i);
			getManagedBean().setZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
		} finally {

		}

	}

	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
	}

	public void technicalSpecBoValidate() {
		getManagedBean().setRequired(Boolean.TRUE);
	}

	// Display Location level Dropdown values
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

	// Supplier Locations Pop Up Method
	public void showZoneLocationPopup() {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			Long locationLevel = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootLocationTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											null,
											null,
											null,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											ScQtLocCriteria.class));
				}

			}
		} finally {
			parameterSo = null;
		}
	}

	// Zone Details Pop up method
	public void showGeneralZoneLocationPopup() {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootZoanlTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											null,
											null,
											null,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											TplSpecZoneLoc.class));
				}

			}
		} finally {
			parameterSo = null;
		}

	}

	// Supplier Qualification Criteria Locations Add Method
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
		if (null == getManagedBean().getZoneTrees()) {
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}

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

	// General Zone Locations Add Method
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
		if (null == getManagedBean().getZoneGeneralTrees()) {
			getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		}

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

	/**
	 * Method to construct spec details to save...
	 * 
	 * @return
	 */

	private SpecificationReqDTO prepareSpecReq() {

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();

		ScQt scQt = this.getManagedBean().getScQt();
		scQt.setScQtStatus(null);
		scQt.setMaxProposal(1);
		scQt.setScheduleType("F");
		ScQtSupplierCriteria qtSupplierCriteria = scQt
				.getScQtSupplierCriteria();
		if (!scQt.isPanel() && null != qtSupplierCriteria) {
			if (scQt.isZonal()
					&& scQt.getQtType().equals(QTConstants.OPEN_TYPE)) {

				List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
						this.getManagedBean().getZoneGeneralTrees(), null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					// TODO Code needs to be remove
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
					qtSupplierCriteria.setLocationLevel(scQt
							.getZoneLocationLevel());
				}
				qtSupplierCriteria.setScQt(scQt);
				// TODO Need to remove below code...
				setUserDetails(qtSupplierCriteria);
				scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			}

			if ((!scQt.isZonal()
					&& scQt.getQtType().equals(QTConstants.OPEN_TYPE) && !this
					.getManagedBean().getScQt().getScQtSupplierCriteria()
					.isAllState())
					|| (scQt.isZonal()
							&& scQt.getQtType().equals(QTConstants.OPEN_TYPE)
							&& this.getManagedBean().getScQt()
									.getEstimatedAmount().intValue() > 500000 && !this
							.getManagedBean().getScQt()
							.getScQtSupplierCriteria().isAllState())) {

				List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
						this.getManagedBean().getZoneTrees(),
						qtSupplierCriteria);
				if (locCriteria != null && !locCriteria.isEmpty()) {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(
							locCriteria);

				}

			}
			if (scQt.getQtType().equals(QTConstants.OPEN_TYPE)
					&& this.getManagedBean().getScQt().getEstimatedAmount()
							.intValue() > 500000
					&& this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().isAllState()) {
				if (null != qtSupplierCriteria) {

					qtSupplierCriteria.setLocationLevel(null);
					qtSupplierCriteria.setScQtLocCriteria(null);
				}
			}

			qtSupplierCriteria.setScQt(scQt);
			// TODO Need to remove below code...
			setUserDetails(qtSupplierCriteria);
			scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			if (QTConstants.OPEN_TYPE.equalsIgnoreCase(scQt.getQtType())) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getSuppCriteriaopenList();
				updateCategoryCodesToQt(openList, scQt);
			} else if (QTConstants.RESTRICTED_TYPE.equalsIgnoreCase(scQt
					.getQtType())) {

				scQt.setZoneLocationLevel(null);
				if (null != scQt.getScQtSupplierCriteria()) {
					scQt.getScQtSupplierCriteria().setLocationLevel(null);

				}
				List<ScQtCategory> openList = this.getManagedBean()
						.getSuppCriteriaopenList();
				updateCategoryCodesToQt(openList, this.getManagedBean()
						.getScQt());

			}
		} else if (scQt.isPanel()) {
			if (null != this.getManagedBean().getCtPanelZones()
					&& !this.getManagedBean().getCtPanelZones().isEmpty()) {
				if (null != scQt.getScQtSupplierCriteria()
						&& null == scQt.getScQtSupplierCriteria()
								.getQtZoneCriterias()) {
					scQt.getScQtSupplierCriteria().setQtZoneCriterias(
							new ScQtZoneCriteria());
				}
				ScQtZoneCriteria zoneCriteria = scQt.getScQtSupplierCriteria()
						.getQtZoneCriterias();
				zoneCriteria.setScQtSupplierCriteria(scQt
						.getScQtSupplierCriteria());
				// TODO: Need to remove
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (CtPanelZone panelzone : this.getManagedBean()
						.getCtPanelZones()) {
					TplSpecZoneDtl zoneDtl = new TplSpecZoneDtl();
					zoneDtl.setZoneName(panelzone.getName());
					setUserDetails(zoneDtl);
					zoneDtl.setTplSpecZone(zoneCriteria);
					zoneDtl.setPanelZone(panelzone);
					zoneLocal.add(zoneDtl);
				}
				zoneCriteria.setTplSpecZoneDtls(zoneLocal);

				scQt.getScQtSupplierCriteria().setQtZoneCriterias(zoneCriteria);

			} else {
				scQt.getScQtSupplierCriteria().setQtZoneCriterias(null);
			}
			scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(QTConstants.OPEN_TYPE);

		}

		List<ScQtCheckList> techSpecList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		List<ScQtCheckList> localSpecList = new ArrayList<ScQtCheckList>();
		if (null != techSpecList && !techSpecList.isEmpty()) {
			localSpecList.addAll(techSpecList);

		}

		List<ScQtCheckList> finSpecList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != finSpecList && !finSpecList.isEmpty()) {
			localSpecList.addAll(finSpecList);
		}

		List<ScQtRemark> remarksList = this.getManagedBean()
				.getScQtRemarksList();

		if (isNotNullOREmpty(remarksList)) {
			scQt.setScQtRemarks(finalizeRemarks(remarksList));

		}
		scQt.setScQtChecklists(localSpecList);
		scQt.setScQtCommittees(null);
		scQt.setScQtBriefings(null);
		reqDTO.setScQt(scQt);

		return reqDTO;

	}

	@PostConstruct
	public void init() {

	}

	/**
	 * Method to retrieve the General info tab details for the BO.
	 * 
	 * @param qtId
	 * @param docType
	 *            (To get the status we need to docType)
	 */
	public void retriveSpecdetails(Long qtId, String docType) {
		IQTDetailSO QtDetailsSO;
		// this.getManagedBean().setActiveIndex(0);

		try {
			if (this.getManagedBean().getTempIndex() == 0) {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setDocType(docType);
				QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
				tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
				ScQt scQt = tenderResDTO.getQtDetail();

				if (null != scQt.getFulfilmentType()
						&& null != scQt.getFulfilmentType().getParameterId()) {
					this.getManagedBean().setFulfilmentType(
							scQt.getFulfilmentType().getParameterCode());
				} else {
					scQt.setFulfilmentType(new PmParameter());
				}
				this.getManagedBean().setPanelChange(scQt.isPanel());
				if (scQt.isZonal()
						&& !scQt.isPanel()
						&& null != this.getManagedBean().getZoneGeneralTrees()
						&& this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
					retriveNonpanelZoneDtls(qtId, docType);

				}
				this.getManagedBean().setScQt(scQt);
				if (null != scQt.getEstimatedAmount()) {
					setProcurementMode(scQt);
				}
			}

		} finally {
			QtDetailsSO = null;
		}

	}

	// Retrive Non Panel Zone Details Method
	public void retriveNonpanelZoneDtls(Long qtId, String docType) {
		IQTDetailSO QtDetailsSO;
		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(qtId);
			tenderReqDTO.setDocType(docType);
			QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			if (isNotNullOREmpty(tenderResDTO.getZoneDetails())) {
				for (TplSpecZoneDtl zone : tenderResDTO.getZoneDetails()) {
					getManagedBean().getZoneGeneralTrees().add(
							LocationSingleton.getInstance().constuctTreeNode(
									zone, zone.getTplSpecZoneLocs()));

				}
			}

		} finally {

		}
	}

	// Evaluation Type set Method
	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	// Quaptaion/Tender Type Set Method

	public void onChangeQtType(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getScQt().setQtType(type);
	}

	// Panel Yes or no Method
	public void panelChangeValue(AjaxBehaviorEvent event) {
		Boolean panel = (Boolean) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQt().setPanel(panel);
	}

	// Technical Checklist Action values Chnanged by DocDefination type method
	public void typeChange(int index, List<ScQtCheckList> checkList) {
		// Long type = (Long) event.getComponent().getAttributes().get("value");
		ScQtCheckList list = checkList.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc
					.getPmParameter()
					.getParameterId()
					.equals(Long.valueOf(list.getDefinationType()
							.getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setDefinationType(paramDesc.getPmParameter());
				break;
			}
		}

		try {

			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(parameterCode)) {

				this.getManagedBean().setTechnicalChecklistAction(null);

				List<String> recommendList = new ArrayList<String>();
				recommendList.add("M");

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
				SelectItem[] uploaditems = getDropdownValuesBySearchBean(
						ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);

				this.getManagedBean().setTechnicalChecklistAction(
						Arrays.asList(uploaditems));

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(parameterCode)) {

				List<String> recommendList = new ArrayList<String>();
				recommendList.add("V");
				recommendList.add("O");

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
				SelectItem[] uploaditems = getDropdownValuesBySearchBean(
						ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);

				this.getManagedBean().setTechnicalChecklistAction(
						Arrays.asList(uploaditems));

			}
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(parameterCode)) {

				List<String> recommendList = new ArrayList<String>();
				recommendList.add("P");

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_IN.value(),
								recommendList));
				SelectItem[] uploaditems = getDropdownValuesBySearchBean(
						ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);

				this.getManagedBean().setTechnicalChecklistAction(
						Arrays.asList(uploaditems));
			}
			this.getManagedBean().getScQtCheckSt2techProposal()
					.set(index, list);
		} finally {

		}

	}

	// Finncial Checklist Action Dropdown Values
	public SelectItem[] getFinChecklIstActionValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add("V");
		recommendList.add("O");

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);
		return items;
	}

	// Technical Checklist Defination Type DropDOwn Values
	public SelectItem[] getTechnicalChecklIstDefValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE);
		recommendList
				.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		return items;
	}

	// Set technical Checklist DropDown Values Set to List
	public void checklistActionChange(int index, List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);

	}

	// Set Financial Checklist DropDown Values Set to List
	public void financialCheckListAction(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}
		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

	}

	// Set Procurement Mode Quotation/Tender Method.
	public void setProcurementMode(ScQt scQt) {
		IParameterSO parameterSo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			if (scQt.getEstimatedAmount().intValue() <= 50000) {

				ParameterReqDTO quotationReqDTO = new ParameterReqDTO();
				quotationReqDTO
						.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				quotationReqDTO
						.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				ParameterResDTO quotationResDTO = parameterSo
						.retrieveParameterByTypeAndCode(quotationReqDTO);
				this.getManagedBean().getScQt()
						.setProcurementMode(quotationResDTO.getPmParameter());

				List<String> types = new ArrayList<String>();
				types.add("1");
				List<SelectItem> itemList = new ArrayList<SelectItem>();
				for (String location : types) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

					for (PmParameterDesc paramDesc : localParamDesc) {
						if (paramDesc.getPmParameter().getParameterCode()
								.equals(location)) {

							itemList.add(new SelectItem(paramDesc
									.getPmParameter().getParameterCode(),
									paramDesc.getCodeName()));
							this.getManagedBean().setEvaluationTypeItem(
									itemList);
							this.getManagedBean()
									.getScQt()
									.setEvaluationType(
											paramDesc.getPmParameter()
													.getParameterCode());
							scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
							scQt.setOpenFor(SourcingConstants.OPERN_FOR_BUMI);
							this.getManagedBean().setScQt(scQt);
						}
					}
				}
			} else if (scQt.getEstimatedAmount().intValue() > 50000
					&& scQt.getEstimatedAmount().intValue() <= 100000) {

				ParameterReqDTO quotationReqDTO = new ParameterReqDTO();
				quotationReqDTO
						.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				quotationReqDTO
						.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				ParameterResDTO quotationResDTO = parameterSo
						.retrieveParameterByTypeAndCode(quotationReqDTO);
				this.getManagedBean().getScQt()
						.setProcurementMode(quotationResDTO.getPmParameter());
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
				scQt.setOpenFor(SourcingConstants.OPERN_FOR_BUMI);
				this.getManagedBean().setScQt(scQt);
			}

			else if (scQt.getEstimatedAmount().intValue() > 100000
					&& scQt.getEstimatedAmount().intValue() <= 500000) {

				ParameterReqDTO quotationReqDTO = new ParameterReqDTO();
				quotationReqDTO
						.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				quotationReqDTO
						.setParameterCode(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				ParameterResDTO quotationResDTO = parameterSo
						.retrieveParameterByTypeAndCode(quotationReqDTO);
				this.getManagedBean().getScQt()
						.setProcurementMode(quotationResDTO.getPmParameter());
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
				scQt.setOpenFor(SourcingConstants.OPERN_FOR_ALL);
				this.getManagedBean().setScQt(scQt);
			}

			else if (scQt.getEstimatedAmount().intValue() > 500000) {

				ParameterReqDTO tenderReqDTO = new ParameterReqDTO();
				tenderReqDTO
						.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
				tenderReqDTO
						.setParameterCode(ParameterConst.PROCUREMENT_MODE_TENDER);
				ParameterResDTO tenderResDTO = parameterSo
						.retrieveParameterByTypeAndCode(tenderReqDTO);
				this.getManagedBean().getScQt()
						.setProcurementMode(tenderResDTO.getPmParameter());
				scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
				scQt.setOpenFor(SourcingConstants.OPERN_FOR_ALL);
				this.getManagedBean().setScQt(scQt);
			}
		} finally {
			parameterSo = null;
		}

	}

	// set FulFilMent Dropdown Values Method
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		IQTSpecSO specso = getSpecSO();
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setFulfilmentType(
							paramDesc.getPmParameter().getParameterCode());
				}
			}

			if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getScQt()
						.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
				specReqDTO.setScQt(this.getManagedBean().getScQt());
				specReqDTO.setFullfilementType(this.getManagedBean()
						.getFulfilmentType());
				specso.removefulfillmentDtls(specReqDTO);
			} else if (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getScQt()
						.setScheduleType(SourcingConstants.PROGRESSIVE_PAYMENT);

			}

		} finally {

		}

	}

	// Set FulFilmentType Parameter Code Method
	public void getfulfilmentDtls(ScQt scQt) {
		this.getManagedBean().setFulfilmentType(
				scQt.getFulfilmentType().getParameterCode());

	}

	// Set Procurement Category Parameter Code method
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

	// Supplier Qualification Criteria All Stats Select Or DeSelect Methos
	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getScQt().getScQtSupplierCriteria()
				.setAllState(state);

		if (!state) {
			if (null != this.getManagedBean().getZoneTrees()
					&& this.getManagedBean().getZoneTrees().isEmpty()) {
				this.getManagedBean().setZoneTrees(null);
			}
			if (null == this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getLocationLevel()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		} else {
			this.getManagedBean().setZoneTrees(null);
		}
	}

	// Frequency Schedule Type change Method
	public void scheduleTypeChange(AjaxBehaviorEvent event) {
		IQTSpecSO specso = getSpecSO();
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		String scheduletype = (String) event.getComponent().getAttributes()
				.get("value");
		specReqDTO.setScQt(this.getManagedBean().getScQt());
		specReqDTO.setFullfilementType(this.getManagedBean()
				.getFulfilmentType());
		specso.removefulfillmentDtls(specReqDTO);

	}

	// Standard or Non standard Spec Selection Method
	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		if (template.equalsIgnoreCase(TemplateSpecConst.PROCESS_TYPE_SOURCING)) {
			getManagedBean().setSpecName(null);
			// getManagedBean().setStandardTemplate(null);
			getManagedBean().setSearchProcurementId(null);
			getManagedBean().setQtNum(null);
			getManagedBean().setSearchItemTypeId(null);

		} else {
			getManagedBean().setSpecName(null);
			// getManagedBean().setStandardTemplate(null);
			getManagedBean().setSearchProcurementId(
					getManagedBean().getScQt().getPmProcurementCategory()
							.getParameterId());
			getManagedBean().setQtNum(null);
			getManagedBean().setSearchItemTypeId(null);
		}
		this.getManagedBean().setStandardTemplate(template);
	}

	// Method to set the location level drop down values
	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (this.getManagedBean().getScQt().isZonal()) {
			this.getManagedBean().getScQt().getZoneLocationLevel()
					.setParameterId(location);
		} else {
			this.getManagedBean().getScQt().getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(location);
		}
	}

	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getScQt().getScQtSupplierCriteria()
				.getLocationLevel().setParameterId(location);
	}

	// User And Auto Score Select Methods in spec committee
	public void scoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}
		if (scoreType.equalsIgnoreCase("2")) {

			this.getManagedBean().setScQtCheckListScore(
					new ScQtCheckListScore());
			toBeRemoveList.addAll(this.getManagedBean().getCheckListScore());
		} else {
			toBeRemoveList.add(this.getManagedBean().getScQtCheckListScore());
		}
		this.getManagedBean().setChecklistScoreType(scoreType);
	}

	// User And Auto Score Select Methods in spec committee
	public void companyscoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}

		if (scoreType.equalsIgnoreCase("2")) {

			this.getManagedBean()
					.setScQtContractScore(new ScQtCheckListScore());

		} else {
			toBeRemoveList.add(this.getManagedBean().getScQtCheckListScore());
		}
		this.getManagedBean().setCompanyScoreType(scoreType);
	}

	public void fromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setFromMonth(formvalue);
		int n = Integer.parseInt(this.getManagedBean().getFromMonth());
		int j = n + 2;
		String Startmonth = "";
		if (j == 0) {
			Startmonth = "January";
		} else if (j == 1) {
			Startmonth = "February";
		} else if (j == 2) {
			Startmonth = "March";
		} else if (j == 3) {
			Startmonth = "April";
		} else if (j == 4) {
			Startmonth = "May";
		} else if (j == 5) {
			Startmonth = "June";
		}

		else if (j == 6) {
			Startmonth = "July";
		} else if (j == 7) {
			Startmonth = "August";
		} else if (j == 8) {
			Startmonth = "September";
		} else if (j == 9) {
			Startmonth = "October";
		} else if (j == 10) {
			Startmonth = "November";
		} else if (j == 11) {
			Startmonth = "December";
		}
		this.getManagedBean().setBankstmntTomonth(Startmonth);
		this.getManagedBean().setToMonth(String.valueOf(j));
	}

	// While Technical Checklist click on edit will call this..
	public String technicalChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(
						checkList.getPmStatus().getStatusId());
				this.getManagedBean().setIndex(index);
				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				specReqDTO
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = templateSpecSO
						.getTemplateMaster(specReqDTO);
				checkList.getScQtDoc().setTplSpecMaster(
						templateresDTO.getSpecMaster());
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				return "/xhtml/sc/main/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);

				if (QTConstants.TECHNICAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						this.getManagedBean().setCompanyScoreType(
								QTConstants.AUTO_SCORE);

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
								.getCheckListScores()) {
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "companyExperience.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						this.getManagedBean().setCompanyScoreType(
								QTConstants.AUTO_SCORE);

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
								.getCheckListScores()) {

							if (checklistScore.getCriteriaType() == 15) {
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

							} else if (checklistScore.getCriteriaType() == 16) {

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
					this.getManagedBean().setScQtChecklist(checkList);
					return "companyExperiencePrivate.xhtmlfaces-redirect=true";
				}

				else if ("WR".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					// this.getManagedBean().setWarantyScore(null);

					/*
					 * reqDTO.setCheckListId(checkList.getQtChecklistId());
					 * resDTO = specSo.retrieveCheckListScoreNValue(reqDTO); if
					 * (resDTO.getCheckListScoreResultBean() == null ||
					 * resDTO.getScQtCheckListValue() == null) {
					 * 
					 * ScQtCheckListValue checkListValue = new
					 * ScQtCheckListValue(); setUserDetails(checkListValue);
					 * 
					 * checkListValue.setScQtCheckList(checkList);
					 * checkList.setScQtCheckListValue(checkListValue);
					 * this.getManagedBean().setWarantyScore( new
					 * ArrayList<ScQtCheckListScore>());
					 * 
					 * } else {
					 * 
					 * List<ScQtCheckListScore> noOfWarranty = new
					 * ArrayList<ScQtCheckListScore>();
					 * 
					 * for (ScQtCheckListScore checklistScore : resDTO
					 * .getCheckListScoreResultBean().getResultList()) { if
					 * (checklistScore.getCriteriaType() == 30) { if
					 * (checklistScore.getScoreType().intValue() == 1) {
					 * this.getManagedBean() .setChecklistScoreType(
					 * String.valueOf(checklistScore .getScoreType()
					 * .intValue())); noOfWarranty.add(checklistScore);
					 * this.getManagedBean().setWarantyScore( noOfWarranty); }
					 * else if (checklistScore.getScoreType() .intValue() == 2)
					 * { this.getManagedBean() .setChecklistScoreType(
					 * String.valueOf(checklistScore .getScoreType()
					 * .intValue())); this.getManagedBean()
					 * .setScQtCheckListScore( checklistScore); }
					 * 
					 * }
					 * 
					 * } checkList.setScQtCheckListValue(resDTO
					 * .getScQtCheckListValue()); }
					 * this.getManagedBean().setScQtChecklist(checkList);
					 */
					return "waranty.xhtml?faces-redirect=true";
				}

				else if ("TR".equalsIgnoreCase(checkList.getPmDocument()
						.getDocDefinitionKey())) {
					// this.getManagedBean().setTrainingScore(null);
					return "training.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScores() == null
							|| resDTO.getScQtCheckListValue() == null) {
						if (QTConstants.USER_SCORE.equalsIgnoreCase(this
								.getManagedBean().getChecklistScoreType())) {
							this.getManagedBean().setChecklistScoreType(
									QTConstants.AUTO_SCORE);

						}

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);

						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> technicalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 20) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									technicalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "technicalEmployeeList.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_NUMBER_EMPLOYEES
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScores() == null
							|| resDTO.getScQtCheckListValue() == null) {
						if (QTConstants.USER_SCORE.equalsIgnoreCase(this
								.getManagedBean().getChecklistScoreType())) {
							this.getManagedBean().setChecklistScoreType(
									QTConstants.AUTO_SCORE);

						}

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> totalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 21) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									totalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "totalEmployees.xhtml?faces-redirect=true";
				}

			}
		} finally {
			specSo = null;
		}

		return null;

	}

	// All Financial Checklists Edit Actions
	public String financialChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		try {
			this.getManagedBean().setIndex(index);
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(
						checkList.getPmStatus().getStatusId());

				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				specReqDTO
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = templateSpecSO
						.getTemplateMaster(specReqDTO);
				checkList.getScQtDoc().setTplSpecMaster(
						templateresDTO.getSpecMaster());
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				return "/xhtml/sc/main/financialCommitteeTemplate.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);

				if (QTConstants.FINANCIAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 12) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "financialGovernementCompany.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 17) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "financialPrivateCompany.xhtml?faces-redirect=true";

				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 51) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									creditFacility.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "creditFacility.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_CASH_ON_HAND_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> cashOnHand = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 52) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									cashOnHand.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);

					return "cashOnhand.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					int year = Calendar.getInstance().get(Calendar.YEAR);
					this.getManagedBean().setStartYear(String.valueOf(year));
					this.getManagedBean().setEndYear(String.valueOf(year));
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 50) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									bankStatement.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "bankStatement.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);

						checkList.setScQtCheckListValue(checkListValue);
						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 60) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									assetLiability.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "assetsLiabilities.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> suppliersScore = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 30) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									suppliersScore.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											suppliersScore);
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "supplierinfo.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.AUTO_SCORE);

						this.getManagedBean().setCheckListScore(
								new ArrayList<ScQtCheckListScore>());

					} else {

						List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == 70) {
								if (checklistScore.getScoreType().intValue() == 1) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									paidUpCapital.add(checklistScore);
									this.getManagedBean().setCheckListScore(
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
					this.getManagedBean().setScQtChecklist(checkList);
					return "paidUpCapital.xhtml?faces-redirect=true";
				}
			}
		} finally {
			specSo = null;
		}

		return null;

	}

	public void compareChecklistScore(int index,
			List<ScQtCheckListScore> checklistScore) {
		try {

			if (checklistScore.size() > 1) {
				ScQtCheckListScore current = checklistScore.get(index);

				ScQtCheckListScore previous = checklistScore.get(index - 1);

				if (current.getRangeFrom().intValue() > previous.getRangeTo()
						.intValue()
						&& current.getRangeTo().intValue() > current
								.getRangeFrom().intValue()) {

				} else {
					/*
					 * throw new EpException( MessageConst.INFO_100022, new
					 * String[] { "You Can't Provide Numbers In between" +
					 * previous.getRangeFrom() + " and " + previous.getRangeTo()
					 * });
					 */
				}

			}
		} finally {

		}
	}

	public String createCommitteeSpecification() {
		this.getManagedBean().setScqtDoc(new ScQtDoc());
		return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";
	}

	// Spec Committee One tier Edit Action
	public String committeeSpecEditAction(int index, ScQtCheckList checklist,
			String specType) {

		IQTTemplateSO qtTemplateSO;
		try {

			this.getManagedBean().setSpecStatusId(
					checklist.getPmStatus().getStatusId());
			this.getManagedBean().setIndex(index);
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setCheckListTitle(checklist.getTitle());

			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(specReqDTO);
			checklist.getScQtDoc().setTplSpecMaster(resDTO.getSpecMaster());
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			templateSpecBean.setSpecMaster(checklist.getScQtDoc()
					.getTplSpecMaster());

		} finally {
			qtTemplateSO = null;
		}
		return "/xhtml/sc/main/committeeTechnicalEditAction.xhtml?faces-redirect=true";
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
		// ScQtScoreWeightage weightage = new ScQtScoreWeightage();
		// setUserDetails(weightage);

		ScQtCheckList additionProposalst1 = new ScQtCheckList();
		// additionProposalst1.setQtScoreWeightage(weightage);
		// additionProposalst1.sets

		// additionProposalst1.setScQtSpec(scQtSpec);
		return additionProposalst1;
	}

	// financial checklist Add Action
	public void stageTwoFiProposalAdd() {
		IStatusSO statussO;
		IParameterSO parameterSo = null;
		try {
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			List<ScQtCheckList> st2financeproposalList = getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null == st2financeproposalList) {
				st2financeproposalList = new ArrayList<ScQtCheckList>();

			}

			ScQtCheckList st2financeproposalList2 = createSpecList("2");

			// st2financeproposalList2.setDefinationType(1);
			st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2
					.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
			st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2.setChecklistSeq(1);

			st2financeproposalList2.setScQt(this.getManagedBean().getScQt());

			ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			docDefTypeReqDTO
					.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			docDefTypeReqDTO
					.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO docDefTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
			st2financeproposalList2.setDefinationType(docDefTypeResDTO
					.getPmParameter());
			st2financeproposalList2.setAction(new PmParameter());

			setUserDetails(st2financeproposalList2);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(1L);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			st2financeproposalList2.setPmStatus(statusres.getPmStatus());
			// st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList.add(st2financeproposalList2);

			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
		} finally {
			statussO = null;
		}
	}

	// Standard Technical checklist Popup methos
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
				scqtlist.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
				scqtlist.setChecklistSeq(1);
				scqtlist.setPmDocument(document);
				setUserDetails(scqtlist);

				scqtlist.setScQt(this.getManagedBean().getScQt());

				// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
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

			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} finally {

		}
	}

	// Technical Checklist Add method in Spec committee
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
			st2technicalproposalList1
					.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
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

			st2technicalproposalList.add(st2technicalproposalList1);

			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
		} finally {
			statussO = null;
		}
	}

	// Financial Standard checklist slect
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
				scqtlist.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
				scqtlist.setScoringReq(true);
				// scqtlist.setEnable(true);
				scqtlist.setDefinationType(document.getDocDefinitionTypeCode());
				scqtlist.setAction(document.getChecklistActionCode());
				// scqtlist.setEvaluationStage("2");
				scqtlist.setManditory(false);
				scqtlist.setChecklistSeq(1);
				scqtlist.setPmDocument(document);

				scqtlist.setScQt(this.getManagedBean().getScQt());

				setUserDetails(scqtlist);
				// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
				statussO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusreq = new StatusReqDTO();
				statusreq.setStatusId(1L);
				StatusResDTO statusres = statussO
						.retrieveStatusByStatusId(statusreq);

				scqtlist.setPmStatus(statusres.getPmStatus());
				compilance.add(scqtlist);
			}

			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} finally {
			statussO = null;
		}
	}

	// Technical Checklist Pm Documnet method
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
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST);

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

	}

	public void deliveryAddressReset() {
		try {
			getManagedBean().setStateId(null);
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
			if (this.getManagedBean().getScQt().isZonal() == true
					&& this.getManagedBean().getScQt().isPanel() == false) {
				for (BriefingZoneBean deliverybean : zoneBean) {

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
							// loc.setPmAddress(addressType.getPmAddress());
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
							// loc.setPmOrgProfile(addressType.getPmOrgProfile());
							loc.setPmAddressType(addressType);
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
			}

			else if (this.getManagedBean().getScQt().isZonal() == true
					&& this.getManagedBean().getScQt().isPanel() == true) {
				for (BriefingZoneBean deliverybean : zoneBean) {

					if (deliverybean
							.getTplSpecZoneDtl()
							.getPanelZone()
							.getName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getPanelZone().getName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						}
						for (PmAddressType addressType : deliveryList) {
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							// loc.setPmAddress(addressType.getPmAddress());
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
							// loc.setPmOrgProfile(addressType.getPmOrgProfile());
							loc.setPmAddressType(addressType);
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
			} else if ((this.getManagedBean().getScQt().isZonal() == false && this
					.getManagedBean().getScQt().isPanel() == true)
					|| (this.getManagedBean().getScQt().isZonal() == false && this
							.getManagedBean().getScQt().isPanel() == false)) {
				List<ScQtDeliveryLoc> nonZone = this.getManagedBean()
						.getDeliveryNonZoneList();
				if (nonZone == null) {
					nonZone = new ArrayList<ScQtDeliveryLoc>();
				}
				for (PmAddressType addressType : deliveryList) {
					ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
					loc.setPmAddressType(addressType);
					String address1 = addressType.getPmAddress().getAddress1();
					String address2 = addressType.getPmAddress().getAddress2();
					String address3 = addressType.getPmAddress().getAddress3();
					String address4 = addressType.getPmAddress().getAddress4();
					String address = address1 + "," + address2 + "," + address3
							+ "," + address4;
					loc.setDeliveryAddress(address);
					loc.setPmAddressType(addressType);
					// loc.setPmOrgProfile(addressType.getPmOrgProfile());
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

		if (!this.getManagedBean().getScQt().isPanel()) {
			for (BriefingZoneBean zonebean : deliveryBean) {
				if (zonebean.getTplSpecZoneDtl().getZoneName()
						.equalsIgnoreCase(deliveryZoneName)) {

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
		} else {
			for (BriefingZoneBean zonebean : deliveryBean) {
				if (zonebean.getTplSpecZoneDtl().getPanelZone().getName()
						.equalsIgnoreCase(deliveryZoneName)) {
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

	}

	public void deleteDeliveryDtls() {
		try {
			List<ScQtDeliveryLoc> nonzone = this.getManagedBean()
					.getDeliveryNonZoneList();
			if (null == nonzone) {
				nonzone = new ArrayList<ScQtDeliveryLoc>();
			}
			List<ScQtDeliveryLoc> selectdeliveryList = this.getManagedBean()
					.getSelectDeliveryList();

			if (nonzone.size() == selectdeliveryList.size()) {

				nonzone.clear();

			} else {

				for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
					nonzone.remove(delLoc);
				}
			}
		} finally {

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
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST);

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
		setUserDetails(score);
		return score;
	}

	public void addScoring(String criteriaType) {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCheckListScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(criteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			checkListScore.add(score);
			this.getManagedBean().setCheckListScore(checkListScore);

		} finally {

		}
	}

	public void addScoringyears(String CriteriaType) {

		try {

			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringForYears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(CriteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getCompanyScoreType()));
			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} finally {

		}
	}

	public void addScheduleDetails() {

		try {

			int year = Calendar.getInstance().get(Calendar.YEAR);
			List<ScQtSchedule> freqSchedule = this.getManagedBean()
					.getScheduleList();
			if (null == freqSchedule) {
				freqSchedule = new ArrayList<ScQtSchedule>();

			}

			if (freqSchedule.size() > this.getManagedBean().getTotalFrequency()) {

				List<ScQtSchedule> localSchedule = new ArrayList<ScQtSchedule>();
				for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {
					localSchedule.add(freqSchedule.get(j));

				}
				this.getManagedBean().setScheduleList(localSchedule);
			} else {
				for (int i = freqSchedule.size(); i < this.getManagedBean()
						.getTotalFrequency(); i++) {

					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setStartYear(String.valueOf(year));
					schedule.setEndYear(String.valueOf(year));
					PmParameter parameter = new PmParameter();
					schedule.setScheduleType(parameter);
					setUserDetails(schedule);
					freqSchedule.add(schedule);
				}
				this.getManagedBean().setScheduleList(freqSchedule);
			}

		} finally {

		}
	}

	public void calculateStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	public void calculateProgressiveStartMonth(AjaxBehaviorEvent event) {
		event.getComponent().getAttributes().get("value");

	}

	public void calculateProgressivepayment(AjaxBehaviorEvent event) {

		event.getComponent().getAttributes().get("value");

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

			if (progressive.size() > this.getManagedBean()
					.getTotalProgressivePayment()) {

				List<ScQtProgressivePayment> progressiveList = new ArrayList<ScQtProgressivePayment>();
				for (int j = 0; j < this.getManagedBean()
						.getTotalProgressivePayment(); j++) {
					progressiveList.add(progressive.get(j));

				}
				this.getManagedBean()
						.setProgressivePaymentList(progressiveList);
			} else {
				for (int i = progressive.size(); i < this.getManagedBean()
						.getTotalProgressivePayment(); i++) {

					ScQtProgressivePayment payment = new ScQtProgressivePayment();
					payment.setStartYear(String.valueOf(year));
					payment.setEndYear(String.valueOf(year));
					// payment.setScheduleType(new BigDecimal(1));

					setUserDetails(payment);
					progressive.add(payment);
				}
				this.getManagedBean().setProgressivePaymentList(progressive);
			}

		} finally {

		}
	}

	public void calculateTotalPackage(ScQtScoreSetting technical) {
		try {
			if (null == technical.getOverallTechnical()) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (null == technical.getOverallFin()) {
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
			if (null == technical.getOverallTechnical()) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (null == technical.getOverallOtherTechnical()) {
				technical.setOverallOtherTechnical(new BigDecimal(0));
			}
			int local = technical.getOverallTechnical().intValue()
					+ technical.getOverallOtherTechnical().intValue();
			this.getManagedBean().setRecommendTechnicalScore(
					new BigDecimal(local));
			/*
			 * if (local > this.getManagedBean().getRecommendationPackageTotal()
			 * .intValue()) { throw new
			 * EpException("You can't Enter More Than Total Score"); } else {
			 * 
			 * this.getManagedBean().setRecommendTechnicalScore( new
			 * BigDecimal(local)); }
			 */
		} finally {

		}
	}

	public void calculateRecommendFinancialScore(ScQtScoreSetting financial) {
		try {
			if (null == financial.getOverallFin()) {
				financial.setOverallFin(new BigDecimal(0));
			}
			if (null == financial.getOverallOtherFin()) {
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

			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();
			String totalNoOfYearsType = this.getManagedBean()
					.getCompanyScoreType();
			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
				ScQtCheckListScore noContract = this.getManagedBean()
						.getScQtCheckListScore();
				noContract.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				noContract.setCriteriaType(new Integer(this.getManagedBean()
						.getCriteriaType()));
				noContract.setCheckList(checkList);
				setUserDetails(noContract);
				noOfContractScore.add(noContract);
			} else {
				List<ScQtCheckListScore> checkListScore = this.getManagedBean()
						.getCheckListScore();
				noOfContractScore.addAll(checkListScore);
			}
			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			if ((null != checkList && null != checkList.getPmDocument())
					&& (QTConstants.TECHNICAL_GOV_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey()) || QTConstants.TECHNICAL_PRIVATE_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey()))) {

				if (QTConstants.USER_SCORE.equalsIgnoreCase(totalNoOfYearsType)) {
					ScQtCheckListScore scoreOfYears = this.getManagedBean()
							.getScQtContractScore();
					scoreOfYears.setScoreType(new BigDecimal(this
							.getManagedBean().getCompanyScoreType()));
					scoreOfYears.setCriteriaType(new Integer(this
							.getManagedBean().getOtherCriteriaType()));
					scoreOfYears.setCheckList(checkList);
					setUserDetails(scoreOfYears);
					noOfYearsScore.add(scoreOfYears);
				} else {
					List<ScQtCheckListScore> scoreYears = this.getManagedBean()
							.getScoringForYears();
					if (null != scoreYears) {
						noOfYearsScore.addAll(scoreYears);
					}
				}
			}
			reqDTO.setCheckListScores(noOfContractScore);
			List<ScQtCheckListScore> removedList = this.getManagedBean()
					.getToBeRemoveList();
			if (null != removedList) {
				for (int i = 0; i < removedList.size(); i++) {
					if (null == removedList.get(i).getScQtCheckListScoreId()) {
						removedList.remove(i);
					}
				}
			}
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			// resDTO.getScQtCheckList();
			ScQtCheckList updatedCheckList = resDTO.getScQtCheckList();
			if (SourcingConstants.TECHINICAL_STAGE.equalsIgnoreCase(this
					.getManagedBean().getSpecType())) {
				this.getManagedBean().getScQtCheckSt2techProposal()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean()
						.getScQtCheckSt2techProposal()
						.add(this.getManagedBean().getIndex(), updatedCheckList);
				technicalChecklistEditAction(this.getManagedBean().getIndex(),
						updatedCheckList, QTConstants.TECHNICAL_SPEC_TYPE);
			} else {
				this.getManagedBean().getScQtCheckSt2financeProposalList()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean()
						.getScQtCheckSt2financeProposalList()
						.add(this.getManagedBean().getIndex(), updatedCheckList);
				financialChecklistEditAction(this.getManagedBean().getIndex(),
						updatedCheckList, QTConstants.FINANCIAL_SPEC_TYPE);
			}
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Scores Successfully Saved. ",
							"Scores Successfully Saved."));

		} finally {

		}

	}

	public String acceptBankScore() {
		try {
			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();

			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
				ScQtCheckListScore noContract = this.getManagedBean()
						.getScQtCheckListScore();
				noContract.setScoreType(new BigDecimal(this.getManagedBean()
						.getChecklistScoreType()));
				noContract.setCriteriaType(new Integer(this.getManagedBean()
						.getCriteriaType()));
				noContract.setCheckList(checkList);
				setUserDetails(noContract);
				noOfContractScore.add(noContract);
			} else {
				List<ScQtCheckListScore> checkListScore = this.getManagedBean()
						.getCheckListScore();
				noOfContractScore.addAll(checkListScore);
			}
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
				checkList.getScQtCheckListValue().setFromDate(
						df.parse(fromdate));
				checkList.setScQt(this.getManagedBean().getScQt());
				checkList.getScQtCheckListValue().setToDate(df.parse(toDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// checkList.setScQtCheckListScores(noOfContractScore);
			reqDTO.setCheckListScores(noOfContractScore);

			List<ScQtCheckListScore> removedList = this.getManagedBean()
					.getToBeRemoveList();
			if (null != removedList) {
				for (int i = 0; i < removedList.size(); i++) {
					if (null == removedList.get(i).getScQtCheckListScoreId()) {
						removedList.remove(i);
					}
				}
			}
			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			ScQtCheckList updatedCheckList = resDTO.getScQtCheckList();
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), updatedCheckList);
			financialChecklistEditAction(this.getManagedBean().getIndex(),
					updatedCheckList, QTConstants.FINANCIAL_SPEC_TYPE);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Scores Successfully Saved. ",
							"Scores Successfully Saved."));
		} finally {

		}
		return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";
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

	public List<SelectItem> getYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			selectItems.add(new SelectItem(null, "--Select One--"));
			selectItems.add(new SelectItem(year, String.valueOf(year)));
			for (int i = 0; i < 20; i++) {
				year = year + 1;
				selectItems.add(new SelectItem(year, String.valueOf(year)));
			}

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {

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
		attachment.setDocNo(scQtChecklist.getScQt().getQtNo());

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
		SpecificationReqDTO spReqDTO = null;
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)
				|| BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
			spReqDTO = prepareSpecChirPersonRequest();
		} else {
			spReqDTO = prepareSpecReq();
		}
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
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										getMessgeConstant(action),
										new String[] {
												"Specification Creation For ",
												qt.getQtNo() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			specSO = null;
		}
		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}

	public void paneltabChange(TabChangeEvent event) {
		if ("Supplier List".equalsIgnoreCase(event.getTab().getTitle())) {
			// retriveZonesByCircularNo();
		}

	}

	public void retriveZonesByCircularNo() {
		IPanelSupplierSO panelSupplierSO;
		PanelSupplierResDTO resDTO = null;
		try {

			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);

			if (null != this.getManagedBean().getCtPanelItem()
					&& null != this.getManagedBean().getCtPanelItem()
							.getPanelItemId()) {

				reqDTO.setPanelItem(this.getManagedBean().getCtPanelItem());
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
			}
			this.getManagedBean().setCtZoneSupplierList(
					new ArrayList<CtPanelSupplier>());
			getManagedBean().setCtZoneSuppliers(new ArrayList<TreeNode>());
			this.getManagedBean().setCtPanelZones(new ArrayList<CtPanelZone>());

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {

				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {
					this.getManagedBean().setCtzones(true);
					getManagedBean().setCtZoneSuppliers(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getCtZoneSuppliers()
							&& getManagedBean().getCtZoneSuppliers().size() == 0) {

						this.getManagedBean().setCtPanelZones(
								resDTO.getCtPanelZones());
						getManagedBean().getCtZoneSuppliers().add(
								LocationSingleton.getInstance()
										.constuctPanelTreeNodes(
												resDTO.getCtPanelZones()));
					}

				}
			} else {
				if (null != this.getManagedBean().getCtPanelItem()
						&& null != this.getManagedBean().getCtPanelItem()
								.getPanelItemId()) {
					this.getManagedBean().setCtzones(false);
					PanelSupplierReqDTO supplierreqDTO = new PanelSupplierReqDTO();
					supplierreqDTO.setPanelItemId(this.getManagedBean()
							.getCtPanelItem().getPanelItemId());
					PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
							.retrievePanelItem(supplierreqDTO);
					this.getManagedBean().setTotalSuppliers(0);
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != panelSupplierResDTO.getCtPanelItem()
							&& null != panelSupplierResDTO.getCtPanelItem()
									.getCtPanelSuppliers()
							&& !panelSupplierResDTO.getCtPanelItem()
									.getCtPanelSuppliers().isEmpty()) {
						this.getManagedBean().setTotalSuppliers(
								panelSupplierResDTO.getCtPanelItem()
										.getCtPanelSuppliers().size());
						this.getManagedBean().setCtZoneSupplierList(
								new ArrayList<CtPanelSupplier>());
						this.getManagedBean().setCtZoneSupplierList(
								panelSupplierResDTO.getCtPanelItem()
										.getCtPanelSuppliers());

					}
				}

			}

		} finally {

		}

	}

	public void updateContractZoneLocation() {

		if (getManagedBean().getCtZoneTreeSelectedNodes() != null) {
			TreeNode treelocal = consturctTreeNode(getManagedBean()
					.getCtZoneTreeSelectedNodes());

			this.getManagedBean().getCtZoneSuppliers().add(treelocal);
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
		this.getManagedBean().setCtZoneSupplierList(
				new ArrayList<CtPanelSupplier>());
		this.getManagedBean().setTotalSuppliers(0);

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

		if (null != panelSupplier.getSupplier()
				&& null != panelSupplier.getSupplier().getSmAppl()
				&& null != panelSupplier.getSupplier().getSmAppl()
						.getSmPersonnels()) {
			List<SmPersonnel> list = panelSupplier.getSupplier().getSmAppl()
					.getSmPersonnels();
			if (null != list) {

				for (SmPersonnel smPersonnel : list) {
					getManagedBean().setPersonnel(smPersonnel);
				}
				SmPersonnel supplierHQPersonnel = this.getManagedBean()
						.getPersonnel();
				// SmAppl appll= supplierHQPersonnel.getSmAppl();

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();

				// System.out.println("INcomeTax No : " +
				// smCompanyBasic.getIncomeTaxNo());
				if (null != smCompanyBasic) {
					if (null != smCompanyBasic.getSmCompanyAddresses()) {
						for (SmCompanyAddress smCompanyAddress : smCompanyBasic
								.getSmCompanyAddresses()) {
							if (null != smCompanyAddress)
								if (AddressType.CONTACT_ADDRESS.value().equals(
										smCompanyAddress.getAddressType())) {

									getManagedBean().setSupplierBranch("HQ");
									getManagedBean().setSupplierAddress(
											smCompanyAddress.getSmAddress());
									getManagedBean().setSupplierContact(
											supplierHQPersonnel.getName());
									getManagedBean().setSupplierEmail(
											supplierHQPersonnel.getEmail());
									break;
								}
						}
					}
				}
			}
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

	public void deleteScoring(String otherType) {
		try {
			List<ScQtCheckListScore> toBeRemoveScores = this.getManagedBean()
					.getToBeRemoveList();
			if (null == toBeRemoveScores) {
				toBeRemoveScores = new ArrayList<ScQtCheckListScore>();
			}

			if (null == otherType || "".equals(otherType)) {
				List<ScQtCheckListScore> mainScoringDtls = getManagedBean()
						.getCheckListScore();
				List<ScQtCheckListScore> selectchecklistScoring = this
						.getManagedBean().getCheckListSelectScore();
				for (ScQtCheckListScore scoring : selectchecklistScoring) {
					mainScoringDtls.remove(scoring);
				}
				toBeRemoveScores.addAll(selectchecklistScoring);
			} else if ("NoOfYears".equalsIgnoreCase(otherType)) {
				List<ScQtCheckListScore> scoringYears = getManagedBean()
						.getScoringForYears();
				List<ScQtCheckListScore> selectOtherScoring = this
						.getManagedBean().getOtherScoreSelected();
				for (ScQtCheckListScore scoring : selectOtherScoring) {
					scoringYears.remove(scoring);
				}
				toBeRemoveScores.addAll(selectOtherScoring);
			}
			this.getManagedBean().setToBeRemoveList(toBeRemoveScores);
		} finally {

		}

	}

	// Circular List Set method
	public void circularListSelect(SelectEvent selectEvent) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			CtCircularVer circular = (CtCircularVer) selectEvent.getObject();
			this.getManagedBean().setCtCircularVer(circular);

			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICircularDetailSO.class);

			reqDTO.setCircularVerId(circular.getCircularVerId());
			reqDTO.setQuotationTender(true);
			CircularDetailResDTO resDTO = circularDetailSO
					.getPanelItemsByCircularVerId(reqDTO);
			/*
			 * for (CtPanelItem panelItem : resDTO.getPanelItemList()) {
			 * System.out.println(panelItem.getItemName());
			 * this.getManagedBean().setCtPanelItem(panelItem); }
			 */
			this.getManagedBean().setCircularExpDate(circular.getEndDate());
			this.getManagedBean().setCirCularNo(circular.getCircularNo());
			this.getManagedBean().setCircularName(circular.getName());
			this.getManagedBean().setCircularType(circular.getDocType());

			// this.getManagedBean().setCircularType(getParameterDesc(circular.getCtPanelItems().get(0).getItemCategoryId()));
			// System.out.println("=======circular type==="+
			// this.getManagedBean().getCircularType());

			this.getManagedBean().setPanelItemList(resDTO.getPanelItemList());

		} finally {

		}

	}

	// Panel Items Set Method

	public void panelListSelect(SelectEvent selectEvent) {
		CtPanelItem ctPanelItem = (CtPanelItem) selectEvent.getObject();
		List<CtPanelItem> panelList = this.getManagedBean()
				.getSelectpanelItem();

		panelList = new ArrayList<CtPanelItem>();
		this.getManagedBean().setCtPanelItem(ctPanelItem);

		this.getManagedBean().setPanelItemCategory(
				getParameterDesc(ctPanelItem.getItemCategoryId()));
		panelList.add(ctPanelItem);
		this.getManagedBean().setSelectpanelItem(panelList);
		this.getManagedBean().setSelectPanelItem(true);

	}

	// Technical weightage Sum
	public void techWeightageSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
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

	// Technical Scoring sum
	public void techScoreSum() {
		try {
			int total = 0;
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
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

	// Financial Scoring Sum
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
						&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
								.equalsIgnoreCase(weight.getDefinationType()
										.getParameterCode())) {

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

	// Finacial Scoring Weightage Sum
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

	// Search circular Details Method.
	public void searchCircularList() {
		searchCircular.reloadData();
		this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		this.getManagedBean().setCtPanelItem(null);
		this.getManagedBean().setCircularExpDate(new Date());
		// this.getManagedBean().setCirCularNo(null);
		// this.getManagedBean().setCircularName(null);
		this.getManagedBean().setCircularType(null);

	}

	// ct Circular Details paging Data Model
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

				if (getManagedBean().getCtSearchBean().getCirCularNo() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCirCularNo())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("circularNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCirCularNo(), true));

				}

				if (getManagedBean().getCtSearchBean().getCircularName() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCircularName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("name",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCircularName(), true));
				}

				if (getManagedBean().getCtSearchBean().getItemName() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getItemName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("ctPanelItems.itemName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getItemName(), true));
				}

				circularReqDTO.setCircularVerSearchBean(searchBean);

				circularSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
								ICircularSO.class);
				resDto = circularSO.searchCircularVer(circularReqDTO);

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

	// Search template Spec
	public void searchScqtDocAction() {
		templateSpecCreate.reloadData();
	}

	// Search Delivery Address
	public void searchDeliveryAddress() {
		searchDeliveryAddress.reloadData();
	}

	// Reset Template Spec
	public void resetTemplate() {
		getManagedBean().setSpecName(null);
		// getManagedBean().setStandardTemplate(null);
		getManagedBean().setSearchProcurementId(null);
		getManagedBean().setQtNum(null);
		getManagedBean().setSearchItemTypeId(null);
	}

	// Tenplate Spec Paging Data Model
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
			IParameterSO parameterSo;

			try {
				TemplateSpecReqDTO qtTemplateReqDTO = new TemplateSpecReqDTO();
				parameterSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterReqDTO techSpecType = new ParameterReqDTO();
				techSpecType
						.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
				techSpecType.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
				ParameterResDTO techspecTyperesDTO = parameterSo
						.retrieveParameterByTypeAndCode(techSpecType);
				String specTypeCode = techspecTyperesDTO.getPmParameter()
						.getParameterCode();
				if (TemplateSpecConst.PROCESS_TYPE_STANDARD
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					getManagedBean().setSearchProcurementId(
							getManagedBean().getScQt()
									.getPmProcurementCategory()
									.getParameterId());

				}
				if (getManagedBean().getSpecName() != null
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSpecName(), true));

				}

				if (getManagedBean().getStandardTemplate() != null
						&& !"".equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("processType",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getStandardTemplate(),
									true));
				}

				if (getManagedBean().getSearchItemTypeId() != null) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					itemSearchBean.setSearchField("itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (getManagedBean().getSearchProcurementId() != null) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					procurementSearchBean
							.setSearchField("procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}
				if (getManagedBean().getQtNum() != null
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtDocs.scQt.qtNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getQtNum(), true));
				}
				if (specTypeCode != null) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					qtnoSearchBean.setSearchField("specTypeCode.parameterCode");
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

	// Delivery Details Paging Data Model
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

			try {
				// SearchBean<PmAddressType> searchBean2=searchBean;
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO
						.setSearchAddressBean(new SearchBean<PmAddressType>());
				if (getManagedBean().getStateCode() != null
						&& !"".equalsIgnoreCase(getManagedBean().getStateCode())) {
					SearchFilterBean stateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					stateSearchBean
							.setSearchField("pmAddress.pmState.stateCode");
					stateSearchBean.setSearchValue(getManagedBean()
							.getStateCode());
					searchBean.getDataFilterBeans().add(stateSearchBean);
				}

				if (getManagedBean().getDistrictCode() != null
						&& !"".equals(getManagedBean().getDistrictCode())) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDistrict.districtCode");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDistrictCode());
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

				if (resDto != null
						&& !resDto.getSearchAddressResultBean().getResultList()
								.isEmpty()) {
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

		renewSesionObject();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		this.getManagedBean().setOutComes(res.getAction());
		SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) obj;
		this.getManagedBean().setSpecificationDataType(quotationTenderDataType);

		FacesContext fc = FacesContext.getCurrentInstance();
		String taskName = res.getTaskName();
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setTaskName(taskName);
		retriveSpecdetails(quotationTenderDataType.getQtId().longValue(),
				getDocTypeByTaskName(taskName));
		if (SCProcessNameConst.PREPARE_SPECIFICATION.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.CONSOLIDATE_SPECIFICATION
						.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.SPECIFICATION_APPROVAL
						.equalsIgnoreCase(taskName)) {

			if (SCProcessNameConst.SPECIFICATION_APPROVAL
					.equalsIgnoreCase(taskName)) {
				loadPrepareSpecDetails(quotationTenderDataType.getQtId()
						.longValue(), getDocTypeByTaskName(taskName));
				this.getManagedBean().setActiveIndex(4);
			}
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

			if (this.getManagedBean().getScQt().isPanel()
					&& null == this.getManagedBean().getScQt()
							.getEstimatedAmount()) {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BUSINESS_OWNER_SPEC_PREPARATION);
			} else if ((this.getManagedBean().getScQt().isPanel() && null != this
					.getManagedBean().getScQt().getEstimatedAmount())
					|| (!this.getManagedBean().getScQt().isPanel() && null != this
							.getManagedBean().getScQt().getEstimatedAmount())) {
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								QTConstants.BO_SPEC_CREATION_PAGE);
			}

		} else {
			if (this.getManagedBean().getScQt().getFulfilmentType() == null) {

				this.getManagedBean().getScQt()
						.setFulfilmentType(new PmParameter());
			} else {
				/*
				 * loadPrepareSpecCommitteeDetails(quotationTenderDataType
				 * .getQtId().longValue(), getDocTypeByTaskName(taskName));
				 */
			}
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.SPECIFICICATON_COMM_PREPARATION);

		}

	}

	// Prepare Qt Checklist Data Construct method
	private SpecificationReqDTO constructSpecCommitteDtls() {
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getScQt();
		if (this.getManagedBean().getFulfilmentType()
				.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
			scQt.setScheduleType(SourcingConstants.PROGRESSIVE_PAYMENT);
		}
		if (this.getManagedBean().getFulfilmentType()
				.equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			scQt.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
		}

		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (techCheckList != null && !techCheckList.isEmpty()) {
			for (ScQtCheckList checkList : techCheckList) {
				if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(checkList
						.getDefinationType().getParameterCode())) {
					scQt.setDemoRequired(true);
				} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					scQt.setSampleReq(true);
					break;
				}

			}
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
			localChecklist.addAll(technicalScoring);
			scQt.setScQtChecklists(localChecklist);
		}
		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();
		if (financialScoring != null && !financialScoring.isEmpty()) {
			ScQtCheckList finalFinSpec = null;
			for (ScQtCheckList checkList : financialScoring) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					finalFinSpec = checkList;
					break;
				}
			}
			List<ScQtCheckList> finSpecs = null;
			if (null != finalFinSpec) {
				finSpecs = constructFinSpecsNSocre(finalFinSpec);
			}
			if (null != finSpecs && finSpecs.size() > 0) {
				financialScoring.remove(0);
				localChecklist.addAll(finSpecs);
			}
			localChecklist.addAll(financialScoring);
			scQt.setScQtChecklists(localChecklist);
		}
		if ("F".equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtSchedule> freqschedule = this.getManagedBean()
					.getScheduleList();
			List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
			if (freqschedule != null && !freqschedule.isEmpty()) {
				String Startmonth = "";
				String endmonth = "";
				for (ScQtSchedule schedule : freqschedule) {
					if (schedule.getStartMonth().equalsIgnoreCase("0")) {
						Startmonth = "01";
					} else if (schedule.getStartMonth().equalsIgnoreCase("1")) {
						Startmonth = "02";
					} else if (schedule.getStartMonth().equalsIgnoreCase("2")) {
						Startmonth = "03";
					} else if (schedule.getStartMonth().equalsIgnoreCase("3")) {
						Startmonth = "04";
					} else if (schedule.getStartMonth().equalsIgnoreCase("4")) {
						Startmonth = "05";
					} else if (schedule.getStartMonth().equalsIgnoreCase("5")) {
						Startmonth = "06";
					}

					else if (schedule.getStartMonth().equalsIgnoreCase("6")) {
						Startmonth = "07";
					} else if (schedule.getStartMonth().equalsIgnoreCase("7")) {
						Startmonth = "08";
					} else if (schedule.getStartMonth().equalsIgnoreCase("8")) {
						Startmonth = "09";
					} else if (schedule.getStartMonth().equalsIgnoreCase("9")) {
						Startmonth = "10";
					} else if (schedule.getStartMonth().equalsIgnoreCase("10")) {
						Startmonth = "11";
					} else if (schedule.getStartMonth().equalsIgnoreCase("11")) {
						Startmonth = "12";
					}
					if (schedule.getEndMonth().equalsIgnoreCase("0")) {
						endmonth = "01";
					} else if (schedule.getEndMonth().equalsIgnoreCase("1")) {
						endmonth = "02";
					} else if (schedule.getEndMonth().equalsIgnoreCase("2")) {
						endmonth = "03";
					}

					else if (schedule.getEndMonth().equalsIgnoreCase("3")) {
						endmonth = "04";
					} else if (schedule.getEndMonth().equalsIgnoreCase("4")) {
						endmonth = "05";
					} else if (schedule.getEndMonth().equalsIgnoreCase("5")) {
						endmonth = "06";
					}

					else if (schedule.getEndMonth().equalsIgnoreCase("6")) {
						endmonth = "07";
					} else if (schedule.getEndMonth().equalsIgnoreCase("7")) {
						endmonth = "08";
					} else if (schedule.getEndMonth().equalsIgnoreCase("8")) {
						endmonth = "09";
					} else if (schedule.getEndMonth().equalsIgnoreCase("9")) {
						endmonth = "10";
					} else if (schedule.getEndMonth().equalsIgnoreCase("10")) {
						endmonth = "11";
					} else if (schedule.getEndMonth().equalsIgnoreCase("11")) {
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

					schedule.setScheduleNo(schedule.getScheduleNo());

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
			} else {
				scQt.setScQtSchedules(null);
			}
		}

		if ("P".equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtProgressivePayment> progressivePayemnt = this
					.getManagedBean().getProgressivePaymentList();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
			if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
				String Startmonth = "";
				String endmonth = "";
				for (ScQtProgressivePayment schedule : progressivePayemnt) {
					if (schedule.getStartMonth().equalsIgnoreCase("0")) {
						Startmonth = "01";
					} else if (schedule.getStartMonth().equalsIgnoreCase("1")) {
						Startmonth = "02";
					} else if (schedule.getStartMonth().equalsIgnoreCase("2")) {
						Startmonth = "03";
					} else if (schedule.getStartMonth().equalsIgnoreCase("3")) {
						Startmonth = "04";
					} else if (schedule.getStartMonth().equalsIgnoreCase("4")) {
						Startmonth = "05";
					} else if (schedule.getStartMonth().equalsIgnoreCase("5")) {
						Startmonth = "06";
					}

					else if (schedule.getStartMonth().equalsIgnoreCase("6")) {
						Startmonth = "07";
					} else if (schedule.getStartMonth().equalsIgnoreCase("7")) {
						Startmonth = "08";
					} else if (schedule.getStartMonth().equalsIgnoreCase("8")) {
						Startmonth = "09";
					} else if (schedule.getStartMonth().equalsIgnoreCase("9")) {
						Startmonth = "10";
					} else if (schedule.getStartMonth().equalsIgnoreCase("10")) {
						Startmonth = "11";
					} else if (schedule.getStartMonth().equalsIgnoreCase("11")) {
						Startmonth = "12";
					}
					if (schedule.getEndMonth().equalsIgnoreCase("0")) {
						endmonth = "01";
					} else if (schedule.getEndMonth().equalsIgnoreCase("1")) {
						endmonth = "02";
					} else if (schedule.getEndMonth().equalsIgnoreCase("2")) {
						endmonth = "03";
					}

					else if (schedule.getEndMonth().equalsIgnoreCase("3")) {
						endmonth = "04";
					} else if (schedule.getEndMonth().equalsIgnoreCase("4")) {
						endmonth = "05";
					} else if (schedule.getEndMonth().equalsIgnoreCase("5")) {
						endmonth = "06";
					}

					else if (schedule.getEndMonth().equalsIgnoreCase("6")) {
						endmonth = "07";
					} else if (schedule.getEndMonth().equalsIgnoreCase("7")) {
						endmonth = "08";
					} else if (schedule.getEndMonth().equalsIgnoreCase("8")) {
						endmonth = "09";
					} else if (schedule.getEndMonth().equalsIgnoreCase("9")) {
						endmonth = "10";
					} else if (schedule.getEndMonth().equalsIgnoreCase("10")) {
						endmonth = "11";
					} else if (schedule.getEndMonth().equalsIgnoreCase("11")) {
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
			} else {
				scQt.setScQtProgressivePayments(null);
			}
		}

		List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
		ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
				.getTechnicalScoreSetting();
		if (null != technicalScoreSetting
				&& technicalScoreSetting.getSettingType() == 1) {
			scoreSetting.add(technicalScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting financialScoreSetting = this.getManagedBean()
				.getFinancialScoreSetting();
		if (null != financialScoreSetting
				&& financialScoreSetting.getSettingType() == 2) {
			scoreSetting.add(financialScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting demoScoreSetting = this.getManagedBean()
				.getDemoScoreSetting();
		if (null != demoScoreSetting && demoScoreSetting.getSettingType() == 3) {
			scoreSetting.add(demoScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting packageScoreSetting = this.getManagedBean()
				.getRecommendationPackageScore();
		if (null != packageScoreSetting
				&& packageScoreSetting.getSettingType() == 4) {
			scoreSetting.add(packageScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		ScQtScoreSetting itemScoreSetting = this.getManagedBean()
				.getRecommendationScore();
		if (null != itemScoreSetting && itemScoreSetting.getSettingType() == 5) {
			scoreSetting.add(itemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		List<ScQtDeliveryLoc> localDeliveryList = new ArrayList<ScQtDeliveryLoc>();
		// List<BriefingZoneBean> deliveryList=
		// this.getManagedBean().getDeliveryLocBean();
		if (scQt.isZonal()) {

			List<BriefingZoneBean> deliveryList = this.getManagedBean()
					.getAddressZoneBean();

			if (deliveryList != null && !deliveryList.isEmpty()) {

				for (BriefingZoneBean briefingZoneBean : deliveryList) {
					if (briefingZoneBean.getDeliveryLocList() != null
							&& !briefingZoneBean.getDeliveryLocList().isEmpty()) {

						localDeliveryList.addAll(briefingZoneBean
								.getDeliveryLocList());

					}

				}
				scQt.setScQtDeliveryLocs(localDeliveryList);
			} else if (null == deliveryList || deliveryList.isEmpty()) {

				deliveryList = new ArrayList<BriefingZoneBean>();
				List<BriefingZoneBean> deliveryListLocal = this
						.getManagedBean().getDeliveryLocBean();
				if (null != deliveryListLocal && !deliveryListLocal.isEmpty()) {

					for (BriefingZoneBean briefingZoneBean : deliveryListLocal) {
						if (briefingZoneBean.getDeliveryLocList() != null
								&& !briefingZoneBean.getDeliveryLocList()
										.isEmpty()) {

							localDeliveryList.addAll(briefingZoneBean
									.getDeliveryLocList());

						}

					}
					scQt.setScQtDeliveryLocs(localDeliveryList);

				} else {

					scQt.setScQtDeliveryLocs(null);
				}

			}
		} else {
			List<ScQtDeliveryLoc> nonZoneList = this.getManagedBean()
					.getDeliveryNonZoneList();
			if (nonZoneList != null && !nonZoneList.isEmpty()) {
				scQt.setScQtDeliveryLocs(nonZoneList);
			} else {
				scQt.setScQtDeliveryLocs(null);
			}
		}

		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		reqDTO.setScQt(scQt);
		List<ScQtCommitteeActivity> committeActivities = this.getManagedBean()
				.getCommitteeActivities();
		if (null != committeActivities) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			for (int i = 0; i < committeActivities.size(); i++) {
				if (pmUser.getIdentificationNo().equals(
						committeActivities.get(i).getScCommitteeMember()
								.getPmUser().getIdentificationNo())) {
					committeActivities.get(i).setSignedOff(true);
					break;
				}

			}
		}
		reqDTO.setCommitteActivities(committeActivities);
		return reqDTO;
	}

	// Prepare Qt Checklist Save related method
	public void validatePrepareQtChecklist() {

		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();

		SpecificationResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();

		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		switch (activeIndex) {
		case 0:
			ScQt scQt = constructSpecCommitteDtls().getScQt();
			specReqDTO.setScQt(scQt);
			specSO.validateSpecCommitteGeneralInfo(specReqDTO);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
			getfulfilmentDtls(resDTO.getScQt());
			break;
		case 1:
			ScQt scQtSupplier = constructSpecCommitteDtls().getScQt();

			specReqDTO.setScQt(scQtSupplier);
			// specSO.validateSupplierQulificationInfo(specReqDTO);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			getfulfilmentDtls(resDTO.getScQt());
			retriveBusinessOwnerQualificationCriteri();

			break;
		case 2:

			ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())) {

				specReqDTO.setScQt(scQtTechChecklist);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveTechnicalSpecification();

			}

			if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())) {

				specReqDTO.setScQt(scQtTechChecklist);
				specSO.validateCheckList(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveTechnicalCheckList();

			}
			break;
		case 3:
			ScQt finChecklist = constructSpecCommitteDtls().getScQt();

			specReqDTO.setScQt(finChecklist);
			resDTO = specSO.saveQTDtl(specReqDTO);
			renewSesionObject();
			this.getManagedBean().setScQt(resDTO.getScQt());
			getfulfilmentDtls(resDTO.getScQt());
			retriveFinancialCheckList();
			break;
		case 4:
			ScQt scoring = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scoring
					.getEvaluationType())) {

				List<ScQtCheckList> financialScoring2 = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring2);
				specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
				specReqDTO.setScQt(scoring);
				specSO.validateSCScoreSettings(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveFinancialScoring();

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scoring
					.getEvaluationType())) {

				List<ScQtCheckList> technicalScoring = this.getManagedBean()
						.getTechnicalScoring();
				ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
						.getTechnicalScoreSetting();

				if (technicalScoreSetting != null) {
					specReqDTO.setScoreSetting(technicalScoreSetting);
				}
				ScQtScoreSetting demoScoreSetting = this.getManagedBean()
						.getDemoScoreSetting();
				if (demoScoreSetting != null) {
					specReqDTO.setScoreSetting(demoScoreSetting);
				}

				specReqDTO.setCheckLists(technicalScoring);
				specReqDTO.setSpecType(QTConstants.TECHNICAL_SPEC_TYPE);
				specReqDTO.setScQt(scoring);
				specSO.validateSCScoreSettings(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveTechnicalScoring();

			}
			break;

		case 5:
			ScQt recommendation = constructSpecCommitteDtls().getScQt();

			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(recommendation
					.getEvaluationType())) {

				List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
				ScQtScoreSetting packageScoreSetting = this.getManagedBean()
						.getRecommendationPackageScore();
				if (null != packageScoreSetting
						&& packageScoreSetting.getSettingType() == 4) {
					scoreSetting.add(packageScoreSetting);

				}

				ScQtScoreSetting itemScoreSetting = this.getManagedBean()
						.getRecommendationScore();
				if (null != itemScoreSetting
						&& itemScoreSetting.getSettingType() == 5) {
					scoreSetting.add(itemScoreSetting);

				}

				specReqDTO.setScoreSettings(scoreSetting);
				specReqDTO.setScQt(this.getManagedBean().getScQt());
				specSO.validateSCRecommandationSettings(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveRecommendationDetails();

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(recommendation
					.getEvaluationType())) {

				List<ScQtCheckList> financialScoring1 = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting
						&& financialScoreSetting.getSettingType() == 2) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring1);
				specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
				specReqDTO.setScQt(recommendation);
				specSO.validateSCScoreSettings(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveFinancialScoring();

			}
			break;

		case 6:
			ScQt delivery = constructSpecCommitteDtls().getScQt();
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(delivery
					.getEvaluationType())) {
				if (delivery.isZonal()) {
					if (null == delivery.getScQtDeliveryLocs()) {
						List<BriefingZoneBean> zoneBean = this.getManagedBean()
								.getDeliveryLocBean();
						if (null != zoneBean) {
							for (BriefingZoneBean deliverybean : zoneBean) {
								if (null != deliverybean.getDeliveryLocList()) {
									delivery.setScQtDeliveryLocs(deliverybean
											.getDeliveryLocList());
								}
							}
						}
					}
				}
				specReqDTO.setScQt(delivery);
				specSO.validateSCdelieryLocs(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveDeliveryDetails();

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(delivery
					.getEvaluationType())) {

				List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
				ScQtScoreSetting packageScoreSetting = this.getManagedBean()
						.getRecommendationPackageScore();
				if (null != packageScoreSetting
						&& packageScoreSetting.getSettingType() == 4) {
					scoreSetting.add(packageScoreSetting);

				}

				ScQtScoreSetting itemScoreSetting = this.getManagedBean()
						.getRecommendationScore();
				if (null != itemScoreSetting
						&& itemScoreSetting.getSettingType() == 5) {
					scoreSetting.add(itemScoreSetting);

				}

				specReqDTO.setScoreSettings(scoreSetting);
				specReqDTO.setScQt(delivery);
				specSO.validateSCRecommandationSettings(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveRecommendationDetails();

			}
			break;

		case 7:
			ScQt fulfilMent = constructSpecCommitteDtls().getScQt();
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(fulfilMent
					.getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					specReqDTO.setScQt(fulfilMent);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveFulfilmentDtls();
				}

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(fulfilMent
					.getEvaluationType())) {
				if (fulfilMent.isZonal()) {
					if (null == fulfilMent.getScQtDeliveryLocs()) {
						List<BriefingZoneBean> zoneBean = this.getManagedBean()
								.getDeliveryLocBean();
						if (null != zoneBean) {
							for (BriefingZoneBean deliverybean : zoneBean) {
								if (null != deliverybean.getDeliveryLocList()) {
									fulfilMent.setScQtDeliveryLocs(deliverybean
											.getDeliveryLocList());
								}
							}
						}
					}
				}
				specReqDTO.setScQt(fulfilMent);
				specSO.validateSCdelieryLocs(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveDeliveryDetails();

			}
			break;

		case 8:
			ScQt fulfilMentdtls = constructSpecCommitteDtls().getScQt();
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(fulfilMentdtls
					.getEvaluationType())) {

			}

			if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(fulfilMentdtls
					.getEvaluationType())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					specReqDTO.setScQt(fulfilMentdtls);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveFulfilmentDtls();
				}

			}
			break;

		case 9:
			break;

		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		/*
		 * this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);
		 * this
		 * .getManagedBean().setScQtCheckSt2techProposal(techchecklistScore);
		 * this.getManagedBean().setFinancialScoring(financialScoring);
		 * this.getManagedBean().setTechnicalScoring(technicalScoringList);
		 */

	}

	public String specCommitteBpmTask(String action) {

		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode("en");
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
		if (isSpecChairPerson(roleDesc)) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			SpecificationResDTO resDTO = updateSpecCommitteDtls(action, null);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMessgeConstant("SUBMIT"),
														new String[] {
																"Specification Preparation And Scoring For",
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		}
		return null;

	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteTreeNode() {
		TreeNode selectedTreeNode = this.getManagedBean().getSelectedTreeNode();
		if (null == selectedTreeNode) {
			throw new EpException("Please select At least one Zone");
		}
		TplSpecZoneDtl zoneDtls = (TplSpecZoneDtl) selectedTreeNode
				.getChildren().get(0).getData();
		if (null != selectedTreeNode) {
			List<TreeNode> zoneTrees = this.getManagedBean()
					.getZoneGeneralTrees();
			for (int i = 0; i < zoneTrees.size(); i++) {
				if (zoneDtls.getZoneName().equalsIgnoreCase(
						((TplSpecZoneDtl) zoneTrees.get(i).getChildren().get(0)
								.getData()).getZoneName())) {
					zoneTrees.remove(i);
					break;
				}
			}
			this.getManagedBean().setZoneGeneralTrees(zoneTrees);
			this.getManagedBean().setIndex(-1);
		}
	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteSupplierQualificationTreeNode() {

		this.getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		this.getManagedBean().setIndex(-1);
	}

	// Category Codes Set method
	private void updateCategoryCodesToQt(List<ScQtCategory> categories,
			ScQt scQt) {

		if (null != categories && !categories.isEmpty()) {

			for (ScQtCategory category : categories) {
				if (null != category.getPmCategoryL3()
						&& null != category.getPmCategoryL3().getPmCategoryL2()
						&& null != category.getPmCategoryL3().getPmCategoryL2()
								.getPmCategoryL1()) {
					category.setPmCategoryL1(category.getPmCategoryL3()
							.getPmCategoryL2().getPmCategoryL1());
					category.setPmCategoryL2(category.getPmCategoryL3()
							.getPmCategoryL2());
					category.setScQt(scQt);
					setUserDetails(category);
				}
				scQt.setScQtCategorys(categories);
			}
		}
	}

	private ScQtDoc constructQtDocByType(ScQtDoc qtDoc, String processType) {
		ScQtDoc scQtDoc = qtDoc;
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		if (null == scQtDoc) {
			scQtDoc = this.getManagedBean().getScqtDoc();
			PmStatus status = new PmStatus();

			status.setStatusId(60550L);
			scQtDoc.getScQtCheckList().setPmStatus(status);
		}

		IOrgProfileSO profileSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileReqDTO profileReq = new OrgProfileReqDTO();
		profileReq.setOrgProfileId(1L);
		OrgProfileResDTO profileRes = profileSo
				.retrieveOrgProfileByProfileId(profileReq);
		ParameterReqDTO templateTypeReqDTO = new ParameterReqDTO();
		templateTypeReqDTO
				.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
		templateTypeReqDTO.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
		ParameterResDTO templateTypeResDTO = parameterSo
				.retrieveParameterByTypeAndCode(templateTypeReqDTO);
		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

		scQtDoc.getScQtCheckList().setTitle(specMaster.getSpecName());

		List<TplSpecQuestion> questions = getManagedBean()
				.getTemplateSpecBean().getSpecMaster().getTplSpecQuestions();

		if (QTConstants.TECHNICAL_DOC_TYPE.equalsIgnoreCase(processType)) {
			if (!this.getManagedBean().isSpecSelectStats()) {
				TemplateSpecHelper.getInstance().cloneTplSpecQuestions(
						specMaster, questions);
			} else {
				specMaster.setTplSpecQuestions(questions);
			}

		} else {
			ParameterReqDTO finSpecType = new ParameterReqDTO();
			finSpecType.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
			finSpecType
					.setParameterCode(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			ParameterResDTO finSpecresDTO = parameterSo
					.retrieveParameterByTypeAndCode(finSpecType);
			specMaster.setSpecTypeCode(finSpecresDTO.getPmParameter());

			TemplateSpecHelper.getInstance().cloneTplSpecQuestions(specMaster,
					questions);
		}

		specMaster.setTemplateTypeCode(templateTypeResDTO.getPmParameter());
		specMaster.setProcessType(TemplateSpecConst.PROCESS_TYPE_SOURCING);
		specMaster.setOrgProfile(profileRes.getPmOrgProfile());
		// specMaster.setDurationMonth(12);
		specMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		List<ScQtDoc> docs = new ArrayList<ScQtDoc>();
		docs.add(scQtDoc);
		specMaster.setScQtDocs(null);
		specMaster.setScQuoteDocs(null);
		specMaster.setBrandNew(false);
		specMaster.setRecordStatus(true);
		setUserDetails(specMaster);

		scQtDoc.setScQt(this.getManagedBean().getScQt());

		// scQtDOc.setScQtCheckList(checkList);
		scQtDoc.setTplSpecMaster(specMaster);
		return scQtDoc;
	}

	// Ministry Spec Approval Method
	private SpecificationReqDTO prepareSpecChirPersonRequest() {
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		List<ScQtRemark> remarks = this.getManagedBean().getScQtRemarksList();
		if (null != remarks) {
			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					remarks.remove(i);
				}
			}
		}
		ScQt scQt = this.getManagedBean().getScQt();
		if (null != remarks) {
			scQt.setScQtRemarks(remarks);
		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		specReqDTO.setScQt(scQt);
		return specReqDTO;
	}

	/**
	 * Method to return the docType..
	 * 
	 * @param taskName
	 * @return
	 */
	private String getDocTypeByTaskName(String taskName) {
		String DOC_TYPE = QTConstants.DOCTYPE_QT;
		return DOC_TYPE;
	}

	/**
	 * Method to create the supplier criteria object and zonecriteria and
	 * loccriteria based on radio selection.
	 */
	public void zoneRadioChange(AjaxBehaviorEvent event) {
		Boolean isZonal = (Boolean) event.getComponent().getAttributes()
				.get("value");
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
		if (null == supplierCriteria) {
			supplierCriteria = new ScQtSupplierCriteria();
		}
		if (isZonal) {
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();
			if (null == zoneCriteria) {
				zoneCriteria = new ScQtZoneCriteria();
				zoneCriteria.setScQtSupplierCriteria(supplierCriteria);
				supplierCriteria.setQtZoneCriterias(zoneCriteria);
			}
			supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel());
			supplierCriteria.setScQtLocCriteria(null);
			scQt.setZoneLocationLevel(new PmParameter());

			this.getManagedBean().setZoneTrees(null);
		} else {
			this.getManagedBean().setZoneGeneralTrees(null);
			supplierCriteria.setQtZoneCriterias(null);
			if (null == supplierCriteria.getLocationLevel()) {
				supplierCriteria.setLocationLevel(new PmParameter());
			}

			this.getManagedBean().setZoneGeneralTrees(null);

		}
		scQt.setScQtSupplierCriteria(supplierCriteria);
		this.getManagedBean().setScQt(scQt);

	}

	// View Circular Attachmnets Download Method
	public void downloadCircularDtls() {

		try {
			downloadFile(this.getManagedBean().getCtCircularVer()
					.getCircularFileName(), this.getManagedBean()
					.getCtCircularVer().getCircularFilePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Spec Item Type DropDOwn Values method
	public SelectItem[] getRoleTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}

	// Local Goods DisPlay List Values Method
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());

					}
				}
			}
		} finally {
		}
		return items;
	}

	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		// Temp : QtNumber + userId
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getScQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}

	public SpecificationResDTO updateSpecCommitteDtls(String action,
			PmDigiSign digiSign) {
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls();
		spReqDTO.setOutcome(action);
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();
		SpecificationResDTO resDTO = null;
		try {
			resDTO = specSO.updateBPMAction(spReqDTO);

		} finally {
			specSO = null;
		}
		return resDTO;
	}

	private boolean isSpecChairPerson(List<PmRoleDesc> roleDesc) {
		if (null != roleDesc) {
			for (PmRoleDesc pmRoleDesc : roleDesc) {
				if (RoleCode.SC_CHAIRPERSON.toString().equalsIgnoreCase(
						pmRoleDesc.getPmRole().getRoleCode())) {
					return true;

				}
			}
		}
		return false;
	}

	public String cancelQt() {
		SpecificationReqDTO spReqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getScQt();
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		spReqDTO.setScQt(scQt);
		spReqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		((SCQuotationTenderDataType) this.getManagedBean()
				.getSpecificationDataType()).setCircular(true);
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();

		try {
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMessgeConstant(BpmOutcomeConst.BPM_SUBMIT),
														new String[] {
																"Specification Creation For ",
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

				return CommonUIConst.ACKNOWLEDGE_PAGE;

			}
		} finally {
			specSO = null;
		}
		return CommonUIConst.ACKNOWLEDGE_PAGE;

	}

	public String gpkiSignAction(PmDigiSign digiSign) {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(
				BpmOutcomeConst.BPM_SUBMIT, digiSign);
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(BpmOutcomeConst.BPM_SUBMIT),
													new String[] {
															"Specification Preparation And Scoring For",
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * will be called to remove specification..
	 * 
	 * @return
	 */
	public String cancelSpecAction(String flowType) {
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		if (null != checkList.getQtChecklistId()) {
			checkList.setDeleted(true);
			List<ScQtCheckList> toBeRemoveCheckLists = new ArrayList<ScQtCheckList>();
			toBeRemoveCheckLists.add(checkList);

			List<ScQtCheckList> financialCheckLIsts = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			for (int i = 0; i < financialCheckLIsts.size(); i++) {
				if (checkList.getTitle().equalsIgnoreCase(
						financialCheckLIsts.get(i).getTitle())) {
					ScQtCheckList financialCheckList = financialCheckLIsts
							.get(i);
					financialCheckList.setDeleted(true);
					toBeRemoveCheckLists.add(financialCheckList);
					financialCheckLIsts.remove(financialCheckLIsts.get(i));
				}
			}

			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

			specReqDTO.setCheckLists(toBeRemoveCheckLists);
			SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);

			if (resDTO.isSuccessful()) {
				if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
					retriveTechnicalSpecification();
					return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
				} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
						.equalsIgnoreCase(flowType)) {
					if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType())) {
						retriveTechnicalSpecification();
					} else if (QTConstants.EVALUATION_TWO_TIER
							.equalsIgnoreCase(this.getManagedBean().getScQt()
									.getEvaluationType())) {
						retriveTechnicalCheckList();
					}
					return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

				}
			}
		} else {
			this.getManagedBean().getScQtCheckSt2techProposal();
		}
		return null;

	}

	/**
	 * Will be called to save or edit for both techinical flowType maybe
	 * preparespec or scroing
	 * 
	 * @return
	 */
	public String saveTemplateSpecDetails(String flowType, String specType) {
		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
				.getSpecName());
		scQtCheckList.getScQtDoc().getTplSpecMaster()
				.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);

		if (!SourcingConstants.FINANCIAL_DOCTYPE.equalsIgnoreCase(specType)) {
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmStatus(status);

		} else {
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_COMPLETE);
			scQtCheckList.setPmStatus(status);
		}
		List<ScQtCheckList> toBeRemoveList = null;
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {

			List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			toBeRemoveList = new ArrayList<ScQtCheckList>();
			if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
				for (int i = 0; i < finanicalCheckList.size(); i++) {
					ScQtCheckList qtCheckList = finanicalCheckList.get(i);
					if (qtCheckList.getTitle().equalsIgnoreCase(
							scQtCheckList.getTitle())) {
						qtCheckList.setDeleted(true);
						toBeRemoveList.add(qtCheckList);
						finanicalCheckList.remove(qtCheckList);
					}
				}
			}
		}
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		updatedCheckLists.add(scQtCheckList);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setCheckLists(updatedCheckLists);
		reqDTO.setTobeRemoveList(toBeRemoveList);
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationResDTO resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);

		// Need to redirect particualr page..
		if (resDTO.isSuccessful()) {
			if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
				retriveTechnicalSpecification();
				return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
			} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
					.equalsIgnoreCase(flowType)) {
				List<ScQtCheckList> savedCheckLists = resDTO
						.getSavedCheckLists();
				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType())) {
					retriveTechnicalSpecification();
				} else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(this.getManagedBean().getScQt()
								.getEvaluationType())) {
					for (ScQtCheckList checkList : savedCheckLists) {
						if (SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(checkList.getDocCategory())) {
							this.getManagedBean().getScQtCheckSt2techProposal()
									.remove(this.getManagedBean().getIndex());
							this.getManagedBean()
									.getScQtCheckSt2techProposal()
									.add(this.getManagedBean().getIndex(),
											checkList);
						} else {
							this.getManagedBean()
									.getScQtCheckSt2financeProposalList()
									.remove(this.getManagedBean().getIndex());
							this.getManagedBean()
									.getScQtCheckSt2financeProposalList()
									.add(this.getManagedBean().getIndex(),
											checkList);

						}
					}
				}
				return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

			}
		}
		return null;
	}

	/**
	 * Will be called to save or edit for both techinical flowType maybe
	 * preparespec or scroing
	 * 
	 * @return
	 */
	public String completeTemplateSpecDetails(String flowType, String specType) {
		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		if (null == scQtCheckList.getTitle()
				|| "".equalsIgnoreCase(scQtCheckList.getTitle())) {
			scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
					.getTplSpecMaster().getSpecName());
		}
		scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
				.getSpecName());
		PmStatus status = new PmStatus();
		status.setStatusId(QTStatusConst.TS_COMPLETE);
		scQtCheckList.setPmStatus(status);
		scQtCheckList.getScQtDoc().getTplSpecMaster()
				.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
		// Code to construct financial checklist..
		ScQt scQt = this.getManagedBean().getScQt();
		if (null == scQt) {
			throw new EpException("Qt id should not be null");
		}
		TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
				.cloneTplSpecMaster(
						scQtCheckList.getScQtDoc().getTplSpecMaster(), false);
		specMaster.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);

		if (scQt.isZonal()) {
			ScQtSupplierCriteria supplierCriteria = scQt
					.getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias()) {
				specMaster
						.setTplSpecZone(supplierCriteria.getQtZoneCriterias());
			} else {
				throw new EpException("Please choose the zones before proceed.");
			}
		}
		IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO finSpecType = new ParameterReqDTO();
		finSpecType.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
		finSpecType
				.setParameterCode(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
		ParameterResDTO finSpecresDTO = parameterSO
				.retrieveParameterByTypeAndCode(finSpecType);
		specMaster.setSpecTypeCode(finSpecresDTO.getPmParameter());
		TemplateSpecHelper.getInstance().cloneTplSpecQuestions(
				specMaster,
				scQtCheckList.getScQtDoc().getTplSpecMaster()
						.getTplSpecQuestions());
		ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
				SourcingConstants.FINANCIAL_DOCTYPE);
		PmStatus financialstatus = new PmStatus();
		financialstatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
		qtCheckList.setPmStatus(financialstatus);
		qtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
				.getSpecName());
		qtCheckList.getScQtDoc().getTplSpecMaster()
				.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		updatedCheckLists.add(scQtCheckList);
		updatedCheckLists.add(qtCheckList);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setCheckLists(updatedCheckLists);

		// code if alredy checklist exist need to remove existing finalcialspec
		List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();
		if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
			for (ScQtCheckList financialCheckList : finanicalCheckList) {
				if (this.getManagedBean().getCheckListTitle()
						.equalsIgnoreCase(financialCheckList.getTitle())) {
					financialCheckList.setDeleted(true);
					toBeRemoveList.add(financialCheckList);
				}
			}
		}
		reqDTO.setTobeRemoveList(toBeRemoveList);
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationResDTO resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);

		// Need to redirect particualr page..
		if (resDTO.isSuccessful()) {
			if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
				retriveTechnicalSpecification();
				return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
			} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
					.equalsIgnoreCase(flowType)) {
				List<ScQtCheckList> savedCheckLists = resDTO
						.getSavedCheckLists();
				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType())) {
					retriveTechnicalSpecification();
				} else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(this.getManagedBean().getScQt()
								.getEvaluationType())) {
					for (ScQtCheckList checkList : savedCheckLists) {
						if (SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(checkList.getDocCategory())) {
							this.getManagedBean().getScQtCheckSt2techProposal()
									.remove(this.getManagedBean().getIndex());
							this.getManagedBean()
									.getScQtCheckSt2techProposal()
									.add(this.getManagedBean().getIndex(),
											checkList);
						} else {
							this.getManagedBean()
									.getScQtCheckSt2financeProposalList()
									.remove(this.getManagedBean().getIndex());
							this.getManagedBean()
									.getScQtCheckSt2financeProposalList()
									.add(this.getManagedBean().getIndex(),
											checkList);

						}
					}
				}
				return "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

			}
		}

		return null;
	}

	/**
	 * will be called to create new template spec
	 * 
	 * @param tplSpecMaster
	 * @return
	 */
	public String createTemplateSpec(TplSpecMaster tplSpecMaster,
			String flowtype) {
		this.getManagedBean().setSpecSelectStats(false);
		try {
			ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
			specReqDTO.setSpecMaster(tplSpecMaster);
			specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(specReqDTO);
			TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(resDTO.getSpecMaster(), true);
			specMaster
					.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
					SourcingConstants.TECHNICAL_DOCTYPE);

			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(specMaster);

			this.getManagedBean().setScQtChecklist(qtCheckList);
		} finally {

		}

		if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowtype)) {

			return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";
		} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
				.equalsIgnoreCase(flowtype)) {

			return "/xhtml/sc/main/technicalCommitteeTemplate.xhtml?faces-redirect=true";
		}

		return null;
	}

	private ScQtCheckList constructQtDocByType(TplSpecMaster specMaster,
			String docType) {
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ScQtDoc scqtDocDest = new ScQtDoc();
		setUserDetails(scqtDocDest);
		PmStatus status = specMaster.getStatus();
		if (null == status) {
			status = new PmStatus();
		}
		status.setStatusId(60550L);
		specMaster.setStatus(status);
		ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
		localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
		localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
		ParameterResDTO localGoodsResDTO = parameterSo
				.retrieveParameterByTypeAndCode(localGoodsReqDTO);
		specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
		scqtDocDest.setTplSpecMaster(specMaster);
		scqtDocDest.setScQt(this.getManagedBean().getScQt());
		// scqtDocDest.setTplDocMaster(null);
		ScQtCheckList checkList = new ScQtCheckList();
		checkList.setDocCategory(docType);

		ParameterReqDTO specType = new ParameterReqDTO();
		specType.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		specType.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
		ParameterResDTO specTyperesDTO = parameterSo
				.retrieveParameterByTypeAndCode(specType);

		ParameterReqDTO checkListActionType = new ParameterReqDTO();
		checkListActionType
				.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		checkListActionType
				.setParameterCode(ParameterConst.CHECKLIST_ACTION_INPUT);
		ParameterResDTO checkListactionresDTO = parameterSo
				.retrieveParameterByTypeAndCode(checkListActionType);
		checkList.setChecklistSeq(1);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(true);
		checkList.setAction(checkListactionresDTO.getPmParameter());
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setDefinationType(specTyperesDTO.getPmParameter());
		PmStatus checklistStatus = new PmStatus();
		checklistStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
		checkList.setPmStatus(checklistStatus);
		setUserDetails(checkList);
		scqtDocDest.setScQtCheckList(checkList);

		return checkList;
	}

	public String specEditAction(ScQtCheckList checklist, int index,
			String specType) {
		this.getManagedBean().setSpecStatusId(
				checklist.getPmStatus().getStatusId());
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setScQtChecklist(checklist);
		this.getManagedBean().setCheckListTitle(checklist.getTitle());

		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO resDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		checklist.getScQtDoc().setTplSpecMaster(resDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(checklist.getScQtDoc()
				.getTplSpecMaster());
		if (SourcingConstants.TECHINICAL_STAGE.equalsIgnoreCase(specType)) {
			return "/xhtml/sc/main/BusinessOwnerTechnicalSpecEdit.xhtml?faces-redirect=true";
		} else if (SourcingConstants.FINANCIAL_STAGE.equalsIgnoreCase(specType)) {
			return "/xhtml/sc/main/financialTemplate.xhtml?faces-redirect=true";
		}
		return null;

	}

}
