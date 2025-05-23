export const API_BASE_URL = "http://localhost:9000/"

const uriManager = {
    homeUri: () => `#home`,
    getUserProfileUri: (uid) => `#users/${uid}`,
    getUserRentalsUri: (uid, skip, limit) => `#users/${uid}/rentals?skip=${skip}&limit=${limit}`,

    listClubsUri: (name, skip, limit) =>`#clubs?${name ? `name=${name}&` : ``}skip=${skip}&limit=${limit}`,
    getClubDetailsUri: (cid) => `#clubs/${cid}`,
    createClubUri: () => `#clubs/create`,

    listClubCourtsUri: (cid, skip, limit) => `#clubs/${cid}/courts?skip=${skip}&limit=${limit}`,
    getCourtDetailsUri: (cid, crid) => `#clubs/${cid}/courts/${crid}`,
    createCourtFromUri: (cid) => `#clubs/${cid}/courts/create`,

    searchCourtsToRentUri: (cid) => `#clubs/${cid}/courts/rent`,
    listCourtRentalsUri: (cid, crid, skip, limit) => `#clubs/${cid}/courts/${crid}/rentals?skip=${skip}&limit=${limit}`,
    searchCourtRentalsUri: (cid, crid) => `#clubs/${cid}/courts/${crid}/rentals/search`,
    getRentalDetailsUri: (cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}`,
    createRentalUri: (cid, crid, date, startHour, endHour) =>
        `#clubs/${cid}/courts/${crid}/rentals/create?date=${date}&start=${startHour}&end=${endHour}`,
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