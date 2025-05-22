import Html from "../../dsl/htmlfuns.js";
import pagination from "./pagination.js"
import auxiliaryFuns from "../auxfuns.js"
import uriManager from "../../managers/uriManager.js";

const { splitIntoHourlySlots } = auxiliaryFuns
const { createPaginationLinks } = pagination
const { div, a, ul, li, p, formRequest, span } = Html
const { getCourtDetailsUri, listClubCourtsUri, getClubDetailsUri, listCourtRentalsUri, searchCourtRentalsUri,
    getCourtAvailableHoursUri, getRentalDetailsUri, createRentalUri, getAvailableHoursByDateAndStartUri,
    getAvailableHoursByDateUri }
    = uriManager

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

function renderCourtDetailsView(contentHeader, content, courtResponse, cid, crid) {
    const header = "Court Info"
    const info =
        ul(
            li(`Name : ${courtResponse.name}`),
            li(a("Club", getClubDetailsUri(cid))),
            li(
                span(
                    a("Court Rentals", listCourtRentalsUri(cid, crid)),
                    a("by date", searchCourtRentalsUri(cid, crid)),
                )
            ),
            li(a("Available Hours", getCourtAvailableHoursUri(cid, crid)))
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
                li(a(`${rental.date.toString()} ${rental.initialHour} to ${rental.finalHour} `, getRentalDetailsUri))
            )
        )
        : p("No rentals found")

    const navigation = createPaginationLinks(listCourtRentalsUri(cid,crid), Number(skip), Number(limit), hasNext)

    if (currHeader !== header) contentHeader.replaceChildren(header)
    content.replaceChildren(backLink, rentalList, navigation)
}

function renderCreateCourtForm(contentHeader, content, cid) {
    const header = "Create Court"
    const handleSubmit = function (e) {
        e.preventDefault()
        const courtName = e.target.querySelector("#courtName").value
        window.location.hash =  `#clubs/${cid}/courts/create?name=${courtName}`
    }

    const fields = [
        { id: "courtName", label: "Name: ", type: "text", required: true, placeholder: "Enter Court Name" },
    ]

    const backLink = div(a("Back", getClubDetailsUri(cid)))
    const children = li(
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Create Court"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children, backLink)
}

function renderCourtAvailableHoursView(contentHeader, content, availableHours, cid, crid, selectedDate) {
    const backLink = div(a("Back", getCourtAvailableHoursUri(cid, crid)))

    const individualSlots = availableHours.flatMap(interval =>
        splitIntoHourlySlots(interval.start, interval.end)
    )

    const mid = Math.ceil(individualSlots.length / 2)
    const [leftSlots, rightSlots] = [individualSlots.slice(0, mid), individualSlots.slice(mid)]

    const renderSlot = hour =>
        li(
            `${hour} `,
                 a("Rent", getAvailableHoursByDateAndStartUri(cid,crid,selectedDate,hour))
        )

    const createSlotList = slots => ul(...slots.map(renderSlot))

    const columns = div(
        { className: "columns", style: "display: flex; gap: 20px;" },
        createSlotList(leftSlots),
        createSlotList(rightSlots)
    )

    const headerText = `Available Hours for ${selectedDate}`

    contentHeader.replaceChildren(headerText)
    content.replaceChildren(backLink, columns)
}

function renderRentalAvailableFinalHours(contentHeader, content, selectedInitialHour, availableRange, cid, crid, date, start) {
    const backLink = div(a("Back", getAvailableHoursByDateUri(cid,crid, date)))
    const headerText = `Available Hours for ${date} starting at ${selectedInitialHour}`

    const individualSlots = splitIntoHourlySlots(availableRange.start, availableRange.end+1) // +1 to include the end hour
    individualSlots.shift() // Removes the first hour, which is the selected hour


    const mid = Math.ceil(individualSlots.length / 2)
    const [leftSlots, rightSlots] = [individualSlots.slice(0, mid), individualSlots.slice(mid)]
    const renderSlot = hour =>
        li(
            `${hour} `,
            a("Rent", createRentalUri(cid, crid, date, start, hour))
        )
    const createSlotList = slots => ul(...slots.map(renderSlot))
    const columns = div(
        { className: "columns", style: "display: flex; gap: 20px;" },
        createSlotList(leftSlots),
        createSlotList(rightSlots)
    )

    contentHeader.replaceChildren(headerText)
    content.replaceChildren( backLink, columns, p("Select the hour you want to rent"))
}

function renderCalendarToSearchAvailableHours(contentHeader, content, cid, crid) {
    const header = "Search Available Hours"
    const handleSubmit = async function(e){
        e.preventDefault()
        const validDate = document.querySelector("#date").value
        window.location.hash = getAvailableHoursByDateUri(cid,crid, validDate)
    }

    const fields = [
        { id: "date", label: "Select Date", type: "date", required: true },
    ]

    const children = div(
        a("Back", getCourtDetailsUri(cid, crid)),
        formRequest(fields, handleSubmit, {
            className: "form",
            submitText: "Get Available Hours"
        })
    )

    contentHeader.replaceChildren(header)
    content.replaceChildren(children)
}

const courtsViews = {
    renderCourtsByClubView,
    renderCourtDetailsView,
    renderCourtRentalsView,
    renderCreateCourtForm,
    renderCourtAvailableHoursView,
    renderCalendarToSearchAvailableHours,
    renderRentalAvailableFinalHours
}

export default courtsViews