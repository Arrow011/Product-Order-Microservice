package com.project.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.project.orderservice.dto.InventoryResponse;
import com.project.orderservice.dto.OrderLineItemsDto;
import com.project.orderservice.dto.OrderRequest;
import com.project.orderservice.event.OrderPlacedEvent;
import com.project.orderservice.model.Order;
import com.project.orderservice.model.OrderLineItems;
import com.project.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	
	private final OrderRepository orderRepository;
	
	private final WebClient.Builder webClientBuilder;
	
	 private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
	
	public String placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNo(UUID.randomUUID().toString());
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
														  .stream()
														  .map(orderLineItemsDto -> mapToDto(orderLineItemsDto)).toList();
		order.setOrderLineItemsList(orderLineItems);
		
		List<String> skuCodes = order.getOrderLineItemsList().stream()
				.map(orderLineItem -> orderLineItem.getSkuCode()).toList();
		
		InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
			.uri("http://inventory-service/api/inventory",
					uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
			.retrieve()
			.bodyToMono(InventoryResponse[].class)
			.block();
		boolean allProductsInStock = Arrays.stream(inventoryResponsArray)
				.allMatch(inventoryResponse -> inventoryResponse.isInStock());
		
		if(allProductsInStock) {
			orderRepository.save(order);
			kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNo()));
			return "Order Placed Successfully.";
		}else {
			throw new IllegalArgumentException("Product is not in stock, please try again later");
		}
		
	}
	
	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		return OrderLineItems.builder()
				.price(orderLineItemsDto.getPrice())
				.quantity(orderLineItemsDto.getQuantity())
				.skuCode(orderLineItemsDto.getSkuCode())
				.build();
	}
}
