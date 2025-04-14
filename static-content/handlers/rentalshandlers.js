import { request } from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";

const { renderRentalDetailsView } = rentalsViews
const { fetchRentalDetails } = rentalsRequests
const { errorView } = errorsViews
const { path } = request

async function getRentalDetails(contentHeader, content) {
    const rentalId = path("rid")

    const result = await fetchRentalDetails(rentalId)

    if(result.status !== 200) errorView(content, result.data)
    renderRentalDetailsView(contentHeader, content, result.data)
}

const rentalshandlers= {
    getRentalDetails,
}

export default rentalshandlers;