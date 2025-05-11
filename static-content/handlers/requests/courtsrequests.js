import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchCourtsByClub(cid, skip, limit) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchCourtDetails(cid, crid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}`)
        .then(handleResponse)
}

function fetchCourtRentals(cid, crid, skip, limit) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchCourtsAvailableHours(cid, crid, options) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/available`, options)
        .then(handleResponse)
}

const courtsRequests = {
    fetchCourtsByClub,
    fetchCourtDetails,
    fetchCourtRentals,
    fetchCourtsAvailableHours
}

export default courtsRequests