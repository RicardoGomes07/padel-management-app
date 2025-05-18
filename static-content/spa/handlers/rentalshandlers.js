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


async function getRentalDetails(contentHeader, content) {
    const clubId = Number(path("cid"))
    const courtId = Number(path("crid"))
    const rentalId = Number(path("rid"))

    const result = await fetchRentalDetails(clubId, courtId, rentalId)

    if(result.status !== 200)
        errorView(contentHeader, content, uriManager.listCourtRentals(courtId, courtId) ,result.data)
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
        errorView(contentHeader, content, uriManager.getCourtAvailableHours(cid, crid),
            {title: "Invalid hours or date", description: "Invalid date or hour format "}
        )
        return
    }

    const startHour = parseHourFromString(initialHour)

    if (isFinalHourValid) {
        const endHour = parseHourFromString(finalHour)

        const rental = await rentalsRequests.createRental(cid, crid, date, startHour, endHour)

        if (rental.status === 201) {
            window.location.hash = uriManager.listCourtRentals(cid,crid)
        } else {
            errorView(contentHeader, content, uriManager.getAvailableHoursByDate(cid,crid,date), rental.data)
        }
        return
    }

    const availableHours = await courtsRequests.getAvailableHours(cid, crid, date)

    if (availableHours.status !== 200) {
        errorView(contentHeader, content, uriManager.getCourtAvailableHours(cid,crid), availableHours.data)
        return
    }

    if (!contains(availableHours.data.hours, startHour)) {
        errorView(contentHeader, content, uriManager.getAvailableHoursByDateAndStart(cid,crid,date,initialHour), {
            title: "Hour not available",
            description: `The selected hour is not available on ${date}`
        })
        return
    }

    const finalRentalHours = getFinalRentalHours(availableHours.data.hours, startHour)
    renderRentalAvailableFinalHours(contentHeader, content, initialHour, finalRentalHours, cid, crid, date, startHour)
}

async function updateRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))
    const date = query("date")
    const startHour = query("start")
    const endHour = query("end")

    if (date!== null && startHour !== null && endHour !== null) {
        const response = await rentalsRequests.editRental(cid, crid, rid, date, startHour, endHour);

        if (response.status === 200) {
            window.location.hash = uriManager.getRentalDetails(cid, crid, rid);
        } else {
            errorView(contentHeader, content, uriManager.getRentalDetails(cid,crid,rid), response.data);
        }

    } else {
        const response = await rentalsRequests.fetchRentalDetails(cid, crid, rid);

        if (response.status === 200) {
            renderUpdateRentalView(contentHeader, content, response.data);
        } else {
            errorView(contentHeader, content, uriManager.listCourtRentals(cid, crid), response.data);
        }
    }
}

async function deleteRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))

    const result = await rentalsRequests.deleteRental(cid, crid, rid)

    if (result.status !== 200) {
        errorView(contentHeader, content, uriManager.listCourtRentals(cid,crid), result.data)
    } else {
        window.location.hash = uriManager.listCourtRentals(cid, crid)
    }
}

async function searchRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const date = query("date")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    if(date == null){
        renderCalendarToSearchRentals(contentHeader, content, date, cid, crid)
    }else{
        const rsp = await courtsRequests.fetchCourtRentalsByDate(cid, crid, skip, limit+1, date)
        if (rsp.status !== 200){
            errorView(contentHeader, content, uriManager.listCourtRentals(cid,crid), rsp.data)
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
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers
