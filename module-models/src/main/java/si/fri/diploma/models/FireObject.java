package si.fri.diploma.models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"room_number",
	"celsius_temperature",
	"obscuration"
})
public class FireObject {

	@JsonProperty("room_number")
	private Integer roomNumber;
	@JsonProperty("celsius_temperature")
	private Double celsiusTemperature;
	@JsonProperty("obscuration")
	private Double obscuration;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public FireObject() {}

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

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
