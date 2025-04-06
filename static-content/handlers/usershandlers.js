import Html from "../utils/htmlfuns.js";
import {API_BASE_URL} from "./home.js";
import {request} from "../router.js";
const { div, a, ul, li, h1, h2 } = Html;
const {path, query} = request

function getUserRentals(mainContent) {
    const userId = path("uid")
    const skip = query("skip") || "0"
    const limit = query("limit") || "10"
    const baseLink = "users/" + userId + "/rentals"
    fetch(API_BASE_URL + baseLink + "?skip=" + skip + "&limit=" + limit)
        .then(res => res.json())
        .then(rentalsResponse => {
            const rentals = rentalsResponse.rentals;
            const all = div(
                h1("Rentals"),
                ul(
                    ...rentals.map(rental =>
                        li(a(rental.date.toString() + ": ", "#rentals/" + rental.rid))
                    ),
                ),
                a("Back", "#users/" + userId),
            )

            const nextLink = a("Next", "#" + baseLink + "?skip=" + (skip + limit) + "&limit=" + limit);
            const prevLink = a("Prev", "#" + baseLink + "?skip=" + (skip - limit) + "&limit=" + limit);

            if (skip <= 0) prevLink.style.display = "none";
            if (skip + limit >= rentals.length) nextLink.style.display = "none";

            mainContent.replaceChildren(all, prevLink, nextLink);
        })
}

function getUserDetails(mainContent){
    const userId = path("uid")
    fetch(API_BASE_URL + "users/" + userId)
        .then(res => res.json())
        .then(user => {
            const header = h2("User Info")
            const info = ul(
                li("Name: " + user.name),
                li("Email: " + user.email),
                li(a("User Rentals ", "#users/" + user.uid + "/rentals")),
            )
            mainContent.replaceChildren(header, info)
        })
}

const userHandlers = {
    getUserDetails,
    getUserRentals,
}

export default userHandlers;