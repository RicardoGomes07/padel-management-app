import Html from "../../utils/htmlfuns.js";
import pagination from "../../utils/pagination.js";

const { div, a, ul, li } = Html;
const { createPaginationLinks } = pagination

function renderUserRentalsView(contentHeader, content, rentals, totalElements, uid, skip, limit, onLinkClick) {
    const currHeader = contentHeader.textContent
    const header = "Rentals"
    const info = div(
        ul(
            ...rentals.map(rental =>
                li(a(rental.date + ": ", "#rentals/" + rental.rid))
            ),
        ),
        a("Back", "#users/" + uid),
    )

    const navigation = createPaginationLinks("users/" + uid + "/rentals", Number(skip), Number(limit), totalElements, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation);
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