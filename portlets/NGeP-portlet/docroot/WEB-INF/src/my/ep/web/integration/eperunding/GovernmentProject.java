package my.ep.web.integration.eperunding;

/*
 * @P Ganga prasad
 */
public class GovernmentProject {

	private String mOFNo;
	private Project project;

	public String getmOFNo() {
		return mOFNo;
	}

	public void setmOFNo(String mOFNo) {
		this.mOFNo = mOFNo;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GovernmentProject) {
			GovernmentProject temp = (GovernmentProject) obj;
			if (this.mOFNo.equalsIgnoreCase(temp.mOFNo)
					&& this.project.getProjectName().equalsIgnoreCase(
							temp.project.getProjectName())
					&& this.project.getProjectNo().equalsIgnoreCase(
							temp.project.getProjectNo()))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.mOFNo.hashCode()
				+ this.project.getProjectName().hashCode() + this.project
				.getProjectNo().hashCode());
	}

}
