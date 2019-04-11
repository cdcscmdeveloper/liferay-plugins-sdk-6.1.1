package my.ep.web.vo;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;
/* PrimeFaces impact - kenny - 20130129
import com.google.common.collect.Iterators;*/




public class FamiliesVO extends NameNode implements TreeNode {

	private List<ClassesVO> classVO = new ArrayList<ClassesVO>();
	private SegmentVO segment;





	public FamiliesVO() {
		this.setType("families");
	}

	public TreeNode getChildAt(int childIndex) {
		return classVO.get(childIndex);
	}

	public int getChildCount() {
		return classVO.size();
	}

	public TreeNode getParent() {
		return segment;
	}

	public void setParent(SegmentVO segment) {
		this.segment = segment;
	}

	public int getIndex(TreeNode node) {
		return classVO.indexOf(node);
	}

	public boolean getAllowsChildren() {
		return true;
	}

	public boolean isLeaf() {
		return classVO.isEmpty();
	}

	public Enumeration children() {
		/* PrimeFaces impact - kenny - 20130129
		return Iterators.asEnumeration(classVO.iterator());*/
		return null;
	}



	/**
	 * @return the classVO
	 */
	 public List<ClassesVO> getClassVO() {
		return classVO;
	}

	/**
	 * @param classVO the classVO to set
	 */
	 public void setClassVO(List<ClassesVO> classVO) {
		 this.classVO = classVO;
	 }


	 /**
	  * @return the segment
	  */
	 public SegmentVO getSegment() {
		 return segment;
	 }

	 /**
	  * @param segment the segment to set
	  */
	 public void setSegment(SegmentVO segment) {
		 this.segment = segment;
	 }








}