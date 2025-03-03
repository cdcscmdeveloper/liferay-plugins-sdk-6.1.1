package my.ep.web.backingbean.cm.audit;

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
import my.ep.biz.service.cm.audit.IAuditCodificationManagementSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.AuditCMConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.cm.audit.AuditCodificationManagementSPDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.cm.AuditCMSearchBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.managedbean.cm.audit.AuditCodificationManagementManagedBean;
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
 * New file introduced for Audit Trail Codification Management (#28895 - #35503)
 * Backing Bean for CM audit report/view
 * 
 * @author irfan
 * @since 01/11/2024
 */
@ManagedBean(name = "auditCodificationManagementReportBackingBean")
@ViewScoped
public class AuditCodificationManagementReportBackingBean extends
		BaseBackingBean<AuditCodificationManagementManagedBean> {

	private static final long serialVersionUID = 1L;
	private String auditCDView = "/xhtml/cm/audit/auditCMBasePage.xhtml";
	private transient IPagingDataModel<AuditCodificationManagementSPDTO> searchCI = new SearchCIPagingDataModel();
	private transient IPagingDataModel<AuditCodificationManagementSPDTO> searchCE = new SearchCEPagingDataModel();
	private transient IAuditCodificationManagementSO auditCMSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.CATALOGUE_MANAGEMENT, IAuditCodificationManagementSO.class);

	/**
	 * Only this role is accessible for Audit Trail hyperlink
	 */
	public boolean isAuditTrailView() {
		return hasAnyRole(RoleCode.CD_OFFICER.value(), RoleCode.BPK_AUDITOR.value(), RoleCode.AUDITOR_INTERNAL.value(),
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
	 * @param segmentCode
	 * @param familyCode
	 * @param classCode
	 * @param commodityCode
	 */
	public void redirectToAuditTrailView(String segmentCode, String familyCode, String classCode, String commodityCode)
			throws IOException {
		AuditCMSearchBean auditCMSearchBean = new AuditCMSearchBean();
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		auditCMSearchBean.setSegmentCode(segmentCode);
		auditCMSearchBean.setFamilyCode(familyCode);
		auditCMSearchBean.setClassCode(classCode);
		auditCMSearchBean.setCommodityCode(commodityCode);
		getManagedBean().setLanguage(languageCode);
		getManagedBean().setSegmentCode(segmentCode);
		getManagedBean().setFamilyCode(familyCode);
		getManagedBean().setClassCode(classCode);
		getManagedBean().setCommodityCode(commodityCode);
		getManagedBean().setAuditCMSearchBean(auditCMSearchBean);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackURL(fc.getViewRoot().getViewId());
		loadCIDetails();
		loadCEDetails();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, auditCDView);
	}

	/**
	 * Back button to previous page
	 */
	public void redirectToMainPage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getBackURL());
	}

	/**
	 * Reload/search item code data
	 */
	public void loadCIDetails() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditCMSearchBean().setSearching(true);
		searchCI.reloadData();
	}

	/**
	 * Reload/search extension code data
	 */
	public void loadCEDetails() {
		this.getManagedBean().setLazy(true);
		this.getManagedBean().getAuditCMSearchBean().setSearching(true);
		searchCE.reloadData();
	}

	/**
	 * Data table for Code Item data
	 * 
	 */
	private class SearchCIPagingDataModel extends PagingDataModel<AuditCodificationManagementSPDTO> {

		private static final long serialVersionUID = 1L;

		public SearchCIPagingDataModel() {

			super(AuditCMConst.SEARCH_CI_ID, getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditCodificationManagementSPDTO> pageChangeListener(
				ISearchBean<AuditCodificationManagementSPDTO> searchBean) {
			AuditCMSearchBean cmSearchBean = getManagedBean().getAuditCMSearchBean();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				ISearchResultBean<AuditCodificationManagementSPDTO> resDTO;
				resDTO = auditCMSO.getAuditCMDetails(cmSearchBean.getCommodityCode(),
						AuditCMConst.GROUP_SECTION_ITEM_ID, languageCode, searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "pageChangeListenerCI",
						null, AuditCMConst.AUDIT_CM_RPT_BB, null, e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	/**
	 * Data table for Code Extension data
	 * 
	 */
	private class SearchCEPagingDataModel extends PagingDataModel<AuditCodificationManagementSPDTO> {

		private static final long serialVersionUID = 1L;

		public SearchCEPagingDataModel() {

			super(AuditCMConst.SEARCH_CE_ID, getManagedBean().isLazy());

		}

		/**
		 * pageChangeListener
		 * 
		 * @param searchBean
		 * @return SearchResultBean
		 */
		@Override
		public ISearchResultBean<AuditCodificationManagementSPDTO> pageChangeListener(
				ISearchBean<AuditCodificationManagementSPDTO> searchBean) {
			AuditCMSearchBean cmSearchBean = getManagedBean().getAuditCMSearchBean();
			try {
				String languageCode = new FacesScopes().getUserLocale().getLanguage();

				ISearchResultBean<AuditCodificationManagementSPDTO> resDTO;
				resDTO = auditCMSO.getAuditCMDetails(cmSearchBean.getCommodityCode(),
						AuditCMConst.GROUP_SECTION_EXTENSION_ID, languageCode, searchBean);
				return resDTO;
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "pageChangeListenerCE",
						null, AuditCMConst.AUDIT_CM_RPT_BB, null, e);
				throw new EpException(MessageConst.ERROR_000001);
			}
		}
	}

	/**
	 * Retrieves the search data model for Code Item.
	 *
	 * @return the data model used for searching CI.
	 */
	public IPagingDataModel<AuditCodificationManagementSPDTO> getSearchCI() {
		return searchCI;
	}

	/**
	 * Sets the search data model for Code Item.
	 *
	 * @param searchCI the data model to be set for searching CI.
	 */
	public void setSearchCI(IPagingDataModel<AuditCodificationManagementSPDTO> searchCI) {
		this.searchCI = searchCI;
	}

	/**
	 * Retrieves the search data model for Code Extension
	 *
	 * @return the data model used for searching CE.
	 */
	public IPagingDataModel<AuditCodificationManagementSPDTO> getSearchCE() {
		return searchCE;
	}

	/**
	 * Sets the search data model for Code Extension
	 *
	 * @param searchCE the data model to be set for searching CE.
	 */
	public void setSearchCE(IPagingDataModel<AuditCodificationManagementSPDTO> searchCE) {
		this.searchCE = searchCE;
	}

	/**
	 * Generate all excel format.
	 * 
	 */
	public void generateExcelAll() {
		List<AuditCodificationManagementSPDTO> resultReportCIList = new ArrayList<>();
		try {
			resultReportCIList = auditCMSO.getAuditCMExcelDetail(getManagedBean().getCommodityCode(),
					AuditCMConst.GROUP_SECTION_ITEM_ID);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "generateExcelAll", null,
					AuditCMConst.AUDIT_CM_RPT_BB, null, e);
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheetCIAudit = wb.createSheet(AuditCMConst.RPT_TITLE_CODE_TYPE_CI);

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableCI = ReportExcelAuditConstant.ColHeaderListType3;

		CellStyle cellStyleTitleCI = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();

		// Section - Setting Table Header Cell Style for each cell
		CellStyle colHeaderDataTableRptStyleCI = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
				.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();

		// Section - Setting Cell Style for No Records Found message.
		CellStyle noRecordsStyleCI = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();

		// Cell Format: String
		CellStyle cellStyleStringCI = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE).buildDataCellStyleString();

		// Cell Format: DateTime
		CellStyle cellStyleDateTimeCI = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

		// Create Row/Cell - Report Title
		Row rowTitleExcelCI = sheetCIAudit.createRow(1); // Create Row
		Cell cellTitleExcelRowCI = rowTitleExcelCI.createCell(1);// Create Cell (Column)
		cellTitleExcelRowCI.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set Cell Value
		cellTitleExcelRowCI.setCellStyle(cellStyleTitleCI);
		sheetCIAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 10)); // Merge Cell

		Row rowTitleCI = sheetCIAudit.createRow(3); // Create Row
		Cell cellTitleRowCI = rowTitleCI.createCell(1);// Create Cell (Column)
		cellTitleRowCI.setCellValue(AuditCMConst.RPT_TITLE_CODE_TYPE_CI); // Set Cell Value
		cellTitleRowCI.setCellStyle(cellStyleTitleCI);
		sheetCIAudit.addMergedRegion(new CellRangeAddress(3, 3, 1, 10)); // Merge Cell

		// Create Row/Cell - Report Header Datatable
		Row rowHeaderTitleCI = sheetCIAudit.createRow(5);
		for (int i = 0; i < colHeaderDataTableCI.length; i++) {
			Cell cell = rowHeaderTitleCI.createCell(i + 1);
			cell.setCellValue(colHeaderDataTableCI[i]);
			cell.setCellStyle(colHeaderDataTableRptStyleCI);
		}

		// Section: Populate CI Audit Trail Details Record.
		// Create Row/Cell - Audit Trail Details
		int rowIndex = sheetCIAudit.getLastRowNum() + 1;
		if (!resultReportCIList.isEmpty()) {
			for (int a = 0; a < resultReportCIList.size(); a++) {
				Row currRow = sheetCIAudit.createRow(rowIndex++);

				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(resultReportCIList.get(a).getCreatedDate());
				cellActionDate.setCellStyle(cellStyleDateTimeCI);

				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(resultReportCIList.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleStringCI);

				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(resultReportCIList.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleStringCI);

				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(resultReportCIList.get(a).getOrgName());
				cellOrg.setCellStyle(cellStyleStringCI);

				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(resultReportCIList.get(a).getRoleName());
				cellRole.setCellStyle(cellStyleStringCI);

				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(resultReportCIList.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleStringCI);

				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(resultReportCIList.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleStringCI);

				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(resultReportCIList.get(a).getExtAttribute());
				cellSubDocNo.setCellStyle(cellStyleStringCI);

				Cell cellOldValue = currRow.createCell(9);
				cellOldValue.setCellValue(resultReportCIList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleStringCI);

				Cell cellNewValue = currRow.createCell(10);
				cellNewValue.setCellValue(resultReportCIList.get(a).getNewValue());
				cellNewValue.setCellStyle(cellStyleStringCI);
			}
		} else {
			notFoundRecord(sheetCIAudit, rowIndex, colHeaderDataTableCI.length, noRecordsStyleCI);
		}

		// Set column width
		setExcelColumnWidth(colHeaderDataTableCI.length, sheetCIAudit);

		// ::::::::::SECOND SHEET::::::::::
		List<AuditCodificationManagementSPDTO> resultReportCEList = new ArrayList<>();
		try {
			resultReportCEList = auditCMSO.getAuditCMExcelDetail(getManagedBean().getCommodityCode(),
					AuditCMConst.GROUP_SECTION_EXTENSION_ID);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "generateExcelAll", null,
					AuditCMConst.AUDIT_CM_RPT_BB, null, e);
		}

		Sheet sheetCEAudit = wb.createSheet(AuditCMConst.RPT_TITLE_CODE_TYPE_CE);

		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableCE = ReportExcelAuditConstant.ColHeaderListType3;

		CellStyle cellStyleTitleCE = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();

		// Section - Setting Table Header Cell Style for each cell
		CellStyle colHeaderDataTableRptStyleCE = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
				.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();

		// Section - Setting Cell Style for No Records Found message.
		CellStyle noRecordsStyleCE = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();

		// Cell Format: String
		CellStyle cellStyleStringCE = new ExcelCellStyleBuilder(wb).setIsTxtWrap(Boolean.TRUE)
				.buildDataCellStyleString();

		// Cell Format: DateTime
		CellStyle cellStyleDateTimeCE = new ExcelCellStyleBuilder(wb).buildDataCellStyleDateTime();

		// Create Row/Cell - Report Title
		Row rowTitleExcelCE = sheetCEAudit.createRow(1); // Create Row
		Cell cellTitleExcelRowCE = rowTitleExcelCE.createCell(1);// Create Cell (Column)
		cellTitleExcelRowCE.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set Cell Value
		cellTitleExcelRowCE.setCellStyle(cellStyleTitleCE);
		sheetCEAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 10)); // Merge Cell

		Row rowTitleCE = sheetCEAudit.createRow(3); // Create Row
		Cell cellTitleRowCE = rowTitleCE.createCell(1);// Create Cell (Column)
		cellTitleRowCE.setCellValue(AuditCMConst.RPT_TITLE_CODE_TYPE_CE); // Set Cell Value
		cellTitleRowCE.setCellStyle(cellStyleTitleCE);
		sheetCEAudit.addMergedRegion(new CellRangeAddress(3, 3, 1, 10)); // Merge
																			// Cell

		// Create Row/Cell - Report Header Datatable
		Row rowHeaderTitleCE = sheetCEAudit.createRow(5);
		for (int i = 0; i < colHeaderDataTableCE.length; i++) {
			Cell cell = rowHeaderTitleCE.createCell(i + 1);
			cell.setCellValue(colHeaderDataTableCE[i]);
			cell.setCellStyle(colHeaderDataTableRptStyleCE);
		}

		// Section: Populate CE Audit Trail Details Record.
		// Create Row/Cell - Audit Trail Details
		int rowIndexCE = sheetCEAudit.getLastRowNum() + 1;
		if (!resultReportCEList.isEmpty()) {
			for (int a = 0; a < resultReportCEList.size(); a++) {
				Row currRow = sheetCEAudit.createRow(rowIndexCE++);

				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(resultReportCEList.get(a).getCreatedDate());
				cellActionDate.setCellStyle(cellStyleDateTimeCE);

				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(resultReportCEList.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleStringCE);

				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(resultReportCEList.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleStringCE);

				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(resultReportCEList.get(a).getOrgName());
				cellOrg.setCellStyle(cellStyleStringCE);

				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(resultReportCEList.get(a).getRoleName());
				cellRole.setCellStyle(cellStyleStringCE);

				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(resultReportCEList.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleStringCE);

				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(resultReportCEList.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleStringCE);

				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(resultReportCEList.get(a).getExtAttribute());
				cellSubDocNo.setCellStyle(cellStyleStringCE);

				Cell cellOldValue = currRow.createCell(9);
				cellOldValue.setCellValue(resultReportCEList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleStringCE);

				Cell cellNewValue = currRow.createCell(10);
				cellNewValue.setCellValue(resultReportCEList.get(a).getNewValue());
				cellNewValue.setCellStyle(cellStyleStringCE);
			}
		} else {
			notFoundRecord(sheetCEAudit, rowIndexCE, colHeaderDataTableCE.length, noRecordsStyleCE);
		}

		// Set column width
		setExcelColumnWidth(colHeaderDataTableCE.length, sheetCEAudit);

		// Export to Excel
		exportExcelReportAudit(wb);
	}

	/**
	 * Generate single excel format.
	 * 
	 */
	public void generateExcelByDocType() {
		List<AuditCodificationManagementSPDTO> resultReportList = new ArrayList<>();
		int activeIndex = getManagedBean().getActiveIndex();

		try {
			if (activeIndex == 0) {
				resultReportList = auditCMSO.getAuditCMExcelDetail(getManagedBean().getCommodityCode(),
						AuditCMConst.GROUP_SECTION_ITEM_ID);
			} else {
				resultReportList = auditCMSO.getAuditCMExcelDetail(getManagedBean().getCommodityCode(),
						AuditCMConst.GROUP_SECTION_EXTENSION_ID);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "generateExcelByDocType",
					null, AuditCMConst.AUDIT_CM_RPT_BB, null, e);
		}

		Workbook wb = new XSSFWorkbook();
		Sheet sheetExcelAudit;
		if (activeIndex == 0) {
			sheetExcelAudit = wb.createSheet(AuditCMConst.RPT_TITLE_CODE_TYPE_CI);
		} else {
			sheetExcelAudit = wb.createSheet(AuditCMConst.RPT_TITLE_CODE_TYPE_CE);
		}

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

		// Create Row/Cell - Report Title
		Row rowTitleExcel = sheetExcelAudit.createRow(1); // Create Row
		Cell cellTitleRow = rowTitleExcel.createCell(1);// Create Cell (Column)
		cellTitleRow.setCellValue(ReportExcelAuditConstant.RPT_TITLE); // Set Cell Value
		cellTitleRow.setCellStyle(cellStyleTitleRpt);
		sheetExcelAudit.addMergedRegion(new CellRangeAddress(1, 1, 1, 10)); // Merge Cell

		Row rowTitleByDocType = sheetExcelAudit.createRow(3); // Create Row
		Cell cellTitleByDocType = rowTitleByDocType.createCell(1);// Create Cell (Column)
		if (activeIndex == 0) {
			cellTitleByDocType.setCellValue(AuditCMConst.RPT_TITLE_CODE_TYPE_CI); // Set cell value
		} else {
			cellTitleByDocType.setCellValue(AuditCMConst.RPT_TITLE_CODE_TYPE_CE); // Set cell value
		}
		cellTitleByDocType.setCellStyle(cellStyleTitleRpt);
		sheetExcelAudit.addMergedRegion(new CellRangeAddress(3, 3, 1, 10)); // Merge Cell

		// Create Row/Cell - Report Header Datatable
		Row titleHeaderRow = sheetExcelAudit.createRow(5);
		for (int i = 0; i < colHeaderDataTableRpt.length; i++) {
			Cell cell = titleHeaderRow.createCell(i + 1);
			cell.setCellValue(colHeaderDataTableRpt[i]);
			cell.setCellStyle(colHeaderDataTableRptStyle);
		}

		// Section: Populate Excel Audit Trail Details Record By Doc Type (CI & CE)
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
				cellSubDocNo.setCellValue(resultReportList.get(a).getExtAttribute());
				cellSubDocNo.setCellStyle(cellStyleString);

				Cell cellOldValue = currRow.createCell(9);
				cellOldValue.setCellValue(resultReportList.get(a).getOldValue());
				cellOldValue.setCellStyle(cellStyleString);

				Cell cellNewValue = currRow.createCell(10);
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
	 * Generate PDF Report For All.
	 * 
	 */
	public void generatePDFAll() throws IOException {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, getManagedBean().getCommodityCode());// P_DOCID

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_CM, paramMap);
	}

	/**
	 * Generate PDF Report by Doc Type.
	 * 
	 */
	public void generatePDFByDocType() throws IOException {

		int activeIndex = getManagedBean().getActiveIndex();
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(AuditReportConst.PARAM_USER_ID, getUserIdSessionFromLoginId().toString());
		paramMap.put(AuditReportConst.PARAM_DOC_ID, getManagedBean().getCommodityCode());// P_DOCID
		if (activeIndex == 0) {
			paramMap.put(AuditReportConst.PARAM_GROUP_SECTION_ID, AuditCMConst.GROUP_SECTION_ITEM_ID.toString());// P_GROUP_SECTION_ID
		} else {
			paramMap.put(AuditReportConst.PARAM_GROUP_SECTION_ID, AuditCMConst.GROUP_SECTION_EXTENSION_ID.toString());// P_GROUP_SECTION_ID
		}

		generateReport(AuditReportConst.RPT_AUDIT_TRAIL_CM, paramMap);
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
		columnWidths.put(6, 22 * 256); // Col: No. Dokumen
		columnWidths.put(7, 40 * 256); // Col: Medan
		columnWidths.put(8, 25 * 256); // Col: Data Asal
		columnWidths.put(9, 25 * 256); // Col: Data Baru

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
			response.setHeader("Content-Disposition", "attachment; filename=" + AuditCMConst.CM_AU_EXCEL_FILE_NAME);
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
			SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "exportExcelReportAudit",
					null, AuditCMConst.AUDIT_CM_RPT_BB, null, e);
		}
	}
}
