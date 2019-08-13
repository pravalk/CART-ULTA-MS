package com.ulta.cart.service;

import org.springframework.stereotype.Service;

import com.ulta.cart.vo.CreateCartRequest;

import io.sphere.sdk.carts.Cart;

@Service
public interface CartService {

	public Cart addToCart(CreateCartRequest requestDto) throws Exception;;
}
