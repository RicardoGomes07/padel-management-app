import { request } from "../router.js"
import pagination from "./views/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";

const { path, query } = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const { renderUserRentalsView, renderUserDetailsView } = usersViews
const { fetchUserRentals, fetchUserDetails } = usersRequests
const { errorView } = errorsViews


async function getUserRentals(contentHeader, content) {
    const uid = path("uid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const response = await fetchUserRentals(uid, skip, limit).then(result => result.data)
    const rentals = response.rentals ?? []
    const totalCount = response.paginationInfo?.totalElements ?? 0

    renderUserRentalsView(
        contentHeader,
        content,
        rentals,
        totalCount,
        uid,
        skip,
        limit
    )
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

export default userHandlers