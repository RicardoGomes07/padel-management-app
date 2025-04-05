import Html from "../utils/htmlfuns.js";
import {path, query} from "../router.js";
import {API_BASE_URL} from "./home.js";
const { div, a, ul, li, h1, h2 } = Html;

function getCourtsByClub(mainContent){
    const cid = path("cid")
    const skip = query("skip") || "0"
    const limit = query("limit") || "10"

    fetch(API_BASE_URL + "courts/clubs/" + cid + "?skip=" + skip + "&limit=" + limit)
        .then(res => res.json())
        .then(courtsResponse => {
            const courts = courtsResponse.courts;
            const all = div(
                h1("Courts"),
                ul(
                    ...courts.map(court =>
                        li(a(court.name, "#clubs/" + cid + "/courts/" + court.clubId)),
                    ),
                ),
            )
            const nextLink = a("Next", "#courts/club/" + cid + "?skip=" + (skip + limit) + "&limit=" + limit);
            const prevLink = a("Prev", "#courts/club/" + cid + "?skip=" + (skip - limit) + "&limit=" + limit);
            
            if (skip <= 0) prevLink.style.display = "none";
            if (skip + limit >= courts.length) nextLink.style.display = "none";
            mainContent.replaceChildren(all, nextLink, prevLink)
        }
    )       
}

function getCourt(mainContent) {
    const crid = path("crid")
    const cid = path("cid")

    fetch(API_BASE_URL + "courts/" + crid)
        .then(res => res.json())
        .then(court => {
            const all = div(
                h2("Court"),
                ul(
                    li(court.name),
                    li(a("Club", "#clubs/" + court.clubId)),
                    li(a("Court Rentals", "#clubs/" + cid + "/courts/"  + crid + "/rentals")),
                )
            )
            mainContent.replaceChildren(all)
        }
    )       
}

function getCourtRentals(mainContent) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = query("skip") || "0"
    const limit = query("limit") || "10"
    const baseLink = "clubs/" + cid + "/courts/" + crid + "/rentals";

    fetch(API_BASE_URL + baseLink)
        .then(res => res.json())
        .then(response => {
            const rentals = response.rentals;
            const all = div(
                h1("Rentals"),
                ul(
                    ...rentals.map(rental =>
                        li(a(rental.date.toString() + ": ", "#rentals/" + rental.rid))
                    ),
                ),
                a("Back", "#clubs/" + cid + "/courts/" + crid),
            )

            //const nextLink = a("Next", "#" + baseLink + "?skip=" + (skip + limit) + "&limit=" + limit);
            //const prevLink = a("Prev", "#" + baseLink + "?skip=" + (skip - limit) + "&limit=" + limit);

            //if (skip === 0) prevLink.style.display = "none";
            //if (skip + limit >= rentals.length) nextLink.style.display = "none";

            mainContent.replaceChildren(all)
        })
}

const courtHandlers = {
    getCourtsByClub,
    getCourt,
    getCourtRentals,
}

export default courtHandlers;