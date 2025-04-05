import Html from "../utils/htmlfuns.js";

const { a, h1 } = Html;
export const API_BASE_URL = "http://localhost:9000/"

function getHome(mainContent) {
    const title = h1("Welcome to the Home Page")
    const clubs = a("Clubs", "#clubs")
    mainContent.replaceChildren(title, clubs)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

