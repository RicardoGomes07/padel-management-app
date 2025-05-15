import Html from "../../dsl/htmlfuns.js";

const { a, ul, li, div, formRequest } = Html;

function renderRentalDetailsView(contentHeader, content, rental) {
    const header = "Rental Info"
    const info = ul(
        li("Court: ", a(rental.court.name, `#clubs/${rental.court.clubId}/courts/${rental.court.crid}`)),
        li("Renter: ", a(rental.renter.name, `#users/${rental.renter.uid}`)),
        li(`Date: ${rental.date.toString()}`),
        li(`TimeSlot: ${rental.initialHour}h - ${rental.finalHour}h`),
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCalendarToSearchRentals(contentHeader, content, date, cid, crid) {
    const header = "Search Rentals"
    const handleSubmit = function(e){
        e.preventDefault()
        const validDate = document.querySelector("#date").value
        window.location.hash = `#clubs/${cid}/courts/${crid}/rentals/search?date=${validDate}`
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const backLink = div(a("Back", `#clubs/${cid}/courts/${crid}`))
    const children = li(
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Search Rentals"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

const rentalsViews = {
    renderRentalDetailsView,
    renderCalendarToSearchRentals
}
export default rentalsViews;
