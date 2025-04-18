import Html from "./htmlfuns.js"

const { div, a } = Html
const DEFAULT_VALUE_SKIP = 0
const DEFAULT_VALUE_LIMIT = 1

function createPaginationLinks(baseLink, skip, limit, totalElements) {
    const nextSkip = skip + limit
    const prevSkip = Math.max(0, skip - limit)

    const nextPath = `#${baseLink}?skip=${nextSkip}&limit=${limit}`
    const prevPath = `#${baseLink}?skip=${prevSkip}&limit=${limit}`

    const nextLink = a("Next", nextPath)
    const prevLink = a("Prev", prevPath)

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

