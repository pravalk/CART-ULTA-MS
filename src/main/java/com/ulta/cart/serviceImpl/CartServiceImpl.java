package com.ulta.cart.serviceImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import javax.money.CurrencyContext;
import javax.money.CurrencyUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neovisionaries.i18n.CountryCode;
import com.ulta.cart.controller.CartController;
import com.ulta.cart.exception.CartException;
import com.ulta.cart.service.CartService;
import com.ulta.cart.vo.CreateCartRequest;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.queries.CartByCustomerIdGet;
import io.sphere.sdk.client.SphereClient;

@Service
public class CartServiceImpl implements CartService {

	private static final int MASTER_VARIANT_ID = 1;
	@Autowired
	SphereClient sphereClient;
	Cart cart = null;
	static Logger log = LoggerFactory.getLogger(CartController.class);

	@Override
	public Cart addToCart(CreateCartRequest requestDto) throws CartException {
		// String customerId = "3105139a-d065-4589-a581-522b55a7dd25";
		if (requestDto.isAnonymousUser()) {
			try {
				cart = createCartForAnonymousUser(requestDto).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Exception during creating cart for anonymous user, details-" + e.getMessage());
				throw new CartException(e.getMessage());
			}
			log.info("Cart created Successfully for anonymous customer" + cart.getId());
		} else {
			boolean isCartAvailable = isCartAvailable(requestDto.getCustomerId());
			if (!isCartAvailable) {
				try {
					cart = createCart(requestDto).get();
				} catch (InterruptedException | ExecutionException e) {
					log.error("Exception during creating cart for existing user, details-" + e.getMessage());
					throw new CartException(e.getMessage());
				}
				log.info("Cart created Successfully for existing customer");
			} else
				log.info("Cart already available for provided user.");
		}
		// log.info("Created cart for customer"+cart.getId());
		log.info("Adding line item to cart for user.");
		final AddLineItem action = AddLineItem.of(requestDto.getProductId(), MASTER_VARIANT_ID,
				requestDto.getQuantity());
		final CompletionStage<Cart> updatedCart = sphereClient.execute(CartUpdateCommand.of(cart, action));
		final CompletableFuture<Cart> futureCart = updatedCart.toCompletableFuture();
		try {
			if (null != futureCart.get()) {
				cart = futureCart.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			log.error("Exception during adding line item to cart, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
		log.info("Line item added successfully to cart for user.");
		return cart;
	}

	public boolean isCartAvailable(String customerId) {
		final CartByCustomerIdGet request = CartByCustomerIdGet.of(customerId);
		final CompletionStage<Cart> fetchedCart = sphereClient.execute(request);
		CompletableFuture<Cart> futureCart = fetchedCart.toCompletableFuture();
		try {
			if (null != futureCart.get()) {
				cart = futureCart.get();
				if (null != cart)
					return true;
			}
		} catch (InterruptedException | ExecutionException e) {
			log.error("Exception during checking available cart for user, details-" + e.getMessage());
			throw new CartException(e.getMessage());
		}
		return false;
	}

	public CompletableFuture<Cart> createCart(CreateCartRequest requestDto) {
		log.info("Creating cart for existing customer");
		final CartDraft cartDraft = CartDraft.of(getCurrency("EUR")).withCountry(CountryCode.DE)
				.withCustomerId(requestDto.getCustomerId()).withCustomerEmail(requestDto.getCustomerEmail())
				.withShippingAddress(requestDto.getCustomerAddress())
				.withBillingAddress(requestDto.getCustomerAddress());

		final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
		final CompletionStage<Cart> cart = sphereClient.execute(cartCreateCommand);
		return cart.toCompletableFuture();
	}

	public CompletableFuture<Cart> createCartForAnonymousUser(CreateCartRequest customer) {
		log.info("Creating cart for anonymous customer");
		final CartDraft cartDraft = CartDraft.of(getCurrency("EUR")).withCountry(CountryCode.DE);
		final CartCreateCommand cartCreateCommand = CartCreateCommand.of(cartDraft);
		final CompletionStage<Cart> cart = sphereClient.execute(cartCreateCommand);
		return cart.toCompletableFuture();
	}
	// To be used when processing checkout for anonymous user
	/*
	 * try { final CustomerSignInCommand cmd = CustomerSignInCommand
	 * .of(customer.getCustomerEmail(), customer.getPassword(),
	 * cart.toCompletableFuture().get().getId())
	 * .withAnonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART); final
	 * CompletionStage<CustomerSignInResult> result = sphereClient.execute(cmd);
	 * resultingCart = result.toCompletableFuture().get().getCart(); } catch
	 * (InterruptedException | ExecutionException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 */

	public CurrencyUnit getCurrency(String code) {
		CurrencyUnit cu = new CurrencyUnit() {

			@Override
			public int compareTo(CurrencyUnit o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getNumericCode() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getDefaultFractionDigits() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getCurrencyCode() {
				// TODO Auto-generated method stub
				return code.trim();
			}

			@Override
			public CurrencyContext getContext() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return cu;
	}
}
