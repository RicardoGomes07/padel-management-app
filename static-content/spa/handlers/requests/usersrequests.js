import {API_BASE_URL} from "../../managers/uriManager.js";
import { handleResponse } from "./fetch.js";
import {userAuthManager} from "../usershandlers.js";



function fetchUserRentals(uid, skip, limit) {
    return fetch(`${API_BASE_URL}users/${uid}/rentals?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

function fetchUserDetails(uid) {
    return fetch(`${API_BASE_URL}users/${uid}`)
        .then(handleResponse)
}

function createUser(name, email, password) {
    return fetch(`${API_BASE_URL}users`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, password })
    }).then(handleResponse)
}

function loginUser(email, password) {
    return fetch(`${API_BASE_URL}users/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password })
    }).then(handleResponse)
}

function logoutUser() {
    return fetch(`${API_BASE_URL}users/logout`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": userAuthManager.getCurrToken(),
        }
    }).then(handleResponse)
}

const usersRequests = {
    fetchUserRentals,
    fetchUserDetails,
    createUser,
    loginUser,
    logoutUser
}

export default usersRequests