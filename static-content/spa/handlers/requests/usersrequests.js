import { API_BASE_URL } from "../home.js";
import { handleResponse } from "./fetch.js";

function fetchUserRentals(uid, skip, limit) {
    return fetch(`${API_BASE_URL}users/${uid}/rentals?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchUserDetails(uid) {
    return fetch(`${API_BASE_URL}users/${uid}`)
        .then(handleResponse)
}

const usersRequests = {
    fetchUserRentals,
    fetchUserDetails,
}

export default usersRequests