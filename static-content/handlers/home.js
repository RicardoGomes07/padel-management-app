export const API_BASE_URL = "http://localhost:8080/"

import Html from "../utils/htmlfuns.js";
const { div, a, ul, li, h1, h2 } = Html;

function getHome(mainContent) {
    const title = h1("Welcome to the Home Page")
    const clubs = a("Clubs", "#clubs")
    mainContent.replaceChildren(title, clubs)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

