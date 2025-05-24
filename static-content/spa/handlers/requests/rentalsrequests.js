import {API_BASE_URL} from "../../managers/uriManager.js";
import { handleResponse } from "./fetch.js";
import {userAuthManager} from "../usershandlers.js";
const userToken = userAuthManager.getCurrToken()

function fetchRentalDetails(cid, crid, rid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals/${rid}`)
        .then(handleResponse)
}

function createRental(cid, crid, date, startTime, endTime) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        },
        body: JSON.stringify({ date: date, initialHour: startTime, finalHour: endTime })
    }).then(handleResponse)
}

function editRental(cid, crid, rid, date, startTime, endTime) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals/${rid}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        },
        body: JSON.stringify({ date: date, initialHour: startTime, finalHour: endTime })
    }).then(handleResponse)
}

function deleteRental(cid, crid, rid) {
    return fetch(`${API_BASE_URL}clubs/${cid}/courts/${crid}/rentals/${rid}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        }
    }).then(handleResponse)
}

const rentalsRequests = {
    fetchRentalDetails,
    createRental,
    editRental,
    deleteRental
}

export default rentalsRequests