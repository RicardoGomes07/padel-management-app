import Html from "../dsl/htmlfuns.js";
import uriManager from "../managers/uriManager.js";

const { a } = Html;



function getHome(contentHeader, content) {
    const header = "Welcome to the Home Page"
    const info = a("Clubs",  uriManager.listClubs())

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

