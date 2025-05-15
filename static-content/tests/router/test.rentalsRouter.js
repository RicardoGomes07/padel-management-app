import router from "../../spa/router.js";
import rentalHandlers from "../../spa/handlers/rentalshandlers.js";

describe('rental_routes', function () {
    it('should find getRentalDetails', function () {

        router.addRouteHandler("rentals/:rid", rentalHandlers.getRentalDetails)

        const handler = router.getRouteHandler("rentals/:rid")

        handler.name.should.be.equal("getRentalDetails")
    })
});