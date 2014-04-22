/**
 * Copyright �2013 Archisoft. All rights reserved.
 */
package my.ep.web.util.sc.helper;

import java.util.HashMap;
import java.util.Map;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.sc.directpurchase.IPurchaseInDefaultSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseInDefaultResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.sc.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.sc.directpurchase.ScWorkFlowStatus;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

public class PIDHelper {
	private static Logger log = EpLogger.getLogger(PIDHelper.class);
	//key need to be in CmItemId + PTJId
	private Map<String, ScPurchaseInDefault> pidMap = new HashMap<String, ScPurchaseInDefault>();
	private Map<Long, CtContractItem> contractItemMap = new HashMap<Long, CtContractItem>();
	private Map<Long, CtPanelItem> panelItemMap = new HashMap<Long, CtPanelItem>();
	private Map<Long, CmSupplierItem> supplierItemMap = new HashMap<Long, CmSupplierItem>();
	
	
	/**
	 * Get CtContractItem By Id
	 * 
	 * @param contractItemId - the contract Item Id
	 * @return CtContractItem
	 */
	public CtContractItem getContractItemById(Long contractItemId) {
		CtContractItem item = contractItemMap.get(contractItemId);
		if(item == null) {
			IContractItemSO contractItemSO;
			contractItemSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractItemReqDTO contractReqDTO = new ContractItemReqDTO();
			contractReqDTO.setContractItemId(contractItemId);
			ContractItemResDTO contractResDTO = contractItemSO.getContractItem(contractReqDTO);
			item = contractResDTO.getCtContractItem();
			contractItemMap.put(item.getScRequestItem().getCmItem().getItemId(), item);
		}
		return item;
	}
	
	/**
	 * Get Panel Item By Id
	 * 
	 * @param panelItemId - the panel item id
	 * @return CtPanelItem
	 */
	public CtPanelItem getPanelItemById(Long panelItemId) {
		//Panel item does not exist yet
		return null;
	}
	
	/**
	 * Get Supplier Item by id
	 * 
	 * @param supplierItemId - the supplier item id
	 * @return CmSupplierItem
	 */
	public CmSupplierItem getSupplierItemById(Long supplierItemId) {
		CmSupplierItem cmSupplierItem = supplierItemMap.get(supplierItemId);
		if(cmSupplierItem == null) {
			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
			CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
			catReqDTO.setDocId(supplierItemId);
			CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
			if (catResDTO.getSupplierItemList() != null) {
				cmSupplierItem = catResDTO.getSupplierItemList().get(0);
				supplierItemMap.put(supplierItemId, cmSupplierItem);
			}
		}
		return cmSupplierItem;
	}
	
	/**
	 * Get PID by Contract Item
	 * @param contractItemId
	 * @param ptjId
	 * @return ScPurchaseInDefault
	 */
	public ScPurchaseInDefault getPidByContractItem(Long contractItemId, Long ptjId) {
		log.debug("getPidByContractItem :- contractItemId : " + contractItemId + " ptjId : " + ptjId);
		CtContractItem contractItem = getContractItemById(contractItemId);
		//Primary key to recognise PID
		StringBuilder key = new StringBuilder()
		.append(contractItem.getScRequestItem().getCmItem().getItemId())
		.append(ptjId);
		ScPurchaseInDefault purchaseInDefault = pidMap.get(key.toString());
		if(purchaseInDefault == null) {
			
			if(contractItem != null) {
				IPurchaseInDefaultSO pidSO = getPIDSO();
				// Get PurchaseInDefault by contractedItem
				PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
				reqDTO.setContractItem(contractItem);
				PurchaseInDefaultResDTO resDTO = pidSO.getPurhcaseInDefaultByContractItemId(reqDTO);
				purchaseInDefault = resDTO.getPurchaseInDefaultDtl();
				if(purchaseInDefault != null) {
					//if PID exist, status(!complete) => disabled PID btn and enable Add to cart item
					
					pidMap.put(key.toString(), purchaseInDefault);
					debugLogger("PIDHelper :- getPidByContractItem - contractItemId : " + contractItemId);
				}
				else {
					//PID does not exist
					debugLogger("PIDHelper :- Enable PID");
				}
			}
		}
		
		return purchaseInDefault;
		
	}
	
	/**
	 * Get PID by panel Item
	 * @param panelItemId
	 * @param ptjId
	 * @return ScPurchaseInDefault
	 */
	public ScPurchaseInDefault getPidByPanelItem(Long panelItemId, Long ptjId) {
//		CtPanelItem panelItem = getPanelItemById(panelItemId);
		ScPurchaseInDefault purchaseInDefault = null;
//		boolean toEnable;
//		if(panelItem != null) {
//			IPurchaseInDefaultSO pidSO = getPIDSO();
//			// Get PurchaseInDefault by contractedItem
//			PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
//			reqDTO.setPanelItem(panelItem);
//			//TODO change to panelItem
//			PurchaseInDefaultResDTO resDTO = pidSO.getPurhcaseInDefaultByContractItemId(reqDTO);
//			purchaseInDefault = resDTO.getPurchaseInDefaultDtl();
//			if(purchaseInDefault != null) {
//				//if PID exist, status(!complete) => disabled PID btn and enable Add to cart item
//				pidMap.put(panelItemId, purchaseInDefault);
//				toEnable = getShouldEnablePIDFlag(purchaseInDefault);
//				debugLogger("PIDHelper :- getPidByPabelItem - panelItemId : " + panelItemId);
//			}
//			else {
//				//PID does not exist
//				toEnable = true;
//				debugLogger("PIDHelper :- Enable PID");
//			}
//		}
		return purchaseInDefault;
		
	} 
	
	/**
	 * Check to disable or enable PID for contractItem/ panel Item
	 * by checking current workflow status of PID
	 * 
	 * @param purchaseInDefault - the purchaseInDefault
	 * @return boolean toEnable 
	 */ 
	public boolean getPidRequiredByStatus(ScPurchaseInDefault purchaseInDefault) {
		boolean isEnablePid = true;
		if(purchaseInDefault == null) {
			//No Existence of PID, ATC is disable
			isEnablePid = true;
			debugLogger("PIDHelper :- getEnablePIDFlagByPID :  " + isEnablePid);
		} 
		else {
			ScWorkFlowStatus status = getCurrentPIDStatus(purchaseInDefault);
			if(status != null) {
				if(!status.getPmStatus().getStatusId().equals(SourcingConstants.PID_COMPLETED)) {
					debugLogger("PIDHelper :- status of PID : " + status.getPmStatus().getStatusId());
					debugLogger("PIDHelper :- Disable PID");
					isEnablePid =  false;
				}
			}
			else {
				debugLogger("PIDHelper :- Enable PID");
				isEnablePid = true;
			}
		}
		return isEnablePid;
	} 
	
	public ScWorkFlowStatus getCurrentPIDStatus(ScPurchaseInDefault pid) {
		return getPIDSO().getCurrentStatus(pid);
	}
	
	/**
	 * Check whether to disable/ enable ATC button
	 * 
	 * @param cmItemId - the item id for CmItem
	 * @param ptjId - ptj Id
	 * @return boolean toEnableATC 
	 */
	public boolean isAddToCartRequired(Long cmItemId, Long ptjId) {
		//provide cmItemId and ptjId to check availability of contract item and panel Item
		boolean isEnableATC = false;
		boolean contractItemExist = true;
		//if no contract Item, ATC is enable
		if(!contractItemExist) {
			isEnableATC = true;
		}
		//if yes, check existence of contractItem/panelitem in ScPID
		StringBuilder key = new StringBuilder()
			.append(cmItemId)
			.append(ptjId);
		ScPurchaseInDefault purchaseInDefault = pidMap.get(key.toString());
		if(purchaseInDefault != null) {
			//if PID is already being applied and incomplete
			isEnableATC = !getPidRequiredByStatus(purchaseInDefault);
		}
		else {
			//if contractItem is exist and no PID 
			if(contractItemExist) {
				isEnableATC = false;
			}
		}
		return isEnableATC;
	}
	
	/**
	 * Check whether to enable/disable PurchaseInDefault Button
	 * 
	 * @param itemId - contractItemId or panelItemId
	 * @param ptjId - PTJ id
	 * @param isPanel - Flag to identified panel item or contract item
	 * @return
	 */
	public boolean isPIDRequired(Long itemId, Long ptjId, boolean isPanel) {
		System.out.println("isEnablePID :- "+ itemId);
		ScPurchaseInDefault purchaseInDefault = null;
		if(!isPanel) {
			purchaseInDefault = getPidByContractItem(itemId, ptjId);
		}
		else {
			purchaseInDefault = getPidByPanelItem(itemId, ptjId);
		}
		boolean isEnablePid = getPidRequiredByStatus(purchaseInDefault);
		return isEnablePid;
	}
	
	private void debugLogger(Object message) {
		if(log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	private IPurchaseInDefaultSO getPIDSO() {
		IPurchaseInDefaultSO pidSO = BusinessDelegate.getInstance().
				getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPurchaseInDefaultSO.class);
		return pidSO;
	}
	
 }
