package com.project.productservice;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.productservice.dto.ProductRequest;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	/**
	 * This method dynamically sets the MongoDB uri as while testing we are not using the local database but the docker image
	 * @param dynamicPropertyRegistry
	 */
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}
	
	@Test
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String request = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform((RequestBuilder) ((ResultActions) MockMvcRequestBuilders.post("/api/product")
				.contentType("application/json")
				.content(request))
				.andExpect(status().isCreated()));
				
	}
	
	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone")
				.description("iPhone 14")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

}
