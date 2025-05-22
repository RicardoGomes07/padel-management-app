import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js";
import uriManager from "../../managers/uriManager.js";

const { div, a, ul, li, p } = Html;
const { createPaginationLinks } = pagination
const { getUserRentalsUri, getUserProfileUri, getRentalDetailsUri } = uriManager

function renderUserRentalsView(contentHeader, content, rentals, username,  uid, skip, limit, hasNext) {
    const currHeader = contentHeader.textContent
    const header = "Rentals of " + username

    const backLink = div(a("Back", getUserProfileUri(uid)))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(
                    a(`${rental.date} ${rental.initialHour} to ${rental.finalHour}`,
                        getRentalDetailsUri(rental.cid, rental.crid, rental.rid)
                    )
                )
            )
        )
        : p("No rentals found")

    const info = div(rentalList)
    const navigation = createPaginationLinks(getUserRentalsUri(uid, skip, limit), Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, info, navigation)
}

function renderUserDetailsView(contentHeader, content, user, skip, limit) {
    const header = "User Info"
    const info = ul(
        li("Name: " + user.name),
        li("Email: " + user.email),
        li(a("User Rentals ", getUserRentalsUri(user.uid, skip, limit))),
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

const usersViews = {
    renderUserRentalsView,
    renderUserDetailsView,
}

export default usersViews;