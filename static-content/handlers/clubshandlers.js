import { request } from "../router.js"
import pagination from "../utils/pagination.js"
import clubFetchers from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import routeStateManager from "../handlerStateManager.js";

const { fetchClub, fetchClubs } = clubFetchers
const { renderClubView, renderClubsView } = clubViews
const { errorView } = errorsViews
const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const { routeState, prev, next, setStateValue } = routeStateManager

async function getClubs(contentHeader, content) {
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchClubs(skip, limit)
        if (result.status !== 200) {
            errorView(result.data, contentHeader, content)
            return
        }
        setStateValue(result.data.clubs, result.data.paginationInfo.totalElements)
    }

    const clubs = {clubs: routeState.curr, paginationInfo: {totalElements: routeState.totalElements}}

    renderClubsView(contentHeader, content, clubs, skip, limit, (action) => {
        if (action === "next") {
            next()
        } else {
            prev()
        }
    })
}

async function getClub(contentHeader, content) {
    const cid = path("cid")

    const result = await fetchClub(cid)
    if( result.status !== 200) errorView(result.data, contentHeader, content)
    else renderClubView(contentHeader, content, result.data)
}

const clubHandlers= {
    getClub,
    getClubs,
}

export default clubHandlers
