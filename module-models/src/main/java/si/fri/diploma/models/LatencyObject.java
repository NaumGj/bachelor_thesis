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
	"ten"
})
public class LatencyObject {

	@JsonProperty("ten")
	private Double ten;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public LatencyObject() {}

	/**
	 *
	 * @return
	 * The ten seconds latency
	 */
	@JsonProperty("ten")
	public Double getTen() {
		return ten;
	}

	/**
	 *
	 * @param ten seconds latency
	 * The ten seconds latency
	 */
	@JsonProperty("ten")
	public void setTen(Double ten) {
		this.ten = ten;
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
