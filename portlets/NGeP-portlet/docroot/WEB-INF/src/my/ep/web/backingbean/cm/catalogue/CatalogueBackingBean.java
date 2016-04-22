/*
 * 
 * ****************************************************
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.cm.catalogue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IZoneMaintenanceSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.CmConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.catalogue.CmTrackingDairyVO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceReqDTO;
import my.ep.dto.cm.catalogue.ZoneMaintenanceResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmImage;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.cm.catalog.CmKeyword;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.cm.catalog.CmSupplierZoneDTL;
import my.ep.persistence.entity.cm.catalog.CmTariffPricing;
import my.ep.persistence.entity.cm.catalog.CmTierPricing;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTariffPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.constant.cm.CmHelper;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.cm.helper.CmApplicationUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.ExtensionCodeVO;
import my.ep.web.vo.PricingVO;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.compat.portal.util.PortalUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;


/**
 * 
 * Class CatalogueBackingBean  
 * @author hketan
 */
@ManagedBean(name = "catalogueBackingBean")
//@ViewScoped
@RequestScoped
public class CatalogueBackingBean extends BaseBackingBean<CatalogueManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = Logger.getLogger(CatalogueBackingBean.class);

	/** The root. */
	private TreeNode root;

	/** The selected node. */
	private TreeNode selectedNode;

	/** The root1. */
	private TreeNode root1;

	/** The root2. */
	private TreeNode root2;

	/** The file. */
	private StreamedContent file;

	/** The remark rendered. */
	private boolean remarkRendered = false;

	/** The status filter bean. */
	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	/**
	 * Instantiates a new CatalogueBackingBean.
	 */
	public CatalogueBackingBean() {

		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	/**
	 * Catalog Items added to CatalogueMaintenance.
	 * 
	 * @return String(../catalogue/preRenderCatalogueMaintenanceFile.xhtml)  
	 */
	public String addServiceToMaintain() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());
		supplierReqDTO.setAllStatuses(true);

		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		log.info("getSmSupplier"
				+ smResDTO.getResultBean().getResultList().get(0));

		CodificationBackingBean bb = new CodificationBackingBean();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
		CmUnspsc commodity = bb.getManagedBean().getSelectedUNSPSCObj();
		log.info("..............bb.getManagedBean().getSelectedUNSPSCObj()="+commodity);
		if (commodity.getUnspscCode() != null) {

			codeReqDTO.setUnspscId(commodity
					.getUnspscId());
			CodificationResDTO codeResDTO = codificationSO
					.searchItem(codeReqDTO);
			log.info(">>>>>>>>>>>>>>>>>codeResDTO.getItemList()="+codeResDTO.getItemList());
			if (codeResDTO.getItemList() != null
					&& codeResDTO.getItemList().size() > 0) {

				CmItem item = codeResDTO.getItemList().get(0);
				log.info(">>>>>>>>>>>>>>>>>codeResDTO.getItemList().get(0)="+item);
				log.info(">>>>>>>>>>>>>>>>>item.getItemId()="+item.getItemId());
				CmSupplierItem supplierItem = new CmSupplierItem();

				supplierItem.setCmItem(item);
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getResultBean()
						.getResultList().get(0));
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);

			} else {
				log.info(">>>>>>>>>>>>>>>>else>>>>>>>>>coudnt find cmItem");

				CmType typeObj = null;
				if(commodity.getCmType()!=null && commodity.getCmType().size()>0){
					for(CmType a : commodity.getCmType()){
						if(a.getTypeCode().equals("00")){
							typeObj = a;
							break;
						}
					}

				}else{
					typeObj = new CmType();
					typeObj.setTypeCode("000");
					typeObj.setTypeName("N/A");
					typeObj.setUnspscId(commodity);
					codeReqDTO.setCmType(typeObj);
					typeObj = codificationSO.addNewType(codeReqDTO).getCmType();
				}

				CmBrand brandObj = null;
				if(commodity.getCmBrand()!=null && commodity.getCmBrand().size()>0)
					brandObj = commodity.getCmBrand().get(0);
				else{
					brandObj = new CmBrand();
					brandObj.setBrandCode("00");
					brandObj.setBrandName("N/A");
					brandObj.setUnspscId(commodity);
					codeReqDTO.setCmBrand(brandObj);
					brandObj = codificationSO.addNewBrand(codeReqDTO).getCmBrand();
				}

				CmMeasurement meaObj = null;
				if(commodity.getCmMeasurement()!=null && commodity.getCmMeasurement().size()>0)
					meaObj = commodity.getCmMeasurement().get(0);
				else{
					meaObj = new CmMeasurement();
					meaObj.setMeasurementCode("000");
					meaObj.setMeasurementName("N/A");
					meaObj.setUnspscId(commodity);
					codeReqDTO.setCmMeasurement(meaObj);
					meaObj = codificationSO.addNewMeasurement(codeReqDTO).getCmMeasurement();
				}

				CmColor colorObj = bb.searchColorObjByColorCode("00");//first record of table reserved for NA

				codeReqDTO.setUnspscCode(commodity.getUnspscCode());
				//				codeResDTO = codificationSO.searchCommodityObj(codeReqDTO);

				CmItem item = new CmItem();
				item.setCmUnspsc(commodity);
				item.setCmType(typeObj);
				item.setCmBrand(brandObj);
				item.setCmMeasurement(meaObj);
				item.setCmColor(colorObj);
				item.setItemName(commodity.getUnspscTitle());//because for service extension parts are NA, just leave empty.
				item.setExtensionCode(bb.getManagedBean()
						.getSelectedUNSPSCObj().getUnspscCode()
						+ typeObj.getTypeCode()
						+ brandObj.getBrandCode()
						+ meaObj.getMeasurementCode() + colorObj.getColorCode());

				log.info("item Name:" + item.getItemName());
				log.info("Extension Code:" + item.getExtensionCode());
				codeReqDTO.setCmItem(item);
				codeResDTO = codificationSO.saveNewItem(codeReqDTO);

				CmSupplierItem supplierItem = new CmSupplierItem();

				supplierItem.setCmItem(codeResDTO.getCmItem());
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getResultBean()
						.getResultList().get(0));
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);
			}

			catalogueSO.saveSupplierItem(reqDTO);
		}else{
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
							.getMessage(MessageConst.ERROR_715032, new String[] { ""
									+ bb.getManagedBean().getExtensionVoList()
									.size() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			return "";
		}



		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710007, new String[] { ""
								+ 1 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "../catalogue/preRenderCatalogueMaintenanceFile.xhtml";
	}

	/**
	 * Add new Item to supplier item
	 * 
	 * @return acknowledge.xhtml
	 */
	public String addToMaintenanceFile(String action){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);


		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());

		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();

		codeReqDTO.setExtensionCode(bb.getManagedBean().getRequestCode().getCmRecommendCodes().get(0).getUnspscCode());


		CodificationResDTO codeResDTO = codificationSO
				.searchItemByItemCode(codeReqDTO);

		if(codeResDTO.getCmItem()!=null){

			CmSupplierItem supplierItem = new CmSupplierItem();

			supplierItem.setCmItem(codeResDTO.getCmItem());
			supplierItem.setItemStatus("D");
			supplierItem.setSmSupplier(smResDTO.getResultBean()
					.getResultList().get(0));
			supplierItem.setRecordStatus(true);
			reqDTO.getSupplierItem().add(supplierItem);

		}else{
			CmItem item = new CmItem();

			String recommendCode = bb.getManagedBean().getRequestCode().getCmRecommendCodes().get(0).getUnspscCode();
			String unspscCode = recommendCode.substring(0, 8);
			String typeCode = recommendCode.substring(8, 11);
			String brandCode = recommendCode.substring(11, 13);
			String meaCode = recommendCode.substring(13, 16);
			String colorCode = recommendCode.substring(16, 18);

			codeReqDTO.setUnspscCode(unspscCode);
			codeResDTO = codificationSO.searchCommodityObj(codeReqDTO);

			CmUnspsc unspsc = codeResDTO.getCommodityObj();
			System.out.println("getUnspscId:"+unspsc.getUnspscId());

			item.setCmUnspsc(unspsc);

			//////////////////////////look up measurement////////////////////////////
			SearchBean<CmMeasurement> meaSearchBean = new SearchBean<CmMeasurement>();

			meaSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("measurementCode",
							SearchOperator.OPERATOR_EQUALS.value(), meaCode));

			meaSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("unspscId.unspscId",
							SearchOperator.OPERATOR_EQUALS.value(), unspsc.getUnspscId()));


			codeReqDTO.setMeasurementSearchBean(meaSearchBean);
			codeResDTO = codificationSO.searchMeasurementObj(codeReqDTO);
			System.out.println("getMeasurementList:"+codeResDTO.getMeasurementList().size());

			item.setCmMeasurement(codeResDTO.getMeasurementList().get(0));

			/////////////////////look up type/////////////////////
			SearchBean<CmType> typeSearchBean = new SearchBean<CmType>();

			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("typeCode",
							SearchOperator.OPERATOR_EQUALS.value(), typeCode));

			typeSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("unspscId.unspscId",
							SearchOperator.OPERATOR_EQUALS.value(), unspsc.getUnspscId()));

			codeReqDTO.setTypeSearchBean(typeSearchBean);
			codeResDTO = codificationSO.searchTypeObj(codeReqDTO);

			System.out.println("getTypeList:"+codeResDTO.getTypeList().size());

			item.setCmType(codeResDTO.getTypeList().get(0));
			////////////////////look up brand/////////////
			SearchBean<CmBrand> brandSearchBean = new SearchBean<CmBrand>();

			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("brandCode",
							SearchOperator.OPERATOR_EQUALS.value(), brandCode));

			brandSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("unspscId.unspscId",
							SearchOperator.OPERATOR_EQUALS.value(), unspsc.getUnspscId()));

			codeReqDTO.setBrandSearchBean(brandSearchBean);
			codeResDTO = codificationSO.searchBrandObj(codeReqDTO);
			System.out.println("getBrandList:"+codeResDTO.getBrandList().size());

			item.setCmBrand(codeResDTO.getBrandList().get(0));
			////////////////////look up color//////////////////////////
			SearchBean<CmColor> colorSearchBean = new SearchBean<CmColor>();
			colorSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("colorCode",
							SearchOperator.OPERATOR_EQUALS.value(), colorCode));

			colorSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatus",
							SearchOperator.OPERATOR_EQUALS.value(), true));
			codeReqDTO.setColorSearchBean(colorSearchBean);
			codeResDTO = codificationSO.searchColorObj(codeReqDTO);

			System.out.println("getColorList:"+codeResDTO.getColorList().size());


			item.setCmColor(codeResDTO.getColorList().get(0));
			item.setItemName(bb.getManagedBean().getRequestCode().getCmRecommendCodes().get(0).getUnspscTitle());
			item.setExtensionCode(bb.getManagedBean().getRequestCode().getCmRecommendCodes().get(0).getUnspscCode());
			codeReqDTO.setCmItem(item);
			codeResDTO = codificationSO.saveNewItem(codeReqDTO);

			CmItem updatedItem = codeResDTO.getCmItem();


			CmSupplierItem supplierItem = new CmSupplierItem();

			supplierItem.setCmItem(updatedItem);
			supplierItem.setItemStatus("D");
			supplierItem.setSmSupplier(smResDTO.getResultBean()
					.getResultList().get(0));
			supplierItem.setRecordStatus(true);
			reqDTO.getSupplierItem().add(supplierItem);


		}
		CatalogueResDTO resDTO = catalogueSO.saveSupplierItem(reqDTO);


		///////////////////End Bpm process/////////////////////////
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		codeReqDTO = new CodificationReqDTO();

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();

		bpmReqDTO.setCtxToken(token);
		bpmReqDTO.setTaskId(bb.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(bpmReqDTO);
		Object obj = res.getPojo();

		codeReqDTO.setTaskId(bb.getManagedBean().getTaskId());
		codeReqDTO.setOutCome(action);
		codeReqDTO.setToken(token);
		codeReqDTO.setPojo(obj);


		CodificationResDTO cdResDTO = codificationSO.updateExtensionRequest(codeReqDTO);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710007, new String[] { ""
								+ 1 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "/xhtml/common/acknowledge.xhtml";
	}

	public String addToMaintainByItemId(String action){
		System.out.println("**********action**************"+action);

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);


		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		supplierReqDTO.setUserId(pmUser.getUserId());

		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		UnspscCodeBackingBean bb = new UnspscCodeBackingBean();
		System.out.println("******getTaskId******"+bb.getManagedBean().getTaskId());
		for(CmRequestCode code : bb.getManagedBean().getReviewExtCodeList()){

			codeReqDTO.setUnspscCode(code.getTempCode());


			CodificationResDTO codeResDTO =codificationSO.searchUnspscObj(codeReqDTO);

			this.getManagedBean().setUnspscObj(codeResDTO.getUnspscObj());

			codeReqDTO.setUnspscId(this.getManagedBean().getUnspscObj().getUnspscId());
			codeReqDTO.setColorId(code.getColorId());
			codeReqDTO.setMeasurementId(code.getMeasurementId());
			codeReqDTO.setTypeId(code.getTypeId());
			codeReqDTO.setBrandId(code.getBrandId());
			CodificationResDTO itemResDTO =codificationSO.searchItem(codeReqDTO);

			System.out.println("this.getManagedBean().getUnspscObj().getUnspscId():"+this.getManagedBean().getUnspscObj().getUnspscId());
			System.out.println("code.getColorId()"+code.getColorId());
			System.out.println("code.getMeasurementId()"+code.getMeasurementId());
			System.out.println("code.getBrandId()"+code.getBrandId());
			System.out.println("code.getTypeId()"+code.getTypeId());
			System.out.println("itemResDTO.getCmItem()"+itemResDTO.getItemList().size());
			if(itemResDTO.getItemList()!=null && itemResDTO.getItemList().size()>0){
				CmSupplierItem supplierItem = new CmSupplierItem();

				supplierItem.setCmItem(itemResDTO.getItemList().get(0));
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getResultBean()
						.getResultList().get(0));
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);

			}else{

				CmItem item = new CmItem();


				item.setCmUnspsc(codeResDTO.getUnspscObj());

				//////////////////////////look up measurement////////////////////////////
				SearchBean<CmMeasurement> meaSearchBean = new SearchBean<CmMeasurement>();

				meaSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("measurementId",
								SearchOperator.OPERATOR_EQUALS.value(), code.getMeasurementId()));

				meaSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("unspscId.unspscId",
								SearchOperator.OPERATOR_EQUALS.value(), this.getManagedBean().getUnspscObj().getUnspscId()));


				codeReqDTO.setMeasurementSearchBean(meaSearchBean);
				CodificationResDTO meaCcodeResDTO = codificationSO.searchMeasurementObj(codeReqDTO);
				System.out.println("getMeasurementList:"+meaCcodeResDTO.getMeasurementList().size());

				item.setCmMeasurement(meaCcodeResDTO.getMeasurementList().get(0));

				/////////////////////look up type/////////////////////
				SearchBean<CmType> typeSearchBean = new SearchBean<CmType>();

				typeSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("typeId",
								SearchOperator.OPERATOR_EQUALS.value(), code.getTypeId()));

				typeSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("unspscId.unspscId",
								SearchOperator.OPERATOR_EQUALS.value(),this.getManagedBean().getUnspscObj().getUnspscId()));

				codeReqDTO.setTypeSearchBean(typeSearchBean);
				CodificationResDTO typeCodeResDTO = codificationSO.searchTypeObj(codeReqDTO);

				System.out.println("getTypeList:"+typeCodeResDTO.getTypeList().size());

				item.setCmType(typeCodeResDTO.getTypeList().get(0));
				////////////////////look up brand/////////////
				SearchBean<CmBrand> brandSearchBean = new SearchBean<CmBrand>();

				brandSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("brandId",
								SearchOperator.OPERATOR_EQUALS.value(), code.getBrandId()));

				brandSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("unspscId.unspscId",
								SearchOperator.OPERATOR_EQUALS.value(), this.getManagedBean().getUnspscObj().getUnspscId()));

				codeReqDTO.setBrandSearchBean(brandSearchBean);
				CodificationResDTO brandCodeResDTO = codificationSO.searchBrandObj(codeReqDTO);
				System.out.println("getBrandList:"+brandCodeResDTO.getBrandList().size());

				item.setCmBrand(brandCodeResDTO.getBrandList().get(0));
				////////////////////look up color//////////////////////////
				SearchBean<CmColor> colorSearchBean = new SearchBean<CmColor>();
				colorSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("colorId",
								SearchOperator.OPERATOR_EQUALS.value(), code.getColorId()));

				colorSearchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(), true));
				codeReqDTO.setColorSearchBean(colorSearchBean);
				CodificationResDTO colorCodeResDTO = codificationSO.searchColorObj(codeReqDTO);

				System.out.println("getColorList:"+colorCodeResDTO.getColorList().size());

				String brand = brandCodeResDTO.getBrandList().get(0).getBrandCode();
				String measurement = meaCcodeResDTO.getMeasurementList().get(0).getMeasurementCode();
				String type = typeCodeResDTO.getTypeList().get(0).getTypeCode();
				String color = colorCodeResDTO.getColorList().get(0).getColorCode();

				String brandName = brandCodeResDTO.getBrandList().get(0).getBrandName();
				String measurementName = meaCcodeResDTO.getMeasurementList().get(0).getMeasurementName();
				String typeName = typeCodeResDTO.getTypeList().get(0).getTypeName();
				String colorName = colorCodeResDTO.getColorList().get(0).getColorName();

				String extensionCode = code.getTempCode() + type + brand
						+ measurement + color;
				String itemName = this.getManagedBean().getUnspscObj().getUnspscTitle() + " "
						+ typeName + " " + brandName + " " + measurementName + " "
						+ colorName;


				item.setCmColor(colorCodeResDTO.getColorList().get(0));
				item.setItemName(itemName);
				item.setExtensionCode(extensionCode);
				codeReqDTO.setCmItem(item);
				codeResDTO = codificationSO.saveNewItem(codeReqDTO);

				CmItem updatedItem = codeResDTO.getCmItem();


				CmSupplierItem supplierItem = new CmSupplierItem();

				supplierItem.setCmItem(updatedItem);
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getResultBean()
						.getResultList().get(0));
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);

			}


		}
		CatalogueResDTO resDTO = catalogueSO.saveSupplierItem(reqDTO);
		FacesContext facesContext = FacesContext.getCurrentInstance();


		///////////////////End Bpm process/////////////////////////
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		codeReqDTO = new CodificationReqDTO();
		CodificationResDTO codeResDTO = null;

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);

		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();

		bpmReqDTO.setCtxToken(token);
		bpmReqDTO.setTaskId(bb.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(bpmReqDTO);
		Object obj = res.getPojo();

		codeReqDTO.setTaskId(bb.getManagedBean().getTaskId());
		codeReqDTO.setOutCome(action);
		codeReqDTO.setToken(token);
		codeReqDTO.setPojo(obj);


		codeResDTO = codificationSO.updateExtensionRequest(codeReqDTO);


		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710007, new String[] { ""
								+ bb.getManagedBean().getReviewExtCodeList().size() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return "/xhtml/common/acknowledge.xhtml";
	}



	/**
	 * Supplier items added to CatalogMaintaince.
	 * 
	 * @return String(../catalogue/preRenderCatalogueMaintenanceFile.xhtml)
	 */
	public String addToMaintain() {

		CodificationBackingBean bb = new CodificationBackingBean();
		if(bb.getManagedBean().getCloneExtensionVOs().size() < 1) {
			throw new EpException(MessageConst.ERROR_715014);
		}

		this.getManagedBean().setRefreshDraftSuppItemFlag(false);

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

		SmSupplier smSupplier = null;

		try{
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			if(smResDTO.getResultBean().getResultList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721017);
			}

			log.info("getSmSupplier: "
					+ smResDTO.getResultBean().getResultList().get(0));
			smSupplier = smResDTO.getResultBean().getResultList().get(0);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();

			log.debug("addToMaintain:" + bb.getManagedBean().getCloneExtensionVOs().size());

			bb.getManagedBean().setExtensionVoList(null);
			for (ExtensionCodeVO clone :bb.getManagedBean().getCloneExtensionVOs()) {
				ExtensionCodeVO extCodeVO = new ExtensionCodeVO();

				extCodeVO.setTypeId(clone.getTypeId());
				extCodeVO.setBrandId(clone.getBrandId());
				extCodeVO.setMeasurementId(Long.valueOf(clone.getMeasurementId()));
				extCodeVO.setColorId(clone.getColorId());


				extCodeVO.setTypeName(clone.getTypeName());
				extCodeVO.setBrandName(clone.getBrandName());
				extCodeVO.setMeasurementName(clone.getMeasurementName());
				extCodeVO.setColorName(clone.getColorName());

				bb.getManagedBean().getExtensionVoList().add(extCodeVO);

			}

			if (bb.getManagedBean().getExtensionVoList() != null
					&& bb.getManagedBean().getExtensionVoList().size() > 0) {
				for (int i = 0; i < bb.getManagedBean().getExtensionVoList().size(); i++) {

					ExtensionCodeVO obj = bb.getManagedBean().getExtensionVoList()
							.get(i);

					codeReqDTO.setBrandId(obj.getBrandId());
					codeReqDTO.setMeasurementId(obj.getMeasurementId());
					codeReqDTO.setTypeId(obj.getTypeId());
					codeReqDTO.setColorId(obj.getColorId());

					log.info("........obj.getBrandId()="+obj.getBrandId() + 
							"\n ........obj.obj.getMeasurementId()="+obj.getMeasurementId() +
							"\n ........obj.getTypeId()="+obj.getTypeId() +
							"\n ........obj.getColorId()="+obj.getColorId());

					CodificationResDTO codeResDTO = codificationSO
							.searchItem(codeReqDTO);

					if (codeResDTO.getItemList() != null
							&& codeResDTO.getItemList().size() > 0) {

						for (CmItem item : codeResDTO.getItemList()) {
							CmSupplierItem supplierItem = new CmSupplierItem();

							supplierItem.setCmItem(item);
							supplierItem.setItemStatus("D");
							supplierItem.setSmSupplier(smResDTO.getResultBean()
									.getResultList().get(0));
							supplierItem.setRecordStatus(true);
							reqDTO.getSupplierItem().add(supplierItem);
						}

					} else {
						log.debug("************else*******************");
						CmType typeObj = bb.searchTypeOjbyTypeId(obj.getTypeId());
						CmBrand brandObj = bb.searchBrandObjByBrandId(obj
								.getBrandId());
						CmMeasurement meaObj = bb
								.searchMeasurementByMeasurementId(obj
										.getMeasurementId());
						CmColor colorObj = bb.searchColorObjByColorId(obj
								.getColorId());

						codeReqDTO.setUnspscCode(bb.getManagedBean()
								.getSelectedUNSPSCObj().getUnspscCode());
						codeResDTO = codificationSO.searchCommodityObj(codeReqDTO);

						CmItem item = new CmItem();
						item.setCmUnspsc(codeResDTO.getCommodityObj());
						item.setCmType(typeObj);
						item.setCmBrand(brandObj);
						item.setCmMeasurement(meaObj);
						item.setCmColor(colorObj);
						item.setItemName(bb.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscTitle()
								+ (typeObj.getTypeName().equals("N/A") ? "": " "+typeObj.getTypeName())
								+ (brandObj.getBrandName().equals("N/A") ? "": " "+brandObj.getBrandName())
								+ (meaObj.getMeasurementName().equals("N/A") ? "" : " "+meaObj.getMeasurementName())
								+ (colorObj.getColorName().equals("N/A") ? "": " "+colorObj.getColorName()));
						item.setExtensionCode(bb.getManagedBean()
								.getSelectedUNSPSCObj().getUnspscCode()
								+ typeObj.getTypeCode()
								+ brandObj.getBrandCode()
								+ meaObj.getMeasurementCode()
								+ colorObj.getColorCode());

						log.info("item Name:" + item.getItemName());
						codeReqDTO.setCmItem(item);
						codeResDTO = codificationSO.saveNewItem(codeReqDTO);

						CmSupplierItem supplierItem = new CmSupplierItem();

						supplierItem.setCmItem(codeResDTO.getCmItem());
						supplierItem.setItemStatus("D");
						supplierItem.setSmSupplier(smResDTO.getResultBean()
								.getResultList().get(0));
						supplierItem.setRecordStatus(true);

						CmStandardPricing standardPrice = new CmStandardPricing();
						standardPrice.setCmSupplierItem(supplierItem);
						supplierItem.setCmStandardPricing(standardPrice);

						CmPromotionPricing promotionPrice = new CmPromotionPricing();
						promotionPrice.setCmSupplierItem(supplierItem);
						supplierItem.setCmPromotionPricing(promotionPrice);

						reqDTO.getSupplierItem().add(supplierItem);
					}

				}



				reqDTO.setUserId(pmUser.getUserId());
				reqDTO.setUserName(pmUser.getUserName());

				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				StatusReqDTO statusReqDTO = new StatusReqDTO();


				statusReqDTO.setStatusId(CmConst.DRAFT);

				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);

				reqDTO.setPmStatus(statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus());
				reqDTO.setRoleCode(CmApplicationUtil.getUserRoleValue());

				CatalogueResDTO resDTO = catalogueSO.saveSupplierItem(reqDTO);
				if(!resDTO.isSuccessful()) {					
					throw new EpException(MessageConst.ERROR_715016);
				}
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();

			// facesContext.addMessage(null, new
			// FacesMessage(FacesMessage.SEVERITY_INFO," Records Added Successfully",
			// ""));

			if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())) {
				retrieveSupplierItemsBySupplierId(catalogueSO, smSupplier.getSupplierId(), null);
			} else {
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
								.getMessage(MessageConst.INFO_710007, new String[] { ""
										+ bb.getManagedBean().getExtensionVoList()
										.size() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			//		} catch (Exception e) {
			//			e.printStackTrace();

			if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())) {




				SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();


				supplierReqDTO.setUserId(pmUser.getUserId());



				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0)
						.getSupplierId());




				reqDTO.setItemStatus(CmConst.ITEM_STATUS_DRAFT);
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().getDraftSupplierItemList().add(resDTO.getSupplierItemList().get(0));



				return navigateToCatalogDetForTempSupp(0);
			}


		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
			codificationSO = null;
		}



		return "../catalogue/preRenderCatalogueMaintenanceFile.xhtml";
	}

	/**
	 * Go to CatalogMaintain Screen
	 * 
	 * @return String(catalogueMaintenanceFile)
	 */
	public String GoToMaintain() {

		log.info("*****GoToMaintain*******");

		this.getManagedBean().getDraftSearchDataModel().reloadData();

		return "catalogueMaintenanceFile";
	}

	/**
	 * This method is used to deletes the draft items.
	 * 
	 * @param index
	 *           (int index(mandatory))
	 */
	public void deleteDraftItem(int index) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		try{
			if(index<0)
				index = this.getManagedBean().getTierRowIndex();

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setCmSupplierItemObj(this.getManagedBean().getDraftSearchDataModel().getList()
					.get(index));

			CatalogueResDTO resDTO = catalogueSO.deleteDraftItem(reqDTO);

			if (resDTO.isSuccessful()) {

				this.getManagedBean().getDraftSearchDataModel().getList().remove(index);

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_710005,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

	}

	/**
	 * This method is mark the draft items to delete Flag.
	 * 
	 * @param supplierItemId
	 *           (long supplierItemId(mandatory))
	 */

	public void deleteSupplierItem(long supplierItemId) throws Exception {
		log.info("Supplier item id to delete: " + supplierItemId);
		ICatalogueSO catalogueSO = null;
		try {
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			reqDTO.setSupplierItemId(supplierItemId);

			catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
			CatalogueResDTO resDTO = catalogueSO.softDeleteSupplierItem(reqDTO);

			if (resDTO.isSuccessful()) {

				retrieveSupplierItemsBySupplierId(catalogueSO, getSupplierId(), resDTO.getItemStatus());

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_710005,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} finally {
			catalogueSO = null;
		}
	}

	public void setItemToDelete(Long supplierItemIdToDelete) {
		log.info("Supplier item id to delete: " + supplierItemIdToDelete);
		this.getManagedBean().setItemToDelete(supplierItemIdToDelete);
	}
	/**
	 * Navigate to catalog details page.
	 * 
	 * @param index
	 *            int index(mandatory)
	 * @return String(editCatalogItem)
	 */
	public String navigateToCatalogDet(int i) {

		System.out.println("**********this.getManagedBean().getDraftTabPageSize()************"+this.getManagedBean().getDraftTabPageSize());
		int index = i%this.getManagedBean().getDraftTabPageSize();


		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + this.getManagedBean().getDraftSearchDataModel().getList().get(index).getSupplierItemId());
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		this.getManagedBean().setPreviousScreen(1);//catMaintenance

		try{
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setSupplierItemId(this.getManagedBean().getDraftSearchDataModel().getList().get(index).getSupplierItemId());
			CatalogueResDTO resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
			reqDTO.setSupplierId(resDTO.getSupplierItem().getSupplierId());

			log.info("resDTO.getSupplierItem().getSupplierId()" + resDTO.getSupplierItem().getSupplierId());


			retriveSupplierGstInfomation(resDTO.getSupplierItem().getSmSupplier());


			CmSupplierItem clone = (CmSupplierItem) SerializationUtils.clone(this.getManagedBean().getDraftSearchDataModel().getList().get(index));

			this.getManagedBean().setCloneCmSupplierItem(clone);
			this.getManagedBean().setCloneIndex(index);

			ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();
			bb.getManagedBean().setCatDetTabActiveIndex(0);

			//reqDTO.setSupplierItemId(this.getManagedBean().getMaintainItemList().get(index).getSupplierItemId());
			//resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
			this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
			this.getManagedBean()
			.getSelectedMaintainItem()
			.setCompareDesc(resDTO.getSupplierItem().getItemDesc());

			//sort Tier
			if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), "S");
			}
			//sort Tier	Promotion		
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), "P");
			}
			//sort Zonal Tier			
			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), "S");
			}
			//sort Zonal Tier Promotion
			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), "P");
			}

			List<CmZonalPromotionTierPricing> zonalPromotionTierPricings = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalPromotionTierPricings();
			if (zonalPromotionTierPricings != null && zonalPromotionTierPricings.size() > 0) {
				for (CmZonalPromotionTierPricing cmPricing : zonalPromotionTierPricings) {
					if (cmPricing.getParentPrice() != null) {
						cmPricing.getParentPrice().setStandardDisabled(true);
						cmPricing.setStandardDisabled(true);
					}
				}
			}

			List<CmPromotionTierPricing> cmList = this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings();
			if (cmList != null && cmList.size() > 0) {
				for (CmPromotionTierPricing cmPricing : cmList) {
					if (cmPricing.getParentPrice() != null) {
						cmPricing.getParentPrice().setStandardDisabled(true);
						cmPricing.setStandardDisabled(true);
					}
				}
			}

			this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing()
			.setStandardDisabled(true);

			this.getManagedBean().setDisplayCatalogue(true);
			updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
			this.getManagedBean().setPricingTabIndex(0);

			//		} catch (Exception e) {
			//			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}	

		//return "addCatalogItem";
		return "editCatalogItem";

	}

	/**
	 * Group prices by tier.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cmPricings
	 *                (List<T> cmPricings(mandatory))
	 * @param stand_id
	 *                (String stand_id(mandatory))
	 */
	private <T extends CmPricing> void groupPricesByTier(List<T> cmPricings, String stand_id){

		if(stand_id.equalsIgnoreCase("S")){
			//Order Tier or Zonal Tier
			Collections.sort(cmPricings, new Comparator<CmPricing>() {
				@Override
				public int compare(CmPricing p1, CmPricing p2) {

					//Sorts by 'stand_group_id' property
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
					//					return p1.getStand_group_id()<p2.getStand_group_id()?-1:p1.getStand_group_id()==p2.getStand_group_id()?doSecodaryOrderSort(p1,p2):1;
				}

				//If 'stand_group_id' property is equal sorts by 'fromQty' property
				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			});
		} else if(stand_id.equalsIgnoreCase("P")){
			//Order Promotion Tier or Zonal Promotion Tier
			Collections.sort(cmPricings, new Comparator<CmPricing>() {
				@Override
				public int compare(CmPricing p1, CmPricing p2) {
					//Sorts by 'stand_group_id' property
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
					//					return p1.getStand_group_id()<p2.getStand_group_id()?-1:doSecodaryOrderSort(p1,p2);
				}

				//If 'stand_group_id' property is equal sorts by 'promo_group_id' property
				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					if(p1.getPromo_group_id()<p2.getPromo_group_id())
						return -1;
					else if(p1.getPromo_group_id()==p2.getPromo_group_id())
						return doTernaryOrderSort(p1, p2);
					else
						return 1;

					//				    return p1.getPromo_group_id()<p2.getPromo_group_id()?-1:doTernaryOrderSort(p1, p2);
				}

				//If 'promo_group_id' property is equal sorts by 'fromQty' property
				public int doTernaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			});

		}


	}


	/**
	 * Navigate to published item detailed tab.
	 * 
	 * @param index
	 *            int index(mandatory)
	 * @return String(editCatalogItem)
	 */
	public String navigateToPublishedItemDet(int i) {


		int index = i%this.getManagedBean().getPublishTabPageSize();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();		
		CatalogueResDTO resDTO = new CatalogueResDTO();
		ICatalogueSO catalogueSO = null;

		try {
			catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			reqDTO.setSupplierItemId(this.getManagedBean().getPublishSearchDataModel().getList().get(index).getSupplierItemId());
			resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
		} finally {
			catalogueSO = null;
		}



		retriveSupplierGstInfomation(resDTO.getSupplierItem().getSmSupplier());

		this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
		this.getManagedBean().getSelectedMaintainItem()
		.setCompareDesc(resDTO.getSupplierItem().getItemDesc());
		this.getManagedBean().setTabActiveIndex(4);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		if(this.getManagedBean().getSelectedMaintainItem().getRecordStatusFlg().intValue()==1)
			this.getManagedBean().getSelectedMaintainItem().setRecordStatus(true);
		else
			this.getManagedBean().getSelectedMaintainItem().setRecordStatus(false);

		//sort Tier
		if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), "S");
		}
		//sort Tier	Promotion		
		if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), "P");
		}
		//sort Zonal Tier			
		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), "S");
		}
		//sort Zonal Tier Promotion
		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), "P");
		}

		updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		//return "addCatalogItem";
		return "editCatalogItem";
	}

	/**
	 * Navigate to expired item details tab.
	 * 
	 * @param index
	 *            int index(mandatory)
	 * @return String(editCatalogItem)
	 */
	public String navigateToExpiredItemDet(int i) {

		int index = i%this.getManagedBean().getExpiredTabPageSize();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();		
		CatalogueResDTO resDTO = new CatalogueResDTO();
		ICatalogueSO catalogueSO = null;

		try {
			catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);



			reqDTO.setSupplierItemId(this.getManagedBean().getExpiredSearchDataModel().getList().get(index).getSupplierItemId());
			resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
		} finally {
			catalogueSO = null;
		}
		retriveSupplierGstInfomation(resDTO.getSupplierItem().getSmSupplier());

		this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
		this.getManagedBean().getSelectedMaintainItem()
		.setCompareDesc(resDTO.getSupplierItem().getItemDesc());
		this.getManagedBean().setTabActiveIndex(5);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		if(this.getManagedBean().getSelectedMaintainItem().getRecordStatusFlg().intValue()==1)
			this.getManagedBean().getSelectedMaintainItem().setRecordStatus(true);
		else
			this.getManagedBean().getSelectedMaintainItem().setRecordStatus(false);

		//sort Tier
		if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), "S");
		}
		//sort Tier	Promotion		
		if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), "P");
		}
		//sort Zonal Tier			
		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), "S");
		}
		//sort Zonal Tier Promotion
		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().isEmpty()){
			groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), "P");
		}

		updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		//return "addCatalogItem";
		return "editCatalogItem";
	}

	/**
	 * Navigate to approval item details tab .
	 * 
	 * @param index
	 *            (int index(mandatory))
	 * @return String(addCatalogItem)
	 */
	public String navigateToApprovalItemDet(int i) {

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		this.getManagedBean().setPreviousScreen(1);//catMaintenance

		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();		
		CatalogueResDTO catResDTO = new CatalogueResDTO();
		ICatalogueSO catalogueSO = null;

		try{

			int index= i%this.getManagedBean().getPendingApprovalTabPageSize();

			catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			catReqDTO.setSupplierItemId(this.getManagedBean().getApprovalSearchDataModel().getList().get(index).getSupplierItemId());
			catResDTO = catalogueSO.retrieveSupplierItemById(catReqDTO);

			statusReqDTO.setDocType("CMR");
			statusReqDTO.setModuleCode("CM");
			statusReqDTO.setLanguageCode(languageCode);

			StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);

			this.getManagedBean().setStatusDescList(resDTO.getStatusDescList());

			/*
			 * this.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
			 * for(CmItemRejectReason reason :
			 * this.getManagedBean().getApprovalItemList
			 * ().get(index).getCmItemRejectReasons()){
			 * this.getManagedBean().getRejectReasonIds
			 * ().add(reason.getPmStatus().getStatusId());
			 * this.getManagedBean().setRejectReason(reason.getRemark()); }
			 */
			retriveSupplierGstInfomation(catResDTO.getSupplierItem().getSmSupplier());
			this.getManagedBean().setSelectedMaintainItem(catResDTO.getSupplierItem());


			this.getManagedBean().getSelectedMaintainItem()
			.setCompareDesc(catResDTO.getSupplierItem().getItemDesc());

			for (CmItemRejectReason remark : this.getManagedBean()
					.getSelectedMaintainItem().getCmItemRejectReasons()) {

				userReqDTO.setUserId(remark.getChangedBy());
				log.info("remark.getChangedBy()" + remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				log.info("userResDTO.getPmUser().getUserName():"
						+ userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}
			this.getManagedBean().setTabActiveIndex(1);
			this.getManagedBean().setDisplayCatalogue(true);
			//updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());

			/*} catch (Exception e) {
			e.printStackTrace();*/
		}finally {
			userSO = null;
			statusSO = null;
			catalogueSO = null;
		}	

		return "addCatalogItem";
		//return "editCatalogItem";
	}

	/**
	 * Navigate to approved item details tab.
	 * 
	 * @param index
	 *            int index(mandatory)
	 * @return String(addCatalogItem)
	 */
	public String navigateToApprovedItemDet(int i) {

		int index= i%this.getManagedBean().getApprovedTabPageSize();

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();		
		CatalogueResDTO resDTO = new CatalogueResDTO();


		reqDTO.setSupplierItemId(this.getManagedBean().getApprovedSearchDataModel().getList().get(index).getSupplierItemId());
		resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);


		retriveSupplierGstInfomation(resDTO.getSupplierItem().getSmSupplier());

		this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
		this.getManagedBean()
		.getSelectedMaintainItem()
		.setCompareDesc(resDTO.getSupplierItem().getItemDesc());
		this.getManagedBean().setTabActiveIndex(2);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		//updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		return "addCatalogItem";
		//return "editCatalogItem";
	}

	/**
	 * Navigate to rejected item details tab.
	 * 
	 * @param index
	 *            int index(mandatory)
	 * @return String(editCatalogItem)
	 */
	public String navigateToRejectedItemDet(int i) {

		int index = i%this.getManagedBean().getRejectedTabPageSize();


		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance		

		//		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		//		CatalogueReqDTO reqDTO = new CatalogueReqDTO();		
		//		CatalogueResDTO resDTO = new CatalogueResDTO();

		try{
			String languageCode = new FacesScopes().getUserLocale().getLanguage();

			StatusReqDTO statusReqDTO = new StatusReqDTO();

			statusReqDTO.setDocType("CMR");
			statusReqDTO.setModuleCode("CM");
			statusReqDTO.setLanguageCode(languageCode);

			StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);

			this.getManagedBean().setStatusDescList(resDTO.getStatusDescList());

			CatalogueReqDTO catReqDTO = new CatalogueReqDTO();

			CatalogueResDTO catResDTO = new CatalogueResDTO();

			catReqDTO.setSupplierItemId(this.getManagedBean().getRejectSearchDataModel().getList().get(index).getSupplierItemId());
			catResDTO = catalogueSO.retrieveSupplierItemById(catReqDTO);


			retriveSupplierGstInfomation(catResDTO.getSupplierItem().getSmSupplier());


			this.getManagedBean().getRejectSearchDataModel().getList().get(index).setCmItemRejectReasons(catResDTO.getSupplierItem().getCmItemRejectReasons());

			this.getManagedBean().setSelectedMaintainItem(catResDTO.getSupplierItem());
			this.getManagedBean().getSelectedMaintainItem().setCompareDesc(catResDTO.getSupplierItem().getItemDesc());

			UserReqDTO userReqDTO = new UserReqDTO();	
			for (CmItemRejectReason remark : catResDTO.getSupplierItem().getCmItemRejectReasons()) {

				userReqDTO.setUserId(remark.getChangedBy());
				log.info("remark.getChangedBy()" + remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				log.info("userResDTO.getPmUser().getUserName():"
						+ userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}

			this.getManagedBean().setTabActiveIndex(3);
			this.getManagedBean().setDisplayCatalogue(true);
			updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());

			/*} catch (Exception e) {
			e.printStackTrace();*/
		}finally {
			userSO = null;
			statusSO = null;
			catalogueSO = null;
		}

		//return "addCatalogItem";
		return "editCatalogItem";
	}

	/**
	 * Navigate to supplier item details.
	 * 
	 * @param supplierItem
	 *            (VCmSupplierItem supplierItem(mandatory))
	 * @return String(addCatalogView)
	 */
	public String navigateToSupplierItemDet(VCmSupplierItem supplierItem) {

		log.info("getDocId" + supplierItem.getDocId());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		this.getManagedBean().setPreviousScreen(0);//catSearch

		try{

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			reqDTO.setDocId(supplierItem.getDocId());
			reqDTO.setOutcome("Active");
			getManagedBean().setPriceTypeTariff(false);
			getManagedBean().setPricingTabIndex(0);
			getManagedBean().setItemSpec(null);

			// Panel Circular Item
			if((supplierItem.getCircularNo()!=null && supplierItem.getCircularNo().length()>0) && 
					(supplierItem.getContractNo()==null)) {
				CmSupplierItem cmSupplierItem = new CmSupplierItem();
				this.getManagedBean().setDisplayCatalogue(true);

				ICircularDetailSO circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularDetailSO.class);
				CircularDetailReqDTO circularReqDTO = new CircularDetailReqDTO();
				circularReqDTO.setPanelItemId(supplierItem.getDocId());
				CircularDetailResDTO circularseqDTO = circularDetailSO.getPanelItemById(circularReqDTO);
				CtPanelItem ctPanelItem = circularseqDTO.getCtPanelItem();
				if(ctPanelItem.getScRequestItem().getCmItem()!=null) {
					cmSupplierItem.setCmItem(ctPanelItem.getScRequestItem().getCmItem());
				} else {
					CmItem cmItem = new CmItem();
					cmItem.setExtensionCode(ctPanelItem.getScRequestItem().getTempUnspscCode());
					// TODO temp unspc code should have value.
					if(cmItem.getExtensionCode()==null) {
						cmItem.setExtensionCode("000"); 
					}
					cmItem.setItemName(ctPanelItem.getItemName());
					cmSupplierItem.setCmItem(cmItem);
				}
				cmSupplierItem.setPmUom(ctPanelItem.getPmUom());
				cmSupplierItem.setMinOrder(Double.valueOf(ctPanelItem.getMinimumOrderQty()).intValue());
				// Set the panel item specification
				if (ctPanelItem.getItemCategoryId().getParameterId() == getParameterByTypeAndCode(ParameterTypeConst.ITEM_CATEGORY_TYPE, 
						ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC).getParameterId()) {					
					for (CtPanelItemSpec ctPanelItemSpec : ctPanelItem.getCtPanelItemSpecs()) {
						this.getManagedBean().getItemSpec().add(ctPanelItemSpec);					
					}
				}

				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

				SupplierSearchReqDTO suppReqDTO = new SupplierSearchReqDTO();
				suppReqDTO.setRegNo(supplierItem.getBizRegNo());
				SupplierSearchResDTO suppResDTO = supplierSearchSO.retriveSupplierByEpNo(suppReqDTO);
				cmSupplierItem.setSmSupplier(suppResDTO.getSmSupplier());
				this.getManagedBean().setSelectedMaintainItem(cmSupplierItem);
				retriveSupplierGstInfomation(suppResDTO.getSmSupplier());
			} else if(supplierItem.getContractNo()!=null) { // Contract Item 
				CmSupplierItem cmSupplierItem = new CmSupplierItem();
				this.getManagedBean().setDisplayCatalogue(true);

				IContractItemSO contractItemSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
				ContractItemReqDTO itemReqDTO = new ContractItemReqDTO();
				itemReqDTO.setContractItemId(supplierItem.getDocId());
				CtContractItem ctContractItem = contractItemSO.getContractItemForCatalogueView(itemReqDTO).getCtContractItem();

				if(ctContractItem.getScRequestItem().getCmItem()!=null) {
					cmSupplierItem.setCmItem(ctContractItem.getScRequestItem().getCmItem());
				} else {
					CmItem cmItem = new CmItem();
					cmItem.setExtensionCode(ctContractItem.getScRequestItem().getTempUnspscCode());
					// TODO temp unspc code should have value.
					if(cmItem.getExtensionCode()==null) {
						cmItem.setExtensionCode("000"); 
					}
					cmItem.setItemName(ctContractItem.getScRequestItem().getItemName());
					cmSupplierItem.setCmItem(cmItem);
				}
				cmSupplierItem.setPmUom(ctContractItem.getScRequestItem().getUom());
				cmSupplierItem.setMinOrder(Double.valueOf(ctContractItem.getMinimumOrderQty()).intValue());
				cmSupplierItem.setSkuNumber(ctContractItem.getSku());
				getContractItemPricing(cmSupplierItem, ctContractItem);

				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
				SupplierSearchReqDTO suppReqDTO = new SupplierSearchReqDTO();
				suppReqDTO.setRegNo(supplierItem.getBizRegNo());
				SupplierSearchResDTO suppResDTO = supplierSearchSO.retriveSupplierByEpNo(suppReqDTO);
				cmSupplierItem.setSmSupplier(suppResDTO.getSmSupplier());
				this.getManagedBean().setSelectedMaintainItem(cmSupplierItem);
				retriveSupplierGstInfomation(suppResDTO.getSmSupplier());
			} else { // Catalogue Item
				CatalogueResDTO resDTO = null;
				resDTO = catalogueSO.viewSupplierItem(reqDTO);
				this.getManagedBean().setDisplayCatalogue(true);
				this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItemList().get(0));

				reqDTO.setSupplierId(resDTO.getSupplierItemList().get(0).getSmSupplier().getSupplierId());
				retriveSupplierGstInfomation(resDTO.getSupplierItemList().get(0).getSmSupplier());
				resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
				this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

			}
			//remove N/A from Item name if any
			String itemNameForView = this.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName().replaceAll("N/A", "");
			this.getManagedBean().getSelectedMaintainItem().getCmItem().setItemName(itemNameForView);
			log.info("*****************end********************");

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

		return "addCatalogView?faces-redirect=true";
	}

	/**
	 * get Contract Item Pricing.
	 * 
	 * @param cmSupplierItem
	 *            (CmSupplierItem cmSupplierItem(mandatory))
	 * @param ctContractItem
	 *            (CtContractItem ctContractItem(mandatory))
	 * 
	 */
	private void getContractItemPricing(CmSupplierItem cmSupplierItem, CtContractItem ctContractItem) {
		Date today = new Date();
		if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_STANDARD)) {
			CmStandardPricing cmStandardPricing = new CmStandardPricing();
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					cmStandardPricing.setPrice(ctItemPrice.getUnitPrice());
					cmStandardPricing.setStartDate(ctItemPrice.getEffDate());
					cmStandardPricing.setEndDate(ctItemPrice.getExpDate());
					break;
				}
			}
			cmSupplierItem.setCmStandardPricing(cmStandardPricing);
		} else if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_STANDARD)) {
			List<CmZonalStandardPricing> zStdPrices = new ArrayList<CmZonalStandardPricing>();
			CmZonalStandardPricing zonalStdPrice = null;
			CmSupplierZone supplierZone = null;
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					zonalStdPrice = new CmZonalStandardPricing();
					supplierZone = new CmSupplierZone();
					supplierZone.setSuppZoneId(ctItemPrice.getCtContractZone().getContractZoneId());
					zonalStdPrice.setCmSupplierZone(supplierZone);
					zonalStdPrice.setPrice(ctItemPrice.getUnitPrice());
					zonalStdPrice.setStartDate(ctItemPrice.getEffDate());
					zonalStdPrice.setEndDate(ctItemPrice.getExpDate());
					zStdPrices.add(zonalStdPrice);
				}
			}
			cmSupplierItem.setzStdPrices(zStdPrices);
			setSupplierZoneList(ctContractItem.getCtContractVer().getCtContractZones());
		} else if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)) {
			List<CmTierPricing> cmTierPricings = new ArrayList<CmTierPricing>();
			CmTierPricing tierPricing = null;
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					tierPricing = new CmTierPricing();
					tierPricing.setFromQty(ctItemPrice.getFromQty().intValue());
					tierPricing.setToQty(ctItemPrice.getToQty().intValue());
					tierPricing.setPrice(ctItemPrice.getUnitPrice());
					tierPricing.setStartDate(ctItemPrice.getEffDate());
					tierPricing.setEndDate(ctItemPrice.getExpDate());
					cmTierPricings.add(tierPricing);
				}
			}
			cmSupplierItem.setCmTierPricings(cmTierPricings);
		} else if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
			List<CmZonalTierPricing> cmZonalTierPricings = new ArrayList<CmZonalTierPricing>();
			CmZonalTierPricing zonalTierPricing = null;
			CmSupplierZone supplierZone = null;
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					zonalTierPricing = new CmZonalTierPricing();
					supplierZone = new CmSupplierZone();
					supplierZone.setSuppZoneId(ctItemPrice.getCtContractZone().getContractZoneId());
					zonalTierPricing.setCmSupplierZone(supplierZone);
					zonalTierPricing.setFromQty(ctItemPrice.getFromQty().intValue());
					zonalTierPricing.setToQty(ctItemPrice.getToQty().intValue());							
					zonalTierPricing.setPrice(ctItemPrice.getUnitPrice());
					zonalTierPricing.setStartDate(ctItemPrice.getEffDate());
					zonalTierPricing.setEndDate(ctItemPrice.getExpDate());
					cmZonalTierPricings.add(zonalTierPricing);
				}
			}
			cmSupplierItem.setCmZonalTierPricings(cmZonalTierPricings);
			setSupplierZoneList(ctContractItem.getCtContractVer().getCtContractZones());
		} else if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF)) {
			List<CmTariffPricing> cmTariffPricings = new ArrayList<CmTariffPricing>();
			CmTariffPricing tariffPricing = null;
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					tariffPricing = new CmTariffPricing();
					tariffPricing.setFromQty(ctItemPrice.getFromQty().intValue());
					tariffPricing.setToQty(ctItemPrice.getToQty().intValue());
					tariffPricing.setSubSequence(ctItemPrice.getSubSequence());
					tariffPricing.setPrice(ctItemPrice.getUnitPrice());
					tariffPricing.setStartDate(ctItemPrice.getEffDate());
					tariffPricing.setEndDate(ctItemPrice.getExpDate());
					cmTariffPricings.add(tariffPricing);
				}
			}
			cmSupplierItem.setCmTariffPricings(cmTariffPricings);
			getManagedBean().setPriceTypeTariff(true);
		} else if(ctContractItem.getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
			List<CmZonalTariffPricing> cmZonalTariffPricings = new ArrayList<CmZonalTariffPricing>();
			CmZonalTariffPricing zonalTariffPricing = null;
			CmSupplierZone supplierZone = null;
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				if(ctItemPrice.getEffDate().before(today) && ctItemPrice.getExpDate().after(today)) {
					zonalTariffPricing = new CmZonalTariffPricing();
					supplierZone = new CmSupplierZone();
					supplierZone.setSuppZoneId(ctItemPrice.getCtContractZone().getContractZoneId());
					zonalTariffPricing.setCmSupplierZone(supplierZone);
					zonalTariffPricing.setFromQty(ctItemPrice.getFromQty().intValue());
					zonalTariffPricing.setToQty(ctItemPrice.getToQty().intValue());	
					zonalTariffPricing.setSubSequence(ctItemPrice.getSubSequence());
					zonalTariffPricing.setPrice(ctItemPrice.getUnitPrice());
					zonalTariffPricing.setStartDate(ctItemPrice.getEffDate());
					zonalTariffPricing.setEndDate(ctItemPrice.getExpDate());
					cmZonalTariffPricings.add(zonalTariffPricing);
				}
			}
			cmSupplierItem.setCmZonalTariffPricings(cmZonalTariffPricings);
			setSupplierZoneList(ctContractItem.getCtContractVer().getCtContractZones());
			getManagedBean().setPriceTypeTariff(true);
		}
	}

	/**
	 * Sets the supplier zone list.
	 * 
	 * @param contractZoneList
	 *            (List<CtContractZone> contractZoneList(mandatory))
	 */
	private void setSupplierZoneList(List<CtContractZone> contractZoneList) {
		List<CmSupplierZone> supplierZoneList = new ArrayList<CmSupplierZone>();
		CmSupplierZone cmSupplierZone = null;
		for(CtContractZone ctContractZone : contractZoneList) {
			cmSupplierZone = new CmSupplierZone();
			cmSupplierZone.setSuppZoneId(ctContractZone.getContractZoneId());
			cmSupplierZone.setSuppZoneName(ctContractZone.getZoneName());
			supplierZoneList.add(cmSupplierZone);
		}
		getManagedBean().setSupplierZoneList(supplierZoneList);
	}

	/**
	 * Count tier prices by stand group id.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cmPricings
	 *            (List<T> cmPricings(mandatory))
	 * @param standGroupId
	 *            (int standGroupId(mandatory))
	 * @return count of tier prices
	 */
	public <T extends CmPricing> int countTierPricesByStandGroupId(List<T> cmPricings, int standGroupId){
		int count = 0;

		for(CmPricing pricing : cmPricings){
			if(pricing.getStand_group_id() == standGroupId)
				count++;
		}

		return count;
	}

	/**
	 * Disable add tier.
	 * 
	 * @return true, if successful
	 *         false,if fail
	 */
	public boolean disableAddTier(){
		boolean disable = false;
		int lastStdGrpId = 0;
		if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().size() > 0){
			lastStdGrpId = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().size() - 1).getStand_group_id();
		}

		if(lastStdGrpId > 0){
			for(int i = 1; i <= lastStdGrpId; i++){
				int countGroup = countTierPricesByStandGroupId(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), i);
				if(countGroup < 2 && countGroup>0){
					disable = true;
					break;
				}
			}
		}
		return disable;
	}

	/**
	 * Disable add zonal tier.
	 * 
	 * @return true, if successful
	 *         false,if fail
	 */       
	public boolean disableAddZonalTier(){
		boolean disable = false;
		int lastStdGrpId = 0;
		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().size() > 0){
			lastStdGrpId = this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().size() - 1).getStand_group_id();
		}

		if(lastStdGrpId > 0){
			for(int i = 1; i <= lastStdGrpId; i++){
				int countGroup = countTierPricesByStandGroupId(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), i);
				if(countGroup < 2 && countGroup>0){
					disable = true;
					break;
				}
			}
		}
		return disable;
	}

	/**
	 * Adds the new tier pricing.
	 */
	public void addNewTierPricing() {

		log.debug("*****addNewTierPricing*****");
		CmTierPricing price = new CmTierPricing();

		if (this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().size() > 0) {
			int size = this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().size();
			price.setStand_group_id(this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings()
					.get(size - 1).getStand_group_id() + 1);
		} else {
			price.setStand_group_id(1);
		}
		price.setFromQty(1);
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
		.add(price);
	}

	/**
	 * Adds the new promotion tier pricing.
	 */
	public void addNewPromotionTierPricing() {

		log.debug("*****addNewPromotionTierPricing*****");
		CmPromotionTierPricing price = new CmPromotionTierPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
		.getCmPromotionTierPricings().add(price);
	}

	/**
	 * Adds the new zonal tier pricing.
	 */
	public void addNewZonalTierPricing() {

		CmZonalTierPricing price = new CmZonalTierPricing();

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().size() > 0) {
			int size = this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().size();
			price.setStand_group_id(this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings()
					.get(size - 1).getStand_group_id() + 1);
		} else {
			price.setStand_group_id(1);
		}
		price.setFromQty(1);
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
		.getCmZonalTierPricings().add(price);

	}

	/**
	 * Adds the new zonal promotion tier pricing.
	 */
	public void addNewZonalPromotionTierPricing() {

		CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
		.getCmZonalPromotionTierPricings().add(price);

	}

	/**
	 * Adds the new zonal standard pricing.
	 */
	public void addNewZonalStandardPricing() {

		CmZonalStandardPricing price = new CmZonalStandardPricing();

		if (this.getManagedBean().getSelectedMaintainItem()
				.getzStdPrices() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getzStdPrices().size() > 0) {
			int size = this.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices().size();
			price.setStand_group_id(this.getManagedBean()
					.getSelectedMaintainItem().getzStdPrices()
					.get(size - 1).getStand_group_id() + 1);
		} else {
			price.setStand_group_id(1);
		}

		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem().getzStdPrices()
		.add(price);








	}

	/**
	 * Adds the new zonal promotion pricing.
	 */
	public void addNewZonalPromotionPricing() {

		CmZonalPromotionPricing price = new CmZonalPromotionPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().add(price);

	}

	/*
	 * public void addNewPromotionPricing(){
	 * 
	 * 
	 * 
	 * CmPromotionPricing price = new CmPromotionPricing();
	 * 
	 * 
	 * this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricings().add
	 * (price);
	 * 
	 * }
	 * 
	 * public void addNewStandardPricing(){
	 * 
	 * 
	 * 
	 * CmStandardPricing price = new CmStandardPricing();
	 * 
	 * 
	 * this.getManagedBean().getSelectedMaintainItem().getCmStandardPricings().add
	 * (price);
	 * 
	 * }
	 */

	/**
	 * District checked.
	 */
	public void districtChecked() {

		log.debug("********hktan**********");

		/*
		 * log.debug("boolean:"+this.getManagedBean().getSelectedPricing().
		 * isSelected());
		 * if(this.getManagedBean().getSelectedPricing().isSelected()){
		 * this.getManagedBean().setDistrictIND(true); }else{
		 * this.getManagedBean().setDistrictIND(false); }
		 */
	}

	/**
	 * Adds the new district pricing.
	 */
	public void addNewDistrictPricing() {
		PricingVO price = new PricingVO();

		this.getManagedBean().getDistrictPricingList().add(price);
	}

	/**
	 * Cancel to maintenance file.
	 * 
	 * @return String(catalogMaintenanceFile)
	 */
	public String cancelToMaintenanceFile() {

		log.info("*********cancelToMaintenanceFile*************");

		if(this.getManagedBean().getCloneCmSupplierItem()!=null && this.getManagedBean().getCloneIndex()>=0){
			this.getManagedBean().getDraftSearchDataModel().getList().set(this.getManagedBean().getCloneIndex(),this.getManagedBean().getCloneCmSupplierItem());
		}
		// this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getCloneCmSupplierItem());

		return "catalogMaintenanceFile";
	}

	/**
	 * Cancel back to maintenance file.
	 * 
	 * @return String(catalogMaintenanceFile?faces-redirect=true)
	 */
	public String cancelBackToMaintenanceFile() {

		log.info("*********cancelBackToMaintenanceFile*************");

		return "catalogMaintenanceFile?faces-redirect=true";
	}

	/**
	 * Ok to maintenance file.
	 * 
	 * @return String(catalogMaintenanceFile?faces-redirect=true)
	 */
	public String okToMaintenanceFile() {

		log.info("*********okToMaintenanceFile*************");

		for (CmPricing price : this.getManagedBean().getSelectedMaintainItem()
				.getCmPricings()) {
			log.info("price.getPrice()" + price.getPrice());
		}

		return "catalogMaintenanceFile?faces-redirect=true";
		// return "catalogMaintenanceFile";
	}

	/**
	 * Advance search.
	 */
	public void advanceSearch() {

		log.debug("#########advanceSearch#############"
				+ this.getManagedBean().isAdvanceSearchInd());
		log.debug("getSearchType:" + this.getManagedBean().getSearchType());
		if (this.getManagedBean().getSearchType().equalsIgnoreCase("S")) {
			this.getManagedBean().setAdvanceSearchInd(false);
		} else {
			this.getManagedBean().setAdvanceSearchInd(true);
		}

	}

	/**
	 * On node delete.
	 */
	public void onNodeDelete() {

		FacesContext context = FacesContext.getCurrentInstance();

		Map map2 = context.getExternalContext().getRequestParameterMap();
		String newCodes = (String) map2.get("name1");

		log.debug("***event.getTreeNode()*****:" + newCodes);
	}

	/**
	 * Upload the catalog items  in CatalogMaintainFile by using excel.
	 * 
	 * @param event
	 *            (FileUploadEvent event(mandatory))
	 * @throws Exception
	 *             the exception
	 */
	public void uploadExcel(FileUploadEvent event) throws Exception {
		try {
			log.debug("uploadExcel");
			UploadedFile item = event.getFile();

			log.debug("item.getFileName()" + item.getFileName());

			SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

			FileInputStream file = (FileInputStream) item.getInputstream();

			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(1);

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();

			List<CmSupplierItem> cmSupplierItems = this.getManagedBean().getDraftSearchDataModel().getList();
			List<CmSupplierItem> uploadedSupplierItems = new ArrayList<CmSupplierItem>();
			// List<CmSupplierItem> catItems = new ArrayList<CmSupplierItem>();

			Double supplierItemId = new Double(0);
			Double price;
			Date startDate = null;
			Date endDate = null;
			String additionalItemDesc = null;
			Double frequency;
			String uom = null;
			String modelNo = null;
			String skuNo = null;
			String manfPartNo = null;
			Double minOrder;
			Double maxOrder;
			Double leadTime;
			// Map map = new HashMap();

			Row row = rowIterator.next();

			String invalidDataMsg = MessageUtils.getMessage(MessageConst.ERROR_715034,MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())+":\n";

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			int rowNum = 1;
			int count = 0;
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				//uom
				if ((row.getCell(10) == null || row.getCell(10).getStringCellValue().isEmpty())
						&& (row.getCell(0) != null && !row.getCell(0).toString().isEmpty())) {
					invalidDataMsg = invalidDataMsg.concat("Row" + rowNum
							+ "\n");
					count++;
				} else {

					if (row.getCell(0) != null && !row.getCell(0).toString().isEmpty()) {
						supplierItemId = row.getCell(0).getNumericCellValue();
						log.info("Supp Item Id");
					} else {
						break;
					}

					// CmSupplierItem catalogItem = new CmSupplierItem();
					log.info("***********supplierItemId" + supplierItemId);
					if (supplierItemId > 0) {
						//						for (CmSupplierItem supplierItem : cmSupplierItems) {
						for(int i = 0; i < cmSupplierItems.size(); i++) {
							CmSupplierItem supplierItem = cmSupplierItems.get(i);
							CatalogueReqDTO reqDTO1 = new CatalogueReqDTO();							
							reqDTO1.setSupplierItemId(supplierItem.getSupplierItemId());
							CatalogueResDTO resDTO1 = catalogueSO.retrieveSupplierItemById(reqDTO1);
							supplierItem = resDTO1.getSupplierItem();

							if (supplierItem.getSupplierItemId() == supplierItemId) {

								CmStandardPricing cmStandardPricing = new CmStandardPricing();

								if (supplierItem.getCmStandardPricing() != null) {
									cmStandardPricing = supplierItem
											.getCmStandardPricing();
								}

								System.out
								.println("cmStandardPricing.getPricingType()"
										+ cmStandardPricing
										.getPricingType());
								if (cmStandardPricing.getPricingType() == null) {
									cmStandardPricing = new CmStandardPricing();
									cmStandardPricing
									.setCmSupplierItem(supplierItem);
									supplierItem
									.setCmStandardPricing(cmStandardPricing);
								}

								//price
								if (row.getCell(7) != null && row.getCell(7).getCellType()==Cell.CELL_TYPE_NUMERIC) {
									//log.info("row.getCell(7).getCellType() = "+row.getCell(7).getCellType());
									price = row.getCell(7).getNumericCellValue();
									cmStandardPricing.setPrice(new BigDecimal(price));
								}

								log.info("row.getCell(8).getCellType(): " + row.getCell(8).getCellType());
								//start date
								if (row.getCell(8) != null && row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC) {
									startDate = row.getCell(8).getDateCellValue();
									cmStandardPricing.setStartDate(startDate);
								}else if (row.getCell(8) != null && row.getCell(8).getCellType() == Cell.CELL_TYPE_STRING &&
										!row.getCell(8).getStringCellValue().isEmpty()) {
									startDate = new SimpleDateFormat("dd/MM/yyyy").parse(row.getCell(8).getStringCellValue());
									cmStandardPricing.setStartDate(startDate);
								}

								/*if (row.getCell(8) != null && !row.getCell(8).getStringCellValue().isEmpty()) {
									//log.info("row.getCell(8).getCellType() = "+row.getCell(8).getCellType());
									if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC)
										startDate = row.getCell(8).getDateCellValue();
									else if (row.getCell(8).getCellType() == Cell.CELL_TYPE_STRING)
										startDate = new SimpleDateFormat("dd/mm/yyyy").parse(row.getCell(8).getStringCellValue());

									cmStandardPricing.setStartDate(startDate);
								}*/

								log.info("row.getCell(9).getCellType(): " + row.getCell(9).getCellType());
								//end date
								if (row.getCell(9) != null && row.getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC) {
									endDate = row.getCell(9).getDateCellValue();
									cmStandardPricing.setEndDate(endDate);
								}else if (row.getCell(9) != null && row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING &&
										!row.getCell(9).getStringCellValue().isEmpty()) {
									endDate = new SimpleDateFormat("dd/MM/yyyy").parse(row.getCell(9).getStringCellValue());
									cmStandardPricing.setEndDate(endDate);
								}

								/*if (row.getCell(9) != null && !row.getCell(9).getStringCellValue().isEmpty()) {
									//log.info("row.getCell(9).getCellType() = "+row.getCell(9).getCellType());
									if (row.getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC)
										endDate = row.getCell(9).getDateCellValue();
									else if (row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING)
										endDate = new SimpleDateFormat("dd/mm/yyyy").parse(row.getCell(9).getStringCellValue());

									cmStandardPricing.setEndDate(endDate);
								}*/

								//uom
								if (row.getCell(10) != null && !row.getCell(10).getStringCellValue().isEmpty()) {

									IUomSO uomSO = (IUomSO) BusinessDelegate
											.getInstance().getServiceObject(
													"pm", IUomSO.class);
									UomResDTO resDTO = uomSO.retrieveUomList();
									List<PmUom> uomList = resDTO.getPmUomList();

									uom = row.getCell(10).toString();
									PmUom pmUom = new PmUom();
									for (PmUom u : uomList) {
										if (u.getUomName() != null
												&& u.getUomName()
												.equalsIgnoreCase(uom)) {
											pmUom = u;
											break;
										}
									}

									if (pmUom.getUomCode() != null)
										supplierItem.setPmUom(pmUom);
									else {
										FacesContext facesContext = FacesContext.getCurrentInstance();
										facesContext.addMessage(
												null,
												new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
														MessageUtils.getMessage(MessageConst.ERROR_715033,
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes().getUserLocale())));
										break;
									}
								}

								//item desc
								if (row.getCell(11) != null && !row.getCell(11).toString().isEmpty()) {
									additionalItemDesc = row.getCell(11).toString();
									supplierItem.setItemDesc(additionalItemDesc);
									supplierItem.setReviewRequired(true);
								}
								// Removed Frequency for Bug#3664 
								/*//frequency
								if (row.getCell(12) != null && !row.getCell(12).toString().isEmpty()) {
									frequency = row.getCell(12)
											.getNumericCellValue();
									supplierItem.setFrequency(new BigDecimal(
											frequency));
								}*/

								//model
								if (row.getCell(12) != null
										&& !row.getCell(12).toString()
										.isEmpty()) {
									modelNo = row.getCell(12).toString();
									supplierItem.setModelNumber(modelNo);
								}

								//sku number
								if (row.getCell(13) != null
										&& !row.getCell(13).toString()
										.isEmpty()) {
									skuNo = row.getCell(13).toString();
									supplierItem.setSkuNumber(skuNo);
								}

								//manufacturer no
								if (row.getCell(14) != null
										&& !row.getCell(14).toString()
										.isEmpty()) {
									manfPartNo = row.getCell(14).toString();
									supplierItem.setManufacturer(manfPartNo);
								}

								//min order
								if (row.getCell(15) != null
										&& !row.getCell(15).toString()
										.isEmpty()) {
									minOrder = row.getCell(15)
											.getNumericCellValue();
									supplierItem.setMinOrder(minOrder
											.intValue());
								}
								// Removed MaxOrder for Bug#3664 
								/*//max order
								if (row.getCell(17) != null
										&& !row.getCell(17).toString()
												.isEmpty()) {
									maxOrder = row.getCell(17)
											.getNumericCellValue();
									supplierItem.setMaxOrder(maxOrder
											.intValue());
								}*/

								//lead time
								if (row.getCell(16) != null
										&& !row.getCell(16).toString()
										.isEmpty()) {
									leadTime = row.getCell(16)
											.getNumericCellValue();
									supplierItem.setLeadTime(new BigDecimal(
											leadTime));
								}

								// set prices to null when upload from excel. 
								supplierItem.setCmPromotionPricing(null);
								supplierItem.setCmTierPricings(null);
								supplierItem.setCmPromotionTierPricings(null);
								supplierItem.setzStdPrices(null);
								supplierItem.setCmZonalPromotionPricings(null);
								supplierItem.setCmZonalTierPricings(null);
								supplierItem.setCmZonalPromotionTierPricings(null);

								supplierItem.setCmTariffPricings(null);
								supplierItem.setCmZonalTariffPricings(null);


								// catItems.add(catalogItem);

								//								int existingIndex = cmSupplierItems
								//										.indexOf(supplierItem);

								//								this.getManagedBean().getMaintainItemList()
								//										.set(existingIndex, supplierItem);
								log.info("supplierItem3: " + supplierItem);
								//								cmSupplierItems.set(i, supplierItem);
								//								log.info("supplierItem4: " + cmSupplierItems.get(i));
								uploadedSupplierItems.add(supplierItem);
							}
						}
					}
				}

				rowNum++;
			}

			log.debug("********sheet2**********");

			file.close();
			// FileOutputStream out =
			// new FileOutputStream(new File("C"));
			// workbook.write(out);
			// out.close();


			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setSaveSupplierItems(uploadedSupplierItems);
			CatalogueResDTO resDTO = catalogueSO.updateSupplierItem(reqDTO);

			/*
			 * for(CmSupplierItem cmSupplierItem : cmSupplierItems){
			 * log.info("\nItem Code:" +
			 * cmSupplierItem.getCmItem().getExtensionCode());
			 * log.info("\nItem Desc:" +
			 * cmSupplierItem.getCmItem().getItemName()); }
			 */

			if (count > 0) {
				log.info(invalidDataMsg);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", invalidDataMsg));
			} else {
				log.info("Updated successfully.");
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"", MessageUtils.getMessage(MessageConst.INFO_711001,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				/*ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);
				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());*/
				//reqDTO.setItemStatus("D");
				//resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);




				this.getManagedBean().getDraftSearchDataModel().reloadData();


				/*this.getManagedBean().setMaintainItemList(
						resDTO.getSupplierItemList());*/
				this.getManagedBean().setTabActiveIndex(0);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to download the catalog items in Excel sheet .
	 * 
	 * @return Excel sheet file
	 * @throws Exception
	 *             the exception
	 */
	public StreamedContent getFile() throws Exception {

		log.debug("***write***");
		PortletResponse resp = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ "Catalogue-offline-template.xlsx");
		response.flushBuffer();

		String tempDir = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/excelUpload");
		log.debug("tempDir" + tempDir);
		// Read Excel document first
		FileInputStream input_document = new FileInputStream(new File(tempDir
				+ "/Catalogue-offline-template.xlsx"));
		// convert it into a POI object
		XSSFWorkbook my_xlsx_workbook = new XSSFWorkbook(input_document);
		// Read excel sheet that needs to be updated
		XSSFSheet product = my_xlsx_workbook.getSheetAt(1);
		XSSFSheet uomPreload = my_xlsx_workbook.getSheetAt(2);
		// XSSFSheet zonal = my_xlsx_workbook.getSheetAt(3);

		// sheet.protectSheet("welcome1");

		CellStyle catalogCellStyle = my_xlsx_workbook.createCellStyle();
		CreationHelper createHelper = my_xlsx_workbook.getCreationHelper();

		// add the 1st title row

		int no = 1;

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CodificationReqDTO reqDTO;
		CatalogueReqDTO catalogueReqDTO;

		for (int i = 0; i < this.getManagedBean().getCloneCmSupplierItemLs().size(); i++) {
			List dataLs = new Vector();
			CmSupplierItem catalog = this.getManagedBean()
					.getCloneCmSupplierItemLs().get(i);

			// dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscCode());
			// dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscTitle());
			dataLs.add(catalog.getSupplierItemId());
			dataLs.add(catalog.getCmItem().getExtensionCode());
			dataLs.add(catalog.getCmItem().getItemName());

			reqDTO = new CodificationReqDTO();
			reqDTO.setSupplierItemId(catalog.getSupplierItemId());
			CodificationResDTO resDTO = codificationSO.getCmItemBySupplierItemId(reqDTO);
			CmItem cmItem = resDTO.getCmItem();
			dataLs.add(cmItem.getCmType().getTypeName());
			dataLs.add(cmItem.getCmBrand().getBrandName());
			dataLs.add(cmItem.getCmMeasurement().getMeasurementName());
			dataLs.add(cmItem.getCmColor().getColorName());

			/*catalogueReqDTO = new CatalogueReqDTO();
			catalogueReqDTO.setSupplierItemId(catalog.getSupplierItemId());
			CatalogueResDTO catalogueResDTO = catalogueSO.getAllPrice(catalogueReqDTO);

			CmPricing cmPricing = new CmPricing();
			for (CmPricing pricing : catalogueResDTO.getSupplierItem().getCmPricings()) {

				if (pricing.getPricingType().equalsIgnoreCase("01")) {
					cmPricing = pricing;
				}
			}
			 */
			CmStandardPricing cmStandardPricing = catalog.getCmStandardPricing();
			if(cmStandardPricing.getPricingType().equalsIgnoreCase("01"))
			{
				if (cmStandardPricing.getPrice() != null)
					dataLs.add(cmStandardPricing.getPrice());
				else
					dataLs.add("");

				if (cmStandardPricing.getStartDate() != null)
					dataLs.add(formater.format(cmStandardPricing.getStartDate()));
				else
					dataLs.add("");

				if (cmStandardPricing.getEndDate() != null)
					dataLs.add(formater.format(cmStandardPricing.getEndDate()));
				else
					dataLs.add("");
			}
			// Removed Frequency and MaxOrder for Bug#3664 and Default Value is '1' for MinOrder
			dataLs.add(catalog.getPmUom().getUomName());
			dataLs.add(catalog.getItemDesc());
			//dataLs.add(catalog.getFrequency());
			dataLs.add(catalog.getModelNumber());
			dataLs.add(catalog.getSkuNumber());
			dataLs.add(catalog.getManufacturer());
			dataLs.add(catalog.getMinOrder()!=null?catalog.getMinOrder():new Integer(1));
			//dataLs.add(catalog.getMaxOrder());
			dataLs.add(catalog.getLeadTime());

			addCellToRow(product, no++, dataLs, catalogCellStyle, createHelper);

		}

		// product.lockSelectUnlockedCells();
		product.enableLocking();
		// my_xlsx_workbook.lockStructure();

		IUomSO uomSO = (IUomSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUomSO.class);
		UomResDTO resDTO = uomSO.retrieveUomList();
		List<PmUom> uomList = resDTO.getPmUomList();

		int uomNo = 1;
		for (PmUom obj : uomList) {
			List dataLs = new Vector();
			dataLs.add(obj.getUomName());
			addCellToRow(uomPreload, uomNo++, dataLs, catalogCellStyle, createHelper);
		}

		uomPreload.enableLocking();
		my_xlsx_workbook.lockStructure();

		input_document.close();

		my_xlsx_workbook.write(response.getOutputStream());

		// close the stream

		return null;
	}

	/**
	 * Adds the cell to row.
	 * 
	 * @param sheet
	 *            (XSSFSheet sheet(mandatory))
	 * @param count
	 *            (int count(mandatory))
	 * @param colValList
	 *            (List colValList(mandatory))
	 * @param catalogCellStyle
	 *            (CellStyle catalogCellStyle(mandatory))
	 * @param createHelper
	 *            (CreationHelper createHelper(mandatory))
	 * @throws Exception
	 *             the exception
	 */
	public void addCellToRow(XSSFSheet sheet, int count, List colValList,
			CellStyle catalogCellStyle, CreationHelper createHelper) throws Exception {

		Row row = sheet.createRow(count);
		XSSFCellStyle cs = sheet.getWorkbook().createCellStyle();
		XSSFDataFormat df = sheet.getWorkbook().createDataFormat();
		XSSFCellStyle unlockedCellStyle;

		for (int i = 0; i < colValList.size(); i++) {
			unlockedCellStyle = sheet.getWorkbook().createCellStyle();
			unlockedCellStyle.setLocked(false);

			Object colVal = (Object) colValList.get(i);

			Cell cell = row.createCell(i);
			/*
			 * if(i==0){ catalogCellStyle.setLocked(false);
			 * cell.setCellStyle(catalogCellStyle); } if(i==1){
			 * catalogCellStyle.setLocked(false);
			 * cell.setCellStyle(catalogCellStyle); }
			 */

			if (colVal == null)
				colVal = new String("");

			/*
			 * if(i==6 || i==10 || i==15 || i==16 || i==17){
			 * cs.setDataFormat(df.getFormat("0.00")); cell.setCellStyle(cs); }
			 */

			if (!(i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6)) {
				cell.setCellStyle(unlockedCellStyle);
			}

			if(i == 8 || i == 9){
				unlockedCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
				cell.setCellStyle(unlockedCellStyle);
			}

			if (colVal instanceof String) {
				cell.setCellValue((String) colVal);
			} else if (colVal instanceof java.lang.Boolean) {
				cell.setCellValue(((java.lang.Boolean) colVal).booleanValue());
			} else if (colVal instanceof BigDecimal) {
				cell.setCellValue(((BigDecimal) colVal).doubleValue());
			} else if (colVal instanceof Date) {
				cell.setCellValue((Date) colVal);
			} else if (colVal instanceof Long) {
				cell.setCellValue(((Long) colVal).doubleValue());
			} else if (colVal instanceof Integer) {
				cell.setCellValue(((Integer) colVal).intValue());
			}

		}

	}

	/**
	 * This method is used to upload the Image .
	 * 
	 * @param event
	 *            (FileUploadEvent event(mandatory))
	 * @throws IOException 
	 */
	public void imageUpload(FileUploadEvent event) throws IOException  {

		// String tempDir = System.getenv("windir");

		String tempDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
		log.info("tempDir = "+tempDir);

		StringBuilder uploadDir = new StringBuilder();
		//		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.UPLOAD_URL)).append("ngep/cm");
		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append("/CM");
		log.info("uploadDir***"+uploadDir);

		UploadedFile item = event.getFile();
		System.out.println("image size******"+item.getSize());
		CmImage catImage = new CmImage();

		if(item.getSize() > 61440){

			//BufferedImage originalImage = ImageIO.read(new File("c:\\image\\mkyong.jpg"));

			BufferedImage originalImage = ImageIO.read(item.getInputstream());
			System.out.println("originalImage.getType():"+originalImage.getType());
			//int type =  BufferedImage.TYPE_INT_BGR;
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_USHORT_565_RGB : originalImage.getType();

			if(originalImage.getWidth()-originalImage.getHeight()>800){
				catImage.setImage(item.getContents());
			}else{

				BufferedImage resizeImageJpg = resizeImage(originalImage, type);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write( resizeImageJpg, "jpg", baos );
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				baos.close();

				//ImageIO.write(resizeImageJpg, "jpg", new File("c:\\image\\mkyong_jpg.jpg")); 
				System.out.println("image Byte*****"+imageInByte.length);
				catImage.setImage(imageInByte);
			}
		}else{
			catImage.setImage(item.getContents());
		}
		System.out.println("************"+item.getFileName().replace(' ', '_'));

		catImage.setImageName(item.getFileName().replace(' ', '_'));
		catImage.setImageDesc(item.getFileName().replace(' ', '_'));
		catImage.setImageType("jpg");
		catImage.setImagePath(item.getFileName().replace(' ', '_'));
		catImage.setTempPath(uploadDir.toString());
		log.info("catImage.TempPath()" + catImage.getTempPath());

		// render for display
		this.getManagedBean().getSelectedMaintainItem().getCmImages().add(catImage);
		// getCatalogImages().add(this.getManagedBean().getImageObj());
		genImage(catImage);
		log.info("size:"+ this.getManagedBean().getSelectedMaintainItem().getCmImages().size());

	}



	private static BufferedImage resizeImage(BufferedImage originalImage, int type){

		int IMG_HEIGHT = 0;
		int IMG_WIDTH = 0;

		IMG_HEIGHT = originalImage.getHeight();
		IMG_WIDTH = originalImage.getWidth();



		System.out.println("IMG_WIDTH:"+IMG_WIDTH);
		System.out.println("IMG_HEIGHT:"+IMG_HEIGHT);


		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
	}




	/**
	 * Gen image.
	 * 
	 * @param imageObj
	 *            (CmImage imageObj(mandatory))
	 */
	public void genImage(CmImage imageObj) {

		try {
			log.info("genImage getEpNo = "+this.getManagedBean().getSelectedMaintainItem().getSmSupplier().getEpNo());
			String check = imageObj.getTempPath() + "/" + this.getManagedBean().getSelectedMaintainItem().getSmSupplier().getEpNo();
			File dir = new File(check);

			log.info(" dir.exists()"+ dir.exists());

			if(dir.exists()){}
			else{dir.mkdirs();}

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(check + "/" + imageObj.getImageName()));
			bufferedOutputStream.write(imageObj.getImage());

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}

		} catch (Exception e) {
			log.error(e);
		}

	}

	/*
	 * public void genMultipleImage(List<CmImage> imageList){ try { String
	 * tempDir = System.getProperty("java.io.tmpdir");
	 * 
	 * for(int i=0; i<imageList.size(); i++){ CmImage image =
	 * (CmImage)imageList.get(i);
	 * 
	 * 
	 * 
	 * //String filename =
	 * catalog.getCatProduct().getProductCode().concat("_").concat
	 * (catalog.getCatProduct
	 * ().getProductSeq().toString()).concat("_").concat(catalog
	 * .getCatalogSeq().toString()).concat(".jpg"); String filename =
	 * image.getImageDesc().concat("_").concat(Integer.toString(i));
	 * 
	 * File CmImage = new File(tempDir + filename);
	 * 
	 * image.setImagePath(filename);
	 * 
	 * //if(!catImage.exists()) { BufferedOutputStream bufferedOutputStream =new
	 * BufferedOutputStream(new FileOutputStream(tempDir + filename));
	 * //bufferedOutputStream.write(image.getImage());
	 * 
	 * if (bufferedOutputStream != null) { bufferedOutputStream.flush();
	 * bufferedOutputStream.close(); }
	 * 
	 * 
	 * }
	 * 
	 * //} }catch(Exception e) { log.error(e); }
	 * 
	 * 
	 * }
	 */

	/**
	 * To Save catalog item.
	 * 
	 * @return string(editCatalogItem or catalogMaintenanceFile)
	 */
	public String saveCatalogItem() {
		log.info("In saveCatalogItem");
		PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		log.info("******getTabActiveIndex:" + this.getManagedBean().getTabActiveIndex());
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		try {

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();


			//1 year period between start and end date
			if(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate()!=null && 
					this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate()!=null){
				Calendar cal = Calendar.getInstance();  
				cal.setTime(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate());
				cal.add(Calendar.YEAR, 1);

				if(cal.getTime().compareTo(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate())<0){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715012,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			//Zonal Pricing
			for (CmZonalStandardPricing promotion : this.getManagedBean().getSelectedMaintainItem().getzStdPrices()) {
				if(promotion.getCmSupplierZone().getSuppZoneId()==0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Promotion Pricing
			for (CmZonalPromotionPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()) {
				if(promotion.getCmSupplierZone().getSuppZoneId()==0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Tier Pricing
			for (CmZonalTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()) {
				if(promotion.getCmSupplierZone().getSuppZoneId()==0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Tier Promotion Pricing
			for (CmZonalPromotionTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()) {
				if(promotion.getCmSupplierZone().getSuppZoneId()==0){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			/*for (CmImage image : this.getManagedBean().getSelectedMaintainItem().getCmImages()) {
				if (image.isBrandNew()) {
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					break;
				}
			}
			log.debug("compareDesc:"+ this.getManagedBean().getSelectedMaintainItem().getCompareDesc());
			log.debug("getItemDesc:"+ this.getManagedBean().getSelectedMaintainItem().getItemDesc());

			if (this.getManagedBean().getSelectedMaintainItem().getCompareDesc() != null
					&& this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length() > 0) {
				if (!(this.getManagedBean().getSelectedMaintainItem().getCompareDesc().equalsIgnoreCase(this
						.getManagedBean().getSelectedMaintainItem().getItemDesc()))) {
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					// this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			} else {
				if (this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
						&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
					// this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
				}
			}
			log.debug("isReviewRequired:"+ this.getManagedBean().getSelectedMaintainItem().isReviewRequired());*/






			System.out.println("getCompareDesc:"+this.getManagedBean().getSelectedMaintainItem().getCompareDesc());
			System.out.println("getItemDesc:"+this.getManagedBean().getSelectedMaintainItem().getItemDesc());
			System.out.println("begining isReviewRequired:"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());
			if (this.getManagedBean().getSelectedMaintainItem().getCompareDesc() != null
					&& this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length() > 0
					&& this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
					&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
				if (!(this.getManagedBean().getSelectedMaintainItem()
						.getCompareDesc().equalsIgnoreCase(this
								.getManagedBean().getSelectedMaintainItem()
								.getItemDesc()))) {
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

				}
			} else {
				if (this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
						&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
					System.out.println("*************set false***************");
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

				}else{
					////// reset review required = false
					System.out.println("*************set false***************");
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);
				}
			}

			////if review required = false, continue check on image
			System.out.println("2nd check review"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());
			if(!this.getManagedBean().getSelectedMaintainItem().isReviewRequired()){


				if(this.getManagedBean().getSelectedMaintainItem().getCmImages()!=null && this.getManagedBean().getSelectedMaintainItem().getCmImages().size()>0){
					for (CmImage image : this.getManagedBean().getSelectedMaintainItem().getCmImages()) {
						if (image.isBrandNew()) {
							this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

							break;
						}
					}
				}else{
					this.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);

				}
			}




			System.out.println("last check review"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());



			for (CmPromotionTierPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}
			System.out
			.println("this.getManagedBean().getSelectedMaintainItem().getCmTierPricings():"
					+ this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().size());
			for (CmTierPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}

			log.info("getzStdPrices:"
					+ this.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices().size());
			for (CmZonalStandardPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem().getzStdPrices()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}

			for (CmZonalPromotionPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalPromotionPricings()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}

			for (CmZonalTierPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}

			for (CmZonalPromotionTierPricing promotion : this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmZonalPromotionTierPricings()) {
				promotion.setCmSupplierItem(this.getManagedBean()
						.getSelectedMaintainItem());
			}

			if (this.getManagedBean().getSelectedMaintainItem().getItemStatus().equalsIgnoreCase("U")) {
				if (!this.getManagedBean().getSelectedMaintainItem().isRecordStatus()) {
					this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(0);
				}
			} else {
				this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(1);
			}

			this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");

			log.info("**********final item status************"
					+ this.getManagedBean().getSelectedMaintainItem()
					.getItemStatus());

			reqDTO.getSaveSupplierItems().add(
					this.getManagedBean().getSelectedMaintainItem());

			log.info("Tracking diary for catalog saved items");
			reqDTO.setTrackingStatus(true);
			reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());




			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();

			statusReqDTO.setStatusId(CmConst.DRAFT);

			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
			PmUser pmUser = new FacesScopes().getUserSessionBean();

			String actionDesc = "Item "+(this.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName())+" telah dicipta oleh " + (pmUser.getUserName() != null ? pmUser.getUserName():"SYS");
			reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(this.getManagedBean().getSelectedMaintainItem(),actionDesc , statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));

			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710008,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();


			supplierReqDTO.setUserId(pmUser.getUserId());

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
					.get(0).getSupplierId());

			if (this.getManagedBean().getTabActiveIndex() == 0) {
				this.getManagedBean().getDraftSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 4) {

				this.getManagedBean().getPublishSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 1) {

				this.getManagedBean().getApprovalSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 2) {

				this.getManagedBean().getApprovedSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 3) {

				this.getManagedBean().getRejectSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 5) {

				this.getManagedBean().getExpiredSearchDataModel().reloadData();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			//			return "editCatalogItem";
			return null;
		} finally {
			catalogueSO = null;
		}

		log.info("Exit saveCatalogItem");

		return "catalogMaintenanceFile";
		//return "preRenderCatalogueMaintenanceFile";
	}

	/**
	 * This method is used to review or publish the catalog items.
	 * 
	 * @return String(editCatalogItem or catalogMaintenanceFile)
	 */
	public String singleSubmitToReviewItems() {
		log.info("submit catalogue");
		PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

		log.info("******getTabActiveIndex:"+ this.getManagedBean().getTabActiveIndex());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm",ISupplierSearchSO.class);

		this.getManagedBean().setRefreshDraftSuppItemFlag(false);
		try {
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			boolean inactiveFlag = false;

			if (!this.getManagedBean().getSelectedMaintainItem().getItemStatus().equalsIgnoreCase("D") && !this.getManagedBean().getSelectedMaintainItem().getItemStatus().equalsIgnoreCase("R")) {
				if (!this.getManagedBean().getSelectedMaintainItem().isRecordStatus()) {
					this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(0);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");
					inactiveFlag = true;
				}
			} else {
				this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(1);
			}

			if (!inactiveFlag) {

				System.out.println("getCompareDesc:"+this.getManagedBean().getSelectedMaintainItem().getCompareDesc());
				System.out.println("getItemDesc:"+this.getManagedBean().getSelectedMaintainItem().getItemDesc());
				System.out.println("begining isReviewRequired:"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());
				if (this.getManagedBean().getSelectedMaintainItem().getCompareDesc() != null
						&& this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length() > 0
						&& this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
						&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
					if (!(this.getManagedBean().getSelectedMaintainItem()
							.getCompareDesc().equalsIgnoreCase(this
									.getManagedBean().getSelectedMaintainItem()
									.getItemDesc()))) {
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

					}
				} else {
					if (this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
							&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
						System.out.println("*************set false***************");
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

					}else{
						////// reset review required = false
						System.out.println("*************set false***************");
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);
					}
				}

				////if review required = false, continue check on image
				System.out.println("2nd check review"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());
				if(!this.getManagedBean().getSelectedMaintainItem().isReviewRequired()){


					if(this.getManagedBean().getSelectedMaintainItem().getCmImages()!=null && this.getManagedBean().getSelectedMaintainItem().getCmImages().size()>0){
						for (CmImage image : this.getManagedBean().getSelectedMaintainItem().getCmImages()) {
							if (image.isBrandNew()) {
								this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);

								break;
							}
						}
					}else{
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);

					}
				}

			}


			System.out.println("last check review"+this.getManagedBean().getSelectedMaintainItem().isReviewRequired());
			log.info("*********getItemStatus*********"+ this.getManagedBean().getSelectedMaintainItem().getItemStatus());

			//----------mandatory checking-------------------------------------------------------------------------------------------//

			if (this.getManagedBean().getSelectedMaintainItem().getPmUom().getUomName()==null || 
					this.getManagedBean().getSelectedMaintainItem().getPmUom().getUomName().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715007,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//Standard Pricing
			CmStandardPricing cmStandardPricing = this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing();
			if(cmStandardPricing.getPrice()==null || cmStandardPricing.getStartDate()==null || cmStandardPricing.getEndDate()==null){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715008,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//Tier Pricing
			for (CmTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()) {
				if(promotion.getPrice()==null || promotion.getFromQty()==null || promotion.getToQty()==null || 
						promotion.getStartDate()==null || promotion.getEndDate()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				} else if(!promotion.isCheckOk()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715030, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			boolean disable = false;
			int lastStdGrpId = 0;
			if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().size() > 0){
				lastStdGrpId = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().size() - 1).getStand_group_id();
			}

			if(lastStdGrpId > 0){
				for(int iter = 1; iter <= lastStdGrpId; iter++){
					int countGroup = countTierPricesByStandGroupId(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), iter);
					if(countGroup < 2 && countGroup>0){
						disable = true;
						break;
					}
				}
			}

			if(disable){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715029,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//Tier Promotion Pricing
			for (CmPromotionTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()) {
				if(promotion.getPrice()==null || promotion.getFromQty()==null || promotion.getToQty()==null || 
						promotion.getStartDate()==null || promotion.getEndDate()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				} else if(!promotion.isCheckOk()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715030, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Pricing
			for (CmZonalStandardPricing promotion : this.getManagedBean().getSelectedMaintainItem().getzStdPrices()) {
				if(promotion.getPrice()==null || promotion.getCmSupplierZone().getSuppZoneId()==0 || 
						promotion.getStartDate()==null || promotion.getEndDate()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Promotion Pricing
			for (CmZonalPromotionPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()) {
				if(promotion.getPrice()==null || promotion.getCmSupplierZone().getSuppZoneId()==0 || 
						promotion.getStartDate()==null || promotion.getEndDate()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Tier Pricing
			for (CmZonalTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()) {
				if(promotion.getPrice()==null || promotion.getCmSupplierZone().getSuppZoneId()==0 || 
						promotion.getStartDate()==null || promotion.getEndDate()==null ||
						promotion.getFromQty()==null || promotion.getToQty()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				} else if(!promotion.isCheckOk()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715030, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			disable = false;
			lastStdGrpId = 0;
			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().size() > 0){
				lastStdGrpId = this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().size() - 1).getStand_group_id();
			}

			if(lastStdGrpId > 0){
				for(int iter = 1; iter <= lastStdGrpId; iter++){
					int countGroup = countTierPricesByStandGroupId(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), iter);
					if(countGroup < 2 && countGroup>0){
						disable = true;
						break;
					}
				}
			}
			if(disable){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715029,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//Zonal Tier Promotion Pricing
			for (CmZonalPromotionTierPricing promotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()) {
				if(promotion.getPrice()==null || promotion.getCmSupplierZone().getSuppZoneId()==0 || 
						promotion.getStartDate()==null || promotion.getEndDate()==null ||
						promotion.getFromQty()==null || promotion.getToQty()==null){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				} else if(!promotion.isCheckOk()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715030, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.zonal.tier",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			//----------mandatory checking end----------------------------------------------------------------------------------------//

			//----------logic checking-----------------------------------------------------------------------------------------------//

			if(cmStandardPricing.getPrice().longValue()<=-1 || cmStandardPricing.getPrice().floatValue()==0.0f){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715017,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			if(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate()
					.after(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate())){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715011,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//end date already passed
			if(cmStandardPricing.getEndDate().before(new Date())){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715021,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//1 year period between start and end date
			Calendar cal = Calendar.getInstance();  
			cal.setTime(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getStartDate());
			cal.add(Calendar.YEAR, CmHelper.getInstance().getStandardPricingYear());

			if(cal.getTime().compareTo(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getEndDate())<0){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715012,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//validate promotion's mandatory case if one field is inserted
			boolean promotionError = false;
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getPrice()==null){
				if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate()==null){
					if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate()!=null)
						promotionError = true;
				}else{
					promotionError = true;
				}
			}else{
				if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate()==null)
					promotionError = true;
				else{
					if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate()==null)
						promotionError = true;
				}
			}

			if(promotionError){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715009, 
								new String[]{ResourceBundleUtils.getMessage("lbl.cm.promotion",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			//promotion price is more than standard price
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getPrice()!=null){
				if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getPrice().doubleValue() >= this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing().getPrice().doubleValue()){
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715031, 
									new String[]{ResourceBundleUtils.getMessage("lbl.cm.promotion",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			//---------------------------------- start comparison each date with standard date range ----------------------------------------------------

			//Promotion
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate()!=null && this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate()!=null){




				if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate().after(cmStandardPricing.getEndDate())||
						this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate().after(cmStandardPricing.getEndDate())||
						this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate().before(cmStandardPricing.getStartDate())||
						this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate().before(cmStandardPricing.getStartDate())){
					log.info("---------------------------------------------------- Promotion ERROR_715020");
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715018,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}


				///promotion end date cannot less than standard start date

				if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getStartDate()
						.after(this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing().getEndDate())){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715011,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}





			//Tier Pricing
			int groupPrevious = 0;
			Collections.sort(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), new Comparator<CmTierPricing>(){
				@Override public int compare(CmTierPricing p1, CmTierPricing p2) {
					/*if(p1.getPricingId()>0 && p2.getPricingId()>0)
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					else
						return 0;*/
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
				}

				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			}
					);

			for (CmTierPricing tierPricing : this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()) {

				if(tierPricing.getStand_group_id()==null || tierPricing.getStand_group_id()!=groupPrevious)
					if(tierPricing.getEndDate().after(cmStandardPricing.getEndDate())||
							tierPricing.getStartDate().after(cmStandardPricing.getEndDate())||
							tierPricing.getEndDate().before(cmStandardPricing.getStartDate())||
							tierPricing.getStartDate().before(cmStandardPricing.getStartDate())){
						log.info("---------------------------------------------------- Tier ERROR_715020");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}else if(tierPricing.getEndDate().before(new Date())){
						log.info("---------------------------------------------------- Tier ERROR_715021");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715021,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}
				groupPrevious = tierPricing.getStand_group_id();
			}
			//Tier Promotion Pricing
			groupPrevious = 0;
			Collections.sort(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), new Comparator<CmPromotionTierPricing>(){
				@Override public int compare(CmPromotionTierPricing p1, CmPromotionTierPricing p2) {
					/*if(p1.getPricingId()>0 && p2.getPricingId()>0)
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					else
						return 0;*/
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
				}

				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			}
					);

			for (CmPromotionTierPricing tierPromotionPricing : this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()) {

				if(tierPromotionPricing.getStand_group_id()==null || tierPromotionPricing.getStand_group_id()!=groupPrevious)
					if(tierPromotionPricing.getEndDate().after(cmStandardPricing.getEndDate())||
							tierPromotionPricing.getStartDate().after(cmStandardPricing.getEndDate())||
							tierPromotionPricing.getEndDate().before(cmStandardPricing.getStartDate())||
							tierPromotionPricing.getStartDate().before(cmStandardPricing.getStartDate())){
						log.info("---------------------------------------------------- Tier Promotion ERROR_715020");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}else if(tierPromotionPricing.getEndDate().before(new Date())){
						log.info("---------------------------------------------------- Tier Promotion ERROR_715021");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715021,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}
				groupPrevious = tierPromotionPricing.getStand_group_id();
			}
			//Zonal Pricing
			for (CmZonalStandardPricing zStdPrice : this.getManagedBean().getSelectedMaintainItem().getzStdPrices()) {
				if(zStdPrice.getEndDate().after(cmStandardPricing.getEndDate())||
						zStdPrice.getStartDate().after(cmStandardPricing.getEndDate())||
						zStdPrice.getEndDate().before(cmStandardPricing.getStartDate())||
						zStdPrice.getStartDate().before(cmStandardPricing.getStartDate())){
					log.info("---------------------------------------------------- Zonal ERROR_715020");
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715020,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}else if(zStdPrice.getEndDate().before(new Date())){
					log.info("---------------------------------------------------- Zonal ERROR_715021");
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715021,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Promotion Pricing
			for (CmZonalPromotionPricing zonalPromotion : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()) {
				if(zonalPromotion.getEndDate().after(cmStandardPricing.getEndDate())||
						zonalPromotion.getStartDate().after(cmStandardPricing.getEndDate())||
						zonalPromotion.getEndDate().before(cmStandardPricing.getStartDate())||
						zonalPromotion.getStartDate().before(cmStandardPricing.getStartDate())){
					log.info("---------------------------------------------------- Zonal Promotion ERROR_715020");
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715020,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}else if(zonalPromotion.getEndDate().before(new Date())){
					log.info("---------------------------------------------------- Zonal Promotion ERROR_715021");
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715021,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}
			//Zonal Tier Pricing
			groupPrevious = 0;
			Collections.sort(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), new Comparator<CmZonalTierPricing>(){
				@Override public int compare(CmZonalTierPricing p1, CmZonalTierPricing p2) {
					/*if(p1.getPricingId()>0 && p2.getPricingId()>0)
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					else
						return 0;*/
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
				}

				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			}
					);

			for (CmZonalTierPricing zonalTier : this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()) {
				log.info("zonalTier.getPricingId() = "+zonalTier.getPricingId());

				if(zonalTier.getStand_group_id()==null || zonalTier.getStand_group_id()!=groupPrevious)
					if(zonalTier.getEndDate().after(cmStandardPricing.getEndDate())||
							zonalTier.getStartDate().after(cmStandardPricing.getEndDate())||
							zonalTier.getEndDate().before(cmStandardPricing.getStartDate())||
							zonalTier.getStartDate().before(cmStandardPricing.getStartDate())){

						log.info("---------------------------------------------------- Zonal Tier ERROR_715020");
						/*log.info("zonalTier.getEndDate().after(cmStandardPricing.getEndDate() "+zonalTier.getEndDate().after(cmStandardPricing.getEndDate()));
						log.info("zonalTier.getStartDate().after(cmStandardPricing.getEndDate() "+zonalTier.getStartDate().after(cmStandardPricing.getEndDate()));
						log.info("zonalTier.getEndDate().before(cmStandardPricing.getStartDate() "+zonalTier.getEndDate().before(cmStandardPricing.getStartDate()));
						log.info("zonalTier.getStartDate().before(cmStandardPricing.getStartDate() "+zonalTier.getStartDate().before(cmStandardPricing.getStartDate()));*/

						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}else if(zonalTier.getEndDate().before(new Date())){
						log.info("---------------------------------------------------- Zonal Tier ERROR_715021");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715021,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}
				groupPrevious = zonalTier.getStand_group_id();
			}
			//Zonal Tier Promotion Pricing
			groupPrevious = 0;
			Collections.sort(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), new Comparator<CmZonalPromotionTierPricing>(){
				@Override public int compare(CmZonalPromotionTierPricing p1, CmZonalPromotionTierPricing p2) {
					/*if(p1.getPricingId()>0 && p2.getPricingId()>0)
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					else
						return 0;*/
					if(p1.getStand_group_id()<p2.getStand_group_id())
						return -1;
					else if(p1.getStand_group_id()==p2.getStand_group_id())
						return doSecodaryOrderSort(p1, p2);
					else
						return 1;
				}

				public int doSecodaryOrderSort(CmPricing p1,CmPricing p2) {
					return p1.getFromQty()<p2.getFromQty()?-1:0;
				}
			}
					);

			for (CmZonalPromotionTierPricing zonalPromotionTier : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()) {

				if(zonalPromotionTier.getStand_group_id()==null || zonalPromotionTier.getStand_group_id()!=groupPrevious)
					if(zonalPromotionTier.getEndDate().after(cmStandardPricing.getEndDate())||
							zonalPromotionTier.getStartDate().after(cmStandardPricing.getEndDate())||
							zonalPromotionTier.getEndDate().before(cmStandardPricing.getStartDate())||
							zonalPromotionTier.getStartDate().before(cmStandardPricing.getStartDate())){
						log.info("---------------------------------------------------- Zonal tier Promotion ERROR_715020");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}else if(zonalPromotionTier.getEndDate().before(new Date())){
						log.info("---------------------------------------------------- Zonal Tier Promotion ERROR_715021");
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715021,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "editCatalogItem";
					}
				groupPrevious = zonalPromotionTier.getStand_group_id();
			}

			//---------------------------------- end comparison each date with standard date range ----------------------------------------------------

			//same item with existed one cannot overlap the date
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
			supplierReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			supplierReqDTO.setAllStatuses(true);
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);

			reqDTO.setSupplierItemId(this.getManagedBean().getSelectedMaintainItem().getSupplierItemId());
			reqDTO.setUomId(this.getManagedBean().getSelectedMaintainItem().getPmUom().getUomId());
			reqDTO.setExtensionCode(this.getManagedBean().getSelectedMaintainItem().getCmItem().getExtensionCode());
			reqDTO.setStartDate1(cmStandardPricing.getStartDate());
			reqDTO.setEndDate1(cmStandardPricing.getEndDate());
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());
			if(catalogueSO.checkExistedItem(reqDTO)){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(MessageConst.ERROR_715019,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				return "editCatalogItem";
			}

			Map<Integer, List<CmTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());
			if (!tierMap.isEmpty() && tierMap.size() > 0) {
				Iterator itMap = tierMap.entrySet().iterator();
				while (itMap.hasNext()) {
					Map.Entry entry = (Map.Entry) itMap.next();
					List<CmTierPricing> dataList = (List) entry.getValue();
					CmTierPricing previousObj = null;
					for (int i = 0; i < dataList.size(); i++) {
						CmTierPricing tier = dataList.get(i);
						if (previousObj != null) {
							if (previousObj.getToQty() >= tier.getFromQty()) {
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(null, new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"", MessageUtils.getMessage(MessageConst.ERROR_715035,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
								this.getManagedBean().setPricingTabIndex(1);
								this.getManagedBean().setTierPricingIndex(0);
								return "editCatalogItem";
							}
						}
						if (tier.getFromQty() >= tier.getToQty()) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715036,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(tierMap);
				List<CmTierPricing> perviouslyList = new ArrayList<CmTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmTierPricing> sortedTier = (List<CmTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) >= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715037,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmTierPricing tier = sortedTier.get(i);

						if (tier.getStartDate().compareTo(tier.getEndDate()) > 0) {

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715038,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			Map<Integer, List<CmPromotionTierPricing>> promotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings());
			if (!promotionTierMap.isEmpty() && promotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(promotionTierMap);
				List<CmPromotionTierPricing> perviouslyList = new ArrayList<CmPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmPromotionTierPricing> sortedTier = (List<CmPromotionTierPricing>) sorted;

					if (perviouslyList != null && perviouslyList.size() > 0) {

						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) >= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715039,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(1);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmPromotionTierPricing tier = sortedTier.get(i);
						CmPricing parentTier = tier.getParentPrice();

						if (parentTier.getPrice().compareTo(tier.getPrice()) <= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715040,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(1);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			List<CmZonalPromotionPricing> zPromotionPricing = sortByStartDate(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings());
			CmZonalPromotionPricing previousZPromotionObj = null;
			for (int i = 0; i < zPromotionPricing.size(); i++) {
				CmZonalPromotionPricing standard = zPromotionPricing.get(i);
				if (previousZPromotionObj != null && standard!=null) {
					if ((previousZPromotionObj.getCmSupplierZone() != null && standard.getCmSupplierZone() != null) && (previousZPromotionObj.getCmSupplierZone().getSuppZoneId() == standard.getCmSupplierZone().getSuppZoneId() || compareZonesForSameDistricts(previousZPromotionObj.getCmSupplierZone(), standard.getCmSupplierZone())) && previousZPromotionObj.getEndDate().compareTo(standard.getStartDate()) >= 0) {
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"", MessageUtils.getMessage(MessageConst.ERROR_715041,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
						this.getManagedBean().setPricingTabIndex(2);
						this.getManagedBean().setZonalPricingIndex(1);
						return "editCatalogItem";
					}
				}
				if (standard!=null && standard.getStartDate().compareTo(standard.getEndDate()) > 0) {
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"",
									MessageUtils.getMessage(MessageConst.ERROR_715042,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					this.getManagedBean().setPricingTabIndex(2);
					this.getManagedBean().setZonalPricingIndex(1);
					return "editCatalogItem";
				}
				previousZPromotionObj = standard;
			}

			Map<Integer, List<CmZonalTierPricing>> zTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings());
			if (!zTierMap.isEmpty() && zTierMap.size() > 0) {
				Iterator itMap = zTierMap.entrySet().iterator();
				while (itMap.hasNext()) {
					Map.Entry entry = (Map.Entry) itMap.next();
					List<CmZonalTierPricing> dataList = (List) entry.getValue();
					CmZonalTierPricing previousObj = null;
					for (int i = 0; i < dataList.size(); i++) {
						CmZonalTierPricing tier = dataList.get(i);
						if (previousObj != null && tier!=null) {
							if (previousObj.getToQty() >= tier.getFromQty()) {
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_ERROR,
												"",
												MessageUtils.getMessage(MessageConst.ERROR_715043,
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes().getUserLocale())));
								this.getManagedBean().setPricingTabIndex(3);
								this.getManagedBean().setZonalTierPricingIndex(0);
								return "editCatalogItem";
							}
						}
						if (previousObj != null && tier!=null) {
							if (tier.getFromQty() >= tier.getToQty()) {
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_ERROR,
												"",
												MessageUtils.getMessage(MessageConst.ERROR_715044,
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes().getUserLocale())));
								this.getManagedBean().setPricingTabIndex(3);
								this.getManagedBean().setZonalTierPricingIndex(0);
								return "editCatalogItem";
							}
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(zTierMap);
				List<CmZonalTierPricing> perviouslyList = new ArrayList<CmZonalTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalTierPricing> sortedTier = (List<CmZonalTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if ((perviouslyList.get(0).getCmSupplierZone().getSuppZoneId() == sortedTier.get(0).getCmSupplierZone().getSuppZoneId() || compareZonesForSameDistricts(perviouslyList.get(0).getCmSupplierZone(), sortedTier.get(0).getCmSupplierZone())) && perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) >= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715045,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(3);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmZonalTierPricing tier = sortedTier.get(i);
						if (tier.getStartDate().compareTo(tier.getEndDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715046,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(3);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			Map<Integer, List<CmZonalPromotionTierPricing>> zPromotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings());
			if (!zPromotionTierMap.isEmpty() && zPromotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(zPromotionTierMap);
				List<CmZonalPromotionTierPricing> perviouslyList = new ArrayList<CmZonalPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalPromotionTierPricing> sortedTier = (List<CmZonalPromotionTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if ((perviouslyList.get(0).getCmSupplierZone().getSuppZoneId() == sortedTier.get(0).getCmSupplierZone().getSuppZoneId() || compareZonesForSameDistricts(perviouslyList.get(0).getCmSupplierZone(), sortedTier.get(0).getCmSupplierZone())) && perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) >= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715047,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmZonalPromotionTierPricing tier = sortedTier.get(i);
						CmPricing parentTier = tier.getParentPrice();
						if (tier.getStartDate().compareTo(tier.getEndDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715048,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						} else if (parentTier.getPrice().compareTo(tier.getPrice()) <= 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"",
											MessageUtils.getMessage(MessageConst.ERROR_715049,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			//check overlap zone with same district
			for(int i=0;i<this.getManagedBean().getSelectedMaintainItem().getzStdPrices().size();i++){
				this.getManagedBean().setError8(false);
				this.getManagedBean().setError4(false);
				validateZonalStandardDateOverlap(i);
				if(this.getManagedBean().isError4()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715022,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}else if(this.getManagedBean().isError8()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715024,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}

			//check overlap zone tier with same district
			/*for(int i=0;i<this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().size();i++){
				this.getManagedBean().setError8(false);
				this.getManagedBean().setError4(false);
				validateZonalTierDateOverlap(i);
				if(this.getManagedBean().isError4()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715023,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}else if(this.getManagedBean().isError8()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.ERROR_715025,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
					return "editCatalogItem";
				}
			}*/


			//----------logic checking end-------------------------------------------------------------------------------------------//

			if (!inactiveFlag) {
				log.info("-------------------------------------------------------------------------------------------here");
				log.info("isReviewRequired:"
						+ this.getManagedBean().getSelectedMaintainItem()
						.isReviewRequired());

				if (this.getManagedBean().getSelectedMaintainItem()
						.isReviewRequired()) {
					this.getManagedBean().getSelectedMaintainItem()
					.setItemStatus("P");
				} else {
					this.getManagedBean().getSelectedMaintainItem()
					.setItemStatus("U");
				}
			}

			//Past Start Date should change to current date
			Date dateNow = new Date();
			if(cmStandardPricing.getStartDate().before(dateNow)){
				cmStandardPricing.setStartDate(dateNow);
			}

			//record status = active
			this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(1);

			reqDTO.getSaveSupplierItems().add(this.getManagedBean().getSelectedMaintainItem());			
			log.info("Tracking diary for to publish the catalog items");			
			reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());
			reqDTO.setTrackingStatus(true);

			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();

			if(this.getManagedBean().getSelectedMaintainItem().isReviewRequired())
			{
				statusReqDTO.setStatusId(CmConst.PENDING);

				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				String actionDesc ="Item "+this.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah dihantar kepada Pentadbir Katalog untuk kelulusan";
				reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(this.getManagedBean().getSelectedMaintainItem(),actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));
			}
			else{
				statusReqDTO.setStatusId(CmConst.DIRECT_PUBLISHED);;
				StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
				String actionDesc ="Item "+this.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah diiklankan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
				reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(this.getManagedBean().getSelectedMaintainItem(),actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));
			}

			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();

			if(this.getManagedBean().getSelectedMaintainItem().getItemStatus().equals("P")){
				facesContext.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.ERROR_715059,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}else{
				facesContext.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_710001,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

			supplierReqDTO = new SupplierSearchReqDTO();


			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);
			smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());

			if (this.getManagedBean().getTabActiveIndex() == 0) {

				this.getManagedBean().getDraftSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 4) {

				this.getManagedBean().getPublishSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 1) {

				this.getManagedBean().getApprovalSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 2) {

				this.getManagedBean().getApprovedSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 3) {

				this.getManagedBean().getRejectSearchDataModel().reloadData();

			} else if (this.getManagedBean().getTabActiveIndex() == 5) {

				this.getManagedBean().getExpiredSearchDataModel().reloadData();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("***********error********");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return "editCatalogItem";
		} finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			log.info("******IS Supplier Temp*******");
			if(!new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
				log.info("********SUPPLIER_TEMP ONLY******");
				return supplierTempUploadedMsg();
			}
		}

		log.info("Exit saveCatalogItem");

		return "catalogMaintenanceFile";
		//return "preRenderCatalogueMaintenanceFile";
	}

	/**
	 * This method is used to publish the items.
	 * 
	 * @return String(catalogMaintenanceFile)
	 * @throws Exception
	 *             the exception
	 */
	public String submitToReviewItems() throws Exception {
		log.debug("submitToReviewItems");

		log.info("this.getManagedBean().getCloneCmSupplierItemLs(): " + this.getManagedBean().getCloneCmSupplierItemLs().size());
		if(this.getManagedBean().getCloneCmSupplierItemLs().size() < 1) {
			throw new EpException(MessageConst.ERROR_715013);
		}
		this.getManagedBean().setRefreshDraftSuppItemFlag(false);

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = new CatalogueResDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		try {
			this.getManagedBean().setTabActiveIndex(0);
			reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());


			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);


			for (int i = 0; i < this.getManagedBean().getCloneCmSupplierItemLs().size(); i++) {					
				reqDTO.setSupplierItemId(this.getManagedBean().getCloneCmSupplierItemLs().get(i).getSupplierItemId());	


				resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);

				//				CmSupplierItem catalog = this.getManagedBean().getSelectedMaintainItems()[i];
				CmSupplierItem catalog = resDTO.getSupplierItem();
				log.info("catalog.getPmUom().getUomName():" + catalog.getPmUom().getUomName());

				catalog.getCmStandardPricing().setPrice(this.getManagedBean().getCloneCmSupplierItemLs().get(i).getCmStandardPricing().getPrice());
				catalog.getCmStandardPricing().setStartDate(this.getManagedBean().getCloneCmSupplierItemLs().get(i).getCmStandardPricing().getStartDate());
				catalog.getCmStandardPricing().setEndDate(this.getManagedBean().getCloneCmSupplierItemLs().get(i).getCmStandardPricing().getEndDate());
				catalog.setPmUom(this.getManagedBean().getCloneCmSupplierItemLs().get(i).getPmUom());
				System.out.println("******UOM*******"+this.getManagedBean().getCloneCmSupplierItemLs().get(i).getPmUom().getUomName());
				//mandatory checking
				if (catalog.getCmStandardPricing().getPrice() == null
						|| catalog.getCmStandardPricing().getStartDate() == null
						|| catalog.getCmStandardPricing().getEndDate() == null
						|| catalog.getPmUom().getUomName() == null
						|| catalog.getPmUom().getUomName().isEmpty()) {

					log.info("********validation**********");
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"",
							MessageUtils.getMessage(MessageConst.ERROR_715050,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
					// facesContext.getExternalContext().getFlash().setKeepMessages(true);

					return "catalogMaintenanceFile";
				}

				//price 
				if(catalog.getCmStandardPricing().getPrice().longValue()<=-1 || catalog.getCmStandardPricing().getPrice().floatValue()==0.0f){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715017,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//end date before start date
				if(catalog.getCmStandardPricing().getStartDate().after(catalog.getCmStandardPricing().getEndDate())){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715011,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//end date already passed
				if(catalog.getCmStandardPricing().getEndDate().before(new Date())){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715021,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//promotion date is outside normal date
				if(catalog.getCmPromotionPricing().getStartDate()!=null && catalog.getCmPromotionPricing().getEndDate()!=null){
					if(catalog.getCmPromotionPricing().getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
							catalog.getCmPromotionPricing().getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
							catalog.getCmPromotionPricing().getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
							catalog.getCmPromotionPricing().getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715018,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "catalogMaintenanceFile";
					}
				}

				//comparison each date with standard date range

				//Tier Pricing
				int groupPrevious = 0;
				Collections.sort(catalog.getCmTierPricings(), new Comparator<CmTierPricing>(){
					@Override public int compare(CmTierPricing p1, CmTierPricing p2) {
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					}
				}
						);

				for (CmTierPricing tierPricing : catalog.getCmTierPricings()) {

					if(tierPricing.getStand_group_id()==null || tierPricing.getStand_group_id()!=groupPrevious)
						if(tierPricing.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
								tierPricing.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
								tierPricing.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
								tierPricing.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
											MessageUtils.getMessage(MessageConst.ERROR_715020,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							return "catalogMaintenanceFile";
						}
					groupPrevious = tierPricing.getStand_group_id();
				}

				//check at least 2 tier pricing
				boolean disable = false;
				int lastStdGrpId = 0;
				if(catalog.getCmTierPricings() != null && catalog.getCmTierPricings().size() > 0){
					lastStdGrpId = catalog.getCmTierPricings().get(catalog.getCmTierPricings().size() - 1).getStand_group_id();
				}

				if(lastStdGrpId > 0){
					for(int iter = 1; iter <= lastStdGrpId; iter++){
						int countGroup = countTierPricesByStandGroupId(catalog.getCmTierPricings(), iter);
						if(countGroup < 2 && countGroup>0){
							disable = true;
							break;
						}
					}
				}

				if(disable){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715029,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}
				//end check at least 2 tier pricing

				//Tier Promotion Pricing
				groupPrevious = 0;
				Collections.sort(catalog.getCmPromotionTierPricings(), new Comparator<CmPromotionTierPricing>(){
					@Override public int compare(CmPromotionTierPricing p1, CmPromotionTierPricing p2) {
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					}
				}
						);

				for (CmPromotionTierPricing tierPromotionPricing : catalog.getCmPromotionTierPricings()) {

					if(tierPromotionPricing.getStand_group_id()==null || tierPromotionPricing.getStand_group_id()!=groupPrevious)
						if(tierPromotionPricing.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
								tierPromotionPricing.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
								tierPromotionPricing.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
								tierPromotionPricing.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
											MessageUtils.getMessage(MessageConst.ERROR_715020,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							return "catalogMaintenanceFile";
						}
					groupPrevious = tierPromotionPricing.getStand_group_id();
				}
				//Zonal Pricing
				for (CmZonalStandardPricing zStdPrice : catalog.getzStdPrices()) {
					if(zStdPrice.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
							zStdPrice.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
							zStdPrice.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
							zStdPrice.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "catalogMaintenanceFile";
					}
				}
				//Zonal Promotion Pricing
				for (CmZonalPromotionPricing zonalPromotion : catalog.getCmZonalPromotionPricings()) {
					if(zonalPromotion.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
							zonalPromotion.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
							zonalPromotion.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
							zonalPromotion.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
						FacesContext.getCurrentInstance().addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
										MessageUtils.getMessage(MessageConst.ERROR_715020,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
						return "catalogMaintenanceFile";
					}
				}

				//Zonal Tier Pricing
				groupPrevious = 0;
				Collections.sort(catalog.getCmZonalTierPricings(), new Comparator<CmZonalTierPricing>(){
					@Override public int compare(CmZonalTierPricing p1, CmZonalTierPricing p2) {
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					}
				}
						);

				for (CmZonalTierPricing zonalTier : catalog.getCmZonalTierPricings()) {

					if(zonalTier.getStand_group_id()==null || zonalTier.getStand_group_id()!=groupPrevious)
						if(zonalTier.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
								zonalTier.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
								zonalTier.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
								zonalTier.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
											MessageUtils.getMessage(MessageConst.ERROR_715020,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							return "catalogMaintenanceFile";
						}
					groupPrevious = zonalTier.getStand_group_id();
				}

				//check at least 2 zonal tier pricing
				disable = false;
				lastStdGrpId = 0;
				if(catalog.getCmZonalTierPricings() != null && catalog.getCmZonalTierPricings().size() > 0){
					lastStdGrpId = catalog.getCmZonalTierPricings().get(catalog.getCmZonalTierPricings().size() - 1).getStand_group_id();
				}

				if(lastStdGrpId > 0){
					for(int iter = 1; iter <= lastStdGrpId; iter++){
						int countGroup = countTierPricesByStandGroupId(catalog.getCmZonalTierPricings(), iter);
						if(countGroup < 2 && countGroup>0){
							disable = true;
							break;
						}
					}
				}
				if(disable){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715029,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}
				//end check at least 2 zonal tier pricing

				//Zonal Tier Promotion Pricing
				groupPrevious = 0;
				Collections.sort(catalog.getCmZonalPromotionTierPricings(), new Comparator<CmZonalPromotionTierPricing>(){
					@Override public int compare(CmZonalPromotionTierPricing p1, CmZonalPromotionTierPricing p2) {
						return Long.compare(p1.getPricingId(),p2.getPricingId());
					}
				}
						);

				for (CmZonalPromotionTierPricing zonalPromotionTier : catalog.getCmZonalPromotionTierPricings()) {

					if(zonalPromotionTier.getStand_group_id()==null || zonalPromotionTier.getStand_group_id()!=groupPrevious)
						if(zonalPromotionTier.getEndDate().after(catalog.getCmStandardPricing().getEndDate())||
								zonalPromotionTier.getStartDate().after(catalog.getCmStandardPricing().getEndDate())||
								zonalPromotionTier.getEndDate().before(catalog.getCmStandardPricing().getStartDate())||
								zonalPromotionTier.getStartDate().before(catalog.getCmStandardPricing().getStartDate())){
							FacesContext.getCurrentInstance().addMessage(
									null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
											MessageUtils.getMessage(MessageConst.ERROR_715020,
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							return "catalogMaintenanceFile";
						}
					groupPrevious = zonalPromotionTier.getStand_group_id();
				}
				//end date checking

				Calendar c = new GregorianCalendar();
				c.add(Calendar.DATE, -1);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				Date d1 = c.getTime(); 

				if(catalog.getCmStandardPricing().getStartDate().before(d1)) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715051,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//promotion price is more than standard price
				if(catalog.getCmPromotionPricing().getPrice()!=null){
					if(catalog.getCmPromotionPricing().getPrice().compareTo(catalog.getCmStandardPricing().getPrice())>0){
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, null,MessageUtils.getMessage(MessageConst.ERROR_715031, 
										new String[]{ResourceBundleUtils.getMessage("lbl.cm.promotion",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())},
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
						return "catalogMaintenanceFile";
					}
				}

				//1 year period between start and end date
				Calendar cal = Calendar.getInstance();  
				cal.setTime(catalog.getCmStandardPricing().getStartDate());
				cal.add(Calendar.YEAR, 1);

				if(cal.getTime().compareTo(catalog.getCmStandardPricing().getEndDate())<0){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715012,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//same item with existed one cannot overlap the date
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
				supplierReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierItemId(catalog.getSupplierItemId());
				reqDTO.setUomId(catalog.getPmUom().getUomId());
				reqDTO.setExtensionCode(catalog.getCmItem().getExtensionCode());
				reqDTO.setStartDate1(catalog.getCmStandardPricing().getStartDate());
				reqDTO.setEndDate1(catalog.getCmStandardPricing().getEndDate());
				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());
				if(catalogueSO.checkExistedItem(reqDTO)){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715019,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//Past Start Date should change to current date
				Date dateNow = new Date();
				if(catalog.getCmStandardPricing().getStartDate().before(dateNow)){
					catalog.getCmStandardPricing().setStartDate(dateNow);
				}

				StatusReqDTO statusReqDTO = new StatusReqDTO();




				if (catalog.isReviewRequired()) {

					log.info("tracking diary for catalog Items to Pending approval:submitToReviewItems");
					statusReqDTO.setStatusId(CmConst.PENDING);

					StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
					reqDTO.setTrackingStatus(true);
					String actionDesc ="Item "+this.getManagedBean().getCloneCmSupplierItemLs().get(i).getCmItem().getItemName()+" telah dihantar kepada Catalogue Admin untuk kelulusan";
					reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(this.getManagedBean().getCloneCmSupplierItemLs().get(i), actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));

					catalog.setItemStatus("P");
				} else {

					log.info("tracking diary for catalog Items to Publish:submitToReviewItems");
					statusReqDTO.setStatusId(CmConst.DIRECT_PUBLISHED);

					StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);
					reqDTO.setTrackingStatus(true);
					String actionDesc ="Item "+this.getManagedBean().getCloneCmSupplierItemLs().get(i).getCmItem().getItemName()+" telah diiklankan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
					reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(this.getManagedBean().getCloneCmSupplierItemLs().get(i), actionDesc,statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));


					catalog.setItemStatus("U");
				}

				//status active
				catalog.setRecordStatusFlg(1);

				catalog.getCmStandardPricing().setCmSupplierItem(catalog);

				/*
				 * for(CmStandardPricing pricing:
				 * catalog.getCmStandardPricings()){
				 * pricing.setCmSupplierItem(catalog); }
				 */

				reqDTO.getSaveSupplierItems().add(catalog);

			}			
			resDTO = catalogueSO.updateSupplierItem(reqDTO);
			log.debug("resDTO.isSuccessful():" + resDTO.isSuccessful());

			if (resDTO.isSuccessful()) {

				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();


				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				this.getManagedBean().getDraftSearchDataModel().reloadData();
				List<CmSupplierItem> suppLs = new ArrayList<CmSupplierItem>();
				this.getManagedBean().setCloneCmSupplierItemLs(suppLs);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "",
						MessageUtils.getMessage(MessageConst.INFO_710001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

			}

			//		} catch (Exception e) {
			//
			//			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			log.info("******IS Supplier Temp*******");
			if(!new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
				log.info("********SUPPLIER_TEMP ONLY******");
				return supplierTempUploadedMsg();
			}
		}

		return "catalogMaintenanceFile";
	}

	/**
	 * Gets the price type.
	 * 
	 * @param docId
	 *            Long docId(mandatory)
	 * @param contractNo
	 *            String contractNo(optional)
	 * @return ""
	 */
	public String getPriceType(Long docId, String contractNo){
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
		try{
			String allPrice = "";
			System.out.println("contractNo"+contractNo);
			System.out.println("docId:"+docId);
			if(contractNo!=null && contractNo.length()>0) {
				ContractItemReqDTO ctItemReqDTO = new ContractItemReqDTO();
				ctItemReqDTO.setContractItemId(docId);
				ContractItemResDTO ctItemResDTO = contractItemSO.getContractItem(ctItemReqDTO);
				allPrice = this.getParameterDesc(ctItemResDTO.getCtContractItem().getPriceType());
			} else {
				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				reqDTO.setSupplierItemId(docId);
				reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
				CatalogueResDTO resDTO = catalogueSO.getAllPriceType(reqDTO);

				System.out.println("resDTO.getList():"+resDTO.getList().size());
				if(resDTO.getList() != null){
					for(int i=0;i<resDTO.getList().size();i++){
						allPrice+=resDTO.getList().get(i);
						if(i!=resDTO.getList().size()-1)
							allPrice+=", "; 
					}
				}
			}
			System.out.println("allPrice:"+allPrice);
			return allPrice;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			catalogueSO = null;
		}
		return "";
	}

	/*
	 * public void searchCatalogue(){
	 * log.info("*******searchCatalogue************");
	 * 
	 * FacesScopes facesScopes = new FacesScopes(); String unspscCode =
	 * facesScopes.getParameter("unspscCode"); log.info("unspscCode: "
	 * + unspscCode + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	 * 
	 * ICatalogueSO catalogueSO = (ICatalogueSO)
	 * BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
	 * 
	 * SearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
	 * statusFilterBean.setSearchField("itemStatus");
	 * statusFilterBean.setSearchValue("D");
	 * searchBean.getDataFilterBeans().add(statusFilterBean);
	 * 
	 * searchBean.setNavigatable(false);
	 * 
	 * if(unspscCode != null && !unspscCode.equals("null") &&
	 * unspscCode.length() > 0) { SearchFilterBean unspscFilterBean = new
	 * SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	 * unspscFilterBean.setSearchField("cmItem.cmUnspsc.unspscCode");
	 * unspscFilterBean.setSearchValue(unspscCode);
	 * searchBean.getDataFilterBeans().add(unspscFilterBean); }
	 * 
	 * CatalogueReqDTO reqDTO = new CatalogueReqDTO();
	 * 
	 * reqDTO.setSearchBean(searchBean); log.info("catalogueSO:"+catalogueSO);
	 * 
	 * CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setSupplierItemList(resDTO.getSupplierItemList());
	 * 
	 * }
	 */

	/**
	 * Compare indxe action.
	 */
	public void compareIndxeAction() {

		log.debug("****index***" + this.getManagedBean().getCompareIndex());

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("name1");
		Integer index = Integer.parseInt(name1);

		CmSupplierItem catalog = this.getManagedBean()
				.getSelectedSupplierItemList().get(index);

		// this.getManagedBean().setCatalogueObj(catalog);
		this.getManagedBean().setCompareIndex(index);

	}

	/**
	 * Back to previous screen.
	 * 
	 * @return supplierItemView
	 */
	public String backToPervious() {

		return "supplierItemView";
	}

	/**
	 * This method is used On tab change at catalogMaintenance.
	 * 
	 * @param status
	 *            (String status(mandatory))
	 * @return String(catalogMaintenanceFile)
	 */
	public String onTabChange1(String status) {
		log.info("onTabChange1");
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String itemStatus = originalServletRequest.getParameter("itemStatus");
		log.info(".2.......itemStatus="+itemStatus);
		if (itemStatus != null) {
			originalServletRequest.getParameterMap().remove("itemStatus");

		}
		log.info("******status*******" + status);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		try {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			log.info("getSmSupplier"
					+ smResDTO.getResultBean().getResultList().get(0));

			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
					.get(0).getSupplierId());

			resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);

			log.info("size:" + resDTO.getSupplierZoneLs().size());

			this.getManagedBean().setSupplierZoneList(
					resDTO.getSupplierZoneLs());

			if (status.equalsIgnoreCase("Draft")) {
				this.getManagedBean().setCloneCmSupplierItemLs(null);
				this.getManagedBean().getDraftSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(0);

			} else if (status.equalsIgnoreCase("Published")) {


				resDTO = catalogueSO.findPublishItemForAlert();

				if(resDTO.getSupplierItemList()!=null && resDTO.getSupplierItemList().size()>0){
					String msg = "";
					Locale locale = new FacesScopes().getUserLocale();

					msg = ResourceBundleUtils.getMessage("lbl.cm.expiry.alert", new String[] { ""+resDTO.getSupplierItemList().size()} ,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);

					this.getManagedBean().setExpiryAlert(msg);
				}else{
					this.getManagedBean().setExpiryAlert("");
				}

				this.getManagedBean().getPublishSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(4);

			} else if (status.equalsIgnoreCase("Approval")) {

				this.getManagedBean().getApprovalSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(1);

			} else if (status.equalsIgnoreCase("Approved")) {

				this.getManagedBean().getApprovedSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(2);

			} else if (status.equalsIgnoreCase("Rejected")) {

				this.getManagedBean().getRejectSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(3);

			} else if (status.equalsIgnoreCase("Expired")) {

				this.getManagedBean().getExpiredSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(5);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}
		log.info("before return to page");

		if(status.equalsIgnoreCase("Draft")){
			return "catalogMaintenanceFile?faces-redirect=true";
		}else{
			return "catalogMaintenanceFile";
		}
	}
	/*
	 * public void onTabChange(TabChangeEvent event) {
	 * log.info("title"+event.getTab().getTitle());
	 * 
	 * ICatalogueSO catalogueSO = (ICatalogueSO)
	 * BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
	 * CatalogueReqDTO reqDTO = new CatalogueReqDTO(); CatalogueResDTO resDTO =
	 * null; SearchBean<CmSupplierItem> searchBean = new
	 * SearchBean<CmSupplierItem>(); searchBean.setNavigatable(false);
	 * 
	 * reqDTO.setSupplierId(new Long(1));
	 * 
	 * resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
	 * 
	 * log.info("size:"+resDTO.getSupplierZoneLs().size());
	 * 
	 * this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
	 * 
	 * if(event.getTab().getTitle().equalsIgnoreCase("Draft")){
	 * 
	 * 
	 * reqDTO.setItemStatus("D"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());
	 * 
	 * }else if(event.getTab().getTitle().equalsIgnoreCase("Published")){
	 * 
	 * reqDTO.setItemStatus("U"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());
	 * 
	 * }else if(event.getTab().getTitle().equalsIgnoreCase("Pending Approval")){
	 * 
	 * reqDTO.setItemStatus("P"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());
	 * 
	 * }else if(event.getTab().getTitle().equalsIgnoreCase("Approved")){
	 * 
	 * reqDTO.setItemStatus("A"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());
	 * 
	 * }else if(event.getTab().getTitle().equalsIgnoreCase("Rejected")){
	 * 
	 * reqDTO.setItemStatus("R"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());
	 * 
	 * }else if(event.getTab().getTitle().equalsIgnoreCase("Expired")){
	 * 
	 * reqDTO.setItemStatus("E"); resDTO =
	 * catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setExpiredItemList(resDTO.getSupplierItemList());
	 * 
	 * }
	 * 
	 * }
	 */

	/**
	 * View cart list item.
	 * 
	 * @return String(cartListItemView)
	 */
	public String viewCartListItem() {

		return "cartListItemView";
	}

	/**
	 * Delete cart item.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteCartItem(int index) {

		this.getManagedBean().getCartList().remove(index);
	}

	/**
	 * Publish approved items.
	 * 
	 * @return String(catalogMaintenanceFile)
	 */
	public String publishApprovedItems() {

		if(this.getManagedBean().getSelectedApprovedItems() != null 
				&& this.getManagedBean().getSelectedApprovedItems().length > 0) {
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			ICatalogueSO catalogueSO = null;
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			List<CmSupplierItem> csiList = new ArrayList<CmSupplierItem>();
			reqDTO.setTrackDairyVOs(new ArrayList<CmTrackingDairyVO>());

			log.info("Tracking diary for catalog approved items:publishApprovedItems");


			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

			StatusReqDTO statusReqDTO = new StatusReqDTO();

			statusReqDTO.setStatusId(CmConst.PUBLISHED);

			StatusResDTO statusResDTO = statusSO.retrieveStatusByStatusId(statusReqDTO);



			for(CmSupplierItem csi : this.getManagedBean().getSelectedApprovedItems()) {
				csiList.add(csi);
				reqDTO.setTrackingStatus(true);
				reqDTO.setSupplierId(csi.getSupplierId());

				String actionDesc ="Item "+this.getManagedBean().getSelectedMaintainItem().getCmItem().getItemName()+" telah diiklankan oleh "+(pmUser.getUserName()!=null?pmUser.getUserName():"SYS");
				reqDTO.getTrackDairyVOs().add(CmApplicationUtil.assignTrackingDairyValues(csi,actionDesc, statusResDTO.getPmStatus()!=null?statusResDTO.getPmStatus():new PmStatus()));
			}			
			try {
				catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
				reqDTO.setSupplierItem(csiList);

				CatalogueResDTO resDTO = catalogueSO.publishApprovedItem(reqDTO);
				if(resDTO.isSuccessful()) {

					//reqDTO.setSupplierItem(null);
					this.getManagedBean().getApprovedSearchDataModel().reloadData();


					FacesContext facesContext = FacesContext.getCurrentInstance();					
					facesContext.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(MessageConst.INFO_710002,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
				}
			} finally {
				catalogueSO = null;
			}			

		} else {
			throw new EpException(MessageConst.ERROR_715015);
		}

		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			log.info("******IS Supplier Temp*******");
			if(!new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
				log.info("********SUPPLIER_TEMP ONLY******");
				return supplierTempUploadedMsg();
			}
		}
		return "catalogMaintenanceFile";
	}

	/**
	 * Publish catalog item.
	 * 
	 * @return String(catalogMaintenanceFile)
	 */
	public String publishCatalogItem() {

		if(this.getManagedBean().getSelectedApprovedItems().length < 1) {
			throw new EpException(MessageConst.ERROR_715015);
		}

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm",ISupplierSearchSO.class);

		try {

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			log.info("this.getManagedBean().getSelectedApprovedItems().length:"
					+ this.getManagedBean().getSelectedApprovedItems().length);
			for (int i = 0; i < this.getManagedBean().getSelectedApprovedItems().length; i++) {
				CmSupplierItem catalog = this.getManagedBean().getSelectedApprovedItems()[i];

				catalog.setItemStatus("U");


				//Past Start Date should change to current date
				Date dateNow = new Date();
				if(catalog.getCmStandardPricing().getStartDate().before(dateNow)){
					catalog.getCmStandardPricing().setStartDate(dateNow);
				}

				//end date already passed
				if(catalog.getCmStandardPricing().getEndDate().before(dateNow)){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715028,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				//same item with existed one cannot overlap the date
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
				supplierReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierItemId(catalog.getSupplierItemId());
				reqDTO.setUomId(catalog.getPmUom().getUomId());
				reqDTO.setExtensionCode(catalog.getCmItem().getExtensionCode());
				reqDTO.setStartDate1(catalog.getCmStandardPricing().getStartDate());
				reqDTO.setEndDate1(catalog.getCmStandardPricing().getEndDate());
				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());
				if(catalogueSO.checkExistedItem(reqDTO)){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
									MessageUtils.getMessage(MessageConst.ERROR_715019,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					return "catalogMaintenanceFile";
				}

				reqDTO.getSaveSupplierItems().add(catalog);
			}

			CatalogueResDTO resDTO = new CatalogueResDTO();

			resDTO = catalogueSO.updateSupplierItem(reqDTO);

			log.info("resDTO.isSuccessful():" + resDTO.isSuccessful());

			if (resDTO.isSuccessful()) {


				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				log.info("getSmSupplier"
						+ smResDTO.getResultBean().getResultList().get(0));

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());

				//log.info("getSupplierId = "+smResDTO.getResultBean().getResultList().get(0).getSupplierId());

				this.getManagedBean().getApprovedSearchDataModel().reloadData();

				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_710002,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}

		}catch(Exception e){
			//this.getManagedBean().setApprovedItemList(null);
			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		if(new FacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			log.info("******IS Supplier Temp*******");
			if(!new FacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
				log.info("********SUPPLIER_TEMP ONLY******");
				return supplierTempUploadedMsg();
			}
		}

		return "catalogMaintenanceFile";
	}

	/**
	 * Compare supplier item prices.
	 * 
	 * @return String(compareCatalogue)
	 */
	public String compareSupplierItem() {

		log.info("==============compareSupplierItem========================");
		log.info("this.getManagedBean().getSelectViewItems().size() = "+this.getManagedBean().getSelectViewItems().size());
		this.getManagedBean().setSelectedSupplierItemList(null);
		this.getManagedBean().setContractedExist(false);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		try{
			for (VCmSupplierItem view : this.getManagedBean().getSelectViewItems()) {



				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				log.info("view.getDocId()" + view.getDocId());
				reqDTO.setDocId(view.getDocId());

				CatalogueResDTO resDTO = catalogueSO.searchSupplierItem(reqDTO);

				CmSupplierItem cmSupplierItem = resDTO.getSupplierItemList().get(0);

				log.info("filtering active price");
				//tier
				Iterator<CmTierPricing> a = cmSupplierItem.getCmTierPricings().iterator();
				while(a.hasNext()){
					CmTierPricing cmTierPricing = a.next();
					if(!(cmTierPricing.getStartDate().getTime()<=new Date().getTime() && cmTierPricing.getEndDate().getTime()>=new Date().getTime())){
						a.remove();
					}
				}
				//tier promotion
				Iterator<CmPromotionTierPricing> b = cmSupplierItem.getCmPromotionTierPricings().iterator();
				while(b.hasNext()){
					CmPromotionTierPricing cmPromotionTierPricing = b.next();
					if(!(cmPromotionTierPricing.getStartDate().getTime()<=new Date().getTime() && cmPromotionTierPricing.getEndDate().getTime()>=new Date().getTime())){
						b.remove();
					}
				}
				//zonal
				Iterator<CmZonalStandardPricing> c = cmSupplierItem.getzStdPrices().iterator();
				while(c.hasNext()){
					CmZonalStandardPricing cmZonalStandardPricing = c.next();
					if(!(cmZonalStandardPricing.getStartDate().getTime()<=new Date().getTime() && cmZonalStandardPricing.getEndDate().getTime()>=new Date().getTime())){
						c.remove();
					}
				}
				//zonal promotion
				Iterator<CmZonalPromotionPricing> d = cmSupplierItem.getCmZonalPromotionPricings().iterator();
				while(d.hasNext()){
					CmZonalPromotionPricing cmZonalPromotionPricing = d.next();
					if(!(cmZonalPromotionPricing.getStartDate().getTime()<=new Date().getTime() && cmZonalPromotionPricing.getEndDate().getTime()>=new Date().getTime())){
						d.remove();
					}
				}
				//zonal tier
				Iterator<CmZonalTierPricing> e = cmSupplierItem.getCmZonalTierPricings().iterator();
				while(e.hasNext()){
					CmZonalTierPricing cmZonalTierPricing = e.next();
					if(!(cmZonalTierPricing.getStartDate().getTime()<=new Date().getTime() && cmZonalTierPricing.getEndDate().getTime()>=new Date().getTime())){
						e.remove();
					}
				}
				//zonal tier promotion
				Iterator<CmZonalPromotionTierPricing> f = cmSupplierItem.getCmZonalPromotionTierPricings().iterator();
				while(f.hasNext()){
					CmZonalPromotionTierPricing cmZonalPromotionTierPricing = f.next();
					if(!(cmZonalPromotionTierPricing.getStartDate().getTime()<=new Date().getTime() && cmZonalPromotionTierPricing.getEndDate().getTime()>=new Date().getTime())){
						f.remove();
					}
				}


				log.info("*****************end********************");

				this.getManagedBean().getSelectedSupplierItemList().add(resDTO.getSupplierItemList().get(0));
			}

			for (VCmSupplierItem view : this.getManagedBean().getViewSupplierItemList()) {

				if (view.getContractNo() != null || view.getCircularNo() != null) {
					this.getManagedBean().setContractedExist(true);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

		return "compareCatalogue";
	}

	/**
	 * add To Cart.
	 * 
	 * @param index
	 *            (int index(mandatory)
	 */
	public void addToCart(int index) {

		// for(CmSupplierItem supplierItem :
		// this.getManagedBean().getSelectedSupplierItemList()){

		this.getManagedBean().getTempList()
		.add(this.getManagedBean().getSupplierItemList().get(index));
		// }

		for (CmSupplierItem temp : this.getManagedBean().getTempList()) {
			if (!this.getManagedBean().getTempMap()
					.containsKey(temp.getSupplierItemId())) {
				this.getManagedBean().getCartList().add(temp);
				this.getManagedBean()
				.getTempMap()
				.put(temp.getSupplierItemId(), temp.getSupplierItemId());
			}
		}
	}

	/**
	 * trigger Compare.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void triggerCompare(int index) {

		log.info("***********triggerCompare************" + index);

		this.getManagedBean().setSelectedSupplierItemList(null);

		for (CmSupplierItem supplierItem : this.getManagedBean()
				.getSupplierItemList()) {
			if (supplierItem.isSelected()) {
				this.getManagedBean().getSelectedSupplierItemList()
				.add(supplierItem);
			}
		}

	}

	/**
	 * adds Into WishList.
	 */
	public void addIntoWishList() {

		this.getManagedBean().setSelectedSupplierItemList(null);

		for (CmSupplierItem supplierItem : this.getManagedBean()
				.getSupplierItemList()) {
			if (supplierItem.isSelected()) {
				this.getManagedBean().getSelectedSupplierItemList()
				.add(supplierItem);
			}
		}

		log.info("size:"
				+ this.getManagedBean().getSelectedSupplierItemList().size());

		// genTempImages(this.getManagedBean().getSelectedCatalogList());

		/*
		 * for (CatCatalog beanNew : this.getManagedBean().getCatalogList()) {
		 * log.info("hktan beanNew.isSelected()" + beanNew.isSelected());
		 * log.info("hktan beanNew.getCatalogSeq()" + beanNew.getCatalogSeq());
		 * if (beanNew.isSelected() &&
		 * !isExistWishList(beanNew.getCatalogSeq())) {
		 * 
		 * this.getManagedBean().getSelectedCatalogList().add(beanNew);
		 * 
		 * }else if(!beanNew.isSelected()){
		 * this.getManagedBean().getSelectedCatalogList().remove(beanNew);
		 * 
		 * }
		 * 
		 * }
		 */

	}

	/**
	 * delete Compared Item.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteComparedItem(int index) {

		boolean removeFlag = false;
		for (VCmSupplierItem itemView : this.getManagedBean()
				.getSelectViewItems()) {
			log.info("itemView.getDocId()" + itemView.getDocId());
			log.info("getSupplierItemId()"
					+ this.getManagedBean().getSelectedSupplierItemList()
					.get(index).getSupplierItemId());
			if (this.getManagedBean().getSelectedSupplierItemList().get(index)
					.getSupplierItemId() == itemView.getDocId()) {
				removeFlag = true;
				break;
			}

		}

		log.info("removeFlag" + removeFlag);
		if (removeFlag) {
			this.getManagedBean().getSelectViewItems().remove(index);
			this.getManagedBean().getSelectedSupplierItemList().remove(index);
		}

	}




	/**
	 * pre Render Supplier Catalogue.
	 */
	public void preRenderSupplierCatalogue() {



		/*		this.getManagedBean().setItemNameFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.getManagedBean().setItemCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setSupplierNameFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.getManagedBean().setContractNoFilterBean( new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setCircularNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setMofNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setEpNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setBizRegNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		 */

		// s1 module
		//		getManagedBean().setSimpleQuoteFlag(Boolean.TRUE);
		RequestNoteBackingBean requestNoteBackingBean = new RequestNoteBackingBean();
		requestNoteBackingBean.renewSesionObject();

		SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
		simpleQuoteBackingBean.renewSesionObject();

		PurchaseInquiryBackingBean purchaseInquiryBackingBean  = new PurchaseInquiryBackingBean();
		purchaseInquiryBackingBean.renewSesionObject();

		PurchaseInDefaultBackingBean purchaseInDefaultBackingBean = new PurchaseInDefaultBackingBean();
		purchaseInDefaultBackingBean.renewSesionObject();

		renewSesionObject();

		this.getManagedBean().setAdvanceSearchList(
				new ArrayList<AdvanceSearchVO>());
		for (int i = 0; i < 3; i++) {
			this.getManagedBean().getAdvanceSearchList()
			.add(new AdvanceSearchVO());
		}

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		try{
			StateReqDTO reqStateDTO = new StateReqDTO();
			reqStateDTO.setCountryCode("MY");
			StateResDTO resStateDTO = stateSO.retriveStatesListByCountryCode(reqStateDTO);

			this.getManagedBean().setStateList(resStateDTO.getStateList());
		}finally {
			stateSO = null;
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "supplierItemView");


	}

	/**
	 * pre Render Catalogue.
	 */
	public void preRenderCatalogue() {
		log.debug("In preRenderCatalogue");

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

		try {
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			log.info("Sm Supplier"
					+ smResDTO.getResultBean().getResultList().get(0)
					.getCompanyName());


			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String itemStatus = originalServletRequest.getParameter("itemStatus");
			log.info("........itemStatus="+itemStatus);
			if (itemStatus != null && !itemStatus.isEmpty()) {
				originalServletRequest.getParameterMap().remove("itemStatus");
				retrieveSupplierItemsBySupplierId(catalogueSO, smResDTO
						.getResultBean().getResultList().get(0).getSupplierId(), itemStatus);

			} else {
				retrieveSupplierItemsBySupplierId(catalogueSO, smResDTO
						.getResultBean().getResultList().get(0).getSupplierId(), null);

			}

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, "catalogMaintenanceFile");

		}catch(Exception ex){
			FacesScopes.preRenderErrorMessage(ex.getMessage());
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		log.debug("Exit preRenderCatalogue");



	}

	/**
	 * retrieve supplier item
	 * 
	 * @param catalogueSO
	 * @param supplierId
	 * @param itemStatus
	 */

	private void retrieveSupplierItemsBySupplierId(ICatalogueSO catalogueSO,
			Long supplierId, String itemStatus) {
		log.info("......retrieveSupplierItemsBySupplierId.....itemStatus="+itemStatus);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		reqDTO.setSupplierId(supplierId);

		if (itemStatus != null && !itemStatus.isEmpty()) {
			if (itemStatus.equals(CmConst.ITEM_STATUS_APPROVED)) {

				this.getManagedBean().getApprovedSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(2);

			} else if (itemStatus.equals(CmConst.ITEM_STATUS_REJECTED)) {

				this.getManagedBean().getRejectSearchDataModel().reloadData();

				this.getManagedBean().setTabActiveIndex(3);

			} else if (itemStatus.equals(CmConst.ITEM_STATUS_DRAFT)) {

				this.getManagedBean().getDraftSearchDataModel().reloadData();

				this.getManagedBean().setTabActiveIndex(0);
			} else if(itemStatus.equals(CmConst.ITEM_STATUS_EXPIRED)) {

				this.getManagedBean().getExpiredSearchDataModel().reloadData();
				this.getManagedBean().setTabActiveIndex(5);
			}

		} else {

			this.getManagedBean().getDraftSearchDataModel().reloadData();

			this.getManagedBean().setTabActiveIndex(0);
		}
		CatalogueResDTO resDTO2 = catalogueSO
				.retrieveSupplierZoneBySuppId(reqDTO);
		log.info("zone size:" + resDTO2.getSupplierZoneLs().size());
		this.getManagedBean().setSupplierZoneList(
				resDTO2.getSupplierZoneLs());

	}


	/**
	 * back To Previous.
	 * 
	 * @return string(supplierItemView)
	 */
	public String backToPrev() {

		return "supplierItemView";
	}

	/**
	 * adds catalogue remarks for item reject reason.
	 */
	public void addCatalogueRemarks() {

		CmItemRejectReason reason = new CmItemRejectReason();


		this.getManagedBean().getSelectedMaintainItem()
		.getCmItemRejectReasons().add(reason);

	}

	/**
	 * search UNSPSC Code For Item.
	 * 
	 * @return String(codificationView)
	 */
	public String navigateToSearchCode() {
		return "codificationView";

	}

	/**
	 * cancel Supplier Item Search.
	 * 
	 * @return String(supplierItemView)
	 */
	public String cancelToSupplierItemSearch() {

		return "supplierItemView";
	}

	/**
	 * Enable remark.
	 */
	public void enableRemark() {

		ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();
		bb.getManagedBean().setCatDetTabActiveIndex(2);
		boolean remark = false;

		String text = this.getManagedBean().getRejectReason();
		log.info("text == "+text);

		for (int i = 0; i < this.getManagedBean().getRejectReasonIds().size(); i++) {
			String id = String.valueOf(this.getManagedBean().getRejectReasonIds().get(i));
			if (id.equals("10010")) {
				setRemarkRendered(true);
				remark = true;
			}
		}

		if(!remark)
			setRemarkRendered(false);

	}

	/**
	 * compare View Item.
	 */
	public void compareViewItem() {

		System.out
		.println("this.getManagedBean().getSelectViewItems().length():"
				+ this.getManagedBean().getSelectViewItems());
	}

	/**
	 * pop Up Pricing.
	 * 
	 * @param item
	 *            (CmSupplierItem item(mandatory))
	 */
	public void popUpPricing(CmSupplierItem item) {

		log.info("***************item*********************");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		try{
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			reqDTO.setSupplierId(item.getSmSupplier().getSupplierId());
			CatalogueResDTO resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
			log.info("size:" + resDTO.getSupplierZoneLs().size());
			this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

			//Prices (Tier/Zonal/Zonal Tier) with future date should not be displayed.
			Date dateNow = new Date();
			//tier
			Iterator<CmTierPricing> iter = item.getCmTierPricings().iterator();
			while (iter.hasNext()) {
				CmTierPricing cmTierPricing = iter.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter.remove();
				}

			}
			//tier promotion
			Iterator<CmPromotionTierPricing> iter2 = item.getCmPromotionTierPricings().iterator();
			while (iter2.hasNext()) {
				CmPromotionTierPricing cmTierPricing = iter2.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter2.remove();
				}

			}
			/*for (CmPromotionTierPricing cmTierPricing : item.getCmPromotionTierPricings()){
				if(cmTierPricing.getStartDate().after(dateNow)){
					item.getCmPromotionTierPricings().remove(cmTierPricing);
				}
			}*/
			//zonal
			Iterator<CmZonalStandardPricing> iter3 = item.getzStdPrices().iterator();
			while (iter3.hasNext()) {
				CmZonalStandardPricing cmTierPricing = iter3.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter3.remove();
				}

			}
			/*for (CmZonalStandardPricing cmTierPricing : item.getzStdPrices()){
				if(cmTierPricing.getStartDate().after(dateNow)){
					item.getzStdPrices().remove(cmTierPricing);
				}
			}*/
			//zonal promotion
			Iterator<CmZonalPromotionPricing> iter4 = item.getCmZonalPromotionPricings().iterator();
			while (iter4.hasNext()) {
				CmZonalPromotionPricing cmTierPricing = iter4.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter4.remove();
				}

			}
			/*for (CmZonalPromotionPricing cmTierPricing : item.getCmZonalPromotionPricings()){
				if(cmTierPricing.getStartDate().after(dateNow)){
					item.getCmZonalPromotionPricings().remove(cmTierPricing);
				}
			}*/
			//zonal tier
			Iterator<CmZonalTierPricing> iter5 = item.getCmZonalTierPricings().iterator();
			while (iter5.hasNext()) {
				CmZonalTierPricing cmTierPricing = iter5.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter5.remove();
				}

			}
			/*for (CmZonalTierPricing cmTierPricing : item.getCmZonalTierPricings()){
				if(cmTierPricing.getStartDate().after(dateNow)){
					item.getCmZonalTierPricings().remove(cmTierPricing);
				}
			}*/
			//zonal tier promotion
			Iterator<CmZonalPromotionTierPricing> iter6 = item.getCmZonalPromotionTierPricings().iterator();
			while (iter6.hasNext()) {
				CmZonalPromotionTierPricing cmTierPricing = iter6.next();
				if(cmTierPricing.getStartDate().after(dateNow)){
					iter6.remove();
				}

			}
			/*for (CmZonalPromotionTierPricing cmTierPricing : item.getCmZonalPromotionTierPricings()){
				if(cmTierPricing.getStartDate().after(dateNow)){
					item.getCmZonalPromotionTierPricings().remove(cmTierPricing);
				}
			}*/

			this.getManagedBean().setSupplierItemObj(item);
			this.getManagedBean().setTabActiveIndex(0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			catalogueSO = null;
		}
	}

	/**
	 * find District.
	 */
	public void findDistrict() {

		log.info("*****************findDistrict***************");
		log.info("Id:" + this.getManagedBean().getStateId());

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		log.info("*********************************");
		for (Long stateId : this.getManagedBean().getStateId()) {
			log.info("stateId:" + stateId);
			reqDTO.setStateId(stateId);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);

			for (PmDistrict district : resDTO.getDistrictList()) {

				this.getManagedBean().getDistrictList().add(district);

			}

		}

		log.info("this.getManagedBean().getDistrictList().size():"
				+ this.getManagedBean().getDistrictList().size());

	}

	/**
	 * refresh Image.
	 */
	public void refreshImage() {

		log.info("********refreshImage*********");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
		.getNavigationHandler()
		.handleNavigation(fc, null,
				"addCatalogEdit.xhtml?faces-redirect=true");

	}

	/**
	 * checks If Is Remark Rendered.
	 * 
	 * @return true-if is remark rendered, false-if is remark not rendered,
	 */
	public boolean isRemarkRendered() {

		if(!this.remarkRendered){
			for (int i = 0; i < this.getManagedBean().getRejectReasonIds().size(); i++) {
				String id = String.valueOf(this.getManagedBean().getRejectReasonIds().get(i));
				log.info("id isRemarkRendered = "+id);
				if (id.equals("10010")) {
					return true;
				}
			}
		}

		return this.remarkRendered;
	}

	/**
	 * sets Remark Rendered.
	 * 
	 * @param remarkRendered
	 *            the new remark rendered
	 */
	public void setRemarkRendered(boolean remarkRendered) {
		this.remarkRendered = remarkRendered;
	}

	/**
	 * sets Row Index.
	 */
	public void setRowIndex() {

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("index");
		int index = Integer.parseInt(name1);
		log.info("*************index***************" + index);

		this.getManagedBean().setTierRowIndex(index);

	}

	/**
	 * sets Zonal Row Index.
	 */
	public void setZonalRowIndex() {

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("index");
		int index = Integer.parseInt(name1);
		log.info("*************index***************" + index);

		this.getManagedBean().setZonalTierRowIndex(index);

	}


	public void setRowIndexForDelete(){

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("indexDelete");
		int index = Integer.parseInt(name1);
		log.info("*************index***************" + index);

		this.getManagedBean().setZonalRowIndex(index);


	}


	/**
	 * delete Tier Price.
	 */
	public void deleteTierPrice() {

		List<CmTierPricing> tierList = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings();
		List<CmTierPricing> dataList = new ArrayList<CmTierPricing>();
		log.info("tierList:" + tierList.size());

		for (CmTierPricing cmPricing : tierList) {
			//log.info("cmPricing.getStand_group_id()" + cmPricing.getStand_group_id());
			//log.info("tierList.get(this.getManagedBean().getTierRowIndex()) "+ cmPricing.get(this.getManagedBean().getTierRowIndex()));
			//if (cmPricing.getStand_group_id().compareTo(tierList.get(this.getManagedBean().getTierRowIndex()).getStand_group_id()) == 0) {
			//	dataList.add(cmPricing);
			//}
			if(cmPricing.getStand_group_id()!=null)
				if (cmPricing.getStand_group_id().compareTo(tierList.get(this.getManagedBean().getTierRowIndex()).getStand_group_id()) == 0) {
					dataList.add(cmPricing);
				}
		}

		if(dataList.size()==0)
			dataList.add(tierList.get(this.getManagedBean().getTierRowIndex()));

		List<CmPromotionTierPricing> promotionTierList = this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings();
		List<CmPromotionTierPricing> childList = new ArrayList<CmPromotionTierPricing>();
		for (CmPromotionTierPricing cmPricing : promotionTierList) {

			CmTierPricing parent = (CmTierPricing) cmPricing.getParentPrice();

			if (dataList.contains(parent)) {
				childList.add(cmPricing);
			}
		}

		for (CmPromotionTierPricing price : childList) {
			this.getManagedBean().getSelectedMaintainItem()
			.getCmPromotionTierPricings().remove(price);
		}

		log.info("dataList:" + dataList.size());
		for (CmTierPricing price : dataList) {
			this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
			.remove(price);
		}

	}

	/**
	 * delete Promotion Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deletePromotionTierPrice(int index) {

		List<CmPromotionTierPricing> promotionTierList = this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings();
		List<CmPromotionTierPricing> dataList = new ArrayList<CmPromotionTierPricing>();
		log.info("tierList:" + promotionTierList.size());

		for (CmPromotionTierPricing cmPricing : promotionTierList) {
			if(cmPricing.getPromo_group_id()!=null)
				if (cmPricing.getPromo_group_id().compareTo(promotionTierList.get(index).getPromo_group_id()) == 0) {
					dataList.add(cmPricing);
				}
		}

		if(dataList.size()==0)
			dataList.add(promotionTierList.get(index));

		List<CmPricing> parentList = new ArrayList<CmPricing>();

		log.info("dataList:" + dataList.size());
		for (CmPromotionTierPricing price : dataList) {
			//			price.getParentPrice().setStandardDisabled(false);
			parentList.add(price.getParentPrice());
			this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()
			.remove(price);
		}

		for(CmPricing pricing : this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings()){
			if(parentList.contains(pricing.getParentPrice())){
				parentList.remove(pricing.getParentPrice());
			}
		}

		for(CmPricing pricing : parentList){
			pricing.setStandardDisabled(false);
		}

		//		Map<Date, List<CmPromotionTierPricing>> tierMap = groupingDate(this
		//				.getManagedBean().getSelectedMaintainItem()
		//				.getCmPromotionTierPricings());
		//
		//		if (!tierMap.isEmpty() && tierMap.size() > 0) {
		//
		//			List<CmPromotionTierPricing> dataList = (List) tierMap.get(this
		//					.getManagedBean().getSelectedMaintainItem()
		//					.getCmPromotionTierPricings().get(index).getStartDate());
		//			Integer groupingId = 0;
		//			for (CmPromotionTierPricing price : dataList) {
		//				groupingId = price.getStand_group_id();
		//				if (price.getParentPrice() != null) {
		//					price.getParentPrice().setStandardDisabled(false);
		//					price.setParentPrice(null);
		//				}
		//				this.getManagedBean().getSelectedMaintainItem()
		//						.getCmPromotionTierPricings().remove(price);
		//			}
		//
		//			for (CmPromotionTierPricing price : this.getManagedBean()
		//					.getSelectedMaintainItem().getCmPromotionTierPricings()) {
		//
		//				if (price.getStand_group_id().equals(groupingId)) {
		//					price.getParentPrice().setStandardDisabled(true);
		//				}
		//			}
		//
		//		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710004,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	/**
	 * delete Zonal Promotion Tier Price.
	 * 
	 * @param index
	 *           (int index(mandatory))
	 */
	public void deleteZonalPromotionTierPrice(int index) {

		List<CmZonalPromotionTierPricing> zonalPromotionTierPricings = this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings();
		List<CmZonalPromotionTierPricing> dataList = new ArrayList<CmZonalPromotionTierPricing>();
		log.info("tierList:" + zonalPromotionTierPricings.size());

		for (CmZonalPromotionTierPricing cmPricing : zonalPromotionTierPricings) {
			if(cmPricing.getPromo_group_id()!=null)
				if (cmPricing.getPromo_group_id().compareTo(zonalPromotionTierPricings.get(index).getPromo_group_id()) == 0) {
					dataList.add(cmPricing);
				}
		}

		if(dataList.size()==0)
			dataList.add(zonalPromotionTierPricings.get(index));

		log.info("dataList:" + dataList.size());
		List<CmPricing> parentList = new ArrayList<CmPricing>();

		for (CmZonalPromotionTierPricing price : dataList) {
			//			price.getParentPrice().setStandardDisabled(false);
			parentList.add(price.getParentPrice());
			this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()
			.remove(price);
		}

		for(CmPricing pricing : this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){
			if(parentList.contains(pricing.getParentPrice())){
				parentList.remove(pricing.getParentPrice());
			}
		}

		for(CmPricing pricing : parentList){
			pricing.setStandardDisabled(false);
		}

		//		Map<Date, List<CmZonalPromotionTierPricing>> tierMap = groupingDate(this
		//				.getManagedBean().getSelectedMaintainItem()
		//				.getCmZonalPromotionTierPricings());
		//
		//		if (!tierMap.isEmpty() && tierMap.size() > 0) {
		//
		//			List<CmZonalPromotionTierPricing> dataList = (List) tierMap
		//					.get(this.getManagedBean().getSelectedMaintainItem()
		//							.getCmZonalPromotionTierPricings().get(index)
		//							.getStartDate());
		//			Integer groupingId = 0;
		//			for (CmZonalPromotionTierPricing price : dataList) {
		//
		//				groupingId = price.getStand_group_id();
		//				if (price.getParentPrice() != null) {
		//					price.getParentPrice().setStandardDisabled(false);
		//					price.setParentPrice(null);
		//				}
		//				this.getManagedBean().getSelectedMaintainItem()
		//						.getCmZonalPromotionTierPricings().remove(price);
		//			}
		//
		//			for (CmZonalPromotionTierPricing price : this.getManagedBean()
		//					.getSelectedMaintainItem()
		//					.getCmZonalPromotionTierPricings()) {
		//				log.info("groupingId:" + groupingId);
		//				log.info("price.getStand_group_id()"
		//						+ price.getStand_group_id());
		//				if (price.getStand_group_id().equals(groupingId)) {
		//					price.getParentPrice().setStandardDisabled(true);
		//				}
		//			}
		//
		//		}

		FacesContext facesContext = FacesContext.getCurrentInstance();

		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710004,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

	}

	/**
	 * delete Zonal Standard Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteZonalStandardPrice() {


		CmZonalStandardPricing pricing = this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(this.getManagedBean().getZonalRowIndex());

		List<CmZonalStandardPricing> dataList = new ArrayList<CmZonalStandardPricing>();



		dataList.add(pricing);


		List<CmZonalPromotionPricing> promotionList = this
				.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionPricings();
		List<CmZonalPromotionPricing> childList = new ArrayList<CmZonalPromotionPricing>();
		for (CmZonalPromotionPricing cmPricing : promotionList) {

			CmZonalStandardPricing parent = (CmZonalStandardPricing) cmPricing
					.getParentPrice();

			if (dataList.contains(parent)) {
				childList.add(cmPricing);
			}
		}

		for (CmZonalPromotionPricing price : childList) {
			this.getManagedBean().getSelectedMaintainItem()
			.getCmZonalPromotionPricings().remove(price);
		}

		for (CmZonalStandardPricing price : dataList) {
			this.getManagedBean().getSelectedMaintainItem()
			.getzStdPrices().remove(price);
		}

	}

	/**
	 * delete Zonal Promotion Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteZonalPromotionPrice(int index) {

		CmZonalStandardPricing standardPricing = (CmZonalStandardPricing) this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().get(index).getParentPrice();

		List<CmZonalPromotionPricing> promoLs = new ArrayList<CmZonalPromotionPricing>();
		List<CmZonalStandardPricing> dataList = new ArrayList<CmZonalStandardPricing>();

		promoLs = this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings();
		dataList.add(standardPricing);

		if(promoLs!=null && promoLs.size()>0){
			for (CmZonalPromotionPricing cmPricing : promoLs) {

				CmZonalStandardPricing parent = (CmZonalStandardPricing) cmPricing.getParentPrice();

				if (!dataList.contains(parent)) {
					System.out.println("no more contain children");
					standardPricing.setStandardDisabled(false);
					break;
				}
			}
		}




		this.getManagedBean().getSelectedMaintainItem()
		.getCmZonalPromotionPricings().remove(index);


		if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().isEmpty()){
			standardPricing.setStandardDisabled(false);
		}

		System.out.println("final isStandardDisabled:"+standardPricing.isStandardDisabled());
	}

	/**
	 * delete Zonal Tier Price.
	 */
	public void deleteZonalTierPrice() {

		List<CmZonalTierPricing> tierList = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalTierPricings();
		List<CmZonalTierPricing> dataList = new ArrayList<CmZonalTierPricing>();
		for (CmZonalTierPricing cmPricing : tierList) {

			if (cmPricing.getStand_group_id().compareTo(
					tierList.get(this.getManagedBean().getZonalTierRowIndex())
					.getStand_group_id()) == 0) {
				dataList.add(cmPricing);
			}
		}

		List<CmZonalPromotionTierPricing> promotionTierList = this
				.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings();
		List<CmZonalPromotionTierPricing> childList = new ArrayList<CmZonalPromotionTierPricing>();
		for (CmZonalPromotionTierPricing cmPricing : promotionTierList) {

			CmZonalTierPricing parent = (CmZonalTierPricing) cmPricing
					.getParentPrice();

			if (dataList.contains(parent)) {
				childList.add(cmPricing);
			}
		}

		for (CmZonalPromotionTierPricing price : childList) {
			this.getManagedBean().getSelectedMaintainItem()
			.getCmZonalPromotionTierPricings().remove(price);
		}

		for (CmZonalTierPricing price : dataList) {
			this.getManagedBean().getSelectedMaintainItem()
			.getCmZonalTierPricings().remove(price);
		}

	}

	/**
	 * update To Quantity.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateToQty(int index) {

		if (index < this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().size() - 1) {
			CmTierPricing tier = this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings()
					.get(index + 1);
			if (tier.getStand_group_id() == this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings().get(index)
					.getStand_group_id())
				tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(index).getToQty() + 1);
		}
	}

	/**
	 * update Zonal Tier Quantity.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateZonalTierToQty(int index) {

		if (index < this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().size() - 1) {
			CmZonalTierPricing zonalTier = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings()
					.get(index + 1);
			if (zonalTier.getStand_group_id() == this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings().get(index)
					.getStand_group_id())
				zonalTier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().get(index).getToQty() + 1);
		}
	}

	/**
	 * update Validate Grouping Dates.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateValidateGroupingDates(int index) {

		CmPromotionTierPricing promotionTierPricing = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings()
				.get(index);

		List<CmPromotionTierPricing> list = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings();

		int tempIndex = index + 1;

		while (tempIndex < this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings().size()) {
			CmPromotionTierPricing pricing = list.get(tempIndex);
			if (pricing.getPromo_group_id() == promotionTierPricing
					.getPromo_group_id()
					&& pricing
					.getParentPrice()
					.getStartDate()
					.compareTo(
							promotionTierPricing.getParentPrice()
							.getStartDate()) == 0) {
				pricing.setStartDate(promotionTierPricing.getStartDate());
				pricing.setEndDate(promotionTierPricing.getEndDate());
			}
			tempIndex++;
		}

		List<CmPromotionTierPricing> cmList = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings();

		List<CmPromotionTierPricing> pList = new ArrayList<CmPromotionTierPricing>();

		tempIndex = 0;

		for (CmPromotionTierPricing pricing : cmList) {
			if (pricing.getPrice() != null && pricing.getStartDate() != null
					&& pricing.getEndDate() != null) {
				if (pList != null && pList.size() > 0) {

					validatePromotionTierDates(tempIndex);
					/*
					 * for(CmPromotionTierPricing p : pList){
					 * if(pricing.getParentPrice
					 * ().getStartDate().compareTo(p.getParentPrice
					 * ().getStartDate()) == 0){ // TO DO } }
					 */
				} else {
					pricing.setDisplayAction(true);
					pList.add(pricing);
				}
			}
			tempIndex++;
		}

	}

	/**
	 * update Validate Zonal Grouping Dates.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateValidateZonalGroupingDates(int index) {

		CmZonalPromotionTierPricing zonal = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings()
				.get(index);

		List<CmZonalPromotionTierPricing> list = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings();

		int tempIndex = index + 1;

		while (tempIndex < this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings().size()) {
			CmZonalPromotionTierPricing pricing = list.get(tempIndex);
			if (pricing.getPromo_group_id() == zonal.getPromo_group_id()
					&& pricing.getParentPrice().getStartDate()
					.compareTo(zonal.getParentPrice().getStartDate()) == 0) {
				pricing.setStartDate(zonal.getStartDate());
				pricing.setEndDate(zonal.getEndDate());
			}
			tempIndex++;
		}

		List<CmZonalPromotionTierPricing> cmList = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings();

		List<CmZonalPromotionTierPricing> pList = new ArrayList<CmZonalPromotionTierPricing>();

		tempIndex = 0;

		for (CmZonalPromotionTierPricing pricing : cmList) {
			if (pricing.getPrice() != null && pricing.getStartDate() != null
					&& pricing.getEndDate() != null) {
				if (pList != null && pList.size() > 0) {

					validateZonalPromotionTierDates(tempIndex);
					/*
					 * for(CmPromotionTierPricing p : pList){
					 * if(pricing.getParentPrice
					 * ().getStartDate().compareTo(p.getParentPrice
					 * ().getStartDate()) == 0){ // TO DO } }
					 */
				} else {
					pricing.setDisplayAction(true);
					pList.add(pricing);
				}
			}
			tempIndex++;
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);
	}

	/**
	 * validate Promotion Tier Dates.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validatePromotionTierDates(int index) {

		CmPromotionTierPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings()
				.get(index);

		List<CmPromotionTierPricing> previousList = new ArrayList<CmPromotionTierPricing>();
		for (int i = 0; i < this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings().size(); i++) {
			if (tier.getStand_group_id().equals(
					this.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionTierPricings().get(i)
					.getStand_group_id())) {
				previousList.add(this.getManagedBean()
						.getSelectedMaintainItem().getCmPromotionTierPricings()
						.get(i));
			}
		}

		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmPromotionTierPricing cmPricing : previousList) {

				if ((tier.getStartDate().compareTo(cmPricing.getStartDate()) < 0 && tier
						.getEndDate().compareTo(cmPricing.getStartDate()) < 0)
						|| (tier.getStartDate().compareTo(
								cmPricing.getEndDate()) > 0 && tier
								.getEndDate().compareTo(cmPricing.getEndDate()) > 0)) {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
					break;
				} else if (tier.getPromo_group_id() == cmPricing
						.getPromo_group_id()
						&& tier.getStartDate().compareTo(
								cmPricing.getStartDate()) == 0) {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
					break;
				} else {
					tier.setDisplayAction(false);
					this.getManagedBean().setError4(true);
					break;
				}
			}
		}

	}

	/**
	 * validate Zonal Promotion Tier Dates.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateZonalPromotionTierDates(int index) {

		CmZonalPromotionTierPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings()
				.get(index);

		List<CmZonalPromotionTierPricing> previousList = new ArrayList<CmZonalPromotionTierPricing>();
		for (int i = 0; i < this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings().size(); i++) {
			if (tier.getStand_group_id().equals(
					this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalPromotionTierPricings().get(i)
					.getStand_group_id())) {
				previousList.add(this.getManagedBean()
						.getSelectedMaintainItem()
						.getCmZonalPromotionTierPricings().get(i));
			}
		}

		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmZonalPromotionTierPricing cmPricing : previousList) {

				if ((tier.getStartDate().compareTo(cmPricing.getStartDate()) < 0 && tier
						.getEndDate().compareTo(cmPricing.getStartDate()) < 0)
						|| (tier.getStartDate().compareTo(
								cmPricing.getEndDate()) > 0 && tier
								.getEndDate().compareTo(cmPricing.getEndDate()) > 0)) {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
					break;
				} else if (tier.getPromo_group_id() == cmPricing
						.getPromo_group_id()
						&& tier.getStartDate().compareTo(
								cmPricing.getStartDate()) == 0) {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
					break;
				} else {
					tier.setDisplayAction(false);
					this.getManagedBean().setError4(true);
					break;
				}
			}
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);

	}

	/**
	 * Gets the last index to clone.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param cmPricings
	 *            (List<T> cmPricings(mandatory)
	 * @param standGroupId
	 *            (Integer standGroupId(mandatory))
	 * @return the last index to clone
	 */
	public <T extends CmPricing> int getLastIndexToClone(List<T> cmPricings, Integer standGroupId){
		int addIndex = 0;
		for(CmPricing pricing : cmPricings){
			if(pricing.getStand_group_id() == standGroupId){
				addIndex = cmPricings.indexOf(pricing);
			}
		}
		return addIndex;
	}

	/**
	 * clone Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void cloneTierPrice(int index) {

		CmTierPricing tier = new CmTierPricing();
		tier.setStartDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getStartDate());
		tier.setEndDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getEndDate());
		tier.setShowFlag(false);
		tier.setStand_group_id(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getStand_group_id());

		int markIndex = getLastIndexToClone(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings(), this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getStand_group_id());

		if(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(markIndex).getToQty() != null){
			tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(markIndex).getToQty() + 1);
			tier.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
			.add(markIndex + 1, tier);
		} else {
			throw new EpException(MessageConst.ERROR_715010);
		}

	}

	/**
	 * clone Zonal Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void cloneZonalTierPrice(int index) {

		CmZonalTierPricing tier = new CmZonalTierPricing();
		tier.setStartDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(index).getStartDate());
		tier.setEndDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(index).getEndDate());
		tier.setCmSupplierZone(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(index).getCmSupplierZone());
		tier.setShowFlag(false);
		tier.setStand_group_id(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(index).getStand_group_id());

		int markIndex = getLastIndexToClone(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings(), this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(index).getStand_group_id());

		if(this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().get(markIndex).getToQty() != null){
			tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(markIndex).getToQty() + 1);
			tier.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
			this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()
			.add(markIndex + 1, tier);
		} else {
			throw new EpException(MessageConst.ERROR_715010);
		}

	}

	/**
	 * validate Date Overlap.
	 * 
	 * @param index
	 *             (int index(mandatory))
	 */
	public void validateDateOverlap(int index) {

		List<CmTierPricing> previousList = new ArrayList<CmTierPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(i));

		CmTierPricing tier = this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index);
		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmTierPricing cmPricing : previousList) {

				if(tier.getStand_group_id() != cmPricing.getStand_group_id()){
					if ((tier.getStartDate().compareTo(cmPricing.getStartDate()) < 0 && tier
							.getEndDate().compareTo(cmPricing.getStartDate()) < 0)
							|| (tier.getStartDate().compareTo(
									cmPricing.getEndDate()) > 0 && tier
									.getEndDate().compareTo(cmPricing.getEndDate()) > 0)) {
						tier.setDisplayAction(true);
						this.getManagedBean().setError4(false);
					} else {
						tier.setDisplayAction(false);
						this.getManagedBean().setError4(true);
						break;
					}
				} else {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
				}
			}
		}
	}

	/**
	 * validate Zonal Standard Date Overlap.
	 * 
	 * @param index
	 *            int index(mandatory))
	 */
	public void validateZonalStandardDateOverlap(int index) {

		CmZonalStandardPricing zonalStandard = this.getManagedBean()
				.getSelectedMaintainItem().getzStdPrices().get(index);
		List<CmZonalStandardPricing> previousList = new ArrayList<CmZonalStandardPricing>();
		//		for (int i = 0; i < index; i++){
		//			if(this.getManagedBean().getSelectedMaintainItem()
		//			.getzStdPrices().get(i).getCmSupplierZone() != null)
		//			if(zonalStandard.getCmSupplierZone().getSuppZoneId() == this.getManagedBean().getSelectedMaintainItem()
		//					.getzStdPrices().get(i).getCmSupplierZone().getSuppZoneId())
		//				previousList.add(this.getManagedBean().getSelectedMaintainItem()
		//					.getzStdPrices().get(i));
		//		}	

		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices().get(i));

		if (zonalStandard.getStartDate() != null
				&& zonalStandard.getEndDate() != null) {
			for (CmZonalStandardPricing cmPricing : previousList) {
				if(zonalStandard.getCmSupplierZone().getSuppZoneId() == cmPricing
						.getCmSupplierZone().getSuppZoneId()){
					if ((zonalStandard.getStartDate().compareTo(
							cmPricing.getStartDate()) >= 0 && zonalStandard
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (zonalStandard.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && zonalStandard
									.getEndDate().compareTo(cmPricing.getEndDate()) <= 0)
									|| cmPricing.getStartDate().compareTo(
											zonalStandard.getStartDate()) >= 0) {
						zonalStandard.setDisplayAction(false);
						this.getManagedBean().setError4(true);
						break;
					} else {
						zonalStandard.setDisplayAction(true);
						this.getManagedBean().setError4(false);
					}
				} else if(zonalStandard.getCmSupplierZone() != null && cmPricing.getCmSupplierZone() != null && compareZonesForSameDistricts(zonalStandard.getCmSupplierZone(), cmPricing.getCmSupplierZone())){
					if ((zonalStandard.getStartDate().compareTo(
							cmPricing.getStartDate()) >= 0 && zonalStandard
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (zonalStandard.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && zonalStandard
									.getEndDate().compareTo(cmPricing.getEndDate()) <= 0)
									|| cmPricing.getStartDate().compareTo(
											zonalStandard.getStartDate()) >= 0) {
						zonalStandard.setDisplayAction(false);
						this.getManagedBean().setError8(true);
						break;
					} else {
						zonalStandard.setDisplayAction(true);
						this.getManagedBean().setError8(false);
					}
				} else {
					zonalStandard.setDisplayAction(true);
					this.getManagedBean().setError8(false);
				}
			}
		} else {
			zonalStandard.setDisplayAction(true);
			this.getManagedBean().setError8(false);
			this.getManagedBean().setError4(false);
		}
	}

	/**
	 * validate Zonal Promo Date Overlap.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateZonalPromoDateOverlap(int index) {

		CmZonalPromotionPricing zonalPromo = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionPricings()
				.get(index);
		List<CmZonalPromotionPricing> previousList = new ArrayList<CmZonalPromotionPricing>();

		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalPromotionPricings().get(i));

		if (zonalPromo.getStartDate() != null
				&& zonalPromo.getEndDate() != null) {
			for (CmZonalPromotionPricing cmPricing : previousList) {
				if (zonalPromo.getCmSupplierZone().getSuppZoneId() == cmPricing
						.getCmSupplierZone().getSuppZoneId()){
					if ((zonalPromo.getStartDate().compareTo(
							cmPricing.getStartDate()) >= 0 && zonalPromo
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (zonalPromo.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && zonalPromo
									.getEndDate().compareTo(cmPricing.getEndDate()) <= 0)
									|| cmPricing.getStartDate().compareTo(
											zonalPromo.getStartDate()) >= 0) {
						zonalPromo.setDisplayAction(false);
						this.getManagedBean().setError4(true);
						break;
					} else {
						zonalPromo.setDisplayAction(true);
						this.getManagedBean().setError4(false);
					}
				} else if(zonalPromo.getCmSupplierZone() != null && cmPricing.getCmSupplierZone() != null && compareZonesForSameDistricts(zonalPromo.getCmSupplierZone(), cmPricing.getCmSupplierZone())){
					if ((zonalPromo.getStartDate().compareTo(
							cmPricing.getStartDate()) >= 0 && zonalPromo
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (zonalPromo.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && zonalPromo
									.getEndDate().compareTo(cmPricing.getEndDate()) <= 0)
									|| cmPricing.getStartDate().compareTo(
											zonalPromo.getStartDate()) >= 0) {
						zonalPromo.setDisplayAction(false);
						this.getManagedBean().setError8(true);
						break;
					} else {
						zonalPromo.setDisplayAction(true);
						this.getManagedBean().setError8(false);
					}
				} else {
					zonalPromo.setDisplayAction(true);
					this.getManagedBean().setError8(false);
				}
			}
		} else {
			zonalPromo.setDisplayAction(true);
			this.getManagedBean().setError8(false);
			this.getManagedBean().setError4(false);
		}
	}

	/**
	 * validate Zonal Tier Date Overlap.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateZonalTierDateOverlap(int index) {

		List<CmZonalTierPricing> previousList = new ArrayList<CmZonalTierPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(i));

		CmZonalTierPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalTierPricings().get(index);
		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmZonalTierPricing cmPricing : previousList) {
				log.info("tier.getCmSupplierZone().getSuppZoneId()"
						+ tier.getCmSupplierZone().getSuppZoneId());
				System.out
				.println("cmPricing.getCmSupplierZone().getSuppZoneId()"
						+ cmPricing.getCmSupplierZone().getSuppZoneId());
				if (tier.getCmSupplierZone().getSuppZoneId() == cmPricing
						.getCmSupplierZone().getSuppZoneId()) {
					log.info("*******in********");
					if ((tier.getStand_group_id() != cmPricing.getStand_group_id()) && ((tier.getStartDate()
							.compareTo(cmPricing.getStartDate()) >= 0 && tier
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (tier.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && tier
									.getEndDate().compareTo(
											cmPricing.getEndDate()) <= 0))) {
						tier.setDisplayAction(false);
						this.getManagedBean().setError4(true);
						break;
					} else {
						tier.setDisplayAction(true);
						this.getManagedBean().setError4(false);
					}
				} else if(tier.getCmSupplierZone() != null && cmPricing.getCmSupplierZone() != null && compareZonesForSameDistricts(tier.getCmSupplierZone(), cmPricing.getCmSupplierZone())){
					if ((tier.getStartDate()
							.compareTo(cmPricing.getStartDate()) >= 0 && tier
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (tier.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && tier
									.getEndDate().compareTo(
											cmPricing.getEndDate()) <= 0)) {
						tier.setDisplayAction(false);
						this.getManagedBean().setError8(true);
						break;
					} else {
						tier.setDisplayAction(true);
						this.getManagedBean().setError8(false);
					}
				} else {
					tier.setDisplayAction(true);
					this.getManagedBean().setError8(false);
				}
			}
		} else {
			tier.setDisplayAction(true);
			this.getManagedBean().setError8(false);
			this.getManagedBean().setError4(false);
		}

	}

	/**
	 * validate Zonal Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateZonalTierPrice(int index){
		CmPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalTierPricings().get(index);
		Date startDate = tier.getStartDate();
		Date endDate = tier.getEndDate();
		updateTierDate(tier.getStand_group_id(), startDate, endDate, tier.getPricingType());
		List<CmPricing> pricings = new ArrayList<CmPricing>();

		for(int i = 0; i < index; i++){
			pricings.add(this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings().get(i));
		}
		if(tier.getPrice() != null){
			validatePriceValue(tier, pricings);
			validateZonalTierDateOverlap(index);
		}	
	}

	/**
	 * validate Zonal Promotion Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateZonalPromotionTierPrice(int index){
		CmPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(index);
		Date startDate = tier.getStartDate();
		Date endDate = tier.getEndDate();
		updateTierDate(tier.getStand_group_id(), startDate, endDate, tier.getPricingType());
		List<CmPricing> pricings = new ArrayList<CmPricing>();

		for(int i = 0; i < index; i++){
			pricings.add(this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalPromotionTierPricings().get(i));
		}
		if(tier.getPrice() != null){
			validatePriceValue(tier, pricings);
			updateValidateZonalGroupingDates(index);
		}	
	}

	/**
	 * validate Tier Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateTierPrice(int index){
		CmPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmTierPricings().get(index);
		Date startDate = tier.getStartDate();
		Date endDate = tier.getEndDate();
		updateTierDate(tier.getStand_group_id(), startDate, endDate, tier.getPricingType());
		List<CmPricing> pricings = new ArrayList<CmPricing>();
		for(int i = 0; i < index; i++){
			pricings.add(this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings().get(i));
		}
		if(tier.getPrice() != null){
			validatePriceValue(tier, pricings);
			validateDateOverlap(index);
		}

		if(tier.getStartDate() != null && tier.getEndDate() != null){
			//check for start and end date change will effect all child date also
		}

	}

	/**
	 * update Tier Date.
	 * 
	 * @param standGroupId
	 *            (int standGroupId(mandatory))
	 * @param startDate
	 *            (Date startDate(mandatory))
	 * @param endDate
	 *            (Date endDate(mandatory))
	 * @param priceType
	 *            (String priceType(mandatory))
	 */
	public void updateTierDate(int standGroupId, Date startDate, Date endDate, String priceType){

		switch(priceType){
		case "03":
			for(CmPricing pricing : this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings()){
				if(pricing.getStand_group_id() == standGroupId){
					pricing.setStartDate(startDate);
					pricing.setEndDate(endDate);
				}
			};
			break;
		case "04":
			for(CmPricing pricing : this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings()){
				if(pricing.getStand_group_id() == standGroupId){
					pricing.setStartDate(startDate);
					pricing.setEndDate(endDate);
				}
			};
			break;
		case "07":	
			for(CmPricing pricing : this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings()){
				if(pricing.getStand_group_id() == standGroupId){
					pricing.setStartDate(startDate);
					pricing.setEndDate(endDate);
				}
			};
			break;
		case "08":	
			for(CmPricing pricing : this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalPromotionTierPricings()){
				if(pricing.getStand_group_id() == standGroupId){
					pricing.setStartDate(startDate);
					pricing.setEndDate(endDate);
				}
			};
			break;
		default:
			// do nothing
			break;
		}

	}

	/**
	 * validate Tier Promotion Price.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void validateTierPromotionPrice(int index){
		CmPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings().get(index);
		Date startDate = tier.getStartDate();
		Date endDate = tier.getEndDate();
		updateTierDate(tier.getStand_group_id(), startDate, endDate, tier.getPricingType());
		List<CmPricing> pricings = new ArrayList<CmPricing>();

		for(int i = 0; i < index; i++){
			pricings.add(this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings().get(i));
		}
		if(tier.getPrice() != null){
			validatePriceValue(tier, pricings);
			updateValidateGroupingDates(index);
		}	
	}



	public void validatePromotionPrice(int index){
		CmPricing promotion = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionPricings().get(index);

		List<CmPricing> pricings = new ArrayList<CmPricing>();

		for(int i = 0; i < this.getManagedBean().getSelectedMaintainItem().getzStdPrices().size(); i++){
			pricings.add(this.getManagedBean()
					.getSelectedMaintainItem().getzStdPrices().get(i));
		}
		if(promotion.getPrice() != null){
			validatePriceValue(promotion, pricings);
			validateZonalPromoDateOverlap(index);
		}	
	}



	/**
	 * to Validate Price On Every Keyup.
	 * 
	 * @param pricing
	 *            (CmPricing pricing(mandatory))
	 * @param pricings
	 *            (List<CmPricing> pricings(mandatory))
	 */
	public void validatePriceValue(CmPricing pricing, List<CmPricing> pricings){
		boolean priceInvalid = false;
		boolean priceInvalidFirstRow = false;

		for(CmPricing cmPricing : pricings){
			System.out.println("*******standard********"+cmPricing.getPrice());
			System.out.println("*******promotion*******"+pricing.getPrice());


			if (pricing.getStand_group_id() == cmPricing.getStand_group_id() && (cmPricing.getPrice().compareTo(pricing.getPrice()) <= 0)){
				priceInvalid = true;
			}

		}	
		System.out.println("**************************************pricing.getPricingType()"+pricing.getPricingType());
		System.out.println("***priceInvalid****"+priceInvalid);
		if(pricing.getPricingType().equals("04")) {
			if(!priceInvalid) {
				BigDecimal promotionTierPrice = pricing.getPrice();
				BigDecimal parentTierPrice = pricing.getParentPrice().getPrice();
				System.out.println("parentTierPrice:"+parentTierPrice);
				System.out.println("promotionTierPrice:"+promotionTierPrice);
				if(parentTierPrice.compareTo(promotionTierPrice) <= 0) {
					priceInvalidFirstRow = true;
				}
			}
		}

		if(pricing.getPricingType().equals("08")) {
			if(!priceInvalid) {
				BigDecimal promotionTierPrice = pricing.getPrice();
				BigDecimal parentTierPrice = pricing.getParentPrice().getPrice();

				if(parentTierPrice.compareTo(promotionTierPrice) <= 0) {
					priceInvalid = true;
				}
			}
		}

		System.out.println("priceInvalidFirstRow"+priceInvalidFirstRow);




		if(priceInvalidFirstRow){
			pricing.setCheckOk(false);
			this.getManagedBean().setError99(true);


		}else{
			pricing.setCheckOk(true);
			this.getManagedBean().setError99(false);

			if(priceInvalid){
				pricing.setCheckOk(false);
				this.getManagedBean().setError9(true);
				if(pricing.getPricingType().equals("06")){
					pricing.setDisplayAction(false);
				}
			} else {
				if(pricing.getPricingType().equals("06")){
					pricing.setDisplayAction(true);
				}
				pricing.setCheckOk(true);
				this.getManagedBean().setError9(false);
			}

		}

		System.out.println("isDisplayAction:"+pricing.isDisplayAction());
	}

	/**
	 * compare Zones For Same Districts.
	 * 
	 * @param zone1
	 *           (CmSupplierZone zone1(mandatory))
	 * @param zone2
	 *            (CmSupplierZone zone2(mandatory))
	 * @return true, if successful
	 *         false, if fail
	 */
	public boolean compareZonesForSameDistricts(CmSupplierZone zone1, CmSupplierZone zone2){
		List<Long> districts1 = new ArrayList<Long>();
		boolean inValid = false;

		if(zone1.getSuppZoneId() == 0 || zone2.getSuppZoneId() == 0)
			return inValid;

		IZoneMaintenanceSO zoneMaintenanceSO = null;
		zoneMaintenanceSO = (IZoneMaintenanceSO) BusinessDelegate
				.getInstance().getServiceObject("cm", IZoneMaintenanceSO.class);
		ZoneMaintenanceReqDTO reqDTO = new ZoneMaintenanceReqDTO();
		reqDTO.setCmSupplierZoneId(zone1.getSuppZoneId());
		ZoneMaintenanceResDTO resDTO = zoneMaintenanceSO.getSupplierZoneById(reqDTO);

		CmSupplierZone z1 = resDTO.getSelectedCmSupplierZone();

		reqDTO.setCmSupplierZoneId(zone2.getSuppZoneId());
		resDTO = zoneMaintenanceSO.getSupplierZoneById(reqDTO);

		CmSupplierZone z2 = resDTO.getSelectedCmSupplierZone();

		if(z1.getCmSupplierZoneDtls() != null && z2.getCmSupplierZoneDtls() != null)
		{
			List<CmSupplierZoneDTL> cmSupplierZoneDTLs1 = z1.getCmSupplierZoneDtls();
			List<CmSupplierZoneDTL> cmSupplierZoneDTLs2 = z2.getCmSupplierZoneDtls();
			for(CmSupplierZoneDTL dtl : cmSupplierZoneDTLs1){
				if(dtl.getPmDistrict() != null && dtl.getPmDistrict().getDistrictId() != null){
					districts1.add(dtl.getPmDistrict().getDistrictId());
				}
			}

			for(CmSupplierZoneDTL dtl : cmSupplierZoneDTLs2){
				if(dtl.getPmDistrict() != null && dtl.getPmDistrict().getDistrictId() != null){
					if(districts1.contains(dtl.getPmDistrict().getDistrictId())){
						inValid = true;
						break;
					}
				}
			}
		}
		return inValid;
	}

	/**
	 * reset Error Dialog.
	 */
	public void resetErrorDialog() {
		this.getManagedBean().setError1(false);
		this.getManagedBean().setError2(false);
		this.getManagedBean().setError3(false);
		this.getManagedBean().setError4(false);
		this.getManagedBean().setError5(false);
		this.getManagedBean().setError6(false);
		this.getManagedBean().setError7(false);
		this.getManagedBean().setError8(false);
		this.getManagedBean().setError9(false);
	}

	/**
	 * update Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateErrorDialog(int index) {
		CmTierPricing cmTierPricing = this.getManagedBean()
				.getSelectedMaintainItem().getCmTierPricings().get(index);
		if (cmTierPricing.getFromQty() != null
				&& cmTierPricing.getToQty() != null
				&& cmTierPricing.getFromQty() >= cmTierPricing.getToQty()) {
			this.getManagedBean().setError1(true);
		} else {
			this.getManagedBean().setError1(false);
		}
		if (cmTierPricing.getStartDate() != null
				&& cmTierPricing.getEndDate() != null
				&& cmTierPricing.getStartDate().compareTo(
						cmTierPricing.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (cmTierPricing.getPrice() == null
				|| cmTierPricing.getFromQty() == null
				|| cmTierPricing.getToQty() == null
				|| cmTierPricing.getStartDate() == null
				|| cmTierPricing.getEndDate() == null) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}
		validateTierPrice(index);

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(0);
	}

	/**
	 * update Zonal Standard Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateZonalStandardErrorDialog(int index) {
		CmZonalStandardPricing cmZonalStandardPricing = this.getManagedBean()
				.getSelectedMaintainItem().getzStdPrices().get(index);

		if (cmZonalStandardPricing.getFromQty() != null
				&& cmZonalStandardPricing.getToQty() != null
				&& cmZonalStandardPricing.getFromQty() >= cmZonalStandardPricing.getToQty()) {
			this.getManagedBean().setError1(true);
		} else {
			this.getManagedBean().setError1(false);
		}
		if (cmZonalStandardPricing.getStartDate() != null
				&& cmZonalStandardPricing.getEndDate() != null
				&& cmZonalStandardPricing.getStartDate().compareTo(
						cmZonalStandardPricing.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (cmZonalStandardPricing.getPrice() == null
				|| cmZonalStandardPricing.getStartDate() == null
				|| cmZonalStandardPricing.getEndDate() == null
				|| cmZonalStandardPricing.getCmSupplierZone().getSuppZoneId() == 0) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		this.getManagedBean().setPricingTabIndex(2);
		this.getManagedBean().setZonalPricingIndex(0);
	}

	/**
	 * update Zonal Promo Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateZonalPromoErrorDialog(int index) {
		CmZonalPromotionPricing promo = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionPricings()
				.get(index);

		if (promo.getStartDate() != null && promo.getEndDate() != null
				&& promo.getStartDate().compareTo(promo.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (promo.getPrice() == null || promo.getStartDate() == null
				|| promo.getEndDate() == null
				|| promo.getCmSupplierZone().getSuppZoneId() == 0) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		this.getManagedBean().setPricingTabIndex(2);
		this.getManagedBean().setZonalPricingIndex(1);
	}

	/**
	 * update Standard Error Eialog.
	 * 
	 * @param standardPricing
	 *            (CmStandardPricing standardPricing(mandatory))
	 */
	public void updateStandardErrorDialog(CmStandardPricing standardPricing) {
		resetErrorDialog();

		if (standardPricing.getStartDate() != null
				&& standardPricing.getEndDate() != null
				&& standardPricing.getStartDate().compareTo(
						standardPricing.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (standardPricing.getPrice() == null
				|| standardPricing.getStartDate() == null
				|| standardPricing.getEndDate() == null) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		//check for 1 year period
		if(standardPricing.getStartDate() != null && standardPricing.getEndDate()!=null){
			Calendar cal = Calendar.getInstance();  
			cal.setTime(standardPricing.getStartDate());
			cal.add(Calendar.YEAR, 1);

			//log.info("start 1year after "+cal.getTime());
			//log.info("end date "+standardPricing.getEndDate().getTime());
			//log.info("start compare to end "+cal.getTime().compareTo(standardPricing.getEndDate()));
			//log.info("end compare to start "+standardPricing.getEndDate().compareTo(cal.getTime()));

			if(cal.getTime().compareTo(standardPricing.getEndDate())<0){
				this.getManagedBean().setError7(true);
			}else{
				this.getManagedBean().setError7(false);
			}
		}else
			this.getManagedBean().setError7(false);
	}

	/**
	 * update Promotion Error Dialog.
	 */
	public void updatePromotionErrorDialog() {
		resetErrorDialog();

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getPrice() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getEndDate() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getStartDate() != null) {

			this.getManagedBean().setError3(false);

			this.getManagedBean().getSelectedMaintainItem()
			.getCmPromotionPricing().setStandardDisabled(true);
			if (this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmPromotionPricing()
					.getStartDate()
					.compareTo(
							this.getManagedBean().getSelectedMaintainItem()
							.getCmPromotionPricing().getEndDate()) > 0) {
				this.getManagedBean().setError2(true);
				this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().setStandardDisabled(false);
				this.getManagedBean().setPricingTabIndex(0);
				this.getManagedBean().setNormalPricingIndex(1);
			} else {
				this.getManagedBean().setError2(false);
				this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().setStandardDisabled(true);
			}

			if(this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmStandardPricing()
					.getStartDate() != null && this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmStandardPricing()
					.getEndDate() != null){
				if (this.getManagedBean()
						.getSelectedMaintainItem()
						.getCmStandardPricing()
						.getStartDate()
						.compareTo(
								this.getManagedBean().getSelectedMaintainItem()
								.getCmPromotionPricing().getStartDate()) > 0
								|| this.getManagedBean()
								.getSelectedMaintainItem()
								.getCmPromotionPricing()
								.getEndDate()
								.compareTo(
										this.getManagedBean()
										.getSelectedMaintainItem()
										.getCmStandardPricing()
										.getEndDate()) > 0) {

					this.getManagedBean().setPricingTabIndex(0);
					this.getManagedBean().setNormalPricingIndex(1);
					this.getManagedBean().setError5(true);
					this.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionPricing().setStandardDisabled(false);
				} else {
					this.getManagedBean().setError5(false);
					this.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionPricing().setStandardDisabled(true);
				}
			} else {
				this.getManagedBean().setError5(false);
				this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().setStandardDisabled(true);
			}

			if(this.getManagedBean().getSelectedMaintainItem()
					.getCmStandardPricing().getPrice() != null){
				if (this.getManagedBean()
						.getSelectedMaintainItem()
						.getCmPromotionPricing()
						.getPrice()
						.compareTo(
								this.getManagedBean().getSelectedMaintainItem()
								.getCmStandardPricing().getPrice()) >= 0) {
					this.getManagedBean().setPricingTabIndex(0);
					this.getManagedBean().setNormalPricingIndex(1);
					this.getManagedBean().setError6(true);
					this.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionPricing().setStandardDisabled(false);
				} else {
					this.getManagedBean().setError6(false);
					this.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionPricing().setStandardDisabled(true);
				}
			} else {
				this.getManagedBean().setError6(false);
				this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().setStandardDisabled(true);
			}
		} else if (this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getPrice() == null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getEndDate() == null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionPricing().getStartDate() == null) {
			this.getManagedBean().setError2(false);
			this.getManagedBean().setError3(false);
			this.getManagedBean().setError5(false);
			this.getManagedBean().setError6(false);
			this.getManagedBean().getSelectedMaintainItem()
			.getCmPromotionPricing().setStandardDisabled(true);

		} else {
			this.getManagedBean().setError3(true);
			this.getManagedBean().setError2(false);
			this.getManagedBean().setError5(false);
			this.getManagedBean().setError6(false);
			this.getManagedBean().getSelectedMaintainItem()
			.getCmPromotionPricing().setStandardDisabled(false);

		}

	}

	/**
	 * update Promotion Tier Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updatePromotionTierErrorDialog(int index) {

		validateTierPromotionPrice(index);

		CmPromotionTierPricing cmPromotionTierPricing = this.getManagedBean()
				.getSelectedMaintainItem().getCmPromotionTierPricings()
				.get(index);
		if (cmPromotionTierPricing.getFromQty() != null
				&& cmPromotionTierPricing.getToQty() != null
				&& cmPromotionTierPricing.getFromQty() >= cmPromotionTierPricing
				.getToQty()) {
			this.getManagedBean().setError1(true);
		} else {
			this.getManagedBean().setError1(false);
		}
		if (cmPromotionTierPricing.getStartDate() != null
				&& cmPromotionTierPricing.getEndDate() != null
				&& cmPromotionTierPricing.getStartDate().compareTo(
						cmPromotionTierPricing.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (cmPromotionTierPricing.getPrice() == null
				|| cmPromotionTierPricing.getFromQty() == null
				|| cmPromotionTierPricing.getToQty() == null
				|| cmPromotionTierPricing.getStartDate() == null
				|| cmPromotionTierPricing.getEndDate() == null) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(1);

	}

	/**
	 * update Zonal Promotion Tier Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateZonalPromotionTierErrorDialog(int index) {

		CmZonalPromotionTierPricing zonal = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionTierPricings()
				.get(index);
		if (zonal.getFromQty() != null && zonal.getToQty() != null
				&& zonal.getFromQty() >= zonal.getToQty()) {
			this.getManagedBean().setError1(true);
		} else {
			this.getManagedBean().setError1(false);
		}
		if (zonal.getStartDate() != null && zonal.getEndDate() != null
				&& zonal.getStartDate().compareTo(zonal.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (zonal.getPrice() == null || zonal.getFromQty() == null
				|| zonal.getToQty() == null || zonal.getStartDate() == null
				|| zonal.getEndDate() == null) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(1);

	}

	/**
	 * update Zonal Tier Error Dialog.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void updateZonalTierErrorDialog(int index) {
		CmZonalTierPricing cmZonalTierPricing = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalTierPricings().get(index);
		if (cmZonalTierPricing.getFromQty() != null
				&& cmZonalTierPricing.getToQty() != null
				&& cmZonalTierPricing.getFromQty() >= cmZonalTierPricing
				.getToQty()) {
			this.getManagedBean().setError1(true);
		} else {
			this.getManagedBean().setError1(false);
		}
		if (cmZonalTierPricing.getStartDate() != null
				&& cmZonalTierPricing.getEndDate() != null
				&& cmZonalTierPricing.getStartDate().compareTo(
						cmZonalTierPricing.getEndDate()) >= 0) {
			this.getManagedBean().setError2(true);
		} else {
			this.getManagedBean().setError2(false);
		}
		if (cmZonalTierPricing.getCmSupplierZone().getSuppZoneId() == 0
				|| cmZonalTierPricing.getPrice() == null
				|| cmZonalTierPricing.getFromQty() == null
				|| cmZonalTierPricing.getToQty() == null
				|| cmZonalTierPricing.getStartDate() == null
				|| cmZonalTierPricing.getEndDate() == null) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}
		validateZonalTierPrice(index);

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(0);
	}

	/**
	 * validate Before Add To Zonal Promotion.
	 * 
	 * @param cmPricings
	 *            (List<CmZonalTierPricing> cmPricings(mandatory))
	 * @return true, if successful
	 *          false, if fail
	 */
	public boolean validateBeforeAddToZonalPromotion(
			List<CmZonalTierPricing> cmPricings) {

		boolean valid = true;
		int index;
		for (CmZonalTierPricing cmPricing : cmPricings) {
			index = cmPricings.indexOf(cmPricing);
			if (index > 0) {
				if (cmPricing.getToQty() == null
						|| (cmPricing.getStand_group_id() == cmPricings.get(index-1).getStand_group_id() && cmPricing.getFromQty() <= cmPricings.get(index - 1)
						.getToQty())
						|| cmPricing.getToQty() <= cmPricing.getFromQty() || cmPricing.getPrice() == null) {
					valid = false;
					break;
				}
			}
		}
		return valid;
	}

	/**
	 * validate Before Add To Promotion.
	 * 
	 * @param cmPricings
	 *            (List<CmTierPricing> cmPricings(mandatory))
	 * @return true, if successful
	 *         false, if fail
	 */
	public boolean validateBeforeAddToPromotion(List<CmTierPricing> cmPricings) {

		boolean valid = true;
		int index;
		for (CmTierPricing cmPricing : cmPricings) {
			index = cmPricings.indexOf(cmPricing);
			if (index > 0) {
				if (cmPricing.getToQty() == null
						|| cmPricing.getFromQty() <= cmPricings.get(index - 1)
						.getToQty()
						|| cmPricing.getToQty() <= cmPricing.getFromQty() || cmPricing.getPrice() == null) {
					valid = false;
					break;
				}
			}
		}
		return valid;
	}

	/**
	 * adds To Promotion.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void addToPromotion(int index) {

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings().size() > 0) {

			log.info("*************copy******************");

			List<CmTierPricing> promotionGroup = new ArrayList<CmTierPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStand_group_id();
			for(CmTierPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			Map<Date, List<CmTierPricing>> tierMap = groupingTier(promotionGroup);

			if (!tierMap.isEmpty() && tierMap.size() > 0) {

				List<CmTierPricing> dataList = (List) tierMap.get(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(index).getStand_group_id());

				if (validateBeforeAddToPromotion(dataList)) {
					int tempIndex = index;
					int promo_group_id = this
							.getManagedBean()
							.getSelectedMaintainItem()
							.getCmPromotionTierPricings()
							.get(this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmPromotionTierPricings().size() - 1)
									.getPromo_group_id();
					for (CmTierPricing pricing : dataList) {
						CmPromotionTierPricing price = new CmPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setShowFlag(pricing.isShowFlag());
						price.setStandardDisabled(true);
						price.setDisplayAction(false);
						price.setParentPrice(this.getManagedBean()
								.getSelectedMaintainItem().getCmTierPricings()
								.get(tempIndex++));
						price.setPromo_group_id(promo_group_id + 1);
						price.setStand_group_id(pricing.getStand_group_id());
						price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

						this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().add(price);

					}
					for(CmTierPricing pricing : promotionGroup){
						//						pricing.setStateDisabled(true);
						pricing.setStandardDisabled(true);
					}
					FacesContext facesContext = FacesContext
							.getCurrentInstance();

					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
									.getMessage(
											MessageConst.INFO_710003,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));

				} else {
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"",
									MessageUtils.getMessage(MessageConst.ERROR_715052,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
				}
			}

		} else {
			log.info("*************1st copy******************");

			List<CmTierPricing> tierList = this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings();

			List<CmTierPricing> checkList = new ArrayList<CmTierPricing>();

			Date stDate = this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(index).getStartDate();

			for (CmTierPricing cmPricing : tierList) {
				if (cmPricing.getStartDate() != null && cmPricing.getStartDate().compareTo(stDate) == 0) {
					checkList.add(cmPricing);
				}
			}

			List<CmTierPricing> promotionGroup = new ArrayList<CmTierPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().get(index).getStand_group_id();
			for(CmTierPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			if (validateBeforeAddToPromotion(promotionGroup)) {

				Map<Date, List<CmTierPricing>> tierMap = groupingTier(promotionGroup);

				if (!tierMap.isEmpty() && tierMap.size() > 0) {

					List<CmTierPricing> dataList = (List) tierMap.get(this
							.getManagedBean().getSelectedMaintainItem()
							.getCmTierPricings().get(index).getStand_group_id());

					int tempIndex = index;

					int promo_group_id = 1;

					for (CmPricing pricing : dataList) {
						CmPromotionTierPricing price = new CmPromotionTierPricing();
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setShowFlag(pricing.isShowFlag());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setStandardDisabled(true);
						price.setDisplayAction(false);
						price.setParentPrice(this.getManagedBean()
								.getSelectedMaintainItem().getCmTierPricings()
								.get(tempIndex++));
						price.setPromo_group_id(promo_group_id);
						price.setStand_group_id(pricing.getStand_group_id());
						price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

						this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().add(price);
					}
					for(CmTierPricing pricing : promotionGroup){
						//						pricing.setStateDisabled(true);
						pricing.setStandardDisabled(true);
					}
				}
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_710003,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			} else {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"",
								MessageUtils.getMessage(MessageConst.ERROR_715053,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		}

	}

	/**
	 * adds To Zonal Tier Promotion.
	 * 
	 * @param index
	 *           (int index(mandatory))
	 */
	public void addToZonalTierPromotion(int index) {

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings().size() > 0) {

			List<CmZonalTierPricing> promotionGroup = new ArrayList<CmZonalTierPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStand_group_id();
			for(CmZonalTierPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			Map<Date, List<CmZonalTierPricing>> tierMap = groupingTier(promotionGroup);

			if (!tierMap.isEmpty() && tierMap.size() > 0) {

				List<CmZonalTierPricing> dataList = (List) tierMap.get(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().get(index).getStand_group_id());

				if (validateBeforeAddToZonalPromotion(dataList)) {
					int tempIndex = index;
					int promo_group_id = this
							.getManagedBean()
							.getSelectedMaintainItem()
							.getCmZonalPromotionTierPricings()
							.get(this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalPromotionTierPricings().size() - 1)
									.getPromo_group_id();
					for (CmZonalTierPricing pricing : dataList) {
						CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
						price.setCmSupplierZone(pricing.getCmSupplierZone());
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setShowFlag(pricing.isShowFlag());
						price.setStandardDisabled(true);
						price.setDisplayAction(false);
						price.setParentPrice(this.getManagedBean()
								.getSelectedMaintainItem()
								.getCmZonalTierPricings().get(tempIndex++));
						price.setPromo_group_id(promo_group_id + 1);
						price.setStand_group_id(pricing.getStand_group_id());
						price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

						this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().add(price);

					}
					for(CmZonalTierPricing price : promotionGroup){
						price.setStandardDisabled(true);
					}
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
									.getMessage(
											MessageConst.INFO_710003,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes()
											.getUserLocale())));
				} else {
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"",
									MessageUtils.getMessage(MessageConst.ERROR_715052,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
				}
			}

		} else {
			log.info("**********1st copy*******");

			List<CmZonalTierPricing> tierList = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings();

			List<CmZonalTierPricing> checkList = new ArrayList<CmZonalTierPricing>();

			Date stDate = this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(index).getStartDate();

			for (CmZonalTierPricing cmPricing : tierList) {
				if (cmPricing.getStartDate() != null && cmPricing.getStartDate().compareTo(stDate) == 0) {
					checkList.add(cmPricing);
				}
			}

			List<CmZonalTierPricing> promotionGroup = new ArrayList<CmZonalTierPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get(index).getStand_group_id();
			for(CmZonalTierPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			if (validateBeforeAddToZonalPromotion(promotionGroup)) {

				Map<Date, List<CmZonalTierPricing>> tierMap = groupingTier(promotionGroup);

				if (!tierMap.isEmpty() && tierMap.size() > 0) {

					List<CmZonalTierPricing> dataList = (List) tierMap
							.get(this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings().get(index)
									.getStand_group_id());

					int tempIndex = index;

					int promo_group_id = 1;

					for (CmPricing pricing : dataList) {
						CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
						price.setCmSupplierZone(pricing.getCmSupplierZone());
						price.setStartDate(pricing.getStartDate());
						price.setEndDate(pricing.getEndDate());
						price.setFromQty(pricing.getFromQty());
						price.setToQty(pricing.getToQty());
						price.setShowFlag(pricing.isShowFlag());
						price.setMinDate(pricing.getStartDate());
						price.setMaxDate(pricing.getEndDate());
						price.setStandardDisabled(true);
						price.setDisplayAction(false);
						price.setParentPrice(this.getManagedBean()
								.getSelectedMaintainItem()
								.getCmZonalTierPricings().get(tempIndex++));
						price.setPromo_group_id(promo_group_id);
						price.setStand_group_id(pricing.getStand_group_id());
						price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

						this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().add(price);
					}
					for(CmZonalTierPricing price : promotionGroup){
						price.setStandardDisabled(true);
					}
				}
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_710003,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			} else {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"",
								MessageUtils.getMessage(MessageConst.ERROR_715053,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		}

	}


	/**
	 * adds To Zonal Promotion.
	 * 
	 * @param index
	 *           (int index(mandatory))
	 */
	public void addToZonalPromotion(int index) {

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionPricings().size() > 0) {

			List<CmZonalStandardPricing> promotionGroup = new ArrayList<CmZonalStandardPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getStand_group_id();



			for(CmZonalStandardPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			//Map<Date, List<CmZonalStandardPricing>> tierMap = groupingTier(promotionGroup);

			if (promotionGroup!=null && promotionGroup.size() > 0) {

				/*List<CmZonalStandardPricing> dataList = (List) tierMap.get(this
						.getManagedBean().getSelectedMaintainItem()
						.getzStdPrices().get(index).getStand_group_id());*/

				int tempIndex = index;
				int promo_group_id = this
						.getManagedBean()
						.getSelectedMaintainItem()
						.getCmZonalPromotionPricings()
						.get(this.getManagedBean()
								.getSelectedMaintainItem()
								.getCmZonalPromotionPricings().size() - 1)
								.getPromo_group_id();
				for (CmZonalStandardPricing pricing : promotionGroup) {
					CmZonalPromotionPricing price = new CmZonalPromotionPricing();
					price.setCmSupplierZone(pricing.getCmSupplierZone());
					price.setStartDate(pricing.getStartDate());
					price.setEndDate(pricing.getEndDate());
					price.setMinDate(pricing.getStartDate());
					price.setMaxDate(pricing.getEndDate());
					price.setShowFlag(pricing.isShowFlag());
					price.setStandardDisabled(true);
					price.setDisplayAction(false);
					price.setParentPrice(this.getManagedBean()
							.getSelectedMaintainItem()
							.getzStdPrices().get(tempIndex++));
					price.setPromo_group_id(promo_group_id + 1);
					price.setStand_group_id(pricing.getStand_group_id());
					price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

					this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().add(price);

				}
				for(CmZonalStandardPricing price : promotionGroup){
					price.setStandardDisabled(true);
				}
				FacesContext facesContext = FacesContext
						.getCurrentInstance();
				facesContext
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								null,
								MessageUtils
								.getMessage(
										MessageConst.INFO_710003,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes()
										.getUserLocale())));

			}

		} else {
			log.info("**********1st copy*******");

			/*List<CmZonalTierPricing> tierList = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings();

			List<CmZonalTierPricing> checkList = new ArrayList<CmZonalTierPricing>();

			Date stDate = this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(index).getStartDate();

			for (CmZonalTierPricing cmPricing : tierList) {
				if (cmPricing.getStartDate() != null && cmPricing.getStartDate().compareTo(stDate) == 0) {
					checkList.add(cmPricing);
				}
			}*/

			List<CmZonalStandardPricing> promotionGroup = new ArrayList<CmZonalStandardPricing>();
			Integer stId = this.getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index).getStand_group_id();
			for(CmZonalStandardPricing pricing : this
					.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices()){
				if(pricing.getStand_group_id() == stId){
					promotionGroup.add(pricing);
				}
			}

			//Map<Date, List<CmZonalStandardPricing>> tierMap = groupingTier(promotionGroup);

			if (promotionGroup!=null && promotionGroup.size() > 0) {

				/*List<CmZonalStandardPricing> dataList = (List) tierMap
							.get(this.getManagedBean()
									.getSelectedMaintainItem()
									.getzStdPrices().get(index)
									.getStand_group_id());*/

				int tempIndex = index;

				int promo_group_id = 1;

				for (CmPricing pricing : promotionGroup) {
					CmZonalPromotionPricing price = new CmZonalPromotionPricing();
					price.setCmSupplierZone(pricing.getCmSupplierZone());
					price.setStartDate(pricing.getStartDate());
					price.setEndDate(pricing.getEndDate());
					price.setShowFlag(pricing.isShowFlag());
					price.setMinDate(pricing.getStartDate());
					price.setMaxDate(pricing.getEndDate());
					price.setStandardDisabled(true);
					price.setDisplayAction(false);
					price.setParentPrice(this.getManagedBean()
							.getSelectedMaintainItem()
							.getzStdPrices().get(tempIndex++));
					price.setPromo_group_id(promo_group_id);
					price.setStand_group_id(pricing.getStand_group_id());
					price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());

					this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings().add(price);
				}
				for(CmZonalStandardPricing price : promotionGroup){
					price.setStandardDisabled(true);
				}
			}
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(
									MessageConst.INFO_710003,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));


		}

	}



	/*
	 * public void duplicateZonalStandardPrice(int index){
	 * 
	 * 
	 * 
	 * CmZonalStandardPricing price = new CmZonalStandardPricing();
	 * 
	 * price.getPmState().setStateId(this.getManagedBean().getSelectedMaintainItem
	 * ().getzStdPrices().get(index).getPmState().getStateId());
	 * price.setPrice(this
	 * .getManagedBean().getSelectedMaintainItem().getzStdPrices
	 * ().get(index).getPrice());
	 * price.setStartDate(this.getManagedBean().getSelectedMaintainItem
	 * ().getzStdPrices().get(index).getStartDate());
	 * price.setEndDate(this.getManagedBean
	 * ().getSelectedMaintainItem().getzStdPrices().get(index).getEndDate());
	 * price.setShowFlag(false);
	 * 
	 * 
	 * DistrictReqDTO reqDTO = new DistrictReqDTO();
	 * 
	 * reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().
	 * getzStdPrices().get(index).getPmState().getStateId()); IDistrictSO
	 * districtSO = (IDistrictSO)
	 * BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
	 * 
	 * DistrictResDTO resDTO = districtSO .retriveDistrictListByStateID(reqDTO);
	 * System
	 * .out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
	 * price.setPmDistricts(resDTO.getDistrictList());
	 * 
	 * 
	 * this.getManagedBean().getSelectedMaintainItem().getzStdPrices().add(index+
	 * 1 , price);
	 * 
	 * }
	 */

	/**
	 * item Reject Reason.
	 * 
	 * @param id
	 *            (long id(mandatory))
	 * @return string
	 */
	public String itemRejectReason(long id) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		try{
			return catalogueSO.findPmStatusDesc(id, new FacesScopes().getUserLocale().getLanguage());
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}finally{
			catalogueSO = null;
		}

	}

	/**
	 * grouping Tier.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param commonList
	 *            (List<T> commonList(mandatory))
	 * @return Map<Integer, List<T>>
	 */
	public <T extends CmPricing> Map groupingTier(List<T> commonList) {

		Map<Integer, List<T>> tierMap = new HashMap<Integer, List<T>>();
		List<T> tierList = new ArrayList<T>();
		for (T tier : commonList) {
			if (tierMap.containsKey(tier.getStand_group_id())) {
				tierList.add(tier);
				tierMap.put(tier.getStand_group_id(), tierList);
			} else {

				tierList = new ArrayList<T>();
				tierList.add(tier);
				tierMap.put(tier.getStand_group_id(), tierList);
			}
		}
		if (!tierMap.isEmpty() && tierMap.size() > 0) {
			Iterator itMap = tierMap.entrySet().iterator();

			while (itMap.hasNext()) {
				Map.Entry entry = (Map.Entry) itMap.next();
				List<T> dataList = (List) entry.getValue();

				CmPricing[] obj = new CmPricing[dataList.size()];
				for (int i = 0; i < dataList.size(); i++) {
					int count = 0;
					for (int j = 0; j < dataList.size(); j++) {

						if (dataList.get(i).getFromQty() > dataList.get(j)
								.getFromQty()) {
							count = count + 1;
						}
					}
					obj[count] = dataList.get(i);
				}

				List<T> sortedList = new ArrayList<T>();
				for (int i = 0; i < obj.length; i++) {
					sortedList.add((T) obj[i]);
				}

				for (T sorted : sortedList) {
					if(sorted!= null && sorted.getFromQty() != null)
						log.info("sorted:" + sorted.getFromQty());
				}

				tierMap.put(dataList.get(0).getStand_group_id(), sortedList);
			}

		}
		return tierMap;

	}

	/**
	 * grouping Date.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param commonList
	 *            (List<T> commonList(mandatory))
	 * @return Map<Date, List<T>>
	 */
	public <T extends CmPricing> Map groupingDate(List<T> commonList) {

		Map<Date, List<T>> tierMap = new HashMap<Date, List<T>>();
		List<T> tierList = new ArrayList<T>();
		for (T tier : commonList) {
			if (tierMap.containsKey(tier.getStartDate())) {
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			} else {

				tierList = new ArrayList<T>();
				tierList.add(tier);
				tierMap.put(tier.getStartDate(), tierList);
			}
		}

		return tierMap;

	}

	/**
	 * grouping Start Date.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param tierMap
	 *            (Map<Integer, List<T>> tierMap(mandatory))
	 * @return List<Object>
	 */
	public <T extends CmPricing> List groupingStartDate(
			Map<Integer, List<T>> tierMap) {

		List<Object> sortedList = new ArrayList<Object>();
		if (!tierMap.isEmpty() && tierMap.size() > 0) {
			Set keyset = tierMap.keySet();

			Object[] keysetarray = keyset.toArray();
			Object[] obj = new Object[keysetarray.length];
			log.info("keysetarray.length:" + keysetarray.length);
			for (int i = 0; i < keysetarray.length; i++) {
				System.out
				.println("******key:" + keysetarray[i]);
				//				int count = 0;
				//				for (int j = 0; j < keysetarray.length; j++) {
				//					log.info("**************in****************");
				//					if (!keysetarray[i].equals(keysetarray[j])) {
				//						count = count + 1;
				//					}
				//
				//				}
				//				log.info("count:" + count);
				log.info("i:" + i);
				List<T> dataList = (List) tierMap.get(keysetarray[i]);
				obj[i] = dataList;

			}

			for (int i = 0; i < obj.length; i++) {
				sortedList.add(obj[i]);
			}

		}
		return sortedList;
	}

	/**
	 * sort By Start Date.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param commonList
	 *           List<T> commonList
	 * @return List<T>
	 */
	public <T extends CmPricing> List sortByStartDate(List<T> commonList) {

		SimpleDateFormat formot = new SimpleDateFormat("YYYYMMdd");

		CmPricing[] obj = new CmPricing[commonList.size()];
		for (int i = 0; i < commonList.size(); i++) {
			int count = 0;
			for (int j = 0; j < commonList.size(); j++) {

				if (formot.format(commonList.get(i).getStartDate()).compareToIgnoreCase(formot.format(commonList.get(j).getStartDate())) > 0) {
					count = count + 1;
				}
			}
			if(count != 0)
				obj[count] = commonList.get(i);
			else
				obj[i] = commonList.get(i);
		}

		List<T> sortedList = new ArrayList<T>();
		for (int i = 0; i < obj.length; i++) {
			sortedList.add((T) obj[i]);
		}

		for (T sorted : sortedList) {
			if(sorted!= null)
				log.info("sorted:" + sorted.getStartDate());
		}

		return sortedList;

	}

	/**
	 * adds SupplierItem To Contract Request.
	 * 
	 * @param supplierItem
	 *           (VCmSupplierItem supplierItem(mandatory))
	 * @return string(supplierItemView)
	 */
	public String addToContractRequest(VCmSupplierItem supplierItem) {

		log.info("************getContractNo***************"
				+ supplierItem.getContractNo());

		this.getManagedBean().setTempSupplierList(null);
		this.getManagedBean().getTempSupplierList().add(supplierItem);

		String contractNo = "";
		boolean flag = false;

		if (this.getManagedBean().getCtRequestList() != null
				&& this.getManagedBean().getCtRequestList().size() > 0) {
			for (VCmSupplierItem item : this.getManagedBean()
					.getCtRequestList()) {
				if (!item.getContractNo().equalsIgnoreCase(
						supplierItem.getContractNo())) {
					System.out
					.println("********************different Contact No.");

					flag = true;
					contractNo = item.getContractNo();
				}
			}
		}

		if (flag) {
			throw new EpException(MessageConst.ERROR_740009, new String[]{contractNo});
		} else {
			log.info("size:"
					+ this.getManagedBean().getTempSupplierList().size());

			for (VCmSupplierItem temp : this.getManagedBean()
					.getTempSupplierList()) {
				log.info("temp.getDocId()" + temp.getDocId());
				log.info("keySet:"
						+ this.getManagedBean().getTempMap().keySet());
				if (!this.getManagedBean().getTempMap()
						.containsKey(temp.getDocId())) {
					this.getManagedBean().getCtRequestList().add(temp);
					this.getManagedBean().getTempMap()
					.put(temp.getDocId(), temp.getDocId());
				}
			}
		}

		return "supplierItemView";

	}

	/**
	 * show Table View.
	 */
	public void showTableView() {

		this.getManagedBean().setRenderTableView(true);
		this.getManagedBean().setRenderGridView(false);
		// clear list of selectedSupplierItem and ctRequestList
		this.getManagedBean().setSelectViewItems(null);
		this.getManagedBean().setCtRequestList(null);
	}

	/**
	 * show Grid View.
	 */
	public void showGridView() {

		this.getManagedBean().setRenderGridView(true);
		this.getManagedBean().setRenderTableView(false);
		// clear list of selectedSupplierItem
		this.getManagedBean().setSelectViewItems(null);
		this.getManagedBean().setCtRequestList(null);
	}

	/**
	 * sets Selected Item.
	 * 
	 * @param supplierItem
	 *            (VCmSupplierItem supplierItem(mandatory))
	 */
	public void setAsSelectedItem(VCmSupplierItem supplierItem) {
		if (this.getManagedBean().getSelectedIds().get(supplierItem.getDocId())) {
			this.getManagedBean().getSelectViewItems().add(supplierItem);
		} else {
			this.getManagedBean().getSelectViewItems().remove(supplierItem);
		}
	}

	// public void checkboxChanged(AjaxBehaviorEvent event){
	// if(((SelectBooleanCheckbox)event.getSource()).isSelected()){
	// add obj to list
	// }else{
	// remove from list
	// }
	//
	// }

	/**
	 * delete Image.
	 * 
	 * @param index
	 *           (int index(mandatory))
	 */
	public void deleteImage(int index) {

		this.getManagedBean().getSelectedMaintainItem().getCmImages()
		.remove(index);

	}

	/**
	 * This method is used to Navigate contract request.
	 * 
	 * @return String(contractRequestView)
	 */
	public String navigateContractRequest() {

		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);

		if (this.getManagedBean().getCtRequestList() != null && this.getManagedBean().getCtRequestList().size() > 0) {
			VCmSupplierItem supplierItemView = this.getManagedBean().getCtRequestList().get(0);
			ContractReqDTO reqDTO = new ContractReqDTO();

			reqDTO.setContractId(supplierItemView.getContractId());
			log.info("supplierItemView.getContractId():"+ supplierItemView.getContractId());
			ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);

			if (resDTO.getContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				log.info("*************FULFILMENT_TYPE_ONE_OFF**************");
				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(
								MessageConst.INFO_710006,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

			} else {
				log.info("*************not one off**************");

			}
			resDTO = contractSO.retrieveAvailableSchedulePhaseNo(reqDTO);
			if(resDTO.getAvailableNoList() != null){
				getManagedBean().setAvailableNoList(resDTO.getAvailableNoList());
			}

		}

		return "contractRequestView";
	}

	/**
	 * back To Catalogue Search.
	 * 
	 * @return String(supplierItemView?faces-redirect=true)
	 */
	public String backTocatalogueSearch() {

		return "supplierItemView?faces-redirect=true";
	}

	/**
	 * this Method Is Used To Delete Contract Request Items.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteContractRequestItem(int index) {

		VCmSupplierItem supplierItemView = this.getManagedBean()
				.getCtRequestList().get(index);

		this.getManagedBean().getTempMap().remove(supplierItemView.getDocId());

		this.getManagedBean().getCtRequestList().remove(supplierItemView);

	}

	/**
	 * navigate To CR.
	 * 
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 */
	public void navigateToCR() throws PortalException, SystemException {
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);

		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())
						+ themeDisplay.getLayout().getGroup().getFriendlyURL()
						.length());
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.getApplicationMap().put(facesScope.getUserSessionBean().getUserId().toString()+getManagedBean().getCtRequestList().get(0)
				.getContractId().toString(), getManagedBean().getCtRequestList());
		String url = "contract-request-from-catalogue";

		try {
			if(getManagedBean().getScheduleNo() != null){
				externalContext.redirect(commonUrl
						+ "/"
						+ url
						+ "?contractId="
						+ this.getManagedBean().getCtRequestList().get(0)
						.getContractId()
						+ "&scheduleNo="
						+ this.getManagedBean().getScheduleNo());
			}else{
				externalContext.redirect(commonUrl
						+ "/"
						+ url
						+ "?contractId="
						+ this.getManagedBean().getCtRequestList().get(0)
						.getContractId());
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * back To Public Catalogue.
	 * 
	 * @return string(publicCatalogueView)
	 */
	public String backToPublicCatalogue(){
		log.info("..................cancelToPublicSearch..............");
		return "publicCatalogueView?faces-redirect=true";
	}

	/**
	 * This method is used to Reset catalogue search.
	 */
	public void resetCatalogueSearch() {

		renewSesionObject();

		this.getManagedBean().setAdvanceSearchList(
				new ArrayList<AdvanceSearchVO>());
		for (int i = 0; i < 3; i++) {
			this.getManagedBean().getAdvanceSearchList()
			.add(new AdvanceSearchVO());
		}

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		try{
			StateReqDTO reqStateDTO = new StateReqDTO();
			reqStateDTO.setCountryCode("MY");
			StateResDTO resStateDTO = stateSO.retriveStatesListByCountryCode(reqStateDTO);

			this.getManagedBean().setStateList(resStateDTO.getStateList());
		}finally {
			stateSO = null;
		}
	}

	/**
	 * adds CmSupplierItems To The List.
	 * 
	 * @param supplierItem
	 *            (VCmSupplierItem supplierItem(mandatory))
	 * @param optionListValue
	 *            (Integer optionListValue(mandatory))
	 */
	public void addToList(VCmSupplierItem supplierItem,Integer optionListValue){
		log.info("addToList trigger");
		if(optionListValue.intValue()==1){
			if (!this.getManagedBean().getCtRequestList().contains(supplierItem)){
				log.info("************getContractNo***************"
						+ supplierItem.getContractNo());

				this.getManagedBean().setTempSupplierList(null);
				this.getManagedBean().getTempSupplierList().add(supplierItem);

				String contractNo = "";
				boolean flag = false;

				if (this.getManagedBean().getCtRequestList() != null && this.getManagedBean().getCtRequestList().size() > 0) {
					for (VCmSupplierItem item : this.getManagedBean().getCtRequestList()) {
						if (!item.getContractNo().equalsIgnoreCase(supplierItem.getContractNo())) {
							log.info("********************different Contact No.");

							flag = true;
							contractNo = item.getContractNo();
							break;
						}
					}
				}

				if (flag) {
					throw new EpException(MessageConst.ERROR_740009, new String[]{contractNo});
				} else {
					log.info("size:" + this.getManagedBean().getTempSupplierList().size());

					for (VCmSupplierItem temp : this.getManagedBean().getTempSupplierList()) {
						log.info("temp.getDocId()" + temp.getDocId());
						log.info("keySet:" + this.getManagedBean().getTempMap().keySet());
						if (!this.getManagedBean().getTempMap().containsKey(temp.getDocId())) {
							this.getManagedBean().getCtRequestList().add(temp);
							this.getManagedBean().getTempMap().put(temp.getDocId(), temp.getDocId());
						}
					}
				}
			}
			else{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_715054,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
			}
		}else if(optionListValue.intValue()==3){
			boolean found = false;
			for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
				if(vCmSupplierItem.getDocId()==supplierItem.getDocId()){
									
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							null, MessageUtils.getMessage(MessageConst.ERROR_715054, new String[]{vCmSupplierItem.getItemName()},MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					found = true;
					break;
				}
			}
			if(!found)
				this.getManagedBean().getSelectViewItems().add(supplierItem);
		}
	}

	/**
	 * This Method is used to select the particular row .
	 * 
	 * @param event
	 *            (SelectEvent event(mandatory))
	 */
	public void onRowSelect(SelectEvent event) {
		VCmSupplierItem supplierItem = ((VCmSupplierItem)event.getObject());
		boolean found = false;

		for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
			if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(MessageConst.ERROR_715054,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				found = true;
				break;
			}
		}

		if(!found)
			this.getManagedBean().getSelectViewItems().add(supplierItem);


	}

	/**
	 * on Row Unselect.
	 * 
	 * @param event
	 *            (UnselectEvent event(mandatory))
	 */
	public void onRowUnselect(UnselectEvent event) {
		VCmSupplierItem supplierItem = ((VCmSupplierItem)event.getObject());
		log.info("onRowDeselect trigger");
		for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
			if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
				this.getManagedBean().getSelectViewItems().remove(vCmSupplierItem);
				break;
			}
		}
	}

	/**
	 * Removes the SupplierItem from list.
	 * 
	 * @param supplierItem
	 *            (VCmSupplierItem supplierItem(mandatory))
	 */
	public void removeFromList(VCmSupplierItem supplierItem){
		log.info("removeFromList trigger");
		for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
			if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
				this.getManagedBean().getSelectViewItems().remove(vCmSupplierItem);
				break;
			}
		}
	}

	/**
	 * This method is used to navigate the previous screens .
	 * 
	 * @return string
	 */
	public String navigate(){
		int a = this.getManagedBean().getPreviousScreen();
		if (a==0)//0=catSearch
			return cancelToSupplierItemSearch();
		else if (a==1)//;1=catMaintenance
			return cancelToMaintenanceFile();
		else if (a==2)//2=pendingTask
			return cancelToPendingApprovalItem();
		else
			return cancelToSupplierItemSearch();
	}

	/**
	 * This method is used to Show active promotion.
	 * 
	 * @param cmSupplierItem
	 *            (CmSupplierItem cmSupplierItem(mandatory))
	 * @return true, if successful
	 */
	public boolean showActivePromotion(CmSupplierItem cmSupplierItem){
		log.info("showActivePromotion");
		log.info(cmSupplierItem.getCmPromotionPricing().getStartDate());
		log.info(cmSupplierItem.getCmPromotionPricing().getEndDate());
		if(cmSupplierItem.getCmPromotionPricing().getStartDate()!=null && cmSupplierItem.getCmPromotionPricing().getEndDate()!=null){
			if(!(cmSupplierItem.getCmPromotionPricing().getStartDate().getTime()<=new Date().getTime() && cmSupplierItem.getCmPromotionPricing().getEndDate().getTime()>=new Date().getTime())){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	/**
	 * This method is used to navigate the pending task page.
	 * 
	 * @return String(/xhtml/cm/catalogue/catalogueTaskList.xhtml)
	 */
	public String cancelToPendingApprovalItem() {

		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";
	}

	/**
	 * Gets the status filter bean.
	 * 
	 * @return SearchFilterBean
	 */
	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	/**
	 * Sets the status filter bean.
	 * 
	 * @param statusFilterBean
	 *                       SearchFilterBean statusFilterBean(mandatory)
	 *            
	 */
	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}

	/**
	 * Gets the root.
	 * 
	 * @return TreeNode
	 */
	public TreeNode getRoot() {
		return root;
	}

	/**
	 * Sets the root.
	 * 
	 * @param root
	 *           (TreeNode root(mandatory))
	 */
	public void setRoot(TreeNode root) {
		this.root = root;
	}

	/**
	 * Gets the root1.
	 * 
	 * @return TreeNode
	 */
	public TreeNode getRoot1() {
		return root1;
	}

	/**
	 * Sets the root1.
	 * 
	 * @param root1
	 *           (TreeNode root1(mandatory))
	 */
	public void setRoot1(TreeNode root1) {
		this.root1 = root1;
	}

	/**
	 * Gets the root2.
	 * 
	 * @return TreeNode
	 */
	public TreeNode getRoot2() {
		return root2;
	}

	/**
	 * Sets the root2.
	 * 
	 * @param root2
	 *            TreeNode(mandatory)
	 */
	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	/**
	 * Gets the selected node.
	 * 
	 * @return TreeNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets the selected node.
	 * 
	 * @param selectedNode
	 *            (TreeNode selectedNode(mandatory))
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            (StreamedContent file(mandatory))
	 */
	public void setFile(StreamedContent file) {
		this.file = file;
	}

	/**
	 * Return the all Supplier Item's status.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getItemStatusOptions() {
		List<SelectItem> options = new ArrayList<SelectItem>();

		options.add(new SelectItem(CmConst.ITEM_STATUS_DRAFT, "Draf"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_PENDING_APPROVAL, "Menunggu Kelulusan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_APPROVED, "Diluluskan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_REJECTED, "Ditolak"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_PUBLISHED, "Diiklankan"));
		options.add(new SelectItem(CmConst.ITEM_STATUS_EXPIRED, "Tamat Tempoh"));

		return options;
	}

	public void setCatTypeAndTabIndex(String catType, int tabIndex) {
		this.getManagedBean().setResultTabActiveIndex(tabIndex);
		if(StringUtils.isNotBlank(catType)) {
			this.getManagedBean().getCatTypeFilterBean().setSearchValue(catType);
		} else {
			this.getManagedBean().getCatTypeFilterBean().setSearchValue(null);
		}
	}

	public String supplierTempUploadedMsg(){
		FacesScopes fs = new FacesScopes();
		String hostName = fs.getRequest().getServerName();
		int portNo = fs.getRequest().getServerPort();

		String softCertPath = "supptemp-request-soft-cert";
		String url = "http://" + hostName + ":" + String.valueOf(portNo) + "/web/epapp/" + softCertPath;

		log.info("*******Str Url**********" + url);
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				null, MessageUtils.getMessage(MessageConst.INFO_721030, new String[]{url},MessageUtils.BUNDLE_COMMON_MESSAGES, fs.getUserLocale())));


		return "/xhtml/sm/postsupplierregistration/catalogueUploadAcknowlegdement.xhtml";
	}

	private Long getSupplierId() {
		if(this.getManagedBean().getSupplierId() == null) {
			ISupplierSearchSO supplierSearchSO = null;
			try {
				supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());
				SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
				this.getManagedBean().setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());

			} finally {
				supplierSearchSO = null;
			}
		}
		return this.getManagedBean().getSupplierId();
	}



	public String navigateToCatalogDetForTempSupp(int index) {

		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + this.getManagedBean().getDraftSupplierItemList().get(index).getSupplierItemId());
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);

		this.getManagedBean().setPreviousScreen(1);//catMaintenance

		try{
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setSupplierItemId(this.getManagedBean().getDraftSupplierItemList().get(index).getSupplierItemId());
			CatalogueResDTO resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
			reqDTO.setSupplierId(resDTO.getSupplierItem().getSupplierId());

			log.info("resDTO.getSupplierItem().getSupplierId()" + resDTO.getSupplierItem().getSupplierId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			supplierReqDTO.setAllStatuses(true);

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			log.info("getSmSupplier"
					+ smResDTO.getResultBean().getResultList().get(0));

			retriveSupplierGstInfomation( smResDTO.getResultBean().getResultList().get(0));

			CmSupplierItem clone = (CmSupplierItem) SerializationUtils.clone(this.getManagedBean().getDraftSupplierItemList().get(index));

			this.getManagedBean().setCloneCmSupplierItem(clone);
			this.getManagedBean().setCloneIndex(index);

			ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();
			bb.getManagedBean().setCatDetTabActiveIndex(0);

			//reqDTO.setSupplierItemId(this.getManagedBean().getMaintainItemList().get(index).getSupplierItemId());
			//resDTO = catalogueSO.retrieveSupplierItemById(reqDTO);
			this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItem());
			this.getManagedBean()
			.getSelectedMaintainItem()
			.setCompareDesc(resDTO.getSupplierItem().getItemDesc());

			//sort Tier
			if(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings(), "S");
			}
			//sort Tier	Promotion		
			if(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings(), "P");
			}
			//sort Zonal Tier			
			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings(), "S");
			}
			//sort Zonal Tier Promotion
			if(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings() != null && !this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings().isEmpty()){
				groupPricesByTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings(), "P");
			}

			List<CmZonalPromotionTierPricing> zonalPromotionTierPricings = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalPromotionTierPricings();
			if (zonalPromotionTierPricings != null && zonalPromotionTierPricings.size() > 0) {
				for (CmZonalPromotionTierPricing cmPricing : zonalPromotionTierPricings) {
					if (cmPricing.getParentPrice() != null) {
						cmPricing.getParentPrice().setStandardDisabled(true);
						cmPricing.setStandardDisabled(true);
					}
				}
			}

			List<CmPromotionTierPricing> cmList = this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings();
			if (cmList != null && cmList.size() > 0) {
				for (CmPromotionTierPricing cmPricing : cmList) {
					if (cmPricing.getParentPrice() != null) {
						cmPricing.getParentPrice().setStandardDisabled(true);
						cmPricing.setStandardDisabled(true);
					}
				}
			}

			this.getManagedBean().getSelectedMaintainItem().getCmPromotionPricing()
			.setStandardDisabled(true);

			this.getManagedBean().setDisplayCatalogue(true);
			updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
			this.getManagedBean().setPricingTabIndex(0);

			//		} catch (Exception e) {
			//			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}	

		//return "addCatalogItem";
		return "editCatalogItem";

	}



	public String maintainKeyword(){




		CodificationBackingBean bb = new CodificationBackingBean();
		bb.getManagedBean().setKeywordItem(null);

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);



		try{



			CodificationReqDTO codeReqDTO = new CodificationReqDTO();

			log.debug("maintain keyword:" + bb.getManagedBean().getSelectedKeywordObject());

			bb.getManagedBean().setExtensionVoList(null);

			ExtensionCodeVO extCodeVO = new ExtensionCodeVO();
			Object[] objects = (Object[]) bb.getManagedBean().getSelectedKeywordObject();

			extCodeVO.setTypeId(Long.valueOf(String.valueOf(objects[0])));
			extCodeVO.setBrandId(Long.valueOf(String.valueOf(objects[1])));
			extCodeVO
			.setMeasurementId(Long.valueOf(String.valueOf(objects[2])));
			extCodeVO.setColorId(Long.valueOf(String.valueOf(objects[3])));

			log.info("******type NAme******" + String.valueOf(objects[4]));
			extCodeVO.setTypeName(String.valueOf(objects[4]));
			extCodeVO.setBrandName(String.valueOf(objects[5]));
			extCodeVO.setMeasurementName(String.valueOf(objects[6]));
			extCodeVO.setColorName(String.valueOf(objects[7]));

			bb.getManagedBean().getExtensionVoList().add(extCodeVO);



			if (bb.getManagedBean().getExtensionVoList() != null
					&& bb.getManagedBean().getExtensionVoList().size() > 0) {
				for (int i = 0; i < bb.getManagedBean().getExtensionVoList().size(); i++) {

					ExtensionCodeVO obj = bb.getManagedBean().getExtensionVoList()
							.get(i);

					codeReqDTO.setBrandId(obj.getBrandId());
					codeReqDTO.setMeasurementId(obj.getMeasurementId());
					codeReqDTO.setTypeId(obj.getTypeId());
					codeReqDTO.setColorId(obj.getColorId());

					log.info("........obj.getBrandId()="+obj.getBrandId() + 
							"\n ........obj.obj.getMeasurementId()="+obj.getMeasurementId() +
							"\n ........obj.getTypeId()="+obj.getTypeId() +
							"\n ........obj.getColorId()="+obj.getColorId());

					CodificationResDTO codeResDTO = codificationSO
							.searchItem(codeReqDTO);

					if (codeResDTO.getItemList() != null
							&& codeResDTO.getItemList().size() > 0) {

						for (CmItem item : codeResDTO.getItemList()) {
							bb.getManagedBean().setKeywordItem(item);
							CmKeyword keyword = new CmKeyword();

							if(bb.getManagedBean().getKeywordItem().getCmKeyword().size()==0){

								bb.getManagedBean().getKeywordItem().getCmKeyword().add(keyword);
							}


						}

					} else {
						log.debug("************else*******************");
						CmType typeObj = bb.searchTypeOjbyTypeId(obj.getTypeId());
						CmBrand brandObj = bb.searchBrandObjByBrandId(obj
								.getBrandId());
						CmMeasurement meaObj = bb
								.searchMeasurementByMeasurementId(obj
										.getMeasurementId());
						CmColor colorObj = bb.searchColorObjByColorId(obj
								.getColorId());

						codeReqDTO.setUnspscCode(bb.getManagedBean()
								.getSelectedUNSPSCObj().getUnspscCode());
						codeResDTO = codificationSO.searchCommodityObj(codeReqDTO);

						CmItem item = new CmItem();
						item.setCmUnspsc(codeResDTO.getCommodityObj());
						item.setCmType(typeObj);
						item.setCmBrand(brandObj);
						item.setCmMeasurement(meaObj);
						item.setCmColor(colorObj);
						item.setItemName(bb.getManagedBean().getSelectedUNSPSCObj()
								.getUnspscTitle()
								+ (typeObj.getTypeName().equals("N/A") ? "": " "+typeObj.getTypeName())
								+ (brandObj.getBrandName().equals("N/A") ? "": " "+brandObj.getBrandName())
								+ (meaObj.getMeasurementName().equals("N/A") ? "" : " "+meaObj.getMeasurementName())
								+ (colorObj.getColorName().equals("N/A") ? "": " "+colorObj.getColorName()));
						item.setExtensionCode(bb.getManagedBean()
								.getSelectedUNSPSCObj().getUnspscCode()
								+ typeObj.getTypeCode()
								+ brandObj.getBrandCode()
								+ meaObj.getMeasurementCode()
								+ colorObj.getColorCode());

						log.info("item Name:" + item.getItemName());
						codeReqDTO.setCmItem(item);
						codeResDTO = codificationSO.saveNewItem(codeReqDTO);


						bb.getManagedBean().setKeywordItem(codeResDTO.getCmItem());
						CmKeyword keyword = new CmKeyword();

						bb.getManagedBean().getKeywordItem().getCmKeyword().add(keyword);

					}

				}

			}

			bb.getManagedBean().setProductKeyword(true);
			bb.getManagedBean().setServiceKeyword(false);



		}finally {

			codificationSO = null;
		}


		return "maintainKeywordDetail?faces-redirect=true;";

	}



	public String addServiceToKeyword(CmUnspsc unspsc) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);




		CodificationBackingBean bb = new CodificationBackingBean();
		bb.getManagedBean().setKeywordItem(null);

		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		log.info("..............bb.getManagedBean().getSelectedUNSPSCObj()="+unspsc);


		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setUnspscCode(unspsc.getUnspscCode());
		CodificationResDTO resDTO = unspscSO
				.retrieveUNSPSCByCommodityCode(reqDTO);

		if (resDTO.isSuccessful()) {
			CmUnspsc commodity = resDTO.getCommodityObj();

			// this.getManagedBean().getSelectedUNSPSCObj().setUnspscCode(commodity.getUnspscCode());
			// this.getManagedBean().getSelectedUNSPSCObj().setUnspscTitle(commodity.getUnspscTitle());
			bb.getManagedBean().setSelectedUNSPSCObj(commodity);

			bb.getManagedBean().setClassCode(
					commodity.getCmUnspsc().getUnspscCode());
			bb.getManagedBean().setClassTitle(
					commodity.getCmUnspsc().getUnspscTitle());

			bb.getManagedBean().setFamilyCode(
					commodity.getCmUnspsc().getCmUnspsc().getUnspscCode());
			bb.getManagedBean().setFamilyTitle(
					commodity.getCmUnspsc().getCmUnspsc().getUnspscTitle());

			bb.getManagedBean().setSegmentCode(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
					.getUnspscCode());
			bb.getManagedBean().setSegmentTitle(
					commodity.getCmUnspsc().getCmUnspsc().getCmUnspsc()
					.getUnspscTitle());
		}

		if (unspsc.getUnspscCode() != null) {

			codeReqDTO.setUnspscId(unspsc
					.getUnspscId());
			CodificationResDTO codeResDTO = codificationSO
					.searchItem(codeReqDTO);
			log.info(">>>>>>>>>>>>>>>>>codeResDTO.getItemList()="+codeResDTO.getItemList());
			if (codeResDTO.getItemList() != null
					&& codeResDTO.getItemList().size() > 0) {

				CmItem item = codeResDTO.getItemList().get(0);
				bb.getManagedBean().setKeywordItem(item);
				CmKeyword keyword = new CmKeyword();

				if(bb.getManagedBean().getKeywordItem().getCmKeyword().size()==0){

					bb.getManagedBean().getKeywordItem().getCmKeyword().add(keyword);
				}


			} else {
				log.info(">>>>>>>>>>>>>>>>else>>>>>>>>>coudnt find cmItem");

				CmType typeObj = null;
				if(unspsc.getCmType()!=null && unspsc.getCmType().size()>0){
					for(CmType a : unspsc.getCmType()){
						if(a.getTypeCode().equals("000")){
							typeObj = a;
							break;
						}
					}

				}else{
					typeObj = new CmType();
					typeObj.setTypeCode("000");
					typeObj.setTypeName("N/A");
					typeObj.setUnspscId(unspsc);
					codeReqDTO.setCmType(typeObj);
					typeObj = codificationSO.addNewType(codeReqDTO).getCmType();
				}

				CmBrand brandObj = null;
				if(unspsc.getCmBrand()!=null && unspsc.getCmBrand().size()>0)
					brandObj = unspsc.getCmBrand().get(0);
				else{
					brandObj = new CmBrand();
					brandObj.setBrandCode("00");
					brandObj.setBrandName("N/A");
					brandObj.setUnspscId(unspsc);
					codeReqDTO.setCmBrand(brandObj);
					brandObj = codificationSO.addNewBrand(codeReqDTO).getCmBrand();
				}

				CmMeasurement meaObj = null;
				if(unspsc.getCmMeasurement()!=null && unspsc.getCmMeasurement().size()>0)
					meaObj = unspsc.getCmMeasurement().get(0);
				else{
					meaObj = new CmMeasurement();
					meaObj.setMeasurementCode("000");
					meaObj.setMeasurementName("N/A");
					meaObj.setUnspscId(unspsc);
					codeReqDTO.setCmMeasurement(meaObj);
					meaObj = codificationSO.addNewMeasurement(codeReqDTO).getCmMeasurement();
				}

				CmColor colorObj = bb.searchColorObjByColorId(1L);//first record of table reserved for NA

				codeReqDTO.setUnspscCode(unspsc.getUnspscCode());

				CmItem item = new CmItem();
				item.setCmUnspsc(unspsc);
				item.setCmType(typeObj);
				item.setCmBrand(brandObj);
				item.setCmMeasurement(meaObj);
				item.setCmColor(colorObj);
				item.setItemName(unspsc.getUnspscTitle());//because for service extension parts are NA, just leave empty.
				item.setExtensionCode(bb.getManagedBean()
						.getSelectedUNSPSCObj().getUnspscCode()
						+ typeObj.getTypeCode()
						+ brandObj.getBrandCode()
						+ meaObj.getMeasurementCode() + colorObj.getColorCode());

				log.info("item Name:" + item.getItemName());
				log.info("Extension Code:" + item.getExtensionCode());
				codeReqDTO.setCmItem(item);
				codeResDTO = codificationSO.saveNewItem(codeReqDTO);

				bb.getManagedBean().setKeywordItem(codeResDTO.getCmItem());
				CmKeyword keyword = new CmKeyword();

				bb.getManagedBean().getKeywordItem().getCmKeyword().add(keyword);
			}

		}

		bb.getManagedBean().setProductKeyword(false);
		bb.getManagedBean().setServiceKeyword(true);


		return "maintainKeywordDetail?faces-redirect=true;";

	}
	/**
	 * get Image size for validation
	 * 
	 * @return Integer size
	 */

	public Integer getImageSize(){


		Integer size = 1024 * CmHelper.getInstance().getMaxCategoryAllowed();

		return size;
	}


	/**
	 * Invalid file size msg
	 * 
	 * @return String msg
	 */
	public String getInvalidFileSize(){

		String msg = "";
		Locale locale = new FacesScopes().getUserLocale();

		msg = ResourceBundleUtils.getMessage("lbl.cm.invalid.file.size", new String[] { ""+CmHelper.getInstance().getMaxCategoryAllowed()} ,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);

		return msg;
	}


	/**
	 * clone selected supplier item for resolving datatable multiple selection issue.
	 * 
	 * @param i
	 */

	public void cloneItemSelection(int i){

		this.getManagedBean().setRefreshDraftSuppItemFlag(true);
		int index = i%10;
		System.out.println("*******getCloneCmSupplierItemLs*************"+this.getManagedBean().getCloneCmSupplierItemLs().size());


		CmSupplierItem suppItem =  this.getManagedBean().getDraftSearchDataModel().getList().get(index);
		System.out.println("getSupplierItemId"+suppItem.getSupplierItemId());




		if(this.getManagedBean().getCloneCmSupplierItemLs()!=null && this.getManagedBean().getCloneCmSupplierItemLs().size()>0){

			boolean addInLs = true;
			for(CmSupplierItem obj : this.getManagedBean().getCloneCmSupplierItemLs()){

				if(obj.getSupplierItemId() == suppItem.getSupplierItemId() && !suppItem.isSelected()){
					this.getManagedBean().getCloneCmSupplierItemLs().remove(obj);
					addInLs = false;
					break;
				}



			}
			System.out.println("addInLs:"+addInLs);
			if(addInLs){
				this.getManagedBean().getCloneCmSupplierItemLs().add(suppItem);
			}

		}else{
			this.getManagedBean().getCloneCmSupplierItemLs().add(suppItem);
		}

		System.out.println("*******size*************"+this.getManagedBean().getCloneCmSupplierItemLs().size());




		int count =0;
		Map tempMap = new HashMap();
		for (int j = 0; j < this.getManagedBean().getDraftSearchDataModel().getList().size(); j++) {
			CmSupplierItem suppItemJ =  this.getManagedBean().getDraftSearchDataModel().getList().get(j);

			if(this.getManagedBean().getCloneCmSupplierItemLs()!=null && this.getManagedBean().getCloneCmSupplierItemLs().size()>0){

				for(CmSupplierItem obj : this.getManagedBean().getCloneCmSupplierItemLs()){

					if(suppItemJ.getSupplierItemId()==obj.getSupplierItemId()){

						if(!tempMap.containsKey(obj.getSupplierItemId())){
							count = count + 1;
							System.out.println("**loop***"+count);
							tempMap.put(obj.getSupplierItemId(), obj.getSupplierItemId());
						}
					}
				}

			}

		}
		if(count == this.getManagedBean().getDraftSearchDataModel().getList().size()){
			this.getManagedBean().setSupplierItemcheckBoxAll(true);
		}else{
			this.getManagedBean().setSupplierItemcheckBoxAll(false);
		}



		System.out.println("isSupplierItemcheckBoxAll:"+this.getManagedBean().isSupplierItemcheckBoxAll());

	}



	/**
	 * clone select all supplier item for resolving datatable multiple selection issue.
	 */
	public void cloneAllSupplierItemSelection(){
		this.getManagedBean().setRefreshDraftSuppItemFlag(true);
		Map checkDuplicateAdd = new HashMap();
		for (int i = 0; i < this.getManagedBean().getDraftSearchDataModel().getList().size(); i++) {



			CmSupplierItem suppItem = this.getManagedBean().getDraftSearchDataModel().getList().get(i);


			if(this.getManagedBean().isSupplierItemcheckBoxAll()){
				suppItem.setSelected(true);

			}else{
				suppItem.setSelected(false);

			}


			if(this.getManagedBean().getCloneCmSupplierItemLs()!=null && this.getManagedBean().getCloneCmSupplierItemLs().size()>0){

				boolean addInLs = true;
				for(CmSupplierItem obj : this.getManagedBean().getCloneCmSupplierItemLs()){

					if(!checkDuplicateAdd.containsKey(obj.getSupplierItemId())){
						checkDuplicateAdd.put(obj.getSupplierItemId(), obj.getSupplierItemId());
					}

					if(!suppItem.isSelected() && suppItem.getSupplierItemId()==obj.getSupplierItemId()){
						this.getManagedBean().getCloneCmSupplierItemLs().remove(obj);
						addInLs = false;
						break;
					}

				}
				System.out.println("addInLs:"+addInLs);
				if(addInLs){
					System.out.println("**add in****"+suppItem.getSupplierId());
					System.out.println("checkDuplicateAdd:"+checkDuplicateAdd);
					if(!checkDuplicateAdd.containsKey(suppItem.getSupplierItemId())){
						this.getManagedBean().getCloneCmSupplierItemLs().add(suppItem);
					}
				}

			}else{

				if(suppItem.isSelected()){
					if(!checkDuplicateAdd.containsKey(suppItem.getSupplierItemId())){
						this.getManagedBean().getCloneCmSupplierItemLs().add(suppItem);
					}
				}
			}

			System.out.println("*******size*************"+this.getManagedBean().getCloneCmSupplierItemLs().size());

		}

	}


	public void checkingCheckAll(){

		System.out.println("****final****"+this.getManagedBean().isSupplierItemcheckBoxAll());



		int count =0;
		Map tempMap = new HashMap();
		for (int i = 0; i < this.getManagedBean().getDraftSearchDataModel().getList().size(); i++) {
			CmSupplierItem suppItem = this.getManagedBean().getDraftSearchDataModel().getList().get(i);

			if(this.getManagedBean().getCloneCmSupplierItemLs()!=null && this.getManagedBean().getCloneCmSupplierItemLs().size()>0){

				for(CmSupplierItem suppItemObj : this.getManagedBean().getCloneCmSupplierItemLs()){
					//if((String.valueOf(objects[4])+String.valueOf(objects[5])+String.valueOf(objects[6])+String.valueOf(objects[7])).equalsIgnoreCase(obj.getKey())) {
					if(suppItem.getSupplierItemId()==suppItemObj.getSupplierItemId()){
						if(!tempMap.containsKey(suppItemObj.getSupplierItemId())){
							suppItem.setSelected(true);
							count = count + 1;
							System.out.println("**loop***"+count);
							tempMap.put(suppItemObj.getSupplierItemId(), suppItemObj.getSupplierItemId());
						}
					}
				}

			}

		}
		System.out.println("count:"+count);
		System.out.println("resDTO.getDraftSearchDataModel().getResultList().size():"+ this.getManagedBean().getDraftSearchDataModel().getList().size());
		if(count ==  this.getManagedBean().getDraftSearchDataModel().getList().size()){
			this.getManagedBean().setSupplierItemcheckBoxAll(true);
		}else{
			this.getManagedBean().setSupplierItemcheckBoxAll(false);
		}


		System.out.println("****last****"+this.getManagedBean().isSupplierItemcheckBoxAll());
	}

	private void retriveSupplierGstInfomation(SmSupplier supplier) {
		ISupplierGstSO supplierGstSo = null;
		try {
			if (null != supplier && null != supplier.getSupplierId()) {
				supplierGstSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO supplierGstreqDTO = new SupplierGstReqDTO();
				System.out.println("*******supplier.getSupplierId()**********"+supplier.getSupplierId());
				supplierGstreqDTO.setSupplierId(supplier.getSupplierId());
				supplierGstreqDTO.setBranchCode(null);
				SupplierGstResDTO supplierGstresDTO = supplierGstSo
						.retrieveGstInfoBySupplierIdAndBranchCode(supplierGstreqDTO);
				if (null != supplierGstresDTO
						&& null != supplierGstresDTO.getSmGstSupplier()) {
					this.getManagedBean().setSmGstSupplier(
							supplierGstresDTO.getSmGstSupplier());

				} else {
					this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
				}

			} else {
				this.getManagedBean().setSmGstSupplier(new SmGstSupplier());
			}

		} finally {
			supplierGstSo = null;
		}

	}

}
