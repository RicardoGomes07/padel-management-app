import Html from "../../utils/htmlfuns.js";
import pagination from "./pagination.js"

const { createPaginationLinks } = pagination
const { div, a, ul, li, p } = Html

function renderCourtsByClubView(contentHeader, content, courts, totalElements, cid, skip, limit) {
    const currHeader = contentHeader.textContent
    const header = "Courts"

    const courtList = courts.length > 0
        ? ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.crid}`))
            )
        )
        : p("No courts found")

    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), totalElements)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(courtList, navigation)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid) {
    const header = "Court"
    const info =
        ul(
            li(courtResponse.name),
            li(a("Club", `#clubs/${cid}`)),
            li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
        )

    content.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, totalElements, cid, crid, skip, limit) {
    const baseLink = `clubs/${cid}/courts/${crid}/rentals`

    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const backLink = div(a("Back", `#clubs/${cid}/courts/${crid}`))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()}: `, `#rentals/${rental.rid}`))
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), totalElements)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, rentalList, navigation)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView
}

export default courtsViews