import {API_BASE_URL} from "../home";
import { handleResponse } from "../../utils/fetch.js"

function fetchCourtsByClubId(clubId, skip, limit) {
    return fetch(`${API_BASE_URL}courts/clubs/${cid}?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchCourtDetails(clubId, courtId) {
    return fetch(`${API_BASE_URL}courts/${courtId}`)
        .then(handleResponse)
}

function fetchCourtRentals(clubId, courtId) {
    return fetch(`${API_BASE_URL}clubs/${clubId}/courts/${courtId}/rentals`)
        .then(handleResponse)
}

const courtsRequests = {
    fetchCourtsByClubId,
    fetchCourtDetails,
    fetchCourtRentals
}

export default courtsRequests