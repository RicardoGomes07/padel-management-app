import Html from "../../dsl/htmlfuns.js";
import uriManager from "../../managers/uriManager.js";

const { a, ul, li, div, formRequest } = Html;

function renderRentalDetailsView(contentHeader, content, rental) {
    const header = "Rental Info"
    const info = ul(
        li("Court: ", a(rental.court.name, uriManager.getCourtDetails(rental.court.cid, rental.court.crid) )),
        li("Renter: ", a(rental.renter.name, uriManager.getUserProfile(rental.renter.uid))),
        li(`Date: ${rental.date.toString()}`),
        li(`TimeSlot: ${rental.initialHour}h - ${rental.finalHour}h`),
        li(a("Update Rental", `#clubs/${rental.court.cid}/courts/${rental.court.crid}/rentals/${rental.rid}/update`)),
        li(a("Delete Rental", uriManager.deleteRental(rental.court.cid, rental.court.crid, rental.rid)))
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCalendarToSearchRentals(contentHeader, content, date, cid, crid) {
    const header = "Search Rentals"
    const handleSubmit = function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value
        window.location.hash = uriManager.searchCourtRentalsByDate(cid, crid, validDate)
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const backLink = div(a("Back", uriManager.getCourtDetails(cid, crid)))
    const children = li(
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Search Rentals"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

function renderUpdateRentalView(contentHeader, content, rental) {
    const header = "Update Rental"
    const handleSubmit = function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value
        window.location.hash = uriManager.updateRental(rental.court.cid, rental.court.crid, rental.rid, validDate, startHour, endHour)
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true, value: rental.date },
        { id: "startHour", label: "Start Hour", type: "hour", required: true, value: String(rental.initialHour).padStart(2, "0") },
        { id: "endHour", label: "End Hour", type: "hour", required: true, value: String(rental.finalHour).padStart(2, "0") },
    ]
    const backButton =  a("Back", uriManager.getRentalDetails(rental.court.cid, rental.court.crid, rental.rid))
    const form = formRequest(fields, handleSubmit, {
                className: "form",
                submitText: "Update Rental"
            })

    contentHeader.replaceChildren(header)
    content.replaceChildren(backButton, form)
}

const rentalsViews = {
    renderRentalDetailsView,
    renderCalendarToSearchRentals,
    renderUpdateRentalView
}
export default rentalsViews;
