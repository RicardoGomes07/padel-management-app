import {API_BASE_URL} from "../home";

function fetchUserRentals(userId, skip, limit) {
    return fetch(API_BASE_URL + "users/" + userId + "/rentals" + "?skip=" + skip*2 + "&limit=" + limit*2)
        .then(res => res.json())
}

function fetchUserDetails(userId) {
    return fetch(API_BASE_URL + "users/" + userId)
        .then(res => res.json())
}

const usersRequests = {
    fetchUserRentals,
    fetchUserDetails,
}

export default usersRequests;