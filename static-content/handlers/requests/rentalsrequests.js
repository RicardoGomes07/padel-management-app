import {API_BASE_URL} from "../home.js";
import {handleResponse} from "../../utils/fetch";

function fetchRentalDetails(rentalId) {
    return fetch(`${API_BASE_URL}rentals/${rentalId}`)
        .then(handleResponse)
}

const rentalsRequests = {
    fetchRentalDetails,
}

export default rentalsRequests