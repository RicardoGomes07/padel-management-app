import Html from "../utils/htmlfuns.js";
import { createPaginationLinks } from "../utils/pagination.js";

const { div, a, ul, li, h1, h2 } = Html;

function renderClubView(mainContent, club){
    const header = h2("Club Info");
    const info = ul(
        li(`Name: ${club.name}`),
        li("Owner: ", a(club.owner.name, `#users/${club.owner.uid}`)),
        li(a("Courts", `#clubs/${club.cid}/courts`)),
        a("Back", "#clubs")
    );
    mainContent.replaceChildren(header, info);
}

function renderClubsView(mainContent, clubsResponse){
    const clubs = clubsResponse.clubs
    const maxNumOfElems = clubsResponse.paginationInfo.totalElements
    const text = h1("Clubs")
    const clubsElements =
        ul(
            ...clubs.map(club =>
                li(a(club.name, `#clubs/${club.cid}`)),
            )
        )
    const container = div(text, clubsElements);
    const navigation = createPaginationLinks("clubs", Number(skip), Number(limit), maxNumOfElems);
    
    mainContent.replaceChildren(container, navigation);
}

const clubviews ={
    renderClubView,
    renderClubsView
}
export default clubviews