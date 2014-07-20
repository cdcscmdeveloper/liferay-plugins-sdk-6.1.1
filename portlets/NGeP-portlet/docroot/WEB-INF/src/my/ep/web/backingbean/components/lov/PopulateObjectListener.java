package my.ep.web.backingbean.components.lov;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.web.bean.LovBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PopulateObjectListener implements ValueChangeListener {

	private static Logger log = Logger.getLogger(PopulateObjectListener.class);
	
	@Override
	public void processValueChange(ValueChangeEvent event)
			throws AbortProcessingException {

		LovBackingBean lovBB = (LovBackingBean)event.getComponent().getAttributes().get("lovBB");
//		lovBB.setLovSearchBean(new SearchBean());
		
		log.debug("lovBB.getLovSearchBean(): " + lovBB.getLovSearchBean());
		log.debug("lovBB.getLovSearchBean().getDataFilterBeans(): " + lovBB.getLovSearchBean().getDataFilterBeans());
		
		lovBB.getLovSearchBean().getDataFilterBeans().clear();
		
		LovBean lovBean = lovBB.getBeanByLovId((String)event.getComponent().getAttributes().get("lovId"));
		lovBB.setLovBean(lovBean);
		
//		ISearchResultBean resultBean = searchAction(lovSearchBean, true);
		String conditionField = (String)event.getComponent().getAttributes().get("conditionField");
		String saveObject = (String)event.getComponent().getAttributes().get("saveObject");
		
		lovBean.setSaveObject(saveObject);
		lovBean.setSelectionMode("single");
		
		log.debug("event.getNewValue(): " + event.getNewValue());
		log.debug("lovBean.getConditionField(): " + lovBean.getConditionField());
		
		if(StringUtils.contains(conditionField, "^this")) {
			lovBean.setConditionField(StringUtils.replace(conditionField, "this", (String)event.getNewValue()));
			log.debug("new condition: " + lovBB.getLovBean().getConditionField());
			
			ISearchResultBean resultBean = lovBB.searchAction(lovBB.getLovSearchBean(), true);
			if(!resultBean.getResultList().isEmpty()) {
				BaseEntity entity = (BaseEntity)resultBean.getResultList().get(0);
				log.info("entity: " + entity);
				lovBB.setSelectedEntity(entity);
				lovBB.populateToSaveObject();
			} else {
				throw new EpException(MessageConst.ERROR_721010);
			}
		}
	}

}
