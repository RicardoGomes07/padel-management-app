import { request } from "../router.js"
import pagination from "./views/pagination.js"
import clubsRequests from "./requests/clubsrequests.js"
import clubViews from "./views/clubsviews.js"
import errorsViews from "./views/errorsview.js"
import uriManager from "../managers/uriManager.js";
import { createPaginationManager } from "../managers/paginationManager.js"
import Html from "../dsl/htmlfuns.js";


const { fetchClubDetails, fetchClubs } = clubsRequests
const { renderClubDetailsView, renderClubsView } = clubViews
const { errorView } = errorsViews
const { path, query } = request
const { DEFAULT_VALUE_LIMIT, DEFAULT_VALUE_SKIP} = pagination
const { formElement, a } = Html
const { listClubsUri, getClubDetailsUri } = uriManager

const clubsPagination =
    createPaginationManager(fetchClubs, "clubs")

async function getClubs(contentHeader, content) {
    const name = query("name")
    const skip = Number(query("skip")) || DEFAULT_VALUE_SKIP
    const limit = Number(query("limit")) || DEFAULT_VALUE_LIMIT

    const clubs = await clubsPagination
        .reqParams(name)
        .resetCacheIfNeeded("name", name)
        .getPage(
            skip,
            limit,
            (message) => { errorView(contentHeader, content, listClubsUri(name, skip, limit) ,message) }
        )

    const hasNext = clubsPagination.hasNext()

    renderClubsView(
        contentHeader,
        content,
        clubs,
        name,
        skip,
        limit,
        hasNext
    )
}

async function getClubDetails(contentHeader, content) {
    const cid = path("cid")

    const result = await fetchClubDetails(cid)

    if( result.status !== 200) errorView(contentHeader, content, listClubsUri("", DEFAULT_VALUE_SKIP, DEFAULT_VALUE_LIMIT) ,result.data)
    else renderClubDetailsView(contentHeader, content, result.data)
}

async function createClub(contentHeader, content) {
    const header = "Create a Club"

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

    const fields = [
        {id: "clubName", name: "clubName", label: "Name of the Club", type: "text", required: true }
    ]
    const form = formElement(fields, handleSubmit, {
        className: "form",
        submitText: "Create Clubs"
    })

    const back = a("Back", listClubsUri("", DEFAULT_VALUE_LIMIT, DEFAULT_VALUE_SKIP))

    contentHeader.replaceChildren(header)
    content.replaceChildren(form, back)
}

const clubHandlers= {
    getClubDetails,
    getClubs,
    createClub,
}

export default clubHandlers
