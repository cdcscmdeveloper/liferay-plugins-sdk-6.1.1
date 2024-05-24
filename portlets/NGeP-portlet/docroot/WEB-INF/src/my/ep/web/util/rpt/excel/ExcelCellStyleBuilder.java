/**
 * 
 */
package my.ep.web.util.rpt.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * #28895 - Audit Trail 
 * Cell style configuration for generating report in excel format 
 * for Audit Trail Detail Transaction Records.
 * 
 * @author norulhaidah
 * @since 03042024
 */
public class ExcelCellStyleBuilder {

	private Workbook workbook;
	private CreationHelper creationHelper;
    private Font font;
    private Boolean fontBold;
    private Boolean txtIsWrap;
    private short fillForegroundColor;
    private FillPatternType fillPattern;
    private HorizontalAlignment hAlignment;
    private VerticalAlignment vAlignment;
    
    /**
     * Assign default cell style setting.
     * @param workbook
     */
    public ExcelCellStyleBuilder(Workbook workbook) {
    	this.workbook = workbook;
        this.creationHelper = workbook.getCreationHelper();
        this.font = workbook.createFont();
        this.fontBold = Boolean.FALSE;
        this.txtIsWrap = Boolean.TRUE;
        this.fillForegroundColor = IndexedColors.GREY_25_PERCENT.getIndex();
        this.fillPattern = FillPatternType.SOLID_FOREGROUND;
        this.hAlignment = HorizontalAlignment.LEFT;
        this.vAlignment = VerticalAlignment.TOP;
    }
    
    public ExcelCellStyleBuilder setCreationHelper(CreationHelper creationHelper){
    	this.creationHelper = creationHelper;
    	return this;
    }
    
    public ExcelCellStyleBuilder setFont(Font font) {
        this.font = font;
        return this;
    }
    
    public ExcelCellStyleBuilder setIsTxtWrap(Boolean isTxtWrap){
    	this.txtIsWrap = isTxtWrap;
    	return this;
    }

    public ExcelCellStyleBuilder setForegroundColor(short color) {
        this.fillForegroundColor = color;
        return this;
    }

    public ExcelCellStyleBuilder setFillPattern(FillPatternType fillPattern) {
		this.fillPattern = fillPattern;
		return this;
	}

	public ExcelCellStyleBuilder setHAlignment(HorizontalAlignment hAlignment) {
        this.hAlignment = hAlignment;
        return this;
    }
	
	public ExcelCellStyleBuilder setVAlignment(VerticalAlignment vAlignment) {
        this.vAlignment = vAlignment;
        return this;
    }
	
	public ExcelCellStyleBuilder setFontBold(Boolean fontBold){
		this.fontBold = fontBold;
		return this;
	}
	
	/**
	 * Build report default font type & size.
	 */
	private void setDefaultFontForAll(){
		this.font.setFontName("Arial");
		this.font.setFontHeightInPoints((short)10);
	}
	
	/**
	 * Build report table border cell style setting.
	 * @param cellStyle
	 */
	private void setDataTableBorder(CellStyle cellStyle){
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
	}
	
	/**
	 * Build default cell style
	 * @return
	 */
	public CellStyle buildDefaultCellStyle(){
		CellStyle cellStyle = workbook.createCellStyle();
		setDefaultFontForAll();
		font.setBold(fontBold);
		
		cellStyle.setFont(font);
		cellStyle.setAlignment(hAlignment);
        cellStyle.setVerticalAlignment(vAlignment);
		
		return cellStyle;
	}
	
	/**
	 * Build cell style for report title
	 * @return
	 */
	public CellStyle buildTitleReportCellStyle(){
		
		CellStyle cellStyle = workbook.createCellStyle();
		font.setBold(Boolean.TRUE);
		setDefaultFontForAll();
		cellStyle.setFont(font);
		cellStyle.setAlignment(hAlignment);
        cellStyle.setVerticalAlignment(vAlignment);
        
		return cellStyle;
	}
    
	/**
	 * Build cell style for report table header.
	 * @return
	 */
	public CellStyle buildHeaderCellStyle() {
		//Setting font-Category
        CellStyle cellStyle = workbook.createCellStyle();
        setDefaultFontForAll();
        font.setBold(Boolean.TRUE);
        
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(fillForegroundColor);
        cellStyle.setFillPattern(fillPattern);
        cellStyle.setAlignment(hAlignment);
        cellStyle.setVerticalAlignment(vAlignment);
        setDataTableBorder(cellStyle);
        
        return cellStyle;
    }
	
	/**
	 * Build format cell style for String
	 * @return
	 */
	public CellStyle buildDataCellStyleString(){
		
		CellStyle cellStyle = workbook.createCellStyle();
		setDefaultFontForAll();
		cellStyle.setFont(font);
		cellStyle.setAlignment(hAlignment);
		cellStyle.setVerticalAlignment(vAlignment);
		cellStyle.setWrapText(txtIsWrap);
		setDataTableBorder(cellStyle);
		 
		return cellStyle;
	}
	
	/**
	 * Build format cell style for DateTime
	 * @return
	 */
	public CellStyle buildDataCellStyleDateTime(){
		
		CellStyle cellStyle = workbook.createCellStyle();
		setDefaultFontForAll();
		cellStyle.setFont(font);
		cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy hh:mm:ss AM/PM"));
		cellStyle.setAlignment(hAlignment);
		cellStyle.setVerticalAlignment(vAlignment);
		setDataTableBorder(cellStyle);
		
		return cellStyle;
	}

	
}
