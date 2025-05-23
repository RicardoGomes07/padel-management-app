import {router, request} from "../../spa/router.js";
import clubHandlers from "../../spa/handlers/clubshandlers.js";
import rentalHandlers from "../../spa/handlers/rentalshandlers.js";
import courtHandlers from "../../spa/handlers/courtshandlers.js";
import userHandlers from "../../spa/handlers/usershandlers.js";
import homeHandlers from "../../spa/handlers/home.js";
const assert = window.chai.assert;

describe("Router", function () {
    it("should add and retrieve route handlers correctly", function () {
        router.addRouteHandler("home", homeHandlers.getHome)
        router.addRouteHandler("users/:uid", userHandlers.getUserDetails)
        router.addRouteHandler("users/:uid/rentals", userHandlers.getUserRentals)
        router.addRouteHandler("clubs", clubHandlers.getClubs)
        router.addRouteHandler("clubs/:cid", clubHandlers.getClubDetails)
        router.addRouteHandler("clubs/:cid/courts", courtHandlers.getCourtsByClub)
        router.addRouteHandler("clubs/:cid/courts/create", courtHandlers.createCourt)
        router.addRouteHandler("clubs/:cid/courts/:crid", courtHandlers.getCourtDetails)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)
        router.addRouteHandler("clubs/:cid/courts/:crid/available_hours", courtHandlers.getCourtAvailableHours)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals/create", rentalHandlers.createRental)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals/search", rentalHandlers.searchRentals)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid", rentalHandlers.getRentalDetails)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/update", rentalHandlers.updateRental)
        router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/delete", rentalHandlers.deleteRental)

        const handlerHome = router.getRouteHandler("home");
        assert.strictEqual(handlerHome.name, "getHome");

        const handlerUser = router.getRouteHandler("users/:uid");
        assert.strictEqual(handlerUser.name, "getUserDetails");

        const handlerUserRentals = router.getRouteHandler("users/:uid/rentals");
        assert.strictEqual(handlerUserRentals.name, "getUserRentals");

        const handlerClubs = router.getRouteHandler("clubs");
        assert.strictEqual(handlerClubs.name, "getClubs");

        const handlerClubDetails = router.getRouteHandler("clubs/:cid");
        assert.strictEqual(handlerClubDetails.name, "getClubDetails");

        const handlerCourtsByClub = router.getRouteHandler("clubs/:cid/courts");
        assert.strictEqual(handlerCourtsByClub.name, "getCourtsByClub");

        const handlerCreateCourt = router.getRouteHandler("clubs/:cid/courts/create");
        assert.strictEqual(handlerCreateCourt.name, "createCourt");

        const handlerCourtDetails = router.getRouteHandler("clubs/:cid/courts/:crid");
        assert.strictEqual(handlerCourtDetails.name, "getCourtDetails");

        const handlerCourtRentals = router.getRouteHandler("clubs/:cid/courts/:crid/rentals");
        assert.strictEqual(handlerCourtRentals.name, "getCourtRentals");

        const handlerCourtAvailableHours = router.getRouteHandler("clubs/:cid/courts/:crid/available_hours");
        assert.strictEqual(handlerCourtAvailableHours.name, "getCourtAvailableHours");

        const handlerCreateRental = router.getRouteHandler("clubs/:cid/courts/:crid/rentals/create");
        assert.strictEqual(handlerCreateRental.name, "createRental");

        const handlerSearchRentals = router.getRouteHandler("clubs/:cid/courts/:crid/rentals/search");
        assert.strictEqual(handlerSearchRentals.name, "searchRentals");

        const handlerRentalDetails = router.getRouteHandler("clubs/:cid/courts/:crid/rentals/:rid");
        assert.strictEqual(handlerRentalDetails.name, "getRentalDetails");

        const handlerUpdateRental = router.getRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/update");
        assert.strictEqual(handlerUpdateRental.name, "updateRental");

        const handlerDeleteRental = router.getRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/delete");
        assert.strictEqual(handlerDeleteRental.name, "deleteRental");
    });

    it("should get the correct path arguments for a route", function () {
        const routeTemplate = "clubs/:cid/courts/:crid/rentals/:rid";
        const route = "clubs/123/courts/456/rentals/789";
        const routeHandler = router.getRouteHandler(routeTemplate);
        const args = request.getRequestArgs(routeHandler, route);
        
        assert.deepEqual(args.path, { cid: "123", crid: "456", rid: "789" });
        assert.deepEqual(args.query, {});
        request.cleanArgs();
    });

    it("should get the correct query arguments for a route", function () {
        const routeTemplate = "clubs/:cid/courts/:crid/rentals";
        const route = "clubs/123/courts/456/rentals?skip=0&limit=10";
        const routeHandler = router.getRouteHandler(routeTemplate);
        const args = request.getRequestArgs(routeHandler, route);
        console.log(args);
        
        assert.deepEqual(args.path, { cid: "123", crid: "456" });
        assert.deepEqual(args.query, { skip: "0", limit: "10" });
    });
});