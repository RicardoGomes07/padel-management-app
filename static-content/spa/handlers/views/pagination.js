import Html from "../../dsl/htmlfuns.js"
import classnames from "./classnames.js";

const { div, a, span } = Html
const DEFAULT_VALUE_SKIP = 0
const DEFAULT_VALUE_LIMIT = 1
const { paginationLinkClassName} = classnames

export const ELEMS_PER_PAGE = 2
const SIDE_PAGES_RANGE = 3

function createPaginationLinks(baseLink, count, currentPage) {
    const totalPages = Math.ceil(count / ELEMS_PER_PAGE)
    const children = []

    const startPage = Math.max(1, currentPage - SIDE_PAGES_RANGE)
    const endPage = Math.min(totalPages, currentPage + SIDE_PAGES_RANGE)

    // Add "..." before
    if (startPage > 1) {
        children.push(makePageLink(baseLink, "1", 1))
        if (startPage > 2) {
            children.push(makePageLink(baseLink, "...", startPage - 1))
        }
    }

    // Page links around current page
    for (let page = startPage; page <= endPage; page++) {
        children.push(makePageLink(baseLink, `${page}`, page, currentPage))
    }

    // Add "..." after
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            children.push(makePageLink(baseLink, "...", endPage + 1))
        }
        children.push(makePageLink(baseLink, `${totalPages}`, totalPages))
    }

    return div(...children)
}

function makePageLink(baseLink, label, targetPage, currentPage = null) {
    const isCurrent = parseInt(label) === currentPage

    if (isCurrent) {
        return span(label)
    }

    const indexOfPage = baseLink.lastIndexOf("page=")
    const baseWithoutPage = indexOfPage !== -1
        ? baseLink.substring(0, indexOfPage)
        : baseLink

    const href = `${baseWithoutPage}page=${targetPage}`

    return a(label, href, paginationLinkClassName)
}

const pagination = {
    DEFAULT_VALUE_LIMIT,
    DEFAULT_VALUE_SKIP,
    createPaginationLinks,
}
export default pagination