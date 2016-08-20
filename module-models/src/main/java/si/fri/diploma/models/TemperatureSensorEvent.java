package si.fri.diploma.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemperatureSensorEvent extends SimpleEvent {

	@JsonProperty("sensor_id")
	private Integer sensorId;
	@JsonProperty("room_number")
	private Integer roomNumber;
	@JsonProperty("celsius_temperature")
	private Double celsiusTemperature;
	
	public TemperatureSensorEvent() {}

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
	 * The temperature in Celsius degrees
	 */
	@JsonProperty("celsius_temperature")
	public Double getCelsiusTemperature() {
		return celsiusTemperature;
	}

	/**
	 *
	 * @param celsiusTemperature
	 * The temperature in Celsius degrees
	 */
	@JsonProperty("celsius_temperature")
	public void setCelsiusTemperature(Double celsiusTemperature) {
		this.celsiusTemperature = celsiusTemperature;
	}
	
}
