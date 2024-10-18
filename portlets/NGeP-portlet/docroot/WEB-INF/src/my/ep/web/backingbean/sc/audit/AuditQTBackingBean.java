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
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
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

	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger.getLogger(SourcingBaseBackingBean.class);

	/**
	 * @param docType
	 * @param docId
	 * @throws IOException
	 * @throws PortalException
	 * @throws SystemException
	 * 
	 */
	public void redirectToAuditTrailsViews(Long docId) throws IOException, PortalException, SystemException {

		RequestContext.getCurrentInstance().execute("blockWidget.show()");
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setQtId(docId);
		this.getManagedBean().setActiveIndex(0);
		this.getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		navigateToGeneralInfoTab();

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
	 * 
	 * @param activeIndex
	 */
	public void onTabMenuChangeAction(Integer activeIndex) {
		log.info("onTabMenuChangeAction");
		RequestContext.getCurrentInstance().execute("blockWidget.show()");
		int previousIndex = this.getManagedBean().getActiveIndex();

		if (previousIndex != activeIndex) {
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
			default:
				break;
			}
			this.getManagedBean().setActiveIndex(activeIndex);
		}
	}

	private void navigateToGeneralInfoTab() {
		log.info("navigateToGeneralInfoTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_GI);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToBriefingSiteVisitTab() {
		log.info("navigateToBriefingSiteVisitTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_BSV);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToSupplierListTab() {
		log.info("navigateToSupplierListTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SL);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToDeskOfficerAssistantTab() {
		log.info("navigateToDeskOfficerAssistantTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DOA);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToDeskOfficerTab() {
		log.info("navigateToDeskOfficerTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_DO);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToSpecCommitteeAppointTab() {
		log.info("navigateToSpecCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_SC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToEvalCommitteeAppointTab() {
		log.info("navigateToEvalCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_EC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToTechCommitteeAppointTab() {
		log.info("navigateToTechCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_TEC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();
	}

	private void navigateToOpenCommitteeAppointTab() {
		log.info("navigateToOpenCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_OC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToFinanceCommitteeAppointTab() {
		log.info("navigateToFinanceCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_FEC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToQuotCommitteeAppointTab() {
		log.info("navigateToQuotCommitteeAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_QC);
		RequestContext.getCurrentInstance().execute(QTAuditConstant.BLOCK_WIDGET_HIDE);
		searchQT.reloadData();

	}

	private void navigateToProcBoardAppointTab() {
		log.info("navigateToProcBoardAppointTab");
		this.getManagedBean().getAuditQTSearchBean().setSearching(true);
		getManagedBean().setGroupSectionId(QTAuditConstant.AU_SECTION_PB);
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
		paramMap.put(AuditReportConst.P_GROUP_SECTION_ID, getManagedBean().getGroupSectionId().toString());

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
		Row titleHeaderRow;
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

					titleRptRow = sheetRptAudit.createRow(3); // Create Row
					cellTitleRow = titleRptRow.createCell(1);
					cellTitleRow.setCellValue(getCurrentReportTab(rpt.getGroupSectionId().longValue(), titleHeader));

					cellTitleRow.setCellStyle(cellStyleTitleRpt);
					sheetRptAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 11)); // Merge
																						// Cell

					// Create Row/Cell - Report Header Datatable
					titleHeaderRow = sheetRptAudit.createRow(4);
					for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
						Cell cell = titleHeaderRow.createCell(i + 1);
						cell.setCellValue(colHeaderDataTableRpt[i]);
						cell.setCellStyle(colHeaderDataTableRptStyle);
					}

					// Section: Populate Audit Trail Details Record for Request
					// Note.Create Row/Cell - Audit Trail Details
					rowIndex = sheetRptAudit.getLastRowNum() + 1;
					currentGroupSecId = rpt.getGroupSectionId();
				}

				if (rpt.getDocNo() == null && rpt.getActionBy() == null) {
					createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
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
	 * #35500, get Report Excel tab Name
	 * 
	 * @param groupSectionId
	 * @return String
	 */
	private String getCurrentReportTab(Long groupSectionId, Integer headerType) {

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
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_SPECIFICATION_COMMITEE_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_SPECIFICATION_COMMITEE_APPOINTMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_EC)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_EVALUATION_COMMITTEE_APPOINMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_EVALUATION_COMMITTEE_APPOINMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_OC)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_OPENING_COMMITTEE_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_OPENING_COMMITTEE_APPOINTMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_TEC)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_TECHNICAL_COMMITTEE_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_TECHNICAL_COMMITTEE_APPOINTMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_FEC)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_FINANCIAL_COMMITTEE_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_FINANCIAL_COMMITTEE_APPOINTMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_QC)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_QUOTATION_COMMITTEE_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_QUOTATION_COMMITTEE_APPOINTMENT;
			}

		} else if (groupSectionId.equals(QTAuditConstant.AU_SECTION_PB)) {
			if (headerType == 1) {
				return ReportExcelAuditConstant.TAB_PROCUMENT_BOARD_APPOINTMENT;
			} else {
				return ReportExcelAuditConstant.HEADER_PROCUMENT_BOARD_APPOINTMENT;
			}
		}
		return null;
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
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
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
