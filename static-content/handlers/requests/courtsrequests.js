import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchCourtsByClub(cid, skip, limit) {
    return fetch(`${API_BASE_URL}courts/clubs/${cid}?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchCourtDetails(cid) {
    return fetch(`${API_BASE_URL}courts/${cid}`)
        .then(handleResponse)
}

function fetchCourtRentals(crid, skip, limit) {
    return fetch(`${API_BASE_URL}rentals/courts/${crid}/rentals?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

const courtsRequests = {
    fetchCourtsByClub,
    fetchCourtDetails,
    fetchCourtRentals
}

export default courtsRequests