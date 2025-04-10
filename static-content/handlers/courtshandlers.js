import {request} from "../router.js";
import pagination from "../utils/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview"

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const {path, query} = request
const {fetchCourtsByClubId, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const {courtsView, courtDetailsView, courtRentalsView} = courtsViews
const { errorView } = errorsViews

async function getCourtsByClub(mainContent) {
    const cid = path("cid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    const courts = await fetchCourtsByClubId(cid, skip, limit)

    if(courts.status !== 200) errorView(courts.data, mainContent)
    else courtsView(courts, cid, skip, limit, mainContent)
}

async function getCourt(mainContent) {
    const crid = path("crid")
    const cid = path("cid")

    const courtResponse = await fetchCourtDetails(crid)

    if (courtResponse.status !== 200) errorView(courtResponse.data, mainContent)
    else courtDetailsView(courtResponse, cid, crid,mainContent)
}

async function getCourtRentals(mainContent) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    const courtRentals = await fetchCourtRentals(cid, crid)

    if (courtRentals.status !== 200) errorView(courtRentals.data, mainContent)
    else courtRentalsView(courtRentals, cid, skip, limit, mainContent)
}


const courtHandlers = {
    getCourtsByClub,
    getCourt,
    getCourtRentals,
}

export default courtHandlers