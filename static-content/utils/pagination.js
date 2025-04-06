import Html from "./htmlfuns.js";
const { div, a} = Html;

const DEFAULT_VALUE_SKIP = "0"
const DEFAULT_VALUE_LIMIT = "1"

function createPaginationLinks(baseLink, skip, limit, totalElements) {
    const nextSkip = skip + limit
    const prevSkip = skip - limit

    const nextLink = a("Next", `#${baseLink}?skip=${nextSkip}&limit=${limit}`)
    const prevLink = a("Prev", `#${baseLink}?skip=${prevSkip}&limit=${limit}`)

    if (skip <= 0) prevLink.style.display = "none"
    if (nextSkip >= totalElements) nextLink.style.display = "none"

    return div(prevLink, nextLink)
}

const pagination = {
    DEFAULT_VALUE_LIMIT,
    DEFAULT_VALUE_SKIP,
    createPaginationLinks,
}
export default pagination

