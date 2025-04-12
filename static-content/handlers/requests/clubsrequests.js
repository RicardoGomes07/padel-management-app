import {API_BASE_URL} from "../home.js";
import { handleResponse } from "../../utils/fetch.js"

function fetchClub(cid){
    return fetch(`${API_BASE_URL}clubs/${cid}`).then(handleResponse)
}

function fetchClubs(skip, limit) {
    return fetch(`${API_BASE_URL}clubs?skip=${skip}&limit=${limit*2}`)
        .then(handleResponse)
}

const clubFetchers = {
    fetchClub,
    fetchClubs
}
export default clubFetchers