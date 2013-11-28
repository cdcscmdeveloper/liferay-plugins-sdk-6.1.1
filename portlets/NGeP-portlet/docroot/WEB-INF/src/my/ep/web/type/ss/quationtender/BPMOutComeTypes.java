/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 *
 */
public enum BPMOutComeTypes implements IEnumType{

	APPROVE("A"),REJECT("R"),VERIFY("V"),REVISE("S"),ACCEPT("E");
	
	private String name;
	
	BPMOutComeTypes(String type){
		this.name=type;
	}

	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public static String getValue(String name){
		BPMOutComeTypes[] bpmOutComeTypes= BPMOutComeTypes.values();
		for(BPMOutComeTypes bpmOutComeTypes2: bpmOutComeTypes){
			if(bpmOutComeTypes2.name.equalsIgnoreCase(name)){
				return (String) bpmOutComeTypes2.name();
			}
		}
		return name;
		
	}
	public static void main(String[] str){
		System.out.println(getValue("a"));
	}
}
