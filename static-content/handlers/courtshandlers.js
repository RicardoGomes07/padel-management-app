import {request} from "../router.js";
import pagination from "../utils/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import routeStateManager from "../handlerStateManager.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const {path, query} = request
const {fetchCourtsByClubId, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const {courtsView, courtDetailsView, courtRentalsView} = courtsViews
const { errorView } = errorsViews
const { routeState, prev, next, setStateValue } = routeStateManager

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchCourtsByClubId(cid, skip, limit)
        if (result.status !== 200) {
            errorView(result.data, contentHeader, content)
            return
        }
        setStateValue(result.data.courts, result.data.paginationInfo.totalElements)
    }

    const courts = {courts: routeState.curr, paginationInfo: {totalElements: routeState.totalElements}}

    courtsView(courts, cid, skip, limit, contentHeader, content, (action) => {
        if (action === "next") {
            next()
        } else {
            prev()
        }
    })
}

async function getCourt(contentHeader, content) {
    const crid = path("crid")
    const cid = path("cid")

    const result = await fetchCourtDetails(crid)

    if (result.status !== 200) errorView(result.data, contentHeader, content)
    else courtDetailsView(result.data, cid, crid, contentHeader, content)
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchCourtRentals(cid, crid)
        if (result.status !== 200) {
            errorView(result.data, contentHeader, content)
            return
        }
        setStateValue(result.data.rentals, result.data.paginationInfo.totalElements)
    }

    const courtRentals = {rentals: routeState.curr, paginationInfo: {totalElements: routeState.totalElements}}

    courtRentalsView(courtRentals, cid, crid, skip, limit, contentHeader, content, (action) => {
        if (action === "next") {
            next()
        } else {
            prev()
        }
    })
}


const courtHandlers = {
    getCourtsByClub,
    getCourt,
    getCourtRentals,
}

export default courtHandlers