import { request } from "../router.js";
import courtsRequests  from "./requests/courtsrequests.js"
import courtsViews from "./views/courtsviews.js"
import errorsViews from "./views/errorsview.js"
import { createPaginationManager } from "../managers/paginationManager.js";
import auxiliaryFuns from "./auxFuns.js";
import uriManager from "../managers/uriManager.js";
import { ELEMS_PER_PAGE } from "./views/pagination.js";
import { authenticated } from "../managers/userAuthenticationManager.js";
import errorManager from "../managers/errorManager.js";
import { redirectTo } from "../router.js";

const { path, query } = request
const { fetchCourtsByClub, fetchCourtDetails, fetchCourtRentals } = courtsRequests
const { renderCourtsByClubView, renderCourtDetailsView, renderCourtRentalsView,
    renderCourtAvailableHoursView, renderCalendarToSearchAvailableHours, renderCreateCourtForm,
    renderSearchForCourtsByDateAndTimeSlot, renderAvailableCourtsToRent } = courtsViews
const { errorView } = errorsViews
const { isValidDate, parseHourFromString } = auxiliaryFuns
const { getCourtDetailsUri, getAvailableHoursByDateUri, loginUri, listClubCourtsUri } = uriManager

const courtsOfClubPagination = createPaginationManager(fetchCourtsByClub, "courts")

async function getCourtsByClub(contentHeader, content) {
    const cid = path("cid")
    const page = Number(query("page")) || 1

    const [courts, count] = await courtsOfClubPagination
        .reqParams(Number(cid))
        .resetCacheIfNeeded("cid", Number(cid))
        .getPage(
            page,
            (message) => { errorManager.store(errorView(message)).render() }
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

    if (result.status === 200){
        renderCourtDetailsView(contentHeader, content, result.data, cid, crid, page)
    } else {
        errorManager.store(errorView(result.data))
        redirectTo(listClubCourtsUri(cid))
    }
}

async function getCourtRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const page = Number(query("page")) || 1

    const skip = (page - 1) * ELEMS_PER_PAGE

    const rsp = await fetchCourtRentals(cid, crid, skip, ELEMS_PER_PAGE)
    if (rsp.status !== 200){
        errorManager.store(errorView(rsp.data))
        redirectTo(getCourtDetailsUri(cid, crid))
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
    const range = query("range")

    const handleSubmit = (e) => {
        e.preventDefault()
        const dateValue = e.target.querySelector("#date").value
        redirectTo(getAvailableHoursByDateUri(cid, crid, dateValue))
    }

    const showCalendar = (error) => {
        if (error) errorManager.store(errorView(error)).render()
        renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid, handleSubmit)
    }

    if (!date) {
        showCalendar()
        return
    }

    if (!isValidDate(date)) {
        showCalendar({ title: "Invalid date", description: "The selected date is not valid" })
        return
    }

    try {
        const response = await courtsRequests.getAvailableHours(cid, crid, date)
        if (response.status !== 200) {
            showCalendar(response.data)
            return
        }

        renderCourtAvailableHoursView(
            contentHeader,
            content,
            response.data.hours,
            cid,
            crid,
            date,
            range
        )
    } catch {
        showCalendar({ title: "Error", description: "Could not fetch available hours." })
    }
}

function createCourt(contentHeader, content) {
    const cid = path("cid")
    if (!authenticated()){
        redirectTo(loginUri())
        return
    }

    const handleSubmit = async function (e) {
        e.preventDefault()
        const courtName = e.target.querySelector("#courtName").value
        const response = await courtsRequests.createCourt(cid, courtName)
        if (response.status === 201){
            const crid = response.data.crid
            redirectTo(getCourtDetailsUri(cid, crid))
        } else {
            errorManager.store(errorView(response.data)).render()
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

        if (availableCourtsRsp.status === 200) renderAvailableCourtsToRent(contentHeader, content, availableCourts, cid, date, startHour, endHour)
        else errorManager.store(errorView(availableCourtsRsp.data)).render()
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