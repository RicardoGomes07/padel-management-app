import Html from "../../utils/htmlfuns";

const { h1 , p} = Html

function errorView(response, mainContent) {
    const errorTitle = h1(response.title)
    const errorDescription = p(response.description)
    mainContent.replaceChildren(errorTitle, errorDescription)
}

const errorsViews = {
    errorView
}

export default errorsViews