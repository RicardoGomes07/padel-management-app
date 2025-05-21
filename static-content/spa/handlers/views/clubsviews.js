import Html from "../../dsl/htmlfuns.js";
import pagination  from "./pagination.js";
import uriManager from "../../managers/uriManager.js";

const { div, a, ul, li, p, span} = Html;
const { createPaginationLinks } = pagination

function renderClubDetailsView(contentHeader, content, club){
    const header = "Club Info"
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, uriManager.getUserProfile(club.owner.uid))),
        li(
            span(a("Courts", uriManager.listClubCourts(club.cid))),
            a("Create Court", uriManager.createCourtForm(club.cid)),
        ),
        a("All Clubs", uriManager.listClubs()),
    );

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderClubsView(contentHeader, content, clubs, skip, limit, hasNext){
    const currHeader = contentHeader.textContent
    const header = "Clubs"
    const info = clubs.length > 0
        ? ul(
            ...clubs.map(club =>
                li(a(club.name, uriManager.getClubDetails(club.cid))),
            )
        )
        : p("No clubs found")

    const navigation = createPaginationLinks(uriManager.listClubs(), Number(skip), Number(limit), hasNext)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation)
}

function renderClubCreationView(contentHeader, content){
    const header = "Create Club"
    const info = div(
        p("Create a new club"),
        div(
            input({ type: "text", placeholder: "Club Name" }),
            button("Create", { type: "submit" })
        )
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

const clubViews ={
    renderClubDetailsView,
    renderClubsView,
    renderClubCreationView,
}
export default clubViews