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
	
}

