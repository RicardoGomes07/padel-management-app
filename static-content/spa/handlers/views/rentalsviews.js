import Html from "../../dsl/htmlfuns.js";
import uriManager from "../../managers/uriManager.js";

const { a, ul, li, div, formRequest } = Html;
const { searchCourtRentalsByDateUri, deleteRentalUri, getUserProfileUri, getCourtDetailsUri,
    updateRentalUri, getRentalDetailsUri, updateRentalIntentUri } = uriManager;

function renderRentalDetailsView(contentHeader, content, rental) {
    const header = "Rental Info"
    const info = ul(
        li("Court: ", a(rental.court.name, getCourtDetailsUri(rental.court.cid, rental.court.crid) )),
        li("Renter: ", a(rental.renter.name, getUserProfileUri(rental.renter.uid))),
        li(`Date: ${rental.date.toString()}`),
        li(`TimeSlot: ${rental.initialHour}h - ${rental.finalHour}h`),
        li(a("Update Rental", updateRentalIntentUri(rental.court.cid, rental.court.crid, rental.rid))),
        li(a("Delete Rental", deleteRentalUri(rental.court.cid, rental.court.crid, rental.rid)))
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCalendarToSearchRentals(contentHeader, content, handleSubmit, cid, crid) {
    const header = "Search Rentals"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const backLink = div(a("Back", getCourtDetailsUri(cid, crid)))
    const children = li(
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Search Rentals"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

function renderUpdateRentalView(contentHeader, content, rental, handleSubmit) {
    const header = "Update Rental"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true, value: rental.date },
        { id: "startHour", label: "Start Hour", type: "hour", required: true, value: String(rental.initialHour).padStart(2, "0") },
        { id: "endHour", label: "End Hour", type: "hour", required: true, value: String(rental.finalHour).padStart(2, "0") },
    ]
    const backButton =  a("Back", getRentalDetailsUri(rental.court.cid, rental.court.crid, rental.rid))
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
