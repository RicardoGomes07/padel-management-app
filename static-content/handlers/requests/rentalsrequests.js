import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js";

function fetchRentalDetails(cid, crid, rid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals/${rid}`)
        .then(handleResponse)
}

const rentalsRequests = {
    fetchRentalDetails,
}

export default rentalsRequests