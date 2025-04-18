import Html from "../../utils/htmlfuns.js";
import pagination  from "../../utils/pagination.js";

const { a, ul, li, p} = Html;
const { createPaginationLinks } = pagination

function renderClubDetailsView(contentHeader, content, club){
    const header = "Club Info"
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, `#users/${club.owner.uid}`)),
        li(a("Courts", `#clubs/${club.cid}/courts`)),
        a("All Clubs", "#clubs")
    );

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderClubsView(contentHeader, content, clubs, totalElements, skip, limit){
    const currHeader = contentHeader.textContent
    const header = "Clubs"
    const info = clubs.length > 0
        ? ul(
            ...clubs.map(club =>
                li(a(club.name, `#clubs/${club.cid}`)),
            )
        )
        : p("No clubs found")

    const navigation = createPaginationLinks("clubs", Number(skip), Number(limit), totalElements)

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(info, navigation)
}

const clubViews ={
    renderClubDetailsView,
    renderClubsView
}
export default clubViews