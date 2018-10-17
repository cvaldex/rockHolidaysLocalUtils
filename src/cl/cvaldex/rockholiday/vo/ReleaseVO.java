package cl.cvaldex.rockholiday.vo;

public class ReleaseVO {
	private String artist;
	private String releaseName;
	private String releaseType;
	private String firstReleaseDate;
	//filter
	private String primaryTypeId;
	private String typeId;
	
	
	public String getFirstReleaseDate() {
		return firstReleaseDate;
	}
	public void setFirstReleaseDate(String firstReleaseDate) {
		this.firstReleaseDate = firstReleaseDate;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getReleaseName() {
		return releaseName;
	}
	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append(releaseName);
		builder.append(" - ");
		builder.append(releaseType);
		builder.append(" - ");
		builder.append(firstReleaseDate);
		
		return builder.toString();
	}
	public String getPrimaryTypeId() {
		return primaryTypeId;
	}
	public void setPrimaryTypeId(String primaryTypeId) {
		this.primaryTypeId = primaryTypeId;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
}
