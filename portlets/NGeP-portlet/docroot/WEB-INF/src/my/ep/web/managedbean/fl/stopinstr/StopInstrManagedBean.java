/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.stopinstr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * ManagedBean for Stop Instruction.
 *
 * @author Kieyun
 * @since 7/7/2014
 */
@ManagedBean
@SessionScoped
public class StopInstrManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The selected tab index. */
	private int selectedTabIndex;
	
	/** The stop instr. */
	private FlStopInstr stopInstr;
	
	/** The charge line item root. */
	private TreeNode chargeLineItemRoot ;
	
	/** The reason descs. */
	private List<PmReasonDesc> reasonDescs;
	
	/** The button val. */
	private String buttonVal;
	
	//gpki signing
	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	/** The auto update. */
	private boolean autoUpdate;
	
	/** The text before sign. */
	private String textBeforeSign;
	
	/** The button action. */
	private String buttonAction;
	
	/** The back to document. */
	private String backToDocument;
	
	/** The spki backing bean. */
    @ManagedProperty(value="#{spkiBackingBean}")
    private SpkiBackingBean spkiBackingBean;
    
    private boolean gfmasError=false;
    private String gfmasErrorContent;
    private List<PmUser> approverList;
    
    private boolean hyperLinkPage = false;
    private boolean issuanceCogstrCheck;
    private boolean coGstr;
    
	/**
	 * Gets the reason descs.
	 *
	 * @return the reason descs
	 */
	public List<PmReasonDesc> getReasonDescs() {
		if(reasonDescs==null) {
			reasonDescs = new ArrayList<PmReasonDesc>();
		}
		return reasonDescs;
	}

	/**
	 * Sets the reason descs.
	 *
	 * @param reasonDescs the new reason descs
	 */
	public void setReasonDescs(List<PmReasonDesc> reasonDescs) {
		this.reasonDescs = reasonDescs;
	}

	/**
	 * Gets the stop instr.
	 *
	 * @return the stop instr
	 */
	public FlStopInstr getStopInstr() {
		return stopInstr;
	}

	/**
	 * Sets the stop instr.
	 *
	 * @param stopInstr the new stop instr
	 */
	public void setStopInstr(FlStopInstr stopInstr) {
		this.stopInstr = stopInstr;
	}

	/**
	 * Gets the charge line item root.
	 *
	 * @return the charge line item root
	 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}

	/**
	 * Sets the charge line item root.
	 *
	 * @param chargeLineItemRoot the new charge line item root
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
	}

	/**
	 * Gets the selected tab index.
	 *
	 * @return the selected tab index
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 *
	 * @param selectedTabIndex the new selected tab index
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
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
	 * Gets the back to document.
	 *
	 * @return the backToDocument
	 */
	public String getBackToDocument() {
		return backToDocument;
	}

	/**
	 * Sets the back to document.
	 *
	 * @param backToDocument the backToDocument to set
	 */
	public void setBackToDocument(String backToDocument) {
		this.backToDocument = backToDocument;
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
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		if(approverList == null){
			setApproverList(new ArrayList<PmUser>());
		}
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<PmUser> approverList) {
		this.approverList = approverList;
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

	/**
	 * @return the issuanceCogstrCheck
	 */
	public boolean isIssuanceCogstrCheck() {
		return issuanceCogstrCheck;
	}

	/**
	 * @param issuanceCogstrCheck the issuanceCogstrCheck to set
	 */
	public void setIssuanceCogstrCheck(boolean issuanceCogstrCheck) {
		this.issuanceCogstrCheck = issuanceCogstrCheck;
	}

	/**
	 * @return the coGstr
	 */
	public boolean isCoGstr() {
		return coGstr;
	}

	/**
	 * @param coGstr the coGstr to set
	 */
	public void setCoGstr(boolean coGstr) {
		this.coGstr = coGstr;
	}

}
