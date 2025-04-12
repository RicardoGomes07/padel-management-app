import {request} from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";

const {getRentalDetailsView} = rentalsViews
const {fetchRentalDetails} = rentalsRequests
const { errorView } = errorsViews
const {path} = request

async function getRentalDetails(contentHeader, content) {
    const rentalId = path("rid")

    const result = await fetchRentalDetails(rentalId)
    if(result.status !== 200) errorView(result.data, content)
    getRentalDetailsView(contentHeader, content, result.data)
}

const rentalshandlers= {
    getRentalDetails,
}

export default rentalshandlers;