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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.janilla.http.HttpExchange;
import com.janilla.json.Converter;
import com.janilla.json.Json;
import com.janilla.web.Handle;

public class CheckoutApi {

	public Properties configuration;

	@Handle(method = "POST", path = "/api/sessions")
	public Response sessions(HttpExchange exchange) throws IOException, InterruptedException {
		var ma = configuration.getProperty("adyencheckout.adyen.merchant-account");
		var a = new Amount(1000, "EUR");
		var ru = "https://your-company.com/checkout?shopperOrder=12xy..";
		var r = UUID.randomUUID().toString();
		var cc = "NL";
		var ad = Map.<String, Object>of("riskdata.skipRisk", true);
		var p = new Request(ma, a, ru, r, cc, ad);
		var q = HttpRequest.newBuilder(URI.create("https://checkout-test.adyen.com/v71/sessions"))
				.header("x-API-key", configuration.getProperty("adyencheckout.adyen.api-key"))
				.header("content-type", "application/json").POST(BodyPublishers.ofString(Json.format(p, true))).build();
		var s = HttpClient.newHttpClient().send(q, BodyHandlers.ofString());
		System.out.println(s.statusCode());
		System.out.println(s.body());
		exchange.getResponse().setStatus(com.janilla.http.HttpResponse.Status.of(s.statusCode()));
		var t = (Response) new Converter().convert(Json.parse(s.body()), Response.class);
		return t;
	}

	public record Request(String merchantAccount, Amount amount, String returnUrl, String reference,
			String countryCode, Map<String, Object> additionalData) {
	}

	public record Response(Amount amount, String countryCode, OffsetDateTime expiresAt, String id,
			String merchantAccount, String reference, String returnUrl, String sessionData) {
	}
}
