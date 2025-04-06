import Html from "../utils/htmlfuns.js";
import {request} from "../router.js";
import {API_BASE_URL} from "./home.js";
import pagination from "../utils/pagination.js";

const { DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT, createPaginationLinks } = pagination

const { div, a, ul, li, h1, h2 } = Html;
const {path, query} = request

function getCourtsByClub(mainContent) {
    const cid = path("cid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT

    fetch(`${API_BASE_URL}courts/clubs/${cid}?skip=${skip}&limit=${limit}`)
        .then(res => res.json())
        .then(courtsResponse => {
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
        )
}

function getCourt(mainContent) {
    const crid = path("crid")
    const cid = path("cid")

    fetch(`${API_BASE_URL}courts/${crid}`)
        .then(res => res.json())
        .then(court => {
                const all = div(
                    h2("Court"),
                    ul(
                        li(court.name),
                        li(a("Club", `#clubs/${court.clubId}`)),
                        li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
                    )
                )
                mainContent.replaceChildren(all)
            }
        )
}

function getCourtRentals(mainContent) {
    const cid = path("cid")
    const crid = path("crid")
    const skip = query("skip") || DEFAULT_VALUE_SKIP
    const limit = query("limit") || DEFAULT_VALUE_LIMIT
    const baseLink = `clubs/${cid}/courts/${crid}/rentals`

    fetch(`${API_BASE_URL}${baseLink}`)
        .then(res => res.json())
        .then(response => {
            const rentals = response.rentals
            const maxNumOfElems = response.paginationInfo.totalElements
            const all = div(
                h1("Rentals"),
                ul(
                    ...rentals.map(rental =>
                        li(a(`${rental.date.toString()}: `, `#rentals/${rental.rid}`))
                    ),
                ),
                a("Back", `#clubs/${cid}/courts/${crid}`),
            )

            const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), maxNumOfElems)

            mainContent.replaceChildren(all, navigation)
        })
}


const courtHandlers = {
    getCourtsByClub,
    getCourt,
    getCourtRentals,
}

export default courtHandlers