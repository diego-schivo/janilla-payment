/*
 * MIT License
 *
 * Copyright (c) 2024 Diego Schivo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.adyen.checkout;

import java.util.Properties;

import com.janilla.web.Handle;
import com.janilla.web.Render;

public class CheckoutWeb {

	public Properties configuration;

	@Handle(method = "GET", path = "/")
	public @Render("Checkout.html") Object index() {
		return "";
	}

	@Handle(method = "GET", path = "/preview/(\\w+)")
	public @Render("Checkout-preview.html") Object preview(String type) {
		return "";
	}

	@Handle(method = "GET", path = "/checkout/(\\w+)")
	public Foo checkout(String type) {
		return new Foo(configuration.getProperty("adyencheckout.adyen.client-key"), type);
	}

	@Handle(method = "GET", path = "/result/(\\w+)")
	public Bar result(String type) {
		return new Bar(switch (type) {
		case "success" -> "Your order has been successfully placed.";
		case "pending" -> "Your order has been received! Payment completion pending.";
		case "failed" -> "The payment was refused. Please try a different payment method or card.";
		case "error" -> "Error! Please review response in console and refer to Response handling.";
		default -> throw new IllegalArgumentException();
		});
	}

	@Render("Checkout-checkout.html")
	public record Foo(String clientKey, String type) {
	}

	@Render("Checkout-result.html")
	public record Bar(String message) {
	}
}
