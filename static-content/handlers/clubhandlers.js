import Html from "../utils/htmlfuns.js";
import {API_BASE_URL} from "./home.js";
import {path, query} from "../router.js";
const { div, a, ul, li, h1, h2 } = Html;


function getClubs(mainContent) {
    const skip = query("skip") || "0"
    const limit = query("limit") || "10"

    fetch(API_BASE_URL + "clubs?skip=" + skip + "&limit=" + limit)
        .then(res => res.json())
        .then(clubsResponse => {
            const clubs = clubsResponse.clubs;
            const text = h1("Clubs");
            const clubsElements =
                ul(
                    ...clubs.map(club =>
                        li(a(club.name, "#clubs/" + club.cid)),
                    )
                )
            const container = div(text,clubsElements);
            const nextLink = a("Next", "#clubs?skip=" + (skip + limit) + "&limit=" + limit);
            const prevLink = a("Prev", "#clubs?skip=" + (skip - limit) + "&limit=" + limit);
            
            if (skip <= 0) prevLink.style.display = "none";
            if (skip + limit >= clubs.length) nextLink.style.display = "none";
            mainContent.replaceChildren(container, prevLink, nextLink);
        })
}

function getClub(mainContent) {
    const cid = path("cid")

    fetch(API_BASE_URL + "clubs/" + cid)
        .then(res => res.json())
        .then(club => {
            const header = h2("Club Info")
            const info = ul(
                li("Name: " + club.name),
                li("Owner: ", a(club.owner.name, "#users/" + club.owner.uid)),
                li(a("Courts", "#courts/clubs/" + club.cid)),
                a("Back", "#clubs")
            )
            mainContent.replaceChildren(header, info)
        })
}

const clubHandlers= {
    getClub,
    getClubs,
}

export default clubHandlers
