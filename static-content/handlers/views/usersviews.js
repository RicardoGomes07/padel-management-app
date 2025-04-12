import Html from "../../utils/htmlfuns.js";
import pagination from "../../utils/pagination.js";

const { div, a, ul, li } = Html;
const { createPaginationLinks } = pagination

function getUserRentalsView(contentHeader, content, rentalsResponse, userId, skip, limit, onLinkClick) {
    const rentals = rentalsResponse.rentals;
    const maxNumOfElems = rentalsResponse.paginationInfo.totalElements
    const currHeader = contentHeader.textContent
    const header = "Rentals"
    const info = div(
        ul(
            ...rentals.map(rental =>
                li(a(rental.date + ": ", "#rentals/" + rental.rid))
            ),
        ),
        a("Back", "#users/" + userId),
    )

    const navigation = createPaginationLinks("users/" + userId + "/rentals", Number(skip), Number(limit), maxNumOfElems, onLinkClick)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation);
}

function getUserDetailsView(contentHeader, content, user) {
    const info = ul(
        li("Name: " + user.name),
        li("Email: " + user.email),
        li(a("User Rentals ", "#users/" + user.uid + "/rentals")),
    )

    contentHeader.replaceChildren("User Info")
    content.replaceChildren(info)
}

const usersViews = {
    getUserRentalsView,
    getUserDetailsView,
}

export default usersViews;