/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.creatdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlDelOrderItem;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sc.common.ScRequestItemSpecOffer;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.DisciplinaryActionVO;

/**
 * Managed Bean for Create Delivery Order.
 *
 * @author Giribabu
 * @since 7/10/2013
 */
@ManagedBean(name = "doMB")
@SessionScoped
public class DeliveryOrderManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	private FlRemark flRemark = new FlRemark();
	private FlAttachment flAttachment = new FlAttachment();
	private List<FlRemark> remarkList;
	private List<FlAttachment> attachmentList;
	
	private FlDeliveryOrder flDeliveryOrder;
	private FlDeliveryAddress deliveryAddress;
	private FlFulfilmentOrder fulfilmentOrder;
    private FlFulfilmentRequest fulfilmentRequest;
    private FlDelOrderItem delOrderItem = new FlDelOrderItem();
	private FlDeliveryAddress selectedAddrRow;
	private FlDeliveryAddress selectedDelAddr;
	private boolean checkPtjFlg = true;
	private String fulfillmentReqId;
	private String fulfilmentOrderId;
	private String statusId;
	private List<FlFulfilmentOrder> pendingDoList;
	private List<FlDeliveryAddress>deliveryAddressList;
	private String docProcess;
	private String buttonVal;
	private String selectedLovDelAddr;
	private String supplierAddress;
	private String docType;
	private String statusDesc;
	private FlFulfilmentItemAddr selectedItemRow;
	private List <FlFulfilmentItemAddr>selectedItemRowList;
	private FlFulfilmentItemAddr selectedItemAmountSection;
	private List <FlFulfilmentItemAddr>selectedItemRowCalList;
	private List<FlFulfilmentNoteItem> rejectionList;
	private String itemSelectedForCal; 
	private int delQtyCal;
	private int delAmtCal;
	
	private PmUser pmUser;
	private String supplierDoRef;
	//tab menu
	private int selectedTabIndex;
	private boolean modificationCancel;
	private boolean docMaintainRemarks = false;
	private String fromPageUrl;
	private boolean fromDoPage;
	private boolean fromDoDashboardPage;
	private boolean prevHideRemarks;
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	private String buttonAction;
	
	//uom
	private String measurementCodes;	
	private String measurementNames;
	private FlStopInstr flStopInstr;
	private List<FlRemark> rejectedRemark;
	private boolean displayStopIntrDetls;
	private String reasonDesc;

	/** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    //gfma
    private boolean gfmasError=false;
    private String gfmasErrorCode;
    private String gfmasErrorContent;
    private String gfmasErrorDesc;
   
    //gst
    private Date estDeliveryDate;
    private Date gstDeclareDate;
    private Date gstEffDate;
    private String gstRegNo;
    private boolean gstAmtChanged;
    
    /** The back to previous. */
	private String backToPrevious;
	private boolean isPendingDelivery;
	private List<FlRemark> previousRemarkList;
	private List<FlAttachment> previousAttachmentList;	
	
	private boolean hyperLinkPage = false;
	private List <FlDeliveryAddress>selectedDeliveryAddressList;
	private boolean orderDisabledForSdCheck = false;
	private boolean delAddressSelected = false;
	private List<FlDeliveryOrder> submittedDoList;
	private String supplierUserName;
	//26279 - start
	private String disciplinaryActionMessage;
	private List<DisciplinaryActionVO> disciplineVO;
	private SmDisciplinaryAction smDisciplinaryAction;
	//26279 - end
	
	// 28855 - Start
	private String supplierOffer;
	private String newOfferedSpec;
	private String itemSpecification;
	private String itemName;
	private boolean dpFlag;
	private boolean contractFlag;
	private List<ScRequestItemSpecOffer> specificationList;
	private List<ScRequestItemSpecOffer> itemSpecList;
	// 28855 - End
	
	//#35508 start
	private String originalSupplierDoRef;
	//#35508 end
	private String backToNtf;//36519

	/**
	 * Gets the item selected for cal.
	 *
	 * @return the itemSelectedForCal
	 */
	public String getItemSelectedForCal() {
		return itemSelectedForCal;
	}

	/**
	 * Sets the item selected for cal.
	 *
	 * @param itemSelectedForCal the itemSelectedForCal to set
	 */
	public void setItemSelectedForCal(String itemSelectedForCal) {
		this.itemSelectedForCal = itemSelectedForCal;
	}

	/**
	 * Gets the fl remark.
	 *
	 * @return the flRemark
	 */
	public FlRemark getFlRemark() {
		return flRemark;
	}

	/**
	 * Sets the fl remark.
	 *
	 * @param flRemark            the flRemark to set
	 */
	public void setFlRemark(FlRemark flRemark) {
		this.flRemark = flRemark;
	}

	/**
	 * Gets the fl attachment.
	 *
	 * @return the flAttachment
	 */
	public FlAttachment getFlAttachment() {
		return flAttachment;
	}

	/**
	 * Sets the fl attachment.
	 *
	 * @param flAttachment            the flAttachment to set
	 */
	public void setFlAttachment(FlAttachment flAttachment) {
		this.flAttachment = flAttachment;
	}

	/**
	 * Gets the selected addr row.
	 *
	 * @return the selectedAddrRow
	 */
	public FlDeliveryAddress getSelectedAddrRow() {
		return selectedAddrRow;
	}

	/**
	 * Sets the selected addr row.
	 *
	 * @param selectedAddrRow the selectedAddrRow to set
	 */
	public void setSelectedAddrRow(FlDeliveryAddress selectedAddrRow) {
		this.selectedAddrRow = selectedAddrRow;
	}

	/**
	 * Gets the fulfillment req id.
	 *
	 * @return the fulfillmentReqId
	 */
	public String getFulfillmentReqId() {
		return fulfillmentReqId;
	}

	/**
	 * Sets the fulfillment req id.
	 *
	 * @param fulfillmentReqId the fulfillmentReqId to set
	 */
	public void setFulfillmentReqId(String fulfillmentReqId) {
		this.fulfillmentReqId = fulfillmentReqId;
	}

	/**
	 * @return the checkPtjFlg
	 */
	public boolean isCheckPtjFlg() {
		return checkPtjFlg;
	}

	/**
	 * Sets the check ptj flg.
	 *
	 * @param checkPtjFlg the checkPtjFlg to set
	 */
	public void setCheckPtjFlg(boolean checkPtjFlg) {
		this.checkPtjFlg = checkPtjFlg;
	}

	/**
	 * Gets the fl delivery order.
	 *
	 * @return the flDeliveryOrder
	 */
	public FlDeliveryOrder getFlDeliveryOrder() {
		return flDeliveryOrder;
	}

	/**
	 * Sets the fl delivery order.
	 *
	 * @param flDeliveryOrder the flDeliveryOrder to set
	 */
	public void setFlDeliveryOrder(FlDeliveryOrder flDeliveryOrder) {
		this.flDeliveryOrder = flDeliveryOrder;
	}

	/**
	 * Gets the fulfilment order id.
	 *
	 * @return the fulfilmentOrderId
	 */
	public String getFulfilmentOrderId() {
		return fulfilmentOrderId;
	}

	/**
	 * Sets the fulfilment order id.
	 *
	 * @param fulfilmentOrderId the fulfilmentOrderId to set
	 */
	public void setFulfilmentOrderId(String fulfilmentOrderId) {
		this.fulfilmentOrderId = fulfilmentOrderId;
	}

	/**
	 * Gets the status id.
	 *
	 * @return the statusId
	 */
	public String getStatusId() {
		return statusId;
	}

	/**
	 * Sets the status id.
	 *
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	/**
	 * Gets the pending do list.
	 *
	 * @return the pendingDoList
	 */
	public List<FlFulfilmentOrder> getPendingDoList() {
		return pendingDoList;
	}

	/**
	 * Gets the delivery address list.
	 *
	 * @return the deliveryAddressList
	 */
	public List<FlDeliveryAddress> getDeliveryAddressList() {
		return deliveryAddressList;
	}

	/**
	 * Sets the delivery address list.
	 *
	 * @param deliveryAddressList the deliveryAddressList to set
	 */
	public void setDeliveryAddressList(List<FlDeliveryAddress> deliveryAddressList) {
		this.deliveryAddressList = deliveryAddressList;
	}

	/**
	 * Sets the pending do list.
	 *
	 * @param pendingDoList the pendingDoList to set
	 */
	public void setPendingDoList(List<FlFulfilmentOrder> pendingDoList) {
		this.pendingDoList = pendingDoList;
	}

	/**
	 * Gets the delivery address.
	 *
	 * @return the deliveryAddress
	 */
	public FlDeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * Sets the delivery address.
	 *
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(FlDeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * Gets the fulfilment order.
	 *
	 * @return the fulfilmentOrder
	 */
	public FlFulfilmentOrder getFulfilmentOrder() {
		return fulfilmentOrder;
	}

	/**
	 * Sets the fulfilment order.
	 *
	 * @param fulfilmentOrder the fulfilmentOrder to set
	 */
	public void setFulfilmentOrder(FlFulfilmentOrder fulfilmentOrder) {
		this.fulfilmentOrder = fulfilmentOrder;
	}

	/**
	 * Gets the fulfilment request.
	 *
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * Sets the fulfilment request.
	 *
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * Gets the doc process.
	 *
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return docProcess;
	}

	/**
	 * Sets the doc process.
	 *
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		this.docProcess = docProcess;
	}

	/**
	 * Gets the button val.
	 *
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}

	/**
	 * Sets the button val.
	 *
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}

	/**
	 * Gets the selected lov del addr.
	 *
	 * @return the selectedLovDelAddr
	 */
	public String getSelectedLovDelAddr() {
		return selectedLovDelAddr;
	}

	/**
	 * Sets the selected lov del addr.
	 *
	 * @param selectedLovDelAddr the selectedLovDelAddr to set
	 */
	public void setSelectedLovDelAddr(String selectedLovDelAddr) {
		this.selectedLovDelAddr = selectedLovDelAddr;
	}

	/**
	 * Gets the doc type.
	 *
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * Sets the doc type.
	 *
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * Gets the selected del addr.
	 *
	 * @return the selectedDelAddr
	 */
	public FlDeliveryAddress getSelectedDelAddr() {
		return selectedDelAddr;
	}

	/**
	 * Sets the selected del addr.
	 *
	 * @param selectedDelAddr the selectedDelAddr to set
	 */
	public void setSelectedDelAddr(FlDeliveryAddress selectedDelAddr) {
		this.selectedDelAddr = selectedDelAddr;
	}

	/**
	 * Gets the del order item.
	 *
	 * @return the delOrderItem
	 */
	public FlDelOrderItem getDelOrderItem() {
		return delOrderItem;
	}

	/**
	 * Sets the del order item.
	 *
	 * @param delOrderItem the delOrderItem to set
	 */
	public void setDelOrderItem(FlDelOrderItem delOrderItem) {
		this.delOrderItem = delOrderItem;
	}

	/**
	 * Gets the status desc.
	 *
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}

	/**
	 * Sets the status desc.
	 *
	 * @param statusDesc the statusDesc to set
	 */
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	/**
	 * Gets the supplier address.
	 *
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * Sets the supplier address.
	 *
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * Gets the selected item row.
	 *
	 * @return the selectedItemRow
	 */
	public FlFulfilmentItemAddr getSelectedItemRow() {
		return selectedItemRow;
	}

	/**
	 * Sets the selected item row.
	 *
	 * @param selectedItemRow the selectedItemRow to set
	 */
	public void setSelectedItemRow(FlFulfilmentItemAddr selectedItemRow) {
		this.selectedItemRow = selectedItemRow;
	}

	/**
	 * Gets the selected item amount section.
	 *
	 * @return the selectedItemAmountSection
	 */
	public FlFulfilmentItemAddr getSelectedItemAmountSection() {
		return selectedItemAmountSection;
	}

	/**
	 * Sets the selected item amount section.
	 *
	 * @param selectedItemAmountSection the selectedItemAmountSection to set
	 */
	public void setSelectedItemAmountSection(
			FlFulfilmentItemAddr selectedItemAmountSection) {
		this.selectedItemAmountSection = selectedItemAmountSection;
	}

	/**
	 * Gets the selected item row list.
	 *
	 * @return the selectedItemRowList
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemRowList() {
		return selectedItemRowList;
	}

	/**
	 * Sets the selected item row list.
	 *
	 * @param selectedItemRowList the selectedItemRowList to set
	 */
	public void setSelectedItemRowList(
			List<FlFulfilmentItemAddr> selectedItemRowList) {
		this.selectedItemRowList = selectedItemRowList;
	}

	/**
	 * Gets the selected item row cal list.
	 *
	 * @return the selectedItemRowCalList
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemRowCalList() {
		return selectedItemRowCalList;
	}

	/**
	 * Sets the selected item row cal list.
	 *
	 * @param selectedItemRowCalList the selectedItemRowCalList to set
	 */
	public void setSelectedItemRowCalList(
			List<FlFulfilmentItemAddr> selectedItemRowCalList) {
		this.selectedItemRowCalList = selectedItemRowCalList;
	}

	/**
	 * Gets the del qty cal.
	 *
	 * @return the delQtyCal
	 */
	public int getDelQtyCal() {
		return delQtyCal;
	}

	/**
	 * Sets the del qty cal.
	 *
	 * @param delQtyCal the delQtyCal to set
	 */
	public void setDelQtyCal(int delQtyCal) {
		this.delQtyCal = delQtyCal;
	}

	/**
	 * Gets the del amt cal.
	 *
	 * @return the delAmtCal
	 */
	public int getDelAmtCal() {
		return delAmtCal;
	}

	/**
	 * Sets the del amt cal.
	 *
	 * @param delAmtCal the delAmtCal to set
	 */
	public void setDelAmtCal(int delAmtCal) {
		this.delAmtCal = delAmtCal;
	}

	/**
	 * Gets the remark list.
	 *
	 * @return the remarkList
	 */
	public List<FlRemark> getRemarkList() {
		return remarkList;
	}

	/**
	 * Sets the remark list.
	 *
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<FlRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * Gets the attachment list.
	 *
	 * @return the attachmentList
	 */
	public List<FlAttachment> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * Sets the attachment list.
	 *
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<FlAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Gets the selected tab index.
	 *
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Gets the supplier do ref.
	 *
	 * @return the supplierDoRef
	 */
	public String getSupplierDoRef() {
		return supplierDoRef;
	}

	/**
	 * Sets the supplier do ref.
	 *
	 * @param supplierDoRef the supplierDoRef to set
	 */
	public void setSupplierDoRef(String supplierDoRef) {
		this.supplierDoRef = supplierDoRef;
	}

	/**
	 * Gets the rejection list.
	 *
	 * @return the rejection list
	 */
	public List<FlFulfilmentNoteItem> getRejectionList() {
		if(rejectionList==null) {
			rejectionList = new ArrayList<FlFulfilmentNoteItem>();
		}
		return rejectionList;
	}

	/**
	 * Sets the rejection list.
	 *
	 * @param rejectionList the new rejection list
	 */
	public void setRejectionList(List<FlFulfilmentNoteItem> rejectionList) {
		this.rejectionList = rejectionList;
	}

	/**
	 * Checks if is modification cancel.
	 *
	 * @return the modificationCancel
	 */
	public boolean isModificationCancel() {
		return modificationCancel;
	}

	/**
	 * Sets the modification cancel.
	 *
	 * @param modificationCancel the modificationCancel to set
	 */
	public void setModificationCancel(boolean modificationCancel) {
		this.modificationCancel = modificationCancel;
	}

	/**
	 * Checks if is doc maintain remarks.
	 *
	 * @return the docMaintainRemarks
	 */
	public boolean isDocMaintainRemarks() {
		return docMaintainRemarks;
	}

	/**
	 * Sets the doc maintain remarks.
	 *
	 * @param docMaintainRemarks the docMaintainRemarks to set
	 */
	public void setDocMaintainRemarks(boolean docMaintainRemarks) {
		this.docMaintainRemarks = docMaintainRemarks;
	}

	/**
	 * Gets the from page url.
	 *
	 * @return the from page url
	 */
	public String getFromPageUrl() {
		return fromPageUrl;
	}

	/**
	 * Sets the from page url.
	 *
	 * @param fromPageUrl the new from page url
	 */
	public void setFromPageUrl(String fromPageUrl) {
		this.fromPageUrl = fromPageUrl;
	}

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
	 * @param pmDigiSign the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Checks if is auto update.
	 *
	 * @return true, if is auto update
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * Sets the auto update.
	 *
	 * @param autoUpdate the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
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
	 * Sets the text before sign.
	 *
	 * @param textBeforeSign the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the button action.
	 *
	 * @return the button action
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * Sets the button action.
	 *
	 * @param buttonAction the new button action
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * Gets the measurement codes.
	 *
	 * @return the measurement codes
	 */
	public String getMeasurementCodes() {
		return measurementCodes;
	}

	/**
	 * Sets the measurement codes.
	 *
	 * @param measurementCodes the new measurement codes
	 */
	public void setMeasurementCodes(String measurementCodes) {
		this.measurementCodes = measurementCodes;
	}

	/**
	 * Gets the measurement names.
	 *
	 * @return the measurement names
	 */
	public String getMeasurementNames() {
		return measurementNames;
	}

	/**
	 * Sets the measurement names.
	 *
	 * @param measurementNames the new measurement names
	 */
	public void setMeasurementNames(String measurementNames) {
		this.measurementNames = measurementNames;
	}

	/**
	 * Gets the fl stop instr.
	 *
	 * @return the flStopInstr
	 */
	public FlStopInstr getFlStopInstr() {
		return flStopInstr;
	}

	/**
	 * Sets the fl stop instr.
	 *
	 * @param flStopInstr the flStopInstr to set
	 */
	public void setFlStopInstr(FlStopInstr flStopInstr) {
		this.flStopInstr = flStopInstr;
	}

	/**
	 * Gets the rejected remark.
	 *
	 * @return the rejectedRemark
	 */
	public List<FlRemark> getRejectedRemark() {
		return rejectedRemark;
	}

	/**
	 * Sets the rejected remark.
	 *
	 * @param rejectedRemark the rejectedRemark to set
	 */
	public void setRejectedRemark(List<FlRemark> rejectedRemark) {
		this.rejectedRemark = rejectedRemark;
	}

	/**
	 * Checks if is display stop intr detls.
	 *
	 * @return the displayStopIntrDetls
	 */
	public boolean isDisplayStopIntrDetls() {
		return displayStopIntrDetls;
	}

	/**
	 * Sets the display stop intr detls.
	 *
	 * @param displayStopIntrDetls the displayStopIntrDetls to set
	 */
	public void setDisplayStopIntrDetls(boolean displayStopIntrDetls) {
		this.displayStopIntrDetls = displayStopIntrDetls;
	}

	/**
	 * Gets the reason desc.
	 *
	 * @return the reasonDesc
	 */
	public String getReasonDesc() {
		return reasonDesc;
	}

	/**
	 * Sets the reason desc.
	 *
	 * @param reasonDesc the reasonDesc to set
	 */
	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}

	/**
	 * @return the spkiBackingBean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * @param spkiBackingBean the spkiBackingBean to set
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * @return the gfmasError
	 */
	public boolean isGfmasError() {
		return gfmasError;
	}

	/**
	 * @param gfmasError the gfmasError to set
	 */
	public void setGfmasError(boolean gfmasError) {
		this.gfmasError = gfmasError;
	}

	/**
	 * @return the gfmasErrorContent
	 */
	public String getGfmasErrorContent() {
		return gfmasErrorContent;
	}

	/**
	 * @param gfmasErrorContent the gfmasErrorContent to set
	 */
	public void setGfmasErrorContent(String gfmasErrorContent) {
		this.gfmasErrorContent = gfmasErrorContent;
	}

	/**
	 * @return the estDeliveryDate
	 */
	public Date getEstDeliveryDate() {
		return estDeliveryDate;
	}

	/**
	 * @param estDeliveryDate the estDeliveryDate to set
	 */
	public void setEstDeliveryDate(Date estDeliveryDate) {
		this.estDeliveryDate = estDeliveryDate;
	}

	/**
	 * @return the gstDeclareDate
	 */
	public Date getGstDeclareDate() {
		return gstDeclareDate;
	}

	/**
	 * @param gstDeclareDate the gstDeclareDate to set
	 */
	public void setGstDeclareDate(Date gstDeclareDate) {
		this.gstDeclareDate = gstDeclareDate;
	}

	/**
	 * @return the gstEffDate
	 */
	public Date getGstEffDate() {
		return gstEffDate;
	}

	/**
	 * @param gstEffDate the gstEffDate to set
	 */
	public void setGstEffDate(Date gstEffDate) {
		this.gstEffDate = gstEffDate;
	}

	/**
	 * @return the gstRegNo
	 */
	public String getGstRegNo() {
		return gstRegNo;
	}

	/**
	 * @param gstRegNo the gstRegNo to set
	 */
	public void setGstRegNo(String gstRegNo) {
		this.gstRegNo = gstRegNo;
	}

	public boolean isGstAmtChanged() {
		return gstAmtChanged;
	}

	public void setGstAmtChanged(boolean gstAmtChanged) {
		this.gstAmtChanged = gstAmtChanged;
	}

	/**
	 * @return the backToPrevious
	 */
	public String getBackToPrevious() {
		return backToPrevious;
	}

	/**
	 * @param backToPrevious the backToPrevious to set
	 */
	public void setBackToPrevious(String backToPrevious) {
		this.backToPrevious = backToPrevious;
	}

	/**
	 * @return back to Pending Delivery
	 */
	public boolean isPendingDelivery() {
		return isPendingDelivery;
	}

	/**
	 * @param isPendingDelivery to set
	 */
	public void setPendingDelivery(boolean isPendingDelivery) {
		this.isPendingDelivery = isPendingDelivery;
	}
	
	
	/**
	 * @return the hyperLinkPage
	 */
	public boolean isHyperLinkPage() {
		return hyperLinkPage;
	}

	/**
	 * @param hyperLinkPage the hyperLinkPage to set
	 */
	public void setHyperLinkPage(boolean hyperLinkPage) {
		this.hyperLinkPage = hyperLinkPage;
	}

	public List<FlRemark> getPreviousRemarkList() {
		return previousRemarkList;
	}

	public void setPreviousRemarkList(List<FlRemark> previousRemarkList) {
		this.previousRemarkList = previousRemarkList;
	}

	public List<FlAttachment> getPreviousAttachmentList() {
		return previousAttachmentList;
	}

	public void setPreviousAttachmentList(List<FlAttachment> previousAttachmentList) {
		this.previousAttachmentList = previousAttachmentList;
	}

	public boolean isFromDoPage() {
		return fromDoPage;
	}

	public void setFromDoPage(boolean fromDoPage) {
		this.fromDoPage = fromDoPage;
	}

	public boolean isFromDoDashboardPage() {
		return fromDoDashboardPage;
	}

	public void setFromDoDashboardPage(boolean fromDoDashboardPage) {
		this.fromDoDashboardPage = fromDoDashboardPage;
	}

	public String getGfmasErrorCode() {
		return gfmasErrorCode;
	}

	public void setGfmasErrorCode(String gfmasErrorCode) {
		this.gfmasErrorCode = gfmasErrorCode;
	}

	/**
	 * @return the selectedDeliveryAddressList
	 */
	public List<FlDeliveryAddress> getSelectedDeliveryAddressList() {
		if(selectedDeliveryAddressList==null) {
			selectedDeliveryAddressList = new ArrayList<FlDeliveryAddress>();
		}
		return selectedDeliveryAddressList;
	}

	/**
	 * @param selectedDeliveryAddressList the selectedDeliveryAddressList to set
	 */
	public void setSelectedDeliveryAddressList(
			List<FlDeliveryAddress> selectedDeliveryAddressList) {
		this.selectedDeliveryAddressList = selectedDeliveryAddressList;
	}

	public String getGfmasErrorDesc() {
		return gfmasErrorDesc;
	}

	public void setGfmasErrorDesc(String gfmasErrorDesc) {
		this.gfmasErrorDesc = gfmasErrorDesc;
	}

	/**
	 * @return the orderDisabledForSdCheck
	 */
	public boolean isOrderDisabledForSdCheck() {
		return orderDisabledForSdCheck;
	}

	/**
	 * @param orderDisabledForSdCheck the orderDisabledForSdCheck to set
	 */
	public void setOrderDisabledForSdCheck(boolean orderDisabledForSdCheck) {
		this.orderDisabledForSdCheck = orderDisabledForSdCheck;
	}

	/**
	 * @return the delAddressSelected
	 */
	public boolean isDelAddressSelected() {
		return delAddressSelected;
	}

	/**
	 * @param delAddressSelected the delAddressSelected to set
	 */
	public void setDelAddressSelected(boolean delAddressSelected) {
		this.delAddressSelected = delAddressSelected;
	}

	/**
	 * @return the submittedDoList
	 */
	public List<FlDeliveryOrder> getSubmittedDoList() {
		return submittedDoList;
	}

	/**
	 * @param submittedDoList the submittedDoList to set
	 */
	public void setSubmittedDoList(List<FlDeliveryOrder> submittedDoList) {
		this.submittedDoList = submittedDoList;
	}

	/**
	 * @return the supplierUserName
	 */
	public String getSupplierUserName() {
		return supplierUserName;
	}

	/**
	 * @param supplierUserName the supplierUserName to set
	 */
	public void setSupplierUserName(String supplierUserName) {
		this.supplierUserName = supplierUserName;
	}

	public boolean isPrevHideRemarks() {
		return prevHideRemarks;
	}

	public void setPrevHideRemarks(boolean prevHideRemarks) {
		this.prevHideRemarks = prevHideRemarks;
	}
	
	//26279 - start
	public String getDisciplinaryActionMessage() {
		return disciplinaryActionMessage;
	}

	public void setDisciplinaryActionMessage(String disciplinaryActionMessage) {
		this.disciplinaryActionMessage = disciplinaryActionMessage;
	}	

	public List<DisciplinaryActionVO> getDisciplineVO() {
		return disciplineVO;
	}

	public void setDisciplineVO(List<DisciplinaryActionVO> disciplineVO) {
		this.disciplineVO = disciplineVO;
	}

	public SmDisciplinaryAction getSmDisciplinaryAction() {
		return smDisciplinaryAction;
	}

	public void setSmDisciplinaryAction(SmDisciplinaryAction smDisciplinaryAction) {
		this.smDisciplinaryAction = smDisciplinaryAction;
	}
	//26279 - end
	
	// 28855 - start
	public String getSupplierOffer() {
		return supplierOffer;
	}

	public void setSupplierOffer(String supplierOffer) {
		this.supplierOffer = supplierOffer;
	}

	public String getNewOfferedSpec() {
		return newOfferedSpec;
	}

	public void setNewOfferedSpec(String newOfferedSpec) {
		this.newOfferedSpec = newOfferedSpec;
	}

	public String getItemSpecification() {
		return itemSpecification;
	}

	public void setItemSpecification(String itemSpecification) {
		this.itemSpecification = itemSpecification;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public boolean isDpFlag() {
		return dpFlag;
	}

	public void setDpFlag(boolean dpFlag) {
		this.dpFlag = dpFlag;
	}

	public boolean isContractFlag() {
		return contractFlag;
	}

	public void setContractFlag(boolean contractFlag) {
		this.contractFlag = contractFlag;
	}

	public List<ScRequestItemSpecOffer> getSpecificationList() {
		return specificationList;
	}

	public void setSpecificationList(List<ScRequestItemSpecOffer> specificationList) {
		this.specificationList = specificationList;
	}

	public List<ScRequestItemSpecOffer> getItemSpecList() {
		return itemSpecList;
	}

	public void setItemSpecList(List<ScRequestItemSpecOffer> itemSpecList) {
		this.itemSpecList = itemSpecList;
	}
	//28855 - End

	public String getOriginalSupplierDoRef() {
		return originalSupplierDoRef;
	}

	public void setOriginalSupplierDoRef(String originalSupplierDoRef) {
		this.originalSupplierDoRef = originalSupplierDoRef;
	}
	
	// 36519-Start
	/**
	 * @return the backToNtf
	 */
	public String getBackToNtf() {
		return backToNtf;
	}

	/**
	 * @param backToNtf
	 *            the backToNtf to set
	 */
	public void setBackToNtf(String backToNtf) {
		this.backToNtf = backToNtf;
	}
	// 36519-End

}
