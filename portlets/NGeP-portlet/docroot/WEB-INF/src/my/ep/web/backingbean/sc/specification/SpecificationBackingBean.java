/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright  2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.specification;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.supplier.IPanelSupplierSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.CompositeValidityConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.BpmConstants;
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
import my.ep.dto.pm.common.uom.UomReqDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScFinancialUserScoringSchemaAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
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
import my.ep.persistence.entity.sc.quationtender.ScTechnicalUserScoringSchemaAttachment;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecEstimation;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.CreateQtBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.SampleQuestionBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.constant.sc.TreeNodeConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.persistence.indirection.IndirectList;
import org.primefaces.component.menuitem.MenuItem;//#25138 Reendorsed 
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;


import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.util.PortalUtil;
//#29732 Start - reOpen Fix
//#29732 End - reOpen Fix
/**
 * The Class SpecificationBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "specificationBackingBean")
@ViewScoped
public class SpecificationBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> implements Serializable {

	/**
	 * The serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/* 25633 spec Information */
	private static List<SpecInfo> specInfo;
	private static Map<Long, PmUom> uomMap = new HashMap<Long, PmUom>();
	private static Map<Long, PmParameter> PmParameterMap = new HashMap<Long, PmParameter>();
	
	/**
	 * The scoreType String
	 */
	private String scoreTypeN = ""; //#25138 - Reendorsed
	
	/** The search circular. */
	private IPagingDataModel<CtCircularVer> searchCircular = new SearchCircularPagingDataModel();

	/** The template spec create. */
	private IPagingDataModel<TplSpecMaster> templateSpecCreate = new CreateTemplateSpecPagingDataModel();

	/** The search delivery address. */
	// private IPagingDataModel<PmAddressType> searchDeliveryAddress = new
	// DeliveryPagingDataModel();

	/** The log. */
	private static Logger log = Logger
			.getLogger(SpecificationBackingBean.class);
	// private List<SpecificationCompilanceBean> selectedCompilenceList;
	/** The count. */
	private int count;

	/** The selct value. */
	private String selctValue;

	/** The current tab. */
	private int currentTab;

	/** The output tab. */
	private int outputTab;

	/** The text before sign. */
	private String textBeforeSign;

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	//11104-Reendorse
	public static final String TECHNICAL_CHECKLIST_SAMPLE_MAIN="/xhtml/sc/specification/specificationCommittee/technicalCheckListSampleMain.xhtml?faces-redirect=true";	
	public static final String  TECHNICAL_CHECKLIST_SAMPLE_ITEM_VIEW="/xhtml/sc/specification/specificationCommittee/technicalCheckListSampleItemView.xhtml?faces-redirect=true";
	public static final String SPECIFICATION_CHECKLIST_MAIN_PAGE="/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";//11104-Reendorse

	// Bug #35219@Syazani 07/11/2023 -- Start
	/** Temporary ID for a new checklist row */
	private int tempNewChecklistIndex = 0;
	// Bug #35219@Syazani 07/11/2023 -- End

	/**
	 * Instantiates a new specification backing bean.
	 */
	public SpecificationBackingBean() {

		log.debug("@@@@@@@@@@" + this.getManagedBean());
	}

	/**
	 * Secretary submit task.Event handle specification Committee send remainder
	 * to all committee members.
	 */
	public void secretarySubmitTask() {
		ICommitteSO committeSO = null;

		try {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);

			reqDTO.setSubject(this.getManagedBean().getSubject());
			reqDTO.setBodyText(this.getManagedBean().getMessage());
			committeSO.sendCommitteEndorseNotification(reqDTO);

		} finally {
			committeSO = null;
		}
	}

	/**
	 * Save specification committee dtls.save the specification committee
	 * details.
	 */
	public void saveSpecificationCommitteeDtls() {

		try {
			validatePrepareQtChecklist();

			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.specification.details.for"), this
					.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
		} finally {

		}
	}

	/**
	 * Construct fin specs n socre.set socreweightage for all financial specs.
	 * 
	 * @param scQtCheckList
	 *            (scQtCheckList)
	 * @return List<ScQtCheckList>
	 */
	private List<ScQtCheckList> constructFinSpecsNSocre(
			ScQtCheckList scQtCheckList) {
		IQTSpecSO	specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);//34852_start
		SpecificationReqDTO specReqDto=new SpecificationReqDTO();
		specReqDto.setScQt(this.getManagedBean().getQt());
		this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(specReqDto));//34852_End
		List<ScQtCheckList> finSpecCheckLists = this.getManagedBean()
				.getFinSpecList();
		if (null != finSpecCheckLists) {
			for (ScQtCheckList scQtCheckList2 : finSpecCheckLists) {

				if (null != scQtCheckList.getQtScoreWeightage()) {

					List<ScQtScoreOfferPrice> offerPrice = scQtCheckList
							.getQtScoreWeightage().getScQtScoreOfferPrices();					
					//#25138 Reendorsed - start
					if (this.getManagedBean().getQt().isZonal() && this.getManagedBean().getWipStatusMax()>SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {//34852_End
						if (null == offerPrice || offerPrice.isEmpty()) {
							offerPrice = new ArrayList<ScQtScoreOfferPrice>();
							if (null != this.getManagedBean()
									.getOfferPricePackageList()) {
								offerPrice.addAll(this.getManagedBean()
										.getOfferPricePackageList());
							}

							scQtCheckList.getQtScoreWeightage()
									.setScQtScoreOfferPrices(offerPrice);
						}
						else {
							List<ScQtScoreOfferPrice> otherofferpriceofferPrice = scQtCheckList
									.getQtScoreWeightage()
									.getScQtScoreOfferPrices();
							
							if (null != otherofferpriceofferPrice
									&& !otherofferpriceofferPrice.isEmpty() 
									// #25138 - 31578 - Start.
									&& CollectionUtils.isNotEmpty(this.getManagedBean().getOfferPricePackageList())
									// #25138 - 31578 - End.
									) {
								
								otherofferpriceofferPrice.addAll(this
										.getManagedBean()
										.getOfferPricePackageList());
								
								List<ScQtScoreOfferPrice> listWithDuplicates = this
										.getManagedBean()
										.getOfferPricePackageList();
						        Set<ScQtScoreOfferPrice> set = new LinkedHashSet<ScQtScoreOfferPrice>(listWithDuplicates);
						        List<ScQtScoreOfferPrice> listWithoutDuplicates = new ArrayList<ScQtScoreOfferPrice>(set);
								
						        scQtCheckList.getQtScoreWeightage()
										.setScQtScoreOfferPrices(
												listWithoutDuplicates);
						        //====#31913 bug related to #25138 ==== start
						        int i = 0;
								for(ScQtScoreOfferPrice price : scQtCheckList.getQtScoreWeightage().getScQtScoreOfferPrices()){
									
									ScQtScoreOfferPrice zonelist = listWithoutDuplicates.get(i);
									price.setTplSpecZoneDtl(zonelist.getTplSpecZoneDtl());
									i++;
								}
								//====#31913 bug related to #25138 ==== end 
						        this.getManagedBean().setOfferPricePackageList(listWithoutDuplicates);
						        System.out.println("offerList===="+this.getManagedBean()
						        		.getOfferPricePackageList().size());
							}
						}
						
					}else{
						if (null == offerPrice || offerPrice.isEmpty()) {
							offerPrice = new ArrayList<ScQtScoreOfferPrice>();
							if (null != this.getManagedBean()
									.getOfferPricePackage()) {
								offerPrice.add(this.getManagedBean()
										.getOfferPricePackage());
							}

							scQtCheckList.getQtScoreWeightage()
									.setScQtScoreOfferPrices(offerPrice);
						} else {
							List<ScQtScoreOfferPrice> otherofferpriceofferPrice = scQtCheckList
									.getQtScoreWeightage()
									.getScQtScoreOfferPrices();
							if (null != otherofferpriceofferPrice
									&& !otherofferpriceofferPrice.isEmpty() ) {
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

								}
								scQtCheckList.getQtScoreWeightage()
										.setScQtScoreOfferPrices(
												otherofferpriceofferPrice);
							}
						}
					}
					//#25138 Reendorsed - end
					scQtCheckList2.setQtScoreWeightage(scQtCheckList
							.getQtScoreWeightage());
				}
			}
		}
		return finSpecCheckLists;
	}

	/**
	 * Reset circular.Event handle to reset all the circular search information.
	 */
	public void resetCircular() {
		getManagedBean().setCtSearchBean(new CtCircularSearchBean());
		this.getManagedBean().setCircularType("");
		this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
		this.getManagedBean().setCircularVer(null);
		searchCircular.reloadData();
	}

	/**
	 * Committee back action.Event handle navigate to specfication committee
	 * Menu page.
	 * 
	 * @return the string
	 */
	public String committeeBackAction() {
		// this.getManagedBean().setScQtCheckSt2techProposal(null);
		// retriveTechnicalCheckList();
		//1873 - start - note 97
		if(CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())
				&& this.getManagedBean().getTempScQtChecklist()!=null
				&& this.getManagedBean().getScQtChecklist()!=null
				&& SourcingConstants.TECHNICAL_DOCTYPE.equals(this.getManagedBean().getScQtChecklist().getDocCategory())){
			ScQtCheckList tempChecklist = this.getManagedBean().getTempScQtChecklist();
			int index = 0;
			for(ScQtCheckList checklist: this.getManagedBean().getScQtCheckSt2techProposal()){
				if(checklist.getQtChecklistId()!=null 
						&& tempChecklist.getQtChecklistId().equals(checklist.getQtChecklistId())
						&& QTConstants.TEMPLETE_SPEC_CODE.equals(checklist.getDefinationType().getParameterCode())){
					tempChecklist.setCurrentlySelected(false);
					this.getManagedBean().getScQtCheckSt2techProposal().set(index, tempChecklist);
					this.getManagedBean().setTempScQtChecklist(null);
					break;
				}
				index++;
			}

			ScQtCheckList finChecklist = this.getManagedBean().getFinTempScQtChecklist();
			if(CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2financeProposalList()) && finChecklist!=null){
				int finIndex = 0;
				for(ScQtCheckList checklist: this.getManagedBean().getScQtCheckSt2financeProposalList()){
					if(finChecklist.getQtChecklistId()!=null 
							&& checklist.getQtChecklistId().equals(finChecklist.getQtChecklistId())){
						this.getManagedBean().getScQtCheckSt2financeProposalList().set(finIndex, finChecklist);
						this.getManagedBean().setFinTempScQtChecklist(null);
						break;
					}
					finIndex++;
				}
			}
		}
		//1873 - end- note 97
		this.getManagedBean().setChecklistScoreType(null);
		this.getManagedBean().setCompanyScoreType(null);
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setCheckListScore(null);
		this.getManagedBean().setScoringForYears(null);
		this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setIndex(-1);

		return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";

	}

	/**
	 * Business owner template spec.navigate to Template spec Search in
	 * Businness owner.
	 * 
	 * @return the string
	 */
	public String businessOwnerTemplateSpec() {
		return "/xhtml/sc/boSctemplatespec/createTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * Retrive templatespec.navigate to Template spec Search in Specification
	 * committee.
	 * 
	 * @return the string
	 */
	public String retriveTemplatespec() {
		return "/xhtml/sc/boSctemplatespec/createCommitteeTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * Adds the new category action.added empty row for category codes
	 * datatable.
	 * 
	 * @param pmCategoryL3
	 *            the pmCategoryL3 instance
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		List<ScQtCategory> qtCategoryList = this.getManagedBean()
				.getQtCategoryList();

		if (qtCategoryList == null) {
			qtCategoryList = new ArrayList<ScQtCategory>();
			this.getManagedBean().setQtCategoryList(qtCategoryList);
		}

		validateDuplicateCategoryCode(pmCategoryL3, qtCategoryList);

		PmCategoryL2 pmCategoryL2 = pmCategoryL3.getPmCategoryL2();
		ScQtCategory category = new ScQtCategory();
		category.setScQt(this.getManagedBean().getQt());
		category.setPmCategoryL1(pmCategoryL2.getPmCategoryL1());
		category.setPmCategoryL2(pmCategoryL2);
		category.setPmCategoryL3(pmCategoryL3);
		category.setPmCategoryL1Name(pmCategoryL2.getPmCategoryL1()
				.getCategoryName());
		category.setPmCategoryL2Name(pmCategoryL2.getCategoryName());
		category.setPmCategoryL3Name(pmCategoryL3.getCategoryName());

		qtCategoryList.add(category);
	}

	/**
	 * Delete category codes.delete row from the category codes datatable.
	 */
	public void deleteCategoryCodes() {
		List<ScQtCategory> removeList = this.getManagedBean()
				.getTobeRemoveCategoryCodes();
		if (null == removeList) {
			removeList = new ArrayList<ScQtCategory>();
		}
		List<ScQtCategory> openList = this.getManagedBean().getQtCategoryList();

		if (null == openList) {
			openList = new ArrayList<ScQtCategory>();
		}

		List<ScQtCategory> openSelectList = this.getManagedBean()
				.getSuppCriteriaopenSelectList();
		if (null == openSelectList || openSelectList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_760146);
		}

		for (ScQtCategory category : openSelectList) {
			openList.remove(category);
			removeList.add(category);
		}
		this.getManagedBean().getQt().setScQtCategorys(openList);
		this.getManagedBean().setTobeRemoveCategoryCodes(removeList);

	}

	/**
	 * Checktemplate spec status.Check the Template Spec is Exist or not in
	 * Specification committee.
	 * 
	 * @param scqt
	 *            (Scqt scqt).
	 */
	private void checktemplateSpecStatus(ScQt scqt) {
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null != techCheckList && !techCheckList.isEmpty()) {
			isTemplateSpec(techCheckList, scqt);
			isTemplateSpecStatus(techCheckList, scqt);

		} else {
			//1873 note 195 start
			if(CollectionUtils.isEmpty(this.getManagedBean().getMarkDeletedTechChecklist())){
				retriveTechnicalCheckList();
			}
			//1873 note 195 end
			techCheckList = this.getManagedBean().getScQtCheckSt2techProposal();

			if (null == techCheckList || techCheckList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760138);

			} else {
				isTemplateSpec(techCheckList, scqt);
				isTemplateSpecStatus(techCheckList, scqt);
			}
		}

	}

	/**
	 * Checks if is template spec.
	 * 
	 * @param techCheckList
	 *            (List<ScQtCheckList>)
	 * @param scqt
	 *            (ScQt scqt)
	 */
	private void isTemplateSpec(List<ScQtCheckList> techCheckList, ScQt scqt) {
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
			throw new EpException(MessageConst.ERROR_760138);
		}
		this.getManagedBean().setTechnicalSpecCount(count);
	}

	/**
	 * Checks if is template spec status.check the Technical Spec Status is
	 * pending or completed.
	 * 
	 * @param techCheckList
	 *            the tech check list
	 * @param scqt
	 *            the scqt
	 */
	private void isTemplateSpecStatus(List<ScQtCheckList> techCheckList,
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
			//#28784
			if(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())){
				if (checkList.getPmStatus().getStatusId()
						.equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
					this.getManagedBean().setActiveIndex(2);
					throw new EpException(MessageConst.ERROR_760542);
				}
				
			}
			else if(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {//#11104-start
				if (QTStatusConst.TS_PENDING_SUBMISSION 
						.equals(checkList.getPmStatus().getStatusId())) {
					this.getManagedBean().setActiveIndex(2);
					throw new EpException(MessageConst.ERROR_760521);
				}
			}//#11104-End

		}
		if (count >= 1) {
			this.getManagedBean().setActiveIndex(2);
			throw new EpException(MessageConst.ERROR_760152);
		}

	}

	/**
	 * Check financial spec status.Check the Financial Spec Status is Pending or
	 * Completed.
	 * 
	 * @param scqt
	 *            (ScQt scqt).
	 */
	private void checkFinancialSpecStatus(ScQt scqt) {
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList || financialCheckList.isEmpty()) {
			retriveFinancialCheckList();
		}
		// retriveFinancialScoring();
		//#25138 Reendorsed - set max score for wip data - start
//		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
//				.equalsIgnoreCase(financialCheckList.get(0).getDefinationType()
//						.getParameterCode())){
//			if(this.getManagedBean().getQt().isZonal()){
//				checkSpecZoneQtyMaxScore(financialCheckList.get(0));
//					
//			}
//			
//		}
		//#25138 Reendorsed - set max score for wip data - end
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
	
	//Bug_28291
	/**
	 * Check financial and technical document mechanism validation
	 
	 * @param scqt
	 *            (ScQt scqt).
	 */
	private void checkTechnicalAndFinancialSpec(ScQt scqt) {
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null == technicalCheckList || technicalCheckList.isEmpty()) {
			retriveTechnicalCheckList();
			technicalCheckList = this.getManagedBean().getScQtCheckSt2techProposal();//#28784
		}
		boolean demoChecklistAvail = false;//#28784
		boolean throwErorrMechaMistmatch = false; // Bug #34065@Syazani 01/11/2022
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE); // Bug #34065@Syazani 01/11/2022
		if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
			for (ScQtCheckList checkList : technicalCheckList) {
				if (ParameterTypeConst.DOCUMENT_DEFINITION_PTJ_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode()) && ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW
								.equalsIgnoreCase(checkList.getAction().getParameterCode()) &&
								checkList.isScoringReq()) {
						this.getManagedBean().setActiveIndex(2);
						throw new EpException(MessageConst.ERROR_760513, new String[]{checkList.getTitle()});
				}
				//Bug #30637
				if (ParameterTypeConst.DOCUMENT_DEFINITION_SUPPLIER_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType().getParameterCode()) && 
					(ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW
							.equalsIgnoreCase(checkList.getAction().getParameterCode()) ||	
					ParameterTypeConst.SC_SUPPLIER_ACTION_DOWN_UPLOAD
						.equalsIgnoreCase(checkList.getAction().getParameterCode()))) {
						this.getManagedBean().setActiveIndex(2);
						throw new EpException(MessageConst.ERROR_760513, new String[]{checkList.getTitle()});
				}
				//#28784 - Start				
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO.equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
						demoChecklistAvail = true;
						if (checkList.getPmStatus().getStatusId().equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
							throw new EpException(MessageConst.ERROR_760542);
						}
					}
				
				// Bug #34065@Syazani 01/11/2022 -- Start
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterId().equals(checkList.getDefinationType().getParameterId())) {
						if (!paramDesc.getPmParameter().getParameterCode().equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
							if (checkList.getPmDocument() == null) {
								if (paramDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
									technicalCheckListTypeChange(technicalCheckList.indexOf(checkList), technicalCheckList);
									throwErorrMechaMistmatch = true;
								} else if (paramDesc.getPmParameter().getParameterCode()
										.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION)) {
									technicalCheckListTypeChange(technicalCheckList.indexOf(checkList), technicalCheckList);
								}
							} else {
								setDefTypeAndActionValues(checkList);
								technicalCheckListTypeChange(technicalCheckList.indexOf(checkList), technicalCheckList);
							}
						}
						break;
					}
				}
				// Bug #34065@Syazani 01/11/2022 -- End
				}			
		}
		
		if (this.getManagedBean().getQt().isDemoRequired() && ParameterConst.EVALUATION_TYPES_TWO_TIER
				.equalsIgnoreCase(this.getManagedBean().getQt().getEvaluationType()) && !demoChecklistAvail && technicalCheckList != null) {
			throw new EpException(MessageConst.ERROR_760542);
		}//#28784 - End
		
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		HashMap<Integer, Integer> totalGroupId = new HashMap<Integer, Integer>() ; // 31658
		if (null == financialCheckList || financialCheckList.isEmpty()) {
			retriveFinancialCheckList();
		}

		if (null != financialCheckList && !financialCheckList.isEmpty()) {
			for (ScQtCheckList checkList : financialCheckList) {
				if (ParameterTypeConst.DOCUMENT_DEFINITION_PTJ_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode()) && ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW
								.equalsIgnoreCase(checkList.getAction().getParameterCode()) &&
								checkList.isScoringReq()) {
						this.getManagedBean().setActiveIndex(3);
						throw new EpException(MessageConst.ERROR_760513, new String[]{checkList.getTitle()});
				}
				//Bug #30637
				if (ParameterTypeConst.DOCUMENT_DEFINITION_SUPPLIER_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType().getParameterCode()) && 
					(ParameterTypeConst.SC_SUPPLIER_ACTION_VIEW
							.equalsIgnoreCase(checkList.getAction().getParameterCode()) ||	
					ParameterTypeConst.SC_SUPPLIER_ACTION_DOWN_UPLOAD
						.equalsIgnoreCase(checkList.getAction().getParameterCode()))) {
						this.getManagedBean().setActiveIndex(3);
						throw new EpException(MessageConst.ERROR_760513, new String[]{checkList.getTitle()});
				}
			//20210914@haziq - 31658 - Start
				
				if (checkList.getPmDocument() != null && checkList.getPmDocument().getDocGroupId() != null){
				
				   Integer groupId = checkList.getPmDocument().getDocGroupId();
                   Integer total = totalGroupId.get(groupId);

					if (total == null){
						totalGroupId.put(groupId, 1);
					}
					else{
						total = total +1;
						totalGroupId.remove(groupId);
						totalGroupId.put(groupId, total);
					}
				}
				
				// Bug #34065@Syazani 01/11/2022 -- Start
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterId().equals(checkList.getDefinationType().getParameterId())) {
						if (!paramDesc.getPmParameter().getParameterCode().equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
							if (checkList.getPmDocument() == null) {
								if (paramDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)) {
									financialCheckListTypeChange(financialCheckList.indexOf(checkList), financialCheckList);
									throwErorrMechaMistmatch = true;
								} else if (paramDesc.getPmParameter().getParameterCode()
										.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION)) {
									financialCheckListTypeChange(financialCheckList.indexOf(checkList), financialCheckList);
								}
							} else {
								setDefTypeAndActionValues(checkList);
								financialCheckListTypeChange(financialCheckList.indexOf(checkList), financialCheckList);
							}
						}
						break;
					}
				}
				// Bug #34065@Syazani 01/11/2022 -- End
			}
			
			for (Map.Entry<Integer, Integer> entry : totalGroupId.entrySet()) {
				boolean isInsertPair = false ;	
				Integer groupId = entry.getKey();
				Integer total = totalGroupId.get(groupId);
				
				if ( total == 1 ){
					retriveFinancialStandardCheckList();
					isInsertPair = true;
					for ( PmDocument document : this.getManagedBean().getStageTwoFinComplianceList()){
						if (document.getDocGroupId() != null && document.getDocGroupId().equals(groupId)){
								this.getManagedBean().setFinStandaraChecklist(true);
								ScQtCheckList scqtlist = createSpecList("2");
								scqtlist.setTitle(document.getDocName());
								scqtlist.setDocCategory(QTConstants.FINANCIAL_DOC_TYPE);
								scqtlist.setScoringReq(document.isScoringReq());
								scqtlist.setDefinationType(document.getDocDefinitionTypeCode());
								scqtlist.setAction(document.getChecklistActionCode());
								scqtlist.setManditory(false);
								scqtlist.setPmDocument(document);
								if (null == document.getDisplaySeq()) {
									scqtlist.setChecklistSeq(formatChecklistSequence(
											SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
											0));
								} else {
									scqtlist.setChecklistSeq(formatChecklistSequence(
											SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
											new Integer(document.getDisplaySeq())));
								}
								scqtlist.setScQt(this.getManagedBean().getQt());

								setUserDetails(scqtlist);
							
								if (null != scqtlist.getDefinationType()
										&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
												.equalsIgnoreCase(scqtlist
														.getDefinationType()
														.getParameterCode())) {
									Long statusId = null;
									if (!scqtlist.isScoringReq()) {
										statusId = QTStatusConst.TS_COMPLETED;
									} else {
										statusId = QTStatusConst.TS_PENDING_SUBMISSION;
									}
									scqtlist.setPmStatus(getPmStatusByStatusId(statusId));
								} else {
									scqtlist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
								}

								scqtlist.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
								financialCheckList.add(scqtlist);
						break;
						}
					}	
				}
				
				if (isInsertPair){
					for ( ScQtCheckList scQtCheckList :financialCheckList){						
						if (  scQtCheckList.getPmDocument() != null &&
							  scQtCheckList.getPmDocument().getDocGroupId() != null && 
							  scQtCheckList.getPmDocument().getDocGroupId().equals(groupId) &&
							  scQtCheckList.getPmStatus().getStatusId().equals(QTStatusConst.TS_COMPLETED)&&
							  ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM.equalsIgnoreCase(scQtCheckList.getDefinationType().getParameterCode())){
							scQtCheckList.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));	
						}
					}
					getManagedBean().setScQtCheckSt2financeProposalList(financialCheckList);
					checklistSorting(getManagedBean().getScQtCheckSt2financeProposalList());
				}
			}
			FacesScopes facesScopes = new FacesScopes();
				for (ScQtCheckList checkList : this.getManagedBean()
						.getScQtCheckSt2financeProposalList()) {
				
					if (checkList.isScoringReq()) {
						// #33068 Farid Start
						ScQtScoreWeightage scoreWeightage = checkList
								.getQtScoreWeightage();
						// #33068 Farid End
						String checklistScoring =  ResourceBundleUtils.getMessage(
								"lbl.sc.checklist.scoring.completed", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								facesScopes.getUserLocale());
						if (checkList.getPmStatus().getStatusId().equals(QTStatusConst.TS_PENDING_SUBMISSION) &&
								SourcingConstants.FINANCIAL_DOCTYPE.equalsIgnoreCase(checkList.getDocCategory())) {
							throw new EpException(MessageConst.ERROR_760267,
							new String[] { checkList.getTitle(),checklistScoring });
						}
						// #33068 Farid Start
						//Redirect to tab financial scoring based on error msg after failed to submit
						if (null == scoreWeightage
								|| null == scoreWeightage.getScore()
								|| scoreWeightage.getScore().intValue() <= 0) {
							if (SourcingConstants.FINANCIAL_DOCTYPE
									.equalsIgnoreCase(checkList.getDocCategory())) { 
										this.getManagedBean().setActiveIndex(5);
										if (!this.getManagedBean().isReadOnly()) {
											checktemplateSpecStatus(this.getManagedBean().getQt());
											checkFinancialSpecStatus(this.getManagedBean().getQt());
										}
										retriveFinancialScoring(null);	// Bug #35688@Syazani 10/07/2024
										
								throw new EpException(MessageConst.ERROR_760267,
										new String[] { checkList.getTitle(),
												checklistScoring });
							}
						}
						// End #33068
					}
				}
		//20210914@haziq - 31658 - End
		}
		
		// Bug #35219@Syazani 07/11/2023 -- Start
		if (throwErorrMechaMistmatch) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.spec.supplier.action", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });
		}
		// Bug #35219@Syazani 07/11/2023 -- End

	}

	/**
	 * Checks if is financial spec.Check Financial Specs is Existing or not.
	 * 
	 * @param techCheckList
	 *            the tech check list
	 */
	private void isFinancialSpec(List<ScQtCheckList> techCheckList) {
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

	//1873 - start - note 52
	private boolean isTechnicalTemplatesEdited(){
		List<ScQtCheckList> technicalChecklists = this.getManagedBean().getScQtCheckSt2techProposal();
		List<ScQtCheckList> financialChecklists = this.getManagedBean().getScQtCheckSt2financeProposalList();
		if(CollectionUtils.isNotEmpty(technicalChecklists)){
			for(ScQtCheckList checklist: technicalChecklists){
				boolean edited = checklist.isEdited()==null ? false:  checklist.isEdited();
				if(checklist.getDefinationType().getParameterCode().equals(QTConstants.TEMPLETE_SPEC_CODE) && edited){
					if(CollectionUtils.isNotEmpty(financialChecklists)){
						for(ScQtCheckList finChecklist: financialChecklists){
							if(checklist.getTitle().trim().equalsIgnoreCase(finChecklist.getTitle().trim()) && finChecklist.getDefinationType().getParameterCode().equals(QTConstants.TEMPLETE_SPEC_CODE)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	//1873 - end - note 52
	/**
	 * Qt checklist tab change.Event handle change the menu in Specification
	 * Committee .accoring to index it will display menu information.
	 * 
	 * @param activeIndex
	 *            (int activeIndex).
	 */
	private int nextIndex = 0;//32204
	public void qtChecklistTabChange(int activeIndex) {

		int previousIndex = this.getManagedBean().getActiveIndex();
		this.getManagedBean().setCheckListYesIndex(activeIndex);

		IQTSpecSO specSo = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		specReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		//#34656 set scqt
		specReqDTO.setScQt(this.getManagedBean().getQt());
		this.getManagedBean().setTechnicalOtherCheckList(true);	//1873 - note 52
		boolean financialOtherCheckList = true;
		boolean isFromTabSpecCommitteDtlsLoad = true; //29732 - use for load only & avoiding trigger at save/update function.
		switch (previousIndex) {
		case 0:
			/*
			 * if (this.getManagedBean().isReadOnly()) { if ((null !=
			 * this.getManagedBean().getQt() .getFulfilmentType() && null ==
			 * this.getManagedBean()
			 * .getQt().getFulfilmentType().getParameterId()) || (null ==
			 * this.getManagedBean().getQt() .getContractDuration())) { throw
			 * new EpException(MessageConst.ERROR_760134);
			 * 
			 * } }
			 */
			if (!this.getManagedBean().isReadOnly()) {
				//Bug #35688@Syazani 10/07/2024 -- Start
				specReqDTO
						.setScQt(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_GENERAL_INFORMATION,isFromTabSpecCommitteDtlsLoad).getScQt());
				//Bug #35688@Syazani 10/07/2024 -- End #29732 adding param
				specSo.validateSpecCommitteGeneralInfo(specReqDTO);
			}
			if (this.getManagedBean().isZoneSpecUpdate()) {
				ZonesUpdateWarningMessage(this.getManagedBean()
						.isZonesWarnMessageUpdate());
				validatePrepareQtChecklist();
			}
			break;
		case 1:
			//Bug #35688@Syazani 10/07/2024 -- Start
			specReqDTO.setScQt(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_SUPPLIER_QUALIFICATION_CRITERIA,isFromTabSpecCommitteDtlsLoad)
					.getScQt());
			//Bug #35688@Syazani 10/07/2024 -- End #29732 adding param
			if (!this.getManagedBean().isMaintanceFlow()) {
				specSo.validateSupplierQulificationInfo(specReqDTO);
			}
			break;

		case 2:
			// ScQt scQtTechChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_TECHNICAL_CHECKLIST,isFromTabSpecCommitteDtlsLoad));	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			// checktemplateSpecStatus(scQtTechChecklist);
			//#34656 move to top
			specReqDTO.setScQt(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_TECHNICAL_CHECKLIST,isFromTabSpecCommitteDtlsLoad).getScQt());	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			//1873 - start- note 52
			String taskName = this.getManagedBean().getTaskName();
			String[] allowedTaskName = new String[]{
				SCProcessNameConst.FINALISE_SCORING,
				SCProcessNameConst.UPDATE_SCORING,
				SCProcessNameConst.VIEW_SCORING,
				SCProcessNameConst.SPEC_MAINTENANCE_REVISE_SCORING //1873 note 149
			};
			//#34656 move validation position ,trigger before popup-start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 move validation position -end
			if (Arrays.asList(allowedTaskName).contains(taskName)
					|| (StringUtils.isEmpty(taskName) && !this.getManagedBean().isReadOnly())) {
				if(isTechnicalTemplatesEdited() && !this.getManagedBean().isFromSubmit()){
					RequestContext.getCurrentInstance().execute("alertFinancialTemplateIncompletePopup.show()");
					this.getManagedBean().setTechnicalOtherCheckList(false);
				}else{
					this.getManagedBean().setTechnicalOtherCheckList(validateTechnicalStandardCheckList());
				}
			}
			//1873 - end- note 52
			specSo.validateCheckList(specReqDTO);
			//#34656 adjust position (remove #31292)
			break;
		case 3:
			// ScQt scQtFinChecklist = constructSpecCommitteDtls().getScQt();
			specSo.validateCheckList(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_FINANCIAL_CHECKLIST,isFromTabSpecCommitteDtlsLoad));	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			// checktemplateSpecStatus(scQtFinChecklist);
			// checkFinancialSpecStatus(scQtFinChecklist);
			if (!this.getManagedBean().isReadOnly()) {
				financialOtherCheckList = validateFinancialStandardCheckList();
			}
			break;

		case 4:
			ScQt scQtTechScore = constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_TECHNICAL_SCORING,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			if (!this.getManagedBean().isReadOnly()) {
				checktemplateSpecStatus(scQtTechScore);
				checkFinancialSpecStatus(scQtTechScore);
			}
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
			specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			specReqDTO.setScQt(scQtTechScore);
			specSo.validateSCScoreSettings(specReqDTO);
			break;

		case 5:

			ScQt scQtFinScore = constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_FINANCIAL_SCORING,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			if (!this.getManagedBean().isReadOnly()) {
				checktemplateSpecStatus(scQtFinScore);
				checkFinancialSpecStatus(scQtFinScore);
			}
			List<ScQtCheckList> financialScoring = this.getManagedBean()
					.getFinancialScoring();
			ScQtScoreSetting financialScoreSetting = this.getManagedBean()
					.getFinancialScoreSetting();

			if (null != financialScoreSetting
					&& financialScoreSetting.getSettingType() == 2) {
				specReqDTO.setScoreSetting(financialScoreSetting);
			}

			specReqDTO.setCheckLists(financialScoring);
			specReqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			specReqDTO.setScQt(scQtFinScore);
			specSo.validateSCScoreSettings(specReqDTO);

			break;
		case 6:

			ScQt scQtRecommendation = constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_OVERALL_SCORING,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			if (!this.getManagedBean().isReadOnly()) {
				checktemplateSpecStatus(scQtRecommendation);
				checkFinancialSpecStatus(scQtRecommendation);
			}
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
			scoreSetting = null;
			break;

		case 7:
			ScQt deliveryAddress = constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_DELIVERY_ADDRESS,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			// checktemplateSpecStatus(deliveryAddress);
			// checkFinancialSpecStatus(deliveryAddress);

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

			break;

			//#29474 - start
			case 8:
				specReqDTO.setScQt(constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_PAYMENT_DEDUCTION,isFromTabSpecCommitteDtlsLoad).getScQt());	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				specSo.validateDeductionDtls(specReqDTO);
				break;

			case 9:

				ScQt fulfilMentDtls = constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_FULFILMENT_SCHEDULE,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				// checktemplateSpecStatus(fulfilMentDtls);
				// checkFinancialSpecStatus(fulfilMentDtls);

				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
						.getManagedBean().getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())) {
					specReqDTO.setScQt(fulfilMentDtls);
					specSo.validateSCFulfillmentSchedules(specReqDTO);
				}

				break;

			case 10://#29474 - end
				constructSpecCommitteDtls("Prev" + QTConstants.TAB_SC_SPECIFICATION_ENDORSEMENT_STATUS,isFromTabSpecCommitteDtlsLoad).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
			// checktemplateSpecStatus(committeeEndorsment);
			// checkFinancialSpecStatus(committeeEndorsment);
			break;

		}
		switch (activeIndex) {
		case 0:

			retriveSpecdetails(this.getManagedBean().getQt().getQtId());
			break;
		case 1:
			retriveSupplierQualificationCriteria("SC");
			break;

		case 2:
			//1873 note 182 start
			if(CollectionUtils.isEmpty(this.getManagedBean().getMarkDeletedTechChecklist())){
				retriveTechnicalCheckList();
			}
			//1873 note 182 end
			else if(this.getManagedBean().getQt().isSampleReq()){//#11104-start
					checksampleChecklist(this.getManagedBean()
							.getScQtCheckSt2techProposal());
			}//#11104-End
			
			// #28784 - Demo WIP
			if (this.getManagedBean().isNewVersion()) {
				addDemoTechnicalCheckList(); // #28784
			}
			break;
		case 3:
			//1873 note 195 start
			if(CollectionUtils.isEmpty(this.getManagedBean().getMarkDeletedTechChecklist())){
				retriveTechnicalCheckList();//1873 note 86
			}
			//1873 note 195 end
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			retriveFinancialCheckList();
			// checktemplateSpecStatus(this.getManagedBean().getQt());
			break;

		case 4:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			if (!this.getManagedBean().isReadOnly() && this.getManagedBean().isTechnicalOtherCheckList()) {//1873 note 149
				checktemplateSpecStatus(this.getManagedBean().getQt());
				checkFinancialSpecStatus(this.getManagedBean().getQt());
			}
			retriveTechnicalScoring();
			break;

		case 5:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			if (!this.getManagedBean().isReadOnly() && this.getManagedBean().isTechnicalOtherCheckList()) {//1873 note 149
				checktemplateSpecStatus(this.getManagedBean().getQt());
				checkFinancialSpecStatus(this.getManagedBean().getQt());
			}
			retriveFinancialScoring("tabChange");	// Bug #35688@Syazani 10/07/2024
			break;

		case 6:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			retriveRecommendationDetails();
			if (!this.getManagedBean().isReadOnly() && this.getManagedBean().isTechnicalOtherCheckList()) {//1873 note 149
				checktemplateSpecStatus(this.getManagedBean().getQt());
				checkFinancialSpecStatus(this.getManagedBean().getQt());
			}
			break;

		case 7:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			retriveDeliveryDetails();
			// checktemplateSpecStatus(this.getManagedBean().getQt());
			// checkFinancialSpecStatus(this.getManagedBean().getQt());
			break;

			//#29474 - start
		case 8:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			retrieveDeductionPaymentTabDtl();
			break;

		case 9:
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType())
					|| ParameterConst.FULFILMENT_TYPE_ONE_OFF
							.equalsIgnoreCase(this.getManagedBean()
									.getFulfilmentType()) || ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED
									.equalsIgnoreCase(this.getManagedBean()
											.getFulfilmentType()) || ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN
											.equalsIgnoreCase(this.getManagedBean()
													.getFulfilmentType())) {//25635&33723&25725
				retriveFulfilmentDtls();
				// checktemplateSpecStatus(this.getManagedBean().getQt());
				// checkFinancialSpecStatus(this.getManagedBean().getQt());
			}
			break;

		case 10:// #29474 - end
			//#34656 validate - start
			specSo.validateSpecZoneQtyZero(specReqDTO);
			//#34656 validate - end
			retriveCommitteeEndorsment();
			// checktemplateSpecStatus(this.getManagedBean().getQt());
			// checkFinancialSpecStatus(this.getManagedBean().getQt());
			break;

		}
		if (getManagedBean().getTempIndex() < activeIndex) {
			getManagedBean().setTempIndex(activeIndex);
		}

		if (this.getManagedBean().getActiveIndex() == 2
				|| this.getManagedBean().getActiveIndex() == 3) {
			if ((this.getManagedBean().getActiveIndex() == 2 && this.getManagedBean().isTechnicalOtherCheckList())//1873 - start- note 52
					|| (this.getManagedBean().getActiveIndex() == 3 && financialOtherCheckList == true)) {
				this.getManagedBean().setActiveIndex(activeIndex);
			}
		} else {
			this.getManagedBean().setActiveIndex(activeIndex);
		}
		checkProcurementModeValue();
	}

	/**
	 * Load prepare spec committee details.Event handle click on submit button
	 * to retrieve all the Specification Committee menus information.
	 * 
	 * @param qtId
	 *            (Long qtId)
	 * @param docType
	 *            (String docType)
	 */
	private void loadPrepareSpecCommitteeDetails(Long qtId) {

		retriveSpecdetails(qtId);

		retriveSupplierQualificationCriteria("SC");
		retriveTechnicalCheckList();
		retriveTechnicalScoring();
		retriveFinancialCheckList();
		retriveFinancialScoring(null);	// Bug #35688@Syazani 10/07/2024
		retriveRecommendationDetails();
		retriveFulfilmentDtls();
		retriveDeliveryDetails();
		retriveCommitteeEndorsment();
		if (isQTStatusChecklistScoring()
				|| isQTStatusChecklistScoringRevision()) {
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
		}
		if (!this.getManagedBean().isReadOnly()) {
			// checktemplateSpecStatus(this.getManagedBean().getQt());
			// checkFinancialSpecStatus(this.getManagedBean().getQt());
			validateTechnicalPassingScoring(this.getManagedBean()
					.getTechnicalScoreSetting(),this.getManagedBean()
					.getTechnicalItemScoreSetting());
			// #21695 START FARID
			resetTechnicalWeightage(this.getManagedBean()
					.getTechnicalScoreSetting());
			validateTechnicalDemoPassingScoring(this.getManagedBean()
					.getDemoScoreSetting());
			technicalWeightageCalculate();
			technicalItemScoreCalculate(this.getManagedBean()
					.getTechnicalItemScoreSetting());
			// #21695 START FARID
			validateFinancialPackageItemPassingScoring(this.getManagedBean()
					.getFinancialScoreSetting(), this.getManagedBean()
					.getFinacialItemScoreSetting());
			resetFinancialWeightage(this.getManagedBean()
					.getFinancialScoreSetting());
			financialWeightageCalculate();
			financialItemScoreCalculate(this.getManagedBean()
					.getFinacialItemScoreSetting());
			calculateRecommendationPackageAndItemScores(this.getManagedBean()
					.getRecommendationPackageScore(), this.getManagedBean()
					.getRecommendationScore());
			validateTechnicalUserScoringAttachment(this.getManagedBean()
					.isTechnicalUserScoringSchema());

			validateFinancialUserScoringAttachment(this.getManagedBean()
					.isFinancialUserScoringSchema());
			setTechncialChecklistEvaluationStatus(this.getManagedBean()
					.getScQtCheckSt2techProposal());
			setFinancialChecklistEvaluationStatus(this.getManagedBean()
					.getScQtCheckSt2financeProposalList());
		}

		// this.getManagedBean().setActiveIndex(0);

	}

	/**
	 * Retrieve supplier qualification criteria.retrieve all the Supplier
	 * Qualification Criteria Menu information and display.
	 * 
	 * @param type
	 *            (String type)
	 */
	public void retriveSupplierQualificationCriteria(String type) {//1873 note 149

		log.debug("retriveBusinessOwnerQualificationCriteri - START");
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveSuppQualificationTabDtl();
	}

	/**
	 * Creates the circular item spec.Pnel item as a specfic create item as a
	 * template spec in technical specification menu.
	 * 
	 * @param panelItem
	 *            (CtPanelItem panelItem)
	 */
	private void createCircularItemSpec(List<CtPanelItem> panelItems) {
		IParameterSO parameterSo = null;
		try {
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster = new TplSpecMaster();
			String itemName = "";
			String panelCoverage = "";
			// if(CollectionUtils.isEmpty(this.getManagedBean().getPanelItemList())){
			// this.getManagedBean().setPanelItemList(panelItems);
			// }
			if (panelItems.size() == 1) {
				CtPanelItem item = panelItems.get(0);
				if (ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equals(item
						.getItemCategoryId().getParameterCode())) {
					//31499
					specMaster.setCtPanelItem(item);
					//31499
					String description = item.getCtPanelItemGroup()
							.getGroupName();
					itemName = description;
					panelCoverage = item.getPanelCoverageId()
							.getParameterCode();
					checkPanelItemIsZonalOrNonzonal(panelCoverage, item);
				} else if (ParameterConst.ITEM_CATEGORY_TYPE_GENERAL
						.equals(item.getItemCategoryId().getParameterCode())) {
					specMaster.setCtPanelItem(item);
					String description = item.getCtPanelItemGroup()
							.getGroupName();
					description = description.concat("-");
					itemName = description.concat(item.getItemName());
					panelCoverage = item.getPanelCoverageId()
							.getParameterCode();
					checkPanelItemIsZonalOrNonzonal(panelCoverage, item);

				}
				// System.out.println("====display concat itemname==="+itemName);
			} else {
				CtPanelItem item = panelItems.get(0);
				String description = item.getCtPanelItemGroup().getGroupName();
				itemName = description;
				panelCoverage = item.getPanelCoverageId().getParameterCode();
				checkPanelItemIsZonalOrNonzonal(panelCoverage, item);
				// System.out.println("====display concat itemname==="+itemName);
			}
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ScQtCheckList checkList = new ScQtCheckList();
			ScQtDoc scqtDocDest = new ScQtDoc();

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
					.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			checkList.setTitle(itemName);
			checkList.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
			checkList
					.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
			checkList.setScoringReq(this.getManagedBean().getQt()
					.isTechEvaluationReq());
			checkList.setAction(checkListactionresDTO.getPmParameter());
			checkList.setScQt(this.getManagedBean().getQt());
			checkList.setDefinationType(specTyperesDTO.getPmParameter());
			setUserDetails(checkList);
			checkList
					.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			checkList.setManditory(false);
			checkList.setRecordStatus(true);
			checkList.setScQtDoc(scqtDocDest);

			scqtDocDest.setScQt(this.getManagedBean().getQt());
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

			/*
			 * ParameterReqDTO itemTypeReqDTO = new ParameterReqDTO();
			 * itemTypeReqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
			 * itemTypeReqDTO
			 * .setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT);
			 * ParameterResDTO itemTypeResDTO = parameterSo
			 * .retrieveParameterByTypeAndCode(itemTypeReqDTO);
			 */

			ParameterReqDTO localGoodsReqDTO = new ParameterReqDTO();
			localGoodsReqDTO.setParameterType(ParameterTypeConst.ITEM_ORIGIN);
			localGoodsReqDTO.setParameterCode(ParameterConst.LOCAL_GOODS);
			ParameterResDTO localGoodsResDTO = parameterSo
					.retrieveParameterByTypeAndCode(localGoodsReqDTO);

			specMaster.setSpecName(itemName);
			specMaster.setSpecDesc(itemName);

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
			specMaster.setItemTypeCode(panelItems.get(0).getItemType());
			specMaster
					.setStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
			specMaster.setItemOriginCode(localGoodsResDTO.getPmParameter());
			specMaster.setQtyByZone(true);
			setUserDetails(specMaster);
			List<TplSpecQuestion> allquestionsList = new ArrayList<TplSpecQuestion>();
			int value = -1;
			for (CtPanelItem panelItem : panelItems) {
				if (allquestionsList.isEmpty()) {
					value = -1;
				} else {
					value = allquestionsList.size() - 1;
				}
				new TemplateSpecBackingBean().addQuestion(
						new TemplateSpecBean(), specMaster, allquestionsList,
						value, TemplateSpecConst.QUESTION_TYPE_ITEM);
				TplSpecQuestion tplSpecQuestion = null;
				if (value == -1) {
					tplSpecQuestion = allquestionsList.get(0);
				} else {
					tplSpecQuestion = allquestionsList.get(value + 1);
				}

				// tplSpecQuestion.setTplSpecMaster(specMaster);
				tplSpecQuestion.setCtPanelItem(panelItem);
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
				tplSpecQuestion.setClPriceTypeCode(PriceTyperesDTO
						.getPmParameter());
				setUserDetails(tplSpecQuestion);
				// questionsList.add(tplSpecQuestion);
				specMaster.setScoringReq(this.getManagedBean().getQt()
						.isTechEvaluationReq());

				if (null != panelItem.getCtPanelItemSpecs()
						&& !panelItem.getCtPanelItemSpecs().isEmpty()) {
					for (int i = 0; i < panelItem.getCtPanelItemSpecs().size(); i++) {
						new TemplateSpecBackingBean().addQuestion(
								new TemplateSpecBean(), specMaster,
								allquestionsList, allquestionsList.size() - 1,
								TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC);
						CtPanelItemSpec itemSpec = panelItem
								.getCtPanelItemSpecs().get(i);
						TplSpecQuestion tplSpecSpecifications = null;
						if (value == -1) {
							tplSpecSpecifications = allquestionsList.get(i + 1);
						} else {
							tplSpecSpecifications = allquestionsList
									.get(allquestionsList.size() - 1);
						}
						PmParameter pmParameter = getParameterByTypeAndCode(
								ParameterTypeConst.ANSWER_TYPE,
								ParameterConst.ANSWER_TYPE_TEXT);
						tplSpecSpecifications.setClSpecDetail(itemSpec
								.getSpecification());
						tplSpecSpecifications.setAnswerTypeCode(pmParameter);
						if (specMaster.isScoringReq()) {
							PmParameter scoreType = getParameterByTypeAndCode(
									ParameterTypeConst.SCORE_TYPE,
									ParameterConst.SCORE_TYPE_USER);
							tplSpecSpecifications.setScoreTypeCode(scoreType);
						} else {
							tplSpecSpecifications.setScoreTypeCode(null);
						}

						setUserDetails(tplSpecSpecifications);
					}
				}
				// allquestionsList.addAll(questionsList);

			}
			specMaster.setTplSpecQuestions(allquestionsList);
			// this.getManagedBean().getQt().setZoneLocationLevel(null);
			this.getManagedBean().getQt().getScQtSupplierCriteria()
					.setLocationLevel(null);
			// this.getManagedBean().getQt().setFulfilmentType(null);
			if (this.getManagedBean().getQt().isZonal()) {
				// validateBusinessOwner(true);
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(specMaster,
							supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance()
						.updateZoneDtl(specMaster, null);
			}

			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
				boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
				this.getManagedBean().setSpecItemWeightageRequired(
						istechnicalSpec);
				boolean weightageType = retriveExistingTemplatespec(technicalCheckList);
				if(istechnicalSpec){//#8979-start
					specMaster.setWeightageReq(weightageType);
				}//#8979-End
				templateSpecBean.setSpecMaster(specMaster);
				TemplateSpecBackingBean templateSpecBackingbean = new TemplateSpecBackingBean();
				templateSpecBackingbean
						.updateItemWeightageReq(templateSpecBean);

			} else {
				//#21695 fix panel disable weightage 27/1/2022 start
				if (this.getManagedBean().getQt().isPartial() == true){
					this.getManagedBean().setSpecItemWeightageRequired(true);
					if(this.getManagedBean().getTemplateSpecBean() != null){
						if(this.getManagedBean().getTemplateSpecBean().getSpecMaster() != null){
							this.getManagedBean().getTemplateSpecBean().getSpecMaster().setWeightageReq(false);
							this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions();
							if(this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null){
								for(TplSpecQuestion specQuestWeightage : this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions()){
									if(specQuestWeightage!=null){
										specQuestWeightage.setWeightageReq(false);
										this.getManagedBean().setSpecItemWeightageRequired(true);
									}
								}
							}
							
						}
					}
				}else{
				this.getManagedBean().setSpecItemWeightageRequired(false);
				}
				//#21695 fix panel disable weightage 27/1/2022 end
			}
			templateSpecBean.setSpecMaster(specMaster);
			if (this.getManagedBean().getQt().isZonal()
					&& this.getManagedBean().getQt().getQtType()
							.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				retriveSpecdetails(this.getManagedBean().getQt().getQtId());
				/*#19637-basri*/
				List<TplSpecZoneDtl> businessOwnerZone = null;
					businessOwnerZone = convertTreeNodesToZoneCriteria(
						this.getManagedBean().getZoneGeneralTrees(), null);
				ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {

					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						supplierCriteria.getQtZoneCriterias().setModified(true);
						tplSpecZoneDtl.setTplSpecZone(supplierCriteria
								.getQtZoneCriterias());

					}
					// templateSpecBean.setSpecMaster(specMaster);
					templateSpecBean.getSpecMaster().setTplSpecZone(
							supplierCriteria.getQtZoneCriterias());
					templateSpecBean.getSpecMaster().getTplSpecZone()
							.setTplSpecZoneDtls(businessOwnerZone);
				}

			}
			this.getManagedBean().setScQtChecklist(checkList);
			// QTTemplateDocReqDTO docReqDTO = new QTTemplateDocReqDTO();
			// List<ScQtDoc> qtDocs = new ArrayList<ScQtDoc>();
			// qtDocs.add(scqtDocDest);
			// docReqDTO.setScQtDoc(qtDocs);

			// IQTTemplateSO qtTemplateSO = BusinessDelegate.getInstance()
			// .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
			// IQTTemplateSO.class);

			// qtTemplateSO.saveOrUpdateTemplateDoc(docReqDTO);
		} finally {
			parameterSo = null;
		}

	}

	/**
	 * Retrive technical specification.retrieve all the technical specification
	 * menu information and display in business owner.
	 */
	private void retriveTechnicalSpecification() {

		try {

			if (null != this.getManagedBean().getPanelItemCategory()
					&& !"".equals(this.getManagedBean().getPanelItemCategory())
					&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
							.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())
					&& this.getManagedBean().getQt().isPanel()) {
				retrieveTechnicalSpecs();
				if (null == this.getManagedBean().getScQtCheckSt2techProposal()
						|| this.getManagedBean().getScQtCheckSt2techProposal()
								.isEmpty()) {
					// createCircularItemSpec(this.getManagedBean()
					// .getCtPanelItem());
					this.getManagedBean().setSelectPanelItem(false);
				}
			}
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				retrieveTechnicalSpecs();
			}

		} finally {

		}

	}

	private void retrieveTechnicalSpecs() {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setPrepareSpec(false);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);

			this.getManagedBean().setScQtCheckSt2techProposal(
					new ArrayList<ScQtCheckList>(resDto.getSpecResultBean()
							.getResultList()));
			this.getManagedBean().setScQtCheckSt2financeProposalList(
					resDto.getFinSpecResultBean().getResultList());

		} finally {
			specSO = null;
		}

	}

	/**
	 * Procurement category code.set procurement category code from scqt.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	private void procurementCategoryCode(ScQt scQt) {
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
	 * Retrieve technical check list.retrieve all the Technical CheckList menu
	 * information and display in Specification committee.
	 */
	public void retriveTechnicalCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedTechChecklist() == null) {
			getManagedBean().setMarkDeletedTechChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {
			 //1873 note 164 - start
			if (CollectionUtils.isEmpty(this.getManagedBean().getScQtCheckSt2techProposal())) {
			 //1873 note 164 - end
				List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				//wip 4serangkai only -start
				if (reqDTO.getScQt() != null) {
					specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
					
					this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(reqDTO));
				}
				//end
				resDTO = specSo.retrieveCheckLists(reqDTO);
				if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {
					if (null != resDTO.getTechnicalCheckList()) {
						// Collections.sort(resDTO.getTechnicalCheckList());
						for (ScQtCheckList checklist : resDTO
								.getTechnicalCheckList()) {
							if (null != checklist.getPmDocument()) {
								if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
										.equalsIgnoreCase(checklist
												.getPmDocument()
												.getDocDefinitionKey())) {
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
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getScQtCheckSt2techProposal())) {
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2techProposal());
				}
			   // 20231122@asyrafhaziq_25726 - Issue 14 
			   if (null == this.getManagedBean()
						.getScQtCheckSt2financeProposalList()
						|| this.getManagedBean()
								.getScQtCheckSt2financeProposalList().isEmpty()){
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getScQtCheckSt2financeProposalList())) {
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2financeProposalList());
				}
			   }
				// Collections.sort(this.getManagedBean()
				// .getScQtCheckSt2financeProposalList());
				localcheckList = null;
			}
			if(this.getManagedBean().getQt().isSampleReq()){//#11104-start
				checksampleChecklist(this.getManagedBean()
						.getScQtCheckSt2techProposal());
			}//#11104-End
			
			//#28784 - Include Demo WIP - Start
			checkDemoVersion(getManagedBean().getQt());//#28784 -  Demo WIP
			if(this.getManagedBean().getQt().isDemoRequired() && this.getManagedBean().isNewVersion()){
				this.getManagedBean().setDemoAction(true);
				this.getManagedBean().setDemoType(true);
				resetDemoSpecStatus();				
			}//28784 - end
		} finally {
			specSo = null;
		}
	}

	/**
	 * Retrieve financial check list.retrieve all the Financial CheckList menu
	 * information and display in Specification committee.
	 */
	public void retriveFinancialCheckList() {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = null;

		if (getManagedBean().getMarkDeletedFinChecklist() == null) {
			getManagedBean().setMarkDeletedFinChecklist(
					new ArrayList<ScQtCheckList>());
		}

		try {

			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				resDTO = specSo.retrieveCheckLists(reqDTO);
				// Collections.sort(resDTO.getFinancialCheckLists());
				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO.getFinancialCheckLists());
				if (CollectionUtils.isNotEmpty(this.getManagedBean()
						.getScQtCheckSt2financeProposalList())) {
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2financeProposalList());
				}
			}
		} finally {
			specSo = null;
		}
	}

	/**
	 * Checks if is atleast one serive req.check service spec is existing or not
	 * in Technical checklist.
	 * 
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @return true, if is atleast one serive req
	 */
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
						templateSpecSO = null;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Construct score setting.create Score Setting Object for
	 * Technical/Financial/Demo/Package/Item passing scores.
	 * 
	 * @param settingType
	 *            (int settingType)
	 * @return ScQtScoreSetting.
	 */
	private ScQtScoreSetting constructScoreSetting(int settingType) {
		ScQtScoreSetting scQtScoreSetting = new ScQtScoreSetting();
		/*
		 * if (settingType == 3 || settingType == 4 || settingType == 5) {
		 * scQtScoreSetting.setPassingScore(new BigDecimal(0)); }
		 * scQtScoreSetting.setOverallFin(new BigDecimal(0));
		 * scQtScoreSetting.setOverallOtherFin(new BigDecimal(0));
		 * scQtScoreSetting.setOverallTechnical(new BigDecimal(0));
		 * scQtScoreSetting.setOverallOtherTechnical(new BigDecimal(0));
		 */
		scQtScoreSetting.setSettingType(settingType);
		scQtScoreSetting.setScQt(this.getManagedBean().getQt());
		setUserDetails(scQtScoreSetting);
		return scQtScoreSetting;

	}

	/**
	 * Retrieve technical scoring.retrieve all the Technical Scoring menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveTechnicalScoring() {
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getQt());
		SpecificationResDTO resDTO = null;
		try {
			if (null == this.getManagedBean().getScQtCheckSt2techProposal()
					|| this.getManagedBean().getScQtCheckSt2techProposal()
							.isEmpty()) {
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

				resDTO = specSo.retrieveCheckLists(reqDTO);
				List<ScQtCheckList> localcheckList = new ArrayList<ScQtCheckList>();
				if (!isAtleastOneSeriveReq(resDTO.getTechnicalCheckList())) {
					if (null != resDTO.getTechnicalCheckList()) {
						// Collections.sort(resDTO.getTechnicalCheckList());
						for (ScQtCheckList checklist : resDTO
								.getTechnicalCheckList()) {
							if (null != checklist.getPmDocument()) {
								if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
										.equalsIgnoreCase(checklist
												.getPmDocument()
												.getDocDefinitionKey())) {
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
			}
			// reqDTO.setSpecType("T");
			//#34451,#34458,#34450 related to #25138 -start
			if(reqDTO.getScQt() != null){
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(reqDTO));
			}
			//#34451,#34458,#34450 related to #25138 -end
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
			settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
			if(this.getManagedBean().getWipStatusMax() > ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE){
				settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM);
			}
			// #21695 START FARID
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDTO = specSo.retrieveScoreSettingDtls(reqDTO);

			//35056 - start - 4serangkai
			if (CollectionUtils.isNotEmpty(resDTO.getScoreSettingResultBean().getResultList())) {
				for (ScQtScoreSetting recommnendationSetting : resDTO
						.getScoreSettingResultBean().getResultList()) {
					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL) {
						if (null == this.getManagedBean()
								.getTechnicalScoreSetting()) {
							this.getManagedBean().setTechnicalScoreSetting(
									recommnendationSetting);
						}
					}

					if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_DEMO) {
						if (null == this.getManagedBean().getDemoScoreSetting()) {
							this.getManagedBean().setDemoScoreSetting(
									recommnendationSetting);
						}
					}

					if (this.getManagedBean().getWipStatusMax() > ParameterConst.FLAG_WIP_STATUS_3SERANGKAI_BEFORE
							&& recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM
							&& null == this.getManagedBean().getTechnicalItemScoreSetting()) {
						this.getManagedBean().setTechnicalItemScoreSetting(
								recommnendationSetting);
					} 
				} 
			} 

			if (null == this.getManagedBean().getTechnicalScoreSetting()) {
				ScQtScoreSetting technicalScoresetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL);
				technicalScoresetting.setPassingScore(retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE));
				this.getManagedBean().setTechnicalScoreSetting(technicalScoresetting);
			} 
			if (null == this.getManagedBean().getDemoScoreSetting()) {
				ScQtScoreSetting demoScoresetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
				this.getManagedBean().setDemoScoreSetting(demoScoresetting);
			} 

			if (this.getManagedBean().getQt().isPartial()) {
				this.getManagedBean().getTechnicalScoreSetting().setWeightageReq(false);
			} 
			//35056 - end - 4serangkai
			settinglist = null;
			this.getManagedBean().setTechnicalScoring(null);
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
					if (null != list.getQtScoreWeightage()
							&& null != list.getQtScoreWeightage().getScore()) {
						addScore = addScore
								+ list.getQtScoreWeightage().getScore()
										.intValue();
					}
					score = (int) addScore;
					if (list.isScoringReq()) {
						this.getManagedBean().getTechnicalScoring().add(list);
					} else {
						list.getQtScoreWeightage().setScore(new BigDecimal(0));
					}
					if (null != list.getQtScoreWeightage()
							&& null != list.getQtScoreWeightage().getScore()) {
						list.getQtScoreWeightage().setScore(
								list.getQtScoreWeightage().getScore());
					}

				}

				
				// 37813,35384 remove coding.
				techWeightageSum();
				techScoreSum();
				
//#28948 Start - remove this (fixed in #21695)			
				//#25635 danial 11/3/2020
//				if(null!=this.getManagedBean().getTechnicalItemScoreSetting()){
//					if(null!=this.getManagedBean().getTechnicalItemScoreSetting().getOverallOtherTechnical()
//							&&
//					   null!=this.getManagedBean().getTechnicalItemScoreSetting().getOverallTechnical()
//							){
//						technicalItemScoreCalculate(this.getManagedBean().getTechnicalItemScoreSetting());
//					}
//				}
//					
//#28949 End - remove this (fixed in #21695)			
			}

			if (null != this.getManagedBean().getTechnicalScoring()
					&& !this.getManagedBean().getTechnicalScoring().isEmpty()) {
//#21695 & #30557 Start retrieve totalscore of sum overalltech + overallothertech 	
				retriveItemScore(this
						.getManagedBean()
						.getTechnicalItemScoreSetting());
//#21695 End
				this.getManagedBean().setTechnicalUserScoringSchema(false);
				for (ScQtCheckList technicalScoringChecklist : this
						.getManagedBean().getTechnicalScoring()) {
					if (null != technicalScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(technicalScoringChecklist
											.getPmStatus().getStatusId())
							&& null != technicalScoringChecklist
									.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION
									.equals(technicalScoringChecklist
											.getPmEvalStatus().getStatusId())) {
						this.getManagedBean().setTechnicalUserScoringSchema(
								true);
						break;

					}

				}
			}
			checklistSorting(this.getManagedBean().getTechnicalScoring());
		} finally {
			specSo = null;
		}
		return "technicalScoring.xhtml";
	}

	/**
	 * Retrieve financial scoring.retrieve all the Financial Scoring menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveFinancialScoring(String condition) {	// Bug #35688@Syazani 10/07/2024
		IQTSpecSO specSo = null;

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setScQt(this.getManagedBean().getQt());
		try {
			//#25138 - add if() condition - start
			if (null == this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					|| this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()
					// #25138 - 31578 - Start.
					|| (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
										&& CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls()))
					// #25138 - 31578 - End.					
					) {
				
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setSpecType(SourcingConstants.FINANCIAL_DOCTYPE);
				SpecificationResDTO resDTO1 = specSo.retrieveCheckLists(reqDTO);

				this.getManagedBean().setScQtCheckSt2financeProposalList(
						resDTO1.getFinancialCheckLists());
			}
			//#25138 - add if() condition - end		
			List<Integer> settinglist = new ArrayList<Integer>();
			settinglist
					.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE);
			settinglist
					.add(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM);

			// reqDTO.setSpecType("T");
			reqDTO.setSettingTypes(settinglist);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationResDTO resDTO = specSo
					.retrieveScoreSettingDtls(reqDTO);

			if (null != resDTO.getScoreSettingResultBean().getResultList()
					&& resDTO.getScoreSettingResultBean().getResultList()
							.isEmpty()) {
				if (null == this.getManagedBean().getFinancialScoreSetting()) {
					ScQtScoreSetting scoreSetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE);
					this.getManagedBean()
							.setFinancialScoreSetting(scoreSetting);
				}
				if (null == this.getManagedBean().getFinacialItemScoreSetting()) {
					ScQtScoreSetting scoreSetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM);
					this.getManagedBean().setFinacialItemScoreSetting(
							scoreSetting);
				}

			} else {
				if (resDTO.getScoreSettingResultBean().getResultList().size() == 2) {
					for (ScQtScoreSetting scoreSttting : resDTO
							.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
							if (null == this.getManagedBean()
									.getFinancialScoreSetting()) {
								this.getManagedBean().setFinancialScoreSetting(
										scoreSttting);
							}
						}
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {
							if (null == this.getManagedBean()
									.getFinacialItemScoreSetting()) {
								this.getManagedBean()
										.setFinacialItemScoreSetting(
												scoreSttting);
								calculateFinancialItemScore(this
										.getManagedBean()
										.getFinacialItemScoreSetting());
							}

						}
					}
				} else if (resDTO.getScoreSettingResultBean().getResultList()
						.size() == 1) {
					for (ScQtScoreSetting scoreSttting : resDTO
							.getScoreSettingResultBean().getResultList()) {
						if (scoreSttting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_PACKAGE) {
							if (null == this.getManagedBean()
									.getFinancialScoreSetting()) {
								this.getManagedBean().setFinancialScoreSetting(
										scoreSttting);
							}
						}

						if (null == this.getManagedBean()
								.getFinacialItemScoreSetting()) {
							ScQtScoreSetting scoreSetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM);
							this.getManagedBean().setFinacialItemScoreSetting(
									scoreSetting);
						}

					}

				}

			}
			settinglist = null;
			
			//#33525 - Get User key in weightageScore if avail
			List<ScQtCheckList> oriFinancialScoring = this.getManagedBean()
					.getFinancialScoring();
			
			this.getManagedBean().setFinancialScoring(null);
			this.getManagedBean().setFinancialScoring(
					new ArrayList<ScQtCheckList>());
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			checklistSorting(checklist);
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
					if (null != list.getQtScoreWeightage()
							&& null != list.getQtScoreWeightage().getScore()) {
						addScore = addScore
								+ list.getQtScoreWeightage().getScore()
										.intValue();
					}

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
						} else {
							list.getQtScoreWeightage().setScore(
									new BigDecimal(0));
						}
					}

					int local = (int) addScore;
					this.getManagedBean().setFinPoints(local);
					if (null != list.getQtScoreWeightage()
							&& null != list.getQtScoreWeightage().getScore()) {
						list.getQtScoreWeightage().setScore(
								list.getQtScoreWeightage().getScore());
					}
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
					//#25138 Reendorsed - start
					
					List<ScQtScoreOfferPrice> offerPrice = specChecklist.get(0)
							.getQtScoreWeightage().getScQtScoreOfferPrices();	
					
					if(this.getManagedBean().getQt().isZonal() && CollectionUtils.isEmpty(offerPrice) &&  CollectionUtils.isNotEmpty(this.getManagedBean()
								.getOfferPricePackageList())){//33587-start
						offerPrice=	this.getManagedBean()
						.getOfferPricePackageList();
						tmpCheckList.getQtScoreWeightage().setScQtScoreOfferPrices(offerPrice);
					}//33587-End
					
					this.getManagedBean().setOfferScoreType(scoreTypeN);
					
					specSo = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
					   this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(reqDTO));//34852_Start
					   if(this.getManagedBean().getQt().isZonal() && this.getManagedBean().getWipStatusMax()>SourcingConstants.SCORE_SETTING_TYPE_FINANCIAL_BY_ITEM) {//34852_End
						ScQt scQt = this.getManagedBean().getQt();
						scQt.setZonal(true);
						List<ScQtScoreOfferPrice> newOfferList = new ArrayList<ScQtScoreOfferPrice>();
						if(null == offerPrice || offerPrice.isEmpty()){
							
							for(TplSpecZoneDtl zone : this.getManagedBean().getTemplateSpecZoneDtls()){
								ScQtScoreOfferPrice scrOfferPrice = new ScQtScoreOfferPrice();
								scrOfferPrice.setScQtScoreWeightage(tmpCheckList
										.getQtScoreWeightage());
								scrOfferPrice
										.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);
								scrOfferPrice.setTplSpecZoneDtl(zone);
								
								//#29168 - Bug - start
								if(null == scoreTypeN || scoreTypeN.isEmpty()){
									this.getManagedBean().setOfferScoreType(QTConstants.INITIAl_SCORE);
									
								} else if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {
									scrOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
											.getOfferScoreType()));
									
								} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
									scrOfferPrice.setScoreType(this.getManagedBean().getScoreType());
									scrOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
											.getOfferScoreType()));
									scrOfferPrice.setAboveScore(null);
									scrOfferPrice.setBelowScore(null);
									scrOfferPrice.setFromVariance(null);
									scrOfferPrice.setToVariance(null);

								}
								setUserDetails(scrOfferPrice);
								this.getManagedBean().setOfferPricePackage(scrOfferPrice);
								newOfferList.add(this.getManagedBean().getOfferPricePackage());
								
							}
							
						}else{
							//====#31913 bug related to #25138====start
							specSo = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
							SpecificationReqDTO reqDTO2 = new SpecificationReqDTO();
							reqDTO2.setWeightageId(tmpCheckList.getQtScoreWeightage().getQtScoreWeightageId());
							SpecificationResDTO resDTO2 = specSo
									.retrieveQtScoreOfferPricesZonal(reqDTO2);
							//====#31913 bug related to #25138====end
							//#34451,#34458,#34450 related to #25138 -start
							if (this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
									&& CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())
									||  this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
									&& CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())
									||  this.getManagedBean().getCurrentStatusId()
									.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT)
									&& CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())) {
								populateZonalDetails(tmpCheckList, newOfferList);
							}
							// #25138 - 31578 - End.
							//#34451,#34458,#34450 related to #25138 -end
							//====#31913 bug related to #25138==== start
							//#34531, #34529 - start
							Map<Long, TplSpecZoneDtl> tplSpecZoneDtlMap = new HashMap<>();
							if(CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())){
								for(TplSpecZoneDtl zoneDtl: this.getManagedBean().getTemplateSpecZoneDtls()){
									tplSpecZoneDtlMap.put(zoneDtl.getSpecZoneId(), zoneDtl);
								}
							}
							//#34531, #34529 - end
							List<ScQtScoreOfferPrice> scQtScoreOfferPrices = new ArrayList<ScQtScoreOfferPrice>();
							if(!resDTO2.getScoreOfferPrice().isEmpty()){
								scQtScoreOfferPrices = resDTO2.getScoreOfferPrice();
								//#34531, #34529 - start
								for(ScQtScoreOfferPrice testoffer:scQtScoreOfferPrices){ 
									TplSpecZoneDtl specZoneDtl = tplSpecZoneDtlMap.get(testoffer.getTplSpecZoneDtl().getSpecZoneId());
									if(specZoneDtl!=null){
										testoffer.setTplSpecZoneDtl(specZoneDtl);
									}
								//#34531, #34529 - end
								}
							}else{
								scQtScoreOfferPrices = tmpCheckList.getQtScoreWeightage()
										.getScQtScoreOfferPrices();
							}
							//Bug #33642 related to #25138 START
							if(CollectionUtils.isNotEmpty(this.getManagedBean().getOfferPricePackageList())){
								scQtScoreOfferPrices = this.getManagedBean().getOfferPricePackageList();
							}
							//Bug #33642 to #25138 END
					        for(ScQtScoreOfferPrice offerScore : scQtScoreOfferPrices){
					        //====#31913 bug related to #25138==== end
								if (ParameterConst.RECOMMEND_TYPE_PACKAGE
										.equalsIgnoreCase(offerScore
												.getOfferPriceType())) {
									if(null == offerScore.getScoreType()){
										
										if(null != this.getManagedBean().getOfferScoreType()){
											if(this.getManagedBean().getOfferScoreType().equals(QTConstants.AUTO_SCORE)){
												offerScore.setScoreType(new BigDecimal(QTConstants.AUTO_SCORE));
											
											}else if(this.getManagedBean().getOfferScoreType().equals(QTConstants.USER_SCORE)){
												offerScore.setScoreType(new BigDecimal(QTConstants.USER_SCORE));
												offerScore.setAboveScore(null);
												offerScore.setBelowScore(null);
												offerScore.setFromVariance(null);
												offerScore.setToVariance(null);
											
											}
										}
									}
									else{
										if(offerScore.getScoreType().equals(new BigDecimal(QTConstants.AUTO_SCORE))){
											this.getManagedBean().setOfferScoreType(QTConstants.AUTO_SCORE);
											
										}else if(offerScore.getScoreType().equals(new BigDecimal(QTConstants.USER_SCORE))){
											this.getManagedBean().setOfferScoreType(QTConstants.USER_SCORE);
										
										}else{
											this.getManagedBean().setOfferScoreType(QTConstants.INITIAl_SCORE);
										}
									}
								}
								
								this.getManagedBean().setOfferPricePackage(offerScore);
								newOfferList.add(this.getManagedBean().getOfferPricePackage());
							
							}
							
						}
						//====#31913 bug related to #25138==== start
						retriveSpecQuestion();
						populateFinancialSpecDetailsByZone();
						//Bug #35111 insert max score if null, wip did not insert max score while 25138 already divert the max score into tplspeczoneqty
						checkSpecZoneQtyMaxScore(tmpCheckList);
						saveTotalMaxScoreItemZonal(newOfferList);
						this.getManagedBean().setOfferPricePackageList(newOfferList);

						tmpCheckList.getQtScoreWeightage().setScQtScoreOfferPrices(this.getManagedBean()
								.getOfferPricePackageList());
						
						
						if(this.getManagedBean().getTotalmaxscore()!=null){
							tmpCheckList.getQtScoreWeightage().setScore(this.getManagedBean()
									.getTotalmaxscore());
						//====#31913 bug related to #25138==== end
						}

					}else{
						if (null == offerPrice || offerPrice.isEmpty()) {
							offerPrice = new ArrayList<ScQtScoreOfferPrice>();
							if (null == this.getManagedBean()
									.getOfferPricePackage()) {
								ScQtScoreOfferPrice pkgOfferPrice = new ScQtScoreOfferPrice();
								pkgOfferPrice.setScQtScoreWeightage(tmpCheckList
										.getQtScoreWeightage());
								pkgOfferPrice
										.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);
								if(this.getManagedBean().getWipStatusMax()<SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM){//34852_Start
									pkgOfferPrice.setScoreType(new BigDecimal(QTConstants.AUTO_SCORE));	
									this.getManagedBean().setOfferScoreType(String.valueOf(pkgOfferPrice
											.getScoreType().intValue()));
									//scoreTypeN=QTConstants.AUTO_SCORE;
								}//34852_End
								
								//#29168 - Bug - start
								if(null == scoreTypeN){
									this.getManagedBean().setOfferScoreType(QTConstants.INITIAl_SCORE);		
								} else if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {
								//#29168 - Bug - end
									pkgOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
											.getOfferScoreType()));

								} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
									pkgOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
											.getOfferScoreType()));
									pkgOfferPrice.setAboveScore(null);
									pkgOfferPrice.setBelowScore(null);
									pkgOfferPrice.setFromVariance(null);
									pkgOfferPrice.setToVariance(null);

								}
								setUserDetails(pkgOfferPrice);
								this.getManagedBean()
										.setOfferPricePackage(pkgOfferPrice);
								offerPrice.add(pkgOfferPrice);
								
							}
								

							tmpCheckList.getQtScoreWeightage()
									.setScQtScoreOfferPrices(offerPrice);
							if (null!=tmpCheckList.getQtScoreWeightage() && tmpCheckList.getQtScoreWeightage().getScore() != null) {//34852_Start			
								BigDecimal totalScore=	tmpCheckList.getQtScoreWeightage().getScore();
									getManagedBean().setScoreVarience(
											totalScore);
									getManagedBean().setTotalmaxscore(totalScore);
								}//34852_End

						} else {
							for (ScQtScoreOfferPrice scoreOfferPrice : tmpCheckList
									.getQtScoreWeightage()
									.getScQtScoreOfferPrices()) {
								
								//#25138 bug Ahmaa - start
								if(null != scoreOfferPrice.getScoreType()){
									this.getManagedBean().setOfferScoreType(
											String.valueOf(scoreOfferPrice.getScoreType().intValue()));									
								}
								//#25138 bug - end
								if (ParameterConst.RECOMMEND_TYPE_PACKAGE
										.equalsIgnoreCase(scoreOfferPrice
												.getOfferPriceType())) {
									
									if(null == scoreOfferPrice.getScoreType()){
										scoreOfferPrice
											.setScoreType(new BigDecimal(QTConstants.AUTO_SCORE));
										//#29168 - Bug - start
										this.getManagedBean().setOfferScoreType(String.valueOf(scoreOfferPrice
												.getScoreType().intValue()));
										//#29168 - Bug - end
										this.getManagedBean()
											.setOfferPricePackage(scoreOfferPrice);
									}else{
										
										if (null == this.getManagedBean().getOfferPricePackage()) {
											
											this.getManagedBean().setOfferScoreType(
													String.valueOf(scoreOfferPrice.getScoreType().intValue()));
											this.getManagedBean()
													.setOfferPricePackage(scoreOfferPrice);

											if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {

												this.getManagedBean()
													.setOfferScoreType(scoreTypeN);
												scoreOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
														.getOfferScoreType()));

											} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
												this.getManagedBean()
														.setOfferScoreType(scoreTypeN);
												scoreOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
														.getOfferScoreType()));
												scoreOfferPrice.setAboveScore(null);
												scoreOfferPrice.setBelowScore(null);
												scoreOfferPrice.setFromVariance(null);
												scoreOfferPrice.setToVariance(null);

											}

										} else {
											if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {

												this.getManagedBean().setOfferScoreType(scoreTypeN);											
												this.getManagedBean().getOfferPricePackage()
													.setScoreType(new BigDecimal(scoreTypeN));

											} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
												this.getManagedBean()
													.setOfferScoreType(scoreTypeN);
												this.getManagedBean()
													.getOfferPricePackage().setFromVariance(null);
												this.getManagedBean()
													.getOfferPricePackage().setToVariance(null);
												this.getManagedBean()
													.getOfferPricePackage().setBelowScore(null);
												this.getManagedBean()
													.getOfferPricePackage().setAboveScore(null);

											}

										}
									}
									
								}

							}
							if (specChecklist.get(0).getQtScoreWeightage().getScore() != null) {//34852_Start			
								BigDecimal totalScore=	specChecklist.get(0).getQtScoreWeightage()
									.getScore();
									getManagedBean().setScoreVarience(
											totalScore);
									getManagedBean().setTotalmaxscore(totalScore);
								}//34852_End
						}
						retriveSpecQuestion();
						//#25138 Reendorsed - end
						
						// #25138 - 31578 - Start.
						if (this.getManagedBean().getCurrentStatusId().equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)) {
							
							if (tmpCheckList.getQtScoreWeightage().getScQtScoreOfferPrices().size() > 1) {
			        			Iterator<ScQtScoreOfferPrice> iterator = tmpCheckList.getQtScoreWeightage().getScQtScoreOfferPrices().iterator();
			        			int itrSize = 0;
			        			while (iterator.hasNext()) {
			        				iterator.next();
			        				if (itrSize != 0) {
			        					iterator.remove();
			        				}
			        				itrSize++;
			        			}
			        		}
							if (CollectionUtils.isNotEmpty(this.getManagedBean().getOfferPricePackageList())) {
								this.getManagedBean().setOfferPricePackageList(tmpCheckList.getQtScoreWeightage().getScQtScoreOfferPrices());
							}
						}
						// #25138 - 31578 - End.
					}
					this.getManagedBean().getFinancialScoring()
							.add(0, tmpCheckList);
					map = null;
					list = null;
					tmpCheckList = null;
				}

				this.getManagedBean().getFinancialScoring()
						.addAll(localChecklist);
				//#34451,#34458,#34450 related to #25138 -start
				if(reqDTO.getScQt() != null){
					specSo = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
					this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(reqDTO));
				}
				//#34451,#34458,#34450 related to #25138 -end

				//#25138 Reendorsed -populate total max score for TS and other element - start
				if(this.getManagedBean().getQt().isZonal()){
					
					finScoreSum();
					int finalScorePoint = this.getManagedBean().getFinPoints();
					//====#31913 bug related to #25138==== start
					BigDecimal totalPoints = this.getManagedBean().getTotalmaxscore();
					//====#31913 bug related to #25138==== end
					finalScorePoint = finalScorePoint + totalPoints.intValue();
					this.getManagedBean().setFinPoints(finalScorePoint);
					System.out.println("totalFinalPoint===>"+finalScorePoint);
				}else{
					finScoreSum();

				}
				//#25138 Reendorsed -populate total max score for TS and other element - end
				
				//#33525 - enter back the user weightage value if avail
				List<ScQtCheckList> newFinancialScoring = this.getManagedBean().getFinancialScoring();
				if (oriFinancialScoring != null) {
					for (ScQtCheckList o : oriFinancialScoring) {
						for (ScQtCheckList l : newFinancialScoring)
							if (null != l.getQtScoreWeightage().getQtScoreWeightageId()
									&& l.getQtScoreWeightage().getQtScoreWeightageId().equals(o.getQtScoreWeightage().getQtScoreWeightageId())) {//bug #35211 related to #33525
								l.getQtScoreWeightage().setWeightage(o.getQtScoreWeightage().getWeightage());
							}
					}
					this.getManagedBean().setFinancialScoring(newFinancialScoring);
				}// #33525 - End
				// Bug #35688@Syazani 10/07/2024 -- Start
				boolean isNotThrow = StringUtils.isNotEmpty(condition) && "tabChange".equalsIgnoreCase(condition) ? true : false;	// Bug #35688@Syazani 10/07/2024
				finWeightageSum(isNotThrow);
				// Bug #35688@Syazani 10/07/2024 -- End
				localChecklist = null;
				specChecklist = null;
				sb = null;
			}
			if (null != this.getManagedBean()
					.getScQtCheckSt2financeProposalList()
					&& !this.getManagedBean()
							.getScQtCheckSt2financeProposalList().isEmpty()) {
				this.getManagedBean().setFinancialUserScoringSchema(false);
				for (ScQtCheckList financialScoringChecklist : this
						.getManagedBean().getScQtCheckSt2financeProposalList()) {
					if (financialScoringChecklist.isScoringReq()
							&& null != financialScoringChecklist.getPmStatus()
							&& QTStatusConst.TS_COMPLETED
									.equals(financialScoringChecklist
											.getPmStatus().getStatusId())
							&& null != financialScoringChecklist
									.getPmEvalStatus()
							&& QTStatusConst.TS_PENDING_SUBMISSION
									.equals(financialScoringChecklist
											.getPmEvalStatus().getStatusId())) {
						this.getManagedBean().setFinancialUserScoringSchema(
								true);
						break;

					}

				}
			}

		} finally {
			specSo = null;
		}
		return "financialScoring.xhtml";

	}

	// #25138 - 31578 - Start.
	/**
	 * @param tmpCheckList
	 * @param newOfferList
	 */
	private void populateZonalDetails(ScQtCheckList tmpCheckList,
			List<ScQtScoreOfferPrice> newOfferList) {
		int i = 0;
		if (this.getManagedBean().getTemplateSpecZoneDtls().size() == tmpCheckList.getQtScoreWeightage()
				.getScQtScoreOfferPrices().size()) {
			for (ScQtScoreOfferPrice offerScore: tmpCheckList.getQtScoreWeightage()
					.getScQtScoreOfferPrices()) {
				offerScore.setTplSpecZoneDtl(this.getManagedBean().getTemplateSpecZoneDtls().get(i));
				if (StringUtils.isNotEmpty(this.getManagedBean().getOfferScoreType())) {
					offerScore.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));
				}
				i++;
			}
		} else {
			if (tmpCheckList.getQtScoreWeightage()
					.getScQtScoreOfferPrices().size() > this.getManagedBean().getTemplateSpecZoneDtls().size()) {
				Iterator<ScQtScoreOfferPrice> iterator = tmpCheckList.getQtScoreWeightage().getScQtScoreOfferPrices().iterator();
				int itrSize = 0;
				while (iterator.hasNext()) {
					iterator.next();
					if ((itrSize+1) > this.getManagedBean().getTemplateSpecZoneDtls().size()) {
						iterator.remove();
					}
					itrSize++;
				}
			}
			
			for(TplSpecZoneDtl zoneDtl : this.getManagedBean().getTemplateSpecZoneDtls()){
				if ((i+1) <= tmpCheckList.getQtScoreWeightage()
						.getScQtScoreOfferPrices().size()) {
					ScQtScoreOfferPrice scoreOfferPrice = tmpCheckList.getQtScoreWeightage().getScQtScoreOfferPrices().get(i);
					scoreOfferPrice.setTplSpecZoneDtl(zoneDtl);
					if (StringUtils.isNotEmpty(this.getManagedBean().getOfferScoreType())) {
						scoreOfferPrice.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));
					}
				} else {
					ScQtScoreOfferPrice scrOfferPrice = new ScQtScoreOfferPrice();
					scrOfferPrice.setScQtScoreWeightage(tmpCheckList.getQtScoreWeightage());
					scrOfferPrice.setOfferPriceType(ParameterConst.RECOMMEND_TYPE_PACKAGE);
					scrOfferPrice.setTplSpecZoneDtl(zoneDtl);
					if(null == scoreTypeN || scoreTypeN.isEmpty()){
						this.getManagedBean().setOfferScoreType(QTConstants.INITIAl_SCORE);
						
					} else if (scoreTypeN.equals(QTConstants.AUTO_SCORE)) {
						scrOfferPrice.setScoreType(new BigDecimal(this.getManagedBean()
								.getOfferScoreType()));
						
					} else if (scoreTypeN.equals(QTConstants.USER_SCORE)) {
						scrOfferPrice.setScoreType(this.getManagedBean().getScoreType());
						scrOfferPrice.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));
						scrOfferPrice.setAboveScore(null);
						scrOfferPrice.setBelowScore(null);
						scrOfferPrice.setFromVariance(null);
						scrOfferPrice.setToVariance(null);
						
					}
					setUserDetails(scrOfferPrice);
					this.getManagedBean().setOfferPricePackage(scrOfferPrice);
					newOfferList.add(this.getManagedBean().getOfferPricePackage());
				}
				i++;
			}
			
		}
	}
	// #25138 - 31578 - End.
	
	/**
	 * Retrieve recommendation details.retrieve all the Recommendation menu
	 * information and display in Specification committee.
	 * 
	 * @return the string
	 */
	private String retriveRecommendationDetails() {
		IQTSpecSO specSo = null;

		try {
			if (null == this.getManagedBean().getRecommendationPackageScore()
					&& null == this.getManagedBean().getRecommendationScore()) {

				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setScQt(this.getManagedBean().getQt());
				// reqDTO.setSpecType("T");

				List<Integer> settinglist = new ArrayList<Integer>();
				settinglist
						.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE);
				settinglist
						.add(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM);
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
						ScQtScoreSetting pkgScoreSetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE);
						BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_WEIGHTAGE);
						pkgScoreSetting.setOverallTechnical(passingScore);
						int financial = 100 - passingScore.intValue();
						pkgScoreSetting
								.setOverallFin(new BigDecimal(financial));
						this.getManagedBean().setRecommendationPackageTotal(
								new BigDecimal(100));

						this.getManagedBean().setRecommendationPackageScore(
								pkgScoreSetting);
					}
					if (null == this.getManagedBean().getRecommendationScore()) {
						ScQtScoreSetting itemScoreSetting = constructScoreSetting(SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM);
						this.getManagedBean().setRecommendationScore(
								itemScoreSetting);
					}

				} else {

					for (ScQtScoreSetting recommnendationSetting : resDTO
							.getScoreSettingResultBean().getResultList()) {

						if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_PACKAGE) {
							this.getManagedBean()
									.setRecommendationPackageScore(
											recommnendationSetting);
							calculateTotalPackage(this.getManagedBean()
									.getRecommendationPackageScore(), false);
						}

						if (recommnendationSetting.getSettingType() == SourcingConstants.SCORE_SETTING_TYPE_RECOMMENDATION_BY_ITEM) {
							this.getManagedBean().setRecommendationScore(
									recommnendationSetting);
							calculateRecommendtechnicalScore(this
									.getManagedBean().getRecommendationScore());
							calculateRecommendFinancialScore(this
									.getManagedBean().getRecommendationScore());
						}

					}

				}
				settinglist = null;
			}

		} finally {
			specSo = null;
		}
		return "recomondation.xhtml";
	}

	/**
	 * Retrieve fulfilment dtls.retrieve all the FulfilMentDetails menu
	 * information and display in Specification committee.
	 */
	private void retriveFulfilmentDtls() {
		IQTSpecSO specSo = null;

		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			List<ScQtProgressivePayment> paymentList = this.getManagedBean()
					.getProgressivePaymentList();

			if (null == scheduleList) {
				scheduleList = new ArrayList<ScQtSchedule>();
				if (null != resDTO.getScheduleResults()
						&& null != resDTO.getScheduleResults().getResultList()
						&& !resDTO.getScheduleResults().getResultList()
								.isEmpty()) {

					this.getManagedBean().setTotalFrequency(
							resDTO.getScheduleResults().getResultList().size());
					try {

						for (ScQtSchedule scQtSchedule : resDTO
								.getScheduleResults().getResultList()) {
							String startMonth = "";
							String endMonth = "";
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									CommonConstants.DEFAULT_DATE_FORMAT);
							int months;
							Date startDate = null;
							Date endDate = null;
							String fromdate = dateFormat.format(scQtSchedule
									.getFromDate());
							String todate = dateFormat.format(scQtSchedule
									.getToDate());
							String startdate = fromdate.substring(3, 5);
							String startYear = fromdate.substring(6,
									fromdate.length());
							String fromDay = fromdate.substring(0, 2);

							String enddate = todate.substring(3, 5);
							String endYear = todate.substring(6,
									todate.length());
							String toDay = todate.substring(0, 2);

							startMonth = fulfilmentMonthlyDtls(startdate);
							endMonth = fulfilmentMonthlyDtls(enddate);

							int sYear = Integer.parseInt(startYear);
							int eYear = Integer.parseInt(endYear);

							String fromDate = fromDay + "/" + startdate + "/"
									+ sYear;
							String toDate = toDay + "/" + enddate + "/" + eYear;

							startDate = dateFormat.parse(fromDate);
							endDate = dateFormat.parse(toDate);

							months = DateUtils.getMonths(startDate, endDate);
							scQtSchedule.setTotalMonths(new BigDecimal(months));
							// scQtSchedule.setTotalMonths(new
							// BigDecimal(months));

							scQtSchedule.setStartMonth(startMonth);
							scQtSchedule.setEndMonth(endMonth);
							scQtSchedule.setStartYear(startYear);
							scQtSchedule.setEndYear(endYear);
							// Reopen 25726 note 271- Issue 4 
							scQtSchedule.setScheduleType(this.getManagedBean().getQt().getFulfilmentType());
							scheduleList.add(scQtSchedule);

						}
					} catch (ParseException e) {

					}
				}
				//20231122@asyrafhaziq_25726 - Issue 10
				if (this.getManagedBean().getToBeRemoveSchedules() != null && !this.getManagedBean().getToBeRemoveSchedules().isEmpty()) {
					EntityHelper.compareAndRemoveEntityFromList(this.getManagedBean().getToBeRemoveSchedules(),
							scheduleList, "scheduleNo");
				}

				this.getManagedBean().setScheduleList(scheduleList);
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
						startMonth = fulfilmentMonthlyDtls(startdate);
						endMonth = fulfilmentMonthlyDtls(enddate);
						payment.setStartMonth(startMonth);
						payment.setEndMonth(endMonth);
						payment.setStartYear(startYear);
						payment.setEndYear(endYear);
						paymentList.add(payment);

					}

				}
				//20231122@asyrafhaziq_25726 - Issue 10
				if (this.getManagedBean().getToBeRemoveProgPayments() != null && !this.getManagedBean().getToBeRemoveProgPayments().isEmpty()) {
					EntityHelper.compareAndRemoveEntityFromList(this.getManagedBean().getToBeRemoveProgPayments(),
							paymentList, "phaseNo");
				}

				this.getManagedBean().setProgressivePaymentList(paymentList);
			}

		} finally {
			specSo = null;
		}

	}
	
	/*
	private void populateFreqSchedules(List<ScQtSchedule> scheduleList){
		if (!EntityHelper.isCollectionEmpty(scheduleList)) {

			this.getManagedBean().setTotalFrequency(
					scheduleList.size());
			try {

				for (ScQtSchedule scQtSchedule : scheduleList) {
					String startMonth = "";
					String endMonth = "";
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							CommonConstants.DEFAULT_DATE_FORMAT);
					int months;
					Date startDate = null;
					Date endDate = null;
					String fromdate = dateFormat.format(scQtSchedule
							.getFromDate());
					String todate = dateFormat.format(scQtSchedule
							.getToDate());
					String startdate = fromdate.substring(3, 5);
					String startYear = fromdate.substring(6,
							fromdate.length());
					String fromDay = fromdate.substring(0, 2);

					String enddate = todate.substring(3, 5);
					String endYear = todate.substring(6,
							todate.length());
					String toDay = todate.substring(0, 2);

					startMonth = fulfilmentMonthlyDtls(startdate);
					endMonth = fulfilmentMonthlyDtls(enddate);

					int sYear = Integer.parseInt(startYear);
					int eYear = Integer.parseInt(endYear);

					String fromDate = fromDay + "/" + startdate + "/"
							+ sYear;
					String toDate = toDay + "/" + enddate + "/" + eYear;

					startDate = dateFormat.parse(fromDate);
					endDate = dateFormat.parse(toDate);

					months = DateUtils.getMonths(startDate, endDate);
					scQtSchedule.setTotalMonths(new BigDecimal(months));


					scQtSchedule.setStartMonth(startMonth);
					scQtSchedule.setEndMonth(endMonth);
					scQtSchedule.setStartYear(startYear);
					scQtSchedule.setEndYear(endYear);

				}
			} catch (ParseException e) {

			}
		}
	}
	*/
	
	/*
	private void populateProgressivePayment(
			List<ScQtProgressivePayment> progressivePayments) throws ParseException {
		if (!EntityHelper.isCollectionEmpty(progressivePayments)) {
			this.getManagedBean().setTotalProgressivePayment(
					progressivePayments.size());
			for (ScQtProgressivePayment scQtProgressivePayment : progressivePayments) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						CommonConstants.DEFAULT_DATE_FORMAT);

				String startMonth = "";
				String endMonth = "";
				String fromdate = dateFormat.format(scQtProgressivePayment
						.getFromDate());
				String todate = dateFormat.format(scQtProgressivePayment
						.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());

				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());
				
				startMonth = fulfilmentMonthlyDtls(startdate);
				endMonth = fulfilmentMonthlyDtls(enddate);

				scQtProgressivePayment.setStartMonth(startMonth);
				scQtProgressivePayment.setEndMonth(endMonth);
				scQtProgressivePayment.setStartYear(startYear);
				scQtProgressivePayment.setEndYear(endYear);

			}
			this.sortProgressivePaymentList(progressivePayments);
			this.getManagedBean().setProgressivePaymentList(
					progressivePayments);
		}
	}
	*/

	private String fulfilmentMonthlyDtls(String startdate) {
		String startMonth = "";
		try {
			if (startdate.equalsIgnoreCase("01")) {
				startMonth = "0";
			} else if (startdate.equalsIgnoreCase("02")) {
				startMonth = "1";
			} else if (startdate.equalsIgnoreCase("03")) {
				startMonth = "2";
			} else if (startdate.equalsIgnoreCase("04")) {
				startMonth = "3";
			} else if (startdate.equalsIgnoreCase("05")) {
				startMonth = "4";
			} else if (startdate.equalsIgnoreCase("06")) {
				startMonth = "5";
			}

			else if (startdate.equalsIgnoreCase("07")) {
				startMonth = "6";
			} else if (startdate.equalsIgnoreCase("08")) {
				startMonth = "7";
			} else if (startdate.equalsIgnoreCase("09")) {
				startMonth = "8";
			} else if (startdate.equalsIgnoreCase("10")) {
				startMonth = "9";
			} else if (startdate.equalsIgnoreCase("11")) {
				startMonth = "10";
			} else if (startdate.equalsIgnoreCase("12")) {
				startMonth = "11";
			}

		} finally {

		}
		return startMonth;
	}

	/**
	 * Retrieve delivery details.retrieve all the Delivery Details menu
	 * information and display in Specification committee.
	 */
	private void retriveDeliveryDetails() {
		log.debug("retriveDeliveryDetails - START");
		IQTSpecSO specSo = null;
		try {
			if (this.getManagedBean().getDeliveryNonZoneList() == null
					&& null == this.getManagedBean().getDeliveryLocBean()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				specSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
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
						this.getManagedBean()
								.setTplSpecZoneDtl(
										this.getManagedBean()
												.getDeliveryZones().get(0));
						reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
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
		log.debug("retriveDeliveryDetails - END");
	}

	/**
	 * Retrieve state list.retrieve all the state list information for Delivery
	 * Addresses.
	 */
	private void retriveStateList() {
		log.debug("retriveStateList - START");
		IStateSO stateSo = null;
		StateResDTO resDto = null;
		ICountrySO countrySo = null;
		this.getManagedBean().setDeliveryAddressSelect(null);
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
		log.debug("retriveStateList - END");
	}

	/**
	 * District bystate.retrieve all the District list information by state for
	 * Delivery Addresses.
	 * 
	 * @param event
	 *            (Long stateId).
	 */
	public void districtBystate(AjaxBehaviorEvent event) {
		log.debug("districtBystate - START");
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

			if (getManagedBean().getQt().isZonal()) {
				PmParameter zonelocationLavel = this.getManagedBean().getQt()
						.getZoneLocationLevel();
				if (null != zonelocationLavel
						&& null != zonelocationLavel.getParameterId()) {
					PmParameter parameter = getPmParameter(zonelocationLavel
							.getParameterId());
					this.getManagedBean().setLocationLevel(
							parameter.getParameterCode());
				}
				String locationLevel = this.getManagedBean().getLocationLevel();
				List<String> codes = new ArrayList<String>();
				List<PmState> pmStates = new ArrayList<PmState>();
				List<PmDistrict> districts = new ArrayList<PmDistrict>();
				List<PmDivision> divisions = new ArrayList<PmDivision>();

				if (null != this.getManagedBean().getTplSpecZoneDtl()) {
					for (TplSpecZoneLoc specZoneLoc : this.getManagedBean()
							.getTplSpecZoneDtl().getTplSpecZoneLocs()) {
						if (ParameterConst.LOCATION_LEVEL_STATE
								.equalsIgnoreCase(locationLevel)) {
							PmState pmState = specZoneLoc.getPmState();
							if (null != pmState) {
								if (!codes.contains(pmState.getStateCode())) {
									codes.add(pmState.getStateCode());
									pmStates.add(pmState);
								}
							}
							this.getManagedBean().setStateList(pmStates);
						} else if (ParameterConst.LOCATION_LEVEL_DISTRICT
								.equalsIgnoreCase(locationLevel)) {
							if (getManagedBean().getStateCode().equals(
									specZoneLoc.getPmState().getStateCode())) {
								PmDistrict district = specZoneLoc
										.getPmDistrict();
								if (null != district) {
									if (!codes.contains(district
											.getDistrictCode())) {
										codes.add(district.getDistrictCode());
										districts.add(district);
									}
								}
							}
							this.getManagedBean().setDistrictList(districts);
						} else if (ParameterConst.LOCATION_LEVEL_DIVISION
								.equalsIgnoreCase(locationLevel)) {
							PmDivision divison = specZoneLoc.getPmDivision();
							if (null != divison) {
								if (!codes.contains(divison.getDivisionCode())) {
									codes.add(divison.getDivisionCode());
									divisions.add(divison);
								}
							}
							this.getManagedBean().setDivisionList(divisions);
						}
					}
				}
				codes = null;
				pmStates = null;
				districts = null;
				divisions = null;
			} else {
				if (ParameterConst.SARAK_SK_CODE.equalsIgnoreCase(this
						.getManagedBean().getStateCode())) {
					this.getManagedBean().setDivisionList(
							PmCommonUtil.retrievePmDivisionList(stateId));
				} else {
					districtSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IDistrictSO.class);
					resDto = districtSo.retriveDistrictListByStateID(reqDTO);
					if (resDto != null) {
						this.getManagedBean().setDistrictList(
								resDto.getDistrictList());
					}
				}
			}
		} finally {
			districtSo = null;
		}
		log.debug("districtBystate - END");
	}

	/**
	 * this method is used to get the pmParameter object based on parameterId.
	 * 
	 * @param parameterId
	 * @return PmParameter
	 */
	private PmParameter getPmParameter(Long parameterId) {
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterId(parameterId);
		return parameterSO.retrieveParameterById(parameterReqDTO)
				.getPmParameter();

	}

	/**
	 * Retrieve division district.retrieve all the District list information by
	 * division for Delivery Addresses.
	 * 
	 * @param event
	 *            (Long divisionId).
	 */
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

	/**
	 * District code.set District code for Delivery Address Search.
	 * 
	 * @param event
	 *            (Long districtId)
	 */
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

	/**
	 * Retrieve ministry list.retrieve all the Ministry list information for
	 * Delivery Addresses.
	 */
	private void retriveMinistryList() {
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

	/**
	 * De part ment by ministry.retrive PTJ Group List by mininstry for Delivery
	 * Addresses.
	 * 
	 * @param event
	 *            (Long ministryId).
	 */

	public void retrievePTJGroupListByMinistry(Long ministryId) {
		IOrgProfileSO orgProfileSO = null;
		try {

			if (null != ministryId) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(ministryId);
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				/*
				 * OrgProfileResDTO resDTO = orgProfileSO
				 * .retrievePTJGroupListByMinistry(reqDTO);
				 */
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());

				this.getManagedBean().setDepartmentList(
						resDTO.getOrgProfileList());

			} else {
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());

				this.getManagedBean().setDepartmentList(
						new ArrayList<PmOrgProfile>());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	/**
	 * Ptj by depart ment.retrive Ptj List by PTJ Group for Delivery Addresses.
	 * 
	 * @param departmentd
	 *            the departmentd
	 */
	public void retrivePTJByPTJGroup(Long ptjGroupId) {
		IOrgProfileSO orgProfileSO = null;
		try {

			if (null != ptjGroupId) {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(ptjGroupId);
				reqDTO.setOrgCode(OrganizationType.DIVISION.value());
				/*
				 * OrgProfileResDTO resDTO = orgProfileSO
				 * .retrievePTJListByMinistry(reqDTO);
				 */

				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveChildDDListByParentOrgProfileId(reqDTO);
				if (null != resDTO.getOrgProfileDDList()) {
					this.getManagedBean().setPtjtList(
							resDTO.getOrgProfileDDList());
				}
			} else {
				this.getManagedBean().setPtjtList(new ArrayList<Object[]>());
			}

		} finally {
			orgProfileSO = null;
		}

	}

	/**
	 * Retrieve committee endorsment.retrieve all the Committee Endorsmnet menu
	 * information and display in Specification committee.
	 */
	private void retriveCommitteeEndorsment() {
		ICommitteSO committeSO = null;
		CommitteResDTO resDTO = null;
		IQTDetailSO qtDetailSO;
		QuationTenderResDTO resDto;

		try {

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
			resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			qtReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			qtReqDTO.setDocType(ParameterConst.DOCTYPE_SPEC_COMMITTE);
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			resDto = qtDetailSO.retriveQtRemarks(qtReqDTO);

			if (resDto.getRemarks() == null) {
				if (null == this.getManagedBean().getSpecificationRemarks()) {
					this.getManagedBean().setSpecificationRemarks(
							new ArrayList<ScQtSpecificationRemark>());
					Long currentStatusId = this.getManagedBean().getCurrentStatusId();
					if (null != currentStatusId
							&& (currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT)
									|| currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF)
									|| currentStatusId.equals(QTStatusConst.QT_PENDING_MSC_APPROVAL)
									|| currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
									//33819 - start
									|| currentStatusId.equals(QTStatusConst.SE_PENDING_ENDORSMENT) 
									|| currentStatusId.equals(QTStatusConst.SE_PENDING_SIGNOFF) 
									|| !this.getManagedBean().isReadOnly())) {
									//33819 - end
						initializeRemarks(this.getManagedBean().getQt(), this
								.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(), this
								.getManagedBean().getSpecificationRemarks(),
								ScQtSpecificationRemark.class);
					}
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
					Long currentStatusId = this.getManagedBean().getCurrentStatusId();
					if (null != currentStatusId
							&& (currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT)
									|| currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF)
									|| currentStatusId.equals(QTStatusConst.QT_PENDING_MSC_APPROVAL)
									|| (currentStatusId.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION) && !this
											.getManagedBean().isReadOnly())
									//33819 - start
									|| currentStatusId.equals(QTStatusConst.SE_PENDING_ENDORSMENT) 
									|| currentStatusId.equals(QTStatusConst.SE_PENDING_SIGNOFF) 
									|| !this.getManagedBean().isReadOnly())) {
									//33819 - end
						initializeRemarks(this.getManagedBean().getQt(), this
								.getManagedBean().getQt()
								.getCurrentWorkFlowStatus(), this
								.getManagedBean().getSpecificationRemarks(),
								ScQtSpecificationRemark.class);
					}
				}
				remarkList = null;
			}
			List<ScQtCommitteeActivity> committeeActivities = resDTO
					.getCommitteActivities();
			if (null != this.getManagedBean().getCurrentStatusId()
					&& QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH
							.equals(this.getManagedBean().getCurrentStatusId())) {
				log.debug("60034L" + this.getManagedBean().getCurrentStatusId());
				if (CollectionUtils.isNotEmpty(committeeActivities)) {
					for (ScQtCommitteeActivity activity : committeeActivities) {
						if (!ParameterConst.COMMITTEE_ROLE_CHAIRPERSON
								.equalsIgnoreCase(activity
										.getScCommitteeMember().getMemberRole()
										.getParameterCode())) {
							activity.setSignedOff(Boolean.TRUE);
						}
					}

				}
			}

			// 25037 - start
			ScQtCommittee qtCommittee = committeSO.retrieveCommitteByQtIdNCommType(reqDTO).getScQtCommittee();
			ScCommittee committee = qtCommittee != null ? qtCommittee.getScCommittee() : null;
			List<ScCommitteeMember> memberList = committee != null ? committee.getScCommitteeMembers()
					: new ArrayList<ScCommitteeMember>();
			if (CollectionUtils.isNotEmpty(memberList) && CollectionUtils.isNotEmpty(committeeActivities)) {
				qtCommittee = committeeActivities.get(0).getScQtCommittee();
				Map<Object, Object> map = CustomUtils.listToMap(committeeActivities,
						"getScCommitteeMember.getCommitteeMemberId");
				List<ScQtCommitteeActivity> missingActivities = new ArrayList<>();
				for (ScCommitteeMember member : memberList) {
					if (map.get(member.getCommitteeMemberId()) == null) {
						ScQtCommitteeActivity activity = new ScQtCommitteeActivity();
						activity.setScQtCommittee(qtCommittee);
						activity.setScCommitteeMember(member);
						setUserDetails(activity);
						missingActivities.add(activity);
					}
				}
				committeeActivities.addAll(missingActivities);
				Collections.sort(committeeActivities, new Comparator<ScQtCommitteeActivity>() {
					List<String> sortOrder = Arrays.asList(SourcingConstants.COMMITTEE_ROLE_SORT_ORDER);
					@Override
					public int compare(ScQtCommitteeActivity o1, ScQtCommitteeActivity o2) {
						return Integer.valueOf(
								sortOrder.indexOf(o1.getScCommitteeMember().getMemberRole().getParameterCode()))
								.compareTo(
										Integer.valueOf(sortOrder.indexOf(o2.getScCommitteeMember().getMemberRole()
												.getParameterCode())));
					}
				});
			}
			// 25037 - end

			this.getManagedBean().setCommitteeActivities(committeeActivities);

		} finally {
			committeSO = null;
			qtDetailSO = null;
			resDTO = null;
		}
	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Close listener.close popup window.
	 */
	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
		// this.getManagedBean().setZoneLocation(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Technical spec bo validate.Validate Template spec Popup.
	 */
	public void technicalSpecBoValidate() {
		getManagedBean().setRequired(Boolean.TRUE);
		this.getManagedBean().setAutoUpdate(false);
		if (null != getManagedBean().getQt().getPmProcurementCategory()
				&& null != getManagedBean().getQt().getPmProcurementCategory()
						.getParameterId()) {
			getManagedBean().setSearchProcurementId(
					getManagedBean().getQt().getPmProcurementCategory()
							.getParameterId());
		}
		getCategoryCodesForClone();//#29468 Added by Vinod
	}

	/**
	 * Gets the dropdown location values.display the Location level dropdown
	 * values.
	 * 
	 * @param locationLevel
	 *            the location level
	 * @return the dropdown location values
	 */
	public SelectItem[] getDropdownLocationValues(String locationLevel,
			ScQt scQt) {
		IOrgProfileSO orgProfile = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDto = orgProfile
				.retrieveOrgStateCodeByOrgProfileId(scQt.getPmOrgProfile()
						.getOrgProfileId());
		this.getManagedBean().setAllStateCode(orgResDto.getStateStatusId());
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_SARAWAK) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
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
		} finally {
			orgProfile = null;
			types = null;
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	/**
	 * To get a list of users (DO+BO) for dropdown
	 * 
	 * @return the list of PmUser instance
	 */
	public List<PmUser> getCreatedByDropdownValues() {
		IUserSO userSO = null;
		List<PmUser> users = new ArrayList<PmUser>();

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			ScQt scQt = getManagedBean().getQt();

			// Get Desk Officer User
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(scQt.getCreatedBy());
			PmUser doUser = userSO.retrieveUserByUserId(reqDTO).getPmUser();
			if (doUser != null) {
				users.add(doUser);
			}

			// Get Specification Initiator Users
			reqDTO = new UserReqDTO();
			List<Long> orgProfileIds = new ArrayList<Long>();
			reqDTO.setRoleCode(RoleCode.BIZ_OWNER.value());
			orgProfileIds.add(scQt.getPmOrgProfile().getOrgProfileId());
			orgProfileIds.add(scQt.getOwnerOrgProfile().getOrgProfileId());
			reqDTO.setOrgProfileIds(orgProfileIds);
			List<PmUser> boUsers = userSO.retrieveUserListByRole(reqDTO)
					.getUserList();
			if (boUsers != null && boUsers.size() > 0) {
				users.addAll(boUsers);
			}
			orgProfileIds = null;
		} finally {
			userSO = null;
		}

		return users;
	}

	/**
	 * Prepare spec req.Prepare Data for Business Owner
	 * save/Submit/Accept/Revise buttons.
	 * 
	 * @return the specification req dto
	 */

	private SpecificationReqDTO prepareSpecReq() {

		SpecificationReqDTO reqDTO = new SpecificationReqDTO();

		ScQt scQt = this.getManagedBean().getQt();
		scQt.setScQtStatus(null);
		scQt.setMaxProposal(1);
		scQt.setScheduleType("F");
		ScQtSupplierCriteria qtSupplierCriteria = scQt
				.getScQtSupplierCriteria();

		if (!scQt.isPanel() && null != qtSupplierCriteria) {
			if (scQt.isZonal()
					&& scQt.getQtType().equals(
							ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				List<TreeNode> zoneTrees = this.getManagedBean()
						.getZoneSuppQualTrees();
				if (zoneTrees == null) {
					zoneTrees = this.getManagedBean().getZoneGeneralTrees();
				}
				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
						zoneTrees, null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						qtSupplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias()
							.setScQtSupplierCriteria(qtSupplierCriteria);
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
				} else {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);

					}
				}
				qtSupplierCriteria.setScQt(scQt);
				setUserDetails(qtSupplierCriteria);
				scQt.setScQtSupplierCriteria(qtSupplierCriteria);
			}
			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equals(scQt
					.getQtType())
					&& !scQt.getScQtSupplierCriteria().isAllState()) {
				List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) convertTreeNodesToLocationCriteria(
						this.getManagedBean().getZoneTrees(),
						qtSupplierCriteria);
				if (locCriteria != null && !locCriteria.isEmpty()) {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(
							locCriteria);
				} else {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
				}
			}

			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equals(scQt
					.getQtType())
					&& ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt
							.getProcurementMode().getParameterCode())
					&& this.getManagedBean().getQt().getScQtSupplierCriteria()
							.isAllState()) {
				if (null != qtSupplierCriteria) {

					qtSupplierCriteria.setLocationLevel(null);
					qtSupplierCriteria.setScQtLocCriteria(null);
				}
			}

			qtSupplierCriteria.setScQt(scQt);
			setUserDetails(qtSupplierCriteria);
			scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			if (ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(scQt
					.getQtType())) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}
			} else if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
					.equalsIgnoreCase(scQt.getQtType())) {
				scQt.setZonal(false);
				scQt.setZoneLocationLevel(null);
				if (null != scQt.getScQtSupplierCriteria()) {
					scQt.getScQtSupplierCriteria().setAllState(false);
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
						.getQtCategoryList();
				if (null != openList && !openList.isEmpty()) {
					scQt.setScQtCategorys(openList);
				}

			}
		} else if (scQt.isPanel()) {

			List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
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
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					zoneCriteria.setModified(true);
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				scQt.setZonal(true);
				//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
			    if (qtSupplierCriteria != null )
				   qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
						zoneLocal);

			}

			// scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

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

		if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			List<ScQtRemark> remarksList = this.getManagedBean()
					.getScQtRemarksList();

			if (isNotNullOREmpty(remarksList)) {
				scQt.setScQtRemarks(finalizeRemarks(remarksList));

			}
		}

		scQt.setScQtCommittees(null);
		if (!scQt.isBsvRequired()) {
			scQt.setScQtBriefings(null);
		}
		reqDTO.setScQt(scQt);
		// reqDTO.setTobeRemoveLocCriteriaList(getManagedBean().getExistingSupplierLocationList());

		return reqDTO;

	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {

	}

	/**
	 * Retrive specdetails.retrive all the general menu information and display.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param docType
	 *            the doc type
	 */
	public void retriveSpecdetails(Long qtId) {

		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(qtId);
	}
	
	//#28784 - start
	public void retriveDemoSpecdetails(Long qtId) {

		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveDemoQtDetails(qtId);
	}
	//#28784 - end
	
	/**
	 * Tier event.change Evaluation Type(OneTier/TwoTier) radion button in
	 * business owner.
	 * 
	 * @param event
	 *            (String EvaluationType).
	 */
	public void tierEvent(AjaxBehaviorEvent event) {
		String type = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setEvaluationType(type);
	}

	/**
	 * Technical check list type change.change Technical Checilist Type Dropdown
	 * in specification committee based on type you will get action values in
	 * technical checklist
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (List<ScQtCheckList> checkList).
	 */
	public void technicalCheckListTypeChange(int index,
			List<ScQtCheckList> checkList) {
		// Long type = (Long) event.getComponent().getAttributes().get("value");

		ScQtCheckList list = checkList.get(index);
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc
					.getPmParameter()
					.getParameterId()
					.equals(list.getDefinationType()
							.getParameterId())) {// Sqube QT Master Bug Minor G10
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setDefinationType((PmParameter) paramDesc.getPmParameter().cloneEntity()); // Bug #34065@Syazani 01/11/2022
				break;
			}
		}

		try {
			this.getManagedBean().setTechnicalChecklistAction(null);
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(parameterCode)) {
				list.setScoringReq(true);

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_MANUAL_SUBMISSION);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(parameterCode)) {
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}

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

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(parameterCode)) {
				list.setScoringReq(true);

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_PRESENTATION_DEMO);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
					.equalsIgnoreCase(parameterCode)) {

				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_SUPPLIER_UPLOAD);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);

			}
			// Bug #35219@Syazani 07/11/2023 -- Start
			selectOneItemActionAndDefinationBugRepair(checkList, this.getManagedBean()
					.getScQtCheckSt2techProposalLstClone(), index);
			selectOneItemActionAndDefinationBugRepair(this.getManagedBean().getScQtCheckSt2financeProposalList(), this
					.getManagedBean().getScQtCheckSt2financeProposalLstClone(), null);
			// Bug #35219@Syazani 07/11/2023 -- End
						
			checkList.set(index, list);
			this.getManagedBean().setScQtCheckSt2techProposal(checkList);
			searchBean = null;
			
		} finally {
			parameterSo = null;
		}

	}

	/**
	 * financial check list type change.change financial Checilist Type Dropdown
	 * in specification committee based on type you will get action values in
	 * financial checklist
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (List<ScQtCheckList> checkList).
	 */
	public void financialCheckListTypeChange(int index,
			List<ScQtCheckList> checkList) {
		// Long type = (Long) event.getComponent().getAttributes().get("value");
		ScQtCheckList list = checkList.get(index);
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {
			// Sqube QT SIT Bug Minor G15@Syazani 09/11/2023 - Start
			if (paramDesc.getPmParameter() != null && list.getDefinationType() != null
					&& paramDesc.getPmParameter().getParameterId().equals(list.getDefinationType().getParameterId())) {
				// Sqube QT SIT Bug Minor G15@Syazani 09/11/2023 - End
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				list.setDefinationType((PmParameter) paramDesc.getPmParameter().cloneEntity()); // Bug #34065@Syazani 01/11/2022
				break;
			}
		}

		try {
			this.getManagedBean().setFinancialChecklistAction(null);
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
					.equalsIgnoreCase(parameterCode)) {
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}

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

				this.getManagedBean().setFinancialChecklistAction(
						Arrays.asList(uploaditems));

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
					.equalsIgnoreCase(parameterCode)) {
				ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
				docDefTypeReqDTO
						.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
				docDefTypeReqDTO
						.setParameterCode(ParameterConst.CHECKLIST_ACTION_SUPPLIER_UPLOAD);
				ParameterResDTO docDefTypeResDTO = parameterSo
						.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
				list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));

				list.setAction(docDefTypeResDTO.getPmParameter());
				list.setScQtCheckListAttachment(null);
			}
			// Bug #35219@Syazani 07/11/2023 -- Start
			selectOneItemActionAndDefinationBugRepair(checkList, this.getManagedBean()
					.getScQtCheckSt2financeProposalLstClone(), index);
			selectOneItemActionAndDefinationBugRepair(this.getManagedBean().getScQtCheckSt2techProposal(), this
					.getManagedBean().getScQtCheckSt2techProposalLstClone(), null);
			// Bug #35219@Syazani 07/11/2023 -- End
						
			checkList.set(index, list);
			this.getManagedBean().setScQtCheckSt2financeProposalList(checkList);
			searchBean = null;
		} finally {
			parameterSo = null;
		}
		
	}

	/**
	 * Gets the financial checklist def type values.display Financial Checklist
	 * Type Dropdown values.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getFinancialChecklistDefTypeValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		searchBean = null;
		recommendList = null;
		return items;
	}

	/**
	 * Gets the fin checklist action values.display Financial Checklist action
	 * values in dropdown.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getFinChecklistActionValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
		recommendList.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);
		searchBean = null;
		recommendList = null;
		return items;
	}

	/**
	 * Gets the technical checklist def type values.display Technical Checklist
	 * Type Dropdown values.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getTechnicalChecklistDefTypeValues(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);

		// 11104-start
		if (this.getManagedBean().getSpecMaintenanceScreen().equals(getSpecMaintenance())
				&& !this.getManagedBean().isSampleShowHide()) {
			recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE);
		}
		// 11104-End

		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
		if (null != this.getManagedBean().getQt()
				&& ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this.getManagedBean().getQt()
						.getEvaluationType())) {
			// #28784 - Demo WIP and normal Demo
			if (!this.getManagedBean().isNewVersion()) {
				recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);
			}
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode", SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		searchBean = null;
		recommendList = null;
		return items;
	}
	
/** #21695 FARID START */	
	/**
	 * Gets the technical checklist def type values.display Technical Checklist IF PARTIAL
	 * Type Dropdown values.
	 * 
	 * @param type
	 *            (String type)
	 * @return SelectItem[].
	 */
	public SelectItem[] getTechnicalChecklistDefTypeValuesPartial(String type) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
		recommendList.add(ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION);
		if (null != this.getManagedBean().getQt()
				&& ParameterConst.EVALUATION_TYPES_TWO_TIER
						.equalsIgnoreCase(this.getManagedBean().getQt()
								.getEvaluationType())) {

			recommendList
					.remove(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);
		}

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_DEFINITION_TYPE, searchBean);
		searchBean = null;
		recommendList = null;
		return items;
	}
	/** #21695 FARID END */	

	/**
	 * Spec title change.Event handle change Title in Template Spec.
	 * 
	 * @param event
	 *            (String title)
	 * 
	 */
	public void specTitleChange(AjaxBehaviorEvent event) {
		// Boolean panel = this.getManagedBean().getQt().isPanel();
		String title = (String) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().getScQtChecklist().setTitle(title);
	}

	/**
	 * Technical check list title change.Event handle Change Title in Technical
	 * Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void technicalCheckListTitleChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);

	}

	/**
	 * Financial check list title change.Event handle change title in Financial
	 * Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void financialCheckListTitleChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);

	}

	/**
	 * Technical checklist drop down action change.Event handle change Action
	 * dropdown in Technical Checklist.
	 * 
	 * @param index
	 *            the index
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void technicalChecklistDropDownActionChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				if (ParameterConst.CHECKLIST_ACTION_VIEW
						.equalsIgnoreCase(parameterCode)) {
					list.setScoringReq(false);
				}
				//#30493 - set status when change supplier action - start
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				
				}else{
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				
				}
				//#30493 - set status when change supplier action - end

				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}

		// Bug #35219@Syazani 07/11/2023 -- Start
		selectOneItemActionAndDefinationBugRepair(checklist, this.getManagedBean()
				.getScQtCheckSt2techProposalLstClone(), index);
		selectOneItemActionAndDefinationBugRepair(this.getManagedBean().getScQtCheckSt2financeProposalList(), this
				.getManagedBean().getScQtCheckSt2financeProposalLstClone(), null);

		checklist.set(index, list);
		this.getManagedBean().setScQtCheckSt2techProposal(checklist);
		// Bug #35219@Syazani 07/11/2023 -- End
	}

	/**
	 * Financial check list drop down action change.Event handle Change Action
	 * Dropdown in Financial Checklist.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 */
	public void financialCheckListDropDownActionChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		String parameterCode = null;
		for (PmParameterDesc paramDesc : localParamDesc) {

			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(list.getAction().getParameterId()))) {
				parameterCode = paramDesc.getPmParameter().getParameterCode();
				if (ParameterConst.CHECKLIST_ACTION_VIEW
						.equalsIgnoreCase(parameterCode)) {
					list.setScoringReq(false);
				}
				//#30493 - set status when change supplier action - start
				if (null == list.getScQtCheckListAttachment()) {
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				
				}else{
					list.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				
				}
				//#30493 - set status when change supplier action - end
				list.setAction(paramDesc.getPmParameter());
				break;
			}
		}

		// Bug #35219@Syazani 07/11/2023 -- Start
		selectOneItemActionAndDefinationBugRepair(checklist, this.getManagedBean()
				.getScQtCheckSt2financeProposalLstClone(), index);
		selectOneItemActionAndDefinationBugRepair(this.getManagedBean().getScQtCheckSt2techProposal(), this
				.getManagedBean().getScQtCheckSt2techProposalLstClone(), null);

		checklist.set(index, list);
		this.getManagedBean().setScQtCheckSt2financeProposalList(checklist);
		// Bug #35219@Syazani 07/11/2023 -- End
	}

	/**
	 * Sets the evaluation type.based on Estimated Price set Evaluation Type.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 */
	public void populateEvaluationTypes(ScQt scQt) {
		if (isOneTierThresHold(scQt.getEstimatedAmount())) {

			List<SelectItem> itemList = new ArrayList<SelectItem>();
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.SC_EVALUATION_TYPE);

			for (PmParameterDesc paramDesc : localParamDesc) {
				String evaluationTypeCode = paramDesc.getPmParameter()
						.getParameterCode();
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER
						.equals(evaluationTypeCode)) {

					itemList.add(new SelectItem(evaluationTypeCode, paramDesc
							.getCodeName()));

					if (scQt.getEvaluationType() == null) {
						scQt.setEvaluationType(evaluationTypeCode);
					}
				}
			}
			getManagedBean().setEvaluationTypeItem(itemList);
		}
	}

	/**
	 * Fulfilment change.changed Fulfilemnt Dropdown in specification committee.
	 * 
	 * @param event
	 *            (Long fulfilmentId)
	 */
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		/*
		 * IQTSpecSO specso = getSpecSO(); SpecificationReqDTO specReqDTO = new
		 * SpecificationReqDTO();
		 */try {
			Long type = (Long) event.getComponent().getAttributes()
					.get("value");
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(Long.valueOf(type))) {

					this.getManagedBean().setFulfilmentType(
							paramDesc.getPmParameter().getParameterCode());
					this.getManagedBean().getQt()
							.setFulfilmentType(paramDesc.getPmParameter());
					break;
				}
			}

			if (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equalsIgnoreCase(this
					.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

			} else if (ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
				this.getManagedBean().setTotalProgressivePayment(0);

			} else if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
					.equalsIgnoreCase(this.getManagedBean().getFulfilmentType())) {
				this.getManagedBean()
						.getQt()
						.setScheduleType(
								ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
				this.getManagedBean().setTotalFrequency(0);
				this.getManagedBean().setTotalProgressivePayment(0);

			}
			this.getManagedBean().getQt().setScQtSchedules(null);
			this.getManagedBean().getQt().setScQtProgressivePayments(null);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT,true);
			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE,true);

		} finally {

		}

	}

	/**
	 * Gets the fulfilment dtls.set Fulfilment Type parameter code in Spec
	 * committee.
	 * 
	 * @param scQt
	 *            (ScQt scQt)
	 * 
	 */
	private void getfulfilmentDtls(ScQt scQt) {
		if (null != scQt.getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(
					scQt.getFulfilmentType().getParameterCode());
		} else {
			scQt.setFulfilmentType(new PmParameter());
		}

	}

	/**
	 * Procurement change.change Procurement Category Dropdown in Business
	 * owner.
	 * 
	 * @param event
	 *            (Long procuremntCategoryId).
	 */
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

	/**
	 * State change.supplier qualification criteria all states check/Uncheck
	 * checkbox.
	 * 
	 * @param event
	 *            (boolean allstates)
	 */
	public void stateChange(AjaxBehaviorEvent event) {
		boolean state = (boolean) event.getComponent().getAttributes()
				.get("value");

		this.getManagedBean().getQt().getScQtSupplierCriteria()
				.setAllState(state);

		if (!state) {
			if (null != this.getManagedBean().getZoneTrees()
					&& this.getManagedBean().getZoneTrees().isEmpty()) {
				this.getManagedBean().setZoneTrees(null);
			}
			if (null == this.getManagedBean().getQt().getScQtSupplierCriteria()
					.getLocationLevel()) {
				this.getManagedBean().getQt().getScQtSupplierCriteria()
						.setLocationLevel(new PmParameter());
			}

		} else {
			this.getManagedBean().setSupplierLocationList(
					getManagedBean().getExistingSupplierLocationList());
			this.getManagedBean().setZoneTrees(null);
		}
	}

	/**
	 * Schedule type change.Event handle change Schedule
	 * type(Schedule/Progressive payment)in Specification committee.
	 * 
	 * @param scqt
	 *            (ScQt scqt).
	 */
	public void scheduleTypeChange(ScQt scqt) {

		String scheduleType = scqt.getScheduleType();
		log.debug("display schedule type" + scheduleType);
		log.debug("display fulfilment  type"+ this.getManagedBean().getFulfilmentType());
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scheduleType)) {
			scheduleType = ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT;
			this.getManagedBean().setTotalProgressivePayment(0);
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			//scheduleType = ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE;
			//this.getManagedBean().setTotalFrequency(0);
		}
		removefullfillmentDetails(scheduleType,true);
	}

	/**
	 * Template change.Change Templte Type(Standard/Non Standard)in Template
	 * Spec.
	 * 
	 * @param event
	 *            (String templateType).
	 */
	public void templateChange(AjaxBehaviorEvent event) {
		String template = (String) event.getComponent().getAttributes()
				.get("value");
		templateSpecCreate.resetData();
		this.getManagedBean().setStandardTemplate(template);
		this.getManagedBean().setSearchCreatedBy(null);
		if (null != getManagedBean().getQt().getPmProcurementCategory()
				&& null != getManagedBean().getQt().getPmProcurementCategory()
						.getParameterId()) {
			getManagedBean().setSearchProcurementId(
					getManagedBean().getQt().getPmProcurementCategory()
							.getParameterId());
		}
	}

	/**
	 * Item type change.Change Item Type(product/Service) Dropdown in Template
	 * Spec Popup.
	 * 
	 * @param event
	 *            (Long itemType)
	 */
	public void itemTypeChange(AjaxBehaviorEvent event) {
		Long itemId = (Long) event.getComponent().getAttributes().get("value");
		if (null != itemId) {
			this.getManagedBean().setSearchItemTypeId(itemId);
			// this.getManagedBean().setRequired(false);

		}
	}

	/**
	 * Select supplier location.chnage Location level dropdown in Supplier
	 * Qualification criteria menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectSupplierLocation(AjaxBehaviorEvent event) {
		if (null != getManagedBean().getZoneTrees()
				&& !getManagedBean().getZoneTrees().isEmpty()) {
			this.getManagedBean().setSupplierLocationList(
					getManagedBean().getExistingSupplierLocationList());
			getManagedBean().setZoneTrees(null);
		}
	}

	/**
	 * Score type change.Change Checklist Score type(Auto/User) Scores in
	 * specification committee menu.
	 * 
	 * @param event
	 *            (String scoreType).
	 */
	public void scoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {
			if (scoreType.equalsIgnoreCase(QTConstants.USER_SCORE)) {
				ScQtCheckListScore score = new ScQtCheckListScore();
				
				//  #31489 - Start.
				if (0 == this.getManagedBean().getCriteriaType() && null != this.getManagedBean().getScQtChecklist()
						&& null != this.getManagedBean().getScQtChecklist().getPmDocument()) {
					log.info("SpecificationBackingBean.scoreTypeChange(): settinf criteriaType as bank statement");
					if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(this.getManagedBean().getScQtChecklist().getPmDocument()
								.getDocDefinitionKey())) {
						this.getManagedBean().setCriteriaType(QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT);
					}
				}
				// #31489 - End.
				
				score.setCriteriaType(this.getManagedBean().getCriteriaType());
				setUserDetails(score);
				this.getManagedBean().setScQtCheckListScore(score);
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

	/**
	 * Companyscore type change.Change Checklist Score type(Auto/User) Scores in
	 * specification committee menu.
	 * 
	 * @param event
	 *            (String ScoreType).
	 */
	public void companyscoreTypeChange(AjaxBehaviorEvent event) {
		String scoreType = (String) event.getComponent().getAttributes()
				.get("value");
		List<ScQtCheckListScore> toBeRemoveList = this.getManagedBean()
				.getToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtCheckListScore>();
		}
		if (null != scoreType && !"".equalsIgnoreCase(scoreType)) {

			if (scoreType.equalsIgnoreCase(QTConstants.USER_SCORE)) {
				ScQtCheckListScore score = new ScQtCheckListScore();
				score.setCriteriaType(this.getManagedBean()
						.getOtherCriteriaType());
				setUserDetails(score);
				this.getManagedBean().setScQtContractScore(score);
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
	//#25138 Reendorsed - start
	/**
	 * Score type change.Change Offer Price Score type(Auto/User) Scores in
	 * specification committee menu.
	 * 
	 * @param event
	 *            (String scoreType).
	 */
	public void offerscoreTypeChange(AjaxBehaviorEvent event) {
		IQTSpecSO qtspecSo = null;
		scoreTypeN = event.getComponent().getAttributes().get("value").toString();
		// String scoreType = this.getManagedBean().getOfferScoreType();
		// scoreTypeN = this.getManagedBean().getOfferScoreType();
		List<ScQtScoreOfferPrice> toBeRemoveList = this.getManagedBean().getOfferToBeRemoveList();
		if (null == toBeRemoveList) {
			toBeRemoveList = new ArrayList<ScQtScoreOfferPrice>();
		}
		if (null != scoreTypeN && !"".equalsIgnoreCase(scoreTypeN)) {
			if (scoreTypeN.equalsIgnoreCase(QTConstants.AUTO_SCORE)) {
				ScQtScoreOfferPrice score = new ScQtScoreOfferPrice();
				setUserDetails(score);

				this.getManagedBean().setScQtScoreOfferPrice(score);
				//====#31913 bug related to #25138==== start
				if(!this.getManagedBean().getQt().isZonal()){
					if (null != this.getManagedBean().getScoreOfferPrice()
							&& !this.getManagedBean().getScoreOfferPrice().isEmpty()) {
						this.getManagedBean().setScoreOfferPrice(new ArrayList<ScQtScoreOfferPrice>());
						
					}
				//====#31913 bug related to #25138==== end
				}
			} else {

				if (null != this.getManagedBean().getScQtScoreOfferPrice()) {
					toBeRemoveList.add(this.getManagedBean().getScQtScoreOfferPrice());
					
				}
				
			}
			this.getManagedBean().setOfferToBeRemoveList(toBeRemoveList);
			this.getManagedBean().setOfferScoreType(scoreTypeN);
			this.getManagedBean().getOfferPricePackage().setScoreType(new BigDecimal(scoreTypeN));
			
			if(this.getManagedBean().getQt().isZonal()){
				for(ScQtScoreOfferPrice scoreOfferprice : this.getManagedBean().getOfferPricePackageList()){
					scoreOfferprice.setScoreType(new BigDecimal(this.getManagedBean().getOfferScoreType()));
					
					if(scoreOfferprice.getScoreType().equals(new BigDecimal(QTConstants.USER_SCORE))){
						scoreOfferprice.setFromVariance(null);
						scoreOfferprice.setToVariance(null);
						scoreOfferprice.setBelowScore(null);
						scoreOfferprice.setAboveScore(null);
					}
					//====#31913 bug related to #25138==== start
					else{
						qtspecSo = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
						SpecificationReqDTO offerReqdto = new SpecificationReqDTO();
						SpecificationResDTO offerResdto = new SpecificationResDTO();
						offerReqdto.setZoneId(scoreOfferprice.getTplSpecZoneDtl().getSpecZoneId());
						offerResdto = qtspecSo.retrieveScoreOfferPriceByZone(offerReqdto);
						if(offerResdto.getScQtScoreOfferPrice()!=null){
							scoreOfferprice.setFromVariance(offerResdto.getScQtScoreOfferPrice().getFromVariance());
							scoreOfferprice.setToVariance(offerResdto.getScQtScoreOfferPrice().getToVariance());
							scoreOfferprice.setBelowScore(offerResdto.getScQtScoreOfferPrice().getBelowScore());
							scoreOfferprice.setAboveScore(offerResdto.getScQtScoreOfferPrice().getAboveScore());
							
						}
						
					}
					//====#31913 bug related to #25138==== end
				}
			}
			System.out.println("ajax_value==" + this.getManagedBean().getOfferScoreType());
		}
	}
	//#25138 Reendorsed - end

	/**
	 * Bank statementsfrom change.change bankStatemnt checklist month dropdown
	 * in specification committee.
	 * 
	 * @param event
	 *            (String formMonth)
	 */
	public void bankStatementsfromChange(AjaxBehaviorEvent event) {
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

	/**
	 * Bank statementsyears change.hange bankStatemnt checklist years dropdown
	 * in specification committee.
	 * 
	 * @param event
	 *            (String Year).
	 */
	public void bankStatementsyearsChange(AjaxBehaviorEvent event) {
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

	/**
	 * Calculate bank months.set StartMonth and EndMonth for bankStatement
	 * checklist.
	 * 
	 * @param j
	 *            (int month)
	 * @param currentYear
	 *            (int currentYear).
	 */
	private void calculateBankMonths(int j, int currentYear) {
		String Startmonth = "";
		if (j == 0) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.january",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 1) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.february",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 2) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.march",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 3) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.april",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 4) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.may",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 5) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.june",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}

		else if (j == 6) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.july",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 7) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.august",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 8) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.september",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 9) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.october",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 10) {
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.november",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 11) {

			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.december",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else if (j == 12) {
			currentYear = currentYear + 1;
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.january",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());

		} else if (j == 13) {
			currentYear = currentYear + 1;
			Startmonth = ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.february",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());

		}

		this.getManagedBean().setBankstmntTomonth(Startmonth);
		this.getManagedBean().setToMonth(String.valueOf(j));
		this.getManagedBean().setEndYear(String.valueOf(currentYear));
	}
	
	/*
	private TplSpecMaster isExistInDB(List<ScQtCheckList> checkList) {
		if (null != checkList && !checkList.isEmpty()) {
			for (ScQtCheckList specChecklist : checkList) {
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
						
						if(specChecklist.getPmStatus().equals(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED))){
							return resDTO.getSpecMaster();
						}
						

					}
				}
			}
		}
		return null;
	}
	*/
	
	/*
	private ScQtCheckList populateWeigthage(List<ScQtCheckList> list,ScQtCheckList checkList){
		//#8979 - danial
		boolean exist=false;
		//null!=isExistInDB(list)?true:false;
		TplSpecMaster spec=isExistInDB(list);
		if(null!=spec){
			exist=true;
		}
		
		this.getManagedBean().setSpecItemWeightageRequired(exist);
		boolean weightageType = false;
		if(exist){
			weightageType=isExistInDB(list).isWeightageReq();
					//retriveExistingTemplatespec(list);
		}
		checkList.getScQtDoc()
		.getTplSpecMaster().setWeightageReq(weightageType);
		return checkList;
	}
	*/

	/**
	 * Technical checklist edit action.click on pencil icon in technical
	 * checklist navigate to checklist score page.
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 * @param specType
	 *            ( String specType)
	 * @return navigate to checklist score page.
	 */
	public String technicalChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(null);
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

				//1873 - start - note 97
				this.getManagedBean().setFinTempScQtChecklist(null); 
				this.getManagedBean().setTempScQtChecklist(null);
				if(QTConstants.TECHNICAL_DOC_TYPE.equals(checkList.getDocCategory())){
					if(CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())){
						for(ScQtCheckList currentChecklist: this.getManagedBean().getScQtCheckSt2techProposal()){
							currentChecklist.setCurrentlySelected(false);
						}
						checkList.setCurrentlySelected(true);
					} 
					this.getManagedBean().setTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(checkList));
				} 
				//1873 - end - note 97
				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				specReqDTO
						.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO =null;
				templateresDTO = templateSpecSO
						.getTemplateMaster(specReqDTO);
				//#25138(wip 4serangkai) bug -Start 8/6/2023
				reqDTO.setScQt(checkList.getScQt());
				if (reqDTO.getScQt() != null) {
					specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
					
					this.getManagedBean().setWipStatusMax(specSo.checkWipScoreSettingMax(reqDTO));
				}
				//#25138(wip 4serangkai) bug -End 8/6/2023
				TemplateSpecBean templateSpecBean = getManagedBean()
						.getTemplateSpecBean();
				if(null!=templateresDTO){
					checkList.getScQtDoc().setTplSpecMaster(
							templateresDTO.getSpecMaster());
					//#8979 - danial
					//checkList=populateWeigthage(this.getManagedBean()
					//		.getScQtCheckSt2techProposal(),checkList);
				}else{
					//checkList=populateWeigthage(this.getManagedBean()
					//		.getScQtCheckSt2techProposal(),checkList);
				}		
				
				checkList.getScQtDoc()
				.getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean()
								.getQt()
								.getQtType()
								.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

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
				if (null != checkList.getPmStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList
								.getPmStatus().getStatusId())) {
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					ScQtZoneCriteria zoneCriteria = null;
					if (null != supplierCriteria
							&& null != supplierCriteria.getQtZoneCriterias()) {
						if (this.getManagedBean().getQt().isZonal()) {
							zoneCriteria = supplierCriteria
									.getQtZoneCriterias();
						}
					}
					TemplateSpecHelper.getInstance().updateZoneDtl(
							templateSpecBean.getSpecMaster(), zoneCriteria);
				}
				if (this.getManagedBean().getQt().isPanel()) {
					this.getManagedBean().setPanelItemCategory("");
					this.getManagedBean().setSpecNewable(true);
					this.getManagedBean().setUpdateSpec(true);
					if (null == this.getManagedBean().getPanelItemCategory()
							|| "".equals(this.getManagedBean()
									.getPanelItemCategory())) {
						// retriveSupplierQualificationCriteria("SC");
						// List<CtPanelItem> panelItemList =
						// getManagedBean().getPanelItemList();

						/*
						 * if (CollectionUtils.isNotEmpty(panelItemList)){
						 * if(null!=panelItemList.get(0).getItemCategoryId()){
						 * this
						 * .getManagedBean().setPanelItemCategory(panelItemList
						 * .get(0).getItemCategoryId().getParameterCode()); }
						 * 
						 * }
						 */
						boolean panelItem=false;
						if(null!=checkList.getScQtDoc().getTplSpecMaster().getCtPanelItem()){
							panelItem=true;
						}
						
						if(null!=templateresDTO 
								&& null!=templateresDTO.getSpecMaster() 
								&& null!=templateresDTO.getSpecMaster().getCtPanelItem()){
							panelItem=true;
						}
						if(panelItem){
							this.getManagedBean().setPanelItemCategory(
								ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC);
						}else{
							this.getManagedBean().setPanelItemCategory(
									ParameterConst.ITEM_CATEGORY_TYPE_GENERAL);
						}
						
						/** 31/10/2019 **/
//						if (null != templateresDTO.getSpecMaster()
//								&& null == templateresDTO.getSpecMaster()
//										.getCtPanelItem()) {
//							this.getManagedBean().setPanelItemCategory(
//									ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC);
//						} else {
//							this.getManagedBean().setPanelItemCategory(
//									ParameterConst.ITEM_CATEGORY_TYPE_GENERAL);
//						}
					}
					if ((null != this.getManagedBean().getPanelItemCategory() && !""
							.equals(this.getManagedBean()
									.getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
									.equalsIgnoreCase(this.getManagedBean()
											.getPanelItemCategory())) {
						this.getManagedBean().setSpecNewable(false);
						this.getManagedBean().setUpdateSpec(false);
					} else if ((null != this.getManagedBean()
							.getPanelItemCategory() && !"".equals(this
							.getManagedBean().getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_GENERAL
									.equalsIgnoreCase(this.getManagedBean()
											.getPanelItemCategory())) {

						setTechnicalSpecEnableDisable();
					}
				} else {
					setTechnicalSpecEnableDisable();
				}
				/*
				 * templateSpecBean.getSpecMaster().setScoringReq(
				 * this.getManagedBean().getQt().isTechEvaluationReq());
				 */
				List<ScQtCheckList> technicalChecklist=this.getManagedBean().getScQtCheckSt2techProposal();//30917-start
				int i=0;
				for(ScQtCheckList checklist:technicalChecklist){
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checklist
							.getDefinationType().getParameterCode())) {
						i++;
					}
				}
				if(i>1){
					this.getManagedBean().setSpecItemWeightageRequired(Boolean.TRUE);
				}//30917-End
				return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())) {//#11104-start
				List<ScQtCheckList> technicalChecklist=this.getManagedBean().getScQtCheckSt2techProposal();//#11104-start
				this.getManagedBean().setSampleSpecCheckList(new ArrayList<ScQtCheckList>());
				this.getManagedBean().getQt().setSampleReq(Boolean.TRUE);//11104ReendorseStart728
				for(ScQtCheckList checklist:technicalChecklist){
					if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checklist
							.getDefinationType().getParameterCode()) && QTStatusConst.TS_COMPLETED.equals(checklist.getPmStatus().getStatusId())) {//11104_newEndorseStart
						if(checkList.isBrandNew()){//#11104-start
							checklist.setCloned(Boolean.TRUE);	
						}//#11104-End						
						this.getManagedBean().getSampleSpecCheckList().	add(checklist);
					}
				}//#11104-End
				return TECHNICAL_CHECKLIST_SAMPLE_MAIN;//11104_newEndorseStart
			}//#11104-End
			else {
				setUserDetails(checkList);
				this.getManagedBean().setChecklistScoreType(null);
				this.getManagedBean().setCriteriaType(0);
				this.getManagedBean().setOtherCriteriaType(0);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setCheckListScore(null);
				this.getManagedBean().setScoringForYears(null);
				this.getManagedBean().setToBeRemoveList(null);
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
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV) {

								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {

									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						noOfContracts = null;
						noOfYears = null;
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_FED_GOV);
					// this.getManagedBean().setOtherCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListGovCompanyExperience.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						this.getManagedBean().setCompanyScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> noOfContracts = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> noOfYears = new ArrayList<ScQtCheckListScore>();
						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {

							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV) {

								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {

									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfContracts.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											noOfContracts);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV) {

								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									noOfYears.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											noOfYears);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						noOfContracts = null;
						noOfYears = null;
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV);
					// this.getManagedBean().setOtherCriteriaType(QTConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListPrivateCompanyExperience.xhtml?faces-redirect=true";
				}

				else if (QTConstants.TECHNICAL_STAFF_LIST
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);

					} else {

						List<ScQtCheckListScore> technicalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									technicalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											technicalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						technicalEmployee = null;
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_TOTAL_YEAR_EXPERIENCE);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListStaffList.xhtml?faces-redirect=true";
				} else if (QTConstants.TECHNICAL_NUMBER_EMPLOYEES
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
						this.getManagedBean().setChecklistScoreType(null);
						this.getManagedBean().setCompanyScoreType(null);
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> totalEmployee = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									totalEmployee.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											totalEmployee);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						totalEmployee = null;
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/technicalCheckListNoOfEmployees.xhtml?faces-redirect=true";
				}

			}
		} finally {
			specSo = null;
		}

		return null;

	}

	/**
	 * Return check list scores.checklist score in ascending order.
	 * 
	 * @param checklistScores
	 *            the checklist scores
	 */
	private void returnCheckListScores(List<ScQtCheckListScore> checklistScores) {

		Collections.sort(checklistScores);

	}

	/**
	 * Financial checklist edit action.click on pencil icon in financial
	 * checklist navigate to checklist score page.
	 * 
	 * @param index
	 *            (int index)
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 * @param specType
	 *            (String specType)
	 * @return navigate to checklist score page.
	 */
	public String financialChecklistEditAction(int index,
			ScQtCheckList checkList, String specType) {
		// 36211 start
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		// 36211 end
		IQTSpecSO specSo = null;
		SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setToBeRemoveList(
				new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		// 36211 Start
		SpecificationReqDTO specReqDto = new SpecificationReqDTO();
		specReqDto.setScQt(this.getManagedBean().getQt());
		this.getManagedBean().setWipStatusMax(specSO.checkWipScoreSettingMax(specReqDto));
		// 36211 End
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
				checkList.getScQtDoc()
				.getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
				templateSpecBean.setSpecMaster(checkList.getScQtDoc()
						.getTplSpecMaster());
				templateSpecBean.setWordBreakForQT(true);//#29007 Added Vinod

				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean()
								.getQt()
								.getQtType()
								.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					ScQtSupplierCriteria supplierCriteria = this
							.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

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
				// #25039#67#76 - start
				// To recalculate estimate amount (Item Total Indicative Price)
					System.out.println("===========reCalculateEstimateAmount start============");
					reCalculateEstimateAmount(templateSpecBean);
				// #25039#67#76 - end
				return "/xhtml/sc/boSctemplatespec/financialCommitteeTemplate.xhtml?faces-redirect=true";
			} else {
				setUserDetails(checkList);
				this.getManagedBean().setCriteriaType(0);
				this.getManagedBean().setOtherCriteriaType(0);
				this.getManagedBean().setCheckListScore(null);
				this.getManagedBean().setScoringForYears(null);
				this.getManagedBean().setCheckListScore(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setScoringForYears(
						new ArrayList<ScQtCheckListScore>());
				this.getManagedBean().setChecklistScoreType(null);
				this.getManagedBean().setCompanyScoreType(null);
				this.getManagedBean().setToBeRemoveList(null);
				this.getManagedBean().setToBeRemoveList(
						new ArrayList<ScQtCheckListScore>());
				if (QTConstants.FINANCIAL_GOV_CODE.equalsIgnoreCase(checkList
						.getPmDocument().getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						contarctAmount = null;
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListGovernementCompany.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PRIVATE_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {

						this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					} else {

						List<ScQtCheckListScore> contarctAmount = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									contarctAmount.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											contarctAmount);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						contarctAmount = null;
					}
					if (null == checkList.getScQtCheckListValue()) {
						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);
						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);
					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListPrivateCompany.xhtml?faces-redirect=true";

				} else if (QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> creditFacility = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									creditFacility.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											creditFacility);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						creditFacility = null;
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListCreditFacility.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_CASH_ON_HAND_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> cashOnHand = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									cashOnHand.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											cashOnHand);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						cashOnHand = null;
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND);
					this.getManagedBean().setScQtChecklist(checkList);

					return "/xhtml/sc/specification/specificationCommittee/financialCheckListCashOnhand.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_BANK_STMT_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					int year = Calendar.getInstance().get(Calendar.YEAR);

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getScQtCheckListValue()) {
						
						//#25138 fix lazy -start (adding condition isnotempty checklistscore)
						if(null!=checkList.getScQtCheckListValue() && isIndirectWithInstantiated(checkList.getScQtCheckListScores()) && CollectionUtils.isNotEmpty(checkList.getScQtCheckListScores())){ // Bug #34412@Syazani 20/06/2023
							List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();
							for (ScQtCheckListScore checklistScore : checkList.getScQtCheckListScores()) {
								if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT) {
									if (checklistScore.getScoreType().intValue() == Integer
											.valueOf(QTConstants.AUTO_SCORE)) {
										this.getManagedBean()
												.setChecklistScoreType(
														String.valueOf(checklistScore
																.getScoreType()
																.intValue()));
										bankStatement.add(checklistScore);
										this.getManagedBean().setCheckListScore(
												bankStatement);
									} else if (checklistScore.getScoreType()
											.intValue() == Integer
											.valueOf(QTConstants.USER_SCORE)) {
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
							
							 checkList.setScQtCheckListScores(null);
							 checkList.setScQtCheckListValue(null);
						}else{
							this.getManagedBean()
							.setStartYear(String.valueOf(year));
							this.getManagedBean().setEndYear(String.valueOf(year));
							//Bug #32862 - start
							this.getManagedBean().setBankstmntTomonth(null);
							this.getManagedBean().setToMonth(null);
							this.getManagedBean().setFromMonth(null);
							//Bug #32862 - end

							this.getManagedBean().setChecklistScoreType(null);

							ScQtCheckListValue checkListValue = new ScQtCheckListValue();
							setUserDetails(checkListValue);

							checkListValue.setScQtCheckList(checkList);
							checkList.setScQtCheckListValue(checkListValue);
						}

					} else {

						List<ScQtCheckListScore> bankStatement = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									bankStatement.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											bankStatement);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						bankStatement = null;
					}
					if (null == checkList.getScQtCheckListValue()) {

						this.getManagedBean()
								.setStartYear(String.valueOf(year));
						this.getManagedBean().setEndYear(String.valueOf(year));
						//Bug #32862 - start
						this.getManagedBean().setBankstmntTomonth(null);
						this.getManagedBean().setToMonth(null);
						this.getManagedBean().setFromMonth(null);
						//Bug #32862 - end

						// this.getManagedBean().setChecklistScoreType(null);

						ScQtCheckListValue checkListValue = new ScQtCheckListValue();
						setUserDetails(checkListValue);

						checkListValue.setScQtCheckList(checkList);
						checkList.setScQtCheckListValue(checkListValue);

					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListBankStatement.xhtml?faces-redirect=true";
				}

				else if (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores() || (resDTO.getCheckListScores().size()==0)) {
						if(isIndirectWithInstantiated(checkList.getScQtCheckListScores())){
							List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
							List<ScQtCheckListScore> currentRation = new ArrayList<ScQtCheckListScore>();
							for (ScQtCheckListScore checklistScore : checkList.getScQtCheckListScores()) {
								if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO) {
									if (checklistScore.getScoreType().intValue() == Integer
											.valueOf(QTConstants.AUTO_SCORE)) {
										this.getManagedBean()
												.setChecklistScoreType(
														String.valueOf(checklistScore
																.getScoreType()
																.intValue()));
										assetLiability.add(checklistScore);
										this.getManagedBean().setCheckListScore(
												assetLiability);
									} else if (checklistScore.getScoreType()
											.intValue() == Integer
											.valueOf(QTConstants.USER_SCORE)) {
										this.getManagedBean()
												.setChecklistScoreType(
														String.valueOf(checklistScore
																.getScoreType()
																.intValue()));
										this.getManagedBean()
												.setScQtCheckListScore(
														checklistScore);
									}

								} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO) {

									if (checklistScore.getScoreType().intValue() == Integer
											.valueOf(QTConstants.AUTO_SCORE)) {

										this.getManagedBean()
												.setCompanyScoreType(
														String.valueOf(checklistScore
																.getScoreType()
																.intValue()));
										currentRation.add(checklistScore);
										this.getManagedBean().setScoringForYears(
												currentRation);
									} else if (checklistScore.getScoreType()
											.intValue() == Integer
											.valueOf(QTConstants.USER_SCORE)) {
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
						}else{
							checkList.setScQtCheckListValue(resDTO
									.getScQtCheckListValue());
							this.getManagedBean().setChecklistScoreType(null);
						}

					} else {

						List<ScQtCheckListScore> assetLiability = new ArrayList<ScQtCheckListScore>();
						List<ScQtCheckListScore> currentRation = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									assetLiability.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											assetLiability);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									this.getManagedBean()
											.setScQtCheckListScore(
													checklistScore);
								}

							} else if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO) {

								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {

									this.getManagedBean()
											.setCompanyScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									currentRation.add(checklistScore);
									this.getManagedBean().setScoringForYears(
											currentRation);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						assetLiability = null;
						currentRation = null;
					}
					this.getManagedBean()
							.setCriteriaType(
									QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);
					this.getManagedBean()
							.setOtherCriteriaType(
									QTConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListAssetsLiabilities.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_SUPPLIER_DETAILS_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {
					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()
							|| resDTO.getCheckListScores().isEmpty()) {
						checkList.setScQtCheckListValue(resDTO
								.getScQtCheckListValue());
						this.getManagedBean().setChecklistScoreType(
								QTConstants.USER_SCORE);

						ScQtCheckListScore checklistScore = new ScQtCheckListScore();
						checklistScore
								.setCriteriaType(QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS);
						checklistScore.setScoreType(new BigDecimal(this
								.getManagedBean().getChecklistScoreType()));
						setUserDetails(checklistScore);
						this.getManagedBean().setScQtCheckListScore(
								checklistScore);

					} else {

						List<ScQtCheckListScore> suppliersScore = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									suppliersScore.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											suppliersScore);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						suppliersScore = null;
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_SUPPLIER_DETAILS);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialChecklistSupplierinfoMain.xhtml?faces-redirect=true";
				} else if (QTConstants.FINANCIAL_PAID_UP_CAPITAL_CODE
						.equalsIgnoreCase(checkList.getPmDocument()
								.getDocDefinitionKey())) {

					reqDTO.setScQtCheckList(checkList);
					resDTO = specSo.retrieveCheckListScoreNValue(reqDTO);
					if (null == resDTO.getCheckListScores()) {

						this.getManagedBean().setChecklistScoreType(null);

					} else {

						List<ScQtCheckListScore> paidUpCapital = new ArrayList<ScQtCheckListScore>();

						for (ScQtCheckListScore checklistScore : resDTO
								.getCheckListScores()) {
							if (checklistScore.getCriteriaType() == QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT) {
								if (checklistScore.getScoreType().intValue() == Integer
										.valueOf(QTConstants.AUTO_SCORE)) {
									this.getManagedBean()
											.setChecklistScoreType(
													String.valueOf(checklistScore
															.getScoreType()
															.intValue()));
									paidUpCapital.add(checklistScore);
									this.getManagedBean().setCheckListScore(
											paidUpCapital);
								} else if (checklistScore.getScoreType()
										.intValue() == Integer
										.valueOf(QTConstants.USER_SCORE)) {
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
						paidUpCapital = null;
					}
					this.getManagedBean().setCriteriaType(
							QTConstants.CRITERIA_TYPE_PAID_UP_CAPITAL_AMOUNT);
					this.getManagedBean().setScQtChecklist(checkList);
					return "/xhtml/sc/specification/specificationCommittee/financialCheckListPaidUpCapital.xhtml?faces-redirect=true";
				}
			}
		} finally {
			specSo = null;
		}

		return null;

	}

	/**
	 * Retrive from month to manth dtls.set Frommonth and tomonth for
	 * bankstatement checklist.
	 * 
	 * @param checkListValue
	 *            (ScQtCheckListValue checkListValue).
	 */
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
				} else if (startdate.equalsIgnoreCase("02")) {
					startMonth = "1";
				} else if (startdate.equalsIgnoreCase("03")) {
					startMonth = "2";
				} else if (startdate.equalsIgnoreCase("04")) {
					startMonth = "3";
				} else if (startdate.equalsIgnoreCase("05")) {
					startMonth = "4";
				} else if (startdate.equalsIgnoreCase("06")) {
					startMonth = "5";
				}

				else if (startdate.equalsIgnoreCase("07")) {
					startMonth = "6";
				} else if (startdate.equalsIgnoreCase("08")) {
					startMonth = "7";
				} else if (startdate.equalsIgnoreCase("09")) {
					startMonth = "8";
				} else if (startdate.equalsIgnoreCase("10")) {
					startMonth = "9";
				} else if (startdate.equalsIgnoreCase("11")) {
					startMonth = "10";
				} else if (startdate.equalsIgnoreCase("12")) {
					startMonth = "11";
				}

				if (enddate.equalsIgnoreCase("01")) {
					endToMonth = "0";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.january",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("02")) {
					endToMonth = "1";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.february",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("03")) {
					endToMonth = "2";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.march",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("04")) {
					endToMonth = "3";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.april",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("05")) {
					endToMonth = "4";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.may",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("06")) {
					endToMonth = "5";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.june",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				}

				else if (enddate.equalsIgnoreCase("07")) {
					endToMonth = "6";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.july",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("08")) {
					endToMonth = "7";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.august",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("09")) {
					endToMonth = "8";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.september",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("10")) {
					endToMonth = "9";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.october",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("11")) {
					endToMonth = "10";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.november",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				} else if (enddate.equalsIgnoreCase("12")) {
					endToMonth = "11";
					endMonth = ResourceBundleUtils.getMessage(
							"lbl.sc.checklist.month.december",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				}
				this.getManagedBean().setFromMonth(startMonth);
				this.getManagedBean().setBankstmntTomonth(endMonth);
				this.getManagedBean().setStartYear(startYear);
				this.getManagedBean().setEndYear(endYear);
				this.getManagedBean().setToMonth(endToMonth);
			} else {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				this.getManagedBean().setStartYear(String.valueOf(year));
				this.getManagedBean().setEndYear(String.valueOf(year));
			}
		}
	}

	/**
	 * Committee spec edit action.Event handle navigate to Template Spec in
	 * Specification committee.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (ScQtCheckList checklist)
	 * @param specType
	 *            (String specType)
	 * @return navigate to template spec.
	 */
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
			if (null != checklist.getPmStatus()
					&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checklist
							.getPmStatus().getStatusId())) {
				ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
						.getQt().getScQtSupplierCriteria();
				ScQtZoneCriteria zoneCriteria = null;
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					zoneCriteria = supplierCriteria.getQtZoneCriterias();
				}
				TemplateSpecHelper.getInstance().updateZoneDtl(
						templateSpecBean.getSpecMaster(), zoneCriteria);
			}
			if (this.getManagedBean().getQt().isPanel()) {
				if (null == this.getManagedBean().getPanelItemCategory()
						|| "".equals(this.getManagedBean()
								.getPanelItemCategory())) {
					retriveSupplierQualificationCriteria("SC");

				}
				if ((null != this.getManagedBean().getPanelItemCategory() && !""
						.equals(this.getManagedBean().getPanelItemCategory()))
						&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC
								.equalsIgnoreCase(this.getManagedBean()
										.getPanelItemCategory())) {
					this.getManagedBean().setSpecNewable(false);
					this.getManagedBean().setUpdateSpec(false);
				}
			}
			/*
			 * templateSpecBean.getSpecMaster().setScoringReq(
			 * this.getManagedBean().getQt().isTechEvaluationReq());
			 */

		} finally {
			templateSpecSO = null;
		}
		return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
	}

	/**
	 * Creates the spec list.
	 * 
	 * @param stage
	 *            the stage
	 * @return the sc qt check list
	 */
	private ScQtCheckList createSpecList(String stage) {

		ScQtCheckList additionProposalst1 = new ScQtCheckList();

		return additionProposalst1;
	}

	/**
	 * Adds the financial check list.add empty row for financial checklist
	 * datatable.
	 */
	public void addFinancialCheckList() {
		// IParameterSO parameterSo = null;
		try {
			/*
			 * parameterSo = BusinessDelegate.getInstance().getServiceObject(
			 * ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			 */
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
			Integer checkListSequence = checkTechnicalORFinancialAddChecklistSequence(st2financeproposalList);
			log.debug("========== financial return checklist sequence===="
					+ checkListSequence);
			st2financeproposalList2.setChecklistSeq(checkListSequence);

			st2financeproposalList2.setScQt(this.getManagedBean().getQt());

			/*
			 * ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			 * docDefTypeReqDTO
			 * .setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			 * docDefTypeReqDTO
			 * .setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
			 * ); ParameterResDTO docDefTypeResDTO = parameterSo
			 * .retrieveParameterByTypeAndCode(docDefTypeReqDTO);
			 */
			st2financeproposalList2.setDefinationType(new PmParameter());
			st2financeproposalList2.setAction(new PmParameter());

			setUserDetails(st2financeproposalList2);

			st2financeproposalList2
					.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			// st2financeproposalList2.setEvaluationStage("2");
			st2financeproposalList2
					.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			st2financeproposalList2.setIndex(tempNewChecklistIndex++);	// Bug #35219@Syazani 07/11/2023
			st2financeproposalList.add(st2financeproposalList2);

			this.getManagedBean().setScQtCheckSt2financeProposalList(
					st2financeproposalList);
			checklistSorting(getManagedBean()
					.getScQtCheckSt2financeProposalList());
			this.getManagedBean().setFinStandaraChecklist(false);
		} finally {
		}
	}

	/**
	 * Adds the technical compliance list.add standard checklist to technical
	 * checklist in specification committee.
	 */
	public void addTechnicalComplianceList() {

		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2techProposal();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoTechComplianceSelect();

			List<PmDocument> technicalDocumentList = this.getManagedBean()
					.getStageTwoTechComplianceList();
			if (CollectionUtils.isNotEmpty(technicalDocumentList)) {
				selectList = new ArrayList<PmDocument>();
				for (PmDocument document : technicalDocumentList) {
					if (document.isSelected()) {
						selectList.add(document);
					}
				}
			}
			this.getManagedBean().setTechStandaraChecklist(false);
			if (null == selectList || selectList.isEmpty()) {
				this.getManagedBean().setTechStandaraChecklist(true);
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {

					if (techStandard.getTitle().equals(document.getDocName())) {
						this.getManagedBean().setTechStandaraChecklist(true);
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
					// scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);
					setUserDetails(scqtlist);
					if (null == document.getDisplaySeq()) {
						scqtlist.setChecklistSeq(formatChecklistSequence(
								SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
								0));
					} else {

						scqtlist.setChecklistSeq(formatChecklistSequence(
								SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
								new Integer(document.getDisplaySeq())));
					}
					scqtlist.setScQt(this.getManagedBean().getQt());

					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());

					if (null != scqtlist.getDefinationType()
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						Long statusId = null;
						if (!scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_COMPLETED;
						} else {
							statusId = QTStatusConst.TS_PENDING_SUBMISSION;
						}
						scqtlist.setPmStatus(getPmStatusByStatusId(statusId));
					} else {
						scqtlist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					}
					scqtlist.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					compilance.add(scqtlist);
				}

			}

			getManagedBean().setScQtCheckSt2techProposal(compilance);
			checklistSorting(getManagedBean().getScQtCheckSt2techProposal());

		} finally {

		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("stageTwoTech.hide()");
	}

	/**
	 * Adds the technical check list.add empty row for Technical checklist
	 * datatable.
	 */
	public void addTechnicalCheckList() {
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
			Integer checkListSequence = checkTechnicalORFinancialAddChecklistSequence(st2technicalproposalList);
			log.debug("========== technical return checklist sequence===="
					+ checkListSequence);
			st2technicalproposalList1.setChecklistSeq(checkListSequence);

			st2technicalproposalList1.setScQt(this.getManagedBean().getQt());
			st2technicalproposalList1.setAction(new PmParameter());

			setUserDetails(st2technicalproposalList1);
			st2technicalproposalList1
					.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			st2technicalproposalList1
					.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			st2technicalproposalList1.setIndex(tempNewChecklistIndex++);	// Bug #35219@Syazani 07/11/2023 -- Start
			st2technicalproposalList.add(st2technicalproposalList1);

			this.getManagedBean().setScQtCheckSt2techProposal(
					st2technicalproposalList);
			checklistSorting(getManagedBean().getScQtCheckSt2techProposal());
			this.getManagedBean().setTechStandaraChecklist(false);
			
			//#28784 - Include WIP Demo
			if(this.getManagedBean().isNewVersion()){
				addDemoTechnicalCheckList();
			}
		} finally {
		}
	}

	/**
	 * Adds the financial compliance list.add standard checklist to financial
	 * checklist datatable.
	 */
	public void addFinancialComplianceList() {
		try {

			List<ScQtCheckList> compilance = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();

			List<PmDocument> selectList = this.getManagedBean()
					.getStageTwoFinComplianceSelect();
			this.getManagedBean().setFinStandaraChecklist(false);
			if (compilance == null) {
				compilance = new ArrayList<ScQtCheckList>();
			}
			List<PmDocument> financialDocumentList = this.getManagedBean()
					.getStageTwoFinComplianceList();
			if (CollectionUtils.isNotEmpty(financialDocumentList)) {
				selectList = new ArrayList<PmDocument>();
				for (PmDocument document : financialDocumentList) {
					if (document.isSelected()) {
						selectList.add(document);
					}
				}
			}
			if (null == selectList || selectList.isEmpty()) {
				this.getManagedBean().setFinStandaraChecklist(true);
				throw new EpException(MessageConst.ERROR_760147);
			}

			for (int i = 0; i < selectList.size(); i++) {
				PmDocument document = selectList.get(i);
				boolean isRequired = false;
				for (ScQtCheckList techStandard : compilance) {
					if (techStandard.getTitle().equals(document.getDocName())) {
						isRequired = true;
						this.getManagedBean().setFinStandaraChecklist(true);
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
					// scqtlist.setChecklistSeq(document.getDisplaySeq());
					scqtlist.setPmDocument(document);
					if (null == document.getDisplaySeq()) {
						scqtlist.setChecklistSeq(formatChecklistSequence(
								SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
								0));
					} else {
						scqtlist.setChecklistSeq(formatChecklistSequence(
								SourcingConstants.STANDARD_CHECKLIST_SEQUENCE,
								new Integer(document.getDisplaySeq())));
					}
					scqtlist.setScQt(this.getManagedBean().getQt());

					setUserDetails(scqtlist);
					// scqtlist.setQtScoreWeightage(new ScQtScoreWeightage());

					if (null != scqtlist.getDefinationType()
							&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
									.equalsIgnoreCase(scqtlist
											.getDefinationType()
											.getParameterCode())) {
						Long statusId = null;
						if (!scqtlist.isScoringReq()) {
							statusId = QTStatusConst.TS_COMPLETED;
						} else {
							statusId = QTStatusConst.TS_PENDING_SUBMISSION;
						}
						scqtlist.setPmStatus(getPmStatusByStatusId(statusId));
					} else {
						scqtlist.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					}

					scqtlist.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					compilance.add(scqtlist);
				}
			}

			getManagedBean().setScQtCheckSt2financeProposalList(compilance);
			checklistSorting(getManagedBean()
					.getScQtCheckSt2financeProposalList());

		} finally {
		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("stageTwoFinance.hide();");
	}

	/**
	 * Retrive technical standard check list.retrieve technical standard
	 * checklist from pmdocument.
	 */
	public void retriveTechnicalStandardCheckList() {
		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_TECHNICAL_CHECKLIST);

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);
			this.getManagedBean().setStageTwoTechComplianceSelect(null);
			this.getManagedBean().setStageTwoTechComplianceSelect(
					new ArrayList<PmDocument>());
			List<PmDocument> localcheckList = new ArrayList<PmDocument>();
			if (!isAtleastOneSeriveReq(this.getManagedBean()
					.getScQtCheckSt2techProposal())) {
				if (null != resDTO.getDocumentList()) {
					// Collections.sort(resDTO.getTechnicalCheckList());
					for (PmDocument checklist : resDTO.getDocumentList()) {

						if (!SourcingConstants.DOC_DEF_KEY_SERVICE_SCOPE
								.equalsIgnoreCase(checklist

								.getDocDefinitionKey())) {
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
			this.getManagedBean().setAutoUpdate(false);
			if (CollectionUtils.isNotEmpty(localcheckList)) {
				localcheckList = checkPmDocumentsExistInMainList(
						localcheckList, this.getManagedBean()
								.getScQtCheckSt2techProposal());
			}
			this.getManagedBean().setStageTwoTechComplianceList(localcheckList);
			localcheckList = null;
		} finally {
			documentSo = null;
		}

	}

	/**
	 * Delivery list.delivery address zones tab change.
	 * 
	 * @param zoneDetail
	 *            (TplSpecZoneDtl zoneDetail).
	 */
	public void deliveryList(TplSpecZoneDtl zoneDetail) {
		log.debug("deliveryList - START");
		IOrgProfileSO orgProfileSO = null;

		getManagedBean().setStateId(null);
		getManagedBean().setDistrictId(null);
		this.getManagedBean().setStateCode(null);
		this.getManagedBean().setDistrictCode(null);
		this.getManagedBean().setDivisionCode(null);
		this.getManagedBean().setDeliveryAddressSelect(null);
		this.getManagedBean().setPtjtList(null);
		getManagedBean().setAutoUpdate(Boolean.FALSE);

		IPagingDataModel<PmAddressType> searchDeliveryAddress = getManagedBean()
				.getSearchDeliveryAddressDataModel();
		if (searchDeliveryAddress == null) {
			getManagedBean().setSearchDeliveryAddressDataModel(
					new DeliveryPagingDataModel("deliveryAddress"));
		}
		getManagedBean().getSearchDeliveryAddressDataModel().resetData();
		if (null != this.getManagedBean().getQt().getPmOrgProfile()) {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO ptjreqDTO = new OrgProfileReqDTO();
			ptjreqDTO.setOrgProfileId(this.getManagedBean().getQt()
					.getPmOrgProfile().getOrgProfileId());
			boolean value = isQTStatusPublished(this.getManagedBean()
					.getCurrentStatusId());
			log.debug("====publish date==="
					+ this.getManagedBean().getQt().getPublishDate());
			log.debug("====created date==="
					+ this.getManagedBean().getQt().getCreatedDate());
			Date date = value ? this.getManagedBean().getQt().getPublishDate()
					: this.getManagedBean().getQt().getCreatedDate();
			log.debug("====passing date===" + date);
			ptjreqDTO.setCreateDate(date);
			PmOrgProfile orgProfileptj = orgProfileSO
					.retrieveOrgProfileByProfileId(ptjreqDTO).getPmOrgProfile();
			if (null != orgProfileptj) {
				this.getManagedBean().setPtjId(orgProfileptj.getOrgProfileId());
			}
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean().getQt()
					.getPmOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);
			if (resDTO.getPmOrgProfile() != null) {
				this.getManagedBean().setMinistryId(
						resDTO.getPmOrgProfile().getOrgProfileId());
			}
			if (null != this.getManagedBean().getMinistryId()) {
				retrievePTJGroupListByMinistry(this.getManagedBean()
						.getMinistryId());
			}

			OrgProfileResDTO pjtGroup = orgProfileSO
					.retrieveJabatanByOrgProfileId(reqDTO);
			if (pjtGroup.getPmOrgProfile() != null) {
				this.getManagedBean().setPtjGroupId(
						pjtGroup.getPmOrgProfile().getOrgProfileId());
				/*
				 * this.getManagedBean().setPtjtList(
				 * pjtGroup.getPmOrgProfile().getChildOrgProfiles());
				 */
			}

			if (null != this.getManagedBean().getPtjGroupId()) {
				retrivePTJByPTJGroup(this.getManagedBean().getPtjGroupId());

			}

		}
		retriveMinistryList();
		if (getManagedBean().getQt().isZonal()) {
			this.getManagedBean().setTplSpecZoneDtl(zoneDetail);
			List<String> codes = new ArrayList<String>();
			List<PmState> pmStates = new ArrayList<PmState>();
			if (null != zoneDetail.getTplSpecZoneLocs()) {
				for (TplSpecZoneLoc specZoneLoc : zoneDetail
						.getTplSpecZoneLocs()) {
					PmState pmState = specZoneLoc.getPmState();
					if (null != pmState) {
						if (!codes.contains(pmState.getStateCode())) {
							codes.add(pmState.getStateCode());
							pmStates.add(pmState);
						}
					}
					this.getManagedBean().setStateList(pmStates);
				}
			}
			codes = null;
			pmStates = null;
		} else {
			retriveStateList();
			log.debug("Non Zonal");
		}
		log.debug("deliveryList - END");
	}

	/**
	 * Zone delivery change.
	 * 
	 * @param event
	 *            the event
	 */
	public void zoneDeliveryChange(TabChangeEvent event) {

	}

	/**
	 * Delivery address reset.to clear all the delivery address data.
	 */
	public void deliveryAddressReset() {
		try {
			getManagedBean().setStateId(null);
			getManagedBean().setDistrictId(null);
			getManagedBean().setMinistryId(null);
			getManagedBean().setDepartmentId(null);
			getManagedBean().setPtjId(null);
			getManagedBean().setPtjGroupId(null);
			this.getManagedBean().setStateCode(null);
			this.getManagedBean().setDistrictCode(null);
			this.getManagedBean().setDivisionCode(null);
		} finally {

		}
	}

	/**
	 * Delivery accept.added delivery addresses to delivery address data table.
	 */
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
			if (CollectionUtils.isNotEmpty(deliveryList)) {
				log.info("=========TotalSize=====" + deliveryList.size());
				List<Long> localaddresstypeId = new ArrayList<Long>();
				for (int i = 0; i < deliveryList.size(); i++) {
					if (localaddresstypeId.contains(deliveryList.get(i)
							.getAddressTypeId())) {
						log.debug("=========deletedCount=====");
						deliveryList.remove(i);
						i--;
					} else {
						localaddresstypeId.add(deliveryList.get(i)
								.getAddressTypeId());
					}
				}
				log.info("=========deleteListSize=====" + deliveryList.size());
			}
			List<BriefingZoneBean> zoneBean = this.getManagedBean()
					.getDeliveryLocBean();
			if (this.getManagedBean().getQt().isZonal() == true
					&& this.getManagedBean().getQt().isPanel() == false) {
				for (BriefingZoneBean deliverybean : zoneBean) {

					if (deliverybean
							.getTplSpecZoneDtl()
							.getZoneName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getZoneName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						List<Long> addressTypes = new ArrayList<Long>();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						} else {

							for (ScQtDeliveryLoc loc : delLoc) {
								addressTypes.add(loc.getPmAddressType()
										.getAddressTypeId());
							}
						}
						for (PmAddressType addressType : deliveryList) {
							if (addressTypes.contains(addressType
									.getAddressTypeId())) {
								throw new EpException(MessageConst.ERROR_760410);
							} else {
								addressTypes
										.add(addressType.getAddressTypeId());
							}
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							ScAddress deliveryAddress = populateDeliveryAddress(addressType);
							loc.setScDeliveryAddress(deliveryAddress);
							loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));
							loc.setPmAddressType(addressType);
							loc.setTplSpecZoneDtl(this.getManagedBean()
									.getTplSpecZoneDtl());
							loc.setScQt(this.getManagedBean().getQt());

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

			else if (this.getManagedBean().getQt().isZonal() == true
					&& this.getManagedBean().getQt().isPanel() == true) {
				for (BriefingZoneBean deliverybean : zoneBean) {

					if (deliverybean
							.getTplSpecZoneDtl()
							.getPanelZone()
							.getName()
							.equals(this.getManagedBean().getTplSpecZoneDtl()
									.getPanelZone().getName())) {
						List<ScQtDeliveryLoc> delLoc = deliverybean
								.getDeliveryLocList();
						List<Long> addressTypes = new ArrayList<Long>();
						if (delLoc == null) {
							delLoc = new ArrayList<ScQtDeliveryLoc>();
						} else {

							for (ScQtDeliveryLoc loc : delLoc) {
								addressTypes.add(loc.getPmAddressType()
										.getAddressTypeId());
							}
						}
						for (PmAddressType addressType : deliveryList) {
							if (addressTypes.contains(addressType
									.getAddressTypeId())) {
								throw new EpException(MessageConst.ERROR_760410);
							} else {
								addressTypes
										.add(addressType.getAddressTypeId());
							}
							ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
							ScAddress deliveryAddress = populateDeliveryAddress(addressType);
							loc.setScDeliveryAddress(deliveryAddress);
							loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));
							loc.setPmAddressType(addressType);
							loc.setTplSpecZoneDtl(this.getManagedBean()
									.getTplSpecZoneDtl());
							loc.setScQt(this.getManagedBean().getQt());

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
			} else if ((this.getManagedBean().getQt().isZonal() == false && this
					.getManagedBean().getQt().isPanel() == true)
					|| (this.getManagedBean().getQt().isZonal() == false && this
							.getManagedBean().getQt().isPanel() == false)) {
				List<ScQtDeliveryLoc> nonZone = this.getManagedBean()
						.getDeliveryNonZoneList();
				List<Long> addressTypes = new ArrayList<Long>();
				if (nonZone == null) {
					nonZone = new ArrayList<ScQtDeliveryLoc>();
				}

				else {

					for (ScQtDeliveryLoc loc : nonZone) {
						addressTypes.add(loc.getPmAddressType()
								.getAddressTypeId());
					}
				}
				for (PmAddressType addressType : deliveryList) {
					if (addressTypes.contains(addressType.getAddressTypeId())) {
						throw new EpException(MessageConst.ERROR_760410);
					} else {
						addressTypes.add(addressType.getAddressTypeId());
					}
					ScQtDeliveryLoc loc = new ScQtDeliveryLoc();
					ScAddress deliveryAddress = populateDeliveryAddress(addressType);
					loc.setScDeliveryAddress(deliveryAddress);
					loc.setDeliveryAddress(getDeliveryAddressString(deliveryAddress));

					loc.setPmAddressType(addressType);
					loc.setTplSpecZoneDtl(null);
					loc.setScQt(this.getManagedBean().getQt());

					setUserDetails(loc);
					nonZone.add(loc);
					this.getManagedBean().setDeliveryNonZoneList(nonZone);
				}

			}
			deliveryList = null;
			addresstypeId = null;
		} finally {
			addressSo = null;
		}

		RequestContext.getCurrentInstance().execute("deliveryAddress.hide();");
	}

	private ScAddress populateDeliveryAddress(PmAddressType addressType) {
		PmAddress pmAddress = addressType.getPmAddress();
		ScAddress scAddress = new ScAddress();
		scAddress.setAddressName(pmAddress.getAddressName());
		scAddress.setAddress1(pmAddress.getAddress1());
		scAddress.setAddress2(pmAddress.getAddress2());
		scAddress.setAddress3(pmAddress.getAddress3());
		// scAddress.setAddress4(pmAddress.getAddress4());
		scAddress.setPostcode(pmAddress.getPostcode());
		scAddress.setPmCountry(pmAddress.getPmCountry());
		scAddress.setPmState(pmAddress.getPmState());
		scAddress.setPmDivision(pmAddress.getPmDivision());
		scAddress.setPmDistrict(pmAddress.getPmDistrict());
		scAddress.setPmCity(pmAddress.getPmCity());
		scAddress.setMapLatitude(pmAddress.getMapLatitude());
		scAddress.setMapLongitude(pmAddress.getMapLongitude());

		scAddress.setContactFaxArea(pmAddress.getFaxArea());
		scAddress.setContactFaxCountry(pmAddress.getFaxCountry());
		scAddress.setContactPhoneArea(pmAddress.getPhoneArea());
		scAddress.setContactPhoneCountry(pmAddress.getPhoneCountry());
		scAddress.setContactFaxNo(pmAddress.getFaxNo());
		scAddress.setContactPhoneNo(pmAddress.getPhoneNo());

		return scAddress;
	}

	private String getDeliveryAddressString(ScAddress scAddress) {
		String COMMA = ",";
		StringBuilder address = new StringBuilder();

		String NULL_VALUE = "null";
		if (StringUtils.isNotBlank(scAddress.getAddressName())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddressName())) {
			address.append(scAddress.getAddressName());
		} else {
			address.append("-");
		}

		if (StringUtils.isNotBlank(scAddress.getAddress1())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress1())) {
			address.append(COMMA).append(scAddress.getAddress1());
		}

		if (StringUtils.isNotBlank(scAddress.getAddress2())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress2())) {
			address.append(COMMA).append(scAddress.getAddress2());
		}
		if (StringUtils.isNotBlank(scAddress.getAddress3())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getAddress3())) {
			address.append(COMMA).append(scAddress.getAddress3());
		}
		if (StringUtils.isNotBlank(scAddress.getPostcode())
				&& !NULL_VALUE.equalsIgnoreCase(scAddress.getPostcode())) {
			address.append(COMMA).append(scAddress.getPostcode());
		}
		if (scAddress.getPmCity() != null
				&& scAddress.getPmCity().getCityName() != null
				&& scAddress.getPmCity().getCityName().length() > 0) {
			address.append(COMMA).append(scAddress.getPmCity().getCityName());
		}
		if (scAddress.getPmState() != null
				&& scAddress.getPmState().getStateName() != null
				&& scAddress.getPmState().getStateName().length() > 0) {
			address.append(COMMA).append(scAddress.getPmState().getStateName());
		}
		/*
		 * if (StringUtils.isNotBlank( scAddress.getAddress4())) {
		 * address.append(COMMA).append(scAddress.getAddress4()); }
		 */

		return address.toString();
	}

	/**
	 * Delete delivery zone.delete delivery addresses based on zonal.
	 * 
	 * @param deliveryZoneName
	 *            the delivery zone name
	 */
	public void deleteDeliveryZone() {
		log.debug("deleteDeliveryZone - START");
		if (getManagedBean().getQt().isZonal()) {
			String deliveryZoneName = "";
			if (null != this.getManagedBean().getTplSpecZoneDtl()) {
				deliveryZoneName = this.getManagedBean().getTplSpecZoneDtl()
						.getZoneName();
			}
			List<BriefingZoneBean> deliveryBean = this.getManagedBean()
					.getDeliveryLocBean();
			List<ScQtDeliveryLoc> locationRemoveList = this.getManagedBean()
					.getDeliveryLocRemoveList();
			if (null == locationRemoveList) {
				locationRemoveList = new ArrayList<ScQtDeliveryLoc>();
			}
			if (!this.getManagedBean().getQt().isPanel()) {
				List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
				for (BriefingZoneBean zonebean : deliveryBean) {
					if (zonebean.getTplSpecZoneDtl().getZoneName()
							.equalsIgnoreCase(deliveryZoneName)) {
						List<ScQtDeliveryLoc> deliveryList = zonebean
								.getDeliveryLocList();
						for (ScQtDeliveryLoc entity : deliveryList) {
							if (entity.isSelected()) {
								entity.setDeleted(true);
								selectRemoveList.add(entity);
							}
						}

						if (null == selectRemoveList
								|| selectRemoveList.isEmpty()) {
							throw new EpException(MessageConst.ERROR_760146);
						}
						locationRemoveList.addAll(selectRemoveList);
						for (ScQtDeliveryLoc delLoc : selectRemoveList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);
						}

					}
					this.getManagedBean().setDeliveryLocRemoveList(
							locationRemoveList);
				}
				selectRemoveList = null;
			} else {
				List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
				for (BriefingZoneBean zonebean : deliveryBean) {
					if (zonebean.getTplSpecZoneDtl().getPanelZone().getName()
							.equalsIgnoreCase(deliveryZoneName)) {
						List<ScQtDeliveryLoc> deliveryList = zonebean
								.getDeliveryLocList();
						for (ScQtDeliveryLoc entity : deliveryList) {
							if (entity.isSelected()) {
								entity.setDeleted(true);
								selectRemoveList.add(entity);
							}
						}

						if (null == selectRemoveList
								|| selectRemoveList.isEmpty()) {
							throw new EpException(MessageConst.ERROR_760146);
						}
						locationRemoveList.addAll(selectRemoveList);
						for (ScQtDeliveryLoc delLoc : selectRemoveList) {
							delLoc.setDeleted(true);
							deliveryList.remove(delLoc);
						}
					}
					this.getManagedBean().setDeliveryLocRemoveList(
							locationRemoveList);
				}
				selectRemoveList = null;
			}
		} else {
			List<ScQtDeliveryLoc> selectRemoveList = new ArrayList<ScQtDeliveryLoc>();
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
			for (ScQtDeliveryLoc entity : nonzone) {
				if (entity.isSelected()) {
					entity.setDeleted(true);
					selectRemoveList.add(entity);
				}
			}

			if (null == selectRemoveList || selectRemoveList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_760146);
			}
			locationRemoveList.addAll(selectRemoveList);
			for (ScQtDeliveryLoc delLoc : selectRemoveList) {
				delLoc.setDeleted(true);
				nonzone.remove(delLoc);
			}
			selectRemoveList = null;
			this.getManagedBean().setDeliveryLocRemoveList(locationRemoveList);
		}

		log.debug("deleteDeliveryZone - END");
	}

	/**
	 * Delete delivery dtls.delete delivery addresses non zonal.
	 */
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

			for (ScQtDeliveryLoc delLoc : selectdeliveryList) {
				delLoc.setDeleted(true);
				locationRemoveList.add(delLoc);
				nonzone.remove(delLoc);
			}

			this.getManagedBean().setDeliveryLocRemoveList(locationRemoveList);
		} finally {

		}
	}

	/**
	 * Retrive financial standard check list.retrieve Financial Standard
	 * checklist from pmdocument.
	 */
	public void retriveFinancialStandardCheckList() {

		IDocumentSO documentSo;
		DocumentResDTO resDTO;
		try {

			documentSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			procurementCategoryCode(this.getManagedBean().getQt());
			reqDTO.setProcurementCatCode(this.getManagedBean()
					.getProcuremntCategory());
			// #13955 reqDTO.setIsMandatory(false);
			reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_FINANCIAL_CHECKLIST);

			resDTO = documentSo.retrieveDocumentCheckList(reqDTO);
			List<PmDocument> localDocList = new ArrayList<PmDocument>();
			this.getManagedBean().setStageTwoFinComplianceSelect(null);
			this.getManagedBean().setStageTwoFinComplianceSelect(
					new ArrayList<PmDocument>());
			this.getManagedBean().setAutoUpdate(false);
			if (CollectionUtils.isNotEmpty(resDTO.getDocumentList())) {
				localDocList = checkPmDocumentsExistInMainList(
						resDTO.getDocumentList(), this.getManagedBean()
								.getScQtCheckSt2financeProposalList());
			}
			this.getManagedBean().setStageTwoFinComplianceList(localDocList);
			localDocList = null;
		} finally {
			documentSo = null;
		}

	}

	/**
	 * Circular date comparision.circular list date comparision
	 * 
	 * @param enddate
	 *            (Date CircularExpiaryDate)
	 * @return (boolean true/false).
	 */
	public boolean circularDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(enddate, d1);
			int day = (int) days;
			if (day > 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	/**
	 * supplierStatusDateComparision.panel suppliers Date comparision.
	 * 
	 * @param enddate
	 *            (Date suppliersExpiaryDate)
	 * 
	 * @return (boolean true/false).
	 */
	public boolean supplierStatusDateComparision(Date enddate) {
		boolean isDate = false;
		Date d1 = new Date();

		if (null != enddate) {
			long days = DateUtils.getDaysDifference(d1, enddate);
			int day = (int) days;
			if (day > 0) {
				isDate = true;
			}
		}

		return isDate;
	}

	/**
	 * Creates the check list.create checklist score.
	 * 
	 * @return ScQtCheckListScore.
	 */
	private ScQtCheckListScore createCheckList() {
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		ScQtCheckListScore score = new ScQtCheckListScore();
		score.setCheckList(checkList);
		setUserDetails(score);
		return score;
	}

	/**
	 * Adds the scoring.add emptty row for scoring datatable.
	 * 
	 * @param criteriaType
	 *            (String criteriaType).
	 */
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

				if (prevRangeTo != null) {
					if ("50".equalsIgnoreCase(criteriaType)
							|| "60".equalsIgnoreCase(criteriaType)) {
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

	/**
	 * Adds the scoringyears.add empty row for scoring datatable.
	 * 
	 * @param CriteriaType
	 *            (String CriteriaType).
	 */
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
				if (prevRangeTo != null) {
					if ("61".equalsIgnoreCase(CriteriaType)
							|| "11".equalsIgnoreCase(CriteriaType)
							|| "16".equalsIgnoreCase(CriteriaType)) {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(0.01)));
					} else {
						score.setRangeFrom(prevRangeTo.add(BigDecimal
								.valueOf(1)));
					}
				}
			}
			checkListScore.add(score);
			this.getManagedBean().setScoringForYears(checkListScore);

		} finally {

		}
	}

	/**
	 * Adds the schedule details.add empty rows for frequency schedule
	 * datatable.
	 */
	public void addScheduleDetails() {
		IParameterSO parameterSo = null;
		try {
			
			if(this.getManagedBean()
					.getScheduleList()==null){
				this.getManagedBean().setScheduleList(new ArrayList<ScQtSchedule>());
			}
			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();
					parameterSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
					docDefTypeReqDTO
							.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
					// Reopen #25726 note 110 - issue 4
					if(null != this.getManagedBean().getFulfilmentType()){
						if(this.getManagedBean().getFulfilmentType().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
							docDefTypeReqDTO
							.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
						}else{
							docDefTypeReqDTO
							.setParameterCode(ParameterConst.SCHEDULE_TYPE_NON_PERIODIC);
						}
					}
					// Reopen #25726 end
					ParameterResDTO docDefTypeResDTO = parameterSo
							.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
					
					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setScQt(this.getManagedBean().getQt());
					schedule.setScheduleType(docDefTypeResDTO.getPmParameter());
				scheduleList.add(schedule);

			this.getManagedBean().setScheduleList(scheduleList);
		}

		finally {
			parameterSo = null;
		}
	}

	/**
	 * Calculate progressive start month.change start month in progessive
	 * payment.
	 * 
	 * @param progressivepayment
	 *            (ScQtProgressivePayment progressivepayment).
	 */
	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			if (null == progressivepayment.getEndMonth()) {
				progressivepayment.setEndMonth(progressivepayment
						.getStartMonth());
			}
			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

	}

	/**
	 * Calculate progressivepayment.change end month dropdown in progressive
	 * payment.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateProgressivepayment(
			ScQtProgressivePayment progressivepayment) {

		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment) {
			int index = payment.indexOf(progressivepayment);

			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

	}

	/**
	 * Calculate schedule percentage.calculate months difference in Frequency
	 * schedule.
	 * 
	 * @param schedule
	 *            (ScQtSchedule schedule).
	 */
	public void calculateSchedulePercentage(ScQtSchedule schedule,
			Boolean startMonthAndYearFlag) throws ParseException {
		/*
		 * String endMonth = (String) event.getComponent().getAttributes()
		 * .get("value");
		 */
		// this.getManagedBean().setScQt(this.getManagedBean().getQt());
		List<ScQtSchedule> freqSchedule = this.getManagedBean()
				.getScheduleList();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, months = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
		endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
				.getEndMonth()) : null;
		startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
				.getStartYear()) : null;
		endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
				.getEndYear()) : null;

		if (null != startMonthAndYearFlag && startMonthAndYearFlag
				&& startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if (endYear != null) {
				schedule.setEndYear(endYear.toString());
			}
		}

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null) {
			// Date currentDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			// String localDate = sdf.format(currentDate);
			// String day = localDate.substring(0, 2);
			String localstartMonth = retriveMonthDtls(new Integer(startMonth));
			String localemdMonth = retriveMonthDtls(new Integer(endMonth));
			String fromDate = 01 + "/" + localstartMonth + "/" + startYear;
			String toDate = 01 + "/" + localemdMonth + "/" + endYear;
			Date startDate = sdf.parse(fromDate);
			Date endDate = sdf.parse(toDate);

			months = DateUtils.getMonths(startDate, endDate);
			schedule.setTotalMonths(new BigDecimal(months));
		} else {
			schedule.setTotalMonths(BigDecimal.ZERO);
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);

	}

	/**
	 * Retrive month dtls.
	 * 
	 * @param Start
	 *            the start
	 * @return the string
	 */
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
		} else if (Start == 12) {
			month = "01";
		} else if (Start == 13) {
			month = "02";
		}
		return month;
	}

	/**
	 * Adds the progressive details.add empty rows for progressive payment
	 * datatable.
	 */
	public void addProgressiveDetails() {

		try {
			if (this.getManagedBean().getProgressivePaymentList() == null) {
				this.getManagedBean().setProgressivePaymentList(
						new ArrayList<ScQtProgressivePayment>());
			}
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();

			ScQtProgressivePayment payment = new ScQtProgressivePayment();

			setUserDetails(payment);
			payment.setScQt(this.getManagedBean().getQt());
			payments.add(payment);
			this.getManagedBean()
					.getQt()
					.setScheduleType(
							ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);

			this.getManagedBean().setProgressivePaymentList(payments);

		} finally {

		}
	}

	/**
	 * Calculate total package.calculate package Technical and Financial scores
	 * in Recommendation Menu.
	 * 
	 * @param technical
	 *            (ScQtScoreSetting package).
	 */
	public void calculateTotalPackage(ScQtScoreSetting technical,
			boolean isValidationRequired) {
		try {
			if (null == technical.getOverallTechnical()) {
				technical.setOverallTechnical(new BigDecimal(0));
			}
			if (null == technical.getOverallFin()) {
				technical.setOverallFin(new BigDecimal(0));
			}
			BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_WEIGHTAGE);
			if (null != passingScore
					&& technical.getOverallTechnical().intValue() < passingScore
							.intValue() && !this.getManagedBean().isReadOnly()
					&& isValidationRequired) {
				throw new EpException(MessageConst.ERROR_760365,
						new String[] { String.valueOf(passingScore) });
			}
			int financialScore = 100 - technical.getOverallTechnical()
					.intValue();
			technical.setOverallFin(new BigDecimal(financialScore));
			this.getManagedBean().setRecommendationPackageScore(technical);
			int local = technical.getOverallTechnical().intValue()
					+ technical.getOverallFin().intValue();

			this.getManagedBean().setRecommendationPackageTotal(
					new BigDecimal(local));
		} finally {

		}
	}

	/**
	 * Calculate recommendtechnical score.calculate Item Technical Scores in
	 * Recommendation Menu.
	 * 
	 * @param technical
	 *            (ScQtScoreSetting technical).
	 */
	public void calculateRecommendtechnicalScore(ScQtScoreSetting technical) {
		try {
			int value1 = technical.getOverallTechnical() != null ? technical
					.getOverallTechnical().intValue() : 0;
			int value2 = technical.getOverallOtherTechnical() != null ? technical
					.getOverallOtherTechnical().intValue() : 0;
			int local = value1 + value2;
			this.getManagedBean().setRecommendTechnicalScore(
					new BigDecimal(local));
			int financialScore = 0;
			if (null != getManagedBean().getRecommendfinancialScore()) {
				financialScore = getManagedBean().getRecommendfinancialScore()
						.intValue();
			}
			this.getManagedBean().setTotalScore(
					new BigDecimal(local + financialScore));
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore().intValue() > 100) {
//#21695 Farid 30/7/2020 Start
				throw new EpException(MessageConst.ERROR_7602571);

			}

		} finally {

		}
	}

	/**
	 * Calculate recommend financial score.calculate Item Financial Scores in
	 * Recommendation Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financial).
	 */
	public void calculateRecommendFinancialScore(ScQtScoreSetting financial) {
		try {
			int value1 = financial.getOverallFin() != null ? financial
					.getOverallFin().intValue() : 0;
			int value2 = financial.getOverallOtherFin() != null ? financial
					.getOverallOtherFin().intValue() : 0;
			int local = value1 + value2;
			this.getManagedBean().setRecommendfinancialScore(
					new BigDecimal(local));

			int technicalScore = 0;
			if (null != getManagedBean().getRecommendTechnicalScore()) {
				technicalScore = getManagedBean().getRecommendTechnicalScore()
						.intValue();
			}
			this.getManagedBean().setTotalScore(
					new BigDecimal(local + technicalScore));
			if ((null != this.getManagedBean().getTotalScore())
					&& this.getManagedBean().getTotalScore().intValue() > 100) {
				throw new EpException(MessageConst.ERROR_760257);

			}

		} finally {

		}
	}

	/**
	 * Calculate Financial Item score.calculate Item Financial Scores in
	 * FinancialScoring Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financialItem).
	 */
	public void calculateFinancialItemScore(ScQtScoreSetting financialItem) {
// #21695 #28949 START FARID - 15/10/2020	
		try {
			if (null != financialItem) {
				String checkDecimal1 = String.valueOf(financialItem.getOverallFin());
				String checkDecimal2 = String.valueOf(financialItem.getOverallOtherFin());
				String pattern1 = "^[0-9\\s]+$";
				if(this.getManagedBean().getFinacialItemScoreSetting().getOverallFin() != null 
						&& this.getManagedBean().getFinacialItemScoreSetting().getOverallOtherFin()  != null){
					if (!checkDecimal1.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}else if (!checkDecimal2.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}
				}
// #21695 #28949 END FARID - 15/10/2020					
				int value1 = financialItem.getOverallFin() != null ? financialItem
						.getOverallFin().intValue() : 0;
				int value2 = financialItem.getOverallOtherFin() != null ? financialItem
						.getOverallOtherFin().intValue() : 0;
				int local = value1 + value2;
				this.getManagedBean().setFinancialItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getFinancialItemScore())
						&& this.getManagedBean().getFinancialItemScore()
								.intValue() > 100) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}

		} finally {

		}
	}

	/**
	 * Calculate Financial Item score.calculate Item Financial Scores in
	 * FinancialScoring Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting financialItem).
	 */
	private void financialItemScoreCalculate(ScQtScoreSetting financialItem) {
		try {
			if (null != financialItem) {
				if (null == financialItem.getOverallFin()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
				if (financialItem.getOverallFin().intValue() <= 0) {
					throw new EpException(MessageConst.ERROR_760269);
				}
				if (null == financialItem.getOverallOtherFin()) {
					throw new EpException(MessageConst.ERROR_760256);
				}
				int value1 = financialItem.getOverallFin() != null ? financialItem
						.getOverallFin().intValue() : 0;
				int value2 = financialItem.getOverallOtherFin() != null ? financialItem
						.getOverallOtherFin().intValue() : 0;
				int local = value1 + value2;
				this.getManagedBean().setFinancialItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getFinancialItemScore())
						&& (this.getManagedBean().getFinancialItemScore()
								.intValue() < 100 || this.getManagedBean()
								.getFinancialItemScore().intValue() > 100)) {
					throw new EpException(MessageConst.ERROR_760257);

				}
			}

		} finally {

		}
	}
	
	private boolean isIndirectWithInstantiated(Object o){
		
		if(o instanceof IndirectList) {
		    boolean inst = ((IndirectList)o).isInstantiated();
		    return inst;
		}
		
		return true;
	}

	/**
	 * Compare checklist value current year.check checklist year greater than
	 * current year.
	 * 
	 * @param checkListValue
	 *            (ScQtCheckListValue checkListValue)
	 */
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

	/**
	 * Save check list scores.save checklist scores.
	 */
	public void saveCheckListScores() {
		IQTSpecSO specSo;
		try {

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
			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					} else {
						checklistRemoveList.get(i).setDeleted(true);
					}
				}
				log.debug("backing bean saveCheckListScores toBeremoveCheckLists  size=="
						+ checklistRemoveList.size());
			}
			if (null != checkList && null != checkList.getScQtCheckListValue()) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				if (null != checkList.getScQtCheckListValue().getYear()) {
					String checkListValueYear = checkList
							.getScQtCheckListValue().getYear().toString();
					if (checkListValueYear.length() < 4) {
						throw new EpException(MessageConst.ERROR_760246);
					}
					if (checkList.getScQtCheckListValue().getYear().intValue() > year) {
						throw new EpException(MessageConst.ERROR_760169);
					}
				}
			}
			if (checkList != null && checkList.isScoringReq()) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				/* FIX VALIDATION PROBLEM 25138*/
				if (null == contractScoreType
						|| "".equalsIgnoreCase(contractScoreType) || "0".equalsIgnoreCase(contractScoreType)) {
					throw new EpException(MessageConst.ERROR_760190);
				}

				if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
					ScQtCheckListScore noContract = this.getManagedBean()
							.getScQtCheckListScore();
					noContract.setScoreType(new BigDecimal(this
							.getManagedBean().getChecklistScoreType()));
					// noContract.setCriteriaType(new Integer(this
					// .getManagedBean().getCriteriaType()));
					noContract.setCheckList(checkList);
					setUserDetails(noContract);
					noOfContractScore.add(noContract);

				} else if (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) {
					List<ScQtCheckListScore> checkListScore = this
							.getManagedBean().getCheckListScore();

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
								|| Score.getScore().intValue() < 0) {
							throw new EpException(MessageConst.ERROR_760139);

						}
						if (null != checkList.getPmDocument()
								&& QTConstants.FINANCIAL_CREDIT_FACILITY_CODE
										.equalsIgnoreCase(checkList
												.getPmDocument()
												.getDocDefinitionKey())) {
							if (null != Score.getRangeTo()
									&& Score.getRangeTo().intValue() > 100) {
								throw new EpException(MessageConst.ERROR_760170);
							}
						}

					}
					isOverlappingScore(checkListScore);
					for (int i = 0; i < checkListScore.size() - 1; i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						localScore.add(Score);
					}

					// Collections.sort(localScore);
					if (null != checkListScore) {
						noOfContractScore.addAll(checkListScore);
					}
				}

				if ((null != checkList && null != checkList.getPmDocument())
						&& (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
								.equalsIgnoreCase(checkList.getPmDocument()
										.getDocDefinitionKey()))) {

					if (QTConstants.USER_SCORE
							.equalsIgnoreCase(totalNoOfYearsType)) {
						ScQtCheckListScore scoreOfYears = this.getManagedBean()
								.getScQtContractScore();
						scoreOfYears.setScoreType(new BigDecimal(this
								.getManagedBean().getCompanyScoreType()));
						// scoreOfYears.setCriteriaType(new Integer(this
						// .getManagedBean().getOtherCriteriaType()));
						scoreOfYears.setCheckList(checkList);
						setUserDetails(scoreOfYears);
						noOfYearsScore.add(scoreOfYears);
					} else if (QTConstants.AUTO_SCORE
							.equalsIgnoreCase(totalNoOfYearsType)) {
						List<ScQtCheckListScore> scoreYears = this
								.getManagedBean().getScoringForYears();

						if (null == scoreYears) {
							scoreYears = new ArrayList<ScQtCheckListScore>();
						}
						if (scoreYears.size() < 2) {
							throw new EpException(MessageConst.ERROR_760140);
						}

						for (int i = 0; i < scoreYears.size(); i++) {
							ScQtCheckListScore Score = scoreYears.get(i);
							if (null == Score.getScore()
									|| Score.getScore().intValue() < 0) {
								throw new EpException(MessageConst.ERROR_760139);

							}
							/*
							 * if (null != Score.getRangeTo() &&
							 * Score.getRangeTo().intValue() > 100) { throw new
							 * EpException(MessageConst.ERROR_760170); }
							 */
						}
						isOverlappingScore(scoreYears);
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
					&& (QTConstants.FINANCIAL_ASSET_LIABILITY_CODE
							.equalsIgnoreCase(checkList.getPmDocument()
									.getDocDefinitionKey()))) {
				if (((QTConstants.AUTO_SCORE
						.equalsIgnoreCase(totalNoOfYearsType) && QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) || (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType) && (null == totalNoOfYearsType || ""
						.equals(totalNoOfYearsType))))) {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					// checkList.setPmEvalStatus(evalStatus);

				} else {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					// checkList.setPmEvalStatus(evalStatus);
				}

			} else {
				if (QTConstants.AUTO_SCORE.equalsIgnoreCase(contractScoreType)) {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					// checkList.setPmEvalStatus(evalStatus);
				} else {
					reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					// checkList.setPmEvalStatus(evalStatus);
				}

			}
			reqDTO.setSpecificationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			if (checkList != null){
			  reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			  reqDTO.setScoringReq(checkList.isScoringReq());
			}
			reqDTO.setTobeRemoveList(checklistRemoveList);
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
						updatedCheckList,
						ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			} else {
				this.getManagedBean().getScQtCheckSt2financeProposalList()
						.remove(this.getManagedBean().getIndex());
				this.getManagedBean()
						.getScQtCheckSt2financeProposalList()
						.add(this.getManagedBean().getIndex(), updatedCheckList);
				financialChecklistEditAction(this.getManagedBean().getIndex(),
						updatedCheckList,
						ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			}
			reqDTO.setTobeRemoveScores(null);
			reqDTO.setTobeRemoveList(null);
			getManagedBean().setMarkDeletedTechChecklist(null);
			getManagedBean().setMarkDeletedFinChecklist(null);
			noOfContractScore = null;
			noOfYearsScore = null;
			saveSpecMaintenanceWorkFlowStatus();

			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.scores"), "", Boolean.FALSE);
		} finally {
			specSo = null;
		}

	}

	/**
	 * Save bank check list scores.save bank statemnt checklist scores.
	 */
	public void saveBankCheckListScores() {
		IQTSpecSO specSo;
		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setTobeRemoveScores(null);
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
			String contractScoreType = this.getManagedBean()
					.getChecklistScoreType();
			List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					} else {
						checklistRemoveList.get(i).setDeleted(true);
					}
				}
				log.debug("backing bean saveBankCheckListScores toBeremoveCheckLists  size=="
						+ checklistRemoveList.size());
			}

			List<ScQtCheckListScore> noOfContractScore = new ArrayList<ScQtCheckListScore>();
			if (checkList.isScoringReq()) {
				//bug #30438 - start
				if (null == contractScoreType
						|| "".equalsIgnoreCase(contractScoreType) || "0".equalsIgnoreCase(contractScoreType)) {
					throw new EpException(MessageConst.ERROR_760190);
				}
				//bug #30438 - end
				if (QTConstants.USER_SCORE.equalsIgnoreCase(contractScoreType)) {
					ScQtCheckListScore noContract = this.getManagedBean()
							.getScQtCheckListScore();
					noContract.setScoreType(new BigDecimal(this
							.getManagedBean().getChecklistScoreType()));
					// noContract.setCriteriaType(new Integer(this
					// .getManagedBean().getCriteriaType()));
					noContract.setCheckList(checkList);
					setUserDetails(noContract);
					noOfContractScore.add(noContract);
				} else if (QTConstants.AUTO_SCORE
						.equalsIgnoreCase(contractScoreType)) {
					List<ScQtCheckListScore> checkListScore = this
							.getManagedBean().getCheckListScore();

					if (null == checkListScore) {
						checkListScore = new ArrayList<ScQtCheckListScore>();
					}
					if (checkListScore.size() < 2) {
						throw new EpException(MessageConst.ERROR_760140);
					}

					for (int i = 0; i < checkListScore.size(); i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						if (null == Score.getScore()
								|| Score.getScore().intValue() < 0) {
							throw new EpException(MessageConst.ERROR_760139);

						}
						/*
						 * if (null != Score.getRangeTo() &&
						 * Score.getRangeTo().intValue() > 100) { throw new
						 * EpException(MessageConst.ERROR_760170); }
						 */

					}
					isOverlappingScore(checkListScore);
					List<ScQtCheckListScore> localScore = new ArrayList<ScQtCheckListScore>();
					for (int i = 0; i < checkListScore.size() - 1; i++) {
						ScQtCheckListScore Score = checkListScore.get(i);
						localScore.add(Score);
					}

					if (null != checkListScore) {
						noOfContractScore.addAll(checkListScore);
					}
				}
			}
			String Startmonth = "";
			String endmonth = "";
			Startmonth = retriveMonthDtls(new Integer(this.getManagedBean()
					.getFromMonth()));

			endmonth = retriveMonthDtls(new Integer(this.getManagedBean()
					.getToMonth()));

			//Bug #32862 - start
			//Date d1 = new Date();
			//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			//Bug #32862 - end
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			//Bug #32862 - start
			//String localDate = format.format(d1);
			String date = "01";  
			//Bug #32862 - end
			String fromdate = date + "/" + Startmonth + "/"
					+ this.getManagedBean().getStartYear();
			String toDate = date + "/" + endmonth + "/"
					+ this.getManagedBean().getEndYear();
			try {
				checkList.getScQtCheckListValue().setFromDate(
						df.parse(fromdate));
				// checkList.setScQt(this.getManagedBean().getQt());
				checkList.getScQtCheckListValue().setToDate(df.parse(toDate));
			} catch (ParseException e) {
				log.debug(e.getMessage());
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
				reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				// checkList.setPmEvalStatus(evalStatus);
			} else {
				reqDTO.setEvaluationStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				// checkList.setPmEvalStatus(evalStatus);
			}

			List<ScQtCheckListScore> noOfYearsScore = new ArrayList<ScQtCheckListScore>();
			reqDTO.setSpecificationStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			reqDTO.setTobeRemoveScores(removedList);
			reqDTO.setScQtCheckList(checkList);
			reqDTO.setOtherCheckListScores(noOfYearsScore);
			reqDTO.setCheckListValue(checkList.getScQtCheckListValue());
			reqDTO.setScoringReq(checkList.isScoringReq());
			reqDTO.setTobeRemoveList(checklistRemoveList);
			SpecificationResDTO resDTO = specSo.saveCheckListScore(reqDTO);
			ScQtCheckList updatedCheckList = resDTO.getScQtCheckList();

			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), updatedCheckList);
			financialChecklistEditAction(this.getManagedBean().getIndex(),
					updatedCheckList,
					ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			reqDTO.setTobeRemoveScores(null);
			reqDTO.setTobeRemoveList(null);
			getManagedBean().setMarkDeletedTechChecklist(null);
			getManagedBean().setMarkDeletedFinChecklist(null);
			noOfContractScore = null;
			noOfYearsScore = null;
			saveSpecMaintenanceWorkFlowStatus();

			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.scores"), "", Boolean.FALSE);
		} finally {
			specSo = null;
		}

	}

	/**
	 * Gets the month drop down values.display months in dropdown.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getMonthDropDownValues() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(0, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.january",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(1, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.february",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(2, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.march",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(3, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.april",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(4, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.may",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(5, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.june",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(6, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.july",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(7, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.august",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(8, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.september",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(9, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.october",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(10, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.november",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(11, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.month.december",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));

			this.getManagedBean().setYearList(selectItems);
			selectItems = null;
		} finally {

		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the years display dtls.display years in dropdown.
	 * 
	 * @return List<SelectItem>.
	 */
	public List<SelectItem> getYearsDisplayDtls() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			selectItems.add(new SelectItem(null, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(year, String.valueOf(year)));
			for (int i = 0; i < 20; i++) {
				year = year + 1;
				selectItems.add(new SelectItem(year, String.valueOf(year)));
			}

			this.getManagedBean().setYearsDisplayList(selectItems);
			selectItems = null;
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Gets the score details.display scoresTypes in dropdown.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getScoreDetails() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		try {

			selectItems.add(new SelectItem(0, ResourceBundleUtils
					.getMessage("lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(1, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.auto.score",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(2, ResourceBundleUtils.getMessage(
					"lbl.sc.checklist.user.score",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale())));

			this.getManagedBean().setScoreTypeList(selectItems);
			selectItems = null;
		} finally {

		}
		return this.getManagedBean().getScoreTypeList();
	}

	/**
	 * Populate values for Clone Template radio button(s)
	 * 
	 * @return an array of recommend type(s)
	 */
	public SelectItem[] getCloneTemplateRadioBtn() {
		SelectItem[] items = getRadioButtonCodeValues(ParameterTypeConst.TEMPLATE_CLONE);
		return items;
	}

	/**
	 * Check list file upload.Event handle to upload technical and financial
	 * checklist files.
	 * 
	 * @param event
	 *            (FileUploadEvent)
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void checkListFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		ScQtCheckList scQtChecklist = this.getManagedBean().getScQtChecklist();
		if (null != scQtChecklist) {
			ScQtCheckListAttachment attachment = scQtChecklist
					.getScQtCheckListAttachment();
			if (null == attachment) {
				attachment = new ScQtCheckListAttachment();
				attachment.setScQtCheckList(scQtChecklist);
			}
			attachment.setDocNo(scQtChecklist.getScQt().getQtNo());

			uploadAttachment(event.getFile(), attachment);

			scQtChecklist.setScQtCheckListAttachment(attachment);
			if (null != scQtChecklist.getScQtCheckListAttachment()) {
				scQtChecklist
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			}
		}
	}

	/**
	 * Gets the selct value.
	 * 
	 * @return the selct value
	 */
	public String getSelctValue() {
		return selctValue;
	}

	/**
	 * Sets the selct value.
	 * 
	 * @param selctValue
	 *            the new selct value
	 */
	public void setSelctValue(String selctValue) {
		this.selctValue = selctValue;
	}

	/**
	 * Update bpm task.Event handle Submit/Accept/Revise in Business owner.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public void updateBpmTask(String action) {

		SpecificationReqDTO spReqDTO = null;

		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			List<ScQtRemark> remarks = this.getManagedBean()
					.getScQtRemarksList();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					ScQtRemark Remark = remarks.get(i);
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						throw new EpException(MessageConst.ERROR_760359);
					}
					if (null == Remark.getRemarkId()) {
						RequestContext.getCurrentInstance().execute(
								"reviceApprovalDlgs.show()");
					}
				}
				/*
				 * if (remarks.size() == 1) { ScQtRemark userRemark =
				 * remarks.get(0); if (null == userRemark.getRemarkId()) {
				 * RequestContext.getCurrentInstance().execute(
				 * "reviceApprovalDlgs.show()"); } else {
				 * reviseMinistrySpecApproval(); return
				 * CommonUIConst.ACKNOWLEDGE_PAGE; } } else { if (remarks.size()
				 * > 1) { reviseMinistrySpecApproval(); return
				 * CommonUIConst.ACKNOWLEDGE_PAGE; } }
				 */
			}

		} else if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
			this.getManagedBean().setLocalOutCome(action);
			// RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			// ministrySpecgpkiSignAction(null);
			// return CommonUIConst.ACKNOWLEDGE_PAGE;
			SpecificationResDTO resDTO = prepareSpecChairPersonRequest(this
					.getManagedBean().getLocalOutCome(), null);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				successMessage(this.getManagedBean().getLocalOutCome(), getMessageCommon("lbl.sc.specification.for"),
						qt.getQtNo(), Boolean.TRUE);
			}
		} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)) {
			if (this.getManagedBean().getQt().isPanel()) {
				this.getManagedBean().setSpecComplete(false);
				retriveSupplierQualificationCriteria("BO");
				retriveTechnicalSpecification();
			} else {
				retriveSupplierQualificationCriteria("BO");
			}
			spReqDTO = prepareSpecReq();
			List<ScQtLocCriteria> qtLocCriteriaRemoveList = this
					.getManagedBean().getSupplierLocationList();
			if (null != qtLocCriteriaRemoveList
					&& !qtLocCriteriaRemoveList.isEmpty()) {

				for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
					criteria.setDeleted(true);
				}
			}
			spReqDTO.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);
			spReqDTO.setOutcome(action);
			spReqDTO.setPrepareSpecStatus(this.getManagedBean()
					.getCurrentStatusId());
			spReqDTO.setTaskId(this.getManagedBean().getTaskId());
			spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			FacesScopes facesScopes = new FacesScopes();
			spReqDTO.setPmUser(facesScopes.getUserSessionBean());
			spReqDTO.setRoleCode(getUserRoleCode());
			spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			spReqDTO.setUserLocale(facesScopes.getUserLocale());
			IQTSpecSO specSO = getSpecSO();
			// #25638 - Start.
			spReqDTO.setSpecMaintenance(getManagedBean().isSpecMaintenance());
			// #25638 - End.
			SpecificationResDTO resDTO = specSO.updateBPMAction(spReqDTO);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				// Code to renew the session Object..
				renewSesionObject();

				String messageCode = "";
				if (qt.getMinistrySpecSecrataryId() == null) {
					messageCode = MessageConst.INFO_700005;
				} else {
					messageCode = MessageConst.INFO_700018;
				}
				successMessage(messageCode, getMessageCommon("lbl.sc.specification.for"), qt.getQtNo(), Boolean.TRUE);
			}

		}
	}

	/**
	 * Gets the spec so.
	 * 
	 * @return the spec so
	 */
	private IQTSpecSO getSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}

	/**
	 * Retrive zones or suppliers by item.retrive zones or suppliers based on
	 * item in supplier qualification criteria.
	 */
	private void retriveZonesOrSuppliersByItem(CtPanelItem item) {
		IPanelSupplierSO panelSupplierSO;
		IQTSupplierSO supplierSo;
		PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {

			PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							IPanelSupplierSO.class);

			// List<CtPanelItem> panelItemList =
			// getManagedBean().getPanelItemList();

			// if (CollectionUtils.isNotEmpty(panelItemList)) {
			// log.info("panel Item Name"+panelItemList.get(0));
			if (QTConstants.CT_PANEL_ZONE_COVERAGE.equalsIgnoreCase(item
					.getPanelCoverageId().getParameterCode())) {
				reqDTO.setPanelItem(item);
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
				// }
			}

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {

				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {

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

							//List<PmState> states = SourcingLocationHelper.getStateFromCtPanelZone(panelZones.getPanelZoneDtls());
							//List<PmDistrict> districts = SourcingLocationHelper.getDistrictFromCtPanelZone(panelZones.getPanelZoneDtls());

							/*#19637 - basri (remove overlapped data during save)*/
							/*getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													true, TplSpecZoneLoc.class));*/

						}
					}

				}
			} else {

				this.getManagedBean().setTotalSuppliers(0);

				// if (CollectionUtils.isNotEmpty(panelItemList)) {
				getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
				this.getManagedBean().setCtzones(false);
				/*
				 * PanelSupplierReqDTO supplierreqDTO = new
				 * PanelSupplierReqDTO();
				 * supplierreqDTO.setPanelItemId(this.getManagedBean()
				 * .getCtPanelItem().getPanelItemId());
				 * supplierreqDTO.setFetchSubEntity(true); PanelSupplierResDTO
				 * panelSupplierResDTO = panelSupplierSO
				 * .retrievePanelItem(supplierreqDTO);
				 */
				supplierSo = (IQTSupplierSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSupplierSO.class);
				QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
				QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
				supplierReqDTO.setPanelItemId(item.getPanelItemId());
				supplierResDTO = supplierSo
						.retrievePanelSuppliersByPanelItem(supplierReqDTO);
				this.getManagedBean().setCtZoneSupplierList(
						new ArrayList<CtPanelSupplier>());
				if (null != supplierResDTO.getCtPanelSuppliers()) {
					// CtPanelItem panelItem =
					// supplierResDTO.getCtPanelSuppliers();
					this.getManagedBean().setTotalSuppliers(
							supplierResDTO.getCtPanelSuppliers().size());
					for (CtPanelSupplier paelSupplier : supplierResDTO
							.getCtPanelSuppliers()) {
						paelSupplier
								.setSmMofAccount(retriveMofAccountBySupplier(paelSupplier
										.getSupplier()));
					}
					this.getManagedBean().setCtZoneSupplierList(
							supplierResDTO.getCtPanelSuppliers());

				}
				// }

			}
			/*
			 * List<CtPanelItem> itemList = this.getManagedBean()
			 * .getSelectpanelItem(); itemList = new ArrayList<CtPanelItem>();
			 * itemList.add(this.getManagedBean().getCtPanelItem());
			 * 
			 * this.getManagedBean().setSelectpanelItem(itemList);
			 */

		} finally {
			panelSupplierSO = null;
			supplierSo = null;
		}

	}

	/**
	 * Un select panel yes treenode.Event handle to unselect panel treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectPanelYesTreenode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Retrive supplier dtls.retrive supplier information based on supplier in
	 * supplier qualification criteria.
	 * 
	 * @param panelSupplier
	 *            (CtPanelSupplier panelSupplier).
	 */
	public void retriveSupplierDtls(CtPanelSupplier panelSupplier) {

		QTSupplierResDTO qtSupplierResDTO = new QTSupplierResDTO();
		MofAccountResDTO mofAccResDTO = new MofAccountResDTO();
		IQTSupplierSO qtSupplierSO = null;
		IMofAccountSO mofAccountSO = null;

		try {
			if (panelSupplier.getSupplier() != null) {
				qtSupplierSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSupplierSO.class);
				QTSupplierReqDTO qtSupplierReqDTO = new QTSupplierReqDTO();
				qtSupplierReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				qtSupplierResDTO = qtSupplierSO
						.retrieveSupplierDetails(qtSupplierReqDTO);
				panelSupplier.setSupplier(qtSupplierResDTO.getSmSupplier());
				// getManagedBean().setSmSupplier(smSupplier);
				// this.getManagedBean().setCtpanelSupplier(panelSupplier);

				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IMofAccountSO.class);
				MofAccountReqDTO mofAccReqDTO = new MofAccountReqDTO();
				mofAccReqDTO.setSupplierId(panelSupplier.getSupplier()
						.getSupplierId());
				mofAccResDTO = mofAccountSO
						.retrieveMofNoBySupplierId(mofAccReqDTO);
				getManagedBean().setSmMofAccount(mofAccResDTO.getMofAccount());

				SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
						.getSmAppl().getSmCompanyBasic();
				// getManagedBean().setSmCompanyBasic(smCompanyBasic);

				List<SmPersonnel> smPersonnelList = panelSupplier.getSupplier()
						.getSmAppl().getSmPersonnels();
				if (null != smPersonnelList) {
					for (SmPersonnel smPersonnel : smPersonnelList) {
						if (smPersonnel.isContactPerson()) {
							// getManagedBean().setSmPersonnel(smPersonnel);
							getManagedBean().setPersonnel(smPersonnel);

							break;
						}
					}
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
				if (null != smCompanyBasic) {
					for (SmCompanyAddress smCompanyAddress : smCompanyBasic
							.getSmCompanyAddresses()) {
						if (AddressType.CONTACT_ADDRESS.value().equals(
								smCompanyAddress.getAddressType())) {
							getManagedBean().setSupplierBranch(
									QTConstants.SUPPLIER_INFO_HQ);
							getManagedBean().setSupplierAddress(
									smCompanyAddress.getSmAddress());
							break;
						}
					}
				}
				this.getManagedBean().setCtpanelSupplier(panelSupplier);
			}
		} finally {
			qtSupplierSO = null;
			mofAccountSO = null;
		}

		/*
		 * if (null != panelSupplier.getSupplier() && null !=
		 * panelSupplier.getSupplier().getSmAppl() && null !=
		 * panelSupplier.getSupplier().getSmAppl() .getSmPersonnels()) {
		 * List<SmPersonnel> list = panelSupplier.getSupplier().getSmAppl()
		 * .getSmPersonnels(); if (null != list) {
		 * 
		 * for (SmPersonnel smPersonnel : list) {
		 * getManagedBean().setPersonnel(smPersonnel); } SmPersonnel
		 * supplierHQPersonnel = this.getManagedBean() .getPersonnel();
		 * getManagedBean().setSupplierContact("");
		 * getManagedBean().setSupplierEmail(""); if (null !=
		 * supplierHQPersonnel) { getManagedBean().setSupplierContact(
		 * supplierHQPersonnel.getName()); getManagedBean().setSupplierEmail(
		 * supplierHQPersonnel.getEmail()); } // SmAppl appll=
		 * supplierHQPersonnel.getSmAppl();
		 * 
		 * SmCompanyBasic smCompanyBasic = panelSupplier.getSupplier()
		 * .getSmAppl().getSmCompanyBasic();
		 * 
		 * // System.out.println("INcomeTax No : " + //
		 * smCompanyBasic.getIncomeTaxNo());
		 * 
		 * if (null != smCompanyBasic) { if (null !=
		 * smCompanyBasic.getSmCompanyAddresses()) { for (SmCompanyAddress
		 * smCompanyAddress : smCompanyBasic .getSmCompanyAddresses()) { if
		 * (null != smCompanyAddress) { if
		 * (AddressType.CONTACT_ADDRESS.value().equals(
		 * smCompanyAddress.getAddressType())) {
		 * 
		 * getManagedBean().setSupplierBranch("HQ");
		 * 
		 * getManagedBean().setSupplierAddress(
		 * smCompanyAddress.getSmAddress());
		 * 
		 * break;
		 * 
		 * } } } } } } }
		 */

	}

	/**
	 * Circular list select.retrieve Panel Item List based on circular
	 * information.
	 * 
	 * @param circular
	 *            (CtCircularVer circular).
	 */
	public void circularListSelect(CtCircularVer circular) {
		ICircularDetailSO circularDetailSO;
		try {
			CircularDetailReqDTO reqDTO = new CircularDetailReqDTO();
			this.getManagedBean().setCtCircularVer(circular);
			circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICircularDetailSO.class);
			if (null != circular) {
				if(null!=circular.getCtCircular().getCurrentCircularVerId()&& null!=circular.getCtCircular().getCurrentCircularVerId().getCircularVerId()&& !circular.getCircularVerId().equals(circular.getCtCircular().getCurrentCircularVerId().getCircularVerId())){ //#8485-start
					reqDTO.setCircularVerId(circular.getCtCircular().getCurrentCircularVerId().getCircularVerId());
					
				}else{
					reqDTO.setCircularVerId(circular.getCircularVerId());	
				} //#8485-End	
				// reqDTO.setQuotationTender(true);
				reqDTO.setPanelItemTypeGroup(true);
				if (null != this.getManagedBean().getQt().getItemGroup()) {
					reqDTO.setGroupItemId(this.getManagedBean().getQt()
							.getItemGroup().getPanelItemGroupId());
				}
				CircularDetailResDTO resDTO = circularDetailSO
						.getPanelItemsByCircularVerId(reqDTO);
				// this.getManagedBean().setCircularExpDate(circular.getEndDate());
				// this.getManagedBean().setCircularNo(circular.getCircularNo());
				// this.getManagedBean().setCircularName(circular.getName());
				this.getManagedBean().setCircularType(circular.getDocType());
				this.getManagedBean().setSearchPanelItemList(
						resDTO.getPanelItemList());
				this.getManagedBean().setSelectCtPanelItem(
						new ArrayList<CtPanelItem>());
				this.getManagedBean().setAutoUpdate(false);
			}

		} finally {
			circularDetailSO = null;
		}

	}

	/**
	 * Tech weightage sum.calculate technical scoring weightage.
	 */
	private void techWeightageSum() {
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
					//20201127@asyrafhaziq_29637 - Start
					String checkDecimal1 = String.valueOf(weight.getQtScoreWeightage().getWeightage());
					String pattern1 = "^[0-9\\s]+$";
						if (!checkDecimal1.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}
					 //20201127@asyrafhaziq_29637 - End
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setTechnicalWeightageScore(
						new BigDecimal(total));
				if (total > 100) {
					throw new EpException(MessageConst.ERROR_760148);
				}

			}
		} finally {

		}

	}

	/**
	 * Tech scoring check list.Event handle change scores in technical scoring
	 * Menu.
	 * 
	 * @param checkList
	 *            (ScQtCheckList checkList)
	 */
	public void techScoringCheckList(ScQtCheckList checkList) {
		techScoreSum();
		techWeightageSum();
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null == technicalCheckList) {
			technicalCheckList = new ArrayList<ScQtCheckList>();
		}
		
		// Bug #33249@Syazani 28/07/2022 -- Start
		String curCheckList = checkList.getTitle() + "_"
				+ (checkList.getDefinationType() != null ? checkList.getDefinationType().getParameterId() : "");
		for (int i = 0; i < technicalCheckList.size(); i++) {
			ScQtCheckList localCheckList = technicalCheckList.get(i);
			String loopCheckList = localCheckList.getTitle() + "_"
					+ (localCheckList.getDefinationType() != null ? localCheckList.getDefinationType().getParameterId() : "");
			if (curCheckList.equals(loopCheckList)) {
				// ScQtCheckList localCheckList = technicalCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				// technicalCheckList.remove(localCheckList);
				// technicalCheckList.add(checkList);
				technicalCheckList.set(i, checkList);
				// Bug #33249@Syazani 28/07/2022 -- End
				break;
			}
		}
	}

	/**
	 * Fin scoring check list.Event handle change scores in Financial scoring
	 * Menu.
	 * 
	 * @param checkList
	 *            (ScQtCheckList checkList).
	 */
	public void finScoringCheckList(ScQtCheckList checkList) {
		//#25138 Reendorsed - settotalpoints - start 
		if(this.getManagedBean().getQt().isZonal()){
			BigDecimal totalPoints = null;
			finScoreSum();
			int finalScorePoint = this.getManagedBean().getFinPoints();
			TplSpecZoneDtl specZoneDtl = this.getManagedBean().getSelectedZone();
			if(specZoneDtl != null){
				totalPoints = specZoneDtl.getTotalMaxScore();					
			
			}else{
				totalPoints = this.getManagedBean().getTemplateSpecZoneDtls()
						.get(0).getTotalMaxScore();
			}
			finalScorePoint = finalScorePoint + totalPoints.intValue();
			this.getManagedBean().setFinPoints(finalScorePoint);
			System.out.println("totalFinalPoint===>"+finalScorePoint);
		}else{
			finScoreSum();

		}
		//#25138 Reendorsed - settotalpoints - end
		finWeightageSum(false);	// Bug #35688@Syazani 10/07/2024
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null == financialCheckList) {
			financialCheckList = new ArrayList<ScQtCheckList>();
		}
		
		// Bug #33249@Syazani 28/07/2022 -- Start
		String curCheckList = checkList.getTitle() + "_" + (checkList.getDefinationType() != null ? checkList.getDefinationType().getParameterId() : "");
		for (int i = 0; i < financialCheckList.size(); i++) {
			ScQtCheckList localCheckList = financialCheckList.get(i);	
			String loopCheckList = localCheckList.getTitle() + "_" + (localCheckList.getDefinationType() != null ? localCheckList.getDefinationType().getParameterId() : "");
			if (curCheckList.equals(loopCheckList)) {
				// ScQtCheckList localCheckList = financialCheckList.get(i);
				// technicalCheckList.setDeleted(true);
				// toBeRemoveCheckLists.add(technicalCheckList);
				// financialCheckList.remove(localCheckList);
				// financialCheckList.add(checkList);
				financialCheckList.set(i, checkList);
				// Bug #33249@Syazani 28/07/2022 -- End
				// #25138 - set score weightage for dettype 217 - start
				financialCheckList.get(i).getQtScoreWeightage().setScore(checkList
						.getQtScoreWeightage().getScore());
				//#25138 - set score weightage for dettype 217 - end
				break;
			}
		}
	}

	/**
	 * Tech score sum.calculate technical scoring sum.
	 */
	private void techScoreSum() {
		try {
			// 37813,35384 -start
			BigDecimal totalScore = BigDecimal.valueOf(0);
			// 37813,35384 -end
			List<ScQtCheckList> weightageList = this.getManagedBean()
					.getTechnicalScoring();
			if (weightageList == null) {
				// 37813,35384 -start
				weightageList = new ArrayList<>();
				// 37813,35384 -end
			}
			for (int i = 0; i < weightageList.size(); i++) {
				// 37813,35384 -start
				ScQtCheckList weight = weightageList.get(i);
				if (null != weight.getQtScoreWeightage()){	
				ScQtScoreWeightage checklistWeighatge=setWeightageScore(weight.getQtScoreWeightage());
				weight.setQtScoreWeightage(checklistWeighatge);
				BigDecimal checkListScore = weight.getQtScoreWeightage()
							.getScore() != null ? weight.getQtScoreWeightage()
							.getScore() : BigDecimal.valueOf(0);
				totalScore = totalScore.add(checkListScore);
				// 37813,35384 -end
				}
				
				
			}
			// 37813,35384 -start
			this.getManagedBean().setTechPoints(totalScore);
			// 37813,35384 -end
		} finally {

		}

	}
	
	// 37813,35384 -start
	/**
	 * set checkist score weight.
	 * @param scoreWeightage
	 * @return ScQtScoreWeightage
	 */
	private ScQtScoreWeightage setWeightageScore(ScQtScoreWeightage scoreWeightage) {
		if (null == scoreWeightage.getWeightage()) {
			scoreWeightage.setWeightage(new BigDecimal(0));
		}
		return scoreWeightage;
	}
	// 37813,35384 -end

	/**
	 * Fin score sum.calculate financial score sum.
	 */
	private void finScoreSum() {
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
					//#25138 Reendorsed - start 
					if(this.getManagedBean().getQt().isZonal()){
						if(!ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
								.equalsIgnoreCase(weight.getDefinationType()
										.getParameterCode())){
						total = total
								+ weight.getQtScoreWeightage().getScore()
										.intValue();	
						}
					}else{
						total = total
								+ weight.getQtScoreWeightage().getScore()
										.intValue();
					}
					//#25138 Reendorsed - end
					
				}
				BigDecimal techScore = new BigDecimal(total);
				this.getManagedBean().setFinPoints(techScore.intValue());
			}
		} finally {

		}

	}

	/**
	 * Financial weightage calculate.calculate financial weightage in financial
	 * scoring menu.
	 */
	private void financialWeightageCalculate() {
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
			List<ScQtScoreWeightage> finscoreWeightage = new ArrayList<ScQtScoreWeightage>();
			int financialWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checklistlocal.getDefinationType()
								.getParameterCode())) {
					if (null != checklistlocal.getQtScoreWeightage()) {
						map.put(checklistlocal.getQtScoreWeightage()
								.getQtScoreWeightageId(), checklistlocal
								.getQtScoreWeightage());
					}
				} else {
					if (null != checklistlocal.getQtScoreWeightage()) {
						finscoreWeightage.add(checklistlocal
								.getQtScoreWeightage());
					}
				}
			}
			List<ScQtScoreWeightage> list = new ArrayList<ScQtScoreWeightage>(
					map.values());
			list.addAll(finscoreWeightage);
			if (null != list && !list.isEmpty()) {
				for (ScQtScoreWeightage weightage : list) {
					if (null == weightage.getWeightage()) {
						weightage.setWeightage(new BigDecimal(0));
					}
					financialWeightage = financialWeightage
							+ weightage.getWeightage().intValue();
				}
				if (financialWeightage < 100) {
					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760221);
				}
			}
			map = null;
			finscoreWeightage = null;
		}

	}

	/**
	 * Technical weightage calculate.calculate technical weightage in technical
	 * scoring menu.
	 */
	private void technicalWeightageCalculate() {
		if (null != this.getManagedBean().getTechnicalScoreSetting()
				&& this.getManagedBean().getTechnicalScoreSetting()
						.isWeightageReq()
				&& !this.getManagedBean().isReadOnly()) {
			List<ScQtCheckList> checklist = this.getManagedBean()
					.getTechnicalScoring();
			if (null == checklist) {
				checklist = new ArrayList<ScQtCheckList>();
			}
			// Map <ScQtScoreWeightage> map = new HashMap<ScQtScoreWeightage>();
			int technicalWeightage = 0;
			for (ScQtCheckList checklistlocal : checklist) {
				if (null != checklistlocal.getQtScoreWeightage()) {
					if (null == checklistlocal.getQtScoreWeightage()
							.getWeightage()) {
						checklistlocal.getQtScoreWeightage().setWeightage(
								new BigDecimal(0));
					}

					technicalWeightage = technicalWeightage
							+ checklistlocal.getQtScoreWeightage()
									.getWeightage().intValue();
				}
			}

			if (technicalWeightage < 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760220);
			}
		}

	}

	/**
	 * Fin weightage sum.calculate Financial weightage.
	 */
	private void finWeightageSum(boolean notThrowError) {	// Bug #35688@Syazani 10/07/2024
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
					//20201127@asyrafhaziq_29637 - Start
					String checkDecimal1 = String.valueOf(weight.getQtScoreWeightage().getWeightage());
					String pattern1 = "^[0-9\\s]+$";
						if (!checkDecimal1.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}
						//20201127@asyrafhaziq_29637 - End
					total = total
							+ weight.getQtScoreWeightage().getWeightage()
									.intValue();
				}
				this.getManagedBean().setFinancialWeightageScore(
						new BigDecimal(total));

				if (!notThrowError && total > 100) {	// Bug #35688@Syazani 10/07/2024
					throw new EpException(MessageConst.ERROR_760148);
				}

			}
		} finally {

		}

	}

	/**
	 * Spec offerprice package from varieance.check offer price from varienace
	 * null or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageFromVarieance(
			ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getFromVariance()
					|| offerPrice.getFromVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760224);
			}

		}

	}

	/**
	 * Spec offerprice package to varieance.check offer price to varienace null
	 * or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageToVarieance(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getToVariance()/*
												 * ||
												 * offerPrice.getToVariance().
												 * intValue() <= 0
												 */) {
				throw new EpException(MessageConst.ERROR_760179);
			}

		}

	}

	/**
	 * Spec offerprice package below score.check offer price belowScore null or
	 * less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 * 
	 * 
	 */
	public void specOfferpricePackageBelowScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {
			if (null == offerPrice.getBelowScore()
					|| offerPrice.getBelowScore().intValue() < 0) {
				throw new EpException(MessageConst.ERROR_760216);
			}

		}

	}

	/**
	 * Spec offerprice package above score.check offer price aboveScore null or
	 * less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 * 
	 * 
	 */
	public void specOfferpricePackageAboveScore(ScQtScoreOfferPrice offerPrice) {
		if (null != offerPrice) {

			if (null == offerPrice.getAboveScore()
					|| offerPrice.getAboveScore().intValue() < 0) {
				throw new EpException(MessageConst.ERROR_760217);
			}
		}

	}

	/**
	 * Check technical passing score.check technical passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param technicalScoreSetting
	 *            (ScQtScoreSetting technicalScoreSetting).
	 */
	public void checkTechnicalPassingScore(
			ScQtScoreSetting technicalScoreSetting) {
		if (null != technicalScoreSetting) {
			BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE);
			if (null == technicalScoreSetting.getPassingScore()
					|| technicalScoreSetting.getPassingScore().intValue() <= 0) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760234);
			}
			if (null != technicalScoreSetting.getPassingScore()
					&& technicalScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760142);
			}
			if (null != passingScore
					&& (technicalScoreSetting.getPassingScore().intValue() < passingScore
							.intValue())) {
				technicalScoreSetting.setPassingScore(new BigDecimal(50));
				this.getManagedBean().setTechnicalScoreSetting(
						technicalScoreSetting);
				throw new EpException(MessageConst.ERROR_760364,
						new String[] { String.valueOf(passingScore) });
			}

		}

	}
	
//#21695 Farid Start 12/8/2020
		/**
		 * Check technical Item passing score.check technical Item passing score null or less
		 * than zero or greater than hundred.
		 * 
		 * @param technicalScoreSetting
		 *            (ScQtScoreSetting technicalItemScoreSetting).
		 */
		public void checkTechnicalItemPassingScore(
				ScQtScoreSetting technicalItemScoreSetting) {
			if (null != technicalItemScoreSetting) {
				BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE);
				if (null == technicalItemScoreSetting.getPassingScore()
						|| technicalItemScoreSetting.getPassingScore().intValue() < 50) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_775155);
				}
				if (null != technicalItemScoreSetting.getPassingScore()
						&& technicalItemScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760142);
				}
				if (null != passingScore
						&& (technicalItemScoreSetting.getPassingScore().intValue() < passingScore
								.intValue())) {
					technicalItemScoreSetting.setPassingScore(new BigDecimal(50));
					this.getManagedBean().setTechnicalItemScoreSetting(
							technicalItemScoreSetting);
					throw new EpException(MessageConst.ERROR_775155,
							new String[] { String.valueOf(passingScore) });
				}
			 }
		}

	/**
	 * Check financial passing score.check financial passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param financialScoreSetting
	 *            (ScQtScoreSetting financialScoreSetting).
	 * 
	 * 
	 */
	public void checkFinancialPassingScore(
			ScQtScoreSetting financialScoreSetting) {
		if (null != financialScoreSetting) {

			if (null == financialScoreSetting.getPassingScore()
					|| financialScoreSetting.getPassingScore().intValue() < 0) {
				this.getManagedBean().setActiveIndex(5);
				throw new EpException(MessageConst.ERROR_760174);
			} else if (null != financialScoreSetting.getPassingScore()
					&& financialScoreSetting.getPassingScore().intValue() > 100) {
				this.getManagedBean().setActiveIndex(5);
				throw new EpException(MessageConst.ERROR_760143);
			}
		}

	}

	/**
	 * Check demo passing score..check technical demo passing score null or less
	 * than zero or greater than hundred.
	 * 
	 * @param demoScoreSetting
	 *            (ScQtScoreSetting demoScoreSetting).
	 * 
	 */
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
	 * Gets the search circular.
	 * 
	 * @return the search circular
	 */
	public IPagingDataModel<CtCircularVer> getSearchCircular() {
		return searchCircular;
	}

	/**
	 * Sets the search circular.
	 * 
	 * @param searchCircular
	 *            the new search circular
	 */
	public void setSearchCircular(IPagingDataModel<CtCircularVer> searchCircular) {
		this.searchCircular = searchCircular;
	}

	/**
	 * Search circular list.user clicks on search button in circular item
	 * search.
	 */
	public void searchCircularList() {
		searchCircular.reloadData();
		this.getManagedBean().setCircularVer(null);

	}

	/**
	 * The Class SearchCircularPagingDataModel.search circular paging data
	 * model.
	 */
	private class SearchCircularPagingDataModel extends
			PagingDataModel<CtCircularVer> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search circular paging data model.
		 */
		public SearchCircularPagingDataModel() {

			super("circularList");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
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

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("qt",
								SearchOperator.OPERATOR_EQUALS.value(), true));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("docType",
								SearchOperator.OPERATOR_EQUALS.value(),
								ParameterConst.DOCTYPE_PANEL_CIRCULAR));

				circularReqDTO.setCircularVerSearchBean(searchBean);

				circularSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
								ICircularSO.class);
				// resDto = circularSO.searchCircularVer(circularReqDTO);
				resDto = circularSO.retrieveActiveCircular(circularReqDTO);

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

	/**
	 * Search template spec action.Event handle search template spec.
	 */
	public void searchTemplateSpecAction() {
		templateSpecCreate.reloadData();
	}

	/**
	 * Search delivery address.Event handle search delivery addresses.
	 */
	public void searchDeliveryAddress() {
		getManagedBean().getSearchDeliveryAddressDataModel().reloadData();
	}

	/**
	 * Reset template.Event handle to clear all the template spec data.
	 */
	public void resetTemplate() {
		getManagedBean().setSpecName(null);
		getManagedBean().setSearchCreatedBy(null);
		// getManagedBean().setStandardTemplate(null);
		// getManagedBean().setSearchProcurementId(null);
		if (!this.getManagedBean().getQt().isPanel()) {
			getManagedBean().setSearchItemTypeId(null);
		}
		this.getManagedBean().getQtnTendertemplateSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTendertemplateSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setProcurementMode(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromCreatedDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToCreatedDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromPublishDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToPublishDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setFromClosingDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setToClosingDate(null);
		this.getManagedBean().getQtnTendertemplateSearchBean()
				.setQtStatus(null);
		//#29468 Start Vinod
		for(TreeNode childTree : this.getManagedBean().getRootCategoryTree().getChildren()){
			childTree.setSelected(false);
		}
		//#29468 End Vinod
	}

	/**
	 * The Class CreateTemplateSpecPagingDataModel.search Template spec paging
	 * data model.
	 */
	private class CreateTemplateSpecPagingDataModel extends
			PagingDataModel<TplSpecMaster> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new creates the template spec paging data model.
		 */
		public CreateTemplateSpecPagingDataModel() {

			super("createList", false);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<TplSpecMaster> pageChangeListener(
				ISearchBean<TplSpecMaster> searchBean) {
			ITemplateSpecSO qtTemplateSO;
			TemplateSpecResDTO resDto;
			IParameterSO parameterSo;

			try {
				 //20211005@fadhli - #31754
				String sessionId = ((PortletRequest) FacesContext.getCurrentInstance()
						.getExternalContext().getRequest()).getRequestedSessionId(); 
				
				long startTime = System.currentTimeMillis();
				long subStartTime = 0L;
				
				TemplateSpecReqDTO qtTemplateReqDTO = new TemplateSpecReqDTO();
				parameterSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				ParameterReqDTO techSpecType = new ParameterReqDTO();
				techSpecType
						.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
				techSpecType.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
				subStartTime = System.currentTimeMillis();
				ParameterResDTO techspecTyperesDTO = parameterSo
						.retrieveParameterByTypeAndCode(techSpecType);
				SLAUtils.getInstance().logSLASubInfo(ModuleCodeConst.SUPPLIER_SELECTION, 
						ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, sessionId, "null",
						"SpecificationBackingBean.pageChangeListener", "retrieveParameterByTypeAndCode", 
						subStartTime); //20211005@fadhli - #31754
				String specTypeCode = techspecTyperesDTO.getPmParameter()
						.getParameterCode();
				log.info("[CreateTemplateSpecPagingDataModel#pageChangeListener] #31754 getManagedBean().getStandardTemplate(): " 
						+ getManagedBean().getStandardTemplate());
				if (TemplateSpecConst.PROCESS_TYPE_STANDARD
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					if (null != getManagedBean().getQt()
							.getPmProcurementCategory()
							&& null != getManagedBean().getQt()
									.getPmProcurementCategory()
									.getParameterId()) {
						getManagedBean().setSearchProcurementId(
								getManagedBean().getQt()
										.getPmProcurementCategory()
										.getParameterId());
					}

					// Filter By - created by
					if (getManagedBean().getSearchCreatedBy() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("createdBy",
										SearchOperator.OPERATOR_EQUALS.value(),
										getManagedBean().getSearchCreatedBy()));
					}

					// Filter By - ministry orgProfileId
					ScQt scQt = getManagedBean().getQt();
					List<String> templeteTypes = new ArrayList<String>();
					templeteTypes.add(ParameterConst.TEMPLATE_TYPE_MINISTRY);
					templeteTypes.add(ParameterConst.TEMPLATE_TYPE_PTJ);

					subStartTime = System.currentTimeMillis();
					List<Long> orgProfileIds = getMinistryPTJOrgIds(scQt
							.getPmOrgProfile());
					SLAUtils.getInstance().logSLASubInfo(ModuleCodeConst.SUPPLIER_SELECTION, 
							ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, sessionId, "null",
							"SpecificationBackingBean.pageChangeListener", "getMinistryPTJOrgIds(getPmOrgProfile())", 
							subStartTime); //20211005@fadhli - #31754
					subStartTime = System.currentTimeMillis();
					List<Long> ownerOrgProfileIds = getMinistryPTJOrgIds(scQt
							.getOwnerOrgProfile());
					SLAUtils.getInstance().logSLASubInfo(ModuleCodeConst.SUPPLIER_SELECTION, 
							ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, sessionId, "null",
							"SpecificationBackingBean.pageChangeListener", "getMinistryPTJOrgIds(getOwnerOrgProfile())", 
							subStartTime); //20211005@fadhli - #31754

					if (ownerOrgProfileIds != null
							&& ownerOrgProfileIds.size() > 0) {
						orgProfileIds.addAll(ownerOrgProfileIds);
					}

					SearchMultiFilterBean orgFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_IN.value(),
									templeteTypes), new SearchFilterBean(
									"orgProfile.orgProfileId",
									SearchOperator.OPERATOR_IN.value(),
									orgProfileIds));

					SearchMultiFilterBean ownFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									ParameterConst.TEMPLATE_TYPE_OWN),
							SearchCondition.CONDITION_OR.value(),
							new SearchFilterBean("createdBy",
									SearchOperator.OPERATOR_EQUALS.value(),
									new FacesScopes().getUserSessionBean()
											.getUserId()));

					SearchMultiFilterBean allFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"templateTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(),
									ParameterConst.TEMPLATE_TYPE_ALL),
							SearchCondition.CONDITION_OR.value(),
							new SearchFilterBean("orgProfile.orgProfileId",
									SearchOperator.OPERATOR_IS_NULL.value(),
									null));

					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									new SearchMultiFilterBean(orgFilterBean,
											ownFilterBean), allFilterBean));

					qtTemplateReqDTO.setOrderByBlankTemplate(true);
					templeteTypes = null;

				} else if (TemplateSpecConst.PROCESS_TYPE_SOURCING
						.equalsIgnoreCase(getManagedBean()
								.getStandardTemplate())) {
					QtnTenderSearchBean templateQtnTenderSearchBean = getManagedBean()
							.getQtnTendertemplateSearchBean();
					if (null == templateQtnTenderSearchBean.getQtNo()
							|| "".equalsIgnoreCase(templateQtnTenderSearchBean
									.getQtNo())) {
						getManagedBean().getQtnTendertemplateSearchBean()
								.setQtNo("QT");
					}
					if (null != getManagedBean().getQt()
							.getPmProcurementCategory()
							&& null != getManagedBean().getQt()
									.getPmProcurementCategory()
									.getParameterId()) {
						getManagedBean().setSearchProcurementId(
								getManagedBean().getQt()
										.getPmProcurementCategory()
										.getParameterId());
					}
					subStartTime = System.currentTimeMillis();
					populateQTSearchCriteria(templateQtnTenderSearchBean,
							searchBean);
					SLAUtils.getInstance().logSLASubInfo(ModuleCodeConst.SUPPLIER_SELECTION, 
							ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, sessionId, "null",
							"SpecificationBackingBean.pageChangeListener", "populateQTSearchCriteria", 
							subStartTime); //20211005@fadhli - #31754
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
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getStandardTemplate()));
				}

				if (null != getManagedBean().getSearchItemTypeId()) {
					SearchFilterBean itemSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					itemSearchBean.setSearchField("itemTypeCode.parameterId");
					itemSearchBean.setSearchValue(getManagedBean()
							.getSearchItemTypeId());
					searchBean.getDataFilterBeans().add(itemSearchBean);
				}

				if (null != getManagedBean().getSearchProcurementId()) {
					SearchFilterBean procurementSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					procurementSearchBean
							.setSearchField("procurementCategory.parameterId");
					procurementSearchBean.setSearchValue(getManagedBean()
							.getSearchProcurementId());
					searchBean.getDataFilterBeans().add(procurementSearchBean);
				}

				if (null != specTypeCode) {
					SearchFilterBean qtnoSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					qtnoSearchBean.setSearchField("specTypeCode.parameterCode");
					qtnoSearchBean.setSearchValue(specTypeCode);

					searchBean.getDataFilterBeans().add(qtnoSearchBean);
				}

				qtTemplateReqDTO.setSearchBean(searchBean);

				qtTemplateSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
								ITemplateSpecSO.class);
				subStartTime = System.currentTimeMillis();
				resDto = qtTemplateSO.searchTemplateMaster(qtTemplateReqDTO);
				String qtNumber = "null";
				if (null != getManagedBean() && null != getManagedBean().getQt()) {
					qtNumber = getManagedBean().getQt().getQtNo();
				}
				SLAUtils.getInstance().logSLASubInfo(ModuleCodeConst.SUPPLIER_SELECTION, 
						ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, sessionId, qtNumber,
						"SpecificationBackingBean.pageChangeListener", "searchTemplateMaster", 
						subStartTime); //20211005@fadhli - #31754

				if (resDto != null) {
					SLAUtils.getInstance().logSLAInfo(ModuleCodeConst.SUPPLIER_SELECTION, ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, 
						"SpecificationBackingBean.pageChangeListener", startTime);
					
					return resDto.getSearchResultBean();
				}

			} finally {
				qtTemplateSO = null;
				parameterSo = null;
			}
			return null;
		}

	}

	private List<Long> getMinistryPTJOrgIds(PmOrgProfile pmOrgProfile) {
		List<Long> orgProfileIds = new ArrayList<Long>();

		if (pmOrgProfile != null) {
			IOrgProfileSO orgProfileSO = null;
			try {
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);

				Long orgProfileId = pmOrgProfile.getOrgProfileId();

				orgProfileIds.add(orgProfileId);

				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				orgProfileReqDTO.setOrgProfileId(orgProfileId);
				PmOrgProfile ministryOrgProfile = orgProfileSO
						.retrieveMinistryByOrgProfileId(orgProfileReqDTO)
						.getPmOrgProfile();
				if (ministryOrgProfile != null) {
					orgProfileIds.add(ministryOrgProfile.getOrgProfileId());
				}
			} finally {
				orgProfileSO = null;
			}
		}

		return orgProfileIds;
	}

	/**
	 * The Class DeliveryPagingDataModel.search delivery addresses paging data
	 * model.
	 */
	private class DeliveryPagingDataModel extends
			PagingDataModel<PmAddressType> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new delivery paging data model.
		 */
		public DeliveryPagingDataModel(String componentId) {
			super(componentId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
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
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile."
											+ OrgProfileUtil
													.getSearchBeanParentString(OrganizationType.MINISTRY
															.value()),
									SearchOperator.OPERATOR_EQUALS.value(),

									getManagedBean().getMinistryId()

							));
				}

				if (null != getManagedBean().getPtjGroupId()) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile."
											+ OrgProfileUtil
													.getSearchBeanParentString(OrganizationType.DEPARTMENT
															.value()),
									SearchOperator.OPERATOR_EQUALS.value(),

									getManagedBean().getPtjGroupId()

							));
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
				addressSO = null;
			}
			return null;
		}
	}

	/**
	 * Gets the template spec create.
	 * 
	 * @return the template spec create
	 */
	public IPagingDataModel<TplSpecMaster> getTemplateSpecCreate() {
		return templateSpecCreate;
	}

	/**
	 * Sets the template spec create.
	 * 
	 * @param templateSpecCreate
	 *            the new template spec create
	 */
	public void setTemplateSpecCreate(
			IPagingDataModel<TplSpecMaster> templateSpecCreate) {
		this.templateSpecCreate = templateSpecCreate;
	}

	/**
	 * Gets the current tab.
	 * 
	 * @return the current tab
	 */
	public int getCurrentTab() {
		return currentTab;
	}

	/**
	 * Sets the current tab.
	 * 
	 * @param currentTab
	 *            the new current tab
	 */
	public void setCurrentTab(int currentTab) {
		this.currentTab = currentTab;
	}

	/**
	 * Gets the output tab.
	 * 
	 * @return the output tab
	 */
	public int getOutputTab() {
		return outputTab;
	}

	/**
	 * Sets the output tab.
	 * 
	 * @param outputTab
	 *            the new output tab
	 */
	public void setOutputTab(int outputTab) {
		this.outputTab = outputTab;
	}

	/**
	 * user click on task in task list menu.it will call preRenderView() method
	 * and Renders the specification committe screens or pages..
	 */
	public void preRenderView() {

		SCQuotationTenderDataType quotationTenderDataType = getBpmTaskDetails(
				SCQuotationTenderDataType.class, null);
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();

		if (SCProcessNameConst.SPECIFICATION_APPROVAL.equalsIgnoreCase(this
				.getManagedBean().getTaskName())) {
			this.getManagedBean().setReadOnly(true);

		}
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		//35056 - 4serangkai - start
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		specReqDTO.setQtId(quotationTenderDataType.getQtId().longValue());
		specReqDTO.setPmUser(curLogedInUser);
		this.getSpecSO().insertTechnicalItemQtScoreSetting(specReqDTO);
		//35056 - 4serangkai - end

		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(quotationTenderDataType.getQtId().longValue());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO resDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = resDTO.getCommMember();
		if (null != member) {
			//danial #8979 17/12/2020
			if (member.getMemberRole().getParameterCode()
                    .equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)
                    ) {//30917-start
				this.getManagedBean().setReadOnly(false);

			} else {

				this.getManagedBean().setReadOnly(true);
			}
		}
		retriveSpecdetails(quotationTenderDataType.getQtId().longValue());
		ScQt scQt = this.getManagedBean().getQt();
		
		//#28784 - Demo WIP - Start
		checkDemoVersion(scQt);
		//28784 - End			
		
		// #25638 - Start
		if (null != this.getManagedBean().getCurrentStatusId()
				&& QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH.equals(this.getManagedBean().getCurrentStatusId()) 
				&& null != scQt && null != scQt.getScQt()) {
			removeReviseForRepublishQT();
		}
		// #25638 - End
		
		/*#19637-basri*/
		if(scQt != null && scQt.isZonal()  && scQt.isPanel()){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
			this.getManagedBean().setPanelZone(true);
		}
		/*end*/
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {

			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());

		}
		if (null != this.getManagedBean().getQt().getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(
					this.getManagedBean().getQt().getFulfilmentType()
							.getParameterCode());
		}
		if (!this.getManagedBean().isReadOnly()) {
			if (scQt != null && scQt.getObtainSpecialApproval() != null
					&& scQt.getObtainSpecialApproval()) { //20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				this.getManagedBean().setProcMethodDropdownPresent(
						checkForProcumentMethodDropdown(scQt));
			}
		}
		
		this.getManagedBean().setMarkDeletedTechChecklist(new ArrayList<ScQtCheckList>());
		this.getManagedBean().setMarkDeletedFinChecklist(new ArrayList<ScQtCheckList>());
		Long statusId = scQt != null ? scQt.getQtStatus().getStatusId():null;//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
		
		// #25077 - #91
		if(!(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(statusId))){
			log.debug("TestmangaEstimatedAmountPrerenderView");
			if (scQt != null && null == scQt.getEstimatedAmount()) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
				SpecificationReqDTO estimatedReqDTo = new SpecificationReqDTO();
				estimatedReqDTo.setQtId(scQt.getQtId());
				IQTSpecSO specSO = getSpecSO();
				SpecificationResDTO resTO = specSO
						.calCulateTemplateSpecEstimations(estimatedReqDTo);
				if (null != resTO
						&& null != resTO.getTotalSpecEstimatedAmount()) {
					scQt.setEstimatedAmount(resTO
							.getTotalSpecEstimatedAmount());
				}
				// #31293 Farid Start Bug SIT
				if(scQt.getEstimatedAmount().compareTo(BigDecimal.ZERO) > 0){
					new CreateQtBackingBean().setProcurementMode(scQt,
							scQt.getEstimatedAmount());
				}
			// #31293 Farid End Bug SIT
			}
			
		}
		// 29521 - start
		retrieveCurrentZonalDetail();
		// 29521 - end
		checkProcurementModeValue();
		retriveTechnicalCheckList();//1873 - note 52

		// 11104-start //20230712@asyrafhaziq- SQUBE Bug NullPointer#3
		if (scQt != null && !scQt.isSampleReq()) {
			IQTSpecSO specSO = getSpecSO();
			
			SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
			specificationReqDTO.setScQt(scQt);
			specificationReqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			
			SpecificationResDTO specificationResDTO = specSO.retrieveTechnicalService(specificationReqDTO);

			List<ScQtCheckList> checklist = specificationResDTO.getTechnicalCheckList();

			if (null != checklist && !checklist.isEmpty()) {
				boolean isSample = Boolean.FALSE;
				
				for (ScQtCheckList specChecklist : checklist) {
					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(specChecklist
							.getDefinationType().getParameterCode())) {
						isSample = Boolean.TRUE;
						break;
					}
				}
				
				this.getManagedBean().getQt().setSampleReq(isSample);
			}
		}
		// 11104-End

		// #29732 @idham 25/07/2023 - Start
		if (isRoleValidationfromTaskList()) {
			this.getManagedBean().setCategoryDescriptionChange(true); // 29732
		}
		String changedCatCodeList = checkCatCodeChange();
		if (!changedCatCodeList.isEmpty()) {
			this.getManagedBean().setActiveIndex(1);
			retriveSupplierQualificationCriteria(QTConstants.SUPPLIER_CRITERIA);
			if (isRoleValidationPopUpMessagefor()
					&& (SCProcessNameConst.ENDORSE_SCORING.equals(this.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_SCORING
							.equals(this.getManagedBean().getTaskName()))) {
				this.getManagedBean().setCheckUserCanRevise(true);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_1, null,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2_A,
								new String[] { changedCatCodeList }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_1, null,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2,
								new String[] { changedCatCodeList }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));
			}
		}
		// #29732 @idham 25/07/2023 - End

		// 11104 -start-
		// 11104-Wip -start-
		boolean isSampleReq = this.getManagedBean().getQt().isSampleReq();
		boolean isSampleWIPCheck = isSampleReq
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())
				&& (isQTStatusChecklistScoring() || isQTStatusChecklistScoringRevision());

		if (isSampleWIPCheck) {
			setSampleWipCheckSpecPrepare(this.getManagedBean().getScQtCheckSt2techProposal());
		}
		// 11104-Wip -end-

		this.getManagedBean().setSampleShowHide(checkSampleRadioShowHideInSpecPrepareAndMaintenance());
		// 11104-End

		committeSO = null;
		this.getManagedBean().setLoginRoleType("SC");
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.SPECIFICICATON_COMM_PREPARATION);

	}

	// #25638 - Start.
	/**
	 * 
	 */
	private void removeReviseForRepublishQT() {
		if (null != this.getManagedBean().getOutComes() && this.getManagedBean().getOutComes().length > 0
				&& Arrays.toString(this.getManagedBean().getOutComes()).contains(BpmOutcomeConst.BPM_REVISE)) {
			
			boolean removeRevise = false;
			// #25638 - Start.
			if (null != this.getManagedBean().getQt() && null != this.getManagedBean().getQt().getScQt()) {
				IQTSuppFinalizationSO qtSuppFinalizationSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTSuppFinalizationSO.class);
				SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getQt().getScQt().getQtId());
				SuppFinalizationResDTO resDTO = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(reqDTO);
				if(null != resDTO && null != resDTO.getScQtFinalization() && null != resDTO.getScQtFinalization().getRePublishOption()){
					if (ParameterConst.WITH_OUT_SPEC_CHANGES.equalsIgnoreCase(resDTO.getScQtFinalization().getRePublishOption().getParameterCode())) {
						removeRevise = true;
					}
				}
			}
			// #25638 - End.
			if (removeRevise) {
				String tempOutComes[] = new String[this.getManagedBean().getOutComes().length -1];
				for (int i=0, k=0; i < this.getManagedBean().getOutComes().length; i++) {
					if (this.getManagedBean().getOutComes()[i].equals(BpmOutcomeConst.BPM_REVISE)) {
						continue;
					}
					tempOutComes[k++] = this.getManagedBean().getOutComes()[i];
				}
				this.getManagedBean().setOutComes(tempOutComes);
			}
		}
	}
	// #25638 - End.
	
	/**
	 * Construct spec committe dtls.prepare data for specification committee
	 * save/submit/accept/Revise.
	 * 
	 * @return SpecificationReqDTO
	 */
	private SpecificationReqDTO constructSpecCommitteDtls(String tabNo, boolean isFromTabSpecCommitteDtls) {	//Bug #35688@Syazani 10/07/2024 #29732 adding param
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getQt();
		boolean notThrowError = true;	//Bug #35688@Syazani 10/07/2024
		/*#19637-basri*/
		if(scQt.isZonal()  && scQt.isPanel()){
			this.getManagedBean().setPanelZone(true);
		}
		
		// Bug #35688@Syazani 10/07/2024 -- Start
		if (StringUtils.isNotEmpty(tabNo) && tabNo.equalsIgnoreCase("Prev" + QTConstants.TAB_SC_FINANCIAL_SCORING)) {
			notThrowError = false;
		}
		// Bug #35688@Syazani 10/07/2024 -- End

		/*end*/
		// #25726 Add Non Periodic As And When
		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& (this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN) 
								|| (this.getManagedBean()
										.getFulfilmentType()
										.equalsIgnoreCase(
												ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_AS_AND_WHEN)))) {
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
		}
		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		}

		if (null != this.getManagedBean().getFulfilmentType()
				&& !"".equalsIgnoreCase(this.getManagedBean()
						.getFulfilmentType())
				&& (this.getManagedBean()
						.getFulfilmentType()
						.equalsIgnoreCase(
								ParameterConst.FULFILMENT_TYPE_SCHEDULED)) 
								|| this.getManagedBean()
								.getFulfilmentType()
								.equalsIgnoreCase(
										ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)) {
			if (null == scQt.getScheduleType()) {
				scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
			}
		}

		if (!scQt.isPanel()) {
			String qtType = scQt.getQtType();
			if (qtType.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)
					|| qtType
							.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
				List<ScQtCategory> qtCategories = this.getManagedBean()
						.getQtCategoryList();
				scQt.setScQtCategorys(qtCategories);
				if (qtType
						.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
					List<ScQtSupplier> qtSuppliers = this.getManagedBean()
							.getQtSupplierList();
					if (qtSuppliers != null && !qtSuppliers.isEmpty()) {
						for (ScQtSupplier scQtSupplier : qtSuppliers) {
							scQtSupplier
									.setInvitationType(SourcingConstants.INVITATION_TYPE_INSERT);
							// setUserDetails(scQtSupplier);
							// qtSupplierList.add(scQtSupplier);
						}
						scQt.setScQtSuppliers(qtSuppliers);
					}
				}
			}
		}

		scQt = saveZonesBeforeTemplateSpec(scQt);
		if (null != this.getManagedBean().getCtContract()) {
			scQt.setExistingContractNo(this.getManagedBean().getCtContract()
					.getContractNo());
		}
		List<ScQtCheckList> localChecklist = new ArrayList<ScQtCheckList>();

		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();

		List<ScQtCheckList> technicalScoring = this.getManagedBean()
				.getTechnicalScoring();
		if (technicalScoring != null && !technicalScoring.isEmpty()) {

			techWeightageSum();

		}
		List<ScQtCheckList> financialScoring = this.getManagedBean()
				.getFinancialScoring();

		if (financialScoring != null && !financialScoring.isEmpty()) {
			ScQtCheckList finalFinSpec = null;
			finWeightageSum(notThrowError);	// Bug #35688@Syazani 10/07/2024

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
					// Bug #33249@Syazani 27/07/2022 -- Start
					String titleSpec = financialSpecs.getTitle() + "_"
							+ (financialSpecs.getDefinationType() != null ? financialSpecs.getDefinationType().getParameterId() : "");
					for (ScQtCheckList financialList : financialCheckList) {
						String titleList = financialList.getTitle() + "_"
								+ (financialList.getDefinationType() != null ? financialList.getDefinationType().getParameterId() : "");
						if (titleList.equals(titleSpec)
								&& ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(financialSpecs.getDefinationType()
										.getParameterCode())) {
							// Bug #33249@Syazani 27/07/2022 -- End
							if (null != financialSpecs.getQtScoreWeightage()
									&& financialSpecs.getQtScoreWeightage().getScore() != null //#25138
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

		}

		if (techCheckList != null && !techCheckList.isEmpty()) {
			boolean sample = false;
			boolean demo = false;//#28784 WIP Idham 30/10/2023
			for (ScQtCheckList checkList : techCheckList) {
			
				//Bug #33358 start
				ScQtScoreWeightage weight = checkList.getQtScoreWeightage();
				if (null != weight && !checkList.isScoringReq()){
					weight.setWeightage(new BigDecimal(0));
					checkList.setQtScoreWeightage(weight);
				}
				//Bug #33358 end
				
				if (QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(checkList
						.getDefinationType().getParameterCode())) {
					if (null != scQt.getEvaluationType()
							&& ParameterConst.EVALUATION_TYPES_TWO_TIER
									.equalsIgnoreCase(scQt.getEvaluationType())) {
						demo = true;//#28784 WIP Idham 30/10/2023
						checkList.setScoringReq(true);
					}

				} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())) {
					sample = true;
					checkList.setScoringReq(true);
					scQt.setSampleReq(sample);//#11104

				}
				/*
				 * if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
				 * .equalsIgnoreCase(checkList.getDefinationType()
				 * .getParameterCode()) &&
				 * !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
				 * .equalsIgnoreCase(checkList.getDefinationType()
				 * .getParameterCode())) { if (!checkList.isScoringReq()) {
				 * StatusReqDTO statusreq = new StatusReqDTO();
				 * statusreq.setStatusId(QTStatusConst.TS_COMPLETED);
				 * StatusResDTO statusres = statussO
				 * .retrieveStatusByStatusId(statusreq);
				 * 
				 * checkList.setPmEvalStatus(statusres.getPmStatus()); }
				 * 
				 * }
				 */
			}
			//#28784 WIP Idham 30/10/2023 - Start
			if (!this.getManagedBean().isNewVersion()) {
				scQt.setDemoRequired(demo);
			}
			//#28784 WIP Idham 30/10/2023 - End
			//scQt.setSampleReq(sample);//#11104
			localChecklist.addAll(techCheckList);
			scQt.setScQtChecklists(localChecklist);
		}

		if (CollectionUtils.isNotEmpty(financialCheckList)) {
		
			//Bug #33358 start
			for (ScQtCheckList checkList : financialCheckList) {
				ScQtScoreWeightage weight = checkList.getQtScoreWeightage();
				if (null != weight && !checkList.isScoringReq()){
					weight.setWeightage(new BigDecimal(0));
					checkList.setQtScoreWeightage(weight);
				}
			}
			//Bug #33358 end
			
			/*
			 * for (ScQtCheckList financialcheckList : financialCheckList) { if
			 * (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
			 * .equalsIgnoreCase(financialcheckList
			 * .getDefinationType().getParameterCode()) &&
			 * !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
			 * .equalsIgnoreCase(financialcheckList
			 * .getDefinationType().getParameterCode())) { if
			 * (!financialcheckList.isScoringReq()) { StatusReqDTO statusreq =
			 * new StatusReqDTO();
			 * statusreq.setStatusId(QTStatusConst.TS_COMPLETED); StatusResDTO
			 * statusres = statussO .retrieveStatusByStatusId(statusreq);
			 * 
			 * financialcheckList.setPmEvalStatus(statusres .getPmStatus()); }
			 * 
			 * }
			 * 
			 * }
			 */
			localChecklist.addAll(financialCheckList);
			scQt.setScQtChecklists(localChecklist);
		}
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtSchedule> freqschedule = this.getManagedBean()
					.getScheduleList();
			List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
			if (freqschedule != null && !freqschedule.isEmpty()) {

				for (ScQtSchedule schedule : freqschedule) {
					String Startmonth = "";
					String endmonth = "";
					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}

					// Date currentDate = new Date();
					SimpleDateFormat df = new SimpleDateFormat(
							CommonConstants.DEFAULT_DATE_FORMAT);
					// String localDate = df.format(currentDate);
					// String day = localDate.substring(0, 2);
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();

					schedule.setScheduleNo(schedule.getScheduleNo());

					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
					localScheduleList.add(schedule);

				}
				scQt.setScQtSchedules(localScheduleList);
			} else {
				scQt.setScQtSchedules(null);
			}
		}

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtProgressivePayment> progressivePayemnt = this
					.getManagedBean().getProgressivePaymentList();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
			if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
				String Startmonth = "";
				String endmonth = "";
				for (ScQtProgressivePayment schedule : progressivePayemnt) {

					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}
					// Date d1 = new Date();
					// SimpleDateFormat format = new
					// SimpleDateFormat("dd/MM/yyyy");
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					// String localDate = format.format(d1);
					// String date = localDate.substring(0, 2);
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();
					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
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
			/*
			 * #28784,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(technicalScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newTechnicalScoreSetting = checkScoreSetting(reqTempDTO);
			if(newTechnicalScoreSetting != null && newTechnicalScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newTechnicalScoreSetting.setWeightageReq(technicalScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newTechnicalScoreSetting.setPassingScore(technicalScoreSetting.getPassingScore());
				newTechnicalScoreSetting.setOverallTechnical(technicalScoreSetting.getOverallTechnical());
				newTechnicalScoreSetting.setOverallOtherTechnical(technicalScoreSetting.getOverallOtherTechnical());
				newTechnicalScoreSetting.setOverallFin(technicalScoreSetting.getOverallFin());
				newTechnicalScoreSetting.setOverallOtherFin(technicalScoreSetting.getOverallOtherFin());
				technicalScoreSetting = newTechnicalScoreSetting;
			}//#28784 - end
			scoreSetting.add(technicalScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
// #21695 farid, if partial set weightage to false		
			if (this.getManagedBean().getQt().isPartial() == true){
				this.getManagedBean().getTechnicalScoreSetting().setWeightageReq(false);
			}
		}
		ScQtScoreSetting financialScoreSetting = this.getManagedBean()
				.getFinancialScoreSetting();
		if (null != financialScoreSetting
				&& financialScoreSetting.getSettingType() == 2) {
			/*
			 * #28784,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(financialScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newFinancialScoreSetting = checkScoreSetting(reqTempDTO);
			if(newFinancialScoreSetting != null &&  newFinancialScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newFinancialScoreSetting.setWeightageReq(financialScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newFinancialScoreSetting.setPassingScore(financialScoreSetting.getPassingScore());
				newFinancialScoreSetting.setOverallTechnical(financialScoreSetting.getOverallTechnical());
				newFinancialScoreSetting.setOverallOtherTechnical(financialScoreSetting.getOverallOtherTechnical());
				newFinancialScoreSetting.setOverallFin(financialScoreSetting.getOverallFin());
				newFinancialScoreSetting.setOverallOtherFin(financialScoreSetting.getOverallOtherFin());
				financialScoreSetting = newFinancialScoreSetting;
			}//#28784 - end
			scoreSetting.add(financialScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		ScQtScoreSetting demoScoreSetting = this.getManagedBean()
				.getDemoScoreSetting();
		if (null != demoScoreSetting && demoScoreSetting.getSettingType() == 3) {
			//#26440 Start
			if (null != scQt.getEvaluationType()
					&& ParameterConst.EVALUATION_TYPES_TWO_TIER
							.equalsIgnoreCase(scQt.getEvaluationType())) {
				/*
				 * #28784,  Checking if score setting already created or not
				 * Start
				 */
				SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
				List<Integer> settinglist = new ArrayList<>();
				reqTempDTO.setScQt(scQt);
				settinglist.add(demoScoreSetting.getSettingType());
				reqTempDTO.setSettingTypes(settinglist);
				ScQtScoreSetting newDemoScoreSetting = checkScoreSetting(reqTempDTO);
				if(newDemoScoreSetting != null && newDemoScoreSetting.getQtScoreSettingId() != null
						&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
					newDemoScoreSetting.setWeightageReq(demoScoreSetting.isWeightageReq());//Bug #34744 under 28784
					newDemoScoreSetting.setPassingScore(demoScoreSetting.getPassingScore());
					newDemoScoreSetting.setOverallTechnical(demoScoreSetting.getOverallTechnical());
					newDemoScoreSetting.setOverallOtherTechnical(demoScoreSetting.getOverallOtherTechnical());
					newDemoScoreSetting.setOverallFin(demoScoreSetting.getOverallFin());
					newDemoScoreSetting.setOverallOtherFin(demoScoreSetting.getOverallOtherFin());
					demoScoreSetting = newDemoScoreSetting;
				}//#28784 - end
				
				if(null!=technicalScoreSetting){
			demoScoreSetting.setWeightageReq(technicalScoreSetting.isWeightageReq()); 
				}
			}else{
				demoScoreSetting.setWeightageReq(Boolean.FALSE); 
			}
			//#26440 End
			scoreSetting.add(demoScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);

		}
		ScQtScoreSetting financialItemScoreSetting = this.getManagedBean()
				.getFinacialItemScoreSetting();
		if (null != financialItemScoreSetting
				&& financialItemScoreSetting.getSettingType() == 6) {
			
			/*
			 * #28784,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(financialItemScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newFinancialItemScoreSetting = checkScoreSetting(reqTempDTO);
			if(newFinancialItemScoreSetting != null && newFinancialItemScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newFinancialItemScoreSetting.setWeightageReq(financialItemScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newFinancialItemScoreSetting.setPassingScore(financialItemScoreSetting.getPassingScore());
				newFinancialItemScoreSetting.setOverallTechnical(financialItemScoreSetting.getOverallTechnical());
				newFinancialItemScoreSetting.setOverallOtherTechnical(financialItemScoreSetting.getOverallOtherTechnical());
				newFinancialItemScoreSetting.setOverallFin(financialItemScoreSetting.getOverallFin());
				newFinancialItemScoreSetting.setOverallOtherFin(financialItemScoreSetting.getOverallOtherFin());
				financialItemScoreSetting = newFinancialItemScoreSetting;
			}//#28784 - end
			
			scoreSetting.add(financialItemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
// #21695 farid, if partial set weightage to false			
			if (this.getManagedBean().getQt().isPartial() == true){
				this.getManagedBean().getFinancialScoreSetting().setWeightageReq(false);
			}
		}
		ScQtScoreSetting packageScoreSetting = this.getManagedBean()
				.getRecommendationPackageScore();
		if (null != packageScoreSetting
				&& packageScoreSetting.getSettingType() == 4) {
			
			/*
			 * #28784,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(packageScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newPackageScoreSetting = checkScoreSetting(reqTempDTO);
			if(newPackageScoreSetting != null && newPackageScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newPackageScoreSetting.setWeightageReq(packageScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newPackageScoreSetting.setPassingScore(packageScoreSetting.getPassingScore());
				newPackageScoreSetting.setOverallTechnical(packageScoreSetting.getOverallTechnical());
				newPackageScoreSetting.setOverallOtherTechnical(packageScoreSetting.getOverallOtherTechnical());
				newPackageScoreSetting.setOverallFin(packageScoreSetting.getOverallFin());
				newPackageScoreSetting.setOverallOtherFin(packageScoreSetting.getOverallOtherFin());
				packageScoreSetting = newPackageScoreSetting;
			}//#28784 - end

			scoreSetting.add(packageScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}

		ScQtScoreSetting itemScoreSetting = this.getManagedBean()
				.getRecommendationScore();
		if (null != itemScoreSetting && itemScoreSetting.getSettingType() == 5) {
			
			/*
			 * #28785,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(itemScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newItemScoreSetting = checkScoreSetting(reqTempDTO);
			if(newItemScoreSetting != null && newItemScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newItemScoreSetting.setWeightageReq(itemScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newItemScoreSetting.setPassingScore(itemScoreSetting.getPassingScore());
				newItemScoreSetting.setOverallTechnical(itemScoreSetting.getOverallTechnical());
				newItemScoreSetting.setOverallOtherTechnical(itemScoreSetting.getOverallOtherTechnical());
				newItemScoreSetting.setOverallFin(itemScoreSetting.getOverallFin());
				newItemScoreSetting.setOverallOtherFin(itemScoreSetting.getOverallOtherFin());
				itemScoreSetting = newItemScoreSetting;
			}//#28784 - end

			scoreSetting.add(itemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		// #21695 START FARID
		ScQtScoreSetting technicalItemScoreSetting = this.getManagedBean()
				.getTechnicalItemScoreSetting();
		if (null != technicalItemScoreSetting
				&& technicalItemScoreSetting.getSettingType() == 7) {
			
			/*
			 * #28784,  Checking if score setting already created or not
			 * Start
			 */
			SpecificationReqDTO reqTempDTO = new SpecificationReqDTO();
			List<Integer> settinglist = new ArrayList<>();
			reqTempDTO.setScQt(scQt);
			settinglist.add(technicalItemScoreSetting.getSettingType());
			reqTempDTO.setSettingTypes(settinglist);
			ScQtScoreSetting newTechnicalItemScoreSetting = checkScoreSetting(reqTempDTO);
			if(newTechnicalItemScoreSetting != null && newTechnicalItemScoreSetting.getQtScoreSettingId() != null
					&& !this.getManagedBean().getQt().isCloned()){//#28784 cloning error
				newTechnicalItemScoreSetting.setWeightageReq(technicalItemScoreSetting.isWeightageReq());//Bug #34744 under 28784
				newTechnicalItemScoreSetting.setPassingScore(technicalItemScoreSetting.getPassingScore());
				newTechnicalItemScoreSetting.setOverallTechnical(technicalItemScoreSetting.getOverallTechnical());
				newTechnicalItemScoreSetting.setOverallOtherTechnical(technicalItemScoreSetting.getOverallOtherTechnical());
				newTechnicalItemScoreSetting.setOverallFin(technicalItemScoreSetting.getOverallFin());
				newTechnicalItemScoreSetting.setOverallOtherFin(technicalItemScoreSetting.getOverallOtherFin());
				technicalItemScoreSetting = newTechnicalItemScoreSetting;
			}//#28784 - end
			scoreSetting.add(technicalItemScoreSetting);
			scQt.setScQtScoreSettings(scoreSetting);
		}
		// #21695 END FARID
		List<ScQtDeliveryLoc> localDeliveryList = new ArrayList<ScQtDeliveryLoc>();
		// List<BriefingZoneBean> deliveryList=
		// this.getManagedBean().getDeliveryLocBean();
		if (scQt.isZonal()) {

			List<BriefingZoneBean> deliveryList = this.getManagedBean()
					.getAddressZoneBean();

			if (CollectionUtils.isNotEmpty(deliveryList)) {

				for (BriefingZoneBean briefingZoneBean : deliveryList) {
					if (briefingZoneBean.getDeliveryLocList() != null
							&& !briefingZoneBean.getDeliveryLocList().isEmpty()) {

						localDeliveryList.addAll(briefingZoneBean
								.getDeliveryLocList());

					}

				}
				scQt.setScQtDeliveryLocs(localDeliveryList);
			} else if (CollectionUtils.isNotEmpty(deliveryList)) {

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
			if (CollectionUtils.isNotEmpty(nonZoneList)) {
				scQt.setScQtDeliveryLocs(nonZoneList);
			} else {
				scQt.setScQtDeliveryLocs(null);
			}
		}
		//#29732 suspected below code is use for save/update. When click Specification Endorsement Status 2 times, 
		//it will remove the textarea field & it also allow user to sumbit revise without fill in. 
		if(!isFromTabSpecCommitteDtls){
		List<ScQtSpecificationRemark> remarks = super.finalizeRemarks(this
				.getManagedBean().getSpecificationRemarks());
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						remarks.remove(i);
					}
				}
				scQt.setScQtSpecificationRemarks(remarks);
			}
		}
		
		// #29474 - start Setting the Deductions to specification.
		List<ScQtDeduction> deductionList = this.getManagedBean()
				.getScQtDeductionList();
		if (null != deductionList && !deductionList.isEmpty()) {
			for (ScQtDeduction deduction : deductionList) {
				deduction.setScQt(this.getManagedBean().getQt());
			}
			scQt.setScQtDeductions(deductionList);
		}
		// #29474 - end

		if (null != localChecklist && localChecklist.size() > 0) {

		} else {
			scQt.setScQtChecklists(null);
		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		if (!scQt.isBsvRequired()) {
			scQt.setScQtBriefings(null);
		}
		localChecklist = null;
		localDeliveryList = null;
		scoreSetting = null;
		reqDTO.setScQt(scQt);
		/*
		 * List<ScQtCommitteeActivity> committeActivities =
		 * this.getManagedBean() .getCommitteeActivities(); if (null !=
		 * committeActivities) { PmUser pmUser = new
		 * FacesScopes().getUserSessionBean(); for (int i = 0; i <
		 * committeActivities.size(); i++) { if
		 * (pmUser.getIdentificationNo().equals(
		 * committeActivities.get(i).getScCommitteeMember()
		 * .getPmUser().getIdentificationNo())) {
		 * committeActivities.get(i).setSignedOff(true); break; }
		 * 
		 * } } reqDTO.setCommitteActivities(committeActivities);
		 */

		return reqDTO;
	}
	//Reopen #25726 note 226 - issue 14
	//function to check and update fulfilment type in specQuestion.
	private void checkAndupdateFulfilmentType(List<ScQtCheckList> allCheckLists,ScQtCheckList finChecklist, boolean isTechInsert, boolean isSample){//11104-778&25726 ){
		log.info("checkAndupdateFulfilmentType");	
		String financialDoccategory=	finChecklist.getDocCategory();//11104-778&25726		
		log.info("===mainChecklistSaveDocType========="+financialDoccategory);
		PmParameter clFulfilmentTypeCode =this.getManagedBean().getQt().getFulfilmentType();
		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		
		if (null == technicalCheckList
				|| technicalCheckList.isEmpty()) {
			retriveTechnicalCheckList();
			technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
		}
		
		if (null != technicalCheckList
				&& !technicalCheckList.isEmpty() && !isTechInsert) {
			
			for (ScQtCheckList technicalchecklist : technicalCheckList) {
				String checklistTitle = (technicalchecklist.getTitle() + "_" + (technicalchecklist.getDefinationType() != null ? technicalchecklist.getDefinationType()
						.getParameterId() : "")).trim().replaceAll("\\s", "");
				String finChecklistTitle = (finChecklist.getTitle() + "_" + (finChecklist.getDefinationType() != null ? finChecklist
							.getDefinationType().getParameterId() : "")).trim().replaceAll("\\s", "");
					
					if (finChecklistTitle.equals(checklistTitle)) {	
						allCheckLists.add(technicalchecklist);
						break;
					}
			}
		}
		
		
		if (null != allCheckLists
				&& !allCheckLists.isEmpty()) {
			
			for (ScQtCheckList checklist : allCheckLists) {
				String technicalDoccategory=	checklist.getDocCategory();//11104-778&25726
				log.info("===subChecklistSaveDocType========="+technicalDoccategory);
				log.info("===subChecklistisSample========="+isSample);
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(checklist.getDefinationType()
								.getParameterCode()) &&
								checklist.getScQtDoc() != null) {
					boolean isDatafetch=SourcingConstants.TECHNICAL_DOCTYPE.equals(technicalDoccategory) &&  SourcingConstants.FINANCIAL_DOCTYPE.equals(financialDoccategory) && isSample;//11104-778&25726
					log.info("===subChecklistisDatafetch========="+isDatafetch);
					if(isDatafetch){
						log.info("===subChecklistBeforeFetchQuestionSize========="+checklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions().size());
						this.setTemplateSpecMaster(checklist);
						log.info("===subChecklistAfterFetchQuestionSize========="+checklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions().size());
					}//11104-778&25726
					TplSpecMaster specSpecMaster = checklist.getScQtDoc().getTplSpecMaster();
					
					if(CollectionUtils.isNotEmpty(specSpecMaster.getTplSpecQuestions())){
						for (TplSpecQuestion specQuestion : specSpecMaster.getTplSpecQuestions()) {
							if (TemplateSpecConst.QUESTION_TYPE_ITEM
									.equalsIgnoreCase(specQuestion.getQuestionType())) {
								String questionFulfilmentcode =  specQuestion.getClFulfilmentTypeCode().getParameterCode();
								if (!questionFulfilmentcode.equals(clFulfilmentTypeCode.getParameterCode())){
									specQuestion.setClFulfilmentTypeCode(clFulfilmentTypeCode);
								}
							}							
						 }
					}	
				}				
			}
		}
	}

	/**
	 * Gets the to be remove list.remove technical and financial checklists.
	 * 
	 * @return List<ScQtCheckList>
	 */
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
	
	private List<ScQtCheckList> deleteSpecCheckList(){
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		List<ScQtCheckList> specCheckList=new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> nonspecCheckList=new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> toBeRemoveCheckLists=getToBeRemoveList();
		
		for(ScQtCheckList cl:toBeRemoveCheckLists){
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equalsIgnoreCase(cl.getDefinationType().getParameterCode())) {
				specCheckList.add(cl);
			}else{
				nonspecCheckList.add(cl);
			}
		}
		
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		specReqDTO.setScQt(this.getManagedBean().getQt());
		specReqDTO.setCheckLists(specCheckList);
		//SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);
		specSo.deleteChecklists(specReqDTO);
//		if (resDTO.isSuccessful()) {
//			this.getManagedBean().setMarkDeletedTechChecklist(new ArrayList<ScQtCheckList>());
//			this.getManagedBean().setMarkDeletedFinChecklist(new ArrayList<ScQtCheckList>());
//		}
		
		return nonspecCheckList;
	}

	/**
	 * Validate prepare qt checklist.user clicks side menus in specification
	 * committee it will validate the side menus data.
	 */
	private void validatePrepareQtChecklist() {
		System.out.println("inside prepare...");
		try {
			System.out.println("before prepare...");
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSpecSO.class);
			int activeIndex = this.getManagedBean().getActiveIndex();
			SCQuotationTenderDataType quotationTenderDataType = (SCQuotationTenderDataType) this
					.getManagedBean().getPayLoad();
			SpecificationResDTO resDTO = null;
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			Long statusId = this.getManagedBean().getCurrentStatusId();
			String taskName = this.getManagedBean().getTaskName();
			boolean readonly = this.getManagedBean().isReadOnly();
			String panelItemCategory = this.getManagedBean()
					.getPanelItemCategory();
			boolean specNewable = this.getManagedBean().isSpecNewable();
			boolean updateSpec = this.getManagedBean().isUpdateSpec();
			String loginRoleType = this.getManagedBean().getLoginRoleType();
			boolean isPanel = this.getManagedBean().getPanel();
			boolean isDropdownPresent = this.getManagedBean()
					.isProcMethodDropdownPresent();
			ScWorkFlowStatus scWorkFlowStatus = this.getManagedBean()
					.getSpecMaintenanceWorkFlowStatus();

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

			//List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
			List<ScQtCheckList> checklistRemoveList = deleteSpecCheckList();
			if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
				for (int i = 0; i < checklistRemoveList.size(); i++) {
					if (null == checklistRemoveList.get(i).getQtChecklistId()) {
						checklistRemoveList.remove(i);
					}
				}

			}
			
			//start #25635 danial
			List<ScQtScoreSetting> toBeRemoveScoreSettings=new ArrayList<ScQtScoreSetting>();
			if(null!=this.getManagedBean().getMarkDeletedScQtScoreSettingTechnical() && 
					!this.getManagedBean().getMarkDeletedScQtScoreSettingTechnical().isEmpty()){
				toBeRemoveScoreSettings=this.getManagedBean().getMarkDeletedScQtScoreSettingTechnical();
			}
			if(null!=this.getManagedBean().getMarkDeletedScQtScoreSettingFinancial() && 
					!this.getManagedBean().getMarkDeletedScQtScoreSettingFinancial().isEmpty()){
				toBeRemoveScoreSettings.addAll(this.getManagedBean().getMarkDeletedScQtScoreSettingFinancial());
			}
			if(null!=this.getManagedBean().getMarkDeletedScQtScoreSettingOverall() && 
					!this.getManagedBean().getMarkDeletedScQtScoreSettingOverall().isEmpty()){
				toBeRemoveScoreSettings.addAll(this.getManagedBean().getMarkDeletedScQtScoreSettingOverall());
			}
			
			specReqDTO.setToBeRemoveScoreSettingList(toBeRemoveScoreSettings);
			//#25635 danial end
			
			//#29474 - start			
			List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
					.getRemoveDeductions();
			if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
				for (int i = 0; i < deductionRemoveList.size(); i++) {
					if (null == deductionRemoveList.get(i).getQtDeductionId()) {
						deductionRemoveList.remove(i);
					}
				}
			}
			specReqDTO.setRemoveDeductions(deductionRemoveList);//#29474 - end
			specReqDTO.setTobeRemoveList(checklistRemoveList);
			specReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);
			specReqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
					.getToBeRemoveSchedules());
			specReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			specReqDTO.setTobeRemoveCategoryCodes(this.getManagedBean()
					.getTobeRemoveCategoryCodes());
			specReqDTO.setToBeRemoveRestrictedSupplierList(this
					.getManagedBean().getToBeRemoveRestrictedSupplierList());
			specReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			boolean isAssignedState = this.getManagedBean().isAssignedState();
			boolean qtMaintenanceFlow = this.getManagedBean().isMaintanceFlow();
			String specScreenMaintenance = this.getManagedBean()
					.getSpecMaintenanceScreen();
			boolean specMaintenance = this.getManagedBean().isSpecMaintenance();
			
			// 29521 - start
			specReqDTO.setBackupScQt(retrieveScqtdetails());
			specReqDTO.setListBackupCategoryCode(retrieveCurrentScQtCategoryCode());
			specReqDTO.setListNewCategoryCode(getManagedBean().getQtCategoryList());
			FacesScopes fs = new FacesScopes();
			specReqDTO.setPmUser(fs.getUserSessionBean());
			validateZonalChanges();
			// 29521 - end
			
			boolean sampleWipShowHide = this.getManagedBean().isSampleShowHide(); // 11104Wip
			
			switch (activeIndex) {
			case 0:
				ScQt scQt = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				if (this.getManagedBean().isReadOnly()) {
					if ((null != scQt.getFulfilmentType() && null == scQt
							.getFulfilmentType().getParameterId())
							|| (null == scQt.getContractDuration())) {
						throw new EpException(MessageConst.ERROR_760134);

					}
				}
				specReqDTO.setScQt(scQt);
				specReqDTO.setZonesDeleteOrSave(this.getManagedBean()
						.isZoneSpecUpdate());
				specSO.validateSpecCommitteGeneralInfo(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				getfulfilmentDtls(resDTO.getScQt());
				retriveTechnicalCheckList();//28784 - related issue 35721
				break;
			case 1:
				ScQt scQtSupplier = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param

				specReqDTO.setScQt(scQtSupplier);
				// specSO.validateSupplierQulificationInfo(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveSupplierQualificationCriteria("SC");

				break;
			case 2:

				ScQt scQtTechChecklist = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				specReqDTO.setScQt(scQtTechChecklist);
				specSO.validateCheckList(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);

				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveTechnicalCheckList();
				break;
			case 3:
				ScQt finChecklist = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				specReqDTO.setScQt(finChecklist);
				specSO.validateCheckList(specReqDTO);
				// specReqDTO.setTobeRemoveList(getToBeRemoveList());

				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveFinancialCheckList();
				break;
			case 4:
				ScQt scoring = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param

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
//#21695 farid Start			
				ScQtScoreSetting technicalItemScoreSetting = this.getManagedBean()
						.getTechnicalItemScoreSetting();
				if (technicalItemScoreSetting != null) {
					specReqDTO.setScoreSetting(technicalItemScoreSetting);
				}
//#21695 End
				specReqDTO.setCheckLists(technicalScoring);
				specReqDTO
						.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
				specReqDTO.setScQt(scoring);
				specSO.validateSCScoreSettings(specReqDTO);
				validateTechnicalPassingScoring(this.getManagedBean()
						.getTechnicalScoreSetting(), this
						.getManagedBean().getTechnicalItemScoreSetting());
				// #21695 START FARID
				validateTechnicalDemoPassingScoring(this.getManagedBean()
						.getDemoScoreSetting());
				validateTechnicalUserScoringAttachment(this.getManagedBean()
						.isTechnicalUserScoringSchema());
				technicalWeightageCalculate();
				technicalItemScoreCalculate(this.getManagedBean()
						.getTechnicalItemScoreSetting());
	//#21695 farid start 12/8/2020
				checkTechnicalItemPassingScore(this.getManagedBean()
						.getTechnicalItemScoreSetting());
				// #21695 START FARID
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveTechnicalScoring();
				break;

			case 5:
				ScQt recommendation = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param

				List<ScQtCheckList> financialScoring1 = this.getManagedBean()
						.getFinancialScoring();
				ScQtScoreSetting financialScoreSetting = this.getManagedBean()
						.getFinancialScoreSetting();

				if (null != financialScoreSetting
						&& financialScoreSetting.getSettingType() == 2) {
					specReqDTO.setScoreSetting(financialScoreSetting);
				}

				specReqDTO.setCheckLists(financialScoring1);
				specReqDTO
						.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
				specReqDTO.setScQt(recommendation);
				//#25138 Reendorsed - start 
				SpecificationReqDTO specReqDto=new SpecificationReqDTO();//34852_start
				specReqDto.setScQt(this.getManagedBean().getQt());
				this.getManagedBean().setWipStatusMax(specSO.checkWipScoreSettingMax(specReqDto));//34852_End
				if(this.getManagedBean().getWipStatusMax()<SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM){//34852_Start	
					this.getManagedBean().setOfferScoreType(ParameterConst.FINANCE_SCORE_TYPE_AUTO);
					scoreTypeN=ParameterConst.FINANCE_SCORE_TYPE_AUTO;
				}//34852_End
				specReqDTO.setOfferScoreType(this.getManagedBean().getOfferScoreType());
				specReqDTO.setScoreOfferPriceList(this.getManagedBean().getOfferPricePackageList());
				specReqDTO.setSingleItem(this.getManagedBean().isSingleItem());
				//#25138 Reendorsed - end
				if(this.getManagedBean().getWipStatusMax()>=SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM){//34852_start
				specReqDTO.setZonesQt(this.getManagedBean().getQt().isZonal());//#25138 - update validation
				}//34852_End
				specReqDTO.setAllowZeroVariance(true);
				validateFinancialPackageItemPassingScoring(this
						.getManagedBean().getFinancialScoreSetting(), this
						.getManagedBean().getFinacialItemScoreSetting());
				specSO.validateSCScoreSettings(specReqDTO);
				specSO.validateSpecOfferPrice(specReqDTO);
				//#25138 Reendorsed - set max score for wip data - start
//				if(this.getManagedBean().getQt().isZonal()){
//					if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
//							.equalsIgnoreCase(financialScoring1.get(0).getDefinationType()
//									.getParameterCode())){
//						checkSpecZoneQtyMaxScore(financialScoring1.get(0));	
//
//					}
//						
//				}
				//#25138 Reendorsed - set max score for wip data - end
				financialWeightageCalculate();
				financialItemScoreCalculate(this.getManagedBean()
						.getFinacialItemScoreSetting());
				validateFinancialUserScoringAttachment(this.getManagedBean()
						.isFinancialUserScoringSchema());
				//#25138 Reendorsed- start 
				if(!this.getManagedBean().getQt().isZonal()){

					retriveFinancialScoring(null);// Bug #35688@Syazani 10/07/2024 // Enhancement(ahmaa) #25138
				}
				//#25138 Reendorsed- end
				resDTO = specSO.saveQTDtl(specReqDTO);
				// #31944 - Start.
				Long currentStatusId = getManagedBean().getCurrentStatusId();
				// #31944 - End.
				renewSesionObject();
				// #31944 - Start.
				getManagedBean().setCurrentStatusId(currentStatusId);
				// #31944 - End.
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveFinancialScoring(null);	// Bug #35688@Syazani 10/07/2024
				break;

			case 6:
				ScQt delivery = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
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
				if (!this.getManagedBean().isReadOnly()) {
					calculateRecommendationPackageAndItemScores(
							packageScoreSetting, itemScoreSetting);
					specSO.validateSCRecommandationSettings(specReqDTO);

				}

				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveRecommendationDetails();
				scoreSetting = null;
				break;

			case 7:
				ScQt fulfilMent = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
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
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				retriveDeliveryDetails();
				break;

			//#29474 - start
			case 8:
				ScQt paymentDeductions = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				specReqDTO.setScQt(paymentDeductions);
				specSO.validateDeductionDtls(specReqDTO);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				this.getManagedBean().setReadOnly(readonly);
				this.getManagedBean().setCurrentStatusId(statusId);
				retrieveDeductionPaymentTabDtl();
				break;

			case 9:
				ScQt fulfilMentdtls = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED
						.equalsIgnoreCase(this.getManagedBean()
								.getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType()) || ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED
										.equalsIgnoreCase(this.getManagedBean()
												.getFulfilmentType())) {//25635&33723&25725
					specReqDTO.setScQt(fulfilMentdtls);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setQt(resDTO.getScQt());
					//#25022	
					//getManagedBean().setScheduleList(getManagedBean().getQt().getScQtSchedules());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId());
					retriveFulfilmentDtls();
					// retriveSupplierQualificationCriteria("SC");
					// retriveCommitteeEndorsment();
					// retriveTechnicalCheckList();
				} else {
					specReqDTO.setScQt(fulfilMentdtls);
					specSO.validateSCFulfillmentSchedules(specReqDTO);
					resDTO = specSO.saveQTDtl(specReqDTO);
					renewSesionObject();
					this.getManagedBean().setQt(resDTO.getScQt());
					getfulfilmentDtls(resDTO.getScQt());
					retriveSpecdetails(resDTO.getScQt().getQtId());
					// retriveFulfilmentDtls();
					retriveCommitteeEndorsment();
				}
				break;

			case 10://#29474 - end

				ScQt endorsment = constructSpecCommitteDtls(null,false).getScQt();	//Bug #35688@Syazani 10/07/2024 #29732 adding param
				specReqDTO.setScQt(endorsment);
				resDTO = specSO.saveQTDtl(specReqDTO);
				renewSesionObject();
				this.getManagedBean().setQt(resDTO.getScQt());
				getfulfilmentDtls(resDTO.getScQt());
				retriveSpecdetails(resDTO.getScQt().getQtId());
				this.getManagedBean().setReadOnly(readonly);
				this.getManagedBean().setCurrentStatusId(statusId);
				retriveCommitteeEndorsment();
				// retriveTechnicalCheckList();
				break;

			}

			if (getManagedBean().getTempIndex() < activeIndex) {
				getManagedBean().setTempIndex(activeIndex);
			}
			this.getManagedBean().setPanelItemCategory(panelItemCategory);
			this.getManagedBean().setSpecNewable(specNewable);
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setActiveIndex(activeIndex);
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setCurrentStatusId(statusId);
			this.getManagedBean().setOutComes(outComes);
			this.getManagedBean().setReadOnly(readonly);
			this.getManagedBean().setPayLoad(quotationTenderDataType);
			this.getManagedBean().setAssignedState(isAssignedState);
			this.getManagedBean().setLoginRoleType(loginRoleType);
			this.getManagedBean().setMaintanceFlow(qtMaintenanceFlow);
			this.getManagedBean().setSpecMaintenanceScreen(
					specScreenMaintenance);
			this.getManagedBean().setSpecMaintenance(specMaintenance);
			this.getManagedBean().setUpdateSpec(updateSpec);
			if (null != this.getManagedBean().getCtContract()) {
				this.getManagedBean()
						.getCtContract()
						.setContractNo(
								this.getManagedBean().getQt()
										.getExistingContractNo());
			}
			this.getManagedBean().setPtjSpecialApproval(
					this.getManagedBean().getQt().getPtjSpecialApproval());
			this.getManagedBean().setPanel(isPanel);
			this.getManagedBean().setProcMethodDropdownPresent(
					isDropdownPresent);
			this.getManagedBean().setSpecMaintenanceWorkFlowStatus(
					scWorkFlowStatus);
			System.out.println("after prepare...");
			
			//#28784 -update
			Long status = this.getManagedBean().getQt().getQtStatus().getStatusId();
			if (status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) && this.getManagedBean().getQt().
					getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
				this.getManagedBean().setDemoType(true);
			}

			this.getManagedBean().setSampleShowHide(sampleWipShowHide); // 11104Wip
		} /*
		 * catch (Exception e) { e.printStackTrace(); }
		 */

		finally {
			System.out.println("final prepare...");
		}
	}

	/**
	 * Spec committe bpm task.Event handle Submit/Revise/Accept in specification
	 * committee.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public void specCommitteBpmTask(String action) {
// #29732 Start - reOpen Fix
		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action) 
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) && !isRoleValidationfromTaskListAllowRevise()) {

			if (null == this.getManagedBean().getQtCategoryList() || this.getManagedBean().getQtCategoryList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_760062);
			}
			SpecificationReqDTO specReqDTOCatChange = new SpecificationReqDTO();
			specReqDTOCatChange.setScQt(this.getManagedBean().getQt());
			this.getManagedBean().setFromSubmitCatChange(true);
			String changedCatCodeList = checkCatCodeChange();
			if (!changedCatCodeList.isEmpty()) {
				this.getManagedBean().setCatCodeList(changedCatCodeList);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
		}
// #29732 End - reOpen Fix
		
//		#36678 related to #35061 start
		BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();
		BigDecimal intendedPrice = this.getManagedBean().getQt().getEstimatedAmount();

		if (!this.getManagedBean().getQt().isPanel() && null != intendedPrice && intendedPrice.compareTo(indPriceThreshold) <= 0 
				&& (this.getManagedBean().getQt().getJustificationProcValue() == null || this.getManagedBean().getQt().getJustificationProcValue().isEmpty())) {
				throw new EpException(MessageConst.ERROR_760560);
		}
//		#36678 related to #35061 end
		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		if (member != null
				&& member
						.getMemberRole()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			this.getManagedBean().setLocalOutCome(action);

			//25638 Revise at SC,EC,OC,TEC,FEC,FC - Start
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();   //Added the line 25638 Enhancement 
				List<ScQtSpecificationRemark> remarks = this.getManagedBean().getSpecificationRemarks();
				
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				
				RequestContext.getCurrentInstance().execute("reviseSCDlgs.show()");
			} else {
				getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
			//25638 - End
		} else {
			this.getManagedBean().setLocalOutCome(action);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();
				List<ScQtSpecificationRemark> remarks = this.getManagedBean().getSpecificationRemarks();
				
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						ScQtSpecificationRemark Remark = remarks.get(0);
						String userRemark = remarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						} else if (null == Remark.getRemarkId()) {
							RequestContext.getCurrentInstance().execute(
									"reviceApprovalDlgs.show()");
						} else {
							reviseSpecificationCommittee();
						}
					}
				}
			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					//Bug_28291
					checkTechnicalAndFinancialSpec(this.getManagedBean().getQt());
					//20210621@asyrafhaziq_Bug #30914 - Start
					if (null != this.getManagedBean().getLoginRoleType()
							&& !"".equals(this.getManagedBean().getLoginRoleType())
							&& "SC".equalsIgnoreCase(this.getManagedBean()
									.getLoginRoleType())) {
						checkZoneLocationChange(this.getManagedBean().getQt());
					}
					//20210621@asyrafhaziq_Bug #30914 - End
					validateSupplierCriteriaIntegrity(this.getManagedBean().getQt());	// Bug #36730@Syazani 07/01/2025
					
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
							&& !this.getManagedBean().isReadOnly()) {
						boolean technical = validateTechnicalStandardCheckListSubmit();

						if (!technical
								&& !this.getManagedBean()
										.isTechnicalOtherChecklistSubmit()) {
							RequestContext
									.getCurrentInstance()
									.execute(
											"technicalStandardChecklistDlgsSubmit.hide()"); // Bug #33269
						}
						boolean financial = validateFinancialStandardCheckListSubmit();
						if (!financial
								&& !this.getManagedBean()
										.isFinancialOtherChecklistSubmit()) {
							RequestContext
									.getCurrentInstance()
									.execute(
											"financialStandardChecklistDlgsSubmit.show()");
						}

					}
					// 25638 - Start
					SpecificationResDTO resDTO = updateSpecCommitteDtls(action, null);
					reviseSpecificationCommittee(resDTO, action);
					// 25638 - End
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					// 25638 - Start
					SpecificationResDTO resDTO = updateSpecCommitteDtls(action, null);
					reviseSpecificationCommittee(resDTO, action);
					// 25638 - End
				}
			}
		}
		committeSO = null;
	}
	
	//25638 - Start
	public void reviseSC() {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this.getManagedBean().getLocalOutCome(), null);

		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();

			successMessage(MessageConst.INFO_700071, getMessageCommon("lbl.sc.quotation.tender"), qt.getQtNo(),
					Boolean.TRUE);
		}
	}
	//25638 - End
	//20210621@asyrafhaziq_Bug #30914 - Start
	 private void checkZoneLocationChange(ScQt scQt) {
			try {
				
				List<ScQtCheckList> technicalSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
				if (null == technicalSpecCheckList
						|| technicalSpecCheckList.isEmpty()) {
					retriveTechnicalCheckList();
				}
				List<ScQtCheckList> financialSpecCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				if (null == financialSpecCheckList
						|| financialSpecCheckList.isEmpty()) {
					retriveFinancialCheckList();
				}
				if (null != financialSpecCheckList
						&& !financialSpecCheckList.isEmpty()) {
					PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					for (ScQtCheckList financialchecklist : financialSpecCheckList) {
						if (QTConstants.TEMPLETE_SPEC_CODE
								.equalsIgnoreCase(financialchecklist.getDefinationType()
										.getParameterCode())) {
							if ((scQt.isZonal() && financialchecklist.getScQtDoc().getTplSpecMaster().getTplSpecZone() == null )||
									(!scQt.isZonal() && financialchecklist.getScQtDoc().getTplSpecMaster().getTplSpecZone() != null))	{
									financialchecklist.setPmStatus(status);
								}
					}
				}
				}
				if (null != technicalSpecCheckList
						&& !technicalSpecCheckList.isEmpty()) {
					PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
					for (ScQtCheckList technicalchecklist : technicalSpecCheckList) {
						if (QTConstants.TEMPLETE_SPEC_CODE
								.equalsIgnoreCase(technicalchecklist.getDefinationType()
										.getParameterCode())) {
							if ((scQt.isZonal() && technicalchecklist.getScQtDoc().getTplSpecMaster().getTplSpecZone() == null)||
									(!scQt.isZonal() && technicalchecklist.getScQtDoc().getTplSpecMaster().getTplSpecZone() != null))	{
								technicalchecklist.setPmStatus(status);
								}
					}
					}
				}

			} finally {

			}

		}
		//20210621@asyrafhaziq_Bug #30914 - End
	/**
	 * Prepare spec chir person request.
	 * 
	 * @param action
	 *            the action
	 * @param pmDigiSign
	 *            the pm digi sign
	 * @return the specification res dto
	 */
	private SpecificationResDTO prepareSpecChairPersonRequest(String action,
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
		ScQt scQt = this.getManagedBean().getQt();
		if (null != remarks) {
			scQt.setScQtRemarks(remarks);
		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		if (!scQt.isBsvRequired()) {
			scQt.setScQtBriefings(null);
		}
		specReqDTO.setScQt(scQt);
		specReqDTO.setOutcome(action);
		if (null != pmDigiSign) {
			updatePmDigiSignData(pmDigiSign, ParameterConst.DOCTYPE_QUOTATION,
					scQt.getQtId(), scQt.getQtNo());
		}
		specReqDTO.setDigiSignData(pmDigiSign);
		specReqDTO.setTaskId(this.getManagedBean().getTaskId());
		specReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		specReqDTO.setPmUser(facesScopes.getUserSessionBean());
		specReqDTO.setRoleCode(getUserRoleCode());
		specReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		specReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		specReqDTO.setUserLocale(facesScopes.getUserLocale());
		IQTSpecSO specSO = getSpecSO();
		// #25638 - Start.
		specReqDTO.setSpecMaintenance(getManagedBean().isSpecMaintenance());
		// #25638 - End.
		try {
			resDTO = specSO.updateBPMAction(specReqDTO);

		} finally {
			specSO = null;
		}

		return resDTO;
	}

	/**
	 * Gets the role type dropdown values.display item type(product/Service)in
	 * template spec dropdown.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[] .
	 */
	public SelectItem[] getRoleTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE);
		/*
		 * if (this.getManagedBean().getQt().isPanel()) {
		 * recommendList.add(ParameterConst.ITEM_TYPE_SERVICE); }
		 */

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		searchBean = null;
		recommendList = null;
		return items;
	}

	/**
	 * Gets the quotation tendervalues.display quotation/Tender
	 * Type(open/Restricted) radio values in business owner.
	 * 
	 * @param quotationTenderType
	 *            ((String quotationTenderType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getQuotationTendervalues(String quotationTenderType) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		ScQt scQt = getManagedBean().getQt();

		if (null != scQt
				&& ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
			List<String> qtTypes = new ArrayList<String>();
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), qtTypes));
		}

		return super.getRadioButtonCodeValuesBySearchBean(quotationTenderType,
				searchBean);
	}
	
	
	public SelectItem[]  getSelectRadioButtonCodeValues(String type)
	{
		
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		//ScQt scQt = getManagedBean().getQt();

		
			List<String> qtTypes = new ArrayList<String>();
			qtTypes.add(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), qtTypes));
		
		
			return super.getRadioButtonCodeValuesBySearchBean(type,searchBean);
	}

	/**
	 * Gets the local goods radio values.display Local goods (Local/Import)
	 * radio values in template spec.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);

		if (!this.getManagedBean().getQt().isPanel()) {
			types.add(ParameterConst.IMPORTED_GOODS);
		}

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
			types = null;
		} finally {
		}
		return items;
	}

	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.Event handle set gpki data.
	 * 
	 * @param textBeforeSign
	 *            (String textBeforeSign).
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		// Temp : QtNumber + userId
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}

	/**
	 * Update spec committe dtls.
	 * 
	 * @param action
	 *            the action
	 * @param digiSign
	 *            the digi sign
	 * @return the specification res dto
	 */
	public SpecificationResDTO updateSpecCommitteDtls(String action,
			PmDigiSign digiSign) {
		ScQt scQt = this.getManagedBean().getQt();
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF)) {

			loadPrepareSpecCommitteeDetails(scQt.getQtId());

			// Update Procurement Mode based on final estimated price.
			scQt = getManagedBean().getQt();
			Long statusId = scQt.getQtStatus().getStatusId();
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
					&& (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING
							.equals(statusId) || QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION
							.equals(statusId))) {
				if (null == scQt.getEstimatedAmount()) {
					SpecificationReqDTO estimatedReqDTo = new SpecificationReqDTO();
					estimatedReqDTo.setQtId(scQt.getQtId());
					IQTSpecSO specSO = getSpecSO();
					SpecificationResDTO resTO = specSO
							.calCulateTemplateSpecEstimations(estimatedReqDTo);
					if (null != resTO
							&& null != resTO.getTotalSpecEstimatedAmount()) {
						scQt.setEstimatedAmount(resTO
								.getTotalSpecEstimatedAmount());
					}
				}
				new CreateQtBackingBean().setProcurementMode(scQt,
						scQt.getEstimatedAmount());
				// 20190415@asyrafhaziq - 15979 -Start
				new CreateQtBackingBean().setOpenFor(scQt,
						scQt.getEstimatedAmount());
				// 20190415@asyrafhaziq - 15979 -End
			}

		}
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls(null,false);	//Bug #35688@Syazani 10/07/2024 #29732 adding param
		List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
		if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
			for (int i = 0; i < checklistRemoveList.size(); i++) {
				if (null == checklistRemoveList.get(i).getQtChecklistId()) {
					checklistRemoveList.remove(i);
				}
			}

		}
		List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
				.getDeliveryLocRemoveList();
		if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
			for (int i = 0; i < deliveryRemoveList.size(); i++) {
				if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
					deliveryRemoveList.remove(i);
				}
			}

		}
		/*
		 * if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) &&
		 * !this.getManagedBean().isReadOnly()) {
		 * validateTechnicalStandardCheckListSubmit(); //
		 * validateFinancialStandardCheckListSubmit(); }
		 */

		// #29474 - start
		List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
				.getRemoveDeductions();
		if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
			for (int i = 0; i < deductionRemoveList.size(); i++) {
				if (null == deductionRemoveList.get(i).getQtDeductionId()) {
					deductionRemoveList.remove(i);
				}
			}
		}
		spReqDTO.setRemoveDeductions(deductionRemoveList);// #29474 - end
		spReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);
		spReqDTO.setTobeRemoveList(checklistRemoveList);
		spReqDTO.setTobeRemoveCategoryCodes(this.getManagedBean()
				.getTobeRemoveCategoryCodes());
		spReqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
				.getToBeRemoveSchedules());
		spReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean()
				.getToBeRemoveProgPayments());
		spReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean()
				.getToBeRemoveRestrictedSupplierList());
		spReqDTO.setOutcome(action);
		//Bug #26214
		if (null != this.getManagedBean()
				&& (SCProcessNameConst.FINALISE_SCORING.equals(this
						.getManagedBean().getTaskName()) || SCProcessNameConst.SPEC_MAINTENANCE_REVISE_SCORING
						.equals(this.getManagedBean().getTaskName()))){
			spReqDTO.setAllowZeroVariance(true);
		}
		// spReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		if (null != digiSign) {
			updatePmDigiSignData(digiSign,
					ParameterConst.DOCTYPE_SPEC_COMMITTE, scQt.getQtId(),
					scQt.getQtNo());
		}

		/*
		 * if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
		 * 
		 * List<ScQtSpecificationRemark> remarks = super
		 * .finalizeRemarks(getManagedBean().getSpecificationRemarks());
		 * 
		 * if (null != remarks) { ScQt localScQt = spReqDTO.getScQt();
		 * localScQt.setScQtSpecificationRemarks(remarks);
		 * spReqDTO.setScQt(localScQt); } List<ScQtCommitteeActivity>
		 * committeActivities = this .getManagedBean().getCommitteeActivities();
		 * if (null != committeActivities) {
		 * 
		 * for (int i = 0; i < committeActivities.size(); i++) {
		 * 
		 * committeActivities.get(i).setSignedOff(false);
		 * 
		 * } } spReqDTO.setCommitteActivities(committeActivities); }
		 */

		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)) {
			List<ScQtCommitteeActivity> committeActivities = this
					.getManagedBean().getCommitteeActivities();
			if (null != committeActivities) {

				for (int i = 0; i < committeActivities.size(); i++) {
					if (ParameterConst.COMMITTEE_ROLE_SECRETARY
							.equalsIgnoreCase(committeActivities.get(i)
									.getScCommitteeMember().getMemberRole()
									.getParameterCode())) {
						committeActivities.get(i).setSignedOff(true);
					} else {
						committeActivities.get(i).setSignedOff(false);
					}

				}
				List<ScQtSpecificationRemark> remarks = super
						.finalizeRemarks(getManagedBean()
								.getSpecificationRemarks());

				if (null != remarks) {
					ScQt localScQt = spReqDTO.getScQt();
					if (null != remarks) {
						for (int i = 0; i < remarks.size(); i++) {
							String userRemark = remarks.get(i).getRemark();
							if (null == userRemark || "".equals(userRemark)) {
								remarks.remove(i);
							}
						}
					}
					localScQt.setScQtSpecificationRemarks(remarks);
					spReqDTO.setScQt(localScQt);
				}
			}
			spReqDTO.setCommitteActivities(committeActivities);
			spReqDTO.setSampleExist(checkISSampleExistWIPChecklist());//11104-Reendose

		} else {
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				List<ScQtSpecificationRemark> remarks = super
						.finalizeRemarks(getManagedBean()
								.getSpecificationRemarks());

				if (null != remarks) {
					ScQt localScQt = spReqDTO.getScQt();
					if (null != remarks) {
						for (int i = 0; i < remarks.size(); i++) {
							String userRemark = remarks.get(i).getRemark();
							if (null == userRemark || "".equals(userRemark)) {
								remarks.remove(i);
							}
						}
					}
					localScQt.setScQtSpecificationRemarks(remarks);
					spReqDTO.setScQt(localScQt);
				}
				// #25638 - Start.
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getCommitteeActivities())) {
		            for (ScQtCommitteeActivity scQtCommitteeActivity : this.getManagedBean().getCommitteeActivities()) {
		                scQtCommitteeActivity.setSignedOff(false);
		            }
		        }
				spReqDTO.setCommitteActivities(this.getManagedBean().getCommitteeActivities());
				// #25638 - End.

			} else {

				List<ScQtCommitteeActivity> committeActivities = this
						.getManagedBean().getCommitteeActivities();
				if (null != committeActivities) {
					PmUser pmUser = new FacesScopes().getUserSessionBean();
					for (int i = 0; i < committeActivities.size(); i++) {
						if (pmUser.getIdentificationNo().equals(
								committeActivities.get(i)
										.getScCommitteeMember().getPmUser()
										.getIdentificationNo())) {
							committeActivities.get(i).setSignedOff(true);
							break;
						}

					}
				}
				spReqDTO.setCommitteActivities(committeActivities);

			}

		}
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		spReqDTO.setPmUser(facesScopes.getUserSessionBean());
		spReqDTO.setRoleCode(getUserRoleCode());
		spReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		spReqDTO.setUserLocale(facesScopes.getUserLocale());
		SpecificationReqDTO specReqDto=new SpecificationReqDTO();//35071_start
		specReqDto.setScQt(this.getManagedBean().getQt());
		this.getManagedBean().setWipStatusMax(getSpecSO().checkWipScoreSettingMax(specReqDto));
		if(this.getManagedBean().getWipStatusMax()<SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM){	
			this.getManagedBean().setOfferScoreType(ParameterConst.FINANCE_SCORE_TYPE_AUTO);
			scoreTypeN=ParameterConst.FINANCE_SCORE_TYPE_AUTO;
		}//35071_End
		spReqDTO.setOfferScoreType(this.getManagedBean().getOfferScoreType());//25138 - ahmaa
		// #25638 - Start.
		spReqDTO.setSpecMaintenance(getManagedBean().isSpecMaintenance());
		// #25638 - End.
		//#25138 - set for one item only - start
		spReqDTO.setSingleItem(this.getManagedBean().isSingleItem());
		//#25138 - set for one item only - end
		//#31450 - bug for #25138 - start
		spReqDTO.setScoreOfferPriceList(this.getManagedBean().getOfferPricePackageList());
		//#31450 - bug for #25138 - end
		IQTSpecSO specSO = getSpecSO();
		
		//29561 -start
		spReqDTO.setBaseURL(SmCommonUtil.getBasePortalUrl());
		spReqDTO.setTaskName(this.getManagedBean().getTaskName());
		//29561 -end
		SpecificationResDTO resDTO = new SpecificationResDTO();
		try {
			resDTO = specSO.updateBPMAction(spReqDTO);

		} finally {
			specSO = null;
		}
		return resDTO;
	}

	/**
	 * Gpki sign action.user clicks on submit button in specification committee.
	 * 
	 * @param digiSign
	 *            (PmDigiSign digiSign)
	 * @return navigate to acknowledgement page.
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		SpecificationResDTO resDTO = null;
		if (null != this.getManagedBean().getSpecMaintenanceWorkFlowStatus()) {
			log.debug("====maintenance flow====");
			resDTO = loadSpecMaintenanceBPmDetails(this.getManagedBean()
					.getLocalOutCome(), digiSign);
		} else {
			resDTO = updateSpecCommitteDtls(this.getManagedBean()
					.getLocalOutCome(), digiSign);
		}
		// 25638 - Start
		reviseSpecificationCommittee(resDTO, this.getManagedBean().getLocalOutCome());
		// 25638 - End
	}

	/**
	 * Revise ministry spec approval.Event handle revise in ministry
	 * specification committee approval.
	 * 
	 * @return navigate to acknowledgement page.
	 */
	public void reviseMinistrySpecApproval() {
		SpecificationResDTO resDTO = prepareSpecChairPersonRequest(this
				.getManagedBean().getLocalOutCome(), null);

		// 25638 - Start
		reviseSpecificationCommittee(resDTO, this.getManagedBean().getLocalOutCome());
		// 25638 - End
	}

	/**
	 * Revise specification committee.Event handle revise in specification
	 * committee .
	 * 
	 * @return navigate to acknowledgement page.
	 */
	

	// 25638 - Start
	public void reviseSpecificationCommittee() {
		SpecificationResDTO resDTO = updateSpecCommitteDtls(this.getManagedBean().getLocalOutCome(), null);

		reviseSpecificationCommittee(resDTO, this.getManagedBean().getLocalOutCome());
	}

	public void reviseSpecificationCommittee(SpecificationResDTO resDTO, String action) {
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();

			successMessage(getMessgeConstant(action),
					getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(), Boolean.TRUE);
		}
	}
	// 25638 - End
	/**
	 * Gets the pm digi sign.
	 * 
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 * 
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Cancel spec action.user click on cancel button in template spec screes it
	 * will remove the existing specs from database.
	 * 
	 * @param flowType
	 *            (String flowType).
	 * @return navigate to business owner or specification committee menu.
	 */
	public String cancelSpecAction(String flowType) {
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();
		// Bug #33661@Syazani 24/05/2023 -- Start
		IQTDetailSO detailSO = null;
		PmStatus qtStatus = null;
		Long statusId = 0L;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			
			qtStatus = detailSO.retrieveActiveStatusByDoctype(this.getManagedBean().getQt().getQtId(),
					ParameterConst.DOCTYPE_QUOTATION);
			if (qtStatus != null)
				statusId = qtStatus.getStatusId();

			if (Arrays.asList(
					new Long[] { QTStatusConst.SE_PENDING_ENDORSMENT, QTStatusConst.SE_PENDING_SIGNOFF,
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF }).contains(statusId)) {
				commonMessageAcknowledgementErrorPage(MessageConst.ERROR_775189, new String[] { this.getManagedBean()
						.getQt().getQtNo() });
				return null;
			}
			// Bug #33661@Syazani 24/05/2023 -- End
			
			if (null != checkList.getQtChecklistId()) {
				// checkList.setDeleted(true);
				List<ScQtCheckList> toBeRemoveCheckLists = new ArrayList<ScQtCheckList>();
				// toBeRemoveCheckLists.add(checkList);
				List<ScQtCheckList> technicalCheckLIsts = this.getManagedBean()
						.getScQtCheckSt2techProposal();
				for (int i = 0; i < technicalCheckLIsts.size(); i++) {
					if (checkList.getQtChecklistId().equals(
							technicalCheckLIsts.get(i).getQtChecklistId())) {
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

				if (null != financialCheckLIsts && !financialCheckLIsts.isEmpty()) {
					if (null != this.getManagedBean().getCheckListTitle()
							&& !"".equals(this.getManagedBean().getCheckListTitle())) {
						SpecificationReqDTO reqDTO = new SpecificationReqDTO();
						reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
						reqDTO.setPrepareSpec(false);

						specSo = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										IQTSpecSO.class);
						SpecificationResDTO resDto = specSo
								.retrieveSpecDetails(reqDTO);

						if (null != resDto
								&& null != resDto.getFinSpecResultBean()
										.getResultList()
								&& !resDto.getFinSpecResultBean().getResultList()
										.isEmpty()) {
							boolean isFinancialChecklistFound = false;
							for (ScQtCheckList financialspec : resDto
									.getFinSpecResultBean().getResultList()) {
								if (financialspec
										.getTitle()
										.trim()
										.replaceAll("\\s", "")
										.equalsIgnoreCase(
												this.getManagedBean()
														.getCheckListTitle().trim()
														.replaceAll("\\s", ""))) {
									isFinancialChecklistFound = true;
									financialspec.setDeleted(true);
									toBeRemoveCheckLists.add(financialspec);
									log.info("BothTitlesareEqualForDelete"
											+ this.getManagedBean().getQt()
													.getQtId());
									break;
									/*
									 * throw new
									 * EpException(MessageConst.ERROR_760310, new
									 * String[] { financialSpecs.getTitle() });
									 */
								}
							}
							if (!isFinancialChecklistFound) {
								if (!checkSequenceMorethanOne(resDto
										.getFinSpecResultBean().getResultList(),
										checkList)) {
									log.info("CancelSpecSequenceFalse");
									for (ScQtCheckList financialspec : resDto
											.getFinSpecResultBean().getResultList()) {
										if (financialspec.getChecklistSeq().equals(
												checkList.getChecklistSeq())) {
											isFinancialChecklistFound = true;
											financialspec.setDeleted(true);
											toBeRemoveCheckLists.add(financialspec);
											log.info("BothSequencesAreEqualForDelete"
													+ this.getManagedBean().getQt()
															.getQtId());
											break;
											/*
											 * throw new
											 * EpException(MessageConst.ERROR_760310
											 * , new String[] {
											 * financialSpecs.getTitle() });
											 */
										}
									}
								}

							}
							if (!isFinancialChecklistFound) {
								for (ScQtCheckList financialspec : resDto
										.getFinSpecResultBean().getResultList()) {
									if (financialspec.getRecordStatusFlg().equals(
											DbValueConst.RECORD_STATUS_DELETED)) {
										financialspec.setDeleted(true);
										toBeRemoveCheckLists.add(financialspec);
										log.info("BothTitlesareNotEqualForDelete"
												+ this.getManagedBean().getQt()
														.getQtId());
										break;
										/*
										 * throw new
										 * EpException(MessageConst.ERROR_760310,
										 * new String[] { financialSpecs.getTitle()
										 * });
										 */
									}
								}

							}

						}
					}
				}

				SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
				specReqDTO.setScQt(this.getManagedBean().getQt());
				specReqDTO.setCheckLists(toBeRemoveCheckLists);
				SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);

				if (resDTO.isSuccessful()) {
					//1873 note 182 start
					ISearchResultBean<ScQtCheckList> checklistResultBean = specSo.retrieveTemplateSpecChecklist(specReqDTO).getSpecResultBean();
					Map<Long, ScQtCheckList> tecChecklistMap = new LinkedHashMap<Long, ScQtCheckList>();
					Map<Long, ScQtCheckList> finChecklistMap = new LinkedHashMap<Long, ScQtCheckList>();
					if(CollectionUtils.isNotEmpty(checklistResultBean.getResultList())){
						for(ScQtCheckList checklist: checklistResultBean.getResultList()){
							if(SourcingConstants.TECHNICAL_DOCTYPE.equals(checklist.getDocCategory())){
								tecChecklistMap.put(checklist.getQtChecklistId(), checklist);
							}else if(SourcingConstants.FINANCIAL_DOCTYPE.equals(checklist.getDocCategory())){
								finChecklistMap.put(checklist.getQtChecklistId(), checklist);
							}
						}
					}
					if(CollectionUtils.isNotEmpty(technicalCheckLIsts)){
						ListIterator<ScQtCheckList> tecChecklistIter = technicalCheckLIsts.listIterator();
						while(tecChecklistIter.hasNext()) {
							ScQtCheckList checklist = tecChecklistIter.next();
							if(finChecklistMap.get(checklist.getQtChecklistId())!=null){
								tecChecklistIter.set(tecChecklistMap.get(checklist.getQtChecklistId()));
							}
						}
					}

					if(financialCheckLIsts != null && CollectionUtils.isNotEmpty(financialCheckLIsts)){//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
						ListIterator<ScQtCheckList> finChecklistIter = financialCheckLIsts.listIterator();
						while(finChecklistIter.hasNext()) {
							ScQtCheckList checklist = finChecklistIter.next();
							if(finChecklistMap.get(checklist.getQtChecklistId())!=null){
								finChecklistIter.set(finChecklistMap.get(checklist.getQtChecklistId()));
							}else if(toBeRemoveCheckLists!=null && toBeRemoveCheckLists.size()>1){
								ScQtCheckList finChecklist = toBeRemoveCheckLists.get(1);
								Long qtChecklistId = checklist.getQtChecklistId()==null ? 0L: checklist.getQtChecklistId();
								if(qtChecklistId.equals(finChecklist.getQtChecklistId())){
									finChecklistIter.remove();
								}
							}
						}
					}
					//1873 note 182 start
					String title=" ";//11104-start
					if(this.getManagedBean().getQt().isSampleReq() && CollectionUtils.isNotEmpty(technicalCheckLIsts)){
						for(ScQtCheckList checklist:technicalCheckLIsts){
							if (checklist.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE)) {
								title=checklist.getTitle();
								break;
							}
						}
					}//11104-End
					
					//#28784 - Start
					if(this.getManagedBean().getQt().isDemoRequired() && CollectionUtils.isNotEmpty(technicalCheckLIsts)){
						for(ScQtCheckList checklist:technicalCheckLIsts){
							if (checklist.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
								title=checklist.getTitle();
								break;
							}
						}
					}
					this.getManagedBean().setFinancialScoring(null);
					this.getManagedBean().setOfferPricePackage(null);
					this.getManagedBean().setQt(resDTO.getScQt());
					if(this.getManagedBean().getQt().isSampleReq() && CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())){//11104-start
						for(ScQtCheckList checklist:this.getManagedBean().getScQtCheckSt2techProposal()){
							if (checklist.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE)) {
								if(null==checklist.getTitle() || StringUtils.isBlank(checklist.getTitle()) || StringUtils.isEmpty(checklist.getTitle())){
									checklist.setTitle(title);
								}
								break;
							}
						}
						sampleDetailsPendingTemplateSpec();//11104-807
					}//11104-End				
					
					addDemoTechnicalCheckList();//#28784
					if(this.getManagedBean().getQt().isDemoRequired() && CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())){//11104-start
						for(ScQtCheckList checklist:this.getManagedBean().getScQtCheckSt2techProposal()){
							if (checklist.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
								if(null==checklist.getTitle() || StringUtils.isBlank(checklist.getTitle()) || StringUtils.isEmpty(checklist.getTitle())){
									checklist.setTitle(title);
								}
								break;
							}
						}
					}//#28784 - End
					if (this.getManagedBean().getQt().isDemoRequired()) {
						// #28784 - reset Demo status after delete spec
						IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
						SpecificationReqDTO reqDTO = new SpecificationReqDTO();
						reqDTO.setScQt(this.getManagedBean().getQt());					
						List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
						List<ScQtCheckList> demoUpdateList = new ArrayList<>();
						ScQtCheckList demo = null;
						if (this.getManagedBean().isNewVersion()) {

							for (ScQtCheckList t : st2technicalproposalList) {
								if (t.getDefinationType().getParameterCode()
										.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
									t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
									t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
									demo = t;
									demoUpdateList.add(t);
									break;
								}
							}
							
							reqDTO.setCheckLists(demoUpdateList);
						}
						if (demo != null) {
							SpecificationResDTO resDto = specSO.completeDemoChecklist(reqDTO);
							this.getManagedBean().getScQtCheckSt2techProposal().set(this.getManagedBean().getScQtCheckSt2techProposal().indexOf(demo)
									, resDto.getSavedCheckLists().get(0));
						}
						resetDemoSpecStatus();// #28784
						saveSpecMaintenanceWorkFlowStatus(); // to Set Spec Status to pending submittion if spec sec delete spec when doing spec maintanance, #28785
					}
					this.getManagedBean().setCheckListTitle("");
					this.getManagedBean().setIndex(-1);
					return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
				}
			} else {
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().getScQtCheckSt2techProposal();
				checkPanelSpecs();
				return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";

			}
			specSo = null;
			return null;
			// Bug #33661@Syazani 24/05/2023 -- Start
		} finally {
			detailSO = null;
		}
		// Bug #33661@Syazani 24/05/2023 -- End

	}

	/**
	 * Save template spec details.user clicks on save button in template spec
	 * screens.
	 * 
	 * @param flowType
	 *            (String flowType)
	 * @param specType
	 *            (String specType)
	 * @return the string
	 */
	public void saveTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		// Bug #33661@Syazani 07/10/2022 -- Start
		IQTDetailSO detailSO = null;
		PmStatus qtStatus = null;
		Long statusId = 0L;
		
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			// Bug #33661@Syazani 07/10/2022 -- End
			boolean issample=this.getManagedBean().getQt().isSampleReq();//11104ReendorseStart728
			ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
			scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
					.getSpecName());
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			Integer index = null;//1873 - note 97
			
			// #29187 Start Vinod
			List<TplSpecQuestion> questionsList = scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions();
			for (TplSpecQuestion question : questionsList) {
				String questionType = question.getQuestionType();
				if (questionType.equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					question.setMandatorySpec(false);
				}
			}
			// #29187 End Vinod
			
			// Bug #33661@Syazani 07/10/2022 -- Start
			qtStatus = detailSO.retrieveActiveStatusByDoctype(this.getManagedBean().getQt().getQtId(), ParameterConst.DOCTYPE_QUOTATION);
			if (qtStatus != null)
				statusId = qtStatus.getStatusId();
			
			if (Arrays.asList(
					new Long[] { QTStatusConst.SE_PENDING_ENDORSMENT, QTStatusConst.SE_PENDING_SIGNOFF,
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT, QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF }).contains(
					statusId)) {
				commonMessageAcknowledgementErrorPage(MessageConst.ERROR_775189, new String[] { this.getManagedBean().getQt().getQtNo() });
				return;
			}
			// Bug #33661@Syazani 07/10/2022 -- End
			
			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
				ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (((null != zoneSupplierCriteria && zoneSupplierCriteria
						.isBrandNew() == true)
						|| (getManagedBean().getQt().isZonal()
								&& null != zoneSupplierCriteria
								&& null != zoneSupplierCriteria
										.getQtZoneCriterias() && null == zoneSupplierCriteria
								.getQtZoneCriterias().getSpecZoneId()) || (getManagedBean()
						.getQt().isPanel() && this.getManagedBean()
						.isPanelSpecExistedOrNot()))) {
					ScQt scQt = saveZonesBeforeTemplateSpec(getManagedBean()
							.getQt());
					reqDTO.setZonesQt(true);
					reqDTO.setScQt(scQt);
					// resDTO = specSO.saveTemplateSpecZoneDtls(reqDTO);
					this.getManagedBean().setQtCategoryList(
							new ArrayList<ScQtCategory>());
					// this.getManagedBean().setZoneGeneralTrees(
					// new ArrayList<TreeNode>());
					// this.getManagedBean().setQt(resDTO.getScQt());
					// retriveSpecdetails(resDTO.getScQt().getQtId());
					// retriveSupplierQualificationCriteria("SC");
					// validateBusinessOwner(true);
				}

			}
			//1873 - start - note 52
			boolean isModified = false;
			//#34048 - Start
			boolean isSpecNotExist = false;
			boolean isItemNotDeleted = false;//#28784
			if (!SourcingConstants.FINANCIAL_DOCTYPE.equalsIgnoreCase(specType)) { 
			PmStatus pendingStatus = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);//#11140-Start
			boolean isTemplateSpec = QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(scQtCheckList.getDefinationType().getParameterCode());
			isModified = isTemplateSpec && isSpecQuestionsModified(scQtCheckList);//1873 - start - note 97
			isSpecNotExist = isSpecQuestionNotExist(scQtCheckList);
			if(isModified || !isTemplateSpec || isSpecNotExist){//#34048 - End
				scQtCheckList.getScQtDoc().getTplSpecMaster().setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT); //Bug #35233
				scQtCheckList.setPmStatus(pendingStatus);
				scQtCheckList.setPmEvalStatus(pendingStatus);
				scQtCheckList.setSampleStatus(pendingStatus);//#11140-End
				scQtCheckList.setPmDemoStatus(pendingStatus);//#28784
			}
			//#28784 - Start
				isItemNotDeleted = isSpecItemNotExist(scQtCheckList);
				if (isItemNotDeleted) {
					scQtCheckList.setPmDemoStatus(pendingStatus);
				}
				//#28784 - End
				//1873 - end - note 52
				if (getManagedBean().getQt().isZonal()) {
					ScQtSupplierCriteria supplierCriteria = getManagedBean()
							.getQt().getScQtSupplierCriteria();
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
				if (this.getManagedBean().getSpecMaintenanceScreen()
						.equalsIgnoreCase("SC")) {
					if (getManagedBean().getQt().getEstimatedAmount() != null
							&& getManagedBean().getQt().getEstimatedAmount()
									.intValue() > 0) {
						checkFinancialSpecEstimations(this.getManagedBean()
								.getScQtCheckSt2financeProposalList(),
								scQtCheckList, getManagedBean().getQt(),true);//20210205@haziq- Bug #30046 
					}
					checkFinancialparentSpecQuestion(scQtCheckList);
				}
				else if(this.getManagedBean().getSpecMaintenanceScreen()
						.equalsIgnoreCase(QTConstants.SPEC_MAINTENANCE)) {//32134-start
					checkFinancialSpecEstimations(this.getManagedBean()
							.getScQtCheckSt2financeProposalList(), scQtCheckList,
							getManagedBean().getQt(),true);
				}//32134-End
				PmStatus pendingStatus=	getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);//#11140-Start
				scQtCheckList
						.setPmStatus(pendingStatus);
				scQtCheckList
						.setPmEvalStatus(pendingStatus);//#11140-End
			}
			List<ScQtCheckList> toBeRemoveList = null;
			List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>(); //1873 - note 52
			ScQtCheckList localCheckList = null;//1873 - note 97
			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {

				List<ScQtCheckList> technicalCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
				List<ScQtCheckList> finanicalAllCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				// Bug #33249@Syazani 24/11/2022 -- Start
				String scQtTitleSpec = scQtCheckList.getTitle() + "_"
						+ (scQtCheckList.getDefinationType() != null ? scQtCheckList.getDefinationType().getParameterId() : "");
				// Bug #33249@Syazani 24/11/2022 -- End
				if (this.getManagedBean().getIndex() == -1
						&& null == scQtCheckList.getQtChecklistId()) {
					if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
						Integer localChecckListseq = SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC;
						int technicalSpecCount = 0;
						// Bug #33249@Syazani 24/11/2022
						
						for (ScQtCheckList techlist : technicalCheckList) {
							if (QTConstants.TEMPLETE_SPEC_CODE
									.equalsIgnoreCase(techlist.getDefinationType()
											.getParameterCode())) {
								technicalSpecCount++;
								localChecckListseq = localChecckListseq < techlist
										.getChecklistSeq() ? techlist
										.getChecklistSeq() : localChecckListseq;

							}
							
							// Bug #33249@Syazani 27/07/2022 -- Start
							String techTitleList = techlist.getTitle() + "_"
									+ (techlist.getDefinationType() != null ? techlist.getDefinationType().getParameterId() : "");
							if (techTitleList.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtTitleSpec.trim().replaceAll("\\s", ""))) { // Bug #33249@Syazani 24/11/2022
								// Bug #33249@Syazani 27/07/2022 -- End
								throw new EpException(MessageConst.ERROR_760207,
										new String[] { scQtCheckList.getTitle() });
							}
						}
						log.info("QTID:" + getManagedBean().getQt().getQtId()
								+ "localChecckListseq" + localChecckListseq);
						if (technicalSpecCount > 0) {
							scQtCheckList.setChecklistSeq(localChecckListseq + 1);
						}
					}
					if (null != finanicalAllCheckList
							&& !finanicalAllCheckList.isEmpty()) {
						// Bug #33249@Syazani 27/07/2022 -- Start
						// Bug #33249@Syazani 24/11/2022
						for (ScQtCheckList financiallist : finanicalAllCheckList) {
							String financialTitleList = financiallist.getTitle() + "_"
									+ (financiallist.getDefinationType() != null ? financiallist.getDefinationType().getParameterId() : "");
							if (null != financiallist.getDefinationType()
									&& !QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financiallist.getDefinationType().getParameterCode())
									&& financialTitleList.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtTitleSpec.trim().replaceAll("\\s", ""))) { // Bug #33249@Syazani 24/11/2022
								// Bug #33249@Syazani 27/07/2022 -- End
								throw new EpException(MessageConst.ERROR_760207,
										new String[] { scQtCheckList.getTitle() });
							}
						}
					}
					// Bug #33249@Syazani 24/11/2022 -- Start
				} else {
					if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
						for (ScQtCheckList techlist : technicalCheckList) {
							Long qtChecklistId = techlist.getQtChecklistId() != null ? techlist.getQtChecklistId() : 0;
							String techlistTitle = techlist.getTitle() + "_"
									+ (techlist.getDefinationType() != null ? techlist.getDefinationType().getParameterId() : "");
							if (techlistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtTitleSpec.trim().replaceAll("\\s", ""))
									&& !scQtCheckList.getQtChecklistId().equals(qtChecklistId)) {
								throw new EpException(MessageConst.ERROR_760207, new String[] { scQtCheckList.getTitle() });
							}
						}
					}
					if (null != finanicalAllCheckList && !finanicalAllCheckList.isEmpty()) {
						for (ScQtCheckList financiallist : finanicalAllCheckList) {
							Long qtChecklistId = financiallist.getQtChecklistId() != null ? financiallist.getQtChecklistId() : 0;
							String financiallistTitle = financiallist.getTitle() + "_"
									+ (financiallist.getDefinationType() != null ? financiallist.getDefinationType().getParameterId() : "");
							if (null != financiallist.getDefinationType()
									&& !QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financiallist.getDefinationType().getParameterCode())
									&& financiallistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtTitleSpec.trim().replaceAll("\\s", ""))
									&& !scQtCheckList.getQtChecklistId().equals(qtChecklistId)) {
								throw new EpException(MessageConst.ERROR_760207, new String[] { scQtCheckList.getTitle() });
							}
						}
					}		
				}
				// Bug #33249@Syazani 24/11/2022 -- End

				List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				toBeRemoveList = new ArrayList<ScQtCheckList>();
				if (null != finanicalCheckList && !finanicalCheckList.isEmpty()) {
					if (null != this.getManagedBean().getCheckListTitle()
							&& !"".equals(this.getManagedBean().getCheckListTitle())) {
						for (int i = 0; i < finanicalCheckList.size(); i++) {
							localCheckList = finanicalCheckList.get(i);//1873 - note 97
							if (null != localCheckList.getDefinationType()
									&& QTConstants.TEMPLETE_SPEC_CODE
											.equalsIgnoreCase(localCheckList
													.getDefinationType()
													.getParameterCode())
									&& this.getManagedBean()
											.getCheckListTitle()
											.trim()
											.replaceAll("\\s", "")
											.equalsIgnoreCase(
													localCheckList.getTitle()
															.trim()
															.replaceAll("\\s", ""))) {
								// localCheckList.setDeleted(true);
								//1873 - start - note 52
								this.getManagedBean().setFinTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(localCheckList));//1873 - note 97
								if(isModified){
									finanicalCheckList.remove(localCheckList);
									toBeRemoveList.add(localCheckList);
									localCheckList.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);//1873 note 86
								}else{
									//1873 - start - note 97
									setTemplateSpecMaster(localCheckList);
									this.getManagedBean().getFinTempScQtChecklist().setCurrentlySelected(true);
									scQtCheckList.setEdited(false);
									//1873 - end - note 97
									TplSpecMaster finSpecMaster = localCheckList.getScQtDoc().getTplSpecMaster();
									TplSpecMaster tecSpecMaster = scQtCheckList.getScQtDoc().getTplSpecMaster();
									localCheckList.setTitle(tecSpecMaster.getSpecName());
									finSpecMaster.setSpecName(tecSpecMaster.getSpecName());
									finSpecMaster.setItemOriginCode(tecSpecMaster.getItemOriginCode());
									finSpecMaster.setWeightageReq(tecSpecMaster.isWeightageReq());
									localCheckList.setPmStatus(scQtCheckList.getPmStatus());
									updatedCheckLists.add(localCheckList);
									
									//1873 - start - note 97
									if(CollectionUtils.isNotEmpty(finSpecMaster.getTplSpecQuestions())){
										List<Long> idList = new ArrayList<Long>();
										if(CollectionUtils.isNotEmpty(tecSpecMaster.getFinSpecQuestionsToDelete())){
											for(TplSpecQuestion question: tecSpecMaster.getFinSpecQuestionsToDelete()){
												idList.add(question.getSpecQuestionId());
											}
										}
									    Iterator<TplSpecQuestion> questionIt = finSpecMaster.getTplSpecQuestions().iterator();
									    while (questionIt.hasNext()) {
									    	TplSpecQuestion question = (TplSpecQuestion) questionIt.next();
									    	if(question.getSpecQuestionId()!=null && idList.contains(question.getSpecQuestionId())){
										    	questionIt.remove();
									    	}
									    }
									}
									//1873 - end - note 97
								};
								index = i;//1873 - note 97
								//1873 - end - note 52
								break;
							}
						}
					}
				}
			}
			updatedCheckLists.add(scQtCheckList);

			reqDTO.setCheckLists(updatedCheckLists);
			reqDTO.setTobeStatusChangeList(toBeRemoveList);
			resetCommitteeActivity(reqDTO);
			reqDTO.setScQt(getManagedBean().getQt());
			reqDTO.setSpecChanged(getSpecMaintenance().equalsIgnoreCase(
					this.getManagedBean().getSpecMaintenanceScreen()));
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());

			//1873 - start - note 97
			SpecificationResDTO resDTO  = new SpecificationResDTO(); 
			try{
				resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);
			}catch(Exception e){
				this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
				ScQtCheckList finChecklist = this.getManagedBean().getFinTempScQtChecklist();
				if(finChecklist!=null 
						&& CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2financeProposalList())
						&& index !=null){
					if(finChecklist.isCurrentlySelected()){
						this.getManagedBean().getScQtCheckSt2financeProposalList().set(index, this.getManagedBean().getFinTempScQtChecklist());
						finChecklist.setCurrentlySelected(false);
					}else{
						this.getManagedBean().getScQtCheckSt2financeProposalList().add(index, this.getManagedBean().getFinTempScQtChecklist());
					}
				}
				throw e;
			}finally{
				this.getManagedBean().setFinTempScQtChecklist(null);
			}
			//1873 - end - note 97
			updatedCheckLists = null;
			toBeRemoveList = null;
			// Need to redirect particualr page..
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setCheckListTitle(scQtCheckList.getTitle());//1873 - note 97
				ScQt qt = resDTO.getScQt();
				if (qt != null) {
					this.getManagedBean().setQt(qt);
					this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
					if (null != this.getManagedBean().getCtContract()) {
						this.getManagedBean()
								.getCtContract()
								.setContractNo(
										this.getManagedBean().getQt()
												.getExistingContractNo());
					}
					if (qt.getEstimatedAmount() != null
							&& qt.getEstimatedAmount().intValue() > 0
							&& SourcingConstants.FINANCIAL_DOCTYPE
									.equalsIgnoreCase(specType)) {
						new CreateQtBackingBean().setProcurementMode(qt,
								qt.getEstimatedAmount());
						// 20190415@asyrafhaziq - 15979 -Start
						new CreateQtBackingBean().setOpenFor(qt,
								qt.getEstimatedAmount());
						// 20190415@asyrafhaziq - 15979 -End
					}
				}
				List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
				this.getManagedBean().setFinancialScoring(null);
				this.getManagedBean().setOfferPricePackage(null);
				this.getManagedBean().setOfferPricePackageList(null);//33587
				if (reqDTO.isZonesQt()
						|| (this.getManagedBean().getQt().isZonal() && SourcingConstants.FINANCIAL_DOCTYPE
								.equalsIgnoreCase(specType))
						&& this.getManagedBean().getSpecMaintenanceScreen()
								.equalsIgnoreCase(getSpecMaintenance())) {
					this.getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
				}

				this.getManagedBean().setPanelSpecExistedOrNot(false);
				specCommitteeResetData();
				// settemplateSpecList(savedCheckLists);
				//1873 - start - note 52
				if(this.getManagedBean().getQt().isZonal()) this.getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());//1873 - note 97
				for(ScQtCheckList checklist: savedCheckLists){
					//1873 - start - note 97
					setTechnicalCheckList(checklist);
					if(SourcingConstants.TECHNICAL_DOCTYPE.equals(checklist.getDocCategory())){
						this.getManagedBean().setTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(checklist));
					}else{
						this.getManagedBean().setFinTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(checklist));
					}
					//1873 - end - note 97
				}
				//1873 - end - note 52
				saveSpecMaintenanceWorkFlowStatus();
				addDemoTechnicalCheckList();//#28784
				if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
					resetDemoSpecStatus();// #28784
				}
				this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
				successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.template.spec"), "", Boolean.FALSE);
			}
			// Bug #33661@Syazani 16/08/2022 -- Start
		} finally {
			detailSO = null;
		}
		// Bug #33661@Syazani 16/08/2022 -- End
	}

	//1873 - start - note 97
	private void setTemplateSpecMaster(ScQtCheckList checklist){
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
		reqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO resDTO = templateSpecSO.getTemplateMaster(reqDTO);
		checklist.getScQtDoc().setTplSpecMaster(resDTO.getSpecMaster());
		checklist.getScQtDoc().getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
		if (this.getManagedBean().getQt().isZonal()) {
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
			if (null != supplierCriteria && null != supplierCriteria.getQtZoneCriterias()) {
				retriveSpecdetails(this.getManagedBean().getQt().getQtId());
				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(this.getManagedBean().getZoneGeneralTrees(), null);
				if (businessOwnerZone != null 
						&& !businessOwnerZone.isEmpty()
						&& QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checklist.getDefinationType().getParameterCode())) {
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						supplierCriteria.getQtZoneCriterias().setModified(true);
						tplSpecZoneDtl.setTplSpecZone(supplierCriteria.getQtZoneCriterias());

					}
					checklist.getScQtDoc().getTplSpecMaster().setTplSpecZone(supplierCriteria.getQtZoneCriterias());
					checklist.getScQtDoc().getTplSpecMaster().getTplSpecZone().setTplSpecZoneDtls(businessOwnerZone);
				}
			}
		}
	}
	//1873 - end - note 97
	private void specCommitteeResetData() {
		this.getManagedBean().setTechnicalScoring(null);
		this.getManagedBean().setProgressivePaymentList(null);
		this.getManagedBean().setScheduleList(null);
		this.getManagedBean().setTechnicalScoreSetting(null);
		this.getManagedBean().setFinancialScoreSetting(null);
		this.getManagedBean().setDemoScoreSetting(null);
		this.getManagedBean().setTechnicalItemScoreSetting(null);
		// #21695 START FARID
		this.getManagedBean().setFinacialItemScoreSetting(null);
		this.getManagedBean().setRecommendationPackageScore(null);
		this.getManagedBean().setRecommendationScore(null);
		//remove #29732
		//this.getManagedBean().setQtCategoryList(null);
		this.getManagedBean().setQtSupplierList(null);

	}

	/**
	 * Sets the technical check list.
	 * 
	 * @param localCheckList
	 *            (ScQtCheckList localCheckList).
	 */
	private void setTechnicalCheckList(ScQtCheckList localCheckList) {
		// this.getManagedBean().setCheckListTitle("");
		this.getManagedBean().setScQtChecklist(localCheckList);
		boolean issample=this.getManagedBean().getQt().isSampleReq();//11104ReendorseStart728
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
		localCheckList.getScQtDoc()
		.getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
		templateSpecBean.setSpecMaster(localCheckList.getScQtDoc()
				.getTplSpecMaster());
		if (this.getManagedBean().getQt().isZonal()) {
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getQt().getScQtSupplierCriteria();
			if (null != supplierCriteria
					&& null != supplierCriteria.getQtZoneCriterias()) {
				if (SourcingConstants.FINANCIAL_DOCTYPE
						.equalsIgnoreCase(localCheckList.getDocCategory())
						|| SourcingConstants.TECHNICAL_DOCTYPE
								.equalsIgnoreCase(localCheckList
										.getDocCategory())) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
							this.getManagedBean().getZoneGeneralTrees(), null);

					if (businessOwnerZone != null
							&& !businessOwnerZone.isEmpty()) {

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

			this.getManagedBean().getQt().setSampleReq(issample); // 11104ReendorseStart728

			// #11104-start
			if (this.getManagedBean().getQt().isSampleReq()) {
				sampleDetailsPendingTemplateSpec();
			}
			// #11104-End

			checklistSorting(this.getManagedBean()
					.getScQtCheckSt2techProposal());
		} else {
			setFinSpecScoreWeightage(localCheckList);
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.remove(this.getManagedBean().getIndex());
			this.getManagedBean().getScQtCheckSt2financeProposalList()
					.add(this.getManagedBean().getIndex(), localCheckList);
			checklistSorting(this.getManagedBean()
					.getScQtCheckSt2financeProposalList());

		}
		templateSpecSO = null;
	}

	/**
	 * Sets the fin spec score weightage.
	 * 
	 * @param scoreWeightage
	 *            (ScQtCheckList scoreWeightage).
	 */
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

	/**
	 * Sets the template spec list.
	 * 
	 * @param savedCheckLists
	 *            (List<ScQtCheckList> savedCheckLists).
	 */
	private void settemplateSpecList(List<ScQtCheckList> savedCheckLists,
			String flowType,String specType) {//11104-778&25726
		boolean issample=this.getManagedBean().getQt().isSampleReq();//11104ReendorseStart728
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
			if(this.getManagedBean().getQt().isZonal()) this.getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());//1873 - note 97
			for (ScQtCheckList checkList : savedCheckLists) {
				this.setTemplateSpecMaster(checkList);//1873 - note 97

				if (SourcingConstants.TECHNICAL_DOCTYPE
						.equalsIgnoreCase(checkList.getDocCategory())) {
					List<ScQtCheckList> technicalCheckLists = this
							.getManagedBean().getScQtCheckSt2techProposal();

					if (currIndex == -1) {
						if (null != technicalCheckLists
								&& !technicalCheckLists.isEmpty()) {
							boolean techSpec = false;
							// Bug #33249@Syazani 27/07/2022 -- Start
							String checkListTitle = checkList.getTitle() + "_"
									+ (checkList.getDefinationType() != null ? checkList.getDefinationType().getParameterId() : "");
							for (int i = 0; i < technicalCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = technicalCheckLists
										.get(i);
								
								String qtCheckListTitle = qtCheckList.getTitle() + "_"
										+ (qtCheckList.getDefinationType() != null ? qtCheckList.getDefinationType().getParameterId() : "");
								if (qtCheckListTitle.equals(checkListTitle)) {
									// Bug #33249@Syazani 27/07/2022 -- End
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
							if(technicalCheckLists == null)
								 technicalCheckLists = new ArrayList<ScQtCheckList>();

							technicalCheckLists.add(checkList);
						}

					} else {
						technicalCheckLists.remove(currIndex);
						technicalCheckLists.add(currIndex, checkList);
					}
					this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2techProposal());

				} else {
					setFinSpecScoreWeightage(checkList);
					List<ScQtCheckList> finanicialCheckLists = this
							.getManagedBean()
							.getScQtCheckSt2financeProposalList();

					if (currIndex == -1) {
						if (isNewTplSpec) {
							if ((null != finanicialCheckLists)
									&& (finanicialCheckLists.size() >= newTplSpecIndex)) {
								finanicialCheckLists.add(newTplSpecIndex,
										checkList);
							} else {
								if(finanicialCheckLists == null)
									 finanicialCheckLists =  new ArrayList<ScQtCheckList>(); 

								finanicialCheckLists.add(checkList);
							}
						} else {
							if(finanicialCheckLists == null)
								 finanicialCheckLists =  new ArrayList<ScQtCheckList>(); 

							finanicialCheckLists.add(checkList);
						}

					} else {
						if (null != finanicialCheckLists
								&& !finanicialCheckLists.isEmpty()) {
							boolean finspec = false;
							// Bug #33249@Syazani 27/07/2022 -- Start
							String checkListTitle = checkList.getTitle() + "_"
									+ (checkList.getDefinationType() != null ? checkList.getDefinationType().getParameterId() : "");
							for (int i = 0; i < finanicialCheckLists.size(); i++) {
								ScQtCheckList qtCheckList = finanicialCheckLists
										.get(i);
								String qtCheckListTitle = qtCheckList.getTitle() + "_"
										+ (qtCheckList.getDefinationType() != null ? qtCheckList.getDefinationType().getParameterId() : "");
								if (qtCheckListTitle.equals(checkListTitle)) {
									// Bug #33249@Syazani 27/07/2022 -- End
									finspec = true;
									finanicialCheckLists.remove(i);
									finanicialCheckLists.add(i, checkList);
								}
							}

							if (!finspec) {
								finanicialCheckLists.add(currIndex, checkList);
							}

						} else {
							if(finanicialCheckLists == null)
								 finanicialCheckLists =  new ArrayList<ScQtCheckList>(); 
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
					checklistSorting(this.getManagedBean()
							.getScQtCheckSt2financeProposalList());
				}
			}
		}

	}

	/**
	 * Complete template spec details.user clicks on complete button in template
	 * spec screens.
	 * 
	 * @param flowType
	 *            ((String flowType)
	 * @param specType
	 *            (String specType)
	 * @return navigate to business owner or specification committee menu.
	 */
	public String completeTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTSpecSO.class);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		ScQt scQt = this.getManagedBean().getQt();//1873 - note 97
		// Bug #33661@Syazani 12/04/2023 -- Start
		IQTDetailSO detailSO = null;
		PmStatus qtStatus = null;
		Long statusId = 0L;
		boolean isItemDeleted = false;//#28784
		boolean issample=this.getManagedBean().getQt().isSampleReq();//11104ReendorseStart728

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			// Bug #33661@Syazani 12/04/2023 -- End
			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
				ScQtSupplierCriteria zoneSupplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (((null != zoneSupplierCriteria && zoneSupplierCriteria
						.isBrandNew() == true)
						|| (getManagedBean().getQt().isZonal()
								&& null != zoneSupplierCriteria
								&& null != zoneSupplierCriteria
										.getQtZoneCriterias() && null == zoneSupplierCriteria
								.getQtZoneCriterias().getSpecZoneId()) || (getManagedBean()
						.getQt().isPanel() && this.getManagedBean()
						.isPanelSpecExistedOrNot()))) {
					scQt = saveZonesBeforeTemplateSpec(getManagedBean().getQt());//1873 - note 97
					// SpecificationReqDTO reqDTO = new SpecificationReqDTO();
					// SpecificationResDTO resDTO = new SpecificationResDTO();
					// reqDTO.setScQt(scQt);
					// resDTO = specSO.saveQTDtl(reqDTO);
					// resDTO = specSO.saveTemplateSpecZoneDtls(reqDTO);
					reqDTO.setZonesQt(true);
					this.getManagedBean().setQtCategoryList(
							new ArrayList<ScQtCategory>());
					// this.getManagedBean().setZoneGeneralTrees(
					// new ArrayList<TreeNode>());
					// this.getManagedBean().setQt(resDTO.getScQt());
					// retriveSpecdetails(resDTO.getScQt().getQtId());
					// retriveSupplierQualificationCriteria("SC");
					// validateBusinessOwner(true);
				}
	
			}
			
			// Bug #33661@Syazani 12/04/2023 -- Start
			qtStatus = detailSO.retrieveActiveStatusByDoctype(this.getManagedBean().getQt().getQtId(),
					ParameterConst.DOCTYPE_QUOTATION);
			if (qtStatus != null)
				statusId = qtStatus.getStatusId();

			if (Arrays.asList(
					new Long[] { QTStatusConst.SE_PENDING_ENDORSMENT, QTStatusConst.SE_PENDING_SIGNOFF,
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
							QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF }).contains(statusId)) {
				commonMessageAcknowledgementErrorPage(MessageConst.ERROR_775189, new String[] { this.getManagedBean()
						.getQt().getQtNo() });
				return null;
			}

			// Bug #33661@Syazani 12/04/2023 -- End
						
			// retriveSpecdetails(this.getManagedBean().getQt().getQtId());
			// retriveBusinessOwnerQualificationCriteri();
			reqDTO.setScQt(scQt);//1873 - note 97
			boolean buildFinancialTemplate = false; //1873 note 86
			ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
			List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
			
			// #29187 Start Vinod
			List<TplSpecQuestion> questionsList = scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions();
			for (TplSpecQuestion question : questionsList) {
				String questionType = question.getQuestionType();
				if (questionType.equalsIgnoreCase(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					question.setMandatorySpec(false);
				}
			}
			// #29187 End Vinod
			//Reopen #25726 note 226 - issue 14
			boolean isTechInsert =false;
			Integer index = null;//1873 - note 97
			if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
				if (null == scQtCheckList.getTitle()
						|| "".equalsIgnoreCase(scQtCheckList.getTitle())) {
					scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
							.getTplSpecMaster().getSpecName());
				}
				List<ScQtCheckList> technicalCheckList = this.getManagedBean()
						.getScQtCheckSt2techProposal();
				List<ScQtCheckList> finanicalAllCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				// Bug #33249@Syazani 24/11/2022 -- Start
				String scQtCheckListTitle = scQtCheckList.getTitle() + "_"
						+ (scQtCheckList.getDefinationType() != null ? scQtCheckList.getDefinationType().getParameterId() : "");
				// Bug #33249@Syazani 24/11/2022 -- End
				if (this.getManagedBean().getIndex() == -1
						&& null == scQtCheckList.getQtChecklistId()) {
					if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
						Integer localChecckListseq = SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC;
						int technicalSpecCount = 0;
						// Bug #33249@Syazani 24/11/2022
						
						for (ScQtCheckList techlist : technicalCheckList) {
							if (QTConstants.TEMPLETE_SPEC_CODE
									.equalsIgnoreCase(techlist.getDefinationType()
											.getParameterCode())) {
								technicalSpecCount++;
								localChecckListseq = localChecckListseq < techlist
										.getChecklistSeq() ? techlist
										.getChecklistSeq() : localChecckListseq;
	
							}
							
							// Bug #33249@Syazani 27/07/2022 -- Start
							String techlistTitle = techlist.getTitle() + "_"
									+ (techlist.getDefinationType() != null ? techlist.getDefinationType().getParameterId() : "");
							if (techlistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtCheckListTitle.trim().replaceAll("\\s", ""))) { // Bug #33249@Syazani 24/11/2022
								// Bug #33249@Syazani 27/07/2022 -- End
								throw new EpException(MessageConst.ERROR_760207,
										new String[] { scQtCheckList.getTitle() });
							}
						}
						log.info("QTID:" + getManagedBean().getQt().getQtId()
								+ "localChecckListseq" + localChecckListseq);
						if (technicalSpecCount > 0) {
							scQtCheckList.setChecklistSeq(localChecckListseq + 1);
						}
					}
					if (null != finanicalAllCheckList
							&& !finanicalAllCheckList.isEmpty()) {
						// Bug #33249@Syazani 27/07/2022 -- Start
						// Bug #33249@Syazani 24/11/2022
						for (ScQtCheckList financiallist : finanicalAllCheckList) {
							String financiallistTitle = financiallist.getTitle() + "_"
									+ (financiallist.getDefinationType() != null ? financiallist.getDefinationType().getParameterId() : "");
							if (null != financiallist.getDefinationType()
									&& !QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financiallist.getDefinationType().getParameterCode())
									&& financiallistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtCheckListTitle.trim().replaceAll("\\s", ""))) { // Bug #33249@Syazani 24/11/2022
								// Bug #33249@Syazani 27/07/2022 -- End
								throw new EpException(MessageConst.ERROR_760207,
										new String[] { scQtCheckList.getTitle() });
							}
						}
					}
					// Bug #33249@Syazani 24/11/2022 -- Start
				} else {
					if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
						for (ScQtCheckList techlist : technicalCheckList) {
							Long qtChecklistId = techlist.getQtChecklistId() != null ? techlist.getQtChecklistId() : 0;
							String techlistTitle = techlist.getTitle() + "_"
									+ (techlist.getDefinationType() != null ? techlist.getDefinationType().getParameterId() : "");
							if (techlistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtCheckListTitle.trim().replaceAll("\\s", ""))
									&& !scQtCheckList.getQtChecklistId().equals(qtChecklistId)) {
								throw new EpException(MessageConst.ERROR_760207, new String[] { scQtCheckList.getTitle() });
							}
						}
					}
					if (null != finanicalAllCheckList && !finanicalAllCheckList.isEmpty()) {
						for (ScQtCheckList financiallist : finanicalAllCheckList) {
							Long qtChecklistId = financiallist.getQtChecklistId() != null ? financiallist.getQtChecklistId() : 0;
							String financiallistTitle = financiallist.getTitle() + "_"
									+ (financiallist.getDefinationType() != null ? financiallist.getDefinationType().getParameterId() : "");
							if (null != financiallist.getDefinationType()
									&& !QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(financiallist.getDefinationType().getParameterCode())
									&& financiallistTitle.trim().replaceAll("\\s", "").equalsIgnoreCase(scQtCheckListTitle.trim().replaceAll("\\s", ""))
									&& !scQtCheckList.getQtChecklistId().equals(qtChecklistId)) {
								throw new EpException(MessageConst.ERROR_760207, new String[] { scQtCheckList.getTitle() });
							}
						}
					}
				}
				// Bug #33249@Syazani 24/11/2022 -- End
				scQtCheckList.setTitle(scQtCheckList.getScQtDoc()
						.getTplSpecMaster().getSpecName());
				scQtCheckList
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				scQtCheckList.getScQtDoc().getTplSpecMaster()
						.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
				scQtCheckList.setSampleStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));//#11104
				//#28784 - Start
				if (null == scQtCheckList.getPmDemoStatus()) {
					scQtCheckList.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));//#28784
				}
				//#28784 - End
				// Code to construct financial checklist..
	
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
	
				TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
						.cloneTplSpecMaster(
								scQtCheckList.getScQtDoc().getTplSpecMaster(),
								false, true, true, false);//#28784
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
	
				buildFinancialTemplate = isSpecQuestionsModified(scQtCheckList);//1873 note 86
				//#28784 - Start
				isItemDeleted = isSpecItemNotExist(scQtCheckList);
				boolean isTemplateSpec = QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(scQtCheckList
						.getDefinationType().getParameterCode());
				boolean isModified = isTemplateSpec && isSpecQuestionsModified(scQtCheckList);
				if(isItemDeleted || isModified){
					scQtCheckList.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}//#28784 - End
				
				if(buildFinancialTemplate){//1873 - note 97
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
				specMaster.setScoringReq(true);
				TemplateSpecHelper.getInstance().cloneTplSpecQuestions(
						specMaster,
						scQtCheckList.getScQtDoc().getTplSpecMaster()
								.getTplSpecQuestions());
				ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
						SourcingConstants.FINANCIAL_DOCTYPE);
				qtCheckList
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION)); //1873 - note 97
				qtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster()
						.getSpecName());
				qtCheckList.setScoringReq(true);
				qtCheckList.getScQtDoc().getTplSpecMaster()
						.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT); //1873 - note 97
				qtCheckList.getScQtDoc().getTplSpecMaster().setWeightageReq(false);
				qtCheckList.setChecklistSeq(scQtCheckList.getChecklistSeq());
				TemplateSpecHelper.getInstance().updateItemWeightageReq(
						qtCheckList.getScQtDoc().getTplSpecMaster(),
						qtCheckList.getScQtDoc().getTplSpecMaster()
								.getTplSpecQuestions());
	
				// copy scoring and estimated cost
				List<ScQtCheckList> toBeRemovedList = validateDuplicateFinancialSpecs(
						qtCheckList, flowType, scQtCheckList);
				if (toBeRemovedList != null && toBeRemovedList.size() > 0) {
					TplSpecMaster oldSpecMaster = toBeRemovedList.get(0)
							.getScQtDoc().getTplSpecMaster();
					ITemplateSpecSO templateSpecSO;
	
					try {
						templateSpecSO = EpEjbBusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
										ITemplateSpecSO.class);
						TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
						specReqDTO.setSpecMaster(oldSpecMaster);
						specReqDTO
								.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
						oldSpecMaster = templateSpecSO
								.getTemplateMaster(specReqDTO).getSpecMaster();
	
						TemplateSpecHelper.getInstance().copySpecDetails(
								specMaster, oldSpecMaster);
	
					} finally {
						templateSpecSO = null;
					}
	
				}
				checkTechnicalFinancialItemCount(scQtCheckList, qtCheckList);
				updatedCheckLists.add(scQtCheckList);
				updatedCheckLists.add(qtCheckList);
				isTechInsert = true;//Reopen #25726 note 226 - issue 14
				List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				//1873 - start - note 97
				if(CollectionUtils.isNotEmpty(finanicalCheckList)){
					index = index == null ? 0: index;
					for(ScQtCheckList finChecklist: finanicalCheckList){
						if(StringUtils.isNotEmpty(finChecklist.getTitle()) 
								&& StringUtils.isNotEmpty(this.getManagedBean().getCheckListTitle())
								&& finChecklist.getTitle().trim().replaceAll("\\s", "").equalsIgnoreCase(this.getManagedBean().getCheckListTitle().trim().replaceAll("\\s", ""))
								&& QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(finChecklist.getDefinationType().getParameterCode())){
							this.getManagedBean().setFinTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(finChecklist));
							finanicalCheckList.remove(finChecklist);
							break;
						}
						index++;
					}
				}
				reqDTO.setTobeRemoveList(toBeRemovedList);
			}else{
				List<ScQtCheckList> finanicalCheckList = this.getManagedBean()
						.getScQtCheckSt2financeProposalList();
				if(CollectionUtils.isNotEmpty(finanicalCheckList)){
					index = index == null ? 0: index;
					for(ScQtCheckList finChecklist: finanicalCheckList){
						if(StringUtils.isNotEmpty(finChecklist.getTitle()) 
								&& finChecklist.getTitle().trim().replaceAll("\\s", "").equalsIgnoreCase(this.getManagedBean().getCheckListTitle().trim().replaceAll("\\s", ""))
								&& QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(finChecklist.getDefinationType().getParameterCode())){
							finChecklist.setCurrentlySelected(true);
							setTemplateSpecMaster(finChecklist);//1873 - note 97
							this.getManagedBean().setFinTempScQtChecklist((ScQtCheckList)SerializationUtils.clone(finChecklist));
							TplSpecMaster finSpecMaster = finChecklist.getScQtDoc().getTplSpecMaster();
							TplSpecMaster tecSpecMaster = scQtCheckList.getScQtDoc().getTplSpecMaster();
							finChecklist.setTitle(tecSpecMaster.getSpecName());
							finSpecMaster.setSpecName(tecSpecMaster.getSpecName());
							finSpecMaster.setItemOriginCode(tecSpecMaster.getItemOriginCode());
							finSpecMaster.setWeightageReq(tecSpecMaster.isWeightageReq());
							scQtCheckList.setEdited(false);
							//1873 - start - note 97
							updatedCheckLists.add(finChecklist); 
							updatedCheckLists.add(scQtCheckList); 
							isTechInsert = true;//Reopen #25726 note 226 - issue 14
							if(CollectionUtils.isNotEmpty(finSpecMaster.getTplSpecQuestions())){
								List<Long> idList = new ArrayList<Long>();
								if(CollectionUtils.isNotEmpty(tecSpecMaster.getFinSpecQuestionsToDelete())){
									for(TplSpecQuestion question: tecSpecMaster.getFinSpecQuestionsToDelete()){
										idList.add(question.getSpecQuestionId());
									}
								}
							    Iterator<TplSpecQuestion> questionIt = finSpecMaster.getTplSpecQuestions().iterator();
							    while (questionIt.hasNext()) {
							    	TplSpecQuestion question = (TplSpecQuestion) questionIt.next();
							    	if(question.getSpecQuestionId()!=null && idList.contains(question.getSpecQuestionId())){
								    	questionIt.remove();
							    	}
							    }
							}
							//1873 - end - note 97
							break;
						} 
						index++;
					} 
				} 
			}
				//1873 - end - note 97
			} else {
				if (this.getManagedBean().getSpecMaintenanceScreen()
						.equalsIgnoreCase("SC")) {
					checkFinancialSpecEstimations(this.getManagedBean()
							.getScQtCheckSt2financeProposalList(), scQtCheckList,
							scQt,false);//20210205@haziq- Bug #30046
					checkFinancialparentSpecQuestion(scQtCheckList);
				}else if(this.getManagedBean().getSpecMaintenanceScreen()
						.equalsIgnoreCase(QTConstants.SPEC_MAINTENANCE)) {//32134-start
					checkFinancialSpecEstimations(this.getManagedBean()
							.getScQtCheckSt2financeProposalList(), scQtCheckList,
							scQt,false);
				}//32134-End
				// Bug #33249@Syazani 27/07/2022 -- Start
				// 1873 - start - note 52
				String finChecklistTitle = (scQtCheckList.getTitle() + "_" + (scQtCheckList.getDefinationType() != null ? scQtCheckList
						.getDefinationType().getParameterId() : "")).trim().replaceAll("\\s", "");// 1873 - note 97
				// 1873 note 86 - start
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2techProposal())) {
					for (ScQtCheckList checklist : this.getManagedBean().getScQtCheckSt2techProposal()) {
						boolean edited = checklist.isEdited() == null ? false : checklist.isEdited();
						String checklistTitle = (checklist.getTitle() + "_" + (checklist.getDefinationType() != null ? checklist.getDefinationType()
								.getParameterId() : "")).trim().replaceAll("\\s", "");
						if (finChecklistTitle.equals(checklistTitle) && edited) {// 1873 - note 97
							// Bug #33249@Syazani 27/07/2022 -- End
							checklist.setEdited(false);
							updatedCheckLists.add(checklist);
							isTechInsert = true; //Reopen #25726 note 226 - issue 14
							break;
						}
				//1873 note 86 - end
					}
				}
				//1873 - end - note 52
				scQtCheckList
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				scQtCheckList
						.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				scQtCheckList.getScQtDoc().getTplSpecMaster()
						.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
				updatedCheckLists.add(scQtCheckList);
	
				reqDTO.setScQt(scQt);
				
			}
			//Reopen #25726 note 226 - issue 14
			checkAndupdateFulfilmentType(updatedCheckLists,scQtCheckList,isTechInsert,issample);//11104-778&25726
			reqDTO.setCheckLists(updatedCheckLists);
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			resetCommitteeActivity(reqDTO);
			reqDTO.setSpecChanged(getSpecMaintenance().equalsIgnoreCase(
					this.getManagedBean().getSpecMaintenanceScreen()));
			// code if alredy checklist exist need to remove existing finalcialspec
	
			/*
			 * IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
			 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
			 * IQTSpecSO.class);
			 */

			
			// 29521 - start
			if (null == reqDTO.getBackupScQt()) {
				reqDTO.setBackupScQt(retrieveScqtdetails());
			}
			if (null == reqDTO.getListBackupCategoryCode()) {
				reqDTO.setListBackupCategoryCode(retrieveCurrentScQtCategoryCode());
			}
			if (null == reqDTO.getListNewCategoryCode()) {
				reqDTO.setListNewCategoryCode(getManagedBean().getQtCategoryList());
			}
			if (null == reqDTO.getPmUser()) {
				FacesScopes fs = new FacesScopes();
				reqDTO.setPmUser(fs.getUserSessionBean());
			}
	
			validateZonalChanges();
			// 29521 - end
			
			// #34656 remove #33860
			
			SpecificationResDTO resDTO = new SpecificationResDTO();
			try {
				resDTO = specSO.saveOrUpdateTemplateSpecs(reqDTO);
			} 
			//1873 -  start - note 97
			catch (Exception e) {
				this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
				ScQtCheckList finChecklist = this.getManagedBean().getFinTempScQtChecklist();
				if(finChecklist!=null 
						&& CollectionUtils.isNotEmpty(this.getManagedBean().getScQtCheckSt2financeProposalList())
						&& index !=null){
					if(finChecklist.isCurrentlySelected()){
						this.getManagedBean().getScQtCheckSt2financeProposalList().set(index, this.getManagedBean().getFinTempScQtChecklist());
						finChecklist.setCurrentlySelected(false);
					}else{
						this.getManagedBean().getScQtCheckSt2financeProposalList().add(index, this.getManagedBean().getFinTempScQtChecklist());
					}
				}
				resDTO.setSuccessful(false);
				if(e instanceof EpException){
					String msgCode = e.getMessage();
					String msgParam[] = ((EpException) e).getParameters();
					if (MessageConst.ERROR_760460.equalsIgnoreCase(msgCode)) {
						msgCode = MessageConst.ERROR_760295;
						msgParam = new String[] { "Tier Quantity" };
					}
					throw new EpException(msgCode, msgParam);
				}
				throw e;
			}finally{
				this.getManagedBean().setFinTempScQtChecklist(null);
				//Bug #34100 - Start
				this.getManagedBean()
				.getScQtChecklist()
				.setPmStatus(
						getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				//Bug #34100 - End
			}
			//1873 - end - note 97
	
			// Need to redirect particualr page..
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setTempScQtChecklist(null);//1873 - note 97
				ScQt qt = resDTO.getScQt();
				if (qt != null) {
					this.getManagedBean().setQt(qt);
				this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
					if (null != this.getManagedBean().getCtContract()) {
						this.getManagedBean()
								.getCtContract()
								.setContractNo(
										this.getManagedBean().getQt()
												.getExistingContractNo());
					}
					if (qt.getEstimatedAmount() != null
							&& qt.getEstimatedAmount().intValue() > 0
							&& SourcingConstants.FINANCIAL_DOCTYPE
									.equalsIgnoreCase(specType)) {
						new CreateQtBackingBean().setProcurementMode(qt,
								qt.getEstimatedAmount());
						// 20190415@asyrafhaziq - 15979 -Start
						new CreateQtBackingBean().setOpenFor(qt,
								qt.getEstimatedAmount());
						// 20190415@asyrafhaziq - 15979 -End
					}
				}
	
				List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
			settemplateSpecList(savedCheckLists, flowType,specType); //#11104-778&25726
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setFinancialScoring(null);
				this.getManagedBean().setOfferPricePackage(null);
				this.getManagedBean().setPanelSpecExistedOrNot(false);
				this.getManagedBean().setOfferPricePackageList(null);//33587
				specCommitteeResetData();
				if (reqDTO.isZonesQt()
						|| (this.getManagedBean().getQt().isZonal() && SourcingConstants.FINANCIAL_DOCTYPE
								.equalsIgnoreCase(specType))) {
					this.getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
				}
			this.getManagedBean().getQt().setSampleReq(issample);//11104ReendorseStart728
				saveSpecMaintenanceWorkFlowStatus();
				addDemoTechnicalCheckList();//#28784 - Start
				if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {
					resetDemoSpecStatus();// #28784

					boolean isModified = false;
					boolean isSpecNotExist = false;
					SpecificationResDTO resDTO1 = new SpecificationResDTO();
					SpecificationReqDTO reqDTO1 = new SpecificationReqDTO();
					boolean isTemplateSpec = QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(scQtCheckList
							.getDefinationType().getParameterCode());
					isModified = isTemplateSpec && isSpecQuestionsModified(scQtCheckList);
					isSpecNotExist = isSpecQuestionNotExist(scQtCheckList);
					List<ScQtCheckList> updatedCheckListsDemo = new ArrayList<ScQtCheckList>();
					if (isModified || !isTemplateSpec || isSpecNotExist || isItemDeleted) {//#28784
						List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
						for (ScQtCheckList t : st2technicalproposalList) {
							if (t.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)
									&& null != t.getQtChecklistId()
									&& t.getQtChecklistId().equals(scQtCheckList.getQtChecklistId())) {// #28784 exclude demo & add null condition
								t.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));// #28784
							}

							if (t.getDefinationType().getParameterCode()
									.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
								t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
								t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
								updatedCheckListsDemo.add(t);
								// break;
							}

							if (updatedCheckListsDemo != null) {
								reqDTO1.setCheckLists(updatedCheckListsDemo);
								resDTO1 = specSO.completeDemoChecklist(reqDTO1);
								
								if (resDTO1.isSuccessful()) {
									List<ScQtCheckList> savedCheckListsDemo = resDTO1.getSavedCheckLists();
									for (ScQtCheckList obj : savedCheckListsDemo) {
										resetDemoChecklist(obj);
									}
								}
							}							
						}
					}
				}//#28784 - End
	            if(issample && SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)){// 11104-Reendose794
				sampleDetailsPendingTemplateSpec();
			   }// 11104-Reendose794
	          //#36678 start
				boolean isPanel = scQt.isPanel();
	    		BigDecimal currentIndPrice = this.getManagedBean().getQt().getEstimatedAmount();
	    		BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();
				if ((!isPanel) && currentIndPrice != null && indPriceThreshold != null
						&& (currentIndPrice.compareTo(indPriceThreshold) <= 0)) {
					// BugBug #35061 - asyrafhaziq
					// set message
					String msg = getMessage(SourcingConstants.ALERT_MESSAGE_BELOW_QUATATION_VALUE, new String[] { String
							.valueOf(indPriceThreshold).trim() });
					// SONARQUBE QT 2023 - haziq
					this.getManagedBean().setMsgConfirmBelowIndi(msg.trim());

					RequestContext.getCurrentInstance().execute("proceedBelowQtValueDlgs.show()");

				} else {
					return QTConstants.SPEC_COMMITTEE_MAIN_PAGE;
				}
				//#36678 end
			}
			return null;
		// Bug #33661@Syazani 12/04/2023 -- Start
		} finally {
			detailSO = null;
		}
		// Bug #33661@Syazani 12/04/2023 -- End
	}

	private List<ScQtCheckList> validateDuplicateFinancialSpecs(
			ScQtCheckList financialSpecs, String flowType,
			ScQtCheckList technicalSpec) {
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		List<ScQtCheckList> toBeRemovedList = new ArrayList<ScQtCheckList>();

		try {
			if (null == financialSpecs.getQtChecklistId()
					|| financialSpecs.isBrandNew()) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				reqDTO.setPrepareSpec(false);

				specSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
				resDto = specSO.retrieveSpecDetails(reqDTO);

				if (null != resDto
						&& null != resDto.getFinSpecResultBean()
								.getResultList()
						&& !resDto.getFinSpecResultBean().getResultList()
								.isEmpty()) {
					if (null != this.getManagedBean().getCheckListTitle()
							&& !"".equals(this.getManagedBean()
									.getCheckListTitle())) {
						for (ScQtCheckList financialspec : resDto
								.getFinSpecResultBean().getResultList()) {
							if (financialspec
									.getTitle()
									.trim()
									.replaceAll("\\s", "")
									.equalsIgnoreCase(
											this.getManagedBean()
													.getCheckListTitle().trim()
													.replaceAll("\\s", ""))) {
								log.info("validateDuplicateFinancialSpecsTitlesEqual"
										+ this.getManagedBean().getQt()
												.getQtId());
								financialspec.setDeleted(true);
								toBeRemovedList.add(financialspec);
								break;
								/*
								 * throw new
								 * EpException(MessageConst.ERROR_760310, new
								 * String[] { financialSpecs.getTitle() });
								 */
							}
						}
					}
					if (CollectionUtils.isEmpty(toBeRemovedList)) {
						if (!checkSequenceMorethanOne(resDto
								.getFinSpecResultBean().getResultList(),
								technicalSpec)) {
							for (ScQtCheckList financialspec : resDto
									.getFinSpecResultBean().getResultList()) {
								if (financialspec.getChecklistSeq().equals(
										technicalSpec.getChecklistSeq())) {
									financialspec.setDeleted(true);
									toBeRemovedList.add(financialspec);
									log.info("validateDuplicateFinancialSpecsSequencesAreEqualForDelete"
											+ this.getManagedBean().getQt()
													.getQtId());
									break;
									/*
									 * throw new
									 * EpException(MessageConst.ERROR_760310,
									 * new String[] { financialSpecs.getTitle()
									 * });
									 */
								}
							}
						}

					}
					if (CollectionUtils.isEmpty(toBeRemovedList)) {
						for (ScQtCheckList financialspec : resDto
								.getFinSpecResultBean().getResultList()) {
							if (financialspec.getRecordStatusFlg().equals(
									DbValueConst.RECORD_STATUS_DELETED)) {
								log.info("validateDuplicateFinancialSpecsRecordStatusDeleteOnly"
										+ this.getManagedBean().getQt()
												.getQtId());
								financialspec.setDeleted(true);
								toBeRemovedList.add(financialspec);
								break;
								/*
								 * throw new
								 * EpException(MessageConst.ERROR_760310, new
								 * String[] { financialSpecs.getTitle() });
								 */
							}
						}
					}
					//1873 -  start - note 97
//					if (null != toBeRemovedList && !toBeRemovedList.isEmpty()) {
//						removeDuplicateFinancialSpec(flowType, toBeRemovedList);
//					}
					//1873 -  end - note 97
				}
			}
		} finally {
			specSO = null;
		}

		return toBeRemovedList;
	}

	/*
	private void removeDuplicateFinancialSpec(String flowType, List<ScQtCheckList> toBeRemoveCheckLists) {
		IQTSpecSO specSo = null;
		try {
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			specReqDTO.setCheckLists(toBeRemoveCheckLists);
			specReqDTO.setScQt(this.getManagedBean().getQt());
			SpecificationResDTO resDTO = specSo.deleteChecklists(specReqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().getQt()
						.setEstimatedAmount(resDTO.getEstimatedamount());
				this.getManagedBean().getQt()
						.setDecryptedEstAmt(resDTO.getDecryptedAmount());
				this.getManagedBean().setScQtCheckSt2financeProposalList(null);
				this.getManagedBean().setFinancialScoring(null);
				retriveFinancialCheckList();
			}
		} finally {
			specSo = null;
		}
	}
	*/

	/**
	 * Creates the template spec.user clicks on create or clone buttons in
	 * template spec popup screen.
	 * 
	 * @param tplSpecMaster
	 *            (TplSpecMaster tplSpecMaster)
	 * @param flowtype
	 *            (String flowtype)
	 * @return navigate to template spec screens.
	 */
	public String createTemplateSpec(TplSpecMaster tplSpecMaster,
			String flowtype) {
		this.getManagedBean().setSpecSelectStats(false);
		this.getManagedBean().setSpecNewable(true);
		this.getManagedBean().setUpdateSpec(true);
		this.getManagedBean().setSpecItemWeightageRequired(false);
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
			
			//#28784 - Adding demoSpec clone condition			
			TplSpecMaster specMaster = TemplateSpecHelper.getInstance()
					.cloneTplSpecMaster(resDTO.getSpecMaster(), true,
							getManagedBean().getQt().isTechEvaluationReq(),
							true, true);//#28784
			
			//Danial - #8979 - if partial, no need to set weightage
			if(this.getManagedBean().getQt().isPartial()){
				//set null
				for(TplSpecQuestion q:specMaster.getTplSpecQuestions()){
					q.setWeightageReq(false);
					q.setWeightage(null);
				}
			}
			
			specMaster
					.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);
			if (getManagedBean().getQt().isZonal()) {
				ScQtSupplierCriteria supplierCriteria = getManagedBean()
						.getQt().getScQtSupplierCriteria();
				if (null != supplierCriteria
						&& null != supplierCriteria.getQtZoneCriterias()) {
					TemplateSpecHelper.getInstance().updateZoneDtl(specMaster,
							supplierCriteria.getQtZoneCriterias());
				} else {
					throw new EpException(
							"Please choose the zones before proceed.");
				}
			} else {
				TemplateSpecHelper.getInstance()
						.updateZoneDtl(specMaster, null);
			}

			ScQtCheckList qtCheckList = constructQtDocByType(specMaster,
					SourcingConstants.TECHNICAL_DOCTYPE);

			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
				boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
				this.getManagedBean().setSpecItemWeightageRequired(
						istechnicalSpec);
				boolean weightageType = retriveExistingTemplatespec(technicalCheckList);
				if(istechnicalSpec){//#8979-start
					specMaster.setWeightageReq(weightageType);
				}//#8979-End
				templateSpecBean.setSpecMaster(specMaster);
				TemplateSpecBackingBean templateSpecBackingbean = new TemplateSpecBackingBean();
				templateSpecBackingbean
						.updateItemWeightageReq(templateSpecBean);

			} else {
				this.getManagedBean().setSpecItemWeightageRequired(false);
			} 
						
			templateSpecBean.setSpecMaster(specMaster);

			this.getManagedBean().setScQtChecklist(qtCheckList);
			this.getManagedBean().setIndex(-1);
			this.getManagedBean().setAutoUpdate(true);
			
			// #21695 farid, if partial set weightage to false and disable, always set to direct scoring		
						if (this.getManagedBean().getQt().isPartial() == true){
							this.getManagedBean().setSpecItemWeightageRequired(true);
							if(this.getManagedBean().getTemplateSpecBean() != null){
								if(this.getManagedBean().getTemplateSpecBean().getSpecMaster() != null){
									this.getManagedBean().getTemplateSpecBean().getSpecMaster().setWeightageReq(false);
									this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions();
									if(this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions() != null){
										for(TplSpecQuestion specQuestWeightage : this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions()){
											if(specQuestWeightage!=null){
												specQuestWeightage.setWeightageReq(false);
											}
										}
									}
									
								}
							}
						}
			//END #21695	
			
			templateSpecSO = null;
			RequestContext.getCurrentInstance().execute(
					"createOwnerSpec.hide()");
			// RequestContext.getCurrentInstance().update("btnId");
		} finally {

		}
		addDemoTechnicalCheckList();//#28784
		resetDemoSpecStatus();//#28784
		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setNewTemplateSpec(true);
		return "/xhtml/sc/boSctemplatespec/technicalCommitteeTemplate.xhtml?faces-redirect=true";
	}

	/**
	 * #28784,Check Demo status base on tech spec status, if spec pending =demo pending
	 * @param none
	 * @return none
	 */
	private void resetDemoSpecStatus() {	
		//#28784 Demo WIP - Start
		if (this.getManagedBean().isNewVersion()) {
			boolean isTechSpecPending = false;
			List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
			for (ScQtCheckList t : st2technicalproposalList) {
				boolean isDemoStatusSubmit = t.getPmDemoStatus() != null && QTStatusConst.TS_PENDING_SUBMISSION.equals(t.getPmDemoStatus().getStatusId());
				if (t.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)
						&& (QTStatusConst.TS_PENDING_SUBMISSION.equals(t.getPmStatus().getStatusId()) || 
								isDemoStatusSubmit)) {
					isTechSpecPending = true;
					t.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));//#28784
					}

				if (isTechSpecPending && t.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
					t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					break;
				}
			}
		}
		//#28784 Demo WIP - End
	}

	/**
	 * Construct qt doc by type.
	 * 
	 * @param specMaster
	 *            the spec master
	 * @param docType
	 *            the doc type
	 * @return the sc qt check list
	 */
	private ScQtCheckList constructQtDocByType(TplSpecMaster specMaster,
			String docType) {
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ScQtDoc scqtDocDest = new ScQtDoc();
		setUserDetails(scqtDocDest);
		specMaster.setStatus(getPmStatusByStatusId(QTStatusConst.BSV_PENDING));
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
		scqtDocDest.setScQt(this.getManagedBean().getQt());
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
		checkList.setScoringReq(this.getManagedBean().getQt()
				.isTechEvaluationReq());
		checkList.setAction(checkListactionresDTO.getPmParameter());
		checkList.setScQt(this.getManagedBean().getQt());
		checkList.setDefinationType(specTyperesDTO.getPmParameter());
		checkList
				.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		checkList
				.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		setUserDetails(checkList);
		scqtDocDest.setScQtCheckList(checkList);
		parameterSo = null;
		return checkList;
	}

	/**
	 * Evaluation type change.change evaluation type radio in business owner.
	 */
	public void evaluationTypeChange() {
		techEvaluationReqChanged();
	}

	/**
	 * Tech evaluation req changed.change Required Technical Evaluation radio in
	 * business owner.
	 */
	public void techEvaluationReqChanged() {
		boolean isTechEvaluationReq = getManagedBean().getQt()
				.isTechEvaluationReq();

		// Get list of technical checklist to set score req.
		if (null != getManagedBean().getQt().getScQtSupplierCriteria()
				&& null != getManagedBean().getQt().getScQtSupplierCriteria()
						.getQtSupplierCriteriaId()) {
			retriveTechnicalSpecification();
		}
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

	/**
	 * Update selected check list indx.
	 * 
	 * @param checkList
	 *            the check list
	 * @param index
	 *            the index
	 * @throws Exception
	 */
	public void updateSelectedCheckListIndx(ScQtCheckList checkList, int index)
			throws Exception {
		if (checkList.isSelected()) {
			checkList.setIndex(index);
		} else {
			checkList.setIndex(-1);
		}
		int count = 0;
		if (checkList.getPmDocument() != null) {
			if (checkList.getPmDocument().getDocGroupId() != null) {
				for (ScQtCheckList scQtCheckList : this.getManagedBean()
						.getScQtCheckSt2financeProposalList()) {
					if (scQtCheckList.getPmDocument() != null && index != count) {
						if (checkList.getPmDocument().getDocGroupId() == scQtCheckList
								.getPmDocument().getDocGroupId()) {
							if (this.getManagedBean()
									.getScQtCheckSt2financeProposalList()
									.get(count).isSelected()) {
								// scQtCheckList.setSelected(false);
								this.getManagedBean()
										.getScQtCheckSt2financeProposalList()
										.get(count).setSelected(false);
								this.getManagedBean()
										.getScQtCheckSt2financeProposalList()
										.get(count).setIndex(-1);
							} else {
								// scQtCheckList.setSelected(true);
								this.getManagedBean()
										.getScQtCheckSt2financeProposalList()
										.get(count).setSelected(true);
								this.getManagedBean()
										.getScQtCheckSt2financeProposalList()
										.get(count).setIndex(count);
							}
						}
					}
					count++;
				}
			}
		}
	}

	/**
	 * Gets the bank years display dtls.display bankstatement checklist years
	 * dropdown values.
	 * 
	 * @return List<SelectItem>.
	 */
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
			selectItems = null;

		} finally {

		}
		return this.getManagedBean().getBankyearsList();
	}

	/**
	 * Delete check list attachment file.delete uploaded checklist attachemnt
	 * from Technical and Financial Checklist menu.
	 * 
	 * @param checkListAttachment
	 *            (ScQtCheckListAttachment checkListAttachment).
	 */
	public void deleteCheckListAttachmentFile(BaseEntity delEntity) {

		this.getManagedBean().getScQtChecklist()
				.setScQtCheckListAttachment(null);
		if (null != this.getManagedBean().getScQtChecklist()
				&& null == this.getManagedBean().getScQtChecklist()
						.getScQtCheckListAttachment()) {
			this.getManagedBean()
					.getScQtChecklist()
					.setPmStatus(
							getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

		}

	}

	/**
	 * Retrieve sm mof account.retrive suupplier information based on supplier
	 * in restricted tender tab.
	 * 
	 * @param scQtSupplier
	 *            the sc qt supplier
	 * @param smSupplier
	 *            the sm supplier
	 * @return SmMofAccount.
	 */
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

	/**
	 * Show supplier details.retrive suupplier address based on supplier in
	 * restricted tender tab
	 * 
	 * @param qtSupplier
	 *            (ScQtSupplier qtSupplier).
	 */
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
					log.debug("found hq address");
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

	/**
	 * Retrive one tier check list.retrive all technical Specs and display in
	 * Prepare Specification menu.
	 */
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
				reqDTO.setScQt(this.getManagedBean().getQt());
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

	/**
	 * technical checklist score change.Event handle check/Uncheck score
	 * checkbox in technical checklist based on score it will display
	 * specification status.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist).
	 */

	public void technicalCheckListScoreReqChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		if (((null != list.getDefinationType()) && (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
				.equalsIgnoreCase(list.getDefinationType().getParameterCode())
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode())
				&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()) && !ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO
					.equalsIgnoreCase(list.getDefinationType()
							.getParameterCode())))) {
			Long statusId = null;
			if (list.isScoringReq()) {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			} else if (!list.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			}
			list.setPmStatus(getPmStatusByStatusId(statusId));
		}

		this.getManagedBean().getScQtCheckSt2techProposal().set(index, list);
	}

	/**
	 * financial checklist score change.Event handle check/Uncheck score
	 * checkbox in financial checklist based on score it will display
	 * specification status.
	 * 
	 * @param index
	 *            (int index)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @param checklist
	 *            (List<ScQtCheckList> checklist).
	 */

	public void financialCheckListScoreReqChange(int index,
			List<ScQtCheckList> checklist) {
		ScQtCheckList list = checklist.get(index);

		if ((null != list.getDefinationType())
				&& (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()) && !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
						.equalsIgnoreCase(list.getDefinationType()
								.getParameterCode()))) {
			Long statusId = null;
			if (list.isScoringReq()) {
				statusId = QTStatusConst.TS_PENDING_SUBMISSION;
			} else if (!list.isScoringReq()) {
				statusId = QTStatusConst.TS_COMPLETED;
			}
			list.setPmStatus(getPmStatusByStatusId(statusId));
		}

		this.getManagedBean().getScQtCheckSt2financeProposalList()
				.set(index, list);
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the count.
	 * 
	 * @param count
	 *            the new count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Method to check all the state/districts in data table.
	 * 
	 * @param event
	 *            (boolean true/false)
	 */
	public void checkAllConditions(AjaxBehaviorEvent event) {
		boolean flag = ((SelectBooleanCheckbox) event.getSource()).isSelected();
		List<TreeNode> list = getManagedBean().getZoneTrees();
		if (null != list) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {
						for (TreeNode node2 : childNode.getChildren()) {
							node2.setSelected(flag);
						}
					}
					childNode.setSelected(flag);
				}
			}
			getManagedBean().setZoneTrees(list);
		} else {

		}
	}

	/**
	 * Method to check all the state/districts in data table in dialog.
	 * 
	 * @param isCheck
	 *            (boolean true/false).
	 */
	public void checkAllPopUpConditions(boolean ischeck) {
		// boolean flag = ((SelectBooleanCheckbox)
		// event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootLocationTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(ischeck);
					}
				}
				stateNode.setSelected(ischeck);
			}
			getManagedBean().setRootLocationTree(treeNode);
		} else {

		}
	}

	/**
	 * Method to check all the state/districts in data table in dialog.
	 * 
	 * @param isCheck
	 *            (boolean true/false).
	 */
	public void checkAllZoneGeneralPopUp(boolean ischeck) {
		// boolean flag = ((SelectBooleanCheckbox)
		// event.getSource()).isSelected();
		TreeNode treeNode = getManagedBean().getRootZoanlTree();
		if (treeNode != null) {
			for (TreeNode stateNode : treeNode.getChildren()) {
				if (stateNode.getChildCount() > 0) {
					for (TreeNode districtNode : stateNode.getChildren()) {
						districtNode.setSelected(ischeck);
					}
				}
				stateNode.setSelected(ischeck);
			}
			getManagedBean().setRootZoanlTree(treeNode);
		} else {

		}
	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param templateQtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 */
	private void populateQTSearchCriteria(
			QtnTenderSearchBean templateQtnTenderSearchBean,
			ISearchBean<TplSpecMaster> qtSearchBean) {

		if (templateQtnTenderSearchBean != null) {
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = templateQtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {

				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtTitle",
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtTitle",
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			String qtNumber = templateQtnTenderSearchBean.getQtNo();
			if (!StringUtils.isBlank(qtNumber)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.qtNo",
								SearchOperator.OPERATOR_LIKE.value(), qtNumber,
								true));
			}
			String procurementMode = templateQtnTenderSearchBean
					.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"scQtDoc.scQt.procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}
			Date fromCreatedDate = templateQtnTenderSearchBean
					.getFromCreatedDate();
			Date toCreatedDate = templateQtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQtDoc.scQt.createdDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromPublishDate = templateQtnTenderSearchBean
					.getFromPublishDate();
			Date toPublishDate = templateQtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQtDoc.scQt.publishDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = templateQtnTenderSearchBean
					.getFromClosingDate();
			Date toClosingDate = templateQtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("scQtDoc.scQt.closingDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			String status = templateQtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)) {
				qtSearchBean
						.getDataFilterBeans()
						.add(new SearchFilterBean(
								"scQtDoc.scQt.scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_EQUALS.value(), status));
				qtSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("scQtDoc.scQt.scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(), true));
			}
			//#29468 Vinod Start
			List<Long> l1Ids = new ArrayList<>();
			List<Long> l2Ids = new ArrayList<>();
			List<Long> l3Ids = new ArrayList<>();
			
			Iterator<Map.Entry<String, Map<Long, Map<Long, Map<Long, String>>>>> mapCat = getManagedBean().getMapCatCode().entrySet().iterator();
			while (mapCat.hasNext()) {
				Map.Entry<String, Map<Long, Map<Long, Map<Long, String>>>> mainMap = mapCat.next();
				if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedCategories())
						&& mainMap.getKey() != null && mainMap.getKey() != ""
						&& getManagedBean().getSelectedCategories().contains(mainMap.getKey())) {

					Iterator<Entry<Long, Map<Long, Map<Long, String>>>> l3It = (mainMap.getValue()).entrySet()
							.iterator();
					Entry<Long, Map<Long, Map<Long, String>>> l3CatMap = l3It.next();
					Iterator<Map.Entry<Long, Map<Long, String>>> l2It = (l3CatMap.getValue()).entrySet().iterator();
					Map.Entry<Long, Map<Long, String>> l2CatMap = l2It.next();
					Iterator<Map.Entry<Long, String>> l1it = (l2CatMap.getValue()).entrySet().iterator();
					Map.Entry<Long, String> l1CatMap = l1it.next();

					l1Ids.add(l1CatMap.getKey());
					l2Ids.add(l2CatMap.getKey());
					l3Ids.add(l3CatMap.getKey());
				}
			}
			if (CollectionUtils.isNotEmpty(l1Ids) && CollectionUtils.isNotEmpty(l2Ids)
					&& CollectionUtils.isNotEmpty(l3Ids)) {
				qtSearchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchMultiFilterBean(new SearchFilterBean(
								QTSearchBeanConst.SEARCH_SCQTDOC_SCQT_SCQTCATEGORYS_PMCATEGORYL1, SearchOperator.OPERATOR_IN.value(),
								l1Ids), new SearchFilterBean(QTSearchBeanConst.SEARCH_SCQTDOC_SCQT_SCQTCATEGORYS_PMCATEGORYL2,
								SearchOperator.OPERATOR_IN.value(), l2Ids)), new SearchFilterBean(
								QTSearchBeanConst.SEARCH_SCQTDOC_SCQT_SCQTCATEGORYS_PMCATEGORYL3, SearchOperator.OPERATOR_IN.value(),
								l3Ids, SearchCondition.CONDITION_AND.value())));
			}
			//#29468 Vinod End
			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
		}
	}

	/**
	 * this method is used to check the technical passing score is less than
	 * zero or greater than 100.
	 * 
	 * 
	 * 
	 */
	private void validateTechnicalPassingScoring(
			ScQtScoreSetting technicalScoreSetting,
			ScQtScoreSetting technicalItemScore) {
		// #21695 START FARID
		try {
			if (null != technicalScoreSetting
					&& technicalScoreSetting.getSettingType() == 1) {

				if (null == technicalScoreSetting.getPassingScore()) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760192);
				} else {

					if (technicalScoreSetting.getPassingScore().intValue() <= 0) {
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760234);
					}

				}

				if (null != technicalScoreSetting.getPassingScore()
						&& technicalScoreSetting.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_760142);
				}
				BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_PASSING_SCORE);

				if (null != passingScore
						&& (technicalScoreSetting.getPassingScore().intValue() < passingScore
								.intValue())) {
					throw new EpException(MessageConst.ERROR_760364,
							new String[] { String.valueOf(passingScore) });
				}
			}
			// #21695 START FARID
			if (null != technicalItemScore
					&& technicalItemScore.getSettingType() == 7) {
				if (null == technicalItemScore.getPassingScore()) {

					this.getManagedBean().setActiveIndex(4);
					//#33593 related to #21695 Start FARID
					throw new EpException(MessageConst.ERROR_760259);
					//#33593 related to #21695 Start FARID

				}
				//#33593 related to #21695 Start FARID
				else if(this.getManagedBean().getTechnicalItemScoreSetting().getPassingScore().compareTo(new BigDecimal(50)) < 0){
					this.getManagedBean().setActiveIndex(4);
					throw new EpException(MessageConst.ERROR_775155);
				}
				//#33593 related to #21695 End FARID
			}
			// #21695 END FARID
		} finally {

		}
	}

	/**
	 * this method is used to check the demo passing score is less than zero or
	 * greater than 100.
	 * 
	 * 
	 * 
	 */
	private void validateTechnicalDemoPassingScoring(
			ScQtScoreSetting demoScoreSetting) {
		try {
			if (null != demoScoreSetting
					&& demoScoreSetting.getSettingType() == 3) {
				if (this.getManagedBean().getQt().isDemoRequired()
						&& this.getManagedBean()
								.getQt()
								.getEvaluationType()
								.equalsIgnoreCase(
										ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
					if (null == demoScoreSetting.getPassingScore()) {
						// demoScoreSetting.setPassingScore(new BigDecimal(0));
						this.getManagedBean().setActiveIndex(4);
						throw new EpException(MessageConst.ERROR_760173);
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

			}

		} finally {

		}

	}

	/**
	 * this method is used to check the financial package and item passing score
	 * is less than zero or greater than 100.
	 * 
	 * 
	 * 
	 */
	private void validateFinancialPackageItemPassingScoring(
			ScQtScoreSetting financialPackageScore,
			ScQtScoreSetting financialItemScore) {
		try {
			if (null != financialPackageScore
					&& financialPackageScore.getSettingType() == 2) {
				//#25138 - add if condition for one item only - start
				if(!this.getManagedBean().isSingleItem()){
					if (null == financialPackageScore.getPassingScore()) {

						this.getManagedBean().setActiveIndex(5);
						throw new EpException(MessageConst.ERROR_760193);
					} else {

						if (financialPackageScore.getPassingScore().intValue() < 0) {
							this.getManagedBean().setActiveIndex(5);
							throw new EpException(MessageConst.ERROR_760174);
						}

					}
				}
				//#25138 - add if condition for one item only - end

				if (null != financialPackageScore.getPassingScore()
						&& financialPackageScore.getPassingScore().intValue() > 100) {
					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760143);
				}

			}

			if (null != financialItemScore
					&& financialItemScore.getSettingType() == 6) {
				if (null == financialItemScore.getPassingScore()) {

					this.getManagedBean().setActiveIndex(5);
					throw new EpException(MessageConst.ERROR_760258);

				}

			}

		} finally {

		}

	}

	/**
	 * this method is used to delete the checklist scores in pmdocuments
	 * checklists.
	 * 
	 * 
	 * 
	 */
	public void deleteCheckListScoreContrats() {

		markDeleteSelectedRowsAction(this.getManagedBean().getCheckListScore(),
				this.getManagedBean().getToBeRemoveList());

	}

	/**
	 * this method is used to delete the checklist scores in assets liability
	 * checklist.
	 * 
	 * 
	 * 
	 */
	public void deleteCheckListScoreYears() {
		markDeleteSelectedRowsAction(
				this.getManagedBean().getScoringForYears(), this
						.getManagedBean().getToBeRemoveList());
	}

	/**
	 * this method is used to delete the checklist in technical checklist side
	 * menu.
	 * 
	 * 
	 * 
	 */
	public void deleteTechnicalCheckList() {
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		if (null != this.getManagedBean().getScQtCheckSt2techProposal()
				&& !this.getManagedBean().getScQtCheckSt2techProposal()
						.isEmpty()) {
			for (ScQtCheckList technicalCheckList : this.getManagedBean()
					.getScQtCheckSt2techProposal()) {
				if (null == technicalCheckList.getPmDocument()
						&& !QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(technicalCheckList.getDefinationType().getParameterCode()) 
						&& !QTConstants.PRESENTATION_DEMO.equalsIgnoreCase(technicalCheckList.getDefinationType().getParameterCode())) {//#28784
					if (null != technicalCheckList.getDefinationType()
							&& null == technicalCheckList.getDefinationType()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						technicalCheckList.setDefinationType(docDefTypeResDTO
								.getPmParameter());
					}
					if (null != technicalCheckList.getAction()
							&& null == technicalCheckList.getAction()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						technicalCheckList.setAction(docDefTypeResDTO
								.getPmParameter());
					}

				}
			}
		}
		List<ScQtCheckList> deleteTechCheckList = markDeleteSelectedRowsActionById(
				this.getManagedBean().getScQtCheckSt2techProposal(), null);
		List<ScQtCheckList> technical = this.getManagedBean()
				.getMarkDeletedTechChecklist();
		if (null == technical) {
			technical = new ArrayList<ScQtCheckList>();
		}
		if (null != deleteTechCheckList && !deleteTechCheckList.isEmpty()) {
			technical.addAll(deleteTechCheckList);
			this.getManagedBean().setMarkDeletedTechChecklist(technical);
		}
		parameterSo = null;
		checklistSorting(getManagedBean().getScQtCheckSt2techProposal());
	}

	/**
	 * this method is used to delete the checklist in financial checklist side
	 * menu.
	 * 
	 * 
	 * 
	 */
	public void deleteFinancialCheckList() {
		IParameterSO parameterSo = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		if (null != this.getManagedBean().getScQtCheckSt2financeProposalList()
				&& !this.getManagedBean().getScQtCheckSt2financeProposalList()
						.isEmpty()) {
			for (ScQtCheckList financialCheckList : this.getManagedBean()
					.getScQtCheckSt2financeProposalList()) {
				if (null == financialCheckList.getPmDocument()
						&& !QTConstants.TEMPLETE_SPEC_CODE
								.equalsIgnoreCase(financialCheckList
										.getDefinationType().getParameterCode())) {
					if (null != financialCheckList.getDefinationType()
							&& null == financialCheckList.getDefinationType()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						financialCheckList.setDefinationType(docDefTypeResDTO
								.getPmParameter());
					}
					if (null != financialCheckList.getAction()
							&& null == financialCheckList.getAction()
									.getParameterId()) {
						ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
						docDefTypeReqDTO
								.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
						docDefTypeReqDTO
								.setParameterCode(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);
						ParameterResDTO docDefTypeResDTO = parameterSo
								.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
						financialCheckList.setAction(docDefTypeResDTO
								.getPmParameter());
					}

				}
			}
		}
		List<ScQtCheckList> deletefinCheckList = markDeleteSelectedRowsActionById(
				this.getManagedBean().getScQtCheckSt2financeProposalList(),
				null);
		List<ScQtCheckList> financial = this.getManagedBean()
				.getMarkDeletedFinChecklist();
		if (null == financial) {
			financial = new ArrayList<ScQtCheckList>();
		}
		if (null != deletefinCheckList && !deletefinCheckList.isEmpty()) {
			financial.addAll(deletefinCheckList);
			this.getManagedBean().setMarkDeletedFinChecklist(financial);
		}
		parameterSo = null;
		checklistSorting(getManagedBean().getScQtCheckSt2financeProposalList());
	}

	/**
	 * this method is used to change the tabs(zones) in delivery address side
	 * menu.
	 * 
	 * @param event
	 *            (BriefingZoneBean zonebean).
	 * 
	 * 
	 * 
	 */
	public void deliveryDetailsZonesTabChange(TabChangeEvent event) {
		BriefingZoneBean specZoneDtl = (BriefingZoneBean) event.getData();
		this.getManagedBean()
				.setTplSpecZoneDtl(specZoneDtl.getTplSpecZoneDtl());
	}

	/**
	 * this method is used to check the Technical Standard Checklist Exist or
	 * not in specification side menu.
	 * 
	 * return true/false.
	 * 
	 * 
	 * 
	 */
	private boolean validateTechnicalStandardCheckList() {
		boolean pmdocument = false;
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != techCheckList && !techCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : techCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

			if (!pmdocument) {
				RequestContext.getCurrentInstance().execute(
						"technicalStandardChecklistDlgs.show()");
			}

		} else {
			pmdocument = true;
		}
		return pmdocument;
	}

	/**
	 * this method is used to check the Financial Standard Checklist Exist or
	 * not in specification side menu.
	 * 
	 * return true/false.
	 * 
	 * 
	 * 
	 */
	private boolean validateFinancialStandardCheckList() {
		boolean pmdocument = false;
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != financialCheckList && !financialCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : financialCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}
			if (!pmdocument) {
				RequestContext.getCurrentInstance().execute(
						"financialStandardChecklistDlgs.show()");
			}

		} else {
			pmdocument = true;

		}
		return pmdocument;
	}

	/**
	 * this method is used to check in the current technical or financial
	 * checklist have mandatory or optional pmdocuments in specification side
	 * menu.
	 * 
	 * @param stage
	 *            (String stage)
	 * 
	 * 
	 * 
	 */
	public void checkListYesConfirmation(String stage) {
		this.getManagedBean().setStandardTemplate(
				ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
		if (this.getManagedBean().getCheckListYesIndex() == 4
				|| this.getManagedBean().getCheckListYesIndex() == 5
				|| this.getManagedBean().getCheckListYesIndex() == 6) {
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
		}
		this.getManagedBean().setActiveIndex(
				this.getManagedBean().getCheckListYesIndex());
	}

	/**
	 * this method is used to check in the current technical or financial
	 * checklist don't have mandatory or optional pmdocuments in specification
	 * side menu.
	 * 
	 * @param stage
	 *            (String stage)
	 * 
	 * 
	 * 
	 */
	public void checkListNoConfirmation(String stage) {
		this.getManagedBean().setStandardTemplate(
				ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD);
		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setActiveIndex(2);

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL
				.equalsIgnoreCase(stage)) {
			this.getManagedBean().setActiveIndex(3);

		}

	}

	/**
	 * this method is used when user clicks on submit button in specification
	 * committee to check the Technical Standard Checklist Exist or not.
	 * 
	 * return true/false.
	 * 
	 * 
	 * 
	 */
	private boolean validateTechnicalStandardCheckListSubmit() {
		boolean pmdocument = false;
		retriveTechnicalCheckList();
		List<ScQtCheckList> techCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != techCheckList && !techCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : techCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

		}
		return pmdocument;
	}

	/**
	 * this method is used when user clicks on submit button in specification
	 * committee to check the Financial Standard Checklist Exist or not.
	 * 
	 * return true/false.
	 */
	private boolean validateFinancialStandardCheckListSubmit() {
		boolean pmdocument = false;
		retriveFinancialCheckList();
		List<ScQtCheckList> financialCheckList = this.getManagedBean()
				.getScQtCheckSt2financeProposalList();
		if (null != financialCheckList && !financialCheckList.isEmpty()) {

			for (ScQtCheckList documentTechnicalCheckList : financialCheckList) {
				if (null != documentTechnicalCheckList.getPmDocument()) {
					pmdocument = true;
					break;
				}

			}

		}
		return pmdocument;
	}

	/**
	 * this method is used when user clicks on submit button in specification
	 * committee to check in the techncial or financial checklist don't have
	 * mandatory or optional pmdocuments.
	 * 
	 * @param stage
	 *            (String stage).
	 * 
	 */
	public void checkListNoConfirmationSubmit(String stage) {

		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setTechnicalOtherChecklistSubmit(false);
			this.getManagedBean().setActiveIndex(2);

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL
				.equalsIgnoreCase(stage)) {
			this.getManagedBean().setFinancialOtherChecklistSubmit(false);
			this.getManagedBean().setActiveIndex(3);

		}
	}

	/**
	 * this method is used when user clicks on submit button in specification
	 * committee to check in the techncial or financial checklist have mandatory
	 * or optional pmdocuments.
	 * 
	 * @param stage
	 *            (String stage). return navigate to acknowledgement page.
	 * 
	 */
	public String checkListYesConfirmationSubmit(String stage) {
		String checklistSubmit = CommonUIConst.ACKNOWLEDGE_PAGE;
		if (ParameterConst.SPECIFICATION_TYPE_TECHNICAL.equalsIgnoreCase(stage)) {
			this.getManagedBean().setTechnicalOtherChecklistSubmit(true);
			boolean financial = validateFinancialStandardCheckListSubmit();
			if (!financial
					&& !this.getManagedBean().isFinancialOtherChecklistSubmit()) {
				RequestContext.getCurrentInstance().execute(
						"financialStandardChecklistDlgsSubmit.show()");
				return null;
			} else {
				specCommitteBpmTask(BpmOutcomeConst.BPM_SUBMIT);
				RequestContext.getCurrentInstance().execute(
						"technicalStandardChecklistDlgsSubmit.hide();");
			}

		} else if (ParameterConst.SPECIFICATION_TYPE_FINANCIAL
				.equalsIgnoreCase(stage)) {
			this.getManagedBean().setFinancialOtherChecklistSubmit(true);
			specCommitteBpmTask(BpmOutcomeConst.BPM_SUBMIT);
			RequestContext.getCurrentInstance().execute(
					"financialStandardChecklistDlgsSubmit.hide()");

		}

		return checklistSubmit;

	}

	/**
	 * this method is used to remove fulfilment details objects in fulfiment
	 * details side menu.
	 * 
	 * @Param String(String scheduleType).
	 * 
	 */
	private void removefullfillmentDetails(String scheduleType, boolean resetList) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();
				if (null == this.getManagedBean().getToBeRemoveSchedules()
						|| this.getManagedBean().getToBeRemoveSchedules()
								.isEmpty()) {
					this.getManagedBean().setToBeRemoveSchedules(
							new ArrayList<ScQtSchedule>());

				}
				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().getToBeRemoveSchedules()
						.addAll(toBeRemoveList);
				this.getManagedBean().getQt().setScQtSchedules(null);
			}
			if (resetList) {
			this.getManagedBean()
					.setScheduleList(new ArrayList<ScQtSchedule>());
			}
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();
			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();
				if (null == this.getManagedBean().getToBeRemoveProgPayments()
						|| this.getManagedBean().getToBeRemoveProgPayments()
								.isEmpty()) {
					this.getManagedBean().setToBeRemoveProgPayments(
							new ArrayList<ScQtProgressivePayment>());

				}
				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().getToBeRemoveProgPayments()
						.addAll(toBeRemoveList);
				this.getManagedBean().getQt().setScQtProgressivePayments(null);
			}
			if (resetList) {
			this.getManagedBean().setProgressivePaymentList(
					new ArrayList<ScQtProgressivePayment>());
			}
		}

	}

	/**
	 * this method is used when user click on cancel button in popup window.
	 * 
	 * 
	 * 
	 */
	public void cancelPopup() {
		this.getManagedBean().setAutoUpdate(true);
		this.getManagedBean().setSelectCtPanelItem(null);

	}

	private void ZonesUpdateWarningMessage(boolean warningMessage) {
		if (warningMessage == true) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, null,
							MessageUtils.getMessage(MessageConst.WARN_760328,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}

	}

	/**
	 * check in the current checklist have template spec exist or not.
	 * 
	 * return (true/false).
	 * 
	 */
	private boolean retriveExistingTemplatespec(List<ScQtCheckList> checkList) {
		if (null != checkList && !checkList.isEmpty()) {
			for (ScQtCheckList specChecklist : checkList) {
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
						
						return resDTO.getSpecMaster().isWeightageReq();

					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if is atleast one serive req.check service spec is existing or not
	 * in Technical checklist.
	 * 
	 * @param checklist
	 *            (List<ScQtCheckList> checklist)
	 * @return true, if is atleast one serive req
	 */
	private boolean isAtleastTechnicalSpec(List<ScQtCheckList> checklist) {
		int i = 0;
		if (null != checklist && !checklist.isEmpty()) {
			for (ScQtCheckList specChecklist : checklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(specChecklist.getDefinationType()
								.getParameterCode())) {
					if (null != specChecklist.getScQtDoc()) {
						i++;
						if (i >= 1) {
							return true;
						}

					}
				}
			}
		}

		return false;
	}

	private void isOverlappingScore(List<ScQtCheckListScore> checkListScorings) {
		Collections.sort(checkListScorings);

		ScQtCheckListScore prevScore = null;
		ScQtCheckListScore lastObject = null;
		String criteriaType = null;
		for (int row = 0; row < checkListScorings.size(); row++) {
			ScQtCheckListScore currScoring = checkListScorings.get(row);
			if (null == lastObject) {
				lastObject = checkListScorings
						.get(checkListScorings.size() - 1);
			}

			if (currScoring.getRangeFrom() != null
					&& currScoring.getRangeTo() != null
					&& currScoring.getRangeFrom().compareTo(
							currScoring.getRangeTo()) >= 0) {
				/*
				 * throw new EpException(MessageConst.ERROR_100033, new String[]
				 * { "To", "From at row " + (row + 1) });
				 */
				throw new EpException(MessageConst.ERROR_760403, new String[] {
						ResourceBundleUtils.getMessage("lbl.sc.from",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()),
						ResourceBundleUtils.getMessage("lbl.sc.to",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()),
						row + 1 + " " });
			}

			if (prevScore != null) {

				if (currScoring.getRangeFrom() != null
						&& currScoring.getRangeTo() != null
						&& prevScore.getRangeFrom() != null
						&& prevScore.getRangeTo() != null) {
					if (null == criteriaType) {
						criteriaType = String.valueOf(prevScore
								.getCriteriaType());
					}

					if ("50".equalsIgnoreCase(criteriaType)
							|| "60".equalsIgnoreCase(criteriaType)
							|| "61".equalsIgnoreCase(criteriaType)
							|| "11".equalsIgnoreCase(criteriaType)
							|| "16".equalsIgnoreCase(criteriaType)) {
						if (prevScore.getRangeTo()
								.add(BigDecimal.valueOf(0.01d))
								.compareTo(currScoring.getRangeFrom()) != 0) {
							throw new EpException(MessageConst.ERROR_100055,
									new String[] { row + 1 + "" });
						}
					} else {
						if (prevScore.getRangeTo().add(BigDecimal.valueOf(1))
								.compareTo(currScoring.getRangeFrom()) != 0) {
							throw new EpException(MessageConst.ERROR_100055,
									new String[] { row + 1 + "" });
						}
					}
				} else {
					if (null == criteriaType) {
						criteriaType = String.valueOf(prevScore
								.getCriteriaType());
					}
					if (null != lastObject && null != prevScore.getRangeTo()
							&& null != lastObject.getRangeFrom()) {
						if ("50".equalsIgnoreCase(criteriaType)
								|| "60".equalsIgnoreCase(criteriaType)
								|| "61".equalsIgnoreCase(criteriaType)
								|| "11".equalsIgnoreCase(criteriaType)
								|| "16".equalsIgnoreCase(criteriaType)) {
							if (prevScore.getRangeTo()
									.add(BigDecimal.valueOf(0.01d))
									.compareTo(lastObject.getRangeFrom()) != 0) {
								throw new EpException(
										MessageConst.ERROR_100055,
										new String[] { row + 1 + "" });

							}
						} else {
							if (prevScore.getRangeTo()
									.add(BigDecimal.valueOf(1))
									.compareTo(lastObject.getRangeFrom()) != 0) {
								throw new EpException(
										MessageConst.ERROR_100055,
										new String[] { row + 1 + "" });

							}
						}

					}
				}
			}

			prevScore = currScoring;
		}
	}

	/**
	 * Change Supplier Type In technical and Financial CheckList get Supplier
	 * action dropdownvalues.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPTJUploadDropDownActionValues() {

		SelectItem[] uploaditems = null;
		try {
			ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(ParameterConst.CHECKLIST_ACTION_VIEW);
			recommendList
					.add(ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION);

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), recommendList));
			uploaditems = getDropdownValuesBySearchBean(
					ParameterTypeConst.SC_SUPPLIER_ACTION, searchBean);
			searchBean = null;
			recommendList = null;
		} finally {

		}
		return uploaditems;
	}

	private BigDecimal retrieveTechnicalEvalutionPassingScorePmrules(
			String ruleType) {
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRuleSO.class);
		BigDecimal technicalPassingScore = new BigDecimal(0);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(ruleType);
		List<PmConfigRuleValue> ruleValueList = ruleSO
				.getRuleValuesByTypeInCurrentDate(ruleReqDTO)
				.getRuleValueList();
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			if (configRuleValue != null
					&& configRuleValue.getRuleValue() != null) {
				technicalPassingScore = new BigDecimal(
						configRuleValue.getRuleValue());
			}
		} else {
			throw new EpException(MessageConst.ERROR_770222,
					new String[] { getRuleTypeDesc(ruleReqDTO.getRuleType()) });
		}
		ruleSO = null;
		return technicalPassingScore;
	}

	private void calculateRecommendationPackageAndItemScores(
			ScQtScoreSetting recommendationPackage,
			ScQtScoreSetting recommendationItem) {
		BigDecimal passingScore = retrieveTechnicalEvalutionPassingScorePmrules(RuleTypeConst.SC_TECHNICAL_WEIGHTAGE);
		if (null != recommendationPackage
				&& null != recommendationPackage.getOverallTechnical()
				&& null != passingScore
				&& recommendationPackage.getOverallTechnical().intValue() < passingScore
						.intValue()) {
			this.getManagedBean().setActiveIndex(6);
			throw new EpException(MessageConst.ERROR_760365,
					new String[] { String.valueOf(passingScore) });
		}
		if (null != recommendationItem) {
			int itemPassingScore = 0;
			int value1 = recommendationItem.getOverallTechnical() != null ? recommendationItem
					.getOverallTechnical().intValue() : 0;
			int value2 = recommendationItem.getOverallOtherTechnical() != null ? recommendationItem
					.getOverallOtherTechnical().intValue() : 0;
			itemPassingScore = value1 + value2;

			if (itemPassingScore < 50) {
				this.getManagedBean().setActiveIndex(6);
				throw new EpException(MessageConst.ERROR_760366);
			}
		}
	}

	/**
	 * Upload Tehnical UserScoring Schema Files.
	 * 
	 */
	public void technicalUserScoringSchemaFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		System.out.println("====upload Scoring Schema Type===="
				+ event.getFile().getFileName());
		ScTechnicalUserScoringSchemaAttachment attachment = this
				.getManagedBean().getQt()
				.getTechnicalUserScoringSchemaAttachment();

		if (null == attachment) {
			attachment = new ScTechnicalUserScoringSchemaAttachment();
			attachment.setScQt(this.getManagedBean().getQt());
		}
		attachment.setDocNo(this.getManagedBean().getQt().getQtNo());

		uploadAttachment(event.getFile(), attachment);
		this.getManagedBean().getQt()
				.setTechnicalUserScoringSchemaAttachment(attachment);

	}

	/**
	 * Upload Financial UserScoring Schema Files.
	 * 
	 */
	public void financialUserScoringSchemaFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		System.out.println("====upload Scoring Schema Type===="
				+ event.getFile().getFileName());
		ScFinancialUserScoringSchemaAttachment attachment = this
				.getManagedBean().getQt()
				.getFinancialUserScoringSchemaAttachment();

		if (null == attachment) {
			attachment = new ScFinancialUserScoringSchemaAttachment();
			attachment.setScQt(this.getManagedBean().getQt());
		}
		attachment.setDocNo(this.getManagedBean().getQt().getQtNo());

		uploadAttachment(event.getFile(), attachment);
		this.getManagedBean().getQt()
				.setFinancialUserScoringSchemaAttachment(attachment);

	}

	/**
	 * delete Tehnical or Financial Upload UserScoring Schema Files.
	 * 
	 */
	public void deleteTechnicalFinacialUserScoringSchema(String type) {
		System.out.println("====delete Scoring Schema Type====" + type);
		if ("T".equals(type)) {
			this.getManagedBean().getQt()
					.setTechnicalUserScoringSchemaAttachment(null);
		} else if ("F".equals(type)) {
			this.getManagedBean().getQt()
					.setFinancialUserScoringSchemaAttachment(null);
		}
	}

	private void setTechncialChecklistEvaluationStatus(
			List<ScQtCheckList> technicalCheckList) {
		if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
			for (ScQtCheckList checkList : technicalCheckList) {
				if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(checkList.getDefinationType()
								.getParameterCode())
						&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
								.equalsIgnoreCase(checkList.getDefinationType()
										.getParameterCode())) {
					if (!checkList.isScoringReq()) {
						checkList
								.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					}

				}
			}
		}

	}

	private void setFinancialChecklistEvaluationStatus(
			List<ScQtCheckList> financialCheckList) {
		if (null != financialCheckList && !financialCheckList.isEmpty()) {
			for (ScQtCheckList financialcheckList : financialCheckList) {
				if (!ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD
						.equalsIgnoreCase(financialcheckList
								.getDefinationType().getParameterCode())
						&& !ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION
								.equalsIgnoreCase(financialcheckList
										.getDefinationType().getParameterCode())) {
					if (!financialcheckList.isScoringReq()) {
						financialcheckList
								.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					}

				}

			}
		}

	}

	/**
	 * this method is used to check the technical user scoring schema attachment
	 * is mandatory or not
	 * 
	 */
	private void validateTechnicalUserScoringAttachment(
			boolean isTechnicalUserScoringSchema) {

		if (isTechnicalUserScoringSchema) {
			if (null == this.getManagedBean().getQt()
					.getTechnicalUserScoringSchemaAttachment()) {
				this.getManagedBean().setActiveIndex(4);
				throw new EpException(MessageConst.ERROR_760378);
			}

		}
	}

	/**
	 * this method is used to check the financial user scoring schema attachment
	 * is mandatory or not
	 * 
	 */
	private void validateFinancialUserScoringAttachment(
			boolean isFinancialUserScoringSchema) {
		if (isFinancialUserScoringSchema) {
			if (null == this.getManagedBean().getQt()
					.getFinancialUserScoringSchemaAttachment()) {
				this.getManagedBean().setActiveIndex(5);
				throw new EpException(MessageConst.ERROR_760379);
			}
		}

	}

	/**
	 * Check status is checklist and scoring
	 * 
	 * @return
	 */
	public boolean isQTStatusChecklistScoring() {
		return QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING
				.equals(getManagedBean().getCurrentStatusId());
	}

	/**
	 * Check status is checklist and scoring revision
	 * 
	 * @return
	 */
	public boolean isQTStatusChecklistScoringRevision() {
		return QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION
				.equals(getManagedBean().getCurrentStatusId());
	}

	private ScQt saveZonesBeforeTemplateSpec(ScQt scQt) {
		ScQtSupplierCriteria qtSupplierCriteria = scQt
				.getScQtSupplierCriteria();
		if (!scQt.isPanel() && null != qtSupplierCriteria) {
			if (scQt.isZonal()
					&& scQt.getQtType().equals(
							ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				List<TreeNode> zoneTrees = this.getManagedBean()
						.getZoneSuppQualTrees();
				if (zoneTrees == null) {
					zoneTrees = this.getManagedBean().getZoneGeneralTrees();
				}
				List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(
						zoneTrees, null);

				if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
					setUserDetails(qtSupplierCriteria.getQtZoneCriterias());
					List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
					for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
						qtSupplierCriteria.getQtZoneCriterias().setModified(
								true);
						tplSpecZoneDtl.setTplSpecZone(qtSupplierCriteria
								.getQtZoneCriterias());
						zoneLocal.add(tplSpecZoneDtl);
					}
					qtSupplierCriteria.getQtZoneCriterias()
							.setScQtSupplierCriteria(qtSupplierCriteria);
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
							zoneLocal);
				} else {
					if (null != scQt.getScQtSupplierCriteria()
							.getQtZoneCriterias()) {
						qtSupplierCriteria.getQtZoneCriterias()
								.setTplSpecZoneDtls(null);

					}
				}
				qtSupplierCriteria.setScQt(scQt);
				setUserDetails(qtSupplierCriteria);
				scQt.setScQtSupplierCriteria(qtSupplierCriteria);
			}
			/*
			 * if (QTConstants.OPEN_TYPE.equals(scQt.getQtType()) &&
			 * !scQt.getScQtSupplierCriteria().isAllState()) {
			 * List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>)
			 * convertTreeNodesToLocationCriteria(
			 * this.getManagedBean().getZoneTrees(), qtSupplierCriteria); if
			 * (locCriteria != null && !locCriteria.isEmpty()) {
			 * scQt.getScQtSupplierCriteria().setScQtLocCriteria( locCriteria);
			 * } else { scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
			 * } }
			 */

			/*
			 * if (QTConstants.OPEN_TYPE.equals(scQt.getQtType()) &&
			 * ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt
			 * .getProcurementMode().getParameterCode()) &&
			 * this.getManagedBean().getQt().getScQtSupplierCriteria()
			 * .isAllState()) { if (null != qtSupplierCriteria) {
			 * 
			 * qtSupplierCriteria.setLocationLevel(null);
			 * qtSupplierCriteria.setScQtLocCriteria(null); } }
			 */

			qtSupplierCriteria.setScQt(scQt);
			setUserDetails(qtSupplierCriteria);
			scQt.setScQtSupplierCriteria(qtSupplierCriteria);

			/*
			 * if (QTConstants.OPEN_TYPE.equalsIgnoreCase(scQt.getQtType())) {
			 * List<ScQtCategory> openList = this.getManagedBean()
			 * .getSuppCriteriaopenList(); if (null != openList &&
			 * !openList.isEmpty()) { scQt.setScQtCategorys(openList); } } else
			 * if (QTConstants.RESTRICTED_TYPE.equalsIgnoreCase(scQt
			 * .getQtType())) { scQt.setZonal(false);
			 * scQt.setZoneLocationLevel(null); if (null !=
			 * scQt.getScQtSupplierCriteria()) {
			 * scQt.getScQtSupplierCriteria().setAllState(false);
			 * scQt.getScQtSupplierCriteria().setLocationLevel(null);
			 * scQt.getScQtSupplierCriteria().setScQtLocCriteria(null); if (null
			 * != scQt.getScQtSupplierCriteria() .getQtZoneCriterias()) {
			 * qtSupplierCriteria.getQtZoneCriterias()
			 * .setTplSpecZoneDtls(null);
			 * qtSupplierCriteria.setQtZoneCriterias(null); }
			 * 
			 * }
			 * 
			 * List<ScQtCategory> openList = this.getManagedBean()
			 * .getSuppCriteriaopenList(); if (null != openList &&
			 * !openList.isEmpty()) { scQt.setScQtCategorys(openList); }
			 * 
			 * }
			 */
		} else if (scQt.isPanel()) {

			List<TplSpecZoneDtl> businessOwnerZone = null;

				businessOwnerZone = convertTreeNodesToZoneCriteria(
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
				setUserDetails(zoneCriteria);
				List<TplSpecZoneDtl> zoneLocal = new ArrayList<TplSpecZoneDtl>();
				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					zoneCriteria.setModified(true);
					tplSpecZoneDtl.setTplSpecZone(zoneCriteria);
					zoneLocal.add(tplSpecZoneDtl);
				}
				scQt.setZonal(true);
				if (qtSupplierCriteria != null )//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
					qtSupplierCriteria.getQtZoneCriterias().setTplSpecZoneDtls(
						zoneLocal);

			}
			//20200214@asyrafhaziq_Bug #27381&#26694   - Start
			else {
				if (null != scQt.getScQtSupplierCriteria()
						.getQtZoneCriterias()&& qtSupplierCriteria != null) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
					qtSupplierCriteria.getQtZoneCriterias()
							.setTplSpecZoneDtls(null);
				}
			}
			//20200214@asyrafhaziq_Bug #27381&#26694   - End

			// scQt.setZoneLocationLevel(null);
			scQt.getScQtSupplierCriteria().setLocationLevel(null);
			scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);

		}
		scQt.setScQtCommittees(null);
		scQt.setScQtStatus(null);
		if (!scQt.isBsvRequired()) {
			scQt.setScQtBriefings(null);
		}
		return scQt;
	}

	/**
	 * add panelItems as a TemplateSpec Items or specifications.
	 * 
	 * @return navigate to template spec screen.
	 * 
	 * 
	 */
	public String addPanelItemsToTemplateSpec() {
		this.getManagedBean().setSpecNewable(true);
		this.getManagedBean().setUpdateSpec(true);
		this.getManagedBean().setPanelSpecExistedOrNot(false);
		List<CtPanelItem> selectedPanelItems = this.getManagedBean()
				.getSelectCtPanelItem();
		if (null == selectedPanelItems || selectedPanelItems.isEmpty()) {
			selectedPanelItems = new ArrayList<CtPanelItem>();
			throw new EpException(MessageConst.ERROR_760147);
		}
		String itemType = "";
		String localItemType = "";
		for (CtPanelItem panelItem : selectedPanelItems) {
			itemType = panelItem.getItemCategoryId().getParameterCode();
			if ("".equals(localItemType) || !localItemType.equals(itemType)) {
				if (null != localItemType && !"".equals(localItemType)) {
					throw new EpException(MessageConst.ERROR_760391);
				} else {
					localItemType = itemType;
				}
			} else {
				localItemType = itemType;
			}
			if (ParameterConst.ITEM_CATEGORY_TYPE_GENERAL.equals(itemType)) {
				int num = selectedPanelItems.size();
				if (num > 1) {
					throw new EpException(MessageConst.ERROR_760392);
				}
			}

		}
		String itemCategory = "";
		String localItemCategory = "";
		for (CtPanelItem checkItem : selectedPanelItems) {
			itemCategory = checkItem.getItemType().getParameterCode();
			if ("".equals(localItemCategory)
					|| !localItemCategory.equals(itemCategory)) {
				if (null != localItemCategory && !"".equals(localItemCategory)) {
					throw new EpException(MessageConst.ERROR_760393);
				} else {
					localItemCategory = itemCategory;
				}
			} else {
				localItemCategory = itemCategory;
			}
		}
		for (CtPanelItem checkItem : selectedPanelItems) {
			if (ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equals(checkItem
					.getItemCategoryId().getParameterCode())) {
				this.getManagedBean().setSpecNewable(false);
				this.getManagedBean().setUpdateSpec(false);
				break;
			}
		}
		if (selectedPanelItems.size() > 1) {
			String panelCoverage = "";
			String localPanelCoverage = "";
			String locationLevel = "";
			String locallocationLevel = "";
			for (CtPanelItem checkItem : selectedPanelItems) {
				// String panelCoverage="";
				// String localPanelCoverage="";
				if (null != checkItem.getPanelCoverageId()) {
					panelCoverage = checkItem.getPanelCoverageId()
							.getParameterCode();
				}
				if (null != checkItem.getLocationLevelId()) {
					locationLevel = checkItem.getLocationLevelId()
							.getParameterCode();
				} else {
					locationLevel = "";
				}
				if ("".equals(localPanelCoverage)
						|| !localPanelCoverage.equals(panelCoverage)) {
					if (null != localPanelCoverage
							&& !"".equals(localPanelCoverage)) {
						throw new EpException(MessageConst.ERROR_760395);
					} else {
						localPanelCoverage = panelCoverage;
					}
				}
				if ("".equals(locallocationLevel)
						|| !locallocationLevel.equals(locationLevel)) {
					if (null != locallocationLevel
							&& !"".equals(locallocationLevel)) {
						throw new EpException(MessageConst.ERROR_760396);
					} else {
						locallocationLevel = locationLevel;
					}
				}

				else {
					locallocationLevel = locationLevel;
					localPanelCoverage = panelCoverage;
				}
				log.debug("===panelCoverage" + panelCoverage
						+ "localPanelCoverage" + localPanelCoverage);
				log.debug("===locationLevel" + locationLevel
						+ "locallocationLevel" + locallocationLevel);
			}

		}

		List<ScQtCheckList> technicalCheckList = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (null != technicalCheckList && !technicalCheckList.isEmpty()) {
			boolean istechnicalSpec = isAtleastTechnicalSpec(technicalCheckList);
			if (istechnicalSpec) {
				String ZonalType = "";
				boolean zonal = this.getManagedBean().getQt().isZonal();
				if (zonal) {
					ZonalType = QTConstants.CT_PANEL_ZONE_COVERAGE;
				} else {
					ZonalType = QTConstants.CT_PANEL_ALL_COVERAGE;
				}
				for (CtPanelItem checkItem : selectedPanelItems) {
					String panelCoverage = checkItem.getPanelCoverageId()
							.getParameterCode();
					if (!ZonalType.equalsIgnoreCase(panelCoverage)) {
						if (QTConstants.CT_PANEL_ZONE_COVERAGE
								.equalsIgnoreCase(ZonalType)) {
							throw new EpException(MessageConst.ERROR_760397);
						} else {
							throw new EpException(MessageConst.ERROR_760398);
						}
					}
				}

			}
		}
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("addPanelItem.hide()");
		createCircularItemSpec(selectedPanelItems);
		this.getManagedBean().setNewTemplateSpec(true);
		return "/xhtml/sc/boSctemplatespec/technicalCommitteeTemplate.xhtml?faces-redirect=true";
	}

	private void checkPanelItemIsZonalOrNonzonal(String panelCoverage,
			CtPanelItem item) {
		ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt()
				.getScQtSupplierCriteria();
		if (QTConstants.CT_PANEL_ZONE_COVERAGE.equalsIgnoreCase(panelCoverage)) {

			if (null == supplierCriteria) {
				supplierCriteria = new ScQtSupplierCriteria();
			}
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();
			if (null == zoneCriteria) {
				zoneCriteria = new ScQtZoneCriteria();
				zoneCriteria.setScQtSupplierCriteria(supplierCriteria);
				supplierCriteria.setQtZoneCriterias(zoneCriteria);
			}
			this.getManagedBean().getQt().setZonal(true);
			this.getManagedBean().getQt()
					.setZoneLocationLevel(item.getLocationLevelId());
			List<ScQtCheckList> technicalCheckList = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			if (((this.getManagedBean().getQt().isZonal() && null != supplierCriteria && null != supplierCriteria.getQtZoneCriterias() && null == supplierCriteria
					.getQtZoneCriterias().getSpecZoneId()) || (!isAtleastTechnicalSpec(technicalCheckList)))) {
				this.getManagedBean().setPanelSpecExistedOrNot(true);
				retriveZonesOrSuppliersByItem(item);
			}

		} else {
			this.getManagedBean().getQt().setZonal(false);
			this.getManagedBean().getQt()
					.setZoneLocationLevel(item.getLocationLevelId());
			retriveZonesOrSuppliersByItem(item);
		}

	}

	private List<PmDocument> checkPmDocumentsExistInMainList(
			List<PmDocument> documentList, List<ScQtCheckList> checklist) {
		List<PmDocument> LocalDocumenList = new ArrayList<PmDocument>();
		try {
			if (CollectionUtils.isEmpty(checklist)) {
				LocalDocumenList = documentList;
			} else {
				List<String> checklistTitle = new ArrayList<String>();
				for (ScQtCheckList checkList : checklist) {
					if (StringUtils.isNotBlank(checkList.getTitle())
							&& StringUtils.isNotEmpty(checkList.getTitle())) {
						checklistTitle.add(checkList.getTitle());
					}
				}
				for (PmDocument document : documentList) {
					if (!checklistTitle.contains(document.getDocName())) {
						LocalDocumenList.add(document);
					}
				}
				checklistTitle = null;
			}
		} finally {

		}

		return LocalDocumenList;
	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	private Integer formatChecklistSequence(String ckecklistSequence,
			int runningNo) {
		StringBuilder docNo = new StringBuilder();
		docNo.append(ckecklistSequence);
		docNo.append(String.format("%1$0" + (6 - docNo.length()) + "d",
				runningNo));
		log.debug("============display doc num=====" + docNo.toString());
		return new Integer(docNo.toString());
	}

	private Integer checkTechnicalORFinancialAddChecklistSequence(
			List<ScQtCheckList> checklists) {
		if (CollectionUtils.isNotEmpty(checklists)) {
			ScQtCheckList checklist = checklists.get(checklists.size() - 1);
			if (checklist.getChecklistSeq().toString()
					.contains(SourcingConstants.UPLOAD_CHECKLIST_SEQUENCE)) {
				return (checklist.getChecklistSeq() + 1);
			} else {
				return SourcingConstants.CHECKLIST_SEQ_UPLOAD;
			}

		} else {
			return SourcingConstants.CHECKLIST_SEQ_UPLOAD;
		}
		// return SourcingConstants.CHECKLIST_SEQ_UPLOAD;
	}

	private void checklistSorting(List<ScQtCheckList> checkList) {
		Collections.sort(checkList, new Comparator<ScQtCheckList>() {
			public int compare(ScQtCheckList checklist1,
					ScQtCheckList checklist2) {

				// return
				// checklist1.getChecklistSeq()-checklist2.getChecklistSeq();
				return new CompareToBuilder()
						.append(checklist1.getChecklistSeq(),
								checklist2.getChecklistSeq())
						.append(checklist1.getCreatedDate(),
								checklist2.getCreatedDate()).toComparison();
			}
		});
	}

	/**
	 * The method to used for SpecMaintenance.
	 * 
	 * @return string value.
	 */
	public String getSpecMaintenance() {

		return QTConstants.SPEC_MAINTENANCE;
	}

	private void setTechnicalSpecEnableDisable() {
		if (this.getManagedBean().getSpecMaintenanceScreen()
				.equals(getSpecMaintenance())) {
			if (!this.getManagedBean().isReadOnly()) {
				this.getManagedBean().setSpecNewable(true); //#15391
				this.getManagedBean().setUpdateSpec(true);
			} else {
				this.getManagedBean().setSpecNewable(false);
				this.getManagedBean().setUpdateSpec(false);
			}
		}

	}

	/**
	 * Spec committe Maintenance bpm task.Event handle Submit/Endorsment in
	 * specification committee.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public void submitSpecMaintenanceDtls(String action) {
// #29732 Start - reOpen Fix
		// #29732 check if submit with no category code before checking category
		// code change
		IQTSpecSO specSo = getSpecSO();
		SpecificationReqDTO specReqDTOCatChange = new SpecificationReqDTO();
		specReqDTOCatChange.setScQt(this.getManagedBean().getQt());
		specSo.validateSupplierQulificationInfoCatChange(specReqDTOCatChange);
		String changedCatCodeList = checkCatCodeChange();
		if (!changedCatCodeList.isEmpty()) {
			this.getManagedBean().setCatCodeList(changedCatCodeList);
			//#29732 issue submit specification maintenance(allow submit without popup validation
			if(!QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(this.getManagedBean().getCurrentStatusId()) 
					&& !QTStatusConst.QT_REVISION_PUBLICATION.equals(this.getManagedBean().getCurrentStatusId())){
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
			
		}
// #29732 End - reOpen Fix
		log.debug("start submitSpecMaintenanceDtls" + action);
		this.getManagedBean().setLocalOutCome(action);
		log.debug("end submitSpecMaintenanceDtls" + action);
		// 25037 - start
		assertSpecCommitteeMaintenanceInProgress(this.getManagedBean().getQt(), true);// 25037
		if (this.getManagedBean().getInactiveSCMemberStr().length() > 0) {
			this.qtChecklistTabChange(10);
			RequestContext.getCurrentInstance().execute("inactiveSCMembersPopup.show()");
			return;
		}
		// 25037 - end
		//1873 - start - note 52
		boolean popupShown = assertPendingFinancialSubmission(); 
		//#28784 - checking for pending spec before submit
		if (this.getManagedBean().getQt().isDemoRequired() && 
				ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this.getManagedBean().getQt().getEvaluationType())){
			checkTechnicalAndFinancialSpec(this.getManagedBean().getQt());
		}
		if(!popupShown){
			SpecificationResDTO resDTO = updateSpecCommitteMaintenaceDtls(action,
					null);
			resDTO.setSuccessful(Boolean.TRUE);
			if (resDTO.isSuccessful()) {
				ScQt qt = resDTO.getScQt();
				successMessage(getMessgeConstant(action),
						getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(), Boolean.TRUE);
			}
		//1873 - end - note 52
		}
	}

	private SpecificationResDTO updateSpecCommitteMaintenaceDtls(String action,
			PmDigiSign digiSign) {
		log.debug("start updateSpecCommitteMaintenaceDtls" + action);
		SpecificationResDTO resDTO = new SpecificationResDTO();
		ScQt scQt = this.getManagedBean().getQt();

		loadPrepareSpecCommitteeMaintenanceDetails(scQt.getQtId());
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls(null,false);	//Bug #35688@Syazani 10/07/2024 #29732 adding param
		//#15391 - start
		List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
		if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
			for (int i = 0; i < checklistRemoveList.size(); i++) {
				if (null == checklistRemoveList.get(i).getQtChecklistId()) {
					checklistRemoveList.remove(i);
				}
			}

		}
		
		List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
				.getDeliveryLocRemoveList();
		if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
			for (int i = 0; i < deliveryRemoveList.size(); i++) {
				if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
					deliveryRemoveList.remove(i);
				}
			}

		}
		
		spReqDTO.setOutcome(action);

		signoffCommitteeActivity(spReqDTO, action);

		if (null != digiSign) {
			updatePmDigiSignData(digiSign,
					ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT,
					scQt.getQtId(), scQt.getQtNo());
		}
		
		// #29474 - start
		List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
				.getRemoveDeductions();
		if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
			for (int i = 0; i < deductionRemoveList.size(); i++) {
				if (null == deductionRemoveList.get(i).getQtDeductionId()) {
					deductionRemoveList.remove(i);
				}
			}
		}
		
		// #29732 - start
		List<ScQtCategory> removeCategoryCodes = this.getManagedBean()
				.getTobeRemoveCategoryCodes();
		if (null != removeCategoryCodes && !removeCategoryCodes.isEmpty()) {
			for (int i = 0; i < removeCategoryCodes.size(); i++) {
				if (null == removeCategoryCodes.get(i).getQtCategoryId()) {
					removeCategoryCodes.remove(removeCategoryCodes.get(i));
				}
			}
		}		

		spReqDTO.setTobeRemoveCategoryCodes(removeCategoryCodes);//#29732
		spReqDTO.setRemoveDeductions(deductionRemoveList);// #29474 - end
		spReqDTO.setTobeRemoveList(checklistRemoveList);
		spReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);//#15391 - end
		// Reopen 25726 note 271- Issue 4 
		spReqDTO.setToBeRemoveSchedulesList(this.getManagedBean().getToBeRemoveSchedules());
		spReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean().getToBeRemoveProgPayments());
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		spReqDTO.setPmUser(facesScopes.getUserSessionBean());
		spReqDTO.setRoleCode(getUserRoleCode());
		spReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		spReqDTO.setUserLocale(facesScopes.getUserLocale());
		//Bug #26214
		spReqDTO.setAllowZeroVariance(true);
		// #25638 - Start.
		spReqDTO.setSpecMaintenance(getManagedBean().isSpecMaintenance());
		// #25638 - End.
		//#25138 - start
		spReqDTO.setOfferScoreType(this.getManagedBean().getOfferScoreType());
		//#25138 - end
		spReqDTO.setSingleItem(this.getManagedBean().isSingleItem());//32453
		SpecificationReqDTO specReqDto=new SpecificationReqDTO();//34852_start
		specReqDto.setScQt(this.getManagedBean().getQt());
		this.getManagedBean().setWipStatusMax(getSpecSO().checkWipScoreSettingMax(specReqDto));
		if(this.getManagedBean().getWipStatusMax()>=SourcingConstants.SCORE_SETTING_TYPE_TECHNICAL_BY_ITEM){
		spReqDTO.setZonesQt(this.getManagedBean().getQt().isZonal());
		}//34852_End
		spReqDTO.setScoreOfferPriceList(this.getManagedBean().getOfferPricePackageList());
		//Bug #33642 related to #25138 END
		spReqDTO.setSampleExist(checkISSampleExistWIPChecklist());//11104-Reendose
		IQTSpecSO specSO = getSpecSO();
		try {
			resDTO = specSO.updateSpecMaintananceDetails(spReqDTO);

		} finally {
			specSO = null;
		}
		log.debug("end updateSpecCommitteMaintenaceDtls" + action);
		return resDTO;

	}

	/**
	 * Load prepare specMaintenance committee details.Event handle click on
	 * submit button to retrieve all the Specification Committee menus
	 * information.
	 * 
	 * @param qtId
	 *            (Long qtId)
	 * 
	 */
	private void loadPrepareSpecCommitteeMaintenanceDetails(Long qtId) {
		log.debug("start loadPrepareSpecCommitteeMaintenanceDetails");

		retriveSpecdetails(qtId);

		retriveSupplierQualificationCriteria("SC");
		retriveTechnicalCheckList();
		retriveTechnicalScoring();
		retriveFinancialCheckList();
		retriveFinancialScoring(null);	// Bug #35688@Syazani 10/07/2024
		retriveRecommendationDetails();
		retriveFulfilmentDtls();
		retriveDeliveryDetails();
		retriveCommitteeEndorsment();

		if (!this.getManagedBean().isReadOnly()
				&& getSpecMaintenance().equalsIgnoreCase(
						this.getManagedBean().getSpecMaintenanceScreen())) {
			log.debug("if condition loadPrepareSpecCommitteeMaintenanceDetails");
			checktemplateSpecStatus(this.getManagedBean().getQt());
			checkFinancialSpecStatus(this.getManagedBean().getQt());
			validateTechnicalPassingScoring(this.getManagedBean()
					.getTechnicalScoreSetting(), this.getManagedBean()
					.getTechnicalItemScoreSetting());
// #21695 START FARID
			resetTechnicalWeightage(this.getManagedBean()
					.getTechnicalScoreSetting());
			validateTechnicalDemoPassingScoring(this.getManagedBean()
					.getDemoScoreSetting());
			technicalWeightageCalculate();
			technicalItemScoreCalculate(this.getManagedBean()
					.getTechnicalItemScoreSetting());
// #21695 START FARID
			validateFinancialPackageItemPassingScoring(this.getManagedBean()
					.getFinancialScoreSetting(), this.getManagedBean()
					.getFinacialItemScoreSetting());
			resetFinancialWeightage(this.getManagedBean()
					.getFinancialScoreSetting());
			financialWeightageCalculate();
			financialItemScoreCalculate(this.getManagedBean()
					.getFinacialItemScoreSetting());
			calculateRecommendationPackageAndItemScores(this.getManagedBean()
					.getRecommendationPackageScore(), this.getManagedBean()
					.getRecommendationScore());
			validateTechnicalUserScoringAttachment(this.getManagedBean()
					.isTechnicalUserScoringSchema());

			validateFinancialUserScoringAttachment(this.getManagedBean()
					.isFinancialUserScoringSchema());
			// setTechncialChecklistEvaluationStatus(this.getManagedBean()
			// .getScQtCheckSt2techProposal());
			// setFinancialChecklistEvaluationStatus(this.getManagedBean()
			// .getScQtCheckSt2financeProposalList());
		}
		log.debug("end loadPrepareSpecCommitteeMaintenanceDetails");
		// this.getManagedBean().setActiveIndex(0);

	}

	private void saveSpecMaintenanceWorkFlowStatus() {
		IQTSpecSO QtSpecSO = null;
		IScWorkFlowStatusSO workFlowStatusSO;
		log.debug("StartsaveSpecMaintenanceWorkFlowStatus");
		try {
			if (this.getManagedBean().getSpecMaintenanceScreen()
					.equalsIgnoreCase(getSpecMaintenance())
					&& (null == this.getManagedBean()
							.getSpecMaintenanceWorkFlowStatus() || QTStatusConst.SE_COMPLETED
							.equals(this.getManagedBean()
									.getSpecMaintenanceWorkFlowStatus()
									.getPmStatus().getStatusId()))) {
				log.info("loopsaveSpecMaintenanceWorkFlowStatus");
				QtSpecSO = getSpecSO();
				SpecificationReqDTO specReqDTo = new SpecificationReqDTO();
				specReqDTo.setScQt(this.getManagedBean().getQt());
				QtSpecSO.updateSpecMaintananceWorkFlowStatus(specReqDTo);

				workFlowStatusSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScWorkFlowStatusSO.class);
				ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
				reqDTO.setDocType(ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT);
				reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
				reqDTO.setStatusIds(Arrays
						.asList(QTStatusConst.SE_PENDING_SUBMISSION));
				ScWorkFlowStatusResDTO workFlowResDTo = workFlowStatusSO
						.retrieveWorkFlowStatus(reqDTO);
				if (CollectionUtils.isNotEmpty(workFlowResDTo
						.getWorkFlowStatusList())) {
					//1873 note 125 start
					ScWorkFlowStatus specStatus = workFlowResDTo.getWorkFlowStatusList().get(0);
					Long specStatusId = specStatus!=null ? specStatus.getPmStatus().getStatusId(): null;
					this.getManagedBean().setSpecMaintenanceWorkFlowStatus(specStatus);
					if(specStatusId!=null){
						this.getManagedBean().setCurrentStatusId(specStatusId);
					}
					//1873 note 125 end
				}
			}

		} finally {
			QtSpecSO = null;
			workFlowStatusSO = null;
		}
		log.debug("endsaveSpecMaintenanceWorkFlowStatus");
	}

	/**
	 * user click on task in task list menu.it will call
	 * specificationMaintenanceEndorsmentPrerenderview() method and Renders the
	 * specification committe screens or pages..
	 */
	public void specificationMaintenanceEndorsmentPrerenderview() {
		SCQuotationTenderDataType quotationTenderDataType = getBpmTaskDetails(
				SCQuotationTenderDataType.class, null);
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(quotationTenderDataType.getQtId().longValue());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO resDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		this.getManagedBean().setMaintanceFlow(true);
		ScCommitteeMember member = resDTO.getCommMember();
		if (null != member) {

			if (member.getMemberRole().getParameterCode()
					.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				this.getManagedBean().setReadOnly(false);
				this.getManagedBean().setSpecMaintenance(true);
			} else {

				this.getManagedBean().setReadOnly(true);
			}
		}
		retriveSpecdetails(quotationTenderDataType.getQtId().longValue());
		ScQt scQt = this.getManagedBean().getQt();
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {

			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());

		}
		if (null != this.getManagedBean().getQt().getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(
					this.getManagedBean().getQt().getFulfilmentType()
							.getParameterCode());
		}
		if (!this.getManagedBean().isReadOnly()) {
			if (scQt.getObtainSpecialApproval() != null
					&& scQt.getObtainSpecialApproval()) {
				this.getManagedBean().setProcMethodDropdownPresent(
						checkForProcumentMethodDropdown(scQt));
			}
		}
		this.getManagedBean().setSpecMaintenanceScreen(getSpecMaintenance());
		// Bug #33661@Syazani 06/10/2022 -- Start
		this.getManagedBean().setFromSpecEndorse(true);
		retrieveStatusByTypeAndId(scQt);
		// Bug #33661@Syazani 06/10/2022 -- End
		
		//#15391 - start
		log.info("STATUS ID: "+scQt.getQtStatus().getStatusId());
		log.info("THIS STATUS ID: "+this.getManagedBean().getCurrentStatusId());
		log.info("QTID: "+scQt.getQtId());
		if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId()) 
				|| QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId())){
			this.getManagedBean().setGenInfo(true);
			if(QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())) {
				this.getManagedBean().setGenInfo(false);
			}
		}
		if((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId())) 
				|| (QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_PUBLICATION_REVISION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
				|| (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(scQt.getQtStatus().getStatusId()))){//#33837 - add bpk and reschedule
			this.getManagedBean().setChecklist(true);
			if(QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())) {
				this.getManagedBean().setChecklist(false);
			}
		}
		//#15391 - end

		// #29732(#232) reuse existing code
		if (isRoleValidationfromTaskList()) {
			this.getManagedBean().setCategoryDescriptionChange(true); // 29732
		}
		String changedCatCodeList = checkCatCodeChange();
		if (!changedCatCodeList.isEmpty()) {
			this.getManagedBean().setActiveIndex(1);
			retriveSupplierQualificationCriteria(QTConstants.SUPPLIER_CRITERIA);
			if(isRoleValidationPopUpMessagefor() && (SCProcessNameConst.ENDORSE_SCORING.equals(this
					.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_SCORING.equals(this
							.getManagedBean().getTaskName()))){
				this.getManagedBean().setCheckUserCanRevise(true);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_1, null,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2_A,
								new String[] { changedCatCodeList }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));
			}else{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_1, null,
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
								QTConstants.LBL_SC_CATEGORY_HAS_CHANGES_ON_PRERENDER_VIEW_2, new String[] { changedCatCodeList },
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			}
		}
		// #29732(#232) - End

		committeSO = null;
		this.getManagedBean().setLoginRoleType("SC");

		// 11104WIP -start-
		this.getManagedBean().setSampleShowHide(checkSampleRadioShowHideInSpecPrepareAndMaintenance());

		retriveTechnicalCheckList();

		checkSampleWipData(this.getManagedBean().getScQtCheckSt2techProposal(), this.getManagedBean().getQt());
		// 11104WIP -end-

		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.SPECIFICICATON_COMM_PREPARATION);
	}

	/**
	 * Spec committe Maintenance bpm task.Event handle Submit/Revise/Accept in
	 * specification committee.
	 * 
	 * @param action
	 *            (String action)
	 * @return navigate to acknowledgement page.
	 */
	public void specCommitteMaintenanceBpmTask(String action) {
		log.debug("start specCommitteMaintenanceBpmTask");
		// #29732(#232) reuse existing code- start
		if (!isRoleValidationfromTaskListAllowRevise()
				&& !action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)
				|| isRoleValidationfromTaskListSpecEndorsement()
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
				|| isRoleValidationfromTaskListAllowRevise()
				&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE) || action
						.equalsIgnoreCase(BpmOutcomeConst.BPM_SIGNOFF))) {
			if (isRoleValidationfromTaskList()) {
				this.getManagedBean().setCategoryDescriptionChange(true); // 29732
			}
		   	//#29732 check if submit with no category code before checking category code change
			this.getManagedBean().setFromSubmitCatChange(true);
			String changedCatCodeList = checkCatCodeChange();
			if (!changedCatCodeList.isEmpty()) {
				if(isRoleValidationPopUpMessagefor() && (SCProcessNameConst.ENDORSE_SCORING.equals(this
						.getManagedBean().getTaskName()) || SCProcessNameConst.SIGNOFF_SCORING.equals(this
								.getManagedBean().getTaskName()))){
					this.getManagedBean().setCheckUserCanRevise(true);
				}
				this.getManagedBean().setCatCodeList(changedCatCodeList);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
		}
		// #29732(#232) End
		// #1873 #34837 -start-
		if (assertPendingFinancialSubmission() && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT))
			return;
		//�#1873�#34837 -end-
		
		FacesScopes facesScopes = new FacesScopes();

		PmUser curLogedInUser = facesScopes.getUserSessionBean();
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		committeReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		committeReqDTO.setPmUser(curLogedInUser);
		committeReqDTO
				.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
		CommitteResDTO CommitteeresDTO = committeSO
				.retrieveCommitteMemberByUserId(committeReqDTO);
		ScCommitteeMember member = CommitteeresDTO.getCommMember();
		if (member != null
				&& member
						.getMemberRole()
						.getParameterCode()
						.equalsIgnoreCase(
								ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			this.getManagedBean().setLocalOutCome(action);
			//#15391 - start
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();
				List<ScQtSpecificationRemark> remarks = this.getManagedBean().getSpecificationRemarks();
				
				if (null != remarks) {
					log.info("Hello");
					for (int i = 0; i < remarks.size(); i++) {
						//ScQtSpecificationRemark Remark = remarks.get(0);
						String userRemark = remarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							log.info("Hello1");
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						}
					}
				}
				RequestContext
				.getCurrentInstance()
				.execute(
						"revicespecMaintenanceEndorsmentDlgs.show()");
			} else {
				getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
			//#15391 - end

		} else {
			this.getManagedBean().setLocalOutCome(action);

			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				retriveCommitteeEndorsment();
				List<ScQtSpecificationRemark> remarks = this.getManagedBean().getSpecificationRemarks();
				
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						ScQtSpecificationRemark Remark = remarks.get(0);
						String userRemark = remarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { ResourceBundleUtils.getMessage(
												    "lbl.sc.remarks",
												    ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												    new FacesScopes().getUserLocale()) });
						} else if (null == Remark.getRemarkId()) {
							RequestContext
									.getCurrentInstance()
									.execute(
											"revicespecMaintenanceEndorsmentDlgs.show()");
						} else {
							reviseSpecificationCommitteeEndorsmentMaintenance();
						}
					}

				}

			} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)
					|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
							&& !this.getManagedBean().isReadOnly()) {
						// boolean technical =
						// validateTechnicalStandardCheckListSubmit();
						/*
						 * if (!technical && !this.getManagedBean()
						 * .isTechnicalOtherChecklistSubmit()) { RequestContext
						 * .getCurrentInstance() .execute(
						 * "technicalStandardChecklistDlgsSubmit.show()");
						 * return null; }
						 */
						// boolean financial =
						// validateFinancialStandardCheckListSubmit();
						/*
						 * if (!financial && !this.getManagedBean()
						 * .isFinancialOtherChecklistSubmit()) { RequestContext
						 * .getCurrentInstance() .execute(
						 * "financialStandardChecklistDlgsSubmit.show()");
						 * return null; }
						 */

					}
					SpecificationResDTO resDTO = loadSpecMaintenanceBPmDetails(
							action, null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						successMessage(getMessgeConstant(action),
								getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(),
								Boolean.TRUE);
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
					SpecificationResDTO resDTO = loadSpecMaintenanceBPmDetails(
							action, null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						successMessage(getMessgeConstant(action),
								getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(),
								Boolean.TRUE);
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					SpecificationResDTO resDTO = loadSpecMaintenanceBPmDetails(
							action, null);
					if (resDTO.isSuccessful()) {
						ScQt qt = resDTO.getScQt();
						successMessage(getMessgeConstant(action),
								getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(),
								Boolean.TRUE);
					}
				}
			}
		}
		committeSO = null;
	}

	private SpecificationResDTO loadSpecMaintenanceBPmDetails(String action,
			PmDigiSign digiSign) {
		log.debug("start loadSpecMaintenanceBPmDetails" + action);
		SpecificationResDTO resDTO = new SpecificationResDTO();
		ScQt scQt = this.getManagedBean().getQt();
		loadPrepareSpecCommitteeMaintenanceDetails(scQt.getQtId());
		SpecificationReqDTO spReqDTO = constructSpecCommitteDtls(null,false);	//Bug #35688@Syazani 10/07/2024 #29732 adding param
		spReqDTO.setOutcome(action);
		//#15391 - start
		List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();
		if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
			for (int i = 0; i < checklistRemoveList.size(); i++) {
				if (null == checklistRemoveList.get(i).getQtChecklistId()) {
					checklistRemoveList.remove(i);
				}
			}
		}
		
		List<ScQtDeliveryLoc> deliveryRemoveList = this.getManagedBean()
				.getDeliveryLocRemoveList();
		if (null != deliveryRemoveList && !deliveryRemoveList.isEmpty()) {
			for (int i = 0; i < deliveryRemoveList.size(); i++) {
				if (null == deliveryRemoveList.get(i).getQtDeliveryLocId()) {
					deliveryRemoveList.remove(i);
				}
			}
		}
		
		signoffCommitteeActivity(spReqDTO, action);
		if (null != digiSign) {
			updatePmDigiSignData(digiSign,
					ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT,
					scQt.getQtId(), scQt.getQtNo());
		}

		// #29474 - start
		List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
				.getRemoveDeductions();
		if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
			for (int i = 0; i < deductionRemoveList.size(); i++) {
				if (null == deductionRemoveList.get(i).getQtDeductionId()) {
					deductionRemoveList.remove(i);
				}
			}
		}

		// #29732(#232) - start
		List<ScQtCategory> removeCategoryCodes = this.getManagedBean().getTobeRemoveCategoryCodes();
		if (null != removeCategoryCodes && !removeCategoryCodes.isEmpty()) {
			for (int i = 0; i < removeCategoryCodes.size(); i++) {
				if (null == removeCategoryCodes.get(i).getQtCategoryId()) {
					removeCategoryCodes.remove(removeCategoryCodes.get(i));
				}
			}
		}
		// #29732(#232) - end
		
		spReqDTO.setTobeRemoveCategoryCodes(removeCategoryCodes);// #29732(#232)
		spReqDTO.setRemoveDeductions(deductionRemoveList);// #29474 - end
		spReqDTO.setTobeRemoveList(checklistRemoveList);
		spReqDTO.setTobeRemoveDeliveryLocs(deliveryRemoveList);//#15391 - end
		spReqDTO.setDigiSignData(digiSign);
		spReqDTO.setTaskId(this.getManagedBean().getTaskId());
		spReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		FacesScopes facesScopes = new FacesScopes();
		spReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		spReqDTO.setPmUser(facesScopes.getUserSessionBean());
		spReqDTO.setRoleCode(getUserRoleCode());
		spReqDTO.setPrepareSpecStatus(this.getManagedBean()
				.getCurrentStatusId());
		spReqDTO.setUserLocale(facesScopes.getUserLocale());
		spReqDTO.setOfferScoreType(this.getManagedBean().getOfferScoreType());//#25138
		spReqDTO.setSingleItem(this.getManagedBean().isSingleItem());//32453
		spReqDTO.setScoreOfferPriceList(this.getManagedBean().getOfferPricePackageList());//#25138

		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)){
			//Bug #26214
			spReqDTO.setAllowZeroVariance(true);
			checkTechnicalAndFinancialSpec(this.getManagedBean().getQt()); // Bug #34065@Syazani 01/11/2022
			spReqDTO.setSampleExist(checkISSampleExistWIPChecklist());//11104-Reendose
		}
		IQTSpecSO specSO = getSpecSO();
		try {
			resDTO = specSO.updateSpecEndorsmentBPmAction(spReqDTO);

		} finally {
			specSO = null;
		}
		log.debug("end updateSpecCommitteMaintenaceDtls" + action);
		return resDTO;

	}

	private void retrieveStatusByTypeAndId(String docType, Long docId) {
		log.debug("Start retrieveStatusByTypeAndId");
		IScWorkFlowStatusSO workFlowStatusSO;
		try {
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION,
					IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(docType);
			reqDTO.setDocId(docId);

			reqDTO.setStatusIds(Arrays
					.asList(QTStatusConst.SE_PENDING_SUBMISSION));
			ScWorkFlowStatusResDTO workFlowResDTo = workFlowStatusSO
					.retrieveStatusByTypeAndId(reqDTO);
			if (null != workFlowResDTo.getWorkFlowStatus()) {
				this.getManagedBean().setSpecMaintenanceWorkFlowStatus(
						workFlowResDTo.getWorkFlowStatus());
			}
		} finally {
			workFlowStatusSO = null;
		}
	}

	/**
	 * Revise specification committee Maintenance endorsment.Event handle revise
	 * in specification committee .
	 * 
	 * @return navigate to acknowledgement page.
	 */
	public void reviseSpecificationCommitteeEndorsmentMaintenance() {
		log.debug("start reviseSpecificationCommitteeEndorsmentMaintenance");
		SpecificationResDTO resDTO = loadSpecMaintenanceBPmDetails(this
				.getManagedBean().getLocalOutCome(), null);
		if (resDTO.isSuccessful()) {
			ScQt qt = resDTO.getScQt();
			successMessage(getMessgeConstant(this.getManagedBean().getLocalOutCome()),
					getMessageCommon("lbl.sc.specification.preparation.and.scoring.for"), qt.getQtNo(), Boolean.TRUE);
		}
	}

	private void resetCommitteeActivity(SpecificationReqDTO spReqDTO) {

		ScWorkFlowStatus workFlowStatus = this.getManagedBean()
				.getSpecMaintenanceWorkFlowStatus();
		if (null != workFlowStatus
				&& QTStatusConst.SE_PENDING_SUBMISSION.equals(workFlowStatus
						.getPmStatus().getStatusId())) {

			List<ScQtCommitteeActivity> committeActivities = this
					.getManagedBean().getCommitteeActivities();
			if (committeActivities == null) {
				retriveCommitteeEndorsment();
				committeActivities = this.getManagedBean()
						.getCommitteeActivities();
			}
			boolean isModified = false;
			for (ScQtCommitteeActivity activity : committeActivities) {
				if (activity.isSignedOff()) {
					activity.setSignedOff(false);
					isModified = true;
				}
			}
			if (isModified) {// to prevent optimistic lock
				spReqDTO.setCommitteActivities(committeActivities);
			}
			this.getManagedBean().setCommitteeActivities(committeActivities);
		}
	}

	private void signoffCommitteeActivity(SpecificationReqDTO spReqDTO,
			String action) {

		List<ScQtCommitteeActivity> committeActivities = this.getManagedBean()
				.getCommitteeActivities();
		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(action)) {
			if (null != committeActivities) {

				for (ScQtCommitteeActivity committeActivity : committeActivities) {
					if (ParameterConst.COMMITTEE_ROLE_SECRETARY
							.equalsIgnoreCase(committeActivity
									.getScCommitteeMember().getMemberRole()
									.getParameterCode())) {
						committeActivity.setSignedOff(true);
					} else {
						committeActivity.setSignedOff(false);
					}

				}
				List<ScQtSpecificationRemark> remarks = super
						.finalizeRemarks(getManagedBean()
								.getSpecificationRemarks());
				if (null != remarks) {
					ScQt localScQt = spReqDTO.getScQt();
					if (null != remarks) {
						for (int i = 0; i < remarks.size(); i++) {
							String userRemark = remarks.get(i).getRemark();
							if (null == userRemark || "".equals(userRemark)) {
								remarks.remove(i);
							}
						}
					}
					localScQt.setScQtSpecificationRemarks(remarks);
					spReqDTO.setScQt(localScQt);
				}
			}
			spReqDTO.setCommitteActivities(committeActivities);

		} else {
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				List<ScQtSpecificationRemark> remarks = super
						.finalizeRemarks(getManagedBean()
								.getSpecificationRemarks());

				if (null != remarks) {
					ScQt localScQt = spReqDTO.getScQt();
					if (null != remarks) {
						for (int i = 0; i < remarks.size(); i++) {
							String userRemark = remarks.get(i).getRemark();
							if (null == userRemark || "".equals(userRemark)) {
								remarks.remove(i);
							}
						}
					}
					localScQt.setScQtSpecificationRemarks(remarks);
					spReqDTO.setScQt(localScQt);
				}

			} else {
				if (null != committeActivities) {
					PmUser pmUser = new FacesScopes().getUserSessionBean();
					for (ScQtCommitteeActivity committeActivity : committeActivities) {
						if (pmUser.getIdentificationNo().equals(
								committeActivity.getScCommitteeMember()
										.getPmUser().getIdentificationNo())) {
							committeActivity.setSignedOff(true);
							break;
						}
					}
				}
				spReqDTO.setCommitteActivities(committeActivities);
			}
		}
	}

	/**
	 * technicalChecklist TemplateSpec back action.Event handle navigate to
	 * specfication committee Menu page.
	 * 
	 * @return the string
	 */
	public String technicalSpecBackAction() {
		checkPanelSpecs();

		return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
	}

	private void checkPanelSpecs() {
		if (this.getManagedBean().getQt().isPanel()) {
			boolean isSpecAvilable = isAtleastTechnicalSpec(this
					.getManagedBean().getScQtCheckSt2techProposal());
			log.debug("isSpecAvilable" + isSpecAvilable);
			if (!isSpecAvilable) {
				log.debug("isSpecAvilable" + false);
				getManagedBean().setZoneGeneralTrees(new ArrayList<TreeNode>());
				log.debug("zonetrees"
						+ getManagedBean().getZoneGeneralTrees().size());
			}
		}
	}
	
	// #32134 - start
	private ITemplateSpecSO templateSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
	}
	// #32134 - end

	private void checkFinancialSpecEstimations(
			List<ScQtCheckList> financialChecklist, ScQtCheckList qtChecklist,
			ScQt qt, boolean isSave){ //Bug #30046
		log.info("checkFinancialSpecEstimations");
		int count = 0;
		
		//Bug_33142 - Start
		Set<Long> duplicateSpecQuestionEstimationCheck = new HashSet<Long>();
		Set<Long> duplicateSpecZoneEstimationCheck = new HashSet<Long>();
		boolean specQuestionDuplicate = false;
		//Bug_33142 - End
		
		if (CollectionUtils.isNotEmpty(financialChecklist)) {
			for (ScQtCheckList checklist : financialChecklist) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
						.equalsIgnoreCase(checklist.getDefinationType()
								.getParameterCode())
						&& QTStatusConst.TS_COMPLETED.equals(checklist
								.getPmStatus().getStatusId())
						&& checklist.getRecordStatusFlg().equals(
								DbValueConst.RECORD_STATUS_ACTIVE)) {
					count++;
					break;
				}
			}
			log.info("checkFinancialSpecEstimations count" + count);
			//#25729 #142 start - checking total indicative price <= 20k
			//Intended Price
		BigDecimal totalSpecEstimatedAmount=BigDecimal.valueOf(0);//Bug #29806

			if (qtChecklist != null
					&& null != qtChecklist.getScQtDoc()
					&& null != qtChecklist.getScQtDoc().getTplSpecMaster()
					&& qtChecklist.getScQtDoc().getTplSpecMaster()
							.getTplSpecQuestions() != null) {
				for (TplSpecQuestion tplSpecQuestion : qtChecklist
						.getScQtDoc().getTplSpecMaster()
						.getTplSpecQuestions()) {
					// #32130 - Start.
					if (qt.isZonal() && CollectionUtils.isNotEmpty(tplSpecQuestion.getTplSpecEstimations()) 
							&& CollectionUtils.isNotEmpty(tplSpecQuestion.getTplSpecZoneQties())) {
							Iterator<TplSpecEstimation> iterator = tplSpecQuestion.getTplSpecEstimations().iterator();
							while (iterator.hasNext()) {
								TplSpecEstimation tplSpecEstimation = iterator.next();
								if (tplSpecEstimation.getTplSpecZoneDtl() == null) {
									iterator.remove();
								}
							}
					}
					
					if (CollectionUtils.isNotEmpty(tplSpecQuestion.getTplSpecEstimations())) {
						for (TplSpecEstimation tplSpecEstimation : tplSpecQuestion
								.getTplSpecEstimations()) {
							// #25138 - Bug #32194 - Start.
							/*if (tplSpecEstimation.getEstimatedCost() != null) {*/
							//20201217@haziq- Bug #29806 - Start
							BigDecimal estimatedCost = tplSpecEstimation.getEstimatedCost();
							//20210205@haziq- Bug #30046 - Start
							BigDecimal indicativeUnitPrice = tplSpecEstimation.getIndicativeUnitPrice();
							//#25138  - Allow indicativeUnitPrice & estimatedCost null if qty 0 - start
							if (CollectionUtils.isNotEmpty(tplSpecQuestion.getTplSpecZoneQties())) {
								for(TplSpecZoneQty zoneQty : tplSpecQuestion.getTplSpecZoneQties()){
									if(zoneQty.getQty().compareTo(BigDecimal.ZERO) > 0 
											// #25138 - Bug #32194 - Start
											&& null != zoneQty.getTplSpecZoneDtl()
											&& null != tplSpecEstimation.getTplSpecZoneDtl()
											&& zoneQty.getTplSpecZoneDtl().getSpecZoneId()
											.equals(tplSpecEstimation.getTplSpecZoneDtl().getSpecZoneId())
											// #25138 - Bug #32194 - End
											){
										if (indicativeUnitPrice == null && !isSave) {
											throw new EpException(MessageConst.ERROR_760520);
										}
										// #33493 - Start.
										// #25138 - Bug #32194 - Start.
										else if (indicativeUnitPrice != null && indicativeUnitPrice.compareTo(BigDecimal.ZERO) == 0) {//20230712@asyrafhaziq- SQUBE Bug NullPointer#3 
										// #25138 - Bug #32194 - End.
										//Bug #32477 related to #25138
										//comment code replace new condition
										//else if ( indicativeUnitPrice != null  && (estimatedCost==null || estimatedCost == null)) {
											//else if ( indicativeUnitPrice != null  && (estimatedCost==null || estimatedCost.compareTo(BigDecimal.ZERO) == 0)) {
										// #33493 - End.
											throw new EpException(MessageConst.ERROR_760519, 
													new String[]{ 
													ResourceBundleUtils.getMessage(
															"lbl.tpl.spec.content.Indicative.unit.price", 
															ResourceBundleUtils.BUNDLE_COMMON_LABELS,
															new FacesScopes().getUserLocale()) });		
											
										}	
									}
									// #33493 - Start.
									//Bug #32477 related to #25138
									//add new condition
									/*else if(zoneQty.getQty().compareTo(BigDecimal.ZERO) == 0){
										estimatedCost = BigDecimal.valueOf(0);
										indicativeUnitPrice = BigDecimal.valueOf(0);
									}*/
									// #33493 - End.
								}//#25138  - Allow indicativeUnitPrice & estimatedCost null if qty 0 - end
								// #33493 - Start.
								if (estimatedCost == null) {
									estimatedCost = BigDecimal.valueOf(0);
								}
								
								if (indicativeUnitPrice == null) {
									indicativeUnitPrice = BigDecimal.valueOf(0);
								}
								// #33493 - Start.
							}
							
							// #33572 - Start.
							if (null != this.getManagedBean().getQt() && !this.getManagedBean().getQt().isZonal()) {
								if (indicativeUnitPrice == null && !isSave) {
									throw new EpException(MessageConst.ERROR_760520);
								}
								else if ( indicativeUnitPrice != null  && estimatedCost.compareTo(BigDecimal.ZERO) == 0){		
									throw new EpException(MessageConst.ERROR_760519, 
											new String[]{ 
											ResourceBundleUtils.getMessage(
													"lbl.tpl.spec.content.Indicative.unit.price", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS,
													new FacesScopes().getUserLocale()) });
								}
							}
							// #33572 - End.
							
							//20210205@haziq- Bug #30046 - End
							if (null != estimatedCost) {
								totalSpecEstimatedAmount = totalSpecEstimatedAmount
										.add(estimatedCost.setScale(2, QTConstants.QT_ROUNDING_MODE));	//Bug #35600@Syazani 19/09/2024
							}
							//20201217@haziq- Bug #29806 - End
							/*}*/
							// #25138 - Bug #32194 - End.
							
							//Bug_33142 - Start
							if(tplSpecEstimation.getTplSpecQuestion() != null && tplSpecEstimation.getTplSpecZoneDtl()!= null){
								if(!duplicateSpecQuestionEstimationCheck.add(tplSpecEstimation.getTplSpecQuestion().getSpecQuestionId())){
									specQuestionDuplicate = true;
								}
								if(!duplicateSpecZoneEstimationCheck.add(tplSpecEstimation.getTplSpecZoneDtl().getSpecZoneId())){
									if(specQuestionDuplicate){
										log.info("Duplicate Spec Estimation ::SpecQuestionID: " + tplSpecEstimation.getTplSpecQuestion().getSpecQuestionId() + 
												"SpecZoneDtlID:" + tplSpecEstimation.getTplSpecZoneDtl().getSpecZoneId());
									}
								}
							}
							//Bug_33142 - End
						}
					} else {
						//Bug #34100 - Start
						this.getManagedBean().getScQtChecklist().getPmStatus().setStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
						//Bug #34100 - End
						throw new EpException(MessageConst.ERROR_760520);
					}
					// #32130 - End.
				}
			}
			log.debug("totalSpecEstimatedAmount" + totalSpecEstimatedAmount);
			if (count == 0) { //Bug #29806
				// Intended Price
				if (qt.isPanel()) {
					BigDecimal panelThreshold = getPanelThresholdAmount();

					if (null == totalSpecEstimatedAmount
							|| (totalSpecEstimatedAmount
									.compareTo(panelThreshold) <= 0)) {
						throw new EpException(MessageConst.ERROR_760380,
								new String[] { SourcingConstants.DF_AMOUNT
										.format(panelThreshold) });
					}
				} else {
					//Bug #32477 related to #25138 #32134-start
					//comment code replace new condition
					//if (null == totalSpecEstimatedAmount || (totalSpecEstimatedAmount.compareTo(BigDecimal.ZERO) <= 0)) {
					if (null == totalSpecEstimatedAmount) {
						throw new EpException(MessageConst.ERROR_760024,
								new String[] { SourcingConstants.DF_AMOUNT.format(BigDecimal.ZERO) });
					}//32134-End
				}
				//Bug #29808
				qt.setEstimatedAmount(totalSpecEstimatedAmount.setScale(2, QTConstants.QT_ROUNDING_MODE));	//Bug #35600@Syazani 19/09/2024
			}
			// Check Intended Price
			BigDecimal indPriceThreshold = getTotalIndPriceThresholdAmount();
			if (null == qt.getTmpExpectedIntendedPrice()
						|| (qt.getTmpExpectedIntendedPrice()
								.compareTo(indPriceThreshold) <= 0)) {
				if (null == totalSpecEstimatedAmount
						|| (totalSpecEstimatedAmount
								.compareTo(indPriceThreshold) > 0)) {
					throw new EpException(MessageConst.ERROR_760504,
							new String[] { SourcingConstants.DF_AMOUNT
								.format(indPriceThreshold) });
				}
			// #32275
			//}
			// #32134 - start
			if(count == 1){
				TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
				reqDTO.setQtId(qt.getQtId());
				TplSpecMaster tplSpecMaster = qtChecklist != null ?qtChecklist.getScQtDoc()
						.getTplSpecMaster():null;//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
				if (tplSpecMaster != null) {
					reqDTO.setExcludeSpecMasterId(tplSpecMaster.getSpecMasterId());
				}
				BigDecimal newTotalSpecEstimatedAmount = templateSpecSO()
						.retrieveTotalSpecEstimationsByQtIdAndZone(reqDTO)
						.getEstimatedPrices();
				if (newTotalSpecEstimatedAmount == null) {
					newTotalSpecEstimatedAmount = BigDecimal.valueOf(0);
				}
				if (tplSpecMaster != null
						&& tplSpecMaster.getTplSpecQuestions() != null) {
					for (TplSpecQuestion tplSpecQuestion : tplSpecMaster
							.getTplSpecQuestions()) {
						for (TplSpecEstimation tplSpecEstimation : tplSpecQuestion
								.getTplSpecEstimations()) {
							if (tplSpecEstimation.getEstimatedCost() != null) {
								newTotalSpecEstimatedAmount = newTotalSpecEstimatedAmount
										.add(tplSpecEstimation.getEstimatedCost());
							}
						}
					}
				}
				
				newTotalSpecEstimatedAmount.setScale(2, QTConstants.QT_ROUNDING_MODE);	//Bug #35600@Syazani 19/09/2024
				if (newTotalSpecEstimatedAmount
								.compareTo(indPriceThreshold) > 0) {
					throw new EpException(MessageConst.ERROR_760504,
							new String[] { SourcingConstants.DF_AMOUNT
								.format(indPriceThreshold) });
				}
			}
			// #32134 - end
			// #32275
			}
		}
		//#25729 #142 end
	}

	private void checkTechnicalFinancialItemCount(
			ScQtCheckList technicalChecklist, ScQtCheckList financialCheckList) {
		try {
			// log.info("TechnicalQuestionSize"+technicalChecklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions().size());
			// log.info("FinancialQuestionSize"+financialCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions().size());
			List<TplSpecQuestion> technicalQuestionList = new ArrayList<TplSpecQuestion>();
			if (CollectionUtils.isNotEmpty(technicalChecklist.getScQtDoc()
					.getTplSpecMaster().getTplSpecQuestions())) {
				for (TplSpecQuestion technicalQuestion : technicalChecklist
						.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
					if (TemplateSpecConst.QUESTION_TYPE_ITEM
							.equalsIgnoreCase(technicalQuestion
									.getQuestionType())) {
						technicalQuestionList.add(technicalQuestion);
					}
				}
				// technicalQuestionList.add(new TplSpecQuestion());
			}
			// log.info("finalTechnicalQuestionSize"+technicalQuestionList.size());
			if (technicalQuestionList.size() != financialCheckList.getScQtDoc()
					.getTplSpecMaster().getTplSpecQuestions().size()) {
				throw new EpException(MessageConst.ERROR_760469);

			}
			technicalQuestionList = null;
		} finally {

		}
	}

	private boolean checkSequenceMorethanOne(
			List<ScQtCheckList> financialSpecs, ScQtCheckList technicalSpec) {
		boolean moreThanOne = false;
		int count = 0;
		log.info("TechnicalSequence" + technicalSpec.getChecklistSeq());
		for (ScQtCheckList financialspec : financialSpecs) {
			log.info("financialSequence" + financialspec.getChecklistSeq());
			if (financialspec.getChecklistSeq().equals(
					technicalSpec.getChecklistSeq())) {
				count++;

			}

		}
		log.info("checkSequenceMorethanOne" + count + "QTID:"
				+ this.getManagedBean().getQt().getQtId());
		if (count > 1) {
			moreThanOne = true;
			return moreThanOne;
		}
		return moreThanOne;
	}

	/**
	 * The method used to check pairing document.
	 * 
	 * @param specCommitteeFinacialDocumentStandardChecklist
	 */
	public void CheckboxPairing(int docGroupId, boolean selected)
			throws Exception {
		List<PmDocument> financialDocumentList = this.getManagedBean()
				.getStageTwoFinComplianceList();
		this.getManagedBean().setStageTwoFinComplianceList(
				new ArrayList<PmDocument>());
		for (PmDocument document : financialDocumentList) {
			if (document.getDocGroupId() != null) {
				if (document.getDocGroupId() == docGroupId) {
					if (selected) {
						document.setSelected(true);
					} else {
						document.setSelected(false);
					}
				}
			}
			this.getManagedBean().getStageTwoFinComplianceList().add(document);
		}
	}

	/**
	 * #13955 check mode = tender - checkIsDisable=true check mode = quotation -
	 * checkIsDisable=false to PROCUREMENT_GROUP_ASSET and
	 * PROCUREMENT_GROUP_OVERDRAF
	 * 
	 * @param isDisable
	 * @return (boolean true/false).
	 */
	public boolean checkIsDisable(ScQtCheckList checkList) {
		boolean isDisable = false;
		ScQt scQt = this.getManagedBean().getQt();

		// if tender
		if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scQt
				.getProcurementMode().getParameterCode())) {
			if (checkList.getPmDocument() != null) {
				if (checkList.getPmDocument().getDocGroupId() != null) {
					for (ScQtCheckList scQtCheckList : this.getManagedBean()
							.getScQtCheckSt2financeProposalList()) {
						if (scQtCheckList.getPmDocument() != null) {
							if (checkList.getPmDocument().getDocGroupId() == scQtCheckList
									.getPmDocument().getDocGroupId()) {
								isDisable = true;
							}
						}
					}
				} else {
					isDisable = true;
				}
			} else {
				isDisable = true;
			}
		}

		// if quotation
		if (ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
				.getProcurementMode().getParameterCode())) {
			if (checkList.getPmDocument() != null) {
				if (checkList.getPmDocument().getDocGroupId() != null) {
					for (ScQtCheckList scQtCheckList : this.getManagedBean()
							.getScQtCheckSt2financeProposalList()) {
						if (scQtCheckList.getPmDocument() != null) {
							if (checkList.getPmDocument().getDocGroupId() == scQtCheckList
									.getPmDocument().getDocGroupId()) {
								if (checkList.getPmDocument().getDocGroupId() == ParameterConst.PROCUREMENT_GROUP_ASSET
										|| checkList.getPmDocument()
												.getDocGroupId() == ParameterConst.PROCUREMENT_GROUP_OVERDRAF) {
									isDisable = false;
								} else {
									isDisable = true;
								}
							}
						}
					}
				} else {
					isDisable = true;
				}
			} else {
				isDisable = true;
			}
		}
		return isDisable;
	}

	private void resetTechnicalWeightage(ScQtScoreSetting technicalScoreSetting) {
		if (null != technicalScoreSetting
				&& !technicalScoreSetting.isWeightageReq()) {
			log.info("====resetTechnicalWeightage===="
					+ technicalScoreSetting.getSettingType());
			List<ScQtCheckList> allTechnicalChecklist = this.getManagedBean()
					.getScQtCheckSt2techProposal();
			for (ScQtCheckList technicalChecklist : allTechnicalChecklist) {
				if (null != technicalChecklist.getQtScoreWeightage()) {
					technicalChecklist.getQtScoreWeightage().setWeightage(
							new BigDecimal(0));
				}
			}
		}
	}

	private void resetFinancialWeightage(ScQtScoreSetting financialScoreSetting) {
		if (null != financialScoreSetting
				&& !financialScoreSetting.isWeightageReq()) {
			log.info("====resetFinancialWeightage===="
					+ financialScoreSetting.getSettingType());
			List<ScQtCheckList> allFinancialChecklist = this.getManagedBean()
					.getScQtCheckSt2financeProposalList();
			for (ScQtCheckList financialChecklist : allFinancialChecklist) {
				if (null != financialChecklist.getQtScoreWeightage()) {
					financialChecklist.getQtScoreWeightage().setWeightage(
							new BigDecimal(0));
				}
			}
		}
	}

	public void checkFinancialparentSpecQuestion(
			ScQtCheckList financialSpecChecklist) {
		log.info("====checkFinancialparentSpecQuestionExecuted=======");
		if (CollectionUtils.isNotEmpty(financialSpecChecklist.getScQtDoc()
				.getTplSpecMaster().getTplSpecQuestions())) {
			for (TplSpecQuestion financialQuestion : financialSpecChecklist
					.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
				log.info("=====financialQuestion.getParentSpecQuestion()===="
						+ financialQuestion.getParentSpecQuestion());
				if (null == financialQuestion.getParentSpecQuestion()
						|| null == financialQuestion.getParentSpecQuestion()
								.getSpecQuestionId()) {
					throw new EpException(MessageConst.ERROR_760483,
							new String[] { financialSpecChecklist.getTitle() });
				}

			}
		}
	}
	
	/**
	 * @param e
	 */
	public void selectAllSchedule(AjaxBehaviorEvent e) {

		for (ScQtSchedule ScQtSchedule : getManagedBean().getScheduleList()
				) {
			if (getManagedBean().isSelectAll()) {
				ScQtSchedule.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtSchedule.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getProgressivePaymentList()) {
			if (getManagedBean().isSelectAll()) {
				ScQtProgressivePayment.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtProgressivePayment.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
	}

	public void selectOneScheCheckboxSelect(AjaxBehaviorEvent e) {
		getManagedBean().setSelectAll(false);
		for (ScQtSchedule ScQtSchedule : getManagedBean().getScheduleList()) {
			if (ScQtSchedule.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getProgressivePaymentList()) {
			if (ScQtProgressivePayment.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			getManagedBean().setButtonHideSchActive(false);
		} else {
			getManagedBean().setButtonHideSchActive(true);
		}
	}
	
	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTSpecSO getQtSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}
	
	//#25022-start	
	public void deleteFreqScheduleDetails() {
		SpecificationResDTO resDTO = null;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
				.getScheduleList());
		reqDTO.setQtId(this.getManagedBean().getQt().getQtId());

		try {
			if (null != getManagedBean().getScheduleList()) {

				resDTO = getQtSpecSO().deleteQtScheduleIdAndPaymentId(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);

				}
				getManagedBean().setScheduleList(resDTO.getToBeRemoveSchedulesList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}
	
	public void deleteProgPaymentDetails() {
		SpecificationResDTO resDTO = null;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setToBeRemoveProgressivePayments(getManagedBean()
				.getProgressivePaymentList());
		reqDTO.setQtId(getManagedBean().getQt().getQtId());
		try {
			if (null != getManagedBean().getProgressivePaymentList()) {
				
				resDTO = getQtSpecSO().deleteQtScheduleIdAndPaymentId(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);
					
				}
				getManagedBean().setProgressivePaymentList(resDTO.getToBeRemoveProgressivePayments());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}//#25022-end	
	
	private void checkProcurementModeValue(){
		log.debug("===========checkProcurementCodeValue============");
		if(null!=this.getManagedBean().getQt() && null==this.getManagedBean().getQt().getProcurementMode()){
			BigDecimal estimatedAmount=null;
			if(null!=this.getManagedBean().getQt().getEstimatedAmount() && this.getManagedBean().getQt().getEstimatedAmount().compareTo(BigDecimal.ZERO)>0){
				estimatedAmount=this.getManagedBean().getQt().getEstimatedAmount();
			}else{
				estimatedAmount=this.getManagedBean().getQt().getTmpExpectedIntendedPrice();
			}
			log.info("mangalatestChangedCodesExecutedestimatedAmount"+estimatedAmount);
			if(null==estimatedAmount){
				estimatedAmount=new BigDecimal(0);
			}
			new CreateQtBackingBean().setProcurementMode(this.getManagedBean().getQt(),
					estimatedAmount);
		}
	}
    /*25633 BEGINNING*/

	public String technicalChecklistEditActionExport(List<String> item, List<String> specification, List<String> uom,
			List<String> priceType, List<String> quantity, List<String> score, int index, ScQtCheckList checkList,
			String specType) {

		//IQTSpecSO specSo = null;
		//SpecificationResDTO resDTO = new SpecificationResDTO();
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setToBeRemoveList(new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		try {

			//SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			//specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);

			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
				this.getManagedBean().setSpecStatusId(checkList.getPmStatus().getStatusId());
				this.getManagedBean().setIndex(index);
				this.getManagedBean().setScQtChecklist(checkList);
				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);

				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);

				TemplateSpecResDTO templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
				checkList.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());

				TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
				templateSpecBean.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());

				List<TplSpecQuestion> tplBacker = new ArrayList<TplSpecQuestion>();

				for (int i = 0; i < item.size(); i++) {

					tplBacker.add(new TplSpecQuestion());
					TplSpecQuestion tpmQuest = tplBacker.get(i);

					if (!"".equalsIgnoreCase(item.get(i))) {
						tpmQuest.setClItem(item.get(i));
						tpmQuest.setQuestionType("I");
						tpmQuest.setQuestionOrder(i + 1);

					}
					if (!"".equalsIgnoreCase(specification.get(i))) {
						tpmQuest.setClSpecDetail(specification.get(i));
						tpmQuest.setQuestionType("S");
						tpmQuest.setQuestionOrder(i + 1);

					}
					if (!"".equalsIgnoreCase(uom.get(i))) {

						PmUom find = new PmUom();
						find = retrieveUomById(Long.parseLong(uom.get(i).replaceAll("([0-9])\\.0+([^0-9]|$)", "$1$2")));

						String b = SpecificationBackingBean.retriveUomDescOnly(find);
						String c = SpecificationBackingBean.retriveUomCodeOnly(find);

						PmUom a = new PmUom();
						a.setUomId(Long.parseLong(uom.get(i).replaceAll("([0-9])\\.0+([^0-9]|$)", "$1$2")));
						a.setUomName(b);
						a.setUomCode(c);
						tpmQuest.setClUom(a);

					}
					if (!"".equalsIgnoreCase(quantity.get(i))) {
						tpmQuest.setClQuantity(new BigDecimal(quantity.get(i)));

					}

					if (!"".equalsIgnoreCase(priceType.get(i))) {
						PmParameter pmfind = new PmParameter();

						if (priceType.get(i).equals("Normal Standard") || priceType.get(i).equals("Biasa Standard")) {

							pmfind = retrieveParameterById(Long.valueOf(294));

						} else if (priceType.get(i).equals("Tier Standard")
								|| priceType.get(i).equals("Berperingkat Standard")) {

							pmfind = retrieveParameterById(Long.valueOf(296));

						}
						tpmQuest.setClPriceTypeCode(pmfind);

					}
					if (!"".equalsIgnoreCase(score.get(i)) && tpmQuest.getClSpecDetail() != null) {

						if ("Yes/ No".equalsIgnoreCase(score.get(i))) {
							PmParameter pmfindBinary = new PmParameter();
							pmfindBinary = retrieveParameterById(Long.parseLong("164"));
							tpmQuest.setAnswerTypeCode(pmfindBinary);

						} else if ("Numeric".equalsIgnoreCase(score.get(i))) {
							PmParameter pmfindNumber = new PmParameter();
							pmfindNumber = retrieveParameterById(Long.parseLong("163"));
							tpmQuest.setAnswerTypeCode(pmfindNumber);

						} else if ("Text".equalsIgnoreCase(score.get(i))) {
							PmParameter pmfindText = new PmParameter();
							pmfindText = retrieveParameterById(Long.parseLong("162"));
							tpmQuest.setAnswerTypeCode(pmfindText);

						}

					}
				}

				templateSpecBean.getSpecMaster().setTplSpecQuestions(tplBacker);

				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean().getQt().getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveSpecdetails(this.getManagedBean().getQt().getQtId());
					ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(this.getManagedBean()
							.getZoneGeneralTrees(), null);

					if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {

						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone().setTplSpecZoneDtls(businessOwnerZone);
					}

				}
				if (null != checkList.getPmStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList.getPmStatus().getStatusId())) {
					ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
					ScQtZoneCriteria zoneCriteria = null;
					if (null != supplierCriteria && null != supplierCriteria.getQtZoneCriterias()) {
						if (this.getManagedBean().getQt().isZonal()) {
							zoneCriteria = supplierCriteria.getQtZoneCriterias();
						}
					}
					TemplateSpecHelper.getInstance().updateZoneDtl(templateSpecBean.getSpecMaster(), zoneCriteria);
				}
				if (this.getManagedBean().getQt().isPanel()) {
					this.getManagedBean().setPanelItemCategory("");
					this.getManagedBean().setSpecNewable(true);
					this.getManagedBean().setUpdateSpec(true);
					if (null == this.getManagedBean().getPanelItemCategory()
							|| "".equals(this.getManagedBean().getPanelItemCategory())) {

						if (null != templateresDTO.getSpecMaster()
								&& null == templateresDTO.getSpecMaster().getCtPanelItem()) {
							this.getManagedBean().setPanelItemCategory(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC);
						} else {
							this.getManagedBean().setPanelItemCategory(ParameterConst.ITEM_CATEGORY_TYPE_GENERAL);
						}
					}
					if ((null != this.getManagedBean().getPanelItemCategory() && !"".equals(this.getManagedBean()
							.getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())) {
						this.getManagedBean().setSpecNewable(false);
						this.getManagedBean().setUpdateSpec(false);
					} else if ((null != this.getManagedBean().getPanelItemCategory() && !"".equals(this
							.getManagedBean().getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_GENERAL.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())) {

						setTechnicalSpecEnableDisable();
					}
				} else {
					setTechnicalSpecEnableDisable();
				}

				return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
			}
		} finally {
			//specSo = null;
		}

		return null;

	}
	public boolean checkTplSpecQuestion(){
		TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
		boolean a = templateSpecBean.getSpecMaster().getTplSpecQuestions().isEmpty();
		return a;
	}
	
	public String handleFileUploadExport(FileUploadEvent event) throws FileNotFoundException, IOException,
			EncryptedDocumentException, InvalidFormatException {
		log.info("handleFileUploadExport - BEGIN");
		//boolean hasDataFlag = true;
		List<String> item = new ArrayList<String>();
		List<String> specification = new ArrayList<String>();
		List<String> uom = new ArrayList<String>();
		List<String> priceType = new ArrayList<String>();
		List<String> quantity = new ArrayList<String>();
		List<String> score = new ArrayList<String>();

		InputStream file;

		HSSFWorkbook workbook = null;
		XSSFWorkbook workbookxlsx = null;

		log.info("File Upload - BEGIN");

		if (event.getFile().getFileName().endsWith("xlsx")) {
			try {
				file = event.getFile().getInputstream();
				workbookxlsx = new XSSFWorkbook(file);
				workbookxlsx.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);			

				XSSFSheet sheetxlsx = workbookxlsx.getSheetAt(0);
	
				DataFormatter dataFormatter = new DataFormatter();
				Iterator<Row> rowIterator = sheetxlsx.rowIterator();
	
				while (rowIterator.hasNext()) {
	
					Row row = rowIterator.next();
					if (row.getRowNum() == 0) {
						continue;
					}
					//hasDataFlag = (row != null);
	
					if (row.getCell(0).toString() != "" || row.getCell(0).toString() != null) {
						item.add(row.getCell(0).toString());
					} else {
						item.add("");
					}
					if (row.getCell(1).toString() != "" || row.getCell(1).toString() != null) {
						specification.add(row.getCell(1).toString());
					} else {
						specification.add("");
					}
					if (row.getCell(2).toString() != "" || row.getCell(2).toString() != null) {
						String tempUom = row.getCell(2).toString();
						String firstThreeChars = "";
						
						if (tempUom.length() > 3) {
							firstThreeChars = tempUom.substring(0, 3);
						} else {
							firstThreeChars = tempUom;
						}
						log.info("firstThreeChars xlsx"+firstThreeChars);
						
						uom.add(firstThreeChars);
					} else {
						uom.add("");
					}
					if (row.getCell(3).toString() != "" || row.getCell(3).toString() != null) {
						priceType.add(row.getCell(3).toString());
					} else {
						priceType.add("");
					}
					if (row.getCell(4).toString() != "" || row.getCell(4).toString() != null) {
						quantity.add(row.getCell(4).toString());
					} else {
						quantity.add("");
					}
					if (row.getCell(5).toString() != "" || row.getCell(5).toString() != null) {
						score.add(row.getCell(5).toString());
					} else {
						score.add("");
					}
	
					Iterator<Cell> cellIterator = row.cellIterator();
	
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String cellValue = dataFormatter.formatCellValue(cell);
						log.info(cellValue + "\t");
					}
					log.info("\n");
				}
			} catch (IOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
				e.printStackTrace();
			} finally {
				workbookxlsx.close();
			}

		} else {
			try {
				file = event.getFile().getInputstream();
				workbook = new HSSFWorkbook(file);
				workbook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
			
				HSSFSheet sheet = workbook.getSheetAt(0);
				DataFormatter dataFormatter = new DataFormatter();
				Iterator<Row> rowIterator = sheet.rowIterator();
	
				while (rowIterator.hasNext()) {
	
					Row row = rowIterator.next();
					//hasDataFlag = (row != null);
	
					if (row.getCell(0).toString() != "" || row.getCell(0).toString() != null) {
						item.add(row.getCell(0).toString());
					} else {
						item.add("");
					}
					if (row.getCell(1).toString() != "" || row.getCell(1).toString() != null) {
						specification.add(row.getCell(1).toString());
					} else {
						specification.add("");
					}
					if (row.getCell(2).toString() != "" || row.getCell(2).toString() != null) {
						String tempUom = row.getCell(2).toString();
						String firstThreeChars = "";
						
						if (tempUom.length() > 3) {
							firstThreeChars = tempUom.substring(0, 3);
						} else {
							firstThreeChars = tempUom;
						}
						log.info("firstThreeChars xls"+firstThreeChars);
						
						uom.add(firstThreeChars);
					} else {
						uom.add("");
					}
					if (row.getCell(3).toString() != "" || row.getCell(3).toString() != null) {
						priceType.add(row.getCell(3).toString());
					} else {
						priceType.add("");
					}
					if (row.getCell(4).toString() != "" || row.getCell(4).toString() != null) {
						quantity.add(row.getCell(4).toString());
					} else {
						quantity.add("");
					}
					if (row.getCell(5).toString() != "" || row.getCell(5).toString() != null) {
						score.add(row.getCell(5).toString());
					} else {
						score.add("");
					}
	
					Iterator<Cell> cellIterator = row.cellIterator();
	
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String cellValue = dataFormatter.formatCellValue(cell);
						log.info(cellValue + "\t");
					}
					log.info("\n");
				}
			} catch (IOException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
				e.printStackTrace();
			} finally {
				workbook.close();
			}
		}

		int index = 0;
		String specType = "T";
		ScQtCheckList checkList = this.getManagedBean().getScQtChecklist();

		technicalChecklistEditActionExport(item, specification, uom, priceType, quantity, score, index, checkList, specType);

		log.info("handleFileUploadExport - END");
		return "/xhtml/sc/boSctemplatespec/committeeTechnicalEditAction.xhtml?faces-redirect=true";
	}

	public void reload() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
	}

	public void handleFileUploadImport() throws FileNotFoundException, IOException, EncryptedDocumentException,
			InvalidFormatException {
		log.info("handleFileUploadImport - BEGIN");
		String fileName = "ImportData.xlsx";
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		List<String> itemm = new ArrayList<String>();
		List<String> specificationn = new ArrayList<String>();
		List<String> uomm = new ArrayList<String>();
		List<String> priceTypee = new ArrayList<String>();
		List<String> quantityy = new ArrayList<String>();
		List<String> scoree = new ArrayList<String>();

		TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();

		specInfo = new ArrayList<SpecInfo>();

		for (int i = 0; i < templateSpecBean.getSpecMaster().getTplSpecQuestions().size(); i++) {
			int j = i + 1;

			if (j > templateSpecBean.getSpecMaster().getTplSpecQuestions().size()) {
				itemm.add("");
				specificationn.add("");
				quantityy.add("");
				uomm.add("");
				priceTypee.add("");
				scoree.add("");
			} else {

				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClItem() != null) {
					itemm.add(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClItem());
				} else {
					itemm.add("");
				}
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClSpecDetail() != null) {
					specificationn.add(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClSpecDetail());

				} else {
					specificationn.add("");

				}
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClQuantity() != null) {
					quantityy.add(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClQuantity()
							.toString());

				} else {
					quantityy.add("");
				}
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUom() != null) {

					uomm.add(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUom().getUomId().toString()+" - "+templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUom().getUomCode()+" - "+templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUom().getUomName());

				} else {
					uomm.add("");

				}
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClPriceTypeCode() != null) {

					priceTypee.add(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClPriceTypeCode()
							.getPmParameterDescsSpecCallOnly().get(0).getCodeName());
				} else {
					priceTypee.add("");
				}

				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getAnswerTypeCode() != null) {
					if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getAnswerTypeCode()
							.getParameterId().toString().equals("164")) {
						scoree.add("Yes/ No");
					} else if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getAnswerTypeCode()
							.getParameterId().toString().equals("163")) {
						scoree.add("Numeric");
					} else if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getAnswerTypeCode()
							.getParameterId().toString().equals("162")) {
						scoree.add("Text");
					}
				} else {
					scoree.add("");

				}

				specInfo.add(new SpecInfo(itemm.get(i), specificationn.get(i), quantityy.get(i), uomm.get(i),
						priceTypee.get(i), scoree.get(i)));

			}

		}

		log.info("itemm is : " + itemm.toString());
		log.info("specificationn is : " + specificationn.toString());
		log.info("uomm is : " + uomm.toString());
		log.info("priceTypee is : " + priceTypee.toString());
		log.info("quantityy is : " + quantityy.toString());
		log.info("scoree is : " + scoree.toString());
		log.info("specInfo is : " + specInfo.toString());

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet(" Specification Info ");

		XSSFRow row;
		XSSFSheet sheet = workbook.getSheetAt(0);

		Map<Integer, Object[]> empinfo = new TreeMap<Integer, Object[]>();
		empinfo.put(0, new Object[] { "Item", "Specification", "UOM", "Price Type", "Quantity", "Scoring setting" });

		for (int k = 1; k <= specInfo.size(); k++) {
			empinfo.put(k,
					new Object[] { itemm.get(k - 1), specificationn.get(k - 1), uomm.get(k - 1), priceTypee.get(k - 1),
							quantityy.get(k - 1), scoree.get(k - 1) });
		}

		Set<Integer> keyid = empinfo.keySet();

		int rowid = 0;

		for (Integer key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = empinfo.get(key);
			int cellid = 0;

			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}
		makeRowBold(workbook, sheet.getRow(0));
	
		FacesContext facesContext = FacesContext.getCurrentInstance();
		PortletRequest req = (PortletRequest) facesContext.getExternalContext().getRequest();
		HttpServletRequest httpServletRequest = (HttpServletRequest) PortalUtil.getHttpServletRequest(req);
		PortletResponse response = (PortletResponse) facesContext.getExternalContext().getResponse();
		HttpServletResponse httpServletResponse = (HttpServletResponse) PortalUtil.getHttpServletResponse(response);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);

		workbook.close();

		httpServletResponse.setHeader("Content-Disposition", "attachment; filename=ImportData.xlsx");
		httpServletResponse.setContentType(contentType);
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentLength(bos.size());
		ServletResponseUtil.sendFile(httpServletRequest, httpServletResponse, fileName, bos.toByteArray());

		log.info("httpServletResponse is : " + httpServletResponse);
		log.info("httpServletResponse.toString() is : " + httpServletResponse.toString());
		log.info("httpServletResponse.getOutputStream() is : " + httpServletResponse.getOutputStream());

		log.info("ImportData.xlsx written successfully");
		log.info("handleFileUploadImport - END");
	}

	public static void makeRowBold(Workbook wb, Row row) {
		CellStyle style = wb.createCellStyle();// Create style
		Font font = wb.createFont();// Create font
		font.setBold(true);// Make font bold
		style.setFont(font);// set it to bold

		for (int i = 0; i < row.getLastCellNum(); i++) {// For each cell in the
														// row
			row.getCell(i).setCellStyle(style);// Set the sty;e
		}
	}

	public PmUom retrieveUomById(long uomId) {

		// Get the parameter description list from map.
		PmUom pmUom = uomMap.get(uomId);

		if (pmUom == null) {
			// Get uom from db directly.
			IUomSO uomSO;

			try {
				uomSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUomSO.class);

				UomReqDTO reqDTO = new UomReqDTO();
				reqDTO.setUomId(uomId);

				// Get PmUom from db.
				UomResDTO resDTO = uomSO.retrieveUomByUomId(reqDTO);
				pmUom = resDTO.getPmUom();

				// Put uom into map.
				uomMap.put(uomId, pmUom);

			} finally {
				uomSO = null;
			}
		}
		return pmUom;
	}

	public PmParameter retrieveParameterById(long pmId) {

		PmParameter pmParam = PmParameterMap.get(pmId);

		if (pmParam == null) {
			// Get uom from db directly.
			IParameterSO parameterSO;

			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);

				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterId(pmId);

				// Get PmUom from db.
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				pmParam = resDTO.getPmParameter();

				// Put uom into map.
				PmParameterMap.put(pmId, pmParam);

			} finally {
				parameterSO = null;
			}
		}
		log.info("pmParam is : " + pmParam);
		return pmParam;
	}
	/*End of 25633*/
	
	
	
	/**
	 * Calculate Technical Item score.calculate Item Technical Scores in
	 * TechnicalScoring Menu.
	 * 
	 * @param Technical
	 *            (ScQtScoreSetting technicalItem).
	 */
// #21695 #28949 START FARID - 15/10/2020	
	public void calculateTechnicalItemScore(ScQtScoreSetting technicalItem) {
		try {
			if (null != technicalItem) {
				String checkDecimal1 = String.valueOf(technicalItem.getOverallTechnical());
				String checkDecimal2 = String.valueOf(technicalItem.getOverallOtherTechnical());
				String pattern1 = "^[0-9\\s]+$";
				if(this.getManagedBean().getTechnicalItemScoreSetting().getOverallTechnical() != null 
						&& this.getManagedBean().getTechnicalItemScoreSetting().getOverallOtherTechnical() != null){
					if (!checkDecimal1.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}else if (!checkDecimal2.matches(pattern1)) {
							throw new EpException(MessageConst.ERROR_775157);
						}
				}
				int value1 = technicalItem.getOverallTechnical() != null ? technicalItem
						.getOverallTechnical().intValue() : 0;
				int value2 = technicalItem.getOverallOtherTechnical() != null ? technicalItem
						.getOverallOtherTechnical().intValue() : 0;
				int local = value1 + value2;
				this.getManagedBean().setTechnicalItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getTechnicalItemScore())
						&& (this.getManagedBean().getTechnicalItemScore()
								.intValue() < 100 || this.getManagedBean()
								.getTechnicalItemScore().intValue() > 100)) {
//#21695 Farid 30/7/2020 Start					
					throw new EpException(MessageConst.ERROR_7602571);
//#21695 Farid 30/7/2020 End
				}
// #21695 #28949 farid END 15/10/2020
			}

		} finally {

		}
	}
	
	/**
	 * Calculate Technical Item score.calculate Item Technical Scores in
	 * TechnicalScoring Menu.
	 * 
	 * @param financial
	 *            (ScQtScoreSetting technical item).
	 */
	private void technicalItemScoreCalculate(ScQtScoreSetting technicalItem) {
		try {
			if (null != technicalItem) {
				if (null == technicalItem.getOverallTechnical()) {
//#21695 Farid 30/7/2020 Start
					throw new EpException(MessageConst.ERROR_7602561);
				}
				if (technicalItem.getOverallTechnical().intValue() <= 0) {
					throw new EpException(MessageConst.ERROR_7602691);
				}
				if (null == technicalItem.getOverallOtherTechnical()) {
					throw new EpException(MessageConst.ERROR_7602561);
				}
//#21695 Farid 30/7/2020 End
				int value1 = technicalItem.getOverallTechnical() != null ? technicalItem
						.getOverallTechnical().intValue() : 0;
				int value2 = technicalItem.getOverallOtherTechnical() != null ? technicalItem
						.getOverallOtherTechnical().intValue() : 0;
				int local = value1 + value2;
				this.getManagedBean().setTechnicalItemScore(
						new BigDecimal(local));
				if ((null != this.getManagedBean().getTechnicalItemScore())
						&& (this.getManagedBean().getTechnicalItemScore()
								.intValue() < 100 || this.getManagedBean()
								.getTechnicalItemScore().intValue() > 100)) {
//#21695 Farid 30/7/2020 Start					
					throw new EpException(MessageConst.ERROR_7602571);
//#21695 Farid 30/7/2020 End
				}
			}

		} finally {

		}
	}
	// #21695 END FARID

	// #25039#67#76 - start
	// To recalculate estimate amount (Item Total Indicative Price)
	private void reCalculateEstimateAmount(TemplateSpecBean templateSpecBean){
		log.info("===========reCalculateEstimateAmount============");
		//20201113@asyrafhaziq_Bug #29591 - start
		BigDecimal contractDuration = new BigDecimal(0);
		//20201113@asyrafhaziq_Bug #29591 - end
		Boolean isItemTypeService = false;
		Boolean isFulfilmentTypeScheduled = false;	
		Boolean isZonal = false;
		// #25726 START
		Boolean isFulfilmentTypeNonPeriodicScheduled = false;	
		// #25726 END
		
		if (templateSpecBean.getSpecMaster().getTplSpecZone() != null) {
			isZonal = true;
		}		
		if (this.getManagedBean().getQt().getContractDuration() != null) {
			contractDuration = BigDecimal.valueOf(this.getManagedBean().getQt().getContractDuration());
		} 

		// #25039#67#92 - start
		// #25726 START
		if (this.getManagedBean().getFulfilmentType().equals(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
			isFulfilmentTypeScheduled = true;
		}
		if(this.getManagedBean().getFulfilmentType().equals(ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED)){
			isFulfilmentTypeNonPeriodicScheduled = true;
		}
		//#25726 END
		if(getManagedBean().getTemplateSpecBean().getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)){
			isItemTypeService = true;
		}
		// #25039#67#92 - end
		
		int questionSize = templateSpecBean.getSpecMaster().getTplSpecQuestions().size();
		
		if (questionSize != 0){
			for (int i = 0; i < questionSize; i++) {
				//20201113@asyrafhaziq_Bug #29591 - start
				Double indicativeUnitPrice = 0.00;  
				BigDecimal clQuantity = new BigDecimal(0); 
				BigDecimal clUomFrequency = new BigDecimal(0);  
				BigDecimal clUomPerDay = new BigDecimal(0);  
				BigDecimal clUomPerMonth = new BigDecimal(0); 
				Long specEstimationId =0L ; // #25039 21-3-2020
				long specZoneId = 0; 
				BigDecimal questionId = new BigDecimal(0);
				//20201113@asyrafhaziq_Bug #29591 - End
				int estimationSize =templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().size();
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClQuantity() != null) {
					clQuantity = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClQuantity();
				} 
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomFrequency() != null) {
					clUomFrequency = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomFrequency();
				} 
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomPerDay() != null) {
					clUomPerDay = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomPerDay();
				} 				
				if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomPerMonth() != null) {
					clUomPerMonth = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getClUomPerMonth();
				} 
				if (estimationSize!= 0){
					for (int x = 0; x < estimationSize; x++) {
						if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getIndicativeUnitPrice() != null) {
							indicativeUnitPrice = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getIndicativeUnitPrice().doubleValue();
						} 
						else {
							indicativeUnitPrice = 0.00 ;
						}
						

                           if (isZonal == true) {
							if(null!=templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getTplSpecZoneDtl()){ //#25039 start 21-3-2020
								long zoneEstimationId = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getTplSpecZoneDtl().getSpecZoneId();
								int zoneQtySize = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().size();
								for (int y = 0; y < zoneQtySize; y++) {
									if (zoneEstimationId == templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().get(y).getTplSpecZoneDtl().getSpecZoneId()){
										if (templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().get(y).getQty() != null) {
											clQuantity = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().get(y).getQty() ;
											specZoneId = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().get(y).getSpecZoneQtyId() ;
										} 
										//#21695 FARID set input text indicative price to 0 if quantity set to 0 start
											if(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecZoneQties().get(y).getQty().equals(new BigDecimal(0))){
												this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).setIndicativeUnitPrice(new BigDecimal(0));
										}
										//#21695 FARID END
									}
								}
							} //#25039 end 21-3-2020
						}
                         if(null!=templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getSpecEstimationId()){  //#25039 start 21-3-2020
                        	 specEstimationId = templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getTplSpecEstimations().get(x).getSpecEstimationId() ;
                        	
                         } //#25039 End
						questionId = BigDecimal.valueOf(templateSpecBean.getSpecMaster().getTplSpecQuestions().get(i).getSpecQuestionId());
						 
/*							log.info("i is : " + i + " x is "+ x );
							log.info("clQuantity is : " + clQuantity);
							log.info("clUomFrequency is : " + clUomFrequency);
							log.info("clUomPerDay is : " + clUomPerDay);
							log.info("clUomPerMonth is : " + clUomPerMonth);
							log.info("contractDuration is : " + contractDuration);
							log.info("isItemTypeService is : " + isItemTypeService);
							log.info("isFulfilmentTypeScheduled is : " + isFulfilmentTypeScheduled);
							log.info("specEstimationId is : " + specEstimationId);
							log.info("specZoneId is : " + specZoneId);
							log.info("questionId is : " + questionId);
							log.info("indicativeUnitPrice is : " + indicativeUnitPrice);*/
						if(null!=specEstimationId && specEstimationId>0)	{	//#25039 start 21-3-2020	
							TemplateSpecBackingBean templateSpecBackingbean = new TemplateSpecBackingBean();
							templateSpecBackingbean
									.calcItemTotalIndicateUnitPrice(templateSpecBean,
									indicativeUnitPrice, 
									clQuantity, 
									clUomFrequency, 
									clUomPerDay, 
									clUomPerMonth, 
									contractDuration, 
									specEstimationId, 
									isItemTypeService,
									isFulfilmentTypeScheduled, 
									specZoneId, 
									questionId,
									isFulfilmentTypeNonPeriodicScheduled); 
						}//#25039 end 21-3-2020
					}
				}
			}
		}	
	}
	// #25039#67#76 - end
	
	/*11104_start
	 * sample checklist cretaed during sample radio button select.
	 */
	public  void  createsampleChecklist(){
		List<ScQtCheckList> st2technicalproposalList = getManagedBean()
				.getScQtCheckSt2techProposal();

		if (null == st2technicalproposalList ||  this.getManagedBean().getScQtCheckSt2techProposal()
				.isEmpty()) {
			// 11104 -start-
			st2technicalproposalList = new ArrayList<>();
			// 11104 -end-
			

		}
		IParameterSO parameterSo = null;
		parameterSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ScQtCheckList checkList = new ScQtCheckList();
		checkList.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
		//String checilistTitle="Test Sample for  ".concat(this.getManagedBean().getQt().getQtNo());
		//checkList.setTitle(checilistTitle);
		ParameterReqDTO specType = new ParameterReqDTO();
		specType.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
		specType.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE);
		ParameterResDTO specTyperesDTO = parameterSo
				.retrieveParameterByTypeAndCode(specType);

		ParameterReqDTO checkListActionType = new ParameterReqDTO();
		checkListActionType
				.setParameterType(ParameterTypeConst.SC_SUPPLIER_ACTION);
		checkListActionType
				.setParameterCode(ParameterConst.CHECKLIST_ACTION_MANUAL_SUBMISSION);
		ParameterResDTO checkListactionresDTO = parameterSo
				.retrieveParameterByTypeAndCode(checkListActionType);
		checkList
				.setChecklistSeq(SourcingConstants.CHECKLIST_SEQ_TEMPLATE_SPEC);
		// PmStatus status = new PmStatus();
		// status.setStatusId(1l);
		// checkList.setPmStatus(status);
		//checkList.setScQtDoc(scqtDocDest);
		checkList.setScoringReq(Boolean.TRUE);
		checkList.setAction(checkListactionresDTO.getPmParameter());
		checkList.setScQt(this.getManagedBean().getQt());
		checkList.setDefinationType(specTyperesDTO.getPmParameter());

		// 11104wip -start-
		PmStatus status = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);

		checkList.setPmStatus(status);
		checkList.setPmEvalStatus(status);
		// 11104wip -end-

		Integer checkListSequence = checkTechnicalORFinancialAddChecklistSequence(st2technicalproposalList);

		checkList.setChecklistSeq(checkListSequence);
		// 11104wip -start-
		checkList.setSampleWip(Boolean.TRUE);
		checkList.setSampleStatus(status);
		// 11104wip -end-

		setUserDetails(checkList);
		st2technicalproposalList.add(checkList);
		this.getManagedBean().setScQtCheckSt2techProposal(
				st2technicalproposalList);
		checklistSorting(getManagedBean().getScQtCheckSt2techProposal());

	}
	/***11104-start
	 * 
	 * sample template questions view during click on view action in templatespec in sample.
	 * @param templateChecklist
	 * @return(String)
	 */
	public String sampleTemplateView(ScQtCheckList templateChecklist){
		this.getManagedBean().setSampleCheckList(templateChecklist);
		retriveSampleSpecmaster(templateChecklist,null);
		if(templateChecklist.isCloned()){//11104-start
			List<TplSpecQuestion> specSampleQuestionList=	this.getManagedBean().getSampleCheckList().getScQtDoc()
					.getTplSpecMaster().getTplSpecQuestions();
			for(TplSpecQuestion samplespecQuestion: specSampleQuestionList){
				samplespecQuestion.setSampleReq(Boolean.FALSE);
				samplespecQuestion.setSampleQuantity(null);
				samplespecQuestion.setSampleScoreTypeCode(null);
				samplespecQuestion.setSampleScore(null);
			}
			
		}//11104-End
		scoringPopUpAction(null);
		// #29187 Start Vinod
		this.retriveSampleSpecmasterForMandatorySpecChecking(templateChecklist);
		// #29187 End Vinod
		return 	TECHNICAL_CHECKLIST_SAMPLE_ITEM_VIEW;//11104-Reendorse		
	}
	/**** 11104-start
	 * 
	 * sample view back action method
	 * @return
	 */
	public String sampleViewBackAction(){//11104-Reendorse
		this.getManagedBean().getQt().setSampleReq(Boolean.TRUE);//11104ReendorseStart728
		return TECHNICAL_CHECKLIST_SAMPLE_MAIN;			
	}
	/**** 11104-start
	 * retrive samplespecmaster and questions during click on view  and search button in sample related spec.
	 * @param templateChecklist
	 * @param itemName
	 */
	public void retriveSampleSpecmaster(ScQtCheckList templateChecklist,String itemName){
		this.getManagedBean().setSampleCheckList(templateChecklist);
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO
				.setSpecMaster(templateChecklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		specReqDTO.setQuestionType(TemplateSpecConst.QUESTION_TYPE_ITEM);
		specReqDTO.setItemName(itemName);
		log.info("===========specificationBackingbeanItemName===="+itemName);
		TemplateSpecResDTO tmpresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		this.getManagedBean().getSampleCheckList().getScQtDoc().setTplSpecMaster(tmpresDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		this.getManagedBean().getSampleCheckList().getScQtDoc()
		.getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
		templateSpecBean.setSpecMaster(this.getManagedBean().getSampleCheckList().getScQtDoc()
				.getTplSpecMaster());	
		Map<Long,SampleQuestionBean> existingSampleSpecQuestion=this.getManagedBean().getExistingSampleSpecQuestions();//11104-ReEndoseStart
		existingSampleSpecQuestion=new HashMap<>();
		for(TplSpecQuestion sampleSpecQuestion:tmpresDTO.getSpecMaster().getTplSpecQuestions()){
			SampleQuestionBean questionBean=new SampleQuestionBean(sampleSpecQuestion.getSpecQuestionId(), sampleSpecQuestion.isSampleReq(),sampleSpecQuestion.getSampleQuantity(),sampleSpecQuestion.getSampleScore());
			existingSampleSpecQuestion.put(sampleSpecQuestion.getSpecQuestionId(),questionBean);
		}
		this.getManagedBean().setExistingSampleSpecQuestions(existingSampleSpecQuestion);//11104-ReEndoseEnd
		this.retriveSampleSpecmasterForMandatorySpecChecking(templateChecklist);//#29187 Added Vinod
	}
/***11104-start
 * display samplequantityerror in sample checkbox selected for sample item.
 * @param itemName
 * @return(String sampleError)
 */
	public String sampleQuantityError(String itemName){//11104-ReendorseChanges
		log.info("====sampleQuantityError===");
		String message = ResourceBundleUtils.getMessage(
				"lbl.sc.qt.spec.sample.quantity.error",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		if (null != itemName) {
			return message.concat("                       ").concat(itemName)
					.concat("                  ");// 11104
		}
		return null;
	}
/***11104-start
 * sample required checkbox selection for sample item.
 * @param specQuestion
 */
public void sampleRequiredChange(TplSpecQuestion specQuestion){
	IParameterSO parameterSo = null;
	log.info("sampleRequiredChange==="+specQuestion.isSampleReq());
	if(specQuestion.isSampleReq()){
		log.info("ifsampleRequiredChange==="+specQuestion.isSampleReq());
	parameterSo = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	ParameterReqDTO  scoreType = new ParameterReqDTO();
	scoreType
			.setParameterType(ParameterTypeConst.ANSWER_TYPE);
	scoreType
			.setParameterCode(ParameterConst.ANSWER_TYPE_TEXT);
	ParameterResDTO scoreTyperesDTO = parameterSo
			.retrieveParameterByTypeAndCode(scoreType);
	specQuestion.setSampleScoreTypeCode(scoreTyperesDTO.getPmParameter());
			// #29187 Start Vinod
			specQuestion.setMandatorySpec(true);
			// #29187 End Vinod
	}else{
		log.info("elsesampleRequiredChange==="+specQuestion.isSampleReq());
		specQuestion.setSampleQuantity(null);
		specQuestion.setSampleScoreTypeCode(null);
		specQuestion.setSampleScore(null);
		scoringPopUpAction(null);
			// #29187 Start Vinod
			specQuestion.setMandatorySpec(false);
			// #29187 Added Vinod
	}
	
	
}
/****11104-start
 * scoring action for sample item.
 * @param specQuestion
 */
public void scoringPopUpAction(TplSpecQuestion specQuestion){
	List<TplSpecQuestion> specSampleQuestionList = this.getManagedBean()
			.getSampleCheckList().getScQtDoc().getTplSpecMaster()
			.getTplSpecQuestions();
	BigDecimal sampleScore = BigDecimal.valueOf(0);
	for (TplSpecQuestion samplespecQuestion : specSampleQuestionList) {
		log.info("samplespecQuestion" + samplespecQuestion.getClItem());
		if (samplespecQuestion.isSampleReq()
				&& null != samplespecQuestion.getSampleScore()) {
			sampleScore = sampleScore.add(samplespecQuestion
					.getSampleScore());
		}

	}
	this.getManagedBean().setSampleTotalScore(sampleScore.intValue());
	if (null != specQuestion) {
		RequestContext.getCurrentInstance().execute(
				"samplescoringSetPopup.hide()");
	}
}
/**** 11104-start
 * sample items save and complete action for sample itmes.
 * @param actionRender
 * @return(String)
 */
public String saveSampleTemplateSpecDetails(boolean actionRender){//11104-ReendorseChanges
	log.info("===========saveSampleTemplateSpecDetails=====" + actionRender);
	List<TplSpecQuestion> samplequestionList = this.getManagedBean()
			.getSampleCheckList().getScQtDoc().getTplSpecMaster()
			.getTplSpecQuestions();
	log.info("=====samplequestionList=====" + samplequestionList.size());
	IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
	SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
	specReqDTO.setScQtCheckList(this.getManagedBean().getSampleCheckList());
	List<ScQtCheckList> technicalChecklist = this.getManagedBean()
			.getScQtCheckSt2techProposal();
	List<ScQtCheckList> sampleChecklist = this.getManagedBean()
			.getSampleSpecCheckList();// #11104
	for (ScQtCheckList checklist : technicalChecklist) {
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
				.equalsIgnoreCase(checklist.getDefinationType()
						.getParameterCode())) {
			specReqDTO.setSampleCheckList(checklist);
			break;
		}
	}
	List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();// 11104-start
	if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
		for (int i = 0; i < checklistRemoveList.size(); i++) {
			if (null == checklistRemoveList.get(i).getQtChecklistId()) {
				checklistRemoveList.remove(i);
			} else {
				checklistRemoveList.get(i).setDeleted(true);
			}
		}
	}
	specReqDTO.setTobeRemoveList(checklistRemoveList);// 11104-End
	specReqDTO=checkSampleSpecQuestionChange(specReqDTO);//11104_ReendorseNew
	List<TplSpecQuestion> sampleQuestions = specReqDTO
			.getScQtCheckList().getScQtDoc().getTplSpecMaster()
			.getTplSpecQuestions();// 11104-Reendose794
if(this.checkAtLeastOneSampleExist(sampleQuestions)){// 11104-Reendose794
specReqDTO.getScQtCheckList().setSampleStatus(
		 getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));//11104_ReendorseNew
}else{// 11104-Reendose794
specReqDTO.getScQtCheckList().setSampleStatus(
		 getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
}// 11104-Reendose794
	SpecificationResDTO specresDTO = specSO
			.saveSampleTemplateSpecs(specReqDTO);
	if (specresDTO.isSuccessful()) {
		specCommitteeResetData();
		retriveSampleSpecmaster(specresDTO.getScQtCheckList(), this
				.getManagedBean().getSampleItemName());
		for (ScQtCheckList checklist : technicalChecklist) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checklist.getDefinationType()
							.getParameterCode())) {
				specReqDTO.setSampleCheckList(specresDTO
						.getSampleChecklist());
				int index = technicalChecklist.indexOf(checklist);

					specReqDTO.getSampleCheckList().setSampleWip(Boolean.TRUE); // 11104WIP

				technicalChecklist.set(index,
						specresDTO.getSampleChecklist());
			} else if (specresDTO.getScQtCheckList().getQtChecklistId()
					.equals(checklist.getQtChecklistId())) {
				int index = technicalChecklist.indexOf(checklist);
				technicalChecklist
						.set(index, specresDTO.getScQtCheckList());
			}
		}
		for (ScQtCheckList checklist : sampleChecklist) {// #11104-start
			if (specresDTO.getScQtCheckList().getQtChecklistId()
					.equals(checklist.getQtChecklistId())) {
				int index = sampleChecklist.indexOf(checklist);
				sampleChecklist.set(index, specresDTO.getScQtCheckList());
				break;
			}
		}// #11104-End
		specReqDTO.setTobeRemoveList(null);// 11104-start
		getManagedBean().setMarkDeletedTechChecklist(null);
		getManagedBean().setMarkDeletedFinChecklist(null);// 11104-End
		saveSpecMaintenanceWorkFlowStatus();
			successMessage(
					MessageConst.INFO_700004,
					getMessageCommon("lbl.sc.qt.sampel.technical.panel.item.search.header"),
					"", Boolean.FALSE);
	}
	return null;
}
/***11104-start
 * reset item name in sample search scrren.
 */
public void resetSampleItem(){
	log.info("=======resetSampleItem======");
this.getManagedBean().setSampleItemName(" ");	
}
/*** 11104-start
 * validate sample items quantity and score during complete action.
 * @param specQuestions
 */
private int validateSampleItems(List<TplSpecQuestion> specQuestions,int sampleExist){//11104_newEndorsestart
	for (TplSpecQuestion question : specQuestions) {
		if (question.isSampleReq()) {
			sampleExist++;// #11104
			if (null == question.getSampleQuantity()) {
				throw new EpException(MessageConst.ERROR_760548);
			}
			if (null == question.getSampleScore()) {
				throw new EpException(MessageConst.ERROR_760549);
			}

		}
	}
return  sampleExist;
}//11104_newEndorseEnd
/***11104-start
 * check sample checkist required or not based on sample radio selection.
 * @param allTechnicalChecklist
 */
private void checksampleChecklist(List<ScQtCheckList> allTechnicalChecklist){
	if (null != allTechnicalChecklist) {//11104Reendorse
		boolean specChecklist = false;
		boolean sampleChecklist = false;
		int i = 0;// 11104
		for (ScQtCheckList checkList : allTechnicalChecklist) {			
			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode())
					&& !specChecklist) {
                                     i++;
				if ((QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList
						.getPmStatus().getStatusId()) || null != checkList
						.getSampleStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION
								.equals(checkList.getSampleStatus()
										.getStatusId()))) {
					specChecklist = true;
				} else if ((QTStatusConst.TS_COMPLETED.equals(checkList
						.getPmStatus().getStatusId())
						&& null != checkList.getSampleStatus() && QTStatusConst.TS_PENDING_SUBMISSION
							.equals(checkList.getSampleStatus()
									.getStatusId()))) {
					specChecklist = true;
				}

			} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())) {
				if (i == 0) {// 11104-Reendose794
					checkList
							.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					if(this.getManagedBean().getSpecMaintenanceScreen()
				.equals(getSpecMaintenance()) && !isQTStatusPublished(this.getManagedBean().getQt().getQtStatus().getStatusId()) && null==this.getManagedBean().getQt().getDemoNotificationCheck()) {//11104-Reendorse
						checkList
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));	
					}//11104-Reendorse
				}
				sampleChecklist = true;
			}
		}
		if (!sampleChecklist) {
			createsampleChecklist();
		}
	}
}

	// 25037 - start
	/**
	 * Submit action based on outcome.
	 * 
	 * @param outcome
	 */
	public void submitAction(String outcome) {
		assertConcurrentComittteeChangesApproval();
		if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			// 1873 - start - note 52
			if (!assertPendingFinancialSubmission()) {
				RequestContext.getCurrentInstance().execute("submitConfirmPopUp2.show()");
			}
			// 1873 - end - note 52
		} else {
			specCommitteBpmTask(outcome);
		}
	}

	private void assertConcurrentComittteeChangesApproval(){
		String taskName = this.getManagedBean().getTaskName();
		taskName = StringUtils.isNotEmpty(taskName) ? taskName : "";
		String[] scoringTaskNames = new String[] { SCProcessNameConst.FINALISE_SCORING,
				SCProcessNameConst.ENDORSE_SCORING, SCProcessNameConst.SIGNOFF_SCORING };
		String[] approvalTaskName = new String[] { SCProcessNameConst.APPROVE_COMMITTEE,
				SCProcessNameConst.REVISE_COMMITTEE };

		if (Arrays.asList(scoringTaskNames).contains(taskName)) {
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			ScQt qt = this.getManagedBean().getQt();
			bpmReqDTO.setDocNumber(qt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
			for (WorklistItemVO currentVO : resDTO.getWorklistVO().getWorklistItem()) {
				if (StringUtils.isNotBlank(currentVO.getTaskName())) {
					if (Arrays.asList(approvalTaskName).contains(currentVO.getTaskName())) {
						throw new EpException(getMessage("lbl.sc.popup.cannotSubmitWaitingCMT", null));
					}
				}
			}
		}
	}
	// 25037 - end

	//#1873-start
	private String buildQuestionCompareString(TplSpecQuestion question){
		String type = question.getQuestionType();
		StringBuilder sb = new StringBuilder("");
		if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM)){
			BigDecimal uomPerDay = question.getClUomPerDay() == null ? new BigDecimal(0): question.getClUomPerDay(); 
			BigDecimal uomFrequency = question.getClUomFrequency() == null ? new BigDecimal(0): question.getClUomFrequency(); 
			BigDecimal uomPerMonth = question.getClUomPerMonth() == null ? new BigDecimal(0): question.getClUomPerMonth(); 
			BigDecimal quantity = question.getClQuantity() == null ? new BigDecimal(0): question.getClQuantity(); 
			String priceType = question.getClPriceTypeCode() == null ? "": question.getClPriceTypeCode().getParameterCode(); 
			
			List<TplSpecTierQty> quantityTier = question.getTplSpecTierQties();
			StringBuilder quantityTierCompareSb = new StringBuilder("tierQty");
			if(CollectionUtils.isNotEmpty(quantityTier)){
				quantityTier = new ArrayList<TplSpecTierQty>(quantityTier);
				Collections.sort(quantityTier, new Comparator<TplSpecTierQty>() {
					public int compare(TplSpecTierQty o1, TplSpecTierQty o2) {
						return o1.getFromQty().compareTo(o2.getFromQty());
					}
				});
				for(TplSpecTierQty qty: quantityTier){
					BigDecimal fromQty = qty.getFromQty() == null ? new BigDecimal(0): qty.getFromQty(); 
					BigDecimal toQty = qty.getToQty() == null ? new BigDecimal(0): qty.getToQty(); 
					quantityTierCompareSb.append(fromQty.doubleValue());
					quantityTierCompareSb.append(toQty.doubleValue());
				}
			}
			
			List<TplSpecZoneQty> zoneQuantityTier = question.getTplSpecZoneQties();
			StringBuilder zoneQuantityTierCompareSb = new StringBuilder("zoneTierQty");
			if(CollectionUtils.isNotEmpty(zoneQuantityTier)){
				zoneQuantityTier = new ArrayList<TplSpecZoneQty>(zoneQuantityTier);
				Collections.sort(zoneQuantityTier, new Comparator<TplSpecZoneQty>() {
					public int compare(TplSpecZoneQty o1, TplSpecZoneQty o2) {
						return o1.getTplSpecZoneDtl().getZoneName().compareTo(o2.getTplSpecZoneDtl().getZoneName());
					}
				});
				for(TplSpecZoneQty zQty: zoneQuantityTier){
					BigDecimal qty = zQty.getQty() == null ? new BigDecimal(0): zQty.getQty(); 
					String zoneName = zQty.getTplSpecZoneDtl() == null ? "" : zQty.getTplSpecZoneDtl().getZoneName();
					zoneQuantityTierCompareSb.append(zoneName);
					zoneQuantityTierCompareSb.append(qty.doubleValue());
				}
			}

			sb.append(quantityTierCompareSb.toString());
			sb.append(zoneQuantityTierCompareSb.toString());
			sb.append(priceType);
			sb.append(question.getClItem()); // Bug #34926@Syazani 17/07/2023
			sb.append(question.getClUom().getUomCode());
			sb.append(uomFrequency.doubleValue());
			sb.append(uomPerDay.doubleValue());
			sb.append(uomPerMonth.doubleValue());
			sb.append(quantity.doubleValue());
		}
		String scoreType = question.getScoreTypeCode() == null ? "": question.getScoreTypeCode().getParameterCode();
		String clSpecDetail = question.getClSpecDetail() == null ? "": question.getClSpecDetail();
		String answerTypeCode = question.getAnswerTypeCode() == null ? "": question.getAnswerTypeCode().getParameterCode();
		sb.append(StringUtils.deleteWhitespace(clSpecDetail));
		sb.append(answerTypeCode);
		sb.append(scoreType);
		return sb.toString();
	}
	private boolean isSpecQuestionsModified(ScQtCheckList checklist){
		if(checklist.getQtChecklistId()==null){
			return true;
		}
		TplSpecMaster localSpecMaster = checklist.getScQtDoc().getTplSpecMaster();
		List<TplSpecQuestion> localQuestions = localSpecMaster.getTplSpecQuestions();
		if(CollectionUtils.isNotEmpty(localQuestions)){
			ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecMaster(localSpecMaster);
			reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TplSpecMaster currentSpecMaster = templateSpecSO.getTemplateMaster(reqDTO).getSpecMaster();
			List<TplSpecQuestion> currentQuestions = currentSpecMaster.getTplSpecQuestions();
			Map<Object, Object> currentQuestionsMap = CustomUtils.listToMap(currentQuestions, "getSpecQuestionId");
			
			String originalTitle = currentSpecMaster.getSpecName();
			
			List<ScQtCheckList> financialCheckList = this.getManagedBean().getScQtCheckSt2financeProposalList();
			boolean isOriginallyCompleted = false;
			if(CollectionUtils.isNotEmpty(financialCheckList)){
				for(ScQtCheckList financialCL: financialCheckList){
					if(financialCL.getDefinationType().getParameterCode().equals(QTConstants.TEMPLETE_SPEC_CODE)
							&& financialCL.getPmStatus().getStatusId().equals(QTStatusConst.TS_COMPLETED)
							&& financialCL.getTitle().equalsIgnoreCase(originalTitle)){
						isOriginallyCompleted = true;
						break;
					}
				}
			}
			
			Map<String, Boolean> changesFlag = new HashMap<String, Boolean>();
			for(String s: new String[]{"itemAdded", "specificationAdded", "itemDeleted", "specificationDeleted", "itemUpdated", "specificationUpdated"}){
				changesFlag.put(s, false);
			}
			for(TplSpecQuestion q: localQuestions){
				String type = q.getQuestionType();
				if(q.getSpecQuestionId()==null){
					if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM) && !changesFlag.get("itemAdded")){
						changesFlag.put("itemAdded", true);
						checklist.setEdited(true && isOriginallyCompleted);//1873 - note 52
					}
					if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC) && !changesFlag.get("specificationAdded")){
						changesFlag.put("specificationAdded", true);
					}
				}else{
					TplSpecQuestion question = (TplSpecQuestion)currentQuestionsMap.get(q.getSpecQuestionId());
					if(question==null){
						if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM) && !changesFlag.get("itemDeleted")){
							changesFlag.put("itemDeleted", true);
						}
						if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC) && !changesFlag.get("specificationDeleted")){
							changesFlag.put("specificationDeleted", true);
						}
					}else{
						String currentQuestionStr = buildQuestionCompareString(question);
						String localQuestionStr = buildQuestionCompareString(q);					
						if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM) && !changesFlag.get("itemUpdated")){
							changesFlag.put("itemUpdated", !currentQuestionStr.equals(localQuestionStr)); // Bug #34926@Syazani 17/07/2023
							//1873 - start - note 52
							boolean isEdited = checklist.isEdited()==null ? false: checklist.isEdited();//1873 note 149
							if(changesFlag.get("itemUpdated") && !isEdited){//1873 note 149
								checklist.setEdited(true && isOriginallyCompleted);
							}
							//1873 - end - note 52
						}
						if(type.equals(TemplateSpecConst.QUESTION_TYPE_ITEM_SPEC) && !changesFlag.get("specificationUpdated")){
							changesFlag.put("specificationUpdated", !currentQuestionStr.equals(localQuestionStr)); // Bug #34926@Syazani 17/07/2023
						}
					}
				}
			}
			boolean specQuestionModified = (changesFlag.get("itemAdded") || changesFlag.get("itemUpdated") || changesFlag.get("itemDeleted"));
			return  specQuestionModified || (!specQuestionModified && !isOriginallyCompleted);
		}
		return false;
	}
	//1873 - start- note 52
	private boolean assertPendingFinancialSubmission(){
		if(isTechnicalTemplatesEdited()){
			this.getManagedBean().setFromSubmit(true);
			RequestContext.getCurrentInstance().execute("alertFinancialTemplateIncompletePopup.show()");
			return true;
		}
		return false;
	}
	
	public void getMethodHandlerOnConfirmation(){
		if(this.getManagedBean().isFromSubmit()){
			qtChecklistTabChange(3);
			this.getManagedBean().setFromSubmit(false);
		}else{
			//32204 - start
			boolean check = validateTechnicalStandardCheckList();
			//1873 note 149-start
			if(check) {
				this.getManagedBean().setActiveIndex(this.getManagedBean().getCheckListYesIndex());
				if(this.getManagedBean().getActiveIndex() == 4 
						|| this.getManagedBean().getActiveIndex() == 5
						|| this.getManagedBean().getActiveIndex() == 6){
					checktemplateSpecStatus(this.getManagedBean().getQt());
					checkFinancialSpecStatus(this.getManagedBean().getQt());
				}
			};	
			//1873 note 149 end
			//32204 - end
		}
	}
	//1873 - end - note 52
	
	//#25138 Reendorsed - start
	/**
	 * create menu model for financial spec zones .
	 * 
	 */
	private void populateFinancialSpecDetailsByZone() {

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecZoneDtls())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean()
					.getTemplateSpecZoneDtls();
			//#25138 - sorting zonedtls list - start
			Collections.sort(zoneDtls, new Comparator<TplSpecZoneDtl>() {
				public int compare(TplSpecZoneDtl zoneDtl1,
						TplSpecZoneDtl zoneDtl2) {

					return zoneDtl1.getSpecZoneId().compareTo(zoneDtl2.getSpecZoneId());
				}
			});
			//#25138 - sorting scoreofferprice list - end
			this.getManagedBean().setSpecZoneTabIndex(0);
			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);
					System.out.println("zoneid===>"+specZoneDtl.getSpecZoneId());
					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					//zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("specTabChange([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]); blockWidget.hide();");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean()
					.setSpecificationSpecZonalMenuModel(zoneMenuModel);
		}
	}
	//#25138 Reendorsed - end
	
	//#25138 Reendorsed - start
	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		//log.info(message);
	}
	//#25138 Reendorsed - end
	
	//#25138 Reendorsed - start
	/**
	 * Spec tab change.Event handle changed zones in financial spec to display
	 * total max score for template spec based on zonal.
	 * 
	 * 
	 *            
	 */
	public void specTabChange() {
		
		ITemplateSpecSO templateSpecSo = null;
		try {
//			this.getManagedBean().getFinancialScoring();
//			retriveFinancialScoring();
			this.getManagedBean().setFinPoints(0);
			Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
					.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("selectedZoneTabIndex"));

			this.getManagedBean().setSpecZoneTabIndex(selectedZoneTabIndex);
			debugLogger("zoneTabChange");
			TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) getManagedBean()
					.getTemplateSpecZoneDtls().get(selectedZoneTabIndex);
			log.debug("Selected Zone : " + specZoneDtl.getZoneName());
			getManagedBean().setSelectedZone(specZoneDtl);
			templateSpecSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
			
			BigDecimal totalMaxScore = new BigDecimal(0); 
			for(TplSpecQuestion specQuestion : this.getManagedBean().getSpecQuestionList()){
				System.out.println("specquestionID====>"+specQuestion.getSpecQuestionId());
				
				TemplateSpecReqDTO reqScoreDto = new TemplateSpecReqDTO();
				TemplateSpecResDTO resScoreDto = new TemplateSpecResDTO();
				reqScoreDto.setSpecQuestionId(specQuestion.getSpecQuestionId());
				reqScoreDto.setSpecZoneId(specZoneDtl.getSpecZoneId());
				resScoreDto = templateSpecSo.retrieveMaxScoreByQuestionIdNZoneId(reqScoreDto);
				
				System.out.println("maxscore===>"+resScoreDto.getMaxScore());	
				totalMaxScore = totalMaxScore.add(resScoreDto.getMaxScore() == null ? new BigDecimal(0): resScoreDto.getMaxScore());//1873 - note 86							
				
			}
			System.out.println("totalmaxscore===>"+totalMaxScore);
			for (ScQtCheckList maxScoreList : this
					.getManagedBean().getScQtCheckSt2financeProposalList()) {
				if(maxScoreList.getDefinationType().getParameterCode()
						.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION) &&
						maxScoreList.getDocCategory().equalsIgnoreCase(QTConstants.FINANCIAL_DOC_TYPE)){
					System.out.println("weightageID===>"+maxScoreList.getQtScoreWeightage().getQtScoreWeightageId());
					if (null != maxScoreList.getQtScoreWeightage()) {
						ScQtScoreWeightage scoreWeightage = maxScoreList.getQtScoreWeightage();
						scoreWeightage.setScore(totalMaxScore);

					}
				}
				break;
			}
			finScoreSum();
			int finalScorePoint = this.getManagedBean().getFinPoints();
			//====#31913 bug related to #25138==== start
			BigDecimal totalPoints = totalMaxScore;
			//====#31913 bug related to #25138==== end
			// #25138 - 31578 - Start.
			if (null != totalPoints) {
				finalScorePoint = finalScorePoint + totalPoints.intValue();
			}
			// #25138 - 31578 - End.
			this.getManagedBean().setFinPoints(finalScorePoint);
			System.out.println("totalFinalPoint===>"+finalScorePoint);
			

		} finally {
		}
	}
	//#25138 Reendorsed - end
	
	//#25138 Reendorsed - start
	/**
	 * To save total max score after accumulated from TplSpecQuestion 
	 * max_score based on zonal
	 * 
	 * 
	 *            
	 */
	//====#31913 bug related to #25138==== start
	public void saveTotalMaxScoreItemZonal(List<ScQtScoreOfferPrice> scoreOfferPrice) {

		ITemplateSpecSO templateSpecSo = null;
		try {
			
			templateSpecSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
			int i = 0;
			for(ScQtScoreOfferPrice offerprice : scoreOfferPrice){
	//====#31913 bug related to #25138==== end
				BigDecimal totalMaxScore = new BigDecimal(0);
				/*
				 * Bug #31578 SHAFFUL START
				 */
				if(offerprice.getTplSpecZoneDtl()!=null && offerprice.getTplSpecZoneDtl().getSpecZoneId()!=null){
					for(TplSpecQuestion specQuestion : this.getManagedBean().getSpecQuestionList()){
						System.out.println("specquestionID====>"+specQuestion.getSpecQuestionId());
						
						TemplateSpecReqDTO reqScoreDto = new TemplateSpecReqDTO();
						TemplateSpecResDTO resScoreDto = new TemplateSpecResDTO();
						reqScoreDto.setSpecQuestionId(specQuestion.getSpecQuestionId());
						reqScoreDto.setSpecZoneId(offerprice.getTplSpecZoneDtl().getSpecZoneId());
						resScoreDto = templateSpecSo.retrieveMaxScoreByQuestionIdNZoneId(reqScoreDto);
						
						if(resScoreDto.getMaxScore()!=null){
							System.out.println("maxscore===>"+resScoreDto.getMaxScore());	
							totalMaxScore = totalMaxScore.add(resScoreDto.getMaxScore());
							System.out.println("totalmaxscore===>"+totalMaxScore);
						}
						
						
					}
					if(totalMaxScore!=null){
						offerprice.getTplSpecZoneDtl().setTotalMaxScore(totalMaxScore);
						//====#31913 bug related to #25138==== start
						if(i == 0){
							this.getManagedBean().setTotalmaxscore(totalMaxScore);
						}
						//====#31913 bug related to #25138==== end
					}
				}else{
					throw new EpException(MessageConst.ERROR_760152);
				}
				/*
				 * Bug #31578 END
				 */
				//====#31913 bug related to #25138==== start
				i++;
				//====#31913 bug related to #25138==== end
			}
			
		} finally {
			templateSpecSo = null;
		}

	}
	//#25138 Reendorsed - end
	
	
	//#25138 Reendorsed - start
	/**
	 * Spec offerprice package from varieance.check offer price from varienace
	 * null or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageFromVarieanceList(ScQtScoreOfferPrice offerPrice,
			List<ScQtScoreOfferPrice> offerPriceList) {
		if (null != offerPrice) {
			
			if (null == offerPrice.getFromVariance()
					|| offerPrice.getFromVariance().intValue() <= 0) {
				throw new EpException(MessageConst.ERROR_760224);
			}

	    //}//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
		if(CollectionUtils.isNotEmpty(offerPriceList)){
			for(ScQtScoreOfferPrice fromVarianceofferPrice:offerPriceList){
				fromVarianceofferPrice.setFromVariance(offerPrice.getFromVariance());
			}
		}
	  }//20230712@asyrafhaziq- SQUBE Bug NullPointer#3

	}
	//#25138 Reendorsed - end
	
	//#25138 Reendorsed - start
	/**
	 * Spec offerprice package to varieance.check offer price to varienace null
	 * or less than zero.
	 * 
	 * @param (ScQtScoreOfferPrice offerPrice).
	 */
	public void specOfferpricePackageToVarieanceList(ScQtScoreOfferPrice offerPrice,
			List<ScQtScoreOfferPrice> offerPriceList) {
		if (null != offerPrice) {
			if (null == offerPrice.getToVariance()/*
												 * ||
												 * offerPrice.getToVariance().
												 * intValue() <= 0
												 */) {
				throw new EpException(MessageConst.ERROR_760179);
			}

		//}//20230712@asyrafhaziq- SQUBE Bug NullPointer#3
		if(CollectionUtils.isNotEmpty(offerPriceList)){
			for(ScQtScoreOfferPrice toVarianceofferPrice:offerPriceList){
				toVarianceofferPrice.setToVariance(offerPrice.getToVariance());
			}
		}
	  }//20230712@asyrafhaziq- SQUBE Bug NullPointer#3

	}
	//#25138 Reendorsed - end
	
	//#25138 Reendorsed - start
	/**
	 * To get list TplSpecQuestion 
	 * 
	 */
	public void retriveSpecQuestion() {

		//IQTSpecSO specSo = null;
		ITemplateSpecSO templateSpecSo = null;
		try {
			
			List<TplSpecQuestion> newSpecQuestions = new ArrayList<TplSpecQuestion>();
			if(!this.getManagedBean().getScQtCheckSt2financeProposalList().isEmpty()){
				for (ScQtCheckList maxScoreList : this
						.getManagedBean().getScQtCheckSt2financeProposalList()) {
					if(maxScoreList.getDefinationType().getParameterCode()
							.equalsIgnoreCase(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION) &&
							maxScoreList.getDocCategory().equalsIgnoreCase(QTConstants.FINANCIAL_DOC_TYPE)){
						System.out.println("specmasterID=="+maxScoreList.getScQtDoc().getTplSpecMaster().getSpecMasterId());
						System.out.println("checlistttddd-----"+maxScoreList.getQtChecklistId());
						
						templateSpecSo = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
						List<TplSpecQuestion> specQuestions = templateSpecSo.getSpecQuestionList(maxScoreList.getScQtDoc()
								.getTplSpecMaster().getSpecMasterId());
						newSpecQuestions.addAll(specQuestions);
						
					}
				}
				this.getManagedBean().setSpecQuestionList(newSpecQuestions);
				if(newSpecQuestions.size()<=1){
					this.getManagedBean().setSingleItem(true);
				}else{
					this.getManagedBean().setSingleItem(false);
				}
			}
			
		} finally {
			//specSo = null;
			templateSpecSo = null;
		}

		
	
	}
	//#25138 Reendorsed - end
	
	/**
	 * Adds the technical check list if Tier 2 and Demonstration is true for Technical checklist
	 * 
	 * #28784
	 */
	public void addDemoTechnicalCheckList() {
		try {
			//this.getManagedBean().setReadOnly(false);
			isAllTSDemoChecklistCompleted();//#28784
			this.getManagedBean().setDemoAction(false);
			List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
			List<ScQtCheckList> newSt2technicalproposalList = new ArrayList();
			boolean demoList = false;
			for (ScQtCheckList cl : st2technicalproposalList) {
				if(null != cl.getDefinationType() && null != cl.getDefinationType().getParameterCode()){
					if (cl.getDefinationType().getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
						demoList = true;
					} else if (cl.getDefinationType().getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
						this.getManagedBean().setDemoAction(true);
					}
				}
			}
			if (null != this.getManagedBean().getQt()
					&& ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this.getManagedBean().getQt()
							.getEvaluationType()) && this.getManagedBean().getQt().isDemoRequired()) {

				if (!demoList) {
					ScQtCheckList st2technicalproposalList1 = createSpecList("2");
					st2technicalproposalList1.setDefinationType(new PmParameter());
					st2technicalproposalList1.setDocCategory(QTConstants.TECHNICAL_DOC_TYPE);
					st2technicalproposalList1.setScoringReq(true); // change from false to true(31032021) - Azmi
					Integer checkListSequence = checkTechnicalORFinancialAddChecklistSequence(st2technicalproposalList);
					st2technicalproposalList1.setChecklistSeq(checkListSequence);

					st2technicalproposalList1.setScQt(this.getManagedBean().getQt());
					st2technicalproposalList1.setAction(new PmParameter());

					setUserDetails(st2technicalproposalList1);
					st2technicalproposalList1.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					st2technicalproposalList1.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));

					IParameterSO parameterSo = BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterReqDTO specType = new ParameterReqDTO();
					specType.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					specType.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO);
					ParameterResDTO specTyperesDTO = parameterSo.retrieveParameterByTypeAndCode(specType);
					st2technicalproposalList1.setDefinationType(specTyperesDTO.getPmParameter());

					st2technicalproposalList.add(st2technicalproposalList1);

					// #28784 idham delete sample in checklist - start
					if (CollectionUtils.isNotEmpty(getManagedBean().getMarkDeletedTechChecklist())) {
						for (ScQtCheckList checklist : getManagedBean().getMarkDeletedTechChecklist()) {
							if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(checklist.getDefinationType().getParameterCode())) {
								st2technicalproposalList.remove(checklist);
							}
						}
					}
					// #28784 idham delete sample in checklist - end

					this.getManagedBean().setScQtCheckSt2techProposal(st2technicalproposalList);
					checklistSorting(getManagedBean().getScQtCheckSt2techProposal());
					this.getManagedBean().setTechStandaraChecklist(false);

				}
			} else if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equalsIgnoreCase(this.getManagedBean().getQt().getEvaluationType())
					&& !this.getManagedBean().getQt().isDemoRequired() && demoList) { // #28784

				for (ScQtCheckList cl : st2technicalproposalList) {
					if (null != cl.getDefinationType() && null != cl.getDefinationType().getParameterCode()) {
						if (!cl.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
							newSt2technicalproposalList.add(cl);
						}
					}
				}
				st2technicalproposalList = newSt2technicalproposalList;
				this.getManagedBean().setScQtCheckSt2techProposal(st2technicalproposalList);
				//#28784
			} else if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this.getManagedBean().getQt().getEvaluationType()) && demoList) {//#28784
				for (ScQtCheckList cl : st2technicalproposalList) {
					if (null != cl.getDefinationType() && null != cl.getDefinationType().getParameterCode()) {
						if (!cl.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
							newSt2technicalproposalList.add(cl);
						}
					}
				}
				st2technicalproposalList = newSt2technicalproposalList;
				this.getManagedBean().setScQtCheckSt2techProposal(st2technicalproposalList);

			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "SpecificationBackingBean", "docNo", "addDemoTechnicalCheckList", getUserIdSessionFromLoginId(), e);
		}

	}

	/**
	 * Adds the technical check list if Tier 2 and Demonstration is true for
	 * Technical checklist
	 * 
	 * #28784
	 */
	public String demoTechnicalTemplatelist() {

		List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
		List<ScQtCheckList> tempList = new ArrayList<ScQtCheckList>();
		
		//#28784 - Enable back to handle record has been updated issue
		for (ScQtCheckList l : st2technicalproposalList) {
			if (l.getQtChecklistId() == null && l.getDefinationType().getParameterCode()
					.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
				//throw new EpException(MessageConst.ERROR_760528);
				//saveSpecificationCommitteeDtls(); //#28784 - Remove notification save
				//#28784 record has been updated bug fix - Start
				if (this.getManagedBean().getQt().isZonal()) {
					QTDetailBackingBean qtDetail = new QTDetailBackingBean();
					qtDetail.retrieveZonalDetailAfterDemoDeletion(this.getManagedBean().getQt().getQtId());
				}
				//#28784 record has been updated bug fix - End
				validatePrepareQtChecklist();
				if (this.getManagedBean().isMaintanceFlow()) {
					if (isSpecCommittee()) {
						checkSpecMaintenanceOrNo(true);
					} else {
						checkSpecMaintenanceOrNo(false);
					}
				}
				//retriveTechnicalCheckList();
			}
		}
		for (ScQtCheckList cl : st2technicalproposalList) {
			if (cl.getDefinationType().getParameterCode()
					.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
				ScQtCheckList st2technicalproposalList1 = createSpecList("2");
				st2technicalproposalList1.setQtChecklistId(cl.getQtChecklistId());
				st2technicalproposalList1.setScQt(cl.getScQt());
				st2technicalproposalList1.setChecklistSeq(cl.getChecklistSeq());
				st2technicalproposalList1.setDocCategory(cl.getDocCategory());
				st2technicalproposalList1.setPmDocument(cl.getPmDocument());
				st2technicalproposalList1.setTitle(cl.getTitle());
				st2technicalproposalList1.setDefinationType(cl.getDefinationType());
				st2technicalproposalList1.setAction(cl.getAction());
				st2technicalproposalList1.setScoringReq(cl.isScoringReq());
				st2technicalproposalList1.setScQtDoc(cl.getScQtDoc());
				st2technicalproposalList1.setManditory(cl.isManditory());
				st2technicalproposalList1.setQtScoreWeightage(cl.getQtScoreWeightage());
				st2technicalproposalList1.setPmStatus(cl.getPmStatus());
				st2technicalproposalList1.setRecordStatus(cl.isRecordStatus());
				st2technicalproposalList1.setCreatedBy(cl.getCreatedBy());
				st2technicalproposalList1.setCreatedDate(cl.getCreatedDate());
				st2technicalproposalList1.setChangedBy(cl.getChangedBy());
				st2technicalproposalList1.setPmEvalStatus(cl.getPmEvalStatus());//#28784 bug #34101 buttont turn evaluate at evaluation
				st2technicalproposalList1.setChangedDate(cl.getChangedDate());
				st2technicalproposalList1.setPmDemoStatus(cl.getPmDemoStatus());
				st2technicalproposalList1.setSampleStatus(cl.getSampleStatus());//#28784 cater for sample status id null during demo save
				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(st2technicalproposalList1.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = null;
				templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
				TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
				if (null != templateresDTO) {
					st2technicalproposalList1.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
					templateSpecBean.setSpecMaster(st2technicalproposalList1.getScQtDoc().getTplSpecMaster());
				}
				tempList.add(st2technicalproposalList1);
			}
			//#28784 sample become completed after demo save - end
			if (cl.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE)) {
				log.info("adsasdwqewqewqe: " + cl.getPmStatus().getStatusId() + cl.getTitle());
				this.getManagedBean().setDemoSampleChecklistStatusId(cl.getPmStatus().getStatusId());
				log.info("adsasdwqewqewqe2: " + this.getManagedBean().getDemoSampleChecklistStatusId());
			}
			//#28784 sample become completed after demo save - end
		}
		this.getManagedBean().setDemoSpecTemplateList(tempList);
		return "/xhtml/sc/specification/specificationCommittee/demoSpecificationTechnicalList.xhtml?faces-redirect=true";

	}
	
	/**
	 * Demo Spec Technical checklist edit action.click on pencil icon in technical
	 * checklist navigate to checklist score page.
	 * 
	 * #28784	
	 */
	public String demoSpectechnicalChecklist(int index, ScQtCheckList checkList, String specType) {
		
		this.getManagedBean().setScQtChecklist(checkList);
		this.getManagedBean().setIndex(index);
		this.getManagedBean().setSpecType(specType);
		this.getManagedBean().setToBeRemoveList(null);
		this.getManagedBean().setToBeRemoveList(new ArrayList<ScQtCheckListScore>());
		this.getManagedBean().setScQtCheckListScore(null);
		this.getManagedBean().setScQtContractScore(null);
		this.getManagedBean().setItemName(null);//#28784
		try {

			List<TplSpecQuestion> newSpecItemList = new ArrayList<>();
			IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList.getDefinationType().getParameterCode())) {
				SpecificationReqDTO reqDTO = new SpecificationReqDTO();
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
				SpecificationResDTO resDTO = specSo.retrieveCheckLists(reqDTO);
				
				for(ScQtCheckList newchecklist: resDTO.getTechnicalCheckList()){
					if(newchecklist.getQtChecklistId().equals(checkList.getQtChecklistId())){
						checkList = newchecklist;
					}
				}
				this.getManagedBean().setSpecStatusId(checkList.getPmStatus().getStatusId());
				this.getManagedBean().setIndex(index);
				this.getManagedBean().setScQtChecklist(checkList);
				this.getManagedBean().setCheckListTitle(checkList.getTitle());

				ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
				TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
				specReqDTO.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
				TemplateSpecResDTO templateresDTO = null;
				templateresDTO = templateSpecSO.getTemplateMaster(specReqDTO);
				TemplateSpecBean templateSpecBean = getManagedBean().getTemplateSpecBean();
				if (null != templateresDTO) {
					checkList.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
				}
				
				if (checkList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions() != null) {
					for (TplSpecQuestion tpl : checkList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
						if (tpl.getQuestionType().equals(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
							PmParameter scoreType = getParameterByTypeAndCode(ParameterTypeConst.SCORE_TYPE,
									ParameterConst.SCORE_TYPE_USER);
							tpl.setScoreTypeCode(scoreType);
							newSpecItemList.add(tpl);
							
						} 
					}
					checkList.getScQtDoc().getTplSpecMaster().setTplSpecQuestions(newSpecItemList);
					this.getManagedBean().setDemoOriSpecQuestionTemplateList(newSpecItemList);
				}

				checkList.getScQtDoc().getTplSpecMaster().setTechnicalSpecQuestionsToDelete(null);
				templateSpecBean.setSpecMaster(checkList.getScQtDoc().getTplSpecMaster());
				if (this.getManagedBean().getQt().isZonal()
						&& this.getManagedBean().getQt().getQtType().equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
					retriveDemoSpecdetails(this.getManagedBean().getQt().getQtId());//#28784
					ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
					List<TplSpecZoneDtl> businessOwnerZone = convertTreeNodesToZoneCriteria(this.getManagedBean()
							.getZoneGeneralTrees(), null);

					if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
						for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
							supplierCriteria.getQtZoneCriterias().setModified(true);
							tplSpecZoneDtl.setTplSpecZone(supplierCriteria.getQtZoneCriterias());

						}
						templateSpecBean.getSpecMaster().setTplSpecZone(supplierCriteria.getQtZoneCriterias());
						templateSpecBean.getSpecMaster().getTplSpecZone().setTplSpecZoneDtls(businessOwnerZone);
					}

				}
				if (null != checkList.getPmStatus()
						&& QTStatusConst.TS_PENDING_SUBMISSION.equals(checkList.getPmStatus().getStatusId())) {
					ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getQt().getScQtSupplierCriteria();
					ScQtZoneCriteria zoneCriteria = null;
					if (null != supplierCriteria && null != supplierCriteria.getQtZoneCriterias()) {
						if (this.getManagedBean().getQt().isZonal()) {
							zoneCriteria = supplierCriteria.getQtZoneCriterias();
						}
					}
					TemplateSpecHelper.getInstance().updateZoneDtl(templateSpecBean.getSpecMaster(), zoneCriteria);
				}
				if (this.getManagedBean().getQt().isPanel()) {
					this.getManagedBean().setPanelItemCategory("");
					this.getManagedBean().setSpecNewable(true);
					this.getManagedBean().setUpdateSpec(true);
					if (null == this.getManagedBean().getPanelItemCategory()
							|| "".equals(this.getManagedBean().getPanelItemCategory())) {
						boolean panelItem = false;
						if (null != checkList.getScQtDoc().getTplSpecMaster().getCtPanelItem()) {
							panelItem = true;
						}

						if (null != templateresDTO && null != templateresDTO.getSpecMaster()
								&& null != templateresDTO.getSpecMaster().getCtPanelItem()) {
							panelItem = true;
						}
						if (panelItem) {
							this.getManagedBean().setPanelItemCategory(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC);
						} else {
							this.getManagedBean().setPanelItemCategory(ParameterConst.ITEM_CATEGORY_TYPE_GENERAL);
						}
					}
					if ((null != this.getManagedBean().getPanelItemCategory() && !"".equals(this.getManagedBean()
							.getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())) {
						this.getManagedBean().setSpecNewable(false);
						this.getManagedBean().setUpdateSpec(false);
					} else if ((null != this.getManagedBean().getPanelItemCategory() && !"".equals(this
							.getManagedBean().getPanelItemCategory()))
							&& ParameterConst.ITEM_CATEGORY_TYPE_GENERAL.equalsIgnoreCase(this.getManagedBean()
									.getPanelItemCategory())) {
						setTechnicalSpecEnableDisable();
					}
				} else {
					setTechnicalSpecEnableDisable();
				}

				return "/xhtml/sc/presentationDemo/presentationDemoSpecTechical.xhtml?faces-redirect=true";
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "SpecificationBackingBean", "docNo", "demoSpectechnicalChecklist", getUserIdSessionFromLoginId(), e);
		}
		return null;
	} 
	
	/**
	 * Save template for Demo spec details.user clicks on save button in demospecTechList
	 * screens.
	 * #28784
	 * @param flowType
	 *            (String flowType)
	 * @param specType
	 *            (String specType)
	 * @return the string
	 */
	public void saveDemoTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		
		//#28784 note-#647 issue 4 - Start
		for (TplSpecQuestion ques : scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
			if (this.getManagedBean().getDemoOriSpecQuestionTemplateList() != null) {
				for (TplSpecQuestion oldQues : this.getManagedBean().getDemoOriSpecQuestionTemplateList()) {
					if (oldQues.getSpecQuestionId().equals(ques.getSpecQuestionId())) {
						oldQues.setItemReqDemo(ques.isItemReqDemo());
						oldQues.setDemoScore(ques.getDemoScore());
					}
				}
			}
		}
		scQtCheckList.getScQtDoc().getTplSpecMaster().setTplSpecQuestions(this.getManagedBean().getDemoOriSpecQuestionTemplateList());
		//#28784 note-#647 issue 4 - End
				
		scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster().getSpecName());
		scQtCheckList.getScQtDoc().getTplSpecMaster().setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();

		PmStatus pendingStatus = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
		PmStatus completeStatus = getPmStatusByStatusId(QTStatusConst.TS_COMPLETED);
		scQtCheckList.setPmDemoStatus(completeStatus);
		//#28784 bug #34101 button turn evaluate at evaluation - Start
		if (null != scQtCheckList.getPmEvalStatus() && scQtCheckList.getPmEvalStatus().getStatusId().equals(QTStatusConst.TS_COMPLETED)) {
			scQtCheckList.setPmEvalStatus(completeStatus);
		} else {
			scQtCheckList.setPmEvalStatus(pendingStatus);
		}
		//#28784 bug #34101 button turn evaluate at evaluation - End
		
		List<ScQtCheckList> st2technicalproposalList = this.getManagedBean().getScQtCheckSt2techProposal();

//		for (ScQtCheckList t : st2technicalproposalList) {
		List<TplSpecQuestion> list = null;
		if (scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions() != null) {
			list = scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions();
		}
		if ((scQtCheckList.getDefinationType().getParameterCode()
				.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION))
				&& (null != list && !list.isEmpty())) {
			for (TplSpecQuestion obj : list) {
				if (BooleanUtils.isTrue(obj.isItemReqDemo()) && obj.getDemoScore() == null) {
					throw new EpException(MessageConst.ERROR_760529);
				}
			}
		}
//		}

		if (scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions() != null) {
			for (TplSpecQuestion q : scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
				if (!q.isItemReqDemo()) {
					q.setDemoScore(null);
				}
			}
		}

		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		updatedCheckLists.add(scQtCheckList);

		reqDTO.setCheckLists(updatedCheckLists);
		resetCommitteeActivity(reqDTO);
		reqDTO.setScQt(getManagedBean().getQt());
		reqDTO.setSpecChanged(getSpecMaintenance().equalsIgnoreCase(this.getManagedBean().getSpecMaintenanceScreen()));
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		/*
		 * Development #31066, Added SLA log to check saveOrUpdateDemoTemplateSpecs Performance
		 * START
		 */
		long startTime = System.currentTimeMillis();
		SpecificationResDTO resDTO = specSO.saveOrUpdateDemoTemplateSpecs(reqDTO);
		SLAUtils.getInstance().logSQLPerformanceInfo("QT","DEMO INVITATION (SAVE)","SpecificationBackingBean.saveOrUpdateDemoTemplateSpecs", startTime,"#29305");
	    
		if (resDTO.isSuccessful()) {
			SpecificationResDTO resDTO1 = new SpecificationResDTO();
			List<ScQtCheckList> updatedCheckListsDemo = new ArrayList<ScQtCheckList>();
			List<ScQtCheckList> updatedCheckListsOtherDemo = new ArrayList<ScQtCheckList>();
			SpecificationReqDTO reqDTO1 = new SpecificationReqDTO();
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			
			//#28784 set back saved values to session
			List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
			List<ScQtCheckList> newSavedCheckLists = resDTO.getSavedCheckLists();
			
			specReqDTO.setScQt(this.getManagedBean().getQt());
			specReqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
			resDTO = specSO.retrieveCheckLists(specReqDTO);
			List<ScQtCheckList> updatedNewCheckLists = resDTO.getTechnicalCheckList();
			
			for(ScQtCheckList sc: updatedNewCheckLists){
				if(sc.getQtChecklistId().equals(savedCheckLists.get(0).getQtChecklistId())){
					this.getManagedBean().setScQtChecklist(sc);//#28784 reset checklist
					newSavedCheckLists.remove(savedCheckLists.get(0));
					newSavedCheckLists.add(sc);
					setTechnicalCheckList(newSavedCheckLists.get(0));
				}
			}
			
			//#28784 - Start
			st2technicalproposalList = this.getManagedBean().getScQtCheckSt2techProposal();
			if (updatedNewCheckLists.size() <= st2technicalproposalList.size()) {
				for (ScQtCheckList st : st2technicalproposalList) {
					if (!updatedNewCheckLists.contains(st)) {
						updatedNewCheckLists.add(st);
					}
					//#28784 - Start
					for(ScQtCheckList up :updatedNewCheckLists){
						if(null != up.getQtChecklistId() &&
							up.getQtChecklistId().equals(st.getQtChecklistId())){//#28784 add null checking
							up.setPmDemoStatus(st.getPmDemoStatus());
							break;
						}
					}//#28784 - End
				}
				checklistSorting(updatedNewCheckLists);
			}//#28784 - End	
			
			for(ScQtCheckList t: updatedNewCheckLists){//#28784
				if(t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)){
					t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					updatedCheckListsDemo.add(t);
				} else if (t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE)) {
					//#28784 sample become completed after demo save - end
					if (null != this.getManagedBean().getDemoSampleChecklistStatusId()) {
						log.info("adsmnlkqwemlwqe: " + this.getManagedBean().getDemoSampleChecklistStatusId());
						if (this.getManagedBean().getDemoSampleChecklistStatusId().equals(QTStatusConst.TS_COMPLETED)) {
							t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
						} else {
							t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));						
						}
					}
					log.info("null brooooooooooooooooooooooo");
					//#28784 sample become completed after demo save - end
				}else if(t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)){
					updatedCheckListsOtherDemo.add(t);
				}
			}
			getManagedBean().setScQtCheckSt2techProposal(updatedNewCheckLists); //#28784
			repopulateDemoQuestionList(savedCheckLists);			
			reqDTO1.setCheckLists(updatedCheckListsDemo);
			reqDTO1.setTobeRemoveList(updatedCheckListsOtherDemo);
			/*
			 * Development #29305, Added SLA log to check updateDemoScoreWeightage Performance
			 * START
			 */
			startTime = System.currentTimeMillis();
			resDTO1 = specSO.updateDemoScoreWeightage(reqDTO1);
			SLAUtils.getInstance().logSQLPerformanceInfo("QT","DEMO WEIGHTAGE","QTSpecSO.updateDemoScoreWeightage", startTime,"#29305");
			/*
			 * Development #29305, Added SLA log to check updateDemoScoreWeightage Performance
			 * END
			 */
			List<ScQtCheckList> savedCheckListsDemo = resDTO1.getSavedCheckLists();
			specCommitteeResetData();
			for(ScQtCheckList obj : savedCheckListsDemo){
				resetDemoChecklist(obj);
			}
			saveSpecMaintenanceWorkFlowStatus();
			addDemoTechnicalCheckList();//#28784
			//#28784 note-#647 issue 4 - Start
			this.getManagedBean().setDemoOriSpecQuestionTemplateList(this.getManagedBean().getScQtChecklist().getScQtDoc().getTplSpecMaster().getTplSpecQuestions());
			if (StringUtils.isNotBlank(this.getManagedBean().getItemName())) {
				actionSearchPresentationDemo();
			}
			//#28784 note-#647 issue 4 - End
			updatedCheckLists = null;
			successMessage(MessageConst.INFO_700004, getMessageCommon("lbl.sc.template.spec"), "", Boolean.FALSE);
		}
	}
	
	public void repopulateDemoQuestionList(List<ScQtCheckList> savedCheckLists){
		List<TplSpecQuestion> newSpecItemList = new ArrayList<>();
		if (savedCheckLists.get(0).getScQtDoc().getTplSpecMaster().getTplSpecQuestions() != null) {
			for (TplSpecQuestion tpl : savedCheckLists.get(0).getScQtDoc().getTplSpecMaster().getTplSpecQuestions()) {
				if (tpl.getQuestionType().equals(TemplateSpecConst.QUESTION_TYPE_ITEM)) {
					PmParameter scoreType = getParameterByTypeAndCode(ParameterTypeConst.SCORE_TYPE,
							ParameterConst.SCORE_TYPE_USER);
					tpl.setScoreTypeCode(scoreType);
					newSpecItemList.add(tpl);
					
				} 
			}
			savedCheckLists.get(0).getScQtDoc().getTplSpecMaster().setTplSpecQuestions(newSpecItemList);
		}
		
	}
	
	/**
	 * Complete Demo template spec details.user clicks on complete button in demospecTechList
	 * spec screens.
	 * 
	 * @param flowType
	 *            ((String flowType)
	 * @param specType
	 *            (String specType)
	 * @return navigate to business owner or specification committee menu.
	 */
	public String completeDemoTemplateSpecDetails(String flowType, String specType) {
		IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		ScQtCheckList scQtCheckList = this.getManagedBean().getScQtChecklist();
		List<ScQtCheckList> updatedCheckLists = new ArrayList<ScQtCheckList>();
		List<ScQtCheckList> st2technicalproposalList = getManagedBean().getScQtCheckSt2techProposal();
	
		ScQt scQt = this.getManagedBean().getQt();
		if (SourcingConstants.TECHNICAL_DOCTYPE.equalsIgnoreCase(specType)) {			
			scQtCheckList.setTitle(scQtCheckList.getScQtDoc().getTplSpecMaster().getSpecName());
			scQtCheckList.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			scQtCheckList.getScQtDoc().getTplSpecMaster().setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			scQtCheckList.setSampleStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));// #11104

			TplSpecMaster specMaster = TemplateSpecHelper.getInstance().cloneTplSpecMaster(
					scQtCheckList.getScQtDoc().getTplSpecMaster(), false, true, true, false);//#28784
			specMaster.setProcessType(ParameterConst.TPL_DOC_PROCESS_TYPE_SOURCING);			

			//boolean buildFinancialTemplate = isSpecQuestionsModified(scQtCheckList);// #1873

			IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO finSpecType = new ParameterReqDTO();
			finSpecType.setParameterType(ParameterTypeConst.SPECIFICATION_TYPE);
			finSpecType.setParameterCode(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			ParameterResDTO finSpecresDTO = parameterSO.retrieveParameterByTypeAndCode(finSpecType);
			specMaster.setSpecTypeCode(finSpecresDTO.getPmParameter());
		
			TemplateSpecHelper.getInstance().cloneTplSpecQuestions(specMaster, scQtCheckList.getScQtDoc().getTplSpecMaster().getTplSpecQuestions());			
			updatedCheckLists.add(scQtCheckList);
					
			
		} else {
			if (this.getManagedBean().getSpecMaintenanceScreen().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)) {//#28784
				checkFinancialSpecEstimations(this.getManagedBean().getScQtCheckSt2financeProposalList(),
						scQtCheckList, scQt, false);// 20210205@haziq- Bug
													// #30046
				checkFinancialparentSpecQuestion(scQtCheckList);
			}
			scQtCheckList.setPmDemoStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
			scQtCheckList.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
			scQtCheckList.getScQtDoc().getTplSpecMaster().setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			updatedCheckLists.add(scQtCheckList);

			reqDTO.setScQt(scQt);

		}

		reqDTO.setCheckLists(updatedCheckLists);
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		resetCommitteeActivity(reqDTO);
		reqDTO.setSpecChanged(getSpecMaintenance().equalsIgnoreCase(this.getManagedBean().getSpecMaintenanceScreen()));
		// code if alredy checklist exist need to remove existing finalcialspec

		SpecificationResDTO resDTO = new SpecificationResDTO();
		boolean result = validateRequiredDemo(this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions());
		if(result){
			resDTO = specSO.saveOrUpdateDemoTemplateSpecs(reqDTO);
			// Need to redirect particualr page..
			if (resDTO.isSuccessful()) {
				SpecificationReqDTO reqDTO1 = new SpecificationReqDTO();
				List<ScQtCheckList> updatedCheckListsDemo = new ArrayList<ScQtCheckList>();
				List<ScQtCheckList> updatedCheckListsOtherDemo = new ArrayList<ScQtCheckList>();
				
				List<ScQtCheckList> savedCheckLists = resDTO.getSavedCheckLists();
				setTechnicalCheckList(savedCheckLists.get(0));

				boolean checkIncompleteTS = false;
				for(ScQtCheckList t: st2technicalproposalList){
					if(t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)){
						if(t.getPmDemoStatus().getStatusId().equals(QTStatusConst.TS_PENDING_SUBMISSION)){
							checkIncompleteTS = true;
							break;
						}
					}
				}
				
				for(ScQtCheckList t: st2technicalproposalList){
					if(t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)){
						if(checkIncompleteTS){
							t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
							t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
						}else{
							t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
							t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
						}
						updatedCheckListsDemo.add(t);
					}else if(t.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)){
						updatedCheckListsOtherDemo.add(t);
					}
				}
				
				SpecificationResDTO resDTO1 = new SpecificationResDTO();
				reqDTO1.setCheckLists(updatedCheckListsDemo);
				reqDTO1.setTobeRemoveList(updatedCheckListsOtherDemo);
				resDTO1 = specSO.updateDemoScoreWeightage(reqDTO1);
				
				settemplateSpecList(savedCheckLists, flowType,specType); //#11104-778&25726
				List<ScQtCheckList> savedCheckListsDemo = resDTO1.getSavedCheckLists();
				for(ScQtCheckList obj : savedCheckListsDemo){
					resetDemoChecklist(obj);
				}
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setFinancialScoring(null);
				this.getManagedBean().setOfferPricePackage(null);
				this.getManagedBean().setPanelSpecExistedOrNot(false);
				specCommitteeResetData();
				return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
			}else{
				return null;
			}
		}else{
			throw new EpException(MessageConst.ERROR_760525);
		}		
	}
	
	//#25138 Reendorsed - start
	/**
	 * To check if tplspeczoneqty max score is null, set max score from tplspecquestion  
	 * Set scqtscoreofferprice tplspeczonedtl
	 * 
	 * 
	 *            
	 */
	public void checkSpecZoneQtyMaxScore(ScQtCheckList checklist) {
		IQTSpecSO qtSpecSo = null;
		ITemplateSpecSO templateSpecSo = null;
		try {
			
			templateSpecSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
			qtSpecSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			retriveSpecQuestion();
			boolean isnullmaxscore = false;
			int count=0;
			for(TplSpecZoneDtl zone : this.getManagedBean().getTemplateSpecZoneDtls()){
				System.out.println("zoneID--------->"+zone.getZoneName());
				for(TplSpecQuestion specQuestion : this.getManagedBean().getSpecQuestionList()){
					System.out.println("specquestionID====>"+specQuestion.getSpecQuestionId());
					
					TemplateSpecReqDTO reqScoreDto = new TemplateSpecReqDTO();
					TemplateSpecResDTO resScoreDto = new TemplateSpecResDTO();
					reqScoreDto.setSpecQuestionId(specQuestion.getSpecQuestionId());
					reqScoreDto.setSpecZoneId(zone.getSpecZoneId());
					resScoreDto = templateSpecSo.retrieveMaxScoreByQuestionIdNZoneId(reqScoreDto);
					
					if(resScoreDto.getMaxScore()==null){
						isnullmaxscore = true;
						
						if (!resScoreDto.getSpecZoneQty().isEmpty()) {
							for (TplSpecZoneQty specZoneQty : resScoreDto.getSpecZoneQty()) {
								System.out.println("specqtyId==="+specZoneQty.getSpecZoneQtyId());
								if(null != specZoneQty.getQty() && specZoneQty.getQty().equals(new BigDecimal(0))){
									specZoneQty.setMaxScore(new BigDecimal(0.00));
									
								}else{
									specZoneQty.setMaxScore(specQuestion.getMaxScore());
								}
								TemplateSpecReqDTO reqDto = new TemplateSpecReqDTO();
								//TemplateSpecResDTO resDto = new TemplateSpecResDTO();
								reqDto.setSpecZoneQty(specZoneQty);
								reqDto.setTotalMaxScore(specZoneQty.getMaxScore());
								//resDto = templateSpecSo.saveMaxScoreBySpecZoneQtyId(reqDto);
								templateSpecSo.saveMaxScoreBySpecZoneQtyId(reqDto);
							
							}
						}
 						
					}
					
				}
				if(isnullmaxscore){
					SpecificationReqDTO specDto = new SpecificationReqDTO();
					SpecificationResDTO spesDto = new SpecificationResDTO();
					specDto.setWeightageId(checklist.getQtScoreWeightage().getQtScoreWeightageId());
					spesDto = qtSpecSo.retrieveQtOfferPriceByQtWeightageId(specDto);
					if(spesDto.getScQtScoreOfferPrice()!= null){
						ScQtScoreOfferPrice scQtScoreOfferPrice = spesDto.getScQtScoreOfferPrice();
						ScQtScoreOfferPrice newScQtScoreOfferPrice = new ScQtScoreOfferPrice();
						List<ScQtScoreOfferPrice> newScQtScoreOfferPriceList = checklist.getQtScoreWeightage().getScQtScoreOfferPrices();
						
						if(count<1){
							if(newScQtScoreOfferPriceList.get(0).getTplSpecZoneDtl()==null){
								newScQtScoreOfferPriceList.get(0).setTplSpecZoneDtl(zone);
								count++;
							}
							
						}else{
							newScQtScoreOfferPrice.setScQtScoreWeightage(scQtScoreOfferPrice.getScQtScoreWeightage());
							newScQtScoreOfferPrice.setScoreType(scQtScoreOfferPrice.getScoreType());
							newScQtScoreOfferPrice.setBelowScore(scQtScoreOfferPrice.getBelowScore());
							newScQtScoreOfferPrice.setAboveScore(scQtScoreOfferPrice.getAboveScore());
							newScQtScoreOfferPrice.setFromVariance(scQtScoreOfferPrice.getFromVariance());
							newScQtScoreOfferPrice.setToVariance(scQtScoreOfferPrice.getToVariance());
							newScQtScoreOfferPrice.setOfferPriceType(scQtScoreOfferPrice.getOfferPriceType());
							newScQtScoreOfferPrice.setTplSpecZoneDtl(zone);
							setUserDetails(newScQtScoreOfferPrice);
							if(newScQtScoreOfferPrice.getTplSpecZoneDtl().getSpecZoneId()!= newScQtScoreOfferPriceList.get(0).getTplSpecZoneDtl().getSpecZoneId()){
								newScQtScoreOfferPriceList.add(newScQtScoreOfferPrice);
							}
							
						}	
					}
					this.getManagedBean().getFinancialScoring().add(0,checklist);
				
				}
			}
					
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			templateSpecSo = null;
		}

	}
	//#25138 Reendorsed - end
	
	// #21695 #30557 #28949 START FARID - 18/03/2020	
		public void retriveItemScore(ScQtScoreSetting technicalItem) {
			try {
				if (null != technicalItem) {

					int value1 = technicalItem.getOverallTechnical() != null ? technicalItem
							.getOverallTechnical().intValue() : 0;
					int value2 = technicalItem.getOverallOtherTechnical() != null ? technicalItem
							.getOverallOtherTechnical().intValue() : 0;
					int local = value1 + value2;
					this.getManagedBean().setTechnicalItemScore(
							new BigDecimal(local));
	
	// #21695 #28949 farid END 18/03/2020
				}

			} finally {

			}
		}
	
	//#28784 - Start
	private boolean validateRequiredDemo(List<TplSpecQuestion> list){
		boolean result = false;
		if(list !=null && list.size()>0){
			for(TplSpecQuestion obj : list){
				if(BooleanUtils.isTrue(obj.isItemReqDemo())){
					result = true;
					break;
				}
			}
		}
		return result;	
	}
	
	private void resetDemoChecklist(ScQtCheckList scQtCheckList){
		List<ScQtCheckList> list = getManagedBean().getScQtCheckSt2techProposal();
		if(CollectionUtils.isNotEmpty(list)){
			for(ScQtCheckList obj: list){
				if(obj.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)){
					obj.setQtChecklistId(scQtCheckList.getQtChecklistId());
					obj.setScQt(scQtCheckList.getScQt());
					obj.setChecklistSeq(scQtCheckList.getChecklistSeq());
					obj.setDocCategory(scQtCheckList.getDocCategory());
					obj.setPmDocument(scQtCheckList.getPmDocument());
					obj.setTitle(scQtCheckList.getTitle());
					obj.setDefinationType(scQtCheckList.getDefinationType());
					obj.setAction(scQtCheckList.getAction());
					obj.setScoringReq(scQtCheckList.isScoringReq());
					obj.setScQtDoc(scQtCheckList.getScQtDoc());
					obj.setManditory(scQtCheckList.isManditory());
					obj.setQtScoreWeightage(scQtCheckList.getQtScoreWeightage());
					obj.setPmStatus(scQtCheckList.getPmStatus());
					obj.setRecordStatus(scQtCheckList.isRecordStatus());
					obj.setCreatedBy(scQtCheckList.getCreatedBy());
					obj.setCreatedDate(scQtCheckList.getCreatedDate());
					obj.setChangedBy(scQtCheckList.getChangedBy());
					obj.setChangedDate(scQtCheckList.getChangedDate());
				}
			}
		}
	}
	
	public boolean isTSChecklistCompleted() {
		boolean result = false;
		List<ScQtCheckList> list = getManagedBean().getScQtCheckSt2techProposal();
		if(CollectionUtils.isNotEmpty(list)){
			for(ScQtCheckList obj: list){
				if(null != obj.getDefinationType().getParameterCode() &&
						obj.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)){
					if(QTStatusConst.TS_PENDING_SUBMISSION
							.equals(obj.getPmStatus().getStatusId())){
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}
	
	//#28784 - check Demo checklist to display edit button base on all spec list completed
	public boolean isAllTSDemoChecklistCompleted() {
		boolean result = false;
		List<ScQtCheckList> list = getManagedBean().getScQtCheckSt2techProposal();
		if(CollectionUtils.isNotEmpty(list)){
			for(ScQtCheckList obj: list){
				if(null != obj.getDefinationType().getParameterCode() &&
						obj.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)){
					if(QTStatusConst.TS_COMPLETED
							.equals(obj.getPmStatus().getStatusId())){
						result = true;
						
					} else if(QTStatusConst.TS_PENDING_SUBMISSION
							.equals(obj.getPmStatus().getStatusId())){
						result = false;
						break;
					}
				}
			}
		}
		return result;
	}
	
	public String demoBackAction() {
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
		
		String navigatePage = demoTechnicalTemplatelist();
		if(StringUtils.isNotBlank(navigatePage)){
			return navigatePage;
		}
		return null;
	}
	
	public String completeDemoAction() {
		boolean result = false;
		boolean checkTS = false;
		//retriveTechnicalCheckList(); //#28784
		List<ScQtCheckList> st2technicalproposalList = this.getManagedBean().getScQtCheckSt2techProposal();
		List<ScQtCheckList> demoTemplateList = new ArrayList<>();

		for (ScQtCheckList t : st2technicalproposalList) {
			if (t.getDefinationType().getParameterCode()
					.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
				if (t.getScQtDoc().getTplSpecMaster().getTplSpecQuestions() != null) {
					List<TplSpecQuestion> list = t.getScQtDoc().getTplSpecMaster().getTplSpecQuestions();
					if (null != list && list.size() > 0) {
						for (TplSpecQuestion obj : list) {
							if (BooleanUtils.isTrue(obj.isItemReqDemo()) && obj.getDemoScore() == null) {
								throw new EpException(MessageConst.ERROR_760529);
							}
						}
						for (TplSpecQuestion obj : list) {
							if (BooleanUtils.isTrue(obj.isItemReqDemo())) {
								result = true;
								break;
							}
						}
					}
				}
				demoTemplateList.add(t);
			}
		}

		List<ScQtCheckList> list = getManagedBean().getScQtCheckSt2techProposal();
		if (CollectionUtils.isNotEmpty(list)) {
			for (ScQtCheckList obj : list) {
				if (null != obj.getDefinationType().getParameterCode()
						&& obj.getDefinationType().getParameterCode()
								.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION)) {
								this.setTemplateSpecMaster(obj);
					if (QTStatusConst.TS_PENDING_SUBMISSION.equals(obj.getPmDemoStatus().getStatusId())) {
						checkTS = true;
						break;
					}
				}
			}
		}

		if (result && BooleanUtils.isFalse(checkTS)) {
			IQTSpecSO specSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
			SpecificationResDTO resDTO1 = new SpecificationResDTO();
			SpecificationReqDTO reqDTO1 = new SpecificationReqDTO();
			List<ScQtCheckList> updatedCheckListsDemo = new ArrayList<ScQtCheckList>();

			for (ScQtCheckList t : st2technicalproposalList) {
				if (t.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
					t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
					updatedCheckListsDemo.add(t);
					break;
				}
			}
			reqDTO1.setCheckLists(updatedCheckListsDemo);
			resDTO1 = specSO.completeDemoChecklist(reqDTO1);

			if (resDTO1.isSuccessful()) {
				List<ScQtCheckList> savedCheckListsDemo = resDTO1.getSavedCheckLists();
				for (ScQtCheckList obj : savedCheckListsDemo) {
					resetDemoChecklist(obj);
				}
				this.getManagedBean().setIndex(-1);
				this.getManagedBean().setCheckListTitle("");
				this.getManagedBean().setFinancialScoring(null);
				this.getManagedBean().setOfferPricePackage(null);
				this.getManagedBean().setPanelSpecExistedOrNot(false);
				specCommitteeResetData();
				checkDemoVersion(savedCheckListsDemo.get(0).getScQt());//28784 -  WIP
				
				//update demo weightage	
				reqDTO1.setTobeRemoveList(demoTemplateList);
				SpecificationResDTO resDTO = specSO.updateDemoScoreWeightage(reqDTO1);
				ScQtCheckList demo = null;
				List<ScQtCheckList> demoUpdateList = new ArrayList<>();				
				specCommitteeResetData();
				
					for (ScQtCheckList t : st2technicalproposalList) {
						if (t.getDefinationType().getParameterCode()
								.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
							t.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
							t.setPmEvalStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
							demo = t;
							demoUpdateList.add(t);
							break;
						}
					}				
				
				if (demoUpdateList != null) {					
					this.getManagedBean().getScQtCheckSt2techProposal().set(this.getManagedBean().getScQtCheckSt2techProposal().indexOf(demo)
							, resDTO.getSavedCheckLists().get(0));
				}
				return "/xhtml/sc/specification/specificationCommittee/specificationCommitteeMain.xhtml?faces-redirect=true";
			}
		} else {
			if (BooleanUtils.isTrue(checkTS)) {
				throw new EpException(MessageConst.ERROR_760534);//#28784
			} else if (BooleanUtils.isFalse(result)) {
				throw new EpException(MessageConst.ERROR_760525);
			}
		}
		return null;
	}
	
	public String getDemoStatusDescById(long statusId) {
		debugLogger("getStatusDescById.statusId: " + statusId);
		String KEY_SEPARATOR = "_";
		Map<String, String> statusMap = new HashMap<String, String>();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = new StringBuilder().append(statusId).append(KEY_SEPARATOR).append(languageCode).toString();

		// Get the status description list from map.
		String statusDesc = statusMap.get(key);

		if (statusDesc == null) {

			try {
				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setStatusId(statusId);
				reqDTO.setLanguageCode(languageCode);

				if (reqDTO.getStatusId().equals(QTStatusConst.TS_COMPLETED)) {
					statusDesc = ResourceBundleUtils.getMessage("lbl.btn.complete",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				} else if (reqDTO.getStatusId().equals(QTStatusConst.TS_PENDING_SUBMISSION)) {
					statusDesc = ResourceBundleUtils.getMessage("lbl.demo.incomplete",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				}

				// Put status description into map.
				statusMap.put(key, statusDesc);

				debugLogger("put into list: " + statusDesc);
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError("QT", "SpecificationBackingBean", "docNo", "getDemoStatusDescById", getUserIdSessionFromLoginId(), e);
				throw new EpException(e.getMessage());
			} finally {
			}
		}

		return statusDesc;
	}
	
	public String getYesNoStatusDescByBoolean(boolean trueFalse) {
		String desc = null;

		if (trueFalse) {
			desc = ResourceBundleUtils.getMessage("lbl.btn.yes", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		} else {
			desc = ResourceBundleUtils.getMessage("lbl.btn.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
		}
		return desc;
	}
		
	
	/**
	* reset Demo scoring and list and reasign back existing weightage to new list, #28784
	* 	
	* @return void
	*/
	public void resetDemoTechSpecListAfterDelete(){
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		List<ScQtCheckList> existingTechChecklist = this.getManagedBean().getScQtCheckSt2techProposal();
		List<ScQtCheckList> deletedTechChecklist = this.getManagedBean().getMarkDeletedTechChecklist();
		reqDTO.setScQt(this.getManagedBean().getQt());
		reqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
		SpecificationResDTO resDTO = specSo.retrieveCheckLists(reqDTO);
		for (ScQtCheckList e : existingTechChecklist) {
			for (ScQtCheckList f : resDTO.getTechnicalCheckList()) {
				if ((e.getQtChecklistId() != null && e.getQtScoreWeightage() != null && f.getQtScoreWeightage() != null)
						&& (e.getQtScoreWeightage().getWeightage() != null && e.getQtChecklistId().equals(
								f.getQtChecklistId()))) {
					f.getQtScoreWeightage().setWeightage(e.getQtScoreWeightage().getWeightage());
				}
			}			
		}
		
		for (ScQtCheckList qt : existingTechChecklist) {
			if (null == qt.getQtChecklistId()
					&& !qt.getDefinationType().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO)) {
				resDTO.getTechnicalCheckList().add(qt);
			}
		}
		
		if(deletedTechChecklist != null){
			for (ScQtCheckList mt : deletedTechChecklist) {
				resDTO.getTechnicalCheckList().remove(mt);
			}
		}
		
		getManagedBean().setScQtCheckSt2techProposal(resDTO.getTechnicalCheckList());

		List<Integer> settinglist = new ArrayList<>();
		settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_DEMO);
		reqDTO.setSettingTypes(settinglist);
		SpecificationResDTO resDTOnew = specSo.retrieveScoreSettingDtls(reqDTO);
		if (resDTOnew.getScoreSettingResultBean() != null) {
			for (ScQtScoreSetting demoScoreSetting : resDTOnew.getScoreSettingResultBean().getResultList()) {
				getManagedBean().setDemoScoreSetting(demoScoreSetting);
			}
		}
		
		//#28784
		if (!this.getManagedBean().getQt().isDemoRequired()) {
			deleteDemoFromManagedBean();
		}

	}
	//#28784 -  End
	
	public void actionSearchPresentationDemo() {//#28784
		List<TplSpecQuestion> listNew = new ArrayList<>();
		List<TplSpecQuestion> list = this.getManagedBean().getDemoOriSpecQuestionTemplateList();
		if (StringUtils.isNotBlank(this.getManagedBean().getItemName())) {
			String itemName = this.getManagedBean().getItemName().toLowerCase();
			for (TplSpecQuestion obj : list) {
				if (StringUtils.isNotBlank(obj.getClItem())) {
					if (obj.getClItem().toLowerCase().contains(itemName)) {
						obj.setSelectable(true);
						listNew.add(obj);
					} else {
						obj.setSelectable(false);
					}
				}
			}
			this.getManagedBean().getTemplateSpecBean().getSpecMaster().setTplSpecQuestions(listNew);
		} else {
			for (TplSpecQuestion obj : list) {
				obj.setSelectable(true);
			}
			this.getManagedBean().getTemplateSpecBean().getSpecMaster().setTplSpecQuestions(list);
		}
	}

	public void resetSearchPresentationDemo() {//#28784
		List<TplSpecQuestion> list = this.getManagedBean().getDemoOriSpecQuestionTemplateList();
		for (TplSpecQuestion obj : list) {
			obj.setSelectable(true);
		}
		this.getManagedBean().getTemplateSpecBean().getSpecMaster().setTplSpecQuestions(list);
		this.getManagedBean().setItemName(null);

	}	
	//#28784 - End
	
	// #29474 - start
	/**
	 * Retrieve deduction payment tab dtl.
	 */
	public void retrieveDeductionPaymentTabDtl() {

		ScQt scQt = getManagedBean().getQt();
		debugLogger("retrieveDeductionPaymentTabDtl");
		if (this.getManagedBean().getScQtDeductionList() == null
				|| this.getManagedBean().getScQtDeductionList().isEmpty()) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();
				if (null != this.getManagedBean().getRemoveDeductions()
						&& CollectionUtils.isNotEmpty(this.getManagedBean()
								.getRemoveDeductions())) {

				} else {
					this.getManagedBean().setScQtDeductionList(deductionList);
					this.getManagedBean().getQt()
							.setScQtDeductions(deductionList);
					getManagedBean().setSelectedScQtDeduction(null);
				}
			} finally {
				detailSO = null;
			}
		}
	}

	public boolean isSpecStage() {
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		boolean isSpec = false;
		if (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING
				.equals(currentStatusId)
				|| QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT
						.equals(currentStatusId)
				|| QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF
						.equals(currentStatusId)
				|| QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION
						.equals(currentStatusId)) {
			return isSpec;
		}
		return !isSpec;
	}
	// #29474 - end
	
	//1873 - start - note 74
	public boolean isMemberChairpersonSpecPreparationTask(){
		String taskName = this.getManagedBean().getTaskName();
		if(StringUtils.isEmpty(taskName)) return false;
		return Arrays.asList(
				new String[] { SCProcessNameConst.UPDATE_SCORING,
						SCProcessNameConst.VIEW_SCORING }).contains(taskName);
	}
	//1873 - end - note 74
	
	//29464 - note 90 - start
	public boolean evaluateRepublishFromDisplay(Long status){
		status = status==null ? 0L: status;
		List<Long> statusList = Arrays.asList(new Long[]{
			QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,
			QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF_WITH_REPUBLISH,
			QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
			QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF,
			QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION
		});
		return (statusList.contains(status) && 
				StringUtils.isNotEmpty(this.getManagedBean().getTaskName())) || 
						QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	//29464 - note 90 - end
	
	// #29732 Start - reOpen Fix
	public String checkCatCodeChange() {
		if (CollectionUtils.isEmpty(this.getManagedBean().getQtCategoryList())
				&& !this.getManagedBean().isFromSubmitCatChange()) {
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			qtBackingBean.retrieveSuppQualificationTabDtl();
		} else if (CollectionUtils.isEmpty(this.getManagedBean().getQtCategoryList())
				&& this.getManagedBean().isFromSubmitCatChange()) {
			IQTSpecSO specSo = getSpecSO();
			SpecificationReqDTO specReqDTOCatChange = new SpecificationReqDTO();
			retriveSupplierQualificationCriteria(QTConstants.SUPPLIER_CRITERIA);
			specReqDTOCatChange.setScQt(this.getManagedBean().getQt());
			specReqDTOCatChange.getScQt().setScQtCategorys(this.getManagedBean().getQtCategoryList());
			specSo.validateSupplierQulificationInfo(specReqDTOCatChange);
		}

		List<ScQtCategory> qtCatCodeList = this.getManagedBean().getQtCategoryList();
		if (CollectionUtils.isEmpty(this.getManagedBean().getQtCategoryList())) {
			retriveSupplierQualificationCriteria("SC");
			qtCatCodeList = this.getManagedBean().getQtCategoryList();
		}

		String catCodeList = "";
		Integer recordstatusInactive = 0; // #29732(#221)
		if (CollectionUtils.isNotEmpty(qtCatCodeList)) {
			for (ScQtCategory catCode : qtCatCodeList) {
				ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
				PmCategoryL1 catCodeL1 = new PmCategoryL1();
				CategoryCodeReqDTO reqDTO1 = new CategoryCodeReqDTO();
				reqDTO1.setCategoryL1Id(catCode.getPmCategoryL1().getCategoryL1Id());
				CategoryCodeResDTO resDTO1 = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO1);
				catCodeL1 = resDTO1.getCategoryL1();
				PmCategoryL2 catCodeL2 = new PmCategoryL2();
				CategoryCodeReqDTO reqDTO2 = new CategoryCodeReqDTO();
				reqDTO2.setCategoryL2Id(catCode.getPmCategoryL2().getCategoryL2Id());
				CategoryCodeResDTO resDTO2 = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO2);
				catCodeL2 = resDTO2.getCategoryL2();
				PmCategoryL3 catCodeL3 = new PmCategoryL3();
				CategoryCodeReqDTO reqDTO3 = new CategoryCodeReqDTO();
				reqDTO3.setCategoryL3Id(catCode.getPmCategoryL3().getCategoryL3Id());
				CategoryCodeResDTO resDTO3 = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO3);
				catCodeL3 = resDTO3.getCategoryL3();
				if (!catCodeL1.getCategoryName().equals(catCode.getPmCategoryL1Name())
						|| !catCodeL2.getCategoryName().equals(catCode.getPmCategoryL2Name())
						|| !catCodeL3.getCategoryName().equals(catCode.getPmCategoryL3Name())
						|| catCodeL1.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
						|| catCodeL2.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
						|| catCodeL3.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
				) {
					if (catCodeList != "") {
						catCodeList = catCodeList.concat(", ");
					}
					catCodeList = catCodeList.concat(catCode.getPmCategoryL1().getCategoryL1Code().toString()
							+ catCode.getPmCategoryL2().getCategoryL2Code().toString()
							+ catCode.getPmCategoryL3().getCategoryL3Code().toString());
				}
			}
		}
		return catCodeList;
	}
	// #29732 End - reOpen Fix
	
	/*#29468 Start Vinod*/
	/**
	* Get Category Codes For Clone constructing tree node for selection of Category Codes.
	* 
	* @param .
	* @return 
	*/
	private void getCategoryCodesForClone() {

		try {

			TreeNode rootNode = new DefaultTreeNode(TreeNodeConstants.DEFAULT_TREE_NODE_ROOT, null);
			Map<String, Map<Long, Map<Long, Map<Long, String>>>> mapCatCode = new HashMap<>();
			for (PmCategoryL1 pmCategoryL1 : BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class)
					.retrieveCategoryL1List().getCategoryL1List()) {
				Map<Long, String> l1Map = new HashMap<>();
				l1Map.put(pmCategoryL1.getCategoryL1Id(), null);
				for (PmCategoryL2 pmCategoryL2 : pmCategoryL1.getPmCategoryL2s()) {
					Map<Long, Map<Long, String>> l2Map = new HashMap<>();
					l2Map.put(pmCategoryL2.getCategoryL2Id(), l1Map);
					addCategoryL3ForMap(pmCategoryL1, pmCategoryL2, l2Map, mapCatCode, rootNode);
				}
				this.getManagedBean().setRootCategoryTree(rootNode);
				this.getManagedBean().setMapCatCode(mapCatCode);
			}
		} catch (Exception e) {
			String qtNo = this.getManagedBean().getQt() != null ? this.getManagedBean().getQt().getQtNo() : "";
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_SPECIFICATION_BACKINGBEAN,
					qtNo, SLALogConstant.QT_GET_CATEGORYCODES_FOR_CLONE, getUserIdSessionFromLoginId(), e);
		}
	}
	
	private void addCategoryL3ForMap(PmCategoryL1 pmCategoryL1, PmCategoryL2 pmCategoryL2,
			Map<Long, Map<Long, String>> l2Map, Map<String, Map<Long, Map<Long, Map<Long, String>>>> mapCatCode,
			TreeNode rootNode) {
		for (PmCategoryL3 pmCategoryL3 : pmCategoryL2.getPmCategoryL3s()) {
			Map<Long, Map<Long, Map<Long, String>>> l3Map = new HashMap<>();
			TreeNode catCode = new DefaultTreeNode(TreeNodeConstants.DEFAULT_TREE_NODE_CATCODE,
					pmCategoryL1.getCategoryL1Code() + pmCategoryL2.getCategoryL2Code()
							+ pmCategoryL3.getCategoryL3Code(), rootNode);
			TreeNode catDes1 = new DefaultTreeNode(TreeNodeConstants.DEFAULT_TREE_NODE_CATDESC1,
					pmCategoryL1.getCategoryL1Code() + " - " + pmCategoryL1.getCategoryName(), catCode);
			catDes1.setSelectable(false);
			TreeNode catDes2 = new DefaultTreeNode(TreeNodeConstants.DEFAULT_TREE_NODE_CATDESC2,
					pmCategoryL2.getCategoryL2Code() + " - " + pmCategoryL2.getCategoryName(), catCode);
			catDes2.setSelectable(false);
			TreeNode catDes3 = new DefaultTreeNode(TreeNodeConstants.DEFAULT_TREE_NODE_CATDESC3,
					pmCategoryL3.getCategoryL3Code() + " - " + pmCategoryL3.getCategoryName(), catCode);
			catDes3.setSelectable(false);

			l3Map.put(pmCategoryL3.getCategoryL3Id(), l2Map);
			mapCatCode.put(
					pmCategoryL1.getCategoryL1Code() + pmCategoryL2.getCategoryL2Code()
							+ pmCategoryL3.getCategoryL3Code(), l3Map);
		}
	}
		
	/**
	* update the selection of Category Codes.
	* 
	* @param event.
	* @return 
	*/
	public void selectCategoryTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		selectedTreeNode.setSelected(true);
		List<String> selectedCatCodes = new ArrayList<>();
		String code=(String) selectedTreeNode.getData();
		if(getManagedBean().getSelectedCategories()!=null 
			&& !getManagedBean().getSelectedCategories().isEmpty())  
				selectedCatCodes.addAll(getManagedBean().getSelectedCategories());
			
		if (!selectedCatCodes.isEmpty()) {
			if (!selectedCatCodes.contains(code)) {
				selectedCatCodes.add(code);
			}
		} else {
			selectedCatCodes.add(code);
		}
		getManagedBean().setSelectedCategories(selectedCatCodes);
	}

	/**
	* update the unSelection of Category Codes.
	* 
	* @param event.
	* @return 
	*/
	public void unSelectCategoryTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		selectedTreeNode.setSelected(false);
		List<String> selectedCatCodes = new ArrayList<>();
			
		if(getManagedBean().getSelectedCategories()!=null 
			&& !getManagedBean().getSelectedCategories().isEmpty())  
				selectedCatCodes.addAll(getManagedBean().getSelectedCategories());
			
		if(!selectedCatCodes.isEmpty()){
			selectedCatCodes.remove((String) selectedTreeNode.getData());
		}
		getManagedBean().setSelectedCategories(selectedCatCodes);
	}
	/*#29468 End Vinod*/
	
	//#28784 - Get Demo Score Setting
	public List<ScQtScoreSetting> getDemoScoreSetting(SpecificationReqDTO reqDTO){	
		List<ScQtScoreSetting> demoScoreList = new ArrayList<ScQtScoreSetting>();
		List<Integer> settinglist = new ArrayList<Integer>();
		settinglist.add(SourcingConstants.SCORE_SETTING_TYPE_DEMO);		
		reqDTO.setSettingTypes(settinglist);
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		SpecificationResDTO resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
		if(resDTO.getScoreSettingResultBean() != null){
			for (ScQtScoreSetting demoScoreSetting : resDTO.getScoreSettingResultBean().getResultList()) {
				demoScoreList.add(demoScoreSetting);
			}
			
		}
		return reqDTO.setDemoScoreToBeRemoveList(demoScoreList);
	}
	
	/**
	 * #28784, To Check existing score setting if record has been created
	 * @param SpecificationReqDTO reqDTO
	 * @return ScQtScoreSetting
	 */
	private ScQtScoreSetting checkScoreSetting(SpecificationReqDTO reqDTO){
		IQTSpecSO specSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
		SpecificationResDTO resDTO = specSo.retrieveScoreSettingDtls(reqDTO);
		if(resDTO.getScoreSettingResultBean() != null){
			for (ScQtScoreSetting demoScoreSetting : resDTO.getScoreSettingResultBean().getResultList()) {
				return demoScoreSetting;
			}
			
		}
		return null;
	}
	
	//#28784 bug note-#424 - start
	public boolean isEvalTier1Or2() {
		boolean evalTier2 = false;
		
		if (null != getManagedBean().getQt().getEvaluationType()
				&& ParameterConst.EVALUATION_TYPES_TWO_TIER
				.equalsIgnoreCase(getManagedBean().getQt().getEvaluationType())) {
			evalTier2 = true;
		}
		
		return evalTier2;
	}
	//#28784 bug note-#424 - end

	//29521 - start
	
	public ScQt retrieveScqtdetails(){
		ScQt scQt = this.getManagedBean().getQt();
		
		IQTSpecSO specSO = getSpecSO();
		SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
		specificationReqDTO.setScQt(scQt);
		SpecificationResDTO specificationResDTO = specSO.retrieveCurrentScQT(specificationReqDTO);
		
		return specificationResDTO.getScQt();
	}
	
	public boolean validateZonalChanges(){
		boolean isNewZonalAdded = Boolean.FALSE;
		SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
		
		List<TplSpecZoneDtl> listZone = new ArrayList<>();
		List<String> listZoneName = new ArrayList<>();
		List<String> listCurrentZoneName = new ArrayList<>();
		List<TreeNode> listZonalTree = new ArrayList<>();
		Long specZoneId = 0L;
		
		
		//retrieve latest Zone from managedBean
		if (null != getManagedBean().getZoneGeneralTrees() && getManagedBean().getZoneGeneralTrees().size() > 0){
			listZonalTree = getManagedBean().getZoneGeneralTrees();
			TplSpecZoneDtl dtl = new TplSpecZoneDtl();
			for (TreeNode node : listZonalTree){
				for (TreeNode no: node.getChildren()){
					dtl = (TplSpecZoneDtl)no.getData();
					if (null != dtl.getTplSpecZone()){
						specZoneId = dtl.getTplSpecZone().getSpecZoneId();
					}
					
					listCurrentZoneName.add(dtl.getZoneName());
				}
				
			}
			specificationReqDTO.setSpecZoneId(specZoneId);
		}
		
		//retrieve from db
		listZone = getManagedBean().getBackupZoneList();
		//compare from db with managedBean for changes
		
		if (null != listZone){
			if (listZone.size() == listCurrentZoneName.size()){
				//check zone name
				for (TplSpecZoneDtl zone: listZone){
					
					listZoneName.add(zone.getZoneName());
				}
				
				
				if (listZoneName.containsAll(listCurrentZoneName)){
					isNewZonalAdded = false;
				}else {
					isNewZonalAdded = true;
				}
			}else {
				isNewZonalAdded = true;
			}
		}
		
		//start insert and update into tplSpecZoneDtlHistory
		// check existing in TplSpecZoneDtlHistory
		// listZone == null = non zonal
		if (null != listZone && !listZone.isEmpty()) { //Sonarqube DP Code Smell Minor Muhamad Za'im [26/4/2024]
			if (null != specificationReqDTO.getSpecZoneId()) {
				SpecificationResDTO resDTO = getQtSpecSO().getTplSpecZoneDtlHistoryBySpecZoneId(specificationReqDTO);
				if (!resDTO.isSuccessful()) {
					if (isNewZonalAdded) {
						// data do not present in tpl spec zone dtl history
						for (TplSpecZoneDtl specZoneDtls : listZone) {
							specificationReqDTO.setSpecZoneDtls(specZoneDtls);
							// if exist
							if (!resDTO.isSuccessful()) {
								// perform update is latest
								// insert new data
								doInsertTplSpecZoneDtl(specificationReqDTO);
							}
							/*
							 * else{
							 * doInsertTplSpecZoneDtl(specificationReqDTO); }
							 */
						}
					}
				}
			}
			
		}//else no changes nothing change
		return isNewZonalAdded;
	}
	
	public void doInsertTplSpecZoneDtl(SpecificationReqDTO specificationReqDTO){
		getQtSpecSO().saveTplSpecZoneDtlHistory(specificationReqDTO);
	}
	
	
	
	public List<ScQtCategory> retrieveCurrentScQtCategoryCode(){
		List<ScQtCategory> listCategoryDB = new ArrayList();
		IQTSpecSO specSO = getSpecSO();
		listCategoryDB = specSO.retrieveScQtCategorytByQtID(getManagedBean().getQt().getQtId());
		
		return listCategoryDB;
	}
	
	public void retrieveCurrentZonalDetail(){
		Long specZoneId = 0L;
		IQTSpecSO specSO = getSpecSO();
		List<String> listCurrentZoneName = new ArrayList<>();
		SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
		SpecificationResDTO specificationResDTO = new SpecificationResDTO();
		
		//retrieve latest Zone from managedBean
				if (null != getManagedBean().getZoneGeneralTrees() && getManagedBean().getZoneGeneralTrees().size() > 0){
					TplSpecZoneDtl dtl = new TplSpecZoneDtl();
					for (TreeNode node : getManagedBean().getZoneGeneralTrees()){
						for (TreeNode no: node.getChildren()){
							dtl = (TplSpecZoneDtl)no.getData();
							if (null != dtl.getTplSpecZone()){
								specZoneId = dtl.getTplSpecZone().getSpecZoneId();
							}
							
							listCurrentZoneName.add(dtl.getZoneName());
						}
						
					}
				}
				
				//retrieve from db
				if (!specZoneId.equals(0L) && !specZoneId.equals(null)){
					
						specificationReqDTO.setSpecZoneId(specZoneId);
						if (null != specificationReqDTO.getSpecZoneId()){
							specificationResDTO = specSO.retrieveCurrentZonalDtl(specificationReqDTO);
							if (null != specificationResDTO.getSpecZoneDtls()){
								getManagedBean().setBackupZoneList(specificationResDTO.getSpecZoneDtls());
							}
						}
				}
	}
	
	//#29521 - end
	
	// #28784
	private void checkSpecMaintenanceOrNo(boolean specMaint) {
		ScQt scQt = this.getManagedBean().getQt();
		if (specMaint) {
			if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt
					.getQtStatus().getStatusId())
					|| QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt
							.getQtStatus().getStatusId())) {
				this.getManagedBean().setGenInfo(true);
				if (QTStatusConst.SE_PENDING_ENDORSMENT.equals(this
						.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this
								.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_REVISION.equals(this
								.getManagedBean().getCurrentStatusId())) {
					this.getManagedBean().setGenInfo(false);
				}
			}
			if ((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt
					.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt
							.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(scQt
							.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_PUBLICATION_REVISION_APPROVAL
							.equals(scQt.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(scQt
							.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(scQt
							.getQtStatus().getStatusId()))) {
				this.getManagedBean().setChecklist(true);
				if (QTStatusConst.SE_PENDING_ENDORSMENT.equals(this
						.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this
								.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_REVISION.equals(this
								.getManagedBean().getCurrentStatusId())) {
					this.getManagedBean().setChecklist(false);
				}
			}
			if (!isSpecCommittee()) {
				this.getManagedBean().setChecklist(false);
			}
		} else {
			if(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId()) 
					|| QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId())){
				this.getManagedBean().setGenInfo(true);
				if(QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())) {
					this.getManagedBean().setGenInfo(false);
				}
			}
			if((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId())) 
					|| (QTStatusConst.QT_REVISION_PUBLICATION.equals(scQt.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_PUBLICATION_REVISION_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(scQt.getQtStatus().getStatusId()))
					|| (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(scQt.getQtStatus().getStatusId()))){
				this.getManagedBean().setChecklist(true);
				if(QTStatusConst.SE_PENDING_ENDORSMENT.equals(this.getManagedBean().getCurrentStatusId())
						|| QTStatusConst.SE_PENDING_SIGNOFF.equals(this.getManagedBean().getCurrentStatusId())) {
					this.getManagedBean().setChecklist(false);
				}
			}
		}
	}

	public boolean isSpecCommittee() {
		return QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #28784

	/* #29187 Start Vinod */
	/**
	 * This method is to get dropdown values for mandatory spec.
	 * 
	 * @param
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getMandatorySpecDropdownValues() {
		List<SelectItem> selectItems = new ArrayList<>();

		try {
			selectItems.add(new SelectItem(false, ResourceBundleUtils.getMessage(QTConstants.LBL_TXT_SELECT,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
			selectItems.add(new SelectItem(true, ResourceBundleUtils.getMessage(QTConstants.LBL_TXT_YES,
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
		} catch (Exception e) {
			String qtNo = this.getManagedBean().getQt().getQtNo();
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE, SLALogConstant.QT_SPECIFICATION_BACKINGBEAN,
					qtNo, SLALogConstant.QT_GET_MANDATORY_SPEC_DROPDOWN_VALUES, getUserIdSessionFromLoginId(), e);
		}

		return selectItems;
	}

	/* #29187 End Vinod */
	/* #29187 Start Vinod */
	/**
	 * This method is to render mandatory spec.
	 * 
	 * @param tplSpecQuestions
	 * @return boolean
	 */
	public boolean renderManSpec(List<TplSpecQuestion> tplSpecQuestions) {

		for (TplSpecQuestion question : tplSpecQuestions) {
			boolean isMandatorySpecForSample = question.isMandatorySpecForSample();
			if (isMandatorySpecForSample) {
				return true;
			}
		}

		return false;
	}

	/* #29187 End Vinod */
	/* #29187 Start Vinod */
	/**
	 * retrives the specification details to check mandatory specification.
	 * 
	 * @param templateChecklist
	 *            .
	 * @return
	 */
	public void retriveSampleSpecmasterForMandatorySpecChecking(ScQtCheckList templateChecklist) {
		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		List<TplSpecQuestion> tplSpecQuestions = templateChecklist.getScQtDoc().getTplSpecMaster().getTplSpecQuestions();

		for (TplSpecQuestion itemQuestion : tplSpecQuestions) {
			specReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			specReqDTO.setItemName(itemQuestion.getClItem());
			TemplateSpecResDTO resDTO = templateSpecSO.retrieveTechSpecByItemNames(specReqDTO);

			checkMandatorySpecForSample(itemQuestion, resDTO);
		}
	}

	/* #29187 End Vinod */
	/* #29187 Start Vinod */
	private void checkMandatorySpecForSample(TplSpecQuestion itemQuestion, TemplateSpecResDTO resDTO) {
		if (resDTO != null && CollectionUtils.isNotEmpty(resDTO.getSpecQuestionList())) {
			List<TplSpecQuestion> tplSpecQuestions = resDTO.getSpecQuestionList();
			for (TplSpecQuestion question : tplSpecQuestions) {
				if (question.isMandatorySpec()) {
					itemQuestion.setMandatorySpecForSample(true);
				}
			}
		}
	}

	/* #29187 End Vinod */

	// Bug #33661@Syazani 06/10/2022 -- Start
	private void retrieveStatusByTypeAndId(ScQt scQt) {
		retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT, scQt.getQtId());
		if (null != this.getManagedBean().getSpecMaintenanceWorkFlowStatus()) {
			log.debug("spec maintenance status id===" + this.getManagedBean().getSpecMaintenanceWorkFlowStatus().getPmStatus().getStatusId());
			this.getManagedBean().setCurrentStatusId(this.getManagedBean().getSpecMaintenanceWorkFlowStatus().getPmStatus().getStatusId());
		}
	}
	// Bug #33661@Syazani 06/10/2022 -- End
	
	// Bug #35219@Syazani 07/11/2023 -- Start
	private void selectOneItemActionAndDefinationBugRepair(List<ScQtCheckList> chcLst, List<ScQtCheckList> chcLstClone,
			Integer currentIndex) {
		int i = 0;
		for (ScQtCheckList copyOfscl : chcLstClone) {
			for (ScQtCheckList scl : chcLst) {
				boolean isNotCurrentIndex = currentIndex == null || (currentIndex != null && i != currentIndex);
				boolean isSameIDi = copyOfscl.getQtChecklistId() != null && scl.getQtChecklistId() != null
						&& copyOfscl.getQtChecklistId().equals(scl.getQtChecklistId());
				boolean isSameIDii = !isSameIDi && copyOfscl.getIndex() != -1 && scl.getIndex() != -1 ? copyOfscl
						.getIndex() == scl.getIndex() : false;
				if (isNotCurrentIndex && (isSameIDi || isSameIDii)) {
					scl.setAction(copyOfscl.getAction());
					scl.setDefinationType(copyOfscl.getDefinationType());
				}
				i++;
			}
			i = 0;
		}
	}

	// Bug #35219@Syazani 07/11/2023 -- End
		
	//#34048 - Start
	public boolean isSpecQuestionNotExist(ScQtCheckList qtCheckList) {
		
		TplSpecMaster spec = qtCheckList.getScQtDoc().getTplSpecMaster();
		List<TplSpecQuestion> questionList = spec.getTplSpecQuestions();
		boolean isFinancialSpec = ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(spec.getSpecTypeCode().getParameterCode());
		int specCount = 0;
		String item = null;
		
		if (!isFinancialSpec) {
			for (TplSpecQuestion ques : questionList) {
				boolean isQuestionTypeItem = TemplateSpecConst.QUESTION_TYPE_ITEM.equals(ques.getQuestionType());
				
				if (item != null && !item.equals(ques.getClItem()) && spec.isSpecificationReq() && isQuestionTypeItem
						&& specCount == 0) {
					return true;
				}
				
				if (isQuestionTypeItem) {
					specCount = 0;
					item = ques.getClItem();
				} else {
					specCount++;
				}
			}
			
			if (item != null && spec.isSpecificationReq() && specCount == 0 && questionList.size() > 0) {
				return true;
			}
		}
		
		return false;
	}
	//#34048 - End
	
	//#28784 - Start
	public boolean isSpecItemNotExist(ScQtCheckList qtCheckList) {
		if (qtCheckList.getQtChecklistId() == null) {
			return true;
		}
		TplSpecMaster spec = qtCheckList.getScQtDoc().getTplSpecMaster();
		List<TplSpecQuestion> questionList = spec.getTplSpecQuestions();
		if (CollectionUtils.isNotEmpty(questionList)) {
			boolean isFinancialSpec = ParameterConst.SPECIFICATION_TYPE_FINANCIAL.equals(spec.getSpecTypeCode()
					.getParameterCode());

			ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setSpecMaster(spec);
			reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
			TplSpecMaster currentSpecMaster = templateSpecSO.getTemplateMaster(reqDTO).getSpecMaster();
			List<TplSpecQuestion> currentQuestions = currentSpecMaster.getTplSpecQuestions();
			List<Long> questionListId = new ArrayList<>();
			for (TplSpecQuestion q : questionList) {
				questionListId.add(q.getSpecQuestionId());
			}

			if (!isFinancialSpec) {
				for (TplSpecQuestion ques : currentQuestions) {
					boolean isQuestionTypeItem = TemplateSpecConst.QUESTION_TYPE_ITEM.equals(ques.getQuestionType());
					if (isQuestionTypeItem && CollectionUtils.isNotEmpty(questionListId)
							&& !questionListId.contains(ques.getSpecQuestionId())) {
						return true;
					}
				}
			}
		}

		return false;
	}
	//#28784 - End
	
	/**
	 * #28784 - To delete unsaved demo from manage bean 
	 * 
	 */	
	private void deleteDemoFromManagedBean() {
		List<ScQtCheckList> newScQtChecklist = new ArrayList<>();
		List<ScQtCheckList> scQtChecklist = this.getManagedBean().getScQtCheckSt2techProposal();
		if (scQtChecklist != null) {
			for (ScQtCheckList qt : scQtChecklist) {
				if (!(null == qt.getQtChecklistId() && qt.getDefinationType().getParameterCode()
						.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_PRESENTATION_DEMO))) {
					newScQtChecklist.add(qt);
				}
			}

			this.getManagedBean().setScQtCheckSt2techProposal(newScQtChecklist);
		}
	}
	
	// Bug #33661@Syazani 12/04/2023 -- Start
	private void commonMessageAcknowledgementErrorPage(String messageId, String[] parameters) {

		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(messageId, parameters,
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
	}
	// Bug #33661@Syazani 12/04/2023 -- End
	
	/**
	 *11104-newEndorsestart
	 *During the sample edit icon, check whether Existing TemplateSpec has sample data or not.
	 * 
	 */	
	public boolean checkTemplateRequiredSample(ScQtCheckList templateChecklist){//11104_newEndorsestart
		if (null == templateChecklist.getSampleStatus()
				 || templateChecklist.isCloned()) {//11104-Reenorse728
			return Boolean.FALSE;
		} else {
			for (TplSpecQuestion specQuestion :  getTemplateSampleQuestions(templateChecklist)) {
				if (BooleanUtils.isTrue(specQuestion.isSampleReq()) && null!=specQuestion.getSampleScore() && null!=specQuestion.getSampleQuantity()) {//11104ReendorseStart728
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}//11104_newEndorseEnd
	
	/**
	 * Complete Sample template spec details.user clicks on complete button in samplespecTechList
	 * spec screens.
	 */
	public String completeSampleTemplateSpecDetails() {//11104_newEndorsestart
		List<ScQtCheckList> sampleTemplateSpecs = this.getManagedBean()
				.getSampleSpecCheckList();
		if (CollectionUtils.isNotEmpty(sampleTemplateSpecs)) {
			int sampleExist = 0;
			boolean isTemplateSamplePending = Boolean.FALSE;
			for (ScQtCheckList sampleSpecs : sampleTemplateSpecs) { 
				if(!sampleSpecs.isCloned()){//11104-ReendorsenewStart
				 sampleExist = validateSampleItems(getTemplateSampleQuestions(sampleSpecs), sampleExist);
				 }//11104-ReendorsenewEnd
				PmStatus statusSample=sampleSpecs.getSampleStatus();// 11104-Reendose794
				if (this.checkTemplateRequiredSample(sampleSpecs)
						&& null != statusSample
						&& QTStatusConst.TS_PENDING_SUBMISSION
								.equals(statusSample.getStatusId())) {
					throw new EpException(MessageConst.ERROR_760550);
				}// 11104-Reendose794
			}
			if (sampleExist == 0) {
				throw new EpException(MessageConst.ERROR_760516);
			}
			if (BooleanUtils.isFalse(isTemplateSamplePending)
					&& sampleExist >= 1) {
				completeSampleDetails();
				return SPECIFICATION_CHECKLIST_MAIN_PAGE;
			}

		}
	
	return null;
	}//11104_newEndorseEnd
	
	private void completeSampleDetails(){//11104_newEndorsestart
		List<ScQtCheckList> technicalChecklist = this.getManagedBean()
				.getScQtCheckSt2techProposal();

		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		for (ScQtCheckList checklist : technicalChecklist) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checklist.getDefinationType()
							.getParameterCode())) {
				checklist
						.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
				specReqDTO.setSampleCheckList(checklist);
				break;
			}
		}
		List<ScQtCheckList> checklistRemoveList = getToBeRemoveList();// 11104-ReendoseStart
		if (null != checklistRemoveList && !checklistRemoveList.isEmpty()) {
			for (int i = 0; i < checklistRemoveList.size(); i++) {
				if (null == checklistRemoveList.get(i).getQtChecklistId()) {
					checklistRemoveList.remove(i);
				} else {
					checklistRemoveList.get(i).setDeleted(true);
				}
			}
		}
		specReqDTO.setTobeRemoveList(checklistRemoveList);// 11104-ReendoseEnd
		SpecificationResDTO resDTO = getQtSpecSO().completeSampleChecklist(
				specReqDTO);
		if (resDTO.isSuccessful()) {
			specCommitteeResetData();
			for (ScQtCheckList checklist : technicalChecklist) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checklist.getDefinationType()
								.getParameterCode())) {
					int index = technicalChecklist.indexOf(checklist);

					resDTO.getSampleChecklist().setSampleWip(Boolean.TRUE); // 11104WIP

					technicalChecklist.set(index, resDTO.getSampleChecklist());
				}
			}
			specReqDTO.setTobeRemoveList(null);
			getManagedBean().setMarkDeletedTechChecklist(null);
			getManagedBean().setMarkDeletedFinChecklist(null);
			saveSpecMaintenanceWorkFlowStatus();
		}
		
	}//11104_newEndorseEnd
	private  SpecificationReqDTO checkSampleSpecQuestionChange(SpecificationReqDTO specReqDTO){//11104_newEndorsestart
		if (null == specReqDTO.getScQtCheckList().getSampleStatus()
				|| QTStatusConst.TS_PENDING_SUBMISSION.equals(specReqDTO
						.getScQtCheckList().getSampleStatus().getStatusId())) {
			specReqDTO.getSampleCheckList().setPmStatus(
					getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
		} else {
			if (null != specReqDTO.getSampleCheckList().getPmStatus()
					&& !QTStatusConst.TS_PENDING_SUBMISSION.equals(specReqDTO
							.getSampleCheckList().getPmStatus()
							.getStatusId())) {
				List<TplSpecQuestion> sampleQuestions = specReqDTO
						.getScQtCheckList().getScQtDoc().getTplSpecMaster()
						.getTplSpecQuestions();
				boolean isExistingSampleItemChange = existingSampleQuestionChange(sampleQuestions);
				if (isExistingSampleItemChange) {
					specReqDTO
							.getSampleCheckList()
							.setPmStatus(
									getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
				}

			}
		}
		return specReqDTO;
	}//11104_newEndorseEnd
	private boolean existingSampleQuestionChange(List<TplSpecQuestion> sampleQuestions){//11104_newEndorsestart
		boolean isExistingSampleItemChange = Boolean.FALSE;
		for (TplSpecQuestion currentQuestion : sampleQuestions) {
			SampleQuestionBean existingSpecQuestion = this.getManagedBean()
					.getExistingSampleSpecQuestions()
					.get(currentQuestion.getSpecQuestionId());
			int sampleEqual = Boolean.compare(existingSpecQuestion.isSample(),
					currentQuestion.isSampleReq());
			if (sampleEqual != 0) {
				return Boolean.TRUE;
			}
			BigDecimal existingQuantity = existingSpecQuestion
					.getSampleQuestionQuantity() != null ? existingSpecQuestion
					.getSampleQuestionQuantity() : BigDecimal.ZERO;
			BigDecimal currentQuantity = currentQuestion.getSampleQuantity() != null ? currentQuestion
					.getSampleQuantity() : BigDecimal.ZERO;
			if (existingQuantity.compareTo(currentQuantity) != 0) {
				return Boolean.TRUE;
			}
			BigDecimal existingScore = existingSpecQuestion
					.getSampleQuestionScore() != null ? existingSpecQuestion
					.getSampleQuestionScore() : BigDecimal.ZERO;
			BigDecimal currentScore = currentQuestion.getSampleScore() != null ? currentQuestion
					.getSampleScore() : BigDecimal.ZERO;
			if (existingScore.compareTo(currentScore) != 0) {
				return Boolean.TRUE;
			}
		}
		return isExistingSampleItemChange;
	}//11104_newEndorseEnd
	/**
	 * check Sample specQuestionExist for wiP QT.
	 * @return boolean
	 */
	public boolean  checkISSampleExistWIPChecklist(){//11104_newEndorseStart
		boolean qtSample = this.getManagedBean().getQt().isSampleReq();
		List<ScQtCheckList> technicalChecklist = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if ((isQTStatusChecklistScoring() || isQTStatusChecklistScoringRevision())) {//11104-Reendorse
			this.getManagedBean()
					.getQt()
					.setDemoNotificationCheck(
							SourcingConstants.SAMPLE_WIP_CHECK);
			return Boolean.TRUE;
		} else if (qtSample
				&& !isQTStatusPublished(this.getManagedBean().getQt()
						.getQtStatus().getStatusId())
				&& SourcingConstants.SAMPLE_WIP_CHECK.equals(this
						.getManagedBean().getQt().getDemoNotificationCheck())) {
			return Boolean.TRUE;
		} else if (qtSample
				&& !isQTStatusPublished(this.getManagedBean().getQt()
						.getQtStatus().getStatusId())
				&& null == this.getManagedBean().getQt()
						.getDemoNotificationCheck()) {
			
			return Boolean.FALSE;
		} else if (qtSample && CollectionUtils.isNotEmpty(technicalChecklist)) {
			List<ScQtCheckList> sampleTemplateSpecChecklist = new ArrayList<>();
			for (ScQtCheckList sampleCheckList : technicalChecklist) {
				if (QTConstants.TEMPLETE_SPEC_CODE
						.equalsIgnoreCase(sampleCheckList.getDefinationType()
								.getParameterCode())
						&& QTStatusConst.TS_COMPLETED.equals(sampleCheckList
								.getPmStatus().getStatusId())) {
					sampleTemplateSpecChecklist.add(sampleCheckList);
				}
			}

			return checkWiPSampleQuestionExist(sampleTemplateSpecChecklist);
		}
		return Boolean.FALSE;
	}//11104_newEndorseEnd
	private boolean checkWiPSampleQuestionExist(List<ScQtCheckList> sampleTemplateSpecChecklist){//11104_newEndorseStart
		for (ScQtCheckList sampleSpecs : sampleTemplateSpecChecklist) {	
			for (TplSpecQuestion specQuestion : getTemplateSampleQuestions(sampleSpecs)) {
				if (BooleanUtils.isTrue(specQuestion.isSampleReq())) {
					return Boolean.TRUE;
				}
			}

		}
		return Boolean.FALSE;
	}//11104_newEndorseEnd
	
	// Bug #35219@Syazani 07/11/2023 -- Start
	private void setDefTypeAndActionValues(ScQtCheckList qtCheckList) {
		String[] defTypeNAction = docDefinitionKeyParameter(qtCheckList);
		String defType = defTypeNAction[0];
		String action = defTypeNAction[1];

		qtCheckList.setDefinationType(retriveParammeterByTypeAndCode(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE,
				defType));
		qtCheckList.setAction(retriveParammeterByTypeAndCode(ParameterTypeConst.SC_SUPPLIER_ACTION, action));
	}

	private String[] docDefinitionKeyParameter(ScQtCheckList qtCheckList) {
		String docDefinitionKey = qtCheckList.getPmDocument().getDocDefinitionKey();
		String defType;
		String action;

		if (Arrays.asList(SourcingConstants.IS_DOCUMENT_DEFINITION_TYPE_WEB_FORM_AND_CHECKLIST_ACTION_INPUT).contains(
				docDefinitionKey)) {
			defType = ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM;
			action = ParameterConst.CHECKLIST_ACTION_INPUT;
		} else if ((Arrays
				.asList(SourcingConstants.IS_DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED_AND_CHECKLIST_ACTION_VIEW)
				.contains(docDefinitionKey))) {
			defType = ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED;
			action = ParameterConst.CHECKLIST_ACTION_VIEW;
		} else if (null != qtCheckList.getDefinationType()
				&& ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION.equalsIgnoreCase(qtCheckList.getDefinationType()
						.getParameterCode())) {
			defType = ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION;
			action = ParameterConst.CHECKLIST_ACTION_SUPPLIER_UPLOAD;
		} else {
			defType = null;
			action = null;
		}

		return new String[] { defType, action };
	}

	private PmParameter retriveParammeterByTypeAndCode(String parameterType, String parameterCode) {
		return ParameterHelper.getInstance().getParameterByTypeAndCode(parameterType, parameterCode);
	}

	// Bug #35219@Syazani 07/11/2023 -- End
	private void checkAtleastOneSampleComplete(List<ScQtCheckList> allTechnicalCheckList){//11104ReendorseStart728
		boolean isAtleastOnesampleComplete=Boolean.FALSE;
		for(ScQtCheckList checkList:allTechnicalCheckList){
			if (QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(checkList
					.getDefinationType().getParameterCode()) && null!=checkList.getSampleStatus() && QTStatusConst.TS_COMPLETED
							.equals(checkList.getSampleStatus()
									.getStatusId()) ) {
				isAtleastOnesampleComplete=Boolean.TRUE;
				
			}else if(ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checkList.getDefinationType()
							.getParameterCode())){
								if(isAtleastOnesampleComplete){
									checkList
									.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_COMPLETED));
								}
								break;
							}
		}
	}
	private boolean checkAtLeastOneSampleExist(List<TplSpecQuestion> allSampleQuestion){
		for (TplSpecQuestion specQuestion :  allSampleQuestion) {
		if (BooleanUtils.isTrue(specQuestion.isSampleReq()) && null!=specQuestion.getSampleScore() && null!=specQuestion.getSampleQuantity()) {
			return Boolean.TRUE;
		}
		}
		return Boolean.FALSE;
	}//11104ReendorseStart728
	
	/**
	 * #28784 - Check Demo BPM Version and set true or false to QTDetailManagedBean,
	 * @param
	 * @return void
	 */
	private void checkDemoVersion(ScQt scQt) {
		boolean isQtNewVersion = validateSupportedTask(scQt.getQtNo(), CompositeValidityConst.PM_COMPOSITE_VALIDITY);
		this.getManagedBean().setNewVersion(isQtNewVersion);
	}
	
	//11104 -start-
	/**
	 * sample checklist status update after template spec save.
	 * 
	 */
	
	private void sampleDetailsPendingTemplateSpec() {// 11104-Reendose794
		List<ScQtCheckList> technicalChecklist = this.getManagedBean()
				.getScQtCheckSt2techProposal();
		if (checkISSampleExistWIPChecklist()) {
			SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
			for (ScQtCheckList checklist : technicalChecklist) {
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
						.equalsIgnoreCase(checklist.getDefinationType()
								.getParameterCode())
						&& QTStatusConst.TS_COMPLETED.equals(checklist
								.getPmStatus().getStatusId())) {
					checklist
							.setPmStatus(getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION));
					specReqDTO.setSampleCheckList(checklist);
					break;
				}
			}
			if (null != specReqDTO.getSampleCheckList()) {
				SpecificationResDTO specResDTO = getQtSpecSO()
						.completeSampleChecklist(specReqDTO);
				setSampleDetailsIndex(specResDTO, technicalChecklist);
			}
		}	
	}// 11104-Reendose794
	
	  //29732 start
    private boolean isRoleValidationfromTaskList(){
    	FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value())
				|| facesScopes.hasRoleUser(RoleCode.PUBLICATION_APPROVER.value())
				|| facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value()); 
	}
    //29732 end
  //29732 start
    private boolean isRoleValidationfromTaskListAllowRevise(){
    	FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.SC_MEMBER.value())
				|| facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value())
				|| facesScopes.hasRoleUser(RoleCode.PUBLICATION_APPROVER.value())
				|| facesScopes.hasRoleUser(RoleCode.SC_CHAIRPERSON.value()); 
	}
    //29732 end
    
	// 29732 start
	private boolean isRoleValidationfromTaskListSpecEndorsement() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value())
				|| facesScopes.hasRoleUser(RoleCode.SC_SECRETARY.value());
	}
	// 29732 end
	
	 //29732 start
    private boolean isRoleValidationPopUpMessagefor(){
    	FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.SC_MEMBER.value())
				|| facesScopes.hasRoleUser(RoleCode.SC_CHAIRPERSON.value()); 
	}
    //29732 end

	/**
	 * 11104-start 
	 * sample checklist status set to pending for working in
	 * progress checklist during qtchecklist scoring and revision.
	 * 
	 * @param technicalCheckList
	 */
	private void setSampleWipCheckSpecPrepare(List<ScQtCheckList> technicalCheckList) {
		boolean checkWip = Boolean.FALSE;

		for (ScQtCheckList checkList : technicalCheckList) {
			String paramCode = checkList.getDefinationType().getParameterCode();
			PmStatus samplestatus = checkList.getSampleStatus();
			PmStatus pmStatus = checkList.getPmStatus();
			boolean templateParamAndStatus = QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(paramCode)
					&& null != samplestatus;
			boolean sampleParamAndStatus = ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(paramCode)
					&& null != pmStatus && QTStatusConst.TS_COMPLETED.equals(pmStatus.getStatusId());

			if (templateParamAndStatus) {
				checkWip = Boolean.TRUE;
			} else if (sampleParamAndStatus && !checkWip) {
				PmStatus pendingStatus = getPmStatusByStatusId(QTStatusConst.TS_PENDING_SUBMISSION);
				checkList.setPmStatus(pendingStatus);
				break;
			}
		}
	}
	// 11104 -end-

	// 11104 -start-
	/**
	 * show and hide sample radio Button during SpecPreparation and Maintenance
	 * screens.
	 * 
	 * @return sample true or false.
	 * 
	 */
	public boolean checkSampleRadioShowHideInSpecPrepareAndMaintenance() {

		Long statusId = this.getManagedBean().getQt().getQtStatus().getStatusId();
		Integer checkNewSampleExist = this.getManagedBean().getQt().getDemoNotificationCheck();
		
		if (isQTStatusChecklistScoring() || isQTStatusChecklistScoringRevision()) {
			this.getManagedBean().getQt().setDemoNotificationCheck(SourcingConstants.SAMPLE_WIP_CHECK);
			return Boolean.TRUE;
		} else if (!isQTStatusPublished(statusId)) {
			return checkSampleWipBeforePublish(checkNewSampleExist);

		} else if (isQTStatusPublished(statusId)) {
			return checkSampleWipCompleted(this.getManagedBean().getQt());
		}

		return Boolean.FALSE;
	}
	// 11104_End

	// 11104 -start-
	/**
	 * check before Publish Sample
	 * 
	 * @param checkNewSampleExist
	 * @return sample true or false.
	 * 
	 */
	private boolean checkSampleWipBeforePublish(Integer checkNewSampleExist) {
		if (SourcingConstants.SAMPLE_WIP_CHECK.equals(checkNewSampleExist)) {
			return Boolean.TRUE;
		} else if (null == checkNewSampleExist) {
			return Boolean.FALSE;
		}
		
		return Boolean.FALSE;
	}
	// 11104 -end-

	// 11104 -start-
	/**
	 * check after Publish Sample
	 * 
	 * @param qt
	 * @return sample true or false.
	 * 
	 */
	private boolean checkSampleWipCompleted(ScQt qt) {
		IQTSpecSO qtSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		SpecificationReqDTO specificationReqDTO = new SpecificationReqDTO();
		specificationReqDTO.setScQt(qt);
		specificationReqDTO.setSpecType(SourcingConstants.TECHNICAL_DOCTYPE);
		SpecificationResDTO specificationResDTO = qtSpecSO.retrieveTechnicalService(specificationReqDTO);
	
		if (CollectionUtils.isNotEmpty(specificationResDTO.getTechnicalCheckList())) {
			for (ScQtCheckList sampleCheckList : specificationResDTO.getTechnicalCheckList()) {
				String paramCode = sampleCheckList.getDefinationType().getParameterCode();
				PmStatus samplestatus = sampleCheckList.getSampleStatus();
				boolean templateParamAndStatus = QTConstants.TEMPLETE_SPEC_CODE.equalsIgnoreCase(paramCode)
						&& null != samplestatus;
				
				if (templateParamAndStatus) {
					return Boolean.TRUE;
				}
			}

			return Boolean.FALSE;
		}

		return Boolean.FALSE;
	}
	// 11104 -end-

	// 11104 -start-
	/**
	 * check Sample wip
	 * 
	 * @param technicalSampleCheckList
	 * @param qt
	 * 
	 */
	private void checkSampleWipData(List<ScQtCheckList> technicalSampleCheckList, ScQt qt) {
		if (CollectionUtils.isNotEmpty(technicalSampleCheckList)) {
			boolean sample = Boolean.FALSE;
			
			for (ScQtCheckList checkList : technicalSampleCheckList) {
				String paramCode = checkList.getDefinationType().getParameterCode();
				
				if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(paramCode)) {
					sample = Boolean.TRUE;
					checkList.setSampleWip(this.getManagedBean().isSampleShowHide());
					break;

				}
			}
			
			qt.setSampleReq(sample);
		}
	}
	// 11104 -end-
	
	public String redirectSpecComMainYes() {
		this.getManagedBean().setFieldProcurementJustification(true);
		return QTConstants.SPEC_COMMITTEE_MAIN_PAGE;
	}

	public String redirectSpecComMainNo() {
		this.getManagedBean().setFieldProcurementJustification(false);
		return QTConstants.SPEC_COMMITTEE_MAIN_PAGE;
	}
	//#36678 related to #35061 end
	
	// 11104 -start-
	/**
	 * set Sample details index to list after save.
	 * @param specResDTO
	 * @param technicalChecklist
	 * 
	 */
	private void setSampleDetailsIndex(SpecificationResDTO specResDTO,
			List<ScQtCheckList> technicalChecklist) {
		for (ScQtCheckList checklist : technicalChecklist) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
					.equalsIgnoreCase(checklist.getDefinationType()
							.getParameterCode())) {
				int index = technicalChecklist.indexOf(checklist);
				specResDTO.getSampleChecklist().setSampleWip(
						this.getManagedBean().isSampleShowHide());// 11104WIP
				technicalChecklist.set(index, specResDTO.getSampleChecklist());
				break;
			}
		}
	}
	// 11104 -end-
	
}
