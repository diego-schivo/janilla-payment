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
addEventListener("DOMContentLoaded", async () => {
	const type = document.getElementById("type")?.textContent;
	if (!type)
		return;
	const s = await callServer("/api/sessions");
	const c = await createAdyenCheckout(s);
	c.create(type).mount(document.getElementById("payment"));
});

async function callServer(url) {
	const s = await fetch(url, { method: "POST" });
	return await s.json();
}

async function createAdyenCheckout(session) {
	const clientKey = document.getElementById("clientKey").textContent;
	return new AdyenCheckout({
		clientKey,
		locale: "en_US",
		environment: "test",
		session,
		showPayButton: true,
		paymentMethodsConfiguration: {
			ideal: {
				showImage: true
			},
			card: {
				hasHolderName: true,
				holderNameRequired: true,
				name: "Credit or debit card",
				amount: {
					value: 10000,
					currency: "EUR"
				}
			},
			paypal: {
				amount: {
					value: 10000,
					currency: "USD"
				},
				environment: "test",
				countryCode: "US"
			}
		},
		onPaymentCompleted: (result, component) => {
			console.info("onPaymentCompleted");
			console.info(result, component);
			handleServerResponse(result);
		},
		onError: (error, component) => {
			console.error("onError");
			console.error(error.name, error.message, error.stack, component);
			handleServerResponse(error);
		}
	});
}

function handleServerResponse(res) {
	switch (res.resultCode) {
		case "Authorised":
			location.href = "/result/success";
			break;
		case "Pending":
		case "Received":
			location.href = "/result/pending";
			break;
		case "Refused":
			location.href = "/result/failed";
			break;
		default:
			location.href = "/result/error";
			break;
	}
}
