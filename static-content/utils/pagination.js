import Html from "./htmlfuns.js";
const { div, a } = Html;

const DEFAULT_VALUE_SKIP = "0"
const DEFAULT_VALUE_LIMIT = "1"

function createPaginationLinks(baseLink, skip, limit, totalElements, onLinkClick) {
    const nextSkip = skip + limit
    const prevSkip = skip - limit

    const nextPath = `#${baseLink}?skip=${nextSkip}&limit=${limit}`
    const prevPath = `#${baseLink}?skip=${prevSkip}&limit=${limit}`

    const nextLink = a("Next", nextPath)
    const prevLink = a("Prev", prevPath)

    if (skip <= 0) prevLink.style.display = "none"
    if (nextSkip >= totalElements) nextLink.style.display = "none"

    nextLink.addEventListener('click', (_) => {
        onLinkClick('next', nextPath); // Call the provided action with 'next' and nextSkip
    });

    prevLink.addEventListener('click', (_) => {
        onLinkClick('prev', prevPath); // Call the provided action with 'prev' and prevSkip
    });

    return div(prevLink, nextLink)
}

const pagination = {
    DEFAULT_VALUE_LIMIT,
    DEFAULT_VALUE_SKIP,
    createPaginationLinks,
}
export default pagination

