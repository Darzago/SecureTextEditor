package persistence;

public class USBMetaData {
	private String driveLetter;
	private int Hash;
	
	public USBMetaData(String driveLetter, int hash) {
		super();
		this.driveLetter = driveLetter;
		Hash = hash;
	}
	
	public USBMetaData() {}
	
	/**
	 * @return the driveLetter
	 */
	public String getDriveLetter() {
		return driveLetter;
	}
	/**
	 * @param driveLetter the driveLetter to set
	 */
	public void setDriveLetter(String driveLetter) {
		this.driveLetter = driveLetter;
	}
	/**
	 * @return the hash
	 */
	public int getHash() {
		return Hash;
	}
	/**
	 * @param hash the hash to set
	 */
	public void setHash(int hash) {
		Hash = hash;
	}
}
