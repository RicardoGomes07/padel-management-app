import Html from "../utils/htmlfuns.js";
import {API_BASE_URL} from "./home.js";
import {request} from "../router.js";
const { a, ul, li, h2 } = Html;
const {path} = request

function getRentalDetails(mainContent) {
    const rentalId = path("rid")

    fetch(`${API_BASE_URL}rentals/${rentalId}`)
        .then(res => res.json())
        .then(rental => {
            const header = h2("Rental Info")
            const info = ul(
                li("Court: ", a(rental.court.name, `#clubs/${rental.court.clubId}/courts/${rental.court.crid}`)),
                li("Renter: ", a(rental.renter.name, `#users/${rental.renter.uid}`)),
                li(`Date: ${rental.date.toString()}`),
                li(`TimeSlot: ${rental.initialHour}h - ${rental.finalHour}h`),
            )
            mainContent.replaceChildren(header, info)
        })
}

const rentalHandlers= {
    getRentalDetails,
}

export default rentalHandlers;