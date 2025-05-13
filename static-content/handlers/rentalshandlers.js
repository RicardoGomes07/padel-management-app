import { request } from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import {parseHourFromString} from "../utils/auxfuns.js";
import Html from "../utils/htmlfuns.js";

const { renderRentalDetailsView } = rentalsViews
const { fetchRentalDetails } = rentalsRequests
const { errorView } = errorsViews
const { path, query } = request
const { formRequest } = Html

async function getRentalDetails(contentHeader, content) {
    const rentalId = path("rid")

    const result = await fetchRentalDetails(rentalId)

    if(result.status !== 200) errorView(content, result.data)
    renderRentalDetailsView(contentHeader, content, result.data)
}

function createRental(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")
    const date = query("date")
    const start = query("start")

    const handleSubmit = async function(e){
        e.preventDefault()
        const finalHour = document.querySelector("#finalHour")
        const initialHour = parseHourFromString(start);
        const result = await rentalsRequests.createRental(cid, crid, date, initialHour, finalHour.value)
        if (result.status !== 201) {
            errorView(contentHeader, content, result.data)
        } else {
            window.location.hash = `#clubs/${cid}/courts/${crid}/rentals`
        }
    }

    const fields = [
        {id: "finalHour", label: "Final Hour", type: "number", min: 0, max: 23, required: true }
    ]
    const form = formRequest(fields, handleSubmit, {
        className: "form",
        submitText: "Create Rental"
    })

    contentHeader.replaceChildren("Create Rental")
    content.replaceChildren(form)
}

function updateRental(contentHeader, content) {
    // TODO: Implement the update rental functionality
}

function deleteRental(contentHeader, content) {
    // TODO: Implement the delete rental functionality
}

function searchRentals(contentHeader, content) {
    // TODO: Implement the search rentals by date functionality
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers;