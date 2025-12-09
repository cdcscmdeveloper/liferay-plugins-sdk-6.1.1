package my.ep.web.backingbean.sc.audit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import my.ep.biz.service.sc.quationtender.IQTAuditTrailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.constant.sc.QTAuditConstant;
import my.ep.dto.sc.audit.AuditQTDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.au.AuditQTSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.audit.AuditQTManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * #35500, Audit Trails Views
 * 
 */
@ManagedBean(name = "auditQTBackingBean")
@RequestScoped
public class AuditQTBackingBean extends BaseBackingBean<AuditQTManagedBean> {

	private static final long serialVersionUID = -5747106368235143463L;

	private transient IPagingDataModel<AuditQTDTO> searchQT = new SearchQTPagingDataModel();

	public static final Logger log = EpLogger.getLogger(AuditQTBackingBean.class);

	private static final Map<Long, TabHeaderPair> sectionTabHeaderMap = createSectionTabHeaderMap();

	/**
	 * Throw exception for AuditTrails
	 */
	public class AuditTrailRedirectException extends Exception {

		private static final long serialVersionUID = 1L;

		/**
		 * Throw exception for AuditTrails
		 * 
		 * @param message
		 * @param cause
		 */
		public AuditTrailRedirectException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * @param docType
	 * @param docId
	 * @throws IOException
	 * @throws PortalException
	 * @throws SystemException
	 * 
	 */
	public void redirectToAuditTrailsViews(Long docId) throws AuditTrailRedirectException {
		RequestContext.getCurrentInstance().execute("blockWidget.show()");
		int activeIndex = 0;
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setQtId(docId);
		this.getManagedBean().setActiveIndex(12);
		this.getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		onTabMenuChangeAction(activeIndex);

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "/xhtml/sc/quatationtender/audit/auditTrailDetailsMain.xhtml");
	}

	/**
	 * Method to navigate back to previous page
	 * 
	 * @return the previous page
	 * 
	 */
	public void backAction() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, this.getManagedBean().getBackURL());
	}
	
	
	/**
	 * #35500 To navigate to the selected tab
	 * @param activeIndex
	 */
	public void onTabMenuChangeAction(Integer activeIndex) {
		log.info("onTabMenuChangeAction");
		RequestContext.getCurrentInstance().execute("blockWidget.show()");
		int previousIndex = this.getManagedBean().getActiveIndex();

		if (previousIndex != activeIndex) {
			switch (activeIndex) {			
			case 0:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_GI);
				break;
			case 1:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_BSV);
				break;
			case 2:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SL);
				break;
			case 3:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DOA);
				break;
			case 4:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DO);
				break;
			case 5:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SC);
				break;
			case 6:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_EC);
				break;
			case 7:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_TEC);
				break;
			case 8:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_FEC);
				break;
			case 9:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_OC);
				break;
			case 10:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_QC);
				break;
			case 11:
				getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_PB);
				break;
			default:				
				break;
			}
			this.getManagedBean().setActiveIndex(activeIndex);
			reloadTabData();
		} else {
			RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		}
	}
	
	private void reloadTabData() {
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
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
			AuditQTSearchBean qtSearchBean = getManagedBean().getAuditQTSearchBean();
			qtSearchBean.setDocId(BigInteger.valueOf(getManagedBean().getQtId().intValue()));
			Long groupSectionId = getManagedBean().getGroupSectionId();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				IQTAuditTrailSO qtAuditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailSO.class);

				return qtAuditSO.searchQTAuditTrailsViewResultList(languageCode, qtSearchBean.getDocId(), searchBean,
						groupSectionId);
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
	 * #35500, Generate Report: Selected tab in PDF Format
	 * 
	 * @param docId
	 * @throws IOException
	 */
	public void generateReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());
		paramMap.put(AuditReportConst.PARAM_GROUP_SECTION_ID, getManagedBean().getGroupSectionId().toString());

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_QT, paramMap);
	}

	/**
	 * #35500, Generate Report: All Tab in PDF Format
	 * 
	 * @param docId
	 * @throws IOException
	 */
	public void generateAllReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_QT, paramMap);
	}

	/**
	 * #35500, Retrieve audit trail detail transaction records.
	 * 
	 * @param docId
	 * @param groupSecId
	 * @return List<AuditQTDTO>
	 */
	public void getAuditTrailDetailsForExlRpt(BigInteger docId, Long groupSecId) {

		Long newGroupSecId = null;

		IQTAuditTrailSO qtAuditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTAuditTrailSO.class);

		if (!groupSecId.equals(0L)) {
			newGroupSecId = groupSecId;
		}
		List<AuditQTDTO> auditQTList = qtAuditSO.getQTAuditTrailsRptViewResultList(docId, newGroupSecId);

		generateRptExcelForAuditTrailDetails(auditQTList);
	}

	/**
	 * #35500, Generate excel format.
	 * 
	 * @param auditQTList
	 * 
	 */
	public void generateRptExcelForAuditTrailDetails(List<AuditQTDTO> auditQTList) {

		Sheet sheetRptAudit = null;
		int rowIndex = 0;
		String[] colHeaderDataTableRpt;
		Workbook wb = new XSSFWorkbook();
		BigDecimal currentGroupSecId = null;
		CellStyle noRecordsStyle;
		CellStyle cellStyleString;
		CellStyle cellStyleDateTime;
		CellStyle colHeaderDataTableRptStyle;
		CellStyle cellStyleTitleRpt;
		Row titleRptRow;
		Cell cellTitleRow;
		int sheetHeader = 1;
		int titleHeader = 2;

		if (!auditQTList.isEmpty()) {
			// Setting Report - Populate Table Header Cell Value
			colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType4;

			// Section - Setting Cell Style for No Records Found message.
			noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();
			// Cell Format: String
			cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString();

			// Cell Format: DateTime
			cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

			// End Section - Setting Cell Style for each cell

			for (AuditQTDTO rpt : auditQTList) {
				if (rpt.getGroupSectionId() != null && !rpt.getGroupSectionId().equals(currentGroupSecId)) {
					sheetRptAudit = wb
							.createSheet(getCurrentReportTab(rpt.getGroupSectionId().longValue(), sheetHeader));
					colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType4;

					cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();

					// Section - Setting Table Header Cell Style for each cell
					colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb)
							.setHAlignment(HorizontalAlignment.CENTER).setVAlignment(VerticalAlignment.CENTER)
							.buildHeaderCellStyle();

					// Section - Setting Cell Style for No Records Found
					// message.
					noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();

					// Cell Format: String
					cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE)
							.buildDataCellStyleString();

					// Cell Format: DateTime
					cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

					// End Section - Setting Cell Style for each cell

					// Create Row/Cell - Report Title
					titleRptRow = sheetRptAudit.createRow(1); // Create Row
					cellTitleRow = titleRptRow.createCell(1);// Create
																// Cell(Column)
					cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE);
					cellTitleRow.setCellStyle(cellStyleTitleRpt);//#35500 fix issue 3
					
					titleRptRow = sheetRptAudit.createRow(3); // Create Row
					cellTitleRow = titleRptRow.createCell(1);
					cellTitleRow.setCellValue(getCurrentReportTab(rpt.getGroupSectionId().longValue(), titleHeader));
					
					cellTitleRow.setCellStyle(cellStyleTitleRpt);
					sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11)); // Merge
																						// Cell

					// Create Row/Cell - Report Header Datatable
					createTitleHeaderRow(sheetRptAudit, colHeaderDataTableRpt, colHeaderDataTableRptStyle);

					// Section: Populate Audit Trail Details Record for Request
					// Note.Create Row/Cell - Audit Trail Details
					rowIndex = sheetRptAudit.getLastRowNum() + 1;
					currentGroupSecId = rpt.getGroupSectionId();
				}

				if (rpt.getDocNo() == null && rpt.getActionBy() == null) {
					createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
					// Set column width
					setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);//#35500 fix issue 4
				} else {
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

					Cell cellDocStatus = currRow.createCell(8);
					cellDocStatus.setCellValue(rpt.getDocStatus());
					cellDocStatus.setCellStyle(cellStyleString);

					Cell cellFieldName = currRow.createCell(9);
					cellFieldName.setCellValue(rpt.getFieldName());
					cellFieldName.setCellStyle(cellStyleString);

					Cell cellOldValue = currRow.createCell(10);
					cellOldValue.setCellValue(rpt.getOldValue());
					cellOldValue.setCellStyle(cellStyleString);

					Cell cellNewValue = currRow.createCell(11);
					cellNewValue.setCellValue(rpt.getNewValue());
					cellNewValue.setCellStyle(cellStyleString);

					// Set column width
					setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
				}

			}

		}

		// Export to Excel
		exportExcelReportAudit(wb);
	}

	/**
	 * Create Title Header Row
	 * 
	 * @param sheetRptAudit
	 * @param colHeaderDataTableRpt
	 * @param colHeaderDataTableRptStyle
	 */
	public void createTitleHeaderRow(Sheet sheetRptAudit, String[] colHeaderDataTableRpt,
			CellStyle colHeaderDataTableRptStyle) {
		Row titleHeaderRow = sheetRptAudit.createRow(4);
		populateHeaderRow(titleHeaderRow, colHeaderDataTableRpt, colHeaderDataTableRptStyle);
	}

	private void populateHeaderRow(Row titleHeaderRow, String[] colHeaderDataTableRpt,
			CellStyle colHeaderDataTableRptStyle) {
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
	 * #35500, get Report Excel tab Name
	 * 
	 * @param groupSectionId
	 * @return String
	 */
	private static Map<Long, TabHeaderPair> createSectionTabHeaderMap() {
		Map<Long, TabHeaderPair> map = new HashMap<>();
		map.put(QTAuditConstant.AU_SECTION_GI, new TabHeaderPair(ReportExcelAuditConstant.TAB_GENERAL_INFORMATION,
				ReportExcelAuditConstant.TAB_GENERAL_INFORMATION));
		map.put(QTAuditConstant.AU_SECTION_BSV, new TabHeaderPair(ReportExcelAuditConstant.TAB_BRIEF_OR_SITE_VISIT,
				ReportExcelAuditConstant.TAB_BRIEF_OR_SITE_VISIT));
		map.put(QTAuditConstant.AU_SECTION_SL, new TabHeaderPair(ReportExcelAuditConstant.TAB_SUPPLIER_LIST,
				ReportExcelAuditConstant.TAB_SUPPLIER_LIST));
		map.put(QTAuditConstant.AU_SECTION_DOA, new TabHeaderPair(ReportExcelAuditConstant.TAB_DESK_OFFICER_ASSISTANT,
				ReportExcelAuditConstant.TAB_DESK_OFFICER_ASSISTANT));
		map.put(QTAuditConstant.AU_SECTION_DO, new TabHeaderPair(ReportExcelAuditConstant.TAB_DESK_OFFICER,
				ReportExcelAuditConstant.TAB_DESK_OFFICER));
		map.put(QTAuditConstant.AU_SECTION_SC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_SPECIFICATION_COMMITEE_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_SPECIFICATION_COMMITEE_APPOINTMENT));
		map.put(QTAuditConstant.AU_SECTION_EC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_EVALUATION_COMMITTEE_APPOINMENT,
				ReportExcelAuditConstant.HEADER_EVALUATION_COMMITTEE_APPOINMENT));
		map.put(QTAuditConstant.AU_SECTION_OC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_OPENING_COMMITTEE_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_OPENING_COMMITTEE_APPOINTMENT));
		map.put(QTAuditConstant.AU_SECTION_TEC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_TECHNICAL_COMMITTEE_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_TECHNICAL_COMMITTEE_APPOINTMENT));
		map.put(QTAuditConstant.AU_SECTION_FEC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_FINANCIAL_COMMITTEE_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_FINANCIAL_COMMITTEE_APPOINTMENT));
		map.put(QTAuditConstant.AU_SECTION_QC, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_QUOTATION_COMMITTEE_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_QUOTATION_COMMITTEE_APPOINTMENT));
		map.put(QTAuditConstant.AU_SECTION_PB, new TabHeaderPair(
				ReportExcelAuditConstant.TAB_PROCUMENT_BOARD_APPOINTMENT,
				ReportExcelAuditConstant.HEADER_PROCUMENT_BOARD_APPOINTMENT));
		return map;
	}

	private String getCurrentReportTab(Long groupSectionId, Integer headerType) {
		TabHeaderPair tabHeaderPair = sectionTabHeaderMap.get(groupSectionId);
		if (tabHeaderPair == null) {
			return null;
		}
		return headerType == 1 ? tabHeaderPair.tab : tabHeaderPair.header;
	}

	private static class TabHeaderPair {
		final String tab;
		final String header;

		TabHeaderPair(String tab, String header) {
			this.tab = tab;
			this.header = header;
		}
	}

	/**
	 * #35500, Set column width
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
	 * #35500, To create new row with cell style if record not found.
	 * 
	 * @param sheet
	 * @param rowIndex
	 * @param totalCell
	 * @param noRecordsStyle
	 */
	private void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell, CellStyle noRecordsStyle) {
		//#35500 fix issue 3 start
		Workbook workbook = sheet.getWorkbook();
		CellStyle borderStyle = workbook.createCellStyle();
		borderStyle.cloneStyleFrom(noRecordsStyle);
		borderStyle.setBorderTop(BorderStyle.THIN);
		borderStyle.setBorderBottom(BorderStyle.THIN);
		borderStyle.setBorderLeft(BorderStyle.THIN);
		borderStyle.setBorderRight(BorderStyle.THIN);

		Row noRecordRow = sheet.createRow(rowIndex);

		for (int i = 1; i <= totalCell; i++) {
			Cell cell = noRecordRow.createCell(i);
			cell.setCellStyle(borderStyle);
			if (i == 1) {
				cell.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
			}
		}

		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, totalCell));
		//#35500 fix issue 3 end
	}

	/**
	 * #35500, Export/Download Document in Excel Format
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
			response.setHeader("Content-Disposition", ReportExcelAuditConstant.RPT_FILE_CONTENT_DISPOSITION);
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
