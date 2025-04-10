import {API_BASE_URL} from "../home.js";

function fetchRentalDetails(rentalId) {
    return fetch(`${API_BASE_URL}rentals/${rentalId}`)
        .then(res => res.json())
}

const rentalsRequests = {
    fetchRentalDetails,
}

export default rentalsRequests