import {request} from "../router.js"
import pagination from "../utils/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview";

const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const {getUserRentalsView, getUserDetailsView} = usersViews
const {fetchUserRentals, fetchUserDetails} = usersRequests
const { errorView } = errorsViews

async function getUserRentals(contentHeader, content) {
    const userId = path("uid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    const rentalsResponse = await fetchUserRentals(content, skip, limit)
    if (rentalsResponse.status !== 200) errorView(rentalsResponse.data, content)
    else getUserRentalsView(contentHeader, content, rentalsResponse, userId, skip, limit)
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const user = await fetchUserDetails(userId)
    if (user.status !== 200) errorView(user.data, content)
    else getUserDetailsView(contentHeader, content, user)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers;