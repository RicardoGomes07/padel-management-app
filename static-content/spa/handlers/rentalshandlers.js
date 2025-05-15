import { request } from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import auxiliaryFuns from "./auxfuns.js";
import courtsRequests from "./requests/courtsrequests.js";
import courtsViews from "./views/courtsviews.js";

const { renderRentalDetailsView } = rentalsViews
const { fetchRentalDetails,  } = rentalsRequests
const { errorView } = errorsViews
const { path, query } = request
const { isValidDate, isValidHour, parseHourFromString, getFinalRentalHours, contains, splitIntoHourlySlots } = auxiliaryFuns
const { renderRentalAvailableFinalHours } = courtsViews


async function getRentalDetails(contentHeader, content) {
    const clubId = Number(path("cid"))
    const courtId = Number(path("crid"))
    const rentalId = Number(path("rid"))

    const result = await fetchRentalDetails(clubId, courtId, rentalId)

    if(result.status !== 200)
        errorView(contentHeader, content, `#clubs/${clubId}/courts/${courtId}/rentals` ,result.data)
    else
        renderRentalDetailsView(contentHeader, content, result.data)
}

async function createRental(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const date = query("date")
    const initialHour = query("start")
    const finalHour = query("end")
    const startHour = parseHourFromString(initialHour)

    if (isValidDate(date) && isValidHour(initialHour) && isValidHour(finalHour)) {
        const endHour = parseHourFromString(finalHour)
        const rental =  await rentalsRequests.createRental(Number(cid), Number(crid), date, startHour, endHour)
        if (rental.status === 201)  window.location.hash = `#clubs/${cid}/courts/${crid}/rentals`
        else errorView(contentHeader, content, `#/#clubs/${cid}/courts/${crid}/available_hours?date=${date}`,rental.data)
    } else if (isValidDate(date) && isValidHour(initialHour)) {
        const availableHours = await courtsRequests.getAvailableHours(Number(cid), Number(crid), date)
        if (availableHours.status !== 200){
            errorView(contentHeader, content, `#clubs/${cid}/courts/${crid}/available_hours` ,availableHours.data)
        }else if (!contains(availableHours.data.hours, startHour)){
            errorView(contentHeader, content,`#clubs/${cid}/courts/${crid}/available_hours?date=${date}&start=${initialHour}`, {
                title: "Hour not available",
                description: `The selected hour is not available in day ${date}`
            })
        }else {
            const finalRentalHours = getFinalRentalHours(availableHours.data.hours, startHour)
            renderRentalAvailableFinalHours(contentHeader, content, initialHour, finalRentalHours, cid, crid, date, initialHour)
        }
    }
}

function updateRental(contentHeader, content) {
    // TODO: Implement the update rental functionality
}

function deleteRental(contentHeader, content) {
    // TODO: Implement the delete rental functionality
}

function searchRentals(contentHeader, content) {
    // TODO: Implement the search rentals by date functionality
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers
