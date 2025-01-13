package my.ep.web.key.sc.supplierfinalization;

import java.io.Serializable;

public class MapKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long zoneId;
	private Long itemId;
	private Long supplierId; // 33380, #6906

	public MapKey() {}
	
	public MapKey(Long zoneId, Long itemId) {
		this.zoneId = zoneId;
		this.itemId = itemId;
		this.supplierId = 0L; // 33380, #6906
	}

	// 33380, #6906 - start
	/**
	 * Construct new MapKey with a composite key of supplierId, zoneId, itemId
	 * @param supplierId
	 * @param zoneId
	 * @param itemId
	 */
	public MapKey(Long supplierId, Long zoneId, Long itemId) {
		this.zoneId = zoneId;
		this.itemId = itemId;
		this.supplierId = supplierId;
	}
	// 33380, #6906 - end

	/**
	 * @return the zoneId
	 */
	public Long getZoneId() {
		return zoneId;
	}

	/**
	 * @param zoneId the zoneId to set
	 */
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	/**
	 * @return the itemId
	 */
	public Long getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = ((zoneId == null) ? 0 : zoneId.hashCode());
		result += ((itemId == null) ? 0 : itemId.hashCode());
		// 33380, #6906 -start-
		result += ((supplierId == null) ? 0 : supplierId.hashCode());
		// 33380, #6906 -end-
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		MapKey other = (MapKey) obj;
		if (zoneId == null && 
				other.zoneId != null) {
			return false;
		} else if (!zoneId.equals(other.zoneId)) {
			return false;
		}
		if (itemId == null && 
				other.itemId != null) {
			return false;
		} else if (!itemId.equals(other.itemId)) {
			return false;
		}
		
		// 33380 - start
		boolean supplierIsNull = null == supplierId && null != other.supplierId;
		boolean supplierNotEqual = null != supplierId && !supplierId.equals(other.supplierId);
		if (supplierIsNull || supplierNotEqual) {
			return false;
		}
		// 33380 - end
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MapKey [itemId=" + (itemId == null ? 0 : itemId) + 
			   ", zoneId=" + (zoneId == null ? 0 : zoneId) + 
			   ", supplierId=" + (zoneId == null ? 0 : zoneId) + "]"; //33380, #6906
	}

	// 33380, #6906 - start
	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	// 33380, #6906 - end
}
