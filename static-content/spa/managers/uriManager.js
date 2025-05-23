export const API_BASE_URL = "http://localhost:9000/"

const uriManager = {
    homeUri: () => `#home`,
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
    updateRentalUri:(cid, crid, rid, date, startHour, endHour) =>
        `#clubs/${cid}/courts/${crid}/rentals/${rid}/update?date=${date}&start=${startHour}&end=${endHour}`,
    deleteRentalUri:(cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}/delete`,

    searchCourtRentalsByDateUri: (cid, crid, date) =>
        `#clubs/${cid}/courts/${crid}/rentals/search?date=${date}`,
    getCourtAvailableHoursUri: (cid, crid) =>
        `#clubs/${cid}/courts/${crid}/available_hours`,
    getAvailableHoursByDateUri: (cid, crid, date) =>
        `#clubs/${cid}/courts/${crid}/available_hours?date=${date}`,
    getAvailableHoursByDateAndStartUri: (cid, crid, date, hour) =>
        `#clubs/${cid}/courts/${crid}/rentals/create?date=${date}&start=${hour}`,
}

export default uriManager