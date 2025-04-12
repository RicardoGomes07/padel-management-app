import {API_BASE_URL} from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchCourtsByClubId(cid, skip, limit) {
    return fetch(`${API_BASE_URL}courts/clubs/${cid}?skip=${skip}&limit=${limit*2}`)
        .then(handleResponse)
}

function fetchCourtDetails(courtId) {
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