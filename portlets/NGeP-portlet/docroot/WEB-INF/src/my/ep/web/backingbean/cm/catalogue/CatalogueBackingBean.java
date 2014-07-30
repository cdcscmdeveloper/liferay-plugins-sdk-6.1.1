package my.ep.web.backingbean.cm.catalogue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmImage;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmTierPricing;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.CodificationBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.sc.directpurchase.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.directpurchase.SimpleQuoteBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.managedbean.cm.catalogue.CatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.ExtensionCodeVO;
import my.ep.web.vo.PricingVO;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "catalogueBackingBean")
//@ViewScoped
@RequestScoped
public class CatalogueBackingBean extends BaseBackingBean<CatalogueManagedBean> {

	private static Logger log = Logger.getLogger(CatalogueBackingBean.class);
	private TreeNode root;
	private TreeNode selectedNode;
	private TreeNode root1;
	private TreeNode root2;
	private StreamedContent file;

	private SearchFilterBean statusFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_EQUALS.value());

	public CatalogueBackingBean() {

		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		/*
		 * this.getManagedBean().setWishLists(null);
		 * 
		 * WishListReqDTO reqDTO = new WishListReqDTO();
		 * 
		 * WishListResDTO resDTO = wishListSO.retrieveWishList(reqDTO);
		 * this.getManagedBean().setWishLists(resDTO.getWishLists());
		 */

		this.getManagedBean().setAdvanceSearchList(
				new ArrayList<AdvanceSearchVO>());
		for (int i = 0; i < 3; i++) {
			this.getManagedBean().getAdvanceSearchList()
					.add(new AdvanceSearchVO());
		}

		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateReqDTO reqStateDTO = new StateReqDTO();
		reqStateDTO.setCountryCode("MY");
		StateResDTO resStateDTO = stateSO.retriveStatesListByCountryCode(reqStateDTO);

		this.getManagedBean().setStateList(resStateDTO.getStateList());

	}

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

		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveSupplierProfiles(supplierReqDTO);

		System.out.println("getSmSupplier"
				+ smResDTO.getResultBean().getResultList().get(0));

		CodificationBackingBean bb = new CodificationBackingBean();

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();
          CmUnspsc commodity = bb.getManagedBean().getSelectedUNSPSCObj();
          System.out.println("..............bb.getManagedBean().getSelectedUNSPSCObj()="+commodity);
		if (commodity.getUnspscCode() != null) {

			codeReqDTO.setUnspscId(commodity
					.getUnspscId());
			CodificationResDTO codeResDTO = codificationSO
					.searchItem(codeReqDTO);
            System.out.println(">>>>>>>>>>>>>>>>>codeResDTO.getItemList()="+codeResDTO.getItemList());
			if (codeResDTO.getItemList() != null
					&& codeResDTO.getItemList().size() > 0) {

				CmItem item = codeResDTO.getItemList().get(0);
				  System.out.println(">>>>>>>>>>>>>>>>>codeResDTO.getItemList().get(0)="+item);
				  System.out.println(">>>>>>>>>>>>>>>>>item.getItemId()="+item.getItemId());
				CmSupplierItem supplierItem = new CmSupplierItem();

				supplierItem.setCmItem(item);
				supplierItem.setItemStatus("D");
				supplierItem.setSmSupplier(smResDTO.getResultBean()
						.getResultList().get(0));
				supplierItem.setRecordStatus(true);
				reqDTO.getSupplierItem().add(supplierItem);

			} else {
				  System.out.println(">>>>>>>>>>>>>>>>else>>>>>>>>>coudnt find cmItem");
				CmType typeObj = commodity.getCmType().get(0);
				CmBrand brandObj = commodity.getCmBrand().get(0);
				CmMeasurement meaObj = commodity.getCmMeasurement().get(0);
				CmColor colorObj = bb.searchColorObjByColorId(1L);//first record of table reserved for NA

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

				System.out.println("item Name:" + item.getItemName());
				System.out.println("Extension Code:" + item.getExtensionCode());
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
							.getMessage("unspsc not selected!", new String[] { ""
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

	public String addToMaintain() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		try{
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
	
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
	
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
	
			if(smResDTO.getResultBean().getResultList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721017);
			}
			
			log.info("getSmSupplier: "
					+ smResDTO.getResultBean().getResultList().get(0));
	
			CodificationBackingBean bb = new CodificationBackingBean();
	
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
	
			System.out.println("addToMaintain:"
					+ bb.getManagedBean().getSelectedObject().length);
	
			bb.getManagedBean().setExtensionVoList(null);
			for (int i = 0; i < bb.getManagedBean().getSelectedObject().length; i++) {
				ExtensionCodeVO extCodeVO = new ExtensionCodeVO();
				Object[] objects = (Object[]) bb.getManagedBean()
						.getSelectedObject()[i];
	
				extCodeVO.setTypeId(Long.valueOf(String.valueOf(objects[0])));
				extCodeVO.setBrandId(Long.valueOf(String.valueOf(objects[1])));
				extCodeVO
						.setMeasurementId(Long.valueOf(String.valueOf(objects[2])));
				extCodeVO.setColorId(Long.valueOf(String.valueOf(objects[3])));
	
				System.out.println("******type NAme******"
						+ String.valueOf(objects[4]));
				extCodeVO.setTypeName(String.valueOf(objects[4]));
				extCodeVO.setBrandName(String.valueOf(objects[5]));
				extCodeVO.setMeasurementName(String.valueOf(objects[6]));
				extCodeVO.setColorName(String.valueOf(objects[7]));
	
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
	
					System.out.println("........obj.getBrandId()="+obj.getBrandId());
					System.out.println("........obj.obj.getMeasurementId()="+obj.getMeasurementId());
					System.out.println("........obj.getTypeId()="+obj.getTypeId());
					System.out.println("........obj.getColorId()="+obj.getColorId());
					
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
						System.out.println("************else*******************");
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
	
						System.out.println("item Name:" + item.getItemName());
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
	
				}
				catalogueSO.saveSupplierItem(reqDTO);
			}
	
			FacesContext facesContext = FacesContext.getCurrentInstance();
	
			// facesContext.addMessage(null, new
			// FacesMessage(FacesMessage.SEVERITY_INFO," Records Added Successfully",
			// ""));
	
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
							.getMessage(MessageConst.INFO_710007, new String[] { ""
									+ bb.getManagedBean().getExtensionVoList()
											.size() },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
//		} catch (Exception e) {
//			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
			codificationSO = null;
		}

		return "../catalogue/preRenderCatalogueMaintenanceFile.xhtml";
	}

	public String GoToMaintain() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		try{
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
	
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
	
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
	
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0)
					.getSupplierId());
			reqDTO.setItemStatus("D");
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
			System.out.println("resDTO.getSupplierItemList():"
					+ resDTO.getSupplierItemList().size());
			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		return "catalogueMaintenanceFile";
	}

	public void deleteDraftItem(int index) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		try{
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
	
			reqDTO.setCmSupplierItemObj(this.getManagedBean().getMaintainItemList()
					.get(index));
	
			CatalogueResDTO resDTO = catalogueSO.deleteDraftItem(reqDTO);
	
			if (resDTO.isSuccessful()) {
	
				this.getManagedBean().getMaintainItemList().remove(index);
	
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

	public String navigateToCatalogDet(int index) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		
		try{

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
	
			reqDTO.setSupplierId(this.getManagedBean().getMaintainItemList()
					.get(index).getSmSupplier().getSupplierId());
	
			CatalogueResDTO resDTO = catalogueSO
					.retrieveSupplierZoneBySuppId(reqDTO);
	
			System.out.println("size:" + resDTO.getSupplierZoneLs().size());
	
			this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
	
			CmSupplierItem clone = (CmSupplierItem) SerializationUtils.clone(this
					.getManagedBean().getMaintainItemList().get(index));
	
			this.getManagedBean().setCloneCmSupplierItem(clone);
			this.getManagedBean().setCloneIndex(index);
	
			log.info("*****this.getManagedBean().getMaintainItemList().get(index).getCmImages().size()*****:"
					+ this.getManagedBean().getMaintainItemList().get(index)
							.getCmImages().size());
			ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();
			bb.getManagedBean().setCatDetTabActiveIndex(0);
	
			this.getManagedBean().setSelectedMaintainItem(
					this.getManagedBean().getMaintainItemList().get(index));
			this.getManagedBean()
					.getSelectedMaintainItem()
					.setCompareDesc(
							this.getManagedBean().getMaintainItemList().get(index)
									.getItemDesc());
	
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}	

		//return "addCatalogItem";
		return "editCatalogItem";

	}

	public String navigateToPublishedItemDet(int index) {

		this.getManagedBean().setSelectedMaintainItem(
				this.getManagedBean().getPublishedItemList().get(index));
		this.getManagedBean()
				.getSelectedMaintainItem()
				.setCompareDesc(
						this.getManagedBean().getPublishedItemList().get(index)
								.getItemDesc());
		this.getManagedBean().setTabActiveIndex(4);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		//return "addCatalogItem";
		return "editCatalogItem";
	}

	public String navigateToExpiredItemDet(int index) {

		this.getManagedBean().setSelectedMaintainItem(
				this.getManagedBean().getExpiredItemList().get(index));
		this.getManagedBean()
				.getSelectedMaintainItem()
				.setCompareDesc(
						this.getManagedBean().getExpiredItemList().get(index)
								.getItemDesc());
		this.getManagedBean().setTabActiveIndex(5);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		//return "addCatalogItem";
		return "editCatalogItem";
	}

	public String navigateToApprovalItemDet(int index) {

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		this.getManagedBean().setPreviousScreen(1);//catMaintenance

		try{
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
	
			this.getManagedBean().setSelectedMaintainItem(
					this.getManagedBean().getApprovalItemList().get(index));
			this.getManagedBean()
					.getSelectedMaintainItem()
					.setCompareDesc(
							this.getManagedBean().getApprovalItemList().get(index)
									.getItemDesc());
	
			for (CmItemRejectReason remark : this.getManagedBean()
					.getSelectedMaintainItem().getCmItemRejectReasons()) {
	
				userReqDTO.setUserId(remark.getChangedBy());
				System.out.println("remark.getChangedBy()" + remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				System.out.println("userResDTO.getPmUser().getUserName():"
						+ userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}
			this.getManagedBean().setTabActiveIndex(1);
			this.getManagedBean().setDisplayCatalogue(true);
			//updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			userSO = null;
			statusSO = null;
		}	
		
		return "addCatalogItem";
		//return "editCatalogItem";
	}

	public String navigateToApprovedItemDet(int index) {

		this.getManagedBean().setSelectedMaintainItem(
				this.getManagedBean().getApprovedItemList().get(index));
		this.getManagedBean()
				.getSelectedMaintainItem()
				.setCompareDesc(
						this.getManagedBean().getApprovedItemList().get(index)
								.getItemDesc());
		this.getManagedBean().setTabActiveIndex(2);
		this.getManagedBean().setDisplayCatalogue(true);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance
		//updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
		return "addCatalogItem";
		//return "editCatalogItem";

	}

	public String navigateToRejectedItemDet(int index) {

		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		this.getManagedBean().setPreviousScreen(1);//catMaintenance

		try{
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
	
			StatusReqDTO statusReqDTO = new StatusReqDTO();
	
			statusReqDTO.setDocType("CMR");
			statusReqDTO.setModuleCode("CM");
			statusReqDTO.setLanguageCode(languageCode);
	
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);
	
			this.getManagedBean().setStatusDescList(resDTO.getStatusDescList());
			/*
			 * this.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
			 * for(CmItemRejectReason reason :
			 * this.getManagedBean().getRejectedItemList
			 * ().get(index).getCmItemRejectReasons()){
			 * this.getManagedBean().getRejectReasonIds
			 * ().add(reason.getPmStatus().getStatusId());
			 * this.getManagedBean().setRejectReason(reason.getRemark()); }
			 */
	
			this.getManagedBean().setSelectedMaintainItem(
					this.getManagedBean().getRejectedItemList().get(index));
			this.getManagedBean()
					.getSelectedMaintainItem()
					.setCompareDesc(
							this.getManagedBean().getRejectedItemList().get(index)
									.getItemDesc());
	
			UserReqDTO userReqDTO = new UserReqDTO();
	
			for (CmItemRejectReason remark : this.getManagedBean()
					.getSelectedMaintainItem().getCmItemRejectReasons()) {
	
				userReqDTO.setUserId(remark.getChangedBy());
				System.out.println("remark.getChangedBy()" + remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				System.out.println("userResDTO.getPmUser().getUserName():"
						+ userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}
	
			this.getManagedBean().setTabActiveIndex(3);
			this.getManagedBean().setDisplayCatalogue(true);
			updateStandardErrorDialog(this.getManagedBean().getSelectedMaintainItem().getCmStandardPricing());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			userSO = null;
			statusSO = null;
		}

		//return "addCatalogItem";
		return "editCatalogItem";
	}

	public String navigateToSupplierItemDet(VCmSupplierItem supplierItem) {

		System.out.println("getDocId" + supplierItem.getDocId());

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		this.getManagedBean().setPreviousScreen(0);//catSearch
		
		try{

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			reqDTO.setDocId(supplierItem.getDocId());
	
			CatalogueResDTO resDTO = null;
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
			this.getManagedBean().setDisplayCatalogue(true);
			this.getManagedBean().setSelectedMaintainItem(resDTO.getSupplierItemList().get(0));
			
			reqDTO.setSupplierId(resDTO.getSupplierItemList().get(0).getSmSupplier().getSupplierId());
			resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
			this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());

			System.out.println("*****************end********************");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

		return "addCatalogView?faces-redirect=true";
	}

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
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
				.add(price);
	}

	public void addNewPromotionTierPricing() {

		log.debug("*****addNewPromotionTierPricing*****");
		CmPromotionTierPricing price = new CmPromotionTierPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings().add(price);
	}

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
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().add(price);

	}

	public void addNewZonalPromotionTierPricing() {

		CmZonalPromotionTierPricing price = new CmZonalPromotionTierPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings().add(price);

	}

	public void addNewZonalStandardPricing() {

		CmZonalStandardPricing price = new CmZonalStandardPricing();
		price.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
		this.getManagedBean().getSelectedMaintainItem().getzStdPrices()
				.add(price);

	}

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

	public void addNewDistrictPricing() {
		PricingVO price = new PricingVO();

		this.getManagedBean().getDistrictPricingList().add(price);
	}

	public String cancelToMaintenanceFile() {

		log.info("*********cancelToMaintenanceFile*************");

		this.getManagedBean()
				.getMaintainItemList()
				.set(this.getManagedBean().getCloneIndex(),
						this.getManagedBean().getCloneCmSupplierItem());
		// this.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getCloneCmSupplierItem());

		return "catalogMaintenanceFile?faces-redirect=true";
	}

	public String cancelBackToMaintenanceFile() {

		log.info("*********cancelBackToMaintenanceFile*************");

		return "catalogMaintenanceFile?faces-redirect=true";
	}

	public String okToMaintenanceFile() {

		log.info("*********okToMaintenanceFile*************");

		for (CmPricing price : this.getManagedBean().getSelectedMaintainItem()
				.getCmPricings()) {
			System.out.println("price.getPrice()" + price.getPrice());
		}

		return "catalogMaintenanceFile?faces-redirect=true";
		// return "catalogMaintenanceFile";
	}

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

	public void onNodeDelete() {

		FacesContext context = FacesContext.getCurrentInstance();

		Map map2 = context.getExternalContext().getRequestParameterMap();
		String newCodes = (String) map2.get("name1");

		log.debug("***event.getTreeNode()*****:" + newCodes);
	}

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

			List<CmSupplierItem> cmSupplierItems = this.getManagedBean()
					.getMaintainItemList();
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

			String invalidDataMsg = "Please fill all mandatory fields in the following rows:\n";
			int rowNum = 1;
			int count = 0;
			while (rowIterator.hasNext()) {

				row = rowIterator.next();

				if ((row.getCell(12) == null || row.getCell(12)
						.getStringCellValue().isEmpty())
						&& (row.getCell(0) != null && !row.getCell(0)
								.toString().isEmpty())) {
					invalidDataMsg = invalidDataMsg.concat("Row" + rowNum
							+ "\n");
					count++;
				} else {

					if (row.getCell(0) != null
							&& !row.getCell(0).toString().isEmpty()) {
						supplierItemId = row.getCell(0).getNumericCellValue();
						System.out.println("Supp Item Id");
					} else {
						break;
					}

					// CmSupplierItem catalogItem = new CmSupplierItem();
					System.out.println("***********supplierItemId"
							+ supplierItemId);
					if (supplierItemId > 0) {
						for (CmSupplierItem supplierItem : cmSupplierItems) {
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

								if (row.getCell(7) != null) {
									price = row.getCell(7)
											.getNumericCellValue();
									cmStandardPricing.setPrice(new BigDecimal(
											price));
								}

								if (row.getCell(8) != null) {
									if (row.getCell(8).getCellType() == Cell.CELL_TYPE_NUMERIC)
										startDate = row.getCell(8)
												.getDateCellValue();
									else if (row.getCell(8).getCellType() == Cell.CELL_TYPE_STRING)
										startDate = new SimpleDateFormat(
												"dd/mm/yyyy").parse(row
												.getCell(8)
												.getStringCellValue());
									cmStandardPricing.setStartDate(startDate);
								}

								if (row.getCell(9) != null) {
									if (row.getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC)
										endDate = row.getCell(9)
												.getDateCellValue();
									else if (row.getCell(9).getCellType() == Cell.CELL_TYPE_STRING)
										endDate = new SimpleDateFormat(
												"dd/mm/yyyy").parse(row
												.getCell(9)
												.getStringCellValue());
									cmStandardPricing.setEndDate(endDate);
								}

								if (row.getCell(10) != null
										&& !row.getCell(10).toString()
												.isEmpty()) {
									additionalItemDesc = row.getCell(10)
											.toString();
									supplierItem
											.setItemDesc(additionalItemDesc);
								}

								if (row.getCell(11) != null
										&& !row.getCell(11).toString()
												.isEmpty()) {
									frequency = row.getCell(11)
											.getNumericCellValue();
									supplierItem.setFrequency(new BigDecimal(
											frequency));
								}

								if (row.getCell(12) != null
										&& !row.getCell(12)
												.getStringCellValue().isEmpty()) {

									IUomSO uomSO = (IUomSO) BusinessDelegate
											.getInstance().getServiceObject(
													"pm", IUomSO.class);
									UomResDTO resDTO = uomSO.retrieveUomList();
									List<PmUom> uomList = resDTO.getPmUomList();

									uom = row.getCell(12).toString();
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
										FacesContext facesContext = FacesContext
												.getCurrentInstance();
										facesContext
												.addMessage(
														null,
														new FacesMessage(
																FacesMessage.SEVERITY_ERROR,
																"Uom not valid",
																""));
										break;
									}
								}

								if (row.getCell(13) != null
										&& !row.getCell(13).toString()
												.isEmpty()) {
									modelNo = row.getCell(13).toString();
									supplierItem.setModelNumber(modelNo);
								}

								if (row.getCell(14) != null
										&& !row.getCell(14).toString()
												.isEmpty()) {
									skuNo = row.getCell(14).toString();
									supplierItem.setSkuNumber(skuNo);
								}

								if (row.getCell(15) != null
										&& !row.getCell(15).toString()
												.isEmpty()) {
									manfPartNo = row.getCell(15).toString();
									supplierItem.setManufacturer(manfPartNo);
								}

								if (row.getCell(16) != null
										&& !row.getCell(16).toString()
												.isEmpty()) {
									minOrder = row.getCell(16)
											.getNumericCellValue();
									supplierItem.setMinOrder(minOrder
											.intValue());
								}

								if (row.getCell(17) != null
										&& !row.getCell(17).toString()
												.isEmpty()) {
									maxOrder = row.getCell(17)
											.getNumericCellValue();
									supplierItem.setMaxOrder(maxOrder
											.intValue());
								}

								if (row.getCell(18) != null
										&& !row.getCell(18).toString()
												.isEmpty()) {
									leadTime = row.getCell(18)
											.getNumericCellValue();
									supplierItem.setLeadTime(new BigDecimal(
											leadTime));
								}

								// catItems.add(catalogItem);

								int existingIndex = cmSupplierItems
										.indexOf(supplierItem);

								this.getManagedBean().getMaintainItemList()
										.set(existingIndex, supplierItem);

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

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			reqDTO.setSaveSupplierItems(this.getManagedBean()
					.getMaintainItemList());
			CatalogueResDTO resDTO = catalogueSO.updateSupplierItem(reqDTO);

			/*
			 * for(CmSupplierItem cmSupplierItem : cmSupplierItems){
			 * System.out.println("\nItem Code:" +
			 * cmSupplierItem.getCmItem().getExtensionCode());
			 * System.out.println("\nItem Desc:" +
			 * cmSupplierItem.getCmItem().getItemName()); }
			 */

			if (count > 0) {
				System.out.println(invalidDataMsg);
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, invalidDataMsg, ""));
			} else {
				System.out.println("Updated successfully.");
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Updated successfully.", ""));

				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);
				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("D");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(0);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
		System.out.println("tempDir" + tempDir);
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

		// add the 1st title row

		int no = 1;

		for (int i = 0; i < this.getManagedBean().getSelectedMaintainItems().length; i++) {
			List dataLs = new Vector();
			CmSupplierItem catalog = this.getManagedBean()
					.getSelectedMaintainItems()[i];

			// dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscCode());
			// dataLs.add(catalog.getCmItem().getCmUnspsc().getUnspscTitle());
			dataLs.add(catalog.getSupplierItemId());
			dataLs.add(catalog.getCmItem().getExtensionCode());
			dataLs.add(catalog.getCmItem().getItemName());
			dataLs.add(catalog.getCmItem().getCmType().getTypeName());
			dataLs.add(catalog.getCmItem().getCmBrand().getBrandName());
			dataLs.add(catalog.getCmItem().getCmMeasurement()
					.getMeasurementName());
			dataLs.add(catalog.getCmItem().getCmColor().getColorName());

			CmPricing cmPricing = new CmPricing();
			for (CmPricing pricing : catalog.getCmPricings()) {

				if (pricing.getPricingType().equalsIgnoreCase("01")) {
					cmPricing = pricing;
				}
			}

			if (cmPricing.getPrice() != null)
				dataLs.add(cmPricing.getPrice());
			else
				dataLs.add("");

			if (cmPricing.getStartDate() != null)
				dataLs.add(formater.format(cmPricing.getStartDate()));
			else
				dataLs.add("");

			if (cmPricing.getEndDate() != null)
				dataLs.add(formater.format(cmPricing.getEndDate()));
			else
				dataLs.add("");

			dataLs.add(catalog.getItemDesc());
			dataLs.add(catalog.getFrequency());
			dataLs.add(catalog.getPmUom().getUomName());
			dataLs.add(catalog.getModelNumber());
			dataLs.add(catalog.getSkuNumber());
			dataLs.add(catalog.getManufacturer());
			dataLs.add(catalog.getMinOrder());
			dataLs.add(catalog.getMaxOrder());
			dataLs.add(catalog.getLeadTime());

			addCellToRow(product, no++, dataLs, catalogCellStyle);

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
			addCellToRow(uomPreload, uomNo++, dataLs, catalogCellStyle);
		}

		uomPreload.enableLocking();
		my_xlsx_workbook.lockStructure();

		input_document.close();

		my_xlsx_workbook.write(response.getOutputStream());

		// close the stream

		return null;
	}

	public void addCellToRow(XSSFSheet sheet, int count, List colValList,
			CellStyle catalogCellStyle) throws Exception {

		Row row = sheet.createRow(count);
		XSSFCellStyle cs = sheet.getWorkbook().createCellStyle();
		XSSFDataFormat df = sheet.getWorkbook().createDataFormat();
		XSSFCellStyle unlockedCellStyle = sheet.getWorkbook().createCellStyle();
		unlockedCellStyle.setLocked(false);

		for (int i = 0; i < colValList.size(); i++) {
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
			}

		}

	}

	public void imageUpload(FileUploadEvent event)  {

		// String tempDir = System.getenv("windir");

		String tempDir = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
		log.info("tempDir = "+tempDir);
		
		StringBuilder uploadDir = new StringBuilder();
//		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.UPLOAD_URL)).append("ngep/cm");
		uploadDir.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append("/CM");
		log.info("uploadDir***"+uploadDir);

		UploadedFile item = event.getFile();
		CmImage catImage = new CmImage();
		catImage.setImage(item.getContents());
		catImage.setImageName(item.getFileName());
		catImage.setImageDesc(item.getFileName());
		catImage.setImageType("jpg");
		catImage.setImagePath(item.getFileName());
		catImage.setTempPath(uploadDir.toString());
		log.info("catImage.TempPath()" + catImage.getTempPath());

		// render for display
		this.getManagedBean().getSelectedMaintainItem().getCmImages().add(catImage);
		// getCatalogImages().add(this.getManagedBean().getImageObj());

		genImage(catImage);
		log.info("size:"+ this.getManagedBean().getSelectedMaintainItem().getCmImages().size());

	}

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

	public String saveCatalogItem() {
		log.info("In saveCatalogItem");
		PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		System.out.println("******getTabActiveIndex:" + this.getManagedBean().getTabActiveIndex());
		ICatalogueSO catalogueSO = null;

		try {
			//gg
			catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
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

			/*Map<Date, List<CmPromotionTierPricing>> promotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings());
			if (!promotionTierMap.isEmpty() && promotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(promotionTierMap);
				List<CmPromotionTierPricing> perviouslyList = new ArrayList<CmPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmPromotionTierPricing> sortedTier = (List<CmPromotionTierPricing>) sorted;

					if (perviouslyList != null && perviouslyList.size() > 0) {

						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
								new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Promotion Tier Start date Overlapping",
									""));
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
									"Promotion Tier price must be less than Standard Tier price.",
									""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(1);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			Map<Date, List<CmZonalPromotionTierPricing>> zPromotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings());

			if (!zPromotionTierMap.isEmpty() && zPromotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(zPromotionTierMap);
				List<CmZonalPromotionTierPricing> perviouslyList = new ArrayList<CmZonalPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalPromotionTierPricing> sortedTier = (List<CmZonalPromotionTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
								new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									" Zonal Promotion Tier Start date Overlapping",
									""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmZonalPromotionTierPricing tier = sortedTier.get(i);
						if (tier.getStartDate().compareTo(tier.getEndDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Promotion Tier End date must be greater than Zonal Promotion Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
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
				if (previousZPromotionObj != null) {
					if (previousZPromotionObj.getEndDate().compareTo(standard.getStartDate()) > 0) {
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Zonal Promotion Start date Overlapping", ""));
						this.getManagedBean().setPricingTabIndex(2);
						this.getManagedBean().setZonalPricingIndex(1);
						return "editCatalogItem";
					}
				}
				if (standard.getStartDate().compareTo(standard.getEndDate()) > 0) {
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Zonal Promotion End Date must be greater than Zonal Promotion Start Date",
											""));
					this.getManagedBean().setPricingTabIndex(2);
					this.getManagedBean().setZonalPricingIndex(1);
					return "editCatalogItem";
				}
				previousZPromotionObj = standard;
			}

			Map<Date, List<CmZonalTierPricing>> zTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings());
			if (!zTierMap.isEmpty() && zTierMap.size() > 0) {
				Iterator itMap = zTierMap.entrySet().iterator();
				while (itMap.hasNext()) {
					Map.Entry entry = (Map.Entry) itMap.next();
					List<CmZonalTierPricing> dataList = (List) entry.getValue();
					CmZonalTierPricing previousObj = null;
					for (int i = 0; i < dataList.size(); i++) {
						CmZonalTierPricing tier = dataList.get(i);
						if (previousObj != null) {
							if (previousObj.getToQty() >= tier.getFromQty()) {
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(
												null,
												new FacesMessage(
														FacesMessage.SEVERITY_ERROR,
														" Zonal Standard Tier Overlapping",
														""));
								this.getManagedBean().setPricingTabIndex(3);
								this.getManagedBean().setZonalTierPricingIndex(0);
								return "editCatalogItem";
							}
						}
						if (tier.getFromQty() >= tier.getToQty()) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Standard Tier To Qty must be greater than Zonal Standard Tier From Qty",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(zTierMap);
				List<CmZonalTierPricing> perviouslyList = new ArrayList<CmZonalTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalTierPricing> sortedTier = (List<CmZonalTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Standard Tier Start date Overlapping",
													""));
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
													" Zonal Standard Tier End date must be greater than Zonal Standard Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			Map<Date, List<CmTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());

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
										" Standard Tier Overlapping", ""));
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
													" Standard Tier To Qty must be greater than Standard Tier From Qty",
													""));
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
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Standard Tier Start date Overlapping",
													""));
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
													" Standard Tier End date must be greater than Standard Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}

			}

			if (this.getManagedBean().getSelectedMaintainItem().getPmUom().getUomName().isEmpty()) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								MessageUtils.getMessage(
										MessageConst.ERROR_715007,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

				return "editCatalogItem";
			}*/

			for (CmImage image : this.getManagedBean().getSelectedMaintainItem().getCmImages()) {
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
			log.debug("isReviewRequired:"+ this.getManagedBean().getSelectedMaintainItem().isReviewRequired());

			/*
			 * if(this.getManagedBean().getSelectedMaintainItem().isReviewRequired
			 * ()){
			 * this.getManagedBean().getSelectedMaintainItem().setItemStatus
			 * ("P"); }else{
			 * this.getManagedBean().getSelectedMaintainItem().setItemStatus
			 * ("D"); }
			 */

		

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

			System.out.println("getzStdPrices:"
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

			if (this.getManagedBean().getSelectedMaintainItem().getItemStatus()
					.equalsIgnoreCase("U")) {
				if (!this.getManagedBean().getSelectedMaintainItem()
						.isRecordStatus()) {
					this.getManagedBean().getSelectedMaintainItem()
							.setRecordStatusFlg(0);

				}
			} else {
				this.getManagedBean().getSelectedMaintainItem()
						.setRecordStatusFlg(1);
			}

			this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");

			System.out.println("**********final item status************"
					+ this.getManagedBean().getSelectedMaintainItem()
							.getItemStatus());

			reqDTO.getSaveSupplierItems().add(
					this.getManagedBean().getSelectedMaintainItem());
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

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
					.get(0).getSupplierId());

			if (this.getManagedBean().getTabActiveIndex() == 0) {
				reqDTO.setItemStatus("D");
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(
						resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 4) {

				reqDTO.setItemStatus("U");
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setPublishedItemList(
						resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 1) {

				reqDTO.setItemStatus("P");
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovalItemList(
						resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 2) {

				reqDTO.setItemStatus("A");
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setApprovedItemList(
						resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 3) {

				reqDTO.setItemStatus("R");
				CatalogueResDTO resDTO = catalogueSO
						.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setRejectedItemList(
						resDTO.getSupplierItemList());

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

		// return "catalogMaintenanceFile?faces-redirect=true";
		return "preRenderCatalogueMaintenanceFile";
	}

	public String singleSubmitToReviewItems() {
		log.info("submit catalogue");
		PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

		System.out.println("******getTabActiveIndex:"+ this.getManagedBean().getTabActiveIndex());

		ICatalogueSO catalogueSO = null;

		try {

			catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			boolean inactiveFlag = false;

			if (!this.getManagedBean().getSelectedMaintainItem().getItemStatus().equalsIgnoreCase("D")) {
				if (!this.getManagedBean().getSelectedMaintainItem().isRecordStatus()) {
					this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(0);
					this.getManagedBean().getSelectedMaintainItem().setItemStatus("D");
					inactiveFlag = true;
				}
			} else {
				this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(1);
			}

			if (!inactiveFlag) {

				for (CmImage image : this.getManagedBean().getSelectedMaintainItem().getCmImages()) {
					if (image.isBrandNew()) {
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
						this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
						break;
					}
				}

				if (this.getManagedBean().getSelectedMaintainItem().getCompareDesc() != null
				&& this.getManagedBean().getSelectedMaintainItem().getCompareDesc().trim().length() > 0) {
					if (!(this.getManagedBean().getSelectedMaintainItem()
							.getCompareDesc().equalsIgnoreCase(this
							.getManagedBean().getSelectedMaintainItem()
							.getItemDesc()))) {
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
						this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
					}
				} else {
					if (this.getManagedBean().getSelectedMaintainItem().getItemDesc() != null
					&& this.getManagedBean().getSelectedMaintainItem().getItemDesc().trim().length() > 0) {
						this.getManagedBean().getSelectedMaintainItem().setReviewRequired(true);
						this.getManagedBean().getSelectedMaintainItem().setItemStatus("P");
					}
				}
				System.out.println("isReviewRequired:"
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

			log.info("*********getItemStatus*********"+ this.getManagedBean().getSelectedMaintainItem().getItemStatus());
			/*throw new EpException(MessageConst.ERROR_715009, 
			new String[]{ResourceBundleUtils.getMessage("lbl.cm.tier", 
			ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
			new FacesScopes().getUserLocale())});*/
			
			
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
			//promotion.setCmSupplierItem(this.getManagedBean().getSelectedMaintainItem());
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
					}
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
					}
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
					}
			}
			
			//----------mandatory checking end----------------------------------------------------------------------------------------//
			
			//----------logic checking-----------------------------------------------------------------------------------------------//
			
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
			
			//1 year period between start and end date
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

			Map<Date, List<CmTierPricing>> tierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmTierPricings());
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
										" Standard Tier Overlapping", ""));
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
													" Standard Tier To Qty must be greater than Standard Tier From Qty",
													""));
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
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Standard Tier Start date Overlapping",
													""));
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
													" Standard Tier End date must be greater than Standard Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(1);
							this.getManagedBean().setTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}
			
			Map<Date, List<CmPromotionTierPricing>> promotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmPromotionTierPricings());
			if (!promotionTierMap.isEmpty() && promotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(promotionTierMap);
				List<CmPromotionTierPricing> perviouslyList = new ArrayList<CmPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmPromotionTierPricing> sortedTier = (List<CmPromotionTierPricing>) sorted;

					if (perviouslyList != null && perviouslyList.size() > 0) {

						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
								new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Promotion Tier Start date Overlapping",
									""));
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
									"Promotion Tier price must be less than Standard Tier price.",
									""));
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
				if (previousZPromotionObj != null) {
					if (previousZPromotionObj.getEndDate().compareTo(standard.getStartDate()) > 0) {
						FacesContext facesContext = FacesContext.getCurrentInstance();
						facesContext.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Zonal Promotion Start date Overlapping", ""));
						this.getManagedBean().setPricingTabIndex(2);
						this.getManagedBean().setZonalPricingIndex(1);
						return "editCatalogItem";
					}
				}
				if (standard.getStartDate().compareTo(standard.getEndDate()) > 0) {
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Zonal Promotion End Date must be greater than Zonal Promotion Start Date",
											""));
					this.getManagedBean().setPricingTabIndex(2);
					this.getManagedBean().setZonalPricingIndex(1);
					return "editCatalogItem";
				}
				previousZPromotionObj = standard;
			}
			
			Map<Date, List<CmZonalTierPricing>> zTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings());
			if (!zTierMap.isEmpty() && zTierMap.size() > 0) {
				Iterator itMap = zTierMap.entrySet().iterator();
				while (itMap.hasNext()) {
					Map.Entry entry = (Map.Entry) itMap.next();
					List<CmZonalTierPricing> dataList = (List) entry.getValue();
					CmZonalTierPricing previousObj = null;
					for (int i = 0; i < dataList.size(); i++) {
						CmZonalTierPricing tier = dataList.get(i);
						if (previousObj != null) {
							if (previousObj.getToQty() >= tier.getFromQty()) {
								FacesContext facesContext = FacesContext.getCurrentInstance();
								facesContext.addMessage(
												null,
												new FacesMessage(
														FacesMessage.SEVERITY_ERROR,
														" Zonal Standard Tier Overlapping",
														""));
								this.getManagedBean().setPricingTabIndex(3);
								this.getManagedBean().setZonalTierPricingIndex(0);
								return "editCatalogItem";
							}
						}
						if (tier.getFromQty() >= tier.getToQty()) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Standard Tier To Qty must be greater than Zonal Standard Tier From Qty",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
						previousObj = tier;
					}
				}

				List<Object> sortedZonalList = groupingStartDate(zTierMap);
				List<CmZonalTierPricing> perviouslyList = new ArrayList<CmZonalTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalTierPricing> sortedTier = (List<CmZonalTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Standard Tier Start date Overlapping",
													""));
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
													" Zonal Standard Tier End date must be greater than Zonal Standard Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}

			Map<Date, List<CmZonalPromotionTierPricing>> zPromotionTierMap = groupingTier(this.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings());
			if (!zPromotionTierMap.isEmpty() && zPromotionTierMap.size() > 0) {
				List<Object> sortedZonalList = groupingStartDate(zPromotionTierMap);
				List<CmZonalPromotionTierPricing> perviouslyList = new ArrayList<CmZonalPromotionTierPricing>();
				for (Object sorted : sortedZonalList) {
					List<CmZonalPromotionTierPricing> sortedTier = (List<CmZonalPromotionTierPricing>) sorted;
					if (perviouslyList != null && perviouslyList.size() > 0) {
						if (perviouslyList.get(0).getEndDate().compareTo(sortedTier.get(0).getStartDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null,
								new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									" Zonal Promotion Tier Start date Overlapping",
									""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					for (int i = 0; i < sortedTier.size(); i++) {
						CmZonalPromotionTierPricing tier = sortedTier.get(i);
						if (tier.getStartDate().compareTo(tier.getEndDate()) > 0) {
							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(
											null,
											new FacesMessage(
													FacesMessage.SEVERITY_ERROR,
													" Zonal Promotion Tier End date must be greater than Zonal Promotion Tier start date",
													""));
							this.getManagedBean().setPricingTabIndex(3);
							this.getManagedBean().setZonalTierPricingIndex(0);
							return "editCatalogItem";
						}
					}
					perviouslyList = sortedTier;
				}
			}
			
			//----------logic checking end-------------------------------------------------------------------------------------------//
			
			//record status = active
			this.getManagedBean().getSelectedMaintainItem().setRecordStatusFlg(1);
			
			reqDTO.getSaveSupplierItems().add(this.getManagedBean().getSelectedMaintainItem());
			catalogueSO.updateSupplierItem(reqDTO);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm",ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
			SupplierSearchResDTO smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0).getSupplierId());

			if (this.getManagedBean().getTabActiveIndex() == 0) {

				reqDTO.setItemStatus("D");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 4) {

				reqDTO.setItemStatus("U");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				this.getManagedBean().setPublishedItemList(resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 1) {

				reqDTO.setItemStatus("P");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				this.getManagedBean().setApprovalItemList(resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 2) {

				reqDTO.setItemStatus("A");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				this.getManagedBean().setApprovedItemList(resDTO.getSupplierItemList());

			} else if (this.getManagedBean().getTabActiveIndex() == 3) {

				reqDTO.setItemStatus("R");
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				this.getManagedBean().setRejectedItemList(resDTO.getSupplierItemList());

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("***********error********");
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
		}

		log.info("Exit saveCatalogItem");

		// return "catalogMaintenanceFile?faces-redirect=true";
		return "preRenderCatalogueMaintenanceFile";
	}

	public String submitToReviewItems() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = new CatalogueResDTO();
		try {

			this.getManagedBean().setTabActiveIndex(0);

			System.out
					.println("this.getManagedBean().getSelectedMaintainItems().length:"
							+ this.getManagedBean().getSelectedMaintainItems().length);
			for (int i = 0; i < this.getManagedBean()
					.getSelectedMaintainItems().length; i++) {
				CmSupplierItem catalog = this.getManagedBean()
						.getSelectedMaintainItems()[i];
				System.out.println("catalog.getPmUom().getUomName():"
						+ catalog.getPmUom().getUomName());
				
				//mandatory checking
				if (catalog.getCmStandardPricing().getPrice() == null
						|| catalog.getCmStandardPricing().getStartDate() == null
						|| catalog.getCmStandardPricing().getEndDate() == null
						|| catalog.getPmUom().getUomName() == null
						|| catalog.getPmUom().getUomName().isEmpty()) {

					System.out.println("********validation**********");
					FacesContext facesContext = FacesContext
							.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							" Please complete all mandatory fields to proceed",
							""));
					// facesContext.getExternalContext().getFlash().setKeepMessages(true);

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
					

				if (catalog.isReviewRequired()) {
					catalog.setItemStatus("P");
				} else {
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

			System.out
					.println("resDTO.isSuccessful():" + resDTO.isSuccessful());
			if (resDTO.isSuccessful()) {

				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm",
								ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
						.get(0).getSupplierId());
				reqDTO.setItemStatus("D");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

				this.getManagedBean().setMaintainItemList(
						resDTO.getSupplierItemList());

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, " Submitted Successfully",
						""));

			}

		} catch (Exception e) {

			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

		return "catalogMaintenanceFile";
	}

	/*
	 * public void searchCatalogue(){
	 * log.info("*******searchCatalogue************");
	 * 
	 * FacesScopes facesScopes = new FacesScopes(); String unspscCode =
	 * facesScopes.getParameter("unspscCode"); System.out.println("unspscCode: "
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

	public String backToPervious() {

		return "supplierItemView";
	}

	public String onTabChange1(String status) {
		System.out.println("******status*******" + status);
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

		try{
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
	
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
	
			System.out.println("getSmSupplier"
					+ smResDTO.getResultBean().getResultList().get(0));
	
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0)
					.getSupplierId());
	
			resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
	
			System.out.println("size:" + resDTO.getSupplierZoneLs().size());
	
			this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
	
			if (status.equalsIgnoreCase("Draft")) {
	
				reqDTO.setItemStatus("D");
				System.out.println("getSupplierId:" + reqDTO.getSupplierId());
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setMaintainItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(0);
	
			} else if (status.equalsIgnoreCase("Published")) {
	
				reqDTO.setItemStatus("U");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setPublishedItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(4);
	
			} else if (status.equalsIgnoreCase("Approval")) {
	
				reqDTO.setItemStatus("P");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setApprovalItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(1);
	
			} else if (status.equalsIgnoreCase("Approved")) {
	
				reqDTO.setItemStatus("A");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setApprovedItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(2);
	
			} else if (status.equalsIgnoreCase("Rejected")) {
	
				reqDTO.setItemStatus("R");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setRejectedItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(3);
	
			} else if (status.equalsIgnoreCase("Expired")) {
	
				reqDTO.setItemStatus("E");
				resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				this.getManagedBean().setExpiredItemList(
						resDTO.getSupplierItemList());
				this.getManagedBean().setTabActiveIndex(5);
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		return "catalogMaintenanceFile?faces-redirect=true";
	}

	/*
	 * public void onTabChange(TabChangeEvent event) {
	 * System.out.println("title"+event.getTab().getTitle());
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
	 * System.out.println("size:"+resDTO.getSupplierZoneLs().size());
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

	public String viewCartListItem() {

		return "cartListItemView";
	}

	public void deleteCartItem(int index) {

		this.getManagedBean().getCartList().remove(index);
	}

	/*
	 * @PostConstruct public void init() {
	 * 
	 * 
	 * FacesContext fCtx = FacesContext.getCurrentInstance(); PortletRequest req
	 * = (PortletRequest) fCtx.getExternalContext() .getRequest(); ThemeDisplay
	 * themeDisplay = (ThemeDisplay) req .getAttribute(WebKeys.THEME_DISPLAY);
	 * Layout layout = themeDisplay.getLayout();
	 * System.out.println("layout.getFriendlyURL():"+layout.getFriendlyURL());
	 * if (layout.getFriendlyURL().equals("/maintain-catalogue")) {
	 * 
	 * ICatalogueSO catalogueSO = (ICatalogueSO)
	 * BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
	 * 
	 * ISearchBean<CmSupplierItem> searchBean = new
	 * SearchBean<CmSupplierItem>();
	 * statusFilterBean.setSearchField("itemStatus");
	 * statusFilterBean.setSearchValue("D");
	 * searchBean.getDataFilterBeans().add(statusFilterBean);
	 * 
	 * 
	 * 
	 * CatalogueReqDTO reqDTO = new CatalogueReqDTO();
	 * 
	 * reqDTO.setSearchBean(searchBean); log.info("catalogueSO:"+catalogueSO);
	 * 
	 * CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	 * 
	 * this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());
	 * 
	 * } }
	 */

	public void publishCatalogItem() {
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		System.out
				.println("this.getManagedBean().getSelectedApprovedItems().length:"
						+ this.getManagedBean().getSelectedApprovedItems().length);
		for (int i = 0; i < this.getManagedBean().getSelectedApprovedItems().length; i++) {
			CmSupplierItem catalog = this.getManagedBean()
					.getSelectedApprovedItems()[i];

			catalog.setItemStatus("U");
			reqDTO.getSaveSupplierItems().add(catalog);
		}

		CatalogueResDTO resDTO = new CatalogueResDTO();
		try {
			resDTO = catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			catalogueSO = null;
		}

		System.out.println("resDTO.isSuccessful():" + resDTO.isSuccessful());

		if (resDTO.isSuccessful()) {

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());

			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);

			System.out.println("getSmSupplier"
					+ smResDTO.getResultBean().getResultList().get(0));

			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList()
					.get(0).getSupplierId());

			reqDTO.setItemStatus("A");
			resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

			this.getManagedBean().setApprovedItemList(
					resDTO.getSupplierItemList());

			FacesContext facesContext = FacesContext.getCurrentInstance();

			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_710002,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

		}
	}

	public String compareSupplierItem() {

		log.info("==============compareSupplierItem========================");
		log.info("this.getManagedBean().getSelectViewItems().size() = "+this.getManagedBean().getSelectViewItems().size());
		this.getManagedBean().setSelectedSupplierItemList(null);
		this.getManagedBean().setContractedExist(false);
		ICatalogueSO catalogueSO = null;

		try{
			for (VCmSupplierItem view : this.getManagedBean().getSelectViewItems()) {
	
				catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
	
				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				System.out.println("view.getDocId()" + view.getDocId());
				reqDTO.setDocId(view.getDocId());
	
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
				System.out.println("*****************end********************");
	
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

	public void triggerCompare(int index) {

		System.out.println("***********triggerCompare************" + index);

		this.getManagedBean().setSelectedSupplierItemList(null);

		for (CmSupplierItem supplierItem : this.getManagedBean()
				.getSupplierItemList()) {
			if (supplierItem.isSelected()) {
				this.getManagedBean().getSelectedSupplierItemList()
						.add(supplierItem);
			}
		}

	}

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

	public void deleteComparedItem(int index) {

		boolean removeFlag = false;
		for (VCmSupplierItem itemView : this.getManagedBean()
				.getSelectViewItems()) {
			System.out.println("itemView.getDocId()" + itemView.getDocId());
			System.out.println("getSupplierItemId()"
					+ this.getManagedBean().getSelectedSupplierItemList()
							.get(index).getSupplierItemId());
			if (this.getManagedBean().getSelectedSupplierItemList().get(index)
					.getSupplierItemId() == itemView.getDocId()) {
				removeFlag = true;
				break;
			}

		}

		System.out.println("removeFlag" + removeFlag);
		if (removeFlag) {
			this.getManagedBean().getSelectViewItems().remove(index);
			this.getManagedBean().getSelectedSupplierItemList().remove(index);
		}

	}
	
	
	
	
	public void preRenderSupplierCatalogue() {

		

		this.getManagedBean().setItemNameFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.getManagedBean().setItemCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setSupplierNameFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
		this.getManagedBean().setContractNoFilterBean( new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setCircularNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setMofNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setEpNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));
		this.getManagedBean().setBizRegNoFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value()));


		// s1 module
		getManagedBean().setSimpleQuoteFlag(Boolean.TRUE);
		RequestNoteBackingBean requestNoteBackingBean = new RequestNoteBackingBean();
		requestNoteBackingBean.renewSesionObject();

		SimpleQuoteBackingBean simpleQuoteBackingBean = new SimpleQuoteBackingBean();
		simpleQuoteBackingBean.renewSesionObject();
		
		PurchaseInquiryBackingBean purchaseInquiryBackingBean  = new PurchaseInquiryBackingBean();
		purchaseInquiryBackingBean.renewSesionObject();
		
		PurchaseInDefaultBackingBean purchaseInDefaultBackingBean = new PurchaseInDefaultBackingBean();
		purchaseInDefaultBackingBean.renewSesionObject();
		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "supplierItemView");

	}

	public void preRenderCatalogue() {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		try{
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
	
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierReqDTO.setUserId(pmUser.getUserId());
	
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierReqDTO);
	
			System.out.println("getSmSupplier"
					+ smResDTO.getResultBean().getResultList().get(0));
	
			reqDTO.setSupplierId(smResDTO.getResultBean().getResultList().get(0)
					.getSupplierId());
			reqDTO.setItemStatus("D");
			CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
	
			/*
			 * for(CmSupplierItem supplierItem: resDTO.getSupplierItemList()){
			 * 
			 * if(supplierItem.getzStdPrices()==null ||
			 * supplierItem.getzStdPrices().isEmpty()){ CmZonalStandardPricing zone
			 * = new CmZonalStandardPricing(); zone.setCmSupplierItem(supplierItem);
			 * 
			 * supplierItem.getzStdPrices().add(zone); } }
			 */
	
			System.out.println("*****************end********************");
			this.getManagedBean().setMaintainItemList(resDTO.getSupplierItemList());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "catalogMaintenanceFile");

	}

	public String backToPrev() {

		return "supplierItemView";
	}

	/*
	 * public void retrieveStandardDistrict(int index){
	 * 
	 * 
	 * 
	 * System.out.println("**********retrieveDistrict******************");
	 * 
	 * DistrictReqDTO reqDTO = new DistrictReqDTO();
	 * System.out.println("pricing.getPmState().getStateId()"
	 * +this.getManagedBean
	 * ().getSelectedMaintainItem().getzStdPrices().get(index
	 * ).getPmState().getStateId());
	 * 
	 * 
	 * reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().
	 * getzStdPrices().get(index).getPmState().getStateId()); IDistrictSO
	 * districtSO = (IDistrictSO)
	 * BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
	 * 
	 * DistrictResDTO resDTO = districtSO .retriveDistrictListByStateID(reqDTO);
	 * System
	 * .out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index)
	 * .setPmDistricts(resDTO.getDistrictList());
	 * 
	 * if(resDTO.getDistrictList()!=null && resDTO.getDistrictList().size()>0){
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getzStdPrices().get(index)
	 * .setDistrictDisabled(false); }else{
	 * this.getManagedBean().getSelectedMaintainItem
	 * ().getzStdPrices().get(index).setDistrictDisabled(true); }
	 * 
	 * 
	 * 
	 * }
	 */

	/*
	 * public void disableState(){
	 * 
	 * System.out.println("**********disableState******************");
	 * 
	 * 
	 * 
	 * 
	 * Long districtId =
	 * this.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricing
	 * ().getPmDistrict().getDistrictId();
	 * System.out.println("districtId:"+districtId); if(districtId == 0 ){
	 * this.getManagedBean
	 * ().getSelectedMaintainItem().getCmZonalStandardPricing(
	 * ).setStateDisabled(false); }else{
	 * this.getManagedBean().getSelectedMaintainItem
	 * ().getCmZonalStandardPricing().setStateDisabled(true); } }
	 */

	/*
	 * public void retrievePromotionDistrict(int index){
	 * 
	 * System.out.println("**********retrievePromotionDistrict******************"
	 * );
	 * 
	 * DistrictReqDTO reqDTO = new DistrictReqDTO();
	 * System.out.println("pricing.getPmState().getStateId()"
	 * +this.getManagedBean
	 * ().getSelectedMaintainItem().getCmZonalPromotionPricings
	 * ().get(index).getPmState().getStateId());
	 * 
	 * 
	 * reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().
	 * getCmZonalPromotionPricings().get(index).getPmState().getStateId());
	 * IDistrictSO districtSO = (IDistrictSO)
	 * BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
	 * 
	 * DistrictResDTO resDTO = districtSO .retriveDistrictListByStateID(reqDTO);
	 * System
	 * .out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings
	 * ().get(index).setPmDistricts(resDTO.getDistrictList());
	 * 
	 * if(resDTO.getDistrictList()!=null && resDTO.getDistrictList().size()>0){
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings
	 * ().get(index).setDistrictDisabled(false); }else{
	 * this.getManagedBean().getSelectedMaintainItem
	 * ().getCmZonalPromotionPricings().get(index).setDistrictDisabled(true); }
	 * 
	 * }
	 */

	/*
	 * public void retrieveTierDistrict(int index){
	 * 
	 * System.out.println("**********retrieveDistrict******************");
	 * 
	 * this.getManagedBean().setDistrictList(null); DistrictReqDTO reqDTO = new
	 * DistrictReqDTO();
	 * System.out.println("pricing.getPmState().getStateId()"+this
	 * .getManagedBean
	 * ().getSelectedMaintainItem().getCmZonalTierPricings().get(index
	 * ).getPmState().getStateId());
	 * 
	 * 
	 * reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().
	 * getCmZonalTierPricings().get(index).getPmState().getStateId());
	 * IDistrictSO districtSO = (IDistrictSO)
	 * BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
	 * 
	 * DistrictResDTO resDTO = districtSO .retriveDistrictListByStateID(reqDTO);
	 * System
	 * .out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings().get
	 * (index).setPmDistricts(resDTO.getDistrictList());
	 * 
	 * }
	 */

	/*
	 * public void retrievePromotionTierDistrict(int index){
	 * System.out.println("**********retrieveDistrict******************");
	 * 
	 * this.getManagedBean().setDistrictList(null); DistrictReqDTO reqDTO = new
	 * DistrictReqDTO();
	 * 
	 * 
	 * reqDTO.setStateId(this.getManagedBean().getSelectedMaintainItem().
	 * getCmZonalPromotionTierPricings().get(index).getPmState().getStateId());
	 * IDistrictSO districtSO = (IDistrictSO)
	 * BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
	 * 
	 * DistrictResDTO resDTO = districtSO .retriveDistrictListByStateID(reqDTO);
	 * System
	 * .out.println("resDTO.getDistrictList()"+resDTO.getDistrictList().size());
	 * this
	 * .getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings
	 * ().get(index).setPmDistricts(resDTO.getDistrictList());
	 * 
	 * }
	 */

	public void addCatalogueRemarks() {

		CmItemRejectReason reason = new CmItemRejectReason();

		this.getManagedBean().getSelectedMaintainItem()
				.getCmItemRejectReasons().add(reason);

	}

	public String navigateToSearchCode() {

		return "codificationView";
	}

	public String cancelToSupplierItemSearch() {
		
		/*log.info("testing");
		ICatalogueSO a = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueResDTO resDTO = new CatalogueResDTO();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		
		reqDTO.setSupplierItemId(new Long("261"));
		resDTO = a.findActivePricing(reqDTO);
		
		log.info("resDTO.getPriceLs().size() = "+resDTO.getPriceLs().size());
		log.info("resDTO.getPriceLs().getPricingId() = "+resDTO.getPriceLs().get(0).getPricingId());
		log.info("resDTO.getPriceLs().get(0).getCmSupplierZone().getSuppZoneId() = "+resDTO.getPriceLs().get(0).getCmSupplierZone().getSuppZoneId());
		log.info("resDTO.getPriceLs().get(0).getCmSupplierZone().getCmSupplierZoneDtls().get(0).getSuppZoneDtlId() = "+resDTO.getPriceLs().get(0).getCmSupplierZone().getCmSupplierZoneDtls().get(0).getSuppZoneDtlId());

		*/return "supplierItemView";
	}

	public void enableRemark() {

		ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();

		bb.getManagedBean().setCatDetTabActiveIndex(2);

	}

	public void compareViewItem() {

		System.out
				.println("this.getManagedBean().getSelectViewItems().length():"
						+ this.getManagedBean().getSelectViewItems());
	}

	public void popUpPricing(CmSupplierItem item) {

		System.out.println("***************item*********************");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierId(item.getSmSupplier().getSupplierId());
		CatalogueResDTO resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
		System.out.println("size:" + resDTO.getSupplierZoneLs().size());
		this.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
		this.getManagedBean().setSupplierItemObj(item);
		this.getManagedBean().setTabActiveIndex(0);
	}

	public void findDistrict() {

		System.out.println("*****************findDistrict***************");
		System.out.println("Id:" + this.getManagedBean().getStateId());

		this.getManagedBean().setDistrictList(null);
		DistrictReqDTO reqDTO = new DistrictReqDTO();

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);

		System.out.println("*********************************");
		for (Long stateId : this.getManagedBean().getStateId()) {
			System.out.println("stateId:" + stateId);
			reqDTO.setStateId(stateId);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);

			for (PmDistrict district : resDTO.getDistrictList()) {

				this.getManagedBean().getDistrictList().add(district);

			}

		}

		System.out.println("this.getManagedBean().getDistrictList().size():"
				+ this.getManagedBean().getDistrictList().size());

	}

	public void refreshImage() {

		log.info("********refreshImage*********");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"addCatalogEdit.xhtml?faces-redirect=true");

	}

	public boolean isRemarkRendered() {

		for (int i = 0; i < this.getManagedBean().getRejectReasonIds().size(); i++) {
			String id = String.valueOf(this.getManagedBean()
					.getRejectReasonIds().get(i));
			if (id.equals("10010")) {
				return true;
			}
		}

		return false;
	}

	public void setRowIndex() {

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("index");
		int index = Integer.parseInt(name1);
		System.out.println("*************index***************" + index);

		this.getManagedBean().setTierRowIndex(index);

	}

	public void setZonalRowIndex() {

		FacesContext context = FacesContext.getCurrentInstance();
		Map map = context.getExternalContext().getRequestParameterMap();
		String name1 = (String) map.get("index");
		int index = Integer.parseInt(name1);
		System.out.println("*************index***************" + index);

		this.getManagedBean().setZonalTierRowIndex(index);

	}

	public void deleteTierPrice() {

		List<CmTierPricing> tierList = this.getManagedBean().getSelectedMaintainItem().getCmTierPricings();
		List<CmTierPricing> dataList = new ArrayList<CmTierPricing>();
		System.out.println("tierList:" + tierList.size());
		
		for (CmTierPricing cmPricing : tierList) {
			//System.out.println("cmPricing.getStand_group_id()" + cmPricing.getStand_group_id());
			//System.out.println("tierList.get(this.getManagedBean().getTierRowIndex()) "+ cmPricing.get(this.getManagedBean().getTierRowIndex()));
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

		System.out.println("dataList:" + dataList.size());
		for (CmTierPricing price : dataList) {
			this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
					.remove(price);
		}

	}

	public void deletePromotionTierPrice(int index) {

		Map<Date, List<CmPromotionTierPricing>> tierMap = groupingDate(this
				.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings());

		if (!tierMap.isEmpty() && tierMap.size() > 0) {

			List<CmPromotionTierPricing> dataList = (List) tierMap.get(this
					.getManagedBean().getSelectedMaintainItem()
					.getCmPromotionTierPricings().get(index).getStartDate());
			Integer groupingId = 0;
			for (CmPromotionTierPricing price : dataList) {
				groupingId = price.getStand_group_id();
				if (price.getParentPrice() != null) {
					price.getParentPrice().setStandardDisabled(false);
					price.setParentPrice(null);
				}
				this.getManagedBean().getSelectedMaintainItem()
						.getCmPromotionTierPricings().remove(price);
			}

			for (CmPromotionTierPricing price : this.getManagedBean()
					.getSelectedMaintainItem().getCmPromotionTierPricings()) {

				if (price.getStand_group_id().equals(groupingId)) {
					price.getParentPrice().setStandardDisabled(true);
				}
			}

		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710004,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	public void deleteZonalPromotionTierPrice(int index) {

		Map<Date, List<CmZonalPromotionTierPricing>> tierMap = groupingDate(this
				.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings());

		if (!tierMap.isEmpty() && tierMap.size() > 0) {

			List<CmZonalPromotionTierPricing> dataList = (List) tierMap
					.get(this.getManagedBean().getSelectedMaintainItem()
							.getCmZonalPromotionTierPricings().get(index)
							.getStartDate());
			Integer groupingId = 0;
			for (CmZonalPromotionTierPricing price : dataList) {

				groupingId = price.getStand_group_id();
				if (price.getParentPrice() != null) {
					price.getParentPrice().setStandardDisabled(false);
					price.setParentPrice(null);
				}
				this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalPromotionTierPricings().remove(price);
			}

			for (CmZonalPromotionTierPricing price : this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmZonalPromotionTierPricings()) {
				System.out.println("groupingId:" + groupingId);
				System.out.println("price.getStand_group_id()"
						+ price.getStand_group_id());
				if (price.getStand_group_id().equals(groupingId)) {
					price.getParentPrice().setStandardDisabled(true);
				}
			}

		}

		FacesContext facesContext = FacesContext.getCurrentInstance();

		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710004,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

	}

	public void deleteZonalStandardPrice(int index) {
		this.getManagedBean().getSelectedMaintainItem().getzStdPrices()
				.remove(index);
	}

	public void deleteZonalPromotionPrice(int index) {
		this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionPricings().remove(index);
	}

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

	public void cloneTierPrice(int index) {

		CmTierPricing tier = new CmTierPricing();
		tier.setStartDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getStartDate());
		tier.setEndDate(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getEndDate());
		tier.setShowFlag(false);
		tier.setStand_group_id(this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index).getStand_group_id());

		if (index < this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().size() - 1) {
			int tempIndex = index + 1;
			int markIndex = index + 1;

			while (this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(tempIndex) != null) {
				if (this.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(tempIndex).getStand_group_id() == this
						.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(index).getStand_group_id()) {
					markIndex = tempIndex;
				}
				if (tempIndex < this.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().size() - 1)
					tempIndex++;
				else {
					break;
				}
			}

			if(this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(markIndex).getToQty() != null){
				tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(markIndex).getToQty() + 1);
				this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
						.add(markIndex + 1, tier);
			} else {
				throw new EpException(MessageConst.ERROR_715010);
			}
		} else {
			tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(index).getToQty() + 1);
			this.getManagedBean().getSelectedMaintainItem().getCmTierPricings()
					.add(index + 1, tier);
		}

	}

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

		if (index < this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalTierPricings().size() - 1) {
			int tempIndex = index + 1;
			int markIndex = index + 1;

			while (this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(tempIndex) != null) {
				if (this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().get(tempIndex)
						.getStand_group_id() == this.getManagedBean()
						.getSelectedMaintainItem().getCmZonalTierPricings()
						.get(index).getStand_group_id()) {

					markIndex = tempIndex;
				}
				if (tempIndex < this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().size() - 1)
					tempIndex++;
				else {
					break;
				}
			}
			
			if(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(markIndex).getToQty() != null){
				tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().get(markIndex).getToQty() + 1);
				this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().add(markIndex + 1, tier);
			} else {
				throw new EpException(MessageConst.ERROR_715010);
			}
		} else {
			tier.setFromQty(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(index).getToQty() + 1);
			this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().add(index + 1, tier);
		}

	}

	public void validateDateOverlap(int index) {

		List<CmTierPricing> previousList = new ArrayList<CmTierPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(i));

		CmTierPricing tier = this.getManagedBean().getSelectedMaintainItem()
				.getCmTierPricings().get(index);
		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmTierPricing cmPricing : previousList) {

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
			}
		}
	}

	public void validateZonalStandardDateOverlap(int index) {

		List<CmZonalStandardPricing> previousList = new ArrayList<CmZonalStandardPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getzStdPrices().get(i));

		CmZonalStandardPricing zonalStandard = this.getManagedBean()
				.getSelectedMaintainItem().getzStdPrices().get(index);
		if (zonalStandard.getStartDate() != null
				&& zonalStandard.getEndDate() != null) {
			for (CmZonalStandardPricing cmPricing : previousList) {
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
			}
		}
	}

	public void validateZonalPromoDateOverlap(int index) {

		List<CmZonalPromotionPricing> previousList = new ArrayList<CmZonalPromotionPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalPromotionPricings().get(i));

		CmZonalPromotionPricing zonalPromo = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalPromotionPricings()
				.get(index);
		if (zonalPromo.getStartDate() != null
				&& zonalPromo.getEndDate() != null) {
			for (CmZonalPromotionPricing cmPricing : previousList) {
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
			}
		}
	}

	public void validateZonalTierDateOverlap(int index) {

		List<CmZonalTierPricing> previousList = new ArrayList<CmZonalTierPricing>();
		for (int i = 0; i < index; i++)
			previousList.add(this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(i));

		CmZonalTierPricing tier = this.getManagedBean()
				.getSelectedMaintainItem().getCmZonalTierPricings().get(index);
		if (tier.getStartDate() != null && tier.getEndDate() != null) {
			for (CmZonalTierPricing cmPricing : previousList) {
				System.out.println("tier.getCmSupplierZone().getSuppZoneId()"
						+ tier.getCmSupplierZone().getSuppZoneId());
				System.out
						.println("cmPricing.getCmSupplierZone().getSuppZoneId()"
								+ cmPricing.getCmSupplierZone().getSuppZoneId());
				if (tier.getCmSupplierZone().getSuppZoneId() == cmPricing
						.getCmSupplierZone().getSuppZoneId()) {
					System.out.println("*******in********");
					if ((tier.getStartDate()
							.compareTo(cmPricing.getStartDate()) >= 0 && tier
							.getStartDate().compareTo(cmPricing.getEndDate()) <= 0)
							|| (tier.getEndDate().compareTo(
									cmPricing.getStartDate()) >= 0 && tier
									.getEndDate().compareTo(
											cmPricing.getEndDate()) <= 0)) {
						tier.setDisplayAction(false);
						this.getManagedBean().setError4(true);
						break;
					} else {
						tier.setDisplayAction(true);
						this.getManagedBean().setError4(false);
					}
				} else {
					tier.setDisplayAction(true);
					this.getManagedBean().setError4(false);
				}
			}
		}
	}

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

		this.getManagedBean().setPricingTabIndex(1);
		this.getManagedBean().setTierPricingIndex(0);
	}

	public void updateZonalStandardErrorDialog(int index) {
		CmZonalStandardPricing cmTierPricing = this.getManagedBean()
				.getSelectedMaintainItem().getzStdPrices().get(index);

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
				|| cmTierPricing.getStartDate() == null
				|| cmTierPricing.getEndDate() == null
				|| cmTierPricing.getCmSupplierZone().getSuppZoneId() == 0) {
			this.getManagedBean().setError3(true);
		} else {
			this.getManagedBean().setError3(false);
		}

		this.getManagedBean().setPricingTabIndex(2);
		this.getManagedBean().setZonalPricingIndex(0);
	}

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

	public void updateStandardErrorDialog(CmStandardPricing standardPricing) {
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

	public void updatePromotionErrorDialog() {

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

			if (this.getManagedBean()
					.getSelectedMaintainItem()
					.getCmPromotionPricing()
					.getPrice()
					.compareTo(
							this.getManagedBean().getSelectedMaintainItem()
									.getCmStandardPricing().getPrice()) > 0) {
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

	public void updatePromotionTierErrorDialog(int index) {

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

		this.getManagedBean().setPricingTabIndex(3);
		this.getManagedBean().setZonalTierPricingIndex(0);
	}

	public boolean validateBeforeAddToZonalPromotion(
			List<CmZonalTierPricing> cmPricings) {

		boolean valid = true;
		int index;
		for (CmZonalTierPricing cmPricing : cmPricings) {
			index = cmPricings.indexOf(cmPricing);
			if (index > 0) {
				if (cmPricing.getToQty() == null
						|| cmPricing.getFromQty() <= cmPricings.get(index - 1)
								.getToQty()
						|| cmPricing.getToQty() <= cmPricing.getFromQty()) {
					valid = false;
					break;
				}
			}
		}
		return valid;
	}

	public boolean validateBeforeAddToPromotion(List<CmTierPricing> cmPricings) {

		boolean valid = true;
		int index;
		for (CmTierPricing cmPricing : cmPricings) {
			index = cmPricings.indexOf(cmPricing);
			if (index > 0) {
				if (cmPricing.getToQty() == null
						|| cmPricing.getFromQty() <= cmPricings.get(index - 1)
								.getToQty()
						|| cmPricing.getToQty() <= cmPricing.getFromQty()) {
					valid = false;
					break;
				}
			}
		}
		return valid;
	}

	public void addToPromotion(int index) {

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmPromotionTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
						.getCmPromotionTierPricings().size() > 0) {

			System.out.println("*************copy******************");

			Map<Date, List<CmTierPricing>> tierMap = groupingTier(this
					.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings());

			if (!tierMap.isEmpty() && tierMap.size() > 0) {

				List<CmTierPricing> dataList = (List) tierMap.get(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings().get(index).getStartDate());

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

						this.getManagedBean().getSelectedMaintainItem()
								.getCmPromotionTierPricings().add(price);

					}
					if (index < this.getManagedBean().getSelectedMaintainItem()
							.getCmTierPricings().size() - 1) {
						tempIndex = index;

						while (this.getManagedBean().getSelectedMaintainItem()
								.getCmTierPricings().get(tempIndex) != null) {
							if (this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmTierPricings()
									.get(tempIndex)
									.getStartDate()
									.compareTo(
											this.getManagedBean()
													.getSelectedMaintainItem()
													.getCmTierPricings()
													.get(index).getStartDate()) == 0) {
								this.getManagedBean().getSelectedMaintainItem()
										.getCmTierPricings().get(tempIndex)
										.setStandardDisabled(true);
							}
							if (tempIndex < this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmTierPricings().size() - 1)
								tempIndex++;
							else {
								break;
							}
						}

					} else {
						this.getManagedBean().getSelectedMaintainItem()
								.getCmTierPricings().get(index)
								.setStandardDisabled(true);
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
											" Promotion price cannot be added. Please check the From and To quantities.",
											""));
				}
			}

		} else {
			System.out.println("*************1st copy******************");

			List<CmTierPricing> tierList = this.getManagedBean()
					.getSelectedMaintainItem().getCmTierPricings();

			List<CmTierPricing> checkList = new ArrayList<CmTierPricing>();

			Date stDate = this.getManagedBean().getSelectedMaintainItem()
					.getCmTierPricings().get(index).getStartDate();

			for (CmTierPricing cmPricing : tierList) {
				if (cmPricing.getStartDate().compareTo(stDate) == 0) {
					checkList.add(cmPricing);
				}
			}

			if (validateBeforeAddToPromotion(checkList)) {

				Map<Date, List<CmTierPricing>> tierMap = groupingTier(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmTierPricings());

				if (!tierMap.isEmpty() && tierMap.size() > 0) {

					List<CmTierPricing> dataList = (List) tierMap.get(this
							.getManagedBean().getSelectedMaintainItem()
							.getCmTierPricings().get(index).getStartDate());

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

						this.getManagedBean().getSelectedMaintainItem()
								.getCmPromotionTierPricings().add(price);
					}
					if (index < this.getManagedBean().getSelectedMaintainItem()
							.getCmTierPricings().size() - 1) {
						tempIndex = index;

						while (this.getManagedBean().getSelectedMaintainItem()
								.getCmTierPricings().get(tempIndex) != null) {
							if (this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmTierPricings()
									.get(tempIndex)
									.getStartDate()
									.compareTo(
											this.getManagedBean()
													.getSelectedMaintainItem()
													.getCmTierPricings()
													.get(index).getStartDate()) == 0) {
								this.getManagedBean().getSelectedMaintainItem()
										.getCmTierPricings().get(tempIndex)
										.setStandardDisabled(true);
							}
							if (tempIndex < this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmTierPricings().size() - 1)
								tempIndex++;
							else {
								break;
							}
						}

					} else {
						this.getManagedBean().getSelectedMaintainItem()
								.getCmTierPricings().get(index)
								.setStandardDisabled(true);
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
										" New Promotion price cannot be added. Please check the From and To quantities.",
										""));
			}
		}

	}

	public void addToZonalPromotion(int index) {

		if (this.getManagedBean().getSelectedMaintainItem()
				.getCmZonalPromotionTierPricings() != null
				&& this.getManagedBean().getSelectedMaintainItem()
						.getCmZonalPromotionTierPricings().size() > 0) {

			Map<Date, List<CmZonalTierPricing>> tierMap = groupingTier(this
					.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings());

			if (!tierMap.isEmpty() && tierMap.size() > 0) {

				List<CmZonalTierPricing> dataList = (List) tierMap.get(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings().get(index).getStartDate());

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

						this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalPromotionTierPricings().add(price);

					}
					if (index < this.getManagedBean().getSelectedMaintainItem()
							.getCmZonalTierPricings().size() - 1) {
						tempIndex = index;

						while (this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalTierPricings().get(tempIndex) != null) {
							if (this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings()
									.get(tempIndex)
									.getStartDate()
									.compareTo(
											this.getManagedBean()
													.getSelectedMaintainItem()
													.getCmZonalTierPricings()
													.get(index).getStartDate()) == 0) {
								this.getManagedBean().getSelectedMaintainItem()
										.getCmZonalTierPricings()
										.get(tempIndex)
										.setStandardDisabled(true);
							}
							if (tempIndex < this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings().size() - 1)
								tempIndex++;
							else {
								break;
							}
						}

					} else {
						this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalTierPricings().get(index)
								.setStandardDisabled(true);
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
											" Promotion price cannot be added. Please check the From and To quantities.",
											""));
				}
			}

		} else {
			System.out.println("**********1st copy*******");

			List<CmZonalTierPricing> tierList = this.getManagedBean()
					.getSelectedMaintainItem().getCmZonalTierPricings();

			List<CmZonalTierPricing> checkList = new ArrayList<CmZonalTierPricing>();

			Date stDate = this.getManagedBean().getSelectedMaintainItem()
					.getCmZonalTierPricings().get(index).getStartDate();

			for (CmZonalTierPricing cmPricing : tierList) {
				if (cmPricing.getStartDate().compareTo(stDate) == 0) {
					checkList.add(cmPricing);
				}
			}

			if (validateBeforeAddToZonalPromotion(checkList)) {

				Map<Date, List<CmZonalTierPricing>> tierMap = groupingTier(this
						.getManagedBean().getSelectedMaintainItem()
						.getCmZonalTierPricings());

				if (!tierMap.isEmpty() && tierMap.size() > 0) {

					List<CmZonalTierPricing> dataList = (List) tierMap
							.get(this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings().get(index)
									.getStartDate());

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

						this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalPromotionTierPricings().add(price);
					}
					if (index < this.getManagedBean().getSelectedMaintainItem()
							.getCmZonalTierPricings().size() - 1) {
						tempIndex = index;

						while (this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalTierPricings().get(tempIndex) != null) {
							if (this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings()
									.get(tempIndex)
									.getStartDate()
									.compareTo(
											this.getManagedBean()
													.getSelectedMaintainItem()
													.getCmZonalTierPricings()
													.get(index).getStartDate()) == 0) {
								this.getManagedBean().getSelectedMaintainItem()
										.getCmZonalTierPricings()
										.get(tempIndex)
										.setStandardDisabled(true);
							}
							if (tempIndex < this.getManagedBean()
									.getSelectedMaintainItem()
									.getCmZonalTierPricings().size() - 1)
								tempIndex++;
							else {
								break;
							}
						}

					} else {
						this.getManagedBean().getSelectedMaintainItem()
								.getCmZonalTierPricings().get(index)
								.setStandardDisabled(true);
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
										" New Promotion price cannot be added. Please check the From and To quantities.",
										""));
			}
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

	public String itemRejectReason(long id) {

		for (PmStatusDesc desc : this.getManagedBean().getStatusDescList()) {

			if (desc.getStatusDescId().equals(id)) {
				return desc.getStatusName();
			}

		}

		return "";
	}

	public <T extends CmPricing> Map groupingTier(List<T> commonList) {

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
					System.out.println("sorted:" + sorted.getFromQty());
				}

				tierMap.put(dataList.get(0).getStartDate(), sortedList);
			}

		}
		return tierMap;

	}

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

	public <T extends CmPricing> List groupingStartDate(
			Map<Date, List<T>> tierMap) {

		SimpleDateFormat formot = new SimpleDateFormat("YYYYMMdd");
		List<Object> sortedList = new ArrayList<Object>();
		if (!tierMap.isEmpty() && tierMap.size() > 0) {
			Set keyset = tierMap.keySet();

			Object[] keysetarray = keyset.toArray();
			Object[] obj = new Object[keysetarray.length];
			System.out.println("keysetarray.length:" + keysetarray.length);
			for (int i = 0; i < keysetarray.length; i++) {
				System.out
						.println("******key:" + formot.format(keysetarray[i]));
				int count = 0;
				for (int j = 0; j < keysetarray.length; j++) {
					System.out.println("**************in****************");
					if (formot.format(keysetarray[i]).compareToIgnoreCase(
							formot.format(keysetarray[j])) > 0) {
						count = count + 1;
					}

				}
				System.out.println("count:" + count);
				System.out.println("i:" + i);
				List<T> dataList = (List) tierMap.get(keysetarray[i]);
				obj[count] = dataList;

			}

			for (int i = 0; i < obj.length; i++) {
				sortedList.add(obj[i]);
			}

		}
		return sortedList;
	}

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
			obj[count] = commonList.get(i);
		}

		List<T> sortedList = new ArrayList<T>();
		for (int i = 0; i < obj.length; i++) {
			sortedList.add((T) obj[i]);
		}

		for (T sorted : sortedList) {
			System.out.println("sorted:" + sorted.getStartDate());
		}

		return sortedList;

	}

	public String addToContractRequest(VCmSupplierItem supplierItem) {

		System.out.println("************getContractNo***************"
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
			System.out.println("size:"
					+ this.getManagedBean().getTempSupplierList().size());

			for (VCmSupplierItem temp : this.getManagedBean()
					.getTempSupplierList()) {
				System.out.println("temp.getDocId()" + temp.getDocId());
				System.out.println("keySet:"
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

	public void showTableView() {

		this.getManagedBean().setRenderTableView(true);
		this.getManagedBean().setRenderGridView(false);
		// clear list of selectedSupplierItem and ctRequestList
		this.getManagedBean().setSelectViewItems(null);
		this.getManagedBean().setCtRequestList(null);
	}

	public void showGridView() {

		this.getManagedBean().setRenderGridView(true);
		this.getManagedBean().setRenderTableView(false);
		// clear list of selectedSupplierItem
		this.getManagedBean().setSelectViewItems(null);
		this.getManagedBean().setCtRequestList(null);
	}

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

	public void deleteImage(int index) {

		this.getManagedBean().getSelectedMaintainItem().getCmImages()
				.remove(index);

	}

	public String navigateContractRequest() {

		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);

		if (this.getManagedBean().getCtRequestList() != null && this.getManagedBean().getCtRequestList().size() > 0) {
			VCmSupplierItem supplierItemView = this.getManagedBean().getCtRequestList().get(0);
			ContractReqDTO reqDTO = new ContractReqDTO();
		
			reqDTO.setContractId(supplierItemView.getContractId());
			System.out.println("supplierItemView.getContractId():"+ supplierItemView.getContractId());
			ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);
			if (resDTO.getContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_ONE_OFF)) {
				System.out.println("*************FULFILMENT_TYPE_ONE_OFF**************");
				FacesContext facesContext = FacesContext.getCurrentInstance();

				facesContext.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_710006,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			} else {
				System.out.println("*************not one off**************");

			}

		}

		return "contractRequestView";
	}

	public String backTocatalogueSearch() {

		return "supplierItemView?faces-redirect=true";
	}

	public void deleteContractRequestItem(int index) {

		VCmSupplierItem supplierItemView = this.getManagedBean()
				.getCtRequestList().get(index);

		this.getManagedBean().getTempMap().remove(supplierItemView.getDocId());

		this.getManagedBean().getCtRequestList().remove(supplierItemView);

	}

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
		ContractRequestBackingBean crBB = new ContractRequestBackingBean();
		crBB.getManagedBean().setvCmSupplierItemList(getManagedBean().getCtRequestList());
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		externalContext.getApplicationMap().put(facesScope.getUserSessionBean().getUserId().toString()+getManagedBean().getCtRequestList().get(0)
				.getContractId().toString(), getManagedBean().getCtRequestList());
		String url = "contract-request";

		try {
			externalContext.redirect(commonUrl
					+ "/"
					+ url
					+ "?contractId="
					+ this.getManagedBean().getCtRequestList().get(0)
							.getContractId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void resetCatalogueSearch() {
//		this.getManagedBean().getItemNameFilterBean().setSearchValue("");
//		this.getManagedBean().getItemCodeFilterBean().setSearchValue("");
//		this.getManagedBean().getSupplierNameFilterBean().setSearchValue("");
//		this.getManagedBean().getBizRegNoFilterBean().setSearchValue("");
//		this.getManagedBean().getEpNoFilterBean().setSearchValue("");
//		this.getManagedBean().getMofNoFilterBean().setSearchValue("");
//		this.getManagedBean().getContractNoFilterBean().setSearchValue("");
//		this.getManagedBean().getCircularNoFilterBean().setSearchValue("");
//		this.getManagedBean().setStateId(new long[0]);
//		this.getManagedBean().setDistrictList(null);
//		this.getManagedBean().setSimpleQuoteFlag(true);
//		this.getManagedBean().setCtRequestList(null);
//		this.getManagedBean().setSelectViewItems(null);
//		this.getManagedBean().getSearchDataModel().resetData();
				
		renewSesionObject();
	}
	
	public void addToList(VCmSupplierItem supplierItem,Integer optionListValue){
		log.info("addToList trigger");
		if(optionListValue.intValue()==1){
			if (!this.getManagedBean().getCtRequestList().contains(supplierItem)){
				System.out.println("************getContractNo***************"
						+ supplierItem.getContractNo());

				this.getManagedBean().setTempSupplierList(null);
				this.getManagedBean().getTempSupplierList().add(supplierItem);

				String contractNo = "";
				boolean flag = false;

				if (this.getManagedBean().getCtRequestList() != null && this.getManagedBean().getCtRequestList().size() > 0) {
					for (VCmSupplierItem item : this.getManagedBean().getCtRequestList()) {
						if (!item.getContractNo().equalsIgnoreCase(supplierItem.getContractNo())) {
							System.out.println("********************different Contact No.");

							flag = true;
							contractNo = item.getContractNo();
							break;
						}
					}
				}

				if (flag) {
					throw new EpException(MessageConst.ERROR_740009, new String[]{contractNo});
				} else {
					System.out.println("size:" + this.getManagedBean().getTempSupplierList().size());

					for (VCmSupplierItem temp : this.getManagedBean().getTempSupplierList()) {
						System.out.println("temp.getDocId()" + temp.getDocId());
						System.out.println("keySet:" + this.getManagedBean().getTempMap().keySet());
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
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Item is already in the list."));
			}
		}else if(optionListValue.intValue()==3){
			boolean found = false;
			for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
				if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
					FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Item is already in the list."));
					found = true;
					break;
				}
			}
			if(!found)
				this.getManagedBean().getSelectViewItems().add(supplierItem);
		}
	}
	
	public void onRowSelect(SelectEvent event) {
		VCmSupplierItem supplierItem = ((VCmSupplierItem)event.getObject());
		boolean found = false;
		
		for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
			if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "Item is already in the list."));
				found = true;
				break;
			}
		}
		
		if(!found)
			this.getManagedBean().getSelectViewItems().add(supplierItem);
		
        
    }
	
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
	
	public void removeFromList(VCmSupplierItem supplierItem){
		log.info("removeFromList trigger");
		for(VCmSupplierItem vCmSupplierItem: this.getManagedBean().getSelectViewItems()){
			if(vCmSupplierItem.getRowId()==supplierItem.getRowId()){
					this.getManagedBean().getSelectViewItems().remove(vCmSupplierItem);
					break;
			}
		}
	}
	
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
	
	public String cancelToPendingApprovalItem() {
	    
		return "/xhtml/cm/catalogue/catalogueTaskList.xhtml";
	}

	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getRoot1() {
		return root1;
	}

	public void setRoot1(TreeNode root1) {
		this.root1 = root1;
	}

	public TreeNode getRoot2() {
		return root2;
	}

	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	public void setFile(StreamedContent file) {
		this.file = file;
	}
}
