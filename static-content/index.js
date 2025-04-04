import { router, request } from "./router.js";
import clubHandlers from "./handlers/clubhandlers.js";
import homeHandlers from "./handlers/home.js";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", homeHandlers.getHome)
    router.addRouteHandler("clubs", clubHandlers.getClubs)
    router.addRouteHandler("clubs/:cid", clubHandlers.getClub)
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