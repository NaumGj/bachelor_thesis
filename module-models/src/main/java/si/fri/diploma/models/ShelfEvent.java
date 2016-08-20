package si.fri.diploma.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShelfEvent extends SimpleEvent {

	@JsonProperty("product_id")
	private String productId;
	
	public ShelfEvent() {}

	/**
	 *
	 * @return
	 * The product ID
	 */
	@JsonProperty("product_id")
	public String getProductId() {
		return productId;
	}

	/**
	 *
	 * @param productId
	 * The product ID
	 */
	@JsonProperty("product_id")
	public void setProductId(String productId) {
		this.productId = productId;
	}

}
