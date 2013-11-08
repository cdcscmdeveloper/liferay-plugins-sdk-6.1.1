/**
 * 
 */
package my.ep.web.managedbean.ss.quationtender;

import javax.faces.component.UICommand;

/**
 * @author admin
 *
 */
public class Node {
	private Long nodeId;
	private String nodeName;
	private String nodeType;
	public Node(){
		
	}
	
	public Node(Long nodeId,String nodeName){
		this.nodeId=nodeId;
		this.nodeName=nodeName;
	}
	public Node(Long nodeId,String nodeName,String nodeType){
		this.nodeId=nodeId;
		this.nodeName=nodeName;
		this.nodeType=nodeType;
	}
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public boolean getStateButtonRequired(){
		if("Zone".equals(nodeType)){
			return true;
		}
		return false;
	}
	public boolean getDistrictButtonRequired(){
		if("State".equals(nodeType)){
			return true;
		}
		return false;
	}

}
