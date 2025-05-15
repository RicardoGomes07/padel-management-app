import { request } from "../router.js"
import pagination from "./views/pagination.js"
import clubsRequests from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import {createPaginationManager} from "../managers/paginationManager.js"


const { fetchClubDetails, fetchClubs } = clubsRequests
const { renderClubDetailsView, renderClubsView } = clubViews
const { errorView } = errorsViews
const { path, query } = request
const {DEFAULT_VALUE_LIMIT, DEFAULT_VALUE_SKIP} = pagination

const clubsPagination =
    createPaginationManager(fetchClubs, "clubs")

async function getClubs(contentHeader, content) {
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const clubs = await clubsPagination
        .getPage(
            skip,
            limit,
            (message) => { errorView(contentHeader, content, "#clubs" ,message) }
        )

    const hasNext = clubsPagination.hasNext()

    renderClubsView(
        contentHeader,
        content,
        clubs,
        skip,
        limit,
        hasNext
    )
}

async function getClubDetails(contentHeader, content) {
    const cid = path("cid")

    const result = await fetchClubDetails(cid)

    if( result.status !== 200) errorView(contentHeader, content, "#clubs" ,result.data)
    else renderClubDetailsView(contentHeader, content, result.data)
}

async function createClub(contentHeader, content) {
    // TODO: Implement the create club functionality
}

const clubHandlers= {
    getClubDetails,
    getClubs,
    createClub,
}

export default clubHandlers
