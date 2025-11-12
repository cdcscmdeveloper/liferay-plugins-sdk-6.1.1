package my.ep.web.backingbean.ct.audit;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.rpt.ReportExcelAuditConstant;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ct.audit.AuditContractManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.liferay.portal.util.PortalUtil;

/**
 * Base backing bean for audit contract-related managed beans.
 * 
 * @param <T>
 *            The type of {@link AuditContractManagedBean} that this class
 *            extends.
 * @author Putra
 */
public abstract class AuditCTBaseBackingBean<T extends AuditContractManagedBean> extends BaseBackingBean<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String FILE_NAME = "AuditCTBaseBackingBean";
	private static final String METHOD_NAME = "exportExcelReportAudit";

	/**
	 * Export/Download Document in Excel Format
	 * 
	 * @param wb
	 */
	public void exportExcelReportAudit(Workbook wb) {
		try {
			PortletResponse resp = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
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
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, FILE_NAME, "",
					METHOD_NAME, getUserId(), e);
		}
	}

	/**
	 * Set column width
	 * 
	 * @param totalNoOfCell
	 * @param sheetName
	 */
	public void setExlSheetColumnWidth(int totalNoOfCell, Sheet sheetName) {

		// Default width for specific column ranges
		int defaultWidth = 50 * 256;

		// Define a map for specific column widths
		Map<Integer, Integer> columnWidths = new HashMap<>();
		columnWidths.put(0, 23 * 256); // Col: Tarikh Tindakan
		columnWidths.put(1, 15 * 256); // Col: Jenis Tindakan
		columnWidths.put(2, 25 * 256); // Col: Tindakan Oleh
		columnWidths.put(3, 30 * 256); // Col: Organisasi
		columnWidths.put(4, 19 * 256); // Col: Peranan
		columnWidths.put(5, 19 * 256); // Col: Jenis Dokumen
		columnWidths.put(6, 22 * 256); // Col: No. Dokumen
		columnWidths.put(7, 35 * 256); // Col: Status Dokumen
		columnWidths.put(8, 20 * 256); // Col: Nama Medan
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
	public void createNotFoundRecord(Sheet sheet, int rowIndex, int totalCell, CellStyle noRecordsStyle) {
		   Row row = sheet.createRow(rowIndex);
		    int startCol = 1;
		    int endCol = totalCell;

		    // Buat & set style ke SEMUA sel dari kolom 1 sampai totalCell
		    for (int i = startCol; i <= endCol; i++) {
		        Cell cell = row.createCell(i);
		        if (i == startCol) {
		            cell.setCellValue(ReportExcelAuditConstant.RPT_NO_RECORDS);
		        }
		        cell.setCellStyle(noRecordsStyle); //noRecordsStyle dari buildDataCellStyleString()
		    }

		    sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, startCol, endCol));
	}

	/**
	 * Retrieves the current session user ID based on the user's screen name
	 * (login ID).
	 * 
	 * @return The user ID of the current session user.
	 */
	public Long getUserId() {
		FacesScopes facesScopes = new FacesScopes();

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(facesScopes.getUserScreenName());

		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		Long userId = (pmUser != null) ? pmUser.getUserId() : facesScopes.getUserSessionBean().getUserId();

		new LogKey(userId);
		return userId;
	}

}