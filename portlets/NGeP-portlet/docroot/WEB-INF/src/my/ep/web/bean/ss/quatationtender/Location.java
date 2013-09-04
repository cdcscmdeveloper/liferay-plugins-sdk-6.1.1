package my.ep.web.bean.ss.quatationtender;

public class Location {

	private String state;
	
	private String location;

	public Location(){
		
	}
	public Location(String state,String location){
		this.state=state;
		this.location=location;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
