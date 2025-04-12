import Html from "../../utils/htmlfuns.js";

const {p} = Html

function errorView(response, contentHeader, content) {
    const errorDescription = p(response.description)

    contentHeader.replaceChildren(response.title)
    content.replaceChildren(errorDescription)
}

const errorsViews = {
    errorView
}

export default errorsViews