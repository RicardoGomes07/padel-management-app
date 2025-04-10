import {request} from "../router.js";
import pagination from "../utils/pagination.js";
import usersViews from "./views/usersviews.js";
import usersRequests from "./requests/usersrequests.js";

const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT } = pagination
const {getUserRentalsView, getUserDetailsView} = usersViews
const {fetchUserRentals, fetchUserDetails} = usersRequests;

async function getUserRentals(contentHeader, content) {
    const userId = path("uid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    const rentalsResponse = await fetchUserRentals(content, skip, limit)
    getUserRentalsView(contentHeader, content, rentalsResponse, userId, skip, limit)
}

async function getUserDetails(contentHeader, content){
    const userId = path("uid")

    const user = await fetchUserDetails(userId)
    getUserDetailsView(contentHeader, content, user)
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers;