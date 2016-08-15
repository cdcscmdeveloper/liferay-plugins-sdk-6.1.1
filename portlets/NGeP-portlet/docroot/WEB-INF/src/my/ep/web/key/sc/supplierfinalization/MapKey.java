package my.ep.web.key.sc.supplierfinalization;

import java.io.Serializable;

public class MapKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long zoneId;
	private Long itemId;
	
	public MapKey() {}
	
	public MapKey(Long zoneId, Long itemId) {
		this.zoneId = zoneId;
		this.itemId = itemId;
	}

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
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MapKey [itemId=" + (itemId == null ? 0 : itemId) + 
			   ", zoneId=" + (zoneId == null ? 0 : zoneId) + "]";
	}
	
}
