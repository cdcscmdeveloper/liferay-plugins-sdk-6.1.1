package my.ep.web.backingbean.pm.audit;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.AuditPMDaoConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.VAuditPMDTO;
import my.ep.helper.EntityHelper;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.constant.report.AuditReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.pm.audit.AuditPMManagedBean;
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
 * 35509
 */
@ManagedBean(name = "auditPMDocBBean")
@ViewScoped
public class AuditPMDocumentBackingBean extends BaseBackingBean<AuditPMManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditPMDocumentBackingBean.class);
	private transient Workbook wb;
	private List<VAuditPMDTO> details;
	
	/**
	 * Generate report for single tab in PDF format.
	 * @param reqDTO
	 */
	public void generateReport(AuditPMReqDTO reqDTO) {
		try {
			Map<String, String> paramMap = new HashMap<>();
			String proc = getAuditPMSO().getProcedureName(reqDTO.getCategoryConfigParamLevel2().trim());
			proc = proc.contains(".") ? proc.substring(proc.indexOf('.') + 1) : proc;
			
			paramMap.put(AuditReportConst.PARAM_PROCESS, proc);
			paramMap.put(AuditPMDaoConst.P_PMPARAMETERLVLID, reqDTO.getPmParameterLvlId().toString());
			paramMap.put(AuditPMDaoConst.P_ITEMCODE, reqDTO.getpItemCode());
			paramMap.put(AuditPMDaoConst.P_ITEMNAME, reqDTO.getpItemName());
			paramMap.put(AuditPMDaoConst.P_ACTION_BY, reqDTO.getCreatedBy());
			paramMap.put(AuditReportConst.PARAM_USER_ID, String.valueOf(getUserIdSessionFromLoginId()));
			paramMap.put(AuditPMDaoConst.P_ROLECODE, reqDTO.getpRoleCode());
			
			SimpleDateFormat sdf = new SimpleDateFormat(ReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
			if (reqDTO.getPdateFrom() != null) {
				paramMap.put(AuditPMDaoConst.P_DATEFROM, sdf.format(reqDTO.getPdateFrom()));
			}
			if (reqDTO.getPdateTo() != null) {
				paramMap.put(AuditPMDaoConst.P_DATETO, sdf.format(reqDTO.getPdateTo()));
			}
			if(reqDTO.getpDocGroupId() != null){
				paramMap.put(AuditPMDaoConst.P_DOCGROUPID, reqDTO.getpDocGroupId().toString());
			}
			if(reqDTO.getpUserOrgProfileId() != null){
				paramMap.put(AuditPMDaoConst.P_USERORGPROFILEID, reqDTO.getpUserOrgProfileId().toString());
			}
			paramMap.values().removeAll(Collections.singleton(null));
			generateReport(AuditReportConst.RPT_AUDIT_TRAIL_PM, paramMap);
			
		} catch (Exception e) {
			log.error(e);
		}
	}


	/**
	 * Generate single tab excel format.
	 * @param reqDTO
	 * @param searchDetail
	 */
	public void generateRptExcel(AuditPMReqDTO reqDTO, IPagingDataModel<VAuditPMDTO> searchDetail) {
		String oriLanguange = reqDTO.getpLanguage();
		try {
			reqDTO.setFetchAll(true);
			reqDTO.setpLanguage(AuditConst.PM_LANGUAGECODE_MS);
			ISearchResultBean<VAuditPMDTO> res = getAuditPMSO().searchDetailResultList(reqDTO, searchDetail.getSearchBean());
			details = res.getResultList();
			
			createSheetLayout();
			exportExcelReportAudit();
			
		} catch (Exception e) {
			log.error(e);
		}
		reqDTO.setFetchAll(false);
		reqDTO.setpLanguage(oriLanguange);
		wb = null;
	}
	
	private void createSheetLayout(){
		if (wb == null){
			wb = new XSSFWorkbook();
		}
		Sheet sheetRptAudit = wb.createSheet("Sheet1");
		// Setting Report - Populate Table Header Cell Value
		String[] colHeaderDataTableRpt = ReportExcelAuditConstant.ColHeaderListType3;
		CellStyle cellStyleTitleRpt = new ExcelCellStyleBuilder(wb).buildTitleReportCellStyle();
		// Section - Setting Table Header Cell Style for each cell
		CellStyle colHeaderDataTableRptStyle = new ExcelCellStyleBuilder(wb).setHAlignment(HorizontalAlignment.CENTER)
				.setVAlignment(VerticalAlignment.CENTER).buildHeaderCellStyle();
		
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
		if (!EntityHelper.isCollectionEmpty(details)) {
			for (int a = 0; a < details.size(); a++) {
				Row currRow = sheetRptAudit.createRow(rowIndex++);
				Cell cellActionDate = currRow.createCell(1);
				cellActionDate.setCellValue(details.get(a).getCreatedDate());		//getActionDate
				cellActionDate.setCellStyle(cellStyleDateTime);
				Cell cellActionType = currRow.createCell(2);
				cellActionType.setCellValue(details.get(a).getActionType());
				cellActionType.setCellStyle(cellStyleString);
				Cell cellActionBy = currRow.createCell(3);
				cellActionBy.setCellValue(details.get(a).getActionBy());
				cellActionBy.setCellStyle(cellStyleString);
				Cell cellOrg = currRow.createCell(4);
				cellOrg.setCellValue(details.get(a).getOrgName());		//getOrg
				cellOrg.setCellStyle(cellStyleString);
				Cell cellRole = currRow.createCell(5);
				cellRole.setCellValue(details.get(a).getRoleName());	//getRole
				cellRole.setCellStyle(cellStyleString);
				Cell cellDocType = currRow.createCell(6);
				cellDocType.setCellValue(details.get(a).getDocType());
				cellDocType.setCellStyle(cellStyleString);
				Cell cellDocNo = currRow.createCell(7);
				cellDocNo.setCellValue(details.get(a).getDocNo());
				cellDocNo.setCellStyle(cellStyleString);
				Cell cellSubDocNo = currRow.createCell(8);
				cellSubDocNo.setCellValue(details.get(a).getFieldLabelName());	//getFieldName
				cellSubDocNo.setCellStyle(cellStyleString);
				Cell cellFieldName = currRow.createCell(9);
				cellFieldName.setCellValue(details.get(a).getOldValue());
				cellFieldName.setCellStyle(cellStyleString);
				Cell cellOldValue = currRow.createCell(10);
				cellOldValue.setCellValue(details.get(a).getNewValue());
				cellOldValue.setCellStyle(cellStyleString);
			}
		} else {
			createNotFoundRecord(sheetRptAudit, rowIndex, colHeaderDataTableRpt.length);
		}
		// Set column width
		setExlSheetColumnWidth(colHeaderDataTableRpt.length, sheetRptAudit);
		
	}
	
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

	private void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell) {
		// Section - Setting Cell Style for No Records Found message.
		CellStyle noRecordsStyle = new ExcelCellStyleBuilder(wb).buildDefaultCellStyle();
		Row noRecordRow = sheet.createRow(rowIndex);
		Cell cellNoRecord = noRecordRow.createCell(1);
		cellNoRecord.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		cellNoRecord.setCellStyle(noRecordsStyle);
		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(), sheet.getLastRowNum(), 1, totalCell));
	}
	
	private void exportExcelReportAudit() {
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
			log.error(e);
		}
	}
	
	private IAuditPMSO getAuditPMSO(){
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAuditPMSO.class);
	}

}
