export const API_BASE_URL = "http://localhost:8080/"

import Html from "../utils/htmlfuns.js";
const { div, a, ul, li, h1, h2 } = Html;

function getHome(mainContent) {
    h1("Welcome to the Home Page")
    a("Go to Clubs", "#clubs")
    mainContent.replaceChildren(h1)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

