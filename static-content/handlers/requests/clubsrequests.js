import {API_BASE_URL} from "./home.js";

function fetchClub(cid){
    return fetch(`${API_BASE_URL}clubs/${cid}`).then(res => res.json());
}

function fetchClubs(skip, limit) {
    return fetch(`${API_BASE_URL}clubs?skip=${skip}&limit=${limit}`).then(res => res.json());
}

const clubfetchers = {
    fetchClub,
    fetchClubs
}
export default clubfetchers