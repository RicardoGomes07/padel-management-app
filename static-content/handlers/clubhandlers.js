import Html from "../utils/htmlfuns.js";
import {API_BASE_URL} from "./home.js";
import { request } from "../router.js";
import pagination from "../utils/pagination.js";

const { div, a, ul, li, h1, h2 } = Html;
const {path, query} = request
const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT, createPaginationLinks } = pagination

function getClubs(mainContent) {
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    fetch(`${API_BASE_URL}clubs?skip=${skip}&limit=${limit}`)
        .then(res => res.json())
        .then(clubsResponse => {
            const clubs = clubsResponse.clubs
            const maxNumOfElems = clubsResponse.paginationInfo.totalElements
            const text = h1("Clubs")
            const clubsElements =
                ul(
                    ...clubs.map(club =>
                        li(a(club.name, `#clubs/${club.cid}`)),
                    )
                )
            const container = div(text, clubsElements)
            const navigation = createPaginationLinks("clubs", Number(skip), Number(limit), maxNumOfElems)
            mainContent.replaceChildren(container, navigation)
        })
}

function getClub(mainContent) {
    const cid = path("cid")

    fetch(`${API_BASE_URL}clubs/${cid}`)
        .then(res => res.json())
        .then(club => {
            const header = h2("Club Info")
            const info = ul(
                li(`Name: ${club.name}`),
                li("Owner: ", a(club.owner.name, `#users/${club.owner.uid}`)),
                li(a("Courts", `#clubs/${club.cid}/courts`)),
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
