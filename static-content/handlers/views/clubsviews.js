import Html from "../../utils/htmlfuns.js";
import pagination  from "./pagination.js";

const { div, a, ul, li, p} = Html;
const { createPaginationLinks } = pagination

function renderClubDetailsView(contentHeader, content, club){
    const header = "Club Info"
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, `#users/${club.owner.uid}`)),
        li(a("Courts", `#clubs/${club.cid}/courts`)),
        a("All Clubs", "#clubs"),
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
                li(a(club.name, `#clubs/${club.cid}`)),
            )
        )
        : p("No clubs found")

    const navigation = createPaginationLinks("clubs", Number(skip), Number(limit), hasNext)

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