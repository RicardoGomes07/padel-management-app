import Html from "../../dsl/htmlfuns.js"
import classnames from "./classnames.js";

const { div, a } = Html
const DEFAULT_VALUE_SKIP = 0
const DEFAULT_VALUE_LIMIT = 1
const { paginationLinkClassName} = classnames

export const ELEMS_PER_PAGE = 2
const SIDE_PAGES_RANGE = 3

function createPaginationLinks(baseLink, count, page) {
    const maxNumOfPages = Math.ceil(count / ELEMS_PER_PAGE)
    const children = []

    const firstPage = Math.max(1, page - SIDE_PAGES_RANGE - 1)
    const lastPage = Math.min(maxNumOfPages, page + SIDE_PAGES_RANGE + 1)

    // "..." and numbered pages before current page
    if (firstPage > 1) {
        children.push(makePageLink(baseLink, "...", firstPage - 1));
        for (let currPage = firstPage; currPage < page - 1; currPage++) {
            children.push(makePageLink(baseLink, `${currPage}`, currPage))
        }
    } else {
        // there is no "..."
        for (let currPage = 1; currPage < page - 1; currPage++) {
            children.push(makePageLink(baseLink,`${currPage}`, currPage))
        }
    }

    // prev button
    if (page > 1) {
        children.push(makePageLink(baseLink, "Prev", page - 1))
    }

    // curr button
    children.push(makePageLink(baseLink, "Curr", page))

    // next button
    if (page < maxNumOfPages) {
        children.push(makePageLink(baseLink, "Next", page + 1))
    }

    // numbered buttons
    for (let currPage = page + 2; currPage <= lastPage; currPage++) {
        children.push(makePageLink(baseLink,`${currPage}`, currPage))
    }

    // "..." button
    if (lastPage < maxNumOfPages) {
        children.push(makePageLink(baseLink,"...",lastPage + 1))
    }

    return div(...children)
}


function makePageLink(baseLink, label, targetPage) {
    const indexOfPage = baseLink.lastIndexOf("page=");
    const baseWithoutPage = baseLink.substring(0, indexOfPage);
    const path = `${baseWithoutPage}page=${targetPage}`;
    return a(label, path, paginationLinkClassName);
}

const pagination = {
    DEFAULT_VALUE_LIMIT,
    DEFAULT_VALUE_SKIP,
    createPaginationLinks,
}
export default pagination

