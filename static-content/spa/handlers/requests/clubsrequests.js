import {API_BASE_URL} from "../../managers/uriManager.js";
import { handleResponse } from "./fetch.js"
import {userAuthManager} from "../usershandlers.js";

const userToken = userAuthManager.getCurrToken()

function fetchClubDetails(cid){
    return fetch(`${API_BASE_URL}clubs/${cid}`)
        .then(handleResponse)
}

function fetchClubs(name, skip, limit) {
    const url = `${API_BASE_URL}clubs?${name ? `name=${name}&` : ``}skip=${skip}&limit=${limit}`
    return fetch(url)
        .then(handleResponse)
}

function createClub(clubName) {
    return fetch(`${API_BASE_URL}clubs`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${userToken}`,
        },
        body: JSON.stringify({ name: clubName })
    }).then(handleResponse)
}

const clubsRequests = {
    fetchClubDetails,
    fetchClubs,
    createClub
}
export default clubsRequests