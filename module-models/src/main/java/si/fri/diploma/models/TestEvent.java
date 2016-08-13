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
	"type",
	"timestamp",
	"serial_num",
	"timestamp_consumed"
})
public class TestEvent {

	@JsonProperty("type")
	private String type;
	@JsonProperty("timestamp")
	private Long timestamp;
	@JsonProperty("serial_num")
	private Integer serial_num;
	@JsonProperty("timestamp_consumed")
	private Long timestamp_consumed;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public TestEvent() {}

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
		return serial_num;
	}

	/**
	 *
	 * @param serial_num
	 * The serial_num
	 */
	@JsonProperty("serial_num")
	public void setSerialNum(Integer serial_num) {
		this.serial_num = serial_num;
	}
	
	/**
	 *
	 * @return
	 * The timestamp_consumed
	 */
	@JsonProperty("timestamp_consumed")
	public Long getTimestampConsumed() {
		return timestamp_consumed;
	}

	/**
	 *
	 * @param timestamp_consumed
	 * The timestamp_consumed
	 */
	@JsonProperty("timestamp_consumed")
	public void setTimestampConsumed(Long timestamp_consumed) {
		this.timestamp_consumed = timestamp_consumed;
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
