import router from "../../router.js";
import clubHandlers from "../../handlers/clubshandlers.js";

describe('club_routes', function () {
    it('should find getClubs', function () {

        router.addRouteHandler("clubs", clubHandlers.getClubs)
        router.addRouteHandler("clubs/:cid", clubHandlers.getClubDetails)

        const handler = router.getRouteHandler("clubs")

        handler.name.should.be.equal("getClubs")
    })

    it('should find getClubDetails', function () {

        router.addRouteHandler("clubs", clubHandlers.getClubs)
        router.addRouteHandler("clubs/:cid", clubHandlers.getClubDetails)

        const handler = router.getRouteHandler("clubs/:cid")

        handler.name.should.be.equal("getClubDetails")
    })
});