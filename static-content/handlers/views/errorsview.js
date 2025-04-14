import Html from "../../utils/htmlfuns.js";

const { p } = Html

function errorView(contentHeader, content, response) {
    const errorDescription = p(response.description)

    contentHeader.replaceChildren(response.title)
    content.replaceChildren(errorDescription)
}

const errorsViews = {
    errorView
}

export default errorsViews