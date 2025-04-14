import { request } from "../router.js"
import pagination from "../utils/pagination.js"
import clubsRequests from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import routeStateManager from "../handlerStateManager.js";

const { fetchClubDetails, fetchClubs } = clubsRequests
const { renderClubDetailsView, renderClubsView } = clubViews
const { errorView } = errorsViews
const { path, query } = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const { routeState, onLinkClick, setStateValue } = routeStateManager

async function getClubs(contentHeader, content) {
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchClubs(skip, limit)
        if (result.status !== 200) {
            errorView(contentHeader, content, result.data)
            return
        }
        setStateValue(result.data.clubs, result.data.paginationInfo.totalElements)
    }

    renderClubsView(contentHeader, content, routeState.curr, routeState.totalElements, skip, limit, onLinkClick)
}

async function getClubDetails(contentHeader, content) {
    const cid = path("cid")

    const result = await fetchClubDetails(cid)

    if( result.status !== 200) errorView(contentHeader, content, result.data)
    else renderClubDetailsView(contentHeader, content, result.data)
}

const clubHandlers= {
    getClubDetails,
    getClubs,
}

export default clubHandlers
