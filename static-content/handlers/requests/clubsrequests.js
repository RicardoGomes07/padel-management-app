import { API_BASE_URL } from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchClubDetails(cid){
    return fetch(`${API_BASE_URL}clubs/${cid}`)
        .then(handleResponse)
}

function fetchClubs(skip, limit) {
    return fetch(`${API_BASE_URL}clubs?skip=${skip}&limit=${limit}`)
        .then(handleResponse)
}

const clubsRequests = {
    fetchClubDetails,
    fetchClubs
}
export default clubsRequests