package my.ep.web.managedbean.sc;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationResultDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommend;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

/**
 * Class to handle user session data corresponding to
 * ConsolidationReportBackingBean
 * 
 * @author Praneeth
 * 
 * @version 1.1
 * @since 14/01/2016
 */
@ManagedBean(name = "consolidationReportManagedBean")
@SessionScoped
public class ConsolidationReportManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private int activeIndex;
	private int zoneActiveIndex;
	private Long qtId;
	private String outcome;
	private String processName;
	private String committeeType;
	private String attachmentDocType;
	private String maxRank;

	// MenuModel
	private MenuModel zoneMenuModel;

	// Header
	private ScQt scQt;
	private ScQtFinalization scQtFinalization;
	private ScQtCommittee scQtCommittee;
	private List<TplSpecZoneDtl> zoneDetails;
	private ScQtFinalizationReport selectedFinalizationReport;

	// Briefing Paper/Kertas Taklimat
	private List<ScQtFinalizationReport> finalizationReports;
	private StreamedContent streamContent;

	private boolean isSubmit;
	private boolean itemized;

	private TplSpecZoneDtl selectedZone;

	private List<ScCommitteeMember> committeeMembers;

	private List<ScQtRecommend> itemsList;
	private List<ScQtEvaluationResultDtl> packageQualifiedSuppliers;
	private List<ScQtEvaluationResultDtl> qualifiedSuppliers;
	private List<ScQtEvaluationResultDtl> toBeSavedQualifiedSuppliers;
	private List<ScQtFinalizationReport> selectedFinalizationReports;

	private Map<Long, TplSpecZoneDtl> zoneMap;
	private Map<Long, List<ScQtEvaluationResultDtl>> qualifiedSupppliersByZone;

	private List<SmPersonnel> boardofDirectors;

	private List<ScQtFinalizationRemarks> finalizationRemars;

	public ConsolidationReportManagedBean() {
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the zoneActiveIndex
	 */
	public int getZoneActiveIndex() {
		return zoneActiveIndex;
	}

	/**
	 * @param zoneActiveIndex
	 *            the zoneActiveIndex to set
	 */
	public void setZoneActiveIndex(int zoneActiveIndex) {
		this.zoneActiveIndex = zoneActiveIndex;
	}

	/**
	 * @return the qtId
	 */
	public Long getQtId() {
		return qtId;
	}

	/**
	 * @param qtId
	 *            the qtId to set
	 */
	public void setQtId(Long qtId) {
		this.qtId = qtId;
	}

	/**
	 * @return the outcome
	 */
	public String getOutcome() {
		return outcome;
	}

	/**
	 * @param outcome
	 *            the outcome to set
	 */
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName
	 *            the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * @param committeeType
	 *            the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the attachmentDocType
	 */
	public String getAttachmentDocType() {
		return attachmentDocType;
	}

	/**
	 * @param attachmentDocType
	 *            the attachmentDocType to set
	 */
	public void setAttachmentDocType(String attachmentDocType) {
		this.attachmentDocType = attachmentDocType;
	}

	/**
	 * @return the maxRank
	 */
	public String getMaxRank() {
		return maxRank;
	}

	/**
	 * @param maxRank
	 *            the maxRank to set
	 */
	public void setMaxRank(String maxRank) {
		this.maxRank = maxRank;
	}

	/**
	 * @return the zoneMenuModel
	 */
	public MenuModel getZoneMenuModel() {
		return zoneMenuModel;
	}

	/**
	 * @param zoneMenuModel
	 *            the zoneMenuModel to set
	 */
	public void setZoneMenuModel(MenuModel zoneMenuModel) {
		this.zoneMenuModel = zoneMenuModel;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt
	 *            the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}

	/**
	 * @param scQtFinalization
	 *            the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}

	/**
	 * @return the scQtCommittee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * @param scQtCommittee
	 *            the scQtCommittee to set
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * @return the zoneDetails
	 */
	public List<TplSpecZoneDtl> getZoneDetails() {
		return zoneDetails;
	}

	/**
	 * @param zoneDetails
	 *            the zoneDetails to set
	 */
	public void setZoneDetails(List<TplSpecZoneDtl> zoneDetails) {
		this.zoneDetails = zoneDetails;
	}

	/**
	 * @return the selectedFinalizationReport
	 */
	public ScQtFinalizationReport getSelectedFinalizationReport() {
		return selectedFinalizationReport;
	}

	/**
	 * @param selectedFinalizationReport
	 *            the selectedFinalizationReport to set
	 */
	public void setSelectedFinalizationReport(
			ScQtFinalizationReport selectedFinalizationReport) {
		this.selectedFinalizationReport = selectedFinalizationReport;
	}

	/**
	 * @return the finalizationReports
	 */
	public List<ScQtFinalizationReport> getFinalizationReports() {
		return finalizationReports;
	}

	/**
	 * @param finalizationReports
	 *            the finalizationReports to set
	 */
	public void setFinalizationReports(
			List<ScQtFinalizationReport> finalizationReports) {
		this.finalizationReports = finalizationReports;
	}

	/**
	 * @return the streamContent
	 */
	public StreamedContent getStreamContent() {
		return streamContent;
	}

	/**
	 * @param streamContent
	 *            the streamContent to set
	 */
	public void setStreamContent(StreamedContent streamContent) {
		this.streamContent = streamContent;
	}

	/**
	 * @return the isSubmit
	 */
	public boolean isSubmit() {
		return isSubmit;
	}

	/**
	 * @param isSubmit
	 *            the isSubmit to set
	 */
	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}

	/**
	 * @return the itemized
	 */
	public boolean isItemized() {
		return itemized;
	}

	/**
	 * @param itemized
	 *            the itemized to set
	 */
	public void setItemized(boolean itemized) {
		this.itemized = itemized;
	}

	/**
	 * @return the selectedZone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * @param selectedZone
	 *            the selectedZone to set
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * @return the committeeMembers
	 */
	public List<ScCommitteeMember> getCommitteeMembers() {
		return committeeMembers;
	}

	/**
	 * @param committeeMembers
	 *            the committeeMembers to set
	 */
	public void setCommitteeMembers(List<ScCommitteeMember> committeeMembers) {
		this.committeeMembers = committeeMembers;
	}

	/**
	 * @return the itemsList
	 */
	public List<ScQtRecommend> getItemsList() {
		return itemsList;
	}

	/**
	 * @param itemsList
	 *            the itemsList to set
	 */
	public void setItemsList(List<ScQtRecommend> itemsList) {
		this.itemsList = itemsList;
	}

	/**
	 * @return the packageQualifiedSuppliers
	 */
	public List<ScQtEvaluationResultDtl> getPackageQualifiedSuppliers() {
		return packageQualifiedSuppliers;
	}

	/**
	 * @param packageQualifiedSuppliers
	 *            the packageQualifiedSuppliers to set
	 */
	public void setPackageQualifiedSuppliers(
			List<ScQtEvaluationResultDtl> packageQualifiedSuppliers) {
		this.packageQualifiedSuppliers = packageQualifiedSuppliers;
	}

	/**
	 * @return the qualifiedSuppliers
	 */
	public List<ScQtEvaluationResultDtl> getQualifiedSuppliers() {
		return qualifiedSuppliers;
	}

	/**
	 * @param qualifiedSuppliers
	 *            the qualifiedSuppliers to set
	 */
	public void setQualifiedSuppliers(
			List<ScQtEvaluationResultDtl> qualifiedSuppliers) {
		this.qualifiedSuppliers = qualifiedSuppliers;
	}

	/**
	 * @return the toBeSavedQualifiedSuppliers
	 */
	public List<ScQtEvaluationResultDtl> getToBeSavedQualifiedSuppliers() {
		return toBeSavedQualifiedSuppliers;
	}

	/**
	 * @param toBeSavedQualifiedSuppliers
	 *            the toBeSavedQualifiedSuppliers to set
	 */
	public void setToBeSavedQualifiedSuppliers(
			List<ScQtEvaluationResultDtl> toBeSavedQualifiedSuppliers) {
		this.toBeSavedQualifiedSuppliers = toBeSavedQualifiedSuppliers;
	}

	/**
	 * @return the selectedFinalizationReports
	 */
	public List<ScQtFinalizationReport> getSelectedFinalizationReports() {
		return selectedFinalizationReports;
	}

	/**
	 * @param selectedFinalizationReports
	 *            the selectedFinalizationReports to set
	 */
	public void setSelectedFinalizationReports(
			List<ScQtFinalizationReport> selectedFinalizationReports) {
		this.selectedFinalizationReports = selectedFinalizationReports;
	}

	/**
	 * @return the zoneMap
	 */
	public Map<Long, TplSpecZoneDtl> getZoneMap() {
		return zoneMap;
	}

	/**
	 * @param zoneMap
	 *            the zoneMap to set
	 */
	public void setZoneMap(Map<Long, TplSpecZoneDtl> zoneMap) {
		this.zoneMap = zoneMap;
	}

	/**
	 * @return the qualifiedSupppliersByZone
	 */
	public Map<Long, List<ScQtEvaluationResultDtl>> getQualifiedSupppliersByZone() {
		return qualifiedSupppliersByZone;
	}

	/**
	 * @param qualifiedSupppliersByZone
	 *            the qualifiedSupppliersByZone to set
	 */
	public void setQualifiedSupppliersByZone(
			Map<Long, List<ScQtEvaluationResultDtl>> qualifiedSupppliersByZone) {
		this.qualifiedSupppliersByZone = qualifiedSupppliersByZone;
	}

	/**
	 * @return the boardofDirectors
	 */
	public List<SmPersonnel> getBoardofDirectors() {
		return boardofDirectors;
	}

	/**
	 * @param boardofDirectors
	 *            the boardofDirectors to set
	 */
	public void setBoardofDirectors(List<SmPersonnel> boardofDirectors) {
		this.boardofDirectors = boardofDirectors;
	}

	/**
	 * @return the finalizationRemars
	 */
	public List<ScQtFinalizationRemarks> getFinalizationRemars() {
		return finalizationRemars;
	}

	/**
	 * @param finalizationRemars
	 *            the finalizationRemars to set
	 */
	public void setFinalizationRemars(
			List<ScQtFinalizationRemarks> finalizationRemars) {
		this.finalizationRemars = finalizationRemars;
	}

}
