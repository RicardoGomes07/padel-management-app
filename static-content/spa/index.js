import { router, request } from "./router.js";
import clubHandlers from "./handlers/clubshandlers.js";
import homeHandlers from "./handlers/home.js";
import userHandlers from "./handlers/usershandlers.js";
import courtHandlers from "./handlers/courtshandlers.js";
import rentalHandlers from "./handlers/rentalshandlers.js";
import {userAuthenticationContext} from "./managers/userAuthenticationContext.js";
import errorManager from "./managers/errorManager.js";


window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", homeHandlers.getHome)
    router.addRouteHandler("users/create", userHandlers.signUp)
    router.addRouteHandler("users/login", userHandlers.login)
    router.addRouteHandler("users/:uid", userHandlers.getUserDetails)
    router.addRouteHandler("users/:uid/rentals", userHandlers.getUserRentals)
    router.addRouteHandler("clubs", clubHandlers.getClubs)
    router.addRouteHandler("clubs/create", clubHandlers.createClub)
    router.addRouteHandler("clubs/:cid", clubHandlers.getClubDetails)
    router.addRouteHandler("clubs/:cid/courts", courtHandlers.getCourtsByClub)
    router.addRouteHandler("clubs/:cid/courts/create", courtHandlers.createCourt)
    router.addRouteHandler("clubs/:cid/courts/rent", courtHandlers.searchCourtsToRent)
    router.addRouteHandler("clubs/:cid/courts/:crid", courtHandlers.getCourtDetails)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals", courtHandlers.getCourtRentals)
    router.addRouteHandler("clubs/:cid/courts/:crid/available_hours", courtHandlers.getCourtAvailableHours)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals/create", rentalHandlers.createRental)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals/search", rentalHandlers.searchRentals)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid", rentalHandlers.getRentalDetails)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/update", rentalHandlers.updateRental)
    router.addRouteHandler("clubs/:cid/courts/:crid/rentals/:rid/delete", rentalHandlers.deleteRental)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
}

function hashChangeHandler(){

    const contentHeader = document.getElementById("contentHeader")
    const content = document.getElementById("content")
    const authContent = document.getElementById("authContent")
    const path =  window.location.hash.replace("#", "")
    const handler = router.getRouteHandler(path)

    if (userAuthenticationContext.userChanged()) userAuthenticationContext.updateState(authContent)

    if (errorManager.hasError()) errorManager.render()
    else errorManager.clear()

    request.cleanArgs() // Clean the previous argument values that can have the same name as skip and limit
    request.getRequestArgs(handler,path)

    handler(contentHeader, content)
}