import { request } from "../router.js";
import pagination from "../utils/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import { createMultiPaginationManager } from "../managers/multiPaginationManager.js"

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView } = courtsViews
const { errorView } = errorsViews
const courtsOfClubPagination =
    createMultiPaginationManager(fetchCourtsByClub, "courts")
const courtRentalsPagination =
    createMultiPaginationManager(fetchCourtRentals, "rentals")

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const courts = await courtsOfClubPagination.getPage(cid, skip, limit,
        (message) => { errorView(contentHeader, content, message) }
    )

    const totalCount = courtsOfClubPagination.getTotal(cid)

    renderCourtsByClubView(
        contentHeader,
        content,
        courts,
        totalCount,
        cid,
        skip,
        limit
    )
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
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT


    const courtRentals = await courtRentalsPagination.getPage(
        crid,
        skip,
        limit,
        (message) => { errorView(contentHeader, content, message) }
    )

    const totalCount = courtRentalsPagination.getTotal(crid)

    renderCourtRentalsView(
        contentHeader,
        content,
        courtRentals,
        totalCount,
        cid,
        crid,
        skip,
        limit
    )
}


const courtHandlers = {
    getCourtsByClub,
    getCourtDetails,
    getCourtRentals,
}

export default courtHandlers