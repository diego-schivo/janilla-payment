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
package com.janilla.payment.checkout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.janilla.http.HeaderField;
import com.janilla.http.Http;
import com.janilla.http.HttpRequest;
import com.janilla.json.Converter;
import com.janilla.json.Json;
import com.janilla.web.Bind;
import com.janilla.web.Handle;

public class CheckoutApi {

	public Properties configuration;

	@Handle(method = "POST", path = "/api/sessions")
	public Response sessions(@Bind("type") String type, HttpRequest request) throws IOException, InterruptedException {
		var ak = configuration.getProperty("paymentcheckout.adyen.api-key");
		var ad = Map.<String, Object>of("riskdata.skipRisk", true);
		var a = new Amount(1000, "EUR");
		var cc = "NL";
		var ma = configuration.getProperty("paymentcheckout.adyen.merchant-account");
		var r = UUID.randomUUID().toString();
		String ru = "https://localhost:8443/redirect?orderRef=" + r;
		var rq = new HttpRequest();
		rq.setMethod("POST");
		rq.setTarget("/v71/sessions");
		var bb = Json.format(new Request(ad, a, cc, ma, r, ru), true).getBytes();
//		System.out.println("CheckoutApi.sessions, bb=" + new String(bb));
		rq.setHeaders(List.of(new HeaderField("accept", "*/*"), new HeaderField("x-api-key", ak),
				new HeaderField("content-type", "application/json"),
				new HeaderField("content-length", String.valueOf(bb.length))));
		rq.setBody(bb);
		var rs = Http.fetch(new InetSocketAddress("checkout-test.adyen.com", 443), rq);
		String s = new String(rs.getBody());
//		System.out.println("CheckoutApi.sessions, s=" + s);
		return (Response) new Converter().convert(Json.parse(s), Response.class);
	}

	public record Request(Map<String, Object> additionalData, Amount amount, String countryCode, String merchantAccount,
			String reference, String returnUrl) {
	}

	public record Response(Amount amount, String countryCode, OffsetDateTime expiresAt, String id,
			String merchantAccount, String reference, String returnUrl, String sessionData) {
	}
}
