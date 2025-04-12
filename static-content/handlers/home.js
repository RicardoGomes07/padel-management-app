import Html from "../utils/htmlfuns.js";

const { a } = Html;
export const API_BASE_URL = "http://localhost:9000/"

function getHome(contentHeader, content) {
    const title = "Welcome to the Home Page"
    const clubs = a("Clubs", "#clubs")

    contentHeader.replaceChildren(title)
    content.replaceChildren(clubs)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

