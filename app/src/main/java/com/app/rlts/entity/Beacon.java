package com.app.rlts.entity;

public class Beacon {
	private int beaconId;
	private String locationName;
	private String beaconName;
	private int minor;
	private int major;
	private String uuid;
	private String type;

	public Beacon(int beaconId, String locationName, String beaconName, int minor, int major, String uuid, String type) {
		this.beaconId = beaconId;
		this.locationName = locationName;
		this.beaconName = beaconName;
		this.minor = minor;
		this.major = major;
		this.uuid = uuid;
		this.type = type;
	}

	public int getBeaconId() {
		return beaconId;
	}

	public String getLocationName() {
		return this.locationName;
	}
	public String getBeaconName() {
		return this.beaconName;
	}
	public int getMinor() {
		return this.minor;
	}
	public int getMajor() {
		return this.major;
	}
	public String getUuid() {
		return this.uuid;
	}
	public String getType() {
		return this.type;
	}

	public void setBeaconId(int beaconId) {
		this.beaconId = beaconId;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public void setBeaconName(String beaconName) {
		this.beaconName = beaconName;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public void setType(String type) {
		this.type = type;
	}
}