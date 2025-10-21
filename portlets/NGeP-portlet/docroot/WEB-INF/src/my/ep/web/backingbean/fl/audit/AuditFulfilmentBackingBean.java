package my.ep.web.backingbean.fl.audit;

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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.IAuditTrailSO;
import my.ep.biz.service.fl.audit.IAuditFulfilmentSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.AuditFLConst;
import my.ep.constant.fl.SLALogFulfilmentConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.fl.audit.AuFLReqDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.fl.AuditFLSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.fl.audit.AuditFulfilmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.rpt.excel.ExcelCellStyleBuilder;

/**
 * Backing Bean for FL audit
 * 
 * @author razin
 * @since 10/07/2024
 */
@ManagedBean(name = "auditFulfilmentBackingBean")
@ViewScoped
public class AuditFulfilmentBackingBean extends BaseBackingBean<AuditFulfilmentManagedBean> {

	private static final long serialVersionUID = 1L;
	private transient IPagingDataModel<AuFLReqDTO> searchFL = new SearchFLPagingDataModel();
	private static Logger log = Logger.getLogger(AuditFulfilmentBackingBean.class);
	private String auditFlView = "/xhtml/fl/audit/auditFLBasePage.xhtml";

	private transient IAuditFulfilmentSO auditFulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.ORDER_FULFILLMENT, IAuditFulfilmentSO.class);

	private transient IAuditTrailSO auditTrailAuSo = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.AUDIT, IAuditTrailSO.class);

	/**
	 * get config audit fl state
	 * 
	 * @return auditFlConfig config for audit fl
	 * 
	 */
	public boolean getAuditFlConfigState() {
		boolean auditFlConfig;
		auditFlConfig = auditTrailAuSo.isAuditConfig(AuditFLConst.FL_AU_CONF);

		return auditFlConfig;
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/*
	 * to check has access audit trail view
	 */
	public boolean isAuditTrailView() {
		boolean isRole;
		if (isRoleCodeAuditorGeneral() || isRoleCodeEPAdmin() || isRoleCodeBPKAuditor() || isRoleCodeAuditorInternal()) {
			isRole = true;
		} else {
			isRole = false;
		}
		return isRole;
	}

	/**
	 * check has role auditor internal
	 * 
	 * @return boolean
	 */
	private boolean isRoleCodeAuditorInternal() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
	}

	/**
	 * 
	 * check has role auditor general
	 * 
	 * @return boolean
	 */
	private boolean isRoleCodeAuditorGeneral() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}

	/**
	 * 
	 * check has role ep admin
	 * 
	 * @return boolean
	 */
	private boolean isRoleCodeEPAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value());
	}

	/**
	 * check has role bpk auditor
	 * 
	 * @return boolean
	 */
	private boolean isRoleCodeBPKAuditor() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}

	/**
	 * loadFLDetails() - reload searchFL data
	 */
	public void loadFLDetails() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditFLSearchBean().setSearching(true);
		searchFL.reloadData();
	}

	/**
	 * redirectToAudittrailView
	 * 
	 * @param docId
	 *            invoice id/delivery order id
	 * @param docType
	 *            invoice/deliver order doctype
	 */
	public void redirectToAuditTrailView(Long docId, String docType) throws IOException {
		AuditFLSearchBean auditFlSearchBean = new AuditFLSearchBean();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditFlSearchBean.setDocId(docId);
		auditFlSearchBean.setDocType(docType);
		getManagedBean().setLanguage(languageCode);
		getManagedBean().setDocId(docId);
		getManagedBean().setDocType(docType);
		getManagedBean().setAuditFLSearchBean(auditFlSearchBean);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		loadFLDetails();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, auditFlView);
	}

	/**
	 * redirectToMainPage()
	 */
	public void redirectToMainPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackURL());
	}

	/**
	 * datatable for FL audit
	 * 
	 */
	private class SearchFLPagingDataModel extends PagingDataModel<AuFLReqDTO> {

		private static final long serialVersionUID = 1L;

		public SearchFLPagingDataModel() {

			super("searchFLId", getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuFLReqDTO> pageChangeListener(ISearchBean<AuFLReqDTO> searchBean) {
			AuditFLSearchBean flSearchBean = getManagedBean().getAuditFLSearchBean();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				ISearchResultBean<AuFLReqDTO> resDTO;
				resDTO = auditFulfilmentSO.getAuditFLDetails(flSearchBean.getDocId(), flSearchBean.getDocType(),
						languageCode, searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(SLALogFulfilmentConst.FL_MODULE, SLALogFulfilmentConst.FL_AUDIT_BB,
						flSearchBean.getDocId().toString(), SLALogFulfilmentConst.FL_AUDIT_POPULATE_VIEW,
						getUserIdSessionFromLoginId(), e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	/**
	 * @return the searchFL
	 */
	public IPagingDataModel<AuFLReqDTO> getSearchFL() {
		return searchFL;
	}

	/**
	 * @param searchFL
	 *            the searchFL to set
	 */
	public void setSearchFL(IPagingDataModel<AuFLReqDTO> searchFL) {
		this.searchFL = searchFL;
	}

	// SECTION: Generate Report (PDF & EXCEL)

	/**
	 * Generate Report: PDF Format
	 * 
	 * @param docId
	 *            invoice id/delivery order id
	 * @param docType
	 *            invoice/deliver order doctype
	 * @throws IOException
	 */
	public void generateReport(Long docId, String docType) throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, docId.toString());// P_DOCID
		paramMap.put(AuditReportConst.PARAM_DOC_TYPE, docType);// P_DOCTYPE

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_FL, paramMap);
	}

	/**
	 * Generate excel format.
	 * 
	 * @param docId
	 *            invoice id/delivery order id
	 * @param docType
	 *            invoice/deliver order doctype
	 */
	public void generateRptExcelForAuditTrailDetails(Long docId, String docType) {
		List<AuFLReqDTO> rsltReportList = new ArrayList<>();
		try {
			rsltReportList = auditFulfilmentSO.getAuditFLDetailsForRpt(docId, docType);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogFulfilmentConst.FL_MODULE, SLALogFulfilmentConst.FL_AUDIT_BB,
					docId.toString(), SLALogFulfilmentConst.FL_AUDIT_GET_DETAIL_FOR_EXCEL,
					getUserIdSessionFromLoginId(), e);
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheetRptAudit = wb.createSheet("Jejak Audit");

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType4;

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

		// Section: Populate Audit Trail Details Record for Fulfilment details.
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
				cellSubDocNo.setCellValue(rsltReportList.get(a).getDocStatus());
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
		exportExcelReportAudit(wb, docId);
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
		columnWidths.put(2, 30 * 256); // Col: Tindakan Oleh
		columnWidths.put(3, 31 * 256); // Col: Organisasi
		columnWidths.put(4, 25 * 256); // Col: Peranan
		columnWidths.put(5, defaultWidth); // Col: Jenis Dokumen
		columnWidths.put(6, 22 * 256); // Col: No. Dokumen
		columnWidths.put(7, 40 * 256); // Col: Dokumen Status
		columnWidths.put(8, 25 * 256); // Col: Nama Medan
		columnWidths.put(9, 25 * 256); // Col: Data Asal
		columnWidths.put(10, 25 * 256); // Col: Data Baru

		//max size 11
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
	private void exportExcelReportAudit(Workbook wb, Long docId) {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse();
			HttpServletResponse response = PortalUtil.getHttpServletResponse(resp);

			response.setContentType("application/ms-excel");
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename="+AuditFLConst.FL_AU_EXCEL_FILE_NAME);
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
			SLAUtils.getInstance().logSLAError(SLALogFulfilmentConst.FL_MODULE, SLALogFulfilmentConst.FL_AUDIT_BB,
					docId.toString(), SLALogFulfilmentConst.FL_AUDIT_GENERATE_EXCEL, getUserIdSessionFromLoginId(), e);
		}
	}
}
