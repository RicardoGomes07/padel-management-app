import Html from "../../utils/htmlfuns.js";
import pagination from "./pagination.js"
import { splitIntoHourlySlots } from "../../utils/auxfuns.js"

const { createPaginationLinks } = pagination
const { div, a, ul, li, p } = Html

function renderCourtsByClubView(contentHeader, content, courts, totalElements, cid, skip, limit) {
    const currHeader = contentHeader.textContent
    const header = "Courts"

    const courtList = courts.length > 0
        ? ul(
            ...courts.map(court =>
                li(a(court.name, `#clubs/${cid}/courts/${court.crid}`))
            )
        )
        : p("No courts found")

    const navigation = createPaginationLinks(`clubs/${cid}/courts`, Number(skip), Number(limit), totalElements)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(courtList, navigation)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid) {
    const header = "Court"
    const info =
        ul(
            li(courtResponse.name),
            li(a("Club", `#clubs/${cid}`)),
            li(a("Court Rentals", `#clubs/${cid}/courts/${crid}/rentals`)),
            li(a("Available Hours", `#clubs/${cid}/courts/${crid}/available`))
        )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, totalElements, cid, crid, skip, limit) {
    const baseLink = `clubs/${cid}/courts/${crid}/rentals`

    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const backLink = div(a("Back", `#clubs/${cid}/courts/${crid}`))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()} ${rental.initialHour} to ${rental.finalHour} `, `#rentals/${rental.rid}`))
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(baseLink, Number(skip), Number(limit), totalElements)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, rentalList, navigation)
}

function renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate) { 
    const header = "Available Hours for " + selectedDate
    const backLink = div(a("Back", `#clubs/${cid}/courts/${crid}`))

    const individualSlots = [];
    availableHours.forEach(interval => {
        individualSlots.push(...splitIntoHourlySlots(interval.start, interval.end));
    });

    const mid = Math.ceil(individualSlots.length / 2);
    const leftSlots = individualSlots.slice(0, mid);
    const rightSlots = individualSlots.slice(mid);

    const leftList = ul(
        ...leftSlots.map(hour =>
            li(
                `${hour.start} `,
                a("Rent", `#clubs/${cid}/courts/${crid}/rentals/create?date=${selectedDate}&start=${hour.start}`)
            )
        )
    );
    const rightList = ul(
        ...rightSlots.map(hour =>
            li(
                `${hour.start} `,
                a("Rent", `#clubs/${cid}/courts/${crid}/rentals/create?date=${selectedDate}&start=${hour.start}`)
            )
        )
    );

    const columns = div({ className: "columns", style: "display: flex; gap: 20px;" }, leftList, rightList);

    contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, columns)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView,
    renderCourtAvailableHoursView
}

export default courtsViews