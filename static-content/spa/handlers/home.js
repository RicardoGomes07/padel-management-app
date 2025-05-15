import Html from "../dsl/htmlfuns.js";

const { a } = Html;

export const API_BASE_URL = "http://localhost:9000/"

function getHome(contentHeader, content) {
    const header = "Welcome to the Home Page"
    const info = a("Clubs",  "#clubs")

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}
const homeHandlers = {
    getHome
}

export default homeHandlers

