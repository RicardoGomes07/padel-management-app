export const API_BASE_URL = "https://service-ls-2425-2-42d-g10-3mlh.onrender.com/"// To run locally use this uri "http://localhost:9000/"

const uriManager = {
    homeUri: () => `#home`,
    signUpUri: () => `#auth/signup`,
    loginUri: () => `#auth/login`,
    logoutUri: () => `#auth/logout`,
    getUserProfileUri: (uid) => `#users/${uid}`,
    getUserRentalsUri: (uid, page=1) =>
        `#users/${uid}/rentals?page=${page}`,

    listClubsUri: (name="", page=1) =>
        `#clubs?${name ? `name=${name}&` : ``}page=${page}`,
    getClubDetailsUri: (cid) => `#clubs/${cid}`,
    createClubUri: () => `#clubs/create`,

    listClubCourtsUri: (cid, page=1) =>
        `#clubs/${cid}/courts?page=${page}`,
    getCourtDetailsUri: (cid, crid) => `#clubs/${cid}/courts/${crid}`,
    createCourtFromUri: (cid) => `#clubs/${cid}/courts/create`,

    searchCourtsToRentUri: (cid) => `#clubs/${cid}/courts/rent`,
    listCourtRentalsUri: (cid, crid, page=1) =>
        `#clubs/${cid}/courts/${crid}/rentals?page=${page}`,
    searchCourtRentalsUri: (cid, crid) => `#clubs/${cid}/courts/${crid}/rentals/search`,
    getRentalDetailsUri: (cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}`,
    createRentalUri: (cid, crid, date, startHour, endHour) =>
        `#clubs/${cid}/courts/${crid}/rentals/create${date && startHour && endHour ? `?date=${date}&start=${startHour}&end=${endHour}` : ``}`,
    updateRentalIntentUri: (cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}/update`,
    deleteRentalUri:(cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}/delete`,

    getCourtAvailableHoursUri: (cid, crid) =>
        `#clubs/${cid}/courts/${crid}/available_hours`,
    getAvailableHoursByDateUri: (cid, crid, date) =>
        `#clubs/${cid}/courts/${crid}/available_hours?date=${date}`,
    getAvailableHoursByDateAndRangeUri: (cid, crid, date, range) =>
        `#clubs/${cid}/courts/${crid}/available_hours?date=${date}&range=${range}`,
}

export default uriManager