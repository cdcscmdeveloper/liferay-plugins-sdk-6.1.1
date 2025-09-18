package my.ep.web.backingbean.sm.audit;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
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

import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementResDTO;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.AuSmSearchBean;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.sm.audit.AuditSupplierManagementViewManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

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
 * @author Amin Ashran
 * @since 24.07.2024 #28895_35501
 */

@ManagedBean(name = "auditSupplierManagementDocumentBackingBean")
@ViewScoped
public class AuditSupplierManagementDocumentBackingBean extends BaseBackingBean<AuditSupplierManagementViewManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Generate all tab excel format.
	 * 
	 * @param auSmSearchBean
	 */
	public void generateRptExcelForAuditTrailDetailsAll(AuSmSearchBean auSmSearchBean) {
		List<AuditSupplierManagementResDTO> rsltReportList = new ArrayList<>();
		try {
			IAuditSupplierManagementSO auditSmSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					IAuditSupplierManagementSO.class);
			BigInteger docId = auSmSearchBean.getDocId();
			String docType = auSmSearchBean.getDocType();
			BigDecimal auGroupSection = null;
			rsltReportList = auditSmSo.searchAuditSupplierManagementReport(docId, docType, auGroupSection);
			resetList();
			prepareList(rsltReportList);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU", auSmSearchBean.getDocId().toString(),
					"generateRptExcelForAuditTrailDetailsAll", getUserIdSessionFromLoginId(), e);
		}
		Workbook wb = new XSSFWorkbook();
		// #28895_35501_DA Start - AUDIT TRAIL SM DA
		if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, getManagedBean().getBankInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, getManagedBean().getUserInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, getManagedBean().getDisciplinaryActionInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, getManagedBean().getAdminInfoList());
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
		// #28895_35501_DA End
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, getManagedBean().getBankInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, getManagedBean().getUserInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, getManagedBean().getAdminInfoList());
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_CANCEL_REGISTERED_CATEGORY, getManagedBean().getCatCodeInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_SUPPLIER_INFO, getManagedBean().getSupplierInfoList());
			// #28895_35501_DA - AUDIT TRAIL SM DA
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, getManagedBean().getDisciplinaryActionInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVED_CATEGORY_CODE, getManagedBean().getApprovedCatCodeInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_OVERLAPPING_CATEGORY, getManagedBean().getOverlappingRemarkInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_APPROVER_REMARKS, getManagedBean().getApproverRemarkInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, getManagedBean().getUserInfoList());
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, getManagedBean().getAdminInfoList());
		}
		// Export to Excel
		exportExcelReportAudit(wb);
	}

	private void resetList() {
		getManagedBean().getBankInfoList().clear();
		getManagedBean().getUserInfoList().clear();
		// #28895_35501_DA - AUDIT TRAIL SM DA
		getManagedBean().getDisciplinaryActionInfoList().clear();
		getManagedBean().getAdminInfoList().clear();
		getManagedBean().getSupplierInfoList().clear();
		getManagedBean().getApproverRemarkInfoList().clear();
		getManagedBean().getOverlappingRemarkInfoList().clear();
		getManagedBean().getCatCodeInfoList().clear();
		getManagedBean().getApprovedCatCodeInfoList().clear();
	}

	private void prepareList(List<AuditSupplierManagementResDTO> rsltReportList) {
		for (AuditSupplierManagementResDTO rsltReport : rsltReportList) {
			switch (rsltReport.getAuSectionGroup().intValueExact()) {
			case 1:
				getManagedBean().getBankInfoList().add(rsltReport);
				break;
			case 2:
				getManagedBean().getUserInfoList().add(rsltReport);
				break;
			// #28895_35501_DA Start - AUDIT TRAIL SM DA
			case 3:
				getManagedBean().getDisciplinaryActionInfoList().add(rsltReport);
				break;
			// #28895_35501_DA End
			case 4:
				getManagedBean().getAdminInfoList().add(rsltReport);
				break;
			case 5:
				getManagedBean().getSupplierInfoList().add(rsltReport);
				break;
			case 6:
				getManagedBean().getApproverRemarkInfoList().add(rsltReport);
				break;
			case 7:
				getManagedBean().getOverlappingRemarkInfoList().add(rsltReport);
				break;
			case 8:
				getManagedBean().getCatCodeInfoList().add(rsltReport);
				break;
			case 9:
				getManagedBean().getApprovedCatCodeInfoList().add(rsltReport);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Generate single tab excel format.
	 * 
	 * @param auSmSearchBean
	 */
	public void generateRptExcelForAuditTrailDetails(AuSmSearchBean auSmSearchBean) {
		List<AuditSupplierManagementResDTO> rsltReportList = new ArrayList<>();
		try {
			IAuditSupplierManagementSO auditSmSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					IAuditSupplierManagementSO.class);
			BigInteger docId = auSmSearchBean.getDocId();
			String docType = auSmSearchBean.getDocType();
			BigDecimal auGroupSection = auSmSearchBean.getAuGroupSection();
			rsltReportList = auditSmSo.searchAuditSupplierManagementReport(docId, docType, auGroupSection);
			resetList();
			prepareList(rsltReportList);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU", auSmSearchBean.getDocId().toString(),
					"generateRptExcelForAuditTrailDetails", getUserIdSessionFromLoginId(), e);
		}
		Workbook wb = new XSSFWorkbook();
		if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			generateBasicSupplierSheet(wb, rsltReportList);
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			generateG2GSupplierSheet(wb, rsltReportList);
		} else if (getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			generateMOFSupplierSheet(wb, rsltReportList);
		}
		// Export to Excel
		exportExcelReportAudit(wb);
	}

	private void generateBasicSupplierSheet(Workbook wb, List<AuditSupplierManagementResDTO> rsltReportList) {
		if (getManagedBean().getActiveIndex() == 0) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 1) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 2) {
			// #28895_35501_DA Start - AUDIT TRAIL SM DA
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DISCIPLINARY_ACTION, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 3) {
		// #28895_35501_DA End
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		}
	}

	private void generateG2GSupplierSheet(Workbook wb, List<AuditSupplierManagementResDTO> rsltReportList) {
		if (getManagedBean().getActiveIndex() == 0) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_BANK_INFO, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 1) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_USER_PROFILE, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 2) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		}
	}

	private void generateMOFSupplierSheet(Workbook wb, List<AuditSupplierManagementResDTO> rsltReportList) {
		if (getManagedBean().getActiveIndex() == 0) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_CANCEL_REGISTERED_CATEGORY, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 1) {
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_SUPPLIER_INFO, rsltReportList);
		} else if (getManagedBean().getActiveIndex() == 2) {
			// #28895_35501_DA Start - AUDIT TRAIL SM DA
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
		// #28895_35501_DA End
			createSheetLayout(wb, ReportExcelAuditConstant.COL_HEADER_DEACTIVE_REPLACE_SUPPLIER_ADMIN, rsltReportList);
		}
	}

	private void createSheetLayout(Workbook wb, String tabName, List<AuditSupplierManagementResDTO> rsltReportList) {
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
				cellActionDate.setCellValue(rsltReportList.get(a).getActionDate());
				cellActionDate.setCellStyle(cellStyleDateTime);
				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(rsltReportList.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleString);
				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(rsltReportList.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleString);
				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(rsltReportList.get(a).getOrg());
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
				Cell cellFieldName = currRow.createCell(8);
				cellFieldName.setCellValue(rsltReportList.get(a).getFieldName());
				cellFieldName.setCellStyle(cellStyleString);
				Cell cellOldValue = currRow.createCell(9);
				cellOldValue.setCellValue(rsltReportList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleString);
				Cell cellNewValue = currRow.createCell(10);
				cellNewValue.setCellValue(rsltReportList.get(a).getNewValue());
				cellNewValue.setCellStyle(cellStyleString);
			}
		} else {
			createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
		}
		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
	}

	private void setExlSheetColumnWidth(int totalNoOfCell, Sheet sheetName) {
		// #35501_Reopen#58 Start
		sheetName.setColumnWidth(1, 25 * 256);
		sheetName.setColumnWidth(2, 20 * 256);
		sheetName.setColumnWidth(3, 30 * 256);
		sheetName.setColumnWidth(4, 30 * 256);
		sheetName.setColumnWidth(5, 30 * 256);
		sheetName.setColumnWidth(6, 20 * 256);
		sheetName.setColumnWidth(7, 20 * 256);
		sheetName.setColumnWidth(8, 20 * 256);
		// #35501_Reopen#65 Start
		sheetName.setColumnWidth(9, 100 * 256);
		sheetName.setColumnWidth(10, 100 * 256);
		// #35501_Reopen#65 End
		// #35501_Reopen#58 End
	}

	private void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell, CellStyle noRecordsStyle) {
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
	}

	private void exportExcelReportAudit(Workbook wb) {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
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
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU",
					this.getManagedBean().getAuSmSearchBean().getDocId().toString(), "exportExcelReportAudit",
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * Generate report for all tab in PDF format.
	 * 
	 * @param auSmSearchBean
	 */
	public void generateRptPdfForAuditTrailDetailsAll(AuSmSearchBean auSmSearchBean) {
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put(AuditReportConst.PARAM_DOC_ID, auSmSearchBean.getDocId().toString());
			paramMap.put(AuditReportConst.PARAM_DOC_TYPE, auSmSearchBean.getDocType());
			paramMap.put(AuditReportConst.PARAM_USER_ID, auSmSearchBean.getUserId().toString());
			generateReport(AuditReportConst.RPT_AUDIT_TRAIL_SM, paramMap);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU",
					this.getManagedBean().getAuSmSearchBean().getDocId().toString(), "generateRptPdfForAuditTrailDetailsAll",
					getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * Generate report for single tab in PDF format.
	 * 
	 * @param auSmSearchBean
	 */
	public void generateRptPdfForAuditTrailDetails(AuSmSearchBean auSmSearchBean) {
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put(AuditReportConst.PARAM_DOC_ID, auSmSearchBean.getDocId().toString());
			paramMap.put(AuditReportConst.PARAM_DOC_TYPE, auSmSearchBean.getDocType());
			paramMap.put(AuditReportConst.PARAM_GROUP_SECTION_ID, auSmSearchBean.getAuGroupSection().toString());
			paramMap.put(AuditReportConst.PARAM_USER_ID, auSmSearchBean.getUserId().toString());
			generateReport(AuditReportConst.RPT_AUDIT_TRAIL_SM, paramMap);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "AU",
					this.getManagedBean().getAuSmSearchBean().getDocId().toString(), "generateRptPdfForAuditTrailDetails",
					getUserIdSessionFromLoginId(), e);
		}
	}
}
