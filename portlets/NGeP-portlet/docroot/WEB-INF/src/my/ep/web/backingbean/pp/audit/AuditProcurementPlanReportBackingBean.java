package my.ep.web.backingbean.pp.audit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import my.ep.biz.service.pp.audit.IAuditProcurementPlanSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pp.AuditPPConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.pp.audit.AuditProcurementPlanSPDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.AuditPPSearchBean;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.pp.audit.AuditProcurementPlanManagedBean;
import my.ep.web.util.FacesScopes;
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
 * New file introduced for Audit Trail Procurement Plan (#28895 - #35502)
 * The Backing Bean for PP audit report/view
 * 
 * @author irfan
 * @since 20/03/2025
 */
@ManagedBean(name = "auditProcurementPlanReportBackingBean")
@ViewScoped
public class AuditProcurementPlanReportBackingBean extends BaseBackingBean<AuditProcurementPlanManagedBean> {

	private static final long serialVersionUID = 1L;
	private String auditPPView = "/xhtml/pp/audit/auditPPBasePage.xhtml";
	private transient IPagingDataModel<AuditProcurementPlanSPDTO> searchPlan = new SearchPPPagingDataModel();
	private transient IAuditProcurementPlanSO auditPPSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);

	/**
	 * Only this role is accessible for Audit Trail hyperlink
	 */
	public boolean isAuditTrailView() {
		return hasAnyRole(RoleCode.BPK_AUDITOR.value(), RoleCode.AUDITOR_INTERNAL.value(),
				RoleCode.AUDITOR_GENERAL.value(), RoleCode.EP_ADMIN.value());
	}

	/**
	 * Check if only this role is accessible
	 */
	private boolean hasAnyRole(String... roles) {
		for (String role : roles) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * redirect to audit page view
	 * 
	 * @param docType
	 * @param docID
	 */
	public void redirectToAuditTrailView(String docType, Long docID) throws IOException {
		AuditPPSearchBean auditPPSearchBean = new AuditPPSearchBean();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		long docIDL = docID;
		int docIDInt = (int) docIDL;
		
		auditPPSearchBean.setDocType(docType);
		auditPPSearchBean.setDocID(docIDInt);

		getManagedBean().setDocType(docType);
		getManagedBean().setDocID(docIDInt);
		getManagedBean().setLanguage(languageCode);
		getManagedBean().setAuditPPSearchBean(auditPPSearchBean);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		loadPPDetails();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, auditPPView);
	}

	/**
	 * Back button to previous page
	 */
	public void redirectToMainPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackURL());
	}

	/**
	 * Reload/search procurement plan
	 */
	public void loadPPDetails() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditPPSearchBean().setSearching(true);
		searchPlan.reloadData();
	}

	/**
	 * Data table for Procurement Plan data
	 * 
	 */
	private class SearchPPPagingDataModel extends PagingDataModel<AuditProcurementPlanSPDTO> {

		private static final long serialVersionUID = 1L;

		public SearchPPPagingDataModel() {

			super(AuditPPConst.SEARCH_PP_ID, getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditProcurementPlanSPDTO> pageChangeListener(
				ISearchBean<AuditProcurementPlanSPDTO> searchBean) {
			AuditPPSearchBean ppSearchBean = getManagedBean().getAuditPPSearchBean();
			try {
				ISearchResultBean<AuditProcurementPlanSPDTO> resDTO;
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				String docType;

				if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(ppSearchBean.getDocType())) {
					docType = ParameterConst.DOCTYPE_PLAN_PROCUREMENT;
				} else {
					docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
				}
				resDTO = auditPPSO.getAuditPPDetails(docType, ppSearchBean.getDocID(), languageCode, searchBean);

				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "pageChangeListenerCI", null,
						AuditPPConst.AUDIT_PP_RPT_BB, null, e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	/**
	 * Retrieves the search data model for Procurement Plan.
	 * 
	 * @return the data model used for searching PP.
	 */
	public IPagingDataModel<AuditProcurementPlanSPDTO> getSearchPlan() {
		return searchPlan;
	}

	/**
	 * Sets the search data model for Procurement Plan.
	 * 
	 * @param searchPlan
	 *            the data model to be set for searching PP.
	 */
	public void setSearchPlan(IPagingDataModel<AuditProcurementPlanSPDTO> searchPlan) {
		this.searchPlan = searchPlan;
	}

	/**
	 * Generate excel format
	 * 
	 */
	public void generateExcel() {
		List<AuditProcurementPlanSPDTO> resultReportList = new ArrayList<>();
		try {
			String docType;
			if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(getManagedBean().getDocType())) {
				docType = ParameterConst.DOCTYPE_PLAN_PROCUREMENT;
			} else {
				docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
			}
			resultReportList = auditPPSO.getAuditPPExcelDetail(docType, getManagedBean().getDocID());
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "generateExcel", null,
					AuditPPConst.AUDIT_PP_RPT_BB, null, e);
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheetExcelAudit;
		sheetExcelAudit = wb.createSheet("Jejak Audit"); 

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType2;

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

		// Create Row/Cell - Report Title
		Row rowTitleExcel = sheetExcelAudit.createRow(1); // Create Row
		Cell cellTitleRow = rowTitleExcel.createCell(1);// Create Cell (Column)
		cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set Cell Value
		cellTitleRow.setCellStyle(cellStyleTitleRpt);
		sheetExcelAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 10)); // Merge Cell

		// Create Row/Cell - Report Header Datatable
		Row titleHeaderRow = sheetExcelAudit.createRow(3);
		for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
			Cell cell = titleHeaderRow.createCell(i + 1);
			cell.setCellValue(colHeaderDataTableRpt[i]);
			cell.setCellStyle(colHeaderDataTableRptStyle);
		}

		// Section: Populate Excel Audit Trail Details Record
		// Create Row/Cell - Audit Trail Details
		int rowIndex = sheetExcelAudit.getLastRowNum() + 1;
		if (!resultReportList.isEmpty()) {
			for (int a = 0; a < resultReportList.size(); a++) {
				Row currRow = sheetExcelAudit.createRow(rowIndex++);

				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(resultReportList.get(a).getCreatedDate());
				cellActionDate.setCellStyle(cellStyleDateTime);

				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(resultReportList.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleString);

				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(resultReportList.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleString);

				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(resultReportList.get(a).getOrgName());
				cellOrg.setCellStyle(cellStyleString);

				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(resultReportList.get(a).getRoleName());
				cellRole.setCellStyle(cellStyleString);

				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(resultReportList.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleString);

				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(resultReportList.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleString);
				
				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(resultReportList.get(a).getSubDocNo());
				cellSubDocNo.setCellStyle(cellStyleString);
				
				Cell cellDocStatus = currRow.createCell(9);
				cellDocStatus.setCellValue(resultReportList.get(a).getDocStatus());
				cellDocStatus.setCellStyle(cellStyleString);

				Cell cellField = currRow.createCell(10);
				cellField.setCellValue(resultReportList.get(a).getFieldName());
				cellField.setCellStyle(cellStyleString);

				Cell cellOldValue = currRow.createCell(11);
				cellOldValue.setCellValue(resultReportList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleString);

				Cell cellNewValue = currRow.createCell(12);
				cellNewValue.setCellValue(resultReportList.get(a).getNewValue());
				cellNewValue.setCellStyle(cellStyleString);
			}
		} else {
			notFoundRecord(sheetExcelAudit, rowIndex, colHeaderDataTableRpt.length, noRecordsStyle);
		}

		// Set column width
		setExcelColumnWidth(colHeaderDataTableRpt.length, sheetExcelAudit);

		// Export to Excel
		exportExcelReportAudit(wb);
	}

	/**
	 * Generate PDF Report
	 * 
	 */
	public void generatePDF() throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditPPConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, getManagedBean().getDocID().toString());// P_DOCID

		String docType;
		if (ParameterConst.PLAN_TYPE_PROCUREMENT.equals(getManagedBean().getDocType())) {
			docType = ParameterConst.DOCTYPE_PLAN_PROCUREMENT;
		} else {
			docType = ParameterConst.DOCTYPE_PLAN_EXPENDITURE;
		}
		paramMap.put(AuditReportConst.PARAM_DOC_TYPE, docType);// P_GROUP_SECTION_ID

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_PP, paramMap);
	}

	/**
	 * To create record not found message in report
	 * 
	 */
	private void notFoundRecord(Sheet sheet, int rowIndex, int totalCell, CellStyle noRecordsStyle) {
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
	}

	/**
	 * To set column size for Excel
	 * 
	 */
	private void setExcelColumnWidth(int totalNoOfCell, Sheet sheetName) {

		// Default width for specific column ranges
		int defaultWidth = 19 * 256;

		// Define a map for specific column width
		Map<Integer, Integer> columnWidths = new HashMap<>();
		columnWidths.put(0, 23 * 256); // Col: Tarikh Tindakan
		columnWidths.put(1, 15 * 256); // Col: Jenis Tindakan
		columnWidths.put(2, 30 * 256); // Col: Tindakan Oleh
		columnWidths.put(3, 31 * 256); // Col: Organisasi
		columnWidths.put(4, 25 * 256); // Col: Peranan
		columnWidths.put(5, defaultWidth); // Col: Jenis Dokumen
		columnWidths.put(6, defaultWidth); // Col: No Dokumen
		columnWidths.put(7, defaultWidth); // Col: No Sub Dokumen
		columnWidths.put(8, 22 * 256); // Col: Dokumen Status
		columnWidths.put(9, 40 * 256); // Col: Medan
		columnWidths.put(10, 25 * 256); // Col: Data Asal
		columnWidths.put(11, 25 * 256); // Col: Data Baru

		// max size 10
		for (int i = 0; i < totalNoOfCell; i++) {
			sheetName.setColumnWidth(i + 1, columnWidths.get(i));
		}
	}

	/**
	 * To export the Excel
	 * 
	 */
	private void exportExcelReportAudit(Workbook wb) {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

			response.setContentType("application/ms-excel");
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=" + AuditPPConst.PP_AU_EXCEL_FILE_NAME);
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
			SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "exportExcelReportAudit", null,
					AuditPPConst.AUDIT_PP_RPT_BB, null, e);
		}
	}

}
