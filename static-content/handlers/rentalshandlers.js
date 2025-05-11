import { request } from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import Html from "../utils/htmlfuns.js";
import { parseHourFromString } from "../utils/auxfuns.js";


const { renderRentalDetailsView } = rentalsViews
const { fetchRentalDetails, fetchCreateRental } = rentalsRequests
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

    const token = '307d48b5-a71b-45d4-9a04-755d2871f7b0'; // Hardcoded for testing purposes

    const fields = [
        {id: "finalHour", label: "Final Hour", type: "number", min: 0, max: 23, required: true }
    ]

    const handleSubmit = async function(e){
        e.preventDefault()
        const finalHour = document.querySelector("#finalHour")
        const initialHour = parseHourFromString(start);
        const options = {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept" : "application/json",
                "Authorization": `${token}`
            },
            body: JSON.stringify({
                date: date,
                initialHour: initialHour,
                finalHour: finalHour.value
            })
        }
        const result = await fetchCreateRental(cid, crid, options)
        if (result.status !== 201) {
            console.error(result.data)
            errorView(contentHeader, content, result.data);
            return;
        } else {
            window.location.hash = `#clubs/${cid}/courts/${crid}/rentals`	
        }
    }

    const form = formRequest(fields, handleSubmit, { 
        className: "form",
        submitText: "Create Rental"
     });
    
    contentHeader.replaceChildren("Create Rental")
    content.replaceChildren(form)
}

const rentalshandlers= {
    getRentalDetails,
    createRental
}

export default rentalshandlers;