import Html from "../utils/htmlfuns.js";
import {API_BASE_URL} from "./home.js";
const { div, a, ul, li, h1, h2 } = Html;


function getClubs(mainContent, skip = 0, limit = 2) {
    fetch(API_BASE_URL + "clubs?skip=" + skip + "&limit=" + limit)
        .then(res => {
            console.log(res)
            return res.json()})
        
        .then(clubs => {
            console.log(clubs)
            const text = h1("Clubs");
            const clubsElements = clubs.map(club => ul(
                li(club.name),
                li(a("Info", "#clubs/" + club.cid)),
            ))
            const container = div(text,clubsElements);
            const nextLink = a("Next", "#clubs?skip=" + (skip + limit) + "&limit=" + limit);
            const prevLink = a("Prev", "#clubs?skip=" + (skip - limit) + "&limit=" + limit);
            
            //if (skip === 0) prevLink.style.display = "none";
            //if (skip + limit >= clubs.length) nextLink.style.display = "none";
            mainContent.replaceChildren(container, nextLink, prevLink);
        })
}

function getClub(mainContent, cid) {
    fetch(API_BASE_URL + "clubs/" + cid) //clubs[0] cid[1]
        .then(res => res.json())
        .then(club => {
            h2("Club Info")
            ul(
                li("Name: " + club.name),
                li("Owner: " + a(club.owner.name, "#users/" + club.owner.uid)),
                li(a("Back", "#clubs"))
            )
            mainContent.replaceChildren(ul)
        })
}

const clubHandlers= {
    getClub,
    getClubs,
}

export default clubHandlers
