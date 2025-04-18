import Html from "../../utils/htmlfuns.js";

const { p } = Html
const DEFAULT_ERROR_TITLE = "Unknown Error"
const DEFAULT_ERROR_MESSAGE = "Something went wrong"

function errorView(contentHeader, content, response) {
    const errorTitle = response.title || DEFAULT_ERROR_TITLE
    const errorDesc = response.description || DEFAULT_ERROR_MESSAGE + response.error.message

    const errorDescription = p(errorDesc)

    contentHeader.replaceChildren(errorTitle)
    content.replaceChildren(errorDescription)
}

const errorsViews = {
    errorView
}

export default errorsViews