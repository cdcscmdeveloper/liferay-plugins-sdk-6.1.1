package my.ep.web.vo;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;




public class CommoditiesVO extends NameNode implements TreeNode{
	
	private ClassesVO classVO;
	private Long commoditySeq;
	
	public CommoditiesVO() {
        this.setType("commodities");
    }
	
	/* public CommoditiesVO(ClassesVO classVO) {
	        super();
	        this.setType("commodities");
	        this.classVO = classVO;
	       
	    }
	
	*/
	public TreeNode getChildAt(int childIndex) {
        return null;
    }
 
    public int getChildCount() {
        return 0;
    }
 
    public TreeNode getParent() {
        return classVO;
    }
 
    public int getIndex(TreeNode node) {
        return 0;
    }
 
    public boolean getAllowsChildren() {
        return false;
    }
 
    public boolean isLeaf() {
        return true;
    }
 
    public Enumeration<TreeNode> children() {
        return new Enumeration<TreeNode>() {
            public boolean hasMoreElements() {
                return false;
            }
 
            public TreeNode nextElement() {
                return null;
            }
        };
    }


	/**
	 * @return the classVO
	 */
	public ClassesVO getClassVO() {
		return classVO;
	}


	/**
	 * @param classVO the classVO to set
	 */
	public void setClassVO(ClassesVO classVO) {
		this.classVO = classVO;
	}

	/**
	 * @return the commoditySeq
	 */
	public Long getCommoditySeq() {
		return commoditySeq;
	}

	/**
	 * @param commoditySeq the commoditySeq to set
	 */
	public void setCommoditySeq(Long commoditySeq) {
		this.commoditySeq = commoditySeq;
	}
	
	
	
	
	

}