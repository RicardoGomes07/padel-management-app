export const API_BASE_URL = "http://localhost:9000/"

const uriManager = {
    home: () => `#home`,
    getUserProfile: (uid) => `#users/${uid}`,

    listClubs: () =>`#clubs`,
    getClubDetails: (cid) => `#clubs/${cid}`,

    listClubCourts: (cid) => `#clubs/${cid}/courts`,
    getCourtDetails: (cid, crid) => `#clubs/${cid}/courts/${crid}`,
    createCourtForm: (cid) => `#clubs/${cid}/courts/create`,

    listCourtRentals: (cid, crid) => `#clubs/${cid}/courts/${crid}/rentals`,
    searchCourtRentals: (cid, crid) => `#clubs/${cid}/courts/${crid}/rentals/search`,
    getRentalDetails: (cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}`,
    createRental: (cid, crid, date, startHour, endHour) =>
        `#clubs/${cid}/courts/${crid}/rentals/create?date=${date}&start=${startHour}&end=${endHour}`,
    updateRental:(cid, crid, rid, date, startHour, endHour) =>
        `#clubs/${cid}/courts/${crid}/rentals/${rid}/update?date=${date}&start=${startHour}&end=${endHour}`,
    deleteRental:(cid, crid, rid) => `#clubs/${cid}/courts/${crid}/rentals/${rid}/delete`,

    searchCourtRentalsByDate: (cid, crid, date) =>
        `#clubs/${cid}/courts/${crid}/rentals/search?date=${date}`,
    getCourtAvailableHours: (cid, crid) =>
        `#clubs/${cid}/courts/${crid}/available_hours`,
    getAvailableHoursByDate: (cid, crid, date) =>
        `#clubs/${cid}/courts/${crid}/available_hours?date=${date}`,
    getAvailableHoursByDateAndStart: (cid, crid, date, hour) =>
        `#clubs/${cid}/courts/${crid}/rentals/create?date=${date}&start=${hour}`,
}

export default uriManager