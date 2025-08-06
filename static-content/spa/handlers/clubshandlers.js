import { request } from "../router.js"
import clubsRequests from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import uriManager from "../managers/uriManager.js";
import { createPaginationManager } from "../managers/paginationManager.js"
import { authenticated } from "../managers/userAuthenticationManager.js";
import errorManager from "../managers/errorManager.js";
import { redirectTo } from "../router.js";

const { fetchClubDetails, fetchClubs } = clubsRequests
const { renderClubDetailsView, renderClubsView, renderCreateClubView } = clubViews
const { errorView } = errorsViews
const { path, query } = request
const { loginUri, getClubDetailsUri, listClubsUri } = uriManager

const clubsPagination = createPaginationManager(fetchClubs, "clubs")

async function getClubs(contentHeader, content) {
    const name = query("name")
    const page = Number(query("page")) || 1

    const [clubs, count] = await clubsPagination
        .reqParams(name)
        .getPage(
            page,
            (message) => { errorManager.store(errorView(message)).render() }
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

    if( result.status === 200){
        renderClubDetailsView(contentHeader, content, result.data)
    } else {
        errorManager.store(errorView(result.data))
        redirectTo(listClubsUri())
    }
}

function createClub(contentHeader, content) {
    if (!authenticated()){
        redirectTo(loginUri())
        return
    }

    const handleSubmit = async function(e){
        e.preventDefault()
        const clubName = document.querySelector("#clubName").value.trim()
        const result = await clubsRequests.createClub(clubName)
        if (result.status === 201) {
            clubsPagination.resetCache()
            const newClubId = result.data.cid
            redirectTo(getClubDetailsUri(newClubId))
        } else {
            errorManager.store(errorView(result.data)).render()
        }
    }

    renderCreateClubView(contentHeader, content, handleSubmit)
}

const clubHandlers= {
    getClubDetails,
    getClubs,
    createClub,
}

export default clubHandlers
