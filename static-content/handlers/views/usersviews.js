    import Html from "../../utils/htmlfuns.js";
import pagination from "./pagination.js";

const { div, a, ul, li, p } = Html;
const { createPaginationLinks } = pagination

function renderUserRentalsView(contentHeader, content, rentals, username,  uid, skip, limit, hasNext) {
    const currHeader = contentHeader.textContent
    const header = "Rentals of " + username

    const backLink = div(a("Back", "#users/" + uid))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(a(`${rental.date} ${rental.initialHour} to ${rental.finalHour}`, "#rentals/" + rental.rid))
            )
        )
        : p("No rentals found")

    const info = div(rentalList)
    const baseLink = "users/" + uid + "/rentals"
    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, info, navigation)
}

function renderUserDetailsView(contentHeader, content, user) {
    const header = "User Info"
    const info = ul(
        li("Name: " + user.name),
        li("Email: " + user.email),
        li(a("User Rentals ", "#users/" + user.uid + "/rentals")),
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

const usersViews = {
    renderUserRentalsView,
    renderUserDetailsView,
}

export default usersViews;