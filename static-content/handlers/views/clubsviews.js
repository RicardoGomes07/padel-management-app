import Html from "../../utils/htmlfuns.js";
import pagination  from "../../utils/pagination.js";

const { a, ul, li } = Html;
const { createPaginationLinks } = pagination
function renderClubView(contentHeader, content, club){
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, `#users/${club.owner.uid}`)),
        li(a("Courts", `#clubs/${club.cid}/courts`)),
        a("All Clubs", "#clubs")
    );

    contentHeader.replaceChildren("Club Info")
    content.replaceChildren(info);
}

function renderClubsView(contentHeader, content, clubsResponse, skip, limit, onLinkClick){
    const clubs = clubsResponse.clubs
    const maxNumOfElems = clubsResponse.paginationInfo.totalElements

    const currHeader = contentHeader.textContent
    const header = "Clubs"

    const clubsElements =
        ul(
            ...clubs.map(club =>
                li(a(club.name, `#clubs/${club.cid}`)),
            )
        )

    const navigation = createPaginationLinks("clubs", Number(skip), Number(limit), maxNumOfElems, onLinkClick);

    if(currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(clubsElements, navigation);
}

const clubViews ={
    renderClubView,
    renderClubsView
}
export default clubViews