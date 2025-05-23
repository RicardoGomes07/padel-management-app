import { request } from "../router.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import {createPaginationManager} from "../managers/paginationManager.js";
import auxiliaryFuns from "./auxfuns.js";
import uriManager from "../managers/uriManager.js";
import {ELEMS_PER_PAGE} from "./views/pagination.js";

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
    const page = Number(query("page")) || 1

    const [courts, count] = await courtsOfClubPagination
        .reqParams(Number(cid))
        .resetCacheIfNeeded("cid", Number(cid))
        .getPage(
            page,
            (message) => { errorView(contentHeader, content, getClubDetailsUri(cid), message) }
        )

    renderCourtsByClubView(
        contentHeader,
        content,
        courts,
        count,
        cid,
        page,
    )
}

async function getCourtDetails(contentHeader, content) {
    const crid = path("crid")
    const cid = path("cid")
    const page = Number(query("page")) || 1

    const result = await fetchCourtDetails(cid, crid)

    if (result.status !== 200) errorView(contentHeader, content, getClubDetailsUri(cid) ,result.data)
    else renderCourtDetailsView(contentHeader, content, result.data, cid, crid, page)
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const page = Number(query("page")) || 1

    const skip = (page - 1) * ELEMS_PER_PAGE

    const rsp = await fetchCourtRentals(cid, crid, skip, ELEMS_PER_PAGE)
    if (rsp.status !== 200){
        errorView(contentHeader, content, listCourtRentalsUri(cid, crid), rsp.data.items)
        return
    }

    const rentals = rsp.data.items.rentals.slice(0, ELEMS_PER_PAGE) ?? []
    const count = rsp.data.count

    renderCourtRentalsView(
        contentHeader,
        content,
        rentals,
        count,
        cid,
        crid,
        page,
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
        const availableCourtsRsp =
            await courtsRequests.getAvailableCourtsByDateAndTimeSlot(
                cid, date, parseHourFromString(startHour), parseHourFromString(endHour)
            )
        const availableCourts = availableCourtsRsp.data.items.courts

        if (availableCourtsRsp.status === 200) renderAvailableCourtsToRent(contentHeader, content, availableCourts, date, startHour, endHour)
        else errorView(contentHeader, content, getClubDetailsUri(cid), availableCourts)
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