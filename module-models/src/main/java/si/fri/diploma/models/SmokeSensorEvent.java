package si.fri.diploma.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmokeSensorEvent extends SimpleEvent {

	@JsonProperty("sensor_id")
	private Integer sensorId;
	@JsonProperty("room_number")
	private Integer roomNumber;
	@JsonProperty("obscuration")
	private Double obscuration;
	
	public SmokeSensorEvent() {}

	/**
	 *
	 * @return
	 * The sensor ID
	 */
	@JsonProperty("sensor_id")
	public Integer getSensorId() {
		return sensorId;
	}

	/**
	 *
	 * @param sensorId
	 * The sensor ID
	 */
	@JsonProperty("sensor_id")
	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	/**
	 *
	 * @return
	 * The room number
	 */
	@JsonProperty("room_number")
	public Integer getRoomNumber() {
		return roomNumber;
	}

	/**
	 *
	 * @param roomNumber
	 * The room number
	 */
	@JsonProperty("room_number")
	public void setRoomNumber(Integer roomNumber) {
		this.roomNumber = roomNumber;
	}

	/**
	 *
	 * @return
	 * The obscuration
	 */
	@JsonProperty("obscuration")
	public Double getObscuration() {
		return obscuration;
	}

	/**
	 *
	 * @param obscuration
	 * The obscuration
	 */
	@JsonProperty("obscuration")
	public void setObscuration(Double obscuration) {
		this.obscuration = obscuration;
	}
	
}
