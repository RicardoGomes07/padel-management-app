import Html from "../../utils/htmlfuns.js";

const { a, ul, li } = Html;

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

const rentalsViews = {
    renderRentalDetailsView,
}
export default rentalsViews;
