import Html from "../../utils/htmlfuns.js";
import pagination from "../../utils/pagination.js"

const { createPaginationLinks } = pagination
const { div, a, ul, li } = Html

function renderCourtsByClubView(contentHeader, content, courts, totalElements, cid, skip, limit, onLinkClick) {
    const currHeader = contentHeader.textContent
    const header = "Courts"
    const info =
        ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.clubId}`)),
            ),
        )

    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), totalElements, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid) {
    const header = "Court"
    const info =
        ul(
            li(courtResponse.name),
            li(a("Club", `#clubs/${courtResponse.clubId}`)),
            li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
        )

    content.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, totalElements, cid, crid, skip, limit, onLinkClick) {
    const baseLink = `clubs/${cid}/courts/${crid}/rentals`

    const currHeader = contentHeader.textContent
    const header = "Rentals"
    const info = div(
        ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()}: `, `#rentals/${rental.rid}`))
            ),
        ),
        a("Back", `#clubs/${cid}/courts/${crid}`),
    )

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), totalElements, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView
}

export default courtsViews