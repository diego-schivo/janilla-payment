export default class Checkout {

	selector;

	adyenCheckout;

	listen() {
		!this.adyenCheckout && setTimeout(async () => {
			this.adyenCheckout = await this.createAdyenCheckout();
			const e = this.selector();
			this.adyenCheckout.create(e.dataset.type).mount(e.querySelector(".payment"));
		}, 0);
	}

	async createAdyenCheckout() {
		const e = this.selector();
		const f = await fetch(`/api/sessions?type=${e.dataset.type}`, { method: "POST" });
		const j = await f.json();
		return new AdyenCheckout({
			clientKey: e.dataset.clientKey,
			locale: "en_US",
			environment: "test",
			session: j,
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
				this.handleServerResponse(result);
			},
			onError: (error, component) => {
				console.error("onError");
				console.error(error.name, error.message, error.stack, component);
				this.handleServerResponse(error);
			}
		});
	}

	handleServerResponse(response) {
		switch (response.resultCode) {
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
}
