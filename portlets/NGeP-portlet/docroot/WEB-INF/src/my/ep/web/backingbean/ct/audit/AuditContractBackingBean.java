package my.ep.web.backingbean.ct.audit;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.ct.audit.IAuditContractSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.audit.AuditContractConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.ct.audit.AuditContractReqDTO;
import my.ep.dto.ct.audit.AuditContractResDTO;
import my.ep.exception.EpException;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.ct.audit.AuditContractManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
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

/**
 * 
 * 
 * @author putra
 * 
 *         AuditContractBackingBean for Audit Trail Contract Process
 */
@ManagedBean(name = "auditContractBackingBean")
@ViewScoped
public class AuditContractBackingBean extends AuditCTBaseBackingBean<AuditContractManagedBean> {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditContractBackingBean.class);
	private transient IPagingDataModel<AuditContractResDTO> searchCT = new SearchCTPagingDataModel();
	private String requestNoneView = "/xhtml/ct/audittrail/auditContractView.xhtml";
	// This property is used to track which tab is
	// currently selected.

	private static final String FILE_NAME = "AuditContractDAO";
	private static final String METHOD_NAME = "insertAudit";

	private static final Map<Long, TabHeaderPair> sectionTabHeaderMap = createSectionTabHeaderMap();

	/**
	 * AuditContractBackingBean
	 */
	public AuditContractBackingBean() {
		log.info("AuditContractBackingBean :::");
	}

	/**
	 * Retrieves the proper portal name from the property file.
	 * 
	 * @return The name of the portal.
	 */
	public String getPortletTitle() {
		return ResourceBundleUtils.getMessage("lbl.audit.link.auditTrail", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}

	/**
	 * loadCTRequestnote() - reload searchCT data
	 */
	public void loadCTAuditTrail() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().setSearching(true);
		searchCT.reloadData();
	}

	/**
	 * redirectToAudittrailView
	 * 
	 * @param ctType
	 * @param docId
	 * @param docType
	 */
	public void redirectToAudittrailView(String ctType, Long docId, String docType) throws IOException {

		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		if (docId != null) {
			getManagedBean().setDocId(docId);
		} else {
			// Handle the case where docId is null, if necessary
			getManagedBean().setDocId(Long.valueOf(0)); // or some other
														// default value
		}

		getManagedBean().setDocType(docType);
		getManagedBean().setLanguage(languageCode);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		if (ctType.equalsIgnoreCase(AuditContractConst.CT_TYPE_MC) || ctType.equalsIgnoreCase(AuditContractConst.CT_TYPE_CC)) {
			getManagedBean().setCtCircularType(AuditContractConst.CT_NULL);
			getManagedBean().setCtType(AuditContractConst.CT_CONTRACT);
		} else {
			getManagedBean().setCtCircularType(ctType);
			getManagedBean().setCtType(AuditContractConst.CT_CIRCULAR);
		}
		loadCTAuditTrail();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, requestNoneView);
	}

	/**
	 * redirectToMainPage()
	 */
	public void redirectToMainPage() {
		log.info("redirectToMainPage");
		log.info("............this.getManagedBean().getBackURL()" + this.getManagedBean().getBackURL());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getBackURL());
	}

	/**
	 * search CT Audit trail list
	 * 
	 */
	private class SearchCTPagingDataModel extends PagingDataModel<AuditContractResDTO> {

		private static final long serialVersionUID = 1L;

		public SearchCTPagingDataModel() {
			super("searchCTId", getManagedBean().isLazy());
		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditContractResDTO> pageChangeListener(ISearchBean<AuditContractResDTO> searchBean) {
			ISearchResultBean<AuditContractResDTO> resDTO = null;
			AuditContractReqDTO auCtReqDTO = new AuditContractReqDTO();
			try {

				IAuditContractSO auditContractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
						IAuditContractSO.class);

				// Prepare request DTO

				auCtReqDTO.setDocType(getManagedBean().getDocType());
				auCtReqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
				auCtReqDTO.setDocId(getManagedBean().getDocId());
				auCtReqDTO.setCtType(getManagedBean().getCtType());
				auCtReqDTO.setAuCtTab(getManagedBean().getAuTabView());

				resDTO = auditContractSO.searchAuditCTResultList(auCtReqDTO);

				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "AU", auCtReqDTO.getDocId().toString(),
						"AuditContractBackingBean.SearchCTPagingDataModel", getUserId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuditContractResDTO> getSearchCT() {
		return searchCT;
	}

	public void setSearchCT(IPagingDataModel<AuditContractResDTO> searchCT) {
		this.searchCT = searchCT;
	}

	// SECTION: Generate Report (PDF & EXCEL)
	/**
	 * Generate Report: PDF Format
	 * 
	 * @param docId
	 *            ,referring to selected P_DOCID = CONTRACT_ID
	 * @param sectionId
	 * 
	 */
	public void generateReport(Long docId, BigInteger sectionId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());// P_DOCID
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_GROUP_SECTION_ID, sectionId.toString());// P_GROUP_SECTION_ID

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_CT, paramMap);
	}

	/**
	 * Generate Report: All Tab in PDF Format
	 * 
	 * @param docId
	 * @throws IOException
	 */
	public void generateAllReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_CT, paramMap);
	}

	/**
	 * Retrieve audit trail detail transaction records.
	 * 
	 * @param docId
	 * @param groupSecId
	 * @param ctType
	 * @return List<AuditQTDTO>
	 */
	public void getAuditTrailDetailsForExlRpt(Long docId, Long groupSecId, String ctType) {

		Long newGroupSecId = null;

		IAuditContractSO auditContractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
				IAuditContractSO.class);

		if (!groupSecId.equals(0L)) {
			newGroupSecId = groupSecId;
		}
		List<AuditContractResDTO> auditCTList = auditContractSO.getCTAuditTrailsRptViewResultList(docId, newGroupSecId, ctType);

		generateRptExcelForAuditTrailDetails(auditCTList);
	}

	/**
	 * Generate excel format.
	 * 
	 * @param auditCTList
	 * 
	 */
	public void generateRptExcelForAuditTrailDetails(List<AuditContractResDTO> auditCTList) {

		Workbook wb = new XSSFWorkbook();
		Sheet sheetRptAudit = null;
		int rowIndex = 0;
		String[] colHeaderDataTableRpt;
		List<BigDecimal> currentGroupSecId = new ArrayList<>();

		CellStyle noRecordsStyle;
		CellStyle cellStyleString;
		CellStyle cellStyleDateTime;
		CellStyle colHeaderDataTableRptStyle;
		CellStyle cellStyleTitleRpt;
		Row titleRptRow;
		Cell cellTitleRow;

		if (!auditCTList.isEmpty()) {
			colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType4;

			noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();
			cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString();
			cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

			for (AuditContractResDTO rpt : auditCTList) {
				if (rpt.getGroupSectionId() != null && !currentGroupSecId.contains(rpt.getGroupSectionId())) {
					String sheetName = getUniqueSheetName(wb, getValidSheetName(getCurrentReportTab(rpt.getGroupSectionId().longValue())));

					sheetRptAudit = wb.createSheet(sheetName);

					cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();
					colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
							.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();

					titleRptRow = sheetRptAudit.createRow(1);
					cellTitleRow = titleRptRow.createCell(1);
					cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE);

					titleRptRow = sheetRptAudit.createRow(3);
					cellTitleRow = titleRptRow.createCell(1);
					cellTitleRow.setCellValue(getCurrentReportTab(rpt.getGroupSectionId().longValue()));
					cellTitleRow.setCellStyle(cellStyleTitleRpt);

					sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11));
					createTitleHeaderRow(sheetRptAudit, colHeaderDataTableRpt, colHeaderDataTableRptStyle);

					currentGroupSecId.add(rpt.getGroupSectionId());
					rowIndex = sheetRptAudit.getLastRowNum() + 1;
				}

				if (rpt.getDocNo() == null && rpt.getActionBy() == null) {
					createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
				} else {
					Row currRow = sheetRptAudit.createRow(rowIndex++);
					createCell(currRow, 1, rpt.getCreatedDate(), cellStyleDateTime);
					createCell(currRow, 2, rpt.getActionType(), cellStyleString);
					createCell(currRow, 3, rpt.getActionBy(), cellStyleString);
					createCell(currRow, 4, rpt.getOrgName(), cellStyleString);
					createCell(currRow, 5, rpt.getRoleName(), cellStyleString);
					createCell(currRow, 6, rpt.getDocType(), cellStyleString);
					createCell(currRow, 7, rpt.getDocNo(), cellStyleString);
					createCell(currRow, 8, rpt.getSubDocNo(), cellStyleString);
					createCell(currRow, 9, rpt.getFieldName(), cellStyleString);
					createCell(currRow, 10, rpt.getOldValue(), cellStyleString);
					createCell(currRow, 11, rpt.getNewValue(), cellStyleString);

					setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
				}
			}
		}

		exportExcelReportAudit(wb);
	}

	/**
	 * Generates a unique sheet name by checking for existing sheets in the
	 * workbook.
	 * 
	 * @param wb
	 *            The Excel workbook.
	 * @param baseSheetName
	 *            The base name for the sheet.
	 * @return A unique sheet name that does not already exist in the workbook.
	 */
	private String getUniqueSheetName(Workbook wb, String baseSheetName) {
		int count = 1;
		String sheetName = baseSheetName;

		while (wb.getSheet(sheetName) != null) {
			sheetName = generateSheetName(baseSheetName, count);
			count++;
		}
		return sheetName;
	}

	/**
	 * Generates a sheet name by appending a numerical suffix if needed.
	 * 
	 * @param baseSheetName
	 *            The base name of the sheet.
	 * @param count
	 *            The counter value for the suffix.
	 * @return The generated sheet name with a numerical suffix if necessary.
	 */
	private String generateSheetName(String baseSheetName, int count) {
		String suffix = " (" + count + ")";
		int maxBaseLength = 31 - suffix.length();
		return baseSheetName.length() > maxBaseLength ? baseSheetName.substring(0, maxBaseLength) + suffix : baseSheetName + suffix;
	}

	/**
	 * Creates a cell in the given row with the specified value (String, Date,
	 * Number) and applies a style.
	 * 
	 * @param row
	 *            The row where the cell should be created.
	 * @param columnIndex
	 *            The column index of the cell.
	 * @param value
	 *            The value to be set in the cell (String, Date, Number, etc.).
	 * @param style
	 *            The cell style to be applied.
	 */
	private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
		Cell cell = row.createCell(columnIndex);
		if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Number) {
			cell.setCellValue(((Number) value).doubleValue()); // Works for
																// Integer,
																// Double,
																// BigDecimal
		} else {
			cell.setCellValue(""); // Default empty if value is null or
									// unsupported type
		}
		cell.setCellStyle(style);
	}

	/**
	 * Retrieves the report tab name associated with the given group section ID.
	 * If the group section ID is not found in the map, returns {@code null}.
	 * Special characters that are not allowed in sheet names (\ / * ? : [ ])
	 * are replaced with an underscore (_).
	 * 
	 * @param groupSectionId
	 *            The ID of the group section.
	 * @return A sanitized report tab name, or {@code null} if the ID is not
	 *         found.
	 */
	private String getCurrentReportTab(Long groupSectionId) {
		TabHeaderPair tabHeaderPair = sectionTabHeaderMap.get(groupSectionId);
		if (tabHeaderPair == null) {
			return null;
		}
		return tabHeaderPair.tab.replaceAll("[\\\\/*?:\\[\\]]", "_");
	}

	/**
	 * Ensures the provided sheet name is valid by replacing invalid characters
	 * and truncating if necessary.
	 * 
	 * <p>
	 * Excel sheet names have the following constraints:
	 * <ul>
	 * <li>Cannot contain any of the following characters: \ / * ? : [ ]</li>
	 * <li>Cannot exceed 31 characters in length</li>
	 * <li>If the input is null, defaults to "Sheet1"</li>
	 * </ul>
	 * 
	 * @param sheetName
	 *            the original sheet name (can be null)
	 * @return a valid sheet name that adheres to Excel's naming constraints
	 */
	private String getValidSheetName(String sheetName) {
		if (sheetName == null) {
			return "Sheet1";
		}
		String sanitizedSheetName = sheetName.replaceAll("[\\\\/*?:\\[\\]]", "_");
		return sanitizedSheetName.length() > 31 ? sanitizedSheetName.substring(0, 31) : sanitizedSheetName;
	}

	/**
	 * get Report Excel tab Name
	 * 
	 * @param groupSectionId
	 * @return String
	 */
	private static Map<Long, TabHeaderPair> createSectionTabHeaderMap() {
		Map<Long, TabHeaderPair> map = new HashMap<>();
		map.put(AuditContractConst.AU_SECTION_FD, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_FD));
		map.put(AuditContractConst.AU_SECTION_ZO, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_ZO));
		map.put(AuditContractConst.AU_SECTION_AG, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_AG));
		map.put(AuditContractConst.AU_SECTION_PD, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_PD));
		map.put(AuditContractConst.AU_SECTION_SD, new TabHeaderPair(ReportExcelAuditConstant.TAB_DESK_OFFICER));
		map.put(AuditContractConst.AU_SECTION_DE, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_SD));
		map.put(AuditContractConst.AU_SECTION_SC, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_SC));
		map.put(AuditContractConst.AU_SECTION_PB, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_PB));
		map.put(AuditContractConst.AU_SECTION_PBD, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_PBD));
		map.put(AuditContractConst.AU_SECTION_ITS, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_ITS));
		map.put(AuditContractConst.AU_SECTION_AGS, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_AGS));
		map.put(AuditContractConst.AU_SECTION_CMN, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_CMN));
		map.put(AuditContractConst.AU_SECTION_FNS, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_FNS));
		map.put(AuditContractConst.AU_SECTION_CO, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_CO));
		map.put(AuditContractConst.AU_SECTION_FT, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_FT));

		map.put(AuditContractConst.AU_SECTION_CC, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_CC));
		map.put(AuditContractConst.AU_SECTION_PC, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_PC));
		map.put(AuditContractConst.AU_SECTION_PID, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_PID));
		map.put(AuditContractConst.AU_SECTION_CPD, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_CPD));
		map.put(AuditContractConst.AU_SECTION_EMD, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_EMD));
		map.put(AuditContractConst.AU_SECTION_RCT, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_RCT));
		map.put(AuditContractConst.AU_SECTION_CL, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_CL));
		map.put(AuditContractConst.AU_SECTION_ID, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_ID));
		map.put(AuditContractConst.AU_SECTION_IS, new TabHeaderPair(ReportExcelAuditConstant.TAB_SECTION_IS));
		return map;
	}

	private static class TabHeaderPair {
		final String tab;

		TabHeaderPair(String tab) {
			this.tab = tab;
		}
	}

	/**
	 * Create Title Header Row
	 * 
	 * @param sheetRptAudit
	 * @param colHeaderDataTableRpt
	 * @param colHeaderDataTableRptStyle
	 */
	public void createTitleHeaderRow(Sheet sheetRptAudit, String[] colHeaderDataTableRpt, CellStyle colHeaderDataTableRptStyle) {
		Row titleHeaderRow = sheetRptAudit.createRow(4);
		populateHeaderRow(titleHeaderRow, colHeaderDataTableRpt, colHeaderDataTableRptStyle);
	}

	private void populateHeaderRow(Row titleHeaderRow, String[] colHeaderDataTableRpt, CellStyle colHeaderDataTableRptStyle) {
		for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
			createAndStyleCell(titleHeaderRow, i + 1, colHeaderDataTableRpt[i], colHeaderDataTableRptStyle);
		}
	}

	private void createAndStyleCell(Row row, int columnIndex, String cellValue, CellStyle cellStyle) {
		Cell cell = row.createCell(columnIndex);
		cell.setCellValue(cellValue);
		cell.setCellStyle(cellStyle);
	}

	/**
	 * Handles button click events in the audit tab interface. Determines which
	 * tab was clicked based on the provided index and updates the corresponding
	 * data accordingly.
	 * 
	 * @param activeIndex
	 *            the identifier of the clicked tab
	 */
	public void handleButtonClick(Integer activeIndex) {
		log.info("handleButtonClick");
		int prevIndex = this.getManagedBean().getActiveTab();

		// Check if the clicked tab is already active; if so, do nothing.
		if (prevIndex == activeIndex) {
			return;
		}

		// Update the tab view based on the selected tab index
		updateTabView(activeIndex);

		// Store the newly selected tab as the active tab
		this.getManagedBean().setActiveTab(activeIndex);

		// Load additional data for the selected tab
		loadCTAuditTrail();
	}

	/**
	 * Updates the audit tab view based on the given active index. If the index
	 * does not match a predefined section, no action is taken.
	 * 
	 * @param activeIndex
	 *            the identifier of the clicked tab
	 */
	private void updateTabView(Integer activeIndex) {
		Long tabView = getTabViews().get(activeIndex);

		// Only update the tab view if a valid mapping exists
		if (tabView != null) {
			getManagedBean().setAuTabView(tabView);
		}
	}

	/**
	 * A mapping of tab indexes to their corresponding audit section constants.
	 * This replaces a lengthy switch statement with a more maintainable
	 * structure. Each key represents a tab index, and the value represents the
	 * associated audit section.
	 */
	public Map<Integer, Long> getTabViews() {
		Map<Integer, Long> tabViews = new HashMap<>();

		if (getManagedBean().getCtType().equals(AuditContractConst.CT_CONTRACT)) {
			tabViews.put(0, AuditContractConst.AU_SECTION_FD);
			tabViews.put(1, AuditContractConst.AU_SECTION_ZO);
			tabViews.put(2, AuditContractConst.AU_SECTION_AG);
			tabViews.put(3, AuditContractConst.AU_SECTION_PD);
			tabViews.put(4, AuditContractConst.AU_SECTION_SD);
			tabViews.put(5, AuditContractConst.AU_SECTION_DE);
			tabViews.put(6, AuditContractConst.AU_SECTION_SC);
			tabViews.put(7, AuditContractConst.AU_SECTION_PB);
			tabViews.put(8, AuditContractConst.AU_SECTION_PBD);
			tabViews.put(9, AuditContractConst.AU_SECTION_ITS);
			tabViews.put(10, AuditContractConst.AU_SECTION_AGS);
			tabViews.put(11, AuditContractConst.AU_SECTION_CMN);
			tabViews.put(12, AuditContractConst.AU_SECTION_FNS);
			tabViews.put(13, AuditContractConst.AU_SECTION_CO);
			tabViews.put(14, AuditContractConst.AU_SECTION_FT);
		}

		if (getManagedBean().getCtCircularType().equals(AuditContractConst.CT_TYPE_CCR)) {
			tabViews.put(0, AuditContractConst.AU_SECTION_CC);
			tabViews.put(1, AuditContractConst.AU_SECTION_PID);
			tabViews.put(2, AuditContractConst.AU_SECTION_CPD);
			tabViews.put(3, AuditContractConst.AU_SECTION_EMD);
			tabViews.put(4, AuditContractConst.AU_SECTION_RCT);
			tabViews.put(5, AuditContractConst.AU_SECTION_CL);
		}

		if (getManagedBean().getCtCircularType().equals(AuditContractConst.CT_TYPE_PCR)) {
			tabViews.put(0, AuditContractConst.AU_SECTION_PC);
			tabViews.put(1, AuditContractConst.AU_SECTION_PID);
			tabViews.put(2, AuditContractConst.AU_SECTION_CPD);
			tabViews.put(3, AuditContractConst.AU_SECTION_RCT);
			tabViews.put(4, AuditContractConst.AU_SECTION_ID);
			tabViews.put(5, AuditContractConst.AU_SECTION_IS);
		}

		return tabViews;
	}

	/**
	 * Saves audit data for a contract, capturing details about document
	 * changes.
	 * 
	 * @param auCtData
	 *            The audit contract request data containing document ID,
	 *            contract version ID, previous version ID, document type,
	 *            creator information, and role.
	 */
	public final void saveAuditData(AuditContractReqDTO auCtData) {
		try {
			IAuditContractSO auditContractSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
					IAuditContractSO.class);
			auCtData.setCreatedBy(getUserId());
			auditContractSO.insertAuditContract(auCtData);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, FILE_NAME, auCtData.getDocId().toString(), METHOD_NAME,
					auCtData.getCreatedBy(), ex);
		}
	}
}
