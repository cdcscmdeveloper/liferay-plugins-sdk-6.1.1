/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.supplierfinalization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.sc.BidItemBean;
import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.schedulebidding.IScheduleBiddingSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingReqDTO;
import my.ep.dto.sc.schedulebidding.ScheduleBiddingResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationEndorsementRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.supplierfinalization.SupplierFinalizationBiddingManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.docx4j.docProps.variantTypes.Array;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 * Implementation for supplier finalization by bidding
 * 
 * @author Faradiana Supplier Finalization online Bidding Backing Bean
 */
@ManagedBean(name = "suppFinalBiddingBackingBean")
@RequestScoped
public class SupplierFinalizationBiddingBackingBean extends
		SourcingBaseBackingBean<SupplierFinalizationBiddingManagedBean> {
	private static final Logger log = EpLogger
			.getLogger(SupplierFinalizationBiddingBackingBean.class);

	private int currentTab;
	private String textBeforeSign;

	public static final int ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY = 0;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT = 1;
	public static final int ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT = 1;

	/**
	 * Default constructor
	 */
	public SupplierFinalizationBiddingBackingBean() {

	}

	/**
	 * Initialize the data required when the page is initially rendered (based
	 * on the task-id of the selected quotation/tender) and sets the data in
	 * managed bean associated with the current user session
	 */
	public void preRenderView() {
		debugLogger("preRenderView");

		try {
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class);
			this.getManagedBean().setPayLoad(dataType);
			Long qtId = Long.valueOf(dataType.getQtId());
			String committeeType = String.valueOf(dataType.getCommitteeType());
			getManagedBean().setCommitteeType(committeeType);
			getManagedBean().setPayLoad(dataType);
			
			retrieveOnlineBiddingEndorsement(qtId);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.FC_ONLINE_BIDDING_ENDORSEMENT_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void retrieveOnlineBiddingEndorsement(Long qtId) {
		retriveQTDetails(qtId);
		retrieveSupplierFinalization(qtId);
		generateZoneMenuModel();
		retrieveBiddingSummary();
	}

	/**
	 * Retrieve quotation/tender details corresponding to the qtId
	 * @param qtId
	 * 			The id of the quotation/tender to be loaded
	 */
	public void retriveQTDetails(Long qtId) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);

			QuationTenderResDTO resDTO = qtDetailSO.retriveQtById(reqDTO);
			ScQt scQt = resDTO.getQtDetail();
			populateParentOrgProfile(scQt);
			getManagedBean().setScQt(scQt);

		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Retrieve supplier finalization details details corresponding to the qtId
	 * @param qtId
	 * 			The id of the quotation/tender 
	 */	
	public void retrieveSupplierFinalization(Long qtId) {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(qtId);
			
			boolean isZonal = this.getManagedBean().getScQt().isZonal();
			reqDTO.setZonal(isZonal);
			
			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.retrieveSupplierFinalization(reqDTO);
			ScQtFinalization scQtFinalization = resDTO.getScQtFinalization();
			
			if (isZonal) {
				this.getManagedBean().setBiddingZoneDetails(
						new ArrayList<TplSpecZoneDtl>());
				this.getManagedBean().setScQtRecommendsByZone(
						new LinkedHashMap<Long, List<ScQtRecommend>>());
				this.getManagedBean().setBidSchedulesByZone(
						new LinkedHashMap<Long, List<ScBidSchedule>>());
				this.getManagedBean().setBidPackageBeansByZone(
						new LinkedHashMap<Long, List<BidPackageBean>>());				
				this.getManagedBean().setBidItemsByZone(
						new LinkedHashMap<Long, List<ScBidItem>>());
				
				Map<Long, List<ScQtRecommend>> scQtRecommendsByZone = 
							new LinkedHashMap<Long, List<ScQtRecommend>>();
				
				List<TplSpecZoneDtl> biddingZoneDetails = new ArrayList<TplSpecZoneDtl>();
				TplSpecZoneDtl specZoneDtl = null;
				for (ScQtRecommend scQtRecommend 
										: scQtFinalization.getScQtRecommends()) {
					if (!ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
							.equals(scQtRecommend.getRecommendBy().getParameterCode())) {
						continue;
					}
					specZoneDtl = scQtRecommend.getTplSpecZoneDtl();
					
					if (!biddingZoneDetails.contains(specZoneDtl)) {
						biddingZoneDetails.add(specZoneDtl);
					}
					if (scQtRecommendsByZone
							.containsKey(specZoneDtl.getSpecZoneId())) {
						List<ScQtRecommend> scQtRecommends 
								= scQtRecommendsByZone.get(specZoneDtl.getSpecZoneId());
						scQtRecommends.add(scQtRecommend);
					} else {
						List<ScQtRecommend> scQtRecommends 
								= new ArrayList<ScQtRecommend>();
						scQtRecommends.add(scQtRecommend);
						scQtRecommendsByZone.put(specZoneDtl.getSpecZoneId(), scQtRecommends);
					}
				}
				if (CollectionUtils.isNotEmpty(biddingZoneDetails)) {
					this.getManagedBean().setBiddingZoneDetails(biddingZoneDetails);
					this.getManagedBean().setSelectedZone(biddingZoneDetails.get(0));
					generateZoneMenuModel();
				}
			}
			
			getManagedBean().setScQtFinalization(scQtFinalization);

		} finally {
			qtSuppFinalizationSO = null;
		}
	}
	
	private void generateZoneMenuModel() {
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getBiddingZoneDetails())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean().getBiddingZoneDetails();

			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					zoneItem.setAjax(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOncomplete("onZoneTabChangeAction([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
		}
	}	
	
	/**
	 * To retrieve the bidding details on selection of a zone tab
	 */
	public void onZoneTabChangeAction() {
		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		if (selectedZoneTabIndex == 
				this.getManagedBean().getZoneActiveIndex()) {
			return;
		} 
		
		List<TplSpecZoneDtl> zoneDetails = this.getManagedBean().getBiddingZoneDetails();
		this.getManagedBean().setSelectedZone(zoneDetails.get(selectedZoneTabIndex));
		this.getManagedBean().setZoneActiveIndex(selectedZoneTabIndex);
		retrieveBiddingSummary();
	}

	/**
	 * To retrieve the details of Online Bidding Summary
	 */
	public void retrieveBiddingSummary() {
		navigateToBiddingSummaryTab();
		
		ScQt scQt = getManagedBean().getScQt();
		boolean isZonal = scQt.isZonal();
		String recommendType = getManagedBean().getScQtFinalization()
				.getRecommendedTypeId().getParameterCode();
		ScheduleBiddingReqDTO reqDTO = new ScheduleBiddingReqDTO();
		Long qtId = scQt.getQtId();
		reqDTO.setQtID(qtId);
		
		if (isZonal) {
			reqDTO.setSelectedZoneId(
					this.getManagedBean().getSelectedZone().getSpecZoneId());
		}
		if (recommendType.equals(
				ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
			reqDTO.setDocType(QTConstants.DOCTYPE_QT);
			retrieveRecommendedSuppliersByPkg(reqDTO);
		} else if (recommendType.equals(
				ParameterConst.RECOMMEND_TYPE_ITEM)) {
			retrieveBiddingItems(reqDTO);
		}
	}
	
	private void retrieveRecommendedSuppliersByPkg(ScheduleBiddingReqDTO reqDTO) {
		IBiddingSO biddingSO = null;
		IScheduleBiddingSO scheduleBiddingSO = null;
		
		TplSpecZoneDtl selectedZone = null;
		List<ScBidSchedule> currentZoneBidSchedules = null;
		List<BidPackageBean> currentZoneBidPackageBeans = null;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
				
		try {
			if (isZonal) {
				selectedZone = this.getManagedBean().getSelectedZone();
				if (MapUtils.isNotEmpty(
						this.getManagedBean().getBidSchedulesByZone())) {
					currentZoneBidSchedules 
						= this.getManagedBean().getBidSchedulesByZone().get(
								selectedZone.getSpecZoneId());
					currentZoneBidPackageBeans
						= this.getManagedBean().getBidPackageBeansByZone().get(
								selectedZone.getSpecZoneId());
				}
			}
			
			if (CollectionUtils.isNotEmpty(
					currentZoneBidSchedules) && 
						CollectionUtils.isNotEmpty(
							currentZoneBidPackageBeans)) {
				processPackageBidSchedules(
						currentZoneBidSchedules, currentZoneBidPackageBeans);
			} else {
				scheduleBiddingSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScheduleBiddingSO.class);
				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
						.retrieveBiddingScheduleByDocId(reqDTO);
				List<ScBidSchedule> bidScheduleList = resDTO
						.getBidSchedules();	
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);	
				
				BiddingReqDTO bidReqDTO = new BiddingReqDTO();
				Long bidScheduleId = bidScheduleList.get(0)
						.getBidScheduleId();				
				bidReqDTO.setBidScheduleId(bidScheduleId);				
				BiddingResDTO bidResDTO = biddingSO
						.retrieveBidScheduleDtl(bidReqDTO);				

				if (bidResDTO.isSuccessful()) {
					processPackageBidSchedules(bidScheduleList, bidResDTO.getBidPackages());
					if (isZonal) {
						this.getManagedBean().getBidSchedulesByZone()
							.put(selectedZone.getSpecZoneId(), bidScheduleList);
						this.getManagedBean().getBidPackageBeansByZone()
							.put(selectedZone.getSpecZoneId(), bidResDTO.getBidPackages());						
					}
				}
			}
		} finally {
			biddingSO = null;
			scheduleBiddingSO = null;
		}
	}
	
	private void processPackageBidSchedules(List<ScBidSchedule> bidScheduleList, 
						List<BidPackageBean> biddingPackagePriceList) {
		BigDecimal estimatedCost = new BigDecimal(0);
		if (CollectionUtils.isNotEmpty(
				bidScheduleList.get(0).getScBidItems())) {
			for (ScBidItem bidItem : bidScheduleList.get(0).getScBidItems()) {
				if (bidItem.getEstimatedCost() != null) {
					estimatedCost = estimatedCost.add(bidItem.getEstimatedCost());
				}
			}
		}

		getManagedBean().setScheduledBid(bidScheduleList.get(0));
		getManagedBean().setEstimatedCost(estimatedCost);
		getManagedBean().setBiddingPackagePriceList(
				biddingPackagePriceList);
		
		BigDecimal bumiSuppLowestOfferedPrice = null;
		BigDecimal lowestNonBumiOfferedPrice = null;
		
		// Set lowest non-bumi offered price
		if (CollectionUtils.isNotEmpty(biddingPackagePriceList)) {
			List<BigDecimal> bumiPriceList = new ArrayList<BigDecimal>();
			List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();
			for (BidPackageBean packageBean : biddingPackagePriceList) {
				if (packageBean.getScBidSupplier().getSmSupplier().isBumi()) {
					bumiPriceList.add(packageBean.getBidPriceTotal());
				} else {
					nonBumiPriceList.add(packageBean.getBidPriceTotal());
				}
				if (StringUtils.isBlank(packageBean.getSupplierName())) {
					String supplierName = getSupplierNameById(
							packageBean.getScBidSupplier().getSmSupplier().getSupplierId());
					packageBean.setSupplierName(supplierName);
				}
//					packageBean.getScBidSupplier().getScBidSupplierItems();
			}
			if (CollectionUtils.isNotEmpty(bumiPriceList)) {
				Collections.sort(bumiPriceList);
				bumiSuppLowestOfferedPrice = bumiPriceList.get(0);
			} 
			if (CollectionUtils.isNotEmpty(nonBumiPriceList)) {
				Collections.sort(nonBumiPriceList);
				lowestNonBumiOfferedPrice = nonBumiPriceList.get(0);
			}
		}
		
		getManagedBean().setBumiSuppLowestOfferedPrice(bumiSuppLowestOfferedPrice);
		getManagedBean().setLowestNonBumiOfferedPrice(lowestNonBumiOfferedPrice);
		
		if (bumiSuppLowestOfferedPrice != null && 
					lowestNonBumiOfferedPrice != null) {
			getManagedBean().setBumiAndNonBumiCombination(true);
		}		
	}
	
	private void retrieveBiddingItems(ScheduleBiddingReqDTO reqDTO) {
		IScheduleBiddingSO scheduleBiddingSO = null;
		try {
			TplSpecZoneDtl selectedZone = this.getManagedBean().getSelectedZone();
			boolean isZonal = this.getManagedBean().getScQt().isZonal();
			List<ScBidItem> biddingItemList = null;
			if (isZonal) {
				if (MapUtils.isNotEmpty(
						this.getManagedBean().getBidItemsByZone())) {
					biddingItemList = this.getManagedBean().getBidItemsByZone()
						.get(selectedZone.getSpecZoneId());
				}
			}
			if (CollectionUtils.isEmpty(biddingItemList)) {
				scheduleBiddingSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScheduleBiddingSO.class);
				
				ScheduleBiddingResDTO resDTO = scheduleBiddingSO
						.retrieveBiddingItem(reqDTO);
				if (resDTO.isSuccessful()) {
					biddingItemList = resDTO.getBidItemsList();
				}				
			}
			if (isZonal) {
				this.getManagedBean().getBidItemsByZone()
					.put(selectedZone.getSpecZoneId(), biddingItemList);
			}
			
			getManagedBean().setBiddingItemList(biddingItemList);
		} finally {
			scheduleBiddingSO = null;
		}
	}
	
	/**
	 * To navigate to bidding summary tab
	 */
	public void navigateToBiddingSummaryTab() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_SUMMARY);
	}

	/**
	 * Event to handle selection of bidding item
	 * @param event
	 * 			selection event object
	 */
	public void selectBiddingItem(SelectEvent event) {
		ScBidItem bidItem = (ScBidItem) event.getObject();
		this.getManagedBean().setSelectedBiddingItem(bidItem);

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidItemId(bidItem.getBidItemId());
			reqDTO.setScBidSchedule(bidItem.getScBidSchedule());

			BiddingResDTO resDTO = biddingSO
					.retrieveLastBidPricesByItem(reqDTO);
			if (resDTO.isSuccessful()) {
				List<BidItemBean> biddingItemPriceList = resDTO.getBidPriceItems();
				if (CollectionUtils.isNotEmpty(biddingItemPriceList)) {
					for (BidItemBean itemBean : biddingItemPriceList) {
						String supplierName = getSupplierNameById(itemBean.getSupplierId());
						itemBean.setSupplierName(supplierName);
					}
				}
				getManagedBean().setBidPriceItems(biddingItemPriceList);
			}
		} finally {
			biddingSO = null;
		}
	}
	
	/**
	 * To calculated the total estimated cost by item
	 * 
	 * @return totalAmount
	 */
	public BigDecimal getCalculatedTotalAmount(BigDecimal bidItemPrice) {
		ScBidItem bidItem = this.getManagedBean().getSelectedBiddingItem();
		BigDecimal totalAmount = TemplateSpecHelper.getInstance().calcTotalAmount(
												bidItem.getScRequestItem().getTplSpecQuestion(),
												bidItemPrice, bidItem.getQty());
		return totalAmount;
	}

	/*
	 * public void retrieveEndorsement() { ScQtFinalization scQtFinalization =
	 * getManagedBean().getScQtFinalization();
	 * if(scQtFinalization.getOnlineBiddingEndorse
	 * ().equals(ParameterConst.ONLINE_BIDDING_MEETING)){
	 * retrieveOnlineEndorsement(); }else{ retrieveOfflineEndorsement(); } }
	 */

	/**
	 * To retrieve the list of Committee for Endorsement (online endorsement)
	 */
	public void retrieveOnlineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_ONLINE_ENDORSEMENT);

		IQTMeetingSO meetingSO;
		try {
			meetingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTMeetingSO.class);
			String committeeType = getManagedBean().getCommitteeType();

			MeetingReqDTO reqDTO = new MeetingReqDTO();
			reqDTO.setQtId(getManagedBean().getScQt().getQtId());
			reqDTO.setCommitteType(committeeType);
			reqDTO.setIsAttended(null);

			MeetingResDTO resDTO = meetingSO
					.retrieveSignOffCommitteeListByCommType(reqDTO);
			if (resDTO.getCommitteMembers() != null) {
				this.getManagedBean().setCommitteeEndorsementList(
						resDTO.getCommitteMembers());
			}
		} finally {
			meetingSO = null;
		}
	}
	
	/**
	 * To show item price in a popup
	 * 
	 * @param packageItem
	 * 				the packageItem corresponding to which price is shown in popup
	 */
	public void supplierItemPricePopupAction(BidPackageBean packageItem) {

		getManagedBean().setSupplierCode(
				packageItem.getScBidSupplier().getSmSupplier().getSupplierId());

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setSupplierId(packageItem.getScBidSupplier().getSmSupplier()
					.getSupplierId());
			reqDTO.setBidScheduleId(getManagedBean().getScheduledBid()
					.getBidScheduleId());
			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidPriceList(reqDTO);
			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidPriceList(resDTO.getBidPrices());
			}
		} finally {
			biddingSO = null;
		}
	}
	
	/**
	 * Set the selected supplier details in managed bean associated with the current user session
	 * 
	 * @param scQtRecommend  
	 * 				the scQtRecommend which holds the item
	 * @param selectedRecommendDtl  
	 * 				the selectedRecommendDtl while holds the recommended supplier
	 */
	public void showSupplierDetails(Long supplierId) {
		debugLogger("showSuppDetails");
		
		resetCompanyInfo();
		
		if (supplierId != null) {
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(supplierId);
			QTSupplierResDTO resDTO = this.retrieveSupplierDetails(smSupplier);
			smSupplier = resDTO.getSmSupplier();
			getManagedBean().setSmSupplier(smSupplier);
			getManagedBean().setSmMofAccount(smSupplier.getSmMofAccounts().get(0));
			
			SmCompanyBasic smCompanyBasic = smSupplier.getSmAppl().getSmCompanyBasic();
			getManagedBean().setSmCompanyBasic(smCompanyBasic);
			
			List<SmPersonnel> smPersonnelList = smSupplier.getSmAppl().getSmPersonnels();
			for (SmPersonnel smPersonnel : smPersonnelList) {
				if (smPersonnel.isContactPerson()) {
					getManagedBean().setSmPersonnel(smPersonnel);
					break;
				}
			}
			for (SmCompanyAddress smCompanyAddress : smCompanyBasic.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(smCompanyAddress.getAddressType())) {
					getManagedBean().setSupplierBranch(QTConstants.SUPPLIER_INFO_HQ);
					getManagedBean().setSupplierAddress(smCompanyAddress.getSmAddress());
					break;
				}
			}
		}
	}	
	
	private void resetCompanyInfo() {
		this.getManagedBean().setSmSupplier(null);
		this.getManagedBean().setSmMofAccount(null);
		this.getManagedBean().setSmCompanyBasic(null);
		this.getManagedBean().setSmPersonnel(null);
		this.getManagedBean().setSupplierBranch(null);
		this.getManagedBean().setSupplierAddress(null);
	}	
	
	private String getSupplierNameById(Long supplierId) {
		SmSupplier smSupplier = new SmSupplier();
		smSupplier.setSupplierId(supplierId);
		
		QTSupplierResDTO resDTO = retrieveSupplierDetails(smSupplier);
		if (resDTO.isSuccessful()) {
			return resDTO.getSmSupplier().getCompanyName();
		}
		
		return StringUtils.EMPTY;
	}	
	
	private QTSupplierResDTO retrieveSupplierDetails(SmSupplier smSupplier) {
		QTSupplierResDTO resDTO = new QTSupplierResDTO();
		IQTSupplierSO qtSupplierSO = null;
		try {
			qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, 
					IQTSupplierSO.class);
			QTSupplierReqDTO reqDTO = new QTSupplierReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			resDTO = qtSupplierSO.retrieveSupplierDetails(reqDTO);
			resDTO.setSuccessful(true);
		} finally {
			qtSupplierSO = null;
		}	
		return resDTO;
	}
	
	/**
	 * Method to call Supplier Details popup in supplier finalization
	 */
	public void showReminder() {
		debugLogger("showReminder");

		this.getManagedBean().setReminderList(
				new ArrayList<ScQtCommitteeActivity>());

		List<ScQtCommitteeActivity> chairpersonList = new ArrayList<ScQtCommitteeActivity>();
		List<ScQtCommitteeActivity> memberList = new ArrayList<ScQtCommitteeActivity>();

		for (ScQtCommitteeActivity committee : getManagedBean()
				.getCommitteeEndorsementList()) {
			String memberRole = committee.getScCommitteeMember()
					.getMemberRole().getParameterCode();

			if (memberRole.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
				chairpersonList.add(committee);
			} else {
				if (!committee.isSignedOff()) {
					memberList.add(committee);
				}
			}
		}

		if (memberList.size() == 0) {
			// send reminder to Chairperson
			this.getManagedBean().getReminderList().addAll(chairpersonList);
		} else {
			// send reminder to Members
			this.getManagedBean().getReminderList().addAll(memberList);
		}
	}

	/**
	 * To send reminder to committee members
	 */
	public void sendReminder() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId()
					.toString());
			reqDTO.setCommitteMembers(getManagedBean()
					.getCommitteeEndorsementList());
			reqDTO.setSubject(getManagedBean().getSubject());
			reqDTO.setMessage(getManagedBean().getMessage());

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.sendNotifications(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				commonMessageAcknowledmentPage(MessageConst.INFO_700047,
						variable1, variable2);
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * For committee members to endorse the Supplier Finalization or chairperson
	 * sign off the Supplier Finalization
	 */
	public void signOff() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			ScQtCommitteeActivity committeeAct = null;
			boolean isChairperson = false;

			retrieveOnlineEndorsement();
			for (ScQtCommitteeActivity scQtCommitteeActivity : getManagedBean()
					.getCommitteeEndorsementList()) {
				if (scQtCommitteeActivity.getScCommitteeMember().getPmUser()
						.equals(getFacesScopes().getUserSessionBean())) {
					if (scQtCommitteeActivity.getScCommitteeMember()
							.getMemberRole()
							.equals(SourcingConstants.MEMBER_ROLE_CHAIRPERSON)) {
						isChairperson = true;
					}

					scQtCommitteeActivity.setSignedOff(true);
					committeeAct = scQtCommitteeActivity;
					break;
				}
			}

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtCommitteeActivity(committeeAct);

			SuppFinalizationResDTO resDTO = qtSuppFinalizationSO
					.signOff(reqDTO);
			if (resDTO.isSuccessful()) {

				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION);
				String variable2 = getManagedBean().getScQt().getQtNo();
				if (isChairperson) {
					commonMessageAcknowledmentPage(MessageConst.INFO_700049,
							variable1, variable2);
				} else {
					commonMessageAcknowledmentPage(MessageConst.INFO_700048,
							variable1, variable2);
				}
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	/**
	 * To retrieve the offline Endorsement (escalation)
	 */
	public void retrieveOfflineEndorsement() {
		this.getManagedBean().setOnlineBiddingActiveIndex(
				ONLINE_BIDDING_ACTIVE_INDEX_OFFLINE_ENDORSEMENT);

		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		List<ScQtFinalizationEndorsementRemarks> remarks = initializeRemarks(
				scQtFinalization, null, scQtFinalization.getScQtFinalizationEndorsementRemarks(), ScQtFinalizationEndorsementRemarks.class);
		
		scQtFinalization.setScQtFinalizationEndorsementRemarks(remarks);
		getManagedBean().setRemarks(remarks);
	}

	/**
	 * To handle file upload event
	 * 
	 * @param event
	 *            the file upload event object
	 * @throws FileNotFoundException
	 *             the exception that would be thrown if the file being updated does
	 *             not exist
	 * @throws IOException
	 *             the exception that would be thrown if an I/O error is encountered
	 *             while uploading the file
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtFinalization scQtFinalization = this.getManagedBean()
					.getScQtFinalization();
			ScQtFinalizationEndorsementAttachment attachment = scQtFinalization
					.getScQtFinalizationEndorsementAttachment();
			if (null == attachment) {
				attachment = new ScQtFinalizationEndorsementAttachment();
				attachment.setScQtFinalization(scQtFinalization);
				scQtFinalization
						.setScQtFinalizationEndorsementAttachment(attachment);
			} else {
				attachment.setScQtFinalization(scQtFinalization);
				scQtFinalization
						.setScQtFinalizationEndorsementAttachment(attachment);
			}
			attachment.setDocNo(String.valueOf(scQtFinalization
					.getFinalizationId()));
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setScQtFinalization(scQtFinalization);
		}
	}

	/**
	 * determineIcon for displaying image icon in pid
	 */
	public String determineIcon(ScQtApprovalAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";
		if (attachment != null) {
			String fileName = attachment.getFileName();
			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}
			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	/**
	 * To save offline endorsement
	 */
	public void saveOfflineEndorsement() {
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);

			int activeIndex = this.getManagedBean()
					.getOnlineBiddingActiveIndex();
			String taskId = this.getManagedBean().getTaskId();
			String[] outComes = this.getManagedBean().getOutComes();
			String processName = this.getManagedBean().getProcessName();
			ScQt scQt = getManagedBean().getScQt();
			Long currentStatusId = this.getManagedBean().getCurrentStatusId();
			
			ScQtFinalization scQtFinalization = prepareFinalizationInfo();

			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setScQtFinalization(scQtFinalization);
			qtSuppFinalizationSO.saveFinalizationOfflineEndorsement(reqDTO);

			renewSesionObject();
			setTaskDetails(activeIndex, taskId, outComes, processName, currentStatusId);

			Long qtId = scQt.getQtId();
			retrieveOnlineBiddingEndorsement(qtId);

			String variable1 = getParameterDesc(
					ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION)
					+ " " + scQt.getQtNo();
			String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_700004,
									new String[] { variable1, variable2 },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private ScQtFinalization prepareFinalizationInfo() {
		ScQtFinalization scQtFinalization = getManagedBean()
				.getScQtFinalization();
		
		finalizeRemarks(scQtFinalization.getScQtFinalizationEndorsementRemarks());		
		
		setUserDetails(scQtFinalization);

		return scQtFinalization;
	}
	
	/**
	 * To submit bidding endorsement
	 * 
	 * @param outcome
	 *  	 	the outcome
	 */
	public void submitBiddingEndorsement(String outcome) {
		debugLogger("submitBiddingEndorsement");
		this.getManagedBean().setAction(outcome);
		if(isDigiSigningRequired(RoleCode.PBA_CHAIRPERSON.value())) {
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		} else {
			updateBPMTask(outcome, null);
		}	
	}

	private void updateBPMTask(String outCome, PmDigiSign digiSign){
		debugLogger("updateBPMTask");
		IQTSuppFinalizationSO qtSuppFinalizationSO = null;
		SuppFinalizationReqDTO suppFinalizationReqDTO = new SuppFinalizationReqDTO();
		SuppFinalizationResDTO suppFinalizationResDTO = null;
		suppFinalizationReqDTO.setOutcome(outCome);
		suppFinalizationReqDTO.setRoleCode(getUserRoleCode());
		FacesScopes facesScope = new FacesScopes();
		suppFinalizationReqDTO.setPmUser(facesScope.getUserSessionBean());
		suppFinalizationReqDTO.setTaskId(this.getManagedBean().getTaskId());
		suppFinalizationReqDTO.setToken(facesScope
				.getBPMAuthenticationCtxToken());
		try {
			qtSuppFinalizationSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTSuppFinalizationSO.class);
			ScQt scQt = getManagedBean().getScQt();

			/*if (ParameterConst.ONLINE_BIDDING_MEETING 
					.equals(getManagedBean().getScQtFinalization()
							.getOnlineBiddingEndorse())) {
				// TODO - for endorse n sign off by committee member
				// status should be pending Endorsement
			} else {*/
			// TODO - for escalation, upload doc and submit
			// status should be pending Escalation

			if (null != digiSign) {
				updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION, 
						 scQt.getQtId(),  scQt.getQtNo());
			}

			ScQtFinalization scQtFinalization = prepareFinalizationInfo();

			suppFinalizationReqDTO.setScQtFinalization(scQtFinalization);
			suppFinalizationReqDTO.setPayLoad(this.getManagedBean()
					.getPayLoad());
			suppFinalizationReqDTO.setDigiSignData(digiSign);
			suppFinalizationResDTO = qtSuppFinalizationSO.updateBPMAction(suppFinalizationReqDTO);
				
			//}
			if (suppFinalizationResDTO.isSuccessful()) {
				String variable1 = getParameterDesc(
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION)
						+ " " + scQt.getQtNo();
				String variable2 = getMessageCommon("lbl.sc.online.bidding.endorsement");
				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			}
		} finally {
			qtSuppFinalizationSO = null;
		}
	}

	private void setTaskDetails(Integer activeIndex, String taskId,
			String[] outComes, String processName, Long currentStatusId) {
		if (activeIndex != null) {
			this.getManagedBean().setOnlineBiddingActiveIndex(activeIndex);
		}
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
	}

	private void commonMessageAcknowledmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								getFacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	
	/**
	 * To digitally sign using GPK device
	 * 
	 * @param digiSign
	 * 			the digital signature
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * @return  the textBeforeSign
	 */
	public String getTextBeforeSign() {
		StringBuilder sb = new StringBuilder();
		if(getManagedBean().getScQt() != null){
			sb.append(getManagedBean().getScQt().getQtNo());
		}
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		textBeforeSign = sb.toString();
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign
	 * 				the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
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

}
