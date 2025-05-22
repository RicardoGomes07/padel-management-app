import { request } from "../router.js"
import pagination from "./views/pagination.js";
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import auxiliaryFuns from "./auxfuns.js";
import courtsRequests from "./requests/courtsrequests.js";
import courtsViews from "./views/courtsviews.js";
import uriManager from "../managers/uriManager.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT} = pagination
const { renderRentalDetailsView, renderCalendarToSearchRentals, renderUpdateRentalView } = rentalsViews
const { fetchRentalDetails,  } = rentalsRequests
const { errorView } = errorsViews
const { path, query } = request
const { isValidDate, isValidHour, parseHourFromString, getFinalRentalHours, contains} = auxiliaryFuns
const { renderRentalAvailableFinalHours, renderCourtRentalsView } = courtsViews
const { listCourtRentalsUri, getCourtAvailableHoursUri, getAvailableHoursByDateUri, getAvailableHoursByDateAndStartUri,
    getRentalDetailsUri}
    = uriManager


async function getRentalDetails(contentHeader, content) {
    const clubId = Number(path("cid"))
    const courtId = Number(path("crid"))
    const rentalId = Number(path("rid"))

    const result = await fetchRentalDetails(clubId, courtId, rentalId)

    if(result.status !== 200)
        errorView(contentHeader, content, listCourtRentalsUri(courtId, courtId, DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT) ,result.data)
    else
        renderRentalDetailsView(contentHeader, content, result.data)
}

async function createRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const date = query("date")
    const initialHour = query("start")
    const finalHour = query("end")

    const isDateValid = isValidDate(date)
    const isInitialHourValid = isValidHour(initialHour)
    const isFinalHourValid = isValidHour(finalHour)

    if (!isDateValid || !isInitialHourValid) {
        errorView(contentHeader, content, getCourtAvailableHoursUri(cid, crid),
            {title: "Invalid hours or date", description: "Invalid date or hour format "}
        )
        return
    }

    const startHour = parseHourFromString(initialHour)

    if (isFinalHourValid) {
        const endHour = parseHourFromString(finalHour)

        const rental = await rentalsRequests.createRental(cid, crid, date, startHour, endHour)

        if (rental.status === 201) {
            window.location.hash = listCourtRentalsUri(cid,crid, DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT)
        } else {
            errorView(contentHeader, content, getAvailableHoursByDateUri(cid,crid,date), rental.data)
        }
        return
    }

    const availableHours = await courtsRequests.getAvailableHours(cid, crid, date)

    if (availableHours.status !== 200) {
        errorView(contentHeader, content, getCourtAvailableHoursUri(cid,crid), availableHours.data)
        return
    }

    if (!contains(availableHours.data.hours, startHour)) {
        errorView(contentHeader, content, getAvailableHoursByDateAndStartUri(cid,crid,date,initialHour), {
            title: "Hour not available",
            description: `The selected hour is not available on ${date}`
        })
        return
    }

    const finalRentalHours = getFinalRentalHours(availableHours.data.hours, startHour)
    renderRentalAvailableFinalHours(contentHeader, content, initialHour, finalRentalHours, cid, crid, date, startHour)
}

function updateRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))

    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value
        const response = await rentalsRequests.editRental(cid, crid, rid, validDate, startHour, endHour);
        if (response.status === 200) {
            window.location.hash = getRentalDetailsUri(cid, crid, rid);
        } else {
            errorView(contentHeader, content, getRentalDetailsUri(cid,crid,rid), response.data);
        }
    }

    const response = await rentalsRequests.fetchRentalDetails(cid, crid, rid);
    if (response.status === 200) {
        renderUpdateRentalView(contentHeader, content, response.data, handleSubmit);
    } else {
        errorView(contentHeader, content, listCourtRentalsUri(cid, crid, DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT), response.data);
    }
    
}

async function deleteRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const result = await rentalsRequests.deleteRental(cid, crid, rid)

    if (result.status !== 200) {
        errorView(contentHeader, content, listCourtRentalsUri(cid,crid), result.data)
    } else {
        window.location.hash = listCourtRentalsUri(cid, crid,skip, limit)
    }
}

function searchRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value
        const rsp = await courtsRequests.fetchCourtRentalsByDate(cid, crid, skip, limit+1, validDate)
        if (rsp.status !== 200){
            errorView(contentHeader, content, listCourtRentalsUri(cid,crid), rsp.data)
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

    renderCalendarToSearchRentals(contentHeader, content, handleSubmit, cid, crid)
    
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers
