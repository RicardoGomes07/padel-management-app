import Html from "../../dsl/htmlfuns.js";

const { p, div, a } = Html
const DEFAULT_ERROR_TITLE = "Unknown Error"
const DEFAULT_ERROR_MESSAGE = "Something went wrong"

function errorView(contentHeader, content, backLocation, response={}) {
    const errorTitle = response.title || DEFAULT_ERROR_TITLE
    const errorMessage = response.message || DEFAULT_ERROR_MESSAGE
    const errorDesc = response.description || errorMessage

    const errorDescription = div(
        a("Back", backLocation),
        p(errorDesc)
    )

    contentHeader.replaceChildren(errorTitle)
    content.replaceChildren(errorDescription)
}

const errorsViews = {
    errorView
}

export default errorsViews