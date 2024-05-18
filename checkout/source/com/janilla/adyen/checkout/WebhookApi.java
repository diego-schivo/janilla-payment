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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.janilla.http.HttpExchange;
import com.janilla.http.HttpResponse;
import com.janilla.json.Converter;
import com.janilla.json.Converter.MapType;
import com.janilla.web.Bind;
import com.janilla.web.Handle;

public class WebhookApi {

	@Handle(method = "POST", path = "/api/webhooks/notifications")
	public void webhooks(@Bind(resolver = Request.Resolver.class) Request request,
			HttpExchange exchange) throws IOException {
		System.out.println(request);
		exchange.getResponse().setStatus(new HttpResponse.Status(202, "Accepted"));
	}

	public record Request(boolean live, List<Item> notificationItems) {

		public static class Resolver implements UnaryOperator<Converter.MapType> {

			@Override
			public MapType apply(MapType x) {
				return x.type() == Item.class
						? new Converter.MapType((Map<?, ?>) x.map().get("NotificationRequestItem"),
								Item.class)
						: null;
			}
		}
	}

	public record Item(Map<String, Object> additionalData, Amount amount, String eventCode,
			OffsetDateTime eventDate, String merchantAccountCode, String merchantReference, String paymentMethod,
			String pspReference, String reason, boolean success) {
	}
}
