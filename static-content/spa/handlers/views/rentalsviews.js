import Html from "../../dsl/htmlfuns.js";
import uriManager from "../../managers/uriManager.js";
import {authenticated} from "../../managers/userAuthenticationManager.js";


const { a, ul, li, div, formElement} = Html;
const { deleteRentalUri, getUserProfileUri, getCourtDetailsUri, getRentalDetailsUri, updateRentalIntentUri } = uriManager;

function renderRentalDetailsView(contentHeader, content, rental) {
    const header = "Rental Info"

    const info = ul(
        li("Court: ", a(rental.court.name, getCourtDetailsUri(rental.court.cid, rental.court.crid) )),
        li("Renter: ", a(rental.renter.name, getUserProfileUri(rental.renter.uid))),
        li(`Date: ${rental.date.toString()}`),
        li(`TimeSlot: ${rental.initialHour}h - ${rental.finalHour === 24 ? "23:59": rental.finalHour }h`),
        authenticated() ? li(a("Update Rental", updateRentalIntentUri(rental.court.cid, rental.court.crid, rental.rid))): "",
        authenticated() ? li(a("Delete Rental", deleteRentalUri(rental.court.cid, rental.court.crid, rental.rid))) : ""
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCalendarToSearchRentals(contentHeader, content, cid, crid, handleSubmit) {
    const header = "Search Rentals"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const backLink = div(a("Back", getCourtDetailsUri(cid, crid)))
    const form = li(
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Search Rentals"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(form, backLink)
}

function renderUpdateRentalView(contentHeader, content, rental, handleSubmit) {
    const header = "Update Rental"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true, value: rental.date },
        { id: "startHour", label: "Start Hour", type: "hour", required: true, value: rental.initialHour },
        { id: "endHour", label: "End Hour", type: "hour", required: true, value: rental.finalHour },
    ]
    const backButton =  a("Back", getRentalDetailsUri(rental.court.cid, rental.court.crid, rental.rid))
    const form = formElement(fields, handleSubmit, {
                className: "form",
                submitText: "Update Rental"
            })

    contentHeader.replaceChildren(header)
    content.replaceChildren(backButton, form)
}

function renderRentalCreationForm(contentHeader, content, cid, crid, rentalInfo, handleSubmit) {
    const header = "Create Rental"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true, value: rentalInfo.date },
        { id: "startHour", label: "Start Hour", type: "hour", required: true, value: rentalInfo.startHour },
        { id: "endHour", label: "End Hour", type: "hour", required: true, value: rentalInfo.endHour },
    ]

    const backLink = div(a("Back", getCourtDetailsUri(cid, crid)))
    const form = formElement(fields, handleSubmit, {
        className: "form",
        submitText: "Create Rental"
    })

    contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, form)
}

const rentalsViews = {
    renderRentalDetailsView,
    renderCalendarToSearchRentals,
    renderUpdateRentalView,
    renderRentalCreationForm,
}
export default rentalsViews;
