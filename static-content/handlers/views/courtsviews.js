import Html from "../../utils/htmlfuns.js";
import pagination from "../../utils/pagination.js"
const { createPaginationLinks } = pagination
const { div, a, ul, li, h1, h2 } = Html

function courtsView(
    courtsResponse,
    cid,
    skip,
    limit,
    contentHeader,
    content,
    onLinkClick,
) {
    const courts = courtsResponse.courts
    const maxNumOfElems = courtsResponse.paginationInfo.totalElements

    const currHeader = contentHeader.textContent
    const header = "Courts"

    const all =
        ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.clubId}`)),
            ),
        )
    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), maxNumOfElems, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(all, navigation)
}

function courtDetailsView(
    courtResponse,
    cid,
    crid,
    contentHeader,
    content,
) {
    const courtDetails =
        ul(
            li(courtResponse.name),
            li(a("Club", `#clubs/${courtResponse.clubId}`)),
            li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
        )

    content.replaceChildren("Court")
    content.replaceChildren(courtDetails)
}

function courtRentalsView(courtRentals, clubId, courtId, skip, limit, contentHeader, content, onLinkClick) {
    const rentals = courtRentals.rentals
    const maxNumOfElems = courtRentals.paginationInfo.totalElements
    const baseLink = `clubs/${clubId}/courts/${courtId}/rentals`

    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const all = div(
        ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()}: `, `#rentals/${rental.rid}`))
            ),
        ),
        a("Back", `#clubs/${clubId}/courts/${courtId}`),
    )

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), maxNumOfElems, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(all, navigation)
}

const courtsViews = {
    courtsView,
    courtDetailsView,
    courtRentalsView
}

export default courtsViews