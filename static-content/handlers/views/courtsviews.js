import Html from "../../utils/htmlfuns";
import pagination from "../../utils/pagination"
const { createPaginationLinks } = pagination
const { div, a, ul, li, h1, h2 } = Html

function courtsView(
    courtsResponse,
    cid,
    skip,
    limit,
    mainContent
) {
    const courts = courtsResponse.courts
    const maxNumOfElems = courtsResponse.paginationInfo.totalElements
    const all = div(
        h1("Courts"),
        ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.clubId}`)),
            ),
        ),
    )
    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), maxNumOfElems)
    mainContent.replaceChildren(all, navigation)
}

function courtDetailsView(
    courtResponse,
    cid,
    crid,
    mainContent
) {
    const courtDetails = div(
        h2("Court"),
        ul(
            li(courtResponse.name),
            li(a("Club", `#clubs/${courtResponse.clubId}`)),
            li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
        )
    )
    mainContent.replaceChildren(courtDetails)
}

function courtRentalsView(courtRentals, clubId, courtId, skip, limit, mainContent) {
    const rentals = courtRentals.rentals
    const maxNumOfElems = courtRentals.paginationInfo.totalElements
    const baseLink = `#clubs/${clubId}/courts/${courtId}`
    const all = div(
        h1("Rentals"),
        ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()}: `, `#rentals/${rental.rid}`))
            ),
        ),
        a("Back", baseLink),
    )

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), maxNumOfElems)
    mainContent.replaceChildren(all, navigation)
}

const courtsViews = {
    courtsView,
    courtDetailsView,
    courtRentalsView
}

export default courtsViews