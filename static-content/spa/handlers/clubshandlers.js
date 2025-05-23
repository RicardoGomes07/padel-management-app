import { request } from "../router.js"
import clubsRequests from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import uriManager from "../managers/uriManager.js";
import { createPaginationManager } from "../managers/paginationManager.js"

const { fetchClubDetails, fetchClubs } = clubsRequests
const { renderClubDetailsView, renderClubsView, renderCreateClubView } = clubViews
const { errorView } = errorsViews
const { path, query } = request
const { listClubsUri, getClubDetailsUri } = uriManager

const clubsPagination =
    createPaginationManager(fetchClubs, "clubs")

async function getClubs(contentHeader, content) {
    const name = query("name")
    const page = Number(query("page")) || 1

    const [clubs, count] = await clubsPagination
        .reqParams(name)
        .resetCacheIfNeeded("name", name)
        .getPage(
            page,
            (message) => { errorView(contentHeader, content, listClubsUri() ,message) }
        )

    renderClubsView(
        contentHeader,
        content,
        clubs,
        count,
        name,
        page,
    )
}

async function getClubDetails(contentHeader, content) {
    const cid = path("cid")

    const result = await fetchClubDetails(cid)

    if( result.status !== 200) errorView(contentHeader, content, listClubsUri() ,result.data)
    else renderClubDetailsView(contentHeader, content, result.data)
}

async function createClub(contentHeader, content) {
    const handleSubmit = async function(e){
        e.preventDefault()
        const clubName = document.querySelector("#clubName").value
        const result = await clubsRequests.createClub(clubName)
        if (result.status === 201) {
            const newClubId = result.data.cid
            window.location.hash = getClubDetailsUri(newClubId)
        } else {
            errorView(contentHeader, content, result.data)
        }
    }

    renderCreateClubView(
        contentHeader,
        content,
        handleSubmit,
    )
}

const clubHandlers= {
    getClubDetails,
    getClubs,
    createClub,
}

export default clubHandlers
