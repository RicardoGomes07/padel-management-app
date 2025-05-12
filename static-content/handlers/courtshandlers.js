import { request } from "../router.js";
import pagination from "./views/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import {createPaginationManager} from "../managers/paginationManager.js";
import Html from "../utils/htmlfuns.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals, fetchCourtsAvailableHours } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView, renderCourtAvailableHoursView } = courtsViews
const { errorView } = errorsViews
const { formRequest } = Html

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
function getCourtAvailableHours(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")

    const handleSubmit = async function(e){
        e.preventDefault()
        const inputDate = document.querySelector("#date")
        const selectedDate = inputDate.value;
        if (!inputDate) {
            console.error("Input date not found")
            return
        }
        const response = await courtsRequests.getAvailableHours(cid, crid, selectedDate)
        if (response.status !== 200) {
            errorView(contentHeader, content, response.data)
        }else{
            renderCourtAvailableHoursView(
                contentHeader,
                content,
                response.data.hours,
                cid,
                crid,
                selectedDate
            )
        }
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const form = formRequest(fields, handleSubmit, {
        className: "form",
        submitText: "Get Available Hours"
    })

    contentHeader.replaceChildren("Available Hours")
    content.replaceChildren(form)
}

const courtHandlers = {
    getCourtsByClub,
    getCourtDetails,
    getCourtRentals,
    getCourtAvailableHours
}

export default courtHandlers