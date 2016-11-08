/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.bean.sc.common;

import java.util.List;

import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;

// TODO: Auto-generated Javadoc
/**
 * The Class ZonesBean.
 * 
 * @author Sri
 */
public class ZonesBean {
	
	/** The tpl spec zone dtl. */
	private TplSpecZoneDtl tplSpecZoneDtl;
	
	/** The tpl spec zone locs. */
	private List<TplSpecZoneLoc> tplSpecZoneLocs;

	/**
	 * Gets the tpl spec zone dtl.
	 *
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * Sets the tpl spec zone dtl.
	 *
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}
	
	/**
	 * Gets the tpl spec zone locs.
	 *
	 * @return the tplSpecZoneLocs
	 */
	public List<TplSpecZoneLoc> getTplSpecZoneLocs() {
		return tplSpecZoneLocs;
	}

	/**
	 * Sets the tpl spec zone locs.
	 *
	 * @param tplSpecZoneLocs the tplSpecZoneLocs to set
	 */
	public void setTplSpecZoneLocs(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		this.tplSpecZoneLocs = tplSpecZoneLocs;
	}
	
}
