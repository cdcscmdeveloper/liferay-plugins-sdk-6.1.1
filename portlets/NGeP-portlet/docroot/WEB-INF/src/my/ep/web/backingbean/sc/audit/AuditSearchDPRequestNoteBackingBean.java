package my.ep.web.backingbean.sc.audit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.sc.audit.AuDPRequestNoteSPDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.directpurchase.AuditDPSearchBean;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.dp.requestnote.audit.AuDPRequestNoteManagedBean;
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

import com.liferay.portal.util.PortalUtil;

/**
 * #28895_35492
 * 
 * @author normuliana
 * 
 *         AuditSearchDPRequestNoteBackingBean for search view
 */
@ManagedBean(name = "auditSearchDPRequestNoteBackingBean")
@ViewScoped
public class AuditSearchDPRequestNoteBackingBean extends BaseBackingBean<AuDPRequestNoteManagedBean> {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditSearchDPRequestNoteBackingBean.class);
	private transient IPagingDataModel<AuDPRequestNoteSPDTO> searchDP = new SearchDPPagingDataModel();
	private String requestNoneView = "/xhtml/sc/directpurchase/requestnote/audit/auditDPRequestNoteView.xhtml";

	/**
	 * AuditSearchDPRequestNoteBackingBean
	 */
	public AuditSearchDPRequestNoteBackingBean() {
		log.info("AuditSearchDPRequestNoteBackingBean :::");
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * @return
	 */
	public boolean getRoleCode() {
		boolean isRole;
		if (isRoleCodeAuditorGeneral() || isRoleCodeEPAdmin() || isRoleCodeBPKAuditor() || isRoleCodeAuditorInternal()) {
			isRole = true;
		} else {
			isRole = false;
		}
		return isRole;
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeAuditorInternal() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeAuditorGeneral() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeEPAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value());
	}

	/**
	 * @return
	 */
	private boolean isRoleCodeBPKAuditor() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}

	/**
	 * loadDPRequestnote() - reload searchDP data
	 */
	public void loadDPRequestNote() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditDPSearchBean().setSearching(true);
		searchDP.reloadData();
	}

	/**
	 * redirectToAudittrailView
	 * 
	 * @param docId
	 */
	public void redirectToAudittrailView(Long docId) throws IOException {
		AuditDPSearchBean auditDPSearchBean = new AuditDPSearchBean();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditDPSearchBean.setDocId(BigInteger.valueOf(docId));
		getManagedBean().setLanguage(languageCode);
		getManagedBean().setDocId(BigInteger.valueOf(docId));
		getManagedBean().setAuditDPSearchBean(auditDPSearchBean);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		loadDPRequestNote();
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
	 * search DP list
	 * 
	 */
	private class SearchDPPagingDataModel extends PagingDataModel<AuDPRequestNoteSPDTO> {

		private static final long serialVersionUID = 1L;

		public SearchDPPagingDataModel() {

			super("searchDPId", getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuDPRequestNoteSPDTO> pageChangeListener(ISearchBean<AuDPRequestNoteSPDTO> searchBean) {

			SearchResultBean<AuDPRequestNoteSPDTO> searchResult = new SearchResultBean<>();
			AuditDPSearchBean dpSearchBean = getManagedBean().getAuditDPSearchBean();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				IAuditDPRequestNoteSO auditSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);

				ISearchResultBean<AuDPRequestNoteSPDTO> resDTO = null;
				resDTO = auditSO.searchDPCancelRNResultList(languageCode, dpSearchBean.getDocId(), searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance()
						.logSLAError(SLALogConstant.COMMON_MODULE, "AU", dpSearchBean.getDocId().toString(),
								"AuditSearchDPRequestNoteBackingBean.SearchDPPagingDataModel",
								getUserIdSessionFromLoginId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	public IPagingDataModel<AuDPRequestNoteSPDTO> getSearchDP() {
		return searchDP;
	}

	public void setSearchDP(IPagingDataModel<AuDPRequestNoteSPDTO> searchDP) {
		this.searchDP = searchDP;
	}

	// SECTION: Generate Report (PDF & EXCEL)

	/**
	 * Generate Report: PDF Format
	 * 
	 * @param docId
	 *            ,referring to selected REQUEST_NOTE_ID
	 * @throws IOException
	 */
	public void generateReport(BigInteger docId) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());// P_DOCID

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_DP, paramMap);
	}

	/**
	 * Retrieve audit trail detail transaction records.
	 * 
	 * @param docId
	 *            , referring to REQUEST_NOTE_ID
	 * @return
	 */
	public List<AuDPRequestNoteSPDTO> getAuditTrailDetailsRNForExlRpt(BigInteger docId) {
		IAuditDPRequestNoteSO auditSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
		return auditSO.getAuditTrailDetailsRNForExlRpt(docId);
	}

	/**
	 * Generate excel format.
	 * 
	 * @param docId
	 *            ,referring to REQUEST_NOTE_ID
	 */
	public void generateRptExcelForAuditTrailDetails(BigInteger docId) {
		List<AuDPRequestNoteSPDTO> rsltReportList = new ArrayList<>();
		try {
			rsltReportList = getAuditTrailDetailsRNForExlRpt(docId);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogConstant.COMMON_MODULE, "AU", docId.toString(),
					"AuditSearchDPRequestNoteBackingBean.generateRptExcelForAuditTrailDetails",
					getUserIdSessionFromLoginId(), e);
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheetRptAudit = wb.createSheet("Jejak Audit");

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType1;

		CellStyle cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();

		// Section - Setting Table Header Cell Style for each cell
		CellStyle colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
				.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();

		// Section - Setting Cell Style for No Records Found message.
		CellStyle noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();

		// Cell Format: String
		CellStyle cellStyleString = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString();

		// Cell Format: DateTime
		CellStyle cellStyleDateTime = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

		// End Section - Setting Cell Style for each cell

		// Create Row/Cell - Report Title
		Row titleRptRow = sheetRptAudit.createRow(1); // Create Row
		Cell cellTitleRow = titleRptRow.createCell(1);// Create Cell (Column)
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

		// Section: Populate Audit Trail Details Record for Request Note.
		// Create Row/Cell - Audit Trail Details
		int rowIndex = sheetRptAudit.getLastRowNum() + 1;
		if (!rsltReportList.isEmpty()) {
			for (int a = 0; a < rsltReportList.size(); a++) {
				Row currRow = sheetRptAudit.createRow(rowIndex++);

				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(rsltReportList.get(a).getCreatedDate());
				cellActionDate.setCellStyle(cellStyleDateTime);

				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(rsltReportList.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleString);

				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(rsltReportList.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleString);

				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(rsltReportList.get(a).getOrgName());
				cellOrg.setCellStyle(cellStyleString);

				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(rsltReportList.get(a).getRoleName());
				cellRole.setCellStyle(cellStyleString);

				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(rsltReportList.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleString);

				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(rsltReportList.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleString);

				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(rsltReportList.get(a).getSubDocNo());
				cellSubDocNo.setCellStyle(cellStyleString);

				Cell cellFieldName = currRow.createCell(9);
				cellFieldName.setCellValue(rsltReportList.get(a).getFieldName());
				cellFieldName.setCellStyle(cellStyleString);

				Cell cellOldValue = currRow.createCell(10);
				cellOldValue.setCellValue(rsltReportList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleString);

				Cell cellNewValue = currRow.createCell(11);
				cellNewValue.setCellValue(rsltReportList.get(a).getNewValue());
				cellNewValue.setCellStyle(cellStyleString);

			}
		} else {
			createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
		}

		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
		// Export to Excel
		exportExcelReportAudit(wb);
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
		columnWidths.put(7, 60 * 256); // Col: Sub No. Dokumen
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
