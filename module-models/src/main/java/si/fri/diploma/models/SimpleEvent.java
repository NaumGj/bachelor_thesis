package si.fri.diploma.models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "temperature"),
    @JsonSubTypes.Type(value = SmokeSensorEvent.class, name = "smoke"),
    @JsonSubTypes.Type(value = ShelfEvent.class, name = "shelf"),
    @JsonSubTypes.Type(value = PaidEvent.class, name = "paid"),
    @JsonSubTypes.Type(value = ExitEvent.class, name = "exit")
})
public class SimpleEvent {

	@JsonProperty("@type")
	private String type2;
	@JsonProperty("type")
	private String type;
	@JsonProperty("timestamp")
	private Long timestamp;
	@JsonProperty("serial_num")
	private Integer serialNum;
	@JsonProperty("timestamp_consumed")
	private Long timestampConsumed;
	
	public SimpleEvent() {}

	/**
	 *
	 * @return
	 * The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 *
	 * @param type
	 * The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 * The timestamp
	 */
	@JsonProperty("timestamp")
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 *
	 * @param timestamp
	 * The timestamp
	 */
	@JsonProperty("timestamp")
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 *
	 * @return
	 * The serial_num
	 */
	@JsonProperty("serial_num")
	public Integer getSerialNum() {
		return serialNum;
	}

	/**
	 *
	 * @param serial_num
	 * The serial_num
	 */
	@JsonProperty("serial_num")
	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}
	
	/**
	 *
	 * @return
	 * The timestamp_consumed
	 */
	@JsonProperty("timestamp_consumed")
	public Long getTimestampConsumed() {
		return timestampConsumed;
	}

	/**
	 *
	 * @param timestamp_consumed
	 * The timestamp_consumed
	 */
	@JsonProperty("timestamp_consumed")
	public void setTimestampConsumed(Long timestampConsumed) {
		this.timestampConsumed = timestampConsumed;
	}

//	@JsonAnyGetter
//	public Map<String, Object> getAdditionalProperties() {
//		return this.additionalProperties;
//	}
//
//	@JsonAnySetter
//	public void setAdditionalProperty(String name, Object value) {
//		this.additionalProperties.put(name, value);
//	}

}
