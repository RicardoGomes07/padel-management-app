import {API_BASE_URL} from "../home";
import {handleResponse} from "../../utils/fetch";

function fetchUserRentals(userId, skip, limit) {
    return fetch(API_BASE_URL + "users/" + userId + "/rentals" + "?skip=" + skip*2 + "&limit=" + limit*2)
        .then(handleResponse)
}

function fetchUserDetails(userId) {
    return fetch(API_BASE_URL + "users/" + userId)
        .then(handleResponse)
}

const usersRequests = {
    fetchUserRentals,
    fetchUserDetails,
}

export default usersRequests;