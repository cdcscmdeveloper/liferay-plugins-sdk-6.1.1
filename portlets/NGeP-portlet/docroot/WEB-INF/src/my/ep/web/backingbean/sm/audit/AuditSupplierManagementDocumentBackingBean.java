package my.ep.web.backingbean.sm.audit;

import java.io.ByteArrayOutputStream;
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

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementResDTO;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

/**
 * @author Amin Ashran
 * @since 24.07.2024 #28895_35501
 */

@ManagedBean(name = "auditSupplierManagementDocumentBackingBean")
@ViewScoped
public class AuditSupplierManagementDocumentBackingBean extends BaseBackingBean<AuditSupplierManagementViewManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditSupplierManagementDocumentBackingBean.class);

	/**
	 * Generate all tab excel format.
	 * 
	 * @param docId
	 */
	public void generateRptExcelForAuditTrailDetailsAll(BigInteger docId) {
		List<AuditSupplierManagementDetailDTO> rsltReportList = new ArrayList<>();

		try {
			// rsltReportList = getAuditTrailDetailsRNForExlRpt();
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU", docId.toString(),
					"AuditSupplierManagementDocumentBackingBean.generateRptExcelForAuditTrailDetails",
					getUserIdSessionFromLoginId(), e);
		}

		Workbook wb = new XSSFWorkbook();

		if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_CANCEL_REGISTERED_CATEGORY, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_SUPPLIER_INFO, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVED_CATEGORY_CODE, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_OVERLAPPING_CATEGORY, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVER_REMARKS, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		}

		// Export to Excel
		exportExcelReportAudit(wb);
	}

	/**
	 * Generate single tab excel format.
	 * 
	 * @param docId
	 */
	public void generateRptExcelForAuditTrailDetails(BigInteger docId) {
		List<AuditSupplierManagementDetailDTO> rsltReportList = new ArrayList<>();

		try {
			// rsltReportList = getAuditTrailDetailsRNForExlRpt();
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU", docId.toString(),
					"AuditSupplierManagementDocumentBackingBean.generateRptExcelForAuditTrailDetails",
					getUserIdSessionFromLoginId(), e);
		}

		Workbook wb = new XSSFWorkbook();

		if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			if (getManagedBean().getActiveIndex() == 0) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 1) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 2) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN,
						rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 3) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, rsltReportList);
			}
		} else if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			if (getManagedBean().getActiveIndex() == 0) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 1) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 2) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN,
						rsltReportList);
			}
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			if (getManagedBean().getActiveIndex() == 0) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_CANCEL_REGISTERED_CATEGORY, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 1) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_SUPPLIER_INFO, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 2) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, rsltReportList); 
			} else if (getManagedBean().getActiveIndex() == 3) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVED_CATEGORY_CODE, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 4) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_OVERLAPPING_CATEGORY, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 5) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVER_REMARKS, rsltReportList);
			} else if (getManagedBean().getActiveIndex() == 6) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList); 
			} else if (getManagedBean().getActiveIndex() == 7) {
				createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
			} 
		}

		// Export to Excel
		exportExcelReportAudit(wb);
	}

	private void createSheetLayout(Workbook wb, String tabName, List<AuditSupplierManagementDetailDTO> rsltReportList) {
		Sheet sheetRptAudit = wb.createSheet(tabName);

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType3;

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
				cellOrg.setCellValue(rsltReportList.get(a).getOrganization());
				cellOrg.setCellStyle(cellStyleString);

				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(rsltReportList.get(a).getRole());
				cellRole.setCellStyle(cellStyleString);

				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(rsltReportList.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleString);

				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(rsltReportList.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleString);

				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(rsltReportList.get(a).getFieldName());
				cellSubDocNo.setCellStyle(cellStyleString);

				Cell cellFieldName = currRow.createCell(9);
				cellFieldName.setCellValue(rsltReportList.get(a).getOldValue());
				cellFieldName.setCellStyle(cellStyleString);

				Cell cellOldValue = currRow.createCell(10);
				cellOldValue.setCellValue(rsltReportList.get(a).getNewValue());
				cellOldValue.setCellStyle(cellStyleString);
			}
		} else {
			createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
		}

		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
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
		columnWidths.put(7, defaultWidth); // Col: Nama Medan
		columnWidths.put(8, defaultWidth); // Col: Data Asal
		columnWidths.put(9, defaultWidth); // Col: Data Baru

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

	/**
	 * Retrieve audit trail detail transaction records.
	 * 
	 * @param docId
	 * @return
	 */
	public ISearchResultBean<AuditSupplierManagementResDTO> getAuditTrailDetailsRNForExlRpt() {
		return null;
		// // AuSmSearchBean auSmSearchBean =
		// getManagedBean().getAuSmSearchBean();
		// try {
		// IAuditSupplierManagementSO auditSmSo =
		// BusinessDelegate.getInstance().getServiceObject(
		// ModuleCodeConst.SUPPLIER_MANAGEMENT,
		// IAuditSupplierManagementSO.class);
		// return
		// auditSmSo.searchAuditSupplierManagement(auSmSearchBean.getLanguage(),
		// auSmSearchBean.getDocId(),
		// auSmSearchBean.getDocType(), auSmSearchBean.getAuGroupSection(),
		// null);
		// } catch (Exception e) {
		// SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT,
		// "retrieve",
		// auSmSearchBean.getDocId().toString(), "IAuditSupplierManagementSO",
		// logKey.getUserId(), e);
		// throw new EpException(MessageConst.ERROR_000001);
		// }
	}

}
