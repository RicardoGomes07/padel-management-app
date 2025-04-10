import {request} from "../router.js";
import rentalsViews from "./views/rentalsviews.js";
import rentalsRequests from "./requests/rentalsrequests.js";
const {getRentalDetailsView} = rentalsViews
const {fetchRentalDetails} = rentalsRequests
const {path} = request

async function getRentalDetails(contentHeader, content) {
    const rentalId = path("rid")

    const rental = await fetchRentalDetails(rentalId)
    getRentalDetailsView(contentHeader, content, rental)
}

const rentalshandlers= {
    getRentalDetails,
}

export default rentalshandlers;