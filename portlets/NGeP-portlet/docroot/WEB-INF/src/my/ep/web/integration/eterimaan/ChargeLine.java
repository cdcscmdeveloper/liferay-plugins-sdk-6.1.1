package my.ep.web.integration.eterimaan;

/*
 * author @Ganga prasad
*/
public class ChargeLine {

	private int recordType;
	private int recordNumber;
	private String DK;
	private String kodPenjenisanTeriman;
	private String votDana;
	private String pegawaiPengawalDipertanggug;
	private String kumpulanPtjNptjDipertanggug;
	private String programAktiviti;
	private String projek;
	private String kodAkaun;
	private int amount;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getDK() {
		return DK;
	}

	public void setDK(String dK) {
		DK = dK;
	}

	public String getKodPenjenisanTeriman() {
		return kodPenjenisanTeriman;
	}

	public void setKodPenjenisanTeriman(String kodPenjenisanTeriman) {
		this.kodPenjenisanTeriman = kodPenjenisanTeriman;
	}

	public String getVotDana() {
		return votDana;
	}

	public void setVotDana(String votDana) {
		this.votDana = votDana;
	}

	public String getPegawaiPengawalDipertanggug() {
		return pegawaiPengawalDipertanggug;
	}

	public void setPegawaiPengawalDipertanggug(
			String pegawaiPengawalDipertanggug) {
		this.pegawaiPengawalDipertanggug = pegawaiPengawalDipertanggug;
	}

	public String getKumpulanPtjNptjDipertanggug() {
		return kumpulanPtjNptjDipertanggug;
	}

	public void setKumpulanPtjNptjDipertanggug(
			String kumpulanPtjNptjDipertanggug) {
		this.kumpulanPtjNptjDipertanggug = kumpulanPtjNptjDipertanggug;
	}

	public String getProgramAktiviti() {
		return programAktiviti;
	}

	public void setProgramAktiviti(String programAktiviti) {
		this.programAktiviti = programAktiviti;
	}

	public String getProjek() {
		return projek;
	}

	public void setProjek(String projek) {
		this.projek = projek;
	}

	public String getKodAkaun() {
		return kodAkaun;
	}

	public void setKodAkaun(String kodAkaun) {
		this.kodAkaun = kodAkaun;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
