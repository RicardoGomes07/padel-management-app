import { router, request } from "./router.js";
import clubHandlers from "./handlers/clubhandlers.js";
import homeHandlers from "./handlers/home.js";
import userHandlers from "./handlers/usershandlers.js";
import courtHandlers from "./handlers/courtshandlers.js";
import rentalHandlers from "./handlers/rentalhandlers.js";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", homeHandlers.getHome)
    router.addRouteHandler("clubs", clubHandlers.getClubs)
    router.addRouteHandler("clubs/:cid", clubHandlers.getClub)
    router.addRouteHandler("users/:uid", userHandlers.getUserDetails)
    router.addRouteHandler("users/:uid/rentals", userHandlers.getUserRentals)
    router.addRouteHandler("courts/:crid", courtHandlers.getCourt)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)
    router.addRouteHandler("courts/clubs/:cid", courtHandlers.getCourtsByClub)
    router.addRouteHandler("rentals/:rid", rentalHandlers.getRentalDetails)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
}

function hashChangeHandler(){

    const mainContent = document.getElementById("mainContent")
    const path =  window.location.hash.replace("#", "")

    const handler = router.getRouteHandler(path)
    request.getRequestArgs(handler,path)

    handler(mainContent) //handler excusa de receber main content e apenas o estritamente necessario
}