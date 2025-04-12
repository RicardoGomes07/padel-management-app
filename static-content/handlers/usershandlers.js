import {request} from "../router.js"
import pagination from "../utils/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";
import routeStateManager from "../handlerStateManager.js";

const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const {getUserRentalsView, getUserDetailsView} = usersViews
const {fetchUserRentals, fetchUserDetails} = usersRequests
const { errorView } = errorsViews
const { routeState, prev, next, setStateValue } = routeStateManager


async function getUserRentals(contentHeader, content) {
    const userId = path("uid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchUserRentals(userId, skip, limit)
        if (result.status !== 200) {
            errorView(result.data, contentHeader, content)
            return
        }
        setStateValue(result.data.rentals, result.data.paginationInfo.totalElements)
    }

    const userRentals = {rentals: routeState.curr, paginationInfo: {totalElements: routeState.totalElements}}

    getUserRentalsView(contentHeader, content, userRentals, userId, skip, limit, (action) => {
        if (action === "next") {
            next()
        } else {
            prev()
        }
    })
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const result = await fetchUserDetails(userId)
    if (result.status !== 200) errorView(result.data, content)
    else getUserDetailsView(contentHeader, content, result.data)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers;