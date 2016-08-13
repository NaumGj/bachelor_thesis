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
	"fifteen"
})
public class LatencyObject {

	@JsonProperty("fifteen")
	private Double fifteen;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public LatencyObject() {}

	/**
	 *
	 * @return
	 * The fifteen seconds latency
	 */
	@JsonProperty("fifteen")
	public Double getFifteen() {
		return fifteen;
	}

	/**
	 *
	 * @param fifteen seconds latency
	 * The fifteen seconds latency
	 */
	@JsonProperty("fifteen")
	public void setFifteen(Double fifteen) {
		this.fifteen = fifteen;
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
