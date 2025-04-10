import {request} from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview";

const {getRentalDetailsView} = rentalsViews
const {fetchRentalDetails} = rentalsRequests
const { errorView } = errorsViews
const {path} = request

async function getRentalDetails(contentHeader, content) {
    const rentalId = path("rid")

    const rental = await fetchRentalDetails(rentalId)
    if(rental.status !== 200) errorView(rental.data, content)
    getRentalDetailsView(contentHeader, content, rental)
}

const rentalshandlers= {
    getRentalDetails,
}

export default rentalshandlers;