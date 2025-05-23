import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js"
import uriManager from "../../managers/uriManager.js";

const { createPaginationLinks } = pagination
const { div, a, ul, li, p, formElement, span } = Html
const { getCourtDetailsUri, listClubCourtsUri, getClubDetailsUri, listCourtRentalsUri, searchCourtRentalsUri,
    getCourtAvailableHoursUri, getRentalDetailsUri, getAvailableHoursByDateUri, createRentalUri } = uriManager

function renderCourtsByClubView(contentHeader, content, courts, cid, skip, limit, hasNext) {
    const currHeader = contentHeader.textContent
    const header = "Courts"

    const courtList = courts.length > 0
        ? ul(
            ...courts.map(court =>
                li(a(court.name, getCourtDetailsUri(cid, court.crid)))
            )
        )
        : p("No courts found")

    const back = a("Back", `#clubs/${cid}`)

    const navigation = createPaginationLinks(listClubCourtsUri(cid), Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(courtList, navigation, back)
}

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid, skip, limit) {
    const header = "Court Info"
    const info =
        ul(
            li(`Name : ${courtResponse.name}`),
            li(a("Club", getClubDetailsUri(cid))),
            li(
                span(
                    a("Court Rentals", listCourtRentalsUri(cid, crid, skip, limit)),
                    a("by date", searchCourtRentalsUri(cid, crid)),
                )
            ),
            li(a("Available Hours", getCourtAvailableHoursUri(cid, crid))),
            li(a("Rent Court", createRentalUri(cid, crid))),
        )

    contentHeader.replaceChildren(header)
    content.replaceChildren(info)
}

function renderCourtRentalsView(contentHeader, content, rentals, cid, crid, skip, limit, hasNext) {
    const currHeader = contentHeader.textContent
    const header = "Rentals"

    const backLink = div(a("Back", getCourtDetailsUri(cid, crid)))

    const rentalList = rentals.length > 0
        ? ul(
            ...rentals.map(rental =>
                li(a(`${rental.date.toString()} ${rental.initialHour} to ${rental.finalHour} `,
                    getRentalDetailsUri(cid, crid, rental.rid)
            ))
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(listCourtRentalsUri(cid,crid), Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, rentalList, navigation)
}

function renderCreateCourtForm(contentHeader, content, cid, handleSubmit) {
    const header = "Create Court"

    const fields = [
        { id: "courtName", label: "Name: ", type: "text", required: true, placeholder: "Enter Court Name" },
    ]

    const backLink = div(a("Back", getClubDetailsUri(cid)))
    const children = li(
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Create Court"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

function renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate) {
    const backLink = div(a("Back", getCourtAvailableHoursUri(cid, crid)))

    const hours = div(
        ul(
            ...availableHours.map(range =>
                li(p(`${range.start} to ${range.end}`))
            )
        )
    )

    const headerText = `Available Hours for ${selectedDate}`

    contentHeader.replaceChildren(headerText)
    content.replaceChildren(backLink, hours)
}

function renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid, handleSubmit) {
    const header = "Search Available Hours"

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const children = div(
        a("Back", getCourtDetailsUri(cid, crid)),
        formElement(fields, handleSubmit, {
            className: "form",
            submitText: "Get Available Hours"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children)
}

function renderSearchForCourtsByDateAndTimeSlot(contentHeader, content, cid, submitHandler){
    const backLink = div(a("Back", getClubDetailsUri(cid)))

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
        {id: "startHour", label: "Start Hour", type: "hour", required: true},
        { id: "endHour", label: "End Hour", type: "hour", required: true},
    ]

    const form = formElement(fields, submitHandler)
    contentHeader.replaceChildren("Select a date and a time to search for courts availability")
    content.replaceChildren(backLink,form)
}

function renderAvailableCourtsToRent(contentHeader, content, availableCourts, date, startHour, endHour){
    const header = "Available Courts"
    const courts = ul(
        ...availableCourts.map(court =>
            li(a(court.name, createRentalUri(court.cid, court.crid, date, startHour, endHour)))
        )
    )
    contentHeader.replaceChildren(header)
    content.replaceChildren(courts)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView,
    renderCreateCourtForm,
    renderCourtAvailableHoursView,
    renderCalendarToSearchAvailableHours,
    renderSearchForCourtsByDateAndTimeSlot,
    renderAvailableCourtsToRent,
}

export default courtsViews