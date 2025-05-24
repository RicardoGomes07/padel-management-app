import Html from "../../dsl/htmlfuns.js";
import pagination  from "./pagination.js";
import uriManager from "../../managers/uriManager.js";
import clubsrequests from "../requests/clubsrequests.js";

const { fetchClubs } = clubsrequests;
const { input, a, ul, li, p, div, formElement } = Html;
const { createPaginationLinks } = pagination
const { getUserProfileUri, listClubsUri, listClubCourtsUri, createCourtFromUri, getClubDetailsUri,
    createClubUri, searchCourtsToRentUri } = uriManager

const DEFAULT_SEARCH_LIMIT = 5

function renderClubDetailsView(contentHeader, content, club){
    const header = "Club Info"
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, getUserProfileUri(club.owner.uid))),
        li(
            a("Courts", listClubCourtsUri(club.cid)),
            a("Create Court", createCourtFromUri(club.cid)),
            a("Rent", searchCourtsToRentUri(club.cid)),
        ),
        a("All Clubs", listClubsUri()),
    );

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function clubsList(clubs) {
    return clubs.length > 0
        ? ul(
            ...clubs.map(club =>
                li(a(club.name, getClubDetailsUri(club.cid))),
            )
        )
        : p("No clubs found")
}

function renderClubsView(contentHeader, content, clubs, count, name, page){
    const currHeader = contentHeader.textContent
    const header = `Clubs${name ? `: ${name}` : ``}`
    const info = clubsList(clubs)

    const createClubAnchor = a("Create a Club", createClubUri())

    const navigation = createPaginationLinks(listClubsUri(name, page), count, page)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(clubSearchBar(), info, navigation, createClubAnchor)
}

function clubSearchBar() {
    let clubNameSearch = ""

    const clubsContainer = div()

    const renderClubs = (clubs) => {
        const clubsInfo = clubsList(clubs)

        clubsContainer.replaceChildren(clubsInfo)
    }

    const onChange = (name) => {
        clubNameSearch = name
        if (clubNameSearch.length >= 3) {
            fetchClubs(clubNameSearch, 0, DEFAULT_SEARCH_LIMIT).then(res => {
                renderClubs(res.data.items.clubs)
            })
        } else {
            clubsContainer.replaceChildren()
        }
        anchor.setAttribute("href", listClubsUri(clubNameSearch));
    }

    const anchor = a("Search Clubs",  listClubsUri(clubNameSearch));

    return ul(
        input("Search", "text", "", "", false, onChange),
        anchor,
        clubsContainer,
    )
}

function renderCreateClubView(contentHeader, content, handleSubmit) {
    const header = "Create a Club"

    const fields = [
        {id: "clubName", name: "clubName", label: "Name of the Club", type: "text", required: true }
    ]

    const form = formElement(fields, handleSubmit, {
        className: "form",
        submitText: "Create Clubs"
    })

    const back = a("Back", listClubsUri())

    contentHeader.replaceChildren(header)
    content.replaceChildren(form, back)
}

const clubViews ={
    renderClubDetailsView,
    renderClubsView,
    clubSearchBar,
    renderCreateClubView,
    clubsList
}
export default clubViews