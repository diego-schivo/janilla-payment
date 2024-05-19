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

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.janilla.web.Bind;
import com.janilla.web.Handle;

public class CheckoutWeb {

	public Properties configuration;

	@Handle(method = "GET", path = "/")
	public Index index() {
		return new Index("Select type",
				List.of(Map.entry("dropin", "Drop-in"), Map.entry("card", "Card"), Map.entry("ideal", "iDEAL"),
						Map.entry("dotpay", "Dotpay"), Map.entry("giropay", "giropay"),
						Map.entry("directEbanking", "SOFORT"), Map.entry("ach", "ACH"), Map.entry("paypal", "PayPal"),
						Map.entry("alipay", "Alipay"), Map.entry("klarna_paynow", "Klarna - Pay now"),
						Map.entry("klarna", "Klarna - Pay later"), Map.entry("klarna_account", "Klarna - Slice it")));
	}

	@Handle(method = "GET", path = "/preview")
	public Preview preview(@Bind("type") String type) {
		return new Preview("Preview", type, List.of(
				new Preview.Item(URI.create("/images/sunglasses.webp"), "Sunglasses", BigDecimal.valueOf(5000, 2)),
				new Preview.Item(URI.create("/images/headphones.webp"), "Headphones", BigDecimal.valueOf(5000, 2))),
				BigDecimal.valueOf(10000, 2));
	}

	@Handle(method = "GET", path = "/checkout")
	public Checkout checkout(@Bind("type") String type) {
		var ck = configuration.getProperty("adyencheckout.adyen.client-key");
		return new Checkout("Checkout", type, ck);
	}

	@Handle(method = "GET", path = "/result/(\\w+)")
	public Result result(String type) {
		return new Result(type, type);
	}
}
