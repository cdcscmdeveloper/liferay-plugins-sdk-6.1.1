/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.quationtender.IQTTemplateSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
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
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
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
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocReqDTO;
import my.ep.dto.sc.quationtender.QTTemplateDocResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.specification.SpecificationReqDTO;
import my.ep.dto.sc.specification.SpecificationResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
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
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
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
import my.ep.persistence.entity.sc.quationtender.ScQtSpecificationRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.directpurchase.ZonesBean;
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
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

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
	private static Logger log = Logger
			.getLogger(SpecificationBackingBean.class);
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
		// this.getManagedBean().setCircularType("");
		// this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		// this.getManagedBean().setPanelItemCategory(null);
		// this.getManagedBean().setSelectPanelItem(false);
		if (this.getManagedBean().getScQt().isPanel()) {
			retriveBusinessOwnerQualificationCriteri("BO");
			if (null != this.getManagedBean().getCtPanelItem()) {

				List<CtPanelItem> panelItem = this.getManagedBean()
						.getPanelItemList();
				panelItem = new ArrayList<CtPanelItem>();
				panelItem.add(this.getManagedBean().getCtPanelItem());
				this.getManagedBean().setPanelItemList(panelItem);
				this.getManagedBean().setSelectCtPanelItem(
						this.getManagedBean().getCtPanelItem());
			}
			if (null != this.getManagedBean().getCtCircularVer()) {
				// circularListSelect(this.getManagedBean().getCtCircularVer());
				getManagedBean().getCtSearchBean().setCirCularNo(
						this.getManagedBean().getCtCircularVer()
								.getCircularNo());
				this.getManagedBean().setCircularType(
						this.getManagedBean().getCtCircularVer().getDocType());
				this.getManagedBean().setCircularVer(
						this.getManagedBean().getCtCircularVer());
				/*
				 * if (null != this.getManagedBean().getCtPanelItem()) {
				 * this.getManagedBean().setSelectCtPanelItem(
				 * this.getManagedBean().getCtPanelItem()); }
				 */
			}
		}
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

		if (null != treeNodes && !treeNodes.isEmpty()) {
			for (TreeNode treeNode : treeNodes) {
				TplSpecZoneDtl tplSpecZoneDtl = null;
				results = new ArrayList<TplSpecZoneLoc>();
				List<TreeNode> zoneNodes = treeNode.getChildren();
				// zoneDtl=(TplSpecZoneDtl)treeNode.getData();
				// zoneDtl = (TplSpecZoneDtl) zoneNodes.getData();
				// TplSpecZoneDtl zoneDtl=new TplSpecZoneDtl();
				zoneDtl = (TplSpecZoneDtl) zoneNodes.get(0).getData();
				for (TreeNode treeNode1 : zoneNodes) {

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
										tplSpecZoneDtl = criteria
												.getTplSpecZoneDtl();
										// criteria.setScQtSupplierCriteria(supplierCriteria);
										setUserDetails(criteria);
										results.add(criteria);
										// tplSpecZoneDtl
										// .setTplSpecZoneLocs(results);

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
									tplSpecZoneDtl = criteria
											.getTplSpecZoneDtl();
									// criteria.setScQtSupplierCriteria(supplierCriteria);
									setUserDetails(criteria);
									results.add(criteria);
									// tplSpecZoneDtl.setTplSpecZoneLocs(results);

								}

								// zoneDtl.setTplSpecZoneLocs(results);
								// setUserDetails(tplSpecZoneDtl);

								// zoneList.add(tplSpecZoneDtl);

							}
						} else {

							TplSpecZoneLoc criteria = (TplSpecZoneLoc) treeNode2
									.getData();
							criteria.setPmState(((TplSpecZoneLoc) treeNode2
									.getData()).getPmState());

							criteria.setTplSpecZoneDtl(((TplSpecZoneDtl) treeNode1
									.getData()));
							tplSpecZoneDtl = criteria.getTplSpecZoneDtl();
							// criteria.setScQtSupplierCriteria(supplierCriteria);
							setUserDetails(criteria);
							results.add(criteria);
							// tplSpecZoneDtl.setTplSpecZoneLocs(results);
						}

					}
					if (tplSpecZoneDtl != null) {
						tplSpecZoneDtl.setTplSpecZoneLocs(results);
						setUserDetails(tplSpecZoneDtl);
						zoneList.add(tplSpecZoneDtl);
						// tplSpecDtls.add(tplSpecZoneDtl);
						// scQuoteZone.setTplSpecZoneDtls(tplSpecDtls);
					} else {
						tplSpecZoneDtl = (TplSpecZoneDtl) treeNode1.getData();
						setUserDetails(tplSpecZoneDtl);
						zoneList.add(tplSpecZoneDtl);
					}

				}

			}

			// zoneDtl.setTplSpecZone(new ScQtZoneCriteria());
			// setUserDetails(zoneDtl);

			// zoneList.add(zoneDtl);

		}

		return zoneList;
	}

	// Method to save the specifcation Details..
	public void saveSpecificationDtls() {
		validateBusinessOwner(false);
		if (!this.getManagedBean().isSupplierValidation()) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.specification.for"),
															this.getManagedBean()
																	.getScQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		}
		this.getManagedBean().setSupplierValidation(false);

	}

	public void validateBusinessOwner(boolean isSpecComplete) {
		try {
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			String zonelocationLevel = this.getManagedBean().getStateCode();
			String locationLevael = this.getManagedBean().getLocalLevelCode();
			int activeIndex = this.getManagedBean().getActiveIndex();
			boolean supplierValidation = this.getManagedBean()
					.isSupplierValidation();
			SpecificationResDTO resDTO = null;
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			boolean selectPanelItem = this.getManagedBean().isSelectPanelItem();
			Long statusId = this.getManagedBean().getCurrentStatusId();

			List<SelectItem> evaluationTypeList = this.getManagedBean()
					.getEvaluationTypeItem();

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
				// setProcurementMode(resDTO.getScQt());
				// this.getManagedBean().setQt(resDTO.getQtDetail());
				break;
			case 1:
				ScQt scQtSupplier = prepareSpecReq().getScQt();
				specReqDTO.setScQt(scQtSupplier);
				if (!this.getManagedBean().isSupplierValidation()) {
					specSO.validateSupplierQulificationInfo(specReqDTO);
				}
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
				retriveBusinessOwnerQualificationCriteri("BO");

				break;
			case 2:
				ScQt techSpec = prepareSpecReq().getScQt();

				specReqDTO.setScQt(techSpec);
				// specSO.validateCheckList(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				if (!isSpecComplete) {
					renewSesionObject();
				} else {

					if (!resDTO.getScQt().isPanel()) {
						this.getManagedBean().setSuppCriteriaopenList(
								new ArrayList<ScQtCategory>());

					}
					this.getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
				}

				this.getManagedBean().setScQt(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
				retriveBusinessOwnerQualificationCriteri("BO");
				if (!isSpecComplete) {
					retriveTechnicalSpecification();
				}
				break;
			case 3:
				ScQt finSpec = prepareSpecReq().getScQt();
				specReqDTO.setScQt(finSpec);
				// specSO.validateCheckList(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				if (!isSpecComplete) {
					renewSesionObject();
				} else {

					if (!resDTO.getScQt().isPanel()) {
						this.getManagedBean().setSuppCriteriaopenList(
								new ArrayList<ScQtCategory>());

					}
					this.getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
				}

				// .getManagedBean().setScQt(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
				retriveBusinessOwnerQualificationCriteri("BO");
				// retriveTechnicalSpecification();
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
			this.getManagedBean().setCurrentStatusId(statusId);
			this.getManagedBean().setEvaluationTypeItem(evaluationTypeList);
			this.getManagedBean().setStateCode(zonelocationLevel);
			this.getManagedBean().setLocalLevelCode(locationLevael);
			this.getManagedBean().setSpecComplete(isSpecComplete);
			this.getManagedBean().setSupplierValidation(supplierValidation);

		} /*
		 * catch(Exception e){ e.printStackTrace(); }
		 */

		finally {

		}

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

			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.specification.details.for"),
															this.getManagedBean()
																	.getScQt()
																	.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

		} finally {

		}
	}

	private List<ScQtCheckList> constructFinSpecsNSocre(
			ScQtCheckList scQtCheckList) {
		List<ScQtCheckList> finSpecCheckLists = this.getManagedBean()
				.getFinSpecList();
		if (null != finSpecCheckLists) {
			for (ScQtCheckList scQtCheckList2 : finSpecCheckLists) {

				if (null != scQtCheckList.getQtScoreWeightage()) {

					List<ScQtScoreOfferPrice> offerPrice = scQtCheckList
							.getQtScoreWeightage().getScQtScoreOfferPrices();
					if (null == offerPrice || offerPrice.isEmpty()) {
						offerPrice = new ArrayList<ScQtScoreOfferPrice>();
						if (null != this.getManagedBean()
								.getOfferPricePackage()) {
							offerPrice.add(this.getManagedBean()
									.getOfferPricePackage());
						}
						/*
						 * if (null !=
						 * this.getManagedBean().getOfferPriceItem()) {
						 * offerPrice.add(this.getManagedBean()
						 * .getOfferPriceItem()); }
						 */
						scQtCheckList.getQtScoreWeightage()
								.setScQtScoreOfferPrices(offerPrice);
					} else {
						List<ScQtScoreOfferPrice> otherofferpriceofferPrice = scQtCheckList
								.getQtScoreWeightage()
								.getScQtScoreOfferPrices();
						if (null != otherofferpriceofferPrice
								&& !otherofferpriceofferPrice.isEmpty()) {
							for (int i = 0; i < otherofferpriceofferPrice
									.size(); i++) {
								ScQtScoreOfferPrice localOfferprice = otherofferpriceofferPrice
										.get(i);
								if (localOfferprice
										.getOfferPriceType()
										.equalsIgnoreCase(
												ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
									if (null != this.getManagedBean()
											.getOfferPricePackage()) {
										otherofferpriceofferPrice
												.remove(localOfferprice);

										otherofferpriceofferPrice.add(this
												.getManagedBean()
												.getOfferPricePackage());
									}
								}
								/*
								 * if (localOfferprice .getOfferPriceType()
								 * .equalsIgnoreCase(
								 * ParameterConst.RECOMMEND_TYPE_ITEM)) { if
								 * (null != this.getManagedBean()
								 * .getOfferPriceItem()) {
								 * otherofferpriceofferPrice
								 * .remove(localOfferprice);
								 * 
								 * otherofferpriceofferPrice.add(this
								 * .getManagedBean() .getOfferPriceItem()); } }
								 */

							}
							scQtCheckList.getQtScoreWeightage()
									.setScQtScoreOfferPrices(
											otherofferpriceofferPrice);
						}
					}

					scQtCheckList2.setQtScoreWeightage(scQtCheckList
							.getQtScoreWeightage());
				}
			}
		}
		return finSpecCheckLists;
	}

	public String newProceedAction() {
		this.getManagedBean().setScQtCheckSt2techProposal(
				new ArrayList<ScQtCheckList>());
		this.getManagedBean().setScQtCheckSt2financeProposalList(
				new ArrayList<ScQtCheckList>());
		if (this.getManagedBean().isReadOnly()) {
			throw new EpException(MessageConst.ERROR_760135);

		}

		if (!this.getManagedBean().getScQt().isPanel()) {
			//this.getManagedBean().setSelectPanelItem(false);
			ScQt scQt = this.getManagedBean().getScQt();
			if (null == scQt.getZoneLocationLevel()) {
				this.getManagedBean().getScQt().setZonal(false);
			}
			ScQtSupplierCriteria supplierCriteria = scQt
					.getScQtSupplierCriteria();

			if (null != supplierCriteria
					&& null != supplierCriteria.getQtSupplierCriteriaId()
					&& (this.getManagedBean().getScQt().isPanel() != this
							.getManagedBean().isPanelChange())) {
				RequestContext.getCurrentInstance().execute(
						"panelApprovalDlgs.show()");
			} else {

				return "specificationCreation.xhtml?faces-redirect=true";
			}

		} else {
			if (null == this.getManagedBean().getCtCircularVer()) {
				throw new EpException(MessageConst.ERROR_760157);
			}
			if (QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
					.getManagedBean().getCircularType())) {
				if (null == this.getManagedBean().getCtPanelItem()) {
					throw new EpException(MessageConst.ERROR_760157);
				}
			}
			if (this.getManagedBean().getScQt().isPanel()
					&& QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
							.getManagedBean().getCircularType())
					&& QTConstants.ITEM_SPECIFIC_TYPE.equalsIgnoreCase(this
							.getManagedBean().getPanelItemCategory())
					|| QTConstants.ITEM_GENERAL_TYPE.equalsIgnoreCase(this
							.getManagedBean().getPanelItemCategory())) {

				ScQt scQt = this.getManagedBean().getScQt();
				scQt.setZonal(false);
				scQt.setQtType(QTConstants.OPEN_TYPE);
				this.getManagedBean().setCtZoneSuppliers(
						new ArrayList<TreeNode>());
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();

				if ((null == supplierCriteria)
						|| (null != supplierCriteria && null == supplierCriteria
								.getQtSupplierCriteriaId())) {
					this.getManagedBean().setScQtCheckSt2techProposal(null);
					supplierCriteria = new ScQtSupplierCriteria();
					supplierCriteria.setScQt(scQt);
					supplierCriteria.setCtPanelItem(this.getManagedBean()
							.getCtPanelItem());
					setUserDetails(supplierCriteria);
					this.getManagedBean().getScQt()
							.setScQtSupplierCriteria(supplierCriteria);
					if (null != this.getManagedBean().getCtPanelItem()
							&& null != this.getManagedBean().getCtPanelItem()
									.getPanelItemId()) {

						this.getManagedBean().getScQt().setZonal(false);
						retriveZonesByCircularNo();
						if (null != this.getManagedBean().getZoneGeneralTrees()
								&& !this.getManagedBean().getZoneGeneralTrees()
										.isEmpty()) {
							this.getManagedBean().getScQt().setZonal(true);
						}
					}
					return "specificationCreation.xhtml?faces-redirect=true";
				} else {

					if (null != supplierCriteria
							&& null != supplierCriteria
									.getQtSupplierCriteriaId()
							&& (this.getManagedBean().getScQt().isPanel() != this
									.getManagedBean().isPanelChange())) {

						RequestContext.getCurrentInstance().execute(
								"panelApprovalDlgs.show()");
					} else {
						if (null != this.getManagedBean().getCtPanelItem()) {
							if (supplierCriteria != null
									&& !supplierCriteria
											.getCtPanelItem()
											.getPanelItemId()
											.equals(this.getManagedBean()
													.getCtPanelItem()
													.getPanelItemId())) {
								supplierCriteria.setCtPanelItem(this
										.getManagedBean().getCtPanelItem());
								removespecDtls();
								if (null == this.getManagedBean().getScQt()
										.getPmProcurementCategory()) {
									this.getManagedBean()
											.getScQt()
											.setPmProcurementCategory(
													new PmParameter());
								}
								this.getManagedBean().getScQt().setZonal(false);
								retriveZonesByCircularNo();
								if (null != this.getManagedBean()
										.getZoneGeneralTrees()
										&& !this.getManagedBean()
												.getZoneGeneralTrees()
												.isEmpty()) {
									this.getManagedBean().getScQt()
											.setZonal(true);
								}
							}

						}
						return "specificationCreation.xhtml?faces-redirect=true";
					}

				}

				// return
				// "searchCircularPanelContract.xhtml?faces-redirect=true";
			}

			else if (this.getManagedBean().getScQt().isPanel()
					&& QTConstants.CIRCULAR_CENTRAL_CONTRACT_TYPE
							.equalsIgnoreCase(this.getManagedBean()
									.getCircularType())) {

				FacesScopes facesScopes = new FacesScopes();
				ScQtRemark remark = new ScQtRemark();
				// ScWorkFlowStatus status = new ScWorkFlowStatus();
				// PmStatus status=new PmStatus();
				setUserDetails(remark);
				remark.setCreatedBy(facesScopes.getUserSessionBean()
						.getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setScQt(this.getManagedBean().getScQt());
				// status.setPmStatus(this.getManagedBean().getScQt().getQtStatus());
				// status.setStatusId(this.getManagedBean().getScQt().getQtStatus().getStatusId());
				remark.setScWorkFlowStatus(null);
				this.getManagedBean().setContractremark(remark);
				this.getManagedBean().setScQtRemarksList(
						new ArrayList<ScQtRemark>());
				this.getManagedBean().getScQtRemarksList()
						.add(this.getManagedBean().getContractremark());

				return "searchCircularPanelContract.xhtml?faces-redirect=true";
			}
		}
		return null;
	}

	public void resetCircular() {
		getManagedBean().setCtSearchBean(new CtCircularSearchBean());
	}

	public String backAction() {
		this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setIndex(-1);
		return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";

	}

	public String committeeBackAction() {
		// this.getManagedBean().setScQtCheckSt2techProposal(null);
		// retriveTechnicalCheckList();
		this.getManagedBean().setChecklistScoreType(null);
		this.getManagedBean().setCompanyScoreType(null);
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setCheckListScore(null);
		this.getManagedBean().setScoringForYears(null);
		this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setIndex(-1);
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

	/*
	 * private ISearchResultBean<ScQtCheckList> retriveChecklistByIdAndTitle(
	 * String title) { IQTSpecSO specSo = null; SpecificationResDTO resDTo =
	 * null; try { SpecificationReqDTO reqDTO = new SpecificationReqDTO();
	 * SearchBean<ScQtCheckList> searchBean = new SearchBean<ScQtCheckList>();
	 * 
	 * String doccategory = QTConstants.FINANCIAL_DOC_TYPE; SearchFilterBean
	 * usernameSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * usernameSearchBean.setSearchField("scQt.qtId");
	 * usernameSearchBean.setSearchValue(getManagedBean().getScQt() .getQtId());
	 * searchBean.getDataFilterBeans().add(usernameSearchBean);
	 * 
	 * SearchFilterBean idNoSearchBean = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * idNoSearchBean.setSearchField("title");
	 * idNoSearchBean.setSearchValue(title);
	 * searchBean.getDataFilterBeans().add(idNoSearchBean);
	 * 
	 * SearchFilterBean specType = new SearchFilterBean(
	 * SearchOperator.OPERATOR_EQUALS.value());
	 * specType.setSearchField("docCategory");
	 * specType.setSearchValue(doccategory);
	 * searchBean.getDataFilterBeans().add(specType);
	 * 
	 * reqDTO.setTitleSearchChecklistBean(searchBean);
	 * 
	 * specSo = BusinessDelegate.getInstance().getServiceObject(
	 * ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class); resDTo =
	 * specSo.retrieveChecklistByIdAndTitle(reqDTO);
	 * 
	 * } finally {
	 * 
	 * } return resDTo.getSpecResultBean(); }
	 */

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

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_700004,
														new String[] { getMessageCommon("lbl.sc.template.spec") },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
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

		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

		switch (previousIndex) {
		case 0:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			// specSo.validateBoGeneralInfo(specReqDTO);

			break;
		case 1:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			// specSo.validateSupplierQulificationInfo(specReqDTO);
			break;

		case 2:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			break;
		case 3:
			specReqDTO.setScQt(prepareSpecReq().getScQt());
			break;

		case 4:
			break;

		}
		switch (activeIndex) {
		case 0:
			retriveSpecdetails(this.getManagedBean().getScQt().getQtId(), "QT");
			break;
		case 1:
			retriveBusinessOwnerQualificationCriteri("BO");
			break;

		case 2:

			retriveTechnicalSpecification();
			break;
		case 3:
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

	public void checktemplateSpecStatus(ScQt scqt) {
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null != techCheckList && !techCheckList.isEmpty()) {
			isTemplateSpec(techCheckList, scqt);
			isTemplateSpecStatus(techCheckList, scqt);

		} else {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
					.getEvaluationType()) && !scqt.isTechEvaluationReq()) {
				// retriveTechnicalSpecification();
				retriveOneTierCheckList();
				techCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scqt
					.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
							.getEvaluationType()) && scqt.isTechEvaluationReq())) {
				retriveTechnicalCheckList();
				techCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
			}
			if (null == techCheckList || techCheckList.isEmpty()
					|| techCheckList.isEmpty()) {
				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
						.getEvaluationType()) && !scqt.isTechEvaluationReq()) {

					throw new EpException(MessageConst.ERROR_760137);

				} else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(scqt.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(scqt.getEvaluationType()) && scqt
								.isTechEvaluationReq())) {

					throw new EpException(MessageConst.ERROR_760138);

				}

			} else {
				isTemplateSpec(techCheckList, scqt);
				isTemplateSpecStatus(techCheckList, scqt);
			}
		}

	}

	public void isTemplateSpec(List<ScQtCheckList> techCheckList, ScQt scqt) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				count++;
			}

		}

		if (count == 0) {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
					.getEvaluationType()) && !scqt.isTechEvaluationReq()) {
				this.getManagedBean().setActiveIndex(2);
				throw new EpException(MessageConst.ERROR_760137);

			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scqt
					.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
							.getEvaluationType()) && scqt.isTechEvaluationReq())) {
				this.getManagedBean().setActiveIndex(2);
				throw new EpException(MessageConst.ERROR_760138);

			}
		}
		this.getManagedBean().setTechnicalSpecCount(count);
	}

	public void isTemplateSpecStatus(List<ScQtCheckList> techCheckList,
			ScQt scqt) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				if (checkList.getPmStatus().getStatusId()
						.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
					count++;
				}
			}

		}
		if (count >= 1) {
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
					.getEvaluationType()) && !scqt.isTechEvaluationReq()) {
				this.getManagedBean().setActiveIndex(2);
				throw new EpException(MessageConst.ERROR_760151);

			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(scqt
					.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scqt
							.getEvaluationType()) && scqt.isTechEvaluationReq())) {
				this.getManagedBean().setActiveIndex(2);
				throw new EpException(MessageConst.ERROR_760152);

			}
		}

	}

	public void checkFinancialSpecStatus(ScQt scqt) {
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList || financialCheckList.isEmpty()) {
			retriveFinancialCheckList();
		}
		// retriveFinancialScoring();

		if (null != financialCheckList && !financialCheckList.isEmpty()) {
			isFinancialSpec(financialCheckList);
			for (ScQtCheckList checkList : financialCheckList) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					if (checkList.getPmStatus().getStatusId()
							.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
						this.getManagedBean().setActiveIndex(3);
						throw new EpException(MessageConst.ERROR_760153);
					}

				}
			}

		}

	}

	public void isFinancialSpec(List<ScQtCheckList> techCheckList) {
		int count = 0;
		for (ScQtCheckList checkList : techCheckList) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				count++;
			}

		}
		if (count == 0) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760215);

		}
		this.getManagedBean().setFinancialSpecCount(count);
		if (this.getManagedBean().getTechnicalSpecCount() != this
				.getManagedBean().getFinancialSpecCount()) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760228);

		}

	}

	public void qtChecklistTabChange(int activeIndex) {

		int previousIndex = this.getManagedBean().getActiveIndex();

		IQTSpecSO specSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

		switch (previousIndex) {
		case 0:
			if (this.getManagedBean().isReadOnly()) {
				if ((null != this.getManagedBean().getScQt()
						.getFulfilmentType() && null == this.getManagedBean()
						.getScQt().getFulfilmentType().getParameterId())
						|| (null == this.getManagedBean().getScQt()
								.getContractDuration())) {
					throw new EpException(MessageConst.ERROR_760134);

				}
			}
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			specSo.validateSpecCommitteGeneralInfo(specReqDTO);

			break;
		case 1:
			specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
			// specSo.validateSupplierQulificationInfo(specReqDTO);

			break;

		case 2:
			ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls());
			checktemplateSpecStatus(scQtTechChecklist);
			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())
					&& !scQtTechChecklist.isTechEvaluationReq()) {

				// specReqDTO.setScQt(constructSpecCommitteDtls().getQtdetail());
				// specSo.validateSupplierQulificationInfo(specReqDTO);

			}

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(scQtTechChecklist
									.getEvaluationType()) && scQtTechChecklist
							.isTechEvaluationReq())) {
				specReqDTO.setScQt(constructSpecCommitteDtls().getScQt());
				specSo.validateCheckList(specReqDTO);
				// specSo.validateCheckList(specReqDTO);

			}

			break;
		case 3:
			ScQt scQtFinChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls());
			checktemplateSpecStatus(scQtFinChecklist);
			checkFinancialSpecStatus(scQtFinChecklist);
			break;

		case 4:
			ScQt scQtTechScore = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(scQtTechScore);
			checkFinancialSpecStatus(scQtTechScore);
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scQtTechScore
					.getEvaluationType())
					&& !scQtTechScore.isTechEvaluationReq()) {
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
				// specSo.validateSpecOfferPrice(specReqDTO);

			}

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtTechScore.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(scQtTechScore.getEvaluationType()) && scQtTechScore
							.isTechEvaluationReq())) {
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
			checktemplateSpecStatus(scQtFinScore);
			checkFinancialSpecStatus(scQtFinScore);
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scQtFinScore
					.getEvaluationType())
					&& !scQtFinScore.isTechEvaluationReq()) {
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
				if (!this.getManagedBean().isReadOnly()) {
					specSo.validateSCRecommandationSettings(specReqDTO);
				}

			}

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtFinScore.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(scQtFinScore.getEvaluationType()) && scQtFinScore
							.isTechEvaluationReq())) {
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
				// specSo.validateSpecOfferPrice(specReqDTO);
			}
			break;
		case 6:

			ScQt scQtRecommendation = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(scQtRecommendation);
			checkFinancialSpecStatus(scQtRecommendation);
			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(scQtRecommendation.getEvaluationType())
					&& !scQtRecommendation.isTechEvaluationReq()) {

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

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(scQtRecommendation.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(scQtRecommendation
									.getEvaluationType()) && scQtRecommendation
							.isTechEvaluationReq())) {

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
				if (!this.getManagedBean().isReadOnly()) {
					specSo.validateSCRecommandationSettings(specReqDTO);
				}
			}

			break;

		case 7:
			ScQt deliveryAddress = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(deliveryAddress);
			checkFinancialSpecStatus(deliveryAddress);
			if (QTConstants.EVALUATION_ONE_TIER
					.equalsIgnoreCase(deliveryAddress.getEvaluationType())
					&& !deliveryAddress.isTechEvaluationReq()) {
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

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(deliveryAddress.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(deliveryAddress
									.getEvaluationType()) && deliveryAddress
							.isTechEvaluationReq())) {

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
			checktemplateSpecStatus(fulfilMentDtls);
			checkFinancialSpecStatus(fulfilMentDtls);
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(fulfilMentDtls
					.getEvaluationType())
					&& !fulfilMentDtls.isTechEvaluationReq()) {
				// specReqDTO.setScQt(constructSpecCommitteDtls().getQtdetail());
				// specSo.validateSupplierQulificationInfo(specReqDTO);

			}

			else if (QTConstants.EVALUATION_TWO_TIER
					.equalsIgnoreCase(fulfilMentDtls.getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER
							.equalsIgnoreCase(fulfilMentDtls
									.getEvaluationType()) && fulfilMentDtls
							.isTechEvaluationReq())) {
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
			ScQt committeeEndorsment = constructSpecCommitteDtls().getScQt();
			checktemplateSpecStatus(committeeEndorsment);
			checkFinancialSpecStatus(committeeEndorsment);
			break;

		}
		switch (activeIndex) {
		case 0:

			retriveSpecdetails(this.getManagedBean().getScQt().getQtId(), "QT");
			break;
		case 1:
			retriveBusinessOwnerQualificationCriteri("SC");
			break;

		case 2:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				// retriveTechnicalCheckList();
				// retriveTechnicalSpecification();
				retriveOneTierCheckList();
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				retriveTechnicalCheckList();
			}
			break;
		case 3:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				retriveFinancialCheckList();

			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				retriveFinancialCheckList();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
			}
			break;

		case 4:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
				retriveFinancialScoring();

			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
				retriveTechnicalScoring();

			}
			break;

		case 5:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				retriveRecommendationDetails();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {

				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
				retriveFinancialScoring();

			}
			break;

		case 6:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				retriveDeliveryDetails();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				retriveRecommendationDetails();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
			}
			break;

		case 7:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					retriveFulfilmentDtls();
					checktemplateSpecStatus(this.getManagedBean().getScQt());
					checkFinancialSpecStatus(this.getManagedBean().getScQt());
				} else if (ParameterConst.FULFILMENT_TYPE_ONE_OFF
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())) {
					retriveCommitteeEndorsment();
					checktemplateSpecStatus(this.getManagedBean().getScQt());
					checkFinancialSpecStatus(this.getManagedBean().getScQt());

				}
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				retriveDeliveryDetails();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
			}
			break;

		case 8:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
				retriveCommitteeEndorsment();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					retriveFulfilmentDtls();
					checktemplateSpecStatus(this.getManagedBean().getScQt());
					checkFinancialSpecStatus(this.getManagedBean().getScQt());
				} else if (ParameterConst.FULFILMENT_TYPE_ONE_OFF
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())) {
					retriveCommitteeEndorsment();
					checktemplateSpecStatus(this.getManagedBean().getScQt());
					checkFinancialSpecStatus(this.getManagedBean().getScQt());

				}
			}
			break;

		case 9:
			if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {

			} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())
					|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType()) && this
							.getManagedBean().getScQt().isTechEvaluationReq())) {
				retriveCommitteeEndorsment();
				checktemplateSpecStatus(this.getManagedBean().getScQt());
				checkFinancialSpecStatus(this.getManagedBean().getScQt());
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
		retriveBusinessOwnerQualificationCriteri("BO");
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

		retriveBusinessOwnerQualificationCriteri("SC");
		if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
				.getManagedBean().getScQt().getEvaluationType())
				&& !this.getManagedBean().getScQt().isTechEvaluationReq()) {
			// retriveTechnicalCheckList();
			// retriveTechnicalSpecification();
			retriveOneTierCheckList();
		} else if (QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
				.getManagedBean().getScQt().getEvaluationType())
				|| (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType()) && this
						.getManagedBean().getScQt().isTechEvaluationReq())) {
			retriveTechnicalCheckList();
			retriveTechnicalScoring();
		}
		retriveFinancialCheckList();
		retriveFinancialScoring();
		retriveRecommendationDetails();
		retriveFulfilmentDtls();
		retriveDeliveryDetails();
		retriveCommitteeEndorsment();
		checktemplateSpecStatus(this.getManagedBean().getScQt());
		checkFinancialSpecStatus(this.getManagedBean().getScQt());
		technicalWeightageCalculate();
		financialWeightageCalculate();

		// this.getManagedBean().setActiveIndex(0);

	}

	/**
	 * To get the Supplier Qualification Criteria Details for BO PrepareSpec..
	 */
	public void retriveBusinessOwnerQualificationCriteri(String type) {

		IQTDetailSO QtDetailsSO;
		QuationTenderResDTO tenderResDTO;

		try {

			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			tenderReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getScQt());

			if (!this.getManagedBean().getScQt().isPanel()) {
				if (null == this.getManagedBean().getSuppCriteriaopenList()
						|| this.getManagedBean().getSuppCriteriaopenList()
								.isEmpty()) {
					tenderResDTO = QtDetailsSO
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
					/*
					 * if (scQt.isZonal() &&
					 * scQt.getEstimatedAmount().intValue() <= 500000 &&
					 * !scQt.getScQtSupplierCriteria().isAllState()
					 * 
					 * && null != this.getManagedBean() .getZoneGeneralTrees()
					 * && this.getManagedBean().getZoneGeneralTrees()
					 * .isEmpty()) {
					 * retriveNonpanelZoneDtls(this.getManagedBean().getScQt()
					 * .getQtId(), "QT");
					 * 
					 * }
					 */
					this.getManagedBean().setTotalSuppliers(0);
					if (SourcingConstants.QT_TYPE_RESTRICTED_TENDER
							.equalsIgnoreCase(scQt.getQtType())
							&& type.equalsIgnoreCase("SC")) {
						if (null != scQt.getScQtSuppliers()
								&& !scQt.getScQtSuppliers().isEmpty()) {
							this.getManagedBean().setRestrictedSupplierList(
									scQt.getScQtSuppliers());
							this.getManagedBean().setTotalSuppliers(
									scQt.getScQtSuppliers().size());
						}
					}

					if (scQt.getEstimatedAmount().intValue() > 500000
							&& this.getManagedBean().getAllStateCode() == 3
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
					}

					if (scQt.getEstimatedAmount().intValue() <= 500000) {
						if (null == scQt.getScQtSupplierCriteria()
								.getLocationLevel()) {
							scQt.getScQtSupplierCriteria().setLocationLevel(
									new PmParameter());
						}
						scQt.getScQtSupplierCriteria().setAllState(false);
					}
					if ((!scQt.isZonal())
							|| (scQt.getEstimatedAmount().intValue() > 500000 && !scQt
									.getScQtSupplierCriteria().isAllState())) {

						List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
						getManagedBean().setZoneTrees(null);
						this.getManagedBean().setPanelItemCategory("");
						if (null != tenderResDTO.getLocationDetails()) {

							if (null == this.getManagedBean()
									.getTotalSuppliersSpecLoc()) {
								this.getManagedBean().setTotalSuppliersSpecLoc(
										new ArrayList<TplSpecLoc>());
							}
							this.getManagedBean().getTotalSuppliersSpecLoc()
									.addAll(tenderResDTO.getLocationDetails());
							for (TplSpecLoc localLoc : tenderResDTO
									.getLocationDetails()) {
								if (null != scQt.getScQtSupplierCriteria()
										&& null != scQt
												.getScQtSupplierCriteria()
												.getLocationLevel()) {
									this.getManagedBean().setLocalLevelCode(
											scQt.getScQtSupplierCriteria()
													.getLocationLevel()
													.getParameterCode());
								}
								getManagedBean().setZoneTrees(
										new ArrayList<TreeNode>());
								ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
								locCriteria.add(criteria);
							}
							if (null != getManagedBean().getZoneTrees()
									&& getManagedBean().getZoneTrees().size() == 0) {
								this.getManagedBean()
										.setExistingSupplierLocationList(
												locCriteria);
								getManagedBean().getZoneTrees().addAll(
										LocationSingleton.getInstance()
												.constuctTreeNode(locCriteria));

							}
						}

					}
				} else {
					ScQt scQt = this.getManagedBean().getScQt();
					if (scQt.getEstimatedAmount().intValue() > 500000
							&& this.getManagedBean().getAllStateCode() == 3
							&& !scQt.getScQtSupplierCriteria().isAllState()) {
						scQt.getScQtSupplierCriteria().setAllState(true);
					}
					if (scQt.getEstimatedAmount().intValue() <= 500000) {
						if (null == scQt.getScQtSupplierCriteria()
								.getLocationLevel()) {
							scQt.getScQtSupplierCriteria().setLocationLevel(
									new PmParameter());
						}
						scQt.getScQtSupplierCriteria().setAllState(false);
					}
					this.getManagedBean().setScQt(scQt);
				}
			} else {
				tenderResDTO = QtDetailsSO
						.retriveQTSuppQulCriDetails(tenderReqDTO);

				ScQt scQt = tenderResDTO.getQtDetail();
				this.getManagedBean().setAllStateCode(
						tenderResDTO.getStateCode());
				this.getManagedBean().setScQt(scQt);
				if (null != scQt.getScQtSupplierCriteria()
						&& null != scQt.getScQtSupplierCriteria()
								.getCtPanelItem()) {
					this.getManagedBean().setCtPanelItem(
							scQt.getScQtSupplierCriteria().getCtPanelItem());
					if (null != scQt.getScQtSupplierCriteria().getCtPanelItem()
							.getCtCircularVer()) {
						this.getManagedBean().setCtCircularVer(
								scQt.getScQtSupplierCriteria().getCtPanelItem()
										.getCtCircularVer());
					}
					this.getManagedBean().setPanelItemCategory(
							getParameterDesc(scQt.getScQtSupplierCriteria()
									.getCtPanelItem().getItemCategoryId()));
					List<CtPanelItem> panelList = this.getManagedBean()
							.getSelectpanelItem();
					panelList = new ArrayList<CtPanelItem>();

					panelList.add(this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getCtPanelItem());
					this.getManagedBean().setSelectpanelItem(panelList);
				}

				if (null != this.getManagedBean().getCtCircularVer()) {
					this.getManagedBean().setCircularExpDate(
							this.getManagedBean().getCtCircularVer()
									.getEndDate());
					this.getManagedBean().setCirCularNo(
							this.getManagedBean().getCtCircularVer()
									.getCircularNo());
					this.getManagedBean().setCircularName(
							this.getManagedBean().getCtCircularVer().getName());
					this.getManagedBean().setCircularType(
							this.getManagedBean().getCtCircularVer()
									.getDocType());
				}

				if (null != this.getManagedBean().getZoneGeneralTrees()
						&& this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {

					/*
					 * if (null != tenderResDTO.getZoneDetails() &&
					 * !tenderResDTO.getZoneDetails().isEmpty()) {
					 * this.getManagedBean().setZoneGeneralTrees( new
					 * ArrayList<TreeNode>()); for (TplSpecZoneDtl zoneDtl :
					 * tenderResDTO .getZoneDetails()) { getManagedBean()
					 * .getZoneGeneralTrees() .add(LocationSingleton
					 * .getInstance() .constuctTreeNode( zoneDtl,
					 * zoneDtl.getTplSpecZoneLocs()));
					 * this.getManagedBean().setCtzones(true); }
					 * 
					 * }
					 */
					retriveZonesByCircularNo();

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
			status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			checkList.setPmEvalStatus(evalStatus);
			checkList.setTitle(panelItem.getItemName());
			checkList.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			checkList
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
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
			specMaster.setScQtDoc(null);
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
			specMaster.setScoringReq(this.getManagedBean().getScQt()
					.isTechEvaluationReq());
			specMaster.setTplSpecQuestions(questionsList);

			this.getManagedBean().getScQt().setZoneLocationLevel(null);
			this.getManagedBean().getScQt().getScQtSupplierCriteria()
					.setLocationLevel(null);
			this.getManagedBean().getScQt().setFulfilmentType(null);
			if (this.getManagedBean().getScQt().isZonal()) {
				validateBusinessOwner(true);
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getScQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					specMaster.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}

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

			if (null != this.getManagedBean().getPanelItemCategory()
					&& QTConstants.ITEM_SPECIFIC_TYPE.equalsIgnoreCase(this
							.getManagedBean().getPanelItemCategory())
					&& this.getManagedBean().isSelectPanelItem()
					&& this.getManagedBean().getScQt().isPanel()) {
				createCircularItemSpec(this.getManagedBean().getCtPanelItem());
				this.getManagedBean().setSelectPanelItem(false);
			}
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
				reqDTO.setPrepareSpec(false);

				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				resDto = specSO.retrieveSpecDetails(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						new ArrayList<ScQtCheckList>(resDto.getSpecResultBean()
								.getResultList()));
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDto.getFinSpecResultBean().getResultList());
			}

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
				reqDTO.setPrepareSpec(false);

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
			initializeRemarks(this.getManagedBean().getScQt(), null, this
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

		if (getManagedBean().getMarkDeletedTechChecklist() == null) {
			getManagedBean().setMarkDeletedTechChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getScQt());
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {
					if (null != resDTO.getTechnicalCheckList()) {
						// Collections.sort(resDTO.getTechnicalCheckList());
						for (ScQtCheckList checklist : resDTO
								.getTechnicalCheckList()) {
							if (checklist.getPmDocument() != null) {
								if (!checklist
										.getPmDocument()
										.getDocDefinitionKey()
										.contains(
												SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE)) {
									localcheckList.add(checklist);
								}
							} else {
								localcheckList.add(checklist);
							}
						}
					}
				} else {
					// Collections.sort(resDTO.getTechnicalCheckList());
					localcheckList = new ArrayList<ScQtCheckList>(
							resDTO.getTechnicalCheckList());
					// localcheckList.addAll(resDTO.getTechnicalCheckList());
				}

				this.getManagedBean().setScQtCheckSt2techProposal(
						localcheckList);
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2financeProposalList());
			}
		} finally {
			specSo = null;
		}
	}

	/**
	 * Will be called to get financial check list if not exist in managed bean
	 */
	public void retriveFinancialCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedFinChecklist() == null) {
			getManagedBean().setMarkDeletedFinChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			/*
			 * if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
			 * .getManagedBean().getScQt().getEvaluationType()) &&
			 * !this.getManagedBean().getScQt().isTechEvaluationReq()) {
			 * this.getManagedBean().setScQtCheckSt2financeProposalList(null); }
			 */
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
				// Collections.sort(resDTO.getFinancialCheckLists());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
			}
		} finally {
			specSo = null;
		}
	}

	private boolean isAtleastOneSeriveReq(List<ScQtCheckList> checklist) {
		int i = 0;
		if (null != checklist && !checklist.isEmpty()) {
			for (ScQtCheckList specChecklist : checklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(specChecklist.getDefinationType()
								.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.COMMON_COMPONENT,
										ITemplateSpecSO.class);
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(specChecklist.getScQtDoc()
								.getTplSpecMaster());
						specReqDTO
								.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						TemplateSpecResDTO resDTO = templateSpecSO
								.getTemplateMaster(specReqDTO);
						/*
						 * specChecklist.getScQtDoc().setTplSpecMaster(
						 * resDTO.getSpecMaster());
						 */
						if (resDTO
								.getSpecMaster()
								.getItemTypeCode()
								.getParameterCode()
								.equalsIgnoreCase(
										ParameterConst.ITEM_TYPE_SERVICE)) {
							i++;
							if (i >= 1) {
								return true;
							}
						}
					}
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
				checkList.setChecklistSeq(pmDoc.getDisplaySeq());
				PmStatus evalStatus = new PmStatus();
				evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				checkList.setPmEvalStatus(evalStatus);
				checkListList.add(checkList);
			}

		} finally {

		}
		return checkListList;

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
						new ArrayList<ScQtCheckList>(resDTO
								.getTechnicalCheckList()));
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
						if (null == this.getManagedBean()
								.getTechnicalScoreSetting()) {
							this.getManagedBean().setTechnicalScoreSetting(
									recommnendationSetting);
						}
					}

					if (recommnendationSetting.getSettingType() == 3) {
						if (null == this.getManagedBean().getDemoScoreSetting()) {
							this.getManagedBean().setDemoScoreSetting(
									recommnendationSetting);
						}
					}

				}
			}
			this.getManagedBean().setTechnicalScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checkList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != checkList && !checkList.isEmpty()) {
				// Collections.sort(checkList);
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
					if (list.isScoringReq()) {
						this.getManagedBean().getTechnicalScoring().add(list);
					}
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
		/*
		 * if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
		 * .getManagedBean().getScQt().getEvaluationType()) &&
		 * !this.getManagedBean().getScQt().isTechEvaluationReq()) {
		 * this.getManagedBean().setScQtCheckSt2financeProposalList(null); }
		 */
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
				if (null == this.getManagedBean().getFinancialScoreSetting()) {
					ScQtScoreSetting scoreSetting = constructScoreSetting(2);
					this.getManagedBean()
							.setFinancialScoreSetting(scoreSetting);
				}

			} else {
				for (ScQtScoreSetting scoreSttting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (null == this.getManagedBean()
							.getFinancialScoreSetting()) {
						this.getManagedBean().setFinancialScoreSetting(
								scoreSttting);
					}
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
				float financialWeightageScore = 0;
				float financialWeightage = 0;
				// String localTitle="";
				StringBuffer sb = new StringBuffer();
				for (ScQtCheckList list : checklist) {
					if (null == list.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = new ScQtScoreWeightage();
						scoreWeightage.setScore(new BigDecimal(0));
						// scoreWeightage.setScQtScoreOfferPrices(null);
						scoreWeightage.setWeightage(new BigDecimal(0));
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

						if (null == list.getQtScoreWeightage().getWeightage()) {
							list.getQtScoreWeightage().setWeightage(
									new BigDecimal(0));
						}
						/*
						 * financialWeightage = financialWeightage +
						 * list.getQtScoreWeightage().getWeightage()
						 * .intValue();
						 */
						sb.append(list.getTitle());
						sb.append(",");

						specChecklist.add(list);
					} else {
						/*
						 * if (!list.getTitle().trim()
						 * .contains("Annual Balance Sheet") && !list.getTitle()
						 * .trim() .contains(
						 * "Verification from Financial Institution")) {
						 * localChecklist.add(list); }
						 */
						if (list.isScoringReq()) {
							localChecklist.add(list);
						}
					}

					int local = (int) addScore;
					this.getManagedBean().setFinPoints(local);
				} //
					// ScQtCheckList schecklist = specChecklist.get(0);
				if (null != specChecklist && specChecklist.size() > 0) {

					Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();

					for (ScQtCheckList checklistlocal : specChecklist) {
						map.put(checklistlocal.getQtScoreWeightage()
								.getQtScoreWeightageId(), checklistlocal
								.getQtScoreWeightage());
					}
					List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
							map.values());
					for (ScQtScoreWeightage weightage : list) {
						financialWeightageScore = financialWeightageScore
								+ weightage.getScore().intValue();
						financialWeightage = financialWeightage
								+ weightage.getWeightage().intValue();
					}

					ScQtCheckList tmpCheckList = new ScQtCheckList();
					// PmParameter definationType = new PmParameter();
					// definationType
					// .setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION);
					tmpCheckList.setDefinationType(specChecklist.get(0)
							.getDefinationType());
					specChecklist.get(0).getQtScoreWeightage()
							.setScore(new BigDecimal(financialWeightageScore));

					specChecklist.get(0).getQtScoreWeightage()
							.setWeightage(new BigDecimal(financialWeightage));
					this.getManagedBean().setFinSpecList(specChecklist);
					tmpCheckList.setQtScoreWeightage(specChecklist.get(0)
							.getQtScoreWeightage());
					// this.getManagedBean().setScQtChecklist(tmpCheckList);
					if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {
						getManagedBean().setScoreVarience(
								specChecklist.get(0).getQtScoreWeightage()
										.getScore());
					}
					tmpCheckList.setTitle(sb.toString());

					List<ScQtScoreOfferPrice> offerPrice = specChecklist.get(0)
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
						/*
						 * if (null ==
						 * this.getManagedBean().getOfferPriceItem()) {
						 * ScQtScoreOfferPrice itemOfferPrice = new
						 * ScQtScoreOfferPrice();
						 * itemOfferPrice.setScQtScoreWeightage(tmpCheckList
						 * .getQtScoreWeightage()); itemOfferPrice
						 * .setOfferPriceType
						 * (ParameterConst.RECOMMEND_TYPE_ITEM);
						 * setUserDetails(itemOfferPrice);
						 * this.getManagedBean().setOfferPriceItem(
						 * itemOfferPrice); offerPrice.add(itemOfferPrice);
						 * 
						 * }
						 */
						tmpCheckList.getQtScoreWeightage()
								.setScQtScoreOfferPrices(offerPrice);

					} else {
						for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList
								.getQtScoreWeightage()
								.getScQtScoreOfferPrices()) {

							if (ParameterConst.RECOMMEND_TYPE_PACKAGE
									.equalsIgnoreCase(scoreOfferPrice
											.getOfferPriceType())) {
								if (null == this.getManagedBean()
										.getOfferPricePackage()) {
									this.getManagedBean().setOfferPricePackage(
											scoreOfferPrice);
								}
							}/*
							 * else if (ParameterConst.RECOMMEND_TYPE_ITEM
							 * .equalsIgnoreCase(scoreOfferPrice
							 * .getOfferPriceType())) { if (null ==
							 * this.getManagedBean() .getOfferPriceItem()) {
							 * this.getManagedBean().setOfferPriceItem(
							 * scoreOfferPrice); } }
							 */
						}

					}
					this.getManagedBean().getFinancialScoring()
							.add(0, tmpCheckList);
				}

				// this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);

				this.getManagedBean().getFinancialScoring()
						.addAll(localChecklist);

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
						if (enddate.equalsIgnoreCase("10")) {
							endMonth = "9";
						}
						if (enddate.equalsIgnoreCase("11")) {
							endMonth = "10";
						}
						if (enddate.equalsIgnoreCase("12")) {
							endMonth = "11";
						}
						int start = Integer.parseInt(startMonth);
						int end = Integer.parseInt(endMonth);

						for (int i = start + 1; i <= end + 1; i++) {
							count++;
						}

						long days = DateUtils.getDaysDifference(
								scQtSchedule.getFromDate(),
								scQtSchedule.getToDate());
						int day = (int) days;
						int months = day / 30;
						scQtSchedule.setTotalMonths(new BigDecimal(months));

						scQtSchedule.setStartMonth(startMonth);
						scQtSchedule.setEndMonth(endMonth);
						scQtSchedule.setStartYear(startYear);
						scQtSchedule.setEndYear(endYear);

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
		ICountrySO countrySo = null;
		this.getManagedBean().setDeliveryAddressSelect(
				new ArrayList<PmAddressType>());
		try {
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);

			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryResDTO.getPmCountry().getCountryId());
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
			Long stateId = (Long) event.getComponent().getAttributes()
					.get("value");
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			if (stateId != null) {
				reqDTO.setStateId(stateId);
				List<PmState> stateList = this.getManagedBean().getStateList();
				for (PmState sate : stateList) {
					if (sate.getStateId().equals(stateId)) {
						this.getManagedBean().setStateCode(sate.getStateCode());

					}
				}
			} else {
				this.getManagedBean().setStateCode(null);
			}
			this.getManagedBean().setDistrictList(null);
			this.getManagedBean().setDivisionList(null);
			if (ParameterConst.SARAK_SK_CODE.equalsIgnoreCase(this
					.getManagedBean().getStateCode())) {

				this.getManagedBean().setDivisionList(
						PmCommonUtil.retrievePmDivisionList(stateId));
			} else {

				districtSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
				resDto = districtSo.retriveDistrictListByStateID(reqDTO);

				if (resDto != null) {
					this.getManagedBean().setDistrictList(
							resDto.getDistrictList());
				}

			}
		} finally {
			districtSo = null;
		}

	}

	public void retriveDivisionDistrict(AjaxBehaviorEvent event) {
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			Long divisionId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (divisionId != null) {
				reqDTO.setDivisionId(divisionId);
				List<PmDivision> divisionList = this.getManagedBean()
						.getDivisionList();
				for (PmDivision division : divisionList) {
					if (division.getDivisionId().equals(divisionId)) {
						this.getManagedBean().setDivisionCode(
								division.getDivisionCode());

					}
				}
			} else {
				this.getManagedBean().setDivisionCode(null);
			}
			this.getManagedBean().setDistrictList(null);
			districtSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			resDto = districtSo.retriveDistrictListByDivision(reqDTO);

			this.getManagedBean().setDistrictList(resDto.getDistrictList());

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

			} else {
				this.getManagedBean().setDistrictCode(null);
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
			if (null != ministryId) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(ministryId);
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				this.getManagedBean()
						.setPtjtList(new ArrayList<PmOrgProfile>());

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

			this.getManagedBean().setPtjtList(resDTO.getOrgProfileList());

		} finally {
			orgProfileSO = null;
		}

	}

	public void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;

		try {

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteeType(QTConstants.DOCTYPE_SPEC_COMMITTE);
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
			qtReqDTO.setDocType(QTConstants.DOCTYPE_PREPARE_SPEC);
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);

			if (resDto.getRemarks() == null) {
				if (null == this.getManagedBean().getSpecificationRemarks()) {
					this.getManagedBean().setSpecificationRemarks(
							new ArrayList<ScQtSpecificationRemark>());
					initializeRemarks(this.getManagedBean().getScQt(), null,
							this.getManagedBean().getSpecificationRemarks(),
							ScQtSpecificationRemark.class);
				}
			}

			else {

				List<ScQtSpecificationRemark> remarkList = new ArrayList<ScQtSpecificationRemark>();
				for (ScRemark remark : resDto.getRemarks()) {
					ScQtSpecificationRemark qtRemark = (ScQtSpecificationRemark) remark;
					remarkList.add(qtRemark);

				}
				if (null == this.getManagedBean().getSpecificationRemarks()
						|| this.getManagedBean().getSpecificationRemarks()
								.isEmpty()) {
					this.getManagedBean().setSpecificationRemarks(remarkList);
					initializeRemarks(this.getManagedBean().getScQt(), null,
							this.getManagedBean().getSpecificationRemarks(),
							ScQtSpecificationRemark.class);
				}
			}

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

	/*
	 * private TplSpecZoneDtl getZone(TreeNode zoneLocation) { TplSpecZoneDtl
	 * result = null; if (zoneLocation.getData() instanceof TplSpecZoneDtl) {
	 * TplSpecZoneDtl locationDTO = (TplSpecZoneDtl) zoneLocation .getData(); if
	 * (locationDTO != null) { result = locationDTO; } else { if
	 * (zoneLocation.getChildren() != null && zoneLocation.getChildren().size()
	 * > 0) { for (TreeNode childLocationNode : zoneLocation .getChildren()) {
	 * result = getZone(childLocationNode); } } } } else { if
	 * (zoneLocation.getChildren() != null && zoneLocation.getChildren().size()
	 * > 0) { for (TreeNode childLocationNode : zoneLocation.getChildren()) {
	 * result = getZone(childLocationNode); } } }
	 * 
	 * return result; }
	 */

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
					this.getManagedBean().setLocalLevelCode(locationLevelCode);

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
		this.getManagedBean().setZoneLocation(Boolean.FALSE);
	}

	public void technicalSpecBoValidate() {
		getManagedBean().setRequired(Boolean.TRUE);
	}

	// Display Location level Dropdown values
	public SelectItem[] getDropdownLocationValues(String locationLevel) {
		IOrgProfileSO orgProfile = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDto = orgProfile
				.retrieveOrgStateCodeByOrgProfileId(this.getManagedBean()
						.getScQt().getPmOrgProfile().getOrgProfileId());
		this.getManagedBean().setAllStateCode(orgResDto.getStateStatusId());
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (this.getManagedBean().getAllStateCode() == 2) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
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

	// Check method for supplier Locations existing
	private List<ScQtLocCriteria> checkExistingLocation(
			List<ScQtLocCriteria> currentLocationList,
			List<ScQtLocCriteria> existingLocations, String locationLevelCode) {
		List<ScQtLocCriteria> criteriaList = new ArrayList<ScQtLocCriteria>();
		if (null == existingLocations || existingLocations.isEmpty()) {
			if (null != currentLocationList && !currentLocationList.isEmpty())

				criteriaList.addAll(currentLocationList);
		} else {
			if (null != currentLocationList) {
				List<PmState> states = new ArrayList<PmState>();
				List<PmDivision> divisions = new ArrayList<PmDivision>();
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				for (ScQtLocCriteria specLoc : currentLocationList) {
					if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_STATE
									.equalsIgnoreCase(locationLevelCode)) {
						PmState state = specLoc.getPmState();
						if (null != state) {
							if (states.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									states.add(existingLoc.getPmState());
									criteriaList.add(existingLoc);
								}
							}

							if (!states.contains(state)) {
								criteriaList.add(specLoc);
							}

						}
					} else if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_DISTRICT
									.equalsIgnoreCase(locationLevelCode)) {
						PmDistrict district = specLoc.getPmDistrict();
						if (null != district) {
							if (districts.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									districts.add(existingLoc.getPmDistrict());
									criteriaList.add(existingLoc);
								}
							}

							if (!districts.contains(district)) {
								criteriaList.add(specLoc);
							}
						}
					} else if (null != locationLevelCode
							&& ParameterConst.LOCATION_LEVEL_DIVISION
									.equalsIgnoreCase(locationLevelCode)) {
						PmDivision divison = specLoc.getPmDivision();
						if (null != divison) {
							if (divisions.isEmpty()) {
								for (ScQtLocCriteria existingLoc : existingLocations) {
									divisions.add(existingLoc.getPmDivision());
									criteriaList.add(existingLoc);
								}
							}

							if (!divisions.contains(divison)) {
								criteriaList.add(specLoc);
							}
						}

					}
				}
			}
		}

		return criteriaList;
	}

	// Supplier Locations Pop Up Method
	public void showZoneLocationPopup() {

		ICountrySO countrySo = null;
		try {

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);

			Long locationLevel = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getLocationLevel()
					.getParameterId();

			if (locationLevel == null) {
				/*
				 * ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				 * districtReqDTO
				 * .setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				 * districtReqDTO
				 * .setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				 * ParameterResDTO districtResDTO = parameterSo
				 * .retrieveParameterByTypeAndCode(districtReqDTO);
				 * 
				 * locationLevel = districtResDTO.getPmParameter()
				 * .getParameterId();
				 */
				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setLocalLevelCode(
							paramDesc.getPmParameter().getParameterCode());
					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					// List<TplSpecZoneLoc> locationDTOs = new
					// ArrayList<TplSpecZoneLoc>();
					List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();
					if (getManagedBean().getZoneTrees() != null) {
						for (TreeNode treeNode : getManagedBean()
								.getZoneTrees()) {
							List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
							locationDTOs.addAll(locationDTO);
						}
					}

					List<PmState> states = SourcingLocationHelper
							.getStateFromLocationDTO(locationDTOs);
					List<PmDivision> divisions = SourcingLocationHelper
							.getDivisionFromLocationDTO(locationDTOs);
					List<PmDistrict> districts = SourcingLocationHelper
							.getDistrictFromLocationDTO(locationDTOs);
					getManagedBean().setRootLocationTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											states,
											districts,
											divisions,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											ScQtLocCriteria.class));
					getManagedBean().setIndex(-1);
				}

			}
			RequestContext.getCurrentInstance().execute("ZoneLocation.show()");
		} finally {

		}
	}

	// Zone Details Pop up method
	public void showGeneralZoneLocationPopup() {

		ICountrySO countrySo = null;

		try {
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			if (locationLevel == null) {
				/*
				 * ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				 * districtReqDTO
				 * .setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				 * districtReqDTO
				 * .setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				 * ParameterResDTO districtResDTO = parameterSo
				 * .retrieveParameterByTypeAndCode(districtReqDTO);
				 * 
				 * locationLevel = districtResDTO.getPmParameter()
				 * .getParameterId();
				 */
				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setZoneLocation(true);
					this.getManagedBean().setStateCode(
							paramDesc.getPmParameter().getParameterCode());
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
					this.getManagedBean().setIndex(-1);
					this.getManagedBean().setZoneName("");
				}

			}
			RequestContext.getCurrentInstance().execute("ZoneGeneral.show()");
		} finally {

		}

	}

	// Supplier Qualification Criteria Locations Add Method
	public void updateZoneLocation() {
		List<ScQtLocCriteria> locationDTOs = new ArrayList<ScQtLocCriteria>();

		// TreeNode[] listSelectedTreeNode =
		// getManagedBean().getSelectedNodes().clone();
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<TplSpecLoc> specLoc = new ArrayList<TplSpecLoc>();

		if (getManagedBean().getSelectedNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedNodes()) {
				if (!treeNode.getParent().isSelected()) {

					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;
						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
					locationDTOs.addAll(locationDTO);
				}

				else {

					List<ScQtLocCriteria> locationDTO = getLocations(treeNode);
					// LocationDTO locationDTO = (LocationDTO)
					// treeNode.getData();
					for (ScQtLocCriteria locCritera : locationDTO) {
						TplSpecLoc localSpecLoc = (TplSpecLoc) locCritera;

						specLoc.add(localSpecLoc);
					}
					this.getManagedBean().getTotalSuppliersSpecLoc()
							.addAll(specLoc);
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
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}

		List<TreeNode> localList = new ArrayList<TreeNode>();
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (getManagedBean().getIndex() >= 0) {
				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStaeList = new ArrayList<PmState>();
					localStaeList.add(state);
					if (getManagedBean().getIndex() == -1) {

						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, localStaeList,
												districts, divisions, cities,
												ScQtLocCriteria.class));
					}
					if (getManagedBean().getIndex() >= 0) {

						/*
						 * getManagedBean().getZoneTrees().remove(
						 * getManagedBean().getIndex());
						 */
						localList.add(LocationSingleton.getInstance()
								.buildLocationTree(null, localStaeList,
										districts, divisions, cities,
										ScQtLocCriteria.class));

					}

				}
				getManagedBean().getZoneTrees().addAll(localList);
			}
		}

		else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}
			if (null != districts && !districts.isEmpty()) {

				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(
								districts, null);

				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
						.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts,
								null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
						.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null
						&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {

						// TreeNode stateNode = new
						// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
						// zoneNode);
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null
								&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								// divisions.add(division);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
										.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
										.keySet();
								if (districtsCitiesKeySet != null
										&& !districtsCitiesKeySet.isEmpty()) {

									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}

								}
							}
							if (getManagedBean().getIndex() == -1) {

								getManagedBean().getZoneTrees().add(
										LocationSingleton.getInstance()
												.buildLocationTree(null,
														states, districts,
														divisions, cities,
														ScQtLocCriteria.class));
							}
							if (getManagedBean().getIndex() >= 0) {

								/*
								 * getManagedBean().getZoneTrees().remove(
								 * getManagedBean().getIndex());
								 */
								localList.add(LocationSingleton.getInstance()
										.buildLocationTree(null, states,
												districts, divisions, cities,
												ScQtLocCriteria.class));
								// getManagedBean().setZoneTrees(localList);
							}
						}
					}
					getManagedBean().getZoneTrees().addAll(localList);
				}

				if (statesDistrictsCitiesKeySet != null
						&& !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
								.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);

							}

						}
						if (getManagedBean().getIndex() == -1) {

							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, states,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
						if (getManagedBean().getIndex() >= 0) {

							/*
							 * getManagedBean().getZoneTrees().remove(
							 * getManagedBean().getIndex());
							 */
							localList.add(LocationSingleton.getInstance()
									.buildLocationTree(null, states, districts,
											divisions, cities,
											ScQtLocCriteria.class));
							// getManagedBean().setZoneTrees(localList);

						}
					}
					getManagedBean().getZoneTrees().addAll(localList);
				}

			}
		}

		else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
			}
			if (null != divisions && !divisions.isEmpty()) {

				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton
						.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();

						// new
						// DefaultTreeNode(QTConstants.NODE_STATE,stateLocation,
						// zoneNode);
						List<PmDivision> divisions1 = mapStatesDivision
								.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);

							}
							if (getManagedBean().getIndex() == -1) {

								getManagedBean().getZoneTrees().add(
										LocationSingleton.getInstance()
												.buildLocationTree(null,
														states, districts,
														divisions, cities,
														ScQtLocCriteria.class));
							}
							if (getManagedBean().getIndex() >= 0) {

								/*
								 * getManagedBean().getZoneTrees().remove(
								 * getManagedBean().getIndex());
								 */
								localList.add(LocationSingleton.getInstance()
										.buildLocationTree(null, states,
												districts, divisions, cities,
												ScQtLocCriteria.class));
								// getManagedBean().setZoneTrees(localList);
							}
						}
					}
					getManagedBean().getZoneTrees().addAll(localList);
				}

			}
		}

		this.getManagedBean().setIndex(-1);

	}

	// General Zone Locations Add Method
	public void updateZoneGeneralLocation() {
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<ZonesBean> beans = getManagedBean().getBeans();
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getStateCode();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			/*
			 * for (TreeNode treeNode : getManagedBean()
			 * .getSelectedGeneralTreeNodes()) {
			 * 
			 * if (!treeNode.getParent().isSelected()) { List<TplSpecZoneLoc>
			 * locationDTO = getZoneLocations(treeNode);
			 * 
			 * this.getManagedBean().getTotalSuppliersSpecLoc()
			 * .addAll(locationDTO);
			 * 
			 * allNodes.addAll(locationDTO); }
			 * 
			 * else { List<TplSpecZoneLoc> locationDTO =
			 * getZoneLocations(treeNode);
			 * 
			 * this.getManagedBean().getTotalSuppliersSpecLoc()
			 * .addAll(locationDTO);
			 * 
			 * allNodes.addAll(locationDTO); }
			 * 
			 * }
			 */

			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (locationLevel
						.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
					if (treeNode.getType().equals("state")) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
						if (treeNode.getChildCount() > 0) {
							break;
						}
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				} else {

					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				}
			}
		}

		if (null == getManagedBean().getZoneGeneralTrees()) {
			getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
		}
		if (null != allNodes && !allNodes.isEmpty()) {

			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			TplSpecZoneDtl zone = new TplSpecZoneDtl();

			zone.setZoneName(getManagedBean().getZoneName());

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(allNodes);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(allNodes);

			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(allNodes);

			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(allNodes);
			zonesBean.setTplSpecZoneDtl(zone);
			// validate zone name
			checkZoneExistsOrNot(zone, getManagedBean().getIndex());

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)) {
				// check whether state exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, allNodes);
				}
				zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DIVISION)) {
				// check whether division exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, allNodes);
				}
				zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (locationLevel
					.equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				// check whether district exists or not
				if (!getManagedBean().getBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone, allNodes);
				}
				zonesBean.setTplSpecZoneLocs(allNodes);
			}

			beans.add(zonesBean);
			getManagedBean().setBeans(beans);
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
		} else {
			throw new EpException("Please select At least one Zone Location");
		}
		this.getManagedBean().setZoneLocation(false);
		getManagedBean().setZoneName(null);
		this.getManagedBean().setIndex(-1);
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");

	}

	private List<Long> checkZoneExistsOrNot(TplSpecZoneDtl tplSpecZoneDtl,
			int index) {
		List<Long> results = new ArrayList<Long>();
		if (index < 0) {
			List<ZonesBean> list = getManagedBean().getBeans();
			if (list != null && list.size() > 0) {

				for (ZonesBean zonesBean : list) {
					TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

					if (zone != null) {

						if (zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							throw new EpException(tplSpecZoneDtl.getZoneName()
									+ " zone already exists.");
						}
					}
				}
			}
		}
		return results;
	}

	private void checkDuplicateStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);
			divIds = getPmDivisionIds(tplSpecZoneLocs);
			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> list = getManagedBean().getBeans();

		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (!zone.getZoneName().equalsIgnoreCase(
							tplSpecZoneDtl.getZoneName())) {

						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {

							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											throw new EpException(
													TplSpecZoneLoc.getPmState()
															.getStateName()
															+ " state already exists in "
															+ zone.getZoneName());
										}
									}

								}

								if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											throw new EpException(
													TplSpecZoneLoc
															.getPmDivision()
															.getDivisionName()
															+ " division already exists in "
															+ zone.getZoneName());
										}
									}
								}

								if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											throw new EpException(
													TplSpecZoneLoc
															.getPmDistrict()
															.getDistrictName()
															+ " district already exists in "
															+ zone.getZoneName());
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private List<Long> getPmStateIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmState() != null) {
					results.add(TplSpecZoneLoc.getPmState().getStateId());
				}
			}
		}
		return results;
	}

	private List<Long> getPmDivisionIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDivision() != null) {
					results.add(TplSpecZoneLoc.getPmDivision().getDivisionId());
				}
			}
		}
		return results;
	}

	private List<Long> getPmDistrictsIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDistrict() != null) {
					results.add(TplSpecZoneLoc.getPmDistrict().getDistrictId());
				}
			}
		}
		return results;
	}

	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);

			divIds = getPmDivisionIds(tplSpecZoneLocs);

			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> results = new ArrayList<ZonesBean>();
		List<ZonesBean> list = getManagedBean().getBeans();
		if (list != null && list.size() > 0) {
			for (ZonesBean zonesBean : list) {
				ZonesBean bean = new ZonesBean();
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (tplSpecZoneDtl != null) {
						if (!zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								}
							}
							bean.setTplSpecZoneLocs(specZoneLocs);
						}
						results.add(bean);
					}
				}
			}
			getManagedBean().setBeans(results);
		}
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
						qtSupplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
					if (null != scQt.getEstimatedAmount()
							&& scQt.getEstimatedAmount().intValue() <= 500000) {
						qtSupplierCriteria.setLocationLevel(null);
					}
				} else {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);

					}
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
				List<ScQtLocCriteria> existingcheckLocCriteria = (List<ScQtLocCriteria>) checkExistingLocation(
						locCriteria, this.getManagedBean()
								.getExistingSupplierLocationList(), this
								.getManagedBean().getLocalLevelCode());
				if (existingcheckLocCriteria != null
						&& !existingcheckLocCriteria.isEmpty()) {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(
							existingcheckLocCriteria);

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
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}
			} else if (QTConstants.RESTRICTED_TYPE.equalsIgnoreCase(scQt
					.getQtType())) {
				scQt.setZonal(false);
				scQt.setZoneLocationLevel(null);
				if (null != scQt.getScQtSupplierCriteria()) {
					scQt.getScQtSupplierCriteria().setLocationLevel(null);
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);
						qtSupplierCriteria.setQtZoneCriterias(null);
					}

				}

				List<ScQtCategory> openList = this.getManagedBean()
						.getSuppCriteriaopenList();
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}

			}
		} else if (scQt.isPanel()) {

			List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
					this.getManagedBean().getZoneGeneralTrees(), null);

			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
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
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					zoneCriteria.setModified(true);
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				scQt.setZonal(true);
				qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
						zoneLocal);

			}
			scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(QTConstants.OPEN_TYPE);

		}

		List<ScQtCheckList> techSpecList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		List<ScQtCheckList> localSpecList = new ArrayList<ScQtCheckList>();
		if (!this.getManagedBean().isSpecComplete()) {
			if (null != techSpecList && !techSpecList.isEmpty()) {
				localSpecList.addAll(techSpecList);

			}

			List<ScQtCheckList> finSpecList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			if (null != finSpecList && !finSpecList.isEmpty()) {
				localSpecList.addAll(finSpecList);
			}
			scQt.setScQtChecklists(localSpecList);
		} else {
			List<ScQtCheckList> localSpecList1 = new ArrayList<ScQtCheckList>();
			scQt.setScQtChecklists(localSpecList1);
		}

		List<ScQtRemark> remarksList = this.getManagedBean()
				.getScQtRemarksList();

		if (isNotNullOREmpty(remarksList)) {
			scQt.setScQtRemarks(finalizeRemarks(remarksList));

		}

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
		try {

			// this.getManagedBean().setActiveIndex(0);
			if ((null == this.getManagedBean().getScQt())
					|| (this.getManagedBean().getScQt().isZonal()

					&& null != this.getManagedBean().getZoneGeneralTrees() && this
							.getManagedBean().getZoneGeneralTrees().isEmpty())) {

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
				if (null == scQt.getPmProcurementCategory()) {
					scQt.setPmProcurementCategory(new PmParameter());
				}
				this.getManagedBean().setPanelChange(scQt.isPanel());
				this.getManagedBean().setScQt(scQt);
				if (scQt.isZonal()

						&& null != this.getManagedBean().getZoneGeneralTrees()
						&& this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
					if (null != scQt.getZoneLocationLevel()) {
						this.getManagedBean().setStateCode(
								scQt.getZoneLocationLevel().getParameterCode());
					}
					retriveNonpanelZoneDtls(tenderResDTO.getZoneDetails());

				}

				if (null != scQt.getEstimatedAmount()) {
					// setProcurementMode(scQt);
					setEvaluationType(scQt);
				}
			}

		} finally {
			QtDetailsSO = null;
		}

	}

	// Retrive Non Panel Zone Details Method
	public void retriveNonpanelZoneDtls(List<TplSpecZoneDtl> zonedtls) {

		try {
			List<ZonesBean> beans = getManagedBean().getBeans();
			/*
			 * QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			 * QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			 * tenderReqDTO.setQtId(qtId); tenderReqDTO.setDocType(docType);
			 * QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
			 * tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
			 */
			this.getManagedBean().setCtzones(false);
			if (isNotNullOREmpty(zonedtls)) {

				for (TplSpecZoneDtl zone : zonedtls) {
					this.getManagedBean().setCtzones(true);
					if (null != this.getManagedBean().getScQt()
							&& !this.getManagedBean().getScQt().isPanel()) {
						ZonesBean zonesBean = new ZonesBean();
						zonesBean.setTplSpecZoneDtl(zone);
						zonesBean.setTplSpecZoneLocs(zone.getTplSpecZoneLocs());
						beans.add(zonesBean);
					}
					/*
					 * if (null == this.getManagedBean()
					 * .getTotalSuppliersSpecLoc()) {
					 * this.getManagedBean().setTotalSuppliersSpecLoc( new
					 * ArrayList<TplSpecLoc>()); }
					 * this.getManagedBean().getTotalSuppliersSpecLoc()
					 * .addAll(zone.getTplSpecZoneLocs());
					 */
					getManagedBean().getZoneGeneralTrees().add(
							LocationSingleton.getInstance().constuctTreeNode(
									zone, zone.getTplSpecZoneLocs()));

				}
			}
			getManagedBean().setBeans(beans);

		} finally {

		}
	}

	// Evaluation Type set Method
	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	// Open For Change set Method
	public void openChange(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQt().setOpenFor(type);
	}

	// Quaptaion/Tender Type Set Method

	public void onChangeQtType(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().setSuppCriteriaopenList(
				new ArrayList<ScQtCategory>());
		if (type.equalsIgnoreCase("O")) {

			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null == this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getLocationLevel()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		}

		this.getManagedBean().getScQt().setQtType(type);
	}

	// Panel Yes or no Method
	public void panelChangeValue() {

		// this.getManagedBean().getScQt().setPanel(panel);
	}

	public void panelConformation(AjaxBehaviorEvent event) {
		// Boolean panel = this.getManagedBean().getScQt().isPanel();
		Boolean panel = (Boolean) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQt().setPanel(panel);
		/*
		 * if (null != this.getManagedBean().getScQt() && null !=
		 * this.getManagedBean().getScQt().getEstimatedAmount()) {
		 * RequestContext.getCurrentInstance().execute(
		 * "reviceApprovalDlgs.show()");
		 * 
		 * }
		 */
	}

	public void panelNoConformation() {
		Boolean panel = this.getManagedBean().getScQt().isPanel();
		if (panel) {
			this.getManagedBean().getScQt().setPanel(false);
		} else {
			this.getManagedBean().getScQt().setPanel(true);
		}

	}

	public String panelConformationDelete() {
		if (!this.getManagedBean().getScQt().isPanel()) {

			if (this.getManagedBean().getScQt().isPanel() != this
					.getManagedBean().isPanelChange()) {
				this.getManagedBean().getScQt().setZonal(false);
				removespecDtls();
				if (null == this.getManagedBean().getScQt()
						.getPmProcurementCategory()) {
					this.getManagedBean().getScQt()
							.setPmProcurementCategory(new PmParameter());
				}
				this.getManagedBean().getScQt().setZonal(false);
				getManagedBean().setZoneGeneralTrees(null);
				getManagedBean().setZoneTrees(null);
			}
			return "specificationCreation.xhtml?faces-redirect=true";
		} else {
			if (null == this.getManagedBean().getCtCircularVer()) {
				throw new EpException(MessageConst.ERROR_760157);
			}
			if (QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
					.getManagedBean().getCircularType())) {
				if (null == this.getManagedBean().getCtPanelItem()) {
					throw new EpException(MessageConst.ERROR_760157);
				}
			}
			if (this.getManagedBean().getScQt().isPanel()
					&& QTConstants.CIRCULAR_PANEL_TYPE.equalsIgnoreCase(this
							.getManagedBean().getCircularType())
					&& QTConstants.ITEM_SPECIFIC_TYPE.equalsIgnoreCase(this
							.getManagedBean().getPanelItemCategory())
					|| QTConstants.ITEM_GENERAL_TYPE.equalsIgnoreCase(this
							.getManagedBean().getPanelItemCategory())) {

				ScQt scQt = this.getManagedBean().getScQt();
				scQt.setZonal(false);
				scQt.setQtType(QTConstants.OPEN_TYPE);
				this.getManagedBean().setCtZoneSuppliers(
						new ArrayList<TreeNode>());
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();
				if (supplierCriteria != null
						&& this.getManagedBean().getScQt().isPanel() != this
								.getManagedBean().isPanelChange()) {
					supplierCriteria.setCtPanelItem(this.getManagedBean()
							.getCtPanelItem());
					removespecDtls();
					if (null == this.getManagedBean().getScQt()
							.getPmProcurementCategory()) {
						this.getManagedBean().getScQt()
								.setPmProcurementCategory(new PmParameter());
					}

				} else {
					if (supplierCriteria != null
							&& !supplierCriteria
									.getCtPanelItem()
									.getPanelItemId()
									.equals(this.getManagedBean()
											.getCtPanelItem())) {
						supplierCriteria.setCtPanelItem(this.getManagedBean()
								.getCtPanelItem());
						removespecDtls();
						if (null == this.getManagedBean().getScQt()
								.getPmProcurementCategory()) {
							this.getManagedBean()
									.getScQt()
									.setPmProcurementCategory(new PmParameter());
						}
					}
				}

				if (null != this.getManagedBean().getCtPanelItem()
						&& null != this.getManagedBean().getCtPanelItem()
								.getPanelItemId()) {

					this.getManagedBean().getScQt().setZonal(false);
					retriveZonesByCircularNo();
					if (null != this.getManagedBean().getZoneGeneralTrees()
							&& !this.getManagedBean().getZoneGeneralTrees()
									.isEmpty()) {
						this.getManagedBean().getScQt().setZonal(true);
					}
				}

				this.getManagedBean().setScQtCheckSt2techProposal(null);

			}
			return "specificationCreation.xhtml?faces-redirect=true";
		}

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
				list.setScoringReq(true);
				this.getManagedBean().setTechnicalChecklistAction(null);

				List<String> recommendList = new ArrayList<String>();
				recommendList
						.add(ParameterConst.CHECKLIST_ACTION_MANUAL_SUBMISSION);

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
				recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
				recommendList
						.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

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
				list.setScoringReq(true);
				List<String> recommendList = new ArrayList<String>();
				recommendList
						.add(ParameterConst.CHECKLIST_ACTION_PRESENTATION_DEMO);

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
		recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
		recommendList.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

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
		if (null != this.getManagedBean().getScQt()
				&& QTConstants.EVALUATION_TWO_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType())) {

			recommendList
					.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		return items;
	}

	public void specTitleChange(AjaxBehaviorEvent event) {
		// Boolean panel = this.getManagedBean().getScQt().isPanel();
		String title = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtChecklist().setTitle(title);
	}

	public void technicalTitleChange(int index, List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);
		// checklist.remove(index);
		List<ScQtCheckList> localCheckList = new ArrayList<ScQtCheckList>();
		for (int i = 0; i < checklist.size() - 1; i++) {
			ScQtCheckList anotherchecklist = checklist.get(i);
			localCheckList.add(anotherchecklist);
		}
		if (localCheckList.contains(list)) {
			list.setTitle(null);
			/*
			 * throw new EpException(MessageConst.ERROR_760207, new String[] {
			 * list.getTitle() });
			 */

		}
		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);

	}

	public void financialTitleChange(int index, List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);
		// checklist.remove(index);
		List<ScQtCheckList> localCheckList = new ArrayList<ScQtCheckList>();
		for (int i = 0; i < checklist.size() - 1; i++) {
			ScQtCheckList anotherchecklist = checklist.get(i);
			localCheckList.add(anotherchecklist);
		}
		if (localCheckList.contains(list)) {
			list.setTitle(null);

			/*
			 * throw new EpException(MessageConst.ERROR_760207, new String[] {
			 * list.getTitle() });
			 */
		}
		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

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
		/*
		 * if (null != list.getPmDocument() &&
		 * QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
		 * .equalsIgnoreCase(list.getPmDocument() .getDocDefinitionKey()) &&
		 * list.isScoringReq()) { throw new
		 * EpException(MessageConst.ERROR_760196);
		 * 
		 * }
		 */
		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

	}

	public void setEvaluationType(ScQt scQt) {

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

							this.getManagedBean().setScQt(scQt);
						}
					}
				}
			}

		} finally {
			parameterSo = null;
		}
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
			getQuotationTendervalues(ParameterTypeConst.QUOTATION_TENDER_TYPE);
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

			} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean().getScQt()
						.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
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
		/*
		 * String scheduletype = (String) event.getComponent().getAttributes()
		 * .get("value");
		 */
		specReqDTO.setScQt(this.getManagedBean().getScQt());
		specReqDTO.setFullfilementType(this.getManagedBean()
				.getFulfilmentType());
		specso.removefulfillmentDtls(specReqDTO);

	}

	// Standard or Non standard Spec Selection Method
	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		templateSpecCreate.resetData();
		this.getManagedBean().setStandardTemplate(template);
	}

	public void itemTypeChange(AjaxBehaviorEvent event) {
		Long itemId = (Long) event.getComponent().getAttributes().get("value");
		if (null != itemId) {
			this.getManagedBean().setSearchItemTypeId(itemId);
			// this.getManagedBean().setRequired(false);

		}
	}

	// Method to set the location level drop down values
	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (null != location) {
			if (this.getManagedBean().getScQt().isZonal()) {
				if (null != this.getManagedBean().getScQt()
						.getZoneLocationLevel()) {
					this.getManagedBean().getScQt().getZoneLocationLevel()
							.setParameterId(location);
				}
				if (null != this.getManagedBean().getZoneGeneralTrees()
						&& !this.getManagedBean().getZoneGeneralTrees()
								.isEmpty()) {
					this.getManagedBean().setZoneGeneralTrees(null);
					this.getManagedBean().setBeans(new ArrayList<ZonesBean>());
				}

			}
		}/*
		 * else { this.getManagedBean().getScQt().getScQtSupplierCriteria()
		 * .getLocationLevel().setParameterId(location); if (null !=
		 * getManagedBean().getZoneTrees() &&
		 * !getManagedBean().getZoneTrees().isEmpty()) {
		 * getManagedBean().setZoneTrees(null); } }
		 */
	}

	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes()
				.get("value");
		if (null != location) {
			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getLocationLevel()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getLocationLevel().setParameterId(location);
			}
			if (null != getManagedBean().getZoneTrees()
					&& !getManagedBean().getZoneTrees().isEmpty()) {
				getManagedBean().setZoneTrees(null);
				if (null != this.getManagedBean()
						.getExistingSupplierLocationList()
						&& !this.getManagedBean()
								.getExistingSupplierLocationList().isEmpty()) {
					this.getManagedBean().setSupplierValidation(true);

					this.getManagedBean().setExistingSupplierLocationList(null);

					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getScQt()
							.getScQtSupplierCriteria();
					supplierCriteria.setScQtLocCriteria(null);
					this.getManagedBean().getScQt()
							.setScQtSupplierCriteria(supplierCriteria);
					saveSpecificationDtls();
				}
			}
		}
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
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {
			if (scoreType.equalsIgnoreCase("2")) {

				this.getManagedBean().setScQtCheckListScore(
						new ScQtCheckListScore());
				// toBeRemoveList.add(this.getManagedBean().getCheckListScore());
				if (null != this.getManagedBean().getCheckListScore()
						&& !this.getManagedBean().getCheckListScore().isEmpty()) {

					toBeRemoveList.addAll(this.getManagedBean()
							.getCheckListScore());
					this.getManagedBean().setCheckListScore(
							new ArrayList<ScQtCheckListScore>());
				}
			} else {
				if (null != this.getManagedBean().getScQtCheckListScore()) {
					toBeRemoveList.add(this.getManagedBean()
							.getScQtCheckListScore());
				}
			}
			this.getManagedBean().setToBeRemoveList(toBeRemoveList);
			this.getManagedBean().setChecklistScoreType(scoreType);
		}
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
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {

			if (scoreType.equalsIgnoreCase("2")) {

				this.getManagedBean().setScQtContractScore(
						new ScQtCheckListScore());
				if (null != this.getManagedBean().getScoringForYears()
						&& !this.getManagedBean().getScoringForYears()
								.isEmpty()) {
					toBeRemoveList.addAll(this.getManagedBean()
							.getScoringForYears());
					this.getManagedBean().setScoringForYears(
							new ArrayList<ScQtCheckListScore>());
				}
			} else {
				if (null != this.getManagedBean().getScQtContractScore()) {
					toBeRemoveList.add(this.getManagedBean()
							.getScQtContractScore());
				}
			}
			this.getManagedBean().setToBeRemoveList(toBeRemoveList);
			this.getManagedBean().setCompanyScoreType(scoreType);
		}
	}

	public void fromChange(AjaxBehaviorEvent event) {
		String formvalue = (String) event.getComponent().getAttributes()
				.get("value");
		String currentYear = this.getManagedBean().getStartYear();
		if (null != formvalue && !"".endsWith(formvalue) && null != currentYear) {
			this.getManagedBean().setFromMonth(formvalue);
			int year = Integer.parseInt(currentYear);
			int n = Integer.parseInt(this.getManagedBean().getFromMonth());
			int j = n + 2;
			calculateBankMonths(j, year);
		}
	}

	public void yearChange(AjaxBehaviorEvent event) {
		String year = (String) event.getComponent().getAttributes()
				.get("value");

		int currentYear = Integer.parseInt(year);

		if (currentYear > 0 && null != this.getManagedBean().getFromMonth()
				&& !"".equals(this.getManagedBean().getFromMonth())) {
			int n = Integer.parseInt(this.getManagedBean().getFromMonth());
			int j = n + 2;
			calculateBankMonths(j, currentYear);
		}

	}

	private void calculateBankMonths(int j, int currentYear) {
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
		} else if (j == 12) {
			currentYear = currentYear + 1;
			Startmonth = "January";

		} else if (j == 13) {
			currentYear = currentYear + 1;
			Startmonth = "February";

		}

		this.getManagedBean().setBankstmntTomonth(Startmonth);
		this.getManagedBean().setToMonth(String.valueOf(j));
		this.getManagedBean().setEndYear(String.valueOf(currentYear));
	}

	// While Technical Checklist click on edit will call this..
	public String technicalChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(
				new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(
						checkList.getPmStatus().getStatusId());
				this.getManagedBean().setIndex(index);
				this.getManagedBean().setScQtChecklist(checkList);
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
				/*
				 * templateSpecBean.getSpecMaster().setScoringReq(
				 * this.getManagedBean().getScQt().isTechEvaluationReq());
				 */
				return "/xhtml/sc/main/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);
				this.getManagedBean().setChecklistScoreType(null);

				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setCheckListScore(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setScoringForYears(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setToBeRemoveList(
						new ArrayList<ScQtCheckListScore>());
				if (QTConstants.TECHNICAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {

							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != this.getManagedBean().getScoringForYears()
								&& this.getManagedBean().getScoringForYears()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getScoringForYears());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
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

						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {

							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != this.getManagedBean().getScoringForYears()
								&& this.getManagedBean().getScoringForYears()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getScoringForYears());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);
					return "companyExperiencePrivate.xhtmlfaces-redirect=true";
				}

				else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (resDTO.getCheckListScores() == null
							|| resDTO.getCheckListScores().isEmpty()) {

						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);
						// ScQtCheckListValue checkListValue = new
						// ScQtCheckListValue();
						// setUserDetails(checkListValue);
						// checkListValue.setScQtCheckList(checkList);

						// checkList.setScQtCheckListValue(checkListValue);
						/*
						 * this.getManagedBean().setCheckListScore( new
						 * ArrayList<ScQtCheckListScore>());
						 */

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

						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}

						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}

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

						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);
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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
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

	private void returnCheckListScores(List<ScQtCheckListScore> checklistScores) {

		Collections.sort(checklistScores);

	}

	// All Financial Checklists Edit Actions
	public String financialChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(
				new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
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

				/*
				 * if (this.getManagedBean().getScQt().isZonal()) {
				 * ScQtSupplierCriteria supplierCriteria = this
				 * .getManagedBean().getScQt() .getScQtSupplierCriteria(); if
				 * (null != supplierCriteria && null !=
				 * supplierCriteria.getQtZoneCriterias()) {
				 * supplierCriteria.getQtZoneCriterias().setModified(true);
				 * templateSpecBean.getSpecMaster().setTplSpecZone(
				 * supplierCriteria.getQtZoneCriterias());
				 * 
				 * } }
				 */
				if (this.getManagedBean().getScQt().isZonal()
						&& this.getManagedBean().getScQt().getQtType()
								.equals(QTConstants.OPEN_TYPE)) {
					retriveSpecdetails(this.getManagedBean().getScQt()
							.getQtId(), "QT");
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getScQt()
							.getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {
						// TODO Code needs to be remove
						// setUserDetails(qtSupplierCriteria.getQtZoneCriterias());

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				}

				return "/xhtml/sc/main/financialCommitteeTemplate.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);
				this.getManagedBean().setCheckListScore(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setScoringForYears(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setChecklistScoreType(null);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setToBeRemoveList(
						new ArrayList<ScQtCheckListScore>());
				if (QTConstants.FINANCIAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
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

						this.getManagedBean().setChecklistScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());
						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);
					return "financialPrivateCompany.xhtml?faces-redirect=true";

				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {

						this.getManagedBean().setChecklistScoreType(null);

						// ScQtCheckListValue checkListValue = new
						// ScQtCheckListValue();
						// setUserDetails(checkListValue);

						// checkListValue.setScQtCheckList(checkList);
						// checkList.setScQtCheckListValue(checkListValue);
						/*
						 * this.getManagedBean().setCheckListScore( new
						 * ArrayList<ScQtCheckListScore>());
						 */

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
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

						this.getManagedBean().setChecklistScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);

					return "cashOnhand.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					int year = Calendar.getInstance().get(Calendar.YEAR);

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| null == resDTO.getScQtCheckListValue()) {
						this.getManagedBean()
								.setStartYear(String.valueOf(year));
						this.getManagedBean().setEndYear(String.valueOf(year));

						this.getManagedBean().setChecklistScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
							retriveFromMonthToManthDtls(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);
					return "bankStatement.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(null);

						// ScQtCheckListValue checkListValue = new
						// ScQtCheckListValue();
						// setUserDetails(checkListValue);

						// checkListValue.setScQtCheckList(checkList);

						// checkList.setScQtCheckListValue(checkListValue);
						/*
						 * this.getManagedBean().setCheckListScore( new
						 * ArrayList<ScQtCheckListScore>());
						 */

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);
					return "assetsLiabilities.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {

						this.getManagedBean().setChecklistScoreType(
								QTConstants.USER_SCORE);

						ScQtCheckListScore checklistScore = new ScQtCheckListScore();
						checklistScore.setCriteriaType(new Integer("30"));
						checklistScore.setScoreType(new BigDecimal(this
								.getManagedBean().getChecklistScoreType()));
						setUserDetails(checklistScore);
						this.getManagedBean().setScQtCheckListScore(
								checklistScore);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
					}
					this.getManagedBean().setScQtChecklist(checkList);
					return "supplierinfo.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(null);

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
						if (null != this.getManagedBean().getCheckListScore()
								&& this.getManagedBean().getCheckListScore()
										.size() > 1) {
							returnCheckListScores(this.getManagedBean()
									.getCheckListScore());

						}
						if (null != resDTO.getScQtCheckListValue()) {
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
						}
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

	private void retriveFromMonthToManthDtls(ScQtCheckListValue checkListValue) {
		if (null != checkListValue) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String startMonth = "";
			String endMonth = "";
			String endToMonth = "";
			if (null != checkListValue.getFromDate()
					&& null != checkListValue.getToDate()) {
				String fromdate = dateFormat.format(checkListValue
						.getFromDate());
				String todate = dateFormat.format(checkListValue.getToDate());
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
					endToMonth = "0";
					endMonth = "January";
				}
				if (enddate.equalsIgnoreCase("02")) {
					endToMonth = "1";
					endMonth = "February";
				}
				if (enddate.equalsIgnoreCase("03")) {
					endToMonth = "2";
					endMonth = "March";
				}
				if (enddate.equalsIgnoreCase("04")) {
					endToMonth = "3";
					endMonth = "April";
				}
				if (enddate.equalsIgnoreCase("05")) {
					endToMonth = "4";
					endMonth = "May";
				}
				if (enddate.equalsIgnoreCase("06")) {
					endToMonth = "5";
					endMonth = "June";
				}

				if (enddate.equalsIgnoreCase("07")) {
					endToMonth = "6";
					endMonth = "July";
				}
				if (enddate.equalsIgnoreCase("08")) {
					endToMonth = "7";
					endMonth = "August";
				}
				if (enddate.equalsIgnoreCase("09")) {
					endToMonth = "8";
					endMonth = "September";
				}
				if (enddate.equalsIgnoreCase("10")) {
					endToMonth = "9";
					endMonth = "October";
				}
				if (enddate.equalsIgnoreCase("11")) {
					endToMonth = "10";
					endMonth = "November";
				}
				if (enddate.equalsIgnoreCase("12")) {
					endToMonth = "11";
					endMonth = "December";
				}
				this.getManagedBean().setFromMonth(startMonth);
				this.getManagedBean().setBankstmntTomonth(endMonth);
				this.getManagedBean().setStartYear(startYear);
				this.getManagedBean().setEndYear(endYear);
				this.getManagedBean().setToMonth(endToMonth);
			}
		}
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
					 * throw new EpException( MessageConst.ERROR_100022, new
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

		ITemplateSpecSO templateSpecSO = null;
		try {

			this.getManagedBean().setSpecStatusId(
					checklist.getPmStatus().getStatusId());
			this.getManagedBean().setIndex(index);
			this.getManagedBean().setScQtChecklist(checklist);
			this.getManagedBean().setCheckListTitle(checklist.getTitle());

			templateSpecSO = EpEjbBusinessDelegate.getInstance()
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
			/*
			 * templateSpecBean.getSpecMaster().setScoringReq(
			 * this.getManagedBean().getScQt().isTechEvaluationReq());
			 */

		} finally {
			templateSpecSO = null;
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
			// st2financeproposalList2.setScoringReq(true);
			st2financeproposalList2
					.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
			st2financeproposalList2.setScoringReq(false);
			st2financeproposalList2
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_UPLOAD);

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
			statusreq.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			st2financeproposalList2.setPmStatus(statusres.getPmStatus());
			// st2financeproposalList2.setEvaluationStage("2");
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			st2financeproposalList2.setPmEvalStatus(evalStatus);
			st2financeproposalList.add(st2financeproposalList2);

			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
			this.getManagedBean().setFinStandaraChecklist(false);
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
			this.getManagedBean().setTechStandaraChecklist(false);
			if (null == selectList || selectList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {
					if (techStandard.getTitle().equals(document.getDocName())) {
						isRequired = true;
						throw new EpException(document.getDocName() + " "
								+ "is Existed");
					}
				}
				if (!isRequired) {
					this.getManagedBean().setTechStandaraChecklist(true);
					ScQtCheckList scqtlist = createSpecList("2");

					scqtlist.setTitle(document.getDocName());
					scqtlist.setDefinationType(document
							.getDocDefinitionTypeCode());
					scqtlist.setAction(document.getChecklistActionCode());
					scqtlist.setManditory(false);
					scqtlist.setScoringReq(document.isScoringReq());
					scqtlist.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
					scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);
					setUserDetails(scqtlist);

					scqtlist.setScQt(this.getManagedBean().getScQt());

					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
					IStatusSO statussO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStatusSO.class);

					StatusReqDTO statusreq = new StatusReqDTO();
					statusreq.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					StatusResDTO statusres = statussO
							.retrieveStatusByStatusId(statusreq);

					scqtlist.setPmStatus(statusres.getPmStatus());
					PmStatus evalStatus = new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					scqtlist.setPmEvalStatus(evalStatus);
					compilance.add(scqtlist);
				}

			}

			getManagedBean().setScQtCheckSt2techProposal(compilance);

		} finally {

		}
		RequestContext.getCurrentInstance().execute("stageTwoTech.hide()");
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
			st2technicalproposalList1.setScoringReq(false);
			st2technicalproposalList1
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_UPLOAD);

			st2technicalproposalList1.setScQt(this.getManagedBean().getScQt());
			st2technicalproposalList1.setAction(new PmParameter());

			setUserDetails(st2technicalproposalList1);
			statussO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusreq = new StatusReqDTO();
			statusreq.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			StatusResDTO statusres = statussO
					.retrieveStatusByStatusId(statusreq);

			st2technicalproposalList1.setPmStatus(statusres.getPmStatus());
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			st2technicalproposalList1.setPmEvalStatus(evalStatus);

			st2technicalproposalList.add(st2technicalproposalList1);

			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
			this.getManagedBean().setTechStandaraChecklist(false);
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
			this.getManagedBean().setFinStandaraChecklist(false);
			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}
			if (null == selectList || selectList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {
					if (techStandard.getTitle().equals(document.getDocName())) {
						isRequired = true;
						throw new EpException(document.getDocName() + " "
								+ "is Existed");
					}
				}
				if (!isRequired) {
					this.getManagedBean().setFinStandaraChecklist(true);
					ScQtCheckList scqtlist = createSpecList("2");
					scqtlist.setTitle(document.getDocName());
					scqtlist.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
					scqtlist.setScoringReq(document.isScoringReq());
					// scqtlist.setEnable(true);
					scqtlist.setDefinationType(document
							.getDocDefinitionTypeCode());
					scqtlist.setAction(document.getChecklistActionCode());
					// scqtlist.setEvaluationStage("2");
					scqtlist.setManditory(false);
					scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);

					scqtlist.setScQt(this.getManagedBean().getScQt());

					setUserDetails(scqtlist);
					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());
					statussO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IStatusSO.class);

					StatusReqDTO statusreq = new StatusReqDTO();
					statusreq.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					StatusResDTO statusres = statussO
							.retrieveStatusByStatusId(statusreq);

					scqtlist.setPmStatus(statusres.getPmStatus());
					PmStatus evalStatus = new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					scqtlist.setPmEvalStatus(evalStatus);
					compilance.add(scqtlist);
				}
			}

			getManagedBean().setScQtCheckSt2financeProposalList(compilance);

		} finally {
			statussO = null;
		}
		RequestContext.getCurrentInstance().execute("stageTwoFinance.hide();");
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
			List<PmDocument> localcheckList = new ArrayList<PmDocument>();
			if (!isAtleastOneSeriveReq(this.getManagedBean()
					.getScQtCheckSt2techProposal())) {
				if (null != resDTO.getDocumentList()) {
					// Collections.sort(resDTO.getTechnicalCheckList());
					for (PmDocument checklist : resDTO.getDocumentList()) {

						if (!checklist

						.getDocDefinitionKey().contains(
								SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE)) {
							localcheckList.add(checklist);
						}

					}
				}
			} else {
				// Collections.sort(resDTO.getTechnicalCheckList());
				localcheckList = new ArrayList<PmDocument>(
						resDTO.getDocumentList());
				// localcheckList.addAll(resDTO.getTechnicalCheckList());
			}

			this.getManagedBean().setStageTwoTechComplianceList(localcheckList);
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
			this.getManagedBean().setStateCode(null);
			this.getManagedBean().setDistrictCode(null);
			this.getManagedBean().setDivisionCode(null);
		} finally {

		}
	}

	public void deliveryAccept() {
		IAddressSO addressSo = null;
		try {
			List<PmAddressType> SelectdeliveryList = this.getManagedBean()
					.getDeliveryAddressSelect();
			if (null == SelectdeliveryList || SelectdeliveryList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760145);

			}
			List<PmAddressType> deliveryList = new ArrayList<PmAddressType>();
			addressSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			AddressReqDTO reqDTO = new AddressReqDTO();
			List<Long> addresstypeId = new ArrayList<Long>();
			if (null != SelectdeliveryList && !SelectdeliveryList.isEmpty()) {
				for (PmAddressType addressType : SelectdeliveryList) {
					addresstypeId.add(addressType.getAddressTypeId());
				}
				reqDTO.setAddressTypeIds(addresstypeId);
				AddressResDTO resDTO = addressSo
						.retrieveAddressTypeDtlListById(reqDTO);
				deliveryList.addAll(resDTO.getAddressTypeList());
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

		RequestContext.getCurrentInstance().execute("deliveryAddress.hide();");
	}

	public void deleteDeliveryZone(String deliveryZoneName) {
		List<BriefingZoneBean> deliveryBean = this.getManagedBean()
				.getDeliveryLocBean();
		List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
				.getDeliveryLocRemoveList();
		if (null == locationRemoveList) {
			locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
		}
		if (!this.getManagedBean().getScQt().isPanel()) {
			for (BriefingZoneBean zonebean : deliveryBean) {
				if (zonebean.getTplSpecZoneDtl().getZoneName()
						.equalsIgnoreCase(deliveryZoneName)) {

					List<ScQtDeliveryLoc> deliveryList = zonebean
							.getDeliveryLocList();

					List<ScQtDeliveryLoc> selectdeliveryList = this
							.getManagedBean().getSelectDeliveryList();
					if (null == selectdeliveryList
							|| selectdeliveryList.isEmpty()) {
						throw new EpException(MessageConst.ERROR_760146);
					}
					locationRemoveList.addAll(selectdeliveryList);
					if (deliveryList.size() == selectdeliveryList.size()) {

						deliveryList.clear();

					} else {

						for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);

						}
					}
				}
				this.getManagedBean().setDeliveryLocRemoveList(
						locationRemoveList);

			}
		} else {
			for (BriefingZoneBean zonebean : deliveryBean) {
				if (zonebean.getTplSpecZoneDtl().getPanelZone().getName()
						.equalsIgnoreCase(deliveryZoneName)) {
					List<ScQtDeliveryLoc> deliveryList = zonebean
							.getDeliveryLocList();

					List<ScQtDeliveryLoc> selectdeliveryList = this
							.getManagedBean().getSelectDeliveryList();
					if (null == selectdeliveryList
							|| selectdeliveryList.isEmpty()) {
						throw new EpException(MessageConst.ERROR_760146);
					}
					locationRemoveList.addAll(selectdeliveryList);
					if (deliveryList.size() == selectdeliveryList.size()) {

						deliveryList.clear();

					} else {

						for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);
						}
					}
				}
				this.getManagedBean().setDeliveryLocRemoveList(
						locationRemoveList);
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
			List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null == locationRemoveList) {
				locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
			}
			List<ScQtDeliveryLoc> selectdeliveryList = this.getManagedBean()
					.getSelectDeliveryList();
			if (null == selectdeliveryList || selectdeliveryList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760146);
			}
			locationRemoveList.addAll(selectdeliveryList);

			if (nonzone.size() == selectdeliveryList.size()) {

				nonzone.clear();

			} else {

				for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
					delLoc.setDeleted(true);
					nonzone.remove(delLoc);
				}
			}
			this.getManagedBean().setDeliveryLocRemoveList(locationRemoveList);
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

	public boolean circularDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(enddate, d1);
			int day = (int) days;
			if (day >= 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	public boolean supplierStatusDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(d1, enddate);
			int day = (int) days;
			if (day >= 0) {
				isDate = true;
			}
		}

		return isDate;
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
			ScQtCheckListScore prevSpecScoring = null;
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getCheckListScore();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}

			if (checkListScore.size() > 0) {
				prevSpecScoring = checkListScore.get(checkListScore.size() - 1);
			}

			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(criteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getChecklistScoreType()));
			if (prevSpecScoring != null) {
				BigDecimal prevRangeTo = prevSpecScoring.getRangeTo();
				/*
				 * if (null != prevRangeTo && prevRangeTo.intValue() == 0) {
				 * throw new EpException(
				 * "To Value Canot be Zero Must be Greater Then from Value"); }
				 */
				if (prevRangeTo != null) {
					if (criteriaType.equalsIgnoreCase("50")
							|| criteriaType.equalsIgnoreCase("60")) {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(0.01)));
					} else {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(1)));
					}
				}
			}
			checkListScore.add(score);

			this.getManagedBean().setCheckListScore(checkListScore);

		} finally {

		}
	}

	public void addScoringyears(String CriteriaType) {

		try {
			ScQtCheckListScore prevSpecScoring = null;
			List<ScQtCheckListScore> checkListScore = this.getManagedBean()
					.getScoringForYears();
			if (null == checkListScore) {
				checkListScore = new ArrayList<ScQtCheckListScore>();

			}
			if (checkListScore.size() > 0) {
				prevSpecScoring = checkListScore.get(checkListScore.size() - 1);
			}

			ScQtCheckListScore score = createCheckList();
			score.setCriteriaType(new Integer(CriteriaType));
			score.setScoreType(new BigDecimal(this.getManagedBean()
					.getCompanyScoreType()));
			if (prevSpecScoring != null) {
				BigDecimal prevRangeTo = prevSpecScoring.getRangeTo();
				/*
				 * if (null != prevRangeTo && prevRangeTo.intValue() == 0) {
				 * throw new EpException(
				 * "To Value Canot be Zero Must be Greater Then from Value"); }
				 */
				if (prevRangeTo != null) {
					score.setRangeFrom(prevRangeTo.add(BigDecimal.valueOf(1)));
				}
			}
			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} finally {

		}
	}

	public void addScheduleDetails() {
		IQTSpecSO specso = getSpecSO();
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		IParameterSO parameterSo = null;
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
					// localSchedule.add(freqSchedule.get(j));
					ScQtSchedule schedule = new ScQtSchedule();
					// schedule.setStartYear(String.valueOf(year));
					// schedule.setEndYear(String.valueOf(year));
					PmParameter parameter = new PmParameter();
					if (null != this.getManagedBean().getFulfilmentType()
							&& !"".equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())
							&& this.getManagedBean()
									.getFulfilmentType()
									.equalsIgnoreCase(
											ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
						parameterSo = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IParameterSO.class);
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						schedule.setScheduleType(docDefTypeResDTO
								.getPmParameter());

					} else {

						schedule.setScheduleType(parameter);
					}
					// schedule.setScheduleType(parameter);
					setUserDetails(schedule);
					// freqSchedule.add(schedule);
					localSchedule.add(schedule);

				}
				this.getManagedBean().getScQt()
						.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
				specReqDTO.setScQt(this.getManagedBean().getScQt());
				specReqDTO.setFullfilementType(this.getManagedBean()
						.getFulfilmentType());
				specso.removefulfillmentDtls(specReqDTO);
				this.getManagedBean().setScheduleList(localSchedule);
			} else {
				for (int i = freqSchedule.size(); i < this.getManagedBean()
						.getTotalFrequency(); i++) {

					ScQtSchedule schedule = new ScQtSchedule();
					// schedule.setStartYear(String.valueOf(year));
					// schedule.setEndYear(String.valueOf(year));
					PmParameter parameter = new PmParameter();
					if (null != this.getManagedBean().getFulfilmentType()
							&& !"".equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())
							&& this.getManagedBean()
									.getFulfilmentType()
									.equalsIgnoreCase(
											ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
						parameterSo = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IParameterSO.class);
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						schedule.setScheduleType(docDefTypeResDTO
								.getPmParameter());

					} else {

						schedule.setScheduleType(parameter);
					}
					setUserDetails(schedule);
					freqSchedule.add(schedule);
				}
				this.getManagedBean().setScheduleList(freqSchedule);
			}

		} finally {

		}
	}

	public void calculateStartMonth(ScQtSchedule schedule) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtSchedule> freqSchedule = this.getManagedBean()
				.getScheduleList();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		if (null != schedule.getStartMonth() && null != schedule.getEndMonth()) {
			int start = Integer.parseInt(schedule.getStartMonth());
			String fromMonth = retriveMonthDtls(start);
			schedule.setEndMonth(schedule.getStartMonth());
			int end = Integer.parseInt(schedule.getEndMonth());
			String toMonth = retriveMonthDtls(end);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String fromdate = null;
			String toDate = null;
			if (null != schedule.getStartYear()
					&& null != schedule.getEndYear()) {
				fromdate = 01 + "/" + fromMonth + "/" + schedule.getStartYear();
				toDate = 31 + "/" + toMonth + "/" + schedule.getEndYear();
			}

			// schedule.setScheduleNo(schedule.getScheduleNo());

			try {

				Date date1;
				Date date2;
				try {
					if (null != fromdate && null != toDate) {
						date1 = format.parse(fromdate);
						date2 = format.parse(toDate);
						long days = DateUtils.getDaysDifference(date1, date2);
						int day = (int) days;
						int months = day / 30;
						schedule.setTotalMonths(new BigDecimal(months));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// schedule.setFromDate(df.parse(fromdate));
				// schedule.setToDate(df.parse(toDate));

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);

				this.getManagedBean().setScheduleList(freqSchedule);

			} finally {

			}
		} else {
			int index = freqSchedule.indexOf(schedule);
			if (null != schedule.getStartMonth()) {
				schedule.setEndMonth(schedule.getStartMonth());
			}
			freqSchedule.set(index, schedule);
		}

	}

	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == progressivepayment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			progressivepayment.setEndMonth(progressivepayment.getStartMonth());
			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

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
		if (null != schedule.getStartMonth() && null != schedule.getEndMonth()) {
			int start = Integer.parseInt(schedule.getStartMonth());
			String fromMonth = retriveMonthDtls(start);
			int end = Integer.parseInt(schedule.getEndMonth());
			String toMonth = retriveMonthDtls(end);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String fromdate = null;
			String toDate = null;
			if (null != schedule.getStartYear()
					&& null != schedule.getEndYear()) {
				fromdate = 01 + "/" + fromMonth + "/" + schedule.getStartYear();
				toDate = 31 + "/" + toMonth + "/" + schedule.getEndYear();
			}

			// schedule.setScheduleNo(schedule.getScheduleNo());

			try {

				Date date1;
				Date date2;
				try {
					if (null != fromdate && null != toDate) {
						date1 = format.parse(fromdate);
						date2 = format.parse(toDate);
						long days = DateUtils.getDaysDifference(date1, date2);
						int day = (int) days;
						int months = day / 30;
						schedule.setTotalMonths(new BigDecimal(months));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// schedule.setFromDate(df.parse(fromdate));
				// schedule.setToDate(df.parse(toDate));

				int index = freqSchedule.indexOf(schedule);
				freqSchedule.set(index, schedule);

				this.getManagedBean().setScheduleList(freqSchedule);

			} finally {

			}
		}

	}

	private String retriveMonthDtls(int Start) {
		String month = "";
		if (Start == 0) {
			month = "01";
		} else if (Start == 1) {
			month = "02";
		} else if (Start == 2) {
			month = "03";
		} else if (Start == 3) {
			month = "04";
		} else if (Start == 4) {
			month = "05";
		} else if (Start == 5) {
			month = "06";
		} else if (Start == 6) {
			month = "07";
		} else if (Start == 7) {
			month = "08";
		} else if (Start == 8) {
			month = "09";
		} else if (Start == 9) {
			month = "10";
		} else if (Start == 10) {
			month = "11";
		} else if (Start == 11) {
			month = "12";
		}
		return month;
	}

	public void addProgressiveDetails() {
		IQTSpecSO specso = getSpecSO();
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
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
					// progressiveList.add(progressive.get(j));
					ScQtProgressivePayment payment = new ScQtProgressivePayment();
					// payment.setStartYear(String.valueOf(year));
					// payment.setEndYear(String.valueOf(year));
					// payment.setScheduleType(new BigDecimal(1));

					setUserDetails(payment);
					progressive.add(payment);
					progressiveList.add(payment);

				}
				this.getManagedBean().getScQt()
						.setScheduleType(SourcingConstants.PROGRESSIVE_PAYMENT);
				specReqDTO.setScQt(this.getManagedBean().getScQt());
				specReqDTO.setFullfilementType(this.getManagedBean()
						.getFulfilmentType());
				specso.removefulfillmentDtls(specReqDTO);

				this.getManagedBean()
						.setProgressivePaymentList(progressiveList);
			} else {
				for (int i = progressive.size(); i < this.getManagedBean()
						.getTotalProgressivePayment(); i++) {

					ScQtProgressivePayment payment = new ScQtProgressivePayment();
					// payment.setStartYear(String.valueOf(year));
					// payment.setEndYear(String.valueOf(year));
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

	public void compareChecklistValueCurrentYear(
			ScQtCheckListValue checkListValue) {
		if (null != checkListValue) {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if (null != checkListValue.getYear()) {
				if (checkListValue.getYear().intValue() > year) {
					throw new EpException(MessageConst.ERROR_760169);
				}
			}
		}

	}

	public void acceptCompanyScoring() {
		try {

			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setTobeRemoveScores(null);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();
			// checkList.setScQtCheckListScores(null);
			String totalNoOfYearsType = this.getManagedBean()
					.getCompanyScoreType();
			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			if (null != checkList && null != checkList.getScQtCheckListValue()) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				if (null != checkList.getScQtCheckListValue().getYear()) {
					if (checkList.getScQtCheckListValue().getYear().intValue() > year) {
						throw new EpException(MessageConst.ERROR_760169);
					}
				}
			}
			if (null == contractScoreType
					|| "".equalsIgnoreCase(contractScoreType)) {
				throw new EpException(MessageConst.ERROR_760190);
			}

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

			} else if (QTConstants.AUTO_SCORE
					.equalsIgnoreCase(contractScoreType)) {
				List<ScQtCheckListScore> checkListScore = this.getManagedBean()
						.getCheckListScore();

				if (null == checkListScore) {
					checkListScore = new ArrayList<ScQtCheckListScore>();
				}
				if (checkListScore.size() < 2) {
					throw new EpException(MessageConst.ERROR_760140);
				}
				List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
				for (int i = 0; i < checkListScore.size(); i++) {
					ScQtCheckListScore Score = checkListScore.get(i);
					if (null == Score.getScore()
							|| Score.getScore().intValue() <= 0) {
						throw new EpException(MessageConst.ERROR_760139);

					}
					if (null != checkList.getPmDocument()
							&& QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
									.equalsIgnoreCase(checkList.getPmDocument()
											.getDocDefinitionKey())) {
						if (null != Score.getRangeTo()
								&& Score.getRangeTo().intValue() > 100) {
							throw new EpException(MessageConst.ERROR_760170);
						}
					}

				}

				for (int i = 0; i < checkListScore.size() - 1; i++) {
					ScQtCheckListScore Score = checkListScore.get(i);
					localScore.add(Score);
				}

				// Collections.sort(localScore);
				if (null != checkListScore) {
					noOfContractScore.addAll(checkListScore);
				}
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
				} else if (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(totalNoOfYearsType)) {
					List<ScQtCheckListScore> scoreYears = this.getManagedBean()
							.getScoringForYears();

					if (null == scoreYears) {
						scoreYears = new ArrayList<ScQtCheckListScore>();
					}
					if (scoreYears.size() >= 1) {

						if (scoreYears.size() < 2) {
							throw new EpException(MessageConst.ERROR_760140);
						}
					}

					for (int i = 0; i < scoreYears.size(); i++) {
						ScQtCheckListScore Score = scoreYears.get(i);
						if (null == Score.getScore()
								|| Score.getScore().intValue() <= 0) {
							throw new EpException(MessageConst.ERROR_760139);

						}
						/*
						 * if (null != Score.getRangeTo() &&
						 * Score.getRangeTo().intValue() > 100) { throw new
						 * EpException(MessageConst.ERROR_760170); }
						 */
					}

					List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
					for (int i = 0; i < scoreYears.size() - 1; i++) {
						ScQtCheckListScore Score = scoreYears.get(i);
						localScore.add(Score);
					}
					// Collections.sort(localScore);
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
					ScQtCheckListScore checkListScore = removedList.get(i);
					if (null != checkListScore) {
						if (null == checkListScore.getScQtCheckListScoreId()) {
							removedList.remove(i);
						}
					}
				}
			}
			if ((null != checkList && null != checkList.getPmDocument())
					&& (QTConstants.TECHNICAL_GOV_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey()) || QTConstants.TECHNICAL_PRIVATE_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey()))) {
				if (((QTConstants.AUTO_SCORE
						.equalsIgnoreCase(totalNoOfYearsType) && QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) || (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType) && (null == totalNoOfYearsType || ""
						.equals(totalNoOfYearsType))))) {
					PmStatus evalStatus = new PmStatus();
					// evalStatus=new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_COMPLETED);
					reqDTO.setEvaluationStatus(evalStatus);
					// checkList.setPmEvalStatus(evalStatus);

				} else {
					PmStatus evalStatus = new PmStatus();
					// evalStatus=new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					reqDTO.setEvaluationStatus(evalStatus);
					// checkList.setPmEvalStatus(evalStatus);
				}

			} else {
				if (QTConstants.AUTO_SCORE.equalsIgnoreCase(contractScoreType)) {
					PmStatus evalStatus = new PmStatus();
					// evalStatus=new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_COMPLETED);
					reqDTO.setEvaluationStatus(evalStatus);
					// checkList.setPmEvalStatus(evalStatus);
				} else {
					PmStatus evalStatus = new PmStatus();
					// evalStatus=new PmStatus();
					evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					reqDTO.setEvaluationStatus(evalStatus);
					// checkList.setPmEvalStatus(evalStatus);
				}

			}
			PmStatus specificationStatus = new PmStatus();
			specificationStatus.setStatusId(QTStatusConst.TS_COMPLETED);
			reqDTO.setSpecificationStatus(specificationStatus);
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
			reqDTO.setTobeRemoveScores(null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] {
											getMessageCommon("lbl.sc.scores"),
											"" },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		} finally {

		}

	}

	public void acceptBankScore() {
		try {
			IQTSpecSO specSo;
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setTobeRemoveScores(null);
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
			} else if (QTConstants.AUTO_SCORE
					.equalsIgnoreCase(contractScoreType)) {
				List<ScQtCheckListScore> checkListScore = this.getManagedBean()
						.getCheckListScore();

				if (null == checkListScore) {
					checkListScore = new ArrayList<ScQtCheckListScore>();
				}
				if (checkListScore.size() < 2) {
					throw new EpException(MessageConst.ERROR_760140);
				}

				for (int i = 0; i < checkListScore.size(); i++) {
					ScQtCheckListScore Score = checkListScore.get(i);
					if (null == Score.getScore()
							|| Score.getScore().intValue() <= 0) {
						throw new EpException(MessageConst.ERROR_760139);

					}
					/*
					 * if (null != Score.getRangeTo() &&
					 * Score.getRangeTo().intValue() > 100) { throw new
					 * EpException(MessageConst.ERROR_760170); }
					 */

				}
				List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
				for (int i = 0; i < checkListScore.size() - 1; i++) {
					ScQtCheckListScore Score = checkListScore.get(i);
					localScore.add(Score);
				}

				if (null != checkListScore) {
					noOfContractScore.addAll(checkListScore);
				}
			}
			String Startmonth = "";
			String endmonth = "";
			if (this.getManagedBean().getFromMonth().equalsIgnoreCase("0")) {
				Startmonth = "01";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("1")) {
				Startmonth = "02";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("2")) {
				Startmonth = "03";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("3")) {
				Startmonth = "04";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("4")) {
				Startmonth = "05";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("5")) {
				Startmonth = "06";
			}

			else if (this.getManagedBean().getFromMonth().equalsIgnoreCase("6")) {
				Startmonth = "07";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("7")) {
				Startmonth = "08";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("8")) {
				Startmonth = "09";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("9")) {
				Startmonth = "10";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("10")) {
				Startmonth = "11";
			} else if (this.getManagedBean().getFromMonth()
					.equalsIgnoreCase("11")) {
				Startmonth = "12";
			}
			if (this.getManagedBean().getToMonth().equalsIgnoreCase("0")) {
				endmonth = "01";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("1")) {
				endmonth = "02";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("2")) {
				endmonth = "03";
			}

			else if (this.getManagedBean().getToMonth().equalsIgnoreCase("3")) {
				endmonth = "04";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("4")) {
				endmonth = "05";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("5")) {
				endmonth = "06";
			}

			else if (this.getManagedBean().getToMonth().equalsIgnoreCase("6")) {
				endmonth = "07";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("7")) {
				endmonth = "08";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("8")) {
				endmonth = "09";
			} else if (this.getManagedBean().getToMonth().equalsIgnoreCase("9")) {
				endmonth = "10";
			} else if (this.getManagedBean().getToMonth()
					.equalsIgnoreCase("10")) {
				endmonth = "11";
			} else if (this.getManagedBean().getToMonth()
					.equalsIgnoreCase("11")) {
				endmonth = "12";
			} else if (this.getManagedBean().getToMonth()
					.equalsIgnoreCase("12")) {
				endmonth = "01";
			} else if (this.getManagedBean().getToMonth()
					.equalsIgnoreCase("13")) {
				endmonth = "02";
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
				// checkList.setScQt(this.getManagedBean().getScQt());
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
					ScQtCheckListScore checkListScore = removedList.get(i);
					if (null != checkListScore) {
						if (null == checkListScore.getScQtCheckListScoreId()) {
							removedList.remove(i);
						}
					}
				}
			}
			if (null != checkList.getScQtCheckListValue()) {
				if (null == checkList.getScQtCheckListValue().getFromDate()
						|| null == checkList.getScQtCheckListValue()
								.getToDate()) {
					throw new EpException(MessageConst.ERROR_760222);
					// checkList.getScQtCheckListValue().setFromDate(new
					// Date());
					// checkList.getScQtCheckListValue().setToDate(
					// DateUtils.addDays(new Date(), 90));
				}
			}
			if (QTConstants.AUTO_SCORE.equalsIgnoreCase(contractScoreType)) {
				PmStatus evalStatus = new PmStatus();
				// evalStatus=new PmStatus();
				evalStatus.setStatusId(QTStatusConst.TS_COMPLETED);
				reqDTO.setEvaluationStatus(evalStatus);
				// checkList.setPmEvalStatus(evalStatus);
			} else {
				PmStatus evalStatus = new PmStatus();
				// evalStatus = new PmStatus();
				evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				reqDTO.setEvaluationStatus(evalStatus);
				// checkList.setPmEvalStatus(evalStatus);
			}

			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			PmStatus specificationStatus = new PmStatus();
			specificationStatus.setStatusId(QTStatusConst.TS_COMPLETED);
			reqDTO.setSpecificationStatus(specificationStatus);
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
			reqDTO.setTobeRemoveScores(null);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] {
											getMessageCommon("lbl.sc.scores"),
											"" },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} finally {

		}

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

	public List<SelectItem> getScoreDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, "--Select One--"));
			selectItems.add(new SelectItem(1, "Auto Score"));
			selectItems.add(new SelectItem(2, "User Score"));

			this.getManagedBean().setScoreTypeList(selectItems);
		} finally {

		}
		return this.getManagedBean().getScoreTypeList();
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {

		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();

		/*
		 * FacesContext.getCurrentInstance().addMessage( null, new
		 * FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
		 * .getMessage(MessageConst.INFO_700003, new String[] {
		 * getMessageCommon("lbl.sc.file") },
		 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
		 * FacesScopes().getUserLocale())));
		 */

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

		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			RequestContext.getCurrentInstance().execute(
					"reviceApprovalDlgs.show()");
		} else if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)) {
			spReqDTO = prepareSpecReq();
			spReqDTO.setOutcome(action);
			spReqDTO.setTaskId(this.getManagedBean().getTaskId());
			spReqDTO.setPayLoad(this.getManagedBean()
					.getSpecificationDataType());
			FacesScopes facesScopes = new FacesScopes();
			String ctx = facesScopes.getBPMAuthenticationCtxToken();
			spReqDTO.setPmUser(facesScopes.getUserSessionBean());
			spReqDTO.setRoleCode(getUserRoleCode());
			spReqDTO.setTaskName(this.getManagedBean().getTaskName());
			spReqDTO.setToken(ctx);
			IQTSpecSO specSO = getSpecSO();
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();

				if (qt.getMinistrySpecSecrataryId() == null) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_700005,
															new String[] {
																	getMessageCommon("lbl.sc.specification.for"),
																	qt.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_700018,
															new String[] {
																	getMessageCommon("lbl.sc.specification.for"),
																	qt.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
				}

				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
			/*
			 * spReqDTO.setOutcome(action);
			 * spReqDTO.setTaskId(this.getManagedBean().getTaskId());
			 * spReqDTO.setPayLoad
			 * (this.getManagedBean().getSpecificationDataType()); FacesScopes
			 * facesScopes = new FacesScopes(); String ctx =
			 * facesScopes.getBPMAuthenticationCtxToken();
			 * spReqDTO.setToken(ctx); IQTSpecSO specSO = getSpecSO();
			 * 
			 * try { SpecificationResDTO resDTO =
			 * specSO.updateBPMAction(spReqDTO); if (resDTO.isSuccessful()) {
			 * ScQt qt = resDTO.getScQt(); // Code to renew the session Object..
			 * renewSesionObject();
			 * FacesContext.getCurrentInstance().addMessage( null, new
			 * FacesMessage(FacesMessage.SEVERITY_INFO, null,
			 * MessageUtils.getMessage( getMessgeConstant(action), new String[]
			 * { getMessageCommon("lbl.sc.specification.for"), qt.getQtNo() },
			 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
			 * FacesScopes().getUserLocale())));
			 * 
			 * return CommonUIConst.ACKNOWLEDGE_PAGE;
			 * 
			 * } } finally { specSO = null; }
			 */

		}
		return null;
	}

	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}

	public void paneltabChange(TabChangeEvent event) {

	}

	public void retriveZonesByCircularNo() {
		IPanelSupplierSO panelSupplierSO;
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
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
			/*
			 * this.getManagedBean().setCtZoneSupplierList( new
			 * ArrayList<CtPanelSupplier>());
			 * getManagedBean().setCtZoneSuppliers(new ArrayList<TreeNode>());
			 * this.getManagedBean().setCtPanelZones(new
			 * ArrayList<CtPanelZone>());
			 */

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {

				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {
					/* this.getManagedBean().setCtzones(true); */
					/*
					 * getManagedBean().setCtZoneSuppliers( new
					 * ArrayList<TreeNode>());
					 */
					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getZoneGeneralTrees()
							&& getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper
									.getStateFromCtPanelZone(panelZones
											.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper
									.getDistrictFromCtPanelZone(panelZones
											.getPanelZoneDtls());

							getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton
											.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													false, TplSpecZoneLoc.class));
							/*
							 * getManagedBean() .getZoneGeneralTrees()
							 * .add(LocationSingleton .getInstance()
							 * .constuctPanelTreeNodes( panelZones, panelZones
							 * .getPanelZoneDtls()));
							 */
						}
					}

				}
			} else {
				if (null != this.getManagedBean().getCtPanelItem()
						&& null != this.getManagedBean().getCtPanelItem()
								.getPanelItemId()) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					PanelSupplierReqDTO supplierreqDTO = new PanelSupplierReqDTO();
					supplierreqDTO.setPanelItemId(this.getManagedBean()
							.getCtPanelItem().getPanelItemId());
					supplierreqDTO.setFetchSubEntity(true);
					PanelSupplierResDTO panelSupplierResDTO = panelSupplierSO
							.retrievePanelItem(supplierreqDTO);
					this.getManagedBean().setTotalSuppliers(0);
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != panelSupplierResDTO.getCtPanelItem()) {
						CtPanelItem panelItem = panelSupplierResDTO
								.getCtPanelItem();
						if (null != panelItem.getCtPanelSuppliers()) {
							this.getManagedBean().setTotalSuppliers(
									panelItem.getCtPanelSuppliers().size());

							this.getManagedBean().setCtZoneSupplierList(
									panelItem.getCtPanelSuppliers());
						}

					}
				}

			}
			List<CtPanelItem> itemList = this.getManagedBean()
					.getSelectpanelItem();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());

			this.getManagedBean().setSelectpanelItem(itemList);

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
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
		TplSpecZoneDtl ctPanelzone = (TplSpecZoneDtl) event.getTreeNode()
				.getData();

		PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();
		reqDTO.setCircularZoneId(ctPanelzone.getPanelZone().getPanelZoneId());
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
			List<CtPanelItem> itemList = this.getManagedBean()
					.getSelectpanelItem();
			itemList = new ArrayList<CtPanelItem>();
			itemList.add(this.getManagedBean().getCtPanelItem());

			this.getManagedBean().setSelectpanelItem(itemList);
		}

	}

	public void unSelectPanelYesTreenode(NodeUnselectEvent event) {
		List<CtPanelItem> itemList = this.getManagedBean().getSelectpanelItem();
		itemList = new ArrayList<CtPanelItem>();
		itemList.add(this.getManagedBean().getCtPanelItem());

		this.getManagedBean().setSelectpanelItem(itemList);
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
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
				getManagedBean().setSupplierContact("");
				getManagedBean().setSupplierEmail("");
				if (null != supplierHQPersonnel) {
					getManagedBean().setSupplierContact(
							supplierHQPersonnel.getName());
					getManagedBean().setSupplierEmail(
							supplierHQPersonnel.getEmail());
				}
				// SmAppl appll= supplierHQPersonnel.getSmAppl();

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();

				// System.out.println("INcomeTax No : " +
				// smCompanyBasic.getIncomeTaxNo());

				if (null != smCompanyBasic) {
					if (null != smCompanyBasic.getSmCompanyAddresses()) {
						for (SmCompanyAddress smCompanyAddress : smCompanyBasic
								.getSmCompanyAddresses()) {
							if (null != smCompanyAddress) {
								if (AddressType.CONTACT_ADDRESS.value().equals(
										smCompanyAddress.getAddressType())) {

									getManagedBean().setSupplierBranch("HQ");

									getManagedBean().setSupplierAddress(
											smCompanyAddress.getSmAddress());

									break;

								}
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
		List<ScQtCheckList> checklistRemoveList = this.getManagedBean()
				.getChecklistRemoveList();
		if (null == checklistRemoveList) {
			checklistRemoveList = new ArrayList<ScQtCheckList>();
		}
		checklistRemoveList.addAll(selects2Tenical);
		if (s2Technical.size() == selects2Tenical.size()) {
			s2Technical.clear();
		} else {
			for (ScQtCheckList checkList : selects2Tenical) {
				checkList.setDeleted(true);
				s2Technical.remove(checkList);
			}
		}
		this.getManagedBean().setChecklistRemoveList(checklistRemoveList);
	}

	public void deleteS2Fin() {
		List<ScQtCheckList> s2Fin = getManagedBean()
				.getScQtCheckSt2financeProposalList();
		List<ScQtCheckList> selects2Fin = getManagedBean().getS2FinSelect();
		List<ScQtCheckList> checklistRemoveList = this.getManagedBean()
				.getChecklistRemoveList();
		if (null == checklistRemoveList) {
			checklistRemoveList = new ArrayList<ScQtCheckList>();
		}
		checklistRemoveList.addAll(selects2Fin);
		if (s2Fin.size() == selects2Fin.size()) {
			s2Fin.clear();
		} else {
			for (ScQtCheckList checkList : selects2Fin) {
				checkList.setDeleted(true);
				s2Fin.remove(checkList);
			}
		}
		this.getManagedBean().setChecklistRemoveList(checklistRemoveList);

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
	public void circularListSelect(CtCircularVer circular) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			// CtCircularVer circular = (CtCircularVer) selectEvent.getObject();
			this.getManagedBean().setCtCircularVer(circular);

			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICircularDetailSO.class);
			if (null != circular) {
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

				this.getManagedBean().setPanelItemList(
						resDTO.getPanelItemList());
			}

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
					.getTechnicalScoring();
			if (weightageList == null) {
				weightageList = new ArrayList<ScQtCheckList>();
			}
			for (int i = 0; i < weightageList.size(); i++) {
				ScQtCheckList weight = (ScQtCheckList) weightageList.get(i);
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setTechnicalWeightageScore(
						new BigDecimal(total));
				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}
				// BigDecimal b1 = new BigDecimal(total);
				// BigDecimal techWeightage = new BigDecimal(100);
				// MathContext mc = new MathContext(4);
				// BigDecimal techWeightage1 = b1.divide(techWeightage, mc);
				/*
				 * this.getManagedBean().setTechnicalWeightageScore( new
				 * BigDecimal(total));
				 */
			}
		} finally {

		}

	}

	public void techScoringCheckList(ScQtCheckList checkList) {
		techScoreSum();
		techWeightageSum();
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null == technicalCheckList) {
			technicalCheckList = new ArrayList<ScQtCheckList>();
		}
		for (int i = 0; i < technicalCheckList.size(); i++) {
			if (checkList.getTitle().equalsIgnoreCase(
					technicalCheckList.get(i).getTitle())) {
				ScQtCheckList localCheckList = technicalCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				technicalCheckList.remove(localCheckList);
				technicalCheckList.add(checkList);
				break;
			}
		}
	}

	public void finScoringCheckList(ScQtCheckList checkList) {
		finScoreSum();
		finWeightageSum();
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList) {
			financialCheckList = new ArrayList<ScQtCheckList>();
		}
		for (int i = 0; i < financialCheckList.size(); i++) {
			if (checkList.getTitle().equalsIgnoreCase(
					financialCheckList.get(i).getTitle())) {
				ScQtCheckList localCheckList = financialCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				financialCheckList.remove(localCheckList);
				financialCheckList.add(checkList);
				break;
			}
		}
	}

	// Technical Scoring sum
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
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getScore()
									.intValue();
				}
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

				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getScore()
									.intValue();
				}
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setFinPoints(techScore.intValue());
			}
		} finally {

		}

	}

	// Financial Weightage Calculation
	public void financialWeightageCalculate() {
		if (null != this.getManagedBean().getFinancialScoreSetting()
				&& this.getManagedBean().getFinancialScoreSetting()
						.isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getFinancialScoring();
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();
			int financialWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (null != checklistlocal.getQtScoreWeightage()) {
					map.put(checklistlocal.getQtScoreWeightage()
							.getQtScoreWeightageId(), checklistlocal
							.getQtScoreWeightage());
				}
			}
			List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
					map.values());
			if (null != list && !list.isEmpty()) {
				for (ScQtScoreWeightage weightage : list) {
					if (null == weightage.getWeightage()) {
						weightage.setWeightage(new BigDecimal(0));
					}
					financialWeightage = financialWeightage
							+ weightage.getWeightage().intValue();
				}
				if (financialWeightage < 100) {
					if (this.getManagedBean().getScQt().getEvaluationType()
							.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
							&& !this.getManagedBean().getScQt()
									.isTechEvaluationReq()) {
						this.getManagedBean().setActiveIndex(4);
					} else {
						this.getManagedBean().setActiveIndex(5);
					}
					throw new EpException(MessageConst.ERROR_760221);
				}
			}
		}

	}

	public void technicalWeightageCalculate() {
		if (null != this.getManagedBean().getTechnicalScoreSetting()
				&& this.getManagedBean().getTechnicalScoreSetting()
						.isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getTechnicalScoring();
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			Map<Long, ScQtScoreWeightage> map = new HashMap<Long, ScQtScoreWeightage>();
			int financialWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (null != checklistlocal.getQtScoreWeightage()) {
					map.put(checklistlocal.getQtScoreWeightage()
							.getQtScoreWeightageId(), checklistlocal
							.getQtScoreWeightage());
				}
			}
			List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
					map.values());
			if (null != list && !list.isEmpty()) {
				for (ScQtScoreWeightage weightage : list) {
					if (null == weightage.getWeightage()) {
						weightage.setWeightage(new BigDecimal(0));
					}
					financialWeightage = financialWeightage
							+ weightage.getWeightage().intValue();
				}
				if (financialWeightage < 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760220);
				}
			}
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
				if (null != weight.getQtScoreWeightage()
						&& weight.getQtScoreWeightage().getWeightage() == null) {
					weight.getQtScoreWeightage()
							.setWeightage(new BigDecimal(0));
				}
				if (null != weight.getQtScoreWeightage()) {
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setFinancialWeightageScore(
						new BigDecimal(total));

				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}
				// BigDecimal b1 = new BigDecimal(total);
				// BigDecimal techWeightage = new BigDecimal(100);
				// MathContext mc = new MathContext(4);
				// BigDecimal techWeightage1 = b1.divide(techWeightage, mc);

			}
		} finally {

		}

	}

	public void specOfferpricePackageFromVarieance(
			ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getFromVariance()
					|| offerPrice.getFromVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760224);
			}

		}

	}

	public void specOfferpricePackageToVarieance(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getToVariance()
					|| offerPrice.getToVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760225);
			}

		}

	}

	public void specOfferpricePackageBelowScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getBelowScore()
					|| offerPrice.getBelowScore().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760216);
			}

		}

	}

	public void specOfferpricePackageAboveScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {

			if (null == offerPrice.getAboveScore()
					|| offerPrice.getAboveScore().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760217);
			}
		}

	}

	public void checkTechnicalPassingScore(
			ScQtScoreSetting technicalScoreSetting) {
		if (null != technicalScoreSetting) {
			if (this.getManagedBean().getScQt().getEvaluationType()
					.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)) {
				if (null == technicalScoreSetting.getPassingScore()
						|| technicalScoreSetting.getPassingScore().intValue() < 0) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760172);
				} else if (null != technicalScoreSetting.getPassingScore()
						&& technicalScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760142);
				}
			} else {
				if (null == technicalScoreSetting.getPassingScore()
						|| technicalScoreSetting.getPassingScore().intValue() <= 0) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760234);
				} else if (null != technicalScoreSetting.getPassingScore()
						&& technicalScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760142);
				}
			}
		}

	}

	public void checkFinancialPassingScore(
			ScQtScoreSetting financialScoreSetting) {
		if (null != financialScoreSetting) {

			if (null == financialScoreSetting.getPassingScore()
					|| financialScoreSetting.getPassingScore().intValue() < 0) {
				if (this.getManagedBean().getScQt().getEvaluationType()
						.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					this.getManagedBean().setActiveIndex(4);
				} else {
					this.getManagedBean().setActiveIndex(5);
				}
				throw new EpException(MessageConst.ERROR_760174);
			} else if (null != financialScoreSetting.getPassingScore()
					&& financialScoreSetting.getPassingScore().intValue() > 100) {
				if (this.getManagedBean().getScQt().getEvaluationType()
						.equalsIgnoreCase(QTConstants.EVALUATION_ONE_TIER)
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					this.getManagedBean().setActiveIndex(4);
				} else {
					this.getManagedBean().setActiveIndex(5);
				}
				throw new EpException(MessageConst.ERROR_760143);
			}
		}

	}

	public void checkDemoPassingScore(ScQtScoreSetting demoScoreSetting) {
		if (null != demoScoreSetting) {

			if (null == demoScoreSetting.getPassingScore()
					|| demoScoreSetting.getPassingScore().intValue() < 0) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760173);
			} else if (null != demoScoreSetting.getPassingScore()
					&& demoScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760144);
			}
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
		this.getManagedBean().setCircularVer(null);
		// this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		// this.getManagedBean().setCtPanelItem(null);
		// this.getManagedBean().setCircularExpDate(new Date());
		// this.getManagedBean().setCirCularNo(null);
		// this.getManagedBean().setCircularName(null);
		// this.getManagedBean().setCircularType(null);

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

				if (null != getManagedBean().getCtSearchBean().getCirCularNo()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCirCularNo())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("circularNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCirCularNo(), true));

				}

				if (null != getManagedBean().getCtSearchBean()
						.getCircularName()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getCircularName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("name",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getCircularName(), true));
				}

				if (null != getManagedBean().getCtSearchBean().getItemName()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getItemName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("ctPanelItems.itemName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getCtSearchBean()
											.getItemName(), true));
				}
				if (null != getManagedBean().getCtSearchBean().getItemName()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getCtSearchBean().getItemName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("ctPanelItems.qt",
									SearchOperator.OPERATOR_LIKE.value(), 1));
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
		getManagedBean().setQtTitle(null);
		getManagedBean().setPublishDate(null);
	}

	// Template Spec Paging Data Model
	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<TplSpecMaster> {
		private static final long serialVersionUID = 1L;

		public CreateTemplateSpecPagingDataModel() {

			super("createList", false);

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

				} else if (TemplateSpecConst.PROCESS_TYPE_SOURCING
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					if (null == getManagedBean().getQtNum()
							|| "".equalsIgnoreCase(getManagedBean().getQtNum())) {
						getManagedBean().setQtNum("QT");
					}

				}
				if (null != getManagedBean().getSpecName()
						&& !"".equalsIgnoreCase(getManagedBean().getSpecName())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("specName",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getSpecName(), true));

				}

				if (null != getManagedBean().getStandardTemplate()
						&& !"".equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("processType",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getStandardTemplate(),
									true));
				}

				if (null != getManagedBean().getSearchItemTypeId()) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					itemSearchBean.setSearchField("itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (null != getManagedBean().getSearchProcurementId()) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					procurementSearchBean
							.setSearchField("procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}
				if (null != getManagedBean().getQtNum()
						&& !"".equalsIgnoreCase(getManagedBean().getQtNum())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtDoc.scQt.qtNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getQtNum(), true));
				}
				if (null != specTypeCode) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_LIKE.value());
					qtnoSearchBean.setSearchField("specTypeCode.parameterCode");
					qtnoSearchBean.setSearchValue(specTypeCode);

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}

				if (null != getManagedBean().getQtTitle()
						&& !"".equalsIgnoreCase(getManagedBean().getQtTitle())) {

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtDoc.scQt.qtTitle",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getQtTitle(), true));
				}

				if (null != getManagedBean().getPublishDate()) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtDoc.scQt.publishDate",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getPublishDate()));

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
				if (null != getManagedBean().getStateCode()
						&& !"".equalsIgnoreCase(getManagedBean().getStateCode())) {
					SearchFilterBean stateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					stateSearchBean
							.setSearchField("pmAddress.pmState.stateCode");
					stateSearchBean.setSearchValue(getManagedBean()
							.getStateCode());
					searchBean.getDataFilterBeans().add(stateSearchBean);
				}

				if (null != getManagedBean().getDistrictCode()
						&& !"".equals(getManagedBean().getDistrictCode())) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDistrict.districtCode");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDistrictCode());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}

				if (null != getManagedBean().getDivisionCode()
						&& !"".equals(getManagedBean().getDivisionCode())) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDivision.divisionCode");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDivisionCode());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}

				if (null != getManagedBean().getMinistryId()) {
					SearchFilterBean ministrySearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					ministrySearchBean
							.setSearchField("pmOrgProfile.parentOrgProfile.orgProfileId");
					ministrySearchBean.setSearchValue(getManagedBean()
							.getMinistryId());
					searchBean.getDataFilterBeans().add(ministrySearchBean);
				}

				if (null != getManagedBean().getPtjId()) {
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

		SCQuotationTenderDataType quotationTenderDataType = getBpmTaskDetails(SCQuotationTenderDataType.class);

		this.getManagedBean().setSpecificationDataType(quotationTenderDataType);
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		if (SCProcessNameConst.PREPARE_SPECIFICATION.equalsIgnoreCase(this
				.getManagedBean().getTaskName())
				|| SCProcessNameConst.CONSOLIDATE_SPECIFICATION
						.equalsIgnoreCase(this.getManagedBean().getTaskName())) {
			PmUser curLogedInUser = facesScopes.getUserSessionBean();
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setQtId(quotationTenderDataType.getQtId()
					.longValue());
			committeReqDTO.setUser(curLogedInUser);
			committeReqDTO
					.setCommitteeType(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);
			CommitteResDTO resDTO = committeSO
					.retrieveCommitteMemberByUserId(committeReqDTO);
			ScCommitteeMember member = resDTO.getCommMember();
			if (!member.isConsolidate()) {
				this.getManagedBean().setReadOnly(true);
			} else {
				this.getManagedBean().setReadOnly(false);
			}
		}
		if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			this.getManagedBean().setReadOnly(true);

		}

		if (SCProcessNameConst.PREPARE_SPECIFICATION.equalsIgnoreCase(this
				.getManagedBean().getTaskName())
				|| SCProcessNameConst.CONSOLIDATE_SPECIFICATION
						.equalsIgnoreCase(this.getManagedBean().getTaskName())
				|| SCProcessNameConst.SPECIFICATION_APPROVAL
						.equalsIgnoreCase(this.getManagedBean().getTaskName())) {
			retriveSpecdetails(quotationTenderDataType.getQtId().longValue(),
					getDocTypeByTaskName(this.getManagedBean().getTaskName()));

			if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				loadPrepareSpecDetails(quotationTenderDataType.getQtId()
						.longValue(), getDocTypeByTaskName(this
						.getManagedBean().getTaskName()));
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

			PmUser curLogedInUser = facesScopes.getUserSessionBean();
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setQtId(quotationTenderDataType.getQtId()
					.longValue());
			committeReqDTO.setUser(curLogedInUser);
			committeReqDTO
					.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
			CommitteResDTO resDTO = committeSO
					.retrieveCommitteMemberByUserId(committeReqDTO);
			ScCommitteeMember member = resDTO.getCommMember();
			if (null != member) {

				if (member.getMemberRole().getParameterCode()
						.contains(SourcingConstants.MEMBER_ROLE_SECRETARY)) {
					this.getManagedBean().setReadOnly(false);

				} else {

					this.getManagedBean().setReadOnly(true);
				}
			}
			retriveSpecdetails(quotationTenderDataType.getQtId().longValue(),
					getDocTypeByTaskName(this.getManagedBean().getTaskName()));
			if (null == this.getManagedBean().getScQt().getFulfilmentType()) {

				this.getManagedBean().getScQt()
						.setFulfilmentType(new PmParameter());

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
		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN)) {
			scQt.setScheduleType(SourcingConstants.PROGRESSIVE_PAYMENT);
		}
		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			scQt.setScheduleType(SourcingConstants.FREQUENCY_SCHEDULE);
		}

		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();

		List<ScQtCheckList> technicalScoring = this.getManagedBean()
				.getTechnicalScoring();
		if (technicalScoring != null && !technicalScoring.isEmpty()) {
			// List<ScQtCheckList> localTechScoring=techCheckList;
			// techCheckList=null;
			/*
			 * for (ScQtCheckList scoringCheckList : technicalScoring) { for
			 * (ScQtCheckList technicalCheckList : techCheckList) { if
			 * (technicalCheckList.getTitle().equalsIgnoreCase(
			 * scoringCheckList.getTitle())) { if (null !=
			 * scoringCheckList.getQtScoreWeightage() &&
			 * scoringCheckList.getQtScoreWeightage() .getScore().intValue() >
			 * 0) { technicalCheckList .setQtScoreWeightage(scoringCheckList
			 * .getQtScoreWeightage()); } } }
			 * 
			 * }
			 */
			techWeightageSum();

			// localChecklist.addAll(technicalScoring);
			// scQt.setScQtChecklists(localChecklist);
		}
		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();

		if (financialScoring != null && !financialScoring.isEmpty()) {
			ScQtCheckList finalFinSpec = null;
			finWeightageSum();

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
				// financialScoring.remove(0);

				// localChecklist.addAll(finSpecs);
				for (ScQtCheckList financialSpecs : finSpecs) {
					for (ScQtCheckList financialList : financialCheckList) {
						if (financialList.getTitle().equalsIgnoreCase(
								financialSpecs.getTitle())
								&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
										.equalsIgnoreCase(financialSpecs
												.getDefinationType()
												.getParameterCode())) {
							if (null != financialSpecs.getQtScoreWeightage()
									&& financialSpecs.getQtScoreWeightage()
											.getScore().intValue() > 0) {
								financialList
										.setQtScoreWeightage(financialSpecs
												.getQtScoreWeightage());
							}
						}
					}

				}
			}

			/*
			 * for (ScQtCheckList financialSpecs : financialScoring) { for
			 * (ScQtCheckList financialList : financialCheckList) { if
			 * (financialList.getTitle().equalsIgnoreCase(
			 * financialSpecs.getTitle()) &&
			 * !ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
			 * .equalsIgnoreCase(financialSpecs .getDefinationType()
			 * .getParameterCode())) { if (null !=
			 * financialSpecs.getQtScoreWeightage() &&
			 * financialSpecs.getQtScoreWeightage() .getScore().intValue() > 0)
			 * { financialList.setQtScoreWeightage(financialSpecs
			 * .getQtScoreWeightage()); } } }
			 * 
			 * }
			 */
			// localChecklist.addAll(financialScoring);
			// scQt.setScQtChecklists(localChecklist);
		}

		if (techCheckList != null && !techCheckList.isEmpty()) {
			for (ScQtCheckList checkList : techCheckList) {
				if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(checkList
						.getDefinationType().getParameterCode())) {
					scQt.setDemoRequired(true);
				} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					scQt.setSampleReq(true);

				}

			}
			localChecklist.addAll(techCheckList);
			scQt.setScQtChecklists(localChecklist);
		}

		if (financialCheckList != null && !financialCheckList.isEmpty()) {
			localChecklist.addAll(financialCheckList);
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

				}
				scQt.setScQtSchedules(localScheduleList);
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
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getScQt());
					paymentList.add(schedule);

				}
				scQt.setScQtProgressivePayments(paymentList);
			} else {
				scQt.setScQtProgressivePayments(null);
			}
		}

		List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
		ScQtScoreSetting technicalScoreSetting = this.getManagedBean()
				.getTechnicalScoreSetting();
		if (null != technicalScoreSetting
				&& technicalScoreSetting.getSettingType() == 1) {

			if (!this.getManagedBean().isReadOnly()) {

				if (null == technicalScoreSetting.getPassingScore()) {
					if (scQt.getEvaluationType().equalsIgnoreCase(
							QTConstants.EVALUATION_TWO_TIER)) {
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760192);
					} else {
						technicalScoreSetting
								.setPassingScore(new BigDecimal(0));
					}
				} else {
					if (scQt.getEvaluationType().equalsIgnoreCase(
							QTConstants.EVALUATION_TWO_TIER)) {
						if (technicalScoreSetting.getPassingScore().intValue() <= 0) {
							this.getManagedBean().setActiveIndex(4);
							throw new EpException(MessageConst.ERROR_760234);
						}

					} else {

						if (technicalScoreSetting.getPassingScore().intValue() < 0) {
							this.getManagedBean().setActiveIndex(4);
							throw new EpException(MessageConst.ERROR_760172);
						}
					}

				}

			}

			if (null != technicalScoreSetting.getPassingScore()
					&& technicalScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760142);
			}
			scoreSetting.add(technicalScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting financialScoreSetting = this.getManagedBean()
				.getFinancialScoreSetting();
		if (null != financialScoreSetting
				&& financialScoreSetting.getSettingType() == 2) {

			if (!this.getManagedBean().isReadOnly()) {

				if (null == financialScoreSetting.getPassingScore()) {
					if (scQt.getEvaluationType().equalsIgnoreCase(
							QTConstants.EVALUATION_TWO_TIER)) {
						this.getManagedBean().setActiveIndex(5);
						throw new EpException(MessageConst.ERROR_760193);
					} else {
						financialScoreSetting
								.setPassingScore(new BigDecimal(0));
					}
				} else {

					if (financialScoreSetting.getPassingScore().intValue() < 0) {
						if (scQt.getEvaluationType().equalsIgnoreCase(
								QTConstants.EVALUATION_ONE_TIER)
								&& !scQt.isTechEvaluationReq()) {
							this.getManagedBean().setActiveIndex(4);
						} else {
							this.getManagedBean().setActiveIndex(5);
						}
						throw new EpException(MessageConst.ERROR_760174);
					}

				}

			}

			if (null != financialScoreSetting.getPassingScore()
					&& financialScoreSetting.getPassingScore().intValue() > 100) {
				if (scQt.getEvaluationType().equalsIgnoreCase(
						QTConstants.EVALUATION_ONE_TIER)
						&& scQt.isTechEvaluationReq()) {
					this.getManagedBean().setActiveIndex(4);
				} else {
					this.getManagedBean().setActiveIndex(5);
				}
				throw new EpException(MessageConst.ERROR_760143);
			}
			scoreSetting.add(financialScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting demoScoreSetting = this.getManagedBean()
				.getDemoScoreSetting();
		if (null != demoScoreSetting && demoScoreSetting.getSettingType() == 3) {
			if (scQt.isDemoRequired()
					&& scQt.getEvaluationType().equalsIgnoreCase(
							QTConstants.EVALUATION_TWO_TIER)
					&& !this.getManagedBean().isReadOnly()) {
				if (null == demoScoreSetting.getPassingScore()) {
					demoScoreSetting.setPassingScore(new BigDecimal(0));
				}
				if (demoScoreSetting.getPassingScore().intValue() < 0) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760173);
				}
				if (null != demoScoreSetting.getPassingScore()
						&& demoScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760144);
				}
			}
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
		if (null != this.getManagedBean().getCurrentStatusId()
				&& !this.getManagedBean().getCurrentStatusId().equals(60401L)) {
			List<ScQtSpecificationRemark> remarks = this.getManagedBean()
					.getSpecificationRemarks();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						remarks.remove(i);
					}
				}
			}
		}
		if (null != localChecklist && localChecklist.size() > 0) {
			for (ScQtCheckList checkList : scQt.getScQtChecklists()) {
				if (null != checkList.getQtScoreWeightage()
						&& null != checkList.getQtScoreWeightage()
								.getQtScoreWeightageId()) {
					log.info("QtId"
							+ checkList.getScQt().getQtId()
							+ "CheckListTitle"
							+ checkList.getTitle()
							+ "weightageId"
							+ checkList.getQtScoreWeightage()
									.getQtScoreWeightageId()
							+ "qtScoreweightage"
							+ checkList.getQtScoreWeightage());
				}
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

	private List<ScQtCheckList> getToBeRemoveList() {
		List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techList = getManagedBean()
				.getMarkDeletedTechChecklist();
		if (techList != null && techList.size() > 0) {
			toBeRemoveList.addAll(techList);
		}

		List<ScQtCheckList> finList = getManagedBean()
				.getMarkDeletedFinChecklist();
		if (finList != null && finList.size() > 0) {
			toBeRemoveList.addAll(finList);
		}

		return toBeRemoveList;
	}

	// Prepare Qt Checklist Save related method
	public void validatePrepareQtChecklist() {
		try {
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			int activeIndex = this.getManagedBean().getActiveIndex();

			SpecificationResDTO resDTO = null;
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long statusId = this.getManagedBean().getCurrentStatusId();
			String taskName = this.getManagedBean().getTaskName();
			boolean readonly = this.getManagedBean().isReadOnly();
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
				for (int i = 0; i < deliveryRemoveList.size(); i++) {
					if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
						deliveryRemoveList.remove(i);
					}
				}

			}

			List<ScQtCheckList> checklistRemoveList = this.getManagedBean()
					.getChecklistRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					}
				}

			}
			specReqDTO.setTobeRemoveList(checklistRemoveList);
			specReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);
			switch (activeIndex) {
			case 0:
				ScQt scQt = constructSpecCommitteDtls().getScQt();
				if (this.getManagedBean().isReadOnly()) {
					if ((null != scQt.getFulfilmentType() && null == scQt
							.getFulfilmentType().getParameterId())
							|| (null == scQt.getContractDuration())) {
						throw new EpException(MessageConst.ERROR_760134);

					}
				}
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
				retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
				retriveBusinessOwnerQualificationCriteri("SC");

				break;
			case 2:

				ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(scQtTechChecklist);
				specSO.validateCheckList(specReqDTO);
				if (QTConstants.EVALUATION_ONE_TIER
						.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())
						&& !scQtTechChecklist.isTechEvaluationReq()) {

					specReqDTO.setTobeRemoveList(getToBeRemoveList());
					resDTO = specSO.saveQTDtl(specReqDTO);

					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
					// retriveTechnicalSpecification();
					retriveOneTierCheckList();

				}

				else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(scQtTechChecklist.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(scQtTechChecklist
										.getEvaluationType()) && scQtTechChecklist
								.isTechEvaluationReq())) {

					// specReqDTO.setScQt(scQtTechChecklist);
					specReqDTO.setTobeRemoveList(getToBeRemoveList());
					// specSO.validateCheckList(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);

					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
					retriveTechnicalCheckList();

				}
				break;
			case 3:
				ScQt finChecklist = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(finChecklist);
				specSO.validateCheckList(specReqDTO);
				specReqDTO.setTobeRemoveList(getToBeRemoveList());

				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());

				/*
				 * if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(resDTO
				 * .getScQt().getEvaluationType()) &&
				 * !resDTO.getScQt().isTechEvaluationReq()) {
				 * retriveTechnicalSpecification(); } else if
				 * (QTConstants.EVALUATION_TWO_TIER
				 * .equalsIgnoreCase(resDTO.getScQt().getEvaluationType()) ||
				 * (QTConstants.EVALUATION_ONE_TIER
				 * .equalsIgnoreCase(resDTO.getScQt() .getEvaluationType()) &&
				 * resDTO .getScQt().isTechEvaluationReq())) {
				 * retriveTechnicalCheckList(); }
				 */
				retriveSpecdetails(resDTO.getScQt().getQtId(), "QT");
				retriveFinancialCheckList();
				break;
			case 4:
				ScQt scoring = constructSpecCommitteDtls().getScQt();

				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(scoring
						.getEvaluationType()) && !scoring.isTechEvaluationReq()) {

					List<ScQtCheckList> financialScoring2 = this
							.getManagedBean().getFinancialScoring();
					ScQtScoreSetting financialScoreSetting = this
							.getManagedBean().getFinancialScoreSetting();

					if (null != financialScoreSetting) {
						specReqDTO.setScoreSetting(financialScoreSetting);
					}

					specReqDTO.setCheckLists(financialScoring2);
					specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
					specReqDTO.setScQt(scoring);
					financialWeightageCalculate();
					specSO.validateSCScoreSettings(specReqDTO);
					specSO.validateSpecOfferPrice(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveFinancialScoring();
					// retriveTechnicalSpecification();

				}

				else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(scoring.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(scoring.getEvaluationType()) && scoring
								.isTechEvaluationReq())) {

					List<ScQtCheckList> technicalScoring = this
							.getManagedBean().getTechnicalScoring();
					ScQtScoreSetting technicalScoreSetting = this
							.getManagedBean().getTechnicalScoreSetting();

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
					technicalWeightageCalculate();
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveTechnicalScoring();
					// retriveTechnicalCheckList();

				}
				break;

			case 5:
				ScQt recommendation = constructSpecCommitteDtls().getScQt();

				if (QTConstants.EVALUATION_ONE_TIER
						.equalsIgnoreCase(recommendation.getEvaluationType())
						&& !recommendation.isTechEvaluationReq()) {

					List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
					ScQtScoreSetting packageScoreSetting = this
							.getManagedBean().getRecommendationPackageScore();
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
					if (!this.getManagedBean().isReadOnly()) {
						specSO.validateSCRecommandationSettings(specReqDTO);
					}

					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveRecommendationDetails();
					// retriveTechnicalSpecification();

				}

				else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(recommendation.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(recommendation
										.getEvaluationType()) && recommendation
								.isTechEvaluationReq())) {

					List<ScQtCheckList> financialScoring1 = this
							.getManagedBean().getFinancialScoring();
					ScQtScoreSetting financialScoreSetting = this
							.getManagedBean().getFinancialScoreSetting();

					if (null != financialScoreSetting
							&& financialScoreSetting.getSettingType() == 2) {
						specReqDTO.setScoreSetting(financialScoreSetting);
					}

					specReqDTO.setCheckLists(financialScoring1);
					specReqDTO.setSpecType(QTConstants.FINANCIAL_SPEC_TYPE);
					specReqDTO.setScQt(recommendation);
					specSO.validateSCScoreSettings(specReqDTO);
					specSO.validateSpecOfferPrice(specReqDTO);
					financialWeightageCalculate();
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveFinancialScoring();
					// retriveTechnicalCheckList();

				}
				break;

			case 6:
				ScQt delivery = constructSpecCommitteDtls().getScQt();
				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(delivery
						.getEvaluationType())
						&& !delivery.isTechEvaluationReq()) {
					if (delivery.isZonal()) {
						if (null == delivery.getScQtDeliveryLocs()) {
							List<BriefingZoneBean> zoneBean = this
									.getManagedBean().getDeliveryLocBean();
							if (null != zoneBean) {
								for (BriefingZoneBean deliverybean : zoneBean) {
									if (null != deliverybean
											.getDeliveryLocList()) {
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
					// retriveTechnicalSpecification();

				}

				else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(delivery.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(delivery.getEvaluationType()) && delivery
								.isTechEvaluationReq())) {

					List<ScQtScoreSetting> scoreSetting = new ArrayList<ScQtScoreSetting>();
					ScQtScoreSetting packageScoreSetting = this
							.getManagedBean().getRecommendationPackageScore();
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
					if (!this.getManagedBean().isReadOnly()) {
						specSO.validateSCRecommandationSettings(specReqDTO);
					}
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveRecommendationDetails();
					// retriveTechnicalCheckList();

				}
				break;

			case 7:
				ScQt fulfilMent = constructSpecCommitteDtls().getScQt();
				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(fulfilMent
						.getEvaluationType())
						&& !fulfilMent.isTechEvaluationReq()) {
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
						// retriveCommitteeEndorsment();
						// retriveTechnicalSpecification();
					} else {
						specReqDTO.setScQt(fulfilMent);
						specSO.validateSCFulfillmentSchedules(specReqDTO);
						resDTO = specSO.saveQTDtl(specReqDTO);
						renewSesionObject();
						this.getManagedBean().setScQt(resDTO.getScQt());
						getfulfilmentDtls(resDTO.getScQt());
						// retriveFulfilmentDtls();
						retriveCommitteeEndorsment();
					}

				}

				else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(fulfilMent.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(fulfilMent
										.getEvaluationType()) && fulfilMent
								.isTechEvaluationReq())) {
					if (fulfilMent.isZonal()) {
						if (null == fulfilMent.getScQtDeliveryLocs()) {
							List<BriefingZoneBean> zoneBean = this
									.getManagedBean().getDeliveryLocBean();
							if (null != zoneBean) {
								for (BriefingZoneBean deliverybean : zoneBean) {
									if (null != deliverybean
											.getDeliveryLocList()) {
										fulfilMent
												.setScQtDeliveryLocs(deliverybean
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
					// retriveTechnicalCheckList();

				}
				break;

			case 8:
				ScQt fulfilMentdtls = constructSpecCommitteDtls().getScQt();
				if (QTConstants.EVALUATION_ONE_TIER
						.equalsIgnoreCase(fulfilMentdtls.getEvaluationType())
						&& !fulfilMentdtls.isTechEvaluationReq()) {
					specReqDTO.setScQt(fulfilMentdtls);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setScQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveCommitteeEndorsment();
					// retriveTechnicalSpecification();
				}

				if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(fulfilMentdtls.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(fulfilMentdtls
										.getEvaluationType()) && fulfilMentdtls
								.isTechEvaluationReq())) {
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
						// retriveCommitteeEndorsment();
						// retriveTechnicalCheckList();
					} else {
						specReqDTO.setScQt(fulfilMentdtls);
						specSO.validateSCFulfillmentSchedules(specReqDTO);
						resDTO = specSO.saveQTDtl(specReqDTO);
						renewSesionObject();
						this.getManagedBean().setScQt(resDTO.getScQt());
						getfulfilmentDtls(resDTO.getScQt());
						// retriveFulfilmentDtls();
						retriveCommitteeEndorsment();
					}

				}
				break;

			case 9:

				ScQt endorsment = constructSpecCommitteDtls().getScQt();
				specReqDTO.setScQt(endorsment);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setScQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveCommitteeEndorsment();
				// retriveTechnicalCheckList();
				break;

			}
			if (getManagedBean().getTempIndex() < activeIndex) {
				getManagedBean().setTempIndex(activeIndex);
			}
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setActiveIndex(activeIndex);
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setCurrentStatusId(statusId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setReadOnly(readonly);
		} /*
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		finally {

		}
		/*
		 * this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);
		 * this
		 * .getManagedBean().setScQtCheckSt2techProposal(techchecklistScore);
		 * this.getManagedBean().setFinancialScoring(financialScoring);
		 * this.getManagedBean().setTechnicalScoring(technicalScoringList);
		 */

	}

	public String specCommitteBpmTask(String action) {
		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		committeReqDTO.setUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		if (member.getMemberRole().getParameterCode()
				.contains(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
			this.getManagedBean().setLocalOutCome(action);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

		} else {
			this.getManagedBean().setLocalOutCome(action);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();
				List<ScQtSpecificationRemark> remarks = this.getManagedBean()
						.getSpecificationRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_760154);
						}
					}
				}

				RequestContext.getCurrentInstance().execute(
						"reviceApprovalDlgs.show()");
			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)) {

				SpecificationResDTO resDTO = updateSpecCommitteDtls(action,
						null);
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
															getMessgeConstant(action),
															new String[] {
																	"Specification Preparation And Scoring For",
																	qt.getQtNo() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			}
		}
		return null;

	}

	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteTreeNode() {

		List<TreeNode> rootTrees = this.getManagedBean().getZoneGeneralTrees();

		// LocationSingleton.getInstance().deleteTreeNodes(rootTrees);
		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (!rootTrees.isEmpty()) {
			getManagedBean()
					.getScQt()
					.getScQtSupplierCriteria()
					.getQtZoneCriterias()
					.setTplSpecZoneDtls(
							getZoneBusinessOwnerLocations(rootTrees, zoneDtl));
		}
		this.getManagedBean().setIndex(-1);

	}

	public List<TreeNode> deleteZonesTreeNodes(List<TreeNode> rootTrees) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> removedList = new ArrayList<TreeNode>();
		if (rootTrees != null) {
			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						tplSpecZoneDtl = (TplSpecZoneDtl) zoneTree.getData();
						if (tplSpecZoneDtl.getPanelZone() != null) {
							// deletePanelItemSuppliers(tplSpecZoneDtl);

						} else {
							deleteStateOrDivOrDist(tplSpecZoneDtl, null);
						}

						rootTrees.remove(row--);
					} else {

						removedList = deleteZoneChildTreeNodes(zoneTree
								.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getZoneLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl,
										locationDTOs);
							}
						}

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}

				}
			}
		}
		return removedList;
	}

	private List<TreeNode> deleteZoneChildTreeNodes(List<TreeNode> treeNodes) {
		List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
		if (treeNodes != null) {

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
		return nodesToRemoved;
	}

	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * To delete the selected TreeNode from list..
	 */
	public void deleteSupplierQualificationTreeNode() {
		List<TreeNode> rootTrees = getManagedBean().getZoneTrees();
		deleteTreeNodes(rootTrees);
		this.getManagedBean().setTotalSuppliersSpecLoc(
				new ArrayList<TplSpecLoc>());
		if (!rootTrees.isEmpty()) {

			ScQtSupplierCriteria supplierCriteria = null;
			// this.getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
			List<TplSpecLoc> localLoc = new ArrayList<TplSpecLoc>();
			List<ScQtLocCriteria> Criteria = new ArrayList<ScQtLocCriteria>();
			this.getManagedBean()
					.getScQt()
					.getScQtSupplierCriteria()
					.setScQtLocCriteria(
							getQuoteZones(rootTrees, supplierCriteria));
			for (ScQtLocCriteria locCriteria : this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getScQtLocCriteria()) {
				// Criteria.add(locCriteria);
				TplSpecLoc specLoc = (TplSpecLoc) locCriteria;

				localLoc.add(specLoc);

			}
			// this.getManagedBean().setExistingSupplierLocationList(Criteria);
			this.getManagedBean().setTotalSuppliersSpecLoc(localLoc);
		}

		this.getManagedBean().setIndex(-1);
	}

	public void deleteTreeNodes(List<TreeNode> rootTrees) {
		if (rootTrees != null) {
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						ScQtLocCriteria loccriteria = (ScQtLocCriteria) zoneTree
								.getData();
						rootTrees.remove(row--);
						deleteSupplierLocacriteria(loccriteria);
					} else {
						deleteChildTreeNodes(zoneTree.getChildren());

						if (zoneTree.getChildren().size() == 0) {
							// rootTrees.remove(row--);
						}
					}
				}
			}
		}
	}

	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {

					nodesToRemoved.add(treeNode);

					// deleteSupplierLocacriteria(loccriteria);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {

						deleteChildTreeNodes(treeNode.getChildren());
						// deleteSupplierLocacriteria(loccriteria);
						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
	}

	private void deleteSupplierLocacriteria(ScQtLocCriteria locCriteria) {
		List<ScQtLocCriteria> existingLocation = this.getManagedBean()
				.getExistingSupplierLocationList();
		if (null != existingLocation && !existingLocation.isEmpty()) {
			List<ScQtLocCriteria> exchangeLoccriteria = new ArrayList<ScQtLocCriteria>();
			for (int i = 0; i < existingLocation.size(); i++) {
				ScQtLocCriteria locallocCriteria = (ScQtLocCriteria) existingLocation
						.get(i);
				if (!locallocCriteria.getSpecLocId().equals(
						locCriteria.getSpecLocId())) {
					exchangeLoccriteria.add(locallocCriteria);

				}
			}

			/*
			 * this.getManagedBean().setExistingSupplierLocationList(
			 * exchangeLoccriteria);
			 */

		}

	}

	public void categoryCodesUpdate(int index, List<ScQtCategory> categoryList,
			PmCategoryL3 categoryL3) {
		ICategoryCodeSO categoryCodeSo;
		try {
			categoryCodeSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
			CategoryCodeReqDTO codeReqDTO = new CategoryCodeReqDTO();
			codeReqDTO.setCategoryL3Id(categoryL3.getCategoryL3Id());
			CategoryCodeResDTO CategoryCodeResDTO = categoryCodeSo
					.retrieveCategoryL3DtlById(codeReqDTO);
			if (null != categoryList && categoryList.size() > 1) {
				boolean isDuplicate = false;

				for (int i = 0; i < categoryList.size() - 1; i++) {
					ScQtCategory category1 = categoryList.get(i);
					if (category1
							.getPmCategoryL3()
							.getCategoryL3Code()
							.equalsIgnoreCase(
									CategoryCodeResDTO.getCategoryL3()
											.getCategoryL3Code())
							&& category1

									.getPmCategoryL2()
									.getCategoryL2Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getCategoryL2Code())
							&& category1

									.getPmCategoryL1()
									.getCategoryL1Code()
									.equalsIgnoreCase(
											CategoryCodeResDTO.getCategoryL3()
													.getPmCategoryL2()
													.getPmCategoryL1()
													.getCategoryL1Code())) {
						isDuplicate = true;

						break;
					}

				}
				if (isDuplicate) {

					ScQtCategory category = categoryList.get(index);
					this.getManagedBean().getSuppCriteriaopenList()
							.remove(index);

					// .setScQt(scQt);
					category.setPmCategoryL1(new PmCategoryL1());
					category.setPmCategoryL2(new PmCategoryL2());
					category.setPmCategoryL3(new PmCategoryL3());

					this.getManagedBean().getSuppCriteriaopenList()
							.add(index, category);
					throw new EpException(MessageConst.ERROR_760111);

				} else {
					ScQtCategory category = categoryList.get(index);
					category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
					category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
							.getPmCategoryL2());
					category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
							.getPmCategoryL2().getPmCategoryL1());
					category.setScQt(this.getManagedBean().getScQt());
					setUserDetails(category);
					this.getManagedBean().getSuppCriteriaopenList()
							.remove(index);
					this.getManagedBean().getSuppCriteriaopenList()
							.add(index, category);
				}
			} else {
				ScQtCategory category = categoryList.get(index);
				category.setPmCategoryL3(CategoryCodeResDTO.getCategoryL3());
				category.setPmCategoryL2(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2());
				category.setPmCategoryL1(CategoryCodeResDTO.getCategoryL3()
						.getPmCategoryL2().getPmCategoryL1());
				category.setScQt(this.getManagedBean().getScQt());
				setUserDetails(category);
				this.getManagedBean().getSuppCriteriaopenList().remove(index);
				this.getManagedBean().getSuppCriteriaopenList()
						.add(index, category);
			}

		} finally {

		}

	}

	// Ministry Spec Approval Method

	public String ministrySpecgpkiSignAction(PmDigiSign digiSign) {
		SpecificationResDTO resDTO = prepareSpecChirPersonRequest(this
				.getManagedBean().getLocalOutCome(), digiSign);
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
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															getMessageCommon("lbl.sc.specification.for"),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	private SpecificationResDTO prepareSpecChirPersonRequest(String action,
			PmDigiSign pmDigiSign) {
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		SpecificationResDTO resDTO = new SpecificationResDTO();
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

		specReqDTO.setOutcome(action);
		if (null != pmDigiSign) {
			updatePmDigiSignData(pmDigiSign, ParameterConst.DOCTYPE_QUOTATION,
					scQt.getQtId(), scQt.getQtNo());
		}
		specReqDTO.setDigiSignData(pmDigiSign);
		specReqDTO.setTaskId(this.getManagedBean().getTaskId());
		specReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		specReqDTO.setPmUser(facesScopes.getUserSessionBean());
		specReqDTO.setRoleCode(getUserRoleCode());
		specReqDTO.setTaskName(this.getManagedBean().getTaskName());
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		specReqDTO.setToken(ctx);
		IQTSpecSO specSO = getSpecSO();

		try {
			resDTO = specSO.updateBPMAction(specReqDTO);

		} finally {
			specSO = null;
		}

		return resDTO;
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
			// supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel());
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
			this.getManagedBean().setBeans(new ArrayList<ZonesBean>());

		}
		scQt.setScQtSupplierCriteria(supplierCriteria);
		this.getManagedBean().setScQt(scQt);

	}

	// View Circular Attachment Download Method
	public void downloadCircularDtls() throws IOException {
		List<CtPanelItem> itemList = this.getManagedBean().getSelectpanelItem();
		itemList = new ArrayList<CtPanelItem>();
		itemList.add(this.getManagedBean().getCtPanelItem());

		String filePath = this.getManagedBean().getCtCircularVer()
				.getCircularFilePath();
		String fileName = this.getManagedBean().getCtCircularVer()
				.getCircularFileName();

		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder actualFilePath = new StringBuilder(docRepoBaseDir);
		actualFilePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		actualFilePath.append(filePath);

		downloadFile(actualFilePath.toString(), fileName);
		this.getManagedBean().setSelectpanelItem(itemList);
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

	// Spec Item Type DropDOwn Values method
	public SelectItem[] getQuotationTendervalues(String itemType) {

		// ISearchBean<PmParameterDesc> searchBean = new
		// SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
		if (null != this.getManagedBean().getScQt()
				&& null != this.getManagedBean().getScQt().getEstimatedAmount()
				&& this.getManagedBean().getScQt().getEstimatedAmount()
						.intValue() > 500000) {
			recommendList
					.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
		}
		recommendList.add("I");

		SelectItem[] items = null;
		try {
			items = new SelectItem[recommendList.size()];
			int i = 0;
			for (String location : recommendList) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterCode(), paramDesc.getCodeName());

					}
				}
			}
		} finally {
		}
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
		ScQt scQt = this.getManagedBean().getScQt();
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			loadPrepareSpecCommitteeDetails(scQt.getQtId(),
					getDocTypeByTaskName(this.getManagedBean().getTaskName()));

		}
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls();

		spReqDTO.setOutcome(action);

		if (null != digiSign) {
			updatePmDigiSignData(digiSign,
					ParameterConst.DOCTYPE_SPEC_COMMITTE, scQt.getQtId(),
					scQt.getQtNo());
		}

		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {

			List<ScQtSpecificationRemark> remarks = this.getManagedBean()
					.getSpecificationRemarks();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						// throw new EpException(MessageConst.ERROR_760154);
					}
				}
			}

			if (null != remarks) {
				ScQt localScQt = spReqDTO.getScQt();
				localScQt.setScQtSpecificationRemarks(remarks);
				spReqDTO.setScQt(localScQt);
			}
			List<ScQtCommitteeActivity> committeActivities = this
					.getManagedBean().getCommitteeActivities();
			if (null != committeActivities) {

				for (int i = 0; i < committeActivities.size(); i++) {

					committeActivities.get(i).setSignedOff(false);

				}
			}
			spReqDTO.setCommitteActivities(committeActivities);
		}
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getSpecificationDataType());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		spReqDTO.setToken(ctx);
		spReqDTO.setPmUser(facesScopes.getUserSessionBean());
		spReqDTO.setRoleCode(getUserRoleCode());
		spReqDTO.setTaskName(this.getManagedBean().getTaskName());
		IQTSpecSO specSO = getSpecSO();
		SpecificationResDTO resDTO = new SpecificationResDTO();
		try {
			resDTO = specSO.updateBPMAction(spReqDTO);

		} finally {
			specSO = null;
		}
		return resDTO;
	}

	public String cancelQt() {
		SpecificationReqDTO spReqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getScQt();
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		scQt.setScQtBriefings(null);
		List<ScQtRemark> remarks = this.getManagedBean().getScQtRemarksList();
		if (null != remarks) {
			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					remarks.remove(i);
				}
			}
		}

		if (null != remarks) {
			scQt.setScQtRemarks(remarks);
		}
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
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this
				.getManagedBean().getLocalOutCome(), digiSign);
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
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
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

	public String reviseMinistrySpecApproval() {
		SpecificationResDTO resDTO = prepareSpecChirPersonRequest(this
				.getManagedBean().getLocalOutCome(), null);
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
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
													new String[] {
															getMessageCommon("lbl.sc.specification.for"),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));

			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}

	public String reviseSpecificationCommittee() {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this
				.getManagedBean().getLocalOutCome(), null);
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
													getMessgeConstant(this
															.getManagedBean()
															.getLocalOutCome()),
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
			// toBeRemoveCheckLists.add(checkList);
			List<ScQtCheckList> technicalCheckLIsts = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			for (int i = 0; i < technicalCheckLIsts.size(); i++) {
				if (checkList.getTitle().equalsIgnoreCase(
						technicalCheckLIsts.get(i).getTitle())) {
					ScQtCheckList technicalCheckList = technicalCheckLIsts
							.get(i);
					technicalCheckList.setDeleted(true);
					toBeRemoveCheckLists.add(technicalCheckList);
					technicalCheckLIsts.remove(technicalCheckList);
					break;
				}
			}

			List<ScQtCheckList> financialCheckLIsts = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			for (int i = 0; i < financialCheckLIsts.size(); i++) {
				if (checkList.getTitle().equalsIgnoreCase(
						financialCheckLIsts.get(i).getTitle())) {
					ScQtCheckList financialCheckList = financialCheckLIsts
							.get(i);
					financialCheckList.setDeleted(true);
					toBeRemoveCheckLists.add(financialCheckList);
					financialCheckLIsts.remove(financialCheckList);
					break;
				}
			}

			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();

			specReqDTO.setCheckLists(toBeRemoveCheckLists);
			SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);

			if (resDTO.isSuccessful()) {
				if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
					this.getManagedBean().setScQtCheckSt2techProposal(null);
					retriveTechnicalSpecification();
					this.getManagedBean().setIndex(-1);
					return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
				} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
						.equalsIgnoreCase(flowType)) {
					if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
							.getManagedBean().getScQt().getEvaluationType())
							&& !this.getManagedBean().getScQt()
									.isTechEvaluationReq()) {
						// retriveTechnicalCheckList();
						// this.getManagedBean().setScQtCheckSt2techProposal(null);
						// retriveTechnicalSpecification();
						this.getManagedBean().setScQtCheckSt2techProposal(null);
						this.getManagedBean().setFinancialScoring(null);
						retriveOneTierCheckList();
					} else if (QTConstants.EVALUATION_TWO_TIER
							.equalsIgnoreCase(this.getManagedBean().getScQt()
									.getEvaluationType())
							|| (QTConstants.EVALUATION_ONE_TIER
									.equalsIgnoreCase(this.getManagedBean()
											.getScQt().getEvaluationType()) && this
									.getManagedBean().getScQt()
									.isTechEvaluationReq())) {
						// this.getManagedBean().setScQtCheckSt2techProposal(null);
						// retriveTechnicalCheckList();
						this.getManagedBean().setScQtCheckSt2techProposal(null);
						this.getManagedBean().setFinancialScoring(null);
						retriveTechnicalCheckList();
					}
					this.getManagedBean().setIndex(-1);
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
		if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)
				&& SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
			validateBusinessOwner(true);

		}

		if (!SourcingConstants.FINANCIAL_DOCTYPE.equalsIgnoreCase(specType)) {
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmStatus(status);
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmEvalStatus(evalStatus);
			if (getManagedBean().getScQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getScQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					scQtCheckList
							.getScQtDoc()
							.getTplSpecMaster()
							.setTplSpecZone(
									supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}

		} else {

			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmStatus(status);
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmEvalStatus(evalStatus);
		}
		List<ScQtCheckList> toBeRemoveList = null;
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {

			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (this.getManagedBean().getIndex() == -1
					&& null == scQtCheckList.getQtChecklistId()) {
				if (null != technicalCheckList && !technicalCheckList.isEmpty()) {

					if (technicalCheckList.contains(scQtCheckList)) {
						throw new EpException(MessageConst.ERROR_760207,
								new String[] { scQtCheckList.getTitle() });
					}
				}
			}

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
			List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
				// retriveTechnicalSpecification();

				// settemplateSpecList(savedCheckLists);
				// this.getManagedBean().setIndex(-1);
				// this.getManagedBean().setCheckListTitle("");
				// ScQtCheckList localCheckList=savedCheckLists.get(0);
				setTechnicalCheckList(savedCheckLists.get(0));
				// return
				// "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
			} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
					.equalsIgnoreCase(flowType)) {

				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType())
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					// retriveTechnicalSpecification();
					// retriveTechnicalCheckList();
					// settemplateSpecList(savedCheckLists);
					this.getManagedBean().setFinancialScoring(null);
					if (SourcingConstants.TECHNICAL_DOCTYPE
							.equalsIgnoreCase(specType)) {
						// settemplateSpecList(savedCheckLists);
						// retriveTechnicalSpecification();
						setTechnicalCheckList(savedCheckLists.get(0));
					} else {
						// settemplateSpecList(savedCheckLists);
						// retriveFinancialCheckList();
						setTechnicalCheckList(savedCheckLists.get(0));
					}
				} else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(this.getManagedBean().getScQt()
								.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(this.getManagedBean()
										.getScQt().getEvaluationType()) && this
								.getManagedBean().getScQt()
								.isTechEvaluationReq())) {
					this.getManagedBean().setFinancialScoring(null);
					// settemplateSpecList(savedCheckLists);
					setTechnicalCheckList(savedCheckLists.get(0));

				}
				// this.getManagedBean().setCheckListTitle("");
				// this.getManagedBean().setIndex(-1);
				// return
				// "/xhtml/sc/specification/specificationCommitteeView.xhtml?faces-redirect=true";

			}
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													MessageConst.INFO_700004,
													new String[] {
															getMessageCommon("lbl.sc.template.spec"),
															"" },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
		}

		return null;
	}

	private void setTechnicalCheckList(ScQtCheckList localCheckList) {
		// this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setScQtChecklist(localCheckList);
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO
				.setSpecMaster(localCheckList.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO tmpresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		localCheckList.getScQtDoc().setTplSpecMaster(tmpresDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(localCheckList.getScQtDoc()
				.getTplSpecMaster());
		if (this.getManagedBean().getScQt().isZonal()) {
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getScQt().getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias()) {
				if (SourcingConstants.FINANCIAL_DOCTYPE
						.equalsIgnoreCase(localCheckList.getDocCategory())) {
					retriveSpecdetails(this.getManagedBean().getScQt()
							.getQtId(), "QT");
					List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {
						// TODO Code needs to be remove
						// setUserDetails(qtSupplierCriteria.getQtZoneCriterias());

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				}

			}
		}
		if (null == this.getManagedBean().getScQtCheckSt2techProposal()) {
			this.getManagedBean().setScQtCheckSt2techProposal(
					new ArrayList<ScQtCheckList>());
		}
		if (null == this.getManagedBean().getScQtCheckSt2financeProposalList()) {
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					new ArrayList<ScQtCheckList>());
		}
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(localCheckList
				.getDocCategory())) {
			if (this.getManagedBean().getIndex() == -1) {
				if (null != this.getManagedBean().getScQtCheckSt2techProposal()
						&& !this.getManagedBean().getScQtCheckSt2techProposal()
								.isEmpty()) {
					boolean techSpec = false;
					for (int i = 0; i < this.getManagedBean()
							.getScQtCheckSt2techProposal().size(); i++) {
						ScQtCheckList qtCheckList = this.getManagedBean()
								.getScQtCheckSt2techProposal().get(i);
						if (qtCheckList.getTitle().equalsIgnoreCase(
								localCheckList.getTitle())) {
							techSpec = true;
							this.getManagedBean().getScQtCheckSt2techProposal()
									.remove(i);
							this.getManagedBean().getScQtCheckSt2techProposal()
									.add(localCheckList);
							// Collections.sort(finanicialCheckLists);
						}
					}
					if (!techSpec) {
						this.getManagedBean().getScQtCheckSt2techProposal()
								.add(localCheckList);

					}
				} else {
					this.getManagedBean().getScQtCheckSt2techProposal()
							.add(localCheckList);
				}
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2techProposal());
			} else {
				this.getManagedBean().getScQtCheckSt2techProposal()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean().getScQtCheckSt2techProposal()
						.add(this.getManagedBean().getIndex(), localCheckList);
			}
		} else {
			setFinSpecScoreWeightage(localCheckList);
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), localCheckList);

		}

	}

	private void setFinSpecScoreWeightage(ScQtCheckList scoreWeightage) {
		List<ScQtCheckList> finacialspecLists = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != finacialspecLists && !finacialspecLists.isEmpty()) {
			for (ScQtCheckList checklists : finacialspecLists) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checklists.getDefinationType()
								.getParameterCode())) {
					checklists.setQtScoreWeightage(scoreWeightage
							.getQtScoreWeightage());

				}
			}
		}
	}

	private void settemplateSpecList(List<ScQtCheckList> savedCheckLists) {

		if (null != savedCheckLists && !savedCheckLists.isEmpty()) {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()) {
				this.getManagedBean().setScQtCheckSt2techProposal(
						new ArrayList<ScQtCheckList>());
			}

			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()) {
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						new ArrayList<ScQtCheckList>());
			}

			int currIndex = this.getManagedBean().getIndex();
			int newTplSpecIndex = 0;
			int lastTplSpecIndex = 0;
			boolean isNewTplSpec = this.getManagedBean().isNewTemplateSpec();

			if (isNewTplSpec) {
				for (ScQtCheckList checkList : this.getManagedBean()
						.getScQtCheckSt2techProposal()) {
					if (checkList
							.getDefinationType()
							.getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
						lastTplSpecIndex = lastTplSpecIndex + 1;
					}
				}
				newTplSpecIndex = lastTplSpecIndex;
			}

			for (ScQtCheckList checkList : savedCheckLists) {

				if (SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(checkList.getDocCategory())) {
					List<ScQtCheckList> technicalCheckLists = this
							.getManagedBean().getScQtCheckSt2techProposal();

					if (currIndex == -1) {
						if (null != technicalCheckLists
								&& !technicalCheckLists.isEmpty()) {
							boolean techSpec = false;

							for (int i = 0; i < technicalCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = technicalCheckLists
										.get(i);

								if (qtCheckList.getTitle().equalsIgnoreCase(
										checkList.getTitle())) {
									techSpec = true;
									technicalCheckLists.remove(i);
									technicalCheckLists.add(i, checkList);
								}
							}
							if (!techSpec) {
								if (isNewTplSpec) {
									technicalCheckLists.add(newTplSpecIndex,
											checkList);
								} else {
									technicalCheckLists.add(checkList);
								}
							}

						} else {
							technicalCheckLists.add(checkList);
						}

					} else {
						technicalCheckLists.remove(currIndex);
						technicalCheckLists.add(currIndex, checkList);
					}

				} else {
					setFinSpecScoreWeightage(checkList);
					List<ScQtCheckList> finanicialCheckLists = this
							.getManagedBean()
							.getScQtCheckSt2financeProposalList();

					if (currIndex == -1) {
						if (isNewTplSpec) {
							finanicialCheckLists
									.add(newTplSpecIndex, checkList);
						} else {
							finanicialCheckLists.add(checkList);
						}

					} else {
						if (null != finanicialCheckLists
								&& !finanicialCheckLists.isEmpty()) {
							boolean finspec = false;

							for (int i = 0; i < finanicialCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = finanicialCheckLists
										.get(i);
								if (qtCheckList.getTitle().equalsIgnoreCase(
										checkList.getTitle())) {
									finspec = true;
									finanicialCheckLists.remove(i);
									finanicialCheckLists.add(i, checkList);
								}
							}

							if (!finspec) {
								finanicialCheckLists.add(currIndex, checkList);
							}

						} else {
							finanicialCheckLists.add(checkList);
						}
					}
					/*
					 * this.getManagedBean().getScQtCheckSt2financeProposalList()
					 * .remove(this.getManagedBean().getIndex());
					 * this.getManagedBean
					 * ().getScQtCheckSt2financeProposalList()
					 * .add(this.getManagedBean().getIndex(), checkList);
					 */
				}
			}
		}

	}

	/**
	 * Will be called to save or edit for both techinical flowType maybe
	 * preparespec or scroing
	 * 
	 * @return
	 * @throws Exception
	 */
	public String completeTemplateSpecDetails(String flowType, String specType) {

		if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)
				&& SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
			validateBusinessOwner(true);

		}
		// retriveSpecdetails(this.getManagedBean().getScQt().getQtId(), "QT");
		// retriveBusinessOwnerQualificationCriteri();

		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
			if (null == scQtCheckList.getTitle()
					|| "".equalsIgnoreCase(scQtCheckList.getTitle())) {
				scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
						.getTplSpecMaster().getSpecName());
			}
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (this.getManagedBean().getIndex() == -1
					&& null == scQtCheckList.getQtChecklistId()) {
				if (null != technicalCheckList && !technicalCheckList.isEmpty()) {

					if (technicalCheckList.contains(scQtCheckList)) {
						throw new EpException(MessageConst.ERROR_760207,
								new String[] { scQtCheckList.getTitle() });
					}
				}
			}
			scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
					.getTplSpecMaster().getSpecName());
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_COMPLETED);
			scQtCheckList.setPmStatus(status);
			scQtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			// Code to construct financial checklist..
			ScQt scQt = this.getManagedBean().getScQt();
			if (scQt.isZonal()) {
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					scQtCheckList
							.getScQtDoc()
							.getTplSpecMaster()
							.setTplSpecZone(
									supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}
			if (null == scQt) {
				throw new EpException("Qt id should not be null");
			}
			TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(
							scQtCheckList.getScQtDoc().getTplSpecMaster(),
							false);
			specMaster
					.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);

			if (scQt.isZonal()) {
				ScQtSupplierCriteria supplierCriteria = scQt
						.getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					specMaster.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
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
			qtCheckList.setScoringReq(true);
			qtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			qtCheckList.getScQtDoc().getTplSpecMaster().setWeightageReq(false);
			TemplateSpecHelper.getInstance().updateItemWeightageReq(
					qtCheckList.getScQtDoc().getTplSpecMaster(),
					qtCheckList.getScQtDoc().getTplSpecMaster()
							.getTplSpecQuestions());
			qtCheckList.getScQtDoc().getTplSpecMaster().setScoringReq(true);

			updatedCheckLists.add(scQtCheckList);
			updatedCheckLists.add(qtCheckList);
			List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			List<ScQtCheckList> toBeRemoveList = new ArrayList<ScQtCheckList>();
			if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
				if (null != this.getManagedBean().getCheckListTitle()
						&& !"".equals(this.getManagedBean().getCheckListTitle())) {
					for (int i = 0; i < finanicalCheckList.size(); i++) {
						ScQtCheckList localCheckList = finanicalCheckList
								.get(i);
						if (this.getManagedBean().getCheckListTitle()
								.equalsIgnoreCase(localCheckList.getTitle())) {
							localCheckList.setDeleted(true);
							toBeRemoveList.add(localCheckList);
							finanicalCheckList.remove(localCheckList);
						}
					}
				}
			}
			reqDTO.setTobeRemoveList(toBeRemoveList);
		} else {
			PmStatus status = new PmStatus();
			status.setStatusId(QTStatusConst.TS_COMPLETED);
			scQtCheckList.setPmStatus(status);
			PmStatus evalStatus = new PmStatus();
			evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
			scQtCheckList.setPmEvalStatus(evalStatus);
			scQtCheckList.getScQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			updatedCheckLists.add(scQtCheckList);

		}

		reqDTO.setCheckLists(updatedCheckLists);

		// code if alredy checklist exist need to remove existing finalcialspec

		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationResDTO resDTO = new SpecificationResDTO();
		try {
			resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);
		} catch (EpException e) {

			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
				if (null != reqDTO.getCheckLists()) {
					for (ScQtCheckList checkList : reqDTO.getCheckLists()) {
						if (SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(checkList.getDocCategory())) {
							PmStatus status = new PmStatus();
							status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
							checkList.setPmStatus(status);
							this.getManagedBean().setScQtChecklist(checkList);
						}
					}
				}
			} else {
				if (SourcingConstants.FINANCIAL_DOCTYPE
						.equalsIgnoreCase(specType)) {
					if (null != reqDTO.getCheckLists()) {
						for (ScQtCheckList checkList : reqDTO.getCheckLists()) {
							if (SourcingConstants.FINANCIAL_DOCTYPE
									.equalsIgnoreCase(checkList
											.getDocCategory())) {
								PmStatus status = new PmStatus();
								status.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
								checkList.setPmStatus(status);
								this.getManagedBean().setScQtChecklist(
										checkList);
							}
						}
					}
				}
			}
			String msgCode = e.getMessage();
			String msgParam[] = e.getParameters();
			throw new EpException(msgCode, msgParam);
		}

		// Need to redirect particualr page..
		if (resDTO.isSuccessful()) {
			List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowType)) {
				// retriveTechnicalSpecification();
				settemplateSpecList(savedCheckLists);
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().setCheckListTitle("");
				return "/xhtml/sc/specification/specificationCreation.xhtml?faces-redirect=true";
			} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
					.equalsIgnoreCase(flowType)) {

				if (QTConstants.EVALUATION_ONE_TIER.equalsIgnoreCase(this
						.getManagedBean().getScQt().getEvaluationType())
						&& !this.getManagedBean().getScQt()
								.isTechEvaluationReq()) {
					// retriveTechnicalSpecification();
					// retriveTechnicalCheckList();
					// settemplateSpecList(savedCheckLists);
					if (SourcingConstants.TECHNICAL_DOCTYPE
							.equalsIgnoreCase(specType)) {
						// retriveTechnicalSpecification();
						settemplateSpecList(savedCheckLists);
					} else {
						settemplateSpecList(savedCheckLists);
						// retriveFinancialCheckList();
						// settemplateSpecList(savedCheckLists);
					}
				} else if (QTConstants.EVALUATION_TWO_TIER
						.equalsIgnoreCase(this.getManagedBean().getScQt()
								.getEvaluationType())
						|| (QTConstants.EVALUATION_ONE_TIER
								.equalsIgnoreCase(this.getManagedBean()
										.getScQt().getEvaluationType()) && this
								.getManagedBean().getScQt()
								.isTechEvaluationReq())) {
					settemplateSpecList(savedCheckLists);

				}
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setFinancialScoring(null);
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
			specMaster.setScoringReq(getManagedBean().getScQt()
					.isTechEvaluationReq());
			if (getManagedBean().getScQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getScQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					specMaster.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			}

			ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
					SourcingConstants.TECHNICAL_DOCTYPE);

			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();

			templateSpecBean.setSpecMaster(specMaster);

			this.getManagedBean().setScQtChecklist(qtCheckList);
			this.getManagedBean().setIndex(-1);
			RequestContext.getCurrentInstance().execute(
					"createOwnerSpec.hide()");
			// RequestContext.getCurrentInstance().update("btnId");
		} finally {

		}

		if (SourcingConstants.FLOW_TYPE_BO.equalsIgnoreCase(flowtype)) {
			return "/xhtml/sc/main/newTemplateMaintenance.xhtml?faces-redirect=true";

		} else if (SourcingConstants.FLOW_TYPE_SPEC_COMMITTE
				.equalsIgnoreCase(flowtype)) {
			this.getManagedBean().setIndex(-1);
			this.getManagedBean().setNewTemplateSpec(true);

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
		specMaster.setScQtDoc(scqtDocDest);
		if (null == specMaster.getItemOriginCode()
				|| null == specMaster.getItemOriginCode().getParameterId()) {
			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			ParameterResDTO localGoodsResDTO = parameterSo
					.retrieveParameterByTypeAndCode(localGoodsReqDTO);
			specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
		} else {
			specMaster.setItemOriginCode(specMaster.getItemOriginCode());
		}
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
		checkList
				.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(this.getManagedBean().getScQt()
				.isTechEvaluationReq());
		checkList.setAction(checkListactionresDTO.getPmParameter());
		checkList.setScQt(this.getManagedBean().getScQt());
		checkList.setDefinationType(specTyperesDTO.getPmParameter());
		PmStatus checklistStatus = new PmStatus();
		checklistStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
		checkList.setPmStatus(checklistStatus);
		PmStatus evalStatus = new PmStatus();
		evalStatus.setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
		checkList.setPmEvalStatus(evalStatus);
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
		ScQt scQt = this.getManagedBean().getScQt();

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
			templateSpecBean.getSpecMaster().setScoringReq(
					scQt.isTechEvaluationReq());
		}
		/* if (SourcingConstants.FINANCIAL_STAGE.equalsIgnoreCase(specType)) { */
		if (scQt.isZonal()) {
			ScQtSupplierCriteria supplierCriteria = scQt
					.getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias()) {
				if (SourcingConstants.FINANCIAL_STAGE
						.equalsIgnoreCase(specType)
						&& !supplierCriteria.getQtZoneCriterias().isModified()) {

					List<TplSpecZoneDtl> businessOwnerZone = getZoneBusinessOwnerLocations(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {
						// TODO Code needs to be remove
						// setUserDetails(qtSupplierCriteria.getQtZoneCriterias());

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(
									true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria
									.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(
								supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone()
								.setTplSpecZoneDtls(businessOwnerZone);
					}

				} else {
					templateSpecBean.getSpecMaster().setTplSpecZone(
							supplierCriteria.getQtZoneCriterias());
				}

			}
		}
		// }

		if (SourcingConstants.TECHINICAL_STAGE.equalsIgnoreCase(specType)) {
			return "/xhtml/sc/main/BusinessOwnerTechnicalSpecEdit.xhtml?faces-redirect=true";
		} else if (SourcingConstants.FINANCIAL_STAGE.equalsIgnoreCase(specType)) {
			return "/xhtml/sc/main/financialTemplate.xhtml?faces-redirect=true";
		}
		return null;

	}

	/**
	 * Method to find the total suppliers count based on supplier qualification
	 * criteria..
	 */
	public void searchForSupplierCount() {
		List<String> codes = new ArrayList<String>();
		List<ScQtCategory> suppCriteriaopenList = getManagedBean()
				.getSuppCriteriaopenList();
		ScQtSupplierCriteria scQtSupplierCriteria = getManagedBean().getScQt()
				.getScQtSupplierCriteria();
		BigDecimal minPaidUpCapital = scQtSupplierCriteria
				.getMinPaidUpCapital();

		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setCount(true);
		supplierSearchReqDTO
				.setCategoryCodeIds(constructL3Ids(suppCriteriaopenList));
		supplierSearchReqDTO.setPaidUpCapital(minPaidUpCapital);
		String locLevel = "";
		List<TplSpecLoc> tplSpecLocList = new ArrayList<TplSpecLoc>();

		ScQt scQt = getManagedBean().getScQt();
		int estimatedAmt = scQt.getEstimatedAmount().intValue();
		if (estimatedAmt >= SourcingConstants.ESTMT_FIVE_HUNDERED_THOUSAND) {
			// value > 500k - always get the
			// scQtSupplierCriteria.getLocationLevel()

			if (getManagedBean().getAllStateCode() != 3
					&& !scQtSupplierCriteria.isAllState()) {
				if (null != scQtSupplierCriteria
						&& null != scQtSupplierCriteria.getLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getScQtSupplierCriteria()
							.getLocationLevel().getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}
				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
			} else {
				// semenanjung, always all state = true or sabah sarawak all
				// state = true
				tplSpecLocList = null;
			}
		} else {
			if (scQt.isZonal()) {
				// value < 500k - if zonal, get the scQt.zoneLocationLevel()
				locLevel = scQt.getZoneLocationLevel().getParameterCode();

				if (null != scQt.getZoneLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getZoneLocationLevel()
							.getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}

				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
				if (null == tplSpecLocList) {
					tplSpecLocList = new ArrayList<TplSpecLoc>();
				}
				// if(tplSpecLocList == null || tplSpecLocList.size() == 0){
				List<TreeNode> rootZoneLocation = getManagedBean()
						.getZoneGeneralTrees();
				if (rootZoneLocation != null) {
					for (TreeNode treeNode : rootZoneLocation) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						tplSpecLocList.addAll(locationDTO);
					}
				}
				// }

			} else {
				// value < 500k - if not zonal, get the
				// scQtSupplierCriteria.getLocationLevel()

				if (null != scQtSupplierCriteria
						&& null != scQtSupplierCriteria.getLocationLevel()) {
					List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
					Long locLevelId = scQt.getScQtSupplierCriteria()
							.getLocationLevel().getParameterId();
					if (null != locLevelId) {
						for (PmParameterDesc paramDesc : localParamDesc) {
							Long paramDescId = paramDesc.getPmParameter()
									.getParameterId();
							if (paramDescId.equals(locLevelId)) {
								locLevel = paramDesc.getPmParameter()
										.getParameterCode();
							}
						}
					}
				}
				tplSpecLocList = getManagedBean().getTotalSuppliersSpecLoc();
			}
		}

		getStatesNDistrict(tplSpecLocList, codes, locLevel);
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setStateCodes(codes);
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION
				.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setDivisionCodes(codes);
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
				.equalsIgnoreCase(locLevel)) {
			supplierSearchReqDTO.setDistrictCodes(codes);
		}

		ISupplierSearchSO supplierSearchSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
		SupplierSearchResDTO searchResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierSearchReqDTO);
		this.getManagedBean().setTotalSuppliers(searchResDTO.getCount());

	}

	public void evaluationTypeChange() {
		String evaluationType = getManagedBean().getScQt().getEvaluationType();
		if (evaluationType.equals("2")) {
			getManagedBean().getScQt().setTechEvaluationReq(true);
		}
		techEvaluationReqChanged();
	}

	public void techEvaluationReqChanged() {
		boolean isTechEvaluationReq = getManagedBean().getScQt()
				.isTechEvaluationReq();

		// TODO - Get list of technical checklist to set score req.
		retriveTechnicalSpecification();
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null != techCheckList) {
			for (ScQtCheckList qtCheckList : techCheckList) {
				qtCheckList.setScoringReq(isTechEvaluationReq);
				qtCheckList.getScQtDoc().getTplSpecMaster()
						.setScoringReq(isTechEvaluationReq);

			}
		}
		// this.getManagedBean().setScQtCheckSt2techProposal(localCheckList);
		// specMaster.setScoringReq(isTechEvaluationReq);
	}

	private List<Long> constructL3Ids(List<ScQtCategory> categories) {
		List<Long> categoryIds = null;
		if (null != categories) {
			categoryIds = new ArrayList<Long>();
			for (ScQtCategory category : categories) {
				Long catId = category.getPmCategoryL3().getCategoryL3Id();
				categoryIds.add(catId);
			}
		}
		return categoryIds;
	}

	private void getStatesNDistrict(List<? extends TplSpecLoc> locations,
			List<String> codes, String locLevel) {

		List<PmState> states = new ArrayList<PmState>();
		List<PmDistrict> districts = new ArrayList<PmDistrict>();
		List<PmDivision> divisions = new ArrayList<PmDivision>();
		if (null != locations) {
			for (TplSpecLoc specLoc : locations) {
				if (ParameterConst.LOCATION_LEVEL_STATE
						.equalsIgnoreCase(locLevel)) {
					PmState state = specLoc.getPmState();
					if (null != state) {
						if (!codes.contains(state.getStateCode())) {
							codes.add(state.getStateCode());
							states.add(state);
						}
					}
					this.getManagedBean().setSupplierStateCode(codes);
					if (null == this.getManagedBean().getStateList()) {
						this.getManagedBean().setStateList(
								new ArrayList<PmState>());
					}
					this.getManagedBean().setStateList(states);

				} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
						.equalsIgnoreCase(locLevel)) {
					PmDistrict district = specLoc.getPmDistrict();
					if (null != district) {
						if (!codes.contains(district.getDistrictCode())) {
							codes.add(district.getDistrictCode());
							districts.add(district);
						}
					}
					this.getManagedBean().setSupplierDistrictCode(codes);
					if (null == this.getManagedBean().getDistrictList()) {
						this.getManagedBean().setDistrictList(
								new ArrayList<PmDistrict>());
					}
					this.getManagedBean().setDistrictList(districts);

				} else if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equalsIgnoreCase(locLevel)) {
					PmDivision divison = specLoc.getPmDivision();
					if (null != divison) {
						if (!codes.contains(divison.getDivisionCode())) {
							codes.add(divison.getDivisionCode());
							divisions.add(divison);
						}
					}
					this.getManagedBean().setSupplierDivisionCode(codes);
					if (null == this.getManagedBean().getDivisionList()) {
						this.getManagedBean().setDivisionList(
								new ArrayList<PmDivision>());
					}
					this.getManagedBean().setDivisionList(divisions);
				}
			}
		}
	}

	public void updateSelectedCheckListIndx(ScQtCheckList checkList, int index) {
		if (checkList.isSelected()) {
			checkList.setIndex(index);
		} else {
			checkList.setIndex(-1);
		}
	}

	public List<SelectItem> getBankYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int previousYear = currentYear - 1;
			int nextYear = currentYear + 1;

			selectItems.add(new SelectItem(previousYear, String
					.valueOf(previousYear)));
			selectItems.add(new SelectItem(currentYear, String
					.valueOf(currentYear)));
			selectItems.add(new SelectItem(nextYear, String.valueOf(nextYear)));
			this.getManagedBean().setBankyearsList(selectItems);

		} finally {

		}
		return this.getManagedBean().getBankyearsList();
	}

	public void deleteCheckListFile(ScQtCheckListAttachment checkListAttachment) {
		checkListAttachment = null;
		this.getManagedBean().getScQtChecklist()
				.setScQtCheckListAttachment(checkListAttachment);

	}

	public SmMofAccount retrieveSmMofAccount(ScQtSupplier scQtSupplier,
			SmSupplier smSupplier) {
		// debugLogger("retrieveSmMofAccount");

		Long supplierId = null;
		SmMofAccount smMofAccount = null;
		if (scQtSupplier != null) {
			supplierId = scQtSupplier.getSmSupplier().getSupplierId();
			smMofAccount = scQtSupplier.getSmMofAccount();

			if (smMofAccount != null) {
				return smMofAccount;
			}
		} else {
			supplierId = smSupplier.getSupplierId();
			smMofAccount = smSupplier.getSmMofAccount();

			if (smMofAccount != null) {
				return smMofAccount;
			}
		}

		IMofAccountSO mofAccountSO;
		try {
			mofAccountSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
			accountReqDTO.setSupplierId(supplierId);
			accountReqDTO
					.setSupplierType(ParameterConst.SUPPLIER_TYPE_CONTRACTOR);

			MofAccountResDTO mofAccountResDTO = mofAccountSO
					.retriveMOFAccountBySupplierIdAndType(accountReqDTO);
			if (mofAccountResDTO.getMofAccount() != null) {
				smMofAccount = mofAccountResDTO.getMofAccount();
				if (scQtSupplier != null) {
					scQtSupplier.setSmMofAccount(smMofAccount);
				} else {
					smSupplier.setSmMofAccount(smMofAccount);
				}
			}
		} finally {
			mofAccountSO = null;
		}
		return smMofAccount;
	}

	public void showSupplierDetails(ScQtSupplier qtSupplier) {
		// debugLogger("showSupplierDetails");
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);

			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(qtSupplier.getSmSupplier().getSupplierId());

			QTSupplierResDTO resDTO = qtSupplierSO
					.retrieveSupplierDtlById(reqDTO);

			this.getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			this.getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());
			/*
			 * List<SmPersonnel> personnelsList = resDTO.getSmSupplier()
			 * .getSmAppl().getSmPersonnels(); if (null != personnelsList) { for
			 * (SmPersonnel smPersonnel : personnelsList) {
			 * getManagedBean().setPersonnel(smPersonnel); } }
			 */
			SmCompanyBasic smCompanyBasic = resDTO.getSmSupplier().getSmAppl()
					.getSmCompanyBasic();
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");
					getManagedBean().setSupplierBranch("HQ");
					getManagedBean().setSupplierAddress(
							smCompanyAddress.getSmAddress());
					// getManagedBean().setSupplierContact(getManagedBean().getPersonnel().getName());
					// getManagedBean().setSupplierEmail(getManagedBean().getPersonnel().getEmail());
					break;
				}
			}
		} finally {
			qtSupplierSO = null;
		}
	}

	public void retriveOneTierCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedTechChecklist() == null) {
			getManagedBean().setMarkDeletedTechChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				// List<ScQtCheckList> localcheckList = new
				// ArrayList<ScQtCheckList>();
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getScQt());
				// reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				resDTO = specSo.retrieveOneTierCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2techProposal(
						resDTO.getSpecResultBean().getResultList());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2financeProposalList());
			}
		} finally {
			specSo = null;
		}
	}

}
