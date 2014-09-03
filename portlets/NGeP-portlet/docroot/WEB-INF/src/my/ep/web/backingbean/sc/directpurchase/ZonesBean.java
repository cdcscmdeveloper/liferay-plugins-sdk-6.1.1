package my.ep.web.backingbean.sc.directpurchase;

import java.util.List;

import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;

public class ZonesBean {
	
	private TplSpecZoneDtl tplSpecZoneDtl;
	
	private List<TplSpecZoneLoc> tplSpecZoneLocs;

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}
	
	/**
	 * @return the tplSpecZoneLocs
	 */
	public List<TplSpecZoneLoc> getTplSpecZoneLocs() {
		return tplSpecZoneLocs;
	}

	/**
	 * @param tplSpecZoneLocs the tplSpecZoneLocs to set
	 */
	public void setTplSpecZoneLocs(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		this.tplSpecZoneLocs = tplSpecZoneLocs;
	}
	
}
