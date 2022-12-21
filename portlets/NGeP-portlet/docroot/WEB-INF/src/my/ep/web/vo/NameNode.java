package my.ep.web.vo;
 
import java.io.Serializable;
 
public class NameNode implements Serializable {
    protected String type;
    protected String name;
    protected String parentCode;
    protected Integer UnspscLevel;
    protected long seq;
    
    
    
    
    public Integer getUnspscLevel() {
		return UnspscLevel;
	}

	public void setUnspscLevel(Integer unspscLevel) {
		UnspscLevel = unspscLevel;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getType() {
        return type;
    }
 
    public void setType(String type) {
        this.type = type;
    }
    
    /**
	 * @return the seq
	 */
	public long getSeq() {
		return seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(long seq) {
		this.seq = seq;
	}

	@Override
    public String toString() {
        return this.name;
    }
}