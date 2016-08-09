package si.fri.diploma.models;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"type",
	"t",
	"k"
})
public class TestEvent {

	@JsonProperty("type")
	private String type;
	@JsonProperty("t")
	private String t;
	@JsonProperty("k")
	private String k;
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
	 * The t
	 */
	@JsonProperty("t")
	public String getT() {
		return t;
	}

	/**
	 *
	 * @param t
	 * The t
	 */
	@JsonProperty("t")
	public void setT(String t) {
		this.t = t;
	}

	/**
	 *
	 * @return
	 * The k
	 */
	@JsonProperty("k")
	public String getK() {
		return k;
	}

	/**
	 *
	 * @param k
	 * The k
	 */
	@JsonProperty("k")
	public void setK(String k) {
		this.k = k;
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
