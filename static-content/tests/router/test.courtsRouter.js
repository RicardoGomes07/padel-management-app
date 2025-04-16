import router from "../../router.js";
import courtHandlers from "../../handlers/courtshandlers.js";

describe('court_routes', function () {
    it('should find getCourtsByClub', function () {

        router.addRouteHandler("clubs/:cid/courts", courtHandlers.getCourtsByClub)
        router.addRouteHandler("clubs/:cid/courts/:crid", courtHandlers.getCourtDetails)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)

        const handler = router.getRouteHandler("clubs/:cid/courts")

        handler.name.should.be.equal("getCourtsByClub")
    })

    it('should find getCourtDetails', function () {

        router.addRouteHandler("clubs/:cid/courts", courtHandlers.getCourtsByClub)
        router.addRouteHandler("clubs/:cid/courts/:crid", courtHandlers.getCourtDetails)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)

        const handler = router.getRouteHandler("clubs/:cid/courts/:crid")

        handler.name.should.be.equal("getCourtDetails")
    })

    it('should find getCourtRentals', function () {

        router.addRouteHandler("clubs/:cid/courts", courtHandlers.getCourtsByClub)
        router.addRouteHandler("clubs/:cid/courts/:crid", courtHandlers.getCourtDetails)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)

        const handler = router.getRouteHandler("clubs/:cid/courts/:crid/rentals")

        handler.name.should.be.equal("getCourtRentals")
    })
});