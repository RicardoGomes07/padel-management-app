import { request } from "../router.js"
import pagination from "../utils/pagination.js"
import clubFetchers from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview"

const { fetchClub, fetchClubs } = clubFetchers
const { renderClubView, renderClubsView } = clubViews
const { errorView } = errorsViews

const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination

async function getClubs(mainContent) {
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    const clubs = await fetchClubs(skip, limit)

    if (clubs.status !== 200) errorView(clubs.data, mainContent)
    else renderClubsView(mainContent, clubs)
}

async function getClub(mainContent) {
    const cid = path("cid")

    const club = await fetchClub(cid)

    if( club.status !== 200) errorView(club.data, mainContent)
    else renderClubView(mainContent, club)
}

const clubHandlers= {
    getClub,
    getClubs,
}

export default clubHandlers
