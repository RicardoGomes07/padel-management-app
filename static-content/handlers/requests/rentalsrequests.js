import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js";

function fetchRentalDetails(cid, crid, rid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals/${rid}`)
        .then(handleResponse)
}

function fetchCreateRental(cid, crid, options) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals`, options)
        .then(handleResponse)
}

const rentalsRequests = {
    fetchRentalDetails,
    fetchCreateRental
}

export default rentalsRequests