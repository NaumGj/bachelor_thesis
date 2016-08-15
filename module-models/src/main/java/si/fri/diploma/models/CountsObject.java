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
public class CountsObject {

	@JsonProperty("counts")
	private Long counts;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	public CountsObject() {}

	/**
	 *
	 * @return
	 * The fifteen seconds latency
	 */
	@JsonProperty("counts")
	public Long getCounts() {
		return counts;
	}

	/**
	 *
	 * @param fifteen seconds latency
	 * The fifteen seconds latency
	 */
	@JsonProperty("fifteen")
	public void setCounts(Long counts) {
		this.counts = counts;
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
