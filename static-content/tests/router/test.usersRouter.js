import router from "../../spa/router.js";
import usershandlers from "../../spa/handlers/usershandlers.js";

  describe('user_routes', function () {

      it('should find getUserDetails', function () {

           router.addRouteHandler("users/:uid", usershandlers.getUserDetails)
           router.addRouteHandler("users/:uid/rentals", usershandlers.getUserRentals)

           const handler = router.getRouteHandler("users/:uid")

           handler.name.should.be.equal("getUserDetails")
        })

        it('should find getUserRentals', function () {

            router.addRouteHandler("users/:uid", usershandlers.getUserDetails)
            router.addRouteHandler("users/:uid/rentals", usershandlers.getUserRentals)

            const handler = router.getRouteHandler("users/:uid/rentals")

            handler.name.should.be.equal("getUserRentals")
        })
  });