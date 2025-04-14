import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js";

function fetchRentalDetails(rid) {
    return fetch(`${API_BASE_URL}rentals/${rid}`)
        .then(handleResponse)
}

const rentalsRequests = {
    fetchRentalDetails,
}

export default rentalsRequests