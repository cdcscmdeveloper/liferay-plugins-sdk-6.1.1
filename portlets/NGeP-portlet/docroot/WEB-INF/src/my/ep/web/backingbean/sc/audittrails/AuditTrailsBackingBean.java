package my.ep.web.backingbean.sc.audittrails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.quationtender.IQTAuditTrailsSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.constant.sc.QTAuditConstant;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.audit.AuditQTDTO;
import my.ep.exception.EpException;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.au.AuditTrailsSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.audittrails.AuditTrailsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "auditTrailsBackingBean")
@RequestScoped
public class AuditTrailsBackingBean extends BaseBackingBean<AuditTrailsManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5747106368235143463L;
	private static Logger log = Logger.getLogger(AuditTrailsBackingBean.class);
	public static final String SC_TRACKING_DIARY_MANAGED_BEAN = "scTrackingDiaryManagedBean";
	private transient IPagingDataModel<AuditQTDTO> searchQT = new SearchQTPagingDataModel();

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
	}

	public String getUserName(Long userId) {
		if (userId.longValue() == CommonConstants.TRACKING_DIARY_SYSTEM_USER_ID) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale());
		}
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		if (userSO != null && resDTO.getPmUser() != null) {
			return resDTO.getPmUser().getUserName();
		} else {
			return "";
		}
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public String getRoleDescByRoleCode(String roleCode) {
		log.info("getRoleDescByRoleCode");
		if (roleCode == null || roleCode.isEmpty()) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale());
		}
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}

	public void redirectToAuditTrailsViews(String docType, Long docId) throws IOException, PortalException,
			SystemException {
		redirectToQtAuditTrailsViews(docType, docId);
	}

	public void redirectToQtAuditTrailsViews(String docType, Long docId) throws IOException, PortalException,
			SystemException {

		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setQtId(docId);
		this.getManagedBean().setActiveIndex(0);
		this.getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		navigateToGeneralInfoTab();

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/sc/quatationtender/auditTrails/auditTrailDetailsMain.xhtml");
	}

	/**
	 * Method to navigate back to previous page
	 * 
	 * @return the previous page
	 */
	public void backAction() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getBackURL());
	}

	/**
	 * #35500 To navigate to the selected tab
	 */
	public void onTabMenuChangeAction(Integer activeIndex) {
		log.info("onTabMenuChangeAction");
		int previousIndex = this.getManagedBean().getActiveIndex();

		if (previousIndex != activeIndex) {
			switch (previousIndex) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			}

			switch (activeIndex) {
			case 0:
				navigateToGeneralInfoTab();
				break;
			case 1:
				navigateToBriefingSiteVisitTab();
				break;
			case 2:
				navigateToSupplierListTab();
				break;
			case 3:
				navigateToDeskOfficerAssistantTab();
				break;
			case 4:
				navigateToDeskOfficerTab();
				break;
			case 5:
				navigateToSpecCommitteeAppointTab();
				break;
			case 6:
				navigateToEvalCommitteeAppointTab();
				break;
			case 7:
				navigateToTechCommitteeAppointTab();
				break;
			case 8:
				navigateToFinanceCommitteeAppointTab();
				break;
			case 9:
				navigateToOpenCommitteeAppointTab();
				break;
			case 10:
				navigateToQuotCommitteeAppointTab();
				break;
			case 11:
				navigateToProcBoardAppointTab();
				break;
			}
			this.getManagedBean().setActiveIndex(activeIndex);
		}
	}

	private void navigateToGeneralInfoTab() {
		log.info("navigateToGeneralInfoTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_GI);
		searchQT.reloadData();

	}

	private void navigateToBriefingSiteVisitTab() {
		log.info("navigateToBriefingSiteVisitTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_BSV);
		searchQT.reloadData();

	}

	private void navigateToSupplierListTab() {
		log.info("navigateToSupplierListTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SL);
		searchQT.reloadData();

	}

	private void navigateToDeskOfficerAssistantTab() {
		log.info("navigateToDeskOfficerAssistantTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DOA);
		searchQT.reloadData();

	}

	private void navigateToDeskOfficerTab() {
		log.info("navigateToDeskOfficerTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DO);
		searchQT.reloadData();

	}

	private void navigateToSpecCommitteeAppointTab() {
		log.info("navigateToSpecCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SC);
		searchQT.reloadData();

	}

	private void navigateToEvalCommitteeAppointTab() {
		log.info("navigateToEvalCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_EC);
		searchQT.reloadData();

	}

	private void navigateToTechCommitteeAppointTab() {
		log.info("navigateToTechCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_TEC);
		searchQT.reloadData();
	}

	private void navigateToOpenCommitteeAppointTab() {
		log.info("navigateToOpenCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_OC);
		searchQT.reloadData();

	}

	private void navigateToFinanceCommitteeAppointTab() {
		log.info("navigateToFinanceCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_FEC);
		searchQT.reloadData();

	}

	private void navigateToQuotCommitteeAppointTab() {
		log.info("navigateToQuotCommitteeAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_QC);
		searchQT.reloadData();

	}

	private void navigateToProcBoardAppointTab() {
		log.info("navigateToProcBoardAppointTab");
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditTrailsSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_PB);
		searchQT.reloadData();
	}

	private class SearchQTPagingDataModel extends PagingDataModel<AuditQTDTO> {

		private static final long serialVersionUID = 1L;

		public SearchQTPagingDataModel() {
			super("searchQtTblId");
		}

		/**
		 * pageChangeListener #35500
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditQTDTO> pageChangeListener(ISearchBean<AuditQTDTO> searchBean) {
			AuditTrailsSearchBean qtSearchBean = getManagedBean().getAuditTrailsSearchBean();
			qtSearchBean.setDocId(BigInteger.valueOf(getManagedBean().getQtId().intValue()));
			Long groupSectionId = getManagedBean().getGroupSectionId();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				IQTAuditTrailsSO qtAuditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailsSO.class);

				ISearchResultBean<AuditQTDTO> resDTO = null;
				resDTO = qtAuditSO.searchQTAuditTrailsViewResultList(languageCode, qtSearchBean.getDocId(), searchBean,
						groupSectionId);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, "AU", groupSectionId.toString(),
						"AuditTrailsBackingBean.pageChangeListener", getUserIdSessionFromLoginId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuditQTDTO> getSearchQT() {
		return searchQT;
	}

	public void setSearchQT(IPagingDataModel<AuditQTDTO> searchQT) {
		this.searchQT = searchQT;
	}

	// SECTION: Generate Report (PDF & EXCEL)

	/**
	 * Generate Report: Selected tab in PDF Format
	 * 
	 * @param docId
	 *            ,referring to selected REQUEST_NOTE_ID
	 * @throws IOException
	 */
	public void generateReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());
		paramMap.put(AuditReportConst.P_GROUP_SECTION_ID, getManagedBean().getGroupSectionId().toString());

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_QT, paramMap);
	}

	/**
	 * Generate Report: All Tab in PDF Format
	 * 
	 * @param docId
	 *            ,referring to selected REQUEST_NOTE_ID
	 * @throws IOException
	 */
	public void generateAllReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_QT, paramMap);
	}

	/**
	 * Retrieve audit trail detail transaction records.
	 * 
	 * @param docId
	 *            , referring to REQUEST_NOTE_ID
	 * @return
	 */
	public List<AuditQTDTO> getAuditTrailDetailsForExlRpt(BigInteger docId) {

		IQTAuditTrailsSO qtAuditSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailsSO.class);

		// return qtAuditSO.getQTAuditTrailsRptViewResultList(docId,
		// getManagedBean().getGroupSectionId());

		return qtAuditSO.getQTAuditTrailsRptViewResultList(docId, null);
	}

	/**
	 * Generate excel format.
	 * 
	 * @param docId
	 *            ,referring to REQUEST_NOTE_ID
	 */
	public void generateRptExcelForAuditTrailDetails(BigInteger docId) {
		List<AuditQTDTO> rsltReportList = new ArrayList<>();
		try {
			rsltReportList = getAuditTrailDetailsForExlRpt(docId);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, "AU", docId.toString(),
					"AuditSearchDPRequestNoteBackingBean.generateRptExcelForAuditTrailDetails",
					getUserIdSessionFromLoginId(), e);
		}

		// Workbook wb = new XSSFWorkbook();
		// Sheet sheetRptAudit =
		// wb.createSheet(getCurrentReportTab(getManagedBean().getGroupSectionId()));
		//
		// // Setting Report - Populate Table Header Cell Value
		// String[] colHeaderDataTableRpt =
		// ReportExcelAuditConstant.ColHeaderListType4;
		//
		// CellStyle cellStyleTitleRpt = new
		// ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();
		//
		// // Section - Setting Table Header Cell Style for each cell
		// CellStyle colHeaderDataTableRptStyle = new
		// ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
		// .setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();
		//
		// // Section - Setting Cell Style for No Records Found message.
		// CellStyle noRecordsStyle = new
		// ExcelCellStyleBuilder(wb).buildDefaultCellStyle();
		//
		// // Cell Format: String
		// CellStyle cellStyleString = new
		// ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString();
		//
		// // Cell Format: DateTime
		// CellStyle cellStyleDateTime = new
		// ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();
		//
		// // End Section - Setting Cell Style for each cell
		//
		// // Create Row/Cell - Report Title
		// Row titleRptRow = sheetRptAudit.createRow(1); // Create Row
		// Cell cellTitleRow = titleRptRow.createCell(1);// Create Cell (Column)
		// cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set
		// Cell Value
		// cellTitleRow.setCellStyle(cellStyleTitleRpt);
		// sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11)); //
		// Merge Cell
		//
		// // Create Row/Cell - Report Header Datatable
		// Row titleHeaderRow = sheetRptAudit.createRow(3);
		// for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
		// Cell cell = titleHeaderRow.createCell(i + 1);
		// cell.setCellValue(colHeaderDataTableRpt[i]);
		// cell.setCellStyle(colHeaderDataTableRptStyle);
		// }
		//
		// // Section: Populate Audit Trail Details Record for Request Note.
		// // Create Row/Cell - Audit Trail Details
		// int rowIndex = sheetRptAudit.getLastRowNum() + 1;

		Sheet sheetRptAudit = null;
		int rowIndex = 0;
		String[] colHeaderDataTableRpt = null;
		Workbook wb = new XSSFWorkbook();
		BigDecimal currentGroupSecId = null;
		BigDecimal existGroupSecId = null;
		CellStyle noRecordsStyle = null;

		if (!rsltReportList.isEmpty()) {
			// for (int a = 0; a < rsltReportList.size(); a++) {

			for (AuditQTDTO rpt : rsltReportList) {// !!!!! ko kene buat cmne nk beza group sec id ni,kalu sama kene add, kalulain kene buat page baru !!!!!!!
				currentGroupSecId = rpt.getGroupSectionId();

				if (rpt.getGroupSectionId() != existGroupSecId) {
					sheetRptAudit = wb.createSheet(getCurrentReportTab(rpt.getGroupSectionId().longValue()));

					// Setting Report - Populate Table Header Cell Value
					colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType4;

					CellStyle cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();

					// Section - Setting Table Header Cell Style for each cell
					CellStyle colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb)
							.setHAlignment(HorizontalAlignment.CENTER).setVAlignment(VerticalAlignment.CENTER)
							.buildHeaderCellStyle();

					// Section - Setting Cell Style for No Records Found
					// message.
					noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();

					// Cell Format: String
					CellStyle cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE)
							.buildDataCellStyleString();

					// Cell Format: DateTime
					CellStyle cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

					// End Section - Setting Cell Style for each cell

					// Create Row/Cell - Report Title
					Row titleRptRow = sheetRptAudit.createRow(1); // Create Row
					Cell cellTitleRow = titleRptRow.createCell(1);// Create Cell
																	// (Column)
					cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set
																					// Cell
																					// Value
					cellTitleRow.setCellStyle(cellStyleTitleRpt);
					sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11)); // Merge
																						// Cell

					// Create Row/Cell - Report Header Datatable
					Row titleHeaderRow = sheetRptAudit.createRow(3);
					for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
						Cell cell = titleHeaderRow.createCell(i + 1);
						cell.setCellValue(colHeaderDataTableRpt[i]);
						cell.setCellStyle(colHeaderDataTableRptStyle);
					}

					// Section: Populate Audit Trail Details Record for Request
					// Note.
					// Create Row/Cell - Audit Trail Details
					rowIndex = sheetRptAudit.getLastRowNum() + 1;

					Row currRow = sheetRptAudit.createRow(rowIndex++);

					Cell cellActionDate = currRow.createCell(1);
					cellActionDate.setCellValue(rpt.getCreatedDate());
					cellActionDate.setCellStyle(cellStyleDateTime);

					Cell cellActionType = currRow.createCell(2);
					cellActionType.setCellValue(rpt.getActionType());
					cellActionType.setCellStyle(cellStyleString);

					Cell cellActionBy = currRow.createCell(3);
					cellActionBy.setCellValue(rpt.getActionBy());
					cellActionBy.setCellStyle(cellStyleString);

					Cell cellOrg = currRow.createCell(4);
					cellOrg.setCellValue(rpt.getOrgName());
					cellOrg.setCellStyle(cellStyleString);

					Cell cellRole = currRow.createCell(5);
					cellRole.setCellValue(rpt.getRoleName());
					cellRole.setCellStyle(cellStyleString);

					Cell cellDocType = currRow.createCell(6);
					cellDocType.setCellValue(rpt.getDocType());
					cellDocType.setCellStyle(cellStyleString);

					Cell cellDocNo = currRow.createCell(7);
					cellDocNo.setCellValue(rpt.getDocNo());
					cellDocNo.setCellStyle(cellStyleString);

					if (rpt.getDocStatus() != null) {
						Cell cellDocStatus = currRow.createCell(8);
						cellDocStatus.setCellValue(getStatusDescById(rpt.getDocStatus().longValue()));
						cellDocStatus.setCellStyle(cellStyleString);
					} else {
						Cell cellDocStatus = currRow.createCell(8);
						cellDocStatus.setCellValue("");
						cellDocStatus.setCellStyle(cellStyleString);
					}

					Cell cellFieldName = currRow.createCell(9);
					cellFieldName.setCellValue(rpt.getFieldName());
					cellFieldName.setCellStyle(cellStyleString);

					Cell cellOldValue = currRow.createCell(10);
					cellOldValue.setCellValue(rpt.getOldValue());
					cellOldValue.setCellStyle(cellStyleString);

					Cell cellNewValue = currRow.createCell(11);
					cellNewValue.setCellValue(rpt.getNewValue());
					cellNewValue.setCellStyle(cellStyleString);
				} else {
					createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
				}

			}
		}

		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
		// Export to Excel
		exportExcelReportAudit(wb);
	}

	/**
	 * #35500, get Report Excel tab Name
	 * 
	 * @param groupSectionId
	 * @return String
	 */
	private String getCurrentReportTab(Long groupSectionId) {

		if (groupSectionId.equals(QTAuditConstant.AU_SECTION_GI)) {
			return ReportExcelAuditConstant.TAB_GENERAL_INFORMATION;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_BSV)) {
			return ReportExcelAuditConstant.TAB_BRIEF_OR_SITE_VISIT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_SL)) {
			return ReportExcelAuditConstant.TAB_SUPPLIER_LIST;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_DOA)) {
			return ReportExcelAuditConstant.TAB_DESK_OFFICER_ASSISTANT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_DO)) {
			return ReportExcelAuditConstant.TAB_DESK_OFFICER;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_SC)) {
			return ReportExcelAuditConstant.TAB_SPECIFICATION_COMMITEE_APPOINTMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_EC)) {
			return ReportExcelAuditConstant.TAB_EVALUATION_COMMITTEE_APPOINMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_OC)) {
			return ReportExcelAuditConstant.TAB_OPENING_COMMITTEE_APPOINTMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_TEC)) {
			return ReportExcelAuditConstant.TAB_TECHNICAL_COMMITTEE_APPOINTMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_FEC)) {
			return ReportExcelAuditConstant.TAB_FINANCIAL_COMMITTEE_APPOINTMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_QC)) {
			return ReportExcelAuditConstant.TAB_QUOTATION_COMMITTEE_APPOINTMENT;
		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_PB)) {
			return ReportExcelAuditConstant.TAB_PROCUMENT_BOARD_APPOINTMENT;
		}
		return null;
	}

	/**
	 * Set column width
	 * 
	 * @param totalNoOfCell
	 * @param sheetName
	 */
	private void setExlSheetColumnWidth(int totalNoOfCell, Sheet sheetName) {

		// Default width for specific column ranges
		int defaultWidth = 19 * 256;

		// Define a map for specific column widths
		Map<Integer, Integer> columnWidths = new HashMap<>();
		columnWidths.put(0, 23 * 256); // Col: Tarikh Tindakan
		columnWidths.put(1, 15 * 256); // Col: Jenis Tindakan
		columnWidths.put(2, 25 * 256); // Col: Tindakan Oleh
		columnWidths.put(3, 31 * 256); // Col: Organisasi
		columnWidths.put(4, defaultWidth); // Col: Peranan
		columnWidths.put(5, defaultWidth); // Col: Jenis Dokumen
		columnWidths.put(6, 22 * 256); // Col: No. Dokumen
		columnWidths.put(7, 60 * 256); // Col: Doc Status
		columnWidths.put(8, defaultWidth); // Col: Nama Medan
		columnWidths.put(9, defaultWidth); // Col: Data Asal
		columnWidths.put(10, defaultWidth); // Col: Data Baru

		for (int i = 0; i < totalNoOfCell; i++) {
			sheetName.setColumnWidth(i + 1, columnWidths.get(i));
		}
	}

	/**
	 * To create new row with cell style if record not found.
	 * 
	 * @param sheet
	 * @param rowIndex
	 * @param totalCell
	 * @param noRecordsStyle
	 */
	private void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell, CellStyle noRecordsStyle) {
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
	}

	/**
	 * Export/Download Document in Excel Format
	 * 
	 * @param wb
	 */
	private void exportExcelReportAudit(Workbook wb) {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

			response.setContentType("application/ms-excel");
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=\"Butiran Jejak Audit.xlsx\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.flushBuffer();

			OutputStream outStream = response.getOutputStream();
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			wb.write(outByteStream);// Write into file
			byte[] outArray = outByteStream.toByteArray();

			outStream.write(outArray);
			outStream.flush();
			outStream.close();
			wb.close();// Close Workbook
		} catch (Exception e) {
			log.error(e);
		}
	}

}
