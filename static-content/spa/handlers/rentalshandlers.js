import { redirectTo, request } from "../router.js"
import rentalsViews from "./views/rentalsviews.js"
import rentalsRequests from "./requests/rentalsrequests.js"
import errorsViews from "./views/errorsview.js";
import courtsRequests from "./requests/courtsrequests.js";
import courtsViews from "./views/courtsviews.js";
import uriManager from "../managers/uriManager.js";
import auxfuns from "./auxFuns.js";
import { authenticated } from "../managers/userAuthenticationManager.js";
import errorManager from "../managers/errorManager.js";

const { renderRentalDetailsView, renderCalendarToSearchRentals, renderUpdateRentalView, renderRentalCreationForm } = rentalsViews
const { fetchRentalDetails} = rentalsRequests
const { errorView } = errorsViews
const { path, query } = request
const { renderCourtRentalsView } = courtsViews
const { listCourtRentalsUri, getRentalDetailsUri, loginUri} = uriManager
const { parseHourFromString } = auxfuns

const rentalsOfCourtPagination = createPaginationManager(courtsRequests.fetchCourtRentals, "rentals")

async function getRentalDetails(contentHeader, content) {
    const clubId = Number(path("cid"))
    const courtId = Number(path("crid"))
    const rentalId = Number(path("rid"))

    const result = await fetchRentalDetails(clubId, courtId, rentalId)

    if (result.status === 200){
        renderRentalDetailsView(contentHeader, content, result.data)
    } else{
        errorManager.store(errorView(result.data))
        redirectTo(listCourtRentalsUri(clubId,courtId))
    }
}

async function createRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))

    if(!authenticated()){
        redirectTo(loginUri())
        return
    }

    const date = query("date")
    const startHour = query("start")
    const endHour = query("end")

    const rentalInfo = {date: null, startHour: null, endHour: null}
    if(date != null && startHour != null && endHour != null){
        rentalInfo.date = date
        rentalInfo.startHour = startHour
        rentalInfo.endHour = endHour
    }

    const onSubmit = async function(e){
        e.preventDefault()
        const date = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value

        if (Number(startHour) >= Number(endHour)) {
            alert("Start hour must be earlier than end hour.")
            return
        }

        const response = await rentalsRequests.createRental(cid, crid, date, parseHourFromString(startHour), parseHourFromString(endHour))

        if (response.status === 201) redirectTo(getRentalDetailsUri(cid, crid, response.data.rid))
        else errorManager.store(errorView(response.data)).render()
    }

    renderRentalCreationForm(contentHeader, content, cid, crid, rentalInfo, onSubmit)

}

async function updateRental(contentHeader, content) {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))

    if(!authenticated()){
        redirectTo(loginUri())
        return
    }

    const handleSubmit = async function(e) {
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value
        const startHour = e.target.querySelector("#startHour").value
        const endHour = e.target.querySelector("#endHour").value

        if (Number(startHour) >= Number(endHour)) {
            alert("Start hour must be earlier than end hour.")
            return
        }

        const response = await rentalsRequests.editRental(cid, crid, rid, validDate, startHour, endHour)

        if (response.status === 200) redirectTo(getRentalDetailsUri(cid, crid, rid))
        else errorManager.store(errorView(response.data)).render()
    }

    const response = await rentalsRequests.fetchRentalDetails(cid, crid, rid)
    if (response.status === 200){
        renderUpdateRentalView(contentHeader, content, response.data, handleSubmit)
    } else {
        errorManager.store(errorView(response.data))
        redirectTo(getRentalDetailsUri(cid, crid, rid))
    }

}

async function deleteRental() {
    const cid = Number(path("cid"))
    const crid = Number(path("crid"))
    const rid = Number(path("rid"))
    const page = Number(query("page")) || 1

    if(!authenticated()){
        redirectTo(loginUri())
        return
    }

    const result = await rentalsRequests.deleteRental(cid, crid, rid)

    if (result.status === 200){
        redirectTo(listCourtRentalsUri(cid, crid, page))
    } else{
        errorManager.store(errorView(result.data)) // Se if error is being shown correctly
        redirectTo(getRentalDetailsUri(cid, crid, rid))
    }
}

function searchRentals(contentHeader, content) {
    const cid = path("cid")
    const crid = path("crid")

    const page = Number(query("page")) || 1

    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = e.target.querySelector("#date").value

        const [rentals, count] = await rentalsOfCourtPagination
            .reqParams(cid, crid, validDate)
            .getPage(page,
               (message) => { errorManager.store(errorView(message)).render() }
            )

        renderCourtRentalsView(
            contentHeader,
            content,
            rentals,
            count,
            cid,
            crid,
            page
        )
    }

    renderCalendarToSearchRentals(contentHeader, content, cid, crid, handleSubmit)
}

const rentalsHandlers= {
    getRentalDetails,
    createRental,
    updateRental,
    deleteRental,
    searchRentals
}

export default rentalsHandlers
