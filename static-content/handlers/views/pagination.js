import Html from "../../utils/htmlfuns.js"
import classnames from "./classnames.js";


const { div, a } = Html
const DEFAULT_VALUE_SKIP = 0
const DEFAULT_VALUE_LIMIT = 1
const { paginationLinksClassName, paginationLinkClassName} = classnames

function createPaginationLinks(baseLink, skip, limit, totalElements) {
    const nextSkip = skip + limit
    const prevSkip = Math.max(0, skip - limit)

    const children = []

    if (skip > 0) {
        const prevPath = `#${baseLink}?skip=${prevSkip}&limit=${limit}`
        const prevLink = a("Prev", prevPath, paginationLinkClassName)
        children.push(prevLink)
    }

    if (nextSkip < totalElements) {
        const nextPath = `#${baseLink}?skip=${nextSkip}&limit=${limit}`
        const nextLink = a("Next", nextPath, paginationLinkClassName)
        children.push(nextLink)
    }

    return div(...children)
}

const pagination = {
    DEFAULT_VALUE_LIMIT,
    DEFAULT_VALUE_SKIP,
    createPaginationLinks,
}
export default pagination

