/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.tpl.spec;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.tpl.spec.ZoneLocationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Zone Location Backing Bean
 * 
 * @author Kenny
 * @since 31.05.2014
 */
@ManagedBean
@ViewScoped
public class ZoneLocationBackingBean extends BaseBackingBean<ZoneLocationManagedBean> {

	private static Logger log = Logger.getLogger(ZoneLocationBackingBean.class);
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	public ZoneLocationBackingBean() {
	}
	
	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		debugLogger("setTplSpecZoneDtl");
		
		ISpecZoneLocSO specZoneLocSO = null;
		try {			
			specZoneLocSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.COMMON_COMPONENT, ISpecZoneLocSO.class);
			
			SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
			reqDTO.setDocType(TemplateSpecConst.SPEC_LOC_DOC_TYPE_ZONE);
			reqDTO.setDocId(tplSpecZoneDtl.getSpecZoneId());
			
			SpecZoneLocResDTO resDTO = specZoneLocSO.retrieveLocationDtlsByDocIdNType(reqDTO);
			List<TplSpecZoneLoc> tplSpecZoneLocs = new ArrayList<TplSpecZoneLoc>();
			
			for (TplSpecLoc tplSpecLoc : resDTO.getLocationsList()) {
				if (tplSpecLoc instanceof TplSpecZoneLoc) {
					tplSpecZoneLocs.add((TplSpecZoneLoc)tplSpecLoc);
				}
			}
			
			List<TreeNode> locTreeNodes = LocationSingleton.getInstance().constuctTreeNode(
					tplSpecZoneDtl, 
					tplSpecZoneLocs).getChildren().get(0).getChildren();
			TreeNode locationTree = new DefaultTreeNode();
			for (TreeNode treeNode : locTreeNodes) {
				locationTree.getChildren().add(treeNode);
			}
			
			expandTreeNode(locationTree);
			
			getManagedBean().setTplSpecZoneDtl(tplSpecZoneDtl);
			getManagedBean().setLocationTree(locationTree);
		} finally {
			specZoneLocSO = null;
		}
	}
	
	private void expandTreeNode(TreeNode node) {
		node.setExpanded(true);
		
		for (TreeNode childNode : node.getChildren()) {
			expandTreeNode(childNode);
		}
	}
}

