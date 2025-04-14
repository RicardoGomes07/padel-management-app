import { request } from "../router.js";
import pagination from "../utils/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import routeStateManager from "../handlerStateManager.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView } = courtsViews
const { errorView } = errorsViews
const { routeState, onLinkClick, setStateValue } = routeStateManager

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchCourtsByClub(cid, skip, limit)
        if (result.status !== 200) {
            errorView(contentHeader, content, result.data)
            return
        }
        setStateValue(result.data.courts, result.data.paginationInfo.totalElements)
    }

    renderCourtsByClubView(contentHeader, content, routeState.curr, routeState.totalElements, cid, skip, limit, onLinkClick)
}

async function getCourtDetails(contentHeader, content) {
    const crid = path("crid")
    const cid = path("cid")

    const result = await fetchCourtDetails(crid)

    if (result.status !== 200) errorView(contentHeader, content, result.data)
    else renderCourtDetailsView(contentHeader, content, result.data, cid, crid)
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    if(Object.keys(routeState).length === 0 || routeState.curr.length === 0) {
        const result = await fetchCourtRentals(cid, crid)
        if (result.status !== 200) {
            errorView(contentHeader, content, result.data)
            return
        }
        setStateValue(result.data.rentals, result.data.paginationInfo.totalElements)
    }

    renderCourtRentalsView(contentHeader, content, routeState.curr, routeState.totalElements, cid, crid, skip, limit, onLinkClick)
}


const courtHandlers = {
    getCourtsByClub,
    getCourtDetails,
    getCourtRentals,
}

export default courtHandlers