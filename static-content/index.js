import router from "./router.js";

import clubhandlers from "./handlers/clubhandlers";
import homeHandlers from "./handlers/home";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)

function loadHandler(){
    router.addRouteHandler("home", homeHandlers.getHome)
    router.addRouteHandler("clubs", clubhandlers.getClubs)
    router.addRouteHandler("clubs/:id?skip=&limit=", clubhandlers.getClub)
    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "home")

    hashChangeHandler()
}

function hashChangeHandler(){

    const mainContent = document.getElementById("mainContent")
    const path =  window.location.hash.replace("#", "")

    const handler = router.getRouteHandler(path)
    const values =
    handler(mainContent, values)
}