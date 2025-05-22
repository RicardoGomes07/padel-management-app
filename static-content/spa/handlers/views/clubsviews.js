import Html from "../../dsl/htmlfuns.js";
import pagination  from "./pagination.js";
import uriManager from "../../managers/uriManager.js";
import clubsrequests from "../requests/clubsrequests.js";

const { fetchClubs } = clubsrequests;
const { input, a, ul, li, p, span, div } = Html;
const { createPaginationLinks } = pagination
const { getUserProfileUri, listClubsUri, listClubCourtsUri, createCourtFromUri, getClubDetailsUri, createClubUri }
    = uriManager

function renderClubDetailsView(contentHeader, content, club){
    const header = "Club Info"
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, getUserProfileUri(club.owner.uid))),
        li(
            a("Courts", listClubCourtsUri(club.cid)),
            a("Create Court", createCourtFromUri(club.cid)),
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

function renderClubsView(contentHeader, content, clubs, name, skip, limit, hasNext){
    const currHeader = contentHeader.textContent
    const header = `Clubs${name ? `: ${name}` : ``}`
    const info = clubsList(clubs)

    const createClubAnchor = a("Create a Club", createClubUri())

    const navigation = createPaginationLinks(listClubsUri(name, skip, limit), Number(skip), Number(limit), hasNext)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(clubSearchBar(skip, limit), info, navigation, createClubAnchor)
}

function clubSearchBar(skip, limit) {
    let clubNameSearch = ""

    const clubsContainer = div()

    const renderClubs = (clubs) => {
        const clubsInfo = clubsList(clubs)

        clubsContainer.replaceChildren(clubsInfo)
    }

    const onChange = (name) => {
        clubNameSearch = name
        if (clubNameSearch.length >= 3) {
            fetchClubs(clubNameSearch).then(res => {
                renderClubs(res.data.clubs)
            })
        } else {
            clubsContainer.replaceChildren()
        }
        anchor.setAttribute("href", listClubsUri(clubNameSearch, skip, limit));
    }

    const anchor = a("Search Clubs",  listClubsUri(clubNameSearch, skip, limit));

    const info =
        ul(
            input("Search", "", onChange),
            anchor,
            clubsContainer,
        )

    return info
}

const clubViews ={
    renderClubDetailsView,
    renderClubsView,
    clubSearchBar,
}
export default clubViews