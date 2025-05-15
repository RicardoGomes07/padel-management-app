import { API_BASE_URL } from "../home.js";
import { handleResponse } from "./fetch.js"
import {userAuthManager} from "../usershandlers.js";
const userToken = userAuthManager.getCurrToken()

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

function fetchCourtRentalsByDate(cid, crid, skip, limit, date) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals?skip=${skip}&limit=${limit}&date=${date}`)
        .then(handleResponse)
}

function createCourt(clubId, courtName) {
    return fetch(`${API_BASE_URL}clubs/${clubId}/courts`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        },
        body: JSON.stringify({ name: courtName })
    }).then(handleResponse)
}

function getAvailableHours(cid, crid, date) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/available`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        },
        body: JSON.stringify({ date: date })
    }).then(handleResponse)
}

const courtsRequests = {
    fetchCourtsByClub,
    fetchCourtDetails,
    fetchCourtRentals,
    getAvailableHours, 
    createCourt,
    fetchCourtRentalsByDate
}

export default courtsRequests