import { request } from "../router.js"
import pagination from "./views/pagination.js"
import usersViews from "./views/usersviews.js"
import usersRequests from "./requests/usersrequests.js"
import errorsViews from "./views/errorsview.js";
import userAuthenticationManager from "../managers/userAuthenticationManager.js";

const { path, query } = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const { renderUserRentalsView, renderUserDetailsView } = usersViews
const { fetchUserRentals, fetchUserDetails } = usersRequests
const { errorView } = errorsViews

export const userAuthManager = userAuthenticationManager()
                                .setCurrToken("b734312a-94c6-492e-a243-5ebe17e023ca")

async function getUserRentals(contentHeader, content) {
    const uid = path("uid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const rsp = await fetchUserRentals(uid, skip, limit+1).then(result => result.data)
    const rentals = rsp.rentals.slice(0, limit) ?? []
    const hasNext = rsp.rentals.length > limit
    const userName = await fetchUserDetails(Number(uid)).then(user => user.data.name)

    renderUserRentalsView(
        contentHeader,
        content,
        rentals,
        userName,
        uid,
        skip,
        limit,
        hasNext
    )
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const result = await fetchUserDetails(userId)

    if (result.status !== 200) errorView(contentHeader, content, `#home`,result.data)
    else renderUserDetailsView(contentHeader, content, result.data)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers