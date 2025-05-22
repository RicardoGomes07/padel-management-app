import clubViews from "./views/clubsviews.js";
import pagination from "./views/pagination.js";

const { clubSearchBar } = clubViews
const { DEFAULT_VALUE_LIMIT, DEFAULT_VALUE_SKIP} = pagination

function getHome(contentHeader, content) {
    const header = "Welcome to the Home Page"

    contentHeader.replaceChildren(header)
    content.replaceChildren(clubSearchBar(DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT))
}

const homeHandlers = {
    getHome
}

export default homeHandlers
