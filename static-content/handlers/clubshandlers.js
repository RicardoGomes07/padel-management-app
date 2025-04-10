import { request } from "../router.js";
import pagination from "../utils/pagination.js";
import clubfetchers from "./requests/clubsrequests.js";
import clubviews from "./views/getclubview.js";

const { fetchClub, fetchClubs } = clubfetchers;
const { renderClubView, renderClubsView } = clubviews;

const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination

async function getClubs(mainContent) {
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT
    const clubs = await fetchClubs(skip, limit)
    renderClubsView(mainContent, clubs)
}

async function getClub(mainContent) {
    const cid = path("cid")
    const club = await fetchClub(cid)
    renderClubView(mainContent, club)
}

const clubHandlers= {
    getClub,
    getClubs,
}

export default clubHandlers
