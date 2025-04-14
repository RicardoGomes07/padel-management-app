import { request } from "../router.js"
import pagination from "../utils/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";
import routeStateManager from "../handlerStateManager.js";

const { path, query } = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const { renderUserRentalsView, renderUserDetailsView } = usersViews
const { fetchUserRentals, fetchUserDetails } = usersRequests
const { errorView } = errorsViews
const { routeState, onLinkClick, setStateValue } = routeStateManager


async function getUserRentals(contentHeader, content) {
    const uid = path("uid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchUserRentals(uid, skip, limit)
        if (result.status !== 200) {
            errorView(contentHeader, content, result.data)
            return
        }
        setStateValue(result.data.rentals, result.data.paginationInfo.totalElements)
    }

    renderUserRentalsView(contentHeader, content, routeState.curr, routeState.totalElements, uid, skip, limit, onLinkClick)
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const result = await fetchUserDetails(userId)

    if (result.status !== 200) errorView(content, result.data)
    else renderUserDetailsView(contentHeader, content, result.data)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers;