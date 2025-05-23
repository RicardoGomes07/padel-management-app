import { request } from "../router.js"
import {ELEMS_PER_PAGE} from "./views/pagination.js";
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import courtsRequests from "./requests/courtsrequests.js";
import courtsViews from "./views/courtsviews.js";
import uriManager from "../managers/uriManager.js";
import auxfuns from "../handlers/auxfuns.js";

const { renderRentalDetailsView, renderCalendarToSearchRentals, renderUpdateRentalView, renderRentalCreationForm } = rentalsViews
const { fetchRentalDetails} = rentalsRequests
const { errorView } = errorsViews
const { path, query } = request
const { renderCourtRentalsView } = courtsViews
const { listCourtRentalsUri, getRentalDetailsUri, getCourtDetailsUri} = uriManager
const { parseHourFromString } = auxfuns


async function getRentalDetails(contentHeader, content) {
    const clubId = Number(path("cid"))
    const courtId = Number(path("crid"))
    const rentalId = Number(path("rid"))

    const result = await fetchRentalDetails(clubId, courtId, rentalId)

    if(result.status !== 200)
        errorView(contentHeader, content, listCourtRentalsUri(courtId, courtId) ,result.data)
    else
        renderRentalDetailsView(contentHeader, content, result.data)
}

async function createRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))

    const date = query("date")
    const startHour = query("start")
    const endHour = query("end")

    const rentalInfo = {date: null, startHour: null, endHour: null}
    if(date != null && startHour != null && endHour != null){
        rentalInfo.date = date
        rentalInfo.startHour = startHour
        rentalInfo.endHour = endHour
    }

    const onSubmit = async function(e){
        e.preventDefault()
        const date = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value
        const response = await rentalsRequests.createRental(cid, crid, date, parseHourFromString(startHour), parseHourFromString(endHour))

        if (response.status === 201) {
            window.location.hash = getRentalDetailsUri(cid, crid, response.data.rid)
        } else {
            errorView(contentHeader, content, getCourtDetailsUri(cid, crid), response.data)
        }
    }

    renderRentalCreationForm(contentHeader, content, cid, crid, rentalInfo, onSubmit)

}

async function updateRental(contentHeader, content) {
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
        errorView(contentHeader, content, listCourtRentalsUri(cid, crid), response.data);
    }

}

async function deleteRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))
    const page = Number(query("page")) || 1

    const result = await rentalsRequests.deleteRental(cid, crid, rid)

    if (result.status !== 200) {
        errorView(contentHeader, content, listCourtRentalsUri(cid,crid), result.data)
    } else {
        window.location.hash = listCourtRentalsUri(cid, crid, page)
    }
}

function searchRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")

    const page = Number(query("page")) || 1
    const skip = (page - 1) * ELEMS_PER_PAGE

    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value

        const rsp = await courtsRequests.fetchCourtRentalsByDate(cid, crid, skip, ELEMS_PER_PAGE, validDate)
        if (rsp.status !== 200){
            errorView(contentHeader, content, listCourtRentalsUri(cid,crid), rsp.data)
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
            page
        )
    }

    renderCalendarToSearchRentals(contentHeader, content, cid, crid, handleSubmit)
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers
