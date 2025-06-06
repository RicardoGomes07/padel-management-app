import Html from "../../dsl/htmlfuns.js";

const { p, div} = Html
const DEFAULT_ERROR_TITLE = "Unknown Error"
const DEFAULT_ERROR_MESSAGE = "Something went wrong"

function errorView(response={}) {
    const errorTitle = response.title || DEFAULT_ERROR_TITLE
    const errorMessage = response.message || DEFAULT_ERROR_MESSAGE
    const errorDesc = response.description || errorMessage

    return div(p(errorTitle + ":"), p(errorDesc))
}

const errorsViews = {
    errorView
}

export default errorsViews