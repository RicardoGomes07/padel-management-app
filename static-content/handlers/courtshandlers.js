import { request } from "../router.js";
import pagination from "./views/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import {createPaginationManager} from "../managers/paginationManager.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView } = courtsViews
const { errorView } = errorsViews

const courtsOfClubPagination =
    createPaginationManager(fetchCourtsByClub, "courts")

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const courts = await courtsOfClubPagination
        .reqParams(Number(cid))
        .filterBy("cid", Number(cid))
        .getPage(
            skip,
            limit,
            (message) => { errorView(contentHeader, content, message) }
        )

    const totalCount = courtsOfClubPagination.getTotal()

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

    const result = await fetchCourtDetails(cid, crid)

    if (result.status !== 200) errorView(contentHeader, content, result.data)
    else renderCourtDetailsView(contentHeader, content, result.data, cid, crid)
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT


    const response = await fetchCourtRentals(cid, crid, skip, limit).then(result => result.data)
    const rentals = response.rentals ?? []
    const totalCount = response.paginationInfo?.totalElements ?? 0
    renderCourtRentalsView(
        contentHeader,
        content,
        rentals,
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