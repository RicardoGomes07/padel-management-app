import { request } from "../router.js";
import pagination from "./views/pagination.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import {createPaginationManager} from "../managers/paginationManager.js";
import auxiliaryFuns from "./auxfuns.js";
import uriManager from "../managers/uriManager.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView,
    renderCourtAvailableHoursView, renderCalendarToSearchAvailableHours, renderCreateCourtForm,
    renderSearchForCourtsByDateAndTimeSlot, renderAvailableCourtsToRent} = courtsViews
const { errorView } = errorsViews
const { isValidDate, parseHourFromString } = auxiliaryFuns
const { getClubDetailsUri, listCourtRentalsUri, getCourtAvailableHoursUri , getCourtDetailsUri, getAvailableHoursByDateUri} = uriManager

const courtsOfClubPagination =
    createPaginationManager(fetchCourtsByClub, "courts")

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const courts = await courtsOfClubPagination
        .reqParams(Number(cid))
        .resetCacheIfNeeded("cid", Number(cid))
        .getPage(
            skip,
            limit,
            (message) => { errorView(contentHeader, content, getClubDetailsUri(cid), message) }
        )

    const hasNext = courtsOfClubPagination.hasNext()

    renderCourtsByClubView(
        contentHeader,
        content,
        courts,
        cid,
        skip,
        limit,
        hasNext
    )
}

async function getCourtDetails(contentHeader, content) {
    const crid = path("crid")
    const cid = path("cid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const result = await fetchCourtDetails(cid, crid)

    if (result.status !== 200) errorView(contentHeader, content, getClubDetailsUri(cid) ,result.data)
    else renderCourtDetailsView(contentHeader, content, result.data, cid, crid, skip, limit)
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const rsp = await fetchCourtRentals(cid, crid, skip, limit+1)
    if (rsp.status !== 200){
        errorView(contentHeader, content, listCourtRentalsUri(cid, crid, skip, limit), rsp.data)
        return
    }
    const rentals = rsp.data.rentals.slice(0, limit) ?? []
    const hasNext = rsp.data.rentals.length > limit

    renderCourtRentalsView(
        contentHeader,
        content,
        rentals,
        cid,
        crid,
        skip,
        limit,
        hasNext
    )
}

async function getCourtAvailableHours(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const date = query("date")

    if (date === null) {
        const handleSubmit = async function(e){
            e.preventDefault()
            const validDate = document.querySelector("#date").value
            window.location.hash = getAvailableHoursByDateUri(cid,crid, validDate)
        }
        renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid, handleSubmit)
        return
    }

    if (!isValidDate(date)) {
        errorView(contentHeader, content, getCourtAvailableHoursUri(cid, crid), {
            title: "Invalid date",
            description: "The selected date is not valid"
        })
        return
    }

    const response = await courtsRequests.getAvailableHours(cid, crid, date)
    if (response.status === 200) {
        renderCourtAvailableHoursView(contentHeader, content, response.data.hours, cid, crid, date)
    } else {
        errorView(contentHeader, content, getCourtAvailableHoursUri(cid,crid), response.data)
    }
}

function createCourt(contentHeader, content) {
    const cid = path("cid")

    const handleSubmit = async function (e) {
        e.preventDefault()
        const courtName = e.target.querySelector("#courtName").value
        const response = await courtsRequests.createCourt(cid, courtName)
        if (response.status === 201){
            const crid = response.data.crid
            window.location.hash = getCourtDetailsUri(cid, crid)
        } else {
            errorView(contentHeader, content, getClubDetailsUri(cid), response.data)
        }
    }
    renderCreateCourtForm(contentHeader, content, cid, handleSubmit)
}

function searchCourtsToRent(contentHeader, content) {
    const cid = path("cid")

    const submitHandler = async function(e){
        e.preventDefault()
        const date = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value
        const availableCourts = await courtsRequests.getAvailableCourtsByDateAndTimeSlot(cid, date, parseHourFromString(startHour), parseHourFromString(endHour))
        if (availableCourts.status === 200) renderAvailableCourtsToRent(contentHeader, content, availableCourts.data, date, startHour, endHour)
        else errorView(contentHeader, content, getClubDetailsUri(cid), availableCourts.data)
    }
    renderSearchForCourtsByDateAndTimeSlot(contentHeader, content, cid, submitHandler)
}

const courtHandlers = {
    getCourtsByClub,
    getCourtDetails,
    getCourtRentals,
    getCourtAvailableHours,
    createCourt,
    searchCourtsToRent
}

export default courtHandlers