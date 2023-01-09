package com.project.orderservice.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.orderservice.dto.OrderRequest;
import com.project.orderservice.service.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CircuitBreaker(name="inventory", fallbackMethod="fallbackMethod")
	@TimeLimiter(name = "inventory")
    @Retry(name = "inventory")//the name should be same as the key provided in the properties file
	public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
		return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
		//orderService.placeOrder(orderRequest);
		//return "Order Placed Successfully!";
	}
	
	//return type of fallbackMethod should be same as the method for which circuit breaker is implemented
	public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
		return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time!");
	}
}
