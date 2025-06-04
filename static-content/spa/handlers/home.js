import clubViews from "./views/clubsviews.js";

const { clubSearchBar } = clubViews

function getHome(contentHeader, content) {
    const header = "Welcome to the Home Page"
    
    contentHeader.replaceChildren(header)
    content.replaceChildren(clubSearchBar())
}

const homeHandlers = {
    getHome
}

export default homeHandlers
