import Html from "../../dsl/htmlfuns.js"
import classnames from "./classnames.js";


const { div, a } = Html
const DEFAULT_VALUE_SKIP = 0
const DEFAULT_VALUE_LIMIT = 1
const { paginationLinkClassName} = classnames

function createPaginationLinks(baseLink, skip, limit, hasNext) {
    const nextSkip = skip + limit
    const prevSkip = Math.max(0, skip - limit)

    const children = []

    if (skip > 0) {
        // chop the base link starting on skip, it's safe as skip and limit are always the last 2 parameters
        const indexOfSkip = baseLink.lastIndexOf("skip=");
        const baseWithoutSkipLimit = baseLink.substring(0, indexOfSkip);
        const prevPath = `${baseWithoutSkipLimit}skip=${prevSkip}&limit=${limit}`
        const prevLink = a("Prev", prevPath, paginationLinkClassName)
        children.push(prevLink)
    }

    if (hasNext) {
        const indexOfSkip = baseLink.lastIndexOf("skip=");
        const baseWithoutSkipLimit = baseLink.substring(0, indexOfSkip);
        const nextPath = `${baseWithoutSkipLimit}skip=${nextSkip}&limit=${limit}`
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

