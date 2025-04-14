import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchCourtsByClub(cid, skip, limit) {
    return fetch(`${API_BASE_URL}courts/clubs/${cid}?skip=${skip}&limit=${limit*2}`)
        .then(handleResponse)
}

function fetchCourtDetails(cid) {
    return fetch(`${API_BASE_URL}courts/${cid}`)
        .then(handleResponse)
}

function fetchCourtRentals(cid, crid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals`)
        .then(handleResponse)
}

const courtsRequests = {
    fetchCourtsByClub,
    fetchCourtDetails,
    fetchCourtRentals
}

export default courtsRequests