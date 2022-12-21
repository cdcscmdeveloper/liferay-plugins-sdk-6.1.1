package my.ep.web.util.taglib;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.view.facelets.ComponentConfig;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.subtable.SubTable;

public class DirectPurchaseDataTableRender extends DataTableRenderer {
	private final static Logger logger = Logger.getLogger(DirectPurchaseDataTableRender.class.getName());
	
	public void encodeTbody(FacesContext context, DataTable table, boolean dataOnly) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String rowIndexVar = table.getRowIndexVar();
		String clientId = table.getClientId(context);
		String emptyMessage = table.getEmptyMessage();
		UIComponent emptyFacet = table.getFacet("emptyMessage");
		SubTable subTable = table.getSubTable();

		// TODO check this one
		/*
		 * if(table.isSelectionEnabled()) { table.findSelectedRowKeys(); }
		 */

		int rows = table.getRows();
		int first = table.getFirst();
		int rowCount = table.getRowCount();
		int rowCountToRender = rows == 0 ? (table.isLiveScroll() ? table.getScrollRows() : rowCount) : rows;
		boolean hasData = rowCount > 0;

		if (!dataOnly) {
			writer.startElement("tbody", null);
			writer.writeAttribute("id", clientId + "_data", null);
			writer.writeAttribute("class", DataTable.DATA_CLASS, null);
		}

		if (hasData) {
			for (int i = first; i < (first + rowCountToRender); i++) {
				encodeRow(context, table, clientId, i);
				if (subTable != null) {
					//Put subtable inside the cell
					writer.startElement("tr", null);
					writer.startElement("td", null);
					writer.writeAttribute("colspan", table.getColumnsCount(), null);

					encodeSubTable(context, table, subTable, i, rowIndexVar);

					writer.endElement("td");
					writer.endElement("tr");
				}
			}

		} else {
			// Empty message
			writer.startElement("tr", null);
			writer.writeAttribute("class", DataTable.EMPTY_MESSAGE_ROW_CLASS, null);

			writer.startElement("td", null);
			writer.writeAttribute("colspan", table.getColumnsCount(), null);

			if (emptyFacet != null)
				emptyFacet.encodeAll(context);
			else
				writer.write(emptyMessage);

			writer.endElement("td");

			writer.endElement("tr");
		}

		if (!dataOnly) {
			writer.endElement("tbody");
		}

		// Cleanup
		table.setRowIndex(-1);
		if (rowIndexVar != null) {
			context.getExternalContext().getRequestMap().remove(rowIndexVar);
		}
	}

	public boolean encodeRow(FacesContext context, DataTable table, String clientId, int rowIndex) throws IOException {
		int	_rowIndex = rowIndex;
		table.setRowIndex(_rowIndex);
		ResponseWriter writer = context.getResponseWriter();        
		boolean selectionEnabled = table.isSelectionEnabled();
		Object rowKey = null;

		if(selectionEnabled) {
			//try rowKey attribute
			rowKey = table.getRowKey();

			//ask selectable datamodel
			if(rowKey == null)
				rowKey = table.getRowKeyFromModel(table.getRowData());
		}

		//Preselection

		String userRowStyleClass = table.getRowStyleClass();
		String rowStyleClass = _rowIndex % 2 == 0 ? DataTable.ROW_CLASS + " " + DataTable.EVEN_ROW_CLASS : DataTable.ROW_CLASS + " " + DataTable.ODD_ROW_CLASS;

		if(table.isEditingRow())
			rowStyleClass = rowStyleClass + " " + DataTable.EDITING_ROW_CLASS;

		if(userRowStyleClass != null)
			rowStyleClass = rowStyleClass + " " + userRowStyleClass;

		writer.startElement("tr", null);
		writer.writeAttribute("data-ri", _rowIndex, null);
		if(rowKey != null) {
			writer.writeAttribute("data-rk", rowKey, null);
		}
		writer.writeAttribute("class", rowStyleClass, null);
		writer.writeAttribute("role", "row", null);
		// EnCode only first column and span all the table
		UIColumn column = table.getColumns().get(0);
		
		String style = column.getStyle();
		String styleClass = selectionEnabled ? DataTable.SELECTION_COLUMN_CLASS : (column.getCellEditor() != null) ? DataTable.EDITABLE_COLUMN_CLASS : null;
		String userStyleClass = column.getStyleClass();
		styleClass = userStyleClass == null ? styleClass : (styleClass == null) ? userStyleClass : styleClass + " " + userStyleClass;

		writer.startElement("td", null);
		writer.writeAttribute("colspan", column.getColspan(), null);
		writer.writeAttribute("role", "gridcell", null);
		if(style != null) writer.writeAttribute("style", style, null);
		if(styleClass != null) writer.writeAttribute("class", styleClass, null);
		column.encodeAll(context);
		writer.endElement("td");
		writer.endElement("tr");
		return true;
	}
}
