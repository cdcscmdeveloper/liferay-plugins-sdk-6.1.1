/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.cc;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.persistence.entity.BaseEntity;
import my.ep.web.backingbean.BaseBackingBean;

import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 * DataTable Backing Bean
 * 
 * @author Kenny
 * @since 28.07.2014
 */
@ManagedBean
@RequestScoped
public class DataTableBackingBean extends BaseBackingBean {

	private static Logger log = Logger.getLogger(DataTableBackingBean.class);
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	public DataTableBackingBean() {
	}
	
	@SuppressWarnings("unchecked")
	public void rowSelectEvent(AjaxBehaviorEvent event) {
		debugLogger("rowSelectEvent");
		
		if (!(event.getSource() instanceof DataTable)) {
			throw new IllegalArgumentException("Invalid component source");
		}
		
		boolean selected;
		DataTable dataTable = ((DataTable)event.getSource());
		List<Object> list = (List<Object>)dataTable.getValue();
		List<Object> selectedList = (List<Object>)dataTable.getLocalSelection();
		
		if (list.size() > 0) {
			for (Object object : list) {
				if (object instanceof BaseEntity) {
					selected = selectedList.indexOf(object) >= 0;
					((BaseEntity)object).setSelected(selected);
					log.info(object + ":" + selected);
				}	
			}
		}
	}
	
	/**
	 * Navigate to first page
	 * 
	 * @param specComponentId
	 * 		the specification component id
	 */
	public void navToFirstPage(String specComponentId) {
		log.info("navToFirstPage");
		RequestContext.getCurrentInstance().execute(
				specComponentId + "questionTblWidget.paginator.setPage(0, false); " +
				specComponentId + "SpecReRender();");
	}
	
	/**
	 * Navigate to previous page
	 * 
	 * @param specComponentId
	 * 		the specification component id
	 */
	public void navToPrevPage(String specComponentId) {
		log.info("navToPrevPage");
		RequestContext.getCurrentInstance().execute(
				specComponentId + "questionTblWidget.paginator.setPage(" + specComponentId + "questionTblWidget.paginator.cfg.page - 1, false); " +
				specComponentId + "SpecReRender();");
	}
	
	/**
	 * Navigate to next page
	 * 
	 * @param specComponentId
	 * 		the specification component id
	 */
	public void navToNextPage(String specComponentId) {
		log.info("navToNextPage");
		RequestContext.getCurrentInstance().execute(
				specComponentId + "questionTblWidget.paginator.setPage(" + specComponentId + "questionTblWidget.paginator.cfg.page + 1, false); " +
				specComponentId + "SpecReRender();");
	}
	
	/**
	 * Navigate to last page
	 * 
	 * @param specComponentId
	 * 		the specification component id
	 */
	public void navToLastPage(String specComponentId) {
		log.info("navToLastPage");
		RequestContext.getCurrentInstance().execute(
				specComponentId + "questionTblWidget.paginator.setPage(" + specComponentId + "questionTblWidget.paginator.cfg.pageCount - 1, false); " +
				specComponentId + "SpecReRender();");
	}
}

